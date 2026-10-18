#!/usr/bin/env bash
# CHG-PWD-SWITCH-001-T08 -- the closure sweeps of the cross-spec amendment (KZ-auth-flow-1, KZ-directory-abstraction-1).
# Forward: every family of sentences the amendment supersedes, over the live documents of CHG-OTP-LOGIN-001 and of this spec.
#   A hit on a line that carries "2026-10-17" or "CHG-PWD-SWITCH-001" is an amendment note or a correction note (tagged [note]);
#   any other hit is tagged [CHECK] and must be judged by hand: it is either a surviving sibling or an intentional statement.
# Backward: who cites each amended clause, outside the two spec folders and inside them.
# Usage: probes/sweeps.sh   (from anywhere; prints to stdout)
set -uo pipefail
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../../../../.." && pwd)"
cd "$ROOT"
O=docs/specs/changes/center-email-otp-login
P=docs/specs/changes/password-login-switch
LIVE="$O/requirements.md $O/design.md $O/runbook.md $O/probes/README.md $P/requirements.md $P/design.md $P/runbook.md"
HIST="$O/proposal.md $O/tasks.md $O/execution.md $O/judgment.md $P/proposal.md $P/tasks.md $P/execution.md $P/judgment.md"
sweep() { # sweep <label> <ERE>
  echo; echo "### FORWARD $1"; echo "grep -n -iE '$2' <live documents>"
  for f in $LIVE; do
    grep -n -iE "$2" "$f" | while IFS= read -r line; do
      case "$line" in *2026-10-17*|*CHG-PWD-SWITCH-001*) tag="[note] ";; *) tag="[CHECK]";; esac
      echo "$tag $f:${line:0:190}"
    done
  done
  echo "historical, append-only (counted, not edited): $(for f in $HIST; do printf '%s=%s ' "$(basename "$(dirname "$f")")/$(basename "$f")" "$(grep -c -iE "$2" "$f")"; done)"
}
sweep "S1 enabled-by-listing-domains and applies-only-while-password-on" 'listing domains|Emptying the list|enabled per Global Unit by|only while password sign-in is on|applies only while password'
sweep "S2 password and Cognito logins do not change (FN-019)" 'Password and Cognito (logins|paths) do not change|exactly as before|do not change behavior|logins are unaffected'
sweep "S3 not enabled for this email (FN-001, FN-006)" 'domainNotEnabled|not enabled for this email|Not enabled for this project|Off or absent MUST disable'
sweep "S4 eligibility never reads is_cgiar_user (SEC-009)" 'plays no part|MUST NOT depend on|no longer read by the code path|is_cgiar_user. no longer|nobody can'
sweep "S5 single method shown directly (UI-006)" 'single method|shown directly|every single-method case|only when the feature is configured'
echo; echo "### BACKWARD B1 full clause ids, anywhere in the repository docs, reports, guides and sources"
echo "grep -rnE 'CHG-OTP-LOGIN-001-(FN-001|FN-003|FN-005|FN-006|FN-015|FN-019|SEC-009|DA-002|UI-001|UI-006)' docs reports AGENTS.md CLAUDE.md marlo-web/src marlo-data/src"
grep -rnE 'CHG-OTP-LOGIN-001-(FN-001|FN-003|FN-005|FN-006|FN-015|FN-019|SEC-009|DA-002|UI-001|UI-006)' docs reports AGENTS.md CLAUDE.md marlo-web/src marlo-data/src 2>/dev/null | cut -c1-150
echo; echo "### BACKWARD B2 short ids, files outside docs/specs/changes/{center-email-otp-login,password-login-switch} (candidate citers)"
for id in FN-001 FN-003 FN-005 FN-006 FN-015 FN-019 SEC-009 DA-002 UI-001 UI-006; do
  printf '%s: ' "$id"
  grep -rlw "$id" reports AGENTS.md CLAUDE.md docs/trd docs/prd.md docs/infrastructure.md docs/ux-ui marlo-web/src marlo-data/src 2>/dev/null | tr '\n' ' '
  echo
done
echo; echo "### BACKWARD B3 citers of the OTP rules in code comments and templates"
grep -rnE 'SEC-009|plays no part|FN-015|CHG-OTP-LOGIN-001' marlo-web/src/main marlo-data/src/main 2>/dev/null | grep -vE 'OtpEligibility.java|SignInMethodRule.java|OtpLog.java' | cut -c1-170 | head -60
echo; echo "### BACKWARD B4 readers of the password switch: the CLARISA filter, HTTP Basic (APCustomRealm, UserManagerImp) and the Shiro configuration must not be among them (SEC-006)"
echo "grep -rlE 'PASSWORD_LOGIN_ACTIVE|passwordGate|SignInMethodRule' marlo-web/src/main marlo-data/src/main"
grep -rlE 'PASSWORD_LOGIN_ACTIVE|passwordGate|SignInMethodRule' marlo-web/src/main marlo-data/src/main 2>/dev/null
echo "files on the Basic / CLARISA path that do NOT appear above: ClarisaPublicAccesFilter.java APCustomRealm.java UserManagerImp.java MarloShiroConfiguration.java WebAppInitializer.java"
for f in ClarisaPublicAccesFilter APCustomRealm UserManagerImp MarloShiroConfiguration WebAppInitializer; do
  printf '  %s: %s\n' "$f" "$(grep -rlE 'PASSWORD_LOGIN_ACTIVE|passwordGate|SignInMethodRule|OtpEligibility' $(find marlo-web/src/main marlo-data/src/main -name "$f.java") 2>/dev/null | wc -l | tr -d ' ') reference(s)"
done
