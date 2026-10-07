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
import java.lang.reflect.Proxy;
import java.text.MessageFormat;
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
  public void testBaseLayoutSendsTheBodyAsItIs() throws IOException {
    Properties base = load(null, "/global.properties");

    assertEquals(BODY, EmailLayout.wrap(texts(base), ASSETS_URL, BODY));
  }

  @Test
  public void testBaseRoleBlocksMatchTheSharedOnes() throws IOException {
    // The role notifications read their own copy of the shared blocks, so AICCRA can drop the support sentence and
    // the platform link from them without changing every other notification. The base copy must stay identical.
    Properties base = load(null, "/global.properties");

    for (String block : new String[] {"dear", "bye", "support", "support.noCrpAdmins", "getStarted"}) {
      assertEquals(block, base.getProperty("email." + block), base.getProperty("email.role." + block));
    }
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
