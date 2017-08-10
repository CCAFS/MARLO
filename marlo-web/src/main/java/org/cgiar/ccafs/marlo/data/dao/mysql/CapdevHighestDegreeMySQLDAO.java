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

import org.cgiar.ccafs.marlo.data.dao.CapdevHighestDegreeDAO;
import org.cgiar.ccafs.marlo.data.model.CapdevHighestDegree;

import java.util.List;

import com.google.inject.Inject;

public class CapdevHighestDegreeMySQLDAO implements CapdevHighestDegreeDAO {

  private final StandardDAO dao;

  @Inject
  public CapdevHighestDegreeMySQLDAO(StandardDAO dao) {
    this.dao = dao;
  }

  @Override
  public boolean deleteCapdevHighestDegree(long capdevHighestDegreeId) {
    final CapdevHighestDegree capdevHighestDegree = this.find(capdevHighestDegreeId);
    return this.save(capdevHighestDegree) > 0;
  }

  @Override
  public boolean existCapdevHighestDegree(long capdevHighestDegreeID) {
    final CapdevHighestDegree capdevHighestDegree = this.find(capdevHighestDegreeID);
    if (capdevHighestDegree == null) {
      return false;
    }
    return true;

  }

  @Override
  public CapdevHighestDegree find(long id) {
    return dao.find(CapdevHighestDegree.class, id);

  }

  @Override
  public List<CapdevHighestDegree> findAll() {
    final String query = "from " + CapdevHighestDegree.class.getName();
    final List<CapdevHighestDegree> list = dao.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public long save(CapdevHighestDegree capdevHighestDegree) {
    if (capdevHighestDegree.getId() == null) {
      dao.save(capdevHighestDegree);
    } else {
      dao.update(capdevHighestDegree);
    }


    return capdevHighestDegree.getId();
  }


}