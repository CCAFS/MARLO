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

import org.cgiar.ccafs.marlo.data.model.ProjectPolicyOwner;

import java.util.List;


public interface ProjectPolicyOwnerDAO {

  /**
   * This method removes a specific projectPolicyOwner value from the database.
   * 
   * @param projectPolicyOwnerId is the projectPolicyOwner identifier.
   * @return true if the projectPolicyOwner was successfully deleted, false otherwise.
   */
  public void deleteProjectPolicyOwner(long projectPolicyOwnerId);

  /**
   * This method validate if the projectPolicyOwner identify with the given id exists in the system.
   * 
   * @param projectPolicyOwnerID is a projectPolicyOwner identifier.
   * @return true if the projectPolicyOwner exists, false otherwise.
   */
  public boolean existProjectPolicyOwner(long projectPolicyOwnerID);

  /**
   * This method gets a projectPolicyOwner object by a given projectPolicyOwner identifier.
   * 
   * @param projectPolicyOwnerID is the projectPolicyOwner identifier.
   * @return a ProjectPolicyOwner object.
   */
  public ProjectPolicyOwner find(long id);

  /**
   * This method gets a list of projectPolicyOwner that are active
   * 
   * @return a list from ProjectPolicyOwner null if no exist records
   */
  public List<ProjectPolicyOwner> findAll();


  /**
   * This method saves the information of the given projectPolicyOwner
   * 
   * @param projectPolicyOwner - is the projectPolicyOwner object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the projectPolicyOwner was
   *         updated
   *         or -1 is some error occurred.
   */
  public ProjectPolicyOwner save(ProjectPolicyOwner projectPolicyOwner);
}
