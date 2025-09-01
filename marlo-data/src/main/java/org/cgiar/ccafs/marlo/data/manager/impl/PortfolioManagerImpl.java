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


import org.cgiar.ccafs.marlo.data.dao.PortfolioDAO;
import org.cgiar.ccafs.marlo.data.manager.PortfolioManager;
import org.cgiar.ccafs.marlo.data.model.Portfolio;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class PortfolioManagerImpl implements PortfolioManager {


  private PortfolioDAO portfolioDAO;
  // Managers


  @Inject
  public PortfolioManagerImpl(PortfolioDAO portfolioDAO) {
    this.portfolioDAO = portfolioDAO;


  }

  @Override
  public void deletePortfolio(long portfolioId) {

    portfolioDAO.deletePortfolio(portfolioId);
  }

  @Override
  public boolean existPortfolio(long portfolioID) {

    return portfolioDAO.existPortfolio(portfolioID);
  }

  @Override
  public List<Portfolio> findAll() {

    return portfolioDAO.findAll();

  }

  @Override
  public Portfolio getPortfolioById(long portfolioID) {

    return portfolioDAO.find(portfolioID);
  }

  @Override
  public List<Portfolio> getPortfoliosByGlobalUnitId(long globalUnitId) {
    return portfolioDAO.getPortfoliosByGlobalUnitId(globalUnitId);
  }

  @Override
  public Portfolio savePortfolio(Portfolio portfolio) {

    return portfolioDAO.save(portfolio);
  }


}
