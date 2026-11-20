#!/usr/bin/env python3
"""CHG-PARTREQ-001-T04 live probe: atomicity (DA-001) and locking (FN-005) of the partner request decisions.

Drives the RUNNING application on the throwaway database with real HTTP requests and reads the database state before
and after through separate connections. Usage:

    PROBE_MYSQL='<command that opens a mysql client on the throwaway database>' \
      python3 -I t04-probe.py <case> <label> [gate]

Cases
  accept-once   one POST to /superadmin/addPartner.do for a fresh pending request, then the counts (case (a), and the
                single-request control)
  accept-race   two POSTs for the same pending request, released together from a barrier, each on its own session
                (cases (b) and the falsifier). `gate` first holds the row lock from a third connection, so that both
                requests are provably blocked inside the manager when the lock is released.
  accept-order  one POST while a second connection polls the institution row every ~1 ms (case (c))
  office-batch  two pending OFFICE requests of one institution for the same country in one batch through
                /acceptOfficesRequest.do (forward pointer of T01 / T06)

Safety: refuses to run unless the connected database name contains "partreq_test". The login is read from the file named
by PROBE_CREDENTIALS (default ~/.marlo-local-credentials) and is never printed. The general log of the MySQL server is
switched on only for the duration of the requests (its default is OFF) and switched off again before the report.
"""
import http.client
import os
import re
import subprocess
import sys
import threading
import time
import urllib.parse

BASE = os.environ.get("PROBE_BASE", "http://localhost:8080/marlo-web")
MYSQL = os.environ["PROBE_MYSQL"]
PHASE = os.environ.get("PROBE_PHASE", "403")
GU = os.environ.get("PROBE_GU", "45")
REQUESTER = os.environ.get("PROBE_REQUESTER", "1082")  # the local superadmin, so no real person is mailed
u = urllib.parse.urlparse(BASE)
HOST, PORT, PREFIX = u.hostname, u.port or 80, u.path.rstrip("/")


def sql(q, *opts):
    out = subprocess.run(MYSQL + " -N " + " ".join(opts), shell=True, input=q, capture_output=True, text=True)
    if out.returncode != 0:
        raise SystemExit("mysql failed: " + out.stderr.strip()[:400])
    return [line.split("\t") for line in out.stdout.strip().splitlines() if line]


def one(q):
    return sql(q)[0][0]


DB = one("SELECT DATABASE();")
if "partreq_test" not in DB:
    raise SystemExit("refusing to run: database %r is not the throwaway copy" % DB)


def credentials():
    path = os.path.expanduser(os.environ.get("PROBE_CREDENTIALS", "~/.marlo-local-credentials"))
    values = {}
    for line in open(path):
        if "=" in line and not line.startswith("#"):
            k, v = line.strip().split("=", 1)
            values[k] = v
    return values["MARLO_LOCAL_USER"], values["MARLO_LOCAL_PASSWORD"]


def http_(method, path, data=None, cookie=None):
    c = http.client.HTTPConnection(HOST, PORT, timeout=120)
    headers = {}
    body = None
    if data is not None:
        body = urllib.parse.urlencode(data)
        headers["Content-Type"] = "application/x-www-form-urlencoded"
    if cookie:
        headers["Cookie"] = cookie
    c.request(method, PREFIX + path, body, headers)
    r = c.getresponse()
    raw = r.read().decode("utf8", "replace")
    hdrs = r.getheaders()
    c.close()
    return r.status, raw, hdrs


def live_cookie(hdrs):
    # Shiro answers JSESSIONID=deleteMe for the stopped session, then the live one: keep the last live value.
    live = [m for m in re.findall(r"JSESSIONID=([^;]+)", "\n".join(v for k, v in hdrs if k.lower() == "set-cookie"))
            if m != "deleteMe"]
    return "JSESSIONID=" + live[-1] if live else None


