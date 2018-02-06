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

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class CapdevDisciplineDAO extends AbstractMarloDAO<CapdevDiscipline, Long> implements ICapdevDisciplineDAO {


  @Inject
  public CapdevDisciplineDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteCapdevDiscipline(long capdevDisciplineId) {
    CapdevDiscipline capdevDiscipline = this.find(capdevDisciplineId);
    capdevDiscipline.setActive(false);
    this.save(capdevDiscipline);
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
    return super.find(CapdevDiscipline.class, id);

  }

  @Override
  public List<CapdevDiscipline> findAll() {
    String query = "from " + CapdevDiscipline.class.getName();
    List<CapdevDiscipline> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<CapdevDiscipline> getCapdevDisciplinesByUserId(long userId) {
    String query = "from " + CapdevDiscipline.class.getName() + " where user_id=" + userId;
    return super.findAll(query);
  }

  @Override
  public CapdevDiscipline save(CapdevDiscipline capdevDiscipline) {
    if (capdevDiscipline.getId() == null) {
      super.saveEntity(capdevDiscipline);
    } else {
      super.update(capdevDiscipline);
    }
    return capdevDiscipline;
  }

  @Override
  public CapdevDiscipline save(CapdevDiscipline capdevDiscipline, String actionName, List<String> relationsName) {
    if (capdevDiscipline.getId() == null) {
      super.saveEntity(capdevDiscipline, actionName, relationsName);
    } else {
      super.update(capdevDiscipline, actionName, relationsName);
    }
    return capdevDiscipline;
  }


}