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

import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.List;

import javax.inject.Inject;

/**
 * When the reports information lives on the database. The class will be used to List the reports.
 */
public class SummaryListAction extends BaseSummariesAction {

  /**
   * 
   */
  private static final long serialVersionUID = -4099604925246954828L;

  private List<Project> allProjects;

  private GlobalUnit loggedCrp;


  private final GlobalUnitManager crpManager;
  private final PhaseManager phaseManager;

  @Inject
  public SummaryListAction(APConfig config, PhaseManager phaseManager, GlobalUnitManager crpManager) {
    super(config, crpManager, phaseManager);
    this.crpManager = crpManager;
    this.phaseManager = phaseManager;
  }

  public List<Project> getAllProjects() {
    return allProjects;
  }


  @Override
  public void prepare() throws Exception {
    /*
     * 
     */
  }

  public void setAllProjects(List<Project> allProjects) {
    this.allProjects = allProjects;
  }

}
