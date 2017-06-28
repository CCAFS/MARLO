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


package org.cgiar.ccafs.marlo.data.dao.impl;

import org.cgiar.ccafs.marlo.data.dao.ICenterImpactObjectiveDAO;
import org.cgiar.ccafs.marlo.data.model.CenterImpactObjective;

import java.util.List;

import com.google.inject.Inject;

public class CenterImpactObjectiveDAO implements ICenterImpactObjectiveDAO {

  private StandardDAO dao;

  @Inject
  public CenterImpactObjectiveDAO(StandardDAO dao) {
    this.dao = dao;
  }

  @Override
  public boolean deleteResearchImpactObjective(long researchImpactObjectiveId) {
    CenterImpactObjective researchImpactObjective = this.find(researchImpactObjectiveId);
    researchImpactObjective.setActive(false);
    return this.save(researchImpactObjective) > 0;
  }

  @Override
  public boolean existResearchImpactObjective(long researchImpactObjectiveID) {
    CenterImpactObjective researchImpactObjective = this.find(researchImpactObjectiveID);
    if (researchImpactObjective == null) {
      return false;
    }
    return true;

  }

  @Override
  public CenterImpactObjective find(long id) {
    return dao.find(CenterImpactObjective.class, id);

  }

  @Override
  public List<CenterImpactObjective> findAll() {
    String query = "from " + CenterImpactObjective.class.getName();
    List<CenterImpactObjective> list = dao.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<CenterImpactObjective> getResearchImpactObjectivesByUserId(long userId) {
    String query = "from " + CenterImpactObjective.class.getName() + " where user_id=" + userId;
    return dao.findAll(query);
  }

  @Override
  public long save(CenterImpactObjective researchImpactObjective) {
    if (researchImpactObjective.getId() == null) {
      dao.save(researchImpactObjective);
    } else {
      dao.update(researchImpactObjective);
    }
    return researchImpactObjective.getId();
  }


}