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
import org.cgiar.ccafs.marlo.data.model.ExpectedStudyProject;
import org.cgiar.ccafs.marlo.data.model.GlobalUnitProject;
import org.cgiar.ccafs.marlo.data.model.Institution;
import org.cgiar.ccafs.marlo.data.model.IpElement;
import org.cgiar.ccafs.marlo.data.model.IpIndicator;
import org.cgiar.ccafs.marlo.data.model.IpProjectContribution;
import org.cgiar.ccafs.marlo.data.model.IpProjectIndicator;
import org.cgiar.ccafs.marlo.data.model.ProgramType;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectBudget;
import org.cgiar.ccafs.marlo.data.model.ProjectCommunication;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudy;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyCountry;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyInfo;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudySrfTarget;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudySubIdo;
import org.cgiar.ccafs.marlo.data.model.ProjectFocus;
import org.cgiar.ccafs.marlo.data.model.ProjectInfo;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovation;
import org.cgiar.ccafs.marlo.data.model.ProjectLp6Contribution;
import org.cgiar.ccafs.marlo.data.model.ProjectOutcome;
import org.cgiar.ccafs.marlo.data.model.ProjectPartner;
import org.cgiar.ccafs.marlo.data.model.ProjectStatusEnum;
import org.cgiar.ccafs.marlo.data.model.SrfTargetUnit;
import org.cgiar.ccafs.marlo.data.model.Submission;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
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


@Named
public class ProjectActivitiesSummaryAction extends BaseSummariesAction implements Summary {

  private static final long serialVersionUID = -624982650510682813L;

  private static Logger LOG = LoggerFactory.getLogger(ProjectActivitiesSummaryAction.class);

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
  public ProjectActivitiesSummaryAction(APConfig config, GlobalUnitManager crpManager, ProjectManager projectManager,
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

    if (this.getProject().getProjectInfo().getAdministrative() != null
      && this.getProject().getProjectInfo().getAdministrative() == true) {

    } else {

      if (hasActivities) {
        masterReport.getParameterValues().put("i8nActivitiesPlanningMenu",
          "On going " + this.getText("projects.menu.activities"));
        masterReport.getParameterValues().put("i8nClosedActivitiesPlanningMenu",
          "Completed/Cancelled " + this.getText("projects.menu.activities"));
      }
    }
    /**
     * End Menu Planning
     */


    /**
     * Menu Reporting
     */
    masterReport.getParameterValues().put("i8nProjectReportingMenu", "1. " + this.getText("projects.menu.description"));

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

      reportResource = resourceManager
        .createDirectly(this.getClass().getResource("/pentaho/crp/ProjectActivitiesPDF.prpt"), MasterReport.class);

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
        // this.fillSubreport((SubReport) hm.get("description"), "description", args);
        // Description Flagships
        args.clear();

        // if (this.getSelectedCycle().equals("Planning")) {
        this.fillSubreport((SubReport) hm.get("activities"), "activities_list", args);
        // } else {
        this.fillSubreport((SubReport) hm.get("closed_activities"), "closed_activities", args);
        // }


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

      case "activities_list":
        model = this.getActivitiesTableModel();
        break;
      case "closed_activities":
        model = this.getClosedActivitiesTableModel();
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

  private TypedTableModel getClosedActivitiesTableModel() {
    TypedTableModel model = new TypedTableModel(
      new String[] {"activity_id", "title", "description", "start_date", "end_date", "institution", "activity_leader",
        "status", "deliverables"},
      new Class[] {Long.class, String.class, String.class, String.class, String.class, String.class, String.class,
        String.class, String.class},
      0);
    SimpleDateFormat formatter = new SimpleDateFormat("MMM yyyy");
    if (!project.getActivities().isEmpty()) {
      for (Activity activity : project.getActivities().stream().sorted((d1, d2) -> Long.compare(d1.getId(), d2.getId()))
        .filter(a -> a.isActive() && (a.getActivityStatus() != 2 && a.getActivityStatus() != 4) && a.getPhase() != null
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


  public String getDeliverableUrl(String fileType, Deliverable deliverable) {
    return config.getDownloadURL() + "/" + this.getDeliverableUrlPath(fileType, deliverable).replace('\\', '/');
  }

  public String getDeliverableUrlPath(String fileType, Deliverable deliverable) {
    return config.getProjectsBaseFolder(this.getCrpSession()) + File.separator + deliverable.getId() + File.separator
      + "deliverable" + File.separator + fileType + File.separator;
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
    fileName.append("ProjectActivitiesSummary-");
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


  @Override
  public InputStream getInputStream() {
    if (inputStream == null) {
      inputStream = new ByteArrayInputStream(bytesPDF);
    }
    return inputStream;
  }


  private TypedTableModel getMasterTableModel(List<CrpProgram> flagships, List<CrpProgram> regions,
    ProjectPartner projectLeader) {
    // Initialization of Model
    TypedTableModel model = new TypedTableModel(
      new String[] {"title", "projectTitle", "center", "current_date", "project_submission", "cycle", "isNew",
        "isAdministrative", "type", "isGlobal", "isPhaseOne", "budget_gender", "hasTargetUnit", "hasW1W2Co",
        "hasActivities", "phaseID", "hasSpecificitiesDeliverableIntellectualAsset", "hasLP6"},
      new Class[] {String.class, String.class, String.class, String.class, String.class, String.class, Boolean.class,
        Boolean.class, String.class, Boolean.class, Boolean.class, Boolean.class, Boolean.class, Boolean.class,
        Boolean.class, Long.class, Boolean.class, Boolean.class});
    // Filling title
    String title = "", projectTitle = "";
    if (projectLeader != null) {
      if (projectLeader.getInstitution() != null && projectLeader.getInstitution().getAcronym() != ""
        && projectLeader.getInstitution().getAcronym() != null) {
        title = this.getText("project.activities.title");
      }
    }

    if (projectInfo.getTitle() != null) {
      projectTitle = projectInfo.getTitle();
    }

    try {

      if (projectInfo.getAdministrative() == null) {
        projectInfo.setAdministrative(false);
      }
    } catch (Exception e) {
      // projectInfo.setAdministrative(false);
    }

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

    model.addRow(new Object[] {title, projectTitle, centerURL, currentDate, submission, this.getSelectedCycle(), isNew,
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

  public Project getProject() {
    return project;
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
    this.setProjectInfo(project.getProjecInfoPhase(this.getSelectedPhase()));
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