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
import org.cgiar.ccafs.marlo.data.manager.PowbExpectedCrpProgressManager;
import org.cgiar.ccafs.marlo.data.manager.PowbExpenditureAreasManager;
import org.cgiar.ccafs.marlo.data.manager.PowbSynthesisManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyManager;
import org.cgiar.ccafs.marlo.data.model.CrossCuttingDimensionTableDTO;
import org.cgiar.ccafs.marlo.data.model.CrpMilestone;
import org.cgiar.ccafs.marlo.data.model.CrpOutcomeSubIdo;
import org.cgiar.ccafs.marlo.data.model.CrpPpaPartner;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.CrpProgramOutcome;
import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.data.model.DeliverableInfo;
import org.cgiar.ccafs.marlo.data.model.GlobalUnitProject;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.PowbCollaborationGlobalUnit;
import org.cgiar.ccafs.marlo.data.model.PowbCrossCuttingDimension;
import org.cgiar.ccafs.marlo.data.model.PowbEvidence;
import org.cgiar.ccafs.marlo.data.model.PowbEvidencePlannedStudy;
import org.cgiar.ccafs.marlo.data.model.PowbEvidencePlannedStudyDTO;
import org.cgiar.ccafs.marlo.data.model.PowbExpectedCrpProgress;
import org.cgiar.ccafs.marlo.data.model.PowbExpenditureAreas;
import org.cgiar.ccafs.marlo.data.model.PowbFinancialExpenditure;
import org.cgiar.ccafs.marlo.data.model.PowbFinancialPlannedBudget;
import org.cgiar.ccafs.marlo.data.model.PowbSynthesis;
import org.cgiar.ccafs.marlo.data.model.PowbSynthesisCrpStaffingCategory;
import org.cgiar.ccafs.marlo.data.model.PowbSynthesisSectionStatusEnum;
import org.cgiar.ccafs.marlo.data.model.ProgramType;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectBudgetsFlagship;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudy;
import org.cgiar.ccafs.marlo.data.model.ProjectFocus;
import org.cgiar.ccafs.marlo.data.model.ProjectStatusEnum;
import org.cgiar.ccafs.marlo.data.model.TypeExpectedStudiesEnum;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
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

  public static double round(double value, int places) {
    if (places < 0) {
      throw new IllegalArgumentException();
    }

    BigDecimal bd = new BigDecimal(value);
    bd = bd.setScale(places, RoundingMode.HALF_UP);
    return bd.doubleValue();
  }

  // Parameters
  private int year;
  private long startTime;
  private List<PowbSynthesis> powbSynthesisList;
  private LiaisonInstitution pmuInstitution;
  private PowbSynthesis powbSynthesisPMU;
  private List<CrpProgram> flagships;
  private CrossCuttingDimensionTableDTO tableC;
  private List<DeliverableInfo> deliverableList;
  private List<PowbEvidencePlannedStudyDTO> flagshipPlannedList;

  // Parameter for tables E and F
  Double totalw1w2 = 0.0, totalw3Bilateral = 0.0, grandTotal = 0.0;
  // Managers
  private PowbExpectedCrpProgressManager powbExpectedCrpProgressManager;
  private PowbExpenditureAreasManager powbExpenditureAreasManager;
  private ProjectExpectedStudyManager projectExpectedStudyManager;
  private PowbSynthesisManager powbSynthesisManager;
  // RTF bytes
  private byte[] bytesRTF;

  // Streams
  InputStream inputStream;


  @Inject
  public POWBSummaryAction(APConfig config, GlobalUnitManager crpManager, PhaseManager phaseManager,
    PowbExpectedCrpProgressManager powbExpectedCrpProgressManager,
    PowbExpenditureAreasManager powbExpenditureAreasManager, PowbSynthesisManager powbSynthesisManager,
    ProjectExpectedStudyManager projectExpectedStudyManager) {
    super(config, crpManager, phaseManager);
    this.powbExpectedCrpProgressManager = powbExpectedCrpProgressManager;
    this.powbExpenditureAreasManager = powbExpenditureAreasManager;
    this.powbSynthesisManager = powbSynthesisManager;
    this.projectExpectedStudyManager = projectExpectedStudyManager;
  }

  /**
   * Method to add i8n parameters to masterReport in Pentaho
   * 
   * @param masterReport
   * @return masterReport with i8n parameters added
   */
  private MasterReport addi8nParameters(MasterReport masterReport) {
    masterReport.getParameterValues().put("i8nMainTitle", this.getText("summaries.powb.mainTitle"));
    masterReport.getParameterValues().put("i8nMainTitle2", this.getText("summaries.powb.mainTitle2"));
    masterReport.getParameterValues().put("i8ncoverPage", this.getText("summaries.powb.cover"));
    masterReport.getParameterValues().put("i8nHeaderTitle", this.getText("summaries.powb.header"));
    masterReport.getParameterValues().put("i8nUnitName", this.getText("summaries.powb.unitName"));
    masterReport.getParameterValues().put("i8nParticipantingCenters",
      this.getText("summaries.powb.participantingCenters"));


    masterReport.getParameterValues().put("i8nExpectedKeyResult", this.getText("summaries.powb.expectedKeyResults"));
    masterReport.getParameterValues().put("i8nAdjustmentsTitle", this.getText("summaries.powb.expectedKeyResults.toc"));
    masterReport.getParameterValues().put("i8nExpectedCrpTitle",
      this.getText("summaries.powb.expectedKeyResults.expectedCrp"));
    masterReport.getParameterValues().put("i8nEvidenceTitle",
      this.getText("summaries.powb.expectedKeyResults.evidence"));
    masterReport.getParameterValues().put("i8nPlansCRPFlagshipTitle",
      this.getText("summaries.powb.expectedKeyResults.flagshipPlans"));
    masterReport.getParameterValues().put("i8nCrossCuttingTitle",
      this.getText("summaries.powb.expectedKeyResults.crossCutting"));
    masterReport.getParameterValues().put("i8nCrossCuttingGenderTitle",
      this.getText("summaries.powb.expectedKeyResults.crossCutting.gender"));
    masterReport.getParameterValues().put("i8nCrossCuttingOpenDataTitle",
      this.getText("summaries.powb.expectedKeyResults.crossCutting.openData"));
    masterReport.getParameterValues().put("i8nEffectivenessandEfficiency",
      this.getText("summaries.powb.effectiveness"));
    masterReport.getParameterValues().put("i8nStaffingTitle", this.getText("summaries.powb.effectiveness.staffing"));
    masterReport.getParameterValues().put("i8nFinancialPlanTitle",
      this.getText("summaries.powb.effectiveness.financial"));
    masterReport.getParameterValues().put("i8nCollaborationandIntegrationTitle",
      this.getText("summaries.powb.effectiveness.collaboration"));
    masterReport.getParameterValues().put("i8nNewKeyExternalPartnershipsTitle",
      this.getText("summaries.powb.effectiveness.collaboration.external"));
    masterReport.getParameterValues().put("i8nNewContributionPlatformsTitle",
      this.getText("summaries.powb.effectiveness.collaboration.contributions"));
    masterReport.getParameterValues().put("i8nNewCrossCRPInteractionsTitle",
      this.getText("summaries.powb.effectiveness.collaboration.newCrossCrp"));
    masterReport.getParameterValues().put("i8nExpectedEffortsCountryCoordinationTitle",
      this.getText("summaries.powb.effectiveness.collaboration.expectedEfforts"));
    masterReport.getParameterValues().put("i8nCRPManagement", this.getText("summaries.powb.management"));
    masterReport.getParameterValues().put("i8nManagementRisksTitle", this.getText("summaries.powb.management.risk"));
    masterReport.getParameterValues().put("i8nCRPManagementGovernanceTitle",
      this.getText("summaries.powb.management.governance"));
    masterReport.getParameterValues().put("i8nTableATitle", this.getText("expectedProgress.tableA.title"));
    masterReport.getParameterValues().put("i8nTableAFPTitle", this.getText("expectedProgress.tableA.fp"));
    masterReport.getParameterValues().put("i8nTableASubIDOTitle", this.getText("expectedProgress.tableA.subIDO"));
    masterReport.getParameterValues().put("i8nTableAOutcomesTitle", this.getText("expectedProgress.tableA.outcomes"));
    masterReport.getParameterValues().put("i8nTableAMilestoneTitle", this.getText("expectedProgress.tableA.milestone"));
    masterReport.getParameterValues().put("i8nTableABudgetTitle", this.getText("summaries.powb.tableA.budget"));
    masterReport.getParameterValues().put("i8nTableAW1W2Title", this.getText("expectedProgress.tableA.w1w2"));
    masterReport.getParameterValues().put("i8nTableAW3BiTitle", this.getText("expectedProgress.tableA.w3bilateral"));
    masterReport.getParameterValues().put("i8nTableAAssessmentTitle",
      this.getText("expectedProgress.tableA.assessment"));
    masterReport.getParameterValues().put("i8nTableAVerificationTitle",
      this.getText("expectedProgress.tableA.meansVerification"));


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


  private TypedTableModel getExpectedKeyResultsTableModel() {
    TypedTableModel model = new TypedTableModel(
      new String[] {"unitName", "leadCenter", "participantingCenters", "adjustmentsDescription",
        "expectedCrpDescription", "evidenceDescription", "plansCRPFlagshipDescription", "crossCuttingGenderDescription",
        "crossCuttingOpenDataDescription", "staffingDescription", "financialPlanDescription",
        "newKeyExternalPartnershipsDescription", "newContributionPlatformsDescription",
        "newCrossCRPInteractionsDescription", "expectedEffortsCountryCoordinationDescription",
        "managementRisksTitleDescription", "CRPManagementGovernanceDescription"},
      new Class[] {String.class, String.class, String.class, String.class, String.class, String.class, String.class,
        String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class,
        String.class, String.class},
      0);
    String unitName = "&lt;Not Defined&gt;", leadCenter = " ", participantingCenters = "",
      adjustmentsDescription = "&lt;Not Defined&gt;", expectedCrpDescription = "&lt;Not Defined&gt;",
      evidenceDescription = "&lt;Not Defined&gt;", plansCRPFlagshipDescription = "",
      crossCuttingGenderDescription = "&lt;Not Defined&gt;", crossCuttingOpenDataDescription = "&lt;Not Defined&gt;";

    unitName = this.getLoggedCrp().getAcronym() != null && !this.getLoggedCrp().getAcronym().isEmpty()
      ? this.getLoggedCrp().getAcronym() : this.getLoggedCrp().getName();

    List<CrpPpaPartner> crpPpaPartnerList = this.getLoggedCrp().getCrpPpaPartners().stream()
      .filter(c -> c.isActive() && c.getPhase() != null && c.getPhase().equals(this.getSelectedPhase()))
      .collect(Collectors.toList());
    if (crpPpaPartnerList != null && !crpPpaPartnerList.isEmpty()) {
      for (CrpPpaPartner crpPpaPartner : crpPpaPartnerList) {
        if (participantingCenters.isEmpty()) {
          if (crpPpaPartner.getInstitution().getAcronym() != null
            && !crpPpaPartner.getInstitution().getAcronym().trim().isEmpty()) {
            participantingCenters = crpPpaPartner.getInstitution().getAcronym();
          } else {
            participantingCenters = crpPpaPartner.getInstitution().getName();
          }
        } else {
          if (crpPpaPartner.getInstitution().getAcronym() != null
            && !crpPpaPartner.getInstitution().getAcronym().trim().isEmpty()) {
            participantingCenters += ", " + crpPpaPartner.getInstitution().getAcronym();
          } else {
            participantingCenters += ", " + crpPpaPartner.getInstitution().getName();
          }
        }
      }
    }
    if (participantingCenters.isEmpty()) {
      participantingCenters = "&lt;Not Defined&gt;";
    }


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

    for (LiaisonInstitution liaisonInstitution : this.getFlagships()) {
      List<PowbSynthesis> powbSynthesisFL = powbSynthesisList.stream()
        .filter(p -> p.isActive() && p.getLiaisonInstitution().equals(liaisonInstitution)).collect(Collectors.toList());
      PowbSynthesis powbSynthesis = null;
      if (powbSynthesisFL != null && powbSynthesisFL.size() > 0) {
        powbSynthesis = powbSynthesisFL.get(0);
      }
      // Flagship Plan
      plansCRPFlagshipDescription =
        this.getFlagshipDescription(powbSynthesis, plansCRPFlagshipDescription, liaisonInstitution);
    }

    if (plansCRPFlagshipDescription.isEmpty()) {
      plansCRPFlagshipDescription = "&lt;Not Defined&gt;";
    }

    // Effectiveness
    String staffingDescription = "&lt;Not Defined&gt;", financialPlanDescription = "&lt;Not Defined&gt;",
      newKeyExternalPartnershipsDescription = "&lt;Not Defined&gt;",
      newContributionPlatformsDescription = "&lt;Not Defined&gt;",
      newCrossCRPInteractionsDescription = "&lt;Not Defined&gt;",
      expectedEffortsCountryCoordinationDescription = "&lt;Not Defined&gt;";

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
    }
    // Crp Management
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
    model.addRow(new Object[] {unitName, leadCenter, participantingCenters, adjustmentsDescription,
      expectedCrpDescription, evidenceDescription, plansCRPFlagshipDescription, crossCuttingGenderDescription,
      crossCuttingOpenDataDescription, staffingDescription, financialPlanDescription,
      newKeyExternalPartnershipsDescription, newContributionPlatformsDescription, newCrossCRPInteractionsDescription,
      expectedEffortsCountryCoordinationDescription, managementRisksTitleDescription,
      CRPManagementGovernanceDescription});
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


  private String getFlagshipDescription(PowbSynthesis powbSynthesis, String plansCRPFlagshipDescription,
    LiaisonInstitution liaisonInstitution) {
    String liaisonName = liaisonInstitution.getAcronym() != null && !liaisonInstitution.getAcronym().isEmpty()
      ? liaisonInstitution.getAcronym() : liaisonInstitution.getName();
    if (plansCRPFlagshipDescription.isEmpty()) {
      plansCRPFlagshipDescription = "<br> • " + liaisonName + ": ";
    } else {
      plansCRPFlagshipDescription += "<br> • " + liaisonName + ": ";
    }

    if (powbSynthesis != null && powbSynthesis.getPowbFlagshipPlans() != null) {
      if (powbSynthesis.getPowbFlagshipPlans().getPlanSummary() != null) {
        plansCRPFlagshipDescription += powbSynthesis.getPowbFlagshipPlans().getPlanSummary();
      }
      if (powbSynthesis.getPowbFlagshipPlans().getFlagshipProgramFile() != null) {
        plansCRPFlagshipDescription +=
          "<br> " + this.getText("plansByFlagship.tableOverall.attached") + ":  <font color=\"blue\"><u>"
            + this.getPowbPath(liaisonInstitution,
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


  public void getFpPlannedList(List<LiaisonInstitution> lInstitutions, long phaseID) {
    flagshipPlannedList = new ArrayList<>();

    if (projectExpectedStudyManager.findAll() != null) {
      List<ProjectExpectedStudy> expectedStudies = new ArrayList<>(projectExpectedStudyManager.findAll().stream()
        .filter(ps -> ps.isActive() && ps.getPhase().getId() == phaseID
          && ps.getProject().getGlobalUnitProjects().stream().filter(
            gup -> gup.isActive() && gup.isOrigin() && gup.getGlobalUnit().getId().equals(this.getLoggedCrp().getId()))
            .collect(Collectors.toList()).size() > 0)
        .collect(Collectors.toList()));

      for (ProjectExpectedStudy projectExpectedStudy : expectedStudies) {

        PowbEvidencePlannedStudyDTO dto = new PowbEvidencePlannedStudyDTO();
        dto.setProjectExpectedStudy(projectExpectedStudy);
        List<ProjectFocus> projectFocuses = new ArrayList<>(projectExpectedStudy.getProject().getProjectFocuses()
          .stream().filter(pf -> pf.isActive() && pf.getPhase().getId() == phaseID).collect(Collectors.toList()));
        List<LiaisonInstitution> liaisonInstitutions = new ArrayList<>();
        for (ProjectFocus projectFocus : projectFocuses) {
          liaisonInstitutions.addAll(projectFocus.getCrpProgram().getLiaisonInstitutions().stream()
            .filter(li -> li.isActive() && li.getCrpProgram() != null
              && li.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue())
            .collect(Collectors.toList()));
        }
        dto.setLiaisonInstitutions(liaisonInstitutions);
        flagshipPlannedList.add(dto);
      }

      List<PowbEvidencePlannedStudy> evidencePlannedStudies = new ArrayList<>();
      for (LiaisonInstitution liaisonInstitution : lInstitutions) {
        PowbSynthesis powbSynthesis = powbSynthesisManager.findSynthesis(phaseID, liaisonInstitution.getId());
        if (powbSynthesis != null) {
          if (powbSynthesis.getPowbEvidence() != null) {
            if (powbSynthesis.getPowbEvidence().getPowbEvidencePlannedStudies() != null) {
              List<PowbEvidencePlannedStudy> studies = new ArrayList<>(powbSynthesis.getPowbEvidence()
                .getPowbEvidencePlannedStudies().stream().filter(s -> s.isActive()).collect(Collectors.toList()));
              if (studies != null || !studies.isEmpty()) {
                for (PowbEvidencePlannedStudy powbEvidencePlannedStudy : studies) {
                  evidencePlannedStudies.add(powbEvidencePlannedStudy);
                }
              }
            }
          }
        }
      }

      List<Integer> removeList = new ArrayList<>();
      for (PowbEvidencePlannedStudy powbEvidencePlannedStudy : evidencePlannedStudies) {
        for (PowbEvidencePlannedStudyDTO dto : flagshipPlannedList) {
          int index = flagshipPlannedList.indexOf(dto);
          if (dto.getProjectExpectedStudy().equals(powbEvidencePlannedStudy.getProjectExpectedStudy())) {
            removeList.add(index);
          }
        }
      }

      for (Integer i : removeList) {
        flagshipPlannedList.remove(i);
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
    Double totalEstimatedPercentajeFS = 0.0;
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
          totalEstimatedPercentajeFS += estimatedPercentajeFS;
          model.addRow(new Object[] {expenditureArea, estimatedPercentajeFS / 100, commentsSpace});
        }
      }
    }
    if (model.getRowCount() > 0) {
      model
        .addRow(new Object[] {"Total Funding (Amount)", round((totalw1w2 * totalEstimatedPercentajeFS) / 100, 2), " "});
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

          model.addRow(new Object[] {FP, subIDO, outcomes, milestone, round(w1w2, 2), round(w3Bilateral, 2), assessment,
            meansVerifications});
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
    for (PowbEvidencePlannedStudyDTO powbEvidencePlannedStudyDTO : flagshipPlannedList.stream()
      .filter(p -> p.getProjectExpectedStudy() != null && p.getProjectExpectedStudy().getType() != null
        && (p.getProjectExpectedStudy().getType() == TypeExpectedStudiesEnum.OUTCOMECASESTUDY.getId()
          || p.getProjectExpectedStudy().getType() == TypeExpectedStudiesEnum.IMPACTASSESMENT.getId()
          || p.getProjectExpectedStudy().getType() == TypeExpectedStudiesEnum.ADOPTIONSTUDY.getId()))
      .collect(Collectors.toList())) {
      String plannedStudy = "", geographicScope = "", revelantSubIDO = "", comments = "";
      plannedStudy = powbEvidencePlannedStudyDTO.getProjectExpectedStudy().getTopicStudy() != null
        && !powbEvidencePlannedStudyDTO.getProjectExpectedStudy().getTopicStudy().trim().isEmpty()
          ? powbEvidencePlannedStudyDTO.getProjectExpectedStudy().getTopicStudy() : " ";

      geographicScope = powbEvidencePlannedStudyDTO.getProjectExpectedStudy().getScopeName() != null
        && !powbEvidencePlannedStudyDTO.getProjectExpectedStudy().getScopeName().trim().isEmpty()
          ? powbEvidencePlannedStudyDTO.getProjectExpectedStudy().getScopeName() : " ";
      if (powbEvidencePlannedStudyDTO.getProjectExpectedStudy().getSrfSubIdo() != null
        && powbEvidencePlannedStudyDTO.getProjectExpectedStudy().getSrfSubIdo().getDescription() != null
        && !powbEvidencePlannedStudyDTO.getProjectExpectedStudy().getSrfSubIdo().getDescription().trim().isEmpty()) {
        revelantSubIDO += "• " + powbEvidencePlannedStudyDTO.getProjectExpectedStudy().getSrfSubIdo().getDescription();
      }

      if (powbEvidencePlannedStudyDTO.getProjectExpectedStudy().getSrfSloIndicator() != null
        && powbEvidencePlannedStudyDTO.getProjectExpectedStudy().getSrfSloIndicator().getTitle() != null
        && !powbEvidencePlannedStudyDTO.getProjectExpectedStudy().getSrfSloIndicator().getTitle().trim().isEmpty()) {
        if (revelantSubIDO.isEmpty()) {
          revelantSubIDO +=
            "• " + powbEvidencePlannedStudyDTO.getProjectExpectedStudy().getSrfSloIndicator().getTitle();
        } else {
          revelantSubIDO +=
            "\n• " + powbEvidencePlannedStudyDTO.getProjectExpectedStudy().getSrfSloIndicator().getTitle();
        }
      }
      if (revelantSubIDO.isEmpty()) {
        revelantSubIDO = " ";
      }
      comments = powbEvidencePlannedStudyDTO.getProjectExpectedStudy().getComments() != null
        && !powbEvidencePlannedStudyDTO.getProjectExpectedStudy().getComments().trim().isEmpty()
          ? powbEvidencePlannedStudyDTO.getProjectExpectedStudy().getComments() : " ";

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
        totalw1w2 += w1w2;
        totalw3Bilateral += w3Bilateral;
        grandTotal += total;
        model.addRow(new Object[] {category, round(w1w2, 2), round(w3Bilateral, 2), round(total, 2), comments});
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
          totalw1w2 += w1w2;
          totalw3Bilateral += w3Bilateral;
          grandTotal += total;
          model.addRow(new Object[] {category, round(w1w2, 2), round(w3Bilateral, 2), round(total, 2), comments});
        }
      }
    }
    if (model.getRowCount() > 0) {
      model
        .addRow(new Object[] {"CRP Total", round(totalw1w2, 2), round(totalw3Bilateral, 2), round(grandTotal, 2), " "});
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
    this.getFpPlannedList(this.getFlagships(), this.getSelectedPhase().getId());
    for (PowbEvidencePlannedStudyDTO powbEvidencePlannedStudyDTO : flagshipPlannedList.stream()
      .filter(p -> p.getProjectExpectedStudy() != null && p.getProjectExpectedStudy().getType() != null
        && (p.getProjectExpectedStudy().getType() == TypeExpectedStudiesEnum.EVAULATION.getId()
          || p.getProjectExpectedStudy().getType() == TypeExpectedStudiesEnum.IMPACTASSESMENT.getId()
          || p.getProjectExpectedStudy().getType() == TypeExpectedStudiesEnum.LEARNING.getId()
          || p.getProjectExpectedStudy().getType() == TypeExpectedStudiesEnum.IMPACTCASESTUDY.getId()
          || p.getProjectExpectedStudy().getType() == TypeExpectedStudiesEnum.CRP_PTF.getId()
          || p.getProjectExpectedStudy().getType() == TypeExpectedStudiesEnum.REVIEW.getId()))
      .collect(Collectors.toList())) {
      String plannedStudy = "", comments = "";
      plannedStudy = powbEvidencePlannedStudyDTO.getProjectExpectedStudy().getTopicStudy() != null
        && !powbEvidencePlannedStudyDTO.getProjectExpectedStudy().getTopicStudy().trim().isEmpty()
          ? powbEvidencePlannedStudyDTO.getProjectExpectedStudy().getTopicStudy() : " ";
      comments = powbEvidencePlannedStudyDTO.getProjectExpectedStudy().getComments() != null
        && !powbEvidencePlannedStudyDTO.getProjectExpectedStudy().getComments().trim().isEmpty()
          ? powbEvidencePlannedStudyDTO.getProjectExpectedStudy().getComments() : " ";

      model.addRow(new Object[] {plannedStudy, comments});
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
  public void tableCInfo(Phase phase) {
    List<Deliverable> deliverables = new ArrayList<>();
    deliverableList = new ArrayList<>();
    int iGenderPrincipal = 0;
    int iGenderSignificant = 0;
    int iGenderNa = 0;
    int iYouthPrincipal = 0;
    int iYouthSignificant = 0;
    int iYouthNa = 0;
    int iCapDevPrincipal = 0;
    int iCapDevSignificant = 0;
    int iCapDevNa = 0;

    for (GlobalUnitProject globalUnitProject : this
      .getLoggedCrp().getGlobalUnitProjects().stream().filter(p -> p.isActive() && p.getProject() != null
        && p.getProject().isActive() && p.getProject().getProjecInfoPhase(phase) != null && p.getProject()
          .getProjectInfo().getStatus().intValue() == Integer.parseInt(ProjectStatusEnum.Ongoing.getStatusId()))
      .collect(Collectors.toList())) {

      for (Deliverable deliverable : globalUnitProject.getProject().getDeliverables().stream()
        .filter(d -> d.isActive() && d.getDeliverableInfo(phase) != null).collect(Collectors.toList())) {
        deliverables.add(deliverable);
      }

    }


    if (deliverables != null && !deliverables.isEmpty()) {
      for (Deliverable deliverable : deliverables) {
        DeliverableInfo deliverableInfo = deliverable.getDeliverableInfo(phase);
        if (deliverableInfo.isActive()) {

          boolean addDeliverable = false;

          if (deliverable.isActive() && deliverableInfo.getNewExpectedYear() != null
            && deliverableInfo.getNewExpectedYear() >= this.getActualPhase().getYear()
            && deliverableInfo.getStatus() != null
            && deliverableInfo.getStatus().intValue() == Integer.parseInt(ProjectStatusEnum.Extended.getStatusId())) {
            addDeliverable = true;
          }

          if (deliverable.isActive() && deliverableInfo.getYear() >= this.getActualPhase().getYear()
            && deliverableInfo.getStatus() != null
            && deliverableInfo.getStatus().intValue() == Integer.parseInt(ProjectStatusEnum.Ongoing.getStatusId())) {
            addDeliverable = true;
          }

          if (addDeliverable) {
            deliverableList.add(deliverableInfo);
            boolean bGender = false;
            boolean bYouth = false;
            boolean bCapDev = false;
            if (deliverableInfo.getCrossCuttingNa() != null && deliverableInfo.getCrossCuttingNa()) {
              iGenderNa++;
              iYouthNa++;
              iCapDevNa++;
            } else {
              // Gender
              if (deliverableInfo.getCrossCuttingGender() != null && deliverableInfo.getCrossCuttingGender()) {
                bGender = true;
                if (deliverableInfo.getCrossCuttingScoreGender() != null
                  && deliverableInfo.getCrossCuttingScoreGender() == 1) {
                  iGenderSignificant++;
                } else if (deliverableInfo.getCrossCuttingScoreGender() != null
                  && deliverableInfo.getCrossCuttingScoreGender() == 2) {
                  iGenderPrincipal++;
                } else if (deliverableInfo.getCrossCuttingScoreGender() == null) {
                  iGenderNa++;
                }
              }

              // Youth
              if (deliverableInfo.getCrossCuttingYouth() != null && deliverableInfo.getCrossCuttingYouth()) {
                bYouth = true;
                if (deliverableInfo.getCrossCuttingScoreYouth() != null
                  && deliverableInfo.getCrossCuttingScoreYouth() == 1) {
                  iYouthSignificant++;
                } else if (deliverableInfo.getCrossCuttingScoreYouth() != null
                  && deliverableInfo.getCrossCuttingScoreYouth() == 2) {
                  iYouthPrincipal++;
                } else if (deliverableInfo.getCrossCuttingScoreYouth() == null) {
                  iYouthNa++;
                }
              }

              // CapDev
              if (deliverableInfo.getCrossCuttingCapacity() != null && deliverableInfo.getCrossCuttingCapacity()) {
                bCapDev = true;
                if (deliverableInfo.getCrossCuttingScoreCapacity() != null
                  && deliverableInfo.getCrossCuttingScoreCapacity() == 1) {
                  iCapDevSignificant++;
                } else if (deliverableInfo.getCrossCuttingScoreCapacity() != null
                  && deliverableInfo.getCrossCuttingScoreCapacity() == 2) {
                  iCapDevPrincipal++;
                } else if (deliverableInfo.getCrossCuttingScoreCapacity() == null) {
                  iCapDevNa++;
                }
              }

              if (!bGender) {
                iGenderNa++;
              }
              if (!bYouth) {
                iYouthNa++;
              }
              if (!bCapDev) {
                iCapDevNa++;
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
      double dGenderNa = (iGenderNa * 100.0) / iDeliverableCount;
      double dYouthPrincipal = (iYouthPrincipal * 100.0) / iDeliverableCount;
      double dYouthSignificant = (iYouthSignificant * 100.0) / iDeliverableCount;
      double dYouthNa = (iYouthNa * 100.0) / iDeliverableCount;
      double dCapDevPrincipal = (iCapDevPrincipal * 100.0) / iDeliverableCount;
      double dCapDevSignificant = (iCapDevSignificant * 100.0) / iDeliverableCount;
      double dCapDevNa = (iCapDevNa * 100.0) / iDeliverableCount;


      // Gender
      tableC.setGenderPrincipal(iGenderPrincipal);
      tableC.setGenderSignificant(iGenderSignificant);
      tableC.setGenderScored(iGenderNa);

      tableC.setPercentageGenderPrincipal(dGenderPrincipal);
      tableC.setPercentageGenderSignificant(dGenderSignificant);
      tableC.setPercentageGenderNotScored(dGenderNa);
      // Youth
      tableC.setYouthPrincipal(iYouthPrincipal);
      tableC.setYouthSignificant(iYouthSignificant);
      tableC.setYouthScored(iYouthNa);

      tableC.setPercentageYouthPrincipal(dYouthPrincipal);
      tableC.setPercentageYouthSignificant(dYouthSignificant);
      tableC.setPercentageYouthNotScored(dYouthNa);
      // CapDev
      tableC.setCapDevPrincipal(iCapDevPrincipal);
      tableC.setCapDevSignificant(iCapDevSignificant);
      tableC.setCapDevScored(iCapDevNa);

      tableC.setPercentageCapDevPrincipal(dCapDevPrincipal);
      tableC.setPercentageCapDevSignificant(dCapDevSignificant);
      tableC.setPercentageCapDevNotScored(dCapDevNa);


    }

  }

}