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

package org.cgiar.ccafs.marlo.action.bilaterals;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.CrpManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectBilateralCofinancingManager;
import org.cgiar.ccafs.marlo.data.model.Crp;
import org.cgiar.ccafs.marlo.data.model.ProjectBilateralCofinancing;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.List;
import java.util.stream.Collectors;

import com.google.inject.Inject;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class CofundedListAction extends BaseAction {


  private static final long serialVersionUID = -8858893084495492581L;


  private Crp loggedCrp;


  private List<ProjectBilateralCofinancing> myProjects;


  private List<ProjectBilateralCofinancing> allProjects;


  private ProjectBilateralCofinancingManager projectBilateralCofinancingManager;


  private CrpManager crpManager;

  @Inject
  public CofundedListAction(APConfig config, ProjectBilateralCofinancingManager projectBilateralCofinancingManager,
    CrpManager crpManager) {
    super(config);
    this.projectBilateralCofinancingManager = projectBilateralCofinancingManager;
    this.crpManager = crpManager;
  }

  public List<ProjectBilateralCofinancing> getAllProjects() {
    return allProjects;
  }

  public Crp getLoggedCrp() {
    return loggedCrp;
  }

  public List<ProjectBilateralCofinancing> getMyProjects() {
    return myProjects;
  }

  @Override
  public void prepare() throws Exception {

    loggedCrp = (Crp) this.getSession().get(APConstants.SESSION_CRP);
    loggedCrp = crpManager.getCrpById(loggedCrp.getId());

    if (projectBilateralCofinancingManager.findAll() != null) {

      if (this.canAccessSuperAdmin() || this.canAcessCrpAdmin()) {
        myProjects =
          loggedCrp.getProjectBilateralCofinancings().stream().filter(p -> p.isActive()).collect(Collectors.toList());
      } else {
        allProjects =
          loggedCrp.getProjectBilateralCofinancings().stream().filter(p -> p.isActive()).collect(Collectors.toList());
        // myProjects = projectManager.getUserProjects(this.getCurrentUser().getId(), loggedCrp.getAcronym());
        // Collections.sort(myProjects, (p1, p2) -> p1.getId().compareTo(p2.getId()));

        allProjects.removeAll(myProjects);
      }
    }

  }

  public void setAllProjects(List<ProjectBilateralCofinancing> allProjects) {
    this.allProjects = allProjects;
  }

  public void setLoggedCrp(Crp loggedCrp) {
    this.loggedCrp = loggedCrp;
  }

  public void setMyProjects(List<ProjectBilateralCofinancing> myProjects) {
    this.myProjects = myProjects;
  }

}
