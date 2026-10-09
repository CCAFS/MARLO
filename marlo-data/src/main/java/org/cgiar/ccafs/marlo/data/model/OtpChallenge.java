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

package org.cgiar.ccafs.marlo.data.model;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * One row of {@code otp_challenges}: a code request of the Center email one-time-code sign-in, decoys included
 * (CHG-OTP-LOGIN-001, DD-5). The row holds only keyed MACs, never the code or the email (SEC-001, SEC-002), and it is
 * neither auditable nor soft-deletable.
 * <p>
 * <b>Time.</b> Every {@code DATETIME} of the table is a <b>UTC wall clock</b>, written from the Java clock and never
 * from the database clock, so an application and a database in different time zones cannot shift an expiry. The
 * fields hold that wall clock as a {@link LocalDateTime}; the accessors expose it as an {@link Instant}.
 * <p>
 * Rows are written only by native statements of {@code OtpChallengeMySQLDAO}. This mapping is for reading.
 */
public class OtpChallenge extends MarloBaseEntity {

  private static final long serialVersionUID = 4021685913777521081L;

  /** The wrong codes after which a challenge is exhausted (FN-012). The consume and increment conditions use it. */
  public static final int MAX_ATTEMPTS = 3;

  private String nonce;
  private String emailHmac;
  private Long globalUnitId;
  private String codeHmac;
  private LocalDateTime expiresAt;
  private int attempts;
  private LocalDateTime consumedAt;
  private LocalDateTime createdAt;

  public OtpChallenge() {
  }

  /** @return the number of wrong codes registered so far. */
  public int getAttempts() {
    return this.attempts;
  }

  /** @return the hex MAC of {@code nonce|code}. */
  public String getCodeHmac() {
    return this.codeHmac;
  }

  /** @return when the challenge was consumed or superseded, or null while it is neither (DD-6). */
  public Instant getConsumedAt() {
    return toInstant(this.consumedAt);
  }

  public Instant getCreatedAt() {
    return toInstant(this.createdAt);
  }

  /** @return the hex MAC of the normalised email. */
  public String getEmailHmac() {
    return this.emailHmac;
  }

  public Instant getExpiresAt() {
    return toInstant(this.expiresAt);
  }

  public Long getGlobalUnitId() {
    return this.globalUnitId;
  }

  public String getNonce() {
    return this.nonce;
  }

  public void setAttempts(int attempts) {
    this.attempts = attempts;
  }

  public void setCodeHmac(String codeHmac) {
    this.codeHmac = codeHmac;
  }

  public void setConsumedAt(Instant consumedAt) {
    this.consumedAt = toUtcWallClock(consumedAt);
  }

  public void setCreatedAt(Instant createdAt) {
    this.createdAt = toUtcWallClock(createdAt);
  }

  public void setEmailHmac(String emailHmac) {
    this.emailHmac = emailHmac;
  }

  public void setExpiresAt(Instant expiresAt) {
    this.expiresAt = toUtcWallClock(expiresAt);
  }

  public void setGlobalUnitId(Long globalUnitId) {
    this.globalUnitId = globalUnitId;
  }

  public void setNonce(String nonce) {
    this.nonce = nonce;
  }

  private static Instant toInstant(LocalDateTime utcWallClock) {
    return utcWallClock == null ? null : utcWallClock.toInstant(ZoneOffset.UTC);
  }

  private static LocalDateTime toUtcWallClock(Instant instant) {
    return instant == null ? null : LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
  }
}
