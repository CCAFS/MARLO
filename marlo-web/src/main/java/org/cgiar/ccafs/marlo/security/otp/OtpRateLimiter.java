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

import org.cgiar.ccafs.marlo.data.manager.OtpRateLimitManager;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.HexFormat;
import java.util.concurrent.atomic.AtomicReference;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The rate limits of the Center email one-time-code sign-in (CHG-OTP-LOGIN-001-T04, SEC-010, FN-008, DA-003, DD-8).
 * <p>
 * <b>Buckets.</b> A bucket is {@code HMAC-SHA256(k_bucket, "<route>|<dimension>|<value>")} in lower-case hex, so the
 * store never holds a raw email or address. The caller passes the email already normalised and the address already
 * resolved. The counters are fixed windows of 15 minutes aligned on the UTC clock:
 * <ul>
 * <li>request code: {@code request|email|<email>} 5, {@code request|ip|<ip>} 20;</li>
 * <li>verify code: {@code verify|email|<pending email>} 10, {@code verify|ip|<ip>} 50. With no pending challenge
 * only the address is counted;</li>
 * <li>cooldown, request code only: {@code cooldown|email|<email>}, a sliding interval of 30 seconds between two
 * accepted requests (RJ-1), not a window. It is decided under a row lock, so parallel requests serialize.</li>
 * </ul>
 * The buckets are counted in that order and the first refusal ends the check, so a refused request spends none of
 * the later buckets and never moves the cooldown.
 * <p>
 * <b>Fail closed.</b> A store failure of any kind, a lock or a deadlock included, answers
 * {@link Decision#UNAVAILABLE}, never an exception, and so does an unconfigured secret. Only the class of the
 * failure is logged: its message may carry the bucket.
 * <p>
 * <b>One time source.</b> The check reads the clock once and uses that {@code now} for every bucket and for the
 * purge cutoffs. The purge runs after the counting, throttled to one run per interval and per instance, in
 * transactions of its own, and a failure never reaches the caller.
 * <p>
 * It is a container singleton: {@code @Named}, found by the component scan. The purge throttle is instance state, so
 * it holds only while every action shares this one instance. Do not construct it in an action: inject it.
 */
@Named
public final class OtpRateLimiter {

  /** The verdict of one check. */
  public enum Decision {
    /** Within every limit. */
    ALLOWED,
    /** Over a window limit, or too soon after the last accepted request. */
    LIMITED,
    /** The store failed or the secret is not configured: refused, because the limits cannot be proven. */
    UNAVAILABLE;

    /**
     * @return true only for {@link #ALLOWED}
     */
    public boolean isAllowed() {
      return this == ALLOWED;
    }
  }

  private static final Logger LOG = LoggerFactory.getLogger(OtpRateLimiter.class);

  static final int REQUEST_EMAIL_LIMIT = 5;
  static final int REQUEST_IP_LIMIT = 20;
  static final int VERIFY_EMAIL_LIMIT = 10;
  static final int VERIFY_IP_LIMIT = 50;

  private static final long WINDOW_SECONDS = 15 * 60L;
  static final Duration COOLDOWN = Duration.ofSeconds(30);

  static final Duration WINDOW_RETENTION = Duration.ofDays(1);
  static final Duration COOLDOWN_RETENTION = Duration.ofHours(1);
  static final Duration PURGE_INTERVAL = Duration.ofMinutes(10);

  private static final String ROUTE_REQUEST = "request";
  private static final String ROUTE_VERIFY = "verify";
  private static final String ROUTE_COOLDOWN = "cooldown";
  private static final String DIMENSION_EMAIL = "email";
  private static final String DIMENSION_IP = "ip";

  private static final String MAC_ALGORITHM = "HmacSHA256";

  private final OtpKeys keys;
  private final OtpRateLimitManager manager;
  private final Clock clock;
  private final AtomicReference<Instant> lastPurge = new AtomicReference<>(Instant.EPOCH);

  /** The only constructor the container may use; the one below is the test seam. */
  @Inject
  public OtpRateLimiter(OtpKeys keys, OtpRateLimitManager manager) {
    this(keys, manager, Clock.systemUTC());
  }

  OtpRateLimiter(OtpKeys keys, OtpRateLimitManager manager, Clock clock) {
    this.keys = keys;
    this.manager = manager;
    this.clock = clock;
  }

  /** The 15-minute window of {@code now}: its start, a multiple of 15 minutes on the UTC clock. */
  static Instant windowStart(Instant now) {
    long epochSecond = now.getEpochSecond();
    return Instant.ofEpochSecond(epochSecond - Math.floorMod(epochSecond, WINDOW_SECONDS));
  }

  private static String bucket(byte[] key, String route, String dimension, String value) {
    try {
      Mac mac = Mac.getInstance(MAC_ALGORITHM);
      mac.init(new SecretKeySpec(key, MAC_ALGORITHM));
      String name = route + "|" + dimension + "|" + (value == null ? "" : value);
      return HexFormat.of().formatHex(mac.doFinal(name.getBytes(StandardCharsets.UTF_8)));
    } catch (GeneralSecurityException e) {
      // HmacSHA256 is a mandatory JCA algorithm and the key is non-empty: unreachable on a conforming JRE.
      throw new IllegalStateException("HmacSHA256 is unavailable", e);
    }
  }

  /**
   * Counts a code request (design 5.1 step 1): the email, the address, then the cooldown.
   *
   * @param normalisedEmail the email as {@code OtpDomainPolicy} normalises it; counted even when it is malformed,
   *        because the shape check comes after the limits.
   * @param clientIp the resolved client address.
   */
  public Decision checkRequestCode(String normalisedEmail, String clientIp) {
    if (!this.keys.isConfigured()) {
      return Decision.UNAVAILABLE;
    }
    Instant now = this.clock.instant();
    Decision decision;
    byte[] key = this.keys.bucketKey();
    try {
      decision = this.countWindow(key, ROUTE_REQUEST, DIMENSION_EMAIL, normalisedEmail, REQUEST_EMAIL_LIMIT, now);
      if (decision.isAllowed()) {
        decision = this.countWindow(key, ROUTE_REQUEST, DIMENSION_IP, clientIp, REQUEST_IP_LIMIT, now);
      }
      if (decision.isAllowed()) {
        decision = this.acceptCooldown(key, normalisedEmail, now);
      }
    } catch (RuntimeException e) {
      return this.failClosed(e);
    } finally {
      Arrays.fill(key, (byte) 0);
    }
    this.purgeStaleRows(now);
    return decision;
  }

  /**
   * Counts a code verification (design 5.2 step 1): the pending email, then the address.
   *
   * @param pendingEmail the normalised email of the session's pending challenge, or null when there is none. It
   *        comes from the session, so no lookup precedes the counting (JD-3).
   * @param clientIp the resolved client address.
   */
  public Decision checkVerifyCode(String pendingEmail, String clientIp) {
    if (!this.keys.isConfigured()) {
      return Decision.UNAVAILABLE;
    }
    Instant now = this.clock.instant();
    Decision decision = Decision.ALLOWED;
    byte[] key = this.keys.bucketKey();
    try {
      if (pendingEmail != null) {
        decision = this.countWindow(key, ROUTE_VERIFY, DIMENSION_EMAIL, pendingEmail, VERIFY_EMAIL_LIMIT, now);
      }
      if (decision.isAllowed()) {
        decision = this.countWindow(key, ROUTE_VERIFY, DIMENSION_IP, clientIp, VERIFY_IP_LIMIT, now);
      }
    } catch (RuntimeException e) {
      return this.failClosed(e);
    } finally {
      Arrays.fill(key, (byte) 0);
    }
    this.purgeStaleRows(now);
    return decision;
  }

  private Decision acceptCooldown(byte[] key, String normalisedEmail, Instant now) {
    // Sliding, not a fixed window: under the row lock the store moves the value to now only when it is at least
    // COOLDOWN older, so exactly one of several requests carrying the same now is accepted (RJ-1).
    boolean accepted = this.manager.acceptCooldown(bucket(key, ROUTE_COOLDOWN, DIMENSION_EMAIL, normalisedEmail), now,
      COOLDOWN);
    return accepted ? Decision.ALLOWED : Decision.LIMITED;
  }

  private Decision countWindow(byte[] key, String route, String dimension, String value, int limit, Instant now) {
    int hits = this.manager.hit(bucket(key, route, dimension, value), windowStart(now));
    return hits > limit ? Decision.LIMITED : Decision.ALLOWED;
  }

  private Decision failClosed(RuntimeException failure) {
    // Never the message or the stack trace: a lock or a constraint message may carry the bucket.
    LOG.warn("auth.otp.ratelimit outcome=store_failure cause={}", failure.getClass().getSimpleName());
    return Decision.UNAVAILABLE;
  }

  /**
   * Opportunistic purge of the stale rows (OPS-003 style): after the counting, so no bucket transaction is open, and
   * at most once per interval and per instance. It never throws.
   */
  private void purgeStaleRows(Instant now) {
    Instant last = this.lastPurge.get();
    if (now.isBefore(last.plus(PURGE_INTERVAL)) || !this.lastPurge.compareAndSet(last, now)) {
      return;
    }
    try {
      this.manager.purgeWindowsBefore(now.minus(WINDOW_RETENTION));
      this.manager.purgeCooldownsBefore(now.minus(COOLDOWN_RETENTION));
    } catch (RuntimeException e) {
      LOG.warn("auth.otp.ratelimit outcome=purge_failed cause={}", e.getClass().getSimpleName());
    }
  }
}
