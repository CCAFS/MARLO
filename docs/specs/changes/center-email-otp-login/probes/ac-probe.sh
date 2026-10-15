#!/usr/bin/env bash
# CHG-OTP-LOGIN-001 -- T15 acceptance probe (requirements section 7), driven over HTTP against a RUNNING local stack.
#
# Usage:  ac-probe.sh <stage> [<stage> ...]
#   enum       AC-4, AC-3, AC-13, FN-006, FN-003, FN-016: the account states, answered side by side; a CGIAR account on the
#              Cognito-active unit gets a code and signs in, and is refused only where its domain is excluded (T21)
#   attempts   AC-6, AC-14 (verify limits), request parameter allow-list (RJ-9)
#   resend     AC-10, AC-8 (superseded), AC-8 (expired, DB-aged) -- includes a real 31 s wait
#   expiry     AC-8: a real 5 min 1 s old challenge (waits 302 s)
#   removed    AC-9: member soft-deleted (crp_users.is_active=0, as deleteCrpUser does) after the request; hard delete as support
#   limits     AC-14: 6th request per email, 21st per client IP
#   session    AC-5 (needs PROBE_JDWP): password login vs code login, session attributes and role rows
#   leak       AC-11 (needs PROBE_JDWP, PROBE_LOGS): full flow, then database (otp_challenges, email_logs) and log search. The code
#              may be in the message of the OPS-005 row, and, outside production, in the one auth.otp.code log line (amended 2026-10-15)
#   emaillog   AC-18 / OPS-005: the code email's email_logs row (delivered, failed, none for a request that sends nothing),
#              and a resend by id and in bulk that sends nothing (the failed half needs PROBE_SINK_STOP and PROBE_SINK_START;
#              the resend half needs PROBE_SA_EMAIL and PROBE_SA_PASS, a super admin test login of the copy)
#   nonprod    AC-19 / FN-018 (needs PROBE_SUPPORT, the app's email.notification): the sink receives one email, only for the support
#              mailbox, prefixed, with the submitted address in the body header; nothing for the submitted address; the code signs in
#   nonprodblank  AC-19, the blank half: run against an application started with a blank email.notification (EMAIL_NOTIFICATION=):
#              nothing is sent, no row, the delivery line says email_failed (needs PROBE_LOGS)
#   live       FN-002 / AC-1 / AC-21: the switch turned off and on, a domain added to and removed from the exclusion, with no
#              restart: the page and the endpoint follow on the next request (T21)
#   nosecret   AC-12: run against an application started with no (or a short) OTP_HMAC_SECRET; records the startup WARN and the JVM env
#              (set PROBE_EXPECT_SECRET_ENV=present|absent to assert which case is running)
#   smtp       AC-15 / OPS-002: notifications off, then delivery failing (needs PROBE_SINK_STOP / PROBE_SINK_START)
#   postlogin  records what a refusal AFTER Subject.login leaves behind (needs the unmapped-type fixture, see README)
#   blackhole  NF-001 / OPS-002: a mail server that accepts and never answers (needs PROBE_SINK_STOP)
#   tz         one request-verify and expiry boundary, for an application started on another JVM time zone
#
# MAIL: a local run is non-production (marlo.production=false), so every code email reaches the sink addressed to the support
# mailbox, whoever asked for it. The probe finds a user's code by the header at the top of the body, "To: <submitted address>",
# never by the envelope or the To header.
# SAFETY: writes to the database (state resets, test users) -- refuses unless the database name contains "probe".
# STANDING FIXTURE (T21): every run first sets, for the Global Unit PROBE_GU, system_otp_login_active = true and
# system_otp_excluded_email_domains = excluded.test (a domain no fixture account uses), the way the Super Admin Specificities tab
# writes them. The users of the README section 2 fixtures must exist, plus otp.staff@cgiar.org (is_cgiar_user=1, a member of the unit).
# Environment: PROBE_BASE, PROBE_MYSQL, PROBE_MBOX, PROBE_GU (as the concurrency probe), PROBE_JDWP (JDWP port of the
# application JVM, or "auto"), PROBE_SUPPORT (the email.notification the application runs with), PROBE_LOGS (colon-separated log files, the CURRENT run's console log first), PROBE_JAVA_CP
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


SWITCH_KEY = "system_otp_login_active"
EXCLUSION_KEY = "system_otp_excluded_email_domains"
STANDING_EXCLUSION = "excluded.test"


def param_id(key):
    """The catalog row of the key for the type of the Global Unit under test."""
    return sql("SELECT p.id FROM parameters p JOIN global_units g ON g.global_unit_type_id = p.global_unit_type_id "
               "WHERE g.id = %s AND p.`key` = '%s';" % (GU, key))[0][0]


