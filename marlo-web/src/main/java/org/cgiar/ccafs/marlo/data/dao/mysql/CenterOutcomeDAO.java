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

public class CenterOutcomeDAO implements ICenterOutcomeDAO {

  private StandardDAO dao;

  @Inject
  public CenterOutcomeDAO(StandardDAO dao) {
    this.dao = dao;
  }

  @Override
  public boolean deleteResearchOutcome(long researchOutcomeId) {
    CenterOutcome researchOutcome = this.find(researchOutcomeId);
    researchOutcome.setActive(false);
    return this.save(researchOutcome) > 0;
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
    return dao.find(CenterOutcome.class, id);

  }

  @Override
  public List<CenterOutcome> findAll() {
    String query = "from " + CenterOutcome.class.getName();
    List<CenterOutcome> list = dao.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<CenterOutcome> getResearchOutcomesByUserId(long userId) {
    String query = "from " + CenterOutcome.class.getName() + " where user_id=" + userId;
    return dao.findAll(query);
  }

  @Override
  public long save(CenterOutcome researchOutcome) {
    if (researchOutcome.getId() == null) {
      dao.save(researchOutcome);
    } else {
      dao.update(researchOutcome);
    }
    return researchOutcome.getId();
  }

  @Override
  public long save(CenterOutcome outcome, String actionName, List<String> relationsName) {
    if (outcome.getId() == null) {
      dao.save(outcome, actionName, relationsName);
    } else {
      dao.update(outcome, actionName, relationsName);
    }
    return outcome.getId();
  }


}