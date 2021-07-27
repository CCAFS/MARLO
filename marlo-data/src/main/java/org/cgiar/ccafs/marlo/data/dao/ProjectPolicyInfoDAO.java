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

import org.cgiar.ccafs.marlo.data.model.ProjectPolicyInfo;

import java.util.List;


public interface ProjectPolicyInfoDAO {

  /**
   * This method removes a specific projectPolicyInfo value from the database.
   * 
   * @param projectPolicyInfoId is the projectPolicyInfo identifier.
   * @return true if the projectPolicyInfo was successfully deleted, false otherwise.
   */
  public void deleteProjectPolicyInfo(long projectPolicyInfoId);

  /**
   * This method validate if the projectPolicyInfo identify with the given id exists in the system.
   * 
   * @param projectPolicyInfoID is a projectPolicyInfo identifier.
   * @return true if the projectPolicyInfo exists, false otherwise.
   */
  public boolean existProjectPolicyInfo(long projectPolicyInfoID);

  /**
   * This method gets a projectPolicyInfo object by a given projectPolicyInfo identifier.
   * 
   * @param projectPolicyInfoID is the projectPolicyInfo identifier.
   * @return a ProjectPolicyInfo object.
   */
  public ProjectPolicyInfo find(long id);

  /**
   * This method gets a list of projectPolicyInfo that are active
   * 
   * @return a list from ProjectPolicyInfo null if no exist records
   */
  public List<ProjectPolicyInfo> findAll();


  /**
   * This method gets a list of projectPolicyInfo by a given projectPolicy identifier.
   * 
   * @param policyId is the projectPolicy identifier.
   * @return a list of projectPolicyInfo objects.
   */
  public List<ProjectPolicyInfo> getAllPolicyInfosByPolicy(long policyId);

  /**
   * This method saves the information of the given projectPolicyInfo
   * 
   * @param projectPolicyInfo - is the projectPolicyInfo object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the projectPolicyInfo was
   *         updated
   *         or -1 is some error occurred.
   */
  public ProjectPolicyInfo save(ProjectPolicyInfo projectPolicyInfo);
}
