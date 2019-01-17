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


import org.cgiar.ccafs.marlo.data.dao.Lp6ContributionGeographicScopeDAO;
import org.cgiar.ccafs.marlo.data.dao.PhaseDAO;
import org.cgiar.ccafs.marlo.data.manager.Lp6ContributionGeographicScopeManager;
import org.cgiar.ccafs.marlo.data.model.Lp6ContributionGeographicScope;
import org.cgiar.ccafs.marlo.data.model.Phase;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class Lp6ContributionGeographicScopeManagerImpl implements Lp6ContributionGeographicScopeManager {


  private Lp6ContributionGeographicScopeDAO lp6ContributionGeographicScopeDAO;
  // Managers
  private PhaseDAO phaseDAO;

  @Inject
  public Lp6ContributionGeographicScopeManagerImpl(Lp6ContributionGeographicScopeDAO lp6ContributionGeographicScopeDAO,
    PhaseDAO phaseDAO) {
    this.lp6ContributionGeographicScopeDAO = lp6ContributionGeographicScopeDAO;
    this.phaseDAO = phaseDAO;
  }

  @Override
  public void deleteLp6ContributionGeographicScope(long lp6ContributionGeographicScopeID) {
    Lp6ContributionGeographicScope lp6ContributionGeographicScope =
      this.getLp6ContributionGeographicScopeById(lp6ContributionGeographicScopeID);
    Phase currentPhase = lp6ContributionGeographicScope.getPhase();


    this.deleteLp6ContributionGeographicScopePhase(currentPhase.getNext(),
      lp6ContributionGeographicScope.getProjectLp6Contribution().getId(), lp6ContributionGeographicScope);


    lp6ContributionGeographicScopeDAO.deleteLp6ContributionGeographicScope(lp6ContributionGeographicScopeID);
  }

  public void deleteLp6ContributionGeographicScopePhase(Phase next, long projectLp6ContributionID,
    Lp6ContributionGeographicScope lp6ContributionGeographicScope) {
    Phase phase = phaseDAO.find(next.getId());

    List<Lp6ContributionGeographicScope> lp6ContributionGeographicScopes =
      phase.getLp6ContributionGeographicScopes().stream()
        .filter(c -> c.isActive() && c.getProjectLp6Contribution().getId().longValue() == projectLp6ContributionID
          && c.getLocElement().getId().equals(lp6ContributionGeographicScope.getLocElement().getId()))
        .collect(Collectors.toList());
    for (Lp6ContributionGeographicScope lp6ContributionGeographicScopeDB : lp6ContributionGeographicScopes) {
      lp6ContributionGeographicScopeDAO.deleteLp6ContributionGeographicScope(lp6ContributionGeographicScopeDB.getId());
    }

    if (phase.getNext() != null) {
      this.deleteLp6ContributionGeographicScopePhase(phase.getNext(), projectLp6ContributionID,
        lp6ContributionGeographicScope);
    }
  }

  @Override
  public boolean existLp6ContributionGeographicScope(long lp6ContributionGeographicScopeID) {
    return lp6ContributionGeographicScopeDAO.existLp6ContributionGeographicScope(lp6ContributionGeographicScopeID);
  }

  @Override
  public List<Lp6ContributionGeographicScope> findAll() {
    return lp6ContributionGeographicScopeDAO.findAll();
  }

  @Override
  public Lp6ContributionGeographicScope getLp6ContributionGeographicScopeById(long lp6ContributionGeographicScopeID) {
    return lp6ContributionGeographicScopeDAO.find(lp6ContributionGeographicScopeID);
  }

  @Override
  public List<Lp6ContributionGeographicScope> getLp6ContributionGeographicScopebyPhase(long projectLp6ContributionID,
    long phaseID) {
    return lp6ContributionGeographicScopeDAO.getLp6ContributionGeographicScopebyPhase(projectLp6ContributionID,
      phaseID);
  }

  @Override
  public Lp6ContributionGeographicScope
    saveLp6ContributionGeographicScope(Lp6ContributionGeographicScope lp6ContributionGeographicScope) {

    Lp6ContributionGeographicScope country = lp6ContributionGeographicScopeDAO.save(lp6ContributionGeographicScope);

    // Phase phase = phaseDAO.find(country.getPhase().getId());

    // if (phase.getDescription().equals(APConstants.REPORTING)) {
    if (country.getPhase().getNext() != null) {
      this.saveLp6ContributionGeographicScopePhase(country.getPhase().getNext(),
        country.getProjectLp6Contribution().getId(), lp6ContributionGeographicScope);
    }
    // }
    return country;
  }

  public void saveLp6ContributionGeographicScopePhase(Phase next, long lp6ContributionGeographicScopeID,
    Lp6ContributionGeographicScope lp6ContributionGeographicScope) {
    Phase phase = phaseDAO.find(next.getId());

    List<Lp6ContributionGeographicScope> lp6ContributionGeographicScopes =
      phase.getLp6ContributionGeographicScopes().stream()
        .filter(c -> c.getProjectLp6Contribution().getId().longValue() == lp6ContributionGeographicScopeID
          && c.getLocElement().getId().equals(lp6ContributionGeographicScope.getLocElement().getId()))
        .collect(Collectors.toList());

    if (lp6ContributionGeographicScopes.isEmpty()) {
      Lp6ContributionGeographicScope lp6ContributionGeographicScopeAdd = new Lp6ContributionGeographicScope();
      lp6ContributionGeographicScopeAdd
        .setProjectLp6Contribution(lp6ContributionGeographicScope.getProjectLp6Contribution());
      lp6ContributionGeographicScopeAdd.setPhase(phase);
      lp6ContributionGeographicScopeAdd.setLocElement(lp6ContributionGeographicScope.getLocElement());
      lp6ContributionGeographicScopeDAO.save(lp6ContributionGeographicScopeAdd);
    }

    if (phase.getNext() != null) {
      this.saveLp6ContributionGeographicScopePhase(phase.getNext(), lp6ContributionGeographicScopeID,
        lp6ContributionGeographicScope);
    }
  }


}
