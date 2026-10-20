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

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.Properties;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * CHG-PWD-SWITCH-001 FN-008, UI-003 (added by the /akili-test Suite 1 tester): the code sign-in welcome copy, rendered
 * the way {@code getText} renders it (every value goes through {@link MessageFormat}), carries every argument today's
 * copy of the same file carries, names the unit where today's copy prints the password, and has no password label.
 * The AICCRA copies never printed the support text {@code {4}}, so the argument set is read from today's copy rather
 * than fixed here.
 * <p>
 * {@code WelcomeSignInTextTest} proves which key and which arguments the helper hands over; it stubs {@code getText},
 * so it cannot see the copy itself. A single apostrophe in a copy makes {@link MessageFormat} drop it and stop
 * substituting the arguments after it; a leftover {@code Password: {3}} would print the unit as a password. Both are
 * caught here, in each of the four files UI-003 names.
 */
public class WelcomeCodeSignInCopyTest {

  private static final String[] FILES =
    {"/global.properties", "/custom/aicrra.properties", "/custom/aiccra3.properties", "/custom/test.properties"};
  private static final String[] ARGS =
    {"ROLES-ARG-0", "https://marlo.example/ARG-1", "member@arg-two.org", "UNITARG3", "SUPPORT-ARG-4"};
  private static final String PASSWORD_LABEL = "<b>Password</b>";

  private static Properties load(String file) throws Exception {
    Properties properties = new Properties();
    try (InputStream in = WelcomeCodeSignInCopyTest.class.getResourceAsStream(file);
      Reader reader = new InputStreamReader(in, StandardCharsets.UTF_8)) {
      properties.load(reader);
    }
    return properties;
  }

  @Test
  public void theRenderedCodeCopyCarriesEveryArgumentAndNoPasswordLabel() throws Exception {
    for (String file : FILES) {
      Properties properties = load(file);
      String copy = properties.getProperty("email.newUser.part1.codeSignIn");
      String today = properties.getProperty("email.newUser.part1");
      assertNotNull(file + " must define the code copy", copy);
      assertNotNull(file + " keeps today's copy", today);

      String rendered = new MessageFormat(copy).format(ARGS);

      for (int i = 0; i < ARGS.length; i++) {
        if (today.contains("{" + i + "}")) {
          assertTrue(file + ": argument {" + i + "} of today's copy must be rendered, so no quote swallowed it: "
            + rendered, rendered.contains(ARGS[i]));
        }
      }
      assertTrue(file + ": the email and the unit are always printed",
        rendered.contains(ARGS[2]) && rendered.contains(ARGS[3]));
      assertFalse(file + ": no format element is left unrendered", rendered.contains("{"));
      assertFalse(file + ": the code copy has no password label (FN-008)", rendered.contains(PASSWORD_LABEL));
    }
  }

  /** The fixture discriminates: today's copy in the same files does print argument {3} after the password label. */
  @Test
  public void todaysCopyInTheSameFilesPrintsTheFourthArgumentAsThePassword() throws Exception {
    for (String file : FILES) {
      String today = load(file).getProperty("email.newUser.part1");
      assertNotNull(file + " keeps today's copy", today);

      String rendered = new MessageFormat(today).format(ARGS);

      assertTrue(file + ": " + rendered, rendered.contains(PASSWORD_LABEL));
      assertTrue(file + ": the fourth argument follows the label",
        rendered.indexOf(ARGS[3]) > rendered.indexOf(PASSWORD_LABEL));
    }
  }
}
