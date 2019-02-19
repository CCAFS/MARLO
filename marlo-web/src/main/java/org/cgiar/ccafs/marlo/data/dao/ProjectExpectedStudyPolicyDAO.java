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

import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyPolicy;

import java.util.List;


public interface ProjectExpectedStudyPolicyDAO {

  /**
   * This method removes a specific projectExpectedStudyPolicy value from the database.
   * 
   * @param projectExpectedStudyPolicyId is the projectExpectedStudyPolicy identifier.
   * @return true if the projectExpectedStudyPolicy was successfully deleted, false otherwise.
   */
  public void deleteProjectExpectedStudyPolicy(long projectExpectedStudyPolicyId);

  /**
   * This method validate if the projectExpectedStudyPolicy identify with the given id exists in the system.
   * 
   * @param projectExpectedStudyPolicyID is a projectExpectedStudyPolicy identifier.
   * @return true if the projectExpectedStudyPolicy exists, false otherwise.
   */
  public boolean existProjectExpectedStudyPolicy(long projectExpectedStudyPolicyID);

  /**
   * This method gets a projectExpectedStudyPolicy object by a given projectExpectedStudyPolicy identifier.
   * 
   * @param projectExpectedStudyPolicyID is the projectExpectedStudyPolicy identifier.
   * @return a ProjectExpectedStudyPolicy object.
   */
  public ProjectExpectedStudyPolicy find(long id);

  /**
   * This method gets a list of projectExpectedStudyPolicy that are active
   * 
   * @return a list from ProjectExpectedStudyPolicy null if no exist records
   */
  public List<ProjectExpectedStudyPolicy> findAll();


  /**
   * This method saves the information of the given projectExpectedStudyPolicy
   * 
   * @param projectExpectedStudyPolicy - is the projectExpectedStudyPolicy object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the projectExpectedStudyPolicy was
   *         updated
   *         or -1 is some error occurred.
   */
  public ProjectExpectedStudyPolicy save(ProjectExpectedStudyPolicy projectExpectedStudyPolicy);
}
