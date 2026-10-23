# Suite 2 FN-009 / AC-13 live probe (non-author): create an external user through the CRP admin users screen
# (CrpUsersAction.save -> sendMailNewUser) with unit 45's password switch OFF, then a control with it ON.
# Refuses unless the database is a probe copy. Prints no password, no session id.
import email, hashlib, html, http.client, json, os, re, subprocess, sys, time, urllib.parse

MYSQL = os.environ["PROBE_MYSQL"]
MBOX = os.environ["PROBE_MBOX"]
ADMIN = "fn009.admin@outlook.com"
PW = os.environ["PROBE_PW_PASS"]
HOST, PORT, PREFIX = "localhost", 8080, "/marlo-web"
GU = "45"
TAG = str(os.getpid())
verdicts = []


def sql(q):
    out = subprocess.run(MYSQL, shell=True, input=q, capture_output=True, text=True)
    if out.returncode != 0:
        raise SystemExit("mysql failed: " + out.stderr.strip()[:300])
    return [l.split("\t") for l in out.stdout.strip().splitlines() if l]


assert "probe" in sql("SELECT DATABASE();")[0][0], "not a probe copy"


def verdict(ac, name, ok, detail=""):
    verdicts.append(ok)
    print("  => %s [%s] %s%s" % ("PASS" if ok else "FAIL", ac, name, ("   (%s)" % detail) if detail != "" else ""))


def http_(method, path, data=None, cookie=None):
    c = http.client.HTTPConnection(HOST, PORT, timeout=90)
    h = {}
    body = None
    if data is not None:
        body = urllib.parse.urlencode(data)
        h["Content-Type"] = "application/x-www-form-urlencoded"
    if cookie:
        h["Cookie"] = "JSESSIONID=" + cookie
    c.request(method, PREFIX + path, body, h)
    r = c.getresponse()
    b = r.read().decode("utf8", "replace")
    hdrs = r.getheaders()
    c.close()
    sc = "\n".join(v for k, v in hdrs if k.lower() == "set-cookie")
    sids = [m for m in re.findall(r"JSESSIONID=([^;]+)", sc) if m != "deleteMe"]
    loc = [v for k, v in hdrs if k.lower() == "location"]
    return r.status, b, (sids[-1] if sids else None), loc


def set_pw(value):
    pid = sql("SELECT p.id FROM parameters p JOIN global_units g ON g.global_unit_type_id = p.global_unit_type_id "
              "WHERE g.id = %s AND p.`key` = 'system_password_login_active';" % GU)[0][0]
    sql("DELETE FROM custom_parameters WHERE parameter_id = %s AND global_unit_id = %s;" % (pid, GU))
    sql("INSERT INTO custom_parameters (parameter_id, value, is_active, global_unit_id) VALUES (%s, '%s', 1, %s);" % (pid, value, GU))


def chunks():
    raw = open(MBOX, "rb").read() if os.path.exists(MBOX) else b""
    return re.split(rb"(?m)^From sink ", raw)[1:]


def parts(chunk):
    msg = email.message_from_bytes(chunk.split(b"\n", 1)[1])
    out = {"subject": str(email.header.make_header(email.header.decode_header(msg.get("Subject", ""))))}
    for p in msg.walk():
        if p.get_content_type().startswith("text/"):
            out[p.get_content_type()] = p.get_payload(decode=True).decode("utf8", "replace")
    return out


def text_of_html(h):
    t = re.sub(r"(?s)<(script|style).*?</\1>", " ", h)
    t = re.sub(r"<br\s*/?>|</p>|</tr>|</td>", " | ", t)
    t = html.unescape(re.sub(r"<[^>]+>", " ", t))
    return re.sub(r"\s+", " ", t).strip()


def code_login():
    """Sign the admin fixture in by an emailed code (the only method while unit 45's password is off)."""
    sql("DELETE FROM otp_challenges; DELETE FROM otp_rate_limits; DELETE FROM otp_cooldowns;")
    _, _, sid, _ = http_("GET", "/login.do")
    n0 = len(chunks())
    st, b, rsid, _ = http_("POST", "/otpRequestCode.do", {"email": ADMIN, "globalUnitId": GU, "agree": "true"}, sid)
    sid = rsid or sid
    code = None
    for _ in range(80):
        cs = chunks()
        if len(cs) > n0:
            m = re.search(r"Your code is: (\d{6})", parts(cs[-1]).get("text/plain", ""))
            if m:
                code = m.group(1)
                break
        time.sleep(0.25)
    st2, b2, sid2, _ = http_("POST", "/otpVerifyCode.do", {"code": code or ""}, sid)
    ok = json.loads(b2).get("status") == "ok" if b2.strip().startswith("{") else False
    return (sid2 or sid), ok


def password_login():
    _, _, sid, _ = http_("GET", "/login.do")
    st, b, sid2, loc = http_("POST", "/login.do", {"user.email": ADMIN, "user.password": PW, "crp": "AICCRA"}, sid)
    return (sid2 or sid), st == 302 and "crpDashboard.do" in "".join(loc)


