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

import org.cgiar.ccafs.marlo.data.model.ProjectInnovationSdgTarget;

import java.util.List;


public interface ProjectInnovationSdgTargetDAO {

  /**
   * This method removes a specific projectInnovationSdgTarget value from the database.
   * 
   * @param projectInnovationSdgTargetId is the projectInnovationSdgTarget identifier.
   * @return true if the projectInnovationSdgTarget was successfully deleted, false otherwise.
   */
  public void deleteProjectInnovationSdgTarget(long projectInnovationSdgTargetId);

  /**
   * This method validate if the projectInnovationSdgTarget identify with the given id exists in the system.
   * 
   * @param projectInnovationSdgTargetID is a projectInnovationSdgTarget identifier.
   * @return true if the projectInnovationSdgTarget exists, false otherwise.
   */
  public boolean existProjectInnovationSdgTarget(long projectInnovationSdgTargetID);

  /**
   * This method gets a projectInnovationSdgTarget object by a given projectInnovationSdgTarget identifier.
   * 
   * @param projectInnovationSdgTargetID is the projectInnovationSdgTarget identifier.
   * @return a ProjectInnovationSdgTarget object.
   */
  public ProjectInnovationSdgTarget find(long id);

  /**
   * This method gets a list of projectInnovationSdgTarget that are active
   * 
   * @return a list from ProjectInnovationSdgTarget null if no exist records
   */
  public List<ProjectInnovationSdgTarget> findAll();


  /**
   * This method gets a list of projectInnovationSdgTarget by a given projectInnovation identifier.
   * 
   * @param innovationId is the projectInnovation identifier.
   * @return a list of projectInnovationSdgTarget objects.
   */
  public List<ProjectInnovationSdgTarget> getAllInnovationSdgTargetsByInnovation(long innovationId);

  /**
   * Gets a ProjectInnovationSdgTarget by a innovation id, a sdg target id and a phase id
   * 
   * @param innovationId the ProjectInnovation id
   * @param sdgTargetId the SdgTargets id
   * @param idPhase the Phase id
   * @return a ProjectInnovationSdgTarget if found; else null
   */
  public ProjectInnovationSdgTarget getInnovationSdgTargetByInnovationSdgTargetAndPhase(long innovationId,
    long sdgTargetId, long idPhase);

  /**
   * This method saves the information of the given projectInnovationSdgTarget
   * 
   * @param projectInnovationSdgTarget - is the projectInnovationSdgTarget object with the new information to be
   *        added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the
   *         projectInnovationSdgTarget was
   *         updated
   *         or -1 is some error occurred.
   */
  public ProjectInnovationSdgTarget save(ProjectInnovationSdgTarget projectInnovationSdgTarget);
}
