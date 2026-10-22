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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * CHG-PWD-SWITCH-001 AC-13, FN-008, NF-001 (added by the /akili-test Suite 1 tester): every one of the 7 welcome-email
 * senders builds its text through {@link WelcomeSignInText}, the one rule, and no other code builds
 * {@code email.newUser.part1} on its own. Requirements section 6 names this gate ("one grep per sender proving all 7
 * call it"); {@code CrpUsersActionWelcomeMailTest} proves the call behaviourally for one sender, this test keeps the
 * other six from drifting back to the old text.
 * <p>
 * Comments are stripped before the text is searched: a comment may name the key, code may not.
 */
public class WelcomeSendersUseTheSignInHelperTest {

  private static final String[] SOURCE_ROOTS =
    {"src/main/java", "../marlo-data/src/main/java", "../marlo-utils/src/main/java"};

  /** The 7 senders of design premise P-9. */
  private static final Set<String> SENDERS = new TreeSet<String>(Arrays.asList("CrpUsersAction.java",
    "CrpProgamRegionsAction.java", "CrpPpaPartnersAction.java", "CrpSiteIntegrationAction.java",
    "CrpAdminManagmentAction.java", "ProjectPartnerAction.java", "ClusterActivitiesAction.java"));

  @Test
  public void everySenderBuildsTheWelcomeTextThroughTheHelperAndNoOneElseDoes() throws IOException {
    assertEquals("the senders that call the helper are exactly the 7 of P-9", SENDERS,
      filesWithCode("this.welcomeSignInText.part1(this, user, loggedCrp, password,"));
  }

  @Test
  public void onlyTheHelperAndTheCallerlessLoaderNameTheOldKey() throws IOException {
    // SendEmails is the one-off loader of premise P-10. It still names the key, so it must stay without a caller: a
    // caller would be an 8th sender that mails a password the unit may not accept.
    assertEquals("a new builder of email.newUser.part1 bypasses the rule (FN-008, NF-001)",
      new TreeSet<String>(Arrays.asList("SendEmails.java", "WelcomeSignInText.java")),
      filesWithCode("\"email.newUser.part1\""));
    assertEquals("SendEmails has no caller (P-10)", new TreeSet<String>(Arrays.asList("SendEmails.java")),
      filesWithCode("SendEmails"));
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
