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

import org.cgiar.ccafs.marlo.data.manager.DeliverableInfoManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.LocElementManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectLocationManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.model.Activity;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.CustomParameter;
import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.data.model.DeliverableInfo;
import org.cgiar.ccafs.marlo.data.model.GeneralStatus;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.LocElement;
import org.cgiar.ccafs.marlo.data.model.LocElementType;
import org.cgiar.ccafs.marlo.data.model.Parameter;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProgramType;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudy;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyInfo;
import org.cgiar.ccafs.marlo.data.model.ProjectFocus;
import org.cgiar.ccafs.marlo.data.model.ProjectLocation;
import org.cgiar.ccafs.marlo.data.model.ProjectOutcome;
import org.cgiar.ccafs.marlo.data.model.ProjectPartner;
import org.cgiar.ccafs.marlo.data.model.StudyType;
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.RestErrorHarness.Translated;
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.projectPage.ProjectPageItem;
import org.cgiar.ccafs.marlo.rest.dto.ProjectPageDTO;
import org.cgiar.ccafs.marlo.rest.errors.ErrorConstants;
import org.cgiar.ccafs.marlo.rest.errors.ErrorDTO;
import org.cgiar.ccafs.marlo.rest.errors.MARLOFieldValidationException;
import org.cgiar.ccafs.marlo.rest.errors.NotFoundException;
import org.cgiar.ccafs.marlo.rest.mappers.ProjectPageMapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * ENH-LOGGING-STANDARDIZATION-001-T17: what {@code ProjectPageItem} answers now that {@code ProjectPage} no longer
 * hides its failures. A Global Unit that switched its project pages off, a Global Unit without a usable project page
 * year and a project whose rows are incomplete used to end in a swallowed empty 200 (or, on the list, in an unlogged
 * 400 or 500); each must answer what the item intended. A Global Unit without a crp_project_page parameter keeps
 * publishing, as it always did.
 */
public class ProjectPageItemValidationTest {

  private static final String ENTITY = "AICCRA";
  private static final long PROJECT_ID = 7L;
  private static final String UNUSABLE_YEAR = "The Global Unit AICCRA has no usable project page year: the custom "
    + "parameter project_website_year_value is missing or its value is not a number";

  @Rule
  public RestErrorHarness harness = new RestErrorHarness();

  private GlobalUnit globalUnit;
  private Project project;
  private Phase phase;
  private Set<CustomParameter> parameters;
  private List<Object[]> cycleLookups;
  private Phase cycleAnswer;
  private List<Project> webPageList;
  private Fakes.Fake<ProjectManager> projectManager;
  private ProjectLocationManager locations;
  private ProjectPageItem<ProjectPage> item;
  private ProjectPage controller;
  private Activity ongoing;
  private ProjectPartner partner;
  private ProjectOutcome outcome;
  private ProjectExpectedStudy completed;
  private Deliverable deliverable;
  private CrpProgram program;
  private ProjectLocation country;

