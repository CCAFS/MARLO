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

import org.cgiar.ccafs.marlo.action.report.MicroserviceReportAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.InstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.LocElementManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyInnovationManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationContributingOrganizationManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationCountryManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationDeliverableManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationGeographicScopeManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationMilestoneManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationRegionManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationSubIdoManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndInnovationTypeManager;
import org.cgiar.ccafs.marlo.data.manager.ReportConfigurationManager;
import org.cgiar.ccafs.marlo.data.manager.ScalingReadinessManager;
import org.cgiar.ccafs.marlo.data.model.Institution;
import org.cgiar.ccafs.marlo.data.model.InstitutionLocation;
import org.cgiar.ccafs.marlo.data.model.InstitutionType;
import org.cgiar.ccafs.marlo.data.model.LocElement;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyInfo;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyInnovation;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovation;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationActor;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationAllianceLevers;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationAllianceOrganization;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationCenter;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationContributingOrganization;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationCountry;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationCrp;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationCrpOutcome;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationDeliverable;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationGeographicScope;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationImpactArea;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationInfo;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationMilestone;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationOrganization;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationPartnership;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationReference;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationReferenceComplementarySolution;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationReferenceUrl;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationRegion;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationSDG;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationShared;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationSubIdo;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationToolCategory;
import org.cgiar.ccafs.marlo.data.model.ProjectPartner;
import org.cgiar.ccafs.marlo.data.model.ProjectPartnerPerson;
import org.cgiar.ccafs.marlo.data.model.RepIndDegreeInnovation;
import org.cgiar.ccafs.marlo.data.model.RepIndInnovationNature;
import org.cgiar.ccafs.marlo.data.model.RepIndInnovationType;
import org.cgiar.ccafs.marlo.data.model.RepIndPhaseResearchPartnership;
import org.cgiar.ccafs.marlo.data.model.RepIndRegion;
import org.cgiar.ccafs.marlo.data.model.RepIndStageInnovation;
import org.cgiar.ccafs.marlo.data.model.ReportConfiguration;
import org.cgiar.ccafs.marlo.data.model.ScalingReadiness;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.URLShortener;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TimeZone;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.dispatcher.Parameter;
import org.jfree.util.Log;
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
public class ProjectInnovationSummaryAction extends BaseSummariesAction implements Summary {

  private static final long serialVersionUID = 1L;
  private static Logger LOG = LoggerFactory.getLogger(ProjectInnovationSummaryAction.class);

  /**
   * Removes the first occurrence of ';' in a string only if there is no text before it.
   * Leading spaces before the ';' are ignored.
   *
   * @param input The original string to process.
   * @return The modified string with the first ';' removed if applicable.
   */
  public static String removeLeadingSemicolon(String input) {
    if (input == null) {
      return null; // Return null if the input is null
    }
    return input.replaceFirst("^\\s*;", "");
  }

  // Managers
  private final ProjectInnovationContributingOrganizationManager projectInnovationContributingOrganizationManager;
  private final ProjectInnovationManager projectInnovationManager;
  private final ProjectInnovationDeliverableManager projectInnovationDeliverableManager;
  private final ResourceManager resourceManager;
  private final ProjectInnovationGeographicScopeManager projectInnovationGeographicScopeManager;
  private final ProjectInnovationRegionManager projectInnovationRegionManager;
  private final ProjectInnovationCountryManager projectInnovationCountryManager;
  private final ProjectInnovationMilestoneManager projectInnovationMilestoneManager;
  private final ProjectInnovationSubIdoManager projectInnovationSubIdoManager;
  private ProjectExpectedStudyInnovationManager projectExpectedStudyInnovationManager;
  private final ReportConfigurationManager reportConfigurationManager;
  private final RepIndInnovationTypeManager repIndInnovationTypeManager;
  private final InstitutionManager institutionManager;
  private final LocElementManager locElementManager;
  private final ScalingReadinessManager scalingReadinessManager;

  private final MicroserviceReportAction microserviceReportAction;
  // Parameters
  private long startTime;
  private Long projectInnovationID;
  private ProjectInnovationInfo projectInnovationInfo;
  private String innovationsTemplateData = null;
  private String innovationsReportName = null;

  private String bucketName = null;
  // XLSX bytes
  private byte[] bytesPDF;

  // Streams
  InputStream inputStream;

  @Inject
  public ProjectInnovationSummaryAction(APConfig config, GlobalUnitManager crpManager,
    ProjectInnovationManager projectInnovationManager, PhaseManager phaseManager, ResourceManager resourceManager,
    ProjectManager projectManager,
    ProjectInnovationContributingOrganizationManager projectInnovationContributingOrganizationManager,
    ProjectInnovationDeliverableManager projectInnovationDeliverableManager,
    ProjectInnovationGeographicScopeManager projectInnovationGeographicScopeManager,
    ProjectInnovationRegionManager projectInnovationRegionManager,
    ProjectInnovationCountryManager projectInnovationCountryManager,
    ProjectInnovationMilestoneManager projectInnovationMilestoneManager,
    ProjectInnovationSubIdoManager projectInnovationSubIdoManager,
    ProjectExpectedStudyInnovationManager projectExpectedStudyInnovationManager,
    ReportConfigurationManager reportConfigurationManager, MicroserviceReportAction microserviceReportAction,
    InstitutionManager institutionManager, LocElementManager locElementManager,
    RepIndInnovationTypeManager repIndInnovationTypeManager, ScalingReadinessManager scalingReadinessManager) {
    super(config, crpManager, phaseManager, projectManager);
    this.projectInnovationManager = projectInnovationManager;
    this.resourceManager = resourceManager;
    this.projectInnovationContributingOrganizationManager = projectInnovationContributingOrganizationManager;
    this.projectInnovationDeliverableManager = projectInnovationDeliverableManager;
    this.projectInnovationGeographicScopeManager = projectInnovationGeographicScopeManager;
    this.projectInnovationRegionManager = projectInnovationRegionManager;
    this.projectInnovationCountryManager = projectInnovationCountryManager;
    this.projectInnovationMilestoneManager = projectInnovationMilestoneManager;
    this.projectInnovationSubIdoManager = projectInnovationSubIdoManager;
    this.projectExpectedStudyInnovationManager = projectExpectedStudyInnovationManager;
    this.reportConfigurationManager = reportConfigurationManager;
    this.microserviceReportAction = microserviceReportAction;
    this.institutionManager = institutionManager;
    this.locElementManager = locElementManager;
    this.repIndInnovationTypeManager = repIndInnovationTypeManager;
    this.scalingReadinessManager = scalingReadinessManager;
  }


  /**
   * Method to add i8n parameters to masterReport in Pentaho
   * 
   * @param masterReport
   * @return masterReport with i8n parameters added
   */
  private MasterReport addi8nParameters(MasterReport masterReport) {
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
    masterReport.getParameterValues().put("i8nInnovationHasMilestones",
      this.getText("projectInnovations.hasMilestones"));
    masterReport.getParameterValues().put("i8nInnovationMilestones", this.getText("projectInnovations.milestones"));
    masterReport.getParameterValues().put("i8nInnovationSubIdos", this.getText("projectInnovations.subIdos"));

    masterReport.getParameterValues().put("i8nInnovationLeadOrganization",
      this.getText("projectInnovations.leadOrganization"));
    masterReport.getParameterValues().put("i8nInnovationContributionOrganization",
      this.getText("projectInnovations.contributingOrganizations"));
    masterReport.getParameterValues().put("i8nInnovationContributionCenters",
      this.getText("projectInnovations.contributingCenters"));
    masterReport.getParameterValues().put("i8nInnovationAdaptativeResearch", "projectInnovations.adaptativeResearch");
    masterReport.getParameterValues().put("i8nInnovationREvidenceLink",
      this.getText("summaries.innovation.evidenceLink"));
    masterReport.getParameterValues().put("i8nInnovationRDeliverables",
      this.getText("projectInnovations.deliverableId"));
    masterReport.getParameterValues().put("i8nInnovationRCrps", this.getText("projectInnovations.contributing"));
    masterReport.getParameterValues().put("i8nInnovationRGenderFocusLevel",
      this.getText("projectInnovations.genderRelevance"));
    masterReport.getParameterValues().put("i8nInnovationRNew", this.getText("projectInnovations.isNew"));
    masterReport.getParameterValues().put("i8nInnovationRYouthFocusLevel",
      this.getText("projectInnovations.youthRelevance"));
    masterReport.getParameterValues().put("i8nInnovationRYouthExplaniation",
      this.getText("projectInnovations.youthRelevance.explanation.readText"));
    masterReport.getParameterValues().put("i8nInnovationRNew",
      this.getText("projectInnovations.youthRelevance.explanation.readText"));
    masterReport.getParameterValues().put("i8nProject", this.getText("summaries.oaprojects.projectTitle"));
    return masterReport;
  }

  /**
   * Converts a Boolean value to its corresponding String representation.
   * If the value is `true`, it returns `"true"`.
   * If the value is `false`, it returns `"false"`.
   * If the value is `null`, it returns `null`.
   *
   * @param value the Boolean value to convert
   * @return "true" if the value is `true`, "false" if the value is `false`, or `null` if the value is `null`
   */
  private String booleanToString(Boolean value) {
    return value != null ? (value ? "true" : "false") : null;
  }

  @Override
  public String execute() throws Exception {

    if (this.getSelectedPhase() == null) {
      return NOT_FOUND;
    }

    if (projectInnovationID == null || projectInnovationManager.getProjectInnovationById(projectInnovationID) == null
      || projectInnovationManager.getProjectInnovationById(projectInnovationID)
        .getProjectInnovationInfo(this.getSelectedPhase()) == null) {
      LOG.error("Project Innovation " + projectInnovationID + " Not found");
      return NOT_FOUND;
    } else {
      projectInnovationInfo = projectInnovationManager.getProjectInnovationById(projectInnovationID)
        .getProjectInnovationInfo(this.getSelectedPhase());
    }
    ByteArrayOutputStream os = new ByteArrayOutputStream();
    if ((this.getCurrentUser() != null)
      && (this.hasSpecificities(APConstants.GENERATE_PENTAHO_INNOVATIONS_REPORT_ACTIVE))) {

      try {
        Resource reportResource = resourceManager
          .createDirectly(this.getClass().getResource("/pentaho/crp/ProjectInnovationPDF.prpt"), MasterReport.class);
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
        this.fillSubreport((SubReport) hm.get("project_innovation"), "project_innovation");
        PdfReportUtil.createPDF(masterReport, os);
        bytesPDF = os.toByteArray();
        os.close();
      } catch (Exception e) {
        LOG.error("Error generating ProjectInnovation Summary: " + e.getMessage());
        throw e;
      }
      // Calculate time of generation
      long stopTime = System.currentTimeMillis();
      stopTime = stopTime - startTime;
      LOG.info("Downloaded successfully: " + this.getFileName() + ". User: " + this.getDownloadByUser() + ". CRP: "
        + this.getLoggedCrp().getAcronym() + ". Time to generate: " + stopTime + "ms.");
    } else {
      // Specificity false
      try {
        this.generateAndSendJson();
        bytesPDF = os.toByteArray();
      } catch (Exception e) {
        if (e.getClass().getName().contains("ClientAbortException")) {
          System.out.println("Client aborted the connection: " + e.getMessage());
        } else {
          System.out.println("Exception while generating JSON: " + e.getMessage());
          throw e;
        }
      } finally {
        try {
          os.close();
        } catch (Exception e) {
          System.out.println("Error closing output stream: " + e.getMessage());
        }
      }
    }
    return SUCCESS;
  }


