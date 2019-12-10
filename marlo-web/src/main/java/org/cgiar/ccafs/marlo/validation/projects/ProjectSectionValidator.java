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

package org.cgiar.ccafs.marlo.validation.projects;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.DeliverableGeographicRegionManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableInfoManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableIntellectualAssetManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableLocationManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableParticipantManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableQualityCheckManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableTypeManager;
import org.cgiar.ccafs.marlo.data.manager.FundingSourceManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitProjectManager;
import org.cgiar.ccafs.marlo.data.manager.LocElementTypeManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyCountryManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyRegionManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationCountryManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationRegionManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectLocationElementTypeManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPolicyCountryManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPolicyManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPolicyRegionManager;
import org.cgiar.ccafs.marlo.data.model.Activity;
import org.cgiar.ccafs.marlo.data.model.CaseStudyIndicator;
import org.cgiar.ccafs.marlo.data.model.CaseStudyProject;
import org.cgiar.ccafs.marlo.data.model.CountryFundingSources;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.data.model.DeliverableActivity;
import org.cgiar.ccafs.marlo.data.model.DeliverableCrossCuttingMarker;
import org.cgiar.ccafs.marlo.data.model.DeliverableDataSharingFile;
import org.cgiar.ccafs.marlo.data.model.DeliverableDissemination;
import org.cgiar.ccafs.marlo.data.model.DeliverableFile;
import org.cgiar.ccafs.marlo.data.model.DeliverableFundingSource;
import org.cgiar.ccafs.marlo.data.model.DeliverableInfo;
import org.cgiar.ccafs.marlo.data.model.DeliverableIntellectualAsset;
import org.cgiar.ccafs.marlo.data.model.DeliverableLocation;
import org.cgiar.ccafs.marlo.data.model.DeliverableParticipant;
import org.cgiar.ccafs.marlo.data.model.DeliverableQualityCheck;
import org.cgiar.ccafs.marlo.data.model.DeliverableUserPartnership;
import org.cgiar.ccafs.marlo.data.model.DeliverableUserPartnershipPerson;
import org.cgiar.ccafs.marlo.data.model.FundingSource;
import org.cgiar.ccafs.marlo.data.model.FundingSourceLocation;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.LocElement;
import org.cgiar.ccafs.marlo.data.model.LocElementType;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProgramType;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectBudget;
import org.cgiar.ccafs.marlo.data.model.ProjectCenterOutcome;
import org.cgiar.ccafs.marlo.data.model.ProjectClusterActivity;
import org.cgiar.ccafs.marlo.data.model.ProjectComponentLesson;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudy;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyCountry;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyRegion;
import org.cgiar.ccafs.marlo.data.model.ProjectFocus;
import org.cgiar.ccafs.marlo.data.model.ProjectHighlight;
import org.cgiar.ccafs.marlo.data.model.ProjectHighlightType;
import org.cgiar.ccafs.marlo.data.model.ProjectHighligthsTypeEnum;
import org.cgiar.ccafs.marlo.data.model.ProjectInfo;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovation;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationCountry;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationRegion;
import org.cgiar.ccafs.marlo.data.model.ProjectLeverage;
import org.cgiar.ccafs.marlo.data.model.ProjectLocation;
import org.cgiar.ccafs.marlo.data.model.ProjectLocationElementType;
import org.cgiar.ccafs.marlo.data.model.ProjectLp6Contribution;
import org.cgiar.ccafs.marlo.data.model.ProjectOutcome;
import org.cgiar.ccafs.marlo.data.model.ProjectPartner;
import org.cgiar.ccafs.marlo.data.model.ProjectPartnerContribution;
import org.cgiar.ccafs.marlo.data.model.ProjectPartnerLocation;
import org.cgiar.ccafs.marlo.data.model.ProjectPartnerPartnership;
import org.cgiar.ccafs.marlo.data.model.ProjectPartnerPartnershipLocation;
import org.cgiar.ccafs.marlo.data.model.ProjectPartnerPartnershipResearchPhase;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicy;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicyCountry;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicyRegion;
import org.cgiar.ccafs.marlo.data.model.ProjectScope;
import org.cgiar.ccafs.marlo.data.model.ProjectSectionStatusEnum;
import org.cgiar.ccafs.marlo.data.model.ProjectStatusEnum;
import org.cgiar.ccafs.marlo.utils.CountryLocationLevel;
import org.cgiar.ccafs.marlo.validation.BaseValidator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

@Named
public class ProjectSectionValidator<T extends BaseAction> extends BaseValidator {

  private final ProjectManager projectManager;

  private final ProjectLocationValidator locationValidator;

  private final ProjectBudgetsValidator projectBudgetsValidator;

  private final DeliverableValidator deliverableValidator;

  private final ProjectOutcomeValidator projectOutcomeValidator;

  // private final ProjectBudgetsCoAValidator projectBudgetsCoAValidator;

  private final ProjectBudgetsFlagshipValidator projectBudgetsFlagshipValidator;

  private final LocElementTypeManager locElementTypeManager;

  private final ProjectLocationElementTypeManager projectLocationElementTypeManager;

  private final DeliverableQualityCheckManager deliverableQualityCheckManager;

  private final ProjectDescriptionValidator descriptionValidator;

  private final ProjectPartnersValidator projectPartnerValidator;

  private final ProjectActivitiesValidator projectActivitiesValidator;

  private final ProjectLeverageValidator projectLeverageValidator;

  private final ProjectHighLightValidator projectHighLightValidator;

  private final ProjectCaseStudyValidation projectCaseStudyValidation;

  private final ProjectCCAFSOutcomeValidator projectCCAFSOutcomeValidator;

  private final ProjectOutcomesPandRValidator projectOutcomesPandRValidator;

  private final ProjectOtherContributionsValidator projectOtherContributionsValidator;

  private final ProjectOutputsValidator projectOutputsValidator;
  private final ProjectExpectedStudiesValidator projectExpectedStudiesValidator;

  private final ProjectExpectedStudyCountryManager projectExpectedStudyCountryManager;

  private final ProjectInnovationValidator projectInnovationValidator;

  private final ProjectInnovationCountryManager projectInnovationCountryManager;

  private final FundingSourceManager fundingSourceManager;

  private final DeliverableIntellectualAssetManager deliverableIntellectualAssetManager;

  private final DeliverableParticipantManager deliverableParticipantManager;

  private final PhaseManager phaseManager;

  private final GlobalUnitManager crpManager;

  private final GlobalUnitProjectManager globalUnitProjectManager;

  private final ProjectCenterMappingValidator projectCenterMappingValidator;

  private final ProjectLP6Validator projectLP6Validator;

  private final DeliverableTypeManager deliverableTypeManager;

  private final DeliverableInfoManager deliverableInfoManager;

  private final DeliverableLocationManager deliverableLocationManager;

  private final DeliverableGeographicRegionManager deliverableGeographicRegionManager;


  private final ProjectPolicyValidator projectPolicyValidator;

  private final ProjectPolicyCountryManager projectPolicyCountryManager;

  private final ProjectPolicyRegionManager projectPolicyRegionManager;

  private final ProjectExpectedStudyRegionManager projectExpectedStudyRegionManager;

  private final ProjectInnovationRegionManager projectInnovationRegionManager;

