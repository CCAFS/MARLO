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

package org.cgiar.ccafs.marlo.data.manager.impl;

import org.cgiar.ccafs.marlo.data.dao.OtpRateLimitDAO;

import java.lang.reflect.Method;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * CHG-OTP-LOGIN-001-T04: the branching of {@link OtpRateLimitManagerImpl} against a hand-written fake DAO. The
 * counts and values the fake returns stand for what the native statements answer. The statements themselves are
 * proven by the scripted run on the throwaway database copy, not here.
 */
public class OtpRateLimitManagerImplTest {

  private static final String BUCKET = "b".repeat(64);
  private static final Instant WINDOW = Instant.parse("2026-10-09T12:00:00Z");
  private static final Duration SPACING = Duration.ofSeconds(30);

  private FakeOtpRateLimitDao dao;
  private OtpRateLimitManagerImpl manager;

  @Before
  public void setUp() {
    this.dao = new FakeOtpRateLimitDao();
    this.manager = new OtpRateLimitManagerImpl(this.dao);
  }

  // ---- hit: upsert, then read back (SEC-010, JI-12) --------------------------------------------------------------

  @Test
  public void aHitIncrementsTheWindowCounterThenReadsItBack() {
    this.dao.hitsAfterIncrement = 4;

    assertEquals(4, this.manager.hit(BUCKET, WINDOW));
    assertEquals(Arrays.asList("incrementWindow", "findHits"), this.dao.calls);
    assertEquals(BUCKET, this.dao.bucket);
    assertEquals(WINDOW, this.dao.window);
  }

  @Test
  public void aHitIsTheReadBackValueWhateverTheUpsertReported() {
    // The affected-row count of an upsert differs between an insert and an update and between driver settings.
    for (int reported : new int[] {0, 1, 2}) {
      this.dao.incrementResult = reported;
      this.dao.hitsAfterIncrement = 7;

      assertEquals("upsert reported " + reported, 7, this.manager.hit(BUCKET, WINDOW));
    }
  }

  @Test
  public void aCounterThatCannotBeReadBackAfterItsIncrementIsAFailureNotAZero() {
    this.dao.hitsAfterIncrement = null;

    try {
      this.manager.hit(BUCKET, WINDOW);
      fail("a missing read-back must not look like a count");
    } catch (IllegalStateException expected) {
      assertFalse("the message must not carry the bucket", expected.getMessage().contains(BUCKET));
    }
  }

  @Test
  public void aFailureOfTheUpsertReachesTheCallerSoTheTransactionRollsBack() {
    this.dao.incrementFailure = new IllegalArgumentException("lock wait timeout");

    try {
      this.manager.hit(BUCKET, WINDOW);
      fail("the store failure must reach the caller");
    } catch (IllegalArgumentException expected) {
      assertEquals("lock wait timeout", expected.getMessage());
    }
    assertEquals(Collections.singletonList("incrementWindow"), this.dao.calls);
  }

  // ---- acceptCooldown: lock, read under the lock, decide in Java (FN-008, RJ-1) ---------------------------------

  private static final Instant NOW = Instant.parse("2026-10-09T12:08:30.000Z");

  @Test
  public void theCooldownLocksTheRowReadsItUnderTheLockAndWritesNowOnlyWhenItIsDue() {
    this.dao.lastRequestAt = NOW.minusSeconds(60);

    assertTrue(this.manager.acceptCooldown(BUCKET, NOW, SPACING));

    assertEquals(Arrays.asList("lockCooldown", "findLastRequestAtForUpdate", "updateCooldown"), this.dao.calls);
    assertEquals(BUCKET, this.dao.bucket);
    assertEquals(NOW, this.dao.updatedTo);
  }

  @Test
  public void aStoredValueEqualToNowIsLimitedBecauseTheDecisionIsNotAComparisonWithNow() {
    // The case the earlier read-back could not see: another request already stored this same millisecond.
    this.dao.lastRequestAt = NOW;

    assertFalse(this.manager.acceptCooldown(BUCKET, NOW, SPACING));
    assertEquals(Arrays.asList("lockCooldown", "findLastRequestAtForUpdate"), this.dao.calls);
  }

