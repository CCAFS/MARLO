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


import org.cgiar.ccafs.marlo.data.dao.PhaseDAO;
import org.cgiar.ccafs.marlo.data.dao.ProjectExpectedStudyPolicyDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyPolicyManager;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyPolicy;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ProjectExpectedStudyPolicyManagerImpl implements ProjectExpectedStudyPolicyManager {


  private ProjectExpectedStudyPolicyDAO projectExpectedStudyPolicyDAO;
  // Managers
  private PhaseDAO phaseDAO;


  @Inject
  public ProjectExpectedStudyPolicyManagerImpl(ProjectExpectedStudyPolicyDAO projectExpectedStudyPolicyDAO,
    PhaseDAO phaseDAO) {
    this.projectExpectedStudyPolicyDAO = projectExpectedStudyPolicyDAO;
    this.phaseDAO = phaseDAO;


  }

  @Override
  public void deleteProjectExpectedStudyPolicy(long projectExpectedStudyPolicyId) {

    ProjectExpectedStudyPolicy projectExpectedStudyPolicy =
      this.getProjectExpectedStudyPolicyById(projectExpectedStudyPolicyId);
    Phase currentPhase = projectExpectedStudyPolicy.getPhase();


    if (currentPhase.getNext() != null) {
      this.deleteProjectExpectedStudyPolicyPhase(currentPhase.getNext(),
        projectExpectedStudyPolicy.getProjectExpectedStudy().getId(), projectExpectedStudyPolicy);
    }


    projectExpectedStudyPolicyDAO.deleteProjectExpectedStudyPolicy(projectExpectedStudyPolicyId);
  }

  public void deleteProjectExpectedStudyPolicyPhase(Phase next, long expectedID,
    ProjectExpectedStudyPolicy projectExpectedStudyPolicy) {
    Phase phase = phaseDAO.find(next.getId());

    List<ProjectExpectedStudyPolicy> projectExpectedStudyPolicies = phase.getProjectExpectedStudyPolicies().stream()
      .filter(c -> c.isActive() && c.getProjectExpectedStudy().getId().longValue() == expectedID
        && c.getProjectPolicy().getId().equals(projectExpectedStudyPolicy.getProjectPolicy().getId()))
      .collect(Collectors.toList());
    for (ProjectExpectedStudyPolicy projectExpectedStudyPolicyDB : projectExpectedStudyPolicies) {
      projectExpectedStudyPolicyDAO.deleteProjectExpectedStudyPolicy(projectExpectedStudyPolicyDB.getId());
    }

    if (phase.getNext() != null) {
      this.deleteProjectExpectedStudyPolicyPhase(phase.getNext(), expectedID, projectExpectedStudyPolicy);
    }
  }

  @Override
  public boolean existProjectExpectedStudyPolicy(long projectExpectedStudyPolicyID) {

    return projectExpectedStudyPolicyDAO.existProjectExpectedStudyPolicy(projectExpectedStudyPolicyID);
  }

  @Override
  public List<ProjectExpectedStudyPolicy> findAll() {

    return projectExpectedStudyPolicyDAO.findAll();

  }

  @Override
  public ProjectExpectedStudyPolicy getProjectExpectedStudyPolicyById(long projectExpectedStudyPolicyID) {

    return projectExpectedStudyPolicyDAO.find(projectExpectedStudyPolicyID);
  }

  public void saveExpectedStudyPolicyPhase(Phase next, long expectedID,
    ProjectExpectedStudyPolicy projectExpectedStudyPolicy) {
    Phase phase = phaseDAO.find(next.getId());

    List<ProjectExpectedStudyPolicy> projectExpectedStudyPolicies = phase.getProjectExpectedStudyPolicies().stream()
      .filter(c -> c.isActive() && c.getProjectExpectedStudy().getId().longValue() == expectedID
        && c.getProjectPolicy().getId().equals(projectExpectedStudyPolicy.getProjectPolicy().getId()))
      .collect(Collectors.toList());

    if (projectExpectedStudyPolicies.isEmpty()) {
      ProjectExpectedStudyPolicy projectExpectedStudyPolicyAdd = new ProjectExpectedStudyPolicy();
      projectExpectedStudyPolicyAdd.setProjectExpectedStudy(projectExpectedStudyPolicy.getProjectExpectedStudy());
      projectExpectedStudyPolicyAdd.setPhase(phase);
      projectExpectedStudyPolicyAdd.setProjectPolicy(projectExpectedStudyPolicy.getProjectPolicy());
      projectExpectedStudyPolicyDAO.save(projectExpectedStudyPolicyAdd);
    }


    if (phase.getNext() != null) {
      this.saveExpectedStudyPolicyPhase(phase.getNext(), expectedID, projectExpectedStudyPolicy);
    }
  }

  @Override
  public ProjectExpectedStudyPolicy
    saveProjectExpectedStudyPolicy(ProjectExpectedStudyPolicy projectExpectedStudyPolicy) {

    ProjectExpectedStudyPolicy projectExpectedStudyPolicyResult =
      projectExpectedStudyPolicyDAO.save(projectExpectedStudyPolicy);
    Phase currentPhase = projectExpectedStudyPolicyResult.getPhase();


    if (currentPhase.getNext() != null) {
      this.saveExpectedStudyPolicyPhase(currentPhase.getNext(),
        projectExpectedStudyPolicy.getProjectExpectedStudy().getId(), projectExpectedStudyPolicy);
    }


    return projectExpectedStudyPolicyResult;
  }


}
