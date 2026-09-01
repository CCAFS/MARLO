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

import org.apache.shiro.authc.AuthenticationToken;

/**
 * The Shiro token that carries an already-validated {@link CognitoAssertion} into the realm.
 * <p>
 * Its only job is to be a distinct <i>type</i>. {@code APCustomRealm.doGetAuthenticationInfo} casts its
 * argument to {@code UsernamePasswordToken} unconditionally today; the Cognito path is introduced by an
 * {@code instanceof} guard placed <b>above</b> that cast, leaving the entire local-login path below it
 * byte-for-byte unchanged (design §2.1, DD-1). A token type is what makes that dispatch possible without
 * a second realm, which would change how <i>every</i> login resolves, local ones included.
 * <p>
 * <b>{@code getPrincipal()} returns the {@code users.id}, not the assertion.</b> That is a hard invariant
 * of this codebase, not a style choice: MARLO's principal consumers all assume a {@code Long}, and none of
 * them is guarded. {@code AddUserIdFilter} casts {@code subject.getPrincipal()} to {@code Long} on every
 * non-static request, {@code APCustomRealm.doGetAuthorizationInfo} does the same on every permission check,
 * {@code AddSessionToRestRequestFilter} does it for {@code /api/**}, and {@code AbstractMarloDAO} does it
 * inside a {@code catch (Exception)} — where a wrong type does not even throw, it silently nulls
 * {@code created_by} / {@code modified_by}. An earlier revision of this class returned the assertion from
 * {@code getPrincipal()}, which made the very first request after a successful Cognito login — the
 * dashboard redirect — fail with {@code ClassCastException}. See {@code design.md} §2.1.
 * <p>
 * This is <b>not</b> OQ-9. OQ-9 asks which Cognito <i>claim</i> joins to the {@code users} row; this is
 * about what Shiro carries afterwards, and the answer is the same on every authentication path MARLO has.
 * <p>
 * <b>Requiring the id in the constructor enforces the design's own ordering.</b> {@code design.md} §13.3
 * resolves the identity and applies gates 1-3 at step ③, before {@code Subject.login(...)} at step ⑥.
 * (Gate 4, {@code crp_users} membership, is not one of them — it lands later, inside {@code finishLogin}.)
 * A token that cannot be built without a resolved id makes that sequence structural rather than
 * conventional — this type cannot exist for a person who has not already passed gates 1-3.
 * <p>
 * {@code getCredentials()} returns the assertion: under DD-5 the proof was verified before this object
 * could exist, so the assertion <i>is</i> the credential the realm accepts.
 * <p>
 * Immutable, like the assertion it wraps.
 */
public final class CognitoAuthenticationToken implements AuthenticationToken {

  private static final long serialVersionUID = 2L;

  private final CognitoAssertion assertion;
  private final Long userId;

  /**
   * @param assertion the validated identity. Required
   * @param userId the {@code users.id} this assertion already resolved to. Required — see the class note
   * @throws IllegalArgumentException if either argument is {@code null}
   */
  public CognitoAuthenticationToken(CognitoAssertion assertion, Long userId) {
    if (assertion == null) {
      throw new IllegalArgumentException("assertion is required");
    }
    if (userId == null) {
      throw new IllegalArgumentException("userId is required");
    }
    this.assertion = assertion;
    this.userId = userId;
  }

  /**
   * @return the validated assertion. Never {@code null}
   */
  public CognitoAssertion getAssertion() {
    return this.assertion;
  }

  /**
   * @return the validated assertion — see the class note on why this is not a separate credential
   */
  @Override
  public Object getCredentials() {
    return this.assertion;
  }

  /**
   * @return the {@code users.id}, matching what every other authentication path in MARLO puts here.
   *         Never {@code null} — see the class note on why this is not the assertion
   */
  @Override
  public Object getPrincipal() {
    return this.userId;
  }

  /**
   * @return the {@code users.id} this assertion resolved to. Never {@code null}
   */
  public Long getUserId() {
    return this.userId;
  }

  @Override
  public String toString() {
    return "CognitoAuthenticationToken[" + this.assertion + "]";
  }
}
