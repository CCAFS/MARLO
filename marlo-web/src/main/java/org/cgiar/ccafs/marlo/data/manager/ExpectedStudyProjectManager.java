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

import org.cgiar.ccafs.marlo.data.model.ExpectedStudyProject;

import java.util.List;


/**
 * @author Christian Garcia
 */

public interface ExpectedStudyProjectManager {


  /**
   * This method removes a specific expectedStudyProject value from the database.
   * 
   * @param expectedStudyProjectId is the expectedStudyProject identifier.
   * @return true if the expectedStudyProject was successfully deleted, false otherwise.
   */
  public void deleteExpectedStudyProject(long expectedStudyProjectId);


  /**
   * This method validate if the expectedStudyProject identify with the given id exists in the system.
   * 
   * @param expectedStudyProjectID is a expectedStudyProject identifier.
   * @return true if the expectedStudyProject exists, false otherwise.
   */
  public boolean existExpectedStudyProject(long expectedStudyProjectID);


  /**
   * This method gets a list of expectedStudyProject that are active
   * 
   * @return a list from ExpectedStudyProject null if no exist records
   */
  public List<ExpectedStudyProject> findAll();


  /**
   * This method gets a expectedStudyProject object by a given expectedStudyProject identifier.
   * 
   * @param expectedStudyProjectID is the expectedStudyProject identifier.
   * @return a ExpectedStudyProject object.
   */
  public ExpectedStudyProject getExpectedStudyProjectById(long expectedStudyProjectID);

  /**
   * This method saves the information of the given expectedStudyProject
   * 
   * @param expectedStudyProject - is the expectedStudyProject object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the expectedStudyProject was
   *         updated
   *         or -1 is some error occurred.
   */
  public ExpectedStudyProject saveExpectedStudyProject(ExpectedStudyProject expectedStudyProject);


}
