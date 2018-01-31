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
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.SrfTargetUnitManager;
import org.cgiar.ccafs.marlo.data.model.CrpMilestone;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.CrpProgramOutcome;
import org.cgiar.ccafs.marlo.data.model.CrpTargetUnit;
import org.cgiar.ccafs.marlo.data.model.GlobalUnitProject;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectMilestone;
import org.cgiar.ccafs.marlo.data.model.ProjectOutcome;
import org.cgiar.ccafs.marlo.data.model.SrfTargetUnit;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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

public class OutcomesContributionsSummaryAction extends BaseSummariesAction implements Summary {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private static Logger LOG = LoggerFactory.getLogger(OutcomesContributionsSummaryAction.class);

  // Parameters
  private long startTime;
  private HashMap<Long, String> targetUnitList;
  // Managers
  private SrfTargetUnitManager srfTargetUnitManager;
  // XLSX bytes
  private byte[] bytesXLSX;
  // Streams
  InputStream inputStream;

  @Inject
  public OutcomesContributionsSummaryAction(APConfig config, GlobalUnitManager crpManager,
    SrfTargetUnitManager srfTargetUnitManager, PhaseManager phaseManager) {
    super(config, crpManager, phaseManager);
    this.srfTargetUnitManager = srfTargetUnitManager;
  }

  /**
   * Method to add i8n parameters to masterReport in Pentaho
   * 
   * @param masterReport
   * @return masterReport with i8n parameters added
   */
  private MasterReport addi8nParameters(MasterReport masterReport) {
    masterReport.getParameterValues().put("i8nProjectId", this.getText("searchTerms.projectId"));
    masterReport.getParameterValues().put("i8nTitle", this.getText("project.title.readText"));
    masterReport.getParameterValues().put("i8nFlagship", this.getText("projectOtherContributions.flagship"));
    masterReport.getParameterValues().put("i8nOutcomeStatement", this.getText("outcome.statement.readText"));
    masterReport.getParameterValues().put("i8nExpectedValue", this.getText("projectOutcome.expectedValue"));
    masterReport.getParameterValues().put("i8nTargetUnit", this.getText("outcome.targetUnit"));
    masterReport.getParameterValues().put("i8nNarrativeTarget",
      this.getText("projectOutcome.narrativeTarget.readText"));
    masterReport.getParameterValues().put("i8nMilestoneStatement",
      this.getText("outcome.milestone.statement.readText"));
    masterReport.getParameterValues().put("i8nMilestoneExpectedValue",
      this.getText("projectOutcomeMilestone.expectedValue"));
    masterReport.getParameterValues().put("i8nMilestoneExpectedNarrative",
      this.getText("outcome.expectedNarrativeMilestone"));
    masterReport.getParameterValues().put("i8nOutcomesTitle",
      this.getText("summaries.outcomesContributions.titleOutcomes"));
    masterReport.getParameterValues().put("i8nMilestonesTitle",
      this.getText("summaries.outcomesContributions.titleMilestones"));
    masterReport.getParameterValues().put("i8nOutcomeIndicator", this.getText("outcome.inidicator.readText"));
    return masterReport;
  }

  @Override
  public String execute() throws Exception {
    ClassicEngineBoot.getInstance().start();
    ByteArrayOutputStream os = new ByteArrayOutputStream();
    ResourceManager manager = new ResourceManager();
    manager.registerDefaults();
    try {
      Resource reportResource = manager
        .createDirectly(this.getClass().getResource("/pentaho/crp/OutcomesContributions.prpt"), MasterReport.class);
      MasterReport masterReport = (MasterReport) reportResource.getResource();
      String center = this.getLoggedCrp().getAcronym();
      // Get datetime
      ZonedDateTime timezone = ZonedDateTime.now();
      DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-d 'at' HH:mm ");
      String zone = timezone.getOffset() + "";
      if (zone.equals("Z")) {
        zone = "+0";
      }
      String currentDate = timezone.format(format) + "(GMT" + zone + ")";
      // Set Main_Query
      CompoundDataFactory cdf = CompoundDataFactory.normalize(masterReport.getDataFactory());
      String masterQueryName = "main";
      TableDataFactory sdf = (TableDataFactory) cdf.getDataFactoryForQuery(masterQueryName);
      TypedTableModel model = this.getMasterTableModel(center, currentDate);
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
      this.fillSubreport((SubReport) hm.get("projects_outcomes"), "projects_outcomes");
      this.fillSubreport((SubReport) hm.get("milestone_projects_outcomes"), "milestone_projects_outcomes");
      ExcelReportUtil.createXLSX(masterReport, os);
      bytesXLSX = os.toByteArray();
      os.close();
    } catch (Exception e) {
      LOG.error("Error generating OutcomesContributions " + e.getMessage());
      throw e;
    }
    // Calculate time of generation
    long stopTime = System.currentTimeMillis();
    stopTime = stopTime - startTime;
    LOG.info("Downloaded successfully: " + this.getFileName() + ". User: "
      + this.getCurrentUser().getComposedCompleteName() + ". CRP: " + this.getLoggedCrp().getAcronym() + ". Cycle: "
      + this.getSelectedCycle() + ". Time to generate: " + stopTime + "ms.");
    return SUCCESS;
  }

