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

import org.cgiar.ccafs.marlo.data.dao.CrpLocElementTypeDAO;
import org.cgiar.ccafs.marlo.data.model.CrpLocElementType;

import java.util.List;

import javax.inject.Named;
import javax.inject.Inject;
import org.hibernate.SessionFactory;

@Named
public class CrpLocElementTypeMySQLDAO extends AbstractMarloDAO<CrpLocElementType, Long> implements CrpLocElementTypeDAO {


  @Inject
  public CrpLocElementTypeMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteCrpLocElementType(long crpLocElementTypeId) {
    CrpLocElementType crpLocElementType = this.find(crpLocElementTypeId);

    super.delete(crpLocElementType);
  }

  @Override
  public boolean existCrpLocElementType(long crpLocElementTypeID) {
    CrpLocElementType crpLocElementType = this.find(crpLocElementTypeID);
    if (crpLocElementType == null) {
      return false;
    }
    return true;

  }

  @Override
  public CrpLocElementType find(long id) {
    return super.find(CrpLocElementType.class, id);

  }

  @Override
  public List<CrpLocElementType> findAll() {
    String query = "from " + CrpLocElementType.class.getName() + " ";

    List<CrpLocElementType> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public CrpLocElementType getByLocElementTypeAndCrpId(long crpId, long locElementTypeID) {
    String query = "from " + CrpLocElementType.class.getName() + " where crp_id=" + crpId + " and loc_element_type_id="
      + locElementTypeID;
    List<CrpLocElementType> list = super.findAll(query);
    if (list.size() > 0) {
      return list.get(0);
    }
    return null;
  }

  @Override
  public CrpLocElementType save(CrpLocElementType crpLocElementType) {
    if (crpLocElementType.getId() == null) {
      super.saveEntity(crpLocElementType);
    } else {
      crpLocElementType = super.update(crpLocElementType);
    }


    return crpLocElementType;
  }


}