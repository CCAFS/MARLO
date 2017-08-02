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

import org.cgiar.ccafs.marlo.data.manager.impl.CenterBeneficiaryManager;
import org.cgiar.ccafs.marlo.data.model.CenterBeneficiary;

import java.util.List;

import com.google.inject.ImplementedBy;

/**
 * @author Christian Garcia
 */
@ImplementedBy(CenterBeneficiaryManager.class)
public interface ICenterBeneficiaryManager {


  /**
   * This method removes a specific beneficiary value from the database.
   * 
   * @param beneficiaryId is the beneficiary identifier.
   * @return true if the beneficiary was successfully deleted, false otherwise.
   */
  public boolean deleteBeneficiary(long beneficiaryId);


  /**
   * This method validate if the beneficiary identify with the given id exists in the system.
   * 
   * @param beneficiaryID is a beneficiary identifier.
   * @return true if the beneficiary exists, false otherwise.
   */
  public boolean existBeneficiary(long beneficiaryID);


  /**
   * This method gets a list of beneficiary that are active
   * 
   * @return a list from CenterBeneficiary null if no exist records
   */
  public List<CenterBeneficiary> findAll();


  /**
   * This method gets a beneficiary object by a given beneficiary identifier.
   * 
   * @param beneficiaryID is the beneficiary identifier.
   * @return a CenterBeneficiary object.
   */
  public CenterBeneficiary getBeneficiaryById(long beneficiaryID);

  /**
   * This method gets a list of beneficiarys belongs of the user
   * 
   * @param userId - the user id
   * @return List of Beneficiarys or null if the user is invalid or not have roles.
   */
  public List<CenterBeneficiary> getBeneficiarysByUserId(Long userId);

  /**
   * This method saves the information of the given beneficiary
   * 
   * @param beneficiary - is the beneficiary object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the beneficiary was
   *         updated
   *         or -1 is some error occurred.
   */
  public long saveBeneficiary(CenterBeneficiary beneficiary);


}
