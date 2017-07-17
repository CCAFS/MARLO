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

import org.cgiar.ccafs.marlo.data.dao.ICapdevOutputsDAO;
import org.cgiar.ccafs.marlo.data.model.CapdevOutputs;

import java.util.List;

import com.google.inject.Inject;

public class CapdevOutputsDAO implements ICapdevOutputsDAO {

  private StandardDAO dao;

  @Inject
  public CapdevOutputsDAO(StandardDAO dao) {
    this.dao = dao;
  }

  @Override
  public boolean deleteCapdevOutputs(long capdevOutputsId) {
    CapdevOutputs capdevOutputs = this.find(capdevOutputsId);
    capdevOutputs.setActive(false);
    return this.save(capdevOutputs) > 0;
  }

  @Override
  public boolean existCapdevOutputs(long capdevOutputsID) {
    CapdevOutputs capdevOutputs = this.find(capdevOutputsID);
    if (capdevOutputs == null) {
      return false;
    }
    return true;

  }

  @Override
  public CapdevOutputs find(long id) {
    return dao.find(CapdevOutputs.class, id);

  }

  @Override
  public List<CapdevOutputs> findAll() {
    String query = "from " + CapdevOutputs.class.getName();
    List<CapdevOutputs> list = dao.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<CapdevOutputs> getCapdevOutputssByUserId(long userId) {
    String query = "from " + CapdevOutputs.class.getName() + " where user_id=" + userId;
    return dao.findAll(query);
  }

  @Override
  public long save(CapdevOutputs capdevOutputs) {
    if (capdevOutputs.getId() == null) {
      dao.save(capdevOutputs);
    } else {
      dao.update(capdevOutputs);
    }
    return capdevOutputs.getId();
  }

  @Override
  public long save(CapdevOutputs capdevOutputs, String actionName, List<String> relationsName) {
    if (capdevOutputs.getId() == null) {
      dao.save(capdevOutputs, actionName, relationsName);
    } else {
      dao.update(capdevOutputs, actionName, relationsName);
    }
    return capdevOutputs.getId();
  }


}