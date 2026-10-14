#!/usr/bin/env bash
# CHG-OTP-LOGIN-001 -- T15 acceptance probe (requirements section 7), driven over HTTP against a RUNNING local stack.
#
# Usage:  ac-probe.sh <stage> [<stage> ...]
#   enum       AC-4, AC-3, AC-13, FN-006, FN-003, FN-016: the account states, answered side by side
#   attempts   AC-6, AC-14 (verify limits), parameter allow-list (RJ-9)
#   resend     AC-10, AC-8 (superseded), AC-8 (expired, DB-aged) -- includes a real 31 s wait
#   expiry     AC-8: a real 5 min 1 s old challenge (waits 302 s)
#   removed    AC-9: member soft-deleted (crp_users.is_active=0, as deleteCrpUser does) after the request; hard delete as support
#   limits     AC-14: 6th request per email, 21st per client IP
#   session    AC-5 (needs PROBE_JDWP): password login vs code login, session attributes and role rows
#   leak       AC-11 (needs PROBE_JDWP, PROBE_LOGS): full flow, then database (otp_challenges, email_logs) and log search
#   emaillog   AC-18 / OPS-005: the code email's email_logs row (delivered, failed, none for a request that sends nothing),
#              and a resend by id and in bulk that sends nothing (the failed half needs PROBE_SINK_STOP and PROBE_SINK_START;
#              the resend half needs PROBE_SA_EMAIL and PROBE_SA_PASS, a super admin test login of the copy)
#   live       FN-002 / AC-1: the allow-list emptied and restored without a restart, the page and the endpoint follow
#   nosecret   AC-12: run against an application started with no (or a short) OTP_HMAC_SECRET; records the startup WARN and the JVM env
#              (set PROBE_EXPECT_SECRET_ENV=present|absent to assert which case is running)
#   smtp       AC-15 / OPS-002: notifications off, then delivery failing (needs PROBE_SINK_STOP / PROBE_SINK_START)
#   postlogin  records what a refusal AFTER Subject.login leaves behind (needs the unmapped-type fixture, see README)
#   blackhole  NF-001 / OPS-002: a mail server that accepts and never answers (needs PROBE_SINK_STOP)
#   tz         one request-verify and expiry boundary, for an application started on another JVM time zone
#
# SAFETY: writes to the database (state resets, test users) -- refuses unless the database name contains "probe".
# Environment: PROBE_BASE, PROBE_MYSQL, PROBE_MBOX, PROBE_GU (as the concurrency probe), PROBE_JDWP (JDWP port of the
# application JVM, or "auto"), PROBE_LOGS (colon-separated log files, the CURRENT run's console log first), PROBE_JAVA_CP
# (directory with SessDump.class, compiled from tools/SessDump.java), PROBE_PW_EMAIL / PROBE_PW_PASS (test password account),
# PROBE_SINK_STOP / PROBE_SINK_START (commands that stop and restart the local SMTP sink), PROBE_SA_EMAIL / PROBE_SA_PASS
# (a super admin test account of the copy, never a real credential) and PROBE_SA_CRP (its Global Unit acronym, default AICCRA).
set -euo pipefail
exec python3 -u -I - "$@" <<'PY'
import collections, email, http.client, json, os, re, subprocess, sys, threading, time, urllib.parse

BASE = os.environ.get("PROBE_BASE", "http://localhost:8080/marlo-web")
MYSQL = os.environ.get("PROBE_MYSQL", "mysql -N aiccradb_otp_probe")
MBOX = os.environ["PROBE_MBOX"]
GU = os.environ.get("PROBE_GU", "45")
JDWP = os.environ.get("PROBE_JDWP")
LOGS = [p for p in os.environ.get("PROBE_LOGS", "").split(":") if p]
JCP = os.environ.get("PROBE_JAVA_CP", "")
# A password account of the throwaway copy, with a test password set there (never a real credential): the session-parity
# stage logs it in with the password and again with a code, on the same Global Unit.
PASSWORD_USER = (os.environ.get("PROBE_PW_EMAIL", "probe.user@outlook.com"), os.environ.get("PROBE_PW_PASS", ""))
u = urllib.parse.urlparse(BASE)
HOST, PORT, PREFIX = u.hostname, u.port or 80, u.path.rstrip("/")


def sql(q):
    out = subprocess.run(MYSQL, shell=True, input=q, capture_output=True, text=True)
    if out.returncode != 0:
        raise SystemExit("mysql failed: " + out.stderr.strip()[:300])
    return [l.split("\t") for l in out.stdout.strip().splitlines() if l]


db = sql("SELECT DATABASE();")[0][0]
if "probe" not in db:
    raise SystemExit("refusing to run: database %r is not a throwaway probe copy" % db)

verdicts = []
not_run = []


def verdict(ac, name, ok, detail=""):
    verdicts.append((ac, name, ok))
    print("  => %s [%s] %s%s" % ("PASS" if ok else "FAIL", ac, name, ("   (%s)" % (detail,)) if detail != "" else ""))


def notrun(ac, name, line):
    """A check the environment could not run. It is neither a pass nor a failure: the SUMMARY line counts it, so a half that
    did not run is never read as green."""
    print("  " + line)
    not_run.append((ac, name))


def reset():
    sql("DELETE FROM otp_challenges; DELETE FROM otp_rate_limits; DELETE FROM otp_cooldowns;")


class R:
    def __init__(self, status, body, headers):
        self.status, self.body, self.headers = status, body, headers
        try:
            self.json = json.loads(body)
        except ValueError:
            self.json = {}
        # Shiro answers JSESSIONID=deleteMe for the stopped session, then the new one: keep the last live value.
        live = [m for m in re.findall(r"JSESSIONID=([^;]+)", "\n".join(v for k, v in headers if k.lower() == "set-cookie")) if m != "deleteMe"]
        self.cookie = live[-1] if live else None

    @property
    def st(self):
        return self.json.get("status", "HTTP%d" % self.status)


def http_(method, path, data=None, cookie=None, extra=None):
    c = http.client.HTTPConnection(HOST, PORT, timeout=60)
    h = dict(extra or {})
    body = None
    if data is not None:
        body = urllib.parse.urlencode(data)
        h["Content-Type"] = "application/x-www-form-urlencoded"
    if cookie:
        h["Cookie"] = "JSESSIONID=" + cookie
    c.request(method, PREFIX + path, body, h)
    r = c.getresponse()
    raw = r.read().decode("utf8", "replace")
    out = R(r.status, raw, r.getheaders())
    c.close()
    return out


APP_ON_PROBE_DB = []


def request_code(addr, cookie=None, **extra):
    d = {"email": addr, "globalUnitId": GU, "agree": "true"}
    d.update(extra)
    r = http_("POST", "/otpRequestCode.do", d, cookie)
    if r.st == "sent" and not APP_ON_PROBE_DB:
        # The guard above checks the database THIS script opens, not the one the application uses. The first answered
        # request must have left its challenge row in the probe copy; otherwise the application is on another database.
        if int(sql("SELECT COUNT(*) FROM otp_challenges;")[0][0]) < 1:
            raise SystemExit("ABORT: the application answered 'sent' but no challenge row appeared in %s: it is not "
                             "running against the probe copy" % db)
        APP_ON_PROBE_DB.append(True)
        print("  (guard: the first challenge row landed in %s, the application is on the probe copy)" % db)
    return r


def verify(code, cookie, **extra):
    d = {"code": code}
    d.update(extra)
    return http_("POST", "/otpVerifyCode.do", d, cookie)


def mbox_for(rcpt):
    raw = open(MBOX, "rb").read() if os.path.exists(MBOX) else b""
    return [c for c in re.split(rb"(?m)^From sink ", raw)[1:] if ("-> <%s>" % rcpt).encode() in c.split(b"\n", 1)[0]]


def text_of(chunk):
    msg = email.message_from_bytes(chunk.split(b"\n", 1)[1])
    parts = {p.get_content_type(): p.get_payload(decode=True).decode("utf8", "replace") for p in msg.walk() if p.get_content_type().startswith("text/")}
    return msg, parts


def code_from(rcpt, after):
    for _ in range(80):
        mine = mbox_for(rcpt)
        if len(mine) > after:
            _, parts = text_of(mine[-1])
            return re.search(r"Your code is: (\d{6})", parts["text/plain"]).group(1)
        time.sleep(0.25)
    return None


