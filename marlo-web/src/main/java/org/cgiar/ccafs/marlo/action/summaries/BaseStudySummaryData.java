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
import org.cgiar.ccafs.marlo.data.model.ProgramType;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyActionAreaOutcomeIndicator;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyCountry;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyCrp;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyFlagship;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyFundingSource;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyGeographicScope;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyImpactAreaIndicator;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyInfo;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyInitiative;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyInnovation;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyInstitution;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyLeverOutcome;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyLink;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyNexus;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyPolicy;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyReference;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyRegion;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudySdgTarget;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudySrfTarget;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudySubIdo;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.HTMLParser;
import org.cgiar.ccafs.marlo.utils.URLShortener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.util.TypedTableModel;

public class BaseStudySummaryData extends BaseSummariesAction {

  private static final long serialVersionUID = -3643067302492726266L;
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
  public MasterReport addi8nParameters(MasterReport masterReport, boolean isAlliance) {
    masterReport.getParameterValues().put("i8nStudies", this.getText("menu.studies"));
    masterReport.getParameterValues().put("i8nStudiesRNoData", this.getText("summaries.study.noData"));
    masterReport.getParameterValues().put("i8nStudiesRCaseStudy", this.getText("summaries.study"));
    masterReport.getParameterValues().put("i8nCaseStudiesRStudyProjects",
      this.getText("summaries.study.studyContributingProjects"));
    masterReport.getParameterValues().put("i8nCaseStudiesRPartI", this.getText("summaries.study.partI"));
    masterReport.getParameterValues().put("i8nStudiesRType", this.getText("study.type"));
    masterReport.getParameterValues().put("i8nStudiesRStatus", this.getText("study.status"));
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
    masterReport.getParameterValues().put("i8nCaseStudiesRTargetOption", this.getText("study.targetsOption"));
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
    masterReport.getParameterValues().put("i8nStudiesCGIARInnovation",
      this.getText("study.innovationsNarrative.readText"));
    masterReport.getParameterValues().put("i8nStudiesCGIARInnovations", this.getText("study.innovationsList"));
    masterReport.getParameterValues().put("i8nStudiesRElaborationOutcomeImpactStatement",
      this.getText("summaries.study.elaborationStatement"));
    masterReport.getParameterValues().put("i8nStudiesRReferenceText", this.getText("summaries.study.referencesCited"));
    masterReport.getParameterValues().put("i8nStudiesRGenderDevelopment",
      this.getText("summaries.study.crossCuttingRelevance"));
    masterReport.getParameterValues().put("i8nStudiesRGenderRelevance", this.getText("study.genderRelevance"));
    masterReport.getParameterValues().put("i8nStudiesRYouthRelevance", this.getText("study.youthRelevance"));
    masterReport.getParameterValues().put("i8nStudiesRCapacityRelevance", this.getText("study.capDevRelevance"));
    masterReport.getParameterValues().put("i8nStudiesRClimateRelevance", this.getText("study.climateRelevance"));
    masterReport.getParameterValues().put("i8nStudiesROtherCrossCuttingDimensions",
      this.getText("study.otherCrossCutting.readText"));
    masterReport.getParameterValues().put("i8nStudiesROtherCrossCuttingDimensionsComments",
      this.getText("study.otherCrossCutting.comments.readText"));
    masterReport.getParameterValues().put("i8nStudiesRContacts", this.getText("summaries.study.contacts"));
    masterReport.getParameterValues().put("i8nStudiesRCommissioningStudy",
      this.getText("study.commissioningStudy.readText"));
    masterReport.getParameterValues().put("i8nStudiesRStudyLink", this.getText("summaries.study.link"));
    masterReport.getParameterValues().put("i8nStudiesRQuantification", this.getText("study.quantification.readText"));
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

    if (isAlliance) {
      masterReport.getParameterValues().put("i8nCaseStudiesRPartIII", this.getText("summaries.study.partIII"));
      masterReport.getParameterValues().put("i8nStudiesRInternalStatus", this.getText("study.internalStatus"));
      masterReport.getParameterValues().put("i8nStudiesRHasLeverOutcomesText",
        this.getText("study.leverOutcomes.question"));
      masterReport.getParameterValues().put("i8nCaseStudiesRLeverOutcomes",
        this.getText("summaries.study.leverOutcomes"));
      masterReport.getParameterValues().put("i8nStudiesRHasNexusText", this.getText("study.nexus.question"));
      masterReport.getParameterValues().put("i8nCaseStudiesRNexus", this.getText("summaries.study.nexus"));
      masterReport.getParameterValues().put("i8nCaseStudiesRFundingSources",
        this.getText("summaries.study.fundingSources"));
      masterReport.getParameterValues().put("i8nStudiesRHasLegacyCrpsText", this.getText("study.legacyCrp.question"));
      masterReport.getParameterValues().put("i8nCaseStudiesRLegacyCrps", this.getText("summaries.study.legacyCrps"));
      masterReport.getParameterValues().put("i8nCaseStudiesRSdgTargets", this.getText("summaries.study.sdgTargets"));
      masterReport.getParameterValues().put("i8nStudiesRHasActionAreaOutcomeIndicatorsText",
        this.getText("study.actionAreaOutcomeIndicators.question"));
      masterReport.getParameterValues().put("i8nCaseStudiesRActionAreaOutcomeIndicators",
        this.getText("summaries.study.actionAreaOutcomeIndicators"));
      masterReport.getParameterValues().put("i8nStudiesRHasImpactAreaIndicatorsText",
        this.getText("study.impactAreaIndicators.question"));
      masterReport.getParameterValues().put("i8nCaseStudiesRImpactAreaIndicators",
        this.getText("summaries.study.impactAreaIndicators"));
      masterReport.getParameterValues().put("i8nStudiesRHasInitiativesText",
        this.getText("study.initiatives.question"));
      masterReport.getParameterValues().put("i8nCaseStudiesRInitiatives", this.getText("summaries.study.initiatives"));
    }

    return masterReport;
  }

