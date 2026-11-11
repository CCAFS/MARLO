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

package org.cgiar.ccafs.marlo.action.json.project;

import org.cgiar.ccafs.marlo.action.json.project.UnsubmitProjectAction.UnsubmitEmail;
import org.cgiar.ccafs.marlo.utils.UnitTextProvider;

import com.opensymphony.xwork2.TextProvider;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * The unsubmit email as the action assembles it (CHG-EMAIL-LAYOUT-001 FN-006, FN-008, FN-011). The texts are the real
 * i18n files read through {@link UnitTextProvider}, so a key that resolves to another copy shows here.
 */
public class UnsubmitProjectEmailTest {

  private static final String CDN = "https://cdn.test";
  private static final String SHELL_LOGO = CDN + "/global/images/email/marlo-logo.png";
  private static final String REASON = "Please fix the budget table";

  private static UnsubmitEmail assemble(String unit) {
    TextProvider texts = UnitTextProvider.forCustomFile(unit);
    String[] values = {"Ana", "Luis", "AICCRA", "Soil data", "P77", "2026", "planning", REASON};
    return UnsubmitProjectAction.assembleEmail(texts, CDN, "AICCRA", "P77", values);
  }

  private static int count(String text, String part) {
    int found = 0;
    for (int at = text.indexOf(part); at >= 0; at = text.indexOf(part, at + part.length())) {
      found++;
    }
    return found;
  }

  private static void assertShellOnce(String label, String body) {
    assertTrue(label + " is not inside the shell", body.contains(SHELL_LOGO));
    assertEquals(label + " support link", 1, count(body, "mailto:MARLOSupport@cgiar.org"));
    assertEquals(label + " do-not-reply notice", 1, count(body, "automated notification"));
    assertEquals(label + " sign-off", 1, count(body, "Kind regards"));
    assertEquals(label + " legacy sign-off", 0, count(body, "Best regards"));
    assertEquals(label + " legacy get-started link", 0, count(body, "To begin, please visit"));
  }

  @Test
  public void testAiccraUsesClusterWordsAndQuotesTheJustification() {
    UnsubmitEmail email = assemble("aicrra");
    assertTrue(email.body, email.body.contains("Luis has returned the AICCRA cluster \"Soil data\" (P77) to you"));
    assertTrue(email.body, email.body.contains("<blockquote"));
    assertTrue(email.body, email.body.contains("<em>" + REASON + "</em></blockquote>"));
    assertTrue(email.body, email.body.contains("submit the cluster again"));
    assertFalse(email.body, email.body.contains("Technical Implementation Team"));
    assertFalse(email.body, email.body.contains("sign in to MARLO"));
    assertShellOnce("aicrra", email.body);
  }

  @Test
  public void testAiccraIiiUsesItsOwnUnitWords() {
    UnsubmitEmail email = assemble("aiccra3");
    assertTrue(email.body, email.body.contains("Technical Implementation Team \"Soil data\""));
    assertFalse(email.body, email.body.contains("cluster") || email.body.contains("PMC"));
    assertFalse(email.subject, email.subject.contains("cluster"));
    assertShellOnce("aiccra3", email.body);
  }

  @Test
  public void testUnitWithoutCustomFileGetsTheBaseCopyInsideTheShell() {
    UnsubmitEmail email = assemble("noSuchFile");
    assertEquals("[MARLO] AICCRA project P77 has been returned to the project leader for edits", email.subject);
    assertTrue(email.body, email.body.contains("returned the AICCRA project \"Soil data\" (P77)"));
    assertEquals("base get-started line", 1, count(email.body, "Please sign in to MARLO to get started."));
    assertShellOnce("base", email.body);
  }

  @Test
  public void testFooterAppearsOnceInEveryUnit() {
    for (String unit : new String[] {"aicrra", "aiccra3", "noSuchFile", "pabra"}) {
      assertShellOnce(unit, assemble(unit).body);
    }
  }

  @Test
  public void testSubjectComesFromTheSameUnitAsTheBody() {
    assertEquals("[AICCRA] AICCRA cluster [P77] has been returned to the cluster leader for edits",
      assemble("aicrra").subject);
    assertEquals("[AICCRA] AICCRA Technical Implementation Team [P77] has been returned to the Technical "
      + "Implementation Team leader for edits", assemble("aiccra3").subject);
  }
}
