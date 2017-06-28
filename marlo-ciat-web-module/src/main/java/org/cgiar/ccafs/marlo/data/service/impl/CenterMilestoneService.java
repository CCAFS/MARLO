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
package org.cgiar.ccafs.marlo.data.service.impl;


import org.cgiar.ccafs.marlo.data.dao.ICenterMilestoneDAO;
import org.cgiar.ccafs.marlo.data.model.CenterMilestone;
import org.cgiar.ccafs.marlo.data.service.ICenterMilestoneService;

import java.util.List;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class CenterMilestoneService implements ICenterMilestoneService {


  private ICenterMilestoneDAO centerMilestoneDAO;

  // Managers


  @Inject
  public CenterMilestoneService(ICenterMilestoneDAO centerMilestoneDAO) {
    this.centerMilestoneDAO = centerMilestoneDAO;


  }

  @Override
  public boolean deleteCenterMilestone(long centerMilestoneId) {

    return centerMilestoneDAO.deleteCenterMilestone(centerMilestoneId);
  }

  @Override
  public boolean existCenterMilestone(long centerMilestoneID) {

    return centerMilestoneDAO.existCenterMilestone(centerMilestoneID);
  }

  @Override
  public List<CenterMilestone> findAll() {

    return centerMilestoneDAO.findAll();

  }

  @Override
  public CenterMilestone getCenterMilestoneById(long centerMilestoneID) {

    return centerMilestoneDAO.find(centerMilestoneID);
  }

  @Override
  public List<CenterMilestone> getCenterMilestonesByUserId(Long userId) {
    return centerMilestoneDAO.getCenterMilestonesByUserId(userId);
  }

  @Override
  public long saveCenterMilestone(CenterMilestone centerMilestone) {

    return centerMilestoneDAO.save(centerMilestone);
  }

  @Override
  public long saveCenterMilestone(CenterMilestone centerMilestone, String actionName, List<String> relationsName) {
    return centerMilestoneDAO.save(centerMilestone, actionName, relationsName);
  }


}
