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
import org.cgiar.ccafs.marlo.data.manager.PolicyMilestoneManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyPolicyManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPolicyCountryManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPolicyGeographicScopeManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPolicyInnovationManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPolicyManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPolicyRegionManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPolicySubIdoManager;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.PolicyMilestone;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyPolicy;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicyCenter;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicyCountry;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicyCrossCuttingMarker;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicyCrp;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicyGeographicScope;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicyInfo;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicyInnovation;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicyRegion;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicySubIdo;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.URLShortener;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.dispatcher.Parameter;
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
 * @author Andrés Valencia - CIAT/CCAFS
 */
public class ProjectPolicySummaryAction extends BaseSummariesAction implements Summary {

  private static final long serialVersionUID = 1L;
  private static Logger LOG = LoggerFactory.getLogger(ProjectPolicySummaryAction.class);
  // Managers
  private final ProjectPolicyManager projectPolicyManager;
  private final ProjectPolicyInnovationManager projectPolicyInnovationManager;
  private final ResourceManager resourceManager;
  private final ProjectPolicyGeographicScopeManager projectPolicyGeographicScopeManager;
  private final ProjectPolicyRegionManager projectPolicyRegionManager;
  private final ProjectPolicyCountryManager projectPolicyCountryManager;
  private final PolicyMilestoneManager policyMilestoneManager;
  private final ProjectPolicySubIdoManager projectPolicySubIdoManager;
  private ProjectExpectedStudyPolicyManager projectExpectedStudyPolicyManager;

  private final URLShortener shortener;


  // Parameters
  private long startTime;
  private Long projectPolicyID;
  private ProjectPolicyInfo projectPolicyInfo;

  // XLSX bytes
  private byte[] bytesPDF;
  // Streams
  InputStream inputStream;

  @Inject
  public ProjectPolicySummaryAction(APConfig config, GlobalUnitManager crpManager,
    ProjectPolicyManager projectInnovationManager, PhaseManager phaseManager, ResourceManager resourceManager,
    ProjectManager projectManager, ProjectPolicyInnovationManager projectInnovationDeliverableManager,
    ProjectPolicyGeographicScopeManager projectInnovationGeographicScopeManager,
    ProjectPolicyRegionManager projectInnovationRegionManager,
    ProjectPolicyCountryManager projectInnovationCountryManager,
    PolicyMilestoneManager projectInnovationMilestoneManager, ProjectPolicySubIdoManager projectInnovationSubIdoManager,
    ProjectExpectedStudyPolicyManager projectExpectedStudyInnovationManager) {
    super(config, crpManager, phaseManager, projectManager);
    this.projectPolicyManager = projectInnovationManager;
    this.resourceManager = resourceManager;
    this.projectPolicyInnovationManager = projectInnovationDeliverableManager;
    this.projectPolicyGeographicScopeManager = projectInnovationGeographicScopeManager;
    this.projectPolicyRegionManager = projectInnovationRegionManager;
    this.projectPolicyCountryManager = projectInnovationCountryManager;
    this.policyMilestoneManager = projectInnovationMilestoneManager;
    this.projectPolicySubIdoManager = projectInnovationSubIdoManager;
    this.projectExpectedStudyPolicyManager = projectExpectedStudyInnovationManager;
    this.shortener = new URLShortener();
  }

