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

import org.cgiar.ccafs.marlo.data.model.AllianceLever;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudy;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyLever;

import java.util.List;


/**
 * @author CCAFS
 */

public interface ProjectExpectedStudyLeverManager {


  /**
   * This method removes a specific projectExpectedStudyLeverOutcome value from the database.
   * 
   * @param projectExpectedStudyLeverOutcomeId is the projectExpectedStudyLeverOutcome identifier.
   * @return true if the projectExpectedStudyLeverOutcome was successfully deleted, false otherwise.
   */
  public void deleteProjectExpectedStudyLever(long projectExpectedStudyLeverOutcomeId);


  /**
   * This method validate if the projectExpectedStudyLeverOutcome identify with the given id exists in the system.
   * 
   * @param projectExpectedStudyLeverOutcomeID is a projectExpectedStudyLeverOutcome identifier.
   * @return true if the projectExpectedStudyLeverOutcome exists, false otherwise.
   */
  public boolean existProjectExpectedStudyLever(long projectExpectedStudyLeverOutcomeID);


  /**
   * This method gets a list of projectExpectedStudyLeverOutcome that are active
   * 
   * @return a list from ProjectExpectedStudyLever null if no exist records
   */
  public List<ProjectExpectedStudyLever> findAll();


  /**
   * This method gets a list of projectExpectedStudyLever by a given projectExpectedStudy identifier.
   * 
   * @param studyId is the projectExpectedStudy identifier.
   * @return a list of projectExpectedStudyLever objects.
   */
  public List<ProjectExpectedStudyLever> getAllStudyLeversByStudy(Long studyId);

  /**
   * This method gets a projectExpectedStudyLeverOutcome object by a given projectExpectedStudyLeverOutcome identifier.
   * 
   * @param projectExpectedStudyLeverOutcomeID is the projectExpectedStudyLeverOutcome identifier.
   * @return a ProjectExpectedStudyLever object.
   */
  public ProjectExpectedStudyLever getProjectExpectedStudyLeverById(long projectExpectedStudyLeverOutcomeID);

  /**
   * Gets a ProjectExpectedStudyLever by a study id, a lever id and a phase id
   * 
   * @param study the ProjectExpectedStudy
   * @param lever the AllianceLever
   * @param phase the Phase
   * @return a ProjectExpectedStudyLever if found; else null
   */
  public ProjectExpectedStudyLever getStudyLeverByStudyLeverAndPhase(ProjectExpectedStudy study, AllianceLever lever,
    Phase phase);

  /**
   * Replicates an studyLever, starting from the given phase
   * 
   * @param originalProjectExpectedStudyLever studyLever to be replicated
   * @param initialPhase initial replication phase
   */
  public void replicate(ProjectExpectedStudyLever originalProjectExpectedStudyLever, Phase initialPhase);

  /**
   * This method saves the information of the given projectExpectedStudyLeverOutcome
   * 
   * @param projectExpectedStudyLeverOutcome - is the projectExpectedStudyLeverOutcome object with the new information
   *        to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the
   *         projectExpectedStudyLeverOutcome was
   *         updated
   *         or -1 is some error occurred.
   */
  public ProjectExpectedStudyLever
    saveProjectExpectedStudyLever(ProjectExpectedStudyLever projectExpectedStudyLeverOutcome);

}
