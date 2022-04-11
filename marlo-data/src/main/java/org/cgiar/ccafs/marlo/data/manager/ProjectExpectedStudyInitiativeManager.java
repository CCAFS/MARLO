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

import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyInitiative;

import java.util.List;


/**
 * @author CCAFS
 */

public interface ProjectExpectedStudyInitiativeManager {


  /**
   * This method removes a specific projectExpectedStudyInitiative value from the database.
   * 
   * @param projectExpectedStudyInitiativeId is the projectExpectedStudyInitiative
   *        identifier.
   * @return true if the projectExpectedStudyInitiative was successfully deleted, false otherwise.
   */
  public void deleteProjectExpectedStudyInitiative(long projectExpectedStudyInitiativeId);


  /**
   * This method validate if the projectExpectedStudyInitiative identify with the given id exists in the
   * system.
   * 
   * @param projectExpectedStudyInitiativeID is a projectExpectedStudyInitiative
   *        identifier.
   * @return true if the projectExpectedStudyInitiative exists, false otherwise.
   */
  public boolean existProjectExpectedStudyInitiative(long projectExpectedStudyInitiativeID);


  /**
   * This method gets a list of projectExpectedStudyInitiative that are active
   * 
   * @return a list from ProjectExpectedStudyInitiative null if no exist records
   */
  public List<ProjectExpectedStudyInitiative> findAll();


  /**
   * This method gets a list of projectExpectedStudyInitiative by a given projectExpectedStudy
   * identifier.
   * 
   * @param studyId is the projectExpectedStudy identifier.
   * @return a list of projectExpectedStudyInitiative objects.
   */
  public List<ProjectExpectedStudyInitiative> getAllStudyInitiativesByStudy(Long studyId);


  /**
   * This method gets a projectExpectedStudyInitiative object by a given
   * projectExpectedStudyInitiative identifier.
   * 
   * @param projectExpectedStudyInitiativeID is the projectExpectedStudyInitiative
   *        identifier.
   * @return a ProjectExpectedStudyInitiative object.
   */
  public ProjectExpectedStudyInitiative getProjectExpectedStudyInitiativeById(long projectExpectedStudyInitiativeID);


  /**
   * This method gets a projectExpectedStudyInitiative object by a given
   * projectExpectedStudyInitiative identifier.
   * 
   * @param expectedID is the projectExpectedStudyInitiative identifier.
   * @param initiativeID is the projectExpectedStudyInitiative identifier.
   * @param phaseID is the projectExpectedStudyInitiative identifier.
   * @return a ProjectExpectedStudyInitiative object.
   */
  public ProjectExpectedStudyInitiative getProjectExpectedStudyInitiativeByPhase(Long expectedID, Long initiativeID,
    Long phaseID);

  /**
   * This method saves the information of the given projectExpectedStudyInitiative
   * 
   * @param projectExpectedStudyInitiative - is the projectExpectedStudyInitiative
   *        object with the new information to be
   *        added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the
   *         projectExpectedStudyInitiative
   *         was
   *         updated
   *         or -1 is some error occurred.
   */
  public ProjectExpectedStudyInitiative
    saveProjectExpectedStudyInitiative(ProjectExpectedStudyInitiative projectExpectedStudyInitiative);
}
