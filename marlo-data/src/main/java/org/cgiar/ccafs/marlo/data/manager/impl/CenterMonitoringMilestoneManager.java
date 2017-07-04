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


import org.cgiar.ccafs.marlo.data.dao.ICenterMonitoringMilestoneDAO;
import org.cgiar.ccafs.marlo.data.model.CenterMonitoringMilestone;
import org.cgiar.ccafs.marlo.data.service.ICenterMonitoringMilestoneManager;

import java.util.List;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class CenterMonitoringMilestoneManager implements ICenterMonitoringMilestoneManager {


  private ICenterMonitoringMilestoneDAO monitoringMilestoneDAO;

  // Managers


  @Inject
  public CenterMonitoringMilestoneManager(ICenterMonitoringMilestoneDAO monitoringMilestoneDAO) {
    this.monitoringMilestoneDAO = monitoringMilestoneDAO;


  }

  @Override
  public boolean deleteMonitoringMilestone(long monitoringMilestoneId) {

    return monitoringMilestoneDAO.deleteMonitoringMilestone(monitoringMilestoneId);
  }

  @Override
  public boolean existMonitoringMilestone(long monitoringMilestoneID) {

    return monitoringMilestoneDAO.existMonitoringMilestone(monitoringMilestoneID);
  }

  @Override
  public List<CenterMonitoringMilestone> findAll() {

    return monitoringMilestoneDAO.findAll();

  }

  @Override
  public CenterMonitoringMilestone getMonitoringMilestoneById(long monitoringMilestoneID) {

    return monitoringMilestoneDAO.find(monitoringMilestoneID);
  }

  @Override
  public List<CenterMonitoringMilestone> getMonitoringMilestonesByUserId(Long userId) {
    return monitoringMilestoneDAO.getMonitoringMilestonesByUserId(userId);
  }

  @Override
  public long saveMonitoringMilestone(CenterMonitoringMilestone monitoringMilestone) {

    return monitoringMilestoneDAO.save(monitoringMilestone);
  }


}
