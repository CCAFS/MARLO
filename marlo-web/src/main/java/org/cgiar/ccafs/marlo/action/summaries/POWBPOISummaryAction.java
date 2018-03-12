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
import org.cgiar.ccafs.marlo.data.model.dto.CrossCuttingDimensionTableDTO;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.POISummary;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class POWBPOISummaryAction extends BaseSummariesAction implements Summary {

  private static final long serialVersionUID = 2828551630719082089L;
  private static Logger LOG = LoggerFactory.getLogger(POWBPOISummaryAction.class);

  public static double round(double value, int places) {
    if (places < 0) {
      throw new IllegalArgumentException();
    }

    BigDecimal bd = new BigDecimal(value);
    bd = bd.setScale(places, RoundingMode.HALF_UP);
    return bd.doubleValue();
  }

  // Managers
  private PowbExpectedCrpProgressManager powbExpectedCrpProgressManager;
  private ProjectExpectedStudyManager projectExpectedStudyManager;
  private PowbSynthesisManager powbSynthesisManager;
  private PowbExpenditureAreasManager powbExpenditureAreasManager;
  // Parameters
  private POISummary poiSummary;
  private List<PowbSynthesis> powbSynthesisList;
  private LiaisonInstitution pmuInstitution;
  private PowbSynthesis powbSynthesisPMU;
  private long startTime;
  private XWPFDocument document;
  private List<PowbEvidencePlannedStudyDTO> flagshipPlannedList;
  private List<DeliverableInfo> deliverableList;
  private CrossCuttingDimensionTableDTO tableC;
  private NumberFormat currencyFormat;
  private DecimalFormat percentageFormat;
  private List<CrpProgram> flagships;
  // Parameter for tables E and F
  Double totalCarry = 0.0, totalw1w2 = 0.0, totalw3Bilateral = 0.0, totalCenter = 0.0, grandTotal = 0.0;
  // Streams
  private InputStream inputStream;

  // DOC bytes
  private byte[] bytesDOC;

  public POWBPOISummaryAction(APConfig config, GlobalUnitManager crpManager, PhaseManager phaseManager,
    PowbExpectedCrpProgressManager powbExpectedCrpProgressManager,
    ProjectExpectedStudyManager projectExpectedStudyManager, PowbSynthesisManager powbSynthesisManager,
    PowbExpenditureAreasManager powbExpenditureAreasManager) {
    super(config, crpManager, phaseManager);
    document = new XWPFDocument();
    poiSummary = new POISummary();
    currencyFormat = NumberFormat.getCurrencyInstance();
    percentageFormat = new DecimalFormat("##.##%");
    this.powbExpectedCrpProgressManager = powbExpectedCrpProgressManager;
    this.projectExpectedStudyManager = projectExpectedStudyManager;
    this.powbSynthesisManager = powbSynthesisManager;
    this.powbExpenditureAreasManager = powbExpenditureAreasManager;
  }

  private void addAdjustmentDescription() {
    if (powbSynthesisPMU != null) {
      String adjustmentsDescription = "";
      if (powbSynthesisPMU.getPowbToc() != null) {
        adjustmentsDescription = powbSynthesisPMU.getPowbToc().getTocOverall() != null
          && !powbSynthesisPMU.getPowbToc().getTocOverall().trim().isEmpty()
            ? powbSynthesisPMU.getPowbToc().getTocOverall() : "";
      }
      poiSummary.textParagraph(document.createParagraph(), adjustmentsDescription);
      if (powbSynthesisPMU.getPowbToc().getFile() != null) {
        poiSummary.textHyperlink(
          this.getPowbPath(powbSynthesisPMU.getLiaisonInstitution(),
            this.getLoggedCrp().getAcronym() + "_"
              + PowbSynthesisSectionStatusEnum.TOC_ADJUSTMENTS.getStatus().toString())
            + powbSynthesisPMU.getPowbToc().getFile().getFileName(),
          "URL: " + powbSynthesisPMU.getPowbToc().getFile().getFileName(), document.createParagraph());
      }
    }
  }

  private void addCollaboration() {
    String newKeyExternalPartnershipsDescription = "";
    String newContributionPlatformsDescription = "";
    String newCrossCRPInteractionsDescription = "";
    String expectedEffortsCountryCoordinationDescription = "";
    if (powbSynthesisPMU != null) {
      // Collaboration and integration
      if (powbSynthesisPMU.getCollaboration() != null) {
        newKeyExternalPartnershipsDescription = powbSynthesisPMU.getCollaboration().getKeyExternalPartners() != null
          && !powbSynthesisPMU.getCollaboration().getKeyExternalPartners().trim().isEmpty()
            ? powbSynthesisPMU.getCollaboration().getKeyExternalPartners() : "";

        newContributionPlatformsDescription = powbSynthesisPMU.getCollaboration().getCotributionsPlatafforms() != null
          && !powbSynthesisPMU.getCollaboration().getCotributionsPlatafforms().trim().isEmpty()
            ? powbSynthesisPMU.getCollaboration().getCotributionsPlatafforms() : "";

        newCrossCRPInteractionsDescription = powbSynthesisPMU.getCollaboration().getCrossCrp() != null
          && !powbSynthesisPMU.getCollaboration().getCrossCrp().trim().isEmpty()
            ? powbSynthesisPMU.getCollaboration().getCrossCrp() : "";

        expectedEffortsCountryCoordinationDescription =
          powbSynthesisPMU.getCollaboration().getEffostornCountry() != null
            && !powbSynthesisPMU.getCollaboration().getEffostornCountry().trim().isEmpty()
              ? powbSynthesisPMU.getCollaboration().getEffostornCountry() : "";
      }
    }
    poiSummary.textHead3Title(document.createParagraph(),
      this.getText("summaries.powb.effectiveness.collaboration.external"));
    poiSummary.textParagraph(document.createParagraph(), newKeyExternalPartnershipsDescription);
    poiSummary.textHead3Title(document.createParagraph(),
      this.getText("summaries.powb.effectiveness.collaboration.contributions"));
    poiSummary.textParagraph(document.createParagraph(), newContributionPlatformsDescription);
    poiSummary.textHead3Title(document.createParagraph(),
      this.getText("summaries.powb.effectiveness.collaboration.newCrossCrp"));
    poiSummary.textParagraph(document.createParagraph(), newCrossCRPInteractionsDescription);
    poiSummary.textHead3Title(document.createParagraph(),
      this.getText("summaries.powb.effectiveness.collaboration.expectedEfforts"));
    poiSummary.textParagraph(document.createParagraph(), expectedEffortsCountryCoordinationDescription);
  }

  private void addCrossCutting() {
    poiSummary.textHead2Title(document.createParagraph(),
      this.getText("summaries.powb.expectedKeyResults.crossCutting"));
    String crossCuttingGenderDescription = "";
    String crossCuttingOpenDataDescription = "";
    // Cross Cutting Dimensions Info
    if (powbSynthesisPMU.getPowbCrossCuttingDimension() != null) {
      PowbCrossCuttingDimension crossCutting = powbSynthesisPMU.getPowbCrossCuttingDimension();
      if (crossCutting != null) {
        crossCuttingGenderDescription = crossCutting.getSummarize();
        crossCuttingOpenDataDescription = crossCutting.getAssets();
      }
    }
    poiSummary.textHead3Title(document.createParagraph(),
      this.getText("summaries.powb.expectedKeyResults.crossCutting.gender"));
    if (crossCuttingGenderDescription != null && !crossCuttingGenderDescription.isEmpty()) {
      poiSummary.textParagraph(document.createParagraph(), crossCuttingGenderDescription);
    }
    poiSummary.textHead3Title(document.createParagraph(),
      this.getText("summaries.powb.expectedKeyResults.crossCutting.openData"));
    if (crossCuttingOpenDataDescription != null && !crossCuttingOpenDataDescription.isEmpty()) {
      poiSummary.textParagraph(document.createParagraph(), crossCuttingOpenDataDescription);
    }
  }

  private void addCrpStaffing() {
    String staffingDescription = "";
    if (powbSynthesisPMU != null) {
      // TOC
      if (powbSynthesisPMU.getCrpStaffing() != null) {
        staffingDescription = powbSynthesisPMU.getCrpStaffing().getStaffingIssues() != null
          && !powbSynthesisPMU.getCrpStaffing().getStaffingIssues().trim().isEmpty()
            ? powbSynthesisPMU.getCrpStaffing().getStaffingIssues() : "";
      }
      poiSummary.textParagraph(document.createParagraph(), staffingDescription);
    }
  }

  private void addEvidence() {
    String evidenceDescription = "";
    if (powbSynthesisPMU != null) {
      // Evidence
      if (powbSynthesisPMU.getPowbEvidence() != null) {
        PowbEvidence powbEvidence = powbSynthesisPMU.getPowbEvidence();
        if (powbEvidence != null) {
          evidenceDescription = powbEvidence.getNarrative() != null && !powbEvidence.getNarrative().trim().isEmpty()
            ? powbEvidence.getNarrative() : "";
        }
      }
      poiSummary.textParagraph(document.createParagraph(), evidenceDescription);
    }
  }

  private void addExpectedCrp() {
    if (powbSynthesisPMU != null) {
      String expectedCrpDescription = "";
      // CRP Progress
      List<PowbExpectedCrpProgress> powbExpectedCrpProgressList =
        powbSynthesisPMU.getPowbExpectedCrpProgresses().stream().filter(e -> e.isActive()).collect(Collectors.toList());

      if (powbExpectedCrpProgressList != null && !powbExpectedCrpProgressList.isEmpty()) {
        PowbExpectedCrpProgress powbExpectedCrpProgress = powbExpectedCrpProgressList.get(0);
        expectedCrpDescription = powbExpectedCrpProgress.getExpectedHighlights() != null
          && !powbExpectedCrpProgress.getExpectedHighlights().trim().isEmpty()
            ? powbExpectedCrpProgress.getExpectedHighlights() : "";
      }
      poiSummary.textParagraph(document.createParagraph(), expectedCrpDescription);
    }
  }

  private void addFinancialPlan() {
    String financialPlanDescription = "";
    if (powbSynthesisPMU != null) {
      // Financial Plan
      if (powbSynthesisPMU.getFinancialPlan() != null) {
        financialPlanDescription = powbSynthesisPMU.getFinancialPlan().getFinancialPlanIssues() != null
          && !powbSynthesisPMU.getFinancialPlan().getFinancialPlanIssues().trim().isEmpty()
            ? powbSynthesisPMU.getFinancialPlan().getFinancialPlanIssues() : "";
      }
    }
    poiSummary.textParagraph(document.createParagraph(), financialPlanDescription);

  }

  private void addFlagshipPlans() {
    for (LiaisonInstitution liaisonInstitution : this.getFlagships()) {
      String plansCRPFlagshipDescription = "";
      List<PowbSynthesis> powbSynthesisFL = powbSynthesisList.stream()
        .filter(p -> p.isActive() && p.getLiaisonInstitution().equals(liaisonInstitution)).collect(Collectors.toList());
      PowbSynthesis powbSynthesis = null;
      if (powbSynthesisFL != null && powbSynthesisFL.size() > 0) {
        powbSynthesis = powbSynthesisFL.get(0);
      }
      String liaisonName = liaisonInstitution.getAcronym() != null && !liaisonInstitution.getAcronym().isEmpty()
        ? liaisonInstitution.getAcronym() : liaisonInstitution.getName();
      plansCRPFlagshipDescription += liaisonName + ": ";

      if (powbSynthesis != null && powbSynthesis.getPowbFlagshipPlans() != null) {
        if (powbSynthesis.getPowbFlagshipPlans().getPlanSummary() != null) {
          plansCRPFlagshipDescription += powbSynthesis.getPowbFlagshipPlans().getPlanSummary();
        }
        poiSummary.textParagraph(document.createParagraph(), plansCRPFlagshipDescription);
        if (powbSynthesis.getPowbFlagshipPlans().getFlagshipProgramFile() != null) {
          poiSummary.textHyperlink(
            this.getPowbPath(liaisonInstitution,
              this.getLoggedCrp().getAcronym() + "_"
                + PowbSynthesisSectionStatusEnum.FLAGSHIP_PLANS.getStatus().toString())
              + powbSynthesis.getPowbFlagshipPlans().getFlagshipProgramFile().getFileName(),
            "URL: " + powbSynthesis.getPowbFlagshipPlans().getFlagshipProgramFile().getFileName(),
            document.createParagraph());
        }
      }
    }
  }

  private void addManagement() {
    // Crp Management
    String managementRisksTitleDescription = "", CRPManagementGovernanceDescription = "";
    if (powbSynthesisPMU != null) {
      // management risk
      if (powbSynthesisPMU.getPowbManagementRisk() != null) {
        managementRisksTitleDescription = powbSynthesisPMU.getPowbManagementRisk().getHighlight() != null
          && !powbSynthesisPMU.getPowbManagementRisk().getHighlight().trim().isEmpty()
            ? powbSynthesisPMU.getPowbManagementRisk().getHighlight() : "";
      }
      // Governance
      if (powbSynthesisPMU.getPowbManagementGovernance() != null) {
        CRPManagementGovernanceDescription = powbSynthesisPMU.getPowbManagementGovernance().getDescription() != null
          && !powbSynthesisPMU.getPowbManagementGovernance().getDescription().trim().isEmpty()
            ? powbSynthesisPMU.getPowbManagementGovernance().getDescription() : "";
      }
    }
    poiSummary.textHead3Title(document.createParagraph(), this.getText("summaries.powb.management.risk"));
    poiSummary.textParagraph(document.createParagraph(), managementRisksTitleDescription);
    poiSummary.textHead3Title(document.createParagraph(), this.getText("summaries.powb.management.governance"));
    poiSummary.textParagraph(document.createParagraph(), CRPManagementGovernanceDescription);


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
      this.getText("summaries.powb.participantingCenters") + ": " + participantingCenters);
  }

  public void createTableA1() {
    this.loadTablePMU();

    List<List<String>> headers = new ArrayList<>();

    String[] sHeader = {this.getText("expectedProgress.tableA.fp"), this.getText("expectedProgress.tableA.subIDO"),
      this.getText("summaries.powb.tableA1.outcomes"),
      this.getSelectedYear() + " Budget  " + this.getText("expectedProgress.tableA.w1w2"),
      this.getSelectedYear() + " Budget  " + this.getText("expectedProgress.tableA.w3bilateral")};

    List<String> header = Arrays.asList(sHeader);
    headers.add(header);
    String FP, subIDO = "", outcomes;

    List<List<String>> datas = new ArrayList<>();
    List<String> data;
    for (CrpProgram flagship : flagships) {
      int flagshipIndex = 0;
      data = new ArrayList<>();
      for (CrpProgramOutcome outcome : flagship.getOutcomes()) {
        subIDO = "";
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
        outcomes = outcome.getComposedName();

        if (flagshipIndex == 0) {
          FP = flagship.getAcronym();
        } else {
          FP = " ";
        }
        String[] sData = {FP, subIDO, outcomes, "", ""};
        data = Arrays.asList(sData);
        datas.add(data);
        flagshipIndex++;
      }
    }


    poiSummary.textTable(document, headers, datas, false);
  }

  private void createTableA2() {
    this.loadTablePMU();

    List<List<String>> headers = new ArrayList<>();

    String[] sHeader = {this.getText("expectedProgress.tableA.fp"), this.getText("summaries.powb.tableA1.outcomes"),
      this.getText("expectedProgress.tableA.milestone") + "*",
      this.getText("expectedProgress.tableA.meansVerification"),
      this.getText("expectedProgress.tableA.assessment") + "**"};

    List<String> header = Arrays.asList(sHeader);
    headers.add(header);
    String FP, outcomes, milestone, assessment, meansVerifications;

    List<List<String>> datas = new ArrayList<>();

    List<String> data;

    for (CrpProgram flagship : flagships) {
      data = new ArrayList<>();
      int outcome_index = 0;
      for (CrpProgramOutcome outcome : flagship.getOutcomes()) {
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

          PowbExpectedCrpProgress milestoneProgress =
            this.getPowbExpectedCrpProgressProgram(crpMilestone.getId(), flagship.getId());
          assessment =
            milestoneProgress.getAssesmentName() != null && !milestoneProgress.getAssesmentName().trim().isEmpty()
              ? milestoneProgress.getAssesmentName() : " ";
          meansVerifications = milestoneProgress.getMeans() != null && !milestoneProgress.getMeans().trim().isEmpty()
            ? milestoneProgress.getMeans() : " ";

          String[] sData = {FP, outcomes, milestone, meansVerifications, assessment};
          data = Arrays.asList(sData);
          datas.add(data);

          milestone_index++;
        }
        outcome_index++;
      }
    }


    poiSummary.textTable(document, headers, datas, false);
  }

  private void createTableB() {
    List<List<String>> headers = new ArrayList<>();
    String[] sHeader = {this.getText("evidenceRelevant.table.plannedTopic"),
      this.getText("evidenceRelevant.tablePlannedStudies.geographicScope"),
      this.getText("evidenceRelevant.tablePlannedStudies.relevant"),
      this.getText("evidenceRelevant.tablePlannedStudies.comments")};
    List<String> header = Arrays.asList(sHeader);
    headers.add(header);
    List<List<String>> datas = new ArrayList<>();

    List<String> data;

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

      String[] sData = {plannedStudy, geographicScope, revelantSubIDO, comments};
      data = Arrays.asList(sData);


      datas.add(data);
    }
    poiSummary.textTable(document, headers, datas, false);
  }

  private void createTableC() {
    List<List<String>> headers = new ArrayList<>();
    String[] sHeader = {this.getText("crossCuttingDimensions.tableC.crossCutting"),
      this.getText("crossCuttingDimensions.tableC.principal"),
      this.getText("crossCuttingDimensions.tableC.significant"),
      this.getText("crossCuttingDimensions.tableC.notTargeted"), this.getText("crossCuttingDimensions.tableC.overall")};
    List<String> header = Arrays.asList(sHeader);
    headers.add(header);
    List<List<String>> datas = new ArrayList<>();

    List<String> data;
    this.tableCInfo(this.getSelectedPhase());

    if (tableC != null) {
      String[] sData = {"Gender", percentageFormat.format(tableC.getPercentageGenderPrincipal() / 100),
        percentageFormat.format(tableC.getPercentageGenderSignificant() / 100),
        percentageFormat.format(tableC.getPercentageGenderNotScored() / 100), String.valueOf(tableC.getTotal())};
      data = Arrays.asList(sData);
      datas.add(data);
      String[] sData2 = {"Youth", percentageFormat.format(tableC.getPercentageYouthPrincipal() / 100),
        percentageFormat.format(tableC.getPercentageYouthSignificant() / 100),
        percentageFormat.format(tableC.getPercentageYouthNotScored() / 100), String.valueOf(tableC.getTotal())};
      data = Arrays.asList(sData2);
      datas.add(data);
      String[] sData3 = {"CapDev", percentageFormat.format(tableC.getPercentageCapDevPrincipal() / 100),
        percentageFormat.format(tableC.getPercentageCapDevSignificant() / 100),
        percentageFormat.format(tableC.getPercentageCapDevNotScored() / 100), String.valueOf(tableC.getTotal())};
      data = Arrays.asList(sData3);
      datas.add(data);
    }

    poiSummary.textTable(document, headers, datas, true);
  }

  private void createTableD() {
    List<List<String>> headers = new ArrayList<>();
    String[] sHeader = {this.getText("crpStaffing.tableD.category"), this.getText("crpStaffing.tableD.female"),
      this.getText("crpStaffing.tableD.male"), this.getText("crpStaffing.tableD.total"),
      this.getText("crpStaffing.tableD.percFemale")};
    List<String> header = Arrays.asList(sHeader);
    headers.add(header);

    List<List<String>> datas = new ArrayList<>();

    List<String> data;
    List<PowbSynthesisCrpStaffingCategory> powbSynthesisCrpStaffingCategoryList =
      powbSynthesisPMU.getPowbSynthesisCrpStaffingCategory().stream().filter(c -> c.isActive())
        .sorted((c1, c2) -> c1.getId().compareTo(c2.getId())).collect(Collectors.toList());
    Double totalFemale = 0.0, totalMale = 0.0, totalFemaleNoCg = 0.0, totalMaleNoCg = 0.0;
    if (powbSynthesisCrpStaffingCategoryList != null && !powbSynthesisCrpStaffingCategoryList.isEmpty()) {
      for (PowbSynthesisCrpStaffingCategory powbSynthesisCrpStaffingCategory : powbSynthesisCrpStaffingCategoryList) {
        String category = "";
        Double female = 0.0, femaleNoCg = 0.0, totalFTE = 0.0, femalePercentaje = 0.0, male = 0.0, maleNoCg = 0.0;
        category = powbSynthesisCrpStaffingCategory.getPowbCrpStaffingCategory().getCategory();
        female =
          powbSynthesisCrpStaffingCategory.getFemale() != null ? powbSynthesisCrpStaffingCategory.getFemale() : 0.0;
        femaleNoCg = powbSynthesisCrpStaffingCategory.getFemaleNoCgiar() != null
          ? powbSynthesisCrpStaffingCategory.getFemaleNoCgiar() : 0.0;
        femalePercentaje = powbSynthesisCrpStaffingCategory.getFemalePercentage() / 100.0;
        male = powbSynthesisCrpStaffingCategory.getMale() != null ? powbSynthesisCrpStaffingCategory.getMale() : 0.0;
        maleNoCg = powbSynthesisCrpStaffingCategory.getMaleNoCgiar() != null
          ? powbSynthesisCrpStaffingCategory.getMaleNoCgiar() : 0.0;
        totalFemale += female;
        totalFemaleNoCg += femaleNoCg;
        totalMale += male;
        totalMaleNoCg += maleNoCg;
        totalFTE = powbSynthesisCrpStaffingCategory.getTotalFTE();
        String[] sData =
          {category, String.valueOf(female) + "(" + femaleNoCg + ")", String.valueOf(male) + "(" + maleNoCg + ")",
            String.valueOf(totalFTE), percentageFormat.format(femalePercentaje)};
        data = Arrays.asList(sData);
        datas.add(data);
      }
    }
    PowbSynthesisCrpStaffingCategory powbSynthesisCrpStaffingCategory = new PowbSynthesisCrpStaffingCategory();
    powbSynthesisCrpStaffingCategory.setMale(totalMale);
    powbSynthesisCrpStaffingCategory.setMaleNoCgiar(totalMaleNoCg);
    powbSynthesisCrpStaffingCategory.setFemale(totalFemale);
    powbSynthesisCrpStaffingCategory.setFemaleNoCgiar(totalFemaleNoCg);
    String[] sData = {"Total CRP",
      String.valueOf(powbSynthesisCrpStaffingCategory.getFemale()) + "("
        + powbSynthesisCrpStaffingCategory.getFemaleNoCgiar() + ")",
      String.valueOf(powbSynthesisCrpStaffingCategory.getMale()) + "("
        + powbSynthesisCrpStaffingCategory.getMaleNoCgiar() + ")",
      String.valueOf(powbSynthesisCrpStaffingCategory.getTotalFTE()),
      percentageFormat.format(powbSynthesisCrpStaffingCategory.getFemalePercentage() / 100.0)};

    data = Arrays.asList(sData);
    datas.add(data);

    poiSummary.textTable(document, headers, datas, false);
  }

  private void createTableE() {

    List<List<String>> headers = new ArrayList<>();
    String[] sHeader =
      {"", this.getText("financialPlan.tableE.plannedBudget", new String[] {String.valueOf(this.getSelectedYear())}),
        "", "", "", "", this.getText("financialPlan.tableE.comments")};
    String[] sHeader2 =
      {"", this.getText("financialPlan.tableE.carryOver", new String[] {String.valueOf(this.getSelectedYear() - 1)}),
        this.getText("financialPlan.tableE.w1w2"), this.getText("financialPlan.tableE.w3bilateral"),
        this.getText("financialPlan.tableE.centerFunds"), this.getText("financialPlan.tableE.total"), ""};

    List<String> header = Arrays.asList(sHeader);
    List<String> header2 = Arrays.asList(sHeader2);
    headers.add(header);
    headers.add(header2);

    List<List<String>> datas = new ArrayList<>();
    List<String> data;
    List<PowbFinancialPlannedBudget> powbFinancialPlannedBudgetList =
      powbSynthesisPMU.getPowbFinancialPlannedBudget().stream().filter(p -> p.isActive()).collect(Collectors.toList());
    // Flagships
    List<LiaisonInstitution> flagships = this.getFlagships();
    if (flagships != null && !flagships.isEmpty()) {
      for (LiaisonInstitution flagship : flagships) {
        Double carry = 0.0, w1w2 = 0.0, w3Bilateral = 0.0, center = 0.0, total = 0.0;
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
            carry = powbFinancialPlannedBudget.getCarry();
            w3Bilateral = powbFinancialPlannedBudget.getW3Bilateral();
            center = powbFinancialPlannedBudget.getCenterFunds();
            total = powbFinancialPlannedBudget.getTotalPlannedBudget();
            comments = powbFinancialPlannedBudget.getComments() == null
              || powbFinancialPlannedBudget.getComments().trim().isEmpty() ? " "
                : powbFinancialPlannedBudget.getComments();
          }
        }
        totalCarry += carry;
        totalw1w2 += w1w2;
        totalw3Bilateral += w3Bilateral;
        totalCenter += center;
        grandTotal += total;
        String[] sData = {category, currencyFormat.format(round(carry, 2)), currencyFormat.format(round(w1w2, 2)),
          currencyFormat.format(round(w3Bilateral, 2)), currencyFormat.format(round(center, 2)),
          currencyFormat.format(round(total, 2)), comments};

        data = Arrays.asList(sData);
        datas.add(data);
      }
    }
    // Expenditure areas
    List<PowbExpenditureAreas> powbExpenditureAreas = this.getPlannedBudgetAreas();
    if (powbExpenditureAreas != null && !powbExpenditureAreas.isEmpty()) {
      for (PowbExpenditureAreas powbExpenditureArea : powbExpenditureAreas) {
        Double carry = 0.0, w1w2 = 0.0, w3Bilateral = 0.0, center = 0.0, total = 0.0;
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
          totalCarry += carry;
          totalw1w2 += w1w2;
          totalw3Bilateral += w3Bilateral;
          totalCenter += center;
          grandTotal += total;
          String[] sData = {category, currencyFormat.format(round(carry, 2)), currencyFormat.format(round(w1w2, 2)),
            currencyFormat.format(round(w3Bilateral, 2)), currencyFormat.format(round(center, 2)),
            currencyFormat.format(round(total, 2)), comments};

          data = Arrays.asList(sData);
          datas.add(data);
        }
      }
    }
    String[] sData = {"CRP Total", currencyFormat.format(round(totalCarry, 2)),
      currencyFormat.format(round(totalw1w2, 2)), currencyFormat.format(round(totalw3Bilateral, 2)),
      currencyFormat.format(round(totalCarry, 2)), currencyFormat.format(round(grandTotal, 2)), " "};

    data = Arrays.asList(sData);
    datas.add(data);

    poiSummary.textTable(document, headers, datas, false);
  }

  private void createTableF() {
    List<List<String>> headers = new ArrayList<>();
    String[] sHeader = {this.getText("financialPlan.tableF.expenditureArea") + "*",
      this.getText("financialPlan.tableF.estimatedPercentage", new String[] {String.valueOf(this.getSelectedYear())})
        + "**",
      this.getText("financialPlan.tableF.comments")};

    List<String> header = Arrays.asList(sHeader);
    headers.add(header);

    List<List<String>> datas = new ArrayList<>();
    List<String> data;


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
          String[] sData =
            {expenditureArea, percentageFormat.format(round(estimatedPercentajeFS / 100, 2)), commentsSpace};

          data = Arrays.asList(sData);
          datas.add(data);
        }
      }
    }
    String[] sData =
      {"Total Funding (Amount)", currencyFormat.format(round((totalw1w2 * totalEstimatedPercentajeFS) / 100, 2)), " "};

    data = Arrays.asList(sData);
    datas.add(data);

    poiSummary.textTable(document, headers, datas, true);
  }

  private void createTableG() {
    List<List<String>> headers = new ArrayList<>();
    String[] sHeader = {this.getText("summaries.powb.tableG.crpName"),
      this.getText("summaries.powb.tableG.description"), this.getText("summaries.powb.tableG.relevantFP")};
    List<String> header = Arrays.asList(sHeader);
    headers.add(header);

    List<List<String>> datas = new ArrayList<>();
    List<String> data;
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

          String[] sData = {crpPlatform, descriptionCollaboration, relevantFP};

          data = Arrays.asList(sData);
          datas.add(data);
        }
      }
    }
    poiSummary.textTable(document, headers, datas, false);
  }

  private void createTableH() {
    List<List<String>> headers = new ArrayList<>();
    String[] sHeader =
      {this.getText("monitoringLearning.table.plannedStudies", new String[] {String.valueOf(this.getSelectedYear())}),
        this.getText("monitoringLearning.table.comments")};
    List<String> header = Arrays.asList(sHeader);
    headers.add(header);

    List<List<String>> datas = new ArrayList<>();
    List<String> data;

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

      String[] sData = {plannedStudy, comments};

      data = Arrays.asList(sData);
      datas.add(data);
    }

    poiSummary.textTable(document, headers, datas, false);
  }

  @Override
  public String execute() throws Exception {
    try {
      poiSummary.pageHeader(document, this.getText("summaries.powb.header"));
      poiSummary.textLineBreak(document, 13);
      poiSummary.textHeadCoverTitle(document.createParagraph(), this.getText("summaries.powb.mainTitle"));
      poiSummary.textLineBreak(document, 12);
      poiSummary.textHead1Title(document.createParagraph(), this.getText("summaries.powb.mainTitle2"));
      poiSummary.textLineBreak(document, 1);
      poiSummary.textHead1Title(document.createParagraph(), this.getText("summaries.powb.cover"));
      poiSummary.textLineBreak(document, 1);
      String unitName = this.getLoggedCrp().getAcronym() != null && !this.getLoggedCrp().getAcronym().isEmpty()
        ? this.getLoggedCrp().getAcronym() : this.getLoggedCrp().getName();
      poiSummary.textParagraph(document.createParagraph(), this.getText("summaries.powb.unitName") + ": " + unitName);
      this.addParticipatingCenters();
      poiSummary.textLineBreak(document, 1);
      poiSummary.textHead1Title(document.createParagraph(), this.getText("summaries.powb.expectedKeyResults"));
      poiSummary.textHead2Title(document.createParagraph(), this.getText("summaries.powb.expectedKeyResults.toc"));
      this.addAdjustmentDescription();
      poiSummary.textHead2Title(document.createParagraph(),
        this.getText("summaries.powb.expectedKeyResults.expectedCrp"));
      this.addExpectedCrp();
      poiSummary.textHead2Title(document.createParagraph(), this.getText("summaries.powb.expectedKeyResults.evidence"));
      this.addEvidence();
      poiSummary.textHead2Title(document.createParagraph(),
        this.getText("summaries.powb.expectedKeyResults.flagshipPlans"));
      this.addFlagshipPlans();
      this.addCrossCutting();
      poiSummary.textHead1Title(document.createParagraph(), this.getText("summaries.powb.effectiveness"));
      poiSummary.textHead2Title(document.createParagraph(), this.getText("summaries.powb.effectiveness.staffing"));
      this.addCrpStaffing();
      poiSummary.textHead2Title(document.createParagraph(), this.getText("summaries.powb.effectiveness.financial"));
      this.addFinancialPlan();
      poiSummary.textHead2Title(document.createParagraph(), this.getText("summaries.powb.effectiveness.collaboration"));
      this.addCollaboration();
      poiSummary.textHead1Title(document.createParagraph(), this.getText("summaries.powb.management"));
      this.addManagement();

      XWPFParagraph paragraph = document.createParagraph();
      paragraph.setPageBreak(true);
      poiSummary.textHead1Title(paragraph, "TABLES");
      poiSummary.textHead2Title(document.createParagraph(), this.getText("summaries.powb.tableA.title"));
      poiSummary.textHead3Title(document.createParagraph(), this.getText("summaries.powb.tableA1.title"));
      this.createTableA1();
      poiSummary.textHead3Title(document.createParagraph(), this.getText("summaries.powb.tableA2.title"));
      this.createTableA2();
      poiSummary.textNotes(document.createParagraph(), "*" + this.getText("expectedProgress.tableA.milestone.help"));
      poiSummary.textHead2Title(document.createParagraph(), this.getText("summaries.powb.tableB.title"));
      this.createTableB();
      poiSummary.textHead2Title(document.createParagraph(), this.getText("crossCuttingDimensions.tableC.title"));
      this.createTableC();
      poiSummary.textHead2Title(document.createParagraph(), this.getText("crpStaffing.tableD.title"));
      this.createTableD();
      poiSummary.textNotes(document.createParagraph(), this.getText("crpStaffing.tableD.help"));
      poiSummary.textHead2Title(document.createParagraph(),
        this.getText("financialPlan.tableE.title", new String[] {String.valueOf(this.getSelectedYear())}));
      this.createTableE();
      poiSummary.textHead2Title(document.createParagraph(), this.getText("financialPlan.tableF.title"));
      this.createTableF();
      poiSummary.textNotes(document.createParagraph(), this.getText("financialPlan.tableF.expenditureArea.help"));
      poiSummary.textNotes(document.createParagraph(), this.getText("financialPlan.tableF.estimatedPercentage.help"));
      poiSummary.textHead2Title(document.createParagraph(),
        this.getText("collaborationIntegration.listCollaborations.title"));
      this.createTableG();
      poiSummary.textNotes(document.createParagraph(), this.getText("summaries.powb.tableG.description.help"));
      poiSummary.textHead2Title(document.createParagraph(), this.getText("summaries.powb.tableF.title"));
      this.createTableH();
      ByteArrayOutputStream os = new ByteArrayOutputStream();
      document.write(os);
      bytesDOC = os.toByteArray();
      os.close();
      document.close();
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

  @Override
  public int getContentLength() {
    return bytesDOC.length;
  }

  @Override
  public String getContentType() {
    return "application/docx";
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

  @Override
  public String getFileName() {
    StringBuffer fileName = new StringBuffer();
    fileName.append(this.getLoggedCrp().getAcronym() + "-POWBSummary-");
    fileName.append(this.getSelectedYear() + "_");
    fileName.append(new SimpleDateFormat("yyyyMMdd-HHmm").format(new Date()));
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

  public void getFpPlannedList(List<LiaisonInstitution> lInstitutions, long phaseID) {
    flagshipPlannedList = new ArrayList<>();

    if (projectExpectedStudyManager.findAll() != null) {
      List<ProjectExpectedStudy> expectedStudies = new ArrayList<>(projectExpectedStudyManager.findAll().stream()
        .filter(ps -> ps.isActive() && ps.getPhase().getId() == phaseID
          && ps.getProject().getGlobalUnitProjects().stream()
            .filter(gup -> gup.isActive() && gup.isOrigin()
              && gup.getGlobalUnit().getId().equals(this.getLoggedCrp().getId()))
            .collect(Collectors.toList()).size() > 0)
        .collect(Collectors.toList()));

      for (ProjectExpectedStudy projectExpectedStudy : expectedStudies) {
        PowbEvidencePlannedStudyDTO dto = new PowbEvidencePlannedStudyDTO();
        projectExpectedStudy.getProject()
          .setProjectInfo(projectExpectedStudy.getProject().getProjecInfoPhase(this.getActualPhase()));
        dto.setProjectExpectedStudy(projectExpectedStudy);
        if (projectExpectedStudy.getProject().getProjectInfo().getAdministrative() != null
          && projectExpectedStudy.getProject().getProjectInfo().getAdministrative()) {
          dto.setLiaisonInstitutions(new ArrayList<>());
          dto.getLiaisonInstitutions().add(this.pmuInstitution);
        } else {
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
        }

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

      List<PowbEvidencePlannedStudyDTO> removeList = new ArrayList<>();
      for (PowbEvidencePlannedStudyDTO dto : flagshipPlannedList) {

        List<LiaisonInstitution> removeLiaison = new ArrayList<>();
        for (LiaisonInstitution liaisonInstitution : dto.getLiaisonInstitutions()) {
          PowbSynthesis powbSynthesis = powbSynthesisManager.findSynthesis(phaseID, liaisonInstitution.getId());
          if (powbSynthesis != null) {
            if (powbSynthesis.getPowbEvidence() != null) {

              PowbEvidencePlannedStudy evidencePlannedStudyNew = new PowbEvidencePlannedStudy();
              evidencePlannedStudyNew = new PowbEvidencePlannedStudy();
              evidencePlannedStudyNew.setProjectExpectedStudy(dto.getProjectExpectedStudy());
              evidencePlannedStudyNew.setPowbEvidence(powbSynthesis.getPowbEvidence());

              if (evidencePlannedStudies.contains(evidencePlannedStudyNew)) {
                removeLiaison.add(liaisonInstitution);
              }
            }
          }
        }

        for (LiaisonInstitution li : removeLiaison) {
          dto.getLiaisonInstitutions().remove(li);
        }

        if (dto.getLiaisonInstitutions().isEmpty()) {
          removeList.add(dto);
        }
      }


      for (PowbEvidencePlannedStudyDTO i : removeList) {
        flagshipPlannedList.remove(i);
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


  public List<PowbExpenditureAreas> getPlannedBudgetAreas() {
    List<PowbExpenditureAreas> plannedBudgetAreasList = powbExpenditureAreasManager.findAll().stream()
      .filter(e -> e.isActive() && !e.getIsExpenditure()).collect(Collectors.toList());
    if (plannedBudgetAreasList != null) {
      return plannedBudgetAreasList;
    } else {
      return new ArrayList<>();
    }
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
            && d.getDeliverableInfo().getStatus().intValue() == Integer
              .parseInt(ProjectStatusEnum.Extended.getStatusId())
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