  private void fillSubreport(SubReport subReport, String query) {
    CompoundDataFactory cdf = CompoundDataFactory.normalize(subReport.getDataFactory());
    TableDataFactory sdf = (TableDataFactory) cdf.getDataFactoryForQuery(query);
    TypedTableModel model = null;
    switch (query) {
      case "projects_outcomes":
        model = this.getProjectsOutcomesTableModel();
        break;
      case "milestone_projects_outcomes":
        model = this.getMilestonesOutcomesTableModel();
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
    fileName.append("ImpactPathWayContributionsSummary-");
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


  private TypedTableModel getMasterTableModel(String center, String date) {
    // Initialization of Model
    TypedTableModel model = new TypedTableModel(new String[] {"center", "date", "hasTargetUnit", "hasOutcomeIndicator"},
      new Class[] {String.class, String.class, Boolean.class, Boolean.class});
    Boolean hasTargetUnit = false;
    if (targetUnitList.size() > 0) {
      hasTargetUnit = true;
    }

    model
      .addRow(new Object[] {center, date, hasTargetUnit, this.hasSpecificities(APConstants.CRP_IP_OUTCOME_INDICATOR)});
    return model;
  }

  private TypedTableModel getMilestonesOutcomesTableModel() {
    TypedTableModel model = new TypedTableModel(
      new String[] {"project_id", "flagship", "outcome", "project_url", "milestone", "expected_value", "expected_unit",
        "narrative_target", "title", "outcomeIndicator"},
      new Class[] {String.class, String.class, String.class, String.class, String.class, Long.class, String.class,
        String.class, String.class, String.class},
      0);
    for (CrpProgram crpProgram : this.getLoggedCrp().getCrpPrograms().stream()
      .filter(cp -> cp.isActive() && cp.getCrp().equals(this.getLoggedCrp())).collect(Collectors.toList())) {
      for (CrpProgramOutcome crpProgramOutcome : crpProgram.getCrpProgramOutcomes().stream()
        .filter(cpo -> cpo.isActive() && cpo.getPhase().equals(this.getSelectedPhase())).collect(Collectors.toList())) {
        for (CrpMilestone crpMilestone : crpProgramOutcome.getCrpMilestones().stream().filter(cm -> cm.isActive())
          .collect(Collectors.toList())) {
          for (ProjectMilestone projectMilestone : crpMilestone.getProjectMilestones().stream()
            .filter(pm -> pm.isActive() && pm.getProjectOutcome().getPhase().equals(this.getSelectedPhase()))
            .collect(Collectors.toList())) {

            if (projectMilestone.getProjectOutcome().isActive()) {
              String projectId = "", title = "", flagship = "", outcome = "", projectUrl = "", milestone = "",
                expectedUnit = "", narrativeTarget = "", outcomeIndicator = null;
              Long expectedValue = -1L;
              projectId = projectMilestone.getProjectOutcome().getProject().getId().toString();
              if (projectMilestone.getProjectOutcome().getProject()
                .getProjecInfoPhase(this.getSelectedPhase()) != null) {
                title = projectMilestone.getProjectOutcome().getProject().getProjecInfoPhase(this.getSelectedPhase())
                  .getTitle();
              }
              flagship = projectMilestone.getProjectOutcome().getCrpProgramOutcome().getCrpProgram().getAcronym();
              outcome = projectMilestone.getProjectOutcome().getCrpProgramOutcome().getDescription();
              if (this.hasSpecificities(APConstants.CRP_IP_OUTCOME_INDICATOR)
                && projectMilestone.getProjectOutcome().getCrpProgramOutcome().getIndicator() != null
                && !projectMilestone.getProjectOutcome().getCrpProgramOutcome().getIndicator().isEmpty()) {
                outcomeIndicator = projectMilestone.getProjectOutcome().getCrpProgramOutcome().getIndicator();
              }
              projectUrl = "P" + projectMilestone.getProjectOutcome().getProject().getId().toString();
              milestone = crpMilestone.getComposedName();
              expectedValue = projectMilestone.getExpectedValue();
              if (projectMilestone.getExpectedUnit() != null) {
                expectedUnit = projectMilestone.getExpectedUnit().getName();
              }
              narrativeTarget = projectMilestone.getNarrativeTarget();
              model.addRow(new Object[] {projectId, flagship, outcome, projectUrl, milestone, expectedValue,
                expectedUnit, narrativeTarget, title, outcomeIndicator});
            }
          }

        }
      }
    }
    return model;
  }


  private TypedTableModel getProjectsOutcomesTableModel() {

    // Get all Global Unit Projects
    List<GlobalUnitProject> globalUnitProjects = new ArrayList<>(this.getLoggedCrp().getGlobalUnitProjects());
    List<Project> guProjects = new ArrayList<>();
    for (GlobalUnitProject globalUnitProject : globalUnitProjects) {
      guProjects.add(globalUnitProject.getProject());
    }

    TypedTableModel model = new TypedTableModel(
      new String[] {"project_id", "title", "flagship", "outcome", "expected_value", "expected_unit",
        "expected_narrative", "project_url", "outcomeIndicator"},
      new Class[] {String.class, String.class, String.class, String.class, BigDecimal.class, String.class, String.class,
        String.class, String.class},
      0);

    for (Project project : guProjects.stream().sorted((p1, p2) -> Long.compare(p1.getId(), p2.getId()))
      .filter(p -> p.isActive() && p.getProjecInfoPhase(this.getSelectedPhase()) != null
        && p.getProjecInfoPhase(this.getSelectedPhase()).getStatus().intValue() == 2)
      .collect(Collectors.toList())) {

      for (ProjectOutcome projectOutcome : project.getProjectOutcomes().stream()
        .sorted((po1, po2) -> Long.compare(po1.getId(), po2.getId()))
        .filter(po -> po.isActive() && po.getPhase() != null && po.getPhase().equals(this.getSelectedPhase()))
        .collect(Collectors.toList())) {
        String projectId = "";
        String title = "";
        String flagship = "";
        String outcome = "";
        String outcomeIndicator = null;
        BigDecimal expectedValue = new BigDecimal(-1);
        String expectedUnit = "";
        String expectedNarrative = "";
        String projectUrl = "";
        projectId = project.getId().toString();
        projectUrl = "P" + project.getId().toString();
        title = project.getProjecInfoPhase(this.getSelectedPhase()).getTitle();
        if (projectOutcome.getCrpProgramOutcome() != null) {
          if (projectOutcome.getCrpProgramOutcome().getCrpProgram() != null) {
            flagship = projectOutcome.getCrpProgramOutcome().getCrpProgram().getAcronym();
            outcome = projectOutcome.getCrpProgramOutcome().getDescription();
            if (this.hasSpecificities(APConstants.CRP_IP_OUTCOME_INDICATOR)
              && projectOutcome.getCrpProgramOutcome().getIndicator() != null
              && !projectOutcome.getCrpProgramOutcome().getIndicator().isEmpty()) {
              outcomeIndicator = projectOutcome.getCrpProgramOutcome().getIndicator();
            }
          }
          expectedValue = projectOutcome.getExpectedValue();
          if (projectOutcome.getExpectedUnit() != null) {
            expectedUnit = projectOutcome.getExpectedUnit().getName();
          }
          expectedNarrative = projectOutcome.getNarrativeTarget();
        }
        model.addRow(new Object[] {projectId, title, flagship, outcome, expectedValue, expectedUnit, expectedNarrative,
          projectUrl, outcomeIndicator});
      }
    }
    return model;
  }

  @Override
  public void prepare() {
    this.setGeneralParameters();
    // Fill target unit list
    targetUnitList = new HashMap<>();
    if (srfTargetUnitManager.findAll() != null) {

      List<SrfTargetUnit> targetUnits = new ArrayList<>();

      List<CrpTargetUnit> crpTargetUnits = new ArrayList<>(
        this.getLoggedCrp().getCrpTargetUnits().stream().filter(tu -> tu.isActive()).collect(Collectors.toList()));

      for (CrpTargetUnit crpTargetUnit : crpTargetUnits) {
        targetUnits.add(crpTargetUnit.getSrfTargetUnit());
      }

      Collections.sort(targetUnits,
        (tu1, tu2) -> tu1.getName().toLowerCase().trim().compareTo(tu2.getName().toLowerCase().trim()));

      for (SrfTargetUnit srfTargetUnit : targetUnits) {
        targetUnitList.put(srfTargetUnit.getId(), srfTargetUnit.getName());
      }
    }
    // Calculate time to generate report
    startTime = System.currentTimeMillis();
    LOG.info(
      "Start report download: " + this.getFileName() + ". User: " + this.getCurrentUser().getComposedCompleteName()
        + ". CRP: " + this.getLoggedCrp().getAcronym() + ". Cycle: " + this.getSelectedCycle());
  }

}
