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
import org.cgiar.ccafs.marlo.security.otp.OtpRateLimiter.Decision;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.LoggerFactory;
import org.springframework.dao.CannotAcquireLockException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * CHG-OTP-LOGIN-001-T04: {@link OtpRateLimiter} (SEC-010, FN-008, DA-003) against a hand-written fake manager.
 * <p>
 * The fake stands for what the database answers: a counter per {@code (bucket, window_start)}, and a cooldown whose
 * row lock serializes the requests of one bucket and moves the stored value to {@code now} only when it is at least
 * the spacing older. The statements themselves are proven by the scripted run on the throwaway database, not here.
 * <p>
 * Every expected number comes from the requirements (5, 20, 10, 50 per 15 min, 30 s apart) and every expected
 * bucket is a literal produced outside this code base with {@code openssl dgst -sha256 -mac HMAC -macopt hexkey:}
 * and {@code python3} (the two agreed), keyed by the {@code k_bucket} of the secret {@code 00 01 02 ... 1f}.
 * <p>
 * Each cooldown case uses <b>two different timestamps</b>: a test that gives both requests one instant cannot see
 * the edge of a window.
 */
public class OtpRateLimiterTest {

  /** base64 of the 32 bytes 0x00..0x1f. */
  private static final String SECRET_32_BYTES = "AAECAwQFBgcICQoLDA0ODxAREhMUFRYXGBkaGxwdHh8=";

  private static final String EMAIL = "user@partner.org";
  private static final String IP = "203.0.113.7";

  /** HMAC-SHA256(k_bucket, "request|email|user@partner.org"), hex. */
  private static final String REQUEST_EMAIL_BUCKET = "95e043b05c7ac7ad370f298183e8857b3bee8a7a434f0493724a81174479542b";
  /** HMAC-SHA256(k_bucket, "request|ip|203.0.113.7"), hex. */
  private static final String REQUEST_IP_BUCKET = "31a2de6fe71d81299f2cdcf33bc85f32bd0dd15262fcff3a82eec4d09009b06e";
  /** HMAC-SHA256(k_bucket, "cooldown|email|user@partner.org"), hex. */
  private static final String COOLDOWN_BUCKET = "505b1a15471ed3fa3c2235f69e6646d4ecd8ecaddbedfaafe206e18f42aa53a1";
  /** HMAC-SHA256(k_bucket, "verify|email|user@partner.org"), hex. */
  private static final String VERIFY_EMAIL_BUCKET = "bd939f880607cef71b88726f498a58a03b1ef0b9067959026a8ab2ab93881c5c";
  /** HMAC-SHA256(k_bucket, "verify|ip|203.0.113.7"), hex. */
  private static final String VERIFY_IP_BUCKET = "c00955a96e9f5672528e4b6d96bec3b6822c93735699fd7bd810111da31e7696";

  /** A marker that cannot be confused with anything else in a log line. */
  private static final String SECRET_MARKER = "bucket-MARKER-7f3a9c";

  private static final Instant NOON = Instant.parse("2026-10-09T12:00:00Z");

  private FakeOtpRateLimitManager manager;
  private TestClock clock;
  private OtpRateLimiter limiter;

  private Logger limiterLogger;
  private Level originalLevel;
  private ListAppender<ILoggingEvent> appender;

  @Before
  public void setUp() {
    this.manager = new FakeOtpRateLimitManager();
    this.clock = new TestClock(NOON);
    this.limiter = new OtpRateLimiter(new OtpKeys(SECRET_32_BYTES), this.manager, this.clock);

    this.limiterLogger = (Logger) LoggerFactory.getLogger(OtpRateLimiter.class);
    this.originalLevel = this.limiterLogger.getLevel();
    this.limiterLogger.setLevel(Level.TRACE);
    this.appender = new ListAppender<ILoggingEvent>();
    this.appender.start();
    this.limiterLogger.addAppender(this.appender);
  }

  @After
  public void tearDown() {
    this.limiterLogger.detachAppender(this.appender);
    this.limiterLogger.setLevel(this.originalLevel);
  }

