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
import org.cgiar.ccafs.marlo.config.PentahoListener;
import org.cgiar.ccafs.marlo.data.manager.CrpManager;
import org.cgiar.ccafs.marlo.data.manager.CrpProgramManager;
import org.cgiar.ccafs.marlo.data.manager.GenderTypeManager;
import org.cgiar.ccafs.marlo.data.manager.InstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.IpElementManager;
import org.cgiar.ccafs.marlo.data.manager.LocElementManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectBudgetManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.SrfTargetUnitManager;
import org.cgiar.ccafs.marlo.data.model.Activity;
import org.cgiar.ccafs.marlo.data.model.CaseStudy;
import org.cgiar.ccafs.marlo.data.model.CaseStudyIndicator;
import org.cgiar.ccafs.marlo.data.model.CaseStudyProject;
import org.cgiar.ccafs.marlo.data.model.ChannelEnum;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.CrpTargetUnit;
import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.data.model.DeliverableCrp;
import org.cgiar.ccafs.marlo.data.model.DeliverableDataSharingFile;
import org.cgiar.ccafs.marlo.data.model.DeliverableDissemination;
import org.cgiar.ccafs.marlo.data.model.DeliverableFundingSource;
import org.cgiar.ccafs.marlo.data.model.DeliverableGenderLevel;
import org.cgiar.ccafs.marlo.data.model.DeliverableMetadataElement;
import org.cgiar.ccafs.marlo.data.model.DeliverablePartnership;
import org.cgiar.ccafs.marlo.data.model.DeliverablePartnershipTypeEnum;
import org.cgiar.ccafs.marlo.data.model.DeliverablePublicationMetadata;
import org.cgiar.ccafs.marlo.data.model.DeliverableQualityCheck;
import org.cgiar.ccafs.marlo.data.model.DeliverableUser;
import org.cgiar.ccafs.marlo.data.model.Institution;
import org.cgiar.ccafs.marlo.data.model.IpElement;
import org.cgiar.ccafs.marlo.data.model.IpIndicator;
import org.cgiar.ccafs.marlo.data.model.IpProjectContribution;
import org.cgiar.ccafs.marlo.data.model.IpProjectContributionOverview;
import org.cgiar.ccafs.marlo.data.model.IpProjectIndicator;
import org.cgiar.ccafs.marlo.data.model.LocElement;
import org.cgiar.ccafs.marlo.data.model.OtherContribution;
import org.cgiar.ccafs.marlo.data.model.ProgramType;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectBudget;
import org.cgiar.ccafs.marlo.data.model.ProjectBudgetsCluserActvity;
import org.cgiar.ccafs.marlo.data.model.ProjectClusterActivity;
import org.cgiar.ccafs.marlo.data.model.ProjectComponentLesson;
import org.cgiar.ccafs.marlo.data.model.ProjectCrpContribution;
import org.cgiar.ccafs.marlo.data.model.ProjectFocus;
import org.cgiar.ccafs.marlo.data.model.ProjectHighlight;
import org.cgiar.ccafs.marlo.data.model.ProjectHighlightCountry;
import org.cgiar.ccafs.marlo.data.model.ProjectHighlightType;
import org.cgiar.ccafs.marlo.data.model.ProjectHighligthsTypeEnum;
import org.cgiar.ccafs.marlo.data.model.ProjectInfo;
import org.cgiar.ccafs.marlo.data.model.ProjectLeverage;
import org.cgiar.ccafs.marlo.data.model.ProjectLocation;
import org.cgiar.ccafs.marlo.data.model.ProjectLocationElementType;
import org.cgiar.ccafs.marlo.data.model.ProjectOtherContribution;
import org.cgiar.ccafs.marlo.data.model.ProjectOutcome;
import org.cgiar.ccafs.marlo.data.model.ProjectOutcomePandr;
import org.cgiar.ccafs.marlo.data.model.ProjectPartner;
import org.cgiar.ccafs.marlo.data.model.ProjectPartnerLocation;
import org.cgiar.ccafs.marlo.data.model.ProjectPartnerOverall;
import org.cgiar.ccafs.marlo.data.model.ProjectPartnerPerson;
import org.cgiar.ccafs.marlo.data.model.ProjectStatusEnum;
import org.cgiar.ccafs.marlo.data.model.SrfTargetUnit;
import org.cgiar.ccafs.marlo.data.model.Submission;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.FileManager;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.google.inject.Inject;
import com.lowagie.text.BadElementException;
import com.lowagie.text.Image;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.pentaho.reporting.engine.classic.core.CompoundDataFactory;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.ItemBand;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.PdfReportUtil;
import org.pentaho.reporting.engine.classic.core.util.TypedTableModel;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Andrés Felipe Valencia Rivera. CCAFS
 * @author Christian Garcia - CIAT/CCAFS
 */
public class ReportingSummaryAction extends BaseSummariesAction implements Summary {

  private static final long serialVersionUID = -624982650510682813L;
  private static Logger LOG = LoggerFactory.getLogger(ReportingSummaryAction.class);

