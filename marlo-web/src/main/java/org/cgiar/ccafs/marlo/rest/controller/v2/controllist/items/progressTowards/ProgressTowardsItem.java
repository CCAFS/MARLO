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

/**************
 * @author Diego Perez - CIAT/CCAFS
 **************/

package org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.progressTowards;

import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.rest.dto.NewProgressTowardsSRFTargetDTO;

import javax.inject.Inject;
import javax.inject.Named;

@Named
public class ProgressTowardsItem<T> {

  private PhaseManager phaseManager;
  private GlobalUnitManager globalUnitManager;

  @Inject
  public ProgressTowardsItem(GlobalUnitManager globalUnitManager, PhaseManager phaseManager) {
    this.phaseManager = phaseManager;
    this.globalUnitManager = globalUnitManager;
  }

  public Long createProgressTowards(NewProgressTowardsSRFTargetDTO newProgressTowardsSRFTargetDTO, String entityAcronym,
    User user) {
    Long policyID = null;

    return policyID;
  }
}
