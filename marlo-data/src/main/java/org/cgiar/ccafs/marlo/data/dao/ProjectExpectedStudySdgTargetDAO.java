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

import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudySdgTarget;

import java.util.List;


public interface ProjectExpectedStudySdgTargetDAO {

  /**
   * This method removes a specific projectExpectedStudySdgTarget value from the database.
   * 
   * @param projectExpectedStudySdgTargetId is the projectExpectedStudySdgTarget identifier.
   * @return true if the projectExpectedStudySdgTarget was successfully deleted, false otherwise.
   */
  public void deleteProjectExpectedStudySdgTarget(long projectExpectedStudySdgTargetId);

  /**
   * This method validate if the projectExpectedStudySdgTarget identify with the given id exists in the system.
   * 
   * @param projectExpectedStudySdgTargetID is a projectExpectedStudySdgTarget identifier.
   * @return true if the projectExpectedStudySdgTarget exists, false otherwise.
   */
  public boolean existProjectExpectedStudySdgTarget(long projectExpectedStudySdgTargetID);

  /**
   * This method gets a projectExpectedStudySdgTarget object by a given projectExpectedStudySdgTarget identifier.
   * 
   * @param projectExpectedStudySdgTargetID is the projectExpectedStudySdgTarget identifier.
   * @return a ProjectExpectedStudySdgTarget object.
   */
  public ProjectExpectedStudySdgTarget find(long id);

  /**
   * This method gets a list of projectExpectedStudySdgTarget that are active
   * 
   * @return a list from ProjectExpectedStudySdgTarget null if no exist records
   */
  public List<ProjectExpectedStudySdgTarget> findAll();


  /**
   * This method gets a list of projectExpectedStudySdgTarget by a given projectExpectedStudy identifier.
   * 
   * @param studyId is the projectExpectedStudy identifier.
   * @return a list of projectExpectedStudySdgTarget objects.
   */
  public List<ProjectExpectedStudySdgTarget> getAllStudySdgTargetsByStudy(long studyId);

  /**
   * Gets a ProjectExpectedStudySdgTarget by a study id, a sdg target id and a phase id
   * 
   * @param studyId the ProjectExpectedStudy id
   * @param sdgTargetId the SdgTargets id
   * @param idPhase the Phase id
   * @return a ProjectExpectedStudySdgTarget if found; else null
   */
  public ProjectExpectedStudySdgTarget getStudySdgTargetByStudySdgTargetAndPhase(long studyId, long sdgTargetId,
    long idPhase);

  /**
   * This method saves the information of the given projectExpectedStudySdgTarget
   * 
   * @param projectExpectedStudySdgTarget - is the projectExpectedStudySdgTarget object with the new information to be
   *        added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the
   *         projectExpectedStudySdgTarget was
   *         updated
   *         or -1 is some error occurred.
   */
  public ProjectExpectedStudySdgTarget save(ProjectExpectedStudySdgTarget projectExpectedStudySdgTarget);
}
