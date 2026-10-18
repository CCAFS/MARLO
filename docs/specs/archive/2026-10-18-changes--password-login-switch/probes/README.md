# CHG-PWD-SWITCH-001 probes and recorded output

Everything here ran against a **throwaway copy** of the local database (`aiccradb_otp_probe`, a clone of `aiccradb_actsave`).
Every script refuses to run unless the connected database name contains `probe`. `aiccradb_actsave` and `aiccradb1`-`4` were never
written to. No password, session id or real address appears in a recorded output: the accounts are the fixtures of `fixtures.sh`.

## 1. What is here

| File | Task | What it is |
|---|---|---|
| `ac1-ac14-migration.sh` | T01 | The `AddPasswordLoginSwitch` migration applied twice on three database shapes; the three descriptions compared byte for byte (AC-1, AC-14) |
| `output/t06-walkthrough.md` | T06 | Browser walkthrough of every step-3 view (AC-3, AC-15) |
| `output/t07-welcome-*.html` | T07 | The rendered welcome emails, switch off and on (AC-13) |
| `fixtures.sh` | T08 | The accounts of the live probes, in the copy only: the password account, a CGIAR account on a unit without Cognito, and the fixture users of the `CHG-OTP-LOGIN-001` probes |
| `ac-live.sh` | T08 | The live probes over HTTP (stages below) |
| `sweeps.sh` | T08 | The closure sweeps of the cross-spec amendment (forward and backward), recorded in `output/sweeps.out` |
| `tools/sink.py` | T08 | The local SMTP sink, writing the SMTP envelope in the first line of each message, the form `ac-probe.sh` reads |

## 2. T08 run (2026-10-17)

Application built clean from the working tree at HEAD `9b2448578d` with `scripts/run-marlo-java17.sh` (2415 / 1066 / 85 files in
marlo-data / marlo-web / test-classes, `BUILD SUCCESS`), `marlo.production=false`, the code key valid unless said, a JDWP agent on
for the session reads. Before any write, `ps eww` of the JVM on 8080 showed `MYSQL_DATABASE=aiccradb_otp_probe`, the sink's host
and port, `EMAIL_NOTIFICATION=support.probe@sink.test` and `OTP_HMAC_SECRET` present (empty in the AC-7 start). No production file
was edited except the temporary falsifier, reverted before the last run (`git diff -- marlo-data marlo-web marlo-utils` empty).

| AC | Stage or script | Result | Output |
|---|---|---|---|
| AC-1, AC-14 | `ac1-ac14-migration.sh` (T01) | already green in T01 | `ac1-ac14-migration.green.out` |
| AC-2 | `ac-live.sh all-on`, then `CHG-OTP-LOGIN-001` `ac-probe.sh enum live attempts resend removed limits nonprod session leak expiry` with the switch on everywhere | 70 checks, **68 pass, 2 fail**. Both failures are the probe reading the SMTP envelope: the sink of the run wrote it in a header, `ac-probe.sh` reads the first line. Every mail's envelope was `<support.probe@sink.test>`. Not an application defect | `ac2-switch-on-ac-probe.out` |
| AC-2 (the two failed checks) | `ac-probe.sh enum nonprod`, with `tools/sink.py` | 26 checks, 0 failed | `ac2-switch-on-ac-probe.rerun-enum-nonprod.out` |
| AC-2 (migration) | `CHG-OTP-LOGIN-001` `ac21-migration.sh`, after its DA-002 was reworded | 17 checks, 0 failed. The first run had one FAIL, a `custom_parameters` row left by earlier probes, not a migration row (`..-1-fixture-row-red.out`) | `docs/specs/changes/center-email-otp-login/probes/output/ac21-migration.pwd-switch-rerun.out` |
| AC-10, SEC-001, SEC-002, OPS-001 | `ac-live.sh ac10` | 23 checks, 0 failed | `ac10.green.out` |
| AC-8, FN-002 | `ac-live.sh ac8` | 8 checks, 0 failed | `ac8.green.out` |
| AC-8 falsifier | the same stage on a build whose switch read goes through the Hibernate query cache | **red: 5 of 8 failed**; reverted, rebuilt: green again, 8 of 8 | `ac8.FALSIFIER-red.out`, `ac8.FALSIFIER-restored-green.out`, `ac8.FALSIFIER.patch` |
| AC-7, OPS-002 | `ac-live.sh ac7`, application started with an empty `OTP_HMAC_SECRET` | 11 checks, 0 failed (the first run had one harness failure, a leftover challenge row) | `ac7.green.out`, `ac7.first-run-harness-fail.out` |
| AC-12 | `ac-live.sh ac12` | CLARISA: pass. `/api/**` Basic: **not verified**, see section 3 | `ac12.green.out` |
| AC-3 .. AC-6 live | `ac-live.sh override` | 10 checks, 0 failed | `override.green.out` |
| OPS-003 | `ac-live.sh runbook-sql` runs the two SQL statements of `runbook.md` as written | 5 checks, 0 failed | `runbook-sql.green.out` |
| T05 advisory items | `ac-live.sh walk` | the authenticated-session item passes; the CGIAR directory item is not reachable offline | `walk.green.out` |
| Sweeps | `sweeps.sh` | recorded in the Decision Log of `CHG-OTP-LOGIN-001` | `sweeps.out` |