def issue(addr, **extra):
    before = len(mbox_for(addr))
    r = request_code(addr, **extra)
    if r.st != "sent":
        raise SystemExit("request for %s answered %s" % (addr, r.st))
    return r.cookie, code_from(addr, before)


def wrong_of(code):
    return "000000" if code != "000000" else "000001"


def jdwp_port():
    """The JDWP agent (address=127.0.0.1:0) listens on a NEW ephemeral port after every debugger detaches, so the
    port is read again from the JVM that owns the HTTP listener each time. PROBE_JDWP=auto selects this."""
    if JDWP != "auto":
        return JDWP
    pid = subprocess.run("lsof -iTCP:%d -sTCP:LISTEN -P -t | head -1" % PORT, shell=True, capture_output=True, text=True).stdout.strip()
    out = subprocess.run("lsof -a -p %s -iTCP -sTCP:LISTEN -P -Fn" % pid, shell=True, capture_output=True, text=True).stdout
    ports = [m for m in re.findall(r"n(?:localhost|127\.0\.0\.1):(\d+)", out)]
    return ports[0]


def sessdump(sid):
    out = subprocess.run(["java", "-cp", JCP, "SessDump", jdwp_port(), sid], capture_output=True, text=True)
    attrs = {}
    for line in out.stdout.splitlines():
        m = re.match(r"  (.*?) = (.*)$", line)
        if m:
            attrs[m.group(1).strip('"')] = m.group(2)
    return out.stdout, attrs


def authed(cookie):
    """True when the Shiro session behind the cookie carries an authenticated principal (read from the JVM, because
    crpDashboard.do answers HTTP 200 with a page for anonymous callers too)."""
    if not cookie or not JDWP:
        return None
    _, attrs = sessdump(cookie)
    return "current_user" in attrs and attrs.get("org.apache.shiro.subject.support.DefaultSubjectContext_AUTHENTICATED_SESSION_KEY") == "true"


def stage_enum():
    print("\n== enum: AC-4 / AC-3 / AC-13 / FN-006 / FN-003 / FN-016")
    reset()
    states = [("eligible", "probe.user@outlook.com"), ("unknown", "otp.unknown@outlook.com"), ("inactive", "otp.inactive@outlook.com"),
              ("non-member", "otp.nonmember@outlook.com"), ("cgiar-refused (is_cgiar_user=1, Cognito-active unit)", "otp.cgiar@outlook.com"),
              ("soft-deleted (crp_users.is_active=0, what deleteCrpUser writes)", "otp.softdeleted@outlook.com")]
    base_mails = {a: len(mbox_for(a)) for _, a in states}
    res = {}
    for name, addr in states:
        r = request_code(addr)
        res[name] = (addr, r)
    def shape(r):
        hdrs = sorted((k, v) for k, v in r.headers if k.lower() not in ("date", "set-cookie", "content-security-policy-report-only", "content-security-policy"))
        body = dict(r.json)
        body["destination"] = re.sub(r"^.", "X", body.get("destination", ""))
        cookie_names = sorted(re.match(r"[^=]+", v).group(0) for k, v in r.headers if k.lower() == "set-cookie")
        return (r.status, json.dumps(body, sort_keys=True), hdrs, cookie_names)
    shapes = {n: shape(r) for n, (a, r) in res.items()}
    ref = shapes["eligible"]
    for n, (a, r) in res.items():
        print("  %-52s HTTP %d  %s" % (n, r.status, r.body))
    verdict("AC-4", "request-code answer, headers and cookie names identical across the 6 account states", all(s == ref for s in shapes.values()), "reference=%s" % (ref[1],))
    time.sleep(2)
    mails = {n: len(mbox_for(a)) - base_mails[a] for n, (a, r) in res.items()}
    print("  (no recorded challenge can authenticate: T11 unit test OtpVerifyCodeActionTest, search aDecoyGuessed)")
    verdict("AC-4", "no email for any ineligible state; exactly 1 for the eligible one", mails["eligible"] == 1 and sum(v for k, v in mails.items() if k != "eligible") == 0, mails)
    rows = sql("SELECT LENGTH(nonce), LENGTH(email_hmac), LENGTH(code_hmac), attempts, consumed_at IS NULL, ROUND(TIMESTAMPDIFF(MICROSECOND, created_at, expires_at) / 1000000) FROM otp_challenges ORDER BY id;")
    cols = [r[0] for r in sql("SHOW COLUMNS FROM otp_challenges;")]
    print("  otp_challenges columns: %s" % cols)
    print("  rows (len nonce, len email_hmac, len code_hmac, attempts, open, ttl s): %s" % rows)
    verdict("AC-4", "one challenge row per request, all the same shape, no column marks a decoy", len(rows) == len(states) and len(set(map(tuple, rows))) == 1 and not [c for c in cols if "decoy" in c or "eligible" in c], "rows=%d distinct shapes=%d" % (len(rows), len(set(map(tuple, rows)))))
    # AC-3 content
    mine = mbox_for("probe.user@outlook.com")[-1]
    msg, parts = text_of(mine)
    plain, html = parts.get("text/plain", ""), parts.get("text/html", "")
    code = re.search(r"Your code is: (\d{6})", plain).group(1)
    checks = collections.OrderedDict([
        ("application name", "MARLO" in plain and "MARLO" in (msg["Subject"] or "")),
        ("6-digit code", bool(re.fullmatch(r"\d{6}", code))),
        ("5-minute expiry", "expires in 5 minutes" in plain),
        ("works once", "only once" in plain),
        ("do not share", "not share" in plain.lower()),
        ("ignore if not requested", "ignore" in plain.lower()),
        ("support contact", "MARLOSupport@cgiar.org" in plain),
        ("no link carrying the code", not [h for h in re.findall(r'href="([^"]*)"', html) if code in h] and "http" not in plain.lower()),
        ("addressed only to the typed address", re.findall(r"<([^>]+)>", mine.split(b"\n", 1)[0].decode()) [-1] == "probe.user@outlook.com"),
    ])
    print("  FN-017 elements: %s" % dict(checks))
    verdict("AC-3", "challenge row + one email with a 6-digit code and every FN-017 element", all(checks.values()) and len(rows) >= 1)
    # AC-4 continuation: verifications and resend, same sequence for each state
    print("  -- the same sequence on every state: wrong, wrong, resend inside 30 s, wrong, correct-or-random")
    seq = {}
    for n, (a, r) in res.items():
        ck = r.cookie
        real = code if n == "eligible" else "246801"
        s = [verify("135790", ck).st, verify("135790", ck).st, request_code(a, ck).st, verify("135790", ck).st, verify(real, ck).st]
        seq[n] = s
        print("  %-52s %s" % (n, s))
    verdict("AC-4", "later wrong codes, in-window resend, exhaustion and a further code answer identically for all 6 states", len({tuple(v) for v in seq.values()}) == 1 and seq["eligible"] == ["mismatch", "mismatch", "rateLimited", "exhausted", "exhausted"], seq["eligible"])
    print("  -- real 31 s wait, then resend on every state")
    time.sleep(31)
    seq2 = {}
    for n, (a, r) in res.items():
        ck = r.cookie
        rr = request_code(a, ck)
        seq2[n] = (rr.st, verify("135790", rr.cookie or ck).st)
    print("  %s" % seq2)
    verdict("AC-4", "resend after 30 s and a wrong code answer identically for all 6 states", len(set(seq2.values())) == 1 and seq2["eligible"] == ("sent", "mismatch"), seq2["eligible"])
    time.sleep(2)
    mails2 = {n: len(mbox_for(a)) - base_mails[a] for n, (a, r) in res.items()}
    verdict("AC-4", "still only the eligible account was mailed after the resend (2 mails)", mails2["eligible"] == 2 and sum(v for k, v in mails2.items() if k != "eligible") == 0, mails2)
    # AC-13 is the cgiar-refused row; FN-016
    ck = http_("GET", "/login.do").cookie
    fresh = verify("123456", ck).st
    verdict("FN-016", "verify with no challenge in the session answers like a wrong code", fresh == seq["eligible"][0], fresh)
    verdict("AC-13", "is_cgiar_user=1 on a Cognito-active unit: neutral response, no email (the cgiar-refused row above)", shapes["cgiar-refused (is_cgiar_user=1, Cognito-active unit)"] == ref and mails["cgiar-refused (is_cgiar_user=1, Cognito-active unit)"] == 0)
    # FN-006 / FN-003 end to end
    reset()
    outs = {}
    for a in ("someone@gmail.com", "x@outlook.com.evil.com", "x@suboutlook.com", "x@sub.outlook.com", "x@attackeroutlook.com", "x@cgiar.org"):
        outs[a] = request_code(a).st
    print("  not-allow-listed / lookalike domains: %s" % outs)
    verdict("FN-006/FN-003", "every lookalike and other domain answers domainNotEnabled, no mail", set(outs.values()) == {"domainNotEnabled"})
    before = len(mbox_for("probe.user@outlook.com"))
    st = request_code("  Probe.User@OUTLOOK.COM ").st
    verdict("FN-003", "'  Probe.User@OUTLOOK.COM ' is accepted as the normalised address and mailed", st == "sent" and code_from("probe.user@outlook.com", before) is not None, st)


