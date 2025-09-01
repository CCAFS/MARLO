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


package org.cgiar.ccafs.marlo.data.dao.mysql;

import org.cgiar.ccafs.marlo.data.dao.PortfolioDAO;
import org.cgiar.ccafs.marlo.data.model.Portfolio;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class PortfolioMySQLDAO extends AbstractMarloDAO<Portfolio, Long> implements PortfolioDAO {


  @Inject
  public PortfolioMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deletePortfolio(long portfolioId) {
    Portfolio portfolio = this.find(portfolioId);
    portfolio.setActive(false);
    this.update(portfolio);
  }

  @Override
  public boolean existPortfolio(long portfolioID) {
    Portfolio portfolio = this.find(portfolioID);
    if (portfolio == null) {
      return false;
    }
    return true;

  }

  @Override
  public Portfolio find(long id) {
    return super.find(Portfolio.class, id);

  }

  @Override
  public List<Portfolio> findAll() {
    String query = "from " + Portfolio.class.getName() + " where is_active=1";
    List<Portfolio> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;
  }

  @Override
  public List<Portfolio> getPortfoliosByGlobalUnitId(long globalUnitId) {
    String query = "from " + Portfolio.class.getName() + " where is_active=1 and global_unit_id = " + globalUnitId;
    List<Portfolio> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;
  }

  @Override
  public Portfolio save(Portfolio portfolio) {
    if (portfolio.getId() == null) {
      super.saveEntity(portfolio);
    } else {
      portfolio = super.update(portfolio);
    }


    return portfolio;
  }


}