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

import org.cgiar.ccafs.marlo.data.model.FundingSourceDivision;

import java.util.List;


public interface FundingSourceDivisionManager {


  /**
   * This method removes a specific fundingSourceDivision value from the database.
   * 
   * @param fundingSourceDivisionId is the fundingSourceDivision identifier.
   * @return true if the fundingSourceDivision was successfully deleted, false otherwise.
   */
  public void deleteFundingSourceDivision(long fundingSourceDivisionId);


  /**
   * This method validate if the fundingSourceDivision identify with the given id exists in the system.
   * 
   * @param fundingSourceDivisionID is a fundingSourceDivision identifier.
   * @return true if the fundingSourceDivision exists, false otherwise.
   */
  public boolean existFundingSourceDivision(long fundingSourceDivisionID);


  /**
   * This method gets a list of fundingSourceDivision that are active
   * 
   * @return a list from FundingSourceDivision null if no exist records
   */
  public List<FundingSourceDivision> findAll();


  /**
   * This method gets a fundingSourceDivision object by a given fundingSourceDivision identifier.
   * 
   * @param fundingSourceDivisionID is the fundingSourceDivision identifier.
   * @return a FundingSourceDivision object.
   */
  public FundingSourceDivision getFundingSourceDivisionById(long fundingSourceDivisionID);

  /**
   * This method saves the information of the given fundingSourceDivision
   * 
   * @param fundingSourceDivision - is the fundingSourceDivision object with the new information to be
   *        added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the fundingSourceDivision
   *         was
   *         updated
   *         or -1 is some error occurred.
   */
  public FundingSourceDivision saveFundingSourceDivision(FundingSourceDivision fundingSourceDivision);


}