  /**
   * Method to add i8n parameters to masterReport in Pentaho
   * 
   * @param masterReport
   * @return masterReport with i8n parameters added
   */
  private MasterReport addi8nParameters(MasterReport masterReport) {
    masterReport.getParameterValues().put("i8nPolicyNoData", this.getText("summaries.projectPolicy.noData"));
    masterReport.getParameterValues().put("i8nPolicyRPolicy", this.getText("projectPolicies.title"));
    masterReport.getParameterValues().put("i8nPolicyRNew", this.getText("projectPolicy.isNew"));

    masterReport.getParameterValues().put("i8nPolicyRTitle", this.getText("projectPolicy.title"));
    masterReport.getParameterValues().put("i8nProject", this.getText("summaries.oaprojects.projectTitle"));
    masterReport.getParameterValues().put("i8nPolicyRNarrative", this.getText("policy.description"));
    masterReport.getParameterValues().put("i8nPolicyRStagePolicy", this.getText("projectPolicy.maturity"));
    masterReport.getParameterValues().put("i8nPolicyRPolicyType", this.getText("projectPolicy.investment"));
    masterReport.getParameterValues().put("i8nPolicyRPolicyAmount", this.getText("projectPolicy.amount"));
    masterReport.getParameterValues().put("i8nPolicyRGeographicScope", this.getText("projectPolicy.geographicScope"));
    masterReport.getParameterValues().put("i8nPolicyRRegion", this.getText("projectInnovations.region"));
    masterReport.getParameterValues().put("i8nPolicyRCountries", this.getText("projectInnovations.countries"));

    masterReport.getParameterValues().put("i8nPolicyRProjectExpectedStudy", this.getText("caseStudy.caseStudyTitle"));
    masterReport.getParameterValues().put("i8nPolicyRInnovations", this.getText("projectPolicy.innovations"));

    masterReport.getParameterValues().put("i8nPolicyRDescriptionStage",
      this.getText("projectPolicy.narrative.readText"));
    masterReport.getParameterValues().put("i8nPolicyHasMilestones", this.getText("projectInnovations.hasMilestones"));
    masterReport.getParameterValues().put("i8nPolicyMilestones", this.getText("projectInnovations.milestones"));
    masterReport.getParameterValues().put("i8nPolicySubIdos", this.getText("projectInnovations.subIdos"));
    masterReport.getParameterValues().put("i8nPolicyContributionCenters",
      this.getText("projectInnovations.contributingCenters"));
    masterReport.getParameterValues().put("i8nPolicyRCrps", this.getText("projectPolicy.contributingCRP"));
    // masterReport.getParameterValues().put("i8nPolicyREvidenceLink",
    // this.getText("summaries.innovation.evidenceLink"));
    // masterReport.getParameterValues().put("i8nPolicyLeadOrganization",
    // this.getText("projectInnovations.leadOrganization"));
    // masterReport.getParameterValues().put("i8nPolicyContributionOrganization",
    // this.getText("projectInnovations.contributingOrganizations"));
    // masterReport.getParameterValues().put("i8nPolicyRPhaseResearch", this.getText("projectPolicy.phase"));
    // masterReport.getParameterValues().put("i8nPolicyRContributionOfCrp",
    // this.getText("projectInnovations.contributionOfCrp"));
    // masterReport.getParameterValues().put("i8nPolicyRDegreePolicy",
    // this.getText("projectInnovations.degreeInnovation"));
    // masterReport.getParameterValues().put("i8nPolicyROrganizations",
    // this.getText("summaries.innovation.organizationalType"));
    //
    // masterReport.getParameterValues().put("i8nPolicyAdaptativeResearch", "projectInnovations.adaptativeResearch");
    // masterReport.getParameterValues().put("i8nPolicyRDeliverables",
    // this.getText("projectInnovations.deliverableId"));
    // masterReport.getParameterValues().put("i8nPolicyRGenderFocusLevel",
    // this.getText("projectInnovations.genderRelevance"));
    // masterReport.getParameterValues().put("i8nPolicyRNew", this.getText("projectInnovations.isNew"));
    // masterReport.getParameterValues().put("i8nPolicyRYouthFocusLevel",
    // this.getText("projectInnovations.youthRelevance"));
    // masterReport.getParameterValues().put("i8nPolicyRYouthExplaniation",
    // this.getText("projectInnovations.youthRelevance.explanation.readText"));
    return masterReport;
  }


