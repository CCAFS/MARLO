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

import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyAllianceLeversOutcome;

import java.util.List;


public interface ProjectExpectedStudyAllianceLeversOutcomeDAO {

  /**
   * This method removes a specific projectExpectedStudyAllianceLeversOutcome value from the database.
   * 
   * @param projectExpectedStudyAllianceLeversOutcomeId is the projectExpectedStudyAllianceLeversOutcome identifier.
   * @return true if the projectExpectedStudyAllianceLeversOutcome was successfully deleted, false otherwise.
   */
  public void deleteProjectExpectedStudyAllianceLeversOutcome(long projectExpectedStudyAllianceLeversOutcomeId);

  /**
   * This method validate if the projectExpectedStudyAllianceLeversOutcome identify with the given id exists in the
   * system.
   * 
   * @param projectExpectedStudyAllianceLeversOutcomeID is a projectExpectedStudyAllianceLeversOutcome identifier.
   * @return true if the projectExpectedStudyAllianceLeversOutcome exists, false otherwise.
   */
  public boolean existProjectExpectedStudyAllianceLeversOutcome(long projectExpectedStudyAllianceLeversOutcomeID);

  /**
   * This method gets a projectExpectedStudyAllianceLeversOutcome object by a given
   * projectExpectedStudyAllianceLeversOutcome identifier.
   * 
   * @param projectExpectedStudyAllianceLeversOutcomeID is the projectExpectedStudyAllianceLeversOutcome identifier.
   * @return a ProjectExpectedStudyAllianceLeversOutcome object.
   */
  public ProjectExpectedStudyAllianceLeversOutcome find(long id);

  /**
   * This method gets a list of projectExpectedStudyAllianceLeversOutcome that are active
   * 
   * @return a list from ProjectExpectedStudyAllianceLeversOutcome null if no exist records
   */
  public List<ProjectExpectedStudyAllianceLeversOutcome> findAll();


  ProjectExpectedStudyAllianceLeversOutcome findByExpectedAndPhaseAndLeverAndOutcome(long expectedId, long phaseId,
    long leverId, long outcomeId);

  /**
   * This method saves the information of the given projectExpectedStudyAllianceLeversOutcome
   * 
   * @param projectExpectedStudyAllianceLeversOutcome - is the projectExpectedStudyAllianceLeversOutcome object with the
   *        new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the
   *         projectExpectedStudyAllianceLeversOutcome was
   *         updated
   *         or -1 is some error occurred.
   */
  public ProjectExpectedStudyAllianceLeversOutcome
    save(ProjectExpectedStudyAllianceLeversOutcome projectExpectedStudyAllianceLeversOutcome);
}
