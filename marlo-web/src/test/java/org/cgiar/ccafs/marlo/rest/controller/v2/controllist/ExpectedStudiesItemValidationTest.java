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
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyQuantificationManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyRegionManager;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudy;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyFlagship;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyInfo;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyLink;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyRegion;
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.RestErrorHarness.Translated;
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.expectedStudies.ExpectedStudiesItem;
import org.cgiar.ccafs.marlo.rest.dto.ProjectExpectedStudyDTO;
import org.cgiar.ccafs.marlo.rest.errors.ErrorDTO;
import org.cgiar.ccafs.marlo.rest.errors.NotFoundException;
import org.cgiar.ccafs.marlo.rest.mappers.ProjectExpectedStudyMapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
 * ENH-LOGGING-STANDARDIZATION-001-T17: {@code ExpectedStudiesItem.findExpectedStudyById} read the information of a
 * study in the requested phase without checking that it exists, and read the phase of every child row without checking
 * that it is set. Both failures used to end in a swallowed empty 200.
 */
public class ExpectedStudiesItemValidationTest {

  private static final long STUDY_ID = 5L;

  @Rule
  public RestErrorHarness harness = new RestErrorHarness();

  private GlobalUnit globalUnit;
  private Phase phase;
  private Phase otherPhase;
  private ProjectExpectedStudy study;
  private List<ProjectExpectedStudyRegion> regionRows;
  private Phase orphanPhase;
  private List<Phase> phases;
  private ProjectExpectedStudyFlagship flagship;
  private ProjectExpectedStudyRegion region;
  private ProjectExpectedStudyLink linkFirst;
  private ProjectExpectedStudyLink linkSecond;
  private ProjectExpectedStudyLink linkWithoutUrl;
  private ExpectedStudiesItem<ProjectExpectedStudyDTO> item;
  private ExpectedStudies controller;

  @Before
  public void createItem() {
    this.globalUnit = new GlobalUnit();
    this.globalUnit.setId(45L);
    this.globalUnit.setAcronym("AICCRA");

    this.phase = new Phase();
    this.phase.setId(100L);
    this.phase.setYear(2022);
    this.phase.setName("Reporting");
    this.phase.setCrp(this.globalUnit);
    this.otherPhase = new Phase();
    this.otherPhase.setId(99L);
    this.otherPhase.setYear(2021);
    this.otherPhase.setName("Reporting");
    this.otherPhase.setCrp(this.globalUnit);
    this.orphanPhase = new Phase();
    this.orphanPhase.setId(98L);
    this.orphanPhase.setYear(2022);
    this.orphanPhase.setName("Reporting");
    this.phases = new ArrayList<Phase>(Arrays.asList(this.otherPhase, this.phase));

    this.study = new ProjectExpectedStudy();
    this.study.setId(STUDY_ID);
    this.regionRows = Collections.emptyList();

    GlobalUnitManager globalUnitManager = Fakes.fake(GlobalUnitManager.class)
      .on("findGlobalUnitByAcronym", args -> "AICCRA".equals(args[0]) ? this.globalUnit : null).build();
    PhaseManager phaseManager = Fakes.fake(PhaseManager.class).on("findAll", args -> this.phases).build();
    ProjectExpectedStudyManager studyManager = Fakes.fake(ProjectExpectedStudyManager.class)
      .on("getProjectExpectedStudyById", args -> ((Long) args[0]) == STUDY_ID ? this.study : null).build();
    ProjectExpectedStudyRegionManager regionManager = Fakes.fake(ProjectExpectedStudyRegionManager.class)
      .on("findAll", args -> this.regionRows).build();
    ProjectExpectedStudyCountryManager countryManager = Fakes.fake(ProjectExpectedStudyCountryManager.class)
      .returning("findAll", Collections.emptyList()).build();
    ProjectExpectedStudyQuantificationManager quantificationManager =
      Fakes.fake(ProjectExpectedStudyQuantificationManager.class).returning("findAll", Collections.emptyList()).build();
    ProjectExpectedStudyMapper mapper = Fakes.fake(ProjectExpectedStudyMapper.class)
      .on("projectExpectedStudyToProjectExpectedStudyDTO", args -> new ProjectExpectedStudyDTO()).build();

    this.item = Fakes.instance(ExpectedStudiesItem.class);
    RestErrorHarness.inject(this.item, "globalUnitManager", globalUnitManager);
    RestErrorHarness.inject(this.item, "phaseManager", phaseManager);
    RestErrorHarness.inject(this.item, "projectExpectedStudyManager", studyManager);
    RestErrorHarness.inject(this.item, "projectExpectedStudyRegionManager", regionManager);
    RestErrorHarness.inject(this.item, "projectExpectedStudyCountryManager", countryManager);
    RestErrorHarness.inject(this.item, "projectExpectedStudyQuantificationManager", quantificationManager);
    RestErrorHarness.inject(this.item, "projectExpectedStudyMapper", mapper);
    this.controller = new ExpectedStudies(this.item, this.harness.userManager());
    RestErrorHarness.inject(this.controller, "env",
      RestErrorHarness.environment("ExpectedStudies.OICR.GET.id.404", "No such OICR"));
  }