  // ---- SEC-010: the four limits, counted per 15-minute window --------------------------------------------------

  @Test
  public void requestCodeIsLimitedPerEmailFromTheSixthCall() {
    // Each call is 30 s after the last, so the cooldown never decides; every call has its own address.
    for (int call = 1; call <= 5; call++) {
      assertEquals("call " + call, Decision.ALLOWED,
        this.limiter.checkRequestCode(EMAIL, "198.51.100." + call));
      this.clock.advance(Duration.ofSeconds(30));
    }

    assertEquals(Decision.LIMITED, this.limiter.checkRequestCode(EMAIL, "198.51.100.6"));
  }

  @Test
  public void requestCodeIsLimitedPerIpFromTheTwentyFirstCall() {
    // Every call has its own email, so neither the email bucket nor the cooldown decides.
    for (int call = 1; call <= 20; call++) {
      assertEquals("call " + call, Decision.ALLOWED, this.limiter.checkRequestCode("user" + call + "@partner.org", IP));
    }

    assertEquals(Decision.LIMITED, this.limiter.checkRequestCode("user21@partner.org", IP));
  }

  @Test
  public void verifyCodeIsLimitedPerEmailFromTheEleventhCall() {
    for (int call = 1; call <= 10; call++) {
      assertEquals("call " + call, Decision.ALLOWED, this.limiter.checkVerifyCode(EMAIL, "198.51.100." + call));
    }

    assertEquals(Decision.LIMITED, this.limiter.checkVerifyCode(EMAIL, "198.51.100.11"));
  }

  @Test
  public void verifyCodeIsLimitedPerIpFromTheFiftyFirstCall() {
    for (int call = 1; call <= 50; call++) {
      assertEquals("call " + call, Decision.ALLOWED, this.limiter.checkVerifyCode("user" + call + "@partner.org", IP));
    }

    assertEquals(Decision.LIMITED, this.limiter.checkVerifyCode("user51@partner.org", IP));
  }

  @Test
  public void verifyCodeWithNoPendingChallengeCountsOnlyTheIpBucket() {
    assertEquals(Decision.ALLOWED, this.limiter.checkVerifyCode(null, IP));

    assertEquals(Collections.singletonList(VERIFY_IP_BUCKET), this.manager.hitBuckets);
    assertTrue(this.manager.cooldowns.isEmpty());
  }

  @Test
  public void verifyCodeHasNoCooldown() {
    assertEquals(Decision.ALLOWED, this.limiter.checkVerifyCode(EMAIL, IP));
    assertEquals(Decision.ALLOWED, this.limiter.checkVerifyCode(EMAIL, IP));

    assertTrue("a wrong code may be retried at once", this.manager.cooldowns.isEmpty());
  }

  @Test
  public void theRequestAndVerifyBucketsOfOneEmailAreIndependent() {
    for (int call = 1; call <= 10; call++) {
      this.limiter.checkVerifyCode(EMAIL, "198.51.100." + call);
    }
    assertEquals(Decision.LIMITED, this.limiter.checkVerifyCode(EMAIL, "198.51.100.11"));

    assertEquals(Decision.ALLOWED, this.limiter.checkRequestCode(EMAIL, "198.51.100.50"));
  }

  // ---- the window: fixed, 15 minutes, aligned on the UTC clock ----------------------------------------------------

  @Test
  public void theCounterRestartsInTheNextFifteenMinuteWindow() {
    this.clock.set(Instant.parse("2026-10-09T12:10:00Z"));
    for (int call = 1; call <= 5; call++) {
      assertEquals("call " + call, Decision.ALLOWED, this.limiter.checkRequestCode(EMAIL, "198.51.100." + call));
      this.clock.advance(Duration.ofSeconds(30));
    }
    // 12:12:30, still window 12:00: the sixth is refused, and the refusal leaves the cooldown where it was.
    assertEquals(Decision.LIMITED, this.limiter.checkRequestCode(EMAIL, "198.51.100.6"));

    this.clock.set(Instant.parse("2026-10-09T12:15:00Z"));

    assertEquals(Decision.ALLOWED, this.limiter.checkRequestCode(EMAIL, "198.51.100.7"));
  }