  @Test
  public void aStoredValueAfterNowIsLimited() {
    // Another instance whose clock runs ahead stored it.
    this.dao.lastRequestAt = NOW.plusMillis(250);

    assertFalse(this.manager.acceptCooldown(BUCKET, NOW, SPACING));
  }

  @Test
  public void aRequest29Point9SecondsAfterTheStoredOneIsLimitedAndChangesNothing() {
    this.dao.lastRequestAt = NOW.minusMillis(29_900);

    assertFalse(this.manager.acceptCooldown(BUCKET, NOW, SPACING));
    assertNull("nothing is written when the request is too soon", this.dao.updatedTo);
  }

  @Test
  public void aRequestExactly30SecondsAfterTheStoredOneIsAccepted() {
    this.dao.lastRequestAt = NOW.minusSeconds(30);

    assertTrue(this.manager.acceptCooldown(BUCKET, NOW, SPACING));
    assertEquals(NOW, this.dao.updatedTo);
  }

  @Test
  public void theSpacingIsElapsedTimeNotTheClockFaceAcrossAMinuteBoundary() {
    Instant now = Instant.parse("2026-10-09T12:08:00.500Z");

    this.dao.lastRequestAt = Instant.parse("2026-10-09T12:07:59.500Z");
    assertFalse("1 s later, across the minute", this.manager.acceptCooldown(BUCKET, now, SPACING));

    this.dao.lastRequestAt = Instant.parse("2026-10-09T12:07:30.500Z");
    assertTrue("exactly 30 s later, across the minute", this.manager.acceptCooldown(BUCKET, now, SPACING));
  }

  @Test
  public void aNewRowCarriesTheFarPastSentinelSoItsFirstRequestIsAlwaysDue() {
    this.dao.lastRequestAt = null;
    this.dao.lastRequestAtAfterLock = Instant.EPOCH;

    assertTrue(this.manager.acceptCooldown(BUCKET, NOW, SPACING));
    assertEquals(Instant.EPOCH, this.dao.sentinel);
  }

  @Test
  public void theDecisionAndTheWrittenValueUseTheMillisecondThatTheColumnStores() {
    // The column keeps milliseconds. 12:08:30.0009 is stored as .000, and is compared as .000 too.
    Instant now = Instant.parse("2026-10-09T12:08:30.000900Z");
    this.dao.lastRequestAt = Instant.parse("2026-10-09T12:08:00.000Z");

    assertTrue(this.manager.acceptCooldown(BUCKET, now, SPACING));
    assertEquals(Instant.parse("2026-10-09T12:08:30.000Z"), this.dao.updatedTo);
  }

  @Test
  public void aRowThatCannotBeReadAfterItWasLockedIsAFailureNotAnAcceptance() {
    this.dao.lastRequestAt = null;

    try {
      this.manager.acceptCooldown(BUCKET, NOW, SPACING);
      fail("a missing row after the locking upsert must not look like a cooldown that is due");
    } catch (IllegalStateException expected) {
      assertFalse("the message must not carry the bucket", expected.getMessage().contains(BUCKET));
    }
    assertNull(this.dao.updatedTo);
  }

  @Test
  public void anUpdateThatDoesNotTouchExactlyOneRowIsAFailureNotAnAcceptance() {
    this.dao.lastRequestAt = NOW.minusSeconds(60);
    this.dao.updateResult = 0;

    try {
      this.manager.acceptCooldown(BUCKET, NOW, SPACING);
      fail("an update that changed no row must not look like an accepted request");
    } catch (IllegalStateException expected) {
      assertFalse("the message must not carry the bucket", expected.getMessage().contains(BUCKET));
    }
  }

  @Test
  public void aLockFailureReachesTheCallerSoNothingIsDecidedWithoutTheLock() {
    this.dao.lockFailure = new IllegalArgumentException("deadlock");
    this.dao.lastRequestAt = NOW.minusSeconds(60);

    try {
      this.manager.acceptCooldown(BUCKET, NOW, SPACING);
      fail("the store failure must reach the caller");
    } catch (IllegalArgumentException expected) {
      assertEquals("deadlock", expected.getMessage());
    }
    assertEquals(Collections.singletonList("lockCooldown"), this.dao.calls);
  }

