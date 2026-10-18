# Password Login Switch — Judgment Day Ledger

| Field | Value |
|---|---|
| Target | `design.md` (Phase 2 draft) against `requirements.md` and `proposal.md`, uncommitted, code at `160ec7dc4a` |
| Mode | judgment_day, two blind read-only judges (`akili-reviewer`) |
| Round | 1 |
| Verdicts | Judge A: REJECT · Judge B: FAIL |
| State | **APPROVED** — 2026-10-16, after two correction rounds |

## Premise Ledger attack

Both judges confirmed P-1, P-3 to P-12 at their citations and every test count. **P-2 is contradicted by both:** the
grep reproduces (8 hits), but the claim "CLARISA is the only other password path" is false (C-1).

## Confirmed — both judges (severe)

| ID | Judge refs | Finding | Evidence |
|---|---|---|---|
| C-1 | A F-2 · B F-1 | `/api/**` accepts any user's password over HTTP Basic through `APCustomRealm` (DB or LDAP). The design and SEC-006 name CLARISA as the only path outside the login page; the required grep cannot see Shiro's own token | `MarloShiroConfiguration.java:140`; `APCustomRealm.java:117-119,179-187` |
| C-2 | A F-3 · B F-2 | DD-5 does not require the attempt's unit to be one the account belongs to. A POST naming any password-on unit bypasses the gate and `validateUser.do` returns an authenticated subject (the `globalUnitId=99999` defect SEC-005 already fixed) | `ValidateUserAction.java:246-266`; `LoginAction.java:192-198`; `UserManagerImp.java:151-157` |
| C-3 | A F-1 · B F-3 | An account typed as a **username** is locked out on a password-off unit: the code path refuses input with no domain, so FN-007 fails and §5's "cannot occur" row happens | `GlobalUnitMySQLDAO.java:45`; `CrpByUserEmailAction.java:101-104`; `OtpEligibility.java:111-113,159`; `OtpRequestCodeAction.java:370-373` |
| C-4 | A F-4 · B F-4 | DD-7 adds the new welcome key to 3 of the 8 custom files; UI-003 requires all of them | `requirements.md` UI-003; `design.md` DD-7; 9 files by grep |

## Confirmed — both judges (warning, info)

| ID | Judge refs | Finding |
|---|---|---|
| W-1 | A F-5 · B F-5 | Four tests lock T21's shape (two static locks over the whole file, the constructor signature, the Spring-scan test); DD-4's "allow only in the override branch" cannot be written as those tests are |
| W-2 | A F-14 · B F-6 | The list of `CHG-OTP-LOGIN-001` clauses to amend differs across documents; FN-005 and FN-006 are missing |
| W-3 | A F-12 · B F-7 | The fixed `system_otp_login_active` text says it applies only while the password is on — false for Cognito accounts (FN-005) |
| W-4 | A F-11 · B F-8 | OPS-002 asks ERROR once per start; the design keeps the WARN and logs a per-call ERROR from an unauthenticated endpoint |
| W-5 | A F-7 · B F-11 | The welcome helper has no injection path to the rule's inputs; which unit each sender passes is unstated |
| W-6 | A F-9 · B F-10 | `crpByEmail.do` computes `otpEnabled` before the per-unit Cognito value; the reorder is unstated |
| W-7 | A F-8 · B F-9 | `otpEnabledFor` exits before the query when the key is missing or there is no domain; `passwordEnabled` on failure is unspecified |
| W-8 | A F-10 · B F-13 | The code-only view must hide the back link, fill `#login-method-info`, and read the new attribute at both capture sites |
| W-9 | A F-16 · B F-16 | The OPS-003 runbook has no named home |

## Suspect — one judge (not auto-fixed)

| ID | Judge ref | Finding |
|---|---|---|
| S-1 | B F-12 | Placement of the Cognito refusal relative to `existActiveCrpUser` may add a distinguisher |
| S-2 | B F-14 | The password-switch refusal returns faster than a wrong password (timing) |
| S-3 | A F-15 | Asymmetric parsing: `0`, `off`, `no` leave the password on silently |
| S-4 | A F-6 | Constructor changes reach `LoginAction` subclasses and four Cognito test classes not in §17 |
| S-5 | B F-17 · A F-17 | Count drift ("four"/"five" consumers), stale Phase 1 status line, task IDs cited before `tasks.md` |

## Round 1 correction (2026-10-16)

User decision: "aplica la corrección con las tres recomendaciones". Applied by the parent orchestrator to
`requirements.md`, `design.md` and `proposal.md`:

