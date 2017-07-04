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

import org.cgiar.ccafs.marlo.data.manager.impl.CenterProjectTypeManager;
import org.cgiar.ccafs.marlo.data.model.CenterProjectType;

import java.util.List;

import com.google.inject.ImplementedBy;

/**
 * @author Christian Garcia
 */
@ImplementedBy(CenterProjectTypeManager.class)
public interface ICenterProjectTypeManager {


  /**
   * This method removes a specific projectType value from the database.
   * 
   * @param projectTypeId is the projectType identifier.
   * @return true if the projectType was successfully deleted, false otherwise.
   */
  public boolean deleteProjectType(long projectTypeId);


  /**
   * This method validate if the projectType identify with the given id exists in the system.
   * 
   * @param projectTypeID is a projectType identifier.
   * @return true if the projectType exists, false otherwise.
   */
  public boolean existProjectType(long projectTypeID);


  /**
   * This method gets a list of projectType that are active
   * 
   * @return a list from CenterProjectType null if no exist records
   */
  public List<CenterProjectType> findAll();


  /**
   * This method gets a projectType object by a given projectType identifier.
   * 
   * @param projectTypeID is the projectType identifier.
   * @return a CenterProjectType object.
   */
  public CenterProjectType getProjectTypeById(long projectTypeID);

  /**
   * This method gets a list of projectTypes belongs of the user
   * 
   * @param userId - the user id
   * @return List of ProjectTypes or null if the user is invalid or not have roles.
   */
  public List<CenterProjectType> getProjectTypesByUserId(Long userId);

  /**
   * This method saves the information of the given projectType
   * 
   * @param projectType - is the projectType object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the projectType was
   *         updated
   *         or -1 is some error occurred.
   */
  public long saveProjectType(CenterProjectType projectType);

  /**
   * This method saves the information of the given projectType
   * 
   * @param projectType - is the projectType object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the projectType was
   *         updated
   *         or -1 is some error occurred.
   */
  public long saveProjectType(CenterProjectType projectType, String actionName, List<String> relationsName);


}
