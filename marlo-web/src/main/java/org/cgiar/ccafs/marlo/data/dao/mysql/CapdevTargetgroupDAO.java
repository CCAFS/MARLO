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

import org.cgiar.ccafs.marlo.data.dao.ICapdevTargetgroupDAO;
import org.cgiar.ccafs.marlo.data.model.CapdevTargetgroup;

import java.util.List;

import javax.inject.Inject;

import org.hibernate.SessionFactory;

public class CapdevTargetgroupDAO extends AbstractMarloDAO<CapdevTargetgroup, Long> implements ICapdevTargetgroupDAO {


  @Inject
  public CapdevTargetgroupDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteCapdevTargetgroup(long capdevTargetgroupId) {
    CapdevTargetgroup capdevTargetgroup = this.find(capdevTargetgroupId);
    capdevTargetgroup.setActive(false);
    this.save(capdevTargetgroup);
  }

  @Override
  public boolean existCapdevTargetgroup(long capdevTargetgroupID) {
    CapdevTargetgroup capdevTargetgroup = this.find(capdevTargetgroupID);
    if (capdevTargetgroup == null) {
      return false;
    }
    return true;

  }

  @Override
  public CapdevTargetgroup find(long id) {
    return super.find(CapdevTargetgroup.class, id);

  }

  @Override
  public List<CapdevTargetgroup> findAll() {
    String query = "from " + CapdevTargetgroup.class.getName();
    List<CapdevTargetgroup> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<CapdevTargetgroup> getCapdevTargetgroupsByUserId(long userId) {
    String query = "from " + CapdevTargetgroup.class.getName() + " where user_id=" + userId;
    return super.findAll(query);
  }

  @Override
  public CapdevTargetgroup save(CapdevTargetgroup capdevTargetgroup) {
    if (capdevTargetgroup.getId() == null) {
      super.saveEntity(capdevTargetgroup);
    } else {
      super.update(capdevTargetgroup);
    }
    return capdevTargetgroup;
  }

  @Override
  public CapdevTargetgroup save(CapdevTargetgroup capdevTargetgroup, String actionName, List<String> relationsName) {
    if (capdevTargetgroup.getId() == null) {
      super.saveEntity(capdevTargetgroup, actionName, relationsName);
    } else {
      super.update(capdevTargetgroup, actionName, relationsName);
    }
    return capdevTargetgroup;
  }


}