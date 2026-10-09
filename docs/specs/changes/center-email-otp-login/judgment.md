# Center Email OTP Login — Judgment Day Ledger

| Field | Value |
|---|---|
| Target | `design.md` (draft of 2026-10-08), judged against `requirements.md` and `proposal.md` |
| Base commit | `398d7b4058` plus uncommitted spec files |
| Mode | Blind dual review. Two read-only judges on `sonnet` (the author ran on `opus`) with identical prompts |
| Round | 1 |
| State | **approved**. Round 1 correction applied; scoped re-judgment found no severe findings (2026-10-09) |

## Frozen findings — round 1

Each finding is classified as one of:

- **Confirmed**: both judges reported it independently.
- **Suspect**: one judge reported it as severe.
- **Info**: a warning or suggestion.

Only confirmed severe findings are auto-fix candidates.

### Confirmed

| ID | Judge A | Judge B | Severity | Finding |
|---|---|---|---|---|
| JD-1 | J-1 severe | J-1 severe | **severe** | The 30-s cooldown reads `otp_challenges`, which only eligible emails have. Two quick requests answer `rateLimited` when the account is eligible and `sent` when it is not, which reveals account state (SEC-006) |
| JD-2 | J-2 severe | J-2 severe | **severe** | Supersede is an oracle. Request in session S1, request again in S2 after 30 s, then verify in S1. An eligible account answers `invalid` (row superseded); an ineligible one answers `mismatch` (decoy untouched). DD-5 mirrors only attempts and expiry |
| JD-3 | J-3 severe | J-4 severe | **severe** | `OtpPending` carries `userId`, not the email or its MAC. The per-email verify bucket cannot be keyed without a lookup, which breaks "count before lookup" (SEC-010). A decoy has no email key, so eligible and decoy accounts hit different limits. The SEC-004 email binding is only implied |
| JD-4 | J-5 severe | J-5 severe (+ J-6 warning) | **severe** | The Cognito session sequence is replayed incompletely. `setUser(detached user)` is missing, so `finishLogin`'s refusal paths dereference a null `user` and NPE into a 500. `saveLastLogin` runs twice. The `agree_terms` handling Cognito performs is not decided |
| JD-5 | J-4 severe | J-10 warning | **severe** (severity split) | No interceptor stack is declared (requirements §8 asks for it). `homeJson`/`json-default` lacks `i18nFile` and `logErrorStatus`. A JSON result on a `LoginAction` subclass with no `root` or `includeProperties` would serialize every inherited getter, session and user included |

### Suspect (single judge, severe)

| ID | Judge | Finding |
|---|---|---|
| JS-1 | B J-3 | Consume is `WHERE consumed_at IS NULL` without `AND attempts < 3`. In a parallel burst, every request reads `attempts = 0`; wrong codes push the count to 3 while a correct one still consumes, so an exhausted challenge authenticates (FN-012). The session mirror is a non-atomic read-modify-write (A J-7 also flags the mirror, as a warning) |
| JS-2 | A J-6 | P-4 is mis-rated. Password login keeps no pre-auth state, so OTP is the first path after Cognito that needs affinity between requests. The Dockerfile ships memcached-session-manager. If it is active, `OtpPending` must be kryo-serializable and concurrent writes resolve as last-write-wins. Judge B confirmed P-4 as cited and noted that `ShiroSpringStartupListener:51` overrides the bean |

### Info (warnings and suggestions)

| ID | Source | Finding |
|---|---|---|
| JI-1 | A J-9, B J-12 | OPS-001 says "exactly one line per call", but the design adds `auth.otp.delivery` lines. The requirement and the design disagree |
| JI-2 | A J-10, B J-13 | The approved proposal says a synchronous send; DD-7 is async. The deviation is not recorded. OQ-5 is still open, yet the design fixes the real recipient |
| JI-3 | A J-11, B J-14 | SEC-008 requires ≥ 256 bits; DD-10 accepts 32 UTF-8 bytes, so 32 hex characters (128 bits) passes |
| JI-4 | A J-8, B J-7 | Executor queue-full behavior is undefined. A rejection after the row commit happens only for eligible accounts, which is an oracle and leaves an orphan challenge |
| JI-5 | A J-12, B J-17 | The trusted-proxy list has no CIDR or IPv6 handling. Missing or malformed `X-Forwarded-For` is undefined. Fixed windows allow 2× the limits. Victim lockout through the per-email buckets is not listed |
| JI-6 | A J-13, B J-20 | Focus-on-open and the explicit sending/verifying states are not in §6. The concurrency probe is absent from the design |
| JI-7 | B J-8 | A purge inside the issue transaction marks the transaction rollback-only on failure, so a purge failure blocks issuing (contradicts OPS-003) |
| JI-8 | B J-9 | `OtpRateLimitDAO` has no `@Transactional` manager, yet the limiter must run in one transaction |
| JI-9 | B J-11 | The second-level and query caches are per-JVM ehcache (TTL 3600 s, `ehcache.xml:28-29`). A `custom_parameters` change, including the rollback "empty the list", can lag up to 1 h per instance. P-10 is rated too low, and the FN-002 and §14 rollback claims overstate |
| JI-10 | B J-15 | DA-002 says "every Global Unit type"; the seed covers 1, 3 and 4 only |
| JI-11 | B J-16 | `otpEnabled` uses `isActive()`, so `crpByEmail.do` would newly disclose inactive status |
| JI-12 | B J-18 | Cooldown is select-then-insert. Concurrent requests from two sessions can leave two open challenges |
| JI-13 | B J-19 | P-1 line citation is imprecise (`login()` is `:297`, `login(User, GlobalUnit)` is `:401`). `isActiveFor` takes three arguments. Missing rows: JSON serialization, executor, kryo |
| JI-14 | A J-15 | `cooldownSeconds?` in the `sent` body has no emission rule, which risks a byte difference |
| JI-15 | A J-16 | Seed `format` 4 differs from Cognito's `format` 1 (P-11 relies on the local database). P-23 was partly settled by the judges: types 1–3 are seeded by `V2_5_0_20171027_1500__GlobalUnitTypes.sql` and types 1, 3, 4 by `V2_6_0_20260409_1645` |