  // ---- purge: the cutoff is the caller's, the count comes back -----------------------------------------------------

  @Test
  public void purgesDeleteWithTheCutoffTheCallerComputedAndReturnTheDeletedCount() {
    Instant cutoff = WINDOW.minus(Duration.ofDays(1));
    this.dao.deleteResult = 7;

    assertEquals(7, this.manager.purgeWindowsBefore(cutoff));
    assertEquals(cutoff, this.dao.windowCutoff);

    this.dao.deleteResult = 3;
    assertEquals(3, this.manager.purgeCooldownsBefore(cutoff));
    assertEquals(cutoff, this.dao.cooldownCutoff);
    assertEquals(Arrays.asList("deleteWindowsBefore", "deleteCooldownsBefore"), this.dao.calls);
  }

  // ---- every method writes, so every method is transactional and none joins the caller's -------------------------

  @Test
  public void everyMethodIsTransactionalInATransactionOfItsOwn() throws NoSuchMethodException {
    Method[] writers = {
      OtpRateLimitManagerImpl.class.getMethod("hit", String.class, Instant.class),
      OtpRateLimitManagerImpl.class.getMethod("acceptCooldown", String.class, Instant.class, Duration.class),
      OtpRateLimitManagerImpl.class.getMethod("purgeWindowsBefore", Instant.class),
      OtpRateLimitManagerImpl.class.getMethod("purgeCooldownsBefore", Instant.class)};

    for (Method writer : writers) {
      Transactional transactional = writer.getAnnotation(Transactional.class);
      assertNotNull(writer.getName() + " writes and must be @Transactional", transactional);
      assertFalse(writer.getName() + " must not be read-only", transactional.readOnly());
      assertEquals(writer.getName() + " must never join the caller's transaction", Propagation.REQUIRES_NEW,
        transactional.propagation());
    }
  }

  /** The fake DAO: it answers what its fields say and records what the manager asked, in order. */
  private static final class FakeOtpRateLimitDao implements OtpRateLimitDAO {

    final List<String> calls = new ArrayList<>();

    String bucket;
    Instant window;
    int incrementResult = 1;
    RuntimeException incrementFailure;
    Integer hitsAfterIncrement = 1;

    Instant sentinel;
    RuntimeException lockFailure;
    Instant lastRequestAt;
    Instant lastRequestAtAfterLock;
    int updateResult = 1;
    Instant updatedTo;

    int deleteResult;
    Instant windowCutoff;
    Instant cooldownCutoff;

    @Override
    public int deleteCooldownsBefore(Instant olderThan) {
      this.calls.add("deleteCooldownsBefore");
      this.cooldownCutoff = olderThan;
      return this.deleteResult;
    }

    @Override
    public int deleteWindowsBefore(Instant olderThan) {
      this.calls.add("deleteWindowsBefore");
      this.windowCutoff = olderThan;
      return this.deleteResult;
    }

    @Override
    public Integer findHits(String bucket, Instant windowStart) {
      this.calls.add("findHits");
      return this.hitsAfterIncrement;
    }

    @Override
    public int incrementWindow(String bucket, Instant windowStart) {
      this.calls.add("incrementWindow");
      this.bucket = bucket;
      this.window = windowStart;
      if (this.incrementFailure != null) {
        throw this.incrementFailure;
      }
      return this.incrementResult;
    }

    @Override
    public int lockCooldown(String bucket, Instant sentinel) {
      this.calls.add("lockCooldown");
      this.bucket = bucket;
      this.sentinel = sentinel;
      if (this.lockFailure != null) {
        throw this.lockFailure;
      }
      return 1;
    }

    @Override
    public Instant findLastRequestAtForUpdate(String bucket) {
      this.calls.add("findLastRequestAtForUpdate");
      return this.lastRequestAt != null ? this.lastRequestAt : this.lastRequestAtAfterLock;
    }

    @Override
    public int updateCooldown(String bucket, Instant now) {
      this.calls.add("updateCooldown");
      this.updatedTo = now;
      return this.updateResult;
    }
  }
}
