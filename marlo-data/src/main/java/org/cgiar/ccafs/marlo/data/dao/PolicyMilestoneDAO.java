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

import org.cgiar.ccafs.marlo.data.model.PolicyMilestone;

import java.util.List;


public interface PolicyMilestoneDAO {

  /**
   * This method removes a specific policyMilestone value from the database.
   * 
   * @param policyMilestoneId is the policyMilestone identifier.
   * @return true if the policyMilestone was successfully deleted, false otherwise.
   */
  public void deletePolicyMilestone(long policyMilestoneId);

  /**
   * This method validate if the policyMilestone identify with the given id exists in the system.
   * 
   * @param policyMilestoneID is a policyMilestone identifier.
   * @return true if the policyMilestone exists, false otherwise.
   */
  public boolean existPolicyMilestone(long policyMilestoneID);

  /**
   * This method gets a policyMilestone object by a given policyMilestone identifier.
   * 
   * @param policyMilestoneID is the policyMilestone identifier.
   * @return a PolicyMilestone object.
   */
  public PolicyMilestone find(long id);

  /**
   * This method gets a list of policyMilestone that are active
   * 
   * @return a list from PolicyMilestone null if no exist records
   */
  public List<PolicyMilestone> findAll();

  /**
   * This method gets a policyMilestone object by its foreign keys (Phase, CrpMilestone and Policy).
   * 
   * @param crpMilestoneId is the crpMilestone identifier.
   * @param policyId is the policy identifier
   * @param phaseId is the phase identifier
   * @return a PolicyMilestone object.
   */
  public PolicyMilestone findByCrpMilestonePolicyAndPhase(long crpMilestoneId, long policyId, long phaseId);


  /**
   * This method saves the information of the given policyMilestone
   * 
   * @param policyMilestone - is the policyMilestone object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the policyMilestone was
   *         updated
   *         or -1 is some error occurred.
   */
  public PolicyMilestone save(PolicyMilestone policyMilestone);
}
