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

import org.cgiar.ccafs.marlo.data.dto.ImpactPathwaysClusterDTO;
import org.cgiar.ccafs.marlo.data.manager.CrpClusterOfActivityManager;
import org.cgiar.ccafs.marlo.data.manager.CrpMilestoneManager;
import org.cgiar.ccafs.marlo.data.manager.CrpOutcomeSubIdoManager;
import org.cgiar.ccafs.marlo.data.manager.CrpProgramManager;
import org.cgiar.ccafs.marlo.data.manager.CrpProgramOutcomeManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.model.CrpAssumption;
import org.cgiar.ccafs.marlo.data.model.CrpClusterKeyOutput;
import org.cgiar.ccafs.marlo.data.model.CrpClusterKeyOutputOutcome;
import org.cgiar.ccafs.marlo.data.model.CrpClusterOfActivity;
import org.cgiar.ccafs.marlo.data.model.CrpMilestone;
import org.cgiar.ccafs.marlo.data.model.CrpOutcomeSubIdo;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.CrpProgramOutcome;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.ProgramType;
import org.cgiar.ccafs.marlo.data.model.SrfSubIdo;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.MilestoneComparators;

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
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.comparators.ComparatorChain;
import org.apache.commons.lang3.StringUtils;
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

/**************
 * @author German C. Martinez - CIAT/CCAFS
 **************/

public class ImpactPathwaysSummaryAction extends BaseSummariesAction implements Summary {

  /**
   * 
   */
  private static final long serialVersionUID = -8119126729090731665L;

  // Variables
  private static final Logger LOG = LoggerFactory.getLogger(ImpactPathwaysSummaryAction.class);
  private byte[] bytesXLSX;
  protected InputStream inputStream;
  private long startTime;
  private List<CrpClusterOfActivity> clusterofActivities = Collections.emptyList();
  private List<CrpProgramOutcome> outcomes = new ArrayList<>();

  // Managers
  private CrpProgramOutcomeManager crpProgramOutcomeManager;
  private CrpClusterOfActivityManager crpClusterOfActivityManager;
  private CrpMilestoneManager crpMilestoneManager;
  private CrpOutcomeSubIdoManager crpOutcomeSubIdoManager;
  private CrpProgramManager crpProgramManager;
  private GlobalUnitManager globalUnitManager;

  private final ResourceManager resourceManager;

  public ImpactPathwaysSummaryAction(APConfig config, GlobalUnitManager crpManager, PhaseManager phaseManager,
    ProjectManager projectManager, CrpProgramOutcomeManager crpProgramOutcomeManager, ResourceManager resourceManager,
    CrpMilestoneManager crpMilestoneManager, CrpOutcomeSubIdoManager crpOutcomeSubIdoManager,
    GlobalUnitManager globalUnitManager, CrpProgramManager crpProgramManager,
    CrpClusterOfActivityManager crpClusterOfActivityManager) {
    super(config, crpManager, phaseManager, projectManager);
    this.crpProgramOutcomeManager = crpProgramOutcomeManager;
    this.crpMilestoneManager = crpMilestoneManager;
    this.crpOutcomeSubIdoManager = crpOutcomeSubIdoManager;
    this.resourceManager = resourceManager;
    this.crpProgramManager = crpProgramManager;
    this.crpClusterOfActivityManager = crpClusterOfActivityManager;
    this.globalUnitManager = globalUnitManager;
  }