  @Inject
  public ProjectSectionValidator(ProjectManager projectManager, ProjectLocationValidator locationValidator,
    ProjectBudgetsValidator projectBudgetsValidator, DeliverableValidator deliverableValidator,
    ProjectOutcomeValidator projectOutcomeValidator, LocElementTypeManager locElementTypeManager,
    ProjectLocationElementTypeManager projectLocationElementTypeManager,
    DeliverableQualityCheckManager deliverableQualityCheckManager, ProjectDescriptionValidator descriptionValidator,
    ProjectPartnersValidator projectPartnerValidator, ProjectActivitiesValidator projectActivitiesValidator,
    ProjectLeverageValidator projectLeverageValidator, ProjectHighLightValidator projectHighLightValidator,
    ProjectCaseStudyValidation projectCaseStudyValidation, ProjectCCAFSOutcomeValidator projectCCAFSOutcomeValidator,
    ProjectOutcomesPandRValidator projectOutcomesPandRValidator,
    ProjectOtherContributionsValidator projectOtherContributionsValidator,
    ProjectOutputsValidator projectOutputsValidator, ProjectExpectedStudiesValidator projectExpectedStudiesValidator,
    ProjectBudgetsFlagshipValidator projectBudgetsFlagshipValidator,
    ProjectExpectedStudyCountryManager projectExpectedStudyCountryManager,
    ProjectInnovationValidator projectInnovationValidator,
    ProjectInnovationCountryManager projectInnovationCountryManager, FundingSourceManager fundingSourceManager,
    DeliverableIntellectualAssetManager deliverableIntellectualAssetManager,
    DeliverableParticipantManager deliverableParticipantManager, PhaseManager phaseManager,
    GlobalUnitManager crpManager, ProjectCenterMappingValidator projectCenterMappingValidator,
    GlobalUnitProjectManager globalUnitProjectManager, DeliverableTypeManager deliverableTypeManager,
    DeliverableInfoManager deliverableInfoManager, DeliverableLocationManager deliverableLocationManager,
    DeliverableGeographicRegionManager deliverableGeographicRegionManager, ProjectLP6Validator projectLP6Validator,
    ProjectPolicyValidator projectPolicyValidator, ProjectPolicyManager projectPolicyManager,
    ProjectPolicyCountryManager projectPolicyCountryManager, ProjectPolicyRegionManager projectPolicyRegionManager,
    ProjectExpectedStudyRegionManager projectExpectedStudyRegionManager,
    ProjectInnovationRegionManager projectInnovationRegionManager) {
    this.projectManager = projectManager;
    this.locationValidator = locationValidator;
    this.projectBudgetsValidator = projectBudgetsValidator;
    this.deliverableValidator = deliverableValidator;
    this.projectOutcomeValidator = projectOutcomeValidator;
    // this.projectBudgetsCoAValidator = projectBudgetsCoAValidator;
    this.locElementTypeManager = locElementTypeManager;
    this.projectLocationElementTypeManager = projectLocationElementTypeManager;
    this.projectPartnerValidator = projectPartnerValidator;
    this.projectActivitiesValidator = projectActivitiesValidator;
    this.deliverableQualityCheckManager = deliverableQualityCheckManager;
    this.descriptionValidator = descriptionValidator;
    this.projectLeverageValidator = projectLeverageValidator;
    this.projectCaseStudyValidation = projectCaseStudyValidation;
    this.projectHighLightValidator = projectHighLightValidator;
    this.projectCCAFSOutcomeValidator = projectCCAFSOutcomeValidator;
    this.projectOutcomesPandRValidator = projectOutcomesPandRValidator;
    this.projectOtherContributionsValidator = projectOtherContributionsValidator;
    this.projectOutputsValidator = projectOutputsValidator;
    this.projectExpectedStudiesValidator = projectExpectedStudiesValidator;
    this.projectBudgetsFlagshipValidator = projectBudgetsFlagshipValidator;
    this.projectExpectedStudyCountryManager = projectExpectedStudyCountryManager;
    this.projectInnovationCountryManager = projectInnovationCountryManager;
    this.projectInnovationValidator = projectInnovationValidator;
    this.fundingSourceManager = fundingSourceManager;
    this.deliverableIntellectualAssetManager = deliverableIntellectualAssetManager;
    this.deliverableParticipantManager = deliverableParticipantManager;
    this.phaseManager = phaseManager;
    this.crpManager = crpManager;
    this.projectCenterMappingValidator = projectCenterMappingValidator;
    this.globalUnitProjectManager = globalUnitProjectManager;
    this.deliverableTypeManager = deliverableTypeManager;
    this.deliverableInfoManager = deliverableInfoManager;
    this.deliverableLocationManager = deliverableLocationManager;
    this.deliverableGeographicRegionManager = deliverableGeographicRegionManager;
    this.projectLP6Validator = projectLP6Validator;
    this.projectPolicyCountryManager = projectPolicyCountryManager;
    this.projectPolicyValidator = projectPolicyValidator;
    this.projectPolicyRegionManager = projectPolicyRegionManager;
    this.projectExpectedStudyRegionManager = projectExpectedStudyRegionManager;
    this.projectInnovationRegionManager = projectInnovationRegionManager;
  }


  public List<CountryLocationLevel> getProjectLocationsData(Project project, BaseAction action) {

    List<Map<String, Object>> parentLocations = new ArrayList<>();
    List<CountryLocationLevel> locationLevels = new ArrayList<>();
    List<ProjectLocationElementType> locationsElementType = new ArrayList<>(
      project.getProjectLocationElementTypes().stream().filter(pl -> pl.getIsGlobal()).collect(Collectors.toList()));

    project
      .setLocations(
        new ArrayList<ProjectLocation>(project
          .getProjectLocations().stream().filter(p -> p.isActive() && p.getLocElementType() == null
            && p.getLocElement() != null && p.getPhase().equals(action.getActualPhase()))
          .collect(Collectors.toList())));
    Map<String, Object> locationParent;
    if (!project.getLocations().isEmpty()) {

      if (locationsElementType != null) {
        for (ProjectLocationElementType projectLocationElementType : locationsElementType) {
          boolean existElementType = false;
          for (ProjectLocation location : project.getLocations()) {
            if (projectLocationElementType.getLocElementType().getId() == location.getLocElement().getLocElementType()
              .getId()) {
              existElementType = true;
            }
          }
          if (!existElementType) {
            locationParent = new HashMap<String, Object>();
            if (!parentLocations.isEmpty()) {
              locationParent.put(projectLocationElementType.getLocElementType().getName(),
                projectLocationElementType.getLocElementType().getId());
              if (!parentLocations.contains(locationParent)) {
                parentLocations.add(locationParent);
              }
            } else {
              locationParent.put(projectLocationElementType.getLocElementType().getName(),
                projectLocationElementType.getLocElementType().getId());
              parentLocations.add(locationParent);
            }
          }
        }
      }

      for (ProjectLocation location : project.getLocations()) {
        locationParent = new HashMap<String, Object>();
        if (!parentLocations.isEmpty()) {
          locationParent.put(location.getLocElement().getLocElementType().getName(),
            location.getLocElement().getLocElementType().getId());
          if (!parentLocations.contains(locationParent)) {
            parentLocations.add(locationParent);
          }
        } else {
          locationParent.put(location.getLocElement().getLocElementType().getName(),
            location.getLocElement().getLocElementType().getId());
          parentLocations.add(locationParent);
        }

      }

    } else {
      if (!locationsElementType.isEmpty()) {
        for (ProjectLocationElementType projectLocationElementType : locationsElementType) {
          locationParent = new HashMap<String, Object>();
          if (!parentLocations.isEmpty()) {
            locationParent.put(projectLocationElementType.getLocElementType().getName(),
              projectLocationElementType.getLocElementType().getId());
            if (!parentLocations.contains(locationParent)) {
              parentLocations.add(locationParent);
            }
          } else {
            locationParent.put(projectLocationElementType.getLocElementType().getName(),
              projectLocationElementType.getLocElementType().getId());
            parentLocations.add(locationParent);
          }
        }
      }
    }

    CountryLocationLevel countryLocationLevel;
    ProjectLocationElementType locationElementType = null;
    for (Map<String, Object> map : parentLocations) {

      for (Map.Entry<String, Object> entry : map.entrySet()) {
        countryLocationLevel = new CountryLocationLevel();
        countryLocationLevel.setId(Long.parseLong(entry.getValue().toString()));
        countryLocationLevel.setName(entry.getKey());
        countryLocationLevel.setLocElements(new ArrayList<LocElement>());

        LocElementType elementType =
          locElementTypeManager.getLocElementTypeById(Long.parseLong(entry.getValue().toString()));

        countryLocationLevel.setAllElements(new ArrayList<LocElement>(elementType.getLocElements()));

        for (ProjectLocation projectLocation : project.getLocations().stream().filter(l -> l.isActive())
          .collect(Collectors.toList())) {
          if (projectLocation.getLocElement().getLocElementType().getId() == Long
            .parseLong(entry.getValue().toString())) {
            countryLocationLevel.getLocElements().add(projectLocation.getLocElement());
          }
        }

        if (elementType.getId() == 2 || elementType.getCrp() != null) {

          locationElementType =
            projectLocationElementTypeManager.getByProjectAndElementType(project.getId(), elementType.getId());
          countryLocationLevel.setList(true);
        } else {
          countryLocationLevel.setList(false);
        }
        locationLevels.add(countryLocationLevel);
      }
    }
    return locationLevels;
  }