  public TypedTableModel getCaseStudiesTableModel(List<ProjectExpectedStudyInfo> projectExpectedStudyInfos,
    boolean isAlliance) {

    String[] columnNames = new String[] {"id", "year", "title", "commissioningStudy", "status", "type",
      "outcomeImpactStatement", "isContributionText", "stageStudy", "srfTargets", "subIdos", "topLevelComments",
      "geographicScopes", "regions", "countries", "scopeComments", "crps", "flagships", "regionalPrograms",
      "institutions", "elaborationOutcomeImpactStatement", "referenceText", "quantification", "genderRelevance",
      "youthRelevance", "capacityRelevance", "otherCrossCuttingDimensions", "comunicationsMaterial", "contacts",
      "studyProjects", "tagged", "cgiarInnovation", "cgiarInnovations", "climateRelevance", "link", "links",
      "studyPolicies", "isSrfTargetText", "otherCrossCuttingDimensionsSelection", "isContribution", "isRegional",
      "isNational", "isOutcomeCaseStudy", "isSrfTarget", "url", "studiesReference"};

    Class[] columnClasses =
      new Class[] {Long.class, Integer.class, String.class, String.class, String.class, String.class, String.class,
        String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class,
        String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class,
        String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class,
        String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class,
        Boolean.class, Boolean.class, Boolean.class, Boolean.class, Boolean.class, String.class, String.class};

    TypedTableModel model = new TypedTableModel(columnNames, columnClasses, /* inititalRowNumber */0);

    if (isAlliance) {
      model.addColumn("hasLeverOutcomes", Boolean.class);
      model.addColumn("hasLeverOutcomesText", String.class);
      model.addColumn("leverOutcomes", String.class);

      model.addColumn("hasNexus", Boolean.class);
      model.addColumn("hasNexusText", String.class);
      model.addColumn("nexus", String.class);

      model.addColumn("fundingSources", String.class);

      model.addColumn("hasLegacyCrps", Boolean.class);
      model.addColumn("hasLegacyCrpsText", String.class);

      model.addColumn("sdgTargets", String.class);

      model.addColumn("hasActionAreaOutcomeIndicators", Boolean.class);
      model.addColumn("hasActionAreaOutcomeIndicatorsText", String.class);
      model.addColumn("actionAreaOutcomeIndicators", String.class);

      model.addColumn("hasImpactAreaIndicators", Boolean.class);
      model.addColumn("hasImpactAreaIndicatorsText", String.class);
      model.addColumn("impactAreaIndicators", String.class);

      model.addColumn("hasInitiatives", Boolean.class);
      model.addColumn("hasInitiativesText", String.class);
      model.addColumn("initiatives", String.class);

      model.addColumn("internalStatus", String.class);
    }

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
          quantification = null, genderRelevance = null, youthRelevance = null, capacityRelevance = null,
          otherCrossCuttingDimensions = null, comunicationsMaterial = null, contacts = null, studyProjects = null,
          tagged = null, cgiarInnovation = null, cgiarInnovations = null, climateRelevance = null, link = null,
          links = null, studyPolicies = null, isSrfTargetText = null, otherCrossCuttingDimensionsSelection = null,
          url = null, studiesReference = null;

        Boolean isContribution = false, isRegional = false, isNational = false, isOutcomeCaseStudy = false,
          isSrfTarget = false;
        StringBuffer referenceText = new StringBuffer();

        id = projectExpectedStudyInfo.getProjectExpectedStudy().getId();
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
          if (this.isSelectedPhaseAR2021() && StringUtils.isBlank(comunicationsMaterial)) {
            comunicationsMaterial = this.getText("global.AR2021.notRequired");
          }
        }
        // Links

