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

import org.cgiar.ccafs.marlo.data.model.ProjectPolicySdgTarget;

import java.util.List;


public interface ProjectPolicySdgTargetDAO {

  /**
   * This method removes a specific projectPolicySdgTarget value from the database.
   * 
   * @param projectPolicySdgTargetId is the projectPolicySdgTarget identifier.
   * @return true if the projectPolicySdgTarget was successfully deleted, false otherwise.
   */
  public void deleteProjectPolicySdgTarget(long projectPolicySdgTargetId);

  /**
   * This method validate if the projectPolicySdgTarget identify with the given id exists in the system.
   * 
   * @param projectPolicySdgTargetID is a projectPolicySdgTarget identifier.
   * @return true if the projectPolicySdgTarget exists, false otherwise.
   */
  public boolean existProjectPolicySdgTarget(long projectPolicySdgTargetID);

  /**
   * This method gets a projectPolicySdgTarget object by a given projectPolicySdgTarget identifier.
   * 
   * @param projectPolicySdgTargetID is the projectPolicySdgTarget identifier.
   * @return a ProjectPolicySdgTarget object.
   */
  public ProjectPolicySdgTarget find(long id);

  /**
   * This method gets a list of projectPolicySdgTarget that are active
   * 
   * @return a list from ProjectPolicySdgTarget null if no exist records
   */
  public List<ProjectPolicySdgTarget> findAll();


  /**
   * This method gets a list of projectPolicySdgTarget by a given projectPolicy identifier.
   * 
   * @param policyId is the projectPolicy identifier.
   * @return a list of projectPolicySdgTarget objects.
   */
  public List<ProjectPolicySdgTarget> getAllPolicySdgTargetsByPolicy(long policyId);

  /**
   * Gets a ProjectPolicySdgTarget by a policy id, a sdg target id and a phase id
   * 
   * @param policyId the ProjectPolicy id
   * @param sdgTargetId the SdgTargets id
   * @param idPhase the Phase id
   * @return a ProjectPolicySdgTarget if found; else null
   */
  public ProjectPolicySdgTarget getPolicySdgTargetByPolicySdgTargetAndPhase(long policyId, long sdgTargetId,
    long idPhase);

  /**
   * This method saves the information of the given projectPolicySdgTarget
   * 
   * @param projectPolicySdgTarget - is the projectPolicySdgTarget object with the new information to be
   *        added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the
   *         projectPolicySdgTarget was
   *         updated
   *         or -1 is some error occurred.
   */
  public ProjectPolicySdgTarget save(ProjectPolicySdgTarget projectPolicySdgTarget);
}
