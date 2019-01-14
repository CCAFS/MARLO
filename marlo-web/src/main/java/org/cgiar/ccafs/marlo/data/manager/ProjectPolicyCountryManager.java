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

import org.cgiar.ccafs.marlo.data.model.ProjectPolicyCountry;

import java.util.List;


/**
 * @author CCAFS
 */

public interface ProjectPolicyCountryManager {


  /**
   * This method removes a specific projectPolicyCountry value from the database.
   * 
   * @param projectPolicyCountryId is the projectPolicyCountry identifier.
   * @return true if the projectPolicyCountry was successfully deleted, false otherwise.
   */
  public void deleteProjectPolicyCountry(long projectPolicyCountryId);


  /**
   * This method validate if the projectPolicyCountry identify with the given id exists in the system.
   * 
   * @param projectPolicyCountryID is a projectPolicyCountry identifier.
   * @return true if the projectPolicyCountry exists, false otherwise.
   */
  public boolean existProjectPolicyCountry(long projectPolicyCountryID);


  /**
   * This method gets a list of projectPolicyCountry that are active
   * 
   * @return a list from ProjectPolicyCountry null if no exist records
   */
  public List<ProjectPolicyCountry> findAll();

  /**
   * This method gets a projectPolicyCountry object list by a given policy and phase identifier.
   * 
   * @param policyID is the policy identifier.
   * @param phaseID is the phase identifier.
   * @return a projectPolicyCountry object list.
   */
  public List<ProjectPolicyCountry> getPolicyCountrybyPhase(long policyID, long phaseID);


  /**
   * This method gets a projectPolicyCountry object by a given projectPolicyCountry identifier.
   * 
   * @param projectPolicyCountryID is the projectPolicyCountry identifier.
   * @return a ProjectPolicyCountry object.
   */
  public ProjectPolicyCountry getProjectPolicyCountryById(long projectPolicyCountryID);

  /**
   * This method saves the information of the given projectPolicyCountry
   * 
   * @param projectPolicyCountry - is the projectPolicyCountry object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the projectPolicyCountry was
   *         updated
   *         or -1 is some error occurred.
   */
  public ProjectPolicyCountry saveProjectPolicyCountry(ProjectPolicyCountry projectPolicyCountry);


}
