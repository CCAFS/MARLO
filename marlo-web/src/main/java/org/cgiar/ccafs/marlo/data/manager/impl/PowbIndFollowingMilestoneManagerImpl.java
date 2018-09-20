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


import org.cgiar.ccafs.marlo.data.dao.PowbIndFollowingMilestoneDAO;
import org.cgiar.ccafs.marlo.data.manager.PowbIndFollowingMilestoneManager;
import org.cgiar.ccafs.marlo.data.model.PowbIndFollowingMilestone;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class PowbIndFollowingMilestoneManagerImpl implements PowbIndFollowingMilestoneManager {


  private PowbIndFollowingMilestoneDAO powbIndFollowingMilestoneDAO;
  // Managers


  @Inject
  public PowbIndFollowingMilestoneManagerImpl(PowbIndFollowingMilestoneDAO powbIndFollowingMilestoneDAO) {
    this.powbIndFollowingMilestoneDAO = powbIndFollowingMilestoneDAO;


  }

  @Override
  public void deletePowbIndFollowingMilestone(long powbIndFollowingMilestoneId) {

    powbIndFollowingMilestoneDAO.deletePowbIndFollowingMilestone(powbIndFollowingMilestoneId);
  }

  @Override
  public boolean existPowbIndFollowingMilestone(long powbIndFollowingMilestoneID) {

    return powbIndFollowingMilestoneDAO.existPowbIndFollowingMilestone(powbIndFollowingMilestoneID);
  }

  @Override
  public List<PowbIndFollowingMilestone> findAll() {

    return powbIndFollowingMilestoneDAO.findAll();

  }

  @Override
  public PowbIndFollowingMilestone getPowbIndFollowingMilestoneById(long powbIndFollowingMilestoneID) {

    return powbIndFollowingMilestoneDAO.find(powbIndFollowingMilestoneID);
  }

  @Override
  public PowbIndFollowingMilestone savePowbIndFollowingMilestone(PowbIndFollowingMilestone powbIndFollowingMilestone) {

    return powbIndFollowingMilestoneDAO.save(powbIndFollowingMilestone);
  }


}
