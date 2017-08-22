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

import org.cgiar.ccafs.marlo.data.dao.GenderTypeDAO;
import org.cgiar.ccafs.marlo.data.model.GenderType;

import java.util.List;

import com.google.inject.Inject;
import org.hibernate.SessionFactory;

public class GenderTypeMySQLDAO extends AbstractMarloDAO<GenderType, Long> implements GenderTypeDAO {


  @Inject
  public GenderTypeMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteGenderType(long genderTypeId) {
    GenderType genderType = this.find(genderTypeId);

    super.delete(genderType);
  }

  @Override
  public boolean existGenderType(long genderTypeID) {
    GenderType genderType = this.find(genderTypeID);
    if (genderType == null) {
      return false;
    }
    return true;

  }

  @Override
  public GenderType find(long id) {
    return super.find(GenderType.class, id);

  }

  @Override
  public List<GenderType> findAll() {
    String query = "from " + GenderType.class.getName() + "";
    List<GenderType> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public GenderType save(GenderType genderType) {
    if (genderType.getId() == null) {
      super.saveEntity(genderType);
    } else {
      genderType = super.update(genderType);
    }


    return genderType;
  }


}