  @Before
  public void createItem() {
    this.parameters = new HashSet<CustomParameter>();
    this.globalUnit = new GlobalUnit();
    this.globalUnit.setId(45L);
    this.globalUnit.setAcronym(ENTITY);
    this.globalUnit.setCustomParameters(this.parameters);

    this.phase = new Phase();
    this.phase.setId(100L);
    this.phase.setYear(2019);
    this.phase.setName("Planning");
    this.phase.setCrp(this.globalUnit);

    this.project = new Project();
    this.project.setId(PROJECT_ID);

    this.cycleLookups = new ArrayList<Object[]>();
    this.cycleAnswer = null;
    this.webPageList = new ArrayList<Project>();
    this.locations = Fakes.fake(ProjectLocationManager.class).returning("findAll", Collections.emptyList()).build();

    this.projectManager = Fakes.fake(ProjectManager.class)
      .on("getProjectById", args -> ((Long) args[0]) == PROJECT_ID ? this.project : null)
      .on("getProjectWebPageList", args -> this.webPageList);
    GlobalUnitManager globalUnitManager = Fakes.fake(GlobalUnitManager.class)
      .on("findGlobalUnitByAcronym", args -> ENTITY.equals(args[0]) ? this.globalUnit : null).build();
    PhaseManager phaseManager = Fakes.fake(PhaseManager.class).on("findCycle", args -> {
      this.cycleLookups.add(args);
      return this.cycleAnswer;
    }).build();
    // the page carries the id of the project it was built from, so a test can tell which project it answers
    ProjectPageMapper mapper = Fakes.fake(ProjectPageMapper.class).on("projectToProjectPageDTO", args -> {
      if (args[0] == null) {
        return null;
      }
      ProjectPageDTO page = new ProjectPageDTO();
      page.setId(((Project) args[0]).getId());
      return page;
    }).build();

    this.item = Fakes.instance(ProjectPageItem.class);
    RestErrorHarness.inject(this.item, "projectManager", this.projectManager.build());
    RestErrorHarness.inject(this.item, "globalUnitManager", globalUnitManager);
    RestErrorHarness.inject(this.item, "phaseManager", phaseManager);
    RestErrorHarness.inject(this.item, "projectLocationManager", this.locations);
    RestErrorHarness.inject(this.item, "projectPageMapper", mapper);
    this.controller = new ProjectPage(this.item);
  }

  private void addParameter(String key, String value) {
    Parameter parameter = new Parameter();
    parameter.setKey(key);
    CustomParameter custom = new CustomParameter();
    // the entities are equal when their ids are, so each parameter needs its own
    custom.setId((long) this.parameters.size() + 1);
    custom.setParameter(parameter);
    custom.setValue(value);
    this.parameters.add(custom);
  }

  private static Activity activity(long id, Phase phase, Integer status) {
    Activity activity = new Activity();
    activity.setId(id);
    activity.setPhase(phase);
    activity.setActivityStatus(status);
    return activity;
  }

  private static ProjectLocation location(long id, Project project, Phase phase, LocElement element) {
    ProjectLocation location = new ProjectLocation();
    location.setId(id);
    location.setProject(project);
    location.setPhase(phase);
    location.setLocElement(element);
    return location;
  }

  private static LocElement element(long id, Long typeId) {
    LocElement element = new LocElement();
    element.setId(id);
    if (typeId != null) {
      LocElementType type = new LocElementType();
      type.setId(typeId);
      element.setLocElementType(type);
    }
    return element;
  }

  private ProjectExpectedStudy study(long id, Long statusId) {
    ProjectExpectedStudyInfo info = new ProjectExpectedStudyInfo();
    info.setPhase(this.phase);
    info.setIsPublic(false);
    StudyType type = new StudyType();
    type.setId(1L);
    info.setStudyType(type);
    if (statusId != null) {
      GeneralStatus status = new GeneralStatus();
      status.setId(statusId);
      info.setStatus(status);
    }
    ProjectExpectedStudy study = new ProjectExpectedStudy();
    study.setId(id);
    study.setProjectExpectedStudyInfos(new HashSet<ProjectExpectedStudyInfo>(Arrays.asList(info)));
    return study;
  }

  @Test
  public void aGlobalUnitWithoutTheProjectPageParameterStillPublishesItsProjectPage() {
    // No crp_project_page row has always meant "enabled": only a value other than "true" switches the pages off
    this.addParameter("project_website_year_value", "2019");
    this.cycleAnswer = this.phase;

    ResponseEntity<ProjectPageDTO> response = this.controller.findProjectById(ENTITY, PROJECT_ID);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(Long.valueOf(PROJECT_ID), response.getBody().getId());
    assertEquals(2019, this.cycleLookups.get(0)[1]);
    this.harness.assertNothingLogged();
  }

