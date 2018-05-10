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

import org.cgiar.ccafs.marlo.data.dao.RepIndFillingTypeDAO;
import org.cgiar.ccafs.marlo.data.model.RepIndFillingType;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class RepIndFillingTypeMySQLDAO extends AbstractMarloDAO<RepIndFillingType, Long>
  implements RepIndFillingTypeDAO {


  @Inject
  public RepIndFillingTypeMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteRepIndFillingType(long repIndFillingTypeId) {
    RepIndFillingType repIndFillingType = this.find(repIndFillingTypeId);
    this.delete(repIndFillingType);
  }

  @Override
  public boolean existRepIndFillingType(long repIndFillingTypeID) {
    RepIndFillingType repIndFillingType = this.find(repIndFillingTypeID);
    if (repIndFillingType == null) {
      return false;
    }
    return true;

  }

  @Override
  public RepIndFillingType find(long id) {
    return super.find(RepIndFillingType.class, id);

  }

  @Override
  public List<RepIndFillingType> findAll() {
    String query = "from " + RepIndFillingType.class.getName();
    List<RepIndFillingType> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public RepIndFillingType save(RepIndFillingType repIndFillingType) {
    if (repIndFillingType.getId() == null) {
      super.saveEntity(repIndFillingType);
    } else {
      repIndFillingType = super.update(repIndFillingType);
    }


    return repIndFillingType;
  }


}