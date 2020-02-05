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

import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.dao.PhaseDAO;
import org.cgiar.ccafs.marlo.data.dao.PolicyMilestoneDAO;
import org.cgiar.ccafs.marlo.data.manager.PolicyMilestoneManager;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.PolicyMilestone;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class PolicyMilestoneManagerImpl implements PolicyMilestoneManager {

  private PolicyMilestoneDAO policyMilestoneDAO;
  // Managers
  private PhaseDAO phaseDAO;

  @Inject
  public PolicyMilestoneManagerImpl(PolicyMilestoneDAO policyMilestoneDAO, PhaseDAO phaseDAO) {
    this.policyMilestoneDAO = policyMilestoneDAO;
    this.phaseDAO = phaseDAO;

  }

  @Override
  public void deletePolicyMilestone(long policyMilestoneId) {

    PolicyMilestone policyMilestone = this.getPolicyMilestoneById(policyMilestoneId);

    // Conditions to Project Policy Works In AR phase and Upkeep Phase
    if (policyMilestone.getPhase().getDescription().equals(APConstants.PLANNING)
      && policyMilestone.getPhase().getNext() != null) {
      this.deletePolicyMilestonePhase(policyMilestone.getPhase().getNext(), policyMilestone.getPolicy().getId(),
        policyMilestone);
    }

    if (policyMilestone.getPhase().getDescription().equals(APConstants.REPORTING)) {
      if (policyMilestone.getPhase().getNext() != null && policyMilestone.getPhase().getNext().getNext() != null) {
        Phase upkeepPhase = policyMilestone.getPhase().getNext().getNext();
        if (upkeepPhase != null) {
          this.deletePolicyMilestonePhase(upkeepPhase, policyMilestone.getPolicy().getId(), policyMilestone);
        }
      }
    }
    policyMilestoneDAO.deletePolicyMilestone(policyMilestoneId);
  }

  public void deletePolicyMilestonePhase(Phase next, long policyID, PolicyMilestone policyMilestone) {
    Phase phase = phaseDAO.find(next.getId());

    List<PolicyMilestone> policyMilestones = phase.getPolicyMilestones().stream()
      .filter(c -> c.getPhase().getId().longValue() == phase.getId().longValue()
        && c.getPolicy().getId().longValue() == policyID
        && c.getCrpMilestone().getId().equals(policyMilestone.getCrpMilestone().getId()))
      .collect(Collectors.toList());

    for (PolicyMilestone policyMilestoneDB : policyMilestones) {

      policyMilestoneDAO.deletePolicyMilestone(policyMilestoneDB.getId());
    }

    if (phase.getNext() != null) {
      this.deletePolicyMilestonePhase(phase.getNext(), policyID, policyMilestone);
    }
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
    PolicyMilestone pMilestone = policyMilestoneDAO.save(policyMilestone);

    Phase phase = phaseDAO.find(pMilestone.getPhase().getId());

    // Conditions to Project Policy Works In AR phase and Upkeep Phase
    if (phase.getDescription().equals(APConstants.PLANNING) && phase.getNext() != null) {
      this.savePolicyMilestonePhase(pMilestone.getPhase().getNext(), pMilestone.getPolicy().getId(), policyMilestone);
    }
    if (phase.getDescription().equals(APConstants.REPORTING)) {
      if (phase.getNext() != null && phase.getNext().getNext() != null) {
        Phase upkeepPhase = phase.getNext().getNext();
        if (upkeepPhase != null) {
          this.savePolicyMilestonePhase(upkeepPhase, pMilestone.getPolicy().getId(), policyMilestone);
        }
      }
    }
    return pMilestone;
  }

  public void savePolicyMilestonePhase(Phase next, long policyID, PolicyMilestone policyMilestone) {

    Phase phase = phaseDAO.find(next.getId());

    List<PolicyMilestone> policyMilestones = phase.getPolicyMilestones().stream()
      .filter(c -> c.getPolicy().getId().longValue() == policyID
        && c.getPhase().getId().longValue() == phase.getId().longValue()
        && c.getCrpMilestone().getId().equals(policyMilestone.getCrpMilestone().getId()))
      .collect(Collectors.toList());

    if (policyMilestones.isEmpty()) {
      PolicyMilestone policyMilestoneAdd = new PolicyMilestone();
      policyMilestoneAdd.setPolicy(policyMilestone.getPolicy());
      policyMilestoneAdd.setPhase(phase);
      policyMilestoneAdd.setCrpMilestone(policyMilestone.getCrpMilestone());
      if (policyMilestone.getPrimary() != null) {
        policyMilestone.setPrimary(false);
      }
      policyMilestoneAdd.setPrimary(policyMilestone.getPrimary());
      policyMilestoneDAO.save(policyMilestoneAdd);
    }

    if (phase.getNext() != null) {
      this.savePolicyMilestonePhase(phase.getNext(), policyID, policyMilestone);
    }
  }

}