  public boolean locElementSelected(long locElementID, long projectID, BaseAction action) {


    Project projectDB = projectManager.getProjectById(projectID);
    List<ProjectLocation> locElements = projectDB
      .getProjectLocations().stream().filter(c -> c.isActive() && c.getLocElement() != null
        && c.getLocElement().getId().longValue() == locElementID && c.getPhase().equals(action.getActualPhase()))
      .collect(Collectors.toList());

    return !locElements.isEmpty();


  }

  public boolean locElementTypeSelected(long locElementID, long projectID, BaseAction action) {


    Project projectDB = projectManager.getProjectById(projectID);
    List<ProjectLocation> locElements = projectDB.getProjectLocations().stream()
      .filter(c -> c.isActive() && c.getLocElementType() != null
        && c.getLocElementType().getId().longValue() == locElementID && c.getPhase().equals(action.getActualPhase()))
      .collect(Collectors.toList());

    return !locElements.isEmpty();


  }


  public void prepareFundingList(Project project, BaseAction action) {

    Project projectDB = projectManager.getProjectById(project.getId());


    List<ProjectBudget> projectBudgets =
      new ArrayList<>(projectDB.getProjectBudgets().stream().filter(pb -> pb.isActive()
        && pb.getYear() == action.getActualPhase().getYear() && pb.getPhase().equals(action.getActualPhase()))
        .collect(Collectors.toList()));

    List<FundingSource> fundingSources = new ArrayList<>();
    for (ProjectBudget projectBudget : projectBudgets) {

      fundingSources.add(projectBudget.getFundingSource());

    }

    HashSet<FundingSource> fuHashSet = new HashSet<>();
    fuHashSet.addAll(fundingSources);

    fundingSources = new ArrayList<>(fuHashSet);

    List<LocElement> locElements = new ArrayList<>();
    List<LocElementType> locElementTypes = new ArrayList<>();

    for (FundingSource fundingSource : fundingSources) {

      List<FundingSourceLocation> fundingSourceLocations = new ArrayList<>(
        fundingSource.getFundingSourceLocations().stream().filter(fs -> fs.isActive()).collect(Collectors.toList()));

      for (FundingSourceLocation fundingSourceLocation : fundingSourceLocations) {
        if (fundingSourceLocation.getLocElementType() == null) {
          locElements.add(fundingSourceLocation.getLocElement());

        } else {
          locElementTypes.add(fundingSourceLocation.getLocElementType());

        }
      }


    }


    HashSet<LocElement> hashElements = new HashSet<>();
    hashElements.addAll(locElements);
    locElements = new ArrayList<>(hashElements);

    for (LocElement locElement : hashElements) {
      CountryFundingSources countryFundingSources = new CountryFundingSources();
      countryFundingSources.setLocElement(locElement);


    }

    HashSet<LocElementType> hashElementTypes = new HashSet<>();
    hashElementTypes.addAll(locElementTypes);
    locElementTypes = new ArrayList<>(hashElementTypes);


  }


  public void validateCaseStduies(BaseAction action, Long projectID) {
    // Getting the project information.
    Project project = projectManager.getProjectById(projectID);

    List<CaseStudyProject> caseStudies =
      project.getCaseStudyProjects().stream().filter(d -> d.isActive()).collect(Collectors.toList());

    for (CaseStudyProject caseStudyProject : caseStudies) {
      if (caseStudyProject.isActive()
        && caseStudyProject.getCaseStudy().getYear() == action.getActualPhase().getYear()) {

        caseStudyProject.getCaseStudy().setIndicators(
          caseStudyProject.getCaseStudy().getCaseStudyIndicators().stream().collect(Collectors.toList()));
        List<String> idsIndicators = new ArrayList<>();
        for (CaseStudyIndicator caseStudyIndicator : caseStudyProject.getCaseStudy().getIndicators()) {
          idsIndicators.add(caseStudyIndicator.getIpIndicator().getId().toString());
        }
        caseStudyProject.getCaseStudy().setCaseStudyIndicatorsIds(idsIndicators);
        projectCaseStudyValidation.validate(action, project, caseStudyProject.getCaseStudy(), false);
      }


    }

  }

  public void validateCCAFSOutcomes(BaseAction action, Long projectID) {
    // Getting the project information.
    Project project = projectManager.getProjectById(projectID);

    project.setProjectIndicators(
      project.getIpProjectIndicators().stream().filter(c -> c.isActive()).collect(Collectors.toList()));
    projectCCAFSOutcomeValidator.validate(action, project, false);


  }

  public void validateContributionLp6(BaseAction action, Long projectID) {
    // Getting the project information.
    Project project = projectManager.getProjectById(projectID);

    List<ProjectLp6Contribution> lp6Contributions = project.getProjectLp6Contributions().stream()
      .filter(pl -> pl.isActive() && pl.getPhase().equals(action.getActualPhase())).collect(Collectors.toList());

    if (lp6Contributions != null && lp6Contributions.size() > 0) {
      ProjectLp6Contribution projectLp6Contribution = lp6Contributions.get(0);
      projectLP6Validator.validate(action, project, projectLp6Contribution, false);
    }
  }

  public void validateHighlight(BaseAction action, Long projectID) {
    // Getting the project information.
    Project project = projectManager.getProjectById(projectID);

    List<ProjectHighlight> highlights = project.getProjectHighligths().stream()
      .filter(d -> d.getProjectHighlightInfo(action.getActualPhase()) != null && d.isActive()
        && d.getProjectHighlightInfo(action.getActualPhase()).getYear().intValue() == action.getActualPhase().getYear())
      .collect(Collectors.toList());

    for (ProjectHighlight projectHighlight : highlights) {
      projectHighlight.setTypes(
        projectHighlight.getProjectHighligthsTypes().stream().filter(c -> c.isActive()).collect(Collectors.toList()));


      if (projectHighlight.getTypes() != null) {
        for (ProjectHighlightType projectHighligthsType : projectHighlight.getTypes()) {
          projectHighlight.getTypesIds().add(ProjectHighligthsTypeEnum.value(projectHighligthsType.getIdType() + ""));
          projectHighlight.getTypesids().add(projectHighligthsType.getIdType() + "");
        }
      }

      projectHighLightValidator.validate(action, project, projectHighlight, false);

    }

  }


  public void validateInnovations(BaseAction action, Long projectID) {
    // Getting the project information.
    Project project = projectManager.getProjectById(projectID);
    Boolean clearLead = null;

    Phase phase = action.getActualPhase();

    List<ProjectInnovation> innovations =
      project.getProjectInnovations().stream().filter(c -> c.isActive()).collect(Collectors.toList());
    project.setInnovations(new ArrayList<ProjectInnovation>());
    for (ProjectInnovation projectInnovation : innovations) {
      if (projectInnovation.getProjectInnovationInfo(phase) != null) {
        project.getInnovations().add(projectInnovation);
      }
    }

    for (ProjectInnovation innovation : project.getInnovations()) {

      if (innovation.getProjectInnovationInfo() == null) {
        innovation.getProjectInnovationInfo(phase);
      }


      // Setup Geographic Scope
      if (innovation.getProjectInnovationGeographicScopes() != null) {
        innovation.setGeographicScopes(new ArrayList<>(innovation.getProjectInnovationGeographicScopes().stream()
          .filter(o -> o.isActive() && o.getPhase().getId().equals(phase.getId())).collect(Collectors.toList())));
      }

      // Innovation Countries List
      if (innovation.getProjectInnovationCountries() == null) {
        innovation.setCountries(new ArrayList<>());
      } else {
        List<ProjectInnovationCountry> countries =
          projectInnovationCountryManager.getInnovationCountrybyPhase(innovation.getId(), phase.getId());
        innovation.setCountries(countries);
      }

      if (innovation.getProjectInnovationRegions() == null) {
        innovation.setRegions(new ArrayList<>());
      } else {
        List<ProjectInnovationRegion> geographics =
          projectInnovationRegionManager.getInnovationRegionbyPhase(innovation.getId(), phase.getId());

        // Load Regions
        innovation.setRegions(geographics.stream().filter(sc -> sc.getLocElement().getLocElementType().getId() == 1)
          .collect(Collectors.toList()));
      }


      // Innovation Organization Type List
      if (innovation.getProjectInnovationOrganizations() != null) {
        innovation.setOrganizations(new ArrayList<>(innovation.getProjectInnovationOrganizations().stream()
          .filter(o -> o.isActive() && o.getPhase().getId().equals(phase.getId())).collect(Collectors.toList())));
      }

      // Innovation Deliverable List
      if (innovation.getProjectInnovationDeliverables() != null) {
        innovation.setDeliverables(new ArrayList<>(innovation.getProjectInnovationDeliverables().stream()
          .filter(d -> d.isActive() && d.getPhase().getId().equals(phase.getId())).collect(Collectors.toList())));
      }

      // Innovation Contributing organizations List
      if (innovation.getProjectInnovationContributingOrganization() != null) {
        innovation
          .setContributingOrganizations(new ArrayList<>(innovation.getProjectInnovationContributingOrganization()
            .stream().filter(d -> d.getPhase().getId().equals(phase.getId())).collect(Collectors.toList())));
      }

      // Innovation Crp list
      if (innovation.getProjectInnovationCrps() != null) {
        innovation.setCrps(new ArrayList<>(innovation.getProjectInnovationCrps().stream()
          .filter(c -> c.isActive() && c.getPhase().getId().equals(phase.getId())).collect(Collectors.toList())));
      }

      // Innovation clear lead
      if (innovation != null && innovation.getProjectInnovationInfo(phase) != null) {
        if (innovation.getProjectInnovationInfo(phase).getClearLead() == null
          || innovation.getProjectInnovationInfo(phase).getClearLead() == false) {
          clearLead = false;
        } else {
          clearLead = true;
        }
      } else {
        clearLead = false;
      }


      if (innovation.getCountries() != null) {
        for (ProjectInnovationCountry country : innovation.getCountries()) {
          innovation.getCountriesIds().add(country.getLocElement().getIsoAlpha2());
        }
      }

      // Change the parameters for the new way to validate the data
      projectInnovationValidator.validate(action, project, innovation, clearLead, false, true, phase.getYear(),
        phase.getUpkeep());
    }

  }

