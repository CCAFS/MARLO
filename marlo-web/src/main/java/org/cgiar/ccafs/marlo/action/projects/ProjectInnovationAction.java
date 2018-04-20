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

package org.cgiar.ccafs.marlo.action.projects;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.utils.APConfig;

import javax.inject.Inject;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class ProjectInnovationAction extends BaseAction {

  private static final long serialVersionUID = 2025842196563364380L;

  private GlobalUnitManager crpManager;
  private GlobalUnit loggedCrp;

  @Inject
  public ProjectInnovationAction(APConfig config, GlobalUnitManager crpManager, GlobalUnit loggedCrp) {
    super(config);
    this.crpManager = crpManager;
    this.loggedCrp = loggedCrp;
  }

}
