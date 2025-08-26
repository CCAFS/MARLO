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
package org.cgiar.ccafs.marlo.data.manager;

import org.cgiar.ccafs.marlo.data.model.Portfolio;

import java.util.List;


/**
 * @author CCAFS
 */

public interface PortfolioManager {


  /**
   * This method removes a specific portfolio value from the database.
   * 
   * @param portfolioId is the portfolio identifier.
   * @return true if the portfolio was successfully deleted, false otherwise.
   */
  public void deletePortfolio(long portfolioId);


  /**
   * This method validate if the portfolio identify with the given id exists in the system.
   * 
   * @param portfolioID is a portfolio identifier.
   * @return true if the portfolio exists, false otherwise.
   */
  public boolean existPortfolio(long portfolioID);


  /**
   * This method gets a list of portfolio that are active
   * 
   * @return a list from Portfolio null if no exist records
   */
  public List<Portfolio> findAll();

  /**
   * This method gets a portfolio object by a given portfolio identifier.
   * 
   * @param portfolioID is the portfolio identifier.
   * @return a Portfolio object.
   */
  public Portfolio getPortfolioById(long portfolioID);


  /**
   * This method gets a list of portfolio that are active filterd by global unit id
   * 
   * @param globalUnitID is the global unit identifier.
   * @return a list from Portfolio null if no exist records
   */
  public List<Portfolio> getPortfoliosByGlobalUnitId(long globalUnitID);

  /**
   * This method saves the information of the given portfolio
   * 
   * @param portfolio - is the portfolio object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the portfolio was
   *         updated
   *         or -1 is some error occurred.
   */
  public Portfolio savePortfolio(Portfolio portfolio);


}