  public void validateLeverage(BaseAction action, Long projectID) {
    // Getting the project information.
    Project project = projectManager.getProjectById(projectID);

    List<ProjectLeverage> projectLeverages = new ArrayList<>(project.getProjectLeverages().stream()
      .filter(pl -> pl.isActive() && pl.getYear() == action.getActualPhase().getYear()).collect(Collectors.toList()));

    project.setLeverages(projectLeverages);

    projectLeverageValidator.validate(action, project, false);


  }

  public void validateOtherContributions(BaseAction action, Long projectID) {
    // Getting the project information.
    Project project = projectManager.getProjectById(projectID);

    project.setCrpContributions(
      project.getProjectCrpContributions().stream().filter(c -> c.isActive()).collect(Collectors.toList()));

    project.setProjectOtherContributionsList(
      project.getProjectOtherContributions().stream().filter(c -> c.isActive()).collect(Collectors.toList()));

    project.setOtherContributionsList(
      project.getOtherContributions().stream().filter(c -> c.isActive()).collect(Collectors.toList()));
    projectOtherContributionsValidator.validate(action, project, false);


  }

  public void validateOutcomesPandR(BaseAction action, Long projectID) {
    // Getting the project information.
    Project project = projectManager.getProjectById(projectID);

    project.setOutcomesPandr(
      project.getProjectOutcomesPandr().stream().filter(c -> c.isActive()).collect(Collectors.toList()));
    projectOutcomesPandRValidator.validate(action, project, false);


  }

  public void validateOutputs(BaseAction action, Long projectID) {
    // Getting the project information.
    Project project = projectManager.getProjectById(projectID);

    project.setOverviews(
      project.getIpProjectContributionOverviews().stream().filter(c -> c.isActive()).collect(Collectors.toList()));
    projectOutputsValidator.validate(action, project, false);


  }

  public void validatePolicy(BaseAction action, Long projectID) {
    // Getting the project information.
    Project project = projectManager.getProjectById(projectID);

    Phase phase = action.getActualPhase();

    List<ProjectPolicy> policies =
      project.getProjectPolicies().stream().filter(c -> c.isActive()).collect(Collectors.toList());
    project.setPolicies(new ArrayList<ProjectPolicy>());
    for (ProjectPolicy projectPolicy : policies) {
      if (projectPolicy.getProjectPolicyInfo(phase) != null) {
        project.getPolicies().add(projectPolicy);
      }
    }

    for (ProjectPolicy policy : project.getPolicies()) {

      if (policy.getProjectPolicyInfo() == null) {
        policy.getProjectPolicyInfo(phase);
      }


      // Setup Geographic Scope
      if (policy.getProjectPolicyGeographicScopes() != null) {
        policy.setGeographicScopes(new ArrayList<>(policy.getProjectPolicyGeographicScopes().stream()
          .filter(o -> o.isActive() && o.getPhase().getId().equals(phase.getId())).collect(Collectors.toList())));
      }


      // Policy Countries List
      if (policy.getProjectPolicyCountries() == null) {
        policy.setCountries(new ArrayList<>());

      } else {
        List<ProjectPolicyCountry> geographics =
          projectPolicyCountryManager.getPolicyCountrybyPhase(policy.getId(), phase.getId());

        // Load Countries
        policy.setCountries(geographics.stream().filter(sc -> sc.getLocElement().getLocElementType().getId() == 2)
          .collect(Collectors.toList()));

      }

      if (policy.getProjectPolicyRegions() == null) {
        policy.setRegions(new ArrayList<>());
      } else {
        List<ProjectPolicyRegion> geographics =
          projectPolicyRegionManager.getPolicyRegionbyPhase(policy.getId(), phase.getId());

        // Load Regions
        policy.setRegions(geographics.stream().filter(sc -> sc.getLocElement().getLocElementType().getId() == 1)
          .collect(Collectors.toList()));
      }


      // Policy Type ( Whose Policy is This ? ) List
      if (policy.getProjectPolicyOwners() != null) {
        policy.setOwners(new ArrayList<>(policy.getProjectPolicyOwners().stream()
          .filter(o -> o.isActive() && o.getPhase().getId().equals(phase.getId())).collect(Collectors.toList())));
      }

      // Crp List
      if (policy.getProjectPolicyCrps() != null) {
        policy.setCrps(new ArrayList<>(policy.getProjectPolicyCrps().stream()
          .filter(o -> o.isActive() && o.getPhase().getId().equals(phase.getId())).collect(Collectors.toList())));
      }

      // SubIdos List
      if (policy.getProjectPolicySubIdos() != null) {
        policy.setSubIdos(new ArrayList<>(policy.getProjectPolicySubIdos().stream()
          .filter(o -> o.isActive() && o.getPhase().getId().equals(phase.getId())).collect(Collectors.toList())));
      }

      // Innovations List
      if (policy.getProjectPolicyInnovations() != null) {
        policy.setInnovations(new ArrayList<>(policy.getProjectPolicyInnovations().stream()
          .filter(o -> o.isActive() && o.getPhase().getId().equals(phase.getId())).collect(Collectors.toList())));
      }

      // Evidence List
      if (policy.getProjectExpectedStudyPolicies() != null) {
        policy.setEvidences(new ArrayList<>(policy.getProjectExpectedStudyPolicies().stream()
          .filter(o -> o.isActive() && o.getPhase().getId().equals(phase.getId())).collect(Collectors.toList())));
      }

      // Cgiar Cross Cutting Markers List
      if (policy.getCrossCuttingMarkers() != null) {
        policy.setCrossCuttingMarkers(new ArrayList<>(policy.getProjectPolicyCrossCuttingMarkers().stream()
          .filter(o -> o.isActive() && o.getPhase().getId().equals(phase.getId())).collect(Collectors.toList())));
      }

      if (policy.getCountries() != null) {
        for (ProjectPolicyCountry country : policy.getCountries()) {
          policy.getCountriesIds().add(country.getLocElement().getIsoAlpha2());
        }
      }

      projectPolicyValidator.validate(action, project, policy, false);
    }

  }