  @Override
  public String execute() throws Exception {
    if (this.getSelectedPhase() == null) {
      return NOT_FOUND;
    }

    if (projectPolicyID == null || projectPolicyManager.getProjectPolicyById(projectPolicyID) == null
      || projectPolicyManager.getProjectPolicyById(projectPolicyID)
        .getProjectPolicyInfo(this.getSelectedPhase()) == null) {
      LOG.error("Project Policy " + projectPolicyID + " Not found");
      return NOT_FOUND;
    } else {
      projectPolicyInfo =
        projectPolicyManager.getProjectPolicyById(projectPolicyID).getProjectPolicyInfo(this.getSelectedPhase());
    }

    ByteArrayOutputStream os = new ByteArrayOutputStream();
    try {
      Resource reportResource = resourceManager
        .createDirectly(this.getClass().getResource("/pentaho/crp/ProjectPolicyPDF.prpt"), MasterReport.class);
      MasterReport masterReport = (MasterReport) reportResource.getResource();
      String center = this.getLoggedCrp().getAcronym();
      // Get datetime
      ZonedDateTime timezone = ZonedDateTime.now();
      DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-d 'at' HH:mm ");
      String zone = timezone.getOffset() + "";
      if (zone.equals("Z")) {
        zone = "+0";
      }

      String date = timezone.format(format) + "(GMT" + zone + ")";
      // Set Main_Query
      CompoundDataFactory cdf = CompoundDataFactory.normalize(masterReport.getDataFactory());
      String masterQueryName = "main";
      TableDataFactory sdf = (TableDataFactory) cdf.getDataFactoryForQuery(masterQueryName);
      TypedTableModel model = this.getMasterTableModel(center, date, String.valueOf(this.getSelectedYear()));
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
      // Uncomment to see which Subreports are detecting the method getAllSubreports
      // System.out.println("Pentaho SubReports: " + hm);
      this.fillSubreport((SubReport) hm.get("project_policy"), "project_policy");
      PdfReportUtil.createPDF(masterReport, os);
      bytesPDF = os.toByteArray();
      os.close();
    } catch (Exception e) {
      LOG.error("Error generating ProjectPolicy Summary: " + e.getMessage());
      throw e;
    }

    // Calculate time of generation
    long stopTime = System.currentTimeMillis();
    stopTime = stopTime - startTime;
    LOG.info("Downloaded successfully: " + this.getFileName() + ". User: " + this.getDownloadByUser() + ". CRP: "
      + this.getLoggedCrp().getAcronym() + ". Time to generate: " + stopTime + "ms.");
    return SUCCESS;
  }

  private void fillSubreport(SubReport subReport, String query) {
    CompoundDataFactory cdf = CompoundDataFactory.normalize(subReport.getDataFactory());
    TableDataFactory sdf = (TableDataFactory) cdf.getDataFactoryForQuery(query);
    TypedTableModel model = null;
    switch (query) {
      case "project_policy":
        model = this.getProjectPolicyTableModel();
        break;
    }
    sdf.addTable(query, model);
    subReport.setDataFactory(cdf);
  }

  public byte[] getBytesPDF() {
    return bytesPDF;
  }


  @Override
  public int getContentLength() {
    return bytesPDF.length;
  }

  @Override
  public String getContentType() {
    return "application/pdf";
  }

  @SuppressWarnings("unused")
  private String getExpectedStudyDirectLink(String center, Long expectedStudyId, String phaseId) {
    return this.getBaseUrl() + "/projects/" + center + "/study.do?expectedID=" + expectedStudyId + "&phaseID="
      + phaseId;
  }