  private ProjectExpectedStudyInfo info(Phase infoPhase) {
    ProjectExpectedStudyInfo info = new ProjectExpectedStudyInfo();
    info.setPhase(infoPhase);
    return info;
  }

  @Test
  public void aStudyWithoutInformationInTheRequestedPhaseAnswers404() {
    this.study.setProjectExpectedStudyInfos(
      new HashSet<ProjectExpectedStudyInfo>(Arrays.asList(this.info(this.otherPhase))));

    Translated translated =
      this.harness.propagate(() -> this.controller.findExpectedStudyById("AICCRA", STUDY_ID, 2022, "Reporting"));

    assertTrue(translated.exception instanceof NotFoundException);
    assertEquals(404, translated.status);
    assertEquals("404 - No such OICR", ((ErrorDTO) translated.body).getMessage());
    assertEquals(Level.INFO, translated.loggedLevel);
  }

  @Test
  public void anUnknownStudyIsStillAnswered400() {
    Translated translated =
      this.harness.propagate(() -> this.controller.findExpectedStudyById("AICCRA", 9999L, 2022, "Reporting"));

    assertEquals(400, translated.status);
    assertEquals(Arrays.asList("ProjectExpectedStudyEntity: 9999 is an invalid Project Expected Study Code"),
      Fakes.fieldErrors(translated.body));
  }

  @Test
  public void anUnknownPhaseIsStillAnswered400() {
    Translated translated =
      this.harness.propagate(() -> this.controller.findExpectedStudyById("AICCRA", STUDY_ID, 2010, "Reporting"));

    assertEquals(400, translated.status);
  }

  @Test
  public void aStudyWithCompleteRowsIsAnsweredAsBefore() {
    List<ProjectExpectedStudyRegion> rows = this.addRows(false);

    ResponseEntity<ProjectExpectedStudyDTO> response =
      this.controller.findExpectedStudyById("AICCRA", STUDY_ID, 2022, "Reporting");

    this.assertAnsweredWithTheCompleteRows(response, rows);
  }

  @Test
  public void aStudyWhoseChildRowsLackAPhaseOrAStudyIsAnsweredWithTheCompleteRows() {
    List<ProjectExpectedStudyRegion> rows = this.addRows(true);

    ResponseEntity<ProjectExpectedStudyDTO> response =
      this.controller.findExpectedStudyById("AICCRA", STUDY_ID, 2022, "Reporting");

    this.assertAnsweredWithTheCompleteRows(response, rows);
  }

