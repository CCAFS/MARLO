#!/usr/bin/env bash
# CHG-OTP-LOGIN-001 -- T15 concurrency probe, HTTP level (design.md section 12, AC-7, FN-012, FN-013, FN-007/008).
#
# Drives the RUNNING application with genuinely parallel requests (threads released together from a barrier, each on
# its own connection) and checks the answers and the database state afterwards. Four cases of design section 12:
#   1  10 parallel correct verifies on one challenge          -> exactly 1 sign-in, 1 consumed row
#   2  10 parallel wrong codes on one challenge               -> attempts ends at 3, never above
#   3  3 wrong + 1 correct after exhaustion (and a race burst from attempts=0, many rounds)
#                                                             -> 0 consumes after exhaustion; never consumed AND attempts=3
#   4  10 parallel request-code calls for one email           -> at most 1 open row, 1 mail (first-ever bucket, no deadlock),
#                                                                with stale otp_challenges / otp_cooldowns / otp_rate_limits rows purged by it
#   RUN ORDER: case 4 runs FIRST. The limiter purge is throttled to once per 10 minutes per application instance, and the first
#   limiter call after a start is always due; any earlier request (cases 1-3 included) spends it. Start the application, then run
#   this probe with nothing else in between. If the purge was not due the last check of case 4 reports it as a FAIL naming the throttle.
#
# SAFETY: it deletes from otp_challenges / otp_rate_limits / otp_cooldowns to reset state between rounds, so it
# refuses to run unless the connected database name contains "probe" (a throwaway copy).
#
# Environment:
#   PROBE_BASE    application base URL                      (default http://localhost:8080/marlo-web)
#   PROBE_MYSQL   command that opens a mysql client on the throwaway database, reading SQL on stdin
#                 (default: mysql -N aiccradb_otp_probe)
#   PROBE_MBOX    mbox file written by the SMTP sink          (required: codes are read from the delivered mail)
#   PROBE_GU      Global Unit id with the allow-list set     (default 45)
#   PROBE_USERS   comma list of eligible emails to use, one per case (default otp.p01@outlook.com,...)
#   PROBE_ROUNDS  rounds of the race burst of case 3b        (default 25)
set -euo pipefail
exec python3 -I - "$@" <<'PY'
import collections, email, http.client, json, os, re, subprocess, sys, threading, time, urllib.parse

BASE = os.environ.get("PROBE_BASE", "http://localhost:8080/marlo-web")
MYSQL = os.environ.get("PROBE_MYSQL", "mysql -N aiccradb_otp_probe")
MBOX = os.environ["PROBE_MBOX"]
GU = os.environ.get("PROBE_GU", "45")
USERS = os.environ.get("PROBE_USERS", ",".join("otp.p%02d@outlook.com" % i for i in range(1, 6))).split(",")
ROUNDS = int(os.environ.get("PROBE_ROUNDS", "25"))
u = urllib.parse.urlparse(BASE)
HOST, PORT, PREFIX = u.hostname, u.port or 80, u.path.rstrip("/")


def sql(q):
    out = subprocess.run(MYSQL, shell=True, input=q, capture_output=True, text=True)
    if out.returncode != 0:
        raise SystemExit("mysql failed: " + out.stderr.strip()[:300])
    return [line.split("\t") for line in out.stdout.strip().splitlines() if line]


db = sql("SELECT DATABASE();")[0][0]
if "probe" not in db:
    raise SystemExit("refusing to run: database %r is not a throwaway probe copy" % db)
print("database under test: %s   base: %s" % (db, BASE))


def reset():
    sql("DELETE FROM otp_challenges; DELETE FROM otp_rate_limits; DELETE FROM otp_cooldowns;")


def new_session():
    c = http.client.HTTPConnection(HOST, PORT, timeout=60)
    c.request("GET", PREFIX + "/login.do")
    r = c.getresponse()
    r.read()
    c.close()
    m = re.search(r"JSESSIONID=([^;]+)", r.getheader("Set-Cookie") or "")
    return m.group(1) if m else None


def post_conn(path, data, cookie):
    """Returns a callable that sends the POST on a connection opened now (so the barrier release is the only wait)."""
    c = http.client.HTTPConnection(HOST, PORT, timeout=60)
    c.connect()
    body = urllib.parse.urlencode(data)
    headers = {"Content-Type": "application/x-www-form-urlencoded"}
    if cookie:
        headers["Cookie"] = "JSESSIONID=" + cookie

    def go():
        t0 = time.time()
        c.request("POST", PREFIX + path, body, headers)
        r = c.getresponse()
        raw = r.read().decode("utf8", "replace")
        t1 = time.time()
        c.close()
        try:
            st = json.loads(raw).get("status")
        except ValueError:
            st = "HTTP%d" % r.status
        return st, t0, t1
    return go


