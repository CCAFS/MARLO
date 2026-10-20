#!/usr/bin/env bash
# CHG-PWD-SWITCH-001-T08 -- live probes of the password switch, driven over HTTP against a RUNNING local stack.
#
# Usage:  ac-live.sh <stage> [<stage> ...]
#   all-on     precondition of AC-2: the password switch is on (no custom_parameters row, the migration default) in the units
#              under test, and crpByEmail.do reports passwordEnabled true for them. Run CHG-OTP-LOGIN-001 ac-probe.sh after it
#   ac10       AC-10, OPS-001, SEC-001, SEC-002: with the switch of the unit off, a CORRECT password posted to validateUser.do and
#              to login.do is refused with exactly the answer a wrong password gets on a unit with the switch on (body, status,
#              Set-Cookie), makes no credential check (no "Trying to log in" line, last_login unchanged, no session), writes one
#              INFO line naming the gate and the unit and no password; a control with the switch on signs in
#   ac8        AC-8, FN-002: the switch changed by raw SQL, with no restart (same JVM pid), is followed by crpByEmail.do,
#              validateUser.do and login.do on the next request, in both directions. The stage the falsifier turns red
#   ac7        AC-7, FN-006, OPS-002: run against an application started with a blank OTP_HMAC_SECRET: the password is offered and
#              accepted whatever the switch says, no code is issued, and exactly one ERROR line per unit is written however many
#              times the unit is looked up (needs PROBE_LOGS)
#   ac12       AC-12, SEC-006: with the switch off in every unit, the CLARISA public user still authenticates through its filter
#              (needs PROBE_JDWP and PROBE_JAVA_CP to read the session) and an /api/** HTTP Basic request still authenticates
#   override   AC-3 .. AC-6 live and the 'via=password_off' log line: with the password off, a code is issued (and signs in) with the
#              code switch off and for an excluded domain; a Cognito account gets the neutral answer and no mail (needs PROBE_MBOX, PROBE_LOGS)
#   runbook-sql  runs the two SQL statements of runbook.md as written (the count and the turn-on) and checks their effect
#   walk       the advisory items of the T05 pointer: an authenticated session posting the form, a CGIAR account against a
#              refusal, the accepted CGIAR residual (records what is reachable offline and what is not)
#
# SAFETY: writes to the database (custom_parameters rows of the units, last_login reads) -- refuses unless the database name
# contains "probe". No password, address or session id of a real account is printed: the accounts are the fixtures of fixtures.sh.
# Environment: PROBE_BASE, PROBE_MYSQL (command opening "mysql -N <copy>"), PROBE_PW_EMAIL / PROBE_PW_PASS (fixture account that is
# an active, non-CGIAR member of units PROBE_GU and PROBE_GU2), PROBE_GU (default 45, AICCRA), PROBE_GU2 (default 47, AICCRA_III),
# PROBE_LOGS (the CURRENT run's console log), PROBE_MBOX, PROBE_JDWP (port or "auto") and PROBE_JAVA_CP (directory with SessDump.class).
set -euo pipefail
exec python3 -u -I - "$@" <<'PY'
import email, http.client, json, os, re, subprocess, sys, time, urllib.parse

BASE = os.environ.get("PROBE_BASE", "http://localhost:8080/marlo-web")
MYSQL = os.environ.get("PROBE_MYSQL", "mysql -N aiccradb_otp_probe")
GU = os.environ.get("PROBE_GU", "45")
GU2 = os.environ.get("PROBE_GU2", "47")
LOGS = [p for p in os.environ.get("PROBE_LOGS", "").split(":") if p]
MBOX = os.environ.get("PROBE_MBOX", "")
JDWP = os.environ.get("PROBE_JDWP")
JCP = os.environ.get("PROBE_JAVA_CP", "")
EMAIL = os.environ.get("PROBE_PW_EMAIL", "probe.user@outlook.com")
PW = os.environ.get("PROBE_PW_PASS", "")
WRONG = "not-the-password-" + str(os.getpid())
u = urllib.parse.urlparse(BASE)
HOST, PORT, PREFIX = u.hostname, u.port or 80, u.path.rstrip("/")
PW_KEY = "system_password_login_active"
OTP_KEY = "system_otp_login_active"
EXCL_KEY = "system_otp_excluded_email_domains"


def sql(q):
    out = subprocess.run(MYSQL, shell=True, input=q, capture_output=True, text=True)
    if out.returncode != 0:
        raise SystemExit("mysql failed: " + out.stderr.strip()[:300])
    return [l.split("\t") for l in out.stdout.strip().splitlines() if l]


db = sql("SELECT DATABASE();")[0][0]
if "probe" not in db:
    raise SystemExit("refusing to run: database %r is not a throwaway probe copy" % db)
if not PW:
    raise SystemExit("PROBE_PW_PASS is required (the fixture account's test password)")

verdicts = []
not_run = []


