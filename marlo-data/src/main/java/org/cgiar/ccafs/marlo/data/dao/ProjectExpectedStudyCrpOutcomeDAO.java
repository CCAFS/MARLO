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

import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyCrpOutcome;

import java.util.List;


public interface ProjectExpectedStudyCrpOutcomeDAO {

  /**
   * This method removes a specific projectExpectedStudyCrpOutcome value from the database.
   * 
   * @param projectExpectedStudyCrpOutcomeId is the projectExpectedStudyCrpOutcome identifier.
   * @return true if the projectExpectedStudyCrpOutcome was successfully deleted, false otherwise.
   */
  public void deleteProjectExpectedStudyCrpOutcome(long projectExpectedStudyCrpOutcomeId);

  /**
   * This method removes a specific projectExpectedStudyCrpOutcome value from the database.
   * 
   * @param projectExpectedStudyCrpOutcomeId is the projectExpectedStudyCrpOutcome identifier.
   * @param phaseId.
   * @return true if the projectExpectedStudyCrpOutcome was successfully deleted, false otherwise.
   */
  public void deleteProjectExpectedStudyCrpOutcome(long projectExpectedStudyCrpOutcomeId, long phaseId);

  /**
   * This method validate if the projectExpectedStudyCrpOutcome identify with the given id exists in the system.
   * 
   * @param projectExpectedStudyCrpOutcomeID is a projectExpectedStudyCrpOutcome identifier.
   * @return true if the projectExpectedStudyCrpOutcome exists, false otherwise.
   */
  public boolean existProjectExpectedStudyCrpOutcome(long projectExpectedStudyCrpOutcomeID);

  /**
   * This method gets a projectExpectedStudyCrpOutcome object by a given projectExpectedStudyCrpOutcome identifier.
   * 
   * @param projectExpectedStudyCrpOutcomeID is the projectExpectedStudyCrpOutcome identifier.
   * @return a ProjectExpectedStudyCrpOutcome object.
   */
  public ProjectExpectedStudyCrpOutcome find(long id);

  /**
   * This method gets a list of projectExpectedStudyCrpOutcome that are active
   * 
   * @return a list from ProjectExpectedStudyCrpOutcome null if no exist records
   */
  public List<ProjectExpectedStudyCrpOutcome> findAll();


  /**
   * This method saves the information of the given projectExpectedStudyCrpOutcome
   * 
   * @param projectExpectedStudyCrpOutcome - is the projectExpectedStudyCrpOutcome object with the new information to be
   *        added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the
   *         projectExpectedStudyCrpOutcome was
   *         updated
   *         or -1 is some error occurred.
   */
  public ProjectExpectedStudyCrpOutcome save(ProjectExpectedStudyCrpOutcome projectExpectedStudyCrpOutcome);
}
