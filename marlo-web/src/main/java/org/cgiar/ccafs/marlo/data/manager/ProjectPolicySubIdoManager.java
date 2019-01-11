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

import org.cgiar.ccafs.marlo.data.model.ProjectPolicySubIdo;

import java.util.List;


/**
 * @author CCAFS
 */

public interface ProjectPolicySubIdoManager {


  /**
   * This method removes a specific projectPolicySubIdo value from the database.
   * 
   * @param projectPolicySubIdoId is the projectPolicySubIdo identifier.
   * @return true if the projectPolicySubIdo was successfully deleted, false otherwise.
   */
  public void deleteProjectPolicySubIdo(long projectPolicySubIdoId);


  /**
   * This method validate if the projectPolicySubIdo identify with the given id exists in the system.
   * 
   * @param projectPolicySubIdoID is a projectPolicySubIdo identifier.
   * @return true if the projectPolicySubIdo exists, false otherwise.
   */
  public boolean existProjectPolicySubIdo(long projectPolicySubIdoID);


  /**
   * This method gets a list of projectPolicySubIdo that are active
   * 
   * @return a list from ProjectPolicySubIdo null if no exist records
   */
  public List<ProjectPolicySubIdo> findAll();


  /**
   * This method gets a projectPolicySubIdo object by a given projectPolicySubIdo identifier.
   * 
   * @param projectPolicySubIdoID is the projectPolicySubIdo identifier.
   * @return a ProjectPolicySubIdo object.
   */
  public ProjectPolicySubIdo getProjectPolicySubIdoById(long projectPolicySubIdoID);

  /**
   * This method saves the information of the given projectPolicySubIdo
   * 
   * @param projectPolicySubIdo - is the projectPolicySubIdo object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the projectPolicySubIdo was
   *         updated
   *         or -1 is some error occurred.
   */
  public ProjectPolicySubIdo saveProjectPolicySubIdo(ProjectPolicySubIdo projectPolicySubIdo);


}
