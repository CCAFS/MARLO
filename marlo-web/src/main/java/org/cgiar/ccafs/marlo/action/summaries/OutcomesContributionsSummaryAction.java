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

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.CrpManager;
import org.cgiar.ccafs.marlo.data.manager.SrfTargetUnitManager;
import org.cgiar.ccafs.marlo.data.model.Crp;
import org.cgiar.ccafs.marlo.data.model.CrpMilestone;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.CrpProgramOutcome;
import org.cgiar.ccafs.marlo.data.model.CrpTargetUnit;
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
import java.util.Map;
import java.util.stream.Collectors;

import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.CompoundDataFactory;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.ItemBand;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportFooter;
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

public class OutcomesContributionsSummaryAction extends BaseAction implements Summary {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private static Logger LOG = LoggerFactory.getLogger(OutcomesContributionsSummaryAction.class);

  // Parameters
  private Crp loggedCrp;
  private long startTime;
  private int year;
  private String cycle;
  private HashMap<Long, String> targetUnitList;
  // Managers
  private CrpManager crpManager;
  private SrfTargetUnitManager srfTargetUnitManager;
  // XLSX bytes
  private byte[] bytesXLSX;
  // Streams
  InputStream inputStream;

  @Inject
  public OutcomesContributionsSummaryAction(APConfig config, CrpManager crpManager,
    SrfTargetUnitManager srfTargetUnitManager) {
    super(config);
    this.crpManager = crpManager;
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
        .createDirectly(this.getClass().getResource("/pentaho/OutcomesContributionsSummary.prpt"), MasterReport.class);
      MasterReport masterReport = (MasterReport) reportResource.getResource();
      String center = loggedCrp.getAcronym();
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
    LOG.info(
      "Downloaded successfully: " + this.getFileName() + ". User: " + this.getCurrentUser().getComposedCompleteName()
        + ". CRP: " + this.loggedCrp.getAcronym() + ". Cycle: " + cycle + ". Time to generate: " + stopTime + "ms.");
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

  /**
   * Get all subreports and store then in a hash map.
   * If it encounters a band, search subreports in the band
   * 
   * @param hm List to populate with subreports found
   * @param itemBand details section in pentaho
   */
  private void getAllSubreports(HashMap<String, Element> hm, ItemBand itemBand) {
    int elementCount = itemBand.getElementCount();
    for (int i = 0; i < elementCount; i++) {
      Element e = itemBand.getElement(i);
      // verify if the item is a SubReport
      if (e instanceof SubReport) {
        hm.put(e.getName(), e);
        if (((SubReport) e).getElementCount() != 0) {
          this.getAllSubreports(hm, ((SubReport) e).getItemBand());
          // If report footer is not null check for subreports
          if (((SubReport) e).getReportFooter().getElementCount() != 0) {
            this.getFooterSubreports(hm, ((SubReport) e).getReportFooter());
          }
        }
      }
      // If is a band, find the subreport if exist
      if (e instanceof Band) {
        this.getBandSubreports(hm, (Band) e);
      }
    }
  }

  /**
   * Get all subreports in the band.
   * If it encounters a band, search subreports in the band
   * 
   * @param hm
   * @param band
   */
  private void getBandSubreports(HashMap<String, Element> hm, Band band) {
    int elementCount = band.getElementCount();
    for (int i = 0; i < elementCount; i++) {
      Element e = band.getElement(i);
      if (e instanceof SubReport) {
        hm.put(e.getName(), e);
        // If report footer is not null check for subreports
        if (((SubReport) e).getReportFooter().getElementCount() != 0) {
          this.getFooterSubreports(hm, ((SubReport) e).getReportFooter());
        }
      }
      if (e instanceof Band) {
        this.getBandSubreports(hm, (Band) e);
      }
    }
  }


  @Override
  public int getContentLength() {
    return bytesXLSX.length;
  }

  @Override
  public String getContentType() {
    return "application/xlsx";
  }

  public String getCycle() {
    return cycle;
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
    fileName.append(this.year + "_");
    fileName.append(new SimpleDateFormat("yyyyMMdd-HHmm").format(new Date()));
    fileName.append(".xlsx");
    return fileName.toString();
  }

  private void getFooterSubreports(HashMap<String, Element> hm, ReportFooter reportFooter) {
    int elementCount = reportFooter.getElementCount();
    for (int i = 0; i < elementCount; i++) {
      Element e = reportFooter.getElement(i);
      if (e instanceof SubReport) {
        hm.put(e.getName(), e);
        if (((SubReport) e).getElementCount() != 0) {
          this.getAllSubreports(hm, ((SubReport) e).getItemBand());
        }
      }
      if (e instanceof Band) {
        this.getBandSubreports(hm, (Band) e);
      }
    }
  }

  @Override
  public InputStream getInputStream() {
    if (inputStream == null) {
      inputStream = new ByteArrayInputStream(bytesXLSX);
    }
    return inputStream;
  }

  public Crp getLoggedCrp() {
    return loggedCrp;
  }

  private TypedTableModel getMasterTableModel(String center, String date) {
    // Initialization of Model
    TypedTableModel model = new TypedTableModel(new String[] {"center", "date", "hasTargetUnit"},
      new Class[] {String.class, String.class, Boolean.class});
    Boolean hasTargetUnit = false;
    if (targetUnitList.size() > 0) {
      hasTargetUnit = true;
    }
    model.addRow(new Object[] {center, date, hasTargetUnit});
    return model;
  }

  private TypedTableModel getMilestonesOutcomesTableModel() {
    TypedTableModel model = new TypedTableModel(
      new String[] {"project_id", "flagship", "outcome", "project_url", "milestone", "expected_value", "expected_unit",
        "narrative_target", "title"},
      new Class[] {String.class, String.class, String.class, String.class, String.class, Long.class, String.class,
        String.class, String.class},
      0);
    for (CrpProgram crpProgram : loggedCrp.getCrpPrograms().stream().filter(cp -> cp.isActive())
      .collect(Collectors.toList())) {
      for (CrpProgramOutcome crpProgramOutcome : crpProgram.getCrpProgramOutcomes().stream()
        .filter(cpo -> cpo.isActive()).collect(Collectors.toList())) {
        for (CrpMilestone crpMilestone : crpProgramOutcome.getCrpMilestones().stream().filter(cm -> cm.isActive())
          .collect(Collectors.toList())) {
          for (ProjectMilestone projectMilestone : crpMilestone.getProjectMilestones().stream()
            .filter(pm -> pm.isActive()).collect(Collectors.toList())) {

            if (projectMilestone.getProjectOutcome().isActive()) {
              String projectId = "", title = "", flagship = "", outcome = "", projectUrl = "", milestone = "",
                expectedUnit = "", narrativeTarget = "";
              Long expectedValue = -1L;
              projectId = projectMilestone.getProjectOutcome().getProject().getId().toString();
              title = projectMilestone.getProjectOutcome().getProject().getTitle();
              flagship = projectMilestone.getProjectOutcome().getCrpProgramOutcome().getCrpProgram().getAcronym();
              outcome = projectMilestone.getProjectOutcome().getCrpProgramOutcome().getDescription();
              projectUrl = "P" + projectMilestone.getProjectOutcome().getProject().getId().toString();
              milestone = crpMilestone.getComposedName();
              expectedValue = projectMilestone.getExpectedValue();
              if (projectMilestone.getExpectedUnit() != null) {
                expectedUnit = projectMilestone.getExpectedUnit().getName();
              }
              narrativeTarget = projectMilestone.getNarrativeTarget();
              model.addRow(new Object[] {projectId, flagship, outcome, projectUrl, milestone, expectedValue,
                expectedUnit, narrativeTarget, title});
            }
          }

        }
      }
    }
    return model;
  }

  private TypedTableModel getProjectsOutcomesTableModel() {
    TypedTableModel model = new TypedTableModel(
      new String[] {"project_id", "title", "flagship", "outcome", "expected_value", "expected_unit",
        "expected_narrative", "project_url"},
      new Class[] {String.class, String.class, String.class, String.class, BigDecimal.class, String.class, String.class,
        String.class},
      0);
    for (Project project : loggedCrp.getProjects().stream().filter(p -> p.isActive() && p.getStatus().intValue() == 2)
      .collect(Collectors.toList())) {
      for (ProjectOutcome projectOutcome : project.getProjectOutcomes().stream().filter(po -> po.isActive())
        .collect(Collectors.toList())) {
        String projectId = "";
        String title = "";
        String flagship = "";
        String outcome = "";
        BigDecimal expectedValue = new BigDecimal(-1);
        String expectedUnit = "";
        String expectedNarrative = "";
        String projectUrl = "";
        projectId = project.getId().toString();
        projectUrl = "P" + project.getId().toString();
        title = project.getTitle();
        if (projectOutcome.getCrpProgramOutcome() != null) {
          if (projectOutcome.getCrpProgramOutcome().getCrpProgram() != null) {
            flagship = projectOutcome.getCrpProgramOutcome().getCrpProgram().getAcronym();
            outcome = projectOutcome.getCrpProgramOutcome().getDescription();
          }
          expectedValue = projectOutcome.getExpectedValue();
          if (projectOutcome.getExpectedUnit() != null) {
            expectedUnit = projectOutcome.getExpectedUnit().getName();
          }
          expectedNarrative = projectOutcome.getNarrativeTarget();
        }
        model.addRow(new Object[] {projectId, title, flagship, outcome, expectedValue, expectedUnit, expectedNarrative,
          projectUrl});
      }
    }
    return model;
  }

  public int getYear() {
    return year;
  }

  @Override
  public void prepare() {
    // Get loggerCrp
    try {
      loggedCrp = (Crp) this.getSession().get(APConstants.SESSION_CRP);
      loggedCrp = crpManager.getCrpById(loggedCrp.getId());
    } catch (Exception e) {
      LOG.error("Failed to get " + APConstants.SESSION_CRP + " parameter. Exception: " + e.getMessage());
    }
    // Get parameters from URL
    // Get year
    try {
      Map<String, Object> parameters = this.getParameters();
      year = Integer.parseInt((StringUtils.trim(((String[]) parameters.get(APConstants.YEAR_REQUEST))[0])));
    } catch (Exception e) {
      LOG.warn("Failed to get " + APConstants.YEAR_REQUEST
        + " parameter. Parameter will be set as CurrentCycleYear. Exception: " + e.getMessage());
      year = this.getCurrentCycleYear();
    }
    // Get cycle
    try {
      Map<String, Object> parameters = this.getParameters();
      cycle = (StringUtils.trim(((String[]) parameters.get(APConstants.CYCLE))[0]));
    } catch (Exception e) {
      LOG.warn("Failed to get " + APConstants.CYCLE + " parameter. Parameter will be set as CurrentCycle. Exception: "
        + e.getMessage());
      cycle = this.getCurrentCycle();
    }
    // Fill target unit list
    targetUnitList = new HashMap<>();
    if (srfTargetUnitManager.findAll() != null) {

      List<SrfTargetUnit> targetUnits = new ArrayList<>();

      List<CrpTargetUnit> crpTargetUnits = new ArrayList<>(
        loggedCrp.getCrpTargetUnits().stream().filter(tu -> tu.isActive()).collect(Collectors.toList()));

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
        + ". CRP: " + this.loggedCrp.getAcronym() + ". Cycle: " + cycle);
  }

  public void setCycle(String cycle) {
    this.cycle = cycle;
  }

  public void setLoggedCrp(Crp loggedCrp) {
    this.loggedCrp = loggedCrp;
  }

  public void setYear(int year) {
    this.year = year;
  }

}
