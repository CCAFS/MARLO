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
import org.hibernate.SessionFactory;

public class CapdevHighestDegreeMySQLDAO extends AbstractMarloDAO<CapdevHighestDegree, Long>
  implements CapdevHighestDegreeDAO {


  @Inject
  public CapdevHighestDegreeMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);

  }

  @Override
  public void deleteCapdevHighestDegree(long capdevHighestDegreeId) {
    final CapdevHighestDegree capdevHighestDegree = this.find(capdevHighestDegreeId);
    this.save(capdevHighestDegree);
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
    return super.find(CapdevHighestDegree.class, id);

  }

  @Override
  public List<CapdevHighestDegree> findAll() {
    final String query = "from " + CapdevHighestDegree.class.getName();
    final List<CapdevHighestDegree> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public CapdevHighestDegree save(CapdevHighestDegree capdevHighestDegree) {
    if (capdevHighestDegree.getId() == null) {
      super.saveEntity(capdevHighestDegree);
    } else {
      super.update(capdevHighestDegree);
    }


    return capdevHighestDegree;
  }


}