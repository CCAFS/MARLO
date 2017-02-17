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


package org.cgiar.ccafs.marlo.action.summaries;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.CrpManager;
import org.cgiar.ccafs.marlo.data.model.Crp;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectStatusEnum;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.List;
import java.util.stream.Collectors;

import com.google.inject.Inject;

public class SummaryListAction extends BaseAction {

  /**
   * 
   */
  private static final long serialVersionUID = -4099604925246954828L;

  private List<Project> allProjects;

  private Crp loggedCrp;


  private CrpManager crpManager;

  @Inject
  public SummaryListAction(APConfig config, CrpManager crpManager) {
    super(config);
    this.crpManager = crpManager;
  }

  public List<Project> getAllProjects() {
    return allProjects;
  }


  @Override
  public void prepare() throws Exception {
    loggedCrp = (Crp) this.getSession().get(APConstants.SESSION_CRP);
    loggedCrp = crpManager.getCrpById(loggedCrp.getId());


    if (!this.isReportingActive()) {
      allProjects = loggedCrp.getProjects().stream()
        .filter(p -> p.isActive() && p.getStatus() != null
          && p.getStatus().intValue() == Integer.parseInt(ProjectStatusEnum.Ongoing.getStatusId()))
        .collect(Collectors.toList());
    } else {
      allProjects = loggedCrp.getProjects().stream()
        .filter(
          p -> p.isActive() && p.getStatus() != null && p.getReporting() != null && p.getReporting().booleanValue())
        .collect(Collectors.toList());
    }


  }

  public void setAllProjects(List<Project> allProjects) {
    this.allProjects = allProjects;
  }

}
