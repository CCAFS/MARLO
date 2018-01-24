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

package org.cgiar.ccafs.marlo.action.json.project;


import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.CrpManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableQualityCheckManager;
import org.cgiar.ccafs.marlo.data.manager.LocElementTypeManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectLocationElementTypeManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.SectionStatusManager;
import org.cgiar.ccafs.marlo.data.model.CaseStudy;
import org.cgiar.ccafs.marlo.data.model.CaseStudyProject;
import org.cgiar.ccafs.marlo.data.model.Crp;
import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectHighlight;
import org.cgiar.ccafs.marlo.data.model.ProjectInfo;
import org.cgiar.ccafs.marlo.data.model.ProjectOutcome;
import org.cgiar.ccafs.marlo.data.model.ProjectSectionStatusEnum;
import org.cgiar.ccafs.marlo.data.model.ProjectStatusEnum;
import org.cgiar.ccafs.marlo.data.model.SectionStatus;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.validation.projects.DeliverableValidator;
import org.cgiar.ccafs.marlo.validation.projects.ProjectActivitiesValidator;
import org.cgiar.ccafs.marlo.validation.projects.ProjectBudgetsCoAValidator;
import org.cgiar.ccafs.marlo.validation.projects.ProjectBudgetsValidator;
import org.cgiar.ccafs.marlo.validation.projects.ProjectCCAFSOutcomeValidator;
import org.cgiar.ccafs.marlo.validation.projects.ProjectCaseStudyValidation;
import org.cgiar.ccafs.marlo.validation.projects.ProjectDescriptionValidator;
import org.cgiar.ccafs.marlo.validation.projects.ProjectHighLightValidator;
import org.cgiar.ccafs.marlo.validation.projects.ProjectLeverageValidator;
import org.cgiar.ccafs.marlo.validation.projects.ProjectLocationValidator;
import org.cgiar.ccafs.marlo.validation.projects.ProjectOtherContributionsValidator;
import org.cgiar.ccafs.marlo.validation.projects.ProjectOutcomeValidator;
import org.cgiar.ccafs.marlo.validation.projects.ProjectOutcomesPandRValidator;
import org.cgiar.ccafs.marlo.validation.projects.ProjectOutputsValidator;
import org.cgiar.ccafs.marlo.validation.projects.ProjectPartnersValidator;
import org.cgiar.ccafs.marlo.validation.projects.ProjectSectionValidator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.dispatcher.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class ValidateProjectSectionAction extends BaseAction {


  public static final long serialVersionUID = 2334147747892988744L;


  // Logger
  private static final Logger LOG = LoggerFactory.getLogger(ValidateProjectSectionAction.class);

  // Model
  private boolean existProject;
  private Crp loggedCrp;
  private boolean validSection;

  private String sectionName;
  private Long projectID;
  private SectionStatus sectionStatus;
  private Map<String, Object> section;
  // Managers
  private final SectionStatusManager sectionStatusManager;
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

  private final CrpManager crpManager;
  private final ProjectSectionValidator<ValidateProjectSectionAction> projectSectionValidator;


  @Inject
  public ValidateProjectSectionAction(APConfig config, SectionStatusManager sectionStatusManager,
    ProjectManager projectManager, ProjectLocationValidator locationValidator,
    ProjectBudgetsValidator projectBudgetsValidator, DeliverableValidator deliverableValidator,
    ProjectOutcomeValidator projectOutcomeValidator, ProjectBudgetsCoAValidator projectBudgetsCoAValidator,
    LocElementTypeManager locElementTypeManager, ProjectLocationElementTypeManager projectLocationElementTypeManager,
    DeliverableQualityCheckManager deliverableQualityCheckManager, ProjectDescriptionValidator descriptionValidator,
    ProjectPartnersValidator projectPartnerValidator, ProjectActivitiesValidator projectActivitiesValidator,
    ProjectLeverageValidator projectLeverageValidator, ProjectHighLightValidator projectHighLightValidator,
    ProjectCaseStudyValidation projectCaseStudyValidation, ProjectCCAFSOutcomeValidator projectCCAFSOutcomeValidator,
    ProjectOutcomesPandRValidator projectOutcomesPandRValidator,
    ProjectOtherContributionsValidator projectOtherContributionsValidator,
    ProjectOutputsValidator projectOutputsValidator, CrpManager crpManager,
    ProjectSectionValidator<ValidateProjectSectionAction> projectSectionValidator) {
    super(config);
    this.sectionStatusManager = sectionStatusManager;
    this.projectManager = projectManager;
    this.locationValidator = locationValidator;
    this.projectBudgetsValidator = projectBudgetsValidator;
    this.deliverableValidator = deliverableValidator;
    this.projectOutcomeValidator = projectOutcomeValidator;
    this.projectBudgetsCoAValidator = projectBudgetsCoAValidator;
    this.locElementTypeManager = locElementTypeManager;
    this.projectLocationElementTypeManager = projectLocationElementTypeManager;
    this.projectSectionValidator = projectSectionValidator;
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
    this.crpManager = crpManager;
  }


  @Override
  public String execute() throws Exception {
    Thread.sleep(200);
    if (existProject && validSection) {
      // getting the current section status.
      switch (ProjectSectionStatusEnum.value(sectionName.toUpperCase())) {
        case LOCATIONS:
          this.projectSectionValidator.validateProjectLocations(this, this.getProjectID());
          break;
        case DESCRIPTION:
          this.projectSectionValidator.validateProjectDescription(this, this.getProjectID());
          break;
        case ACTIVITIES:
          this.projectSectionValidator.validateProjectActivities(this, this.getProjectID());
          break;
        case PARTNERS:
          this.projectSectionValidator.validateProjectParnters(this, this.getProjectID(), this.loggedCrp);
        case BUDGET:
          if (this.isPlanningActive()) {
            this.projectSectionValidator.validateProjectBudgets(this, this.getProjectID());
          }

          break;
        case BUDGETBYCOA:
          this.projectSectionValidator.validateProjectBudgetsCoAs(this, this.getProjectID());
          break;
        case DELIVERABLES:
          this.projectSectionValidator.validateProjectDeliverables(this, this.getProjectID());
          break;
        case OUTCOMES:
          this.projectSectionValidator.validateProjectOutcomes(this, this.getProjectID());
          break;
        case LEVERAGES:
          this.projectSectionValidator.validateLeverage(this, this.getProjectID());
          break;

        case HIGHLIGHT:
          this.projectSectionValidator.validateHighlight(this, this.getProjectID());
          break;

        case CASESTUDIES:
          this.projectSectionValidator.validateCaseStduies(this, this.getProjectID());
          break;

        case CCAFSOUTCOMES:
          this.projectSectionValidator.validateCCAFSOutcomes(this, this.getProjectID());
          break;
        case OUTCOMES_PANDR:
          this.projectSectionValidator.validateOutcomesPandR(this, this.getProjectID());
        case OUTPUTS:
          this.projectSectionValidator.validateOutputs(this, this.getProjectID());

        case OTHERCONTRIBUTIONS:
          this.projectSectionValidator.validateOtherContributions(this, this.getProjectID());
          break;
        default:
          break;
      }
    }

    String cycle = "";
    if (this.isPlanningActive()) {
      cycle = APConstants.PLANNING;
    } else {
      cycle = APConstants.REPORTING;
    }

    Project project = projectManager.getProjectById(projectID);
    ProjectInfo projectInfo = project.getProjecInfoPhase(this.getActualPhase());
    switch (ProjectSectionStatusEnum.value(sectionName.toUpperCase())) {
      case OUTCOMES:
        section = new HashMap<String, Object>();
        section.put("sectionName", ProjectSectionStatusEnum.OUTCOMES);
        section.put("missingFields", "");

        List<ProjectOutcome> projectOutcomes = project.getProjectOutcomes().stream()
          .filter(c -> c.isActive() && c.getPhase().equals(this.getActualPhase())).collect(Collectors.toList());


        if (!(projectInfo.getAdministrative() != null && projectInfo.getAdministrative().booleanValue() == true)) {
          if (projectOutcomes.isEmpty()) {
            section.put("missingFields", section.get("missingFields") + "-" + "outcomes");
          }
          project.setOutcomes(projectOutcomes);
          for (ProjectOutcome projectOutcome : project.getOutcomes()) {
            sectionStatus = sectionStatusManager.getSectionStatusByProjectOutcome(projectOutcome.getId(), cycle,
              this.getActualPhase().getYear(), sectionName);
            if (sectionStatus.getMissingFields().length() > 0) {
              section.put("missingFields", section.get("missingFields") + "-" + sectionStatus.getMissingFields());

            }
          }
        } else {
          section.put("missingFields", "");
        }


        break;

      case DELIVERABLES:
        section = new HashMap<String, Object>();
        section.put("sectionName", ProjectSectionStatusEnum.DELIVERABLES);
        section.put("missingFields", "");

        if (project.getDeliverables().stream().filter(d -> d.isActive()).collect(Collectors.toList()).isEmpty()) {
          section.put("missingFields", section.get("missingFields") + "-" + "deliveralbes");
        }

        List<Deliverable> deliverables =
          project.getDeliverables().stream().filter(d -> d.isActive()).collect(Collectors.toList());
        List<Deliverable> openA = deliverables.stream().filter(a -> a.isActive()

          && ((a.getDeliverableInfo(this.getActualPhase()).getStatus() == null
            || (a.getDeliverableInfo(this.getActualPhase()).getStatus() == Integer
              .parseInt(ProjectStatusEnum.Ongoing.getStatusId())
              && a.getDeliverableInfo(this.getActualPhase()).getYear() >= this.getActualPhase().getYear())
            || (a.getDeliverableInfo(this.getActualPhase()).getStatus() == Integer
              .parseInt(ProjectStatusEnum.Extended.getStatusId())
              || a.getDeliverableInfo(this.getActualPhase()).getStatus().intValue() == 0))))
          .collect(Collectors.toList());
        if (this.isReportingActive()) {
          openA.addAll(deliverables.stream()
            .filter(d -> d.isActive()
              && d.getDeliverableInfo(this.getActualPhase()).getYear() == this.getActualPhase().getYear()
              && d.getDeliverableInfo(this.getActualPhase()).getStatus() != null
              && d.getDeliverableInfo(this.getActualPhase()).getStatus().intValue() == Integer
                .parseInt(ProjectStatusEnum.Complete.getStatusId()))
            .collect(Collectors.toList()));
        }

        for (Deliverable deliverable : openA) {
          sectionStatus = sectionStatusManager.getSectionStatusByDeliverable(deliverable.getId(), cycle,
            this.getActualPhase().getYear(), sectionName);
          if (sectionStatus == null) {

            sectionStatus = new SectionStatus();
            sectionStatus.setMissingFields("No section");
          }
          if (sectionStatus.getMissingFields().length() > 0) {
            section.put("missingFields", section.get("missingFields") + "-" + sectionStatus.getMissingFields());

          }


        }
        if (project.getProjecInfoPhase(this.getActualPhase()).getAdministrative() != null
          && project.getProjecInfoPhase(this.getActualPhase()).getAdministrative().booleanValue()) {
          sectionStatus = new SectionStatus();
          sectionStatus.setMissingFields("");
          section.put("missingFields", "");
        } else {
          if (openA.isEmpty()) {
            sectionStatus = new SectionStatus();
            sectionStatus.setMissingFields("");
            section.put("missingFields", "Empty Deliverables");
          }
        }


        break;


      case ACTIVITIES:
        sectionStatus = sectionStatusManager.getSectionStatusByProject(projectID, cycle,
          this.getActualPhase().getYear(), sectionName);
        section = new HashMap<String, Object>();
        section.put("sectionName", sectionStatus.getSectionName());
        section.put("missingFields", sectionStatus.getMissingFields());


        break;

      case CASESTUDIES:
        List<CaseStudyProject> caseStudies =
          project.getCaseStudyProjects().stream().filter(d -> d.isActive()).collect(Collectors.toList());
        List<CaseStudy> projectCaseStudies = new ArrayList<>();
        section = new HashMap<String, Object>();
        section.put("sectionName", ProjectSectionStatusEnum.CASESTUDIES);
        section.put("missingFields", "");


        for (CaseStudyProject caseStudyProject : caseStudies) {
          if (caseStudyProject.isCreated()
            && caseStudyProject.getCaseStudy().getYear() == this.getActualPhase().getYear()) {
            projectCaseStudies.add(caseStudyProject.getCaseStudy());
            sectionStatus = sectionStatusManager.getSectionStatusByCaseStudy(caseStudyProject.getCaseStudy().getId(),
              cycle, this.getActualPhase().getYear(), sectionName);
            if (sectionStatus == null) {

              sectionStatus = new SectionStatus();
              sectionStatus.setMissingFields("No section");
            }
            if (sectionStatus.getMissingFields().length() > 0) {
              section.put("missingFields", section.get("missingFields") + "-" + sectionStatus.getMissingFields());

            }
          }
        }


        break;

      case HIGHLIGHT:
        List<ProjectHighlight> highlights = project.getProjectHighligths().stream()
          .filter(d -> d.isActive() && d.getYear().intValue() == this.getActualPhase().getYear())
          .collect(Collectors.toList());

        section = new HashMap<String, Object>();
        section.put("sectionName", ProjectSectionStatusEnum.HIGHLIGHT);
        section.put("missingFields", "");


        for (ProjectHighlight highlight : highlights) {

          sectionStatus = sectionStatusManager.getSectionStatusByProjectHighlight(highlight.getId(), cycle,
            this.getActualPhase().getYear(), sectionName);
          if (sectionStatus == null) {

            sectionStatus = new SectionStatus();
            sectionStatus.setMissingFields("No section");
          }
          if (sectionStatus.getMissingFields().length() > 0) {
            section.put("missingFields", section.get("missingFields") + "-" + sectionStatus.getMissingFields());

          }

        }


        break;


      case BUDGET:

        if (this.isReportingActive()) {
          section = new HashMap<String, Object>();

          section.put("sectionName", ProjectSectionStatusEnum.BUDGET);
          section.put("missingFields", "");

        } else {
          sectionStatus = sectionStatusManager.getSectionStatusByProject(projectID, cycle,
            this.getActualPhase().getYear(), sectionName);
          section = new HashMap<String, Object>();

          section.put("sectionName", sectionStatus.getSectionName());
          section.put("missingFields", sectionStatus.getMissingFields());
        }


        break;


      case LEVERAGES:
        sectionStatus = sectionStatusManager.getSectionStatusByProject(projectID, cycle,
          this.getActualPhase().getYear(), sectionName);
        section = new HashMap<String, Object>();

        section.put("sectionName", sectionStatus.getSectionName());
        section.put("missingFields", sectionStatus.getMissingFields());
        break;

      default:
        sectionStatus = sectionStatusManager.getSectionStatusByProject(projectID, cycle,
          this.getActualPhase().getYear(), sectionName);
        if (sectionStatus != null) {
          section = new HashMap<String, Object>();
          section.put("sectionName", sectionStatus.getSectionName());
          section.put("missingFields", sectionStatus.getMissingFields());
        }

        break;
    }


    // Thread.sleep(500);
    return SUCCESS;
  }


  public Long getProjectID() {
    return projectID;
  }


  public Map<String, Object> getSection() {
    return section;
  }


  public String getSectionName() {
    return sectionName;
  }


  public boolean isExistProject() {
    return existProject;
  }


  public boolean isValidSection() {
    return validSection;
  }

  @Override
  public void prepare() throws Exception {

    // Map<String, Object> parameters = this.getParameters();
    Map<String, Parameter> parameters = this.getParameters();

    loggedCrp = (Crp) this.getSession().get(APConstants.SESSION_CRP);
    loggedCrp = crpManager.getCrpById(loggedCrp.getId());
    // sectionName = StringUtils.trim(((String[]) parameters.get(APConstants.SECTION_NAME))[0]);
    sectionName = StringUtils.trim(parameters.get(APConstants.SECTION_NAME).getMultipleValues()[0]);

    // projectID = Long.parseLong(StringUtils.trim(((String[]) parameters.get(APConstants.PROJECT_REQUEST_ID))[0]));
    projectID = Long.parseLong(StringUtils.trim(parameters.get(APConstants.PROJECT_REQUEST_ID).getMultipleValues()[0]));
    // Validate if project exists.
    existProject = projectManager.existProject(projectID);

    // Validate if the section exists.
    validSection = ProjectSectionStatusEnum.value(sectionName) != null;


  }

  public void setExistProject(boolean existProject) {
    this.existProject = existProject;
  }


  public void setProjectID(Long projectID) {
    this.projectID = projectID;
  }

  public void setSection(Map<String, Object> section) {
    this.section = section;
  }

  public void setSectionName(String sectionName) {
    this.sectionName = sectionName;
  }


  public void setValidSection(boolean validSection) {
    this.validSection = validSection;
  }

}