def stage_attempts():
    print("\n== attempts: AC-6, verify limits (SEC-010), parameter allow-list (RJ-9)")
    reset()
    ck, code = issue("otp.p05@outlook.com")
    s = [verify(wrong_of(code), ck).st for _ in range(3)]
    after = verify(code, ck).st
    print("  3 wrong then the correct code: %s then %s" % (s, after))
    verdict("AC-6", "3rd wrong code says exhausted; the correct code is refused afterwards", s == ["mismatch", "mismatch", "exhausted"] and after == "exhausted")
    reset()
    ck, code = issue("otp.p05@outlook.com")
    answers = [verify(wrong_of(code), ck).st for _ in range(11)]
    print("  11 verifies for one email: %s" % answers)
    verdict("AC-14", "per-email verify limit: the 11th verify in 15 min is rateLimited", answers[-1] == "rateLimited" and "rateLimited" not in answers[:10])
    reset()
    ck, code = issue("otp.p05@outlook.com")
    c = collections.Counter()
    for i in range(52):
        c[verify(wrong_of(code), http_("GET", "/login.do").cookie).st] += 1
    print("  52 verifies from one IP with no challenge: %s" % dict(c))
    verdict("AC-14", "per-IP verify limit (50): the answers beyond 50 are rateLimited", c.get("rateLimited", 0) == 2 and c.get("mismatch", 0) == 50, dict(c))
    print("  -- RJ-9: extra request parameters are not bound")
    reset()
    ck, code = issue("otp.p05@outlook.com", url="http://evil.example/", crp="CIAT", **{"user.email": "x@y.z", "session": "x"})
    r = verify(code, ck, url="http://evil.example/", crp="CIAT", **{"user.email": "evil@x.y", "globalUnit": "1"})
    print("  verify with url/crp/user.email/globalUnit extra: %s" % r.body)
    ok = r.st == "ok" and "evil" not in r.body and r.json.get("redirect", "").endswith("/AICCRA/crpDashboard.do")
    verdict("RJ-9/T12", "email, globalUnitId, agree and code bind through the real package; extra url/crp/user parameters are ignored", ok, r.body)


def stage_resend():
    print("\n== resend: AC-10, AC-8 (superseded and expired), FN-007/008 (real 31 s wait)")
    reset()
    n06, n07 = len(mbox_for("otp.p06@outlook.com")), len(mbox_for("otp.p07@outlook.com"))
    ck1, code1 = issue("otp.p06@outlook.com")
    rows0 = sql("SELECT COUNT(*) FROM otp_challenges")[0][0]
    open0 = sql("SELECT COUNT(*) FROM otp_challenges WHERE consumed_at IS NULL")[0][0]
    quick = request_code("otp.p06@outlook.com", ck1).st
    rows1 = sql("SELECT COUNT(*) FROM otp_challenges")[0][0]
    open1 = sql("SELECT COUNT(*) FROM otp_challenges WHERE consumed_at IS NULL")[0][0]
    ckB, codeB = issue("otp.p07@outlook.com")
    time.sleep(31)
    # same session resend
    rr = request_code("otp.p07@outlook.com", ckB)
    code2B = code_from("otp.p07@outlook.com", n07 + 1)
    old = verify(codeB, rr.cookie or ckB).st
    new = verify(code2B, rr.cookie or ckB).st
    # two sessions, superseded
    r2 = request_code("otp.p06@outlook.com")
    code2 = code_from("otp.p06@outlook.com", n06 + 1)
    superseded = verify(code1, ck1).st
    fresh = verify(code2, r2.cookie).st
    print("  resend inside 30 s: %s   same-session resend after 31 s: %s   old code: %s   new code: %s" % (quick, rr.st, old, new))
    print("  other session after 31 s: old code from session 1: %s   new code from session 2: %s" % (superseded, fresh))
    verdict("AC-10", "a resend within 30 s returns rateLimited and changes nothing", quick == "rateLimited" and rows0 == rows1 and open1 == open0, "rows %s -> %s, open rows %s -> %s" % (rows0, rows1, open0, open1))
    verdict("AC-10", "resend after 30 s: the old code is refused and the new one works", rr.st == "sent" and old == "mismatch" and new == "ok", "old=%s new=%s" % (old, new))
    verdict("AC-8", "a superseded challenge (another session, store level) is refused even though its row exists", superseded == "invalid" and fresh == "ok", "old=%s new=%s" % (superseded, fresh))
    # expired, aged in the throwaway database
    reset()
    ck, code = issue("otp.p08@outlook.com")
    sql("UPDATE otp_challenges SET expires_at = UTC_TIMESTAMP(3) - INTERVAL 1 SECOND, created_at = UTC_TIMESTAMP(3) - INTERVAL 301 SECOND;")
    expired = verify(code, ck).st
    verdict("AC-8", "a challenge whose age is 5 min 1 s (row aged in the copy) is refused with the correct code", expired == "invalid", expired)
    reset()
    ck, code = issue("otp.p08@outlook.com")
    sql("UPDATE otp_challenges SET expires_at = UTC_TIMESTAMP(3) + INTERVAL 25 SECOND;")
    boundary = verify(code, ck).st
    verdict("AC-8", "the same challenge 25 s before expiry is accepted (no early false refusal)", boundary == "ok", boundary)


def stage_expiry():
    print("\n== expiry: a real 5 min 1 s old challenge (AC-8)")
    reset()
    ck, code = issue("otp.p09@outlook.com")
    t0 = time.time()
    time.sleep(301)
    st = verify(code, ck).st
    age = time.time() - t0
    print("  age at verify %.1f s -> %s" % (age, st))
    verdict("AC-8", "correct code submitted 5 min 1 s after issue is refused (real clock)", st == "invalid" and age > 300, "%s at %.1f s" % (st, age))


