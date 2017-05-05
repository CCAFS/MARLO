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

import org.cgiar.ccafs.marlo.data.manager.impl.FundingSourceLocationsManagerImpl;
import org.cgiar.ccafs.marlo.data.model.FundingSourceLocation;

import java.util.List;

import com.google.inject.ImplementedBy;

/**
 * @author Christian Garcia
 */
@ImplementedBy(FundingSourceLocationsManagerImpl.class)
public interface FundingSourceLocationsManager {


  /**
   * This method removes a specific fundingSourceLocations value from the database.
   * 
   * @param fundingSourceLocationsId is the fundingSourceLocations identifier.
   * @return true if the fundingSourceLocations was successfully deleted, false otherwise.
   */
  public boolean deleteFundingSourceLocations(long fundingSourceLocationsId);


  /**
   * This method validate if the fundingSourceLocations identify with the given id exists in the system.
   * 
   * @param fundingSourceLocationsID is a fundingSourceLocations identifier.
   * @return true if the fundingSourceLocations exists, false otherwise.
   */
  public boolean existFundingSourceLocations(long fundingSourceLocationsID);


  /**
   * This method gets a list of fundingSourceLocations that are active
   * 
   * @return a list from FundingSourceLocation null if no exist records
   */
  public List<FundingSourceLocation> findAll();


  /**
   * This method gets a fundingSourceLocations object by a given fundingSourceLocations identifier.
   * 
   * @param fundingSourceLocationsID is the fundingSourceLocations identifier.
   * @return a FundingSourceLocation object.
   */
  public FundingSourceLocation getFundingSourceLocationsById(long fundingSourceLocationsID);

  /**
   * This method saves the information of the given fundingSourceLocations
   * 
   * @param fundingSourceLocations - is the fundingSourceLocations object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the fundingSourceLocations was
   *         updated
   *         or -1 is some error occurred.
   */
  public long saveFundingSourceLocations(FundingSourceLocation fundingSourceLocations);


}
