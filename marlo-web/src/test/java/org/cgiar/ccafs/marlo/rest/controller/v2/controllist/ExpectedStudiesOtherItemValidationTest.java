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
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyCountryManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyRegionManager;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudy;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyInfo;
import org.cgiar.ccafs.marlo.data.model.StudyType;
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.RestErrorHarness.Translated;
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.expectedStudies.ExpectedStudiesOtherItem;
import org.cgiar.ccafs.marlo.rest.dto.MeliaARDTO;
import org.cgiar.ccafs.marlo.rest.dto.MeliaDTO;
import org.cgiar.ccafs.marlo.rest.errors.ErrorDTO;
import org.cgiar.ccafs.marlo.rest.errors.NotFoundException;
import org.cgiar.ccafs.marlo.rest.mappers.MeliaMapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import ch.qos.logback.classic.Level;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * ENH-LOGGING-STANDARDIZATION-001-T17: {@code ExpectedStudiesOtherItem} read the year and the study of every
 * information row of a phase, and the information of a study in the requested phase, without checking that they
 * exist. A single incomplete row used to turn the whole answer into a swallowed empty 200.
 */
public class ExpectedStudiesOtherItemValidationTest {

  private static final long STUDY_ID = 5L;

  @Rule
  public RestErrorHarness harness = new RestErrorHarness();

  private Phase phase;
  private Phase otherPhase;
  private Phase orphanPhase;
  private List<Phase> phases;
  private ProjectExpectedStudy study;
  private List<MeliaARDTO> mapped;
  private ExpectedStudiesOtherItem<MeliaDTO> item;
  private ExpectedStudiesOther controller;

  @Before
  public void createItem() {
    GlobalUnit globalUnit = new GlobalUnit();
    globalUnit.setId(45L);
    globalUnit.setAcronym("AICCRA");

    this.phase = new Phase();
    this.phase.setId(100L);
    this.phase.setYear(2022);
    this.phase.setName("Reporting");
    this.phase.setCrp(globalUnit);
    this.otherPhase = new Phase();
    this.otherPhase.setId(99L);
    this.otherPhase.setYear(2021);
    this.otherPhase.setName("Reporting");
    this.otherPhase.setCrp(globalUnit);
    this.orphanPhase = new Phase();
    this.orphanPhase.setId(98L);
    this.orphanPhase.setYear(2022);
    this.orphanPhase.setName("Reporting");
    this.phases = new ArrayList<Phase>(Arrays.asList(this.otherPhase, this.phase));

    this.study = new ProjectExpectedStudy();
    this.study.setId(STUDY_ID);
    this.mapped = new ArrayList<MeliaARDTO>();

    GlobalUnitManager globalUnitManager = Fakes.fake(GlobalUnitManager.class)
      .on("findGlobalUnitByAcronym", args -> "AICCRA".equals(args[0]) ? globalUnit : null).build();
    PhaseManager phaseManager = Fakes.fake(PhaseManager.class).on("findAll", args -> this.phases).build();
    ProjectExpectedStudyManager studyManager = Fakes.fake(ProjectExpectedStudyManager.class)
      .on("getProjectExpectedStudyById", args -> ((Long) args[0]) == STUDY_ID ? this.study : null)
      .returning("isStudyExcluded", Boolean.TRUE).build();
    ProjectExpectedStudyRegionManager regionManager = Fakes.fake(ProjectExpectedStudyRegionManager.class)
      .returning("findAll", Collections.emptyList()).build();
    ProjectExpectedStudyCountryManager countryManager = Fakes.fake(ProjectExpectedStudyCountryManager.class)
      .returning("findAll", Collections.emptyList()).build();
    MeliaMapper mapper = Fakes.fake(MeliaMapper.class).on("projectExpectedStudyToMeliaARDTO", args -> {
      MeliaARDTO melia = new MeliaARDTO();
      this.mapped.add(melia);
      return melia;
    }).on("projectExpectedStudyToMeliaDTO", args -> new MeliaDTO()).build();

    this.item = Fakes.instance(ExpectedStudiesOtherItem.class);
    RestErrorHarness.inject(this.item, "globalUnitManager", globalUnitManager);
    RestErrorHarness.inject(this.item, "phaseManager", phaseManager);
    RestErrorHarness.inject(this.item, "projectExpectedStudyManager", studyManager);
    RestErrorHarness.inject(this.item, "projectExpectedStudyRegionManager", regionManager);
    RestErrorHarness.inject(this.item, "projectExpectedStudyCountryManager", countryManager);
    RestErrorHarness.inject(this.item, "projectExpectedStudiesOtherMapper", mapper);
    this.controller = new ExpectedStudiesOther(this.item, this.harness.userManager());
    RestErrorHarness.inject(this.controller, "env",
      RestErrorHarness.environment("ExpectedStudies.MELIA.GET.id.404", "No such MELIA study"));
  }