  @Test
  public void theWindowStartsOnTheFifteenMinuteMarkOfTheUtcClock() {
    this.clock.set(Instant.parse("2026-10-09T12:14:59.999Z"));
    this.limiter.checkVerifyCode(EMAIL, IP);
    this.clock.set(Instant.parse("2026-10-09T12:15:00.000Z"));
    this.limiter.checkVerifyCode(EMAIL, IP);
    this.clock.set(Instant.parse("2026-10-09T12:07:31.123Z"));
    this.limiter.checkVerifyCode(EMAIL, IP);

    assertEquals(
      Arrays.asList(Instant.parse("2026-10-09T12:00:00Z"), Instant.parse("2026-10-09T12:00:00Z"),
        Instant.parse("2026-10-09T12:15:00Z"), Instant.parse("2026-10-09T12:15:00Z"),
        Instant.parse("2026-10-09T12:00:00Z"), Instant.parse("2026-10-09T12:00:00Z")),
      this.manager.hitWindows);
  }

  // ---- FN-008 / RJ-1: at least 30 s apart, sliding, not a fixed window ---------------------------------------------

  @Test
  public void aSecondRequest29Point9SecondsLaterIsLimited() {
    assertEquals(Decision.ALLOWED, this.limiter.checkRequestCode(EMAIL, IP));

    this.clock.set(NOON.plusMillis(29_900));

    assertEquals(Decision.LIMITED, this.limiter.checkRequestCode(EMAIL, IP));
  }

  @Test
  public void aSecondRequestExactly30SecondsLaterIsAccepted() {
    assertEquals(Decision.ALLOWED, this.limiter.checkRequestCode(EMAIL, IP));

    this.clock.set(NOON.plusSeconds(30));

    assertEquals(Decision.ALLOWED, this.limiter.checkRequestCode(EMAIL, IP));
  }

  @Test
  public void aRequestOneSecondAfterAnotherIsLimitedAcrossAMinuteBoundary() {
    // 12:07:59.500 and 12:08:00.500 sit in different fixed 30-s windows (RJ-1), one second apart.
    this.clock.set(Instant.parse("2026-10-09T12:07:59.500Z"));
    assertEquals(Decision.ALLOWED, this.limiter.checkRequestCode(EMAIL, IP));

    this.clock.set(Instant.parse("2026-10-09T12:08:00.500Z"));

    assertEquals(Decision.LIMITED, this.limiter.checkRequestCode(EMAIL, IP));
  }

  @Test
  public void aRequestIsLimitedAcrossAFifteenMinuteBoundaryToo() {
    // The email counter restarts at 12:15:00; the cooldown does not follow the window.
    this.clock.set(Instant.parse("2026-10-09T12:14:59.800Z"));
    assertEquals(Decision.ALLOWED, this.limiter.checkRequestCode(EMAIL, IP));

    this.clock.set(Instant.parse("2026-10-09T12:15:00.200Z"));

    assertEquals(Decision.LIMITED, this.limiter.checkRequestCode(EMAIL, IP));
  }

  @Test
  public void aRejectedRequestChangesNothingSoTheSpacingRunsFromTheAcceptedOne() {
    this.clock.set(Instant.parse("2026-10-09T12:07:59.500Z"));
    assertEquals(Decision.ALLOWED, this.limiter.checkRequestCode(EMAIL, IP));
    this.clock.set(Instant.parse("2026-10-09T12:08:00.500Z"));
    assertEquals(Decision.LIMITED, this.limiter.checkRequestCode(EMAIL, IP));

    // 29.5 s after the accepted request: still too early, whatever the rejected one did.
    this.clock.set(Instant.parse("2026-10-09T12:08:29.000Z"));
    assertEquals(Decision.LIMITED, this.limiter.checkRequestCode(EMAIL, IP));
    // 30.0 s after the accepted request, 29.5 s after the rejected one: accepted.
    this.clock.set(Instant.parse("2026-10-09T12:08:29.500Z"));
    assertEquals(Decision.ALLOWED, this.limiter.checkRequestCode(EMAIL, IP));
  }

