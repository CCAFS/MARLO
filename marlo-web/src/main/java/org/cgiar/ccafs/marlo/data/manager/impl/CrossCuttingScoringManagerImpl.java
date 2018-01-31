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

import org.cgiar.ccafs.marlo.data.dao.CrossCuttingScoringDAO;
import org.cgiar.ccafs.marlo.data.manager.CrossCuttingScoringManager;
import org.cgiar.ccafs.marlo.data.model.CrossCuttingScoring;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

@Named
public class CrossCuttingScoringManagerImpl implements CrossCuttingScoringManager {


  private CrossCuttingScoringDAO crossCuttingDAO;


  @Inject
  public CrossCuttingScoringManagerImpl(CrossCuttingScoringDAO crossCuttingDAO) {
    this.crossCuttingDAO = crossCuttingDAO;
  }

  @Override
  public boolean existCrossCuttingScoring(long crossCuttingId) {
    return crossCuttingDAO.existCrossCuttingScoring(crossCuttingId);
  }


  @Override
  public List<CrossCuttingScoring> findAll() {
    return crossCuttingDAO.findAll();
  }

  @Override
  public CrossCuttingScoring getCrossCuttingScoringById(long crossCuttingId) {
    return crossCuttingDAO.find(crossCuttingId);
  }


  @Override
  public CrossCuttingScoring saveCrossCuttingScoring(CrossCuttingScoring crossCutting) {
    return crossCuttingDAO.save(crossCutting);
  }

}
