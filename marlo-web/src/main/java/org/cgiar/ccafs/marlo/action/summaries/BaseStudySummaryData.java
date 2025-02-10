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

import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyCountryManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.model.ExpectedStudyProject;
import org.cgiar.ccafs.marlo.data.model.ImpactArea;
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
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyInfo;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyInnovation;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyInstitution;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyLink;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyReference;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyRegion;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudySdgAllianceLever;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudySrfTarget;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudySubIdo;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.HTMLParser;
import org.cgiar.ccafs.marlo.utils.URLShortener;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.jfree.util.Log;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.util.TypedTableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseStudySummaryData extends BaseSummariesAction {

  private static final long serialVersionUID = -3643067302492726266L;

  private static Logger LOG = LoggerFactory.getLogger(BaseStudySummaryData.class);

  private final HTMLParser htmlParser;
  private final ProjectExpectedStudyCountryManager projectExpectedStudyCountryManager;

  public BaseStudySummaryData(APConfig config, GlobalUnitManager crpManager, PhaseManager phaseManager,
    ProjectManager projectManager, HTMLParser htmlParser,
    ProjectExpectedStudyCountryManager projectExpectedStudyCountryManager) {
    super(config, crpManager, phaseManager, projectManager);
    this.htmlParser = htmlParser;
    this.projectExpectedStudyCountryManager = projectExpectedStudyCountryManager;
  }

  /**
   * Method to add i8n parameters to masterReport in Pentaho
   * 
   * @param masterReport
   * @return masterReport with i8n parameters added
   */
  public MasterReport addi8nParameters(MasterReport masterReport) {
    masterReport.getParameterValues().put("i8nStudies", this.getText("menu.studies"));
    masterReport.getParameterValues().put("i8nStudiesRNoData", this.getText("summaries.study.noData"));
    masterReport.getParameterValues().put("i8nStudiesMainTitle", this.getText("summaries.study.mainTitle"));
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


    return masterReport;
  }

  public TypedTableModel getCaseStudiesTableModel(List<ProjectExpectedStudyInfo> projectExpectedStudyInfos) {

    TypedTableModel model = new TypedTableModel(
      new String[] {"id", "year", "title", "commissioningStudy", "status", "type", "outcomeImpactStatement",
        "isContributionText", "stageStudy", "srfTargets", "subIdos", "topLevelComments", "geographicScopes", "regions",
        "countries", "scopeComments", "crps", "flagships", "regionalPrograms", "institutions",
        "elaborationOutcomeImpactStatement", "referenceText", "quantification", "genderRelevance", "youthRelevance",
        "capacityRelevance", "otherCrossCuttingDimensions", "comunicationsMaterial", "contacts", "studyProjects",
        "tagged", "cgiarInnovation", "cgiarInnovations", "climateRelevance", "link", "links", "studyPolicies",
        "isSrfTargetText", "otherCrossCuttingDimensionsSelection", "isContribution", "isRegional", "isNational",
        "isOutcomeCaseStudy", "isSrfTarget", "url", "studiesReference", "meliaPublications", "performanceIndicator",
        "covidAnalysis", "centers", "clusterAcronym", "allianceOICRID", "primaryAllianceLever", "strategicOutcome",
        "primarySDGcontribution", "relatedLever", "relatedSDGContribution", "hasCgiarContribution", "impactArea"},
      new Class[] {Long.class, Integer.class, String.class, String.class, String.class, String.class, String.class,
        String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class,
        String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class,
        String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class,
        String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class,
        Boolean.class, Boolean.class, Boolean.class, Boolean.class, Boolean.class, String.class, String.class,
        String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class,
        String.class, String.class, String.class, String.class, String.class},
      0);

    URLShortener urlShortener = new URLShortener();
    if (projectExpectedStudyInfos != null && !projectExpectedStudyInfos.isEmpty()) {
      projectExpectedStudyInfos
        .sort((p1, p2) -> p1.getProjectExpectedStudy().getId().compareTo(p2.getProjectExpectedStudy().getId()));
      for (ProjectExpectedStudyInfo projectExpectedStudyInfo : projectExpectedStudyInfos) {

        Long id = null;
        Integer year = null;
        String title = null, commissioningStudy = null, status = null, type = null, outcomeImpactStatement = null,
          isContributionText = null, stageStudy = null, srfTargets = null, subIdos = null, topLevelComments = null,
          geographicScopes = null, regions = null, countries = null, scopeComments = null, crps = null,
          flagships = null, regionalPrograms = null, institutions = null, elaborationOutcomeImpactStatement = null,
          referenceText = null, quantification = null, genderRelevance = null, youthRelevance = null,
          capacityRelevance = null, otherCrossCuttingDimensions = null, comunicationsMaterial = null, contacts = null,
          studyProjects = null, tagged = null, cgiarInnovation = null, cgiarInnovations = null, climateRelevance = null,
          link = null, links = null, studyPolicies = null, isSrfTargetText = null,
          otherCrossCuttingDimensionsSelection = null, url = null, studiesReference = null, meliaPublications = null,
          performanceIndicator = null, covidAnalysis = null, centers = null, clusterAcronym = null,
          allianceOICRID = null, primaryAllianceLever = null, strategicOutcome = null, primarySDGcontribution = null,
          relatedLever = null, relatedSDGContribution = null, hasCGIARContribution = null, impactArea = null;

        Boolean isContribution = false, isRegional = false, isNational = false, isOutcomeCaseStudy = false,
          isSrfTarget = false;

        id = projectExpectedStudyInfo.getProjectExpectedStudy().getId();

        // Alliance OICR ID
        if (projectExpectedStudyInfo.getAllianceOicr() != null) {
          allianceOICRID = projectExpectedStudyInfo.getAllianceOicr();
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
        if (projectExpectedStudyInfo.getProjectExpectedStudy().getProjectExpectedStudyInfo(this.getSelectedPhase())
          .getYear() != null) {
          year = projectExpectedStudyInfo.getProjectExpectedStudy().getProjectExpectedStudyInfo(this.getSelectedPhase())
            .getYear();
        }
        // Tagged
        if (projectExpectedStudyInfo != null && projectExpectedStudyInfo.getEvidenceTag() != null
          && projectExpectedStudyInfo.getEvidenceTag().getName() != null) {
          tagged = projectExpectedStudyInfo.getEvidenceTag().getName();
        }
        // Title
        if (projectExpectedStudyInfo.getTitle() != null && !projectExpectedStudyInfo.getTitle().trim().isEmpty()) {
          title = projectExpectedStudyInfo.getTitle();
        }
        // Commissioning Study
        if (projectExpectedStudyInfo.getCommissioningStudy() != null
          && !projectExpectedStudyInfo.getCommissioningStudy().trim().isEmpty()) {
          commissioningStudy = projectExpectedStudyInfo.getCommissioningStudy();
        }
        // Outcome Impact Statement
        if (projectExpectedStudyInfo.getOutcomeImpactStatement() != null
          && !projectExpectedStudyInfo.getOutcomeImpactStatement().trim().isEmpty()) {
          outcomeImpactStatement = projectExpectedStudyInfo.getOutcomeImpactStatement();
        }
        // Communications materials
        if (projectExpectedStudyInfo.getComunicationsMaterial() != null
          && !projectExpectedStudyInfo.getComunicationsMaterial().trim().isEmpty()) {
          comunicationsMaterial = htmlParser.plainTextToHtml(projectExpectedStudyInfo.getComunicationsMaterial());
        }
        // Links

        List<ProjectExpectedStudyLink> linksList =
          projectExpectedStudyInfo.getProjectExpectedStudy().getProjectExpectedStudyLinks().stream()
            .filter(s -> s.isActive() && s.getPhase() != null && s.getPhase().equals(this.getSelectedPhase()))
            .collect(Collectors.toList());
        Set<String> linkSet = new HashSet<>();
        if (linksList != null && !linksList.isEmpty()) {
          linksList.sort((l1, l2) -> l1.getId().compareTo(l2.getId()));
          for (ProjectExpectedStudyLink projectExpectedStudyLink : linksList) {
            if (!projectExpectedStudyLink.getLink().isEmpty() && projectExpectedStudyLink.getLink() != null) {
              /*
               * Get short url calling tinyURL service
               */
              linkSet.add(
                "<br>&nbsp;&nbsp;&nbsp;&nbsp;● " + urlShortener.getShortUrlService(projectExpectedStudyLink.getLink()));
            }
          }
          links = String.join("", linkSet);
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
        List<ProjectExpectedStudySubIdo> subIdosList =
          projectExpectedStudyInfo.getProjectExpectedStudy().getProjectExpectedStudySubIdos().stream()
            .filter(s -> s.isActive() && s.getPhase() != null && s.getPhase().equals(this.getSelectedPhase()))
            .collect(Collectors.toList());
        Set<String> subIdoSet = new HashSet<>();
        if (subIdosList != null && !subIdosList.isEmpty()) {
          for (ProjectExpectedStudySubIdo studySrfTarget : subIdosList) {
            subIdoSet.add("<br>&nbsp;&nbsp;&nbsp;&nbsp;● " + studySrfTarget.getSrfSubIdo().getDescription());
          }
          subIdos = String.join("", subIdoSet);
        }
        // is SRF Target
        if (projectExpectedStudyInfo.getIsSrfTarget() != null && !projectExpectedStudyInfo.getIsSrfTarget().isEmpty()) {
          isSrfTargetText = this.getText("study." + projectExpectedStudyInfo.getIsSrfTarget());
          if (projectExpectedStudyInfo.getIsSrfTarget().equals("targetsOptionYes")) {
            isSrfTarget = true;
            // SRF Targets
            List<ProjectExpectedStudySrfTarget> studySrfTargets =
              projectExpectedStudyInfo.getProjectExpectedStudy().getProjectExpectedStudySrfTargets().stream()
                .filter(s -> s.isActive() && s.getPhase() != null && s.getPhase().equals(this.getSelectedPhase()))
                .collect(Collectors.toList());
            Set<String> srfTargetSet = new HashSet<>();
            if (studySrfTargets != null && !studySrfTargets.isEmpty()) {
              for (ProjectExpectedStudySrfTarget studySrfTarget : studySrfTargets) {
                srfTargetSet.add("<br>&nbsp;&nbsp;&nbsp;&nbsp;● " + studySrfTarget.getSrfSloIndicator().getTitle());
              }
              srfTargets = String.join("", srfTargetSet);
            }
          }
        }
        // Comments
        if (projectExpectedStudyInfo.getTopLevelComments() != null
          && !projectExpectedStudyInfo.getTopLevelComments().trim().isEmpty()) {
          topLevelComments = htmlParser.plainTextToHtml(projectExpectedStudyInfo.getTopLevelComments());
        }
        // Geographic Scopes
        List<ProjectExpectedStudyGeographicScope> geographicScopeList =
          projectExpectedStudyInfo.getProjectExpectedStudy().getProjectExpectedStudyGeographicScopes().stream()
            .filter(s -> s.isActive() && s.getPhase() != null && s.getPhase().equals(this.getSelectedPhase()))
            .collect(Collectors.toList());
        Set<String> geographicScopeSet = new HashSet<>();
        if (geographicScopeList != null && !geographicScopeList.isEmpty()) {
          for (ProjectExpectedStudyGeographicScope geographicScope : geographicScopeList) {
            if (!geographicScope.getRepIndGeographicScope().getId().equals(this.getReportingIndGeographicScopeGlobal())
              && !geographicScope.getRepIndGeographicScope().getId()
                .equals(this.getReportingIndGeographicScopeRegional())) {
              isNational = true;
            }
            if (geographicScope.getRepIndGeographicScope().getId()
              .equals(this.getReportingIndGeographicScopeRegional())) {
              isRegional = true;
            }
            geographicScopeSet
              .add("<br>&nbsp;&nbsp;&nbsp;&nbsp;● " + geographicScope.getRepIndGeographicScope().getName());
          }
          geographicScopes = String.join("", geographicScopeSet);
        }

        // Country(s)
        if (isNational) {
          List<ProjectExpectedStudyCountry> studyCountries = this.projectExpectedStudyCountryManager
            .getProjectExpectedStudyCountrybyPhase(projectExpectedStudyInfo.getProjectExpectedStudy().getId(),
              this.getSelectedPhase().getId())
            .stream().filter(le -> le.isActive() && le.getLocElement().getLocElementType().getId() == 2)
            .collect(Collectors.toList());
          if (studyCountries != null && !studyCountries.isEmpty()) {
            Set<String> countriesSet = new HashSet<>();
            for (ProjectExpectedStudyCountry projectExpectedStudyCountry : studyCountries) {
              countriesSet
                .add("<br>&nbsp;&nbsp;&nbsp;&nbsp; ● " + projectExpectedStudyCountry.getLocElement().getName());
            }
            countries = String.join("", countriesSet);
          }
        }
        // Region(s)
        if (isRegional) {
          List<ProjectExpectedStudyRegion> studyRegions =
            projectExpectedStudyInfo.getProjectExpectedStudy().getProjectExpectedStudyRegions().stream()
              .filter(c -> c.isActive() && c.getPhase() != null && c.getPhase().equals(this.getSelectedPhase()))
              .collect(Collectors.toList());
          if (studyRegions != null && !studyRegions.isEmpty()) {
            Set<String> regionsSet = new HashSet<>();
            for (ProjectExpectedStudyRegion projectExpectedStudyRegion : studyRegions) {
              regionsSet.add("<br>&nbsp;&nbsp;&nbsp;&nbsp; ● " + projectExpectedStudyRegion.getLocElement().getName());
            }
            regions = String.join("", regionsSet);
          }
        }
        // Centers(s)

        List<ProjectExpectedStudyCenter> studyCenters =
          projectExpectedStudyInfo.getProjectExpectedStudy().getProjectExpectedStudyCenters().stream()
            .filter(c -> c.isActive() && c.getPhase() != null && c.getPhase().equals(this.getSelectedPhase()))
            .collect(Collectors.toList());
        if (studyCenters != null && !studyCenters.isEmpty()) {
          Set<String> centersSet = new HashSet<>();
          for (ProjectExpectedStudyCenter projectExpectedStudyCenter : studyCenters) {
            centersSet
              .add("<br>&nbsp;&nbsp;&nbsp;&nbsp; ● " + projectExpectedStudyCenter.getInstitution().getComposedName());
          }
          centers = String.join("", centersSet);
        }

        // Geographic Scope comment
        if (projectExpectedStudyInfo.getScopeComments() != null
          && !projectExpectedStudyInfo.getScopeComments().trim().isEmpty()) {

          scopeComments = htmlParser.plainTextToHtml(projectExpectedStudyInfo.getScopeComments());

          /*
           * Get short url calling tinyURL service
           */
          scopeComments = urlShortener.detectAndShortenLinks(scopeComments);

        }
        // Key Contributions
        // CRPs/Platforms
        List<ProjectExpectedStudyCrp> studyCrpsList =
          projectExpectedStudyInfo.getProjectExpectedStudy().getProjectExpectedStudyCrps().stream()
            .filter(s -> s.isActive() && s.getPhase() != null && s.getPhase().equals(this.getSelectedPhase()))
            .collect(Collectors.toList());
        Set<String> crpsSet = new HashSet<>();
        if (studyCrpsList != null && !studyCrpsList.isEmpty()) {
          for (ProjectExpectedStudyCrp studyCrp : studyCrpsList) {
            crpsSet.add("<br>&nbsp;&nbsp;&nbsp;&nbsp; ● " + studyCrp.getGlobalUnit().getComposedName());
          }
          crps = String.join("", crpsSet);
        }
        // Crp Programs
        List<ProjectExpectedStudyFlagship> studyProgramsList =
          projectExpectedStudyInfo.getProjectExpectedStudy().getProjectExpectedStudyFlagships().stream()
            .filter(s -> s.isActive() && s.getPhase() != null && s.getPhase().equals(this.getSelectedPhase()))
            .collect(Collectors.toList());
        // Flagships
        List<ProjectExpectedStudyFlagship> studyFlagshipList = studyProgramsList.stream()
          .filter(f -> f.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue())
          .collect(Collectors.toList());
        Set<String> flaghipsSet = new HashSet<>();
        if (studyFlagshipList != null && !studyFlagshipList.isEmpty()) {
          for (ProjectExpectedStudyFlagship studyFlagship : studyFlagshipList) {
            flaghipsSet.add("<br>&nbsp;&nbsp;&nbsp;&nbsp; ● " + studyFlagship.getCrpProgram().getComposedName());
          }
          flagships = String.join("", flaghipsSet);
        }
        // Regional Programs
        List<ProjectExpectedStudyFlagship> studyRegionsList = studyProgramsList.stream()
          .filter(f -> f.getCrpProgram().getProgramType() == ProgramType.REGIONAL_PROGRAM_TYPE.getValue())
          .collect(Collectors.toList());
        Set<String> regionSet = new HashSet<>();
        if (studyRegionsList != null && !studyRegionsList.isEmpty()) {
          for (ProjectExpectedStudyFlagship studyFlagship : studyRegionsList) {
            regionSet.add("<br>&nbsp;&nbsp;&nbsp;&nbsp; ● " + studyFlagship.getCrpProgram().getComposedName());
          }
          regionalPrograms = String.join("", regionSet);
        }
        // External Partners
        List<ProjectExpectedStudyInstitution> studyInstitutionList =
          projectExpectedStudyInfo.getProjectExpectedStudy().getProjectExpectedStudyInstitutions().stream()
            .filter(s -> s.isActive() && s.getPhase() != null && s.getPhase().equals(this.getSelectedPhase()))
            .collect(Collectors.toList());
        Set<String> institutionSet = new HashSet<>();
        if (studyInstitutionList != null && !studyInstitutionList.isEmpty()) {
          for (ProjectExpectedStudyInstitution studyinstitution : studyInstitutionList) {
            institutionSet.add("<br>&nbsp;&nbsp;&nbsp;&nbsp; ● " + studyinstitution.getInstitution().getComposedName());
          }
          institutions = String.join("", institutionSet);
        }
        // cgiarInnovations
        if (projectExpectedStudyInfo.getCgiarInnovation() != null) {
          cgiarInnovation = projectExpectedStudyInfo.getCgiarInnovation();
        }
        // Innovations
        List<ProjectExpectedStudyInnovation> studyInnovationList =
          projectExpectedStudyInfo.getProjectExpectedStudy().getProjectExpectedStudyInnovations().stream()
            .filter(s -> s.isActive() && s.getPhase() != null && s.getPhase().equals(this.getSelectedPhase()))
            .collect(Collectors.toList());
        Set<String> innovationSet = new HashSet<>();
        if (studyInnovationList != null && !studyInnovationList.isEmpty()) {
          for (ProjectExpectedStudyInnovation studyInnovation : studyInnovationList) {
            studyInnovation.getProjectInnovation().getProjectInnovationInfo(this.getSelectedPhase());
            String composedName = studyInnovation.getProjectInnovation().getComposedName();
            if (composedName != null && !composedName.isEmpty()) {
              innovationSet
                .add("<br>&nbsp;&nbsp;&nbsp;&nbsp; ● " + studyInnovation.getProjectInnovation().getComposedName());
            }
          }
          cgiarInnovations = String.join("", innovationSet);
        }
        // Elaboration of Outcome/Impact Statement
        if (projectExpectedStudyInfo.getElaborationOutcomeImpactStatement() != null
          && !projectExpectedStudyInfo.getElaborationOutcomeImpactStatement().trim().isEmpty()) {
          elaborationOutcomeImpactStatement =
            htmlParser.plainTextToHtml(projectExpectedStudyInfo.getElaborationOutcomeImpactStatement());
        }


        // New references
        // Expected Study Reference List
        try {
          if (projectExpectedStudyInfo.getProjectExpectedStudy().getProjectExpectedStudyReferences() != null) {
            projectExpectedStudyInfo.getProjectExpectedStudy()
              .setReferences(new ArrayList<>(
                projectExpectedStudyInfo.getProjectExpectedStudy().getProjectExpectedStudyReferences().stream()
                  .filter(
                    o -> o != null && o.getId() != null && o.getPhase().getId().equals(this.getSelectedPhase().getId()))
                  .sorted((o1, o2) -> Comparator.comparing(ProjectExpectedStudyReference::getId).compare(o1, o2))
                  .collect(Collectors.toList())));
          }

          if (projectExpectedStudyInfo.getProjectExpectedStudy().getReferences() != null) {
            int count = 1;
            for (ProjectExpectedStudyReference reference : projectExpectedStudyInfo.getProjectExpectedStudy()
              .getReferences()) {
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

                if (reference.getExternalAuthor() != null && reference.getExternalAuthor()) {
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
        } catch (Exception e) {
          LOG.error("Failed to get new reference information: " + e.getMessage());
        }

        // References cited
        try {
          if ((referenceText == null) || (referenceText != null && referenceText.isEmpty())) {
            if (projectExpectedStudyInfo.getReferencesText() != null
              && !projectExpectedStudyInfo.getReferencesText().trim().isEmpty()) {
              studiesReference = htmlParser.plainTextToHtml(projectExpectedStudyInfo.getReferencesText());
              referenceText = urlShortener.detectAndShortenLinks(studiesReference);
            }
          }
        } catch (Exception e) {
          LOG.error("Failed to get reference text: " + e.getMessage());
        }

        // MELIA publications
        if (projectExpectedStudyInfo.getMELIAPublications() != null) {
          if (!projectExpectedStudyInfo.getMELIAPublications().contains(" ")) {
            meliaPublications = urlShortener.detectAndShortenLinks(projectExpectedStudyInfo.getMELIAPublications());
          } else {
            try {
              int firstSpace = projectExpectedStudyInfo.getMELIAPublications().indexOf(" ");
              meliaPublications = urlShortener
                .detectAndShortenLinks(projectExpectedStudyInfo.getMELIAPublications().substring(0, firstSpace));
              meliaPublications += projectExpectedStudyInfo.getMELIAPublications().substring(firstSpace + 1);
            } catch (Exception e) {
              throw e;
            }
          }
        }

        // Cluster Acronum
        if (projectExpectedStudyInfo.getProjectExpectedStudy().getProject().getAcronym() != null) {
          clusterAcronym = projectExpectedStudyInfo.getProjectExpectedStudy().getProject().getAcronym();
        } else {
          clusterAcronym = "C" + projectExpectedStudyInfo.getProjectExpectedStudy().getProject().getId();
        }

        // TODO: Add Quantifications in Pentaho/MySQL

        // Gender, Youth, and Capacity Development
        // Gender
        if (projectExpectedStudyInfo.getGenderLevel() != null) {
          genderRelevance = projectExpectedStudyInfo.getGenderLevel().getPowbName();
          if (!projectExpectedStudyInfo.getGenderLevel().getId().equals(1l)
            && !projectExpectedStudyInfo.getGenderLevel().getId().equals(4l)
            && projectExpectedStudyInfo.getDescribeGender() != null
            && !projectExpectedStudyInfo.getDescribeGender().isEmpty()) {
            genderRelevance += "<br>" + this.getText("study.achievementsGenderRelevance.readText") + ": "
              + htmlParser.plainTextToHtml(projectExpectedStudyInfo.getDescribeGender());
          }
        }
        // Youth
        if (projectExpectedStudyInfo.getYouthLevel() != null) {
          youthRelevance = projectExpectedStudyInfo.getYouthLevel().getPowbName();
          if (!projectExpectedStudyInfo.getYouthLevel().getId().equals(1l)
            && !projectExpectedStudyInfo.getYouthLevel().getId().equals(4l)
            && projectExpectedStudyInfo.getDescribeYouth() != null
            && !projectExpectedStudyInfo.getDescribeYouth().isEmpty()) {
            youthRelevance += "<br>" + this.getText("study.achievementsYouthRelevance.readText") + ": "
              + htmlParser.plainTextToHtml(projectExpectedStudyInfo.getDescribeYouth());
          }
        }
        // Capacity Development
        if (projectExpectedStudyInfo.getCapdevLevel() != null) {
          capacityRelevance = projectExpectedStudyInfo.getCapdevLevel().getPowbName();
          if (!projectExpectedStudyInfo.getCapdevLevel().getId().equals(1l)
            && !projectExpectedStudyInfo.getCapdevLevel().getId().equals(4l)
            && projectExpectedStudyInfo.getDescribeCapdev() != null
            && !projectExpectedStudyInfo.getDescribeCapdev().isEmpty()) {
            capacityRelevance += "<br>" + this.getText("study.generalInformation.achievementsCapDevRelevance.readText")
              + ": " + htmlParser.plainTextToHtml(projectExpectedStudyInfo.getDescribeCapdev());
          }
        }

        // Climate change
        if (projectExpectedStudyInfo.getClimateChangeLevel() != null) {
          climateRelevance = projectExpectedStudyInfo.getClimateChangeLevel().getPowbName();
          if (!projectExpectedStudyInfo.getClimateChangeLevel().getId().equals(1l)
            && !projectExpectedStudyInfo.getClimateChangeLevel().getId().equals(4l)
            && projectExpectedStudyInfo.getDescribeClimateChange() != null
            && !projectExpectedStudyInfo.getDescribeClimateChange().isEmpty()) {
            climateRelevance +=
              "<br>" + this.getText("study.generalInformation.achievementsClimateChangeRelevance.readText") + ": "
                + htmlParser.plainTextToHtml(projectExpectedStudyInfo.getDescribeClimateChange());
          }
        }

        if (projectExpectedStudyInfo.getOtherCrossCuttingSelection() != null
          && !projectExpectedStudyInfo.getOtherCrossCuttingSelection().isEmpty()) {
          otherCrossCuttingDimensionsSelection = projectExpectedStudyInfo.getOtherCrossCuttingSelection();
        }
        // Other cross-cutting dimensions
        if (projectExpectedStudyInfo.getOtherCrossCuttingDimensions() != null
          && !projectExpectedStudyInfo.getOtherCrossCuttingDimensions().trim().isEmpty()) {
          otherCrossCuttingDimensions =
            htmlParser.plainTextToHtml(projectExpectedStudyInfo.getOtherCrossCuttingDimensions());
        }
        // Contact person
        if (projectExpectedStudyInfo.getContacts() != null
          && !projectExpectedStudyInfo.getContacts().trim().isEmpty()) {
          contacts = htmlParser.plainTextToHtml(projectExpectedStudyInfo.getContacts());
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
        if (projectExpectedStudyInfo.getProjectExpectedStudy() != null
          && projectExpectedStudyInfo.getProjectExpectedStudy().getProjectExpectedStudyCrpOutcomes() != null) {
          projectExpectedStudyInfo.getProjectExpectedStudy()
            .setCrpOutcomes(new ArrayList<>(projectExpectedStudyInfo.getProjectExpectedStudy()
              .getProjectExpectedStudyCrpOutcomes().stream()
              .filter(o -> o.getPhase().getId().equals(this.getSelectedPhase().getId())).collect(Collectors.toList())));
        }

        if (projectExpectedStudyInfo.getProjectExpectedStudy().getCrpOutcomes() != null) {
          for (ProjectExpectedStudyCrpOutcome outcome : projectExpectedStudyInfo.getProjectExpectedStudy()
            .getCrpOutcomes()) {
            if (outcome != null && outcome.getCrpOutcome() != null
              && outcome.getCrpOutcome().getDescription() != null) {

              if (performanceIndicator == null) {
                performanceIndicator = " ●" + outcome.getCrpOutcome().getDescription();
              } else {
                performanceIndicator += "<br>&nbsp;&nbsp;&nbsp;&nbsp; ●" + outcome.getCrpOutcome().getDescription();
              }
            }
          }

        }
        /*
         * Generate link url from parameters
         */
        if (projectExpectedStudyInfo.getIsPublic() != null && projectExpectedStudyInfo.getIsPublic()
          && projectExpectedStudyInfo.getPhase() != null && this.getBaseUrl() != null) {
          link = this.getBaseUrl() + "/projects/" + this.getCrpSession() + "/studySummary.do?studyID="
            + projectExpectedStudyInfo.getProjectExpectedStudy().getId() + "&cycle=Reporting&year="
            + this.getSelectedPhase().getYear();
        }
        // Projects
        List<ExpectedStudyProject> studyProjectList =
          projectExpectedStudyInfo.getProjectExpectedStudy().getExpectedStudyProjects().stream()
            .filter(e -> e.isActive() && e.getPhase() != null && e.getPhase().equals(this.getSelectedPhase()))
            .sorted((sp1, sp2) -> sp2.getProject().getId().compareTo(sp1.getProject().getId()))
            .collect(Collectors.toList());
        Set<String> studyProjectSet = new HashSet<>();
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

        ProjectExpectedStudy expectedStudy = null;
        expectedStudy = projectExpectedStudyInfo.getProjectExpectedStudy();
        try {
          if (expectedStudy.getProjectExpectedStudyAllianceLeversOutcomes() != null) {
            expectedStudy.setAllianceLeversOutcomes(new ArrayList<>(expectedStudy
              .getProjectExpectedStudyAllianceLeversOutcomes().stream().filter(o -> o != null && o.isActive()
                && o.getId() != null && o.getPhase().getId().equals(this.getSelectedPhase().getId()))
              .collect(Collectors.toList())));

            if (expectedStudy.getAllianceLeversOutcomes() != null
              && !expectedStudy.getAllianceLeversOutcomes().isEmpty()) {
              for (ProjectExpectedStudyAllianceLeversOutcome allianceLeverOutcome : expectedStudy
                .getAllianceLeversOutcomes()) {

                // Primary alliance lever
                if (allianceLeverOutcome.getAllianceLever() != null
                  && allianceLeverOutcome.getAllianceLever().getName() != null
                  && allianceLeverOutcome.getAllianceLever().getDescription() != null) {

                  if (primaryAllianceLever == null) {
                    primaryAllianceLever +=
                      "<br>&nbsp;&nbsp;&nbsp;&nbsp; ● " + allianceLeverOutcome.getAllianceLever().getName() + ": "
                        + allianceLeverOutcome.getAllianceLever().getDescription();
                  } else {
                    if (primaryAllianceLever != null
                      && !primaryAllianceLever.contains(allianceLeverOutcome.getAllianceLever().getName())) {
                      primaryAllianceLever +=
                        "<br>&nbsp;&nbsp;&nbsp;&nbsp; ● " + allianceLeverOutcome.getAllianceLever().getName() + ": "
                          + allianceLeverOutcome.getAllianceLever().getDescription();
                    }
                  }
                  primaryAllianceLever = primaryAllianceLever.replace("null", "");
                }

                // Strategic outcome
                if (allianceLeverOutcome.getAllianceLeverOutcome() != null
                  && allianceLeverOutcome.getAllianceLeverOutcome().getName() != null
                  && allianceLeverOutcome.getAllianceLeverOutcome().getDescription() != null) {
                  strategicOutcome +=
                    "<br>&nbsp;&nbsp;&nbsp;&nbsp; ● " + allianceLeverOutcome.getAllianceLeverOutcome().getName() + ": "
                      + allianceLeverOutcome.getAllianceLeverOutcome().getDescription();
                  strategicOutcome = strategicOutcome.replace("null", "");
                }
              }
            }
          }

          if (expectedStudy.getProjectExpectedStudySdgAllianceLevers() != null) {
            expectedStudy.setSdgAllianceLevers(null);
          }
        } catch (Exception e) {
          Log.error("error getting primary alliance lever");
        }

        // Primary SDG contribution
        try {
          if (expectedStudy.getProjectExpectedStudySdgAllianceLevers() != null) {
            expectedStudy.setSdgAllianceLevers(new ArrayList<>(expectedStudy
              .getProjectExpectedStudySdgAllianceLevers().stream().filter(o -> o != null && o.isActive()
                && o.getId() != null && o.getPhase().getId().equals(this.getSelectedPhase().getId()))
              .collect(Collectors.toList())));
            if (expectedStudy.getSdgAllianceLevers() != null && !expectedStudy.getSdgAllianceLevers().isEmpty()) {
              for (ProjectExpectedStudySdgAllianceLever sdgAllianceLever : expectedStudy.getSdgAllianceLevers()) {
                if (sdgAllianceLever != null && sdgAllianceLever.getsDGContribution() != null
                  && sdgAllianceLever.getsDGContribution() != null
                  && sdgAllianceLever.getsDGContribution().getName() != null) {
                  String allianceLeverTemp = "";
                  if (sdgAllianceLever.getAllianceLever() != null
                    && sdgAllianceLever.getAllianceLever().getName() != null
                    && sdgAllianceLever.getAllianceLever().getDescription() != null) {
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
                  }
                }
              }

            }
          }
        } catch (Exception e) {
          Log.error("error getting primary alliance lever");
        }

        // Related levers
        try {
          List<ProjectExpectedStudySdgAllianceLever> relatedLeversList = new ArrayList<>(expectedStudy
            .getProjectExpectedStudySdgAllianceLevers().stream().filter(nu -> nu.isActive()
              && nu.getPhase().getId().equals(this.getSelectedPhase().getId()) && !nu.getIsPrimary())
            .collect(Collectors.toList()));

          if (relatedLeversList != null && !relatedLeversList.isEmpty()) {
            for (final ProjectExpectedStudySdgAllianceLever sdgRelatedLever : relatedLeversList) {
              if (sdgRelatedLever.getsDGContribution() != null) {
                if (sdgRelatedLever.getsDGContribution().getName() != null
                  && sdgRelatedLever.getsDGContribution().getDescription() != null) {
                  relatedLever = sdgRelatedLever.getsDGContribution().getName() + ": "
                    + sdgRelatedLever.getsDGContribution().getDescription();
                }
              }
            }
          }
        } catch (Exception e) {
          Log.error("error getting Related levers");
        }

        /*
         * One CGIAR Tab
         */

        // Has CGIAR Contribution
        if (projectExpectedStudyInfo.getHasCgiarContribution() != null) {
          hasCGIARContribution = projectExpectedStudyInfo.getHasCgiarContribution().toString();
        }

        // Impact Area
        try {
          if (expectedStudy.getProjectExpectedStudyImpactAreas() != null) {
            expectedStudy.setImpactAreas(new ArrayList<>(expectedStudy
              .getProjectExpectedStudyImpactAreas().stream().filter(o -> (o != null) && (o.getId() != null)
                && o.isActive() && o.getPhase().getId().equals(this.getSelectedPhase().getId()))
              .collect(Collectors.toList())));
            if (expectedStudy.getImpactAreas() != null && !expectedStudy.getImpactAreas().isEmpty()) {
              ImpactArea impactAreaTmp = new ImpactArea();
              impactAreaTmp.setName(expectedStudy.getImpactAreas().get(0).getImpactArea().getName());
              impactAreaTmp.setDescription(expectedStudy.getImpactAreas().get(0).getImpactArea().getDescription());
              impactArea +=
                "<br>&nbsp;&nbsp;&nbsp;&nbsp; ● " + impactAreaTmp.getName() + ": " + impactAreaTmp.getDescription();
            }
          }

          // Expected Study global target
          String globalTargets = null;
          if (expectedStudy.getProjectExpectedStudyGlobalTargets() != null) {
            expectedStudy.setGlobalTargets(new ArrayList<>(expectedStudy
              .getProjectExpectedStudyGlobalTargets().stream().filter(o -> (o != null) && (o.getId() != null)
                && o.isActive() && o.getPhase().getId().equals(this.getSelectedPhase().getId()))
              .collect(Collectors.toList())));
            if (expectedStudy.getImpactAreas() != null && !expectedStudy.getImpactAreas().isEmpty()) {
              expectedStudy.getImpactArea().setGlobalTargets(new ArrayList<>());
              for (ProjectExpectedStudyGlobalTarget projectExpectedStudyGlobalTargetTmp : expectedStudy
                .getGlobalTargets()) {
                if (projectExpectedStudyGlobalTargetTmp.getGlobalTarget() != null
                  && projectExpectedStudyGlobalTargetTmp.getGlobalTarget().getName() != null
                  && projectExpectedStudyGlobalTargetTmp.getGlobalTarget().getDescription() != null) {
                  if (globalTargets == null) {
                    globalTargets = "<br>Global Targets:<br>&nbsp;&nbsp;&nbsp;&nbsp";
                  }
                  globalTargets +=
                    "<br>&nbsp;&nbsp;&nbsp;&nbsp; ● " + projectExpectedStudyGlobalTargetTmp.getGlobalTarget().getName()
                      + ": " + projectExpectedStudyGlobalTargetTmp.getGlobalTarget().getDescription();
                }
              }
            }
          }
          impactArea += globalTargets;
          impactArea = impactArea.replace("null", "");
        } catch (Exception e) {
          Log.error("error getting Impact Areas");
        }

        // Shared clusters
        if (studyProjectList != null && !studyProjectList.isEmpty()) {
          for (ExpectedStudyProject studyProject : studyProjectList) {
            if (studyProject.getProject().getAcronym() != null) {
              if (studyProjectSet != null && !studyProjectSet
                .contains("<br>&nbsp;&nbsp;&nbsp;&nbsp; ● " + studyProject.getProject().getAcronym())) {
                studyProjectSet.add("<br>&nbsp;&nbsp;&nbsp;&nbsp; ● " + studyProject.getProject().getAcronym());
              }
            } else {
              if (studyProjectSet != null
                && !studyProjectSet.contains("<br>&nbsp;&nbsp;&nbsp;&nbsp; ● C" + studyProject.getProject().getId())) {
                studyProjectSet.add("<br>&nbsp;&nbsp;&nbsp;&nbsp; ● C" + studyProject.getProject().getId());
              }
            }
          }
        }
        if (studyProjectSet != null && !studyProjectSet.isEmpty()) {
          studyProjects = String.join("", studyProjectSet);
        }

        model.addRow(new Object[] {id, year, title, commissioningStudy, status, type, outcomeImpactStatement,
          isContributionText, stageStudy, srfTargets, subIdos, topLevelComments, geographicScopes, regions, countries,
          scopeComments, crps, flagships, regionalPrograms, institutions, elaborationOutcomeImpactStatement,
          referenceText, quantification, genderRelevance, youthRelevance, capacityRelevance,
          otherCrossCuttingDimensions, comunicationsMaterial, contacts, studyProjects, tagged, cgiarInnovation,
          cgiarInnovations, climateRelevance, link, links, studyPolicies, isSrfTargetText,
          otherCrossCuttingDimensionsSelection, isContribution, isRegional, isNational, isOutcomeCaseStudy, isSrfTarget,
          url, studiesReference, meliaPublications, performanceIndicator, covidAnalysis, centers, clusterAcronym,
          allianceOICRID, primaryAllianceLever, strategicOutcome, primarySDGcontribution, relatedLever,
          relatedSDGContribution, hasCGIARContribution, impactArea});

      }
    }

    return model;
  }

}