  @Test
  public void twoRequestsCarryingTheSameInstantAcceptOnlyTheFirst() {
    // Same-millisecond case: the row lock picks one winner, so a second request with an identical now is limited.
    assertEquals(Decision.ALLOWED, this.limiter.checkRequestCode(EMAIL, IP));

    assertEquals(Decision.LIMITED, this.limiter.checkRequestCode(EMAIL, "198.51.100.9"));
  }

  @Test
  public void theCooldownBelongsToTheEmailNotToTheAddress() {
    assertEquals(Decision.ALLOWED, this.limiter.checkRequestCode("one@partner.org", IP));
    assertEquals(Decision.ALLOWED, this.limiter.checkRequestCode("two@partner.org", IP));
  }

  // ---- what is stored: only HMAC buckets --------------------------------------------------------------------------

  @Test
  public void aRequestCountsTheEmailTheAddressAndTheCooldownUnderKnownHmacBuckets() {
    this.limiter.checkRequestCode(EMAIL, IP);

    assertEquals(Arrays.asList(REQUEST_EMAIL_BUCKET, REQUEST_IP_BUCKET), this.manager.hitBuckets);
    assertEquals(Collections.singletonList(COOLDOWN_BUCKET), this.manager.cooldownBuckets);
  }

  @Test
  public void aVerifyCountsTheEmailAndTheAddressUnderKnownHmacBuckets() {
    this.limiter.checkVerifyCode(EMAIL, IP);

    assertEquals(Arrays.asList(VERIFY_EMAIL_BUCKET, VERIFY_IP_BUCKET), this.manager.hitBuckets);
  }

  @Test
  public void noBucketCarriesTheRawEmailOrTheRawAddress() {
    this.limiter.checkRequestCode(EMAIL, IP);
    this.limiter.checkVerifyCode(EMAIL, IP);

    List<String> everything = new ArrayList<>(this.manager.hitBuckets);
    everything.addAll(this.manager.cooldownBuckets);
    assertEquals(5, everything.size());
    for (String bucket : everything) {
      assertEquals(64, bucket.length());
      assertTrue(bucket, bucket.matches("[0-9a-f]{64}"));
      assertFalse(bucket.contains("partner"));
      assertFalse(bucket.contains("203"));
    }
  }

  @Test
  public void theSpacingAndTheClockReachTheStoreAsTheRequirementStatesThem() {
    this.limiter.checkRequestCode(EMAIL, IP);

    assertEquals(Duration.ofSeconds(30), this.manager.cooldownSpacing);
    assertEquals(NOON, this.manager.cooldownNow);
  }

  @Test
  public void everyCheckReadsTheClockOnceSoTheCooldownAndTheWindowsShareOneNow() {
    this.limiter.checkRequestCode(EMAIL, IP);
    assertEquals(1, this.clock.reads);

    this.limiter.checkVerifyCode(EMAIL, IP);
    assertEquals(2, this.clock.reads);
  }

  // ---- order: a refused request does not touch the later buckets -------------------------------------------------

  @Test
  public void aRequestRefusedByTheEmailLimitTouchesNeitherTheAddressNorTheCooldown() {
    for (int call = 1; call <= 5; call++) {
      this.limiter.checkRequestCode(EMAIL, IP);
      this.clock.advance(Duration.ofSeconds(30));
    }
    Instant lastAccepted = this.manager.cooldowns.get(COOLDOWN_BUCKET);
    int addressHits = this.manager.hitsOf(REQUEST_IP_BUCKET);

    assertEquals(Decision.LIMITED, this.limiter.checkRequestCode(EMAIL, IP));

    assertEquals(addressHits, this.manager.hitsOf(REQUEST_IP_BUCKET));
    assertEquals(lastAccepted, this.manager.cooldowns.get(COOLDOWN_BUCKET));
  }

  // ---- DD-8: a store failure fails closed, and never leaks ---------------------------------------------------------

