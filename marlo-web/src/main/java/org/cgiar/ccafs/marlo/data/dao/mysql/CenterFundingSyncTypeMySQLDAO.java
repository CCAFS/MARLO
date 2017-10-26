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

import org.cgiar.ccafs.marlo.data.dao.CenterFundingSyncTypeDAO;
import org.cgiar.ccafs.marlo.data.model.CenterFundingSyncType;

import java.util.List;

import com.google.inject.Inject;
import org.hibernate.SessionFactory;

public class CenterFundingSyncTypeMySQLDAO extends AbstractMarloDAO<CenterFundingSyncType, Long>
  implements CenterFundingSyncTypeDAO {


  @Inject
  public CenterFundingSyncTypeMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public boolean deleteCenterFundingSyncType(long centerFundingSyncTypeId) {
    CenterFundingSyncType centerFundingSyncType = this.find(centerFundingSyncTypeId);
    centerFundingSyncType.setActive(false);
    return this.save(centerFundingSyncType) > 0;
  }

  @Override
  public boolean existCenterFundingSyncType(long centerFundingSyncTypeID) {
    CenterFundingSyncType centerFundingSyncType = this.find(centerFundingSyncTypeID);
    if (centerFundingSyncType == null) {
      return false;
    }
    return true;

  }

  @Override
  public CenterFundingSyncType find(long id) {
    return super.find(CenterFundingSyncType.class, id);

  }

  @Override
  public List<CenterFundingSyncType> findAll() {
    String query = "from " + CenterFundingSyncType.class.getName() + " where is_active=1";
    List<CenterFundingSyncType> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public long save(CenterFundingSyncType centerFundingSyncType) {
    if (centerFundingSyncType.getId() == null) {
      super.saveEntity(centerFundingSyncType);
    } else {
      super.update(centerFundingSyncType);
    }


    return centerFundingSyncType.getId();
  }


}