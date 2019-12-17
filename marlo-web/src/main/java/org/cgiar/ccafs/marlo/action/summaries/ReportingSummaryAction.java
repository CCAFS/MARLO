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
import org.cgiar.ccafs.marlo.config.MarloLocalizedTextProvider;
import org.cgiar.ccafs.marlo.data.manager.CrossCuttingScoringManager;
import org.cgiar.ccafs.marlo.data.manager.CrpProgramManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableCrossCuttingMarkerManager;
import org.cgiar.ccafs.marlo.data.manager.GenderTypeManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.InstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.IpElementManager;
import org.cgiar.ccafs.marlo.data.manager.LocElementManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectBudgetManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyCountryManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyGeographicScopeManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyLinkManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyPolicyManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyQuantificationManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationContributingOrganizationManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectLp6ContributionDeliverableManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectLp6ContributionManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPolicyCrossCuttingMarkerManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPolicyCrpManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPolicyInnovationManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPolicyManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPolicyOwnerManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPolicySubIdoManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndPolicyInvestimentTypeManager;
import org.cgiar.ccafs.marlo.data.manager.RepositoryChannelManager;
import org.cgiar.ccafs.marlo.data.manager.SrfTargetUnitManager;
import org.cgiar.ccafs.marlo.data.model.Activity;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.CrpTargetUnit;
import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.data.model.DeliverableActivity;
import org.cgiar.ccafs.marlo.data.model.DeliverableCrossCuttingMarker;
import org.cgiar.ccafs.marlo.data.model.DeliverableCrp;
import org.cgiar.ccafs.marlo.data.model.DeliverableDataSharingFile;
import org.cgiar.ccafs.marlo.data.model.DeliverableDissemination;
import org.cgiar.ccafs.marlo.data.model.DeliverableFundingSource;
import org.cgiar.ccafs.marlo.data.model.DeliverableIntellectualAsset;
import org.cgiar.ccafs.marlo.data.model.DeliverableIntellectualAssetPantentTypeEnum;
import org.cgiar.ccafs.marlo.data.model.DeliverableIntellectualAssetTypeEnum;
import org.cgiar.ccafs.marlo.data.model.DeliverableMetadataElement;
import org.cgiar.ccafs.marlo.data.model.DeliverableParticipant;
import org.cgiar.ccafs.marlo.data.model.DeliverablePublicationMetadata;
import org.cgiar.ccafs.marlo.data.model.DeliverableQualityCheck;
import org.cgiar.ccafs.marlo.data.model.DeliverableType;
import org.cgiar.ccafs.marlo.data.model.DeliverableUser;
import org.cgiar.ccafs.marlo.data.model.DeliverableUserPartnership;
import org.cgiar.ccafs.marlo.data.model.DeliverableUserPartnershipPerson;
import org.cgiar.ccafs.marlo.data.model.ExpectedStudyProject;
import org.cgiar.ccafs.marlo.data.model.GlobalUnitProject;
import org.cgiar.ccafs.marlo.data.model.Institution;
import org.cgiar.ccafs.marlo.data.model.IpElement;
import org.cgiar.ccafs.marlo.data.model.IpIndicator;
import org.cgiar.ccafs.marlo.data.model.IpProjectContribution;
import org.cgiar.ccafs.marlo.data.model.IpProjectIndicator;
import org.cgiar.ccafs.marlo.data.model.LocElement;
import org.cgiar.ccafs.marlo.data.model.ProgramType;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectBudget;
import org.cgiar.ccafs.marlo.data.model.ProjectClusterActivity;
import org.cgiar.ccafs.marlo.data.model.ProjectCommunication;
import org.cgiar.ccafs.marlo.data.model.ProjectComponentLesson;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudy;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyCountry;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyCrp;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyFlagship;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyGeographicScope;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyInfo;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyInstitution;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyLink;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyPolicy;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyQuantification;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudySrfTarget;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudySubIdo;
import org.cgiar.ccafs.marlo.data.model.ProjectFocus;
import org.cgiar.ccafs.marlo.data.model.ProjectHighlight;
import org.cgiar.ccafs.marlo.data.model.ProjectHighlightCountry;
import org.cgiar.ccafs.marlo.data.model.ProjectHighlightType;
import org.cgiar.ccafs.marlo.data.model.ProjectHighligthsTypeEnum;
import org.cgiar.ccafs.marlo.data.model.ProjectInfo;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovation;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationContributingOrganization;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationCountry;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationCrp;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationDeliverable;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationGeographicScope;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationInfo;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationOrganization;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationRegion;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationShared;
import org.cgiar.ccafs.marlo.data.model.ProjectLeverage;
import org.cgiar.ccafs.marlo.data.model.ProjectLocation;
import org.cgiar.ccafs.marlo.data.model.ProjectLocationElementType;
import org.cgiar.ccafs.marlo.data.model.ProjectLp6Contribution;
import org.cgiar.ccafs.marlo.data.model.ProjectLp6ContributionDeliverable;
import org.cgiar.ccafs.marlo.data.model.ProjectOutcome;
import org.cgiar.ccafs.marlo.data.model.ProjectPartner;
import org.cgiar.ccafs.marlo.data.model.ProjectPartnerLocation;
import org.cgiar.ccafs.marlo.data.model.ProjectPartnerPartnership;
import org.cgiar.ccafs.marlo.data.model.ProjectPartnerPartnershipLocation;
import org.cgiar.ccafs.marlo.data.model.ProjectPartnerPartnershipResearchPhase;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicy;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicyCrossCuttingMarker;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicyCrp;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicyInnovation;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicyOwner;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicySubIdo;
import org.cgiar.ccafs.marlo.data.model.ProjectStatusEnum;
import org.cgiar.ccafs.marlo.data.model.RepositoryChannel;
import org.cgiar.ccafs.marlo.data.model.SrfTargetUnit;
import org.cgiar.ccafs.marlo.data.model.Submission;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.FileManager;
import org.cgiar.ccafs.marlo.utils.URLShortener;

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
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Image;
import com.opensymphony.xwork2.LocalizedTextProvider;
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
 * ReportingSummaryAction
 * 
 * @author avalencia - CCAFS
 * @author Christian Garcia - CIAT/CCAFS
 * @date Nov 8, 2017
 * @time 10:30:10 AM: get deliverable dissemination from RepositoryChannel table
 */
@Named
public class ReportingSummaryAction extends BaseSummariesAction implements Summary {

  private static final long serialVersionUID = -624982650510682813L;

  private static Logger LOG = LoggerFactory.getLogger(ReportingSummaryAction.class);