def set_unit_value(key, value):
    """The unit's active custom_parameters row for the key, inserted when the unit has none (what the Super Admin
    Specificities tab saves). The next request must follow it with no restart (DD-12)."""
    pid = param_id(key)
    if sql("SELECT COUNT(*) FROM custom_parameters WHERE parameter_id = %s AND global_unit_id = %s;" % (pid, GU))[0][0] == "0":
        sql("INSERT INTO custom_parameters (parameter_id, value, is_active, global_unit_id) VALUES (%s, '%s', 1, %s);" % (pid, value, GU))
    else:
        sql("UPDATE custom_parameters SET value = '%s', is_active = 1 WHERE parameter_id = %s AND global_unit_id = %s;" % (value, pid, GU))


def unit_value(key):
    rows = sql("SELECT value FROM custom_parameters WHERE parameter_id = %s AND global_unit_id = %s AND is_active = 1;" % (param_id(key), GU))
    return rows[0][0] if rows else None


def standing_fixture():
    set_unit_value(SWITCH_KEY, "true")
    set_unit_value(EXCLUSION_KEY, STANDING_EXCLUSION)
    print("  (standing fixture on unit %s: %s=%s, %s=%s)" % (GU, SWITCH_KEY, unit_value(SWITCH_KEY), EXCLUSION_KEY, unit_value(EXCLUSION_KEY)))


def otp_flags(addr):
    """The crpByEmail.do answer for an address: (isCgiarUser, {acronym: (cognitoEnabled, otpEnabled)})."""
    page = http_("POST", "/crpByEmail.do", {"userEmail": addr})
    user = page.json.get("user") or {}
    return user.get("isCgiarUser"), {c.get("acronym"): (c.get("cognitoEnabled"), c.get("otpEnabled")) for c in page.json.get("crps", [])}


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


def sink_chunks():
    """Every message the local sink recorded, whoever it is addressed to."""
    raw = open(MBOX, "rb").read() if os.path.exists(MBOX) else b""
    return re.split(rb"(?m)^From sink ", raw)[1:]


def envelope_rcpts(chunk):
    """The SMTP recipients (RCPT TO) the sink recorded for one message."""
    first = chunk.split(b"\n", 1)[0].decode("utf8", "replace")
    return re.findall(r"<([^>]*)>", first.split(" -> ", 1)[1]) if " -> " in first else []


def text_of(chunk):
    msg = email.message_from_bytes(chunk.split(b"\n", 1)[1])
    parts = {p.get_content_type(): p.get_payload(decode=True).decode("utf8", "replace") for p in msg.walk() if p.get_content_type().startswith("text/")}
    return msg, parts


def submitted_of(chunk):
    """The submitted address named by the non-production header at the top of the plain part ("To: <address>"), or None.
    Outside production the envelope and the To header always name the support mailbox (FN-018), so the header is the only way
    to tell whose code email this is."""
    try:
        parts = text_of(chunk)[1]
    except (IndexError, ValueError):
        return None
    m = re.match(r"To: (\S+)\r?\n", parts.get("text/plain", ""))
    return m.group(1) if m else None


def mbox_for(rcpt):
    """The code emails asked for by this address, found by the body header (see submitted_of), oldest first."""
    return [c for c in sink_chunks() if submitted_of(c) == rcpt]