def login():
    """A fresh session: POST the credentials to login.do (GET login.do sets no cookie), keep the live JSESSIONID."""
    user, pw = credentials()
    st, _, hdrs = http_("POST", "/login.do", {"user.email": user, "user.password": pw, "crp": "AICCRA"})
    cookie = live_cookie(hdrs)
    if cookie is None:
        raise SystemExit("login gave no session cookie (HTTP %s)" % st)
    return cookie


def counts():
    return {
        "institutions": int(one("SELECT COUNT(*) FROM institutions;")),
        "institutions_locations": int(one("SELECT COUNT(*) FROM institutions_locations;")),
        "partner_requests": int(one("SELECT COUNT(*) FROM partner_requests;")),
        "pending_partner_copies": int(one(
            "SELECT COUNT(*) FROM partner_requests WHERE is_active=1 AND acepted IS NULL AND is_office=0 "
            "AND partner_request_id IS NOT NULL;")),
    }


def show(title, d):
    print("  %-28s %s" % (title, ", ".join("%s=%s" % kv for kv in d.items())))


def new_pair(label):
    """Original + copy, both pending, active, with a type and a country (accept must not throw)."""
    cols = ("partner_name, acronym, institution_type_id, loc_element_id, web_page, is_office, request_source, "
            "is_active, active_since, created_by, global_unit_id, id_phase")
    vals = "'%s', 'T04', 31, 78, 'https://t04.example', 0, 'T04 probe %s', 1, NOW(), %s, %s, %s" % (
        label, label, REQUESTER, GU, PHASE)
    sql("INSERT INTO partner_requests (%s) VALUES (%s);" % (cols, vals))
    original = int(one("SELECT MAX(id) FROM partner_requests;"))
    sql("INSERT INTO partner_requests (%s, partner_request_id, is_modified) VALUES (%s, %d, 0);" % (cols, vals, original))
    copy = int(one("SELECT MAX(id) FROM partner_requests;"))
    return original, copy


def request_state(ids):
    rows = sql("SELECT id, is_active, IFNULL(acepted,'NULL'), IFNULL(institution_id,'NULL'), is_office FROM partner_requests "
               "WHERE id IN (%s) ORDER BY id;" % ",".join(str(i) for i in ids))
    return "; ".join("req %s active=%s acepted=%s institution=%s office=%s" % tuple(r) for r in rows)


def institutions_named(label):
    rows = sql("SELECT i.id, i.name, (SELECT COUNT(*) FROM institutions_locations l WHERE l.institution_id=i.id) "
               "FROM institutions i WHERE i.name='%s' ORDER BY i.id;" % label)
    return rows


def ns():
    return time.time_ns()


def ms(t0, t):
    return "%.1f" % ((t - t0) / 1e6)


def general_log(on):
    sql("SET GLOBAL general_log = '%s';" % ("ON" if on else "OFF"))


def dump_sql_log(since, ids, title):
    """The statements of the connections that touched the request rows, from mysql.general_log (log_output=TABLE)."""
    pat = "|".join(str(i) for i in ids)
    threads = [r[0] for r in sql(
        "SELECT DISTINCT thread_id FROM mysql.general_log WHERE event_time >= '%s' AND command_type IN ('Query','Execute') "
        "AND CONVERT(argument USING utf8mb4) REGEXP 'partner_requests' AND CONVERT(argument USING utf8mb4) REGEXP "
        "'(where|WHERE).*(id *= *(%s)|id=(%s))' AND CONVERT(argument USING utf8mb4) LIKE '%%for update%%';" % (since, pat, pat))]
    print("  -- %s: connections that ran a FOR UPDATE on the request row(s): %s" % (title, ",".join(threads) or "NONE"))
    if not threads:
        return
    rows = sql(
        "SELECT DATE_FORMAT(event_time,'%%H:%%i:%%s.%%f'), thread_id, "
        "REPLACE(REPLACE(IF(LENGTH(argument)>230,CONCAT(LEFT(CONVERT(argument USING utf8mb4),70),' ... ',RIGHT(CONVERT(argument USING utf8mb4),130)),CONVERT(argument USING utf8mb4)),'\\n',' '),'\\t',' ') FROM mysql.general_log "
        "WHERE event_time >= '%s' AND thread_id IN (%s) AND command_type IN ('Query','Execute') AND ("
        "CONVERT(argument USING utf8mb4) REGEXP '(partner_requests|institutions|institutions_locations)' "
        "OR LOWER(CONVERT(argument USING utf8mb4)) IN ('commit','rollback','begin','set autocommit=0','set autocommit=1')) "
        "ORDER BY event_time;" % (since, ",".join(threads)))
    for t, th, arg in rows:
        print("  %s  thr %-6s %s" % (t, th, arg))


