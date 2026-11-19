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
package org.cgiar.ccafs.marlo.utils;

import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.model.CustomParameter;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.Parameter;
import org.cgiar.ccafs.marlo.utils.PartnerRequestEmails.Email;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * The six emails of the partner and country-office requests as the builder assembles them (CHG-PARTREQ-001 FN-002,
 * FN-003, FN-009, FN-010, NF-002). The texts are the real i18n files, read through {@link UnitTextProvider}; the
 * expected values are literals written from the design, not built with the builder.
 */
public class PartnerRequestEmailsTest {

  private static final String CDN = "https://cdn.test";
  private static final String SHELL_LOGO = CDN + "/global/images/email/marlo-logo.png";
  private static final String DEAR_SUPPORT = "font-weight:bold;\">Dear MARLO support team,</p>";
  private static final String DEAR_ANA = "font-weight:bold;\">Dear Ana,</p>";
  private static final String BYE = "Thank you!</p><p style=\"margin:0;\">Kind regards,<br>MARLO Team</p>";
  private static final String HOSTILE = "O'Brien <b>";
  private static final String HOSTILE_ESCAPED = "O'Brien &lt;b&gt;";

  private static GlobalUnit unit(String acronym, String customFile, String emailSwitch) {
    GlobalUnit unit = new GlobalUnit();
    unit.setAcronym(acronym);
    unit.setCustomParameters(new HashSet<CustomParameter>());
    if (customFile != null) {
      unit.getCustomParameters().add(parameter(APConstants.CRP_CUSTOM_FILE, customFile));
    }
    if (emailSwitch != null) {
      unit.getCustomParameters().add(parameter(APConstants.CRP_EMAIL_NOTIFICATIONS, emailSwitch));
    }
    return unit;
  }

  private static CustomParameter parameter(String key, String value) {
    Parameter parameter = new Parameter();
    parameter.setKey(key);
    CustomParameter custom = new CustomParameter();
    custom.setParameter(parameter);
    custom.setValue(value);
    return custom;
  }

  /** The six emails of a unit, labelled, with the hostile text in every argument that reaches a body. */
  private static Map<String, Email> sixEmails(GlobalUnit unit) {
    Map<String, Email> emails = new LinkedHashMap<String, Email>();
    emails.put("newPartner", PartnerRequestEmails.newPartner(unit, CDN, HOSTILE, "req@x.org", HOSTILE, "AC", "NGO",
      "Kenya", "https://acme.org", "Project P1", "https://marlo.test/review?a=1&b=2"));
    emails.put("newOffice", PartnerRequestEmails.newOffice(unit, CDN, HOSTILE, "req@x.org", HOSTILE, "Kenya, Peru",
      "KE, PE", "P1 - Soil"));
    emails.put("partnerAccepted", PartnerRequestEmails.partnerAccepted(unit, CDN, "Ana", HOSTILE, "Acme"));
    emails.put("partnerRejected", PartnerRequestEmails.partnerRejected(unit, CDN, "Ana", "Acme", HOSTILE));
    emails.put("officeAccepted", PartnerRequestEmails.officeAccepted(unit, CDN, "Ana", "Acme", "Kenya, Peru"));
    emails.put("officeRejected", PartnerRequestEmails.officeRejected(unit, CDN, "Ana", "Acme", "Kenya", HOSTILE));
    return emails;
  }

  @Test
  public void testEverySixEmailIsInsideTheShellWithTheRoleGreetingAndSignOffAndNoLegacyFooter() {
    String legacyFooter = UnitTextProvider.forCustomFile(null).getText("email.support.noCrpAdmins");
    assertTrue("fixture: the legacy footer text must exist", legacyFooter.length() > 20);
    for (Map.Entry<String, Email> entry : sixEmails(unit("U", "aicrra", null)).entrySet()) {
      String body = entry.getValue().getBody();
      String label = entry.getKey();
      assertTrue(label + " is not inside the shell", body.contains(SHELL_LOGO));
      boolean alert = label.startsWith("new");
      assertTrue(label + " greeting", body.contains(alert ? DEAR_SUPPORT : DEAR_ANA));
      assertTrue(label + " sign-off", body.contains(BYE));
      assertFalse(label + " legacy footer", body.contains(legacyFooter));
      assertFalse(label + " legacy sign-off", body.contains("Best regards"));
    }
  }

