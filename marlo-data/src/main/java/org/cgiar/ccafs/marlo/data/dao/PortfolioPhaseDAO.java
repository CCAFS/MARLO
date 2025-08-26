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


package org.cgiar.ccafs.marlo.data.dao;

import org.cgiar.ccafs.marlo.data.model.PortfolioPhase;

import java.util.List;


public interface PortfolioPhaseDAO {

  /**
   * This method removes a specific portfolioPhase value from the database.
   * 
   * @param portfolioPhaseId is the portfolioPhase identifier.
   * @return true if the portfolioPhase was successfully deleted, false otherwise.
   */
  public void deletePortfolioPhase(long portfolioPhaseId);

  /**
   * This method validate if the portfolioPhase identify with the given id exists in the system.
   * 
   * @param portfolioPhaseID is a portfolioPhase identifier.
   * @return true if the portfolioPhase exists, false otherwise.
   */
  public boolean existPortfolioPhase(long portfolioPhaseID);

  /**
   * This method gets a portfolioPhase object by a given portfolioPhase identifier.
   * 
   * @param portfolioPhaseID is the portfolioPhase identifier.
   * @return a PortfolioPhase object.
   */
  public PortfolioPhase find(long id);

  /**
   * This method gets a list of portfolioPhase that are active
   * 
   * @return a list from PortfolioPhase null if no exist records
   */
  public List<PortfolioPhase> findAll();


  /**
   * This method gets a list of portfolioPhase that are active filtered by portfolioID
   * 
   * @param portfolioID is the portfolio identifier.
   * @return a list from PortfolioPhase null if no exist records
   */
  public List<PortfolioPhase> getPortfolioPhasesByPortfolioID(long portfolioID);

  /**
   * This method saves the information of the given portfolioPhase
   * 
   * @param portfolioPhase - is the portfolioPhase object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the portfolioPhase was
   *         updated
   *         or -1 is some error occurred.
   */
  public PortfolioPhase save(PortfolioPhase portfolioPhase);
}
