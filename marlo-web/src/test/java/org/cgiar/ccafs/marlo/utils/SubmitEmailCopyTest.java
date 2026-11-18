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

import java.text.MessageFormat;

import com.opensymphony.xwork2.TextProvider;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * The submit and unsubmit emails (requirements Appendix A). Every key is read through the real i18n files and
 * formatted with MessageFormat, as Struts does, so a misplaced apostrophe or brace shows here, not in a user's inbox.
 */
public class SubmitEmailCopyTest {

  private static final String P = "<p style=\"margin:0 0 16px 0;\">";
  private static final String QUOTE = "<blockquote style=\"margin:0 0 16px 0;padding:0 0 0 16px;"
    + "border-left:3px solid #cccccc;\"><em>{7}</em></blockquote>";
  private static final String[] SENTINELS = {"@0@", "@1@", "@2@", "@3@", "@4@", "@5@", "https://s6", "@7@"};
  private static final String[] KEYS =
    {"submit.email.message", "submit.email.message.noPDF", "unsubmit.email.message"};

  /** The Appendix A text of a key as a pattern; the unit words are the only thing that changes between units. */
  private static String appendix(String key, String noun, String summary, String returner) {
    if (key.equals(KEYS[2])) {
      return P + "Dear {0},</p>" + P + "{1} has returned the {2} " + noun + " \"{3}\" ({4}) to you for changes in "
        + "the {5} {6} cycle.</p>" + P + "Reason given:</p>" + QUOTE + P
        + "Please make the requested changes and submit the " + noun + " again.</p>";
    }
    String link = key.equals(KEYS[0]) ? "The " + summary + " is attached as a PDF. You can also <a href=\"{6}\">open it"
      + " in MARLO</a> or generate it at any time from the Summaries section."
      : "You can <a href=\"{6}\">download the " + summary + " as a PDF</a> or generate it at any time from the "
        + "Summaries section.";
    return P + "Dear all,</p>" + P + "{0} has submitted the {1} " + noun + " \"{2}\" ({3}) for review in the {4} {5}"
      + " cycle.</p>" + P + "The " + noun + " cannot be edited again in this cycle unless " + returner
      + " for changes.</p>" + P + link + "</p>";
  }

  private static String format(String unit, String key) {
    TextProvider texts = UnitTextProvider.forCustomFile(unit);
    return texts.getText(key, SENTINELS);
  }

  private static void checkUnit(String unit, String noun, String summary, String returner) {
    for (String key : KEYS) {
      String label = (unit.isEmpty() ? "global" : unit) + " " + key;
      String email = format(unit, key);
      int arguments = key.equals(KEYS[2]) ? 8 : 7;
      for (int i = 0; i < arguments; i++) {
        assertTrue(label + " lacks " + SENTINELS[i], email.contains(SENTINELS[i]));
      }
      assertTrue(label + " must link {6}", email.contains("href=\"https://s6\"") || key.equals(KEYS[2]));
      assertFalse(label + " keeps a hardcoded project", email.contains("102080") || email.contains("407"));
      assertFalse(label + " still stacks <br><br>", email.contains("<br><br>"));
      assertEquals(label + " differs from Appendix A", MessageFormat.format(appendix(key, noun, summary, returner),
        (Object[]) SENTINELS), email);
      String text = EmailPlainText.fromHtml(email);
      assertFalse(label + " plain text keeps markup: " + text, text.contains("<") || text.contains(">"));
      assertTrue(label + " plain text loses the argument: " + text, text.contains(SENTINELS[arguments - 1]));
    }
  }

  @Test
  public void testAiccraCopyMatchesAppendixA1() {
    checkUnit("aicrra", "cluster", "cluster summary", "a member of the PMC or the cluster leader returns it");
  }

  @Test
  public void testAiccraIiiCopyMatchesAppendixA2() {
    checkUnit("aiccra3", "Technical Implementation Team", "Technical Implementation Team summary",
      "a member of the PIU or the Technical Implementation Team leader returns it");
  }

  @Test
  public void testBaseCopyMatchesAppendixA3AndIsNeutral() {
    checkUnit("", "project", "project summary", "it is returned");
    for (String key : KEYS) {
      assertFalse(key, format("", key).contains("Flagship") || format("", key).contains("PMU"));
    }
  }

  @Test
  public void testPabraSubmitEmailsLinkTheSummaryFromTheArgument() {
    for (String key : new String[] {KEYS[0], KEYS[1]}) {
      String email = format("pabra", key);
      assertTrue(key + " must link {6}", email.contains("href=\"https://s6\""));
      assertFalse(key + " keeps a hardcoded project", email.contains("102080") || email.contains("407"));
      assertTrue(key, email.contains("@5@") && EmailPlainText.fromHtml(email).contains("(https://s6)"));
    }
  }
}