  private String getExpectedStudyPDFLink(String center, Long expectedStudyId, Phase phase) {
    String longUrl = this.getBaseUrl() + "/projects/" + center + "/studySummary.do?studyID=" + expectedStudyId
      + "&cycle=" + phase.getDescription() + "&year=" + phase.getYear();

    return shortener.getShortUrlService(longUrl);
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
    fileName.append("ProjectPolicySummary-");
    fileName.append(this.getLoggedCrp().getAcronym() + "-");
    try {
      if (projectPolicyInfo != null && projectPolicyInfo.getProjectPolicy().getProject() != null) {
        fileName.append(
          projectPolicyInfo.getProjectPolicy().getProject().getStandardIdentifier(Project.EMAIL_SUBJECT_IDENTIFIER)
            + "-");
      }
    } catch (Exception e) {
      LOG.info("Error getting project for policy: " + projectPolicyID);
    }
    fileName.append("P" + projectPolicyID + "-");
    fileName.append(this.getSelectedYear() + "_");
    fileName.append(new SimpleDateFormat("yyyyMMdd-HHmm").format(new Date()));
    fileName.append(".pdf");
    return fileName.toString();
  }


  private String getInnovationDirectLink(String center, Long innovationId, String phaseId, Long projectId) {
    return this.getBaseUrl() + "/projects/" + center + "/innovation.do?innovationID=" + innovationId + "&phaseID="
      + phaseId + "&projectID=" + projectId;
  }

  @Override
  public InputStream getInputStream() {
    if (inputStream == null) {
      inputStream = new ByteArrayInputStream(bytesPDF);
    }

    return inputStream;
  }

  private TypedTableModel getMasterTableModel(String center, String date, String year) {
    // Initialization of Model
    TypedTableModel model = new TypedTableModel(new String[] {"center", "date", "year", "id", "cycle"},
      new Class[] {String.class, String.class, String.class, Long.class, String.class});
    model.addRow(new Object[] {center, date, year, projectPolicyID, this.getSelectedCycle()});
    return model;
  }

