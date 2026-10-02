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
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * SendMailS.buildContent decides the MIME structure of every email MARLO sends: a text/plain part before the
 * text/html one, and the attachment outside the alternative, next to it.
 */
public class SendMailSContentTest {

  private static final String HTML = "<p>Dear Jane,</p><p>You've been added to the <b>PMC</b>.</p>";

  /**
   * Sets the content on a message and saves it, as Transport.send does before sending: the Content-Type headers of
   * the parts are only written then.
   */
  private static MimeMultipart sent(MimeMultipart content) throws MessagingException {
    MimeMessage message = new MimeMessage(Session.getInstance(new Properties()));
    message.setContent(content);
    message.saveChanges();
    return content;
  }

  private static void assertType(String expected, BodyPart part) throws MessagingException {
    assertTrue(expected + " expected, got " + part.getContentType(), part.isMimeType(expected));
  }

  @Test
  public void testHtmlBodyHasTextThenHtml() throws MessagingException, IOException {
    MimeMultipart content = sent(SendMailS.buildContent(HTML, true, null, null, null));

    assertTrue(content.getContentType(), content.getContentType().startsWith("multipart/alternative"));
    assertEquals(2, content.getCount());
    assertType("text/plain", content.getBodyPart(0));
    assertType("text/html", content.getBodyPart(1));
    assertEquals("Dear Jane,\n\nYou've been added to the PMC.", content.getBodyPart(0).getContent());
    assertEquals(HTML, content.getBodyPart(1).getContent());
  }

  @Test
  public void testAttachmentGoesNextToTheBody() throws MessagingException, IOException {
    byte[] file = "%PDF-1.4".getBytes(StandardCharsets.US_ASCII);

    MimeMultipart content = sent(SendMailS.buildContent(HTML, true, file, "application/pdf", "guide.pdf"));

    assertTrue(content.getContentType(), content.getContentType().startsWith("multipart/mixed"));
    assertEquals(2, content.getCount());
    MimeMultipart body = (MimeMultipart) content.getBodyPart(0).getContent();
    assertTrue(body.getContentType(), body.getContentType().startsWith("multipart/alternative"));
    assertType("text/plain", body.getBodyPart(0));
    assertType("text/html", body.getBodyPart(1));
    assertType("application/pdf", content.getBodyPart(1));
    assertEquals("guide.pdf", content.getBodyPart(1).getFileName());
  }

  @Test
  public void testNonAsciiSurvivesInBothParts() throws MessagingException, IOException {
    String html = "<p>Welcome to MARLO \u2014 Bogot\u00e1, C\u00f4te d\u2019Ivoire</p>";

    MimeMultipart content = sent(SendMailS.buildContent(html, true, null, null, null));

    assertTrue(content.getBodyPart(0).getContentType(), content.getBodyPart(0).getContentType().contains("utf-8"));
    assertEquals("Welcome to MARLO \u2014 Bogot\u00e1, C\u00f4te d\u2019Ivoire", content.getBodyPart(0).getContent());
    assertEquals(html, content.getBodyPart(1).getContent());
  }

  @Test
  public void testPlainBodyHasOnlyText() throws MessagingException, IOException {
    MimeMultipart content = sent(SendMailS.buildContent("Plain body", false, null, null, null));

    assertEquals(1, content.getCount());
    assertType("text/plain", content.getBodyPart(0));
    assertEquals("Plain body", content.getBodyPart(0).getContent());
  }
}
