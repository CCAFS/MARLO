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
import org.cgiar.ccafs.marlo.data.manager.DeliverableQualityCheckManager;
import org.cgiar.ccafs.marlo.data.manager.LocElementTypeManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectLocationElementTypeManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.model.Activity;
import org.cgiar.ccafs.marlo.data.model.CaseStudyIndicator;
import org.cgiar.ccafs.marlo.data.model.CaseStudyProject;
import org.cgiar.ccafs.marlo.data.model.CountryFundingSources;
import org.cgiar.ccafs.marlo.data.model.Crp;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.data.model.DeliverableActivity;
import org.cgiar.ccafs.marlo.data.model.DeliverableDataSharingFile;
import org.cgiar.ccafs.marlo.data.model.DeliverableDissemination;
import org.cgiar.ccafs.marlo.data.model.DeliverableFile;
import org.cgiar.ccafs.marlo.data.model.DeliverablePartnership;
import org.cgiar.ccafs.marlo.data.model.DeliverablePartnershipTypeEnum;
import org.cgiar.ccafs.marlo.data.model.DeliverableQualityCheck;
import org.cgiar.ccafs.marlo.data.model.FundingSource;
import org.cgiar.ccafs.marlo.data.model.FundingSourceLocation;
import org.cgiar.ccafs.marlo.data.model.LocElement;
import org.cgiar.ccafs.marlo.data.model.LocElementType;
import org.cgiar.ccafs.marlo.data.model.ProgramType;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectBudget;
import org.cgiar.ccafs.marlo.data.model.ProjectClusterActivity;
import org.cgiar.ccafs.marlo.data.model.ProjectFocus;
import org.cgiar.ccafs.marlo.data.model.ProjectHighlight;
import org.cgiar.ccafs.marlo.data.model.ProjectHighlightType;
import org.cgiar.ccafs.marlo.data.model.ProjectHighligthsTypeEnum;
import org.cgiar.ccafs.marlo.data.model.ProjectInfo;
import org.cgiar.ccafs.marlo.data.model.ProjectLeverage;
import org.cgiar.ccafs.marlo.data.model.ProjectLocation;
import org.cgiar.ccafs.marlo.data.model.ProjectLocationElementType;
import org.cgiar.ccafs.marlo.data.model.ProjectOutcome;
import org.cgiar.ccafs.marlo.data.model.ProjectPartner;
import org.cgiar.ccafs.marlo.data.model.ProjectPartnerContribution;
import org.cgiar.ccafs.marlo.data.model.ProjectPartnerLocation;
import org.cgiar.ccafs.marlo.data.model.ProjectPartnerOverall;
import org.cgiar.ccafs.marlo.data.model.ProjectScope;
import org.cgiar.ccafs.marlo.data.model.ProjectSectionStatusEnum;
import org.cgiar.ccafs.marlo.data.model.ProjectStatusEnum;
import org.cgiar.ccafs.marlo.utils.CountryLocationLevel;
import org.cgiar.ccafs.marlo.validation.BaseValidator;