def burst(calls):
    """Runs all calls released together. Returns (statuses, overlap_ok): overlap_ok = every call started before the
    first call finished, which is what makes the burst a concurrency test and not a sequence."""
    barrier = threading.Barrier(len(calls))
    results = [None] * len(calls)

    def run(i):
        barrier.wait()
        results[i] = calls[i]()
    ts = [threading.Thread(target=run, args=(i,)) for i in range(len(calls))]
    [t.start() for t in ts]
    [t.join() for t in ts]
    overlap = max(r[1] for r in results) < min(r[2] for r in results)
    spread_ms = (max(r[1] for r in results) - min(r[1] for r in results)) * 1000
    return [r[0] for r in results], overlap, spread_ms


def mbox_messages():
    raw = open(MBOX, "rb").read() if os.path.exists(MBOX) else b""
    return re.split(rb"(?m)^From sink ", raw)[1:]


def is_for(chunk, rcpt):
    """Whether this sink message is the code email for rcpt. In production the envelope names the user; outside
    production (T19, FN-018) every code email goes to the support mailbox and the plain body starts "To: <address>"."""
    if ("-> <%s>" % rcpt).encode() in chunk.split(b"\n", 1)[0]:
        return True
    try:
        body = email.message_from_bytes(chunk.split(b"\n", 1)[1])
        for part in body.walk():
            if part.get_content_type() == "text/plain":
                m = re.match(r"To: (\S+)\r?\n", part.get_payload(decode=True).decode("utf8", "replace"))
                return bool(m) and m.group(1) == rcpt
    except Exception:
        return False
    return False


def code_for(rcpt, after):
    """Waits for the delivered mail beyond the first `after` messages addressed to rcpt and returns its code."""
    for _ in range(80):
        mine = [c for c in mbox_messages() if is_for(c, rcpt)]
        if len(mine) > after:
            body = email.message_from_bytes(mine[-1].split(b"\n", 1)[1])
            for p in body.walk():
                if p.get_content_type() == "text/plain":
                    m = re.search(r"Your code is: (\d{6})", p.get_payload(decode=True).decode("utf8", "replace"))
                    return m.group(1)
        time.sleep(0.25)
    raise SystemExit("no mail for " + rcpt)


def mails_for(rcpt):
    return len([c for c in mbox_messages() if is_for(c, rcpt)])


def issue(rcpt):
    """Fresh session + request-code. Returns (cookie, code)."""
    before = mails_for(rcpt)
    # The Shiro session that holds the pending challenge is the one this very response sets.
    c = http.client.HTTPConnection(HOST, PORT, timeout=60)
    c.request("POST", PREFIX + "/otpRequestCode.do", urllib.parse.urlencode({"email": rcpt, "globalUnitId": GU, "agree": "true"}),
              {"Content-Type": "application/x-www-form-urlencoded"})
    r = c.getresponse()
    st = json.loads(r.read().decode("utf8")).get("status")
    m = re.search(r"JSESSIONID=([^;]+)", r.getheader("Set-Cookie") or "")
    c.close()
    if st != "sent" or not m:
        raise SystemExit("request-code for %s answered %s (cookie %s)" % (rcpt, st, bool(m)))
    return m.group(1), code_for(rcpt, before)


def wrong_of(code):
    return "000000" if code != "000000" else "000001"


def rows():
    return sql("SELECT attempts, consumed_at IS NOT NULL FROM otp_challenges ORDER BY id;")


verdicts = []


def verdict(name, ok, detail):
    verdicts.append((name, ok))
    print("  => %s: %s   (%s)" % ("PASS" if ok else "FAIL", name, detail))


def require_on_probe_db(rows_for_this_burst):
    """The guard at the top checks the database THIS script opens. The application may be on another one: its first answered
    request must leave a challenge row in the probe copy, else abort."""
    if int(rows_for_this_burst) < 1:
        raise SystemExit("ABORT: the application answered but no challenge row appeared in %s: it is not running against the probe copy" % db)
    print("  (guard: challenge rows of the application's own requests are in %s)" % db)


def ver(cookie, code):
    return post_conn("/otpVerifyCode.do", {"code": code}, cookie)


# ---------------------------------------------------------------- case 4 (runs FIRST, see the header)
print("\n[case 4] 10 parallel request-code calls, ONE email, fresh buckets, stale rows of all three tables pre-seeded (FN-007/008, T04 pointer)")
reset()
rcpt = USERS[3]
sql("INSERT INTO otp_challenges (nonce, email_hmac, global_unit_id, code_hmac, expires_at, attempts, consumed_at, created_at) "
    "VALUES ('purgeprobe%d', REPEAT('a',64), %s, REPEAT('b',64), UTC_TIMESTAMP(3) - INTERVAL 2 HOUR, 0, NULL, UTC_TIMESTAMP(3) - INTERVAL 3 HOUR);" % (1, GU))
