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

import org.cgiar.ccafs.marlo.data.dao.CrpIndicatorDAO;
import org.cgiar.ccafs.marlo.data.model.CrpIndicator;

import java.util.List;

import com.google.inject.Inject;
import org.hibernate.SessionFactory;

public class CrpIndicatorMySQLDAO extends AbstractMarloDAO implements CrpIndicatorDAO {


  @Inject
  public CrpIndicatorMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public boolean deleteCrpIndicator(long crpIndicatorId) {
    CrpIndicator crpIndicator = this.find(crpIndicatorId);
    crpIndicator.setActive(false);
    return this.save(crpIndicator) > 0;
  }

  @Override
  public boolean existCrpIndicator(long crpIndicatorID) {
    CrpIndicator crpIndicator = this.find(crpIndicatorID);
    if (crpIndicator == null) {
      return false;
    }
    return true;

  }

  @Override
  public CrpIndicator find(long id) {
    return super.find(CrpIndicator.class, id);

  }

  @Override
  public List<CrpIndicator> findAll() {
    String query = "from " + CrpIndicator.class.getName() + " where is_active=1";
    List<CrpIndicator> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public long save(CrpIndicator crpIndicator) {
    if (crpIndicator.getId() == null) {
      super.save(crpIndicator);
    } else {
      super.update(crpIndicator);
    }


    return crpIndicator.getId();
  }


}