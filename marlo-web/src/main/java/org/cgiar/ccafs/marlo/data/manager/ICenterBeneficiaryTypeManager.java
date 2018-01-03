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

import org.cgiar.ccafs.marlo.data.model.CenterBeneficiaryType;

import java.util.List;


/**
 * @author Christian Garcia
 */

public interface ICenterBeneficiaryTypeManager {


  /**
   * This method removes a specific beneficiaryType value from the database.
   * 
   * @param beneficiaryTypeId is the beneficiaryType identifier.
   * @return true if the beneficiaryType was successfully deleted, false otherwise.
   */
  public void deleteBeneficiaryType(long beneficiaryTypeId);


  /**
   * This method validate if the beneficiaryType identify with the given id exists in the system.
   * 
   * @param beneficiaryTypeID is a beneficiaryType identifier.
   * @return true if the beneficiaryType exists, false otherwise.
   */
  public boolean existBeneficiaryType(long beneficiaryTypeID);


  /**
   * This method gets a list of beneficiaryType that are active
   * 
   * @return a list from CenterBeneficiaryType null if no exist records
   */
  public List<CenterBeneficiaryType> findAll();


  /**
   * This method gets a beneficiaryType object by a given beneficiaryType identifier.
   * 
   * @param beneficiaryTypeID is the beneficiaryType identifier.
   * @return a CenterBeneficiaryType object.
   */
  public CenterBeneficiaryType getBeneficiaryTypeById(long beneficiaryTypeID);

  /**
   * This method gets a list of beneficiaryTypes belongs of the user
   * 
   * @param userId - the user id
   * @return List of BeneficiaryTypes or null if the user is invalid or not have roles.
   */
  public List<CenterBeneficiaryType> getBeneficiaryTypesByUserId(Long userId);

  /**
   * This method saves the information of the given beneficiaryType
   * 
   * @param beneficiaryType - is the beneficiaryType object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the beneficiaryType was
   *         updated
   *         or -1 is some error occurred.
   */
  public CenterBeneficiaryType saveBeneficiaryType(CenterBeneficiaryType beneficiaryType);


}
