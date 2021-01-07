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


import org.cgiar.ccafs.marlo.data.dao.ProgressTargetCaseGeographicScopeDAO;
import org.cgiar.ccafs.marlo.data.manager.ProgressTargetCaseGeographicScopeManager;
import org.cgiar.ccafs.marlo.data.model.ProgressTargetCaseGeographicScope;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ProgressTargetCaseGeographicScopeManagerImpl implements ProgressTargetCaseGeographicScopeManager {


  private ProgressTargetCaseGeographicScopeDAO progressTargetCaseGeographicScopeDAO;
  // Managers


  @Inject
  public ProgressTargetCaseGeographicScopeManagerImpl(ProgressTargetCaseGeographicScopeDAO progressTargetCaseGeographicScopeDAO) {
    this.progressTargetCaseGeographicScopeDAO = progressTargetCaseGeographicScopeDAO;


  }

  @Override
  public void deleteProgressTargetCaseGeographicScope(long progressTargetCaseGeographicScopeId) {

    progressTargetCaseGeographicScopeDAO.deleteProgressTargetCaseGeographicScope(progressTargetCaseGeographicScopeId);
  }

  @Override
  public boolean existProgressTargetCaseGeographicScope(long progressTargetCaseGeographicScopeID) {

    return progressTargetCaseGeographicScopeDAO.existProgressTargetCaseGeographicScope(progressTargetCaseGeographicScopeID);
  }

  @Override
  public List<ProgressTargetCaseGeographicScope> findAll() {

    return progressTargetCaseGeographicScopeDAO.findAll();

  }

  @Override
  public ProgressTargetCaseGeographicScope getProgressTargetCaseGeographicScopeById(long progressTargetCaseGeographicScopeID) {

    return progressTargetCaseGeographicScopeDAO.find(progressTargetCaseGeographicScopeID);
  }

  @Override
  public ProgressTargetCaseGeographicScope saveProgressTargetCaseGeographicScope(ProgressTargetCaseGeographicScope progressTargetCaseGeographicScope) {

    return progressTargetCaseGeographicScopeDAO.save(progressTargetCaseGeographicScope);
  }


}