  @Test
  public void aGlobalUnitWithTheProjectPageFlagSwitchedOffIsStillNotAuthorized() {
    this.addParameter("crp_project_page", "false");
    this.addParameter("project_website_year_value", "2019");

    Translated translated = this.harness.propagate(() -> this.controller.findProjectById(ENTITY, PROJECT_ID));

    assertEquals(400, translated.status);
    assertEquals(Arrays.asList("GlobalUnitEntity: CGIAR entity not autorized"), Fakes.fieldErrors(translated.body));
    assertTrue("the page year is not even looked up", this.cycleLookups.isEmpty());
  }

  @Test
  public void aGlobalUnitWithoutProjectPageYearAnswers500() {
    this.addParameter("crp_project_page", "true");

    Translated translated = this.harness.propagate(() -> this.controller.findProjectById(ENTITY, PROJECT_ID));

    this.assertAnsweredAsUnusableProjectPageYear(translated);
    assertNull(translated.exception.getCause());
  }

  @Test
  public void aProjectPageYearWithoutValueAnswers500() {
    this.addParameter("crp_project_page", "true");
    this.addParameter("project_website_year_value", null);

    Translated translated = this.harness.propagate(() -> this.controller.findProjectById(ENTITY, PROJECT_ID));

    this.assertAnsweredAsUnusableProjectPageYear(translated);
  }

  @Test
  public void aProjectPageYearThatIsNotANumberAnswers500AndKeepsTheValueOutOfTheAnswer() {
    this.addParameter("crp_project_page", "true");
    this.addParameter("project_website_year_value", "twenty");

    Translated translated = this.harness.propagate(() -> this.controller.findProjectById(ENTITY, PROJECT_ID));

    this.assertAnsweredAsUnusableProjectPageYear(translated);
    this.assertValueReachesOnlyTheLog(translated);
  }

  @Test
  public void aConfiguredYearWithoutPlanningPhaseAnswers404() {
    this.addParameter("crp_project_page", "true");
    this.addParameter("project_website_year_value", "0");

    Translated translated = this.harness.propagate(() -> this.controller.findProjectById(ENTITY, PROJECT_ID));

    assertTrue(translated.exception instanceof NotFoundException);
    assertEquals(404, translated.status);
    assertEquals("404 - Not Found", ((ErrorDTO) translated.body).getMessage());
    assertEquals(Level.INFO, translated.loggedLevel);
    assertEquals(0, this.cycleLookups.get(0)[1]);
  }

  @Test
  public void aPublishedProjectIsAnsweredWithItsPageAsBefore() {
    this.addParameter("crp_project_page", "TRUE");
    this.addParameter("project_website_year_value", "2019");
    this.cycleAnswer = this.phase;

    ResponseEntity<ProjectPageDTO> response = this.controller.findProjectById(ENTITY, PROJECT_ID);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(Long.valueOf(PROJECT_ID), response.getBody().getId());
    assertEquals(1, this.cycleLookups.size());
    assertEquals("Planning", this.cycleLookups.get(0)[0]);
    assertEquals(2019, this.cycleLookups.get(0)[1]);
    assertEquals(true, this.cycleLookups.get(0)[2]);
    assertEquals(45L, this.cycleLookups.get(0)[3]);
    this.harness.assertNothingLogged();
  }

  @Test
  public void anUnknownProjectIsStillAnswered400() {
    this.addParameter("crp_project_page", "true");
    this.addParameter("project_website_year_value", "2019");

    Translated translated = this.harness.propagate(() -> this.controller.findProjectById(ENTITY, 9999L));

    assertEquals(400, translated.status);
    assertEquals(Arrays.asList("Project: Invalid Project code"), Fakes.fieldErrors(translated.body));
  }

