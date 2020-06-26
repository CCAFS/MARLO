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

import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicy;

import java.util.List;


/**
 * @author CCAFS
 */

public interface ProjectPolicyManager {


  /**
   * This method removes a specific projectPolicy value from the database.
   * 
   * @param projectPolicyId is the projectPolicy identifier.
   * @return true if the projectPolicy was successfully deleted, false otherwise.
   */
  public void deleteProjectPolicy(long projectPolicyId);


  /**
   * This method validate if the projectPolicy identify with the given id exists in the system.
   * 
   * @param projectPolicyID is a projectPolicy identifier.
   * @return true if the projectPolicy exists, false otherwise.
   */
  public boolean existProjectPolicy(long projectPolicyID);


  /**
   * This method gets a list of projectPolicy that are active
   * 
   * @return a list from ProjectPolicy null if no exist records
   */
  public List<ProjectPolicy> findAll();


  /**
   * This method gets a list of projectPolicies that are active for an specific liaisonInstitution
   * Flagship: Get the list of projects that have project_focus equal to the liaisonInstitution
   * PMU: Get the list of liaison according to what was selected by the flagships
   * 
   * @param liaisonInstitution
   * @param phase
   * @return a list from ProjectPolicy null if no exist records
   */
  public List<ProjectPolicy> getProjectPoliciesList(LiaisonInstitution liaisonInstitution, Phase phase);

  /**
   * This method gets a list of projectPolicies that are active for an specific liaisonInstitution thats not belong in
   * AR sytnhesis of the current phase
   * Flagship: Get the list of projects that have project_focus equal to the liaisonInstitution
   * PMU: Get the list of liaison according to what was selected by the flagships
   * 
   * @param liaisonInstitution
   * @param phase
   * @return a list from ProjectPolicy null if no exist records
   */
  public List<ProjectPolicy> getProjectPoliciesNoSynthesisList(LiaisonInstitution liaisonInstitution, Phase phase);

  /**
   * This method gets a projectPolicy object by a given projectPolicy identifier.
   * 
   * @param projectPolicyID is the projectPolicy identifier.
   * @return a ProjectPolicy object.
   */
  public ProjectPolicy getProjectPolicyById(long projectPolicyID);

  /**
   * This method gets a list of ProjectPolicy that are active by a given phase
   * 
   * @return a list from ProjectPolicy null if no exist records
   */
  public List<ProjectPolicy> getProjectPolicyByPhase(Phase phase);

  public Boolean isPolicyExcluded(Long policyId, Long phaseId);

  /**
   * This method saves the information of the given projectPolicy
   * 
   * @param projectPolicy - is the projectPolicy object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the projectPolicy was
   *         updated
   *         or -1 is some error occurred.
   */
  public ProjectPolicy saveProjectPolicy(ProjectPolicy projectPolicy);

  public ProjectPolicy saveProjectPolicy(ProjectPolicy projectPolicy, String section, List<String> relationsName,
    Phase phase);
}
