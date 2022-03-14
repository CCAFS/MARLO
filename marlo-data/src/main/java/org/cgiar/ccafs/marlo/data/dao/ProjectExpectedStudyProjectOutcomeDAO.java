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

import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyProjectOutcome;

import java.util.List;


public interface ProjectExpectedStudyProjectOutcomeDAO {

  /**
   * This method removes a specific projectExpectedStudyProjectOutcome value from the database.
   * 
   * @param projectExpectedStudyProjectOutcomeId is the projectExpectedStudyProjectOutcome identifier.
   * @return true if the projectExpectedStudyProjectOutcome was successfully deleted, false otherwise.
   */
  public void deleteProjectExpectedStudyProjectOutcome(long projectExpectedStudyProjectOutcomeId);

  /**
   * This method validate if the projectExpectedStudyProjectOutcome identify with the given id exists in the system.
   * 
   * @param projectExpectedStudyProjectOutcomeID is a projectExpectedStudyProjectOutcome identifier.
   * @return true if the projectExpectedStudyProjectOutcome exists, false otherwise.
   */
  public boolean existProjectExpectedStudyProjectOutcome(long projectExpectedStudyProjectOutcomeID);

  /**
   * This method gets a projectExpectedStudyProjectOutcome object by a given projectExpectedStudyProjectOutcome identifier.
   * 
   * @param projectExpectedStudyProjectOutcomeID is the projectExpectedStudyProjectOutcome identifier.
   * @return a ProjectExpectedStudyProjectOutcome object.
   */
  public ProjectExpectedStudyProjectOutcome find(long id);

  /**
   * This method gets a list of projectExpectedStudyProjectOutcome that are active
   * 
   * @return a list from ProjectExpectedStudyProjectOutcome null if no exist records
   */
  public List<ProjectExpectedStudyProjectOutcome> findAll();


  /**
   * This method saves the information of the given projectExpectedStudyProjectOutcome
   * 
   * @param projectExpectedStudyProjectOutcome - is the projectExpectedStudyProjectOutcome object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the projectExpectedStudyProjectOutcome was
   *         updated
   *         or -1 is some error occurred.
   */
  public ProjectExpectedStudyProjectOutcome save(ProjectExpectedStudyProjectOutcome projectExpectedStudyProjectOutcome);
}
