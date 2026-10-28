/*****************************************************************
 * \ * This file is part of Managing Agricultural Research for Learning &
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

package org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.projectPage;

import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.DeliverableInfoManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.LocElementManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectLocationManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.model.Activity;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.CustomParameter;
import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.data.model.DeliverableDissemination;
import org.cgiar.ccafs.marlo.data.model.DeliverableInfo;
import org.cgiar.ccafs.marlo.data.model.GeneralStatus;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.InstitutionLocation;
import org.cgiar.ccafs.marlo.data.model.LocElement;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProgramType;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudy;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyInfo;
import org.cgiar.ccafs.marlo.data.model.ProjectFocus;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovation;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationInfo;
import org.cgiar.ccafs.marlo.data.model.ProjectLocation;
import org.cgiar.ccafs.marlo.data.model.ProjectMilestone;
import org.cgiar.ccafs.marlo.data.model.ProjectOutcome;
import org.cgiar.ccafs.marlo.data.model.ProjectPartner;
import org.cgiar.ccafs.marlo.data.model.ProjectPartnerLocation;
import org.cgiar.ccafs.marlo.data.model.ProjectPartnerPerson;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicy;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicyInfo;
import org.cgiar.ccafs.marlo.rest.dto.ProjectPageDTO;
import org.cgiar.ccafs.marlo.rest.errors.FieldErrorDTO;
import org.cgiar.ccafs.marlo.rest.errors.MARLOFieldValidationException;
import org.cgiar.ccafs.marlo.rest.mappers.ProjectPageMapper;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * @author Luis Benavides - CIAT/CCAFS
 */

@Named
public class ProjectPageItem<T> {

  private static final String STRING_TRUE = "true";
  private ProjectManager projectManager;
  private PhaseManager phaseManager;
  private GlobalUnitManager globalUnitManager;
  private ProjectLocationManager projectLocationManager;
  private LocElementManager locElementManager;
  private ProjectPageMapper projectPageMapper;
  protected APConfig config;
  private DeliverableInfoManager deliverableInfoManager;


  @Inject
  public ProjectPageItem(ProjectManager projectManager, PhaseManager phaseManager, GlobalUnitManager globalUnitManager,
    ProjectLocationManager projectLocationManager, LocElementManager locElementManager,
    DeliverableInfoManager deliverableInfoManager, ProjectPageMapper projectPageMapper, APConfig config) {
    this.projectManager = projectManager;
    this.phaseManager = phaseManager;
    this.globalUnitManager = globalUnitManager;
    this.projectPageMapper = projectPageMapper;
    this.projectLocationManager = projectLocationManager;
    this.locElementManager = locElementManager;
    this.deliverableInfoManager = deliverableInfoManager;
    this.config = config;
  }

  public List<ProjectPageDTO> findAllProjectPage(String globalUnitAcronym) {
    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();
    List<ProjectPageDTO> ppList = new ArrayList<ProjectPageDTO>();
    GlobalUnit globalUnit = globalUnitManager.findGlobalUnitByAcronym(globalUnitAcronym);
    List<Project> projectList = new ArrayList<Project>();
    if (globalUnit == null) {
      fieldErrors.add(new FieldErrorDTO("ProjectPageDTO", "GlobalUnitEntity", "Invalid CGIAR entity acronym"));
    } else {
      // is project web page expose enable by CRP parameter
      if (this.isProjectPageEnabled(globalUnit)) {

        for (Project project : projectManager.getProjectWebPageList(globalUnit.getId())) {
          project = (project != null) ? this.getProjectInformation(project, globalUnit) : null;
          projectList.add(project);
        }
      }
    }

    if (!fieldErrors.isEmpty()) {
      throw new MARLOFieldValidationException("Field Validation errors", "", fieldErrors);
    }
    ppList = projectList.stream().map(projects -> this.projectPageMapper.projectToProjectPageDTO(projects))
      .collect(Collectors.toList());
    return ppList;
  }

