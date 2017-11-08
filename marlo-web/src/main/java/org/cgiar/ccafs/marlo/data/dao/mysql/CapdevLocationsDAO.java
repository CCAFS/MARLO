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

import org.cgiar.ccafs.marlo.data.dao.ICapdevLocationsDAO;
import org.cgiar.ccafs.marlo.data.model.CapdevLocations;

import java.util.List;

import com.google.inject.Inject;
import org.hibernate.SessionFactory;

public class CapdevLocationsDAO extends AbstractMarloDAO<CapdevLocations, Long> implements ICapdevLocationsDAO {


  @Inject
  public CapdevLocationsDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteCapdevLocations(long capdevLocationsId) {
    CapdevLocations capdevLocations = this.find(capdevLocationsId);
    capdevLocations.setActive(false);
    this.save(capdevLocations);
  }

  @Override
  public boolean existCapdevLocations(long capdevLocationsID) {
    CapdevLocations capdevLocations = this.find(capdevLocationsID);
    if (capdevLocations == null) {
      return false;
    }
    return true;

  }

  @Override
  public CapdevLocations find(long id) {
    return super.find(CapdevLocations.class, id);

  }

  @Override
  public List<CapdevLocations> findAll() {
    String query = "from " + CapdevLocations.class.getName();
    List<CapdevLocations> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<CapdevLocations> getCapdevLocationssByUserId(long userId) {
    String query = "from " + CapdevLocations.class.getName() + " where user_id=" + userId;
    return super.findAll(query);
  }

  @Override
  public CapdevLocations save(CapdevLocations capdevLocations) {
    if (capdevLocations.getId() == null) {
      super.saveEntity(capdevLocations);
    } else {
      super.update(capdevLocations);
    }
    return capdevLocations;
  }

  @Override
  public CapdevLocations save(CapdevLocations capdevLocations, String actionName, List<String> relationsName) {
    if (capdevLocations.getId() == null) {
      super.saveEntity(capdevLocations, actionName, relationsName);
    } else {
      super.update(capdevLocations, actionName, relationsName);
    }
    return capdevLocations;
  }


}