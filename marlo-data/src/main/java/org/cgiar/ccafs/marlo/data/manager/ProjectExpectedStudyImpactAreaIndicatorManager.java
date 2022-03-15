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

import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyImpactAreaIndicator;

import java.util.List;


/**
 * @author CCAFS
 */

public interface ProjectExpectedStudyImpactAreaIndicatorManager {


  /**
   * This method removes a specific projectExpectedStudyImpactAreaIndicator value from the database.
   * 
   * @param projectExpectedStudyImpactAreaIndicatorId is the projectExpectedStudyImpactAreaIndicator
   *        identifier.
   * @return true if the projectExpectedStudyImpactAreaIndicator was successfully deleted, false otherwise.
   */
  public void deleteProjectExpectedStudyImpactAreaIndicator(long projectExpectedStudyImpactAreaIndicatorId);


  /**
   * This method validate if the projectExpectedStudyImpactAreaIndicator identify with the given id exists in the
   * system.
   * 
   * @param projectExpectedStudyImpactAreaIndicatorID is a projectExpectedStudyImpactAreaIndicator
   *        identifier.
   * @return true if the projectExpectedStudyImpactAreaIndicator exists, false otherwise.
   */
  public boolean existProjectExpectedStudyImpactAreaIndicator(long projectExpectedStudyImpactAreaIndicatorID);


  /**
   * This method gets a list of projectExpectedStudyImpactAreaIndicator that are active
   * 
   * @return a list from ProjectExpectedStudyImpactAreaIndicator null if no exist records
   */
  public List<ProjectExpectedStudyImpactAreaIndicator> findAll();


  /**
   * This method gets a list of projectExpectedStudyImpactAreaIndicator by a given projectExpectedStudy
   * identifier.
   * 
   * @param studyId is the projectExpectedStudy identifier.
   * @return a list of projectExpectedStudyImpactAreaIndicator objects.
   */
  public List<ProjectExpectedStudyImpactAreaIndicator> getAllStudyImpactAreaIndicatorsByStudy(Long studyId);


  /**
   * This method gets a projectExpectedStudyImpactAreaIndicator object by a given
   * projectExpectedStudyImpactAreaIndicator identifier.
   * 
   * @param projectExpectedStudyImpactAreaIndicatorID is the projectExpectedStudyImpactAreaIndicator
   *        identifier.
   * @return a ProjectExpectedStudyImpactAreaIndicator object.
   */
  public ProjectExpectedStudyImpactAreaIndicator
    getProjectExpectedStudyImpactAreaIndicatorById(long projectExpectedStudyImpactAreaIndicatorID);


  /**
   * This method gets a projectExpectedStudyImpactAreaIndicator object by a given
   * projectExpectedStudyImpactAreaIndicator identifier.
   * 
   * @param expectedID is the projectExpectedStudyImpactAreaIndicator identifier.
   * @param impactAreaIndicatorID is the projectExpectedStudyImpactAreaIndicator identifier.
   * @param phaseID is the projectExpectedStudyImpactAreaIndicator identifier.
   * @return a ProjectExpectedStudyImpactAreaIndicator object.
   */
  public ProjectExpectedStudyImpactAreaIndicator getProjectExpectedStudyImpactAreaIndicatorByPhase(Long expectedID,
    Long impactAreaIndicatorID, Long phaseID);

  /**
   * This method saves the information of the given projectExpectedStudyImpactAreaIndicator
   * 
   * @param projectExpectedStudyImpactAreaIndicator - is the projectExpectedStudyImpactAreaIndicator
   *        object with the new information to be
   *        added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the
   *         projectExpectedStudyImpactAreaIndicator
   *         was
   *         updated
   *         or -1 is some error occurred.
   */
  public ProjectExpectedStudyImpactAreaIndicator saveProjectExpectedStudyImpactAreaIndicator(
    ProjectExpectedStudyImpactAreaIndicator projectExpectedStudyImpactAreaIndicator);
}