        List<ProjectExpectedStudyLink> linksList =
          projectExpectedStudyInfo.getProjectExpectedStudy().getProjectExpectedStudyLinks().stream()
            .filter(s -> s.isActive() && s.getPhase() != null && s.getPhase().equals(this.getSelectedPhase()))
            .collect(Collectors.toList());
        Set<String> linkSet = new HashSet<>();
        if (linksList != null && linksList.size() > 0) {
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
        if (projectExpectedStudyInfo.getIsContribution() != null) {
          isContribution = projectExpectedStudyInfo.getIsContribution();
          isContributionText = projectExpectedStudyInfo.getIsContribution() ? "Yes" : "No";
          if (isContribution) {
            // Policies Contribution
            List<ProjectExpectedStudyPolicy> studyPoliciesList =
              projectExpectedStudyInfo.getProjectExpectedStudy().getProjectExpectedStudyPolicies().stream()
                .filter(s -> s.isActive() && s.getPhase() != null && s.getPhase().equals(this.getSelectedPhase()))
                .collect(Collectors.toList());
            Set<String> studyPoliciesSet = new HashSet<>();
            if (studyPoliciesList != null && studyPoliciesList.size() > 0) {
              for (ProjectExpectedStudyPolicy projectExpectedStudyPolicy : studyPoliciesList) {
                if (projectExpectedStudyPolicy.getProjectPolicy()
                  .getProjectPolicyInfo(this.getSelectedPhase()) != null) {
                  studyPoliciesSet.add(
                    "<br>&nbsp;&nbsp;&nbsp;&nbsp;● " + projectExpectedStudyPolicy.getProjectPolicy().getComposedName());
                }
              }
              studyPolicies = String.join("", studyPoliciesSet);
            }
          }
        }
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
        if (subIdosList != null && subIdosList.size() > 0) {
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
            if (studySrfTargets != null && studySrfTargets.size() > 0) {
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
        if (geographicScopeList != null && geographicScopeList.size() > 0) {
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
          if (studyCountries != null && studyCountries.size() > 0) {
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
          if (studyRegions != null && studyRegions.size() > 0) {
            Set<String> regionsSet = new HashSet<>();
            for (ProjectExpectedStudyRegion projectExpectedStudyRegion : studyRegions) {
              regionsSet.add("<br>&nbsp;&nbsp;&nbsp;&nbsp; ● " + projectExpectedStudyRegion.getLocElement().getName());
            }
            regions = String.join("", regionsSet);
          }
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
        if (studyCrpsList != null && studyCrpsList.size() > 0) {
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
        if (studyFlagshipList != null && studyFlagshipList.size() > 0) {
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
        if (studyRegionsList != null && studyRegionsList.size() > 0) {
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
        if (studyInstitutionList != null && studyInstitutionList.size() > 0) {
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
        if (studyInnovationList != null && studyInnovationList.size() > 0) {
          for (ProjectExpectedStudyInnovation studyInnovation : studyInnovationList) {
            studyInnovation.getProjectInnovation().getProjectInnovationInfo(this.getSelectedPhase());
            innovationSet
              .add("<br>&nbsp;&nbsp;&nbsp;&nbsp; ● " + studyInnovation.getProjectInnovation().getComposedName());
          }
          cgiarInnovations = String.join("", innovationSet);
        }
        // Elaboration of Outcome/Impact Statement
        if (projectExpectedStudyInfo.getElaborationOutcomeImpactStatement() != null
          && !projectExpectedStudyInfo.getElaborationOutcomeImpactStatement().trim().isEmpty()) {
          elaborationOutcomeImpactStatement =
            htmlParser.plainTextToHtml(projectExpectedStudyInfo.getElaborationOutcomeImpactStatement());
        }

        // References cited
        if (this.isNotEmpty(projectExpectedStudyInfo.getProjectExpectedStudy().getProjectExpectedStudyReferences())) {
          List<ProjectExpectedStudyReference> currentPhaseReferences =
            projectExpectedStudyInfo.getProjectExpectedStudy().getProjectExpectedStudyReferences().stream()
              .filter(l -> l != null && l.getId() != null && l.getPhase() != null && l.getPhase().getId() != null
                && l.getPhase().equals(this.getSelectedPhase()))
              .collect(Collectors.toList());
          for (int i = 0; i < currentPhaseReferences.size(); i++) {
            ProjectExpectedStudyReference studyReferenceObject = currentPhaseReferences.get(i);

            if (!StringUtils.isAllBlank(studyReferenceObject.getReference(), studyReferenceObject.getLink())) {
              referenceText = referenceText.append("<p>•[").append(i + 1).append("] ")
                .append(StringUtils.trimToEmpty(studyReferenceObject.getReference())).append(" (")
                .append(urlShortener.getShortUrlService(StringUtils.trimToEmpty(studyReferenceObject.getLink())))
                .append(")</p>");
            }
          }
        } else if (StringUtils.isNotEmpty(
          projectExpectedStudyInfo.getProjectExpectedStudy().getProjectExpectedStudyInfo().getReferencesText())) {
          String referencePlain =
            projectExpectedStudyInfo.getProjectExpectedStudy().getProjectExpectedStudyInfo().getReferencesText();
          referencePlain = RegExUtils.replaceAll(referencePlain, "(\r|\n)+", "</p><p>");
          referenceText = referenceText.append("<p>").append(referencePlain).append("</p>");
        } else {
          referenceText = referenceText.append(notProvided);
        }
        /*
         * if (("AR".equals(this.getSelectedPhase().getName()) && 2021 == this.getSelectedPhase().getYear())
         * || this.getSelectedPhase().getYear() > 2021) {
         * if (this.isNotEmpty(projectExpectedStudyInfo.getProjectExpectedStudy().getProjectExpectedStudyReferences()))
         * {
         * List<ProjectExpectedStudyReference> currentPhaseReferences =
         * projectExpectedStudyInfo.getProjectExpectedStudy().getProjectExpectedStudyReferences().stream()
         * .filter(l -> l != null && l.getId() != null && l.getPhase() != null && l.getPhase().getId() != null
         * && l.getPhase().equals(this.getSelectedPhase()))
         * .collect(Collectors.toList());
         * for (ProjectExpectedStudyReference studyReferenceObject : currentPhaseReferences) {
         * if (StringUtils.isNotEmpty(studyReferenceObject.getReference())) {
         * String reference = StringUtils.trimToEmpty(studyReferenceObject.getReference());
         * referenceText = referenceText.append("• ").append(reference).append("</br>");
         * }
         * }
         * } else {
         * referenceText = referenceText.append(notProvided);
         * }
         * } else {
         * if (StringUtils.isNotEmpty(
         * projectExpectedStudyInfo.getProjectExpectedStudy().getProjectExpectedStudyInfo().getReferencesText())) {
         * referenceText = referenceText.append(
         * projectExpectedStudyInfo.getProjectExpectedStudy().getProjectExpectedStudyInfo().getReferencesText());
         * } else {
         * referenceText = referenceText.append(notProvided);
         * }
         * }
         */

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
            capacityRelevance += "<br>" + this.getText("study.achievementsCapDevRelevance.readText") + ": "
              + htmlParser.plainTextToHtml(projectExpectedStudyInfo.getDescribeCapdev());
          }
        }

        // Climate change
        if (projectExpectedStudyInfo.getClimateChangeLevel() != null) {
          climateRelevance = projectExpectedStudyInfo.getClimateChangeLevel().getPowbName();
          if (!projectExpectedStudyInfo.getClimateChangeLevel().getId().equals(1l)
            && !projectExpectedStudyInfo.getClimateChangeLevel().getId().equals(4l)
            && projectExpectedStudyInfo.getDescribeClimateChange() != null
            && !projectExpectedStudyInfo.getDescribeClimateChange().isEmpty()) {
            climateRelevance += "<br>" + this.getText("study.achievementsClimateChangeRelevance.readText") + ": "
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
          if (this.isSelectedPhaseAR2021()) {
            otherCrossCuttingDimensions = htmlParser.plainTextToHtml(this.getText("global.AR2021.notRequired"));
          }
        }
        // Contact person
        if (projectExpectedStudyInfo.getContacts() != null
          && !projectExpectedStudyInfo.getContacts().trim().isEmpty()) {
          contacts = htmlParser.plainTextToHtml(projectExpectedStudyInfo.getContacts());
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
        if (projectExpectedStudyInfo.getProjectExpectedStudy().getProject() != null) {
          studyProjectSet.add("<br>&nbsp;&nbsp;&nbsp;&nbsp; ● P"
            + projectExpectedStudyInfo.getProjectExpectedStudy().getProject().getId() + " - " + projectExpectedStudyInfo
              .getProjectExpectedStudy().getProject().getProjecInfoPhase(this.getSelectedPhase()).getTitle());
        }
        if (studyProjectList != null && !studyProjectList.isEmpty()) {
          for (ExpectedStudyProject studyProject : studyProjectList) {
            if (studyProject.getProject() != null
              && studyProject.getProject().getProjecInfoPhase(this.getSelectedPhase()) != null
              && studyProject.getProject().getProjecInfoPhase(this.getSelectedPhase()).getTitle() != null) {
              studyProjectSet.add("<br>&nbsp;&nbsp;&nbsp;&nbsp; ● P" + studyProject.getProject().getId() + " - "
                + studyProject.getProject().getProjecInfoPhase(this.getSelectedPhase()).getTitle());
            }
          }
        }
        if (studyProjectSet != null && !studyProjectSet.isEmpty()) {
          studyProjects = String.join("", studyProjectSet);
        }

        List<Object> rowObjects = new ArrayList<>(Arrays.asList(
          new Object[] {id, year, title, commissioningStudy, status, type, outcomeImpactStatement, isContributionText,
            stageStudy, srfTargets, subIdos, topLevelComments, geographicScopes, regions, countries, scopeComments,
            crps, flagships, regionalPrograms, institutions, elaborationOutcomeImpactStatement, referenceText,
            quantification, genderRelevance, youthRelevance, capacityRelevance, otherCrossCuttingDimensions,
            comunicationsMaterial, contacts, studyProjects, tagged, cgiarInnovation, cgiarInnovations, climateRelevance,
            link, links, studyPolicies, isSrfTargetText, otherCrossCuttingDimensionsSelection, isContribution,
            isRegional, isNational, isOutcomeCaseStudy, isSrfTarget, url, studiesReference}));

        if (isAlliance) {
          Boolean hasLeverOutcomes, hasNexus, hasLegacyCrps, hasActionAreaOutcomeIndicators, hasImpactAreaIndicators,
            hasInitiatives;
          String hasLeverOutcomesText = "", leverOutcomes = "", hasNexusText = "", nexus = "", fundingSources = "",
            hasLegacyCrpsText = "", sdgTargets = "", hasActionAreaOutcomeIndicatorsText = "",
            actionAreaOutcomeIndicators = "", hasImpactAreaIndicatorsText = "", impactAreaIndicators = "",
            hasInitiativesText = "", initiatives = "", internalStatus = "";

          // Lever Outcomes
          hasLeverOutcomes = BooleanUtils.isTrue(projectExpectedStudyInfo.getHasLeverOutcomeContribution());
          hasLeverOutcomesText =
            this.getYesNoStringOrNotDefined(projectExpectedStudyInfo.getHasLeverOutcomeContribution(), true);
          if (hasLeverOutcomes) {
            // Lever Outcomes Contribution
            List<ProjectExpectedStudyLeverOutcome> studyLeverOutcomes =
              projectExpectedStudyInfo.getProjectExpectedStudy().getProjectExpectedStudyLeverOutcomes().stream()
                .filter(s -> s.isActive() && s.getPhase() != null && s.getPhase().equals(this.getSelectedPhase()))
                .collect(Collectors.toList());

            Set<String> studyLeverOutcomesSet = new HashSet<>();
            if (this.isNotEmpty(studyLeverOutcomes)) {
              for (ProjectExpectedStudyLeverOutcome projectExpectedStudyLeverOutcome : studyLeverOutcomes) {
                if (projectExpectedStudyLeverOutcome.getLeverOutcome() != null
                  && projectExpectedStudyLeverOutcome.getLeverOutcome().getId() != null) {
                  studyLeverOutcomesSet.add("<br>&nbsp;&nbsp;&nbsp;&nbsp;● "
                    + projectExpectedStudyLeverOutcome.getLeverOutcome().getComposedName());
                }
              }

              leverOutcomes = String.join("", studyLeverOutcomesSet);
            } else {
              leverOutcomes = this.notDefinedHtml;
            }
          }

          // Nexus
          hasNexus = BooleanUtils.isTrue(projectExpectedStudyInfo.getHasNexusContribution());
          hasNexusText = this.getYesNoStringOrNotDefined(projectExpectedStudyInfo.getHasNexusContribution(), true);
          if (hasNexus) {
            // Nexus Contribution
            List<ProjectExpectedStudyNexus> studyNexus =
              projectExpectedStudyInfo.getProjectExpectedStudy().getProjectExpectedStudyNexus().stream()
                .filter(s -> s.isActive() && s.getPhase() != null && s.getPhase().equals(this.getSelectedPhase()))
                .collect(Collectors.toList());

            Set<String> studyNexusSet = new HashSet<>();
            if (this.isNotEmpty(studyNexus)) {
              for (ProjectExpectedStudyNexus projectExpectedStudyNexus : studyNexus) {
                if (projectExpectedStudyNexus.getNexus() != null
                  && projectExpectedStudyNexus.getNexus().getId() != null) {
                  studyNexusSet
                    .add("<br>&nbsp;&nbsp;&nbsp;&nbsp;● " + projectExpectedStudyNexus.getNexus().getComposedName());
                }
              }

              nexus = String.join("", studyNexusSet);
            } else {
              nexus = this.notDefinedHtml;
            }
          }

          // Funding Source Contribution
          List<ProjectExpectedStudyFundingSource> studyFundingSources =
            projectExpectedStudyInfo.getProjectExpectedStudy().getProjectExpectedStudyFundingSources().stream()
              .filter(s -> s.isActive() && s.getPhase() != null && s.getPhase().equals(this.getSelectedPhase()))
              .collect(Collectors.toList());

          Set<String> studyFundingSourcesSet = new HashSet<>();
          if (this.isNotEmpty(studyFundingSources)) {
            for (ProjectExpectedStudyFundingSource projectExpectedStudyFundingSource : studyFundingSources) {
              if (projectExpectedStudyFundingSource.getFundingSource() != null
                && projectExpectedStudyFundingSource.getFundingSource().getId() != null
                && projectExpectedStudyFundingSource.getFundingSource()
                  .getFundingSourceInfo(this.getSelectedPhase()) != null) {
                studyFundingSourcesSet.add("<br>&nbsp;&nbsp;&nbsp;&nbsp;● "
                  + projectExpectedStudyFundingSource.getFundingSource().getComposedName());
              }
            }

            fundingSources = String.join("", studyFundingSourcesSet);
          } else {
            fundingSources = this.notDefinedHtml;
          }

          // Legacy CRPs/PTFs
          hasLegacyCrps = BooleanUtils.isTrue(projectExpectedStudyInfo.getHasLegacyCrpContribution());
          hasLegacyCrpsText =
            this.getYesNoStringOrNotDefined(projectExpectedStudyInfo.getHasLegacyCrpContribution(), true);

          // SDG Target Contribution
          List<ProjectExpectedStudySdgTarget> studySdgTargets =
            projectExpectedStudyInfo.getProjectExpectedStudy().getProjectExpectedStudySdgTargets().stream()
              .filter(s -> s.isActive() && s.getPhase() != null && s.getPhase().equals(this.getSelectedPhase()))
              .collect(Collectors.toList());

          Set<String> studySdgTargetsSet = new HashSet<>();
          if (this.isNotEmpty(studySdgTargets)) {
            for (ProjectExpectedStudySdgTarget projectExpectedStudySdgTarget : studySdgTargets) {
              if (projectExpectedStudySdgTarget.getSdgTarget() != null
                && projectExpectedStudySdgTarget.getSdgTarget().getId() != null) {
                studySdgTargetsSet.add(
                  "<br>&nbsp;&nbsp;&nbsp;&nbsp;● " + projectExpectedStudySdgTarget.getSdgTarget().getComposedName());
              }
            }

            sdgTargets = String.join("", studySdgTargetsSet);
          } else {
            sdgTargets = this.notDefinedHtml;
          }

          // Action Area Outcome Indicators
          hasActionAreaOutcomeIndicators =
            BooleanUtils.isTrue(projectExpectedStudyInfo.getHasActionAreaOutcomeIndicatorContribution());
          hasActionAreaOutcomeIndicatorsText = this
            .getYesNoStringOrNotDefined(projectExpectedStudyInfo.getHasActionAreaOutcomeIndicatorContribution(), true);
          if (hasActionAreaOutcomeIndicators) {
            // Action Area Outcome Indicators Contribution
            List<ProjectExpectedStudyActionAreaOutcomeIndicator> studyActionAreaOutcomeIndicators =
              projectExpectedStudyInfo.getProjectExpectedStudy().getProjectExpectedStudyActionAreaOutcomeIndicators()
                .stream()
                .filter(s -> s.isActive() && s.getPhase() != null && s.getPhase().equals(this.getSelectedPhase()))
                .collect(Collectors.toList());

            Set<String> studyActionAreaOutcomeIndicatorsSet = new HashSet<>();
            if (this.isNotEmpty(studyActionAreaOutcomeIndicators)) {
              for (ProjectExpectedStudyActionAreaOutcomeIndicator projectExpectedStudyActionAreaOutcomeIndicator : studyActionAreaOutcomeIndicators) {
                if (projectExpectedStudyActionAreaOutcomeIndicator.getOutcomeIndicator() != null
                  && projectExpectedStudyActionAreaOutcomeIndicator.getOutcomeIndicator().getId() != null) {
                  studyActionAreaOutcomeIndicatorsSet.add("<br>&nbsp;&nbsp;&nbsp;&nbsp;● "
                    + projectExpectedStudyActionAreaOutcomeIndicator.getOutcomeIndicator().getComposedName());
                }
              }

              actionAreaOutcomeIndicators = String.join("", studyActionAreaOutcomeIndicatorsSet);
            } else {
              actionAreaOutcomeIndicators = this.notDefinedHtml;
            }
          }

          // Impact Area Indicator
          hasImpactAreaIndicators =
            BooleanUtils.isTrue(projectExpectedStudyInfo.getHasImpactAreaIndicatorContribution());
          hasImpactAreaIndicatorsText =
            this.getYesNoStringOrNotDefined(projectExpectedStudyInfo.getHasImpactAreaIndicatorContribution(), true);
          if (hasImpactAreaIndicators) {
            // Impact Area Indicator Contribution
            List<ProjectExpectedStudyImpactAreaIndicator> studyImpactAreaIndicators =
              projectExpectedStudyInfo.getProjectExpectedStudy().getProjectExpectedStudyImpactAreaIndicators().stream()
                .filter(s -> s.isActive() && s.getPhase() != null && s.getPhase().equals(this.getSelectedPhase()))
                .collect(Collectors.toList());

            Set<String> studyImpactAreaIndicatorsSet = new HashSet<>();
            if (this.isNotEmpty(studyImpactAreaIndicators)) {
              for (ProjectExpectedStudyImpactAreaIndicator projectExpectedStudyImpactAreaIndicator : studyImpactAreaIndicators) {
                if (projectExpectedStudyImpactAreaIndicator.getImpactAreaIndicator() != null
                  && projectExpectedStudyImpactAreaIndicator.getImpactAreaIndicator().getId() != null) {
                  studyImpactAreaIndicatorsSet.add("<br>&nbsp;&nbsp;&nbsp;&nbsp;● "
                    + projectExpectedStudyImpactAreaIndicator.getImpactAreaIndicator().getComposedName());
                }
              }

              impactAreaIndicators = String.join("", studyImpactAreaIndicatorsSet);
            } else {
              impactAreaIndicators = this.notDefinedHtml;
            }
          }

          // Initiative
          hasInitiatives = BooleanUtils.isTrue(projectExpectedStudyInfo.getHasInitiativeContribution());
          hasInitiativesText =
            this.getYesNoStringOrNotDefined(projectExpectedStudyInfo.getHasInitiativeContribution(), true);
          if (hasInitiatives) {
            // Initiative Contribution
            List<ProjectExpectedStudyInitiative> studyInitiatives =
              projectExpectedStudyInfo.getProjectExpectedStudy().getProjectExpectedStudyInitiatives().stream()
                .filter(s -> s.isActive() && s.getPhase() != null && s.getPhase().equals(this.getSelectedPhase()))
                .collect(Collectors.toList());

            Set<String> studyInitiativesSet = new HashSet<>();
            if (this.isNotEmpty(studyInitiatives)) {
              for (ProjectExpectedStudyInitiative projectExpectedStudyInitiative : studyInitiatives) {
                if (projectExpectedStudyInitiative.getInitiative() != null
                  && projectExpectedStudyInitiative.getInitiative().getId() != null) {
                  studyInitiativesSet.add("<br>&nbsp;&nbsp;&nbsp;&nbsp;● "
                    + projectExpectedStudyInitiative.getInitiative().getComposedName());
                }
              }

              initiatives = String.join("", studyInitiativesSet);
            } else {
              initiatives = this.notDefinedHtml;
            }
          }

          // Internal status
          if (StringUtils.isNotBlank(projectExpectedStudyInfo.getInternalStatus())) {
            internalStatus = StringUtils.trimToEmpty(projectExpectedStudyInfo.getInternalStatus());
          } else {
            internalStatus = notDefinedHtml;
          }

          rowObjects.add(hasLeverOutcomes);
          rowObjects.add(hasLeverOutcomesText);
          rowObjects.add(leverOutcomes);

          rowObjects.add(hasNexus);
          rowObjects.add(hasNexusText);
          rowObjects.add(nexus);

          rowObjects.add(fundingSources);

          rowObjects.add(hasLegacyCrps);
          rowObjects.add(hasLegacyCrpsText);

          rowObjects.add(sdgTargets);

          rowObjects.add(hasActionAreaOutcomeIndicators);
          rowObjects.add(hasActionAreaOutcomeIndicatorsText);
          rowObjects.add(actionAreaOutcomeIndicators);

          rowObjects.add(hasImpactAreaIndicators);
          rowObjects.add(hasImpactAreaIndicatorsText);
          rowObjects.add(impactAreaIndicators);

          rowObjects.add(hasInitiatives);
          rowObjects.add(hasInitiativesText);
          rowObjects.add(initiatives);

          rowObjects.add(internalStatus);
        }

        Object[] rowObjectsArray = rowObjects.toArray(new Object[0]);
        model.addRow(rowObjectsArray);

      }
    }

    return model;
  }

}