def gate_holder(copy):
    """A third connection that holds the row lock until told to commit."""
    p = subprocess.Popen(MYSQL + " -N -n", shell=True, stdin=subprocess.PIPE, stdout=subprocess.PIPE, text=True, bufsize=1)
    p.stdin.write("BEGIN; SELECT 'held', id FROM partner_requests WHERE id=%d FOR UPDATE;\n" % copy)
    p.stdin.flush()
    print("  gate: %s" % p.stdout.readline().strip())
    return p


def waiting_sessions():
    return int(one("SELECT COUNT(DISTINCT REQUESTING_ENGINE_TRANSACTION_ID) FROM performance_schema.data_lock_waits;"))


def post_accept(cookie, copy, out, key, barrier):
    barrier.wait()
    t_start = ns()
    st, raw, hdrs = http_("POST", "/superadmin/addPartner.do", {"requestID": copy, "edit": "true", "phaseID": PHASE}, cookie)
    t_end = ns()
    loc = [v for k, v in hdrs if k.lower() == "location"]
    out[key] = (t_start, t_end, st, loc[0] if loc else "")


def case_accept_once(label):
    original, copy = new_pair(label)
    print("== accept-once  label=%s  original=%d copy=%d  db=%s" % (label, original, copy, DB))
    before = counts()
    show("counts before", before)
    print("  requests before:", request_state([original, copy]))
    cookie = login()
    since = one("SELECT DATE_FORMAT(NOW(6),'%Y-%m-%d %H:%i:%s.%f');")
    general_log(True)
    out = {}
    t0 = ns()
    post_accept(cookie, copy, out, "A", threading.Barrier(1))
    general_log(False)
    show("counts after", counts())
    print("  requests after:", request_state([original, copy]))
    s, e, st, loc = out["A"]
    print("  request A: HTTP %s %s ms  Location=%s" % (st, ms(s, e), loc))
    print("  institutions named %r: %s" % (label, institutions_named(label)))
    dump_sql_log(since, [copy], "accept-once")


def case_accept_race(label, gate):
    original, copy = new_pair(label)
    print("== accept-race  label=%s gate=%s original=%d copy=%d  db=%s" % (label, gate, original, copy, DB))
    before = counts()
    show("counts before", before)
    print("  requests before:", request_state([original, copy]))
    cookies = [login(), login()]
    since = one("SELECT DATE_FORMAT(NOW(6),'%Y-%m-%d %H:%i:%s.%f');")
    general_log(True)
    holder = gate_holder(copy) if gate else None
    out = {}
    barrier = threading.Barrier(2)
    threads = [threading.Thread(target=post_accept, args=(cookies[i], copy, out, "AB"[i], barrier)) for i in range(2)]
    for t in threads:
        t.start()
    if holder:
        # both requests run prepare() (loads every request into the session) and then block on the row lock
        deadline = time.time() + 60
        seen = 0
        while time.time() < deadline:
            seen = waiting_sessions()
            if seen >= 2:
                break
            time.sleep(0.05)
        print("  gate: sessions waiting on the row lock when released: %d (performance_schema.data_lock_waits)" % seen)
        holder.stdin.write("COMMIT;\n")
        holder.stdin.flush()
        holder.stdin.close()
        holder.wait(timeout=30)
    for t in threads:
        t.join()
    general_log(False)
    t0 = min(v[0] for v in out.values())
    for k in "AB":
        s, e, st, loc = out[k]
        print("  request %s: start +%s ms  end +%s ms  (%s ms)  HTTP %s  Location=%s" % (
            k, ms(t0, s), ms(t0, e), ms(s, e), st, loc))
    a, b = out["A"], out["B"]
    overlap = max(a[0], b[0]) < min(a[1], b[1])
    print("  OVERLAP: each request started before the other ended: %s (both started within %s ms of each other)" % (
        overlap, ms(0, abs(a[0] - b[0])).strip()))
    show("counts after", counts())
    print("  requests after:", request_state([original, copy]))
    named = institutions_named(label)
    print("  institutions named %r: %d  %s" % (label, len(named), named))
    dump_sql_log(since, [copy], "accept-race")


