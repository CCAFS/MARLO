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

import org.cgiar.ccafs.marlo.data.dao.OtpChallengeDAO;
import org.cgiar.ccafs.marlo.data.manager.OtpChallengeManager.MismatchOutcome;
import org.cgiar.ccafs.marlo.data.model.OtpChallenge;

import java.lang.reflect.Method;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * CHG-OTP-LOGIN-001-T03: the branching of {@link OtpChallengeManagerImpl} against a hand-written fake DAO (P-17).
 * The affected-row counts the fake returns stand for what the conditional SQL statements answer. The statements
 * themselves are proven by the scripted run on the throwaway database copy and by the T15 probe, not here.
 */
public class OtpChallengeManagerImplTest {

  private static final String EMAIL_HMAC = "e".repeat(64);
  private static final String CODE_HMAC = "c".repeat(64);
  private static final String NONCE = "abcdefghijklmnopqrstuv";
  private static final long GLOBAL_UNIT_ID = 12L;
  private static final Instant NOW = Instant.parse("2026-10-09T12:00:00.250Z");
  private static final Instant EXPIRES_AT = NOW.plusSeconds(300);

  /** The two counts a conditional statement can answer for one row: it matched, or it did not. */
  private static final int MATCHED = 1;
  private static final int NOT_MATCHED = 0;

  private FakeOtpChallengeDao dao;
  private OtpChallengeManagerImpl manager;

  @Before
  public void setUp() {
    this.dao = new FakeOtpChallengeDao();
    this.manager = new OtpChallengeManagerImpl(this.dao, Clock.fixed(NOW, ZoneOffset.UTC));
  }

  // ---- issue: supersede, then insert (FN-007, DD-6) --------------------------------------------------------------

  @Test
  public void issueSupersedesTheEmailsOpenRowsBeforeInsertingTheNewOne() {
    this.manager.issue(EMAIL_HMAC, GLOBAL_UNIT_ID, NONCE, CODE_HMAC, EXPIRES_AT);

    assertEquals(Arrays.asList("supersede", "insert"), this.dao.calls);
    assertEquals(EMAIL_HMAC, this.dao.supersedeEmailHmac);
    assertEquals(EMAIL_HMAC, this.dao.insertEmailHmac);
    assertEquals(GLOBAL_UNIT_ID, this.dao.insertGlobalUnitId);
    assertEquals(NONCE, this.dao.insertNonce);
    assertEquals(CODE_HMAC, this.dao.insertCodeHmac);
  }

  @Test
  public void issueStampsTheSupersedeAndTheCreationWithTheManagerClockAndKeepsTheCallersExpiry() {
    this.manager.issue(EMAIL_HMAC, GLOBAL_UNIT_ID, NONCE, CODE_HMAC, EXPIRES_AT);

    assertEquals(NOW, this.dao.supersedeNow);
    assertEquals(NOW, this.dao.insertNow);
    assertEquals(EXPIRES_AT, this.dao.insertExpiresAt);
  }

  @Test
  public void issueLetsAnInsertFailureEscapeSoTheTransactionRollsTheSupersedeBack() {
    this.dao.insertFailure = new IllegalArgumentException("duplicate nonce");

    try {
      this.manager.issue(EMAIL_HMAC, GLOBAL_UNIT_ID, NONCE, CODE_HMAC, EXPIRES_AT);
      fail("the insert failure must reach the caller, or the transaction would commit the supersede alone");
    } catch (IllegalArgumentException expected) {
      assertEquals("duplicate nonce", expected.getMessage());
    }
  }

  @Test
  public void issueThatAddsNoRowFailsInsteadOfCommittingTheSupersedeAlone() {
    this.dao.insertResult = NOT_MATCHED;

    try {
      this.manager.issue(EMAIL_HMAC, GLOBAL_UNIT_ID, NONCE, CODE_HMAC, EXPIRES_AT);
      fail("an insert that added no row must not look like an issued challenge");
    } catch (IllegalStateException expected) {
      assertTrue(expected.getMessage().length() > 0);
    }
  }

  // ---- consume: exactly one row, or refused (FN-012, FN-013, FN-014, JS-1) ---------------------------------------

  @Test
  public void consumeSucceedsOnlyWhenTheConditionalUpdateMatchedExactlyOneRow() {
    this.dao.consumeResult = MATCHED;

    assertTrue(this.manager.consume(NONCE));
  }

  @Test
  public void consumeIsRefusedWhenTheConditionalUpdateMatchedNothing() {
    // An exhausted, expired, consumed, superseded or missing challenge all answer 0 rows.
    this.dao.consumeResult = NOT_MATCHED;

    assertFalse(this.manager.consume(NONCE));
  }