  public void validateProjectActivities(BaseAction action, Long projectID) {
    // Getting the project information.
    Project project = projectManager.getProjectById(projectID);

    project.setProjectActivities(new ArrayList<Activity>(project.getActivities().stream()
      .filter(a -> a.isActive() && a.getPhase().equals(action.getActualPhase()) && a.getActivityStatus() != null
        && ((a.getActivityStatus() == Integer.parseInt(ProjectStatusEnum.Ongoing.getStatusId())
          || (a.getActivityStatus() == Integer.parseInt(ProjectStatusEnum.Extended.getStatusId())))))
      .collect(Collectors.toList())));


    if (project.getProjectActivities() != null) {
      for (Activity openActivity : project.getProjectActivities()) {
        openActivity.setDeliverables(new ArrayList<DeliverableActivity>(openActivity.getDeliverableActivities().stream()
          .filter(da -> da.isActive() && da.getPhase().equals(action.getActualPhase())).collect(Collectors.toList())));
      }
    }

    project.setClosedProjectActivities(new ArrayList<Activity>(project.getActivities().stream()
      .filter(a -> a.isActive() && a.getActivityStatus() != null && a.getPhase().equals(action.getActualPhase())
        && ((a.getActivityStatus() == Integer.parseInt(ProjectStatusEnum.Complete.getStatusId())
          || (a.getActivityStatus() == Integer.parseInt(ProjectStatusEnum.Cancelled.getStatusId())))))
      .collect(Collectors.toList())));

    if (project.getClosedProjectActivities() != null) {
      for (Activity closedActivity : project.getClosedProjectActivities()) {
        closedActivity.setDeliverables(new ArrayList<DeliverableActivity>(
          closedActivity.getDeliverableActivities().stream().filter(da -> da.isActive()).collect(Collectors.toList())));
      }
    }
    projectActivitiesValidator.validate(action, project, false);
  }

  public void validateProjectBudgets(BaseAction action, Long projectID) {
    // Getting the project information.
    Project project = projectManager.getProjectById(projectID);

    project.setBudgets(project.getProjectBudgets().stream()
      .filter(c -> c.isActive() && c.getPhase() != null && c.getPhase().equals(action.getActualPhase())
        && (c.getFundingSource().getFundingSourceInfo(action.getActualPhase()).getStatus() != 3
          || c.getFundingSource().getFundingSourceInfo(action.getActualPhase()).getStatus() != 5))
      .collect(Collectors.toList()));

    if ((action.isReportingActive() || action.isUpKeepActive())
      && action.hasSpecificities(action.getCrpEnableBudgetExecution())) {
      project.setBudgetExecutions(project.getProjectBudgetExecutions().stream()
        .filter(c -> c.isActive() && c.getPhase() != null && c.getPhase().equals(action.getActualPhase()))
        .collect(Collectors.toList()));
    }

    projectBudgetsValidator.validate(action, project, false);

  }


  /*
   * public void validateProjectBudgetsCoAs(BaseAction action, Long projectID, boolean sMessage) {
   * // Getting the project information.
   * Project project = projectManager.getProjectById(projectID);
   * project.setBudgetsCluserActvities(project.getProjectBudgetsCluserActvities().stream()
   * .filter(c -> c.isActive() && c.getPhase().equals(action.getActualPhase())).collect(Collectors.toList()));
   * if (!(project.getProjectBudgetsCluserActvities().isEmpty()
   * || project.getProjectBudgetsCluserActvities().size() == 1)) {
   * projectBudgetsCoAValidator.validate(action, project, false, sMessage);
   * }
   * }
   */

  public void validateProjectBudgetsFlagship(BaseAction action, Long projectID, boolean sMessage) {
    // Getting the project information.
    Project project = projectManager.getProjectById(projectID);
    project.setBudgetsFlagship(project.getProjectBudgetsFlagships().stream()
      .filter(c -> c.isActive() && c.getPhase().equals(action.getActualPhase())).collect(Collectors.toList()));
    if (!(project.getProjectBudgetsFlagships().isEmpty() || project.getProjectBudgetsFlagships().size() == 1)) {
      projectBudgetsFlagshipValidator.validate(action, project, false, sMessage);
    }

  }

  public void validateProjectCenterMapping(BaseAction action, Long projectID, Phase phase) {
    Project project = projectManager.getProjectById(projectID);

    GlobalUnit loggedCrp = (GlobalUnit) action.getSession().get(APConstants.SESSION_CRP);

    ProjectInfo projectInfo = project.getProjecInfoPhase(phase);

    // Load the DB information and adjust it to the structures with which the front end
    project.setProjectInfo(project.getProjecInfoPhase(phase));
    if (project.getProjectInfo() == null) {
      project.setProjectInfo(new ProjectInfo());
    }

    // Load the center Programs
    project.setFlagshipValue("");
    project.setRegionsValue("");
    List<CrpProgram> programs = new ArrayList<>();
    for (ProjectFocus projectFocuses : project.getProjectFocuses().stream()
      .filter(c -> c.isActive() && c.getPhase() != null && c.getPhase().equals(phase)
        && c.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue()
        && c.getCrpProgram().getCrp().getId().equals(loggedCrp.getId()))
      .collect(Collectors.toList())) {
      programs.add(projectFocuses.getCrpProgram());
      if (project.getFlagshipValue().isEmpty()) {
        project.setFlagshipValue(projectFocuses.getCrpProgram().getId().toString());
      } else {
        project.setFlagshipValue(project.getFlagshipValue() + "," + projectFocuses.getCrpProgram().getId().toString());
      }
    }

    List<CrpProgram> regions = new ArrayList<>();

    for (ProjectFocus projectFocuses : project.getProjectFocuses().stream()
      .filter(c -> c.isActive() && c.getPhase() != null && c.getPhase().equals(phase)
        && c.getCrpProgram().getProgramType() == ProgramType.REGIONAL_PROGRAM_TYPE.getValue()
        && c.getCrpProgram().getCrp().getId().equals(loggedCrp.getId()))
      .collect(Collectors.toList())) {
      regions.add(projectFocuses.getCrpProgram());
      if (project.getRegionsValue() != null && project.getRegionsValue().isEmpty()) {
        project.setRegionsValue(projectFocuses.getCrpProgram().getId().toString());
      } else {
        project.setRegionsValue(project.getRegionsValue() + "," + projectFocuses.getCrpProgram().getId().toString());
      }
    }

    List<ProjectCenterOutcome> projectCenterOutcomes = new ArrayList<>();
    for (ProjectCenterOutcome projectCenterOutcome : project.getProjectCenterOutcomes().stream()
      .filter(c -> c.isActive() && c.getPhase() != null && c.getPhase().equals(phase)).collect(Collectors.toList())) {
      projectCenterOutcomes.add(projectCenterOutcome);
    }
    project.setCenterOutcomes(projectCenterOutcomes);
    project.setFlagships(programs);
    project.setRegions(regions);

    projectCenterMappingValidator.validate(action, project, false, phase.getId());
  }

