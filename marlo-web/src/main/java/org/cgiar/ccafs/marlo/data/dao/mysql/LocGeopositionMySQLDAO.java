/*****************************************************************
 * This file is part of CCAFS Planning and Reporting Platform.
 * CCAFS P&R is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 * CCAFS P&R is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with CCAFS P&R. If not, see <http://www.gnu.org/licenses/>.
 *****************************************************************/


package org.cgiar.ccafs.marlo.data.dao.mysql;

import org.cgiar.ccafs.marlo.data.dao.LocGeopositionDAO;
import org.cgiar.ccafs.marlo.data.model.LocGeoposition;

import java.util.List;

import com.google.inject.Inject;

public class LocGeopositionMySQLDAO implements LocGeopositionDAO {

  private StandardDAO dao;

  @Inject
  public LocGeopositionMySQLDAO(StandardDAO dao) {
    this.dao = dao;
  }

  @Override
  public boolean deleteLocGeoposition(long locGeopositionId) {
    LocGeoposition locGeoposition = this.find(locGeopositionId);

    return dao.delete(locGeoposition);
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
  public long save(LocGeoposition locGeoposition) {
    if (locGeoposition.getId() == null) {
      dao.save(locGeoposition);
    } else {
      dao.update(locGeoposition);
    }
    return locGeoposition.getId();
  }


}