def verdict(ac, name, ok, detail=""):
    verdicts.append((ac, name, ok))
    print("  => %s [%s] %s%s" % ("PASS" if ok else "FAIL", ac, name, ("   (%s)" % (detail,)) if detail != "" else ""))


def notrun(ac, name, line):
    print("  " + line)
    not_run.append((ac, name))


# ---- database: the switches, the way the Super Admin Specificities tab (or one SQL statement) writes them -------------------
def param_id(unit, key):
    return sql("SELECT p.id FROM parameters p JOIN global_units g ON g.global_unit_type_id = p.global_unit_type_id "
               "WHERE g.id = %s AND p.`key` = '%s';" % (unit, key))[0][0]


def set_value(unit, key, value):
    """value None deletes the unit's row, so the catalog default applies (the state after the migration)."""
    pid = param_id(unit, key)
    sql("DELETE FROM custom_parameters WHERE parameter_id = %s AND global_unit_id = %s;" % (pid, unit))
    if value is not None:
        sql("INSERT INTO custom_parameters (parameter_id, value, is_active, global_unit_id) VALUES (%s, '%s', 1, %s);"
            % (pid, value, unit))


def raw_update(unit, key, value):
    """The raw statement of the runbook: no Hibernate, no cache invalidation, no restart."""
    sql("UPDATE custom_parameters SET value = '%s' WHERE parameter_id = %s AND global_unit_id = %s;" % (value, param_id(unit, key), unit))


def user_id(email):
    return sql("SELECT id FROM users WHERE email = '%s';" % email)[0][0]


def last_login(email):
    return sql("SELECT IFNULL(last_login, 'NULL') FROM users WHERE email = '%s';" % email)[0][0]


# ---- HTTP -------------------------------------------------------------------------------------------------------------------
class R:
    def __init__(self, status, body, headers):
        self.status, self.body, self.headers = status, body, headers
        try:
            self.json = json.loads(body)
        except ValueError:
            self.json = {}
        live = [m for m in re.findall(r"JSESSIONID=([^;]+)", "\n".join(v for k, v in headers if k.lower() == "set-cookie")) if m != "deleteMe"]
        self.cookie = live[-1] if live else None

    def header(self, name):
        return [v for k, v in self.headers if k.lower() == name.lower()]


def http_(method, path, data=None, cookie=None, extra=None):
    c = http.client.HTTPConnection(HOST, PORT, timeout=90)
    h = dict(extra or {})
    body = None
    if data is not None:
        body = urllib.parse.urlencode(data)
        h["Content-Type"] = "application/x-www-form-urlencoded"
    if cookie:
        h["Cookie"] = "JSESSIONID=" + cookie
    c.request(method, PREFIX + path, body, h)
    r = c.getresponse()
    out = R(r.status, r.read().decode("utf8", "replace"), r.getheaders())
    c.close()
    return out


def fresh_session():
    return http_("GET", "/login.do").cookie


def validate(password, unit, email=EMAIL, cookie=None):
    return http_("POST", "/validateUser.do", {"userEmail": email, "userPassword": password, "globalUnitId": unit, "agree": "true"},
                 cookie or fresh_session())


def login(password, acronym, email=EMAIL, cookie=None):
    return http_("POST", "/login.do", {"user.email": email, "user.password": password, "crp": acronym}, cookie or fresh_session())


def acronym(unit):
    return sql("SELECT acronym FROM global_units WHERE id = %s;" % unit)[0][0]


def flags(email=EMAIL):
    page = http_("POST", "/crpByEmail.do", {"userEmail": email})
    return {c.get("acronym"): (c.get("otpEnabled"), c.get("passwordEnabled")) for c in page.json.get("crps", [])}


def jvm_pid():
    return subprocess.run("lsof -iTCP:%d -sTCP:LISTEN -P -t | head -1" % PORT, shell=True, capture_output=True, text=True).stdout.strip()


def normal_cookies(r):
    """Set-Cookie headers with the session id and the expiry date masked: what is left is what a client can tell apart."""
    out = []
    for v in r.header("set-cookie"):
        v = re.sub(r"(JSESSIONID=)[^;]*", r"\1<sid>", v)
        v = re.sub(r"Expires=[^;]*", "Expires=<t>", v)
        out.append(v)
    return sorted(out)


def normal_json(r, drop=("globalUnitId",)):
    d = dict(r.json)
    for k in drop:
        d.pop(k, None)
    return json.dumps(d, sort_keys=True)


# ---- the application log ----------------------------------------------------------------------------------------------------
def log_pos():
    return {p: os.path.getsize(p) for p in LOGS if os.path.exists(p)}


def log_since(pos):
    out = []
    for p, off in pos.items():
        with open(p, "rb") as f:
            f.seek(off)
            out.extend(f.read().decode("utf8", "replace").splitlines())
    return out


def need_logs():
    if not LOGS:
        raise SystemExit("PROBE_LOGS (the current run's console log) is required for this stage")