def stage_removed():
    print("\n== removed: AC-9 (the removal MARLO performs is a soft delete: CrpUserMySQLDAO.deleteCrpUser sets is_active = 0)")
    member = "(SELECT id FROM users WHERE email='%s')"
    ctl, = sql("SELECT id FROM crp_users WHERE user_id = %s AND global_unit_id = %s;" % (member % "otp.p10@outlook.com", GU))[0]

    def membership(active):
        sql("UPDATE crp_users SET is_active = %d WHERE id = %s;" % (active, ctl))

    # A. soft delete, challenge issued while the member was active, removed before the verify
    reset()
    ck, code = issue("otp.p10@outlook.com")
    membership(0)
    print("  crp_users %s set to is_active=0 (soft delete) after the code was issued" % ctl)
    st = verify(code, ck).st
    row = sql("SELECT attempts, consumed_at IS NOT NULL FROM otp_challenges;")
    auth = authed(ck)
    dash = http_("GET", "/AICCRA/crpDashboard.do", cookie=ck)
    print("  verify after soft delete: %s   challenge row (attempts, consumed): %s   dashboard with that session: HTTP %d" % (st, row, dash.status))
    verdict("AC-9", "soft delete after the request: the correct code answers invalid and the challenge is consumed", st == "invalid" and row[0][1] == "1", "answer=%s consumed=%s" % (st, row[0][1]))
    verdict("AC-9", "soft delete after the request: no authenticated session was issued", auth is False, "pre-auth session authenticated=%s" % auth)
    # B. the request side for the member that is still soft-deleted: neutral response, no mail, no spent cooldown shape
    reset()
    base = len(mbox_for("otp.p10@outlook.com"))
    r = request_code("otp.p10@outlook.com")
    time.sleep(2)
    mails = len(mbox_for("otp.p10@outlook.com")) - base
    ref = request_code("otp.p11@outlook.com")
    print("  code request for the soft-deleted member: %s   (an active member answers %s)   mails delivered: %d" % (r.body, ref.body, mails))
    same = r.status == ref.status and r.st == ref.st == "sent" and re.sub(r"^.", "X", r.json.get("destination", "")) == re.sub(r"^.", "X", ref.json.get("destination", ""))
    verdict("AC-9", "soft-deleted member: a code request answers the neutral response and no mail is sent", same and mails == 0, "answer=%s mails=%d" % (r.st, mails))
    membership(1)
    # C. supporting evidence: the row deleted outright (not a removal MARLO performs)
    reset()
    ck, code = issue("otp.p10@outlook.com")
    saved = sql("SELECT id, user_id, is_active, created_by, global_unit_id FROM crp_users WHERE id = %s;" % ctl)[0]
    sql("DELETE FROM crp_users WHERE id = %s;" % ctl)
    st = verify(code, ck).st
    row = sql("SELECT attempts, consumed_at IS NOT NULL FROM otp_challenges;")
    auth = authed(ck)
    sql("INSERT INTO crp_users (id, user_id, is_active, created_by, global_unit_id) VALUES (%s, %s, %s, %s, %s);" % tuple(saved))
    print("  (supporting) hard delete of the crp_users row: verify %s   consumed %s   authenticated %s" % (st, row[0][1], auth))
    verdict("AC-9", "(supporting) hard delete of the membership row: invalid, consumed, no authenticated session", st == "invalid" and row[0][1] == "1" and auth is False, "answer=%s consumed=%s auth=%s" % (st, row[0][1], auth))
    # control: an active member still signs in (the gate does not refuse everybody)
    reset()
    ck, code = issue("otp.p10@outlook.com")
    st = verify(code, ck).st
    verdict("AC-9", "(control) the same member, active again, signs in with a new code", st == "ok", st)


def stage_limits():
    print("\n== limits: AC-14")
    reset()
    answers = []
    for i in range(6):
        answers.append(request_code("otp.p11@outlook.com").st)
        sql("DELETE FROM otp_cooldowns;")  # isolate the window bucket from the 30 s cooldown (throwaway copy)
    hits = sql("SELECT hits FROM otp_rate_limits ORDER BY hits DESC LIMIT 3;")
    print("  6 requests for one email (cooldown cleared between): %s   buckets' hits: %s" % (answers, hits))
    verdict("AC-14", "the 6th request in 15 min for one email is rateLimited, the first 5 are sent", answers == ["sent"] * 5 + ["rateLimited"], answers)
    reset()
    c = []
    for i in range(22):
        c.append(request_code("otp.ip%02d@outlook.com" % i).st)
    print("  22 requests, 22 different emails, one IP: %s" % collections.Counter(c))
    verdict("AC-14", "the per-IP limit trips across different emails (21st request)", c[:20] == ["sent"] * 20 and c[20] == "rateLimited" and c[21] == "rateLimited", "first limited at request %d" % (c.index("rateLimited") + 1 if "rateLimited" in c else -1))
    cd = request_code("otp.p12@outlook.com").st
    cd2 = request_code("otp.p12@outlook.com").st
    print("  (control) per-IP bucket is spent: request for a new email -> %s" % cd)


def stage_session():
    print("\n== session: AC-5 (password login vs code login, same user and Global Unit)")
    reset()
    uid, = sql("SELECT id FROM users WHERE email='probe.user@outlook.com';")[0]
    def role_state():
        return sql("SELECT MD5(CONCAT_WS('|', (SELECT IFNULL(GROUP_CONCAT(CONCAT(id,':',role_id,':',user_id) ORDER BY id),'') FROM user_roles WHERE user_id=%s),"
                   "(SELECT IFNULL(GROUP_CONCAT(CONCAT(id,':',global_unit_id,':',is_active) ORDER BY id),'') FROM crp_users WHERE user_id=%s))), "
                   "(SELECT COUNT(*) FROM user_roles WHERE user_id=%s), (SELECT COUNT(*) FROM crp_users WHERE user_id=%s);" % (uid, uid, uid, uid))[0]
    roles_before = role_state()
    pw = http_("POST", "/login.do", {"user.email": PASSWORD_USER[0], "user.password": PASSWORD_USER[1], "crp": "AICCRA"}, http_("GET", "/login.do").cookie)
    pw_loc = dict(pw.headers).get("Location")
    pw_text, pw_attrs = sessdump(pw.cookie)
    before = len(mbox_for("probe.user@outlook.com"))
    pre = request_code("probe.user@outlook.com")
    code = code_from("probe.user@outlook.com", before)
    pre_text, pre_attrs = sessdump(pre.cookie)
    print("  pre-auth session attributes: %s" % sorted(pre_attrs))
    lastlogin0 = sql("SELECT last_login FROM users WHERE id=%s;" % uid)[0][0]
    time.sleep(1.1)
    ok = verify(code, pre.cookie)
    lastlogin1 = sql("SELECT last_login FROM users WHERE id=%s;" % uid)[0][0]
    code_text, code_attrs = sessdump(ok.cookie)
    pre_after_text, _ = sessdump(pre.cookie)
    print("  pre-auth session id %s ; authenticated session id %s ; verify answer %s" % (pre.cookie, ok.cookie, ok.body))
    verdict("AC-5", "session ID differs from the pre-auth one (SEC-005)", ok.cookie is not None and ok.cookie != pre.cookie)
    old = http_("GET", "/AICCRA/crpDashboard.do", cookie=pre.cookie)
    new = http_("GET", "/AICCRA/crpDashboard.do", cookie=ok.cookie)
    pwd = http_("GET", "/AICCRA/crpDashboard.do", cookie=pw.cookie)
    print("  dashboard: pre-auth cookie HTTP %d (Location %s) | code-login cookie HTTP %d | password-login cookie HTTP %d" % (old.status, dict(old.headers).get("Location"), new.status, pwd.status))
    verdict("AC-5", "the pre-auth session id is not authenticated; the new one is, like the password one", authed(pre.cookie) is False and authed(ok.cookie) is True and authed(pw.cookie) is True, "pre=%s code-login=%s password-login=%s" % (authed(pre.cookie), authed(ok.cookie), authed(pw.cookie)))
    ignore = {"nonce", "color"}  # per-session CSP nonce, and the random avatar colour LoginAction picks on every login
    keys_pw, keys_code = set(pw_attrs) - ignore, set(code_attrs) - ignore
    only_pw, only_code = sorted(keys_pw - keys_code), sorted(keys_code - keys_pw)
    diff = {k: (pw_attrs[k], code_attrs[k]) for k in keys_pw & keys_code if pw_attrs[k] != code_attrs[k]}
    print("  attributes: password %d, code %d (nonce excluded)   only in password: %s   only in code: %s   differing values: %s" % (len(keys_pw), len(keys_code), only_pw, only_code, diff))
    verdict("AC-5", "same session attribute names and values as a password login for the same user and unit (every custom_parameters key included)", not only_pw and not only_code and not diff, "%d attributes compared" % len(keys_pw & keys_code))
    print("  landing: password Location=%s ; code redirect=%s" % (pw_loc, ok.json.get("redirect")))
    verdict("AC-5", "same landing page", ok.json.get("redirect", "").endswith(pw_loc or "?") if pw_loc else False)
    roles_after = role_state()
    print("  role/membership checksum before %s after %s ; last_login %s -> %s" % (roles_before, roles_after, lastlogin0, lastlogin1))
    verdict("AC-5/SEC-007", "user_roles and crp_users rows unchanged by a code sign-in", roles_before == roles_after)
    verdict("FN-011", "last_login updated by the code sign-in", lastlogin1 != lastlogin0, "%s -> %s" % (lastlogin0, lastlogin1))
    print("  pre-auth session after the login (should be gone/stopped):")
    print("   " + pre_after_text.replace("\n", "\n   ")[:300])


def sqlq(value):
    return "'" + value.replace("\\", "\\\\").replace("'", "''") + "'"


def email_log_max():
    return int(sql("SELECT IFNULL(MAX(id),0) FROM email_logs;")[0][0])


# The columns of email_logs as Hibernate maps them (EmailLogs.hbm.xml); every query below lists them explicitly.
EMAIL_LOG_COLS = ("id", "to_email", "cc_email", "bbc_email", "subject", "message", "tried", "date_email", "error_email",
                  "succes_email", "file_name", "fileContent", "message_id", "global_unit_id", "source_action")


