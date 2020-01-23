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

import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyCenter;

import java.util.List;

public interface ProjectExpectedStudyCenterDAO {

  /**
   * This method removes a specific projectExpectedStudyCenter value from the database.
   * 
   * @param projectExpectedStudyCenterId is the projectExpectedStudyCenter identifier.
   * @return true if the projectExpectedStudyCenter was successfully deleted, false otherwise.
   */
  public void deleteProjectExpectedStudyCenter(long projectExpectedStudyCenterId);

  /**
   * This method validate if the projectExpectedStudyCenter identify with the given id exists in the system.
   * 
   * @param projectExpectedStudyCenterID is a projectExpectedStudyCenter identifier.
   * @return true if the projectExpectedStudyCenter exists, false otherwise.
   */
  public boolean existProjectExpectedStudyCenter(long projectExpectedStudyCenterID);

  /**
   * This method gets a projectExpectedStudyCenter object by a given projectExpectedStudyCenter identifier.
   * 
   * @param projectExpectedStudyCenterID is the projectExpectedStudyCenter identifier.
   * @return a projectExpectedStudyCenter object.
   */
  public ProjectExpectedStudyCenter find(long id);

  /**
   * This method gets a list of projectExpectedStudyCenter that are active
   * 
   * @return a list from projectExpectedStudyCenter null if no exist records
   */
  public List<ProjectExpectedStudyCenter> findAll();


  /**
   * This method saves the information of the given projectExpectedStudyCenter
   * 
   * @param projectExpectedStudyCenter - is the projectExpectedStudyCenter object with the new information to be
   *        added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the
   *         projectExpectedStudyCenter
   *         was
   *         updated
   *         or -1 is some error occurred.
   */
  public ProjectExpectedStudyCenter save(ProjectExpectedStudyCenter projectExpectedStudyCenter);
}