  public void validateProjectDeliverables(BaseAction action, Long projectID) {
    // Getting the project information.
    Project project = projectManager.getProjectById(projectID);

    Phase phase = action.getActualPhase();

    List<Deliverable> deliverables;

    if (project.getDeliverables() != null) {

      List<DeliverableInfo> infos = deliverableInfoManager.getDeliverablesInfoByProjectAndPhase(phase, project);
      deliverables = new ArrayList<>();
      if (infos != null && !infos.isEmpty()) {
        for (DeliverableInfo deliverableInfo : infos) {
          Deliverable deliverable = deliverableInfo.getDeliverable();
          deliverable.setDeliverableInfo(deliverableInfo);
          deliverables.add(deliverable);
        }
      }

      for (Deliverable deliverable : deliverables) {

        deliverable.getDeliverableInfo(phase);

        // Setup Geographic Scope
        if (deliverable.getDeliverableGeographicScopes() != null) {
          deliverable.setGeographicScopes(new ArrayList<>(deliverable.getDeliverableGeographicScopes().stream()
            .filter(o -> o.isActive() && o.getPhase().getId().equals(phase.getId())).collect(Collectors.toList())));
        }

        // Deliverable Countries List
        if (deliverable.getDeliverableLocations() == null) {
          deliverable.setCountries(new ArrayList<>());
        } else {
          List<DeliverableLocation> countries =
            deliverableLocationManager.getDeliverableLocationbyPhase(deliverable.getId(), phase.getId());
          deliverable.setCountries(countries);
        }

        if (deliverable.getCountries() != null) {
          for (DeliverableLocation country : deliverable.getCountries()) {
            deliverable.getCountriesIds().add(country.getLocElement().getIsoAlpha2());
          }
        }

        /*
         * -- Deliverable responsible
         */
        if (deliverable.getDeliverableUserPartnerships() != null) {

          List<DeliverableUserPartnership> deList = deliverable.getDeliverableUserPartnerships().stream()
            .filter(dp -> dp.isActive() && dp.getPhase().getId().equals(phase.getId())
              && dp.getDeliverablePartnerType().getId().equals(APConstants.DELIVERABLE_PARTNERSHIP_TYPE_RESPONSIBLE))
            .collect(Collectors.toList());

          if (deList != null && !deList.isEmpty()) {
            Collections.sort(deList, (p1, p2) -> p1.getInstitution().getId().compareTo(p2.getInstitution().getId()));
            deliverable.setResponsiblePartnership(new ArrayList<>());
            for (DeliverableUserPartnership deliverableUserPartnership : deList) {

              if (deliverableUserPartnership.getDeliverableUserPartnershipPersons() != null) {
                List<DeliverableUserPartnershipPerson> partnershipPersons =
                  new ArrayList<>(deliverableUserPartnership.getDeliverableUserPartnershipPersons().stream()
                    .filter(d -> d.isActive()).collect(Collectors.toList()));
                deliverableUserPartnership.setPartnershipPersons(partnershipPersons);
              }
              deliverable.getResponsiblePartnership().add(deliverableUserPartnership);
            }

          }
        }


        /*
         * -- Deliverable Others
         */
        if (deliverable.getDeliverableUserPartnerships() != null) {

          List<DeliverableUserPartnership> deList = deliverable.getDeliverableUserPartnerships().stream()
            .filter(dp -> dp.isActive() && dp.getPhase().getId().equals(phase.getId())
              && dp.getDeliverablePartnerType().getId().equals(APConstants.DELIVERABLE_PARTNERSHIP_TYPE_OTHER))
            .collect(Collectors.toList());

          if (deList != null && !deList.isEmpty()) {
            Collections.sort(deList, (p1, p2) -> p1.getInstitution().getId().compareTo(p2.getInstitution().getId()));
            deliverable.setOtherPartnerships(new ArrayList<>());
            for (DeliverableUserPartnership deliverableUserPartnership : deList) {

              if (deliverableUserPartnership.getDeliverableUserPartnershipPersons() != null) {
                List<DeliverableUserPartnershipPerson> partnershipPersons =
                  new ArrayList<>(deliverableUserPartnership.getDeliverableUserPartnershipPersons().stream()
                    .filter(d -> d.isActive()).collect(Collectors.toList()));
                deliverableUserPartnership.setPartnershipPersons(partnershipPersons);
              }
              deliverable.getOtherPartnerships().add(deliverableUserPartnership);
            }

          }
        }

        // Expected Study Geographic Regions List
        if (deliverable.getDeliverableGeographicRegions() != null
          && !deliverable.getDeliverableGeographicRegions().isEmpty()) {
          deliverable.setDeliverableRegions(new ArrayList<>(
            deliverableGeographicRegionManager.getDeliverableGeographicRegionbyPhase(deliverable.getId(), phase.getId())
              .stream().filter(le -> le.isActive() && le.getLocElement().getLocElementType().getId() == 1)
              .collect(Collectors.toList())));
        }

        deliverable.setFundingSources(deliverable.getDeliverableFundingSources().stream()
          .filter(c -> c.isActive() && c.getPhase() != null && c.getPhase().equals(phase))
          .collect(Collectors.toList()));

        for (DeliverableFundingSource deliverableFundingSource : deliverable.getFundingSources()) {

          deliverableFundingSource.setFundingSource(
            fundingSourceManager.getFundingSourceById(deliverableFundingSource.getFundingSource().getId()));
          deliverableFundingSource.getFundingSource().setFundingSourceInfo(
            deliverableFundingSource.getFundingSource().getFundingSourceInfo(action.getActualPhase()));
          if (deliverableFundingSource.getFundingSource().getFundingSourceInfo() == null) {
            deliverableFundingSource.getFundingSource().setFundingSourceInfo(
              deliverableFundingSource.getFundingSource().getFundingSourceInfoLast(action.getActualPhase()));
          }
        }
        deliverable.setGenderLevels(deliverable.getDeliverableGenderLevels().stream()
          .filter(c -> c.isActive() && c.getPhase().equals(phase)).collect(Collectors.toList()));

        List<DeliverableCrossCuttingMarker> deliverableCrossCuttingMarkers =
          deliverable.getDeliverableCrossCuttingMarkers().stream()
            .filter(dc -> dc.isActive() && dc.getPhase().equals(action.getActualPhase())).collect(Collectors.toList());

        if (deliverableCrossCuttingMarkers != null && !deliverableCrossCuttingMarkers.isEmpty()) {
          deliverable.setCrossCuttingMarkers(deliverableCrossCuttingMarkers);
        }

        if (action.isReportingActive() || action.isUpKeepActive()) {

          DeliverableQualityCheck deliverableQualityCheck =
            deliverableQualityCheckManager.getDeliverableQualityCheckByDeliverable(deliverable.getId(), phase.getId());
          deliverable.setQualityCheck(deliverableQualityCheck);

          if (deliverable.getDeliverableMetadataElements() != null) {
            deliverable.setMetadataElements(new ArrayList<>(deliverable.getDeliverableMetadataElements().stream()
              .filter(c -> c.isActive() && c.getPhase().equals(action.getActualPhase())).collect(Collectors.toList())));
          }

          if (deliverable.getDeliverableDisseminations() != null) {
            deliverable.setDisseminations(new ArrayList<>(deliverable.getDeliverableDisseminations().stream()
              .filter(c -> c.isActive() && c.getPhase().equals(phase)).collect(Collectors.toList())));
            if (deliverable.getDisseminations().size() > 0) {
              deliverable.setDissemination(deliverable.getDisseminations().get(0));
            } else {
              deliverable.setDissemination(new DeliverableDissemination());
            }
          }

          if (deliverable.getDeliverableDataSharingFiles() != null) {
            deliverable.setDataSharingFiles(new ArrayList<>(deliverable.getDeliverableDataSharingFiles().stream()
              .filter(c -> c.isActive() && c.getPhase().equals(phase)).collect(Collectors.toList())));
          }

          if (deliverable.getDeliverablePublicationMetadatas() != null) {
            deliverable.setPublicationMetadatas(new ArrayList<>(deliverable.getDeliverablePublicationMetadatas()
              .stream().filter(c -> c.isActive() && c.getPhase().equals(phase)).collect(Collectors.toList())));
          }
          if (!deliverable.getPublicationMetadatas().isEmpty()) {
            deliverable.setPublication(deliverable.getPublicationMetadatas().get(0));
          }

          if (deliverable.getDeliverableDataSharings() != null) {
            deliverable.setDataSharing(new ArrayList<>(deliverable.getDeliverableDataSharings().stream()
              .filter(c -> c.getPhase().equals(phase)).collect(Collectors.toList())));
          }


          deliverable.setUsers(deliverable.getDeliverableUsers().stream()
            .filter(c -> c.isActive() && c.getPhase().equals(phase)).collect(Collectors.toList()));
          deliverable.setCrps(deliverable.getDeliverableCrps().stream()
            .filter(c -> c.isActive() && c.getPhase().equals(phase)).collect(Collectors.toList()));
          deliverable.setFiles(new ArrayList<>());
          for (DeliverableDataSharingFile dataSharingFile : deliverable.getDeliverableDataSharingFiles().stream()
            .filter(c -> c.isActive() && c.getPhase().equals(phase)).collect(Collectors.toList())) {

            DeliverableFile deFile = new DeliverableFile();
            switch (dataSharingFile.getTypeId().toString()) {
              case APConstants.DELIVERABLE_FILE_LOCALLY_HOSTED:
                deFile.setHosted(APConstants.DELIVERABLE_FILE_LOCALLY_HOSTED_STR);
                deFile.setName(dataSharingFile.getFile().getFileName());
                break;

              case APConstants.DELIVERABLE_FILE_EXTERNALLY_HOSTED:
                deFile.setHosted(APConstants.DELIVERABLE_FILE_EXTERNALLY_HOSTED_STR);
                deFile.setName(dataSharingFile.getExternalFile());
                break;
            }
            deFile.setId(dataSharingFile.getId());
            deFile.setSize(0);
            deliverable.getFiles().add(deFile);
          }

          if (action.hasSpecificities(action.crpDeliverableIntellectualAsset())) {
            if (deliverable.getDeliverableIntellectualAssets() != null) {
              List<DeliverableIntellectualAsset> intellectualAssets = deliverable.getDeliverableIntellectualAssets()
                .stream().filter(c -> c.isActive() && c.getPhase().equals(phase)).collect(Collectors.toList());
            }
          }

          if (deliverable.getDeliverableParticipants() != null) {
            List<DeliverableParticipant> deliverableParticipants = deliverable.getDeliverableParticipants().stream()
              .filter(c -> c.isActive() && c.getPhase().equals(phase)).collect(Collectors.toList());

            if (deliverableParticipants.size() > 0) {
              deliverable.setDeliverableParticipant(
                deliverableParticipantManager.getDeliverableParticipantById(deliverableParticipants.get(0).getId()));


            } else {
              deliverable.setDeliverableParticipant(new DeliverableParticipant());
            }
          }

        }
        deliverableValidator.validate(action, deliverable, false);
      }
    }


  }

