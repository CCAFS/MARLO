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
import org.cgiar.ccafs.marlo.data.manager.LocElementManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectBudgetManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPartnerManager;
import org.cgiar.ccafs.marlo.data.model.Activity;
import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.data.model.ExpectedStudyProject;
import org.cgiar.ccafs.marlo.data.model.LocElement;
import org.cgiar.ccafs.marlo.data.model.ProgramType;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectBudget;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudy;
import org.cgiar.ccafs.marlo.data.model.ProjectFocus;
import org.cgiar.ccafs.marlo.data.model.ProjectLocation;
import org.cgiar.ccafs.marlo.data.model.ProjectLocationElementType;
import org.cgiar.ccafs.marlo.data.model.ProjectOutcome;
import org.cgiar.ccafs.marlo.data.model.ProjectPartner;
import org.cgiar.ccafs.marlo.data.model.ProjectPartnerPerson;
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
import java.util.LinkedHashSet;
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

/**
 * @author Andrés Felipe Valencia Rivera. CCAFS
 */

/**
 * ProjectsSummaryAction:
 * 
 * @author avalencia - CCAFS
 */
public class ProjectsSummaryAction extends BaseSummariesAction implements Summary {

  private static final long serialVersionUID = 1L;

  private static Logger LOG = LoggerFactory.getLogger(ProjectsSummaryCompleteInfoAction.class);
  // Parameters
  private long startTime;

  // Managers
  private final CrpProgramManager crpProgramManager;
  private final ResourceManager resourceManager;
  private final LocElementManager locElementManager;
  private final ProjectBudgetManager projectBudgetManager;
  private final ProjectPartnerManager projectPartnerManager;


  // XLS bytes
  private byte[] bytesXLSX;

  private int yearSearched;
  // Streams
  InputStream inputStream;
  Set<Long> projectsList = new HashSet<Long>();


  @Inject
  public ProjectsSummaryAction(APConfig config, GlobalUnitManager crpManager, ProjectBudgetManager projectBudgetManager,
    PhaseManager phaseManager, CrpProgramManager crpProgramManager, ResourceManager resourceManager,
    ProjectManager projectManager, LocElementManager locElementManager, ProjectPartnerManager projectPartnerManager) {
    super(config, crpManager, phaseManager, projectManager);
    this.crpProgramManager = crpProgramManager;
    this.resourceManager = resourceManager;
    this.locElementManager = locElementManager;
    this.projectBudgetManager = projectBudgetManager;
    this.projectPartnerManager = projectPartnerManager;

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
    masterReport.getParameterValues().put("i8nTotalW1W2", this.getText("budgetPartner.w1w2"));
    masterReport.getParameterValues().put("i8nGrandTotalW3BilateralCenter",
      this.getText("budgetPartner.grandTotalW3BilateralCenter"));

    return masterReport;
  }