GATE = "auth.password.refused gate=password_switch gu="
CRED = "Trying to log in a user against the database."


def sessdump(sid):
    out = subprocess.run(["java", "-cp", JCP, "SessDump", jdwp_port(), sid], capture_output=True, text=True)
    attrs = {}
    for line in out.stdout.splitlines():
        m = re.match(r"  (.*?) = (.*)$", line)
        if m:
            attrs[m.group(1).strip('"')] = m.group(2)
    return attrs


def authed(cookie):
    """True when the Shiro session behind the cookie carries an authenticated principal, read from the JVM (the dashboard
    answers HTTP 200 for anonymous callers too). None without PROBE_JDWP."""
    if not cookie or not JDWP or not JCP:
        return None
    attrs = sessdump(cookie)
    return "current_user" in attrs and attrs.get("org.apache.shiro.subject.support.DefaultSubjectContext_AUTHENTICATED_SESSION_KEY") == "true"


def jdwp_port():
    if JDWP != "auto":
        return JDWP
    pid = jvm_pid()
    out = subprocess.run("lsof -a -p %s -iTCP -sTCP:LISTEN -P -Fn" % pid, shell=True, capture_output=True, text=True).stdout
    return [m for m in re.findall(r"n(?:localhost|127\.0\.0\.1):(\d+)", out)][0]



def sink_chunks():
    raw = open(MBOX, "rb").read() if MBOX and os.path.exists(MBOX) else b""
    return re.split(rb"(?m)^From sink ", raw)[1:]


def text_of(chunk):
    msg = email.message_from_bytes(chunk.split(b"\n", 1)[1])
    return {p.get_content_type(): p.get_payload(decode=True).decode("utf8", "replace") for p in msg.walk() if p.get_content_type().startswith("text/")}


def mails_for(rcpt):
    """The code emails asked for by this address (outside production the body starts with 'To: <submitted address>')."""
    out = []
    for c in sink_chunks():
        try:
            m = re.match(r"To: (\S+)\r?\n", text_of(c).get("text/plain", ""))
        except (IndexError, ValueError):
            continue
        if m and m.group(1) == rcpt:
            out.append(c)
    return out


def code_of(rcpt, after):
    for _ in range(80):
        mine = mails_for(rcpt)
        if len(mine) > after:
            return re.search(r"Your code is: (\d{6})", text_of(mine[-1])["text/plain"]).group(1)
        time.sleep(0.25)
    return None


def reset_otp():
    sql("DELETE FROM otp_challenges; DELETE FROM otp_rate_limits; DELETE FROM otp_cooldowns;")


def request_code(addr, unit, cookie=None):
    return http_("POST", "/otpRequestCode.do", {"email": addr, "globalUnitId": unit, "agree": "true"}, cookie or fresh_session())


# ================================================================================================================================
def stage_all_on():
    print("\n== all-on: the password switch is on in the units under test (AC-2 precondition)")
    for unit in (GU, GU2):
        set_value(unit, PW_KEY, None)
    seen = flags()
    print("  (crpByEmail passwordEnabled per unit: %s)" % {k: v[1] for k, v in seen.items()})
    verdict("AC-2", "no custom_parameters row for the password key: every unit reads on (FN-001, AC-1)",
            all(v[1] is True for v in seen.values()) and len(seen) >= 2, str(sorted(seen)))
    r = validate(PW, GU)
    verdict("AC-2", "password sign-in works in the unit with the default", r.json.get("userFound", {}).get("loginSuccess") is True)


