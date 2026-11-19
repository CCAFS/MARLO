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

import com.opensymphony.xwork2.TextProvider;
import org.apache.commons.text.StringEscapeUtils;

/**
 * Builds the six emails of the partner and country-office requests (CHG-PARTREQ-001 FN-002, FN-003, FN-009, FN-010):
 * the two alerts to the support mailbox and the four answers to the requesters. Each email is the greeting
 * {@code email.role.dear}, a body key and the sign-off {@code email.role.bye}, inside the branded shell of its Global
 * Unit. The texts are read from the custom file of that unit with {@link UnitTextProvider}, because the JSON actions
 * that send them have no i18n interceptor. The arguments of a body are HTML-escaped; the arguments of a subject are
 * not, since the subject is a plain-text header. The class only builds text: whoever sends the email checks
 * {@link #isEnabled(GlobalUnit)} first, because the mail sender never suppresses mail to the notification mailbox.
 */
public final class PartnerRequestEmails {

  /** A subject and a body, ready for {@code SendMailS.send}. */
  public static final class Email {

    private final String subject;
    private final String body;

    Email(String subject, String body) {
      this.subject = subject;
      this.body = body;
    }

    public String getBody() {
      return this.body;
    }

    public String getSubject() {
      return this.subject;
    }
  }

  static final String SUPPORT_NAME_KEY = "marloRequestInstitution.mail.supportName";

  private PartnerRequestEmails() {
  }

  /**
   * Builds an email: the subject from its plain arguments and the body inside the shell.
   *
   * @param texts the texts of the Global Unit of the email.
   * @param assetsUrl the URL the images of the shell are served from.
   * @param subjectKey the subject key.
   * @param subjectArgs the subject arguments, as they are shown.
   * @param dearName the name in the greeting.
   * @param bodyKey the body key.
   * @param bodyArgs the body arguments, to be escaped.
   */
  private static Email build(TextProvider texts, String assetsUrl, String subjectKey, String[] subjectArgs,
    String dearName, String bodyKey, String... bodyArgs) {
    StringBuilder body = new StringBuilder();
    body.append(texts.getText("email.role.dear", new String[] {escape(dearName)}));
    body.append(texts.getText(bodyKey, escapeAll(bodyArgs)));
    body.append(texts.getText("email.role.bye"));
    String subject = texts.getText(subjectKey, plainAll(subjectArgs));
    return new Email(subject, EmailLayout.wrap(texts, assetsUrl, body.toString()));
  }

  private static String escape(String value) {
    return value == null ? "" : StringEscapeUtils.escapeHtml4(value);
  }

  private static String[] escapeAll(String[] values) {
    String[] escaped = new String[values.length];
    for (int i = 0; i < values.length; i++) {
      escaped[i] = escape(values[i]);
    }
    return escaped;
  }

  /**
   * Whether the Global Unit lets MARLO send these emails: the same rule as the email-notification switch has always
   * had, so a unit without the parameter, or with an empty value, sends. A missing unit sends nothing.
   */
  public static boolean isEnabled(GlobalUnit unit) {
    if (unit == null || unit.getCustomParameters() == null) {
      return false;
    }
    return unit.getCustomParameters().stream()
      .filter(c -> c.getParameter() != null && c.getParameter().getKey() != null
        && c.getParameter().getKey().equalsIgnoreCase(APConstants.CRP_EMAIL_NOTIFICATIONS))
      .allMatch(c -> c.getValue() == null || c.getValue().equalsIgnoreCase("true"));
  }

  /** The email to the support mailbox for a new country-office request. */
  public static Email newOffice(GlobalUnit unit, String assetsUrl, String requesterName, String requesterEmail,
    String institution, String countries, String countriesIso, String sourceProject) {
    TextProvider texts = unitTexts(unit);
    return build(texts, assetsUrl, "marloRequestInstitution.office.email.subject",
      new String[] {acronym(unit), countriesIso, institution}, texts.getText(SUPPORT_NAME_KEY),
      "marloRequestInstitution.mail.newOffice", requesterName, requesterEmail, institution, countries,
      sourceProject);
  }

  /** The email to the support mailbox for a new partner request. */
  public static Email newPartner(GlobalUnit unit, String assetsUrl, String requesterName, String requesterEmail,
    String name, String acronym, String type, String country, String website, String source, String reviewUrl) {
    TextProvider texts = unitTexts(unit);
    return build(texts, assetsUrl, "marloRequestInstitution.email.subject", new String[] {acronym(unit), name},
      texts.getText(SUPPORT_NAME_KEY), "marloRequestInstitution.mail.newPartner", requesterName, requesterEmail, name,
      acronym, type, country, website, source, reviewUrl);
  }

  /** The answer to the requester of an accepted country office. */
  public static Email officeAccepted(GlobalUnit unit, String assetsUrl, String dearName, String institution,
    String countries) {
    TextProvider texts = unitTexts(unit);
    return build(texts, assetsUrl, "marloRequestInstitution.office.accept.email.subject",
      new String[] {institution}, dearName, "marloRequestInstitution.mail.officeAccepted", institution, countries);
  }

  /** The answer to the requester of a rejected country office. */
  public static Email officeRejected(GlobalUnit unit, String assetsUrl, String dearName, String institution,
    String countries, String justification) {
    TextProvider texts = unitTexts(unit);
    return build(texts, assetsUrl, "marloRequestInstitution.office.reject.email.subject",
      new String[] {institution}, dearName, "marloRequestInstitution.mail.officeRejected", institution, countries,
      justification);
  }

  /** The answer to the requester of an accepted partner. */
  public static Email partnerAccepted(GlobalUnit unit, String assetsUrl, String dearName, String institution,
    String partnerName) {
    TextProvider texts = unitTexts(unit);
    return build(texts, assetsUrl, "marloRequestInstitution.accept.email.subject", new String[] {partnerName},
      dearName, "marloRequestInstitution.mail.accepted", institution);
  }

  /** The answer to the requester of a rejected partner. */
  public static Email partnerRejected(GlobalUnit unit, String assetsUrl, String dearName, String partnerName,
    String justification) {
    TextProvider texts = unitTexts(unit);
    return build(texts, assetsUrl, "marloRequestInstitution.reject.email.subject", new String[] {partnerName},
      dearName, "marloRequestInstitution.mail.rejected", partnerName, justification);
  }

  /**
   * The texts of a Global Unit, read from its own custom file and not from the shared provider, which holds whichever
   * unit the server served last. A unit with no custom file gets the base texts.
   */
  public static TextProvider unitTexts(GlobalUnit unit) {
    String file = null;
    if (unit != null && unit.getCustomParameters() != null) {
      for (CustomParameter parameter : unit.getCustomParameters()) {
        if (parameter.getParameter() != null && APConstants.CRP_CUSTOM_FILE.equals(parameter.getParameter().getKey())) {
          file = parameter.getValue();
        }
      }
    }
    return UnitTextProvider.forCustomFile(file);
  }

  private static String acronym(GlobalUnit unit) {
    return unit == null ? null : unit.getAcronym();
  }

  private static String[] plainAll(String[] values) {
    String[] plain = new String[values.length];
    for (int i = 0; i < values.length; i++) {
      plain[i] = values[i] == null ? "" : values[i];
    }
    return plain;
  }
}