def without_header(part, html):
    """A body part with the non-production header removed, as the OPS-005 row stores it (line ends as Java wrote them)."""
    pattern = r"^To: \S+<br>-+<br><br>" if html else r"^To: \S+\r?\n-+\r?\n\r?\n"
    return re.sub(pattern, "", part, count=1).replace("\r\n", "\n")


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
              ("non-member", "otp.nonmember@outlook.com"),
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
    verdict("AC-4", "request-code answer, headers and cookie names identical across the 5 account states", all(s == ref for s in shapes.values()), "reference=%s" % (ref[1],))
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
        ("not addressed to the typed address (non-production: the support mailbox only, the typed address in the body header)", "probe.user@outlook.com" not in envelope_rcpts(mine) and len(envelope_rcpts(mine)) == 1 and submitted_of(mine) == "probe.user@outlook.com"),
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
    verdict("AC-4", "later wrong codes, in-window resend, exhaustion and a further code answer identically for all 5 states", len({tuple(v) for v in seq.values()}) == 1 and seq["eligible"] == ["mismatch", "mismatch", "rateLimited", "exhausted", "exhausted"], seq["eligible"])
    print("  -- real 31 s wait, then resend on every state")
    time.sleep(31)
    seq2 = {}
    for n, (a, r) in res.items():
        ck = r.cookie
        rr = request_code(a, ck)
        seq2[n] = (rr.st, verify("135790", rr.cookie or ck).st)
    print("  %s" % seq2)
    verdict("AC-4", "resend after 30 s and a wrong code answer identically for all 5 states", len(set(seq2.values())) == 1 and seq2["eligible"] == ("sent", "mismatch"), seq2["eligible"])
    time.sleep(2)
    mails2 = {n: len(mbox_for(a)) - base_mails[a] for n, (a, r) in res.items()}
    verdict("AC-4", "still only the eligible account was mailed after the resend (2 mails)", mails2["eligible"] == 2 and sum(v for k, v in mails2.items() if k != "eligible") == 0, mails2)
    ck = http_("GET", "/login.do").cookie
    fresh = verify("123456", ck).st
    verdict("FN-016", "verify with no challenge in the session answers like a wrong code", fresh == seq["eligible"][0], fresh)
    stage_enum_cgiar()
    # FN-006 / FN-003 end to end, with the standing exclusion (STANDING_EXCLUSION), no CGIAR rule and no allow-list (T21)
    reset()
    base_unknown = {a: len(mbox_for(a)) for a in ("someone@gmail.com", "x@attackerexcluded.test", "x@excluded.test.evil.com", "x@sub.excluded.test")}
    outs = {}
    # The padded upper-case form uses another local part: the same normalised address would hit the 30 s cooldown of the first one
    for a in ("x@excluded.test", "  Y@EXCLUDED.TEST ", "x@attackerexcluded.test", "x@excluded.test.evil.com", "x@sub.excluded.test", "someone@gmail.com"):
        outs[a] = request_code(a).st
    print("  exclusion %s: %s" % (STANDING_EXCLUSION, outs))
    verdict("FN-006/FN-003", "the excluded domain, as typed and in its normalised form, answers domainNotEnabled",
            outs["x@excluded.test"] == "domainNotEnabled" and outs["  Y@EXCLUDED.TEST "] == "domainNotEnabled")
    verdict("FN-003/AC-2", "lookalikes, a sub-domain that is not listed and an unrelated domain are not excluded: they get the neutral answer",
            all(outs[a] == "sent" for a in ("x@attackerexcluded.test", "x@excluded.test.evil.com", "x@sub.excluded.test", "someone@gmail.com")), outs)
    time.sleep(2)
    verdict("FN-005", "no mail for any of them (none is a registered account), and a row only for the ones that went on",
            all(len(mbox_for(a)) == n for a, n in base_unknown.items()) and int(sql("SELECT COUNT(*) FROM otp_challenges;")[0][0]) == 4)
    before = len(mbox_for("probe.user@outlook.com"))
    st = request_code("  Probe.User@OUTLOOK.COM ").st
    verdict("FN-003", "'  Probe.User@OUTLOOK.COM ' is accepted as the normalised address and mailed", st == "sent" and code_from("probe.user@outlook.com", before) is not None, st)