def stage_ac10():
    need_logs()
    print("\n== ac10: SEC-001 / SEC-002 / OPS-001 / AC-10 (the password is refused where the switch is off)")
    set_value(GU, PW_KEY, "false")
    set_value(GU2, PW_KEY, None)
    a_off, a_on = acronym(GU), acronym(GU2)
    ll0 = last_login(EMAIL)
    print("  (unit %s %s: switch off; unit %s %s: switch on by default; fixture account, last_login before: %s)" % (GU, a_off, GU2, a_on, ll0))

    # ---- validateUser.do
    pos = log_pos()
    refused = validate(PW, GU)
    lines = log_since(pos)
    gate = [l for l in lines if GATE in l]
    verdict("AC-10", "validateUser.do: the CORRECT password for the switch-off unit is refused",
            refused.json.get("userFound", {}).get("loginSuccess") is False and "userName" not in refused.json.get("userFound", {}),
            normal_json(refused))
    verdict("OPS-001", "validateUser.do: exactly one INFO line names the gate and the unit", len(gate) == 1 and ("gu=" + a_off) in gate[0] and "[INFO ]" in gate[0],
            "%d line(s)" % len(gate))
    verdict("OPS-001", "that line holds neither the password nor the address",
            len(gate) == 1 and PW not in gate[0] and EMAIL not in gate[0] and "@" not in gate[0].split(GATE, 1)[1])
    verdict("SEC-001", "validateUser.do: no credential check ran (no 'Trying to log in' line, no LDAP line)",
            not any(CRED in l for l in lines) and not any(re.search(r"ADConexion|LDAP|ldap", l) for l in lines),
            "%d log lines in the window" % len(lines))
    verdict("OPS-001", "the password and the address appear nowhere in that request's log window", not any(PW in l or EMAIL in l for l in lines))
    pos = log_pos()
    control = validate(WRONG, GU2)
    clines = log_since(pos)
    verdict("SEC-002", "validateUser.do: the refusal equals a wrong password on a unit with the switch on (body, ignoring the echoed unit id)",
            normal_json(refused) == normal_json(control), "refused=%s control=%s" % (normal_json(refused), normal_json(control)))
    verdict("SEC-002", "validateUser.do: same Set-Cookie headers (including rememberMe=deleteMe), session id and date masked",
            normal_cookies(refused) == normal_cookies(control), str(normal_cookies(refused)))
    verdict("SEC-002", "validateUser.do: same HTTP status", refused.status == control.status, "%d / %d" % (refused.status, control.status))
    verdict("SEC-001", "control: the wrong password on the switch-on unit DID run the credential check (the probe can see one)",
            any(CRED in l for l in clines) and not any(GATE in l for l in clines))

    # ---- login.do
    pos = log_pos()
    lrefused = login(PW, a_off)
    lines = log_since(pos)
    gate = [l for l in lines if GATE in l]
    verdict("AC-10", "login.do: the CORRECT password for the switch-off unit is refused (HTTP 200, the login page, no redirect)",
            lrefused.status == 200 and not lrefused.header("location"), "HTTP %d" % lrefused.status)
    verdict("OPS-001", "login.do: exactly one INFO line names the gate and the unit, without password or address",
            len(gate) == 1 and ("gu=" + a_off) in gate[0] and PW not in gate[0] and EMAIL not in gate[0], "%d line(s)" % len(gate))
    verdict("SEC-001", "login.do: no credential check ran", not any(CRED in l for l in lines) and not any(re.search(r"ADConexion|LDAP|ldap", l) for l in lines))
    verdict("OPS-001", "login.do: the password and the address appear nowhere in that request's log window", not any(PW in l or EMAIL in l for l in lines))
    lcontrol = login(WRONG, a_on)
    verdict("SEC-002", "login.do: the refusal equals a wrong password on a unit with the switch on (status, page byte for byte)",
            lrefused.status == lcontrol.status and lrefused.body == lcontrol.body, "%d bytes / %d bytes" % (len(lrefused.body), len(lcontrol.body)))
    verdict("SEC-002", "login.do: same Set-Cookie headers", normal_cookies(lrefused) == normal_cookies(lcontrol), str(normal_cookies(lrefused)))

    # ---- nothing was granted
    verdict("SEC-001", "last_login did not move: no sign-in happened", last_login(EMAIL) == ll0, ll0)
    for what, r in (("validateUser.do", refused), ("login.do", lrefused)):
        a = authed(r.cookie)
        if a is None:
            notrun("SEC-001", "refused session not signed in (%s)" % what, "session of the refused %s: NOT RUN (needs PROBE_JDWP and PROBE_JAVA_CP)" % what)
        else:
            verdict("SEC-001", "the session of the refused %s holds no authenticated principal (read from the JVM)" % what, a is False)
    # ---- a CGIAR account on a unit without Cognito: its password would go to Active Directory over LDAP
    cg = "probe.cgiar@cgiar.org"
    set_value(GU2, PW_KEY, "false")
    pos = log_pos()
    cg_off = validate(WRONG, GU2, email=cg)
    lines = log_since(pos)
    ldap = [l for l in lines if re.search(r"LDAPService|createADConectionGeneric|Could not connect to .*Domain Controller|UnknownHostException", l)]
    gate = [l for l in lines if GATE in l]
    verdict("SEC-001", "CGIAR account, switch off: NO LDAP call is made before the refusal (no directory connection attempt in the log)",
            len(ldap) == 0 and len(gate) == 1 and ("gu=" + a_on) in gate[0], "%d LDAP line(s), %d gate line(s)" % (len(ldap), len(gate)))
    set_value(GU2, PW_KEY, None)
    pos = log_pos()
    validate(WRONG, GU2, email=cg)
    ldap_on = [l for l in log_since(pos) if re.search(r"LDAPService|createADConectionGeneric|Could not connect to .*Domain Controller|UnknownHostException", l)]
    verdict("SEC-001", "control: with the switch on the same request DOES try the directory (the probe can see an LDAP attempt)", len(ldap_on) >= 1,
            "%d LDAP line(s)" % len(ldap_on))

    # ---- control: switch on -> the same correct password signs in
    set_value(GU, PW_KEY, "true")
    ok = validate(PW, GU)
    verdict("AC-10", "control: with the switch back on, the same correct password signs in (so the refusal came from the switch)",
            ok.json.get("userFound", {}).get("loginSuccess") is True)
    lok = login(PW, a_off)
    verdict("AC-10", "control: login.do with the switch on redirects to the dashboard",
            lok.status == 302 and "crpDashboard.do" in "".join(lok.header("location")), ",".join(lok.header("location")))
    if authed(lok.cookie) is not None:
        verdict("SEC-001", "control: that login.do session IS authenticated, so the probe can tell a signed-in session from a refused one",
                authed(lok.cookie) is True)
    set_value(GU, PW_KEY, "false")