  private TypedTableModel getProjectPolicyTableModel() {
    TypedTableModel model = new TypedTableModel(
      new String[] {"id", "isRegional", "isNational", "title", "project", "narrative", "isNew", "stagePolicy",
        "policyType", "policyAmount", "geographicScope", "region", "countries", "projectExpectedStudy", "innovations",
        "descriptionStage", "hasMilestones", "milestones", "subIdos", "centers", "crps", "genderFocusLevel",
        "genderExplanation", "youthFocusLevel", "youthExplanation", "deliverableLink", "phaseID", "center"},
      new Class[] {Long.class, Boolean.class, Boolean.class, String.class, String.class, String.class, String.class,
        String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class,
        String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class,
        String.class, String.class, String.class, String.class, String.class},
      0);

    Long id = null;
    String title = null, narrative = null, stagePolicy = "", policyType = "", geographicScope = null, region = null,
      countries = null, projectExpectedStudies = "", descriptionStage = null, deliverables = null, crps = null,
      genderFocusLevel = null, genderExplaniation = null, youthFocusLevel = null, youthExplaniation = null,
      project = null, centers = "", hasMilestones = "", milestones = null, subIdos = null, deliverableLink = "",
      phaseID = "", loggedCenter = "", isNew = null, policyAmount = "";
    Boolean isRegional = false, isNational = false;

    // Id
    id = projectPolicyID;

    // phaseID
    phaseID = this.getSelectedPhase().getId().toString();

    // Center
    loggedCenter = this.getLoggedCrp().getAcronym();

    // Project
    if (projectPolicyInfo.getProjectPolicy().getProject() != null) {
      Project policyProject = projectPolicyInfo.getProjectPolicy().getProject();
      policyProject.getProjecInfoPhase(this.getSelectedPhase());
      project = policyProject.getComposedName();
    }
    // Title
    if (projectPolicyInfo.getTitle() != null && !projectPolicyInfo.getTitle().trim().isEmpty()) {
      title = projectPolicyInfo.getTitle();
    }

    // Narrative
    if (StringUtils.isNotBlank(projectPolicyInfo.getDescription())) {
      narrative = StringUtils.strip(projectPolicyInfo.getDescription());
    }
    // Stage
    if (projectPolicyInfo.getRepIndStageProcess() != null) {
      stagePolicy = projectPolicyInfo.getRepIndStageProcess().getName();
    }
    // Type
    if (projectPolicyInfo.getRepIndPolicyInvestimentType() != null) {
      policyType = projectPolicyInfo.getRepIndPolicyInvestimentType().getName();
    }

    // Studies
    List<ProjectExpectedStudyPolicy> studyPolicies = new ArrayList<>();

    // Expected Study Policies List
    if (projectExpectedStudyPolicyManager.findAll() != null && projectExpectedStudyPolicyManager.findAll().stream()
      .filter(p -> p != null && p.getPhase().getId().equals(this.getSelectedPhase().getId())
        && p.getProjectPolicy().getId().equals(projectPolicyInfo.getProjectPolicy().getId())) != null) {
      studyPolicies = projectExpectedStudyPolicyManager.findAll().stream()
        .filter(p -> p != null && p.getPhase().getId().equals(this.getActualPhase().getId())
          && p.getProjectPolicy().getId().equals(projectPolicyInfo.getProjectPolicy().getId()))
        .collect(Collectors.toList());
    }

    if (studyPolicies != null && !studyPolicies.isEmpty()) {
      for (ProjectExpectedStudyPolicy studyPolicy : studyPolicies) {
        String url = this.getExpectedStudyPDFLink(loggedCenter, studyPolicy.getProjectExpectedStudy().getId(),
          this.getSelectedPhase());
        projectExpectedStudies += "<br>&nbsp;&nbsp;&nbsp;&nbsp; ● " + studyPolicy.getProjectExpectedStudy().getId()
          + " - " + studyPolicy.getProjectExpectedStudy().getProjectExpectedStudyInfo(this.getActualPhase()).getTitle()
          + " <font color=\"blue\">(" + url + ")</font>";
      }

      projectExpectedStudies = StringUtils.trim(projectExpectedStudies);
    }

    // Is new Policy
    if (this.isPolicyNew(projectPolicyID) != null) {
      if (this.isPolicyNew(projectPolicyID) == true) {
        isNew = "Yes";
      } else {
        isNew = "No";
      }
    }

    // Has milestones
    if (projectPolicyInfo.getHasMilestones() != null) {
      if (projectPolicyInfo.getHasMilestones() == true) {
        hasMilestones = "Yes";
      } else if (projectPolicyInfo.getHasMilestones() == false) {
        hasMilestones = "No";
      }
    } else {
      hasMilestones = "&lt;Not Provided&gt;";
    }

    // List<ProjectInnovationMilestone> projectInnovationMilestoneList = new ArrayList<>();
    List<PolicyMilestone> policyMilestones = new ArrayList<>();
    policyMilestones = policyMilestoneManager.findAll().stream()
      .filter(pi -> pi.getPhase().getId().equals(this.getSelectedPhase().getId())
        && pi.getPolicy().getId().equals(projectPolicyInfo.getProjectPolicy().getId()))
      .collect(Collectors.toList());

    if (policyMilestones != null && !policyMilestones.isEmpty()) {
      Set<String> milestonesSet = new HashSet<>();
      for (PolicyMilestone milestone : policyMilestones) {
        if (milestone.getCrpMilestone() != null && milestone.getCrpMilestone().getTitle() != null) {
          milestonesSet.add("<br>&nbsp;&nbsp;&nbsp;&nbsp; ● " + milestone.getCrpMilestone().getTitle());
        }
      }

      milestones = String.join("", milestonesSet);
    } else {
      milestones = "&lt;Not Provided&gt;";
    }

    // Sub Idos
    List<ProjectPolicySubIdo> subIdosList = projectPolicySubIdoManager.findAll().stream()
      .filter(pi -> pi.getPhase().getId().equals(this.getSelectedPhase().getId())
        && pi.getProjectPolicy().getId().equals(projectPolicyInfo.getProjectPolicy().getId()))
      .collect(Collectors.toList());

    /*
     * List<ProjectInnovationSubIdo> subIdosList =
     * new ArrayList<>(projectPolicyInfo.getProjectInnovation().getProjectInnovationSubIdos().stream()
     * .filter(o -> o.getPhase().getId().equals(this.getSelectedPhase().getId())).collect(Collectors.toList()));
     */
    if (subIdosList != null && !subIdosList.isEmpty()) {
      Set<String> subIdosSet = new HashSet<>();
      for (ProjectPolicySubIdo subIdo : subIdosList) {
        subIdosSet.add("<br>&nbsp;&nbsp;&nbsp;&nbsp; ● " + subIdo.getSrfSubIdo().getId() + " - "
          + subIdo.getSrfSubIdo().getDescription());
      }

      subIdos = String.join("", subIdosSet);
    } else {
      subIdos = "&lt;Not Provided&gt;";
    }


    // Geographic Scope
    List<ProjectPolicyGeographicScope> projectPolicyGeographicScopes = projectPolicyGeographicScopeManager.findAll()
      .stream().filter(p -> p.getPhase().getId().equals(this.getSelectedPhase().getId())
        && p.getProjectPolicy() == projectPolicyInfo.getProjectPolicy())
      .collect(Collectors.toList());

    List<ProjectPolicyRegion> projectPolicyRegions = projectPolicyRegionManager.findAll().stream()
      .filter(p -> p.getPhase().getId().equals(this.getSelectedPhase().getId())
        && p.getProjectPolicy().getId().equals(projectPolicyID))
      .collect(Collectors.toList());

    List<ProjectPolicyCountry> projectPolicyCountries = projectPolicyCountryManager.findAll().stream()
      .filter(p -> p.getPhase().getId().equals(this.getSelectedPhase().getId())
        && p.getProjectPolicy().getId().equals(projectPolicyID))
      .collect(Collectors.toList());

    try {
      if (projectPolicyGeographicScopes != null) {
        if (projectPolicyGeographicScopes.get(0) != null
          && projectPolicyGeographicScopes.get(0).getRepIndGeographicScope() != null
          && projectPolicyGeographicScopes.get(0).getRepIndGeographicScope().getName() != null) {
          geographicScope = projectPolicyGeographicScopes.get(0).getRepIndGeographicScope().getName();
        }

        // Regional
        if (projectPolicyGeographicScopes != null && projectPolicyGeographicScopes.get(0) != null
          && projectPolicyGeographicScopes.get(0).getRepIndGeographicScope() != null
          && projectPolicyGeographicScopes.get(0).getRepIndGeographicScope().getId() != null) {
          if (projectPolicyGeographicScopes.get(0).getRepIndGeographicScope().getId()
            .equals(this.getReportingIndGeographicScopeRegional())) {
            isRegional = true;
            if (projectPolicyRegions != null && projectPolicyRegions.size() > 0) {
              Set<String> regionsSet = new HashSet<>();
              for (ProjectPolicyRegion innovationRegion : projectPolicyRegions) {
                regionsSet.add("<br>&nbsp;&nbsp;&nbsp;&nbsp; ● " + innovationRegion.getLocElement().getName());
              }
              region = String.join("", regionsSet);
            }
          }
        }
        // Country
        if (projectPolicyGeographicScopes.get(0).getRepIndGeographicScope() != null
          && projectPolicyGeographicScopes.get(0).getRepIndGeographicScope().getId() != null) {
          if (!projectPolicyGeographicScopes.get(0).getRepIndGeographicScope().getId()
            .equals(this.getReportingIndGeographicScopeGlobal())
            && !projectPolicyGeographicScopes.get(0).getRepIndGeographicScope().getId()
              .equals(this.getReportingIndGeographicScopeRegional())) {
            isNational = true;
            if (projectPolicyCountries != null && projectPolicyCountries.size() > 0) {
              Set<String> countriesSet = new HashSet<>();
              for (ProjectPolicyCountry innovationCountry : projectPolicyCountries) {
                countriesSet.add("<br>&nbsp;&nbsp;&nbsp;&nbsp; ● " + innovationCountry.getLocElement().getName());
              }
              countries = String.join("", countriesSet);
            }
          }
        }
        /*
         * if (region != null) {
         * geographicScope += region;
         * }
         * if (countries != null) {
         * geographicScope += countries;
         * }
         */
      }
    } catch (Exception e) {

    }

    // Amount
    if (projectPolicyInfo.getRepIndPolicyInvestimentType() != null
      && projectPolicyInfo.getRepIndPolicyInvestimentType().getId().longValue() == 3L) {
      if (projectPolicyInfo.getAmount() != null) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance();
        policyAmount = "USD " + formatter.format(projectPolicyInfo.getAmount());
      }
    } else {
      policyAmount = "&lt;Not Applicable&gt;";
    }