def email_rows(after):
    """The email_logs rows above an id, as dicts (mysql batch output: NULL prints as NULL, tabs and newlines are escaped)."""
    out = sql("SELECT %s FROM email_logs WHERE id > %d ORDER BY id;" % (", ".join("`%s`" % c for c in EMAIL_LOG_COLS), after))
    return [dict(zip(EMAIL_LOG_COLS, r + [""] * (len(EMAIL_LOG_COLS) - len(r)))) for r in out]


def email_rows_after(after, expect=1, wait=15.0):
    """The number of email_logs rows above an id, waiting up to `wait` s for `expect` of them: the code email is recorded on
    the sender's worker thread once the delivery attempt ends (OPS-005), not on the request thread."""
    deadline = time.time() + wait
    while True:
        n = int(sql("SELECT COUNT(*) FROM email_logs WHERE id > %d;" % after)[0][0])
        if n >= expect or time.time() >= deadline:
            return n
        time.sleep(0.25)


def email_log_hits(after, needle, skip=()):
    """{column: rows} for the email_logs rows above an id that hold the needle as an exact substring, in every column of the
    table (read from the database, so a column added later is searched too) except those in `skip`."""
    hits = {}
    for c in [r[0] for r in sql("SHOW COLUMNS FROM email_logs;")]:
        if c in skip:
            continue
        n = int(sql("SELECT COUNT(*) FROM email_logs WHERE id > %d AND LOCATE(%s, IFNULL(CAST(`%s` AS CHAR), '')) > 0;" % (after, sqlq(needle), c))[0][0])
        if n:
            hits[c] = n
    return hits


def email_log_six_digit_runs(after):
    """{column: rows} holding a run of exactly six digits, in every column but the numeric ones. For a row whose code the probe
    never saw (the mail was not delivered), this is the closest check that no code was written."""
    hits = {}
    for c in [r[0] for r in sql("SHOW COLUMNS FROM email_logs;")]:
        if c in ("id", "tried", "succes_email", "global_unit_id"):
            continue
        n = int(sql("SELECT COUNT(*) FROM email_logs WHERE id > %d AND IFNULL(CAST(`%s` AS CHAR), '') REGEXP '(^|[^0-9])[0-9]{6}([^0-9]|$)';" % (after, c))[0][0])
        if n:
            hits[c] = n
    return hits


def sink_total():
    """Every message the local sink has recorded, whoever it is addressed to (a resent mail can be redirected)."""
    raw = open(MBOX, "rb").read() if os.path.exists(MBOX) else b""
    return len(re.split(rb"(?m)^From sink ", raw)[1:])


def stage_leak():
    print("\n== leak: AC-11 (SEC-001, SEC-002, OPS-001)")
    reset()
    sizes = {p: os.path.getsize(p) for p in LOGS}
    mark = sql("SELECT UTC_TIMESTAMP(3);")[0][0]
    el_max = int(sql("SELECT IFNULL(MAX(id),0) FROM email_logs;")[0][0])
    addr = "probe.user@outlook.com"
    before = len(mbox_for(addr))
    pre = request_code(addr)
    code = code_from(addr, before)
    _, attrs = sessdump(pre.cookie)
    nonce = None
    for k, v in attrs.items():
        mm = re.search(r"nonce=\"([^\"]+)\"", v)
        if mm:
            nonce = mm.group(1)
    w1 = verify(wrong_of(code), pre.cookie).st
    ok = verify(code, pre.cookie)
    time.sleep(1.5)
    print("  flow: request -> %s ; wrong -> %s ; correct -> %s" % ("sent", w1, ok.st))
    print("  needles: code=%s nonce=%s email=%s pre-auth sid=%s auth sid=%s" % (code, nonce, addr, pre.cookie, ok.cookie))
    verdict("AC-11", "the nonce was captured from the session for the log search", bool(nonce))
    rows = sql("SELECT * FROM otp_challenges;")
    flat = "\n".join("\t".join(r) for r in rows)
    needles = {"code": code, "full email": addr, "local part": addr.split("@")[0], "nonce": nonce or "NO-NONCE"}
    print("  (the nonce is the lookup key and is stored by design; only the code and the email are forbidden columns)")
    for n, v in needles.items():
        if n == "nonce":
            continue
        hit = v in flat
        verdict("AC-11", "otp_challenges holds no %s" % n, not hit, "%d rows scanned" % len(rows))
    tables = [r[0] for r in sql("SHOW TABLES LIKE 'email_log%';")]
    # OPS-005: the code email leaves ONE email_logs row, written by the sender's worker thread once the delivery attempt ends.
    # AC-11 (amended 2026-10-14): the code is in no column of it; the email and its local part are only in to_email, the
    # recipient column, which every MARLO email row holds.
    email_rows_after(el_max, 1)
    time.sleep(1.5)  # a second, unexpected row would land inside this grace
    new = email_rows(el_max)
    print("  email_logs: %d row(s) written since the flow began (id > %d); source_action %s; succes_email %s; global_unit_id %s" % (
        len(new), el_max, [r["source_action"] for r in new], [r["succes_email"] for r in new], [r["global_unit_id"] for r in new]))
    code_hits = email_log_hits(el_max, code)
    verdict("AC-11", "email_logs (the real table name): the code is in no column of any row written for this flow", code_hits == {} and tables == ["email_logs"], "tables=%s code matches per column=%s" % (tables, code_hits))
    verdict("AC-11", "email_logs: exactly one new row for the flow, with source_action /otpRequestCode (OPS-005)", len(new) == 1 and new[0]["source_action"] == "/otpRequestCode", "rows=%d" % len(new))
    other_hits = {n: email_log_hits(el_max, v, skip=("to_email",)) for n, v in needles.items() if n in ("full email", "local part")}
    verdict("AC-11", "email_logs: the full email and the local part are in no column but to_email, which holds the recipient",
            all(h == {} for h in other_hits.values()) and [r["to_email"] for r in new] == [addr], "matches outside to_email=%s to_email=%s" % (other_hits, [r["to_email"] for r in new]))
    ids = [pre.cookie, ok.cookie]
    all_lines = collections.OrderedDict()
    for p in LOGS:
        with open(p, "rb") as f:
            f.seek(sizes[p])
            all_lines[p] = f.read().decode("utf8", "replace").splitlines()
    for p, lines in all_lines.items():
        print("  log %s: %d new lines since the flow began" % (p, len(lines)))
        for label, needle in (("code", code), ("nonce", nonce or "NO-NONCE"), ("full email", addr), ("pre-auth session id", pre.cookie), ("authenticated session id", ok.cookie)):
            hits = [l for l in lines if needle in l]
            verdict("AC-11", "log %s has no line with the %s" % (os.path.basename(p), label), not hits, ("%d hits: %s" % (len(hits), [h[:220] for h in hits[:2]])) if hits else "%d lines searched" % len(lines))
    first = list(all_lines.values())[0] if all_lines else []
    otp = [l for l in first if "auth.otp." in l]
    for l in otp:
        print("   " + re.sub(r"^.*?\[(INFO|WARN)", r"[\1", l)[:200])
    kinds = collections.Counter(re.search(r"auth\.otp\.(\w+)", l).group(1) for l in otp)
    verdict("AC-11/OPS-001", "exactly one auth.otp.request per request call, one auth.otp.verify per verify call, one auth.otp.delivery per mail", kinds == {"request": 1, "verify": 2, "delivery": 1}, dict(kinds))
    ok_lines = [l for l in otp if "outcome=ok" in l]
    print("  structured fields present: %s" % bool(otp and all(re.search(r"outcome=\S+ gu=\S* domain=\S* ms=\d+", l) for l in otp)))


