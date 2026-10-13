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

package org.cgiar.ccafs.marlo.security.otp;

import java.io.Serializable;

/**
 * The session handle of an open one-time-code challenge (CHG-OTP-LOGIN-001-T05, SEC-004, JD-3), stored under
 * {@code APConstants.OTP_PENDING_CHALLENGE}.
 * <p>
 * It binds the challenge to the session that requested it, the email it was issued for and the Global Unit. It holds
 * the nonce, the normalised email, the Global Unit id, the return URL and the terms flag, and <b>never the code or
 * its MAC</b>: the client sends only the code, and the server loads the stored MAC by nonce. Immutable.
 */
public final class OtpPending implements Serializable {

  private static final long serialVersionUID = 1L;

  private final String nonce;
  private final String email;
  private final long globalUnitId;
  private final String returnUrl;
  private final boolean agree;

  /**
   * @param nonce the challenge nonce
   * @param email the normalised email the challenge was issued for
   * @param globalUnitId the Global Unit the challenge was issued for
   * @param returnUrl a validated same-origin return URL, or {@code null}
   * @param agree whether the user accepted the terms on the request
   */
  public OtpPending(String nonce, String email, long globalUnitId, String returnUrl, boolean agree) {
    this.nonce = nonce;
    this.email = email;
    this.globalUnitId = globalUnitId;
    this.returnUrl = returnUrl;
    this.agree = agree;
  }

  /**
   * @return the normalised email the challenge was issued for
   */
  public String getEmail() {
    return this.email;
  }

  /**
   * @return the Global Unit the challenge was issued for
   */
  public long getGlobalUnitId() {
    return this.globalUnitId;
  }

  /**
   * @return the challenge nonce
   */
  public String getNonce() {
    return this.nonce;
  }

  /**
   * @return the validated same-origin return URL, or {@code null}
   */
  public String getReturnUrl() {
    return this.returnUrl;
  }

  /**
   * @return whether the user accepted the terms on the request
   */
  public boolean isAgree() {
    return this.agree;
  }
}
