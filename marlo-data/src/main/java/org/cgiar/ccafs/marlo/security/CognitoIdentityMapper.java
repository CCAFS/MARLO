/*****************************************************************
 * This file is part of Managing Agricultural Research for Learning &
 * Outcomes Platform (MARLO).
 * MARLO is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 * MARLO is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with MARLO. If not, see <http://www.gnu.org/licenses/>.
 *****************************************************************/

package org.cgiar.ccafs.marlo.security;

/**
 * Resolves a validated {@link CognitoAssertion} to a MARLO {@code users} row and applies gates 1-3 of
 * design.md 13.1, in order.
 * <p>
 * <b>Why this is a collaborator distinct from {@link CognitoTokenValidator}.</b> T05's validator is
 * deliberately I/O-narrow: it fetches JWKS and performs cryptographic checks, and every one of its tests
 * runs fully offline with no network and no database (its own javadoc and test suite make that a design
 * constraint, not an accident). Identity mapping is a different concern -- it reads the {@code users}
 * table through {@link org.cgiar.ccafs.marlo.data.manager.UserManager} and, on gate success, writes
 * {@code users.username} through it. Folding that into the token validator would make "pure unit, no
 * network" false for a class whose own contract promises it. It is design.md 13.3's step ③, distinct from
 * step ② (token validation, T05) and step ⑥ ({@code Subject.login}, T06) -- {@link CognitoAuthenticationToken}
 * already requires a resolved {@code users.id} in its constructor, which makes this resolution a
 * precondition of building that token, not something the realm or the validator can do for it.
 * <p>
 * <b>The three gates, in order (design.md 13.1):</b>
 * <ol>
 * <li>A {@code users} row exists for the mapped claim -- {@code MUST NOT} auto-provision (FN-002).</li>
 * <li>{@code users.is_cgiar_user = 1} -- SEC-006: a federated identity {@code MUST NOT} unlock a local
 * account.</li>
 * <li>{@code users.is_active} is authoritative regardless of IdP state.</li>
 * </ol>
 * Gate 4, {@code crp_users} membership, is deliberately <b>not</b> here: design.md 13.1 places it inside
 * {@code finishLogin}, after the session for the selected Global Unit exists.
 * <p>
 * <b>SEC-006's non-disclosure clause is a caller obligation, not this interface's.</b> {@link
 * RejectionReason#ACCOUNT_NOT_FOUND} and {@link RejectionReason#NOT_CGIAR_ACCOUNT} are returned as
 * distinct values so a caller can log which gate failed (OPS-001, T14) -- but {@link
 * RejectionReason#toMessageKey()} maps both to the identical i18n key, matching how T08's {@code
 * CognitoLoginAction} already folds its own two analogous pre-filter refusals into one key on this same
 * Cognito path. Folding them into one enum value here would make that observability impossible; the
 * distinction separates "what a machine may know" from "what a browser may see".
 */
public interface CognitoIdentityMapper {

  /**
   * Resolves {@code assertion} to a {@code users} row and applies gates 1-3.
   *
   * @param assertion the already-validated identity (T05's output). Required
   * @return an accepted {@link Result} carrying the resolved {@code users.id}, or a rejected one carrying
   *         which gate failed. Never {@code null}
   * @throws IllegalArgumentException if {@code assertion} is {@code null}
   */
  Result map(CognitoAssertion assertion);

  /**
   * Which gate refused the mapping. Coarse by design -- see the class javadoc on why {@link
   * #ACCOUNT_NOT_FOUND} and {@link #NOT_CGIAR_ACCOUNT} exist as distinct values without licensing a
   * caller to expose that distinction to the person who just authenticated.
   */
  enum RejectionReason {

    /** Gate 1: no {@code users} row matches the mapped claim. FN-002 forbids auto-provisioning one. */
    ACCOUNT_NOT_FOUND,

    /** Gate 2: the row exists but {@code is_cgiar_user = 0} (SEC-006). */
    NOT_CGIAR_ACCOUNT,

    /** Gate 3: the row exists, is a CGIAR account, but {@code is_active} is {@code false}. */
    USER_DISABLED;

    /**
     * The i18n key -- never resolved text -- this rejection renders as (hard rule 8: no hardcoded
     * user-facing strings, and nothing in {@code marlo-data} resolves a key against a locale).
     * <p>
     * SEC-006 requires {@link #ACCOUNT_NOT_FOUND} and {@link #NOT_CGIAR_ACCOUNT} to be indistinguishable
     * to the person who just authenticated -- "no such row" and "row exists but is not a CGIAR account"
     * must not be told apart from the outside, or a federated credential attempt would itself disclose
     * which authentication mode an email uses. Both therefore share the identical key T08's
     * {@code CognitoLoginAction} already uses for its own two analogous pre-filter refusals on this same
     * Cognito path -- {@code login.error.cognitoNotEligible} -- rather than {@code ValidateUserAction}'s
     * {@code ADLoginMessages}, which is the legacy local-password path and speaks of a password no
     * Cognito user ever types. {@link #USER_DISABLED} keeps its own distinct key, {@code
     * login.error.inactive}, both verified present in {@code global.properties}.
     *
     * @return the i18n key a caller resolves with {@code getText(...)}, as T09's action does. Never
     *         {@code null} or blank
     */
    public String toMessageKey() {
      if (this == USER_DISABLED) {
        return "login.error.inactive";
      }
      return "login.error.cognitoNotEligible";
    }
  }

  /**
   * The outcome of a {@link #map(CognitoAssertion)} call: exactly one of a resolved {@code users.id} or a
   * rejection reason, never both, never neither.
   */
  final class Result {

    private final Long userId;
    private final RejectionReason rejectionReason;

    private Result(Long userId, RejectionReason rejectionReason) {
      this.userId = userId;
      this.rejectionReason = rejectionReason;
    }

    /**
     * @param userId the resolved {@code users.id}. Required
     * @return an accepted result carrying {@code userId}
     * @throws IllegalArgumentException if {@code userId} is {@code null}
     */
    public static Result accepted(Long userId) {
      if (userId == null) {
        throw new IllegalArgumentException("userId is required for an accepted result");
      }
      return new Result(userId, null);
    }

    /**
     * @param reason which gate refused the mapping. Required
     * @return a rejected result carrying {@code reason}
     * @throws IllegalArgumentException if {@code reason} is {@code null}
     */
    public static Result rejected(RejectionReason reason) {
      if (reason == null) {
        throw new IllegalArgumentException("reason is required for a rejected result");
      }
      return new Result(null, reason);
    }

    /**
     * @return which gate refused the mapping
     * @throws IllegalStateException if this result was accepted
     */
    public RejectionReason getRejectionReason() {
      if (this.rejectionReason == null) {
        throw new IllegalStateException("no rejection reason on an accepted result; call isAccepted() first");
      }
      return this.rejectionReason;
    }

    /**
     * @return the resolved {@code users.id}
     * @throws IllegalStateException if this result was rejected
     */
    public Long getUserId() {
      if (this.userId == null) {
        throw new IllegalStateException("no userId on a rejected result; call isAccepted() first");
      }
      return this.userId;
    }

    /**
     * @return {@code true} when all three gates passed and {@link #getUserId()} is safe to call
     */
    public boolean isAccepted() {
      return this.userId != null;
    }
  }
}
