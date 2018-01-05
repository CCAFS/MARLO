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

import org.cgiar.ccafs.marlo.data.model.FundingSourceInstitution;

import java.util.List;


public interface FundingSourceInstitutionDAO {

  /**
   * This method removes a specific fundingSourceInstitution value from the database.
   * 
   * @param fundingSourceInstitutionId is the fundingSourceInstitution identifier.
   * @return true if the fundingSourceInstitution was successfully deleted, false otherwise.
   */
  public void deleteFundingSourceInstitution(long fundingSourceInstitutionId);

  /**
   * This method validate if the fundingSourceInstitution identify with the given id exists in the system.
   * 
   * @param fundingSourceInstitutionID is a fundingSourceInstitution identifier.
   * @return true if the fundingSourceInstitution exists, false otherwise.
   */
  public boolean existFundingSourceInstitution(long fundingSourceInstitutionID);

  /**
   * This method gets a fundingSourceInstitution object by a given fundingSourceInstitution identifier.
   * 
   * @param fundingSourceInstitutionID is the fundingSourceInstitution identifier.
   * @return a FundingSourceInstitution object.
   */
  public FundingSourceInstitution find(long id);

  /**
   * This method gets a list of fundingSourceInstitution that are active
   * 
   * @return a list from FundingSourceInstitution null if no exist records
   */
  public List<FundingSourceInstitution> findAll();


  /**
   * This method saves the information of the given fundingSourceInstitution
   * 
   * @param fundingSourceInstitution - is the fundingSourceInstitution object with the new information to be
   *        added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the fundingSourceInstitution
   *         was
   *         updated
   *         or -1 is some error occurred.
   */
  public FundingSourceInstitution save(FundingSourceInstitution fundingSourceInstitution);
}
