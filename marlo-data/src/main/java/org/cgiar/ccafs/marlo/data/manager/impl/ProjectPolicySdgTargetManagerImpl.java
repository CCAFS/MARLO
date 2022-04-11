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


import org.cgiar.ccafs.marlo.data.dao.ProjectPolicySdgTargetDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectPolicySdgTargetManager;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicy;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicySdgTarget;
import org.cgiar.ccafs.marlo.data.model.SdgTargets;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ProjectPolicySdgTargetManagerImpl implements ProjectPolicySdgTargetManager {


  private ProjectPolicySdgTargetDAO projectPolicySdgTargetDAO;
  // Managers


  @Inject
  public ProjectPolicySdgTargetManagerImpl(ProjectPolicySdgTargetDAO projectPolicySdgTargetDAO) {
    this.projectPolicySdgTargetDAO = projectPolicySdgTargetDAO;


  }

  @Override
  public void deleteProjectPolicySdgTarget(long projectPolicySdgTargetId) {

    projectPolicySdgTargetDAO.deleteProjectPolicySdgTarget(projectPolicySdgTargetId);
  }

  @Override
  public boolean existProjectPolicySdgTarget(long projectPolicySdgTargetID) {

    return projectPolicySdgTargetDAO.existProjectPolicySdgTarget(projectPolicySdgTargetID);
  }

  @Override
  public List<ProjectPolicySdgTarget> findAll() {

    return projectPolicySdgTargetDAO.findAll();

  }

  @Override
  public List<ProjectPolicySdgTarget> getAllPolicySdgTargetsByPolicy(Long policyId) {
    return this.projectPolicySdgTargetDAO.getAllPolicySdgTargetsByPolicy(policyId.longValue());
  }

  @Override
  public ProjectPolicySdgTarget getPolicySdgTargetByPolicySdgTargetAndPhase(ProjectPolicy policy, SdgTargets sdgTarget,
    Phase phase) {
    if (policy != null && sdgTarget != null && phase != null) {
      return this.projectPolicySdgTargetDAO.getPolicySdgTargetByPolicySdgTargetAndPhase(policy.getId().longValue(),
        sdgTarget.getId().longValue(), phase.getId().longValue());
    }
    return null;
  }

  @Override
  public ProjectPolicySdgTarget getProjectPolicySdgTargetById(long projectPolicySdgTargetID) {

    return projectPolicySdgTargetDAO.find(projectPolicySdgTargetID);
  }

  @Override
  public void replicate(ProjectPolicySdgTarget originalProjectPolicySdgTarget, Phase initialPhase) {
    Phase current = initialPhase;

    while (current != null && originalProjectPolicySdgTarget != null
      && originalProjectPolicySdgTarget.getProjectPolicy() != null
      && originalProjectPolicySdgTarget.getSdgTarget() != null && originalProjectPolicySdgTarget.getPhase() != null) {
      ProjectPolicySdgTarget policySdgTarget = this.getPolicySdgTargetByPolicySdgTargetAndPhase(
        originalProjectPolicySdgTarget.getProjectPolicy(), originalProjectPolicySdgTarget.getSdgTarget(), current);
      if (policySdgTarget == null) {
        policySdgTarget = new ProjectPolicySdgTarget();
      }

      policySdgTarget.copyFields(originalProjectPolicySdgTarget);
      policySdgTarget.setPhase(current);

      policySdgTarget = this.projectPolicySdgTargetDAO.save(policySdgTarget);

      // LOG.debug(current.toString());
      current = current.getNext();
    }
  }

  @Override
  public ProjectPolicySdgTarget saveProjectPolicySdgTarget(ProjectPolicySdgTarget projectPolicySdgTarget) {

    return projectPolicySdgTargetDAO.save(projectPolicySdgTarget);
  }
}