  @Test
  public void aStudyIsAnsweredEvenWhenAnotherPhaseHasNoGlobalUnit() {
    // A phase that does not belong to any Global Unit must not stop the others from being found
    this.phases.add(0, this.orphanPhase);
    this.addRows(false);

    ResponseEntity<ProjectExpectedStudyDTO> response =
      this.controller.findExpectedStudyById("AICCRA", STUDY_ID, 2022, "Reporting");

    assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  /**
   * Gives the study one flagship, two links and one region of the requested phase; with gaps it also has rows that lack
   * their phase, their study or their URL.
   *
   * @return the region rows that the manager answers
   */
  private List<ProjectExpectedStudyRegion> addRows(boolean gaps) {
    this.study.setProjectExpectedStudyInfos(
      new HashSet<ProjectExpectedStudyInfo>(Arrays.asList(this.info(this.phase))));

    ProjectExpectedStudyFlagship flagship = new ProjectExpectedStudyFlagship();
    flagship.setId(1L);
    flagship.setPhase(this.phase);
    this.flagship = flagship;
    Set<ProjectExpectedStudyFlagship> flagships = new HashSet<ProjectExpectedStudyFlagship>(Arrays.asList(flagship));

    this.linkSecond = this.link(1, "https://b", this.phase);
    this.linkFirst = this.link(2, "https://a", this.phase);
    Set<ProjectExpectedStudyLink> links =
      new HashSet<ProjectExpectedStudyLink>(Arrays.asList(this.linkSecond, this.linkFirst));

    this.region = new ProjectExpectedStudyRegion();
    this.region.setId(1L);
    this.region.setProjectExpectedStudy(this.study);
    this.region.setPhase(this.phase);
    List<ProjectExpectedStudyRegion> regions = new ArrayList<ProjectExpectedStudyRegion>(Arrays.asList(this.region));

    if (gaps) {
      ProjectExpectedStudyFlagship orphanFlagship = new ProjectExpectedStudyFlagship();
      orphanFlagship.setId(2L);
      flagships.add(orphanFlagship);
      this.linkWithoutUrl = this.link(3, null, this.phase);
      links.add(this.linkWithoutUrl);
      links.add(this.link(4, "x", null));
      ProjectExpectedStudyRegion regionWithoutStudy = new ProjectExpectedStudyRegion();
      regionWithoutStudy.setId(2L);
      regionWithoutStudy.setPhase(this.phase);
      ProjectExpectedStudyRegion regionWithoutPhase = new ProjectExpectedStudyRegion();
      regionWithoutPhase.setId(3L);
      regionWithoutPhase.setProjectExpectedStudy(this.study);
      regions.add(0, regionWithoutStudy);
      regions.add(regionWithoutPhase);
    } else {
      this.linkWithoutUrl = null;
    }
    this.study.setProjectExpectedStudyFlagships(flagships);
    this.study.setProjectExpectedStudyLinks(links);
    this.regionRows = regions;
    return regions;
  }

  private void assertAnsweredWithTheCompleteRows(ResponseEntity<ProjectExpectedStudyDTO> response,
    List<ProjectExpectedStudyRegion> rows) {
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(Arrays.asList(this.flagship), this.study.getFlagships());
    assertEquals(Arrays.asList(this.region), this.study.getStudyRegions());
    // the links are sorted by URL, and one without URL goes last
    List<ProjectExpectedStudyLink> sorted =
      new ArrayList<ProjectExpectedStudyLink>(Arrays.asList(this.linkFirst, this.linkSecond));
    if (this.linkWithoutUrl != null) {
      sorted.add(this.linkWithoutUrl);
    }
    assertEquals(sorted, this.study.getLinks());
    this.harness.assertNothingLogged();
  }

  private ProjectExpectedStudyLink link(long id, String url, Phase linkPhase) {
    ProjectExpectedStudyLink link = new ProjectExpectedStudyLink();
    link.setId(id);
    link.setLink(url);
    link.setPhase(linkPhase);
    return link;
  }
}
