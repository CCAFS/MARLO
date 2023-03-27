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

import org.cgiar.ccafs.marlo.data.model.ProjectInnovationCrpOutcome;

import java.util.List;


/**
 * @author CCAFS
 */

public interface ProjectInnovationCrpOutcomeManager {


  /**
   * This method removes a specific projectInnovationCrpOutcome value from the database.
   * 
   * @param projectInnovationCrpOutcomeId is the projectInnovationCrpOutcome identifier.
   * @return true if the projectInnovationCrpOutcome was successfully deleted, false otherwise.
   */
  public void deleteProjectInnovationCrpOutcome(long projectInnovationCrpOutcomeId, long phaseId);


  /**
   * This method validate if the projectInnovationCrpOutcome identify with the given id exists in the system.
   * 
   * @param projectInnovationCrpOutcomeID is a projectInnovationCrpOutcome identifier.
   * @return true if the projectInnovationCrpOutcome exists, false otherwise.
   */
  public boolean existProjectInnovationCrpOutcome(long projectInnovationCrpOutcomeID);


  /**
   * This method gets a list of projectInnovationCrpOutcome that are active
   * 
   * @return a list from ProjectInnovationCrpOutcome null if no exist records
   */
  public List<ProjectInnovationCrpOutcome> findAll();


  /**
   * This method gets a projectInnovationCrpOutcome object by a given projectInnovationCrpOutcome identifier.
   * 
   * @param projectInnovationCrpOutcomeID is the projectInnovationCrpOutcome identifier.
   * @return a ProjectInnovationCrpOutcome object.
   */
  public ProjectInnovationCrpOutcome getProjectInnovationCrpOutcomeById(long projectInnovationCrpOutcomeID);

  /**
   * This method saves the information of the given projectInnovationCrpOutcome
   * 
   * @param projectInnovationCrpOutcome - is the projectInnovationCrpOutcome object with the new information to be
   *        added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the
   *         projectInnovationCrpOutcome was
   *         updated
   *         or -1 is some error occurred.
   */
  public ProjectInnovationCrpOutcome
    saveProjectInnovationCrpOutcome(ProjectInnovationCrpOutcome projectInnovationCrpOutcome);


}
