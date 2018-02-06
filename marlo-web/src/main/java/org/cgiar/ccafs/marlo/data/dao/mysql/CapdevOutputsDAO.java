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

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class CapdevOutputsDAO extends AbstractMarloDAO<CapdevOutputs, Long> implements ICapdevOutputsDAO {


  @Inject
  public CapdevOutputsDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteCapdevOutputs(long capdevOutputsId) {
    CapdevOutputs capdevOutputs = this.find(capdevOutputsId);
    capdevOutputs.setActive(false);
    this.save(capdevOutputs);
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
    return super.find(CapdevOutputs.class, id);

  }

  @Override
  public List<CapdevOutputs> findAll() {
    String query = "from " + CapdevOutputs.class.getName();
    List<CapdevOutputs> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<CapdevOutputs> getCapdevOutputssByUserId(long userId) {
    String query = "from " + CapdevOutputs.class.getName() + " where user_id=" + userId;
    return super.findAll(query);
  }

  @Override
  public CapdevOutputs save(CapdevOutputs capdevOutputs) {
    if (capdevOutputs.getId() == null) {
      super.saveEntity(capdevOutputs);
    } else {
      super.update(capdevOutputs);
    }
    return capdevOutputs;
  }

  @Override
  public CapdevOutputs save(CapdevOutputs capdevOutputs, String actionName, List<String> relationsName) {
    if (capdevOutputs.getId() == null) {
      super.saveEntity(capdevOutputs, actionName, relationsName);
    } else {
      super.update(capdevOutputs, actionName, relationsName);
    }
    return capdevOutputs;
  }


}