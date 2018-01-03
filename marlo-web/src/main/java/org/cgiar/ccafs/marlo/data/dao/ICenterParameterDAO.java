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

import org.cgiar.ccafs.marlo.data.model.CenterParameter;

import java.util.List;


public interface ICenterParameterDAO {

  /**
   * This method removes a specific centerParameter value from the database.
   * 
   * @param centerParameterId is the centerParameter identifier.
   * @return true if the centerParameter was successfully deleted, false otherwise.
   */
  public void deleteCenterParameter(long centerParameterId);

  /**
   * This method validate if the centerParameter identify with the given id exists in the system.
   * 
   * @param centerParameterID is a centerParameter identifier.
   * @return true if the centerParameter exists, false otherwise.
   */
  public boolean existCenterParameter(long centerParameterID);

  /**
   * This method gets a centerParameter object by a given centerParameter identifier.
   * 
   * @param centerParameterID is the centerParameter identifier.
   * @return a CenterParameter object.
   */
  public CenterParameter find(long id);

  /**
   * This method gets a list of centerParameter that are active
   * 
   * @return a list from CenterParameter null if no exist records
   */
  public List<CenterParameter> findAll();


  /**
   * This method gets a list of centerParameters belongs of the user
   * 
   * @param userId - the user id
   * @return List of CenterParameters or null if the user is invalid or not have roles.
   */
  public List<CenterParameter> getCenterParametersByUserId(long userId);

  /**
   * This method saves the information of the given centerParameter
   * 
   * @param centerParameter - is the centerParameter object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the centerParameter was
   *         updated
   *         or -1 is some error occurred.
   */
  public CenterParameter save(CenterParameter centerParameter);

  /**
   * This method saves the information of the given centerParameter
   * 
   * @param centerParameter - is the centerParameter object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the centerParameter was
   *         updated
   *         or -1 is some error occurred.
   */
  public CenterParameter save(CenterParameter centerParameter, String actionName, List<String> relationsName);
}
