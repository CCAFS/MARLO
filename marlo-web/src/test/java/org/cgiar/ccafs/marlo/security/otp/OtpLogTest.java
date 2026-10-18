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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.cgiar.ccafs.marlo.security.otp.OtpLog.DeliveryOutcome;
import org.cgiar.ccafs.marlo.security.otp.OtpLog.RequestOutcome;
import org.cgiar.ccafs.marlo.security.otp.OtpLog.VerifyOutcome;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.LoggerFactory;

/**
 * CHG-OTP-LOGIN-001-T05, OPS-001 and OPS-002: one line per call in the agreed shape, values sanitised, and no way
 * to put a code, nonce, email or session id on the line. Expected lines are literals.
 */
public class OtpLogTest {

  private Logger otpLogger;
  private Level originalLevel;
  private ListAppender<ILoggingEvent> appender;

  @Before
  public void attachAppender() {
    this.otpLogger = (Logger) LoggerFactory.getLogger(OtpLog.class);
    this.originalLevel = this.otpLogger.getLevel();
    this.otpLogger.setLevel(Level.TRACE);
    this.appender = new ListAppender<ILoggingEvent>();
    this.appender.start();
    this.otpLogger.addAppender(this.appender);
  }

  @After
  public void detachAppender() {
    this.otpLogger.detachAppender(this.appender);
    this.otpLogger.setLevel(this.originalLevel);
  }

  private String onlyLine() {
    assertEquals("exactly one line per call", 1, this.appender.list.size());
    return this.appender.list.get(0).getFormattedMessage();
  }

  @Test
  public void requestLineHasTheAgreedShapeAtInfo() {
    OtpLog.request(RequestOutcome.SENT, "CCAFS", "partner.org", 12L);

    assertEquals("auth.otp.request outcome=sent gu=ccafs domain=partner.org ms=12", this.onlyLine());
    assertEquals(Level.INFO, this.appender.list.get(0).getLevel());
  }

  @Test
  public void aSentLineOfACodeIssuedByThePasswordOffOverrideEndsWithViaPasswordOff() {
    OtpLog.request(RequestOutcome.SENT, "CCAFS", "partner.org", 12L, true);

    assertEquals("auth.otp.request outcome=sent gu=ccafs domain=partner.org ms=12 via=password_off", this.onlyLine());
    assertEquals(Level.INFO, this.appender.list.get(0).getLevel());
  }

  @Test
  public void aSentLineWithoutTheOverrideIsTheUnchangedLine() {
    OtpLog.request(RequestOutcome.SENT, "CCAFS", "partner.org", 12L, false);

    assertEquals("auth.otp.request outcome=sent gu=ccafs domain=partner.org ms=12", this.onlyLine());
  }

  @Test
  public void noOutcomeButSentCanCarryViaWhateverTheCallerPasses() {
    // The suffix says "a code was issued because the password is off": a neutral, refused or failed request issued no
    // real code, so it must never carry it, even if a caller passes true.
    String[] names = {"sent", "neutral", "denied_domain", "terms_required", "rate_limited", "unconfigured",
      "internal_error", "bad_method"};
    RequestOutcome[] outcomes = RequestOutcome.values();
    assertEquals(names.length, outcomes.length);
    for (int i = 1; i < outcomes.length; i++) {
      this.appender.list.clear();
      OtpLog.request(outcomes[i], "G", "d.org", 1L, true);
      assertEquals(names[i], "auth.otp.request outcome=" + names[i] + " gu=g domain=d.org ms=1", this.onlyLine());
    }
  }

  @Test
  public void verifyLineHasTheAgreedShapeAtInfo() {
    OtpLog.verify(VerifyOutcome.NOT_ELIGIBLE, "AICCRA", "partner.org", 7L);

    assertEquals("auth.otp.verify outcome=not_eligible gu=aiccra domain=partner.org ms=7", this.onlyLine());
    assertEquals(Level.INFO, this.appender.list.get(0).getLevel());
  }

  @Test
  public void deliveredLineIsInfoAndFailedLineIsWarn() {
    OtpLog.delivery(DeliveryOutcome.DELIVERED, "CCAFS", "partner.org", 340L);
    OtpLog.delivery(DeliveryOutcome.EMAIL_FAILED, "CCAFS", "partner.org", 10_000L);

    assertEquals(2, this.appender.list.size());
    assertEquals("auth.otp.delivery outcome=delivered gu=ccafs domain=partner.org ms=340",
      this.appender.list.get(0).getFormattedMessage());
    assertEquals(Level.INFO, this.appender.list.get(0).getLevel());
    assertEquals("auth.otp.delivery outcome=email_failed gu=ccafs domain=partner.org ms=10000",
      this.appender.list.get(1).getFormattedMessage());
    assertEquals(Level.WARN, this.appender.list.get(1).getLevel());
  }

