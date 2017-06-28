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


package org.cgiar.ccafs.marlo.data.dao.impl;

import org.cgiar.ccafs.marlo.data.dao.ICenterMonitoringOutcomeDAO;
import org.cgiar.ccafs.marlo.data.model.CenterMonitoringOutcome;

import java.util.List;

import com.google.inject.Inject;

public class CenterMonitoringOutcomeDAO implements ICenterMonitoringOutcomeDAO {

  private StandardDAO dao;

  @Inject
  public CenterMonitoringOutcomeDAO(StandardDAO dao) {
    this.dao = dao;
  }

  @Override
  public boolean deleteMonitoringOutcome(long monitoringOutcomeId) {
    CenterMonitoringOutcome monitoringOutcome = this.find(monitoringOutcomeId);
    monitoringOutcome.setActive(false);
    return this.save(monitoringOutcome) > 0;
  }

  @Override
  public boolean existMonitoringOutcome(long monitoringOutcomeID) {
    CenterMonitoringOutcome monitoringOutcome = this.find(monitoringOutcomeID);
    if (monitoringOutcome == null) {
      return false;
    }
    return true;

  }

  @Override
  public CenterMonitoringOutcome find(long id) {
    return dao.find(CenterMonitoringOutcome.class, id);

  }

  @Override
  public List<CenterMonitoringOutcome> findAll() {
    String query = "from " + CenterMonitoringOutcome.class.getName();
    List<CenterMonitoringOutcome> list = dao.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<CenterMonitoringOutcome> getMonitoringOutcomesByUserId(long userId) {
    String query = "from " + CenterMonitoringOutcome.class.getName() + " where user_id=" + userId;
    return dao.findAll(query);
  }

  @Override
  public long save(CenterMonitoringOutcome monitoringOutcome) {
    if (monitoringOutcome.getId() == null) {
      dao.save(monitoringOutcome);
    } else {
      dao.update(monitoringOutcome);
    }
    return monitoringOutcome.getId();
  }


}