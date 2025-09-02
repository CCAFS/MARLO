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


import org.cgiar.ccafs.marlo.data.dao.PortfolioPhaseDAO;
import org.cgiar.ccafs.marlo.data.manager.PortfolioPhaseManager;
import org.cgiar.ccafs.marlo.data.model.PortfolioPhase;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class PortfolioPhaseManagerImpl implements PortfolioPhaseManager {


  private PortfolioPhaseDAO portfolioPhaseDAO;
  // Managers


  @Inject
  public PortfolioPhaseManagerImpl(PortfolioPhaseDAO portfolioPhaseDAO) {
    this.portfolioPhaseDAO = portfolioPhaseDAO;


  }

  @Override
  public void deletePortfolioPhase(long portfolioPhaseId) {

    portfolioPhaseDAO.deletePortfolioPhase(portfolioPhaseId);
  }

  @Override
  public boolean existPortfolioPhase(long portfolioPhaseID) {

    return portfolioPhaseDAO.existPortfolioPhase(portfolioPhaseID);
  }

  @Override
  public List<PortfolioPhase> findAll() {

    return portfolioPhaseDAO.findAll();

  }

  @Override
  public PortfolioPhase getPortfolioPhaseById(long portfolioPhaseID) {

    return portfolioPhaseDAO.find(portfolioPhaseID);
  }

  @Override
  public List<PortfolioPhase> getPortfolioPhasesByPortfolioID(long portfolioID) {
    return portfolioPhaseDAO.getPortfolioPhasesByPortfolioID(portfolioID);
  }

  @Override
  public PortfolioPhase savePortfolioPhase(PortfolioPhase portfolioPhase) {

    return portfolioPhaseDAO.save(portfolioPhase);
  }


}
