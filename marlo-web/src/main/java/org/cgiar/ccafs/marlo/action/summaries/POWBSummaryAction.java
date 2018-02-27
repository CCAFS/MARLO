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
import org.cgiar.ccafs.marlo.data.manager.DeliverableManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.PowbExpectedCrpProgressManager;
import org.cgiar.ccafs.marlo.data.manager.PowbExpenditureAreasManager;
import org.cgiar.ccafs.marlo.data.manager.PowbSynthesisManager;
import org.cgiar.ccafs.marlo.data.model.CrpMilestone;
import org.cgiar.ccafs.marlo.data.model.CrpOutcomeSubIdo;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.CrpProgramOutcome;
import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.data.model.DeliverableInfo;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.PowbCollaborationGlobalUnit;
import org.cgiar.ccafs.marlo.data.model.PowbCrossCuttingDimension;
import org.cgiar.ccafs.marlo.data.model.PowbEvidence;
import org.cgiar.ccafs.marlo.data.model.PowbEvidencePlannedStudy;
import org.cgiar.ccafs.marlo.data.model.PowbExpectedCrpProgress;
import org.cgiar.ccafs.marlo.data.model.PowbExpenditureAreas;
import org.cgiar.ccafs.marlo.data.model.PowbFinancialExpenditure;
import org.cgiar.ccafs.marlo.data.model.PowbFinancialPlannedBudget;
import org.cgiar.ccafs.marlo.data.model.PowbMonitoringEvaluationLearningExercise;
import org.cgiar.ccafs.marlo.data.model.PowbSynthesis;
import org.cgiar.ccafs.marlo.data.model.PowbSynthesisCrpStaffingCategory;
import org.cgiar.ccafs.marlo.data.model.PowbSynthesisSectionStatusEnum;
import org.cgiar.ccafs.marlo.data.model.ProgramType;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectBudgetsFlagship;
import org.cgiar.ccafs.marlo.data.model.ProjectFocus;
import org.cgiar.ccafs.marlo.data.model.ProjectStatusEnum;
import org.cgiar.ccafs.marlo.data.model.dto.CrossCuttingDimensionTableDTO;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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
import org.pentaho.reporting.engine.classic.core.modules.output.table.rtf.RTFReportUtil;
import org.pentaho.reporting.engine.classic.core.util.TypedTableModel;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Andrés Felipe Valencia Rivera. CCAFS
 */

public class POWBSummaryAction extends BaseSummariesAction implements Summary {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private static Logger LOG = LoggerFactory.getLogger(POWBSummaryAction.class);

  // Parameters
  private int year;
  private long startTime;
  private List<PowbSynthesis> powbSynthesisList;
  private LiaisonInstitution pmuInstitution;
  private PowbSynthesis powbSynthesisPMU;
  private List<CrpProgram> flagships;
  private CrossCuttingDimensionTableDTO tableC;
  private List<DeliverableInfo> deliverableList;
  private List<PowbMonitoringEvaluationLearningExercise> flagshipExercises;
  private List<PowbEvidencePlannedStudy> flagshipPlannedList;

  // Managers
  private PowbExpectedCrpProgressManager powbExpectedCrpProgressManager;
  private DeliverableManager deliverableManager;
  private PowbExpenditureAreasManager powbExpenditureAreasManager;
  private PowbSynthesisManager powbSynthesisManager;

  // RTF bytes
  private byte[] bytesRTF;
  // Streams
  InputStream inputStream;

  @Inject
  public POWBSummaryAction(APConfig config, GlobalUnitManager crpManager, PhaseManager phaseManager,
    PowbExpectedCrpProgressManager powbExpectedCrpProgressManager, DeliverableManager deliverableManager,
    PowbExpenditureAreasManager powbExpenditureAreasManager, PowbSynthesisManager powbSynthesisManager) {
    super(config, crpManager, phaseManager);
    this.powbExpectedCrpProgressManager = powbExpectedCrpProgressManager;
    this.deliverableManager = deliverableManager;
    this.powbExpenditureAreasManager = powbExpenditureAreasManager;
    this.powbSynthesisManager = powbSynthesisManager;
  }


