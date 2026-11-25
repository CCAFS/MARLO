#!/usr/bin/env python3
"""CHG-PARTREQ-001-T11a live probe: end-to-end checks of PR 1 on the throwaway database.

Drives the RUNNING application with real HTTP requests and reads the database state through separate MySQL
connections. Usage:

    PROBE_MYSQL='<command that opens a mysql client on the throwaway database>' \
      python3 -I t11a-probe.py <case> [args]

Safety (hard rules of the task):
  * refuses to run unless the connected database name contains "partreq_test";
  * the superadmin login is read from the file named by PROBE_CREDENTIALS (default ~/.marlo-local-credentials) and is
    never printed; the non-superadmin test user is a user of the COPY whose password was set on the COPY only
    (PROBE_NONSA_USER / PROBE_NONSA_PW);
  * every request created here by SQL is created by PROBE_REQUESTER, the COPY user whose email is the address of the
    notification mailbox (email.notification), and has externalUserMail empty, so no decision email can reach anyone else
    (and outside production SendMailS addresses every message to that mailbox only, with no CC and no BCC).
"""
import http.client
import json
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
REQUESTER = os.environ.get("PROBE_REQUESTER", "2491")
NONSA_USER = os.environ.get("PROBE_NONSA_USER", "")
NONSA_PW = os.environ.get("PROBE_NONSA_PW", "")
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


def sa_credentials():
    path = os.path.expanduser(os.environ.get("PROBE_CREDENTIALS", "~/.marlo-local-credentials"))
    values = {}
    for line in open(path):
        if "=" in line and not line.startswith("#"):
            k, v = line.strip().split("=", 1)
            values[k] = v
    return values["MARLO_LOCAL_USER"], values["MARLO_LOCAL_PASSWORD"]


def http_(method, path, data=None, cookie=None, headers=None):
    c = http.client.HTTPConnection(HOST, PORT, timeout=120)
    h = dict(headers or {})
    body = None
    if data is not None:
        body = urllib.parse.urlencode(data)
        h["Content-Type"] = "application/x-www-form-urlencoded"
    if cookie:
        h["Cookie"] = cookie
    c.request(method, PREFIX + path, body, h)
    r = c.getresponse()
    raw = r.read().decode("utf8", "replace")
    hdrs = r.getheaders()
    c.close()
    return r.status, raw, hdrs


def live_cookie(hdrs):
    live = [m for m in re.findall(r"JSESSIONID=([^;]+)", "\n".join(v for k, v in hdrs if k.lower() == "set-cookie"))
            if m != "deleteMe"]
    return "JSESSIONID=" + live[-1] if live else None


def login(who="sa", crp="AICCRA"):
    user, pw = sa_credentials() if who == "sa" else (NONSA_USER, NONSA_PW)
    st, _, hdrs = http_("POST", "/login.do", {"user.email": user, "user.password": pw, "crp": crp})
    cookie = live_cookie(hdrs)
    if cookie is None:
        raise SystemExit("login (%s) gave no session cookie (HTTP %s)" % (who, st))
    return cookie


def request_cols():
    return ("partner_name, acronym, institution_type_id, loc_element_id, web_page, is_office, request_source, "
            "is_active, active_since, created_by, global_unit_id, id_phase")


def new_pair(label, gu=None):
    """Original + copy, both pending, active, with a type and a country. Created by REQUESTER, no external mail."""
    gu = gu or GU
    vals = "'%s', 'T11A', 31, 78, 'https://t11a.example', 0, 'T11a probe %s', 1, NOW(), %s, %s, %s" % (
        label, label, REQUESTER, gu, PHASE)
    sql("INSERT INTO partner_requests (%s) VALUES (%s);" % (request_cols(), vals))
    original = int(one("SELECT MAX(id) FROM partner_requests;"))
    sql("INSERT INTO partner_requests (%s, partner_request_id, is_modified) VALUES (%s, %d, 0);" % (
        request_cols(), vals, original))
    copy = int(one("SELECT MAX(id) FROM partner_requests;"))
    return original, copy