# Stale rows for the limiter's own purge (OtpRateLimiter: WINDOW_RETENTION 1 day, COOLDOWN_RETENTION 1 hour, PURGE_INTERVAL 10 min,
# throttled per application instance, the first call after a start is always due). Two controls inside the retention must survive.
sql("INSERT INTO otp_cooldowns (bucket, last_request_at) VALUES (REPEAT('s',64), UTC_TIMESTAMP(3) - INTERVAL 2 HOUR), "
    "(REPEAT('t',64), UTC_TIMESTAMP(3) - INTERVAL 10 MINUTE);"
    "INSERT INTO otp_rate_limits (bucket, window_start, hits) VALUES (REPEAT('s',64), UTC_TIMESTAMP() - INTERVAL 2 DAY, 3), "
    "(REPEAT('t',64), UTC_TIMESTAMP() - INTERVAL 1 HOUR, 3);")
before = mails_for(rcpt)
calls = []
for _ in range(10):
    calls.append(post_conn("/otpRequestCode.do", {"email": rcpt, "globalUnitId": GU, "agree": "true"}, None))
st, ov, spread = burst(calls)
cnt = collections.Counter(st)
time.sleep(1.5)
open_rows = sql("SELECT COUNT(*) FROM otp_challenges WHERE consumed_at IS NULL AND nonce NOT LIKE 'purgeprobe%';")[0][0]
all_rows = sql("SELECT COUNT(*) FROM otp_challenges WHERE nonce NOT LIKE 'purgeprobe%';")[0][0]
require_on_probe_db(all_rows)
stale = sql("SELECT COUNT(*) FROM otp_challenges WHERE nonce LIKE 'purgeprobe%';")[0][0]
cd_stale = sql("SELECT COUNT(*) FROM otp_cooldowns WHERE bucket = REPEAT('s',64);")[0][0]
cd_ctl = sql("SELECT COUNT(*) FROM otp_cooldowns WHERE bucket = REPEAT('t',64);")[0][0]
rl_stale = sql("SELECT COUNT(*) FROM otp_rate_limits WHERE bucket = REPEAT('s',64);")[0][0]
rl_ctl = sql("SELECT COUNT(*) FROM otp_rate_limits WHERE bucket = REPEAT('t',64);")[0][0]
mails = mails_for(rcpt) - before
print("  statuses: %s   overlap: %s   start spread: %.1f ms" % (dict(cnt), ov, spread))
print("  open rows: %s   rows total: %s   mails delivered: %d   pre-seeded 3h-old expired row still present: %s" % (open_rows, all_rows, mails, stale))
print("  stale otp_cooldowns row (2 h) left: %s   control cooldown (10 min) left: %s   stale otp_rate_limits window (2 days) left: %s   control window (1 h) left: %s"
      % (cd_stale, cd_ctl, rl_stale, rl_ctl))
verdict("case 4: at most 1 open row", int(open_rows) <= 1, "open=%s" % open_rows)
verdict("case 4: exactly 1 sent, none unavailable (no deadlock/lock timeout)", cnt.get("sent", 0) == 1 and cnt.get("unavailable", 0) == 0, dict(cnt))
verdict("case 4: exactly 1 mail", mails == 1, "mails=%d" % mails)
verdict("case 4: purge of the expired challenge ran alongside, no failure", stale == "0", "stale=%s" % stale)
verdict("case 4: the stale cooldown and the stale rate-limit window were purged during the accept burst, the in-retention controls survived",
        cd_stale == "0" and rl_stale == "0" and cd_ctl == "1" and rl_ctl == "1",
        "cooldown stale=%s control=%s, window stale=%s control=%s (a nonzero stale count with the app not freshly started means the 10-minute throttle was not due)"
        % (cd_stale, cd_ctl, rl_stale, rl_ctl))

# ---------------------------------------------------------------- case 1
print("\n[case 1] 10 parallel CORRECT verifies, one challenge (AC-7, FN-013)")
reset()
cookie, code = issue(USERS[0])
require_on_probe_db(sql("SELECT COUNT(*) FROM otp_challenges;")[0][0])
st, ov, spread = burst([ver(cookie, code) for _ in range(10)])
cnt = collections.Counter(st)
r = rows()
print("  statuses: %s   all-started-before-first-finished: %s   start spread: %.1f ms" % (dict(cnt), ov, spread))
print("  otp_challenges (attempts, consumed): %s" % r)
verdict("case 1: exactly 1 sign-in", cnt.get("ok", 0) == 1 and sum(1 for x in r if x[1] == "1") == 1, "ok=%d consumed rows=%d" % (cnt.get("ok", 0), sum(1 for x in r if x[1] == "1")))
verdict("case 1: burst was concurrent", ov, "overlap=%s" % ov)

