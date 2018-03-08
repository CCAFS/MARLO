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
import org.cgiar.ccafs.marlo.data.manager.PowbSynthesisManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyManager;
import org.cgiar.ccafs.marlo.data.model.CrpMilestone;
import org.cgiar.ccafs.marlo.data.model.CrpOutcomeSubIdo;
import org.cgiar.ccafs.marlo.data.model.CrpPpaPartner;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.CrpProgramOutcome;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.PowbCrossCuttingDimension;
import org.cgiar.ccafs.marlo.data.model.PowbEvidence;
import org.cgiar.ccafs.marlo.data.model.PowbEvidencePlannedStudy;
import org.cgiar.ccafs.marlo.data.model.PowbEvidencePlannedStudyDTO;
import org.cgiar.ccafs.marlo.data.model.PowbExpectedCrpProgress;
import org.cgiar.ccafs.marlo.data.model.PowbSynthesis;
import org.cgiar.ccafs.marlo.data.model.PowbSynthesisSectionStatusEnum;
import org.cgiar.ccafs.marlo.data.model.ProgramType;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectBudgetsFlagship;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudy;
import org.cgiar.ccafs.marlo.data.model.ProjectFocus;
import org.cgiar.ccafs.marlo.data.model.ProjectStatusEnum;
import org.cgiar.ccafs.marlo.data.model.TypeExpectedStudiesEnum;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.POISummary;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
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

  // Parameters
  private POISummary poiSummary;
  private List<PowbSynthesis> powbSynthesisList;
  private LiaisonInstitution pmuInstitution;
  private PowbSynthesis powbSynthesisPMU;
  private long startTime;
  private XWPFDocument document = new XWPFDocument();
  private List<PowbEvidencePlannedStudyDTO> flagshipPlannedList;

  private List<CrpProgram> flagships;
  // Streams
  private InputStream inputStream;

  // DOC bytes
  private byte[] bytesDOC;

  public POWBPOISummaryAction(APConfig config, GlobalUnitManager crpManager, PhaseManager phaseManager,
    PowbExpectedCrpProgressManager powbExpectedCrpProgressManager,
    ProjectExpectedStudyManager projectExpectedStudyManager, PowbSynthesisManager powbSynthesisManager) {
    super(config, crpManager, phaseManager);
    poiSummary = new POISummary();
    this.powbExpectedCrpProgressManager = powbExpectedCrpProgressManager;
    this.projectExpectedStudyManager = projectExpectedStudyManager;
    this.powbSynthesisManager = powbSynthesisManager;
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

  public void createTableA() {
    this.loadTablePMU();

    String[] header = {this.getText("expectedProgress.tableA.fp"), this.getText("expectedProgress.tableA.subIDO"),
      this.getText("expectedProgress.tableA.outcomes"), this.getText("expectedProgress.tableA.milestone") + "*",
      "Budget  " + this.getText("expectedProgress.tableA.w1w2"),
      "Budget  " + this.getText("expectedProgress.tableA.w3bilateral"),
      this.getText("expectedProgress.tableA.assessment") + "**",
      this.getText("expectedProgress.tableA.meansVerification")};

    List<String> headers = Arrays.asList(header);

    String FP, subIDO = "", outcomes, milestone, assessment, meansVerifications;
    Double w1w2, w3Bilateral;

    List<List<String>> datas = new ArrayList<>();

    List<String> data;

    for (CrpProgram flagship : flagships) {
      data = new ArrayList<>();
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


          String[] sData = {FP, subIDO, outcomes, milestone, String.valueOf(round(w1w2, 2)),
            String.valueOf(round(w3Bilateral, 2)), assessment, meansVerifications};
          data = Arrays.asList(sData);


          datas.add(data);

          milestone_index++;
        }
        outcome_index++;
      }
    }


    poiSummary.textTable(document, headers, datas);
  }

  private void createTableB() {
    String[] header = {this.getText("evidenceRelevant.table.plannedTopic"),
      this.getText("evidenceRelevant.tablePlannedStudies.geographicScope"),
      this.getText("evidenceRelevant.tablePlannedStudies.relevant"),
      this.getText("evidenceRelevant.tablePlannedStudies.comments")};
    List<String> headers = Arrays.asList(header);

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
    poiSummary.textTable(document, headers, datas);
  }

  @Override
  public String execute() throws Exception {
    try {
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
      this.createTableA();
      poiSummary.textNotes(document.createParagraph(), "*" + this.getText("expectedProgress.tableA.milestone.help"));
      poiSummary.textNotes(document.createParagraph(), "**" + this.getText("expectedProgress.tableA.assessment.help"));
      poiSummary.textHead2Title(document.createParagraph(), this.getText("summaries.powb.tableB.title"));
      this.createTableB();
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

  @Override
  public String getFileName() {
    StringBuffer fileName = new StringBuffer();
    fileName.append("POWBSummary-");
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
      inputStream = new ByteArrayInputStream(bytesDOC);
    }
    return inputStream;
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


}