def new_offices(label, country, inst, n=2, gu=None):
    gu = gu or GU
    cols = ("partner_name, acronym, institution_type_id, loc_element_id, is_office, institution_id, request_source, "
            "is_active, active_since, created_by, global_unit_id, id_phase")
    ids = []
    for i in range(n):
        sql("INSERT INTO partner_requests (%s) VALUES ('%s', 'T11A', 31, %d, 1, %s, 'T11a probe %s office %d', 1, NOW(), "
            "%s, %s, %s);" % (cols, label, country, inst, label, i + 1, REQUESTER, gu, PHASE))
        ids.append(int(one("SELECT MAX(id) FROM partner_requests;")))
    return ids


def state(ids):
    rows = sql("SELECT id, is_active, IFNULL(acepted,'NULL'), IFNULL(institution_id,'NULL'), is_modified, "
               "IFNULL(LEFT(modification_justification,40),'NULL'), IFNULL(partner_name,'NULL') FROM partner_requests "
               "WHERE id IN (%s) ORDER BY id;" % ",".join(str(i) for i in ids))
    return "; ".join("req %s active=%s acepted=%s institution=%s modified=%s justification=%r name=%r" % tuple(r)
                     for r in rows)


def fingerprint():
    """A cheap fingerprint of every table the review actions can write, to prove 'no row changed'."""
    return {
        "partner_requests": one("SELECT CONCAT(COUNT(*),'/',IFNULL(SUM(is_active),0),'/',IFNULL(SUM(IFNULL(acepted,7)),0),"
                                "'/',IFNULL(SUM(is_modified),0),'/',IFNULL(MAX(id),0)) FROM partner_requests;"),
        "institutions": one("SELECT CONCAT(COUNT(*),'/',IFNULL(MAX(id),0)) FROM institutions;"),
        "institutions_locations": one("SELECT CONCAT(COUNT(*),'/',IFNULL(MAX(id),0)) FROM institutions_locations;"),
        "email_logs": one("SELECT IFNULL(MAX(id),0) FROM email_logs;"),
    }


def show(title, d):
    print("  %-30s %s" % (title, ", ".join("%s=%s" % kv for kv in d.items())))


def body_short(raw, n=260):
    return re.sub(r"\s+", " ", raw)[:n]


def hdr(hdrs, name):
    v = [x for k, x in hdrs if k.lower() == name]
    return v[0] if v else ""


# --------------------------------------------------------------------------------------------------------------------
# Cases
# --------------------------------------------------------------------------------------------------------------------

def edit_data(copy, tag):
    return {"requestID": copy, "institutionName": "T11a probe edited " + tag, "institutionAcronym": "T11E",
            "institutionWebPage": "https://t11a-edited.example", "institutionTypeID": "31", "countryID": "FR",
            "justification": "T11a probe edit justification", "phaseID": PHASE, "edit": "true"}


def case_nonsa(label):
    """Non-superadmin, hand-crafted POSTs to the five review routes (and the GETs). Run on staging (falsifier) and on
    the branch: the same script, the same fixtures."""
    print("== nonsa label=%s  db=%s  (non-superadmin: COPY user id %s; requests created by COPY user %s)" % (
        label, DB, os.environ.get("PROBE_NONSA_ID", "?"), REQUESTER))
    o1, c1 = new_pair(label + " reject")
    o2, c2 = new_pair(label + " edit")
    o3, c3 = new_pair(label + " accept")
    inst = one("SELECT i.id FROM institutions i WHERE i.is_active=1 ORDER BY i.id DESC LIMIT 1;")
    offs = new_offices(label + " office", 78, inst, 2)
    offs2 = new_offices(label + " office rej", 78, inst, 2)
    all_ids = [o1, c1, o2, c2, o3, c3] + offs + offs2
    before = fingerprint()
    show("fingerprint before", before)
    rows_before = state(all_ids)
    print("  rows before:", rows_before)
    cookie = login("nonsa")
    calls = [
        ("POST", "/rejectPartnerRequest.do", {"requestID": c1, "justification": "T11a probe nonsa reject",
                                              "sendNotification": "false", "phaseID": PHASE}),
        ("POST", "/editPartnerRequest.do", edit_data(c2, label)),
        ("POST", "/superadmin/addPartner.do", {"requestID": c3, "edit": "true", "phaseID": PHASE}),
        ("POST", "/acceptOfficesRequest.do", {"countryOfficePOJO.institution.id": inst,
                                              "countryOfficePOJO.ids": ",".join(map(str, offs)),
                                              "phaseID": PHASE, "edit": "true"}),
        ("POST", "/rejectOfficesRequest.do", {"countryOfficePOJO.institution.id": inst,
                                              "countryOfficePOJO.ids": ",".join(map(str, offs2)),
                                              "justification": "T11a probe nonsa office reject",
                                              "sendNotification": "false", "phaseID": PHASE, "edit": "true"}),
    ]
    for method, path, data in calls:
        st, raw, hdrs = http_(method, path, data, cookie)
        print("  %s %-28s -> HTTP %s  Location=%s  body=%s" % (method, path, st, hdr(hdrs, "location"), body_short(raw)))
    after = fingerprint()
    show("fingerprint after", after)
    rows_after = state(all_ids)
    print("  rows after: ", rows_after)
    print("  RESULT: %s" % ("NO ROW CHANGED" if rows_after == rows_before and after == before else "ROWS CHANGED"))


