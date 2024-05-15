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
package org.cgiar.ccafs.marlo.data.manager.impl;


import org.cgiar.ccafs.marlo.data.dao.DisaggregatedTargetCrpMilestoneDAO;
import org.cgiar.ccafs.marlo.data.manager.DisaggregatedTargetCrpMilestoneManager;
import org.cgiar.ccafs.marlo.data.model.DisaggregatedTargetCrpMilestone;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class DisaggregatedTargetCrpMilestoneManagerImpl implements DisaggregatedTargetCrpMilestoneManager {


  private DisaggregatedTargetCrpMilestoneDAO disaggregatedTargetCrpMilestoneDAO;
  // Managers


  @Inject
  public DisaggregatedTargetCrpMilestoneManagerImpl(DisaggregatedTargetCrpMilestoneDAO disaggregatedTargetCrpMilestoneDAO) {
    this.disaggregatedTargetCrpMilestoneDAO = disaggregatedTargetCrpMilestoneDAO;


  }

  @Override
  public void deleteDisaggregatedTargetCrpMilestone(long disaggregatedTargetCrpMilestoneId) {

    disaggregatedTargetCrpMilestoneDAO.deleteDisaggregatedTargetCrpMilestone(disaggregatedTargetCrpMilestoneId);
  }

  @Override
  public boolean existDisaggregatedTargetCrpMilestone(long disaggregatedTargetCrpMilestoneID) {

    return disaggregatedTargetCrpMilestoneDAO.existDisaggregatedTargetCrpMilestone(disaggregatedTargetCrpMilestoneID);
  }

  @Override
  public List<DisaggregatedTargetCrpMilestone> findAll() {

    return disaggregatedTargetCrpMilestoneDAO.findAll();

  }

  @Override
  public DisaggregatedTargetCrpMilestone getDisaggregatedTargetCrpMilestoneById(long disaggregatedTargetCrpMilestoneID) {

    return disaggregatedTargetCrpMilestoneDAO.find(disaggregatedTargetCrpMilestoneID);
  }

  @Override
  public DisaggregatedTargetCrpMilestone saveDisaggregatedTargetCrpMilestone(DisaggregatedTargetCrpMilestone disaggregatedTargetCrpMilestone) {

    return disaggregatedTargetCrpMilestoneDAO.save(disaggregatedTargetCrpMilestone);
  }


}
