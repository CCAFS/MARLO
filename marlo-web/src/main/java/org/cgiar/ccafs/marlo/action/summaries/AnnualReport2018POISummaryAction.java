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
import org.cgiar.ccafs.marlo.data.manager.FileDBManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.PowbExpenditureAreasManager;
import org.cgiar.ccafs.marlo.data.manager.ProgressTargetCaseGeographicCountryManager;
import org.cgiar.ccafs.marlo.data.manager.ProgressTargetCaseGeographicRegionManager;
import org.cgiar.ccafs.marlo.data.manager.ProgressTargetCaseGeographicScopeManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPolicyManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisCrpProgressTargetManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisFlagshipProgressManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisFlagshipProgressOutcomeManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisFlagshipProgressOutcomeMilestoneManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisKeyPartnershipCollaborationManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisKeyPartnershipExternalManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisMeliaManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisSrfProgressTargetCasesManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisSrfProgressTargetContributionManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisSrfProgressTargetManager;
import org.cgiar.ccafs.marlo.data.manager.SrfSloIndicatorTargetManager;
import org.cgiar.ccafs.marlo.data.model.CrpMilestone;
import org.cgiar.ccafs.marlo.data.model.CrpOutcomeSubIdo;
import org.cgiar.ccafs.marlo.data.model.CrpPpaPartner;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.CrpProgramOutcome;
import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.data.model.DeliverableDissemination;
import org.cgiar.ccafs.marlo.data.model.DeliverablePublicationMetadata;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.PowbExpenditureAreas;
import org.cgiar.ccafs.marlo.data.model.ProgramType;
import org.cgiar.ccafs.marlo.data.model.ProgressTargetCaseGeographicCountry;
import org.cgiar.ccafs.marlo.data.model.ProgressTargetCaseGeographicRegion;
import org.cgiar.ccafs.marlo.data.model.ProgressTargetCaseGeographicScope;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectBudgetsFlagship;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudy;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyPolicy;
import org.cgiar.ccafs.marlo.data.model.ProjectFocus;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovation;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationCountry;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationGeographicScope;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationRegion;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicy;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicyCrossCuttingMarker;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicySubIdo;
import org.cgiar.ccafs.marlo.data.model.ProjectStatusEnum;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesis;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisCrossCuttingDimension;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisCrpFinancialReport;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFinancialSummary;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFinancialSummaryBudget;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgress;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgressDeliverable;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgressInnovation;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgressOutcome;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgressOutcomeMilestone;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgressOutcomeMilestoneLink;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgressPolicy;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgressStudy;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFundingUseExpendituryArea;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFundingUseSummary;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisGovernance;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisKeyPartnershipCollaboration;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisKeyPartnershipCollaborationCrp;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisKeyPartnershipExternal;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisKeyPartnershipExternalInstitution;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisKeyPartnershipExternalMainArea;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisMeliaEvaluation;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisMeliaEvaluationAction;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisRisk;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisSrfProgressTargetCases;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisSrfProgressTargetContribution;
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
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
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

    BigDecimal bd = new BigDecimal(String.valueOf(value));
    bd = bd.setScale(places, RoundingMode.HALF_UP);
    return bd.doubleValue();
  }

  private String notRequiredAR2021 = "According to the AR template for 2021, this field is no longer required";

  // Managers
  private PowbExpenditureAreasManager powbExpenditureAreasManager;
  private ReportSynthesisManager reportSynthesisManager;
  private ReportSynthesisFlagshipProgressManager reportSynthesisFlagshipProgressManager;
  private ReportSynthesisFlagshipProgressOutcomeMilestoneManager reportSynthesisFlagshipProgressOutcomeMilestoneManager;
  private ProjectPolicyManager projectPolicyManager;
  private ProjectExpectedStudyManager projectExpectedStudyManager;
  private ProjectInnovationManager projectInnovationManager;
  private CrpProgramOutcomeManager crpProgramOutcomeManager;
  private ReportSynthesisFlagshipProgressOutcomeManager reportSynthesisFlagshipProgressOutcomeManager;
  private ReportSynthesisKeyPartnershipExternalManager reportSynthesisKeyPartnershipExternalManager;
  private ReportSynthesisKeyPartnershipCollaborationManager reportSynthesisKeyPartnershipCollaborationManager;
  private ReportSynthesisMeliaManager reportSynthesisMeliaManager;
  private FileDBManager fileDBManager;
  private SrfSloIndicatorTargetManager srfSloIndicatorTargetManager;
  private ProgressTargetCaseGeographicCountryManager progressTargetCaseGeographicCountryManager;
  private ReportSynthesisSrfProgressTargetCasesManager reportSynthesisSrfProgressTargetCasesManager;
  private ReportSynthesisSrfProgressTargetContributionManager reportSynthesisSrfProgressTargetContributionManager;
  private ProgressTargetCaseGeographicScopeManager progressTargetCaseGeographicScopeManager;
  private ProgressTargetCaseGeographicRegionManager progressTargetCaseGeographicRegionManager;
  private CrpProgramManager crpProgramManager;


  private DeliverableManager deliverableManager;
  private List<ReportSynthesisKeyPartnershipExternal> flagshipExternalPartnerships;
  private List<ReportSynthesisKeyPartnershipExternal> externalPartnerships;
  private List<ReportSynthesisKeyPartnershipCollaboration> collaborations;
  private List<ProjectExpectedStudy> meliaDto;
  // Parameters
  private POISummary poiSummary;
  private LiaisonInstitution pmuInstitution;
  private ReportSynthesis reportSynthesisPMU;
  private long startTime;
  private XWPFDocument document;
  private List<CrpProgram> flagships;
  private List<LiaisonInstitution> flagshipLiaisonInstitutions;
  private LinkedHashSet<ProjectPolicy> projectPoliciesTable2;
  private LinkedHashSet<ProjectExpectedStudy> projectExpectedStudiesTable3;
  private LinkedHashSet<ProjectInnovation> projectInnovationsTable4;

  private List<ReportSynthesisFlagshipProgress> flagshipsReportSynthesisFlagshipProgress;
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
    ReportSynthesisCrpProgressTargetManager reportSynthesisCrpProgressTargetManager, ProjectManager projectManager,
    ProjectPolicyManager projectPolicyManager,
    ReportSynthesisFlagshipProgressManager reportSynthesisFlagshipProgressManager,
    ProjectInnovationManager projectInnovationManager,
    ReportSynthesisFlagshipProgressOutcomeMilestoneManager reportSynthesisFlagshipProgressOutcomeMilestoneManager,
    ReportSynthesisFlagshipProgressOutcomeManager reportSynthesisFlagshipProgressOutcomeManager,
    ReportSynthesisSrfProgressTargetManager reportSynthesisSrfProgressTargetManager,
    ProjectExpectedStudyManager projectExpectedStudyManager, CrpProgramOutcomeManager crpProgramOutcomeManager,
    DeliverableManager deliverableManager,
    ReportSynthesisKeyPartnershipExternalManager reportSynthesisKeyPartnershipExternalManager,
    ReportSynthesisKeyPartnershipCollaborationManager reportSynthesisKeyPartnershipCollaborationManager,
    ReportSynthesisMeliaManager reportSynthesisMeliaManager, FileDBManager fileDBManager,
    SrfSloIndicatorTargetManager srfSloIndicatorTargetManager,
    ProgressTargetCaseGeographicCountryManager progressTargetCaseGeographicCountryManager,
    ReportSynthesisSrfProgressTargetCasesManager reportSynthesisSrfProgressTargetCasesManager,
    ReportSynthesisSrfProgressTargetContributionManager reportSynthesisSrfProgressTargetContributionManager,
    ProgressTargetCaseGeographicScopeManager progressTargetCaseGeographicScopeManager,
    CrpProgramManager crpProgramManager,
    ProgressTargetCaseGeographicRegionManager progressTargetCaseGeographicRegionManager) {
    super(config, crpManager, phaseManager, projectManager);
    document = new XWPFDocument();
    poiSummary = new POISummary();
    this.powbExpenditureAreasManager = powbExpenditureAreasManager;
    this.reportSynthesisManager = reportSynthesisManager;
    this.reportSynthesisFlagshipProgressManager = reportSynthesisFlagshipProgressManager;
    this.reportSynthesisFlagshipProgressOutcomeMilestoneManager =
      reportSynthesisFlagshipProgressOutcomeMilestoneManager;
    this.projectPolicyManager = projectPolicyManager;
    this.projectInnovationManager = projectInnovationManager;
    this.projectExpectedStudyManager = projectExpectedStudyManager;
    this.crpProgramOutcomeManager = crpProgramOutcomeManager;
    this.reportSynthesisFlagshipProgressOutcomeManager = reportSynthesisFlagshipProgressOutcomeManager;
    this.deliverableManager = deliverableManager;
    this.reportSynthesisKeyPartnershipExternalManager = reportSynthesisKeyPartnershipExternalManager;
    this.reportSynthesisKeyPartnershipCollaborationManager = reportSynthesisKeyPartnershipCollaborationManager;
    this.reportSynthesisMeliaManager = reportSynthesisMeliaManager;
    this.fileDBManager = fileDBManager;
    this.srfSloIndicatorTargetManager = srfSloIndicatorTargetManager;
    this.reportSynthesisSrfProgressTargetCasesManager = reportSynthesisSrfProgressTargetCasesManager;
    this.reportSynthesisSrfProgressTargetContributionManager = reportSynthesisSrfProgressTargetContributionManager;
    this.crpProgramManager = crpProgramManager;
    this.progressTargetCaseGeographicRegionManager = progressTargetCaseGeographicRegionManager;
    this.progressTargetCaseGeographicScopeManager = progressTargetCaseGeographicScopeManager;
    this.progressTargetCaseGeographicCountryManager = progressTargetCaseGeographicCountryManager;
  }

  private void addAlmetricCrp() {
    if (reportSynthesisPMU != null && reportSynthesisPMU.getReportSynthesisFlagshipProgress() != null
      && reportSynthesisPMU.getReportSynthesisFlagshipProgress().getAltmetricScore() != null) {
      String synthesisCrpAltmetric = reportSynthesisPMU.getReportSynthesisFlagshipProgress().getAltmetricScore() != null
        ? reportSynthesisPMU.getReportSynthesisFlagshipProgress().getAltmetricScore() : "";
      poiSummary.convertHTMLTags(document, synthesisCrpAltmetric, null);
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
      poiSummary.convertHTMLTags(document, crossCuttingCapacityDevelopment, null);
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
      poiSummary.convertHTMLTags(document, crossCuttingClimateChange, null);
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
      this.createSubtitle("annualReport2018.ccDimensions.gender.researchFindings");
      // poiSummary.convertHTMLTags(document, this.getText("annualReport2018.ccDimensions.gender.researchFindings"));
      poiSummary.convertHTMLTags(document, crossCuttingGenderResearchFindings, null);
    }

    if (crossCuttingGenderLearned != null && !crossCuttingGenderLearned.isEmpty()) {
      this.createSubtitle("annualReport2018.ccDimensions.gender.learned");
      // poiSummary.convertHTMLTags(document, this.getText("annualReport2018.ccDimensions.gender.learned"));
      poiSummary.convertHTMLTags(document, crossCuttingGenderLearned, null);
    }

    if (crossCuttingGenderProblemsArimes != null && !crossCuttingGenderProblemsArimes.isEmpty()) {
      this.createSubtitle("annualReport2018.ccDimensions.gender.problemsArisen");
      // poiSummary.convertHTMLTags(document, this.getText("annualReport2018.ccDimensions.gender.problemsArisen"));
      poiSummary.convertHTMLTags(document, crossCuttingGenderProblemsArimes, null);
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
      poiSummary.convertHTMLTags(document, crossCuttingYouthContribution, null);
    }

    if (crossCuttingYouthResearchFindings != null && !crossCuttingYouthResearchFindings.isEmpty()) {
      this.createSubtitle("annualReport2018.ccDimensions.youth.researchFindings");
      // poiSummary.convertHTMLTags(document, this.getText("annualReport2018.ccDimensions.gender.researchFindings"));
      poiSummary.convertHTMLTags(document, crossCuttingYouthResearchFindings, null);
    }
    if (crossCuttingYouthLearned != null && !crossCuttingYouthLearned.isEmpty()) {
      this.createSubtitle("annualReport2018.ccDimensions.youth.learned");
      // poiSummary.convertHTMLTags(document, this.getText("annualReport2018.ccDimensions.gender.learned"));
      poiSummary.convertHTMLTags(document, crossCuttingYouthLearned, null);
    }
    if (crossCuttingYouthProblemsArisen != null && !crossCuttingYouthProblemsArisen.isEmpty()) {
      this.createSubtitle("annualReport2018.ccDimensions.youth.problemsArisen");
      // poiSummary.convertHTMLTags(document, this.getText("annualReport2018.ccDimensions.gender.problemsArisen"));
      poiSummary.convertHTMLTags(document, crossCuttingYouthProblemsArisen, null);
    }

  }

  private void addCrossPartnerships() {
    if (reportSynthesisPMU != null) {

      if (reportSynthesisPMU.getReportSynthesisKeyPartnership() != null
        && reportSynthesisPMU.getReportSynthesisKeyPartnership().getSummaryCgiar() != null) {
        poiSummary.convertHTMLTags(document, reportSynthesisPMU.getReportSynthesisKeyPartnership().getSummaryCgiar(),
          null);
      }
    }
  }


  private void addExpectedCrp() {
    if (reportSynthesisPMU != null && reportSynthesisPMU.getReportSynthesisSrfProgress() != null
      && reportSynthesisPMU.getReportSynthesisSrfProgress().getSummary() != null) {
      // TODO the replaceAll() is a temporal solution. we need to check where the problem comes from
      String synthesisCrpDescription = reportSynthesisPMU.getReportSynthesisSrfProgress().getSummary() != null
        ? reportSynthesisPMU.getReportSynthesisSrfProgress().getSummary().replaceAll("&amp;", "&") : "";
      if (synthesisCrpDescription != null) {
        poiSummary.convertHTMLTags(document, synthesisCrpDescription, null);
      }
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
      poiSummary.convertHTMLTags(document, keyExternal, null);
    }

  }

  private void addFinancialSummary() {

    String financialSummaryNarrative = "";

    if (reportSynthesisPMU != null) {
      if (this.getSelectedPhase().getName().equals("AR") && this.getSelectedPhase().getYear() == 2021) {
        if (reportSynthesisPMU != null && reportSynthesisPMU.getReportSynthesisCrpFinancialReports() != null
          && !reportSynthesisPMU.getReportSynthesisCrpFinancialReports().isEmpty()) {
          reportSynthesisPMU.setReportSynthesisCrpFinancialReport(reportSynthesisPMU
            .getReportSynthesisCrpFinancialReports().stream().sorted((f1, f2) -> Comparator
              .comparing(ReportSynthesisCrpFinancialReport::getActiveSince).reversed().compare(f1, f2))
            .findFirst().orElse(null));
        }

        if (reportSynthesisPMU.getReportSynthesisCrpFinancialReport() != null
          && reportSynthesisPMU.getReportSynthesisCrpFinancialReport().getId() != null) {
          financialSummaryNarrative =
            reportSynthesisPMU.getReportSynthesisCrpFinancialReport().getFinancialStatusNarrative();
        }
      } else {
        if (reportSynthesisPMU.getReportSynthesisFinancialSummary() != null) {
          ReportSynthesisFinancialSummary financialSummary = reportSynthesisPMU.getReportSynthesisFinancialSummary();
          if (financialSummary != null) {
            financialSummaryNarrative = financialSummary.getNarrative();
          }
        }
      }
    }

    if (financialSummaryNarrative != null && !financialSummaryNarrative.isEmpty()) {
      poiSummary.convertHTMLTags(document, financialSummaryNarrative, null);

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
      poiSummary.convertHTMLTags(document, brieflySummarize, null);
    }
  }

  private void addImprovingEfficiency() {
    if (reportSynthesisPMU != null) {
      if (reportSynthesisPMU.getReportSynthesisEfficiency() != null
        && reportSynthesisPMU.getReportSynthesisEfficiency().getDescription() != null) {
        // poiSummary.textParagraph(document.createParagraph(),
        // reportSynthesisPMU.getReportSynthesisEfficiency().getDescription());
        poiSummary.convertHTMLTags(document, reportSynthesisPMU.getReportSynthesisEfficiency().getDescription(), null);

      }
    }
  }

  public void addIntellectualAssets() {
    if (reportSynthesisPMU != null) {
      if (reportSynthesisPMU.getReportSynthesisIntellectualAsset() != null) {


        if (reportSynthesisPMU.getReportSynthesisIntellectualAsset().getManaged() != null) {
          if (this.isEntityCRP()) {
            this.createSubtitle("summaries.annualReport2018.effectiveness.intellectual1");
          } else {
            this.createSubtitle("summaries.annualReport2018.effectivenessPTF.intellectual1");
          }
          // poiSummary.convertHTMLTags(document,
          // this.getText("summaries.annualReport2018.effectiveness.intellectual1"));
          poiSummary.convertHTMLTags(document, reportSynthesisPMU.getReportSynthesisIntellectualAsset().getManaged(),
            null);
        }
        if (reportSynthesisPMU.getReportSynthesisIntellectualAsset().getPatents() != null) {
          this.createSubtitle("summaries.annualReport2018.effectiveness.intellectual2");
          // poiSummary.convertHTMLTags(document,
          // this.getText("summaries.annualReport2018.effectiveness.intellectual2"));
          poiSummary.convertHTMLTags(document, reportSynthesisPMU.getReportSynthesisIntellectualAsset().getPatents(),
            null);
        }
        if (reportSynthesisPMU.getReportSynthesisIntellectualAsset().getCriticalIssues() != null) {
          if (this.isEntityCRP()) {
            this.createSubtitle("summaries.annualReport2018.effectiveness.intellectual3");
          } else {
            this.createSubtitle("summaries.annualReport2018.effectivenessPTF.intellectual3");
          }
          // poiSummary.convertHTMLTags(document,
          // this.getText("summaries.annualReport2018.effectiveness.intellectual3"));
          poiSummary.convertHTMLTags(document,
            reportSynthesisPMU.getReportSynthesisIntellectualAsset().getCriticalIssues(), null);
        }
      }
    }
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
      poiSummary.convertHTMLTags(document, managementGovernanceDescription, null);

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
      poiSummary.convertHTMLTags(document, managementRiskBrief, null);
    }
  }

  private void addNarrativeSection() {
    if (reportSynthesisPMU != null && reportSynthesisPMU.getReportSynthesisNarrative() != null
      && reportSynthesisPMU.getReportSynthesisNarrative().getNarrative() != null) {
      // TODO the replaceAll() is a temporal solution. we need to check where the problem comes from
      String narrative = reportSynthesisPMU.getReportSynthesisNarrative().getNarrative().replaceAll("&amp;", "&");
      poiSummary.convertHTMLTags(document, narrative, null);
    }
  }

  private void addOverallProgressCrp() {
    if (reportSynthesisPMU != null && reportSynthesisPMU.getReportSynthesisFlagshipProgress() != null
      && reportSynthesisPMU.getReportSynthesisFlagshipProgress().getOverallProgress() != null) {
      String synthesisCrpOveral = reportSynthesisPMU.getReportSynthesisFlagshipProgress().getOverallProgress() != null
        ? reportSynthesisPMU.getReportSynthesisFlagshipProgress().getOverallProgress() : "";
      poiSummary.convertHTMLTags(document, synthesisCrpOveral, null);
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
      this.getText("summaries.ar2018.otherParticipans") + ": " + participantingCenters);
  }


  private void addProgressFlagshipCrp() {

    if (reportSynthesisPMU != null && reportSynthesisPMU.getReportSynthesisFlagshipProgress() != null
      && reportSynthesisPMU.getReportSynthesisFlagshipProgress().getProgressByFlagships() != null) {
      String synthesisCrpProgress =
        reportSynthesisPMU.getReportSynthesisFlagshipProgress().getProgressByFlagships() != null
          ? reportSynthesisPMU.getReportSynthesisFlagshipProgress().getProgressByFlagships() : "";
      poiSummary.convertHTMLTags(document, synthesisCrpProgress, null);
    }
  }

  public void addReportSynthesisMelia() {
    if (reportSynthesisPMU != null) {
      if (reportSynthesisPMU.getReportSynthesisMelia() != null
        && reportSynthesisPMU.getReportSynthesisMelia().getSummary() != null) {
        poiSummary.convertHTMLTags(document, reportSynthesisPMU.getReportSynthesisMelia().getSummary(), null);

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
      this.createSubtitle("annualReport2018.flagshipProgress.expandedResearchAreas");
      // poiSummary.convertHTMLTags(document, this.getText("annualReport2018.flagshipProgress.expandedResearchAreas"));
      poiSummary.convertHTMLTags(document, expanded, null);
    }

    if (cutBack != null && !cutBack.isEmpty()) {
      this.createSubtitle("annualReport2018.flagshipProgress.droppedResearchLines");
      // poiSummary.convertHTMLTags(document, this.getText("annualReport2018.flagshipProgress.droppedResearchLines"));
      poiSummary.convertHTMLTags(document, cutBack, null);
    }

    if (direction != null && !direction.isEmpty()) {
      this.createSubtitle("annualReport2018.flagshipProgress.changedDirection");
      // poiSummary.convertHTMLTags(document, this.getText("annualReport2018.flagshipProgress.changedDirection"));
      poiSummary.convertHTMLTags(document, direction, null);
    }
  }

  public String conversion(double valor) {
    Locale.setDefault(Locale.US);
    DecimalFormat num = new DecimalFormat("#,###.00");
    return num.format(valor);

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

  public void createSubtitle(String text) {
    XWPFParagraph paragraph = document.createParagraph();
    XWPFRun run = paragraph.createRun();
    paragraph = document.createParagraph();
    run = paragraph.createRun();
    run.setFontSize(11);
    run.setFontFamily("Calibri Light");
    run.setBold(true);
    run.setColor("444444");
    run.setText(this.getText(text));
  }

  public void createTable1() {
    List<List<POIField>> headers = new ArrayList<>();
    String blackColor = "000000";

    POIField[] sHeader = {
      new POIField(this.getText("annualReport.crpProgress.selectSLOTarget"), ParagraphAlignment.CENTER, true,
        blackColor),
      new POIField(this.getText("summaries.annualReport2018.table1a"), ParagraphAlignment.CENTER, true, blackColor),
      new POIField(this.getText("summaries.annualReport2018.table1b"), ParagraphAlignment.CENTER, true, blackColor),
      new POIField("Geographic Scope", ParagraphAlignment.CENTER, true, blackColor)};

    List<POIField> header = Arrays.asList(sHeader);
    headers.add(header);

    List<List<POIField>> datas = new ArrayList<>();
    List<POIField> data;
    List<SrfSloIndicatorTarget> sloTargets = new ArrayList<>(this.getTable1Info());

    if (reportSynthesisPMU != null && sloTargets != null) {
      String sloTargetPrev = "";
      for (SrfSloIndicatorTarget sloTarget : sloTargets) {
        String sloTargetSummary = "", briefSummaries = "", additionalContribution = "", geographicScope = "",
          regions = "", countries = "", checkContributing = "";

        // Slo Target Name
        if (sloTarget.getNarrative() != null && !sloTarget.getNarrative().isEmpty() && sloTarget.getSmoCode() != null
          && sloTarget.getTitle() != null) {
          sloTargetSummary = sloTarget.getSmoCode() + " " + sloTarget.getTitle() + ": " + sloTarget.getNarrative();
        }

        // Slo Target Check contributing
        ReportSynthesisSrfProgressTargetContribution sloContribution =
          new ReportSynthesisSrfProgressTargetContribution();
        if (reportSynthesisSrfProgressTargetContributionManager.findBySloTargetSynthesis(sloTarget.getId(),
          reportSynthesisPMU.getId()) != null) {
          sloContribution = reportSynthesisSrfProgressTargetContributionManager
            .findBySloTargetSynthesis(sloTarget.getId(), reportSynthesisPMU.getId()).get(0);
        }

        if (sloContribution != null && sloContribution.isHasEvidence()) {
          checkContributing = "N/A";
        } else {
          checkContributing = "";
        }

        // Get Target Cases (evidences)
        if (sloTarget.getTargetCases() != null && !sloTarget.getTargetCases().isEmpty()) {
          for (ReportSynthesisSrfProgressTargetCases targetCase : sloTarget.getTargetCases()) {
            if (targetCase != null) {

              if (targetCase.getBriefSummary() != null) {
                briefSummaries = targetCase.getBriefSummary();
              }

              if (this.getSelectedYear() != 2021) {
                if (targetCase.getAdditionalContribution() != null) {
                  additionalContribution = targetCase.getAdditionalContribution();
                }
              } else {
                additionalContribution = notRequiredAR2021;
              }

              if (targetCase.getGeographicScopes() != null && !targetCase.getGeographicScopes().isEmpty()) {
                boolean hasRegions = false, hasCountries = false;
                geographicScope = " •Geographic Scope: ";
                for (ProgressTargetCaseGeographicScope geScope : targetCase.getGeographicScopes()) {
                  if (geScope != null && geScope.getRepIndGeographicScope() != null
                    && geScope.getRepIndGeographicScope().getName() != null
                    && !geScope.getRepIndGeographicScope().getName().isEmpty()) {
                    geographicScope += " " + geScope.getRepIndGeographicScope().getName() + ", ";
                  }

                  if (geScope.getRepIndGeographicScope().getId() == 2) {
                    hasRegions = true;
                  }

                  if (geScope.getRepIndGeographicScope().getId() == 3 || geScope.getRepIndGeographicScope().getId() == 4
                    || geScope.getRepIndGeographicScope().getId() == 5) {
                    hasCountries = true;
                  }
                }
                if (geographicScope.contains(",")) {
                  StringBuilder textBuilder = new StringBuilder(geographicScope);
                  textBuilder.setCharAt(geographicScope.lastIndexOf(","), '.');
                  geographicScope = textBuilder.toString();
                  geographicScope = geographicScope += "\n";
                }

                if (targetCase.getGeographicRegions() != null && !targetCase.getGeographicRegions().isEmpty()
                  && hasRegions) {
                  regions = "  •Regions: ";
                  for (ProgressTargetCaseGeographicRegion region : targetCase.getGeographicRegions()) {
                    if (region != null && region.getLocElement() != null && region.getLocElement().getName() != null
                      && !region.getLocElement().getName().isEmpty()) {
                      regions += region.getLocElement().getName() + ", ";
                    }
                  }
                  if (regions.contains(",")) {
                    StringBuilder textBuilder2 = new StringBuilder(regions);
                    textBuilder2.setCharAt(regions.lastIndexOf(","), '.');
                    regions = textBuilder2.toString();
                    regions = regions += "\n";
                  }
                }

                if (targetCase.getGeographicScopes() != null && !targetCase.getGeographicScopes().isEmpty()
                  && hasCountries) {
                  countries = "  •Countries: ";
                  for (ProgressTargetCaseGeographicCountry country : targetCase.getCountries()) {
                    if (country != null && country.getLocElement() != null && country.getLocElement().getName() != null
                      && !country.getLocElement().getName().isEmpty()) {
                      countries += country.getLocElement().getName() + ", ";
                    }
                  }
                  if (countries.contains(",")) {
                    StringBuilder textBuilder2 = new StringBuilder(countries);
                    textBuilder2.setCharAt(countries.lastIndexOf(","), '.');
                    countries = textBuilder2.toString();
                    countries += "\n";
                  }
                }
              }
            }

            if (sloTargetPrev.equals(sloTargetSummary)) {
              sloTargetSummary = "";
            }
            Boolean bold = false;
            POIField[] sData =
              {new POIField(poiSummary.replaceHTMLTags(sloTargetSummary), ParagraphAlignment.LEFT, bold, blackColor),
                new POIField(this.deleteSpanTags(briefSummaries), ParagraphAlignment.LEFT, true),
                new POIField(this.deleteSpanTags(additionalContribution), ParagraphAlignment.LEFT, true),
                new POIField(geographicScope + " " + regions + " " + countries, ParagraphAlignment.LEFT, true)};
            data = Arrays.asList(sData);
            datas.add(data);
            sloTargetPrev = sloTargetSummary;
          }
        } else {
          Boolean bold = false;
          POIField[] sData =
            {new POIField(poiSummary.replaceHTMLTags(sloTargetSummary), ParagraphAlignment.LEFT, bold, blackColor),
              new POIField(checkContributing, ParagraphAlignment.LEFT, true),
              new POIField("", ParagraphAlignment.LEFT, true), new POIField("", ParagraphAlignment.LEFT, true)};
          data = Arrays.asList(sData);
          datas.add(data);
        }
      }
    }
    poiSummary.textTable(document, headers, datas, true, "table1AnnualReport2020");

  }

  private void createTable10() {
    List<List<POIField>> headers = new ArrayList<>();
    List<List<POIField>> datas = new ArrayList<>();
    List<POIField> data;
    POIField[] sHeader = {
      new POIField(
        this.getText("summaries.annualReport2018.table10Title1", new String[] {String.valueOf(this.getSelectedYear())}),
        ParagraphAlignment.CENTER, false),
      new POIField(this.getText("summaries.annualReport2018.table10Title2"), ParagraphAlignment.CENTER, false),
      new POIField(this.getText("summaries.annualReport2018.table10Title3"), ParagraphAlignment.CENTER, true, "839B49"),
      new POIField(this.getText("summaries.annualReport2018.table10Title5"), ParagraphAlignment.CENTER, false),
      new POIField(this.getText("summaries.annualReport2018.table10Title6"), ParagraphAlignment.CENTER, false)};
    List<POIField> header = Arrays.asList(sHeader);
    headers.add(header);

    if (meliaDto != null) {
      for (ProjectExpectedStudy study : meliaDto) {
        String name = "", status = "", type = "", comments = "", meliaPublications = "";
        if (study.getProjectExpectedStudyInfo(this.getSelectedPhase()) != null) {
          if (study.getProjectExpectedStudyInfo(this.getSelectedPhase()).getTitle() != null) {
            name = "S" + study.getId() + " - " + study.getProjectExpectedStudyInfo(this.getSelectedPhase()).getTitle();
          }

          if (study.getProjectExpectedStudyInfo(this.getSelectedPhase()).getStatus() != null
            && study.getProjectExpectedStudyInfo(this.getSelectedPhase()).getStatus().getName() != null) {
            status = study.getProjectExpectedStudyInfo(this.getSelectedPhase()).getStatus().getName();
          }

          if (study.getProjectExpectedStudyInfo(this.getSelectedPhase()).getStudyType() != null
            && study.getProjectExpectedStudyInfo(this.getSelectedPhase()).getStudyType().getName() != null) {
            type = study.getProjectExpectedStudyInfo(this.getSelectedPhase()).getStudyType().getName();
          }

          if (study.getProjectExpectedStudyInfo(this.getSelectedPhase()).getTopLevelComments() != null) {
            comments = study.getProjectExpectedStudyInfo(this.getSelectedPhase()).getTopLevelComments();
          }

          if (study.getProjectExpectedStudyInfo(this.getSelectedPhase()).getMELIAPublications() != null) {
            meliaPublications = study.getProjectExpectedStudyInfo(this.getSelectedPhase()).getMELIAPublications();
          }

          POIField[] sData =
            {new POIField(name, ParagraphAlignment.LEFT, false), new POIField(status, ParagraphAlignment.CENTER, false),
              new POIField(type, ParagraphAlignment.LEFT, false), new POIField(comments, ParagraphAlignment.LEFT, true),
              new POIField(meliaPublications, ParagraphAlignment.LEFT, true)};
          data = Arrays.asList(sData);
          datas.add(data);
        }
      }
    }

    poiSummary.textTable(document, headers, datas, false, "table10AnnualReport2018");
  }

  private void createTable11() {
    List<List<POIField>> headers = new ArrayList<>();
    List<List<POIField>> datas = new ArrayList<>();
    List<POIField> data;
    POIField[] sHeader = {
      new POIField(
        this.getText("summaries.annualReport2018.table11Title1", new String[] {String.valueOf(this.getSelectedYear())}),
        ParagraphAlignment.CENTER, false),
      new POIField(this.getText("summaries.annualReport2018.table11Title2"), ParagraphAlignment.CENTER, true, "000000"),
      new POIField(this.getText("summaries.annualReport2018.table11Title3"), ParagraphAlignment.CENTER, true, "76923C"),
      new POIField(this.getText("summaries.annualReport2018.table11Title4"), ParagraphAlignment.CENTER, true, "000000"),
      new POIField(this.getText("summaries.annualReport2018.table11Title5"), ParagraphAlignment.CENTER, true, "000000"),
      new POIField(this.getText("summaries.annualReport2018.table11Title6"), ParagraphAlignment.CENTER, true, "76923C"),
      new POIField(this.getText("summaries.annualReport2018.table11Title7"), ParagraphAlignment.CENTER, true, "76923C"),
      new POIField(this.getText("summaries.annualReport2018.table11Title9"), ParagraphAlignment.CENTER, true,
        "000000")};
    List<POIField> header = Arrays.asList(sHeader);
    headers.add(header);
    String lastName = "", lastRecomendation = "", lastText = "", lastId = "";
    if (reportSynthesisPMU != null && reportSynthesisPMU.getReportSynthesisMelia() != null
      && reportSynthesisPMU.getReportSynthesisMelia().getEvaluations() != null) {
      for (ReportSynthesisMeliaEvaluation evaluation : reportSynthesisPMU.getReportSynthesisMelia().getEvaluations()) {
        for (ReportSynthesisMeliaEvaluationAction action : evaluation.getMeliaEvaluationActions()) {
          String name = "", recomendation = "", text = "", status = "", actions = "", whom = "", when = "",
            evidence = "", id = "";
          if (action.getReportSynthesisMeliaEvaluation().getNameEvaluation() != null) {
            name = action.getReportSynthesisMeliaEvaluation().getNameEvaluation();

          }
          if (action.getReportSynthesisMeliaEvaluation().getRecommendation() != null) {
            recomendation = action.getReportSynthesisMeliaEvaluation().getRecommendation();
          }

          if (action.getReportSynthesisMeliaEvaluation().getManagementResponse() != null) {
            text = action.getReportSynthesisMeliaEvaluation().getManagementResponse();
          }

          if (action.getReportSynthesisMeliaEvaluation().getId() != 0) {
            id = action.getReportSynthesisMeliaEvaluation().getId().toString();
          }

          if (action.getReportSynthesisMeliaEvaluation().getStatus() != null) {
            switch (action.getReportSynthesisMeliaEvaluation().getStatus()) {
              case 2:
                status = "On Going";
                break;
              case 3:
                status = "Complete";
                break;
            }
          }

          if (action.getReportSynthesisMeliaEvaluation().getComments() != null) {
            evidence = action.getReportSynthesisMeliaEvaluation().getEvidences();
          }

          if (evidence != null && !evidence.isEmpty()) {
            evidence = evidence.replaceAll("&amp;", "&");
          }

          if (action != null && action.getActions() != null) {
            actions += poiSummary.replaceHTMLTags(action.getActions());
          }
          if (action != null && action.getTextWhom() != null) {
            whom = action.getTextWhom();
          }
          if (action != null && action.getTextWhen() != null) {
            when = action.getTextWhen();
          }

          /*
           * if (id.equals(lastId)) {
           * id = "";
           * } else {
           * lastId = id;
           * }
           * if (name.equals(lastName) && id.equals(lastId)) {
           * name = "";
           * } else {
           * lastName = name;
           * }
           * if (recomendation.equals(lastRecomendation) && id.equals(lastId)) {
           * recomendation = "";
           * } else {
           * lastRecomendation = recomendation;
           * }
           * if (text.equals(lastText) && id.equals(lastId)) {
           * text = "";
           * } else {
           * lastText = text;
           * }
           */
          POIField[] sData = {new POIField(name, ParagraphAlignment.LEFT, false, "000000"),
            new POIField(recomendation, ParagraphAlignment.LEFT, false),
            new POIField(text, ParagraphAlignment.LEFT, true),
            new POIField(status, ParagraphAlignment.LEFT, false, "000000"),
            new POIField(actions, ParagraphAlignment.LEFT, false, "000000"),
            new POIField(whom, ParagraphAlignment.LEFT, false, "000000"),
            new POIField(when, ParagraphAlignment.LEFT, false, "000000"),
            new POIField(evidence, ParagraphAlignment.LEFT, true)};
          data = Arrays.asList(sData);
          datas.add(data);
        }
      }
    }

    poiSummary.textTable(document, headers, datas, false, "table11AnnualReport2018");
  }

  private void createTable12() {
    List<List<POIField>> headers = new ArrayList<>();
    List<List<POIField>> datas = new ArrayList<>();
    List<POIField> data;
    POIField[] sHeader = {
      new POIField(
        this.getText("summaries.annualReport2018.table12Title1", new String[] {String.valueOf(this.getSelectedYear())}),
        ParagraphAlignment.LEFT, false),
      new POIField(this.getText("summaries.annualReport2018.table12Title2"), ParagraphAlignment.LEFT, false)};
    List<POIField> header = Arrays.asList(sHeader);
    headers.add(header);

    if (reportSynthesisPMU != null && reportSynthesisPMU.getReportSynthesisFundingUseSummary() != null
      && reportSynthesisPMU.getReportSynthesisFundingUseSummary().getExpenditureAreas() != null) {
      for (ReportSynthesisFundingUseExpendituryArea expenditureArea : reportSynthesisPMU
        .getReportSynthesisFundingUseSummary().getExpenditureAreas()) {
        String examples = "", area = "";

        if (expenditureArea.getExampleExpenditure() != null) {
          examples = expenditureArea.getExampleExpenditure();
        }
        if (expenditureArea.getExpenditureCategory() != null
          && expenditureArea.getExpenditureCategory().getName() != null) {
          area = expenditureArea.getExpenditureCategory().getName();
        }
        if (expenditureArea.getExpenditureCategory() != null
          && expenditureArea.getExpenditureCategory().getName() != null
          && expenditureArea.getExpenditureCategory().getName().equals("Other")
          && expenditureArea.getOtherCategory() != null) {
          area += ": " + expenditureArea.getOtherCategory();
        }

        POIField[] sData =
          {new POIField(poiSummary.replaceHTMLTags(examples), ParagraphAlignment.LEFT, false, "000000"),
            new POIField(area, ParagraphAlignment.LEFT, false, "0000")};
        data = Arrays.asList(sData);
        datas.add(data);
      }
    }

    poiSummary.textTable(document, headers, datas, false, "table3AnnualReport2018");
  }

  private void createTable13() {
    try {
      String blackColor = "000000";
      if (reportSynthesisPMU != null && reportSynthesisPMU.getReportSynthesisFinancialSummary() != null
        && reportSynthesisPMU.getReportSynthesisFinancialSummary()
          .getReportSynthesisFinancialSummaryBudgets() != null) {
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
      POIField[] sHeader = {new POIField("", ParagraphAlignment.CENTER, false),
        new POIField(
          this.getText("annualReport.financial.tableJ.budget", new String[] {String.valueOf(this.getSelectedYear())})
            + "*",
          ParagraphAlignment.CENTER, false),
        new POIField("", ParagraphAlignment.CENTER, false), new POIField("", ParagraphAlignment.CENTER, false),
        new POIField(this.getText("annualReport.financial.tableJ.expenditure") + "*", ParagraphAlignment.CENTER, false),
        new POIField("", ParagraphAlignment.CENTER, false), new POIField("", ParagraphAlignment.CENTER, false),
        new POIField(this.getText("annualReport.financial.tableJ.difference") + "*", ParagraphAlignment.CENTER, false),
        new POIField("", ParagraphAlignment.CENTER, false), new POIField("", ParagraphAlignment.CENTER, false),
        new POIField(this.getText("summaries.annualReport2018.table13Title1"), ParagraphAlignment.CENTER, true,
          "CC0000")};

      POIField[] sHeader2 = {new POIField(" ", ParagraphAlignment.CENTER, false, blackColor),
        new POIField(this.getText("financialPlan.tableE.w1w2"), ParagraphAlignment.CENTER, false, blackColor),
        new POIField(this.getText("financialPlan.tableE.w3bilateral"), ParagraphAlignment.CENTER, false, blackColor),
        new POIField(this.getText("financialPlan.tableE.total"), ParagraphAlignment.CENTER, false, blackColor),
        new POIField(this.getText("financialPlan.tableE.w1w2"), ParagraphAlignment.CENTER, false, blackColor),
        new POIField(this.getText("financialPlan.tableE.w3bilateral"), ParagraphAlignment.CENTER, false, blackColor),
        new POIField(this.getText("financialPlan.tableE.total"), ParagraphAlignment.CENTER, false, blackColor),
        new POIField(this.getText("financialPlan.tableE.w1w2"), ParagraphAlignment.CENTER, false, blackColor),
        new POIField(this.getText("financialPlan.tableE.w3bilateral"), ParagraphAlignment.CENTER, false, blackColor),
        new POIField(this.getText("financialPlan.tableE.total"), ParagraphAlignment.CENTER, false, blackColor),
        new POIField(this.getText(""), ParagraphAlignment.CENTER, false, blackColor)};

      List<POIField> header = Arrays.asList(sHeader);
      List<POIField> header2 = Arrays.asList(sHeader2);
      headers.add(header);
      headers.add(header2);

      List<List<POIField>> datas = new ArrayList<>();
      List<POIField> data;

      double totalW1w2Difference = 0.0, totalW3Difference = 0.0, grandTotalDifference = 0.0;
      reportSynthesisFinancialSummaryBudgetList
        .sort(Comparator.comparing(ReportSynthesisFinancialSummaryBudget::getId));

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
            if (this.isEntityPlatform()) {
              if (category.contains("CRP")) {
                category = category.replace("CRP", "Platform");
              }
            }
          }
          if (reportSynthesisFinancialSummaryBudget.getW1Planned() != null) {
            w1w2Planned = reportSynthesisFinancialSummaryBudget.getW1Planned();
          }

          if (reportSynthesisFinancialSummaryBudget.getW3Planned() != null) {
            w3Planned = reportSynthesisFinancialSummaryBudget.getW3Planned();
          }

          totalPlanned = w1w2Planned + w3Planned;

          if (reportSynthesisFinancialSummaryBudget.getW1Actual() != null) {
            w1w2Actual = reportSynthesisFinancialSummaryBudget.getW1Actual();
          }

          if (reportSynthesisFinancialSummaryBudget.getW3Actual() != null) {
            w3Actual = reportSynthesisFinancialSummaryBudget.getW3Actual();
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

          POIField[] sData = {new POIField(category, ParagraphAlignment.CENTER, false),
            new POIField("US$ " + (this.conversion(w1w2Planned)), ParagraphAlignment.CENTER, false),
            new POIField("US$ " + (this.conversion(w3Planned)), ParagraphAlignment.CENTER, false),
            new POIField("US$ " + (this.conversion(totalPlanned)), ParagraphAlignment.CENTER, false),
            new POIField("US$ " + (this.conversion(w1w2Actual)), ParagraphAlignment.CENTER, false),
            new POIField("US$ " + (this.conversion(w3Actual)), ParagraphAlignment.CENTER, false),
            new POIField("US$ " + (this.conversion(totalActual)), ParagraphAlignment.CENTER, false),
            new POIField("US$ " + (this.conversion(w1w2Difference)), ParagraphAlignment.CENTER, false),
            new POIField("US$ " + (this.conversion(w3Difference)), ParagraphAlignment.CENTER, false),
            new POIField("US$ " + (this.conversion(totalDifference)), ParagraphAlignment.CENTER, false),
            new POIField(comments, ParagraphAlignment.LEFT, true)};

          data = Arrays.asList(sData);
          datas.add(data);

        }
      }

      Boolean bold = true;
      String totalCRPPTL = "";
      if (this.isEntityCRP()) {
        totalCRPPTL = "CRP Total";
      } else {
        totalCRPPTL = "Platform Total";
      }

      POIField[] sData = {new POIField(totalCRPPTL, ParagraphAlignment.CENTER, bold, blackColor),

        new POIField("US$ " + (this.conversion(totalw1w2Planned)), ParagraphAlignment.CENTER, bold, blackColor),
        new POIField("US$ " + (this.conversion(totalW3Planned)), ParagraphAlignment.CENTER, bold, blackColor),
        new POIField("US$ " + (this.conversion(grandTotalPlanned)), ParagraphAlignment.CENTER, bold, blackColor),
        new POIField("US$ " + (this.conversion(totalw1w2Actual)), ParagraphAlignment.CENTER, bold, blackColor),
        new POIField("US$ " + (this.conversion(totalW3Actual)), ParagraphAlignment.CENTER, bold, blackColor),
        new POIField("US$ " + (this.conversion(grandTotalActual)), ParagraphAlignment.CENTER, bold, blackColor),
        new POIField("US$ " + (this.conversion(totalW1w2Difference)), ParagraphAlignment.CENTER, bold, blackColor),
        new POIField("US$ " + (this.conversion(totalW3Difference)), ParagraphAlignment.CENTER, bold, blackColor),
        new POIField("US$ " + (this.conversion(grandTotalDifference)), ParagraphAlignment.CENTER, bold, blackColor),};

      data = Arrays.asList(sData);
      datas.add(data);

      poiSummary.textTable(document, headers, datas, true, "table13AnnualReport2018");
    } catch (Exception e) {
      System.out.println(e);
    }
  }


  private void createTable13AR2021() {
    try {
      String blackColor = "000000";
      if (reportSynthesisPMU != null && reportSynthesisPMU.getReportSynthesisCrpFinancialReports() != null
        && !reportSynthesisPMU.getReportSynthesisCrpFinancialReports().isEmpty()) {
        reportSynthesisPMU.setReportSynthesisCrpFinancialReport(reportSynthesisPMU
          .getReportSynthesisCrpFinancialReports().stream().sorted((f1, f2) -> Comparator
            .comparing(ReportSynthesisCrpFinancialReport::getActiveSince).reversed().compare(f1, f2))
          .findFirst().orElse(null));
      }

      List<List<POIField>> headers = new ArrayList<>();
      POIField[] sHeader = {new POIField("", ParagraphAlignment.CENTER, false),
        new POIField(this.getText("annualReport2018.financial.table12.2020Forecast") + "*", ParagraphAlignment.CENTER,
          false),
        new POIField(this.getText("annualReport2018.financial.table12.2021Budget") + "*", ParagraphAlignment.CENTER,
          false),
        new POIField(this.getText("annualReport2018.financial.table12.commentsChanges") + "*",
          ParagraphAlignment.CENTER, false)};

      List<POIField> header = Arrays.asList(sHeader);
      headers.add(header);

      List<List<POIField>> datas = new ArrayList<>();
      List<POIField> data;
      POIField[] sData;

      ReportSynthesisCrpFinancialReport financialReport = reportSynthesisPMU.getReportSynthesisCrpFinancialReport();

      if (financialReport != null && financialReport.getId() != null) {
        sData = new POIField[] {
          new POIField(this.getText("annualReport2018.financial.table12.personnel"), ParagraphAlignment.CENTER, false),
          new POIField(
            "US$ " + (this.conversion(
              financialReport.getPersonnel2020Forecast() != null ? financialReport.getPersonnel2020Forecast() : 0d)),
            ParagraphAlignment.CENTER, false),
          new POIField(
            "US$ " + (this.conversion(
              financialReport.getPersonnel2021Budget() != null ? financialReport.getPersonnel2021Budget() : 0d)),
            ParagraphAlignment.CENTER, false),
          new POIField(financialReport.getPersonnelComments(), ParagraphAlignment.LEFT, true)};

        data = Arrays.asList(sData);
        datas.add(data);

        sData = new POIField[] {
          new POIField(this.getText("annualReport2018.financial.table12.consultancy"), ParagraphAlignment.CENTER,
            false),
          new POIField("US$ " + (this.conversion(
            financialReport.getConsultancy2020Forecast() != null ? financialReport.getConsultancy2020Forecast() : 0d)),
            ParagraphAlignment.CENTER, false),
          new POIField(
            "US$ " + (this.conversion(
              financialReport.getConsultancy2021Budget() != null ? financialReport.getConsultancy2021Budget() : 0d)),
            ParagraphAlignment.CENTER, false),
          new POIField(financialReport.getConsultancyComments(), ParagraphAlignment.LEFT, true)};

        data = Arrays.asList(sData);
        datas.add(data);

        sData = new POIField[] {
          new POIField(this.getText("annualReport2018.financial.table12.travel"), ParagraphAlignment.CENTER, false),
          new POIField(
            "US$ " + (this.conversion(
              financialReport.getTravel2020Forecast() != null ? financialReport.getTravel2020Forecast() : 0d)),
            ParagraphAlignment.CENTER, false),
          new POIField(
            "US$ " + (this
              .conversion(financialReport.getTravel2021Budget() != null ? financialReport.getTravel2021Budget() : 0d)),
            ParagraphAlignment.CENTER, false),
          new POIField(financialReport.getTravelComments(), ParagraphAlignment.LEFT, true)};

        data = Arrays.asList(sData);
        datas.add(data);

        sData = new POIField[] {
          new POIField(this.getText("annualReport2018.financial.table12.expenses"), ParagraphAlignment.CENTER, false),
          new POIField(
            "US$ " + (this.conversion(
              financialReport.getOperation2020Forecast() != null ? financialReport.getOperation2020Forecast() : 0d)),
            ParagraphAlignment.CENTER, false),
          new POIField(
            "US$ " + (this.conversion(
              financialReport.getOperation2021Budget() != null ? financialReport.getOperation2021Budget() : 0d)),
            ParagraphAlignment.CENTER, false),
          new POIField(financialReport.getOperationComments(), ParagraphAlignment.LEFT, true)};

        data = Arrays.asList(sData);
        datas.add(data);

        sData = new POIField[] {
          new POIField(this.getText("annualReport2018.financial.table12.collaborators"), ParagraphAlignment.CENTER,
            false),
          new POIField("US$ " + (this.conversion(financialReport.getCollaboratorPartnerships2020Forecast() != null
            ? financialReport.getCollaboratorPartnerships2020Forecast() : 0d)), ParagraphAlignment.CENTER, false),
          new POIField("US$ " + (this.conversion(financialReport.getCollaboratorPartnerships2021Budget() != null
            ? financialReport.getCollaboratorPartnerships2021Budget() : 0d)), ParagraphAlignment.CENTER, false),
          new POIField(financialReport.getCollaboratorPartnershipsComments(), ParagraphAlignment.LEFT, true)};

        data = Arrays.asList(sData);
        datas.add(data);

        sData = new POIField[] {
          new POIField(this.getText("annualReport2018.financial.table12.capital"), ParagraphAlignment.CENTER, false),
          new POIField("US$ " + (this.conversion(financialReport.getCapitalEquipment2020Forecast() != null
            ? financialReport.getCapitalEquipment2020Forecast() : 0d)), ParagraphAlignment.CENTER, false),
          new POIField("US$ " + (this.conversion(financialReport.getCapitalEquipment2021Budget() != null
            ? financialReport.getCapitalEquipment2021Budget() : 0d)), ParagraphAlignment.CENTER, false),
          new POIField(financialReport.getCapitalEquipmentComments(), ParagraphAlignment.LEFT, true)};

        data = Arrays.asList(sData);
        datas.add(data);

        sData = new POIField[] {
          new POIField(this.getText("annualReport2018.financial.table12.closeout"), ParagraphAlignment.CENTER, false),
          new POIField(
            "US$ " + (this.conversion(
              financialReport.getCloseout2020Forecast() != null ? financialReport.getCloseout2020Forecast() : 0d)),
            ParagraphAlignment.CENTER, false),
          new POIField(
            "US$ " + (this.conversion(
              financialReport.getCloseout2021Budget() != null ? financialReport.getCloseout2021Budget() : 0d)),
            ParagraphAlignment.CENTER, false),
          new POIField(financialReport.getCloseoutComments(), ParagraphAlignment.LEFT, true)};

        data = Arrays.asList(sData);
        datas.add(data);

        String totalCRPPTL = "";
        if (this.isEntityCRP()) {
          totalCRPPTL = "CRP Total";
        } else {
          totalCRPPTL = "Platform Total";
        }

        sData = new POIField[] {new POIField(totalCRPPTL, ParagraphAlignment.CENTER, true, blackColor),
          new POIField(
            "US$ " + (this.conversion(
              financialReport.getCrpTotal2020Forecast() != null ? financialReport.getCrpTotal2020Forecast() : 0d)),
            ParagraphAlignment.CENTER, true, blackColor),
          new POIField(
            "US$ " + (this.conversion(
              financialReport.getCrpTotal2021Budget() != null ? financialReport.getCrpTotal2021Budget() : 0d)),
            ParagraphAlignment.CENTER, true, blackColor),
          new POIField(financialReport.getCrpTotalComments(), ParagraphAlignment.LEFT, true)};

        data = Arrays.asList(sData);
        datas.add(data);
      }

      poiSummary.textTable(document, headers, datas, true, "table13AnnualReport2021");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void createTable2() {

    List<List<POIField>> headers = new ArrayList<>();

    String blackColor = "000000";

    Boolean bold = false;
    POIField[] sHeader = {
      new POIField(this.getText("summaries.annualReport2019.table2Title1"), ParagraphAlignment.CENTER, bold,
        blackColor),
      new POIField(this.getText("summaries.annualReport2019.table2Description"), ParagraphAlignment.CENTER, bold,
        blackColor),
      new POIField(this.getText("summaries.annualReport2018.table2Title2"), ParagraphAlignment.CENTER, bold,
        blackColor),
      new POIField(this.getText("summaries.annualReport2018.table2Title3"), ParagraphAlignment.CENTER, bold,
        blackColor),
      new POIField(this.getText("summaries.annualReport2018.table2Title4"), ParagraphAlignment.CENTER, bold,
        blackColor),
      new POIField("", ParagraphAlignment.CENTER, bold, blackColor),
      new POIField("", ParagraphAlignment.CENTER, bold, blackColor),
      new POIField("", ParagraphAlignment.CENTER, bold, blackColor), new POIField(
        this.getText("summaries.annualReport2018.table2Title8"), ParagraphAlignment.CENTER, bold, blackColor)};

    bold = false;
    POIField[] sHeader2 = {new POIField("", ParagraphAlignment.LEFT, bold, blackColor),
      new POIField("", ParagraphAlignment.LEFT, bold, blackColor),
      new POIField("", ParagraphAlignment.LEFT, bold, blackColor),
      new POIField("", ParagraphAlignment.LEFT, bold, blackColor),
      new POIField(this.getText("summaries.annualReport2018.table2Gender"), ParagraphAlignment.CENTER, bold,
        blackColor),
      new POIField(this.getText("summaries.annualReport2018.table2Youth"), ParagraphAlignment.CENTER, bold, blackColor),
      new POIField(this.getText("summaries.annualReport2018.table2Capdev"), ParagraphAlignment.CENTER, bold,
        blackColor),
      new POIField(this.getText("summaries.annualReport2018.table2ClimateChange"), ParagraphAlignment.CENTER, bold,
        blackColor),
      new POIField("", ParagraphAlignment.CENTER, bold, blackColor)};

    List<POIField> header = Arrays.asList(sHeader);
    List<POIField> header2 = Arrays.asList(sHeader2);
    headers.add(header);
    headers.add(header2);

    List<List<POIField>> datas = new ArrayList<>();
    List<POIField> data;
    data = new ArrayList<>();


    for (ProjectPolicy projectPolicy : projectPoliciesTable2) {
      String name = null, description = null, levelMaturity = "", srfSubIdo = "", gender = "", youth = "", capdev = "",
        climateChange = "", evidences = "";

      // List of Urls
      List<String> urls = new ArrayList<>();
      // List of Texts
      List<String> texts = new ArrayList<>();


      if (projectPolicy != null && projectPolicy.getProjectPolicyInfo() != null) {
        srfSubIdo = "";
        if (projectPolicy.getProjectPolicyInfo().getTitle() != null) {
          name = projectPolicy.getId() + " - " + projectPolicy.getProjectPolicyInfo().getTitle();
        }

        if (projectPolicy.getProjectPolicyInfo().getDescription() != null) {
          description = projectPolicy.getProjectPolicyInfo().getDescription();
        }

        if (projectPolicy.getProjectPolicyInfo().getRepIndStageProcess() != null
          && projectPolicy.getProjectPolicyInfo().getRepIndStageProcess().getName() != null) {
          levelMaturity = projectPolicy.getProjectPolicyInfo().getRepIndStageProcess().getName();
        }

        if (projectPolicy.getSubIdos(this.getSelectedPhase()) != null) {
          for (ProjectPolicySubIdo subIdo : projectPolicy.getSubIdos(this.getSelectedPhase())) {
            if (subIdo.getSrfSubIdo() != null && subIdo.getSrfSubIdo().getDescription() != null) {
              srfSubIdo += "• " + subIdo.getSrfSubIdo().getDescription() + "\n";
            }
          }
        }

        if (projectPolicy.getProjectPolicyInfo().getRepIndStageProcess() != null
          && projectPolicy.getProjectPolicyInfo().getRepIndStageProcess().getId() == 3) {
          if (projectPolicy.getProjectPolicyInfo().getNarrativeEvidence() != null
            && !projectPolicy.getProjectPolicyInfo().getNarrativeEvidence().isEmpty()) {
            evidences += projectPolicy.getProjectPolicyInfo().getNarrativeEvidence() + " \n";
          }
        }

        if (projectPolicy.getEvidences(this.getSelectedPhase()) != null) {
          String temp = "";
          for (ProjectExpectedStudyPolicy evidence : projectPolicy.getEvidences(this.getSelectedPhase())) {
            if (evidence.getProjectExpectedStudy().getId() != null) {
              temp = this.getBaseUrl() + "/projects/" + this.getCrpSession() + "/studySummary.do?studyID="
                + (evidence.getProjectExpectedStudy().getId()).toString() + "&cycle=" + this.getCurrentCycle()
                + "&year=" + this.getSelectedPhase().getYear();

              urls.add(temp);
              if (evidence.getProjectExpectedStudy().getComposedName() != null) {
                texts.add(evidence.getProjectExpectedStudy().getComposedIdentifier());
              } else {
                texts.add(evidence.getProjectExpectedStudy().getId().toString());
              }
            }
          }
        }

        if (projectPolicy.getCrossCuttingMarkers(this.getSelectedPhase()) != null) {
          for (ProjectPolicyCrossCuttingMarker policyCrossCutting : projectPolicy
            .getCrossCuttingMarkers(this.getSelectedPhase())) {
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

        try {
          if (srfSubIdo.contains(",")) {
            srfSubIdo = srfSubIdo.substring(0, srfSubIdo.length() - 2);
          }
        } catch (Exception e) {

        }
      }

      if (urls != null && !urls.isEmpty()) {
        POIField[] sData = {new POIField(name, ParagraphAlignment.LEFT, false),
          new POIField(description, ParagraphAlignment.LEFT, false),
          new POIField(levelMaturity, ParagraphAlignment.LEFT, false),
          new POIField(srfSubIdo, ParagraphAlignment.LEFT, false),
          new POIField(gender, ParagraphAlignment.LEFT, false, blackColor),
          new POIField(youth, ParagraphAlignment.LEFT, false), new POIField(capdev, ParagraphAlignment.LEFT, false),
          new POIField(climateChange, ParagraphAlignment.CENTER, false),
          new POIField(texts, urls, ParagraphAlignment.LEFT, false, blackColor, 1)};
        data = Arrays.asList(sData);
        datas.add(data);
      } else {
        POIField[] sData = {new POIField(name, ParagraphAlignment.LEFT, false),
          new POIField(description, ParagraphAlignment.LEFT, false),
          new POIField(levelMaturity, ParagraphAlignment.LEFT, false),
          new POIField(srfSubIdo, ParagraphAlignment.LEFT, false),
          new POIField(gender, ParagraphAlignment.LEFT, false, blackColor),
          new POIField(youth, ParagraphAlignment.LEFT, false), new POIField(capdev, ParagraphAlignment.LEFT, false),
          new POIField(climateChange, ParagraphAlignment.CENTER, false),
          new POIField(evidences, ParagraphAlignment.LEFT, false)};
        data = Arrays.asList(sData);
        datas.add(data);
      }
    }

    String text = "";

    text = "table2AnnualReport2018CRP";

    poiSummary.textTable(document, headers, datas, false, text);
  }

  private void createTable3() {

    List<List<POIField>> headers = new ArrayList<>();
    List<List<POIField>> datas = new ArrayList<>();
    List<POIField> data;
    POIField[] sHeader = {
      new POIField(
        this.getText("summaries.annualReport2018.table3Title1", new String[] {String.valueOf(this.getSelectedYear())}),
        ParagraphAlignment.CENTER, false, true),
      new POIField(this.getText("summaries.annualReport2018.table3Title2a"), ParagraphAlignment.CENTER, false),
      new POIField(this.getText("summaries.annualReport2018.table3Title2"), ParagraphAlignment.CENTER,
        false)/*
               * ,
               * //REMOVED FOR AR 2020
               * new POIField(this.getText("summaries.annualReport2018.table3Title3"), ParagraphAlignment.LEFT, false)
               */};
    List<POIField> header = Arrays.asList(sHeader);
    headers.add(header);


    // "${baseUrl}/projects/${crpSession}/studySummary.do?studyID=${(item.id)!}&cycle=Reporting&year=${(actualPhase.year)!}"]

    for (ProjectExpectedStudy projectExpectStudy : projectExpectedStudiesTable3) {
      String title = "", maturity = "", linkOICR = "", indicator = "", url = "";
      if (projectExpectStudy != null
        && projectExpectStudy.getProjectExpectedStudyInfo(this.getSelectedPhase()) != null) {
        if (projectExpectStudy.getProjectExpectedStudyInfo(this.getSelectedPhase()).getTitle() == null) {
          title = "OICR" + projectExpectStudy.getId();
        } else {
          title = "OICR" + projectExpectStudy.getId() + " - "
            + projectExpectStudy.getProjectExpectedStudyInfo(this.getSelectedPhase()).getTitle();
        }
        if (projectExpectStudy.getProjectExpectedStudyInfo().getRepIndStageStudy() != null
          && projectExpectStudy.getProjectExpectedStudyInfo().getRepIndStageStudy().getName() != null) {
          maturity = projectExpectStudy.getProjectExpectedStudyInfo().getRepIndStageStudy().getName();
        }

        if (projectExpectStudy.getProjectExpectedStudyInfo().getIsPublic() != null
          && projectExpectStudy.getProjectExpectedStudyInfo().getIsPublic() == true) {
          linkOICR = this.getBaseUrl() + "/projects/" + this.getCrpSession() + "/studySummary.do?studyID="
            + (projectExpectStudy.getId()).toString() + "&cycle=" + this.getCurrentCycle() + "&year="
            + this.getSelectedPhase().getYear();
        }
      }

      url = this.getBaseUrl() + "/projects/" + this.getCrpSession() + "/studySummary.do?studyID="
        + (projectExpectStudy.getId()).toString() + "&cycle=" + this.getCurrentCycle() + "&year="
        + this.getSelectedPhase().getYear();

      POIField[] sData = {new POIField(title, ParagraphAlignment.LEFT, false),
        new POIField("Link", ParagraphAlignment.CENTER, false, "000000", url), new POIField(maturity,
          ParagraphAlignment.CENTER, false)/*
                                            * ,
                                            * //REMOVED FOR AR 2020
                                            * new POIField(indicator, ParagraphAlignment.LEFT, false)
                                            */};
      data = Arrays.asList(sData);
      datas.add(data);
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
        ParagraphAlignment.CENTER, false),
      new POIField(this.getText("summaries.annualReport2018.table4Title2"), ParagraphAlignment.CENTER, false),
      new POIField(this.getText("summaries.annualReport2018.table4Title3"), ParagraphAlignment.CENTER, false),
      new POIField(this.getText("summaries.annualReport2018.table4Title4"), ParagraphAlignment.CENTER, false)};
    List<POIField> header = Arrays.asList(sHeader);
    headers.add(header);
    for (ProjectInnovation projectInnovation : projectInnovationsTable4) {
      String title = "", type = "", stage = "", geographic = "", country = "", region = "", url = "";

      if (projectInnovation != null && projectInnovation.getProjectInnovationInfo(this.getSelectedPhase()) != null) {
        if (projectInnovation.getProjectInnovationInfo(this.getSelectedPhase()).getTitle() != null) {
          title = projectInnovation.getId() + " - "
            + projectInnovation.getProjectInnovationInfo(this.getSelectedPhase()).getTitle();
        }
        if (projectInnovation.getProjectInnovationInfo(this.getSelectedPhase()).getRepIndInnovationType() != null
          && projectInnovation.getProjectInnovationInfo(this.getSelectedPhase()).getRepIndInnovationType()
            .getName() != null) {
          type =
            projectInnovation.getProjectInnovationInfo(this.getSelectedPhase()).getRepIndInnovationType().getName();
        }
        if (projectInnovation.getProjectInnovationInfo(this.getSelectedPhase()).getRepIndStageInnovation() != null
          && projectInnovation.getProjectInnovationInfo(this.getSelectedPhase()).getRepIndStageInnovation()
            .getName() != null) {
          stage =
            projectInnovation.getProjectInnovationInfo(this.getSelectedPhase()).getRepIndStageInnovation().getName();
        }
        if (projectInnovation.getGeographicScopes(this.getSelectedPhase()) != null) {
          List<ProjectInnovationGeographicScope> innovationGeographics =
            projectInnovation.getGeographicScopes(this.getSelectedPhase());
          for (ProjectInnovationGeographicScope innovationGeographic : innovationGeographics) {
            if (innovationGeographic != null && innovationGeographic.getRepIndGeographicScope() != null
              && innovationGeographic.getRepIndGeographicScope().getName() != null) {
              if (innovationGeographic.getRepIndGeographicScope().getName().contains("Global")) {
                geographic += innovationGeographic.getRepIndGeographicScope().getName() + ", ";
              } else {
                geographic += innovationGeographic.getRepIndGeographicScope().getName() + ": ";
              }
            }
          }
        }

        if (projectInnovation.getRegions(this.getSelectedPhase()) != null) {
          List<ProjectInnovationRegion> innovationGeographics = projectInnovation.getRegions(this.getSelectedPhase());
          for (ProjectInnovationRegion innovationGeographic : innovationGeographics) {
            if (innovationGeographic != null && innovationGeographic.getLocElement() != null
              && innovationGeographic.getLocElement().getName() != null) {
              region += innovationGeographic.getLocElement().getName() + ", ";
            }
          }
        }

        if (projectInnovation.getCountries(this.getSelectedPhase()) != null) {
          List<ProjectInnovationCountry> innovationGeographics =
            projectInnovation.getCountries(this.getSelectedPhase());
          for (ProjectInnovationCountry innovationGeographic : innovationGeographics) {
            if (innovationGeographic != null && innovationGeographic.getLocElement() != null
              && innovationGeographic.getLocElement().getName() != null) {
              country += innovationGeographic.getLocElement().getName() + ", ";
            }
          }
        }
      }

      // ${(crpSession)!}/projectInnovationSummary'][@s.param name='innovationID']${item.id?c}[/@s.param][@s.param
      // name='phaseID']${(item.projectInnovationInfo.phase.id)!''}


      url = this.getBaseUrl() + "/summaries/" + this.getCrpSession() + "/projectInnovationSummary.do?innovationID="
        + (projectInnovation.getId()).toString() + "&phaseID=" + this.getSelectedPhase().getId();

      if (country != null) {
        geographic += country;
      }
      if (region != null) {
        geographic += region;
      }

      try {
        geographic = geographic.substring(0, geographic.length() - 2);
      } catch (Exception e) {

      }

      POIField[] sData = {new POIField(title, ParagraphAlignment.LEFT, false, "0000", url),
        new POIField(type, ParagraphAlignment.CENTER, false), new POIField(stage, ParagraphAlignment.LEFT, false),
        new POIField(geographic, ParagraphAlignment.LEFT, false)};
      data = Arrays.asList(sData);
      datas.add(data);
    }
    poiSummary.textTable(document, headers, datas, false, "table4AnnualReport2018");
  }


  private void createTable5() {

    List<List<POIField>> headers = new ArrayList<>();
    List<List<POIField>> datas = new ArrayList<>();
    List<POIField> data;
    if (this.isEntityCRP() == true) {
      POIField[] sHeader = {
        new POIField(this.getText("summaries.annualReport2018.table5Title1"), ParagraphAlignment.CENTER, false,
          "000000"),
        new POIField(this.getText("summaries.annualReport2019.table5Title2"), ParagraphAlignment.CENTER, false,
          "000000"),
        new POIField(this.getText("summaries.annualReport2018.table5Title2a"), ParagraphAlignment.CENTER, false,
          "000000"),
        new POIField(this.getText("summaries.annualReport2018.table5Title3"), ParagraphAlignment.CENTER, false,
          "000000"),
        new POIField(this.getText("summaries.annualReport2018.table5Title4"), ParagraphAlignment.CENTER, false,
          "000000"),
        new POIField(this.getSelectedYear() + " " + this.getText("summaries.annualReport2019.table5Title5a"),
          ParagraphAlignment.CENTER, false, "000000"),
        new POIField(this.getText("summaries.annualReport2019.table5Title1") + "  "
          + this.getText("summaries.annualReport2018.table5Title6") + " "
          + this.getText("summaries.annualReport2018.table5Title61"), ParagraphAlignment.CENTER, false, "000000"),
        new POIField(this.getText("summaries.annualReport2018.table5Title7"), ParagraphAlignment.CENTER, false,
          "000000")};

      List<POIField> header = Arrays.asList(sHeader);
      headers.add(header);
    }

    if (this.isEntityPlatform() == true) {
      POIField[] sHeader =
        {new POIField(this.getText("summaries.annualReportCRP2019.module"), ParagraphAlignment.CENTER, true, "000000"),
          new POIField(this.getText("summaries.annualReport2019.table5Title2Module"), ParagraphAlignment.CENTER, true,
            "000000"),
          new POIField(this.getText("summaries.annualReport2019.table5Title2a"), ParagraphAlignment.CENTER, true,
            "000000"),
          new POIField(this.getText("summaries.annualReport2019.table5Title3Module"), ParagraphAlignment.CENTER, true,
            "000000"),
          new POIField(this.getText("summaries.annualReport2019.table5Title4"), ParagraphAlignment.CENTER, true,
            "000000"),
          new POIField(this.getSelectedYear() + " " + this.getText("summaries.annualReport2019.table5Title5a"),
            ParagraphAlignment.CENTER, true, "000000"),
          new POIField(this.getText("summaries.annualReport2019.table5Title1") + "  "
            + this.getText("summaries.annualReport2019.table5Title6") + " "
            + this.getText("summaries.annualReport2019.table5Title61"), ParagraphAlignment.LEFT, true, "000000"),
          new POIField(this.getText("summaries.annualReport2018.table5Title7"), ParagraphAlignment.CENTER, true,
            "000000")};

      List<POIField> header = Arrays.asList(sHeader);
      headers.add(header);
    }


    if (flagships != null && !flagships.isEmpty()) {
      flagships.sort((p1, p2) -> p1.getAcronym().compareTo(p2.getAcronym()));
      for (CrpProgram flagship : flagships) {

        int outcome_index = 0;
        if (flagship.getOutcomes() != null) {
          String lastOutcome = "", lastNarrative = "", lastFP = "", lastSubIDO = "";
          for (CrpProgramOutcome outcome : flagship.getOutcomes()) {
            String fp = "", subIdos = "", outcomes = "", narrative = "", milestone = "", milestoneStatus = "",
              evidenceMilestone = "", evidence = "";
            int milestone_index = 0;
            outcome.setSubIdos(
              outcome.getCrpOutcomeSubIdos().stream().filter(c -> c.isActive()).collect(Collectors.toList()));
            if (outcome.getSubIdos() != null && outcome.getSubIdos().size() > 0) {
              for (CrpOutcomeSubIdo subIdo : outcome.getSubIdos()) {
                if (subIdo.getSrfSubIdo() != null && subIdo.getSrfSubIdo().getDescription() != null) {
                  subIdos += "• " + subIdo.getSrfSubIdo().getDescription() + "\n";
                }
              }
            }

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

              if (crpMilestone.getComposedName() != null) {
                milestone = crpMilestone.getComposedName();
              }
              /*
               * flagshipsReportSynthesisFlagshipProgress =
               * reportSynthesisFlagshipProgressManager.getFlagshipsReportSynthesisFlagshipProgress(
               * flagshipLiaisonInstitutions, this.getSelectedPhase().getId());
               * if (flagshipsReportSynthesisFlagshipProgress != null) {
               * int i = 0;
               * for (ReportSynthesisFlagshipProgress flagshipProgress : flagshipsReportSynthesisFlagshipProgress) {
               * String FP = "", annex = "";
               * if (flagshipProgress.getDetailedAnnex() != null) {
               * annex = flagshipProgress.getDetailedAnnex();
               * }
               * if (this.isEntityPlatform()) {
               * FP = flagships.get(i).getComposedName();
               * } else {
               * FP = flagships.get(i).getComposedName();
               * }
               * i++;
               * POIField[] sData = {new POIField(FP, ParagraphAlignment.CENTER, true),
               * new POIField(poiSummary.replaceHTMLTags(annex), ParagraphAlignment.CENTER, true)};
               * data = Arrays.asList(sData);
               * datas.add(data);
               * }
               * }
               */
              ReportSynthesisFlagshipProgressOutcomeMilestone milestoneOb = null;
              if (reportSynthesisFlagshipProgressOutcomeMilestoneManager.findAll() != null
                && reportSynthesisFlagshipProgressOutcomeMilestoneManager.findAll().stream().filter(
                  m -> m.getCrpMilestone().getId() != null && m.getCrpMilestone().getId().equals(crpMilestone.getId()))
                  .collect(Collectors.toList()) != null) {
                try {
                  milestoneOb =
                    reportSynthesisFlagshipProgressOutcomeMilestoneManager.findAll().stream()
                      .filter(m -> m.getCrpMilestone().getId() != null
                        && m.getCrpMilestone().getId().equals(crpMilestone.getId()))
                      .collect(Collectors.toList()).get(0);
                } catch (Exception e) {
                }

                if (milestoneOb != null) {

                  if (milestoneOb.getEvidence() != null) {
                    evidenceMilestone = milestoneOb.getEvidence();
                    if (evidenceMilestone != null) {
                      // TODO the replaceAll() is a temporal solution. we need to check where the problem comes from
                      evidenceMilestone = evidenceMilestone.replaceAll("&amp;", "&");
                    }
                  }

                  if (this.getSelectedYear() != 2021) {
                    if (milestoneOb.getEvidenceLink() != null) {
                      evidence = milestoneOb.getEvidenceLink();
                      if (evidence != null) {
                        // TODO the replaceAll() is a temporal solution. we need to check where the problem comes from
                        evidence = evidence.replaceAll("&amp;", "&");
                      }
                    }
                  } else {
                    if (this.isNotEmpty(milestoneOb.getReportSynthesisFlagshipProgressOutcomeMilestoneLinks())) {
                      StringBuffer links = new StringBuffer();
                      String linkTemp = "";
                      for (ReportSynthesisFlagshipProgressOutcomeMilestoneLink milestoneLink : milestoneOb
                        .getReportSynthesisFlagshipProgressOutcomeMilestoneLinks()) {
                        linkTemp = StringUtils.trimToEmpty(milestoneLink.getLink());
                        if (StringUtils.isNotBlank(linkTemp)) {
                          links = links.append("•").append(linkTemp.replaceAll("&amp;", "&")).append("<br>");
                        }
                      }

                      if (links.length() != 0) {
                        evidence = links.toString();
                      }
                    }
                  }

                  if (milestoneOb.getMilestonesStatus() != null && milestoneOb.getMilestonesStatus().getId() != null) {
                    milestoneStatus = milestoneOb.getMilestonesStatus().getName();
                    if (milestoneStatus != null) {
                      // TODO the replaceAll() is a temporal solution. we need to check where the problem comes from
                      milestoneStatus = milestoneStatus.replaceAll("&amp;", "&");
                    }
                  }
                }
              }

              ReportSynthesisFlagshipProgressOutcome outcomeOb = null;
              if (reportSynthesisFlagshipProgressOutcomeManager.findAll() != null
                && reportSynthesisFlagshipProgressOutcomeManager.findAll().stream()
                  .filter(o -> o.getCrpProgramOutcome().getId() != null
                    && o.getCrpProgramOutcome().getId().equals(outcome.getId()))
                  .collect(Collectors.toList()) != null) {
                try {
                  outcomeOb =
                    reportSynthesisFlagshipProgressOutcomeManager.findAll().stream()
                      .filter(o -> o.getCrpProgramOutcome().getId() != null
                        && o.getCrpProgramOutcome().getId().equals(outcome.getId()))
                      .collect(Collectors.toList()).get(0);
                } catch (Exception e) {

                }
                if (outcomeOb != null) {

                  if (outcomeOb != null && outcomeOb.getSummary() != null) {
                    narrative = outcomeOb.getSummary();
                  }
                }
              }

              if (narrative.equals(lastNarrative)) {
                narrative = "";
              } else {
                lastNarrative = narrative;
              }

              if (subIdos.equals(lastSubIDO)) {
                subIdos = "";
              } else {
                lastSubIDO = subIdos;
              }

              POIField[] sData = {new POIField(fp, ParagraphAlignment.CENTER, false),
                new POIField(outcomes, ParagraphAlignment.LEFT, false),
                new POIField(subIdos, ParagraphAlignment.LEFT, false),
                new POIField(poiSummary.replaceHTMLTags(narrative), ParagraphAlignment.LEFT, false),
                new POIField(milestone, ParagraphAlignment.LEFT, false),
                new POIField(milestoneStatus, ParagraphAlignment.CENTER, false),
                new POIField(evidenceMilestone, ParagraphAlignment.LEFT, true),
                new POIField(evidence, ParagraphAlignment.LEFT, true)};
              data = Arrays.asList(sData);
              datas.add(data);
            }
          }
        }
      }
    }
    poiSummary.textTable(document, headers, datas, false, "table4AnnualReport2018");
  }

  public void createTable6() {
    List<List<POIField>> headers = new ArrayList<>();
    String blackColor = "000000";

    POIField[] sHeader = {new POIField("", ParagraphAlignment.CENTER, true, blackColor),
      new POIField(this.getText("summaries.annualReport2018.table6Title4"), ParagraphAlignment.CENTER, true,
        blackColor),
      new POIField(this.getText("summaries.annualReport2018.table6Title5"), ParagraphAlignment.CENTER, true,
        blackColor)};
    List<POIField> header = Arrays.asList(sHeader);
    headers.add(header);

    List<List<POIField>> datas = new ArrayList<>();
    List<POIField> data;

    data = new ArrayList<>();

    int number = 0;
    Double percent = 0.00;
    List<String> titles = new ArrayList<>();
    titles.add(this.getText("summaries.annualReport2018.table6Title1"));
    titles.add(this.getText("summaries.annualReport2018.table6Title2"));
    titles.add(this.getText("summaries.annualReport2018.table6Title3"));
    int i = 0;
    for (String title : titles) {
      if (total != 0) {
        switch (i) {
          case 0:
            number = total;
            percent = 100.00;
            break;
          case 1:
            number = totalOpenAccess;
            if (number != 0) {
              percent = (Double.valueOf(number) * 100) / total;
            } else {
              percent = 0.00;
            }
            break;
          case 2:

            number = totalIsis;
            if (number != 0) {
              percent = (Double.valueOf(number) * 100) / total;
            } else {
              percent = 0.00;
            }
            break;
        }
      }
      percent = new BigDecimal(percent).setScale(2, RoundingMode.HALF_UP).doubleValue();
      Boolean bold = true;
      POIField[] sData = {new POIField(title, ParagraphAlignment.CENTER, true, blackColor),
        new POIField(number + "", ParagraphAlignment.LEFT, bold, blackColor),
        new POIField(percent + "%", ParagraphAlignment.LEFT, bold, blackColor)};
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
        ParagraphAlignment.CENTER, false),
      new POIField(this.getText("summaries.annualReport2018.table7Title2"), ParagraphAlignment.CENTER, false),
      new POIField(this.getText("summaries.annualReport2018.table7Title3"), ParagraphAlignment.CENTER, false)};
    List<POIField> header = Arrays.asList(sHeader);
    headers.add(header);
    String trainees = "";
    String female = "0", male = "0";

    for (int i = 0; i < 3; i++) {
      switch (i) {
        case 0:
          trainees = this.getText("summaries.annualReport2018.table7.field1");
          if (reportSynthesisPMU != null && reportSynthesisPMU.getReportSynthesisCrossCuttingDimension() != null) {
            if (reportSynthesisPMU.getReportSynthesisCrossCuttingDimension().getTraineesShortTermFemale() != null) {
              female =
                reportSynthesisPMU.getReportSynthesisCrossCuttingDimension().getTraineesShortTermFemale().intValue()
                  + "";
            }
            if (reportSynthesisPMU.getReportSynthesisCrossCuttingDimension().getTraineesShortTermMale() != null) {
              male =
                reportSynthesisPMU.getReportSynthesisCrossCuttingDimension().getTraineesShortTermMale().intValue() + "";
            }
          }
          break;
        case 1:
          trainees = this.getText("summaries.annualReport2018.table7.field2");
          if (reportSynthesisPMU != null && reportSynthesisPMU.getReportSynthesisCrossCuttingDimension() != null) {
            if (reportSynthesisPMU.getReportSynthesisCrossCuttingDimension().getTraineesLongTermFemale() != null) {
              female =
                reportSynthesisPMU.getReportSynthesisCrossCuttingDimension().getTraineesLongTermFemale().intValue()
                  + "";
            }
            if (reportSynthesisPMU.getReportSynthesisCrossCuttingDimension().getTraineesLongTermMale() != null) {
              male =
                reportSynthesisPMU.getReportSynthesisCrossCuttingDimension().getTraineesLongTermMale().intValue() + "";
            }
          }
          break;
        case 2:
          trainees = this.getText("summaries.annualReport2018.table7.field3");
          if (reportSynthesisPMU != null && reportSynthesisPMU.getReportSynthesisCrossCuttingDimension() != null) {
            if (reportSynthesisPMU.getReportSynthesisCrossCuttingDimension().getPhdFemale() != null) {
              female = reportSynthesisPMU.getReportSynthesisCrossCuttingDimension().getPhdFemale().intValue() + "";
            }
            if (reportSynthesisPMU.getReportSynthesisCrossCuttingDimension().getPhdMale() != null) {
              male = reportSynthesisPMU.getReportSynthesisCrossCuttingDimension().getPhdMale().intValue() + "";
            }
          }
          break;
      }

      POIField[] sData = {new POIField(trainees, ParagraphAlignment.LEFT, false),
        new POIField(female + "", ParagraphAlignment.CENTER, false),
        new POIField(male + "", ParagraphAlignment.CENTER, false)};
      data = Arrays.asList(sData);
      datas.add(data);
    }

    poiSummary.textTable(document, headers, datas, false, "table3AnnualReport2018");

    // Evidence Link - Visible only if evidence link is indicated in synthesis section
    if (reportSynthesisPMU != null && reportSynthesisPMU.getReportSynthesisCrossCuttingDimension() != null
      && reportSynthesisPMU.getReportSynthesisCrossCuttingDimension().getEvidenceLink() != null
      && !reportSynthesisPMU.getReportSynthesisCrossCuttingDimension().getEvidenceLink().isEmpty()) {
      String link = reportSynthesisPMU.getReportSynthesisCrossCuttingDimension().getEvidenceLink();

      // Convert evidence link to html link format if its has the right structure
      if (link.contains("http") && link.contains("://")) {
        link = "<a href=\"" + reportSynthesisPMU.getReportSynthesisCrossCuttingDimension().getEvidenceLink() + "\">"
          + reportSynthesisPMU.getReportSynthesisCrossCuttingDimension().getEvidenceLink() + "</a>";
      }

      // One space between table and evidence link text
      poiSummary.textLineBreak(document, 1);
      poiSummary.convertHTMLTags(document, "Evidence Link: " + link, null);
    }
  }

  private void createTable8() {
    List<List<POIField>> headers = new ArrayList<>();
    List<List<POIField>> datas = new ArrayList<>();
    List<POIField> data;
    if (this.isEntityCRP() == true) {
      POIField[] sHeader = {
        new POIField(this.getText("summaries.annualReportCRP2018.table8Title1",
          new String[] {String.valueOf(this.getSelectedYear())}), ParagraphAlignment.CENTER, false),
        new POIField(this.getText("summaries.annualReport2018.table8Title2"), ParagraphAlignment.CENTER, false),
        new POIField(this.getText("summaries.annualReport2018.table8Title3"), ParagraphAlignment.CENTER, false),
        new POIField(this.getText("summaries.annualReport2018.table8Title4"), ParagraphAlignment.CENTER, false)};
      List<POIField> header = Arrays.asList(sHeader);
      headers.add(header);
    }

    if (this.isEntityPlatform() == true) {
      POIField[] sHeader = {
        new POIField(this.getText("summaries.annualReportCRP2018.moduleLead",
          new String[] {String.valueOf(this.getSelectedYear())}), ParagraphAlignment.CENTER, false),
        new POIField(this.getText("summaries.annualReport2018.table8Title2"), ParagraphAlignment.CENTER, false),
        new POIField(this.getText("summaries.annualReport2018.table8Title3"), ParagraphAlignment.CENTER, false),
        new POIField(this.getText("summaries.annualReport2018.table8Title4"), ParagraphAlignment.CENTER, false)};
      List<POIField> header = Arrays.asList(sHeader);
      headers.add(header);
    }

    for (ReportSynthesisKeyPartnershipExternal flagshipExternalPartnership : externalPartnerships) {
      String leadFP = "", description = "", keyPartners = "", mainArea = "", documentationLink = "";

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
          description = flagshipExternalPartnership.getDescription() + "\n";
        }

        // Load File
        if (flagshipExternalPartnership.getFile() != null) {
          if (flagshipExternalPartnership.getFile().getId() != null) {
            flagshipExternalPartnership
              .setFile(fileDBManager.getFileDBById(flagshipExternalPartnership.getFile().getId()));
          }
        }

        // Get link of external partnerships document
        if (flagshipExternalPartnership.getFile() != null && flagshipExternalPartnership.getFile().getFileName() != null
          && !flagshipExternalPartnership.getFile().getFileName().isEmpty()) {
          documentationLink = config.getBaseUrl() + "/annualReport2018/" + this.getCrpSession()
            + "/downloadExternalPartnershipsFile.do?filename=" + flagshipExternalPartnership.getFile().getFileName()
            + "&crp=" + this.getCrpSession();
          // description += "\n •Document link (BETA): " + documentationLink;

        }

        if (flagshipExternalPartnership.getInstitutions() != null) {
          for (ReportSynthesisKeyPartnershipExternalInstitution institution : flagshipExternalPartnership
            .getInstitutions()) {
            if (institution != null && institution.getInstitution() != null
              && institution.getInstitution().getComposedName() != null) {
              keyPartners += "• " + institution.getInstitution().getComposedName() + " \n";
            }
          }
        }

        if (flagshipExternalPartnership.getMainAreas() != null) {
          for (ReportSynthesisKeyPartnershipExternalMainArea externalMainArea : flagshipExternalPartnership
            .getMainAreas()) {
            if (externalMainArea != null && externalMainArea.getPartnerArea() != null
              && externalMainArea.getPartnerArea().getName() != null) {
              mainArea += "• " + externalMainArea.getPartnerArea().getName() + "\n";
            }
          }
        }
      }

      try {
        if (keyPartners.contains(",")) {
          keyPartners = keyPartners.substring(0, keyPartners.length() - 2);
        }
      } catch (Exception e) {

      }

      try {
        if (mainArea.contains(",")) {
          mainArea = mainArea.substring(0, mainArea.length() - 2);
        }
      } catch (Exception e) {

      }
      POIField[] sData =
        {new POIField(leadFP, ParagraphAlignment.LEFT, false), new POIField(description, ParagraphAlignment.LEFT, true),
          new POIField(keyPartners, ParagraphAlignment.LEFT, false),
          new POIField(mainArea, ParagraphAlignment.LEFT, false)};
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
        ParagraphAlignment.CENTER, false),
      new POIField(this.getText("summaries.annualReport2018.table9Title2"), ParagraphAlignment.CENTER, false),
      new POIField(this.getText("summaries.annualReport2018.table9Title3"), ParagraphAlignment.CENTER, false)};
    List<POIField> header = Arrays.asList(sHeader);
    headers.add(header);


    if (collaborations != null) {
      for (ReportSynthesisKeyPartnershipCollaboration collaboration : collaborations) {
        String description = "", name = "", optional = "";
        if (collaboration.getDescription() != null) {
          description = collaboration.getDescription();
        }

        if (collaboration.getReportSynthesisKeyPartnership() != null) {
          for (ReportSynthesisKeyPartnershipCollaborationCrp partner : collaboration
            .getReportSynthesisKeyPartnershipCollaborationCrps()) {
            name += partner.getGlobalUnit().getAcronym() + ", ";
          }
        }

        if (collaboration.getValueAdded() != null) {
          optional = collaboration.getValueAdded();
        }

        try {
          if (name.contains(",")) {
            name = name.substring(0, name.length() - 2);
          }
        } catch (Exception e) {

        }

        POIField[] sData = {new POIField(description, ParagraphAlignment.LEFT, true),
          new POIField(poiSummary.replaceHTMLTags(name), ParagraphAlignment.CENTER, false),
          new POIField(optional, ParagraphAlignment.LEFT, true)};
        data = Arrays.asList(sData);
        datas.add(data);
      }
    }

    poiSummary.textTable(document, headers, datas, false, "table3AnnualReport2018");
  }

  private void createTableAnnexes() {

    List<List<POIField>> headers = new ArrayList<>();
    List<List<POIField>> datas = new ArrayList<>();
    List<POIField> data;
    POIField[] sHeader = {
      new POIField(this.getText("summaries.annualReport2018.tableAnnexesTitle1",
        new String[] {String.valueOf(this.getSelectedYear())}), ParagraphAlignment.CENTER, false),
      new POIField(this.getText("summaries.annualReport2018.tableAnnexesTitle2"), ParagraphAlignment.CENTER, false)};
    List<POIField> header = Arrays.asList(sHeader);
    headers.add(header);

    // Flagship - Synthesis

    flagshipsReportSynthesisFlagshipProgress = reportSynthesisFlagshipProgressManager
      .getFlagshipsReportSynthesisFlagshipProgress(flagshipLiaisonInstitutions, this.getSelectedPhase().getId());

    if (flagshipsReportSynthesisFlagshipProgress != null) {
      int i = 0;
      for (ReportSynthesisFlagshipProgress flagshipProgress : flagshipsReportSynthesisFlagshipProgress) {


        String FP = "", annex = "";
        if (flagshipProgress.getDetailedAnnex() != null) {
          annex = flagshipProgress.getDetailedAnnex();
        }

        if (this.isEntityPlatform()) {
          FP = flagships.get(i).getComposedName();
        } else {
          FP = flagships.get(i).getComposedName();
        }

        i++;

        POIField[] sData = {new POIField(FP, ParagraphAlignment.CENTER, true),
          new POIField(poiSummary.replaceHTMLTags(annex), ParagraphAlignment.CENTER, true)};
        data = Arrays.asList(sData);
        datas.add(data);
      }
    }

    poiSummary.textTable(document, headers, datas, false, "tableAnnexesAnnualReport2018");
  }

  public String deleteFormatTags(String text) {
    // Add format tags to remove to the list
    List<String> tagsToRemove = new ArrayList<String>();
    tagsToRemove.add("<span");
    tagsToRemove.add("<style");

    // Search tags in the current text
    for (int i = 0; i < tagsToRemove.size(); i++) {
      if (text.contains(tagsToRemove.get(i))) {

        // Search for the position of the tag in the text
        int finalTagPos = 0;
        String tagText = "";
        for (int j = 0; j < text.length(); j++) {
          if (text.substring(j, j + tagsToRemove.get(i).length() - 1).equals(tagsToRemove.get(j))) {

            // Detect the close part of the tag
            finalTagPos = text.indexOf(">", j);
            if (finalTagPos != 0) {
              tagText = text.substring(j, finalTagPos);
              text = text.replaceAll(tagText, "");
              j = j + tagsToRemove.get(i).length();
            }
          }
        }
      }
    }
    return text;
  }


  public String deleteSpanTags(String text) {
    text = text.replaceAll("<span style=\"color: rgb(130, 130, 130); font-size: 0.98em;\">", "");
    text = text.replaceAll("<span style=\"color: rgb(130, 130, 130); font-size: 0.98em;", "");
    text = text.replaceAll("</span>", "");
    return text;
  }


  @Override
  public String execute() throws Exception {
    if (this.getSelectedPhase() == null || this.getSelectedPhase().getId() == null) {
      return NOT_FOUND;
    }

    // Toc section
    addCustomHeadingStyle(document, "headingTitle1", 1);
    addCustomHeadingStyle(document, "headingTitle0", 1);
    addCustomHeadingStyle(document, "headingTitle2", 1);
    addCustomHeadingStyle(document, "headingTitle3", 2);
    addCustomHeadingStyle(document, "headingTitle4", 3);
    addCustomHeadingStyle(document, "headingTitle5", 3);
    addCustomHeadingStyle(document, "headingTitle6", 4);
    addCustomHeadingStyle(document, "headingTitle7", 4);
    addCustomHeadingStyle(document, "headingTitle8", 4);
    addCustomHeadingStyle(document, "headingTitle9", 4);
    addCustomHeadingStyle(document, "heading 10", 3);
    addCustomHeadingStyle(document, "heading 11", 4);
    addCustomHeadingStyle(document, "heading 12", 4);
    addCustomHeadingStyle(document, "heading 13", 4);
    addCustomHeadingStyle(document, "heading 14", 4);
    addCustomHeadingStyle(document, "heading 15", 2);
    addCustomHeadingStyle(document, "heading 16", 3);
    addCustomHeadingStyle(document, "heading 17", 3);
    addCustomHeadingStyle(document, "heading 18", 4);
    addCustomHeadingStyle(document, "heading 19", 4);
    addCustomHeadingStyle(document, "heading 20", 3);
    addCustomHeadingStyle(document, "heading 21", 3);
    addCustomHeadingStyle(document, "heading 22", 3);
    addCustomHeadingStyle(document, "heading 23", 3);
    addCustomHeadingStyle(document, "heading 24", 3);
    addCustomHeadingStyle(document, "heading 25", 2);
    addCustomHeadingStyle(document, "headingTitleB", 1);
    addCustomHeadingStyle(document, "heading 26", 2);
    addCustomHeadingStyle(document, "heading 27", 2);
    addCustomHeadingStyle(document, "heading 28", 2);
    addCustomHeadingStyle(document, "heading 29", 2);
    addCustomHeadingStyle(document, "heading 30", 2);
    addCustomHeadingStyle(document, "heading 31", 2);
    addCustomHeadingStyle(document, "heading 32", 2);
    addCustomHeadingStyle(document, "heading 33", 2);
    addCustomHeadingStyle(document, "heading 34", 2);
    addCustomHeadingStyle(document, "heading 35", 2);
    addCustomHeadingStyle(document, "heading 36", 2);
    addCustomHeadingStyle(document, "heading 37", 2);
    addCustomHeadingStyle(document, "heading 38", 2);
    addCustomHeadingStyle(document, "heading 39", 2);
    if (this.isEntityCRP()) {
      try {

        CTDocument1 doc = document.getDocument();
        CTBody body = doc.getBody();
        poiSummary.pageHeaderCenter(document,
          this.getSelectedYear() + " " + this.getText("summaries.annualReportCRP2019.headerA"));

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
          this.getSelectedYear() + " " + this.getText("summaries.annualReportCRP.mainTitle"));
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

        // First page
        document.createParagraph().setPageBreak(true);
        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.setFontSize(14);
        run.setBold(true);
        run.setText(this.getText("summaries.annualReportCRP2018.cover"));
        paragraph.setStyle("headingTitle1");

        String unitName = this.getLoggedCrp().getAcronym() != null && !this.getLoggedCrp().getAcronym().isEmpty()
          ? this.getLoggedCrp().getAcronym() : this.getLoggedCrp().getName();
        poiSummary.textParagraph(document.createParagraph(),
          this.getText("summaries.annualReportCRP2018.unitName") + ": " + unitName);
        poiSummary.textParagraph(document.createParagraph(), this.getText("summaries.annualReport.LeadCenter") + ": "
          + this.getLoggedCrp().getInstitution().getAcronymName());

        // Flagships lead institutions
        poiSummary.textParagraph(document.createParagraph(), this.getText("summaries.powb2019.flagshipLeadInst"));
        run.addTab();

        if (flagshipLiaisonInstitutions != null) {
          for (int i = 0; i < flagshipLiaisonInstitutions.size(); i++) {

            poiSummary.textParagraph(document.createParagraph(),
              "         " + this.getText("summaries.powb2019.flagShip") + " " + (i + 1) + ": "
                + flagshipLiaisonInstitutions.get(i).getName());
          }
        }

        this.addParticipatingCenters();

        // Part A - Narrative section
        // Executive Summary
        poiSummary.textLineBreak(document, 1);
        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.setFontSize(14);
        run.setColor("063970");
        run.setBold(true);
        run.setText(this.getText("summaries.annualReportCRP2018.executiveSummary"));
        this.addNarrativeSection();
        paragraph.setStyle("headingTitle0");

        // First page
        poiSummary.textLineBreak(document, 1);
        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.setFontSize(14);
        run.setBold(true);
        run.setText(this.getText("summaries.annualReportCRP2018.narrative"));
        paragraph.setStyle("headingTitle2");

        // section 1 - Key Results
        // poiSummary.textLineBreak(document, 1);
        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.setFontSize(14);
        run.setBold(true);
        run.setText(this.getText("summaries.annualReport.keyResults"));
        paragraph.setStyle("headingTitle3");

        // 1.1 Progress Towards SDG and SLO
        // poiSummary.textLineBreak(document, 1);
        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.setFontSize(13);
        run.setBold(true);
        run.setText(this.getText("summaries.annualReport2018.keyResults.crpProgress"));
        this.addExpectedCrp();
        paragraph.setStyle("headingTitle4");

        // 1.2 CRP progress towars outputs and outcomes
        poiSummary.textLineBreak(document, 1);
        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.setFontSize(14);
        run.setBold(true);
        run.setText(this.getText("summaries.annualReport2018.keyResults.progressTowards"));
        paragraph.setStyle("headingTitle5");

        // 1.2.1
        // poiSummary.textLineBreak(document, 1);
        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.setFontSize(11);
        run.setBold(true);
        run.setText(this.getText("summaries.annualReport2018.keyResults.overall"));
        this.addOverallProgressCrp();
        paragraph.setStyle("headingTitle6");

        // 1.2.2
        poiSummary.textLineBreak(document, 1);
        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.setFontSize(11);
        run.setBold(true);
        run.setText(this.getText("summaries.annualReport2018.keyResults.progress"));
        this.getProgressByFlagships();
        paragraph.setStyle("headingTitle7");

        // 1.2.3
        poiSummary.textLineBreak(document, 1);
        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.setFontSize(11);
        run.setBold(true);
        run.setText(this.getText("summaries.annualReport2018.keyResults.variance"));
        this.addVariancePlanned();
        paragraph.setStyle("headingTitle8");

        // 1.2.4
        poiSummary.textLineBreak(document, 1);
        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.setFontSize(11);
        run.setBold(true);
        run.setText(this.getText("summaries.annualReport2018.keyResults.altmetric"));
        this.addAlmetricCrp();
        paragraph.setStyle("headingTitle9");

        // 1.3 Cross cutting dimensions
        poiSummary.textLineBreak(document, 1);
        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.setFontSize(13);
        run.setBold(true);
        run.setText(this.getText("summaries.annualReport2018.crossCutting"));
        paragraph.setStyle("heading 10");

        // 1.3.1 Gender
        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.setFontSize(11);
        run.setBold(true);
        run.setText(this.getText("summaries.annualReport2018.crossCutting.gender"));
        this.addCrossCuttingGender();
        paragraph.setStyle("heading 11");

        // 1.3.2 Youth
        poiSummary.textLineBreak(document, 1);
        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.setFontSize(11);
        run.setBold(true);
        run.setText(this.getText("summaries.annualReport2018.crossCutting.youth"));
        this.addCrossCuttingYouth();
        paragraph.setStyle("heading 12");

        // 1.3.3 Capdev
        poiSummary.textLineBreak(document, 1);
        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.setFontSize(11);
        run.setBold(true);
        run.setText(this.getText("summaries.annualReport2018.crossCutting.capacity"));
        this.addCrossCuttingCapacityDevelopment();
        paragraph.setStyle("heading 13");

        // 1.3.4 Climate Change
        poiSummary.textLineBreak(document, 1);
        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.setFontSize(11);
        run.setBold(true);
        run.setText(this.getText("summaries.annualReport2018.crossCutting.climateChange"));
        this.addCrossCuttingClimateChange();
        paragraph.setStyle("heading 14");

        // section 2 - Effectiveness and Efficiency
        poiSummary.textLineBreak(document, 1);
        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.setFontSize(14);
        run.setBold(true);
        run.setText(this.getText("summaries.annualReport2018.effectiveness"));
        paragraph.setStyle("heading 15");

        // 2.1 Management and governance
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

        // 2.2.1 Highlights of External Partnerships
        // poiSummary.textLineBreak(document, 1);
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
        // poiSummary.textLineBreak(document, 1);
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

        // Part B
        document.createParagraph().setPageBreak(true);
        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.setFontSize(16);
        run.setBold(true);
        run.setText("Part B. TABLES");
        paragraph.setStyle("headingTitleB");


        // Table 1
        poiSummary.textLineBreak(document, 1);
        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.setFontSize(13);
        run.setBold(true);
        run.setText(this.getText("summaries.annualReport2018.table1"));
        paragraph.setStyle("heading 26");
        this.createTable1();

        // Table 2
        document.createParagraph().setPageBreak(true);
        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.setFontSize(13);
        run.setBold(true);
        run.setText(this.getText("summaries.annualReport2018.table2"));
        paragraph.setStyle("heading 27");
        this.createTable2();

        // Table 3
        document.createParagraph().setPageBreak(true);
        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.setFontSize(13);
        run.setBold(true);
        run.setText(this.getText("summaries.annualReport2018.table3"));
        paragraph.setStyle("heading 28");
        this.createTable3();

        // Table 4
        document.createParagraph().setPageBreak(true);
        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.setFontSize(13);
        run.setBold(true);
        run.setText(this.getText("summaries.annualReport2018.table4"));
        paragraph.setStyle("heading 29");
        this.createTable4();

        // Table 5
        document.createParagraph().setPageBreak(true);
        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.setFontSize(13);
        run.setBold(true);
        run.setText(this.getText("summaries.annualReport2018.table5"));
        paragraph.setStyle("heading 30");
        this.createTable5();

        // Table 6
        document.createParagraph().setPageBreak(true);
        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.setFontSize(13);
        run.setBold(true);
        run.setText(this.getText("summaries.annualReport2018.table6"));
        paragraph.setStyle("heading 31");
        this.createTable6();

        // Table 7
        document.createParagraph().setPageBreak(true);
        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.setFontSize(13);
        run.setBold(true);
        run.setText(this.getText("summaries.annualReport2018.table7"));
        paragraph.setStyle("heading 32");
        this.createTable7();

        // Table 8
        document.createParagraph().setPageBreak(true);
        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.setFontSize(13);
        run.setBold(true);
        run.setText(this.getText("summaries.annualReport2018.table8"));
        paragraph.setStyle("heading 33");
        this.createTable8();

        // Table 9
        document.createParagraph().setPageBreak(true);
        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.setFontSize(13);
        run.setBold(true);
        run.setText(this.getText("summaries.annualReport2018.table9"));
        paragraph.setStyle("heading 34");
        this.createTable9();

        // Table 10
        document.createParagraph().setPageBreak(true);
        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.setFontSize(13);
        run.setBold(true);
        run.setText(this.getText("summaries.annualReport2018.table10"));
        paragraph.setStyle("heading 35");
        this.createTable10();

        // Table 11
        document.createParagraph().setPageBreak(true);
        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.setFontSize(13);
        run.setBold(true);
        run.setText(this.getText("summaries.annualReport2018.table11"));
        paragraph.setStyle("heading 36");
        this.createTable11();

        // Table 12
        document.createParagraph().setPageBreak(true);
        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.setFontSize(13);
        run.setBold(true);
        run.setText(this.getText("summaries.annualReport2019.table12A") + " (" + this.getSelectedYear() + ")");
        paragraph.setStyle("heading 37");
        this.createTable12();

        // Table 13
        document.createParagraph().setPageBreak(true);
        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.setFontSize(13);
        run.setBold(true);
        run.setText(this.getText("summaries.annualReport2018.table13"));
        paragraph.setStyle("heading 38");
        if (this.getSelectedPhase().getName().equals("AR") && this.getSelectedPhase().getYear() == 2021) {
          this.createTable13AR2021();
        } else {
          this.createTable13();
        }

        // Part C
        document.createParagraph().setPageBreak(true);
        poiSummary.textHead1Title(document.createParagraph(), "Annexes");

        // Table Annexes
        // document.createParagraph().setPageBreak(true);
        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.setFontSize(13);
        run.setBold(true);
        run.setText(this.getText("summaries.annualReport2018.tableAnnexes"));
        paragraph.setStyle("heading 39");
        this.createTableAnnexes();


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
        poiSummary.pageHeader(document,
          this.getSelectedYear() + " " + this.getText("summaries.annualReportPlatform2019.headerA"));

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
          this.getSelectedYear() + " " + this.getText("summaries.annualReportPlatform2019.mainTitle2"));
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
        poiSummary.textParagraph(document.createParagraph(), this.getText("summaries.annualReport.LeadCenter") + ": "
          + this.getLoggedCrp().getInstitution().getAcronymName());
        // Flagships lead institutions
        poiSummary.textParagraph(document.createParagraph(),
          this.getText("summaries.annualReportCRP2018Platform.Flagship"));
        run.addTab();

        if (flagshipLiaisonInstitutions != null) {
          for (int i = 0; i < flagshipLiaisonInstitutions.size(); i++) {

            poiSummary.textParagraph(document.createParagraph(),
              "         " + this.getText("summaries.annualReportCRP2018.module") + " " + (i + 1) + ": "
                + flagshipLiaisonInstitutions.get(i).getName());
          }
        }
        this.addParticipatingCenters();

        // Part A - Narrative section
        // Executive Summary
        poiSummary.textLineBreak(document, 1);
        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.setFontSize(14);
        run.setColor("063970");
        run.setBold(true);
        run.setText(this.getText("summaries.annualReportCRP2018.executiveSummary"));
        this.addNarrativeSection();
        paragraph.setStyle("headingTitle0");

        // First page
        poiSummary.textLineBreak(document, 1);
        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.setFontSize(14);
        run.setBold(true);
        run.setText(this.getText("summaries.annualReportCRP2018.narrative"));
        paragraph.setStyle("headingTitle2");

        // section 1 - Key Results
        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.setFontSize(14);
        run.setBold(true);
        run.setText(this.getText("summaries.annualReport.keyResults"));
        paragraph.setStyle("headingTitle3");

        // 1.1 Progress Towards SDG and SLO
        // poiSummary.textLineBreak(document, 1);
        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.setFontSize(13);
        run.setBold(true);
        run.setText(this.getText("summaries.annualReport2018Platform.keyResults.crpProgress"));
        this.addExpectedCrp();
        paragraph.setStyle("headingTitle4");

        // 1.2 CRP progress towars outputs and outcomes
        poiSummary.textLineBreak(document, 1);
        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.setFontSize(14);
        run.setBold(true);
        run.setText(this.getText("summaries.annualReport2018Platform.keyResults.progressTowards"));
        paragraph.setStyle("headingTitle5");

        // 1.2.1
        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.setFontSize(11);
        run.setBold(true);
        run.setText(this.getText("summaries.annualReport2018Platform.keyResults.overall"));
        this.addOverallProgressCrp();
        paragraph.setStyle("headingTitle6");

        // 1.2.2
        // poiSummary.textLineBreak(document, 1);
        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.setFontSize(11);
        run.setBold(true);
        run.setText(this.getText("summaries.annualReport2018Platform.keyResults.progress"));
        this.getProgressByFlagships();
        paragraph.setStyle("headingTitle7");

        // 1.2.3
        poiSummary.textLineBreak(document, 1);
        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.setFontSize(11);
        run.setBold(true);
        run.setText(this.getText("summaries.annualReport2018.keyResults.variance"));
        this.addVariancePlanned();
        paragraph.setStyle("headingTitle8");

        // 1.2.4
        poiSummary.textLineBreak(document, 1);
        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.setFontSize(11);
        run.setBold(true);
        run.setText(this.getText("summaries.annualReport2018.keyResults.altmetric"));
        this.addAlmetricCrp();
        paragraph.setStyle("headingTitle9");

        // 1.3 Cross cutting dimensions
        poiSummary.textLineBreak(document, 1);
        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.setFontSize(13);
        run.setBold(true);
        run.setText(this.getText("summaries.annualReport2018Platform.crossCutting"));
        paragraph.setStyle("heading 10");

        // 1.3.1 Gender
        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.setFontSize(11);
        run.setBold(true);
        run.setText(this.getText("summaries.annualReport2018.crossCutting.gender"));
        this.addCrossCuttingGender();
        paragraph.setStyle("heading 11");

        // 1.3.2 Youth
        poiSummary.textLineBreak(document, 1);
        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.setFontSize(11);
        run.setBold(true);
        run.setText(this.getText("summaries.annualReport2018.crossCutting.youth"));
        this.addCrossCuttingYouth();
        paragraph.setStyle("heading 12");

        // 1.3.3 Capdev
        poiSummary.textLineBreak(document, 1);
        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.setFontSize(11);
        run.setBold(true);
        run.setText(this.getText("summaries.annualReport2018.crossCutting.capacity"));
        this.addCrossCuttingCapacityDevelopment();
        paragraph.setStyle("heading 13");

        // 1.3.4 Climate Change
        poiSummary.textLineBreak(document, 1);
        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.setFontSize(11);
        run.setBold(true);
        run.setText(this.getText("summaries.annualReport2018.crossCutting.climateChange"));
        this.addCrossCuttingClimateChange();
        paragraph.setStyle("heading 14");

        // section 2 - Effectiveness and Efficiency
        poiSummary.textLineBreak(document, 1);
        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.setFontSize(14);
        run.setBold(true);
        run.setText(this.getText("summaries.annualReport2018.effectiveness"));
        paragraph.setStyle("heading 15");

        // 2.1 Management and governance
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

        // 2.2.1 Highlights of External Partnerships
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

        // 2.6 Management of Risk
        poiSummary.textLineBreak(document, 1);
        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.setFontSize(13);
        run.setBold(true);
        run.setText(this.getText("summaries.annualReport2018Platform.effectiveness.risk"));
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

        // Part B
        document.createParagraph().setPageBreak(true);
        poiSummary.textHead1Title(document.createParagraph(), "Part B. TABLES");

        // Table 2
        document.createParagraph().setPageBreak(true);
        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.setFontSize(13);
        run.setBold(true);
        run.setText(this.getText("summaries.annualReport2018.table2"));
        paragraph.setStyle("heading 27");
        this.createTable2();

        // Table 3
        document.createParagraph().setPageBreak(true);
        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.setFontSize(13);
        run.setBold(true);
        run.setText(this.getText("summaries.annualReport2018.table3"));
        paragraph.setStyle("heading 28");
        this.createTable3();

        // Table 4
        document.createParagraph().setPageBreak(true);
        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.setFontSize(13);
        run.setBold(true);
        run.setText(this.getText("summaries.annualReport2018.table4"));
        paragraph.setStyle("heading 29");
        this.createTable4();

        // Table 5
        document.createParagraph().setPageBreak(true);
        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.setFontSize(13);
        run.setBold(true);
        run.setText(this.getText("summaries.annualReport2018.table5"));
        paragraph.setStyle("heading 30");
        this.createTable5();

        // Table 6
        document.createParagraph().setPageBreak(true);
        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.setFontSize(13);
        run.setBold(true);
        run.setText(this.getText("summaries.annualReport2018Platform.table6"));
        paragraph.setStyle("heading 31");
        this.createTable6();

        // Table 7
        document.createParagraph().setPageBreak(true);
        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.setFontSize(13);
        run.setBold(true);
        run.setText(this.getText("summaries.annualReport2018.table7"));
        paragraph.setStyle("heading 32");
        this.createTable7();

        // Table 8
        document.createParagraph().setPageBreak(true);
        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.setFontSize(13);
        run.setBold(true);
        run.setText(this.getText("summaries.annualReport2018.table8"));
        paragraph.setStyle("heading 33");
        this.createTable8();

        // Table 9
        document.createParagraph().setPageBreak(true);
        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.setFontSize(13);
        run.setBold(true);
        run.setText(this.getText("summaries.annualReport2018.table9"));
        paragraph.setStyle("heading 34");
        this.createTable9();

        // Table 10
        document.createParagraph().setPageBreak(true);
        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.setFontSize(13);
        run.setBold(true);
        run.setText(this.getText("summaries.annualReport2018.table10"));
        paragraph.setStyle("heading 35");
        this.createTable10();

        // Table 11
        document.createParagraph().setPageBreak(true);
        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.setFontSize(13);
        run.setBold(true);
        run.setText(this.getText("summaries.annualReport2018.table11"));
        paragraph.setStyle("heading 36");
        this.createTable11();

        // Table 12
        document.createParagraph().setPageBreak(true);
        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.setFontSize(13);
        run.setBold(true);
        run.setText(this.getText("summaries.annualReport2019.table12"));
        paragraph.setStyle("heading 37");
        this.createTable12();

        // Table 13
        document.createParagraph().setPageBreak(true);
        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.setFontSize(13);
        run.setBold(true);
        run.setText(this.getText("summaries.annualReport2018Platform.table13"));
        paragraph.setStyle("heading 38");
        if (this.getSelectedPhase().getName().equals("AR") && this.getSelectedPhase().getYear() == 2021) {
          this.createTable13AR2021();
        } else {
          this.createTable13();
        }


        // Part C
        document.createParagraph().setPageBreak(true);
        poiSummary.textHead1Title(document.createParagraph(), "Part C. Annexes");

        // Table Annexes
        // document.createParagraph().setPageBreak(true);
        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.setFontSize(13);
        run.setBold(true);
        run.setText(this.getText("summaries.annualReport2018.tableAnnexes"));
        paragraph.setStyle("heading 39");
        this.createTableAnnexes();


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

  private void fillProjectPoliciesTable2List() {
    projectPoliciesTable2 =
      new LinkedHashSet<>(projectPolicyManager.getProjectPoliciesList(pmuInstitution, this.getSelectedPhase()));

    if (reportSynthesisPMU != null && reportSynthesisPMU.getReportSynthesisFlagshipProgress() != null
      && reportSynthesisPMU.getReportSynthesisFlagshipProgress().getReportSynthesisFlagshipProgressPolicies() != null
      && !reportSynthesisPMU.getReportSynthesisFlagshipProgress().getReportSynthesisFlagshipProgressPolicies()
        .isEmpty()) {
      for (ReportSynthesisFlagshipProgressPolicy flagshipProgressPolicy : reportSynthesisPMU
        .getReportSynthesisFlagshipProgress().getReportSynthesisFlagshipProgressPolicies().stream()
        .filter(ro -> ro.isActive()).collect(Collectors.toList())) {
        projectPoliciesTable2.remove(flagshipProgressPolicy.getProjectPolicy());
      }
    }

  }


  public void fillProjectsInnovationsTable4List() {
    projectInnovationsTable4 =
      new LinkedHashSet<>(projectInnovationManager.getProjectInnovationsList(pmuInstitution, this.getSelectedPhase()));

    if (reportSynthesisPMU != null && reportSynthesisPMU.getReportSynthesisFlagshipProgress() != null
      && reportSynthesisPMU.getReportSynthesisFlagshipProgress().getReportSynthesisFlagshipProgressInnovations() != null
      && !reportSynthesisPMU.getReportSynthesisFlagshipProgress().getReportSynthesisFlagshipProgressInnovations()
        .isEmpty()) {
      for (ReportSynthesisFlagshipProgressInnovation flagshipProgressInnovation : reportSynthesisPMU
        .getReportSynthesisFlagshipProgress().getReportSynthesisFlagshipProgressInnovations().stream()
        .filter(ro -> ro.isActive()).collect(Collectors.toList())) {
        projectInnovationsTable4.remove(flagshipProgressInnovation.getProjectInnovation());
      }
    }

  }

  private void fillProjectStudiesTable3List() {

    projectExpectedStudiesTable3 =
      new LinkedHashSet<>(projectExpectedStudyManager.getProjectStudiesList(pmuInstitution, this.getSelectedPhase()));
    if (reportSynthesisPMU != null && reportSynthesisPMU.getReportSynthesisFlagshipProgress() != null) {
      reportSynthesisPMU.getReportSynthesisFlagshipProgress().setProjectStudies(new ArrayList<>());
    }
    if (reportSynthesisPMU != null && reportSynthesisPMU.getReportSynthesisFlagshipProgress() != null
      && reportSynthesisPMU.getReportSynthesisFlagshipProgress().getReportSynthesisFlagshipProgressStudies() != null
      && !reportSynthesisPMU.getReportSynthesisFlagshipProgress().getReportSynthesisFlagshipProgressStudies()
        .isEmpty()) {
      for (ReportSynthesisFlagshipProgressStudy flagshipProgressStudy : reportSynthesisPMU
        .getReportSynthesisFlagshipProgress().getReportSynthesisFlagshipProgressStudies().stream()
        .filter(ro -> ro.isActive()).collect(Collectors.toList())) {
        reportSynthesisPMU.getReportSynthesisFlagshipProgress().getProjectStudies()
          .add(flagshipProgressStudy.getProjectExpectedStudy());
        projectExpectedStudiesTable3.remove(flagshipProgressStudy.getProjectExpectedStudy());
      }
    }

  }


  public void flagshipExternalPartnerships(List<LiaisonInstitution> flagshipliaisonInstitutions) {

    flagshipExternalPartnerships = new ArrayList<>();

    for (LiaisonInstitution liaisonInstitution : flagshipliaisonInstitutions) {
      ReportSynthesis reportSynthesisFP = null;
      try {
        reportSynthesisFP =
          reportSynthesisManager.findSynthesis(this.getSelectedPhase().getId(), liaisonInstitution.getId());
      } catch (Exception e) {

      }
      if (reportSynthesisFP != null) {
        if (reportSynthesisFP.getReportSynthesisKeyPartnership() != null) {
          if (reportSynthesisFP.getReportSynthesisKeyPartnership()
            .getReportSynthesisKeyPartnershipExternals() != null) {

            List<ReportSynthesisKeyPartnershipExternal> externals = new ArrayList<>(
              reportSynthesisFP.getReportSynthesisKeyPartnership().getReportSynthesisKeyPartnershipExternals().stream()
                .filter(c -> c.isActive()).collect(Collectors.toList()));

            for (ReportSynthesisKeyPartnershipExternal external : externals) {

              if (external != null && external.getReportSynthesisKeyPartnershipExternalInstitutions() != null
                && !external.getReportSynthesisKeyPartnershipExternalInstitutions().isEmpty()) {
                external.setInstitutions(new ArrayList<>(external.getReportSynthesisKeyPartnershipExternalInstitutions()
                  .stream().filter(c -> c.isActive()).collect(Collectors.toList())));
              }

              if (external != null && external.getReportSynthesisKeyPartnershipExternalMainAreas() != null
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
      reportSynthesisManager.findSynthesis(this.getSelectedPhase().getId(), inst.getId());


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

  private void getProgressByFlagships() {
    // Check if relation is null -create it
    if (reportSynthesisPMU != null && reportSynthesisPMU.getReportSynthesisFlagshipProgress() == null) {
      ReportSynthesisFlagshipProgress flagshipProgress = new ReportSynthesisFlagshipProgress();
      // create one to one relation
      reportSynthesisPMU.setReportSynthesisFlagshipProgress(flagshipProgress);
      flagshipProgress.setReportSynthesis(reportSynthesisPMU);
    }

    // Flagship - Synthesis

    flagshipsReportSynthesisFlagshipProgress = reportSynthesisFlagshipProgressManager
      .getFlagshipsReportSynthesisFlagshipProgress(flagshipLiaisonInstitutions, this.getSelectedPhase().getId());

    if (flagshipsReportSynthesisFlagshipProgress != null) {
      int i = 1;
      for (ReportSynthesisFlagshipProgress flagshipProgress : flagshipsReportSynthesisFlagshipProgress) {

        if (flagshipProgress.getProgressByFlagships() != null) {
          if (this.isEntityCRP()) {
            this.createSubtitle("F" + i + " - Flagship progress:");
          } else {
            this.createSubtitle("M" + i + " - Module progress:");
          }
          try {
            poiSummary.convertHTMLTags(document, flagshipProgress.getProgressByFlagships(), null);
          } catch (Exception e) {
            poiSummary.convertHTMLTags(document, poiSummary.replaceHTMLTags(flagshipProgress.getProgressByFlagships()),
              null);
          }
          // poiSummary.textLineBreak(document, 1);
        }

        if (flagshipProgress.getDetailedAnnex() != null && !flagshipProgress.getDetailedAnnex().isEmpty()) {
          this.createSubtitle("Detailed Annex:");
          try {
            poiSummary.convertHTMLTags(document, flagshipProgress.getDetailedAnnex(), null);
          } catch (Exception e) {
            poiSummary.convertHTMLTags(document, poiSummary.replaceHTMLTags(flagshipProgress.getDetailedAnnex()), null);
          }
          // poiSummary.textLineBreak(document, 1);
        }

        if (flagshipProgress.getRelevanceCovid() != null && !flagshipProgress.getRelevanceCovid().isEmpty()) {
          this.createSubtitle("Relevance to Covid-19:");
          try {
            poiSummary.convertHTMLTags(document, flagshipProgress.getRelevanceCovid(), null);
          } catch (Exception e) {
            poiSummary.convertHTMLTags(document, poiSummary.replaceHTMLTags(flagshipProgress.getRelevanceCovid()),
              null);
          }
        }
        i++;
      }
    }
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

  public void getTable10Info() {

    meliaDto = reportSynthesisMeliaManager.getTable10(flagshipLiaisonInstitutions, this.getSelectedPhase().getId(),
      this.getLoggedCrp(), pmuInstitution);
  }

  private void getTable11Info() {

    if (reportSynthesisPMU != null && reportSynthesisPMU.getReportSynthesisMelia() != null
      && reportSynthesisPMU.getReportSynthesisMelia().getReportSynthesisMeliaEvaluations() != null
      && !reportSynthesisPMU.getReportSynthesisMelia().getReportSynthesisMeliaEvaluations().isEmpty()) {
      reportSynthesisPMU.getReportSynthesisMelia()
        .setEvaluations(new ArrayList<>(reportSynthesisPMU.getReportSynthesisMelia()
          .getReportSynthesisMeliaEvaluations().stream().filter(e -> e.isActive()).collect(Collectors.toList())));
      reportSynthesisPMU.getReportSynthesisMelia().getEvaluations()
        .sort(Comparator.comparing(ReportSynthesisMeliaEvaluation::getId));
    }

    // load evaluation actions
    if (reportSynthesisPMU != null && reportSynthesisPMU.getReportSynthesisMelia() != null
      && reportSynthesisPMU.getReportSynthesisMelia().getEvaluations() != null
      && !reportSynthesisPMU.getReportSynthesisMelia().getEvaluations().isEmpty()) {
      for (ReportSynthesisMeliaEvaluation reportSynthesisMeliaEvaluation : reportSynthesisPMU.getReportSynthesisMelia()
        .getEvaluations()) {
        if (reportSynthesisMeliaEvaluation.getReportSynthesisMeliaEvaluationActions() != null
          && !reportSynthesisMeliaEvaluation.getReportSynthesisMeliaEvaluationActions().isEmpty()) {
          reportSynthesisMeliaEvaluation.setMeliaEvaluationActions(
            new ArrayList<>(reportSynthesisMeliaEvaluation.getReportSynthesisMeliaEvaluationActions().stream()
              .filter(e -> e.isActive()).collect(Collectors.toList())));
        }
      }
    }
  }

  public void getTable12Info() {
    // Flagships Funding Expenditure Areas
    if (reportSynthesisPMU != null && reportSynthesisPMU.getReportSynthesisFundingUseSummary() != null
      && reportSynthesisPMU.getReportSynthesisFundingUseSummary().getReportSynthesisFundingUseExpendituryAreas() != null
      && !reportSynthesisPMU.getReportSynthesisFundingUseSummary().getReportSynthesisFundingUseExpendituryAreas()
        .isEmpty()) {
      reportSynthesisPMU.getReportSynthesisFundingUseSummary()
        .setExpenditureAreas(new ArrayList<>(reportSynthesisPMU.getReportSynthesisFundingUseSummary()
          .getReportSynthesisFundingUseExpendituryAreas().stream().filter(t -> t.isActive())
          .sorted((f1, f2) -> f1.getId().compareTo(f2.getId())).collect(Collectors.toList())));
    } else {
      if (reportSynthesisPMU != null && reportSynthesisPMU.getReportSynthesisFundingUseSummary() != null) {
        reportSynthesisPMU.getReportSynthesisFundingUseSummary().setExpenditureAreas(new ArrayList<>());
      }
    }
  }

  public List<SrfSloIndicatorTarget> getTable1Info() {

    List<SrfSloIndicatorTarget> sloTargetsFP = new ArrayList<>(srfSloIndicatorTargetManager.findAll().stream()
      .filter(sr -> sr.isActive() && sr.getYear() == 2022).collect(Collectors.toList()));

    // Fill sloTargets List
    List<SrfSloIndicatorTarget> sloTargetsTemp = new ArrayList<>();

    if (sloTargetsFP != null) {

      for (SrfSloIndicatorTarget target : sloTargetsFP) {

        // Get value for 'no new evidence' check button
        ReportSynthesisSrfProgressTargetContribution sloContribution =
          new ReportSynthesisSrfProgressTargetContribution();
        if (reportSynthesisSrfProgressTargetContributionManager.findBySloTargetSynthesis(target.getId(),
          reportSynthesisPMU.getId()) != null) {
          sloContribution = reportSynthesisSrfProgressTargetContributionManager
            .findBySloTargetSynthesis(target.getId(), reportSynthesisPMU.getId()).get(0);
        }

        if (sloContribution != null) {
          target.setHasEvidence(sloContribution.isHasEvidence());
        } else {
          target.setHasEvidence(false);
        }
        ReportSynthesis currentReportSynthesis =
          reportSynthesisManager.findSynthesis(this.getActualPhase().getId(), pmuInstitution.getId());

        List<ReportSynthesisSrfProgressTargetCases> targetCases;
        targetCases = reportSynthesisSrfProgressTargetCasesManager
          .getReportSynthesisSrfProgressId(currentReportSynthesis.getId(), target.getId());

        if (targetCases != null) {

          // Fill target cases
          for (ReportSynthesisSrfProgressTargetCases targetCase : targetCases) {
            if (targetCase != null && targetCase.getId() != null) {
              List<ProgressTargetCaseGeographicScope> targetCaseGeographicScopes = new ArrayList<>();

              // Geographic Scope
              targetCaseGeographicScopes =
                progressTargetCaseGeographicScopeManager.findGeographicScopeByTargetCase(targetCase.getId());

              if (targetCaseGeographicScopes != null) {
                targetCase.setGeographicScopes(targetCaseGeographicScopes);
              }

              // Geographic regions
              List<ProgressTargetCaseGeographicRegion> targetCaseGeographicRegions;
              targetCaseGeographicRegions =
                progressTargetCaseGeographicRegionManager.findGeographicRegionByTargetCase(targetCase.getId());

              if (targetCaseGeographicRegions != null) {
                targetCase.setGeographicRegions(targetCaseGeographicRegions);
              }

              // Geographic countries
              List<ProgressTargetCaseGeographicCountry> targetCaseGeographicCountries;
              targetCaseGeographicCountries =
                progressTargetCaseGeographicCountryManager.findGeographicCountryByTargetCase(targetCase.getId());

              if (targetCaseGeographicCountries != null) {
                targetCase.setCountries(targetCaseGeographicCountries);

                if (targetCase.getCountries() != null) {
                  for (ProgressTargetCaseGeographicCountry country : targetCase.getCountries()) {
                    targetCase.getCountriesIds().add(country.getLocElement().getIsoAlpha2());
                  }
                }
              }
              targetCase.setLiaisonInstitution(pmuInstitution);
            }
          }

          targetCases.addAll(target.getTargetCases());
          target.setTargetCases(targetCases);
        }

        sloTargetsTemp.add(target);
      }

      sloTargetsFP = new ArrayList<>();
      sloTargetsFP.addAll(sloTargetsTemp);
    }

    return sloTargetsFP;
  }

  private void getTable6Info() {
    /** Graphs and Tables */
    LinkedHashSet<Deliverable> deliverables =
      new LinkedHashSet<>(deliverableManager.getPublicationsList(pmuInstitution, this.getSelectedPhase()));

    if (reportSynthesisPMU != null && reportSynthesisPMU.getReportSynthesisFlagshipProgress() != null
      && reportSynthesisPMU.getReportSynthesisFlagshipProgress()
        .getReportSynthesisFlagshipProgressDeliverables() != null
      && !reportSynthesisPMU.getReportSynthesisFlagshipProgress().getReportSynthesisFlagshipProgressDeliverables()
        .isEmpty()) {
      for (ReportSynthesisFlagshipProgressDeliverable flagshipProgressDeliverable : reportSynthesisPMU
        .getReportSynthesisFlagshipProgress().getReportSynthesisFlagshipProgressDeliverables().stream()
        .filter(ro -> ro.isActive()).collect(Collectors.toList())) {
        deliverables.remove(flagshipProgressDeliverable.getDeliverable());
      }
    }

    if (deliverables != null && !deliverables.isEmpty()) {
      total = deliverables.size();

      if (deliverables != null && !deliverables.isEmpty()) {
        for (Deliverable deliverable : deliverables) {

          // Chart: Deliverables open access
          List<DeliverableDissemination> deliverableDisseminations = deliverable
            .getDeliverableInfo(this.getSelectedPhase()).getDeliverable().getDeliverableDisseminations().stream()
            .filter(dd -> dd.isActive() && dd.getPhase() != null && dd.getPhase().equals(this.getSelectedPhase()))
            .collect(Collectors.toList());
          if (deliverableDisseminations != null && !deliverableDisseminations.isEmpty()) {
            deliverable.getDeliverableInfo(this.getSelectedPhase()).getDeliverable()
              .setDissemination(deliverableDisseminations.get(0));
            if (deliverable.getDeliverableInfo(this.getSelectedPhase()).getDeliverable().getDissemination()
              .getIsOpenAccess() != null) {
              // Journal Articles by Open Access
              if (deliverable.getDeliverableInfo(this.getSelectedPhase()).getDeliverable().getDissemination()
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
            .getDeliverableInfo(this.getSelectedPhase()).getDeliverable().getDeliverablePublicationMetadatas().stream()
            .filter(dp -> dp.isActive() && dp.getPhase() != null && dp.getPhase().equals(this.getSelectedPhase()))
            .collect(Collectors.toList());
          if (deliverablePublicationMetadatas != null && !deliverablePublicationMetadatas.isEmpty()) {
            deliverable.getDeliverableInfo(this.getSelectedPhase()).getDeliverable()
              .setPublication(deliverablePublicationMetadatas.get(0));
            // Journal Articles by ISI status
            if (deliverable.getDeliverableInfo(this.getSelectedPhase()).getDeliverable().getPublication()
              .getIsiPublication() != null) {
              if (deliverable.getDeliverableInfo(this.getSelectedPhase()).getDeliverable().getPublication()
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

  public void getTable8Info() {

    externalPartnerships = reportSynthesisKeyPartnershipExternalManager.getTable8(flagshipLiaisonInstitutions,
      pmuInstitution, this.getSelectedPhase());
  }

  public void getTable9Info() {

    collaborations = reportSynthesisKeyPartnershipCollaborationManager.getTable9(flagshipLiaisonInstitutions,
      pmuInstitution, this.getSelectedPhase());
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
          .filter(c -> c != null && c.getId() != null && c.isActive()
            && ((c.getYear() != null && c.getYear().intValue() == this.getSelectedPhase().getYear())
              || (c.getExtendedYear() != null && c.getExtendedYear().intValue() == this.getSelectedPhase().getYear())))
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

    if (this.getLoggedCrp() != null && this.getLoggedCrp().getLiaisonInstitutions() != null) {
      pmuInstitution = this.getLoggedCrp().getLiaisonInstitutions().stream()
        .filter(c -> c.getCrpProgram() == null && c.getAcronym() != null && c.getAcronym().equals("PMU"))
        .collect(Collectors.toList()).get(0);
    }

    reportSynthesisPMU = reportSynthesisManager.findSynthesis(this.getSelectedPhase().getId(), pmuInstitution.getId());

    // Get the list of liaison institutions Flagships.
    flagshipLiaisonInstitutions = this.getLoggedCrp().getLiaisonInstitutions().stream()
      .filter(c -> c.getCrpProgram() != null && c.isActive()
        && c.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue())
      .collect(Collectors.toList());
    flagshipLiaisonInstitutions.sort(Comparator.comparing(LiaisonInstitution::getAcronym));

    this.fillProjectPoliciesTable2List();
    this.fillProjectStudiesTable3List();
    this.fillProjectsInnovationsTable4List();
    this.getTable6Info();
    this.getTable8Info();
    this.getTable9Info();
    this.getTable10Info();
    this.getTable11Info();
    this.getTable12Info();

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