def case_sa_get(label):
    """Superadmin GET to each of the five review routes: nothing changes."""
    print("== sa-get label=%s  db=%s" % (label, DB))
    o1, c1 = new_pair(label + " reject")
    o2, c2 = new_pair(label + " edit")
    o3, c3 = new_pair(label + " accept")
    inst = one("SELECT i.id FROM institutions i WHERE i.is_active=1 ORDER BY i.id DESC LIMIT 1;")
    offs = new_offices(label + " office", 78, inst, 2)
    offs2 = new_offices(label + " office rej", 78, inst, 2)
    all_ids = [o1, c1, o2, c2, o3, c3] + offs + offs2
    before = fingerprint()
    show("fingerprint before", before)
    print("  rows before:", state(all_ids))
    cookie = login("sa")
    calls = [
        ("/rejectPartnerRequest.do", {"requestID": c1, "justification": "T11a probe get reject", "sendNotification": "false", "phaseID": PHASE}),
        ("/editPartnerRequest.do", edit_data(c2, label)),
        ("/superadmin/addPartner.do", {"requestID": c3, "edit": "true", "phaseID": PHASE}),
        ("/acceptOfficesRequest.do", {"countryOfficePOJO.institution.id": inst, "countryOfficePOJO.ids": ",".join(map(str, offs)), "phaseID": PHASE, "edit": "true"}),
        ("/rejectOfficesRequest.do", {"countryOfficePOJO.institution.id": inst, "countryOfficePOJO.ids": ",".join(map(str, offs2)), "justification": "T11a probe get office reject", "sendNotification": "false", "phaseID": PHASE, "edit": "true"}),
    ]
    for path, data in calls:
        st, raw, hdrs = http_("GET", path + "?" + urllib.parse.urlencode(data), None, cookie)
        print("  GET  %-28s -> HTTP %s  Location=%s  body=%s" % (path, st, hdr(hdrs, "location"), body_short(raw)))
    show("fingerprint after", fingerprint())
    print("  rows after: ", state(all_ids))
    print("  RESULT: %s" % ("NOTHING CHANGED" if fingerprint() == before else "STATE CHANGED"))


def page_text(raw):
    txt = re.sub(r"<script.*?</script>|<style.*?</style>", "", raw, flags=re.S)
    return re.sub(r"\s+", " ", re.sub(r"<[^>]+>", " ", txt)).strip()


def links_to(raw, action):
    return sorted(set(re.findall(r'href="([^"]*/%s\.do[^"]*)"' % action, raw)))


