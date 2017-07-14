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


package org.cgiar.ccafs.marlo.data.dao.mysql;

import org.cgiar.ccafs.marlo.data.dao.ICenterMonitoringMilestoneDAO;
import org.cgiar.ccafs.marlo.data.model.CenterMonitoringMilestone;

import java.util.List;

import com.google.inject.Inject;

public class CenterMonitoringMilestoneDAO implements ICenterMonitoringMilestoneDAO {

  private StandardDAO dao;

  @Inject
  public CenterMonitoringMilestoneDAO(StandardDAO dao) {
    this.dao = dao;
  }

  @Override
  public boolean deleteMonitoringMilestone(long monitoringMilestoneId) {
    CenterMonitoringMilestone monitoringMilestone = this.find(monitoringMilestoneId);
    monitoringMilestone.setActive(false);
    return this.save(monitoringMilestone) > 0;
  }

  @Override
  public boolean existMonitoringMilestone(long monitoringMilestoneID) {
    CenterMonitoringMilestone monitoringMilestone = this.find(monitoringMilestoneID);
    if (monitoringMilestone == null) {
      return false;
    }
    return true;

  }

  @Override
  public CenterMonitoringMilestone find(long id) {
    return dao.find(CenterMonitoringMilestone.class, id);

  }

  @Override
  public List<CenterMonitoringMilestone> findAll() {
    String query = "from " + CenterMonitoringMilestone.class.getName();
    List<CenterMonitoringMilestone> list = dao.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<CenterMonitoringMilestone> getMonitoringMilestonesByUserId(long userId) {
    String query = "from " + CenterMonitoringMilestone.class.getName() + " where user_id=" + userId;
    return dao.findAll(query);
  }

  @Override
  public long save(CenterMonitoringMilestone monitoringMilestone) {
    if (monitoringMilestone.getId() == null) {
      dao.save(monitoringMilestone);
    } else {
      dao.update(monitoringMilestone);
    }
    return monitoringMilestone.getId();
  }


}