def case_accept_order(label):
    original, copy = new_pair(label)
    print("== accept-order label=%s original=%d copy=%d  db=%s" % (label, original, copy, DB))
    before = counts()
    show("counts before", before)
    cookie = login()
    poller = subprocess.Popen(MYSQL + " -N -n", shell=True, stdin=subprocess.PIPE, stdout=subprocess.PIPE, text=True, bufsize=1)
    samples = []
    stop = threading.Event()
    q = "SELECT (SELECT COUNT(*) FROM institutions WHERE name='%s'), (SELECT COUNT(*) FROM institutions_locations l " \
        "JOIN institutions i ON i.id=l.institution_id WHERE i.name='%s'), (SELECT IFNULL(acepted,'NULL') FROM " \
        "partner_requests WHERE id=%d), DATE_FORMAT(NOW(6),'%%H:%%i:%%s.%%f');\n" % (label, label, copy)

    def poll():
        while not stop.is_set():
            t = ns()
            poller.stdin.write(q)
            poller.stdin.flush()
            samples.append((t, poller.stdout.readline().strip().split("\t")))
            time.sleep(0.001)

    since = one("SELECT DATE_FORMAT(NOW(6),'%Y-%m-%d %H:%i:%s.%f');")
    general_log(True)
    pt = threading.Thread(target=poll)
    pt.start()
    time.sleep(0.2)
    out = {}
    post_accept(cookie, copy, out, "A", threading.Barrier(1))
    time.sleep(0.5)
    stop.set()
    pt.join()
    poller.stdin.close()
    general_log(False)
    s, e, st, loc = out["A"]
    t0 = s
    first_seen = next((t for t, r in samples if len(r) >= 3 and r[0] == "1"), None)
    last_unseen = max((t for t, r in samples if len(r) >= 3 and r[0] == "0" and t < (first_seen or 1 << 62)), default=None)
    print("  request A: start +0.0 ms  end +%s ms  HTTP %s" % (ms(t0, e), st))
    print("  second connection: %d polls; last poll with NO institution row: +%s ms; first poll WITH the row "
          "(institution=1, its location=%s, request acepted=%s): +%s ms" % (
              len(samples), ms(t0, last_unseen) if last_unseen else "n/a",
              next(r[1] for t, r in samples if t == first_seen) if first_seen else "n/a",
              next(r[2] for t, r in samples if t == first_seen) if first_seen else "n/a",
              ms(t0, first_seen) if first_seen else "NEVER"))
    show("counts after", counts())
    print("  requests after:", request_state([original, copy]))
    # mail: the account is queued by sendMail.send() after the manager returned; the email_logs row follows
    # the (possibly retried) SMTP outcome, read it with `email-log <label>`
    dump_sql_log(since, [copy], "accept-order")
    print("  server clock now: %s ; COMMIT and email_logs rows: run `email-log %s %s`" % (one("SELECT NOW(6);"), label, since))