def stage_enum_cgiar():
    """AC-13 / SEC-009 (amended 2026-10-15, T21): is_cgiar_user plays no part. The unit is Cognito-active (the copy carries
    cognito_auth_active = true for AICCRA), the switch is on."""
    print("  -- AC-13: an is_cgiar_user=1 account on the Cognito-active unit")
    accounts = [("otp.cgiar@outlook.com", "an outlook.com account"), ("otp.staff@cgiar.org", "a cgiar.org account")]
    for addr, label in accounts:
        flag, units = otp_flags(addr)
        print("  crpByEmail for %s (%s): isCgiarUser=%s units(cognitoEnabled, otpEnabled)=%s" % (addr, label, flag, units))
    cgiar_flag, cgiar_units = otp_flags("otp.staff@cgiar.org")
    verdict("AC-13", "step 3 data for the cgiar.org account, empty exclusion of its domain: isCgiarUser, cognitoEnabled and otpEnabled are all true (the two methods are offered)",
            cgiar_flag is True and any(c is True and o is True for c, o in cgiar_units.values()), (cgiar_flag, cgiar_units))
    reset()
    before = len(mbox_for("otp.cgiar@outlook.com")), len(mbox_for("otp.staff@cgiar.org"))
    codes = {}
    for addr, label in accounts:
        ck, code = issue(addr)
        codes[addr] = (ck, code)
    after = len(mbox_for("otp.cgiar@outlook.com")), len(mbox_for("otp.staff@cgiar.org"))
    verdict("AC-13/SEC-009", "both CGIAR accounts are mailed exactly one code each (is_cgiar_user plays no part)", after[0] - before[0] == 1 and after[1] - before[1] == 1, (before, after))
    signed = {addr: verify(code, ck) for addr, (ck, code) in codes.items()}
    print("  verify: %s" % {a: r.body for a, r in signed.items()})
    verdict("AC-13/SEC-009", "each code signs its CGIAR account in", all(r.st == "ok" for r in signed.values()), {a: r.st for a, r in signed.items()})
    if JDWP:
        verdict("AC-13/SEC-005", "the signed-in sessions are authenticated (read from the JVM)", all(authed(r.cookie) is True for r in signed.values()), {a: authed(r.cookie) for a, r in signed.items()})
    else:
        notrun("AC-13/SEC-005", "the signed-in sessions are authenticated (read from the JVM)", "authenticated session: NOT RUN (needs PROBE_JDWP); the answer ok is the evidence")
    # the same account once its domain is excluded: not-enabled on a direct call, and the page data shows only Cognito
    set_unit_value(EXCLUSION_KEY, "cgiar.org")
    try:
        reset()
        mails0 = len(mbox_for("otp.staff@cgiar.org"))
        direct = request_code("otp.staff@cgiar.org")
        flag2, units2 = otp_flags("otp.staff@cgiar.org")
        flag3, units3 = otp_flags("otp.cgiar@outlook.com")
        time.sleep(2)
        print("  cgiar.org excluded: request-code -> %s ; staff %s ; outlook account %s" % (direct.st, units2, units3))
        verdict("AC-13/FN-006", "with cgiar.org excluded a direct request-code call answers domainNotEnabled, no mail, no challenge",
                direct.st == "domainNotEnabled" and len(mbox_for("otp.staff@cgiar.org")) == mails0 and sql("SELECT COUNT(*) FROM otp_challenges;")[0][0] == "0", direct.st)
        verdict("AC-13/UI-006", "with cgiar.org excluded the page data keeps cognitoEnabled true and turns otpEnabled false (step 3 shows only Sign in with CGIAR); an outlook.com account is unaffected",
                flag2 is True and all(c is True and o is False for c, o in units2.values()) and all(o is True for c, o in units3.values()), (units2, units3))
    finally:
        set_unit_value(EXCLUSION_KEY, STANDING_EXCLUSION)
    reset()


def stage_attempts():
    print("\n== attempts: AC-6, verify limits (SEC-010), request parameter allow-list (RJ-9)")
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


def show_row(row):
    """A row for the output: the body holds the code, so only its length is printed."""
    keep = ("id", "to_email", "bbc_email", "cc_email", "subject", "succes_email", "error_email", "source_action", "global_unit_id", "tried", "date_email")
    out = {k: v for k, v in row.items() if k in keep}
    if "message" in row:
        out["message"] = "<%d chars>" % len(row["message"])
    return out


def code_in(message):
    """The six-digit code as the HTML body shows it (the one element that is only the code), or None."""
    m = re.search(r">\s*(\d{6})\s*<", message)
    return m.group(1) if m else None


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


def email_log_six_digit_runs(after, skip=()):
    """{column: rows} holding a run of exactly six digits, in every column but the numeric ones. For a row whose code the probe
    never saw (the mail was not delivered), this is the closest check that no code was written."""
    hits = {}
    for c in [r[0] for r in sql("SHOW COLUMNS FROM email_logs;")]:
        if c in ("id", "tried", "succes_email", "global_unit_id") or c in skip:
            continue
        n = int(sql("SELECT COUNT(*) FROM email_logs WHERE id > %d AND IFNULL(CAST(`%s` AS CHAR), '') REGEXP '(^|[^0-9])[0-9]{6}([^0-9]|$)';" % (after, c))[0][0])
        if n:
            hits[c] = n
    return hits


