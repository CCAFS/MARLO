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

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * An identity that has <b>already been validated</b> against the Cognito User Pool.
 * <p>
 * Per design decision DD-5, token validation happens in the callback action and never in the Shiro realm:
 * a realm that performs network calls is untestable, as {@code LDAPAuthenticator} demonstrates today. The
 * realm therefore receives one of these — a plain value object — and every SEC-001 rejection case becomes
 * a pure unit test with no network, no container and no mock server.
 * <p>
 * <b>The raw ID token is deliberately not carried.</b> This object exists only after the signature,
 * issuer, audience, expiry, nonce and {@code token_use} checks have all passed, so the token has no
 * further use; holding it would widen the exposure of a live credential for no gain.
 * <p>
 * <b>The name of the identity claim is not carried either</b>, and that is a scope decision rather than
 * an oversight. Which claim is authoritative — {@code sub}, {@code oid} or email — is <b>OQ-9</b>, owned
 * by CHG-COGNITO-AUTH-001-T07. Whatever T07 decides applies to every assertion identically, so the claim
 * name is a constant of the deployment, not per-assertion data.
 * <p>
 * Instances are immutable: the class is {@code final}, every field is {@code final}, no mutator exists,
 * and every field type ({@link String}, {@link Instant}) is itself immutable.
 * <p>
 * <b>{@link Serializable} is required, not decorative.</b> Shiro's {@code AuthenticationToken} extends
 * {@code Serializable}, and {@link CognitoAuthenticationToken} holds one of these — so a non-serializable
 * assertion would make that token a broken implementation of the interface it declares. Nothing in MARLO
 * serializes it today (an in-memory {@code MemorySessionDAO}, no {@code CacheManager}), which is exactly
 * why the gap could go unnoticed: it surfaces only once authentication caching, a clustered session store,
 * or Tomcat session persistence is switched on. Every field type is already serializable.
 */
public final class CognitoAssertion implements Serializable {

  private static final long serialVersionUID = 1L;

  private final String identityClaim;
  private final String email;
  private final String usernameClaim;
  private final Instant issuedAt;

  /**
   * @param identityClaim the value of the claim that identifies this person stably (OQ-9 decides which
   *        claim that is). Required
   * @param email the person's email address as asserted by the pool. Required
   * @param usernameClaim the corporate username claim, or {@code null} when the identity provider does
   *        not map one. <b>Optional by design</b> — whether {@code sAMAccountName} is mapped at
   *        federation time is OQ-18, and it is not MARLO's to decide
   * @param issuedAt when the pool issued the token this assertion came from. Required
   * @throws IllegalArgumentException if a required value is missing or blank
   */
  public CognitoAssertion(String identityClaim, String email, String usernameClaim, Instant issuedAt) {
    // A blank identity or email must fail loudly here rather than reach the realm and resolve to
    // "no such user", which would read as a login failure instead of the coding error it is.
    if (identityClaim == null || identityClaim.trim().isEmpty()) {
      throw new IllegalArgumentException("identityClaim is required");
    }
    if (email == null || email.trim().isEmpty()) {
      throw new IllegalArgumentException("email is required");
    }
    if (issuedAt == null) {
      throw new IllegalArgumentException("issuedAt is required");
    }
    this.identityClaim = identityClaim.trim();
    this.email = email.trim();
    this.usernameClaim = usernameClaim == null || usernameClaim.trim().isEmpty() ? null : usernameClaim.trim();
    this.issuedAt = issuedAt;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (other == null || this.getClass() != other.getClass()) {
      return false;
    }
    CognitoAssertion that = (CognitoAssertion) other;
    return this.identityClaim.equals(that.identityClaim) && this.email.equals(that.email)
      && Objects.equals(this.usernameClaim, that.usernameClaim) && this.issuedAt.equals(that.issuedAt);
  }

  /**
   * @return the email address the pool asserted. Never {@code null} or blank
   */
  public String getEmail() {
    return this.email;
  }

  /**
   * @return the stable identifying claim value. Never {@code null} or blank
   */
  public String getIdentityClaim() {
    return this.identityClaim;
  }

  /**
   * @return when the pool issued the underlying token. Never {@code null}
   */
  public Instant getIssuedAt() {
    return this.issuedAt;
  }

  /**
   * @return the corporate username claim, or {@code null} when the provider mapped none (OQ-18)
   */
  public String getUsernameClaim() {
    return this.usernameClaim;
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.identityClaim, this.email, this.usernameClaim, this.issuedAt);
  }

  /**
   * Renders the email and issue time only. The identity claim is left out on purpose: it is the join key
   * to a MARLO account, and this value lands in logs.
   */
  @Override
  public String toString() {
    return "CognitoAssertion[email=" + this.email + ", issuedAt=" + this.issuedAt + "]";
  }
}
