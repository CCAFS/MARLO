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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.lang.reflect.Proxy;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import com.opensymphony.xwork2.TextProvider;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * EmailLayout resolves the shell through the same i18n files the actions read, so these tests load the real
 * global.properties and custom files, and format them with MessageFormat the way Struts does.
 */
public class EmailLayoutTest {

  private static final String ASSETS_URL = "https://aiccra.marlo.cgiar.org";

  // A body with an apostrophe and braces, which MessageFormat would break if the body were read as a pattern.
  private static final String BODY = "Dear Jane,<br><br>You''re {now} part of the PMC.";

  private static Properties load(Properties defaults, String resource) throws IOException {
    Properties properties = new Properties(defaults);
    try (InputStream stream = EmailLayoutTest.class.getResourceAsStream(resource)) {
      properties.load(stream);
    }
    return properties;
  }

  /**
   * Answers getText(key, String[]) as Struts does: the pattern of the key formatted with the arguments, or the key
   * itself when no file defines it.
   */
  private static TextProvider texts(Properties properties) {
    return (TextProvider) Proxy.newProxyInstance(TextProvider.class.getClassLoader(),
      new Class<?>[] {TextProvider.class}, (proxy, invoked, args) -> {
        String key = (String) args[0];
        String pattern = properties.getProperty(key);
        return pattern == null ? key : MessageFormat.format(pattern, (Object[]) args[1]);
      });
  }

  private static String wrapWith(String customFile) throws IOException {
    Properties base = load(null, "/global.properties");
    return EmailLayout.wrap(texts(load(base, "/custom/" + customFile)), ASSETS_URL, BODY);
  }

  @Test
  public void testBaseLayoutWrapsTheBodyInTheShell() throws IOException {
    Properties base = load(null, "/global.properties");

    String email = EmailLayout.wrap(texts(base), ASSETS_URL, BODY);

    assertTrue("the base shell keeps the body untouched", email.contains(BODY));
    assertTrue(email.contains("src=\"" + ASSETS_URL + "/global/images/email/marlo-logo.png\""));
    assertTrue(email.contains("mailto:MARLOSupport@cgiar.org"));
    assertFalse("the body is wrapped, not sent as it is", BODY.equals(email));
  }

  @Test
  public void testAUnitThatOptsOutSendsTheBodyAsItIs() throws IOException {
    // DD-9: a unit that must stay plain sets email.layout={0} in its custom file over the base shell.
    Properties base = load(null, "/global.properties");
    Properties optOut = new Properties(base);
    optOut.setProperty("email.layout", "{0}");

    assertEquals(BODY, EmailLayout.wrap(texts(optOut), ASSETS_URL, BODY));
  }

  @Test
  public void testBaseRoleBlocksAreShellAwareAndTheSharedOnesAreUnchanged() throws IOException {
    // DD-2: inside the shell the footer carries the support address and the do-not-reply notice, so the base role
    // blocks hold the AICCRA values. The shared email.* blocks (read by the notifications outside the shell) keep
    // the pre-shell footer.
    Properties base = load(null, "/global.properties");
    Properties aicrra = load(null, "/custom/aicrra.properties");

    for (String block : new String[] {"dear", "support", "support.noCrpAdmins", "bye"}) {
      assertEquals(block, aicrra.getProperty("email.role." + block), base.getProperty("email.role." + block));
    }
    assertEquals("", base.getProperty("email.role.support"));
    assertEquals("", base.getProperty("email.role.support.noCrpAdmins"));
    assertTrue(base.getProperty("email.role.getStarted").contains("Please sign in to MARLO to get started."));
    assertFalse(base.getProperty("email.role.getStarted").contains("<br>"));

    assertTrue(base.getProperty("email.support").contains("MARLOSupport@cgiar.org"));
    assertTrue(base.getProperty("email.support.noCrpAdmins").contains("MARLOSupport@cgiar.org"));
    assertTrue(base.getProperty("email.bye").contains("Please do not reply to this email"));
    assertEquals("<br><br>Please sign in to MARLO to get started.", base.getProperty("email.getStarted"));
  }