  /**
   * Method to add i8n parameters to masterReport in Pentaho
   * 
   * @param masterReport
   * @return masterReport with i8n parameters added
   */
  private MasterReport addi8nParameters(MasterReport masterReport) {
    masterReport.getParameterValues().put("i8nOutcomesTitle", this.getText("summaries.impactPathways.outcome.title"));
    masterReport.getParameterValues().put("i8nFlagship", this.getText("summaries.impactPathways.flagship"));
    masterReport.getParameterValues().put("i8nOutcomeId", this.getText("summaries.impactPathways.outcome.identifier"));
    masterReport.getParameterValues().put("i8nOutcomeName", this.getText("summaries.impactPathways.outcome.name"));
    masterReport.getParameterValues().put("i8nOutcomeTargetYear", this.getText("summaries.impactPathways.targetYear"));
    masterReport.getParameterValues().put("i8nOutcomeTargetUnit", this.getText("summaries.impactPathways.targetUnit"));
    masterReport.getParameterValues().put("i8nOutcomeTargetValue",
      this.getText("summaries.impactPathways.targetValue"));
    masterReport.getParameterValues().put("i8nSubIdosAndContribution",
      this.getText("summaries.impactPathways.outcome.subido"));

    masterReport.getParameterValues().put("i8nMilestonesTitle",
      this.getText("summaries.impactPathways.milestone.title"));
    masterReport.getParameterValues().put("i8nMilestoneId",
      this.getText("summaries.impactPathways.milestone.identifier"));
    masterReport.getParameterValues().put("i8nMilestoneName", this.getText("summaries.impactPathways.milestone.name"));
    masterReport.getParameterValues().put("i8nMilestoneTargetYear",
      this.getText("summaries.impactPathways.targetYear"));
    masterReport.getParameterValues().put("i8nMilestoneStatus",
      this.getText("summaries.impactPathways.milestone.status"));
    masterReport.getParameterValues().put("i8nMilestoneTargetUnit",
      this.getText("summaries.impactPathways.targetUnit"));
    masterReport.getParameterValues().put("i8nMilestoneTargetValue",
      this.getText("summaries.impactPathways.targetValue"));
    masterReport.getParameterValues().put("i8nMilestoneLevelOfChange",
      this.getText("summaries.impactPathways.milestone.level"));
    masterReport.getParameterValues().put("i8nMilestoneAssessment",
      this.getText("summaries.impactPathways.milestone.assessment"));
    masterReport.getParameterValues().put("i8nMilestoneMainRisk",
      this.getText("summaries.impactPathways.milestone.risk"));
    masterReport.getParameterValues().put("i8nMilestoneVerification",
      this.getText("summaries.impactPathways.milestone.means"));
    masterReport.getParameterValues().put("i8nMilestoneGender",
      this.getText("summaries.impactPathways.milestone.gender"));
    masterReport.getParameterValues().put("i8nMilestoneYouth",
      this.getText("summaries.impactPathways.milestone.youth"));
    masterReport.getParameterValues().put("i8nMilestoneCapDev",
      this.getText("summaries.impactPathways.milestone.capdev"));
    masterReport.getParameterValues().put("i8nMilestoneClimate",
      this.getText("summaries.impactPathways.milestone.climate"));
    masterReport.getParameterValues().put("i8nMilestoneProjects",
      this.getText("summaries.impactPathways.milestone.projects"));

    masterReport.getParameterValues().put("i8nClusterOfActivitiesTitle",
      this.getText("summaries.impactPathways.cluster.title"));
    masterReport.getParameterValues().put("i8nClusterId", this.getText("summaries.impactPathways.cluster.identifier"));
    masterReport.getParameterValues().put("i8nClusterOfActivityTitle",
      this.getText("summaries.impactPathways.cluster.name"));
    masterReport.getParameterValues().put("i8nClusterOfActivityLeaders",
      this.getText("summaries.impactPathways.cluster.leaders"));
    masterReport.getParameterValues().put("i8nKeyOutputStatement",
      this.getText("summaries.impactPathways.cluster.keyOutput.statement"));
    masterReport.getParameterValues().put("i8nKeyOutputContribution",
      this.getText("summaries.impactPathways.cluster.keyOutput.contribution"));
    masterReport.getParameterValues().put("i8nKeyOutputOutcomes",
      this.getText("summaries.impactPathways.cluster.keyOutput.outcomes"));

    return masterReport;
  }