def sink_total():
    """Every message the local sink has recorded, whoever it is addressed to (a resent mail can be redirected)."""
    return len(sink_chunks())


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
    # AC-11 (amended 2026-10-15): the code is only in the message of that row (the HTML body, as MARLO stores every email); the
    # email and its local part are only in to_email, the recipient column, which every MARLO email row holds.
    email_rows_after(el_max, 1)
    time.sleep(1.5)  # a second, unexpected row would land inside this grace
    new = email_rows(el_max)
    print("  email_logs: %d row(s) written since the flow began (id > %d); source_action %s; succes_email %s; global_unit_id %s" % (
        len(new), el_max, [r["source_action"] for r in new], [r["succes_email"] for r in new], [r["global_unit_id"] for r in new]))
    code_hits = email_log_hits(el_max, code)
    verdict("AC-11", "email_logs (the real table name): the code is only in the message of the one row written for this flow, in no other column",
            code_hits == {"message": 1} and tables == ["email_logs"], "tables=%s code matches per column=%s" % (tables, code_hits))
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
        # Amended 2026-10-15 (OPS-001): outside production the one auth.otp.code line of the handed-off email carries the code and the
        # submitted address, and nothing else does. A local run is non-production, so that line is the only allowed hit of those two.
        for label, needle in (("code", code), ("full email", addr)):
            hits = [l for l in lines if needle in l]
            stray = [l for l in hits if "auth.otp.code " not in l]
            verdict("AC-11", "log %s: the %s is only in the one auth.otp.code line (non-production), in no other line" % (os.path.basename(p), label),
                    not stray and len(hits) == 1, ("%d hits, %d outside the code line: %s" % (len(hits), len(stray), [h[:220] for h in stray[:2]])) if stray or len(hits) != 1 else "%d lines searched, 1 code line" % len(lines))
        for label, needle in (("nonce", nonce or "NO-NONCE"), ("pre-auth session id", pre.cookie), ("authenticated session id", ok.cookie)):
            hits = [l for l in lines if needle in l]
            verdict("AC-11", "log %s has no line with the %s" % (os.path.basename(p), label), not hits, ("%d hits: %s" % (len(hits), [h[:220] for h in hits[:2]])) if hits else "%d lines searched" % len(lines))
        code_lines = [l for l in lines if "auth.otp.code " in l]
        shape = re.search(r"\[INFO *\].*auth\.otp\.code gu=(\S+) to=(\S+) code=(\d{6})$", code_lines[0]) if len(code_lines) == 1 else None
        verdict("AC-11/OPS-001", "log %s: the auth.otp.code line is INFO, names the unit, the submitted address and the code of the email" % os.path.basename(p),
                bool(shape) and shape.group(2) == addr and shape.group(3) == code, re.sub(r"code=\d{6}", "code=<code>", re.sub(r"^.*?(\[INFO)", r"\1", code_lines[0]))[:160] if code_lines else "no line")
    first = list(all_lines.values())[0] if all_lines else []
    otp = [l for l in first if "auth.otp." in l]
    for l in otp:
        print("   " + re.sub(r"code=\d{6}", "code=<code>", re.sub(r"^.*?\[(INFO|WARN)", r"[\1", l))[:200])
    kinds = collections.Counter(re.search(r"auth\.otp\.(\w+)", l).group(1) for l in otp)
    verdict("AC-11/OPS-001", "exactly one auth.otp.request per request call, one auth.otp.verify per verify call, one auth.otp.delivery per mail, and (non-production) one auth.otp.code per mail", kinds == {"request": 1, "verify": 2, "delivery": 1, "code": 1}, dict(kinds))
    ok_lines = [l for l in otp if "outcome=ok" in l]
    print("  structured fields present: %s" % bool(otp and all(re.search(r"outcome=\S+ gu=\S* domain=\S* ms=\d+", l) for l in otp if "auth.otp.code " not in l)))