  private ProjectExpectedStudyInfo info(long id, Phase infoPhase, Integer year, ProjectExpectedStudy infoStudy) {
    ProjectExpectedStudyInfo info = new ProjectExpectedStudyInfo();
    // the entities are equal when their ids are, so each row needs its own
    info.setId(id);
    info.setPhase(infoPhase);
    info.setYear(year);
    info.setProjectExpectedStudy(infoStudy);
    StudyType type = new StudyType();
    type.setId(2L);
    info.setStudyType(type);
    return info;
  }

  @Test
  public void aStudyWithoutInformationInTheRequestedPhaseAnswers404() {
    this.study.setProjectExpectedStudyInfos(
      new HashSet<ProjectExpectedStudyInfo>(Arrays.asList(this.info(1, this.otherPhase, 2021, this.study))));

    Translated translated =
      this.harness.propagate(() -> this.controller.findExpectedStudyById("AICCRA", STUDY_ID, 2022, "Reporting"));

    assertTrue(translated.exception instanceof NotFoundException);
    assertEquals(404, translated.status);
    assertEquals("404 - No such MELIA study", ((ErrorDTO) translated.body).getMessage());
    assertEquals(Level.INFO, translated.loggedLevel);
  }

  @Test
  public void aStudyWithInformationInTheRequestedPhaseIsAnsweredAsBefore() {
    this.study.setProjectExpectedStudyInfos(
      new HashSet<ProjectExpectedStudyInfo>(Arrays.asList(this.info(1, this.phase, 2022, this.study))));

    MeliaDTO melia = this.controller.findExpectedStudyById("AICCRA", STUDY_ID, 2022, "Reporting").getBody();

    assertTrue(melia != null);
    this.harness.assertNothingLogged();
  }

  @Test
  public void aStudyIsAnsweredEvenWhenAnotherPhaseHasNoGlobalUnit() {
    // A phase that does not belong to any Global Unit must not stop the others from being found
    this.phases.add(0, this.orphanPhase);
    this.study.setProjectExpectedStudyInfos(
      new HashSet<ProjectExpectedStudyInfo>(Arrays.asList(this.info(1, this.phase, 2022, this.study))));

    assertTrue(this.controller.findExpectedStudyById("AICCRA", STUDY_ID, 2022, "Reporting").getBody() != null);
  }

  @Test
  public void theMeliaListIsAnsweredEvenWhenAnotherPhaseHasNoGlobalUnit() {
    // A phase that does not belong to any Global Unit must not stop the others from being found
    this.phases.add(0, this.orphanPhase);
    this.phase.setProjectExpectedStudyInfos(
      new HashSet<ProjectExpectedStudyInfo>(Arrays.asList(this.info(1, this.phase, 2022, this.study))));

    assertEquals(1, this.controller.findAllMeliaByGlobalUnit("AICCRA", 2022, "Reporting").size());
  }

  @Test
  public void theMeliaListIsAnsweredAsBefore() {
    this.phase.setProjectExpectedStudyInfos(
      new HashSet<ProjectExpectedStudyInfo>(Arrays.asList(this.info(1, this.phase, 2022, this.study))));

    List<MeliaARDTO> melia = this.controller.findAllMeliaByGlobalUnit("AICCRA", 2022, "Reporting");

    assertEquals(1, melia.size());
    assertSame(this.mapped.get(0), melia.get(0));
    this.harness.assertNothingLogged();
  }

  @Test
  public void theMeliaListIgnoresInformationRowsWithoutYearOrStudy() {
    ProjectExpectedStudyInfo complete = this.info(1, this.phase, 2022, this.study);
    this.phase.setProjectExpectedStudyInfos(new HashSet<ProjectExpectedStudyInfo>(Arrays.asList(complete,
      this.info(2, this.phase, null, this.study), this.info(3, this.phase, 2022, null))));

    List<MeliaARDTO> melia = this.controller.findAllMeliaByGlobalUnit("AICCRA", 2022, "Reporting");

    assertEquals(1, melia.size());
    assertSame(this.mapped.get(0), melia.get(0));
    this.harness.assertNothingLogged();
  }
}
