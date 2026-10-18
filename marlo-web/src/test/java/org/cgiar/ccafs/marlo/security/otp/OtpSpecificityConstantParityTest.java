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

import org.cgiar.ccafs.marlo.config.APConstants;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * CHG-OTP-LOGIN-001-DA-002 (added by the /akili-test Suite 1 tester): the two code sign-in specificities.
 * <ul>
 * <li>their constants are declared in <b>both</b> {@code APConstants.java} files, each equal to its
 * {@code parameters.key};</li>
 * <li>the T21 migration seeds both keys off and empty for the Global Unit types 1 to 4 and removes the allow-list
 * {@code crp_otp_allowed_email_domains}, its {@code custom_parameters} first;</li>
 * <li>the description the migrations leave stored for each key is exactly the text of DA-002 (as amended on
 * 2026-10-17 by CHG-PWD-SWITCH-001 DA-003).</li>
 * </ul>
 * The two {@code APConstants} classes share one fully qualified name, so only the marlo-web copy is visible at run
 * time; both sources are read instead. Every expected value is a literal of the requirement, never a value taken from
 * the files under test. Applying the migrations to a database is AC-17 / AC-21, live.
 */
public class OtpSpecificityConstantParityTest {

  private static final String SWITCH_KEY = "system_otp_login_active";
  private static final String EXCLUSION_KEY = "system_otp_excluded_email_domains";
  private static final String[] AP_CONSTANTS = {"src/main/java/org/cgiar/ccafs/marlo/config/APConstants.java",
    "../marlo-data/src/main/java/org/cgiar/ccafs/marlo/config/APConstants.java"};
  private static final Path MIGRATIONS = Paths.get("src/main/resources/database/migrations");
  private static final String SQL_TEXT = "'((?:[^']|'')*)'";

  private static final String SWITCH_DESCRIPTION = "Lets users of this Global Unit sign in on the login page with a "
    + "one-time code sent to their email, as an alternative to their password or to Sign in with CGIAR. Only "
    + "registered users who are active and members of this Global Unit receive a code. Turning it off removes the "
    + "option on the next login, and users whose email domain is listed in system_otp_excluded_email_domains do not "
    + "receive a code. Exception: while system_password_login_active is off and the server has a code key, everyone "
    + "who does not use Sign in with CGIAR signs in with the code, whatever this setting and the exclusion say.";
  private static final String EXCLUSION_DESCRIPTION = "Email domains that cannot sign in with a one-time code in "
    + "this Global Unit, even when system_otp_login_active is on. Write exact domains separated by commas, for "
    + "example cgiar.org,example.org. A subdomain is only excluded if it is listed itself. Leave it empty to exclude "
    + "no one. Exception: while system_password_login_active is off, the exclusion applies only to users of Sign in "
    + "with CGIAR.";

  private static String read(Path file) throws IOException {
    return new String(Files.readAllBytes(file), StandardCharsets.UTF_8);
  }

  private static String sqlUnquote(String text) {
    return text.replace("''", "'");
  }

  private static List<Path> sortedMigrations() throws IOException {
    try (Stream<Path> files = Files.list(MIGRATIONS)) {
      return files.filter(path -> path.getFileName().toString().endsWith(".sql")).sorted()
        .collect(Collectors.toList());
    }
  }