  public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
    Map<Object, Boolean> seen = new ConcurrentHashMap<>();
    return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
  }


  // PDF bytes
  private byte[] bytesPDF;

  // Streams
  InputStream inputStream;

  // Parameters
  private long startTime;
  private HashMap<Long, String> targetUnitList;
  private SrfTargetUnitManager srfTargetUnitManager;
  private Project project;
  private Boolean hasW1W2Co;
  private Boolean hasGender;
  private long projectID;
  private ProjectInfo projectInfo;


  // Managers
  private ProjectManager projectManager;
  private CrpProgramManager programManager;
  private GenderTypeManager genderTypeManager;
  private InstitutionManager institutionManager;
  private ProjectBudgetManager projectBudgetManager;
  private LocElementManager locElementManager;
  private IpElementManager ipElementManager;

  @Inject
  public ReportingSummaryAction(APConfig config, CrpManager crpManager, ProjectManager projectManager,
    GenderTypeManager genderTypeManager, CrpProgramManager programManager, InstitutionManager institutionManager,
    ProjectBudgetManager projectBudgetManager, LocElementManager locElementManager, IpElementManager ipElementManager,
    SrfTargetUnitManager srfTargetUnitManager, PhaseManager phaseManager) {
    super(config, crpManager, phaseManager);
    this.projectManager = projectManager;
    this.programManager = programManager;
    this.institutionManager = institutionManager;
    this.projectBudgetManager = projectBudgetManager;
    this.locElementManager = locElementManager;
    this.ipElementManager = ipElementManager;
    this.genderTypeManager = genderTypeManager;
    this.srfTargetUnitManager = srfTargetUnitManager;
  }

  /**
   * Add columns depending on specificity parameters
   * 
   * @param masterReport: used to update the parameters
   * @return masterReport with the added parameters.
   */
  private MasterReport addColumnParameters(MasterReport masterReport) {
    // Set columns for BudgetByPartners
    int columnBudgetPartner = 4;
    String paramName = "BudgetPartner";
    // used to which index will be excluded
    ArrayList<Integer> exludeIndex = new ArrayList<>();
    if (this.hasGender) {
      columnBudgetPartner++;
    } else {
      exludeIndex.add(0);
    }
    if (this.hasW1W2Co) {
      columnBudgetPartner++;
    } else {
      exludeIndex.add(2);
    }
    // Calculate column width
    long width = 471l / columnBudgetPartner;
    HashMap<String, Long> hm = this.calculateWidth(width, columnBudgetPartner, paramName, exludeIndex, 0l);
    // Add x parameters
    for (HashMap.Entry<String, Long> entry : hm.entrySet()) {
      String key = entry.getKey();
      Object value = entry.getValue();
      masterReport.getParameterValues().put(key, value);
    }
    // add width
    masterReport.getParameterValues().put("BudgetPartnerWidth", width);
    return masterReport;
  }

  /**
   * Method to add i8n parameters to masterReport in Pentaho
   * 
   * @param masterReport
   * @return masterReport with i8n parameters added
   */
  private MasterReport addi8nParameters(MasterReport masterReport) {
    /**
     * Menus Planning & Reporting
     */
    masterReport.getParameterValues().put("i8nProjectMenu", this.getText("projects.menu.description"));
    masterReport.getParameterValues().put("i8nPartnersMenu", this.getText("projects.menu.partners"));
    masterReport.getParameterValues().put("i8nLocationsMenu", this.getText("projects.menu.locations"));
    masterReport.getParameterValues().put("i8nActivitiesMenu", this.getText("projects.menu.activities"));
    /**
     * Menus Planning
     */
    masterReport.getParameterValues().put("i8nOutcomesMenu", this.getText("projects.menu.contributionsCrpList"));
    masterReport.getParameterValues().put("i8nDeliverablesMenu",
      "Expected " + this.getText("projects.menu.deliverables"));
    masterReport.getParameterValues().put("i8nBudgetMenu",
      "Project " + this.getText("projects.menu.budget") + " (USD)");
    masterReport.getParameterValues().put("i8nBudgetPartnerMenu",
      this.getText("projects.menu.budgetByPartners") + " (USD)");
    masterReport.getParameterValues().put("i8nBudgetCoAsMenu", this.getText("planning.cluster") + " (USD)");

    /**
     * Menus Reporting
     */
    masterReport.getParameterValues().put("i8nOutcomesRMenu", this.getText("breadCrumb.menu.outcomes"));
    masterReport.getParameterValues().put("i8nProjectOutcomesRMenu", this.getText("projects.menu.projectOutcomes"));
    masterReport.getParameterValues().put("i8nCCAFSOutcomesRMenu", this.getText("projects.menu.ccafsOutcomes"));
    masterReport.getParameterValues().put("i8nOtherContributionsRMenu",
      this.getText("projects.menu.otherContributions"));
    masterReport.getParameterValues().put("i8nCaseStudiesRMenu", this.getText("projects.menu.caseStudies"));
    masterReport.getParameterValues().put("i8nProjectOutputsRMenu", this.getText("projects.menu.projectOutputs"));
    masterReport.getParameterValues().put("i8nOverviewByMOGsRMenu", this.getText("projects.menu.overviewByMogs"));
    masterReport.getParameterValues().put("i8nDeliverablesRMenu", this.getText("projects.menu.deliverables"));
    masterReport.getParameterValues().put("i8nProjectHighlightsRMenu",
      this.getText("breadCrumb.menu.projectHighlights"));
    masterReport.getParameterValues().put("i8nLeveragesRMenu", this.getText("breadCrumb.menu.leverage"));

    /*
     * Description
     */
    masterReport.getParameterValues().put("i8nProjectTitle", this.getText("project.title"));
    masterReport.getParameterValues().put("i8nProjectStartDate", this.getText("project.startDate"));
    masterReport.getParameterValues().put("i8nProjectEndDate", this.getText("project.endDate"));
    masterReport.getParameterValues().put("i8nProjectFundingSourcesTypes", this.getText("project.fundingSourcesTypes"));
    masterReport.getParameterValues().put("i8nProjectStatus", this.getText("projectsList.projectStatus"));
    masterReport.getParameterValues().put("i8nProjectLeadOrg", this.getText("project.leadOrg"));
    masterReport.getParameterValues().put("i8nProjectPL", this.getText("projectPartners.types.PL"));
    masterReport.getParameterValues().put("i8nProjectDescFLRP", this.getText("project.menuFLRP"));
    masterReport.getParameterValues().put("i8nProjectFL", this.getText("project.Flagships"));
    masterReport.getParameterValues().put("i8nProjectRP", this.getText("project.Regions"));
    masterReport.getParameterValues().put("i8nProjectCluster", this.getText("project.clusterDesc"));
    masterReport.getParameterValues().put("i8nProjectSummary", this.getText("project.summary"));
    masterReport.getParameterValues().put("i8nProjectGenderDesc", this.getText("project.GenderDesc"));
    masterReport.getParameterValues().put("i8nProjectCrossCutting",
      this.getText("project.crossCuttingDimensions.readText"));


    /*
     * Partners
     */
    masterReport.getParameterValues().put("i8nPartnerNoData", this.getText("partner.noData"));
    masterReport.getParameterValues().put("i8nPartner", this.getText("partner.partnerSingular"));
    masterReport.getParameterValues().put("i8nPartnerLeader", this.getText("projectsList.projectLeader"));
    masterReport.getParameterValues().put("i8nPartnerInstitution", this.getText("partner.institution") + ":");
    masterReport.getParameterValues().put("i8nPartnerResponsibilities", this.getText("partner.responsabilities") + ":");
    masterReport.getParameterValues().put("i8nPartnerCountryOffices", this.getText("partner.countryOffices") + ":");
    masterReport.getParameterValues().put("i8nPartnerContacts", this.getText("partner.contacts") + ":");
    masterReport.getParameterValues().put("i8nPartnerType", this.getText("projectPartners.partnerType"));
    masterReport.getParameterValues().put("i8nPartnerContact", this.getText("projectPartners.contactPersonEmail"));
    masterReport.getParameterValues().put("i8nPartnerLessonsStatement",
      this.getText("projectPartners.lessons.planning.readText"));
    masterReport.getParameterValues().put("i8nPartnerYear", this.getText("partner.year"));
    masterReport.getParameterValues().put("i8nPartnerLessons", this.getText("partner.lessons"));
    /*
     * Locations
     */
    masterReport.getParameterValues().put("i8nLocationNoData", this.getText("location.noData"));
    masterReport.getParameterValues().put("i8nLocationProjectLevel", this.getText("location.projectLevel"));
    masterReport.getParameterValues().put("i8nLocationLatitude", this.getText("location.inputLatitude.placeholder"));
    masterReport.getParameterValues().put("i8nLocationLongitude", this.getText("location.inputLongitude.placeholder"));
    masterReport.getParameterValues().put("i8nLocationName", this.getText("location.inputName.placeholder"));
    masterReport.getParameterValues().put("i8nLocationGlobal", this.getText("projectLocations.isGlobalYes"));
    masterReport.getParameterValues().put("i8nLocationNoGlobal", this.getText("projectLocations.isGlobalNo"));
    /*
     * Contribution to flagship outcomes
     */
    masterReport.getParameterValues().put("i8nOutcomeNoData", this.getText("outcome.noData"));
    masterReport.getParameterValues().put("i8nOutcome", this.getText("outcome.statement.readText"));
    masterReport.getParameterValues().put("i8nOutcomeTargetUnit", this.getText("outcome.targetUnit"));
    masterReport.getParameterValues().put("i8nOutcomeTargetValue", this.getText("outcome.targetValue"));
    masterReport.getParameterValues().put("i8nOutcomeExpectedContribution",
      this.getText("outcome.expectedContribution"));
    masterReport.getParameterValues().put("i8nOutcomeExpectedUnit", this.getText("projectOutcome.expectedUnit"));
    masterReport.getParameterValues().put("i8nOutcomeExpectedValue", this.getText("outcome.expectedValue"));
    masterReport.getParameterValues().put("i8nOutcomeExpectedNarrative", this.getText("outcome.expectedNarrative"));
    masterReport.getParameterValues().put("i8nOutcomeCrossCutting", this.getText("outcome.crossCutting"));
    masterReport.getParameterValues().put("i8nOutcomeMilestones", this.getText("outcome.milestone"));
    masterReport.getParameterValues().put("i8nOutcomeNextUsers", this.getText("outcome.nextUsers"));
    masterReport.getParameterValues().put("i8nOutcomeLesssonsStatement",
      this.getText("projectOutcome.lessons.planning"));
    masterReport.getParameterValues().put("i8nOutcomeYear", this.getText("outcome.inputTargetYear.placeholder"));
    masterReport.getParameterValues().put("i8nOutcomeLessons", this.getText("outcome.lessons"));
    /*
     * Deliverables
     */
    masterReport.getParameterValues().put("i8nDeliverableNoData", this.getText("deliverable.NoData"));
    masterReport.getParameterValues().put("i8nDeliverableType", this.getText("deliverable.type"));
    masterReport.getParameterValues().put("i8nDeliverableSubType", this.getText("deliverable.subtype"));
    masterReport.getParameterValues().put("i8nDeliverableStatus",
      this.getText("project.deliverable.generalInformation.status"));
    masterReport.getParameterValues().put("i8nDeliverableExpectedYear",
      this.getText("project.deliverable.generalInformation.year"));
    masterReport.getParameterValues().put("i8nDeliverableKeyOutput",
      this.getText("project.deliverable.generalInformation.keyOutput"));
    masterReport.getParameterValues().put("i8nDeliverableFundingSources", this.getText("deliverable.fundingSource"));
    masterReport.getParameterValues().put("i8nDeliverableCrossCutting",
      this.getText("project.crossCuttingDimensions.readText"));
    masterReport.getParameterValues().put("i8nDeliverablePartnersStatement",
      this.getText("project.deliverable.partnership"));
    masterReport.getParameterValues().put("i8nDeliverableInstitution", this.getText("deliverable.institution"));
    masterReport.getParameterValues().put("i8nDeliverablePartner", this.getText("project.deliverable.partner"));
    masterReport.getParameterValues().put("i8nDeliverableType", this.getText("deliverable.type"));
    /*
     * Activities
     */
    masterReport.getParameterValues().put("i8nActivityNoData", this.getText("activity.noData"));
    masterReport.getParameterValues().put("i8nActivityDescription",
      this.getText("project.activities.inputDescription.readText"));
    masterReport.getParameterValues().put("i8nActivityStartDate", this.getText("project.activities.inputStartDate"));
    masterReport.getParameterValues().put("i8nActivityEndDate", this.getText("project.activities.inputEndDate"));
    masterReport.getParameterValues().put("i8nActivityLeader", this.getText("project.activities.inputLeader"));
    masterReport.getParameterValues().put("i8nActivityStatus", this.getText("project.activities.inputStatus"));
    masterReport.getParameterValues().put("i8nActivityDeliverables",
      this.getText("project.activities.deliverableList"));
    /*
     * Budget
     */
    masterReport.getParameterValues().put("i8nBudgetNoData", this.getText("budget.noData"));
    masterReport.getParameterValues().put("i8nBudgetTotal", this.getText("budget.total"));
    masterReport.getParameterValues().put("i8nBudget", this.getText("projects.menu.budget").toLowerCase());
    masterReport.getParameterValues().put("i8nBudgetW1W2", this.getText("projectsList.W1W2projectBudget"));
    masterReport.getParameterValues().put("i8nBudgetW1W2Cofinancing", this.getText("budget.w1w2cofinancing"));
    masterReport.getParameterValues().put("i8nBudgetW3", this.getText("projectsList.W3projectBudget"));
    masterReport.getParameterValues().put("i8nBudgetBilateral", this.getText("projectsList.BILATERALprojectBudget"));
    masterReport.getParameterValues().put("i8nBudgetCenterFunds", this.getText("budget.centerFunds"));
    /*
     * Budget by Partners
     */

    masterReport.getParameterValues().put("i8nBudgetPartnerGender", this.getText("budgetPartner.gender"));
    masterReport.getParameterValues().put("i8nBudgetPartnerType", this.getText("budgetPartner.type"));
    masterReport.getParameterValues().put("i8nBudgetPartnerAmount", this.getText("budget.amount"));
    masterReport.getParameterValues().put("i8nBudgetPartnerFundingSources",
      this.getText("budgetPartner.fundingSource"));
    masterReport.getParameterValues().put("i8nBudgetPartnerBudget", this.getText("projects.menu.budget"));

    /*
     * Reporting Only
     */

    /*
     * Reporting
     * Partners
     */
    masterReport.getParameterValues().put("i8nPartnerRLessonsStatement",
      this.getText("projectPartners.lessons.reporting.readText"));
    masterReport.getParameterValues().put("i8nPartnerROverall",
      this.getText("projectPartners.partnershipsOverall.readText"));
    /*
     * Reporting
     * Project Outcomes
     */
    masterReport.getParameterValues().put("i8nProjectOutcomesRNoData", this.getText("projectOutcomes.noData"));
    masterReport.getParameterValues().put("i8nProjectOutcomesRStatement",
      this.getText("projectOutcomes.statement.readText"));
    masterReport.getParameterValues().put("i8nProjectOutcomesRAnnualProgress",
      this.getText("projectOutcomes.annualProgress.readText", new String[] {String.valueOf(this.getSelectedYear())}));
    masterReport.getParameterValues().put("i8nProjectOutcomesRAnnualProgressCurrent",
      this.getText("projectOutcomes.annualProgressCurrentReporting.readText",
        new String[] {String.valueOf(this.getSelectedYear())}));
    masterReport.getParameterValues().put("i8nProjectOutcomesRComunnicationCurrent",
      this.getText("projectOutcomes.commEngagementOutcomes.readText"));
    masterReport.getParameterValues().put("i8nProjectOutcomesREvidence",
      this.getText("projectOutcomes.uploadSummary.readText"));
    masterReport.getParameterValues().put("i8nProjectOutcomesRAnnualProgressOutcome",
      this.getText("projectOutcomes.Annual"));
    masterReport.getParameterValues().put("i8nProjectOutcomesRLessons",
      this.getText("projectOutcomes.lessons.readText"));
    /*
     * Reporting
     * CCAFS Outcomes
     */
    masterReport.getParameterValues().put("i8nCCAFSOutcomesRNoData", this.getText("ccafsOutcomes.noData"));
    masterReport.getParameterValues().put("i8nCCAFSOutcomesRTargetValue",
      this.getText("projectCcafsOutcomes.targetValue"));
    masterReport.getParameterValues().put("i8nCCAFSOutcomesRCumulativeTarget",
      this.getText("projectCcafsOutcomes.comulativeTarget"));
    masterReport.getParameterValues().put("i8nCCAFSOutcomesRAchievedTarget",
      this.getText("projectCcafsOutcomes.achievedTarget"));
    masterReport.getParameterValues().put("i8nCCAFSOutcomesRTargetNarrative",
      this.getText("projectCcafsOutcomes.targetNarrative"));
    masterReport.getParameterValues().put("i8nCCAFSOutcomesRTargetNarrativeAchieved",
      this.getText("projectCcafsOutcomes.targetNarrativeAchieved"));
    masterReport.getParameterValues().put("i8nCCAFSOutcomesRTargetNarrativeGenderAchieved",
      this.getText("projectCcafsOutcomes.targetNarrativeGenderAchieved"));
    masterReport.getParameterValues().put("i8nCCAFSOutcomesRTargetGender",
      this.getText("projectCcafsOutcomes.targetGender"));
    masterReport.getParameterValues().put("i8nCCAFSOutcomesRMogs", this.getText("projectCcafsOutcomes.mogs"));
    /*
     * Reporting
     * Other Contributions
     */
    masterReport.getParameterValues().put("i8nOtherContributionsRNoData", this.getText("otherContributions.noData"));
    masterReport.getParameterValues().put("i8nOtherContributionsRStatement",
      this.getText("projectOtherContributions.contribution.readText"));
    masterReport.getParameterValues().put("i8nOtherContributionsRRegion",
      this.getText("projectOtherContributions.region"));
    masterReport.getParameterValues().put("i8nOtherContributionsRIndicator",
      this.getText("projectOtherContributions.indicators"));
    masterReport.getParameterValues().put("i8nOtherContributionsRContributionTarget",
      this.getText("otherContributions.contributionTarget"));
    masterReport.getParameterValues().put("i8nOtherContributionsRTargetValueContrib",
      this.getText("projectOtherContributions.target.readText"));
    masterReport.getParameterValues().put("i8nOtherContributionsRColaboratingCRPS",
      this.getText("projectOtherContributions.collaboratingCRPs.readText"));
    masterReport.getParameterValues().put("i8nOtherContributionsRCRPEmpty",
      this.getText("projectOtherContributions.crpsEmpty"));
    masterReport.getParameterValues().put("i8nOtherContributionsRColaboratingDescription",
      this.getText("projectOtherContributions.collaborationNature.readText"));

    /*
     * Reporting
     * Case Studies
     */
    masterReport.getParameterValues().put("i8nCaseStudiesRNoData", this.getText("caseStudy.noData"));
    masterReport.getParameterValues().put("i8nCaseStudiesRCaseStudy", this.getText("breadCrumb.menu.caseStudy"));
    masterReport.getParameterValues().put("i8nCaseStudiesRTitle", this.getText("caseStudy.title"));
    masterReport.getParameterValues().put("i8nCaseStudiesRYear", this.getText("caseStudy.caseStudyYear"));
    masterReport.getParameterValues().put("i8nCaseStudiesRProjects", this.getText("caseStudy.projects"));
    masterReport.getParameterValues().put("i8nCaseStudiesRStatement",
      this.getText("caseStudy.outcomeStatement.readText"));
    masterReport.getParameterValues().put("i8nCaseStudiesRResearchOutputs",
      this.getText("caseStudy.researchOutput.readText"));
    masterReport.getParameterValues().put("i8nCaseStudiesRResearchPartners",
      this.getText("caseStudy.researchPartners.readText"));
    masterReport.getParameterValues().put("i8nCaseStudiesRActivities",
      this.getText("caseStudy.activitiesContributed.readText"));
    masterReport.getParameterValues().put("i8nCaseStudiesRNonResearchPartners",
      this.getText("caseStudy.nonResearchPartners.readText"));
    masterReport.getParameterValues().put("i8nCaseStudiesROutputUsers", this.getText("caseStudy.outputUsers.readText"));
    masterReport.getParameterValues().put("i8nCaseStudiesREvidence", this.getText("caseStudy.evidence.readText"));
    masterReport.getParameterValues().put("i8nCaseStudiesROutputUsers", this.getText("caseStudy.outputUsers.readText"));
    masterReport.getParameterValues().put("i8nCaseStudiesRReferences", this.getText("caseStudy.references.readText"));
    masterReport.getParameterValues().put("i8nCaseStudiesRIndicators",
      this.getText("caseStudy.caseStudyIndicators.readText"));
    masterReport.getParameterValues().put("i8nCaseStudiesRExplainIndicatorRelation",
      this.getText("caseStudy.explainIndicatorRelation.readText"));
    masterReport.getParameterValues().put("i8nCaseStudiesRUploadAnnexes",
      this.getText("caseStudy.uploadAnnexes.readText"));
    /*
     * Reporting
     * Overview By MOGs
     */
    masterReport.getParameterValues().put("i8nOverviewByMOGsRNoData", this.getText("overviewByMOGS.noData"));
    masterReport.getParameterValues().put("i8nOverviewByMOGsRMogs", this.getText("projectCcafsOutcomes.mogs"));

    /*
     * Reporting
     * Deliverables
     */
    masterReport.getParameterValues().put("i8nDeliverablesRMainInfo",
      this.getText("project.deliverable.generalInformation.titleTab"));
    masterReport.getParameterValues().put("i8nDeliverablesRType",
      this.getText("project.deliverable.generalInformation.type"));
    masterReport.getParameterValues().put("i8nDeliverablesRSubType",
      this.getText("project.deliverable.generalInformation.subType"));
    masterReport.getParameterValues().put("i8nDeliverablesRStatus",
      this.getText("project.deliverable.generalInformation.status"));
    masterReport.getParameterValues().put("i8nDeliverablesRYearExpectedCompletion",
      this.getText("project.deliverable.generalInformation.year"));
    masterReport.getParameterValues().put("i8nDeliverablesRNewExpectedYear",
      this.getText("deliverable.newExpectedYear"));
    masterReport.getParameterValues().put("i8nDeliverablesRJustificationNewExpectedDate",
      this.getText("deliverable.justificationNewExpectedDate"));
    masterReport.getParameterValues().put("i8nDeliverablesRKeyOutput",
      this.getText("project.deliverable.generalInformation.keyOutput"));
    masterReport.getParameterValues().put("i8nDeliverablesRFundingSource",
      this.getText("project.deliverable.fundingSource.readText"));
    masterReport.getParameterValues().put("i8nDeliverablesRCrossCuttingDimensions",
      this.getText("deliverable.crossCuttingDimensions.readText"));
    masterReport.getParameterValues().put("i8nDeliverablesRDiseminationTitle",
      this.getText("deliverable.diseminationTitle"));
    masterReport.getParameterValues().put("i8nDeliverablesRAlreadyDisseminatedQuestion",
      this.getText("project.deliverable.dissemination.alreadyDisseminatedQuestion"));
    masterReport.getParameterValues().put("i8nDeliverablesRDisseminationChanel",
      this.getText("project.deliverable.dissemination.v.DisseminationChanel"));
    masterReport.getParameterValues().put("i8nDeliverablesRDisseminationUrl",
      this.getText("project.deliverable.dissemination.disseminationUrl"));
    masterReport.getParameterValues().put("i8nDeliverablesRIsOpenAccess",
      this.getText("project.deliverable.dissemination.v.isOpenAccess"));
    masterReport.getParameterValues().put("i8nDeliverablesROpenAccessRestriction",
      this.getText("project.deliverable.dissemination.v.openAccessRestriction"));
    masterReport.getParameterValues().put("i8nDeliverablesRALicense", this.getText("project.deliverable.v.ALicense"));
    masterReport.getParameterValues().put("i8nDeliverablesRPublicationAllowModifications",
      this.getText("publication.publicationAllowModifications"));
    masterReport.getParameterValues().put("i8nDeliverablesRMetadataSubtitle",
      this.getText("project.deliverable.dissemination.metadataSubtitle"));
    masterReport.getParameterValues().put("i8nDeliverablesRMetadataTitle", this.getText("metadata.title"));
    masterReport.getParameterValues().put("i8nDeliverablesRMetadataDescription",
      this.getText("metadata.description.readText"));
    masterReport.getParameterValues().put("i8nDeliverablesRMetadataDate", this.getText("metadata.date"));
    masterReport.getParameterValues().put("i8nDeliverablesRLanguage", this.getText("metadata.language"));
    masterReport.getParameterValues().put("i8nDeliverablesRCountry", this.getText("metadata.country"));
    masterReport.getParameterValues().put("i8nDeliverablesRKeywords", this.getText("metadata.keywords.help"));
    masterReport.getParameterValues().put("i8nDeliverablesRCitation", this.getText("metadata.citation.readText"));
    masterReport.getParameterValues().put("i8nDeliverablesRHandle", this.getText("metadata.handle"));
    masterReport.getParameterValues().put("i8nDeliverablesRDoi", this.getText("metadata.doi"));
    masterReport.getParameterValues().put("i8nDeliverablesRCreator", this.getText("metadata.creator"));
    masterReport.getParameterValues().put("i8nDeliverablesRPublicationTitle",
      this.getText("project.deliverable.dissemination.publicationTitle"));
    masterReport.getParameterValues().put("i8nDeliverablesRVolume",
      this.getText("project.deliverable.dissemination.volume"));
    masterReport.getParameterValues().put("i8nDeliverablesRIssue",
      this.getText("project.deliverable.dissemination.issue"));
    masterReport.getParameterValues().put("i8nDeliverablesRPages",
      this.getText("project.deliverable.dissemination.pages"));
    masterReport.getParameterValues().put("i8nDeliverablesRJournalName",
      this.getText("project.deliverable.dissemination.journalName"));
    masterReport.getParameterValues().put("i8nDeliverablesRIndicatorsJournal",
      this.getText("project.deliverable.dissemination.indicatorsJournal"));
    masterReport.getParameterValues().put("i8nDeliverablesRPublicationAcknowledge",
      this.getText("deliverable.publicationAcknowledge"));
    masterReport.getParameterValues().put("i8nDeliverablesRPublicationFLContribution",
      this.getText("deliverable.publicationFLContribution"));
    masterReport.getParameterValues().put("i8nDeliverablesRQualityCheckTitle",
      this.getText("deliverable.qualityCheckTitle"));
    masterReport.getParameterValues().put("i8nDeliverablesRFairTitle",
      this.getText("project.deliverable.quality.fairTitle"));
    masterReport.getParameterValues().put("i8nDeliverablesRQualityCheckAssurance",
      this.getText("deliverable.qualityCheckAssurance"));
    masterReport.getParameterValues().put("i8nDeliverablesRQualityCheckDataDictionary",
      this.getText("deliverable.qualityCheckDataDictionary"));
    masterReport.getParameterValues().put("i8nDeliverablesRQualityCheckQuestion3",
      this.getText("project.deliverable.quality.question3"));
    masterReport.getParameterValues().put("i8nDeliverablesRDataSharingTitle",
      this.getText("projectDeliverable.dataSharingTitle"));
    masterReport.getParameterValues().put("i8nDeliverablesRDeliverableFiles",
      this.getText("projectDeliverable.dataSharing.deliverableFiles"));
    masterReport.getParameterValues().put("i8nDeliverablesRPartnership",
      this.getText("project.deliverable.partnership"));
    masterReport.getParameterValues().put("i8nDeliverablesRInstitution", this.getText("deliverable.institution"));
    masterReport.getParameterValues().put("i8nDeliverablesRPartnerSingular", this.getText("partner.partnerSingular"));
    masterReport.getParameterValues().put("i8nDeliverablesRType2", this.getText("deliverable.type"));

    /*
     * Reporting
     * Project highlights
     */
    masterReport.getParameterValues().put("i8nProjectHighlightsRNoData", this.getText("projectHighlight.noData"));
    masterReport.getParameterValues().put("i8nProjectHighlightsRSingular", this.getText("projectHighlight.singular"));
    masterReport.getParameterValues().put("i8nProjectHighlightsRTitle", this.getText("highlight.title"));
    masterReport.getParameterValues().put("i8nProjectHighlightsRAuthor", this.getText("highlight.author"));
    masterReport.getParameterValues().put("i8nProjectHighlightsRSubject", this.getText("highlight.subject"));
    masterReport.getParameterValues().put("i8nProjectHighlightsRPublisher", this.getText("highlight.publisher"));
    masterReport.getParameterValues().put("i8nProjectHighlightsRYear", this.getText("highlight.year"));
    masterReport.getParameterValues().put("i8nProjectHighlightsRTypes", this.getText("highlight.types"));
    masterReport.getParameterValues().put("i8nProjectHighlightsRIsGlobal", this.getText("highlight.isGlobal.readText"));
    masterReport.getParameterValues().put("i8nProjectHighlightsRStartDate", this.getText("highlight.startDate"));
    masterReport.getParameterValues().put("i8nProjectHighlightsREndDate", this.getText("highlight.endDate"));
    masterReport.getParameterValues().put("i8nProjectHighlightsRKeywords", this.getText("highlight.keywords"));
    masterReport.getParameterValues().put("i8nProjectHighlightsRCountries", this.getText("highlight.countries"));
    masterReport.getParameterValues().put("i8nProjectHighlightsRDescripition",
      this.getText("highlight.descripition.readText"));
    masterReport.getParameterValues().put("i8nProjectHighlightsRObjectives",
      this.getText("highlight.objectives.readText"));
    masterReport.getParameterValues().put("i8nProjectHighlightsRResults", this.getText("highlight.results.readText"));
    masterReport.getParameterValues().put("i8nProjectHighlightsRPartners", this.getText("highlight.partners.readText"));
    masterReport.getParameterValues().put("i8nProjectHighlightsRLinks", this.getText("highlight.links.readText"));

    /*
     * Reporting
     * Project activities
     */
    masterReport.getParameterValues().put("i8nProjectActivityRNoData", this.getText("projectActivity.noData"));
    masterReport.getParameterValues().put("i8nProjectActivityRInputDescription",
      this.getText("project.activities.inputDescription.readText"));
    masterReport.getParameterValues().put("i8nProjectActivityRInputStartDate",
      this.getText("project.activities.inputStartDate"));
    masterReport.getParameterValues().put("i8nProjectActivityRInputEndDate",
      this.getText("project.activities.inputEndDate"));
    masterReport.getParameterValues().put("i8nProjectActivityRInputLeader",
      this.getText("project.activities.inputLeader"));
    masterReport.getParameterValues().put("i8nProjectActivityRInputStatus",
      this.getText("project.activities.inputStatus"));
    masterReport.getParameterValues().put("i8nProjectActivityRStatusJustification",
      this.getText("project.activities.statusJustification.status2.readText"));
    masterReport.getParameterValues().put("i8nProjectActivityRDeliverableList",
      this.getText("project.activities.deliverableList"));

    /*
     * Reporting
     * Project leverages
     */
    masterReport.getParameterValues().put("i8nProjectLeverageRNoData", this.getText("projectLeverage.noData"));
    masterReport.getParameterValues().put("i8nProjectLeverageRSingular", this.getText("projectLeverage.singular"));
    masterReport.getParameterValues().put("i8nProjectLeverageRPartnerName",
      this.getText("projectLeverage.partnerName"));
    masterReport.getParameterValues().put("i8nProjectLeverageRYear", this.getText("reporting.projectLeverages.year"));
    masterReport.getParameterValues().put("i8nProjectLeverageRFlagship", this.getText("projectLeverage.flagship"));
    masterReport.getParameterValues().put("i8nProjectLeverageRBudget", this.getText("projectLeverage.budget"));


    return masterReport;
  }

  public String calculateAcumulativeTarget(int yearCalculate, IpProjectIndicator id) {
    int acumulative = 0;
    try {
      for (IpProjectIndicator indicators : project.getProjectIndicators()) {
        if (indicators != null) {
          if (id.getIpIndicator().getIpIndicator() != null) {
            if (indicators.getYear() <= yearCalculate && indicators.getIpIndicator().getIpIndicator().getId()
              .longValue() == id.getIpIndicator().getIpIndicator().getId().longValue()) {
              if (indicators.getTarget() == null) {
                indicators.setTarget("0");
              }
              if (indicators.getTarget() != null) {
                if (!indicators.getTarget().equals("")) {
                  try {
                    acumulative = acumulative + Integer.parseInt(indicators.getTarget());
                  } catch (NumberFormatException e) {
                    LOG.warn("Cannot calculate acumulative target. NumberFormatException: " + e.getMessage());
                    return "Cannot be Calculated";
                  }
                }
              }
            }
          } else {
            if (indicators.getYear() <= yearCalculate && indicators.getIpIndicator() != null
              && indicators.getIpIndicator().getId() != null
              && indicators.getIpIndicator().getId().longValue() == id.getIpIndicator().getId().longValue()) {
              if (indicators.getTarget() == null) {
                indicators.setTarget("0");
              }
              if (indicators.getTarget() != null) {

                if (!indicators.getTarget().equals("")) {
                  try {
                    acumulative = acumulative + Integer.parseInt(indicators.getTarget());
                  } catch (NumberFormatException e) {
                    LOG.warn("Cannot calculate acumulative target. NumberFormatException: " + e.getMessage());
                    return "Cannot be Calculated";
                  }
                }
              }
            }
          }
        }
      }
    } catch (Exception e) {
      LOG.warn("Cannot calculate acumulative target. NumberFormatException: " + e.getMessage());
      return "Cannot be Calculated";
    }
    return String.valueOf(acumulative);
  }

  private HashMap<String, Long> calculateWidth(long width, int numColumns, String name, ArrayList<Integer> excludeIndex,
    long xPosition) {
    HashMap<String, Long> hm = new HashMap<String, Long>();
    for (int i = 0; i <= numColumns; i++) {
      if (!excludeIndex.contains(i)) {
        hm.put("xPosition" + name + i, xPosition);
        xPosition += width;
      }
    }

    return hm;
  }

  public boolean containsOutput(long outputID, long outcomeID) {
    if (project.getMogs() != null) {
      for (IpElement output : project.getMogs()) {
        IpElement outputDB = ipElementManager.getIpElementById(output.getId());
        if (outputDB != null && outputDB.getId().longValue() == outputID) {
          return true;
        }
      }
    }
    return false;
  }


  @Override
  public String execute() throws Exception {
    try {
      hasGender = this.hasSpecificities(APConstants.CRP_BUDGET_GENDER);
    } catch (Exception e) {
      LOG.warn("Failed to get " + APConstants.CYCLE + " parameter. Parameter will be set as false. Exception: "
        + e.getMessage());
      hasGender = false;
    }
    // get w1w2 co
    try {
      hasW1W2Co = this.hasSpecificities(APConstants.CRP_FS_W1W2_COFINANCING);
    } catch (Exception e) {
      LOG.warn("Failed to get " + APConstants.CYCLE + " parameter. Parameter will be set as false. Exception: "
        + e.getMessage());
      hasW1W2Co = false;
    }

    // Fill target unit list
    targetUnitList = new HashMap<>();
    if (srfTargetUnitManager.findAll() != null) {
      List<SrfTargetUnit> targetUnits = new ArrayList<>();
      List<CrpTargetUnit> crpTargetUnits = new ArrayList<>(
        this.getLoggedCrp().getCrpTargetUnits().stream().filter(tu -> tu.isActive()).collect(Collectors.toList()));
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
        + ". CRP: " + this.getLoggedCrp().getAcronym() + ". Cycle: " + this.getSelectedCycle());

    ByteArrayOutputStream os = new ByteArrayOutputStream();
    ResourceManager manager = // new ResourceManager();
      (ResourceManager) ServletActionContext.getServletContext().getAttribute(PentahoListener.KEY_NAME);
    manager.registerDefaults();
    try {
      String masterQueryName = "Main_Query";
      Resource reportResource;
      if (this.getSelectedCycle().equals("Planning")) {
        reportResource = manager.createDirectly(
          this.getClass().getResource("/pentaho/project-description(Planning)-Annualization.prpt"), MasterReport.class);
      } else {
        reportResource = manager.createDirectly(
          this.getClass().getResource("/pentaho/project-description(Reporting)-Annualization.prpt"),
          MasterReport.class);
      }
      // Get main report
      MasterReport masterReport = (MasterReport) reportResource.getResource();
      // General list to store parameters of Subreports
      List<Object> args = new LinkedList<>();
      // Verify if the project was found
      if (project != null) {
        // Get details band
        ItemBand masteritemBand = masterReport.getItemBand();
        // Create new empty subreport hash map
        HashMap<String, Element> hm = new HashMap<String, Element>();
        // method to get all the subreports in the prpt and store in the HashMap
        this.getAllSubreports(hm, masteritemBand);
        // Uncomment to see which Subreports are detecting the method getAllSubreports
        // System.out.println("Pentaho SubReports: " + hm);
        // get project leader
        ProjectPartner projectLeader = project.getLeader(this.getSelectedPhase());
        // get Flagships related to the project sorted by acronym
        List<CrpProgram> flagships = new ArrayList<>();
        for (ProjectFocus projectFocuses : project.getProjectFocuses().stream()
          .sorted((o1, o2) -> o1.getCrpProgram().getAcronym().compareTo(o2.getCrpProgram().getAcronym()))
          .filter(
            c -> c.isActive() && c.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue()
              && c.getPhase() != null && c.getPhase().equals(this.getSelectedPhase()))
          .collect(Collectors.toList())) {
          flagships.add(programManager.getCrpProgramById(projectFocuses.getCrpProgram().getId()));
        }
        List<CrpProgram> regions = new ArrayList<>();
        // If has regions, add the regions to regionsArrayList
        // Get Regions related to the project sorted by acronym
        if (this.hasProgramnsRegions() != false) {
          for (ProjectFocus projectFocuses : project.getProjectFocuses().stream()
            .sorted((c1, c2) -> c1.getCrpProgram().getAcronym().compareTo(c2.getCrpProgram().getAcronym()))
            .filter(
              c -> c.isActive() && c.getCrpProgram().getProgramType() == ProgramType.REGIONAL_PROGRAM_TYPE.getValue()
                && c.getPhase() != null && c.getPhase().equals(this.getSelectedPhase()))
            .collect(Collectors.toList())) {
            regions.add(programManager.getCrpProgramById(projectFocuses.getCrpProgram().getId()));
          }
        }
        // Set Main_Query
        CompoundDataFactory cdf = CompoundDataFactory.normalize(masterReport.getDataFactory());
        TableDataFactory sdf = (TableDataFactory) cdf.getDataFactoryForQuery(masterQueryName);
        TypedTableModel model = this.getMasterTableModel(flagships, regions, projectLeader);
        sdf.addTable(masterQueryName, model);
        masterReport.setDataFactory(cdf);
        // Set i8n for pentaho
        masterReport = this.addi8nParameters(masterReport);
        // Set columns parameters (x and width)
        masterReport = this.addColumnParameters(masterReport);

        // Start Setting Planning Subreports
        // Subreport Description
        args.add(projectLeader);
        args.add(this.hasProgramnsRegions());
        this.fillSubreport((SubReport) hm.get("description"), "description", args);
        // Description Flagships
        args.clear();
        args.add(flagships);
        this.fillSubreport((SubReport) hm.get("Flagships"), "description_flagships", args);
        // Description Regions
        args.clear();
        args.add(regions);
        this.fillSubreport((SubReport) hm.get("Regions"), "description_regions", args);
        if (this.getSelectedCycle().equals("Planning")) {
          // Description CoAs
          args.clear();
          this.fillSubreport((SubReport) hm.get("Description_CoAs"), "description_coas", args);
        }

        // Subreport Partners
        this.fillSubreport((SubReport) hm.get("partners"), "partners_count", args);
        // Subreport Partner Leader
        args.clear();
        args.add(projectLeader);
        this.fillSubreport((SubReport) hm.get("partner_leader"), "institution_leader", args);
        // Subreport Partner Others
        args.clear();
        args.add(projectLeader);
        this.fillSubreport((SubReport) hm.get("partners_others"), "partners_others_ins", args);
        // Note: Contacts for partners are filled by queries inside the prpt
        // Subreport Partner Lessons
        args.clear();
        this.fillSubreport((SubReport) hm.get("partner_lessons"), "partner_lessons", args);

        // Subreport Locations
        args.clear();
        this.fillSubreport((SubReport) hm.get("locations"), "locations", args);

        if (this.getSelectedCycle().equals("Planning")) {
          // Subreport Outcomes
          args.clear();
          this.fillSubreport((SubReport) hm.get("outcomes"), "outcomes_list", args);
        } else {
          // Subreport Project Outcomes
          args.clear();
          this.fillSubreport((SubReport) hm.get("project_outcomes"), "project_outcomes", args);
          this.fillSubreport((SubReport) hm.get("other_outcomes"), "other_outcomes", args);
          this.fillSubreport((SubReport) hm.get("ccafs_outcomes"), "ccafs_outcomes", args);
          this.fillSubreport((SubReport) hm.get("other_contributions"), "other_contributions", args);
          this.fillSubreport((SubReport) hm.get("other_contributions_detail"), "other_contributions_detail", args);
          this.fillSubreport((SubReport) hm.get("other_contributions_crps"), "other_contributions_crps", args);
          this.fillSubreport((SubReport) hm.get("case_studies"), "case_studies", args);
          this.fillSubreport((SubReport) hm.get("overview_by_mogs"), "overview_by_mogs", args);
        }

        // Subreport Deliverables
        if (this.getSelectedCycle().equals("Planning")) {
          // Subreport Outcomes
          args.clear();
          this.fillSubreport((SubReport) hm.get("deliverables"), "deliverables_list", args);
        } else {
          // args.clear();
          this.fillSubreport((SubReport) hm.get("deliverables"), "deliverables_list_reporting", args);
          this.fillSubreport((SubReport) hm.get("project_highlight"), "project_highlight", args);
        }

        // Subreport Activities
        args.clear();
        if (this.getSelectedCycle().equals("Planning")) {
          this.fillSubreport((SubReport) hm.get("activities"), "activities_list", args);
        } else {
          this.fillSubreport((SubReport) hm.get("activities_reporting_list"), "activities_reporting_list", args);
        }

        if (this.getSelectedCycle().equals("Planning")) {
          // Subreport Budgets Summary
          args.clear();
          this.fillSubreport((SubReport) hm.get("budgets"), "budget_summary", args);
          // Subreport BudgetsbyPartners
          this.fillSubreport((SubReport) hm.get("budgets_by_partners"), "budgets_by_partners_list", args);
          // Subreport BudgetsbyCoas
          this.fillSubreport((SubReport) hm.get("budgets_by_coas"), "budgets_by_coas_list", args);
        } else {
          // Subreport Leverages for reporting
          this.fillSubreport((SubReport) hm.get("leverages"), "leverages", args);
        }

      }
      PdfReportUtil.createPDF(masterReport, os);
      bytesPDF = os.toByteArray();
      os.close();
    } catch (Exception e) {
      LOG.error("Error generating PDF " + e.getMessage());
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

  private void fillSubreport(SubReport subReport, String query, List<Object> args) {
    CompoundDataFactory cdf = CompoundDataFactory.normalize(subReport.getDataFactory());
    TableDataFactory sdf = (TableDataFactory) cdf.getDataFactoryForQuery(query);
    TypedTableModel model = null;
    switch (query) {
      case "description":
        if (args.get(0) instanceof ProjectPartner) {
          model = this.getDescTableModel((ProjectPartner) args.get(0), (Boolean) args.get(1));
        } else {
          model = this.getDescTableModel(new ProjectPartner(), (Boolean) args.get(1));
        }
        break;
      case "description_flagships":
        model = this.getFLTableModel((List<CrpProgram>) args.get(0));
        break;
      case "description_regions":
        model = this.getRLTableModel((List<CrpProgram>) args.get(0));
        break;
      case "description_coas":
        model = this.getDescCoAsTableModel();
        break;
      case "partners_count":
        model = this.getPartnersTableModel();
        break;
      case "institution_leader":
        if (args.get(0) instanceof ProjectPartner) {
          model = this.getPartnerLeaderTableModel((ProjectPartner) args.get(0));
        } else {
          model = this.getPartnerLeaderTableModel(new ProjectPartner());
        }
        break;
      case "partners_others_ins":
        if (args.get(0) instanceof ProjectPartner) {
          model = this.getPartnersOtherTableModel((ProjectPartner) args.get(0));
        } else {
          model = this.getPartnersOtherTableModel(new ProjectPartner());
        }
        break;
      case "partner_lessons":
        model = this.getPartnersLessonsTableModel();
        break;
      case "locations":
        model = this.getLocationsTableModel();
        break;
      case "outcomes_list":
        model = this.getOutcomesTableModel();
        break;
      case "deliverables_list":
        model = this.getDeliverablesTableModel();
        break;
      case "activities_list":
        model = this.getActivitiesTableModel();
        break;
      case "activities_reporting_list":
        model = this.getActivitiesReportingTableModel();
        break;
      case "budget_summary":
        model = this.getBudgetSummaryTableModel();
        break;
      case "budgets_by_partners_list":
        model = this.getBudgetsbyPartnersTableModel();
        break;
      case "budgets_by_coas_list":
        model = this.getBudgetsbyCoasTableModel();
        break;
      // Especific for reporting
      case "project_outcomes":
        model = this.getProjectOutcomesTableModel();
        break;
      case "other_outcomes":
        model = this.getProjectOtherOutcomesTableModel();
        break;
      case "ccafs_outcomes":
        model = this.getccafsOutcomesTableModel();
        break;
      case "other_contributions":
        model = this.getOtherContributionsTableModel();
        break;
      case "other_contributions_detail":
        model = this.getOtherContributionsDetailTableModel();
        break;
      case "other_contributions_crps":
        model = this.getOtherContributionsCrpsTableModel();
        break;
      case "case_studies":
        model = this.getCaseStudiesTableModel();
        break;
      case "overview_by_mogs":
        model = this.getOverviewByMogsTableModel();
        break;
      case "deliverables_list_reporting":
        model = this.getDeliverablesReportingTableModel();
        break;
      case "project_highlight":
        model = this.getProjectHighlightReportingTableModel();
        break;
      case "leverages":
        model = this.getLeveragesTableModel();
        break;
    }
    sdf.addTable(query, model);
    subReport.setDataFactory(cdf);
  }


  private TypedTableModel getActivitiesReportingTableModel() {
    TypedTableModel model = new TypedTableModel(
      new String[] {"activity_id", "title", "description", "start_date", "end_date", "institution", "activity_leader",
        "status", "overall"},
      new Class[] {Long.class, String.class, String.class, String.class, String.class, String.class, String.class,
        String.class, String.class},
      0);
    SimpleDateFormat formatter = new SimpleDateFormat("MMM yyyy");
    if (!project.getActivities().isEmpty()) {
      for (Activity activity : project.getActivities().stream().sorted((d1, d2) -> Long.compare(d1.getId(), d2.getId()))
        .filter(a -> a.isActive() && a.getActivityStatus() != null
          && (a.getActivityStatus() == 2 || a.getActivityStatus() == 4 || a.getActivityStatus() == 3)
          && a.getStartDate() != null && a.getEndDate() != null && a.getPhase() != null
          && a.getPhase().equals(this.getSelectedPhase()))
        .collect(Collectors.toList())) {
        // Filter by date
        Calendar cal = Calendar.getInstance();
        cal.setTime(activity.getStartDate());
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(activity.getEndDate());
        if (cal.get(Calendar.YEAR) >= this.getSelectedYear() || cal2.get(Calendar.YEAR) >= this.getSelectedYear()) {
          String institution = null;
          String activityLeader = null;
          String status = null;
          String startDate = null;
          String endDate = null;
          String overall = null;
          if (activity.getStartDate() != null) {
            startDate = formatter.format(activity.getStartDate());
          }
          if (activity.getEndDate() != null) {
            endDate = formatter.format(activity.getEndDate());
          }
          if (activity.getProjectPartnerPerson() != null && activity.getProjectPartnerPerson().isActive()) {
            institution = activity.getProjectPartnerPerson().getProjectPartner().getInstitution().getComposedName();
            activityLeader = activity.getProjectPartnerPerson().getUser().getComposedName() + "\n&lt;"
              + activity.getProjectPartnerPerson().getUser().getEmail() + "&gt;";
          }
          status = ProjectStatusEnum.getValue(activity.getActivityStatus().intValue()).getStatus();
          // Reporting
          if (activity.getActivityProgress() != null && !activity.getActivityProgress().isEmpty()) {
            overall = activity.getActivityProgress();
          }
          model.addRow(new Object[] {activity.getId(), activity.getTitle(), activity.getDescription(), startDate,
            endDate, institution, activityLeader, status, overall});
        }
      }
    }
    return model;
  }


  private TypedTableModel getActivitiesTableModel() {
    TypedTableModel model = new TypedTableModel(
      new String[] {"activity_id", "title", "description", "start_date", "end_date", "institution", "activity_leader",
        "status"},
      new Class[] {Long.class, String.class, String.class, String.class, String.class, String.class, String.class,
        String.class},
      0);
    SimpleDateFormat formatter = new SimpleDateFormat("MMM yyyy");
    if (!project.getActivities().isEmpty()) {
      for (Activity activity : project.getActivities().stream().sorted((d1, d2) -> Long.compare(d1.getId(), d2.getId()))
        .filter(a -> a.isActive() && (a.getActivityStatus() == 2 || a.getActivityStatus() == 4) && a.getPhase() != null
          && a.getPhase().equals(this.getSelectedPhase()))
        .collect(Collectors.toList())) {
        String institution = null;
        String activityLeader = null;
        String status = null;
        String startDate = null;
        String endDate = null;
        if (activity.getStartDate() != null) {
          startDate = formatter.format(activity.getStartDate());
        }
        if (activity.getEndDate() != null) {
          endDate = formatter.format(activity.getEndDate());
        }
        if (activity.getProjectPartnerPerson() != null) {
          institution = activity.getProjectPartnerPerson().getProjectPartner().getInstitution().getComposedName();
          activityLeader = activity.getProjectPartnerPerson().getUser().getComposedName() + "\n&lt;"
            + activity.getProjectPartnerPerson().getUser().getEmail() + "&gt;";
        }
        status = ProjectStatusEnum.getValue(activity.getActivityStatus().intValue()).getStatus();
        model.addRow(new Object[] {activity.getId(), activity.getTitle(), activity.getDescription(), startDate, endDate,
          institution, activityLeader, status});
      }
    }
    return model;
  }

  public ProjectBudgetsCluserActvity getBudgetbyCoa(Long activitiyId, int year, long type) {
    for (ProjectBudgetsCluserActvity pb : project.getProjectBudgetsCluserActvities().stream()
      .filter(pb -> pb.isActive() && pb.getYear() == year && pb.getCrpClusterOfActivity() != null
        && pb.getCrpClusterOfActivity().getId() == activitiyId && type == pb.getBudgetType().getId()
        && pb.getPhase() != null && pb.getPhase().equals(this.getSelectedPhase()))
      .collect(Collectors.toList())) {
      return pb;
    }
    return null;
  }

  private TypedTableModel getBudgetsbyCoasTableModel() {
    DecimalFormat df = new DecimalFormat("###,###.00");
    TypedTableModel model = new TypedTableModel(
      new String[] {"description", "year", "w1w2", "w3", "bilateral", "center", "w1w2GenderPer", "w3GenderPer",
        "bilateralGenderPer", "centerGenderPer", "w1w2CoFinancing", "w1w2CoFinancingGenderPer", "hasW1W2Co",
        "totalW1w2", "totalW3", "totalBilateral", "totalCenter", "totalW1w2Gender", "totalW3Gender",
        "totalBilateralGender", "totalCenterGender", "totalW1w2Co", "totalW1w2CoGender"},
      new Class[] {String.class, Integer.class, String.class, String.class, String.class, String.class, String.class,
        String.class, String.class, String.class, String.class, String.class, Boolean.class, String.class, String.class,
        String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class},
      0);
    Boolean hasW1W2CoTemp = false;
    List<ProjectClusterActivity> coAs = new ArrayList<>();
    coAs = project.getProjectClusterActivities().stream()
      .filter(c -> c.isActive() && c.getPhase() != null && c.getPhase().equals(this.getSelectedPhase()))
      .collect(Collectors.toList());
    /*     */
    Double totalW1w2 = 0.0, totalW3 = 0.0, totalBilateral = 0.0, totalCenter = 0.0, totalW1w2Gender = 0.0,
      totalW3Gender = 0.0, totalBilateralGender = 0.0, totalCenterGender = 0.0, totalW1w2Co = 0.0,
      totalW1w2CoGender = 0.0;

    // get total budget per year
    if (coAs.size() == 1 && this.hasW1W2Co) {
      hasW1W2CoTemp = true;
      // W1W2 no including co
      totalW1w2 = this.getTotalYear(this.getSelectedYear(), 1, project, 3);
      // W1W2 including co
      totalW1w2Co = this.getTotalYear(this.getSelectedYear(), 1, project, 2);

    } else {
      totalW1w2 = this.getTotalYear(this.getSelectedYear(), 1, project, 1);
    }

    totalW3 = this.getTotalYear(this.getSelectedYear(), 2, project, 1);
    totalBilateral = this.getTotalYear(this.getSelectedYear(), 3, project, 1);
    totalCenter = this.getTotalYear(this.getSelectedYear(), 4, project, 1);

    // get total gender per year
    for (ProjectPartner pp : project.getProjectPartners().stream()
      .filter(pp -> pp.isActive() && pp.getPhase() != null && pp.getPhase().equals(this.getSelectedPhase()))
      .collect(Collectors.toList())) {
      if (this.isPPA(pp.getInstitution())) {
        if (coAs.size() == 1 && this.hasW1W2Co) {
          totalW1w2CoGender += this.getTotalGender(pp.getInstitution().getId(), this.getSelectedYear(), 1, project, 2);
          totalW1w2Gender += this.getTotalGender(pp.getInstitution().getId(), this.getSelectedYear(), 1, project, 3);
        } else {
          totalW1w2Gender += this.getTotalGender(pp.getInstitution().getId(), this.getSelectedYear(), 1, project, 1);
        }
        totalW3Gender += this.getTotalGender(pp.getInstitution().getId(), this.getSelectedYear(), 2, project, 1);
        totalBilateralGender += this.getTotalGender(pp.getInstitution().getId(), this.getSelectedYear(), 3, project, 1);
        totalCenterGender += this.getTotalGender(pp.getInstitution().getId(), this.getSelectedYear(), 4, project, 1);
      }
    }
    /**/
    if (coAs.size() == 1) {
      ProjectClusterActivity projectClusterActivity = coAs.get(0);
      String description = projectClusterActivity.getCrpClusterOfActivity().getComposedName();
      String w1w2 = null;
      String w1w2GenderPer = null;
      String w3 = null;
      String w3GenderPer = null;
      String bilateral = null;
      String bilateralGenderPer = null;
      String center = null;
      String centerGenderPer = null;
      String w1w2CoFinancing = null;
      String w1w2CoFinancingGenderPer = null;

      // Get types of funding sources
      for (ProjectBudget pb : project
        .getProjectBudgets().stream().filter(pb -> pb.isActive() && pb.getYear() == this.getSelectedYear()
          && pb.getBudgetType() != null && pb.getPhase() != null && pb.getPhase().equals(this.getSelectedPhase()))
        .collect(Collectors.toList())) {

        if (pb.getBudgetType().getId() == 1 && pb.getFundingSource() != null
          && pb.getFundingSource().getFundingSourceInfo(this.getSelectedPhase()).getW1w2() != null
          && pb.getFundingSource().getFundingSourceInfo(this.getSelectedPhase()).getW1w2()) {
          w1w2CoFinancing = "100";
          w1w2CoFinancingGenderPer = "100";
        } else if (pb.getBudgetType().getId() == 1) {
          w1w2 = "100";
          w1w2GenderPer = "100";
        }


        if (pb.getBudgetType().getId() == 2) {
          w3 = "100";
          w3GenderPer = "100";
        }
        if (pb.getBudgetType().getId() == 3) {
          bilateral = "100";
          bilateralGenderPer = "100";
        }
        if (pb.getBudgetType().getId() == 4) {
          center = "100";
          centerGenderPer = "100";
        }
      }
      model.addRow(new Object[] {description, this.getSelectedYear(), w1w2, w3, bilateral, center, w1w2GenderPer,
        w3GenderPer, bilateralGenderPer, centerGenderPer, w1w2CoFinancing, w1w2CoFinancingGenderPer, hasW1W2CoTemp,
        df.format(totalW1w2), df.format(totalW3), df.format(totalBilateral), df.format(totalCenter),
        df.format(totalW1w2Gender), df.format(totalW3Gender), df.format(totalBilateralGender),
        df.format(totalCenterGender), df.format(totalW1w2Co), df.format(totalW1w2CoGender)});
    } else {
      for (ProjectClusterActivity clusterActivity : coAs) {
        String description = clusterActivity.getCrpClusterOfActivity().getComposedName();
        String w1w2Percentage = null;
        String w1w2GenderPer = null;
        String w3Percentage = null;
        String w3GenderPer = null;
        String bilateralPercentage = null;
        String bilateralGenderPer = null;
        String centerPercentage = null;
        String centerGenderPer = null;
        String w1w2CoFinancingPercentage = null;
        String w1w2CoFinancingGenderPer = null;
        // budget
        Double w1w2 = 0.0;
        Double w3 = 0.0;
        Double bilateral = 0.0;
        Double center = 0.0;
        Double w1w2Gender = 0.0;
        Double w3Gender = 0.0;
        Double bilateralGender = 0.0;
        Double centerGender = 0.0;


        ProjectBudgetsCluserActvity w1w2pb =
          this.getBudgetbyCoa(clusterActivity.getCrpClusterOfActivity().getId(), this.getSelectedYear(), 1);
        if (w1w2pb != null) {
          w1w2Percentage = df.format(w1w2pb.getAmount());
          if (w1w2pb.getGenderPercentage() != null) {
            w1w2GenderPer = df.format(w1w2pb.getGenderPercentage());
            w1w2Gender = (w1w2pb.getGenderPercentage() * totalW1w2Gender) / 100;
          }
          w1w2 = (w1w2pb.getAmount() * totalW1w2) / 100;
        }

        ProjectBudgetsCluserActvity w3pb =
          this.getBudgetbyCoa(clusterActivity.getCrpClusterOfActivity().getId(), this.getSelectedYear(), 2);
        ProjectBudgetsCluserActvity bilateralpb =
          this.getBudgetbyCoa(clusterActivity.getCrpClusterOfActivity().getId(), this.getSelectedYear(), 3);
        ProjectBudgetsCluserActvity centerpb =
          this.getBudgetbyCoa(clusterActivity.getCrpClusterOfActivity().getId(), this.getSelectedYear(), 4);

        if (w3pb != null) {
          w3Percentage = df.format(w3pb.getAmount());
          if (w3pb.getGenderPercentage() != null) {
            w3GenderPer = df.format(w3pb.getGenderPercentage());
            w3Gender = (w3pb.getGenderPercentage() * totalW3Gender) / 100;
          }
          w3 = (w3pb.getAmount() * totalW3) / 100;
        }
        if (bilateralpb != null) {
          bilateralPercentage = df.format(bilateralpb.getAmount());
          if (bilateralpb.getGenderPercentage() != null) {
            bilateralGenderPer = df.format(bilateralpb.getGenderPercentage());
            bilateralGender = (bilateralpb.getGenderPercentage() * totalBilateralGender) / 100;
          }
          bilateral = (bilateralpb.getAmount() * totalBilateral) / 100;
        }
        if (centerpb != null) {
          centerPercentage = df.format(centerpb.getAmount());
          if (centerpb.getGenderPercentage() != null) {
            centerGenderPer = df.format(centerpb.getGenderPercentage());
            centerGender = (centerpb.getGenderPercentage() * totalCenterGender) / 100;
          }
          center = (centerpb.getAmount() * totalCenter) / 100;
        }
        model.addRow(new Object[] {description, this.getSelectedYear(), w1w2Percentage, w3Percentage,
          bilateralPercentage, centerPercentage, w1w2GenderPer, w3GenderPer, bilateralGenderPer, centerGenderPer,
          w1w2CoFinancingPercentage, w1w2CoFinancingGenderPer, hasW1W2CoTemp, df.format(w1w2), df.format(w3),
          df.format(bilateral), df.format(center), df.format(w1w2Gender), df.format(w3Gender),
          df.format(bilateralGender), df.format(centerGender), null, null});
      }
    }
    return model;
  }

  private TypedTableModel getBudgetsbyPartnersTableModel() {
    TypedTableModel model = new TypedTableModel(
      new String[] {"year", "institution", "w1w2", "w3", "bilateral", "center", "institution_id", "p_id", "w1w2Gender",
        "w3Gender", "bilateralGender", "centerGender", "w1w2GAmount", "w3GAmount", "bilateralGAmount", "centerGAmount",
        "w1w2CoFinancing", "w1w2CoFinancingGender", "w1w2CoFinancingGAmount", "partner_total"},
      new Class[] {Integer.class, String.class, String.class, String.class, String.class, String.class, Long.class,
        Long.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class,
        String.class, String.class, String.class, String.class, String.class},
      0);
    // Get ppaPartners of project
    for (ProjectPartner pp : project.getProjectPartners().stream()
      .filter(pp -> pp.isActive() && pp.getPhase() != null && pp.getPhase().equals(this.getSelectedPhase()))
      .collect(Collectors.toList())) {
      if (this.isPPA(pp.getInstitution())) {
        DecimalFormat myFormatter = new DecimalFormat("###,###.00");
        String w1w2Budget = null;
        String w1w2CoBudget = null;
        String w1w2Gender = null;
        String w1w2CoGender = null;
        String w1w2GAmount = null;
        String w1w2CoGAmount = null;
        // Partner Total
        String partnerTotal = null;
        double partnerTotald = 0.0;

        if (hasW1W2Co) {
          w1w2Budget = myFormatter.format(Double.parseDouble(
            this.getTotalAmount(pp.getInstitution().getId(), this.getSelectedYear(), 1, project.getId(), 3)));
          w1w2CoBudget = myFormatter.format(Double.parseDouble(
            this.getTotalAmount(pp.getInstitution().getId(), this.getSelectedYear(), 1, project.getId(), 2)));

          w1w2Gender = myFormatter
            .format(this.getTotalGenderPer(pp.getInstitution().getId(), this.getSelectedYear(), 1, project.getId(), 3));
          w1w2CoGender = myFormatter
            .format(this.getTotalGenderPer(pp.getInstitution().getId(), this.getSelectedYear(), 1, project.getId(), 2));

          w1w2GAmount = myFormatter
            .format(this.getTotalGender(pp.getInstitution().getId(), this.getSelectedYear(), 1, project.getId(), 3));
          w1w2CoGAmount = myFormatter
            .format(this.getTotalGender(pp.getInstitution().getId(), this.getSelectedYear(), 1, project.getId(), 2));

          // increment partner total
          partnerTotald += Double.parseDouble(
            this.getTotalAmount(pp.getInstitution().getId(), this.getSelectedYear(), 1, project.getId(), 3));
          partnerTotald += Double.parseDouble(
            this.getTotalAmount(pp.getInstitution().getId(), this.getSelectedYear(), 1, project.getId(), 2));
        } else {
          w1w2Budget = myFormatter.format(Double.parseDouble(
            this.getTotalAmount(pp.getInstitution().getId(), this.getSelectedYear(), 1, project.getId(), 1)));
          w1w2Gender = myFormatter
            .format(this.getTotalGenderPer(pp.getInstitution().getId(), this.getSelectedYear(), 1, project.getId(), 1));
          w1w2GAmount = myFormatter
            .format(this.getTotalGender(pp.getInstitution().getId(), this.getSelectedYear(), 1, project.getId(), 1));
          w1w2CoBudget = myFormatter.format(0.0);
          w1w2CoGender = myFormatter.format(0.0);
          w1w2CoGAmount = myFormatter.format(0.0);

          // increment partner total
          partnerTotald += Double.parseDouble(
            this.getTotalAmount(pp.getInstitution().getId(), this.getSelectedYear(), 1, project.getId(), 1));
        }

        String w3Budget = myFormatter.format(Double.parseDouble(
          this.getTotalAmount(pp.getInstitution().getId(), this.getSelectedYear(), 2, project.getId(), 1)));
        String bilateralBudget = myFormatter.format(Double.parseDouble(
          this.getTotalAmount(pp.getInstitution().getId(), this.getSelectedYear(), 3, project.getId(), 1)));
        String centerBudget = myFormatter.format(Double.parseDouble(
          this.getTotalAmount(pp.getInstitution().getId(), this.getSelectedYear(), 4, project.getId(), 1)));


        String w3Gender = myFormatter
          .format(this.getTotalGenderPer(pp.getInstitution().getId(), this.getSelectedYear(), 2, project.getId(), 1));
        String bilateralGender = myFormatter
          .format(this.getTotalGenderPer(pp.getInstitution().getId(), this.getSelectedYear(), 3, project.getId(), 1));
        String centerGender = myFormatter
          .format(this.getTotalGenderPer(pp.getInstitution().getId(), this.getSelectedYear(), 4, project.getId(), 1));


        String w3GAmount = myFormatter
          .format(this.getTotalGender(pp.getInstitution().getId(), this.getSelectedYear(), 2, project.getId(), 1));
        String bilateralGAmount = myFormatter
          .format(this.getTotalGender(pp.getInstitution().getId(), this.getSelectedYear(), 3, project.getId(), 1));
        String centerGAmount = myFormatter
          .format(this.getTotalGender(pp.getInstitution().getId(), this.getSelectedYear(), 4, project.getId(), 1));

        // increment partner total
        partnerTotald += Double
          .parseDouble(this.getTotalAmount(pp.getInstitution().getId(), this.getSelectedYear(), 2, project.getId(), 1));
        partnerTotald += Double
          .parseDouble(this.getTotalAmount(pp.getInstitution().getId(), this.getSelectedYear(), 3, project.getId(), 1));
        partnerTotald += Double
          .parseDouble(this.getTotalAmount(pp.getInstitution().getId(), this.getSelectedYear(), 4, project.getId(), 1));

        // set partner total
        partnerTotal = "$" + myFormatter.format(partnerTotald);

        model.addRow(new Object[] {this.getSelectedYear(), pp.getInstitution().getComposedName(), w1w2Budget, w3Budget,
          bilateralBudget, centerBudget, pp.getInstitution().getId(), projectID, w1w2Gender, w3Gender, bilateralGender,
          centerGender, w1w2GAmount, w3GAmount, bilateralGAmount, centerGAmount, w1w2CoBudget, w1w2CoGender,
          w1w2CoGAmount, partnerTotal});
      }
    }
    return model;
  }

  private TypedTableModel getBudgetSummaryTableModel() {
    TypedTableModel model = new TypedTableModel(
      new String[] {"year", "w1w2", "w3", "bilateral", "centerfunds", "w1w2CoFinancing", "grand_total"},
      new Class[] {Integer.class, String.class, String.class, String.class, String.class, String.class, String.class},
      0);
    String w1w2 = null;
    String w3 = null;
    String bilateral = null;
    String centerfunds = null;
    String w1w2CoFinancing = null;
    // Budget Total
    String grand_total = null;
    double grand_totald = 0.0;
    // Decimal format
    DecimalFormat myFormatter = new DecimalFormat("###,###.00");

    if (hasW1W2Co) {
      w1w2 = myFormatter.format(this.getTotalYear(this.getSelectedYear(), 1, project, 3));
      w1w2CoFinancing = myFormatter.format(this.getTotalYear(this.getSelectedYear(), 1, project, 2));
      // increment Budget Total with w1w2 cofinancing
      grand_totald += this.getTotalYear(this.getSelectedYear(), 1, project, 3)
        + this.getTotalYear(this.getSelectedYear(), 1, project, 2);
    } else {
      w1w2 = myFormatter.format(this.getTotalYear(this.getSelectedYear(), 1, project, 1));
      // increment Budget Total with w1w2
      grand_totald += this.getTotalYear(this.getSelectedYear(), 1, project, 1);
    }
    w3 = myFormatter.format(this.getTotalYear(this.getSelectedYear(), 2, project, 1));
    bilateral = myFormatter.format(this.getTotalYear(this.getSelectedYear(), 3, project, 1));
    centerfunds = myFormatter.format(this.getTotalYear(this.getSelectedYear(), 4, project, 1));
    // increment Budget Total with w3,bilateral and centerfunds
    grand_totald += this.getTotalYear(this.getSelectedYear(), 2, project, 1)
      + this.getTotalYear(this.getSelectedYear(), 3, project, 1)
      + this.getTotalYear(this.getSelectedYear(), 4, project, 1);
    grand_total = "$" + myFormatter.format(grand_totald);

    model.addRow(new Object[] {this.getSelectedYear(), w1w2, w3, bilateral, centerfunds, w1w2CoFinancing, grand_total});
    return model;
  }

  public byte[] getBytesPDF() {
    return bytesPDF;
  }

  private TypedTableModel getCaseStudiesTableModel() {
    // Code Author: Hermes Jimenez
    TypedTableModel model = new TypedTableModel(
      new String[] {"id", "title", "outcomeStatement", "researchOutputs", "researchPartners", "activities",
        "nonResearchPartneres", "outputUsers", "evidenceOutcome", "outputUsed", "referencesCase",
        "explainIndicatorRelation", "anex", "owner", "indicators", "shared", "year"},
      new Class[] {Long.class, String.class, String.class, String.class, String.class, String.class, String.class,
        String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class,
        String.class, String.class},
      0);
    Long id = null;
    String title = "", outcomeStatement = "", researchOutputs = "", researchPartners = "", activities = "",
      nonResearchPartneres = "", outputUsers = "", evidenceOutcome = "", outputUsed = "", referencesCase = "",
      explainIndicatorRelation = "", anex = "", owner = "", shared = "", indicators = "", year = "";
    for (CaseStudyProject caseStudyProject : project
      .getCaseStudyProjects().stream().filter(csp -> csp.isActive() && csp.getCaseStudy() != null
        && csp.getCaseStudy().getYear() != null && csp.getCaseStudy().getYear() >= this.getSelectedYear())
      .collect(Collectors.toList())) {
      CaseStudy caseStudy = caseStudyProject.getCaseStudy();
      id = caseStudy.getId();
      year = String.valueOf(caseStudy.getYear());
      title = caseStudy.getTitle().trim().isEmpty() ? null : caseStudy.getTitle();
      outcomeStatement = caseStudy.getOutcomeStatement().trim().isEmpty() ? null : caseStudy.getOutcomeStatement();
      researchOutputs = caseStudy.getResearchOutputs().trim().isEmpty() ? null : caseStudy.getResearchOutputs();
      researchPartners = caseStudy.getResearchPartners().trim().isEmpty() ? null : caseStudy.getResearchPartners();
      activities = caseStudy.getActivities().trim().isEmpty() ? null : caseStudy.getActivities();
      nonResearchPartneres =
        caseStudy.getNonResearchPartneres().trim().isEmpty() ? null : caseStudy.getNonResearchPartneres();
      outputUsers = caseStudy.getOutputUsers().trim().isEmpty() ? null : caseStudy.getOutputUsers();
      outputUsed = caseStudy.getOutputUsed().trim().isEmpty() ? null : caseStudy.getOutputUsed();
      evidenceOutcome = caseStudy.getEvidenceOutcome().trim().isEmpty() ? null : caseStudy.getEvidenceOutcome();
      referencesCase = caseStudy.getReferencesCase().trim().isEmpty() ? null : caseStudy.getReferencesCase();
      explainIndicatorRelation =
        caseStudy.getExplainIndicatorRelation().trim().isEmpty() ? null : caseStudy.getExplainIndicatorRelation();
      List<CaseStudyProject> studyProjects = new ArrayList<>(
        caseStudy.getCaseStudyProjects().stream().filter(csp -> csp.isActive()).collect(Collectors.toList()));
      for (CaseStudyProject caseStudyProjectList : studyProjects) {
        if (caseStudyProjectList.isCreated()) {
          shared = String.valueOf(caseStudyProjectList.getProject().getId());
        }
        owner = "P" + caseStudyProjectList.getProject().getId();
      }
      List<CaseStudyIndicator> studyIndicators = new ArrayList<>(
        caseStudy.getCaseStudyIndicators().stream().filter(c -> c.isActive()).collect(Collectors.toList()));
      StringBuilder indicatorsS = new StringBuilder();
      for (CaseStudyIndicator caseStudyIndicator : studyIndicators) {
        if (caseStudyIndicator.isActive()) {
          indicatorsS.append("● " + caseStudyIndicator.getIpIndicator().getDescription() + "<br>");
        }
      }
      indicators = indicatorsS.toString();
      if (caseStudy.getFile() != null) {
        anex = (this.getCaseStudyUrl(shared) + caseStudy.getFile().getFileName()).replace(" ", "%20");
      }
      model.addRow(new Object[] {id, title, outcomeStatement, researchOutputs, researchPartners, activities,
        nonResearchPartneres, outputUsers, evidenceOutcome, outputUsed, referencesCase, explainIndicatorRelation, anex,
        owner.trim(), indicators.trim(), shared.trim(), year});
    }
    return model;
  }

  public String getCaseStudyUrl(String project) {
    return config.getDownloadURL() + "/" + this.getCaseStudyUrlPath(project).replace('\\', '/');
  }

  public String getCaseStudyUrlPath(String project) {
    return config.getProjectsBaseFolder(this.getCrpSession()) + File.separator + project + File.separator + "caseStudy"
      + File.separator;
  }

  private TypedTableModel getccafsOutcomesTableModel() {
    TypedTableModel model = new TypedTableModel(
      new String[] {"program_outcome", "program_outcome_description", "indicator", "indicator_description",
        "ipProjectIndicatoryear", "target_value", "target_cumulative", "target_achieved", "target_narrative",
        "target_achieved_narrative", "achieved_annual_gender", "annual_gender", "is_current", "showOutcome",
        "showIndicator", "showOutputs", "outputs"},
      new Class[] {String.class, String.class, String.class, String.class, Integer.class, String.class, String.class,
        String.class, String.class, String.class, String.class, String.class, Boolean.class, Boolean.class,
        Boolean.class, Boolean.class, String.class},
      0);
    project.setProjectIndicators(
      project.getIpProjectIndicators().stream().filter(c -> c.isActive()).collect(Collectors.toList()));
    // Get list of outcomes
    Set<IpElement> outcomesList = new HashSet<>();
    for (IpProjectIndicator ipProjectIndicator : project.getIpProjectIndicators().stream().filter(i -> i.isActive())
      .collect(Collectors.toList())) {
      IpElement ipElement = ipElementManager.getIpElementById(ipProjectIndicator.getOutcomeId());
      outcomesList.add(ipElement);
    }
    for (IpElement outcome : outcomesList) {
      // System.out.println(outcome.getId());
      Boolean showOutcome = true;
      // Get list of indicators
      Set<IpIndicator> indicatorsList = new HashSet<>();
      for (IpProjectIndicator ipProjectIndicator : project.getIpProjectIndicators().stream()
        .filter(i -> i.isActive() && i.getOutcomeId() == outcome.getId().intValue()
          && (i.getYear() == this.getSelectedYear() || i.getYear() == this.getSelectedYear() - 1
            || i.getYear() == this.getSelectedYear() + 1 || i.getYear() == APConstants.MID_OUTCOME_YEAR))
        .collect(Collectors.toList())) {
        IpIndicator ipIndicator = ipProjectIndicator.getIpIndicator();
        // System.out.println("Current " + ipIndicator.getId());
        // System.out.println("Final " + this.getFinalIndicator(ipIndicator).getId());
        indicatorsList.add(this.getFinalIndicator(ipIndicator));
      }
      int ultimoIndicator = indicatorsList.size();
      int ultimoIndicatorCount = 0;
      Boolean showOutputs = false;
      int indicatorNumber = 0;
      for (IpIndicator ipIndicator : indicatorsList) {
        indicatorNumber++;
        ultimoIndicatorCount++;
        Boolean showIndicator = true;
        int ultimoProjectIndicator = (int) project.getIpProjectIndicators().stream()
          .filter(
            i -> i.isActive() && i.getOutcomeId() == outcome.getId().intValue() && i.getIpIndicator() == ipIndicator
              && (i.getYear() == this.getSelectedYear() || i.getYear() == this.getSelectedYear() - 1
                || i.getYear() == this.getSelectedYear() + 1 || i.getYear() == APConstants.MID_OUTCOME_YEAR))
          .count();
        int ultimoProjectIndicatorCount = 0;
        for (IpProjectIndicator ipProjectIndicator : project.getIpProjectIndicators().stream()
          .filter(
            i -> i.isActive() && i.getOutcomeId() == outcome.getId().intValue() && i.getIpIndicator() == ipIndicator
              && (i.getYear() == this.getSelectedYear() || i.getYear() == this.getSelectedYear() - 1
                || i.getYear() == this.getSelectedYear() + 1 || i.getYear() == APConstants.MID_OUTCOME_YEAR))
          .collect(Collectors.toList())) {
          ultimoProjectIndicatorCount++;
          String programOutcome = null, programOutcomeDescription = null, indicator = null, indicatorDescription = null,
            targetValue = null, targetCumulative = null, targetNarrative = null, targetAchievedNarrative = null,
            achievedAnnualGender = null, annualGender = null, outputs = "";
          Integer ipProjectIndicatoryear = null;
          Double targetAchieved = 0.0;
          Boolean isCurrent = false;
          if (outcome.getIpProgram() != null && !outcome.getIpProgram().getAcronym().isEmpty()) {
            programOutcome = outcome.getIpProgram().getAcronym() + " Outcome " + APConstants.MID_OUTCOME_YEAR;
          }
          if (outcome.getDescription() != null && !outcome.getDescription().isEmpty()) {
            programOutcomeDescription = outcome.getDescription();
          }
          if (ipProjectIndicator.getIpIndicator() != null) {
            indicatorDescription = this.getFinalIndicator(ipProjectIndicator.getIpIndicator()).getDescription();
            indicator = "Indicator #" + indicatorNumber;
          }
          ipProjectIndicatoryear = ipProjectIndicator.getYear();
          if (ipProjectIndicator.getTarget() != null && !ipProjectIndicator.getTarget().isEmpty()) {
            targetValue = ipProjectIndicator.getTarget();
          }
          targetCumulative = this.calculateAcumulativeTarget(ipProjectIndicator.getYear(), ipProjectIndicator);
          if (ipProjectIndicator.getArchived() != null) {
            targetAchieved = ipProjectIndicator.getArchived();
          }
          if (ipProjectIndicator.getDescription() != null && !ipProjectIndicator.getDescription().isEmpty()) {
            targetNarrative = ipProjectIndicator.getDescription();
          }
          if (ipProjectIndicator.getNarrativeTargets() != null && !ipProjectIndicator.getNarrativeTargets().isEmpty()) {
            targetAchievedNarrative = ipProjectIndicator.getNarrativeTargets();
          }
          if (ipProjectIndicator.getNarrativeGender() != null && !ipProjectIndicator.getNarrativeGender().isEmpty()) {
            achievedAnnualGender = ipProjectIndicator.getNarrativeGender();
          }
          if (ipProjectIndicator.getGender() != null && !ipProjectIndicator.getGender().isEmpty()) {
            annualGender = ipProjectIndicator.getGender();
          }
          if (ipProjectIndicator.getYear() == this.getSelectedYear()) {
            isCurrent = true;
          }
          if (ultimoProjectIndicatorCount == ultimoProjectIndicator && ultimoIndicatorCount == ultimoIndicator) {
            showOutputs = true;
            List<IpElement> outputsList = this.getMidOutcomeOutputs(outcome.getId());
            for (IpElement ipElement : outputsList) {
              outputs += "● " + ipElement.getIpProgram().getAcronym() + ": " + ipElement.getDescription() + "<br><br>";
            }
          }
          if (outputs.isEmpty()) {
            outputs = null;
          }
          model.addRow(new Object[] {programOutcome, programOutcomeDescription, indicator, indicatorDescription,
            ipProjectIndicatoryear, targetValue, targetCumulative, targetAchieved, targetNarrative,
            targetAchievedNarrative, achievedAnnualGender, annualGender, isCurrent, showOutcome, showIndicator,
            showOutputs, outputs});
          showOutcome = false;
          showIndicator = false;
        }
      }
    }
    return model;
  }

  @Override
  public int getContentLength() {
    return bytesPDF.length;
  }

  @Override
  public String getContentType() {
    return "application/pdf";
  }

  private String getDeliverableDataSharingFilePath() {
    String upload = config.getDownloadURL();
    return upload + "/" + this.getDeliverableDataSharingFileRelativePath().replace('\\', '/');
  }

  private String getDeliverableDataSharingFileRelativePath() {
    return config.getProjectsBaseFolder(this.getCrpSession()) + File.separator + projectID + File.separator
      + "deliverableDataSharing" + File.separator;
  }

  private TypedTableModel getDeliverablesReportingTableModel() {
    TypedTableModel model = new TypedTableModel(
      new String[] {"deliverable_id", "title", "deliv_type", "deliv_sub_type", "deliv_status", "deliv_year",
        "key_output", "leader", "institution", "funding_sources", "cross_cutting", "deliv_new_year",
        "deliv_new_year_justification", "deliv_dissemination_channel", "deliv_dissemination_url", "deliv_open_access",
        "deliv_license", "titleMetadata", "descriptionMetadata", "dateMetadata", "languageMetadata", "countryMetadata",
        "keywordsMetadata", "citationMetadata", "HandleMetadata", "DOIMetadata", "creator_authors", "data_sharing",
        "qualityAssurance", "dataDictionary", "tools", "showFAIR", "F", "A", "I", "R", "isDisseminated", "disseminated",
        "restricted_access", "isRestricted", "restricted_date", "isLastTwoRestricted", "deliv_license_modifications",
        "show_deliv_license_modifications", "volume", "issue", "pages", "journal", "journal_indicators", "acknowledge",
        "fl_contrib", "show_publication", "showCompilance"},
      new Class[] {Long.class, String.class, String.class, String.class, String.class, String.class, String.class,
        String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class,
        String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class,
        String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class,
        Boolean.class, String.class, String.class, String.class, String.class, Boolean.class, String.class,
        String.class, Boolean.class, String.class, Boolean.class, String.class, Boolean.class, String.class,
        String.class, String.class, String.class, String.class, String.class, String.class, Boolean.class,
        Boolean.class},
      0);
    if (!project.getDeliverables().isEmpty()) {
      // get Reporting deliverables
      List<Deliverable> deliverables = new ArrayList<>(project.getDeliverables().stream().filter(d -> d.isActive()
        && d.getProject() != null && d.getProject().isActive()
        && d.getProject().getProjecInfoPhase(this.getSelectedPhase()).getReporting() != null
        && d.getProject().getProjecInfoPhase(this.getSelectedPhase()).getReporting() && d.getProject().getCrp() != null
        && d.getProject().getCrp().getId().equals(this.getLoggedCrp().getId())
        && d.getDeliverableInfo(this.getSelectedPhase()).getStatus() != null
        && ((d.getDeliverableInfo(this.getSelectedPhase()).getStatus().intValue() == Integer
          .parseInt(ProjectStatusEnum.Complete.getStatusId())
          && (d.getDeliverableInfo(this.getSelectedPhase()).getYear() >= this.getSelectedYear()
            || (d.getDeliverableInfo(this.getSelectedPhase()).getNewExpectedYear() != null && d
              .getDeliverableInfo(this.getSelectedPhase()).getNewExpectedYear().intValue() >= this.getSelectedYear())))
          || (d.getDeliverableInfo(this.getSelectedPhase()).getStatus().intValue() == Integer
            .parseInt(ProjectStatusEnum.Extended.getStatusId())
            && (d.getDeliverableInfo(this.getSelectedPhase()).getNewExpectedYear() != null && d
              .getDeliverableInfo(this.getSelectedPhase()).getNewExpectedYear().intValue() == this.getSelectedYear()))
          || (d.getDeliverableInfo(this.getSelectedPhase()).getStatus().intValue() == Integer
            .parseInt(ProjectStatusEnum.Cancelled.getStatusId())
            && (d.getDeliverableInfo(this.getSelectedPhase()).getYear() == this.getSelectedYear()
              || (d.getDeliverableInfo(this.getSelectedPhase()).getNewExpectedYear() != null
                && d.getDeliverableInfo(this.getSelectedPhase()).getNewExpectedYear().intValue() == this
                  .getSelectedYear()))))
        && (d.getDeliverableInfo(this.getSelectedPhase()).getStatus().intValue() == Integer
          .parseInt(ProjectStatusEnum.Extended.getStatusId())
          || d.getDeliverableInfo(this.getSelectedPhase()).getStatus().intValue() == Integer
            .parseInt(ProjectStatusEnum.Complete.getStatusId())
          || d.getDeliverableInfo(this.getSelectedPhase()).getStatus().intValue() == Integer
            .parseInt(ProjectStatusEnum.Cancelled.getStatusId())))
        .collect(Collectors.toList()));
      deliverables
        .sort((p1, p2) -> p1.getDeliverableInfo(this.getSelectedPhase()).isRequieriedReporting(this.getSelectedYear())
          .compareTo(p2.getDeliverableInfo(this.getSelectedPhase()).isRequieriedReporting(this.getSelectedYear())));
      HashSet<Deliverable> deliverablesHL = new HashSet<>();
      deliverablesHL.addAll(deliverables);
      deliverables.clear();
      deliverables.addAll(deliverablesHL);
      for (Deliverable deliverable : deliverables) {
        String delivType = null;
        String delivSubType = null;
        String delivStatus = deliverable.getDeliverableInfo(this.getSelectedPhase()).getStatusName();
        String delivYear = null;
        String keyOutput = "";
        String leader = null;
        String institution = null;
        String fundingSources = "";
        Boolean showFAIR = false;
        Boolean showPublication = false;
        Boolean showCompilance = false;
        if (deliverable.getDeliverableInfo(this.getSelectedPhase()).getDeliverableType() != null) {
          delivSubType = deliverable.getDeliverableInfo(this.getSelectedPhase()).getDeliverableType().getName();
          if (deliverable.getDeliverableInfo(this.getSelectedPhase()).getDeliverableType().getId() == 51
            || deliverable.getDeliverableInfo(this.getSelectedPhase()).getDeliverableType().getId() == 56
            || deliverable.getDeliverableInfo(this.getSelectedPhase()).getDeliverableType().getId() == 57
            || deliverable.getDeliverableInfo(this.getSelectedPhase()).getDeliverableType().getId() == 76
            || deliverable.getDeliverableInfo(this.getSelectedPhase()).getDeliverableType().getId() == 54
            || deliverable.getDeliverableInfo(this.getSelectedPhase()).getDeliverableType().getId() == 81
            || deliverable.getDeliverableInfo(this.getSelectedPhase()).getDeliverableType().getId() == 82
            || deliverable.getDeliverableInfo(this.getSelectedPhase()).getDeliverableType().getId() == 83
            || deliverable.getDeliverableInfo(this.getSelectedPhase()).getDeliverableType().getId() == 55
            || deliverable.getDeliverableInfo(this.getSelectedPhase()).getDeliverableType().getId() == 62
            || deliverable.getDeliverableInfo(this.getSelectedPhase()).getDeliverableType().getId() == 53
            || deliverable.getDeliverableInfo(this.getSelectedPhase()).getDeliverableType().getId() == 60
            || deliverable.getDeliverableInfo(this.getSelectedPhase()).getDeliverableType().getId() == 59
            || deliverable.getDeliverableInfo(this.getSelectedPhase()).getDeliverableType().getId() == 58
            || deliverable.getDeliverableInfo(this.getSelectedPhase()).getDeliverableType().getId() == 77
            || deliverable.getDeliverableInfo(this.getSelectedPhase()).getDeliverableType().getId() == 75
            || deliverable.getDeliverableInfo(this.getSelectedPhase()).getDeliverableType().getId() == 78
            || deliverable.getDeliverableInfo(this.getSelectedPhase()).getDeliverableType().getId() == 72
            || deliverable.getDeliverableInfo(this.getSelectedPhase()).getDeliverableType().getId() == 73) {
            showFAIR = true;
          }
          if (deliverable.getDeliverableInfo(this.getSelectedPhase()).getDeliverableType().getId() == 51
            || deliverable.getDeliverableInfo(this.getSelectedPhase()).getDeliverableType().getId() == 74) {
            showCompilance = true;
          }
          if (deliverable.getDeliverableInfo(this.getSelectedPhase()).getDeliverableType()
            .getDeliverableType() != null) {
            delivType = deliverable.getDeliverableInfo(this.getSelectedPhase()).getDeliverableType()
              .getDeliverableType().getName();
            // FAIR and deliverable publication
            if (deliverable.getDeliverableInfo(this.getSelectedPhase()).getDeliverableType().getDeliverableType()
              .getId() == 49) {
              showFAIR = true;
              showPublication = true;
            }
          }
        }
        if (delivStatus.equals("")) {
          delivStatus = null;
        }
        if (deliverable.getDeliverableInfo(this.getSelectedPhase()).getYear() != 0) {
          delivYear = "" + deliverable.getDeliverableInfo(this.getSelectedPhase()).getYear();
        }
        if (deliverable.getDeliverableInfo(this.getSelectedPhase()).getCrpClusterKeyOutput() != null) {
          keyOutput += "● ";
          if (deliverable.getDeliverableInfo(this.getSelectedPhase()).getCrpClusterKeyOutput().getCrpClusterOfActivity()
            .getCrpProgram() != null) {
            keyOutput += deliverable.getDeliverableInfo(this.getSelectedPhase()).getCrpClusterKeyOutput()
              .getCrpClusterOfActivity().getCrpProgram().getAcronym() + " - ";
          }
          keyOutput += deliverable.getDeliverableInfo(this.getSelectedPhase()).getCrpClusterKeyOutput().getKeyOutput();
        }
        // Get partner responsible and institution
        // Set responible;
        DeliverablePartnership responisble = this.responsiblePartner(deliverable);
        if (responisble != null) {
          if (responisble.getProjectPartnerPerson() != null) {
            ProjectPartnerPerson responsibleppp = responisble.getProjectPartnerPerson();
            leader =
              responsibleppp.getUser().getComposedName() + "<br>&lt;" + responsibleppp.getUser().getEmail() + "&gt;";
            if (responsibleppp.getProjectPartner() != null) {
              if (responsibleppp.getProjectPartner().getInstitution() != null) {
                institution = responsibleppp.getProjectPartner().getInstitution().getComposedName();
              }
            }
          }
        }
        // Get funding sources if exist
        for (DeliverableFundingSource dfs : deliverable.getDeliverableFundingSources().stream()
          .filter(d -> d.isActive()).collect(Collectors.toList())) {
          fundingSources +=
            "● " + dfs.getFundingSource().getFundingSourceInfo(this.getSelectedPhase()).getTitle() + "<br>";
        }
        if (fundingSources.isEmpty()) {
          fundingSources = null;
        }
        // Get cross_cutting dimension
        String crossCutting = "";
        if (deliverable.getDeliverableInfo(this.getSelectedPhase()).getCrossCuttingNa() != null) {
          if (deliverable.getDeliverableInfo(this.getSelectedPhase()).getCrossCuttingNa() == true) {
            crossCutting += "&nbsp;&nbsp;&nbsp;&nbsp;● N/A <br>";
          }
        }
        if (deliverable.getDeliverableInfo(this.getSelectedPhase()).getCrossCuttingGender() != null) {
          if (deliverable.getDeliverableInfo(this.getSelectedPhase()).getCrossCuttingGender() == true) {
            crossCutting += "&nbsp;&nbsp;&nbsp;&nbsp;● Gender <br>";
          }
        }
        if (deliverable.getDeliverableInfo(this.getSelectedPhase()).getCrossCuttingYouth() != null) {
          if (deliverable.getDeliverableInfo(this.getSelectedPhase()).getCrossCuttingYouth() == true) {
            crossCutting += "&nbsp;&nbsp;&nbsp;&nbsp;● Youth <br>";
          }
        }
        if (deliverable.getDeliverableInfo(this.getSelectedPhase()).getCrossCuttingCapacity() != null) {
          if (deliverable.getDeliverableInfo(this.getSelectedPhase()).getCrossCuttingCapacity() == true) {
            crossCutting += "&nbsp;&nbsp;&nbsp;&nbsp;● Capacity Development <br>";
          }
        }
        if (deliverable.getDeliverableInfo(this.getSelectedPhase()).getCrossCuttingGender() != null) {
          if (deliverable.getDeliverableInfo(this.getSelectedPhase()).getCrossCuttingGender() == true) {
            if (deliverable.getDeliverableGenderLevels() == null
              || deliverable.getDeliverableGenderLevels().isEmpty()) {
              crossCutting += "<br><b>Gender level(s):</b><br>&nbsp;&nbsp;&nbsp;&nbsp;&lt;Not Defined&gt;";
            } else {
              crossCutting += "<br><b>Gender level(s): </b><br>";
              for (DeliverableGenderLevel dgl : deliverable.getDeliverableGenderLevels().stream()
                .filter(dgl -> dgl.isActive()).collect(Collectors.toList())) {
                if (dgl.getGenderLevel() != 0.0) {
                  crossCutting += "&nbsp;&nbsp;&nbsp;&nbsp;● "
                    + genderTypeManager.getGenderTypeById(dgl.getGenderLevel()).getDescription() + "<br>";
                }
              }
            }
          }
        }
        if (crossCutting.isEmpty()) {
          crossCutting = null;
        }
        if (keyOutput.isEmpty()) {
          keyOutput = null;
        }
        // Reporting
        Integer delivNewYear = null;
        String delivNewYearJustification = null;
        if (deliverable.getDeliverableInfo(this.getSelectedPhase()).getStatus() != null) {
          // Extended
          if (deliverable.getDeliverableInfo(this.getSelectedPhase()).getStatus().intValue() == Integer
            .parseInt(ProjectStatusEnum.Extended.getStatusId())) {
            delivNewYear = deliverable.getDeliverableInfo(this.getSelectedPhase()).getNewExpectedYear();
            delivNewYearJustification = deliverable.getDeliverableInfo(this.getSelectedPhase()).getStatusDescription();
          }
          // Complete
          if (deliverable.getDeliverableInfo(this.getSelectedPhase()).getStatus().intValue() == Integer
            .parseInt(ProjectStatusEnum.Complete.getStatusId())) {
            delivNewYear = deliverable.getDeliverableInfo(this.getSelectedPhase()).getNewExpectedYear();
          }
          // Canceled
          if (deliverable.getDeliverableInfo(this.getSelectedPhase()).getStatus().intValue() == Integer
            .parseInt(ProjectStatusEnum.Cancelled.getStatusId())) {
            delivNewYearJustification = deliverable.getDeliverableInfo(this.getSelectedPhase()).getStatusDescription();
          }
        }
        String delivDisseminationChannel = null;
        String delivDisseminationUrl = null;
        String delivOpenAccess = null;
        String delivLicense = null;
        String delivLicenseModifications = null;
        Boolean isDisseminated = false;
        String disseminated = "No";
        String restrictedAccess = null;
        String restrictedDate = null;
        Boolean isRestricted = false;
        Boolean isLastTwoRestricted = false;
        Boolean showDelivLicenseModifications = false;
        if (deliverable.getDeliverableDisseminations().stream().collect(Collectors.toList()).size() > 0
          && deliverable.getDeliverableDisseminations().stream().collect(Collectors.toList()).get(0) != null) {
          // Get deliverable dissemination
          DeliverableDissemination deliverableDissemination =
            deliverable.getDeliverableDisseminations().stream().collect(Collectors.toList()).get(0);
          if (deliverableDissemination.getAlreadyDisseminated() != null
            && deliverableDissemination.getAlreadyDisseminated() == true) {
            isDisseminated = true;
            disseminated = "Yes";
          }
          if (deliverableDissemination.getDisseminationChannel() != null
            && !deliverableDissemination.getDisseminationChannel().isEmpty()) {
            if (ChannelEnum.getValue(deliverableDissemination.getDisseminationChannel()) != null) {
              delivDisseminationChannel =
                ChannelEnum.getValue(deliverableDissemination.getDisseminationChannel()).getDesc();
            }
            // deliv_dissemination_channel = deliverableDissemination.getDisseminationChannel();
          }
          if (deliverableDissemination.getDisseminationUrl() != null
            && !deliverableDissemination.getDisseminationUrl().isEmpty()) {
            delivDisseminationUrl = deliverableDissemination.getDisseminationUrl().replace(" ", "%20");
          }
          if (deliverableDissemination.getIsOpenAccess() != null) {
            if (deliverableDissemination.getIsOpenAccess() == true) {
              delivOpenAccess = "Yes";
            } else {
              // get the open access
              delivOpenAccess = "No";
              isRestricted = true;
              if (deliverableDissemination.getIntellectualProperty() != null
                && deliverableDissemination.getIntellectualProperty() == true) {
                restrictedAccess = "Intellectual Property Rights (confidential information)";
              }
              if (deliverableDissemination.getLimitedExclusivity() != null
                && deliverableDissemination.getLimitedExclusivity() == true) {
                restrictedAccess = "Limited Exclusivity Agreements";
              }
              if (deliverableDissemination.getNotDisseminated() != null
                && deliverableDissemination.getNotDisseminated() == true) {
                restrictedAccess = "Not Disseminated";
              }
              if (deliverableDissemination.getRestrictedUseAgreement() != null
                && deliverableDissemination.getRestrictedUseAgreement() == true) {
                restrictedAccess = "Restricted Use Agreement - Restricted access (if so, what are these periods?)";
                isLastTwoRestricted = true;
                if (deliverableDissemination.getRestrictedAccessUntil() != null) {
                  restrictedDate =
                    "<b>Restricted access until: </b>" + deliverableDissemination.getRestrictedAccessUntil();
                } else {
                  restrictedDate = "<b>Restricted access until: </b>&lt;Not Defined&gt;";
                }
              }
              if (deliverableDissemination.getEffectiveDateRestriction() != null
                && deliverableDissemination.getEffectiveDateRestriction() == true) {
                restrictedAccess = "Effective Date Restriction - embargoed periods (if so, what are these periods?)";
                isLastTwoRestricted = true;
                if (deliverableDissemination.getRestrictedEmbargoed() != null) {
                  restrictedDate =
                    "<b>Restricted embargoed date: </b>" + deliverableDissemination.getRestrictedEmbargoed();
                } else {
                  restrictedDate = "<b>Restricted embargoed date: </b>&lt;Not Defined&gt;";
                }
              }
            }
          }
          if (deliverable.getDeliverableInfo(this.getSelectedPhase()).getAdoptedLicense() != null) {
            if (deliverable.getDeliverableInfo(this.getSelectedPhase()).getAdoptedLicense() == true) {
              delivLicense = deliverable.getDeliverableInfo(this.getSelectedPhase()).getLicense();
              if (delivLicense.equals("OTHER")) {
                delivLicense = deliverable.getDeliverableInfo(this.getSelectedPhase()).getOtherLicense();
                showDelivLicenseModifications = true;
                if (deliverable.getDeliverableInfo(this.getSelectedPhase()).getAllowModifications() != null
                  && deliverable.getDeliverableInfo(this.getSelectedPhase()).getAllowModifications() == true) {
                  delivLicenseModifications = "Yes";
                } else {
                  delivLicenseModifications = "No";
                }
              }
            } else {
              delivLicense = "No";
            }
          }
        }
        if (delivLicense != null && delivLicense.isEmpty()) {
          delivLicense = null;
        }
        String titleMetadata = null;
        String descriptionMetadata = null;
        String dateMetadata = null;
        String languageMetadata = null;
        String countryMetadata = null;
        String keywordsMetadata = null;
        String citationMetadata = null;
        String HandleMetadata = null;
        String DOIMetadata = null;
        for (DeliverableMetadataElement deliverableMetadataElement : deliverable.getDeliverableMetadataElements()
          .stream().filter(dm -> dm.isActive() && dm.getMetadataElement() != null).collect(Collectors.toList())) {
          if (deliverableMetadataElement.getMetadataElement().getId() == 1) {
            if (deliverableMetadataElement.getElementValue() != null
              && !deliverableMetadataElement.getElementValue().isEmpty()) {
              titleMetadata = deliverableMetadataElement.getElementValue();
            }
          }
          if (deliverableMetadataElement.getMetadataElement().getId() == 8) {
            if (deliverableMetadataElement.getElementValue() != null
              && !deliverableMetadataElement.getElementValue().isEmpty()) {
              descriptionMetadata = deliverableMetadataElement.getElementValue();
            }
          }
          if (deliverableMetadataElement.getMetadataElement().getId() == 17) {
            if (deliverableMetadataElement.getElementValue() != null
              && !deliverableMetadataElement.getElementValue().isEmpty()) {
              dateMetadata = deliverableMetadataElement.getElementValue();
            }
          }
          if (deliverableMetadataElement.getMetadataElement().getId() == 24) {
            if (deliverableMetadataElement.getElementValue() != null
              && !deliverableMetadataElement.getElementValue().isEmpty()) {
              languageMetadata = deliverableMetadataElement.getElementValue();
            }
          }
          if (deliverableMetadataElement.getMetadataElement().getId() == 28) {
            if (deliverableMetadataElement.getElementValue() != null
              && !deliverableMetadataElement.getElementValue().isEmpty()) {
              countryMetadata = deliverableMetadataElement.getElementValue();
            }
          }
          if (deliverableMetadataElement.getMetadataElement().getId() == 37) {
            if (deliverableMetadataElement.getElementValue() != null
              && !deliverableMetadataElement.getElementValue().isEmpty()) {
              keywordsMetadata = deliverableMetadataElement.getElementValue();
            }
          }
          if (deliverableMetadataElement.getMetadataElement().getId() == 22) {
            if (deliverableMetadataElement.getElementValue() != null
              && !deliverableMetadataElement.getElementValue().isEmpty()) {
              citationMetadata = deliverableMetadataElement.getElementValue();
            }
          }
          if (deliverableMetadataElement.getMetadataElement().getId() == 35) {
            if (deliverableMetadataElement.getElementValue() != null
              && !deliverableMetadataElement.getElementValue().isEmpty()) {
              HandleMetadata = deliverableMetadataElement.getElementValue();
            }
          }
          if (deliverableMetadataElement.getMetadataElement().getId() == 36) {
            if (deliverableMetadataElement.getElementValue() != null
              && !deliverableMetadataElement.getElementValue().isEmpty()) {
              DOIMetadata = deliverableMetadataElement.getElementValue();
            }
          }
        }
        String creatorAuthors = "";
        for (DeliverableUser deliverableUser : deliverable.getDeliverableUsers().stream().filter(du -> du.isActive())
          .collect(Collectors.toList())) {
          creatorAuthors += "<br>● ";
          if (!deliverableUser.getLastName().isEmpty()) {
            creatorAuthors += deliverableUser.getLastName() + " - ";
          }
          if (!deliverableUser.getFirstName().isEmpty()) {
            creatorAuthors += deliverableUser.getFirstName();
          }
          if (!deliverableUser.getElementId().isEmpty()) {
            creatorAuthors += "&lt;" + deliverableUser.getElementId() + "&gt;";
          }
        }
        if (creatorAuthors.isEmpty()) {
          creatorAuthors = null;
        }
        String dataSharing = "";
        for (DeliverableDataSharingFile deliverableDataSharingFile : deliverable.getDeliverableDataSharingFiles()
          .stream().filter(ds -> ds.isActive()).collect(Collectors.toList())) {
          if (deliverableDataSharingFile.getExternalFile() != null
            && !deliverableDataSharingFile.getExternalFile().isEmpty()) {
            dataSharing += deliverableDataSharingFile.getExternalFile().replace(" ", "%20") + "<br>";
          }
          if (deliverableDataSharingFile.getFile() != null && deliverableDataSharingFile.getFile().isActive()) {
            dataSharing +=
              (this.getDeliverableDataSharingFilePath() + deliverableDataSharingFile.getFile().getFileName())
                .replace(" ", "%20") + "<br>";
          }
        }
        if (dataSharing.isEmpty()) {
          dataSharing = null;
        }
        String qualityAssurance = "";
        String dataDictionary = "";
        String tools = "";
        if (deliverable.getDeliverableQualityChecks().stream().filter(qc -> qc.isActive()).collect(Collectors.toList())
          .size() > 0
          && deliverable.getDeliverableQualityChecks().stream().filter(qc -> qc.isActive()).collect(Collectors.toList())
            .get(0) != null) {
          DeliverableQualityCheck deliverableQualityCheck = deliverable.getDeliverableQualityChecks().stream()
            .filter(qc -> qc.isActive()).collect(Collectors.toList()).get(0);
          // QualityAssurance
          if (deliverableQualityCheck.getQualityAssurance() != null) {
            if (deliverableQualityCheck.getQualityAssurance().getId() == 2) {
              if (deliverableQualityCheck.getFileAssurance() != null
                && deliverableQualityCheck.getFileAssurance().isActive()) {
                qualityAssurance += "<br>● File: <font size=2 face='Segoe UI' color='blue'>"
                  + (this.getDeliverableUrl("Assurance", deliverable)
                    + deliverableQualityCheck.getFileAssurance().getFileName()).replace(" ", "%20")
                  + "</font>";
              }
              if (deliverableQualityCheck.getLinkAssurance() != null
                && !deliverableQualityCheck.getLinkAssurance().isEmpty()) {
                qualityAssurance += "<br>● Link: <font size=2 face='Segoe UI' color='blue'>"
                  + deliverableQualityCheck.getLinkAssurance().replace(" ", "%20") + "</font>";
              }
            } else {
              qualityAssurance = "● " + deliverableQualityCheck.getQualityAssurance().getName();
            }
          }
          // Data dictionary
          if (deliverableQualityCheck.getDataDictionary() != null) {
            if (deliverableQualityCheck.getDataDictionary().getId() == 2) {
              if (deliverableQualityCheck.getFileDictionary() != null
                && deliverableQualityCheck.getFileDictionary().isActive()) {
                dataDictionary += "<br>● File: <font size=2 face='Segoe UI' color='blue'>"
                  + (this.getDeliverableUrl("Dictionary", deliverable)
                    + deliverableQualityCheck.getFileDictionary().getFileName()).replace(" ", "%20")
                  + "</font>";
              }
              if (deliverableQualityCheck.getLinkDictionary() != null
                && !deliverableQualityCheck.getLinkDictionary().isEmpty()) {
                dataDictionary += "<br>● Link: <font size=2 face='Segoe UI' color='blue'>"
                  + deliverableQualityCheck.getLinkDictionary().replace(" ", "%20") + "</font>";
              }
            } else {
              dataDictionary = "● " + deliverableQualityCheck.getDataDictionary().getName();
            }
          }
          // Tools
          if (deliverableQualityCheck.getDataTools() != null) {
            if (deliverableQualityCheck.getDataTools().getId() == 2) {
              if (deliverableQualityCheck.getFileTools() != null && deliverableQualityCheck.getFileTools().isActive()) {
                tools += "<br>● File: <font size=2 face='Segoe UI' color='blue'>"
                  + (this.getDeliverableUrl("Tools", deliverable)
                    + deliverableQualityCheck.getFileTools().getFileName()).replace(" ", "%20")
                  + "</font>";
              }
              if (deliverableQualityCheck.getLinkTools() != null && !deliverableQualityCheck.getLinkTools().isEmpty()) {
                tools += "<br>● Link: <font size=2 face='Segoe UI' color='blue'>"
                  + deliverableQualityCheck.getLinkTools().replace(" ", "%20") + "</font>";
              }
            } else {
              tools = "● " + deliverableQualityCheck.getDataTools().getName();
            }
          }
        }
        if (qualityAssurance.isEmpty()) {
          qualityAssurance = null;
        }
        if (dataDictionary.isEmpty()) {
          dataDictionary = null;
        }
        if (tools.isEmpty()) {
          tools = null;
        }
        // FAIR
        String F = "";
        if (this.isF(deliverable.getId()) == null) {
          F = "#a3a3a3";
        } else {
          if (this.isF(deliverable.getId()) == true) {
            F = "#008000";
          } else {
            F = "#ca1010";
          }
        }
        String A = "";
        if (this.isA(deliverable.getId()) == null) {
          A += "#a3a3a3";
        } else {
          if (this.isA(deliverable.getId()) == true) {
            A += "#008000";
          } else {
            A += "#ca1010";
          }
        }
        String I = "";
        if (this.isI(deliverable.getId()) == null) {
          I += "#a3a3a3";
        } else {
          if (this.isI(deliverable.getId()) == true) {
            I += "#008000";
          } else {
            I += "#ca1010";
          }
        }
        String R = "";
        if (this.isR(deliverable.getId()) == null) {
          R += "#a3a3a3";
        } else {
          if (this.isR(deliverable.getId()) == true) {
            R += "#008000";
          } else {
            R += "#ca1010";
          }
        }
        String volume = null;
        String issue = null;
        String pages = null;
        String journal = null;
        String journalIndicators = "";
        String acknowledge = null;
        String flContrib = "";
        // Publication metadata
        // Verify if the deliverable is of type Articles and Books
        if (deliverable.getDeliverablePublicationMetadatas().stream().filter(dpm -> dpm.isActive())
          .collect(Collectors.toList()).size() > 0
          && deliverable.getDeliverablePublicationMetadatas().stream().filter(dpm -> dpm.isActive())
            .collect(Collectors.toList()).get(0) != null) {
          DeliverablePublicationMetadata deliverablePublicationMetadata =
            deliverable.getDeliverablePublicationMetadatas().stream().filter(dpm -> dpm.isActive())
              .collect(Collectors.toList()).get(0);
          volume = deliverablePublicationMetadata.getVolume();
          issue = deliverablePublicationMetadata.getIssue();
          pages = deliverablePublicationMetadata.getPages();
          journal = deliverablePublicationMetadata.getJournal();
          if (deliverablePublicationMetadata.getIsiPublication() != null
            && deliverablePublicationMetadata.getIsiPublication() == true) {
            journalIndicators += "● This journal article is an ISI publication <br>";
          }
          if (deliverablePublicationMetadata.getNasr() != null && deliverablePublicationMetadata.getNasr() == true) {
            journalIndicators +=
              "● This article have a co-author from a developing country National Agricultural Research System (NARS)<br>";
          }
          if (deliverablePublicationMetadata.getCoAuthor() != null
            && deliverablePublicationMetadata.getCoAuthor() == true) {
            journalIndicators +=
              "● This article have a co-author based in an Earth System Science-related academic department";
          }
          if (journalIndicators.isEmpty()) {
            journalIndicators = null;
          }
          if (deliverablePublicationMetadata.getPublicationAcknowledge() != null
            && deliverablePublicationMetadata.getPublicationAcknowledge() == true) {
            acknowledge = "Yes";
          } else {
            acknowledge = "No";
          }
          for (DeliverableCrp deliverableCrp : deliverable.getDeliverableCrps().stream().filter(dc -> dc.isActive())
            .collect(Collectors.toList())) {
            if (deliverableCrp.getCrpPandr() != null && deliverableCrp.getIpProgram() != null) {
              flContrib += "● " + deliverableCrp.getCrpPandr().getAcronym().toUpperCase() + " - "
                + deliverableCrp.getIpProgram().getAcronym().toUpperCase() + "<br>";
            } else {
              if (deliverableCrp.getCrpPandr() != null) {
                flContrib += "● " + deliverableCrp.getCrpPandr().getName().toUpperCase() + "<br>";
              }
            }
          }
        }
        model.addRow(new Object[] {deliverable.getId(),
          deliverable.getDeliverableInfo(this.getSelectedPhase()).getTitle(), delivType, delivSubType, delivStatus,
          delivYear, keyOutput, leader, institution, fundingSources, crossCutting, delivNewYear,
          delivNewYearJustification, delivDisseminationChannel, delivDisseminationUrl, delivOpenAccess, delivLicense,
          titleMetadata, descriptionMetadata, dateMetadata, languageMetadata, countryMetadata, keywordsMetadata,
          citationMetadata, HandleMetadata, DOIMetadata, creatorAuthors, dataSharing, qualityAssurance, dataDictionary,
          tools, showFAIR, F, A, I, R, isDisseminated, disseminated, restrictedAccess, isRestricted, restrictedDate,
          isLastTwoRestricted, delivLicenseModifications, showDelivLicenseModifications, volume, issue, pages, journal,
          journalIndicators, acknowledge, flContrib, showPublication, showCompilance});
      }
    }
    return model;
  }

  private TypedTableModel getDeliverablesTableModel() {
    TypedTableModel model = new TypedTableModel(
      new String[] {"deliverable_id", "title", "deliv_type", "deliv_sub_type", "deliv_status", "deliv_year",
        "key_output", "leader", "institution", "funding_sources", "cross_cutting"},
      new Class[] {Long.class, String.class, String.class, String.class, String.class, String.class, String.class,
        String.class, String.class, String.class, String.class},
      0);
    if (!project.getDeliverables().isEmpty()) {
      for (Deliverable deliverable : project.getDeliverables().stream()
        .sorted((d1, d2) -> Long.compare(d1.getId(), d2.getId()))
        .filter(c -> c.isActive() && c.getDeliverableInfo(this.getSelectedPhase()).getYear() >= this.getSelectedYear())
        .collect(Collectors.toList())) {
        String delivType = null;
        String delivSubType = null;
        String delivStatus = deliverable.getDeliverableInfo(this.getSelectedPhase()).getStatusName();
        String delivYear = null;
        String keyOutput = "";
        String leader = null;
        String institution = null;
        String fundingSources = "";
        if (deliverable.getDeliverableInfo(this.getSelectedPhase()).getDeliverableType() != null) {
          delivSubType = deliverable.getDeliverableInfo(this.getSelectedPhase()).getDeliverableType().getName();
          if (deliverable.getDeliverableInfo(this.getSelectedPhase()).getDeliverableType()
            .getDeliverableType() != null) {
            delivType = deliverable.getDeliverableInfo(this.getSelectedPhase()).getDeliverableType()
              .getDeliverableType().getName();
          }
        }
        if (delivStatus.equals("")) {
          delivStatus = null;
        }
        if (deliverable.getDeliverableInfo(this.getSelectedPhase()).getYear() != 0) {
          delivYear = "" + deliverable.getDeliverableInfo(this.getSelectedPhase()).getYear();
        }
        if (deliverable.getDeliverableInfo(this.getSelectedPhase()).getCrpClusterKeyOutput() != null) {
          keyOutput += "● ";
          if (deliverable.getDeliverableInfo(this.getSelectedPhase()).getCrpClusterKeyOutput().getCrpClusterOfActivity()
            .getCrpProgram() != null) {
            keyOutput += deliverable.getDeliverableInfo(this.getSelectedPhase()).getCrpClusterKeyOutput()
              .getCrpClusterOfActivity().getCrpProgram().getAcronym() + " - ";
          }
          keyOutput += deliverable.getDeliverableInfo(this.getSelectedPhase()).getCrpClusterKeyOutput().getKeyOutput();
        }
        // Get partner responsible and institution
        // Set responible;
        DeliverablePartnership responisble = this.responsiblePartner(deliverable);
        if (responisble != null) {
          if (responisble.getProjectPartnerPerson() != null) {
            ProjectPartnerPerson responsibleppp = responisble.getProjectPartnerPerson();
            leader =
              responsibleppp.getUser().getComposedName() + "<br>&lt;" + responsibleppp.getUser().getEmail() + "&gt;";
            if (responsibleppp.getProjectPartner() != null) {
              if (responsibleppp.getProjectPartner().getInstitution() != null) {
                institution = responsibleppp.getProjectPartner().getInstitution().getComposedName();
              }
            }
          }
        }
        // Get funding sources if exist
        for (DeliverableFundingSource dfs : deliverable.getDeliverableFundingSources().stream()
          .filter(d -> d.isActive() && d.getPhase() != null && d.getPhase().equals(this.getSelectedPhase()))
          .collect(Collectors.toList())) {
          fundingSources += "● " + "(" + dfs.getFundingSource().getId() + ") - "
            + dfs.getFundingSource().getFundingSourceInfo(this.getSelectedPhase()).getTitle() + "<br>";
        }
        if (fundingSources.isEmpty()) {
          fundingSources = null;
        }
        // Get cross_cutting dimension
        String crossCutting = "";
        if (deliverable.getDeliverableInfo(this.getSelectedPhase()).getCrossCuttingNa() != null) {
          if (deliverable.getDeliverableInfo(this.getSelectedPhase()).getCrossCuttingNa() == true) {
            crossCutting += "&nbsp;&nbsp;&nbsp;&nbsp;● N/A <br>";
          }
        }
        if (deliverable.getDeliverableInfo(this.getSelectedPhase()).getCrossCuttingGender() != null) {
          if (deliverable.getDeliverableInfo(this.getSelectedPhase()).getCrossCuttingGender() == true) {
            crossCutting += "&nbsp;&nbsp;&nbsp;&nbsp;● Gender <br>";
          }
        }
        if (deliverable.getDeliverableInfo(this.getSelectedPhase()).getCrossCuttingYouth() != null) {
          if (deliverable.getDeliverableInfo(this.getSelectedPhase()).getCrossCuttingYouth() == true) {
            crossCutting += "&nbsp;&nbsp;&nbsp;&nbsp;● Youth <br>";
          }
        }
        if (deliverable.getDeliverableInfo(this.getSelectedPhase()).getCrossCuttingCapacity() != null) {
          if (deliverable.getDeliverableInfo(this.getSelectedPhase()).getCrossCuttingCapacity() == true) {
            crossCutting += "&nbsp;&nbsp;&nbsp;&nbsp;● Capacity Development <br>";
          }
        }
        if (deliverable.getDeliverableInfo(this.getSelectedPhase()).getCrossCuttingGender() != null) {
          if (deliverable.getDeliverableInfo(this.getSelectedPhase()).getCrossCuttingGender() == true) {
            if (deliverable.getDeliverableGenderLevels() == null
              || deliverable.getDeliverableGenderLevels().isEmpty()) {
              crossCutting += "<br><b>Gender level(s):</b><br>&nbsp;&nbsp;&nbsp;&nbsp;&lt;Not Defined&gt;";
            } else {
              crossCutting += "<br><b>Gender level(s): </b><br>";
              for (DeliverableGenderLevel dgl : deliverable.getDeliverableGenderLevels().stream()
                .filter(dgl -> dgl.isActive()).collect(Collectors.toList())) {
                if (dgl.getGenderLevel() != 0.0) {
                  crossCutting += "&nbsp;&nbsp;&nbsp;&nbsp;● "
                    + genderTypeManager.getGenderTypeById(dgl.getGenderLevel()).getDescription() + "<br>";
                }
              }
            }
          }
        }
        if (crossCutting.isEmpty()) {
          crossCutting = null;
        }
        if (keyOutput.isEmpty()) {
          keyOutput = null;
        }
        model.addRow(new Object[] {deliverable.getId(),
          deliverable.getDeliverableInfo(this.getSelectedPhase()).getTitle(), delivType, delivSubType, delivStatus,
          delivYear, keyOutput, leader, institution, fundingSources, crossCutting});
      }
    }
    return model;
  }

  public String getDeliverableUrl(String fileType, Deliverable deliverable) {
    return config.getDownloadURL() + "/" + this.getDeliverableUrlPath(fileType, deliverable).replace('\\', '/');
  }

  public String getDeliverableUrlPath(String fileType, Deliverable deliverable) {
    return config.getProjectsBaseFolder(this.getCrpSession()) + File.separator + deliverable.getId() + File.separator
      + "deliverable" + File.separator + fileType + File.separator;
  }

  private TypedTableModel getDescCoAsTableModel() {
    TypedTableModel model = new TypedTableModel(new String[] {"description"}, new Class[] {String.class}, 0);
    if (project.getProjectClusterActivities() != null) {
      for (ProjectClusterActivity projectClusterActivity : project.getProjectClusterActivities().stream()
        .filter(c -> c.isActive() && c.getPhase() != null && c.getPhase().equals(this.getSelectedPhase()))
        .collect(Collectors.toList())) {
        model.addRow(new Object[] {projectClusterActivity.getCrpClusterOfActivity().getComposedName()});
      }
    }
    return model;
  }

  private TypedTableModel getDescTableModel(ProjectPartner projectLeader, Boolean hasRegions) {
    TypedTableModel model = new TypedTableModel(
      new String[] {"title", "start_date", "end_date", "ml", "ml_contact", "type", "status", "org_leader", "leader",
        "summary", "cycle", "analysis", "cross-cutting", "hasRegions", "ml_text", "ml_contact_text"},
      new Class[] {String.class, String.class, String.class, String.class, String.class, String.class, String.class,
        String.class, String.class, String.class, String.class, String.class, String.class, Boolean.class, String.class,
        String.class});
    SimpleDateFormat formatter = new SimpleDateFormat("MMM yyyy");

    String orgLeader = null;
    String ml = null;
    String mlContact = null;
    String title = projectInfo.getTitle();
    String startDate = null;
    String endDate = null;
    if (projectInfo.getStartDate() != null) {
      startDate = formatter.format(projectInfo.getStartDate());
    }
    if (projectInfo.getEndDate() != null) {
      endDate = formatter.format(projectInfo.getEndDate());
    }
    if (projectInfo.getLiaisonInstitution() != null) {
      ml = projectInfo.getLiaisonInstitution().getAcronym();
    }
    if (projectInfo.getLiaisonUser() != null) {
      ml = projectInfo.getLiaisonUser().getLiaisonInstitution().getAcronym();
      mlContact = projectInfo.getLiaisonUser().getComposedName() + "\n&lt;"
        + projectInfo.getLiaisonUser().getUser().getEmail() + "&gt;";
      if (projectInfo.getLiaisonUser() != null) {
        mlContact = projectInfo.getLiaisonUser().getComposedName() + "\n&lt;"
          + projectInfo.getLiaisonUser().getUser().getEmail() + "&gt;";
      }
      // Get type from funding sources
      String type = "";
      List<String> typeList = new ArrayList<String>();
      for (ProjectBudget projectBudget : project.getProjectBudgets().stream()
        .filter(pb -> pb.isActive() && pb.getYear() == this.getSelectedYear() && pb.getFundingSource() != null)
        .collect(Collectors.toList())) {
        typeList.add(
          projectBudget.getFundingSource().getFundingSourceInfo(this.getSelectedPhase()).getBudgetType().getName());
      }
      // Remove duplicates
      Set<String> s = new LinkedHashSet<String>(typeList);
      for (String typeString : s.stream().collect(Collectors.toList())) {
        if (type.isEmpty()) {
          type = typeString;
        } else {
          type += ", " + typeString;
        }
      }
      String status = ProjectStatusEnum.getValue(projectInfo.getStatus().intValue()).getStatus();
      if (projectLeader.getInstitution() != null) {
        orgLeader = projectLeader.getInstitution().getComposedName();
      }
      String leader = null;
      // Check if project leader is assigned
      if (project.getLeaderPerson(this.getSelectedPhase()) != null
        && project.getLeaderPerson(this.getSelectedPhase()).getUser() != null) {
        leader = project.getLeaderPerson(this.getSelectedPhase()).getUser().getComposedName() + "\n&lt;"
          + project.getLeaderPerson(this.getSelectedPhase()).getUser().getEmail() + "&gt;";
      }
      String summary = projectInfo.getSummary();
      if (summary != null) {
        if (summary.equals("")) {
          summary = null;
        }
      }
      String analysis = projectInfo.getGenderAnalysis();
      if (analysis != null) {
        if (analysis.equals("")) {
          analysis = null;
        }
      }
      String crossCutting = "";
      if (projectInfo.getCrossCuttingNa() != null) {
        if (projectInfo.getCrossCuttingNa() == true) {
          crossCutting += "● N/A <br>";
        }
      }
      if (projectInfo.getCrossCuttingGender() != null) {
        if (projectInfo.getCrossCuttingGender() == true) {
          crossCutting += "● Gender <br>";
        }
      }
      if (projectInfo.getCrossCuttingYouth() != null) {
        if (projectInfo.getCrossCuttingYouth() == true) {
          crossCutting += "● Youth <br>";
        }
      }
      if (projectInfo.getCrossCuttingCapacity() != null) {
        if (projectInfo.getCrossCuttingCapacity() == true) {
          crossCutting += "● Capacity Development <br>";
        }
      }
      if (projectInfo.getCrossCuttingGender() != null) {
        if (projectInfo.getCrossCuttingGender() == false) {
          if (projectInfo.getDimension() == null || projectInfo.getDimension().isEmpty()) {
            crossCutting += "<br><br>" + "<b>Reason for not addressing gender dimension: </b> &lt;Not Defined&gt;";
          } else {
            crossCutting +=
              "<br><br>" + "<b>Reason for not addressing gender dimension: </b>" + projectInfo.getDimension();
          }
        }
      }
      if (crossCutting.isEmpty()) {
        crossCutting = null;
      }
      String mlText = null, mlContactText = null;
      mlText = this.getText("project.liaisonInstitution");
      mlContactText = this.getText("project.liaisonUser");
      model.addRow(new Object[] {title, startDate, endDate, ml, mlContact, type, status, orgLeader, leader, summary,
        this.getSelectedCycle(), analysis, crossCutting, hasRegions, mlText, mlContactText});
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
    fileName.append("FullProjectReportSummary-");
    fileName.append(project.getCrp().getName() + "-");
    fileName.append("P" + projectID + "-");
    fileName.append(this.getSelectedYear() + "_");
    fileName.append(new SimpleDateFormat("yyyyMMdd-HHmm").format(new Date()));
    fileName.append(".pdf");
    return fileName.toString();
  }

  public IpIndicator getFinalIndicator(IpIndicator ipIndicator) {
    IpIndicator newIpIndicator = ipIndicator;
    if (newIpIndicator.getIpIndicator() != null) {
      return this.getFinalIndicator(newIpIndicator.getIpIndicator());
    } else {
      return newIpIndicator;
    }
  }

  private TypedTableModel getFLTableModel(List<CrpProgram> flagships) {
    TypedTableModel model = new TypedTableModel(new String[] {"FL"}, new Class[] {String.class}, 0);
    for (CrpProgram crpProgram : flagships) {
      model.addRow(new Object[] {crpProgram.getComposedName()});
    }
    return model;
  }

  public String getHighlightsImagesUrl() {
    return config.getDownloadURL() + "/" + this.getHighlightsImagesUrlPath().replace('\\', '/');
  }

  public String getHighlightsImagesUrlPath() {
    return config.getProjectsBaseFolder(this.getCrpSession()) + File.separator + project.getId() + File.separator
      + "hightlightsImage" + File.separator;
  }

  public String getHighlightsImagesUrlPath(long projectID) {
    return config.getProjectsBaseFolder(this.getCrpSession()) + File.separator + projectID + File.separator
      + "hightlightsImage" + File.separator;
  }

  private String getHightlightImagePath(long projectID) {
    return config.getUploadsBaseFolder() + File.separator + this.getHighlightsImagesUrlPath(projectID) + File.separator;
  }


  @Override
  public InputStream getInputStream() {
    if (inputStream == null) {
      inputStream = new ByteArrayInputStream(bytesPDF);
    }
    return inputStream;
  }

  private TypedTableModel getLeveragesTableModel() {
    // Decimal format
    DecimalFormat myFormatter = new DecimalFormat("###,###.00");
    TypedTableModel model =
      new TypedTableModel(new String[] {"id", "title", "partner_name", "leverage_year", "flagship", "budget"},
        new Class[] {Long.class, String.class, String.class, String.class, String.class, String.class}, 0);
    for (ProjectLeverage projectLeverage : project.getProjectLeverages().stream()
      .filter(pl -> pl.isActive() && pl.getYear() == this.getSelectedYear()).collect(Collectors.toList())) {
      String title = null, partnerName = null, leverageYear = null, flagship = null, budget = null;
      if (projectLeverage.getTitle() != null && !projectLeverage.getTitle().isEmpty()) {
        title = projectLeverage.getTitle();
      }
      if (projectLeverage.getInstitution() != null && !projectLeverage.getInstitution().getComposedName().isEmpty()) {
        partnerName = projectLeverage.getInstitution().getComposedName();
      }
      if (projectLeverage.getYear() != null) {
        leverageYear = projectLeverage.getYear() + "";
      }
      if (projectLeverage.getCrpProgram() != null && !projectLeverage.getCrpProgram().getComposedName().isEmpty()) {
        flagship = projectLeverage.getCrpProgram().getComposedName();
      }
      if (projectLeverage.getBudget() != null) {
        budget = myFormatter.format(projectLeverage.getBudget());
      }
      model.addRow(new Object[] {projectLeverage.getId(), title, partnerName, leverageYear, flagship, budget});
    }
    return model;
  }


  private TypedTableModel getLocationsTableModel() {
    TypedTableModel model = new TypedTableModel(new String[] {"level", "lat", "long", "name"},
      new Class[] {String.class, Double.class, Double.class, String.class}, 0);
    if (!project.getProjectLocations().isEmpty()) {
      // Get all selected and show it
      List<LocElement> locElementsAll = locElementManager.findAll();
      for (ProjectLocationElementType projectLocType : project.getProjectLocationElementTypes().stream()
        .filter(plt -> plt.getIsGlobal() && plt.getLocElementType().isActive()).collect(Collectors.toList())) {
        String locTypeName = projectLocType.getLocElementType().getName();
        for (LocElement locElement : locElementsAll.stream()
          .filter(le -> le.isActive() && le.getLocElementType() != null
            && le.getLocElementType().getId() == projectLocType.getLocElementType().getId())
          .collect(Collectors.toList())) {
          Double locLat = null;
          Double locLong = null;
          String locName = null;
          if (locElement != null) {
            if (locElement.getLocGeoposition() != null) {
              locLat = locElement.getLocGeoposition().getLatitude();
              locLong = locElement.getLocGeoposition().getLongitude();
            }
            locName = locElement.getName();
          }
          model.addRow(new Object[] {locTypeName, locLat, locLong, locName});
        }
      }
      for (ProjectLocation pl : project.getProjectLocations().stream()
        .filter(c -> c.isActive() && c.getPhase() != null && c.getPhase().equals(this.getSelectedPhase()))
        .collect(Collectors.toList())) {
        LocElement le = pl.getLocElement();
        String locTypeName = null;
        Double locLat = null;
        Double locLong = null;
        String locName = null;
        if (le != null) {
          if (le.getLocElementType() != null) {
            locTypeName = le.getLocElementType().getName();
          }
          if (le.getLocGeoposition() != null) {
            locLat = le.getLocGeoposition().getLatitude();
            locLong = le.getLocGeoposition().getLongitude();
          }
          locName = le.getName();
        }
        model.addRow(new Object[] {locTypeName, locLat, locLong, locName});
      }
    }
    return model;
  }

  private TypedTableModel getMasterTableModel(List<CrpProgram> flagships, List<CrpProgram> regions,
    ProjectPartner projectLeader) {
    // Initialization of Model
    TypedTableModel model = new TypedTableModel(
      new String[] {"title", "center", "current_date", "project_submission", "cycle", "isNew", "isAdministrative",
        "type", "isGlobal", "isPhaseOne", "budget_gender", "hasTargetUnit", "hasW1W2Co", "hasActivities", "phaseID"},
      new Class[] {String.class, String.class, String.class, String.class, String.class, Boolean.class, Boolean.class,
        String.class, Boolean.class, Boolean.class, Boolean.class, Boolean.class, Boolean.class, Boolean.class,
        Long.class});
    // Filling title
    String title = "";
    if (projectLeader != null) {
      if (projectLeader.getInstitution() != null && projectLeader.getInstitution().getAcronym() != ""
        && projectLeader.getInstitution().getAcronym() != null) {
        title += projectLeader.getInstitution().getAcronym() + "-";
      }
    }
    if (projectInfo.getAdministrative() == false) {
      if (flagships != null) {
        if (!flagships.isEmpty()) {
          for (CrpProgram crpProgram : flagships) {
            title += crpProgram.getAcronym() + "-";
          }
        }
      }
      if (projectInfo.getNoRegional() != null && projectInfo.getNoRegional()) {
        title += "Global" + "-";
      } else {
        if (regions != null && !regions.isEmpty()) {
          for (CrpProgram crpProgram : regions) {
            title += crpProgram.getAcronym() + "-";
          }
        }
      }
    }
    title += "P" + Long.toString(projectID);
    // Get datetime
    ZonedDateTime timezone = ZonedDateTime.now();
    DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-d 'at' HH:mm ");
    String zone = timezone.getOffset() + "";
    if (zone.equals("Z")) {
      zone = "+0";
    }
    String currentDate = timezone.format(format) + "(GMT" + zone + ")";
    // Filling submission
    List<Submission> submissions = new ArrayList<>();
    for (Submission submission : project.getSubmissions().stream()
      .filter(c -> c.getCycle().equals(this.getSelectedCycle()) && c.getYear() == this.getSelectedYear()
        && c.getUnSubmitUser() == null)
      .collect(Collectors.toList())) {
      submissions.add(submission);
    }
    String submission = "";
    if (!submissions.isEmpty()) {
      if (submissions.size() > 1) {
        LOG.error("More than one submission was found, the report will retrieve the first one");
      }
      Submission fisrtSubmission = submissions.get(0);
      String submissionDate = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm").format(fisrtSubmission.getDateTime());
      submission = "Submitted on " + submissionDate + " (" + fisrtSubmission.getCycle() + " cycle "
        + fisrtSubmission.getYear() + ")";
    } else {
      if (!this.getSelectedCycle().isEmpty() && this.getSelectedYear() != 0) {
        if (this.getSelectedCycle().equals("Reporting")) {
          submission =
            "Submission for " + this.getSelectedCycle() + " cycle " + this.getSelectedYear() + ": &lt;not submited&gt;";
        } else {
          submission =
            "Submission for " + this.getSelectedCycle() + " cycle " + this.getSelectedYear() + ": &lt;pending&gt;";
        }
      } else {
        submission = "Submission for " + "&lt;Not Defined&gt;" + " cycle " + "&lt;Not Defined&gt;" + " year"
          + ": &lt;Not Defined&gt;";
      }
    }
    // TODO: Get image from repository
    String centerURL = "";
    // set CRP imgage URL from repo
    // centerURL = this.getBaseUrl() + "/global/images/crps/" + project.getCrp().getAcronym() + ".png";
    // Add center url to LOG
    // LOG.info("Center URL is: " + centerURL);
    centerURL = project.getCrp().getAcronym();
    Boolean isAdministrative = false;
    String type = "Research Project";
    if (projectInfo.getAdministrative() != null) {
      if (projectInfo.getAdministrative() == true) {
        type = "Management Project";
      }
      isAdministrative = projectInfo.getAdministrative();
    } else {
      isAdministrative = false;
    }
    Boolean isNew = this.isProjectNew(projectID);
    Boolean hasGender = false;
    try {
      hasGender = this.hasSpecificities(APConstants.CRP_BUDGET_GENDER);
    } catch (Exception e) {
      LOG.warn("Failed to get " + APConstants.CRP_BUDGET_GENDER
        + " parameter. Parameter will be set as false. Exception: " + e.getMessage());
      hasGender = false;
    }
    Boolean hasTargetUnit = false;
    if (targetUnitList.size() > 0) {
      hasTargetUnit = true;
    }

    Boolean hasActivities = false;
    try {
      hasActivities = this.hasSpecificities(APConstants.CRP_ACTIVITES_MODULE);
    } catch (Exception e) {
      LOG.warn("Failed to get " + APConstants.CRP_ACTIVITES_MODULE
        + " parameter. Parameter will be set as false. Exception: " + e.getMessage());
      hasActivities = false;
    }

    Long phaseID = this.getSelectedPhase().getId();

    model.addRow(new Object[] {title, centerURL, currentDate, submission, this.getSelectedCycle(), isNew,
      isAdministrative, type, projectInfo.getLocationGlobal(), this.isPhaseOne(), hasGender, hasTargetUnit, hasW1W2Co,
      hasActivities, phaseID});
    return model;
  }

  public List<IpElement> getMidOutcomeOutputs(long midOutcomeID) {
    List<IpProjectContribution> ipProjectContributions =
      project.getIpProjectContributions().stream().filter(c -> c.isActive()).collect(Collectors.toList());
    project.setMogs(new ArrayList<>());
    for (IpProjectContribution ipProjectContribution : ipProjectContributions) {
      project.getMogs().add(ipProjectContribution.getIpElementByMogId());
    }
    List<IpElement> outputs = new ArrayList<>();
    IpElement midOutcome = ipElementManager.getIpElementById(midOutcomeID);
    if (this.isRegionalOutcome(midOutcome)) {
      List<IpElement> mogs = new ArrayList<>();
      List<IpElement> translatedOf =
        ipElementManager.getIPElementsRelated(midOutcome.getId().intValue(), APConstants.ELEMENT_RELATION_TRANSLATION);
      for (IpElement fsOutcome : translatedOf) {
        mogs.addAll(ipElementManager.getIPElementsByParent(fsOutcome, APConstants.ELEMENT_RELATION_CONTRIBUTION));
        for (IpElement mog : mogs) {
          if (!outputs.contains(mog)) {
            outputs.add(mog);
          }
        }
      }
    } else {
      outputs = ipElementManager.getIPElementsByParent(midOutcome, APConstants.ELEMENT_RELATION_CONTRIBUTION);
    }
    List<IpElement> elements = new ArrayList<>();
    elements.addAll(outputs);
    for (IpElement ipElement : elements) {
      if (!this.containsOutput(ipElement.getId(), midOutcomeID)) {
        outputs.remove(ipElement);
      }
    }
    return outputs;
  }

  private TypedTableModel getOtherContributionsCrpsTableModel() {
    TypedTableModel model = new TypedTableModel(new String[] {"crp_name", "collaboration_description"},
      new Class[] {String.class, String.class}, 0);
    for (ProjectCrpContribution projectCrpContribution : project.getProjectCrpContributions().stream()
      .filter(pcc -> pcc.isActive()).collect(Collectors.toList())) {
      String crpName = null, collaborationDescription = null;
      if (projectCrpContribution.getCrp() != null && !projectCrpContribution.getCrp().getName().isEmpty()) {
        crpName = projectCrpContribution.getCrp().getName();
      }
      if (projectCrpContribution.getCollaborationNature() != null) {
        if (!projectCrpContribution.getCollaborationNature().isEmpty()) {
          collaborationDescription = projectCrpContribution.getCollaborationNature();
        }
      }
      model.addRow(new Object[] {crpName, collaborationDescription});
    }
    return model;
  }

  private TypedTableModel getOtherContributionsDetailTableModel() {
    TypedTableModel model = new TypedTableModel(
      new String[] {"region", "indicator", "contribution_description", "target_contribution", "otherContributionyear"},
      new Class[] {String.class, String.class, String.class, Integer.class, Integer.class}, 0);
    for (OtherContribution otherContribution : project.getOtherContributions().stream().filter(oc -> oc.isActive())
      .collect(Collectors.toList())) {
      String region = null, indicator = null, contributionDescription = null;
      Integer targetContribution = null;
      int otherContributionyear = 0;
      if (otherContribution.getIpProgram() != null) {
        if (otherContribution.getIpProgram().getAcronym() != null) {
          if (!otherContribution.getIpProgram().getAcronym().isEmpty()) {
            region = otherContribution.getIpProgram().getAcronym();
          }
        }
      }
      if (otherContribution.getIpIndicator() != null
        && !otherContribution.getIpIndicator().getComposedName().isEmpty()) {
        indicator = otherContribution.getIpIndicator().getComposedName();
      }
      if (otherContribution.getDescription() != null) {
        if (!otherContribution.getDescription().isEmpty()) {
          contributionDescription = otherContribution.getDescription();
        }
      }
      if (otherContribution.getTarget() != null) {
        targetContribution = otherContribution.getTarget();
      }
      otherContributionyear = this.getSelectedYear();
      model
        .addRow(new Object[] {region, indicator, contributionDescription, targetContribution, otherContributionyear});
    }

    return model;
  }

  private TypedTableModel getOtherContributionsTableModel() {
    TypedTableModel model = new TypedTableModel(new String[] {"contribution"}, new Class[] {String.class}, 0);
    String contribution = null;
    for (ProjectOtherContribution projectOtherContribution : project.getProjectOtherContributions().stream()
      .filter(poc -> poc.isActive()).collect(Collectors.toList())) {
      if (projectOtherContribution.getContribution() != null && !projectOtherContribution.getContribution().isEmpty()) {
        contribution = projectOtherContribution.getContribution();
      }
    }
    model.addRow(new Object[] {contribution});
    return model;
  }

  private TypedTableModel getOutcomesTableModel() {
    TypedTableModel model = new TypedTableModel(
      new String[] {"exp_value", "narrative", "outcome_id", "out_fl", "out_year", "out_value", "out_statement",
        "out_unit", "cross_cutting", "exp_unit"},
      new Class[] {Long.class, String.class, Long.class, String.class, String.class, String.class, String.class,
        String.class, String.class, String.class},
      0);
    if (!project.getProjectOutcomes().isEmpty()) {
      for (ProjectOutcome projectOutcome : project.getProjectOutcomes().stream()
        .filter(c -> c.isActive() && c.getPhase() != null && c.getPhase().equals(this.getSelectedPhase()))
        .collect(Collectors.toList())) {
        String expValue = null;
        String expUnit = null;
        String outFl = null;
        String outYear = null;
        String outValue = null;
        String outStatement = null;
        String outUnit = null;
        String crossCutting = "";
        if (projectOutcome.getCrpProgramOutcome() != null) {
          outYear = "" + projectOutcome.getCrpProgramOutcome().getYear();
          outValue = "" + projectOutcome.getCrpProgramOutcome().getValue();
          outStatement = projectOutcome.getCrpProgramOutcome().getDescription();
          if (projectOutcome.getCrpProgramOutcome().getSrfTargetUnit() != null) {
            outUnit = projectOutcome.getCrpProgramOutcome().getSrfTargetUnit().getName();
          }
          if (projectOutcome.getCrpProgramOutcome().getCrpProgram() != null) {
            outFl = projectOutcome.getCrpProgramOutcome().getCrpProgram().getAcronym();
          }
        }
        expValue = projectOutcome.getExpectedValue() + "";
        if (outUnit == null) {
          if (projectOutcome.getExpectedUnit() != null) {
            expUnit = projectOutcome.getExpectedUnit().getName();
          }
        } else {
          expUnit = outUnit;
        }
        if (projectOutcome.getGenderDimenssion() != null && !projectOutcome.getGenderDimenssion().isEmpty()) {
          crossCutting +=
            "<b>Narrative for your expected project contribution to the gender dimensions of this outcome: </b>"
              + projectOutcome.getGenderDimenssion() + "<br><br>";
        }
        if (projectOutcome.getYouthComponent() != null && !projectOutcome.getYouthComponent().isEmpty()) {
          crossCutting +=
            "<b>Narrative for your expected project contribution to the youth component of this outcome: </b>"
              + projectOutcome.getYouthComponent();
        }
        if (crossCutting.isEmpty()) {
          crossCutting = null;
        }
        model.addRow(new Object[] {expValue, projectOutcome.getNarrativeTarget(), projectOutcome.getId(), outFl,
          outYear, outValue, outStatement, outUnit, crossCutting, expUnit});
      }
    }
    return model;
  }

  private TypedTableModel getOverviewByMogsTableModel() {
    TypedTableModel model = new TypedTableModel(
      new String[] {"ipProgram", "ipElement", "bullet_points", "summary", "plan", "gender", "output_year",
        "changeYearTab", "isMidOutcome"},
      new Class[] {String.class, String.class, String.class, String.class, String.class, String.class, Integer.class,
        Boolean.class, Boolean.class},
      0);
    int controlYear = 0;
    Boolean changeYearTab = true;
    Boolean isMidOutcome = false;
    for (IpProjectContributionOverview ipProjectContributionOverview : project.getIpProjectContributionOverviews()
      .stream().sorted((co1, co2) -> co2.getYear() - co1.getYear()).filter(co -> co.isActive())
      .collect(Collectors.toList())) {
      // System.out.println(ipProjectContributionOverview.getYear());
      if (ipProjectContributionOverview.getYear() == APConstants.MID_OUTCOME_YEAR) {
        isMidOutcome = true;
      } else {
        isMidOutcome = false;
      }
      if (controlYear == 0) {
        controlYear = ipProjectContributionOverview.getYear();
      } else {
        if (controlYear == ipProjectContributionOverview.getYear()) {
          changeYearTab = false;
        } else {
          changeYearTab = true;
          controlYear = ipProjectContributionOverview.getYear();
        }
      }
      String ipProgram = null;
      String ipElement = null;
      String bulletPoints = null;
      String summary = null;
      String plan = null;
      String gender = null;
      int outputYear = 0;
      if (ipProjectContributionOverview.getIpElement() != null) {
        if (!ipProjectContributionOverview.getIpElement().getDescription().isEmpty()) {
          ipElement = ipProjectContributionOverview.getIpElement().getDescription();
        }
        if (ipProjectContributionOverview.getIpElement().getIpProgram() != null) {
          if (!ipProjectContributionOverview.getIpElement().getIpProgram().getAcronym().isEmpty()) {
            ipProgram = ipProjectContributionOverview.getIpElement().getIpProgram().getAcronym();
          }
        }
      }
      if (ipProjectContributionOverview.getAnualContribution() != null
        && !ipProjectContributionOverview.getAnualContribution().isEmpty()) {
        bulletPoints = ipProjectContributionOverview.getAnualContribution();
      }
      if (ipProjectContributionOverview.getBriefSummary() != null
        && !ipProjectContributionOverview.getBriefSummary().isEmpty()) {
        summary = ipProjectContributionOverview.getBriefSummary();
      }
      if (ipProjectContributionOverview.getGenderContribution() != null
        && !ipProjectContributionOverview.getGenderContribution().isEmpty()) {
        plan = ipProjectContributionOverview.getGenderContribution();
      }
      if (ipProjectContributionOverview.getSummaryGender() != null
        && !ipProjectContributionOverview.getSummaryGender().isEmpty()) {
        gender = ipProjectContributionOverview.getSummaryGender();
      }
      outputYear = ipProjectContributionOverview.getYear();
      model.addRow(new Object[] {ipProgram, ipElement, bulletPoints, summary, plan, gender, outputYear, changeYearTab,
        isMidOutcome});
    }
    return model;
  }

  private TypedTableModel getPartnerLeaderTableModel(ProjectPartner projectLeader) {
    TypedTableModel model =
      new TypedTableModel(new String[] {"org_leader", "pp_id", "responsibilities", "countryOffices"},
        new Class[] {String.class, Long.class, String.class, String.class}, 0);
    long ppId = 0;
    String orgLeader = null;
    String responsibilities = null;
    String countryOffices = null;
    if (projectLeader.getId() != null && projectLeader.getInstitution() != null) {
      ppId = projectLeader.getId();
      orgLeader = projectLeader.getInstitution().getComposedName();
      responsibilities = projectLeader.getResponsibilities();
      for (ProjectPartnerLocation projectPartnerLocation : projectLeader.getProjectPartnerLocations().stream()
        .filter(ppl -> ppl.isActive()).collect(Collectors.toList())) {
        if (countryOffices == null || countryOffices.isEmpty()) {
          countryOffices = projectPartnerLocation.getInstitutionLocation().getLocElement().getName();
        } else {
          countryOffices += ", " + projectPartnerLocation.getInstitutionLocation().getLocElement().getName();
        }
      }
      model.addRow(new Object[] {orgLeader, ppId, responsibilities, countryOffices});
    } else if (projectLeader.getId() != null && projectLeader.getInstitution() == null) {
      ppId = projectLeader.getId();
      model.addRow(new Object[] {null, ppId, responsibilities, countryOffices});
    } else if (projectLeader.getId() == null && projectLeader.getInstitution() != null) {
      orgLeader = projectLeader.getInstitution().getComposedName();
      responsibilities = projectLeader.getResponsibilities();
      for (ProjectPartnerLocation projectPartnerLocation : projectLeader.getProjectPartnerLocations().stream()
        .filter(ppl -> ppl.isActive()).collect(Collectors.toList())) {
        if (countryOffices == null || countryOffices.isEmpty()) {
          countryOffices = projectPartnerLocation.getInstitutionLocation().getLocElement().getName();
        } else {
          countryOffices += ", " + projectPartnerLocation.getInstitutionLocation().getLocElement().getName();
        }
      }
      model.addRow(new Object[] {orgLeader, null, responsibilities, countryOffices});
    }
    return model;
  }

  private TypedTableModel getPartnersLessonsTableModel() {
    TypedTableModel model =
      new TypedTableModel(new String[] {"year", "lesson"}, new Class[] {Integer.class, String.class}, 0);
    if (!this.getSelectedCycle().equals("")) {
      for (ProjectComponentLesson pcl : project.getProjectComponentLessons().stream()
        .sorted((p1, p2) -> p1.getYear() - p2.getYear())
        .filter(c -> c.isActive() && c.getComponentName().equals("partners")
          && c.getCycle().equals(this.getSelectedCycle()) && c.getYear() == this.getSelectedYear()
          && c.getPhase() != null && c.getPhase().equals(this.getSelectedPhase()))
        .collect(Collectors.toList())) {
        String lessons = null;
        if (pcl.getLessons() != null && !pcl.getLessons().isEmpty()) {
          lessons = pcl.getLessons();
        }
        model.addRow(new Object[] {pcl.getYear(), lessons});
      }
    }
    return model;
  }

  private TypedTableModel getPartnersOtherTableModel(ProjectPartner projectLeader) {
    TypedTableModel model =
      new TypedTableModel(new String[] {"instituttion", "pp_id", "leader_count", "responsibilities", "countryOffices"},
        new Class[] {String.class, Long.class, Integer.class, String.class, String.class}, 0);
    int leaderCount = 0;
    String responsibilities = null;
    String countryOffices = null;
    if (projectLeader.getId() != null) {
      leaderCount = 1;
      // Get list of partners except project leader
      for (ProjectPartner projectPartner : project.getProjectPartners().stream().filter(c -> c.isActive()
        && c.getId() != projectLeader.getId() && c.getPhase() != null && c.getPhase().equals(this.getSelectedPhase()))
        .collect(Collectors.toList())) {
        countryOffices = null;
        responsibilities = projectPartner.getResponsibilities();
        for (ProjectPartnerLocation projectPartnerLocation : projectPartner.getProjectPartnerLocations().stream()
          .filter(ppl -> ppl.isActive()).collect(Collectors.toList())) {
          if (countryOffices == null || countryOffices.isEmpty()) {
            countryOffices = projectPartnerLocation.getInstitutionLocation().getLocElement().getName();
          } else {
            countryOffices += ", " + projectPartnerLocation.getInstitutionLocation().getLocElement().getName();
          }
        }
        model.addRow(new Object[] {projectPartner.getInstitution().getComposedName(), projectPartner.getId(),
          leaderCount, responsibilities, countryOffices});
      }
    } else {
      // Get all partners
      for (ProjectPartner projectPartner : project.getProjectPartners().stream()
        .filter(c -> c.isActive() && c.getPhase() != null && c.getPhase().equals(this.getSelectedPhase()))
        .collect(Collectors.toList())) {
        countryOffices = null;
        responsibilities = projectPartner.getResponsibilities();
        for (ProjectPartnerLocation projectPartnerLocation : projectPartner.getProjectPartnerLocations().stream()
          .filter(ppl -> ppl.isActive()).collect(Collectors.toList())) {
          if (countryOffices == null || countryOffices.isEmpty()) {
            countryOffices = projectPartnerLocation.getInstitutionLocation().getLocElement().getName();
          } else {
            countryOffices += ", " + projectPartnerLocation.getInstitutionLocation().getLocElement().getName();
          }
        }
        model.addRow(new Object[] {projectPartner.getInstitution().getComposedName(), projectPartner.getId(),
          leaderCount, responsibilities, countryOffices});
      }
    }
    return model;
  }

  private TypedTableModel getPartnersTableModel() {
    TypedTableModel model =
      new TypedTableModel(new String[] {"count", "overall"}, new Class[] {Integer.class, String.class}, 0);
    int partnersSize = 0;
    List<ProjectPartner> projectPartners = project.getProjectPartners().stream()
      .filter(pp -> pp.isActive() && pp.getPhase() != null && pp.getPhase().equals(this.getSelectedPhase()))
      .collect(Collectors.toList());
    if (!projectPartners.isEmpty()) {
      partnersSize = projectPartners.size();
    }

    String overall = "";
    if (this.getSelectedCycle().equals("Reporting")) {
      // Get project partners overall
      for (ProjectPartner projectPartner : project.getProjectPartners().stream()
        .filter(pp -> pp.isActive() && pp.getPhase() != null && pp.getPhase().equals(this.getSelectedPhase()))
        .collect(Collectors.toList())) {
        for (ProjectPartnerOverall projectPartnerOverall : projectPartner.getProjectPartnerOveralls().stream()
          .filter(ppo -> ppo.getYear() == this.getSelectedYear()).collect(Collectors.toList())) {
          if (!projectPartnerOverall.getOverall().isEmpty()) {
            if (!projectPartnerOverall.getOverall().equals("null")) {
              overall = projectPartnerOverall.getOverall();
            }
          }
        }
      }
      if (overall.isEmpty()) {
        overall = "&lt;Not Defined&gt;";
      }
    }
    model.addRow(new Object[] {partnersSize, overall});
    return model;
  }

  public Project getProject() {
    return project;
  }

  private TypedTableModel getProjectHighlightReportingTableModel() {
    TypedTableModel model = new TypedTableModel(
      new String[] {"id", "title", "author", "subject", "publisher", "year_reported", "highlights_types",
        "highlights_is_global", "start_date", "end_date", "keywords", "countries", "image", "highlight_desc",
        "introduction", "results", "partners", "links", "width", "heigth"},
      new Class[] {Long.class, String.class, String.class, String.class, String.class, Long.class, String.class,
        String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class,
        String.class, String.class, String.class, Integer.class, Integer.class},
      0);
    SimpleDateFormat formatter = new SimpleDateFormat("MMM yyyy");
    for (ProjectHighlight projectHighlight : project.getProjectHighligths().stream()
      .filter(ph -> ph.isActive() && ph.getYear() != null && ph.getYear() >= this.getSelectedYear())
      .collect(Collectors.toList())) {
      String title = null, author = null, subject = null, publisher = null, highlightsTypes = "",
        highlightsIsGlobal = null, startDate = null, endDate = null, keywords = null, countries = "", image = "",
        highlightDesc = null, introduction = null, results = null, partners = null, links = null;
      Long yearReported = null;
      int width = 0;
      int heigth = 0;
      if (projectHighlight.getTitle() != null && !projectHighlight.getTitle().isEmpty()) {
        title = projectHighlight.getTitle();
      }
      if (projectHighlight.getAuthor() != null && !projectHighlight.getAuthor().isEmpty()) {
        author = projectHighlight.getAuthor();
      }
      if (projectHighlight.getSubject() != null && !projectHighlight.getSubject().isEmpty()) {
        subject = projectHighlight.getSubject();
      }
      if (projectHighlight.getPublisher() != null && !projectHighlight.getPublisher().isEmpty()) {
        publisher = projectHighlight.getPublisher();
      }
      if (projectHighlight.getYear() != null) {
        yearReported = projectHighlight.getYear();
      }
      for (ProjectHighlightType projectHighlightType : projectHighlight.getProjectHighligthsTypes().stream()
        .filter(pht -> pht.isActive()).collect(Collectors.toList())) {
        if (ProjectHighligthsTypeEnum.getEnum(projectHighlightType.getIdType() + "") != null) {
          highlightsTypes +=
            "<br>● " + ProjectHighligthsTypeEnum.getEnum(projectHighlightType.getIdType() + "").getDescription();
        }
      }
      if (highlightsTypes.isEmpty()) {
        highlightsTypes = null;
      }
      if (projectHighlight.isGlobal() == true) {
        highlightsIsGlobal = "Yes";
      } else {
        highlightsIsGlobal = "No";
      }
      if (projectHighlight.getStartDate() != null) {
        startDate = formatter.format(projectHighlight.getStartDate());
      }
      if (projectHighlight.getEndDate() != null) {
        endDate = formatter.format(projectHighlight.getEndDate());
      }
      if (projectHighlight.getKeywords() != null && !projectHighlight.getKeywords().isEmpty()) {
        keywords = projectHighlight.getKeywords();
      }
      int countriesFlag = 0;
      for (ProjectHighlightCountry projectHighlightCountry : projectHighlight.getProjectHighligthCountries().stream()
        .filter(phc -> phc.isActive()).collect(Collectors.toList())) {
        if (projectHighlightCountry.getLocElement() != null) {
          if (countriesFlag == 0) {
            countries += projectHighlightCountry.getLocElement().getName();
            countriesFlag++;
          } else {
            countries += ", " + projectHighlightCountry.getLocElement().getName();
            countriesFlag++;
          }
        }
      }
      if (countries.isEmpty()) {
        countries = null;
      }
      if (projectHighlight.getFile() != null) {
        double pageWidth = 612 * 0.4;
        double pageHeigth = 792 * 0.4;
        double imageWidth = 0;
        double imageHeigth = 0;
        image =
          this.getHightlightImagePath(projectHighlight.getProject().getId()) + projectHighlight.getFile().getFileName();
        Image imageFile = null;
        LOG.info("Image name: " + image);
        File url;
        try {
          url = new File(image);
        } catch (Exception e) {
          LOG.warn("Failed to get image File. Url was set to null. Exception: " + e.getMessage());
          url = null;
        }
        if (url != null && url.exists()) {
          // System.out.println("Project: " + projectHighlight.getProject().getId() + " PH: " +
          // projectHighlight.getId());
          try {
            imageFile = Image.getInstance(FileManager.readURL(url));
            // System.out.println("W: " + imageFile.getWidth() + " \nH: " + imageFile.getHeight());
            if (imageFile.getWidth() >= imageFile.getHeight()) {
              imageWidth = pageWidth;
              imageHeigth = imageFile.getHeight() * (((pageWidth * 100) / imageFile.getWidth()) / 100);
            } else {
              imageHeigth = pageHeigth;
              imageWidth = imageFile.getWidth() * (((pageHeigth * 100) / imageFile.getHeight()) / 100);
            }
            // System.out.println("New W: " + imageWidth + " \nH: " + imageHeigth);
            width = (int) imageWidth;
            heigth = (int) imageHeigth;
            // If successful, process the message
          } catch (BadElementException e) {
            LOG.warn("BadElementException getting image: " + e.getMessage());
            image = "";
          } catch (MalformedURLException e) {
            LOG.warn("MalformedURLException getting image: " + e.getMessage());
            image = "";
          } catch (IOException e) {
            LOG.warn("IOException getting image: " + e.getMessage());
            image = "";
          }
        }
      }
      if (projectHighlight.getDescription() != null && !projectHighlight.getDescription().isEmpty()) {
        highlightDesc = projectHighlight.getDescription();
      }
      if (projectHighlight.getObjectives() != null && !projectHighlight.getObjectives().isEmpty()) {
        introduction = projectHighlight.getObjectives();
      }
      if (projectHighlight.getResults() != null && !projectHighlight.getResults().isEmpty()) {
        results = projectHighlight.getResults();
      }
      if (projectHighlight.getPartners() != null && !projectHighlight.getPartners().isEmpty()) {
        partners = projectHighlight.getPartners();
      }
      if (projectHighlight.getLinks() != null && !projectHighlight.getLinks().isEmpty()) {
        links = projectHighlight.getLinks();
      }
      model.addRow(new Object[] {projectHighlight.getId(), title, author, subject, publisher, yearReported,
        highlightsTypes, highlightsIsGlobal, startDate, endDate, keywords, countries, image, highlightDesc,
        introduction, results, partners, links, width, heigth});
    }
    return model;
  }

  public long getProjectID() {
    return projectID;
  }

  public ProjectInfo getProjectInfo() {
    return projectInfo;
  }

  private TypedTableModel getProjectOtherOutcomesTableModel() {
    TypedTableModel model =
      new TypedTableModel(new String[] {"out_statement", "year"}, new Class[] {String.class, Integer.class}, 0);
    String outStatement = "";
    int outcomeYear = 0;
    for (ProjectOutcomePandr projectOutcomePandr : project.getProjectOutcomesPandr().stream()
      .sorted((p1, p2) -> p1.getYear() - p2.getYear())
      .filter(pop -> pop.isActive() && pop.getYear() != 2019 && pop.getYear() != this.getSelectedYear())
      .collect(Collectors.toList())) {
      outStatement = projectOutcomePandr.getStatement();
      outcomeYear = projectOutcomePandr.getYear();
      model.addRow(new Object[] {outStatement, outcomeYear});
    }
    return model;
  }

  private TypedTableModel getProjectOutcomesTableModel() {
    TypedTableModel model = new TypedTableModel(
      new String[] {"out_statement", "out_statement_current", "out_progress_current", "communication_current",
        "current_year", "lessons", "file"},
      new Class[] {String.class, String.class, String.class, String.class, Integer.class, String.class, String.class},
      0);
    String outStatement = null;
    String outStatementCurrent = null;
    String communicationCurrent = null;
    String outProgressCurrent = null;
    String file = null;
    String lessons = null;
    for (ProjectOutcomePandr projectOutcomePandr : project.getProjectOutcomesPandr().stream()
      .filter(pop -> pop.isActive()
        && (pop.getYear() == APConstants.MID_OUTCOME_YEAR || pop.getYear() == this.getSelectedYear()))
      .collect(Collectors.toList())) {
      if (projectOutcomePandr.getYear() == APConstants.MID_OUTCOME_YEAR) {
        if (projectOutcomePandr.getStatement() != null) {
          if (!projectOutcomePandr.getStatement().isEmpty()) {
            outStatement = projectOutcomePandr.getStatement();
          }
        }
      }
      if (projectOutcomePandr.getYear() == this.getSelectedYear()) {
        if (projectOutcomePandr.getStatement() != null) {
          if (!projectOutcomePandr.getStatement().isEmpty()) {
            outStatementCurrent = projectOutcomePandr.getStatement();
          }
        }
        if (projectOutcomePandr.getComunication() != null) {
          if (!projectOutcomePandr.getComunication().isEmpty()) {
            communicationCurrent = projectOutcomePandr.getComunication();
          }
        }
        if (projectOutcomePandr.getAnualProgress() != null) {
          if (!projectOutcomePandr.getAnualProgress().isEmpty()) {
            outProgressCurrent = projectOutcomePandr.getAnualProgress();
          }
        }
        if (projectOutcomePandr.getFile() != null) {
          file = (this.getProjectOutcomeUrl() + projectOutcomePandr.getFile().getFileName()).replace(" ", "%20");
        }
      }
    }
    if (!this.getSelectedCycle().equals("")) {
      for (ProjectComponentLesson pcl : project.getProjectComponentLessons().stream()
        .sorted((p1, p2) -> p1.getYear() - p2.getYear())
        .filter(pcl -> pcl.isActive() && pcl.getComponentName().equals("outcomesPandR")
          && pcl.getPhase().equals(this.getSelectedPhase()) && pcl.getCycle().equals(this.getSelectedCycle())
          && pcl.getYear() == this.getSelectedYear())
        .collect(Collectors.toList())) {
        if (pcl.getLessons() != null) {
          if (!pcl.getLessons().isEmpty()) {
            lessons = pcl.getLessons();
          }
        }
      }
    }
    model.addRow(new Object[] {outStatement, outStatementCurrent, outProgressCurrent, communicationCurrent,
      this.getSelectedYear(), lessons, file});
    return model;
  }

  public String getProjectOutcomeUrl() {
    return config.getDownloadURL() + "/" + this.getProjectOutcomeUrlPath().replace('\\', '/');
  }

  public String getProjectOutcomeUrlPath() {
    return config.getProjectsBaseFolder(this.getCrpSession()) + File.separator + project.getId() + File.separator
      + "project_outcome" + File.separator;
  }

  private TypedTableModel getRLTableModel(List<CrpProgram> regions) {
    TypedTableModel model = new TypedTableModel(new String[] {"RL"}, new Class[] {String.class}, 0);
    String global = "";
    if (projectInfo.getNoRegional() != null && projectInfo.getNoRegional()) {
      global = "Global";
      model.addRow(new Object[] {global});
    } else {
      for (CrpProgram crpProgram : regions) {
        model.addRow(new Object[] {crpProgram.getComposedName()});
      }
    }
    return model;
  }

  public HashMap<Long, String> getTargetUnitList() {
    return targetUnitList;
  }

  /**
   * Get total amount per institution year and type
   * 
   * @param institutionId
   * @param year current platform year
   * @param budgetType
   * @return String with the total amount.
   */
  public String getTotalAmount(long institutionId, int year, long budgetType, Long projectId, Integer coFinancing) {
    return projectBudgetManager.amountByBudgetType(institutionId, year, budgetType, projectId, coFinancing,
      this.getSelectedPhase().getId());
  }

  /**
   * Get gender amount per institution, year and budet type
   * 
   * @param institutionId
   * @param year
   * @param budgetType
   * @return
   */
  public double getTotalGender(long institutionId, int year, long budgetType, long projectID, Integer coFinancing) {

    List<ProjectBudget> budgets =
      projectBudgetManager.getByParameters(institutionId, year, budgetType, projectID, coFinancing);

    double totalGender = 0;
    if (budgets != null) {
      for (ProjectBudget projectBudget : budgets) {
        if (projectBudget.getPhase().equals(this.getSelectedPhase())) {
          double amount = projectBudget.getAmount() != null ? projectBudget.getAmount() : 0.0;
          double gender = projectBudget.getGenderPercentage() != null ? projectBudget.getGenderPercentage() : 0.0;

          totalGender = totalGender + (amount * (gender / 100));
        }
      }
    }

    return totalGender;
  }

  /**
   * Get gender amount per institution, year and budet type
   * 
   * @param institutionId
   * @param year
   * @param budgetType
   * @return
   */
  public double getTotalGender(long institutionId, int year, long budgetType, Project project, Integer coFinancing) {

    List<ProjectBudget> budgets =
      projectBudgetManager.getByParameters(institutionId, year, budgetType, project.getId(), coFinancing);

    double totalGender = 0;
    if (budgets != null) {
      for (ProjectBudget projectBudget : budgets) {
        if (projectBudget.getPhase().equals(this.getSelectedPhase())) {
          double amount = projectBudget.getAmount() != null ? projectBudget.getAmount() : 0;
          double gender = projectBudget.getGenderPercentage() != null ? projectBudget.getGenderPercentage() : 0;

          totalGender = totalGender + (amount * (gender / 100));
        }
      }
    }

    return totalGender;
  }

  /**
   * Get total gender percentaje per institution, year and type
   * 
   * @param institutionId
   * @param year
   * @param budgetType
   * @return
   */
  public double getTotalGenderPer(long institutionId, int year, long budgetType, long projectId, Integer coFinancing) {

    String totalAmount = this.getTotalAmount(institutionId, year, budgetType, projectId, coFinancing);

    double dTotalAmount = Double.parseDouble(totalAmount);

    double totalGender = this.getTotalGender(institutionId, year, budgetType, projectId, coFinancing);

    if (dTotalAmount != 0) {
      return (totalGender * 100) / dTotalAmount;
    } else {
      return 0.0;
    }
  }

  /**
   * Get the total budget per year and type
   * 
   * @param year current year in the platform
   * @param type budget type (W1W2/Bilateral/W3/Center funds)
   * @param coFinancing coFinancing 1: cofinancing+no cofinancing, 2: cofinancing 3: no cofinancing
   * @return total budget in the year and type passed as parameters
   */
  public double getTotalYear(int year, long type, Project project, Integer coFinancing) {
    double total = 0;

    switch (coFinancing) {
      case 1:
        for (ProjectBudget pb : project.getProjectBudgets().stream()
          .filter(pb -> pb.isActive() && pb.getYear() == year && pb.getBudgetType() != null
            && pb.getBudgetType().getId() == type && pb.getPhase() != null
            && pb.getPhase().equals(this.getSelectedPhase()))
          .collect(Collectors.toList())) {
          total = total + pb.getAmount();
        }
        break;
      case 2:
        for (ProjectBudget pb : project.getProjectBudgets().stream()
          .filter(pb -> pb.isActive() && pb.getYear() == year && pb.getBudgetType() != null
            && pb.getBudgetType().getId() == type && pb.getFundingSource() != null
            && pb.getFundingSource().getFundingSourceInfo(this.getSelectedPhase()).getW1w2() != null
            && pb.getFundingSource().getFundingSourceInfo(this.getSelectedPhase()).getW1w2().booleanValue() == true
            && pb.getPhase() != null && pb.getPhase().equals(this.getSelectedPhase()))
          .collect(Collectors.toList())) {
          total = total + pb.getAmount();
        }
        break;
      case 3:
        for (ProjectBudget pb : project.getProjectBudgets().stream()
          .filter(pb -> pb.isActive() && pb.getYear() == year && pb.getBudgetType() != null
            && pb.getBudgetType().getId() == type && pb.getFundingSource() != null
            && pb.getFundingSource().getFundingSourceInfo(this.getSelectedPhase()).getW1w2() != null
            && pb.getFundingSource().getFundingSourceInfo(this.getSelectedPhase()).getW1w2().booleanValue() == false
            && pb.getPhase() != null && pb.getPhase().equals(this.getSelectedPhase()))
          .collect(Collectors.toList())) {

          total = total + pb.getAmount();
        }
        break;

      default:
        break;
    }

    return total;
  }

  /**
   * Verify if an institution isPPA or not
   * 
   * @param institution
   * @return boolean with true if is ppa and false if not
   */
  @Override
  public boolean isPPA(Institution institution) {
    if (institution == null) {
      return false;
    }
    if (institution.getId() != null) {
      institution = institutionManager.getInstitutionById(institution.getId());
      if (institution != null) {
        if (institution.getCrpPpaPartners().stream().filter(c -> c.isActive()).collect(Collectors.toList())
          .size() > 0) {
          return true;
        }
      }
    }
    return false;
  }

  public boolean isRegionalOutcome(IpElement outcome) {
    List<IpElement> translatedOf =
      ipElementManager.getIPElementsRelated(outcome.getId().intValue(), APConstants.ELEMENT_RELATION_TRANSLATION);
    return !translatedOf.isEmpty();
  }

  @Override
  /**
   * Prepare the parameters of the project.
   * Note: If you add a parameter here, you must add it in the ProjectSubmissionAction class
   */
  public void prepare() {
    this.setGeneralParameters();

    // Set projectID
    try {
      this
        .setProjectID(Long.parseLong(StringUtils.trim(this.getRequest().getParameter(APConstants.PROJECT_REQUEST_ID))));
      this.setCrpSession(this.getLoggedCrp().getAcronym());
    } catch (Exception e) {
      LOG.error("Failed to get " + APConstants.PROJECT_REQUEST_ID + " parameter. Exception: " + e.getMessage());
    }
    // Get project from DB
    try {
      this.setProject(projectManager.getProjectById(this.getProjectID()));
    } catch (Exception e) {
      LOG.error("Failed to get project. Exception: " + e.getMessage());
    }
    this.setProjectInfo(project.getProjecInfoPhase(this.getSelectedPhase()));
  }

  private DeliverablePartnership responsiblePartner(Deliverable deliverable) {
    try {
      DeliverablePartnership partnership = deliverable.getDeliverablePartnerships().stream()
        .filter(
          dp -> dp.isActive() && dp.getPartnerType().equals(DeliverablePartnershipTypeEnum.RESPONSIBLE.getValue()))
        .collect(Collectors.toList()).get(0);
      return partnership;
    } catch (Exception e) {
      return null;
    }
  }

  public void setBytesPDF(byte[] bytesPDF) {
    this.bytesPDF = bytesPDF;
  }

  public void setHasW1W2Co(Boolean hasW1W2Co) {
    this.hasW1W2Co = hasW1W2Co;
  }

  public void setProject(Project project) {
    this.project = project;
  }

  public void setProjectID(long projectID) {
    this.projectID = projectID;
  }

  public void setProjectInfo(ProjectInfo projectInfo) {
    this.projectInfo = projectInfo;
  }

  public void setTargetUnitList(HashMap<Long, String> targetUnitList) {
    this.targetUnitList = targetUnitList;
  }


}