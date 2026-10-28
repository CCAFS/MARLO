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

import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.LiaisonInstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisFundingUseExpendituryAreaManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisFundingUseSummaryManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisManager;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesis;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFundingUseExpendituryArea;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFundingUseSummary;
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.RestErrorHarness.Translated;
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.Expenditures.ExpendituresItem;
import org.cgiar.ccafs.marlo.rest.dto.W1W2ExpenditureDTO;
import org.cgiar.ccafs.marlo.rest.errors.ErrorDTO;
import org.cgiar.ccafs.marlo.rest.errors.NotFoundException;
import org.cgiar.ccafs.marlo.rest.mappers.ExpenditureMapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ch.qos.logback.classic.Level;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * ENH-LOGGING-STANDARDIZATION-001-T17: {@code ExpendituresItem.getExpenditure} read the Global Unit of every phase of
 * the platform without checking that it is set; one phase without it turned every request into a swallowed empty 200.
 */
public class ExpendituresItemValidationTest {

  private static final long EXAMPLE_ID = 7L;

  @Rule
  public RestErrorHarness harness = new RestErrorHarness();

  private ReportSynthesisFundingUseExpendituryArea example;
  private Phase orphanPhase;
  private List<Phase> phases;
  private Expenditures controller;

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

    this.example = new ReportSynthesisFundingUseExpendituryArea();
    this.example.setId(EXAMPLE_ID);
    LiaisonInstitution liaison = new LiaisonInstitution();
    liaison.setId(3L);
    ReportSynthesis synthesis = new ReportSynthesis();
    synthesis.setId(4L);

    GlobalUnitManager globalUnitManager = Fakes.fake(GlobalUnitManager.class)
      .on("findGlobalUnitByAcronym", args -> "AICCRA".equals(args[0]) ? globalUnit : null).build();
    PhaseManager phaseManager = Fakes.fake(PhaseManager.class).on("findAll", args -> this.phases).build();
    LiaisonInstitutionManager liaisonManager =
      Fakes.fake(LiaisonInstitutionManager.class).returning("findByAcronymAndCrp", liaison).build();
    ReportSynthesisManager synthesisManager =
      Fakes.fake(ReportSynthesisManager.class).returning("findSynthesis", synthesis).build();
    ReportSynthesisFundingUseSummaryManager summaryManager = Fakes.fake(ReportSynthesisFundingUseSummaryManager.class)
      .returning("getReportSynthesisFundingUseSummaryById", new ReportSynthesisFundingUseSummary()).build();
    ReportSynthesisFundingUseExpendituryAreaManager areaManager =
      Fakes.fake(ReportSynthesisFundingUseExpendituryAreaManager.class)
        .on("getReportSynthesisFundingUseExpendituryAreaById",
          args -> ((Long) args[0]) == EXAMPLE_ID ? this.example : null)
        .build();
    ExpenditureMapper mapper = Fakes.fake(ExpenditureMapper.class)
      .on("reportSynthesisFundingUseExpendituryAreaToW1W2ExpenditureDTO", args -> new W1W2ExpenditureDTO()).build();

    ExpendituresItem<W1W2ExpenditureDTO> item = Fakes.instance(ExpendituresItem.class);
    RestErrorHarness.inject(item, "globalUnitManager", globalUnitManager);
    RestErrorHarness.inject(item, "phaseManager", phaseManager);
    RestErrorHarness.inject(item, "liaisonInstitutionManager", liaisonManager);
    RestErrorHarness.inject(item, "reportSynthesisManager", synthesisManager);
    RestErrorHarness.inject(item, "reportSynthesisFundingUseSummaryManager", summaryManager);
    RestErrorHarness.inject(item, "reportSynthesisFundingUseExpendituryAreaManager", areaManager);
    RestErrorHarness.inject(item, "expenditureMapper", mapper);
    this.controller = new Expenditures(this.harness.userManager(), item);
    RestErrorHarness.inject(this.controller, "env",
      RestErrorHarness.environment("Expenditure.Example.GET.id.404", "No such expenditure example"));
  }

  @Test
  public void anExpenditureIsAnsweredAsBefore() {
    ResponseEntity<W1W2ExpenditureDTO> response =
      this.controller.findExpenditureExampleById("AICCRA", EXAMPLE_ID, 2024, "Reporting");

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    this.harness.assertNothingLogged();
  }

  @Test
  public void anExpenditureIsAnsweredEvenWhenAnotherPhaseHasNoGlobalUnit() {
    // A phase that does not belong to any Global Unit must not stop the others from being found
    this.phases.add(0, this.orphanPhase);
    ResponseEntity<W1W2ExpenditureDTO> response =
      this.controller.findExpenditureExampleById("AICCRA", EXAMPLE_ID, 2024, "Reporting");

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    this.harness.assertNothingLogged();
  }

  @Test
  public void anUnknownPhaseIsAnswered400() {
    Translated translated =
      this.harness.propagate(() -> this.controller.findExpenditureExampleById("AICCRA", EXAMPLE_ID, 2010, "Reporting"));

    assertEquals(400, translated.status);
    assertEquals(Arrays.asList("phase: 2010 is an invalid year"), Fakes.fieldErrors(translated.body));
    assertEquals(Level.WARN, translated.loggedLevel);
  }

  @Test
  public void anUnknownExpenditureIsAnswered404() {
    Translated translated =
      this.harness.propagate(() -> this.controller.findExpenditureExampleById("AICCRA", 9999L, 2024, "Reporting"));

    assertTrue(translated.exception instanceof NotFoundException);
    assertEquals(404, translated.status);
    assertEquals("404 - No such expenditure example", ((ErrorDTO) translated.body).getMessage());
  }
}
