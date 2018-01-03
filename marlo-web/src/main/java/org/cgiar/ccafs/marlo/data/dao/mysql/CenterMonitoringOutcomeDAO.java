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

import org.cgiar.ccafs.marlo.data.dao.ICenterMonitoringOutcomeDAO;
import org.cgiar.ccafs.marlo.data.model.CenterMonitoringOutcome;

import java.util.List;

import javax.inject.Named;
import javax.inject.Inject;
import org.hibernate.SessionFactory;

@Named
public class CenterMonitoringOutcomeDAO extends AbstractMarloDAO<CenterMonitoringOutcome, Long>
  implements ICenterMonitoringOutcomeDAO {


  @Inject
  public CenterMonitoringOutcomeDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteMonitoringOutcome(long monitoringOutcomeId) {
    CenterMonitoringOutcome monitoringOutcome = this.find(monitoringOutcomeId);
    monitoringOutcome.setActive(false);
    this.save(monitoringOutcome);
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
    return super.find(CenterMonitoringOutcome.class, id);

  }

  @Override
  public List<CenterMonitoringOutcome> findAll() {
    String query = "from " + CenterMonitoringOutcome.class.getName();
    List<CenterMonitoringOutcome> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<CenterMonitoringOutcome> getMonitoringOutcomesByUserId(long userId) {
    String query = "from " + CenterMonitoringOutcome.class.getName() + " where user_id=" + userId;
    return super.findAll(query);
  }

  @Override
  public CenterMonitoringOutcome save(CenterMonitoringOutcome monitoringOutcome) {
    if (monitoringOutcome.getId() == null) {
      super.saveEntity(monitoringOutcome);
    } else {
      monitoringOutcome = super.update(monitoringOutcome);
    }
    return monitoringOutcome;
  }


}