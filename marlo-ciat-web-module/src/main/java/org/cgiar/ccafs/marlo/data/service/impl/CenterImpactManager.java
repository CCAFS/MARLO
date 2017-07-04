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


import org.cgiar.ccafs.marlo.data.dao.ICenterImpactDAO;
import org.cgiar.ccafs.marlo.data.model.CenterImpact;
import org.cgiar.ccafs.marlo.data.service.ICenterImpactManager;

import java.util.List;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class CenterImpactManager implements ICenterImpactManager {


  private ICenterImpactDAO researchImpactDAO;

  // Managers


  @Inject
  public CenterImpactManager(ICenterImpactDAO researchImpactDAO) {
    this.researchImpactDAO = researchImpactDAO;


  }

  @Override
  public boolean deleteResearchImpact(long researchImpactId) {

    return researchImpactDAO.deleteResearchImpact(researchImpactId);
  }

  @Override
  public boolean existResearchImpact(long researchImpactID) {

    return researchImpactDAO.existResearchImpact(researchImpactID);
  }

  @Override
  public List<CenterImpact> findAll() {

    return researchImpactDAO.findAll();

  }

  @Override
  public CenterImpact getResearchImpactById(long researchImpactID) {

    return researchImpactDAO.find(researchImpactID);
  }

  @Override
  public List<CenterImpact> getResearchImpactsByUserId(Long userId) {
    return researchImpactDAO.getResearchImpactsByUserId(userId);
  }

  @Override
  public long saveResearchImpact(CenterImpact researchImpact) {

    return researchImpactDAO.save(researchImpact);
  }


}
