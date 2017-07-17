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

import org.cgiar.ccafs.marlo.data.dao.ICapdevDisciplineDAO;
import org.cgiar.ccafs.marlo.data.model.CapdevDiscipline;

import java.util.List;

import com.google.inject.Inject;

public class CapdevDisciplineDAO implements ICapdevDisciplineDAO {

  private StandardDAO dao;

  @Inject
  public CapdevDisciplineDAO(StandardDAO dao) {
    this.dao = dao;
  }

  @Override
  public boolean deleteCapdevDiscipline(long capdevDisciplineId) {
    CapdevDiscipline capdevDiscipline = this.find(capdevDisciplineId);
    capdevDiscipline.setActive(false);
    return this.save(capdevDiscipline) > 0;
  }

  @Override
  public boolean existCapdevDiscipline(long capdevDisciplineID) {
    CapdevDiscipline capdevDiscipline = this.find(capdevDisciplineID);
    if (capdevDiscipline == null) {
      return false;
    }
    return true;

  }

  @Override
  public CapdevDiscipline find(long id) {
    return dao.find(CapdevDiscipline.class, id);

  }

  @Override
  public List<CapdevDiscipline> findAll() {
    String query = "from " + CapdevDiscipline.class.getName();
    List<CapdevDiscipline> list = dao.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<CapdevDiscipline> getCapdevDisciplinesByUserId(long userId) {
    String query = "from " + CapdevDiscipline.class.getName() + " where user_id=" + userId;
    return dao.findAll(query);
  }

  @Override
  public long save(CapdevDiscipline capdevDiscipline) {
    if (capdevDiscipline.getId() == null) {
      dao.save(capdevDiscipline);
    } else {
      dao.update(capdevDiscipline);
    }
    return capdevDiscipline.getId();
  }

  @Override
  public long save(CapdevDiscipline capdevDiscipline, String actionName, List<String> relationsName) {
    if (capdevDiscipline.getId() == null) {
      dao.save(capdevDiscipline, actionName, relationsName);
    } else {
      dao.update(capdevDiscipline, actionName, relationsName);
    }
    return capdevDiscipline.getId();
  }


}