  @Test
  public void aFailureOfTheCounterFailsClosed() {
    this.manager.hitFailure = new IllegalStateException("store down " + SECRET_MARKER);

    Decision decision = this.limiter.checkRequestCode(EMAIL, IP);

    assertEquals(Decision.UNAVAILABLE, decision);
    assertFalse(decision.isAllowed());
  }

  @Test
  public void aFailureOfTheCooldownFailsClosed() {
    this.manager.cooldownFailure = new IllegalStateException("store down " + SECRET_MARKER);

    assertEquals(Decision.UNAVAILABLE, this.limiter.checkRequestCode(EMAIL, IP));
  }

  @Test
  public void aLockOrDeadlockFailureIsAStoreFailureAndIsNeverRethrown() {
    this.manager.hitFailure = new CannotAcquireLockException("Deadlock found on " + SECRET_MARKER);

    assertEquals(Decision.UNAVAILABLE, this.limiter.checkRequestCode(EMAIL, IP));
    assertEquals(Decision.UNAVAILABLE, this.limiter.checkVerifyCode(EMAIL, IP));
  }

  @Test
  public void aStoreFailureLogsNoMessageAndNoStackTraceBecauseItMayCarryTheBucket() {
    this.manager.hitFailure = new CannotAcquireLockException("Deadlock found on " + SECRET_MARKER);

    this.limiter.checkRequestCode(EMAIL, IP);

    assertFalse("the failure must be observable", this.appender.list.isEmpty());
    for (ILoggingEvent event : this.appender.list) {
      assertFalse(event.getFormattedMessage(), event.getFormattedMessage().contains(SECRET_MARKER));
      assertNull("no throwable, whose message would carry the marker", event.getThrowableProxy());
    }
  }

  @Test
  public void aStoreFailureAttemptsNoPurge() {
    this.manager.hitFailure = new IllegalStateException("store down");

    this.limiter.checkRequestCode(EMAIL, IP);

    assertTrue(this.manager.purgeCutoffs.isEmpty());
  }

  @Test
  public void anUnconfiguredSecretFailsClosedWithoutTouchingTheStore() {
    OtpRateLimiter unconfigured = new OtpRateLimiter(new OtpKeys(""), this.manager, this.clock);

    assertEquals(Decision.UNAVAILABLE, unconfigured.checkRequestCode(EMAIL, IP));
    assertEquals(Decision.UNAVAILABLE, unconfigured.checkVerifyCode(EMAIL, IP));

    assertTrue(this.manager.calls.isEmpty());
  }

  // ---- OPS: purge of the stale rows, opportunistic and never part of the answer ----------------------------------

  @Test
  public void staleWindowsOlderThanADayAndCooldownsOlderThanAnHourArePurgedAfterTheCounting() {
    this.limiter.checkRequestCode(EMAIL, IP);

    assertEquals(Arrays.asList("hit", "hit", "acceptCooldown", "purgeWindows", "purgeCooldowns"), this.manager.calls);
    assertEquals(NOON.minus(Duration.ofDays(1)), this.manager.purgeCutoffs.get("purgeWindows"));
    assertEquals(NOON.minus(Duration.ofHours(1)), this.manager.purgeCutoffs.get("purgeCooldowns"));
  }

  @Test
  public void thePurgeDoesNotRunOnEveryCheck() {
    this.limiter.checkVerifyCode(EMAIL, IP);
    this.manager.calls.clear();

    this.clock.advance(Duration.ofSeconds(1));
    this.limiter.checkVerifyCode(EMAIL, IP);

    assertEquals(Arrays.asList("hit", "hit"), this.manager.calls);
  }

  @Test
  public void thePurgeRunsAgainOnceItsIntervalHasPassed() {
    this.limiter.checkVerifyCode(EMAIL, IP);
    this.manager.calls.clear();

    this.clock.advance(Duration.ofMinutes(10));
    this.limiter.checkVerifyCode(EMAIL, IP);

    assertEquals(Arrays.asList("hit", "hit", "purgeWindows", "purgeCooldowns"), this.manager.calls);
  }

