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

import org.cgiar.ccafs.marlo.data.dao.CrpClusterActivityLeaderDAO;
import org.cgiar.ccafs.marlo.data.model.CrpClusterActivityLeader;

import java.util.List;

import javax.inject.Named;
import javax.inject.Inject;
import org.hibernate.SessionFactory;

@Named
public class CrpClusterActivityLeaderMySQLDAO extends AbstractMarloDAO<CrpClusterActivityLeader, Long> implements CrpClusterActivityLeaderDAO {


  @Inject
  public CrpClusterActivityLeaderMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteCrpClusterActivityLeader(long crpClusterActivityLeaderId) {
    CrpClusterActivityLeader crpClusterActivityLeader = this.find(crpClusterActivityLeaderId);
    crpClusterActivityLeader.setActive(false);
    this.save(crpClusterActivityLeader);
  }

  @Override
  public boolean existCrpClusterActivityLeader(long crpClusterActivityLeaderID) {
    CrpClusterActivityLeader crpClusterActivityLeader = this.find(crpClusterActivityLeaderID);
    if (crpClusterActivityLeader == null) {
      return false;
    }
    return true;

  }

  @Override
  public CrpClusterActivityLeader find(long id) {
    return super.find(CrpClusterActivityLeader.class, id);

  }

  @Override
  public List<CrpClusterActivityLeader> findAll() {
    String query = "from " + CrpClusterActivityLeader.class.getName() + " where is_active=1";
    List<CrpClusterActivityLeader> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public CrpClusterActivityLeader save(CrpClusterActivityLeader crpClusterActivityLeader) {
    if (crpClusterActivityLeader.getId() == null) {
      super.saveEntity(crpClusterActivityLeader);
    } else {
      crpClusterActivityLeader = super.update(crpClusterActivityLeader);
    }


    return crpClusterActivityLeader;
  }


}