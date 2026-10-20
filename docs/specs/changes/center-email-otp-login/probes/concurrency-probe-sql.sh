#!/usr/bin/env bash
# CHG-OTP-LOGIN-001 -- T15 concurrency probe, statement level (design.md section 12; JS-1; FN-012, FN-013, FN-007).
#
# Executes the REAL conditional statements of the challenge store -- read from the source of OtpChallengeMySQLDAO.java,
# never retyped here -- from N parallel MySQL sessions released together by a GET_LOCK barrier, and checks the affected-row
# counts and the final row. It needs only the database, not the application, and it reaches what the HTTP probe cannot:
# the statements are fired without the Java pre-checks (consumed / attempts / expiry) that would refuse first, so the
# statement's own conditions are the only thing under test. That is the point of the falsifier: drop `attempts < 3`
# from CONSUME_SQL and the "after exhaustion" case must report a consume (JS-1).
#
# SAFETY: refuses to run unless the connected database name contains "probe".
#
# Environment:
#   PROBE_MYSQL   command opening a mysql client on the throwaway database, SQL on stdin   (default: mysql -N aiccradb_otp_probe)
#   PROBE_DAO     path of OtpChallengeMySQLDAO.java            (default: from the repository root)
#   PROBE_ROUNDS  rounds of the race burst S3b                  (default 30)
set -euo pipefail
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../../../../.." && pwd)"
export PROBE_DAO="${PROBE_DAO:-$ROOT/marlo-data/src/main/java/org/cgiar/ccafs/marlo/data/dao/mysql/OtpChallengeMySQLDAO.java}"
export PROBE_MODEL="${PROBE_MODEL:-$ROOT/marlo-data/src/main/java/org/cgiar/ccafs/marlo/data/model/OtpChallenge.java}"
exec python3 -u -I - "$@" <<'PY'
import collections, concurrent.futures as cf, datetime, os, re, subprocess, sys, time

MYSQL = os.environ.get("PROBE_MYSQL", "mysql -N aiccradb_otp_probe")
DAO = open(os.environ["PROBE_DAO"]).read()
MODEL = open(os.environ["PROBE_MODEL"]).read()
ROUNDS = int(os.environ.get("PROBE_ROUNDS", "30"))
MAX_ATTEMPTS = re.search(r"MAX_ATTEMPTS\s*=\s*(\d+)", MODEL).group(1)


def run(q, stdin_pipe=False):
    return subprocess.run(MYSQL, shell=True, input=q, capture_output=True, text=True)


def sql(q):
    out = run(q)
    if out.returncode != 0:
        raise SystemExit("mysql failed: " + out.stderr.strip()[:300])
    return [l.split("\t") for l in out.stdout.strip().splitlines() if l]


db = sql("SELECT DATABASE();")[0][0]
if "probe" not in db:
    raise SystemExit("refusing to run: database %r is not a throwaway probe copy" % db)


def constant(name):
    m = re.search(r"private static final String %s\s*=\s*(.*?);\n" % name, DAO, re.S)
    parts = re.findall(r'"((?:[^"\\]|\\.)*)"|(OtpChallenge\.MAX_ATTEMPTS)', m.group(1))
    return "".join(p if p else MAX_ATTEMPTS for p, v in parts).replace('\\"', '"')


STM = {n: constant(n + "_SQL") for n in ("SUPERSEDE", "INSERT", "INCREMENT_ATTEMPTS", "CONSUME")}
print("database under test: %s   MAX_ATTEMPTS=%s" % (db, MAX_ATTEMPTS))
for n, s in STM.items():
    print("  %-18s %s" % (n, s))

# Isolation level: the probe sessions and, when the application is connected to this database, its pooled connections. The
# application sets none itself (MarloDatabaseConfiguration: no Hikari isolation, no hibernate.connection.isolation), so its
# connections take the server default; the live reading below is the proof, not that inference.
iso = sql("SELECT @@global.transaction_isolation, @@session.transaction_isolation;")[0]
print("isolation: @@global.transaction_isolation=%s   @@session.transaction_isolation=%s (the probe's own sessions)" % (iso[0], iso[1]))
live = sql("SELECT IFNULL(t.PROCESSLIST_DB,''), v.VARIABLE_VALUE, COUNT(*) FROM performance_schema.variables_by_thread v "
           "JOIN performance_schema.threads t ON t.THREAD_ID = v.THREAD_ID WHERE v.VARIABLE_NAME = 'transaction_isolation' "
           "AND t.PROCESSLIST_DB = DATABASE() AND t.PROCESSLIST_ID <> CONNECTION_ID() AND t.TYPE = 'FOREGROUND' "
           "AND t.PROCESSLIST_COMMAND IN ('Sleep','Query') GROUP BY 1, 2;")