def stage_ac8():
    print("\n== ac8: FN-002 / AC-8 (a change of the switch is live: raw SQL, no restart)")
    pid0 = jvm_pid()
    a = acronym(GU)
    set_value(GU, PW_KEY, "false")
    set_value(GU2, PW_KEY, None)
    set_value(GU, OTP_KEY, "true")
    set_value(GU, EXCL_KEY, "")
    print("  (JVM pid %s; unit %s: password switch false, code switch true, no exclusion)" % (pid0, GU))
    f = flags()
    verdict("AC-8", "off: crpByEmail offers the code only (otpEnabled true, passwordEnabled false)", f.get(a) == (True, False), str(f.get(a)))
    r = validate(PW, GU)
    verdict("AC-8", "off: validateUser.do refuses the correct password", r.json.get("userFound", {}).get("loginSuccess") is False)
    raw_update(GU, PW_KEY, "true")
    f = flags()
    verdict("AC-8", "UPDATE ... 'true' with no restart: the next lookup offers today's methods (both)", f.get(a) == (True, True), str(f.get(a)))
    r = validate(PW, GU)
    verdict("AC-8", "UPDATE ... 'true': the next validateUser.do accepts the password",
            r.json.get("userFound", {}).get("loginSuccess") is True)
    l = login(PW, a)
    verdict("AC-8", "UPDATE ... 'true': the next login.do signs in", l.status == 302 and "crpDashboard.do" in "".join(l.header("location")),
            ",".join(l.header("location")))
    raw_update(GU, PW_KEY, "false")
    f = flags()
    verdict("AC-8", "UPDATE ... 'false' with no restart: the next lookup is code only again", f.get(a) == (True, False), str(f.get(a)))
    r = validate(PW, GU)
    verdict("AC-8", "UPDATE ... 'false': the next validateUser.do refuses again", r.json.get("userFound", {}).get("loginSuccess") is False)
    pid1 = jvm_pid()
    verdict("AC-8", "no restart: the JVM pid is the same before and after", pid0 == pid1 and pid0 != "", "%s -> %s" % (pid0, pid1))


