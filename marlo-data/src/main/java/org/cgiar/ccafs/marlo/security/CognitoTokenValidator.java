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
 * Validates a Cognito ID token and produces either a validated {@link CognitoAssertion} or a typed
 * rejection.
 * <p>
 * Per design decision DD-5, token validation happens here -- never in the Shiro realm, which stays
 * I/O-free and receives only an already-validated {@link CognitoAssertion} carried by a
 * {@link CognitoAuthenticationToken}. This interface is where CHG-COGNITO-AUTH-001-T05 discharges
 * SEC-001 in full: every clause of that requirement corresponds to exactly one {@link RejectionReason}.
 * <p>
 * <b>Constitutional check: nothing that identifies a person or proves a session may reach a log
 * through this interface.</b> A conforming implementation never logs the raw token, a signature, or a
 * claim value that identifies a person -- it logs a {@link RejectionReason} instead, which by
 * construction carries none of those things.
 */
public interface CognitoTokenValidator {

  /**
   * Validates {@code idToken} against the configured Cognito User Pool: signature (against the pool's
   * JWKS), {@code iss}, {@code aud}, {@code exp} (with a small bounded clock-skew leeway), {@code nonce}
   * (against {@code expectedNonce}), and {@code token_use == "id"} (design.md 13.2).
   *
   * @param idToken the raw, compact-serialized ID token as returned by the token endpoint. A conforming
   *        implementation never logs this value
   * @param expectedNonce the nonce this caller bound to the authorization request, read from the
   *        pre-auth session (design.md DD-4). A token whose {@code nonce} claim is absent or does not
   *        match is rejected with {@link RejectionReason#NONCE_MISMATCH}
   * @return an accepted {@link Result} carrying the validated {@link CognitoAssertion}, or a rejected
   *         one carrying a {@link RejectionReason}. Never {@code null}
   */
  Result validate(String idToken, String expectedNonce);

  /**
   * Why a token was rejected. Deliberately coarse enough to log safely -- none of these values carry
   * token content, a signature, or a claim value -- and fine enough to satisfy design.md 11's "which
   * check failed" observability requirement (owned by T14; this interface only makes the reason
   * available).
   */
  enum RejectionReason {

    /** The token could not be parsed as a JWT, or its claims could not be read. */
    MALFORMED_TOKEN,

    /** The token carries no signature at all (e.g. an {@code alg=none} / plain JWT). */
    UNSIGNED_TOKEN,

    /** The token's {@code kid} names no key in the pool's JWKS, or the cryptographic check failed. */
    UNTRUSTED_SIGNING_KEY,

    /**
     * {@code token_use} was not {@code "id"}. Cognito signs ID and access tokens with the identical
     * JWKS (design.md 13.2), so this is the only check that tells them apart -- signature validity
     * alone does not.
     */
    UNEXPECTED_TOKEN_USE,

    /** {@code iss} did not match the configured pool. */
    UNEXPECTED_ISSUER,

    /** {@code aud} did not include the configured client id. */
    UNEXPECTED_AUDIENCE,

    /** {@code exp} was in the past, beyond the validator's bounded clock-skew leeway (R-D7). */
    TOKEN_EXPIRED,

    /** {@code nonce} was absent, or did not match the value bound to this authorization request. */
    NONCE_MISMATCH
  }

  /**
   * The outcome of a {@link #validate(String, String)} call: exactly one of an accepted assertion or a
   * rejection reason, never both, never neither.
   */
  final class Result {

    private final CognitoAssertion assertion;
    private final RejectionReason rejectionReason;

    private Result(CognitoAssertion assertion, RejectionReason rejectionReason) {
      this.assertion = assertion;
      this.rejectionReason = rejectionReason;
    }

    /**
     * @param assertion the validated identity. Required
     * @return an accepted result carrying {@code assertion}
     * @throws IllegalArgumentException if {@code assertion} is {@code null}
     */
    public static Result accepted(CognitoAssertion assertion) {
      if (assertion == null) {
        throw new IllegalArgumentException("assertion is required for an accepted result");
      }
      return new Result(assertion, null);
    }

    /**
     * @param reason why the token was rejected. Required
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
     * @return the validated assertion
     * @throws IllegalStateException if this result was rejected
     */
    public CognitoAssertion getAssertion() {
      if (this.assertion == null) {
        throw new IllegalStateException("no assertion on a rejected result; call isAccepted() first");
      }
      return this.assertion;
    }

    /**
     * @return why the token was rejected
     * @throws IllegalStateException if this result was accepted
     */
    public RejectionReason getRejectionReason() {
      if (this.rejectionReason == null) {
        throw new IllegalStateException("no rejection reason on an accepted result; call isAccepted() first");
      }
      return this.rejectionReason;
    }

    /**
     * @return {@code true} when the token validated and {@link #getAssertion()} is safe to call
     */
    public boolean isAccepted() {
      return this.assertion != null;
    }
  }
}