  private void fillSubreport(SubReport subReport, String query) {
    CompoundDataFactory cdf = CompoundDataFactory.normalize(subReport.getDataFactory());
    TableDataFactory sdf = (TableDataFactory) cdf.getDataFactoryForQuery(query);
    TypedTableModel model = null;
    switch (query) {
      case "project_innovation":
        model = this.getProjectInnovationTableModel();
        break;
    }
    sdf.addTable(query, model);
    subReport.setDataFactory(cdf);
  }

  public String generateAndSendJson() {
    ObjectMapper objectMapper = new ObjectMapper();

    String innovationID = null, title = null, narrative = null, innovationImportance = null, year = null,
      impactAreaCode = null;
    Long projectID = null;
    String phaseID = null, projectAcronym = null;
    ProjectInnovation innovation = null;
    Project project = null;
    final Phase phase = this.getSelectedPhase();
    boolean clearLead = false, haveRegions = false, haveCountries = false;
    RepIndPhaseResearchPartnership repIndPhaseResearchPartnership = null;
    RepIndStageInnovation repIndStageInnovation = null;
    RepIndRegion repIndRegion = null;
    RepIndInnovationType repIndInnovationType = null;
    RepIndInnovationNature repIndInnovationNature = null;
    RepIndDegreeInnovation repIndDegreeInnovation = null;
    Institution leadOrganization = null;
    String geographicScopes = null, regions = null, countries = null;
    String centersJson = null, studiesJson = null, sharedInnovationsJson = null, allianceLeversJson = null,
      sdgsJson = null, impactAreasJson = null, allianceOrganizationsJson = null, actorsJson = null,
      toolCategoriesJson = null, referencesJson = null, referenceUrlsJson = null,
      referenceComplementarySolutionsJson = null, crpOutcomesJson = null, partnersJson = null,
      partnerInstitutionsJson = null, partnerPersonsJson = null, myProjectsJson = null, partnershipsJson = null,
      contributingOrganizationsJson = null, isAllianceContribution = null, deliverablesJson = null,
      intellectualProperty = null, hasFurtherDevelopment = null, hasLegalRestrictions = null, hasAssetPotential = null,
      hasCgiarContribution = null, beneficiariesNarrative = null, reasonNotCgiarContribution = null,
      scalingReadiness = null, knowledgeMethodsAndToolsNarrative = null, knowledgeResultsNarrative = null;

    List<ProjectInnovationDeliverable> deliverables = new ArrayList<>();
    List<ProjectInnovationContributingOrganization> contributingOrganizations = new ArrayList<>();
    List<ProjectInnovationCenter> centers = new ArrayList<>();
    List<ProjectInnovationAllianceLevers> allianceLevers = new ArrayList<>();
    List<ProjectInnovationSDG> sdgs = new ArrayList<>();
    List<ProjectInnovationImpactArea> impactAreas = new ArrayList<>();
    List<ProjectInnovationAllianceOrganization> allianceOrganizations = new ArrayList<>();
    List<ProjectInnovationActor> actors = new ArrayList<>();
    List<ProjectInnovationToolCategory> toolCategories = new ArrayList<>();
    List<ProjectInnovationReference> references = new ArrayList<>();
    List<ProjectInnovationReferenceUrl> referenceUrls = new ArrayList<>();
    List<ProjectInnovationReferenceComplementarySolution> referenceComplementarySolutions = new ArrayList<>();
    List<ProjectInnovationCrpOutcome> crpOutcomes = new ArrayList<>();
    List<ProjectPartner> partners = new ArrayList<>();
    List<Institution> partnerInstitutions = new ArrayList<>();
    List<ProjectPartnerPerson> partnerPersons = new ArrayList<>();
    List<Project> myProjects = new ArrayList<>();
    List<ProjectInnovationPartnership> partnerships = new ArrayList<>();

    try {

      innovationID = StringUtils.trim(this.getRequest().getParameter(APConstants.INNOVATION_REQUEST_ID));
      innovation = projectInnovationInfo.getProjectInnovation();
      if (innovation != null) {
        projectID = innovation.getProject().getId();
        project = projectManager.getProjectById(projectID);
        projectAcronym = project.getAcronym();
        phaseID = phase.getId().toString();
        title = projectInnovationInfo.getTitle();
        narrative = projectInnovationInfo.getNarrative();
        innovationImportance = projectInnovationInfo.getInnovationImportance();
        year = projectInnovationInfo.getYear() + "";
        intellectualProperty =
          (projectInnovationInfo != null && projectInnovationInfo.getIntellectualPropertyInstitution() != null)
            ? projectInnovationInfo.getIntellectualPropertyInstitution().getAcronym() : null;
        hasLegalRestrictions =
          this.booleanToString(projectInnovationInfo != null ? projectInnovationInfo.getHasLegalRestrictions() : null);
        hasAssetPotential =
          this.booleanToString(projectInnovationInfo != null ? projectInnovationInfo.getHasAssetPotential() : null);
        hasFurtherDevelopment =
          this.booleanToString(projectInnovationInfo != null ? projectInnovationInfo.getHasFurtherDevelopment() : null);
        hasCgiarContribution =
          this.booleanToString(projectInnovationInfo != null ? projectInnovationInfo.getHasCgiarContribution() : null);
        reasonNotCgiarContribution =
          (projectInnovationInfo != null && projectInnovationInfo.getReasonNotCgiarContribution() != null)
            ? projectInnovationInfo.getReasonNotCgiarContribution() : null;
        beneficiariesNarrative =
          (projectInnovationInfo != null && projectInnovationInfo.getBeneficiariesNarrative() != null)
            ? projectInnovationInfo.getBeneficiariesNarrative() : null;
        knowledgeMethodsAndToolsNarrative =
          (projectInnovationInfo != null && projectInnovationInfo.getKnowledgeMethodsAndToolsNarrative() != null)
            ? projectInnovationInfo.getKnowledgeMethodsAndToolsNarrative() : null;
        knowledgeResultsNarrative =
          (projectInnovationInfo != null && projectInnovationInfo.getKnowledgeResultsNarrative() != null)
            ? projectInnovationInfo.getKnowledgeResultsNarrative() : null;

        try {
          int scalingReadinessId = (projectInnovationInfo != null && projectInnovationInfo.getReadinessScale() != null)
            ? projectInnovationInfo.getReadinessScale() : 0;
          ScalingReadiness scalingReadinessObj = scalingReadinessManager.getScalingReadinessById(scalingReadinessId);
          if (scalingReadinessObj != null && scalingReadinessObj.getComposedName() != null) {
            scalingReadiness = scalingReadinessObj.getComposedName().split(" - ")[0].trim();
          }
        } catch (Exception e) {
          System.out.println("error getting readiness scale " + e);
        }

        try {
          innovation.setProjectInnovationShareds(
            innovation.getProjectInnovationShareds() != null ? innovation.getProjectInnovationShareds().stream()
              .filter(ProjectInnovationShared::isActive).collect(Collectors.toSet()) : new HashSet<>());

          innovation.setOrganizations(innovation.getProjectInnovationOrganizations() != null
            ? innovation.getProjectInnovationOrganizations().stream().collect(Collectors.toList()) : null);

        } catch (Exception e) {
          Log.error("Error getting shared projects info");
        }

        try {
          // Innovations alliance organizations
          if (innovation.getProjectInnovationAllianceOrganizations() != null) {
            innovation
              .setAllianceOrganizations(new ArrayList<>(innovation.getProjectInnovationAllianceOrganizations().stream()
                .filter(o -> o.isActive() && o.getPhase().getId().equals(phase.getId())).collect(Collectors.toList())));
          }
        } catch (Exception e) {
          System.out.println("error setting alliance organizations " + e);
        }
        // Innovations references
        if (innovation.getProjectInnovationReferences() != null
          && !innovation.getProjectInnovationReferences().isEmpty()) {
          try {
            Set<Long> referenceIds = new HashSet<>();

            innovation.setReferences(innovation.getProjectInnovationReferences().stream()
              .filter(o -> o.isActive() && o.getPhase() != null && phase.getId().equals(o.getPhase().getId()))
              .filter(o -> o.getId() != null).sorted(Comparator.comparing(ProjectInnovationReference::getId))
              .filter(o -> referenceIds.add(o.getId())).collect(Collectors.toList()));

          } catch (Exception e) {
            System.out.println("error setting references " + e);
          }
        }

        try {
          // Innovations references URL
          if (innovation.getProjectInnovationReferenceUrls() != null
            && !innovation.getProjectInnovationReferenceUrls().isEmpty()) {
            try {
              Set<Long> referenceUrlIds = new HashSet<>();

              innovation.setReferenceUrls(innovation.getProjectInnovationReferenceUrls().stream()
                .filter(o -> o.isActive() && o.getPhase() != null && phase.getId().equals(o.getPhase().getId()))
                .filter(o -> o.getId() != null).sorted(Comparator.comparing(ProjectInnovationReferenceUrl::getId))
                .filter(o -> referenceUrlIds.add(o.getId())).collect(Collectors.toList()));

            } catch (Exception e) {
              e.printStackTrace();
            }
          }
        } catch (Exception e) {
          System.out.println("error setting references url " + e);
        }

        // Innovations actors
        if (innovation.getProjectInnovationActors() != null) {
          innovation.setActors(new ArrayList<>(innovation.getProjectInnovationActors().stream()
            .filter(o -> o.isActive() && o.getPhase().getId().equals(phase.getId())).collect(Collectors.toList())));
        }

        // Expected Study crp Outcome list
        if (innovation.getProjectInnovationCrpOutcomes() != null) {
          innovation.setCrpOutcomes(new ArrayList<>(innovation.getProjectInnovationCrpOutcomes().stream()
            .filter(o -> o.getPhase().getId().equals(phase.getId())).collect(Collectors.toList())));
        }

        // Geographic Scopes
        boolean isNational = false;
        boolean isRegional = false;

        final List<ProjectInnovationGeographicScope> geographicScopeList =
          innovation.getProjectInnovationGeographicScopes().stream()
            .filter(s -> s.isActive() && (s.getPhase() != null) && s.getPhase().equals(phase))
            .collect(Collectors.toList());
        final Set<String> geographicScopeSet = new HashSet<>();
        if ((geographicScopeList != null) && !geographicScopeList.isEmpty()) {
          for (final ProjectInnovationGeographicScope geographicScope : geographicScopeList) {
            if (!geographicScope.getRepIndGeographicScope().getId().equals(this.getReportingIndGeographicScopeGlobal())
              && !geographicScope.getRepIndGeographicScope().getId()
                .equals(this.getReportingIndGeographicScopeRegional())) {
              isNational = true;
            }
            if (geographicScope.getRepIndGeographicScope().getId()
              .equals(this.getReportingIndGeographicScopeRegional())) {
              isRegional = true;
            }
            geographicScopeSet.add(geographicScope.getRepIndGeographicScope().getName());
          }
          geographicScopes = String.join(", ", geographicScopeSet);
        }

        // Country(s)
        if (isNational) {
          final List<ProjectInnovationCountry> studyCountries =
            this.projectInnovationCountryManager.getInnovationCountrybyPhase(innovation.getId(), phase.getId()).stream()
              .filter(le -> le.isActive() && (le.getLocElement().getLocElementType().getId() == 2))
              .collect(Collectors.toList());
          if ((studyCountries != null) && !studyCountries.isEmpty()) {
            final Set<String> countriesSet = new HashSet<>();
            for (final ProjectInnovationCountry projectInnovationCountry : studyCountries) {
              countriesSet.add("; " + projectInnovationCountry.getLocElement().getName());
            }
            countries = String.join("", countriesSet);
          }
        }

        // Region(s)
        if (isRegional) {
          final List<ProjectInnovationRegion> studyRegions = innovation.getProjectInnovationRegions().stream()
            .filter(c -> c.isActive() && (c.getPhase() != null) && c.getPhase().equals(phase))
            .collect(Collectors.toList());
          if ((studyRegions != null) && !studyRegions.isEmpty()) {
            final Set<String> regionsSet = new HashSet<>();
            for (final ProjectInnovationRegion projectInnovationRegion : studyRegions) {
              regionsSet.add("; " + projectInnovationRegion.getLocElement().getName());
            }
            regions = String.join("", regionsSet);
          }
        }

        // Impact Area
        try {
          if (innovation.getProjectInnovationImpactAreas() != null) {
            innovation.setImpactAreas(new ArrayList<>(innovation.getProjectInnovationImpactAreas().stream()
              .filter(o -> o.isActive() && o.getPhase().getId().equals(phase.getId())).collect(Collectors.toList())));

            // Prepare list of impact area IDs
            List<Long> impactAreaIds = new ArrayList<>();

            if (innovation.getImpactAreas() != null && !innovation.getImpactAreas().isEmpty()) {
              for (ProjectInnovationImpactArea impactAreaItem : innovation.getImpactAreas()) {
                if (impactAreaItem != null && impactAreaItem.getImpactArea() != null
                  && impactAreaItem.getImpactArea().getId() != null) {
                  impactAreaIds.add(impactAreaItem.getImpactArea().getId());
                }
              }
            }

            // Convert list to JSON string with brackets
            impactAreaCode = objectMapper.writeValueAsString(impactAreaIds);
          }
        } catch (final NullPointerException e) {
          System.out.println("NullPointerException while getting Impact Areas: " + e);
        } catch (final Exception e) {
          System.out.println("Unexpected error while getting Impact Areas: " + e);
        }

        // Innovations tool categories
        if (innovation.getProjectInnovationToolCategories() != null) {
          innovation.setToolCategories(new ArrayList<>(innovation.getProjectInnovationToolCategories().stream()
            .filter(o -> o.isActive() && o.getPhase().getId().equals(phase.getId())).collect(Collectors.toList())));
        }

        // Contribution Organization
        // Innovation Contributing organizations List
        if (innovation.getProjectInnovationContributingOrganization() != null) {
          innovation
            .setContributingOrganizations(new ArrayList<>(innovation.getProjectInnovationContributingOrganization()
              .stream().filter(d -> d.getPhase().getId().equals(phase.getId()))
              .sorted(
                (o1, o2) -> o1.getInstitution().getComposedName().compareTo(o2.getInstitution().getComposedName()))
              .collect(Collectors.toList())));
        }

        // Innovation Center list
        if (innovation.getProjectInnovationCenters() != null) {
          innovation.setCenters(new ArrayList<>(innovation.getProjectInnovationCenters().stream()
            .filter(c -> c.isActive() && c.getPhase().getId().equals(phase.getId())).collect(Collectors.toList())));
        }

        try {
          // Innovation Crp list
          if (innovation.getProjectInnovationCrps() != null) {
            innovation.setCrps(new ArrayList<>(innovation.getProjectInnovationCrps().stream()
              .filter(c -> c.isActive() && c.getPhase().getId().equals(phase.getId())).collect(Collectors.toList())));
          }
        } catch (Exception e) {
          Log.error("error setting crps " + e);
        }
        try {
          // Innovation Deliverable List
          if (innovation.getProjectInnovationDeliverables() != null) {
            innovation.setDeliverables(new ArrayList<>(innovation.getProjectInnovationDeliverables().stream()
              .filter(d -> d.isActive() && d.getPhase().getId() == phase.getId()).collect(Collectors.toList())));
          }

        } catch (Exception e) {
          Log.error("error getting deliverables innovations " + e);
        }

        try {
          // Innovation shared Projects List
          if (innovation.getProjectInnovationShareds() != null) {
            innovation.setSharedInnovations(new ArrayList<>(innovation.getProjectInnovationShareds().stream()
              .filter(o -> o.isActive() && o.getPhase().getId().equals(phase.getId())).collect(Collectors.toList())));
          }
        } catch (Exception e) {
          Log.error("error getting shared innovations " + e);
        }


        try {
          // load InnovationType
          if (projectInnovationInfo.getRepIndInnovationType() != null
            && projectInnovationInfo.getRepIndInnovationType().getId() != null) {
            projectInnovationInfo.setRepIndInnovationType(repIndInnovationTypeManager
              .getRepIndInnovationTypeById(projectInnovationInfo.getRepIndInnovationType().getId()));
          }
        } catch (Exception e) {
          Log.error("error getting shared innovations " + e);
        }

        try {
          // Innovations Alliance levers
          if (innovation.getProjectInnovationAllianceLevers() != null) {
            innovation.setAllianceLevers(new ArrayList<>(innovation.getProjectInnovationAllianceLevers().stream()
              .filter(o -> o.isActive() && o.getPhase().getId().equals(phase.getId())).collect(Collectors.toList())));
          }

          allianceLevers = null;
        } catch (Exception e) {
          Log.error("error getting strategic outcomes " + e);
        }

        try {
          // Innovations SDGs
          if (innovation.getProjectInnovationSDGs() != null) {
            innovation.setSdgs(new ArrayList<>(innovation.getProjectInnovationSDGs().stream()
              .filter(o -> o.isActive() && o.getPhase().getId().equals(phase.getId())).collect(Collectors.toList())));
          }
        } catch (Exception e) {
          Log.error("error getting SDGs " + e);
        }

        try {
          // Innovations references Complementary solutions
          if (innovation.getProjectInnovationReferenceComplementarySolutions() != null
            && !innovation.getProjectInnovationReferenceComplementarySolutions().isEmpty()) {

            innovation
              .setReferenceComplementarySolutions(innovation.getProjectInnovationReferenceComplementarySolutions()
                .stream().filter(o -> o.isActive() && o.getPhase().getId().equals(phase.getId()))
                .sorted(Comparator.comparing(o -> o.getId())).collect(Collectors.toList()));
          }
        } catch (Exception e) {
          Log.error("error getting Complementary solutions " + e);
        }

        contributingOrganizations = innovation.getContributingOrganizations(); // getContributingOrganizations()
        impactAreas = innovation.getImpactAreas(); // getImpactAreas()
        repIndInnovationNature = projectInnovationInfo.getRepIndInnovationNature();
        repIndInnovationType = projectInnovationInfo.getRepIndInnovationType();
        deliverables = innovation.getDeliverables(); // getDeliverables()
        centers = innovation.getCenters(); // getCenters()
        allianceLevers = innovation.getAllianceLevers(); // getAllianceLevers()
        sdgs = innovation.getSdgs(); // getSdgs()
        allianceOrganizations = innovation.getAllianceOrganizations(); // getAllianceOrganizations()
        actors = innovation.getActors(); // getActors()
        toolCategories = innovation.getToolCategories(); // getToolCategories()
        references = innovation.getReferences(); // getReferences()
        referenceUrls = innovation.getReferenceUrls(); // getReferenceUrls()
        referenceComplementarySolutions = innovation.getReferenceComplementarySolutions(); // getReferenceComplementarySolutions()
        crpOutcomes = innovation.getCrpOutcomes(); // getCrpOutcomes()

        // Is Alliance Contribution
        isAllianceContribution = Boolean.TRUE.equals(this.isAllianceSelected(innovation)) ? "Yes" : "No";

      }
    } catch (Exception e) {
      System.out.println("error setting innovation report variables " + e);
    }

    // JSON list

    try {
      deliverablesJson = objectMapper.writeValueAsString(deliverables != null ? deliverables : null);
    } catch (Exception e) {
      System.out.println("error in deliverables json " + e);
    }

    try {
      List<Map<String, String>> contributingOrganizationsDTO = contributingOrganizations.stream().map(org -> {
        Map<String, String> dto = new HashMap<>();

        dto.put("name", Optional.ofNullable(org.getInstitution()).map(Institution::getComposedName).orElse(null));
        dto.put("type", Optional.ofNullable(org.getInstitution()).map(Institution::getInstitutionType)
          .map(InstitutionType::getName).orElse(null));

        try {
          if (org.getInstitution() != null) {
            org.getInstitution().setLocations(new ArrayList<>());

            if (org.getInstitution().getInstitutionsLocations() != null
              && !org.getInstitution().getInstitutionsLocations().isEmpty()) {

              org.getInstitution().getLocations().addAll(org.getInstitution().getInstitutionsLocations().stream()
                .filter(InstitutionLocation::isActive).collect(Collectors.toList()));
            }
          }
        } catch (Exception e) {
          System.out.println("Error setting locations: " + e.getMessage());
          e.printStackTrace();
        }

        String headquarter = Optional.ofNullable(org.getInstitution()).map(Institution::getLocations).flatMap(
          locations -> locations.stream().filter(loc -> loc.isHeadquater() && loc.getLocElement() != null).map(loc -> {
            LocElement locElement = locElementManager.getLocElementById(loc.getLocElement().getId());
            return (locElement != null) ? locElement.getName() : null;
          }).filter(Objects::nonNull).findFirst()).orElse(null);

        dto.put("headquarter", headquarter);

        return dto;
      }).collect(Collectors.toList());

      contributingOrganizationsJson = objectMapper.writeValueAsString(contributingOrganizationsDTO);
    } catch (Exception e) {
      System.out.println("Contribution organizations json " + e.getMessage());
      e.printStackTrace();
    }


    try {
      List<Map<String, String>> centersDTO = centers.stream().map(center -> {
        Map<String, String> dto = new HashMap<>();

        dto.put("name", (center.getInstitution() != null && center.getInstitution().getName() != null)
          ? center.getInstitution().getName() : null);

        dto.put("type", Optional.ofNullable(center.getInstitution()).map(Institution::getInstitutionType)
          .map(InstitutionType::getName).orElse(null));

        try {
          if (center.getInstitution() != null) {
            center.getInstitution().setLocations(new ArrayList<>());

            if (center.getInstitution().getInstitutionsLocations() != null
              && !center.getInstitution().getInstitutionsLocations().isEmpty()) {

              center.getInstitution().getLocations().addAll(center.getInstitution().getInstitutionsLocations().stream()
                .filter(InstitutionLocation::isActive).collect(Collectors.toList()));
            }
          }
        } catch (Exception e) {
          System.out.println("Error setting locations: " + e);
        }

        try {
          if (center.getInstitution() != null) {
            center.getInstitution().setLocations(new ArrayList<>());

            if (center.getInstitution().getInstitutionsLocations() != null
              && !center.getInstitution().getInstitutionsLocations().isEmpty()) {

              center.getInstitution().getLocations().addAll(center.getInstitution().getInstitutionsLocations().stream()
                .filter(InstitutionLocation::isActive).collect(Collectors.toList()));
            }
          }
        } catch (Exception e) {
          System.out.println("Error setting locations: " + e.getMessage());
          e.printStackTrace();
        }

        String headquarter = Optional.ofNullable(center.getInstitution()).map(Institution::getLocations).flatMap(
          locations -> locations.stream().filter(loc -> loc.isHeadquater() && loc.getLocElement() != null).map(loc -> {
            LocElement locElement = locElementManager.getLocElementById(loc.getLocElement().getId());
            return (locElement != null) ? locElement.getName() : null;
          }).filter(Objects::nonNull).findFirst()).orElse(null);

        dto.put("headquarter", headquarter);

        return dto;
      }).collect(Collectors.toList());

      centersJson = objectMapper.writeValueAsString(centersDTO);
    } catch (Exception e) {
      System.out.println("Error getting centers: " + e.getMessage());
      e.printStackTrace();
    }

    try {
      List<Map<String, String>> studiesList = new ArrayList<>();

      if (innovation != null && innovation.getProjectExpectedStudyInnovations() != null) {
        for (ProjectExpectedStudyInnovation study : innovation.getProjectExpectedStudyInnovations()) {
          if (study != null && study.getProjectExpectedStudy() != null) {
            ProjectExpectedStudyInfo studyInfo = study.getProjectExpectedStudy().getProjectExpectedStudyInfo(phase);

            if (studyInfo != null) {
              Map<String, String> studyMap = new HashMap<>();
              studyMap.put("name", studyInfo.getTitle());
              studyMap.put("studyType", studyInfo.getStudyType() != null ? studyInfo.getStudyType().getName() : null);

              studiesList.add(studyMap);
            }
          }
        }
      }
      studiesJson = objectMapper.writeValueAsString(studiesList);
    } catch (Exception e) {
      System.out.println("Error getting studies: " + e.getMessage());
      e.printStackTrace();
    }


    try {
      List<String> sharedInnovationsList = new ArrayList<>();

      if (innovation != null && innovation.getProjectInnovationShareds() != null) {
        for (ProjectInnovationShared shared : innovation.getProjectInnovationShareds()) {
          if (shared != null && shared.isActive() && shared.getPhase() != null
            && shared.getPhase().getId().equals(phase.getId())) {
            if (shared.getProject() != null && shared.getProject().getAcronym() != null) {
              sharedInnovationsList.add(shared.getProject().getAcronym());
            } else {
              sharedInnovationsList.add(null);
            }
          }
        }
      }

      sharedInnovationsJson = objectMapper.writeValueAsString(sharedInnovationsList);
    } catch (Exception e) {
      System.out.println("Error getting sharedInnovations: " + e.getMessage());
      e.printStackTrace();
    }

    try {
      List<String> allianceLeversList = new ArrayList<>();

      if (allianceLevers != null) {
        Set<String> uniqueLevers = new HashSet<>(); // Use a Set to avoid duplicates
        for (ProjectInnovationAllianceLevers lever : allianceLevers) {
          if (lever != null && lever.getAllianceLever() != null && lever.getAllianceLever().getName() != null) {
            uniqueLevers.add(lever.getAllianceLever().getName()); // Add only unique values
          } else {
            uniqueLevers.add(null); // Keep the logic of adding null if there is no name
          }
        }
        allianceLeversList.addAll(uniqueLevers); // Transfer unique values to the final list
      }

      allianceLeversJson = objectMapper.writeValueAsString(allianceLeversList);
    } catch (Exception e) {
      System.out.println("Error getting allianceLevers: " + e.getMessage());
      e.printStackTrace();
    }

    try {
      List<String> sdgsList = new ArrayList<>();

      if (sdgs != null) {
        for (ProjectInnovationSDG sdgItem : sdgs) {
          if (sdgItem != null && sdgItem.getSdg() != null && sdgItem.getSdg().getShortName() != null) {
            sdgsList.add(sdgItem.getSdg().getShortName());
          } else {
            sdgsList.add(null);
          }
        }
      }

      sdgsJson = objectMapper.writeValueAsString(sdgsList);
    } catch (Exception e) {
      System.out.println("Error getting sdgs: " + e.getMessage());
      e.printStackTrace();
    }

    try {
      List<String> impactAreasList = new ArrayList<>();

      if (impactAreas != null) {
        for (ProjectInnovationImpactArea area : impactAreas) {
          if (area != null && area.getImpactArea() != null && area.getImpactArea().getName() != null) {
            impactAreasList.add(area.getImpactArea().getName());
          } else {
            impactAreasList.add(null);
          }
        }
      }

      impactAreasJson = objectMapper.writeValueAsString(impactAreasList);
    } catch (Exception e) {
      System.out.println("Error getting impactAreas: " + e.getMessage());
      e.printStackTrace();
    }

    try {
      Set<Map<String, String>> allianceOrganizationsSet = new HashSet<>();

      if (allianceOrganizations != null) {
        for (ProjectInnovationAllianceOrganization allianceOrg : allianceOrganizations) {
          if (allianceOrg != null && allianceOrg.getInstitution() != null) {
            Map<String, String> orgMap = new HashMap<>();
            orgMap.put("name", allianceOrg.getInstitution().getComposedName());
            orgMap.put("scalingPartner", this.booleanToString(allianceOrg.getScalingPartner()));
            orgMap.put("type",
              allianceOrg.getInstitution() != null && allianceOrg.getInstitution().getComposedNameType() != null
                ? allianceOrg.getInstitution().getInstitutionType().getName() : null);

            allianceOrganizationsSet.add(orgMap);
          }
        }
      }

      allianceOrganizationsJson = objectMapper.writeValueAsString(new ArrayList<>(allianceOrganizationsSet));
    } catch (Exception e) {
      System.out.println("Error getting allianceOrganizations: " + e.getMessage());
      e.printStackTrace();
    }


    try {
      // Convert the list of ProjectInnovationActor to the required JSON structure
      List<Map<String, Object>> actorsList = new ArrayList<>();

      if (actors != null) {
        for (ProjectInnovationActor actor : actors) {
          if (actor != null && actor.getActor() != null) {
            Map<String, Object> actorMap = new HashMap<>();
            actorMap.put("type", actor.getActor().getName());
            actorMap.put("name", actor.getActor().getName());
            actorMap.put("sexAgeNotApply", Boolean.TRUE.equals(actor.getSexAgeNotApply()) ? "Yes" : "No");
            actorMap.put("womenYouth", Boolean.TRUE.equals(actor.getWomenYouth()) ? "Yes" : "No");
            actorMap.put("womenNotYouth", Boolean.TRUE.equals(actor.getWomenNotYouth()) ? "Yes" : "No");
            actorMap.put("menYouth", Boolean.TRUE.equals(actor.getMenYouth()) ? "Yes" : "No");
            actorMap.put("menNotYouth", Boolean.TRUE.equals(actor.getMenNotYouth()) ? "Yes" : "No");
            actorMap.put("nonbinaryYouth", Boolean.TRUE.equals(actor.getNonbinaryYouth()) ? "Yes" : "No");
            actorMap.put("nonbinaryNotYouth", Boolean.TRUE.equals(actor.getNonbinaryNotYouth()) ? "Yes" : "No");
            actorsList.add(actorMap);
          }
        }
      }

      // Convert the transformed list to JSON
      actorsJson = objectMapper.writeValueAsString(actorsList);
    } catch (Exception e) {
      System.out.println("Error getting actors: " + e.getMessage());
      e.printStackTrace();
    }


    try {
      toolCategoriesJson = toolCategories != null && !toolCategories.isEmpty() ? toolCategories.stream()
        .map(tc -> tc.getToolCategory().getName()).filter(Objects::nonNull).collect(Collectors.joining("; ")) : "";
    } catch (Exception e) {
      System.out.println("Error getting toolCategories: " + e.getMessage());
      e.printStackTrace();
    }

    try {
      List<Map<String, String>> referencesList = new ArrayList<>();

      if (references != null) {
        for (ProjectInnovationReference ref : references) {
          if (ref != null) {
            Map<String, String> refMap = new HashMap<>();

            // Evidence by deliverable
            if (Boolean.TRUE.equals(ref.getEvidenceByDeliverable())) {
              String link =
                this.getBaseUrl() + "/clusters/" + this.getCurrentCrp().getAcronym() + "/deliverable.do?deliverableID="
                  + ref.getDeliverable().getId() + "&edit=true&phaseID=" + this.getSelectedPhase().getId();
              refMap.put("url", link);

              refMap.put("reference",
                ref.getDeliverable() != null && ref.getDeliverable().getDeliverableInfo(phase) != null ? "D"
                  + ref.getDeliverable().getId() + " - " + ref.getDeliverable().getDeliverableInfo(phase).getTitle()
                  : null);
              refMap.put("deliverableCategory",
                ref.getDeliverable() != null && ref.getDeliverable().getDeliverableInfo(phase) != null
                  && ref.getDeliverable().getDeliverableInfo(phase).getDeliverableType() != null
                  && ref.getDeliverable().getDeliverableInfo(phase).getDeliverableType()
                    .getDeliverableCategory() != null
                      ? ref.getDeliverable().getDeliverableInfo(phase).getDeliverableType().getDeliverableCategory()
                        .getName()
                      : null);
              refMap.put("deliverableType",
                ref.getDeliverable() != null && ref.getDeliverable().getDeliverableInfo(phase) != null
                  && ref.getDeliverable().getDeliverableInfo(phase).getDeliverableType() != null
                    ? ref.getDeliverable().getDeliverableInfo(phase).getDeliverableType().getName() : null);

            } else {
              // Evidence by link
              refMap.put("url", ref.getLink());
              refMap.put("reference", ref.getReference());
              refMap.put("deliverableCategory",
                ref.getDeliverableType() != null && ref.getDeliverableType().getDeliverableCategory() != null
                  ? ref.getDeliverableType().getDeliverableCategory().getName() : null);
              refMap.put("deliverableType",
                ref.getDeliverableType() != null ? ref.getDeliverableType().getName() : null);
            }

            referencesList.add(refMap);
          }
        }
      }

      referencesJson = objectMapper.writeValueAsString(referencesList);
    } catch (Exception e) {
      System.out.println("Error getting references: " + e.getMessage());
      e.printStackTrace();
    }


    try {
      List<Map<String, String>> referenceUrlsList = new ArrayList<>();

      if (referenceUrls != null) {
        for (ProjectInnovationReferenceUrl refUrl : referenceUrls) {
          if (refUrl != null) {
            Map<String, String> refMap = new HashMap<>();

            // Evidence by deliverable
            if (Boolean.TRUE.equals(refUrl.getEvidenceByDeliverable())) {
              String link =
                this.getBaseUrl() + "/clusters/" + this.getCurrentCrp().getAcronym() + "/deliverable.do?deliverableID="
                  + refUrl.getDeliverable().getId() + "&edit=true&phaseID=" + this.getSelectedPhase().getId();
              refMap.put("url", link);
              refMap.put("reference",
                refUrl.getDeliverable() != null && refUrl.getDeliverable().getDeliverableInfo(phase) != null
                  ? "D" + refUrl.getDeliverable().getId() + " - "
                    + refUrl.getDeliverable().getDeliverableInfo(phase).getTitle()
                  : null);
              refMap.put("deliverableCategory",
                refUrl.getDeliverable() != null && refUrl.getDeliverable().getDeliverableInfo(phase) != null
                  && refUrl.getDeliverable().getDeliverableInfo(phase).getDeliverableType() != null
                  && refUrl.getDeliverable().getDeliverableInfo(phase).getDeliverableType()
                    .getDeliverableCategory() != null
                      ? refUrl.getDeliverable().getDeliverableInfo(phase).getDeliverableType().getDeliverableCategory()
                        .getName()
                      : null);
              refMap.put("deliverableType",
                refUrl.getDeliverable() != null && refUrl.getDeliverable().getDeliverableInfo(phase) != null
                  && refUrl.getDeliverable().getDeliverableInfo(phase).getDeliverableType() != null
                    ? refUrl.getDeliverable().getDeliverableInfo(phase).getDeliverableType().getName() : null);
            } else {

              // Evidence by link
              refMap.put("url", refUrl.getLink());
              refMap.put("reference", refUrl.getReference());
              refMap.put("deliverableCategory",
                refUrl.getDeliverableType() != null && refUrl.getDeliverableType().getDeliverableCategory() != null
                  ? refUrl.getDeliverableType().getDeliverableCategory().getName() : null);
              refMap.put("deliverableType",
                refUrl.getDeliverableType() != null ? refUrl.getDeliverableType().getName() : null);
            }
            referenceUrlsList.add(refMap);
          }
        }
      }

      referenceUrlsJson = objectMapper.writeValueAsString(referenceUrlsList);
    } catch (Exception e) {
      System.out.println("Error getting referenceUrls: " + e.getMessage());
      e.printStackTrace();
    }


    try {
      List<Map<String, String>> referenceComplementarySolutionsList = new ArrayList<>();

      if (referenceComplementarySolutions != null) {
        for (ProjectInnovationReferenceComplementarySolution ref : referenceComplementarySolutions) {
          if (ref != null) {
            Map<String, String> refMap = new HashMap<>();

            // Evidence by deliverable
            if (Boolean.TRUE.equals(ref.getEvidenceByDeliverable())) {
              String link =
                this.getBaseUrl() + "/clusters/" + this.getCurrentCrp().getAcronym() + "/deliverable.do?deliverableID="
                  + ref.getDeliverable().getId() + "&edit=true&phaseID=" + this.getSelectedPhase().getId();
              refMap.put("url", link);

              refMap.put("reference",
                ref.getDeliverable() != null && ref.getDeliverable().getDeliverableInfo(phase) != null ? "D"
                  + ref.getDeliverable().getId() + " - " + ref.getDeliverable().getDeliverableInfo(phase).getTitle()
                  : null);
              refMap.put("deliverableCategory",
                ref.getDeliverable() != null && ref.getDeliverable().getDeliverableInfo(phase) != null
                  && ref.getDeliverable().getDeliverableInfo(phase).getDeliverableType() != null
                  && ref.getDeliverable().getDeliverableInfo(phase).getDeliverableType()
                    .getDeliverableCategory() != null
                      ? ref.getDeliverable().getDeliverableInfo(phase).getDeliverableType().getDeliverableCategory()
                        .getName()
                      : null);
              refMap.put("deliverableType",
                ref.getDeliverable() != null && ref.getDeliverable().getDeliverableInfo(phase) != null
                  && ref.getDeliverable().getDeliverableInfo(phase).getDeliverableType() != null
                    ? ref.getDeliverable().getDeliverableInfo(phase).getDeliverableType().getName() : null);

            } else {
              // Evidence by link
              refMap.put("url", ref.getLink());
              refMap.put("reference", ref.getReference());
              refMap.put("deliverableCategory",
                ref.getDeliverableType() != null && ref.getDeliverableType().getDeliverableCategory() != null
                  ? ref.getDeliverableType().getDeliverableCategory().getName() : null);
              refMap.put("deliverableType",
                ref.getDeliverableType() != null ? ref.getDeliverableType().getName() : null);
            }

            referenceComplementarySolutionsList.add(refMap);
          }
        }
      }

      referenceComplementarySolutionsJson = objectMapper.writeValueAsString(referenceComplementarySolutionsList);
    } catch (Exception e) {
      System.out.println("Error getting referenceComplementarySolutions: " + e.getMessage());
      e.printStackTrace();
    }

    try {
      List<String> crpOutcomesList = crpOutcomes != null ? crpOutcomes.stream().map(outcome -> {
        if (outcome != null && outcome.getCrpOutcome() != null && outcome.getCrpOutcome().getComposedName() != null) {
          return outcome.getCrpOutcome().getComposedName();
        } else {
          return null;
        }
      }).collect(Collectors.toList()) : null;

      crpOutcomesJson = objectMapper.writeValueAsString(crpOutcomesList);
    } catch (Exception e) {
      System.out.println("Error getting crpOutcomes: " + e.getMessage());
      e.printStackTrace();
    }

    try {
      partnersJson = objectMapper.writeValueAsString(partners != null ? partners : null);
    } catch (Exception e) {
      System.out.println("Error getting partners: " + e.getMessage());
      e.printStackTrace();
    }

    try {
      partnerInstitutionsJson =
        objectMapper.writeValueAsString(partnerInstitutions != null ? partnerInstitutions : null);
    } catch (Exception e) {
      System.out.println("Error getting partnerInstitutions: " + e.getMessage());
      e.printStackTrace();
    }

    try {
      partnerPersonsJson = objectMapper.writeValueAsString(partnerPersons != null ? partnerPersons : null);
    } catch (Exception e) {
      System.out.println("Error getting partnerPersons: " + e.getMessage());
      e.printStackTrace();
    }

    try {
      myProjectsJson = objectMapper.writeValueAsString(myProjects != null ? myProjects : null);
    } catch (Exception e) {
      System.out.println("Error getting myProjects: " + e.getMessage());
      e.printStackTrace();
    }

    try {
      partnershipsJson = objectMapper.writeValueAsString(partnerships != null ? partnerships : null);
    } catch (Exception e) {
      System.out.println("Error getting partnerships: " + e.getMessage());
      e.printStackTrace();
    }

    /**
     * Generate Json
     */
    final Map<String, Object> jsonMainRoot = new HashMap<>();
    final Map<String, Object> jsonRoot = new HashMap<>();
    final Map<String, Object> jsonData = new HashMap<>();
    final Map<String, Object> jsonOptions = new HashMap<>();
    final Map<String, String> headerMap = new HashMap<>();
    final Map<String, String> footerMap = new HashMap<>();

    try {
      jsonData.put("type", "AICCRA Innovation");
      jsonData.put("id", innovationID);
      jsonData.put("projectID", projectID);
      jsonData.put("clusterAcronym", projectAcronym);
      jsonData.put("title", title);
      jsonData.put("narrative", narrative);
      jsonData.put("innovationImportance", innovationImportance);
      jsonData.put("year", year);
      jsonData.put("impactAreaCode", impactAreaCode);
      jsonData.put("phaseID", phaseID);
      jsonData.put("clearLead", clearLead);
      jsonData.put("haveRegions", haveRegions);
      jsonData.put("haveCountries", haveCountries);
      jsonData.put("actors", this.normalizeJson(actorsJson));
      jsonData.put("allianceOrganizations", this.normalizeJson(allianceOrganizationsJson));
      jsonData.put("beneficiariesNarrative", beneficiariesNarrative);
      jsonData.put("knowledgeMethodsAndToolsNarrative", knowledgeMethodsAndToolsNarrative);
      jsonData.put("knowledgeResultsNarrative", knowledgeResultsNarrative);

      // Alliance tab
      jsonData.put("sdgs", this.normalizeJson(sdgsJson));
      jsonData.put("allianceLevers", this.normalizeJson(allianceLeversJson));
      jsonData.put("intellectualProperty", intellectualProperty);
      jsonData.put("hasFurtherDevelopment", hasFurtherDevelopment);
      jsonData.put("hasLegalRestrictions", hasLegalRestrictions);
      jsonData.put("hasAssetPotential", hasAssetPotential);

      // One CGIAR Alignment tab
      jsonData.put("hasCgiarContribution", hasCgiarContribution);
      jsonData.put("reasonNotCgiarContribution", reasonNotCgiarContribution);
      jsonData.put("impactArea", this.normalizeJson(impactAreasJson));

      // Innovation Readiness tab
      jsonData.put("readinessScale", scalingReadiness);

      jsonData.put("repIndPhaseResearchPartnership",
        repIndPhaseResearchPartnership != null ? repIndPhaseResearchPartnership.getName() : null);
      jsonData.put("repIndStageInnovation", repIndStageInnovation != null ? repIndStageInnovation.getName() : null);
      jsonData.put("repIndRegion", repIndRegion != null ? repIndRegion.getName() : null);
      jsonData.put("repIndInnovationType", repIndInnovationType != null ? repIndInnovationType.getName() : null);
      jsonData.put("repIndInnovationNature", repIndInnovationNature != null ? repIndInnovationNature.getName() : null);
      jsonData.put("repIndDegreeInnovation", repIndDegreeInnovation != null ? repIndDegreeInnovation.getName() : null);
      jsonData.put("leadOrganization", leadOrganization != null ? leadOrganization.getComposedName() : null);
      jsonData.put("centers", this.normalizeJson(centersJson));
      jsonData.put("geographicScopes", geographicScopes);
      jsonData.put("regions", removeLeadingSemicolon(regions));
      jsonData.put("countries", removeLeadingSemicolon(countries));
      jsonData.put("deliverables", this.normalizeJson(deliverablesJson));
      jsonData.put("institutions", this.normalizeJson(contributingOrganizationsJson));
      jsonData.put("centers", this.normalizeJson(centersJson));
      jsonData.put("studies", this.normalizeJson(studiesJson));
      jsonData.put("studyProjects", this.normalizeJson(sharedInnovationsJson));
      jsonData.put("toolCategories", this.normalizeJson(toolCategoriesJson));
      jsonData.put("references", this.normalizeJson(referencesJson));
      jsonData.put("referenceUrls", this.normalizeJson(referenceUrlsJson));
      jsonData.put("referenceComplementarySolutions", this.normalizeJson(referenceComplementarySolutionsJson));
      jsonData.put("performanceIndicator", this.normalizeJson(crpOutcomesJson));
      jsonData.put("partners", this.normalizeJson(partnersJson));
      jsonData.put("partnerInstitutions", this.normalizeJson(partnerInstitutionsJson));
      jsonData.put("partnerPersons", this.normalizeJson(partnerPersonsJson));
      jsonData.put("myProjects", this.normalizeJson(myProjectsJson));
      jsonData.put("partnerships", this.normalizeJson(partnershipsJson));
      jsonData.put("isAllianceContribution", isAllianceContribution);
      jsonData.put("timeCreation", this.getCurrentDatev2());
    } catch (Exception e) {
      System.out.println("error setting jsonData " + e);
    }

    headerMap.put("height", "40mm");
    footerMap.put("height", "30mm");
    try {
      jsonOptions.put("format", "A3");
      jsonOptions.put("orientation", "portrait");
      jsonOptions.put("border", "0");
      jsonOptions.put("zoomFactor", 1);
      jsonOptions.put("header", headerMap);
      jsonOptions.put("footer", footerMap);
      jsonOptions.put("timeout", "300000");
    } catch (Exception e) {
      System.out.println("error setting jsonOptions " + e);
    }

    try {
      jsonRoot.put("data", jsonData);
      jsonRoot.put("options", jsonOptions);
    } catch (Exception e) {
      System.out.println("Error setting jsonRoot info " + e);
    }
    this.loadData();

    try {
      this.innovationsReportName =
        "AICCRA-Innovation" + innovation.getId() + "-Summary-" + this.getCurrentDateTime() + ".pdf";

      jsonRoot.put("templateData", this.innovationsTemplateData);
      jsonRoot.put("fileName", this.innovationsReportName);
      jsonRoot.put("bucketName", this.bucketName);
    } catch (Exception e) {
      System.out.println("Error setting other json root information");
    }

    String username = null, password = null;
    try {
      username = this.config.getMicroserviceUsername();
      password = this.config.getMicroservicePassword();
    } catch (final Exception e) {
      System.out.println("error getting conf credentials " + e);
    }

    try {
      final String credentialsJson = "{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}";
      jsonRoot.put("credentials", credentialsJson);

      jsonMainRoot.put("data", jsonRoot);
      jsonMainRoot.put("pattern", "pdf.generate");
    } catch (Exception e) {
      System.out.println("Error setting jsonRoot and jsonMainRoot information " + e);
    }

    try {
      objectMapper = new ObjectMapper();
      final String jsonOutput = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonMainRoot);
      try {
        final FileWriter fileWriter = new FileWriter(new File("D:/innovations_Report.json"));
        fileWriter.write(jsonOutput);
        fileWriter.close();
      } catch (Exception e) {
        System.out.println("Generated just for local environments");
      }

      System.out.println("send microservice class: " + this.innovationsReportName);
      this.microserviceReportAction.sendInnovationsQueueMessage(jsonOutput, this.innovationsReportName);

    } catch (final IOException e) {
      System.out.println("error generating json " + e);
    }

