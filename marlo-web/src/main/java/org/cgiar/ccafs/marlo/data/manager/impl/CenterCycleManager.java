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


import org.cgiar.ccafs.marlo.data.dao.ICenterCycleDAO;
import org.cgiar.ccafs.marlo.data.manager.ICenterCycleManager;
import org.cgiar.ccafs.marlo.data.model.CenterCycle;

import java.util.List;

import javax.inject.Named;
import javax.inject.Inject;

/**
 * @author Christian Garcia
 */
@Named
public class CenterCycleManager implements ICenterCycleManager {


  private ICenterCycleDAO researchCycleDAO;

  // Managers


  @Inject
  public CenterCycleManager(ICenterCycleDAO researchCycleDAO) {
    this.researchCycleDAO = researchCycleDAO;


  }

  @Override
  public void deleteResearchCycle(long researchCycleId) {

    researchCycleDAO.deleteResearchCycle(researchCycleId);
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
  public CenterCycle saveResearchCycle(CenterCycle researchCycle) {

    return researchCycleDAO.save(researchCycle);
  }


}