  @Test
  public void bothApConstantsDeclareBothCodeSignInKeysWithTheValueOfTheirParametersKey() throws IOException {
    Map<String, String> expected = new HashMap<String, String>();
    expected.put("OTP_LOGIN_ACTIVE", SWITCH_KEY);
    expected.put("OTP_EXCLUDED_EMAIL_DOMAINS", EXCLUSION_KEY);
    for (String file : AP_CONSTANTS) {
      String source = read(Paths.get(file));
      for (Map.Entry<String, String> constant : expected.entrySet()) {
        Matcher matcher = Pattern
          .compile("public static final String " + constant.getKey() + "\\s*=\\s*\"([^\"]*)\"\\s*;").matcher(source);
        List<String> values = new ArrayList<String>();
        while (matcher.find()) {
          values.add(matcher.group(1));
        }
        assertEquals(file + " must declare " + constant.getKey() + " exactly once", 1, values.size());
        assertEquals(file + " " + constant.getKey(), constant.getValue(), values.get(0));
      }
    }
    assertEquals("the switch constant the running code reads", SWITCH_KEY, APConstants.OTP_LOGIN_ACTIVE);
    assertEquals("the exclusion constant the running code reads", EXCLUSION_KEY,
      APConstants.OTP_EXCLUDED_EMAIL_DOMAINS);
  }

  @Test
  public void theT21MigrationSeedsBothKeysOffForTypesOneToFourAndRemovesTheAllowListInForeignKeyOrder()
    throws IOException {
    List<Path> t21 = sortedMigrations().stream()
      .filter(path -> path.getFileName().toString().endsWith("__ReplaceOtpAllowListWithSwitchAndExclusion.sql"))
      .collect(Collectors.toList());
    assertEquals("one T21 migration", 1, t21.size());
    String sql = read(t21.get(0));

    Map<String, String> defaults = new HashMap<String, String>();
    defaults.put(SWITCH_KEY, "false");
    defaults.put(EXCLUSION_KEY, "");
    for (Map.Entry<String, String> seed : defaults.entrySet()) {
      Matcher matcher = Pattern.compile("SELECT gut\\.id,\\s*'" + seed.getKey() + "',\\s*" + SQL_TEXT
        + ",\\s*\\d+,\\s*" + SQL_TEXT + ",\\s*\\d+\\s*FROM global_unit_types gut\\s*WHERE gut\\.id IN \\(1, 2, 3, 4\\)")
        .matcher(sql);
      assertTrue(seed.getKey() + " is seeded for the Global Unit types 1, 2, 3 and 4", matcher.find());
      assertEquals(seed.getKey() + " is seeded off / empty", seed.getValue(), sqlUnquote(matcher.group(2)));
      assertTrue(seed.getKey() + " is seeded once", !matcher.find());
    }

    int customParameters = sql.indexOf("FROM custom_parameters custom_parameter");
    int parameters = sql.indexOf("DELETE FROM parameters WHERE `key` = 'crp_otp_allowed_email_domains';");
    assertTrue("the allow-list values are removed", customParameters >= 0
      && sql.indexOf("old_parameter.`key` = 'crp_otp_allowed_email_domains'", customParameters) > customParameters);
    assertTrue("the allow-list parameters rows are removed, after their custom_parameters",
      parameters > customParameters);
  }

  @Test
  public void theDescriptionLeftStoredForEachKeyIsExactlyTheTextOfTheRequirement() throws IOException {
    Map<String, String> stored = new HashMap<String, String>();
    for (Path migration : sortedMigrations()) {
      String sql = read(migration);
      for (String key : new String[] {SWITCH_KEY, EXCLUSION_KEY}) {
        Matcher seed = Pattern.compile("SELECT gut\\.id,\\s*'" + key + "',\\s*" + SQL_TEXT).matcher(sql);
        while (seed.find()) {
          stored.put(key, sqlUnquote(seed.group(1)));
        }
        Matcher update =
          Pattern.compile("SET `description` = " + SQL_TEXT + "\\s*WHERE `key` = '" + key + "'\\s*;").matcher(sql);
        while (update.find()) {
          stored.put(key, sqlUnquote(update.group(1)));
        }
      }
    }
    assertEquals(SWITCH_KEY, SWITCH_DESCRIPTION, stored.get(SWITCH_KEY));
    assertEquals(EXCLUSION_KEY, EXCLUSION_DESCRIPTION, stored.get(EXCLUSION_KEY));
  }
}