    return SUCCESS;
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

  public String getCurrentDate() {
    final SimpleDateFormat formatter = new SimpleDateFormat("EEEE, MMMM d, yyyy, 'at' HH:mm", Locale.US);
    formatter.setTimeZone(TimeZone.getTimeZone("CET"));
    return formatter.format(new Date());
  }

  public String getCurrentDateTime() {
    LocalDateTime currentDateTime = LocalDateTime.now();
    String formattedDateTime = currentDateTime.format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmm"));

    return formattedDateTime;
  }

  public String getCurrentDatev2() {
    // Define the date format: "Monday, March 17, 2025, at 21:57"
    final SimpleDateFormat formatter = new SimpleDateFormat("EEEE, MMMM d, yyyy, 'at' HH:mm", Locale.US);
    formatter.setTimeZone(TimeZone.getTimeZone("CET")); // Set timezone to CET

    // Format the current date without ordinal suffix
    String formattedDate = formatter.format(new Date());

    // Extract the day of the month
    Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("CET"), Locale.US);
    int day = calendar.get(Calendar.DAY_OF_MONTH);

    // Get the appropriate ordinal suffix (st, nd, rd, th)
    String ordinal = this.getDayOrdinal(day);

    // Replace the plain day number with the ordinal version (e.g., "17" → "17th")
    return formattedDate.replaceFirst("\\b" + day + "\\b", day + ordinal);
  }

  // Method to determine the ordinal suffix for a given day
  private String getDayOrdinal(int day) {
    // Special cases: 11th, 12th, 13th always use "th"
    if (day >= 11 && day <= 13) {
      return "th";
    }

    // Determine suffix based on the last digit
    switch (day % 10) {
      case 1:
        return "st"; // 1st, 21st, 31st
      case 2:
        return "nd"; // 2nd, 22nd
      case 3:
        return "rd"; // 3rd, 23rd
      default:
        return "th"; // 4th, 5th, ..., 24th, 25th, etc.
    }
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
    fileName.append("ProjectInnovationSummary-");
    fileName.append(this.getLoggedCrp().getAcronym() + "-");
    try {
      if (projectInnovationInfo != null && projectInnovationInfo.getProjectInnovation().getProject() != null) {
        fileName.append(projectInnovationInfo.getProjectInnovation().getProject()
          .getStandardIdentifier(Project.EMAIL_SUBJECT_IDENTIFIER) + "-");
      }
    } catch (Exception e) {
      LOG.info("Error getting project for innovation: " + projectInnovationID);
    }
    fileName.append("I" + projectInnovationID + "-");
    fileName.append(this.getSelectedYear() + "_");
    fileName.append(new SimpleDateFormat("yyyyMMdd-HHmm").format(new Date()));
    fileName.append(".pdf");
    return fileName.toString();
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
    model.addRow(new Object[] {center, date, year, projectInnovationID, this.getSelectedCycle()});
    return model;
  }


  private TypedTableModel getProjectInnovationTableModel() {
    TypedTableModel model = new TypedTableModel(
      new String[] {"id", "isRegional", "isNational", "isStage4", "title", "narrative", "stageInnovation",
        "innovationType", "contributionOfCrp", "degreeInnovation", "geographicScope", "region", "countries",
        "organizations", "projectExpectedStudy", "descriptionStage", "leadOrganization", "contributingOrganization",
        "adaptativeResearch", "evidenceLink", "deliverables", "crps", "genderFocusLevel", "genderExplaniation",
        "youthFocusLevel", "youthExplaniation", "project", "oicr", "centers", "hasMilestones", "milestones", "subIdos",
        "deliverableLink", "phaseID", "center", "isNew"},
      new Class[] {Long.class, Boolean.class, Boolean.class, Boolean.class, String.class, String.class, String.class,
        String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class,
        String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class,
        String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class,
        String.class, String.class, String.class, String.class, String.class},
      0);
    Long id = null;
    String title = null, narrative = null, phaseResearch = null, stageInnovation = null, innovationType = null,
      contributionOfCrp = null, degreeInnovation = null, geographicScope = null, region = null, countries = null,
      organizations = null, projectExpectedStudy = null, projectExpectedStudies = null, descriptionStage = null,
      leadOrganization = null, contributingOrganization = null, adaptativeResearch = null, evidenceLink = null,
      links = null, deliverables = null, crps = null, genderFocusLevel = null, genderExplaniation = null,
      youthFocusLevel = null, youthExplaniation = null, project = null, oicr = "", centers = "", hasMilestones = "",
      milestones = null, subIdos = null, deliverableLink = "", phaseID = "", loggedCenter = "", deliverableID = "",
      isNew = null;
    Boolean isRegional = false, isNational = false, isStage4 = false;
    // Id
    id = projectInnovationID;
    // Title
    if (projectInnovationInfo.getTitle() != null && !projectInnovationInfo.getTitle().trim().isEmpty()) {
      title = projectInnovationInfo.getTitle();
    }
    // Narrative
    if (projectInnovationInfo.getNarrative() != null && !projectInnovationInfo.getNarrative().trim().isEmpty()) {
      narrative = projectInnovationInfo.getNarrative();
    }
    // Phase Research
    if (projectInnovationInfo.getRepIndPhaseResearchPartnership() != null) {
      phaseResearch = projectInnovationInfo.getRepIndPhaseResearchPartnership().getName();
    }
    // Stage
    if (projectInnovationInfo.getRepIndStageInnovation() != null) {
      stageInnovation = projectInnovationInfo.getRepIndStageInnovation().getName();
      if (projectInnovationInfo.getRepIndStageInnovation().getId()
        .equals(APConstants.REP_IND_STAGE_INNOVATION_STAGE4)) {
        isStage4 = true;
        // Organizations
        List<ProjectInnovationOrganization> projectInnovationOrganizations =
          projectInnovationInfo.getProjectInnovation().getProjectInnovationOrganizations().stream()
            .filter(o -> o.isActive() && o.getPhase() != null && o.getPhase().equals(this.getSelectedPhase()))
            .collect(Collectors.toList());
        if (projectInnovationOrganizations != null && projectInnovationOrganizations.size() > 0) {
          Set<String> organizationSet = new HashSet<>();
          for (ProjectInnovationOrganization projectInnovationOrganization : projectInnovationOrganizations) {
            organizationSet.add(
              "<br>&nbsp;&nbsp;&nbsp;&nbsp; ● " + projectInnovationOrganization.getRepIndOrganizationType().getName());
          }
          organizations = String.join("", organizationSet);
        }

        // Studies
        List<ProjectExpectedStudyInnovation> studyInnovations = new ArrayList<>();

        // Expected Study Innovations List
        if (projectExpectedStudyInnovationManager.findAll() != null && projectExpectedStudyInnovationManager.findAll()
          .stream().filter(p -> p != null && p.getPhase().getId().equals(this.getActualPhase().getId())
            && p.getProjectInnovation().getId().equals(projectInnovationInfo.getProjectInnovation().getId())) != null) {
          studyInnovations = projectExpectedStudyInnovationManager.findAll().stream()
            .filter(p -> p != null && p.getPhase().getId().equals(this.getActualPhase().getId())
              && p.getProjectInnovation().getId().equals(projectInnovationInfo.getProjectInnovation().getId()))
            .collect(Collectors.toList());
        }

        if (projectInnovationInfo.getProjectExpectedStudy() != null && projectInnovationInfo.getProjectExpectedStudy()
          .getProjectExpectedStudyInfo(this.getSelectedPhase()) != null) {

          if (studyInnovations != null) {
            projectExpectedStudy = projectInnovationInfo.getProjectExpectedStudy().getId() + " - "
              + projectInnovationInfo.getProjectExpectedStudy().getProjectExpectedStudyInfo().getTitle();

          }

          for (ProjectExpectedStudyInnovation studyInnovation : studyInnovations) {

            projectExpectedStudies = studyInnovation.getProjectExpectedStudy().getId() + " - "
              + studyInnovation.getProjectExpectedStudy().getProjectExpectedStudyInfo(this.getActualPhase()).getTitle();

            /*
             * oicr += this.getBaseUrl() + "/projects/" + this.getLoggedCrp().getAcronym() + "/studySummary.do?studyID="
             * + studyInnovation.getProjectExpectedStudy().getId() + "&cycle=" + this.getSelectedCycle() + "&year="
             * + this.getSelectedPhase().getYear() + "";
             */
          }

          // oicr link
          /*
           * oicr = this.getBaseUrl() + "/projects/" + this.getLoggedCrp().getAcronym() + "/studySummary.do?studyID="
           * + projectInnovationInfo.getProjectExpectedStudy().getId() + "&cycle=" + this.getSelectedCycle() + "&year="
           * + this.getSelectedPhase().getYear();
           */
        }
      }
    }
    // load InnovationType
    if (projectInnovationInfo.getRepIndInnovationType() != null
      && projectInnovationInfo.getRepIndInnovationType().getId() != null) {
      projectInnovationInfo.setRepIndInnovationType(repIndInnovationTypeManager
        .getRepIndInnovationTypeById(projectInnovationInfo.getRepIndInnovationType().getId()));
    }
    // Degree
    if (projectInnovationInfo.getRepIndDegreeInnovation() != null) {
      degreeInnovation = projectInnovationInfo.getRepIndDegreeInnovation().getName();
    }

    // Is new Innovation
    if (this.isInnovationNew(projectInnovationID) != null) {
      if (this.isInnovationNew(projectInnovationID)) {
        isNew = "Yes";
      } else {
        isNew = "No";
      }
    }

    // Has milestones
    if (projectInnovationInfo.getHasMilestones() != null) {

      if (projectInnovationInfo.getHasMilestones() == true) {
        hasMilestones = "Yes";
      }
      if (projectInnovationInfo.getHasMilestones() == false) {
        hasMilestones = "No";
      }
    } else {
      hasMilestones = "<Not Defined>";
    }

    // Milestones
    /*
     * projectInnovationInfo.getProjectInnovation().setMilestones(
     * new ArrayList<>(projectInnovationInfo.getProjectInnovation().getProjectInnovationMilestones().stream()
     * .filter(o -> o.getPhase().getId().equals(this.getSelectedPhase().getId())).collect(Collectors.toList())));
     */
    /*
     * if (projectInnovationInfo.getProjectInnovation().getMilestones() != null) {
     * Set<String> milestonesSet = new HashSet<>();
     * for (ProjectInnovationMilestone milestone : projectInnovationInfo.getProjectInnovation().getMilestones()) {
     * if (milestone.getCrpMilestone() != null && milestone.getCrpMilestone().getComposedName() != null) {
     * milestonesSet.add("<br>&nbsp;&nbsp;&nbsp;&nbsp; ● " + milestone.getCrpMilestone().getComposedName());
     * }
     * }
     * milestones = String.join("", milestonesSet);
     * } else {
     * milestones = "<Not Defined>";
     * }
     */


    // List<ProjectInnovationMilestone> projectInnovationMilestoneList = new ArrayList<>();
    List<ProjectInnovationMilestone> projectInnovationMilestoneList = new ArrayList<>();
    projectInnovationMilestoneList = projectInnovationMilestoneManager.findAll().stream()
      .filter(pi -> pi.getPhase().getId().equals(this.getSelectedPhase().getId())
        && pi.getProjectInnovation().getId().equals(projectInnovationInfo.getProjectInnovation().getId()))
      .collect(Collectors.toList());

    if (projectInnovationMilestoneList != null && !projectInnovationMilestoneList.isEmpty()) {
      Set<String> milestonesSet = new HashSet<>();
      for (ProjectInnovationMilestone milestone : projectInnovationMilestoneList) {
        if (milestone.getCrpMilestone() != null && milestone.getCrpMilestone().getTitle() != null) {
          milestonesSet.add("<br>&nbsp;&nbsp;&nbsp;&nbsp; ● " + milestone.getCrpMilestone().getTitle());
        }
      }
      milestones = String.join("", milestonesSet);
    } else {
      milestones = "No milestones associated";
    }

    // Sub Idos

    List<ProjectInnovationSubIdo> subIdosList = projectInnovationSubIdoManager.findAll().stream()
      .filter(pi -> pi.getPhase().getId().equals(this.getSelectedPhase().getId())
        && pi.getProjectInnovation().getId().equals(projectInnovationInfo.getProjectInnovation().getId()))
      .collect(Collectors.toList());

    /*
     * List<ProjectInnovationSubIdo> subIdosList =
     * new ArrayList<>(projectInnovationInfo.getProjectInnovation().getProjectInnovationSubIdos().stream()
     * .filter(o -> o.getPhase().getId().equals(this.getSelectedPhase().getId())).collect(Collectors.toList()));
     */
    if (subIdosList != null && !subIdosList.isEmpty()) {
      Set<String> subIdosSet = new HashSet<>();
      for (ProjectInnovationSubIdo subIdo : subIdosList) {
        subIdosSet.add("<br>&nbsp;&nbsp;&nbsp;&nbsp; ● " + subIdo.getSrfSubIdo().getId() + " - "
          + subIdo.getSrfSubIdo().getDescription());
      }
      subIdos = String.join("", subIdosSet);

    } else {
      subIdos = "<Not Defined>";
    }

    // phaseID
    phaseID = this.getSelectedPhase().getId().toString();

    // Center
    loggedCenter = this.getLoggedCrp().getAcronym();

    // Geographic Scope
    List<ProjectInnovationGeographicScope> projectInnovationGeographicScopeList =
      projectInnovationGeographicScopeManager.findAll().stream()
        .filter(p -> p.getPhase().getId().equals(this.getSelectedPhase().getId())
          && p.getProjectInnovation() == projectInnovationInfo.getProjectInnovation())
        .collect(Collectors.toList());

    List<ProjectInnovationRegion> projectInnovationRegionList = projectInnovationRegionManager.findAll().stream()
      .filter(p -> p.getPhase().getId().equals(this.getSelectedPhase().getId())
        && p.getProjectInnovation().getId().equals(projectInnovationID))
      .collect(Collectors.toList());
    List<ProjectInnovationCountry> projectInnovationCountryList = projectInnovationCountryManager.findAll().stream()
      .filter(p -> p.getPhase().getId().equals(this.getSelectedPhase().getId())
        && p.getProjectInnovation().getId().equals(projectInnovationID))
      .collect(Collectors.toList());

    try {
      if (projectInnovationGeographicScopeList != null) {
        if (projectInnovationGeographicScopeList.get(0) != null
          && projectInnovationGeographicScopeList.get(0).getRepIndGeographicScope() != null
          && projectInnovationGeographicScopeList.get(0).getRepIndGeographicScope().getName() != null) {
          geographicScope = projectInnovationGeographicScopeList.get(0).getRepIndGeographicScope().getName();
        }

        // Regional
        if (projectInnovationGeographicScopeList != null && projectInnovationGeographicScopeList.get(0) != null
          && projectInnovationGeographicScopeList.get(0).getRepIndGeographicScope() != null
          && projectInnovationGeographicScopeList.get(0).getRepIndGeographicScope().getId() != null) {
          if (projectInnovationGeographicScopeList.get(0).getRepIndGeographicScope().getId()
            .equals(this.getReportingIndGeographicScopeRegional())) {
            isRegional = true;
            if (projectInnovationRegionList != null && !projectInnovationRegionList.isEmpty()) {
              Set<String> regionsSet = new HashSet<>();
              for (ProjectInnovationRegion innovationRegion : projectInnovationRegionList) {
                regionsSet.add("<br>&nbsp;&nbsp;&nbsp;&nbsp; ● " + innovationRegion.getLocElement().getName());
              }
              region = String.join("", regionsSet);
            }
          }
        }
        // Country
        if (projectInnovationGeographicScopeList.get(0).getRepIndGeographicScope() != null
          && projectInnovationGeographicScopeList.get(0).getRepIndGeographicScope().getId() != null) {
          if (!projectInnovationGeographicScopeList.get(0).getRepIndGeographicScope().getId()
            .equals(this.getReportingIndGeographicScopeGlobal())
            && !projectInnovationGeographicScopeList.get(0).getRepIndGeographicScope().getId()
              .equals(this.getReportingIndGeographicScopeRegional())) {
            isNational = true;
            if (projectInnovationCountryList != null && !projectInnovationCountryList.isEmpty()) {
              Set<String> countriesSet = new HashSet<>();
              for (ProjectInnovationCountry innovationCountry : projectInnovationCountryList) {
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

    // Description
    if (projectInnovationInfo.getDescriptionStage() != null
      && !projectInnovationInfo.getDescriptionStage().trim().isEmpty()) {
      descriptionStage = projectInnovationInfo.getDescriptionStage();
    }
    // Lead Organization
    if (projectInnovationInfo.getLeadOrganization() != null) {
      if (projectInnovationInfo.getLeadOrganization().getComposedName() != null
        && !projectInnovationInfo.getLeadOrganization().getComposedName().isEmpty()) {
        leadOrganization = projectInnovationInfo.getLeadOrganization().getComposedName();
      }
    }

    // Contributing Organization
    List<ProjectInnovationContributingOrganization> contributingOrganizationsList =
      new ArrayList<ProjectInnovationContributingOrganization>();
    contributingOrganizationsList = projectInnovationContributingOrganizationManager.findAll();
    if (contributingOrganizationsList != null && !contributingOrganizationsList.isEmpty()) {
      contributingOrganizationsList = contributingOrganizationsList.stream()
        .filter(p -> p.getProjectInnovation().getId().equals(projectInnovationInfo.getProjectInnovation().getId())
          && p.getPhase().getId() == this.getSelectedPhase().getId())
        .collect(Collectors.toList());
    }
    if (contributingOrganizationsList != null && !contributingOrganizationsList.isEmpty()) {
      Set<String> contributingSet = new HashSet<>();
      for (ProjectInnovationContributingOrganization contributingOrganizationItem : contributingOrganizationsList) {
        if (contributingOrganizationItem.getInstitution() != null) {
          contributingSet
            .add("<br>&nbsp;&nbsp;&nbsp;&nbsp; ● " + contributingOrganizationItem.getInstitution().getComposedName());
        }
      }
      contributingOrganization = String.join("", contributingSet);
    }

    // Adaptative research
    if (projectInnovationInfo.getAdaptativeResearchNarrative() != null
      && !projectInnovationInfo.getAdaptativeResearchNarrative().trim().isEmpty()) {
      adaptativeResearch = projectInnovationInfo.getAdaptativeResearchNarrative();
    }

    // Evidence Link
    URLShortener urlShortener = new URLShortener();
    if (projectInnovationInfo.getEvidenceLink() != null && !projectInnovationInfo.getEvidenceLink().trim().isEmpty()) {

      /*
       * Get short url calling tinyURL service
       */
      evidenceLink = urlShortener.getShortUrlService(projectInnovationInfo.getEvidenceLink());
    }

    // Deliverables
    List<ProjectInnovationDeliverable> projectInnovationDeliverables =
      projectInnovationDeliverableManager.findAll().stream()
        .filter(p -> p.getProjectInnovation().getId().equals(projectInnovationInfo.getProjectInnovation().getId())
          && p.getPhase().getId().equals(this.getSelectedPhase().getId()))
        .collect(Collectors.toList());
    if (projectInnovationDeliverables != null && projectInnovationDeliverables.size() > 0) {
      Set<String> deliverablesSet = new HashSet<>();
      for (ProjectInnovationDeliverable projectInnovationDeliverable : projectInnovationDeliverables) {
        if (projectInnovationDeliverable.getDeliverable() != null
          && projectInnovationDeliverable.getDeliverable().getId() != null
          && projectInnovationDeliverable.getDeliverable().getDeliverableInfo(this.getSelectedPhase()) != null) {
          String url = this.getBaseUrl() + "/projects/" + loggedCenter + "/deliverable.do?deliverableID="
            + projectInnovationDeliverable.getDeliverable().getId() + "&phaseID=" + phaseID;
          url = urlShortener.getShortUrlService(url);
          deliverablesSet
            .add("<br>&nbsp;&nbsp;&nbsp;&nbsp; ● " + "D" + projectInnovationDeliverable.getDeliverable().getId() + " - "
              + projectInnovationDeliverable.getDeliverable().getDeliverableInfo().getTitle() + "<br>(" + url + ")");
        }
      }
      deliverables = String.join("", deliverablesSet);
    }

    if (projectInnovationInfo.getProjectInnovation().getProjectInnovationCenters() != null) {
      projectInnovationInfo.getProjectInnovation()
        .setCenters(new ArrayList<>(projectInnovationInfo.getProjectInnovation().getProjectInnovationCenters().stream()
          .filter(c -> c.isActive() && c.getPhase().getId().equals(this.getSelectedPhase().getId()))
          .collect(Collectors.toList())));
    }
    if (projectInnovationInfo.getProjectInnovation().getCenters() != null) {
      Set<String> centerSet = new HashSet<>();

      for (ProjectInnovationCenter center : projectInnovationInfo.getProjectInnovation().getCenters()) {
        if (center.getInstitution() != null && center.getInstitution().getComposedName() != null) {
          centerSet.add("<br>&nbsp;&nbsp;&nbsp;&nbsp; ● " + center.getInstitution().getComposedName());
        }
      }
      centers = String.join("", centerSet);
    }

    // Contributions CRPS/Platforms
    List<ProjectInnovationCrp> projectInnovationCrps =
      projectInnovationInfo.getProjectInnovation().getProjectInnovationCrps().stream()
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
    if (projectInnovationInfo.getGenderFocusLevel() != null) {
      genderFocusLevel = projectInnovationInfo.getGenderFocusLevel().getName();
    }
    if (projectInnovationInfo.getGenderExplaniation() != null
      && !projectInnovationInfo.getGenderExplaniation().trim().isEmpty()) {
      genderExplaniation = projectInnovationInfo.getGenderExplaniation();
    }

    // Youth Relevance
    if (projectInnovationInfo.getYouthFocusLevel() != null) {
      youthFocusLevel = projectInnovationInfo.getYouthFocusLevel().getName();
    }
    if (projectInnovationInfo.getYouthExplaniation() != null
      && !projectInnovationInfo.getYouthExplaniation().trim().isEmpty()) {
      youthExplaniation = projectInnovationInfo.getYouthExplaniation();
    }

    if (projectInnovationInfo.getProjectInnovation().getProject() != null && projectInnovationInfo
      .getProjectInnovation().getProject().getProjecInfoPhase(this.getSelectedPhase()) != null) {
      project = projectInnovationInfo.getProjectInnovation().getProject().getComposedName();
    }

    model.addRow(new Object[] {id, isRegional, isNational, isStage4, title, narrative, stageInnovation, innovationType,
      contributionOfCrp, degreeInnovation, geographicScope, region, countries, organizations, projectExpectedStudies,
      descriptionStage, leadOrganization, contributingOrganization, adaptativeResearch, evidenceLink, deliverables,
      crps, genderFocusLevel, genderExplaniation, youthFocusLevel, youthExplaniation, project, oicr, centers,
      hasMilestones, milestones, subIdos, deliverableLink, phaseID, loggedCenter, isNew});
    return model;
  }

  /**
   * Validate the Alliance center selection
   *
   * @param action base action
   * @param project related project
   * @param projectExpectedStudy An specific projectExpectedStudy
   * @param saving related action
   */
  public boolean isAllianceSelected(ProjectInnovation projectInnovation) {
    // Validate if the Alliance institution is selected
    if ((projectInnovation != null) && (projectInnovation.getCenters() != null)) {
      try {
        for (final ProjectInnovationCenter center : projectInnovation.getCenters()) {
          if ((center != null) && (center.getInstitution() != null) && (center.getInstitution().getId() != null)) {
            final Institution institutiontmp =
              this.institutionManager.getInstitutionById(center.getInstitution().getId());
            if ((institutiontmp != null) && (institutiontmp.getName() != null)) {
              center.getInstitution().setName(institutiontmp.getName());
            }
          }
          if ((center != null) && (center.getInstitution() != null) && (center.getInstitution().getId() != null)
            && (center.getInstitution().getName() != null)
            && center.getInstitution().getName().toLowerCase().contains(APConstants.ALLIANCE_INSTITUTION_NAME)) {
            return true;
          }
        }
      } catch (final Exception e) {
        Log.error("error in isAllianceSelected " + e);
      }
    }
    return false;
  }

  public void loadData() {
    try {
      List<ReportConfiguration> reportConfigurations = new ArrayList<>();
      reportConfigurations = reportConfigurationManager.findAll();
      if (reportConfigurations != null && !reportConfigurations.isEmpty()) {
        ReportConfiguration reportConfiguration = reportConfigurations.get(0);
        if (reportConfiguration.getInnovationTemplateData() != null) {
          innovationsTemplateData = reportConfiguration.getInnovationTemplateData();
        }

      }

      this.bucketName = this.config.getMicroserviceBucketname();

    } catch (final Exception e) {
      System.out.println("error getting report configuration data " + e);
    }
  }

  /**
   * Normalizes a JSON string by converting empty JSON arrays ("[]") to null.
   * 
   * @param json The JSON string to normalize. It can be an empty JSON array ("[]") or any other JSON content.
   * @return The original JSON string if it is not an empty array; otherwise, returns null.
   */
  private String normalizeJson(String json) {
    return "[]".equals(json) ? null : json;
  }

  @Override
  public void prepare() throws Exception {
    Map<String, Parameter> parameters = this.getParameters();
    this.setPublicAccessParameters();
    projectInnovationID =
      Long.parseLong(StringUtils.trim(parameters.get(APConstants.INNOVATION_REQUEST_ID).getMultipleValues()[0]));
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