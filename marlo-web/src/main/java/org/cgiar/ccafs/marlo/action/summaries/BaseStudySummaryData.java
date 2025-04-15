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
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyCountryManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.ReportConfigurationManager;
import org.cgiar.ccafs.marlo.data.model.ExpectedStudyProject;
import org.cgiar.ccafs.marlo.data.model.ImpactArea;
import org.cgiar.ccafs.marlo.data.model.Institution;
import org.cgiar.ccafs.marlo.data.model.InstitutionLocation;
import org.cgiar.ccafs.marlo.data.model.InstitutionType;
import org.cgiar.ccafs.marlo.data.model.LocElement;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProgramType;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudy;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyAllianceLeversOutcome;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyCenter;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyCountry;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyCrp;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyCrpOutcome;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyFlagship;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyGeographicScope;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyGlobalTarget;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyImpactArea;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyInfo;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyInnovation;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyInstitution;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyLink;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyPartnership;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyPartnershipsPerson;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyPublication;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyQuantification;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyReference;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyRegion;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudySdgAllianceLever;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudySrfTarget;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudySubIdo;
import org.cgiar.ccafs.marlo.data.model.ReportConfiguration;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.HTMLParser;
import org.cgiar.ccafs.marlo.utils.URLShortener;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.jfree.util.Log;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.util.TypedTableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseStudySummaryData extends BaseSummariesAction {

  public class InstitutionDTO {

    private String name;
    private String type;
    private String headquarter;

    public InstitutionDTO(String name, String type, String headquarter) {
      this.name = name;
      this.type = type;
      this.headquarter = headquarter;
    }

    public String getHeadquarter() {
      return this.headquarter;
    }

    // Getters
    public String getName() {
      return this.name;
    }

    public String getType() {
      return this.type;
    }

    public void setHeadquarter(String headquarter) {
      this.headquarter = headquarter;
    }

    // Setters
    public void setName(String name) {
      this.name = name;
    }

    public void setType(String type) {
      this.type = type;
    }
  }

  public static class PublicationDTO {

    private final String name;
    private final String position;
    private final String affiliation;

    public PublicationDTO(String name, String position, String affiliation) {
      this.name = name;
      this.position = position;
      this.affiliation = affiliation;
    }

    public String getAffiliation() {
      return this.affiliation;
    }

    // Getters required for Jackson serialization
    public String getName() {
      return this.name;
    }

    public String getPosition() {
      return this.position;
    }
  }

  public static class QuantificationDTO {

    private String type;
    private String number;
    private String unit;
    private String comments;

    public QuantificationDTO(String type, String number, String unit, String comments) {
      super();
      this.type = type;
      this.number = number;
      this.unit = unit;
      this.comments = comments;
    }

    public String getComments() {
      return this.comments;
    }

    public String getNumber() {
      return this.number;
    }

    public String getType() {
      return this.type;
    }

    public String getUnit() {
      return this.unit;
    }

    public void setComments(String comments) {
      this.comments = comments;
    }

    public void setNumber(String number) {
      this.number = number;
    }

    public void setType(String type) {
      this.type = type;
    }

    public void setUnit(String unit) {
      this.unit = unit;
    }
  }

  private static final long serialVersionUID = -3643067302492726266L;

  private static Logger LOG = LoggerFactory.getLogger(BaseStudySummaryData.class);

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

  private final HTMLParser htmlParser;

  private final ProjectExpectedStudyCountryManager projectExpectedStudyCountryManager;
  private final InstitutionManager institutionManager;
  private final MicroserviceReportAction microserviceReportAction;
  private final ReportConfigurationManager reportConfigurationManager;
  private final LocElementManager locElementManager;
  private String OICRsTemplateData = null;
  private String OICRsReportName = null;
  private String bucketName = null;
  private boolean activateShortURL = false;

  public BaseStudySummaryData(APConfig config, GlobalUnitManager crpManager, PhaseManager phaseManager,
    ProjectManager projectManager, HTMLParser htmlParser,
    ProjectExpectedStudyCountryManager projectExpectedStudyCountryManager, InstitutionManager institutionManager,
    MicroserviceReportAction microserviceReportAction, ReportConfigurationManager reportConfigurationManager,
    LocElementManager locElementManager) {
    super(config, crpManager, phaseManager, projectManager);
    this.htmlParser = htmlParser;
    this.projectExpectedStudyCountryManager = projectExpectedStudyCountryManager;
    this.institutionManager = institutionManager;
    this.microserviceReportAction = microserviceReportAction;
    this.reportConfigurationManager = reportConfigurationManager;
    this.locElementManager = locElementManager;
  }

  /**
   * Method to add i8n parameters to masterReport in Pentaho
   * 
   * @param masterReport
   * @return masterReport with i8n parameters added
   */
  public MasterReport addi8nParameters(MasterReport masterReport) {
    masterReport.getParameterValues().put("i8nStudies", this.getText("summaries.study.header"));
    masterReport.getParameterValues().put("i8nStudiesRNoData", this.getText("summaries.study.noData"));
    masterReport.getParameterValues().put("i8nStudiesMainTitle", this.getText("summaries.study.mainTitle"));
    masterReport.getParameterValues().put("i8nStudiesTagAs", this.getText("study.general.tag"));
    masterReport.getParameterValues().put("i8nStudiesAllianceID", this.getText("study.general.allianceID"));
    masterReport.getParameterValues().put("i8nStudiesCovidAnalysis", this.getText("summaries.study.hasCovidAnalysis"));
    masterReport.getParameterValues().put("i8nStudiesLinkPerformance",
      this.getText("summaries.study.linkPerformanceIndicator"));
    masterReport.getParameterValues().put("i8nStudiesRCaseStudy", this.getText("summaries.study"));
    masterReport.getParameterValues().put("i8nStudiesRSharedClusters", this.getText("study.sharedProjects.title"));
    masterReport.getParameterValues().put("i8nCaseStudiesRStudyProjects",
      this.getText("summaries.study.studyProjects"));
    masterReport.getParameterValues().put("i8nCaseStudiesRProject", this.getText("global.Project"));
    masterReport.getParameterValues().put("i8nCaseStudiesRPartI", this.getText("summaries.study.partI"));
    masterReport.getParameterValues().put("i8nStudiesRType", this.getText("study.general.type"));
    masterReport.getParameterValues().put("i8nStudiesRStatus", this.getText("study.general.status"));
    masterReport.getParameterValues().put("i8nStudiesRYear", this.getText("summaries.study.year"));
    masterReport.getParameterValues().put("i8nStudiesRTagged", this.getText("summaries.study.tagged"));
    masterReport.getParameterValues().put("i8nStudiesRTitle", this.getText("summaries.study.title"));
    masterReport.getParameterValues().put("i8nStudiesROutcomeImpactStatement",
      this.getText("summaries.study.outcomeStatement"));
    masterReport.getParameterValues().put("i8nCaseStudiesROutcomeStory", this.getText("study.outcomestory.readText"));
    masterReport.getParameterValues().put("i8nCaseStudiesROutcomestoryLinks",
      this.getText("study.outcomestoryLinks.readText"));
    masterReport.getParameterValues().put("i8nCaseStudiesRPartII", this.getText("summaries.study.partII"));
    masterReport.getParameterValues().put("i8nStudiesRIsContributionText",
      this.getText("summaries.study.reportingIndicatorThree"));
    masterReport.getParameterValues().put("i8nCaseStudiesRPolicies", this.getText("study.policies.readText"));
    masterReport.getParameterValues().put("i8nStudiesRStageStudy", this.getText("summaries.study.maturityChange"));
    masterReport.getParameterValues().put("i8nStudiesRStrategicResults",
      this.getText("summaries.study.stratgicResultsLink"));
    masterReport.getParameterValues().put("i8nStudiesRSubIdos",
      this.getText("summaries.study.stratgicResultsLink.subIDOs"));
    masterReport.getParameterValues().put("i8nCaseStudiesRTargetOption",
      this.getText("study.generalInformation.targetsOption"));
    masterReport.getParameterValues().put("i8nStudiesRSRFTargets",
      this.getText("summaries.study.stratgicResultsLink.srfTargets"));
    masterReport.getParameterValues().put("i8nStudiesRTopLevelCommentst",
      this.getText("summaries.study.stratgicResultsLink.comments"));
    masterReport.getParameterValues().put("i8nActivityDescription",
      this.getText("study.generalInformation.activityDescription"));
    masterReport.getParameterValues().put("i8nMeliaPublications", this.getText("summaries.study.meliaPublications"));
    masterReport.getParameterValues().put("i8nStudiesRGeographicScope",
      this.getText("study.generalInformation.geographicScope"));
    masterReport.getParameterValues().put("i8nStudiesRRegion", this.getText("study.region"));
    masterReport.getParameterValues().put("i8nStudiesRContries", this.getText("involveParticipants.countries"));
    masterReport.getParameterValues().put("i8nStudiesRScopeComments",
      this.getText("study.generalInformation.geographicScopeComments.readText"));
    masterReport.getParameterValues().put("i8nStudiesRKeyContributors",
      this.getText("summaries.study.keyContributors"));
    masterReport.getParameterValues().put("i8nStudiesRCrps",
      this.getText("study.generalInformation.keyContributors.crps"));
    masterReport.getParameterValues().put("i8nStudiesRFlagships", this.getText("study.keyContributors.flagships"));
    masterReport.getParameterValues().put("i8nStudiesRCenters",
      this.getText("study.generalInformation.keyContributors.centers"));
    masterReport.getParameterValues().put("i8nStudiesRRegions",
      this.getText("study.generalInformation.keyContributors.regions"));
    masterReport.getParameterValues().put("i8nStudiesRInstitutions",
      this.getText("study.generalInformation.keyContributors.externalPartners"));
    masterReport.getParameterValues().put("i8nStudiesCGIARInnovation",
      this.getText("study.generalInformation.innovationsNarrative.readText"));
    masterReport.getParameterValues().put("i8nStudiesCGIARInnovations",
      this.getText("study.generalInformation.innovationsList"));
    masterReport.getParameterValues().put("i8nStudiesRElaborationOutcomeImpactStatement",
      this.getText("summaries.study.elaborationStatement"));
    masterReport.getParameterValues().put("i8nStudiesRReferenceText", this.getText("summaries.study.referencesCited"));
    masterReport.getParameterValues().put("i8nStudiesRGenderDevelopment",
      this.getText("summaries.study.crossCuttingRelevance"));
    masterReport.getParameterValues().put("i8nStudiesRGenderRelevance",
      this.getText("study.generalInformation.genderRelevance"));
    masterReport.getParameterValues().put("i8nStudiesRYouthRelevance",
      this.getText("study.generalInformation.youthRelevance"));
    masterReport.getParameterValues().put("i8nStudiesRCapacityRelevance",
      this.getText("study.generalInformation.capDevRelevance"));
    masterReport.getParameterValues().put("i8nStudiesRClimateRelevance", this.getText("study.climateRelevance"));
    masterReport.getParameterValues().put("i8nStudiesROtherCrossCuttingDimensions",
      this.getText("study.generalInformation.otherCrossCutting.readText"));
    masterReport.getParameterValues().put("i8nStudiesROtherCrossCuttingDimensionsComments",
      this.getText("study.generalInformation.otherCrossCutting.comments.readText"));
    masterReport.getParameterValues().put("i8nStudiesRContacts", this.getText("summaries.study.contacts"));
    masterReport.getParameterValues().put("i8nStudiesRCommissioningStudy",
      this.getText("study.commissioningStudy.readText"));
    masterReport.getParameterValues().put("i8nStudiesRStudyLink", this.getText("summaries.study.link"));
    masterReport.getParameterValues().put("i8nStudiesRQuantification",
      this.getText("study.quantification.comments.readText"));
    masterReport.getParameterValues().put("i8nStudiesRQuantificationType", this.getText("study.quantificationType"));
    masterReport.getParameterValues().put("i8nStudiesRQuantificationNumber",
      this.getText("study.quantification.number"));
    masterReport.getParameterValues().put("i8nStudiesRQuantificationTargetUnit",
      this.getText("study.quantification.targetUnit"));
    masterReport.getParameterValues().put("i8nStudiesRQuantificationComments",
      this.getText("study.quantification.comments.readText"));
    masterReport.getParameterValues().put("i8nStudiesRQuantificationType1",
      this.getText("study.quantification.quantificationType-1"));
    masterReport.getParameterValues().put("i8nStudiesRQuantificationType2",
      this.getText("study.quantification.quantificationType-2"));
    masterReport.getParameterValues().put("i8nStudiesPublications", this.getText("study.communications.publications"));
    masterReport.getParameterValues().put("i8nStudiesFooter", this.getText("summaries.study.footer"));


    return masterReport;
  }

  /**
   * Cleans an HTML string by removing unnecessary characters.
   * - Replaces multiple "&nbsp;" and "<br>
   * " with a single space.
   * - Converts "●" into ";" ensuring no spaces before it but one space after.
   * - Removes any leading ";" from the final result.
   *
   * @param input The original HTML string.
   * @return The cleaned string.
   */
  public String cleanHtml(String input) {
    try {
      if (input == null) {
        return null;
      }
      final String cleanedText = input.replaceAll("(&nbsp;|<br>)+", " ") // Replace multiple &nbsp; and <br> with a
                                                                         // single
        // space
        .replaceAll("\\s*●", ";") // Removes spaces before '●' and replaces it with ';'
        .replaceAll("●", ";") // Ensures remaining '●' are replaced
        .replaceAll("\\s+;", ";") // Removes any extra spaces before ';'
        .replaceAll(";\\s*", "; ") // Ensures exactly one space after ';'
        .trim();

      // Removes any leading ';' characters
      return cleanedText.replaceAll("^;+\\s*", "").trim();
    } catch (final Exception e) {
      return input;
    }
  }

  private String extractCodeFromName(String name) {
    Pattern pattern = Pattern.compile("\\d+");
    Matcher matcher = pattern.matcher(name);
    return matcher.find() ? matcher.group() : "N/A";
  }

  public String generateAndSendJson(List<ProjectExpectedStudyInfo> projectExpectedStudyInfos) {
    final URLShortener urlShortener = new URLShortener();
    if ((projectExpectedStudyInfos != null) && !projectExpectedStudyInfos.isEmpty()) {
      projectExpectedStudyInfos
        .sort((p1, p2) -> p1.getProjectExpectedStudy().getId().compareTo(p2.getProjectExpectedStudy().getId()));

      for (final ProjectExpectedStudyInfo projectExpectedStudyInfo : projectExpectedStudyInfos) {

        Long id = null;
        Integer year = null;
        String title = null, commissioningStudy = null, status = null, type = null, outcomeImpactStatement = null;
        final String isContributionText = null;
        String stageStudy = null, srfTargets = null, subIdos = null, topLevelComments = null, geographicScopes = null,
          regions = null, countries = null, scopeComments = null, crps = null, flagships = null,
          regionalPrograms = null, institutions = null, elaborationOutcomeImpactStatement = null, referenceText = null,
          genderRelevance = null, youthRelevance = null, capacityRelevance = null, otherCrossCuttingDimensions = null,
          communicationsMaterial = null, contacts = null, studyProjects = null, tagged = null, cgiarInnovation = null,
          cgiarInnovations = null, climateRelevance = null, link = null, links = null, centers = null;
        final String studyPolicies = null, url = null;
        String studiesReference = null, meliaPublications = null, performanceIndicator = null, covidAnalysis = null,
          studyCenters = null, clusterAcronym = null, clusterName = null, leadPerson = null,
          isAllianceContribution = null, allianceOICRID = null, primaryAllianceLever = null, strategicOutcome = null,
          primarySDGcontribution = null, relatedLever = "", relatedSDGContribution = null, hasCGIARContribution = null,
          impactArea = null, tagAs = null, globalTargets = null, impactAreaCode = null,
          reasonNotCgiarContribution = null, otherCrossCuttingSelection = null;

        Boolean isRegional = false, isNational = false;
        final ProjectExpectedStudy projectExpectedStudy = projectExpectedStudyInfo.getProjectExpectedStudy();
        final Phase phase = this.getSelectedPhase();
        id = projectExpectedStudy.getId();

        // Alliance OICR ID
        if (projectExpectedStudyInfo.getAllianceOicr() != null) {
          allianceOICRID = projectExpectedStudyInfo.getAllianceOicr();
        }
        // Tag As
        if ((projectExpectedStudyInfo.getTag() != null) && (projectExpectedStudyInfo.getTag().getTagName() != null)) {
          tagAs = projectExpectedStudyInfo.getTag().getTagName();
        }
        // Type
        if (projectExpectedStudyInfo.getStudyType() != null) {
          type = projectExpectedStudyInfo.getStudyType().getName();
          if (projectExpectedStudyInfo.getStudyType().getId().intValue() == 1) {
            type = "Outcome Impact Case Report (OICR)";
          }
        }
        // Status
        if (projectExpectedStudyInfo.getStatus() != null) {
          status = projectExpectedStudyInfo.getStatus().getName();
        }
        // Year
        if (projectExpectedStudy.getProjectExpectedStudyInfo(phase).getYear() != null) {
          year = projectExpectedStudy.getProjectExpectedStudyInfo(phase).getYear();
        }
        // Tagged
        if ((projectExpectedStudyInfo != null) && (projectExpectedStudyInfo.getEvidenceTag() != null)
          && (projectExpectedStudyInfo.getEvidenceTag().getName() != null)) {
          tagged = projectExpectedStudyInfo.getEvidenceTag().getName();
        }
        // Title
        if ((projectExpectedStudyInfo.getTitle() != null) && !projectExpectedStudyInfo.getTitle().trim().isEmpty()) {
          title = projectExpectedStudyInfo.getTitle();
        }
        // Commissioning Study
        if ((projectExpectedStudyInfo.getCommissioningStudy() != null)
          && !projectExpectedStudyInfo.getCommissioningStudy().trim().isEmpty()) {
          commissioningStudy = projectExpectedStudyInfo.getCommissioningStudy();
        }
        // Outcome Impact Statement
        if ((projectExpectedStudyInfo.getOutcomeImpactStatement() != null)
          && !projectExpectedStudyInfo.getOutcomeImpactStatement().trim().isEmpty()) {
          outcomeImpactStatement = projectExpectedStudyInfo.getOutcomeImpactStatement();
        }
        // Communications materials
        if ((projectExpectedStudyInfo.getComunicationsMaterial() != null)
          && !projectExpectedStudyInfo.getComunicationsMaterial().trim().isEmpty()) {
          communicationsMaterial = this.htmlParser.plainTextToHtml(projectExpectedStudyInfo.getComunicationsMaterial());
        }

        // Level of maturity
        if ((projectExpectedStudyInfo.getRepIndStageStudy() != null)
          && (projectExpectedStudyInfo.getRepIndStageStudy().getName() != null)
          && (projectExpectedStudyInfo.getRepIndStageStudy().getDescription() != null)) {
          stageStudy = projectExpectedStudyInfo.getRepIndStageStudy().getName() + " - "
            + projectExpectedStudyInfo.getRepIndStageStudy().getDescriptionAF();
        }

        // SubIdos
        final List<ProjectExpectedStudySubIdo> subIdosList = projectExpectedStudy.getProjectExpectedStudySubIdos()
          .stream().filter(s -> s.isActive() && (s.getPhase() != null) && s.getPhase().equals(phase))
          .collect(Collectors.toList());
        final Set<String> subIdoSet = new HashSet<>();
        if ((subIdosList != null) && !subIdosList.isEmpty()) {
          for (final ProjectExpectedStudySubIdo studySrfTarget : subIdosList) {
            subIdoSet.add("; " + studySrfTarget.getSrfSubIdo().getDescription());
          }
          subIdos = String.join("", subIdoSet);
        }
        // is SRF Target
        if ((projectExpectedStudyInfo.getIsSrfTarget() != null)
          && !projectExpectedStudyInfo.getIsSrfTarget().isEmpty()) {
          if (projectExpectedStudyInfo.getIsSrfTarget().equals("targetsOptionYes")) {
            // SRF Targets
            final List<ProjectExpectedStudySrfTarget> studySrfTargets =
              projectExpectedStudy.getProjectExpectedStudySrfTargets().stream()
                .filter(s -> s.isActive() && (s.getPhase() != null) && s.getPhase().equals(phase))
                .collect(Collectors.toList());
            final Set<String> srfTargetSet = new HashSet<>();
            if ((studySrfTargets != null) && !studySrfTargets.isEmpty()) {
              for (final ProjectExpectedStudySrfTarget studySrfTarget : studySrfTargets) {
                srfTargetSet.add("; " + studySrfTarget.getSrfSloIndicator().getTitle());
              }
              srfTargets = String.join("", srfTargetSet);
            }
          }
        }
        // Comments
        if ((projectExpectedStudyInfo.getTopLevelComments() != null)
          && !projectExpectedStudyInfo.getTopLevelComments().trim().isEmpty()) {
          topLevelComments = this.htmlParser.plainTextToHtml(projectExpectedStudyInfo.getTopLevelComments());
        }
        // Geographic Scopes
        final List<ProjectExpectedStudyGeographicScope> geographicScopeList =
          projectExpectedStudy.getProjectExpectedStudyGeographicScopes().stream()
            .filter(s -> s.isActive() && (s.getPhase() != null) && s.getPhase().equals(phase))
            .collect(Collectors.toList());
        final Set<String> geographicScopeSet = new HashSet<>();
        if ((geographicScopeList != null) && !geographicScopeList.isEmpty()) {
          for (final ProjectExpectedStudyGeographicScope geographicScope : geographicScopeList) {
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
          final List<ProjectExpectedStudyCountry> studyCountries = this.projectExpectedStudyCountryManager
            .getProjectExpectedStudyCountrybyPhase(projectExpectedStudy.getId(), phase.getId()).stream()
            .filter(le -> le.isActive() && (le.getLocElement().getLocElementType().getId() == 2))
            .collect(Collectors.toList());
          if ((studyCountries != null) && !studyCountries.isEmpty()) {
            final Set<String> countriesSet = new HashSet<>();
            for (final ProjectExpectedStudyCountry projectExpectedStudyCountry : studyCountries) {
              countriesSet.add("; " + projectExpectedStudyCountry.getLocElement().getName());
            }
            countries = String.join("", countriesSet);
          }
        }

        // Region(s)
        if (isRegional) {
          final List<ProjectExpectedStudyRegion> studyRegions = projectExpectedStudy.getProjectExpectedStudyRegions()
            .stream().filter(c -> c.isActive() && (c.getPhase() != null) && c.getPhase().equals(phase))
            .collect(Collectors.toList());
          if ((studyRegions != null) && !studyRegions.isEmpty()) {
            final Set<String> regionsSet = new HashSet<>();
            for (final ProjectExpectedStudyRegion projectExpectedStudyRegion : studyRegions) {
              regionsSet.add("; " + projectExpectedStudyRegion.getLocElement().getName());
            }
            regions = String.join("", regionsSet);
          }
        }
        // Centers(s)
        try {
          List<ProjectExpectedStudyCenter> studyCentersList = projectExpectedStudy.getProjectExpectedStudyCenters()
            .stream().filter(c -> c.isActive() && c.getPhase() != null && c.getPhase().equals(phase))
            .collect(Collectors.toList());

          if (!studyCentersList.isEmpty()) {
            List<Map<String, String>> centersList = studyCentersList.stream().map(center -> {
              Map<String, String> centerMap = new HashMap<>();
              Institution institution = center.getInstitution();

              if (institution != null) {
                centerMap.put("name", institution.getComposedName());

                String institutionType = Optional.ofNullable(institution.getInstitutionType())
                  .map(InstitutionType::getName).orElse("Not available");
                centerMap.put("type", institutionType);

                try {
                  if (institution != null) {
                    institution.setLocations(new ArrayList<>());
                    if (institution.getInstitutionsLocations() != null
                      && !institution.getInstitutionsLocations().isEmpty()) {
                      institution.getLocations().addAll(institution.getInstitutionsLocations().stream()
                        .filter(o -> o.isActive()).collect(Collectors.toList()));
                    }
                  }
                } catch (Exception e) {
                  System.out.println("Error setting locations: " + e);
                }

                String headquarter = Optional.ofNullable(institution.getLocations()).flatMap(locations -> locations
                  .stream().filter(loc -> loc.isHeadquater() && loc.getLocElement() != null).map(loc -> {
                    LocElement locElement = locElementManager.getLocElementById(loc.getLocElement().getId());
                    return (locElement != null && locElement.getName() != null) ? locElement.getName() : "";
                  }).filter(name -> !name.isEmpty()).findFirst()).orElse("Not available");
                centerMap.put("headquarter", headquarter);
              }

              return centerMap;
            }).collect(Collectors.toList());

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
            studyCenters = objectMapper.writeValueAsString(centersList);
          } else {
            studyCenters = null;
          }
        } catch (Exception e) {
          System.out.println("Error generating centers JSON: " + e.getMessage());
        }

        // Expected Study Center List
        try {
          projectExpectedStudy.setCenters(new ArrayList<>());

          if (projectExpectedStudy.getProjectExpectedStudyPartnerships() != null) {
            List<ProjectExpectedStudyPartnership> deList =
              projectExpectedStudy.getProjectExpectedStudyPartnerships().stream()
                .filter(dp -> dp.isActive() && dp.getPhase().getId().equals(this.getActualPhase().getId())
                  && dp.getProjectExpectedStudyPartnerType().getId()
                    .equals(APConstants.EXPECTED_STUDIES_PARTNERSHIP_TYPE_CENTER))
                .sorted((p1, p2) -> p1.getInstitution().getId().compareTo(p2.getInstitution().getId()))
                .collect(Collectors.toList());
            projectExpectedStudy.setCenters(deList);
            if (!deList.isEmpty()) {
              List<Map<String, String>> centersList = deList.stream().map(partnership -> {
                Map<String, String> centerMap = new HashMap<>();
                Institution institution = partnership.getInstitution();

                if (institution != null) {
                  centerMap.put("name", institution.getComposedName());

                  String institutionType = Optional.ofNullable(institution.getInstitutionType())
                    .map(InstitutionType::getName).orElse("Not available");
                  centerMap.put("type", institutionType);
                  institution.setLocations(institution.getInstitutionsLocations().stream().filter(c -> c.isActive())
                    .collect(Collectors.toList()));

                  String headquarter = Optional.ofNullable(institution.getLocations())
                    .flatMap(
                      locations -> locations.stream().filter(loc -> loc.isHeadquater() && loc.getLocElement() != null)
                        .map(loc -> loc.getLocElement().getComposedName()).findFirst())
                    .orElse("Not available");
                  centerMap.put("headquarter", headquarter);
                }

                return centerMap;
              }).collect(Collectors.toList());

              ObjectMapper objectMapper = new ObjectMapper();
              objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
              centers = objectMapper.writeValueAsString(centersList);
            } else {
              centers = null;
            }
          }
        } catch (Exception e) {
          System.out.println("Error generating expected study centers JSON: " + e.getMessage());
        }

        // Geographic Scope comment
        if ((projectExpectedStudyInfo.getScopeComments() != null)
          && !projectExpectedStudyInfo.getScopeComments().trim().isEmpty()) {

          scopeComments = this.htmlParser.plainTextToHtml(projectExpectedStudyInfo.getScopeComments());

          /*
           * Get short url calling tinyURL service
           */
          if (activateShortURL) {
            try {
              scopeComments = urlShortener.detectAndShortenLinks(scopeComments);
            } catch (Exception e) {
              LOG.error("Error shortening URL: " + e.getMessage());
            }
          }

        }
        // Key Contributions
        // CRPs/Platforms
        final List<ProjectExpectedStudyCrp> studyCrpsList = projectExpectedStudy.getProjectExpectedStudyCrps().stream()
          .filter(s -> s.isActive() && (s.getPhase() != null) && s.getPhase().equals(phase))
          .collect(Collectors.toList());
        final Set<String> crpsSet = new HashSet<>();
        if ((studyCrpsList != null) && !studyCrpsList.isEmpty()) {
          for (final ProjectExpectedStudyCrp studyCrp : studyCrpsList) {
            crpsSet.add("; " + studyCrp.getGlobalUnit().getComposedName());
          }
          crps = String.join("", crpsSet);
        }
        // Crp Programs
        final List<ProjectExpectedStudyFlagship> studyProgramsList =
          projectExpectedStudy.getProjectExpectedStudyFlagships().stream()
            .filter(s -> s.isActive() && (s.getPhase() != null) && s.getPhase().equals(phase))
            .collect(Collectors.toList());
        // Flagships
        final List<ProjectExpectedStudyFlagship> studyFlagshipList = studyProgramsList.stream()
          .filter(f -> f.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue())
          .collect(Collectors.toList());
        final Set<String> flaghipsSet = new HashSet<>();
        if ((studyFlagshipList != null) && !studyFlagshipList.isEmpty()) {
          for (final ProjectExpectedStudyFlagship studyFlagship : studyFlagshipList) {
            flaghipsSet.add("; " + studyFlagship.getCrpProgram().getComposedName());
          }
          flagships = String.join("", flaghipsSet);
        }
        // Regional Programs
        final List<ProjectExpectedStudyFlagship> studyRegionsList = studyProgramsList.stream()
          .filter(f -> f.getCrpProgram().getProgramType() == ProgramType.REGIONAL_PROGRAM_TYPE.getValue())
          .collect(Collectors.toList());
        final Set<String> regionSet = new HashSet<>();
        if ((studyRegionsList != null) && !studyRegionsList.isEmpty()) {
          for (final ProjectExpectedStudyFlagship studyFlagship : studyRegionsList) {
            regionSet.add("; " + studyFlagship.getCrpProgram().getComposedName());
          }
          regionalPrograms = String.join("", regionSet);
        }
        // External Partners
        final List<ProjectExpectedStudyInstitution> studyInstitutionList =
          projectExpectedStudy.getProjectExpectedStudyInstitutions().stream()
            .filter(s -> s.isActive() && (s.getPhase() != null) && s.getPhase().equals(phase))
            .collect(Collectors.toList());

        // Institutions
        if (!studyInstitutionList.isEmpty()) {
          final List<InstitutionDTO> institutionList = studyInstitutionList.stream().map(studyInstitution -> {
            final Institution institution = studyInstitution.getInstitution();
            if (institution == null) {
              return null;
            }

            final String institutionType =
              Optional.ofNullable(institution.getInstitutionType()).map(InstitutionType::getName).orElse(null);

            institution.setLocations(
              institution.getInstitutionsLocations().stream().filter(c -> c.isActive()).collect(Collectors.toList()));

            final String headquarter =
              Optional.ofNullable(institution.getLocations()).map(locations -> locations.stream()
                .filter(location -> location.isHeadquater() && location.getLocElement() != null).map(location -> {
                  LocElement locElement = locElementManager.getLocElementById(location.getLocElement().getId());
                  return (locElement != null && locElement.getName() != null) ? locElement.getName() : "";
                }).filter(name -> !name.isEmpty()).findFirst().orElse("")).orElse("");

            return new InstitutionDTO(institution.getComposedName(), institutionType, headquarter);
          }).filter(Objects::nonNull).collect(Collectors.toList());

          // Convert to JSON
          final ObjectMapper objectMapper = new ObjectMapper();
          objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

          try {
            final String institutionsJson = objectMapper.writeValueAsString(institutionList);
            institutions = institutionsJson;
          } catch (final JsonProcessingException e) {
            e.printStackTrace();
          }
        }
        // cgiarInnovations
        if (projectExpectedStudyInfo.getCgiarInnovation() != null) {
          cgiarInnovation = projectExpectedStudyInfo.getCgiarInnovation();
        }
        // Innovations
        /*
         * final List<ProjectExpectedStudyInnovation> studyInnovationList =
         * projectExpectedStudy.getProjectExpectedStudyInnovations().stream()
         * .filter(s -> s.isActive() && (s.getPhase() != null) && s.getPhase().equals(phase))
         * .collect(Collectors.toList());
         * final Set<String> innovationSet = new HashSet<>();
         * if ((studyInnovationList != null) && !studyInnovationList.isEmpty()) {
         * for (final ProjectExpectedStudyInnovation studyInnovation : studyInnovationList) {
         * studyInnovation.getProjectInnovation().getProjectInnovationInfo(phase);
         * final String composedName = studyInnovation.getProjectInnovation().getComposedName();
         * if ((composedName != null) && !composedName.isEmpty()) {
         * innovationSet.add(composedName);
         * }
         * }
         * cgiarInnovations = String.join(", ", innovationSet);
         * }
         */
        // Innovations
        List<ProjectExpectedStudyInnovation> studyInnovationList =
          projectExpectedStudy.getProjectExpectedStudyInnovations().stream()
            .filter(s -> s.isActive() && (s.getPhase() != null) && s.getPhase().equals(phase))
            .collect(Collectors.toList());

        List<Map<String, String>> innovationList = new ArrayList<>();

        for (ProjectExpectedStudyInnovation studyInnovation : studyInnovationList) {
          studyInnovation.getProjectInnovation().getProjectInnovationInfo(phase);
          String composedName = studyInnovation.getProjectInnovation().getComposedName();
          String code = studyInnovation.getProjectInnovation().getId() + "";
          String pdf = "Comming soon";

          if (composedName != null && !composedName.isEmpty()) {
            Map<String, String> innovation = new HashMap<>();
            innovation.put("code", code);
            innovation.put("title", composedName);
            innovation.put("pdf", pdf);
            innovationList.add(innovation);
          }
        }

        ObjectMapper objectMapper = new ObjectMapper();
        try {
          cgiarInnovations = objectMapper.writeValueAsString(innovationList);
        } catch (JsonProcessingException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }

        // otherCrossCuttingSelection
        otherCrossCuttingSelection = projectExpectedStudyInfo.getOtherCrossCuttingSelection();

        // Elaboration of Outcome/Impact Statement
        if ((projectExpectedStudyInfo.getElaborationOutcomeImpactStatement() != null)
          && !projectExpectedStudyInfo.getElaborationOutcomeImpactStatement().trim().isEmpty()) {
          elaborationOutcomeImpactStatement =
            this.htmlParser.plainTextToHtml(projectExpectedStudyInfo.getElaborationOutcomeImpactStatement());
        }


        // New references
        // Expected Study Reference List
        try {
          if (projectExpectedStudy.getProjectExpectedStudyReferences() != null) {
            projectExpectedStudy.setReferences(new ArrayList<>(projectExpectedStudy.getProjectExpectedStudyReferences()
              .stream().filter(o -> (o != null) && (o.getId() != null) && o.getPhase().getId().equals(phase.getId()))
              .sorted((o1, o2) -> Comparator.comparing(ProjectExpectedStudyReference::getId).compare(o1, o2))
              .collect(Collectors.toList())));
          }

          if ((projectExpectedStudy.getReferences() != null) && !projectExpectedStudy.getReferences().isEmpty()) {
            // List to store reference data with numbering
            final List<Map<String, Object>> referenceList = new ArrayList<>();

            // Counter for numbering
            int count = 1;
            for (final ProjectExpectedStudyReference reference : projectExpectedStudy.getReferences()) {
              if ((reference != null) && ((reference.getReference() != null) || (reference.getLink() != null))) {
                final Map<String, Object> referenceMap = new LinkedHashMap<>(); // Maintain insertion order
                String countString = count + "";
                referenceMap.put("code", countString);
                referenceMap.put("title", reference.getReference());
                referenceMap.put("link", reference.getLink());
                referenceMap.put("externalAuthor", reference.getExternalAuthor());

                referenceList.add(referenceMap);
                count++;
              }
            }

            // Convert the list to a JSON string
            objectMapper = new ObjectMapper();
            try {
              studiesReference = objectMapper.writeValueAsString(referenceList);
            } catch (final JsonProcessingException e) {
              e.printStackTrace();
            }
            /*
             * Get short url calling tinyURL service
             */
            if (activateShortURL) {
              try {
                referenceText = urlShortener.detectAndShortenLinks(studiesReference);
              } catch (final Exception e) {
                LOG.error("Failed to shorten URL: " + e.getMessage());
              }
            } else {
              // If not using short URL, clean the HTML
              referenceText = studiesReference;
            }
          }
        } catch (final Exception e) {
          LOG.error("Failed to get new reference information: " + e.getMessage());
        }

        // References cited
        try {
          if ((referenceText == null) || ((referenceText != null) && referenceText.isEmpty())) {
            if ((projectExpectedStudyInfo.getReferencesText() != null)
              && !projectExpectedStudyInfo.getReferencesText().trim().isEmpty()) {
              studiesReference = this.htmlParser.plainTextToHtml(projectExpectedStudyInfo.getReferencesText());
              if (activateShortURL) {
                referenceText = urlShortener.detectAndShortenLinks(studiesReference);
              } else {
                referenceText = studiesReference;
              }
            }
          }
        } catch (final Exception e) {
          LOG.error("Failed to get reference text: " + e.getMessage());
        }

        // MELIA publications
        if (projectExpectedStudyInfo.getMELIAPublications() != null) {
          if (!projectExpectedStudyInfo.getMELIAPublications().contains(" ")) {
            if (activateShortURL) {
              meliaPublications = urlShortener.detectAndShortenLinks(projectExpectedStudyInfo.getMELIAPublications());
            } else {
              meliaPublications = projectExpectedStudyInfo.getMELIAPublications();
            }
          } else {
            try {
              final int firstSpace = projectExpectedStudyInfo.getMELIAPublications().indexOf(" ");
              if (activateShortURL) {
                meliaPublications = urlShortener
                  .detectAndShortenLinks(projectExpectedStudyInfo.getMELIAPublications().substring(0, firstSpace));
              } else {
                meliaPublications = projectExpectedStudyInfo.getMELIAPublications().substring(0, firstSpace);
              }
              // Append the rest of the string after the first space
              meliaPublications += projectExpectedStudyInfo.getMELIAPublications().substring(firstSpace + 1);
            } catch (final Exception e) {
              throw e;
            }
          }
        }

        // Cluster Acronym
        if (projectExpectedStudy.getProject().getAcronym() != null) {
          clusterAcronym = projectExpectedStudy.getProject().getAcronym();
        } else {
          clusterAcronym = "C" + projectExpectedStudy.getProject().getId();
        }

        // Quantifications
        final StringBuilder quantification = new StringBuilder();
        if (projectExpectedStudy.getProjectExpectedStudyQuantifications() != null) {
          projectExpectedStudy
            .setQuantifications(new ArrayList<>(projectExpectedStudy.getProjectExpectedStudyQuantifications().stream()
              .filter(o -> o.isActive() && o.getPhase().getId().equals(phase.getId())).collect(Collectors.toList())));

          final List<QuantificationDTO> quantificationsList = new ArrayList<>();

          for (final ProjectExpectedStudyQuantification quantificationItem : projectExpectedStudy
            .getQuantifications()) {
            if ((quantificationItem.getQuantificationType().getName() != null)
              || (quantificationItem.getNumber() != null) || (quantificationItem.getTargetUnit() != null)
              || (quantificationItem.getComments() != null)) {
              quantificationsList.add(new QuantificationDTO(quantificationItem.getQuantificationType().getName(),
                quantificationItem.getNumber() + "", quantificationItem.getTargetUnit() + "",
                quantificationItem.getComments()));
            }
          }

          // Convert list to JSON
          objectMapper = new ObjectMapper();
          try {
            String quantificationsJson = objectMapper.writeValueAsString(quantificationsList);
            // Append properly formatted JSON to the existing string
            quantification.append(quantificationsJson);

          } catch (final JsonProcessingException e) {
            e.printStackTrace();
          }
        }

        // Cluster Name
        if ((projectExpectedStudy.getProject().getProjecInfoPhase(phase) != null)
          && (projectExpectedStudy.getProject().getProjecInfoPhase(phase).getTitle() != null)) {
          clusterName = projectExpectedStudy.getProject().getAcronym();
        } else {
          clusterName = "C" + projectExpectedStudy.getProject().getId();
        }

        // Lead Person
        try {
          leadPerson = projectExpectedStudy.getProject().getLeaderPerson(this.getActualPhase()).getUser()
            .getComposedNameWithoutEmail();
        } catch (final Exception e) {
          Log.error("error getting leader " + e);
        }

        // Is Alliance Contribution
        isAllianceContribution = this.isAllianceSelected(projectExpectedStudy) ? "Yes" : "No";

        // Reason Not CGIAR Contribution
        if (projectExpectedStudyInfo.getReasonNotCgiarContribution() != null) {
          reasonNotCgiarContribution = projectExpectedStudyInfo.getReasonNotCgiarContribution();
        }

        // TODO: Add Quantifications in Pentaho/MySQL

        // Gender, Youth, and Capacity Development
        // Gender
        if (projectExpectedStudyInfo.getGenderLevel() != null) {
          genderRelevance = projectExpectedStudyInfo.getGenderLevel().getPowbName();
        }
        // Youth
        if (projectExpectedStudyInfo.getYouthLevel() != null) {
          youthRelevance = projectExpectedStudyInfo.getYouthLevel().getPowbName();
        }
        // Capacity Development
        if (projectExpectedStudyInfo.getCapdevLevel() != null) {
          capacityRelevance = projectExpectedStudyInfo.getCapdevLevel().getPowbName();
        }

        // Climate change
        if (projectExpectedStudyInfo.getClimateChangeLevel() != null) {
          climateRelevance = projectExpectedStudyInfo.getClimateChangeLevel().getPowbName();
        }

        if ((projectExpectedStudyInfo.getOtherCrossCuttingSelection() != null)
          && !projectExpectedStudyInfo.getOtherCrossCuttingSelection().isEmpty()) {
        }
        // Other cross-cutting dimensions
        if ((projectExpectedStudyInfo.getOtherCrossCuttingDimensions() != null)
          && !projectExpectedStudyInfo.getOtherCrossCuttingDimensions().trim().isEmpty()) {
          otherCrossCuttingDimensions =
            this.htmlParser.plainTextToHtml(projectExpectedStudyInfo.getOtherCrossCuttingDimensions());
        }

        // Covid Analysis
        if (projectExpectedStudyInfo.getHasCovidAnalysis() != null) {
          if (projectExpectedStudyInfo.getHasCovidAnalysis()) {
            covidAnalysis = "Yes";
          } else {
            covidAnalysis = "No";

          }
        }

        // Performance indicator
        // Expected Study Project Outcome list
        if ((projectExpectedStudyInfo.getProjectExpectedStudy() != null)
          && (projectExpectedStudyInfo.getProjectExpectedStudy().getProjectExpectedStudyCrpOutcomes() != null)) {
          projectExpectedStudyInfo.getProjectExpectedStudy()
            .setCrpOutcomes(new ArrayList<>(projectExpectedStudyInfo.getProjectExpectedStudy()
              .getProjectExpectedStudyCrpOutcomes().stream()
              .filter(o -> o.getPhase().getId().equals(this.getSelectedPhase().getId())).collect(Collectors.toList())));
        }

        try {
          List<String> crpOutcomeList = new ArrayList<>();

          if (projectExpectedStudyInfo.getProjectExpectedStudy().getCrpOutcomes() != null) {
            for (ProjectExpectedStudyCrpOutcome outcome : projectExpectedStudyInfo.getProjectExpectedStudy()
              .getCrpOutcomes()) {
              if (outcome != null && outcome.getCrpOutcome() != null
                && outcome.getCrpOutcome().getDescription() != null) {
                crpOutcomeList.add(outcome.getCrpOutcome().getDescription());
              }
            }
          }

          objectMapper = new ObjectMapper();
          performanceIndicator = objectMapper.writeValueAsString(crpOutcomeList);
        } catch (Exception e) {
          System.out.println("Error getting performance indicators: " + e.getMessage());
        }


        /*
         * Generate link url from parameters
         */
        if ((projectExpectedStudyInfo.getIsPublic() != null) && projectExpectedStudyInfo.getIsPublic()
          && (projectExpectedStudyInfo.getPhase() != null) && (this.getBaseUrl() != null)) {
          link = this.getBaseUrl() + "/projects/" + this.getCrpSession() + "/studySummary.do?studyID="
            + projectExpectedStudyInfo.getProjectExpectedStudy().getId() + "&cycle=Reporting&year=" + phase.getYear();
        }
        // Projects
        final List<ExpectedStudyProject> studyProjectList =
          projectExpectedStudyInfo.getProjectExpectedStudy().getExpectedStudyProjects().stream()
            .filter(e -> e.isActive() && (e.getPhase() != null) && e.getPhase().equals(phase))
            .sorted((sp1, sp2) -> sp2.getProject().getId().compareTo(sp1.getProject().getId()))
            .collect(Collectors.toList());
        final Set<String> studyProjectSet = new HashSet<>();


        /**
         * Alliance Tab
         */
        try {
          if (projectExpectedStudy.getProjectExpectedStudyAllianceLeversOutcomes() != null) {

            List<ProjectExpectedStudyAllianceLeversOutcome> allianceLeversList =
              projectExpectedStudy.getProjectExpectedStudyAllianceLeversOutcomes().stream()
                .filter(
                  o -> o != null && o.isActive() && o.getId() != null && o.getPhase().getId().equals(phase.getId()))
                .collect(Collectors.toList());

            projectExpectedStudy.setAllianceLeversOutcomes(allianceLeversList);

            if (!allianceLeversList.isEmpty()) {
              ProjectExpectedStudyAllianceLeversOutcome firstLeverOutcome = allianceLeversList.get(0);

              if (firstLeverOutcome.getAllianceLever() != null) {
                Map<String, Object> primaryLeverMap = new LinkedHashMap<>();
                String name = firstLeverOutcome.getAllianceLever().getName();
                String description = firstLeverOutcome.getAllianceLever().getDescription();

                primaryLeverMap.put("code", this.extractCodeFromName(name));
                primaryLeverMap.put("name", name);
                primaryLeverMap.put("description", description);

                List<String> strategicOutcomes = allianceLeversList.stream()
                  .filter(o -> o.getAllianceLever() != null
                    && o.getAllianceLever().getId().equals(firstLeverOutcome.getAllianceLever().getId()))
                  .map(o -> o.getAllianceLeverOutcome().getDescription()).filter(Objects::nonNull).distinct()
                  .collect(Collectors.toList());

                primaryLeverMap.put("strategicOutcome", strategicOutcomes);

                objectMapper = new ObjectMapper();
                objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
                primaryAllianceLever = objectMapper.writeValueAsString(primaryLeverMap);
              }
            }
          }

          if (projectExpectedStudy.getProjectExpectedStudySdgAllianceLevers() != null) {
            projectExpectedStudy.setSdgAllianceLevers(null);
          }

        } catch (Exception e) {
          Logger log = LoggerFactory.getLogger(this.getClass());
          log.error("Error processing primary alliance levers and strategic outcomes: ", e);
        }


        // Primary SDG contribution
        try {
          if (projectExpectedStudy.getProjectExpectedStudySdgAllianceLevers() != null) {
            List<ProjectExpectedStudySdgAllianceLever> sdgAllianceLeversList =
              projectExpectedStudy.getProjectExpectedStudySdgAllianceLevers().stream()
                .filter(
                  o -> o != null && o.isActive() && o.getId() != null && o.getPhase().getId().equals(phase.getId()))
                .collect(Collectors.toList());

            projectExpectedStudy.setSdgAllianceLevers(sdgAllianceLeversList);

            if (!sdgAllianceLeversList.isEmpty()) {
              List<String> primarySDGList = new ArrayList<>();
              List<String> relatedSDGList = new ArrayList<>();
              List<String> relatedLeverList = new ArrayList<>();

              for (ProjectExpectedStudySdgAllianceLever sdgAllianceLever : sdgAllianceLeversList) {
                if (sdgAllianceLever.getsDGContribution() != null
                  && sdgAllianceLever.getsDGContribution().getName() != null) {

                  if (sdgAllianceLever.getIsPrimary()) {
                    primarySDGList.add(sdgAllianceLever.getsDGContribution().getCode() + " "
                      + sdgAllianceLever.getsDGContribution().getName());
                  } else {
                    relatedSDGList.add(sdgAllianceLever.getsDGContribution().getCode() + " "
                      + sdgAllianceLever.getsDGContribution().getName());

                    if (sdgAllianceLever.getAllianceLever() != null
                      && sdgAllianceLever.getAllianceLever().getName() != null
                      && sdgAllianceLever.getAllianceLever().getDescription() != null) {

                      String relatedLeverEntry = sdgAllianceLever.getAllianceLever().getName() + ": "
                        + sdgAllianceLever.getAllianceLever().getDescription();

                      if (!relatedLeverList.contains(relatedLeverEntry)) {
                        relatedLeverList.add(relatedLeverEntry);
                      }
                    }
                  }
                }
              }

              objectMapper = new ObjectMapper();
              objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

              primarySDGcontribution = objectMapper.writeValueAsString(primarySDGList);
              relatedSDGContribution = objectMapper.writeValueAsString(relatedSDGList);
              relatedLever = objectMapper.writeValueAsString(relatedLeverList);
            }
          }
        } catch (Exception e) {
          System.out.println("Error processing SDG Alliance Levers: " + e);
        }


        /*
         * One CGIAR Tab
         */

        // Has CGIAR Contribution
        if (projectExpectedStudyInfo.getHasCgiarContribution() != null) {
          hasCGIARContribution = projectExpectedStudyInfo.getHasCgiarContribution() ? "Yes" : "No";
        }

        // Impact Area
        try {
          final StringBuilder globalTargetsBuilder = new StringBuilder();

          // Filter Impact Areas
          if (projectExpectedStudy.getProjectExpectedStudyImpactAreas() != null) {
            final List<ProjectExpectedStudyImpactArea> filteredImpactAreas = projectExpectedStudy
              .getProjectExpectedStudyImpactAreas().stream().filter(o -> (o != null) && (o.getId() != null)
                && o.isActive() && (o.getPhase() != null) && o.getPhase().getId().equals(phase.getId()))
              .collect(Collectors.toList());

            projectExpectedStudy.setImpactAreas(filteredImpactAreas);

            // Impact area code
            if ((filteredImpactAreas != null) && !filteredImpactAreas.isEmpty()) {
              final ImpactArea impactAreaTemp = filteredImpactAreas.get(0).getImpactArea();
              if ((impactAreaTemp != null) && (impactAreaTemp.getId() != null)) {
                impactArea = impactAreaTemp.getName();
                impactAreaCode = String.valueOf(impactAreaTemp.getId());
              }
            }
          }

          // Filter Expected Study Global Targets
          if (projectExpectedStudy.getProjectExpectedStudyGlobalTargets() != null) {
            final List<ProjectExpectedStudyGlobalTarget> filteredGlobalTargets = projectExpectedStudy
              .getProjectExpectedStudyGlobalTargets().stream().filter(o -> (o != null) && (o.getId() != null)
                && o.isActive() && (o.getPhase() != null) && o.getPhase().getId().equals(phase.getId()))
              .collect(Collectors.toList());

            projectExpectedStudy.setGlobalTargets(filteredGlobalTargets);

            if (!filteredGlobalTargets.isEmpty()) {
              final Set<String> uniqueTargets = new HashSet<>();

              for (final ProjectExpectedStudyGlobalTarget target : filteredGlobalTargets) {
                if ((target.getGlobalTarget() != null) && (target.getGlobalTarget().getName() != null)
                  && (target.getGlobalTarget().getDescription() != null)) {
                  globalTargets += "; " + target.getGlobalTarget().getName();
                  final String formattedTarget = "; " + target.getGlobalTarget().getName();

                  if (!uniqueTargets.contains(formattedTarget)) {
                    uniqueTargets.add(formattedTarget);
                    globalTargetsBuilder.append(formattedTarget);
                  }
                }
              }
            }
          }
          if (globalTargets != null) {
            globalTargets = globalTargets.replace("null", "");
          }

        } catch (final NullPointerException e) {
          Log.error("NullPointerException while getting Impact Areas", e);
        } catch (final Exception e) {
          Log.error("Unexpected error while getting Impact Areas", e);
        }

        /*
         * Communications tab
         */

        // Links
        try {
          List<ProjectExpectedStudyLink> linksList =
            projectExpectedStudyInfo.getProjectExpectedStudy().getProjectExpectedStudyLinks().stream()
              .filter(s -> s.isActive() && s.getPhase() != null && s.getPhase().equals(phase))
              .sorted(Comparator.comparing(ProjectExpectedStudyLink::getId)).collect(Collectors.toList());


          List<String> shortLinks = null;
          if (activateShortURL) {
            linksList.stream().map(linkV -> linkV.getLink()).filter(linkV -> linkV != null && !linkV.isEmpty())
              .map(urlShortener::getShortUrlService).distinct().collect(Collectors.toList());
          } else {
            linksList.stream().map(linkV -> linkV.getLink()).filter(linkV -> linkV != null && !linkV.isEmpty())
              .distinct().collect(Collectors.toList());
          }

          objectMapper = new ObjectMapper();
          objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
          links = objectMapper.writeValueAsString(shortLinks);

        } catch (Exception e) {
          System.out.println("Error processing links: " + e.getMessage());
        }

        // Expected Study Publications List
        if (projectExpectedStudy.getProjectExpectedStudyPublications() != null) {
          projectExpectedStudy
            .setPublications(new ArrayList<>(projectExpectedStudy.getProjectExpectedStudyPublications().stream()
              .filter(
                o -> (o != null) && (o.getId() != null) && o.isActive() && o.getPhase().getId().equals(phase.getId()))
              .collect(Collectors.toList())));
        }
        final StringBuilder publications = new StringBuilder();

        // Ensure publications list exists
        if ((projectExpectedStudy.getPublications() != null) && !projectExpectedStudy.getPublications().isEmpty()) {
          final List<PublicationDTO> publicationsList = new ArrayList<>();

          for (final ProjectExpectedStudyPublication publication : projectExpectedStudy.getPublications()) {
            if ((publication.getName() != null) || (publication.getPosition() != null)
              || (publication.getAffiliation() != null)) {
              publicationsList.add(
                new PublicationDTO(publication.getName(), publication.getPosition(), publication.getAffiliation()));
            }
          }

          // Convert list to JSON
          objectMapper = new ObjectMapper();
          try {
            String publicationsJson = objectMapper.writeValueAsString(publicationsList);

            // Append properly formatted JSON to the existing string
            publications.append("").append(publicationsJson);

          } catch (final JsonProcessingException e) {
            e.printStackTrace();
          }
        }

        // Partners persons
        try {
          if (projectExpectedStudy.getProjectExpectedStudyPartnerships() != null) {

            final List<ProjectExpectedStudyPartnership> deList =
              projectExpectedStudy.getProjectExpectedStudyPartnerships().stream()
                .filter(dp -> dp.isActive() && dp.getPhase().getId().equals(this.getActualPhase().getId())
                  && dp.getProjectExpectedStudyPartnerType().getId()
                    .equals(APConstants.DELIVERABLE_PARTNERSHIP_TYPE_RESPONSIBLE))
                .collect(Collectors.toList());

            if ((deList != null) && !deList.isEmpty()) {
              projectExpectedStudy.setPartnerships(new ArrayList<>());
              for (final ProjectExpectedStudyPartnership projectExpectedStudyPartnership : deList) {

                if (projectExpectedStudyPartnership.getProjectExpectedStudyPartnershipsPersons() != null) {
                  final List<ProjectExpectedStudyPartnershipsPerson> partnershipPersons =
                    new ArrayList<>(projectExpectedStudyPartnership.getProjectExpectedStudyPartnershipsPersons()
                      .stream().filter(ProjectExpectedStudyPartnershipsPerson::isActive).collect(Collectors.toList()));
                  projectExpectedStudyPartnership.setPartnershipPersons(partnershipPersons);
                }
                projectExpectedStudy.getPartnerships().add(projectExpectedStudyPartnership);
              }

            }
          }
        } catch (Exception e) {
          System.out.println("error in Partners persons" + e);
        }


        final StringBuilder persons = new StringBuilder();
        try {
          final List<ProjectExpectedStudyPartnership> partnerships = projectExpectedStudy.getPartnerships();
          if ((partnerships != null) && !partnerships.isEmpty()) {
            final ProjectExpectedStudyPartnership partnerTemp = partnerships.get(0);

            if (partnerTemp != null) {
              final Institution institution = partnerTemp.getInstitution();
              if ((institution != null) && (institution.getComposedName() != null)) {
                persons.append(institution.getComposedName());
              }

              final List<ProjectExpectedStudyPartnershipsPerson> personsList = partnerTemp.getPartnershipPersons();
              if ((personsList != null) && !personsList.isEmpty()) {
                final String personsDetails = personsList.stream().map(ProjectExpectedStudyPartnershipsPerson::getUser)
                  .filter(Objects::nonNull).map(User::getComposedName).filter(Objects::nonNull).map(name -> name)
                  .collect(Collectors.joining());

                persons.append(personsDetails);
              }
            }
          }
          contacts += persons.toString().replace("null", "");
        } catch (Exception e) {
          System.out.println("Error in partnerships " + e);
        }

        // Shared clusters
        try {
          List<String> studyProjectListJson = new ArrayList<>();

          if (studyProjectList != null && !studyProjectList.isEmpty()) {
            for (final ExpectedStudyProject studyProject : studyProjectList) {
              if (studyProject.getProject().getAcronym() != null) {
                if (!studyProjectSet.contains(studyProject.getProject().getAcronym())) {
                  studyProjectSet.add(studyProject.getProject().getAcronym());
                  studyProjectListJson.add(studyProject.getProject().getAcronym());
                }
              } else {
                String projectId = "C" + studyProject.getProject().getId();
                if (!studyProjectSet.contains(projectId)) {
                  studyProjectSet.add(projectId);
                  studyProjectListJson.add(projectId);
                }
              }
            }
          }

          objectMapper = new ObjectMapper();
          studyProjects = objectMapper.writeValueAsString(studyProjectListJson);
        } catch (Exception e) {
          System.out.println("Error in study projects " + e);
        }

        if (relatedLever.isEmpty()) {
          relatedLever = null;
        }
        contacts = contacts.replace("null", "");
        /**
         * Generate Json
         */
        final Map<String, Object> jsonMainRoot = new HashMap<>();
        final Map<String, Object> jsonRoot = new HashMap<>();
        final Map<String, Object> jsonData = new HashMap<>();
        final Map<String, Object> jsonOptions = new HashMap<>();
        final Map<String, Object> jsonCredentials = new HashMap<>();
        final Map<String, String> headerMap = new HashMap<>();
        final Map<String, String> footerMap = new HashMap<>();

        try {
          jsonData.put("id", id);
          jsonData.put("year", year);
          jsonData.put("title", title);
          jsonData.put("commissioningStudy", commissioningStudy);
          jsonData.put("status", status);
          jsonData.put("type", type);
          jsonData.put("outcomeImpactStatement", outcomeImpactStatement);
          jsonData.put("isContributionText", isContributionText);
          jsonData.put("stageStudy", stageStudy);
          jsonData.put("srfTargets", srfTargets);
          jsonData.put("subIdos", subIdos);
          jsonData.put("topLevelComments", topLevelComments);
          jsonData.put("geographicScopes", geographicScopes);
          jsonData.put("regions", removeLeadingSemicolon(regions));
          jsonData.put("countries", removeLeadingSemicolon(countries));
          jsonData.put("scopeComments", scopeComments);
          jsonData.put("crps", crps);
          jsonData.put("flagships", flagships);
          jsonData.put("regionalPrograms", regionalPrograms);
          jsonData.put("institutions", removeLeadingSemicolon(institutions));
          jsonData.put("elaborationOutcomeImpactStatement", elaborationOutcomeImpactStatement);
          jsonData.put("referenceText", referenceText);
          jsonData.put("quantification", quantification);
          jsonData.put("genderRelevance", genderRelevance);
          jsonData.put("youthRelevance", youthRelevance);
          jsonData.put("capacityRelevance", capacityRelevance);
          jsonData.put("otherCrossCuttingDimensions", otherCrossCuttingDimensions);
          jsonData.put("communicationsMaterial", communicationsMaterial);
          jsonData.put("contacts", removeLeadingSemicolon(contacts));
          jsonData.put("studyProjects", removeLeadingSemicolon(studyProjects));
          jsonData.put("tagged", tagged);
          jsonData.put("cgiarInnovation", cgiarInnovation);
          jsonData.put("cgiarInnovations", cgiarInnovations);
          jsonData.put("climateRelevance", climateRelevance);
          jsonData.put("link", link);
          jsonData.put("links", links);
          jsonData.put("studyPolicies", studyPolicies);
          jsonData.put("url", url);
          jsonData.put("studiesReference", studiesReference);
          jsonData.put("meliaPublications", meliaPublications);
          jsonData.put("performanceIndicator", performanceIndicator);
          jsonData.put("covidAnalysis", covidAnalysis);
          jsonData.put("centers", removeLeadingSemicolon(centers));
          jsonData.put("clusterAcronym", clusterAcronym);
          jsonData.put("allianceOICRID", allianceOICRID);
          jsonData.put("primaryAllianceLever", primaryAllianceLever);
          jsonData.put("strategicOutcome", removeLeadingSemicolon(strategicOutcome));
          jsonData.put("primarySDGcontribution", removeLeadingSemicolon(primarySDGcontribution));
          jsonData.put("relatedLever", removeLeadingSemicolon(relatedLever));
          jsonData.put("relatedSDGContribution", removeLeadingSemicolon(relatedSDGContribution));
          jsonData.put("hasCgiarContribution", hasCGIARContribution);
          jsonData.put("impactArea", impactArea);
          jsonData.put("globalTargets", removeLeadingSemicolon(globalTargets));
          jsonData.put("publications", publications);
          jsonData.put("tagAs", tagAs);
          jsonData.put("clusterName", clusterName);
          jsonData.put("leadPerson", leadPerson);
          jsonData.put("isAllianceContribution", isAllianceContribution);
          jsonData.put("impactAreaCode", impactAreaCode);
          jsonData.put("reasonNotCgiarContribution", reasonNotCgiarContribution);
          jsonData.put("otherCrossCuttingSelection", otherCrossCuttingSelection);
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
          this.OICRsReportName =
            "AICCRA-OICR" + projectExpectedStudy.getId() + "-Summary-" + this.getCurrentDateTime() + ".pdf";

          jsonRoot.put("templateData", this.OICRsTemplateData);
          jsonRoot.put("fileName", this.OICRsReportName);
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
            final FileWriter fileWriter = new FileWriter(new File("D:/OICRs_Report.json"));
            fileWriter.write(jsonOutput);
            fileWriter.close();
          } catch (Exception e) {
            System.out.println("Generated just for local environments");
          }

          System.out.println("send microservice class: " + this.OICRsReportName);
          this.microserviceReportAction.sendOICRsQueueMessage(jsonOutput, this.OICRsReportName);

          return jsonOutput;
        } catch (final IOException e) {
          System.out.println("error generating json " + e);
          return "{}";
        }

      }

    }
    return SUCCESS;
  }

  public TypedTableModel getCaseStudiesTableModel(List<ProjectExpectedStudyInfo> projectExpectedStudyInfos) {

    final TypedTableModel model = new TypedTableModel(
      new String[] {"id", "year", "title", "commissioningStudy", "status", "type", "outcomeImpactStatement",
        "isContributionText", "stageStudy", "srfTargets", "subIdos", "topLevelComments", "geographicScopes", "regions",
        "countries", "scopeComments", "crps", "flagships", "regionalPrograms", "institutions",
        "elaborationOutcomeImpactStatement", "referenceText", "quantification", "genderRelevance", "youthRelevance",
        "capacityRelevance", "otherCrossCuttingDimensions", "comunicationsMaterial", "contacts", "studyProjects",
        "tagged", "cgiarInnovation", "cgiarInnovations", "climateRelevance", "link", "links", "studyPolicies",
        "isSrfTargetText", "otherCrossCuttingDimensionsSelection", "isContribution", "isRegional", "isNational",
        "isOutcomeCaseStudy", "isSrfTarget", "url", "studiesReference", "meliaPublications", "performanceIndicator",
        "covidAnalysis", "centers", "clusterAcronym", "allianceOICRID", "primaryAllianceLever", "strategicOutcome",
        "primarySDGcontribution", "relatedLever", "relatedSDGContribution", "hasCgiarContribution", "impactArea",
        "publications", "tagAs"},
      new Class[] {Long.class, Integer.class, String.class, String.class, String.class, String.class, String.class,
        String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class,
        String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class,
        String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class,
        String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class,
        Boolean.class, Boolean.class, Boolean.class, Boolean.class, Boolean.class, String.class, String.class,
        String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class,
        String.class, String.class, String.class, String.class, String.class, String.class, String.class},
      0);

    final URLShortener urlShortener = new URLShortener();
    if ((projectExpectedStudyInfos != null) && !projectExpectedStudyInfos.isEmpty()) {
      projectExpectedStudyInfos
        .sort((p1, p2) -> p1.getProjectExpectedStudy().getId().compareTo(p2.getProjectExpectedStudy().getId()));
      for (final ProjectExpectedStudyInfo projectExpectedStudyInfo : projectExpectedStudyInfos) {

        Long id = null;
        Integer year = null;
        String title = null, commissioningStudy = null, status = null, type = null, outcomeImpactStatement = null;
        final String isContributionText = null;
        String stageStudy = null, srfTargets = null, subIdos = null, topLevelComments = null, geographicScopes = null,
          regions = null, countries = null, scopeComments = null, crps = null, flagships = null,
          regionalPrograms = null, institutions = null, elaborationOutcomeImpactStatement = null, referenceText = null;
        final String quantification = null;
        String genderRelevance = null, youthRelevance = null, capacityRelevance = null,
          otherCrossCuttingDimensions = null, comunicationsMaterial = null, contacts = null, studyProjects = null,
          tagged = null, cgiarInnovation = null, cgiarInnovations = null, climateRelevance = null, link = null,
          links = null;
        final String studyPolicies = null;
        String isSrfTargetText = null, otherCrossCuttingDimensionsSelection = null;
        final String url = null;
        String studiesReference = null, meliaPublications = null, performanceIndicator = null, covidAnalysis = null,
          centers = null, clusterAcronym = null, allianceOICRID = null, primaryAllianceLever = null,
          strategicOutcome = null, primarySDGcontribution = null, relatedLever = "", relatedSDGContribution = null,
          hasCGIARContribution = null, impactArea = null, tagAs = null;

        final Boolean isContribution = false;
        Boolean isRegional = false, isNational = false, isOutcomeCaseStudy = false, isSrfTarget = false;
        final ProjectExpectedStudy projectExpectedStudy = projectExpectedStudyInfo.getProjectExpectedStudy();
        id = projectExpectedStudy.getId();

        // Alliance OICR ID
        if (projectExpectedStudyInfo.getAllianceOicr() != null) {
          allianceOICRID = projectExpectedStudyInfo.getAllianceOicr();
        }
        // Tag As
        if ((projectExpectedStudyInfo.getTag() != null) && (projectExpectedStudyInfo.getTag().getTagName() != null)) {
          tagAs = projectExpectedStudyInfo.getTag().getTagName();
        }
        // Type
        if (projectExpectedStudyInfo.getStudyType() != null) {
          type = projectExpectedStudyInfo.getStudyType().getName();
          if (projectExpectedStudyInfo.getStudyType().getId().intValue() == 1) {
            isOutcomeCaseStudy = true;
          }
        }
        // Status
        if (projectExpectedStudyInfo.getStatus() != null) {
          status = projectExpectedStudyInfo.getStatus().getName();
        }
        // Year
        if (projectExpectedStudy.getProjectExpectedStudyInfo(this.getSelectedPhase()).getYear() != null) {
          year = projectExpectedStudy.getProjectExpectedStudyInfo(this.getSelectedPhase()).getYear();
        }
        // Tagged
        if ((projectExpectedStudyInfo != null) && (projectExpectedStudyInfo.getEvidenceTag() != null)
          && (projectExpectedStudyInfo.getEvidenceTag().getName() != null)) {
          tagged = projectExpectedStudyInfo.getEvidenceTag().getName();
        }
        // Title
        if ((projectExpectedStudyInfo.getTitle() != null) && !projectExpectedStudyInfo.getTitle().trim().isEmpty()) {
          title = projectExpectedStudyInfo.getTitle();
        }
        // Commissioning Study
        if ((projectExpectedStudyInfo.getCommissioningStudy() != null)
          && !projectExpectedStudyInfo.getCommissioningStudy().trim().isEmpty()) {
          commissioningStudy = projectExpectedStudyInfo.getCommissioningStudy();
        }
        // Outcome Impact Statement
        if ((projectExpectedStudyInfo.getOutcomeImpactStatement() != null)
          && !projectExpectedStudyInfo.getOutcomeImpactStatement().trim().isEmpty()) {
          outcomeImpactStatement = projectExpectedStudyInfo.getOutcomeImpactStatement();
        }
        // Communications materials
        if ((projectExpectedStudyInfo.getComunicationsMaterial() != null)
          && !projectExpectedStudyInfo.getComunicationsMaterial().trim().isEmpty()) {
          comunicationsMaterial = this.htmlParser.plainTextToHtml(projectExpectedStudyInfo.getComunicationsMaterial());
        }

        // isContribution
        /*
         * if (projectExpectedStudyInfo.getIsContribution() != null) {
         * isContribution = projectExpectedStudyInfo.getIsContribution();
         * isContributionText = projectExpectedStudyInfo.getIsContribution() ? "Yes" : "No";
         * if (isContribution) {
         * // Policies Contribution
         * List<ProjectExpectedStudyPolicy> studyPoliciesList =
         * projectExpectedStudyInfo.getProjectExpectedStudy().getProjectExpectedStudyPolicies().stream()
         * .filter(s -> s.isActive() && s.getPhase() != null && s.getPhase().equals(this.getSelectedPhase()))
         * .collect(Collectors.toList());
         * Set<String> studyPoliciesSet = new HashSet<>();
         * if (studyPoliciesList != null && studyPoliciesList.size() > 0) {
         * for (ProjectExpectedStudyPolicy projectExpectedStudyPolicy : studyPoliciesList) {
         * if (projectExpectedStudyPolicy.getProjectPolicy()
         * .getProjectPolicyInfo(this.getSelectedPhase()) != null) {
         * studyPoliciesSet.add(
         * "<br>&nbsp;&nbsp;&nbsp;&nbsp;● " + projectExpectedStudyPolicy.getProjectPolicy().getComposedName());
         * }
         * }
         * studyPolicies = String.join("", studyPoliciesSet);
         * }
         * }
         * }
         */
        // Level of maturity
        if (projectExpectedStudyInfo.getRepIndStageStudy() != null) {
          stageStudy = projectExpectedStudyInfo.getRepIndStageStudy().getName();
        }
        // SubIdos
        final List<ProjectExpectedStudySubIdo> subIdosList = projectExpectedStudy.getProjectExpectedStudySubIdos()
          .stream().filter(s -> s.isActive() && (s.getPhase() != null) && s.getPhase().equals(this.getSelectedPhase()))
          .collect(Collectors.toList());
        final Set<String> subIdoSet = new HashSet<>();
        if ((subIdosList != null) && !subIdosList.isEmpty()) {
          for (final ProjectExpectedStudySubIdo studySrfTarget : subIdosList) {
            subIdoSet.add("<br>&nbsp;&nbsp;&nbsp;&nbsp;● " + studySrfTarget.getSrfSubIdo().getDescription());
          }
          subIdos = String.join("", subIdoSet);
        }
        // is SRF Target
        if ((projectExpectedStudyInfo.getIsSrfTarget() != null)
          && !projectExpectedStudyInfo.getIsSrfTarget().isEmpty()) {
          isSrfTargetText = this.getText("study." + projectExpectedStudyInfo.getIsSrfTarget());
          if (projectExpectedStudyInfo.getIsSrfTarget().equals("targetsOptionYes")) {
            isSrfTarget = true;
            // SRF Targets
            final List<ProjectExpectedStudySrfTarget> studySrfTargets =
              projectExpectedStudy.getProjectExpectedStudySrfTargets().stream()
                .filter(s -> s.isActive() && (s.getPhase() != null) && s.getPhase().equals(this.getSelectedPhase()))
                .collect(Collectors.toList());
            final Set<String> srfTargetSet = new HashSet<>();
            if ((studySrfTargets != null) && !studySrfTargets.isEmpty()) {
              for (final ProjectExpectedStudySrfTarget studySrfTarget : studySrfTargets) {
                srfTargetSet.add("<br>&nbsp;&nbsp;&nbsp;&nbsp;● " + studySrfTarget.getSrfSloIndicator().getTitle());
              }
              srfTargets = String.join("", srfTargetSet);
            }
          }
        }
        // Comments
        if ((projectExpectedStudyInfo.getTopLevelComments() != null)
          && !projectExpectedStudyInfo.getTopLevelComments().trim().isEmpty()) {
          topLevelComments = this.htmlParser.plainTextToHtml(projectExpectedStudyInfo.getTopLevelComments());
        }
        // Geographic Scopes
        final List<ProjectExpectedStudyGeographicScope> geographicScopeList =
          projectExpectedStudy.getProjectExpectedStudyGeographicScopes().stream()
            .filter(s -> s.isActive() && (s.getPhase() != null) && s.getPhase().equals(this.getSelectedPhase()))
            .collect(Collectors.toList());
        final Set<String> geographicScopeSet = new HashSet<>();
        if ((geographicScopeList != null) && !geographicScopeList.isEmpty()) {
          for (final ProjectExpectedStudyGeographicScope geographicScope : geographicScopeList) {
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
          final List<ProjectExpectedStudyCountry> studyCountries = this.projectExpectedStudyCountryManager
            .getProjectExpectedStudyCountrybyPhase(projectExpectedStudy.getId(), this.getSelectedPhase().getId())
            .stream().filter(le -> le.isActive() && (le.getLocElement().getLocElementType().getId() == 2))
            .collect(Collectors.toList());
          if ((studyCountries != null) && !studyCountries.isEmpty()) {
            final Set<String> countriesSet = new HashSet<>();
            for (final ProjectExpectedStudyCountry projectExpectedStudyCountry : studyCountries) {
              countriesSet
                .add("<br>&nbsp;&nbsp;&nbsp;&nbsp; ● " + projectExpectedStudyCountry.getLocElement().getName());
            }
            countries = String.join("", countriesSet);
          }
        }

        // Region(s)
        if (isRegional) {
          final List<ProjectExpectedStudyRegion> studyRegions =
            projectExpectedStudy.getProjectExpectedStudyRegions().stream()
              .filter(c -> c.isActive() && (c.getPhase() != null) && c.getPhase().equals(this.getSelectedPhase()))
              .collect(Collectors.toList());
          if ((studyRegions != null) && !studyRegions.isEmpty()) {
            final Set<String> regionsSet = new HashSet<>();
            for (final ProjectExpectedStudyRegion projectExpectedStudyRegion : studyRegions) {
              regionsSet.add("<br>&nbsp;&nbsp;&nbsp;&nbsp; ● " + projectExpectedStudyRegion.getLocElement().getName());
            }
            regions = String.join("", regionsSet);
          }
        }
        // Centers(s)
        final List<ProjectExpectedStudyCenter> studyCenters = projectExpectedStudy.getProjectExpectedStudyCenters()
          .stream().filter(c -> c.isActive() && (c.getPhase() != null) && c.getPhase().equals(this.getSelectedPhase()))
          .collect(Collectors.toList());
        if ((studyCenters != null) && !studyCenters.isEmpty()) {
          final Set<String> centersSet = new HashSet<>();
          for (final ProjectExpectedStudyCenter projectExpectedStudyCenter : studyCenters) {

            final String institutionType =
              Optional.ofNullable(projectExpectedStudyCenter.getInstitution().getInstitutionType())
                .map(InstitutionType::getName).map(name -> " | Type: " + name).orElse("");
            try {
              projectExpectedStudyCenter.getInstitution().setLocations(projectExpectedStudyCenter.getInstitution()
                .getInstitutionsLocations().stream().filter(c -> c.isActive()).collect(Collectors.toList()));
              projectExpectedStudyCenter.getInstitution().getLocations()
                .addAll(projectExpectedStudyCenter.getInstitution().getInstitutionsLocations().stream()
                  .filter(InstitutionLocation::isActive).collect(Collectors.toList()));
            } catch (final Exception e) {
              Log.error("Error setting locations " + e);
            }
            final String headquarter = Optional.ofNullable(projectExpectedStudyCenter.getInstitution().getLocations())
              .flatMap(locations -> locations.stream()
                .filter(location -> location.isHeadquater() && (location.getLocElement() != null)
                  && (location.getLocElement().getName() != null))
                .map(location -> " | headquarter: " + location.getLocElement().getName()).findFirst())
              .orElse("");

            centersSet.add("<br>&nbsp;&nbsp;&nbsp;&nbsp; ● "
              + projectExpectedStudyCenter.getInstitution().getComposedName() + institutionType + headquarter);
          }
          centers = String.join("", centersSet);
        }

        // Geographic Scope comment
        if ((projectExpectedStudyInfo.getScopeComments() != null)
          && !projectExpectedStudyInfo.getScopeComments().trim().isEmpty()) {

          scopeComments = this.htmlParser.plainTextToHtml(projectExpectedStudyInfo.getScopeComments());

          /*
           * Get short url calling tinyURL service
           */
          scopeComments = urlShortener.detectAndShortenLinks(scopeComments);

        }
        // Key Contributions
        // CRPs/Platforms
        final List<ProjectExpectedStudyCrp> studyCrpsList = projectExpectedStudy.getProjectExpectedStudyCrps().stream()
          .filter(s -> s.isActive() && (s.getPhase() != null) && s.getPhase().equals(this.getSelectedPhase()))
          .collect(Collectors.toList());
        final Set<String> crpsSet = new HashSet<>();
        if ((studyCrpsList != null) && !studyCrpsList.isEmpty()) {
          for (final ProjectExpectedStudyCrp studyCrp : studyCrpsList) {
            crpsSet.add("<br>&nbsp;&nbsp;&nbsp;&nbsp; ● " + studyCrp.getGlobalUnit().getComposedName());
          }
          crps = String.join("", crpsSet);
        }
        // Crp Programs
        final List<ProjectExpectedStudyFlagship> studyProgramsList =
          projectExpectedStudy.getProjectExpectedStudyFlagships().stream()
            .filter(s -> s.isActive() && (s.getPhase() != null) && s.getPhase().equals(this.getSelectedPhase()))
            .collect(Collectors.toList());
        // Flagships
        final List<ProjectExpectedStudyFlagship> studyFlagshipList = studyProgramsList.stream()
          .filter(f -> f.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue())
          .collect(Collectors.toList());
        final Set<String> flaghipsSet = new HashSet<>();
        if ((studyFlagshipList != null) && !studyFlagshipList.isEmpty()) {
          for (final ProjectExpectedStudyFlagship studyFlagship : studyFlagshipList) {
            flaghipsSet.add("<br>&nbsp;&nbsp;&nbsp;&nbsp; ● " + studyFlagship.getCrpProgram().getComposedName());
          }
          flagships = String.join("", flaghipsSet);
        }
        // Regional Programs
        final List<ProjectExpectedStudyFlagship> studyRegionsList = studyProgramsList.stream()
          .filter(f -> f.getCrpProgram().getProgramType() == ProgramType.REGIONAL_PROGRAM_TYPE.getValue())
          .collect(Collectors.toList());
        final Set<String> regionSet = new HashSet<>();
        if ((studyRegionsList != null) && !studyRegionsList.isEmpty()) {
          for (final ProjectExpectedStudyFlagship studyFlagship : studyRegionsList) {
            regionSet.add("<br>&nbsp;&nbsp;&nbsp;&nbsp; ● " + studyFlagship.getCrpProgram().getComposedName());
          }
          regionalPrograms = String.join("", regionSet);
        }
        // External Partners
        final List<ProjectExpectedStudyInstitution> studyInstitutionList =
          projectExpectedStudy.getProjectExpectedStudyInstitutions().stream()
            .filter(s -> s.isActive() && (s.getPhase() != null) && s.getPhase().equals(this.getSelectedPhase()))
            .collect(Collectors.toList());

        if (!studyInstitutionList.isEmpty()) {
          final Set<String> institutionSet = studyInstitutionList.stream().map(studyInstitution -> {
            final Institution institution = studyInstitution.getInstitution();
            if (institution == null) {
              return null;
            }

            final String institutionType = Optional.ofNullable(institution.getInstitutionType())
              .map(InstitutionType::getName).map(name -> " | Type: " + name).orElse("");
            final String headquarter = Optional.ofNullable(institution.getLocations())
              .flatMap(locations -> locations.stream()
                .filter(location -> location.isHeadquater() && (location.getLocElement() != null)
                  && (location.getLocElement().getComposedName() != null))
                .map(location -> " | headquarter: " + location.getLocElement().getComposedName()).findFirst())
              .orElse("");

            return "<br>&nbsp;&nbsp;&nbsp;&nbsp; ● " + institution.getComposedName() + institutionType + headquarter;
          }).filter(Objects::nonNull).collect(Collectors.toSet());

          institutions = String.join(" ", institutionSet);
        }
        // cgiarInnovations
        if (projectExpectedStudyInfo.getCgiarInnovation() != null) {
          cgiarInnovation = projectExpectedStudyInfo.getCgiarInnovation();
        }
        // Innovations
        final List<ProjectExpectedStudyInnovation> studyInnovationList =
          projectExpectedStudy.getProjectExpectedStudyInnovations().stream()
            .filter(s -> s.isActive() && (s.getPhase() != null) && s.getPhase().equals(this.getSelectedPhase()))
            .collect(Collectors.toList());
        final Set<String> innovationSet = new HashSet<>();
        if ((studyInnovationList != null) && !studyInnovationList.isEmpty()) {
          for (final ProjectExpectedStudyInnovation studyInnovation : studyInnovationList) {
            studyInnovation.getProjectInnovation().getProjectInnovationInfo(this.getSelectedPhase());
            final String composedName = studyInnovation.getProjectInnovation().getComposedName();
            if ((composedName != null) && !composedName.isEmpty()) {
              innovationSet.add(composedName);
            }
          }
          cgiarInnovations = String.join(", ", innovationSet);

        }
        // Elaboration of Outcome/Impact Statement
        if ((projectExpectedStudyInfo.getElaborationOutcomeImpactStatement() != null)
          && !projectExpectedStudyInfo.getElaborationOutcomeImpactStatement().trim().isEmpty()) {
          elaborationOutcomeImpactStatement =
            this.htmlParser.plainTextToHtml(projectExpectedStudyInfo.getElaborationOutcomeImpactStatement());
        }


        // New references
        // Expected Study Reference List
        try {
          if (projectExpectedStudy.getProjectExpectedStudyReferences() != null) {
            projectExpectedStudy.setReferences(new ArrayList<>(projectExpectedStudy.getProjectExpectedStudyReferences()
              .stream()
              .filter(
                o -> (o != null) && (o.getId() != null) && o.getPhase().getId().equals(this.getSelectedPhase().getId()))
              .sorted((o1, o2) -> Comparator.comparing(ProjectExpectedStudyReference::getId).compare(o1, o2))
              .collect(Collectors.toList())));
          }

          if (projectExpectedStudy.getReferences() != null) {
            int count = 1;
            for (final ProjectExpectedStudyReference reference : projectExpectedStudy.getReferences()) {
              if (reference != null) {
                if (studiesReference == null) {
                  studiesReference = "&nbsp;&nbsp;&nbsp;&nbsp;" + count + ". ";
                  // studiesReference = "&nbsp;&nbsp;&nbsp;&nbsp;● ";
                } else {
                  studiesReference += " <br>&nbsp;&nbsp;&nbsp;&nbsp;" + count + ". ";
                  // studiesReference += "<br>&nbsp;&nbsp;&nbsp;&nbsp;● ";
                }
                if (reference.getReference() != null) {
                  studiesReference += reference.getReference() + " | ";
                }
                if (reference.getLink() != null) {
                  studiesReference += reference.getLink();
                }

                if ((reference.getExternalAuthor() != null) && reference.getExternalAuthor()) {
                  studiesReference += " (External Author)";
                }
                count++;
              }
            }
            /*
             * Get short url calling tinyURL service
             */
            referenceText = urlShortener.detectAndShortenLinks(studiesReference);
          }
        } catch (final Exception e) {
          LOG.error("Failed to get new reference information: " + e.getMessage());
        }

        // References cited
        try {
          if ((referenceText == null) || ((referenceText != null) && referenceText.isEmpty())) {
            if ((projectExpectedStudyInfo.getReferencesText() != null)
              && !projectExpectedStudyInfo.getReferencesText().trim().isEmpty()) {
              studiesReference = this.htmlParser.plainTextToHtml(projectExpectedStudyInfo.getReferencesText());
              referenceText = urlShortener.detectAndShortenLinks(studiesReference);
            }
          }
        } catch (final Exception e) {
          LOG.error("Failed to get reference text: " + e.getMessage());
        }

        // MELIA publications
        if (projectExpectedStudyInfo.getMELIAPublications() != null) {
          if (!projectExpectedStudyInfo.getMELIAPublications().contains(" ")) {
            meliaPublications = urlShortener.detectAndShortenLinks(projectExpectedStudyInfo.getMELIAPublications());
          } else {
            try {
              final int firstSpace = projectExpectedStudyInfo.getMELIAPublications().indexOf(" ");
              meliaPublications = urlShortener
                .detectAndShortenLinks(projectExpectedStudyInfo.getMELIAPublications().substring(0, firstSpace));
              meliaPublications += projectExpectedStudyInfo.getMELIAPublications().substring(firstSpace + 1);
            } catch (final Exception e) {
              throw e;
            }
          }
        }

        // Cluster Acronum
        if (projectExpectedStudy.getProject().getAcronym() != null) {
          clusterAcronym = projectExpectedStudy.getProject().getAcronym();
        } else {
          clusterAcronym = "C" + projectExpectedStudy.getProject().getId();
        }

        // TODO: Add Quantifications in Pentaho/MySQL

        // Gender, Youth, and Capacity Development
        // Gender
        if (projectExpectedStudyInfo.getGenderLevel() != null) {
          genderRelevance = projectExpectedStudyInfo.getGenderLevel().getPowbName();
          if (!projectExpectedStudyInfo.getGenderLevel().getId().equals(1l)
            && !projectExpectedStudyInfo.getGenderLevel().getId().equals(4l)
            && (projectExpectedStudyInfo.getDescribeGender() != null)
            && !projectExpectedStudyInfo.getDescribeGender().isEmpty()) {
            genderRelevance += "<br>" + this.getText("study.achievementsGenderRelevance.readText") + ": "
              + this.htmlParser.plainTextToHtml(projectExpectedStudyInfo.getDescribeGender());
          }
        }
        // Youth
        if (projectExpectedStudyInfo.getYouthLevel() != null) {
          youthRelevance = projectExpectedStudyInfo.getYouthLevel().getPowbName();
          if (!projectExpectedStudyInfo.getYouthLevel().getId().equals(1l)
            && !projectExpectedStudyInfo.getYouthLevel().getId().equals(4l)
            && (projectExpectedStudyInfo.getDescribeYouth() != null)
            && !projectExpectedStudyInfo.getDescribeYouth().isEmpty()) {
            youthRelevance += "<br>" + this.getText("study.achievementsYouthRelevance.readText") + ": "
              + this.htmlParser.plainTextToHtml(projectExpectedStudyInfo.getDescribeYouth());
          }
        }
        // Capacity Development
        if (projectExpectedStudyInfo.getCapdevLevel() != null) {
          capacityRelevance = projectExpectedStudyInfo.getCapdevLevel().getPowbName();
          if (!projectExpectedStudyInfo.getCapdevLevel().getId().equals(1l)
            && !projectExpectedStudyInfo.getCapdevLevel().getId().equals(4l)
            && (projectExpectedStudyInfo.getDescribeCapdev() != null)
            && !projectExpectedStudyInfo.getDescribeCapdev().isEmpty()) {
            capacityRelevance += "<br>" + this.getText("study.generalInformation.achievementsCapDevRelevance.readText")
              + ": " + this.htmlParser.plainTextToHtml(projectExpectedStudyInfo.getDescribeCapdev());
          }
        }

        // Climate change
        if (projectExpectedStudyInfo.getClimateChangeLevel() != null) {
          climateRelevance = projectExpectedStudyInfo.getClimateChangeLevel().getPowbName();
          if (!projectExpectedStudyInfo.getClimateChangeLevel().getId().equals(1l)
            && !projectExpectedStudyInfo.getClimateChangeLevel().getId().equals(4l)
            && (projectExpectedStudyInfo.getDescribeClimateChange() != null)
            && !projectExpectedStudyInfo.getDescribeClimateChange().isEmpty()) {
            climateRelevance +=
              "<br>" + this.getText("study.generalInformation.achievementsClimateChangeRelevance.readText") + ": "
                + this.htmlParser.plainTextToHtml(projectExpectedStudyInfo.getDescribeClimateChange());
          }
        }

        if ((projectExpectedStudyInfo.getOtherCrossCuttingSelection() != null)
          && !projectExpectedStudyInfo.getOtherCrossCuttingSelection().isEmpty()) {
          otherCrossCuttingDimensionsSelection = projectExpectedStudyInfo.getOtherCrossCuttingSelection();
        }
        // Other cross-cutting dimensions
        if ((projectExpectedStudyInfo.getOtherCrossCuttingDimensions() != null)
          && !projectExpectedStudyInfo.getOtherCrossCuttingDimensions().trim().isEmpty()) {
          otherCrossCuttingDimensions =
            this.htmlParser.plainTextToHtml(projectExpectedStudyInfo.getOtherCrossCuttingDimensions());
        }

        // Covid Analysis
        if (projectExpectedStudyInfo.getHasCovidAnalysis() != null) {
          if (projectExpectedStudyInfo.getHasCovidAnalysis()) {
            covidAnalysis = "Yes";
          } else {
            covidAnalysis = "No";

          }
        }

        // Performance indicator
        // Expected Study Project Outcome list
        if ((projectExpectedStudyInfo.getProjectExpectedStudy() != null)
          && (projectExpectedStudyInfo.getProjectExpectedStudy().getProjectExpectedStudyCrpOutcomes() != null)) {
          projectExpectedStudyInfo.getProjectExpectedStudy()
            .setCrpOutcomes(new ArrayList<>(projectExpectedStudyInfo.getProjectExpectedStudy()
              .getProjectExpectedStudyCrpOutcomes().stream()
              .filter(o -> o.getPhase().getId().equals(this.getSelectedPhase().getId())).collect(Collectors.toList())));
        }

        if (projectExpectedStudyInfo.getProjectExpectedStudy().getCrpOutcomes() != null) {
          for (final ProjectExpectedStudyCrpOutcome outcome : projectExpectedStudyInfo.getProjectExpectedStudy()
            .getCrpOutcomes()) {
            if ((outcome != null) && (outcome.getCrpOutcome() != null)
              && (outcome.getCrpOutcome().getDescription() != null)) {

              if (performanceIndicator == null) {
                performanceIndicator = "&nbsp;&nbsp;&nbsp;&nbsp; ●" + outcome.getCrpOutcome().getDescription();
              } else {
                performanceIndicator += "<br>&nbsp;&nbsp;&nbsp;&nbsp; ●" + outcome.getCrpOutcome().getDescription();
              }
            }
          }

        }
        /*
         * Generate link url from parameters
         */
        if ((projectExpectedStudyInfo.getIsPublic() != null) && projectExpectedStudyInfo.getIsPublic()
          && (projectExpectedStudyInfo.getPhase() != null) && (this.getBaseUrl() != null)) {
          link = this.getBaseUrl() + "/projects/" + this.getCrpSession() + "/studySummary.do?studyID="
            + projectExpectedStudyInfo.getProjectExpectedStudy().getId() + "&cycle=Reporting&year="
            + this.getSelectedPhase().getYear();
        }
        // Projects
        final List<ExpectedStudyProject> studyProjectList =
          projectExpectedStudyInfo.getProjectExpectedStudy().getExpectedStudyProjects().stream()
            .filter(e -> e.isActive() && (e.getPhase() != null) && e.getPhase().equals(this.getSelectedPhase()))
            .sorted((sp1, sp2) -> sp2.getProject().getId().compareTo(sp1.getProject().getId()))
            .collect(Collectors.toList());
        final Set<String> studyProjectSet = new HashSet<>();
        /*
         * if (projectExpectedStudyInfo.getProjectExpectedStudy().getProject() != null) {
         * if (projectExpectedStudyInfo.getProjectExpectedStudy().getProject().getAcronym() != null) {
         * studyProjectSet.add("<br>&nbsp;&nbsp;&nbsp;&nbsp; ● "
         * + projectExpectedStudyInfo.getProjectExpectedStudy().getProject().getAcronym());
         * } else {
         * studyProjectSet.add("<br>&nbsp;&nbsp;&nbsp;&nbsp; ● C"
         * + projectExpectedStudyInfo.getProjectExpectedStudy().getProject().getId());
         * }
         * }
         */

        /**
         * Alliance Tab
         */
        try {
          if (projectExpectedStudy.getProjectExpectedStudyAllianceLeversOutcomes() != null) {
            projectExpectedStudy.setAllianceLeversOutcomes(new ArrayList<>(projectExpectedStudy
              .getProjectExpectedStudyAllianceLeversOutcomes().stream().filter(o -> (o != null) && o.isActive()
                && (o.getId() != null) && o.getPhase().getId().equals(this.getSelectedPhase().getId()))
              .collect(Collectors.toList())));

            if ((projectExpectedStudy.getAllianceLeversOutcomes() != null)
              && !projectExpectedStudy.getAllianceLeversOutcomes().isEmpty()) {
              for (final ProjectExpectedStudyAllianceLeversOutcome allianceLeverOutcome : projectExpectedStudy
                .getAllianceLeversOutcomes()) {

                // Primary alliance lever
                if ((allianceLeverOutcome.getAllianceLever() != null)
                  && (allianceLeverOutcome.getAllianceLever().getName() != null)
                  && (allianceLeverOutcome.getAllianceLever().getDescription() != null)) {

                  if (primaryAllianceLever == null) {
                    primaryAllianceLever +=
                      "<br>&nbsp;&nbsp;&nbsp;&nbsp; ● " + allianceLeverOutcome.getAllianceLever().getName() + ": "
                        + allianceLeverOutcome.getAllianceLever().getDescription();
                  } else {
                    if ((primaryAllianceLever != null)
                      && !primaryAllianceLever.contains(allianceLeverOutcome.getAllianceLever().getName())) {
                      primaryAllianceLever +=
                        "<br>&nbsp;&nbsp;&nbsp;&nbsp; ● " + allianceLeverOutcome.getAllianceLever().getName() + ": "
                          + allianceLeverOutcome.getAllianceLever().getDescription();
                    }
                  }
                  primaryAllianceLever = primaryAllianceLever.replace("null", "");
                }

                // Strategic outcome
                if ((allianceLeverOutcome.getAllianceLeverOutcome() != null)
                  && (allianceLeverOutcome.getAllianceLeverOutcome().getName() != null)
                  && (allianceLeverOutcome.getAllianceLeverOutcome().getDescription() != null)) {
                  strategicOutcome +=
                    "<br>&nbsp;&nbsp;&nbsp;&nbsp; ● " + allianceLeverOutcome.getAllianceLeverOutcome().getName() + ": "
                      + allianceLeverOutcome.getAllianceLeverOutcome().getDescription();
                  strategicOutcome = strategicOutcome.replace("null", "");
                }
              }
            }
          }

          if (projectExpectedStudy.getProjectExpectedStudySdgAllianceLevers() != null) {
            projectExpectedStudy.setSdgAllianceLevers(null);
          }
        } catch (final Exception e) {
          Log.error("error getting primary alliance lever");
        }

        // Primary SDG contribution
        try {
          if (projectExpectedStudy.getProjectExpectedStudySdgAllianceLevers() != null) {
            projectExpectedStudy.setSdgAllianceLevers(new ArrayList<>(projectExpectedStudy
              .getProjectExpectedStudySdgAllianceLevers().stream().filter(o -> (o != null) && o.isActive()
                && (o.getId() != null) && o.getPhase().getId().equals(this.getSelectedPhase().getId()))
              .collect(Collectors.toList())));
            if ((projectExpectedStudy.getSdgAllianceLevers() != null)
              && !projectExpectedStudy.getSdgAllianceLevers().isEmpty()) {
              for (final ProjectExpectedStudySdgAllianceLever sdgAllianceLever : projectExpectedStudy
                .getSdgAllianceLevers()) {
                if ((sdgAllianceLever != null) && (sdgAllianceLever.getsDGContribution() != null)
                  && (sdgAllianceLever.getsDGContribution() != null)
                  && (sdgAllianceLever.getsDGContribution().getName() != null)) {
                  String allianceLeverTemp = "";
                  if ((sdgAllianceLever.getAllianceLever() != null)
                    && (sdgAllianceLever.getAllianceLever().getName() != null)
                    && (sdgAllianceLever.getAllianceLever().getDescription() != null)) {
                    allianceLeverTemp = " (" + sdgAllianceLever.getAllianceLever().getName() + ") ";;
                  }
                  if (sdgAllianceLever.getIsPrimary()) {
                    primarySDGcontribution += "<br>&nbsp;&nbsp;&nbsp;&nbsp; ● " + allianceLeverTemp
                      + sdgAllianceLever.getsDGContribution().getName();
                    primarySDGcontribution = primarySDGcontribution.replace("null", "");
                  } else {
                    relatedSDGContribution += "<br>&nbsp;&nbsp;&nbsp;&nbsp; ● " + allianceLeverTemp
                      + sdgAllianceLever.getsDGContribution().getName();
                    relatedSDGContribution = relatedSDGContribution.replace("null", "");

                    // Related levers
                    if ((sdgAllianceLever.getAllianceLever() != null)
                      && (sdgAllianceLever.getAllianceLever().getName() != null)
                      && (sdgAllianceLever.getAllianceLever().getDescription() != null)
                      && !relatedLever.contains(sdgAllianceLever.getAllianceLever().getName())) {
                      relatedLever += "<br>&nbsp;&nbsp;&nbsp;&nbsp; ● " + sdgAllianceLever.getAllianceLever().getName()
                        + ": " + sdgAllianceLever.getAllianceLever().getDescription();
                    }
                  }
                }
              }

            }
          }
        } catch (final Exception e) {
          Log.error("error getting primary alliance lever");
        }


        /*
         * One CGIAR Tab
         */

        // Has CGIAR Contribution
        if (projectExpectedStudyInfo.getHasCgiarContribution() != null) {
          hasCGIARContribution = projectExpectedStudyInfo.getHasCgiarContribution() ? "Yes" : "No";
        }

        // Impact Area
        try {
          final StringBuilder impactAreaBuilder = new StringBuilder();
          final StringBuilder globalTargetsBuilder = new StringBuilder();

          // Filter Impact Areas
          if (projectExpectedStudy.getProjectExpectedStudyImpactAreas() != null) {
            final List<ProjectExpectedStudyImpactArea> filteredImpactAreas =
              projectExpectedStudy.getProjectExpectedStudyImpactAreas().stream()
                .filter(o -> (o != null) && (o.getId() != null) && o.isActive() && (o.getPhase() != null)
                  && o.getPhase().getId().equals(this.getSelectedPhase().getId()))
                .collect(Collectors.toList());

            projectExpectedStudy.setImpactAreas(filteredImpactAreas);

            if (!filteredImpactAreas.isEmpty()) {
              final ImpactArea firstImpactArea = filteredImpactAreas.get(0).getImpactArea();
              if ((firstImpactArea != null) && (firstImpactArea.getName() != null)
                && (firstImpactArea.getDescription() != null)) {
                impactAreaBuilder.append("<br>&nbsp;&nbsp;&nbsp;&nbsp; ● ").append(firstImpactArea.getName());
              }
            }
          }

          // Filter Expected Study Global Targets
          if (projectExpectedStudy.getProjectExpectedStudyGlobalTargets() != null) {
            final List<ProjectExpectedStudyGlobalTarget> filteredGlobalTargets =
              projectExpectedStudy.getProjectExpectedStudyGlobalTargets().stream()
                .filter(o -> (o != null) && (o.getId() != null) && o.isActive() && (o.getPhase() != null)
                  && o.getPhase().getId().equals(this.getSelectedPhase().getId()))
                .collect(Collectors.toList());

            projectExpectedStudy.setGlobalTargets(filteredGlobalTargets);

            if (!filteredGlobalTargets.isEmpty()) {
              globalTargetsBuilder.append("<br><br>Global Targets:&nbsp;&nbsp;&nbsp;&nbsp;");
              final Set<String> uniqueTargets = new HashSet<>();

              for (final ProjectExpectedStudyGlobalTarget target : filteredGlobalTargets) {
                if ((target.getGlobalTarget() != null) && (target.getGlobalTarget().getName() != null)
                  && (target.getGlobalTarget().getDescription() != null)) {

                  final String formattedTarget = "<br>&nbsp;&nbsp;&nbsp;&nbsp; ● " + target.getGlobalTarget().getName()
                    + ": " + target.getGlobalTarget().getDescription();

                  if (!uniqueTargets.contains(formattedTarget)) {
                    uniqueTargets.add(formattedTarget);
                    globalTargetsBuilder.append(formattedTarget);
                  }
                }
              }
            }
          }

          final String impactAreaResult = impactAreaBuilder.toString().trim();
          final String globalTargetsResult = globalTargetsBuilder.toString().trim();

          impactArea = (impactAreaResult.isEmpty() ? "" : impactAreaResult)
            + (globalTargetsResult.isEmpty() ? "" : globalTargetsResult);

        } catch (final NullPointerException e) {
          Log.error("NullPointerException while getting Impact Areas", e);
        } catch (final Exception e) {
          Log.error("Unexpected error while getting Impact Areas", e);
        }

        /*
         * Communications tab
         */

        // Links
        final List<ProjectExpectedStudyLink> linksList =
          projectExpectedStudyInfo.getProjectExpectedStudy().getProjectExpectedStudyLinks().stream()
            .filter(s -> s.isActive() && (s.getPhase() != null) && s.getPhase().equals(this.getSelectedPhase()))
            .collect(Collectors.toList());
        final Set<String> linkSet = new HashSet<>();
        if ((linksList != null) && !linksList.isEmpty()) {
          linksList.sort((l1, l2) -> l1.getId().compareTo(l2.getId()));
          for (final ProjectExpectedStudyLink projectExpectedStudyLink : linksList) {
            if (!projectExpectedStudyLink.getLink().isEmpty() && (projectExpectedStudyLink.getLink() != null)) {
              /*
               * Get short url calling tinyURL service
               */
              linkSet.add(
                "<br>&nbsp;&nbsp;&nbsp;&nbsp;● " + urlShortener.getShortUrlService(projectExpectedStudyLink.getLink()));
            }
          }
          links = String.join("", linkSet);
        }

        // Expected Study Publications List
        if (projectExpectedStudy.getProjectExpectedStudyPublications() != null) {
          projectExpectedStudy.setPublications(new ArrayList<>(projectExpectedStudy
            .getProjectExpectedStudyPublications().stream().filter(o -> (o != null) && (o.getId() != null)
              && o.isActive() && o.getPhase().getId().equals(this.getSelectedPhase().getId()))
            .collect(Collectors.toList())));
        }
        final StringBuilder publications = new StringBuilder();
        if (projectExpectedStudy.getPublications() != null) {
          int count = 1;
          for (final ProjectExpectedStudyPublication publication : projectExpectedStudy.getPublications()) {
            publications.append("<br><b>Publication ").append(count).append("</b>");
            if (publication.getName() != null) {
              publications.append("<br>&nbsp;&nbsp;&nbsp;&nbsp;● Name: ").append(publication.getName());
            }
            if (publication.getPosition() != null) {
              publications.append("<br>&nbsp;&nbsp;&nbsp;&nbsp;● Position: ").append(publication.getPosition());
            }
            if (publication.getAffiliation() != null) {
              publications.append("<br>&nbsp;&nbsp;&nbsp;&nbsp;● Affiliation: ").append(publication.getAffiliation());
            }
            count++;
          }
        }

        // Partners persons
        if (projectExpectedStudy.getProjectExpectedStudyPartnerships() != null) {

          final List<ProjectExpectedStudyPartnership> deList =
            projectExpectedStudy.getProjectExpectedStudyPartnerships().stream()
              .filter(dp -> dp.isActive() && dp.getPhase().getId().equals(this.getActualPhase().getId())
                && dp.getProjectExpectedStudyPartnerType().getId()
                  .equals(APConstants.DELIVERABLE_PARTNERSHIP_TYPE_RESPONSIBLE))
              .collect(Collectors.toList());

          if ((deList != null) && !deList.isEmpty()) {
            projectExpectedStudy.setPartnerships(new ArrayList<>());
            for (final ProjectExpectedStudyPartnership projectExpectedStudyPartnership : deList) {

              if (projectExpectedStudyPartnership.getProjectExpectedStudyPartnershipsPersons() != null) {
                final List<ProjectExpectedStudyPartnershipsPerson> partnershipPersons =
                  new ArrayList<>(projectExpectedStudyPartnership.getProjectExpectedStudyPartnershipsPersons().stream()
                    .filter(ProjectExpectedStudyPartnershipsPerson::isActive).collect(Collectors.toList()));
                projectExpectedStudyPartnership.setPartnershipPersons(partnershipPersons);
              }
              projectExpectedStudy.getPartnerships().add(projectExpectedStudyPartnership);
            }

          }
        }


        final StringBuilder persons = new StringBuilder();
        final List<ProjectExpectedStudyPartnership> partnerships = projectExpectedStudy.getPartnerships();
        if ((partnerships != null) && !partnerships.isEmpty()) {
          final ProjectExpectedStudyPartnership partnerTemp = partnerships.get(0);

          if (partnerTemp != null) {
            final Institution institution = partnerTemp.getInstitution();
            if ((institution != null) && (institution.getComposedName() != null)) {
              persons.append(institution.getComposedName());
            }

            final List<ProjectExpectedStudyPartnershipsPerson> personsList = partnerTemp.getPartnershipPersons();
            if ((personsList != null) && !personsList.isEmpty()) {
              final String personsDetails = personsList.stream().map(ProjectExpectedStudyPartnershipsPerson::getUser)
                .filter(Objects::nonNull).map(User::getComposedName).filter(Objects::nonNull)
                .map(name -> "<br>&nbsp;&nbsp;&nbsp;&nbsp; ●" + name).collect(Collectors.joining());

              persons.append(personsDetails);
            }
          }
        }
        contacts += persons.toString().replace("null", "");

        // Shared clusters
        if ((studyProjectList != null) && !studyProjectList.isEmpty()) {
          for (final ExpectedStudyProject studyProject : studyProjectList) {
            if (studyProject.getProject().getAcronym() != null) {
              if ((studyProjectSet != null) && !studyProjectSet
                .contains("<br>&nbsp;&nbsp;&nbsp;&nbsp; ● " + studyProject.getProject().getAcronym())) {
                studyProjectSet.add("<br>&nbsp;&nbsp;&nbsp;&nbsp; ● " + studyProject.getProject().getAcronym());
              }
            } else {
              if ((studyProjectSet != null)
                && !studyProjectSet.contains("<br>&nbsp;&nbsp;&nbsp;&nbsp; ● C" + studyProject.getProject().getId())) {
                studyProjectSet.add("<br>&nbsp;&nbsp;&nbsp;&nbsp; ● C" + studyProject.getProject().getId());
              }
            }
          }
        }
        if ((studyProjectSet != null) && !studyProjectSet.isEmpty()) {
          studyProjects = String.join("", studyProjectSet);
        }

        if (relatedLever.isEmpty()) {
          relatedLever = null;
        }
        contacts = contacts.replace("null", "");

        model.addRow(new Object[] {id, year, title, commissioningStudy, status, type, outcomeImpactStatement,
          isContributionText, stageStudy, srfTargets, subIdos, topLevelComments, geographicScopes, regions, countries,
          scopeComments, crps, flagships, regionalPrograms, institutions, elaborationOutcomeImpactStatement,
          referenceText, quantification, genderRelevance, youthRelevance, capacityRelevance,
          otherCrossCuttingDimensions, comunicationsMaterial, contacts, studyProjects, tagged, cgiarInnovation,
          cgiarInnovations, climateRelevance, link, links, studyPolicies, isSrfTargetText,
          otherCrossCuttingDimensionsSelection, isContribution, isRegional, isNational, isOutcomeCaseStudy, isSrfTarget,
          url, studiesReference, meliaPublications, performanceIndicator, covidAnalysis, centers, clusterAcronym,
          allianceOICRID, primaryAllianceLever, strategicOutcome, primarySDGcontribution, relatedLever,
          relatedSDGContribution, hasCGIARContribution, impactArea, publications, tagAs});

      }
      // this.generateAndSendJson(projectExpectedStudyInfos);

    }

    return model;
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

  /**
   * Validate the Alliance center selection
   *
   * @param action base action
   * @param project related project
   * @param projectExpectedStudy An specific projectExpectedStudy
   * @param saving related action
   */
  public boolean isAllianceSelected(ProjectExpectedStudy projectExpectedStudy) {
    // Validate if the Alliance institution is selected
    if ((projectExpectedStudy != null) && (projectExpectedStudy.getCenters() != null)) {
      try {
        for (final ProjectExpectedStudyPartnership center : projectExpectedStudy.getCenters()) {
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
        if (reportConfiguration.getOicrTemplateData() != null) {
          OICRsTemplateData = reportConfiguration.getOicrTemplateData();
        }

      }

      this.bucketName = this.config.getMicroserviceBucketname();

    } catch (final Exception e) {
      System.out.println("error getting report configuration data " + e);
    }
  }
}