def stage_emaillog():
    print("\n== emaillog: AC-18 / OPS-005 (the code email recorded in email_logs for support, without the code; resend sends nothing)")
    addr = "probe.user@outlook.com"
    source = "/otpRequestCode"
    placeholder = "Sign-in code email (code withheld)"
    sink_stop, sink_start = os.environ.get("PROBE_SINK_STOP"), os.environ.get("PROBE_SINK_START")
    bare_class = re.compile(r"^[A-Za-z_][A-Za-z0-9_$]*$")
    # 1. a delivered code email, and a resend inside the cooldown, which sends nothing
    reset()
    el0 = email_log_max()
    ck, code = issue(addr)
    again = request_code(addr, ck)
    email_rows_after(el0, 1)
    time.sleep(2)
    rows = email_rows(el0)
    mail_subject = str(text_of(mbox_for(addr)[-1])[0]["Subject"])
    r = rows[0] if len(rows) == 1 else {}
    print("  delivered: %d row(s) above id %d: %s" % (len(rows), el0, {k: v for k, v in r.items() if k in ("id", "succes_email", "error_email", "source_action", "global_unit_id", "tried", "message", "subject", "date_email")}))
    verdict("AC-18", "delivered code email: exactly one new email_logs row", len(rows) == 1, "rows=%d" % len(rows))
    verdict("AC-18", "delivered row: succes_email 1, no error, source_action /otpRequestCode, the placeholder message, global_unit_id %s, tried 1, the recipient and the mail's own subject, a date" % GU,
            bool(r) and r["succes_email"] == "1" and r["error_email"] == "NULL" and r["source_action"] == source and r["message"] == placeholder
            and r["global_unit_id"] == GU and r["tried"] == "1" and r["to_email"] == addr and r["subject"] == mail_subject and r["date_email"] != "NULL",
            "subject=%r" % (r.get("subject"),))
    verdict("AC-18", "delivered row: cc, bcc, file name, file content and message id stay NULL",
            bool(r) and all(r[c] == "NULL" for c in ("cc_email", "bbc_email", "file_name", "fileContent", "message_id")))
    verdict("AC-18", "delivered row: the code is in no column, and no column holds a run of six digits",
            email_log_hits(el0, code) == {} and email_log_six_digit_runs(el0) == {}, "code matches=%s six-digit runs=%s" % (email_log_hits(el0, code), email_log_six_digit_runs(el0)))
    verdict("AC-18", "a resend inside the 30 s cooldown answers rateLimited and leaves no second row", again.st == "rateLimited" and len(rows) == 1, "answer=%s rows=%d" % (again.st, len(rows)))
    # 2. a failed code email: the sink is stopped, so the delivery fails; the probe restores it afterwards
    failed_id = None
    if sink_stop and sink_start:
        subprocess.run(sink_stop, shell=True)
        time.sleep(1)
        try:
            reset()
            el1 = email_log_max()
            d = request_code(addr)
            email_rows_after(el1, 1, 30)
            time.sleep(2)
        finally:
            subprocess.run(sink_start, shell=True)
            time.sleep(1)
        frows = email_rows(el1)
        f = frows[0] if len(frows) == 1 else {}
        print("  failed (sink stopped): response %s ; %d row(s) above id %d: %s" % (d.body, len(frows), el1, {k: v for k, v in f.items() if k in ("id", "succes_email", "error_email", "source_action", "global_unit_id", "tried", "message", "subject", "date_email")}))
        verdict("AC-18", "failed code email: the response is still the neutral one and exactly one new email_logs row exists", d.st == "sent" and len(frows) == 1, "answer=%s rows=%d" % (d.st, len(frows)))
        verdict("AC-18", "failed row: succes_email 0, error_email a bare class name (no address, no space, no SMTP text), the source, the placeholder message, the unit, the recipient",
                bool(f) and f["succes_email"] == "0" and bool(bare_class.match(f["error_email"])) and f["source_action"] == source and f["message"] == placeholder
                and f["global_unit_id"] == GU and f["tried"] == "1" and f["to_email"] == addr, "error_email=%r" % (f.get("error_email"),))
        verdict("AC-18", "failed row: no column holds a run of six digits (the code never reached the sink, so it cannot be searched for)",
                email_log_six_digit_runs(el1) == {}, email_log_six_digit_runs(el1))
        if len(frows) == 1:
            failed_id = int(f["id"])
    else:
        notrun("AC-18", "failed code email leaves one row, succes_email 0 and a bare class name",
               "failed: NOT RUN (needs PROBE_SINK_STOP and PROBE_SINK_START: the probe must be able to restore the sink it stops)")
    # 3. requests that send nothing leave no row
    reset()
    el2 = email_log_max()
    mails0 = sink_total()
    cases = [("unknown", "otp.unknown@outlook.com", "sent"), ("inactive", "otp.inactive@outlook.com", "sent"), ("non-member", "otp.nonmember@outlook.com", "sent"),
             ("cgiar-refused", "otp.cgiar@outlook.com", "sent"), ("soft-deleted", "otp.softdeleted@outlook.com", "sent"), ("domain not allow-listed", "someone@gmail.com", "domainNotEnabled")]
    answers = {name: request_code(a).st for name, a, want in cases}
    time.sleep(3)
    quiet = email_rows(el2)
    print("  requests that send nothing: %s ; email_logs rows written: %d ; mails at the sink: %d" % (answers, len(quiet), sink_total() - mails0))
    verdict("AC-18", "an ineligible request (5 account states, 1 domain off the allow-list) leaves no email_logs row and no mail",
            answers == {name: want for name, a, want in cases} and len(quiet) == 0 and sink_total() == mails0, "rows=%d" % len(quiet))
    # 4. System Admin -> Emails lists the failed row, and resending it, by id or in bulk, sends nothing
    sa_email, sa_pass = os.environ.get("PROBE_SA_EMAIL"), os.environ.get("PROBE_SA_PASS")
    if not sa_email or not sa_pass:
        notrun("AC-18", "System Admin -> Emails lists the failed row; resend by id and in bulk sends nothing",
               "resend: NOT RUN (no PROBE_SA_*); covered by SendFailEmailActionTest")
        return
    if failed_id is None:
        notrun("AC-18", "System Admin -> Emails lists the failed row; resend by id and in bulk sends nothing",
               "resend: NOT RUN (there is no failed code email row from step 2 to resend)")
        return
    login = http_("POST", "/login.do", {"user.email": sa_email, "user.password": sa_pass, "crp": os.environ.get("PROBE_SA_CRP", "AICCRA")}, http_("GET", "/login.do").cookie)
    sa = login.cookie
    listing = http_("GET", "/emailLogs.do?" + urllib.parse.urlencode({"status": "notSent", "source": source, "length": 100}), cookie=sa)
    if "data" not in listing.json:
        notrun("AC-18", "System Admin -> Emails lists the failed row; resend by id and in bulk sends nothing",
               "resend: NOT RUN (the PROBE_SA_* login does not reach System Admin -> Emails: login HTTP %d, emailLogs.do HTTP %d, a super admin account is needed)" % (login.status, listing.status))
        return

    def ids_of(items):
        return [str(i.get("id")) for i in (items or []) if isinstance(i, dict)]

    def otp_state():
        return sql("SET SESSION group_concat_max_len = 10000000; SELECT COUNT(*), MD5(GROUP_CONCAT(CONCAT_WS('|', id, IFNULL(succes_email,'N'), IFNULL(tried,'N'), IFNULL(error_email,'N'), IFNULL(date_email,'N'), "
                   "IFNULL(message,'N')) ORDER BY id)) FROM email_logs WHERE source_action = %s;" % sqlq(source))[0]

    verdict("AC-18", "System Admin -> Emails (emailLogs.do, not sent, source /otpRequestCode) lists the failed code email", str(failed_id) in ids_of(listing.json.get("data")), "listed ids=%s" % ids_of(listing.json.get("data")))
    state0, mails_before = otp_state(), sink_total()
    by_id_list = http_("POST", "/sendFailEmail.do", {"type": 0, "id": failed_id}, sa)
    by_id = http_("POST", "/sendFailEmail.do", {"type": 1, "id": failed_id}, sa)
    bulk_list = http_("POST", "/sendFailEmail.do", {"type": 0, "source": source}, sa)
    bulk = http_("POST", "/sendFailEmail.do", {"type": 1, "source": source}, sa)
    time.sleep(3)
    state1, mails_after = otp_state(), sink_total()
    print("  sendFailEmail.do on the failed row (id %d): list by id -> HTTP %d emails=%s ; resend by id -> HTTP %d results=%s ; list filtered on the source -> emails=%s ; resend filtered on the source -> results=%s" % (
        failed_id, by_id_list.status, ids_of(by_id_list.json.get("emails")), by_id.status, by_id.json.get("results"), ids_of(bulk_list.json.get("emails")), bulk.json.get("results")))
    verdict("AC-18", "the failed code email is not selected by id (type 0 lists nothing, type 1 resends nothing)",
            by_id_list.status == 200 and by_id_list.json.get("emails") == [] and by_id.status == 200 and by_id.json.get("results") == [], "list=%s resend=%s" % (by_id_list.json.get("emails"), by_id.json.get("results")))
    verdict("AC-18", "no code email is selected in bulk (the filter of the page, source /otpRequestCode, which does select them in the table)",
            bulk_list.status == 200 and bulk_list.json.get("emails") == [] and bulk.status == 200 and bulk.json.get("results") == [], "list=%s resend=%s" % (bulk_list.json.get("emails"), bulk.json.get("results")))
    verdict("AC-18", "after both resends the code email rows are unchanged (count, succes_email, tried, error, date, message) and no mail reached the sink", state0 == state1 and mails_after == mails_before, "rows/checksum %s -> %s, sink %d -> %d" % (state0, state1, mails_before, mails_after))
    # Control: the same action and session DO select a failed row that is not a code email, so the empty answers above mean something.
    ctl = int(sql("INSERT INTO email_logs (to_email, subject, message, tried, date_email, succes_email, source_action, global_unit_id) VALUES "
                  "('probe.control@outlook.com', 'probe control (not a code email)', '<p>control</p>', 1, NOW(), 0, '/probe/control', %s); SELECT LAST_INSERT_ID();" % GU)[0][0])
    try:
        # Lists first: a resend that succeeds marks the row sent, and a sent row is (rightly) no longer selected.
        c_id = http_("POST", "/sendFailEmail.do", {"type": 0, "id": ctl}, sa)
        c_list = http_("POST", "/sendFailEmail.do", {"type": 0, "source": "/probe/control"}, sa)
        c_bulk = http_("POST", "/sendFailEmail.do", {"type": 1, "source": "/probe/control"}, sa)
        got = (ids_of(c_id.json.get("emails")), ids_of(c_bulk.json.get("results")), ids_of(c_list.json.get("emails")))
        print("  control (a failed row of source /probe/control, id %d): list by id -> %s ; resend filtered -> %s ; list filtered -> %s" % ((ctl,) + got))
        verdict("AC-18", "control: a failed row that is not a code email IS selected by id, in bulk and in the list (so the empty answers above are not a broken request)",
                got == ([str(ctl)], [str(ctl)], [str(ctl)]), got)
    finally:
        sql("DELETE FROM email_logs WHERE source_action = '/probe/control';")


