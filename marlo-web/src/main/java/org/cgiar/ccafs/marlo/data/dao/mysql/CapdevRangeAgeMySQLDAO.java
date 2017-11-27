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

import org.cgiar.ccafs.marlo.data.dao.CapdevRangeAgeDAO;
import org.cgiar.ccafs.marlo.data.model.CapdevRangeAge;

import java.util.List;

import com.google.inject.Inject;
import org.hibernate.SessionFactory;

public class CapdevRangeAgeMySQLDAO extends AbstractMarloDAO<CapdevRangeAge, Long> implements CapdevRangeAgeDAO {


  @Inject
  public CapdevRangeAgeMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteCapdevRangeAge(long capdevRangeAgeId) {
    CapdevRangeAge capdevRangeAge = this.find(capdevRangeAgeId);
    capdevRangeAge.setActive(false);
    this.save(capdevRangeAge);
  }

  @Override
  public boolean existCapdevRangeAge(long capdevRangeAgeID) {
    CapdevRangeAge capdevRangeAge = this.find(capdevRangeAgeID);
    if (capdevRangeAge == null) {
      return false;
    }
    return true;

  }

  @Override
  public CapdevRangeAge find(long id) {
    return super.find(CapdevRangeAge.class, id);

  }

  @Override
  public List<CapdevRangeAge> findAll() {
    String query = "from " + CapdevRangeAge.class.getName() + " where is_active=1";
    List<CapdevRangeAge> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public CapdevRangeAge save(CapdevRangeAge capdevRangeAge) {
    if (capdevRangeAge.getId() == null) {
      super.saveEntity(capdevRangeAge);
    } else {
      capdevRangeAge = super.update(capdevRangeAge);
    }


    return capdevRangeAge;
  }


}