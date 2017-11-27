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

import org.cgiar.ccafs.marlo.data.dao.LocElementDAO;
import org.cgiar.ccafs.marlo.data.model.LocElement;

import java.util.List;

import com.google.inject.Inject;
import org.hibernate.SessionFactory;

public class LocElementMySQLDAO extends AbstractMarloDAO<LocElement, Long> implements LocElementDAO {


  @Inject
  public LocElementMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteLocElement(long locElementId) {
    LocElement locElement = this.find(locElementId);
    locElement.setActive(false);
    this.save(locElement);
  }

  @Override
  public boolean existLocElement(long locElementID) {
    LocElement locElement = this.find(locElementID);
    if (locElement == null) {
      return false;
    }
    return true;

  }

  @Override
  public LocElement find(long id) {
    return super.find(LocElement.class, id);

  }

  @Override
  public List<LocElement> findAll() {
    String query = "from " + LocElement.class.getName() + " where is_active=1";
    List<LocElement> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public LocElement findISOCode(String ISOcode) {
    String query = "from " + LocElement.class.getName() + " where iso_alpha_2='" + ISOcode + "'";
    List<LocElement> list = super.findAll(query);
    if (list.size() > 0) {
      return list.get(0);
    }
    return null;
  }

  @Override
  public List<LocElement> findLocElementByParent(Long parentId) {
    String query = "from " + LocElement.class.getName() + " where parent_id='" + parentId + "'";
    List<LocElement> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;
  }

  @Override
  public LocElement save(LocElement locElement) {
    if (locElement.getId() == null) {
      super.saveEntity(locElement);
    } else {
      locElement = super.update(locElement);
    }
    return locElement;
  }


}