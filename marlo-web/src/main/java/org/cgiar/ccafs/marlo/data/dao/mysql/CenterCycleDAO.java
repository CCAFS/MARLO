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

import org.cgiar.ccafs.marlo.data.dao.ICenterCycleDAO;
import org.cgiar.ccafs.marlo.data.model.CenterCycle;

import java.util.List;

import javax.inject.Named;
import javax.inject.Inject;
import org.hibernate.SessionFactory;

@Named
public class CenterCycleDAO extends AbstractMarloDAO<CenterCycle, Long> implements ICenterCycleDAO {


  @Inject
  public CenterCycleDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteResearchCycle(long researchCycleId) {
    CenterCycle researchCycle = this.find(researchCycleId);
    super.delete(researchCycle);
  }

  @Override
  public boolean existResearchCycle(long researchCycleID) {
    CenterCycle researchCycle = this.find(researchCycleID);
    if (researchCycle == null) {
      return false;
    }
    return true;

  }

  @Override
  public CenterCycle find(long id) {
    return super.find(CenterCycle.class, id);

  }

  @Override
  public List<CenterCycle> findAll() {
    String query = "from " + CenterCycle.class.getName();
    List<CenterCycle> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<CenterCycle> getResearchCyclesByUserId(long userId) {
    String query = "from " + CenterCycle.class.getName() + " where user_id=" + userId;
    return super.findAll(query);
  }

  @Override
  public CenterCycle save(CenterCycle researchCycle) {
    if (researchCycle.getId() == null) {
      super.saveEntity(researchCycle);
    } else {
      researchCycle = super.update(researchCycle);
    }
    return researchCycle;
  }


}