  @Override
  public String execute() throws Exception {
    if (this.getSelectedPhase() == null) {
      return NOT_FOUND;
    }

    ByteArrayOutputStream os = new ByteArrayOutputStream();
    Resource reportResource;
    try {
      reportResource = resourceManager.createDirectly(this.getClass().getResource("/pentaho/crp/ImpactPathways.prpt"),
        MasterReport.class);

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
      this.fillSubreport((SubReport) hm.get("crp_outcomes"), "crp_outcomes");
      this.fillSubreport((SubReport) hm.get("crp_milestones"), "crp_milestones");
      this.fillSubreport((SubReport) hm.get("crp_cluster_of_activities"), "crp_cluster_of_activities");
      ExcelReportUtil.createXLSX(masterReport, os);
      bytesXLSX = os.toByteArray();
      os.close();
    } catch (Exception e) {
      LOG.error("Error generating ImpactPathwaysSummary " + e.getMessage());
      throw e;
    }
    // Calculate time of generation
    long stopTime = System.currentTimeMillis();
    stopTime = stopTime - startTime;
    LOG.info("Downloaded successfully: " + this.getFileName() + ". User: "
      + this.getCurrentUser().getComposedCompleteName() + ". CRP: " + this.getLoggedCrp().getAcronym() + ". Cycle: "
      + this.getSelectedPhase().getName() + ". Time to generate: " + stopTime + "ms.");

    return SUCCESS;
  }

