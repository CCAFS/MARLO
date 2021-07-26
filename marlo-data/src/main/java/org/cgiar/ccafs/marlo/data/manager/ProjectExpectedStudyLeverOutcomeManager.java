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

import org.cgiar.ccafs.marlo.data.model.AllianceLeverOutcome;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudy;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyLeverOutcome;

import java.util.List;


/**
 * @author CCAFS
 */

public interface ProjectExpectedStudyLeverOutcomeManager {


  /**
   * This method removes a specific projectExpectedStudyLeverOutcome value from the database.
   * 
   * @param projectExpectedStudyLeverOutcomeId is the projectExpectedStudyLeverOutcome identifier.
   * @return true if the projectExpectedStudyLeverOutcome was successfully deleted, false otherwise.
   */
  public void deleteProjectExpectedStudyLeverOutcome(long projectExpectedStudyLeverOutcomeId);


  /**
   * This method validate if the projectExpectedStudyLeverOutcome identify with the given id exists in the system.
   * 
   * @param projectExpectedStudyLeverOutcomeID is a projectExpectedStudyLeverOutcome identifier.
   * @return true if the projectExpectedStudyLeverOutcome exists, false otherwise.
   */
  public boolean existProjectExpectedStudyLeverOutcome(long projectExpectedStudyLeverOutcomeID);


  /**
   * This method gets a list of projectExpectedStudyLeverOutcome that are active
   * 
   * @return a list from ProjectExpectedStudyLeverOutcome null if no exist records
   */
  public List<ProjectExpectedStudyLeverOutcome> findAll();


  /**
   * This method gets a list of projectExpectedStudyLeverOutcome by a given projectExpectedStudy identifier.
   * 
   * @param studyId is the projectExpectedStudy identifier.
   * @return a list of projectExpectedStudyLeverOutcome objects.
   */
  public List<ProjectExpectedStudyLeverOutcome> getAllStudyLeverOutcomesByStudy(Long studyId);

  /**
   * This method gets a projectExpectedStudyLeverOutcome object by a given projectExpectedStudyLeverOutcome identifier.
   * 
   * @param projectExpectedStudyLeverOutcomeID is the projectExpectedStudyLeverOutcome identifier.
   * @return a ProjectExpectedStudyLeverOutcome object.
   */
  public ProjectExpectedStudyLeverOutcome
    getProjectExpectedStudyLeverOutcomeById(long projectExpectedStudyLeverOutcomeID);

  /**
   * Gets a ProjectExpectedStudyLeverOutcome by a study, a lever outcome and a phase
   * 
   * @param study the ProjectExpectedStudy
   * @param leverOutcome the AllianceLeverOutcome
   * @param idPhase the Phase
   * @return a ProjectExpectedStudyLeverOutcome if found; else null
   */
  public ProjectExpectedStudyLeverOutcome getStudyLeverOutcomeByStudyLeverOutcomeAndPhase(ProjectExpectedStudy study,
    AllianceLeverOutcome leverOutcome, Phase phase);

  /**
   * Replicates an studyLeverOutcome, starting from the given phase
   * 
   * @param originalProjectExpectedStudyLeverOutcome studyLever to be replicated
   * @param initialPhase initial replication phase
   */
  public void replicate(ProjectExpectedStudyLeverOutcome originalProjectExpectedStudyLeverOutcome, Phase initialPhase);

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
  public ProjectExpectedStudyLeverOutcome
    saveProjectExpectedStudyLeverOutcome(ProjectExpectedStudyLeverOutcome projectExpectedStudyLeverOutcome);
}
