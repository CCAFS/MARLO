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


package org.cgiar.ccafs.marlo.data.dao.impl;

import org.cgiar.ccafs.marlo.data.dao.ILocGeopositionDAO;
import org.cgiar.ccafs.marlo.data.model.LocGeoposition;

import java.util.List;

import com.google.inject.Inject;

public class LocGeopositionDAO implements ILocGeopositionDAO {

  private StandardDAO dao;

  @Inject
  public LocGeopositionDAO(StandardDAO dao) {
    this.dao = dao;
  }

  @Override
  public boolean deleteLocGeoposition(long locGeopositionId) {
    LocGeoposition locGeoposition = this.find(locGeopositionId);
    locGeoposition.setActive(false);
    return this.save(locGeoposition) > 0;
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
    return dao.find(LocGeoposition.class, id);

  }

  @Override
  public List<LocGeoposition> findAll() {
    String query = "from " + LocGeoposition.class.getName();
    List<LocGeoposition> list = dao.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<LocGeoposition> getLocGeopositionsByUserId(long userId) {
    String query = "from " + LocGeoposition.class.getName() + " where user_id=" + userId;
    return dao.findAll(query);
  }

  @Override
  public long save(LocGeoposition locGeoposition) {
    if (locGeoposition.getId() == null) {
      dao.save(locGeoposition);
    } else {
      dao.update(locGeoposition);
    }
    return locGeoposition.getId();
  }


}