  public void validateProjectDescription(BaseAction action, Long projectID) {
    Project project = projectManager.getProjectById(projectID);
    ProjectInfo projectInfo = project.getProjecInfoPhase(action.getActualPhase());

    List<CrpProgram> programs = new ArrayList<>();
    for (ProjectFocus projectFocuses : project.getProjectFocuses().stream()
      .filter(c -> c.isActive() && c.getPhase() != null && c.getPhase().equals(action.getActualPhase())
        && c.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue())
      .collect(Collectors.toList())) {
      programs.add(projectFocuses.getCrpProgram());

      if (project.getFlagshipValue() == null) {
        project.setFlagshipValue(projectFocuses.getCrpProgram().getId().toString());

      } else {
        project.setFlagshipValue(project.getFlagshipValue() + "," + projectFocuses.getCrpProgram().getId().toString());
      }
    }

    List<CrpProgram> regions = new ArrayList<>();
    for (ProjectFocus projectFocuses : project.getProjectFocuses().stream()
      .filter(c -> c.isActive() && c.getPhase() != null && c.getPhase().equals(action.getActualPhase())
        && c.getCrpProgram().getProgramType() == ProgramType.REGIONAL_PROGRAM_TYPE.getValue())
      .collect(Collectors.toList())) {
      regions.add(projectFocuses.getCrpProgram());
      if (project.getRegionsValue() == null) {
        project.setRegionsValue(projectFocuses.getCrpProgram().getId().toString());

      } else {
        project.setRegionsValue(project.getRegionsValue() + "," + projectFocuses.getCrpProgram().getId().toString());
      }
    }

    List<ProjectClusterActivity> projectClusterActivities = new ArrayList<>();
    for (ProjectClusterActivity projectClusterActivity : project.getProjectClusterActivities().stream()
      .filter(c -> c.isActive() && c.getPhase() != null && c.getPhase().equals(action.getActualPhase()))
      .collect(Collectors.toList())) {

      projectClusterActivity.getCrpClusterOfActivity().setLeaders(projectClusterActivity.getCrpClusterOfActivity()
        .getCrpClusterActivityLeaders().stream().filter(c -> c.isActive()).collect(Collectors.toList()));
      projectClusterActivities.add(projectClusterActivity);
    }

    List<ProjectScope> projectLocations = new ArrayList<>();
    for (ProjectScope projectLocation : project.getProjectScopes().stream().filter(c -> c.isActive())
      .collect(Collectors.toList())) {

      projectLocations.add(projectLocation);
    }
    List<ProjectCenterOutcome> projectCenterOutcomes = new ArrayList<>();
    for (ProjectCenterOutcome projectCenterOutcome : project.getProjectCenterOutcomes().stream()
      .filter(c -> c.isActive() && c.getPhase() != null && c.getPhase().equals(action.getActualPhase()))
      .collect(Collectors.toList())) {
      projectCenterOutcomes.add(projectCenterOutcome);
    }
    project.setCenterOutcomes(projectCenterOutcomes);
    project.setClusterActivities(projectClusterActivities);
    project.setFlagships(programs);
    project.setRegions(regions);
    project.setScopes(projectLocations);

    descriptionValidator.validate(action, project, false);
  }

  public void validateProjectExpectedStudies(BaseAction action, Long projectID) {
    // Getting the project information.
    Project project = projectManager.getProjectById(projectID);

    List<ProjectExpectedStudy> allProjectStudies = new ArrayList<ProjectExpectedStudy>();

    // Load Studies
    List<ProjectExpectedStudy> studies = project.getProjectExpectedStudies().stream()
      .filter(c -> c.isActive() && c.getProjectExpectedStudyInfo(action.getActualPhase()) != null)
      .collect(Collectors.toList());
    if (studies != null && studies.size() > 0) {
      allProjectStudies.addAll(studies);
    }

    List<ProjectExpectedStudy> projectStudies = new ArrayList<ProjectExpectedStudy>();

    if (allProjectStudies != null && allProjectStudies.size() > 0) {
      // Editable project studies: Current cycle year-1 will be editable except Complete and Cancelled.
      // Every study of the current cycle year will be editable
      projectStudies = allProjectStudies.stream()
        .filter(ps -> ps.getProjectExpectedStudyInfo().getYear() != null
          && ps.getProjectExpectedStudyInfo().getStatus() != null
          && ps.getProjectExpectedStudyInfo().getYear() >= action.getCurrentCycleYear())
        .collect(Collectors.toList());
    }


    Phase phase = action.getActualPhase();
    for (ProjectExpectedStudy expectedStudy : projectStudies) {

      if (expectedStudy.getProjectExpectedStudyInfo() == null) {
        expectedStudy.getProjectExpectedStudyInfo(phase);
      }

      // Setup Geographic Scope
      if (expectedStudy.getProjectExpectedStudyGeographicScopes() != null) {
        expectedStudy
          .setGeographicScopes(new ArrayList<>(expectedStudy.getProjectExpectedStudyGeographicScopes().stream()
            .filter(o -> o.isActive() && o.getPhase().getId().equals(phase.getId())).collect(Collectors.toList())));
      }

      // Expected Study Countries List
      if (expectedStudy.getProjectExpectedStudyCountries() == null) {
        expectedStudy.setCountries(new ArrayList<>());
      } else {
        List<ProjectExpectedStudyCountry> countries = this.projectExpectedStudyCountryManager
          .getProjectExpectedStudyCountrybyPhase(expectedStudy.getId(), phase.getId()).stream()
          .filter(le -> le.isActive() && le.getLocElement().getLocElementType().getId() == 2)
          .collect(Collectors.toList());
        expectedStudy.setCountries(countries);
      }

      if (expectedStudy.getProjectExpectedStudyRegions() == null) {
        expectedStudy.setStudyRegions(new ArrayList<>());
      } else {
        List<ProjectExpectedStudyRegion> geographics = this.projectExpectedStudyRegionManager
          .getProjectExpectedStudyRegionbyPhase(expectedStudy.getId(), phase.getId());

        // Load Regions
        expectedStudy.setStudyRegions(geographics.stream()
          .filter(sc -> sc.getLocElement().getLocElementType().getId() == 1).collect(Collectors.toList()));
      }

      // Expected Study SubIdos List
      if (expectedStudy.getProjectExpectedStudySubIdos() != null) {
        expectedStudy.setSubIdos(new ArrayList<>(expectedStudy.getProjectExpectedStudySubIdos().stream()
          .filter(o -> o.isActive() && o.getPhase().getId().equals(phase.getId())).collect(Collectors.toList())));
      }

      // Expected Study Flagship List
      if (expectedStudy.getProjectExpectedStudyFlagships() != null) {
        expectedStudy.setFlagships(new ArrayList<>(expectedStudy.getProjectExpectedStudyFlagships().stream()
          .filter(o -> o.isActive() && o.getPhase().getId().equals(phase.getId())
            && o.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue())
          .collect(Collectors.toList())));
      }

      // Expected Study Regions List
      if (expectedStudy.getProjectExpectedStudyFlagships() != null) {
        expectedStudy.setRegions(new ArrayList<>(expectedStudy.getProjectExpectedStudyFlagships().stream()
          .filter(o -> o.isActive() && o.getPhase().getId().equals(phase.getId())
            && o.getCrpProgram().getProgramType() == ProgramType.REGIONAL_PROGRAM_TYPE.getValue())
          .collect(Collectors.toList())));
      }

      // Expected Study Crp List
      if (expectedStudy.getProjectExpectedStudyCrps() != null) {
        expectedStudy.setCrps(new ArrayList<>(expectedStudy.getProjectExpectedStudyCrps().stream()
          .filter(o -> o.isActive() && o.getPhase().getId().equals(phase.getId())).collect(Collectors.toList())));
      }

      // Expected Study Institutions List
      if (expectedStudy.getProjectExpectedStudyInstitutions() != null) {
        expectedStudy.setInstitutions(new ArrayList<>(expectedStudy.getProjectExpectedStudyInstitutions().stream()
          .filter(o -> o.isActive() && o.getPhase().getId().equals(phase.getId())).collect(Collectors.toList())));
      }

      // Expected Study Srf Target List
      if (expectedStudy.getProjectExpectedStudySrfTargets() != null) {
        expectedStudy.setSrfTargets(new ArrayList<>(expectedStudy.getProjectExpectedStudySrfTargets().stream()
          .filter(o -> o.isActive() && o.getPhase().getId().equals(phase.getId())).collect(Collectors.toList())));
      }

      // Expected Study Projects List
      if (expectedStudy.getExpectedStudyProjects() != null) {
        expectedStudy.setProjects(new ArrayList<>(expectedStudy.getExpectedStudyProjects().stream()
          .filter(o -> o.isActive() && o.getPhase().getId().equals(phase.getId())).collect(Collectors.toList())));
      }

      // Expected Study Link List
      if (expectedStudy.getProjectExpectedStudyLinks() != null) {
        expectedStudy.setLinks(new ArrayList<>(expectedStudy.getProjectExpectedStudyLinks().stream()
          .filter(o -> o.isActive() && o.getPhase().getId().equals(phase.getId())).collect(Collectors.toList())));
      }

      // Expected Study Policies List
      if (expectedStudy.getProjectExpectedStudyPolicies() != null) {
        expectedStudy.setPolicies(new ArrayList<>(expectedStudy.getProjectExpectedStudyPolicies().stream()
          .filter(o -> o.isActive() && o.getPhase().getId().equals(phase.getId())).collect(Collectors.toList())));
      }

      // Expected Study Quantifications List
      if (expectedStudy.getProjectExpectedStudyQuantifications() != null) {
        expectedStudy.setQuantifications(new ArrayList<>(expectedStudy.getProjectExpectedStudyQuantifications().stream()
          .filter(o -> o.isActive() && o.getPhase().getId().equals(phase.getId())).collect(Collectors.toList())));
      }

      // Expected Study Quantifications List
      if (expectedStudy.getProjectExpectedStudyInnovations() != null) {
        expectedStudy.setInnovations(new ArrayList<>(expectedStudy.getProjectExpectedStudyInnovations().stream()
          .filter(o -> o.isActive() && o.getPhase().getId().equals(phase.getId())).collect(Collectors.toList())));
      }

      if (expectedStudy.getCountries() != null) {
        for (ProjectExpectedStudyCountry country : expectedStudy.getCountries()) {
          expectedStudy.getCountriesIds().add(country.getLocElement().getIsoAlpha2());
        }
      }

      projectExpectedStudiesValidator.validate(action, project, expectedStudy, false);
    }


  }