def case_menus(label):
    """FN-011 (superadmin menu entry in AICCRA), SEC-003 (CRP-admin menu entry and route), P-15 (no phaseID)."""
    print("== menus label=%s db=%s" % (label, DB))
    sa = login("sa")
    # P-15: the review URL without phaseID, exactly as the alert email links to it
    st, raw, hdrs = http_("GET", "/superadmin/marloInstitutions.do", None, sa)
    print("  [SA, AICCRA] GET /superadmin/marloInstitutions.do (no phaseID, no edit) -> HTTP %s, %d bytes, title=%r" % (
        st, len(raw), (re.search(r"<title>(.*?)</title>", raw, re.S) or [0, ""])[1].strip()))
    print("      page lists pending partner requests: %s ; has 'Institutions' menu link to marloInstitutions: %s" % (
        "partnerRequestItem" in raw, bool(links_to(raw, "marloInstitutions"))))
    print("      menu links to marloInstitutions: %s" % links_to(raw, "marloInstitutions")[:3])
    print("      menu entry text near it: %r" % re.findall(r">\s*([^<>]*Institutions[^<>]*)<", raw)[:4])
    # the same, as the link in the alert email is built (with phaseID)
    st2, raw2, _ = http_("GET", "/superadmin/marloInstitutions.do?phaseID=%s" % PHASE, None, sa)
    print("  [SA, AICCRA] GET /superadmin/marloInstitutions.do?phaseID=%s -> HTTP %s" % (PHASE, st2))
    # SEC-003: the CRP-admin route
    st, raw, hdrs = http_("GET", "/admin/AICCRA/marloInstitutions.do", None, sa)
    print("  [SA, AICCRA] GET /admin/AICCRA/marloInstitutions.do -> HTTP %s Location=%s" % (st, hdr(hdrs, "location")))
    cr = login("nonsa")
    st, raw, hdrs = http_("GET", "/admin/AICCRA/marloInstitutions.do", None, cr)
    print("  [CRP admin 22, AICCRA] GET /admin/AICCRA/marloInstitutions.do -> HTTP %s Location=%s body=%r" % (
        st, hdr(hdrs, "location"), page_text(raw)[:80]))
    st, raw, hdrs = http_("GET", "/superadmin/marloInstitutions.do", None, cr)
    print("  [CRP admin 22, AICCRA] GET /superadmin/marloInstitutions.do -> HTTP %s Location=%s body=%r" % (
        st, hdr(hdrs, "location"), page_text(raw)[:60]))
    for act in ("management", "ppaPartners", "crpUsers"):
        st, raw, hdrs = http_("GET", "/admin/AICCRA/%s.do?phaseID=%s" % (act, PHASE), None, cr)
        print("  [CRP admin 22, AICCRA] GET /admin/AICCRA/%s.do -> HTTP %s, CRP-admin menu rendered: %s ; link to marloInstitutions: %s ; "
              "text 'Institutions' in menu: %s" % (act, st, "ppaPartners" in raw or "crpUsers" in raw,
              links_to(raw, "marloInstitutions"), bool(re.search(r"menu[^>]*>[^<]*Institutions", raw))))
    # a non-AICCRA unit: the CRP-admin menu branch of menu-admin.ftl (!action.isAiccra())
    sa2 = login("sa", crp="CCAFS")
    for act in ("management", "ppaPartners"):
        st, raw, hdrs = http_("GET", "/admin/CCAFS/%s.do?phaseID=%s" % (act, PHASE), None, sa2)
        print("  [SA, CCAFS] GET /admin/CCAFS/%s.do -> HTTP %s, CRP-admin menu rendered: %s ; links to marloInstitutions: %s" % (
            act, st, "ppaPartners" in raw or "crpUsers" in raw, links_to(raw, "marloInstitutions")))
    st, raw, hdrs = http_("GET", "/admin/CCAFS/marloInstitutions.do", None, sa2)
    print("  [SA, CCAFS] GET /admin/CCAFS/marloInstitutions.do -> HTTP %s Location=%s" % (st, hdr(hdrs, "location")))


def case_helpers(case, label, rest):
    """Small helpers used between browser steps: fixtures by SQL, the row state, the emails since an id."""
    if case == "pair":
        o, c = new_pair(label)
        print("%d %d" % (o, c))
    elif case == "offices":
        inst, country = rest[0], int(rest[1])
        print(" ".join(str(i) for i in new_offices(label, country, inst, int(rest[2]) if len(rest) > 2 else 2)))
    elif case == "state":
        print(state([int(i) for i in rest]))
    elif case == "emails":
        since = int(rest[0])
        rows = sql("SELECT id, date_email, subject, REPLACE(to_email, '%s', '<notification mailbox>'), source_action, "
                   "succes_email FROM email_logs WHERE id > %d ORDER BY id;" % (os.environ.get("PROBE_MAILBOX", "-"), since))
        print("== email_logs since id %d: %d row(s)" % (since, len(rows)))
        for r in rows:
            print("  id=%s %s | subject=%s | to=%s | source=%s | succes_email=%s" % tuple(r))



