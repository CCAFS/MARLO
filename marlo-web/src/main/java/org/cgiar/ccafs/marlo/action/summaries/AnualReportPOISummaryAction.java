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

import org.cgiar.ccafs.marlo.data.manager.CrpProgramManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableIntellectualAssetManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.PowbExpenditureAreasManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyInfoManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndSynthesisIndicatorManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisCrossCgiarCollaborationManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisCrossCgiarManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisCrossCuttingDimensionManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisCrpProgressTargetManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisExternalPartnershipManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisFinancialSummaryBudgetManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisFlagshipProgressManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisFlagshipProgressMilestoneManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisFundingUseExpendituryAreaManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisIndicatorManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisMeliaEvaluationManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisMeliaManager;
import org.cgiar.ccafs.marlo.data.manager.SrfSloIndicatorTargetManager;
import org.cgiar.ccafs.marlo.data.model.CrossCuttingDimensionTableDTO;
import org.cgiar.ccafs.marlo.data.model.CrpMilestone;
import org.cgiar.ccafs.marlo.data.model.CrpOutcomeSubIdo;
import org.cgiar.ccafs.marlo.data.model.CrpPpaPartner;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.CrpProgramOutcome;
import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.data.model.DeliverableInfo;
import org.cgiar.ccafs.marlo.data.model.DeliverableIntellectualAsset;
import org.cgiar.ccafs.marlo.data.model.GlobalUnitProject;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.PowbEvidencePlannedStudyDTO;
import org.cgiar.ccafs.marlo.data.model.PowbExpenditureAreas;
import org.cgiar.ccafs.marlo.data.model.ProgramType;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectBudgetsFlagship;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyInfo;
import org.cgiar.ccafs.marlo.data.model.ProjectFocus;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovation;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationInfo;
import org.cgiar.ccafs.marlo.data.model.ProjectStatusEnum;
import org.cgiar.ccafs.marlo.data.model.RepIndSynthesisIndicator;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesis;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisCrossCgiar;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisCrossCgiarCollaboration;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisCrossCuttingDimension;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisCrpProgressTarget;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisExternalPartnership;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisExternalPartnershipDTO;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFinancialSummary;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFinancialSummaryBudget;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgress;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgressMilestone;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFundingUseExpendituryArea;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFundingUseSummary;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisGovernance;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisIndicator;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisMelia;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisMeliaEvaluation;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisProgramVariance;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisRisk;
import org.cgiar.ccafs.marlo.data.model.SrfSloIndicatorTarget;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.POIField;
import org.cgiar.ccafs.marlo.utils.POISummary;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.poi.xwpf.model.XWPFHeaderFooterPolicy;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBody;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDocument1;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageSz;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STHdrFtr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STPageOrientation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AnualReportPOISummaryAction extends BaseSummariesAction implements Summary {

  private static final long serialVersionUID = 2828551630719082089L;
  // private static final String ProgramType = null;
  private static Logger LOG = LoggerFactory.getLogger(AnualReportPOISummaryAction.class);

  public static double round(double value, int places) {
    if (places < 0) {
      throw new IllegalArgumentException();
    }

    BigDecimal bd = new BigDecimal(value);
    bd = bd.setScale(places, RoundingMode.HALF_UP);
    return bd.doubleValue();
  }

  // Managers
  private CrpProgramManager crpProgramManager;
  private PowbExpenditureAreasManager powbExpenditureAreasManager;
  private ReportSynthesisManager reportSynthesisManager;
  private ReportSynthesisCrpProgressTargetManager reportSynthesisCrpProgressTargetManager;
  private RepIndSynthesisIndicatorManager repIndSynthesisIndicatorManager;
  private ProjectExpectedStudyInfoManager projectExpectedStudyInfoManager;
  private ReportSynthesisFundingUseExpendituryAreaManager reportSynthesisFundingUseExpendituryAreaManager;
  private ProjectInnovationManager projectInnovationManager;
  private ReportSynthesisCrossCgiarManager reportSynthesisCrossCgiarManager;
  private DeliverableIntellectualAssetManager deliverableIntellectualAssetManager;
  private ReportSynthesisExternalPartnershipManager reportSynthesisExternalPartnershipManager;
  private ReportSynthesisMeliaManager reportSynthesisMeliaManager;
  private ReportSynthesisCrossCgiarCollaborationManager reportSynthesisCrossCgiarCollaborationManager;
  private ReportSynthesisMeliaEvaluationManager reportSynthesisMeliaEvaluationManager;
  private ReportSynthesisFlagshipProgressManager reportSynthesisFlagshipProgressManager;
  private ReportSynthesisFinancialSummaryBudgetManager reportSynthesisFinancialSummaryBudgetManager;
  private ReportSynthesisFlagshipProgressMilestoneManager reportSynthesisFlagshipProgressMilestoneManager;
  private ReportSynthesisIndicatorManager reportSynthesisIndicatorManager;
  private ReportSynthesisCrossCuttingDimensionManager reportSynthesisCrossCuttingDimensionManager;

  // Parameters
  private POISummary poiSummary;
  private List<ReportSynthesis> reportSysthesisList;
  private LiaisonInstitution pmuInstitution;
  private ReportSynthesis reportSynthesisPMU;
  private ReportSynthesis reportSynthesis;
  private long startTime;
  private XWPFDocument document;
  private List<DeliverableInfo> deliverableList;
  private CrossCuttingDimensionTableDTO tableC;
  private NumberFormat currencyFormat;
  private DecimalFormat percentageFormat;
  private List<CrpProgram> flagships;

  private List<PowbEvidencePlannedStudyDTO> flagshipPlannedList;
  private List<SrfSloIndicatorTarget> sloTargets;
  private SrfSloIndicatorTargetManager srfSloIndicatorTargetManager;
  private List<DeliverableIntellectualAsset> assetsList;
  private List<ReportSynthesisExternalPartnershipDTO> flagshipExternalPlannedList;
  private List<LiaisonInstitution> liaisonInstitutions;
  private List<ReportSynthesisMelia> reportSynthesisMeliaList;
  private LiaisonInstitution liaisonInstitution;


  Double totalw1w2 = 0.0, totalw1w2Planned = 0.0, totalCenter = 0.0, grandTotal = 0.0, totalw1w2Actual = 0.0,
    totalW3Actual = 0.0, totalW3Bilateral = 0.0, totalW3Planned = 0.0, grandTotalPlanned = 0.0, grandTotalActual = 0.0;
  // Streams
  private InputStream inputStream;

  // DOC bytes
  private byte[] bytesDOC;

  public AnualReportPOISummaryAction(APConfig config, GlobalUnitManager crpManager, PhaseManager phaseManager,
    PowbExpenditureAreasManager powbExpenditureAreasManager, ReportSynthesisManager reportSynthesisManager,
    SrfSloIndicatorTargetManager srfSloIndicatorTargetManager,
    ReportSynthesisCrpProgressTargetManager reportSynthesisCrpProgressTargetManager,
    RepIndSynthesisIndicatorManager repIndSynthesisIndicatorManager,
    ProjectExpectedStudyInfoManager projectExpectedStudyInfoManager,
    ReportSynthesisFundingUseExpendituryAreaManager reportSynthesisFundingUseExpendituryAreaManager,
    ProjectInnovationManager projectInnovationManager, ProjectManager projectManager,
    ReportSynthesisCrossCgiarManager reportSynthesisCrossCgiarManager,
    DeliverableIntellectualAssetManager deliverableIntellectualAssetManager,
    ReportSynthesisExternalPartnershipManager reportSynthesisExternalPartnershipManager,
    ReportSynthesisMeliaManager reportSynthesisMeliaManager,
    ReportSynthesisCrossCgiarCollaborationManager reportSynthesisCrossCgiarCollaborationManager,
    ReportSynthesisMeliaEvaluationManager reportSynthesisMeliaEvaluationManager, CrpProgramManager crpProgramManager,
    ReportSynthesisFlagshipProgressManager reportSynthesisFlagshipProgressManager,
    ReportSynthesisFinancialSummaryBudgetManager reportSynthesisFinancialSummaryBudgetManager,
    ReportSynthesisFlagshipProgressMilestoneManager reportSynthesisFlagshipProgressMilestoneManager,
    ReportSynthesisIndicatorManager reportSynthesisIndicatorManager,
    ReportSynthesisCrossCuttingDimensionManager reportSynthesisCrossCuttingDimensionManager) {

    super(config, crpManager, phaseManager, projectManager);
    document = new XWPFDocument();
    poiSummary = new POISummary();
    currencyFormat = NumberFormat.getCurrencyInstance();
    percentageFormat = new DecimalFormat("##.##%");
    this.powbExpenditureAreasManager = powbExpenditureAreasManager;
    this.reportSynthesisManager = reportSynthesisManager;
    this.srfSloIndicatorTargetManager = srfSloIndicatorTargetManager;
    this.reportSynthesisCrpProgressTargetManager = reportSynthesisCrpProgressTargetManager;
    this.repIndSynthesisIndicatorManager = repIndSynthesisIndicatorManager;
    this.projectExpectedStudyInfoManager = projectExpectedStudyInfoManager;
    this.reportSynthesisFundingUseExpendituryAreaManager = reportSynthesisFundingUseExpendituryAreaManager;
    this.projectInnovationManager = projectInnovationManager;
    this.reportSynthesisCrossCgiarManager = reportSynthesisCrossCgiarManager;
    this.deliverableIntellectualAssetManager = deliverableIntellectualAssetManager;
    this.reportSynthesisExternalPartnershipManager = reportSynthesisExternalPartnershipManager;
    this.reportSynthesisMeliaManager = reportSynthesisMeliaManager;
    this.reportSynthesisCrossCgiarCollaborationManager = reportSynthesisCrossCgiarCollaborationManager;
    this.reportSynthesisMeliaEvaluationManager = reportSynthesisMeliaEvaluationManager;
    this.crpProgramManager = crpProgramManager;
    this.reportSynthesisFlagshipProgressManager = reportSynthesisFlagshipProgressManager;
    this.reportSynthesisFinancialSummaryBudgetManager = reportSynthesisFinancialSummaryBudgetManager;
    this.reportSynthesisFlagshipProgressMilestoneManager = reportSynthesisFlagshipProgressMilestoneManager;
    this.reportSynthesisIndicatorManager = reportSynthesisIndicatorManager;
    this.reportSynthesisCrossCuttingDimensionManager = reportSynthesisCrossCuttingDimensionManager;
  }

  private void addAdjustmentDescription() {
    List<ReportSynthesisFlagshipProgress> reportSynthesisFlagshipProgressManagerList = null;
    reportSynthesisFlagshipProgressManagerList = reportSynthesisFlagshipProgressManager.findAll();

    if (reportSynthesisFlagshipProgressManagerList != null && !reportSynthesisFlagshipProgressManagerList.isEmpty()) {

      for (int i = 0; i < reportSynthesisFlagshipProgressManagerList.size(); i++) {
        String summary = "";
        if (reportSynthesisFlagshipProgressManagerList.get(i).getSummary() != null
          && !reportSynthesisFlagshipProgressManagerList.get(i).getSummary().isEmpty()) {
          summary = reportSynthesisFlagshipProgressManagerList.get(i).getSummary() + "\n";
          poiSummary.textParagraph(document.createParagraph(), summary);

        }
      }
    }
  }

  private void addCrossCuttingCapacityDevelopment() {

    String crossCuttingCapacityDevelopment = "";

    if (reportSynthesisPMU != null) {

      if (reportSynthesisPMU.getReportSynthesisCrossCuttingDimension() != null) {
        ReportSynthesisCrossCuttingDimension crossCutting =
          reportSynthesisPMU.getReportSynthesisCrossCuttingDimension();
        if (crossCutting != null) {
          crossCuttingCapacityDevelopment = crossCutting.getCapDev();
        }
      }
    }

    if (crossCuttingCapacityDevelopment != null && !crossCuttingCapacityDevelopment.isEmpty()) {
      poiSummary.textParagraph(document.createParagraph(), crossCuttingCapacityDevelopment);
    }


  }

  private void addCrossCuttingGender() {

    String crossCuttingGenderDescription = "";
    String crossCuttingGenderLessons = "";
    if (reportSynthesisPMU != null) {

      // Cross Cutting Gender Info
      if (reportSynthesisPMU.getReportSynthesisCrossCuttingDimension() != null) {
        ReportSynthesisCrossCuttingDimension crossCutting =
          reportSynthesisPMU.getReportSynthesisCrossCuttingDimension();
        if (crossCutting != null) {
          crossCuttingGenderDescription = crossCutting.getGenderDescription();
          crossCuttingGenderLessons = crossCutting.getGenderLessons();
        }
      }
    }

    if (crossCuttingGenderDescription != null && !crossCuttingGenderDescription.isEmpty()) {
      poiSummary.textParagraph(document.createParagraph(), crossCuttingGenderDescription);
    }

    if (crossCuttingGenderLessons != null && !crossCuttingGenderLessons.isEmpty()) {
      poiSummary.textParagraph(document.createParagraph(), crossCuttingGenderLessons);
    }

  }

  private void addCrossCuttingIntellectualAssets() {

    String crossCuttingIntellectualAssets = "";

    if (reportSynthesisPMU != null) {

      if (reportSynthesisPMU.getReportSynthesisCrossCuttingDimension() != null) {
        ReportSynthesisCrossCuttingDimension crossCutting =
          reportSynthesisPMU.getReportSynthesisCrossCuttingDimension();
        if (crossCutting != null) {
          crossCuttingIntellectualAssets = crossCutting.getIntellectualAssets();
        }
      }
    }

    if (crossCuttingIntellectualAssets != null && !crossCuttingIntellectualAssets.isEmpty()) {
      poiSummary.textParagraph(document.createParagraph(), crossCuttingIntellectualAssets);
    }

  }

  private void addCrossCuttingOpenData() {

    String crossCuttingOpenData = "";
    if (reportSynthesisPMU != null) {

      // Cross Cutting Gender Info
      if (reportSynthesisPMU.getReportSynthesisCrossCuttingDimension() != null) {
        ReportSynthesisCrossCuttingDimension crossCutting =
          reportSynthesisPMU.getReportSynthesisCrossCuttingDimension();
        if (crossCutting != null) {
          crossCuttingOpenData = crossCutting.getOpenData();
        }
      }
    }

    if (crossCuttingOpenData != null && !crossCuttingOpenData.isEmpty()) {
      poiSummary.textParagraph(document.createParagraph(), crossCuttingOpenData);
    }

  }


  private void addCrossCuttingOtherAspects() {

    String crossCuttingOtherAspects = "";

    if (reportSynthesisPMU != null) {

      if (reportSynthesisPMU.getReportSynthesisCrossCuttingDimension() != null) {
        ReportSynthesisCrossCuttingDimension crossCutting =
          reportSynthesisPMU.getReportSynthesisCrossCuttingDimension();
        if (crossCutting != null) {
          crossCuttingOtherAspects = crossCutting.getOtherAspects();
        }
      }
    }

    if (crossCuttingOtherAspects != null && !crossCuttingOtherAspects.isEmpty()) {
      poiSummary.textParagraph(document.createParagraph(), crossCuttingOtherAspects);
    }

  }

  private void addCrossCuttingYouth() {

    String crossCuttingYouthDescription = "";
    String crossCuttingYouthLessons = "";
    if (reportSynthesisPMU != null) {

      // Cross Cutting Gender Info
      if (reportSynthesisPMU.getReportSynthesisCrossCuttingDimension() != null) {
        ReportSynthesisCrossCuttingDimension crossCutting =
          reportSynthesisPMU.getReportSynthesisCrossCuttingDimension();
        if (crossCutting != null) {
          crossCuttingYouthDescription = crossCutting.getYouthDescription();
          crossCuttingYouthLessons = crossCutting.getYouthLessons();
        }
      }
    }

    if (crossCuttingYouthDescription != null && !crossCuttingYouthDescription.isEmpty()) {
      poiSummary.textParagraph(document.createParagraph(), crossCuttingYouthDescription);
    }

    if (crossCuttingYouthLessons != null && !crossCuttingYouthLessons.isEmpty()) {
      poiSummary.textParagraph(document.createParagraph(), crossCuttingYouthLessons);
    }

  }

  private void addCrossPartnerships() {
    List<ReportSynthesisCrossCgiar> reportSynthesisCrossCgiarList = reportSynthesisCrossCgiarManager.findAll();

    String highlights = "";
    if (reportSynthesisCrossCgiarList != null) {
      for (int i = 0; i < reportSynthesisCrossCgiarList.size(); i++) {
        if (reportSynthesisCrossCgiarList.get(i).getHighlights() == null) {
          highlights += "";
        } else {
          highlights += reportSynthesisCrossCgiarList.get(i).getHighlights();
        }
      }
      poiSummary.textParagraph(document.createParagraph(), highlights);
    }
  }

  private void addExpectedCrp() {

    if (reportSynthesisPMU != null) {
      String synthesisCrpDescription = "";

      synthesisCrpDescription = reportSynthesisPMU.getReportSynthesisCrpProgress().getOverallProgress();
      if (synthesisCrpDescription != null) {
        poiSummary.textParagraph(document.createParagraph(), synthesisCrpDescription);
      }
    }
  }

  private void addExternalPartnerships() {

    String keyExternal = "";

    if (reportSynthesisPMU != null) {

      if (reportSynthesisPMU.getReportSynthesisExternalPartnership() != null) {
        ReportSynthesisExternalPartnership externalPartnership =
          reportSynthesisPMU.getReportSynthesisExternalPartnership();
        if (externalPartnership != null) {
          keyExternal = externalPartnership.getHighlights();
        }
      }
    }

    if (keyExternal != null && !keyExternal.isEmpty()) {
      poiSummary.textParagraph(document.createParagraph(), keyExternal);
    }

  }

  private void addFinancialSummary() {

    String financialSummaryNarrative = "";

    if (reportSynthesisPMU != null) {

      if (reportSynthesisPMU.getReportSynthesisFinancialSummary() != null) {
        ReportSynthesisFinancialSummary financialSummary = reportSynthesisPMU.getReportSynthesisFinancialSummary();
        if (financialSummary != null) {
          financialSummaryNarrative = financialSummary.getNarrative();
        }
      }
    }

    if (financialSummaryNarrative != null && !financialSummaryNarrative.isEmpty()) {
      poiSummary.textParagraph(document.createParagraph(), financialSummaryNarrative);
    }
  }

  private void addFundingSummarize() {
    String brieflySummarize = "";

    if (reportSynthesisPMU != null) {

      if (reportSynthesisPMU.getReportSynthesisFundingUseSummary() != null) {
        ReportSynthesisFundingUseSummary funding = reportSynthesisPMU.getReportSynthesisFundingUseSummary();
        if (funding != null) {
          brieflySummarize = funding.getMainArea();
        }
      }
    }

    if (brieflySummarize != null && !brieflySummarize.isEmpty()) {
      poiSummary.textParagraph(document.createParagraph(), brieflySummarize);
    }
  }

  private void addImprovingEfficiency() {

    String improvingEfficiencyDescription = "";

    if (reportSynthesisPMU != null) {

      if (reportSynthesisPMU.getReportSynthesisFundingUseSummary() != null) {
        ReportSynthesisFundingUseSummary funding = reportSynthesisPMU.getReportSynthesisFundingUseSummary();
        if (funding != null) {
          improvingEfficiencyDescription = funding.getMainArea();
        }
      }
    }

    if (improvingEfficiencyDescription != null && !improvingEfficiencyDescription.isEmpty()) {
      poiSummary.textParagraph(document.createParagraph(), improvingEfficiencyDescription);
    }
  }

  private void addManagement() {
    poiSummary.textHead2Title(document.createParagraph(), this.getText("summaries.annualReport.management.governance"));
    this.addManagementGovernance();
    // poiSummary.textParagraph(document.createParagraph(), CRPManagementGovernanceDescription);

    poiSummary.textHead2Title(document.createParagraph(), this.getText("summaries.annualReport.management.risk"));
    this.addManagementRisks();

    // poiSummary.textParagraph(document.createParagraph(), managementRisksTitleDescription);
    poiSummary.textHead2Title(document.createParagraph(), this.getText("summaries.annualReport.management.financial"));
    this.addFinancialSummary();
  }

  private void addManagementGovernance() {

    String managementGovernanceDescription = "";

    if (reportSynthesisPMU != null) {

      if (reportSynthesisPMU.getReportSynthesisGovernance() != null) {
        ReportSynthesisGovernance governance = reportSynthesisPMU.getReportSynthesisGovernance();
        if (governance != null) {
          managementGovernanceDescription = governance.getDescription();
        }
      }
    }

    if (managementGovernanceDescription != null && !managementGovernanceDescription.isEmpty()) {
      poiSummary.textParagraph(document.createParagraph(), managementGovernanceDescription);
    }
  }

  private void addManagementRisks() {

    String managementRiskBrief = "";

    if (reportSynthesisPMU != null) {

      if (reportSynthesisPMU.getReportSynthesisRisk() != null) {
        ReportSynthesisRisk risk = reportSynthesisPMU.getReportSynthesisRisk();
        if (risk != null) {
          managementRiskBrief = risk.getBriefSummary();
        }
      }
    }

    if (managementRiskBrief != null && !managementRiskBrief.isEmpty()) {
      poiSummary.textParagraph(document.createParagraph(), managementRiskBrief);
    }
  }

  private void addParticipatingCenters() {
    String participantingCenters = "";
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
    poiSummary.textParagraph(document.createParagraph(),
      this.getText("summaries.annualReport.participantingCenters") + ": " + participantingCenters + ":");
  }

  public void addReportSynthesisMelia() {
    reportSynthesisMeliaList = reportSynthesisMeliaManager.findAll();
    String studies = "";
    if (reportSynthesisMeliaList != null && !reportSynthesisMeliaList.isEmpty()) {
      for (int i = 0; i < reportSynthesisMeliaList.size(); i++) {
        if (reportSynthesisMeliaList.get(i).getSummary() != null) {
          studies += reportSynthesisMeliaList.get(i).getSummary() + "\n";
        }
      }
    }
    if (studies != null && !studies.isEmpty()) {
      poiSummary.textParagraph(document.createParagraph(), studies);
    }
  }

  private void addVariancePlanned() {

    String variancePlanned = "";

    if (reportSynthesisPMU != null) {

      if (reportSynthesisPMU.getReportSynthesisProgramVariance() != null) {
        ReportSynthesisProgramVariance programVariance = reportSynthesisPMU.getReportSynthesisProgramVariance();
        if (programVariance != null) {
          variancePlanned = programVariance.getDescription();
        }
      }
    }

    if (variancePlanned != null && !variancePlanned.isEmpty()) {
      poiSummary.textParagraph(document.createParagraph(), variancePlanned);
    }

  }

  public void createPageFooter() {
    CTP ctp = CTP.Factory.newInstance();

    // this add page number incremental
    ctp.addNewR().addNewPgNum();

    XWPFParagraph codePara = new XWPFParagraph(ctp, document);
    XWPFParagraph[] paragraphs = new XWPFParagraph[1];
    paragraphs[0] = codePara;

    // position of number
    codePara.setAlignment(ParagraphAlignment.CENTER);

    CTSectPr sectPr = document.getDocument().getBody().addNewSectPr();

    try {
      XWPFHeaderFooterPolicy headerFooterPolicy = new XWPFHeaderFooterPolicy(document, sectPr);
      headerFooterPolicy.createFooter(STHdrFtr.DEFAULT, paragraphs);
    } catch (IOException e) {
      LOG.error("Failed to createFooter. Exception: " + e.getMessage());
    }
  }


  public void createTableA1() {
    List<List<POIField>> headers = new ArrayList<>();

    POIField[] sHeader = {
      new POIField(this.getText("annualReport.crpProgress.selectSLOTarget") + "\n"
        + this.getText("summaries.annualReport.tableA1.targetTitle2"), ParagraphAlignment.LEFT),
      new POIField(this.getText("annualReport.crpProgress.summaryNewEvidence.readText"), ParagraphAlignment.LEFT),
      new POIField(this.getText("annualReport.crpProgress.additionalContribution"), ParagraphAlignment.LEFT),};

    List<POIField> header = Arrays.asList(sHeader);
    headers.add(header);
    String sloTarget = "", briefSummaries = "", additionalContribution = "", targetsIndicator = "";

    /*
     * Get all crp Progress Targets and compare the slo indicador Target id with the actual slotarget id
     */

    List<List<POIField>> datas = new ArrayList<>();
    List<POIField> data;
    if (!sloTargets.isEmpty() || sloTargets != null) {

      data = new ArrayList<>();
      List<ReportSynthesisCrpProgressTarget> listCrpProgressTargets = null;
      if (reportSynthesisCrpProgressTargetManager.findAll() != null) {
        listCrpProgressTargets = reportSynthesisCrpProgressTargetManager.findAll();
      }

      for (int i = 0; i < sloTargets.size(); i++) {

        if (sloTargets.get(i).getTargetsIndicator() != null && !sloTargets.get(i).getTargetsIndicator().isEmpty()) {
          targetsIndicator = sloTargets.get(i).getTargetsIndicator();
        }
        sloTarget = targetsIndicator + " " + sloTargets.get(i).getNarrative();
        String synthesisCrpBriefSummaries = "";
        String synthesisCrpTargets = "";

        if (listCrpProgressTargets != null) {
          for (int j = 0; j < listCrpProgressTargets.size(); j++) {
            if (listCrpProgressTargets.get(j).getSrfSloIndicatorTarget().getId() == sloTargets.get(i).getId()
              && listCrpProgressTargets.get(j).getSrfSloIndicatorTarget().isActive() == true) {
              synthesisCrpBriefSummaries += listCrpProgressTargets.get(j).getBirefSummary() + "\n";
              synthesisCrpTargets += listCrpProgressTargets.get(j).getAdditionalContribution() + "\n";
            }
          }
        }

        briefSummaries = synthesisCrpBriefSummaries;
        additionalContribution = synthesisCrpTargets;

        Boolean bold = false;
        String blackColor = "000000";
        String blueColor = "000099";
        POIField[] sData = {new POIField(sloTarget, ParagraphAlignment.LEFT, bold, blackColor),
          new POIField(briefSummaries, ParagraphAlignment.LEFT, bold, blueColor),
          new POIField(additionalContribution, ParagraphAlignment.LEFT, bold, blueColor)};
        data = Arrays.asList(sData);
        datas.add(data);
      }
    }

    poiSummary.textTable(document, headers, datas, true, "tableA1AnnualReport");
  }

  private void createTableA2() {

    /*
     * Get all Progress Expected study info and compare the actual phase
     */

    List<List<POIField>> headers = new ArrayList<>();

    POIField[] sHeader = {new POIField(this.getText("summaries.annualReport.tableA2.field1"), ParagraphAlignment.LEFT),
      new POIField(this.getText("summaries.annualReport.tableA2.field2"), ParagraphAlignment.LEFT),
      new POIField(this.getText("summaries.annualReport.tableA2.field3"), ParagraphAlignment.LEFT),
      new POIField(this.getText("summaries.annualReport.tableA2.field4"), ParagraphAlignment.LEFT)};

    List<POIField> header = Arrays.asList(sHeader);
    headers.add(header);
    String title = "", subIdo = "", describeGender = "", describeYouth = "", describeCapDev = "", additional = "",
      link = "";

    List<List<POIField>> datas = new ArrayList<>();

    List<POIField> data;
    // Get liaison institution list
    List<LiaisonInstitution> liaisonInstitutionsList =
      new ArrayList<>(this.getLoggedCrp().getLiaisonInstitutions().stream()
        .filter(c -> c.getCrpProgram() != null && c.isActive()
          && c.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue())
        .collect(Collectors.toList()));
    liaisonInstitutionsList.sort(Comparator.comparing(LiaisonInstitution::getAcronym));
    flagshipPlannedList = reportSynthesisMeliaManager.getMeliaPlannedList(liaisonInstitutionsList,
      this.getSelectedPhase().getId(), this.getLoggedCrp(), this.pmuInstitution);
    if (flagshipPlannedList != null && !flagshipPlannedList.isEmpty()) {
      for (int i = 0; i < flagshipPlannedList.size(); i++) {
        title = "";
        subIdo = "";
        describeGender = "";
        describeYouth = "";
        describeCapDev = "";
        additional = "";
        link = "";

        /** creating download link **/
        String year = flagshipPlannedList.get(i).getProjectExpectedStudy().getYear() + "";
        String cycle = this.getCurrentCycle();
        String study = flagshipPlannedList.get(i).getProjectExpectedStudy().getId() + "";
        link = "/studyID=" + study + "&cycle=" + cycle + "&year=" + year;

        if (flagshipPlannedList.get(i).getProjectExpectedStudy().getProjectExpectedStudyInfo() != null) {
          ProjectExpectedStudyInfo projectExpectedStudyInfo =
            flagshipPlannedList.get(i).getProjectExpectedStudy().getProjectExpectedStudyInfo();
          data = new ArrayList<>();
          title = projectExpectedStudyInfo.getTitle();
          if (flagshipPlannedList.get(i).getProjectExpectedStudy().getSubIdos() != null
            && !flagshipPlannedList.get(i).getProjectExpectedStudy().getSubIdos().isEmpty()) {
            for (int j = 0; j < flagshipPlannedList.get(i).getProjectExpectedStudy().getSubIdos().size(); j++) {
              subIdo += "\n •" + flagshipPlannedList.get(i).getProjectExpectedStudy().getSubIdos().get(j).getSrfSubIdo()
                .getDescription();
            }
          }
          if (projectExpectedStudyInfo.getGenderLevel() != null) {
            if (projectExpectedStudyInfo.getDescribeGender() == null) {
              describeGender = projectExpectedStudyInfo.getGenderLevel().getName() + "\n"
                + projectExpectedStudyInfo.getGenderLevel().getDefinition();
            } else {
              describeGender = projectExpectedStudyInfo.getGenderLevel().getName() + "\n"
                + projectExpectedStudyInfo.getGenderLevel().getDefinition() + "\n"
                + projectExpectedStudyInfo.getDescribeGender();
            }
          }
          if (projectExpectedStudyInfo.getYouthLevel() != null) {
            if (projectExpectedStudyInfo.getDescribeYouth() == null) {
              describeYouth = projectExpectedStudyInfo.getYouthLevel().getName() + "\n"
                + projectExpectedStudyInfo.getYouthLevel().getDefinition();
            } else {
              describeYouth = projectExpectedStudyInfo.getYouthLevel().getName() + "\n"
                + projectExpectedStudyInfo.getYouthLevel().getDefinition() + "\n"
                + projectExpectedStudyInfo.getDescribeYouth();
            }
          }
          if (projectExpectedStudyInfo.getCapdevLevel() != null) {
            if (projectExpectedStudyInfo.getDescribeCapdev() == null) {
              describeCapDev = projectExpectedStudyInfo.getCapdevLevel().getName() + "\n"
                + projectExpectedStudyInfo.getCapdevLevel().getDefinition();
            } else {
              describeCapDev = projectExpectedStudyInfo.getCapdevLevel().getName() + "\n"
                + projectExpectedStudyInfo.getCapdevLevel().getDefinition() + "\n"
                + projectExpectedStudyInfo.getDescribeCapdev();
            }
          }

          additional = "Gender: " + describeGender + "\nYouth: " + describeYouth + " \nCapDev: " + describeCapDev;
          POIField[] sData =
            {new POIField(title, ParagraphAlignment.LEFT), new POIField(subIdo, ParagraphAlignment.LEFT),
              new POIField(link, ParagraphAlignment.LEFT), new POIField(additional, ParagraphAlignment.LEFT)};
          data = Arrays.asList(sData);
          datas.add(data);
        }
      }
    }

    poiSummary.textTable(document, headers, datas, false, "tableA1AnnualReport");
  }

  private void createTableB() {

    List<List<POIField>> headers = new ArrayList<>();

    POIField[] sHeader = {new POIField(this.getText("expectedProgress.tableA.fp"), ParagraphAlignment.LEFT),
      new POIField(this.getText("expectedProgress.tableA.subIDO"), ParagraphAlignment.LEFT),
      new POIField(this.getText("expectedProgress.tableA.outcomes"), ParagraphAlignment.LEFT),
      new POIField(this.getText("expectedProgress.tableA.milestone") + "*", ParagraphAlignment.LEFT),
      new POIField(this.getText("summaries.annualReport.tableB.field5"), ParagraphAlignment.LEFT),
      new POIField(this.getText("summaries.annualReport.tableB.field6"), ParagraphAlignment.LEFT)};
    List<ReportSynthesisFlagshipProgressMilestone> reportSynthesisFlagshipProgressMilestoneList =
      reportSynthesisFlagshipProgressMilestoneManager.findAll();
    List<POIField> header = Arrays.asList(sHeader);
    headers.add(header);
    String FP = "", outcomes = "", milestone = "", subIDO = "", status = "", evidence = "";

    List<List<POIField>> datas = new ArrayList<>();

    List<POIField> data;
    for (CrpProgram flagship : flagships) {

      data = new ArrayList<>();
      int outcome_index = 0;
      for (CrpProgramOutcome outcome : flagship.getOutcomes()) {
        subIDO = "";
        int milestone_index = 0;
        for (CrpOutcomeSubIdo subIdo : outcome.getSubIdos()) {
          if (subIdo.getSrfSubIdo() != null) {
            if (subIDO.isEmpty()) {
              if (subIdo.getSrfSubIdo().getSrfIdo().isIsCrossCutting()) {
                subIDO = "• CC " + subIdo.getSrfSubIdo().getDescription();
              } else {
                subIDO = "• " + subIdo.getSrfSubIdo().getDescription();
              }
            } else {
              if (subIdo.getSrfSubIdo().getSrfIdo().isIsCrossCutting()) {
                subIDO += "\n • CC " + subIdo.getSrfSubIdo().getDescription();
              } else {
                subIDO += "\n • " + subIdo.getSrfSubIdo().getDescription();
              }
            }
          }
        }

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
          if (reportSynthesisFlagshipProgressMilestoneList != null
            && !reportSynthesisFlagshipProgressMilestoneList.isEmpty()) {
            for (int i = 0; i < reportSynthesisFlagshipProgressMilestoneList.size(); i++) {
              if (reportSynthesisFlagshipProgressMilestoneList.get(i).getCrpMilestone().getId() == crpMilestone
                .getId()) {
                evidence = "";
                evidence = reportSynthesisFlagshipProgressMilestoneList.get(i).getEvidence();
                status = reportSynthesisFlagshipProgressMilestoneList.get(i).getStatusName();

              }
            }
          }

          POIField[] sData = {new POIField(FP, ParagraphAlignment.LEFT), new POIField(subIDO, ParagraphAlignment.LEFT),
            new POIField(outcomes, ParagraphAlignment.LEFT), new POIField(milestone, ParagraphAlignment.LEFT),
            new POIField(status, ParagraphAlignment.LEFT), new POIField(evidence, ParagraphAlignment.LEFT)};
          data = Arrays.asList(sData);
          datas.add(data);

          milestone_index++;
        }
        outcome_index++;
      }
    }

    poiSummary.textTable(document, headers, datas, false, "tableBAnnualReport");
  }

  private void createTableC() {
    List<List<POIField>> headers = new ArrayList<>();
    POIField[] sHeader =
      {new POIField(this.getText("crossCuttingDimensions.tableC.crossCutting"), ParagraphAlignment.CENTER),
        new POIField(this.getText("crossCuttingDimensions.tableC.principal"), ParagraphAlignment.CENTER),
        new POIField(this.getText("crossCuttingDimensions.tableC.significant"), ParagraphAlignment.CENTER),
        new POIField(this.getText("crossCuttingDimensions.tableC.notTargeted"), ParagraphAlignment.CENTER),
        new POIField(this.getText("summaries.annualreport.tablec.field5"), ParagraphAlignment.CENTER)};
    List<POIField> header = Arrays.asList(sHeader);
    headers.add(header);
    List<List<POIField>> datas = new ArrayList<>();

    List<POIField> data;
    this.tableCInfo(this.getActualPhase());

    if (tableC != null) {
      POIField[] sData = {new POIField("Gender", ParagraphAlignment.LEFT),
        new POIField(percentageFormat.format(round(tableC.getPercentageGenderPrincipal() / 100, 4)),
          ParagraphAlignment.CENTER),
        new POIField(percentageFormat.format(round(tableC.getPercentageGenderSignificant() / 100, 4)),
          ParagraphAlignment.CENTER),
        new POIField(percentageFormat.format(round(tableC.getPercentageGenderNotScored() / 100, 4)),
          ParagraphAlignment.CENTER),
        new POIField(String.valueOf((int) tableC.getTotal()), ParagraphAlignment.CENTER)};
      data = Arrays.asList(sData);
      datas.add(data);
      POIField[] sData2 = {new POIField("Youth", ParagraphAlignment.LEFT),
        new POIField(percentageFormat.format(round(tableC.getPercentageYouthPrincipal() / 100, 4)),
          ParagraphAlignment.CENTER),
        new POIField(percentageFormat.format(round(tableC.getPercentageYouthSignificant() / 100, 4)),
          ParagraphAlignment.CENTER),
        new POIField(percentageFormat.format(round(tableC.getPercentageYouthNotScored() / 100, 4)),
          ParagraphAlignment.CENTER),
        new POIField("", ParagraphAlignment.LEFT)};
      data = Arrays.asList(sData2);
      datas.add(data);
      POIField[] sData3 = {new POIField("CapDev", ParagraphAlignment.LEFT),
        new POIField(percentageFormat.format(round(tableC.getPercentageCapDevPrincipal() / 100, 4)),
          ParagraphAlignment.CENTER),
        new POIField(percentageFormat.format(round(tableC.getPercentageCapDevSignificant() / 100, 4)),
          ParagraphAlignment.CENTER),
        new POIField(percentageFormat.format(round(tableC.getPercentageCapDevNotScored() / 100, 4)),
          ParagraphAlignment.CENTER),
        new POIField("", ParagraphAlignment.LEFT)};
      data = Arrays.asList(sData3);
      datas.add(data);
    }

    poiSummary.textTable(document, headers, datas, true, "tableCAnnualReport");
  }

  public void createTableD1() {
    List<List<POIField>> headers = new ArrayList<>();

    POIField[] sHeader = {new POIField(this.getText("summaries.annualReport.tableD1.field1"), ParagraphAlignment.LEFT),
      new POIField(this.getText("summaries.annualReport.tableD1.field2"), ParagraphAlignment.LEFT),
      new POIField(this.getText("summaries.annualReport.tableD1.field3"), ParagraphAlignment.LEFT),
      new POIField(this.getText("summaries.annualReport.tableD1.field4"), ParagraphAlignment.LEFT)};
    List<RepIndSynthesisIndicator> listRepIndSynthesis = null;
    List<ReportSynthesisIndicator> reportSynthesisIndicatorList = null;
    listRepIndSynthesis = repIndSynthesisIndicatorManager.findAll();
    reportSynthesisIndicatorList = reportSynthesisIndicatorManager.findAll();

    List<POIField> header = Arrays.asList(sHeader);
    headers.add(header);
    String type = "", indicator = "", name = "", dataRep = "", comments = "", lastType = "";

    List<List<POIField>> datas = new ArrayList<>();
    List<POIField> data;
    if (listRepIndSynthesis != null && !listRepIndSynthesis.isEmpty()) {
      for (int i = 0; i < listRepIndSynthesis.size(); i++) {
        type = "";
        indicator = "";
        name = "";
        dataRep = "";
        comments = "";
        lastType = "";
        data = new ArrayList<>();
        type = listRepIndSynthesis.get(i).getType();

        if (type.equals(lastType)) {
          type = "";
        } else {
          lastType = type;
        }

        if (reportSynthesisIndicatorList != null) {
          for (int j = 0; j < reportSynthesisIndicatorList.size(); j++) {
            if (reportSynthesisIndicatorList.get(j).getRepIndSynthesisIndicator().getId() == listRepIndSynthesis.get(i)
              .getId()) {
              dataRep = reportSynthesisIndicatorList.get(j).getData();
              comments = reportSynthesisIndicatorList.get(j).getComment();
            }
          }
        }

        indicator = listRepIndSynthesis.get(i).getIndicator();
        name = listRepIndSynthesis.get(i).getName();

        Boolean bold = false;
        String blackColor = "000000";
        POIField[] sData = {new POIField(type, ParagraphAlignment.CENTER, bold, blackColor),
          new POIField(indicator + "." + name, ParagraphAlignment.LEFT, bold, blackColor),
          new POIField(dataRep, ParagraphAlignment.LEFT, bold, blackColor),
          new POIField(comments, ParagraphAlignment.CENTER, bold, blackColor)};
        data = Arrays.asList(sData);
        datas.add(data);
      }
    }

    poiSummary.textTable(document, headers, datas, true, "tableD1AnnualReport");

  }

  private void createTableD2() {
    List<List<POIField>> headers = new ArrayList<>();
    POIField[] sHeader =
      {new POIField(this.getText("summaries.annualReport.tableD2.field1"), ParagraphAlignment.CENTER),
        new POIField(this.getText("summaries.annualReport.tableD2.field2"), ParagraphAlignment.CENTER),
        new POIField(this.getText("summaries.annualReport.tableD2.field3"), ParagraphAlignment.CENTER),
        new POIField(this.getText("summaries.annualReport.tableD2.field4"), ParagraphAlignment.CENTER),
        new POIField(this.getText("summaries.annualReport.tableD2.field5"), ParagraphAlignment.CENTER),};
    List<POIField> header = Arrays.asList(sHeader);
    headers.add(header);

    List<List<POIField>> datas = new ArrayList<>();
    List<POIField> data;

    List<ProjectInnovation> projectInnovationList = projectInnovationManager.findAll();

    if (projectInnovationList != null && !projectInnovationList.isEmpty()) {
      for (int i = 0; i < projectInnovationList.size(); i++) {
        String title = " ", stage = "", degree = " ", contribution = " ", geographicScope = " ";
        if (projectInnovationList.get(i).getProjectInnovationInfo(this.getActualPhase()) != null) {
          ProjectInnovationInfo projectInnovationInfo =
            projectInnovationList.get(i).getProjectInnovationInfo(this.getActualPhase());
          title = projectInnovationInfo.getTitle();
          if (projectInnovationInfo.getRepIndStageInnovation() != null) {
            stage = projectInnovationInfo.getRepIndStageInnovation().getName();
          }
          if (projectInnovationInfo.getRepIndDegreeInnovation() != null) {
            degree = projectInnovationInfo.getRepIndDegreeInnovation().getName();
          }
          if (projectInnovationInfo.getRepIndContributionOfCrp() != null) {
            contribution = projectInnovationInfo.getRepIndContributionOfCrp().getName();
          }
          if (projectInnovationInfo.getRepIndGeographicScope() != null) {
            geographicScope = projectInnovationInfo.getRepIndGeographicScope().getName();
          }

          POIField[] sData =
            {new POIField(title, ParagraphAlignment.CENTER), new POIField(stage, ParagraphAlignment.CENTER),
              new POIField(degree, ParagraphAlignment.LEFT), new POIField(contribution, ParagraphAlignment.LEFT),
              new POIField(geographicScope, ParagraphAlignment.LEFT)};

          data = Arrays.asList(sData);
          datas.add(data);

        }
      }
    }

    poiSummary.textTable(document, headers, datas, false, "tableD2AnnualReport");

  }

  private void createTableE() {
    this.getAssetsList();
    List<List<POIField>> headers = new ArrayList<>();

    POIField[] sHeader = {new POIField(this.getText("summaries.annualReport.tableE.field1"), ParagraphAlignment.LEFT),
      new POIField(this.getText("summaries.annualReport.tableE.field2"), ParagraphAlignment.CENTER),
      new POIField(this.getText("summaries.annualReport.tableE.field3"), ParagraphAlignment.CENTER),
      new POIField(this.getText("summaries.annualReport.tableE.field4"), ParagraphAlignment.CENTER),
      new POIField(this.getText("summaries.annualReport.tableE.field5"), ParagraphAlignment.CENTER),
      new POIField(this.getText("summaries.annualReport.tableE.field6"), ParagraphAlignment.CENTER)};

    List<POIField> header = Arrays.asList(sHeader);
    headers.add(header);

    List<List<POIField>> datas = new ArrayList<>();

    List<POIField> data;
    String title = "", patent = "", applicant = "", aditional = "", registration = "", communication = "";

    for (int i = 0; i < assetsList.size(); i++) {
      title = assetsList.get(i).getTitle();
      applicant = assetsList.get(i).getApplicant();
      patent = assetsList.get(i).getApplicant();
      aditional = assetsList.get(i).getAdditionalInformation();
      registration = assetsList.get(i).getLink();
      communication = assetsList.get(i).getPublicCommunication();

      POIField[] sData =
        {new POIField(title, ParagraphAlignment.CENTER), new POIField(applicant, ParagraphAlignment.LEFT),
          new POIField(patent, ParagraphAlignment.LEFT), new POIField(aditional, ParagraphAlignment.LEFT),
          new POIField(registration, ParagraphAlignment.CENTER), new POIField(communication, ParagraphAlignment.LEFT)};
      data = Arrays.asList(sData);
      datas.add(data);
    }
    poiSummary.textTable(document, headers, datas, false, "tableAAnnualReport");
  }

  private void createTableF() {

    List<List<POIField>> headers = new ArrayList<>();
    POIField[] sHeader =
      {new POIField(this.getText("financialPlan.tableF.expenditureArea") + "*", ParagraphAlignment.LEFT),
        new POIField(this.getText("financialPlan.tableF.estimatedPercentage",
          new String[] {String.valueOf(this.getSelectedYear())}) + "**", ParagraphAlignment.LEFT),
        new POIField(this.getText("financialPlan.tableF.comments2017"), ParagraphAlignment.LEFT)};

    List<POIField> header = Arrays.asList(sHeader);
    headers.add(header);

    List<List<POIField>> datas = new ArrayList<>();
    List<POIField> data;

    List<ReportSynthesisFundingUseExpendituryArea> reportSynthesisFundingUseExpendituryAreaList =
      reportSynthesisFundingUseExpendituryAreaManager.findAll();
    Double totalEstimatedPercentajeFS = 0.0;
    if (reportSynthesisFundingUseExpendituryAreaList != null) {

      for (int i = 0; i < reportSynthesisFundingUseExpendituryAreaList.size(); i++) {
        String expenditureArea = "", commentsSpace = "";
        Double estimatedPercentajeFS = 0.0;
        expenditureArea = reportSynthesisFundingUseExpendituryAreaList.get(i).getExpenditureArea().getExpenditureArea();
        estimatedPercentajeFS = reportSynthesisFundingUseExpendituryAreaList.get(i).getW1w2Percentage();
        commentsSpace = reportSynthesisFundingUseExpendituryAreaList.get(i).getComments();

        totalEstimatedPercentajeFS += estimatedPercentajeFS;
        POIField[] sData = {new POIField(expenditureArea, ParagraphAlignment.LEFT),
          new POIField(percentageFormat.format(round(estimatedPercentajeFS / 100, 4)), ParagraphAlignment.CENTER),
          new POIField(commentsSpace, ParagraphAlignment.LEFT)};
        data = Arrays.asList(sData);
        datas.add(data);
      }
    }

    Boolean bold = true;
    String blackColor = "000000";

    POIField[] sData = {new POIField("TOTAL FUNDING (AMOUNT)", ParagraphAlignment.LEFT, bold, blackColor),
      new POIField(currencyFormat.format(round((totalw1w2 * totalEstimatedPercentajeFS) / 100, 2)),
        ParagraphAlignment.CENTER, bold, blackColor),
      new POIField(" ", ParagraphAlignment.LEFT, bold, blackColor)};

    data = Arrays.asList(sData);
    datas.add(data);

    poiSummary.textTable(document, headers, datas, true, "tableFAnnualReport");
  }


  private void createTableG() {
    List<List<POIField>> headers = new ArrayList<>();

    List<LiaisonInstitution> liaisonInstitutions = new ArrayList<>(this.getLoggedCrp().getLiaisonInstitutions().stream()
      .filter(c -> c.getCrpProgram() != null && c.isActive()
        && c.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue())
      .collect(Collectors.toList()));

    POIField[] sHeader = {new POIField(this.getText("summaries.annualReport.tableG.field1"), ParagraphAlignment.LEFT),
      new POIField(this.getText("summaries.annualReport.tableG.field2"), ParagraphAlignment.LEFT),
      new POIField(this.getText("summaries.annualReport.tableG.field3"), ParagraphAlignment.LEFT),
      new POIField(this.getText("summaries.annualReport.tableG.field4"), ParagraphAlignment.LEFT),
      new POIField(this.getText("summaries.annualReport.tableG.field5"), ParagraphAlignment.LEFT)};

    List<POIField> header = Arrays.asList(sHeader);
    headers.add(header);

    flagshipExternalPlannedList = new ArrayList<>();
    List<List<POIField>> datas = new ArrayList<>();
    List<POIField> data;


    flagshipExternalPlannedList = reportSynthesisExternalPartnershipManager.getPlannedPartnershipList(
      liaisonInstitutions, this.getActualPhase().getId(), this.getLoggedCrp(), pmuInstitution);


    if (flagshipExternalPlannedList != null && !flagshipExternalPlannedList.isEmpty()) {

      for (int i = 0; i < flagshipExternalPlannedList.size(); i++) {

        String FP = "", stage = "", partner = "", partnerType = "", mainArea = "";
        if (flagshipExternalPlannedList.get(i).getProjectPartnerPartnership() != null) {
          if (flagshipExternalPlannedList.get(i).getProjectPartnerPartnership().getProjectPartner().getProject()
            .getFlagships() != null
            && !flagshipExternalPlannedList.get(i).getProjectPartnerPartnership().getProjectPartner().getProject()
              .getFlagships().isEmpty()) {

            if (flagshipExternalPlannedList.get(i).getProjectPartnerPartnership().getProjectPartner().getProject()
              .getFlagships().get(0).getAcronym() != null
              && !flagshipExternalPlannedList.get(i).getProjectPartnerPartnership().getProjectPartner().getProject()
                .getFlagships().get(0).getAcronym().isEmpty()) {
              FP = flagshipExternalPlannedList.get(i).getProjectPartnerPartnership().getProjectPartner().getProject()
                .getFlagships().get(0).getAcronym();
            }
          }
          if (flagshipExternalPlannedList.get(i).getProjectPartnerPartnership().getProjectPartner().getInstitution()
            .getName() != null
            && !flagshipExternalPlannedList.get(i).getProjectPartnerPartnership().getProjectPartner().getInstitution()
              .getName().isEmpty()) {
            partner = flagshipExternalPlannedList.get(i).getProjectPartnerPartnership().getProjectPartner()
              .getInstitution().getName();
          }
          if (flagshipExternalPlannedList.get(i).getProjectPartnerPartnership().getPartnershipResearchPhases() != null
            && !flagshipExternalPlannedList.get(i).getProjectPartnerPartnership().getPartnershipResearchPhases()
              .isEmpty()) {
            for (int j = 0; j < flagshipExternalPlannedList.get(i).getProjectPartnerPartnership()
              .getPartnershipResearchPhases().size(); j++) {

              stage += flagshipExternalPlannedList.get(i).getProjectPartnerPartnership().getPartnershipResearchPhases()
                .get(j).getRepIndPhaseResearchPartnership().getName() + "\n";
            }
          }
          if (flagshipExternalPlannedList.get(i).getProjectPartnerPartnership().getMainArea() != null) {
            mainArea = flagshipExternalPlannedList.get(i).getProjectPartnerPartnership().getMainArea();
          }
          partnerType = flagshipExternalPlannedList.get(i).getProjectPartnerPartnership().getProjectPartner()
            .getInstitution().getInstitutionType().getName();

          POIField[] sData = {new POIField(FP, ParagraphAlignment.CENTER),
            new POIField(stage, ParagraphAlignment.CENTER), new POIField(partner, ParagraphAlignment.CENTER),
            new POIField(partnerType, ParagraphAlignment.CENTER), new POIField(mainArea, ParagraphAlignment.CENTER)};
          data = Arrays.asList(sData);
          datas.add(data);
        }
      }
    }
    poiSummary.textTable(document, headers, datas, false, "tableAAnnualReport");
  }


  private void createTableH() {
    List<List<POIField>> headers = new ArrayList<>();
    POIField[] sHeader = {new POIField(this.getText("summaries.annualReport.tableG.crpName"), ParagraphAlignment.LEFT),
      new POIField(this.getText("summaries.annualReport.tableG.description"), ParagraphAlignment.LEFT),
      new POIField(this.getText("summaries.annualReport.tableG.relevantFP"), ParagraphAlignment.LEFT)};
    List<POIField> header = Arrays.asList(sHeader);
    headers.add(header);

    List<List<POIField>> datas = new ArrayList<>();
    List<POIField> data;


    List<ReportSynthesisCrossCgiarCollaboration> reportSynthesisCrossCgiarCollaborationList =
      reportSynthesisCrossCgiarCollaborationManager.findAll();
    if (reportSynthesisCrossCgiarCollaborationList != null) {
      for (int i = 0; i < reportSynthesisCrossCgiarCollaborationList.size(); i++) {


        String crpPlatform = " ", descriptionCollaboration = " ", relevantFP = " ";
        crpPlatform = reportSynthesisCrossCgiarCollaborationList.get(i).getGlobalUnit().getAcronym();
        descriptionCollaboration = reportSynthesisCrossCgiarCollaborationList.get(i).getDescription();
        relevantFP = reportSynthesisCrossCgiarCollaborationList.get(i).getFlagship();

        POIField[] sData = {new POIField(crpPlatform, ParagraphAlignment.CENTER),
          new POIField(descriptionCollaboration, ParagraphAlignment.LEFT),
          new POIField(relevantFP, ParagraphAlignment.LEFT)};

        data = Arrays.asList(sData);
        datas.add(data);

      }
    }

    poiSummary.textTable(document, headers, datas, false, "tableAAnnualReport");
  }

  private void createTableI1() {

    List<List<POIField>> headers = new ArrayList<>();
    List<List<POIField>> datas = new ArrayList<>();
    List<POIField> data;
    POIField[] sHeader = {
      new POIField(
        this.getText("annualReport.melia.tableI.studies", new String[] {String.valueOf(this.getSelectedYear())}),
        ParagraphAlignment.CENTER),
      new POIField(this.getText("annualReport.melia.tableI.status"), ParagraphAlignment.CENTER),
      new POIField(this.getText("annualReport.melia.tableI.comments"), ParagraphAlignment.CENTER)};
    List<POIField> header = Arrays.asList(sHeader);
    headers.add(header);
    List<ProjectExpectedStudyInfo> projectExpectedStudyInfoList = projectExpectedStudyInfoManager.findAll();

    if (projectExpectedStudyInfoList != null && !projectExpectedStudyInfoList.isEmpty()) {
      for (int i = 0; i < projectExpectedStudyInfoList.size(); i++) {
        String studies = "", status = "", comments = "";
        if (projectExpectedStudyInfoList.get(i).getPhase().getId() == this.getActualPhase().getId()
          && projectExpectedStudyInfoList.get(i).getStudyType() != null
          && projectExpectedStudyInfoList.get(i).getStudyType().getId() != 1) {

          if (projectExpectedStudyInfoList.get(i).getTitle() != null
            && !projectExpectedStudyInfoList.get(i).getTitle().isEmpty()) {

            studies = projectExpectedStudyInfoList.get(i).getTitle();
            status = projectExpectedStudyInfoList.get(i).getStatusName();
            if (projectExpectedStudyInfoList.get(i).getTopLevelComments() != null) {
              comments = projectExpectedStudyInfoList.get(i).getTopLevelComments();
            }

            POIField[] sData = {new POIField(studies, ParagraphAlignment.CENTER),
              new POIField(status, ParagraphAlignment.LEFT), new POIField(comments, ParagraphAlignment.LEFT)};
            data = Arrays.asList(sData);
            datas.add(data);
          }
        }
      }
    }
    poiSummary.textTable(document, headers, datas, false, "tableIAnnualReport");
  }

  private void createTableI2() {
    List<List<POIField>> headers = new ArrayList<>();
    POIField[] sHeader = {new POIField(this.getText("annualReport.melia.evaluation.name"), ParagraphAlignment.CENTER),
      new POIField(this.getText("annualReport.melia.evaluation.recommendation"), ParagraphAlignment.CENTER),
      new POIField(this.getText("annualReport.melia.evaluation.managementResponse") + " - Action Plan",
        ParagraphAlignment.CENTER),
      new POIField(this.getText("annualReport.melia.evaluation.whom"), ParagraphAlignment.CENTER),
      new POIField(this.getText("annualReport.melia.evaluation.when"), ParagraphAlignment.CENTER),
      new POIField(this.getText("summaries.annualReport.tableI2.field6"), ParagraphAlignment.CENTER)};
    List<POIField> header = Arrays.asList(sHeader);
    headers.add(header);

    List<List<POIField>> datas = new ArrayList<>();
    List<POIField> data;
    List<ReportSynthesisMeliaEvaluation> reportSynthesisMeliaEvaluationList =
      reportSynthesisMeliaEvaluationManager.findAll();
    if (reportSynthesisMeliaEvaluationList != null) {
      for (int i = 0; i < reportSynthesisMeliaEvaluationList.size(); i++) {
        String nameEvaluation = "", recommendation = "", response = "", whom = "", when = "", status = "";
        if (reportSynthesisMeliaEvaluationList.get(i).isActive()) {
          nameEvaluation = reportSynthesisMeliaEvaluationList.get(i).getNameEvaluation();
          recommendation = reportSynthesisMeliaEvaluationList.get(i).getRecommendation();
          response = reportSynthesisMeliaEvaluationList.get(i).getManagementResponse();
          whom = reportSynthesisMeliaEvaluationList.get(i).getTextWhom();
          when = reportSynthesisMeliaEvaluationList.get(i).getTextWhen();
          status = reportSynthesisMeliaEvaluationList.get(i).getStatus().toString();

          POIField[] sData = {new POIField(nameEvaluation, ParagraphAlignment.CENTER),
            new POIField(recommendation, ParagraphAlignment.LEFT), new POIField(response, ParagraphAlignment.LEFT),
            new POIField(whom, ParagraphAlignment.LEFT), new POIField(when, ParagraphAlignment.LEFT),
            new POIField(status, ParagraphAlignment.LEFT)};

          data = Arrays.asList(sData);
          datas.add(data);
        }
      }
    }

    poiSummary.textTable(document, headers, datas, false, "tableIAnnualReport");
  }

  private void createTableJ() {
    this.getInformationTableJ();
    List<ReportSynthesisFinancialSummaryBudget> reportSynthesisFinancialSummaryBudgetList = new ArrayList<>();
    reportSynthesisFinancialSummaryBudgetList = reportSynthesisFinancialSummaryBudgetManager.findAll();

    List<List<POIField>> headers = new ArrayList<>();
    POIField[] sHeader = {new POIField("", ParagraphAlignment.CENTER),
      new POIField(
        this.getText("annualReport.financial.tableJ.budget", new String[] {String.valueOf(this.getSelectedYear())}),
        ParagraphAlignment.CENTER),
      new POIField("", ParagraphAlignment.CENTER), new POIField("", ParagraphAlignment.CENTER),
      new POIField(this.getText("annualReport.financial.tableJ.expenditure"), ParagraphAlignment.CENTER),
      new POIField("", ParagraphAlignment.CENTER), new POIField("", ParagraphAlignment.CENTER),
      new POIField(this.getText("annualReport.financial.tableJ.difference"), ParagraphAlignment.CENTER),
      new POIField("", ParagraphAlignment.CENTER), new POIField("", ParagraphAlignment.CENTER)};

    POIField[] sHeader2 = {new POIField(" ", ParagraphAlignment.CENTER),
      new POIField(this.getText("financialPlan.tableE.w1w2"), ParagraphAlignment.CENTER),
      new POIField(this.getText("financialPlan.tableE.w3bilateral"), ParagraphAlignment.CENTER),
      new POIField(this.getText("financialPlan.tableE.total"), ParagraphAlignment.CENTER),
      new POIField(this.getText("financialPlan.tableE.w1w2"), ParagraphAlignment.CENTER),
      new POIField(this.getText("financialPlan.tableE.w3bilateral"), ParagraphAlignment.CENTER),
      new POIField(this.getText("financialPlan.tableE.total"), ParagraphAlignment.CENTER),
      new POIField(this.getText("financialPlan.tableE.w1w2"), ParagraphAlignment.CENTER),
      new POIField(this.getText("financialPlan.tableE.w3bilateral"), ParagraphAlignment.CENTER),
      new POIField(this.getText("financialPlan.tableE.total"), ParagraphAlignment.CENTER)};

    List<POIField> header = Arrays.asList(sHeader);
    List<POIField> header2 = Arrays.asList(sHeader2);
    headers.add(header);
    headers.add(header2);

    List<List<POIField>> datas = new ArrayList<>();
    List<POIField> data;
    double totalW1w2Difference = 0.0, totalW3Difference = 0.0, grandTotalDifference = 0.0;
    if (reportSynthesisFinancialSummaryBudgetList != null && reportSynthesisFinancialSummaryBudgetList.isEmpty()) {
      for (int i = 0; i < reportSynthesisFinancialSummaryBudgetList.size(); i++) {

        String category = "";
        Double w1w2Planned = 0.0, w3Planned = 0.0, w1w2Actual = 0.0, w3Actual = 0.0, totalPlanned = 0.0,
          totalActual = 0.0, w1w2Difference = 0.0, w3Difference = 0.0, totalDifference = 0.0;

        /** Getting category name **/
        if (reportSynthesisFinancialSummaryBudgetList.get(i).getLiaisonInstitution() != null) {
          category = reportSynthesisFinancialSummaryBudgetList.get(i).getLiaisonInstitution().getName();
        } else {
          category = reportSynthesisFinancialSummaryBudgetList.get(i).getExpenditureArea().getExpenditureArea();
        }

        w1w2Planned = reportSynthesisFinancialSummaryBudgetList.get(i).getW1Planned();
        w3Planned = reportSynthesisFinancialSummaryBudgetList.get(i).getW3Planned();
        totalPlanned = w1w2Planned + w3Planned;

        w1w2Actual = reportSynthesisFinancialSummaryBudgetList.get(i).getW1Actual();
        w3Actual = reportSynthesisFinancialSummaryBudgetList.get(i).getW3Actual();
        totalActual = w1w2Actual + w3Actual;


        w1w2Difference = w1w2Planned - w1w2Actual;
        w3Difference = w3Planned - w3Actual;
        totalDifference = totalPlanned - totalActual;

        totalw1w2Planned += w1w2Planned;
        totalW3Planned += w3Planned;
        grandTotalPlanned += totalPlanned;

        totalw1w2Actual += w1w2Actual;
        totalW3Actual += w3Actual;
        grandTotalActual += totalActual;

        totalW1w2Difference += w1w2Difference;
        totalW3Difference += w3Difference;
        grandTotalDifference += totalDifference;

        POIField[] sData = {new POIField(category, ParagraphAlignment.CENTER),
          new POIField(currencyFormat.format(round(w1w2Planned, 2)), ParagraphAlignment.CENTER),
          new POIField(currencyFormat.format(round(w3Planned, 2)), ParagraphAlignment.CENTER),
          new POIField(currencyFormat.format(round(totalPlanned, 2)), ParagraphAlignment.CENTER),
          new POIField(currencyFormat.format(round(w1w2Actual, 2)), ParagraphAlignment.CENTER),
          new POIField(currencyFormat.format(round(w3Actual, 2)), ParagraphAlignment.CENTER),
          new POIField(currencyFormat.format(round(totalActual, 2)), ParagraphAlignment.CENTER),
          new POIField(currencyFormat.format(round(w1w2Difference, 2)), ParagraphAlignment.CENTER),
          new POIField(currencyFormat.format(round(w3Difference, 2)), ParagraphAlignment.CENTER),
          new POIField(currencyFormat.format(round(totalDifference, 2)), ParagraphAlignment.CENTER)};

        data = Arrays.asList(sData);
        datas.add(data);
      }
    }

    Boolean bold = true;
    String blackColor = "000000";
    POIField[] sData = {new POIField("CRP Total", ParagraphAlignment.CENTER, bold, blackColor),

      new POIField(currencyFormat.format(round(totalw1w2Planned, 2)), ParagraphAlignment.CENTER, bold, blackColor),
      new POIField(currencyFormat.format(round(totalW3Planned, 2)), ParagraphAlignment.CENTER, bold, blackColor),
      new POIField(currencyFormat.format(round(grandTotalPlanned, 2)), ParagraphAlignment.CENTER, bold, blackColor),
      new POIField(currencyFormat.format(round(totalw1w2Actual, 2)), ParagraphAlignment.CENTER, bold, blackColor),
      new POIField(currencyFormat.format(round(totalW3Actual, 2)), ParagraphAlignment.CENTER, bold, blackColor),
      new POIField(currencyFormat.format(round(grandTotalActual, 2)), ParagraphAlignment.CENTER, bold, blackColor),
      new POIField(currencyFormat.format(round(totalW1w2Difference, 2)), ParagraphAlignment.CENTER, bold, blackColor),
      new POIField(currencyFormat.format(round(totalW3Difference, 2)), ParagraphAlignment.CENTER, bold, blackColor),
      new POIField(currencyFormat.format(round(grandTotalDifference, 2)), ParagraphAlignment.CENTER, bold,
        blackColor),};

    data = Arrays.asList(sData);
    datas.add(data);

    poiSummary.textTable(document, headers, datas, true, "tableJAnnualReport");
  }

  @Override
  public String execute() throws Exception {
    try {

      CTDocument1 doc = document.getDocument();
      CTBody body = doc.getBody();

      poiSummary.pageHeader(document, this.getText("summaries.annualReport.header"));
      // Get datetime
      ZonedDateTime timezone = ZonedDateTime.now();
      String zone = timezone.getOffset() + "";
      if (zone.equals("Z")) {
        zone = "+0";
      }

      //
      this.createPageFooter();
      // poiSummary.pageFooter(document, "This report was generated on " + currentDate);

      // Cover
      poiSummary.textLineBreak(document, 10);
      poiSummary.textHeadCoverTitle(document.createParagraph(), this.getText("summaries.annualReport.mainTitle"));

      // First page
      poiSummary.textLineBreak(document, 17);
      poiSummary.textHead1Title(document.createParagraph(), this.getText("summaries.annualReport.mainTitle2"));
      poiSummary.textHead1Title(document.createParagraph(), this.getText("summaries.annualReport.cover"));
      String unitName = this.getLoggedCrp().getAcronym() != null && !this.getLoggedCrp().getAcronym().isEmpty()
        ? this.getLoggedCrp().getAcronym() : this.getLoggedCrp().getName();
      poiSummary.textParagraph(document.createParagraph(),
        this.getText("summaries.annualReport.unitName") + ": " + unitName);
      poiSummary.textParagraph(document.createParagraph(), this.getText("summaries.annualReport.LeadCenter") + ":");
      this.addParticipatingCenters();

      // section 1 - key results
      poiSummary.textHead1Title(document.createParagraph(), this.getText("summaries.annualReport.keyResults"));
      poiSummary.textHead2Title(document.createParagraph(),
        this.getText("summaries.annualReport.keyResults.crpProgress"));
      this.addExpectedCrp();
      poiSummary.textHead2Title(document.createParagraph(),
        this.getText("summaries.annualReport.keyResults.progressFlagships"));
      this.addAdjustmentDescription();
      poiSummary.textHead2Title(document.createParagraph(),
        this.getText("summaries.annualReport.keyResults.dimensions"));
      poiSummary.textHead3Title(document.createParagraph(),
        this.getText("summaries.annualReport.keyResults.dimensions.gender"));
      this.addCrossCuttingGender();
      poiSummary.textHead3Title(document.createParagraph(),
        this.getText("summaries.annualReport.keyResults.dimensions.youth"));
      this.addCrossCuttingYouth();
      poiSummary.textHead3Title(document.createParagraph(),
        this.getText("summaries.annualReport.keyResults.dimensions.otherAspects"));
      this.addCrossCuttingOtherAspects();
      poiSummary.textHead3Title(document.createParagraph(),
        this.getText("summaries.annualReport.keyResults.dimensions.capacityDevelopment"));
      this.addCrossCuttingCapacityDevelopment();
      poiSummary.textHead3Title(document.createParagraph(),
        this.getText("summaries.annualReport.keyResults.dimensions.openData"));
      this.addCrossCuttingOpenData();
      poiSummary.textHead3Title(document.createParagraph(),
        this.getText("summaries.annualReport.keyResults.dimensions.intellectualAssets"));
      this.addCrossCuttingIntellectualAssets();

      // section 2 - variance from planned program
      poiSummary.textHead1Title(document.createParagraph(), this.getText("summaries.annualReport.effectiveness"));
      poiSummary.textHead2Title(document.createParagraph(),
        this.getText("summaries.annualReport.effectiveness.program"));
      this.addVariancePlanned();

      poiSummary.textHead2Title(document.createParagraph(),
        this.getText("summaries.annualReport.effectiveness.funding"));
      this.addFundingSummarize();
      poiSummary.textHead2Title(document.createParagraph(),
        this.getText("summaries.annualReport.effectiveness.partnership"));
      this.addExternalPartnerships();
      poiSummary.textHead2Title(document.createParagraph(), this.getText("summaries.annualReport.effectiveness.cross"));
      this.addCrossPartnerships();
      poiSummary.textHead2Title(document.createParagraph(), this.getText("summaries.annualReport.effectiveness.mel"));
      this.addReportSynthesisMelia();
      poiSummary.textHead2Title(document.createParagraph(),
        this.getText("summaries.annualReport.effectiveness.efficiency"));
      this.addImprovingEfficiency();

      // section 3 - crp management
      poiSummary.textLineBreak(document, 1);
      poiSummary.textHead1Title(document.createParagraph(), this.getText("summaries.annualReport.management"));
      this.addManagement();

      /* Create a landscape text Section */
      XWPFParagraph para = document.createParagraph();
      CTSectPr sectionTable = body.getSectPr();
      CTPageSz pageSizeTable = sectionTable.addNewPgSz();
      CTP ctpTable = para.getCTP();
      CTPPr brTable = ctpTable.addNewPPr();
      brTable.setSectPr(sectionTable);
      /* standard Letter page size */
      pageSizeTable.setOrient(STPageOrientation.LANDSCAPE);
      pageSizeTable.setW(BigInteger.valueOf(842 * 20));
      pageSizeTable.setH(BigInteger.valueOf(595 * 20));
      this.loadTablePMU();

      // Table a1
      poiSummary.textLineBreak(document, 1);
      // poiSummary.textHead1Title(paragraph, "TABLES");
      poiSummary.textHead1Title(document.createParagraph(), "TABLES");
      poiSummary.textHead2Title(document.createParagraph(), this.getText("summaries.annualReport.tableA.title"));
      poiSummary.textHead3Title(document.createParagraph(), this.getText("summaries.annualReport.tableA1.title"));
      this.createTableA1();

      // Table a2
      poiSummary.textLineBreak(document, 1);
      poiSummary.textHead3Title(document.createParagraph(), this.getText("summaries.annualReport.tableA2.title"));
      this.createTableA2();
      poiSummary.textNotes(document.createParagraph(), this.getText("summaries.annualReport.tableA2.footer"));

      // Table b
      poiSummary.textLineBreak(document, 1);
      poiSummary.textHead2Title(document.createParagraph(), this.getText("summaries.annualReport.tableB.title"));
      this.createTableB();
      poiSummary.textNotes(document.createParagraph(), this.getText("summaries.annualReport.tableB.description1"));
      poiSummary.textNotes(document.createParagraph(), this.getText("summaries.annualReport.tableB.description2"));

      // Table c
      poiSummary.textLineBreak(document, 1);
      poiSummary.textHead2Title(document.createParagraph(), this.getText("annualReport.ccDimensions.tableCTitle"));
      this.createTableC();

      // Table d
      poiSummary.textLineBreak(document, 1);
      poiSummary.textHead2Title(document.createParagraph(), this.getText("summaries.annualReport.tableD.title"));

      // Table d1
      poiSummary.textHead3Title(document.createParagraph(), this.getText("summaries.annualReport.tableD1.title"));
      this.createTableD1();
      poiSummary.textNotes(document.createParagraph(), this.getText("summaries.annualReport.tableD1.footer"));

      // Table d2
      poiSummary.textHead3Title(document.createParagraph(), this.getText("summaries.annualReport.tableD2.title"));
      this.createTableD2();
      poiSummary.textNotes(document.createParagraph(), this.getText("summaries.annualReport.tableD2.footer"));

      // Table e
      poiSummary.textLineBreak(document, 1);
      poiSummary.textHead2Title(document.createParagraph(),
        this.getText("annualReport.ccDimensions.tableETitle", new String[] {String.valueOf(this.getSelectedYear())}));
      this.createTableE();
      poiSummary.textNotes(document.createParagraph(), this.getText("intellectualAsset.title.help2017"));

      // Table f
      poiSummary.textLineBreak(document, 1);
      poiSummary.textHead2Title(document.createParagraph(), this.getText("financialPlan.tableF.title2017"));
      this.createTableF();
      poiSummary.textNotes(document.createParagraph(), this.getText("financialPlan.tableF.expenditureArea.help"));
      poiSummary.textNotes(document.createParagraph(), this.getText("financialPlan.tableF.expenditureArea.help2017"));

      // Table g
      poiSummary.textLineBreak(document, 1);
      poiSummary.textHead2Title(document.createParagraph(), this.getText("summaries.annualReport.tableG.title"));
      this.createTableG();
      poiSummary.textNotes(document.createParagraph(), this.getText("summaries.annualReport.tableG.description.help"));

      // Table h
      poiSummary.textLineBreak(document, 1);
      poiSummary.textHead2Title(document.createParagraph(), this.getText("summaries.annualReport.tableH.title"));
      this.createTableH();
      poiSummary.textNotes(document.createParagraph(), this.getText("summaries.powb.tableG.description.help"));

      // Table i1
      poiSummary.textLineBreak(document, 1);
      poiSummary.textHead2Title(document.createParagraph(), this.getText("summaries.annualReport.tableI.title"));
      poiSummary.textHead3Title(document.createParagraph(),
        this.getText("annualReport.melia.tableI.title", new String[] {String.valueOf(this.getSelectedYear())})
          + " POWB");
      this.createTableI1();

      // Table i2
      poiSummary.textLineBreak(document, 1);
      poiSummary.textHead3Title(document.createParagraph(), this.getText("annualReport.melia.evaluation.title"));
      this.createTableI2();

      // Table j
      poiSummary.textLineBreak(document, 1);
      poiSummary.textHead2Title(document.createParagraph(), this.getText("summaries.annualReport.tableJ.title"));
      this.createTableJ();
      poiSummary.textNotes(document.createParagraph(), this.getText("summaries.annualReport.tableJ.description.help"));


      ByteArrayOutputStream os = new ByteArrayOutputStream();
      document.write(os);
      bytesDOC = os.toByteArray();
      os.close();
      document.close();
    } catch (Exception e) {
      LOG.error("Error generating " + this.getFileName() + ". Exception: " + e.getMessage());
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


  private void getAssetsList() {
    assetsList = new ArrayList<>();

    List<DeliverableIntellectualAsset> assetsListTemp = deliverableIntellectualAssetManager.findAll();

    for (int i = 0; i < assetsListTemp.size(); i++) {
      if (assetsListTemp.get(i).getPhase().getId() == this.getActualPhase().getId() && assetsListTemp.get(i).isActive()
        && assetsListTemp.get(i).getHasPatentPvp()) {
        assetsList.add(assetsListTemp.get(i));
      }
    }
  }

  @Override
  public int getContentLength() {
    return bytesDOC.length;
  }

  @Override
  public String getContentType() {
    return "application/docx";
  }

  @Override
  public String getFileName() {
    StringBuffer fileName = new StringBuffer();
    String crp = this.getLoggedCrp().getAcronym();
    Date date = new Date();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

    fileName.append("2017_" + crp + "_AR_" + sdf.format(date));
    fileName.append(".docx");

    return fileName.toString();
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

  public void getInformationTableJ() {

    // Check if relation is null -create it
    if (reportSynthesisPMU != null) {
      if (reportSynthesisPMU.getReportSynthesisFinancialSummary() == null) {
        ReportSynthesisFinancialSummary financialSummary = new ReportSynthesisFinancialSummary();
        // create one to one relation
        reportSynthesisPMU.setReportSynthesisFinancialSummary(financialSummary);;
        financialSummary.setReportSynthesis(reportSynthesisPMU);
        // save the changes
        reportSynthesisPMU = reportSynthesisManager.saveReportSynthesis(reportSynthesisPMU);
      }

      if (this.isPMU()) {
        // Flagships Financial Budgets
        if (reportSynthesisPMU.getReportSynthesisFinancialSummary().getReportSynthesisFinancialSummaryBudgets() != null
          && !reportSynthesisPMU.getReportSynthesisFinancialSummary().getReportSynthesisFinancialSummaryBudgets()
            .isEmpty()) {
          reportSynthesisPMU.getReportSynthesisFinancialSummary()
            .setBudgets(new ArrayList<>(
              reportSynthesisPMU.getReportSynthesisFinancialSummary().getReportSynthesisFinancialSummaryBudgets()
                .stream().filter(t -> t.isActive()).collect(Collectors.toList())));
        } else {

          List<LiaisonInstitution> flagshipList = this.getLoggedCrp().getLiaisonInstitutions().stream()
            .filter(c -> c.getCrpProgram() != null && c.isActive()
              && c.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue())
            .collect(Collectors.toList());
          flagshipList.sort(Comparator.comparing(LiaisonInstitution::getAcronym));
          reportSynthesisPMU.getReportSynthesisFinancialSummary().setBudgets(new ArrayList<>());
          for (LiaisonInstitution liInstitution : flagshipList) {
            ReportSynthesisFinancialSummaryBudget financialSummaryBudget = new ReportSynthesisFinancialSummaryBudget();
            financialSummaryBudget.setLiaisonInstitution(liInstitution);
            reportSynthesisPMU.getReportSynthesisFinancialSummary().getBudgets().add(financialSummaryBudget);
          }

          List<PowbExpenditureAreas> expAreas = new ArrayList<>(powbExpenditureAreasManager.findAll().stream()
            .filter(x -> x.isActive() && !x.getIsExpenditure()).collect(Collectors.toList()));
          for (PowbExpenditureAreas powbExpenditureAreas : expAreas) {
            ReportSynthesisFinancialSummaryBudget financialSummaryBudget = new ReportSynthesisFinancialSummaryBudget();
            financialSummaryBudget.setExpenditureArea(powbExpenditureAreas);
            reportSynthesisPMU.getReportSynthesisFinancialSummary().getBudgets().add(financialSummaryBudget);
          }
        }
      }
    }
  }

  @Override
  public InputStream getInputStream() {
    if (inputStream == null) {
      inputStream = new ByteArrayInputStream(bytesDOC);
    }
    return inputStream;
  }

  public List<Deliverable> getProjectDeliverables(Project project, Phase phase) {

    List<Deliverable> deliverables = new ArrayList<>();


    deliverables = project.getDeliverables().stream()
      .filter(d -> d.isActive() && d.getProject() != null && d.getProject().isActive()
        && d.getProject().getGlobalUnitProjects().stream()
          .filter(gup -> gup.isActive() && gup.getGlobalUnit().getId().equals(this.getLoggedCrp().getId()))
          .collect(Collectors.toList()).size() > 0
        && d.getDeliverableInfo(phase) != null && d.getDeliverableInfo(phase).getStatus() != null
        && ((d.getDeliverableInfo(phase).getStatus().intValue() == Integer
          .parseInt(ProjectStatusEnum.Complete.getStatusId())
          && (d.getDeliverableInfo(phase).getYear() >= this.getCurrentCycleYear()
            || (d.getDeliverableInfo(phase).getNewExpectedYear() != null
              && d.getDeliverableInfo(phase).getNewExpectedYear().intValue() >= this.getCurrentCycleYear())))
          || (d.getDeliverableInfo(phase).getStatus().intValue() == Integer
            .parseInt(ProjectStatusEnum.Extended.getStatusId())
            && (d.getDeliverableInfo(phase).getNewExpectedYear() != null
              && d.getDeliverableInfo(phase).getNewExpectedYear().intValue() == this.getCurrentCycleYear()))
          || (d.getDeliverableInfo(phase).getStatus().intValue() == Integer
            .parseInt(ProjectStatusEnum.Cancelled.getStatusId())
            && (d.getDeliverableInfo(phase).getYear() == this.getCurrentCycleYear()
              || (d.getDeliverableInfo(phase).getNewExpectedYear() != null
                && d.getDeliverableInfo(phase).getNewExpectedYear().intValue() == this.getCurrentCycleYear()))))
        && (d.getDeliverableInfo(phase).getStatus().intValue() == Integer
          .parseInt(ProjectStatusEnum.Extended.getStatusId())
          || d.getDeliverableInfo(phase).getStatus().intValue() == Integer
            .parseInt(ProjectStatusEnum.Complete.getStatusId())
          || d.getDeliverableInfo(phase).getStatus().intValue() == Integer
            .parseInt(ProjectStatusEnum.Cancelled.getStatusId())))
      .collect(Collectors.toList());


    return deliverables;
  }

  public boolean isFlagship() {
    boolean isFP = false;
    if (liaisonInstitution != null) {
      if (liaisonInstitution.getCrpProgram() != null) {
        CrpProgram crpProgram =
          crpProgramManager.getCrpProgramById(liaisonInstitution.getCrpProgram().getId().longValue());
        if (crpProgram.getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue()) {
          isFP = true;
        }
      }
    }
    return isFP;
  }

  public boolean isPMU(LiaisonInstitution institution) {
    if (institution.getAcronym().equals("PMU")) {
      return true;
    }
    return false;
  }

  public void loadFlagShipBudgetInfo(CrpProgram crpProgram) {
    List<ProjectFocus> projects =
      crpProgram.getProjectFocuses().stream().filter(c -> c.getProject().isActive() && c.isActive()
        && c.getPhase() != null && c.getPhase().equals(this.getSelectedPhase())).collect(Collectors.toList());
    Set<Project> myProjects = new HashSet<Project>();
    for (ProjectFocus projectFocus : projects) {
      Project project = projectFocus.getProject();
      if (project.isActive()) {
        project.setProjectInfo(project.getProjecInfoPhase(this.getSelectedPhase()));
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

      double w1 = project.getCoreBudget(this.getSelectedPhase().getYear(), this.getSelectedPhase());
      double w3 = project.getW3Budget(this.getSelectedPhase().getYear(), this.getSelectedPhase());
      double bilateral = project.getBilateralBudget(this.getSelectedPhase().getYear(), this.getSelectedPhase());
      List<ProjectBudgetsFlagship> budgetsFlagships = project.getProjectBudgetsFlagships().stream()
        .filter(c -> c.isActive() && c.getCrpProgram().getId().longValue() == crpProgram.getId().longValue()
          && c.getPhase().equals(this.getSelectedPhase()) && c.getYear() == this.getSelectedPhase().getYear())
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
        .filter(c -> c.isActive() && c.getPhase().equals(this.getSelectedPhase())).collect(Collectors.toList()));
      List<CrpProgramOutcome> validOutcomes = new ArrayList<>();
      for (CrpProgramOutcome crpProgramOutcome : crpProgram.getOutcomes()) {

        crpProgramOutcome.setMilestones(crpProgramOutcome.getCrpMilestones().stream()
          .filter(c -> c.isActive() && c.getYear().intValue() == this.getSelectedPhase().getYear())
          .collect(Collectors.toList()));
        crpProgramOutcome.setSubIdos(
          crpProgramOutcome.getCrpOutcomeSubIdos().stream().filter(c -> c.isActive()).collect(Collectors.toList()));
        crpProgram.getMilestones().addAll(crpProgramOutcome.getMilestones());

        // if (!crpProgram.getMilestones().isEmpty()) {
        validOutcomes.add(crpProgramOutcome);
        // }
      }
      crpProgram.setOutcomes(validOutcomes);
      this.loadFlagShipBudgetInfo(crpProgram);

    }
  }

  @Override
  public void prepare() {
    this.setGeneralParameters();

    reportSysthesisList =
      this.getSelectedPhase().getReportSynthesis().stream().filter(ps -> ps.isActive()).collect(Collectors.toList());

    LiaisonInstitution pmuInstitution = this.getLoggedCrp().getLiaisonInstitutions().stream()
      .filter(c -> c.getCrpProgram() == null && c.getAcronym().equals("PMU")).collect(Collectors.toList()).get(0);

    reportSynthesisPMU = reportSynthesisManager.findSynthesis(this.getSelectedPhase().getId(), pmuInstitution.getId());

    List<ReportSynthesis> reportSysthesisPMUList = reportSysthesisList.stream()
      .filter(p -> p.isActive() && p.getLiaisonInstitution().equals(pmuInstitution)).collect(Collectors.toList());
    if (reportSysthesisPMUList != null && !reportSysthesisPMUList.isEmpty()) {
      reportSynthesisPMU = reportSysthesisPMUList.get(0);
    }

    // Calculate time to generate report
    startTime = System.currentTimeMillis();
    LOG.info(
      "Start report download: " + this.getFileName() + ". User: " + this.getCurrentUser().getComposedCompleteName()
        + ". CRP: " + this.getLoggedCrp().getAcronym() + ". Cycle: " + this.getSelectedCycle());


    // Table A-2 PMU Information
    sloTargets = new ArrayList<>(srfSloIndicatorTargetManager.findAll().stream()
      .filter(sr -> sr.isActive() && sr.getYear() == 2022).collect(Collectors.toList()));


  }

  public void setInputStream(InputStream inputStream) {
    this.inputStream = inputStream;
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

    for (GlobalUnitProject globalUnitProject : this.getLoggedCrp().getGlobalUnitProjects().stream()
      .filter(p -> p.isActive() && p.getProject() != null && p.getProject().isActive()
        && (p.getProject().getProjecInfoPhase(phase) != null
          && p.getProject().getProjectInfo().getStatus().intValue() == Integer
            .parseInt(ProjectStatusEnum.Ongoing.getStatusId())
          || p.getProject().getProjecInfoPhase(phase) != null && p.getProject().getProjectInfo().getStatus()
            .intValue() == Integer.parseInt(ProjectStatusEnum.Extended.getStatusId())))
      .collect(Collectors.toList())) {

      for (Deliverable deliverable : globalUnitProject.getProject().getDeliverables().stream().filter(d -> d.isActive()
        && d.getDeliverableInfo(phase) != null
        && ((d.getDeliverableInfo().getStatus() == null && d.getDeliverableInfo().getYear() == phase.getYear())
          || (d.getDeliverableInfo().getStatus() != null
            && d.getDeliverableInfo().getStatus()
              .intValue() == Integer.parseInt(ProjectStatusEnum.Extended.getStatusId())
            && d.getDeliverableInfo().getNewExpectedYear() != null
            && d.getDeliverableInfo().getNewExpectedYear() == phase.getYear())
          || (d.getDeliverableInfo().getStatus() != null && d.getDeliverableInfo().getYear() == phase.getYear() && d
            .getDeliverableInfo().getStatus().intValue() == Integer.parseInt(ProjectStatusEnum.Ongoing.getStatusId()))))
        .collect(Collectors.toList())) {
        deliverables.add(deliverable);
      }

    }

    if (deliverables != null && !deliverables.isEmpty()) {
      for (Deliverable deliverable : deliverables) {
        DeliverableInfo deliverableInfo = deliverable.getDeliverableInfo(phase);
        if (deliverableInfo.isActive()) {

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
      tableC = reportSynthesisCrossCuttingDimensionManager.getTableC(this.getActualPhase(), this.getLoggedCrp());
      deliverableList = tableC.getDeliverableList();
      // tableC = new CrossCuttingDimensionTableDTO();
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

      // Get the list of liaison institutions Flagships and PMU.
      liaisonInstitutions = this.getLoggedCrp().getLiaisonInstitutions().stream()
        .filter(c -> c.getCrpProgram() != null && c.isActive()
          && c.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue())
        .collect(Collectors.toList());
      liaisonInstitutions.sort(Comparator.comparing(LiaisonInstitution::getAcronym));

      // ADD PMU as liasion Institution too
      liaisonInstitutions.addAll(this.getLoggedCrp().getLiaisonInstitutions().stream()
        .filter(c -> c.getCrpProgram() == null && c.isActive() && c.getAcronym().equals("PMU"))
        .collect(Collectors.toList()));

      // Informative table to Flagships
      if (this.isFlagship()) {
        if (reportSynthesisPMU != null && reportSynthesisPMU.getReportSynthesisFundingUseSummary() != null
          && reportSynthesisPMU.getReportSynthesisFundingUseSummary()
            .getReportSynthesisFundingUseExpendituryAreas() != null
          && !reportSynthesisPMU.getReportSynthesisFundingUseSummary().getReportSynthesisFundingUseExpendituryAreas()
            .isEmpty()) {
          reportSynthesis.getReportSynthesisFundingUseSummary()
            .setExpenditureAreas(new ArrayList<>(reportSynthesisPMU.getReportSynthesisFundingUseSummary()
              .getReportSynthesisFundingUseExpendituryAreas().stream().filter(t -> t.isActive())
              .sorted((f1, f2) -> f1.getId().compareTo(f2.getId())).collect(Collectors.toList())));
        } else {
          reportSynthesis.getReportSynthesisFundingUseSummary().setExpenditureAreas(new ArrayList<>());
          List<PowbExpenditureAreas> expAreas = new ArrayList<>(
            powbExpenditureAreasManager.findAll().stream().filter(x -> x.isActive() && x.getIsExpenditure())
              .sorted((f1, f2) -> f1.getId().compareTo(f2.getId())).collect(Collectors.toList()));
          for (PowbExpenditureAreas powbExpenditureAreas : expAreas) {
            ReportSynthesisFundingUseExpendituryArea fundingUseExpenditureArea =
              new ReportSynthesisFundingUseExpendituryArea();
            fundingUseExpenditureArea.setExpenditureArea(powbExpenditureAreas);
            reportSynthesis.getReportSynthesisFundingUseSummary().getExpenditureAreas().add(fundingUseExpenditureArea);
          }
        }
      }
    }

  }


}
