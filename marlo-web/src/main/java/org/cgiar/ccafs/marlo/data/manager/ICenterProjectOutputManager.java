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

import org.cgiar.ccafs.marlo.data.model.CenterProjectOutput;

import java.util.List;


/**
 * @author Christian Garcia
 */

public interface ICenterProjectOutputManager {


  /**
   * This method removes a specific projectOutput value from the database.
   * 
   * @param projectOutputId is the projectOutput identifier.
   * @return true if the projectOutput was successfully deleted, false otherwise.
   */
  public void deleteProjectOutput(long projectOutputId);


  /**
   * This method validate if the projectOutput identify with the given id exists in the system.
   * 
   * @param projectOutputID is a projectOutput identifier.
   * @return true if the projectOutput exists, false otherwise.
   */
  public boolean existProjectOutput(long projectOutputID);


  /**
   * This method gets a list of projectOutput that are active
   * 
   * @return a list from CenterProjectOutput null if no exist records
   */
  public List<CenterProjectOutput> findAll();


  /**
   * This method gets a projectOutput object by a given projectOutput identifier.
   * 
   * @param projectOutputID is the projectOutput identifier.
   * @return a CenterProjectOutput object.
   */
  public CenterProjectOutput getProjectOutputById(long projectOutputID);

  /**
   * This method gets a list of projectOutputs belongs of the user
   * 
   * @param userId - the user id
   * @return List of ProjectOutputs or null if the user is invalid or not have roles.
   */
  public List<CenterProjectOutput> getProjectOutputsByUserId(Long userId);

  /**
   * This method saves the information of the given projectOutput
   * 
   * @param projectOutput - is the projectOutput object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the projectOutput was
   *         updated
   *         or -1 is some error occurred.
   */
  public CenterProjectOutput saveProjectOutput(CenterProjectOutput projectOutput);


}
