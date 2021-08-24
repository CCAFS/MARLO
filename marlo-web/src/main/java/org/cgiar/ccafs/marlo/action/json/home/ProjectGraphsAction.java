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

package org.cgiar.ccafs.marlo.action.json.home;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.dto.ProjectHomeDTO;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.dto.GraphCountDTO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.collections4.Bag;
import org.apache.commons.collections4.bag.HashBag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**************
 * @author German C. Martinez - CIAT/CCAFS
 **************/

public class ProjectGraphsAction extends BaseAction {

  /**
   * 
   */
  private static final long serialVersionUID = -3789498953595517996L;

  // Logger
  private static final Logger LOG = LoggerFactory.getLogger(ProjectGraphsAction.class);

  // Variables
  private List<ProjectHomeDTO> projects;

  // Graph results
  private List<GraphCountDTO> byProjectProgramType;
  private List<GraphCountDTO> byProjectStatus;

  @Inject
  public ProjectGraphsAction(APConfig config) {
    super(config);
  }

  @Override
  public String execute() throws Exception {
    this.byProjectProgramType = new ArrayList<>();
    this.byProjectStatus = new ArrayList<>();

    this.projects.stream().collect(Collectors.groupingBy(p -> p.getStatus()))
      .forEach((k, v) -> byProjectStatus.add(new GraphCountDTO(k, (long) v.size())));
    Bag<String> acc =
      this.projects.stream().flatMap(p -> p.getProgramType().stream()).collect(Collectors.toCollection(HashBag::new));
    acc.uniqueSet().forEach(u -> byProjectProgramType.add(new GraphCountDTO(u, (long) acc.getCount(u))));

    return SUCCESS;
  }

  public List<GraphCountDTO> getByProjectProgramType() {
    return byProjectProgramType;
  }

  public List<GraphCountDTO> getByProjectStatus() {
    return byProjectStatus;
  }

  @SuppressWarnings("unchecked")
  @Override
  public void prepare() throws Exception {
    try {
      this.projects = (List<ProjectHomeDTO>) this.getSession().get(APConstants.USER_PROJECTS);
      if (projects == null) {
        projects = Collections.emptyList();
      }
    } catch (Exception e) {
      e.printStackTrace();
      this.projects = Collections.emptyList();
    }
  }

  public void setByProjectProgramType(List<GraphCountDTO> byProjectProgramType) {
    this.byProjectProgramType = byProjectProgramType;
  }

  public void setByProjectStatus(List<GraphCountDTO> byProjectStatus) {
    this.byProjectStatus = byProjectStatus;
  }
}
