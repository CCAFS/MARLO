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

import org.cgiar.ccafs.marlo.data.dao.LocGeopositionDAO;
import org.cgiar.ccafs.marlo.data.model.LocGeoposition;

import java.util.List;

import javax.inject.Named;
import javax.inject.Inject;
import org.hibernate.SessionFactory;

@Named
public class LocGeopositionMySQLDAO extends AbstractMarloDAO<LocGeoposition, Long> implements LocGeopositionDAO {


  @Inject
  public LocGeopositionMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteLocGeoposition(long locGeopositionId) {
    LocGeoposition locGeoposition = this.find(locGeopositionId);
    locGeoposition.setActive(false);
    this.save(locGeoposition);
  }

  @Override
  public boolean existLocGeoposition(long locGeopositionID) {
    LocGeoposition locGeoposition = this.find(locGeopositionID);
    if (locGeoposition == null) {
      return false;
    }
    return true;

  }

  @Override
  public LocGeoposition find(long id) {
    return super.find(LocGeoposition.class, id);

  }

  @Override
  public List<LocGeoposition> findAll() {
    String query = "from " + LocGeoposition.class.getName() + " where is_active=1";
    List<LocGeoposition> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public LocGeoposition save(LocGeoposition locGeoposition) {
    if (locGeoposition.getId() == null) {
      super.saveEntity(locGeoposition);
    } else {
      locGeoposition = super.update(locGeoposition);
    }
    return locGeoposition;
  }


}