if live:
    print("isolation of the connections open on %s right now (the application pool, when it is running): %s"
          % (db, "; ".join("%s x %s" % (r[1], r[2]) for r in live)))
else:
    print("isolation of the application's pooled connections: no connection other than this probe on %s (application not running)" % db)


def bound(name, **params):
    """The statement with its named parameters bound through PREPARE/EXECUTE, as a driver would."""
    s = STM[name]
    names = re.findall(r":(\w+)", s)
    q = re.sub(r":\w+", "?", s).replace("'", "''")
    out = "SET @q = '%s'; PREPARE st FROM @q;\n" % q
    for i, n in enumerate(names):
        out += "SET @p%d = '%s';\n" % (i, params[n])
    return out + "EXECUTE st USING %s;\n" % ", ".join("@p%d" % i for i in range(len(names)))


def utc(dt):
    return dt.strftime("%Y-%m-%d %H:%M:%S.") + "%03d" % (dt.microsecond // 1000)


def seed(nonce, attempts=0, email="e" * 64, ttl=300):
    now = datetime.datetime.now(datetime.timezone.utc)
    sql("DELETE FROM otp_challenges WHERE nonce LIKE 'sqlprobe%%'; INSERT INTO otp_challenges (nonce,email_hmac,global_unit_id,code_hmac,"
        "expires_at,attempts,consumed_at,created_at) VALUES ('%s','%s',0,REPEAT('c',64),'%s',%d,NULL,'%s');"
        % (nonce, email, utc(now + datetime.timedelta(seconds=ttl)), attempts, utc(now)))


def burst(workers):
    """workers: list of SQL bodies. Each session blocks on GET_LOCK until every one is waiting; then the lock is
    released. Returns, per worker, the ROW_COUNT() printed after its body (the affected rows of its statement)."""
    n = len(workers)
    hold = subprocess.Popen(MYSQL, shell=True, stdin=subprocess.PIPE, stdout=subprocess.PIPE, stderr=subprocess.PIPE, text=True)
    hold.stdin.write("SELECT GET_LOCK('probe_go', 120);\n")
    hold.stdin.flush()
    for _ in range(200):  # the mysql client buffers its output on a pipe, so the held lock is polled, not read
        if sql("SELECT IS_USED_LOCK('probe_go') IS NOT NULL;")[0][0] == "1":
            break
        time.sleep(0.05)

    def one(body):
        r = run("SELECT GET_LOCK('probe_go', 120); SELECT RELEASE_LOCK('probe_go');\n" + body + "SELECT ROW_COUNT();\n")
        if r.returncode != 0:
            return "ERR:" + r.stderr.strip().splitlines()[-1][:80]
        return r.stdout.strip().splitlines()[-1]
    with cf.ThreadPoolExecutor(n) as ex:
        futs = [ex.submit(one, w) for w in workers]
        for _ in range(200):
            waiting = int(sql("SELECT COUNT(*) FROM performance_schema.metadata_locks WHERE object_type='USER LEVEL LOCK' "
                              "AND lock_status='PENDING';")[0][0])
            if waiting >= n:
                break
            time.sleep(0.05)
        else:
            waiting = -1
        hold.stdin.write("SELECT RELEASE_LOCK('probe_go');\n")
        hold.stdin.close()
        res = [f.result() for f in futs]
    hold.wait()
    return res, waiting


def final(nonce):
    return sql("SELECT attempts, consumed_at IS NOT NULL FROM otp_challenges WHERE nonce='%s';" % nonce)[0]


verdicts = []


def verdict(name, ok, detail):
    verdicts.append((name, ok))
    print("  => %s: %s   (%s)" % ("PASS" if ok else "FAIL", name, detail))


NOW = lambda: utc(datetime.datetime.now(datetime.timezone.utc))
consume = lambda nonce: bound("CONSUME", nonce=nonce, now=NOW())
incr = lambda nonce: bound("INCREMENT_ATTEMPTS", nonce=nonce)

print("\n[S1] 10 parallel CONSUME on one open challenge (AC-7)")
seed("sqlprobe1")
res, waiting = burst([consume("sqlprobe1") for _ in range(10)])
won = sum(1 for r in res if r == "1")
print("  sessions blocked on the barrier when released: %d   affected rows per session: %s   final (attempts, consumed): %s" % (waiting, res, final("sqlprobe1")))
verdict("S1: exactly one UPDATE affects a row", won == 1 and final("sqlprobe1")[1] == "1", "winners=%d" % won)

print("\n[S2] 10 parallel INCREMENT_ATTEMPTS on one open challenge (FN-012)")
seed("sqlprobe2")
res, waiting = burst([incr("sqlprobe2") for _ in range(10)])
won = sum(1 for r in res if r == "1")
print("  barrier: %d   affected rows per session: %s   final (attempts, consumed): %s" % (waiting, res, final("sqlprobe2")))
verdict("S2: attempts ends at 3, exactly 3 increments land", final("sqlprobe2")[0] == MAX_ATTEMPTS and won == int(MAX_ATTEMPTS), "winners=%d attempts=%s" % (won, final("sqlprobe2")[0]))

print("\n[S3] challenge already EXHAUSTED (attempts=%s): 3 wrong-code increments + 4 consume attempts fired together (JS-1)" % MAX_ATTEMPTS)
consumes = 0
for rnd in range(10):
    seed("sqlprobe3", attempts=int(MAX_ATTEMPTS))
    res, waiting = burst([incr("sqlprobe3") for _ in range(3)] + [consume("sqlprobe3") for _ in range(4)])
    won = sum(1 for r in res[3:] if r == "1")
    consumes += won
    if rnd < 3 or won:
        print("  round %d: increments %s consumes %s final %s" % (rnd + 1, res[:3], res[3:], final("sqlprobe3")))
print("  total consumes over 10 rounds: %d" % consumes)
verdict("S3: 0 consumes after exhaustion (10 rounds)", consumes == 0, "consumes=%d" % consumes)

print("\n[S3b] race burst from attempts=0, %d rounds: %s increments + 1 consume together" % (ROUNDS, MAX_ATTEMPTS))
viol = 0
seen = collections.Counter()
for rnd in range(ROUNDS):
    seed("sqlprobe3")
    res, waiting = burst([incr("sqlprobe3") for _ in range(int(MAX_ATTEMPTS))] + [consume("sqlprobe3")])
    a, c = final("sqlprobe3")
    seen[("consumed" if c == "1" else "not consumed", "attempts=" + a)] += 1
    if c == "1" and a == MAX_ATTEMPTS:
        viol += 1
print("  outcomes: %s" % dict(seen))
verdict("S3b: never consumed with attempts=%s" % MAX_ATTEMPTS, viol == 0, "violations=%d of %d" % (viol, ROUNDS))

print("\n[S4] 10 parallel issue transactions (supersede + insert) for ONE email, no cooldown in front (FN-007; informational)")
sql("DELETE FROM otp_challenges WHERE email_hmac = REPEAT('d',64);")
workers = []
for i in range(10):
    now = NOW()
    workers.append("START TRANSACTION;\n" + bound("SUPERSEDE", emailHmac="d" * 64, now=now)
                   + bound("INSERT", nonce="sqlprobe4_%d" % i, emailHmac="d" * 64, globalUnitId="0", codeHmac="c" * 64,
                           expiresAt=utc(datetime.datetime.now(datetime.timezone.utc) + datetime.timedelta(seconds=300)), now=now) + "COMMIT;\n")
res, waiting = burst(workers)
open_rows = sql("SELECT COUNT(*) FROM otp_challenges WHERE email_hmac = REPEAT('d',64) AND consumed_at IS NULL;")[0][0]
print("  barrier: %d   sessions' last ROW_COUNT: %s   open rows afterwards: %s" % (waiting, res, open_rows))
print("  (the HTTP probe case 4 covers the real path, where the cooldown row lock admits one request at a time)")
sql("DELETE FROM otp_challenges WHERE nonce LIKE 'sqlprobe%' OR email_hmac = REPEAT('d',64) OR email_hmac = REPEAT('e',64);")
INFO_S4 = open_rows

failed = [n for n, ok in verdicts if not ok]
print("\nSUMMARY: %d checks, %d failed%s   [S4 open rows without cooldown: %s]" % (len(verdicts), len(failed), (": " + "; ".join(failed)) if failed else "", INFO_S4))
sys.exit(1 if failed else 0)
PY
