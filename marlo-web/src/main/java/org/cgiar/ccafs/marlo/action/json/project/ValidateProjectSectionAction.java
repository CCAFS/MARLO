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
import org.cgiar.ccafs.marlo.data.manager.DeliverableInfoManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitProjectManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.SectionStatusManager;
import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.data.model.DeliverableInfo;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.GlobalUnitProject;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudy;
import org.cgiar.ccafs.marlo.data.model.ProjectHighlight;
import org.cgiar.ccafs.marlo.data.model.ProjectInfo;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovation;
import org.cgiar.ccafs.marlo.data.model.ProjectLp6Contribution;
import org.cgiar.ccafs.marlo.data.model.ProjectOutcome;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicy;
import org.cgiar.ccafs.marlo.data.model.ProjectSectionStatusEnum;
import org.cgiar.ccafs.marlo.data.model.ProjectStatusEnum;
import org.cgiar.ccafs.marlo.data.model.SectionStatus;
import org.cgiar.ccafs.marlo.data.model.SharedProjectSectionStatusEnum;
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
  private boolean validSectionCenter;

  private String sectionName;
  private Long projectID;
  private SectionStatus sectionStatus;
  private Map<String, Object> section;
  // Managers
  private final SectionStatusManager sectionStatusManager;
  private final ProjectManager projectManager;

  private final GlobalUnitManager crpManager;
  private final ProjectSectionValidator<ValidateProjectSectionAction> projectSectionValidator;
  private final GlobalUnitProjectManager globalUnitProjectManager;
  private final DeliverableInfoManager deliverableInfoManager;


  @Inject
  public ValidateProjectSectionAction(APConfig config, GlobalUnitManager crpManager, ProjectManager projectManager,
    SectionStatusManager sectionStatusManager,
    ProjectSectionValidator<ValidateProjectSectionAction> projectSectionValidator,
    LocalizedTextProvider localizedTextProvider, GlobalUnitProjectManager globalUnitProjectManager,
    DeliverableInfoManager deliverableInfoManager) {
    super(config);
    this.sectionStatusManager = sectionStatusManager;
    this.projectManager = projectManager;
    this.projectSectionValidator = projectSectionValidator;
    this.crpManager = crpManager;
    this.localizedTextProvider = localizedTextProvider;
    this.globalUnitProjectManager = globalUnitProjectManager;
    this.deliverableInfoManager = deliverableInfoManager;
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
        case SAFEGUARDS:
          if (this.isUpKeepActive() || this.isReportingActive()) {
            this.projectSectionValidator.validateSafeguards(this, this.getProjectID());
          }
          break;
        case IMPACTS:
          this.projectSectionValidator.validateProjectImpactCovid(this, this.getProjectID());
          break;
        case ACTIVITIES:
          this.projectSectionValidator.validateProjectActivities(this, this.getProjectID());
          break;

        case EXPECTEDSTUDIES:
          if (this.isAiccra() == false) {
            this.projectSectionValidator.validateProjectExpectedStudies(this, this.getProjectID());
            break;
          }

        case POLICIES:
          this.projectSectionValidator.validatePolicy(this, this.getProjectID());
          break;
        case INNOVATIONS:
          this.projectSectionValidator.validateInnovations(this, this.getProjectID());
          break;
        case PARTNERS:
          this.projectSectionValidator.validateProjectParnters(this, this.getProjectID(), this.loggedCrp);
        case BUDGET:
          if (this.isPlanningActive() || ((this.isReportingActive() || this.isUpKeepActive())
            && this.hasSpecificities(this.getCrpEnableBudgetExecution()))) {
            this.projectSectionValidator.validateProjectBudgets(this, this.getProjectID());
          }
          break;
        case BUDGETBYFLAGSHIP:
          this.projectSectionValidator.validateProjectBudgetsFlagship(this, this.getProjectID(), true);
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
        case HIGHLIGHTS:
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
        case CONTRIBUTIONLP6:
          this.projectSectionValidator.validateContributionLp6(this, this.getProjectID());
          break;
        default:
          break;
      }

    }


    // Validate Shared Projects Sections
    if (this.isCenterGlobalUnit()) {
      if (validSectionCenter) {
        switch (SharedProjectSectionStatusEnum.value(sectionName.toUpperCase())) {
          case CENTER_MAPPING:

            Phase phase = this.getActualPhase();
            GlobalUnitProject globalUnitProject =
              globalUnitProjectManager.findByProjectAndGlobalUnitId(this.getProjectID(), loggedCrp.getId());
            if (!globalUnitProject.isOrigin()) {
              GlobalUnitProject globalUnitProjectOrigin = globalUnitProjectManager.findByProjectId(this.getProjectID());
              List<Phase> phases = globalUnitProjectOrigin.getGlobalUnit().getPhases().stream()
                .filter(c -> c.isActive() && c.getDescription().equals(this.getActualPhase().getDescription())
                  && c.getYear() == this.getActualPhase().getYear() && c.getUpkeep())
                .collect(Collectors.toList());
              if (phases.size() > 0) {
                phase = phases.get(0);
              }
            }

            this.projectSectionValidator.validateProjectCenterMapping(this, this.getProjectID(), phase);
            break;

        }
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
    if (existProject && validSection) {
      switch (ProjectSectionStatusEnum.value(sectionName.toUpperCase())) {
        case OUTCOMES:
          section = new HashMap<String, Object>();
          section.put("sectionName", ProjectSectionStatusEnum.OUTCOMES);
          section.put("missingFields", "");

          // Validate LP6 Contribution question
          if (this.hasSpecificities(APConstants.CRP_LP6_ACTIVE) && this.isReportingActive()) {

            List<ProjectLp6Contribution> projectLp6Contributions = project.getProjectLp6Contributions().stream()
              .filter(pl -> pl.isActive() && pl.getPhase().equals(this.getActualPhase())).collect(Collectors.toList());
            if (projectLp6Contributions != null && !projectLp6Contributions.isEmpty()) {
              ProjectLp6Contribution projectLp6Contribution = projectLp6Contributions.get(0);
              if (projectLp6Contribution.getContribution() == null) {
                section.put("missingFields", "projects.LP6Contribution.contribution");
              }
            } else {
              section.put("missingFields", "projects.LP6Contribution.contribution");
            }

          }

          List<ProjectOutcome> projectOutcomes = project.getProjectOutcomes().stream()
            .filter(c -> c.isActive() && c.getPhase().equals(this.getActualPhase())).collect(Collectors.toList());


          if (!(projectInfo.getAdministrative() != null && projectInfo.getAdministrative().booleanValue() == true)) {
            if (projectOutcomes.isEmpty()) {
              section.put("missingFields", section.get("missingFields") + "-" + "outcomes");
            }
            project.setOutcomes(projectOutcomes);
            for (ProjectOutcome projectOutcome : project.getOutcomes()) {
              sectionStatus = sectionStatusManager.getSectionStatusByProjectOutcome(projectOutcome.getId(), cycle,
                this.getActualPhase().getYear(), this.getActualPhase().getUpkeep(), sectionName);
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

          if (project.getDeliverables().stream().filter(d -> d.isActive()).collect(Collectors.toList()).isEmpty()
            && project.getProjecInfoPhase(this.getActualPhase()) != null
            && project.getProjecInfoPhase(this.getActualPhase()).getAdministrative() != null
            && !project.getProjecInfoPhase(this.getActualPhase()).getAdministrative()) {
            section.put("missingFields", section.get("missingFields") + "-" + "deliveralbes");
          }

          Phase phase = this.getActualPhase();
          List<Deliverable> deliverables = new ArrayList<>();

          if (project.getDeliverables() != null) {

            List<DeliverableInfo> infos = deliverableInfoManager.getDeliverablesInfoByProjectAndPhase(phase, project);

            if (infos != null && !infos.isEmpty()) {
              for (DeliverableInfo deliverableInfo : infos) {
                Deliverable deliverable = deliverableInfo.getDeliverable();
                deliverable.setDeliverableInfo(deliverableInfo);
                deliverables.add(deliverable);
              }
            }
          }


          for (Deliverable deliverable : deliverables) {
            sectionStatus = sectionStatusManager.getSectionStatusByDeliverable(deliverable.getId(), cycle,
              this.getActualPhase().getYear(), this.getActualPhase().getUpkeep(), sectionName);
            if (sectionStatus == null) {
              sectionStatus = new SectionStatus();
              sectionStatus.setMissingFields("No section");
            } else {
              if (deliverable.getDeliverableInfo(phase).getStatus() != null && deliverable.getDeliverableInfo(phase)
                .getStatus().intValue() == Integer.parseInt(ProjectStatusEnum.Ongoing.getStatusId())) {
                if (deliverable.getDeliverableInfo(phase).getYear() > this.getActualPhase().getYear()) {
                  sectionStatus.setMissingFields("");
                }
              }


              if (this.isPlanningActive() && this.isUpKeepActive()) {
                if (deliverable.getDeliverableInfo(phase).getStatus() != null && deliverable.getDeliverableInfo(phase)
                  .getStatus().intValue() == Integer.parseInt(ProjectStatusEnum.Complete.getStatusId())) {
                  sectionStatus.setMissingFields("");
                }
              }

            }

            if (sectionStatus.getMissingFields().length() > 0) {
              section.put("missingFields", section.get("missingFields") + "-" + sectionStatus.getMissingFields());
            }
          }

          break;


        case ACTIVITIES:
          sectionStatus = sectionStatusManager.getSectionStatusByProject(projectID, cycle,
            this.getActualPhase().getYear(), this.getActualPhase().getUpkeep(), sectionName);
          section = new HashMap<String, Object>();
          section.put("sectionName", sectionStatus.getSectionName());
          section.put("missingFields", sectionStatus.getMissingFields());
          break;


        case EXPECTEDSTUDIES:
          section = new HashMap<String, Object>();
          section.put("sectionName", sectionName);
          section.put("missingFields", "");

          List<ProjectExpectedStudy> allProjectStudies = new ArrayList<ProjectExpectedStudy>();

          // Load Studies
          List<ProjectExpectedStudy> studies = project.getProjectExpectedStudies().stream()
            .filter(c -> c.isActive() && c.getProjectExpectedStudyInfo(this.getActualPhase()) != null)
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
                && ps.getProjectExpectedStudyInfo().getYear() >= this.getCurrentCycleYear())
              .collect(Collectors.toList());
          }


          for (ProjectExpectedStudy projectExpectedStudy : projectStudies) {
            sectionStatus = sectionStatusManager.getSectionStatusByProjectExpectedStudy(projectExpectedStudy.getId(),
              cycle, this.getActualPhase().getYear(), this.getActualPhase().getUpkeep(), sectionName);
            if (sectionStatus == null) {
              sectionStatus = new SectionStatus();
              sectionStatus.setMissingFields("No section");
            }
            if (sectionStatus.getMissingFields().length() > 0) {
              section.put("missingFields", section.get("missingFields") + "-" + sectionStatus.getMissingFields());
            }
          }
          break;

        case POLICIES:
          section = new HashMap<String, Object>();
          section.put("sectionName", sectionName);
          section.put("missingFields", "");

          List<ProjectPolicy> policies =
            project.getProjectPolicies().stream().filter(c -> c.isActive()).collect(Collectors.toList());
          for (ProjectPolicy projectPolicy : policies) {
            sectionStatus = sectionStatusManager.getSectionStatusByProjectPolicy(projectPolicy.getId(), cycle,
              this.getActualPhase().getYear(), this.getActualPhase().getUpkeep(), sectionName);
            section.put("sectionName", sectionStatus.getSectionName());
            if (sectionStatus == null) {
              sectionStatus = new SectionStatus();
              sectionStatus.setMissingFields("No section");
            }
            if (sectionStatus.getMissingFields().length() > 0) {
              section.put("missingFields", section.get("missingFields") + "-" + sectionStatus.getMissingFields());
            }
          }

          break;

        case INNOVATIONS:
          section = new HashMap<String, Object>();
          section.put("sectionName", sectionName);
          section.put("missingFields", "");

          List<ProjectInnovation> innovations =
            project.getProjectInnovations().stream().filter(c -> c.isActive()).collect(Collectors.toList());
          for (ProjectInnovation projectInnovation : innovations) {
            sectionStatus = sectionStatusManager.getSectionStatusByProjectInnovation(projectInnovation.getId(), cycle,
              this.getActualPhase().getYear(), this.getActualPhase().getUpkeep(), sectionName);
            if (sectionStatus != null && sectionStatus.getSectionName() != null) {
              section.put("sectionName", sectionStatus.getSectionName());
            }
            if (sectionStatus == null) {
              sectionStatus = new SectionStatus();
              sectionStatus.setMissingFields("No section");
            }
            if (sectionStatus.getMissingFields().length() > 0) {
              section.put("missingFields", section.get("missingFields") + "-" + sectionStatus.getMissingFields());
            }
          }

          break;
        case HIGHLIGHTS:
          section = new HashMap<String, Object>();
          section.put("sectionName", ProjectSectionStatusEnum.HIGHLIGHTS);
          section.put("missingFields", "");

          List<ProjectHighlight> highlights = project.getProjectHighligths().stream().filter(d -> d.isActive()
            && d.getProjectHighlightInfo(this.getActualPhase()).getYear().intValue() == this.getActualPhase().getYear())
            .collect(Collectors.toList());

          for (ProjectHighlight highlight : highlights) {
            sectionStatus = sectionStatusManager.getSectionStatusByProjectHighlight(highlight.getId(), cycle,
              this.getActualPhase().getYear(), this.getActualPhase().getUpkeep(), sectionName);
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

          if (this.isReportingActive() && !this.hasSpecificities(this.getCrpEnableBudgetExecution())) {
            section = new HashMap<String, Object>();

            section.put("sectionName", ProjectSectionStatusEnum.BUDGET);
            section.put("missingFields", "");

          } else {
            sectionStatus = sectionStatusManager.getSectionStatusByProject(projectID, cycle,
              this.getActualPhase().getYear(), this.getActualPhase().getUpkeep(), sectionName);
            section = new HashMap<String, Object>();

            section.put("sectionName", sectionStatus.getSectionName());
            section.put("missingFields", sectionStatus.getMissingFields());
          }


          break;


        case LEVERAGES:
          sectionStatus = sectionStatusManager.getSectionStatusByProject(projectID, cycle,
            this.getActualPhase().getYear(), this.getActualPhase().getUpkeep(), sectionName);
          section = new HashMap<String, Object>();

          section.put("sectionName", sectionStatus.getSectionName());
          section.put("missingFields", sectionStatus.getMissingFields());
          break;

        case IMPACTS:
          if (!this.hasSpecificities(APConstants.CRP_COVID_REQUIRED)) {
            section = new HashMap<String, Object>();

            section.put("sectionName", ProjectSectionStatusEnum.IMPACTS);
            section.put("missingFields", "");
          } else {
            sectionStatus = sectionStatusManager.getSectionStatusByProject(projectID, cycle,
              this.getActualPhase().getYear(), this.getActualPhase().getUpkeep(), sectionName);
            section = new HashMap<String, Object>();

            section.put("sectionName", sectionStatus.getSectionName());
            section.put("missingFields", sectionStatus.getMissingFields());
          }
          break;

        default:

          sectionStatus = sectionStatusManager.getSectionStatusByProject(projectID, cycle,
            this.getActualPhase().getYear(), this.getActualPhase().getUpkeep(), sectionName);
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
    }

    // Validate Shared Projects Sections
    if (this.isCenterGlobalUnit()) {
      if (validSectionCenter) {
        switch (SharedProjectSectionStatusEnum.value(sectionName.toUpperCase())) {
          default:
            sectionStatus = sectionStatusManager.getSectionStatusByProject(projectID, cycle,
              this.getActualPhase().getYear(), this.getActualPhase().getUpkeep(), sectionName);
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
      }
    }


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
    if (aTextName != null && locale != null) {
      return localizedTextProvider.findDefaultText(aTextName, locale);
    } else {
      return "";
    }
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
    if (loggedCrp.getId() != null && crpManager.getGlobalUnitById(loggedCrp.getId()) != null) {
      loggedCrp = crpManager.getGlobalUnitById(loggedCrp.getId());
    }

    // sectionName = StringUtils.trim(((String[]) parameters.get(APConstants.SECTION_NAME))[0]);
    sectionName = StringUtils.trim(parameters.get(APConstants.SECTION_NAME).getMultipleValues()[0]);

    // projectID = Long.parseLong(StringUtils.trim(((String[]) parameters.get(APConstants.PROJECT_REQUEST_ID))[0]));
    projectID = Long.parseLong(StringUtils.trim(parameters.get(APConstants.PROJECT_REQUEST_ID).getMultipleValues()[0]));
    // Validate if project exists.
    existProject = projectManager.existProject(projectID);

    // Validate if the section exists.
    validSection = ProjectSectionStatusEnum.value(sectionName) != null;

    // Validate if is Shared project Section
    if (this.isCenterGlobalUnit()) {
      validSectionCenter = SharedProjectSectionStatusEnum.value(sectionName) != null;
    }
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
