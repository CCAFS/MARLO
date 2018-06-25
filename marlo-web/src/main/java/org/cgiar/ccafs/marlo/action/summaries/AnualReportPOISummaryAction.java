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
import org.cgiar.ccafs.marlo.data.manager.DeliverableIntellectualAssetManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.LiaisonInstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.PowbCrpStaffingCategoriesManager;
import org.cgiar.ccafs.marlo.data.manager.PowbExpectedCrpProgressManager;
import org.cgiar.ccafs.marlo.data.manager.PowbExpenditureAreasManager;
import org.cgiar.ccafs.marlo.data.manager.PowbSynthesisManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyInfoManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectFocusManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPartnerManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndSynthesisIndicatorManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisCrossCgiarCollaborationManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisCrossCgiarManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisCrpProgressManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisCrpProgressTargetManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisExternalPartnershipManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisFundingUseExpendituryAreaManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisManager;
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
import org.cgiar.ccafs.marlo.data.model.PowbEvidence;
import org.cgiar.ccafs.marlo.data.model.PowbEvidencePlannedStudy;
import org.cgiar.ccafs.marlo.data.model.PowbEvidencePlannedStudyDTO;
import org.cgiar.ccafs.marlo.data.model.PowbExpectedCrpProgress;
import org.cgiar.ccafs.marlo.data.model.PowbExpenditureAreas;
import org.cgiar.ccafs.marlo.data.model.PowbFinancialPlannedBudget;
import org.cgiar.ccafs.marlo.data.model.PowbSynthesis;
import org.cgiar.ccafs.marlo.data.model.PowbSynthesisSectionStatusEnum;
import org.cgiar.ccafs.marlo.data.model.ProgramType;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectBudgetsFlagship;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudy;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyInfo;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudySubIdo;
import org.cgiar.ccafs.marlo.data.model.ProjectFocus;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovation;
import org.cgiar.ccafs.marlo.data.model.ProjectStatusEnum;
import org.cgiar.ccafs.marlo.data.model.RepIndSynthesisIndicator;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesis;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisCrossCgiar;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisCrossCgiarCollaboration;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisCrossCuttingDimension;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisCrpProgress;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisCrpProgressTarget;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisExternalPartnership;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisExternalPartnershipDTO;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFinancialSummary;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFinancialSummaryBudget;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFundingUseExpendituryArea;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFundingUseSummary;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisGovernance;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisMelia;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisProgramVariance;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisRisk;
import org.cgiar.ccafs.marlo.data.model.SrfSloIndicatorTarget;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.POIField;
import org.cgiar.ccafs.marlo.utils.POISummary;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
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
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STHdrFtr;
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
  private PowbExpectedCrpProgressManager powbExpectedCrpProgressManager;
  private ProjectExpectedStudyManager projectExpectedStudyManager;
  private PowbSynthesisManager powbSynthesisManager;
  private PowbExpenditureAreasManager powbExpenditureAreasManager;
  private LiaisonInstitutionManager liaisonInstitutionManager;
  private PowbCrpStaffingCategoriesManager powbCrpStaffingCategoriesManager;
  private ReportSynthesisManager reportSynthesisManager;
  private ReportSynthesisCrpProgressTargetManager reportSynthesisCrpProgressTargetManager;
  private RepIndSynthesisIndicatorManager repIndSynthesisIndicatorManager;
  private ProjectExpectedStudyInfoManager projectExpectedStudyInfoManager;
  private ReportSynthesisFundingUseExpendituryAreaManager reportSynthesisFundingUseExpendituryAreaManager;
  private ProjectInnovationManager projectInnovationManager;
  private ProjectManager projectManager;
  private ProjectFocusManager projectFocusManager;
  private ReportSynthesisCrossCgiarManager reportSynthesisCrossCgiarManager;
  private DeliverableIntellectualAssetManager deliverableIntellectualAssetManager;
  private ProjectPartnerManager projectPartnerManager;
  private ReportSynthesisExternalPartnershipManager reportSynthesisExternalPartnershipManager;
  private ReportSynthesisMeliaManager reportSynthesisMeliaManager;
  private ReportSynthesisCrossCgiarCollaborationManager reportSynthesisCrossCgiarCollaborationManager;

  // Parameters
  private POISummary poiSummary;
  private List<PowbSynthesis> powbSynthesisList;
  private List<ReportSynthesis> reportSysthesisList;
  private LiaisonInstitution pmuInstitution;
  private PowbSynthesis powbSynthesisPMU;
  private ReportSynthesis reportSynthesisPMU;
  private long startTime;
  private XWPFDocument document;
  // private List<PowbEvidencePlannedStudyDTO> flagshipPlannedList;
  private List<DeliverableInfo> deliverableList;
  private CrossCuttingDimensionTableDTO tableC;
  private NumberFormat currencyFormat;
  private DecimalFormat percentageFormat;
  private List<CrpProgram> flagships;

  private List<PowbEvidencePlannedStudyDTO> flagshipPlannedList;
  private List<ReportSynthesisCrpProgressTarget> fpSynthesisTable;
  private List<ReportSynthesisCrpProgress> flagshipCrpProgress;
  private ReportSynthesisCrpProgressManager reportSynthesisCrpProgressManager;
  private List<SrfSloIndicatorTarget> sloTargets;
  private SrfSloIndicatorTargetManager srfSloIndicatorTargetManager;
  private List<Deliverable> deliverables;
  private List<ProjectInnovation> innovationsList;
  private List<DeliverableIntellectualAsset> assetsList;
  private List<ReportSynthesisExternalPartnership> flagshipExternalPartnerships;
  private List<ReportSynthesisExternalPartnershipDTO> flagshipExternalPlannedList;
  private List<LiaisonInstitution> liaisonInstitutions;

  // Parameter for tables E and F
  Double totalCarry = 0.0, totalw1w2 = 0.0, totalw3Bilateral = 0.0, totalCenter = 0.0, grandTotal = 0.0;
  // Streams
  private InputStream inputStream;

  // DOC bytes
  private byte[] bytesDOC;

  public AnualReportPOISummaryAction(APConfig config, GlobalUnitManager crpManager, PhaseManager phaseManager,
    PowbExpectedCrpProgressManager powbExpectedCrpProgressManager,
    ProjectExpectedStudyManager projectExpectedStudyManager, PowbSynthesisManager powbSynthesisManager,
    PowbExpenditureAreasManager powbExpenditureAreasManager, LiaisonInstitutionManager liaisonInstitutionManager,
    PowbCrpStaffingCategoriesManager powbCrpStaffingCategoriesManager, ReportSynthesisManager reportSynthesisManager,
    SrfSloIndicatorTargetManager srfSloIndicatorTargetManager,
    ReportSynthesisCrpProgressTargetManager reportSynthesisCrpProgressTargetManager,
    RepIndSynthesisIndicatorManager repIndSynthesisIndicatorManager,
    ProjectExpectedStudyInfoManager projectExpectedStudyInfoManager,
    ReportSynthesisFundingUseExpendituryAreaManager reportSynthesisFundingUseExpendituryAreaManager,
    ProjectInnovationManager projectInnovationManager, ProjectManager projectManager,
    ProjectFocusManager projectFocusManager, ReportSynthesisCrossCgiarManager reportSynthesisCrossCgiarManager,
    DeliverableIntellectualAssetManager deliverableIntellectualAssetManager,
    ProjectPartnerManager projectPartnerManager,
    ReportSynthesisExternalPartnershipManager reportSynthesisExternalPartnershipManager,
    ReportSynthesisMeliaManager reportSynthesisMeliaManager,
    ReportSynthesisCrossCgiarCollaborationManager reportSynthesisCrossCgiarCollaborationManager) {

    super(config, crpManager, phaseManager);
    document = new XWPFDocument();
    poiSummary = new POISummary();
    currencyFormat = NumberFormat.getCurrencyInstance();
    percentageFormat = new DecimalFormat("##.##%");
    this.powbExpectedCrpProgressManager = powbExpectedCrpProgressManager;
    this.projectExpectedStudyManager = projectExpectedStudyManager;
    this.powbSynthesisManager = powbSynthesisManager;
    this.powbExpenditureAreasManager = powbExpenditureAreasManager;
    this.liaisonInstitutionManager = liaisonInstitutionManager;
    this.powbCrpStaffingCategoriesManager = powbCrpStaffingCategoriesManager;
    this.reportSynthesisManager = reportSynthesisManager;
    this.srfSloIndicatorTargetManager = srfSloIndicatorTargetManager;
    this.reportSynthesisCrpProgressTargetManager = reportSynthesisCrpProgressTargetManager;
    this.repIndSynthesisIndicatorManager = repIndSynthesisIndicatorManager;
    this.projectExpectedStudyInfoManager = projectExpectedStudyInfoManager;
    this.reportSynthesisFundingUseExpendituryAreaManager = reportSynthesisFundingUseExpendituryAreaManager;
    this.projectInnovationManager = projectInnovationManager;
    this.projectManager = projectManager;
    this.projectFocusManager = projectFocusManager;
    this.reportSynthesisCrossCgiarManager = reportSynthesisCrossCgiarManager;
    this.deliverableIntellectualAssetManager = deliverableIntellectualAssetManager;
    this.projectPartnerManager = projectPartnerManager;
    this.reportSynthesisExternalPartnershipManager = reportSynthesisExternalPartnershipManager;
    this.reportSynthesisMeliaManager = reportSynthesisMeliaManager;
    this.reportSynthesisCrossCgiarCollaborationManager = reportSynthesisCrossCgiarCollaborationManager;
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
      if (powbSynthesisPMU.getPowbToc() != null && powbSynthesisPMU.getPowbToc().getFile() != null) {
        poiSummary.textHyperlink(
          this.getPowbPath(powbSynthesisPMU.getLiaisonInstitution(),
            this.getLoggedCrp().getAcronym() + "_"
              + PowbSynthesisSectionStatusEnum.TOC_ADJUSTMENTS.getStatus().toString())
            + powbSynthesisPMU.getPowbToc().getFile().getFileName().replaceAll(" ", "%20"),
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

    if (reportSynthesisPMU != null) {
      String synthesisCrpDescription = "";
      String synthesisCrpSummaries = "";

      synthesisCrpDescription = reportSynthesisPMU.getReportSynthesisCrpProgress().getOverallProgress();
      synthesisCrpSummaries = reportSynthesisPMU.getReportSynthesisFlagshipProgress().getSummary();
      // CRP Progress
      List<ReportSynthesisCrpProgress> reportSynthesisCrpProgressList = null;
      // reportSysthesisPMU.getReportSynthesisCrpProgress().stream().filter(e ->
      // e.isActive()).collect(Collectors.toList());

      if (reportSynthesisCrpProgressList != null && !reportSynthesisCrpProgressList.isEmpty()) {
        ReportSynthesisCrpProgress reportSynthesisCrpProgress = reportSynthesisCrpProgressList.get(0);
        synthesisCrpDescription = reportSynthesisCrpProgress.getOverallProgress() != null
          && !reportSynthesisCrpProgress.getOverallProgress().trim().isEmpty()
            ? reportSynthesisCrpProgress.getOverallProgress() : "";
      }
      poiSummary.textParagraph(document.createParagraph(), synthesisCrpDescription);

      // poiSummary.textParagraph(document.createParagraph(), synthesisCrpSummaries);
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
        if (powbSynthesis.getPowbFlagshipPlans() != null
          && powbSynthesis.getPowbFlagshipPlans().getFlagshipProgramFile() != null) {
          poiSummary.textHyperlink(
            this.getPowbPath(liaisonInstitution,
              this.getLoggedCrp().getAcronym() + "_"
                + PowbSynthesisSectionStatusEnum.FLAGSHIP_PLANS.getStatus().toString())
              + powbSynthesis.getPowbFlagshipPlans().getFlagshipProgramFile().getFileName().replaceAll(" ", "%20"),
            "URL: " + powbSynthesis.getPowbFlagshipPlans().getFlagshipProgramFile().getFileName(),
            document.createParagraph());
        }
      }
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
    List<ReportSynthesisMelia> reportSynthesisMeliaList = reportSynthesisMeliaManager.findAll();
    String studies = "", status = "", comments = "";
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
      // TODO Auto-generated catch block
      e.printStackTrace();
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

      int flagshipIndex = 0;
      data = new ArrayList<>();
      List<ReportSynthesisCrpProgressTarget> listCrpProgressTargets = reportSynthesisCrpProgressTargetManager.findAll();

      for (int i = 0; i < sloTargets.size(); i++) {

        if (sloTargets.get(i).getTargetsIndicator() != null && !sloTargets.get(i).getTargetsIndicator().isEmpty()) {
          targetsIndicator = sloTargets.get(i).getTargetsIndicator();
        }
        sloTarget = targetsIndicator + " " + sloTargets.get(i).getNarrative();
        String synthesisCrpBriefSummaries = "";
        String synthesisCrpTargets = "";
        for (int j = 0; j < listCrpProgressTargets.size(); j++) {
          if (listCrpProgressTargets.get(j).getSrfSloIndicatorTarget().getId() == sloTargets.get(i).getId()
            && listCrpProgressTargets.get(j).getSrfSloIndicatorTarget().isActive() == true) {
            try {

              synthesisCrpBriefSummaries += listCrpProgressTargets.get(j).getBirefSummary() + "\n";
              synthesisCrpTargets += listCrpProgressTargets.get(j).getAdditionalContribution() + "\n";

            } catch (Exception e) {

            }
          }
        }

        briefSummaries = synthesisCrpBriefSummaries;
        additionalContribution = synthesisCrpTargets;

        Boolean bold = false;
        String blackColor = "000000";
        String redColor = "c00000";
        String blueColor = "000099";
        POIField[] sData = {new POIField(sloTarget, ParagraphAlignment.LEFT, bold, blackColor),
          new POIField(briefSummaries, ParagraphAlignment.LEFT, bold, blueColor),
          new POIField(additionalContribution, ParagraphAlignment.LEFT, bold, blueColor)};
        data = Arrays.asList(sData);
        datas.add(data);
        flagshipIndex++;
      }
    }

    poiSummary.textTable(document, headers, datas, true, "tableAAnnualReport");
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
    List<ProjectExpectedStudyInfo> projectExpectedStudyInfoList = null;
    List<ProjectExpectedStudy> projectExpectedStudyList = null;
    List<ProjectExpectedStudySubIdo> projectExpectedStudySubIdoList = null;

    projectExpectedStudyList = projectExpectedStudyManager.findAll();

    // System.out.println("actual phase " + this.getActualPhase());
    List<POIField> header = Arrays.asList(sHeader);
    headers.add(header);
    String title = "", subIdo = "", describeGender = "";

    List<List<POIField>> datas = new ArrayList<>();

    List<POIField> data;
    if (projectExpectedStudyList != null && !projectExpectedStudyList.isEmpty()) {
      for (int i = 0; i < projectExpectedStudyList.size(); i++) {


        if (projectExpectedStudyList.get(i).getProjectExpectedStudyInfo(this.getActualPhase()) != null) {

          if (projectExpectedStudyList.get(i).getProjectExpectedStudyInfo(this.getActualPhase()).getStudyType() != null
            && projectExpectedStudyList.get(i).getProjectExpectedStudyInfo(this.getActualPhase()).getStudyType()
              .getId() == 1) {

            data = new ArrayList<>();

            try {
              title = projectExpectedStudyList.get(i).getProjectExpectedStudyInfo(this.getActualPhase()).getTitle();

              projectExpectedStudyList.get(i)
                .setSubIdos(new ArrayList<>(projectExpectedStudyList.get(i).getProjectExpectedStudySubIdos().stream()
                  .filter(s -> s.getPhase().getId() == this.getActualPhase().getId()).collect(Collectors.toList())));

              for (int j = 0; j < projectExpectedStudyList.get(i).getSubIdos().size(); j++) {
                subIdo += projectExpectedStudyList.get(i).getSubIdos().get(i).getSrfSubIdo().getDescription() + "\n";
              }

              // describeGender = projectExpectedStudyList.get(i).

            } catch (Exception e) {
              // System.out.println(e);
            }
          }

          Boolean bold = false;
          String blackColor = "000000";
          String redColor = "c00000";
          POIField[] sData =
            {new POIField(title, ParagraphAlignment.LEFT), new POIField(subIdo, ParagraphAlignment.CENTER),
              new POIField(describeGender, ParagraphAlignment.CENTER), new POIField("", ParagraphAlignment.CENTER)};
          data = Arrays.asList(sData);
          datas.add(data);
        }
      }
    }


    /** using crp progress action manager **/
    /*
     * for (int i = 0; i < flagshipPlannedList.size(); i++) {
     * flagshipPlannedList.get(i).getProjectExpectedStudy().getProjectExpectedStudyInfo(this.getActualPhase());
     * title = flagshipPlannedList.get(i).getProjectExpectedStudy().getProjectExpectedStudyInfo(this.getActualPhase())
     * .getTitle();
     * Boolean bold = false;
     * String blackColor = "000000";
     * String redColor = "c00000";
     * POIField[] sData = {new POIField(title, ParagraphAlignment.CENTER), new POIField("", ParagraphAlignment.CENTER),
     * new POIField("", ParagraphAlignment.CENTER), new POIField("", ParagraphAlignment.CENTER)};
     * data = Arrays.asList(sData);
     * datas.add(data);
     * }
     */

    poiSummary.textTable(document, headers, datas, false, "tableA2AnnualReport");
  }

  private void createTableB() {

    List<List<POIField>> headers = new ArrayList<>();

    POIField[] sHeader = {new POIField(this.getText("expectedProgress.tableA.fp"), ParagraphAlignment.LEFT),
      new POIField(this.getText("expectedProgress.tableA.subIDO"), ParagraphAlignment.LEFT),
      new POIField(this.getText("expectedProgress.tableA.outcomes"), ParagraphAlignment.LEFT),
      new POIField(this.getText("expectedProgress.tableA.milestone") + "*", ParagraphAlignment.LEFT),
      new POIField(this.getText("summaries.annualReport.tableB.field5"), ParagraphAlignment.LEFT),
      new POIField(this.getText("summaries.annualReport.tableB.field6"), ParagraphAlignment.LEFT)};

    List<POIField> header = Arrays.asList(sHeader);
    headers.add(header);
    String FP, outcomes, milestone, assessment, subIDO = "", meansVerifications;

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

          PowbExpectedCrpProgress milestoneProgress =
            this.getPowbExpectedCrpProgressProgram(crpMilestone.getId(), flagship.getId());

          POIField[] sData = {new POIField(FP, ParagraphAlignment.LEFT), new POIField(subIDO, ParagraphAlignment.LEFT),
            new POIField(outcomes, ParagraphAlignment.LEFT), new POIField(milestone, ParagraphAlignment.LEFT),
            new POIField("", ParagraphAlignment.LEFT), new POIField("", ParagraphAlignment.LEFT)};
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
    this.tableCInfo(this.getSelectedPhase());

    if (tableC != null) {
      POIField[] sData = {new POIField("Gender", ParagraphAlignment.LEFT),
        new POIField(percentageFormat.format(round(tableC.getPercentageGenderPrincipal() / 100, 4)),
          ParagraphAlignment.CENTER),
        new POIField(percentageFormat.format(round(tableC.getPercentageGenderSignificant() / 100, 4)),
          ParagraphAlignment.CENTER),
        new POIField(percentageFormat.format(round(tableC.getPercentageGenderNotScored() / 100, 4)),
          ParagraphAlignment.CENTER),
        new POIField(String.valueOf((int) tableC.getTotal()), ParagraphAlignment.LEFT)};
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
    try {
      listRepIndSynthesis = repIndSynthesisIndicatorManager.findAll();
    } catch (Exception e) {

    }

    List<POIField> header = Arrays.asList(sHeader);
    headers.add(header);
    String type = "", indicator = "", name = "", description = "", comments = "", lastType = "";

    List<List<POIField>> datas = new ArrayList<>();
    List<POIField> data;
    if (listRepIndSynthesis != null && !listRepIndSynthesis.isEmpty()) {
      for (int i = 0; i < listRepIndSynthesis.size(); i++) {

        data = new ArrayList<>();
        type = listRepIndSynthesis.get(i).getType();

        if (type.equals(lastType)) {
          type = "";
        } else {
          lastType = type;
        }

        indicator = listRepIndSynthesis.get(i).getIndicator();
        description = listRepIndSynthesis.get(i).getDescription();
        name = listRepIndSynthesis.get(i).getName();

        Boolean bold = false;
        String blackColor = "000000";
        String redColor = "c00000";
        POIField[] sData = {new POIField(type, ParagraphAlignment.CENTER, bold, blackColor),
          new POIField(indicator + "." + name, ParagraphAlignment.LEFT, bold, blackColor),
          new POIField(description, ParagraphAlignment.LEFT, bold, blackColor),
          new POIField(comments, ParagraphAlignment.CENTER, bold, redColor)};
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
        try {
          title = projectInnovationList.get(i).getProjectInnovationInfo(this.getActualPhase()).getTitle();
          stage = projectInnovationList.get(i).getProjectInnovationInfo(this.getActualPhase())
            .getRepIndStageInnovation().getName();
          degree = projectInnovationList.get(i).getProjectInnovationInfo(this.getActualPhase())
            .getRepIndDegreeInnovation().getName();
          contribution = projectInnovationList.get(i).getProjectInnovationInfo(this.getActualPhase())
            .getRepIndContributionOfCrp().getName();
          geographicScope = projectInnovationList.get(i).getProjectInnovationInfo(this.getActualPhase())
            .getRepIndGeographicScope().getName();

          POIField[] sData =
            {new POIField(title, ParagraphAlignment.CENTER), new POIField(stage, ParagraphAlignment.CENTER),
              new POIField(degree, ParagraphAlignment.LEFT), new POIField(contribution, ParagraphAlignment.LEFT),
              new POIField(geographicScope, ParagraphAlignment.LEFT)};

          data = Arrays.asList(sData);
          datas.add(data);
        } catch (Exception e) {
          System.out.println();
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
    System.out.println("assets list " + assetsList.size());
    for (int i = 0; i < assetsList.size(); i++) {
      try {
        title = assetsList.get(i).getTitle();
        applicant = assetsList.get(i).getApplicant();
        patent = assetsList.get(i).getApplicant();
        aditional = assetsList.get(i).getAdditionalInformation();
        registration = assetsList.get(i).getLink();
        communication = assetsList.get(i).getPublicCommunication();

        POIField[] sData = {new POIField(title, ParagraphAlignment.CENTER),
          new POIField(applicant, ParagraphAlignment.LEFT), new POIField(patent, ParagraphAlignment.LEFT),
          new POIField(aditional, ParagraphAlignment.LEFT), new POIField(registration, ParagraphAlignment.CENTER),
          new POIField(communication, ParagraphAlignment.LEFT)};
        data = Arrays.asList(sData);
        datas.add(data);
      } catch (Exception e) {

      }
    }
    poiSummary.textTable(document, headers, datas, false, "tableAAnnualReport");
  }


  private void createTableETestFinal() {

    List<List<POIField>> headers = new ArrayList<>();
    POIField[] sHeader = {new POIField("", ParagraphAlignment.CENTER),
      new POIField(
        this.getText("financialPlan.tableE.plannedBudget", new String[] {String.valueOf(this.getSelectedYear())}),
        ParagraphAlignment.CENTER),
      new POIField("", ParagraphAlignment.CENTER), new POIField("", ParagraphAlignment.CENTER),
      new POIField("", ParagraphAlignment.CENTER), new POIField("", ParagraphAlignment.CENTER),
      new POIField(this.getText("financialPlan.tableE.comments"), ParagraphAlignment.CENTER)};
    POIField[] sHeader2 = {new POIField(" ", ParagraphAlignment.CENTER),
      new POIField(
        this.getText("financialPlan.tableE.carryOver", new String[] {String.valueOf(this.getSelectedYear() - 1)}),
        ParagraphAlignment.CENTER),
      new POIField(this.getText("financialPlan.tableE.w1w2"), ParagraphAlignment.CENTER),
      new POIField(this.getText("financialPlan.tableE.w3bilateral"), ParagraphAlignment.CENTER),
      new POIField(this.getText("financialPlan.tableE.centerFunds"), ParagraphAlignment.CENTER),
      new POIField(this.getText("financialPlan.tableE.total"), ParagraphAlignment.CENTER),
      new POIField(" ", ParagraphAlignment.CENTER)};

    List<POIField> header = Arrays.asList(sHeader);
    List<POIField> header2 = Arrays.asList(sHeader2);
    headers.add(header);
    headers.add(header2);

    List<List<POIField>> datas = new ArrayList<>();
    List<POIField> data;
    powbSynthesisPMU.setPowbFinancialPlannedBudgetList(powbSynthesisPMU.getPowbFinancialPlannedBudget().stream()
      .filter(fp -> fp.isActive()).collect(Collectors.toList()));
    // Flagships
    List<LiaisonInstitution> flagships = this.getFlagships();
    if (flagships != null && !flagships.isEmpty()) {
      for (LiaisonInstitution flagship : flagships) {
        Double carry = 0.0, w1w2 = 0.0, w3Bilateral = 0.0, center = 0.0, total = 0.0;
        String category = "", comments = "";
        category = flagship.getAcronym();
        PowbFinancialPlannedBudget powbFinancialPlannedBudget =
          this.getPowbFinancialPlanBudgetTest(flagship.getId(), true);
        if (powbFinancialPlannedBudget != null) {
          w1w2 = powbFinancialPlannedBudget.getW1w2() != null ? powbFinancialPlannedBudget.getW1w2() : 0.0;
          carry = powbFinancialPlannedBudget.getCarry() != null ? powbFinancialPlannedBudget.getCarry() : 0.0;
          w3Bilateral =
            powbFinancialPlannedBudget.getW3Bilateral() != null ? powbFinancialPlannedBudget.getW3Bilateral() : 0.0;
          center =
            powbFinancialPlannedBudget.getCenterFunds() != null ? powbFinancialPlannedBudget.getCenterFunds() : 0.0;
          total = powbFinancialPlannedBudget.getTotalPlannedBudget() != null
            ? powbFinancialPlannedBudget.getTotalPlannedBudget() : 0.0;
          comments = powbFinancialPlannedBudget.getComments() == null
            || powbFinancialPlannedBudget.getComments().trim().isEmpty() ? " "
              : powbFinancialPlannedBudget.getComments();
        }
        totalCarry += carry;
        totalw1w2 += w1w2;
        totalw3Bilateral += w3Bilateral;
        totalCenter += center;
        grandTotal += total;
        POIField[] sData = {new POIField(category, ParagraphAlignment.CENTER),
          new POIField(currencyFormat.format(round(carry, 2)), ParagraphAlignment.CENTER),
          new POIField(currencyFormat.format(round(w1w2, 2)), ParagraphAlignment.CENTER),
          new POIField(currencyFormat.format(round(w3Bilateral, 2)), ParagraphAlignment.CENTER),
          new POIField(currencyFormat.format(round(center, 2)), ParagraphAlignment.CENTER),
          new POIField(currencyFormat.format(round(total, 2)), ParagraphAlignment.CENTER),
          new POIField(comments, ParagraphAlignment.LEFT)};

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
        PowbFinancialPlannedBudget powbFinancialPlannedBudget =
          this.getPowbFinancialPlanBudgetTest(powbExpenditureArea.getId(), false);
        if (powbFinancialPlannedBudget != null) {
          w1w2 = powbFinancialPlannedBudget.getW1w2() != null ? powbFinancialPlannedBudget.getW1w2() : 0.0;
          carry = powbFinancialPlannedBudget.getCarry() != null ? powbFinancialPlannedBudget.getCarry() : 0.0;
          w3Bilateral =
            powbFinancialPlannedBudget.getW3Bilateral() != null ? powbFinancialPlannedBudget.getW3Bilateral() : 0.0;
          center =
            powbFinancialPlannedBudget.getCenterFunds() != null ? powbFinancialPlannedBudget.getCenterFunds() : 0.0;
          total = powbFinancialPlannedBudget.getTotalPlannedBudget() != null
            ? powbFinancialPlannedBudget.getTotalPlannedBudget() : 0.0;
          comments = powbFinancialPlannedBudget.getComments() == null
            || powbFinancialPlannedBudget.getComments().trim().isEmpty() ? " "
              : powbFinancialPlannedBudget.getComments();
        }
        totalCarry += carry;
        totalw1w2 += w1w2;
        totalw3Bilateral += w3Bilateral;
        totalCenter += center;
        grandTotal += total;
        POIField[] sData = {new POIField(category, ParagraphAlignment.CENTER),
          new POIField(currencyFormat.format(round(carry, 2)), ParagraphAlignment.CENTER),
          new POIField(currencyFormat.format(round(w1w2, 2)), ParagraphAlignment.CENTER),
          new POIField(currencyFormat.format(round(w3Bilateral, 2)), ParagraphAlignment.CENTER),
          new POIField(currencyFormat.format(round(center, 2)), ParagraphAlignment.CENTER),
          new POIField(currencyFormat.format(round(total, 2)), ParagraphAlignment.CENTER),
          new POIField(comments, ParagraphAlignment.LEFT)};

        data = Arrays.asList(sData);
        datas.add(data);

      }
    }

    Boolean bold = true;
    String blackColor = "000000";
    POIField[] sData = {new POIField("CRP Total", ParagraphAlignment.CENTER, bold, blackColor),
      new POIField(currencyFormat.format(round(totalCarry, 2)), ParagraphAlignment.CENTER, bold, blackColor),
      new POIField(currencyFormat.format(round(totalw1w2, 2)), ParagraphAlignment.CENTER, bold, blackColor),
      new POIField(currencyFormat.format(round(totalw3Bilateral, 2)), ParagraphAlignment.CENTER, bold, blackColor),
      new POIField(currencyFormat.format(round(totalCenter, 2)), ParagraphAlignment.CENTER, bold, blackColor),
      new POIField(currencyFormat.format(round(grandTotal, 2)), ParagraphAlignment.CENTER, bold, blackColor),
      new POIField(" ", ParagraphAlignment.LEFT, bold, blackColor)};

    data = Arrays.asList(sData);
    datas.add(data);

    poiSummary.textTable(document, headers, datas, false, "tableE");
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


    Boolean bold = true;
    String blackColor = "000000";

    POIField[] sData = {new POIField("Total Funding (Amount)", ParagraphAlignment.LEFT, bold, blackColor),
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
    flagshipExternalPartnerships = new ArrayList<>();
    List<List<POIField>> datas = new ArrayList<>();

    List<POIField> data;
    String FP = "", stage = "", phase = "", partner = "", geographic = "", mainArea = "";


    flagshipExternalPartnerships = reportSynthesisExternalPartnershipManager.findAll();

    /*
     * flagshipExternalPlannedList = reportSynthesisExternalPartnershipManager.getPlannedPartnershipList(
     * liaisonInstitutions, this.getActualPhase().getId(), this.getLoggedCrp(), pmuInstitution);
     * Flagship External Partnership Synthesis Progress
     * flagshipExternalPartnerships = reportSynthesisExternalPartnershipManager
     * .getFlagshipCExternalPartnership(liaisonInstitutions, this.getActualPhase().getId());
     */
    if (flagshipExternalPartnerships != null && !flagshipExternalPartnerships.isEmpty()) {
      for (int i = 0; i < flagshipExternalPlannedList.size(); i++) {


        try {

          if (flagshipExternalPartnerships.get(i).getReportSynthesis().getPhase().getId() == this.getActualPhase()
            .getId() && flagshipExternalPartnerships.get(i).isActive()) {
            for (int j = 0; j < flagshipExternalPartnerships.get(j).getPartnerships().size(); j++) {
              geographic += flagshipExternalPartnerships.get(i).getPartnerships().get(j).getProjectPartnerPartnership()
                .getGeographicScope().getName();
              partner += flagshipExternalPartnerships.get(i).getPartnerPartnerships().get(j).getProjectPartner()
                .getInstitution().getName();
              // stage = flagshipExternalPartnerships.get(i);
            }


          }
          /*
           * for (int j = 0; j < flagshipExternalPartnerships.get(j).getPartnerships().size(); j++) {
           * geographic += flagshipExternalPartnerships.get(i).getPartnerships().get(j).getProjectPartnerPartnership()
           * .getGeographicScope().getName();
           * partner += flagshipExternalPartnerships.get(i).getPartnerPartnerships().get(j).getProjectPartner()
           * .getInstitution().getName();
           * // stage = flagshipExternalPartnerships.get(i);
           * }
           */


          POIField[] sData = {new POIField(FP, ParagraphAlignment.CENTER),
            new POIField(stage, ParagraphAlignment.CENTER), new POIField(partner, ParagraphAlignment.CENTER),
            new POIField(geographic, ParagraphAlignment.CENTER), new POIField(mainArea, ParagraphAlignment.CENTER)};
          data = Arrays.asList(sData);
          datas.add(data);

        } catch (Exception e) {

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
    /*
     * List<List<POIField>> headers = new ArrayList<>();
     * List<List<POIField>> datas = new ArrayList<>();
     * List<POIField> data = new ArrayList<>();
     * POIField[] sHeader = {
     * new POIField(
     * this.getText("annualReport.melia.tableI.studies", new String[] {String.valueOf(this.getSelectedYear())}),
     * ParagraphAlignment.CENTER),
     * new POIField(this.getText("annualReport.melia.tableI.status"), ParagraphAlignment.CENTER),
     * new POIField(this.getText("annualReport.melia.tableI.comments"), ParagraphAlignment.CENTER)};
     * List<POIField> header = Arrays.asList(sHeader);
     * headers.add(header);
     * List<ProjectExpectedStudyInfo> ProjectExpectedStudyInfoList = new ArrayList<>();
     * try {
     * ProjectExpectedStudyInfoList = projectExpectedStudyInfoManager.findAll();
     * } catch (Exception e) {
     * }
     * try {
     * if (ProjectExpectedStudyInfoList != null) {
     * for (int i = 0; i < ProjectExpectedStudyInfoList.size(); i++) {
     * String studies = "", status = "", comments = "";
     * if (ProjectExpectedStudyInfoList.get(i).getPhase().getId() == 11
     * && (ProjectExpectedStudyInfoList.get(i).getTitle() != null
     * || !ProjectExpectedStudyInfoList.get(i).getTitle().isEmpty())) {
     * studies = ProjectExpectedStudyInfoList.get(i).getTitle();
     * status = ProjectExpectedStudyInfoList.get(i).getStatusName();
     * comments = ProjectExpectedStudyInfoList.get(i).getTopLevelComments();
     * POIField[] sData = {new POIField(studies, ParagraphAlignment.CENTER),
     * new POIField(status, ParagraphAlignment.LEFT), new POIField(comments, ParagraphAlignment.LEFT)};
     * data = Arrays.asList(sData);
     * datas.add(data);
     * }
     * }
     * }
     * } catch (Exception e) {
     * }
     * poiSummary.textTable(document, headers, datas, false, "tableAAnnualReport");
     */
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
    for (PowbSynthesis powbSynthesis : powbSynthesisList) {

      String crpPlatform = " ", descriptionCollaboration = " ", relevantFP = " ";

      POIField[] sData = {new POIField(crpPlatform, ParagraphAlignment.CENTER),
        new POIField(descriptionCollaboration, ParagraphAlignment.LEFT),
        new POIField(relevantFP, ParagraphAlignment.LEFT), new POIField("", ParagraphAlignment.LEFT),
        new POIField("", ParagraphAlignment.LEFT), new POIField("", ParagraphAlignment.LEFT)};

      data = Arrays.asList(sData);
      datas.add(data);


    }
    poiSummary.textTable(document, headers, datas, false, "tableAAnnualReport");
  }

  private void createTableJ() {
    this.getInformationTableJ();
    List<List<POIField>> headers = new ArrayList<>();
    POIField[] sHeader = {new POIField("", ParagraphAlignment.CENTER),
      new POIField(
        this.getText("annualReport.financial.tableJ.budget", new String[] {String.valueOf(this.getSelectedYear())}),
        ParagraphAlignment.CENTER),
      new POIField("", ParagraphAlignment.CENTER), new POIField("", ParagraphAlignment.CENTER),
      new POIField(this.getText("annualReport.financial.tableJ.expenditure"), ParagraphAlignment.CENTER),
      new POIField("", ParagraphAlignment.CENTER), new POIField("", ParagraphAlignment.CENTER),
      new POIField(this.getText("annualReport.financial.tableJ.difference"), ParagraphAlignment.CENTER),
      new POIField("", ParagraphAlignment.CENTER), new POIField("", ParagraphAlignment.CENTER),};

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
    try {
      reportSynthesisPMU.getReportSynthesisFinancialSummary().getBudgets().stream().filter(fp -> fp.isActive())
        .collect(Collectors.toList());
      // Flagships
      List<LiaisonInstitution> flagships = this.getFlagships();
      if (flagships != null && !flagships.isEmpty()) {
        System.out.println("Hola prueba 1");
        for (LiaisonInstitution flagship : flagships) {
          System.out.println("Hola prueba 1");
          Double carry = 0.0, w1w2 = 0.0, w3Bilateral = 0.0, total = 0.0;
          String category = "";
          category = flagship.getAcronym();
          ReportSynthesisFinancialSummaryBudget powbFinancialPlannedBudget =
            this.reportSynthesisPMU.getReportSynthesisFinancialSummary().getBudgets().get(0);
          if (powbFinancialPlannedBudget != null) {
            w1w2 = powbFinancialPlannedBudget.getW1Actual() != null ? powbFinancialPlannedBudget.getW1Actual() : 0.0;
            // carry = powbFinancialPlannedBudget.getCarry() != null ? powbFinancialPlannedBudget.getCarry() : 0.0;
            w3Bilateral =
              powbFinancialPlannedBudget.getW3Actual() != null ? powbFinancialPlannedBudget.getW3Actual() : 0.0;
            // total = powbFinancialPlannedBudget.getTotalPlannedBudget() != null
            // ? powbFinancialPlannedBudget.getTotalPlannedBudget() : 0.0;
          }
          totalCarry += carry;
          totalw1w2 += w1w2;
          totalw3Bilateral += w3Bilateral;
          grandTotal += total;
          POIField[] sData = {new POIField(category, ParagraphAlignment.CENTER),
            new POIField(currencyFormat.format(round(w1w2, 2)), ParagraphAlignment.CENTER),
            new POIField(currencyFormat.format(round(w3Bilateral, 2)), ParagraphAlignment.CENTER),
            new POIField(currencyFormat.format(round(total, 2)), ParagraphAlignment.CENTER)};

          data = Arrays.asList(sData);
          datas.add(data);
        }
      }

      // Expenditure areas
      List<PowbExpenditureAreas> powbExpenditureAreas = this.getPlannedBudgetAreas();
      if (powbExpenditureAreas != null && !powbExpenditureAreas.isEmpty()) {
        for (PowbExpenditureAreas powbExpenditureArea : powbExpenditureAreas) {

          Double carry = 0.0, w1w2 = 0.0, w3Bilateral = 0.0, total = 0.0;
          String category = "";
          category = powbExpenditureArea.getExpenditureArea();

          ReportSynthesisFinancialSummaryBudget powbFinancialPlannedBudget =
            reportSynthesisPMU.getReportSynthesisFinancialSummary().getBudgets().get(1);
          if (powbFinancialPlannedBudget != null) {
            w1w2 = powbFinancialPlannedBudget.getW1Actual() != null ? powbFinancialPlannedBudget.getW1Actual() : 0.0;
            // carry = powbFinancialPlannedBudget.getCarry() != null ? powbFinancialPlannedBudget.getCarry() : 0.0;
            w3Bilateral =
              powbFinancialPlannedBudget.getW3Actual() != null ? powbFinancialPlannedBudget.getW3Actual() : 0.0;
            // total = powbFinancialPlannedBudget.getTotalPlannedBudget() != null
            // ? powbFinancialPlannedBudget.getTotalPlannedBudget() : 0.0;
          }
          totalCarry += carry;
          totalw1w2 += w1w2;
          totalw3Bilateral += w3Bilateral;
          grandTotal += total;
          POIField[] sData = {new POIField(category, ParagraphAlignment.CENTER),
            new POIField(currencyFormat.format(round(w1w2, 2)), ParagraphAlignment.CENTER),
            new POIField(currencyFormat.format(round(w3Bilateral, 2)), ParagraphAlignment.CENTER),
            new POIField(currencyFormat.format(round(total, 2)), ParagraphAlignment.CENTER)};

          data = Arrays.asList(sData);
          datas.add(data);

        }
      }
    } catch (Exception e) {
    }
    Boolean bold = true;
    String blackColor = "000000";
    POIField[] sData = {new POIField("CRP Total", ParagraphAlignment.CENTER, bold, blackColor),

      new POIField(currencyFormat.format(round(totalw1w2, 2)), ParagraphAlignment.CENTER, bold, blackColor),
      new POIField(currencyFormat.format(round(totalw3Bilateral, 2)), ParagraphAlignment.CENTER, bold, blackColor),
      new POIField(currencyFormat.format(round(grandTotal, 2)), ParagraphAlignment.CENTER, bold, blackColor),};

    data = Arrays.asList(sData);
    datas.add(data);

    poiSummary.textTable(document, headers, datas, true, "tableJAnnualReport");
  }

  @Override
  public String execute() throws Exception {
    try {
      /* Create a portrait text Section */
      CTDocument1 doc = document.getDocument();
      CTBody body = doc.getBody();

      poiSummary.pageHeader(document, this.getText("summaries.annualReport.header"));
      // Get datetime
      ZonedDateTime timezone = ZonedDateTime.now();
      DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-d 'at' HH:mm ");
      String zone = timezone.getOffset() + "";
      if (zone.equals("Z")) {
        zone = "+0";
      }

      String currentDate = timezone.format(format) + "(GMT" + zone + ")";

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
      poiSummary.textParagraph(document.createParagraph(),
        this.getText("summaries.annualReport.LeadCenter") + ": " + " name ");
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
      // this.addEvidence();
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


      this.addFlagshipPlans();

      // section 2 - variance from planned program
      poiSummary.textHead1Title(document.createParagraph(), this.getText("summaries.annualReport.effectiveness"));
      poiSummary.textHead2Title(document.createParagraph(),
        this.getText("summaries.annualReport.effectiveness.program"));
      this.addVariancePlanned();

      this.addCrpStaffing();
      poiSummary.textHead2Title(document.createParagraph(),
        this.getText("summaries.annualReport.effectiveness.funding"));
      // this.addFinancialPlan();
      this.addFundingSummarize();
      poiSummary.textHead2Title(document.createParagraph(),
        this.getText("summaries.annualReport.effectiveness.partnership"));
      this.addExternalPartnerships();
      this.addCollaboration();
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

      /*
       * XWPFParagraph para = document.createParagraph();
       * CTSectPr sectionTable = body.getSectPr();
       * CTPageSz pageSizeTable = sectionTable.addNewPgSz();
       * CTP ctpTable = para.getCTP();
       * CTPPr brTable = ctpTable.addNewPPr();
       * brTable.setSectPr(sectionTable);
       * pageSizeTable.setOrient(STPageOrientation.PORTRAIT);
       */
      /*
       * pageSizeTable.setW(BigInteger.valueOf(842 * 20));
       * pageSizeTable.setH(BigInteger.valueOf(595 * 20));
       */

      // XWPFParagraph paragraph = document.createParagraph();


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


  private void getAssetsList() {
    assetsList = new ArrayList<>();

    List<DeliverableIntellectualAsset> assetsListTemp = deliverableIntellectualAssetManager.findAll();
    System.out.println(assetsListTemp.size() + " assets list largo " + this.getActualPhase().getId());
    for (int i = 0; i < assetsListTemp.size(); i++) {
      System.out.println(assetsListTemp.get(i).getPhase().getId());
      if (assetsListTemp.get(i).getPhase().getId() == this.getPhaseID()) {
        try {
          assetsList.add(assetsListTemp.get(i));
          System.out.println(" Entro + phase " + this.getPhaseID());
        } catch (Exception e) {
          throw e;
        }
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

    // this.getLoggedCrp().getAcronym()
    fileName.append("2017_CRP_AR");
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
    try {
      if (projectExpectedStudyManager.findAll() != null) {
        List<ProjectExpectedStudy> expectedStudies = new ArrayList<>(projectExpectedStudyManager.findAll().stream()
          .filter(ps -> ps.isActive() && ps.getPhase() != null && ps.getPhase() == phaseID
            && ps.getProject().getGlobalUnitProjects().stream()
              .filter(gup -> gup.isActive() && gup.isOrigin()
                && gup.getGlobalUnit().getId().equals(this.getLoggedCrp().getId()))
              .collect(Collectors.toList()).size() > 0)
          .collect(Collectors.toList()));

        for (ProjectExpectedStudy projectExpectedStudy : expectedStudies) {
          PowbEvidencePlannedStudyDTO dto = new PowbEvidencePlannedStudyDTO();
          projectExpectedStudy.getProject()
            .setProjectInfo(projectExpectedStudy.getProject().getProjecInfoPhase(this.getSelectedPhase()));
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
    } catch (Exception e) {

    }
  }

  public void getInformationTableJ() {

    // Check if relation is null -create it
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
            reportSynthesisPMU.getReportSynthesisFinancialSummary().getReportSynthesisFinancialSummaryBudgets().stream()
              .filter(t -> t.isActive()).collect(Collectors.toList())));
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

  public PowbExpectedCrpProgress getPowbExpectedCrpProgressProgram(Long crpMilestoneID, Long crpProgramID) {
    List<PowbExpectedCrpProgress> powbExpectedCrpProgresses =
      powbExpectedCrpProgressManager.findByProgram(crpProgramID);
    List<PowbExpectedCrpProgress> powbExpectedCrpProgressMilestone = powbExpectedCrpProgresses.stream()
      .filter(c -> c.getCrpMilestone().getId().longValue() == crpMilestoneID.longValue() && c.isActive())
      .collect(Collectors.toList());
    if (!powbExpectedCrpProgressMilestone.isEmpty()) {
      return powbExpectedCrpProgressMilestone.get(0);
    }
    return new PowbExpectedCrpProgress();
  }

  /*
   * public ReportSynthesisFinancialSummaryBudget getPowbFinancialPlanBudget(Long plannedBudgetRelationID,
   * Boolean isLiaison) {
   * if (isLiaison) {
   * LiaisonInstitution liaisonInstitution =
   * liaisonInstitutionManager.getLiaisonInstitutionById(plannedBudgetRelationID);
   * if (liaisonInstitution != null) {
   * List<PowbFinancialPlannedBudget> powbFinancialPlannedBudgetList = powbSynthesisPMU
   * .getPowbFinancialPlannedBudgetList().stream()
   * .filter(
   * p -> p.getLiaisonInstitution() != null && p.getLiaisonInstitution().getId().equals(plannedBudgetRelationID))
   * .collect(Collectors.toList());
   * if (powbFinancialPlannedBudgetList != null && !powbFinancialPlannedBudgetList.isEmpty()) {
   * ReportSynthesisFinancialSummaryBudget powbFinancialPlannedBudget =
   * new ReportSynthesisFinancialSummaryBudget();
   * powbFinancialPlannedBudget.setLiaisonInstitution(pmuInstitution);
   * // reportSynthesis.getReportSynthesisFinancialSummary().getBudgets().add(powbFinancialPlannedBudget);
   * if (liaisonInstitution.getCrpProgram() != null) {
   * powbFinancialPlannedBudget.setW1Actual(liaisonInstitution.getCrpProgram().getW1());
   * powbFinancialPlannedBudget.setW3Actual(liaisonInstitution.getCrpProgram().getW3());
   * }
   * return powbFinancialPlannedBudget;
   * } else {
   * ReportSynthesisFinancialSummaryBudget powbFinancialPlannedBudget =
   * new ReportSynthesisFinancialSummaryBudget();
   * powbFinancialPlannedBudget.setLiaisonInstitution(liaisonInstitution);
   * if (liaisonInstitution.getCrpProgram() != null) {
   * powbFinancialPlannedBudget.setW1Actual(new Double(liaisonInstitution.getCrpProgram().getW1()));
   * powbFinancialPlannedBudget.setW3Actual(liaisonInstitution.getCrpProgram().getW3());
   * }
   * return powbFinancialPlannedBudget;
   * }
   * } else {
   * return null;
   * }
   * } else {
   * PowbExpenditureAreas powbExpenditureArea =
   * powbExpenditureAreasManager.getPowbExpenditureAreasById(plannedBudgetRelationID);
   * if (powbExpenditureArea != null) {
   * List<PowbFinancialPlannedBudget> powbFinancialPlannedBudgetList =
   * powbSynthesisPMU.getPowbFinancialPlannedBudgetList().stream().filter(p -> p.getPowbExpenditureArea() != null
   * && p.getPowbExpenditureArea().getId().equals(plannedBudgetRelationID)).collect(Collectors.toList());
   * if (powbFinancialPlannedBudgetList != null && !powbFinancialPlannedBudgetList.isEmpty()) {
   * ReportSynthesisFinancialSummaryBudget powbFinancialPlannedBudget =
   * new ReportSynthesisFinancialSummaryBudget();
   * if (powbExpenditureArea.getExpenditureArea().equals("CRP Management & Support Cost")) {
   * this.loadPMU(powbExpenditureArea);
   * powbFinancialPlannedBudget.setW1Actual(powbExpenditureArea.getW1());
   * powbFinancialPlannedBudget.setW3Actual(powbExpenditureArea.getW3());
   * }
   * return powbFinancialPlannedBudget;
   * } else {
   * ReportSynthesisFinancialSummaryBudget powbFinancialPlannedBudget =
   * new ReportSynthesisFinancialSummaryBudget();
   * powbFinancialPlannedBudget.setExpenditureArea(powbExpenditureArea);
   * if (powbExpenditureArea.getExpenditureArea().equals("CRP Management & Support Cost")) {
   * this.loadPMU(powbExpenditureArea);
   * powbFinancialPlannedBudget.setW1Actual(powbExpenditureArea.getW1());
   * powbFinancialPlannedBudget.setW3Actual(powbExpenditureArea.getW3());
   * }
   * return powbFinancialPlannedBudget;
   * }
   * } else {
   * return null;
   * }
   * }
   * }
   */
  public PowbFinancialPlannedBudget getPowbFinancialPlanBudgetTest(Long plannedBudgetRelationID, Boolean isLiaison) {
    if (isLiaison) {
      LiaisonInstitution liaisonInstitution =
        liaisonInstitutionManager.getLiaisonInstitutionById(plannedBudgetRelationID);
      if (liaisonInstitution != null) {
        List<PowbFinancialPlannedBudget> powbFinancialPlannedBudgetList = powbSynthesisPMU
          .getPowbFinancialPlannedBudgetList().stream()
          .filter(
            p -> p.getLiaisonInstitution() != null && p.getLiaisonInstitution().getId().equals(plannedBudgetRelationID))
          .collect(Collectors.toList());
        if (powbFinancialPlannedBudgetList != null && !powbFinancialPlannedBudgetList.isEmpty()) {
          PowbFinancialPlannedBudget powbFinancialPlannedBudget = powbFinancialPlannedBudgetList.get(0);

          if (liaisonInstitution.getCrpProgram() != null) {
            powbFinancialPlannedBudget.setW1w2(liaisonInstitution.getCrpProgram().getW1());
            powbFinancialPlannedBudget.setW3Bilateral(liaisonInstitution.getCrpProgram().getW3());
            powbFinancialPlannedBudget.setCenterFunds(liaisonInstitution.getCrpProgram().getCenterFunds());
          }

          return powbFinancialPlannedBudget;
        } else {
          PowbFinancialPlannedBudget powbFinancialPlannedBudget = new PowbFinancialPlannedBudget();
          powbFinancialPlannedBudget.setLiaisonInstitution(liaisonInstitution);

          if (liaisonInstitution.getCrpProgram() != null) {
            powbFinancialPlannedBudget.setW1w2(new Double(liaisonInstitution.getCrpProgram().getW1()));
            powbFinancialPlannedBudget.setW3Bilateral(liaisonInstitution.getCrpProgram().getW3());
            powbFinancialPlannedBudget.setCenterFunds(liaisonInstitution.getCrpProgram().getCenterFunds());
          }

          return powbFinancialPlannedBudget;
        }
      } else {
        return null;
      }
    } else {
      PowbExpenditureAreas powbExpenditureArea =
        powbExpenditureAreasManager.getPowbExpenditureAreasById(plannedBudgetRelationID);

      if (powbExpenditureArea != null) {
        List<PowbFinancialPlannedBudget> powbFinancialPlannedBudgetList =
          powbSynthesisPMU.getPowbFinancialPlannedBudgetList().stream().filter(p -> p.getPowbExpenditureArea() != null
            && p.getPowbExpenditureArea().getId().equals(plannedBudgetRelationID)).collect(Collectors.toList());
        if (powbFinancialPlannedBudgetList != null && !powbFinancialPlannedBudgetList.isEmpty()) {
          PowbFinancialPlannedBudget powbFinancialPlannedBudget = powbFinancialPlannedBudgetList.get(0);
          if (powbExpenditureArea.getExpenditureArea().equals("CRP Management & Support Cost")) {
            this.loadPMU(powbExpenditureArea);
            powbFinancialPlannedBudget.setW1w2(powbExpenditureArea.getW1());
            powbFinancialPlannedBudget.setW3Bilateral(powbExpenditureArea.getW3());
            powbFinancialPlannedBudget.setCenterFunds(powbExpenditureArea.getCenterFunds());
          }
          return powbFinancialPlannedBudget;
        } else {

          PowbFinancialPlannedBudget powbFinancialPlannedBudget = new PowbFinancialPlannedBudget();
          powbFinancialPlannedBudget.setPowbExpenditureArea(powbExpenditureArea);
          if (powbExpenditureArea.getExpenditureArea().equals("CRP Management & Support Cost")) {
            this.loadPMU(powbExpenditureArea);
            powbFinancialPlannedBudget.setW1w2(powbExpenditureArea.getW1());
            powbFinancialPlannedBudget.setW3Bilateral(powbExpenditureArea.getW3());
            powbFinancialPlannedBudget.setCenterFunds(powbExpenditureArea.getCenterFunds());
          }
          return powbFinancialPlannedBudget;
        }
      } else {
        return null;
      }
    }
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
    Set<Project> myProjects = new HashSet();
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

  public void loadPMU(PowbExpenditureAreas liaisonInstitution) {

    Set<Project> myProjects = new HashSet();
    for (GlobalUnitProject projectFocus : this.getLoggedCrp().getGlobalUnitProjects().stream()
      .filter(c -> c.isActive() && c.isOrigin()).collect(Collectors.toList())) {
      Project project = projectFocus.getProject();
      if (project.isActive()) {
        project.setProjectInfo(project.getProjecInfoPhase(this.getSelectedPhase()));
        if (project.getProjectInfo() != null && project.getProjectInfo().getStatus() != null) {
          if (project.getProjectInfo().getStatus().intValue() == Integer
            .parseInt(ProjectStatusEnum.Ongoing.getStatusId())
            || project.getProjectInfo().getStatus().intValue() == Integer
              .parseInt(ProjectStatusEnum.Extended.getStatusId())) {
            if (project.getProjecInfoPhase(this.getSelectedPhase()).getAdministrative() != null
              && project.getProjecInfoPhase(this.getSelectedPhase()).getAdministrative().booleanValue()) {
              myProjects.add(project);
            }

          }
        }
      }
    }
    for (Project project : myProjects) {

      double w1 = project.getCoreBudget(this.getSelectedPhase().getYear(), this.getSelectedPhase());
      double w3 = project.getW3Budget(this.getSelectedPhase().getYear(), this.getSelectedPhase());
      double bilateral = project.getBilateralBudget(this.getSelectedPhase().getYear(), this.getSelectedPhase());
      double centerFunds = project.getCenterBudget(this.getSelectedPhase().getYear(), this.getSelectedPhase());

      double percentageW1 = 0;
      double percentageW3 = 0;
      double percentageB = 0;
      double percentageCenterFunds = 0;


      percentageW1 = 100;
      percentageW3 = 100;
      percentageB = 100;
      percentageCenterFunds = 100;


      w1 = w1 * (percentageW1) / 100;
      w3 = w3 * (percentageW3) / 100;
      bilateral = bilateral * (percentageB) / 100;
      centerFunds = centerFunds * (percentageCenterFunds) / 100;

      liaisonInstitution.setW1(liaisonInstitution.getW1() + w1);
      liaisonInstitution.setW3(liaisonInstitution.getW3() + w3 + bilateral);
      liaisonInstitution.setCenterFunds(liaisonInstitution.getCenterFunds() + centerFunds);

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
        /* Change requested by htobon: Show outcomes without milestones for table A1 */
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
    powbSynthesisList =
      this.getSelectedPhase().getPowbSynthesis().stream().filter(ps -> ps.isActive()).collect(Collectors.toList());

    List<PowbSynthesis> powbSynthesisPMUList = powbSynthesisList.stream()
      .filter(p -> p.isActive() && p.getLiaisonInstitution().equals(pmuInstitution)).collect(Collectors.toList());
    if (powbSynthesisPMUList != null && !powbSynthesisPMUList.isEmpty()) {
      powbSynthesisPMU = powbSynthesisPMUList.get(0);
    }

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

    // Get and create assetsList


    // Calculate time to generate report
    startTime = System.currentTimeMillis();
    LOG.info(
      "Start report download: " + this.getFileName() + ". User: " + this.getCurrentUser().getComposedCompleteName()
        + ". CRP: " + this.getLoggedCrp().getAcronym() + ". Cycle: " + this.getSelectedCycle());

    // Get liaison institution list
    List<LiaisonInstitution> liaisonInstitutionsList =
      new ArrayList<>(this.getLoggedCrp().getLiaisonInstitutions().stream()
        .filter(c -> c.getCrpProgram() != null && c.isActive()
          && c.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue())
        .collect(Collectors.toList()));
    liaisonInstitutionsList.sort(Comparator.comparing(LiaisonInstitution::getAcronym));


    // Table A-2 PMU Information

    sloTargets = new ArrayList<>(srfSloIndicatorTargetManager.findAll().stream()
      .filter(sr -> sr.isActive() && sr.getYear() == 2022).collect(Collectors.toList()));


    try {
      flagshipPlannedList = reportSynthesisCrpProgressManager.getPlannedList(liaisonInstitutionsList,
        this.getSelectedPhase().getId(), this.getLoggedCrp(), pmuInstitution);

      // Table A-1 Evidence on Progress
      fpSynthesisTable = reportSynthesisCrpProgressTargetManager.flagshipSynthesis(liaisonInstitutionsList,
        this.getSelectedPhase().getId());

      // Flagships Synthesis Progress
      flagshipCrpProgress = reportSynthesisCrpProgressManager.getFlagshipCrpProgress(liaisonInstitutionsList,
        this.getSelectedPhase().getId());
      // flagshipCrpProgress.get(0).getSloTargets().get(0).getSrfSloIndicatorTarget().getNarrative()

    } catch (Exception e) {
      // System.out.println(e);
    }

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
