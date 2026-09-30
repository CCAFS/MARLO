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
import org.cgiar.ccafs.marlo.data.model.Institution;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.ProgramType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Order of the components in Project Description: PDO first, then the numbered components in numeric order, then
 * the rest by their displayed label. The same rule orders the Cluster Description checklist (A2-2580) and the
 * component options of the Management Liaison select (A2-2578).
 */
public class ProjectDescriptionActionComponentOrderTest {

  // Cluster Description checklist order (A2-2580): PDO first, then the numbered components in numeric order, then
  // the rest by their displayed label

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
  public void missingAcronymOrNameStillSortsByTheDisplayedLabel() {
    // getComposedName() concatenates, so a missing acronym or name reads as "null" instead of failing
    List<String> labels =
      this.sortedLabels(this.program(null, null), this.program(null, "No acronym"), this.program("PDO", null));

    assertEquals(Arrays.asList("PDO: null", "null: No acronym", "null: null"), labels);
  }

  @Test
  public void numberTooLargeForAnIntSortsWithTheUnnumberedComponents() {
    List<String> labels = this.sortedLabels(this.program("Component 99999999999999999999", "Overflow"),
      this.program("CEBS", "Not numbered"), this.program("Component 1", "Numbered"));

    assertEquals(
      Arrays.asList("Component 1: Numbered", "CEBS: Not numbered", "Component 99999999999999999999: Overflow"),
      labels);
  }

  @Test
  public void pdoAndNumberedComponentsGoFirstWhateverTheirLabel() {
    // Alphabetically "Alpha" would come first and "PDO" last
    List<String> labels = this.sortedLabels(this.program("Alpha", "Not numbered"),
      this.program("PDO", "Development Objective"), this.program("Component 3", "Numbered"));

    assertEquals(Arrays.asList("PDO: Development Objective", "Component 3: Numbered", "Alpha: Not numbered"), labels);
  }

  // Management Liaison select order (A2-2578): the component options follow the same rule as the checklist; the
  // options that are not components tie, so their label keeps deciding their order

  private LiaisonInstitution component(String acronym, String name) {
    return this.liaison(acronym, name, ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue());
  }

  private LiaisonInstitution liaison(String acronym, String name, int programType) {
    CrpProgram program = new CrpProgram();
    program.setProgramType(programType);
    LiaisonInstitution liaison = new LiaisonInstitution();
    liaison.setAcronym(acronym);
    liaison.setName(name);
    liaison.setCrpProgram(program);
    return liaison;
  }

  private List<String> sortedLiaisonLabels(LiaisonInstitution... liaisons) {
    List<LiaisonInstitution> list = new ArrayList<>(Arrays.asList(liaisons));
    // The action breaks ties with the displayed label, reading a null composed name as ""; the component tag is the
    // same for all of them
    list.sort(ProjectDescriptionAction.LIAISON_COMPONENT_ORDER.thenComparing(
      l -> l.getComposedName() == null ? "" : l.getComposedName(), String.CASE_INSENSITIVE_ORDER));
    return list.stream().map(LiaisonInstitution::getComposedName).collect(Collectors.toList());
  }

  @Test
  public void liaisonAiccraComponentsFollowTheNumberInTheirName() {
    // The AICCRA (global unit 45) liaisons carry the number in the name; alphabetically CSA (3) would come first
    List<String> labels = this.sortedLiaisonLabels(
      this.component("CSA", "3. Validating Climate-Smart Agriculture Innovations through Piloting"),
      this.component("KS", "1. Knowledge Generation and Sharing"),
      this.component("PDO", "0. Project Development Objective Indicators"),
      this.component("PD", "2. Strengthening Partnerships for Delivery of Climate-Smart Innovations in Agriculture"));

    assertEquals(Arrays.asList("PDO - 0. Project Development Objective Indicators",
      "KS - 1. Knowledge Generation and Sharing",
      "PD - 2. Strengthening Partnerships for Delivery of Climate-Smart Innovations in Agriculture",
      "CSA - 3. Validating Climate-Smart Agriculture Innovations through Piloting"), labels);
  }

  @Test
  public void liaisonAiccraThreeComponentsStartWithThePdoAndFollowTheirNumber() {
    // The AICCRA_III (global unit 47) liaisons carry the number in the acronym; CEBS and PDO have none
    List<String> labels =
      this.sortedLiaisonLabels(this.component("Component 4", "Boosting skills for innovation and jobs"),
        this.component("CEBS", "Citizen engagement - Beneficiaries' satisfaction"),
        this.component("Component 1", "Enhancing data systems and decision-making tools"),
        this.component("PDO", "Project Development Objective Indicators"),
        this.component("Component 3", "Scaling-up innovation uptake"),
        this.component("Component 2", "Strengthening regional scaling capacity"));

    assertEquals(Arrays.asList("PDO - Project Development Objective Indicators",
      "Component 1 - Enhancing data systems and decision-making tools",
      "Component 2 - Strengthening regional scaling capacity", "Component 3 - Scaling-up innovation uptake",
      "Component 4 - Boosting skills for innovation and jobs",
      "CEBS - Citizen engagement - Beneficiaries' satisfaction"), labels);
  }

  @Test
  public void liaisonNumbersAreComparedAsNumbersNotAsText() {
    List<String> labels = this.sortedLiaisonLabels(this.component("Component 10", "Tenth"),
      this.component("Component 9", "Ninth"), this.component("Component 2", "Second"));

    assertEquals(Arrays.asList("Component 2 - Second", "Component 9 - Ninth", "Component 10 - Tenth"), labels);
  }

  @Test
  public void liaisonWithoutAcronymOrNameDoesNotThrow() {
    List<String> labels =
      this.sortedLiaisonLabels(this.component(null, null), this.component("", "3. Third"), this.component("PDO", ""));

    // An empty acronym leaves the plain name; no acronym and no name leave a null label, sorted with the unnumbered
    assertEquals(Arrays.asList("PDO - ", "3. Third", null), labels);
  }

  @Test
  public void liaisonOptionsThatAreNotComponentsTieWhateverTheNumberInTheirName() {
    LiaisonInstitution regional = this.liaison("WA", "2. Western Africa", ProgramType.REGIONAL_PROGRAM_TYPE.getValue());
    LiaisonInstitution partner = this.component("CIAT", "1. Partner with a number");
    partner.setInstitution(new Institution());
    LiaisonInstitution noProgram = new LiaisonInstitution();
    noProgram.setName("9. No program");

    assertEquals(0, ProjectDescriptionAction.LIAISON_COMPONENT_ORDER.compare(regional, partner));
    assertEquals(0, ProjectDescriptionAction.LIAISON_COMPONENT_ORDER.compare(partner, noProgram));
  }
}