def case_submit(label, rest):
    """Submit a partner request through the same form POST the request page sends (as COPY user REQUESTER, whose
    email is the notification mailbox). rest = [unit acronym, namespace, projectID]. Used where the browser form was
    already shown to work and only another unit or another row is needed."""
    crp, ns, project = rest[0], rest[1], rest[2]
    saved = os.environ.get("PROBE_NONSA_USER")
    cookie = login("nonsa", crp=crp) if False else None
    user = os.environ.get("PROBE_REQUESTER_USER", "")
    st, _, hdrs = http_("POST", "/login.do", {"user.email": user, "user.password": NONSA_PW, "crp": crp})
    cookie = live_cookie(hdrs)
    data = {"activityPartner.partner.name": label, "activityPartner.partner.acronym": "T11S",
            "activityPartner.partner.institutionType.id": "39", "locationId": "78",
            "activityPartner.partner.websiteLink": "https://t11a-submit.example", "projectID": project,
            "context": "", "phaseID": PHASE, "save": ""}
    before = int(one("SELECT MAX(id) FROM partner_requests;"))
    st, raw, hdrs = http_("POST", "/%s/%s/partnerSave.do" % (ns, crp), data, cookie)
    print("  POST /%s/%s/partnerSave.do (%s) -> HTTP %s Location=%s" % (ns, crp, label, st, hdr(hdrs, "location")))
    rows = sql("SELECT id, partner_request_id, is_active, global_unit_id, created_by, id_phase, request_source FROM "
               "partner_requests WHERE id > %d ORDER BY id;" % before)
    for r in rows:
        print("  new row: id=%s partner_request_id=%s is_active=%s global_unit_id=%s created_by=%s id_phase=%s source=%r" % tuple(r))



def row(i):
    r = sql("SELECT id, partner_name, acronym, web_page, IFNULL(institution_type_id,'NULL'), IFNULL(loc_element_id,'NULL'), "
            "IFNULL(is_modified,'NULL'), IFNULL(modification_justification,'NULL'), is_active, IFNULL(acepted,'NULL') "
            "FROM partner_requests WHERE id=%s;" % i)[0]
    return ("id=%s name=%r acronym=%r web=%r type=%s country=%s modified=%s justification=%r active=%s acepted=%s" % tuple(r))


def case_edit(label):
    """FN-007 with the field names the action reads (the current screen sends other names: see t11a-ui-edit.out)."""
    print("== edit label=%s db=%s (superadmin POST, the field names read by EditPartnerRequestAction)" % (label, DB))
    cookie = login("sa")
    o, c = new_pair(label)
    print("  fixture original=%d copy=%d" % (o, c))
    print("  original before: ", row(o))
    print("  copy before:     ", row(c))
    st, raw, _ = http_("POST", "/editPartnerRequest.do", edit_data(c, "ok"), cookie)
    print("  POST edit (pending) -> HTTP %s %s" % (st, body_short(raw)))
    print("  original after:  ", row(o))
    print("  copy after:      ", row(c))
    # T05 forward pointer: an edit whose type and country are empty / unknown
    data = edit_data(c, "blank")
    data["institutionTypeID"] = ""
    data["countryID"] = "ZZ"
    before = row(c)
    st, raw, _ = http_("POST", "/editPartnerRequest.do", data, cookie)
    print("  POST edit with empty type and unknown country ZZ -> HTTP %s %s" % (st, body_short(raw)))
    print("  copy after blank/unknown edit: ", row(c))
    print("  (unchanged type/country? type %s, country %s)" % (
        "kept" if "type=%s" % one("SELECT institution_type_id FROM partner_requests WHERE id=%d" % c) in row(c) else "?",
        one("SELECT loc_element_id FROM partner_requests WHERE id=%d" % c)))
    # not pending: close the request, then edit again
    sql("UPDATE partner_requests SET is_active=0, acepted=0 WHERE id IN (%d, %d);" % (o, c))
    before = row(c)
    st, raw, _ = http_("POST", "/editPartnerRequest.do", edit_data(c, "late"), cookie)
    print("  POST edit of a request that is no longer pending -> HTTP %s %s" % (st, body_short(raw)))
    print("  copy unchanged by the late edit: %s" % (before == row(c)))
    # unknown id
    st, raw, _ = http_("POST", "/editPartnerRequest.do", edit_data(99999999, "none"), cookie)
    print("  POST edit of an unknown id -> HTTP %s %s" % (st, body_short(raw)))
    # missing fields (what the current screen sends)
    st, raw, _ = http_("POST", "/editPartnerRequest.do", {"requestID": c, "name": "x", "acronym": "x", "type": "39",
                       "country": "FR", "webPage": "x", "modificationJustification": "x", "phaseID": PHASE}, cookie)
    print("  POST edit with the screen's field names (name/type/country/...) -> HTTP %s %s" % (st, body_short(raw)))



