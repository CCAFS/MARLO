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
import org.cgiar.ccafs.marlo.data.manager.DeliverableManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectBudgetManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectComponentLessonManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPolicyManager;
import org.cgiar.ccafs.marlo.data.model.Activity;
import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.data.model.ExpectedStudyProject;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProgramType;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectBudget;
import org.cgiar.ccafs.marlo.data.model.ProjectClusterActivity;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudy;
import org.cgiar.ccafs.marlo.data.model.ProjectFocus;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovation;
import org.cgiar.ccafs.marlo.data.model.ProjectOutcome;
import org.cgiar.ccafs.marlo.data.model.ProjectPartner;
import org.cgiar.ccafs.marlo.data.model.ProjectPartnerPerson;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicy;
import org.cgiar.ccafs.marlo.data.model.ProjectStatusEnum;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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
  private final ProjectPolicyManager policyManager;
  private final ProjectInnovationManager projectInnovationManager;
  private final DeliverableManager deliverableManager;
  private final ProjectBudgetManager projectBudgetManager;


  private List<Phase> phasesbyGlobalUnitList;
  private Boolean hasW1W2Co;

  // XLS bytes
  private byte[] bytesXLSX;
  // Streams
  InputStream inputStream;
  Set<Long> projectsList = new HashSet<Long>();


  @Inject
  public ProjectsDeleteFieldsSummaryAction(APConfig config, GlobalUnitManager crpManager, PhaseManager phaseManager,
    CrpProgramManager crpProgramManager, ResourceManager resourceManager, ProjectManager projectManager,
    ProjectComponentLessonManager projectComponentLessonManager, ProjectPolicyManager policyManager,
    ProjectInnovationManager projectInnovationManager, DeliverableManager deliverableManager,
    ProjectBudgetManager projectBudgetManager) {
    super(config, crpManager, phaseManager, projectManager);
    this.crpProgramManager = crpProgramManager;
    this.resourceManager = resourceManager;
    this.projectComponentLessonManager = projectComponentLessonManager;
    this.phaseManager = phaseManager;
    this.policyManager = policyManager;
    this.projectInnovationManager = projectInnovationManager;
    this.deliverableManager = deliverableManager;
    this.projectBudgetManager = projectBudgetManager;
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

    this.getPhasesByGlobalUnit();

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
      LOG.error("Error generating ProjectsFieldsToRemoveAction " + e.getMessage());
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


  private TypedTableModel getBudgetsbyCoasTableModel(Project project) {
    DecimalFormat df = new DecimalFormat("###,###.00");
    TypedTableModel model = new TypedTableModel(
      new String[] {"description", "year", "w1w2", "w3", "bilateral", "center", "w1w2GenderPer", "w3GenderPer",
        "bilateralGenderPer", "centerGenderPer", "w1w2CoFinancing", "w1w2CoFinancingGenderPer", "hasW1W2Co",
        "totalW1w2", "totalW3", "totalBilateral", "totalCenter", "totalW1w2Gender", "totalW3Gender",
        "totalBilateralGender", "totalCenterGender", "totalW1w2Co", "totalW1w2CoGender"},
      new Class[] {String.class, Integer.class, String.class, String.class, String.class, String.class, String.class,
        String.class, String.class, String.class, String.class, String.class, Boolean.class, String.class, String.class,
        String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class},
      0);
    Boolean hasW1W2CoTemp = false;
    List<ProjectClusterActivity> coAs = new ArrayList<>();
    coAs = project.getProjectClusterActivities().stream()
      .filter(c -> c.isActive() && c.getPhase() != null && c.getPhase().equals(this.getSelectedPhase()))
      .collect(Collectors.toList());
    /*     */
    Double totalW1w2 = 0.0, totalW3 = 0.0, totalBilateral = 0.0, totalCenter = 0.0, totalW1w2Gender = 0.0,
      totalW3Gender = 0.0, totalBilateralGender = 0.0, totalCenterGender = 0.0, totalW1w2Co = 0.0,
      totalW1w2CoGender = 0.0;

    // get total budget per year
    if (coAs != null && coAs.size() > 0) {
      if (coAs.size() == 1 && this.hasW1W2Co) {
        hasW1W2CoTemp = true;
        // W1W2 no including co
        totalW1w2 = this.getTotalYear(this.getSelectedYear(), 1, project, 3);
        // W1W2 including co
        totalW1w2Co = this.getTotalYear(this.getSelectedYear(), 1, project, 2);

      } else {
        totalW1w2 = this.getTotalYear(this.getSelectedYear(), 1, project, 1);
      }
    } else {
      totalW1w2 = this.getTotalYear(this.getSelectedYear(), 1, project, 1);
    }

    totalW3 = this.getTotalYear(this.getSelectedYear(), 2, project, 1);
    totalBilateral = this.getTotalYear(this.getSelectedYear(), 3, project, 1);
    totalCenter = this.getTotalYear(this.getSelectedYear(), 4, project, 1);

    // get total gender per year
    for (ProjectPartner pp : project.getProjectPartners().stream()
      .filter(pp -> pp.isActive() && pp.getPhase() != null && pp.getPhase().equals(this.getSelectedPhase()))
      .collect(Collectors.toList())) {
      if (this.isPPA(pp.getInstitution())) {
        if (coAs.size() == 1 && this.hasW1W2Co) {
          totalW1w2CoGender += this.getTotalGender(pp.getInstitution().getId(), this.getSelectedYear(), 1, project, 2);
          totalW1w2Gender += this.getTotalGender(pp.getInstitution().getId(), this.getSelectedYear(), 1, project, 3);
        } else {
          totalW1w2Gender += this.getTotalGender(pp.getInstitution().getId(), this.getSelectedYear(), 1, project, 1);
        }
        totalW3Gender += this.getTotalGender(pp.getInstitution().getId(), this.getSelectedYear(), 2, project, 1);
        totalBilateralGender += this.getTotalGender(pp.getInstitution().getId(), this.getSelectedYear(), 3, project, 1);
        totalCenterGender += this.getTotalGender(pp.getInstitution().getId(), this.getSelectedYear(), 4, project, 1);
      }
    }
    /**/
    if (coAs.size() == 1) {
      ProjectClusterActivity projectClusterActivity = coAs.get(0);
      String description = projectClusterActivity.getCrpClusterOfActivity().getComposedName();
      String w1w2 = null;
      String w1w2GenderPer = null;
      String w3 = null;
      String w3GenderPer = null;
      String bilateral = null;
      String bilateralGenderPer = null;
      String center = null;
      String centerGenderPer = null;
      String w1w2CoFinancing = null;
      String w1w2CoFinancingGenderPer = null;

      // Get types of funding sources
      for (ProjectBudget pb : project
        .getProjectBudgets().stream().filter(pb -> pb.isActive() && pb.getYear() == this.getSelectedYear()
          && pb.getBudgetType() != null && pb.getPhase() != null && pb.getPhase().equals(this.getSelectedPhase()))
        .collect(Collectors.toList())) {

        if (pb.getBudgetType().getId() == 1 && pb.getFundingSource() != null
          && pb.getFundingSource().getFundingSourceInfo(this.getSelectedPhase()).getW1w2() != null
          && pb.getFundingSource().getFundingSourceInfo(this.getSelectedPhase()).getW1w2()) {
          w1w2CoFinancing = "100";
          w1w2CoFinancingGenderPer = "100";
        } else if (pb.getBudgetType().getId() == 1) {
          w1w2 = "100";
          w1w2GenderPer = "100";
        }


        if (pb.getBudgetType().getId() == 2) {
          w3 = "100";
          w3GenderPer = "100";
        }
        if (pb.getBudgetType().getId() == 3) {
          bilateral = "100";
          bilateralGenderPer = "100";
        }
        if (pb.getBudgetType().getId() == 4) {
          center = "100";
          centerGenderPer = "100";
        }
      }
      model.addRow(new Object[] {description, this.getSelectedYear(), w1w2, w3, bilateral, center, w1w2GenderPer,
        w3GenderPer, bilateralGenderPer, centerGenderPer, w1w2CoFinancing, w1w2CoFinancingGenderPer, hasW1W2CoTemp,
        df.format(totalW1w2), df.format(totalW3), df.format(totalBilateral), df.format(totalCenter),
        df.format(totalW1w2Gender), df.format(totalW3Gender), df.format(totalBilateralGender),
        df.format(totalCenterGender), df.format(totalW1w2Co), df.format(totalW1w2CoGender)});
    } else {
      for (ProjectClusterActivity clusterActivity : coAs) {
        String description = clusterActivity.getCrpClusterOfActivity().getComposedName();
        String w1w2Percentage = null;
        String w1w2GenderPer = null;
        String w3Percentage = null;
        String w3GenderPer = null;
        String bilateralPercentage = null;
        String bilateralGenderPer = null;
        String centerPercentage = null;
        String centerGenderPer = null;
        String w1w2CoFinancingPercentage = null;
        String w1w2CoFinancingGenderPer = null;
        // budget
        Double w1w2 = 0.0;
        Double w3 = 0.0;
        Double bilateral = 0.0;
        Double center = 0.0;
        Double w1w2Gender = 0.0;
        Double w3Gender = 0.0;
        Double bilateralGender = 0.0;
        Double centerGender = 0.0;


        /*
         * ProjectBudgetsCluserActvity w1w2pb =
         * this.getBudgetbyCoa(clusterActivity.getCrpClusterOfActivity().getId(), this.getSelectedYear(), 1, project);
         * if (w1w2pb != null) {
         * w1w2Percentage = df.format(w1w2pb.getAmount());
         * if (w1w2pb.getGenderPercentage() != null) {
         * w1w2GenderPer = df.format(w1w2pb.getGenderPercentage());
         * w1w2Gender = (w1w2pb.getGenderPercentage() * totalW1w2Gender) / 100;
         * }
         * w1w2 = (w1w2pb.getAmount() * totalW1w2) / 100;
         * }
         * ProjectBudgetsCluserActvity w3pb =
         * this.getBudgetbyCoa(clusterActivity.getCrpClusterOfActivity().getId(), this.getSelectedYear(), 2, project);
         * ProjectBudgetsCluserActvity bilateralpb =
         * this.getBudgetbyCoa(clusterActivity.getCrpClusterOfActivity().getId(), this.getSelectedYear(), 3, project);
         * ProjectBudgetsCluserActvity centerpb =
         * this.getBudgetbyCoa(clusterActivity.getCrpClusterOfActivity().getId(), this.getSelectedYear(), 4, project);
         * if (w3pb != null) {
         * w3Percentage = df.format(w3pb.getAmount());
         * if (w3pb.getGenderPercentage() != null) {
         * w3GenderPer = df.format(w3pb.getGenderPercentage());
         * w3Gender = (w3pb.getGenderPercentage() * totalW3Gender) / 100;
         * }
         * w3 = (w3pb.getAmount() * totalW3) / 100;
         * }
         * if (bilateralpb != null) {
         * bilateralPercentage = df.format(bilateralpb.getAmount());
         * if (bilateralpb.getGenderPercentage() != null) {
         * bilateralGenderPer = df.format(bilateralpb.getGenderPercentage());
         * bilateralGender = (bilateralpb.getGenderPercentage() * totalBilateralGender) / 100;
         * }
         * bilateral = (bilateralpb.getAmount() * totalBilateral) / 100;
         * }
         * if (centerpb != null) {
         * centerPercentage = df.format(centerpb.getAmount());
         * if (centerpb.getGenderPercentage() != null) {
         * centerGenderPer = df.format(centerpb.getGenderPercentage());
         * centerGender = (centerpb.getGenderPercentage() * totalCenterGender) / 100;
         * }
         * center = (centerpb.getAmount() * totalCenter) / 100;
         * }
         */
        model.addRow(new Object[] {description, this.getSelectedYear(), w1w2Percentage, w3Percentage,
          bilateralPercentage, centerPercentage, w1w2GenderPer, w3GenderPer, bilateralGenderPer, centerGenderPer,
          w1w2CoFinancingPercentage, w1w2CoFinancingGenderPer, hasW1W2CoTemp, df.format(w1w2), df.format(w3),
          df.format(bilateral), df.format(center), df.format(w1w2Gender), df.format(w3Gender),
          df.format(bilateralGender), df.format(centerGender), null, null});
      }
    }
    return model;
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
    long selectedGlobalUnitID = this.getCurrentGlobalUnit().getId();
    phasesbyGlobalUnitList = null;
    if (selectedGlobalUnitID != -1) {

      // Get phases by Global Unit
      phasesbyGlobalUnitList =
        phaseManager.findAll().stream().filter(p -> p.getCrp().getId().longValue() == selectedGlobalUnitID
          && p.isActive() && p.getYear() > 2017 && p.getYear() < 2020).collect(Collectors.toList());

      if (phasesbyGlobalUnitList != null && !phasesbyGlobalUnitList.isEmpty()) {
        phasesbyGlobalUnitList.sort((p1, p2) -> p1.getStartDate().compareTo(p2.getStartDate()));
        // Build the list into a Map
      }
    }
  }

  private TypedTableModel getProjecsDetailsTableModel() {
    TypedTableModel model = new TypedTableModel(
      new String[] {"projectId", "projectTitle", "projectSummary", "status", "managementLiaison", "flagships",
        "regions", "institutionLeader", "projectLeader", "activitiesOnGoing", "expectedDeliverables", "outcomes",
        "expectedStudies", "phaseID", "crossCutting", "contactPerson", "genderAnalysis", "newPartnershipsPlanned",
        "projectComponentLesson", "genderDimenssion", "youthComponent", "repIndOrganization", "repIndOrganizationType",
        "phase", "license", "otherLicense"},
      new Class[] {Long.class, String.class, String.class, String.class, String.class, String.class, String.class,
        String.class, String.class, Integer.class, Integer.class, Integer.class, Integer.class, Long.class,
        String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class,
        String.class, String.class, String.class, String.class},
      0);
    // Status of projects
    String[] statuses = null;

    // Get projects with the status defined

    for (Phase phase : phasesbyGlobalUnitList) {
      List<Project> activeProjects = this.getActiveProjectsByPhase(phase, 0, statuses);
      for (Project project : activeProjects) {
        Long projectId = project.getId();
        String projectTitle = project.getProjectInfo().getTitle();
        String managementLiaison = null;
        String crossCutting = null;
        String projectSummary = null;
        String managementLiaisonContactPerson = null;
        String genderAnalysis = null;
        String newPartnershipsPlanned = null;
        String projectComponentLesson = null;
        String genderDimenssions = null;
        String youthComponent = null;
        String repIndOrganization = null;
        String contributionCRP = null;
        String license = null;
        String otherLicense = null;


        if (project.getProjectInfo().getSummary() != null && !project.getProjectInfo().getSummary().isEmpty()) {
          projectSummary = project.getProjectInfo().getSummary();
        }
        if (project.getProjectInfo().getLiaisonInstitution() != null) {
          managementLiaison = project.getProjectInfo().getLiaisonInstitution().getComposedName();

          managementLiaison = managementLiaison.replaceAll("<", "&lt;");
          managementLiaison = managementLiaison.replaceAll(">", "&gt;");
        }


        String flagships = null;
        // get Flagships related to the project sorted by acronym
        for (ProjectFocus projectFocuses : project.getProjectFocuses().stream()
          .filter(c -> c.isActive() && c.getPhase().equals(phase)
            && c.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue())
          .sorted((o1, o2) -> o1.getCrpProgram().getAcronym().compareTo(o2.getCrpProgram().getAcronym()))
          .collect(Collectors.toList())) {
          if (flagships == null || flagships.isEmpty()) {
            flagships = crpProgramManager.getCrpProgramById(projectFocuses.getCrpProgram().getId()).getAcronym();
          } else {
            flagships +=
              ", " + crpProgramManager.getCrpProgramById(projectFocuses.getCrpProgram().getId()).getAcronym();
          }
        }
        String regions = null;
        // If has regions, add the regions to regionsArrayList
        // Get Regions related to the project sorted by acronym
        if (this.hasProgramnsRegions()) {
          for (ProjectFocus projectFocuses : project.getProjectFocuses().stream()
            .filter(c -> c.isActive() && c.getPhase().equals(phase)
              && c.getCrpProgram().getProgramType() == ProgramType.REGIONAL_PROGRAM_TYPE.getValue())
            .sorted((c1, c2) -> c1.getCrpProgram().getAcronym().compareTo(c2.getCrpProgram().getAcronym()))
            .collect(Collectors.toList())) {
            if (regions == null || regions.isEmpty()) {
              regions = crpProgramManager.getCrpProgramById(projectFocuses.getCrpProgram().getId()).getAcronym();
            } else {
              regions +=
                ", " + crpProgramManager.getCrpProgramById(projectFocuses.getCrpProgram().getId()).getAcronym();
            }
          }
          if (project.getProjecInfoPhase(phase).getNoRegional() != null && project.getProjectInfo().getNoRegional()) {
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

        ProjectPartnerPerson projectLeader = project.getLeaderPersonDB(phase);
        if (projectLeader != null) {
          institutionLeader = projectLeader.getProjectPartner().getInstitution().getComposedName();
          projectLeaderName = projectLeader.getComposedName();
          projectLeaderName = projectLeaderName.replaceAll("<", "&lt;");
          projectLeaderName = projectLeaderName.replaceAll(">", "&gt;");
        }

        Set<Activity> activitiesSet = new HashSet();
        for (Activity activity : project.getActivities().stream()
          .sorted((d1, d2) -> Long.compare(d1.getId(), d2.getId()))
          .filter(
            a -> a.isActive() && (a.getActivityStatus() == 2) && a.getPhase() != null && a.getPhase().equals(phase))
          .collect(Collectors.toList())) {
          activitiesSet.add(activity);
        }
        Integer activitiesOnGoing = activitiesSet.size();
        Set<Deliverable> deliverablesSet = new HashSet();
        for (Deliverable deliverable : project.getDeliverables().stream()
          .sorted((d1, d2) -> Long.compare(d1.getId(), d2.getId()))
          .filter(d -> d.isActive() && d.getDeliverableInfo(phase) != null && d.getDeliverableInfo().isActive()
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
          .filter(c -> c.isActive() && c.getPhase() != null && c.getPhase().equals(phase))
          .collect(Collectors.toList())) {
          projectOutcomeSet.add(projectOutcome);
        }
        Integer outcomes = projectOutcomeSet.size();

        Set<ProjectExpectedStudy> projectExpectedStudySet = new HashSet();
        for (ProjectExpectedStudy projectExpectedStudy : project.getProjectExpectedStudies().stream()
          .filter(c -> c.isActive() && c.getPhase() != null && c.getPhase() == phase.getId())
          .collect(Collectors.toList())) {
          projectExpectedStudySet.add(projectExpectedStudy);
        }
        for (ExpectedStudyProject expectedStudyProject : project.getExpectedStudyProjects().stream()
          .filter(c -> c.isActive() && c.getProjectExpectedStudy().getPhase() == phase.getId())
          .collect(Collectors.toList())) {
          projectExpectedStudySet.add(expectedStudyProject.getProjectExpectedStudy());
        }
        Integer expectedStudies = projectExpectedStudySet.size();

        String status = "";
        project.setProjectInfo(project.getProjecInfoPhase(phase));
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
          if (crossCutting != null && crossCutting.length() > 0) {
            crossCutting += ", Capacity Development";
          } else {
            crossCutting += " Capacity Development";
          }
        }
        if (project.getProjectInfo().getCrossCuttingClimate() != null
          && project.getProjectInfo().getCrossCuttingClimate() == true) {
          if (crossCutting != null && crossCutting.length() > 0) {
            crossCutting += ", Climate Change";
          } else {
            crossCutting += " Climate Change";
          }
        }
        if (project.getProjectInfo().getCrossCuttingNa() != null
          && project.getProjectInfo().getCrossCuttingNa() == true) {
          if (crossCutting != null && crossCutting.length() > 0) {
            crossCutting += ", N/A";
          } else {
            crossCutting += " N/A";
          }
        }

        List<ProjectOutcome> projectOutcomes = new ArrayList<>();

        projectOutcomes = project.getProjectOutcomes().stream()
          .filter(c -> c.isActive() && c.getPhase() != null && c.getPhase().equals(phase)).collect(Collectors.toList());

        ProjectPolicy projectPolicy = new ProjectPolicy();
        List<ProjectPolicy> projectPolicyList = new ArrayList<>();
        projectPolicyList = policyManager.getProjectPolicyByPhase(phase).stream()
          .filter(po -> po.isActive() && po.getProjectPolicyInfo(phase) != null && po.getProject() == project)
          .collect(Collectors.toList());

        if (projectPolicyList != null && !projectPolicyList.isEmpty() && projectPolicyList.size() > 0) {
          projectPolicy = projectPolicyList.get(0);
        }

        ProjectInnovation projectInnovation = new ProjectInnovation();
        List<ProjectInnovation> projectInnovationList = new ArrayList<>();
        projectInnovationList = projectInnovationManager.findAll().stream()
          .filter(i -> i.isActive() && i.getProject() == project && i.getProjectInnovationInfo(phase) != null)
          .collect(Collectors.toList());

        if (projectInnovationList != null && !projectInnovationList.isEmpty() && projectInnovationList.size() > 0) {
          projectInnovation = projectInnovationList.get(0);
        }

        List<Deliverable> deliverableList = new ArrayList<>();
        deliverableList = deliverableManager.getDeliverablesByProjectAndPhase(phase.getId(), project.getId()).stream()
          .filter(d -> d.isActive() && d.getProject() == project).collect(Collectors.toList());

        // BudgetsbyCoasTableModel
        // this.getBudgetsbyCoasTableModel(project);

        // ***************//

        if (projectComponentLesson == null || projectComponentLesson.isEmpty()) {
          projectComponentLesson = "<Not Defined>";
        }
        if (repIndOrganization == null || repIndOrganization.isEmpty()) {
          repIndOrganization = "<Not Defined>";
        }
        if (contributionCRP == null || contributionCRP.isEmpty()) {
          contributionCRP = "<Not Defined>";
        }
        if (genderAnalysis == null || genderAnalysis.isEmpty()) {
          genderAnalysis = "<Not Defined>";
        }
        if (newPartnershipsPlanned == null || newPartnershipsPlanned.isEmpty()) {
          newPartnershipsPlanned = "<Not Defined>";
        }


        model.addRow(new Object[] {projectId, projectTitle, projectSummary, status, managementLiaison, flagships,
          regions, institutionLeader, projectLeaderName, activitiesOnGoing, expectedDeliverables, outcomes,
          expectedStudies, phase.getId(), crossCutting, managementLiaisonContactPerson, genderAnalysis,
          newPartnershipsPlanned, projectComponentLesson, genderDimenssions, youthComponent, repIndOrganization,
          contributionCRP, phase.getComposedName(), license, otherLicense});
      }
    }
    return model;
  }

  /**
   * Get gender amount per institution, year and budet type
   * 
   * @param institutionId
   * @param year
   * @param budgetType
   * @return
   */
  public double getTotalGender(long institutionId, int year, long budgetType, Project project, Integer coFinancing) {

    List<ProjectBudget> budgets = projectBudgetManager.getByParameters(institutionId, year, budgetType, project.getId(),
      coFinancing, this.getSelectedPhase().getId());

    double totalGender = 0;
    if (budgets != null) {
      for (ProjectBudget projectBudget : budgets) {
        if (projectBudget.getPhase().equals(this.getSelectedPhase())) {
          double amount = projectBudget.getAmount() != null ? projectBudget.getAmount() : 0;
          double gender = projectBudget.getGenderPercentage() != null ? projectBudget.getGenderPercentage() : 0;

          totalGender = totalGender + (amount * (gender / 100));
        }
      }
    }

    return totalGender;
  }

  /**
   * Get the total budget per year and type
   * 
   * @param year current year in the platform
   * @param type budget type (W1W2/Bilateral/W3/Center funds)
   * @param coFinancing coFinancing 1: cofinancing+no cofinancing, 2: cofinancing 3: no cofinancing
   * @return total budget in the year and type passed as parameters
   */
  public double getTotalYear(int year, long type, Project project, Integer coFinancing) {
    double total = 0;

    switch (coFinancing) {
      case 1:
        for (ProjectBudget pb : project.getProjectBudgets().stream()
          .filter(pb -> pb.isActive() && pb.getYear() == year && pb.getBudgetType() != null
            && pb.getBudgetType().getId() == type && pb.getPhase() != null
            && pb.getPhase().equals(this.getSelectedPhase()))
          .collect(Collectors.toList())) {
          total = total + pb.getAmount();
        }
        break;
      case 2:
        for (ProjectBudget pb : project.getProjectBudgets().stream()
          .filter(pb -> pb.isActive() && pb.getYear() == year && pb.getBudgetType() != null
            && pb.getBudgetType().getId() == type && pb.getFundingSource() != null
            && pb.getFundingSource().getFundingSourceInfo(this.getSelectedPhase()).getW1w2() != null
            && pb.getFundingSource().getFundingSourceInfo(this.getSelectedPhase()).getW1w2().booleanValue() == true
            && pb.getPhase() != null && pb.getPhase().equals(this.getSelectedPhase()))
          .collect(Collectors.toList())) {
          total = total + pb.getAmount();
        }
        break;
      case 3:
        for (ProjectBudget pb : project.getProjectBudgets().stream()
          .filter(pb -> pb.isActive() && pb.getYear() == year && pb.getBudgetType() != null
            && pb.getBudgetType().getId() == type && pb.getFundingSource() != null
            && pb.getFundingSource().getFundingSourceInfo(this.getSelectedPhase()).getW1w2() != null
            && pb.getFundingSource().getFundingSourceInfo(this.getSelectedPhase()).getW1w2().booleanValue() == false
            && pb.getPhase() != null && pb.getPhase().equals(this.getSelectedPhase()))
          .collect(Collectors.toList())) {

          total = total + pb.getAmount();
        }
        break;

      default:
        break;
    }

    return total;
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
