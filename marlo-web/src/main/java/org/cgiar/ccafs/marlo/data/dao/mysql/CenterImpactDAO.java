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

import org.cgiar.ccafs.marlo.data.dao.ICenterImpactDAO;
import org.cgiar.ccafs.marlo.data.model.CenterImpact;

import java.util.List;

import com.google.inject.Inject;
import org.hibernate.SessionFactory;

public class CenterImpactDAO extends AbstractMarloDAO<CenterImpact, Long> implements ICenterImpactDAO {


  @Inject
  public CenterImpactDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public boolean deleteResearchImpact(long researchImpactId) {
    CenterImpact researchImpact = this.find(researchImpactId);
    researchImpact.setActive(false);
    return this.save(researchImpact) > 0;
  }

  @Override
  public boolean existResearchImpact(long researchImpactID) {
    CenterImpact researchImpact = this.find(researchImpactID);
    if (researchImpact == null) {
      return false;
    }
    return true;

  }

  @Override
  public CenterImpact find(long id) {
    return super.find(CenterImpact.class, id);

  }

  @Override
  public List<CenterImpact> findAll() {
    String query = "from " + CenterImpact.class.getName();
    List<CenterImpact> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<CenterImpact> getResearchImpactsByUserId(long userId) {
    String query = "from " + CenterImpact.class.getName() + " where user_id=" + userId;
    return super.findAll(query);
  }

  @Override
  public long save(CenterImpact researchImpact) {
    if (researchImpact.getId() == null) {
      super.saveEntity(researchImpact);
    } else {
      super.update(researchImpact);
    }
    return researchImpact.getId();
  }


}