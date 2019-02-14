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

import org.cgiar.ccafs.marlo.data.model.ProjectPolicyRegion;

import java.util.List;


public interface ProjectPolicyRegionDAO {

  /**
   * This method removes a specific projectPolicyRegion value from the database.
   * 
   * @param projectPolicyRegionId is the projectPolicyRegion identifier.
   * @return true if the projectPolicyRegion was successfully deleted, false otherwise.
   */
  public void deleteProjectPolicyRegion(long projectPolicyRegionId);

  /**
   * This method validate if the projectPolicyRegion identify with the given id exists in the system.
   * 
   * @param projectPolicyRegionID is a projectPolicyRegion identifier.
   * @return true if the projectPolicyRegion exists, false otherwise.
   */
  public boolean existProjectPolicyRegion(long projectPolicyRegionID);

  /**
   * This method gets a projectPolicyRegion object by a given projectPolicyRegion identifier.
   * 
   * @param projectPolicyRegionID is the projectPolicyRegion identifier.
   * @return a ProjectPolicyRegion object.
   */
  public ProjectPolicyRegion find(long id);

  /**
   * This method gets a list of projectPolicyRegion that are active
   * 
   * @return a list from ProjectPolicyRegion null if no exist records
   */
  public List<ProjectPolicyRegion> findAll();


  public List<ProjectPolicyRegion> getPolicyRegionbyPhase(long policyID, long phaseID);

  /**
   * This method saves the information of the given projectPolicyRegion
   * 
   * @param projectPolicyRegion - is the projectPolicyRegion object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the projectPolicyRegion was
   *         updated
   *         or -1 is some error occurred.
   */
  public ProjectPolicyRegion save(ProjectPolicyRegion projectPolicyRegion);
}
