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

/**************
 * @author Diego Perez - CIAT/CCAFS
 **************/

package org.cgiar.ccafs.marlo.data.dao.mysql;

import org.cgiar.ccafs.marlo.data.dao.OneCGIARStudyTypeDAO;
import org.cgiar.ccafs.marlo.data.model.OneCGIARStudyType;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;


@Named
public class OneCGIARStudyTypeMySQLDAO extends AbstractMarloDAO<OneCGIARStudyType, Long>
  implements OneCGIARStudyTypeDAO {

  @Inject
  public OneCGIARStudyTypeMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteOneCGIARStudyType(long oneCGIARStudyTypeId) {
    OneCGIARStudyType oneCGIARStudyType = this.getStudyTypeById(oneCGIARStudyTypeId);
    this.delete(oneCGIARStudyType);

  }

  @Override
  public boolean existOneCGIARStudyType(long oneCGIARStudyTypeID) {
    OneCGIARStudyType oneCGIARStudyType = this.getStudyTypeById(oneCGIARStudyTypeID);
    if (oneCGIARStudyType == null) {
      return false;
    }
    return true;
  }

  @Override
  public List<OneCGIARStudyType> getAll() {
    String query = "from " + OneCGIARStudyType.class.getName() + " ORDER BY norder asc";
    List<OneCGIARStudyType> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;
  }

  @Override
  public OneCGIARStudyType getStudyTypeById(long id) {
    return super.find(OneCGIARStudyType.class, id);
  }

  @Override
  public OneCGIARStudyType save(OneCGIARStudyType oneCGIARStudyType) {
    if (oneCGIARStudyType.getId() == null) {
      super.saveEntity(oneCGIARStudyType);
    } else {
      oneCGIARStudyType = super.update(oneCGIARStudyType);
    }
    return oneCGIARStudyType;
  }

}