def stage_emaillog():
    print("\n== emaillog: AC-18 / OPS-005 (the code email recorded in email_logs for support, with its body; resend sends nothing)")
    addr = "probe.user@outlook.com"
    source = "/otpRequestCode"
    prefix = "(TESTING ENVIRONMENT) "
    support = os.environ.get("PROBE_SUPPORT")
    if not support:
        raise SystemExit("emaillog needs PROBE_SUPPORT, the email.notification the application runs with")
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
    sent = mbox_for(addr)[-1]
    mail_subject = str(text_of(sent)[0]["Subject"])
    sent_html = without_header(text_of(sent)[1]["text/html"], True)
    r = rows[0] if len(rows) == 1 else {}
    print("  delivered: %d row(s) above id %d: %s" % (len(rows), el0, show_row(r)))
    verdict("AC-18", "delivered code email: exactly one new email_logs row", len(rows) == 1, "rows=%d" % len(rows))
    verdict("AC-18", "delivered row: succes_email 1, no error, source_action /otpRequestCode, global_unit_id %s, tried 1, the submitted address as recipient, the mail's subject without the non-production prefix, a date" % GU,
            bool(r) and r["succes_email"] == "1" and r["error_email"] == "NULL" and r["source_action"] == source
            and r["global_unit_id"] == GU and r["tried"] == "1" and r["to_email"] == addr and mail_subject.startswith(prefix) and r["subject"] == mail_subject[len(prefix):] and r["date_email"] != "NULL",
            "mail subject %r, row subject %r" % (mail_subject, r.get("subject")))
    expected_body = sent_html.replace("\\", "\\\\").replace("\n", "\\n").replace("\t", "\\t")  # mysql -N prints a newline as \n
    verdict("AC-18", "delivered row: message is the HTML part of the sent email with its code and without the non-production header (amended 2026-10-15)",
            bool(r) and code in sent_html and r["message"] == expected_body and not r["message"].startswith("To:") and "TESTING" not in r["message"],
            "message %d chars, equal to the sent HTML minus the header: %s" % (len(r.get("message", "")), bool(r) and r["message"] == expected_body))
    verdict("AC-18", "delivered row: bcc is the support mailbox (non-production), cc, file name, file content and message id stay NULL",
            bool(r) and all(r[c] == "NULL" for c in ("cc_email", "file_name", "fileContent", "message_id")) and r["bbc_email"] not in ("NULL", "") and r["bbc_email"] == support,
            "bbc_email is the support mailbox: %s" % (bool(r) and r["bbc_email"] == support))
    verdict("AC-18", "delivered row: the code is in the message and in no other column, and no other column holds a run of six digits",
            email_log_hits(el0, code) == {"message": 1} and email_log_six_digit_runs(el0, skip=("message",)) == {},
            "code matches=%s six-digit runs outside message=%s" % (email_log_hits(el0, code), email_log_six_digit_runs(el0, skip=("message",))))
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
        print("  failed (sink stopped): response %s ; %d row(s) above id %d: %s" % (d.body, len(frows), el1, show_row(f)))
        verdict("AC-18", "failed code email: the response is still the neutral one and exactly one new email_logs row exists", d.st == "sent" and len(frows) == 1, "answer=%s rows=%d" % (d.st, len(frows)))
        verdict("AC-18", "failed row: succes_email 0, error_email a bare class name (no address, no space, no SMTP text), the source, the unit, the submitted address as recipient, the support mailbox as bcc, the subject without the prefix",
                bool(f) and f["succes_email"] == "0" and bool(bare_class.match(f["error_email"])) and f["source_action"] == source
                and f["global_unit_id"] == GU and f["tried"] == "1" and f["to_email"] == addr and f["bbc_email"] == support and f["bbc_email"] not in ("NULL", "")
                and not f["subject"].startswith(prefix), "error_email=%r" % (f.get("error_email"),))
        fcode = code_in(f.get("message", ""))
        verdict("AC-18", "failed row: the message holds the body with its code and no test header; the code is in no other column and no other column holds a run of six digits (the code never reached the sink)",
                bool(fcode) and not f["message"].startswith("To:") and email_log_hits(el1, fcode or "NO-CODE") == {"message": 1} and email_log_six_digit_runs(el1, skip=("message",)) == {},
                "code found in the body: %s, in %s" % (bool(fcode), email_log_hits(el1, fcode or "NO-CODE")))
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
             ("soft-deleted", "otp.softdeleted@outlook.com", "sent"), ("unregistered domain, not excluded", "someone@gmail.com", "sent"),
             ("excluded domain", "x@" + STANDING_EXCLUSION, "domainNotEnabled")]
    answers = {name: request_code(a).st for name, a, want in cases}
    time.sleep(3)
    quiet = email_rows(el2)
    print("  requests that send nothing: %s ; email_logs rows written: %d ; mails at the sink: %d" % (answers, len(quiet), sink_total() - mails0))
    verdict("AC-18", "an ineligible request (4 account states, an unregistered address, 1 excluded domain) leaves no email_logs row and no mail",
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


def stage_nonprod():
    print("\n== nonprod: AC-19 / FN-018 (marlo.production=false: the support mailbox only, the prefix, the header; the row keeps the submitted address)")
    support = os.environ.get("PROBE_SUPPORT")
    if not support:
        raise SystemExit("nonprod needs PROBE_SUPPORT, the email.notification the application runs with")
    addr = "probe.user@outlook.com"
    prefix = "(TESTING ENVIRONMENT) "
    reset()
    el0 = email_log_max()
    total0 = sink_total()
    ck, code = issue(addr)
    time.sleep(3)
    new = sink_chunks()[total0:]
    verdict("AC-19", "an eligible request leaves exactly one email at the sink", len(new) == 1, "%d new message(s)" % len(new))
    if len(new) != 1:
        return
    mail = new[0]
    msg, parts = text_of(mail)
    plain, html = parts.get("text/plain", ""), parts.get("text/html", "")
    env = envelope_rcpts(mail)
    print("  envelope recipients: %s ; To header: %s ; Cc: %s ; Bcc: %s ; subject: %r" % (env, msg.get_all("To"), msg.get_all("Cc"), msg.get_all("Bcc"), str(msg["Subject"])))
    verdict("AC-19", "the only recipient (envelope and To header, no Cc or Bcc) is the support mailbox email.notification",
            env == [support] and [a.strip() for a in (msg.get_all("To") or [])] == [support] and msg.get_all("Cc") is None and msg.get_all("Bcc") is None)
    verdict("AC-19", "the subject starts with '(TESTING ENVIRONMENT) '", str(msg["Subject"]).startswith(prefix), str(msg["Subject"]))
    verdict("AC-19", "the plain and the HTML part start with a header naming the submitted address",
            re.match(r"To: %s\r?\n-+\r?\n\r?\n" % re.escape(addr), plain) is not None and re.match(r"To: %s<br>-+<br><br>" % re.escape(addr), html) is not None,
            "plain starts %r ; html starts %r" % (plain[:60], html[:60]))
    all_rcpts = [a for c in sink_chunks() for a in envelope_rcpts(c)]
    all_to = [str(email.message_from_bytes(c.split(b"\n", 1)[1]).get("To")) for c in sink_chunks()]
    verdict("AC-19", "the submitted address received nothing: it is no SMTP recipient and in no To header of any message at the sink",
            addr not in all_rcpts and not [t for t in all_to if addr in t], "%d message(s) at the sink, recipients seen: %s" % (len(all_rcpts), sorted(set(all_rcpts))))
    v = verify(code, ck)
    verdict("AC-19", "the code from the redirected email signs the user in", v.st == "ok" and authed(v.cookie) in (True, None), v.st)
    email_rows_after(el0, 1)
    time.sleep(1.5)
    rows = email_rows(el0)
    r = rows[0] if len(rows) == 1 else {}
    print("  email_logs row: %s" % (show_row(r)))
    sent_html = without_header(html, True)
    expected_body = sent_html.replace("\\", "\\\\").replace("\n", "\\n").replace("\t", "\\t")
    verdict("AC-19", "its email_logs row holds the submitted address in to_email, the support mailbox in bbc_email and the subject without the prefix",
            bool(r) and r["to_email"] == addr and r["bbc_email"] == support and r["subject"] == str(msg["Subject"])[len(prefix):] and r["cc_email"] == "NULL"
            and r["succes_email"] == "1" and r["source_action"] == "/otpRequestCode")
    verdict("AC-19", "its message is the HTML as built for production (the code, no test header)",
            bool(r) and code in sent_html and r["message"] == expected_body and not r["message"].startswith("To:") and "TESTING" not in r["message"])


def stage_nonprodblank():
    print("\n== nonprodblank: AC-19, blank email.notification (the application was started with EMAIL_NOTIFICATION= empty)")
    addr = "probe.user@outlook.com"
    reset()
    el0 = email_log_max()
    total0 = sink_total()
    mark = os.path.getsize(LOGS[0]) if LOGS else 0
    r = request_code(addr)
    time.sleep(4)
    new = sink_chunks()[total0:]
    rows = email_rows(el0)
    lines = []
    if LOGS:
        with open(LOGS[0], "rb") as f:
            f.seek(mark)
            lines = [l for l in f.read().decode("utf8", "replace").splitlines() if "auth.otp." in l]
    for l in lines:
        print("   " + re.sub(r"code=\d{6}", "code=<code>", re.sub(r"^.*?\[(INFO|WARN)", r"[\1", l))[:200])
    print("  response %s ; messages at the sink: %d ; email_logs rows: %d" % (r.body, len(new), len(rows)))
    verdict("AC-19", "with email.notification blank the response is still the neutral sent", r.st == "sent", r.body)
    verdict("AC-19", "with email.notification blank nothing is sent: no message at the sink, none for the submitted address", len(new) == 0, "%d message(s)" % len(new))
    verdict("AC-19", "with email.notification blank no email_logs row is written", len(rows) == 0, "%d row(s)" % len(rows))
    delivery = [l for l in lines if "auth.otp.delivery" in l]
    verdict("AC-19", "the delivery line says email_failed, at WARN, and no auth.otp.code line is written",
            len(delivery) == 1 and "outcome=email_failed" in delivery[0] and "[WARN" in delivery[0] and not [l for l in lines if "auth.otp.code" in l], "%d delivery line(s)" % len(delivery))


def stage_live():
    print("\n== live: FN-002 / FN-001 / AC-1 / AC-21 (the switch and the exclusion changed with no restart)")
    reset()
    flags = lambda body: re.findall(r'"otpEnabled":(true|false)', body)
    before = request_code("probe.user@outlook.com").st
    on_state = (unit_value(SWITCH_KEY), unit_value(EXCLUSION_KEY))
    # 1. the switch turned off
    set_unit_value(SWITCH_KEY, "false")
    reset()
    mails0 = len(mbox_for("probe.user@outlook.com"))
    after = request_code("probe.user@outlook.com")
    page = http_("POST", "/crpByEmail.do", {"userEmail": "probe.user@outlook.com"})
    print("  switch off: request-code -> %s ; crpByEmail otpEnabled flags: %s" % (after.st, flags(page.body)))
    time.sleep(2)
    verdict("AC-1/FN-001", "switch off: request-code refused (domainNotEnabled), no mail, no challenge", after.st == "domainNotEnabled" and len(mbox_for("probe.user@outlook.com")) == mails0 and sql("SELECT COUNT(*) FROM otp_challenges;")[0][0] == "0", after.st)
    verdict("AC-1", "switch off: crpByEmail offers no OTP for any unit", set(flags(page.body)) == {"false"})
    pw = http_("POST", "/login.do", {"user.email": PASSWORD_USER[0], "user.password": PASSWORD_USER[1], "crp": "AICCRA"}, http_("GET", "/login.do").cookie)
    verdict("AC-1/FN-019", "password login unchanged with the feature off", pw.status == 302 and dict(pw.headers).get("Location", "").endswith("/AICCRA/crpDashboard.do"), dict(pw.headers).get("Location"))
    co = http_("GET", "/cognitoLogin.do?crp=AICCRA")
    print("  cognitoLogin.do?crp=AICCRA -> HTTP %d Location %s" % (co.status, (dict(co.headers).get("Location") or "")[:90]))
    # 2. turned back on
    set_unit_value(SWITCH_KEY, "true")
    reset()
    back = request_code("probe.user@outlook.com").st
    page2 = http_("POST", "/crpByEmail.do", {"userEmail": "probe.user@outlook.com"})
    print("  switch on again: request-code -> %s ; otpEnabled flags: %s" % (back, flags(page2.body)))
    verdict("FN-002/AC-21", "turning the switch on takes effect on the next request with no restart", back == "sent" and "true" in flags(page2.body))
    # 3. a domain added to the exclusion
    set_unit_value(EXCLUSION_KEY, STANDING_EXCLUSION + ", Outlook.COM")
    reset()
    mails1 = len(mbox_for("probe.user@outlook.com"))
    added = request_code("probe.user@outlook.com")
    page3 = http_("POST", "/crpByEmail.do", {"userEmail": "probe.user@outlook.com"})
    time.sleep(2)
    print("  outlook.com excluded: request-code -> %s ; otpEnabled flags: %s" % (added.st, flags(page3.body)))
    verdict("FN-002/AC-21", "adding a domain to the exclusion takes effect on the next request: domainNotEnabled, no mail, otpEnabled false",
            added.st == "domainNotEnabled" and len(mbox_for("probe.user@outlook.com")) == mails1 and set(flags(page3.body)) == {"false"}, added.st)
    # 4. and removed again
    set_unit_value(EXCLUSION_KEY, STANDING_EXCLUSION)
    reset()
    removed = request_code("probe.user@outlook.com").st
    page4 = http_("POST", "/crpByEmail.do", {"userEmail": "probe.user@outlook.com"})
    print("  outlook.com no longer excluded: request-code -> %s ; otpEnabled flags: %s" % (removed, flags(page4.body)))
    verdict("FN-002/AC-21", "removing a domain from the exclusion takes effect on the next request", removed == "sent" and "true" in flags(page4.body))
    # 5. an empty exclusion
    set_unit_value(EXCLUSION_KEY, "")
    reset()
    empty = request_code("probe.user@outlook.com").st
    verdict("FN-003", "an empty exclusion excludes nobody", empty == "sent", empty)
    set_unit_value(EXCLUSION_KEY, STANDING_EXCLUSION)
    print("  (before the toggles: %s ; the unit's values then: switch=%s exclusion=%s ; restored to the standing fixture)" % (before, on_state[0], on_state[1]))


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
    print("  the unit's switch is on; request-code -> %s ; otpEnabled flags %s ; challenge rows %s" % (r.st, flags, rows))
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
    print("  fixture (throwaway copy only): global_unit_types 99, unit 9901 PROBEGU whose type has the switch on by catalog default, member otp.p20@outlook.com")
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
          "limits": stage_limits, "session": stage_session, "leak": stage_leak, "emaillog": stage_emaillog, "nonprod": stage_nonprod, "nonprodblank": stage_nonprodblank, "live": stage_live, "nosecret": stage_nosecret,
          "smtp": stage_smtp, "blackhole": stage_blackhole, "tz": stage_tz}
print("== standing fixture")
standing_fixture()
for name in sys.argv[1:]:
    STAGES[name]()
failed = [(a, n) for a, n, ok in verdicts if not ok]
print("\nSUMMARY: %d checks, %d failed%s%s" % (len(verdicts), len(failed), (": " + "; ".join("[%s] %s" % f for f in failed)) if failed else "",
      ("; %d NOT RUN (neither pass nor fail): %s" % (len(not_run), "; ".join("[%s] %s" % f for f in not_run))) if not_run else ""))
sys.exit(1 if failed else 0)
PY
