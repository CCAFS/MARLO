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

import org.cgiar.ccafs.marlo.data.model.CrpProgram;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Order of the Cluster Description component checklist (A2-2580): PDO first, then the numbered components in
 * numeric order, then the rest by their displayed label.
 */
public class ProjectDescriptionActionComponentOrderTest {

  private CrpProgram program(String acronym, String name) {
    CrpProgram program = new CrpProgram();
    program.setAcronym(acronym);
    program.setName(name);
    return program;
  }

  private List<String> sortedLabels(CrpProgram... programs) {
    List<CrpProgram> list = new ArrayList<>(Arrays.asList(programs));
    list.sort(ProjectDescriptionAction.COMPONENT_ORDER);
    return list.stream().map(CrpProgram::getComposedName).collect(Collectors.toList());
  }

  @Test
  public void aiccraThreeComponentsStartWithThePdoAndFollowTheirNumber() {
    // The AICCRA_III (global unit 47) components, in the order the database returns them
    List<String> labels = this.sortedLabels(this.program("Component 4", "Boosting skills for innovation and jobs"),
      this.program("CEBS", "Citizen engagement - Beneficiaries' satisfaction"),
      this.program("Component 1", "Enhancing data systems and decision-making tools"),
      this.program("PDO", "Project Development Objective Indicators"),
      this.program("Component 3", "Scaling-up innovation uptake"),
      this.program("Component 2", "Strengthening regional scaling capacity"));

    assertEquals(Arrays.asList("PDO: Project Development Objective Indicators",
      "Component 1: Enhancing data systems and decision-making tools",
      "Component 2: Strengthening regional scaling capacity", "Component 3: Scaling-up innovation uptake",
      "Component 4: Boosting skills for innovation and jobs",
      "CEBS: Citizen engagement - Beneficiaries' satisfaction"), labels);
  }

  @Test
  public void aiccraComponentsUseTheNumberInTheirName() {
    // The AICCRA (global unit 45) components carry their number in the name, not in the acronym
    List<String> labels = this.sortedLabels(
      this.program("CSA", "3. Validating Climate-Smart Agriculture Innovations through Piloting"),
      this.program("KS", "1. Knowledge Generation and Sharing"),
      this.program("PD", "2. Strengthening Partnerships for Delivery of Climate-Smart Innovations in Agriculture"),
      this.program("PDO", "0. Project Development Objective Indicators"));

    assertEquals(Arrays.asList("PDO: 0. Project Development Objective Indicators",
      "KS: 1. Knowledge Generation and Sharing",
      "PD: 2. Strengthening Partnerships for Delivery of Climate-Smart Innovations in Agriculture",
      "CSA: 3. Validating Climate-Smart Agriculture Innovations through Piloting"), labels);
  }

  @Test
  public void numbersAreComparedAsNumbersNotAsText() {
    List<String> labels = this.sortedLabels(this.program("Component 10", "Tenth"),
      this.program("Component 9", "Ninth"), this.program("Component 2", "Second"));

    assertEquals(Arrays.asList("Component 2: Second", "Component 9: Ninth", "Component 10: Tenth"), labels);
  }

  @Test
  public void pdoIsRecognizedIgnoringCaseAndSurroundingSpaces() {
    List<String> labels =
      this.sortedLabels(this.program("Component 1", "First"), this.program(" pdo ", "Development Objective"));

    assertEquals(" pdo : Development Objective", labels.get(0));
  }

  @Test
  public void componentsWithoutNumberGoLastInAlphabeticalOrderIgnoringCase() {
    List<String> labels = this.sortedLabels(this.program("zeta", "Last"), this.program("Alpha", "First"),
      this.program("Component 1", "Numbered"));

    assertEquals(Arrays.asList("Component 1: Numbered", "Alpha: First", "zeta: Last"), labels);
  }

  @Test
  public void missingAcronymOrNameDoesNotThrow() {
    // getComposedName() concatenates, so a missing acronym or name reads as "null" instead of failing
    List<String> labels =
      this.sortedLabels(this.program(null, "No acronym"), this.program("PDO", null), this.program(null, null));

    assertEquals("PDO: null", labels.get(0));
    assertEquals(3, labels.size());
  }

  @Test
  public void numberTooLargeForAnIntIsTreatedAsNoNumber() {
    CrpProgram huge = this.program("Component 99999999999999999999", "Overflow");

    assertEquals(Integer.MAX_VALUE, ProjectDescriptionAction.getComponentNumber(huge));
    assertEquals(2, ProjectDescriptionAction.getComponentSortGroup(huge));
  }

  @Test
  public void pdoNumberedAndUnnumberedComponentsFallInTheirSortGroups() {
    assertEquals(0, ProjectDescriptionAction.getComponentSortGroup(this.program("PDO", "Development Objective")));
    assertEquals(1, ProjectDescriptionAction.getComponentSortGroup(this.program("Component 3", "Numbered")));
    assertEquals(2, ProjectDescriptionAction.getComponentSortGroup(this.program("CEBS", "Not numbered")));
  }
}
