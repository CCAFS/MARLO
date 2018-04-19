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

import org.cgiar.ccafs.marlo.data.dao.RepIndLandUseTypeDAO;
import org.cgiar.ccafs.marlo.data.model.RepIndLandUseType;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class RepIndLandUseTypeMySQLDAO extends AbstractMarloDAO<RepIndLandUseType, Long>
  implements RepIndLandUseTypeDAO {


  @Inject
  public RepIndLandUseTypeMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteRepIndLandUseType(long repIndLandUseTypeId) {
    RepIndLandUseType repIndLandUseType = this.find(repIndLandUseTypeId);
    this.delete(repIndLandUseType);
  }

  @Override
  public boolean existRepIndLandUseType(long repIndLandUseTypeID) {
    RepIndLandUseType repIndLandUseType = this.find(repIndLandUseTypeID);
    if (repIndLandUseType == null) {
      return false;
    }
    return true;

  }

  @Override
  public RepIndLandUseType find(long id) {
    return super.find(RepIndLandUseType.class, id);

  }

  @Override
  public List<RepIndLandUseType> findAll() {
    String query = "from " + RepIndLandUseType.class.getName();
    List<RepIndLandUseType> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public RepIndLandUseType save(RepIndLandUseType repIndLandUseType) {
    if (repIndLandUseType.getId() == null) {
      super.saveEntity(repIndLandUseType);
    } else {
      repIndLandUseType = super.update(repIndLandUseType);
    }


    return repIndLandUseType;
  }


}