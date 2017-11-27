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

import org.cgiar.ccafs.marlo.data.manager.impl.CenterProjectLocationManager;
import org.cgiar.ccafs.marlo.data.model.CenterProjectLocation;

import java.util.List;

import com.google.inject.ImplementedBy;

/**
 * @author Christian Garcia
 */
@ImplementedBy(CenterProjectLocationManager.class)
public interface ICenterProjectLocationManager {


  /**
   * This method removes a specific projectLocation value from the database.
   * 
   * @param projectLocationId is the projectLocation identifier.
   * @return true if the projectLocation was successfully deleted, false otherwise.
   */
  public void deleteProjectLocation(long projectLocationId);


  /**
   * This method validate if the projectLocation identify with the given id exists in the system.
   * 
   * @param projectLocationID is a projectLocation identifier.
   * @return true if the projectLocation exists, false otherwise.
   */
  public boolean existProjectLocation(long projectLocationID);


  /**
   * This method gets a list of projectLocation that are active
   * 
   * @return a list from CenterProjectLocation null if no exist records
   */
  public List<CenterProjectLocation> findAll();


  /**
   * This method gets a projectLocation object by a given projectLocation identifier.
   * 
   * @param projectLocationID is the projectLocation identifier.
   * @return a CenterProjectLocation object.
   */
  public CenterProjectLocation getProjectLocationById(long projectLocationID);

  /**
   * This method gets a list of projectLocations belongs of the user
   * 
   * @param userId - the user id
   * @return List of ProjectLocations or null if the user is invalid or not have roles.
   */
  public List<CenterProjectLocation> getProjectLocationsByUserId(Long userId);

  /**
   * This method saves the information of the given projectLocation
   * 
   * @param projectLocation - is the projectLocation object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the projectLocation was
   *         updated
   *         or -1 is some error occurred.
   */
  public CenterProjectLocation saveProjectLocation(CenterProjectLocation projectLocation);

  /**
   * This method saves the information of the given projectLocation
   * 
   * @param projectLocation - is the projectLocation object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the projectLocation was
   *         updated
   *         or -1 is some error occurred.
   */
  public CenterProjectLocation saveProjectLocation(CenterProjectLocation projectLocation, String actionName, List<String> relationsName);


}
