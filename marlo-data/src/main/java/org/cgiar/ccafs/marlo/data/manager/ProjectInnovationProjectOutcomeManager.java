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

import org.cgiar.ccafs.marlo.data.model.ProjectInnovationProjectOutcome;

import java.util.List;


/**
 * @author CCAFS
 */

public interface ProjectInnovationProjectOutcomeManager {


  /**
   * This method removes a specific projectInnovationProjectOutcome value from the database.
   * 
   * @param projectInnovationProjectOutcomeId is the projectInnovationProjectOutcome identifier.
   * @return true if the projectInnovationProjectOutcome was successfully deleted, false otherwise.
   */
  public void deleteProjectInnovationProjectOutcome(long projectInnovationProjectOutcomeId, long phaseId);


  /**
   * This method validate if the projectInnovationProjectOutcome identify with the given id exists in the system.
   * 
   * @param projectInnovationProjectOutcomeID is a projectInnovationProjectOutcome identifier.
   * @return true if the projectInnovationProjectOutcome exists, false otherwise.
   */
  public boolean existProjectInnovationProjectOutcome(long projectInnovationProjectOutcomeID);


  /**
   * This method gets a list of projectInnovationProjectOutcome that are active
   * 
   * @return a list from ProjectInnovationProjectOutcome null if no exist records
   */
  public List<ProjectInnovationProjectOutcome> findAll();


  /**
   * This method gets a projectInnovationProjectOutcome object by a given projectInnovationProjectOutcome identifier.
   * 
   * @param projectInnovationProjectOutcomeID is the projectInnovationProjectOutcome identifier.
   * @return a ProjectInnovationProjectOutcome object.
   */
  public ProjectInnovationProjectOutcome getProjectInnovationProjectOutcomeById(long projectInnovationProjectOutcomeID);

  /**
   * This method saves the information of the given projectInnovationProjectOutcome
   * 
   * @param projectInnovationProjectOutcome - is the projectInnovationProjectOutcome object with the new information to
   *        be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the
   *         projectInnovationProjectOutcome was
   *         updated
   *         or -1 is some error occurred.
   */
  public ProjectInnovationProjectOutcome
    saveProjectInnovationProjectOutcome(ProjectInnovationProjectOutcome projectInnovationProjectOutcome);


}