  @Test
  public void testBodyArgumentsAreEscapedAndSubjectsCarryNoJustification() {
    Map<String, Email> emails = sixEmails(unit("U", null, null));
    assertTrue(emails.get("partnerRejected").getBody(), emails.get("partnerRejected").getBody()
      .contains("<em>" + HOSTILE_ESCAPED + "</em>"));
    assertTrue(emails.get("officeRejected").getBody(), emails.get("officeRejected").getBody()
      .contains("<em>" + HOSTILE_ESCAPED + "</em>"));
    assertTrue(emails.get("newPartner").getBody().contains("Institution: " + HOSTILE_ESCAPED));
    assertTrue(emails.get("newPartner").getBody().contains("review?a=1&amp;b=2"));
    for (Map.Entry<String, Email> entry : emails.entrySet()) {
      assertFalse(entry.getKey() + " body holds the raw text", entry.getValue().getBody().contains(HOSTILE));
    }
    assertFalse(emails.get("partnerRejected").getSubject(), emails.get("partnerRejected").getSubject()
      .contains("O'Brien"));
    assertFalse(emails.get("officeRejected").getSubject(), emails.get("officeRejected").getSubject()
      .contains("O'Brien"));
  }

  @Test
  public void testSubjectArgumentsAreNotEscaped() {
    Email email = PartnerRequestEmails.newPartner(unit("U", null, null), CDN, "Ana", "a@x.org", "A&B <x>", "AB", "NGO",
      "Kenya", "https://ab.org", "P1", "https://marlo.test/r");
    assertEquals("[MARLO - U] Partner verification - A&B <x>", email.getSubject());
    assertTrue(email.getBody(), email.getBody().contains("Institution: A&amp;B &lt;x&gt;"));
  }

  @Test
  public void testSubjectsUseTheReusedKeysWithTheirArguments() {
    GlobalUnit unit = unit("UNIT-A", "global", null);
    Map<String, Email> emails = sixEmails(unit);
    assertEquals("[MARLO - UNIT-A] Partner verification - O'Brien <b>", emails.get("newPartner").getSubject());
    assertEquals("[MARLO - UNIT-A] Add office location (KE, PE) to O'Brien <b>", emails.get("newOffice").getSubject());
    assertEquals("[MARLO] Partner verification - Approved Acme", emails.get("partnerAccepted").getSubject());
    assertEquals("[MARLO] Partner verification - Rejected Acme", emails.get("partnerRejected").getSubject());
    assertEquals("[MARLO] Country office(s) verification - Approved Acme", emails.get("officeAccepted").getSubject());
    assertEquals("[MARLO] Country office(s) verification - Rejected Acme", emails.get("officeRejected").getSubject());
  }

  @Test
  public void testTextsComeFromTheCustomFileOfTheUnitOfTheEmail() {
    Email aiccra = PartnerRequestEmails.partnerAccepted(unit("UNIT-A", "aicrra", null), CDN, "Ana", "Acme", "Acme");
    Email base = PartnerRequestEmails.partnerAccepted(unit("UNIT-B", "global", null), CDN, "Ana", "Acme", "Acme");
    assertEquals("[AICCRA] Partner verification - Approved Acme", aiccra.getSubject());
    assertEquals("[MARLO] Partner verification - Approved Acme", base.getSubject());
    Email aiccraAlert = PartnerRequestEmails.newPartner(unit("UNIT-A", "aicrra", null), CDN, "R", "r@x.org", "Acme",
      "AC", "NGO", "Kenya", "https://a.org", "P1", "https://marlo.test/r");
    assertEquals("[AICCRA - UNIT-A] Partner verification - Acme", aiccraAlert.getSubject());
  }

  @Test
  public void testSwitchIsOffOnlyWhenTheCustomParameterIsNotTrue() {
    assertFalse(PartnerRequestEmails.isEnabled(unit("U", null, "false")));
    assertFalse(PartnerRequestEmails.isEnabled(unit("U", null, "FALSE")));
    assertTrue(PartnerRequestEmails.isEnabled(unit("U", null, "true")));
    assertTrue(PartnerRequestEmails.isEnabled(unit("U", null, "TRUE")));
    assertTrue(PartnerRequestEmails.isEnabled(unit("U", null, null)));
    assertTrue(PartnerRequestEmails.isEnabled(unit("U", "aicrra", "true")));
    assertFalse(PartnerRequestEmails.isEnabled(null));
  }
}
