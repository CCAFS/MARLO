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

import com.google.inject.Inject;

public class CapdevTargetgroupDAO implements ICapdevTargetgroupDAO {

  private StandardDAO dao;

  @Inject
  public CapdevTargetgroupDAO(StandardDAO dao) {
    this.dao = dao;
  }

  @Override
  public boolean deleteCapdevTargetgroup(long capdevTargetgroupId) {
    CapdevTargetgroup capdevTargetgroup = this.find(capdevTargetgroupId);
    capdevTargetgroup.setActive(false);
    return this.save(capdevTargetgroup) > 0;
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
    return dao.find(CapdevTargetgroup.class, id);

  }

  @Override
  public List<CapdevTargetgroup> findAll() {
    String query = "from " + CapdevTargetgroup.class.getName();
    List<CapdevTargetgroup> list = dao.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<CapdevTargetgroup> getCapdevTargetgroupsByUserId(long userId) {
    String query = "from " + CapdevTargetgroup.class.getName() + " where user_id=" + userId;
    return dao.findAll(query);
  }

  @Override
  public long save(CapdevTargetgroup capdevTargetgroup) {
    if (capdevTargetgroup.getId() == null) {
      dao.save(capdevTargetgroup);
    } else {
      dao.update(capdevTargetgroup);
    }
    return capdevTargetgroup.getId();
  }

  @Override
  public long save(CapdevTargetgroup capdevTargetgroup, String actionName, List<String> relationsName) {
    if (capdevTargetgroup.getId() == null) {
      dao.save(capdevTargetgroup, actionName, relationsName);
    } else {
      dao.update(capdevTargetgroup, actionName, relationsName);
    }
    return capdevTargetgroup.getId();
  }


}