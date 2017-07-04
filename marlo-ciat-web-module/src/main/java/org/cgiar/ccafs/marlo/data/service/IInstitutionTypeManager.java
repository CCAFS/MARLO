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
package org.cgiar.ccafs.marlo.data.service;

import org.cgiar.ccafs.marlo.data.model.InstitutionType;
import org.cgiar.ccafs.marlo.data.service.impl.InstitutionTypeManager;

import java.util.List;

import com.google.inject.ImplementedBy;

/**
 * @author Christian Garcia
 */
@ImplementedBy(InstitutionTypeManager.class)
public interface IInstitutionTypeManager {


  /**
   * This method removes a specific institutionType value from the database.
   * 
   * @param institutionTypeId is the institutionType identifier.
   * @return true if the institutionType was successfully deleted, false otherwise.
   */
  public boolean deleteInstitutionType(long institutionTypeId);


  /**
   * This method validate if the institutionType identify with the given id exists in the system.
   * 
   * @param institutionTypeID is a institutionType identifier.
   * @return true if the institutionType exists, false otherwise.
   */
  public boolean existInstitutionType(long institutionTypeID);


  /**
   * This method gets a list of institutionType that are active
   * 
   * @return a list from InstitutionType null if no exist records
   */
  public List<InstitutionType> findAll();


  /**
   * This method gets a institutionType object by a given institutionType identifier.
   * 
   * @param institutionTypeID is the institutionType identifier.
   * @return a InstitutionType object.
   */
  public InstitutionType getInstitutionTypeById(long institutionTypeID);

  /**
   * This method gets a list of institutionTypes belongs of the user
   * 
   * @param userId - the user id
   * @return List of InstitutionTypes or null if the user is invalid or not have roles.
   */
  public List<InstitutionType> getInstitutionTypesByUserId(Long userId);

  /**
   * This method saves the information of the given institutionType
   * 
   * @param institutionType - is the institutionType object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the institutionType was
   *         updated
   *         or -1 is some error occurred.
   */
  public long saveInstitutionType(InstitutionType institutionType);


}