    // Narrative Evidence
    if (projectPolicyInfo.getRepIndStageProcess() != null
      && projectPolicyInfo.getRepIndStageProcess().getId().longValue() == 3L) {
      if (StringUtils.isNotBlank(projectPolicyInfo.getNarrativeEvidence())) {
        descriptionStage = StringUtils.strip(projectPolicyInfo.getNarrativeEvidence());
      } else {
        descriptionStage = "&lt;Not Provided&gt;";
      }
    } else {
      descriptionStage = "&lt;Not Applicable&gt;";
    }

    // Innovations
    List<ProjectPolicyInnovation> projectPolicyInnovations = projectPolicyInnovationManager.findAll().stream()
      .filter(p -> p.getProjectPolicy().getId().equals(projectPolicyInfo.getProjectPolicy().getId())
        && p.getPhase().getId().equals(this.getSelectedPhase().getId()))
      .collect(Collectors.toList());
    if (projectPolicyInnovations != null && projectPolicyInnovations.size() > 0) {
      Set<String> deliverablesSet = new HashSet<>();
      for (ProjectPolicyInnovation projectPolicyInnovation : projectPolicyInnovations) {
        if (projectPolicyInnovation.getProjectInnovation() != null
          && projectPolicyInnovation.getProjectInnovation().getId() != null
          && projectPolicyInnovation.getProjectInnovation().getProjectInnovationInfo(this.getSelectedPhase()) != null) {
          String url =
            this.getInnovationDirectLink(loggedCenter, projectPolicyInnovation.getProjectInnovation().getId(), phaseID,
              projectPolicyInfo.getProjectPolicy().getProject().getId());

          deliverablesSet
            .add("<br>&nbsp;&nbsp;&nbsp;&nbsp; ● I" + projectPolicyInnovation.getProjectInnovation().getId() + " - "
              + projectPolicyInnovation.getProjectInnovation().getProjectInnovationInfo().getTitle()
              + " <font color=\"blue\">(" + url + ")</font>");
        }
      }

      deliverables = String.join("", deliverablesSet);
    } else {
      deliverables = "&lt;Not Provided&gt;";
    }

