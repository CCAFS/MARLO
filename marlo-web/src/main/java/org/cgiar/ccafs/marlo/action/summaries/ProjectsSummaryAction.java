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
import org.cgiar.ccafs.marlo.data.model.Activity;
import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.data.model.GlobalUnitProject;
import org.cgiar.ccafs.marlo.data.model.ProgramType;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudy;
import org.cgiar.ccafs.marlo.data.model.ProjectFocus;
import org.cgiar.ccafs.marlo.data.model.ProjectOutcome;
import org.cgiar.ccafs.marlo.data.model.ProjectPartner;
import org.cgiar.ccafs.marlo.data.model.ProjectStatusEnum;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
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

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private static Logger LOG = LoggerFactory.getLogger(ProjectsSummaryAction.class);
  // Parameters
  private long startTime;

  // Managers
  private CrpProgramManager crpProgramManager;
  // XLS bytes
  private byte[] bytesXLSX;
  // Streams
  InputStream inputStream;
  Set<Long> projectsList = new HashSet<Long>();

  @Inject
  public ProjectsSummaryAction(APConfig config, GlobalUnitManager crpManager, PhaseManager phaseManager,
    CrpProgramManager crpProgramManager) {
    super(config, crpManager, phaseManager);
    this.crpProgramManager = crpProgramManager;
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

    return masterReport;
  }

  @Override
  public String execute() throws Exception {
    ClassicEngineBoot.getInstance().start();
    ByteArrayOutputStream os = new ByteArrayOutputStream();

    ResourceManager manager = new ResourceManager();
    manager.registerDefaults();
    try {
      Resource reportResource =
        manager.createDirectly(this.getClass().getResource("/pentaho/crp/Projects.prpt"), MasterReport.class);

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
    fileName.append("ProjectsSummary-");
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
      new String[] {"projectId", "projectTitle", "managementLiaison", "flagships", "regions", "institutionLeader",
        "projectLeader", "activitiesOnGoing", "expectedDeliverables", "outcomes", "expectedStudies", "phaseID"},
      new Class[] {Long.class, String.class, String.class, String.class, String.class, String.class, String.class,
        Integer.class, Integer.class, Integer.class, Integer.class, Long.class},
      0);

    for (GlobalUnitProject globalUnitProject : this.getLoggedCrp().getGlobalUnitProjects().stream()
      .filter(p -> p.isActive() && p.getProject() != null && p.getProject().isActive()
        && (p.getProject().getProjecInfoPhase(this.getSelectedPhase()) != null
          && p.getProject().getProjectInfo().getStatus().intValue() == Integer
            .parseInt(ProjectStatusEnum.Ongoing.getStatusId())
          || p.getProject().getProjecInfoPhase(this.getSelectedPhase()) != null && p.getProject().getProjectInfo()
            .getStatus().intValue() == Integer.parseInt(ProjectStatusEnum.Extended.getStatusId())))
      .sorted((p1, p2) -> p1.getId().compareTo(p2.getId())).collect(Collectors.toList())) {
      Long projectId = globalUnitProject.getProject().getId();
      String projectTitle = globalUnitProject.getProject().getProjectInfo().getTitle();
      String managementLiaison = null;
      if (globalUnitProject.getProject().getProjectInfo().getLiaisonInstitution() != null) {
        managementLiaison = globalUnitProject.getProject().getProjectInfo().getLiaisonInstitution().getComposedName();
        if (globalUnitProject.getProject().getProjectInfo().getLiaisonUser() != null) {
          managementLiaison +=
            " - " + globalUnitProject.getProject().getProjectInfo().getLiaisonUser().getComposedName();
        }
        managementLiaison = managementLiaison.replaceAll("<", "&lt;");
        managementLiaison = managementLiaison.replaceAll(">", "&gt;");
      }

      String flagships = null;
      // get Flagships related to the project sorted by acronym
      for (ProjectFocus projectFocuses : globalUnitProject.getProject().getProjectFocuses().stream()
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
        for (ProjectFocus projectFocuses : globalUnitProject.getProject().getProjectFocuses().stream()
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
        if (globalUnitProject.getProject().getProjecInfoPhase(this.getSelectedPhase()).getNoRegional() != null
          && globalUnitProject.getProject().getProjectInfo().getNoRegional()) {
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
      ProjectPartner projectLeader = globalUnitProject.getProject().getLeader(this.getSelectedPhase());
      if (projectLeader != null) {
        institutionLeader = projectLeader.getInstitution().getComposedName();
        projectLeaderName =
          projectLeader.getProjectPartnerPersons().stream().collect(Collectors.toList()).get(0).getComposedName();
        projectLeaderName = projectLeaderName.replaceAll("<", "&lt;");
        projectLeaderName = projectLeaderName.replaceAll(">", "&gt;");
      }


      Set<Activity> activitiesSet = new HashSet();
      for (Activity activity : globalUnitProject.getProject().getActivities().stream()
        .sorted((d1, d2) -> Long.compare(d1.getId(), d2.getId())).filter(a -> a.isActive()
          && (a.getActivityStatus() == 2) && a.getPhase() != null && a.getPhase().equals(this.getSelectedPhase()))
        .collect(Collectors.toList())) {
        activitiesSet.add(activity);
      }
      Integer activitiesOnGoing = activitiesSet.size();
      Set<Deliverable> deliverablesSet = new HashSet();
      for (Deliverable deliverable : globalUnitProject.getProject().getDeliverables().stream()
        .sorted((d1, d2) -> Long.compare(d1.getId(), d2.getId()))
        .filter(d -> d.isActive() && d.getDeliverableInfo(this.getSelectedPhase()) != null
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

      Set<ProjectOutcome> projectOutcomeSet = new HashSet();
      for (ProjectOutcome projectOutcome : globalUnitProject.getProject().getProjectOutcomes().stream()
        .filter(c -> c.isActive() && c.getPhase() != null && c.getPhase().equals(this.getSelectedPhase()))
        .collect(Collectors.toList())) {
        projectOutcomeSet.add(projectOutcome);
      }
      Integer outcomes = projectOutcomeSet.size();

      Set<ProjectExpectedStudy> projectExpectedStudySet = new HashSet();
      for (ProjectExpectedStudy projectExpectedStudy : globalUnitProject.getProject().getProjectExpectedStudies()
        .stream().filter(c -> c.isActive() && c.getPhase() != null && c.getPhase().equals(this.getSelectedPhase()))
        .collect(Collectors.toList())) {
        projectExpectedStudySet.add(projectExpectedStudy);
      }
      Integer expectedStudies = projectExpectedStudySet.size();

      model.addRow(new Object[] {projectId, projectTitle, managementLiaison, flagships, regions, institutionLeader,
        projectLeaderName, activitiesOnGoing, expectedDeliverables, outcomes, expectedStudies,
        this.getSelectedPhase().getId()});
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