  @Test
  public void consumeIsRefusedWhenMoreThanOneRowMatchedBecauseTheCountIsNotTheContract() {
    this.dao.consumeResult = 2;

    assertFalse(this.manager.consume(NONCE));
  }

  @Test
  public void consumeAsksTheDatabaseWithTheManagerClockAndTheNonce() {
    this.manager.consume(NONCE);

    assertEquals(Collections.singletonList("consume"), this.dao.calls);
    assertEquals(NONCE, this.dao.consumeNonce);
    assertEquals(NOW, this.dao.consumeNow);
  }

  // ---- registerMismatch: increment, re-read, classify (FN-012, RJ-3) ---------------------------------------------

  @Test
  public void aWrongCodeThatLeavesAttemptsIsAMismatch() {
    this.dao.incrementResult = MATCHED;
    this.dao.attemptsAfterIncrement = 1;

    assertEquals(MismatchOutcome.MISMATCH, this.manager.registerMismatch(NONCE));
    assertEquals(Arrays.asList("incrementAttempts", "findAttempts"), this.dao.calls);
    assertEquals(NONCE, this.dao.incrementNonce);
  }

  @Test
  public void theSecondWrongCodeIsStillAMismatch() {
    this.dao.incrementResult = MATCHED;
    this.dao.attemptsAfterIncrement = 2;

    assertEquals(MismatchOutcome.MISMATCH, this.manager.registerMismatch(NONCE));
  }

  @Test
  public void theThirdWrongCodeExhaustsTheChallenge() {
    this.dao.incrementResult = MATCHED;
    this.dao.attemptsAfterIncrement = 3;

    assertEquals(MismatchOutcome.EXHAUSTED, this.manager.registerMismatch(NONCE));
  }

  @Test
  public void aCountThatCannotBeReadBackAfterTheIncrementFailsClosed() {
    this.dao.incrementResult = MATCHED;
    this.dao.attemptsAfterIncrement = null;

    assertEquals(MismatchOutcome.INVALID, this.manager.registerMismatch(NONCE));
  }

  @Test
  public void whenNoRowWasIncrementedTheRowIsReReadInsteadOfTheAttemptsCount() {
    this.dao.incrementResult = NOT_MATCHED;
    this.dao.rowOnReRead = row(3, null, NOW.plusSeconds(60));

    this.manager.registerMismatch(NONCE);

    assertEquals(Arrays.asList("incrementAttempts", "findByNonce"), this.dao.calls);
  }

  @Test
  public void aRowThatIsGoneAfterNothingWasIncrementedIsInvalid() {
    this.dao.incrementResult = NOT_MATCHED;
    this.dao.rowOnReRead = null;

    assertEquals(MismatchOutcome.INVALID, this.manager.registerMismatch(NONCE));
  }

  @Test
  public void aConsumedOrSupersededRowIsInvalid() {
    this.dao.incrementResult = NOT_MATCHED;
    this.dao.rowOnReRead = row(1, NOW.minusSeconds(5), NOW.plusSeconds(60));

    assertEquals(MismatchOutcome.INVALID, this.manager.registerMismatch(NONCE));
  }

  @Test
  public void aConsumedRowIsInvalidEvenWhenItsAttemptsAlsoReachedTheCap() {
    // The order of the verify pre-checks: consumed first, then exhausted.
    this.dao.incrementResult = NOT_MATCHED;
    this.dao.rowOnReRead = row(3, NOW.minusSeconds(5), NOW.plusSeconds(60));

    assertEquals(MismatchOutcome.INVALID, this.manager.registerMismatch(NONCE));
  }

  @Test
  public void anExhaustedRowThatIsNotConsumedIsExhausted() {
    this.dao.incrementResult = NOT_MATCHED;
    this.dao.rowOnReRead = row(3, null, NOW.plusSeconds(60));

    assertEquals(MismatchOutcome.EXHAUSTED, this.manager.registerMismatch(NONCE));
  }

  @Test
  public void anExhaustedRowThatAlsoExpiredIsStillExhaustedAsInTheVerifyPreChecks() {
    this.dao.incrementResult = NOT_MATCHED;
    this.dao.rowOnReRead = row(3, null, NOW.minusSeconds(1));

    assertEquals(MismatchOutcome.EXHAUSTED, this.manager.registerMismatch(NONCE));
  }

  @Test
  public void anExpiredRowWithAttemptsLeftIsInvalid() {
    this.dao.incrementResult = NOT_MATCHED;
    this.dao.rowOnReRead = row(1, null, NOW.minusSeconds(1));

    assertEquals(MismatchOutcome.INVALID, this.manager.registerMismatch(NONCE));
  }

