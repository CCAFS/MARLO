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


import org.cgiar.ccafs.marlo.data.dao.RepIndMilestoneReasonDAO;
import org.cgiar.ccafs.marlo.data.manager.RepIndMilestoneReasonManager;
import org.cgiar.ccafs.marlo.data.model.RepIndMilestoneReason;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class RepIndMilestoneReasonManagerImpl implements RepIndMilestoneReasonManager {


  private RepIndMilestoneReasonDAO repIndMilestoneReasonDAO;
  // Managers


  @Inject
  public RepIndMilestoneReasonManagerImpl(RepIndMilestoneReasonDAO repIndMilestoneReasonDAO) {
    this.repIndMilestoneReasonDAO = repIndMilestoneReasonDAO;


  }

  @Override
  public void deleteRepIndMilestoneReason(long repIndMilestoneReasonId) {

    repIndMilestoneReasonDAO.deleteRepIndMilestoneReason(repIndMilestoneReasonId);
  }

  @Override
  public boolean existRepIndMilestoneReason(long repIndMilestoneReasonID) {

    return repIndMilestoneReasonDAO.existRepIndMilestoneReason(repIndMilestoneReasonID);
  }

  @Override
  public List<RepIndMilestoneReason> findAll() {

    return repIndMilestoneReasonDAO.findAll();

  }

  @Override
  public RepIndMilestoneReason getRepIndMilestoneReasonById(long repIndMilestoneReasonID) {

    return repIndMilestoneReasonDAO.find(repIndMilestoneReasonID);
  }

  @Override
  public RepIndMilestoneReason saveRepIndMilestoneReason(RepIndMilestoneReason repIndMilestoneReason) {

    return repIndMilestoneReasonDAO.save(repIndMilestoneReason);
  }


}