    if (projectPolicyInfo.getProjectPolicy().getProjectPolicyCenters() != null) {
      projectPolicyInfo.getProjectPolicy()
        .setCenters(new ArrayList<>(projectPolicyInfo.getProjectPolicy().getProjectPolicyCenters().stream()
          .filter(c -> c.isActive() && c.getPhase().getId().equals(this.getSelectedPhase().getId()))
          .collect(Collectors.toList())));
    }

    if (projectPolicyInfo.getProjectPolicy().getCenters() != null
      && !projectPolicyInfo.getProjectPolicy().getCenters().isEmpty()) {
      Set<String> centerSet = new HashSet<>();

      for (ProjectPolicyCenter center : projectPolicyInfo.getProjectPolicy().getCenters()) {
        if (center.getInstitution() != null && center.getInstitution().getComposedName() != null) {
          centerSet.add("<br>&nbsp;&nbsp;&nbsp;&nbsp; ● " + center.getInstitution().getComposedName());
        }
      }

      centers = String.join("", centerSet);
    } else {
      centers = "&lt;Not Provided&gt;";
    }

    // Contributions CRPS/Platforms
    List<ProjectPolicyCrp> projectPolicyCrps = projectPolicyInfo.getProjectPolicy().getProjectPolicyCrps().stream()
      .filter(o -> o.isActive() && o.getPhase() != null && o.getPhase().equals(this.getSelectedPhase()))
      .collect(Collectors.toList());
    if (projectPolicyCrps != null && projectPolicyCrps.size() > 0) {
      Set<String> crpsSet = new HashSet<>();
      for (ProjectPolicyCrp projectPolicyCrp : projectPolicyCrps) {
        crpsSet.add("<br>&nbsp;&nbsp;&nbsp;&nbsp; ● " + projectPolicyCrp.getGlobalUnit().getComposedName());
      }

      crps = String.join("", crpsSet);
    } else {
      crps = "&lt;Not Provided&gt;";
    }

