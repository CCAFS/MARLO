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

import org.cgiar.ccafs.marlo.data.manager.impl.CenterFundingSourceTypeManager;
import org.cgiar.ccafs.marlo.data.model.CenterFundingSourceType;

import java.util.List;

import com.google.inject.ImplementedBy;

/**
 * @author Christian Garcia
 */
@ImplementedBy(CenterFundingSourceTypeManager.class)
public interface ICenterFundingSourceTypeManager {


  /**
   * This method removes a specific fundingSourceType value from the database.
   * 
   * @param fundingSourceTypeId is the fundingSourceType identifier.
   * @return true if the fundingSourceType was successfully deleted, false otherwise.
   */
  public void deleteFundingSourceType(long fundingSourceTypeId);


  /**
   * This method validate if the fundingSourceType identify with the given id exists in the system.
   * 
   * @param fundingSourceTypeID is a fundingSourceType identifier.
   * @return true if the fundingSourceType exists, false otherwise.
   */
  public boolean existFundingSourceType(long fundingSourceTypeID);


  /**
   * This method gets a list of fundingSourceType that are active
   * 
   * @return a list from CenterFundingSourceType null if no exist records
   */
  public List<CenterFundingSourceType> findAll();


  /**
   * This method gets a fundingSourceType object by a given fundingSourceType identifier.
   * 
   * @param fundingSourceTypeID is the fundingSourceType identifier.
   * @return a CenterFundingSourceType object.
   */
  public CenterFundingSourceType getFundingSourceTypeById(long fundingSourceTypeID);

  /**
   * This method gets a list of fundingSourceTypes belongs of the user
   * 
   * @param userId - the user id
   * @return List of FundingSourceTypes or null if the user is invalid or not have roles.
   */
  public List<CenterFundingSourceType> getFundingSourceTypesByUserId(Long userId);

  /**
   * This method saves the information of the given fundingSourceType
   * 
   * @param fundingSourceType - is the fundingSourceType object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the fundingSourceType was
   *         updated
   *         or -1 is some error occurred.
   */
  public CenterFundingSourceType saveFundingSourceType(CenterFundingSourceType fundingSourceType);


}
