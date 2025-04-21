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
package org.cgiar.ccafs.marlo.data.manager.impl;


import org.cgiar.ccafs.marlo.data.dao.ImpactAreaScoreDAO;
import org.cgiar.ccafs.marlo.data.manager.ImpactAreaScoreManager;
import org.cgiar.ccafs.marlo.data.model.ImpactAreaScore;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ImpactAreaScoreManagerImpl implements ImpactAreaScoreManager {


  private ImpactAreaScoreDAO impactAreaScoreDAO;
  // Managers


  @Inject
  public ImpactAreaScoreManagerImpl(ImpactAreaScoreDAO impactAreaScoreDAO) {
    this.impactAreaScoreDAO = impactAreaScoreDAO;


  }

  @Override
  public void deleteImpactAreaScore(long impactAreaScoreId) {

    impactAreaScoreDAO.deleteImpactAreaScore(impactAreaScoreId);
  }

  @Override
  public boolean existImpactAreaScore(long impactAreaScoreID) {

    return impactAreaScoreDAO.existImpactAreaScore(impactAreaScoreID);
  }

  @Override
  public List<ImpactAreaScore> findAll() {

    return impactAreaScoreDAO.findAll();

  }

  @Override
  public ImpactAreaScore getImpactAreaScoreById(long impactAreaScoreID) {

    return impactAreaScoreDAO.find(impactAreaScoreID);
  }

  @Override
  public ImpactAreaScore saveImpactAreaScore(ImpactAreaScore impactAreaScore) {

    return impactAreaScoreDAO.save(impactAreaScore);
  }


}