  public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
    Map<Object, Boolean> seen = new ConcurrentHashMap<>();
    return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
  }

  private final LocalizedTextProvider localizedTextProvider;


  // PDF bytes
  private byte[] bytesPDF;

  // Streams
  InputStream inputStream;

  // Parameters
  private long startTime;
  private HashMap<Long, String> targetUnitList;

  private Project project;
  private Boolean hasW1W2Co;
  private Boolean hasGender;
  private long projectID;
  private ProjectInfo projectInfo;
  private List<ProjectLp6Contribution> projectLp6Contributions;
  private List<ProjectInnovation> innovationsList;

  // Managers
  private final CrpProgramManager programManager;
  private final GenderTypeManager genderTypeManager;
  private final InstitutionManager institutionManager;
  private final ProjectBudgetManager projectBudgetManager;
  private final LocElementManager locElementManager;
  private final CrossCuttingScoringManager crossCuttingScoringManager;
  private final IpElementManager ipElementManager;
  private final RepositoryChannelManager repositoryChannelManager;
  private final SrfTargetUnitManager srfTargetUnitManager;
  private final ResourceManager resourceManager;
  private final ProjectExpectedStudyCountryManager projectExpectedStudyCountryManager;
  private final DeliverableCrossCuttingMarkerManager deliverableCrossCuttingMarkerManager;
  private final ProjectExpectedStudyLinkManager projectExpectedStudyLinkManager;
  private final ProjectPolicyManager projectPolicyManager;
  private final ProjectInnovationManager projectInnovationManager;
  private final ProjectInnovationContributingOrganizationManager projectInnovationContributingOrganizationManager;
  private final ProjectLp6ContributionDeliverableManager projectLp6ContributionDeliverableManager;
  private final RepIndPolicyInvestimentTypeManager repIndPolicyInvestimentTypeManager;
  private final ProjectExpectedStudyPolicyManager projectExpectedStudyPolicyManager;
  private final ProjectPolicyOwnerManager projectPolicyOwnerManager;
  private final ProjectPolicyInnovationManager projectPolicyInnovationManager;
  private final ProjectPolicyCrpManager projectPolicyCrpManager;
  private final ProjectPolicyCrossCuttingMarkerManager projectPolicyCrossCuttingMarkerManager;
  private final ProjectPolicySubIdoManager projectPolicySubIdoManager;
  private final ProjectExpectedStudyGeographicScopeManager projectExpectedStudyGeographicScopeManager;
  private final ProjectExpectedStudyQuantificationManager projectExpectedStudyQuantificationManager;

  @Inject
  public ReportingSummaryAction(APConfig config, GlobalUnitManager crpManager, ProjectManager projectManager,
    GenderTypeManager genderTypeManager, CrpProgramManager programManager, InstitutionManager institutionManager,
    ProjectBudgetManager projectBudgetManager, LocElementManager locElementManager, IpElementManager ipElementManager,
    SrfTargetUnitManager srfTargetUnitManager, PhaseManager phaseManager,
    RepositoryChannelManager repositoryChannelManager, LocalizedTextProvider localizedTextProvider,
    CrossCuttingScoringManager crossCuttingScoringManager, ResourceManager resourceManager,
    ProjectExpectedStudyCountryManager projectExpectedStudyCountryManager,
    DeliverableCrossCuttingMarkerManager deliverableCrossCuttingMarkerManager,
    ProjectExpectedStudyLinkManager projectExpectedStudyLinkManager, ProjectPolicyManager projectPolicyManager,
    ProjectInnovationManager projectInnovationManager,
    ProjectInnovationContributingOrganizationManager projectInnovationContributingOrganizationManager,
    ProjectLp6ContributionDeliverableManager projectLp6ContributionDeliverableManager,
    RepIndPolicyInvestimentTypeManager repIndPolicyInvestimentTypeManager,
    ProjectLp6ContributionManager projectLp6ContributionManager,
    ProjectExpectedStudyPolicyManager projectExpectedStudyPolicyManager,
    ProjectPolicyOwnerManager projectPolicyOwnerManager, ProjectPolicyInnovationManager projectPolicyInnovationManager,
    ProjectPolicyCrpManager projectPolicyCrpManager,
    ProjectPolicyCrossCuttingMarkerManager projectPolicyCrossCuttingMarkerManager,
    ProjectPolicySubIdoManager projectPolicySubIdoManager,
    ProjectExpectedStudyGeographicScopeManager projectExpectedStudyGeographicScopeManager,
    ProjectExpectedStudyQuantificationManager projectExpectedStudyQuantificationManager) {
    super(config, crpManager, phaseManager, projectManager);
    this.programManager = programManager;
    this.institutionManager = institutionManager;
    this.projectBudgetManager = projectBudgetManager;
    this.locElementManager = locElementManager;
    this.ipElementManager = ipElementManager;
    this.genderTypeManager = genderTypeManager;
    this.localizedTextProvider = localizedTextProvider;
    this.srfTargetUnitManager = srfTargetUnitManager;
    this.repositoryChannelManager = repositoryChannelManager;
    this.crossCuttingScoringManager = crossCuttingScoringManager;
    this.resourceManager = resourceManager;
    this.projectExpectedStudyCountryManager = projectExpectedStudyCountryManager;
    this.deliverableCrossCuttingMarkerManager = deliverableCrossCuttingMarkerManager;
    this.projectExpectedStudyLinkManager = projectExpectedStudyLinkManager;
    this.projectPolicyManager = projectPolicyManager;
    this.projectInnovationManager = projectInnovationManager;
    this.projectInnovationContributingOrganizationManager = projectInnovationContributingOrganizationManager;
    this.projectLp6ContributionDeliverableManager = projectLp6ContributionDeliverableManager;
    this.repIndPolicyInvestimentTypeManager = repIndPolicyInvestimentTypeManager;
    this.projectExpectedStudyPolicyManager = projectExpectedStudyPolicyManager;
    this.projectPolicyOwnerManager = projectPolicyOwnerManager;
    this.projectPolicyInnovationManager = projectPolicyInnovationManager;
    this.projectPolicyCrpManager = projectPolicyCrpManager;
    this.projectPolicyCrossCuttingMarkerManager = projectPolicyCrossCuttingMarkerManager;
    this.projectPolicySubIdoManager = projectPolicySubIdoManager;
    this.projectExpectedStudyGeographicScopeManager = projectExpectedStudyGeographicScopeManager;
    this.projectExpectedStudyQuantificationManager = projectExpectedStudyQuantificationManager;
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
    Boolean hasActivities = false;
    try {
      hasActivities = this.hasSpecificities(APConstants.CRP_ACTIVITES_MODULE);
    } catch (Exception e) {
      LOG.warn("Failed to get " + APConstants.CRP_ACTIVITES_MODULE
        + " parameter. Parameter will be set as false. Exception: " + e.getMessage());
      hasActivities = false;
    }

    /**
     * Menu Planning
     */
    masterReport.getParameterValues().put("i8nProjectPlanningMenu", "1. " + this.getText("projects.menu.description"));
    masterReport.getParameterValues().put("i8nPartnersPlanningMenu", "2. " + this.getText("projects.menu.partners"));
    masterReport.getParameterValues().put("i8nLocationsPlanningMenu", "3. " + this.getText("projects.menu.locations"));
    if (this.getProject().getProjectInfo().getAdministrative() != null
      && this.getProject().getProjectInfo().getAdministrative() == true) {
      masterReport.getParameterValues().put("i8nExpectedStudiesPlanningMenu",
        "4. " + this.getText("projects.menu.expectedStudies"));
      masterReport.getParameterValues().put("i8nDeliverablesPlanningMenu",
        "5. " + "Expected " + this.getText("projects.menu.deliverables"));
      if (hasActivities) {
        masterReport.getParameterValues().put("i8nActivitiesPlanningMenu",
          "6. " + this.getText("projects.menu.activities"));
        masterReport.getParameterValues().put("i8nBudgetPlanningMenu",
          "7. " + "Project " + this.getText("projects.menu.budget") + " (USD)");
        masterReport.getParameterValues().put("i8nBudgetPartnerPlanningMenu",
          "7.1 " + this.getText("projects.menu.budgetByPartners") + " (USD)");
        masterReport.getParameterValues().put("i8nBudgetCoAsPlanningMenu",
          "7.2 " + this.getText("planning.cluster") + " (USD)");
      } else {
        masterReport.getParameterValues().put("i8nBudgetPlanningMenu",
          "6. " + "Project " + this.getText("projects.menu.budget") + " (USD)");
        masterReport.getParameterValues().put("i8nBudgetPartnerPlanningMenu",
          "6.1 " + this.getText("projects.menu.budgetByPartners") + " (USD)");
        masterReport.getParameterValues().put("i8nBudgetCoAsPlanningMenu",
          "6.2 " + this.getText("planning.cluster") + " (USD)");
      }
    } else {
      masterReport.getParameterValues().put("i8nOutcomesPlanningMenu",
        "4. " + this.getText("projects.menu.contributionsCrpList"));
      masterReport.getParameterValues().put("i8nExpectedStudiesPlanningMenu",
        "5. " + this.getText("projects.menu.expectedStudies"));
      masterReport.getParameterValues().put("i8nDeliverablesPlanningMenu",
        "6. " + "Expected " + this.getText("projects.menu.deliverables"));
      if (hasActivities) {
        masterReport.getParameterValues().put("i8nActivitiesPlanningMenu",
          "7. " + this.getText("projects.menu.activities"));
        masterReport.getParameterValues().put("i8nBudgetPlanningMenu",
          "8. " + "Project " + this.getText("projects.menu.budget") + " (USD)");
        masterReport.getParameterValues().put("i8nBudgetPartnerPlanningMenu",
          "8.1 " + this.getText("projects.menu.budgetByPartners") + " (USD)");
        masterReport.getParameterValues().put("i8nBudgetCoAsPlanningMenu",
          "8.2 " + this.getText("planning.cluster") + " (USD)");
      } else {
        masterReport.getParameterValues().put("i8nBudgetPlanningMenu",
          "7. " + "Project " + this.getText("projects.menu.budget") + " (USD)");
        masterReport.getParameterValues().put("i8nBudgetPartnerPlanningMenu",
          "7.1 " + this.getText("projects.menu.budgetByPartners") + " (USD)");
        masterReport.getParameterValues().put("i8nBudgetCoAsPlanningMenu",
          "7.2 " + this.getText("planning.cluster") + " (USD)");
      }
    }
    /**
     * End Menu Planning
     */


    /**
     * Menu Reporting
     */
    masterReport.getParameterValues().put("i8nProjectReportingMenu", "1. " + this.getText("projects.menu.description"));
    masterReport.getParameterValues().put("i8nPartnersReportingMenu", "2. " + this.getText("projects.menu.partners"));
    masterReport.getParameterValues().put("i8nLocationsReportingMenu", "3. " + this.getText("projects.menu.locations"));
    masterReport.getParameterValues().put("i8nOutcomesReportingMenu", "4. " + this.getText("breadCrumb.menu.outcomes"));


    if (this.getProject().getProjectInfo().getAdministrative() != null
      && this.getProject().getProjectInfo().getAdministrative() == true) {
      masterReport.getParameterValues().put("i8nStudiesReportingMenu", "4. " + this.getText("menu.studies"));
    } else {
      masterReport.getParameterValues().put("i8nFlagshipOutcomesReportingMenu",
        "4.1 " + this.getText("projects.menu.contributionsCrpList"));
      masterReport.getParameterValues().put("i8nStudiesReportingMenu", "4.2 " + this.getText("menu.studies"));
    }
    masterReport.getParameterValues().put("i8nDeliverablesReportingMenu",
      "5. " + this.getText("projects.menu.deliverables"));
    masterReport.getParameterValues().put("i8nInnovationsReportingMenu",
      "6. " + this.getText("projects.menu.innovations"));
    masterReport.getParameterValues().put("i8nProjectHighlightsReportingMenu",
      "7. " + this.getText("breadCrumb.menu.projectHighlights"));
    if (hasActivities) {
      masterReport.getParameterValues().put("i8nActivitiesReportingMenu",
        "8. " + this.getText("projects.menu.activities"));
      masterReport.getParameterValues().put("i8nLeveragesReportingMenu",
        "9. " + this.getText("breadCrumb.menu.leverage"));
      masterReport.getParameterValues().put("i8nProjectPolicyMenu", "10. " + this.getText("breadCrumb.menu.policy"));
      masterReport.getParameterValues().put("i8nProjectContributionMenu",
        "11. " + this.getText("breadCrumb.menu.contribution"));

    } else {
      masterReport.getParameterValues().put("i8nLeveragesReportingMenu",
        "8. " + this.getText("breadCrumb.menu.leverage"));
    }


    /**
     * End Menu Reporting
     */
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
    masterReport.getParameterValues().put("i8nDeliverablesROtherPartners", this.getText("deliverable.otherPartner"));


    /**
     * Partners reporting
     */
    masterReport.getParameterValues().put("i8nPartnerPartnership", "Partnership");
    masterReport.getParameterValues().put("i8nPartnerPartnershipFormal",
      this.getText("projectPartners.hasPartnerships"));
    masterReport.getParameterValues().put("i8nPartnerPartnershipResearchPhase",
      this.getText("projectPartners.researchPhase"));
    masterReport.getParameterValues().put("i8nPartnerPartnershipGeographicScope",
      this.getText("projectPartners.geographicScope"));
    masterReport.getParameterValues().put("i8nPartnerPartnershipRegion", this.getText("projectPartners.region"));
    masterReport.getParameterValues().put("i8nPartnerPartnershipCountries",
      this.getText("projectPartners.partnershipsCountries"));
    masterReport.getParameterValues().put("i8nPartnerPartnershipMainArea",
      this.getText("projectPartners.partnershipMainarea.readText"));


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
     * Contribution to project outcomes
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
     * Expected Studies
     */
    masterReport.getParameterValues().put("i8nExpectedStudiesNoData", this.getText("summaries.expectedStudies.noData"));
    masterReport.getParameterValues().put("i8nExpectedStudiesCaseStudy", this.getText("summaries.projectStudy"));
    masterReport.getParameterValues().put("i8nExpectedStudiesProjects",
      this.getText("summaries.expectedStudies.projects"));
    masterReport.getParameterValues().put("i8nExpectedStudiesTitle", this.getText("summaries.study.title"));
    masterReport.getParameterValues().put("i8nExpectedStudiesTagged", this.getText("summaries.study.tagged"));
    masterReport.getParameterValues().put("i8nExpectedStudiesYear", this.getText("summaries.study.year"));
    masterReport.getParameterValues().put("i8nExpectedStudiesStatus", this.getText("study.status"));
    masterReport.getParameterValues().put("i8nExpectedStudiesType", this.getText("expectedStudy.type"));
    masterReport.getParameterValues().put("i8nExpectedStudiesOutcomesStory",
      this.getText("summaries.study.outcomeStory"));
    masterReport.getParameterValues().put("i8nStudiesCGIARInnovations",
      this.getText("summaries.study.cgiarInnovations"));
    masterReport.getParameterValues().put("i8nStudiesCGIARInnovationsList",
      this.getText("summaries.study.innovationsList"));
    masterReport.getParameterValues().put("i8nExpectedStudiesLinksProvided",
      this.getText("summaries.study.linksProvided"));
    masterReport.getParameterValues().put("i8nExpectedStudiesSubIdo", this.getText("expectedStudy.srfSubIdo"));
    masterReport.getParameterValues().put("i8nExpectedStudiesSRF", this.getText("expectedStudy.srfSloIndicator"));
    masterReport.getParameterValues().put("i8nExpectedStudiesComments", this.getText("expectedStudy.comments"));
    masterReport.getParameterValues().put("i8nExpectedStudiesGeographicScope", this.getText("expectedStudy.scope"));
    masterReport.getParameterValues().put("i8nExpectedStudiesRegion", this.getText("study.region"));
    masterReport.getParameterValues().put("i8nExpectedStudiesContries", this.getText("involveParticipants.countries"));
    masterReport.getParameterValues().put("i8nExpectedStudiesScopeComments", this.getText("expectedStudy.comments"));
    masterReport.getParameterValues().put("i8nExpectedCommunicationsMaterials",
      this.getText("summaries.study.CommunicationsMaterials"));


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
    masterReport.getParameterValues().put("i8nDeliverableNewExpectedYear", this.getText("deliverable.newExpectedYear"));
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
     * Reporting Outcomes Phase Two
     * Project Outcomes
     */
    masterReport.getParameterValues().put("i8nFlagshipOutcomesRNoData",
      this.getText("summaries.flagshipOutcomes.noData"));
    masterReport.getParameterValues().put("i8nFlagshipOutcomesROutcomeContribution",
      this.getText("projectOutcome.contributionToThisOutcome"));
    masterReport.getParameterValues().put("i8nFlagshipOutcomesRMilestonesContribution",
      this.getText("projectOutcome.contributionToMilestones"));
    masterReport.getParameterValues().put("i8nFlagshipOutcomesRLessons",
      this.getText("projectOutcome.lessons.reporting.readText"));
    masterReport.getParameterValues().put("i8nFlagshipOutcomesRNarrative",
      this.getText("projectOutcome.narrativeTarget.readText"));
    masterReport.getParameterValues().put("i8nFlagshipOutcomesRAchievedValue",
      this.getText("projectOutcome.achievedValue"));
    masterReport.getParameterValues().put("i8nFlagshipOutcomesRAchievedUnit",
      this.getText("projectOutcome.achievedUnit"));
    masterReport.getParameterValues().put("i8nFlagshipOutcomesRNarrativeAchieved",
      this.getText("projectOutcome.narrativeAchieved.readText"));
    masterReport.getParameterValues().put("i8nFlagshipOutcomesRCommunications",
      this.getText("summaries.flagshipOutcomes.communications"));


    /*
     * Reporting Outcomes Phase Two
     * Studies
     */
    masterReport.getParameterValues().put("i8nStudiesRNoData", this.getText("summaries.study.noData"));
    masterReport.getParameterValues().put("i8nStudiesRCaseStudy", this.getText("summaries.projectStudy"));
    masterReport.getParameterValues().put("i8nStudiesRTitle", this.getText("summaries.study.title"));
    masterReport.getParameterValues().put("i8nStudiesRYear", this.getText("summaries.study.year"));
    masterReport.getParameterValues().put("i8nStudiesRStatus", this.getText("study.status"));
    masterReport.getParameterValues().put("i8nStudiesRType", this.getText("study.type"));
    masterReport.getParameterValues().put("i8nStudiesROutcomeImpactStatement",
      this.getText("summaries.study.outcomeStatement"));
    masterReport.getParameterValues().put("i8nStudiesRIsContributionText",
      this.getText("summaries.study.reportingIndicatorThree"));
    masterReport.getParameterValues().put("i8nStudiesRpolicyInvestimentType",
      this.getText("study.reportingIndicatorThree.policyType"));
    masterReport.getParameterValues().put("i8nStudiesRPolicyAmount",
      this.getText("study.reportingIndicatorThree.amount"));
    masterReport.getParameterValues().put("i8nStudiesROrganizationType",
      this.getText("study.reportingIndicatorThree.organizationType"));
    masterReport.getParameterValues().put("i8nStudiesRStageProcess",
      this.getText("study.reportingIndicatorThree.stage"));
    masterReport.getParameterValues().put("i8nStudiesRStageStudy", this.getText("summaries.study.maturityChange"));
    masterReport.getParameterValues().put("i8nStudiesRStrategicResults",
      this.getText("summaries.study.stratgicResultsLink"));
    masterReport.getParameterValues().put("i8nStudiesRSubIdos",
      this.getText("summaries.study.stratgicResultsLink.subIDOs"));
    masterReport.getParameterValues().put("i8nStudiesRSRFTargets",
      this.getText("summaries.study.stratgicResultsLink.srfTargets"));
    masterReport.getParameterValues().put("i8nStudiesRTopLevelCommentst",
      this.getText("summaries.study.stratgicResultsLink.comments"));
    masterReport.getParameterValues().put("i8nStudiesRGeographicScope", this.getText("study.geographicScope"));
    masterReport.getParameterValues().put("i8nStudiesRRegion", this.getText("study.region"));
    masterReport.getParameterValues().put("i8nStudiesRContries", this.getText("involveParticipants.countries"));
    masterReport.getParameterValues().put("i8nStudiesRScopeComments",
      this.getText("study.geographicScopeComments.readText"));
    masterReport.getParameterValues().put("i8nStudiesRKeyContributors",
      this.getText("summaries.study.keyContributors"));
    masterReport.getParameterValues().put("i8nStudiesRCrps", this.getText("study.keyContributors.crps"));
    masterReport.getParameterValues().put("i8nStudiesRFlagships", this.getText("study.keyContributors.flagships"));
    masterReport.getParameterValues().put("i8nStudiesRRegions", this.getText("study.keyContributors.regions"));
    masterReport.getParameterValues().put("i8nStudiesRInstitutions",
      this.getText("study.keyContributors.externalPartners"));
    masterReport.getParameterValues().put("i8nStudiesRElaborationOutcomeImpactStatement",
      this.getText("summaries.study.elaborationStatement"));
    masterReport.getParameterValues().put("i8nStudiesRReferenceText", this.getText("summaries.study.referencesCited"));
    masterReport.getParameterValues().put("i8nStudiesRReferencesFile",
      this.getText("study.referencesCitedAttach.readText"));
    masterReport.getParameterValues().put("i8nStudiesRQuantification", this.getText("summaries.study.quantification"));
    masterReport.getParameterValues().put("i8nStudiesRGenderDevelopment",
      this.getText("summaries.study.crossCuttingRelevance"));
    masterReport.getParameterValues().put("i8nPolicyRGenderDevelopment", this.getText("policy.crossCuttingRelevance"));
    masterReport.getParameterValues().put("i8nStudiesRGenderRelevance", this.getText("study.genderRelevance"));
    masterReport.getParameterValues().put("i8nStudiesRYouthRelevance", this.getText("study.youthRelevance"));
    masterReport.getParameterValues().put("i8nStudiesRCapacityRelevance", this.getText("study.capDevRelevance"));
    masterReport.getParameterValues().put("i8nStudiesRClimateRelevance", this.getText("study.climateRelevance"));

    masterReport.getParameterValues().put("i8nStudiesRClimate", this.getText("study.capDevRelevance"));
    masterReport.getParameterValues().put("i8nStudiesROtherCrossCuttingDimensions",
      this.getText("summaries.study.otherCrossCutting"));
    masterReport.getParameterValues().put("i8nStudiesRComunicationsMaterial",
      this.getText("study.outcomestory.readText"));
    masterReport.getParameterValues().put("i8nStudiesRComunicationsFile",
      this.getText("study.communicationMaterialsAttach.readText"));
    masterReport.getParameterValues().put("i8nStudiesRContacts", this.getText("summaries.study.contacts"));
    masterReport.getParameterValues().put("i8nCaseStudiesRStudyProjects",
      this.getText("summaries.expectedStudies.projects"));


    /*
     * Reporting
     * Deliverables
     */
    masterReport.getParameterValues().put("i8nDeliverablesRMainInfo",
      this.getText("project.deliverable.generalInformation.titleTab"));
    masterReport.getParameterValues().put("i8nDeliverablesRDescription",
      this.getText("project.deliverable.generalInformation.description.readText"));
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
    masterReport.getParameterValues().put("i8nDeliverablesRConfidentialUrl",
      this.getText("project.deliverable.dissemination.confidentialURL"));
    masterReport.getParameterValues().put("i8nDeliverablesRIsOpenAccess",
      this.getText("project.deliverable.dissemination.v.isOpenAccess"));
    masterReport.getParameterValues().put("i8nDeliverablesROpenAccessRestriction",
      this.getText("project.deliverable.dissemination.v.openAccessRestriction"));
    masterReport.getParameterValues().put("i8nDeliverablesRALicense", this.getText("project.deliverable.v.ALicense"));
    masterReport.getParameterValues().put("i8nDeliverablesRPublicationAllowModifications",
      this.getText("publication.publicationAllowModifications"));
    masterReport.getParameterValues().put("i8nDeliverablesRHasIntellectualAsset",
      this.getText("deliverable.hasIntellectualAsset.title"));
    masterReport.getParameterValues().put("i8nDeliverablesRHasParticipants",
      this.getText("deliverable.involveParticipants.title"));
    masterReport.getParameterValues().put("i8nDeliverablesRIntellectualAsset",
      this.getText("summaries.deliverable.intellectualAssets"));
    masterReport.getParameterValues().put("i8nDeliverablesRIntellectualAssetApplicants",
      this.getText("intellectualAsset.applicants"));
    masterReport.getParameterValues().put("i8nDeliverablesRIntellectualAssetType",
      this.getText("intellectualAsset.type"));
    masterReport.getParameterValues().put("i8nDeliverablesRIntellectualAssetTitle",
      this.getText("intellectualAsset.title.readText"));
    masterReport.getParameterValues().put("i8nDeliverablesRIntellectualAssetFillingType",
      this.getText("intellectualAsset.fillingType"));
    masterReport.getParameterValues().put("i8nDeliverablesRIntellectualAssetPantentStatus",
      this.getText("intellectualAsset.patentStatus"));
    masterReport.getParameterValues().put("i8nDeliverablesRIntellectualAssetPatentType",
      this.getText("intellectualAsset.patentType"));
    masterReport.getParameterValues().put("i8nDeliverablesRIntellectualAssetPvpVarietyName",
      this.getText("intellectualAsset.varietyName"));
    masterReport.getParameterValues().put("i8nDeliverablesRIntellectualAssetPvpStatus",
      this.getText("intellectualAsset.status"));
    masterReport.getParameterValues().put("i8nDeliverablesRIntellectualAssetPvpCountry",
      this.getText("intellectualAsset.country"));
    masterReport.getParameterValues().put("i8nDeliverablesRIntellectualAssetPvpApplicationNumber",
      this.getText("intellectualAsset.appRegNumber"));
    masterReport.getParameterValues().put("i8nDeliverablesRIntellectualAssetPvpBreederCrop",
      this.getText("intellectualAsset.breederCrop"));
    masterReport.getParameterValues().put("i8nDeliverablesRIntellectualAssetDateFilling",
      this.getText("intellectualAsset.dateFilling"));
    masterReport.getParameterValues().put("i8nDeliverablesRIntellectualAssetDateRegistration",
      this.getText("intellectualAsset.dateRegistration"));
    masterReport.getParameterValues().put("i8nDeliverablesRIntellectualAssetDateExpiry",
      this.getText("intellectualAsset.dateExpiry"));
    masterReport.getParameterValues().put("i8nDeliverablesRIntellectualAssetAdditionalInformation",
      this.getText("intellectualAsset.additionalInformation.readText"));
    masterReport.getParameterValues().put("i8nDeliverablesRIntellectualAssetLinkPublished",
      this.getText("intellectualAsset.link.readText"));
    masterReport.getParameterValues().put("i8nDeliverablesRIntellectualAssetCommunication",
      this.getText("intellectualAsset.publicCommunication.readText"));

    masterReport.getParameterValues().put("i8nDeliverablesRParticipant",
      this.getText("summaries.deliverable.participants"));
    masterReport.getParameterValues().put("i8nDeliverablesRParticipantEvent",
      this.getText("involveParticipants.title"));
    masterReport.getParameterValues().put("i8nDeliverablesRParticipantActivityType",
      this.getText("involveParticipants.typeActivity"));
    masterReport.getParameterValues().put("i8nDeliverablesRParticipantAcademicDegree",
      this.getText("involveParticipants.academicDegree"));
    masterReport.getParameterValues().put("i8nDeliverablesRParticipantTotalParticipants",
      this.getText("involveParticipants.participants"));
    masterReport.getParameterValues().put("i8nDeliverablesRParticipantFemales",
      this.getText("involveParticipants.females"));
    masterReport.getParameterValues().put("i8nDeliverablesRParticipantType",
      this.getText("involveParticipants.participantsType"));

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
     * Innovations
     */
    masterReport.getParameterValues().put("i8nInnovationNoData", this.getText("summaries.innovation.noData"));
    masterReport.getParameterValues().put("i8nInnovationsRInnovation", this.getText("summaries.innovation"));
    masterReport.getParameterValues().put("i8nInnovationRTitle", this.getText("projectInnovations.title"));
    masterReport.getParameterValues().put("i8nInnovationRNarrative",
      this.getText("projectInnovations.narrative.readText"));
    masterReport.getParameterValues().put("i8nInnovationRPhaseResearch", this.getText("projectInnovations.phase"));
    masterReport.getParameterValues().put("i8nInnovationRStageInnovation", this.getText("projectInnovations.stage"));
    masterReport.getParameterValues().put("i8nInnovationRInnovationType",
      this.getText("projectInnovations.innovationType"));
    masterReport.getParameterValues().put("i8nInnovationRContributionOfCrp",
      this.getText("projectInnovations.contributionOfCrp"));
    masterReport.getParameterValues().put("i8nInnovationRDegreeInnovation",
      this.getText("projectInnovations.degreeInnovation"));
    masterReport.getParameterValues().put("i8nInnovationRGeographicScope",
      this.getText("projectInnovations.geographicScope"));
    masterReport.getParameterValues().put("i8nInnovationRRegion", this.getText("projectInnovations.region"));
    masterReport.getParameterValues().put("i8nInnovationRCountries", this.getText("projectInnovations.countries"));
    masterReport.getParameterValues().put("i8nInnovationROrganizations",
      this.getText("summaries.innovation.organizationalType"));
    masterReport.getParameterValues().put("i8nInnovationRProjectExpectedStudy",
      this.getText("caseStudy.caseStudyTitle"));
    masterReport.getParameterValues().put("i8nInnovationRDescriptionStage",
      this.getText("projectInnovations.stageDescription.readText"));
    masterReport.getParameterValues().put("i8nInnovationRLeadOrganization",
      this.getText("projectInnovations.leadOrganization"));
    masterReport.getParameterValues().put("i8nInnovationRContributingOrganizations",
      this.getText("projectInnovations.topFiveContributing"));
    masterReport.getParameterValues().put("i8nInnovationREvidenceLink",
      this.getText("summaries.innovation.evidenceLink"));
    masterReport.getParameterValues().put("i8nInnovationRDeliverables",
      this.getText("projectInnovations.deliverableId"));
    masterReport.getParameterValues().put("i8nInnovationRCrps", this.getText("projectInnovations.contributing"));
    masterReport.getParameterValues().put("i8nInnovationRGenderFocusLevel",
      this.getText("projectInnovations.genderRelevance"));
    masterReport.getParameterValues().put("i8nInnovationRGenderExplaniation",
      this.getText("projectInnovations.genderRelevance.explanation.readText"));
    masterReport.getParameterValues().put("i8nInnovationRYouthFocusLevel",
      this.getText("projectInnovations.youthRelevance"));
    masterReport.getParameterValues().put("i8nInnovationRYouthExplaniation",
      this.getText("projectInnovations.youthRelevance.explanation.readText"));


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

    /*
     * Reporting
     * Project contribution to Lp6
     */
    masterReport.getParameterValues().put("i8nProjectContributionRNoData", this.getText("projectContribution.noData"));
    masterReport.getParameterValues().put("i8nProjectContributionDescription",
      this.getText("projectContribution.narrative"));
    masterReport.getParameterValues().put("i8nProjectContributionDeliverables",
      this.getText("projectContribution.deliverables"));
    masterReport.getParameterValues().put("i8nProjectContributionGeographicScope",
      this.getText("projectContribution.grographicScope"));
    masterReport.getParameterValues().put("i8nProjectContributionWorkingAcrossFlagships",
      this.getText("projectContribution.workingAcrossFlagships"));
    masterReport.getParameterValues().put("i8nProjectContributionUndertakingEfforts",
      this.getText("projectContribution.undertakingEfforts"));
    masterReport.getParameterValues().put("i8nProjectContributionProvidingPathways",
      this.getText("projectContribution.providingPathways"));
    masterReport.getParameterValues().put("i8nProjectContributionTop3",
      this.getText("projectContribution.top3Partnerships"));
    masterReport.getParameterValues().put("i8nProjectContributionUndertakingEffortsCSA",
      this.getText("projectContribution.undertakingEffortsCSA"));
    masterReport.getParameterValues().put("i8nProjectContributionUndertakingInitiative",
      this.getText("projectContribution.initivativeRelated"));

    /*
     * Project Policy
     */
    masterReport.getParameterValues().put("i8nProjectPolicyYear", this.getText("projectPolicy.year"));
    masterReport.getParameterValues().put("i8nProjectPolicyRNoData", this.getText("projectPolicy.noData"));
    masterReport.getParameterValues().put("i8nProjectPolicyInvestmentType", this.getText("projectPolicy.investment"));
    masterReport.getParameterValues().put("i8nProjectPolicyOrganizationType",
      this.getText("projectPolicy.implementing"));
    masterReport.getParameterValues().put("i8nProjectPolicyLevelMaturity", this.getText("projectPolicy.maturity"));
    masterReport.getParameterValues().put("i8nProjectPolicyWhose", this.getText("projectPolicy.whose"));
    masterReport.getParameterValues().put("i8nProjectPolicyOutcomeCaseReport",
      this.getText("projectPolicy.outcomeCaseReport"));
    masterReport.getParameterValues().put("i8nProjectPolicyInnovation", this.getText("projectPolicy.innovations"));
    masterReport.getParameterValues().put("i8nProjectPolicyContributingCRP",
      this.getText("projectPolicy.contributingCRP"));
    masterReport.getParameterValues().put("i8nProjectPolicySubIDOs", this.getText("projectPolicy.subIDOS"));
    masterReport.getParameterValues().put("i8nProjectPolicyGender", this.getText("projectPolicy.gender"));
    masterReport.getParameterValues().put("i8nProjectPolicyYouth", this.getText("projectPolicy.youth"));
    masterReport.getParameterValues().put("i8nProjectPolicyCapDev", this.getText("projectPolicy.capdev"));
    masterReport.getParameterValues().put("i8nProjectPolicyClimateChange", this.getText("projectPolicy.climateChange"));
    masterReport.getParameterValues().put("i8nProjectPolicyGeographicScope",
      this.getText("projectPolicy.geographicScope"));
    masterReport.getParameterValues().put("i8nProjectPolicyNarrative", this.getText("policy.narrative"));

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

    if (this.getSelectedPhase() == null) {
      return NOT_FOUND;
    }

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

    try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
      String masterQueryName = "Main_Query";
      Resource reportResource;
      if (this.getSelectedCycle().equals("Planning")) {
        reportResource = resourceManager.createDirectly(
          this.getClass().getResource("/pentaho/crp/ProjectFullPDF(Planning).prpt"), MasterReport.class);
      } else {
        reportResource = resourceManager.createDirectly(
          this.getClass().getResource("/pentaho/crp/ProjectFullPDF(Reporting).prpt"), MasterReport.class);
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
        // get project leader

        ProjectPartner projectLeader = project.getLeader(this.getSelectedPhase());
        // get Flagships related to the project sorted by acronym
        List<CrpProgram> flagships = new ArrayList<>();
        for (ProjectFocus projectFocuses : project.getProjectFocuses().stream()
          .sorted((o1, o2) -> o1.getCrpProgram().getAcronym().compareTo(o2.getCrpProgram().getAcronym()))
          .filter(
            c -> c.isActive() && c.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue()
              && c.getPhase() != null && c.getPhase().equals(this.getSelectedPhase())
              && c.getCrpProgram().getResearchArea() == null && c.getCrpProgram().getCrp().isCenterType() == false)
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
                && c.getPhase() != null && c.getPhase().equals(this.getSelectedPhase())
                && c.getCrpProgram().getResearchArea() == null && c.getCrpProgram().getCrp().isCenterType() == false)
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

        // Subreport Outcomes
        if (this.getSelectedCycle().equals("Planning")) {
          args.clear();
          this.fillSubreport((SubReport) hm.get("outcomes"), "outcomes_list", args);
          this.fillSubreport((SubReport) hm.get("expected_studies"), "expected_studies", args);
        } else {
          args.clear();
          // Outcomes Phase Two
          this.fillSubreport((SubReport) hm.get("flagship_outcomes"), "flagship_outcomes", args);
          this.fillSubreport((SubReport) hm.get("studies"), "studies", args);
        }

        // Subreport Deliverables
        if (this.getSelectedCycle().equals("Planning")) {
          // Subreport Outcomes
          args.clear();
          this.fillSubreport((SubReport) hm.get("deliverables"), "deliverables_list", args);
        } else {
          // args.clear();
          this.fillSubreport((SubReport) hm.get("deliverables"), "deliverables_list_reporting", args);
          this.fillSubreport((SubReport) hm.get("innovations"), "innovations", args);
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
        } else {
          // Subreport Leverages for reporting
          this.fillSubreport((SubReport) hm.get("leverages"), "leverages", args);
        }

        if (this.getSelectedCycle().equals("Reporting")) {
          // Project Policy
          args.clear();
          this.fillSubreport((SubReport) hm.get("project_policy"), "project_policy", args);

          // Lp6 contribution
          args.clear();
          this.fillSubreport((SubReport) hm.get("project_contribution"), "project_contribution", args);
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
      case "expected_studies":
        model = this.getExpectedStudiesTableModel();
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
      // Reporting Only
      case "flagship_outcomes":
        model = this.getFlagshipOutcomesTableModel();
        break;
      case "studies":
        model = this.getStudiesTableModel();
        break;
      case "deliverables_list_reporting":
        model = this.getDeliverablesReportingTableModel();
        break;
      case "innovations":
        model = this.getInnovationsTableModel();
        break;
      case "project_highlight":
        model = this.getProjectHighlightReportingTableModel();
        break;
      case "leverages":
        model = this.getLeveragesTableModel();
        break;
      case "project_policy":
        model = this.getProjectPolicyTableModel();
        break;
      case "project_contribution":
        model = this.getProjectContributionTableModel();
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
        "status", "deliverables"},
      new Class[] {Long.class, String.class, String.class, String.class, String.class, String.class, String.class,
        String.class, String.class},
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
        String deliverables = "";
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
        List<DeliverableActivity> deliverableActivityList = activity.getDeliverableActivities().stream()
          .filter(da -> da.isActive() && da.getPhase() != null && da.getPhase().equals(this.getSelectedPhase()))
          .collect(Collectors.toList());
        if (deliverableActivityList != null && !deliverableActivityList.isEmpty()) {
          for (DeliverableActivity deliverableActivity : deliverableActivityList) {
            String deliverableTitle = "";
            if (deliverableActivity.getDeliverable().getDeliverableInfo(this.getSelectedPhase()).getTitle() != null) {
              deliverableTitle =
                deliverableActivity.getDeliverable().getDeliverableInfo(this.getSelectedPhase()).getTitle();
            } else {
              deliverableTitle = "&lt;Not Defined&gt;";
            }
            if (deliverables.isEmpty()) {
              deliverables = "● D" + deliverableActivity.getDeliverable().getId() + ": " + deliverableTitle;
            } else {
              deliverables += "<br>● D" + deliverableActivity.getDeliverable().getId() + ": " + deliverableTitle;
            }
          }
        }
        status = ProjectStatusEnum.getValue(activity.getActivityStatus().intValue()).getStatus();
        model.addRow(new Object[] {activity.getId(), activity.getTitle(), activity.getDescription(), startDate, endDate,
          institution, activityLeader, status, deliverables});
      }
    }
    return model;
  }

  /*
   * public ProjectBudgetsCluserActvity getBudgetbyCoa(Long activitiyId, int year, long type) {
   * for (ProjectBudgetsCluserActvity pb : project.getProjectBudgetsCluserActvities().stream()
   * .filter(pb -> pb.isActive() && pb.getYear() == year && pb.getCrpClusterOfActivity() != null
   * && pb.getCrpClusterOfActivity().getId() == activitiyId && type == pb.getBudgetType().getId()
   * && pb.getPhase() != null && pb.getPhase().equals(this.getSelectedPhase()))
   * .collect(Collectors.toList())) {
   * return pb;
   * }
   * return null;
   * }
   */

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

    // TODO change when the ProjectCoas Duplication will has been fix it
    List<ProjectClusterActivity> coAsPrev = new ArrayList<>();

    List<ProjectClusterActivity> coAs = new ArrayList<>();
    coAsPrev = project.getProjectClusterActivities().stream()
      .filter(c -> c.isActive() && c.getPhase() != null && c.getPhase().equals(this.getSelectedPhase()))
      .collect(Collectors.toList());

    for (ProjectClusterActivity projectClusterActivity : coAsPrev) {
      if (coAs.isEmpty()) {
        coAs.add(projectClusterActivity);
      } else {
        boolean duplicated = false;
        for (ProjectClusterActivity projectCoas : coAs) {
          if (projectCoas.getCrpClusterOfActivity().getId()
            .equals(projectClusterActivity.getCrpClusterOfActivity().getId())) {
            duplicated = true;
            break;
          }
        }

        if (!duplicated) {
          coAs.add(projectClusterActivity);
        }
      }
    }
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


        /*
         * ProjectBudgetsCluserActvity w1w2pb =
         * this.getBudgetbyCoa(clusterActivity.getCrpClusterOfActivity().getId(), this.getSelectedYear(), 1);
         * if (w1w2pb != null) {
         * w1w2Percentage = df.format(w1w2pb.getAmount());
         * if (w1w2pb.getGenderPercentage() != null) {
         * w1w2GenderPer = df.format(w1w2pb.getGenderPercentage());
         * w1w2Gender = (w1w2pb.getGenderPercentage() * totalW1w2Gender) / 100;
         * }
         * w1w2 = (w1w2pb.getAmount() * totalW1w2) / 100;
         * }
         * ProjectBudgetsCluserActvity w3pb =
         * this.getBudgetbyCoa(clusterActivity.getCrpClusterOfActivity().getId(), this.getSelectedYear(), 2);
         * ProjectBudgetsCluserActvity bilateralpb =
         * this.getBudgetbyCoa(clusterActivity.getCrpClusterOfActivity().getId(), this.getSelectedYear(), 3);
         * ProjectBudgetsCluserActvity centerpb =
         * this.getBudgetbyCoa(clusterActivity.getCrpClusterOfActivity().getId(), this.getSelectedYear(), 4);
         * if (w3pb != null) {
         * w3Percentage = df.format(w3pb.getAmount());
         * if (w3pb.getGenderPercentage() != null) {
         * w3GenderPer = df.format(w3pb.getGenderPercentage());
         * w3Gender = (w3pb.getGenderPercentage() * totalW3Gender) / 100;
         * }
         * w3 = (w3pb.getAmount() * totalW3) / 100;
         * }
         * if (bilateralpb != null) {
         * bilateralPercentage = df.format(bilateralpb.getAmount());
         * if (bilateralpb.getGenderPercentage() != null) {
         * bilateralGenderPer = df.format(bilateralpb.getGenderPercentage());
         * bilateralGender = (bilateralpb.getGenderPercentage() * totalBilateralGender) / 100;
         * }
         * bilateral = (bilateralpb.getAmount() * totalBilateral) / 100;
         * }
         * if (centerpb != null) {
         * centerPercentage = df.format(centerpb.getAmount());
         * if (centerpb.getGenderPercentage() != null) {
         * centerGenderPer = df.format(centerpb.getGenderPercentage());
         * centerGender = (centerpb.getGenderPercentage() * totalCenterGender) / 100;
         * }
         * center = (centerpb.getAmount() * totalCenter) / 100;
         * }
         */
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

  public String getCaseStudyUrl(String project) {
    return config.getDownloadURL() + "/" + this.getCaseStudyUrlPath(project).replace('\\', '/');
  }

  public String getCaseStudyUrlPath(String project) {
    return config.getProjectsBaseFolder(this.getCrpSession()) + File.separator + project + File.separator + "caseStudy"
      + File.separator;
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
        "fl_contrib", "show_publication", "showCompilance", "deliv_description", "hasIntellectualAsset", "isPantent",
        "isPvp", "hasParticipants", "isAcademicDegree", "hasParticipantsText", "participantEvent",
        "participantActivityType", "participantAcademicDegree", "participantTotalParticipants", "participantFemales",
        "participantType", "hasIntellectualAssetText", "intellectualAssetApplicants", "intellectualAssetType",
        "intellectualAssetTitle", "intellectualAssetFillingType", "intellectualAssetPantentStatus",
        "intellectualAssetPatentType", "intellectualAssetPvpVarietyName", "intellectualAssetPvpStatus",
        "intellectualAssetPvpCountry", "intellectualAssetPvpApplicationNumber", "intellectualAssetPvpBreederCrop",
        "intellectualAssetDateFilling", "intellectualAssetDateRegistration", "intellectualAssetDateExpiry",
        "intellectualAssetAdditionalInformation", "intellectualAssetLinkPublished", "intellectualAssetCommunication",
        "otherPartner", "deliv_confidential_url"},
      new Class[] {Long.class, String.class, String.class, String.class, String.class, String.class, String.class,
        String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class,
        String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class,
        String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class,
        Boolean.class, String.class, String.class, String.class, String.class, Boolean.class, String.class,
        String.class, Boolean.class, String.class, Boolean.class, String.class, Boolean.class, String.class,
        String.class, String.class, String.class, String.class, String.class, String.class, Boolean.class,
        Boolean.class, String.class, Boolean.class, Boolean.class, Boolean.class, Boolean.class, Boolean.class,
        String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class,
        String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class,
        String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class,
        String.class, String.class, String.class},
      0);
    SimpleDateFormat formatter = new SimpleDateFormat("MMM yyyy");
    if (!project.getDeliverables().isEmpty()) {
      // get Reporting deliverables
      List<Deliverable> deliverables = project.getDeliverables().stream()
        .filter(d -> d.isActive() && d.getProject() != null && d.getProject().isActive()
          && d.getProject().getGlobalUnitProjects().stream()
            .filter(gup -> gup.isActive() && gup.getGlobalUnit().getId().equals(this.getLoggedCrp().getId()))
            .collect(Collectors.toList()).size() > 0
          && d.getDeliverableInfo(this.getSelectedPhase()) != null
          && d.getDeliverableInfo(this.getSelectedPhase()).getStatus() != null
          && ((d.getDeliverableInfo(this.getSelectedPhase()).getStatus().intValue() == Integer
            .parseInt(ProjectStatusEnum.Complete.getStatusId())
            && (d.getDeliverableInfo(this.getSelectedPhase()).getYear() >= this.getSelectedYear()
              || (d.getDeliverableInfo(this.getSelectedPhase()).getNewExpectedYear() != null
                && d.getDeliverableInfo(this.getSelectedPhase()).getNewExpectedYear().intValue() >= this
                  .getSelectedYear())))
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
          && d.getDeliverableInfo().isActive()
          && (d.getDeliverableInfo(this.getSelectedPhase()).getStatus().intValue() == Integer
            .parseInt(ProjectStatusEnum.Extended.getStatusId())
            || d.getDeliverableInfo(this.getSelectedPhase()).getStatus().intValue() == Integer
              .parseInt(ProjectStatusEnum.Complete.getStatusId())
            || d.getDeliverableInfo(this.getSelectedPhase()).getStatus().intValue() == Integer
              .parseInt(ProjectStatusEnum.Cancelled.getStatusId())))
        .collect(Collectors.toList());

      deliverables.sort((p1, p2) -> this.isDeliverableComplete(p1.getId(), this.getSelectedPhase().getId())
        .compareTo(this.isDeliverableComplete(p2.getId(), this.getSelectedPhase().getId())));
      HashSet<Deliverable> deliverablesHL = new HashSet<>();
      deliverablesHL.addAll(deliverables);
      deliverables.clear();
      deliverables.addAll(deliverablesHL);
      for (Deliverable deliverable : deliverables) {
        String delivType = null, delivSubType = null, delivYear = null, keyOutput = "", leader = null,
          institution = null, fundingSources = "", deliv_description = null, otherPartner = "";;
        String delivStatus =
          deliverable.getDeliverableInfo(this.getSelectedPhase()).getStatusName(this.getSelectedPhase());
        Boolean showFAIR = false, showPublication = false, showCompilance = false;

        if (deliverable.getDeliverableInfo().getDescription() != null
          && !deliverable.getDeliverableInfo().getDescription().isEmpty()) {
          deliv_description = deliverable.getDeliverableInfo().getDescription();
        }

        if (deliverable.getDeliverableInfo().getDeliverableType() != null) {
          DeliverableType deliverableSubType = deliverable.getDeliverableInfo().getDeliverableType();
          delivSubType = deliverableSubType.getName();
          if (deliverableSubType.getFair() != null && deliverableSubType.getFair()) {
            showFAIR = true;
          }
          showCompilance =
            this.hasDeliverableRule(deliverable.getDeliverableInfo(), APConstants.DELIVERABLE_RULE_COMPILANCE_CHECK);
          showPublication = this.hasDeliverableRule(deliverable.getDeliverableInfo(),
            APConstants.DELIVERABLE_RULE_PUBLICATION_METADATA);

          if (deliverableSubType.getDeliverableCategory() != null) {
            DeliverableType deliverableType = deliverableSubType.getDeliverableCategory();
            delivType = deliverableType.getName();
            if (deliverableType.getFair() != null && deliverableType.getFair()) {
              showFAIR = true;
            }
          }
        }
        if (delivStatus.equals("")) {
          delivStatus = null;
        }
        if (deliverable.getDeliverableInfo().getYear() != 0) {
          delivYear = "" + deliverable.getDeliverableInfo().getYear();
        }
        if (deliverable.getDeliverableInfo().getCrpClusterKeyOutput() != null) {
          keyOutput += "● ";
          if (deliverable.getDeliverableInfo().getCrpClusterKeyOutput().getCrpClusterOfActivity()
            .getCrpProgram() != null) {
            keyOutput += deliverable.getDeliverableInfo().getCrpClusterKeyOutput().getCrpClusterOfActivity()
              .getCrpProgram().getAcronym() + " - ";
          }
          keyOutput += deliverable.getDeliverableInfo().getCrpClusterKeyOutput().getKeyOutput();
        }
        // Get partner responsible and institution
        List<DeliverableUserPartnership> deliverablePartnershipResponsibles =
          deliverable.getDeliverableUserPartnerships().stream()
            .filter(dp -> dp.isActive() && dp.getPhase().getId().equals(this.getActualPhase().getId())
              && dp.getDeliverablePartnerType().getId().equals(APConstants.DELIVERABLE_PARTNERSHIP_TYPE_RESPONSIBLE))
            .collect(Collectors.toList());
        if (deliverablePartnershipResponsibles != null && deliverablePartnershipResponsibles.size() > 0) {
          if (deliverablePartnershipResponsibles.size() > 1) {
            LOG.warn("There are more than 1 deliverable responsibles for D" + deliverable.getId() + " "
              + this.getSelectedPhase().toString());
          }
          DeliverableUserPartnership responisble = deliverablePartnershipResponsibles.get(0);

          if (responisble != null) {
            if (responisble.getDeliverableUserPartnershipPersons() != null) {

              DeliverableUserPartnershipPerson responsibleppp = new DeliverableUserPartnershipPerson();
              List<DeliverableUserPartnershipPerson> persons = responisble.getDeliverableUserPartnershipPersons()
                .stream().filter(dp -> dp.isActive()).collect(Collectors.toList());
              if (persons.size() > 0) {
                responsibleppp = persons.get(0);
              }

              if (responsibleppp != null && responsibleppp.getUser() != null
                && responsibleppp.getUser().getComposedName() != null) {
                leader = responsibleppp.getUser().getComposedName() + "<br>&lt;" + responsibleppp.getUser().getEmail()
                  + "&gt;";
              }

              if (responisble.getInstitution() != null) {
                institution = responisble.getInstitution().getComposedName();
              }

            }
          }
        }


        // Get funding sources if exist
        for (DeliverableFundingSource dfs : deliverable.getDeliverableFundingSources().stream()
          .filter(d -> d.isActive() && d.getPhase() != null && d.getPhase().equals(this.getSelectedPhase())
            && d.getFundingSource().getFundingSourceInfo(this.getSelectedPhase()) != null)
          .collect(Collectors.toList())) {
          fundingSources += "● " + dfs.getFundingSource().getComposedName() + "<br>";
        }
        if (fundingSources.isEmpty()) {
          fundingSources = null;
        }
        // Get cross_cutting dimension
        String crossCutting = "";
        DeliverableCrossCuttingMarker deliverableCrossCuttingMarkerGender = deliverableCrossCuttingMarkerManager
          .getDeliverableCrossCuttingMarkerId(deliverable.getId(), 1, this.getSelectedPhase().getId());
        DeliverableCrossCuttingMarker deliverableCrossCuttingMarkerYouth = deliverableCrossCuttingMarkerManager
          .getDeliverableCrossCuttingMarkerId(deliverable.getId(), 2, this.getSelectedPhase().getId());
        DeliverableCrossCuttingMarker deliverableCrossCuttingMarkerCapDev = deliverableCrossCuttingMarkerManager
          .getDeliverableCrossCuttingMarkerId(deliverable.getId(), 3, this.getSelectedPhase().getId());
        DeliverableCrossCuttingMarker deliverableCrossCuttingMarkerClimate = deliverableCrossCuttingMarkerManager
          .getDeliverableCrossCuttingMarkerId(deliverable.getId(), 4, this.getSelectedPhase().getId());

        if (deliverableCrossCuttingMarkerGender != null) {
          if (deliverableCrossCuttingMarkerGender.getRepIndGenderYouthFocusLevel() != null) {
            crossCutting += "&nbsp;&nbsp;&nbsp;&nbsp;● Gender ("
              + deliverableCrossCuttingMarkerGender.getRepIndGenderYouthFocusLevel().getPowbName() + ")<br>";
          } else {
            crossCutting += "&nbsp;&nbsp;&nbsp;&nbsp;● Gender <br>";
          }
        }

        if (deliverableCrossCuttingMarkerYouth != null) {
          if (deliverableCrossCuttingMarkerYouth.getRepIndGenderYouthFocusLevel() != null) {
            crossCutting += "&nbsp;&nbsp;&nbsp;&nbsp;● Youth ("
              + deliverableCrossCuttingMarkerYouth.getRepIndGenderYouthFocusLevel().getPowbName() + ")<br>";
          } else {
            crossCutting += "&nbsp;&nbsp;&nbsp;&nbsp;● Youth <br>";
          }
        }
        if (deliverableCrossCuttingMarkerCapDev != null) {
          if (deliverableCrossCuttingMarkerCapDev.getRepIndGenderYouthFocusLevel() != null) {
            crossCutting += "&nbsp;&nbsp;&nbsp;&nbsp;● Capacity Development ("
              + deliverableCrossCuttingMarkerCapDev.getRepIndGenderYouthFocusLevel().getPowbName() + ")<br>";
          } else {
            crossCutting += "&nbsp;&nbsp;&nbsp;&nbsp;● Capacity Development <br>";
          }
        }
        if (deliverableCrossCuttingMarkerClimate != null) {
          if (deliverableCrossCuttingMarkerClimate.getRepIndGenderYouthFocusLevel() != null) {
            crossCutting += "&nbsp;&nbsp;&nbsp;&nbsp;● Climate Change ("
              + deliverableCrossCuttingMarkerClimate.getRepIndGenderYouthFocusLevel().getPowbName() + ")<br>";
          } else {
            crossCutting += "&nbsp;&nbsp;&nbsp;&nbsp;● Climate Change <br>";
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
        if (deliverable.getDeliverableInfo().getStatus() != null) {
          // Extended
          if (deliverable.getDeliverableInfo().getStatus().intValue() == Integer
            .parseInt(ProjectStatusEnum.Extended.getStatusId())) {
            if (deliverable.getDeliverableInfo().getNewExpectedYear() != null
              && deliverable.getDeliverableInfo().getNewExpectedYear().intValue() != -1) {
              delivNewYear = deliverable.getDeliverableInfo().getNewExpectedYear();
            }
            delivNewYearJustification = deliverable.getDeliverableInfo().getStatusDescription();
          }
          // Cancelled
          if (deliverable.getDeliverableInfo().getStatus().intValue() == Integer
            .parseInt(ProjectStatusEnum.Cancelled.getStatusId())) {
            delivNewYearJustification = deliverable.getDeliverableInfo().getStatusDescription();
          }
        }
        String delivDisseminationChannel = null;
        String delivDisseminationUrl = null;
        String delivConfidentialUrl = null;
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
            RepositoryChannel repositoryChannel = repositoryChannelManager
              .getRepositoryChannelByShortName(deliverableDissemination.getDisseminationChannel());
            if (repositoryChannel != null) {
              delivDisseminationChannel = repositoryChannel.getName();
            }
          }
          if (deliverableDissemination.getDisseminationUrl() != null
            && !deliverableDissemination.getDisseminationUrl().isEmpty()) {
            delivDisseminationUrl = deliverableDissemination.getDisseminationUrl().replace(" ", "%20");
          }
          if (deliverableDissemination.getConfidentialUrl() != null
            && !deliverableDissemination.getConfidentialUrl().isEmpty()) {
            delivConfidentialUrl = deliverableDissemination.getConfidentialUrl().replace(" ", "%20");
            System.out.println("confidential " + delivConfidentialUrl);
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
                restrictedAccess = "Restricted Use AgreementOCS - Restricted access (if so, what are these periods?)";
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
        }
        // Intellectual Assets
        Boolean hasIntellectualAsset = false, isPantent = false, isPvp = false;
        String hasIntellectualAssetText = null, intellectualAssetApplicants = null, intellectualAssetType = null,
          intellectualAssetTitle = null, intellectualAssetFillingType = null, intellectualAssetPantentStatus = null,
          intellectualAssetPatentType = null, intellectualAssetPvpVarietyName = null, intellectualAssetPvpStatus = null,
          intellectualAssetPvpCountry = null, intellectualAssetPvpApplicationNumber = null,
          intellectualAssetPvpBreederCrop = null, intellectualAssetDateFilling = null,
          intellectualAssetDateRegistration = null, intellectualAssetDateExpiry = null,
          intellectualAssetAdditionalInformation = null, intellectualAssetLinkPublished = null,
          intellectualAssetCommunication = null;
        if (this.hasSpecificities(this.crpDeliverableIntellectualAsset())) {
          List<DeliverableIntellectualAsset> intellectualAssets =
            deliverable.getDeliverableIntellectualAssets().stream()
              .filter(c -> c.isActive() && c.getPhase().equals(this.getSelectedPhase())).collect(Collectors.toList());

          if (intellectualAssets != null && intellectualAssets.size() > 0) {
            DeliverableIntellectualAsset asset = intellectualAssets.get(0);
            if (asset.getHasPatentPvp() != null) {
              hasIntellectualAsset = asset.getHasPatentPvp();
              if (asset.getHasPatentPvp()) {
                hasIntellectualAssetText = "Yes";
                if (asset.getApplicant() != null && !asset.getApplicant().isEmpty()) {
                  intellectualAssetApplicants = asset.getApplicant();
                }
                if (asset.getType() != null && asset.getType().intValue() != -1) {
                  intellectualAssetType = DeliverableIntellectualAssetTypeEnum.getValue(asset.getType()).getType();

                  if (DeliverableIntellectualAssetTypeEnum.getValue(asset.getType())
                    .equals(DeliverableIntellectualAssetTypeEnum.Patent)) {
                    // Patent
                    isPantent = true;
                    if (asset.getFillingType() != null && asset.getFillingType().getId() != -1) {
                      intellectualAssetFillingType = asset.getFillingType().getName();
                    }
                    if (asset.getPatentStatus() != null && asset.getPatentStatus().getId() != -1) {
                      intellectualAssetPantentStatus = asset.getPatentStatus().getName();
                    }
                    if (asset.getPatentType() != null && asset.getPatentType().intValue() != -1) {
                      intellectualAssetPatentType =
                        DeliverableIntellectualAssetPantentTypeEnum.getValue(asset.getPatentType()).getType();
                    }
                  } else if (DeliverableIntellectualAssetTypeEnum.getValue(asset.getType())
                    .equals(DeliverableIntellectualAssetTypeEnum.PVP)) {
                    // PVP
                    isPvp = true;
                    if (asset.getVarietyName() != null && !asset.getVarietyName().isEmpty()) {
                      intellectualAssetPvpVarietyName = asset.getVarietyName();
                    }

                    if (asset.getStatus() != null && asset.getStatus() != -1) {
                      intellectualAssetPvpStatus = ProjectStatusEnum.getValue(asset.getStatus()).getStatus();
                    }
                    if (asset.getCountry() != null && !asset.getCountry().getIsoAlpha2().equals("-1")) {
                      intellectualAssetPvpCountry = asset.getCountry().getName();
                    }
                    if (asset.getAppRegNumber() != null) {
                      intellectualAssetPvpApplicationNumber = asset.getAppRegNumber() + "";
                    }
                    if (asset.getBreederCrop() != null && !asset.getBreederCrop().isEmpty()) {
                      intellectualAssetPvpBreederCrop = intellectualAssetPvpVarietyName = asset.getBreederCrop();
                    }
                  }
                }
                if (asset.getTitle() != null && !asset.getTitle().isEmpty()) {
                  intellectualAssetTitle = asset.getTitle();
                }
                if (asset.getDateFilling() != null) {
                  intellectualAssetDateFilling = formatter.format(asset.getDateFilling());
                }
                if (asset.getDateRegistration() != null) {
                  intellectualAssetDateRegistration = formatter.format(asset.getDateRegistration());
                }
                if (asset.getDateExpiry() != null) {
                  intellectualAssetDateExpiry = formatter.format(asset.getDateExpiry());
                }
                if (asset.getAdditionalInformation() != null && !asset.getAdditionalInformation().isEmpty()) {
                  intellectualAssetAdditionalInformation = asset.getAdditionalInformation();
                }
                if (asset.getLink() != null && !asset.getLink().isEmpty()) {
                  intellectualAssetLinkPublished = asset.getLink();
                }
                if (asset.getPublicCommunication() != null && !asset.getPublicCommunication().isEmpty()) {
                  intellectualAssetCommunication = asset.getPublicCommunication();
                }

              } else {
                hasIntellectualAssetText = "No";
              }
            }
          }
        }


        // Participants
        Boolean hasParticipants = false, isAcademicDegree = false;
        String hasParticipantsText = null, participantEvent = null, participantActivityType = null,
          participantAcademicDegree = null, participantTotalParticipants = null, participantFemales = null,
          participantType = null;

        List<DeliverableParticipant> deliverableParticipants = deliverable.getDeliverableParticipants().stream()
          .filter(c -> c.isActive() && c.getPhase().equals(this.getSelectedPhase())).collect(Collectors.toList());

        if (deliverableParticipants != null && deliverableParticipants.size() > 0) {
          DeliverableParticipant participant = deliverableParticipants.get(0);
          if (participant.getHasParticipants() != null) {
            hasParticipants = participant.getHasParticipants();
            if (participant.getHasParticipants()) {
              hasParticipantsText = "Yes";
              if (participant.getEventActivityName() != null && !participant.getEventActivityName().isEmpty()) {
                participantEvent = participant.getEventActivityName();
              }

              if (participant.getRepIndTypeActivity() != null && participant.getRepIndTypeActivity().getId() != -1) {
                participantActivityType = participant.getRepIndTypeActivity().getName();
                if (participant.getRepIndTypeActivity().getId()
                  .equals(this.getReportingIndTypeActivityAcademicDegree())) {
                  isAcademicDegree = true;
                  participantAcademicDegree = participant.getAcademicDegree();
                }
              }
              if (participant.getParticipants() != null) {
                participantTotalParticipants = participant.getParticipants() + "";
              }
              if (participant.getFemales() != null) {
                participantFemales = participant.getFemales() + "";
              }
              if (participant.getRepIndTypeParticipant() != null
                && participant.getRepIndTypeParticipant().getId() != -1) {
                participantType = participant.getRepIndTypeParticipant().getName();
              }

            } else {
              hasParticipantsText = "No";
            }
          }
        }

        // Metadata

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
        for (DeliverableUser deliverableUser : deliverable.getDeliverableUsers().stream()
          .filter(du -> du.isActive() && du.getPhase().equals(this.getSelectedPhase())).collect(Collectors.toList())) {
          creatorAuthors += "<br>● ";

          if (deliverableUser.getElementId() != null) {
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
          F = "#cccccc";
        } else {
          if (this.isF(deliverable.getId()) == true) {
            F = "#8ea786";
          } else {
            F = "#D32F2F";
          }
        }
        String A = "";
        if (this.isA(deliverable.getId()) == null) {
          A += "#cccccc";
        } else {
          if (this.isA(deliverable.getId()) == true) {
            A += "#8ea786";
          } else {
            A += "#D32F2F";
          }
        }
        String I = "";
        if (this.isI(deliverable.getId()) == null) {
          I += "#cccccc";
        } else {
          if (this.isI(deliverable.getId()) == true) {
            I += "#8ea786";
          } else {
            I += "#D32F2F";
          }
        }
        String R = "";
        if (this.isR(deliverable.getId()) == null) {
          R += "#cccccc";
        } else {
          if (this.isR(deliverable.getId()) == true) {
            R += "#8ea786";
          } else {
            R += "#D32F2F";
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

          List<DeliverableCrp> deliverableCrps = deliverable
            .getDeliverableCrps().stream().filter(dc -> dc.isActive() && dc.getPhase() != null
              && dc.getPhase().equals(this.getSelectedPhase()) && dc.getGlobalUnit() != null)
            .collect(Collectors.toList());
          List<DeliverableCrp> deliverableFlagships = deliverable
            .getDeliverableCrps().stream().filter(dc -> dc.isActive() && dc.getPhase() != null
              && dc.getPhase().equals(this.getSelectedPhase()) && dc.getCrpProgram() != null)
            .collect(Collectors.toList());
          // Crps
          for (DeliverableCrp deliverableCrp : deliverableCrps) {
            if (deliverableCrp.getGlobalUnit() != null) {
              flContrib += "<br> ● " + deliverableCrp.getGlobalUnit().getComposedName();
            }
          }
          // Flagships
          for (DeliverableCrp deliverableFlagship : deliverableFlagships) {
            if (deliverableFlagship.getCrpProgram() != null) {
              flContrib += "<br> ● " + deliverableFlagship.getCrpProgram().getComposedName();
            }
          }

          // Other partnert
          List<DeliverableUserPartnership> otherPartners = deliverable.getDeliverableUserPartnerships().stream()
            .filter(dp -> dp.isActive() && dp.getPhase().getId().equals(this.getActualPhase().getId())
              && dp.getDeliverablePartnerType().getId().equals(APConstants.DELIVERABLE_PARTNERSHIP_TYPE_OTHER))
            .collect(Collectors.toList());

          if (otherPartners != null) {
            for (DeliverableUserPartnership partner : otherPartners) {
              if (partner.getInstitution() != null) {
                otherPartner += partner.getInstitution().getComposedName();
              }
            }
          }
        }
        model.addRow(new Object[] {deliverable.getId(), deliverable.getDeliverableInfo().getTitle(), delivType,
          delivSubType, delivStatus, delivYear, keyOutput, leader, institution, fundingSources, crossCutting,
          delivNewYear, delivNewYearJustification, delivDisseminationChannel, delivDisseminationUrl, delivOpenAccess,
          delivLicense, titleMetadata, descriptionMetadata, dateMetadata, languageMetadata, countryMetadata,
          keywordsMetadata, citationMetadata, HandleMetadata, DOIMetadata, creatorAuthors, dataSharing,
          qualityAssurance, dataDictionary, tools, showFAIR, F, A, I, R, isDisseminated, disseminated, restrictedAccess,
          isRestricted, restrictedDate, isLastTwoRestricted, delivLicenseModifications, showDelivLicenseModifications,
          volume, issue, pages, journal, journalIndicators, acknowledge, flContrib, showPublication, showCompilance,
          deliv_description, hasIntellectualAsset, isPantent, isPvp, hasParticipants, isAcademicDegree,
          hasParticipantsText, participantEvent, participantActivityType, participantAcademicDegree,
          participantTotalParticipants, participantFemales, participantType, hasIntellectualAssetText,
          intellectualAssetApplicants, intellectualAssetType, intellectualAssetTitle, intellectualAssetFillingType,
          intellectualAssetPantentStatus, intellectualAssetPatentType, intellectualAssetPvpVarietyName,
          intellectualAssetPvpStatus, intellectualAssetPvpCountry, intellectualAssetPvpApplicationNumber,
          intellectualAssetPvpBreederCrop, intellectualAssetDateFilling, intellectualAssetDateRegistration,
          intellectualAssetDateExpiry, intellectualAssetAdditionalInformation, intellectualAssetLinkPublished,
          intellectualAssetCommunication, otherPartner, delivConfidentialUrl});
      }
    }
    return model;
  }

  private TypedTableModel getDeliverablesTableModel() {
    TypedTableModel model = new TypedTableModel(
      new String[] {"deliverable_id", "title", "deliv_type", "deliv_sub_type", "deliv_status", "deliv_year",
        "key_output", "leader", "institution", "funding_sources", "cross_cutting", "deliv_new_year", "isExtended"},
      new Class[] {Long.class, String.class, String.class, String.class, String.class, String.class, String.class,
        String.class, String.class, String.class, String.class, String.class, Boolean.class},
      0);
    if (!project.getDeliverables().isEmpty()) {
      for (Deliverable deliverable : project.getDeliverables().stream()
        .sorted((d1, d2) -> Long.compare(d1.getId(), d2.getId()))
        .filter(d -> d.isActive() && d.getDeliverableInfo(this.getSelectedPhase()) != null
          && ((d.getDeliverableInfo().getStatus() == null && d.getDeliverableInfo().getYear() == this.getSelectedYear())
            || (d.getDeliverableInfo().getStatus() != null
              && d.getDeliverableInfo().getStatus().intValue() == Integer
                .parseInt(ProjectStatusEnum.Extended.getStatusId())
              && d.getDeliverableInfo().getNewExpectedYear() != null
              && d.getDeliverableInfo().getNewExpectedYear() == this.getSelectedYear())
            || (d.getDeliverableInfo().getStatus() != null && d.getDeliverableInfo().getYear() == this.getSelectedYear()
              && d.getDeliverableInfo().getStatus().intValue() == Integer
                .parseInt(ProjectStatusEnum.Ongoing.getStatusId()))
            || (d.getDeliverableInfo().getStatus() != null
              && d.getDeliverableInfo().getStatus().intValue() == Integer
                .parseInt(ProjectStatusEnum.Complete.getStatusId())
              && ((d.getDeliverableInfo().getNewExpectedYear() != null
                && d.getDeliverableInfo().getNewExpectedYear() == this.getSelectedYear())
                || (d.getDeliverableInfo().getNewExpectedYear() == null
                  && d.getDeliverableInfo().getYear() == this.getSelectedYear())))))
        .collect(Collectors.toList())) {
        String delivType = null;
        String delivSubType = null;
        String delivStatus =
          deliverable.getDeliverableInfo(this.getSelectedPhase()).getStatusName(this.getSelectedPhase());
        String delivYear = null;
        String delivNewYear = null;
        String keyOutput = "";
        String leader = null;
        String institution = null;
        String fundingSources = "";
        Boolean isExtended = false;
        if (deliverable.getDeliverableInfo(this.getSelectedPhase()).getDeliverableType() != null) {
          delivSubType = deliverable.getDeliverableInfo(this.getSelectedPhase()).getDeliverableType().getName();
          if (deliverable.getDeliverableInfo(this.getSelectedPhase()).getDeliverableType()
            .getDeliverableCategory() != null) {
            delivType = deliverable.getDeliverableInfo(this.getSelectedPhase()).getDeliverableType()
              .getDeliverableCategory().getName();
          }
        }
        if (delivStatus.equals("")) {
          delivStatus = null;
        }
        if (deliverable.getDeliverableInfo(this.getSelectedPhase()).getYear() != 0) {
          delivYear = "" + deliverable.getDeliverableInfo(this.getSelectedPhase()).getYear();
        }
        if (deliverable.getDeliverableInfo(this.getSelectedPhase()).getStatus().intValue() == Integer
          .parseInt(ProjectStatusEnum.Extended.getStatusId())) {
          delivNewYear = "" + deliverable.getDeliverableInfo(this.getSelectedPhase()).getNewExpectedYear();
          isExtended = true;
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
        List<DeliverableUserPartnership> deliverablePartnershipResponsibles =
          deliverable.getDeliverableUserPartnerships().stream()
            .filter(dp -> dp.isActive() && dp.getPhase().getId().equals(this.getActualPhase().getId())
              && dp.getDeliverablePartnerType().getId().equals(APConstants.DELIVERABLE_PARTNERSHIP_TYPE_RESPONSIBLE))
            .collect(Collectors.toList());

        if (deliverablePartnershipResponsibles != null && deliverablePartnershipResponsibles.size() > 0) {
          if (deliverablePartnershipResponsibles.size() > 1) {
            LOG.warn("There are more than 1 deliverable responsibles for D" + deliverable.getId() + " "
              + this.getSelectedPhase().toString());
          }
          DeliverableUserPartnership responisble = deliverablePartnershipResponsibles.get(0);

          if (responisble != null) {
            if (responisble.getDeliverableUserPartnershipPersons() != null) {

              DeliverableUserPartnershipPerson responsibleppp = new DeliverableUserPartnershipPerson();
              List<DeliverableUserPartnershipPerson> persons = responisble.getDeliverableUserPartnershipPersons()
                .stream().filter(dp -> dp.isActive()).collect(Collectors.toList());
              if (persons != null && persons.size() > 0) {
                responsibleppp = persons.get(0);
              }

              if (responsibleppp != null && responsibleppp.getUser() != null
                && responsibleppp.getUser().getComposedName() != null) {
                leader = responsibleppp.getUser().getComposedName() + "<br>&lt;" + responsibleppp.getUser().getEmail()
                  + "&gt;";
              }

              if (responisble.getInstitution() != null) {
                institution = responisble.getInstitution().getComposedName();
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
        DeliverableCrossCuttingMarker deliverableCrossCuttingMarkerGender = deliverableCrossCuttingMarkerManager
          .getDeliverableCrossCuttingMarkerId(deliverable.getId(), 1, this.getSelectedPhase().getId());
        DeliverableCrossCuttingMarker deliverableCrossCuttingMarkerYouth = deliverableCrossCuttingMarkerManager
          .getDeliverableCrossCuttingMarkerId(deliverable.getId(), 2, this.getSelectedPhase().getId());
        DeliverableCrossCuttingMarker deliverableCrossCuttingMarkerCapDev = deliverableCrossCuttingMarkerManager
          .getDeliverableCrossCuttingMarkerId(deliverable.getId(), 3, this.getSelectedPhase().getId());
        DeliverableCrossCuttingMarker deliverableCrossCuttingMarkerClimate = deliverableCrossCuttingMarkerManager
          .getDeliverableCrossCuttingMarkerId(deliverable.getId(), 4, this.getSelectedPhase().getId());

        if (deliverableCrossCuttingMarkerGender != null) {
          if (deliverableCrossCuttingMarkerGender.getRepIndGenderYouthFocusLevel() != null) {
            crossCutting += "&nbsp;&nbsp;&nbsp;&nbsp;● Gender ("
              + deliverableCrossCuttingMarkerGender.getRepIndGenderYouthFocusLevel().getPowbName() + ")<br>";
          } else {
            crossCutting += "&nbsp;&nbsp;&nbsp;&nbsp;● Gender <br>";
          }
        }
        if (deliverableCrossCuttingMarkerYouth != null) {
          if (deliverableCrossCuttingMarkerYouth.getRepIndGenderYouthFocusLevel() != null) {
            crossCutting += "&nbsp;&nbsp;&nbsp;&nbsp;● Youth ("
              + deliverableCrossCuttingMarkerYouth.getRepIndGenderYouthFocusLevel().getPowbName() + ")<br>";
          } else {
            crossCutting += "&nbsp;&nbsp;&nbsp;&nbsp;● Youth <br>";
          }
        }
        if (deliverableCrossCuttingMarkerCapDev != null) {
          if (deliverableCrossCuttingMarkerCapDev.getRepIndGenderYouthFocusLevel() != null) {
            crossCutting += "&nbsp;&nbsp;&nbsp;&nbsp;● Capacity Development ("
              + deliverableCrossCuttingMarkerCapDev.getRepIndGenderYouthFocusLevel().getPowbName() + ")<br>";
          } else {
            crossCutting += "&nbsp;&nbsp;&nbsp;&nbsp;● Capacity Development <br>";
          }
        }
        if (deliverableCrossCuttingMarkerClimate != null) {
          if (deliverableCrossCuttingMarkerClimate.getRepIndGenderYouthFocusLevel() != null) {
            crossCutting += "&nbsp;&nbsp;&nbsp;&nbsp;● Climate Change ("
              + deliverableCrossCuttingMarkerClimate.getRepIndGenderYouthFocusLevel().getPowbName() + ")<br>";
          } else {
            crossCutting += "&nbsp;&nbsp;&nbsp;&nbsp;● Climate Change <br>";
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
          delivYear, keyOutput, leader, institution, fundingSources, crossCutting, delivNewYear, isExtended});
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


      // TODO change when the ProjectCoas Duplication will has been fix it
      List<ProjectClusterActivity> coAsPrev = new ArrayList<>();

      List<ProjectClusterActivity> coAs = new ArrayList<>();
      coAsPrev = project.getProjectClusterActivities().stream()
        .filter(c -> c.isActive() && c.getPhase() != null && c.getPhase().equals(this.getSelectedPhase()))
        .collect(Collectors.toList());

      for (ProjectClusterActivity projectClusterActivity : coAsPrev) {
        if (coAs.isEmpty()) {
          coAs.add(projectClusterActivity);
        } else {
          boolean duplicated = false;
          for (ProjectClusterActivity projectCoas : coAs) {
            if (projectCoas.getCrpClusterOfActivity().getId()
              .equals(projectClusterActivity.getCrpClusterOfActivity().getId())) {
              duplicated = true;
              break;
            }
          }

          if (!duplicated) {
            coAs.add(projectClusterActivity);
          }
        }


      }

      for (ProjectClusterActivity projectClusterActivity : coAs) {
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


      // Get type from funding sources
      String type = "";
      List<String> typeList = new ArrayList<String>();
      for (ProjectBudget projectBudget : project
        .getProjectBudgets().stream().filter(pb -> pb.isActive() && pb.getYear() == this.getSelectedYear()
          && pb.getFundingSource() != null && pb.getPhase() != null && pb.getPhase().equals(this.getSelectedPhase()))
        .collect(Collectors.toList())) {
        if (projectBudget.getFundingSource().getFundingSourceInfo(this.getSelectedPhase()) != null
          && projectBudget.getFundingSource().getFundingSourceInfo(this.getSelectedPhase()).getBudgetType() != null
          && projectBudget.getFundingSource().getFundingSourceInfo(this.getSelectedPhase()).getBudgetType()
            .getName() != null) {
          typeList.add(
            projectBudget.getFundingSource().getFundingSourceInfo(this.getSelectedPhase()).getBudgetType().getName());
        }
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
      String crossCutting = "";
      if (projectInfo.getCrossCuttingNa() != null) {
        if (projectInfo.getCrossCuttingNa() == true) {
          crossCutting += "● N/A <br>";
        }
      }

      if (projectInfo.getCrossCuttingCapacity() != null) {
        if (projectInfo.getCrossCuttingCapacity() == true) {
          crossCutting += "● Capacity Development <br>";
        }
      }

      if (crossCutting.isEmpty()) {
        crossCutting = null;
      }
      String mlText = null, mlContactText = null;
      mlText = this.getText("project.liaisonInstitution");
      mlContactText = this.getText("project.liaisonUser");
      model.addRow(new Object[] {title, startDate, endDate, ml, mlContact, type, status, orgLeader, leader, summary,
        this.getSelectedCycle(), "", crossCutting, hasRegions, mlText, mlContactText});
    }
    return model;
  }

  private TypedTableModel getExpectedStudiesTableModel() {
    TypedTableModel model = new TypedTableModel(
      new String[] {"id", "title", "expectedStudiesYear", "expectedStudiesStatus", "expectedStudiesType",
        "expectedStudiesCommissioningStudy", "expectedStudiesSubIdo", "expectedStudiesSRF", "geographicScope", "region",
        "countries", "scopeComments", "studyProjects", "expectedStudiesComments", "isRegional", "isNational",
        "hasMultipleProjects"},
      new Class[] {Long.class, String.class, String.class, String.class, String.class, String.class, String.class,
        String.class, String.class, String.class, String.class, String.class, String.class, String.class, Boolean.class,
        Boolean.class, Boolean.class},
      0);
    Set<ProjectExpectedStudy> myStudies = new HashSet<>();

    List<ProjectExpectedStudy> projectExpectedStudies = project.getProjectExpectedStudies().stream()
      .filter(p -> p.isActive() && p.getProjectExpectedStudyInfo(this.getSelectedPhase()) != null
        && p.getProjectExpectedStudyInfo(this.getSelectedPhase()).getYear() != null
        && p.getProjectExpectedStudyInfo(this.getSelectedPhase()).getYear().equals(this.getSelectedYear()))
      .collect(Collectors.toList());
    if (projectExpectedStudies != null && !projectExpectedStudies.isEmpty()) {
      myStudies.addAll(projectExpectedStudies);
    }
    // Shared Studies
    List<ExpectedStudyProject> sharedStudies = project.getExpectedStudyProjects().stream()
      .filter(c -> c.isActive() && c.getPhase() != null && c.getPhase().equals(this.getSelectedPhase())
        && c.getProjectExpectedStudy().getProjectExpectedStudyInfo(this.getSelectedPhase()) != null
        && c.getProjectExpectedStudy().getProjectExpectedStudyInfo(this.getSelectedPhase()).getYear() != null
        && c.getProjectExpectedStudy().getProjectExpectedStudyInfo(this.getSelectedPhase()).getYear() == this
          .getCurrentCycleYear())
      .collect(Collectors.toList());

    if (sharedStudies != null && !sharedStudies.isEmpty()) {
      for (ExpectedStudyProject expectedStudyProject : sharedStudies) {
        myStudies.add(expectedStudyProject.getProjectExpectedStudy());
      }
    }

    if (myStudies != null && !myStudies.isEmpty()) {
      for (ProjectExpectedStudy projectExpectedStudy : myStudies.stream()
        .sorted((s1, s2) -> s1.getId().compareTo(s2.getId())).collect(Collectors.toList())) {

        String id = "", title = "", expectedStudiesYear = "", expectedStudiesStatus = "", expectedStudiesType = "",
          expectedStudiesCommissioningStudy = "", expectedStudiesSubIdo = "", expectedStudiesSRF = "",
          geographicScope = "", region = "", countries = "", scopeComments = "", studyProjects = "",
          expectedStudiesComments = "";

        Boolean isRegional = false, isNational = false, hasMultipleProjects = false;

        id = projectExpectedStudy.getId() + "";
        ProjectExpectedStudyInfo projectExpectedStudyInfo =
          projectExpectedStudy.getProjectExpectedStudyInfo(this.getSelectedPhase());
        if (projectExpectedStudyInfo.getTitle() != null) {
          title = projectExpectedStudyInfo.getTitle();
        }
        if (projectExpectedStudyInfo.getYear() != null) {
          expectedStudiesYear = projectExpectedStudyInfo.getYear() + "";
        }
        if (projectExpectedStudyInfo.getStatus() != null) {
          expectedStudiesStatus = projectExpectedStudyInfo.getStatusName();
        }
        if (projectExpectedStudyInfo.getStudyType() != null) {
          expectedStudiesType = projectExpectedStudyInfo.getStudyType().getName();
        }
        if (projectExpectedStudyInfo.getCommissioningStudy() != null) {
          expectedStudiesCommissioningStudy = projectExpectedStudyInfo.getCommissioningStudy();
        }

        List<ProjectExpectedStudySubIdo> projectExpectedStudySubIdos =
          projectExpectedStudy.getProjectExpectedStudySubIdos().stream()
            .filter(s -> s.getPhase() != null && s.getPhase().equals(this.getSelectedPhase()))
            .collect(Collectors.toList());
        if (projectExpectedStudySubIdos != null && !projectExpectedStudySubIdos.isEmpty()) {

          Set<String> studySubIdos = new HashSet<>();
          for (ProjectExpectedStudySubIdo projectExpectedStudySubIdo : projectExpectedStudySubIdos) {
            studySubIdos.add(projectExpectedStudySubIdo.getSrfSubIdo().getDescription());
          }
          if (studySubIdos != null && !studySubIdos.isEmpty()) {
            expectedStudiesSubIdo = String.join(", ", studySubIdos);
          }
        }

        List<ProjectExpectedStudySrfTarget> projectExpectedStudySrfTargets =
          projectExpectedStudy.getProjectExpectedStudySrfTargets().stream()
            .filter(s -> s.getPhase() != null && s.getPhase().equals(this.getSelectedPhase()))
            .collect(Collectors.toList());
        if (projectExpectedStudySrfTargets != null && !projectExpectedStudySrfTargets.isEmpty()) {

          Set<String> studySrfTargets = new HashSet<>();
          for (ProjectExpectedStudySrfTarget pprojectExpectedStudySrfTarget : projectExpectedStudySrfTargets) {
            studySrfTargets.add(pprojectExpectedStudySrfTarget.getSrfSloIndicator().getTitle());
          }
          if (studySrfTargets != null && !studySrfTargets.isEmpty()) {
            expectedStudiesSRF = String.join(", ", studySrfTargets);
          }
        }

        /*
         * Geographic Scope
         */
        if (projectExpectedStudyInfo.getRepIndGeographicScope() != null) {
          geographicScope = projectExpectedStudyInfo.getRepIndGeographicScope().getName();
          // Regional
          if (projectExpectedStudyInfo.getRepIndGeographicScope().getId()
            .equals(this.getReportingIndGeographicScopeRegional())) {

            isRegional = true;
            List<ProjectExpectedStudyCountry> projectExpectedStudyRegions = projectExpectedStudyCountryManager
              .getProjectExpectedStudyCountrybyPhase(projectExpectedStudy.getId(), this.getSelectedPhase().getId())
              .stream().filter(le -> le.isActive() && le.getLocElement().getLocElementType().getId() == 1)
              .collect(Collectors.toList());

            if (projectExpectedStudyRegions != null && projectExpectedStudyRegions.size() > 0) {
              Set<String> regionsSet = new HashSet<>();
              for (ProjectExpectedStudyCountry projectExpectedStudyCountry : projectExpectedStudyRegions) {
                regionsSet.add(projectExpectedStudyCountry.getLocElement().getName());
              }
              region = String.join(", ", regionsSet);
            }

          }

          // Country
          if (!projectExpectedStudyInfo.getRepIndGeographicScope().getId()
            .equals(this.getReportingIndGeographicScopeGlobal())
            && !projectExpectedStudyInfo.getRepIndGeographicScope().getId()
              .equals(this.getReportingIndGeographicScopeRegional())) {
            isNational = true;
            List<ProjectExpectedStudyCountry> deliverableCountries = projectExpectedStudyCountryManager
              .getProjectExpectedStudyCountrybyPhase(projectExpectedStudy.getId(), this.getSelectedPhase().getId())
              .stream().filter(le -> le.isActive() && le.getLocElement().getLocElementType().getId() == 2)
              .collect(Collectors.toList());
            if (deliverableCountries != null && deliverableCountries.size() > 0) {
              Set<String> countriesSet = new HashSet<>();
              for (ProjectExpectedStudyCountry deliverableCountry : deliverableCountries) {
                countriesSet.add(deliverableCountry.getLocElement().getName());
              }
              countries = String.join(", ", countriesSet);
            }
          }
        }
        if (geographicScope.isEmpty()) {
          geographicScope = null;
        }
        if (region.isEmpty()) {
          region = null;
        }
        if (countries.isEmpty()) {
          countries = null;
        }


        // Projects excluding projectExpectedStudy project
        List<ExpectedStudyProject> studyProjectList = projectExpectedStudy.getExpectedStudyProjects().stream()
          .filter(e -> e.isActive() && e.getPhase() != null && e.getPhase().equals(this.getSelectedPhase())
            && e.getProject() != projectExpectedStudy.getProject())
          .sorted((s1, s2) -> s1.getProject().getId().compareTo(s2.getProject().getId())).collect(Collectors.toList());
        LinkedHashSet<String> studyProjectSet = new LinkedHashSet<>();
        if (studyProjectList != null && studyProjectList.size() > 0) {
          for (ExpectedStudyProject studyProject : studyProjectList) {
            studyProjectSet.add("P" + studyProject.getProject().getId());
          }
        }

        if (studyProjectSet != null && !studyProjectSet.isEmpty()) {
          studyProjects = String.join(", ", studyProjectSet);
          hasMultipleProjects = true;
        }

        model.addRow(new Object[] {id, title, expectedStudiesYear, expectedStudiesStatus, expectedStudiesType,
          expectedStudiesCommissioningStudy, expectedStudiesSubIdo, expectedStudiesSRF, geographicScope, region,
          countries, scopeComments, studyProjects, expectedStudiesComments, isRegional, isNational,
          hasMultipleProjects});
      }
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
    // Get The Crp/Center/Platform where the project was created
    GlobalUnitProject globalUnitProject = project.getGlobalUnitProjects().stream()
      .filter(gu -> gu.isActive() && gu.isOrigin()).collect(Collectors.toList()).get(0);

    StringBuffer fileName = new StringBuffer();
    fileName.append("FullProjectReportSummary-");
    fileName.append(globalUnitProject.getGlobalUnit().getAcronym() + "-");
    fileName.append("P" + projectID + "-");
    fileName.append(this.getSelectedCycle() + "-");
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

  private TypedTableModel getFlagshipOutcomesTableModel() {
    TypedTableModel model = new TypedTableModel(
      new String[] {"exp_value", "narrative", "outcome_id", "out_fl", "out_year", "out_value", "out_statement",
        "out_unit", "cross_cutting", "exp_unit", "ach_unit", "ach_value", "ach_narrative", "communications",
        "showCommunications"},
      new Class[] {Long.class, String.class, Long.class, String.class, String.class, String.class, String.class,
        String.class, String.class, String.class, String.class, String.class, String.class, String.class,
        Boolean.class},
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
        String ach_unit = null, ach_value = null, ach_narrative = null;
        String communications = null;
        if (projectOutcome.getCrpProgramOutcome() != null) {
          outYear = "" + projectOutcome.getCrpProgramOutcome().getYear();
          outValue = "" + projectOutcome.getCrpProgramOutcome().getValue();
          if (this.hasSpecificities(APConstants.CRP_IP_OUTCOME_INDICATOR)) {
            outStatement = projectOutcome.getCrpProgramOutcome().getDescription();
            if (projectOutcome.getCrpProgramOutcome().getIndicator() != null
              && !projectOutcome.getCrpProgramOutcome().getIndicator().isEmpty()) {
              outStatement += "<br><b>Indicator: </b>" + projectOutcome.getCrpProgramOutcome().getIndicator();
            }
          } else {
            outStatement = projectOutcome.getCrpProgramOutcome().getDescription();
          }

          if (projectOutcome.getCrpProgramOutcome().getSrfTargetUnit() != null) {
            outUnit = projectOutcome.getCrpProgramOutcome().getSrfTargetUnit().getName();
          }
          if (projectOutcome.getCrpProgramOutcome().getCrpProgram() != null) {
            outFl = projectOutcome.getCrpProgramOutcome().getCrpProgram().getAcronym();
          }
        }
        expValue = projectOutcome.getExpectedValue() + "";
        if (projectOutcome.getAchievedValue() != null) {
          ach_value = projectOutcome.getAchievedValue() + "";
        }

        if (outUnit == null) {
          if (projectOutcome.getExpectedUnit() != null) {
            expUnit = projectOutcome.getExpectedUnit().getName();
          }
          if (projectOutcome.getAchievedUnit() != null) {
            ach_unit = projectOutcome.getAchievedUnit().getName();
          }
        } else {
          expUnit = outUnit;
          ach_unit = outUnit;
        }
        ach_narrative = projectOutcome.getNarrativeAchieved();

        if (crossCutting.isEmpty()) {
          crossCutting = null;
        }
        if (this.hasSpecificities(APConstants.CRP_SHOW_PROJECT_OUTCOME_COMMUNICATIONS)) {
          List<ProjectCommunication> projectCommunications = projectOutcome.getProjectCommunications().stream()
            .filter(pc -> pc.isActive() && pc.getYear() == this.getSelectedYear()).collect(Collectors.toList());
          if (projectCommunications != null && projectCommunications.size() > 0) {
            communications = projectCommunications.get(0).getCommunication();
            if (projectCommunications.size() > 1) {
              LOG.warn("There is more than one Project Communication for P" + this.getProjectID() + "Project Outcome "
                + projectOutcome.getId());
            }
          }
        }

        model.addRow(new Object[] {expValue, projectOutcome.getNarrativeTarget(), projectOutcome.getId(), outFl,
          outYear, outValue, outStatement, outUnit, crossCutting, expUnit, ach_unit, ach_value, ach_narrative,
          communications, this.hasSpecificities(APConstants.CRP_SHOW_PROJECT_OUTCOME_COMMUNICATIONS)});
      }
    }
    return model;
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

  public String getHighlightsImagesUrl(String projectId) {
    return config.getDownloadURL() + "/" + this.getHighlightsImagesUrlPath(projectId).replace('\\', '/');
  }

  public String getHighlightsImagesUrlPath() {
    return config.getProjectsBaseFolder(this.getCrpSession()) + File.separator + project.getId() + File.separator
      + "hightlightsImage" + File.separator;
  }

  public String getHighlightsImagesUrlPath(long projectID) {
    return config.getProjectsBaseFolder(this.getCrpSession()) + File.separator + projectID + File.separator
      + "hightlightsImage" + File.separator;
  }

  public String getHighlightsImagesUrlPath(String projectId) {
    return config.getProjectsBaseFolder(this.getCrpSession()) + File.separator + projectId + File.separator
      + "hightlightsImage" + File.separator;
  }

  private String getHightlightImagePath(long projectID) {
    return config.getUploadsBaseFolder() + File.separator + this.getHighlightsImagesUrlPath(projectID) + File.separator;
  }

  private TypedTableModel getInnovationsTableModel() {
    TypedTableModel model = new TypedTableModel(
      new String[] {"id", "isRegional", "isNational", "isStage4", "title", "narrative", "phaseResearch",
        "stageInnovation", "innovationType", "contributionOfCrp", "degreeInnovation", "geographicScope", "region",
        "countries", "organizations", "projectExpectedStudy", "descriptionStage", "evidenceLink", "deliverables",
        "crps", "genderFocusLevel", "genderExplaniation", "youthFocusLevel", "youthExplaniation", "leadOrganization",
        "contributingOrganizations"},
      new Class[] {Long.class, Boolean.class, Boolean.class, Boolean.class, String.class, String.class, String.class,
        String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class,
        String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class,
        String.class, String.class, String.class},
      0);
    URLShortener urlShortener = new URLShortener();

    List<ProjectInnovation> projectInnovations = project.getProjectInnovations().stream()
      .filter(p -> p.isActive() && p.getProjectInnovationInfo(this.getSelectedPhase()) != null)
      .sorted((i1, i2) -> i1.getId().compareTo(i2.getId())).collect(Collectors.toList());

    /*
     * Update 4/25/2019 Adding Shared Project Innovation in the lists.
     */
    List<ProjectInnovationShared> innovationShareds = new ArrayList<>(project.getProjectInnovationShareds().stream()
      .filter(px -> px.isActive() && px.getPhase().getId() == this.getActualPhase().getId()
        && px.getProjectInnovation().isActive()
        && px.getProjectInnovation().getProjectInnovationInfo(this.getActualPhase()) != null)
      .collect(Collectors.toList()));
    if (innovationShareds != null && innovationShareds.size() > 0) {
      for (ProjectInnovationShared innovationShared : innovationShareds) {
        if (!projectInnovations.contains(innovationShared.getProjectInnovation())) {
          if (innovationShared.getProjectInnovation().getProjectInnovationInfo(this.getActualPhase()) != null) {
            projectInnovations.add(innovationShared.getProjectInnovation());
          }
        }
      }
    }


    if (projectInnovations != null && !projectInnovations.isEmpty()) {
      for (ProjectInnovation projectInnovation : projectInnovations) {
        ProjectInnovationInfo innovationInfo = projectInnovation.getProjectInnovationInfo();
        Long id = null;
        String title = null, narrative = null, phaseResearch = null, stageInnovation = null, innovationType = null,
          contributionOfCrp = null, degreeInnovation = null, geographicScope = null, region = null, countries = null,
          organizations = null, projectExpectedStudy = null, descriptionStage = null, evidenceLink = null,
          deliverables = null, crps = null, genderFocusLevel = null, genderExplaniation = null, youthFocusLevel = null,
          youthExplaniation = null, leadOrganization = null, contributingOrganizations = null;
        Boolean isRegional = false, isNational = false, isStage4 = false;
        // Id
        id = projectInnovation.getId();
        // Title
        if (innovationInfo.getTitle() != null && !innovationInfo.getTitle().trim().isEmpty()) {
          title = innovationInfo.getTitle();
        }
        // Narrative
        if (innovationInfo.getNarrative() != null && !innovationInfo.getNarrative().trim().isEmpty()) {
          narrative = innovationInfo.getNarrative();
        }
        // Phase Research
        if (innovationInfo.getRepIndPhaseResearchPartnership() != null) {
          phaseResearch = innovationInfo.getRepIndPhaseResearchPartnership().getName();
        }
        // Stage
        if (innovationInfo.getRepIndStageInnovation() != null) {
          stageInnovation = innovationInfo.getRepIndStageInnovation().getName();
          if (innovationInfo.getRepIndStageInnovation().getId().equals(APConstants.REP_IND_STAGE_INNOVATION_STAGE4)) {
            isStage4 = true;
            // Organizations
            List<ProjectInnovationOrganization> projectInnovationOrganizations =
              projectInnovation.getProjectInnovationOrganizations().stream()
                .filter(o -> o.isActive() && o.getPhase() != null && o.getPhase().equals(this.getSelectedPhase()))
                .collect(Collectors.toList());
            if (projectInnovationOrganizations != null && projectInnovationOrganizations.size() > 0) {
              Set<String> organizationSet = new HashSet<>();
              for (ProjectInnovationOrganization projectInnovationOrganization : projectInnovationOrganizations) {
                organizationSet.add("<br>&nbsp;&nbsp;&nbsp;&nbsp; ● "
                  + projectInnovationOrganization.getRepIndOrganizationType().getName());
              }
              organizations = String.join("", organizationSet);
            }

            // Studies
            if (innovationInfo.getProjectExpectedStudy() != null) {
              projectExpectedStudy = innovationInfo.getProjectExpectedStudy().getComposedName();
            }
          }
        }
        // Type
        if (innovationInfo.getRepIndInnovationType() != null) {
          innovationType = innovationInfo.getRepIndInnovationType().getName();
        }

        // Degree
        if (innovationInfo.getRepIndDegreeInnovation() != null) {
          degreeInnovation = innovationInfo.getRepIndDegreeInnovation().getName();
        }


        // Validate Geographic Scope
        boolean haveRegions = false;
        boolean haveCountries = false;

        if (projectInnovation.getProjectInnovationGeographicScopes() == null
          || projectInnovation.getProjectInnovationGeographicScopes().isEmpty()) {
          geographicScope = "<Not Defined>";
        } else {
          Set<String> geographicSet = new HashSet<>();
          for (ProjectInnovationGeographicScope innovationGeographicScope : projectInnovation
            .getProjectInnovationGeographicScopes().stream()
            .filter(g -> g.isActive() && g.getPhase().getId().equals(this.getSelectedPhase().getId()))
            .collect(Collectors.toList())) {

            geographicSet
              .add("<br>&nbsp;&nbsp;&nbsp;&nbsp; ● " + innovationGeographicScope.getRepIndGeographicScope().getName());

            if (innovationGeographicScope.getRepIndGeographicScope().getId() == 2) {
              haveRegions = true;
            }
            if (innovationGeographicScope.getRepIndGeographicScope().getId() != 1
              && innovationGeographicScope.getRepIndGeographicScope().getId() != 2) {
              haveCountries = true;
            }
          }
          geographicScope = String.join("", geographicSet);
        }


        if (haveRegions) {
          isRegional = true;
          List<ProjectInnovationRegion> regions = new ArrayList<>(projectInnovation.getProjectInnovationRegions()
            .stream().filter(r -> r.isActive() && r.getPhase().getId().equals(this.getSelectedPhase().getId()))
            .collect(Collectors.toList()));

          if (regions != null && !regions.isEmpty()) {
            Set<String> regionsSet = new HashSet<>();
            for (ProjectInnovationRegion geoRegion : regions) {
              regionsSet.add("<br>&nbsp;&nbsp;&nbsp;&nbsp; ● " + geoRegion.getLocElement().getName());
            }
            region = String.join("", regionsSet);
          } else {
            region = "<Not Defined>";
          }

        } else {
          region = "<Not Aplicable>";
        }

        if (haveCountries) {
          isNational = true;
          List<ProjectInnovationCountry> geoCountries =
            new ArrayList<>(projectInnovation.getProjectInnovationCountries().stream()
              .filter(r -> r.isActive() && r.getPhase().getId().equals(this.getSelectedPhase().getId()))
              .collect(Collectors.toList()));

          if (geoCountries != null && !geoCountries.isEmpty()) {
            Set<String> countriesSet = new HashSet<>();
            for (ProjectInnovationCountry geoCountry : geoCountries) {
              countriesSet.add("<br>&nbsp;&nbsp;&nbsp;&nbsp; ● " + geoCountry.getLocElement().getName());
            }
            countries = String.join("", countriesSet);
          } else {
            countries = "<Not Defined>";
          }

        } else {
          countries = "<Not Aplicable>";
        }


        // Description
        if (innovationInfo.getDescriptionStage() != null && !innovationInfo.getDescriptionStage().trim().isEmpty()) {
          descriptionStage = innovationInfo.getDescriptionStage();
        }
        // Lead Organization
        if (innovationInfo.getLeadOrganization() != null
          && innovationInfo.getLeadOrganization().getComposedName() != null) {
          leadOrganization = innovationInfo.getLeadOrganization().getComposedName();
        }
        // Contributing Organizations
        List<ProjectInnovationContributingOrganization> contributingOrganizationsList =
          new ArrayList<ProjectInnovationContributingOrganization>(projectInnovationContributingOrganizationManager
            .findAll().stream().filter(p -> p.getProjectInnovation().getId().equals(projectInnovation.getId())
              && p.getPhase().getId().equals(this.getSelectedPhase().getId()))
            .collect(Collectors.toList()));
        if (contributingOrganizationsList != null && !contributingOrganizationsList.isEmpty()) {
          Set<String> contributingSet = new HashSet<>();
          for (ProjectInnovationContributingOrganization contributingOrganization : contributingOrganizationsList) {
            if (contributingOrganization.getInstitution() != null) {
              contributingSet
                .add("<br>&nbsp;&nbsp;&nbsp;&nbsp; ● " + contributingOrganization.getInstitution().getComposedName());
            }
          }
          contributingOrganizations = String.join("", contributingSet);
        }

        // Evidence Link
        if (innovationInfo.getEvidenceLink() != null && !innovationInfo.getEvidenceLink().trim().isEmpty()) {

          /*
           * Get short url calling tinyURL service
           */
          evidenceLink = urlShortener.getShortUrlService(innovationInfo.getEvidenceLink());
        }
        // Deliverables
        List<ProjectInnovationDeliverable> projectInnovationDeliverables =
          projectInnovation.getProjectInnovationDeliverables().stream()
            .filter(o -> o.isActive() && o.getPhase() != null && o.getPhase().equals(this.getSelectedPhase()))
            .collect(Collectors.toList());
        if (projectInnovationDeliverables != null && projectInnovationDeliverables.size() > 0) {
          Set<String> deliverablesSet = new HashSet<>();
          for (ProjectInnovationDeliverable projectInnovationDeliverable : projectInnovationDeliverables) {
            deliverablesSet
              .add("<br>&nbsp;&nbsp;&nbsp;&nbsp; ● " + projectInnovationDeliverable.getDeliverable().getComposedName());
          }
          deliverables = String.join("", deliverablesSet);
        }
        // Contributions CRPS/Platforms
        List<ProjectInnovationCrp> projectInnovationCrps = projectInnovation.getProjectInnovationCrps().stream()
          .filter(o -> o.isActive() && o.getPhase() != null && o.getPhase().equals(this.getSelectedPhase()))
          .collect(Collectors.toList());
        if (projectInnovationCrps != null && projectInnovationCrps.size() > 0) {
          Set<String> crpsSet = new HashSet<>();
          for (ProjectInnovationCrp projectInnovationCrp : projectInnovationCrps) {
            crpsSet.add("<br>&nbsp;&nbsp;&nbsp;&nbsp; ● " + projectInnovationCrp.getGlobalUnit().getComposedName());
          }
          crps = String.join("", crpsSet);
        }

        // Gender Relevance
        if (innovationInfo.getGenderFocusLevel() != null) {
          genderFocusLevel = innovationInfo.getGenderFocusLevel().getName();
        }
        if (innovationInfo.getGenderExplaniation() != null
          && !innovationInfo.getGenderExplaniation().trim().isEmpty()) {
          genderExplaniation = innovationInfo.getGenderExplaniation();
        }

        // Youth Relevance
        if (innovationInfo.getYouthFocusLevel() != null) {
          youthFocusLevel = innovationInfo.getYouthFocusLevel().getName();
        }
        if (innovationInfo.getYouthExplaniation() != null && !innovationInfo.getYouthExplaniation().trim().isEmpty()) {
          youthExplaniation = innovationInfo.getYouthExplaniation();
        }

        model.addRow(new Object[] {id, isRegional, isNational, isStage4, title, narrative, phaseResearch,
          stageInnovation, innovationType, contributionOfCrp, degreeInnovation, geographicScope, region, countries,
          organizations, projectExpectedStudy, descriptionStage, evidenceLink, deliverables, crps, genderFocusLevel,
          genderExplaniation, youthFocusLevel, youthExplaniation, leadOrganization, contributingOrganizations});
      }
    }

    return model;
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
      .filter(pl -> pl.isActive() && pl.getYear() == this.getSelectedYear() && pl.getPhase() != null
        && pl.getPhase().equals(this.getSelectedPhase()))
      .collect(Collectors.toList())) {
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
      if (projectLeverage.isPhaseOneLeverage()) {
        if (projectLeverage.getIpProgram() != null && !projectLeverage.getIpProgram().getComposedName().isEmpty()) {
          flagship = projectLeverage.getIpProgram().getComposedName();
        }
      } else {
        if (projectLeverage.getCrpProgram() != null && !projectLeverage.getCrpProgram().getComposedName().isEmpty()) {
          flagship = projectLeverage.getCrpProgram().getComposedName();
        }
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
        "type", "isGlobal", "isPhaseOne", "budget_gender", "hasTargetUnit", "hasW1W2Co", "hasActivities", "phaseID",
        "hasSpecificitiesDeliverableIntellectualAsset", "hasLP6"},
      new Class[] {String.class, String.class, String.class, String.class, String.class, Boolean.class, Boolean.class,
        String.class, Boolean.class, Boolean.class, Boolean.class, Boolean.class, Boolean.class, Boolean.class,
        Long.class, Boolean.class, Boolean.class});
    // Filling title
    String title = "";
    if (projectLeader != null) {
      if (projectLeader.getInstitution() != null && projectLeader.getInstitution().getAcronym() != ""
        && projectLeader.getInstitution().getAcronym() != null) {
        title += projectLeader.getInstitution().getAcronym() + "-";
      }
    }
    try {

      if (projectInfo != null && projectInfo.getAdministrative() == null) {
        projectInfo.setAdministrative(false);
      }
    } catch (Exception e) {
      try {
        projectInfo.setAdministrative(false);
      } catch (Exception i) {

      }

    }

    if (projectInfo.getAdministrative() != null && projectInfo.getAdministrative() == false) {
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
    // Get The Crp/Center/Platform where the project was created
    GlobalUnitProject globalUnitProject = project.getGlobalUnitProjects().stream()
      .filter(gu -> gu.isActive() && gu.isOrigin()).collect(Collectors.toList()).get(0);
    centerURL = globalUnitProject.getGlobalUnit().getAcronym();
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

    Boolean hasSpecificitiesDeliverableIntellectualAsset =
      this.hasSpecificities(this.crpDeliverableIntellectualAsset());

    Boolean hasLP6 = false;
    if (this.hasSpecificities(APConstants.CRP_LP6_ACTIVE)) {
      hasLP6 = true;
    }

    model.addRow(new Object[] {title, centerURL, currentDate, submission, this.getSelectedCycle(), isNew,
      isAdministrative, type, projectInfo.getLocationGlobal(), this.isPhaseOne(), hasGender, hasTargetUnit, hasW1W2Co,
      hasActivities, phaseID, hasSpecificitiesDeliverableIntellectualAsset, hasLP6});
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
          if (this.hasSpecificities(APConstants.CRP_IP_OUTCOME_INDICATOR)) {
            outStatement = projectOutcome.getCrpProgramOutcome().getDescription();
            if (projectOutcome.getCrpProgramOutcome().getIndicator() != null
              && !projectOutcome.getCrpProgramOutcome().getIndicator().isEmpty()) {
              outStatement += "<br><b>Indicator: </b>" + projectOutcome.getCrpProgramOutcome().getIndicator();
            }
          } else {
            outStatement = projectOutcome.getCrpProgramOutcome().getDescription();
          }

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

        if (crossCutting.isEmpty()) {
          crossCutting = null;
        }
        model.addRow(new Object[] {expValue, projectOutcome.getNarrativeTarget(), projectOutcome.getId(), outFl,
          outYear, outValue, outStatement, outUnit, crossCutting, expUnit});
      }
    }
    return model;
  }

  private TypedTableModel getPartnerLeaderTableModel(ProjectPartner projectLeader) {
    TypedTableModel model = new TypedTableModel(
      new String[] {"org_leader", "pp_id", "responsibilities", "countryOffices", "partnerPartnershipFormal",
        "partnerPartnershipResearchPhase", "partnerPartnershipGeographicScope", "partnerPartnershipRegion",
        "partnerPartnershipCountries", "partnerPartnershipMainArea", "showRegion", "showCountry"},
      new Class[] {String.class, Long.class, String.class, String.class, String.class, String.class, String.class,
        String.class, String.class, String.class, Boolean.class, Boolean.class},
      0);
    long ppId = 0;
    String orgLeader = null;
    String responsibilities = null;
    String countryOffices = null;
    String partnerPartnershipFormal = null, partnerPartnershipResearchPhase = null,
      partnerPartnershipGeographicScope = null, partnerPartnershipRegion = null, partnerPartnershipCountries = null,
      partnerPartnershipMainArea = null;
    Boolean showRegion = false, showCountry = false;

    // Partnerships
    List<ProjectPartnerPartnership> projectPartnerPartnerships =
      projectLeader.getProjectPartnerPartnerships().stream().filter(p -> p.isActive()).collect(Collectors.toList());
    if (projectPartnerPartnerships != null && projectPartnerPartnerships.size() > 0) {
      if (projectPartnerPartnerships.size() > 1) {
        LOG.warn("There is more than one Partner Partnership for P" + this.getProjectID() + " Phase "
          + this.getSelectedPhase().toString());
      }
      ProjectPartnerPartnership partnerPartnership = projectPartnerPartnerships.get(0);

      List<ProjectPartnerPartnershipResearchPhase> projectPartnerPartnershipResearchPhases = partnerPartnership
        .getProjectPartnerPartnershipResearchPhases().stream().filter(pp -> pp.isActive()).collect(Collectors.toList());
      Set<String> researchPhases = new HashSet<String>();
      if (projectPartnerPartnershipResearchPhases != null && projectPartnerPartnershipResearchPhases.size() > 0) {
        for (ProjectPartnerPartnershipResearchPhase projectPartnerPartnershipResearchPhase : projectPartnerPartnershipResearchPhases) {
          researchPhases.add(projectPartnerPartnershipResearchPhase.getRepIndPhaseResearchPartnership().getName());
        }
        partnerPartnershipResearchPhase = String.join(",", researchPhases);
      }

      if (partnerPartnership.getGeographicScope() != null) {
        Long geographicScopeID = partnerPartnership.getGeographicScope().getId();
        partnerPartnershipGeographicScope = partnerPartnership.getGeographicScope().getName();
        if (!geographicScopeID.equals(this.getReportingIndGeographicScopeGlobal())) {
          if (geographicScopeID.equals(this.getReportingIndGeographicScopeRegional())) {
            showRegion = true;
            if (partnerPartnership.getRegion() != null) {
              partnerPartnershipRegion = partnerPartnership.getRegion().getName();
            }
          } else {
            showCountry = true;
            List<ProjectPartnerPartnershipLocation> partnershipLocations =
              partnerPartnership.getProjectPartnerPartnershipLocations().stream().filter(pl -> pl.isActive())
                .collect(Collectors.toList());
            if (partnershipLocations != null && partnershipLocations.size() > 0) {
              partnershipLocations
                .sort((pl1, pl2) -> pl1.getLocation().getName().compareTo(pl2.getLocation().getName()));
              Set<String> countries = new HashSet<String>();
              for (ProjectPartnerPartnershipLocation partnershipLocation : partnershipLocations) {
                countries.add(partnershipLocation.getLocation().getName());
              }
              partnerPartnershipCountries = String.join(", ", countries);
            }
          }
        }
      }
      if (partnerPartnership.getMainArea() != null && !partnerPartnership.getMainArea().isEmpty()) {
        partnerPartnershipMainArea = partnerPartnership.getMainArea();
      }
    }

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

      model.addRow(new Object[] {orgLeader, ppId, responsibilities, countryOffices, partnerPartnershipFormal,
        partnerPartnershipResearchPhase, partnerPartnershipGeographicScope, partnerPartnershipRegion,
        partnerPartnershipCountries, partnerPartnershipMainArea, showRegion, showCountry});
    } else if (projectLeader.getId() != null && projectLeader.getInstitution() == null) {
      ppId = projectLeader.getId();
      model.addRow(new Object[] {null, ppId, responsibilities, countryOffices, partnerPartnershipFormal,
        partnerPartnershipResearchPhase, partnerPartnershipGeographicScope, partnerPartnershipRegion,
        partnerPartnershipCountries, partnerPartnershipMainArea, showRegion, showCountry});
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
      model.addRow(new Object[] {orgLeader, null, responsibilities, countryOffices, partnerPartnershipFormal,
        partnerPartnershipResearchPhase, partnerPartnershipGeographicScope, partnerPartnershipRegion,
        partnerPartnershipCountries, partnerPartnershipMainArea, showRegion, showCountry});
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
    TypedTableModel model = new TypedTableModel(
      new String[] {"instituttion", "pp_id", "leader_count", "responsibilities", "countryOffices",
        "partnerPartnershipFormal", "partnerPartnershipResearchPhase", "partnerPartnershipGeographicScope",
        "partnerPartnershipRegion", "partnerPartnershipCountries", "partnerPartnershipMainArea", "showRegion",
        "showCountry"},
      new Class[] {String.class, Long.class, Integer.class, String.class, String.class, String.class, String.class,
        String.class, String.class, String.class, String.class, Boolean.class, Boolean.class},
      0);
    int leaderCount = 0;

    if (projectLeader.getId() != null) {
      leaderCount = 1;
      // Get list of partners except project leader
      for (ProjectPartner projectPartner : project.getProjectPartners().stream().filter(c -> c.isActive()
        && c.getId() != projectLeader.getId() && c.getPhase() != null && c.getPhase().equals(this.getSelectedPhase()))
        .collect(Collectors.toList())) {
        String responsibilities = null;
        String countryOffices = null;
        String partnerPartnershipFormal = null, partnerPartnershipResearchPhase = null,
          partnerPartnershipGeographicScope = null, partnerPartnershipRegion = null, partnerPartnershipCountries = null,
          partnerPartnershipMainArea = null;
        Boolean showRegion = false, showCountry = false;

        // Partnerships
        List<ProjectPartnerPartnership> projectPartnerPartnerships = projectPartner.getProjectPartnerPartnerships()
          .stream().filter(p -> p.isActive()).collect(Collectors.toList());
        if (projectPartnerPartnerships != null && projectPartnerPartnerships.size() > 0) {
          if (projectPartnerPartnerships.size() > 1) {
            LOG.warn("There is more than one Partner Partnership for P" + this.getProjectID() + " Phase "
              + this.getSelectedPhase().toString());
          }
          ProjectPartnerPartnership partnerPartnership = projectPartnerPartnerships.get(0);

          List<ProjectPartnerPartnershipResearchPhase> projectPartnerPartnershipResearchPhases =
            partnerPartnership.getProjectPartnerPartnershipResearchPhases().stream().filter(pp -> pp.isActive())
              .collect(Collectors.toList());
          Set<String> researchPhases = new HashSet<String>();
          if (projectPartnerPartnershipResearchPhases != null && projectPartnerPartnershipResearchPhases.size() > 0) {
            for (ProjectPartnerPartnershipResearchPhase projectPartnerPartnershipResearchPhase : projectPartnerPartnershipResearchPhases) {
              researchPhases.add(projectPartnerPartnershipResearchPhase.getRepIndPhaseResearchPartnership().getName());
            }
            partnerPartnershipResearchPhase = String.join(",", researchPhases);
          }
          if (partnerPartnership.getGeographicScope() != null) {
            Long geographicScopeID = partnerPartnership.getGeographicScope().getId();
            partnerPartnershipGeographicScope = partnerPartnership.getGeographicScope().getName();
            if (!geographicScopeID.equals(this.getReportingIndGeographicScopeGlobal())) {
              if (geographicScopeID.equals(this.getReportingIndGeographicScopeRegional())) {
                showRegion = true;
                if (partnerPartnership.getRegion() != null) {
                  partnerPartnershipRegion = partnerPartnership.getRegion().getName();
                }
              } else {
                showCountry = true;
                List<ProjectPartnerPartnershipLocation> partnershipLocations =
                  partnerPartnership.getProjectPartnerPartnershipLocations().stream().filter(pl -> pl.isActive())
                    .collect(Collectors.toList());
                if (partnershipLocations != null && partnershipLocations.size() > 0) {
                  partnershipLocations
                    .sort((pl1, pl2) -> pl1.getLocation().getName().compareTo(pl2.getLocation().getName()));
                  Set<String> countries = new HashSet<String>();
                  for (ProjectPartnerPartnershipLocation partnershipLocation : partnershipLocations) {
                    countries.add(partnershipLocation.getLocation().getName());
                  }
                  partnerPartnershipCountries = String.join(", ", countries);
                }
              }
            }
          }
          if (partnerPartnership.getMainArea() != null && !partnerPartnership.getMainArea().isEmpty()) {
            partnerPartnershipMainArea = partnerPartnership.getMainArea();
          }
        }

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
          leaderCount, responsibilities, countryOffices, partnerPartnershipFormal, partnerPartnershipResearchPhase,
          partnerPartnershipGeographicScope, partnerPartnershipRegion, partnerPartnershipCountries,
          partnerPartnershipMainArea, showRegion, showCountry});
      }
    } else {
      // Get all partners
      for (ProjectPartner projectPartner : project.getProjectPartners().stream()
        .filter(c -> c.isActive() && c.getPhase() != null && c.getPhase().equals(this.getSelectedPhase()))
        .collect(Collectors.toList())) {
        String responsibilities = null;
        String countryOffices = null;
        String partnerPartnershipFormal = null, partnerPartnershipResearchPhase = null,
          partnerPartnershipGeographicScope = null, partnerPartnershipRegion = null, partnerPartnershipCountries = null,
          partnerPartnershipMainArea = null;
        Boolean showRegion = false, showCountry = false;

        // Partnerships
        List<ProjectPartnerPartnership> projectPartnerPartnerships = projectPartner.getProjectPartnerPartnerships()
          .stream().filter(p -> p.isActive()).collect(Collectors.toList());
        if (projectPartnerPartnerships != null && projectPartnerPartnerships.size() > 0) {
          if (projectPartnerPartnerships.size() > 1) {
            LOG.warn("There is more than one Partner Partnership for P" + this.getProjectID() + " Phase "
              + this.getSelectedPhase().toString());
          }
          ProjectPartnerPartnership partnerPartnership = projectPartnerPartnerships.get(0);

          List<ProjectPartnerPartnershipResearchPhase> projectPartnerPartnershipResearchPhases =
            partnerPartnership.getProjectPartnerPartnershipResearchPhases().stream().filter(pp -> pp.isActive())
              .collect(Collectors.toList());
          Set<String> researchPhases = new HashSet<String>();
          if (projectPartnerPartnershipResearchPhases != null && projectPartnerPartnershipResearchPhases.size() > 0) {
            for (ProjectPartnerPartnershipResearchPhase projectPartnerPartnershipResearchPhase : projectPartnerPartnershipResearchPhases) {
              researchPhases.add(projectPartnerPartnershipResearchPhase.getRepIndPhaseResearchPartnership().getName());
            }
            partnerPartnershipResearchPhase = String.join(",", researchPhases);
          }
          if (partnerPartnership.getGeographicScope() != null) {
            Long geographicScopeID = partnerPartnership.getGeographicScope().getId();
            partnerPartnershipGeographicScope = partnerPartnership.getGeographicScope().getName();
            if (!geographicScopeID.equals(this.getReportingIndGeographicScopeGlobal())) {
              if (geographicScopeID.equals(this.getReportingIndGeographicScopeRegional())) {
                showRegion = true;
                if (partnerPartnership.getRegion() != null) {
                  partnerPartnershipRegion = partnerPartnership.getRegion().getName();
                }
              } else {
                showCountry = true;
                List<ProjectPartnerPartnershipLocation> partnershipLocations =
                  partnerPartnership.getProjectPartnerPartnershipLocations().stream().filter(pl -> pl.isActive())
                    .collect(Collectors.toList());
                if (partnershipLocations != null && partnershipLocations.size() > 0) {
                  partnershipLocations
                    .sort((pl1, pl2) -> pl1.getLocation().getName().compareTo(pl2.getLocation().getName()));
                  Set<String> countries = new HashSet<String>();
                  for (ProjectPartnerPartnershipLocation partnershipLocation : partnershipLocations) {
                    countries.add(partnershipLocation.getLocation().getName());
                  }
                  partnerPartnershipCountries = String.join(", ", countries);
                }
              }
            }
          }
          if (partnerPartnership.getMainArea() != null && !partnerPartnership.getMainArea().isEmpty()) {
            partnerPartnershipMainArea = partnerPartnership.getMainArea();
          }
        }
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
          leaderCount, responsibilities, countryOffices, partnerPartnershipFormal, partnerPartnershipResearchPhase,
          partnerPartnershipGeographicScope, partnerPartnershipRegion, partnerPartnershipCountries,
          partnerPartnershipMainArea, showRegion, showCountry});
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
      overall = project.getProjectInfoLast(this.getSelectedPhase()).getPartnerOverall();
      if (overall == null || overall.isEmpty()) {
        overall = "&lt;Not Defined&gt;";
      }
    }
    model.addRow(new Object[] {partnersSize, overall});
    return model;
  }

  public Project getProject() {
    return project;
  }

  private TypedTableModel getProjectContributionTableModel() {
    // Project Lp6 contribution
    // Decimal format
    TypedTableModel model = new TypedTableModel(
      new String[] {"description", "deliverables", "geographicScope", "workingAcrossFlagships", "undertakingEfforts",
        "providingPathways", "top3Partnerships", "undertakingEffortsCSA", "undertakingInitiative"},
      new Class[] {String.class, String.class, String.class, String.class, String.class, String.class, String.class,
        String.class, String.class},
      0);

    for (ProjectLp6Contribution projectLp6Contribution : project.getProjectLp6Contributions().stream().filter(
      pl -> pl.getProject().getId().equals(projectID) && pl.getPhase().getId().equals(this.getSelectedPhase().getId()))
      .collect(Collectors.toList())) {
      String description = null, deliverables = null, geographicScope = null, workingAcrossFlagships = null,
        undertakingEfforts = null, providingPathways = null, top3Partnerships = null, undertakingEffortsCSA = null,
        undertakingInitiative = null;


      // Narrative - description
      if (projectLp6Contribution.getNarrative() != null) {
        description = projectLp6Contribution.getNarrative();
      }
      // Deliverables
      Set<String> contributionDeliverablesSet = new HashSet<>();
      List<ProjectLp6ContributionDeliverable> contributionDeliverableList =
        new ArrayList<ProjectLp6ContributionDeliverable>();
      contributionDeliverableList = projectLp6ContributionDeliverableManager.findAll().stream()
        .filter(d -> d.getProjectLp6Contribution().getId().equals(projectLp6Contribution.getId())
          && d.getPhase().getId().equals(this.getSelectedPhase().getId())
          && d.getProjectLp6Contribution().getId().equals(projectLp6Contribution.getId()))
        .collect(Collectors.toList());
      if (contributionDeliverableList != null && !contributionDeliverableList.isEmpty()) {
        for (ProjectLp6ContributionDeliverable contributionDeliverable : contributionDeliverableList) {
          if (contributionDeliverable.getDeliverable() != null
            && contributionDeliverable.getDeliverable().getDeliverableInfo() != null) {
            contributionDeliverablesSet.add("<br>&nbsp;&nbsp;&nbsp;&nbsp; ● "
              + contributionDeliverable.getDeliverable().getDeliverableInfo().getTitle());
          }
        }
        deliverables = String.join("", contributionDeliverablesSet);
      }
      // Geographic Scope
      if (projectLp6Contribution.getGeographicScopeNarrative() != null) {
        geographicScope = projectLp6Contribution.getGeographicScopeNarrative();
      }
      // Working Across Flagships
      if (projectLp6Contribution.getWorkingAcrossFlagshipsNarrative() != null) {
        workingAcrossFlagships = projectLp6Contribution.getWorkingAcrossFlagshipsNarrative();
      }
      // Undertaking Efforts
      if (projectLp6Contribution.getUndertakingEffortsLeadingNarrative() != null) {
        undertakingEfforts = projectLp6Contribution.getUndertakingEffortsLeadingNarrative();
      }
      // providingPathways
      if (projectLp6Contribution.getProvidingPathwaysNarrative() != null) {
        providingPathways = projectLp6Contribution.getProvidingPathwaysNarrative();
      }
      // Top 3 Partnerships
      if (projectLp6Contribution.getTopThreePartnershipsNarrative() != null) {
        top3Partnerships = projectLp6Contribution.getTopThreePartnershipsNarrative();
      }
      // Undertaking Efforts CSA
      if (projectLp6Contribution.getUndertakingEffortsCsaNarrative() != null) {
        undertakingEffortsCSA = projectLp6Contribution.getUndertakingEffortsCsaNarrative();
      }
      // Undertaking Initiative
      if (projectLp6Contribution.getInitiativeRelatedNarrative() != null) {
        undertakingInitiative = projectLp6Contribution.getInitiativeRelatedNarrative();
      }
      model.addRow(new Object[] {description, deliverables, geographicScope, workingAcrossFlagships, undertakingEfforts,
        providingPathways, top3Partnerships, undertakingEffortsCSA, undertakingInitiative});
    }
    return model;
  }

  private TypedTableModel getProjectHighlightReportingTableModel() {
    TypedTableModel model = new TypedTableModel(
      new String[] {"id", "title", "author", "subject", "publisher", "year_reported", "highlights_types",
        "highlights_is_global", "start_date", "end_date", "keywords", "countries", "image", "highlight_desc",
        "introduction", "results", "partners", "links", "width", "heigth", "isGlobal", "imageurl"},
      new Class[] {Long.class, String.class, String.class, String.class, String.class, Long.class, String.class,
        String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class,
        String.class, String.class, String.class, Integer.class, Integer.class, Boolean.class, String.class},
      0);
    SimpleDateFormat formatter = new SimpleDateFormat("MMM yyyy");
    for (ProjectHighlight projectHighlight : project.getProjectHighligths().stream()
      .sorted((h1, h2) -> Long.compare(h1.getId(), h2.getId()))
      .filter(ph -> ph.getProjectHighlightInfo(this.getSelectedPhase()) != null && ph.isActive()
        && ph.getProjectHighlightInfo().getYear() != null
        && ph.getProjectHighlightInfo().getYear() == this.getSelectedYear())
      .collect(Collectors.toList())) {
      String title = null, author = null, subject = null, publisher = null, highlightsTypes = "",
        highlightsIsGlobal = null, startDate = null, endDate = null, keywords = null, countries = "", image = "",
        highlightDesc = null, introduction = null, results = null, partners = null, links = null, imageurl = null;
      Long yearReported = null;
      Boolean isGlobal = false;
      int width = 0;
      int heigth = 0;
      if (projectHighlight.getProjectHighlightInfo().getTitle() != null
        && !projectHighlight.getProjectHighlightInfo().getTitle().isEmpty()) {
        title = projectHighlight.getProjectHighlightInfo().getTitle();
      }
      if (projectHighlight.getProjectHighlightInfo().getAuthor() != null
        && !projectHighlight.getProjectHighlightInfo().getAuthor().isEmpty()) {
        author = projectHighlight.getProjectHighlightInfo().getAuthor();
      }
      if (projectHighlight.getProjectHighlightInfo().getSubject() != null
        && !projectHighlight.getProjectHighlightInfo().getSubject().isEmpty()) {
        subject = projectHighlight.getProjectHighlightInfo().getSubject();
      }
      if (projectHighlight.getProjectHighlightInfo().getPublisher() != null
        && !projectHighlight.getProjectHighlightInfo().getPublisher().isEmpty()) {
        publisher = projectHighlight.getProjectHighlightInfo().getPublisher();
      }
      if (projectHighlight.getProjectHighlightInfo().getYear() != null) {
        yearReported = projectHighlight.getProjectHighlightInfo().getYear();
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
      if (projectHighlight.getProjectHighlightInfo().isGlobal() == true) {
        isGlobal = true;
        highlightsIsGlobal = "Yes";
      } else {
        highlightsIsGlobal = "No";
      }
      if (projectHighlight.getProjectHighlightInfo().getStartDate() != null) {
        startDate = formatter.format(projectHighlight.getProjectHighlightInfo().getStartDate());
      }
      if (projectHighlight.getProjectHighlightInfo().getEndDate() != null) {
        endDate = formatter.format(projectHighlight.getProjectHighlightInfo().getEndDate());
      }
      if (projectHighlight.getProjectHighlightInfo().getKeywords() != null
        && !projectHighlight.getProjectHighlightInfo().getKeywords().isEmpty()) {
        keywords = projectHighlight.getProjectHighlightInfo().getKeywords();
      }
      int countriesFlag = 0;
      for (ProjectHighlightCountry projectHighlightCountry : projectHighlight.getProjectHighlightCountries().stream()
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
      if (projectHighlight.getProjectHighlightInfo().getFile() != null) {
        double pageWidth = 612 * 0.4;
        double pageHeigth = 792 * 0.4;
        double imageWidth = 0;
        double imageHeigth = 0;
        image = this.getHightlightImagePath(projectHighlight.getProject().getId())
          + projectHighlight.getProjectHighlightInfo().getFile().getFileName();
        imageurl = this.getHighlightsImagesUrl(projectHighlight.getProject().getId().toString())
          + projectHighlight.getProjectHighlightInfo().getFile().getFileName();
        Image imageFile = null;
        LOG.info("Image name: " + image);
        File url;
        try {
          url = new File(image);
        } catch (Exception e) {
          LOG.warn("Failed to get image File. Url was set to null. Exception: " + e.getMessage());
          url = null;
          image = "";
          imageurl = null;
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
            imageurl = null;
          } catch (IOException e) {
            LOG.warn("IOException getting image: " + e.getMessage());
            image = "";
            imageurl = null;
          }
        } else {
          image = "";
          imageurl = null;
        }
      }
      if (projectHighlight.getProjectHighlightInfo().getDescription() != null
        && !projectHighlight.getProjectHighlightInfo().getDescription().isEmpty()) {
        highlightDesc = projectHighlight.getProjectHighlightInfo().getDescription();
      }
      if (projectHighlight.getProjectHighlightInfo().getObjectives() != null
        && !projectHighlight.getProjectHighlightInfo().getObjectives().isEmpty()) {
        introduction = projectHighlight.getProjectHighlightInfo().getObjectives();
      }
      if (projectHighlight.getProjectHighlightInfo().getResults() != null
        && !projectHighlight.getProjectHighlightInfo().getResults().isEmpty()) {
        results = projectHighlight.getProjectHighlightInfo().getResults();
      }
      if (projectHighlight.getProjectHighlightInfo().getPartners() != null
        && !projectHighlight.getProjectHighlightInfo().getPartners().isEmpty()) {
        partners = projectHighlight.getProjectHighlightInfo().getPartners();
      }
      if (projectHighlight.getProjectHighlightInfo().getLinks() != null
        && !projectHighlight.getProjectHighlightInfo().getLinks().isEmpty()) {
        links = projectHighlight.getProjectHighlightInfo().getLinks();
      }
      model.addRow(new Object[] {projectHighlight.getId(), title, author, subject, publisher, yearReported,
        highlightsTypes, highlightsIsGlobal, startDate, endDate, keywords, countries, image, highlightDesc,
        introduction, results, partners, links, width, heigth, isGlobal, imageurl});
    }
    return model;
  }

  public long getProjectID() {
    return projectID;
  }

  public ProjectInfo getProjectInfo() {
    return projectInfo;
  }

  public String getProjectOutcomeUrl() {
    return config.getDownloadURL() + "/" + this.getProjectOutcomeUrlPath().replace('\\', '/');
  }

  public String getProjectOutcomeUrlPath() {
    return config.getProjectsBaseFolder(this.getCrpSession()) + File.separator + project.getId() + File.separator
      + "project_outcome" + File.separator;
  }

  private TypedTableModel getProjectPolicyTableModel() {
    // Project Policy

    TypedTableModel model = new TypedTableModel(
      new String[] {"id", "title", "year", "investment", "organizationType", "levelMaturity", "whosePolicy",
        "outcomeCaseReport", "innovations", "contributingCRP", "subIDOs", "gender", "youth", "capdev", "climateChange",
        "geographicScope", "narrative"},
      new Class[] {Long.class, String.class, String.class, String.class, String.class, String.class, String.class,
        String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class,
        String.class, String.class},
      0);
    for (ProjectPolicy projectPolicy : project.getProjectPolicies().stream()
      .filter(pl -> pl.getProject() != null && pl.getProject().getId().equals(projectID)
        && pl.getProjectPolicyInfo(this.getSelectedPhase()) != null && pl
          .getProjectPolicyInfo((this.getSelectedPhase())).getPhase().getId().equals(this.getSelectedPhase().getId()))
      .collect(Collectors.toList())) {
      String title = null, year = null, investment = null, organizationType = null, levelMaturity = null,
        whosePolicy = null, outcomeCaseReport = null, innovations = null, contributingCRP = null, subIDOs = null,
        gender = null, youth = null, capdev = null, climateChange = null, geographicScope = null, region = "",
        countries = "", narrative = "";

      Boolean isRegional = false, isNational = false;
      URLShortener urlShortener = new URLShortener();
      // Title
      if (projectPolicy.getProjectPolicyInfo() != null && projectPolicy.getProjectPolicyInfo().getTitle() != null) {
        title = projectPolicy.getProjectPolicyInfo().getTitle();
      }

      // Narrative
      if (projectPolicy.getProjectPolicyInfo() != null
        && projectPolicy.getProjectPolicyInfo().getNarrativeEvidence() != null) {
        narrative = projectPolicy.getProjectPolicyInfo().getNarrativeEvidence();
      }

      // Year
      if (projectPolicy.getProjectPolicyInfo() != null && projectPolicy.getProjectPolicyInfo().getYear() != null) {
        year = projectPolicy.getProjectPolicyInfo().getYear().toString();
      }
      // Investment
      if (projectPolicy.getProjectPolicyInfo() != null
        && projectPolicy.getProjectPolicyInfo().getRepIndPolicyInvestimentType() != null
        && projectPolicy.getProjectPolicyInfo().getRepIndPolicyInvestimentType().getName() != null) {
        investment = projectPolicy.getProjectPolicyInfo().getRepIndPolicyInvestimentType().getName();
      }

      // Organizational Type
      // delete fields in prpt

      // Level Maturity
      if (projectPolicy.getProjectPolicyInfo() != null
        && projectPolicy.getProjectPolicyInfo().getRepIndStageProcess() != null
        && projectPolicy.getProjectPolicyInfo().getRepIndStageProcess().getName() != null) {
        levelMaturity = projectPolicy.getProjectPolicyInfo().getRepIndStageProcess().getName();
      }

      // Owners
      List<ProjectPolicyOwner> pList = projectPolicyOwnerManager.findAll();
      if (pList != null) {
        pList = pList.stream().filter(p -> p.getPhase().getId().equals(this.getSelectedPhase().getId())
          && p.getProjectPolicy().getId().equals(projectPolicy.getId())).collect(Collectors.toList());
      }

      if (pList != null) {
        Set<String> ownersSet = new HashSet<>();
        for (ProjectPolicyOwner projectPolicyOwner : pList) {
          if (projectPolicyOwner.getRepIndPolicyType() != null) {
            ownersSet.add("<br>&nbsp;&nbsp;&nbsp;&nbsp; ● " + projectPolicyOwner.getRepIndPolicyType().getName());
          }
        }
        whosePolicy = String.join("", ownersSet);
      }

      // Outcomes case report
      List<ProjectExpectedStudyPolicy> expectedStudyList = projectExpectedStudyPolicyManager.findAll();
      if (expectedStudyList != null) {
        expectedStudyList =
          expectedStudyList.stream().filter(p -> p.getPhase().getId().equals(this.getSelectedPhase().getId())
            && p.getProjectPolicy().getId().equals(projectPolicy.getId())).collect(Collectors.toList());
        if (expectedStudyList != null) {
          Set<String> evidencesSet = new HashSet<>();
          for (ProjectExpectedStudyPolicy evidences : expectedStudyList) {
            if (evidences.getProjectExpectedStudy() != null
              && evidences.getProjectExpectedStudy().getComposedName() != null) {
              evidencesSet
                .add("<br>&nbsp;&nbsp;&nbsp;&nbsp; ● " + evidences.getProjectExpectedStudy().getComposedName());
            }
          }
          outcomeCaseReport = String.join("", evidencesSet);
        }
      }

      // Innovation
      /*
       * List<ProjectPolicyInnovation> projectPolicyInnovationList = new ArrayList<ProjectPolicyInnovation>();
       * projectPolicyInnovationList = projectPolicyInnovationManager.findAll().stream()
       * .filter(p -> p.getPhase().getId().equals(this.getSelectedPhase().getId())
       * && p.getProjectPolicy().getId().equals(projectPolicy.getId()))
       * .collect(Collectors.toList());
       */
      if (projectPolicy.getInnovations() != null) {
        Set<String> innovationsSet = new HashSet<>();
        for (ProjectPolicyInnovation innovation : projectPolicy.getInnovations()) {
          if (innovation.getProjectInnovation() != null
            && innovation.getProjectInnovation().getProjectInnovationInfo() != null) {
            innovationsSet.add("<br>&nbsp;&nbsp;&nbsp;&nbsp; ● " + innovation.getProjectInnovation().getComposedName());
          }
        }
        innovations = String.join("", innovationsSet);
      }

      // Contributing CRP
      List<ProjectPolicyCrp> projectPolicyCrpList = projectPolicyCrpManager.findAll().stream()
        .filter(p -> p.getPhase().getId().equals(this.getSelectedPhase().getId())
          && p.getProjectPolicy().getId().equals(projectPolicy.getId()))
        .collect(Collectors.toList());

      if (projectPolicyCrpList != null) {
        Set<String> crpSet = new HashSet<>();
        for (ProjectPolicyCrp crp : projectPolicyCrpList) {
          if (crp.getGlobalUnit() != null && crp.getGlobalUnit().getComposedName() != null) {
            crpSet.add("<br>&nbsp;&nbsp;&nbsp;&nbsp; ● " + crp.getGlobalUnit().getComposedName());
          }
        }
        contributingCRP = String.join("", crpSet);
      }

      // subIDOs
      List<ProjectPolicySubIdo> ProjectPolicySubIdoList = projectPolicySubIdoManager.findAll().stream()
        .filter(p -> p.getPhase().getId().equals(this.getSelectedPhase().getId())
          && p.getProjectPolicy().getId().equals(projectPolicy.getId()))
        .collect(Collectors.toList());
      if (ProjectPolicySubIdoList != null) {


        Set<String> subIdosSet = new HashSet<>();
        for (ProjectPolicySubIdo subIdo : ProjectPolicySubIdoList) {
          if (subIdo.getSrfSubIdo() != null && subIdo.getSrfSubIdo().getDescription() != null) {
            subIdosSet.add("<br>&nbsp;&nbsp;&nbsp;&nbsp; ● " + subIdo.getSrfSubIdo().getDescription());
          }
        }
        subIDOs = String.join("", subIdosSet);
      }

      List<ProjectPolicyCrossCuttingMarker> projectPolicyCrossCuttingMarkerList = projectPolicyCrossCuttingMarkerManager
        .findAll().stream().filter(p -> p.getPhase().getId().equals(this.getSelectedPhase().getId())
          && p.getProjectPolicy().getId().equals(projectPolicy.getId()))
        .collect(Collectors.toList());

      if (projectPolicyCrossCuttingMarkerList != null) {

        for (ProjectPolicyCrossCuttingMarker crossCuttingMarker : projectPolicyCrossCuttingMarkerList) {
          if (crossCuttingMarker.getRepIndGenderYouthFocusLevel() != null
            && crossCuttingMarker.getRepIndGenderYouthFocusLevel().getPowbName() != null
            && crossCuttingMarker.getCgiarCrossCuttingMarker() != null) {
            if (crossCuttingMarker.getCgiarCrossCuttingMarker().getId() == 1) {
              gender = crossCuttingMarker.getRepIndGenderYouthFocusLevel().getPowbName();
            }
            if (crossCuttingMarker.getCgiarCrossCuttingMarker().getId() == 2) {
              youth = crossCuttingMarker.getRepIndGenderYouthFocusLevel().getPowbName();
            }
            if (crossCuttingMarker.getCgiarCrossCuttingMarker().getId() == 3) {
              capdev = crossCuttingMarker.getRepIndGenderYouthFocusLevel().getPowbName();
            }
            if (crossCuttingMarker.getCgiarCrossCuttingMarker().getId() == 4) {
              climateChange = crossCuttingMarker.getRepIndGenderYouthFocusLevel().getPowbName();
            }
          }
        }
      }

      /*
       * Geographic Scope
       */
      if (projectPolicy.getProjectPolicyInfo().getRepIndGeographicScope() != null) {
        geographicScope = projectPolicy.getProjectPolicyInfo().getRepIndGeographicScope().getName();
        // Regional
        if (projectPolicy.getProjectPolicyInfo().getRepIndGeographicScope().getId()
          .equals(this.getReportingIndGeographicScopeRegional())) {

          isRegional = true;
          List<ProjectExpectedStudyCountry> projectExpectedStudyRegions = projectExpectedStudyCountryManager
            .getProjectExpectedStudyCountrybyPhase(projectPolicy.getId(), this.getSelectedPhase().getId()).stream()
            .filter(le -> le.isActive() && le.getLocElement().getLocElementType().getId() == 1)
            .collect(Collectors.toList());

          if (projectExpectedStudyRegions != null && projectExpectedStudyRegions.size() > 0) {
            Set<String> regionsSet = new HashSet<>();
            for (ProjectExpectedStudyCountry projectExpectedStudyCountry : projectExpectedStudyRegions) {
              regionsSet.add(projectExpectedStudyCountry.getLocElement().getName());
            }
            region = String.join(", ", regionsSet);
          }
        }

        // Country
        if (!projectPolicy.getProjectPolicyInfo().getRepIndGeographicScope().getId()
          .equals(this.getReportingIndGeographicScopeGlobal())
          && !projectPolicy.getProjectPolicyInfo().getRepIndGeographicScope().getId()
            .equals(this.getReportingIndGeographicScopeRegional())) {
          isNational = true;
          List<ProjectExpectedStudyCountry> deliverableCountries = projectExpectedStudyCountryManager
            .getProjectExpectedStudyCountrybyPhase(projectPolicy.getProjectPolicyInfo().getId(),
              this.getSelectedPhase().getId())
            .stream().filter(le -> le.isActive() && le.getLocElement().getLocElementType().getId() == 2)
            .collect(Collectors.toList());
          if (deliverableCountries != null && deliverableCountries.size() > 0) {
            Set<String> countriesSet = new HashSet<>();
            for (ProjectExpectedStudyCountry deliverableCountry : deliverableCountries) {
              countriesSet.add(deliverableCountry.getLocElement().getName());
            }
            countries = String.join(", ", countriesSet);
          }
        }
      }
      /*
       * if (geographicScope.isEmpty()) {
       * geographicScope = null;
       * }
       * if (region.isEmpty()) {
       * region = null;
       * }
       * if (countries.isEmpty()) {
       * countries = null;
       * }
       */
      model.addRow(new Object[] {projectPolicy.getId(), title, year, investment, organizationType, levelMaturity,
        whosePolicy, outcomeCaseReport, innovations, contributingCRP, subIDOs, gender, youth, capdev, climateChange,
        geographicScope, narrative});

    }
    return model;
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

  private TypedTableModel getStudiesTableModel() {
    TypedTableModel model = new TypedTableModel(new String[] {"id", "year", "policyAmount", "title", "status", "type",
      "tagged", "outcomeImpactStatement", "outcomeHistory", "linksProvided", "isContributionText",
      "policyInvestimentType", "organizationType", "stageProcess", "stageStudy", "srfTargets", "subIdos",
      "topLevelComments", "geographicScope", "region", "countries", "scopeComments", "crps", "flagships", "regions",
      "institutions", "elaborationOutcomeImpactStatement", "referenceText", "referencesFile", "quantification",
      "genderRelevance", "youthRelevance", "capacityRelevance", "otherCrossCuttingDimensions", "comunicationsMaterial",
      "comunicationsFile", "contacts", "studyProjects", "isContribution", "isBudgetInvestment", "isStage1",
      "isRegional", "isNational", "hasreferencesFile", "hasCommunicationFile", "isOutcomeCaseStudy",
      "hasMultipleProjects", "cgiarInnovations", "cgiarInnovationsList", "climateRelevance", "comunicationsMaterials"},
      new Class[] {Long.class, Integer.class, Double.class, String.class, String.class, String.class, String.class,
        String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class,
        String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class,
        String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class,
        String.class, String.class, String.class, String.class, String.class, String.class, String.class, Boolean.class,
        Boolean.class, Boolean.class, Boolean.class, Boolean.class, Boolean.class, Boolean.class, Boolean.class,
        Boolean.class, String.class, String.class, String.class, String.class},
      0);
    Set<ProjectExpectedStudy> myStudies = new HashSet<>();

    List<ProjectExpectedStudy> projectExpectedStudies = project.getProjectExpectedStudies().stream()
      .filter(p -> p.isActive() && p.getProjectExpectedStudyInfo(this.getSelectedPhase()) != null
        && p.getProjectExpectedStudyInfo(this.getSelectedPhase()).getYear() != null
        && p.getProjectExpectedStudyInfo(this.getSelectedPhase()).getYear().equals(this.getSelectedYear()))
      .collect(Collectors.toList());
    if (projectExpectedStudies != null && !projectExpectedStudies.isEmpty()) {
      myStudies.addAll(projectExpectedStudies);
    }

    URLShortener urlShortener = new URLShortener();

    // Shared Studies
    List<ExpectedStudyProject> sharedStudies = project.getExpectedStudyProjects().stream()
      .filter(c -> c.isActive() && c.getPhase() != null && c.getPhase().equals(this.getSelectedPhase())
        && c.getProjectExpectedStudy().getProjectExpectedStudyInfo(this.getSelectedPhase()) != null
        && c.getProjectExpectedStudy().getProjectExpectedStudyInfo(this.getSelectedPhase()).getYear() != null
        && c.getProjectExpectedStudy().getProjectExpectedStudyInfo(this.getSelectedPhase()).getYear() == this
          .getCurrentCycleYear())
      .collect(Collectors.toList());

    if (sharedStudies != null && !sharedStudies.isEmpty()) {
      for (ExpectedStudyProject expectedStudyProject : sharedStudies) {
        myStudies.add(expectedStudyProject.getProjectExpectedStudy());
      }
    }

    if (myStudies != null && !myStudies.isEmpty()) {
      for (ProjectExpectedStudy projectExpectedStudy : myStudies.stream()
        .sorted((s1, s2) -> s1.getId().compareTo(s2.getId())).collect(Collectors.toList())) {
        ProjectExpectedStudyInfo studyinfo = projectExpectedStudy.getProjectExpectedStudyInfo();
        Long id = null;
        Integer year = null;
        Double policyAmount = null;
        String title = null, status = null, type = null, outcomeImpactStatement = null, outcomeHistory = null,
          linksProvided = null, isContributionText = null, policyInvestimentType = null, organizationType = null,
          stageProcess = null, stageStudy = null, srfTargets = null, subIdos = null, topLevelComments = null,
          geographicScope = "", region = "", countries = "", scopeComments = null, crps = null, flagships = null,
          regions = null, institutions = null, elaborationOutcomeImpactStatement = null, referenceText = null,
          referencesFile = null, quantification = null, genderRelevance = null, youthRelevance = null,
          capacityRelevance = null, climateChangeRelevance = null, otherCrossCuttingDimensions = null,
          comunicationsMaterial = null, comunicationsMaterials = null, projectPolicy = null, comunicationsFile = null,
          contacts = null, studyProjects = null, commissioningStudy = null, tagget = null, cgiarInnovations = null,
          cgiarInnovationsList = null, link = null;

        Boolean isContribution = false, isBudgetInvestment = false, isStage1 = false, isRegional = false,
          isNational = false, hasreferencesFile = false, hasCommunicationFile = false, isOutcomeCaseStudy = false,
          hasMultipleProjects = false;

        // Id
        id = projectExpectedStudy.getId();
        // Year
        if (studyinfo != null && studyinfo.getYear() != null) {
          year = projectExpectedStudy.getProjectExpectedStudyInfo().getYear();
        }
        // Title
        if (studyinfo.getTitle() != null && !studyinfo.getTitle().trim().isEmpty()) {
          title = studyinfo.getTitle();
        }
        // Tagged
        if (studyinfo != null && studyinfo.getEvidenceTag() != null && studyinfo.getEvidenceTag().getName() != null) {
          tagget = studyinfo.getEvidenceTag().getName();
        }
        // Status
        if (studyinfo.getStatus() != null) {
          status = studyinfo.getStatus().getName();
        }
        // Type
        if (studyinfo.getStudyType() != null) {
          type = studyinfo.getStudyType().getName();
          if (studyinfo.getStudyType().getId().intValue() == 1) {
            isOutcomeCaseStudy = true;
          }
        }

        if (studyinfo.getCommissioningStudy() != null) {
          commissioningStudy = studyinfo.getCommissioningStudy();
        }

        // outcomeImpactStatement
        if (studyinfo.getOutcomeImpactStatement() != null && !studyinfo.getOutcomeImpactStatement().trim().isEmpty()) {
          outcomeImpactStatement = projectExpectedStudy.getProjectExpectedStudyInfo().getOutcomeImpactStatement();
        }
        // OutcomeStory
        if (projectExpectedStudy.getProjectExpectedStudyInfo() != null
          && projectExpectedStudy.getProjectExpectedStudyInfo().getComunicationsMaterial() != null) {

          /*
           * Get short url calling tinyURL service
           */
          outcomeHistory = urlShortener
            .detectAndShortenLinks(projectExpectedStudy.getProjectExpectedStudyInfo().getComunicationsMaterial());
        }
        // Policy
        if (projectExpectedStudy != null && projectExpectedStudy.getProject() != null
          && projectExpectedStudy.getProjectExpectedStudyInfo() != null && projectExpectedStudy.getProject() != null) {
          /*
           * projectPolicy = projectPolicyManager.findAll().stream()
           * .filter(pp -> pp.getProject().getId() == projectExpectedStudy.getProject().getId()
           * && pp.getProjectPolicyInfo().getPhase() == this.getSelectedPhase())
           * .collect(Collectors.toList()).get(0).getProjectPolicyInfo().getTitle();
           */
        }

        // Links Provided
        if (projectExpectedStudy.getProjectExpectedStudyLinks() != null) {
          projectExpectedStudy.setLinks(new ArrayList<>(projectExpectedStudy.getProjectExpectedStudyLinks().stream()
            .filter(o -> o.isActive() && o.getPhase().getId() == this.getSelectedPhase().getId())
            .collect(Collectors.toList())));
        }

        if (projectExpectedStudy.getProjectExpectedStudyLinks() != null
          && projectExpectedStudy.getProjectExpectedStudyLinks().size() > 0) {
          List<ProjectExpectedStudyLink> linkPrev =
            new ArrayList<>(projectExpectedStudy.getProjectExpectedStudyLinks());
          Set<String> linkSet = new HashSet<>();
          for (ProjectExpectedStudyLink studyLink : linkPrev) {
            linkSet.add("<br>&nbsp;&nbsp;&nbsp;&nbsp; ● " + studyLink.getLink());
          }
          linksProvided = String.join("", linkSet);
        }
        // isContribution
        if (studyinfo.getIsContribution() != null) {
          isContribution = studyinfo.getIsContribution();
          isContributionText = studyinfo.getIsContribution() ? "Yes" : "No";
          if (isContribution) {
            // Policy Investment and Amount
            if (studyinfo.getRepIndPolicyInvestimentType() != null) {
              policyInvestimentType = studyinfo.getRepIndPolicyInvestimentType().getName();
              if (studyinfo.getRepIndPolicyInvestimentType().getId().equals(3l)) {
                isBudgetInvestment = true;
                if (studyinfo.getPolicyAmount() != null) {
                  policyAmount = studyinfo.getPolicyAmount();
                }
              }
            }
            // organizationType
            if (studyinfo.getRepIndOrganizationType() != null) {
              organizationType = studyinfo.getRepIndOrganizationType().getName();
            }
            // stageProcess and stageStudy
            if (studyinfo.getRepIndStageProcess() != null) {
              stageProcess = studyinfo.getRepIndStageProcess().getName();
              if (studyinfo.getRepIndStageProcess().getId().equals(1l)) {
                isStage1 = true;
              }
            }
          }
        }
        if (studyinfo.getRepIndStageStudy() != null) {
          stageStudy = studyinfo.getRepIndStageStudy().getName();
        }

        // cgiarInnovations
        if (studyinfo.getCgiarInnovation() != null) {
          cgiarInnovations = studyinfo.getCgiarInnovation();

          /*
           * Get short url calling tinyURL service
           */
          cgiarInnovations = urlShortener.detectAndShortenLinks(studyinfo.getCgiarInnovation());

        }
        // Innovations
        if (this.project != null) {
          Project projectL = this.projectManager.getProjectById(this.projectID);

          List<ProjectInnovation> innovations =
            projectL.getProjectInnovations().stream().filter(c -> c.isActive()).collect(Collectors.toList());
          Set<String> innovationsSet = new HashSet<>();
          for (ProjectInnovation projectInnovation : innovations) {
            if (projectInnovation != null
              && projectInnovation.getProjectInnovationInfo(this.getSelectedPhase()) != null) {
              // this.innovationsList.add(projectInnovation);
              innovationsSet
                .add("<br>&nbsp;&nbsp;&nbsp;&nbsp; ● " + projectInnovation.getProjectInnovationInfo().getTitle());
            }
          }
          cgiarInnovationsList = String.join("", innovationsSet);
        }

        // SubIdos
        List<ProjectExpectedStudySubIdo> subIdosList = projectExpectedStudy.getProjectExpectedStudySubIdos().stream()
          .filter(s -> s.isActive() && s.getPhase() != null && s.getPhase().equals(this.getSelectedPhase()))
          .collect(Collectors.toList());
        Set<String> subIdoSet = new HashSet<>();
        if (subIdosList != null && subIdosList.size() > 0) {
          for (ProjectExpectedStudySubIdo studySrfTarget : subIdosList) {
            subIdoSet.add("<br>&nbsp;&nbsp;&nbsp;&nbsp; ● " + studySrfTarget.getSrfSubIdo().getDescription());
          }
          subIdos = String.join("", subIdoSet);
        }

        // SRF Targets
        List<ProjectExpectedStudySrfTarget> studySrfTargets = projectExpectedStudy.getProjectExpectedStudySrfTargets()
          .stream().filter(s -> s.isActive() && s.getPhase() != null && s.getPhase().equals(this.getSelectedPhase()))
          .collect(Collectors.toList());
        Set<String> srfTargetSet = new HashSet<>();
        if (studySrfTargets != null && studySrfTargets.size() > 0) {
          for (ProjectExpectedStudySrfTarget studySrfTarget : studySrfTargets) {
            srfTargetSet.add("<br>&nbsp;&nbsp;&nbsp;&nbsp; ● " + studySrfTarget.getSrfSloIndicator().getTitle());
          }
          srfTargets = String.join("", srfTargetSet);
        }

        // Comments
        if (studyinfo.getTopLevelComments() != null && !studyinfo.getTopLevelComments().trim().isEmpty()) {
          topLevelComments = studyinfo.getTopLevelComments();
        }

        /*
         * Geographic Scope
         */
        List<ProjectExpectedStudyGeographicScope> projectExpectedStudyGeographicScopeList =
          projectExpectedStudyGeographicScopeManager.findAll().stream()
            .filter(p -> p.getPhase().getId().equals(this.getSelectedPhase().getId())
              && p.getProjectExpectedStudy() == studyinfo.getProjectExpectedStudy())
            .collect(Collectors.toList());

        if (projectExpectedStudyGeographicScopeList.size() > 0) {

          geographicScope = projectExpectedStudyGeographicScopeList.get(0).getRepIndGeographicScope().getName();

          // Regional
          if (projectExpectedStudyGeographicScopeList.get(0).getRepIndGeographicScope().getId()
            .equals(this.getReportingIndGeographicScopeRegional())) {

            isRegional = true;
            List<ProjectExpectedStudyCountry> projectExpectedStudyRegions = projectExpectedStudyCountryManager
              .getProjectExpectedStudyCountrybyPhase(projectExpectedStudy.getId(), this.getSelectedPhase().getId())
              .stream().filter(le -> le.isActive() && le.getLocElement().getLocElementType().getId() == 1)
              .collect(Collectors.toList());

            if (projectExpectedStudyRegions != null && projectExpectedStudyRegions.size() > 0) {
              Set<String> regionsSet = new HashSet<>();
              for (ProjectExpectedStudyCountry projectExpectedStudyCountry : projectExpectedStudyRegions) {
                regionsSet.add(projectExpectedStudyCountry.getLocElement().getName());
              }
              region = String.join(", ", regionsSet);
            }

          }

          // Country
          if (!projectExpectedStudyGeographicScopeList.get(0).getRepIndGeographicScope().getId()
            .equals(this.getReportingIndGeographicScopeGlobal())
            && !projectExpectedStudyGeographicScopeList.get(0).getRepIndGeographicScope().getId()
              .equals(this.getReportingIndGeographicScopeRegional())) {
            isNational = true;
            List<ProjectExpectedStudyCountry> deliverableCountries = projectExpectedStudyCountryManager
              .getProjectExpectedStudyCountrybyPhase(projectExpectedStudy.getId(), this.getSelectedPhase().getId())
              .stream().filter(le -> le.isActive() && le.getLocElement().getLocElementType().getId() == 2)
              .collect(Collectors.toList());
            if (deliverableCountries != null && deliverableCountries.size() > 0) {
              Set<String> countriesSet = new HashSet<>();
              for (ProjectExpectedStudyCountry deliverableCountry : deliverableCountries) {
                countriesSet.add(deliverableCountry.getLocElement().getName());
              }
              countries = String.join(", ", countriesSet);
            }
          }
        }
        if (geographicScope.isEmpty()) {
          geographicScope = null;
        }
        if (region.isEmpty()) {
          region = null;
        }
        if (countries.isEmpty()) {
          countries = null;
        }

        // Geographic Scope comment
        if (studyinfo.getScopeComments() != null && !studyinfo.getScopeComments().trim().isEmpty()) {
          scopeComments = studyinfo.getScopeComments();
        }

        // Communications Materials


        // Expected Study Link List
        List<ProjectExpectedStudyLink> projectLinkList = projectExpectedStudyLinkManager.findAll();

        if (projectLinkList != null) {
          projectLinkList =
            projectLinkList.stream()
              .filter(p -> p.getPhase().equals(this.getSelectedPhase())
                && p.getProjectExpectedStudy().getId().equals(projectExpectedStudy.getId()))
              .collect(Collectors.toList());
        }

        for (ProjectExpectedStudyLink projectLink : projectLinkList) {
          if (projectLink.getLink() != null) {
            comunicationsMaterial += projectLink.getLink();
          }
        }

        // Key Contributions
        // CRPs/Platforms
        List<ProjectExpectedStudyCrp> studyCrpsList = projectExpectedStudy.getProjectExpectedStudyCrps().stream()
          .filter(s -> s.isActive() && s.getPhase() != null && s.getPhase().equals(this.getSelectedPhase()))
          .collect(Collectors.toList());
        Set<String> crpsSet = new HashSet<>();
        if (studyCrpsList != null && studyCrpsList.size() > 0) {
          for (ProjectExpectedStudyCrp studyCrp : studyCrpsList) {
            crpsSet.add("<br>&nbsp;&nbsp;&nbsp;&nbsp; ● " + studyCrp.getGlobalUnit().getComposedName());
          }
          crps = String.join("", crpsSet);
        }
        // Crp Programs
        List<ProjectExpectedStudyFlagship> studyProgramsList = projectExpectedStudy.getProjectExpectedStudyFlagships()
          .stream().filter(s -> s.isActive() && s.getPhase() != null && s.getPhase().equals(this.getSelectedPhase()))
          .collect(Collectors.toList());
        // Flagships
        List<ProjectExpectedStudyFlagship> studyFlagshipList = studyProgramsList.stream()
          .filter(f -> f.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue()
            && f.getCrpProgram().getResearchArea() == null && f.getCrpProgram().getResearchArea() == null
            && f.getCrpProgram().getCrp() != null && f.getCrpProgram().getCrp().isCenterType() == false)
          .collect(Collectors.toList());
        Set<String> flaghipsSet = new HashSet<>();
        if (studyFlagshipList != null && studyFlagshipList.size() > 0) {
          for (ProjectExpectedStudyFlagship studyFlagship : studyFlagshipList) {
            flaghipsSet.add("<br>&nbsp;&nbsp;&nbsp;&nbsp; ● " + studyFlagship.getCrpProgram().getComposedName());
          }
          flagships = String.join("", flaghipsSet);
        }
        // Regional Programs
        List<ProjectExpectedStudyFlagship> studyRegionsList = studyProgramsList.stream()
          .filter(f -> f.getCrpProgram().getProgramType() == ProgramType.REGIONAL_PROGRAM_TYPE.getValue()
            && f.getCrpProgram().getResearchArea() == null && f.getCrpProgram().getCrp().isCenterType() == false)
          .collect(Collectors.toList());
        Set<String> regionSet = new HashSet<>();
        if (studyRegionsList != null && studyRegionsList.size() > 0) {
          for (ProjectExpectedStudyFlagship studyFlagship : studyRegionsList) {
            regionSet.add("<br>&nbsp;&nbsp;&nbsp;&nbsp; ● " + studyFlagship.getCrpProgram().getComposedName());
          }
          regions = String.join("", regionSet);
        }
        // External Partners
        List<ProjectExpectedStudyInstitution> studyInstitutionList =
          projectExpectedStudy.getProjectExpectedStudyInstitutions().stream()
            .filter(s -> s.isActive() && s.getPhase() != null && s.getPhase().equals(this.getSelectedPhase()))
            .collect(Collectors.toList());
        Set<String> institutionSet = new HashSet<>();
        if (studyInstitutionList != null && studyInstitutionList.size() > 0) {
          for (ProjectExpectedStudyInstitution studyinstitution : studyInstitutionList) {
            institutionSet.add("<br>&nbsp;&nbsp;&nbsp;&nbsp; ● " + studyinstitution.getInstitution().getComposedName());
          }
          institutions = String.join("", institutionSet);
        }

        // Elaboration of Outcome/Impact Statement
        if (studyinfo.getElaborationOutcomeImpactStatement() != null
          && !studyinfo.getElaborationOutcomeImpactStatement().trim().isEmpty()) {
          elaborationOutcomeImpactStatement = studyinfo.getElaborationOutcomeImpactStatement();
        }


        // References cited
        if (studyinfo.getReferencesText() != null && !studyinfo.getReferencesText().trim().isEmpty()) {
          referenceText = studyinfo.getReferencesText();
          /*
           * Get short url calling tinyURL service
           */
          referenceText = urlShortener.detectAndShortenLinks(studyinfo.getReferencesText());
        }

        // Atached material
        if (studyinfo.getReferencesFile() != null) {
          hasreferencesFile = true;
          referencesFile = studyinfo.getReferencesFile().getFileName();
        }

        // Quantification
        List<ProjectExpectedStudyQuantification> projectExpectedStudyQuantificationList =
          projectExpectedStudyQuantificationManager.findAll().stream()
            .filter(q -> q.getProjectExpectedStudy() == projectExpectedStudy
              && q.getPhase().getId().equals(this.getSelectedPhase().getId()))
            .collect(Collectors.toList());
        if (projectExpectedStudyQuantificationList != null && projectExpectedStudyQuantificationList.size() > 0) {
          quantification = studyinfo.getQuantification();
          if (projectExpectedStudyQuantificationList.get(0).getTypeQuantification() != null) {
            quantification += "<b>Quantification Type :</b>"
              + projectExpectedStudyQuantificationList.get(0).getTypeQuantification() + "<br>";
          } else {
            quantification += " <not defined><br>";
          }
          if (projectExpectedStudyQuantificationList.get(0).getNumber() != null) {
            quantification += "<b>Number :</b>" + projectExpectedStudyQuantificationList.get(0).getNumber() + "<br>";
          } else {
            quantification += " <not defined><br>";
          }
          if (projectExpectedStudyQuantificationList.get(0).getTargetUnit() != null) {
            quantification += "<b>Unit :</b>" + projectExpectedStudyQuantificationList.get(0).getTargetUnit() + "<br>";
          } else {
            quantification += " <not defined><br>";
          }
          if (projectExpectedStudyQuantificationList.get(0).getComments() != null) {
            quantification +=
              "<b>Comments :</b>" + projectExpectedStudyQuantificationList.get(0).getComments() + "<br>";
          } else {
            quantification += " <not defined><br>";
          }

        }

        // Gender, Youth, and Capacity Development
        // Gender
        if (studyinfo.getGenderLevel() != null) {
          genderRelevance = studyinfo.getGenderLevel().getName();

          if (!studyinfo.getGenderLevel().getId().equals(1l) && studyinfo.getDescribeGender() != null
            && !studyinfo.getDescribeGender().isEmpty()) {
            genderRelevance += "<br>" + this.getText("study.achievementsGenderRelevance.readText") + ": "
              + studyinfo.getDescribeGender();
          }
        }
        // Youth
        if (studyinfo.getYouthLevel() != null) {
          youthRelevance = studyinfo.getYouthLevel().getName();
          if (!studyinfo.getYouthLevel().getId().equals(1l) && studyinfo.getDescribeYouth() != null
            && !studyinfo.getDescribeYouth().isEmpty()) {
            youthRelevance +=
              "<br>" + this.getText("study.achievementsYouthRelevance.readText") + ": " + studyinfo.getDescribeYouth();
          }
        }
        // Capacity Development
        if (studyinfo.getCapdevLevel() != null) {
          capacityRelevance = studyinfo.getCapdevLevel().getName();
          if (!studyinfo.getCapdevLevel().getId().equals(1l) && studyinfo.getDescribeCapdev() != null
            && !studyinfo.getDescribeCapdev().isEmpty()) {
            capacityRelevance += "<br>" + this.getText("study.achievementsCapDevRelevance.readText") + ": "
              + studyinfo.getDescribeCapdev();
          }
        }

        // Climate Change
        if (studyinfo.getClimateChangeLevel() != null) {
          climateChangeRelevance = studyinfo.getClimateChangeLevel().getName();
          if (!studyinfo.getClimateChangeLevel().getId().equals(1l) && studyinfo.getDescribeClimateChange() != null
            && !studyinfo.getDescribeClimateChange().isEmpty()) {
            climateChangeRelevance += "<br>" + this.getText("study.achievementsClimateChangeRelevance.readText") + ": "
              + studyinfo.getDescribeClimateChange();
          }
        }

        // Other cross-cutting dimensions
        if (studyinfo.getOtherCrossCuttingDimensions() != null
          && !studyinfo.getOtherCrossCuttingDimensions().trim().isEmpty()) {
          otherCrossCuttingDimensions = studyinfo.getOtherCrossCuttingDimensions();
        }

        // Communications materials
        if (studyinfo.getComunicationsMaterial() != null && !studyinfo.getComunicationsMaterial().trim().isEmpty()) {
          comunicationsMaterials = studyinfo.getComunicationsMaterial();
        }
        // Atached material
        if (studyinfo.getOutcomeFile() != null) {
          hasCommunicationFile = true;
          comunicationsFile = studyinfo.getOutcomeFile().getFileName();
        }

        // link

        // {baseUrl}/projects/${crpSession}/studySummary.do?studyID=${(element.id)!}&cycle=Reporting&year=${(actualPhase.year)
        if (studyinfo.getProjectExpectedStudy().getId() != null && studyinfo.getPhase() != null
          && this.getBaseUrl() != null) {
          link = this.getBaseUrl() + "/projects/" + this.getCrpSession() + "/studySummary.do?studyID="
            + studyinfo.getProjectExpectedStudy().getId() + "&cycle=Reporting&year=" + studyinfo.getPhase().getYear();

          /*
           * Get short url calling tinyURL service
           */
          link = urlShortener.getShortUrlService(link);
        }

        // Contact person
        if (studyinfo.getContacts() != null && !studyinfo.getContacts().trim().isEmpty()) {
          contacts = studyinfo.getContacts();

          if (link != null && !link.isEmpty() && studyinfo.getProjectExpectedStudy() != null
            && studyinfo.getProjectExpectedStudy().getProjectExpectedStudyInfo() != null
            && studyinfo.getProjectExpectedStudy().getProjectExpectedStudyInfo().getIsPublic() != null
            && studyinfo.getProjectExpectedStudy().getProjectExpectedStudyInfo().getIsPublic() == true) {
            contacts +=
              "<br></br><p><font size=2 face='Segoe UI' \n> <b>Outcome Impact Case Report link:</b></font></p> "
                + "<p><font size=2 face='Segoe UI' \n>" + link + "</font></p>";

          }
        }


        // Projects
        List<ExpectedStudyProject> studyProjectList =
          studyinfo.getProjectExpectedStudy().getExpectedStudyProjects().stream()
            .filter(e -> e.isActive() && e.getPhase() != null && e.getPhase().equals(this.getSelectedPhase())
              && e.getProject() != projectExpectedStudy.getProject())
            .sorted((sp1, sp2) -> sp2.getProject().getId().compareTo(sp1.getProject().getId()))
            .collect(Collectors.toList());
        Set<String> studyProjectSet = new HashSet<>();
        if (studyProjectList != null && studyProjectList.size() > 0) {
          for (ExpectedStudyProject studyProject : studyProjectList) {
            studyProjectSet.add("P" + studyProject.getProject().getId());
          }
        }
        if (studyProjectSet != null && !studyProjectSet.isEmpty()) {
          studyProjects = String.join(", ", studyProjectSet);
          hasMultipleProjects = true;
        }


        model.addRow(new Object[] {id, year, policyAmount, title, status, type, tagget, outcomeImpactStatement,
          outcomeHistory, linksProvided, isContributionText, policyInvestimentType, organizationType, stageProcess,
          stageStudy, srfTargets, subIdos, topLevelComments, geographicScope, region, countries, scopeComments, crps,
          flagships, regions, institutions, elaborationOutcomeImpactStatement, referenceText, referencesFile,
          quantification, genderRelevance, youthRelevance, capacityRelevance, otherCrossCuttingDimensions,
          comunicationsMaterial, comunicationsFile, contacts, studyProjects, isContribution, isBudgetInvestment,
          isStage1, isRegional, isNational, hasreferencesFile, hasCommunicationFile, isOutcomeCaseStudy,
          hasMultipleProjects, cgiarInnovations, cgiarInnovationsList, climateChangeRelevance, comunicationsMaterials});
      }
    }

    return model;
  }

  public HashMap<Long, String> getTargetUnitList() {
    return targetUnitList;
  }

  @Override
  public String getText(String aTextName) {
    String language = APConstants.CUSTOM_LAGUAGE;


    Locale locale = new Locale(language);

    return localizedTextProvider.findDefaultText(aTextName, locale);
  }

  @Override
  public String getText(String key, String[] args) {
    String language = APConstants.CUSTOM_LAGUAGE;


    Locale locale = new Locale(language);

    return localizedTextProvider.findDefaultText(key, locale, args);

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

    List<ProjectBudget> budgets = projectBudgetManager.getByParameters(institutionId, year, budgetType, projectID,
      coFinancing, this.getSelectedPhase().getId());

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

    List<ProjectBudget> budgets = projectBudgetManager.getByParameters(institutionId, year, budgetType, project.getId(),
      coFinancing, this.getSelectedPhase().getId());

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
        if (institution.getCrpPpaPartners().stream()
          .filter(c -> c.isActive() && c.getPhase().equals(this.getSelectedPhase())).collect(Collectors.toList())
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

  public void loadProvider(Map<String, Object> session) {
    String language = APConstants.CUSTOM_LAGUAGE;
    String pathFile = APConstants.PATH_CUSTOM_FILES;
    if (session.containsKey(APConstants.CRP_LANGUAGE)) {
      language = (String) session.get(APConstants.CRP_LANGUAGE);
    }

    Locale locale = new Locale(language);

    /**
     * This is yuck to have to cast the interface to a custom implementation but I can't see a nice way to remove custom
     * properties bundles (the reason we are doing this is the scenario where a user navigates between CRPs. If we don't
     * reset the properties bundles then the user will potentially get the properties loaded from another CRP if that
     * property has not been defined by that CRP or Center.
     */
    ((MarloLocalizedTextProvider) this.localizedTextProvider).resetResourceBundles();

    this.localizedTextProvider.addDefaultResourceBundle(APConstants.CUSTOM_FILE);


    try {
      ServletActionContext.getContext().setLocale(locale);
    } catch (Exception e) {

    }

    if (session.containsKey(APConstants.SESSION_CRP)) {

      if (session.containsKey(APConstants.CRP_CUSTOM_FILE)) {
        pathFile = pathFile + session.get(APConstants.CRP_CUSTOM_FILE);
        this.localizedTextProvider.addDefaultResourceBundle(pathFile);
      } else if (session.containsKey(APConstants.CENTER_CUSTOM_FILE)) {
        pathFile = pathFile + session.get(APConstants.CENTER_CUSTOM_FILE);
        this.localizedTextProvider.addDefaultResourceBundle(pathFile);
      } else {

        this.localizedTextProvider.addDefaultResourceBundle(APConstants.CUSTOM_FILE);
      }
    }
  }

  @Override
  /**
   * Prepare the parameters of the project.
   * Note: If you add a parameter here, you must add it in the ProjectSubmissionAction class
   */
  public void prepare() {
    this.loadProvider(this.getSession());
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
    if (this.getSelectedPhase() != null && project.getProjecInfoPhase(this.getSelectedPhase()) != null) {
      this.setProjectInfo(project.getProjecInfoPhase(this.getSelectedPhase()));
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