  /**
   * The custom files that override at least one key with the given prefix, found by scanning the folder, so a new
   * override file is covered without editing the test.
   */
  private static List<String> customFilesOverriding(String prefix) throws Exception {
    List<String> names = new ArrayList<>();
    File folder = new File(EmailLayoutTest.class.getResource("/custom").toURI());
    for (File file : folder.listFiles((dir, name) -> name.endsWith(".properties"))) {
      Properties own = load(null, "/custom/" + file.getName());
      for (String key : own.stringPropertyNames()) {
        if (key.startsWith(prefix)) {
          names.add(file.getName());
          break;
        }
      }
    }
    Collections.sort(names);
    return names;
  }

  /** Strips the markup, so an address inside an href and as link text counts once, as the reader sees it. */
  private static String visibleText(String email) {
    return email.replaceAll("<[^>]*>", "");
  }

  private static int count(String text, String needle) {
    int total = 0;
    for (int at = text.indexOf(needle); at >= 0; at = text.indexOf(needle, at + 1)) {
      total++;
    }
    return total;
  }

  /** Appends one line to the problems when the email repeats, or lacks, the support address or the notice. */
  private static void checkFooterOnce(String label, String email, List<String> problems) {
    String visible = visibleText(email).toLowerCase();
    int addresses = count(visible, "marlosupport@cgiar.org");
    int notices = count(visible, "do not reply");
    if (addresses != 1 || notices != 1) {
      problems.add(label + ": support address x" + addresses + ", do-not-reply notice x" + notices);
    }
  }

  @Test
  public void testRoleEmailShowsTheFooterOnceForTheBaseAndEveryUnitThatOverridesARoleKey() throws Exception {
    List<String> units = new ArrayList<>();
    units.add("");
    units.addAll(customFilesOverriding("email.role."));
    assertTrue("the scan finds the overriding units", units.size() > 1);
    Properties base = load(null, "/global.properties");
    List<String> problems = new ArrayList<>();

    for (String unit : units) {
      Properties properties = unit.isEmpty() ? base : load(base, "/custom/" + unit);
      TextProvider texts = texts(properties);
      // The admins list a caller passes can be empty (DD-2), and can hold names.
      for (String admins : new String[] {"", "Jane Admin"}) {
        String body = texts.getText("email.role.dear", new String[] {"Jane"})
          + texts.getText("email.role.support", new String[] {admins})
          + texts.getText("email.role.support.noCrpAdmins", new String[] {admins})
          + texts.getText("email.role.getStarted", new String[] {})
          + texts.getText("email.role.bye", new String[] {});
        String label = (unit.isEmpty() ? "global.properties" : unit) + " (admins='" + admins + "')";
        String email = EmailLayout.wrap(texts, ASSETS_URL, body);
        assertTrue(label + " keeps its body", email.contains("Jane"));
        checkFooterOnce(label, email, problems);
      }
    }
    assertEquals("units that do not show the footer exactly once: " + problems, 0, problems.size());
  }

  @Test
  public void testOtpEmailShowsTheFooterOnceForTheBaseAndEveryUnitThatOverridesIt() throws Exception {
    List<String> units = new ArrayList<>();
    units.add("");
    units.addAll(customFilesOverriding("login.otp.email.html"));
    Properties base = load(null, "/global.properties");
    List<String> problems = new ArrayList<>();

    for (String unit : units) {
      Properties properties = unit.isEmpty() ? base : load(base, "/custom/" + unit);
      TextProvider texts = texts(properties);
      String body = texts.getText("login.otp.email.html",
        new String[] {"MARLO", "123456", "10", properties.getProperty("footer.contactEmail")});
      checkFooterOnce((unit.isEmpty() ? "global.properties" : unit) + " OTP", EmailLayout.wrap(texts, ASSETS_URL, body),
        problems);
    }
    assertEquals("OTP emails that do not show the footer exactly once: " + problems, 0, problems.size());
  }