def case_office_request(label, rest):
    """FN-003: a country-office request through the route the project partners page calls (requestCountryOffice.do),
    as COPY user REQUESTER (email = notification mailbox). rest = [institutionID, ISO,ISO, projectID]."""
    inst, isos, project = rest[0], rest[1].split(","), rest[2]
    user = os.environ.get("PROBE_REQUESTER_USER", "")
    st, _, hdrs = http_("POST", "/login.do", {"user.email": user, "user.password": NONSA_PW, "crp": os.environ.get("PROBE_CRP", "AICCRA")})
    cookie = live_cookie(hdrs)
    before = int(one("SELECT MAX(id) FROM partner_requests;"))
    mail0 = int(one("SELECT IFNULL(MAX(id),0) FROM email_logs;"))
    qs = [("projectID", project), ("institutionID", inst)] + [("countriesID", c) for c in isos] + [("phaseID", PHASE)]
    st, raw, hdrs = http_("GET", "/requestCountryOffice.do?" + urllib.parse.urlencode(qs), None, cookie)
    print("  GET /requestCountryOffice.do (%s, institution %s, %s) -> HTTP %s %s" % (label, inst, ",".join(isos), st, body_short(raw)))
    rows = sql("SELECT id, institution_id, loc_element_id, is_office, is_active, global_unit_id, created_by, request_source FROM "
               "partner_requests WHERE id > %d ORDER BY id;" % before)
    for r in rows:
        print("  new row: id=%s institution=%s country=%s is_office=%s is_active=%s global_unit_id=%s created_by=%s source=%r" % tuple(r))
    time.sleep(4)
    print("  email_logs after: %s" % [r for r in sql("SELECT id, subject, source_action, succes_email FROM email_logs WHERE id > %d;" % mail0)])



def case_switch(label):
    """FN-010 / AC alerts: the unit's email-notification switch (custom_parameters 2184, crp_enable_email_notification of
    unit 45) set to false on the COPY: every request is stored, every decision applies, and no email is sent."""
    print("== switch label=%s db=%s" % (label, DB))
    key = one("SELECT CONCAT(p.`key`, '=', cp.value) FROM custom_parameters cp JOIN parameters p ON p.id = cp.parameter_id "
              "WHERE cp.id = 2184;")
    print("  switch before: custom_parameters 2184 %s (unit %s)" % (key, one("SELECT global_unit_id FROM custom_parameters WHERE id=2184;")))
    sql("UPDATE custom_parameters SET value='false' WHERE id=2184;")
    print("  switch set to: %s" % one("SELECT value FROM custom_parameters WHERE id=2184;"))
    mail0 = int(one("SELECT IFNULL(MAX(id),0) FROM email_logs;"))
    sa = login("sa")
    try:
        # a) partner request and office request stored, no alert
        case_submit(label + " submit", ["AICCRA", "clusters", "102076"])
        inst = one("SELECT id FROM institutions WHERE name='T11a ui accept';")
        case_office_request(label + " office", [inst, "SN", "102076"])
        # b) decisions with the switch off
        o1, c1 = new_pair(label + " accept")
        o2, c2 = new_pair(label + " reject")
        offs = new_offices(label + " office accept", 78, inst, 1)
        offs2 = new_offices(label + " office reject", 78, inst, 1)
        r = [http_("POST", "/superadmin/addPartner.do", {"requestID": c1, "edit": "true", "phaseID": PHASE}, sa),
             http_("POST", "/rejectPartnerRequest.do", {"requestID": c2, "justification": "T11a switch off", "sendNotification": "true", "phaseID": PHASE}, sa),
             http_("POST", "/acceptOfficesRequest.do", {"countryOfficePOJO.institution.id": inst, "countryOfficePOJO.ids": ",".join(map(str, offs)), "phaseID": PHASE}, sa),
             http_("POST", "/rejectOfficesRequest.do", {"countryOfficePOJO.institution.id": inst, "countryOfficePOJO.ids": ",".join(map(str, offs2)), "justification": "T11a switch off", "sendNotification": "true", "phaseID": PHASE}, sa)]
        for name, x in zip(("addPartner", "rejectPartnerRequest(notify=true)", "acceptOfficesRequest", "rejectOfficesRequest(notify=true)"), r):
            print("  %-36s HTTP %s %s" % (name, x[0], body_short(x[1], 120) if x[0] != 302 else "-> " + hdr(x[2], "location")))
        print("  rows: ", state([o1, c1, o2, c2] + offs + offs2))
        time.sleep(5)
        n = int(one("SELECT COUNT(*) FROM email_logs WHERE id > %d;" % mail0))
        print("  email_logs rows created while the switch was off: %d" % n)
        print("  RESULT: %s" % ("NO EMAIL (switch honoured)" if n == 0 else "EMAILS SENT"))
    finally:
        sql("UPDATE custom_parameters SET value='true' WHERE id=2184;")
        print("  switch restored: %s" % one("SELECT value FROM custom_parameters WHERE id=2184;"))



