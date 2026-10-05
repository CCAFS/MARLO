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

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Properties;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * EmailPlainText writes the text/plain part of every email. The role notifications are checked on the real AICCRA
 * shell and copy, loaded from the i18n files and formatted the way Struts does.
 */
public class EmailPlainTextTest {

  private static Properties aiccra() throws IOException {
    Properties base = new Properties();
    try (InputStream stream = EmailPlainTextTest.class.getResourceAsStream("/global.properties")) {
      base.load(stream);
    }
    Properties custom = new Properties(base);
    try (InputStream stream = EmailPlainTextTest.class.getResourceAsStream("/custom/aicrra.properties")) {
      custom.load(stream);
    }
    return custom;
  }

  private static String format(Properties texts, String key, Object... args) {
    return MessageFormat.format(texts.getProperty(key), args);
  }

  @Test
  public void testRoleNotificationReadsInOrder() throws IOException {
    Properties texts = aiccra();
    String body = format(texts, "email.role.dear", "Jane")
      + format(texts, "email.programManagement.assigned", "AICCRA",
        format(texts, "email.programManagement.responsibilities"))
      + format(texts, "email.role.bye");
    String html = format(texts, "email.layout", body, "https://aiccra.marlo.cgiar.org");

    String text = EmailPlainText.fromHtml(html);

    assertFalse("no markup is left: " + text, text.contains("<") || text.contains(">"));
    String[] expectedInOrder = {"MARLO", "Managing Agricultural Research for Learning and Outcomes", "Dear Jane,",
      "You've been added to the Project Management Committee (PMC) for AICCRA in MARLO.",
      "As a PMC member, you may be asked to:", "- Set up and edit the component Overall Performance Indicators.",
      "- Enter the detailed information", "Thank you!", "Kind regards,\nMARLO Team",
      "You are receiving this email because you are registered in the MARLO Platform as a user.",
      "MARLOSupport@cgiar.org"};
    int from = 0;
    for (String expected : expectedInOrder) {
      int at = text.indexOf(expected, from);
      assertTrue("missing or out of order: " + expected + "\n---\n" + text, at >= 0);
      from = at + expected.length();
    }
    // The paragraphs stay apart, and the spacer cells leave no run of blank lines.
    assertTrue(text, text.contains("Dear Jane,\n\nYou've been added"));
    // The items of a list follow each other, one per line, after their intro paragraph.
    assertTrue(text,
      text.contains("asked to:\n\n- Set up and edit the component Overall Performance Indicators.\n- Enter"));
    assertFalse(text, text.contains("\n\n\n"));
  }

  @Test
  public void testLinksKeepTheirUrl() {
    String text = EmailPlainText.fromHtml("Go to <a href=\"https://aiccra.marlo.cgiar.org\">MARLO</a>. Write to "
      + "<a href=\"mailto:MARLOSupport@cgiar.org\">MARLOSupport@cgiar.org</a> or <a href=\"https://x.org\">"
      + "https://x.org</a>.");

    assertEquals("Go to MARLO (https://aiccra.marlo.cgiar.org). Write to MARLOSupport@cgiar.org or https://x.org.",
      text);
  }

  @Test
  public void testLegacyBodiesKeepTheirBreaks() {
    // The other notifications are still built with <br> and entities, as the SendMailS testing header is.
    String text = EmailPlainText.fromHtml("To: a@b.org<br>CC: null<br>----<br><br>Dear Ana, <br><br>"
      + "Your deliverable &amp; its files were <b>submitted</b>.");

    assertEquals("To: a@b.org\nCC: null\n----\n\nDear Ana,\n\nYour deliverable & its files were submitted.", text);
  }

  @Test
  public void testEmptyBodyGivesEmptyText() {
    assertEquals("", EmailPlainText.fromHtml(null));
    assertEquals("", EmailPlainText.fromHtml("  "));
  }
}