  @Test
  public void theProjectListOfAGlobalUnitWithoutTheParameterIsStillAnswered() {
    this.addParameter("project_website_year_value", "2019");
    this.webPageList.add(this.project);
    this.cycleAnswer = this.phase;

    List<ProjectPageDTO> pages = this.controller.findAllProjectPage(ENTITY);

    assertEquals(1, pages.size());
    assertEquals(Long.valueOf(PROJECT_ID), pages.get(0).getId());
    assertEquals(2019, this.cycleLookups.get(0)[1]);
    this.harness.assertNothingLogged();
  }

  @Test
  public void theProjectListOfAGlobalUnitWithTheProjectPageFlagSwitchedOffIsStillEmpty() {
    this.addParameter("crp_project_page", "false");
    this.addParameter("project_website_year_value", "2019");
    this.webPageList.add(this.project);

    List<ProjectPageDTO> pages = this.controller.findAllProjectPage(ENTITY);

    assertTrue(pages.isEmpty());
    assertFalse("the projects of a Global Unit that did not publish are not read",
      this.projectManager.calls().contains("getProjectWebPageList"));
  }

  @Test
  public void theProjectListOfAPublishedGlobalUnitIsAnsweredAsBefore() {
    this.addParameter("crp_project_page", "true");
    this.addParameter("project_website_year_value", "2019");
    this.webPageList.add(this.project);
    this.cycleAnswer = this.phase;

    List<ProjectPageDTO> pages = this.controller.findAllProjectPage(ENTITY);

    assertEquals(1, pages.size());
    assertEquals(Long.valueOf(PROJECT_ID), pages.get(0).getId());
    assertEquals(1, this.cycleLookups.size());
    assertEquals(2019, this.cycleLookups.get(0)[1]);
    this.harness.assertNothingLogged();
  }

  @Test
  public void theProjectListOfAGlobalUnitWithoutProjectsIsEmptyBecauseNoYearIsRead() {
    // The year is read per project, as it always was: with nothing to publish, a missing year is not consulted
    this.addParameter("crp_project_page", "true");

    List<ProjectPageDTO> pages = this.controller.findAllProjectPage(ENTITY);

    assertTrue(pages.isEmpty());
    assertTrue(this.cycleLookups.isEmpty());
    this.harness.assertNothingLogged();
  }

  @Test
  public void theProjectListOfAGlobalUnitWithoutProjectPageYearAnswers500() {
    this.addParameter("crp_project_page", "true");
    this.webPageList.add(this.project);

    Translated translated = this.harness.propagate(() -> this.controller.findAllProjectPage(ENTITY));

    this.assertAnsweredAsUnusableProjectPageYear(translated);
    assertNull(translated.exception.getCause());
  }

  @Test
  public void theProjectListOfAProjectPageYearWithoutValueAnswers500() {
    this.addParameter("crp_project_page", "true");
    this.addParameter("project_website_year_value", null);
    this.webPageList.add(this.project);

    Translated translated = this.harness.propagate(() -> this.controller.findAllProjectPage(ENTITY));

    this.assertAnsweredAsUnusableProjectPageYear(translated);
  }

  @Test
  public void theProjectListOfAnEmptyProjectPageYearAnswers500() {
    this.addParameter("crp_project_page", "true");
    this.addParameter("project_website_year_value", "");
    this.webPageList.add(this.project);

    Translated translated = this.harness.propagate(() -> this.controller.findAllProjectPage(ENTITY));

    this.assertAnsweredAsUnusableProjectPageYear(translated);
    assertTrue(translated.exception.getCause() instanceof NumberFormatException);
  }

  @Test
  public void theProjectListOfAProjectPageYearThatIsNotANumberAnswers500AndKeepsTheValueOutOfTheAnswer() {
    this.addParameter("crp_project_page", "true");
    this.addParameter("project_website_year_value", "twenty");
    this.webPageList.add(this.project);
    this.webPageList.add(this.project);

    Translated translated = this.harness.propagate(() -> this.controller.findAllProjectPage(ENTITY));

    this.assertAnsweredAsUnusableProjectPageYear(translated);
    this.assertValueReachesOnlyTheLog(translated);
  }