def log_lines_since(mark, classes):
    lines = open(os.environ["PROBE_APPLOG"], errors="replace").read().splitlines()[mark:]
    out = []
    for l in lines:
        m = re.search(r"\[(ERROR|WARN ?)\] \[MARLO\] \[\w+\] \[(\w+)\]", l)
        if m and m.group(2) in classes:
            out.append(re.sub(r"^(\S{19})\S* - \[[^\]]*\] ", r"\1 ", re.sub(r"environment=\S+ request_id=(\S+) .*? controller_affected=\S+ ", r"request_id=\1 ", l))[:230])
    return out


def case_ops(label):
    """OPS-001: a forced failure in each review action gives one ERROR/WARN line with the action and the request id and
    no email address (read from the application log file named by PROBE_APPLOG)."""
    print("== ops label=%s db=%s" % (label, DB))
    sa = login("sa")
    classes = {"PartnerRequestAction", "EditPartnerRequestAction", "RejectPartnerRequestAction", "PartnerRequestManagerImpl",
               "PartnerRequestEmails", "SendMailS"}
    inst = one("SELECT id FROM institutions WHERE name='T11a ui accept';")
    # a pending copy whose country and type are missing: the accept fails after the manager started
    cols = "partner_name, is_office, request_source, is_active, active_since, created_by, global_unit_id, id_phase"
    vals = "'%s', 0, 'T11a probe %s', 1, NOW(), %s, %s, %s" % (label + " broken", label, REQUESTER, GU, PHASE)
    sql("INSERT INTO partner_requests (%s) VALUES (%s);" % (cols, vals))
    bo = int(one("SELECT MAX(id) FROM partner_requests;"))
    sql("INSERT INTO partner_requests (%s, partner_request_id, is_modified) VALUES (%s, %d, 0);" % (cols, vals, bo))
    bc = int(one("SELECT MAX(id) FROM partner_requests;"))
    cases = [
        ("addPartner, accept of a request without country and type (id %d)" % bc, "POST", "/superadmin/addPartner.do",
         {"requestID": bc, "edit": "true", "phaseID": PHASE}),
        ("rejectPartnerRequest, requestID not a number", "POST", "/rejectPartnerRequest.do",
         {"requestID": "abc", "justification": "T11a ops", "sendNotification": "false", "phaseID": PHASE}),
        ("editPartnerRequest, fields the action cannot read (request %d)" % bc, "POST", "/editPartnerRequest.do",
         {"requestID": bc, "name": "x", "phaseID": PHASE}),
        ("acceptOfficesRequest, ids not numbers (institution %s)" % inst, "POST", "/acceptOfficesRequest.do",
         {"countryOfficePOJO.institution.id": inst, "countryOfficePOJO.ids": "abc,def", "phaseID": PHASE}),
        ("rejectOfficesRequest, ids not numbers (institution %s)" % inst, "POST", "/rejectOfficesRequest.do",
         {"countryOfficePOJO.institution.id": inst, "countryOfficePOJO.ids": "abc,def", "justification": "T11a ops",
          "sendNotification": "false", "phaseID": PHASE}),
    ]
    fp = fingerprint()
    for title, method, path, data in cases:
        time.sleep(1)
        mark = len(open(os.environ["PROBE_APPLOG"], errors="replace").read().splitlines())
        st, raw, hdrs = http_(method, path, data, sa)
        time.sleep(2)
        lines = log_lines_since(mark, classes)
        print("  [%s] -> HTTP %s %s" % (title, st, body_short(raw, 110) if st != 302 else "-> " + hdr(hdrs, "location")))
        print("     WARN/ERROR lines from the review classes: %d" % len(lines))
        for l in lines:
            print("       " + l)
        print("     email address in those lines: %s" % ("YES" if any("@" in l for l in lines) else "no"))
    print("  email_logs unchanged by the failures: %s" % (fingerprint()["email_logs"] == fp["email_logs"]))
    print("  institutions unchanged by the failures: %s" % (fingerprint()["institutions"] == fp["institutions"]))