def case_email_log(label, since):
    """The decision email of a request: its email_logs row, its Message-ID (the epoch milliseconds at which the message
    was built by SendMailS.send, after the manager returned) and the COMMITs of the server since `since`."""
    rows = sql("SELECT id, date_email, subject, to_email, source_action, succes_email, message_id FROM email_logs WHERE "
               "message LIKE '%%%s%%' ORDER BY id;" % label)
    print("== email-log label=%s: %d row(s)" % (label, len(rows)))
    for r in rows:
        built = re.search(r"<\d+\.\d+\.(\d+)@", r[6])
        built_at = one("SELECT DATE_FORMAT(FROM_UNIXTIME(%s/1000),'%%H:%%i:%%s.%%f');" % built.group(1)) if built else "?"
        print("  email_logs id=%s date_email=%s subject=%s to=%s source=%s succes_email=%s message built at %s (local, from "
              "the Message-ID %s)" % (r[0], r[1], r[2], r[3], r[4], r[5], built_at, r[6]))
    log = sql("SELECT DATE_FORMAT(event_time,'%%H:%%i:%%s.%%f'), thread_id, LEFT(CONVERT(argument USING utf8mb4),90) "
              "FROM mysql.general_log WHERE event_time >= '%s' AND command_type='Query' AND LOWER(CONVERT(argument "
              "USING utf8mb4))='commit' ORDER BY event_time;" % since)
    for t, th, a in log:
        print("  general_log %s thr %-6s %s" % (t, th, a))


def case_office_batch(label):
    country = 78
    inst = one("SELECT i.id FROM institutions i WHERE i.id NOT IN (SELECT institution_id FROM institutions_locations WHERE "
               "loc_element_id=%d) AND i.is_active=1 ORDER BY i.id DESC LIMIT 1;" % country)
    cols = ("partner_name, acronym, institution_type_id, loc_element_id, is_office, institution_id, request_source, "
            "is_active, active_since, created_by, global_unit_id, id_phase")
    ids = []
    for n in (1, 2):
        sql("INSERT INTO partner_requests (%s) VALUES ('%s', 'T04', 31, %d, 1, %s, 'T04 probe %s office %d', 1, NOW(), "
            "%s, %s, %s);" % (cols, label, country, inst, label, n, REQUESTER, GU, PHASE))
        ids.append(int(one("SELECT MAX(id) FROM partner_requests;")))
    print("== office-batch label=%s institution=%s country=%d requests=%s db=%s" % (label, inst, country, ids, DB))
    show("counts before", counts())
    print("  locations of the institution in the country before: %s" % one(
        "SELECT COUNT(*) FROM institutions_locations WHERE institution_id=%s AND loc_element_id=%d;" % (inst, country)))
    print("  requests before:", request_state(ids))
    cookie = login()
    st, raw, hdrs = http_("POST", "/acceptOfficesRequest.do", {
        "countryOfficePOJO.institution.id": inst, "countryOfficePOJO.ids": ",".join(map(str, ids)),
        "phaseID": PHASE, "edit": "true"}, cookie)
    print("  HTTP %s body=%s" % (st, re.sub(r"\s+", " ", raw)[:300]))
    show("counts after", counts())
    print("  locations of the institution in the country after: %s" % one(
        "SELECT COUNT(*) FROM institutions_locations WHERE institution_id=%s AND loc_element_id=%d;" % (inst, country)))
    print("  requests after:", request_state(ids))
    print("  accepted flags (DONE -> 1, SKIPPED_EXISTING -> 0): %s" % sql(
        "SELECT id, acepted, LEFT(IFNULL(modification_justification,''),60) FROM partner_requests WHERE id IN (%s) ORDER BY id;" %
        ",".join(map(str, ids))))


if __name__ == "__main__":
    case = sys.argv[1]
    label = sys.argv[2]
    if case == "accept-once":
        case_accept_once(label)
    elif case == "accept-race":
        case_accept_race(label, len(sys.argv) > 3 and sys.argv[3] == "gate")
    elif case == "accept-order":
        case_accept_order(label)
    elif case == "sqllog":
        dump_sql_log(sys.argv[3], [int(i) for i in sys.argv[4].split(",")], label)
    elif case == "email-log":
        case_email_log(label, sys.argv[3])
    elif case == "office-batch":
        case_office_batch(label)
    else:
        raise SystemExit("unknown case " + case)
