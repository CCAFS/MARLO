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
package org.cgiar.ccafs.marlo.data.service.impl;


import org.cgiar.ccafs.marlo.data.dao.ICenterCycleDAO;
import org.cgiar.ccafs.marlo.data.model.CenterCycle;
import org.cgiar.ccafs.marlo.data.service.ICenterCycleService;

import java.util.List;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class CenterCycleService implements ICenterCycleService {


  private ICenterCycleDAO researchCycleDAO;

  // Managers


  @Inject
  public CenterCycleService(ICenterCycleDAO researchCycleDAO) {
    this.researchCycleDAO = researchCycleDAO;


  }

  @Override
  public boolean deleteResearchCycle(long researchCycleId) {

    return researchCycleDAO.deleteResearchCycle(researchCycleId);
  }

  @Override
  public boolean existResearchCycle(long researchCycleID) {

    return researchCycleDAO.existResearchCycle(researchCycleID);
  }

  @Override
  public List<CenterCycle> findAll() {

    return researchCycleDAO.findAll();

  }

  @Override
  public CenterCycle getResearchCycleById(long researchCycleID) {

    return researchCycleDAO.find(researchCycleID);
  }

  @Override
  public List<CenterCycle> getResearchCyclesByUserId(Long userId) {
    return researchCycleDAO.getResearchCyclesByUserId(userId);
  }

  @Override
  public long saveResearchCycle(CenterCycle researchCycle) {

    return researchCycleDAO.save(researchCycle);
  }


}
