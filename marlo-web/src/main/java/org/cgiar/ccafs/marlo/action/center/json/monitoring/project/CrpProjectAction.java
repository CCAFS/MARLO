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

package org.cgiar.ccafs.marlo.action.center.json.monitoring.project;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.HashMap;
import java.util.Map;

import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class CrpProjectAction extends BaseAction {


  private static final long serialVersionUID = 1318197532459096198L;


  private ProjectManager projectManager;

  private long projectID;

  private Map<String, Object> projectInfo;

  @Inject
  public CrpProjectAction(APConfig config, ProjectManager projectManager) {
    super(config);
    this.projectManager = projectManager;
  }

  @Override
  public String execute() throws Exception {
    projectInfo = new HashMap<String, Object>();
    Project project = projectManager.getProjectById(projectID);

    if (project != null) {
      projectInfo.put("id", project.getId());
      projectInfo.put("id", project.getId());
    }


    return SUCCESS;
  }

  public Map<String, Object> getProjectInfo() {
    return projectInfo;
  }

  @Override
  public void prepare() throws Exception {
    Map<String, Object> parameters = this.getParameters();
    projectID = Long.parseLong(StringUtils.trim(((String[]) parameters.get(APConstants.PROJECT_ID))[0]));
  }

  public void setProjectInfo(Map<String, Object> projectInfo) {
    this.projectInfo = projectInfo;
  }

}
