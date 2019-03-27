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
import org.cgiar.ccafs.marlo.data.manager.CrpProgramOutcomeManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.PowbExpenditureAreasManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectFocusManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPolicyManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndSynthesisIndicatorManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisCrossCgiarCollaborationManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisCrossCuttingDimensionManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisCrpProgressManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisCrpProgressTargetManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisExternalPartnershipManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisFinancialSummaryBudgetManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisFlagshipProgressManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisFlagshipProgressMilestoneManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisFlagshipProgressOutcomeManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisMeliaManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisSrfProgressTargetManager;
import org.cgiar.ccafs.marlo.data.model.CrossCuttingDimensionTableDTO;
import org.cgiar.ccafs.marlo.data.model.CrpMilestone;
import org.cgiar.ccafs.marlo.data.model.CrpPpaPartner;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.CrpProgramOutcome;
import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.data.model.DeliverableDissemination;
import org.cgiar.ccafs.marlo.data.model.DeliverablePublicationMetadata;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.PowbEvidencePlannedStudyDTO;
import org.cgiar.ccafs.marlo.data.model.PowbExpenditureAreas;
import org.cgiar.ccafs.marlo.data.model.ProgramType;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectBudgetsFlagship;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudy;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyInfo;
import org.cgiar.ccafs.marlo.data.model.ProjectFocus;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovation;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationCountry;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationGeographicScope;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationRegion;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicy;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicyCrossCuttingMarker;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicyGeographicScope;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicySubIdo;
import org.cgiar.ccafs.marlo.data.model.ProjectStatusEnum;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesis;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisCrossCuttingDimension;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisCrossCuttingInnovationDTO;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFinancialSummary;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFinancialSummaryBudget;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgress;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgressInnovationDTO;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgressMilestone;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgressOutcome;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgressStudy;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgressStudyDTO;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFundingUseSummary;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisGovernance;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisKeyPartnershipExternal;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisKeyPartnershipExternalInstitution;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisKeyPartnershipExternalMainArea;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisRisk;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisSrfProgressTarget;
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
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFStyle;
import org.apache.poi.xwpf.usermodel.XWPFStyles;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBody;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDecimalNumber;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDocument1;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTOnOff;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageSz;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSimpleField;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTString;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTStyle;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STHdrFtr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STOnOff;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STPageOrientation;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STStyleType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AnnualReport2018POISummaryAction extends BaseSummariesAction implements Summary {

  private static final long serialVersionUID = 2828551630719082089L;
  // private static final String ProgramType = null;
  private static Logger LOG = LoggerFactory.getLogger(AnnualReport2018POISummaryAction.class);

  private static void addCustomHeadingStyle(XWPFDocument docxDocument, String strStyleId, int headingLevel) {

    CTStyle ctStyle = CTStyle.Factory.newInstance();

    ctStyle.setStyleId(strStyleId);

    CTString styleName = CTString.Factory.newInstance();
    styleName.setVal(strStyleId);
    ctStyle.setName(styleName);

    CTDecimalNumber indentNumber = CTDecimalNumber.Factory.newInstance();
    indentNumber.setVal(BigInteger.valueOf(headingLevel));

    // lower number > style is more prominent in the formats bar
    ctStyle.setUiPriority(indentNumber);

    CTOnOff onoffnull = CTOnOff.Factory.newInstance();
    ctStyle.setUnhideWhenUsed(onoffnull);

    // style shows up in the formats bar
    ctStyle.setQFormat(onoffnull);

    // style defines a heading of the given level
    CTPPr ppr = CTPPr.Factory.newInstance();
    ppr.setOutlineLvl(indentNumber);
    ctStyle.setPPr(ppr);

    XWPFStyle style = new XWPFStyle(ctStyle);

    // is a null op if already defined
    XWPFStyles styles = docxDocument.createStyles();

    style.setType(STStyleType.PARAGRAPH);
    styles.addStyle(style);
  }

  public static double round(double value, int places) {
    if (places < 0) {
      throw new IllegalArgumentException();
    }

    BigDecimal bd = new BigDecimal(value);
    bd = bd.setScale(places, RoundingMode.HALF_UP);
    return bd.doubleValue();
  }

  // Managers
  private PowbExpenditureAreasManager powbExpenditureAreasManager;
  private ReportSynthesisManager reportSynthesisManager;
  private ReportSynthesisSrfProgressTargetManager reportSynthesisSrfProgressTargetManager;
  private RepIndSynthesisIndicatorManager repIndSynthesisIndicatorManager;
  private ReportSynthesisExternalPartnershipManager reportSynthesisExternalPartnershipManager;
  private ReportSynthesisMeliaManager reportSynthesisMeliaManager;
  private ReportSynthesisCrossCgiarCollaborationManager reportSynthesisCrossCgiarCollaborationManager;
  private ReportSynthesisFlagshipProgressManager reportSynthesisFlagshipProgressManager;
  private ReportSynthesisFinancialSummaryBudgetManager reportSynthesisFinancialSummaryBudgetManager;
  private ReportSynthesisFlagshipProgressMilestoneManager reportSynthesisFlagshipProgressMilestoneManager;
  private ReportSynthesisCrossCuttingDimensionManager reportSynthesisCrossCuttingDimensionManager;
  private ReportSynthesisCrpProgressManager reportSynthesisCrpProgressManager;
  private CrpProgramManager crpProgramManager;
  private ProjectPolicyManager projectPolicyManager;
  private ProjectExpectedStudyManager projectExpectedStudyManager;
  private ProjectFocusManager projectFocusManager;
  private ProjectInnovationManager projectInnovationManager;
  private CrpProgramOutcomeManager crpProgramOutcomeManager;
  private ReportSynthesisFlagshipProgressOutcomeManager reportSynthesisFlagshipProgressOutcomeManager;
  private DeliverableManager deliverableManager;
  private List<ReportSynthesisKeyPartnershipExternal> flagshipExternalPartnerships;


  // Parameters
  private POISummary poiSummary;
  private LiaisonInstitution liaisonInstitution;
  private LiaisonInstitution pmuInstitution;
  private ReportSynthesis reportSynthesisPMU;
  private long startTime;
  private XWPFDocument document;
  private CrossCuttingDimensionTableDTO tableC;
  private NumberFormat currencyFormat;
  private DecimalFormat percentageFormat;
  private List<CrpProgram> flagships;
  private List<PowbEvidencePlannedStudyDTO> flagshipPlannedList;
  private List<ReportSynthesisFlagshipProgressStudyDTO> flagshipPlannedListReport;
  private List<LiaisonInstitution> flagshipLiaisonInstitutions;
  private List<ReportSynthesisCrossCuttingInnovationDTO> flagshipPlannedInnovations;
  private List<ProjectPolicy> projectPolicies;
  private List<ProjectExpectedStudy> projectExpectedStudies;
  private List<ProjectInnovation> projectInnovations;


  Double totalw1w2 = 0.0, totalw1w2Planned = 0.0, totalCenter = 0.0, grandTotal = 0.0, totalw1w2Actual = 0.0,
    totalW3Actual = 0.0, totalW3Bilateral = 0.0, totalW3Planned = 0.0, grandTotalPlanned = 0.0, grandTotalActual = 0.0;

  private Integer totalOpenAccess = 0;
  private Integer totalLimited = 0;
  private Integer totalIsis = 0;
  private Integer totalNoIsis = 0;
  private Integer total = 0;
  // Streams
  private InputStream inputStream;

  // DOC bytes
  private byte[] bytesDOC;

  public AnnualReport2018POISummaryAction(APConfig config, GlobalUnitManager crpManager, PhaseManager phaseManager,
    PowbExpenditureAreasManager powbExpenditureAreasManager, ReportSynthesisManager reportSynthesisManager,
    ReportSynthesisCrpProgressTargetManager reportSynthesisCrpProgressTargetManager,
    RepIndSynthesisIndicatorManager repIndSynthesisIndicatorManager, ProjectManager projectManager,
    ReportSynthesisExternalPartnershipManager reportSynthesisExternalPartnershipManager,
    ProjectPolicyManager projectPolicyManager, ReportSynthesisMeliaManager reportSynthesisMeliaManager,
    ReportSynthesisCrossCgiarCollaborationManager reportSynthesisCrossCgiarCollaborationManager,
    ReportSynthesisFlagshipProgressManager reportSynthesisFlagshipProgressManager,
    ProjectInnovationManager projectInnovationManager,
    ReportSynthesisFinancialSummaryBudgetManager reportSynthesisFinancialSummaryBudgetManager,
    ReportSynthesisFlagshipProgressMilestoneManager reportSynthesisFlagshipProgressMilestoneManager,
    ReportSynthesisFlagshipProgressOutcomeManager reportSynthesisFlagshipProgressOutcomeManager,
    ReportSynthesisCrossCuttingDimensionManager reportSynthesisCrossCuttingDimensionManager,
    ReportSynthesisCrpProgressManager reportSynthesisCrpProgressManager,
    ReportSynthesisSrfProgressTargetManager reportSynthesisSrfProgressTargetManager,
    CrpProgramManager crpProgramManager, ProjectExpectedStudyManager projectExpectedStudyManager,
    ProjectFocusManager projectFocusManager, CrpProgramOutcomeManager crpProgramOutcomeManager,
    DeliverableManager deliverableManager) {
    super(config, crpManager, phaseManager, projectManager);
    document = new XWPFDocument();
    poiSummary = new POISummary();
    currencyFormat = NumberFormat.getCurrencyInstance();
    percentageFormat = new DecimalFormat("##.##%");
    this.powbExpenditureAreasManager = powbExpenditureAreasManager;
    this.reportSynthesisManager = reportSynthesisManager;
    this.reportSynthesisSrfProgressTargetManager = reportSynthesisSrfProgressTargetManager;
    this.repIndSynthesisIndicatorManager = repIndSynthesisIndicatorManager;
    this.reportSynthesisExternalPartnershipManager = reportSynthesisExternalPartnershipManager;
    this.reportSynthesisMeliaManager = reportSynthesisMeliaManager;
    this.reportSynthesisCrossCgiarCollaborationManager = reportSynthesisCrossCgiarCollaborationManager;
    this.reportSynthesisFlagshipProgressManager = reportSynthesisFlagshipProgressManager;
    this.reportSynthesisFinancialSummaryBudgetManager = reportSynthesisFinancialSummaryBudgetManager;
    this.reportSynthesisFlagshipProgressMilestoneManager = reportSynthesisFlagshipProgressMilestoneManager;
    this.reportSynthesisCrossCuttingDimensionManager = reportSynthesisCrossCuttingDimensionManager;
    this.projectPolicyManager = projectPolicyManager;
    this.projectInnovationManager = projectInnovationManager;
    this.reportSynthesisCrpProgressManager = reportSynthesisCrpProgressManager;
    this.crpProgramManager = crpProgramManager;
    this.projectExpectedStudyManager = projectExpectedStudyManager;
    this.projectFocusManager = projectFocusManager;
    this.crpProgramOutcomeManager = crpProgramOutcomeManager;
    this.reportSynthesisFlagshipProgressOutcomeManager = reportSynthesisFlagshipProgressOutcomeManager;
    this.deliverableManager = deliverableManager;
  }

  private void addAdjustmentDescription() {
    List<ReportSynthesisFlagshipProgress> reportSynthesisFlagshipProgressManagerList =
      reportSynthesisFlagshipProgressManager.findAll().stream().filter(fp -> fp.isActive()
        && fp.getReportSynthesis().isActive() && fp.getReportSynthesis().getPhase().equals(this.getSelectedPhase()))
        .collect(Collectors.toList());

    if (reportSynthesisFlagshipProgressManagerList != null && !reportSynthesisFlagshipProgressManagerList.isEmpty()) {

      for (ReportSynthesisFlagshipProgress reportSynthesisFlagshipProgress : reportSynthesisFlagshipProgressManagerList) {
        String acronym = "", summary = "";
        if (reportSynthesisFlagshipProgress.getReportSynthesis().getLiaisonInstitution().getAcronym() != null) {
          acronym = reportSynthesisFlagshipProgress.getReportSynthesis().getLiaisonInstitution().getAcronym();
        }
        if (reportSynthesisFlagshipProgress.getSummary() != null
          && !reportSynthesisFlagshipProgress.getSummary().isEmpty()) {
          summary = acronym + ": " + reportSynthesisFlagshipProgress.getSummary() + "\n";
          poiSummary.textParagraph(document.createParagraph(), summary);
        }
      }

    }
  }

  private void addAlmetricCrp() {
    if (reportSynthesisPMU != null && reportSynthesisPMU.getReportSynthesisFlagshipProgress() != null
      && reportSynthesisPMU.getReportSynthesisFlagshipProgress().getAltmetricScore() != null) {
      String synthesisCrpAltmetric = reportSynthesisPMU.getReportSynthesisFlagshipProgress().getAltmetricScore() != null
        ? reportSynthesisPMU.getReportSynthesisFlagshipProgress().getAltmetricScore() : "";
      poiSummary.textParagraph(document.createParagraph(), synthesisCrpAltmetric);
    }
  }

  private void addCrossCuttingCapacityDevelopment() {
    String crossCuttingCapacityDevelopment = "";
    if (reportSynthesisPMU != null) {
      if (reportSynthesisPMU.getReportSynthesisCrossCuttingDimension() != null) {
        ReportSynthesisCrossCuttingDimension crossCutting =
          reportSynthesisPMU.getReportSynthesisCrossCuttingDimension();
        if (crossCutting != null) {
          crossCuttingCapacityDevelopment = crossCutting.getCapDevKeyAchievements();
        }
      }
    }

    if (crossCuttingCapacityDevelopment != null && !crossCuttingCapacityDevelopment.isEmpty()) {
      poiSummary.textParagraph(document.createParagraph(), crossCuttingCapacityDevelopment);
    }
  }

  private void addCrossCuttingClimateChange() {
    String crossCuttingClimateChange = "";
    if (reportSynthesisPMU != null) {
      if (reportSynthesisPMU.getReportSynthesisCrossCuttingDimension() != null) {
        ReportSynthesisCrossCuttingDimension crossCutting =
          reportSynthesisPMU.getReportSynthesisCrossCuttingDimension();
        if (crossCutting != null) {
          crossCuttingClimateChange = crossCutting.getClimateChangeKeyAchievements();
        }
      }
    }

    if (crossCuttingClimateChange != null && !crossCuttingClimateChange.isEmpty()) {
      poiSummary.textParagraph(document.createParagraph(), crossCuttingClimateChange);
    }
  }

  private void addCrossCuttingGender() {

    String crossCuttingGenderResearchFindings = "";
    String crossCuttingGenderLearned = "";
    String crossCuttingGenderProblemsArimes = "";
    if (reportSynthesisPMU != null) {
      // Cross Cutting Gender Info
      if (reportSynthesisPMU.getReportSynthesisCrossCuttingDimension() != null) {
        ReportSynthesisCrossCuttingDimension crossCutting =
          reportSynthesisPMU.getReportSynthesisCrossCuttingDimension();
        if (crossCutting != null) {
          crossCuttingGenderResearchFindings = crossCutting.getGenderResearchFindings();
          crossCuttingGenderLearned = crossCutting.getGenderLearned();
          crossCuttingGenderProblemsArimes = crossCutting.getGenderProblemsArisen();
        }
      }
    }
    if (crossCuttingGenderResearchFindings != null && !crossCuttingGenderResearchFindings.isEmpty()) {
      poiSummary.convertHTMLTags(document, crossCuttingGenderResearchFindings);
    }

    if (crossCuttingGenderLearned != null && !crossCuttingGenderLearned.isEmpty()) {
      poiSummary.convertHTMLTags(document, crossCuttingGenderLearned);

    }

    if (crossCuttingGenderProblemsArimes != null && !crossCuttingGenderProblemsArimes.isEmpty()) {
      poiSummary.convertHTMLTags(document, crossCuttingGenderProblemsArimes);
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
      poiSummary.convertHTMLTags(document, crossCuttingIntellectualAssets);

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
      // poiSummary.textParagraph(document.createParagraph(), crossCuttingOpenData);
      poiSummary.convertHTMLTags(document, crossCuttingOpenData);
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
      // poiSummary.textParagraph(document.createParagraph(), crossCuttingOtherAspects);
      poiSummary.convertHTMLTags(document, crossCuttingOtherAspects);

    }

  }

  private void addCrossCuttingYouth() {

    String crossCuttingYouthContribution = "";
    String crossCuttingYouthResearchFindings = "";
    String crossCuttingYouthLearned = "";
    String crossCuttingYouthProblemsArisen = "";
    if (reportSynthesisPMU != null) {

      // Cross Cutting Gender Info
      if (reportSynthesisPMU.getReportSynthesisCrossCuttingDimension() != null) {
        ReportSynthesisCrossCuttingDimension crossCutting =
          reportSynthesisPMU.getReportSynthesisCrossCuttingDimension();
        if (crossCutting != null) {
          crossCuttingYouthContribution = crossCutting.getYouthContribution();
          crossCuttingYouthResearchFindings = crossCutting.getYouthResearchFindings();
          crossCuttingYouthLearned = crossCutting.getYouthLearned();
          crossCuttingYouthProblemsArisen = crossCutting.getYouthProblemsArisen();
        }
      }
    }
    if (crossCuttingYouthContribution != null && !crossCuttingYouthContribution.isEmpty()) {
      poiSummary.convertHTMLTags(document, crossCuttingYouthContribution);
    }
    if (crossCuttingYouthResearchFindings != null && !crossCuttingYouthResearchFindings.isEmpty()) {
      poiSummary.convertHTMLTags(document, crossCuttingYouthResearchFindings);
    }
    if (crossCuttingYouthLearned != null && !crossCuttingYouthLearned.isEmpty()) {
      poiSummary.convertHTMLTags(document, crossCuttingYouthLearned);
    }
    if (crossCuttingYouthProblemsArisen != null && !crossCuttingYouthProblemsArisen.isEmpty()) {
      poiSummary.convertHTMLTags(document, crossCuttingYouthProblemsArisen);
    }
  }

  private void addCrossPartnerships() {
    if (reportSynthesisPMU != null) {

      if (reportSynthesisPMU.getReportSynthesisKeyPartnership() != null
        && reportSynthesisPMU.getReportSynthesisKeyPartnership().getSummaryCgiar() != null) {
        poiSummary.convertHTMLTags(document, reportSynthesisPMU.getReportSynthesisKeyPartnership().getSummaryCgiar());
      }
    }
  }

  private void addCrpProgressOutcomes() {
    if (reportSynthesisPMU != null && reportSynthesisPMU.getReportSynthesisCrpProgress() != null
      && reportSynthesisPMU.getReportSynthesisCrpProgress().getSummaries() != null) {
      String synthesisCrpSummaries = reportSynthesisPMU.getReportSynthesisCrpProgress().getSummaries() != null
        ? reportSynthesisPMU.getReportSynthesisCrpProgress().getSummaries() : "";
      // poiSummary.textParagraph(document.createParagraph(), synthesisCrpSummaries);
      poiSummary.convertHTMLTags(document, synthesisCrpSummaries);
    }
  }

  private void addExpectedCrp() {
    if (reportSynthesisPMU != null && reportSynthesisPMU.getReportSynthesisSrfProgress() != null
      && reportSynthesisPMU.getReportSynthesisSrfProgress().getSummary() != null) {
      String synthesisCrpDescription = reportSynthesisPMU.getReportSynthesisSrfProgress().getSummary() != null
        ? reportSynthesisPMU.getReportSynthesisSrfProgress().getSummary() : "";
      // poiSummary.textParagraph(document.createParagraph(), synthesisCrpDescription);
      poiSummary.convertHTMLTags(document, synthesisCrpDescription);
    }
  }

  private void addExternalPartnerships() {

    String keyExternal = "";

    if (reportSynthesisPMU != null) {

      if (reportSynthesisPMU.getReportSynthesisKeyPartnership() != null
        && reportSynthesisPMU.getReportSynthesisKeyPartnership().getSummary() != null) {
        keyExternal = reportSynthesisPMU.getReportSynthesisKeyPartnership().getSummary();
      }
    }

    if (keyExternal != null && !keyExternal.isEmpty()) {
      // poiSummary.textParagraph(document.createParagraph(), keyExternal);
      poiSummary.convertHTMLTags(document, keyExternal);

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
      // poiSummary.textParagraph(document.createParagraph(), financialSummaryNarrative);
      poiSummary.convertHTMLTags(document, financialSummaryNarrative);

    }
  }


  private void addFundingSummarize() {
    String brieflySummarize = "";
    if (reportSynthesisPMU != null) {
      if (reportSynthesisPMU.getReportSynthesisFundingUseSummary() != null) {
        ReportSynthesisFundingUseSummary funding = reportSynthesisPMU.getReportSynthesisFundingUseSummary();
        if (funding != null) {
          brieflySummarize = funding.getInterestingPoints();
        }
      }
    }

    if (brieflySummarize != null && !brieflySummarize.isEmpty()) {
      // poiSummary.textParagraph(document.createParagraph(), brieflySummarize);
      poiSummary.convertHTMLTags(document, brieflySummarize);

    }
  }

  private void addImprovingEfficiency() {
    if (reportSynthesisPMU != null) {
      if (reportSynthesisPMU.getReportSynthesisEfficiency() != null
        && reportSynthesisPMU.getReportSynthesisEfficiency().getDescription() != null) {
        // poiSummary.textParagraph(document.createParagraph(),
        // reportSynthesisPMU.getReportSynthesisEfficiency().getDescription());
        poiSummary.convertHTMLTags(document, reportSynthesisPMU.getReportSynthesisEfficiency().getDescription());

      }
    }
  }

  public void addIntellectualAssets() {
    if (reportSynthesisPMU != null) {
      if (reportSynthesisPMU.getReportSynthesisIntellectualAsset() != null) {


        if (reportSynthesisPMU.getReportSynthesisIntellectualAsset().getManaged() != null) {
          poiSummary.convertHTMLTags(document, this.getText("summaries.annualReport2018.effectiveness.intellectual1"));
          poiSummary.convertHTMLTags(document, reportSynthesisPMU.getReportSynthesisIntellectualAsset().getManaged());
        }
        if (reportSynthesisPMU.getReportSynthesisIntellectualAsset().getPatents() != null) {
          poiSummary.convertHTMLTags(document, this.getText("summaries.annualReport2018.effectiveness.intellectual2"));
          poiSummary.convertHTMLTags(document, reportSynthesisPMU.getReportSynthesisIntellectualAsset().getPatents());
        }
        if (reportSynthesisPMU.getReportSynthesisIntellectualAsset().getCriticalIssues() != null) {
          poiSummary.convertHTMLTags(document, this.getText("summaries.annualReport2018.effectiveness.intellectual3"));
          poiSummary.convertHTMLTags(document,
            reportSynthesisPMU.getReportSynthesisIntellectualAsset().getCriticalIssues());
        }
      }
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
      // poiSummary.textParagraph(document.createParagraph(), managementGovernanceDescription);
      poiSummary.convertHTMLTags(document, managementGovernanceDescription);

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
      // poiSummary.textParagraph(document.createParagraph(), managementRiskBrief);
      poiSummary.convertHTMLTags(document, managementRiskBrief);
    }
  }

  private void addNarrativeSection() {
    if (reportSynthesisPMU != null && reportSynthesisPMU.getReportSynthesisNarrative() != null
      && reportSynthesisPMU.getReportSynthesisNarrative().getNarrative() != null) {
      String narrative = reportSynthesisPMU.getReportSynthesisNarrative().getNarrative();
      // poiSummary.textParagraph(document.createParagraph(), synthesisCrpDescription);
      poiSummary.convertHTMLTags(document, narrative);
    }
  }

  private void addOverallProgressCrp() {
    if (reportSynthesisPMU != null && reportSynthesisPMU.getReportSynthesisFlagshipProgress() != null
      && reportSynthesisPMU.getReportSynthesisFlagshipProgress().getOverallProgress() != null) {
      String synthesisCrpOveral = reportSynthesisPMU.getReportSynthesisFlagshipProgress().getOverallProgress() != null
        ? reportSynthesisPMU.getReportSynthesisFlagshipProgress().getOverallProgress() : "";
      // poiSummary.textParagraph(document.createParagraph(), synthesisCrpOveral);
      poiSummary.convertHTMLTags(document, synthesisCrpOveral);
    }
  }

  private void addParticipatingCenters() {
    String participantingCenters = "";
    List<CrpPpaPartner> crpPpaPartnerList = this.getLoggedCrp().getCrpPpaPartners().stream()
      .filter(c -> c.isActive() && c.getPhase() != null && c.getPhase().equals(this.getSelectedPhase()))
      .sorted((c1, c2) -> c1.getInstitution().getAcronymName().compareTo(c2.getInstitution().getAcronymName()))
      .collect(Collectors.toList());
    if (crpPpaPartnerList != null && !crpPpaPartnerList.isEmpty()) {
      for (CrpPpaPartner crpPpaPartner : crpPpaPartnerList) {
        if (participantingCenters.isEmpty()) {
          participantingCenters = crpPpaPartner.getInstitution().getAcronymName();
        } else {
          participantingCenters += ", " + crpPpaPartner.getInstitution().getAcronymName();
        }
      }
    }
    poiSummary.textParagraph(document.createParagraph(),
      this.getText("summaries.annualReport.participantingCenters") + ": " + participantingCenters + ":");
    poiSummary.convertHTMLTags(document, participantingCenters);

  }

  private void addProgressFlagshipCrp() {
    if (reportSynthesisPMU != null && reportSynthesisPMU.getReportSynthesisFlagshipProgress() != null
      && reportSynthesisPMU.getReportSynthesisFlagshipProgress().getProgressByFlagships() != null) {
      String synthesisCrpProgress =
        reportSynthesisPMU.getReportSynthesisFlagshipProgress().getProgressByFlagships() != null
          ? reportSynthesisPMU.getReportSynthesisFlagshipProgress().getProgressByFlagships() : "";
      // poiSummary.textParagraph(document.createParagraph(), synthesisCrpProgress);
      poiSummary.convertHTMLTags(document, synthesisCrpProgress);
    }
  }

  public void addReportSynthesisMelia() {
    if (reportSynthesisPMU != null) {
      if (reportSynthesisPMU.getReportSynthesisMelia() != null
        && reportSynthesisPMU.getReportSynthesisMelia().getSummary() != null) {
        // poiSummary.textParagraph(document.createParagraph(),
        // reportSynthesisPMU.getReportSynthesisMelia().getSummary());
        poiSummary.convertHTMLTags(document, reportSynthesisPMU.getReportSynthesisMelia().getSummary());

      }
    }
  }

  private void addVariancePlanned() {

    String expanded = "";
    String cutBack = "";
    String direction = "";

    if (reportSynthesisPMU != null) {

      if (reportSynthesisPMU != null && reportSynthesisPMU.getReportSynthesisFlagshipProgress() != null
        && reportSynthesisPMU.getReportSynthesisFlagshipProgress().getExpandedResearchAreas() != null) {
        expanded = reportSynthesisPMU.getReportSynthesisFlagshipProgress().getExpandedResearchAreas() != null
          ? reportSynthesisPMU.getReportSynthesisFlagshipProgress().getExpandedResearchAreas() : "";
      }

      if (reportSynthesisPMU != null && reportSynthesisPMU.getReportSynthesisFlagshipProgress() != null
        && reportSynthesisPMU.getReportSynthesisFlagshipProgress().getDroppedResearchLines() != null) {
        cutBack = reportSynthesisPMU.getReportSynthesisFlagshipProgress().getDroppedResearchLines() != null
          ? reportSynthesisPMU.getReportSynthesisFlagshipProgress().getDroppedResearchLines() : "";
      }

      if (reportSynthesisPMU != null && reportSynthesisPMU.getReportSynthesisFlagshipProgress() != null
        && reportSynthesisPMU.getReportSynthesisFlagshipProgress().getChangedDirection() != null) {
        direction = reportSynthesisPMU.getReportSynthesisFlagshipProgress().getChangedDirection() != null
          ? reportSynthesisPMU.getReportSynthesisFlagshipProgress().getChangedDirection() : "";
      }
    }

    if (expanded != null && !expanded.isEmpty()) {
      poiSummary.textParagraph(document.createParagraph(),
        this.getText("summaries.annualReport2018.keyResults.variance1"));
      poiSummary.convertHTMLTags(document, expanded);
    }

    if (cutBack != null && !cutBack.isEmpty()) {
      poiSummary.textParagraph(document.createParagraph(),
        this.getText("summaries.annualReport2018.keyResults.variance2"));
      poiSummary.convertHTMLTags(document, cutBack);
    }

    if (direction != null && !direction.isEmpty()) {
      poiSummary.textParagraph(document.createParagraph(),
        this.getText("summaries.annualReport2018.keyResults.variance3"));
      poiSummary.convertHTMLTags(document, direction);
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

  public void createTable1() {
    List<List<POIField>> headers = new ArrayList<>();
    String blackColor = "000000";

    POIField[] sHeader = {
      new POIField(this.getText("annualReport.crpProgress.selectSLOTarget"), ParagraphAlignment.CENTER, true,
        blackColor),
      new POIField(this.getText("summaries.annualReport2018.table1a"), ParagraphAlignment.LEFT, true, blackColor),
      new POIField(this.getText("summaries.annualReport2018.table1b"), ParagraphAlignment.LEFT, true, blackColor)};

    List<POIField> header = Arrays.asList(sHeader);
    headers.add(header);


    /*
     * Get all crp Progress Targets and compare the slo indicador Target id with the actual slotarget id
     */

    List<List<POIField>> datas = new ArrayList<>();
    List<POIField> data;

    data = new ArrayList<>();
    // Table A-1 Evidence on Progress

    List<ReportSynthesisSrfProgressTarget> listSrfProgressTargets = new ArrayList<>();
    try {
      listSrfProgressTargets = reportSynthesisSrfProgressTargetManager.findAll().stream().filter(t -> t.isActive())
        .collect(Collectors.toList());
    } catch (Exception e) {
      System.out.println(e);
    }
    if (listSrfProgressTargets != null && !listSrfProgressTargets.isEmpty()
      && reportSynthesisPMU.getReportSynthesisSrfProgress() != null) {
      listSrfProgressTargets = listSrfProgressTargets.stream().filter(l -> l.getReportSynthesisSrfProgress().getId()
        .equals(reportSynthesisPMU.getReportSynthesisSrfProgress().getId())).collect(Collectors.toList());
      for (ReportSynthesisSrfProgressTarget reportSynthesisSrfProgressTarget : listSrfProgressTargets.stream()
        .filter(c -> c.getSrfSloIndicatorTarget().getTargetsIndicator() != null)
        .sorted((c1, c2) -> c1.getSrfSloIndicatorTarget().getTargetsIndicator()
          .compareTo(c2.getSrfSloIndicatorTarget().getTargetsIndicator()))
        .collect(Collectors.toList())) {
        String sloTarget = "", briefSummaries = "", additionalContribution = "";
        if (reportSynthesisSrfProgressTarget.getSrfSloIndicatorTarget() != null) {
          if (reportSynthesisSrfProgressTarget.getSrfSloIndicatorTarget().getTargetsIndicator() != null
            && !reportSynthesisSrfProgressTarget.getSrfSloIndicatorTarget().getTargetsIndicator().isEmpty()) {
            sloTarget = reportSynthesisSrfProgressTarget.getSrfSloIndicatorTarget().getTargetsIndicator();
          }
          if (reportSynthesisSrfProgressTarget.getSrfSloIndicatorTarget().getNarrative() != null
            && !reportSynthesisSrfProgressTarget.getSrfSloIndicatorTarget().getNarrative().isEmpty()) {
            sloTarget += " " + reportSynthesisSrfProgressTarget.getSrfSloIndicatorTarget().getNarrative();
          }
        }

        briefSummaries = reportSynthesisSrfProgressTarget.getBirefSummary() != null
          ? reportSynthesisSrfProgressTarget.getBirefSummary() : "";
        additionalContribution = reportSynthesisSrfProgressTarget.getAdditionalContribution() != null
          ? reportSynthesisSrfProgressTarget.getAdditionalContribution() : "";

        Boolean bold = false;
        String blueColor = "000099";
        POIField[] sData = {new POIField(sloTarget, ParagraphAlignment.LEFT, bold, blackColor),
          new POIField(briefSummaries, ParagraphAlignment.LEFT, bold, blueColor),
          new POIField(additionalContribution, ParagraphAlignment.LEFT, bold, blueColor)};
        data = Arrays.asList(sData);
        datas.add(data);
      }
    }

    poiSummary.textTable(document, headers, datas, true, "tableA1AnnualReport2018");
  }

  private void createTable10() {
    List<List<POIField>> headers = new ArrayList<>();
    List<List<POIField>> datas = new ArrayList<>();
    List<POIField> data;
    POIField[] sHeader = {
      new POIField(
        this.getText("summaries.annualReport2018.table10Title1", new String[] {String.valueOf(this.getSelectedYear())}),
        ParagraphAlignment.LEFT),
      new POIField(this.getText("summaries.annualReport2018.table10Title2"), ParagraphAlignment.LEFT),
      new POIField(this.getText("summaries.annualReport2018.table10Title3"), ParagraphAlignment.LEFT, true, "839B49"),
      new POIField(this.getText("summaries.annualReport2018.table10Title4"), ParagraphAlignment.LEFT)};
    List<POIField> header = Arrays.asList(sHeader);
    headers.add(header);

    if (flagshipPlannedList != null && !flagshipPlannedList.isEmpty()) {
      // for (int i = 0; i < flagshipPlannedList.size(); i++) {
      String studies = "", status = "", comments = "";

      POIField[] sData =
        {new POIField(studies, ParagraphAlignment.LEFT), new POIField(status, ParagraphAlignment.CENTER),
          new POIField(comments, ParagraphAlignment.LEFT), new POIField(comments, ParagraphAlignment.LEFT)};
      data = Arrays.asList(sData);
      datas.add(data);
      // }
    }

    poiSummary.textTable(document, headers, datas, false, "table3AnnualReport2018");
  }

  private void createTable11() {
    List<List<POIField>> headers = new ArrayList<>();
    List<List<POIField>> datas = new ArrayList<>();
    List<POIField> data;
    POIField[] sHeader = {
      new POIField(
        this.getText("summaries.annualReport2018.table11Title1", new String[] {String.valueOf(this.getSelectedYear())}),
        ParagraphAlignment.LEFT),
      new POIField(this.getText("summaries.annualReport2018.table11Title2"), ParagraphAlignment.CENTER, true, "000000"),
      new POIField(this.getText("summaries.annualReport2018.table11Title3"), ParagraphAlignment.CENTER, true, "76923C"),
      new POIField(this.getText("summaries.annualReport2018.table11Title4"), ParagraphAlignment.CENTER, true, "000000"),
      new POIField(this.getText("summaries.annualReport2018.table11Title5"), ParagraphAlignment.CENTER, true, "000000"),
      new POIField(this.getText("summaries.annualReport2018.table11Title6"), ParagraphAlignment.CENTER, true, "76923C"),
      new POIField(this.getText("summaries.annualReport2018.table11Title7"), ParagraphAlignment.CENTER, true, "76923C"),
      new POIField(this.getText("summaries.annualReport2018.table11Title8"), ParagraphAlignment.CENTER, true,
        "000000")};
    List<POIField> header = Arrays.asList(sHeader);
    headers.add(header);

    if (flagshipPlannedList != null && !flagshipPlannedList.isEmpty()) {
      // for (int i = 0; i < flagshipPlannedList.size(); i++) {
      String name = "", recomendation = "", text = "", status = "", actions = "", whom = "", when = "", comments = "";

      POIField[] sData = {new POIField(name, ParagraphAlignment.LEFT, false, "000000"),
        new POIField(recomendation, ParagraphAlignment.LEFT, false, "000000"),
        new POIField(text, ParagraphAlignment.LEFT, false, "000000"),
        new POIField(status, ParagraphAlignment.LEFT, false, "000000"),
        new POIField(actions, ParagraphAlignment.LEFT, false, "000000"),
        new POIField(whom, ParagraphAlignment.LEFT, false, "000000"),
        new POIField(when, ParagraphAlignment.LEFT, false, "000000"),
        new POIField(comments, ParagraphAlignment.LEFT, false, "000000")};
      data = Arrays.asList(sData);
      datas.add(data);
      // }
    }

    poiSummary.textTable(document, headers, datas, false, "table3AnnualReport2018");
  }

  private void createTable12() {
    List<List<POIField>> headers = new ArrayList<>();
    List<List<POIField>> datas = new ArrayList<>();
    List<POIField> data;
    POIField[] sHeader = {
      new POIField(
        this.getText("summaries.annualReport2018.table12Title1", new String[] {String.valueOf(this.getSelectedYear())}),
        ParagraphAlignment.LEFT),
      new POIField(this.getText("summaries.annualReport2018.table12Title2"), ParagraphAlignment.LEFT)};
    List<POIField> header = Arrays.asList(sHeader);
    headers.add(header);

    if (flagshipPlannedList != null && !flagshipPlannedList.isEmpty()) {
      // for (int i = 0; i < flagshipPlannedList.size(); i++) {
      String examples = "", area = "";

      POIField[] sData = {new POIField(examples, ParagraphAlignment.LEFT, true, "000000"),
        new POIField(area, ParagraphAlignment.LEFT, true, "0000")};
      data = Arrays.asList(sData);
      datas.add(data);
      // }
    }

    poiSummary.textTable(document, headers, datas, false, "table3AnnualReport2018");
  }

  private void createTable13() {
    try {

      if (reportSynthesisPMU.getReportSynthesisFinancialSummary() != null && reportSynthesisPMU
        .getReportSynthesisFinancialSummary().getReportSynthesisFinancialSummaryBudgets() != null) {
        reportSynthesisPMU.getReportSynthesisFinancialSummary()
          .setBudgets(new ArrayList<>(
            reportSynthesisPMU.getReportSynthesisFinancialSummary().getReportSynthesisFinancialSummaryBudgets().stream()
              .filter(t -> t.isActive()).collect(Collectors.toList())));
      } else {
        flagshipLiaisonInstitutions.sort(Comparator.comparing(LiaisonInstitution::getAcronym));
        reportSynthesisPMU.getReportSynthesisFinancialSummary().setBudgets(new ArrayList<>());
        for (LiaisonInstitution liInstitution : flagshipLiaisonInstitutions) {
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

      List<ReportSynthesisFinancialSummaryBudget> reportSynthesisFinancialSummaryBudgetList =
        reportSynthesisPMU.getReportSynthesisFinancialSummary().getBudgets();

      List<List<POIField>> headers = new ArrayList<>();
      POIField[] sHeader = {new POIField("", ParagraphAlignment.CENTER),
        new POIField(
          this.getText("annualReport.financial.tableJ.budget", new String[] {String.valueOf(this.getSelectedYear())})
            + "*",
          ParagraphAlignment.CENTER),
        new POIField("", ParagraphAlignment.CENTER), new POIField("", ParagraphAlignment.CENTER),
        new POIField(this.getText("annualReport.financial.tableJ.expenditure") + "*", ParagraphAlignment.CENTER),
        new POIField("", ParagraphAlignment.CENTER), new POIField("", ParagraphAlignment.CENTER),
        new POIField(this.getText("annualReport.financial.tableJ.difference") + "*", ParagraphAlignment.CENTER),
        new POIField("", ParagraphAlignment.CENTER), new POIField("", ParagraphAlignment.CENTER),
        new POIField(this.getText("summaries.annualReport2018.table13Title1"), ParagraphAlignment.CENTER)};

      POIField[] sHeader2 = {new POIField(" ", ParagraphAlignment.CENTER),
        new POIField(this.getText("financialPlan.tableE.w1w2"), ParagraphAlignment.CENTER),
        new POIField(this.getText("financialPlan.tableE.w3bilateral"), ParagraphAlignment.CENTER),
        new POIField(this.getText("financialPlan.tableE.total"), ParagraphAlignment.CENTER),
        new POIField(this.getText("financialPlan.tableE.w1w2"), ParagraphAlignment.CENTER),
        new POIField(this.getText("financialPlan.tableE.w3bilateral"), ParagraphAlignment.CENTER),
        new POIField(this.getText("financialPlan.tableE.total"), ParagraphAlignment.CENTER),
        new POIField(this.getText("financialPlan.tableE.w1w2"), ParagraphAlignment.CENTER),
        new POIField(this.getText("financialPlan.tableE.w3bilateral"), ParagraphAlignment.CENTER),
        new POIField(this.getText("financialPlan.tableE.total"), ParagraphAlignment.CENTER),
        new POIField(this.getText(""), ParagraphAlignment.CENTER)};

      List<POIField> header = Arrays.asList(sHeader);
      List<POIField> header2 = Arrays.asList(sHeader2);
      headers.add(header);
      headers.add(header2);

      List<List<POIField>> datas = new ArrayList<>();
      List<POIField> data;

      double totalW1w2Difference = 0.0, totalW3Difference = 0.0, grandTotalDifference = 0.0;
      if (reportSynthesisFinancialSummaryBudgetList != null && !reportSynthesisFinancialSummaryBudgetList.isEmpty()) {

        for (ReportSynthesisFinancialSummaryBudget reportSynthesisFinancialSummaryBudget : reportSynthesisFinancialSummaryBudgetList) {

          String category = "", comments = "";
          Double w1w2Planned = 0.0, w3Planned = 0.0, w1w2Actual = 0.0, w3Actual = 0.0, totalPlanned = 0.0,
            totalActual = 0.0, w1w2Difference = 0.0, w3Difference = 0.0, totalDifference = 0.0;

          /** Getting category name **/
          if (reportSynthesisFinancialSummaryBudget.getLiaisonInstitution() != null) {
            category = reportSynthesisFinancialSummaryBudget.getLiaisonInstitution().getComposedName();
          } else if (reportSynthesisFinancialSummaryBudget.getExpenditureArea().getExpenditureArea() != null) {
            category = reportSynthesisFinancialSummaryBudget.getExpenditureArea().getExpenditureArea();
          }
          if (reportSynthesisFinancialSummaryBudget.getW1Planned() != null) {
            w1w2Planned = reportSynthesisFinancialSummaryBudget.getW1Planned();
          }

          if (reportSynthesisFinancialSummaryBudget.getW3Planned() != null) {
            w3Planned = reportSynthesisFinancialSummaryBudget.getW3Planned()
              + reportSynthesisFinancialSummaryBudget.getBilateralPlanned();
          }

          totalPlanned = w1w2Planned + w3Planned;
          if (reportSynthesisFinancialSummaryBudget.getW1Actual() != null) {
            w1w2Actual = reportSynthesisFinancialSummaryBudget.getW1Actual();
          }

          if (reportSynthesisFinancialSummaryBudget.getW3Actual() != null) {
            w3Actual = reportSynthesisFinancialSummaryBudget.getW3Actual()
              + reportSynthesisFinancialSummaryBudget.getBilateralActual();
          }

          if (reportSynthesisFinancialSummaryBudget.getComments() != null) {
            comments = reportSynthesisFinancialSummaryBudget.getComments();
          }
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
            new POIField(currencyFormat.format(round(w1w2Planned / 1000, 2)), ParagraphAlignment.CENTER),
            new POIField(currencyFormat.format(round(w3Planned / 1000, 2)), ParagraphAlignment.CENTER),
            new POIField(currencyFormat.format(round(totalPlanned / 1000, 2)), ParagraphAlignment.CENTER),
            new POIField(currencyFormat.format(round(w1w2Actual / 1000, 2)), ParagraphAlignment.CENTER),
            new POIField(currencyFormat.format(round(w3Actual / 1000, 2)), ParagraphAlignment.CENTER),
            new POIField(currencyFormat.format(round(totalActual / 1000, 2)), ParagraphAlignment.CENTER),
            new POIField(currencyFormat.format(round(w1w2Difference / 1000, 2)), ParagraphAlignment.CENTER),
            new POIField(currencyFormat.format(round(w3Difference / 1000, 2)), ParagraphAlignment.CENTER),
            new POIField(currencyFormat.format(round(totalDifference / 1000, 2)), ParagraphAlignment.CENTER),
            new POIField(comments, ParagraphAlignment.CENTER)};

          data = Arrays.asList(sData);
          datas.add(data);
        }
      }

      Boolean bold = true;
      String blackColor = "000000";
      POIField[] sData = {new POIField("CRP Total", ParagraphAlignment.CENTER, bold, blackColor),

        new POIField(currencyFormat.format(round(totalw1w2Planned / 1000, 2)), ParagraphAlignment.CENTER, bold,
          blackColor),
        new POIField(currencyFormat.format(round(totalW3Planned / 1000, 2)), ParagraphAlignment.CENTER, bold,
          blackColor),
        new POIField(currencyFormat.format(round(grandTotalPlanned / 1000, 2)), ParagraphAlignment.CENTER, bold,
          blackColor),
        new POIField(currencyFormat.format(round(totalw1w2Actual / 1000, 2)), ParagraphAlignment.CENTER, bold,
          blackColor),
        new POIField(currencyFormat.format(round(totalW3Actual / 1000, 2)), ParagraphAlignment.CENTER, bold,
          blackColor),
        new POIField(currencyFormat.format(round(grandTotalActual / 1000, 2)), ParagraphAlignment.CENTER, bold,
          blackColor),
        new POIField(currencyFormat.format(round(totalW1w2Difference / 1000, 2)), ParagraphAlignment.CENTER, bold,
          blackColor),
        new POIField(currencyFormat.format(round(totalW3Difference / 1000, 2)), ParagraphAlignment.CENTER, bold,
          blackColor),
        new POIField(currencyFormat.format(round(grandTotalDifference / 1000, 2)), ParagraphAlignment.CENTER, bold,
          blackColor),};

      data = Arrays.asList(sData);
      datas.add(data);

      poiSummary.textTable(document, headers, datas, true, "tableJAnnualReport");
    } catch (Exception e) {
    }
  }

  private void createTable2() {

    List<List<POIField>> headers = new ArrayList<>();

    String blackColor = "000000";
    if (this.isEntityPlatform()) {
      Boolean bold = false;
      POIField[] sHeader = {
        new POIField(this.getText("summaries.annualReport2018.table2Title1"), ParagraphAlignment.LEFT, bold,
          blackColor),
        new POIField(this.getText("summaries.annualReport2018.table2Title2"), ParagraphAlignment.LEFT, bold,
          blackColor),
        new POIField(this.getText("summaries.annualReport2018.table2Title3"), ParagraphAlignment.LEFT, bold,
          blackColor),
        new POIField(this.getText("summaries.annualReport2018.table2Title4"), ParagraphAlignment.LEFT, true,
          blackColor),
        new POIField(this.getText(""), ParagraphAlignment.LEFT, bold, blackColor),
        new POIField(this.getText(""), ParagraphAlignment.LEFT, bold, blackColor),
        new POIField(this.getText(""), ParagraphAlignment.LEFT, bold, blackColor),
        new POIField(this.getText("summaries.annualReport2018.table2Title8"), ParagraphAlignment.LEFT, bold,
          blackColor),
        new POIField("", ParagraphAlignment.LEFT, bold, blackColor)};

      POIField[] sHeader2 = {new POIField("", ParagraphAlignment.LEFT, bold, blackColor),
        new POIField("", ParagraphAlignment.LEFT, bold, blackColor),
        new POIField("", ParagraphAlignment.LEFT, bold, blackColor),
        new POIField(this.getText("summaries.annualReport2018.table2Gender"), ParagraphAlignment.CENTER, bold,
          blackColor),
        new POIField(this.getText("summaries.annualReport2018.table2Youth"), ParagraphAlignment.CENTER, bold,
          blackColor),
        new POIField(this.getText("summaries.annualReport2018.table2Capdev"), ParagraphAlignment.CENTER, bold,
          blackColor),
        new POIField(this.getText("summaries.annualReport2018.table2ClimateChange"), ParagraphAlignment.CENTER, bold,
          blackColor)};

      List<POIField> header = Arrays.asList(sHeader);
      List<POIField> header2 = Arrays.asList(sHeader2);
      headers.add(header);
      headers.add(header2);
    }

    if (this.isEntityCRP()) {
      Boolean bold = true;
      POIField[] sHeader = {
        new POIField(this.getText("summaries.annualReport2018.table2Title1"), ParagraphAlignment.LEFT, bold,
          blackColor),
        new POIField(this.getText("summaries.annualReport2018.table2Title2"), ParagraphAlignment.LEFT, false,
          blackColor),
        new POIField(this.getText("summaries.annualReport2018.table2Title3"), ParagraphAlignment.LEFT, false,
          blackColor),
        new POIField(this.getText("summaries.annualReport2018.table2Title4"), ParagraphAlignment.CENTER, false,
          blackColor),
        new POIField("", ParagraphAlignment.CENTER, false, blackColor),
        new POIField("", ParagraphAlignment.CENTER, false, blackColor),
        new POIField("", ParagraphAlignment.CENTER, false, blackColor),
        new POIField(this.getText(""), ParagraphAlignment.CENTER, false, blackColor), new POIField(
          this.getText("summaries.annualReport2018.table2Title8"), ParagraphAlignment.LEFT, false, blackColor)};

      bold = false;
      POIField[] sHeader2 = {new POIField("", ParagraphAlignment.LEFT, bold, blackColor),
        new POIField("", ParagraphAlignment.LEFT, bold, blackColor),
        new POIField("", ParagraphAlignment.LEFT, bold, blackColor),
        new POIField(this.getText("summaries.annualReport2018.table2Gender"), ParagraphAlignment.LEFT, bold,
          blackColor),
        new POIField(this.getText("summaries.annualReport2018.table2Youth"), ParagraphAlignment.LEFT, bold, blackColor),
        new POIField(this.getText("summaries.annualReport2018.table2Capdev"), ParagraphAlignment.LEFT, bold,
          blackColor),
        new POIField(this.getText("summaries.annualReport2018.table2ClimateChange"), ParagraphAlignment.LEFT, bold,
          blackColor)};

      List<POIField> header = Arrays.asList(sHeader);
      List<POIField> header2 = Arrays.asList(sHeader2);
      headers.add(header);
      headers.add(header2);
    }
    List<List<POIField>> datas = new ArrayList<>();
    List<POIField> data;
    data = new ArrayList<>();

    String name = null, levelMaturity = "", srfSubIdo = "", gender = "", youth = "", capdev = "", climateChange = "",
      investmentType = "", policytype = "", geographicScope = "";

    for (ProjectPolicy projectPolicy : projectPolicies) {
      if (projectPolicy != null && projectPolicy.getProjectPolicyInfo() != null) {
        srfSubIdo = "";
        if (projectPolicy.getProjectPolicyInfo().getTitle() != null) {
          name = projectPolicy.getProjectPolicyInfo().getId() + " - " + projectPolicy.getProjectPolicyInfo().getTitle();
        }
        if (projectPolicy.getProjectPolicyInfo().getRepIndStageProcess() != null
          && projectPolicy.getProjectPolicyInfo().getRepIndStageProcess().getName() != null) {
          levelMaturity = projectPolicy.getProjectPolicyInfo().getRepIndStageProcess().getName();
        }
        if (projectPolicy.getSubIdos(this.getActualPhase()) != null) {
          for (ProjectPolicySubIdo subIdo : projectPolicy.getSubIdos(this.getActualPhase())) {
            if (subIdo.getSrfSubIdo() != null && subIdo.getSrfSubIdo().getDescription() != null) {
              srfSubIdo += subIdo.getSrfSubIdo().getDescription() + " ";
            }
          }
        }

        if (projectPolicy.getProjectPolicyInfo().getRepIndOrganizationType() != null
          && projectPolicy.getProjectPolicyInfo().getRepIndOrganizationType().getName() != null) {
          investmentType = projectPolicy.getProjectPolicyInfo().getRepIndOrganizationType().getName();
        }

        if (projectPolicy.getProjectPolicyInfo().getRepIndPolicyInvestimentType() != null
          && projectPolicy.getProjectPolicyInfo().getRepIndPolicyInvestimentType().getName() != null) {
          investmentType = "";
        }

        if (projectPolicy.getGeographicScopes(this.getActualPhase()) != null) {
          for (ProjectPolicyGeographicScope policyGeographicScope : projectPolicy
            .getGeographicScopes(this.getActualPhase())) {
            if (policyGeographicScope != null && policyGeographicScope.getRepIndGeographicScope() != null
              && policyGeographicScope.getRepIndGeographicScope().getName() != null) {
              geographicScope = policyGeographicScope.getRepIndGeographicScope().getName();
            }
          }
        }
        if (projectPolicy.getCrossCuttingMarkers(this.getActualPhase()) != null) {
          for (ProjectPolicyCrossCuttingMarker policyCrossCutting : projectPolicy
            .getCrossCuttingMarkers(this.getActualPhase())) {
            if (policyCrossCutting != null && policyCrossCutting.getCgiarCrossCuttingMarker() != null
              && policyCrossCutting.getCgiarCrossCuttingMarker().getId() != null) {
              if (policyCrossCutting.getRepIndGenderYouthFocusLevel() != null
                && policyCrossCutting.getRepIndGenderYouthFocusLevel().getName() != null) {
                if (policyCrossCutting.getCgiarCrossCuttingMarker().getId() == 1) {
                  gender = policyCrossCutting.getRepIndGenderYouthFocusLevel().getName();
                }
                if (policyCrossCutting.getCgiarCrossCuttingMarker().getId() == 2) {
                  youth = policyCrossCutting.getRepIndGenderYouthFocusLevel().getName();
                }
                if (policyCrossCutting.getCgiarCrossCuttingMarker().getId() == 3) {
                  capdev = policyCrossCutting.getRepIndGenderYouthFocusLevel().getName();
                }
                if (policyCrossCutting.getCgiarCrossCuttingMarker().getId() == 4) {
                  climateChange = policyCrossCutting.getRepIndGenderYouthFocusLevel().getName();
                }
              }
            }
          }
        }
      }
      POIField[] sData = {new POIField(name, ParagraphAlignment.LEFT),
        new POIField(levelMaturity, ParagraphAlignment.LEFT), new POIField(srfSubIdo, ParagraphAlignment.LEFT),
        new POIField(gender, ParagraphAlignment.LEFT, false, blackColor), new POIField(youth, ParagraphAlignment.LEFT),
        new POIField(capdev, ParagraphAlignment.LEFT), new POIField(climateChange, ParagraphAlignment.CENTER),
        new POIField(investmentType, ParagraphAlignment.CENTER)};
      data = Arrays.asList(sData);
      datas.add(data);
    }


    String text = "";

    if (this.isEntityPlatform()) {
      text = "table2AnnualReport2018PLT";
    }
    if (this.isEntityCRP()) {
      text = "table2AnnualReport2018CRP";
    }
    if (text == null || text.isEmpty()) {
      text = "table2AnnualReport2018CRP";
    }
    poiSummary.textTable(document, headers, datas, false, text);
  }


  private void createTable3() {

    List<List<POIField>> headers = new ArrayList<>();
    List<List<POIField>> datas = new ArrayList<>();
    List<POIField> data;
    POIField[] sHeader = {
      new POIField(
        this.getText("summaries.annualReport2018.table3Title1", new String[] {String.valueOf(this.getSelectedYear())}),
        ParagraphAlignment.LEFT),
      new POIField(this.getText("summaries.annualReport2018.table3Title2"), ParagraphAlignment.LEFT),
      new POIField("", ParagraphAlignment.LEFT)};
    List<POIField> header = Arrays.asList(sHeader);
    headers.add(header);

    for (ProjectExpectedStudy projectExpectStudy : projectExpectedStudies) {
      String title = "", maturity = "", indicator = "";
      if (projectExpectStudy != null && projectExpectStudy.getProjectExpectedStudyInfo(this.getActualPhase()) != null) {
        if (projectExpectStudy.getProjectExpectedStudyInfo(this.getActualPhase()).getTitle() != null) {
          title = "OICR" + projectExpectStudy.getId() + " - "
            + projectExpectStudy.getProjectExpectedStudyInfo(this.getActualPhase()).getTitle();
        }
        if (projectExpectStudy.getProjectExpectedStudyInfo().getRepIndStageProcess() != null
          && projectExpectStudy.getProjectExpectedStudyInfo().getRepIndStageProcess().getName() != null) {
          maturity = projectExpectStudy.getProjectExpectedStudyInfo().getRepIndStageProcess().getName();
        }
        if (projectExpectStudy.getProjectExpectedStudyInfo().getEvidenceTag() != null
          && projectExpectStudy.getProjectExpectedStudyInfo().getEvidenceTag().getName() != null) {
          indicator = projectExpectStudy.getProjectExpectedStudyInfo().getEvidenceTag().getName();
        }
      }

      POIField[] sData = {new POIField(title, ParagraphAlignment.LEFT),
        new POIField(maturity, ParagraphAlignment.CENTER), new POIField(indicator, ParagraphAlignment.LEFT)};
      data = Arrays.asList(sData);
      datas.add(data);

    }

    List<LiaisonInstitution> liaisonInstitutionsList =
      new ArrayList<>(this.getLoggedCrp().getLiaisonInstitutions().stream()
        .filter(c -> c.getCrpProgram() != null && c.isActive()
          && c.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue())
        .collect(Collectors.toList()));
    liaisonInstitutionsList.sort(Comparator.comparing(LiaisonInstitution::getAcronym));

    flagshipPlannedList = reportSynthesisMeliaManager.getMeliaPlannedList(liaisonInstitutionsList,
      this.getSelectedPhase().getId(), this.getLoggedCrp(), pmuInstitution);
    if (flagshipPlannedList != null && !flagshipPlannedList.isEmpty()) {
      for (int i = 0; i < flagshipPlannedList.size(); i++) {
        String studies = "", status = "", comments = "";
        studies = flagshipPlannedList.get(i).getProjectExpectedStudy()
          .getProjectExpectedStudyInfo(this.getSelectedPhase()).getTitle();
        status = flagshipPlannedList.get(i).getProjectExpectedStudy()
          .getProjectExpectedStudyInfo(this.getSelectedPhase()).getStatusName();
        if (flagshipPlannedList.get(i).getProjectExpectedStudy().getProjectExpectedStudyInfo(this.getSelectedPhase())
          .getTopLevelComments() != null) {
          comments = flagshipPlannedList.get(i).getProjectExpectedStudy()
            .getProjectExpectedStudyInfo(this.getSelectedPhase()).getTopLevelComments();
        }

        POIField[] sData = {new POIField(studies, ParagraphAlignment.LEFT),
          new POIField(status, ParagraphAlignment.CENTER), new POIField(comments, ParagraphAlignment.LEFT)};
        data = Arrays.asList(sData);
        datas.add(data);
      }
    }

    poiSummary.textTable(document, headers, datas, false, "table3AnnualReport2018");
  }

  private void createTable4() {

    List<List<POIField>> headers = new ArrayList<>();
    List<List<POIField>> datas = new ArrayList<>();
    List<POIField> data;
    POIField[] sHeader = {
      new POIField(
        this.getText("summaries.annualReport2018.table4Title1", new String[] {String.valueOf(this.getSelectedYear())}),
        ParagraphAlignment.LEFT),
      new POIField(this.getText("summaries.annualReport2018.table4Title2"), ParagraphAlignment.LEFT),
      new POIField(this.getText("summaries.annualReport2018.table4Title3"), ParagraphAlignment.LEFT),
      new POIField(this.getText("summaries.annualReport2018.table4Title4"), ParagraphAlignment.LEFT)};
    List<POIField> header = Arrays.asList(sHeader);
    headers.add(header);
    for (ProjectInnovation projectInnovation : projectInnovations) {
      String title = "", type = "", stage = "", geographic = "", country = "", region = "";

      if (projectInnovation != null && projectInnovation.getProjectInnovationInfo(this.getActualPhase()) != null) {
        if (projectInnovation.getProjectInnovationInfo(this.getActualPhase()).getTitle() != null) {
          title = projectInnovation.getId() + " - "
            + projectInnovation.getProjectInnovationInfo(this.getActualPhase()).getTitle();
        }
        if (projectInnovation.getProjectInnovationInfo(this.getActualPhase()).getRepIndInnovationType() != null
          && projectInnovation.getProjectInnovationInfo(this.getActualPhase()).getRepIndInnovationType()
            .getName() != null) {
          type = projectInnovation.getProjectInnovationInfo(this.getActualPhase()).getRepIndInnovationType().getName();
        }
        if (projectInnovation.getProjectInnovationInfo(this.getActualPhase()).getRepIndStageInnovation() != null
          && projectInnovation.getProjectInnovationInfo(this.getActualPhase()).getRepIndStageInnovation()
            .getName() != null) {
          stage =
            projectInnovation.getProjectInnovationInfo(this.getActualPhase()).getRepIndStageInnovation().getName();
        }
        if (projectInnovation.getGeographicScopes(this.getActualPhase()) != null) {
          List<ProjectInnovationGeographicScope> innovationGeographics =
            projectInnovation.getGeographicScopes(this.getActualPhase());
          for (ProjectInnovationGeographicScope innovationGeographic : innovationGeographics) {
            if (innovationGeographic != null && innovationGeographic.getRepIndGeographicScope() != null
              && innovationGeographic.getRepIndGeographicScope().getName() != null) {
              geographic += innovationGeographic.getRepIndGeographicScope().getName() + ", ";
            }
          }
        }

        if (projectInnovation.getRegions(this.getActualPhase()) != null) {
          List<ProjectInnovationRegion> innovationGeographics = projectInnovation.getRegions(this.getActualPhase());
          for (ProjectInnovationRegion innovationGeographic : innovationGeographics) {
            if (innovationGeographic != null && innovationGeographic.getLocElement() != null
              && innovationGeographic.getLocElement().getName() != null) {
              region += innovationGeographic.getLocElement().getName() + ", ";
            }
          }
        }

        if (projectInnovation.getCountries(this.getActualPhase()) != null) {
          List<ProjectInnovationCountry> innovationGeographics = projectInnovation.getCountries(this.getActualPhase());
          for (ProjectInnovationCountry innovationGeographic : innovationGeographics) {
            if (innovationGeographic != null && innovationGeographic.getLocElement() != null
              && innovationGeographic.getLocElement().getName() != null) {
              country += innovationGeographic.getLocElement().getName() + ", ";
            }
          }
        }
      }

      if (country != null) {
        geographic += country;
      }
      if (region != null) {
        geographic += region;
      }

      POIField[] sData = {new POIField(title, ParagraphAlignment.LEFT), new POIField(type, ParagraphAlignment.CENTER),
        new POIField(stage, ParagraphAlignment.LEFT), new POIField(geographic, ParagraphAlignment.LEFT)};
      data = Arrays.asList(sData);
      datas.add(data);
    }
    poiSummary.textTable(document, headers, datas, false, "table4AnnualReport2018");
  }

  private void createTable5() {

    List<List<POIField>> headers = new ArrayList<>();
    List<List<POIField>> datas = new ArrayList<>();
    List<POIField> data;
    POIField[] sHeader = {
      new POIField(
        this.getText("summaries.annualReport2018.table5Title1", new String[] {String.valueOf(this.getSelectedYear())}),
        ParagraphAlignment.LEFT, false, "000000"),
      new POIField(this.getText("summaries.annualReport2018.table5Title2"), ParagraphAlignment.CENTER, false, "000000"),
      new POIField(this.getText("summaries.annualReport2018.table5Title3"), ParagraphAlignment.LEFT, false, "76923C"),
      new POIField(this.getText("summaries.annualReport2018.table5Title4"), ParagraphAlignment.CENTER, false, "000000"),
      new POIField(this.getText("summaries.annualReport2018.table5Title5") + " "
        + this.getText("summaries.annualReport2018.table5Title51"), ParagraphAlignment.LEFT, false, "000000"),
      new POIField(this.getText("summaries.annualReport2018.table5Title6") + " "
        + this.getText("summaries.annualReport2018.table5Title61"), ParagraphAlignment.LEFT, false, "000000")};
    List<POIField> header = Arrays.asList(sHeader);
    headers.add(header);

    String fp = "", outcomes = "", narrative = "", milestone = "", milestoneStatus = "", evidenceMilestone = "",
      lastFP = "", lastOutcome = "";

    if (flagships != null && !flagships.isEmpty()) {
      flagships.sort((p1, p2) -> p1.getAcronym().compareTo(p2.getAcronym()));

      for (CrpProgram flagship : flagships) {
        int outcome_index = 0;
        if (flagship.getOutcomes() != null) {
          for (CrpProgramOutcome outcome : flagship.getOutcomes()) {

            int milestone_index = 0;
            for (CrpMilestone crpMilestone : outcome.getMilestones()) {
              Boolean isFlagshipRow = (outcome_index == 0) && (milestone_index == 0);
              Boolean isOutcomeRow = (milestone_index == 0);

              if (isFlagshipRow) {
                fp = flagship.getAcronym();
              } else {
                fp = "";
              }
              if (fp.equals(lastFP)) {
                fp = "";
              } else {
                lastFP = fp;
              }

              if (isOutcomeRow) {
                outcomes = outcome.getComposedName();
              } else {
                outcomes = "";
              }
              if (outcomes.equals(lastOutcome)) {
                outcomes = "";
              } else {
                lastOutcome = outcomes;
              }

              milestone = crpMilestone.getComposedName();

              evidenceMilestone = crpMilestone.getModificationJustification();

              POIField[] sData =
                {new POIField(fp, ParagraphAlignment.CENTER), new POIField(outcomes, ParagraphAlignment.LEFT),
                  new POIField(narrative, ParagraphAlignment.LEFT), new POIField(milestone, ParagraphAlignment.LEFT),
                  new POIField(milestoneStatus, ParagraphAlignment.LEFT),
                  new POIField(evidenceMilestone, ParagraphAlignment.LEFT)};
              data = Arrays.asList(sData);
              datas.add(data);
            }
          }
        }
      }
    }
    poiSummary.textTable(document, headers, datas, false, "table5AnnualReport2018");
  }

  public void createTable6() {
    List<List<POIField>> headers = new ArrayList<>();
    String blackColor = "000000";

    POIField[] sHeader = {new POIField("", ParagraphAlignment.CENTER, true, blackColor),
      new POIField(this.getText("summaries.annualReport2018.table6Title4"), ParagraphAlignment.LEFT, true, blackColor),
      new POIField(this.getText("summaries.annualReport2018.table6Title5"), ParagraphAlignment.LEFT, true, blackColor)};
    List<POIField> header = Arrays.asList(sHeader);
    headers.add(header);

    List<List<POIField>> datas = new ArrayList<>();
    List<POIField> data;

    data = new ArrayList<>();

    String number = "", percent = "";
    List<String> titles = new ArrayList<>();
    titles.add(this.getText("summaries.annualReport2018.table6Title1"));
    titles.add(this.getText("summaries.annualReport2018.table6Title2"));
    titles.add(this.getText("summaries.annualReport2018.table6Title3"));
    int i = 0;
    for (String title : titles) {

      switch (i) {
        case 0:
          number = String.valueOf(totalLimited);
          break;
        case 1:
          number = String.valueOf(totalOpenAccess);
          break;
        case 2:
          number = String.valueOf(totalIsis);
          break;
      }

      Boolean bold = false;
      String blueColor = "000099";
      POIField[] sData = {new POIField(title, ParagraphAlignment.LEFT, true, blackColor),
        new POIField(number, ParagraphAlignment.LEFT, bold, blueColor),
        new POIField(percent, ParagraphAlignment.LEFT, bold, blueColor)};
      data = Arrays.asList(sData);
      datas.add(data);
      i++;
    }
    poiSummary.textTable(document, headers, datas, false, "table6AnnualReport2018");
  }

  private void createTable7() {
    List<List<POIField>> headers = new ArrayList<>();
    List<List<POIField>> datas = new ArrayList<>();
    List<POIField> data;
    POIField[] sHeader = {
      new POIField(
        this.getText("summaries.annualReport2018.table7Title1", new String[] {String.valueOf(this.getSelectedYear())}),
        ParagraphAlignment.LEFT),
      new POIField(this.getText("summaries.annualReport2018.table7Title2"), ParagraphAlignment.LEFT),
      new POIField(this.getText("summaries.annualReport2018.table7Title3"), ParagraphAlignment.LEFT)};
    List<POIField> header = Arrays.asList(sHeader);
    headers.add(header);

    for (ProjectExpectedStudy projectExpectStudy : projectExpectedStudies) {
      String trinees = "", female = "", male = "";

      POIField[] sData = {new POIField(trinees, ParagraphAlignment.LEFT),
        new POIField(female, ParagraphAlignment.CENTER), new POIField(male, ParagraphAlignment.LEFT)};
      data = Arrays.asList(sData);
      datas.add(data);
    }


    // for (int i = 0; i < flagshipPlannedList.size(); i++) {
    String studies = "", status = "", comments = "";


    POIField[] sData = {new POIField(studies, ParagraphAlignment.LEFT), new POIField(status, ParagraphAlignment.CENTER),
      new POIField(comments, ParagraphAlignment.LEFT)};
    data = Arrays.asList(sData);
    datas.add(data);

    // }

    poiSummary.textTable(document, headers, datas, false, "table3AnnualReport2018");
  }

  private void createTable8() {
    List<List<POIField>> headers = new ArrayList<>();
    List<List<POIField>> datas = new ArrayList<>();
    List<POIField> data;
    POIField[] sHeader = {
      new POIField(
        this.getText("summaries.annualReport2018.table8Title1", new String[] {String.valueOf(this.getSelectedYear())}),
        ParagraphAlignment.LEFT),
      new POIField(this.getText("summaries.annualReport2018.table8Title2"), ParagraphAlignment.LEFT),
      new POIField(this.getText("summaries.annualReport2018.table8Title3"), ParagraphAlignment.LEFT),
      new POIField(this.getText("summaries.annualReport2018.table8Title4"), ParagraphAlignment.LEFT)};
    List<POIField> header = Arrays.asList(sHeader);
    headers.add(header);

    for (ReportSynthesisKeyPartnershipExternal flagshipExternalPartnership : flagshipExternalPartnerships) {
      String leadFP = "", description = "", keyPartners = "", mainArea = "";

      if (flagshipExternalPartnership != null) {

        if (flagshipExternalPartnership.getReportSynthesisKeyPartnership() != null
          && flagshipExternalPartnership.getReportSynthesisKeyPartnership().getReportSynthesis() != null
          && flagshipExternalPartnership.getReportSynthesisKeyPartnership().getReportSynthesis()
            .getLiaisonInstitution() != null
          && flagshipExternalPartnership.getReportSynthesisKeyPartnership().getReportSynthesis().getLiaisonInstitution()
            .getCrpProgram() != null
          && flagshipExternalPartnership.getReportSynthesisKeyPartnership().getReportSynthesis().getLiaisonInstitution()
            .getCrpProgram().getAcronym() != null) {
          leadFP = flagshipExternalPartnership.getReportSynthesisKeyPartnership().getReportSynthesis()
            .getLiaisonInstitution().getCrpProgram().getAcronym();
        }

        if (flagshipExternalPartnership.getDescription() != null) {
          description = flagshipExternalPartnership.getDescription();
        }

        if (flagshipExternalPartnership.getInstitutions() != null) {
          for (ReportSynthesisKeyPartnershipExternalInstitution institution : flagshipExternalPartnership
            .getInstitutions()) {
            keyPartners += institution + ",";
          }
        }

        if (flagshipExternalPartnership.getMainAreas() != null) {
          for (ReportSynthesisKeyPartnershipExternalMainArea externalMainArea : flagshipExternalPartnership
            .getMainAreas()) {
            mainArea += mainArea + ",";
          }
        }

      }
      POIField[] sData =
        {new POIField(leadFP, ParagraphAlignment.LEFT), new POIField(description, ParagraphAlignment.CENTER),
          new POIField(keyPartners, ParagraphAlignment.LEFT), new POIField(mainArea, ParagraphAlignment.LEFT)};
      data = Arrays.asList(sData);
      datas.add(data);
    }
    poiSummary.textTable(document, headers, datas, false, "table3AnnualReport2018");
  }

  private void createTable9() {
    List<List<POIField>> headers = new ArrayList<>();
    List<List<POIField>> datas = new ArrayList<>();
    List<POIField> data;
    POIField[] sHeader = {
      new POIField(
        this.getText("summaries.annualReport2018.table9Title1", new String[] {String.valueOf(this.getSelectedYear())}),
        ParagraphAlignment.LEFT),
      new POIField(this.getText("summaries.annualReport2018.table9Title2"), ParagraphAlignment.LEFT),
      new POIField(this.getText("summaries.annualReport2018.table9Title3"), ParagraphAlignment.LEFT)};
    List<POIField> header = Arrays.asList(sHeader);
    headers.add(header);


    if (flagshipPlannedList != null && !flagshipPlannedList.isEmpty()) {
      // for (int i = 0; i < flagshipPlannedList.size(); i++) {
      String studies = "", status = "", comments = "";

      POIField[] sData = {new POIField(studies, ParagraphAlignment.LEFT),
        new POIField(status, ParagraphAlignment.CENTER), new POIField(comments, ParagraphAlignment.LEFT)};
      data = Arrays.asList(sData);
      datas.add(data);
      // }
    }

    poiSummary.textTable(document, headers, datas, false, "table3AnnualReport2018");
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

    List<List<POIField>> datas = new ArrayList<>();;
    List<POIField> data;
    // Get liaison institution list
    List<LiaisonInstitution> liaisonInstitutionsList =
      new ArrayList<>(this.getLoggedCrp().getLiaisonInstitutions().stream()
        .filter(c -> c.getCrpProgram() != null && c.isActive()
          && c.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue())
        .collect(Collectors.toList()));
    liaisonInstitutionsList.sort(Comparator.comparing(LiaisonInstitution::getAcronym));
    flagshipPlannedList = reportSynthesisCrpProgressManager.getPlannedList(liaisonInstitutionsList,
      this.getSelectedPhase().getId(), this.getLoggedCrp(), pmuInstitution);

    if (flagshipPlannedList != null && !flagshipPlannedList.isEmpty()) {
      for (PowbEvidencePlannedStudyDTO powbEvidencePlannedStudyDTO : flagshipPlannedList.stream()
        .sorted((f1, f2) -> f1.getProjectExpectedStudy().getProjectExpectedStudyInfo(this.getSelectedPhase()).getTitle()
          .compareTo(f2.getProjectExpectedStudy().getProjectExpectedStudyInfo(this.getSelectedPhase()).getTitle()))
        .collect(Collectors.toList())) {
        String title = "", outcomeReportLink = "", subIdo = "", describeGender = "", describeYouth = "",
          describeCapDev = "", additional = "", linkToEvidence = "";

        /** creating download link **/
        String year = powbEvidencePlannedStudyDTO.getProjectExpectedStudy()
          .getProjectExpectedStudyInfo(this.getSelectedPhase()).getYear() + "";
        String cycle = this.getCurrentCycle();
        String study = powbEvidencePlannedStudyDTO.getProjectExpectedStudy().getId() + "";

        outcomeReportLink = this.getBaseUrl() + "/projects/" + this.getLoggedCrp().getAcronym()
          + "/studySummary.do?studyID=" + study + "&cycle=" + cycle + "&year=" + year;

        if (powbEvidencePlannedStudyDTO.getProjectExpectedStudy().getProjectExpectedStudyInfo() != null) {
          ProjectExpectedStudyInfo projectExpectedStudyInfo =
            powbEvidencePlannedStudyDTO.getProjectExpectedStudy().getProjectExpectedStudyInfo();
          data = new ArrayList<>();
          title = projectExpectedStudyInfo.getTitle() != null && !projectExpectedStudyInfo.getTitle().isEmpty()
            ? projectExpectedStudyInfo.getTitle() : this.getText("global.untitled");
          if (powbEvidencePlannedStudyDTO.getProjectExpectedStudy().getSubIdos() != null
            && !powbEvidencePlannedStudyDTO.getProjectExpectedStudy().getSubIdos().isEmpty()) {
            for (int j = 0; j < powbEvidencePlannedStudyDTO.getProjectExpectedStudy().getSubIdos().size(); j++) {
              subIdo += "\n •" + powbEvidencePlannedStudyDTO.getProjectExpectedStudy().getSubIdos().get(j)
                .getSrfSubIdo().getDescription();
            }
          }

          if (powbEvidencePlannedStudyDTO.getProjectExpectedStudy().getProjectExpectedStudyInfo()
            .getReferencesText() != null
            && !powbEvidencePlannedStudyDTO.getProjectExpectedStudy().getProjectExpectedStudyInfo().getReferencesText()
              .isEmpty()) {
            linkToEvidence =
              powbEvidencePlannedStudyDTO.getProjectExpectedStudy().getProjectExpectedStudyInfo().getReferencesText();
          }

          if (projectExpectedStudyInfo.getGenderLevel() != null) {
            if (projectExpectedStudyInfo.getDescribeGender() == null) {
              describeGender = projectExpectedStudyInfo.getGenderLevel().getName() + "\n";
            } else {
              describeGender = projectExpectedStudyInfo.getGenderLevel().getName() + "\n"
                + projectExpectedStudyInfo.getDescribeGender();
            }
          }
          if (projectExpectedStudyInfo.getYouthLevel() != null) {
            if (projectExpectedStudyInfo.getDescribeYouth() == null) {
              describeYouth = projectExpectedStudyInfo.getYouthLevel().getName();
            } else {
              describeYouth =
                projectExpectedStudyInfo.getYouthLevel().getName() + "\n" + projectExpectedStudyInfo.getDescribeYouth();
            }
          }
          if (projectExpectedStudyInfo.getCapdevLevel() != null) {
            if (projectExpectedStudyInfo.getDescribeCapdev() == null) {
              describeCapDev = projectExpectedStudyInfo.getCapdevLevel().getName();
            } else {
              describeCapDev = projectExpectedStudyInfo.getCapdevLevel().getName() + "\n"
                + projectExpectedStudyInfo.getDescribeCapdev();
            }
          }

          Boolean bold = false;
          String blueColor = "0000EE";
          additional = "Gender: " + describeGender + "\nYouth: " + describeYouth + " \nCapDev: " + describeCapDev;
          POIField[] sData = {new POIField(title, ParagraphAlignment.LEFT, bold, blueColor, outcomeReportLink),
            new POIField(subIdo, ParagraphAlignment.LEFT), new POIField(linkToEvidence, ParagraphAlignment.LEFT),
            new POIField(additional, ParagraphAlignment.LEFT)};
          data = Arrays.asList(sData);
          datas.add(data);
        }
      }
    }
    poiSummary.textTable(document, headers, datas, false, "tableA2AnnualReport");
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
    // Setup Table C
    tableC = reportSynthesisCrossCuttingDimensionManager.getTableC(this.getSelectedPhase(), this.getLoggedCrp());
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


  @Override
  public String execute() throws Exception {
    if (this.getSelectedPhase() == null) {
      return NOT_FOUND;
    }


    if (this.isEntityCRP()) {
      try {

        CTDocument1 doc = document.getDocument();
        CTBody body = doc.getBody();
        poiSummary.pageHeader(document, this.getText("summaries.annualReportCRP2018.header"));

        // Get datetime
        ZonedDateTime timezone = ZonedDateTime.now();
        String zone = timezone.getOffset() + "";
        if (zone.equals("Z")) {
          zone = "+0";
        }

        this.createPageFooter();
        // poiSummary.pageFooter(document, "This report was generated on " + currentDate);

        // Cover
        poiSummary.textLineBreak(document, 6);
        poiSummary.textHeadCoverTitleAR2018(document.createParagraph(),
          this.getText("summaries.annualReportCRP2018.mainTitle"));
        document.createParagraph().setPageBreak(true);

        // Table of contents
        document.createTOC();

        // the body content
        XWPFParagraph paragraph = document.createParagraph();
        CTP ctP = paragraph.getCTP();
        CTSimpleField toc = ctP.addNewFldSimple();
        toc.setInstr("TOC \\h");
        toc.setDirty(STOnOff.TRUE);
        XWPFRun run = paragraph.createRun();

        // Toc section
        addCustomHeadingStyle(document, "heading 1", 1);
        addCustomHeadingStyle(document, "heading 0", 1);
        addCustomHeadingStyle(document, "heading 2", 1);
        addCustomHeadingStyle(document, "heading 3", 1);
        addCustomHeadingStyle(document, "heading 4", 2);
        addCustomHeadingStyle(document, "heading 5", 2);
        addCustomHeadingStyle(document, "heading 6", 2);
        addCustomHeadingStyle(document, "heading 7", 2);
        addCustomHeadingStyle(document, "heading 8", 2);
        addCustomHeadingStyle(document, "heading 9", 2);
        addCustomHeadingStyle(document, "heading 10", 2);
        addCustomHeadingStyle(document, "heading 11", 2);
        addCustomHeadingStyle(document, "heading 12", 2);
        addCustomHeadingStyle(document, "heading 13", 2);
        addCustomHeadingStyle(document, "heading 14", 2);
        addCustomHeadingStyle(document, "heading 15", 1);
        addCustomHeadingStyle(document, "heading 16", 2);
        addCustomHeadingStyle(document, "heading 17", 2);
        addCustomHeadingStyle(document, "heading 18", 2);
        addCustomHeadingStyle(document, "heading 19", 2);
        addCustomHeadingStyle(document, "heading 20", 2);
        addCustomHeadingStyle(document, "heading 21", 2);
        addCustomHeadingStyle(document, "heading 22", 2);
        addCustomHeadingStyle(document, "heading 23", 2);
        addCustomHeadingStyle(document, "heading 24", 2);
        addCustomHeadingStyle(document, "heading 25", 1);

        // First page
        document.createParagraph().setPageBreak(true);
        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.setFontSize(14);
        run.setBold(true);
        run.setText(this.getText("summaries.annualReportCRP2018.cover"));
        paragraph.setStyle("heading 1");

        String unitName = this.getLoggedCrp().getAcronym() != null && !this.getLoggedCrp().getAcronym().isEmpty()
          ? this.getLoggedCrp().getAcronym() : this.getLoggedCrp().getName();
        poiSummary.textParagraph(document.createParagraph(),
          this.getText("summaries.annualReportCRP2018.unitName") + ": " + unitName);
        poiSummary.textParagraph(document.createParagraph(), this.getText("summaries.annualReport.LeadCenter") + ":");
        this.addParticipatingCenters();

        // Part A - Narrative section
        // Executive Summary
        poiSummary.textLineBreak(document, 1);
        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.setFontSize(14);
        run.setColor("76923C");
        run.setBold(true);
        run.setText(this.getText("summaries.annualReportCRP2018.executiveSummary"));
        this.addNarrativeSection();
        paragraph.setStyle("heading 0");

        // First page
        poiSummary.textLineBreak(document, 1);
        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.setFontSize(14);
        run.setBold(true);
        run.setText(this.getText("summaries.annualReportCRP2018.narrative"));
        paragraph.setStyle("heading 2");

        // section 1 - Key Results
        poiSummary.textLineBreak(document, 1);
        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.setFontSize(14);
        run.setBold(true);
        run.setText(this.getText("summaries.annualReport.keyResults"));
        paragraph.setStyle("heading 3");

        // 1.1 Progress Towards SDG and SLO
        poiSummary.textLineBreak(document, 1);
        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.setFontSize(13);
        run.setBold(true);
        run.setText(this.getText("summaries.annualReport2018.keyResults.crpProgress"));
        this.addExpectedCrp();
        paragraph.setStyle("heading 4");


        // 1.2 CRP progress towars outputs and outcomes
        poiSummary.textLineBreak(document, 1);
        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.setFontSize(14);
        run.setBold(true);
        run.setText(this.getText("summaries.annualReport2018.keyResults.progressTowards"));
        paragraph.setStyle("heading 5");

        // 1.2.1
        poiSummary.textLineBreak(document, 1);
        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.setFontSize(11);
        run.setBold(true);
        run.setText(this.getText("summaries.annualReport2018.keyResults.overall"));
        this.addOverallProgressCrp();
        paragraph.setStyle("heading 6");

        // 1.2.2
        poiSummary.textLineBreak(document, 1);
        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.setFontSize(11);
        run.setBold(true);
        run.setText(this.getText("summaries.annualReport2018.keyResults.progress"));
        this.addProgressFlagshipCrp();
        paragraph.setStyle("heading 7");

        // 1.2.3
        poiSummary.textLineBreak(document, 1);
        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.setFontSize(11);
        run.setBold(true);
        run.setText(this.getText("summaries.annualReport2018.keyResults.variance"));
        this.addVariancePlanned();
        paragraph.setStyle("heading 8");

        // 1.2.4
        poiSummary.textLineBreak(document, 1);
        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.setFontSize(11);
        run.setBold(true);
        run.setText(this.getText("summaries.annualReport2018.keyResults.altmetric"));
        this.addAlmetricCrp();
        paragraph.setStyle("heading 9");

        // 1.3 Cross cutting dimensions
        poiSummary.textLineBreak(document, 1);
        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.setFontSize(13);
        run.setBold(true);
        run.setText(this.getText("summaries.annualReport2018.crossCutting"));
        paragraph.setStyle("heading 10");

        // poiSummary.textLineBreak(document, 1);
        //
        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.setFontSize(11);
        run.setBold(true);
        run.setText(this.getText("summaries.annualReport2018.crossCutting.gender"));
        this.addCrossCuttingGender();
        paragraph.setStyle("heading 11");

        poiSummary.textLineBreak(document, 1);
        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.setFontSize(11);
        run.setBold(true);
        run.setText(this.getText("summaries.annualReport2018.crossCutting.youth"));
        paragraph.setStyle("heading 12");
        this.addCrossCuttingYouth();

        poiSummary.textLineBreak(document, 1);
        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.setFontSize(11);
        run.setBold(true);
        run.setText(this.getText("summaries.annualReport2018.crossCutting.capacity"));
        paragraph.setStyle("heading 13");
        this.addCrossCuttingCapacityDevelopment();

        poiSummary.textLineBreak(document, 1);
        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.setFontSize(11);
        run.setBold(true);
        run.setText(this.getText("summaries.annualReport2018.crossCutting.climateChange"));
        paragraph.setStyle("heading 14");
        this.addCrossCuttingClimateChange();

        // section 2 - Effectiveness and Efficiency
        poiSummary.textLineBreak(document, 1);
        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.setFontSize(14);
        run.setBold(true);
        run.setText(this.getText("summaries.annualReport2018.effectiveness"));
        paragraph.setStyle("heading 15");

        // 2.1 Management and governance
        poiSummary.textLineBreak(document, 1);
        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.setFontSize(13);
        run.setBold(true);
        run.setText(this.getText("summaries.annualReport2018.effectiveness.management"));
        paragraph.setStyle("heading 16");
        this.addManagementGovernance();

        // 2.2 Partnerships
        poiSummary.textLineBreak(document, 1);
        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.setFontSize(13);
        run.setBold(true);
        run.setText(this.getText("summaries.annualReport2018.effectiveness.partnerships"));
        paragraph.setStyle("heading 17");
        // this.addFundingSummarize();

        // 2.2.1 Highlights of External Partnerships
        poiSummary.textLineBreak(document, 1);
        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.setFontSize(11);
        run.setBold(true);
        run.setText(this.getText("summaries.annualReport2018.effectiveness.highlights"));
        this.addExternalPartnerships();
        paragraph.setStyle("heading 18");

        // 2.2.2
        poiSummary.textLineBreak(document, 1);
        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.setFontSize(11);
        run.setBold(true);
        run.setText(this.getText("summaries.annualReport2018.effectiveness.crossCgiar"));
        this.addCrossPartnerships();
        paragraph.setStyle("heading 19");

        // 2.3 Intellectual Assets
        poiSummary.textLineBreak(document, 1);
        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.setFontSize(13);
        run.setBold(true);
        run.setText(this.getText("summaries.annualReport2018.effectiveness.intellectual"));
        this.addIntellectualAssets();
        paragraph.setStyle("heading 20");

        // 2.4 Melia
        poiSummary.textLineBreak(document, 1);
        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.setFontSize(13);
        run.setBold(true);
        run.setText(this.getText("summaries.annualReport2018.effectiveness.monitoring"));
        this.addReportSynthesisMelia();
        paragraph.setStyle("heading 21");

        poiSummary.textLineBreak(document, 1);
        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.setFontSize(13);
        run.setBold(true);
        run.setText(this.getText("summaries.annualReport2018.effectiveness.efficiency"));
        paragraph.setStyle("heading 22");
        this.addImprovingEfficiency();

        poiSummary.textLineBreak(document, 1);
        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.setFontSize(13);
        run.setBold(true);
        run.setText(this.getText("summaries.annualReport2018.effectiveness.risk"));
        paragraph.setStyle("heading 23");
        this.addManagementRisks();

        poiSummary.textLineBreak(document, 1);
        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.setFontSize(13);
        run.setBold(true);
        run.setText(this.getText("summaries.annualReport2018.effectiveness.funding"));
        paragraph.setStyle("heading 24");
        this.addFundingSummarize();

        // section 3 - Financial summary
        poiSummary.textLineBreak(document, 1);
        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.setFontSize(14);
        run.setBold(true);
        run.setText(this.getText("summaries.annualReport2018.financial"));
        paragraph.setStyle("heading 25");
        this.addFinancialSummary();

        /* Create a landscape text Section */
        paragraph = document.createParagraph();
        CTSectPr sectionTable = body.getSectPr();
        CTPageSz pageSizeTable = sectionTable.addNewPgSz();
        CTP ctpTable = paragraph.getCTP();
        CTPPr brTable = ctpTable.addNewPPr();
        brTable.setSectPr(sectionTable);
        /* standard Letter page size */
        pageSizeTable.setOrient(STPageOrientation.LANDSCAPE);
        pageSizeTable.setW(BigInteger.valueOf(842 * 20));
        pageSizeTable.setH(BigInteger.valueOf(595 * 20));
        this.loadTablePMU();


        poiSummary.textLineBreak(document, 1);
        poiSummary.textHead1Title(document.createParagraph(), "Part B. TABLES");

        // Table 1
        poiSummary.textLineBreak(document, 1);
        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.setFontSize(13);
        run.setBold(true);
        run.setText(this.getText("summaries.annualReport2018.table1"));
        paragraph.setStyle("heading 2");
        this.createTable1();

        // Table 2
        poiSummary.textLineBreak(document, 1);
        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.setFontSize(13);
        run.setBold(true);
        run.setText(this.getText("summaries.annualReport2018.table2"));
        paragraph.setStyle("heading 2");
        this.createTable2();

        // Table 3
        poiSummary.textLineBreak(document, 1);
        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.setFontSize(13);
        run.setBold(true);
        run.setText(this.getText("summaries.annualReport2018.table3"));
        paragraph.setStyle("heading 2");
        this.createTable3();

        // Table 4
        poiSummary.textLineBreak(document, 1);
        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.setFontSize(13);
        run.setBold(true);
        run.setText(this.getText("summaries.annualReport2018.table4"));
        paragraph.setStyle("heading 2");
        this.createTable4();

        // Table 5
        poiSummary.textLineBreak(document, 1);
        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.setFontSize(13);
        run.setBold(true);
        run.setText(this.getText("summaries.annualReport2018.table5"));
        paragraph.setStyle("heading 2");
        this.createTable5();

        // Table 6
        poiSummary.textLineBreak(document, 1);
        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.setFontSize(13);
        run.setBold(true);
        run.setText(this.getText("summaries.annualReport2018.table6"));
        paragraph.setStyle("heading 2");
        this.createTable6();

        // Table 7
        poiSummary.textLineBreak(document, 1);
        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.setFontSize(13);
        run.setBold(true);
        run.setText(this.getText("summaries.annualReport2018.table7"));
        paragraph.setStyle("heading 2");
        this.createTable7();

        // Table 8
        poiSummary.textLineBreak(document, 1);
        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.setFontSize(13);
        run.setBold(true);
        run.setText(this.getText("summaries.annualReport2018.table8"));
        paragraph.setStyle("heading 2");
        this.createTable8();

        // Table 9
        poiSummary.textLineBreak(document, 1);
        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.setFontSize(13);
        run.setBold(true);
        run.setText(this.getText("summaries.annualReport2018.table9"));
        paragraph.setStyle("heading 2");
        this.createTable9();

        // Table 10
        poiSummary.textLineBreak(document, 1);
        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.setFontSize(13);
        run.setBold(true);
        run.setText(this.getText("summaries.annualReport2018.table10"));
        paragraph.setStyle("heading 2");
        this.createTable10();

        // Table 11
        poiSummary.textLineBreak(document, 1);
        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.setFontSize(13);
        run.setBold(true);
        run.setText(this.getText("summaries.annualReport2018.table11"));
        paragraph.setStyle("heading 2");
        this.createTable11();

        // Table 12
        poiSummary.textLineBreak(document, 1);
        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.setFontSize(13);
        run.setBold(true);
        run.setText(this.getText("summaries.annualReport2018.table12"));
        paragraph.setStyle("heading 2");
        this.createTable12();

        // Table 12
        poiSummary.textLineBreak(document, 1);
        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.setFontSize(13);
        run.setBold(true);
        run.setText(this.getText("summaries.annualReport2018.table12"));
        paragraph.setStyle("heading 2");
        this.createTable12();

        // Table 13
        poiSummary.textLineBreak(document, 1);
        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.setFontSize(13);
        run.setBold(true);
        run.setText(this.getText("summaries.annualReport2018.table13"));
        paragraph.setStyle("heading 2");
        this.createTable13();

        // Table c
        poiSummary.textLineBreak(document, 1);
        poiSummary.textHead2Title(document.createParagraph(), this.getText("annualReport.ccDimensions.tableCTitle"));
        this.createTableC();


        ByteArrayOutputStream os = new ByteArrayOutputStream();
        document.write(os);
        bytesDOC = os.toByteArray();
        os.close();
        document.close();
      } catch (Exception e) {
        LOG.error("Error generating " + this.getFileName() + ". Exception: " + e.getMessage());
        throw e;
      }
    }

    if (this.isEntityPlatform()) {
      try {

        CTDocument1 doc = document.getDocument();
        CTBody body = doc.getBody();
        poiSummary.pageHeader(document, this.getText("summaries.annualReportPlatform2018.header"));

        // Get datetime
        ZonedDateTime timezone = ZonedDateTime.now();
        String zone = timezone.getOffset() + "";
        if (zone.equals("Z")) {
          zone = "+0";
        }

        this.createPageFooter();
        // poiSummary.pageFooter(document, "This report was generated on " + currentDate);

        // Cover
        poiSummary.textLineBreak(document, 6);
        poiSummary.textHeadCoverTitleAR2018(document.createParagraph(),
          this.getText("summaries.annualReportPlatform2018.mainTitle"));
        document.createParagraph().setPageBreak(true);

        // Table of contents
        document.createTOC();

        // the body content
        XWPFParagraph paragraph = document.createParagraph();
        CTP ctP = paragraph.getCTP();
        CTSimpleField toc = ctP.addNewFldSimple();
        toc.setInstr("TOC \\h");
        toc.setDirty(STOnOff.TRUE);
        XWPFRun run = paragraph.createRun();

        // Toc section
        addCustomHeadingStyle(document, "heading 1", 1);
        addCustomHeadingStyle(document, "heading 2", 2);

        // First page
        document.createParagraph().setPageBreak(true);
        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.setFontSize(14);
        run.setBold(true);
        run.setText(this.getText("summaries.annualReportPlatform2018.cover"));
        paragraph.setStyle("heading 2");
        String unitName = this.getLoggedCrp().getAcronym() != null && !this.getLoggedCrp().getAcronym().isEmpty()
          ? this.getLoggedCrp().getAcronym() : this.getLoggedCrp().getName();
        poiSummary.textParagraph(document.createParagraph(),
          this.getText("summaries.annualReportPlatform2018.unitName") + ": " + unitName);
        run.setText(this.getText("summaries.annualReport.LeadCenter") + ":");
        poiSummary.textLineBreak(document, 1);
        this.addParticipatingCenters();

        // section 1 - key results
        poiSummary.textHead1Title(document.createParagraph(), this.getText("summaries.annualReport.keyResults"));
        poiSummary.textHead2Title(document.createParagraph(),
          this.getText("summaries.annualReport.keyResults.crpProgress"));
        this.addExpectedCrp();
        this.addCrpProgressOutcomes();
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
        poiSummary.textHead2Title(document.createParagraph(),
          this.getText("summaries.annualReport.effectiveness.cross"));
        this.addCrossPartnerships();
        poiSummary.textHead2Title(document.createParagraph(), this.getText("summaries.annualReport.effectiveness.mel"));
        this.addReportSynthesisMelia();
        poiSummary.textHead2Title(document.createParagraph(),
          this.getText("summaries.annualReport.effectiveness.efficiency"));
        this.addImprovingEfficiency();

        // section 3 - crp management
        poiSummary.textLineBreak(document, 1);
        poiSummary.textHead1Title(document.createParagraph(), this.getText("summaries.annualReport.management"));
        // this.addManagement();

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
        poiSummary.textHead1Title(document.createParagraph(), "TABLES");
        poiSummary.textHead2Title(document.createParagraph(), this.getText("summaries.annualReport.tableA.title"));
        poiSummary.textHead3Title(document.createParagraph(), this.getText("summaries.annualReport.tableA1.title"));
        this.createTable1();

        // Table a2
        poiSummary.textLineBreak(document, 1);
        poiSummary.textHead3Title(document.createParagraph(), this.getText("summaries.annualReport.tableA2.title"));
        this.createTableA2();


        // Table c
        poiSummary.textLineBreak(document, 1);
        poiSummary.textHead2Title(document.createParagraph(), this.getText("annualReport.ccDimensions.tableCTitle"));
        this.createTableC();

        // Table d
        poiSummary.textLineBreak(document, 1);
        poiSummary.textHead2Title(document.createParagraph(), this.getText("summaries.annualReport.tableD.title"));


        ByteArrayOutputStream os = new ByteArrayOutputStream();
        document.write(os);
        bytesDOC = os.toByteArray();
        os.close();
        document.close();
      } catch (Exception e) {
        LOG.error("Error generating " + this.getFileName() + ". Exception: " + e.getMessage());
        throw e;
      }
    }

    // Calculate time of generation
    long stopTime = System.currentTimeMillis();
    stopTime = stopTime - startTime;
    LOG.info("Downloaded successfully: " + this.getFileName() + ". User: "
      + this.getCurrentUser().getComposedCompleteName() + ". CRP: " + this.getLoggedCrp().getAcronym() + ". Cycle: "
      + this.getSelectedCycle() + ". Time to generate: " + stopTime + "ms.");
    return SUCCESS;
  }
  /*
   * private void fillProjectStudiesList(LiaisonInstitution liaisonInstitution) {
   * projectExpectedStudies = new ArrayList<>();
   * // this.isFlagship()
   * if (false) {
   * // Fill Project Expected Studies of the current flagship
   * if (projectFocusManager.findAll() != null) {
   * List<ProjectFocus> projectFocus = new ArrayList<>(projectFocusManager.findAll().stream()
   * .filter(pf -> pf.isActive() && pf.getCrpProgram().getId() == liaisonInstitution.getCrpProgram().getId()
   * && pf.getPhase() != null && pf.getPhase().getId() == this.getActualPhase().getId())
   * .collect(Collectors.toList()));
   * for (ProjectFocus focus : projectFocus) {
   * Project project = projectManager.getProjectById(focus.getProject().getId());
   * List<ProjectExpectedStudy> plannedProjectExpectedStudies =
   * new ArrayList<>(project.getProjectExpectedStudies().stream()
   * .filter(ps -> ps.isActive() && ps.getProjectExpectedStudyInfo(this.getActualPhase()) != null
   * && ps.getProjectExpectedStudyInfo().getStudyType() != null
   * && ps.getProjectExpectedStudyInfo().getStudyType().getId() == 1
   * && ps.getProjectExpectedStudyInfo().getYear() != null
   * && ps.getProjectExpectedStudyInfo().getYear() == this.getActualPhase().getYear())
   * .collect(Collectors.toList()));
   * for (ProjectExpectedStudy projectExpectedStudy : plannedProjectExpectedStudies) {
   * projectExpectedStudy.getProjectExpectedStudyInfo(this.getActualPhase());
   * projectExpectedStudy.setSrfTargets(projectExpectedStudy.getSrfTargets(this.getActualPhase()));
   * projectExpectedStudy.setSubIdos(projectExpectedStudy.getSubIdos(this.getActualPhase()));
   * projectExpectedStudies.add(projectExpectedStudy);
   * }
   * }
   * }
   * } else {
   * // Fill Project Expected Studies of the PMU, removing flagship deletions
   * List<LiaisonInstitution> liaisonInstitutions = new ArrayList<>();
   * liaisonInstitutions = this.getLoggedCrp().getLiaisonInstitutions().stream()
   * .filter(c -> c.getCrpProgram() != null && c.isActive()
   * && c.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue())
   * .collect(Collectors.toList());
   * liaisonInstitutions.sort(Comparator.comparing(LiaisonInstitution::getAcronym));
   * // List<ReportSynthesisFlagshipProgressStudyDTO> flagshipPlannedList =
   * // this.fillFpPlannedList(liaisonInstitutions);
   * for (ReportSynthesisFlagshipProgressStudyDTO reportSynthesisFlagshipProgressStudyDTO : flagshipPlannedList) {
   * ProjectExpectedStudy projectExpectedStudy = reportSynthesisFlagshipProgressStudyDTO.getProjectExpectedStudy();
   * projectExpectedStudy.getProjectExpectedStudyInfo(this.getActualPhase());
   * projectExpectedStudy.setSrfTargets(projectExpectedStudy.getSrfTargets(this.getActualPhase()));
   * projectExpectedStudy.setSubIdos(projectExpectedStudy.getSubIdos(this.getActualPhase()));
   * projectExpectedStudy.setSelectedFlahsgips(new ArrayList<>());
   * // sort selected flagships
   * if (reportSynthesisFlagshipProgressStudyDTO.getLiaisonInstitutions() != null
   * && !reportSynthesisFlagshipProgressStudyDTO.getLiaisonInstitutions().isEmpty()) {
   * reportSynthesisFlagshipProgressStudyDTO.getLiaisonInstitutions()
   * .sort((l1, l2) -> l1.getCrpProgram().getAcronym().compareTo(l2.getCrpProgram().getAcronym()));
   * }
   * projectExpectedStudy.getSelectedFlahsgips()
   * .addAll(reportSynthesisFlagshipProgressStudyDTO.getLiaisonInstitutions());
   * projectExpectedStudies.add(projectExpectedStudy);
   * }
   * }
   * }
   */

  public List<ReportSynthesisFlagshipProgressStudyDTO>
    fillFpPlannedList(List<LiaisonInstitution> flagshipsLiaisonInstitutions) {
    List<ReportSynthesisFlagshipProgressStudyDTO> flagshipPlannedList = new ArrayList<>();

    if (projectExpectedStudyManager.findAll() != null) {

      // Get global unit studies
      List<ProjectExpectedStudy> projectExpectedStudies = new ArrayList<>(projectExpectedStudyManager.findAll().stream()
        .filter(ps -> ps.isActive() && ps.getProjectExpectedStudyInfo(this.getActualPhase()) != null
          && ps.getProjectExpectedStudyInfo().getStudyType() != null
          && ps.getProjectExpectedStudyInfo().getStudyType().getId() == 1
          && ps.getProjectExpectedStudyInfo().getYear() != null
          && ps.getProjectExpectedStudyInfo().getYear() == this.getActualPhase().getYear() && ps.getProject() != null
          && ps.getProject().getGlobalUnitProjects().stream().filter(
            gup -> gup.isActive() && gup.isOrigin() && gup.getGlobalUnit().getId().equals(this.getLoggedCrp().getId()))
            .collect(Collectors.toList()).size() > 0)
        .collect(Collectors.toList()));

      // Fill all project studies of the global unit
      for (ProjectExpectedStudy projectExpectedStudy : projectExpectedStudies) {
        ReportSynthesisFlagshipProgressStudyDTO dto = new ReportSynthesisFlagshipProgressStudyDTO();
        projectExpectedStudy.getProject()
          .setProjectInfo(projectExpectedStudy.getProject().getProjecInfoPhase(this.getActualPhase()));
        dto.setProjectExpectedStudy(projectExpectedStudy);
        if (projectExpectedStudy.getProject().getProjectInfo().getAdministrative() != null
          && projectExpectedStudy.getProject().getProjectInfo().getAdministrative()) {
          dto.setLiaisonInstitutions(new ArrayList<>());
          dto.getLiaisonInstitutions().add(this.liaisonInstitution);
        } else {
          List<ProjectFocus> projectFocuses = new ArrayList<>(projectExpectedStudy.getProject().getProjectFocuses()
            .stream().filter(pf -> pf.isActive() && pf.getPhase().getId() == this.getActualPhase().getId())
            .collect(Collectors.toList()));
          List<LiaisonInstitution> liaisonInstitutions = new ArrayList<>();
          for (ProjectFocus projectFocus : projectFocuses) {
            liaisonInstitutions.addAll(projectFocus.getCrpProgram().getLiaisonInstitutions().stream()
              .filter(li -> li.isActive() && li.getCrpProgram() != null
                && li.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue()
                && li.getCrp() != null && li.getCrp().equals(this.getLoggedCrp()))
              .collect(Collectors.toList()));
          }
          dto.setLiaisonInstitutions(liaisonInstitutions);
        }

        flagshipPlannedList.add(dto);
      }

      // Get supplementary studies
      List<ProjectExpectedStudy> projectSupplementaryStudies =
        new ArrayList<>(projectExpectedStudyManager.findAll().stream()
          .filter(ps -> ps.isActive() && ps.getProjectExpectedStudyInfo(this.getActualPhase()) != null
            && ps.getProject() == null && ps.getProjectExpectedStudyInfo().getStudyType() != null
            && ps.getProjectExpectedStudyInfo().getStudyType().getId() == 1
            && ps.getProjectExpectedStudyInfo().getYear() != null
            && ps.getProjectExpectedStudyInfo().getYear() == this.getActualPhase().getYear())
          .collect(Collectors.toList()));

      // Fill all supplementary studies
      for (ProjectExpectedStudy projectExpectedStudy : projectSupplementaryStudies) {
        ReportSynthesisFlagshipProgressStudyDTO dto = new ReportSynthesisFlagshipProgressStudyDTO();
        dto.setProjectExpectedStudy(projectExpectedStudy);
        dto.setLiaisonInstitutions(new ArrayList<>());
        dto.getLiaisonInstitutions().add(this.liaisonInstitution);
        flagshipPlannedList.add(dto);
      }

      // Get deleted studies
      List<ReportSynthesisFlagshipProgressStudy> flagshipProgressStudies = new ArrayList<>();
      for (LiaisonInstitution liaisonInstitution : flagshipsLiaisonInstitutions) {
        ReportSynthesis reportSynthesis =
          reportSynthesisManager.findSynthesis(this.getActualPhase().getId(), liaisonInstitution.getId());
        if (reportSynthesis != null) {
          if (reportSynthesis.getReportSynthesisFlagshipProgress() != null) {
            if (reportSynthesis.getReportSynthesisFlagshipProgress()
              .getReportSynthesisFlagshipProgressStudies() != null) {
              List<ReportSynthesisFlagshipProgressStudy> studies = new ArrayList<>(
                reportSynthesis.getReportSynthesisFlagshipProgress().getReportSynthesisFlagshipProgressStudies()
                  .stream().filter(s -> s.isActive()).collect(Collectors.toList()));
              if (studies != null || !studies.isEmpty()) {
                for (ReportSynthesisFlagshipProgressStudy reportSynthesisFlagshipProgressStudy : studies) {
                  flagshipProgressStudies.add(reportSynthesisFlagshipProgressStudy);
                }
              }
            }
          }
        }
      }

      // Get list of studies to remove
      List<ReportSynthesisFlagshipProgressStudyDTO> removeList = new ArrayList<>();
      for (ReportSynthesisFlagshipProgressStudyDTO dto : flagshipPlannedList) {

        List<LiaisonInstitution> removeLiaison = new ArrayList<>();
        for (LiaisonInstitution liaisonInstitution : dto.getLiaisonInstitutions()) {
          ReportSynthesis reportSynthesis =
            reportSynthesisManager.findSynthesis(this.getActualPhase().getId(), liaisonInstitution.getId());
          if (reportSynthesis != null) {
            if (reportSynthesis.getReportSynthesisFlagshipProgress() != null) {

              ReportSynthesisFlagshipProgressStudy flagshipProgressStudyNew =
                new ReportSynthesisFlagshipProgressStudy();
              flagshipProgressStudyNew = new ReportSynthesisFlagshipProgressStudy();
              flagshipProgressStudyNew.setProjectExpectedStudy(dto.getProjectExpectedStudy());
              flagshipProgressStudyNew
                .setReportSynthesisFlagshipProgress(reportSynthesis.getReportSynthesisFlagshipProgress());

              if (flagshipProgressStudies.contains(flagshipProgressStudyNew)) {
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

      // Remove studies unselected by flagships
      for (ReportSynthesisFlagshipProgressStudyDTO i : removeList) {
        flagshipPlannedList.remove(i);
      }

    }
    return flagshipPlannedList;
  }


  public void flagshipExternalPartnerships(List<LiaisonInstitution> flagshipliaisonInstitutions) {

    flagshipExternalPartnerships = new ArrayList<>();

    for (LiaisonInstitution liaisonInstitution : flagshipliaisonInstitutions) {


      ReportSynthesis reportSynthesisFP =
        reportSynthesisManager.findSynthesis(this.getActualPhase().getId(), liaisonInstitution.getId());

      if (reportSynthesisFP != null) {
        if (reportSynthesisFP.getReportSynthesisKeyPartnership() != null) {
          if (reportSynthesisFP.getReportSynthesisKeyPartnership().getReportSynthesisKeyPartnershipExternals() != null
            && !reportSynthesisFP.getReportSynthesisKeyPartnership().getReportSynthesisKeyPartnershipExternals()
              .isEmpty()) {


            List<ReportSynthesisKeyPartnershipExternal> externals = new ArrayList<>(
              reportSynthesisFP.getReportSynthesisKeyPartnership().getReportSynthesisKeyPartnershipExternals().stream()
                .filter(c -> c.isActive()).collect(Collectors.toList()));

            for (ReportSynthesisKeyPartnershipExternal external : externals) {

              if (external.getReportSynthesisKeyPartnershipExternalInstitutions() != null
                && !external.getReportSynthesisKeyPartnershipExternalInstitutions().isEmpty()) {
                external.setInstitutions(new ArrayList<>(external.getReportSynthesisKeyPartnershipExternalInstitutions()
                  .stream().filter(c -> c.isActive()).collect(Collectors.toList())));
              }

              if (external.getReportSynthesisKeyPartnershipExternalMainAreas() != null
                && !external.getReportSynthesisKeyPartnershipExternalMainAreas().isEmpty()) {
                external.setMainAreas(new ArrayList<>(external.getReportSynthesisKeyPartnershipExternalMainAreas()
                  .stream().filter(c -> c.isActive()).collect(Collectors.toList())));
              }

              flagshipExternalPartnerships.add(external);

            }
          }
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


  @Override
  public String getFileName() {
    StringBuffer fileName = new StringBuffer();
    String crp = this.getLoggedCrp().getAcronym();
    Date date = new Date();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HHmm");
    fileName.append(this.getSelectedYear() + "_");
    fileName.append(crp + "-");
    fileName.append("AR_");
    fileName.append(sdf.format(date));
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

  @Override
  public InputStream getInputStream() {
    if (inputStream == null) {
      inputStream = new ByteArrayInputStream(bytesDOC);
    }
    return inputStream;
  }

  public ReportSynthesisFlagshipProgressOutcome getOutcomeToPmu(Long fpId, long outcomeID) {
    ReportSynthesisFlagshipProgressOutcome outcome = new ReportSynthesisFlagshipProgressOutcome();


    CrpProgramOutcome crpProgramOutcome = crpProgramOutcomeManager.getCrpProgramOutcomeById(outcomeID);

    LiaisonInstitution inst = crpProgramOutcome.getCrpProgram().getLiaisonInstitutions().stream()
      .filter(c -> c.isActive() && c.getCrpProgram().getId().equals(fpId)).collect(Collectors.toList()).get(0);


    // ReportSynthesisSrfProgress crpProgress = new ReportSynthesisSrfProgress();
    ReportSynthesis reportSynthesisFP =
      reportSynthesisManager.findSynthesis(this.getActualPhase().getId(), inst.getId());


    ReportSynthesisFlagshipProgress flagshipProgress = new ReportSynthesisFlagshipProgress();
    if (reportSynthesisFP != null) {
      if (reportSynthesisFP.getReportSynthesisFlagshipProgress() != null) {
        flagshipProgress = reportSynthesisFP.getReportSynthesisFlagshipProgress();

        outcome = reportSynthesisFlagshipProgressOutcomeManager.getOutcomeId(flagshipProgress.getId(), outcomeID);

        if (outcome != null) {
          return outcome;
        } else {
          return null;
        }
      }
    }

    return null;

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
            && d.getDeliverableInfo().isActive()
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


  private void getProjectExpectedStudies() {
    // Table 3
    if (projectExpectedStudyManager.findAll() != null) {
      // Get global unit studies
      projectExpectedStudies = new ArrayList<>(projectExpectedStudyManager.findAll().stream()
        .filter(ps -> ps.isActive() && ps.getProjectExpectedStudyInfo(this.getActualPhase()) != null
          && ps.getProjectExpectedStudyInfo().getStudyType() != null
          && ps.getProjectExpectedStudyInfo().getStudyType().getId() == 1
          && ps.getProjectExpectedStudyInfo().getYear() != null
          && ps.getProjectExpectedStudyInfo().getYear() == this.getActualPhase().getYear() && ps.getProject() != null
          && ps.getProject().getGlobalUnitProjects().stream().filter(
            gup -> gup.isActive() && gup.isOrigin() && gup.getGlobalUnit().getId().equals(this.getLoggedCrp().getId()))
            .collect(Collectors.toList()).size() > 0)
        .collect(Collectors.toList()));

    }
  }

  public void getProjectPolicies() {
    projectPolicies = new ArrayList<>();
    projectPolicies =
      (projectPolicyManager.findAll().stream()
        .filter(ps -> ps.isActive() && ps.getProjectPolicyInfo(this.getActualPhase()) != null
          && ps.getProjectPolicyInfo().isRequired() && ps.getProject() != null
          && ps.getProject().getGlobalUnitProjects().stream().filter(
            gup -> gup.isActive() && gup.isOrigin() && gup.getGlobalUnit().getId().equals(this.getCurrentCrp().getId()))
            .collect(Collectors.toList()).size() > 0)
        .collect(Collectors.toList()));
  }

  public void getProjectsInnovations() {
    if (projectInnovationManager.findAll() != null) {

      // Get global unit Innovations
      projectInnovations = new ArrayList<>(projectInnovationManager.findAll().stream()
        .filter(ps -> ps.isActive() && ps.getProjectInnovationInfo(this.getActualPhase()) != null
          && ps.getProjectInnovationInfo().getYear() != null
          && ps.getProjectInnovationInfo().getYear() == this.getActualPhase().getYear() && ps.getProject() != null
          && ps.getProject().getGlobalUnitProjects().stream().filter(
            gup -> gup.isActive() && gup.isOrigin() && gup.getGlobalUnit().getId().equals(this.getLoggedCrp().getId()))
            .collect(Collectors.toList()).size() > 0)
        .collect(Collectors.toList()));

      // Fill all project Innovations of the global unit
      for (ProjectInnovation projectInnovation : projectInnovations) {
        ReportSynthesisFlagshipProgressInnovationDTO dto = new ReportSynthesisFlagshipProgressInnovationDTO();
        projectInnovation.getProject()
          .setProjectInfo(projectInnovation.getProject().getProjecInfoPhase(this.getActualPhase()));
        dto.setProjectInnovation(projectInnovation);
        if (projectInnovation.getProject().getProjectInfo().getAdministrative() != null
          && projectInnovation.getProject().getProjectInfo().getAdministrative()) {
          dto.setLiaisonInstitutions(new ArrayList<>());
          dto.getLiaisonInstitutions().add(this.liaisonInstitution);
        } else {
          List<ProjectFocus> projectFocuses = new ArrayList<>(projectInnovation.getProject().getProjectFocuses()
            .stream().filter(pf -> pf.isActive() && pf.getPhase().getId() == this.getActualPhase().getId())
            .collect(Collectors.toList()));
          List<LiaisonInstitution> liaisonInstitutions = new ArrayList<>();
          for (ProjectFocus projectFocus : projectFocuses) {
            liaisonInstitutions.addAll(projectFocus.getCrpProgram().getLiaisonInstitutions().stream()
              .filter(li -> li.isActive() && li.getCrpProgram() != null
                && li.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue()
                && li.getCrp() != null && li.getCrp().equals(this.getLoggedCrp()))
              .collect(Collectors.toList()));
          }
          dto.setLiaisonInstitutions(liaisonInstitutions);
        }

        // flagshipPlannedList.add(dto);
      }

    }
  }

  public ReportSynthesisFlagshipProgressMilestone getReportSynthesisFlagshipProgressProgram(Long crpMilestoneID,
    Long crpProgramID) {
    List<ReportSynthesisFlagshipProgressMilestone> flagshipProgressMilestonesPrev =
      reportSynthesisFlagshipProgressMilestoneManager.findByProgram(crpProgramID);
    List<ReportSynthesisFlagshipProgressMilestone> flagshipProgressMilestones = flagshipProgressMilestonesPrev.stream()
      .filter(c -> c.getCrpMilestone().getId().longValue() == crpMilestoneID.longValue() && c.isActive())
      .collect(Collectors.toList());
    if (!flagshipProgressMilestones.isEmpty()) {
      return flagshipProgressMilestones.get(0);
    }
    return new ReportSynthesisFlagshipProgressMilestone();
  }

  private void getTable6Info() {
    /** Graphs and Tables */

    List<Deliverable> deliverables = new ArrayList<>(deliverableManager.findAll().stream()
      .filter(d -> d.isActive() && d.getDeliverableInfo(this.getActualPhase()) != null
        && d.getDeliverableInfo().isRequired() && d.getDeliverableInfo().getDeliverableType() != null
        && d.getDeliverableInfo().getDeliverableType().getId() == 63)
      .collect(Collectors.toList()));

    List<Deliverable> selectedDeliverables = new ArrayList<Deliverable>();
    if (deliverables != null && !deliverables.isEmpty()) {
      deliverables.sort((p1, p2) -> p1.getId().compareTo(p2.getId()));
      selectedDeliverables.addAll(deliverables);
      // Remove unchecked deliverables
      if (reportSynthesisPMU.getReportSynthesisFlagshipProgress() != null) {
        if (reportSynthesisPMU.getReportSynthesisFlagshipProgress().getDeliverables() != null
          && !reportSynthesisPMU.getReportSynthesisFlagshipProgress().getDeliverables().isEmpty()) {
          for (Deliverable deliverable : reportSynthesisPMU.getReportSynthesisFlagshipProgress().getDeliverables()) {
            selectedDeliverables.remove(deliverable);
          }
        }
      }
      total = selectedDeliverables.size();

      if (selectedDeliverables != null && !selectedDeliverables.isEmpty()) {
        if (selectedDeliverables != null && !selectedDeliverables.isEmpty()) {
          for (Deliverable deliverable : selectedDeliverables) {

            // Chart: Deliverables open access
            List<DeliverableDissemination> deliverableDisseminations = deliverable
              .getDeliverableInfo(this.getActualPhase()).getDeliverable().getDeliverableDisseminations().stream()
              .filter(dd -> dd.isActive() && dd.getPhase() != null && dd.getPhase().equals(this.getActualPhase()))
              .collect(Collectors.toList());
            if (deliverableDisseminations != null && !deliverableDisseminations.isEmpty()) {
              deliverable.getDeliverableInfo(this.getActualPhase()).getDeliverable()
                .setDissemination(deliverableDisseminations.get(0));
              if (deliverable.getDeliverableInfo(this.getActualPhase()).getDeliverable().getDissemination()
                .getIsOpenAccess() != null) {
                // Journal Articles by Open Access
                if (deliverable.getDeliverableInfo(this.getActualPhase()).getDeliverable().getDissemination()
                  .getIsOpenAccess()) {
                  totalOpenAccess++;
                } else {
                  totalLimited++;
                }
              } else {
                totalLimited++;
              }
            } else {
              totalLimited++;
            }

            // Chart: Deliverables by ISI
            List<DeliverablePublicationMetadata> deliverablePublicationMetadatas = deliverable
              .getDeliverableInfo(this.getActualPhase()).getDeliverable().getDeliverablePublicationMetadatas().stream()
              .filter(dp -> dp.isActive() && dp.getPhase() != null && dp.getPhase().equals(this.getActualPhase()))
              .collect(Collectors.toList());
            if (deliverablePublicationMetadatas != null && !deliverablePublicationMetadatas.isEmpty()) {
              deliverable.getDeliverableInfo(this.getActualPhase()).getDeliverable()
                .setPublication(deliverablePublicationMetadatas.get(0));
              // Journal Articles by ISI status
              if (deliverable.getDeliverableInfo(this.getActualPhase()).getDeliverable().getPublication()
                .getIsiPublication() != null) {
                if (deliverable.getDeliverableInfo(this.getActualPhase()).getDeliverable().getPublication()
                  .getIsiPublication()) {
                  totalIsis++;
                } else {
                  totalNoIsis++;
                }
              } else {
                totalNoIsis++;
              }
            } else {
              totalNoIsis++;
            }
          }
        }
      }
    }
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

    pmuInstitution = this.getLoggedCrp().getLiaisonInstitutions().stream()
      .filter(c -> c.getCrpProgram() == null && c.getAcronym().equals("PMU")).collect(Collectors.toList()).get(0);

    reportSynthesisPMU = reportSynthesisManager.findSynthesis(this.getSelectedPhase().getId(), pmuInstitution.getId());
    this.getProjectPolicies();
    this.getProjectExpectedStudies();
    this.getProjectsInnovations();
    this.getTable6Info();

    // Get the list of liaison institutions Flagships.
    flagshipLiaisonInstitutions = this.getLoggedCrp().getLiaisonInstitutions().stream()
      .filter(c -> c.getCrpProgram() != null && c.isActive()
        && c.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue())
      .collect(Collectors.toList());
    flagshipLiaisonInstitutions.sort(Comparator.comparing(LiaisonInstitution::getAcronym));


    this.flagshipExternalPartnerships(flagshipLiaisonInstitutions);


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