  public void validateProjectLocations(BaseAction action, Long projectID) {
    // Getting the project information.
    // Getting the project information.
    Project project = projectManager.getProjectById(projectID);
    project.setLocationsData(new ArrayList<>(this.getProjectLocationsData(project, action)));
    this.prepareFundingList(project, action);
    if (project.getRegionFS() != null) {
      for (CountryFundingSources locElement : project.getRegionFS()) {


        if (locElement.getLocElement() != null) {
          locElement.setSelected(this.locElementSelected(locElement.getLocElement().getId(), project.getId(), action));
        } else {
          locElement
            .setSelected(this.locElementTypeSelected(locElement.getLocElementType().getId(), project.getId(), action));
        }

      }
    }
    if (project.getCountryFS() != null) {
      for (CountryFundingSources locElement : project.getCountryFS()) {


        if (locElement.getLocElement() != null) {
          locElement.setSelected(this.locElementSelected(locElement.getLocElement().getId(), project.getId(), action));
        } else {
          locElement
            .setSelected(this.locElementTypeSelected(locElement.getLocElementType().getId(), project.getId(), action));
        }

      }
    }
    locationValidator.validate(action, project, false);
  }

  public void validateProjectOutcomes(BaseAction action, Long projectID) {
    // Getting the project information.
    Project project = projectManager.getProjectById(projectID);

    List<ProjectOutcome> projectOutcomes = project.getProjectOutcomes().stream()
      .filter(c -> c.isActive() && c.getPhase().equals(action.getActualPhase())).collect(Collectors.toList());


    project.setOutcomes(projectOutcomes);
    for (ProjectOutcome projectOutcome : project.getOutcomes()) {
      projectOutcome.setMilestones(
        projectOutcome.getProjectMilestones().stream().filter(c -> c.isActive()).collect(Collectors.toList()));
      if (action.hasSpecificities(APConstants.CRP_SHOW_PROJECT_OUTCOME_COMMUNICATIONS)) {
        projectOutcome.setCommunications(
          projectOutcome.getProjectCommunications().stream().filter(c -> c.isActive()).collect(Collectors.toList()));
      }
      projectOutcome.setNextUsers(
        projectOutcome.getProjectNextusers().stream().filter(c -> c.isActive()).collect(Collectors.toList()));
      projectOutcome.setIndicators(
        projectOutcome.getProjectOutcomeIndicators().stream().filter(c -> c.isActive()).collect(Collectors.toList()));

      List<ProjectComponentLesson> projectComponentLessons = projectOutcome.getProjectComponentLessons().stream()
        .filter(c -> c.isActive() && c.getYear() == action.getCurrentCycleYear()
          && c.getCycle().equals(action.getCurrentCycle()))
        .collect(Collectors.toList());
      if (projectComponentLessons != null && projectComponentLessons.size() > 0) {
        projectOutcome.setProjectComponentLesson(projectComponentLessons.get(0));
      }

      projectOutcomeValidator.validate(action, projectOutcome, false);

    }


  }

  public void validateProjectParnters(BaseAction action, Long projectID, GlobalUnit crp) {
    Project project = projectManager.getProjectById(projectID);
    project.setPartners(project.getProjectPartners().stream()
      .filter(c -> c.isActive() && c.getPhase().equals(action.getActualPhase())).collect(Collectors.toList()));
    for (ProjectPartner projectPartner : project.getPartners()) {
      List<ProjectPartnerContribution> contributors = new ArrayList<>();

      List<ProjectPartnerContribution> partnerContributions =
        projectPartner.getProjectPartnerContributions().stream().filter(c -> c.isActive()).collect(Collectors.toList());
      for (ProjectPartnerContribution projectPartnerContribution : partnerContributions) {
        projectPartner.getPartnerContributors().add(projectPartnerContribution);
      }
      List<ProjectPartnerPartnership> partnerPartnerships =
        projectPartner.getProjectPartnerPartnerships().stream().filter(c -> c.isActive()).collect(Collectors.toList());
      if (partnerPartnerships.size() > 0) {
        projectPartner.setProjectPartnerPartnership(partnerPartnerships.get(0));

        // Partnership Locations
        List<ProjectPartnerPartnershipLocation> partnerPartnershipLocations =
          projectPartner.getProjectPartnerPartnership().getProjectPartnerPartnershipLocations().stream()
            .filter(p -> p.isActive()).collect(Collectors.toList());
        for (ProjectPartnerPartnershipLocation projectPartnerPartnershipLocation : partnerPartnershipLocations) {
          projectPartner.getProjectPartnerPartnership().getPartnershipLocationsIsos()
            .add(projectPartnerPartnershipLocation.getLocation().getIsoAlpha2());
        }

        // Partnership Research Phases
        List<ProjectPartnerPartnershipResearchPhase> partnershipResearchPhases =
          projectPartner.getProjectPartnerPartnership().getProjectPartnerPartnershipResearchPhases().stream()
            .filter(rf -> rf.isActive()).collect(Collectors.toList());
        for (ProjectPartnerPartnershipResearchPhase partnershipResearchPhase : partnershipResearchPhases) {
          projectPartner.getProjectPartnerPartnership().getResearchPhasesIds()
            .add(partnershipResearchPhase.getRepIndPhaseResearchPartnership().getId());
        }

      }

      projectPartner.setPartnerPersons(
        projectPartner.getProjectPartnerPersons().stream().filter(c -> c.isActive()).collect(Collectors.toList()));
      projectPartner.setSelectedLocations(new ArrayList<>());
      for (ProjectPartnerLocation projectPartnerLocation : projectPartner.getProjectPartnerLocations().stream()
        .filter(c -> c.isActive()).collect(Collectors.toList())) {
        projectPartner.getSelectedLocations().add(projectPartnerLocation.getInstitutionLocation());
      }

    }
    if (action.isLessonsActive()) {
      action.loadLessons(crp, project, ProjectSectionStatusEnum.PARTNERS.getStatus());
    }


    projectPartnerValidator.validate(action, project, false);

  }


}
