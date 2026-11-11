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

package org.cgiar.ccafs.marlo.action.projects;

import org.cgiar.ccafs.marlo.action.projects.ProjectSubmissionAction.SubmitEmail;
import org.cgiar.ccafs.marlo.utils.UnitTextProvider;

import com.opensymphony.xwork2.TextProvider;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * The submit email as the action assembles it (CHG-EMAIL-LAYOUT-001 FN-006, FN-007, FN-011). The texts are the real
 * i18n files read through {@link UnitTextProvider}, so a key that resolves to another copy shows here.
 */
public class ProjectSubmissionEmailTest {

  private static final String CDN = "https://cdn.test";
  private static final String LINK = "https://marlo.test/projects/AICCRA/reportingSummary.do?projectID=77&phaseID=5";
  private static final String SHELL_LOGO = CDN + "/global/images/email/marlo-logo.png";

  private static SubmitEmail assemble(String unit, boolean pdfAttached) {
    TextProvider texts = UnitTextProvider.forCustomFile(unit);
    String[] values = {"Ana Perez", "AICCRA", "Soil data", "P77", "2026", "planning", LINK};
    return ProjectSubmissionAction.assembleEmail(texts, CDN, "AICCRA", "P77", values, pdfAttached);
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
    // The shell links the address once (href and visible text); a second support sentence would add another.
    assertEquals(label + " support link", 1, count(body, "mailto:MARLOSupport@cgiar.org"));
    assertEquals(label + " support sentence", 0, count(body, "technical MARLO team"));
    assertEquals(label + " do-not-reply notice", 1, count(body, "automated notification"));
    assertEquals(label + " sign-off", 1, count(body, "Kind regards"));
  }

  @Test
  public void testAiccraWithPdfSendsPlanningCopyWithTheLink() {
    SubmitEmail email = assemble("aicrra", true);
    assertEquals("[AICCRA] AICCRA cluster [P77] has been submitted", email.subject);
    assertTrue(email.body, email.body.contains("has submitted the AICCRA cluster \"Soil data\" (P77) for review in the "
      + "2026 planning cycle."));
    assertTrue(email.body, email.body.contains("a member of the PMC or the cluster leader returns it"));
    assertTrue(email.body, email.body.contains("The cluster summary is attached as a PDF."));
    assertTrue(email.body, email.body.contains("<a href=\"" + LINK + "\">open it in MARLO</a>"));
    assertFalse(email.body, email.body.contains("projectID=102080") || email.body.contains("phaseID=407"));
    assertShellOnce("aicrra with PDF", email.body);
  }

  @Test
  public void testAiccraWithoutPdfSendsNoPdfCopyAndNeverSaysAttached() {
    SubmitEmail email = assemble("aicrra", false);
    assertTrue(email.body, email.body.contains("<a href=\"" + LINK + "\">download the cluster summary as a PDF</a>"));
    assertFalse(email.body, email.body.contains("attached"));
    assertShellOnce("aicrra without PDF", email.body);
  }

  @Test
  public void testAiccraIiiUsesItsOwnUnitWordsInSubjectAndBody() {
    SubmitEmail email = assemble("aiccra3", true);
    assertEquals("[AICCRA] AICCRA Technical Implementation Team [P77] has been submitted", email.subject);
    assertTrue(email.body, email.body.contains("Technical Implementation Team \"Soil data\""));
    assertFalse(email.body, email.body.contains("PMC") || email.body.contains("cluster"));
    assertShellOnce("aiccra3", email.body);
  }

  @Test
  public void testUnitWithoutCustomFileGetsTheBaseCopyInsideTheShell() {
    SubmitEmail email = assemble("noSuchFile", true);
    assertEquals("[MARLO] AICCRA project P77 has been submitted", email.subject);
    assertTrue(email.body, email.body.contains("project \"Soil data\" (P77)"));
    assertFalse(email.body, email.body.contains("cluster") || email.body.contains("Flagship"));
    assertShellOnce("base", email.body);
  }

  @Test
  public void testFooterAppearsOnceInEveryUnit() {
    for (String unit : new String[] {"aicrra", "aiccra3", "noSuchFile", "pabra"}) {
      assertShellOnce(unit, assemble(unit, true).body);
      assertShellOnce(unit + " no PDF", assemble(unit, false).body);
    }
  }
}
