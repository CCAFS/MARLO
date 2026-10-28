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

package org.cgiar.ccafs.marlo.rest.controller.v2.controllist;

import org.cgiar.ccafs.marlo.data.manager.CrpProgramOutcomeManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.LiaisonInstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisFlagshipProgressManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisFlagshipProgressOutcomeManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisManager;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.CrpProgramOutcome;
import org.cgiar.ccafs.marlo.data.model.CrpUser;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesis;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgress;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgressOutcome;
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.RestErrorHarness.Translated;
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.statusPlannedOutcomes.StatusPlannedOutcomesItem;
import org.cgiar.ccafs.marlo.rest.dto.StatusPlannedOutcomesDTO;
import org.cgiar.ccafs.marlo.rest.mappers.StatusPlannedOutcomesMapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import ch.qos.logback.classic.Level;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * ENH-LOGGING-STANDARDIZATION-001-T17: {@code StatusPlannedOutcomesItem.findStatusPlannedOutcome} assumed that the
 * user exists and read the Global Unit of every phase of the platform without checking that it is set.
 */
public class StatusPlannedOutcomesItemValidationTest {

  @Rule
  public RestErrorHarness harness = new RestErrorHarness();

  private StatusPlannedOutcomesItem<StatusPlannedOutcomesDTO> item;
  private Phase orphanPhase;
  private List<Phase> phases;
  private StatusPlannedOutcomes controller;

  @Before
  public void createController() {
    GlobalUnit globalUnit = new GlobalUnit();
    globalUnit.setId(45L);
    globalUnit.setAcronym("AICCRA");
    Phase phase = new Phase();
    phase.setId(100L);
    phase.setYear(2024);
    phase.setName("Reporting");
    phase.setCrp(globalUnit);
    this.orphanPhase = new Phase();
    this.orphanPhase.setId(98L);
    this.orphanPhase.setYear(2024);
    this.orphanPhase.setName("Reporting");
    this.phases = new ArrayList<Phase>(Arrays.asList(phase));

    CrpUser crpUser = new CrpUser();
    crpUser.setCrp(globalUnit);
    this.harness.user().setCrpUsers(new HashSet<CrpUser>(Arrays.asList(crpUser)));

    CrpProgram program = new CrpProgram();
    program.setId(9L);
    program.setAcronym("FP1");
    CrpProgramOutcome outcome = new CrpProgramOutcome();
    outcome.setId(8L);
    outcome.setCrpProgram(program);
    LiaisonInstitution liaison = new LiaisonInstitution();
    liaison.setId(3L);
    ReportSynthesis synthesis = new ReportSynthesis();
    synthesis.setId(4L);
    ReportSynthesisFlagshipProgress progress = new ReportSynthesisFlagshipProgress();
    progress.setId(5L);
    ReportSynthesisFlagshipProgressOutcome progressOutcome = new ReportSynthesisFlagshipProgressOutcome();
    progressOutcome.setCrpProgramOutcome(outcome);

    GlobalUnitManager globalUnitManager = Fakes.fake(GlobalUnitManager.class)
      .on("findGlobalUnitByAcronym", args -> "AICCRA".equals(args[0]) ? globalUnit : null).build();
    PhaseManager phaseManager = Fakes.fake(PhaseManager.class).on("findAll", args -> this.phases).build();
    CrpProgramOutcomeManager outcomeManager =
      Fakes.fake(CrpProgramOutcomeManager.class).returning("getCrpProgramOutcome", outcome).build();
    LiaisonInstitutionManager liaisonManager =
      Fakes.fake(LiaisonInstitutionManager.class).returning("findByAcronymAndCrp", liaison).build();
    ReportSynthesisManager synthesisManager =
      Fakes.fake(ReportSynthesisManager.class).returning("findSynthesis", synthesis).build();
    ReportSynthesisFlagshipProgressManager progressManager = Fakes.fake(ReportSynthesisFlagshipProgressManager.class)
      .returning("getReportSynthesisFlagshipProgressById", progress).build();
    ReportSynthesisFlagshipProgressOutcomeManager progressOutcomeManager =
      Fakes.fake(ReportSynthesisFlagshipProgressOutcomeManager.class).returning("getOutcomeId", progressOutcome)
        .build();
    StatusPlannedOutcomesMapper mapper = Fakes.fake(StatusPlannedOutcomesMapper.class)
      .on("reportSynthesisFlagshipProgressOutcomeToStatusPlannedOutcomesDTO", args -> new StatusPlannedOutcomesDTO())
      .build();

    this.item = Fakes.instance(StatusPlannedOutcomesItem.class);
    RestErrorHarness.inject(this.item, "globalUnitManager", globalUnitManager);
    RestErrorHarness.inject(this.item, "phaseManager", phaseManager);
    RestErrorHarness.inject(this.item, "crpProgramOutcomeManager", outcomeManager);
    RestErrorHarness.inject(this.item, "liaisonInstitutionManager", liaisonManager);
    RestErrorHarness.inject(this.item, "reportSynthesisManager", synthesisManager);
    RestErrorHarness.inject(this.item, "reportSynthesisFlagshipProgressManager", progressManager);
    RestErrorHarness.inject(this.item, "reportSynthesisFlagshipProgressOutcomeManager", progressOutcomeManager);
    RestErrorHarness.inject(this.item, "statusPlannedOutcomesMapper", mapper);
    this.controller = new StatusPlannedOutcomes(this.item, null, this.harness.userManager());
    RestErrorHarness.inject(this.controller, "env",
      RestErrorHarness.environment("StatusPlannedOutcomes.outcomes.GET.id.404", "No such planned outcome status"));
  }

  @Test
  public void aStatusIsAnsweredAsBefore() {
    ResponseEntity<StatusPlannedOutcomesDTO> response =
      this.controller.findStatusPlannedOutcome("AICCRA", "FP1-O1", 2024, "Reporting");

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    this.harness.assertNothingLogged();
  }

  @Test
  public void aStatusIsAnsweredEvenWhenAnotherPhaseHasNoGlobalUnit() {
    // A phase that does not belong to any Global Unit must not stop the others from being found
    this.phases.add(0, this.orphanPhase);
    ResponseEntity<StatusPlannedOutcomesDTO> response =
      this.controller.findStatusPlannedOutcome("AICCRA", "FP1-O1", 2024, "Reporting");

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    this.harness.assertNothingLogged();
  }

  @Test
  public void aUserThatCannotBeFoundIsNotAuthorized() {
    UserManager noUser = Fakes.fake(UserManager.class).returning("getUser", null).build();
    StatusPlannedOutcomes unauthenticated = new StatusPlannedOutcomes(this.item, null, noUser);

    Translated translated = this.harness
      .propagate(() -> unauthenticated.findStatusPlannedOutcome("AICCRA", "FP1-O1", 2024, "Reporting"));

    assertEquals(400, translated.status);
    assertEquals(Arrays.asList("GlobalUnitEntity: CGIAR entity not autorized"), Fakes.fieldErrors(translated.body));
    assertEquals(Level.WARN, translated.loggedLevel);
  }

  @Test
  public void anEntityTheUserDoesNotBelongToIsStillNotAuthorized() {
    this.harness.user().setCrpUsers(new HashSet<CrpUser>());

    Translated translated =
      this.harness.propagate(() -> this.controller.findStatusPlannedOutcome("AICCRA", "FP1-O1", 2024, "Reporting"));

    assertEquals(400, translated.status);
    assertEquals(Arrays.asList("GlobalUnitEntity: CGIAR entity not autorized"), Fakes.fieldErrors(translated.body));
  }
}
