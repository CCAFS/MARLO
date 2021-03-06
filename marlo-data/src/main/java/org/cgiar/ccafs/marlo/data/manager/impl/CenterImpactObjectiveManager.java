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


import org.cgiar.ccafs.marlo.data.dao.ICenterImpactObjectiveDAO;
import org.cgiar.ccafs.marlo.data.manager.ICenterImpactObjectiveManager;
import org.cgiar.ccafs.marlo.data.model.CenterImpactObjective;

import java.util.List;

import javax.inject.Named;
import javax.inject.Inject;

/**
 * @author Christian Garcia
 */
@Named
public class CenterImpactObjectiveManager implements ICenterImpactObjectiveManager {


  private ICenterImpactObjectiveDAO researchImpactObjectiveDAO;

  // Managers


  @Inject
  public CenterImpactObjectiveManager(ICenterImpactObjectiveDAO researchImpactObjectiveDAO) {
    this.researchImpactObjectiveDAO = researchImpactObjectiveDAO;


  }

  @Override
  public void deleteResearchImpactObjective(long researchImpactObjectiveId) {

    researchImpactObjectiveDAO.deleteResearchImpactObjective(researchImpactObjectiveId);
  }

  @Override
  public boolean existResearchImpactObjective(long researchImpactObjectiveID) {

    return researchImpactObjectiveDAO.existResearchImpactObjective(researchImpactObjectiveID);
  }

  @Override
  public List<CenterImpactObjective> findAll() {

    return researchImpactObjectiveDAO.findAll();

  }

  @Override
  public CenterImpactObjective getResearchImpactObjectiveById(long researchImpactObjectiveID) {

    return researchImpactObjectiveDAO.find(researchImpactObjectiveID);
  }

  @Override
  public List<CenterImpactObjective> getResearchImpactObjectivesByUserId(Long userId) {
    return researchImpactObjectiveDAO.getResearchImpactObjectivesByUserId(userId);
  }

  @Override
  public CenterImpactObjective saveResearchImpactObjective(CenterImpactObjective researchImpactObjective) {

    return researchImpactObjectiveDAO.save(researchImpactObjective);
  }


}