def case_multiunit(label):
    """FN-008 amended at T03: one office email per requester PER Global Unit of the requests. Requests of one institution
    from unit 45 and unit 47, same requester, accepted in one batch: two emails (one per unit), then the same for a reject."""
    print("== multiunit label=%s db=%s" % (label, DB))
    sa = login("sa")
    inst = one("SELECT id FROM institutions WHERE name='T11a ui accept';")
    free = [r[0] for r in sql("SELECT id FROM loc_elements WHERE element_type_id=2 AND is_active=1 AND id NOT IN "
                              "(SELECT loc_element_id FROM institutions_locations WHERE institution_id=%s) ORDER BY id LIMIT 4;" % inst)]
    print("  institution %s, countries free for it: %s" % (inst, free))
    mail0 = int(one("SELECT IFNULL(MAX(id),0) FROM email_logs;"))
    a = new_offices(label + " u45", int(free[0]), inst, 1, gu=45)[0]
    b = new_offices(label + " u47", int(free[1]), inst, 1, gu=47)[0]
    print("  accept batch: request %d (unit 45) + request %d (unit 47)" % (a, b))
    st, raw, _ = http_("POST", "/acceptOfficesRequest.do", {"countryOfficePOJO.institution.id": inst,
                       "countryOfficePOJO.ids": "%d,%d" % (a, b), "phaseID": PHASE}, sa)
    print("  -> HTTP %s %s" % (st, body_short(raw)))
    time.sleep(5)
    rows = sql("SELECT id, subject, REPLACE(to_email, '%s', '<notification mailbox>') FROM email_logs WHERE id > %d ORDER BY id;" % (
        os.environ.get("PROBE_MAILBOX", "-"), mail0))
    print("  emails sent: %d" % len(rows))
    for r in rows:
        print("    id=%s subject=%s to=%s" % tuple(r))
    mail1 = int(one("SELECT IFNULL(MAX(id),0) FROM email_logs;"))
    c = new_offices(label + " r45", int(free[2]), inst, 1, gu=45)[0]
    d = new_offices(label + " r47", int(free[3]), inst, 1, gu=47)[0]
    st, raw, _ = http_("POST", "/rejectOfficesRequest.do", {"countryOfficePOJO.institution.id": inst,
                       "countryOfficePOJO.ids": "%d,%d" % (c, d), "justification": "T11a multiunit", "sendNotification": "true",
                       "phaseID": PHASE}, sa)
    print("  reject batch %d (unit 45) + %d (unit 47) -> HTTP %s %s" % (c, d, st, body_short(raw)))
    time.sleep(5)
    rows = sql("SELECT id, subject FROM email_logs WHERE id > %d ORDER BY id;" % mail1)
    print("  emails sent: %d" % len(rows))
    for r in rows:
        print("    id=%s subject=%s" % tuple(r))



if __name__ == "__main__":
    case = sys.argv[1]
    label = sys.argv[2] if len(sys.argv) > 2 else "T11a"
    if case == "nonsa":
        case_nonsa(label)
    elif case in ("pair", "offices", "state", "emails"):
        case_helpers(case, label, sys.argv[3:] if case == "offices" else (sys.argv[2:] if case in ("state", "emails") else []))
    elif case == "edit":
        case_edit(label)
    elif case == "office-request":
        case_office_request(label, sys.argv[3:])
    elif case == "switch":
        case_switch(label)
    elif case == "ops":
        case_ops(label)
    elif case == "multiunit":
        case_multiunit(label)
    elif case == "submit":
        case_submit(label, sys.argv[3:])
    elif case == "menus":
        case_menus(label)
    elif case == "sa-get":
        case_sa_get(label)
    else:
        raise SystemExit("unknown case " + case)