| ID | Correction |
|---|---|
| C-1 | SEC-006 names `/api/**` Basic as out of scope (accepted risk); §13, §16, proposal §6 aligned; new premise P-13 |
| C-2 | DD-5 rewritten: the attempt's unit counts only if the account is a member; otherwise sweep every membership; unknown account and query failure defined; §13 drops "account-blind" and records timing as accepted (S-2) |
| C-3 | New FN-010, UI-005, scenario "Username typed, code only", AC-9 widened with the identifier axis, AC-15; §1 and §5 (new FN-010 view); P-14 |
| C-4 | UI-003 amended to `global` + `aicrra`, `aiccra3`, `test`; DD-7 aligned; Decision Log |
| W-1 | DD-4 reversion challenge lists the four tests and replaces them with behavioral zero-call tests; P-16 |
| W-2 | One amendment list everywhere: FN-001, FN-003, FN-005, FN-006, FN-015, SEC-009, DA-002, UI-006 |
| W-3 | DA-003 and the `system_otp_login_active` text: "apply only to users of Sign in with CGIAR" while the password is off |
| W-4 | OPS-002 and §11: ERROR at most once per Global Unit per start, not repeatable from an unauthenticated request |
| W-5 | DD-7: `@Named` bean injected into the 7 senders; unit per sender stated |
| W-6, W-7 | DD-6: reorder in `crpByEmail.do`, one `methodsFor` call, short paths and failure behavior; P-17, P-18 |
| W-8 | §5: code-only view hides the back link, fills the info text, reads the attribute at both capture sites |
| W-9 | §2: new `runbook.md` in this spec folder |
| S-1 | DD-4/§13: Cognito flag read before the lookup; `is_cgiar_user` refusal after `existActiveCrpUser` |
| S-4, S-5 | Consumers list widened; counts fixed (three production consumers, six rule consumers); status lines updated; task IDs no longer cited |
| — | Budget: 8 tasks, ~1,300 LOC, 10 rounds |

## Round 1 scoped re-judgment (2026-10-16)

Both judges: every item C-1…C-4, W-1…W-9, S-1, S-4, S-5 **resolved**; verdict FAIL on new findings.

| ID | Judge refs | Finding | Status |
|---|---|---|---|
| N-1 | A N-1 · B N-1 | **Confirmed severe.** The last-resort ERROR (OPS-002, AC-7) could not fire: DD-6 skipped the query when the key was missing and DD-5 read the switch only with a configured key | fixed in round 2 |
| N-2 | A N-6 · B N-2 | Timing rationale wrong after C-2 (refusal reveals membership in a password-off unit) | fixed in round 2 |
| N-3 | A N-3 · B N-6 | "five inputs" vs six AC-9 axes; "all three OFF shows the code" false for a username | fixed in round 2 |
| N-4 | A N-4 · B N-5 | Proposal leftovers of the old W-3 wording and a partial MODIFIED list | fixed in round 2 |
| N-5 | A N-5 · B N-3 | Fail-open on query failure not recorded against SEC-001 | fixed in round 2 |
| N-6 | A N-8 · B N-7 | Blank line splits P-13…P-18 from the ledger table | fixed in round 2 |
| N-7 | B N-4 (one judge) | DA-003 texts keep absolute sentences contradicted by their own exception | fixed in round 2 (texts reworded) |
| N-8 | A N-2 (one judge) | §13 says `passwordEnabled` depends on the identifier's shape | fixed in round 2 |
| N-9 | A N-7 (one judge) | P-17 cites `:339`, a failure test; no-domain early exit unaddressed | fixed in round 2 |
| N-10 | B N-7 (one judge) | §12 understates the gate's cost; DD-6 needs two passes; stray backtick | fixed in round 2 |

## Round 2 correction (final, 2026-10-16)

Applied by the parent orchestrator (round two needs no new user approval under the protocol):
DD-6 rewritten (the settings query always runs; the early exit at `OtpEligibility.java:159-161` and its lock at
`CrpByUserEmailActionTest.java:314` are replaced; two passes in `crpByEmail.do`; the ERROR is decided there); DD-5 adds
the key-missing path and names the fail-open exception; requirements Decision Log records N-1, the fail-open exception
and the accepted timing; §13 timing and disclosure rewritten; §12 cost stated; DA-003 texts reworded with an explicit
"Exception:" sentence; proposal §4, §5, §9, §13 aligned; ledger table joined; P-17 corrected.

## Final scoped re-judgment (round 2, 2026-10-16)

| Judge | Severe | Result |
|---|---|---|
| A | 0 new | N-1, N-2, N-5…N-10 resolved; N-3, N-4 one proposal sentence each; R2-1 warning (NF-002 wording) |
| B | 0 new | N-1, N-2, N-4…N-10 resolved; N-3 one proposal sentence; NW-1 (SEC-001 note), NW-2 (NF-002 wording), NW-3 (DA-003 text vs last resort) warnings |

No confirmed severe finding remains. The remaining warnings are `info` under the protocol; the parent applied them as
one-line text edits without a further judgment round (none is severe, and the round ceiling is reached):
proposal §4 username caveat and §7 wording; design Executive Summary and §12 "at most one query"; requirements SEC-001
fail-open note and NF-002 note; DA-003 `system_otp_login_active` text adds "and the server has a code key".

**JUDGMENT: APPROVED ✅** — no confirmed severe finding open after round 2; info items applied, not re-judged.
