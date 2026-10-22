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
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * CHG-PWD-SWITCH-001-DA-001 (added by the /akili-test Suite 1 tester): the constant of the password switch is declared
 * in <b>both</b> {@code APConstants.java} files, and each value equals the {@code parameters.key} the migration seeds.
 * <p>
 * The two classes share one fully qualified name, so only the marlo-web copy is visible at run time: a reflective read
 * cannot see a marlo-data copy that drifted. Both sources are read instead. The expected key is the literal of the
 * requirement, not a value taken from either file.
 */
public class PasswordSwitchConstantParityTest {

  private static final String KEY = "system_password_login_active";
  private static final String[] AP_CONSTANTS = {"src/main/java/org/cgiar/ccafs/marlo/config/APConstants.java",
    "../marlo-data/src/main/java/org/cgiar/ccafs/marlo/config/APConstants.java"};
  private static final Pattern DECLARATION =
    Pattern.compile("public static final String PASSWORD_LOGIN_ACTIVE\\s*=\\s*\"([^\"]*)\"\\s*;");
  private static final Pattern SEEDED_KEY = Pattern.compile("SELECT gut\\.id,\\s*'([^']*)'");

  @Test
  public void bothApConstantsDeclareTheSwitchWithTheValueOfItsKey() throws IOException {
    for (String file : AP_CONSTANTS) {
      String source = new String(Files.readAllBytes(Paths.get(file)), StandardCharsets.UTF_8);
      Matcher matcher = DECLARATION.matcher(source);
      List<String> values = new ArrayList<String>();
      while (matcher.find()) {
        values.add(matcher.group(1));
      }
      assertEquals(file + " must declare PASSWORD_LOGIN_ACTIVE exactly once, equal to the key", 1, values.size());
      assertEquals(file, KEY, values.get(0));
    }
    assertEquals("the constant the running code reads", KEY, APConstants.PASSWORD_LOGIN_ACTIVE);
  }

  @Test
  public void theMigrationSeedsExactlyTheKeyTheConstantsName() throws IOException {
    Path migrations = Paths.get("src/main/resources/database/migrations");
    List<Path> seeds;
    try (Stream<Path> files = Files.list(migrations)) {
      seeds = files.filter(path -> path.getFileName().toString().endsWith("__AddPasswordLoginSwitch.sql"))
        .collect(Collectors.toList());
    }
    assertEquals("one migration seeds the switch", 1, seeds.size());
    String sql = new String(Files.readAllBytes(seeds.get(0)), StandardCharsets.UTF_8);
    Matcher matcher = SEEDED_KEY.matcher(sql);
    assertTrue("the migration seeds a key", matcher.find());
    assertEquals("the seeded parameters.key", KEY, matcher.group(1));
  }
}
