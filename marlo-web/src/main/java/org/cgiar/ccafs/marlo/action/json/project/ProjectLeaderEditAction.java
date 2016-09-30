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
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.SectionStatusManager;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectSectionStatusEnum;
import org.cgiar.ccafs.marlo.data.model.SectionStatus;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.google.inject.Inject;
import com.opensymphony.xwork2.Action;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class ProjectLeaderEditAction extends BaseAction {

  private static final long serialVersionUID = -7458726524471438475L;
  @Inject
  private SectionStatusManager sectionStatusManager;
  private ProjectManager projectManager;
  private long projectId;
  private boolean projectStatus;
  private Map<String, Object> status;

  @Inject
  public ProjectLeaderEditAction(APConfig config, ProjectManager projectManager) {
    super(config);
    this.projectManager = projectManager;
  }

  @Override
  public String execute() throws Exception {
    Project project = projectManager.getProjectById(projectId);
    status = new HashMap<String, Object>();
    status.put("ProjectId", projectId);
    if (project != null) {
      project.setProjectEditLeader(projectStatus);
      project.setPresetDate(new Date());
      projectManager.saveProject(project);

      this.saveMissingFields(project, APConstants.PLANNING, this.getPlanningYear(),
        ProjectSectionStatusEnum.PARTNERS.getStatus());
      this.saveMissingFields(project, APConstants.PLANNING, this.getPlanningYear(),
        ProjectSectionStatusEnum.DESCRIPTION.getStatus());
      this.saveMissingFields(project, APConstants.PLANNING, this.getPlanningYear(),
        ProjectSectionStatusEnum.BUDGET.getStatus());

      this.saveMissingFields(project, APConstants.PLANNING, this.getPlanningYear(),
        ProjectSectionStatusEnum.ACTIVITIES.getStatus());
      this.saveMissingFields(project, APConstants.PLANNING, this.getPlanningYear(),
        ProjectSectionStatusEnum.DELIVERABLES.getStatus());
      this.saveMissingFields(project, APConstants.PLANNING, this.getPlanningYear(),
        ProjectSectionStatusEnum.OUTCOMES.getStatus());
      status.put("status", project.isProjectEditLeader());
      status.put("ok", true);
    } else {
      status.put("ok", false);
    }

    return Action.SUCCESS;
  }


  public Map<String, Object> getStatus() {
    return status;
  }


  @Override
  public void prepare() throws Exception {
    Map<String, Object> parameters = this.getParameters();
    projectId = Long.parseLong(StringUtils.trim(((String[]) parameters.get(APConstants.PROJECT_REQUEST_ID))[0]));
    projectStatus = Boolean.parseBoolean(StringUtils.trim(((String[]) parameters.get("projectStatus"))[0]));
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

  public void setStatus(Map<String, Object> status) {
    this.status = status;
  }

}
