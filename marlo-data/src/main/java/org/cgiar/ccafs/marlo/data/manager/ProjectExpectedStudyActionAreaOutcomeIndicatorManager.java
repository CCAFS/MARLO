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

import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyActionAreaOutcomeIndicator;

import java.util.List;


/**
 * @author CCAFS
 */

public interface ProjectExpectedStudyActionAreaOutcomeIndicatorManager {


  /**
   * This method removes a specific projectExpectedStudyActionAreaOutcomeIndicator value from the database.
   * 
   * @param projectExpectedStudyActionAreaOutcomeIndicatorId is the projectExpectedStudyActionAreaOutcomeIndicator
   *        identifier.
   * @return true if the projectExpectedStudyActionAreaOutcomeIndicator was successfully deleted, false otherwise.
   */
  public void
    deleteProjectExpectedStudyActionAreaOutcomeIndicator(long projectExpectedStudyActionAreaOutcomeIndicatorId);


  /**
   * This method validate if the projectExpectedStudyActionAreaOutcomeIndicator identify with the given id exists in the
   * system.
   * 
   * @param projectExpectedStudyActionAreaOutcomeIndicatorID is a projectExpectedStudyActionAreaOutcomeIndicator
   *        identifier.
   * @return true if the projectExpectedStudyActionAreaOutcomeIndicator exists, false otherwise.
   */
  public boolean
    existProjectExpectedStudyActionAreaOutcomeIndicator(long projectExpectedStudyActionAreaOutcomeIndicatorID);


  /**
   * This method gets a list of projectExpectedStudyActionAreaOutcomeIndicator that are active
   * 
   * @return a list from ProjectExpectedStudyActionAreaOutcomeIndicator null if no exist records
   */
  public List<ProjectExpectedStudyActionAreaOutcomeIndicator> findAll();


  /**
   * This method gets a list of projectExpectedStudyActionAreaOutcomeIndicator by a given projectExpectedStudy
   * identifier.
   * 
   * @param studyId is the projectExpectedStudy identifier.
   * @return a list of projectExpectedStudyActionAreaOutcomeIndicator objects.
   */
  public List<ProjectExpectedStudyActionAreaOutcomeIndicator>
    getAllStudyActionAreaOutcomeIndicatorsByStudy(Long studyId);


  /**
   * This method gets a projectExpectedStudyActionAreaOutcomeIndicator object by a given
   * projectExpectedStudyActionAreaOutcomeIndicator identifier.
   * 
   * @param projectExpectedStudyActionAreaOutcomeIndicatorID is the projectExpectedStudyActionAreaOutcomeIndicator
   *        identifier.
   * @return a ProjectExpectedStudyActionAreaOutcomeIndicator object.
   */
  public ProjectExpectedStudyActionAreaOutcomeIndicator
    getProjectExpectedStudyActionAreaOutcomeIndicatorById(long projectExpectedStudyActionAreaOutcomeIndicatorID);


  /**
   * This method gets a projectExpectedStudyActionAreaOutcomeIndicator object by a given
   * projectExpectedStudyActionAreaOutcomeIndicator identifier.
   * 
   * @param expectedID is the projectExpectedStudyActionAreaOutcomeIndicator identifier.
   * @param actionAreaOutcomeIndicatorID is the projectExpectedStudyActionAreaOutcomeIndicator identifier.
   * @param phaseID is the projectExpectedStudyActionAreaOutcomeIndicator identifier.
   * @return a ProjectExpectedStudyActionAreaOutcomeIndicator object.
   */
  public ProjectExpectedStudyActionAreaOutcomeIndicator getProjectExpectedStudyActionAreaOutcomeIndicatorByPhase(
    Long expectedID, Long actionAreaOutcomeIndicatorID, Long phaseID);

  /**
   * This method saves the information of the given projectExpectedStudyActionAreaOutcomeIndicator
   * 
   * @param projectExpectedStudyActionAreaOutcomeIndicator - is the projectExpectedStudyActionAreaOutcomeIndicator
   *        object with the new information to be
   *        added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the
   *         projectExpectedStudyActionAreaOutcomeIndicator
   *         was
   *         updated
   *         or -1 is some error occurred.
   */
  public ProjectExpectedStudyActionAreaOutcomeIndicator saveProjectExpectedStudyActionAreaOutcomeIndicator(
    ProjectExpectedStudyActionAreaOutcomeIndicator projectExpectedStudyActionAreaOutcomeIndicator);
}
