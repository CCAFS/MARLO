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

import org.cgiar.ccafs.marlo.data.dao.PortfolioPhaseDAO;
import org.cgiar.ccafs.marlo.data.model.PortfolioPhase;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class PortfolioPhaseMySQLDAO extends AbstractMarloDAO<PortfolioPhase, Long> implements PortfolioPhaseDAO {


  @Inject
  public PortfolioPhaseMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deletePortfolioPhase(long portfolioPhaseId) {
    PortfolioPhase portfolioPhase = this.find(portfolioPhaseId);
    portfolioPhase.setActive(false);
    this.update(portfolioPhase);
  }

  @Override
  public boolean existPortfolioPhase(long portfolioPhaseID) {
    PortfolioPhase portfolioPhase = this.find(portfolioPhaseID);
    if (portfolioPhase == null) {
      return false;
    }
    return true;

  }

  @Override
  public PortfolioPhase find(long id) {
    return super.find(PortfolioPhase.class, id);

  }

  @Override
  public List<PortfolioPhase> findAll() {
    String query = "from " + PortfolioPhase.class.getName() + " where is_active=1";
    List<PortfolioPhase> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<PortfolioPhase> getPortfolioPhasesByPortfolioID(long portfolioID) {
    String query = "from " + PortfolioPhase.class.getName() + " where is_active=1 and portfolio_id = " + portfolioID;
    List<PortfolioPhase> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;
  }

  @Override
  public PortfolioPhase save(PortfolioPhase portfolioPhase) {
    if (portfolioPhase.getId() == null) {
      super.saveEntity(portfolioPhase);
    } else {
      portfolioPhase = super.update(portfolioPhase);
    }


    return portfolioPhase;
  }


}