  /**
   * A Global Unit whose project page year cannot be read is a server configuration error: one explicit exception,
   * answered 500 with the one log event ExceptionTranslator writes, and a message that names the Global Unit and the
   * parameter and holds nothing the client sent. No phase is looked up with a made-up year.
   */
  private void assertAnsweredAsUnusableProjectPageYear(Translated translated) {
    assertTrue(translated.exception instanceof IllegalStateException);
    assertEquals(UNUSABLE_YEAR, translated.exception.getMessage());
    assertEquals(500, translated.status);
    assertEquals(Level.ERROR, translated.loggedLevel);
    ErrorDTO body = (ErrorDTO) translated.body;
    assertEquals(ErrorConstants.ERR_INTERNAL_SERVER, body.getMessage());
    // the handler puts the exception message in the body, whichever field it uses
    String clientText = body.getMessage() + "|" + body.getDescription() + "|" + body.getSeverity();
    assertTrue(clientText, clientText.contains(UNUSABLE_YEAR));
    assertTrue("no phase is looked up with a made-up year", this.cycleLookups.isEmpty());
  }

  /**
   * The configured value is a database value, but not one for the client: it is only in the cause the log keeps.
   */
  private void assertValueReachesOnlyTheLog(Translated translated) {
    assertTrue(translated.exception.getCause() instanceof NumberFormatException);
    ErrorDTO body = (ErrorDTO) translated.body;
    String clientText = body.getMessage() + "|" + body.getDescription() + "|" + body.getSeverity();
    for (String internal : Arrays.asList("twenty", "NumberFormatException", "java.lang", "org.cgiar", "\tat ")) {
      assertFalse(internal + " reached the client: " + clientText, clientText.contains(internal));
    }
    ILoggingEvent event = this.harness.loggedEvents().get(0);
    assertNotNull("the log event keeps the exception", event.getThrowableProxy());
    assertEquals(NumberFormatException.class.getName(), event.getThrowableProxy().getCause().getClassName());
    assertTrue(event.getThrowableProxy().getCause().getMessage().contains("twenty"));
  }

  @Test
  public void aProjectWithCompleteRowsIsAnsweredAsBefore() {
    this.addRows(false);

    Project page = this.item.getProjectInformation(this.project, this.globalUnit);

    this.assertAnsweredWithTheCompleteRows(page);
  }

  @Test
  public void aProjectWithIncompleteRowsIsAnsweredWithTheCompleteOnes() {
    this.addRows(true);

    Project page = this.item.getProjectInformation(this.project, this.globalUnit);

    this.assertAnsweredWithTheCompleteRows(page);
  }

