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

/**************
 * @author Diego Perez - CIAT/CCAFS
 **************/

package org.cgiar.ccafs.marlo.data.dao;

import org.cgiar.ccafs.marlo.data.model.ProjectPolicyCenter;

import java.util.List;

public interface ProjectPolicyCenterDAO {

  /**
   * This method removes a specific projectPolicyCenter value from the database.
   * 
   * @param projectPolicyCenterId is the projectPolicyCenter identifier.
   * @return true if the projectPolicyCenter was successfully deleted, false otherwise.
   */
  public void deleteProjectPolicyCenter(long projectPolicyCenterId);

  /**
   * This method validate if the projectPolicyCenter identify with the given id exists in the system.
   * 
   * @param projectPolicyCenterID is a projectPolicyCenter identifier.
   * @return true if the projectPolicyCenter exists, false otherwise.
   */
  public boolean existProjectPolicyCenter(long projectPolicyCenterID);

  /**
   * This method gets a projectPolicyCenter object by a given projectPolicyCenter identifier.
   * 
   * @param projectPolicyCenterID is the projectPolicyCenter identifier.
   * @return a projectPolicyCenter object.
   */
  public ProjectPolicyCenter find(long id);

  /**
   * This method gets a list of projectPolicyCenter that are active
   * 
   * @return a list from projectPolicyCenter null if no exist records
   */
  public List<ProjectPolicyCenter> findAll();


  /**
   * This method saves the information of the given projectPolicyCenter
   * 
   * @param projectPolicyCenter - is the projectPolicyCenter object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the projectPolicyCenter was
   *         updated
   *         or -1 is some error occurred.
   */
  public ProjectPolicyCenter save(ProjectPolicyCenter projectPolicyCenter);
}
