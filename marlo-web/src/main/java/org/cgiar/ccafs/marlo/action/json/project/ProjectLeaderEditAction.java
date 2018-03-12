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
import org.cgiar.ccafs.marlo.data.manager.ProjectInfoManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.SectionStatusManager;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectInfo;
import org.cgiar.ccafs.marlo.data.model.ProjectSectionStatusEnum;
import org.cgiar.ccafs.marlo.data.model.SectionStatus;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.validation.projects.ProjectSectionValidator;

import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.LocalizedTextProvider;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.dispatcher.Parameter;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
@Named
public class ProjectLeaderEditAction extends BaseAction {

  private static final long serialVersionUID = -7458726524471438475L;
  private SectionStatusManager sectionStatusManager;
  private ProjectManager projectManager;
  private ProjectInfoManager projectInfoManager;
  private long projectId;
  private boolean projectStatus;

  private Map<String, Object> status;


  private ProjectSectionValidator<ProjectLeaderEditAction> validateProject;

  private final LocalizedTextProvider localizedTextProvider;

  @Inject
  public ProjectLeaderEditAction(APConfig config, ProjectManager projectManager, ProjectInfoManager projectInfoManager,
    SectionStatusManager sectionStatusManager, ProjectSectionValidator<ProjectLeaderEditAction> validateProject,
    LocalizedTextProvider localizedTextProvider) {
    super(config);
    this.projectInfoManager = projectInfoManager;
    this.projectManager = projectManager;
    this.validateProject = validateProject;
    this.localizedTextProvider = localizedTextProvider;
  }

  @Override
  public String execute() throws Exception {
    this.loadProvider(this.getSession());
    Project project = projectManager.getProjectById(projectId);
    project.setProjectInfo(null);
    ProjectInfo projectInfo = project.getProjecInfoPhase(this.getActualPhase());
    status = new HashMap<String, Object>();
    status.put("ProjectId", projectId);
    if (project != null) {

      projectInfo.setProjectEditLeader(projectStatus);
      projectInfo.setPresetDate(new Date());
      projectInfoManager.saveProjectInfo(projectInfo);


      status.put("status", projectInfo.isProjectEditLeader());
      status.put("ok", true);

      for (ProjectSectionStatusEnum projectSectionStatusEnum : ProjectSectionStatusEnum.values()) {
        switch (projectSectionStatusEnum) {

          case LOCATIONS:
            validateProject.validateProjectLocations(this, this.projectId);
            break;
          case DESCRIPTION:
            validateProject.validateProjectDescription(this, this.projectId);
            break;
          case ACTIVITIES:
            validateProject.validateProjectActivities(this, this.projectId);
            break;
          case PARTNERS:
            validateProject.validateProjectParnters(this, this.projectId, this.getCurrentCrp());
          case BUDGET:
            validateProject.validateProjectBudgets(this, this.projectId);
            break;
          case BUDGETBYCOA:
            validateProject.validateProjectBudgetsCoAs(this, this.projectId);
            break;

          case DELIVERABLES:
            validateProject.validateProjectDeliverables(this, this.projectId);
            break;

          case OUTCOMES:
            validateProject.validateProjectOutcomes(this, this.projectId);
            break;

          default:
            break;
        }
      }
    } else {
      status.put("ok", false);
    }

    return Action.SUCCESS;
  }


  public long getProjectId() {
    return projectId;
  }

  public Map<String, Object> getStatus() {
    return status;
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


  public boolean isProjectStatus() {
    return projectStatus;
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
    // projectId = Long.parseLong(StringUtils.trim(((String[]) parameters.get(APConstants.PROJECT_REQUEST_ID))[0]));
    // projectStatus = Boolean.parseBoolean(StringUtils.trim(((String[]) parameters.get("projectStatus"))[0]));

    Map<String, Parameter> parameters = this.getParameters();
    projectId = Long.parseLong(StringUtils.trim(parameters.get(APConstants.PROJECT_REQUEST_ID).getMultipleValues()[0]));
    projectStatus = Boolean.parseBoolean(StringUtils.trim(parameters.get("projectStatus").getMultipleValues()[0]));
  }


  private void saveMissingFields(Project project, String cycle, int year, String sectionName) {
    // Reporting missing fields into the database.

    SectionStatus status = sectionStatusManager.getSectionStatusByProject(project.getId(), cycle, year, sectionName);
    if (status == null) {

      status = new SectionStatus();
      status.setCycle(cycle);
      status.setYear(year);
      status.setProject(project);
      status.setSectionName(sectionName);


    }
    status.setMissingFields("editLeader");
    sectionStatusManager.saveSectionStatus(status);
  }


  public void setProjectId(long projectId) {
    this.projectId = projectId;
  }

  public void setProjectStatus(boolean projectStatus) {
    this.projectStatus = projectStatus;
  }

  public void setStatus(Map<String, Object> status) {
    this.status = status;
  }

}