  @Test
  public void aRowThatLooksOpenAfterNothingWasIncrementedFailsClosed() {
    this.dao.incrementResult = NOT_MATCHED;
    this.dao.rowOnReRead = row(1, null, NOW.plusSeconds(60));

    assertEquals(MismatchOutcome.INVALID, this.manager.registerMismatch(NONCE));
  }

  // ---- purgeExpired: the cutoff is the caller's, the count comes back (OPS-003) ----------------------------------

  @Test
  public void purgeDeletesWithTheCutoffTheCallerComputedAndReturnsTheDeletedCount() {
    Instant cutoff = NOW.minusSeconds(3600);
    this.dao.deleteResult = 7;

    assertEquals(7, this.manager.purgeExpired(cutoff));
    assertEquals(Collections.singletonList("deleteExpiredBefore"), this.dao.calls);
    assertEquals(cutoff, this.dao.deleteCutoff);
  }

  // ---- allowListFor: NULL, blank and missing all mean "off" (T01 advisory, FN-001, FN-002) -----------------------

  private static final String ALLOW_LIST_KEY = "crp_otp_allowed_email_domains";

  @Test
  public void aNullValueIsOff() {
    // The existing format-4 catalog rows are seeded with NULL, and a custom row can hold NULL.
    this.dao.allowListValues.put(GLOBAL_UNIT_ID, null);

    assertEquals("", this.manager.allowListFor(ALLOW_LIST_KEY, Collections.singletonList(GLOBAL_UNIT_ID))
      .get(GLOBAL_UNIT_ID));
  }

  @Test
  public void anEmptyValueIsOff() {
    // The OTP catalog rows are seeded with default_value ''.
    this.dao.allowListValues.put(GLOBAL_UNIT_ID, "");

    assertEquals("", this.manager.allowListFor(ALLOW_LIST_KEY, Collections.singletonList(GLOBAL_UNIT_ID))
      .get(GLOBAL_UNIT_ID));
  }

  @Test
  public void aWhitespaceOnlyValueIsOff() {
    this.dao.allowListValues.put(GLOBAL_UNIT_ID, " \t ");

    assertEquals("", this.manager.allowListFor(ALLOW_LIST_KEY, Collections.singletonList(GLOBAL_UNIT_ID))
      .get(GLOBAL_UNIT_ID));
  }

  @Test
  public void aGlobalUnitAbsentFromTheStoreResultIsOff() {
    // A Global Unit that does not exist, so the request is refused as "domain not enabled".
    assertEquals("", this.manager.allowListFor(ALLOW_LIST_KEY, Collections.singletonList(GLOBAL_UNIT_ID))
      .get(GLOBAL_UNIT_ID));
  }

  @Test
  public void aConfiguredValueIsReturnedExactlyAsStoredForTheCallerToParse() {
    this.dao.allowListValues.put(GLOBAL_UNIT_ID, " Partner.org , other.org ");

    assertEquals(" Partner.org , other.org ",
      this.manager.allowListFor(ALLOW_LIST_KEY, Collections.singletonList(GLOBAL_UNIT_ID)).get(GLOBAL_UNIT_ID));
  }

  @Test
  public void everyRequestedGlobalUnitGetsAnEntryAndOthersAreNotInvented() {
    this.dao.allowListValues.put(12L, "a.org");
    this.dao.allowListValues.put(14L, null);

    Map<Long, String> result = this.manager.allowListFor(ALLOW_LIST_KEY, Arrays.asList(12L, 13L, 14L));

    Map<Long, String> expected = new HashMap<>();
    expected.put(12L, "a.org");
    expected.put(13L, "");
    expected.put(14L, "");
    assertEquals(expected, result);
  }

  @Test
  public void theKeyAndTheIdsGoToTheStoreAsGiven() {
    List<Long> ids = Arrays.asList(12L, 13L);

    this.manager.allowListFor(ALLOW_LIST_KEY, ids);

    assertEquals(Collections.singletonList("findAllowListValues"), this.dao.calls);
    assertEquals(ALLOW_LIST_KEY, this.dao.allowListKey);
    assertEquals(ids, new ArrayList<>(this.dao.allowListIds));
  }

  @Test
  public void noGlobalUnitsAskNothingFromTheStoreBecauseAnEmptyInListIsNotValidSql() {
    assertTrue(this.manager.allowListFor(ALLOW_LIST_KEY, Collections.<Long>emptyList()).isEmpty());
    assertTrue(this.manager.allowListFor(ALLOW_LIST_KEY, null).isEmpty());
    assertTrue(this.dao.calls.isEmpty());
  }

  // ---- findByNonce ------------------------------------------------------------------------------------------------

