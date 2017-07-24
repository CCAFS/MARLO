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

public class CenterRegionDAO implements ICenterRegionDAO {

  private StandardDAO dao;

  @Inject
  public CenterRegionDAO(StandardDAO dao) {
    this.dao = dao;
  }

  @Override
  public boolean deleteResearchRegion(long researchRegionId) {
    CenterRegion researchRegion = this.find(researchRegionId);
    researchRegion.setActive(false);
    return this.save(researchRegion) > 0;
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
    return dao.find(CenterRegion.class, id);

  }

  @Override
  public List<CenterRegion> findAll() {
    String query = "from " + CenterRegion.class.getName();
    List<CenterRegion> list = dao.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<CenterRegion> getResearchRegionsByUserId(long userId) {
    String query = "from " + CenterRegion.class.getName() + " where user_id=" + userId;
    return dao.findAll(query);
  }

  @Override
  public long save(CenterRegion researchRegion) {
    if (researchRegion.getId() == null) {
      dao.save(researchRegion);
    } else {
      dao.update(researchRegion);
    }
    return researchRegion.getId();
  }


}