def stage_live():
    print("\n== live: FN-002 / FN-001 / AC-1 (allow-list emptied and restored with no restart)")
    reset()
    before = request_code("probe.user@outlook.com").st
    cp = sql("SELECT id, value FROM custom_parameters WHERE parameter_id=397 AND global_unit_id=%s;" % GU)
    sql("UPDATE custom_parameters SET value='' WHERE parameter_id=397 AND global_unit_id=%s;" % GU)
    reset()
    mails0 = len(mbox_for("probe.user@outlook.com"))
    after = request_code("probe.user@outlook.com")
    page = http_("POST", "/crpByEmail.do", {"userEmail": "probe.user@outlook.com"})
    print("  empty allow-list: request-code -> %s ; crpByEmail otpEnabled flags: %s" % (after.st, re.findall(r'"otpEnabled":(true|false)', page.body)))
    time.sleep(2)
    verdict("AC-1/FN-001", "empty allow-list: request-code refused (domainNotEnabled), no mail, no challenge", after.st == "domainNotEnabled" and len(mbox_for("probe.user@outlook.com")) == mails0 and sql("SELECT COUNT(*) FROM otp_challenges;")[0][0] == "0", after.st)
    verdict("AC-1", "empty allow-list: crpByEmail offers no OTP for any unit", set(re.findall(r'"otpEnabled":(true|false)', page.body)) == {"false"})
    pw = http_("POST", "/login.do", {"user.email": PASSWORD_USER[0], "user.password": PASSWORD_USER[1], "crp": "AICCRA"}, http_("GET", "/login.do").cookie)
    verdict("AC-1/FN-019", "password login unchanged with the feature off", pw.status == 302 and dict(pw.headers).get("Location", "").endswith("/AICCRA/crpDashboard.do"), dict(pw.headers).get("Location"))
    co = http_("GET", "/cognitoLogin.do?crp=AICCRA")
    print("  cognitoLogin.do?crp=AICCRA -> HTTP %d Location %s" % (co.status, (dict(co.headers).get("Location") or "")[:90]))
    sql("UPDATE custom_parameters SET value='outlook.com' WHERE parameter_id=397 AND global_unit_id=%s;" % GU)
    reset()
    back = request_code("probe.user@outlook.com").st
    page2 = http_("POST", "/crpByEmail.do", {"userEmail": "probe.user@outlook.com"})
    print("  restored: request-code -> %s ; otpEnabled flags: %s" % (back, re.findall(r'"otpEnabled":(true|false)', page2.body)))
    verdict("FN-002", "restoring the allow-list takes effect on the next request with no restart", back == "sent" and "true" in re.findall(r'"otpEnabled":(true|false)', page2.body))
    print("  (before the toggle: %s ; custom_parameters row: %s)" % (before, cp))


def stage_nosecret():
    print("\n== nosecret: AC-12 (application started with no OTP_HMAC_SECRET, or a 16-byte one)")
    reset()
    page = http_("GET", "/login.do")
    verdict("AC-12", "the application started and serves a real 2xx route (login.do)", page.status == 200, "HTTP %d" % page.status)
    mails0 = len(mbox_for("probe.user@outlook.com"))
    r = request_code("probe.user@outlook.com")
    time.sleep(1.5)
    flags = re.findall(r'"otpEnabled":(true|false)', http_("POST", "/crpByEmail.do", {"userEmail": "probe.user@outlook.com"}).body)
    rows = sql("SELECT COUNT(*) FROM otp_challenges;")[0][0]
    print("  allow-list is set for the unit; request-code -> %s ; otpEnabled flags %s ; challenge rows %s" % (r.st, flags, rows))
    verdict("AC-12", "request-code refused (domainNotEnabled), no challenge, no mail", r.st == "domainNotEnabled" and rows == "0" and len(mbox_for("probe.user@outlook.com")) == mails0)
    verdict("AC-12", "the option is hidden: otpEnabled false for every unit", set(flags) <= {"false"} and flags != [])
    v = verify("123456", http_("GET", "/login.do").cookie).st
    print("  verify-code -> %s" % v)
    verdict("AC-12", "verify-code does not authenticate (answers unavailable or mismatch, never ok)", v in ("unavailable", "mismatch"), v)
    pw = http_("POST", "/login.do", {"user.email": PASSWORD_USER[0], "user.password": PASSWORD_USER[1], "crp": "AICCRA"}, http_("GET", "/login.do").cookie)
    verdict("AC-12/FN-019", "password login still works without the secret", pw.status == 302 and dict(pw.headers).get("Location", "").endswith("/AICCRA/crpDashboard.do"))
    # The JVM's environment (value never printed) and the startup WARN of OtpKeys, as the log holds it.
    pid = subprocess.run("lsof -iTCP:%d -sTCP:LISTEN -P -t | head -1" % PORT, shell=True, capture_output=True, text=True).stdout.strip()
    envline = subprocess.run(["ps", "eww", "-p", pid], capture_output=True, text=True).stdout
    found = re.search(r"OTP_HMAC_SECRET=(\S*)", envline)
    warns = []
    for p in LOGS[:1]:
        warns = [re.sub(r"^.*?(\[WARN *\])", r"\1", l)[:220] for l in open(p, "rb").read().decode("utf8", "replace").splitlines() if "auth.otp.keys" in l]
    for w in warns[:3]:
        print("  log: %s" % w)
    leaked = bool(found and found.group(1) and any(found.group(1) in l for l in warns))
    verdict("AC-12", "the startup log carries the auth.otp.keys WARN (outcome=unconfigured) and not the secret value", bool(warns) and all("outcome=unconfigured" in w for w in warns) and not leaked, "%d line(s), secret value in them: %s" % (len(warns), leaked))
    print("  ps eww of the JVM owning port %d: OTP_HMAC_SECRET %s" % (PORT, ("present, value redacted (%d characters)" % len(found.group(1))) if found else "absent"))
    expect = os.environ.get("PROBE_EXPECT_SECRET_ENV")
    if expect:
        verdict("AC-12", "JVM environment matches the case under test (OTP_HMAC_SECRET %s)" % expect, bool(found) == (expect == "present"))