import java.util.ArrayList;
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

  private final ProjectBudgetsCoAValidator projectBudgetsCoAValidator;

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


  @Inject
  public ProjectSectionValidator(ProjectManager projectManager, ProjectLocationValidator locationValidator,
    ProjectBudgetsValidator projectBudgetsValidator, DeliverableValidator deliverableValidator,
    ProjectOutcomeValidator projectOutcomeValidator, ProjectBudgetsCoAValidator projectBudgetsCoAValidator,
    LocElementTypeManager locElementTypeManager, ProjectLocationElementTypeManager projectLocationElementTypeManager,
    DeliverableQualityCheckManager deliverableQualityCheckManager, ProjectDescriptionValidator descriptionValidator,
    ProjectPartnersValidator projectPartnerValidator, ProjectActivitiesValidator projectActivitiesValidator,
    ProjectLeverageValidator projectLeverageValidator, ProjectHighLightValidator projectHighLightValidator,
    ProjectCaseStudyValidation projectCaseStudyValidation, ProjectCCAFSOutcomeValidator projectCCAFSOutcomeValidator,
    ProjectOutcomesPandRValidator projectOutcomesPandRValidator,
    ProjectOtherContributionsValidator projectOtherContributionsValidator,
    ProjectOutputsValidator projectOutputsValidator) {
    this.projectManager = projectManager;
    this.locationValidator = locationValidator;
    this.projectBudgetsValidator = projectBudgetsValidator;
    this.deliverableValidator = deliverableValidator;
    this.projectOutcomeValidator = projectOutcomeValidator;
    this.projectBudgetsCoAValidator = projectBudgetsCoAValidator;
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
    List<ProjectLocation> locElements = projectDB.getProjectLocations()
      .stream().filter(c -> c.isActive() && c.getLocElement() != null
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

  public List<DeliverablePartnership> otherPartners(Deliverable deliverable, BaseAction action) {
    try {
      List<DeliverablePartnership> list = deliverable.getDeliverablePartnerships().stream()
        .filter(dp -> dp.getPhase() != null && dp.getPhase() != null && dp.getPhase().equals(action.getActualPhase())
          && dp.isActive() && dp.getPartnerType().equals(DeliverablePartnershipTypeEnum.OTHER.getValue()))
        .collect(Collectors.toList());
      return list;
    } catch (Exception e) {
      return null;
    }
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

  public DeliverablePartnership responsiblePartner(Deliverable deliverable, BaseAction action) {
    try {
      DeliverablePartnership partnership = deliverable.getDeliverablePartnerships().stream()
        .filter(dp -> dp.getPhase() != null && dp.getPhase().equals(action.getActualPhase()) && dp.isActive()
          && dp.getPartnerType().equals(DeliverablePartnershipTypeEnum.RESPONSIBLE.getValue()))
        .collect(Collectors.toList()).get(0);
      return partnership;
    } catch (Exception e) {
      return null;
    }
  }


  public void validateCaseStduies(BaseAction action, Long projectID) {
    // Getting the project information.
    Project project = projectManager.getProjectById(projectID);

    List<CaseStudyProject> caseStudies =
      project.getCaseStudyProjects().stream().filter(d -> d.isActive()).collect(Collectors.toList());

    for (CaseStudyProject caseStudyProject : caseStudies) {
      if (caseStudyProject.isCreated()
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

  public void validateHighlight(BaseAction action, Long projectID) {
    // Getting the project information.
    Project project = projectManager.getProjectById(projectID);

    List<ProjectHighlight> highlights = project.getProjectHighligths().stream()
      .filter(d -> d.isActive() && d.getYear().intValue() == action.getActualPhase().getYear())
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
      .filter(c -> c.isActive() && c.getPhase().equals(action.getActualPhase())).collect(Collectors.toList()));


    projectBudgetsValidator.validate(action, project, false);

  }


  public void validateProjectBudgetsCoAs(BaseAction action, Long projectID) {
    // Getting the project information.
    Project project = projectManager.getProjectById(projectID);
    project.setBudgetsCluserActvities(project.getProjectBudgetsCluserActvities().stream()
      .filter(c -> c.isActive() && c.getPhase().equals(action.getActualPhase())).collect(Collectors.toList()));
    if (!(project.getProjectBudgetsCluserActvities().isEmpty()
      || project.getProjectBudgetsCluserActvities().size() == 1)) {
      projectBudgetsCoAValidator.validate(action, project, false);
    }

  }

  public void validateProjectDeliverables(BaseAction action, Long projectID) {
    // Getting the project information.
    Project project = projectManager.getProjectById(projectID);

    List<Deliverable> deliverables =
      project.getDeliverables().stream().filter(d -> d.isActive()).collect(Collectors.toList());
    List<Deliverable> openA = deliverables.stream()
      .filter(a -> a.isActive() && ((a.getDeliverableInfo(action.getActualPhase()).getStatus() == null
        || (a.getDeliverableInfo(action.getActualPhase()).getStatus() == Integer
          .parseInt(ProjectStatusEnum.Ongoing.getStatusId())
        && a.getDeliverableInfo(action.getActualPhase()).getYear() >= action.getCurrentCycleYear())
      || (a.getDeliverableInfo(action.getActualPhase()).getStatus() == Integer
        .parseInt(ProjectStatusEnum.Extended.getStatusId())
        || a.getDeliverableInfo(action.getActualPhase()).getStatus().intValue() == 0))))
      .collect(Collectors.toList());

    if (action.isReportingActive()) {
      openA.addAll(deliverables.stream()
        .filter(d -> d.isActive()
          && d.getDeliverableInfo(action.getActualPhase()).getYear() == action.getActualPhase().getYear()
          && d.getDeliverableInfo(action.getActualPhase()).getStatus() != null
          && d.getDeliverableInfo(action.getActualPhase()).getStatus().intValue() == Integer
            .parseInt(ProjectStatusEnum.Complete.getStatusId()))
        .collect(Collectors.toList()));
      openA.addAll(deliverables.stream()
        .filter(d -> d.isActive() && d.getDeliverableInfo(action.getActualPhase()).getNewExpectedYear() != null
          && d.getDeliverableInfo(action.getActualPhase()).getNewExpectedYear().intValue() == action
            .getCurrentCycleYear()
          && d.getDeliverableInfo(action.getActualPhase()).getStatus() != null
          && d.getDeliverableInfo(action.getActualPhase()).getStatus().intValue() == Integer
            .parseInt(ProjectStatusEnum.Complete.getStatusId()))
        .collect(Collectors.toList()));
    }

    for (Deliverable deliverable : openA) {

      deliverable.setDeliverableInfo(deliverable.getDeliverableInfo(action.getActualPhase()));
      deliverable.setResponsiblePartner(this.responsiblePartner(deliverable, action));
      deliverable.setOtherPartners(this.otherPartners(deliverable, action));
      deliverable.setGenderLevels(
        deliverable.getDeliverableGenderLevels().stream().filter(c -> c.isActive()).collect(Collectors.toList()));
      deliverable.setFundingSources(
        deliverable.getDeliverableFundingSources().stream().filter(c -> c.isActive()).collect(Collectors.toList()));


      if (action.isReportingActive()) {

        DeliverableQualityCheck deliverableQualityCheck = deliverableQualityCheckManager
          .getDeliverableQualityCheckByDeliverable(deliverable.getId(), action.getActualPhase().getId());
        deliverable.setQualityCheck(deliverableQualityCheck);

        if (deliverable.getDeliverableMetadataElements() != null) {
          deliverable.setMetadataElements(new ArrayList<>(deliverable.getDeliverableMetadataElements()));
        }

        if (deliverable.getDeliverableDisseminations() != null) {
          deliverable.setDisseminations(new ArrayList<>(deliverable.getDeliverableDisseminations()));
          if (deliverable.getDeliverableDisseminations().size() > 0) {
            deliverable.setDissemination(deliverable.getDisseminations().get(0));
          } else {
            deliverable.setDissemination(new DeliverableDissemination());
          }

        }

        if (deliverable.getDeliverableDataSharingFiles() != null) {
          deliverable.setDataSharingFiles(new ArrayList<>(deliverable.getDeliverableDataSharingFiles()));
        }

        if (deliverable.getDeliverablePublicationMetadatas() != null) {
          deliverable.setPublicationMetadatas(new ArrayList<>(deliverable.getDeliverablePublicationMetadatas()));
        }
        if (!deliverable.getPublicationMetadatas().isEmpty()) {
          deliverable.setPublication(deliverable.getPublicationMetadatas().get(0));
        }

        if (deliverable.getDeliverableDataSharings() != null) {
          deliverable.setDataSharing(new ArrayList<>(deliverable.getDeliverableDataSharings()));
        }


        deliverable.setUsers(deliverable.getDeliverableUsers().stream()
          .filter(c -> c.getPhase() != null && c.getPhase().equals(action.getActualPhase()))
          .collect(Collectors.toList()));
        deliverable.setCrps(deliverable.getDeliverableCrps().stream()
          .filter(c -> c.getPhase() != null && c.getPhase().equals(action.getActualPhase()))
          .collect(Collectors.toList()));
        deliverable.setFiles(new ArrayList<>());
        for (DeliverableDataSharingFile dataSharingFile : deliverable.getDeliverableDataSharingFiles()) {

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
      }

      deliverableValidator.validate(action, deliverable, false);
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
    project.setClusterActivities(projectClusterActivities);
    project.setFlagships(programs);
    project.setRegions(regions);
    project.setScopes(projectLocations);

    descriptionValidator.validate(action, project, false);
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

      projectOutcome.setCommunications(
        projectOutcome.getProjectCommunications().stream().filter(c -> c.isActive()).collect(Collectors.toList()));
      projectOutcome.setNextUsers(
        projectOutcome.getProjectNextusers().stream().filter(c -> c.isActive()).collect(Collectors.toList()));
      projectOutcome.setIndicators(
        projectOutcome.getProjectOutcomeIndicators().stream().filter(c -> c.isActive()).collect(Collectors.toList()));
      projectOutcomeValidator.validate(action, projectOutcome, false);

    }


  }

  public void validateProjectParnters(BaseAction action, Long projectID, Crp crp) {
    Project project = projectManager.getProjectById(projectID);
    project.setPartners(project.getProjectPartners().stream()
      .filter(c -> c.isActive() && c.getPhase().equals(action.getActualPhase())).collect(Collectors.toList()));
    for (ProjectPartner projectPartner : project.getPartners()) {
      List<ProjectPartnerContribution> contributors = new ArrayList<>();

      if (action.isReportingActive()) {

        List<ProjectPartnerOverall> overalls = projectPartner.getProjectPartnerOveralls().stream()
          .filter(c -> c.isActive() && c.getYear() == action.getReportingYear()).collect(Collectors.toList());
        if (!overalls.isEmpty()) {
          project.setOverall(overalls.get(0).getOverall());

        }
      }
      List<ProjectPartnerContribution> partnerContributions =
        projectPartner.getProjectPartnerContributions().stream().filter(c -> c.isActive()).collect(Collectors.toList());
      for (ProjectPartnerContribution projectPartnerContribution : partnerContributions) {
        contributors.add(projectPartnerContribution);
      }
      projectPartner.setPartnerContributors(contributors);
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