def stage_ac7():
    need_logs()
    print("\n== ac7: FN-006 / OPS-002 / AC-7 (the code key is blank: the password is the last resort)")
    pid = jvm_pid()
    log_all = LOGS[0]
    text = open(log_all, "rb").read().decode("utf8", "replace")
    unconf = [l for l in text.splitlines() if "auth.otp.keys outcome=unconfigured" in l]
    verdict("AC-7", "the startup WARN says the code key is unconfigured (this application runs with a blank key)", len(unconf) >= 1, "%d line(s)" % len(unconf))
    envs = subprocess.run("ps eww -p %s" % pid, shell=True, capture_output=True, text=True).stdout
    m = re.search(r"(?:^|\s)OTP_HMAC_SECRET=(\S*)", envs)
    print("  (JVM pid %s; OTP_HMAC_SECRET in its environment: %s)" % (pid, "empty" if m and m.group(1) == "" else ("absent" if not m else "set, length not printed")))
    verdict("AC-7", "the JVM environment holds an empty OTP_HMAC_SECRET", bool(m) and m.group(1) == "")
    a1, a2 = acronym(GU), acronym(GU2)
    for unit in (GU, GU2):
        set_value(unit, PW_KEY, "false")
    set_value(GU, OTP_KEY, "true")
    set_value(GU, EXCL_KEY, "")
    before = len([l for l in log_since({log_all: 0}) if "auth.password.last_resort" in l])
    print("  (both units: password switch false; auth.password.last_resort lines already in the log: %d)" % before)
    pos = log_pos()
    f = flags()
    print("  (crpByEmail otpEnabled/passwordEnabled per unit: %s)" % {k: v for k, v in f.items()})
    verdict("AC-7", "both units offer the password and no code, though the switch is off", f.get(a1) == (False, True) and f.get(a2) == (False, True), str(f))
    reset_otp()
    mails0 = os.path.getsize(MBOX) if MBOX and os.path.exists(MBOX) else 0
    rc = http_("POST", "/otpRequestCode.do", {"email": EMAIL, "globalUnitId": GU, "agree": "true"}, fresh_session())
    verdict("AC-7", "request-code issues nothing (domainNotEnabled), no mail, no challenge row",
            rc.json.get("status") == "domainNotEnabled" and (not MBOX or os.path.getsize(MBOX) == mails0)
            and sql("SELECT COUNT(*) FROM otp_challenges;")[0][0] == "0", str(rc.json.get("status")))
    for unit in (GU, GU2):
        r = validate(PW, unit)
        verdict("AC-7", "validateUser.do accepts the correct password in unit %s (today's rules)" % unit, r.json.get("userFound", {}).get("loginSuccess") is True)
    l = login(PW, a1)
    verdict("AC-7", "login.do signs in with the password", l.status == 302 and "crpDashboard.do" in "".join(l.header("location")), ",".join(l.header("location")))
    wrong = validate(WRONG, GU)
    verdict("AC-7", "a wrong password is still refused by the credential check, not by the switch",
            wrong.json.get("userFound", {}).get("loginSuccess") is False)
    for _ in range(4):
        flags()
        http_("POST", "/crpByEmail.do", {"userEmail": "nobody.%d@outlook.com" % os.getpid()})
    lines = [l for l in log_since({log_all: 0}) if "auth.password.last_resort" in l]
    new = [l for l in log_since(pos) if "auth.password.last_resort" in l]
    mine1 = [l for l in lines if "gu=%s " % a1 in l]
    mine2 = [l for l in lines if "gu=%s " % a2 in l]
    print("  (last_resort lines: whole log %d, since this stage began %d)" % (len(lines), len(new)))
    for l in lines:
        print("    " + re.sub(r"^\S+ - \[[^\]]*\] ", "", l)[:230])
    verdict("OPS-002", "exactly one ERROR line per unit however many lookups: %s once, %s once" % (a1, a2),
            len(mine1) == 1 and len(mine2) == 1 and all("[ERROR]" in l for l in mine1 + mine2), "%d + %d" % (len(mine1), len(mine2)))
    verdict("OPS-002", "the line names the reason and the unit and nothing else (no address, no password)",
            all("reason=code_key_unconfigured" in l and EMAIL not in l and PW not in l for l in lines))
    verdict("OPS-002", "lookups for an unknown address write no further line", len(lines) == 2, "%d total" % len(lines))


def stage_ac12():
    print("\n== ac12: SEC-006 / AC-12 (authentication outside the login page is unchanged with the switch off)")
    for unit in (GU, GU2):
        set_value(unit, PW_KEY, "false")
    print("  (password switch false in units %s and %s)" % (GU, GU2))
    # CLARISA technical account: the filter logs it in on /swagger/index.html
    r = http_("GET", "/swagger/index.html")
    print("  GET /swagger/index.html -> HTTP %d, session cookie %s" % (r.status, "issued" if r.cookie else "none"))
    if r.cookie and JDWP and JCP:
        attrs = sessdump(r.cookie)
        authed = attrs.get("org.apache.shiro.subject.support.DefaultSubjectContext_AUTHENTICATED_SESSION_KEY")
        public = attrs.get("clarisa_public") or attrs.get("CLARISA_PUBLIC") or next((v for k, v in attrs.items() if "clarisa" in k.lower()), None)
        print("  (session read from the JVM: authenticated=%s, clarisa attribute=%s)" % (authed, public))
        verdict("AC-12", "the CLARISA public user is authenticated through ClarisaPublicAccesFilter (session read from the JVM)",
                authed == "true" and public == "true", "authenticated=%s clarisa=%s" % (authed, public))
    else:
        notrun("AC-12", "CLARISA session authenticated", "CLARISA session: NOT RUN (needs PROBE_JDWP and PROBE_JAVA_CP): HTTP %d only" % r.status)
        verdict("AC-12", "the CLARISA page answers", r.status == 200)
    # /api/** HTTP Basic: the fixture account's own password, and a wrong one, with the switch off and then on
    import base64

    def basic(pw):
        tok = base64.b64encode(("%s:%s" % (EMAIL, pw)).encode()).decode()
        return http_("GET", "/api/%s/ExpenditureExample/1" % acronym(GU), extra={"Authorization": "Basic " + tok})

    def api_round(label):
        pos = log_pos()
        good, bad = basic(PW), basic(WRONG)
        lines = log_since(pos)
        exc = [l for l in lines if l.startswith("org.hibernate.HibernateException")]
        gate = [l for l in lines if GATE in l]
        print("  /api/** Basic, switch %s: correct password HTTP %d, wrong password HTTP %d; HibernateException lines %d; password-switch lines %d"
              % (label, good.status, bad.status, len(exc), len(gate)))
        if exc:
            print("    " + exc[0][:150])
        return good.status, bad.status, len(exc), len(gate)
    off = api_round("off")
    for unit in (GU, GU2):
        set_value(unit, PW_KEY, None)
    on = api_round("on")
    verdict("AC-12", "/api/** HTTP Basic answers the same with the switch off and on (status, error), and never reaches the password gate",
            off[:3] == on[:3] and off[3] == 0 and on[3] == 0, "off=%s on=%s" % (off, on))
    if off[0] != 401:
        verdict("AC-12", "/api/** HTTP Basic with a correct stored password authenticates (not 401) with the switch off", True, "HTTP %d" % off[0])
    else:
        notrun("AC-12", "/api/** Basic authenticates", "/api/** Basic authenticates: NOT VERIFIED. The correct password is refused with 401 on this stack also "
               "with the switch ON: the Shiro filter reaches APCustomRealm -> UserManagerImp.getUserByEmail with no Hibernate session "
               "(HibernateException 'Could not obtain transaction-synchronized Session'), code this spec does not touch")



