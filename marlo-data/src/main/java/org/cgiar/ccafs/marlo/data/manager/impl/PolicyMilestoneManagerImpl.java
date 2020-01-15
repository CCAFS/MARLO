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
package org.cgiar.ccafs.marlo.data.manager.impl;


import org.cgiar.ccafs.marlo.data.dao.PolicyMilestoneDAO;
import org.cgiar.ccafs.marlo.data.manager.PolicyMilestoneManager;
import org.cgiar.ccafs.marlo.data.model.PolicyMilestone;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class PolicyMilestoneManagerImpl implements PolicyMilestoneManager {


  private PolicyMilestoneDAO policyMilestoneDAO;
  // Managers


  @Inject
  public PolicyMilestoneManagerImpl(PolicyMilestoneDAO policyMilestoneDAO) {
    this.policyMilestoneDAO = policyMilestoneDAO;


  }

  @Override
  public void deletePolicyMilestone(long policyMilestoneId) {

    policyMilestoneDAO.deletePolicyMilestone(policyMilestoneId);
  }

  @Override
  public boolean existPolicyMilestone(long policyMilestoneID) {

    return policyMilestoneDAO.existPolicyMilestone(policyMilestoneID);
  }

  @Override
  public List<PolicyMilestone> findAll() {

    return policyMilestoneDAO.findAll();

  }

  @Override
  public PolicyMilestone getPolicyMilestoneById(long policyMilestoneID) {

    return policyMilestoneDAO.find(policyMilestoneID);
  }

  @Override
  public PolicyMilestone savePolicyMilestone(PolicyMilestone policyMilestone) {

    return policyMilestoneDAO.save(policyMilestone);
  }


}