def stage_smtp():
    print("\n== smtp: AC-15 / FN-018 / OPS-002 / NF-001")
    reset()
    prev = sql("SELECT id, value FROM custom_parameters WHERE parameter_id=266 AND global_unit_id=%s;" % GU)
    print("  crp_enable_email_notification custom_parameters for the unit: %s" % prev)
    sql("UPDATE custom_parameters SET value='false' WHERE parameter_id=266 AND global_unit_id=%s;" % GU)
    pw = http_("POST", "/login.do", {"user.email": PASSWORD_USER[0], "user.password": PASSWORD_USER[1], "crp": "AICCRA"}, http_("GET", "/login.do").cookie)
    flag = None
    if JDWP:
        _, attrs = sessdump(pw.cookie)
        flag = attrs.get("crp_enable_email_notification")
    print("  a password login now carries crp_enable_email_notification = %s in its session" % flag)
    before = len(mbox_for("probe.user@outlook.com"))
    r = request_code("probe.user@outlook.com")
    code = code_from("probe.user@outlook.com", before)
    verdict("AC-15", "notifications off for the unit: the code email still arrives", r.st == "sent" and code is not None and flag in ('"false"', "false", None), "flag=%s code=%s" % (flag, "received" if code else None))
    saved = prev[0][1] if prev else None
    if saved is not None:
        sql("UPDATE custom_parameters SET value='%s' WHERE parameter_id=266 AND global_unit_id=%s;" % (saved.replace("'", "''"), GU))
    print("  notifications restored to the saved value %r" % saved)
    if os.environ.get("PROBE_SINK_STOP"):
        subprocess.run(os.environ["PROBE_SINK_STOP"], shell=True)
        time.sleep(1)
        reset()
        t0 = time.time()
        d = request_code("probe.user@outlook.com")
        dt = time.time() - t0
        print("  SMTP down: response %s in %.2f s: %s" % (d.status, dt, d.body))
        verdict("AC-15", "SMTP down: the response is the neutral one (sent), unchanged", d.st == "sent" and d.json.get("destination") == "p***@outlook.com", d.body)
        verdict("NF-001", "the response does not wait for the mail server (<15 s)", dt < 15, "%.2f s" % dt)
        time.sleep(3)
        for p in LOGS[:1]:
            tail = open(p, "rb").read().decode("utf8", "replace").splitlines()[-400:]
            lines = [l for l in tail if "auth.otp.delivery" in l and "email_failed" in l]
            print("  log: %s" % [re.sub(r"^.*?\[(\w+) *\]", r"[\1]", l)[:200] for l in lines[-1:]])
            verdict("AC-15/OPS-002", "auth.otp.delivery outcome=email_failed is logged at WARN", bool(lines) and "[WARN ]" in lines[-1], lines[-1][:140] if lines else "none")
    else:
        print("  (PROBE_SINK_STOP not set: SMTP-down half not run)")


def stage_blackhole():
    print("\n== blackhole: NF-001 / OPS-002 with a mail server that accepts the connection and never answers (needs PROBE_SINK_STOP)")
    if not os.environ.get("PROBE_SINK_STOP"):
        print("  (PROBE_SINK_STOP not set: not run)")
        return
    import socket
    subprocess.run(os.environ["PROBE_SINK_STOP"], shell=True)
    time.sleep(1)
    srv = socket.socket()
    srv.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
    srv.bind(("127.0.0.1", 2525))
    srv.listen(16)
    held = []

    def accept_forever():
        while True:
            try:
                conn, _ = srv.accept()
            except OSError:
                return
            held.append(conn)  # accepted, never read from, never answered
    threading.Thread(target=accept_forever, daemon=True).start()
    reset()
    mark = os.path.getsize(LOGS[0]) if LOGS else 0
    t0 = time.time()
    d = request_code("probe.user@outlook.com")
    dt = time.time() - t0
    print("  black-hole SMTP: response HTTP %d in %.3f s: %s" % (d.status, dt, d.body))
    verdict("AC-15", "black-hole SMTP: the neutral response (sent) comes back without waiting for the mail server", d.st == "sent" and d.json.get("destination") == "p***@outlook.com" and dt < 5, "%.3f s" % dt)
    lines = []
    for _ in range(40):
        time.sleep(0.5)
        if LOGS:
            with open(LOGS[0], "rb") as f:
                f.seek(mark)
                new = f.read().decode("utf8", "replace").splitlines()
            lines = [l for l in new if "auth.otp.delivery" in l and "email_failed" in l]
        if lines:
            break
    srv.close()
    for c in held:
        c.close()
    print("  log: %s" % [re.sub(r"^.*?\[(\w+) *\]", r"[\1]", l)[:200] for l in lines[-1:]])
    m = re.search(r"ms=(\d+)", lines[-1]) if lines else None
    verdict("AC-15/OPS-002", "the delivery outcome=email_failed is logged at WARN after the SMTP timeout (about 10 s)", bool(lines) and "[WARN ]" in lines[-1] and m is not None and 9000 <= int(m.group(1)) <= 20000, lines[-1][:140] if lines else "none")


def stage_tz():
    print("\n== tz: one flow on a JVM whose time zone differs from the database's (false refusals)")
    reset()
    ck, code = issue("otp.p13@outlook.com")
    row = sql("SELECT created_at, expires_at, UTC_TIMESTAMP(3), @@session.time_zone, @@system_time_zone FROM otp_challenges;")[0]
    print("  stored created_at / expires_at / UTC now / mysql tz: %s" % row)
    ok = verify(code, ck).st
    verdict("TZ", "correct code on a non-UTC JVM is accepted (no false refusal)", ok == "ok", ok)
    reset()
    ck, code = issue("otp.p14@outlook.com")
    sql("UPDATE otp_challenges SET expires_at = UTC_TIMESTAMP(3) - INTERVAL 2 SECOND;")
    e = verify(code, ck).st
    reset()
    ck, code = issue("otp.p14@outlook.com")
    sql("UPDATE otp_challenges SET expires_at = UTC_TIMESTAMP(3) + INTERVAL 20 SECOND;")
    b = verify(code, ck).st
    verdict("TZ", "expiry boundary is the UTC instant: -2 s refused, +20 s accepted", e == "invalid" and b == "ok", "%s / %s" % (e, b))


def stage_postlogin():
    print("\n== postlogin: a refusal AFTER Subject.login (finishLogin INPUT on a Global Unit type with no landing page)")
    print("  fixture (throwaway copy only): global_unit_types 99, unit 9901 PROBEGU with the allow-list outlook.com, member otp.p20@outlook.com")
    reset()
    before = len(mbox_for("otp.p20@outlook.com"))
    r = request_code("otp.p20@outlook.com", globalUnitId="9901")
    code = code_from("otp.p20@outlook.com", before)
    v = verify(code, r.cookie)
    print("  request -> %s ; verify -> %s" % (r.st, v.body))
    live = v.cookie
    print("  session cookie after the answer: %s ; authenticated=%s ; pre-auth session authenticated=%s" % ("set" if live else "none", authed(live) if live else None, authed(r.cookie)))
    verdict("T11-gap", "RECORD: answer of the post-login refusal is %s; the authenticated session after it is %s" % (v.st, "LEFT BEHIND" if live and authed(live) else "not left behind"), True, v.body)


STAGES = {"postlogin": stage_postlogin, "enum": stage_enum, "attempts": stage_attempts, "resend": stage_resend, "expiry": stage_expiry, "removed": stage_removed,
          "limits": stage_limits, "session": stage_session, "leak": stage_leak, "emaillog": stage_emaillog, "live": stage_live, "nosecret": stage_nosecret,
          "smtp": stage_smtp, "blackhole": stage_blackhole, "tz": stage_tz}
for name in sys.argv[1:]:
    STAGES[name]()
failed = [(a, n) for a, n, ok in verdicts if not ok]
print("\nSUMMARY: %d checks, %d failed%s%s" % (len(verdicts), len(failed), (": " + "; ".join("[%s] %s" % f for f in failed)) if failed else "",
      ("; %d NOT RUN (neither pass nor fail): %s" % (len(not_run), "; ".join("[%s] %s" % f for f in not_run))) if not_run else ""))
sys.exit(1 if failed else 0)
PY