  @Override
  public String execute() throws Exception {

    if (this.getSelectedPhase() == null) {
      return NOT_FOUND;
    }

    ByteArrayOutputStream os = new ByteArrayOutputStream();
    try {
      Resource reportResource =
        resourceManager.createDirectly(this.getClass().getResource("/pentaho/crp/Projects.prpt"), MasterReport.class);

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
    fileName.append("ProjectsSummary-");
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


  private TypedTableModel getProjecsDetailsTableModel() {
    TypedTableModel model = new TypedTableModel(
      new String[] {"projectId", "projectTitle", "projectSummary", "status", "managementLiaison", "flagships",
        "regions", "institutionLeader", "projectLeader", "activitiesOnGoing", "expectedDeliverables", "outcomes",
        "expectedStudies", "phaseID", "crossCutting", "type", "locations", "start_date", "end_date", "budgetw1w2",
        "totalw3bilateralcenter", "ppa", "global", "regional", "regionLoc", "countryLoc", "deliverables2017",
        "deliverables2018", "deliverables2019", "deliverables2020", "deliverables2021", "deliverables2022"},
      new Class[] {Long.class, String.class, String.class, String.class, String.class, String.class, String.class,
        String.class, String.class, Integer.class, Integer.class, Integer.class, Integer.class, Long.class,
        String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class,
        String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class,
        String.class, String.class},
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
      String locations = "";
      String startDate = "";
      String endDate = "";
      SimpleDateFormat formatter = new SimpleDateFormat("MMM yyyy");
      String ppa = "";
      String global = "";
      String regional = "";
      String regionLoc = "";
      String countryLoc = "";
      String provinceLoc = "";
      String districtLoc = "";
      String deliverables2017 = "";
      String deliverables2018 = "";
      String deliverables2019 = "";
      String deliverables2020 = "";
      String deliverables2021 = "";
      String deliverables2022 = "";


      if (project.getProjectInfo().getSummary() != null && !project.getProjectInfo().getSummary().isEmpty()) {
        projectSummary = project.getProjectInfo().getSummary();
      }

      if (project.getProjectInfo().getStartDate() != null) {
        startDate = formatter.format(project.getProjectInfo().getStartDate());
      }
      if (project.getProjectInfo().getEndDate() != null) {
        endDate = formatter.format(project.getProjectInfo().getEndDate());
      }
      if (project.getProjectInfo().getLiaisonInstitution() != null) {
        managementLiaison = project.getProjectInfo().getLiaisonInstitution().getComposedName();

        managementLiaison = managementLiaison.replaceAll("<", "&lt;");
        managementLiaison = managementLiaison.replaceAll(">", "&gt;");
      }


      // Get PPA Partners
      List<ProjectPartner> listPPA = new ArrayList<>();
      List<ProjectPartner> tempPartners = new ArrayList<>();
      tempPartners = new ArrayList<>(project.getProjectPartners().stream()
        .filter(c -> c.isActive() && c.getPhase().getId().equals(this.getSelectedPhase().getId()))
        .collect(Collectors.toList()));

      for (ProjectPartner partner : tempPartners) {
        if (partner.getInstitution().isPPA(this.getLoggedCrp().getId(), this.getSelectedPhase())) {
          listPPA.add(partner);
        }
      }

      String t = "";
      for (ProjectPartner projectPartner : listPPA) {
        if (!t.equals(projectPartner.getComposedName())) {
          t = projectPartner.getComposedName();
          if (ppa.isEmpty()) {
            ppa += projectPartner.getComposedName();
          } else {
            ppa += ", " + projectPartner.getComposedName();
          }

        } else {
          t = projectPartner.getComposedName();
        }
      }

      // Get type from funding sources
      String type = "";
      List<String> typeList = new ArrayList<String>();
      for (ProjectBudget projectBudget : project
        .getProjectBudgets().stream().filter(pb -> pb.isActive() && pb.getYear() == this.getSelectedYear()
          && pb.getFundingSource() != null && pb.getPhase() != null && pb.getPhase().equals(this.getSelectedPhase()))
        .collect(Collectors.toList())) {
        if (projectBudget.getFundingSource().getFundingSourceInfo(this.getSelectedPhase()) != null
          && projectBudget.getFundingSource().getFundingSourceInfo(this.getSelectedPhase()).getBudgetType() != null
          && projectBudget.getFundingSource().getFundingSourceInfo(this.getSelectedPhase()).getBudgetType()
            .getName() != null) {
          typeList.add(
            projectBudget.getFundingSource().getFundingSourceInfo(this.getSelectedPhase()).getBudgetType().getName());
        }
      }
      // Remove duplicates
      Set<String> s = new LinkedHashSet<String>(typeList);
      for (String typeString : s.stream().collect(Collectors.toList())) {
        if (type.isEmpty()) {
          type = typeString;
        } else {
          type += ", " + typeString;
        }
      }

      String flagships = null;
      // get Flagships related to the project sorted by acronym
      for (ProjectFocus projectFocuses : project.getProjectFocuses().stream()
        .filter(c -> c.isActive() && c.getPhase().equals(this.getSelectedPhase())
          && c.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue()
          && c.getCrpProgram().getCrp().isCenterType() == false)
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
        .filter(d -> d.isActive() && d.getDeliverableInfo(this.getActualPhase()) != null
          && d.getDeliverableInfo().isActive()
          && ((d.getDeliverableInfo().getStatus() == null && d.getDeliverableInfo().getYear() == this.getSelectedYear())
            || (d.getDeliverableInfo().getStatus() != null
              && d.getDeliverableInfo().getStatus().intValue() == Integer
                .parseInt(ProjectStatusEnum.Extended.getStatusId())
              && d.getDeliverableInfo().getNewExpectedYear() != null
              && d.getDeliverableInfo().getNewExpectedYear() == this.getSelectedYear())
            || (d.getDeliverableInfo().getStatus() != null && d.getDeliverableInfo().getYear() == this.getSelectedYear()
              && d.getDeliverableInfo().getStatus().intValue() == Integer
                .parseInt(ProjectStatusEnum.Ongoing.getStatusId()))))
        .collect(Collectors.toList())) {
        deliverablesSet.add(deliverable);
      }
      Integer expectedDeliverables = deliverablesSet.size();
      Set<Deliverable> deliverablesSet2020 = new HashSet();
      Set<Deliverable> deliverablesSet2021 = new HashSet();
      Set<Deliverable> deliverablesSet2022 = new HashSet();

      // Deliverables for years 2017, 2018, 2019, 2020 for actual phase
      for (yearSearched = 2017; yearSearched <= 2022; yearSearched++) {
        for (Deliverable deliverable : project.getDeliverables().stream()
          .sorted((d1, d2) -> Long.compare(d1.getId(), d2.getId()))
          .filter(d -> d.isActive() && d.getDeliverableInfo(this.getActualPhase()) != null
            && d.getDeliverableInfo().isActive()
            && ((d.getDeliverableInfo().getStatus() == null && d.getDeliverableInfo().getYear() == yearSearched)
              || (d.getDeliverableInfo().getStatus() != null
                && d.getDeliverableInfo().getStatus().intValue() == Integer
                  .parseInt(ProjectStatusEnum.Extended.getStatusId())
                && d.getDeliverableInfo().getNewExpectedYear() != null
                && d.getDeliverableInfo().getNewExpectedYear() == yearSearched)
              || (d.getDeliverableInfo().getStatus() != null && d.getDeliverableInfo().getYear() == yearSearched
                && d.getDeliverableInfo().getStatus().intValue() == Integer
                  .parseInt(ProjectStatusEnum.Ongoing.getStatusId()))))
          .collect(Collectors.toList())) {

          if (!deliverables2017.isEmpty() && yearSearched == 2017) {
            deliverables2017 += deliverable.getId() + ", ";
          } else {
            deliverables2017 += deliverable.getId() + ", ";
          }

          if (!deliverables2018.isEmpty() && yearSearched == 2018) {
            deliverables2018 += deliverable.getId() + ", ";
          } else {
            deliverables2018 += deliverable.getId() + ", ";
          }

          if (!deliverables2019.isEmpty() && yearSearched == 2019) {
            deliverables2019 += ", " + deliverable.getId() + ", ";
          } else {
            deliverables2019 += " " + deliverable.getId() + ", ";
          }

          if (yearSearched == 2020) {
            deliverablesSet2020.add(deliverable);
          }

          if (yearSearched == 2021) {
            deliverablesSet2021.add(deliverable);

          }

          if (yearSearched == 2021) {
            deliverablesSet2022.add(deliverable);
          }
        }
      }
      deliverables2020 = "" + deliverablesSet2020.size();
      deliverables2021 = "" + deliverablesSet2021.size();
      deliverables2022 = "" + deliverablesSet2022.size();

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

      // Project Location:

      if (project.getProjectInfo().getNoRegional() != null) {
        if (project.getProjectInfo().getNoRegional() == true) {
          regional = "Yes";
        }
        if (project.getProjectInfo().getNoRegional() == false) {
          regional = "No";
        }
      } else {
        regional = "<Not defined>";
      }

      if (project.getProjectInfo().getLocationGlobal() != null) {
        if (project.getProjectInfo().getLocationGlobal() == true) {
          global = "Yes";
        }
        if (project.getProjectInfo().getLocationGlobal() == false) {
          global = "No";
        }
      } else {
        global = "<Not defined>";
      }

      if (!project.getProjectLocations().isEmpty()) {
        // Get all selected and show it
        List<LocElement> locElementsAll = locElementManager.findAll();
        String lastLocTypeName = "";
        List<String> temp1 = new ArrayList<>();
        List<String> temp2 = new ArrayList<>();
        List<String> temp3 = new ArrayList<>();
        List<String> temp4 = new ArrayList<>();

        List<ProjectLocationElementType> locationTypes = new ArrayList<>();
        locationTypes = project.getProjectLocationElementTypes().stream()
          .filter(plt -> plt.getIsGlobal() && plt.getLocElementType().isActive()).collect(Collectors.toList());
        for (ProjectLocationElementType projectLocType : project.getProjectLocationElementTypes().stream()
          .filter(plt -> plt.getIsGlobal() && plt.getLocElementType().isActive()).collect(Collectors.toList())) {
          String locTypeName = projectLocType.getLocElementType().getName();
          String locElementID = projectLocType.getLocElementType().getId().toString();
          for (LocElement locElement : locElementsAll.stream()
            .filter(le -> le.isActive() && le.getLocElementType() != null
              && le.getLocElementType().getId() == projectLocType.getLocElementType().getId())
            .collect(Collectors.toList())) {

            String locName = null;
            if (locElement != null) {

              locName = locElement.getName();
            }

            switch (locElementID) {
              case "1":
                if (temp1 != null) {
                  if (!temp1.contains(locName)) {
                    if (regionLoc.isEmpty()) {
                      regionLoc = locName;
                    } else {
                      regionLoc += ", " + locName;
                    }
                    temp1.add(locName);
                  }
                }
                break;
              case "2":
                if (temp2 != null) {
                  if (!temp2.contains(locName)) {
                    if (countryLoc.isEmpty()) {
                      countryLoc = locName;
                    } else {
                      countryLoc += ", " + locName;
                    }
                    temp2.add(locName);
                  }
                }
                break;
              case "3":
                if (temp3 != null) {
                  if (!temp3.contains(locName)) {
                    if (provinceLoc.isEmpty()) {
                      provinceLoc = locName;
                    } else {
                      provinceLoc += ", " + locName;
                    }
                    temp3.add(locName);
                  }
                }
                break;
              case "4":
                if (temp4 != null) {
                  if (!temp4.contains(locName)) {
                    if (districtLoc.isEmpty()) {
                      districtLoc = locName;
                    } else {
                      districtLoc = ", " + locName;
                    }
                    temp4.add(locName);
                  }
                }
                break;
            }

            if (locations.isEmpty() || locations == null) {
              locations += locTypeName + ": " + locName;
            } else {
              if (locTypeName.equals(lastLocTypeName)) {
                locations += ", " + locName;
              } else {
                locations += ", " + locTypeName + ": " + locName;
              }
            }
            lastLocTypeName = locTypeName;
          }
        }
        for (ProjectLocation pl : project.getProjectLocations().stream()
          .filter(c -> c.isActive() && c.getPhase() != null && c.getPhase().equals(this.getSelectedPhase()))
          .collect(Collectors.toList())) {
          LocElement le = pl.getLocElement();
          String locTypeName = null;

          String locName = null;
          String locElementID = "";
          if (le != null) {
            if (le.getLocElementType() != null) {
              locTypeName = le.getLocElementType().getName();
              locElementID = le.getLocElementType().getId().toString();
            }
            locName = le.getName();
          }

          switch (locElementID) {
            case "1":
              if (temp1 != null) {
                if (!temp1.contains(locName)) {
                  if (regionLoc.isEmpty()) {
                    regionLoc = locName;
                  } else {
                    regionLoc += ", " + locName;
                  }
                  temp1.add(locName);
                }
              }
              break;
            case "2":
              if (temp2 != null) {
                if (!temp2.contains(locName)) {
                  if (countryLoc.isEmpty()) {
                    countryLoc = locName;
                  } else {
                    countryLoc += ", " + locName;
                  }
                  temp2.add(locName);
                }
              }
              break;
            case "3":
              if (temp3 != null) {
                if (!temp3.contains(locName)) {
                  if (provinceLoc.isEmpty()) {
                    provinceLoc = locName;
                  } else {
                    provinceLoc += ", " + locName;
                  }
                  temp3.add(locName);
                }
              }
              break;
            case "4":
              if (temp4 != null) {
                if (!temp4.contains(locName)) {
                  if (regionLoc.isEmpty()) {
                    districtLoc = locName;
                  } else {
                    districtLoc += ", " + locName;
                  }
                  temp4.add(locName);
                }
              }
              break;
          }

          if (locations.isEmpty() || locations == null) {
            locations += locTypeName + ": " + locName;
          } else {
            if (locTypeName.equals(lastLocTypeName)) {
              locations += ", " + locName;
            } else {
              locations += ", " + locTypeName + ": " + locName;
            }
          }
          lastLocTypeName = locTypeName;
        }
      }


      // Get ppaPartners of project
      // get w1w2 co
      boolean hasW1W2Co;
      try {
        hasW1W2Co = this.hasSpecificities(APConstants.CRP_FS_W1W2_COFINANCING);
      } catch (Exception e) {
        LOG.warn("Failed to get " + APConstants.CYCLE + " parameter. Parameter will be set as false. Exception: "
          + e.getMessage());
        hasW1W2Co = false;
      }

      String w1w2 = null;
      String w3 = null;
      String bilateral = null;
      String centerfunds = null;
      String w1w2CoFinancing = null;
      // Budget Total
      String grand_total = null;
      double grand_totald = 0.0;
      // Decimal format
      DecimalFormat myFormatter = new DecimalFormat("###,###");

      if (hasW1W2Co) {
        w1w2 = myFormatter.format(this.getTotalYear(this.getSelectedYear(), 1, project, 3));
        w1w2CoFinancing = myFormatter.format(this.getTotalYear(this.getSelectedYear(), 1, project, 2));
        // increment Budget Total with w1w2 cofinancing
        grand_totald += this.getTotalYear(this.getSelectedYear(), 1, project, 3)
          + this.getTotalYear(this.getSelectedYear(), 1, project, 2);
      } else {
        w1w2 = myFormatter.format(this.getTotalYear(this.getSelectedYear(), 1, project, 1));
        // increment Budget Total with w1w2
        grand_totald += this.getTotalYear(this.getSelectedYear(), 1, project, 1);
      }
      w3 = myFormatter.format(this.getTotalYear(this.getSelectedYear(), 2, project, 1));
      bilateral = myFormatter.format(this.getTotalYear(this.getSelectedYear(), 3, project, 1));
      centerfunds = myFormatter.format(this.getTotalYear(this.getSelectedYear(), 4, project, 1));
      // increment Budget Total with w3,bilateral and centerfunds
      grand_totald += this.getTotalYear(this.getSelectedYear(), 2, project, 1)
        + this.getTotalYear(this.getSelectedYear(), 3, project, 1)
        + this.getTotalYear(this.getSelectedYear(), 4, project, 1);
      grand_total = "$" + myFormatter.format(grand_totald);


      if (locations.isEmpty() || locations == null || locations.length() == 0) {
        locations = "<Not defined>";
      }

      if (type.isEmpty() || type == null || type.length() == 0) {
        type = "<Not defined>";
      }
      model.addRow(new Object[] {projectId, projectTitle, projectSummary, status, managementLiaison, flagships, regions,
        institutionLeader, projectLeaderName, activitiesOnGoing, expectedDeliverables, outcomes, expectedStudies,
        this.getSelectedPhase().getId(), crossCutting, type, locations, startDate, endDate, w1w2, bilateral, ppa,
        global, regional, regionLoc, countryLoc, deliverables2017, deliverables2018, deliverables2019, deliverables2020,
        deliverables2021, deliverables2022});
    }
    return model;
  }

  /**
   * Get total amount per institution year and type
   * 
   * @param institutionId
   * @param year current platform year
   * @param budgetType
   * @return String with the total amount.
   */
  public String getTotalAmount(long institutionId, int year, long budgetType, Long projectId, Integer coFinancing) {
    return projectBudgetManager.amountByBudgetType(institutionId, year, budgetType, projectId, coFinancing,
      this.getSelectedPhase().getId());
  }

  /**
   * Get gender amount per institution, year and budet type
   * 
   * @param institutionId
   * @param year
   * @param budgetType
   * @return
   */
  public double getTotalGender(long institutionId, int year, long budgetType, long projectID, Integer coFinancing) {

    List<ProjectBudget> budgets = projectBudgetManager.getByParameters(institutionId, year, budgetType, projectID,
      coFinancing, this.getSelectedPhase().getId());

    double totalGender = 0;
    if (budgets != null) {
      for (ProjectBudget projectBudget : budgets) {
        if (projectBudget.getPhase().equals(this.getSelectedPhase())) {
          double amount = projectBudget.getAmount() != null ? projectBudget.getAmount() : 0.0;
          double gender = projectBudget.getGenderPercentage() != null ? projectBudget.getGenderPercentage() : 0.0;

          totalGender = totalGender + (amount * (gender / 100));
        }
      }
    }

    return totalGender;
  }


  /**
   * Get total gender percentaje per institution, year and type
   * 
   * @param institutionId
   * @param year
   * @param budgetType
   * @return
   */
  public double getTotalGenderPer(long institutionId, int year, long budgetType, long projectId, Integer coFinancing) {

    String totalAmount = this.getTotalAmount(institutionId, year, budgetType, projectId, coFinancing);

    double dTotalAmount = Double.parseDouble(totalAmount);

    double totalGender = this.getTotalGender(institutionId, year, budgetType, projectId, coFinancing);

    if (dTotalAmount != 0) {
      return (totalGender * 100) / dTotalAmount;
    } else {
      return 0.0;
    }
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
