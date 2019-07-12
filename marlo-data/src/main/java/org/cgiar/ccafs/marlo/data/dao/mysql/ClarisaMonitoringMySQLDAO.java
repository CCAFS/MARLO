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

import org.cgiar.ccafs.marlo.data.dao.ClarisaMonitoringDAO;
import org.cgiar.ccafs.marlo.data.model.ClarisaMonitoring;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ClarisaMonitoringMySQLDAO extends AbstractMarloDAO<ClarisaMonitoring, Long>
  implements ClarisaMonitoringDAO {


  @Inject
  public ClarisaMonitoringMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteClarisaMonitoring(long clarisaMonitoringId) {
    ClarisaMonitoring clarisaMonitoring = this.find(clarisaMonitoringId);
    this.delete(clarisaMonitoring);
  }

  @Override
  public boolean existClarisaMonitoring(long clarisaMonitoringID) {
    ClarisaMonitoring clarisaMonitoring = this.find(clarisaMonitoringID);
    if (clarisaMonitoring == null) {
      return false;
    }
    return true;

  }

  @Override
  public ClarisaMonitoring find(long id) {
    return super.find(ClarisaMonitoring.class, id);

  }

  @Override
  public List<ClarisaMonitoring> findAll() {
    String query = "from " + ClarisaMonitoring.class.getName();
    List<ClarisaMonitoring> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public ClarisaMonitoring save(ClarisaMonitoring clarisaMonitoring) {
    if (clarisaMonitoring.getId() == null) {
      super.saveEntity(clarisaMonitoring);
    } else {
      clarisaMonitoring = super.update(clarisaMonitoring);
    }


    return clarisaMonitoring;
  }


}