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

import org.cgiar.ccafs.marlo.data.model.FundingSourceInfo;

import java.util.List;

/**
 * @author Christian Garcia
 */
public interface FundingSourceInfoManager {


  /**
   * This method removes a specific fundingSourceInfo value from the database.
   * 
   * @param fundingSourceInfoId is the fundingSourceInfo identifier.
   * @return true if the fundingSourceInfo was successfully deleted, false otherwise.
   */
  public void deleteFundingSourceInfo(long fundingSourceInfoId);


  /**
   * This method validate if the fundingSourceInfo identify with the given id exists in the system.
   * 
   * @param fundingSourceInfoID is a fundingSourceInfo identifier.
   * @return true if the fundingSourceInfo exists, false otherwise.
   */
  public boolean existFundingSourceInfo(long fundingSourceInfoID);


  /**
   * This method gets a list of fundingSourceInfo that are active
   * 
   * @return a list from FundingSourceInfo null if no exist records
   */
  public List<FundingSourceInfo> findAll();


  /**
   * This method gets a fundingSourceInfo object by a given fundingSourceInfo identifier.
   * 
   * @param fundingSourceInfoID is the fundingSourceInfo identifier.
   * @return a FundingSourceInfo object.
   */
  public FundingSourceInfo getFundingSourceInfoById(long fundingSourceInfoID);

  /**
   * This method saves the information of the given fundingSourceInfo
   * 
   * @param fundingSourceInfo - is the fundingSourceInfo object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the fundingSourceInfo was
   *         updated
   *         or -1 is some error occurred.
   */
  public FundingSourceInfo saveFundingSourceInfo(FundingSourceInfo fundingSourceInfo);


}
