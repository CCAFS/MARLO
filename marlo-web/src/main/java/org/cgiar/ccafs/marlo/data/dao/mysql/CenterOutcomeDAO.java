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

import org.cgiar.ccafs.marlo.data.dao.ICenterOutcomeDAO;
import org.cgiar.ccafs.marlo.data.model.CenterOutcome;

import java.util.List;

import com.google.inject.Inject;
import org.hibernate.SessionFactory;

public class CenterOutcomeDAO extends AbstractMarloDAO<CenterOutcome, Long> implements ICenterOutcomeDAO {


  @Inject
  public CenterOutcomeDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteResearchOutcome(long researchOutcomeId) {
    CenterOutcome researchOutcome = this.find(researchOutcomeId);
    researchOutcome.setActive(false);
    this.save(researchOutcome);
  }

  @Override
  public boolean existResearchOutcome(long researchOutcomeID) {
    CenterOutcome researchOutcome = this.find(researchOutcomeID);
    if (researchOutcome == null) {
      return false;
    }
    return true;

  }

  @Override
  public CenterOutcome find(long id) {
    return super.find(CenterOutcome.class, id);

  }

  @Override
  public List<CenterOutcome> findAll() {
    String query = "from " + CenterOutcome.class.getName();
    List<CenterOutcome> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<CenterOutcome> getResearchOutcomesByUserId(long userId) {
    String query = "from " + CenterOutcome.class.getName() + " where user_id=" + userId;
    return super.findAll(query);
  }

  @Override
  public CenterOutcome save(CenterOutcome researchOutcome) {
    if (researchOutcome.getId() == null) {
      super.saveEntity(researchOutcome);
    } else {
      researchOutcome = super.update(researchOutcome);
    }
    return researchOutcome;
  }

  @Override
  public CenterOutcome save(CenterOutcome outcome, String actionName, List<String> relationsName) {
    if (outcome.getId() == null) {
      super.saveEntity(outcome, actionName, relationsName);
    } else {
      outcome = super.update(outcome, actionName, relationsName);
    }
    return outcome;
  }


}