  private void fillSubreport(SubReport subReport, String query) {
    CompoundDataFactory cdf = CompoundDataFactory.normalize(subReport.getDataFactory());
    TableDataFactory sdf = (TableDataFactory) cdf.getDataFactoryForQuery(query);
    TypedTableModel model = null;

    switch (query) {
      case "crp_outcomes":
        model = this.getCrpOutcomesTableModel();
        break;
      case "crp_milestones":
        model = this.getCrpMilestonesTableModel();
        break;
      case "crp_cluster_of_activities":
        model = this.getCrpClusterOfActivitiesTableModel();
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

  private TypedTableModel getCrpClusterOfActivitiesTableModel() {
    TypedTableModel model = new TypedTableModel(
      new String[] {"flagship", "clusterIdentifier", "clusterTitle", "clusterLeaders", "keyOutputStatement",
        "keyOutputContribution", "outcomes"},
      new Class[] {String.class, String.class, String.class, String.class, String.class, BigDecimal.class,
        String.class});
    String flagship = "", clusterIdentifier = "", clusterTitle = "", clusterLeaders = "", keyOutputStatement = "",
      outcomesListString = "";
    BigDecimal keyOutputContribution = BigDecimal.ZERO;

    for (ImpactPathwaysClusterDTO clusterInfo : this.crpClusterOfActivityManager
      .getAllClusterInfoFromPhase(this.getSelectedPhase().getId())) {
      // StringBuffer clusterLeaders = new StringBuffer();

      // Flagship/module
      if (StringUtils.isNotBlank(clusterInfo.getFlagshipAcronym())) {
        flagship = clusterInfo.getFlagshipAcronym();
      } else {
        flagship = notDefined;
      }

      // Cluster identifier
      if (StringUtils.isNotBlank(clusterInfo.getClusterIdentifier())) {
        clusterIdentifier = clusterInfo.getClusterIdentifier();
      } else {
        clusterIdentifier = notProvided;
      }

      // Cluster title
      if (StringUtils.isNotBlank(clusterInfo.getClusterTitle())) {
        clusterTitle = clusterInfo.getClusterTitle();
      } else {
        clusterTitle = notProvided;
      }

      // Cluster leaders
      if (StringUtils.isNotBlank(clusterInfo.getClusterLeaders())) {
        clusterLeaders = clusterInfo.getClusterLeaders();
      } else {
        clusterLeaders = notProvided;
      }

      // Key outputs
      if (StringUtils.isNotBlank(clusterInfo.getKeyOutputStatement())) {
        keyOutputStatement = clusterInfo.getKeyOutputStatement();
      } else {
        keyOutputStatement = notProvided;
      }

      // Key output contribution
      if (clusterInfo.getKeyOutputContribution() != null
        && clusterInfo.getKeyOutputContribution().compareTo(BigDecimal.ZERO) >= 0) {
        keyOutputContribution = clusterInfo.getKeyOutputContribution();
      } else {
        // keyOutputContribution = notDefined;
      }

      // Outcomes linked to the key output
      if (StringUtils.isNotBlank(clusterInfo.getOutcomes())) {
        outcomesListString = clusterInfo.getOutcomes();
      } else {
        outcomesListString = notDefined;
      }

      model.addRow(new Object[] {flagship, clusterIdentifier, clusterTitle, clusterLeaders, keyOutputStatement,
        keyOutputContribution, outcomesListString});
    }

    return model;
  }

  private TypedTableModel getCrpMilestonesTableModel() {
    TypedTableModel model = new TypedTableModel(
      new String[] {"flagship", "outcomeComposedId", "outcomeName", "milestoneComposedId", "milestoneName",
        "milestoneTargetYear", "milestoneStatus", "milestoneTargetUnit", "milestoneTargetValue", "levelOfChange",
        "assessmentOfRisk", "mainRisk", "meansOfVerification", "markerGender", "markerYouth", "markerCapdev",
        "markerClimate", "projectMilestones"},
      new Class[] {String.class, String.class, String.class, String.class, String.class, String.class, String.class,
        String.class, BigDecimal.class, String.class, String.class, String.class, String.class, String.class,
        String.class, String.class, String.class, String.class});

    String flagship = "", outcomeComposedId = "", outcomeName = "", milestoneComposedId = "", milestoneName = "",
      milestoneStatus = "", milestoneTargetUnit = "", levelOfChange = "", assessmentOfRisk = "",
      meansOfVerification = "", markerGender = "", markerYouth = "", markerCapdev = "", markerClimate = "";
    BigDecimal milestoneTargetValue = BigDecimal.ZERO;
    StringBuffer milestoneTargetYear = null, mainRisk = null, projectMilestones = null;

    for (CrpProgramOutcome outcome : outcomes) {
      // Flagship/Module
      if (outcome.getCrpProgram() != null && outcome.getCrpProgram().getId() != null) {
        flagship = outcome.getCrpProgram().getAcronym();
      } else {
        flagship = notDefined;
      }

      // Outcome composedId
      if (StringUtils.isNotBlank(outcome.getComposeID())) {
        outcomeComposedId = outcome.getComposeID();
      } else {
        outcomeComposedId = notDefined;
      }

      // Outcome name
      if (StringUtils.isNotBlank(outcome.getDescription())) {
        outcomeName = StringUtils.trim(outcome.getDescription());
      } else {
        outcomeName = notProvided;
      }

      if (this.isNotEmpty(outcome.getMilestones())) {
        for (CrpMilestone milestone : outcome.getMilestones()) {
          milestoneTargetYear = new StringBuffer();
          mainRisk = new StringBuffer();
          projectMilestones = new StringBuffer();

          // Milestone composedId
          if (StringUtils.isNotBlank(milestone.getComposeID())) {
            milestoneComposedId = milestone.getComposeID();
          } else {
            milestoneComposedId = notDefined;
          }

          // Milestone name
          if (StringUtils.isNotBlank(milestone.getTitle())) {
            milestoneName = StringUtils.trim(milestone.getTitle());
          } else {
            milestoneName = notProvided;
          }

          // Milestone year
          if (milestone.getYear() != null && milestone.getYear().intValue() != -1) {
            milestoneTargetYear = milestoneTargetYear.append(milestone.getYear());
            if (milestone.getExtendedYear() != null
              && milestone.getExtendedYear().intValue() > milestone.getYear().intValue()) {
              milestoneTargetYear = milestoneTargetYear.append(" extended to ").append(milestone.getExtendedYear());
            }
          } else {
            milestoneTargetYear = milestoneTargetYear.append(notProvided);
          }

          // Milestone status
          if (milestone.getMilestonesStatus() != null && milestone.getMilestonesStatus().getId() != null) {
            milestoneStatus = milestone.getMilestonesStatus().getName();
          } else {
            milestoneStatus = notProvided;
          }

          // Milestone target unit
          if (milestone.getSrfTargetUnit() != null && milestone.getSrfTargetUnit().getId() != null) {
            milestoneTargetUnit = milestone.getSrfTargetUnit().getName();
          } else {
            milestoneTargetUnit = notProvided;
          }

          // Milestone target value
          if (milestone.getValue() != null) {
            milestoneTargetValue = milestone.getValue();
          } else {
            milestoneTargetValue = BigDecimal.ONE.negate();
          }

          // Milestone level or change
          if (milestone.getPowbIndFollowingMilestone() != null
            && milestone.getPowbIndFollowingMilestone().getId() != null) {
            levelOfChange = milestone.getPowbIndFollowingMilestone().getName();
          } else {
            levelOfChange = notProvided;
          }

          // Milestone assessment of risk, main risk and other risk
          if (milestone.getPowbIndAssesmentRisk() != null && milestone.getPowbIndAssesmentRisk().getId() != null) {
            assessmentOfRisk = milestone.getPowbIndAssesmentRisk().getName();
            if (milestone.getPowbIndAssesmentRisk().getId() > 1) {
              if (milestone.getPowbIndMilestoneRisk() != null && milestone.getPowbIndMilestoneRisk().getId() != null) {
                mainRisk = mainRisk.append(milestone.getPowbIndMilestoneRisk().getName());
                if (milestone.getPowbIndMilestoneRisk().getId().intValue() == 7
                  && StringUtils.isNotBlank(milestone.getPowbMilestoneOtherRisk())) {
                  mainRisk = mainRisk.append(": ").append(StringUtils.trim(milestone.getPowbMilestoneOtherRisk()));
                }
              } else {
                mainRisk = mainRisk.append(notProvided);
              }
            } else if (milestone.getPowbIndAssesmentRisk().getId() == 1) {
              mainRisk = mainRisk.append(notRequired);
            }
          } else {
            assessmentOfRisk = notProvided;
            mainRisk = mainRisk.append(notProvided);
          }

          // Milestone means of verification
          if (StringUtils.isNotBlank(milestone.getPowbMilestoneVerification())) {
            meansOfVerification = StringUtils.trim(milestone.getPowbMilestoneVerification());
          } else {
            meansOfVerification = notProvided;
          }

          // Milestone ccm: gender
          if (milestone.getGenderFocusLevel() != null && milestone.getGenderFocusLevel().getId() != null) {
            markerGender = milestone.getGenderFocusLevel().getPowbName();
          } else {
            markerGender = notProvided;
          }

          // Milestone ccm: youth
          if (milestone.getYouthFocusLevel() != null && milestone.getYouthFocusLevel().getId() != null) {
            markerYouth = milestone.getYouthFocusLevel().getPowbName();
          } else {
            markerYouth = notProvided;
          }

          // Milestone ccm: capdev
          if (milestone.getCapdevFocusLevel() != null && milestone.getCapdevFocusLevel().getId() != null) {
            markerCapdev = milestone.getCapdevFocusLevel().getPowbName();
          } else {
            markerCapdev = notProvided;
          }

          // Milestone ccm: climate change
          if (milestone.getClimateFocusLevel() != null && milestone.getClimateFocusLevel().getId() != null) {
            markerClimate = milestone.getClimateFocusLevel().getPowbName();
          } else {
            markerClimate = notProvided;
          }

          // Project Milestones
          if (this.isNotEmpty(milestone.getProjectMilestones())) {
            projectMilestones = projectMilestones.append(milestone.getProjectMilestones().stream()
              .filter(pm -> pm != null && pm.getId() != null && pm.getProjectOutcome() != null
                && pm.getProjectOutcome().getId() != null && pm.getProjectOutcome().getProject() != null
                && pm.getProjectOutcome().getProject().getId() != null)
              .map(pm -> pm.getProjectOutcome().getProject().getId()).distinct().map(m -> "P" + m.toString())
              .collect(Collectors.joining(", ")));
          } else {
            if (projectMilestones.length() == 0) {
              projectMilestones = projectMilestones.append(notDefined);
            }
          }

          model.addRow(new Object[] {flagship, outcomeComposedId, outcomeName, milestoneComposedId, milestoneName,
            milestoneTargetYear.toString(), milestoneStatus, milestoneTargetUnit, milestoneTargetValue.toPlainString(),
            levelOfChange, assessmentOfRisk, mainRisk.toString(), meansOfVerification, markerGender, markerYouth,
            markerCapdev, markerClimate, projectMilestones.toString()});
        }
      } else {
        milestoneComposedId = notDefined;
        milestoneName = notProvided;
        milestoneTargetYear = new StringBuffer(notProvided);
        milestoneStatus = notProvided;
        milestoneTargetUnit = notProvided;
        milestoneTargetValue = BigDecimal.ONE.negate();
        levelOfChange = notProvided;
        assessmentOfRisk = notProvided;
        mainRisk = new StringBuffer(notProvided);
        meansOfVerification = notProvided;
        markerGender = notProvided;
        markerYouth = notProvided;
        markerCapdev = notProvided;
        markerClimate = notProvided;
        projectMilestones = new StringBuffer(notDefined);

        model.addRow(new Object[] {flagship, outcomeComposedId, outcomeName, milestoneComposedId, milestoneName,
          milestoneTargetYear.toString(), milestoneStatus, milestoneTargetUnit, milestoneTargetValue, levelOfChange,
          assessmentOfRisk, mainRisk.toString(), meansOfVerification, markerGender, markerYouth, markerCapdev,
          markerClimate, projectMilestones.toString()});
      }
    }

    return model;
  }

  private TypedTableModel getCrpOutcomesTableModel() {
    TypedTableModel model = new TypedTableModel(
      new String[] {"flagship", "composedId", "outcomeName", "outcomeTargetYear", "outcomeTargetUnit",
        "outcomeTargetValue", "allSubidosAndContribution", "phaseId"},
      new Class[] {String.class, String.class, String.class, Long.class, String.class, String.class, String.class,
        Long.class});

    String flagship = "", composedId = "", outcomeName = "", outcomeTargetUnit = "";
    Long outcomeTargetYear = 0L, phaseId = 0L;
    StringBuffer allSubidosAndContribution = null, primarySubIdos = null;
    BigDecimal outcomeTargetValue = BigDecimal.ZERO;
    phaseId = Long.valueOf(this.getSelectedPhase().getYear());

    for (CrpProgramOutcome outcome : outcomes) {
      allSubidosAndContribution = new StringBuffer();
      primarySubIdos = new StringBuffer();

      // Outcome FP/Module
      if (outcome.getCrpProgram() != null && outcome.getCrpProgram().getId() != null) {
        flagship = outcome.getCrpProgram().getAcronym();
      } else {
        flagship = notDefined;
      }

      // Outcome composedId
      if (StringUtils.isNotBlank(outcome.getComposeID())) {
        composedId = outcome.getComposeID();
      } else {
        composedId = notDefined;
      }

      // Outcome name
      if (StringUtils.isNotBlank(outcome.getDescription())) {
        outcomeName = StringUtils.trim(outcome.getDescription());
      } else {
        outcomeName = notProvided;
      }

      // Outcome year
      if (outcome.getYear() != null) {
        outcomeTargetYear = Long.valueOf(outcome.getYear());
      } else {
        outcomeTargetYear = -1L;
      }

      // Outcome target unit
      if (outcome.getSrfTargetUnit() != null && outcome.getSrfTargetUnit().getId() != -1) {
        outcomeTargetUnit = outcome.getSrfTargetUnit().getName();
      } else {
        outcomeTargetUnit = notProvided;
      }

      // Outcome target value
      if (outcome.getSrfTargetUnit() != null && outcome.getSrfTargetUnit().getId() != -1) {
        outcomeTargetValue = outcome.getValue();
      } else {
        // outcomeTargetValue = notProvided;
      }

      // Sub-Idos and their contributions
      if (this.isNotEmpty(outcome.getSubIdos())) {
        for (CrpOutcomeSubIdo subIdo : outcome.getSubIdos()) {
          if (subIdo.getSrfSubIdo() != null && subIdo.getSrfSubIdo().getId() != -1) {
            if (subIdo.getPrimary() != null && subIdo.getPrimary()) {
              primarySubIdos = primarySubIdos.append("•").append("{Primary}").append(" ")
                .append(subIdo.getSrfSubIdo().getComposedName());
              if (subIdo.getContribution() != null) {
                primarySubIdos = primarySubIdos.append(": ").append(subIdo.getContribution()).append('%');
              }

              primarySubIdos = primarySubIdos.append("\r\n");
            } else {
              allSubidosAndContribution =
                allSubidosAndContribution.append("•").append(" ").append(subIdo.getSrfSubIdo().getComposedName());

              if (subIdo.getContribution() != null) {
                allSubidosAndContribution =
                  allSubidosAndContribution.append(": ").append(subIdo.getContribution()).append('%');
              }

              allSubidosAndContribution = allSubidosAndContribution.append("\r\n");
            }
          }
        }

        allSubidosAndContribution = primarySubIdos.append(allSubidosAndContribution);
      } else {
        allSubidosAndContribution = allSubidosAndContribution.append(notProvided);
      }

      model.addRow(new Object[] {flagship, composedId, outcomeName, outcomeTargetYear, outcomeTargetUnit,
        outcomeTargetValue, allSubidosAndContribution.toString(), phaseId});
    }

    return model;
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
    fileName.append("ImpactPathwaysSummary-");
    fileName.append(this.getLoggedCrp().getAcronym() + "-");
    fileName.append(this.getSelectedPhase().getName() + "_");
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
    TypedTableModel model = new TypedTableModel(new String[] {"center", "date", "cycle", "year"},
      new Class[] {String.class, String.class, String.class, Integer.class});

    model.addRow(new Object[] {center, date, this.getSelectedPhase().getName(), this.getSelectedYear()});
    return model;
  }

  private void loadClusterOfActivities() {

    List<CrpProgram> thisCrpPrograms = crpProgramManager.findAll().stream()
      .filter(c -> c != null && c.getId() != null && c.getCrp() != null && c.getCrp().getId() != null
        && c.getCrp().getId().longValue() == this.getCrpID())
      .sorted(Comparator.comparing(CrpProgram::getAcronym)).collect(Collectors.toList());
    thisCrpPrograms.forEach(program -> clusterofActivities.addAll(program.getCrpClusterOfActivities().stream()
      .filter(c -> c.isActive() && c.getPhase().equals(this.getSelectedPhase()))
      .sorted(Comparator.comparing(CrpClusterOfActivity::getIdentifier)).collect(Collectors.toList())));
    for (CrpClusterOfActivity crpClusterOfActivity : clusterofActivities) {
      crpClusterOfActivity.setLeaders(crpClusterOfActivity.getCrpClusterActivityLeaders().stream()
        .filter(c -> c.isActive()).collect(Collectors.toList()));
      crpClusterOfActivity.setKeyOutputs(
        crpClusterOfActivity.getCrpClusterKeyOutputs().stream().filter(c -> c.isActive()).collect(Collectors.toList()));
      for (CrpClusterKeyOutput crpClusterKeyOutput : crpClusterOfActivity.getKeyOutputs()) {
        crpClusterKeyOutput.setKeyOutputOutcomes(crpClusterKeyOutput.getCrpClusterKeyOutputOutcomes().stream()
          .filter(c -> c.isActive()).collect(Collectors.toList()));
        for (CrpClusterKeyOutputOutcome keyOuputOutcome : crpClusterKeyOutput.getKeyOutputOutcomes()) {
          keyOuputOutcome.setCrpProgramOutcome(
            crpProgramOutcomeManager.getCrpProgramOutcomeById(keyOuputOutcome.getCrpProgramOutcome().getId()));
        }
      }
    }

  }

  private void loadOutcomesAndMilestones() {
    GlobalUnit crp = this.globalUnitManager.getGlobalUnitById(this.getCrpID());
    List<CrpProgram> allPrograms =
      crp.getCrpPrograms().stream().filter(c -> c.getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue()
        && c.isActive() && c.getResearchArea() == null).collect(Collectors.toList());
    allPrograms.sort((p1, p2) -> p1.getAcronym().compareTo(p2.getAcronym()));

    allPrograms.forEach(program -> outcomes.addAll(program.getCrpProgramOutcomes().stream()
      .filter(c -> c != null && c.getId() != null && c.isActive() && c.getPhase().equals(this.getSelectedPhase()))
      .collect(Collectors.toList())));

    Comparator<CrpMilestone> milestoneComparator = new ComparatorChain<>(new MilestoneComparators.YearComparator())
      .thenComparing(new MilestoneComparators.ComposedIdComparator());

    for (CrpProgramOutcome crpProgramOutcome : outcomes) {

      crpProgramOutcome.setMilestones(crpProgramOutcome.getCrpMilestones().stream()
        .filter(c -> c != null && c.getId() != null && c.isActive()
          && ((c.getExtendedYear() != null && c.getExtendedYear().intValue() <= this.getSelectedPhase().getYear())
            || (c.getYear() != null && c.getYear().intValue() <= this.getSelectedPhase().getYear())))
        .sorted(milestoneComparator::compare).collect(Collectors.toList()));

      crpProgramOutcome.setIndicators(crpProgramOutcome.getCrpProgramOutcomeIndicators().stream()
        .filter(c -> c.isActive()).collect(Collectors.toList()));
      crpProgramOutcome.setSubIdos(crpProgramOutcome.getCrpOutcomeSubIdos().stream()
        .filter(c -> c != null && c.getId() != null && c.isActive()).collect(Collectors.toList()));

      for (CrpOutcomeSubIdo crpOutcomeSubIdo : crpProgramOutcome.getSubIdos()) {
        List<CrpAssumption> assumptions =
          crpOutcomeSubIdo.getCrpAssumptions().stream().filter(c -> c.isActive()).collect(Collectors.toList());
        crpOutcomeSubIdo.setAssumptions(assumptions);
        HashMap<Long, String> mapSubidos = new HashMap<>();
        try {
          for (SrfSubIdo srfSubIdo : crpOutcomeSubIdo.getSrfSubIdo().getSrfIdo().getSrfSubIdos().stream()
            .filter(c -> c.isActive()).collect(Collectors.toList())) {

            if (srfSubIdo.getSrfIdo().isIsCrossCutting()) {
              mapSubidos.put(srfSubIdo.getId(), "CrossCutting:" + srfSubIdo.getDescription());
            } else {
              mapSubidos.put(srfSubIdo.getId(), srfSubIdo.getDescription());
            }
          }
        } catch (Exception e) {

        }
        crpOutcomeSubIdo.setSubIdoList(mapSubidos);
      }
    }
  }

  @Override
  public void prepare() {
    this.setGeneralParameters();

    // Cluster of activities
    // this.loadClusterOfActivities();

    // Load outcomes and milestones
    this.loadOutcomesAndMilestones();

    // Calculate time to generate report
    startTime = System.currentTimeMillis();
    LOG.info(
      "Start report download: " + this.getFileName() + ". User: " + this.getCurrentUser().getComposedCompleteName()
        + ". CRP: " + this.getLoggedCrp().getAcronym() + ". Cycle: " + this.getSelectedPhase().getName());
  }
}