def create(sid, addr, first, last):
    st0, page, _, _ = http_("GET", "/admin/AICCRA/crpUsers.do", None, sid)
    n0 = len(chunks())
    st, b, _, loc = http_("POST", "/admin/AICCRA/crpUsers.do",
                          {"user.email": addr, "user.firstName": first, "user.lastName": last, "selectedGlobalUnitID": GU,
                           "emailSend": "true", "save": ""}, sid)
    mails = []
    for _ in range(40):
        mails = [parts(c) for c in chunks()[n0:]]
        if any(addr in (m.get("text/html", "") + m.get("text/plain", "")) for m in mails):
            break
        time.sleep(0.5)
    mine = [m for m in mails if addr in (m.get("text/html", "") + m.get("text/plain", "")) and "text/html" in m]
    return st0, st, loc, mine


def run(label, switch, addr, login):
    print("\n== %s: unit 45 password switch %s" % (label, switch))
    set_pw(switch)
    sid, ok = login()
    verdict("setup", "the admin fixture (temporary CRP-Admin of unit 45 in the copy) is signed in by %s" % (
        "an emailed code" if login is code_login else "password"), ok)
    st0, st, loc, mine = create(sid, addr, "Fn009", label.replace(" ", ""))
    print("  GET /AICCRA/crpUsers.do -> HTTP %d; POST save -> HTTP %d %s" % (st0, st, ",".join(loc)))
    row = sql("SELECT id, IFNULL(password,'<NULL>'), is_cgiar_user, is_active FROM users WHERE email = '%s';" % addr)
    verdict("FN-009", "the account was created (users row)", len(row) == 1, "id %s" % (row[0][0] if row else "-"))
    if not row:
        return None
    uid, pw, cg, act = row[0]
    mem = sql("SELECT COUNT(*) FROM crp_users WHERE user_id = %s AND global_unit_id = %s AND is_active = 1;" % (uid, GU))[0][0]
    print("  users.id=%s is_cgiar_user=%s is_active=%s crp_users(45, active)=%s; stored password: %s" % (
        uid, cg, act, mem, "NULL" if pw == "<NULL>" else ("empty" if pw == "" else "%d chars, %s" % (
            len(pw), "32-hex (MD5 form)" if re.fullmatch(r"[0-9a-f]{32}", pw) else "not MD5 form"))))
    verdict("FN-009", "a password is stored: non-null, non-empty, in MARLO's MD5 form", pw not in ("<NULL>", "") and bool(re.fullmatch(r"[0-9a-f]{32}", pw)))
    verdict("FN-009", "the account is external (is_cgiar_user = 0), so the switch applies to it", cg == "0")
    verdict("AC-13", "exactly one welcome email for the new address reached the sink", len(mine) == 1, "%d" % len(mine))
    if len(mine) != 1:
        return uid, pw, None
    t = text_of_html(mine[0]["text/html"])
    i = t.find(addr)
    excerpt = t[max(0, i - 40): i + 420] if i >= 0 else t[:460]
    print("  subject: %s" % mine[0]["subject"])
    print("  body excerpt (rendered text, 6-digit numbers masked): %s" % re.sub(r"(?<!\d)\d{6}(?!\d)", "<6 digits>", excerpt))
    return uid, pw, t


def pw_row(t):
    return re.search(r"Password\s*:?\s*\|?\s*(\d{6})\b", t)


# ---- switch OFF ---------------------------------------------------------------------------------------------------------
off_addr = "fn009.off.%s@outlook.com" % TAG
r = run("switch off", "false", off_addr, code_login)
if r and r[2]:
    t = r[2]
    verdict("AC-13", "switch off: the email carries the code-sign-in copy (How to sign in / Email me a sign-in code / valid for 5 minutes)",
            "How to sign in" in t and "Email me a sign-in code" in t and "valid for 5 minutes" in t)
    six = re.findall(r"(?<![\d])\d{6}(?![\d])", t)
    hit = [s for s in six if hashlib.md5(s.encode()).hexdigest() == r[1]]
    verdict("AC-13", "switch off: the email does NOT carry the stored password (no 6-digit number in it hashes to the stored value)",
            len(hit) == 0 and not pw_row(t), "%d six-digit number(s) in the body, %d match the stored hash" % (len(six), len(hit)))
    verdict("AC-13", "switch off: no 'Password' row in the sign-in block", not re.search(r"\bPassword\s*:", t))

# ---- control: switch ON -------------------------------------------------------------------------------------------------
on_addr = "fn009.on.%s@outlook.com" % TAG
r2 = run("control switch on", "true", on_addr, password_login)
if r2 and r2[2]:
    t = r2[2]
    six = re.findall(r"(?<![\d])\d{6}(?![\d])", t)
    hit = [s for s in six if hashlib.md5(s.encode()).hexdigest() == r2[1]]
    verdict("AC-13", "control (switch on): today's email, with the password, and that password is the stored credential (MD5 match)",
            len(hit) == 1 and "Email me a sign-in code" not in t, "%d match" % len(hit))
set_pw("false")
print("\n  (unit 45 password switch left false; fixture users created: %s, %s)" % (off_addr, on_addr))
print("\nSUMMARY: %d checks, %d failed" % (len(verdicts), verdicts.count(False)))
sys.exit(0 if all(verdicts) else 1)
