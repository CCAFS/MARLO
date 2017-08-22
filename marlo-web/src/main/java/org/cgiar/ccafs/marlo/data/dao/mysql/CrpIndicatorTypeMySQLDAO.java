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

import org.cgiar.ccafs.marlo.data.dao.CrpIndicatorTypeDAO;
import org.cgiar.ccafs.marlo.data.model.CrpIndicatorType;

import java.util.List;

import com.google.inject.Inject;
import org.hibernate.SessionFactory;

public class CrpIndicatorTypeMySQLDAO extends AbstractMarloDAO<CrpIndicatorType, Long> implements CrpIndicatorTypeDAO {


  @Inject
  public CrpIndicatorTypeMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteCrpIndicatorType(long crpIndicatorTypeId) {
    CrpIndicatorType crpIndicatorType = this.find(crpIndicatorTypeId);
    super.delete(crpIndicatorType);
  }

  @Override
  public boolean existCrpIndicatorType(long crpIndicatorTypeID) {
    CrpIndicatorType crpIndicatorType = this.find(crpIndicatorTypeID);
    if (crpIndicatorType == null) {
      return false;
    }
    return true;

  }

  @Override
  public CrpIndicatorType find(long id) {
    return super.find(CrpIndicatorType.class, id);

  }

  @Override
  public List<CrpIndicatorType> findAll() {
    String query = "from " + CrpIndicatorType.class.getName();
    List<CrpIndicatorType> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public CrpIndicatorType save(CrpIndicatorType crpIndicatorType) {
    if (crpIndicatorType.getId() == null) {
      super.saveEntity(crpIndicatorType);
    } else {
      crpIndicatorType = super.update(crpIndicatorType);
    }


    return crpIndicatorType;
  }


}