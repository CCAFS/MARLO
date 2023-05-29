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
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.dispatcher.Parameter;

public class ProjectById extends BaseAction {

  /**
   * 
   */
  private static final long serialVersionUID = 2999679358601654825L;

  private Map<String, Object> projectMap;
  private ProjectManager projectManager;

  private Integer projectId;

  @Inject
  public ProjectById(APConfig config, ProjectManager projectManager) {
    super(config);

    this.projectManager = projectManager;
  }

  @Override
  public String execute() throws Exception {
    Project project = (projectId != null) ? projectManager.getProjectById(this.projectId) : null;

    if (project != null) {
      this.projectMap = new HashMap<>();
      this.projectMap.put("id", project.getId());
      this.projectMap.put("acronym", project.getAcronym());
      if (project.getId() != null && this.isProjectSubmitted(project.getId())) {
        this.projectMap.put("submitted", true);
      } else {
        this.projectMap.put("false", true);
      }
    } else {
      this.projectMap = Collections.emptyMap();
    }

    return SUCCESS;
  }

  public Map<String, Object> getProjectMap() {
    return projectMap;
  }

  @Override
  public void prepare() throws Exception {
    Map<String, Parameter> parameters = this.getParameters();

    // If there are parameters, take its values
    try {
      this.projectId = Integer.valueOf(StringUtils.trim(parameters.get(APConstants.PROJECT_ID).getMultipleValues()[0]));
    } catch (Exception e) {
      this.projectId = null;
    }
  }

  public void setProjectMap(Map<String, Object> projectMap) {
    this.projectMap = projectMap;
  }

}
