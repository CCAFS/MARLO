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

import org.cgiar.ccafs.marlo.data.dao.ICenterRegionDAO;
import org.cgiar.ccafs.marlo.data.model.CenterRegion;

import java.util.List;

import com.google.inject.Inject;
import org.hibernate.SessionFactory;

public class CenterRegionDAO extends AbstractMarloDAO<CenterRegion, Long> implements ICenterRegionDAO {


  @Inject
  public CenterRegionDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteResearchRegion(long researchRegionId) {
    CenterRegion researchRegion = this.find(researchRegionId);
    researchRegion.setActive(false);
    this.save(researchRegion);
  }

  @Override
  public boolean existResearchRegion(long researchRegionID) {
    CenterRegion researchRegion = this.find(researchRegionID);
    if (researchRegion == null) {
      return false;
    }
    return true;

  }

  @Override
  public CenterRegion find(long id) {
    return super.find(CenterRegion.class, id);

  }

  @Override
  public List<CenterRegion> findAll() {
    String query = "from " + CenterRegion.class.getName();
    List<CenterRegion> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<CenterRegion> getResearchRegionsByUserId(long userId) {
    String query = "from " + CenterRegion.class.getName() + " where user_id=" + userId;
    return super.findAll(query);
  }

  @Override
  public CenterRegion save(CenterRegion researchRegion) {
    if (researchRegion.getId() == null) {
      super.saveEntity(researchRegion);
    } else {
      researchRegion = super.update(researchRegion);
    }
    return researchRegion;
  }


}