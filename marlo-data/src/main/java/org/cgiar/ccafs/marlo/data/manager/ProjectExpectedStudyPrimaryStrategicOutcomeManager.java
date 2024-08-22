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

import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyPrimaryStrategicOutcome;

import java.util.List;


/**
 * @author CCAFS
 */

public interface ProjectExpectedStudyPrimaryStrategicOutcomeManager {


  /**
   * This method removes a specific projectExpectedStudyPrimaryStrategicOutcome value from the database.
   * 
   * @param projectExpectedStudyPrimaryStrategicOutcomeId is the projectExpectedStudyPrimaryStrategicOutcome identifier.
   * @return true if the projectExpectedStudyPrimaryStrategicOutcome was successfully deleted, false otherwise.
   */
  public void deleteProjectExpectedStudyPrimaryStrategicOutcome(long projectExpectedStudyPrimaryStrategicOutcomeId);


  /**
   * This method validate if the projectExpectedStudyPrimaryStrategicOutcome identify with the given id exists in the system.
   * 
   * @param projectExpectedStudyPrimaryStrategicOutcomeID is a projectExpectedStudyPrimaryStrategicOutcome identifier.
   * @return true if the projectExpectedStudyPrimaryStrategicOutcome exists, false otherwise.
   */
  public boolean existProjectExpectedStudyPrimaryStrategicOutcome(long projectExpectedStudyPrimaryStrategicOutcomeID);


  /**
   * This method gets a list of projectExpectedStudyPrimaryStrategicOutcome that are active
   * 
   * @return a list from ProjectExpectedStudyPrimaryStrategicOutcome null if no exist records
   */
  public List<ProjectExpectedStudyPrimaryStrategicOutcome> findAll();


  /**
   * This method gets a projectExpectedStudyPrimaryStrategicOutcome object by a given projectExpectedStudyPrimaryStrategicOutcome identifier.
   * 
   * @param projectExpectedStudyPrimaryStrategicOutcomeID is the projectExpectedStudyPrimaryStrategicOutcome identifier.
   * @return a ProjectExpectedStudyPrimaryStrategicOutcome object.
   */
  public ProjectExpectedStudyPrimaryStrategicOutcome getProjectExpectedStudyPrimaryStrategicOutcomeById(long projectExpectedStudyPrimaryStrategicOutcomeID);

  /**
   * This method saves the information of the given projectExpectedStudyPrimaryStrategicOutcome
   * 
   * @param projectExpectedStudyPrimaryStrategicOutcome - is the projectExpectedStudyPrimaryStrategicOutcome object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the projectExpectedStudyPrimaryStrategicOutcome was
   *         updated
   *         or -1 is some error occurred.
   */
  public ProjectExpectedStudyPrimaryStrategicOutcome saveProjectExpectedStudyPrimaryStrategicOutcome(ProjectExpectedStudyPrimaryStrategicOutcome projectExpectedStudyPrimaryStrategicOutcome);


}
