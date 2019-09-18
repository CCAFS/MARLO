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

package org.cgiar.ccafs.marlo.action.summaries;

import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.CrpProgramManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectComponentLessonManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.model.Activity;
import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.data.model.ExpectedStudyProject;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProgramType;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectComponentLesson;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudy;
import org.cgiar.ccafs.marlo.data.model.ProjectFocus;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovation;
import org.cgiar.ccafs.marlo.data.model.ProjectOutcome;
import org.cgiar.ccafs.marlo.data.model.ProjectPartnerPerson;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicy;
import org.cgiar.ccafs.marlo.data.model.ProjectStatusEnum;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.pentaho.reporting.engine.classic.core.CompoundDataFactory;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.ItemBand;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.pentaho.reporting.engine.classic.core.modules.output.table.xls.ExcelReportUtil;
import org.pentaho.reporting.engine.classic.core.util.TypedTableModel;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProjectsDeleteFieldsSummaryAction extends BaseSummariesAction implements Summary {

  private static final long serialVersionUID = 1L;

  private static Logger LOG = LoggerFactory.getLogger(ProjectsDeleteFieldsSummaryAction.class);
  // Parameters
  private long startTime;

  // Managers
  private final CrpProgramManager crpProgramManager;
  private final ResourceManager resourceManager;
  private final ProjectComponentLessonManager projectComponentLessonManager;
  private final PhaseManager phaseManager;
  // XLS bytes
  private byte[] bytesXLSX;
  // Streams
  InputStream inputStream;
  Set<Long> projectsList = new HashSet<Long>();


  @Inject
  public ProjectsDeleteFieldsSummaryAction(APConfig config, GlobalUnitManager crpManager, PhaseManager phaseManager,
    CrpProgramManager crpProgramManager, ResourceManager resourceManager, ProjectManager projectManager,
    ProjectComponentLessonManager projectComponentLessonManager) {
    super(config, crpManager, phaseManager, projectManager);
    this.crpProgramManager = crpProgramManager;
    this.resourceManager = resourceManager;
    this.projectComponentLessonManager = projectComponentLessonManager;
    this.phaseManager = phaseManager;
  }


  /**
   * Method to add i8n parameters to masterReport in Pentaho
   * 
   * @param masterReport
   * @return masterReport with i8n parameters added
   */
  private MasterReport addi8nParameters(MasterReport masterReport) {
    masterReport.getParameterValues().put("i8nProjectId", this.getText("searchTerms.projectId"));
    masterReport.getParameterValues().put("i8nProjectTitle", this.getText("project.title.readText"));
    masterReport.getParameterValues().put("i8nProjectSummary", this.getText("project.summary"));
    masterReport.getParameterValues().put("i8nStatus", this.getText("project.status"));
    masterReport.getParameterValues().put("i8nManagementLiaison", this.getText("project.liaisonInstitution"));
    masterReport.getParameterValues().put("i8nFlagships", this.getText("project.Flagships"));
    masterReport.getParameterValues().put("i8nRegions", this.getText("project.Regions"));
    masterReport.getParameterValues().put("i8nInstitutionLeader", this.getText("summaries.oaprojects.leadInstitution"));
    masterReport.getParameterValues().put("i8nProjectLeader", this.getText("summaries.oaprojects.projectLeader"));
    masterReport.getParameterValues().put("i8nActivitiesAmount", this.getText("summaries.oaprojects.activitiesAmount"));
    masterReport.getParameterValues().put("i8nDeliverablesAmount",
      this.getText("summaries.oaprojects.deliverablesAmount"));
    masterReport.getParameterValues().put("i8nOutcomesAmount", this.getText("summaries.oaprojects.outcomesAmount"));
    masterReport.getParameterValues().put("i8nStudiesAmount", this.getText("summaries.oaprojects.studiesAmount"));
    masterReport.getParameterValues().put("i8nStudiesCrossCutting",
      this.getText("summaries.oaprojects.crossCuttingDimensions"));
    masterReport.getParameterValues().put("i8nContactPerson", "Management Liaison Contact Person");
    masterReport.getParameterValues().put("i8nGenderAnalysis", "Gender Analysis");
    masterReport.getParameterValues().put("i8nNewPartnershipsPlanned", "New Partnerships Planned");
    masterReport.getParameterValues().put("i8nGenderDimenssion", "GenderDimenssion");
    masterReport.getParameterValues().put("i8nYouthComponent", "Youth Component");
    masterReport.getParameterValues().put("i8nProjectComponentLesson", "Lesson");
    masterReport.getParameterValues().put("i8nRepIndOrganization", "Implementing Organization Type");
    masterReport.getParameterValues().put("i8nRepIndOrganizationType", "Contribution of CRP");
    return masterReport;
  }

  @Override
  public String execute() throws Exception {

    if (this.getSelectedPhase() == null) {
      return NOT_FOUND;
    }

    ByteArrayOutputStream os = new ByteArrayOutputStream();
    try {
      Resource reportResource = resourceManager
        .createDirectly(this.getClass().getResource("/pentaho/crp/ProjectsDeleteFields.prpt"), MasterReport.class);

      MasterReport masterReport = (MasterReport) reportResource.getResource();
      // Set Main_Query
      CompoundDataFactory cdf = CompoundDataFactory.normalize(masterReport.getDataFactory());
      String masterQueryName = "main";
      TableDataFactory sdf = (TableDataFactory) cdf.getDataFactoryForQuery(masterQueryName);
      TypedTableModel model = this.getMasterTableModel();
      sdf.addTable(masterQueryName, model);
      masterReport.setDataFactory(cdf);
      // Set i8n for pentaho
      masterReport = this.addi8nParameters(masterReport);
      // Get details band
      ItemBand masteritemBand = masterReport.getItemBand();
      // Create new empty subreport hash map
      HashMap<String, Element> hm = new HashMap<String, Element>();
      // method to get all the subreports in the prpt and store in the HashMap
      this.getAllSubreports(hm, masteritemBand);
      // Uncomment to see which Subreports are detecting the method getAllSubreports
      // System.out.println("Pentaho SubReports: " + hm);
      this.fillSubreport((SubReport) hm.get("details"), "details");
      ExcelReportUtil.createXLSX(masterReport, os);
      bytesXLSX = os.toByteArray();
      os.close();
    } catch (Exception e) {
      LOG.error("Error generating ProjectsSummaryAction " + e.getMessage());
      throw e;
    }
    // Calculate time of generation
    long stopTime = System.currentTimeMillis();
    stopTime = stopTime - startTime;
    LOG.info(
      "Downloaded successfully: " + this.getFileName() + ". User: " + this.getCurrentUser().getComposedCompleteName()
        + ". CRP: " + this.getLoggedCrp().getAcronym() + ". Time to generate: " + stopTime + "ms.");
    return SUCCESS;

  }

  private void fillSubreport(SubReport subReport, String query) {
    CompoundDataFactory cdf = CompoundDataFactory.normalize(subReport.getDataFactory());
    TableDataFactory sdf = (TableDataFactory) cdf.getDataFactoryForQuery(query);
    TypedTableModel model = null;
    switch (query) {
      case "details":
        model = this.getProjecsDetailsTableModel();
        break;

    }
    sdf.addTable(query, model);
    subReport.setDataFactory(cdf);
  }


  @Override
  public int getContentLength() {
    return bytesXLSX.length;
  }


  @Override
  public String getContentType() {
    return "application/xlsx";
  }

  @SuppressWarnings("unused")
  private File getFile(String fileName) {
    // Get file from resources folder
    ClassLoader classLoader = this.getClass().getClassLoader();
    File file = new File(classLoader.getResource(fileName).getFile());
    return file;
  }

  @Override
  public String getFileName() {
    StringBuffer fileName = new StringBuffer();
    fileName.append("ProjectsDeleteFieldsSummary-");
    fileName.append(this.getLoggedCrp().getAcronym() + "-");
    fileName.append(this.getSelectedCycle() + "-");
    fileName.append(this.getSelectedYear() + "_");
    fileName.append(new SimpleDateFormat("yyyyMMdd-HHmm").format(new Date()));
    fileName.append(".xlsx");

    return fileName.toString();

  }

  @Override
  public InputStream getInputStream() {
    if (inputStream == null) {
      inputStream = new ByteArrayInputStream(bytesXLSX);
    }
    return inputStream;
  }


  private TypedTableModel getMasterTableModel() {
    // Initialization of Model
    TypedTableModel model =
      new TypedTableModel(new String[] {"center", "date", "year", "regionalAvalaible", "showDescription", "cycle"},
        new Class[] {String.class, String.class, String.class, Boolean.class, Boolean.class, String.class});
    String center = this.getLoggedCrp().getAcronym();

    ZonedDateTime timezone = ZonedDateTime.now();
    DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-d 'at' HH:mm ");
    String zone = timezone.getOffset() + "";
    if (zone.equals("Z")) {
      zone = "+0";
    }
    String date = timezone.format(format) + "(GMT" + zone + ")";
    String year = this.getSelectedYear() + "";
    model.addRow(new Object[] {center, date, year, this.hasProgramnsRegions(),
      this.hasSpecificities(APConstants.CRP_REPORTS_DESCRIPTION), this.getSelectedCycle()});
    return model;
  }

  public void getPhasesByGlobalUnit() {
    // Move to global variables
    long selectedGlobalUnitID = 0l;
    List<Map<String, Object>> phasesbyGlobalUnit;
    phasesbyGlobalUnit = new ArrayList<Map<String, Object>>();

    if (selectedGlobalUnitID != -1) {

      // Get phases by Global Unit
      List<Phase> phasesbyGlobalUnitList = phaseManager.findAll().stream()
        .filter(p -> p.getCrp().getId().longValue() == selectedGlobalUnitID && p.isActive())
        .collect(Collectors.toList());

      if (phasesbyGlobalUnitList != null && !phasesbyGlobalUnitList.isEmpty()) {
        phasesbyGlobalUnitList.sort((p1, p2) -> p1.getStartDate().compareTo(p2.getStartDate()));
        // Build the list into a Map
        for (Phase phase : phasesbyGlobalUnitList) {
          try {
            Map<String, Object> phasestMap = new HashMap<String, Object>();
            phasestMap.put("id", phase.getId());
            phasestMap.put("name", phase.getName());
            phasestMap.put("description", phase.getDescription());
            phasestMap.put("year", phase.getYear());
            phasesbyGlobalUnit.add(phasestMap);
          } catch (Exception e) {
            // logger.error("Unable to add Phase to Phase list", e);
          }
        }
      }
    }
  }

  private TypedTableModel getProjecsDetailsTableModel() {
    TypedTableModel model = new TypedTableModel(
      new String[] {"projectId", "projectTitle", "projectSummary", "status", "managementLiaison", "flagships",
        "regions", "institutionLeader", "projectLeader", "activitiesOnGoing", "expectedDeliverables", "outcomes",
        "expectedStudies", "phaseID", "crossCutting", "contactPerson", "genderAnalysis", "newPartnershipsPlanned",
        "projectComponentLesson", "genderDimenssion", "youthComponent", "repIndOrganization", "repIndOrganizationType"},
      new Class[] {Long.class, String.class, String.class, String.class, String.class, String.class, String.class,
        String.class, String.class, Integer.class, Integer.class, Integer.class, Integer.class, Long.class,
        String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class,
        String.class},
      0);
    // Status of projects
    String[] statuses = null;

    // Get projects with the status defined
    List<Project> activeProjects = this.getActiveProjectsByPhase(this.getSelectedPhase(), 0, statuses);
    for (Project project : activeProjects) {
      Long projectId = project.getId();
      String projectTitle = project.getProjectInfo().getTitle();
      String managementLiaison = null;
      String crossCutting = "";
      String projectSummary = "";
      String managementLiaisonContactPerson = "";
      String genderAnalysis = "";
      String newPartnershipsPlanned = "";
      String projectComponentLesson = "";
      String genderDimenssions = "";
      String youthComponent = "";
      String repIndOrganization = "";
      String repIndOrganizationType = "";

      if (project.getProjectInfo().getSummary() != null && !project.getProjectInfo().getSummary().isEmpty()) {
        projectSummary = project.getProjectInfo().getSummary();
      }
      if (project.getProjectInfo().getLiaisonInstitution() != null) {
        managementLiaison = project.getProjectInfo().getLiaisonInstitution().getComposedName();
        if (project.getProjectInfo().getLiaisonUser() != null) {
          managementLiaison += " - " + project.getProjectInfo().getLiaisonUser().getComposedName();
        }
        managementLiaison = managementLiaison.replaceAll("<", "&lt;");
        managementLiaison = managementLiaison.replaceAll(">", "&gt;");
      }

      if (project.getProjectInfo().getLiaisonUser() != null
        && project.getProjectInfo().getLiaisonUser().getUser() != null) {
        if (project.getProjectInfo().getLiaisonUser().getUser().getComposedName() != null) {
          managementLiaisonContactPerson = project.getProjectInfo().getLiaisonUser().getUser().getComposedName();
        } else if (project.getProjectInfo().getLiaisonUser().getUser().getFirstName() != null
          && project.getProjectInfo().getLiaisonUser().getUser().getLastName() != null) {
          managementLiaisonContactPerson = project.getProjectInfo().getLiaisonUser().getUser().getFirstName() + " "
            + project.getProjectInfo().getLiaisonUser().getUser().getLastName();
        }
      }

      String flagships = null;
      // get Flagships related to the project sorted by acronym
      for (ProjectFocus projectFocuses : project.getProjectFocuses().stream()
        .filter(c -> c.isActive() && c.getPhase().equals(this.getSelectedPhase())
          && c.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue())
        .sorted((o1, o2) -> o1.getCrpProgram().getAcronym().compareTo(o2.getCrpProgram().getAcronym()))
        .collect(Collectors.toList())) {
        if (flagships == null || flagships.isEmpty()) {
          flagships = crpProgramManager.getCrpProgramById(projectFocuses.getCrpProgram().getId()).getAcronym();
        } else {
          flagships += ", " + crpProgramManager.getCrpProgramById(projectFocuses.getCrpProgram().getId()).getAcronym();
        }
      }
      String regions = null;
      // If has regions, add the regions to regionsArrayList
      // Get Regions related to the project sorted by acronym
      if (this.hasProgramnsRegions()) {
        for (ProjectFocus projectFocuses : project.getProjectFocuses().stream()
          .filter(c -> c.isActive() && c.getPhase().equals(this.getSelectedPhase())
            && c.getCrpProgram().getProgramType() == ProgramType.REGIONAL_PROGRAM_TYPE.getValue())
          .sorted((c1, c2) -> c1.getCrpProgram().getAcronym().compareTo(c2.getCrpProgram().getAcronym()))
          .collect(Collectors.toList())) {
          if (regions == null || regions.isEmpty()) {
            regions = crpProgramManager.getCrpProgramById(projectFocuses.getCrpProgram().getId()).getAcronym();
          } else {
            regions += ", " + crpProgramManager.getCrpProgramById(projectFocuses.getCrpProgram().getId()).getAcronym();
          }
        }
        if (project.getProjecInfoPhase(this.getSelectedPhase()).getNoRegional() != null
          && project.getProjectInfo().getNoRegional()) {
          if (regions != null && !regions.isEmpty()) {
            LOG.warn("Project is global and has regions selected");
          }
          regions = "Global";
        }
      } else {
        regions = null;
      }

      String institutionLeader = null;
      String projectLeaderName = null;

      ProjectPartnerPerson projectLeader = project.getLeaderPersonDB(this.getSelectedPhase());
      if (projectLeader != null) {
        institutionLeader = projectLeader.getProjectPartner().getInstitution().getComposedName();
        projectLeaderName = projectLeader.getComposedName();
        projectLeaderName = projectLeaderName.replaceAll("<", "&lt;");
        projectLeaderName = projectLeaderName.replaceAll(">", "&gt;");
      }

      Set<Activity> activitiesSet = new HashSet();
      for (Activity activity : project
        .getActivities().stream().sorted((d1, d2) -> Long.compare(d1.getId(), d2.getId())).filter(a -> a.isActive()
          && (a.getActivityStatus() == 2) && a.getPhase() != null && a.getPhase().equals(this.getSelectedPhase()))
        .collect(Collectors.toList())) {
        activitiesSet.add(activity);
      }
      Integer activitiesOnGoing = activitiesSet.size();
      Set<Deliverable> deliverablesSet = new HashSet();
      for (Deliverable deliverable : project.getDeliverables().stream()
        .sorted((d1, d2) -> Long.compare(d1.getId(), d2.getId()))
        .filter(
          d -> d.isActive() && d.getDeliverableInfo(this.getActualPhase()) != null && d.getDeliverableInfo().isActive()
            && ((d.getDeliverableInfo().getStatus() == null && d.getDeliverableInfo().getYear() == 2017)
              || (d.getDeliverableInfo().getStatus() != null
                && d.getDeliverableInfo().getStatus().intValue() == Integer
                  .parseInt(ProjectStatusEnum.Extended.getStatusId())
                && d.getDeliverableInfo().getNewExpectedYear() != null
                && d.getDeliverableInfo().getNewExpectedYear() == 2017)
              || (d.getDeliverableInfo().getStatus() != null && d.getDeliverableInfo().getYear() == 2017
                && d.getDeliverableInfo().getStatus().intValue() == Integer
                  .parseInt(ProjectStatusEnum.Ongoing.getStatusId()))))
        .collect(Collectors.toList())) {
        deliverablesSet.add(deliverable);
      }
      Integer expectedDeliverables = deliverablesSet.size();

      Set<ProjectOutcome> projectOutcomeSet = new HashSet();
      for (ProjectOutcome projectOutcome : project.getProjectOutcomes().stream()
        .filter(c -> c.isActive() && c.getPhase() != null && c.getPhase().equals(this.getSelectedPhase()))
        .collect(Collectors.toList())) {
        projectOutcomeSet.add(projectOutcome);
      }
      Integer outcomes = projectOutcomeSet.size();

      Set<ProjectExpectedStudy> projectExpectedStudySet = new HashSet();
      for (ProjectExpectedStudy projectExpectedStudy : project.getProjectExpectedStudies().stream()
        .filter(c -> c.isActive() && c.getPhase() != null && c.getPhase() == this.getActualPhase().getId())
        .collect(Collectors.toList())) {
        projectExpectedStudySet.add(projectExpectedStudy);
      }
      for (ExpectedStudyProject expectedStudyProject : project.getExpectedStudyProjects().stream()
        .filter(c -> c.isActive() && c.getProjectExpectedStudy().getPhase() == this.getActualPhase().getId())
        .collect(Collectors.toList())) {
        projectExpectedStudySet.add(expectedStudyProject.getProjectExpectedStudy());
      }
      Integer expectedStudies = projectExpectedStudySet.size();

      String status = "";
      project.setProjectInfo(project.getProjecInfoPhase(this.getSelectedPhase()));
      if (project.getProjectInfo() != null && project.getProjectInfo().getStatus() != null) {

        if (project.getProjectInfo().getStatus().intValue() == Integer
          .parseInt(ProjectStatusEnum.Ongoing.getStatusId())) {
          status = ProjectStatusEnum.Ongoing.getStatus();
        }

        if (project.getProjectInfo().getStatus().intValue() == Integer
          .parseInt(ProjectStatusEnum.Complete.getStatusId())) {
          status = ProjectStatusEnum.Complete.getStatus();
        }

        if (project.getProjectInfo().getStatus().intValue() == Integer
          .parseInt(ProjectStatusEnum.Cancelled.getStatusId())) {
          status = ProjectStatusEnum.Cancelled.getStatus();
        }

        if (project.getProjectInfo().getStatus().intValue() == Integer
          .parseInt(ProjectStatusEnum.Extended.getStatusId())) {
          status = ProjectStatusEnum.Extended.getStatus();
        }
      }

      // Cross cutting Dimensions

      if (project.getProjectInfo().getCrossCuttingCapacity() != null
        && project.getProjectInfo().getCrossCuttingCapacity() == true) {
        if (crossCutting.length() > 0) {
          crossCutting += ", Capacity Development";
        } else {
          crossCutting += " Capacity Development";
        }
      }
      if (project.getProjectInfo().getCrossCuttingClimate() != null
        && project.getProjectInfo().getCrossCuttingClimate() == true) {
        if (crossCutting.length() > 0) {
          crossCutting += ", Climate Change";
        } else {
          crossCutting += " Climate Change";
        }
      }
      if (project.getProjectInfo().getCrossCuttingNa() != null
        && project.getProjectInfo().getCrossCuttingNa() == true) {
        if (crossCutting.length() > 0) {
          crossCutting += ", N/A";
        } else {
          crossCutting += " N/A";
        }
      }
      if (project.getProjectInfo().getCrossCuttingGender() != null
        && project.getProjectInfo().getCrossCuttingGender() == true) {
        if (crossCutting.length() > 0) {
          crossCutting += ", Gender";
        } else {
          crossCutting += "Gender";
        }
      }
      if (project.getProjectInfo().getCrossCuttingYouth() != null
        && project.getProjectInfo().getCrossCuttingYouth() == true) {
        if (crossCutting.length() > 0) {
          crossCutting += ", Youth";
        } else {
          crossCutting += "Youth";
        }
      }

      if (project.getProjectInfo().getGenderAnalysis() != null) {
        genderAnalysis = project.getProjectInfo().getGenderAnalysis();
      }

      if (project.getProjectInfo().getNewPartnershipsPlanned() != null) {
        genderAnalysis = project.getProjectInfo().getNewPartnershipsPlanned();
      }

      List<ProjectComponentLesson> pcList = new ArrayList<>();

      if (project.getProjectComponentLessons() != null) {
        pcList = project.getProjectComponentLessons().stream()
          .filter(
            p -> p.isActive() && p.getProject().getId() == project.getId() && p.getPhase() == this.getActualPhase())
          .collect(Collectors.toList());
      }

      if (pcList != null && pcList.size() > 0) {
        if (pcList.get(0) != null && pcList.get(0).getLessons() != null) {
          projectComponentLesson = pcList.get(0).getLessons();
        }
      }

      List<ProjectOutcome> projectOutcomes = new ArrayList<>();

      projectOutcomes = project.getProjectOutcomes().stream()
        .filter(c -> c.isActive() && c.getPhase() != null && c.getPhase().equals(this.getSelectedPhase()))
        .collect(Collectors.toList());

      if (projectOutcomes != null && projectOutcomes.size() > 0) {
        if (projectOutcomes.get(0).getGenderDimenssion() != null) {
          genderDimenssions = projectOutcomes.get(0).getGenderDimenssion();
        }

        if (projectOutcomes.get(0).getYouthComponent() != null) {
          youthComponent = projectOutcomes.get(0).getYouthComponent();
        }
      }
      List<ProjectPolicy> projectPolicies = new ArrayList<>();

      projectPolicies = project.getProjectPolicies().stream()
        .filter(p -> p.isActive() && p.getProject().getId().equals(project.getId()) && p.getProjectPolicyInfo() != null
          && p.getProjectPolicyInfo().getRepIndOrganizationType() != null
          && p.getProjectPolicyInfo().getRepIndOrganizationType().getName() != null
          && p.getProjectPolicyInfo().getPhase() != null && this.getActualPhase() != null
          && p.getProjectPolicyInfo().getPhase() == this.getActualPhase())
        .collect(Collectors.toList());

      if (projectPolicies != null && projectPolicies.size() > 0) {
        if (projectPolicies.get(0).getProjectPolicyInfo(this.getActualPhase()) != null) {
          repIndOrganization =
            projectPolicies.get(0).getProjectPolicyInfo(this.getActualPhase()).getRepIndOrganizationType().getName();
        }
      }

      List<ProjectInnovation> projectInnovations = new ArrayList<>();
      projectInnovations = project.getProjectInnovations().stream()
        .filter(
          i -> i.isActive() && i.getProjectInnovationInfo() != null && i.getProjectInnovationInfo().getPhase() != null
            && i.getProjectInnovationInfo().getPhase() == this.getActualPhase()
            && i.getProjectInnovationOrganizations() != null)
        .collect(Collectors.toList());
      if (projectInnovations != null && projectInnovations.size() > 0) {
        repIndOrganizationType = projectInnovations.get(0).getProjectInnovationOrganizations().stream()
          .filter(o -> o.isActive() && o.getPhase() != null && o.getPhase().equals(this.getSelectedPhase())
            && o.getRepIndOrganizationType() != null && o.getRepIndOrganizationType().getName() != null)
          .collect(Collectors.toList()).get(0).getRepIndOrganizationType().getName();
      }


      model.addRow(new Object[] {projectId, projectTitle, projectSummary, status, managementLiaison, flagships, regions,
        institutionLeader, projectLeaderName, activitiesOnGoing, expectedDeliverables, outcomes, expectedStudies,
        this.getSelectedPhase().getId(), crossCutting, managementLiaisonContactPerson, genderAnalysis,
        newPartnershipsPlanned, projectComponentLesson, genderDimenssions, youthComponent, repIndOrganization,
        repIndOrganizationType});
    }
    return model;
  }


  @Override
  public void prepare() {
    this.setGeneralParameters();
    // Calculate time to generate report
    startTime = System.currentTimeMillis();
    LOG.info("Start report download: " + this.getFileName() + ". User: "
      + this.getCurrentUser().getComposedCompleteName() + ". CRP: " + this.getLoggedCrp().getAcronym());
  }

}
