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
import org.cgiar.ccafs.marlo.data.dao.ProjectExpectedStudyFlagshipDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyFlagshipManager;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyFlagship;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ProjectExpectedStudyFlagshipManagerImpl implements ProjectExpectedStudyFlagshipManager {


  private ProjectExpectedStudyFlagshipDAO projectExpectedStudyFlagshipDAO;
  // Managers
  private PhaseDAO phaseDAO;

  @Inject
  public ProjectExpectedStudyFlagshipManagerImpl(ProjectExpectedStudyFlagshipDAO projectExpectedStudyFlagshipDAO,
    PhaseDAO phaseDAO) {
    this.projectExpectedStudyFlagshipDAO = projectExpectedStudyFlagshipDAO;
    this.phaseDAO = phaseDAO;


  }

  @Override
  public void deleteProjectExpectedStudyFlagship(long projectExpectedStudyFlagshipId) {

    ProjectExpectedStudyFlagship projectExpectedStudyFlagship =
      this.getProjectExpectedStudyFlagshipById(projectExpectedStudyFlagshipId);
    Phase currentPhase = projectExpectedStudyFlagship.getPhase();

    if (currentPhase.getNext() != null) {
      this.deleteProjectExpectedStudyFlagshipPhase(currentPhase.getNext(),
        projectExpectedStudyFlagship.getProjectExpectedStudy().getId(), projectExpectedStudyFlagship);
    }

    // Uncomment this line to allow reporting replication to upkeep
    // if (currentPhase.getDescription().equals(APConstants.REPORTING)) {
    // if (currentPhase.getNext() != null && currentPhase.getNext().getNext() != null) {
    // Phase upkeepPhase = currentPhase.getNext().getNext();
    // if (upkeepPhase != null) {
    // this.deleteProjectExpectedStudyFlagshipPhase(upkeepPhase,
    // projectExpectedStudyFlagship.getProjectExpectedStudy().getId(), projectExpectedStudyFlagship);
    // }
    // }
    // }

    projectExpectedStudyFlagshipDAO.deleteProjectExpectedStudyFlagship(projectExpectedStudyFlagshipId);
  }

  public void deleteProjectExpectedStudyFlagshipPhase(Phase next, long expectedID,
    ProjectExpectedStudyFlagship projectExpectedStudyFlagship) {
    Phase phase = phaseDAO.find(next.getId());

    List<ProjectExpectedStudyFlagship> projectExpectedStudyFlagships = phase.getProjectExpectedStudyFlagships().stream()
      .filter(c -> c.isActive() && c.getProjectExpectedStudy().getId().longValue() == expectedID
        && c.getCrpProgram().getId().equals(projectExpectedStudyFlagship.getCrpProgram().getId()))
      .collect(Collectors.toList());
    for (ProjectExpectedStudyFlagship projectExpectedStudyFlagshipDB : projectExpectedStudyFlagships) {
      projectExpectedStudyFlagshipDAO.deleteProjectExpectedStudyFlagship(projectExpectedStudyFlagshipDB.getId());
    }

    if (phase.getNext() != null) {
      this.deleteProjectExpectedStudyFlagshipPhase(phase.getNext(), expectedID, projectExpectedStudyFlagship);
    }
  }

  @Override
  public boolean existProjectExpectedStudyFlagship(long projectExpectedStudyFlagshipID) {

    return projectExpectedStudyFlagshipDAO.existProjectExpectedStudyFlagship(projectExpectedStudyFlagshipID);
  }

  @Override
  public List<ProjectExpectedStudyFlagship> findAll() {

    return projectExpectedStudyFlagshipDAO.findAll();

  }

  @Override
  public ProjectExpectedStudyFlagship getProjectExpectedStudyFlagshipById(long projectExpectedStudyFlagshipID) {

    return projectExpectedStudyFlagshipDAO.find(projectExpectedStudyFlagshipID);
  }

  public void saveExpectedStudyFlagshipPhase(Phase next, long expectedID,
    ProjectExpectedStudyFlagship projectExpectedStudyFlagship) {
    Phase phase = phaseDAO.find(next.getId());

    List<ProjectExpectedStudyFlagship> projectExpectedStudyFlagships = phase.getProjectExpectedStudyFlagships().stream()
      .filter(c -> c.getProjectExpectedStudy().getId().longValue() == expectedID
        && c.getCrpProgram().getId().equals(projectExpectedStudyFlagship.getCrpProgram().getId()))
      .collect(Collectors.toList());

    if (projectExpectedStudyFlagships.isEmpty()) {
      ProjectExpectedStudyFlagship projectExpectedStudyFlagshipAdd = new ProjectExpectedStudyFlagship();
      projectExpectedStudyFlagshipAdd.setProjectExpectedStudy(projectExpectedStudyFlagship.getProjectExpectedStudy());
      projectExpectedStudyFlagshipAdd.setPhase(phase);
      projectExpectedStudyFlagshipAdd.setCrpProgram(projectExpectedStudyFlagship.getCrpProgram());
      projectExpectedStudyFlagshipDAO.save(projectExpectedStudyFlagshipAdd);
    }


    if (phase.getNext() != null) {
      this.saveExpectedStudyFlagshipPhase(phase.getNext(), expectedID, projectExpectedStudyFlagship);
    }
  }

  @Override
  public ProjectExpectedStudyFlagship
    saveProjectExpectedStudyFlagship(ProjectExpectedStudyFlagship projectExpectedStudyFlagship) {

    ProjectExpectedStudyFlagship flagship = projectExpectedStudyFlagshipDAO.save(projectExpectedStudyFlagship);
    Phase currentPhase = flagship.getPhase();


    if (currentPhase.getNext() != null) {
      this.saveExpectedStudyFlagshipPhase(currentPhase.getNext(), flagship.getProjectExpectedStudy().getId(),
        projectExpectedStudyFlagship);
    }

    // Uncomment this line to allow reporting replication to upkeep
    // if (currentPhase.getDescription().equals(APConstants.REPORTING)) {
    // if (currentPhase.getNext() != null && currentPhase.getNext().getNext() != null) {
    // Phase upkeepPhase = currentPhase.getNext().getNext();
    // if (upkeepPhase != null) {
    // this.saveExpectedStudyFlagshipPhase(upkeepPhase, flagship.getProjectExpectedStudy().getId(),
    // projectExpectedStudyFlagship);
    // }
    // }
    // }

    return flagship;
  }


}