def stage_override():
    need_logs()
    print("\n== override: FN-004 / FN-005 / SEC-004 / AC-3..AC-6 (the code is the default method while the password is off)")
    a = acronym(GU)
    set_value(GU, PW_KEY, "false")
    set_value(GU2, PW_KEY, None)
    set_value(GU, OTP_KEY, "false")
    set_value(GU, EXCL_KEY, "")
    cases = []
    # AC-4: password off, everything else off: the code switch is off, nothing excluded
    reset_otp()
    n0 = len(mails_for(EMAIL)); pos = log_pos()
    r = request_code(EMAIL, GU)
    code = code_of(EMAIL, n0)
    sent = [l for l in log_since(pos) if "auth.otp.request outcome=sent" in l]
    verdict("AC-4", "password off, code switch OFF: request-code issues a code (sent, one mail)", r.json.get("status") == "sent" and code is not None,
            str(r.json.get("status")))
    verdict("AC-4", "the request line ends with via=password_off", len(sent) == 1 and sent[0].rstrip().endswith("via=password_off") and ("gu=" + a.lower()) in sent[0].lower(),
            re.sub(r"^.* - \[[^\]]*\] ", "", sent[0])[-120:] if sent else "no line")
    f = flags()
    verdict("AC-4", "crpByEmail: code offered, password not (otpEnabled true, passwordEnabled false) although the code switch is off", f.get(a) == (True, False), str(f.get(a)))
    v = http_("POST", "/otpVerifyCode.do", {"code": code}, r.cookie)
    verdict("AC-4", "the code signs the member in (otpVerifyCode answers ok)", v.json.get("status") == "ok", str(v.json.get("status")))
    # AC-5: password off, code switch on, the domain of the account excluded
    set_value(GU, OTP_KEY, "true")
    set_value(GU, EXCL_KEY, "outlook.com")
    reset_otp()
    n0 = len(mails_for(EMAIL))
    r = request_code(EMAIL, GU)
    code = code_of(EMAIL, n0)
    verdict("AC-5", "password off, domain excluded: request-code still issues a code", r.json.get("status") == "sent" and code is not None, str(r.json.get("status")))
    f = flags()
    verdict("AC-5", "crpByEmail: code only for the excluded domain", f.get(a) == (True, False), str(f.get(a)))
    # the same account with the password back on and the domain still excluded: today's rule (password only)
    set_value(GU, PW_KEY, None)
    f = flags()
    verdict("AC-5", "BUT with the password back on, the excluded domain is password only again (otpEnabled false, passwordEnabled true)",
            f.get(a) == (False, True), str(f.get(a)))
    reset_otp()
    r = request_code(EMAIL, GU)
    verdict("AC-5", "and request-code answers domainNotEnabled again", r.json.get("status") == "domainNotEnabled", str(r.json.get("status")))
    # AC-6: a Cognito account is not touched by the password switch
    set_value(GU, PW_KEY, "false")
    cg = "otp.cgiar@outlook.com"
    f = flags(cg)
    print("  (Cognito account %s: crpByEmail per unit %s)" % (cg, f))
    verdict("AC-6", "Cognito account: the password switch changes nothing in crpByEmail (code not forced, password not offered)", f.get(a) == (False, False), str(f.get(a)))
    reset_otp()
    n0 = len(mails_for(cg)); pos = log_pos()
    r = request_code(cg, GU)
    time.sleep(2)
    sent = [l for l in log_since(pos) if "auth.otp.request outcome=sent" in l and "via=password_off" in l]
    verdict("AC-6", "a direct request-code for the Cognito account answers the neutral 'sent' but sends no mail and logs no via=password_off line",
            r.json.get("status") == "sent" and len(mails_for(cg)) == n0 and len(sent) == 0, "%s, %d new mail(s)" % (r.json.get("status"), len(mails_for(cg)) - n0))
    set_value(GU, EXCL_KEY, "")
    set_value(GU, OTP_KEY, "true")