# ---------------------------------------------------------------- case 2
print("\n[case 2] 10 parallel WRONG codes, one challenge (FN-012)")
reset()
cookie, code = issue(USERS[1])
st, ov, spread = burst([ver(cookie, wrong_of(code)) for _ in range(10)])
cnt = collections.Counter(st)
r = rows()
print("  statuses: %s   overlap: %s   start spread: %.1f ms" % (dict(cnt), ov, spread))
print("  otp_challenges (attempts, consumed): %s" % r)
verdict("case 2: attempts ends at 3 (never above)", r[0][0] == "3" and r[0][1] == "0", "attempts=%s consumed=%s" % (r[0][0], r[0][1]))
verdict("case 2: no more than 2 mismatch answers, at least 1 exhausted", cnt.get("mismatch", 0) <= 2 and cnt.get("exhausted", 0) >= 1, dict(cnt))
sql("DELETE FROM otp_rate_limits;")  # the 10 wrong codes spent the per-email verify budget; clear it so the answer is the challenge's
st2, _, _ = ver(cookie, code)()
verdict("case 2: the correct code is refused afterwards (AC-6)", st2 in ("exhausted", "invalid"), "answer=%s" % st2)

# ---------------------------------------------------------------- case 3
print("\n[case 3] 3 wrong + 1 correct AFTER exhaustion (0 consumes expected)")
bad = 0
for rnd in range(5):
    reset()
    cookie, code = issue(USERS[2])
    for _ in range(3):
        ver(cookie, wrong_of(code))()
    reset_rl = sql("DELETE FROM otp_rate_limits;")  # keep the verify budget clear; the challenge row is untouched
    calls = [ver(cookie, wrong_of(code)) for _ in range(3)] + [ver(cookie, code)]
    st, ov, spread = burst(calls)
    r = rows()
    consumed = sum(1 for x in r if x[1] == "1")
    bad += consumed + st.count("ok")
    print("  round %d: statuses %s overlap=%s -> rows %s" % (rnd + 1, dict(collections.Counter(st)), ov, r))
verdict("case 3: 0 consumes and 0 sign-ins after exhaustion (5 rounds)", bad == 0, "consumes+ok = %d" % bad)

print("\n[case 3b] race burst from attempts=0: 3 wrong + 1 correct fired together, %d rounds" % ROUNDS)
viol = 0
outcomes = collections.Counter()
for rnd in range(ROUNDS):
    reset()
    cookie, code = issue(USERS[2])
    calls = [ver(cookie, wrong_of(code)) for _ in range(3)] + [ver(cookie, code)]
    st, ov, spread = burst(calls)
    a, c = rows()[0]
    ok = st.count("ok")
    outcomes[(("ok" if ok else "no-ok"), "attempts=" + a, "consumed=" + c)] += 1
    if c == "1" and a == "3":
        viol += 1  # a consume that landed after exhaustion
    if ok > 1:
        viol += 1
print("  outcomes (signed-in?, attempts, consumed) -> count: %s" % dict(outcomes))
verdict("case 3b: never consumed AND attempts=3; never 2 sign-ins", viol == 0, "violations=%d of %d rounds" % (viol, ROUNDS))

print("\n[case 4b] first-ever concurrency, 8 rounds x 10 parallel, fresh email each round (deadlock hunt)")
unavail = 0
for rnd in range(8):
    reset()
    rcpt = "otp.race%d@outlook.com" % rnd  # decoys: unknown accounts take the same path (DD-5)
    calls = [post_conn("/otpRequestCode.do", {"email": rcpt, "globalUnitId": GU, "agree": "true"}, None) for _ in range(10)]
    st, ov, spread = burst(calls)
    cnt = collections.Counter(st)
    open_rows = sql("SELECT COUNT(*) FROM otp_challenges WHERE consumed_at IS NULL;")[0][0]
    unavail += cnt.get("unavailable", 0)
    print("  round %d: %s open=%s overlap=%s" % (rnd + 1, dict(cnt), open_rows, ov))
    if int(open_rows) > 1 or cnt.get("sent", 0) != 1:
        unavail += 100
verdict("case 4b: every round 1 sent, <=1 open row, 0 unavailable", unavail == 0, "bad=%d" % unavail)

failed = [n for n, ok in verdicts if not ok]
print("\nSUMMARY: %d checks, %d failed%s" % (len(verdicts), len(failed), (": " + "; ".join(failed)) if failed else ""))
sys.exit(1 if failed else 0)
PY
