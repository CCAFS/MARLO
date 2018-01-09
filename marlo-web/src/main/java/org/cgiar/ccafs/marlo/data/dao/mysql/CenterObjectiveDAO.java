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

import org.cgiar.ccafs.marlo.data.dao.ICenterObjectiveDAO;
import org.cgiar.ccafs.marlo.data.model.CenterObjective;

import java.util.List;

import javax.inject.Named;
import javax.inject.Inject;
import org.hibernate.SessionFactory;

@Named
public class CenterObjectiveDAO extends AbstractMarloDAO<CenterObjective, Long> implements ICenterObjectiveDAO {


  @Inject
  public CenterObjectiveDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteResearchObjective(long researchObjectiveId) {
    CenterObjective researchObjective = this.find(researchObjectiveId);
    researchObjective.setActive(false);
    this.save(researchObjective);
  }

  @Override
  public boolean existResearchObjective(long researchObjectiveID) {
    CenterObjective researchObjective = this.find(researchObjectiveID);
    if (researchObjective == null) {
      return false;
    }
    return true;

  }

  @Override
  public CenterObjective find(long id) {
    return super.find(CenterObjective.class, id);

  }

  @Override
  public List<CenterObjective> findAll() {
    String query = "from " + CenterObjective.class.getName();
    List<CenterObjective> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<CenterObjective> getResearchObjectivesByUserId(long userId) {
    String query = "from " + CenterObjective.class.getName() + " where user_id=" + userId;
    return super.findAll(query);
  }

  @Override
  public CenterObjective save(CenterObjective researchObjective) {
    if (researchObjective.getId() == null) {
      super.saveEntity(researchObjective);
    } else {
      researchObjective = super.update(researchObjective);
    }
    return researchObjective;
  }


}