  @Test
  public void aFailedPurgeNeverChangesTheDecisionAndLogsNoMessage() {
    this.manager.purgeFailure = new IllegalStateException("purge failed " + SECRET_MARKER);

    assertEquals(Decision.ALLOWED, this.limiter.checkRequestCode(EMAIL, IP));

    for (ILoggingEvent event : this.appender.list) {
      assertFalse(event.getFormattedMessage(), event.getFormattedMessage().contains(SECRET_MARKER));
      assertNull(event.getThrowableProxy());
    }
  }

  // ---- fakes ----------------------------------------------------------------------------------------------------

  /** A clock the test moves, counting how often the limiter reads it. */
  private static final class TestClock extends Clock {

    private Instant now;
    int reads;

    TestClock(Instant now) {
      this.now = now;
    }

    void advance(Duration by) {
      this.now = this.now.plus(by);
    }

    void set(Instant now) {
      this.now = now;
    }

    @Override
    public ZoneId getZone() {
      return ZoneOffset.UTC;
    }

    @Override
    public Instant instant() {
      this.reads++;
      return this.now;
    }

    @Override
    public Clock withZone(ZoneId zone) {
      return this;
    }
  }

  /**
   * The fake store. {@link #hit} stands for the upsert and the read-back of the counter. {@link #acceptCooldown}
   * stands for the locking read-decide-write: the stored value moves to {@code now} only when it is not after
   * {@code now} minus the spacing, and that is the answer. A stored value equal to {@code now} is limited.
   */
  private static final class FakeOtpRateLimitManager implements OtpRateLimitManager {

    final List<String> calls = new ArrayList<>();
    final List<String> hitBuckets = new ArrayList<>();
    final List<Instant> hitWindows = new ArrayList<>();
    final List<String> cooldownBuckets = new ArrayList<>();
    final Map<String, Integer> counters = new HashMap<>();
    final Map<String, Instant> cooldowns = new HashMap<>();
    final Map<String, Instant> purgeCutoffs = new HashMap<>();

    Duration cooldownSpacing;
    Instant cooldownNow;

    RuntimeException hitFailure;
    RuntimeException cooldownFailure;
    RuntimeException purgeFailure;

    @Override
    public boolean acceptCooldown(String bucket, Instant now, Duration minimumSpacing) {
      this.calls.add("acceptCooldown");
      this.cooldownBuckets.add(bucket);
      this.cooldownSpacing = minimumSpacing;
      this.cooldownNow = now;
      if (this.cooldownFailure != null) {
        throw this.cooldownFailure;
      }
      Instant stored = this.cooldowns.get(bucket);
      if (stored != null && stored.isAfter(now.minus(minimumSpacing))) {
        return false;
      }
      this.cooldowns.put(bucket, now);
      return true;
    }

    @Override
    public int hit(String bucket, Instant windowStart) {
      this.calls.add("hit");
      this.hitBuckets.add(bucket);
      this.hitWindows.add(windowStart);
      if (this.hitFailure != null) {
        throw this.hitFailure;
      }
      return this.counters.merge(bucket + "@" + windowStart, 1, Integer::sum);
    }

    int hitsOf(String bucket) {
      int total = 0;
      for (Map.Entry<String, Integer> counter : this.counters.entrySet()) {
        if (counter.getKey().startsWith(bucket + "@")) {
          total += counter.getValue();
        }
      }
      return total;
    }

    @Override
    public int purgeCooldownsBefore(Instant cutoff) {
      this.calls.add("purgeCooldowns");
      this.purgeCutoffs.put("purgeCooldowns", cutoff);
      if (this.purgeFailure != null) {
        throw this.purgeFailure;
      }
      return 0;
    }

    @Override
    public int purgeWindowsBefore(Instant cutoff) {
      this.calls.add("purgeWindows");
      this.purgeCutoffs.put("purgeWindows", cutoff);
      if (this.purgeFailure != null) {
        throw this.purgeFailure;
      }
      return 0;
    }
  }
}