## Premise Ledger results

| Row | Judge A | Judge B |
|---|---|---|
| P-1 | confirmed | confirmed, citation imprecise |
| P-4 | conclusion contradicted (JS-2) | confirmed |
| P-6 | refutation attempted, inconclusive, UNVERIFIED | same |
| P-10 | not re-run | confirmed as cited, per-JVM invalidation (JI-9) |
| P-18 | refutation attempted, plausible, UNVERIFIED | same |
| P-23 | evidence that types 1–3 are seeded | evidence that types 1, 3, 4 are seeded |
| P-25 | no repository source, UNVERIFIED | same |
| Others | confirmed or not re-run (database or tests) | confirmed or not re-run |

Count line (25 / 21 / 4 / 2 High / 2 Low) matches the table, according to both judges.

## Round 1 correction (2026-10-09)

The user authorised "Fix and Re-judge". The fixes covered every confirmed severe finding (JD-1 to JD-5), both suspects (JS-1, JS-2), and the low-cost info items JI-1, JI-2, JI-3, JI-4, JI-7, JI-8 and JI-9. The fix delta is the rewrite of `design.md` dated 2026-10-09, plus the amendments to FN-005, its scenario, OPS-001, DA-002, AC-4 and the Decision Log in `requirements.md`.

## Scoped re-judgment — round 1

Both judges re-ran blind (`sonnet`) over this ledger plus the fix delta.

| Finding | Judge A | Judge B |
|---|---|---|
| JD-1 | resolved | resolved |
| JD-2 | resolved | resolved |
| JD-3 | resolved | resolved |
| JD-4 | resolved | resolved |
| JD-5 | resolved | resolved |
| JS-1 | resolved | resolved |
| JS-2 | resolved | partially resolved (memcached serialization not addressed; moot, since Shiro native sessions are not container sessions — P-4) |
| JI-1 | partially resolved (AC-11 not amended) | resolved |
| JI-2, JI-3, JI-4, JI-7, JI-8, JI-9 | resolved | resolved |

**No severe finding remains, and neither judge reported a new severe finding.**

New info rows from the re-judgment (warnings and suggestions; not auto-fixed under this protocol):

| ID | Source | Finding |
|---|---|---|
| RJ-1 | A N-1, B N-1 | The 30-s cooldown is a fixed window, so two requests about 1 s apart across a window edge both pass. This contradicts FN-008 and AC-10 |
| RJ-2 | A N-3, B N-4 + N-3 | `finishLogin(user, …)` does not say whether `user` is the loaded entity or the detached field. Loading has no null-user guard. The SUCCESS redirect is `${crpSession}/crpDashboard` for every type; only type 2 returns LOGIN |
| RJ-3 | A N-2, B N-5 | `registerMismatch` with 0 affected rows answers `exhausted`, even when the row was concurrently superseded or consumed. It should answer `invalid`. Not an oracle |
| RJ-4 | A N-8, B N-6 | Seeding types 1, 3 and 4 leaves Center (type 2 on migration-only databases) without a catalog row |
| RJ-5 | A N-5, B N-8 | FN-016 still says a neutral request has no challenge. That wording is stale, and the closure sweep missed it |
| RJ-6 | A N-7, B N-9 | `crpByEmail.do` is unthrottled and now makes one uncached read per Global Unit per call; §12 omits this. DD-12 says "1-h TTL", but the entity caches use 5000 s |
| RJ-7 | A N-4 | AC-11 still says one `auth.otp.*` line per call |
| RJ-8 | B N-7 | P-21 is marked verified, although its contents were not read. It should be `UNVERIFIED`/Low, which makes the count 20 verified and 7 `UNVERIFIED` |
| RJ-9 | A N-6 | The `homeOtp` package has no `input` result, and the inherited `LoginAction` setters can be bound from the request. A global JSON `input` result and a parameter exclusion are needed |
| RJ-10 | B N-2 | Database work still differs slightly between eligible and ineligible requests (membership and SEC-009 reads). Timing is untested |
| RJ-11 | A N-9 | A third party can supersede a victim's real code once every 30 s. This is not listed among the accepted risks |

## Round history

- **Round 1:** judged; correction authorised and applied; scoped re-judgment shows all severe findings resolved.

**Terminal state: `approved`.** No severe finding remains after the round-1 correction. Rows RJ-1 to RJ-11 stay `info` and are handed to the user as an optional Adjust round.

JUDGMENT: APPROVED ✅