  /**
   * find a project requesting by Id
   * 
   * @param id
   * @return
   */
  public ResponseEntity<ProjectPageDTO> findProjectPageById(Long id, String globalUnitAcronym) {

    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();

    Project project = projectManager.getProjectById(id);

    if (project == null) {
      fieldErrors.add(new FieldErrorDTO("ProjectPageDTO", "Project", "Invalid Project code"));
    }

    GlobalUnit globalUnit = globalUnitManager.findGlobalUnitByAcronym(globalUnitAcronym);

    if (globalUnit == null) {
      fieldErrors.add(new FieldErrorDTO("ProjectPageDTO", "GlobalUnitEntity", "Invalid CGIAR entity acronym"));
    } else {
      if (this.isProjectPageEnabled(globalUnit)) {
        project = (project != null) ? this.getProjectInformation(project, globalUnit) : null;
      } else {
        fieldErrors.add(new FieldErrorDTO("ProjectPageDTO", "GlobalUnitEntity", "CGIAR entity not autorized"));
      }
    }

    if (!fieldErrors.isEmpty()) {
      throw new MARLOFieldValidationException("Field Validation errors", "", fieldErrors);
    }

    return Optional.ofNullable(project).map(this.projectPageMapper::projectToProjectPageDTO)
      .map(result -> new ResponseEntity<>(result, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  /**
   * for this method get the project information
   * 
   * @param project
   * @return
   */
  public Project getProjectInformation(Project project, GlobalUnit globalUnit) {
    int projectPageYear = this.getProjectPageYear(globalUnit);

    //

    Phase phase = phaseManager.findCycle("Planning", projectPageYear, true, globalUnit.getId().longValue());
    if (phase != null) {
      // Get the Project Info
      project.getProjecInfoPhase(phase);


      // Get the Flagship and Programs Regions that belongs in the Project
      List<CrpProgram> programs = new ArrayList<>();
      for (ProjectFocus projectFocuses : project.getProjectFocuses().stream()
        .filter(c -> c.isActive() && c.getPhase() != null && c.getPhase().getId().equals(phase.getId())
          && c.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue()
          && c.getCrpProgram().getCrp() != null && c.getCrpProgram().getCrp().getId().equals(globalUnit.getId()))
        .collect(Collectors.toList())) {
        programs.add(projectFocuses.getCrpProgram());
      }

      List<CrpProgram> regions = new ArrayList<>();

      for (ProjectFocus projectFocuses : project.getProjectFocuses().stream()
        .filter(c -> c.isActive() && c.getPhase() != null && c.getPhase().getId().equals(phase.getId())
          && c.getCrpProgram().getProgramType() == ProgramType.REGIONAL_PROGRAM_TYPE.getValue()
          && c.getCrpProgram().getCrp() != null && c.getCrpProgram().getCrp().getId().equals(globalUnit.getId()))
        .collect(Collectors.toList())) {
        regions.add(projectFocuses.getCrpProgram());
      }
      final long projectID = project.getId();
      List<ProjectLocation> projectRegions = new ArrayList<ProjectLocation>();
      List<ProjectLocation> projectCountries = new ArrayList<ProjectLocation>();
      for (ProjectLocation projectLocations : projectLocationManager
        .findAll().stream().filter(c -> c.isActive() && c.getPhase() != null
          && c.getPhase().getId().equals(phase.getId()) && c.getProject().getId().longValue() == projectID)
        .collect(Collectors.toList())) {
        LocElement loc = projectLocations.getLocElement() != null
          ? locElementManager.getLocElementById(projectLocations.getLocElement().getId()) : null;
        if (loc != null && loc.getLocElementType() != null) {
          projectLocations.setLocElement(loc);
          if (loc.getLocElementType().getId().longValue() == 1) {
            projectRegions.add(projectLocations);
          } else if (loc.getLocElementType().getId().longValue() == 2) {
            projectCountries.add(projectLocations);
          }
        }
      }

      // activities ongoing or complete
      List<Activity> activitiesData = project.getActivities().stream()
        .filter(a -> a.isActive() && phase.equals(a.getPhase()) && a.getActivityStatus() != null
          && (a.getActivityStatus().longValue() == 3 || a.getActivityStatus().longValue() == 2))
        .collect(Collectors.toList());

      List<Activity> activities = new ArrayList<Activity>();
      for (Activity activity : activitiesData) {
        if (activity.getActivityStatus().longValue() == 3) {
          activity.setStatusName("Completed");
        } else {
          activity.setStatusName("On Going");
        }
        activities.add(activity);
      }

      // Deliverables complete

      List<Deliverable> deliverables = new ArrayList<Deliverable>();
      for (Deliverable deliverable : project.getDeliverables().stream().filter(c -> c.isActive())
        .collect(Collectors.toList())) {
        DeliverableInfo deliverableInfo = deliverable.getDeliverableInfo(phase);
        if (deliverableInfo != null && deliverableInfo.getStatus() != null
          && deliverableInfo.getStatus().longValue() == 3) {
          deliverable.setDeliverableInfo(deliverableInfo);
          DeliverableDissemination deliverableDissemination = deliverable.getDissemination(phase);
          // deliverable external link not provided removed when this is null
          deliverable.setDissemination(deliverableDissemination);
          deliverable.setIsFindable(this.isF(deliverable));
          deliverable.setIsAccesible(this.isA(deliverable));
          deliverable.setIsInteroperable(this.isI(deliverable));
          deliverable.setIsReusable(this.isR(deliverable));
          if (deliverableInfoManager.isDeliverableSubcategoryIncludedWebsite(deliverable.getId().longValue(), phase)) {
            deliverables.add(deliverable);
          }

        }
      }

      // Policies
      List<ProjectPolicy> policies = new ArrayList<ProjectPolicy>();
      for (ProjectPolicy policy : project.getProjectPolicies().stream().filter(c -> c.isActive())
        .collect(Collectors.toList())) {
        ProjectPolicyInfo projectPolicyInfo = policy.getProjectPolicyInfo(phase);
        if (projectPolicyInfo != null) {
          policy.setProjectPolicyInfo(projectPolicyInfo);
          policies.add(policy);
        }
      }

      // innovations
      List<ProjectInnovation> innovations = new ArrayList<ProjectInnovation>();
      for (ProjectInnovation innovation : project.getProjectInnovations().stream().filter(c -> c.isActive())
        .collect(Collectors.toList())) {
        ProjectInnovationInfo projectInnovationInfo = innovation.getProjectInnovationInfo(phase);
        if (projectInnovationInfo != null) {

          innovation.setProjectInnovationInfo(projectInnovationInfo);
          String pdflink = config.getClarisa_summaries_pdf() + "summaries/" + globalUnit.getAcronym()
            + "/projectInnovationSummary.do?innovationID=" + innovation.getId().longValue() + "&phaseID="
            + phase.getId().longValue();
          innovation.setPdfLink(pdflink);
          innovations.add(innovation);
        }
      }

      // contributing outcomes
      List<ProjectOutcome> projectOutcomes = new ArrayList<ProjectOutcome>();
      for (ProjectOutcome projectOutcome : project.getProjectOutcomes().stream()
        .filter(c -> c.isActive() && c.getPhase() != null && c.getPhase().getId().equals(phase.getId()))
        .collect(Collectors.toList())) {
        List<ProjectMilestone> milestones = new ArrayList<ProjectMilestone>();
        milestones = projectOutcome.getProjectMilestones().stream().filter(c -> c != null && c.isActive())
          .collect(Collectors.toList());
        projectOutcome.setMilestones(milestones);
        projectOutcomes.add(projectOutcome);
      }


      // project partners
      List<ProjectPartner> partners = new ArrayList<ProjectPartner>();
      for (ProjectPartner projectPartner : project.getProjectPartners().stream()
        .filter(c -> c != null && c.isActive() && c.getPhase() != null && c.getPhase().getId().equals(phase.getId()))
        .collect(Collectors.toList())) {
        // search partner office location
        List<InstitutionLocation> locations = new ArrayList<InstitutionLocation>();
        for (ProjectPartnerLocation partnersLocation : projectPartner.getProjectPartnerLocations().stream()
          .filter(c -> c.isActive()).collect(Collectors.toList())) {
          locations.add(partnersLocation.getInstitutionLocation());
        }
        projectPartner.setSelectedLocations(locations);
        // search if this partner has projectLeader
        List<ProjectPartnerPerson> projectPartnerPersons = new ArrayList<ProjectPartnerPerson>();
        for (ProjectPartnerPerson projectPartnerPerson : projectPartner.getProjectPartnerPersons().stream()
          .filter(c -> c.isActive()).collect(Collectors.toList())) {
          if (projectPartnerPerson.getContactType().equals("PL")) {
            projectPartnerPersons.add(projectPartnerPerson);
          }
        }
        // add Project leader if exist
        projectPartner.setPartnerLeader(projectPartnerPersons.size() == 0 ? null : projectPartnerPersons.get(0));
        partners.add(projectPartner);
      }
      /*
       * Comparator<ProjectPartner> partnerComparator =
       * (ProjectPartner o1, ProjectPartner o2) -> (o1.getInstitution().isPPA(phase.getCrp().getId().longValue(),
       * phase) == o2.getInstitution().isPPA(phase.getCrp().getId().longValue(), phase)) ? 0 : 1;
       */
      Collections.sort(partners, new Comparator<ProjectPartner>() {

        @Override
        public int compare(ProjectPartner o1, ProjectPartner o2) {
          Boolean a1 = o1.getInstitution().isPPA(o1.getPhase().getCrp().getId(), phase);
          Boolean a2 = o2.getInstitution().isPPA(o1.getPhase().getCrp().getId(), phase);

          return (a1 ^ a2) ? ((a1 ^ true) ? 1 : -1) : 0;
        }

      });
      // Collections.reverse(partners);

      // Outcome impact case reports
      List<ProjectExpectedStudy> outcomeImpactCaseReports = new ArrayList<ProjectExpectedStudy>();
      for (ProjectExpectedStudy projectExpectedStudy : project.getProjectExpectedStudies().stream()
        .filter(c -> c.isActive()).collect(Collectors.toList())) {
        ProjectExpectedStudyInfo projectExpectedStudyInfo = projectExpectedStudy.getProjectExpectedStudyInfo(phase);
        if (projectExpectedStudyInfo != null && projectExpectedStudyInfo.isActive()) {
          // only view ongoing or completed
          GeneralStatus studyStatus = projectExpectedStudyInfo.getStatus();
          if (studyStatus != null && (studyStatus.getId().longValue() == 3 || studyStatus.getId().longValue() == 2)) {
            if (projectExpectedStudyInfo.getStudyType() != null
              && projectExpectedStudyInfo.getStudyType().getId().longValue() == 1) {
              projectExpectedStudy.setProjectExpectedStudyInfo(projectExpectedStudyInfo);
              if (projectExpectedStudyInfo.getIsPublic() != null && projectExpectedStudyInfo.getIsPublic()) {
                String pdflink = config.getClarisa_summaries_pdf() + "projects/" + globalUnit.getAcronym()
                  + "/studySummary.do?studyID=" + projectExpectedStudy.getId().longValue() + "&cycle="
                  + phase.getDescription() + "&year=" + phase.getYear();
                projectExpectedStudy.setPdfLink(pdflink);
              } else {
                projectExpectedStudy.setPdfLink(null);
              }
              outcomeImpactCaseReports.add(projectExpectedStudy);
            }
          }

        }
      }


      project.setRegions(regions);
      project.setFlagships(programs);
      project.setProjectRegions(projectRegions);
      project.setLocations(projectCountries);
      project.setProjectActivities(activities);
      project.setProjectDeliverables(deliverables);
      project.setPolicies(policies);
      project.setInnovations(innovations);
      project.setOutcomes(projectOutcomes);
      project.setPartners(partners);
      project.setExpectedStudies(outcomeImpactCaseReports);
    } else {
      project = null;
    }


    return project;
  }

  /**
   * The year whose Planning phase the project page shows: the value of the Global Unit's project_website_year_value
   * parameter. A Global Unit without the parameter, or whose value is empty or not a number, has no project page year.
   * That is a configuration error of the Global Unit, not a client error and not "no data", so it is raised as an
   * exception that ExceptionTranslator answers 500 and logs once.
   * 
   * @param globalUnit the Global Unit that publishes the project page
   * @return the configured year
   * @throws IllegalStateException when the Global Unit has no usable project page year
   */
  private int getProjectPageYear(GlobalUnit globalUnit) {
    CustomParameter param = globalUnit.getCustomParameters().stream()
      .filter(c -> c.getParameter() != null
        && APConstants.CRP_PROJECT_PAGE_YEAR.equalsIgnoreCase(c.getParameter().getKey()))
      .findFirst().orElse(null);
    if (param == null || param.getValue() == null) {
      throw this.unusableProjectPageYear(globalUnit, null);
    }
    try {
      return Integer.parseInt(param.getValue());
    } catch (NumberFormatException e) {
      throw this.unusableProjectPageYear(globalUnit, e);
    }
  }

  private Boolean isA(Deliverable deliverableBD) {
    try {
      if (deliverableBD.getDissemination().getIsOpenAccess() != null
        && deliverableBD.getDissemination().getIsOpenAccess().booleanValue()) {
        return true;
      }

      if (deliverableBD.getDissemination().getIsOpenAccess() == null) {
        return null;
      }
      return false;
    } catch (Exception e) {
      return null;
    }
  }

  private Boolean isF(Deliverable deliverableBD) {
    try {
      if (deliverableBD.getDissemination().getAlreadyDisseminated() != null) {
        if (deliverableBD.getDissemination().getAlreadyDisseminated().booleanValue()) {
          if (deliverableBD.getDissemination().getDisseminationChannel() != null) {
            if (deliverableBD.getDissemination().getDisseminationChannel().equals("other")) {
              if (deliverableBD.getDissemination().getDisseminationUrl() != null
                && !deliverableBD.getDissemination().getDisseminationUrl().trim().isEmpty()) {
                return true;
              }
            } else {
              if (deliverableBD.getDissemination().getSynced() != null
                && deliverableBD.getDissemination().getSynced()) {
                return true;
              }
            }
          }
        } else {
          return false;
        }
      } else {
        return null;
      }
      return null;
    } catch (Exception e) {
      return null;
    }
  }

  private Boolean isI(Deliverable deliverableBD) {
    try {
      if (deliverableBD.getDissemination().getAlreadyDisseminated() != null
        && deliverableBD.getDissemination().getAlreadyDisseminated().booleanValue()) {
        String channel = deliverableBD.getDissemination().getDisseminationChannel();
        String link = deliverableBD.getDissemination().getDisseminationUrl().replaceAll(" ", "%20");;
        if (channel == null || channel.equals("-1")) {
          return null;
        }
        if (link == null || link.equals("-1") || link.isEmpty()) {
          return null;
        }

        // If the deliverable is synced
        if (deliverableBD.getDissemination().getSynced() != null
          && deliverableBD.getDissemination().getSynced().booleanValue()) {
          return true;
        }
        return null;
      }
      if (deliverableBD.getDissemination().getAlreadyDisseminated() == null) {
        return null;
      }
    } catch (Exception e) {
      return null;
    }
    return null;
  }

  /**
   * A Global Unit publishes its project pages unless its crp_project_page parameter says otherwise: without the
   * parameter the project pages are enabled, and with it every value must be "true".
   * 
   * @param globalUnit the Global Unit that is asked for its project pages
   * @return true when the project pages are enabled
   */
  private boolean isProjectPageEnabled(GlobalUnit globalUnit) {
    return globalUnit.getCustomParameters().stream()
      .filter(c -> c.getParameter() != null && APConstants.CRP_PROJECT_PAGE.equalsIgnoreCase(c.getParameter().getKey()))
      .allMatch(t -> t.getValue() != null && t.getValue().equalsIgnoreCase(STRING_TRUE));
  }

  public Boolean isR(Deliverable deliverableBD) {
    try {

      if (deliverableBD.getDeliverableInfo().getAdoptedLicense() == null) {
        return null;
      }
      if (deliverableBD.getDeliverableInfo().getAdoptedLicense()) {
        return true;
      }
      return false;
    } catch (Exception e) {
      return false;
    }
  }

  /**
   * The configuration error of a Global Unit whose project page year is missing or is not a number. The message holds
   * database values only (the Global Unit acronym and the parameter key), never a value the client sent; the cause
   * keeps the configured value for the log, which is the only place it reaches.
   * 
   * @param globalUnit the Global Unit whose project page year is not usable
   * @param cause why the configured value could not be read, null when there is no value
   * @return the exception to throw
   */
  private IllegalStateException unusableProjectPageYear(GlobalUnit globalUnit, NumberFormatException cause) {
    return new IllegalStateException("The Global Unit " + globalUnit.getAcronym()
      + " has no usable project page year: the custom parameter " + APConstants.CRP_PROJECT_PAGE_YEAR
      + " is missing or its value is not a number", cause);
  }

}