def stage_runbook_sql():
    print("\n== runbook-sql: the statements of runbook.md, as written (OPS-003)")
    here = os.path.dirname(os.path.abspath(sys.argv[0])) if sys.argv and sys.argv[0] else "."
    path = os.environ.get("PROBE_RUNBOOK", os.path.join(here, "..", "runbook.md"))
    text = open(path, encoding="utf-8").read()

    def block(tag):
        m = re.search(r"```sql\n-- runbook:%s\n(.*?)```" % tag, text, re.S)
        return m.group(1) if m else None
    count, turn_on = block("count"), block("turn-on")
    verdict("OPS-003", "the runbook holds both statements", bool(count) and bool(turn_on))
    a = acronym(GU)
    rows = sql(count.replace("<unit id>", GU))
    print("  count for unit %s: active_members, local_accounts, cgiar_accounts = %s" % (GU, rows[0] if rows else None))
    verdict("OPS-003", "the count statement runs and returns three numbers", bool(rows) and len(rows[0]) == 3 and all(x.isdigit() for x in rows[0]), str(rows))
    set_value(GU, PW_KEY, "false")
    off = flags().get(a)
    out = sql(turn_on.replace("<unit id>", GU) + "\nSELECT ROW_COUNT();")
    n = sql("SELECT value FROM custom_parameters WHERE parameter_id = %s AND global_unit_id = %s;" % (param_id(GU, PW_KEY), GU))
    on = flags().get(a)
    print("  before: passwordEnabled=%s; after the UPDATE of the runbook: value=%s, passwordEnabled=%s" % (off[1] if off else None, n, on[1] if on else None))
    verdict("OPS-003", "the turn-on statement affects exactly one row (ROW_COUNT)", out == [["1"]], str(out))
    verdict("OPS-003", "the turn-on statement, as written, flips the unit to on on the next lookup, with no restart",
            off is not None and off[1] is False and on is not None and on[1] is True)
    r = validate(PW, GU)
    verdict("OPS-003", "and the password is accepted again", r.json.get("userFound", {}).get("loginSuccess") is True)


def stage_walk():
    print("\n== walk: the advisory items of the T05 pointer")
    a1, a2 = acronym(GU), acronym(GU2)
    set_value(GU, PW_KEY, "false")
    set_value(GU2, PW_KEY, None)
    # (4) an already authenticated session posts the form for a switch-off unit
    ok = validate(PW, GU2)
    cookie = ok.cookie
    print("  (4) a session signed in through unit %s (switch on): loginSuccess=%s" % (a2, ok.json.get("userFound", {}).get("loginSuccess")))
    again = validate(PW, GU, cookie=cookie)
    other = validate(PW, GU, cookie=fresh_session())
    verdict("walk-4", "the authenticated session that posts the correct password for the switch-off unit gets the refusal any caller gets",
            again.json.get("userFound", {}).get("loginSuccess") is False and normal_json(again) == normal_json(other), normal_json(again))
    # (1) and (5): a CGIAR account against a refusal, and the accepted residual
    cg = "probe.cgiar@cgiar.org"
    set_value(GU2, PW_KEY, None)
    pos = log_pos()
    on = validate(WRONG, GU2, email=cg)
    ldap = [l for l in log_since(pos) if "UnknownHostException" in l]
    set_value(GU2, PW_KEY, "false")
    off = validate(WRONG, GU2, email=cg)
    set_value(GU2, PW_KEY, None)
    print("  (1)(5) CGIAR account (is_cgiar_user=1) on unit %s without Cognito, a wrong password, Active Directory unreachable offline:" % a2)
    print("      switch on : %s   (directory lookup threw UnknownHostException %d time(s))" % (json.dumps(on.json.get("messageEror")), len(ldap)))
    print("      switch off: %s" % json.dumps(off.json.get("messageEror")))
    notrun("walk-1", "a wrong CGIAR password against a refusal gives the text a real directory failure gives",
           "(1) NOT REACHABLE offline: the directory does not answer, so the text of a real AD 'wrong password' cannot be produced. What is "
           "observed is the accepted residual (requirements Decision Log, T05): the refusal says the CGIAR wrong-password text while an "
           "unreachable directory leaves no message at all")


STAGES = {"override": stage_override, "runbook-sql": stage_runbook_sql, "all-on": stage_all_on, "ac10": stage_ac10, "ac8": stage_ac8, "ac7": stage_ac7, "ac12": stage_ac12, "walk": stage_walk}
for s in sys.argv[1:] or ["--help"]:
    if s not in STAGES:
        raise SystemExit("unknown stage %r; stages: %s" % (s, ", ".join(STAGES)))
    STAGES[s]()
failed = [(ac, n) for ac, n, ok in verdicts if not ok]
print("\nSUMMARY: %d checks, %d failed%s%s" % (len(verdicts), len(failed), (": " + "; ".join("[%s] %s" % f for f in failed)) if failed else "",
      ("; NOT RUN: " + "; ".join("[%s] %s" % f for f in not_run)) if not_run else ""))
sys.exit(1 if failed else 0)
PY