  @Test
  public void findByNonceReturnsTheStoredRowOrNullWhenThereIsNone() {
    OtpChallenge stored = row(1, null, EXPIRES_AT);
    this.dao.rowOnReRead = stored;

    assertSame(stored, this.manager.findByNonce(NONCE));

    this.dao.rowOnReRead = null;
    assertNull(this.manager.findByNonce(NONCE));
  }

  // ---- every method that writes is transactional: a write with no transaction is silently rolled back ------------

  @Test
  public void everyMethodThatWritesIsTransactional() throws NoSuchMethodException {
    Method[] writers = {
      OtpChallengeManagerImpl.class.getMethod("issue", String.class, long.class, String.class, String.class,
        Instant.class),
      OtpChallengeManagerImpl.class.getMethod("registerMismatch", String.class),
      OtpChallengeManagerImpl.class.getMethod("consume", String.class),
      OtpChallengeManagerImpl.class.getMethod("purgeExpired", Instant.class)};

    for (Method writer : writers) {
      Transactional transactional = writer.getAnnotation(Transactional.class);
      assertNotNull(writer.getName() + " writes and must be @Transactional", transactional);
      assertFalse(writer.getName() + " must not be read-only", transactional.readOnly());
    }
  }

  @Test
  public void thePurgeRunsInATransactionOfItsOwnThatNeverJoinsTheCallers() throws NoSuchMethodException {
    Transactional transactional =
      OtpChallengeManagerImpl.class.getMethod("purgeExpired", Instant.class).getAnnotation(Transactional.class);

    assertNotNull(transactional);
    assertEquals(Propagation.REQUIRES_NEW, transactional.propagation());
  }

  private static OtpChallenge row(int attempts, Instant consumedAt, Instant expiresAt) {
    OtpChallenge challenge = new OtpChallenge();
    challenge.setNonce(NONCE);
    challenge.setAttempts(attempts);
    challenge.setConsumedAt(consumedAt);
    challenge.setExpiresAt(expiresAt);
    return challenge;
  }

  /** The fake DAO: it answers what its fields say and records what the manager asked, in order. */
  private static final class FakeOtpChallengeDao implements OtpChallengeDAO {

    final List<String> calls = new ArrayList<>();

    int supersedeResult = 0;
    String supersedeEmailHmac;
    Instant supersedeNow;

    int incrementResult = MATCHED;
    String incrementNonce;
    Integer attemptsAfterIncrement;
    OtpChallenge rowOnReRead;

    final Map<Long, String> allowListValues = new HashMap<>();
    String allowListKey;
    Collection<Long> allowListIds;

    int deleteResult;
    Instant deleteCutoff;

    int consumeResult = MATCHED;
    String consumeNonce;
    Instant consumeNow;

    int insertResult = MATCHED;
    RuntimeException insertFailure;
    String insertEmailHmac;
    long insertGlobalUnitId;
    String insertNonce;
    String insertCodeHmac;
    Instant insertExpiresAt;
    Instant insertNow;

    @Override
    public Map<Long, String> findAllowListValues(String key, Collection<Long> globalUnitIds) {
      this.calls.add("findAllowListValues");
      this.allowListKey = key;
      this.allowListIds = globalUnitIds;
      return this.allowListValues;
    }

    @Override
    public Integer findAttempts(String nonce) {
      this.calls.add("findAttempts");
      return this.attemptsAfterIncrement;
    }

    @Override
    public OtpChallenge findByNonce(String nonce) {
      this.calls.add("findByNonce");
      return this.rowOnReRead;
    }

    @Override
    public int incrementAttempts(String nonce) {
      this.calls.add("incrementAttempts");
      this.incrementNonce = nonce;
      return this.incrementResult;
    }

    @Override
    public int insert(String emailHmac, long globalUnitId, String nonce, String codeHmac, Instant expiresAt,
      Instant now) {
      this.calls.add("insert");
      this.insertEmailHmac = emailHmac;
      this.insertGlobalUnitId = globalUnitId;
      this.insertNonce = nonce;
      this.insertCodeHmac = codeHmac;
      this.insertExpiresAt = expiresAt;
      this.insertNow = now;
      if (this.insertFailure != null) {
        throw this.insertFailure;
      }
      return this.insertResult;
    }

    @Override
    public int consume(String nonce, Instant now) {
      this.calls.add("consume");
      this.consumeNonce = nonce;
      this.consumeNow = now;
      return this.consumeResult;
    }

    @Override
    public int deleteExpiredBefore(Instant olderThan) {
      this.calls.add("deleteExpiredBefore");
      this.deleteCutoff = olderThan;
      return this.deleteResult;
    }

    @Override
    public int supersede(String emailHmac, Instant now) {
      this.calls.add("supersede");
      this.supersedeEmailHmac = emailHmac;
      this.supersedeNow = now;
      return this.supersedeResult;
    }
  }
}