  /**
   * The DD-9 opt-out block exactly as global.properties documents it: the commented lines below email.layout,
   * uncommented and read as properties, plus email.layout={0}. It is the block a unit file would carry.
   */
  private static Properties optOutUnit(Properties base) throws Exception {
    StringBuilder block = new StringBuilder("email.layout={0}\n");
    String documented = new String(Files.readAllBytes(
      Paths.get(EmailLayoutTest.class.getResource("/global.properties").toURI())), StandardCharsets.ISO_8859_1);
    for (String line : documented.split("\\r?\\n")) {
      if (line.startsWith("# email.role.") || line.startsWith("# login.otp.email.html=")) {
        block.append(line.substring(2)).append('\n');
      }
    }
    Properties unit = new Properties(base);
    unit.load(new StringReader(block.toString()));
    return unit;
  }

  @Test
  public void testUnitThatCopiesTheDocumentedOptOutBlockSendsPlainEmailsWithSupportAndNoticeOnce() throws Exception {
    Properties base = load(null, "/global.properties");
    Properties unit = optOutUnit(base);
    TextProvider texts = texts(unit);
    List<String> problems = new ArrayList<>();

    for (String admins : new String[] {"", "Jane Admin"}) {
      // A caller sends the admins sentence or the no-admins one, never both.
      String body = texts.getText("email.role.dear", new String[] {"Jane"})
        + texts.getText(admins.isEmpty() ? "email.role.support.noCrpAdmins" : "email.role.support",
          new String[] {admins})
        + texts.getText("email.role.getStarted", new String[] {})
        + texts.getText("email.role.bye", new String[] {});
      assertEquals("the opted-out unit sends the body as it is", body, EmailLayout.wrap(texts, ASSETS_URL, body));
      checkFooterOnce("opt-out role (admins='" + admins + "')", body, problems);
    }
    String otp = texts.getText("login.otp.email.html",
      new String[] {"MARLO", "123456", "10", base.getProperty("footer.contactEmail")});
    assertEquals(otp, EmailLayout.wrap(texts, ASSETS_URL, otp));
    // The pre-shell OTP copy never carried the do-not-reply notice: only its support address is checked.
    assertEquals(1, count(visibleText(otp).toLowerCase(), "marlosupport@cgiar.org"));
    assertEquals("opt-out emails that do not show the footer exactly once: " + problems, 0, problems.size());
  }

  @Test
  public void testAiccraEmailCopyKeepsItsApostrophes() throws IOException {
    Properties base = load(null, "/global.properties");
    for (String customFile : new String[] {"aicrra.properties", "aiccra3.properties"}) {
      Properties custom = load(base, "/custom/" + customFile);
      for (String key : custom.stringPropertyNames()) {
        String pattern = custom.getProperty(key);
        if (!key.startsWith("email.") || !pattern.contains("'")) {
          continue;
        }
        String text = MessageFormat.format(pattern, "{0}", "{1}", "{2}", "{3}", "{4}");
        // A single quote is dropped by MessageFormat, and a doubled one is the only way to deliver one.
        assertFalse(customFile + " " + key + " delivers a doubled apostrophe", text.contains("''"));
        assertTrue(customFile + " " + key + " loses its apostrophes", text.contains("'"));
      }
    }
  }

  @Test
  public void testUndefinedLayoutKeepsTheBody() {
    assertEquals(BODY, EmailLayout.wrap(texts(new Properties()), ASSETS_URL, BODY));
  }

  @Test
  public void testAiccraShellHoldsTheBodyAndTheImages() throws IOException {
    for (String customFile : new String[] {"aicrra.properties", "aiccra3.properties", "test.properties"}) {
      String email = wrapWith(customFile);

      assertTrue(customFile + " must keep the body untouched", email.contains(BODY));
      assertTrue(customFile, email.contains("src=\"" + ASSETS_URL + "/global/images/email/marlo-logo.png\""));
      assertTrue(customFile, email.contains("src=\"" + ASSETS_URL + "/global/images/email/email-band.png\""));
      assertTrue(customFile, email.contains("mailto:MARLOSupport@cgiar.org"));
      // A placeholder the shell did not fill would be delivered literally.
      String shell = email.replace(BODY, "");
      assertFalse(customFile + " left a placeholder unfilled", shell.contains("{") || shell.contains("}"));
    }
  }
}