**Not run, with the reason.**

| Not run | Why |
|---|---|
| `ac-probe.sh emaillog` (AC-18 of the code spec) | Needs a super admin login of the copy and control of the sink; no code under it changed in this spec |
| `ac-probe.sh postlogin`, `smtp`, `blackhole`, `tz`, `nosecret` | Need extra fixtures or restarts and exercise code this spec did not touch; the blank-key start of AC-7 covers the key-missing case |
| A live wrong CGIAR password against an Active Directory refusal (T05 advisory 1) | The directory does not answer offline (`UnknownHostException ciatroot5.CGIARAD.ORG`); what is observable is the accepted residual, printed in `walk.green.out` |

## 3. Findings of this run

- **`/api/**` HTTP Basic does not authenticate on this local stack, with the switch on or off.** The Shiro filter reaches
  `APCustomRealm.doGetAuthenticationInfo`, then `UserManagerImp.getUserByEmail`, which throws `HibernateException: Could not obtain
  transaction-synchronized Session for current thread`, so even the correct stored password answers 401 (log excerpt in
  `ac12.green.out`). No file of that path changed in this spec, none reads the switch (`sweeps.out`, B4), and the answer is the same
  with the switch on. This stage therefore proves only that the switch is not consulted on that path; it cannot show Basic
  authenticating. It needs one check on an environment where `/api/**` authenticates.
- **The first falsifier did not turn the probe red.** Reading the switch through `CustomParameterManager` (a cacheable query that
  returns an entity) left the probe green: the query cache stores the id, the entity is loaded from the database, and the value is fresh
  (`ac8.FALSIFIER-attempt1-entity-path-stays-green.out`). A valid falsifier caches the value itself, as the one recorded in
  `ac8.FALSIFIER.patch` does.
- **`MARLOCustomPersistFilter` calls `cache.evictAllRegions()` on every request it filters.** In this run its log line appeared for `/swagger/index.html` and for no
  `.do` or `/api/**` request. If it ever filters the login endpoints, the cache would be emptied on each request and a cached read of
  the switch would stop looking stale: the falsifier above would then need the cache primed by another route.
- **A first clean build failed once** with `cannot access APConfig` in `marlo-web` test-compile and passed unchanged on the
  next run: the known dirty-tree flake, not a code error.

## 4. How to repeat

```
export PROBE_MYSQL='<command opening mysql -N on the throwaway copy>' PROBE_PW_PASS=<test password>     # fixtures.sh sets it in the copy
export PROBE_MBOX=<sink mbox> PROBE_LOGS=<this run's console log> PROBE_JDWP=auto PROBE_JAVA_CP=<dir with SessDump.class>
python3 probes/tools/sink.py <sink mbox> &            # or any sink that writes " -> <rcpt>" in the first line
probes/fixtures.sh
probes/ac-live.sh all-on ac10 ac8 override runbook-sql ac12 walk
../center-email-otp-login/probes/ac-probe.sh enum live attempts resend removed limits nonprod session leak expiry
# AC-7: restart the application with OTP_HMAC_SECRET="" (empty), then:
probes/ac-live.sh ac7
# falsifier: apply output/ac8.FALSIFIER.patch, rebuild, run probes/ac-live.sh ac8 (red), revert, rebuild, run it again (green)
probes/sweeps.sh
```

## 5. Cleanup

The application and the sink were stopped, the scratch credentials and logs deleted, and `aiccradb_otp_probe` dropped. The fixture
users (3862, the T07 users 3865-3870, the `otp.*` and `probe.*` users) went with the drop. `aiccradb_actsave` and `aiccradb1`-`4`
were never written to.
