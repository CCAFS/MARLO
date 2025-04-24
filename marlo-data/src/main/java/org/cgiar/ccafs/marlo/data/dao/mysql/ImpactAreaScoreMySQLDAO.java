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

import org.cgiar.ccafs.marlo.data.dao.ImpactAreaScoreDAO;
import org.cgiar.ccafs.marlo.data.model.ImpactAreaScore;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ImpactAreaScoreMySQLDAO extends AbstractMarloDAO<ImpactAreaScore, Long> implements ImpactAreaScoreDAO {


  @Inject
  public ImpactAreaScoreMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteImpactAreaScore(long impactAreaScoreId) {
    ImpactAreaScore impactAreaScore = this.find(impactAreaScoreId);
    this.delete(impactAreaScore);
  }

  @Override
  public boolean existImpactAreaScore(long impactAreaScoreID) {
    ImpactAreaScore impactAreaScore = this.find(impactAreaScoreID);
    if (impactAreaScore == null) {
      return false;
    }
    return true;

  }

  @Override
  public ImpactAreaScore find(long id) {
    return super.find(ImpactAreaScore.class, id);

  }

  @Override
  public List<ImpactAreaScore> findAll() {
    String query = "from " + ImpactAreaScore.class.getName();
    List<ImpactAreaScore> list = super.findAll(query);
    if (!list.isEmpty()) {
      return list;
    }
    return null;

  }

  @Override
  public ImpactAreaScore save(ImpactAreaScore impactAreaScore) {
    if (impactAreaScore.getId() == null) {
      super.saveEntity(impactAreaScore);
    } else {
      impactAreaScore = super.update(impactAreaScore);
    }


    return impactAreaScore;
  }


}