  /**
   * Gives the project one complete row of every kind the project page reads. With gaps it also has the rows that real
   * data holds: without status, without phase, without element or without Global Unit.
   */
  private void addRows(boolean gaps) {
    this.addParameter("crp_project_page", "true");
    this.addParameter("project_website_year_value", "2019");
    this.cycleAnswer = this.phase;

    // activities
    this.ongoing = activity(1, this.phase, 2);
    Set<Activity> activities = new HashSet<Activity>(Arrays.asList(this.ongoing));
    // partners and contributing outcomes
    this.partner = new ProjectPartner();
    this.partner.setId(1L);
    this.partner.setPhase(this.phase);
    Set<ProjectPartner> partners = new HashSet<ProjectPartner>(Arrays.asList(this.partner));
    this.outcome = new ProjectOutcome();
    this.outcome.setId(1L);
    this.outcome.setPhase(this.phase);
    Set<ProjectOutcome> outcomes = new HashSet<ProjectOutcome>(Arrays.asList(this.outcome));
    // outcome impact case reports
    this.completed = this.study(1, 3L);
    Set<ProjectExpectedStudy> studies = new HashSet<ProjectExpectedStudy>(Arrays.asList(this.completed));
    // deliverables
    this.deliverable = this.deliverable(1, 3);
    Set<Deliverable> deliverables = new HashSet<Deliverable>(Arrays.asList(this.deliverable));
    // flagships
    this.program = new CrpProgram();
    this.program.setId(1L);
    this.program.setProgramType(ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue());
    this.program.setCrp(this.globalUnit);
    Set<ProjectFocus> focuses = new HashSet<ProjectFocus>(Arrays.asList(this.focus(1, this.program)));
    // locations
    this.country = location(3, this.project, this.phase, element(30, 2L));
    List<ProjectLocation> locationRows = new ArrayList<ProjectLocation>(Arrays.asList(this.country));

    if (gaps) {
      // no status, and no phase
      activities.add(activity(2, this.phase, null));
      activities.add(activity(3, null, 3));
      // no phase
      ProjectPartner orphanPartner = new ProjectPartner();
      orphanPartner.setId(2L);
      partners.add(orphanPartner);
      ProjectOutcome orphanOutcome = new ProjectOutcome();
      orphanOutcome.setId(2L);
      outcomes.add(orphanOutcome);
      // no status
      studies.add(this.study(2, null));
      deliverables.add(this.deliverable(2, null));
      // a program that has no Global Unit
      CrpProgram orphanProgram = new CrpProgram();
      orphanProgram.setId(2L);
      orphanProgram.setProgramType(ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue());
      focuses.add(this.focus(2, orphanProgram));
      // no element, and an element that has no type
      locationRows.add(0, location(1, this.project, this.phase, null));
      locationRows.add(1, location(2, this.project, this.phase, element(20, null)));
    }

    this.project.setActivities(activities);
    this.project.setProjectPartners(partners);
    this.project.setProjectOutcomes(outcomes);
    this.project.setProjectExpectedStudies(studies);
    this.project.setDeliverables(deliverables);
    this.project.setProjectFocuses(focuses);
    RestErrorHarness.inject(this.item, "projectLocationManager",
      Fakes.fake(ProjectLocationManager.class).returning("findAll", locationRows).build());
    RestErrorHarness.inject(this.item, "locElementManager", Fakes.fake(LocElementManager.class)
      .on("getLocElementById", args -> element((Long) args[0], args[0].equals(20L) ? null : 2L)).build());
    RestErrorHarness.inject(this.item, "deliverableInfoManager", Fakes.fake(DeliverableInfoManager.class)
      .returning("isDeliverableSubcategoryIncludedWebsite", Boolean.TRUE).build());
  }

  private void assertAnsweredWithTheCompleteRows(Project page) {
    assertSame(this.project, page);
    assertEquals(Arrays.asList(this.ongoing), page.getProjectActivities());
    assertEquals(Arrays.asList(this.partner), page.getPartners());
    assertEquals(Arrays.asList(this.outcome), page.getOutcomes());
    assertEquals(Arrays.asList(this.completed), page.getExpectedStudies());
    assertEquals(Arrays.asList(this.deliverable), page.getProjectDeliverables());
    assertEquals(Arrays.asList(this.program), page.getFlagships());
    assertEquals(1, page.getLocations().size());
    assertSame(this.country, page.getLocations().get(0));
    assertTrue(page.getProjectRegions().isEmpty());
    this.harness.assertNothingLogged();
  }

  private Deliverable deliverable(long id, Integer status) {
    DeliverableInfo info = new DeliverableInfo();
    info.setPhase(this.phase);
    info.setStatus(status);
    Deliverable deliverable = new Deliverable();
    deliverable.setId(id);
    deliverable.setDeliverableInfos(new HashSet<DeliverableInfo>(Arrays.asList(info)));
    return deliverable;
  }

  private ProjectFocus focus(long id, CrpProgram program) {
    ProjectFocus focus = new ProjectFocus();
    focus.setId(id);
    focus.setPhase(this.phase);
    focus.setCrpProgram(program);
    return focus;
  }
}