  /**
   * Method to add i8n parameters to masterReport in Pentaho
   * 
   * @param masterReport
   * @return masterReport with i8n parameters added
   */
  private MasterReport addi8nParameters(MasterReport masterReport) {
    // masterReport.getParameterValues().put("i8nParameterName", "i8nText"));
    masterReport.getParameterValues().put("i8nPlansCRPFlagshipTitle",
      this.getText("summaries.powb.synthesis.flagshipPlans.title"));

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
        manager.createDirectly(this.getClass().getResource("/pentaho/crp/POWBTemplate.prpt"), MasterReport.class);
      MasterReport masterReport = (MasterReport) reportResource.getResource();
      // Set Main_Query
      CompoundDataFactory cdf = CompoundDataFactory.normalize(masterReport.getDataFactory());
      String masterQueryName = "Main_Query";
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
      this.fillSubreport((SubReport) hm.get("ExpectedKeyResults"), "ExpectedKeyResults");
      this.fillSubreport((SubReport) hm.get("EffectivenessandEfficiency"), "EffectivenessandEfficiency");
      this.fillSubreport((SubReport) hm.get("CRPManagement"), "CRPManagement");
      // Table A
      this.fillSubreport((SubReport) hm.get("PlannedMilestones"), "PlannedMilestones");
      this.fillSubreport((SubReport) hm.get("TableAContent"), "TableAContent");
      // Table B
      this.fillSubreport((SubReport) hm.get("PlannedStudies"), "PlannedStudies");
      this.fillSubreport((SubReport) hm.get("TableBContent"), "TableBContent");
      // Table C
      this.fillSubreport((SubReport) hm.get("Crosscutting"), "Crosscutting");
      this.fillSubreport((SubReport) hm.get("TableCContent"), "TableCContent");
      // Table D
      this.fillSubreport((SubReport) hm.get("CRPStaffing"), "CRPStaffing");
      if (powbSynthesisPMU != null) {
        this.fillSubreport((SubReport) hm.get("TableDContent"), "TableDContent");
      }
      // Table E
      this.fillSubreport((SubReport) hm.get("CRPPlannedBudget"), "CRPPlannedBudget");
      if (powbSynthesisPMU != null) {
        this.fillSubreport((SubReport) hm.get("TableEContent"), "TableEContent");
      }
      // Table F
      if (powbSynthesisPMU != null) {
        this.fillSubreport((SubReport) hm.get("MainAreas"), "MainAreas");
      }

      // Table G
      this.fillSubreport((SubReport) hm.get("CGIARCollaborations"), "CGIARCollaborations");
      this.fillSubreport((SubReport) hm.get("TableGContent"), "TableGContent");
      // Table H
      this.fillSubreport((SubReport) hm.get("PlannedMonitoring"), "PlannedMonitoring");
      this.fillSubreport((SubReport) hm.get("TableHContent"), "TableHContent");

      RTFReportUtil.createRTF(masterReport, os);
      bytesRTF = os.toByteArray();
      os.close();
    } catch (Exception e) {
      LOG.error("Error generating POWB Summary " + e.getMessage());
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

  /**
   * @param subReport Subreport to fill
   * @param query Name of the query in Pentaho Report Data Set
   */
  private void fillSubreport(SubReport subReport, String query) {
    CompoundDataFactory cdf = CompoundDataFactory.normalize(subReport.getDataFactory());
    TableDataFactory sdf = (TableDataFactory) cdf.getDataFactoryForQuery(query);
    TypedTableModel model = null;
    switch (query) {
      case "ExpectedKeyResults":
        model = this.getExpectedKeyResultsTableModel();
        break;
      case "EffectivenessandEfficiency":
        model = this.getEffectivenessandEfficiencyTableModel();
        break;
      case "CRPManagement":
        model = this.getCRPManagementTableModel();
        break;
      // Table A
      case "PlannedMilestones":
        model = this.getPlannedMilestonesTableModel();
        break;
      case "TableAContent":
        model = this.getTableAContentTableModel();
        break;
      // Table B
      case "PlannedStudies":
        model = this.getPlannedStudiesTableModel();
        break;
      case "TableBContent":
        model = this.getTableBContentTableModel();
        break;
      // Table C
      case "Crosscutting":
        model = this.getCrossCuttingTableModel();
        break;
      case "TableCContent":
        model = this.getTableCContentTableModel();
        break;
      // Table D
      case "CRPStaffing":
        model = this.getCRPStaffingTableModel();
        break;
      case "TableDContent":
        model = this.getTableDContentTableModel();
        break;
      // Table E
      case "CRPPlannedBudget":
        model = this.getCRPPlannedBudgetTableModel();
        break;
      case "TableEContent":
        model = this.getTableEContentTableModel();
        break;
      // Table F
      case "MainAreas":
        model = this.getMainAreasTableModel();
        break;
      // Table G
      case "CGIARCollaborations":
        model = this.getCGIARCollaborationsTableModel();
        break;
      case "TableGContent":
        model = this.getTableGContentTableModel();
        break;
      // Table H
      case "PlannedMonitoring":
        model = this.getPlannedMonitoringTableModel();
        break;
      case "TableHContent":
        model = this.getTableHContentTableModel();
        break;
    }
    sdf.addTable(query, model);
    subReport.setDataFactory(cdf);
  }


  private TypedTableModel getCGIARCollaborationsTableModel() {
    TypedTableModel model = new TypedTableModel(new String[] {"tableGDescription"}, new Class[] {String.class}, 0);

    model.addRow(new Object[] {""});
    return model;
  }

  @Override
  public int getContentLength() {
    return bytesRTF.length;
  }

  @Override
  public String getContentType() {
    return "application/rtf";
  }


  private TypedTableModel getCrossCuttingTableModel() {
    TypedTableModel model = new TypedTableModel(new String[] {"tableCDescription"}, new Class[] {String.class}, 0);

    model.addRow(new Object[] {""});
    return model;
  }


  private TypedTableModel getCRPManagementTableModel() {
    TypedTableModel model =
      new TypedTableModel(new String[] {"managementRisksTitleDescription", "CRPManagementGovernanceDescription"},
        new Class[] {String.class, String.class}, 0);
    String managementRisksTitleDescription = "&lt;Not Defined&gt;",
      CRPManagementGovernanceDescription = "&lt;Not Defined&gt;";
    if (powbSynthesisPMU != null) {
      // management risk
      if (powbSynthesisPMU.getPowbManagementRisk() != null) {
        managementRisksTitleDescription = powbSynthesisPMU.getPowbManagementRisk().getHighlight() != null
          && !powbSynthesisPMU.getPowbManagementRisk().getHighlight().trim().isEmpty()
            ? powbSynthesisPMU.getPowbManagementRisk().getHighlight() : "&lt;Not Defined&gt;";
      }
      // Governance
      if (powbSynthesisPMU.getPowbManagementGovernance() != null) {
        CRPManagementGovernanceDescription = powbSynthesisPMU.getPowbManagementGovernance().getDescription() != null
          && !powbSynthesisPMU.getPowbManagementGovernance().getDescription().trim().isEmpty()
            ? powbSynthesisPMU.getPowbManagementGovernance().getDescription() : "&lt;Not Defined&gt;";
      }
    }
    model.addRow(new Object[] {managementRisksTitleDescription, CRPManagementGovernanceDescription});
    return model;
  }


  private TypedTableModel getCRPPlannedBudgetTableModel() {
    TypedTableModel model = new TypedTableModel(new String[] {"tableEDescription"}, new Class[] {String.class}, 0);

    model.addRow(new Object[] {""});
    return model;
  }


  private TypedTableModel getCRPStaffingTableModel() {
    TypedTableModel model = new TypedTableModel(new String[] {"tableDDescription"}, new Class[] {String.class}, 0);

    model.addRow(new Object[] {""});
    return model;
  }


  private TypedTableModel getEffectivenessandEfficiencyTableModel() {
    TypedTableModel model = new TypedTableModel(
      new String[] {"staffingDescription", "financialPlanDescription", "newKeyExternalPartnershipsDescription",
        "newContributionPlatformsDescription", "newCrossCRPInteractionsDescription",
        "expectedEffortsCountryCoordinationDescription", "monitoringEvaluationLearningDescription"},
      new Class[] {String.class, String.class, String.class, String.class, String.class, String.class, String.class},
      0);
    String staffingDescription = "&lt;Not Defined&gt;", financialPlanDescription = "&lt;Not Defined&gt;",
      newKeyExternalPartnershipsDescription = "&lt;Not Defined&gt;",
      newContributionPlatformsDescription = "&lt;Not Defined&gt;",
      newCrossCRPInteractionsDescription = "&lt;Not Defined&gt;",
      expectedEffortsCountryCoordinationDescription = "&lt;Not Defined&gt;",
      monitoringEvaluationLearningDescription = "&lt;Not Defined&gt;";

    if (powbSynthesisPMU != null) {
      // TOC
      if (powbSynthesisPMU.getCrpStaffing() != null) {
        staffingDescription = powbSynthesisPMU.getCrpStaffing().getStaffingIssues() != null
          && !powbSynthesisPMU.getCrpStaffing().getStaffingIssues().trim().isEmpty()
            ? powbSynthesisPMU.getCrpStaffing().getStaffingIssues() : "&lt;Not Defined&gt;";
      }
      // Financial Plan
      if (powbSynthesisPMU.getFinancialPlan() != null) {
        financialPlanDescription = powbSynthesisPMU.getFinancialPlan().getFinancialPlanIssues() != null
          && !powbSynthesisPMU.getFinancialPlan().getFinancialPlanIssues().trim().isEmpty()
            ? powbSynthesisPMU.getFinancialPlan().getFinancialPlanIssues() : "&lt;Not Defined&gt;";
      }

      // Collaboration and integration

      if (powbSynthesisPMU.getCollaboration() != null) {
        newKeyExternalPartnershipsDescription = powbSynthesisPMU.getCollaboration().getKeyExternalPartners() != null
          && !powbSynthesisPMU.getCollaboration().getKeyExternalPartners().trim().isEmpty()
            ? powbSynthesisPMU.getCollaboration().getKeyExternalPartners() : "&lt;Not Defined&gt;";

        newContributionPlatformsDescription = powbSynthesisPMU.getCollaboration().getCotributionsPlatafforms() != null
          && !powbSynthesisPMU.getCollaboration().getCotributionsPlatafforms().trim().isEmpty()
            ? powbSynthesisPMU.getCollaboration().getCotributionsPlatafforms() : "&lt;Not Defined&gt;";

        newCrossCRPInteractionsDescription = powbSynthesisPMU.getCollaboration().getCrossCrp() != null
          && !powbSynthesisPMU.getCollaboration().getCrossCrp().trim().isEmpty()
            ? powbSynthesisPMU.getCollaboration().getCrossCrp() : "&lt;Not Defined&gt;";

        expectedEffortsCountryCoordinationDescription =
          powbSynthesisPMU.getCollaboration().getEffostornCountry() != null
            && !powbSynthesisPMU.getCollaboration().getEffostornCountry().trim().isEmpty()
              ? powbSynthesisPMU.getCollaboration().getEffostornCountry() : "&lt;Not Defined&gt;";
      }

      // Monitoring, Evaluation, and Learning
      if (powbSynthesisPMU.getPowbMonitoringEvaluationLearning() != null) {
        monitoringEvaluationLearningDescription =
          powbSynthesisPMU.getPowbMonitoringEvaluationLearning().getHighlight() != null
            && !powbSynthesisPMU.getPowbMonitoringEvaluationLearning().getHighlight().trim().isEmpty()
              ? powbSynthesisPMU.getPowbMonitoringEvaluationLearning().getHighlight() : "&lt;Not Defined&gt;";
      }
    }

    model.addRow(new Object[] {staffingDescription, financialPlanDescription, newKeyExternalPartnershipsDescription,
      newContributionPlatformsDescription, newCrossCRPInteractionsDescription,
      expectedEffortsCountryCoordinationDescription, monitoringEvaluationLearningDescription});
    return model;
  }


  private TypedTableModel getExpectedKeyResultsTableModel() {
    TypedTableModel model = new TypedTableModel(
      new String[] {"unitName", "leadCenter", "participantingCenters", "adjustmentsDescription",
        "expectedCrpDescription", "evidenceDescription", "plansCRPFlagshipDescription", "crossCuttingGenderDescription",
        "crossCuttingOpenDataDescription"},
      new Class[] {String.class, String.class, String.class, String.class, String.class, String.class, String.class,
        String.class, String.class},
      0);
    String unitName = "&lt;Not Defined&gt;", leadCenter = "&lt;Not Defined&gt;",
      participantingCenters = "&lt;Not Defined&gt;", adjustmentsDescription = "&lt;Not Defined&gt;",
      expectedCrpDescription = "&lt;Not Defined&gt;", evidenceDescription = "&lt;Not Defined&gt;",
      plansCRPFlagshipDescription = "", crossCuttingGenderDescription = "&lt;Not Defined&gt;",
      crossCuttingOpenDataDescription = "&lt;Not Defined&gt;";

    unitName = this.getLoggedCrp().getAcronym() != null && !this.getLoggedCrp().getAcronym().isEmpty()
      ? this.getLoggedCrp().getAcronym() : this.getLoggedCrp().getName();


    if (powbSynthesisPMU != null) {
      // TOC
      if (powbSynthesisPMU.getPowbToc() != null) {
        adjustmentsDescription = powbSynthesisPMU.getPowbToc().getTocOverall() != null
          && !powbSynthesisPMU.getPowbToc().getTocOverall().trim().isEmpty()
            ? powbSynthesisPMU.getPowbToc().getTocOverall() : "&lt;Not Defined&gt;";

        if (powbSynthesisPMU.getPowbToc().getFile() != null) {
          adjustmentsDescription +=
            ".<br> " + this.getText("adjustmentsChanges.uploadFile.readText") + ":  <font color=\"blue\"><u>"
              + this.getPowbPath(powbSynthesisPMU.getLiaisonInstitution(),
                this.getLoggedCrp().getAcronym() + "_"
                  + PowbSynthesisSectionStatusEnum.TOC_ADJUSTMENTS.getStatus().toString())
              + powbSynthesisPMU.getPowbToc().getFile().getFileName() + "</u></font>";
        }
      }

      // CRP Progress
      List<PowbExpectedCrpProgress> powbExpectedCrpProgressList =
        powbSynthesisPMU.getPowbExpectedCrpProgresses().stream().filter(e -> e.isActive()).collect(Collectors.toList());

      if (powbExpectedCrpProgressList != null && !powbExpectedCrpProgressList.isEmpty()) {
        PowbExpectedCrpProgress powbExpectedCrpProgress = powbExpectedCrpProgressList.get(0);
        expectedCrpDescription = powbExpectedCrpProgress.getExpectedHighlights() != null
          && !powbExpectedCrpProgress.getExpectedHighlights().trim().isEmpty()
            ? powbExpectedCrpProgress.getExpectedHighlights() : "&lt;Not Defined&gt;";
      }

      // Evidence
      if (powbSynthesisPMU.getPowbEvidence() != null) {
        PowbEvidence powbEvidence = powbSynthesisPMU.getPowbEvidence();
        if (powbEvidence != null) {
          evidenceDescription = powbEvidence.getNarrative() != null && !powbEvidence.getNarrative().trim().isEmpty()
            ? powbEvidence.getNarrative() : "&lt;Not Defined&gt;";
        }
      }

      // Cross Cutting Dimensions Info
      if (powbSynthesisPMU.getPowbCrossCuttingDimension() != null) {
        PowbCrossCuttingDimension crossCutting = powbSynthesisPMU.getPowbCrossCuttingDimension();
        if (crossCutting != null) {
          crossCuttingGenderDescription = crossCutting.getSummarize();
          crossCuttingOpenDataDescription = crossCutting.getAssets();
        }
      }
    }

    if (powbSynthesisList != null && !powbSynthesisList.isEmpty()) {
      for (PowbSynthesis powbSynthesis : powbSynthesisList) {
        // Flagship Plan
        plansCRPFlagshipDescription = this.getFlagshipDescription(powbSynthesis, plansCRPFlagshipDescription);
      }
    }
    if (plansCRPFlagshipDescription.isEmpty()) {
      plansCRPFlagshipDescription = "&lt;Not Defined&gt;";
    }

    model.addRow(new Object[] {unitName, leadCenter, participantingCenters, adjustmentsDescription,
      expectedCrpDescription, evidenceDescription, plansCRPFlagshipDescription, crossCuttingGenderDescription,
      crossCuttingOpenDataDescription});
    return model;
  }


  public List<PowbExpenditureAreas> getExpenditureAreas() {
    List<PowbExpenditureAreas> expenditureAreaList = powbExpenditureAreasManager.findAll().stream()
      .filter(e -> e.isActive() && e.getIsExpenditure()).collect(Collectors.toList());
    if (expenditureAreaList != null) {
      return expenditureAreaList;
    } else {
      return new ArrayList<>();
    }
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
    fileName.append("POWBSummary-");
    fileName.append(this.getSelectedYear() + "_");
    fileName.append(new SimpleDateFormat("yyyyMMdd-HHmm").format(new Date()));
    fileName.append(".rtf");
    return fileName.toString();
  }


  private String getFlagshipDescription(PowbSynthesis powbSynthesis, String plansCRPFlagshipDescription) {
    if (powbSynthesis.getPowbFlagshipPlans() != null) {
      String liaisonName = powbSynthesis.getLiaisonInstitution().getAcronym() != null
        && !powbSynthesis.getLiaisonInstitution().getAcronym().isEmpty()
          ? powbSynthesis.getLiaisonInstitution().getAcronym() : powbSynthesis.getLiaisonInstitution().getName();
      if (plansCRPFlagshipDescription.isEmpty()) {
        plansCRPFlagshipDescription = "• " + liaisonName + ": ";
      } else {
        plansCRPFlagshipDescription += "<br> • " + liaisonName + ": ";
      }

      if (powbSynthesis.getPowbFlagshipPlans().getPlanSummary() != null) {
        plansCRPFlagshipDescription += powbSynthesis.getPowbFlagshipPlans().getPlanSummary();
      }
      if (powbSynthesis.getPowbFlagshipPlans().getFlagshipProgramFile() != null) {
        plansCRPFlagshipDescription +=
          "<br> " + this.getText("plansByFlagship.tableOverall.attached") + ":  <font color=\"blue\"><u>"
            + this.getPowbPath(powbSynthesis.getLiaisonInstitution(),
              this.getLoggedCrp().getAcronym() + "_"
                + PowbSynthesisSectionStatusEnum.FLAGSHIP_PLANS.getStatus().toString())
            + powbSynthesis.getPowbFlagshipPlans().getFlagshipProgramFile().getFileName() + "</u></font>";
      }
    }
    return plansCRPFlagshipDescription;
  }


  public List<LiaisonInstitution> getFlagships() {
    List<LiaisonInstitution> flagshipsList = this.getLoggedCrp().getLiaisonInstitutions().stream()
      .filter(c -> c.getCrpProgram() != null
        && c.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue() && c.isActive())
      .collect(Collectors.toList());
    if (flagshipsList != null) {
      flagshipsList.sort(Comparator.comparing(LiaisonInstitution::getAcronym));
      return flagshipsList;
    } else {
      return new ArrayList<>();
    }
  }


  public void getfpExercises(List<LiaisonInstitution> lInstitutions, long phaseID) {
    flagshipExercises = new ArrayList<>();
    for (LiaisonInstitution liaisonInstitution : lInstitutions) {
      PowbSynthesis powbSynthesis = powbSynthesisManager.findSynthesis(phaseID, liaisonInstitution.getId());
      if (powbSynthesis != null) {
        if (powbSynthesis.getPowbMonitoringEvaluationLearning() != null) {
          if (powbSynthesis.getPowbMonitoringEvaluationLearning()
            .getPowbMonitoringEvaluationLearningExercises() != null) {
            List<PowbMonitoringEvaluationLearningExercise> exercises = new ArrayList<>(
              powbSynthesis.getPowbMonitoringEvaluationLearning().getPowbMonitoringEvaluationLearningExercises()
                .stream().filter(s -> s.isActive()).collect(Collectors.toList()));
            if (exercises != null || !exercises.isEmpty()) {
              for (PowbMonitoringEvaluationLearningExercise powbMonitoringEvaluationLearningExercise : exercises) {
                flagshipExercises.add(powbMonitoringEvaluationLearningExercise);
              }
            }
          }
        }
      }
    }
  }

  public void getFpPlannedList(List<LiaisonInstitution> lInstitutions, long phaseID) {
    flagshipPlannedList = new ArrayList<>();
    for (LiaisonInstitution liaisonInstitution : lInstitutions) {
      PowbSynthesis powbSynthesis = powbSynthesisManager.findSynthesis(phaseID, liaisonInstitution.getId());
      if (powbSynthesis != null) {
        if (powbSynthesis.getPowbEvidence() != null) {
          if (powbSynthesis.getPowbEvidence().getPowbEvidencePlannedStudies() != null) {
            List<PowbEvidencePlannedStudy> studies = new ArrayList<>(powbSynthesis.getPowbEvidence()
              .getPowbEvidencePlannedStudies().stream().filter(s -> s.isActive()).collect(Collectors.toList()));
            if (studies != null || !studies.isEmpty()) {
              for (PowbEvidencePlannedStudy powbEvidencePlannedStudy : studies) {
                flagshipPlannedList.add(powbEvidencePlannedStudy);
              }
            }
          }
        }
      }
    }
  }

  @Override
  public InputStream getInputStream() {
    if (inputStream == null) {
      inputStream = new ByteArrayInputStream(bytesRTF);
    }
    return inputStream;
  }


  private TypedTableModel getMainAreasTableModel() {
    TypedTableModel model =
      new TypedTableModel(new String[] {"expenditureArea", "estimatedPercentajeFS", "commentsSpace"},
        new Class[] {String.class, Double.class, String.class}, 0);
    List<PowbFinancialExpenditure> powbFinancialExpenditureList =
      powbSynthesisPMU.getPowbFinancialExpenditures().stream().filter(p -> p.isActive()).collect(Collectors.toList());
    // Expenditure areas
    List<PowbExpenditureAreas> powbExpenditureAreas = this.getExpenditureAreas();
    if (powbExpenditureAreas != null && !powbExpenditureAreas.isEmpty()) {
      for (PowbExpenditureAreas powbExpenditureArea : powbExpenditureAreas) {
        Double estimatedPercentajeFS = 0.0;
        String expenditureArea = "", commentsSpace = "";
        expenditureArea = powbExpenditureArea.getExpenditureArea();
        if (powbFinancialExpenditureList != null && !powbFinancialExpenditureList.isEmpty()) {
          List<PowbFinancialExpenditure> powbFinancialExpenditureAreaList = powbFinancialExpenditureList.stream()
            .filter(f -> f.getPowbExpenditureArea() != null && f.getPowbExpenditureArea().equals(powbExpenditureArea))
            .collect(Collectors.toList());
          PowbFinancialExpenditure powbFinancialExpenditure = null;
          if (powbFinancialExpenditureAreaList != null && !powbFinancialExpenditureAreaList.isEmpty()) {
            powbFinancialExpenditure = powbFinancialExpenditureAreaList.get(0);
            estimatedPercentajeFS = powbFinancialExpenditure.getW1w2Percentage();
            commentsSpace =
              powbFinancialExpenditure.getComments() == null || powbFinancialExpenditure.getComments().trim().isEmpty()
                ? " " : powbFinancialExpenditure.getComments();
          }
          model.addRow(new Object[] {expenditureArea, estimatedPercentajeFS, commentsSpace});
        }
      }
    }
    return model;
  }


  private TypedTableModel getMasterTableModel() {
    // Initialization of Model
    TypedTableModel model =
      new TypedTableModel(new String[] {"unit", "year"}, new Class[] {String.class, Integer.class});
    model.addRow(new Object[] {this.getLoggedCrp().getAcronym(), this.getSelectedYear()});
    return model;
  }


  public List<PowbExpenditureAreas> getPlannedBudgetAreas() {
    List<PowbExpenditureAreas> plannedBudgetAreasList = powbExpenditureAreasManager.findAll().stream()
      .filter(e -> e.isActive() && !e.getIsExpenditure()).collect(Collectors.toList());
    if (plannedBudgetAreasList != null) {
      return plannedBudgetAreasList;
    } else {
      return new ArrayList<>();
    }
  }


  private TypedTableModel getPlannedMilestonesTableModel() {
    TypedTableModel model = new TypedTableModel(new String[] {"tableADescription"}, new Class[] {String.class}, 0);

    model.addRow(new Object[] {""});
    return model;
  }


  private TypedTableModel getPlannedMonitoringTableModel() {
    TypedTableModel model = new TypedTableModel(new String[] {"tableHDescription"}, new Class[] {String.class}, 0);

    model.addRow(new Object[] {""});
    return model;
  }

  private TypedTableModel getPlannedStudiesTableModel() {
    TypedTableModel model = new TypedTableModel(new String[] {"tableBDescription"}, new Class[] {String.class}, 0);

    model.addRow(new Object[] {""});
    return model;
  }

  /**
   * get the PMU institution
   * 
   * @param institution
   * @return
   */
  public LiaisonInstitution getPMUInstitution() {
    for (LiaisonInstitution liaisonInstitution : this.getLoggedCrp().getLiaisonInstitutions()) {
      if (this.isPMU(liaisonInstitution)) {
        return liaisonInstitution;
      }
    }
    return null;
  }

  public PowbExpectedCrpProgress getPowbExpectedCrpProgressProgram(Long crpMilestoneID, Long crpProgramID) {
    List<PowbExpectedCrpProgress> powbExpectedCrpProgresses =
      powbExpectedCrpProgressManager.findByProgram(crpProgramID);
    List<PowbExpectedCrpProgress> powbExpectedCrpProgressMilestone = powbExpectedCrpProgresses.stream()
      .filter(c -> c.getCrpMilestone().getId().longValue() == crpMilestoneID.longValue()).collect(Collectors.toList());
    if (!powbExpectedCrpProgressMilestone.isEmpty()) {
      return powbExpectedCrpProgressMilestone.get(0);
    }
    return new PowbExpectedCrpProgress();
  }

  // Method to download link file
  public String getPowbPath(LiaisonInstitution liaisonInstitution, String actionName) {
    return config.getDownloadURL() + "/" + this.getPowbSourceFolder(liaisonInstitution, actionName).replace('\\', '/');
  }

  // Method to get the download folder
  private String getPowbSourceFolder(LiaisonInstitution liaisonInstitution, String actionName) {
    return APConstants.POWB_FOLDER.concat(File.separator).concat(this.getCrpSession()).concat(File.separator)
      .concat(liaisonInstitution.getAcronym()).concat(File.separator).concat(actionName.replace("/", "_"))
      .concat(File.separator);
  }

  private TypedTableModel getTableAContentTableModel() {
    TypedTableModel model = new TypedTableModel(
      new String[] {"FP", "subIDO", "outcomes", "milestone", "w1w2", "w3Bilateral", "assessment", "meansVerifications"},
      new Class[] {String.class, String.class, String.class, String.class, Double.class, Double.class, String.class,
        String.class},
      0);
    this.loadTablePMU();
    String FP, subIDO = "", outcomes, milestone, assessment, meansVerifications;
    Double w1w2, w3Bilateral;

    for (CrpProgram flagship : flagships) {
      int outcome_index = 0;
      for (CrpProgramOutcome outcome : flagship.getOutcomes()) {
        subIDO = "";
        int milestone_index = 0;
        for (CrpMilestone crpMilestone : outcome.getMilestones()) {
          Boolean isFlagshipRow = (outcome_index == 0) && (milestone_index == 0);
          Boolean isOutcomeRow = (milestone_index == 0);
          if (isFlagshipRow) {
            FP = flagship.getAcronym();
          } else {
            FP = " ";
          }
          if (isOutcomeRow) {
            outcomes = outcome.getComposedName();
          } else {
            outcomes = " ";
          }
          milestone = crpMilestone.getComposedName();
          if (isOutcomeRow) {
            for (CrpOutcomeSubIdo subIdo : outcome.getSubIdos()) {
              if (subIDO.isEmpty()) {
                if (subIdo.getSrfSubIdo().getSrfIdo().isIsCrossCutting()) {
                  subIDO = "• CC: " + subIdo.getSrfSubIdo().getDescription();
                } else {
                  subIDO = "• " + subIdo.getSrfSubIdo().getDescription();
                }
              } else {
                if (subIdo.getSrfSubIdo().getSrfIdo().isIsCrossCutting()) {
                  subIDO += "\n • CC:" + subIdo.getSrfSubIdo().getDescription();
                } else {
                  subIDO += "\n •" + subIdo.getSrfSubIdo().getDescription();
                }
              }
            }
          } else {
            subIDO = " ";
          }

          w1w2 = flagship.getW1();
          w3Bilateral = flagship.getW3();
          PowbExpectedCrpProgress milestoneProgress =
            this.getPowbExpectedCrpProgressProgram(crpMilestone.getId(), flagship.getId());
          assessment =
            milestoneProgress.getAssesmentName() != null && !milestoneProgress.getAssesmentName().trim().isEmpty()
              ? milestoneProgress.getAssesmentName() : " ";
          meansVerifications = milestoneProgress.getMeans() != null && !milestoneProgress.getMeans().trim().isEmpty()
            ? milestoneProgress.getMeans() : " ";

          model
            .addRow(new Object[] {FP, subIDO, outcomes, milestone, w1w2, w3Bilateral, assessment, meansVerifications});
          milestone_index++;
        }
        outcome_index++;
      }
    }
    return model;
  }

  private TypedTableModel getTableBContentTableModel() {
    TypedTableModel model =
      new TypedTableModel(new String[] {"plannedStudy", "geographicScope", "revelantSubIDO", "comments"},
        new Class[] {String.class, String.class, String.class, String.class}, 0);


    this.getFpPlannedList(this.getFlagships(), this.getSelectedPhase().getId());
    for (PowbEvidencePlannedStudy powbEvidencePlannedStudy : flagshipPlannedList) {
      String plannedStudy = "", geographicScope = "", revelantSubIDO = "", comments = "";
      plannedStudy = powbEvidencePlannedStudy.getProjectExpectedStudy().getTopicStudy() != null
        && !powbEvidencePlannedStudy.getProjectExpectedStudy().getTopicStudy().trim().isEmpty()
          ? powbEvidencePlannedStudy.getPowbEvidence().getPowbSynthesis().getLiaisonInstitution().getCrpProgram()
            .getAcronym() + ": " + powbEvidencePlannedStudy.getProjectExpectedStudy().getTopicStudy()
          : powbEvidencePlannedStudy.getPowbEvidence().getPowbSynthesis().getLiaisonInstitution().getCrpProgram()
            .getAcronym();

      geographicScope = powbEvidencePlannedStudy.getProjectExpectedStudy().getScopeName() != null
        && !powbEvidencePlannedStudy.getProjectExpectedStudy().getScopeName().trim().isEmpty()
          ? powbEvidencePlannedStudy.getProjectExpectedStudy().getScopeName() : " ";
      if (powbEvidencePlannedStudy.getProjectExpectedStudy().getSrfSubIdo() != null
        && powbEvidencePlannedStudy.getProjectExpectedStudy().getSrfSubIdo().getDescription() != null
        && !powbEvidencePlannedStudy.getProjectExpectedStudy().getSrfSubIdo().getDescription().trim().isEmpty()) {
        revelantSubIDO += "• " + powbEvidencePlannedStudy.getProjectExpectedStudy().getSrfSubIdo().getDescription();
      }

      if (powbEvidencePlannedStudy.getProjectExpectedStudy().getSrfSloIndicator() != null
        && powbEvidencePlannedStudy.getProjectExpectedStudy().getSrfSloIndicator().getTitle() != null
        && !powbEvidencePlannedStudy.getProjectExpectedStudy().getSrfSloIndicator().getTitle().trim().isEmpty()) {
        if (revelantSubIDO.isEmpty()) {
          revelantSubIDO += "• " + powbEvidencePlannedStudy.getProjectExpectedStudy().getSrfSloIndicator().getTitle();
        } else {
          revelantSubIDO += "\n• " + powbEvidencePlannedStudy.getProjectExpectedStudy().getSrfSloIndicator().getTitle();
        }
      }
      if (revelantSubIDO.isEmpty()) {
        revelantSubIDO = " ";
      }
      comments = powbEvidencePlannedStudy.getProjectExpectedStudy().getComments() != null
        && !powbEvidencePlannedStudy.getProjectExpectedStudy().getComments().trim().isEmpty()
          ? powbEvidencePlannedStudy.getProjectExpectedStudy().getComments() : " ";

      model.addRow(new Object[] {plannedStudy, geographicScope, revelantSubIDO, comments});
    }
    return model;
  }

  private TypedTableModel getTableCContentTableModel() {
    TypedTableModel model = new TypedTableModel(
      new String[] {"Scored2Gender", "Scored2Youth", "Scored2CapDev", "scored1Gender", "scored1Youth", "scored1CapDev",
        "scored0Gender", "scored0Youth", "scored0CapDev", "totalOverallOutputs"},
      new Class[] {Double.class, Double.class, Double.class, Double.class, Double.class, Double.class, Double.class,
        Double.class, Double.class, Double.class},
      0);

    this.tableCInfo(this.getSelectedPhase());

    if (tableC != null) {
      model.addRow(new Object[] {tableC.getPercentageGenderPrincipal() / 100,
        tableC.getPercentageYouthPrincipal() / 100, tableC.getPercentageCapDevPrincipal() / 100,
        tableC.getPercentageGenderSignificant() / 100, tableC.getPercentageYouthSignificant() / 100,
        tableC.getPercentageCapDevSignificant() / 100, tableC.getPercentageGenderNotScored() / 100,
        tableC.getPercentageYouthNotScored() / 100, tableC.getPercentageCapDevNotScored() / 100, tableC.getTotal()});
    }
    return model;
  }

  private TypedTableModel getTableDContentTableModel() {
    TypedTableModel model =
      new TypedTableModel(new String[] {"category", "female", "totalFTE", "FemalePercentaje", "male"},
        new Class[] {String.class, Double.class, Double.class, Double.class, Double.class}, 0);

    List<PowbSynthesisCrpStaffingCategory> powbSynthesisCrpStaffingCategoryList =
      powbSynthesisPMU.getPowbSynthesisCrpStaffingCategory().stream().filter(c -> c.isActive())
        .sorted((c1, c2) -> c1.getId().compareTo(c2.getId())).collect(Collectors.toList());
    if (powbSynthesisCrpStaffingCategoryList != null && !powbSynthesisCrpStaffingCategoryList.isEmpty()) {
      for (PowbSynthesisCrpStaffingCategory powbSynthesisCrpStaffingCategory : powbSynthesisCrpStaffingCategoryList) {
        String category = "";
        Double female = 0.0, totalFTE = 0.0, femalePercentaje = 0.0, male = 0.0;
        category = powbSynthesisCrpStaffingCategory.getPowbCrpStaffingCategory().getCategory();
        female = powbSynthesisCrpStaffingCategory.getFemale();
        totalFTE = powbSynthesisCrpStaffingCategory.getTotalFTE();
        femalePercentaje = powbSynthesisCrpStaffingCategory.getFemalePercentage() / 100.0;
        male = powbSynthesisCrpStaffingCategory.getMale();
        model.addRow(new Object[] {category, female, totalFTE, femalePercentaje, male});
      }
    }
    return model;
  }

  private TypedTableModel getTableEContentTableModel() {
    TypedTableModel model = new TypedTableModel(new String[] {"category", "w1w2", "w3Bilateral", "total", "comments"},
      new Class[] {String.class, Double.class, Double.class, Double.class, String.class}, 0);
    List<PowbFinancialPlannedBudget> powbFinancialPlannedBudgetList =
      powbSynthesisPMU.getPowbFinancialPlannedBudget().stream().filter(p -> p.isActive()).collect(Collectors.toList());
    // Flagships
    List<LiaisonInstitution> flagships = this.getFlagships();
    if (flagships != null && !flagships.isEmpty()) {
      for (LiaisonInstitution flagship : flagships) {
        Double w1w2 = 0.0, w3Bilateral = 0.0, total = 0.0;
        String category = "", comments = "";
        category = flagship.getAcronym();
        if (powbFinancialPlannedBudgetList != null && !powbFinancialPlannedBudgetList.isEmpty()) {
          List<PowbFinancialPlannedBudget> powbFinancialPlannedBudgetFlagshipList = powbFinancialPlannedBudgetList
            .stream().filter(f -> f.getLiaisonInstitution() != null && f.getLiaisonInstitution().equals(flagship))
            .collect(Collectors.toList());
          PowbFinancialPlannedBudget powbFinancialPlannedBudget = null;
          if (powbFinancialPlannedBudgetFlagshipList != null && !powbFinancialPlannedBudgetFlagshipList.isEmpty()) {
            powbFinancialPlannedBudget = powbFinancialPlannedBudgetFlagshipList.get(0);
            w1w2 = powbFinancialPlannedBudget.getW1w2();
            w3Bilateral = powbFinancialPlannedBudget.getW3Bilateral();
            total = powbFinancialPlannedBudget.getTotalPlannedBudget();
            comments = powbFinancialPlannedBudget.getComments() == null
              || powbFinancialPlannedBudget.getComments().trim().isEmpty() ? " "
                : powbFinancialPlannedBudget.getComments();
          }
        }
        model.addRow(new Object[] {category, w1w2, w3Bilateral, total, comments});
      }
    }
    // Expenditure areas
    List<PowbExpenditureAreas> powbExpenditureAreas = this.getPlannedBudgetAreas();
    if (powbExpenditureAreas != null && !powbExpenditureAreas.isEmpty()) {
      for (PowbExpenditureAreas powbExpenditureArea : powbExpenditureAreas) {
        Double w1w2 = 0.0, w3Bilateral = 0.0, total = 0.0;
        String category = "", comments = "";
        category = powbExpenditureArea.getExpenditureArea();
        if (powbFinancialPlannedBudgetList != null && !powbFinancialPlannedBudgetList.isEmpty()) {
          List<PowbFinancialPlannedBudget> powbFinancialPlannedBudgetExpenditureList =
            powbFinancialPlannedBudgetList.stream()
              .filter(f -> f.getPowbExpenditureArea() != null && f.getPowbExpenditureArea().equals(powbExpenditureArea))
              .collect(Collectors.toList());
          PowbFinancialPlannedBudget powbFinancialPlannedBudget = null;
          if (powbFinancialPlannedBudgetExpenditureList != null
            && !powbFinancialPlannedBudgetExpenditureList.isEmpty()) {
            powbFinancialPlannedBudget = powbFinancialPlannedBudgetExpenditureList.get(0);
            w1w2 = powbFinancialPlannedBudget.getW1w2();
            w3Bilateral = powbFinancialPlannedBudget.getW3Bilateral();
            total = powbFinancialPlannedBudget.getTotalPlannedBudget();
            comments = powbFinancialPlannedBudget.getComments() == null
              || powbFinancialPlannedBudget.getComments().trim().isEmpty() ? " "
                : powbFinancialPlannedBudget.getComments();
          }
          model.addRow(new Object[] {category, w1w2, w3Bilateral, total, comments});
        }
      }
    }

    return model;
  }

  private TypedTableModel getTableGContentTableModel() {
    TypedTableModel model = new TypedTableModel(new String[] {"crpPlatform", "descriptionCollaboration", "relevantFP"},
      new Class[] {String.class, String.class, String.class}, 0);

    for (PowbSynthesis powbSynthesis : powbSynthesisList) {
      List<PowbCollaborationGlobalUnit> powbCollaborationGlobalUnitList =
        powbSynthesis.getPowbCollaborationGlobalUnits().stream().filter(c -> c.isActive()).collect(Collectors.toList());
      if (powbCollaborationGlobalUnitList != null && !powbCollaborationGlobalUnitList.isEmpty()) {
        for (PowbCollaborationGlobalUnit powbCollaborationGlobalUnit : powbCollaborationGlobalUnitList) {
          String crpPlatform = " ", descriptionCollaboration = " ", relevantFP = " ";
          if (powbCollaborationGlobalUnit.getGlobalUnit() != null) {
            crpPlatform = powbCollaborationGlobalUnit.getGlobalUnit().getAcronym() != null
              && !powbCollaborationGlobalUnit.getGlobalUnit().getAcronym().isEmpty()
                ? powbCollaborationGlobalUnit.getGlobalUnit().getAcronym()
                : powbCollaborationGlobalUnit.getGlobalUnit().getName();
          }

          descriptionCollaboration =
            powbCollaborationGlobalUnit.getBrief() != null && !powbCollaborationGlobalUnit.getBrief().isEmpty()
              ? powbCollaborationGlobalUnit.getBrief() : " ";
          relevantFP =
            powbCollaborationGlobalUnit.getFlagship() != null && !powbCollaborationGlobalUnit.getFlagship().isEmpty()
              ? powbCollaborationGlobalUnit.getFlagship() : " ";
          model.addRow(new Object[] {crpPlatform, descriptionCollaboration, relevantFP});
        }
      }

    }

    return model;
  }

  private TypedTableModel getTableHContentTableModel() {
    TypedTableModel model = new TypedTableModel(new String[] {"plannedStudiesLearning", "comments"},
      new Class[] {String.class, String.class}, 0);
    // Flagships
    List<LiaisonInstitution> flagships = this.getFlagships();

    this.getfpExercises(flagships, this.getSelectedPhase().getId());
    if (flagships != null && !flagships.isEmpty()) {
      for (LiaisonInstitution flagship : flagships) {
        String plannedStudiesLearning = "", comments = "";
        if (flagshipExercises != null && !flagshipExercises.isEmpty()) {
          List<PowbMonitoringEvaluationLearningExercise> PowbMonitoringEvaluationLearningFlagshipExercise =
            flagshipExercises.stream()
              .filter(fe -> fe.isActive()
                && fe.getPowbMonitoringEvaluationLearning().getPowbSynthesis().getLiaisonInstitution().equals(flagship))
              .collect(Collectors.toList());

          for (PowbMonitoringEvaluationLearningExercise powbMonitoringEvaluationLearningExercise : PowbMonitoringEvaluationLearningFlagshipExercise) {
            plannedStudiesLearning = powbMonitoringEvaluationLearningExercise.getExercise() == null
              || powbMonitoringEvaluationLearningExercise.getExercise().trim().isEmpty() ? flagship.getAcronym()
                : flagship.getAcronym() + ": " + powbMonitoringEvaluationLearningExercise.getExercise();
            comments = powbMonitoringEvaluationLearningExercise.getComments() == null
              || powbMonitoringEvaluationLearningExercise.getComments().trim().isEmpty() ? " "
                : powbMonitoringEvaluationLearningExercise.getComments();
            model.addRow(new Object[] {plannedStudiesLearning, comments});
          }
        }
      }
    }

    return model;
  }

  public int getYear() {
    return year;
  }

  public boolean isPMU(LiaisonInstitution institution) {
    if (institution.getAcronym().equals("PMU")) {
      return true;
    }
    return false;
  }

  public void loadFlagShipBudgetInfo(CrpProgram crpProgram) {
    List<ProjectFocus> projects = crpProgram.getProjectFocuses().stream()
      .filter(c -> c.getProject().isActive() && c.isActive()).collect(Collectors.toList());
    Set<Project> myProjects = new HashSet();
    for (ProjectFocus projectFocus : projects) {
      Project project = projectFocus.getProject();
      if (project.isActive()) {
        project.setProjectInfo(project.getProjecInfoPhase(this.getActualPhase()));
        if (project.getProjectInfo() != null && project.getProjectInfo().getStatus() != null) {
          if (project.getProjectInfo().getStatus().intValue() == Integer
            .parseInt(ProjectStatusEnum.Ongoing.getStatusId())
            || project.getProjectInfo().getStatus().intValue() == Integer
              .parseInt(ProjectStatusEnum.Extended.getStatusId())) {
            myProjects.add(project);
          }
        }
      }
    }
    for (Project project : myProjects) {
      double w1 = project.getCoreBudget(this.getActualPhase().getYear(), this.getActualPhase());
      double w3 = project.getW3Budget(this.getActualPhase().getYear(), this.getActualPhase());
      double bilateral = project.getBilateralBudget(this.getActualPhase().getYear(), this.getActualPhase());
      List<ProjectBudgetsFlagship> budgetsFlagships = project.getProjectBudgetsFlagships().stream()
        .filter(c -> c.isActive() && c.getCrpProgram().getId().longValue() == crpProgram.getId().longValue()
          && c.getPhase().equals(this.getActualPhase()) && c.getYear() == this.getActualPhase().getYear())
        .collect(Collectors.toList());
      double percentageW1 = 0;
      double percentageW3 = 0;
      double percentageB = 0;
      if (!this.getCountProjectFlagships(project.getId())) {
        percentageW1 = 100;
        percentageW3 = 100;
        percentageB = 100;
      }
      for (ProjectBudgetsFlagship projectBudgetsFlagship : budgetsFlagships) {
        switch (projectBudgetsFlagship.getBudgetType().getId().intValue()) {
          case 1:
            percentageW1 = percentageW1 + projectBudgetsFlagship.getAmount();
            break;
          case 2:
            percentageW3 = percentageW3 + projectBudgetsFlagship.getAmount();
            break;
          case 3:
            percentageB = percentageB + projectBudgetsFlagship.getAmount();
            break;
          default:
            break;
        }
      }
      w1 = w1 * (percentageW1) / 100;
      w3 = w3 * (percentageW3) / 100;
      bilateral = bilateral * (percentageB) / 100;
      crpProgram.setW1(crpProgram.getW1() + w1);
      crpProgram.setW3(crpProgram.getW3() + w3 + bilateral);
    }
  }

  public void loadTablePMU() {
    flagships = this.getLoggedCrp().getCrpPrograms().stream()
      .filter(c -> c.isActive() && c.getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue())
      .collect(Collectors.toList());
    flagships.sort((p1, p2) -> p1.getAcronym().compareTo(p2.getAcronym()));

    for (CrpProgram crpProgram : flagships) {
      crpProgram.setMilestones(new ArrayList<>());
      crpProgram.setW1(new Double(0));
      crpProgram.setW3(new Double(0));

      crpProgram.setOutcomes(crpProgram.getCrpProgramOutcomes().stream()
        .filter(c -> c.isActive() && c.getPhase().equals(this.getActualPhase())).collect(Collectors.toList()));
      List<CrpProgramOutcome> validOutcomes = new ArrayList<>();
      for (CrpProgramOutcome crpProgramOutcome : crpProgram.getOutcomes()) {

        crpProgramOutcome.setMilestones(crpProgramOutcome.getCrpMilestones().stream()
          .filter(c -> c.isActive() && c.getYear().intValue() == this.getActualPhase().getYear())
          .collect(Collectors.toList()));
        crpProgramOutcome.setSubIdos(
          crpProgramOutcome.getCrpOutcomeSubIdos().stream().filter(c -> c.isActive()).collect(Collectors.toList()));
        crpProgram.getMilestones().addAll(crpProgramOutcome.getMilestones());
        if (!crpProgram.getMilestones().isEmpty()) {
          validOutcomes.add(crpProgramOutcome);
        }
      }
      crpProgram.setOutcomes(validOutcomes);
      this.loadFlagShipBudgetInfo(crpProgram);
    }
  }

  @Override
  public void prepare() {
    this.setGeneralParameters();
    powbSynthesisList =
      this.getSelectedPhase().getPowbSynthesis().stream().filter(ps -> ps.isActive()).collect(Collectors.toList());
    pmuInstitution = this.getPMUInstitution();
    List<PowbSynthesis> powbSynthesisPMUList = powbSynthesisList.stream()
      .filter(p -> p.isActive() && p.getLiaisonInstitution().equals(pmuInstitution)).collect(Collectors.toList());
    if (powbSynthesisPMUList != null && !powbSynthesisPMUList.isEmpty()) {
      powbSynthesisPMU = powbSynthesisPMUList.get(0);
    }

    // Calculate time to generate report
    startTime = System.currentTimeMillis();
    LOG.info(
      "Start report download: " + this.getFileName() + ". User: " + this.getCurrentUser().getComposedCompleteName()
        + ". CRP: " + this.getLoggedCrp().getAcronym() + ". Cycle: " + this.getSelectedCycle());
  }

  /**
   * List all the deliverables of the Crp to make the calculations in the Cross Cutting Socores.
   * 
   * @param pashe - The phase that get the deliverable information.
   */
  public void tableCInfo(Phase pashe) {
    List<Deliverable> deliverables = new ArrayList<>();
    deliverableList = new ArrayList<>();
    int iGenderPrincipal = 0;
    int iGenderSignificant = 0;
    int iYouthPrincipal = 0;
    int iYouthSignificant = 0;
    int iCapDevPrincipal = 0;
    int iCapDevSignificant = 0;
    int iNa = 0;

    if (deliverableManager.findAll() != null) {
      deliverables = deliverableManager.findAll().stream().filter(d -> d.isActive() && d.getPhase().equals(pashe))
        .collect(Collectors.toList());
      for (Deliverable deliverable : deliverables) {
        DeliverableInfo deliverableInfo = deliverable.getDeliverableInfo(pashe);
        if (deliverableInfo.isActive()) {
          if (deliverableInfo.getStatus() != null) {
            if (deliverableInfo.getStatus() == Integer.parseInt(ProjectStatusEnum.Ongoing.getStatusId())
              || deliverableInfo.getStatus() == Integer.parseInt(ProjectStatusEnum.Extended.getStatusId())) {
              deliverableList.add(deliverableInfo);
              if (deliverableInfo.getCrossCuttingNa() != null && deliverableInfo.getCrossCuttingNa()) {
                iNa++;
              } else {
                // Gender
                if (deliverableInfo.getCrossCuttingGender() != null && deliverableInfo.getCrossCuttingGender()) {
                  if (deliverableInfo.getCrossCuttingScoreGender() != null
                    && deliverableInfo.getCrossCuttingScoreGender() == 1) {
                    iGenderSignificant++;
                  } else if (deliverableInfo.getCrossCuttingScoreGender() != null
                    && deliverableInfo.getCrossCuttingScoreGender() == 2) {
                    iGenderPrincipal++;
                  }
                }

                // Youth
                if (deliverableInfo.getCrossCuttingYouth() != null && deliverableInfo.getCrossCuttingYouth()) {
                  if (deliverableInfo.getCrossCuttingScoreYouth() != null
                    && deliverableInfo.getCrossCuttingScoreYouth() == 1) {
                    iYouthSignificant++;
                  } else if (deliverableInfo.getCrossCuttingScoreYouth() != null
                    && deliverableInfo.getCrossCuttingScoreYouth() == 2) {
                    iYouthPrincipal++;
                  }
                }

                // CapDev
                if (deliverableInfo.getCrossCuttingCapacity() != null && deliverableInfo.getCrossCuttingCapacity()) {
                  if (deliverableInfo.getCrossCuttingScoreCapacity() != null
                    && deliverableInfo.getCrossCuttingScoreCapacity() == 1) {
                    iCapDevSignificant++;
                  } else if (deliverableInfo.getCrossCuttingScoreCapacity() != null
                    && deliverableInfo.getCrossCuttingScoreCapacity() == 2) {
                    iCapDevPrincipal++;
                  }
                }
              }
            }
          }
        }
      }
      tableC = new CrossCuttingDimensionTableDTO();
      int iDeliverableCount = deliverableList.size();

      tableC.setTotal(iDeliverableCount);

      double dGenderPrincipal = (iGenderPrincipal * 100.0) / iDeliverableCount;
      double dGenderSignificant = (iGenderSignificant * 100.0) / iDeliverableCount;
      double dYouthPrincipal = (iYouthPrincipal * 100.0) / iDeliverableCount;
      double dYouthSignificant = (iYouthSignificant * 100.0) / iDeliverableCount;
      double dCapDevPrincipal = (iCapDevPrincipal * 100.0) / iDeliverableCount;
      double dCapDevSignificant = (iCapDevSignificant * 100.0) / iDeliverableCount;
      double dNa = (iNa * 100.0) / iDeliverableCount;


      // Gender
      tableC.setGenderPrincipal(iGenderPrincipal);
      tableC.setGenderSignificant(iGenderSignificant);
      tableC.setGenderScored(iNa);

      tableC.setPercentageGenderPrincipal(dGenderPrincipal);
      tableC.setPercentageGenderSignificant(dGenderSignificant);
      tableC.setPercentageGenderNotScored(dNa);
      // Youth
      tableC.setYouthPrincipal(iYouthPrincipal);
      tableC.setYouthSignificant(iYouthSignificant);
      tableC.setYouthScored(iNa);

      tableC.setPercentageYouthPrincipal(dYouthPrincipal);
      tableC.setPercentageYouthSignificant(dYouthSignificant);
      tableC.setPercentageYouthNotScored(dNa);
      // CapDev
      tableC.setCapDevPrincipal(iCapDevPrincipal);
      tableC.setCapDevSignificant(iCapDevSignificant);
      tableC.setCapDevScored(iNa);

      tableC.setPercentageCapDevPrincipal(dCapDevPrincipal);
      tableC.setPercentageCapDevSignificant(dCapDevSignificant);
      tableC.setPercentageCapDevNotScored(dNa);


    }

  }

}