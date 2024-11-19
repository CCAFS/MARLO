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

import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyGlobalTarget;

import java.util.List;


public interface ProjectExpectedStudyGlobalTargetDAO {

  /**
   * This method removes a specific projectExpectedStudyGlobalTarget value from the database.
   * 
   * @param projectExpectedStudyGlobalTargetId is the projectExpectedStudyGlobalTarget identifier.
   * @return true if the projectExpectedStudyGlobalTarget was successfully deleted, false otherwise.
   */
  public void deleteProjectExpectedStudyGlobalTarget(long projectExpectedStudyGlobalTargetId);

  /**
   * This method validate if the projectExpectedStudyGlobalTarget identify with the given id exists in the system.
   * 
   * @param projectExpectedStudyGlobalTargetID is a projectExpectedStudyGlobalTarget identifier.
   * @return true if the projectExpectedStudyGlobalTarget exists, false otherwise.
   */
  public boolean existProjectExpectedStudyGlobalTarget(long projectExpectedStudyGlobalTargetID);

  /**
   * This method gets a projectExpectedStudyGlobalTarget object by a given projectExpectedStudyGlobalTarget identifier.
   * 
   * @param projectExpectedStudyGlobalTargetID is the projectExpectedStudyGlobalTarget identifier.
   * @return a ProjectExpectedStudyGlobalTarget object.
   */
  public ProjectExpectedStudyGlobalTarget find(long id);

  /**
   * This method gets a list of projectExpectedStudyGlobalTarget that are active
   * 
   * @return a list from ProjectExpectedStudyGlobalTarget null if no exist records
   */
  public List<ProjectExpectedStudyGlobalTarget> findAll();


  ProjectExpectedStudyGlobalTarget findByExpectedAndGlobalAndPhase(long expectedId, long targetId, long phaseId);

  /**
   * This method saves the information of the given projectExpectedStudyGlobalTarget
   * 
   * @param projectExpectedStudyGlobalTarget - is the projectExpectedStudyGlobalTarget object with the new information
   *        to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the
   *         projectExpectedStudyGlobalTarget was
   *         updated
   *         or -1 is some error occurred.
   */
  public ProjectExpectedStudyGlobalTarget save(ProjectExpectedStudyGlobalTarget projectExpectedStudyGlobalTarget);
}
