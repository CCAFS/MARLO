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


import org.cgiar.ccafs.marlo.data.dao.ICenterImpactStatementDAO;
import org.cgiar.ccafs.marlo.data.model.CenterImpactStatement;
import org.cgiar.ccafs.marlo.data.service.ICenterImpactStatementManager;

import java.util.List;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class CenterImpactStatementManager implements ICenterImpactStatementManager {


  private ICenterImpactStatementDAO researchImpactStatementDAO;

  // Managers


  @Inject
  public CenterImpactStatementManager(ICenterImpactStatementDAO researchImpactStatementDAO) {
    this.researchImpactStatementDAO = researchImpactStatementDAO;


  }

  @Override
  public boolean deleteResearchImpactStatement(long researchImpactStatementId) {

    return researchImpactStatementDAO.deleteResearchImpactStatement(researchImpactStatementId);
  }

  @Override
  public boolean existResearchImpactStatement(long researchImpactStatementID) {

    return researchImpactStatementDAO.existResearchImpactStatement(researchImpactStatementID);
  }

  @Override
  public List<CenterImpactStatement> findAll() {

    return researchImpactStatementDAO.findAll();

  }

  @Override
  public CenterImpactStatement getResearchImpactStatementById(long researchImpactStatementID) {

    return researchImpactStatementDAO.find(researchImpactStatementID);
  }

  @Override
  public List<CenterImpactStatement> getResearchImpactStatementsByUserId(Long userId) {
    return researchImpactStatementDAO.getResearchImpactStatementsByUserId(userId);
  }

  @Override
  public long saveResearchImpactStatement(CenterImpactStatement researchImpactStatement) {

    return researchImpactStatementDAO.save(researchImpactStatement);
  }

  @Override
  public long saveResearchImpactStatement(CenterImpactStatement researchImpactStatement, String actionName, List<String> relationsName) {
    return researchImpactStatementDAO.save(researchImpactStatement, actionName, relationsName);
  }


}
