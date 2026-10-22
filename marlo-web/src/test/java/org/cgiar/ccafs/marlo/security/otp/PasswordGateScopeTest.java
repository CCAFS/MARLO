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

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * CHG-PWD-SWITCH-001-T05, SEC-001 and SEC-006: the scope of the password switch, locked on the source text.
 * <p>
 * The switch governs the two password endpoints of the login page and nothing else. Two facts keep that true, and
 * neither can be seen from a behavioural test of the endpoints:
 * <ol>
 * <li><b>Every site that checks a typed password is known.</b> Premise P-2 of the design is an inventory: the two
 * login-page endpoints, the CLARISA technical account, and the realm behind {@code userManager.login}. A new caller
 * of the credential check would be a third door that the gate does not guard, so it fails here until someone decides
 * whether the switch must govern it.</li>
 * <li><b>The files SEC-006 and SEC-007 promise not to touch do not mention the gate.</b> The CLARISA technical login,
 * the {@code /api/**} HTTP Basic realm configuration and the session-switch interceptor keep working with the switch
 * off, so none of them may read the switch or call the gate.</li>
 * </ol>
 * Comments are stripped before the text is searched: a comment may name the gate, code may not.
 */
public class PasswordGateScopeTest {

  private static final String[] SOURCE_ROOTS =
    {"src/main/java", "../marlo-data/src/main/java", "../marlo-utils/src/main/java"};

  /** The files that check a password typed by a person or a client, by name, with the reason each is known. */
  private static final Set<String> CREDENTIAL_CHECK_SITES = new TreeSet<String>(Arrays.asList(
    // The two login-page endpoints: the password gate guards both (SEC-001).
    "LoginAction.java", "ValidateUserAction.java",
    // The CLARISA technical account: outside the login page, out of the switch (SEC-006).
    "ClarisaPublicAccesFilter.java"));

  /**
   * The files that build a password token: the realm's entry point, the CLARISA technical account and the gate itself,
   * which builds one only to hand it to Shiro's remember-me failure handling and checks nothing with it (asserted
   * below: the gate never calls {@code login}).
   */
  private static final Set<String> TOKEN_BUILDERS = new TreeSet<String>(
    Arrays.asList("UserManagerImp.java", "ClarisaPublicAccesFilter.java", "OtpEligibility.java"));

  /** The files whose behaviour SEC-006 and SEC-007 keep unchanged: the switch must not appear in them. */
  private static final List<String> UNTOUCHED = Arrays.asList(
    "src/main/java/org/cgiar/ccafs/marlo/web/filter/ClarisaPublicAccesFilter.java",
    "../marlo-data/src/main/java/org/cgiar/ccafs/marlo/MarloShiroConfiguration.java",
    "src/main/java/org/cgiar/ccafs/marlo/interceptor/ValidSessionCrpInterceptor.java");

  private static final List<String> GATE_MARKERS =
    Arrays.asList("OtpEligibility", "passwordGate", "PASSWORD_LOGIN_ACTIVE", "system_password_login_active");

  @Test
  public void everyCallerOfTheCredentialCheckIsAKnownDoorAndTheGateGuardsTheLoginPageOnes() throws IOException {
    Set<String> callers = filesWithCode("userManager.login(");
    assertEquals("a new caller of userManager.login is a password door the gate does not guard (SEC-001): "
      + "decide whether the switch must govern it, then add it here", CREDENTIAL_CHECK_SITES, callers);

    for (String guarded : new String[] {"src/main/java/org/cgiar/ccafs/marlo/action/home/LoginAction.java",
      "src/main/java/org/cgiar/ccafs/marlo/action/json/global/ValidateUserAction.java"}) {
      String code = code(Paths.get(guarded));
      assertTrue(guarded + " must call the gate", code.contains("passwordGate("));
      assertTrue(guarded + ": the gate runs before the credential check",
        code.indexOf("passwordGate(") < code.indexOf("userManager.login("));
    }
  }

  /**
   * The other way to check a typed password is to build the token and log in with it: the realm does it behind
   * {@code userManager.login}, and the CLARISA filter does it for the technical account. A third builder would be a
   * password door that neither the gate nor SEC-006 has accounted for.
   */
  @Test
  public void everyBuilderOfAPasswordTokenIsAKnownDoor() throws IOException {
    assertEquals("a new UsernamePasswordToken is a password door the gate does not guard (SEC-001): decide whether "
      + "the switch must govern it, then add it here", TOKEN_BUILDERS, filesWithCode("new UsernamePasswordToken("));

    String gate = code(Paths.get("src/main/java/org/cgiar/ccafs/marlo/security/otp/OtpEligibility.java"));
    assertFalse("the gate builds a token for the remember-me failure handling and must never log in with it",
      gate.contains(".login("));
  }

  @Test
  public void theFilesTheSwitchMustNotGovernDoNotMentionIt() throws IOException {
    for (String untouched : UNTOUCHED) {
      String code = code(Paths.get(untouched));
      for (String marker : GATE_MARKERS) {
        assertFalse(untouched + " must not mention " + marker + " (SEC-006, SEC-007)", code.contains(marker));
      }
    }
  }

  /** The names of the source files, in the three modules, whose code (comments stripped) contains the text. */
  private static Set<String> filesWithCode(String text) throws IOException {
    Set<String> names = new TreeSet<String>();
    for (String root : SOURCE_ROOTS) {
      try (Stream<Path> files = Files.walk(Paths.get(root))) {
        for (Path file : files.filter(path -> path.toString().endsWith(".java")).collect(Collectors.toList())) {
          if (code(file).contains(text)) {
            names.add(file.getFileName().toString());
          }
        }
      }
    }
    return names;
  }

  private static String code(Path file) throws IOException {
    String source = new String(Files.readAllBytes(file), StandardCharsets.UTF_8);
    return source.replaceAll("(?s)/\\*.*?\\*/", "").replaceAll("//[^\n]*", "");
  }
}
