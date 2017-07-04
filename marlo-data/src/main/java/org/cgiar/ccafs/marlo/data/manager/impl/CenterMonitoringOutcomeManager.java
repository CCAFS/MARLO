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


import org.cgiar.ccafs.marlo.data.dao.ICenterMonitoringOutcomeDAO;
import org.cgiar.ccafs.marlo.data.manager.ICenterMonitoringOutcomeManager;
import org.cgiar.ccafs.marlo.data.model.CenterMonitoringOutcome;

import java.util.List;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class CenterMonitoringOutcomeManager implements ICenterMonitoringOutcomeManager {


  private ICenterMonitoringOutcomeDAO monitoringOutcomeDAO;

  // Managers


  @Inject
  public CenterMonitoringOutcomeManager(ICenterMonitoringOutcomeDAO monitoringOutcomeDAO) {
    this.monitoringOutcomeDAO = monitoringOutcomeDAO;


  }

  @Override
  public boolean deleteMonitoringOutcome(long monitoringOutcomeId) {

    return monitoringOutcomeDAO.deleteMonitoringOutcome(monitoringOutcomeId);
  }

  @Override
  public boolean existMonitoringOutcome(long monitoringOutcomeID) {

    return monitoringOutcomeDAO.existMonitoringOutcome(monitoringOutcomeID);
  }

  @Override
  public List<CenterMonitoringOutcome> findAll() {

    return monitoringOutcomeDAO.findAll();

  }

  @Override
  public CenterMonitoringOutcome getMonitoringOutcomeById(long monitoringOutcomeID) {

    return monitoringOutcomeDAO.find(monitoringOutcomeID);
  }

  @Override
  public List<CenterMonitoringOutcome> getMonitoringOutcomesByUserId(Long userId) {
    return monitoringOutcomeDAO.getMonitoringOutcomesByUserId(userId);
  }

  @Override
  public long saveMonitoringOutcome(CenterMonitoringOutcome monitoringOutcome) {

    return monitoringOutcomeDAO.save(monitoringOutcome);
  }


}
