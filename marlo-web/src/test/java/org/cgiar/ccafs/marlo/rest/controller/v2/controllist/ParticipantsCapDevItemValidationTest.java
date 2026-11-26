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
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisCrossCuttingDimensionManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisManager;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesis;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisCrossCuttingDimension;
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.RestErrorHarness.Translated;
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.ParticipantsCapDev.ParticipantsCapDevItem;
import org.cgiar.ccafs.marlo.rest.dto.ParticipantsCapDevDTO;

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
import static org.junit.Assert.assertNull;

/**
 * ENH-LOGGING-STANDARDIZATION-001-T17: {@code ParticipantsCapDevItem.deleteParticipantsCapDevById} read the Global
 * Unit of the request after finding a phase for its acronym, and the Global Unit of every phase of the platform,
 * without checking that either exists.
 */
public class ParticipantsCapDevItemValidationTest {

  private static final long DIMENSION_ID = 31L;

  @Rule
  public RestErrorHarness harness = new RestErrorHarness();

  private GlobalUnit globalUnit;
  private Phase orphanPhase;
  private List<Phase> phases;
  private ReportSynthesisCrossCuttingDimension dimension;
  private int saves;
  private ParticipantsCapDev controller;

  @Before
  public void createController() {
    this.globalUnit = new GlobalUnit();
    this.globalUnit.setId(45L);
    this.globalUnit.setAcronym("AICCRA");
    Phase phase = new Phase();
    phase.setId(100L);
    phase.setYear(2024);
    phase.setName("Reporting");
    phase.setCrp(this.globalUnit);
    this.orphanPhase = new Phase();
    this.orphanPhase.setId(98L);
    this.orphanPhase.setYear(2024);
    this.orphanPhase.setName("Reporting");
    this.phases = new ArrayList<Phase>(Arrays.asList(phase));

    this.dimension = new ReportSynthesisCrossCuttingDimension();
    this.dimension.setId(DIMENSION_ID);
    this.dimension.setPhdFemale(8.0);
    LiaisonInstitution liaison = new LiaisonInstitution();
    liaison.setId(3L);
    ReportSynthesis synthesis = new ReportSynthesis();
    synthesis.setId(4L);

    // The phases name AICCRA, but the Global Unit lookup does not find it
    GlobalUnitManager globalUnitManager = Fakes.fake(GlobalUnitManager.class)
      .on("findGlobalUnitByAcronym", args -> "AICCRA".equals(args[0]) ? this.globalUnit : null).build();
    PhaseManager phaseManager = Fakes.fake(PhaseManager.class).on("findAll", args -> this.phases).build();
    LiaisonInstitutionManager liaisonManager =
      Fakes.fake(LiaisonInstitutionManager.class).returning("findByAcronymAndCrp", liaison).build();
    ReportSynthesisManager synthesisManager =
      Fakes.fake(ReportSynthesisManager.class).returning("findSynthesis", synthesis).build();
    ReportSynthesisCrossCuttingDimensionManager dimensionManager =
      Fakes.fake(ReportSynthesisCrossCuttingDimensionManager.class)
        .on("getReportSynthesisCrossCuttingDimensionById",
          args -> ((Long) args[0]) == DIMENSION_ID ? this.dimension : null)
        .on("saveReportSynthesisCrossCuttingDimension", args -> {
          this.saves++;
          return args[0];
        }).build();

    ParticipantsCapDevItem<ParticipantsCapDevDTO> item = Fakes.instance(ParticipantsCapDevItem.class);
    RestErrorHarness.inject(item, "globalUnitManager", globalUnitManager);
    RestErrorHarness.inject(item, "phaseManager", phaseManager);
    RestErrorHarness.inject(item, "liaisonInstitutionManager", liaisonManager);
    RestErrorHarness.inject(item, "reportSynthesisManager", synthesisManager);
    RestErrorHarness.inject(item, "reportSynthesisCrossCuttingDimensionManager", dimensionManager);
    this.controller = new ParticipantsCapDev(item, this.harness.userManager());
    RestErrorHarness.inject(this.controller, "env",
      RestErrorHarness.environment("ParticipantsCapDev.participantscapdev.DELETE.id.404", "No such participants"));
  }

  @Test
  public void theParticipantsAreClearedAsBefore() {
    ResponseEntity<Long> response =
      this.controller.deleteParticipantsCapDevById("AICCRA", DIMENSION_ID, 2024, "Reporting");

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(Long.valueOf(DIMENSION_ID), response.getBody());
    assertNull(this.dimension.getPhdFemale());
    assertEquals(1, this.saves);
    this.harness.assertNothingLogged();
  }

  @Test
  public void theParticipantsAreClearedEvenWhenAnotherPhaseHasNoGlobalUnit() {
    // A phase that does not belong to any Global Unit must not stop the others from being found
    this.phases.add(0, this.orphanPhase);
    ResponseEntity<Long> response =
      this.controller.deleteParticipantsCapDevById("AICCRA", DIMENSION_ID, 2024, "Reporting");

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(Long.valueOf(DIMENSION_ID), response.getBody());
    assertNull(this.dimension.getPhdFemale());
    assertEquals(1, this.saves);
    this.harness.assertNothingLogged();
  }

  @Test
  public void aGlobalUnitThatCannotBeFoundIsAnswered400() {
    this.globalUnit.setAcronym("AICCRA");
    // the phases still name it, as they do when the Global Unit lookup and the phase lookup disagree
    Translated translated = this.harness.propagate(
      () -> this.controller.deleteParticipantsCapDevById("aiccra", DIMENSION_ID, 2024, "Reporting"));

    assertEquals(400, translated.status);
    assertEquals(Arrays.asList("GlobalUnitEntity: aiccra is an invalid CGIAR entity acronym"),
      Fakes.fieldErrors(translated.body));
    assertEquals(Level.WARN, translated.loggedLevel);
    assertEquals("nothing is cleared", 0, this.saves);
  }

  @Test
  public void anUnknownDimensionIsAnswered400() {
    Translated translated = this.harness
      .propagate(() -> this.controller.deleteParticipantsCapDevById("AICCRA", 9999L, 2024, "Reporting"));

    assertEquals(400, translated.status);
    assertEquals(0, this.saves);
  }
}
