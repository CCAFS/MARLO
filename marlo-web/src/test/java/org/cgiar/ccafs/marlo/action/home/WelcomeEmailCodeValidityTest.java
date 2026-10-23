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

package org.cgiar.ccafs.marlo.action.home;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * CHG-PWD-SWITCH-001-T07 (UI-003): the welcome email for an account that signs in with a code says how long the code
 * is valid. The number is copy, not a placeholder (DD-7 keeps its five arguments), so this test is what stops it from
 * drifting from the real expiry, as {@code OtpRequestCodeActionTest} does for the code email itself.
 */
public class WelcomeEmailCodeValidityTest {

  private static final String KEY = "email.newUser.part1.codeSignIn";
  private static final Pattern VALID_FOR = Pattern.compile("valid for (\\d+) minutes");
  private static final String[] FILES =
    {"/global.properties", "/custom/aicrra.properties", "/custom/aiccra3.properties", "/custom/test.properties"};

  @Test
  public void everyWelcomeCopyStatesTheRealExpiryInMinutes() throws Exception {
    long real = OtpRequestCodeAction.CODE_VALIDITY.toMinutes();
    for (String file : FILES) {
      Properties properties = new Properties();
      try (InputStream in = WelcomeEmailCodeValidityTest.class.getResourceAsStream(file);
        Reader reader = new InputStreamReader(in, StandardCharsets.UTF_8)) {
        properties.load(reader);
      }
      String copy = properties.getProperty(KEY);
      assertTrue(file + " must define " + KEY, copy != null);
      Matcher matcher = VALID_FOR.matcher(copy);
      assertTrue(file + " must say how many minutes the code is valid", matcher.find());
      assertEquals(file + ": the welcome copy and the real expiry cannot drift", real,
        Long.parseLong(matcher.group(1)));
    }
  }
}
