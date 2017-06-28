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

import org.cgiar.ccafs.marlo.data.model.CenterCustomParameter;
import org.cgiar.ccafs.marlo.data.service.impl.CenterCustomParameterService;

import java.util.List;

import com.google.inject.ImplementedBy;

/**
 * @author Christian Garcia
 */
@ImplementedBy(CenterCustomParameterService.class)
public interface ICenterCustomParameterService {


  /**
   * This method removes a specific centerCustomParameter value from the database.
   * 
   * @param centerCustomParameterId is the centerCustomParameter identifier.
   * @return true if the centerCustomParameter was successfully deleted, false otherwise.
   */
  public boolean deleteCenterCustomParameter(long centerCustomParameterId);


  /**
   * This method validate if the centerCustomParameter identify with the given id exists in the system.
   * 
   * @param centerCustomParameterID is a centerCustomParameter identifier.
   * @return true if the centerCustomParameter exists, false otherwise.
   */
  public boolean existCenterCustomParameter(long centerCustomParameterID);


  /**
   * This method gets a list of centerCustomParameter that are active
   * 
   * @return a list from CenterCustomParameter null if no exist records
   */
  public List<CenterCustomParameter> findAll();


  /**
   * This method gets a centerCustomParameter object by a given centerCustomParameter identifier.
   * 
   * @param centerCustomParameterID is the centerCustomParameter identifier.
   * @return a CenterCustomParameter object.
   */
  public CenterCustomParameter getCenterCustomParameterById(long centerCustomParameterID);

  /**
   * This method gets a list of centerCustomParameters belongs of the user
   * 
   * @param userId - the user id
   * @return List of CenterCustomParameters or null if the user is invalid or not have roles.
   */
  public List<CenterCustomParameter> getCenterCustomParametersByUserId(Long userId);

  /**
   * This method saves the information of the given centerCustomParameter
   * 
   * @param centerCustomParameter - is the centerCustomParameter object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the centerCustomParameter was
   *         updated
   *         or -1 is some error occurred.
   */
  public long saveCenterCustomParameter(CenterCustomParameter centerCustomParameter);

  /**
   * This method saves the information of the given centerCustomParameter
   * 
   * @param centerCustomParameter - is the centerCustomParameter object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the centerCustomParameter was
   *         updated
   *         or -1 is some error occurred.
   */
  public long saveCenterCustomParameter(CenterCustomParameter centerCustomParameter, String actionName, List<String> relationsName);


}