    // Cross cutting Relevance (not used)
    if (projectPolicyInfo.getProjectPolicy().getProjectPolicyCrossCuttingMarkers() != null
      && !projectPolicyInfo.getProjectPolicy().getProjectPolicyCrossCuttingMarkers().isEmpty()) {
      for (ProjectPolicyCrossCuttingMarker crossCuttingMarker : projectPolicyInfo.getProjectPolicy()
        .getProjectPolicyCrossCuttingMarkers()) {
        if (crossCuttingMarker != null && crossCuttingMarker.getCgiarCrossCuttingMarker() != null
          && crossCuttingMarker.getCgiarCrossCuttingMarker().getId() != null) {
          switch (crossCuttingMarker.getCgiarCrossCuttingMarker().getId().intValue()) {
            case 1:
              if (crossCuttingMarker.getRepIndGenderYouthFocusLevel() != null) {
                genderFocusLevel = crossCuttingMarker.getRepIndGenderYouthFocusLevel().getPowbName();
              }

              break;

            case 2:
              if (crossCuttingMarker.getRepIndGenderYouthFocusLevel() != null) {
                youthFocusLevel = crossCuttingMarker.getRepIndGenderYouthFocusLevel().getPowbName();
              }

              break;

            case 3:
              break;

            case 4:
              break;

            default:
              break;
          }
        }
      }
    }

    model.addRow(new Object[] {id, isRegional, isNational, title, project, narrative, isNew, stagePolicy, policyType,
      policyAmount, geographicScope, region, countries, projectExpectedStudies, deliverables, descriptionStage,
      hasMilestones, milestones, subIdos, centers, crps, genderFocusLevel, genderExplaniation, youthFocusLevel,
      youthExplaniation, deliverableLink, phaseID, loggedCenter});
    return model;
  }

  @Override
  public void prepare() throws Exception {
    Map<String, Parameter> parameters = this.getParameters();
    this.setPublicAccessParameters();
    projectPolicyID =
      Long.parseLong(StringUtils.trim(parameters.get(APConstants.POLICY_REQUEST_ID).getMultipleValues()[0]));
    // Calculate time to generate report
    startTime = System.currentTimeMillis();
    LOG.info("Start report download: " + this.getFileName() + ". User: " + this.getDownloadByUser() + ". CRP: "
      + this.getLoggedCrp().getAcronym());
  }

  public void setBytesPDF(byte[] bytesPDF) {
    this.bytesPDF = bytesPDF;
  }

  public void setInputStream(InputStream inputStream) {
    this.inputStream = inputStream;
  }


}