  @Test
  public void everyRequestOutcomeHasItsClosedSetName() {
    String[] expected = {"sent", "neutral", "denied_domain", "terms_required", "rate_limited", "unconfigured",
      "internal_error", "bad_method"};
    RequestOutcome[] outcomes = RequestOutcome.values();

    assertEquals(expected.length, outcomes.length);
    for (int i = 0; i < outcomes.length; i++) {
      this.appender.list.clear();
      OtpLog.request(outcomes[i], "G", "d.org", 1L);
      assertEquals("auth.otp.request outcome=" + expected[i] + " gu=g domain=d.org ms=1", this.onlyLine());
    }
  }

  @Test
  public void everyVerifyOutcomeHasItsClosedSetName() {
    String[] expected = {"ok", "mismatch", "exhausted", "invalid", "not_eligible", "rate_limited", "failed",
      "internal_error", "bad_method"};
    VerifyOutcome[] outcomes = VerifyOutcome.values();

    assertEquals(expected.length, outcomes.length);
    for (int i = 0; i < outcomes.length; i++) {
      this.appender.list.clear();
      OtpLog.verify(outcomes[i], "G", "d.org", 1L);
      assertEquals("auth.otp.verify outcome=" + expected[i] + " gu=g domain=d.org ms=1", this.onlyLine());
    }
  }

  @Test
  public void newlinesInTheDomainAreRemovedSoTheCallStaysOneLine() {
    OtpLog.request(RequestOutcome.DENIED_DOMAIN, "CCAFS",
      "evil.org\r\n2026-10-13 10:00:00 INFO auth.otp.verify outcome=ok gu=CCAFS", 3L);

    String line = this.onlyLine();
    assertEquals("auth.otp.request outcome=denied_domain gu=ccafs "
      + "domain=evil.org2026-10-13_10_00_00_info_auth.otp.verify_outcome_ok_gu_ccafs ms=3", line);
    assertFalse(line.contains("\n"));
    assertFalse(line.contains("\r"));
  }

  @Test
  public void aDomainWithASpaceCannotForgeASecondOutcomeField() {
    OtpLog.request(RequestOutcome.SENT, "CCAFS", "evil.org outcome=ok", 1L);

    String line = this.onlyLine();
    assertEquals("auth.otp.request outcome=sent gu=ccafs domain=evil.org_outcome_ok ms=1", line);
    assertEquals(1, line.split("outcome=", -1).length - 1);
  }

  @Test
  public void anAcronymCannotForgeFieldsEither() {
    OtpLog.verify(VerifyOutcome.OK, "G ms=0 domain=x", "partner.org", 4L);

    String line = this.onlyLine();
    assertEquals("auth.otp.verify outcome=ok gu=g_ms_0_domain_x domain=partner.org ms=4", line);
    assertEquals(1, line.split("domain=", -1).length - 1);
  }

  @Test
  public void everyCharacterOutsideLowerCaseLettersDigitsDotAndHyphenBecomesAnUnderscore() {
    // Kelvin sign U+212A and u-umlaut U+00FC are not ASCII: each becomes one underscore and is never case-folded.
    OtpLog.request(RequestOutcome.SENT, "Ab-9", "\u212Aey.org|t\u00fcrk.org;x\ty", 2L);

    assertEquals("auth.otp.request outcome=sent gu=ab-9 domain=_ey.org_t_rk.org_x_y ms=2", this.onlyLine());
  }

  @Test
  public void newlinesInTheAcronymAreRemovedToo() {
    OtpLog.verify(VerifyOutcome.OK, "CC\nAFS", "partner.org", 3L);

    assertEquals("auth.otp.verify outcome=ok gu=ccafs domain=partner.org ms=3", this.onlyLine());
  }

  @Test
  public void nullValuesPrintEmptyNeverTheWordNull() {
    OtpLog.request(RequestOutcome.UNCONFIGURED, null, null, 0L);

    assertEquals("auth.otp.request outcome=unconfigured gu= domain= ms=0", this.onlyLine());
  }

  @Test
  public void aNullRequestOutcomeLogsInternalErrorInsteadOfThrowing() {
    OtpLog.request(null, "CCAFS", "partner.org", 5L);

    assertEquals("auth.otp.request outcome=internal_error gu=ccafs domain=partner.org ms=5", this.onlyLine());
  }

  @Test
  public void aNullVerifyOutcomeLogsInternalErrorInsteadOfThrowing() {
    OtpLog.verify(null, "CCAFS", "partner.org", 5L);

    assertEquals("auth.otp.verify outcome=internal_error gu=ccafs domain=partner.org ms=5", this.onlyLine());
  }

  @Test
  public void aNullDeliveryOutcomeLogsInternalErrorAtWarnInsteadOfThrowing() {
    OtpLog.delivery(null, "CCAFS", "partner.org", 5L);

    assertEquals("auth.otp.delivery outcome=internal_error gu=ccafs domain=partner.org ms=5", this.onlyLine());
    assertEquals(Level.WARN, this.appender.list.get(0).getLevel());
  }
}
