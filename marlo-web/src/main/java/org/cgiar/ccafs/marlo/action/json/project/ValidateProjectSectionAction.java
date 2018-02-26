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
import org.cgiar.ccafs.marlo.config.MarloLocalizedTextProvider;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.SectionStatusManager;
import org.cgiar.ccafs.marlo.data.model.CaseStudy;
import org.cgiar.ccafs.marlo.data.model.CaseStudyProject;
import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectHighlight;
import org.cgiar.ccafs.marlo.data.model.ProjectInfo;
import org.cgiar.ccafs.marlo.data.model.ProjectOutcome;
import org.cgiar.ccafs.marlo.data.model.ProjectSectionStatusEnum;
import org.cgiar.ccafs.marlo.data.model.ProjectStatusEnum;
import org.cgiar.ccafs.marlo.data.model.SectionStatus;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.validation.projects.ProjectSectionValidator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import com.opensymphony.xwork2.LocalizedTextProvider;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.dispatcher.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
@Named
public class ValidateProjectSectionAction extends BaseAction {


  public static final long serialVersionUID = 2334147747892988744L;


  // Logger
  private static final Logger LOG = LoggerFactory.getLogger(ValidateProjectSectionAction.class);

  // Model
  private boolean existProject;
  private GlobalUnit loggedCrp;
  private final LocalizedTextProvider localizedTextProvider;
  private boolean validSection;

  private String sectionName;
  private Long projectID;
  private SectionStatus sectionStatus;
  private Map<String, Object> section;
  // Managers
  private final SectionStatusManager sectionStatusManager;
  private final ProjectManager projectManager;

  private final GlobalUnitManager crpManager;
  private final ProjectSectionValidator<ValidateProjectSectionAction> projectSectionValidator;


  @Inject
  public ValidateProjectSectionAction(APConfig config, GlobalUnitManager crpManager, ProjectManager projectManager,
    SectionStatusManager sectionStatusManager,
    ProjectSectionValidator<ValidateProjectSectionAction> projectSectionValidator,
    LocalizedTextProvider localizedTextProvider) {
    super(config);
    this.sectionStatusManager = sectionStatusManager;
    this.projectManager = projectManager;
    this.projectSectionValidator = projectSectionValidator;
    this.crpManager = crpManager;
    this.localizedTextProvider = localizedTextProvider;
  }


  @Override
  public String execute() throws Exception {
    if (existProject && validSection) {
      this.loadProvider(this.getSession());
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
        case EXPECTEDSTUDIES:
          this.projectSectionValidator.validateProjectExpectedStudies(this, this.getProjectID());
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

        case BUDGETBYFLAGSHIP:
          this.projectSectionValidator.validateProjectBudgetsFlagship(this, this.getProjectID());
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


      case EXPECTEDSTUDIES:
        sectionStatus = sectionStatusManager.getSectionStatusByProject(projectID, cycle,
          this.getActualPhase().getYear(), sectionName);
        section = new HashMap<String, Object>();
        if (sectionStatus != null) {
          section.put("sectionName", sectionStatus.getSectionName());
          section.put("missingFields", sectionStatus.getMissingFields());
        } else {
          section.put("sectionName", sectionName);
          section.put("missingFields", "");
        }

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
        section = new HashMap<String, Object>();
        if (sectionStatus != null) {
          section.put("sectionName", sectionStatus.getSectionName());
          section.put("missingFields", sectionStatus.getMissingFields());
        } else {
          section.put("sectionName", sectionName);
          section.put("missingFields", "empty");
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

  public boolean isExistProject() {
    return existProject;
  }


  public boolean isValidSection() {
    return validSection;
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
  public void prepare() throws Exception {

    // Map<String, Object> parameters = this.getParameters();
    Map<String, Parameter> parameters = this.getParameters();

    loggedCrp = (GlobalUnit) this.getSession().get(APConstants.SESSION_CRP);
    loggedCrp = crpManager.getGlobalUnitById(loggedCrp.getId());
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
