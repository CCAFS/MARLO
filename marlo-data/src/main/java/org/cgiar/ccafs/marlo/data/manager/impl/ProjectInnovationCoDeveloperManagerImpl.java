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
import org.cgiar.ccafs.marlo.data.dao.ProjectInnovationCoDeveloperDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationCoDeveloperManager;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationCoDeveloper;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.transaction.annotation.Transactional;

/**
 * Co-developing institutions of a bundled innovation (A2-2613). Saves and deletes follow the innovation
 * replication rule: a planning phase replicates to every next phase, a reporting phase to its upkeep phase and on.
 */
@Named
public class ProjectInnovationCoDeveloperManagerImpl implements ProjectInnovationCoDeveloperManager {

  private ProjectInnovationCoDeveloperDAO projectInnovationCoDeveloperDAO;
  private PhaseDAO phaseDAO;

  @Inject
  public ProjectInnovationCoDeveloperManagerImpl(ProjectInnovationCoDeveloperDAO projectInnovationCoDeveloperDAO,
    PhaseDAO phaseDAO) {
    this.projectInnovationCoDeveloperDAO = projectInnovationCoDeveloperDAO;
    this.phaseDAO = phaseDAO;
  }

  @Override
  @Transactional
  public void deleteProjectInnovationCoDeveloper(long projectInnovationCoDeveloperId) {
    ProjectInnovationCoDeveloper coDeveloper = projectInnovationCoDeveloperDAO.find(projectInnovationCoDeveloperId);
    Phase nextPhase = this.replicationTarget(coDeveloper.getPhase());
    if (nextPhase != null) {
      this.deleteProjectInnovationCoDeveloperPhase(nextPhase, coDeveloper);
    }
    projectInnovationCoDeveloperDAO.deleteProjectInnovationCoDeveloper(projectInnovationCoDeveloperId);
  }

  private void deleteProjectInnovationCoDeveloperPhase(Phase next, ProjectInnovationCoDeveloper coDeveloper) {
    Phase phase = phaseDAO.find(next.getId());
    for (ProjectInnovationCoDeveloper coDeveloperDB : this.sameInstitution(phase, coDeveloper)) {
      projectInnovationCoDeveloperDAO.deleteProjectInnovationCoDeveloper(coDeveloperDB.getId());
    }
    if (phase.getNext() != null) {
      this.deleteProjectInnovationCoDeveloperPhase(phase.getNext(), coDeveloper);
    }
  }

  @Override
  public List<ProjectInnovationCoDeveloper> getByInnovationAndPhase(long innovationID, long phaseID) {
    return projectInnovationCoDeveloperDAO.getByInnovationAndPhase(innovationID, phaseID);
  }

  /**
   * The phase a change replicates to: the next phase of a planning phase, the upkeep phase of a reporting one.
   */
  private Phase replicationTarget(Phase phaseRef) {
    Phase phase = phaseDAO.find(phaseRef.getId());
    if (APConstants.PLANNING.equals(phase.getDescription())) {
      return phase.getNext();
    }
    if (APConstants.REPORTING.equals(phase.getDescription()) && phase.getNext() != null) {
      return phase.getNext().getNext();
    }
    return null;
  }

  private List<ProjectInnovationCoDeveloper> sameInstitution(Phase phase, ProjectInnovationCoDeveloper coDeveloper) {
    return projectInnovationCoDeveloperDAO
      .getByInnovationAndPhase(coDeveloper.getProjectInnovation().getId(), phase.getId()).stream()
      .filter(c -> c.getInstitution().getId().equals(coDeveloper.getInstitution().getId()))
      .collect(Collectors.toList());
  }

  @Override
  @Transactional
  public ProjectInnovationCoDeveloper
    saveProjectInnovationCoDeveloper(ProjectInnovationCoDeveloper projectInnovationCoDeveloper) {
    ProjectInnovationCoDeveloper coDeveloper = projectInnovationCoDeveloperDAO.save(projectInnovationCoDeveloper);
    Phase nextPhase = this.replicationTarget(coDeveloper.getPhase());
    if (nextPhase != null) {
      this.saveProjectInnovationCoDeveloperPhase(nextPhase, coDeveloper);
    }
    return coDeveloper;
  }

  private void saveProjectInnovationCoDeveloperPhase(Phase next, ProjectInnovationCoDeveloper coDeveloper) {
    Phase phase = phaseDAO.find(next.getId());
    if (this.sameInstitution(phase, coDeveloper).isEmpty()) {
      ProjectInnovationCoDeveloper coDeveloperAdd = new ProjectInnovationCoDeveloper();
      coDeveloperAdd.setProjectInnovation(coDeveloper.getProjectInnovation());
      coDeveloperAdd.setInstitution(coDeveloper.getInstitution());
      coDeveloperAdd.setPhase(phase);
      projectInnovationCoDeveloperDAO.save(coDeveloperAdd);
    }
    if (phase.getNext() != null) {
      this.saveProjectInnovationCoDeveloperPhase(phase.getNext(), coDeveloper);
    }
  }
}
