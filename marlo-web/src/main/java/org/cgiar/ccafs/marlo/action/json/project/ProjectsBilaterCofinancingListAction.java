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
import org.cgiar.ccafs.marlo.data.manager.ProjectBilateralCofinancingManager;
import org.cgiar.ccafs.marlo.data.model.ProjectBilateralCofinancing;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Christian Garcia - CIAT/CCAFS
 */
public class ProjectsBilaterCofinancingListAction extends BaseAction {


  private static final long serialVersionUID = 7165684771318861734L;


  private long institutionID;
  private int year;
  private String queryParameter;
  private ProjectBilateralCofinancingManager projectBilateralCofinancingManager;
  private List<Map<String, Object>> projects;


  @Inject
  public ProjectsBilaterCofinancingListAction(APConfig config,
    ProjectBilateralCofinancingManager projectBilateralCofinancingManager) {
    super(config);
    this.projectBilateralCofinancingManager = projectBilateralCofinancingManager;

  }


  @Override
  public String execute() throws Exception {

    List<ProjectBilateralCofinancing> projectBilateralCofinancings =
      projectBilateralCofinancingManager.searchProject(queryParameter, institutionID, year);
    this.projects = new ArrayList<>();
    for (ProjectBilateralCofinancing projectBilateralCofinancing : projectBilateralCofinancings) {
      Map<String, Object> projectMap = new HashMap<>();
      projectMap.put("id", projectBilateralCofinancing.getId());
      projectMap.put("title", projectBilateralCofinancing.getTitle());
      projects.add(projectMap);

    }

    return SUCCESS;
  }


  public List<Map<String, Object>> getProjects() {
    return projects;
  }


  @Override
  public void prepare() throws Exception {
    Map<String, Object> parameters = this.getParameters();
    institutionID =
      Long.parseLong(StringUtils.trim(((String[]) parameters.get(APConstants.INSTITUTION_REQUEST_ID))[0]));
    queryParameter = StringUtils.trim(((String[]) parameters.get(APConstants.QUERY_PARAMETER))[0]);
    year = Integer.parseInt(StringUtils.trim(((String[]) parameters.get(APConstants.YEAR_REQUEST))[0]));
  }


  public void setProjects(List<Map<String, Object>> projects) {
    this.projects = projects;
  }


}
