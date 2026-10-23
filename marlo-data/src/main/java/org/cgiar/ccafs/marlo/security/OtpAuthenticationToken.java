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
 * The Shiro token that carries an already-verified email-code sign-in into the realm.
 * <p>
 * Like {@link CognitoAuthenticationToken}, its only job is to be a distinct <i>type</i>, so that
 * {@code APCustomRealm.doGetAuthenticationInfo} can dispatch on it with an {@code instanceof} guard placed
 * <b>above</b> its unconditional {@code UsernamePasswordToken} cast, leaving the password and Cognito paths
 * untouched (design §5.4).
 * <p>
 * <b>{@code getPrincipal()} returns the {@code users.id}.</b> Every principal consumer in MARLO casts to
 * {@code Long} without a guard, so any other type fails on the first request after login. The id is required
 * in the constructor: the code was already verified and the user already resolved before this object could
 * be built, so it cannot exist for a person who has not passed the verification.
 * <p>
 * <b>The token carries no secret.</b> It never holds the code, the nonce or the MAC. The realm hands the
 * token itself back as the credentials, which is a non-secret marker of the verified sign-in.
 * <p>
 * Immutable.
 */
public final class OtpAuthenticationToken implements AuthenticationToken {

  private static final long serialVersionUID = 1L;

  private final Long userId;

  /**
   * @param userId the {@code users.id} the verified code resolved to. Required
   * @throws IllegalArgumentException if {@code userId} is {@code null}
   */
  public OtpAuthenticationToken(Long userId) {
    if (userId == null) {
      throw new IllegalArgumentException("userId is required");
    }
    this.userId = userId;
  }

  /**
   * @return this token: a non-secret marker of the verified sign-in. Never {@code null}
   */
  @Override
  public Object getCredentials() {
    return this;
  }

  /**
   * @return the {@code users.id}, matching what every other authentication path in MARLO puts here.
   *         Never {@code null}
   */
  @Override
  public Object getPrincipal() {
    return this.userId;
  }

  /**
   * @return the {@code users.id} the verified code resolved to. Never {@code null}
   */
  public Long getUserId() {
    return this.userId;
  }

  @Override
  public String toString() {
    return "OtpAuthenticationToken[userId=" + this.userId + "]";
  }
}
