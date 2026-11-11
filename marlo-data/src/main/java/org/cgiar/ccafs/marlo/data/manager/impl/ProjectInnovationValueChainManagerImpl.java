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
import org.cgiar.ccafs.marlo.data.dao.ProjectInnovationValueChainDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationValueChainManager;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationValueChain;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.transaction.annotation.Transactional;

/**
 * Value chains of an innovation (A2-2613). Saves and deletes follow the innovation replication rule: a planning
 * phase replicates to every next phase, a reporting phase to its upkeep phase and on.
 */
@Named
public class ProjectInnovationValueChainManagerImpl implements ProjectInnovationValueChainManager {

  private ProjectInnovationValueChainDAO projectInnovationValueChainDAO;
  private PhaseDAO phaseDAO;

  @Inject
  public ProjectInnovationValueChainManagerImpl(ProjectInnovationValueChainDAO projectInnovationValueChainDAO,
    PhaseDAO phaseDAO) {
    this.projectInnovationValueChainDAO = projectInnovationValueChainDAO;
    this.phaseDAO = phaseDAO;
  }

  @Override
  @Transactional
  public void deleteProjectInnovationValueChain(long projectInnovationValueChainId) {
    ProjectInnovationValueChain valueChain = projectInnovationValueChainDAO.find(projectInnovationValueChainId);
    Phase nextPhase = this.replicationTarget(valueChain.getPhase());
    if (nextPhase != null) {
      this.deleteProjectInnovationValueChainPhase(nextPhase, valueChain);
    }
    projectInnovationValueChainDAO.deleteProjectInnovationValueChain(projectInnovationValueChainId);
  }

  private void deleteProjectInnovationValueChainPhase(Phase next, ProjectInnovationValueChain valueChain) {
    Phase phase = phaseDAO.find(next.getId());
    for (ProjectInnovationValueChain valueChainDB : this.sameValueChain(phase, valueChain)) {
      projectInnovationValueChainDAO.deleteProjectInnovationValueChain(valueChainDB.getId());
    }
    if (phase.getNext() != null) {
      this.deleteProjectInnovationValueChainPhase(phase.getNext(), valueChain);
    }
  }

  @Override
  public List<ProjectInnovationValueChain> getByInnovationAndPhase(long innovationID, long phaseID) {
    return projectInnovationValueChainDAO.getByInnovationAndPhase(innovationID, phaseID);
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

  private List<ProjectInnovationValueChain> sameValueChain(Phase phase, ProjectInnovationValueChain valueChain) {
    return projectInnovationValueChainDAO
      .getByInnovationAndPhase(valueChain.getProjectInnovation().getId(), phase.getId()).stream()
      .filter(c -> c.getValueChain().getId().equals(valueChain.getValueChain().getId()))
      .collect(Collectors.toList());
  }

  @Override
  @Transactional
  public ProjectInnovationValueChain
    saveProjectInnovationValueChain(ProjectInnovationValueChain projectInnovationValueChain) {
    ProjectInnovationValueChain valueChain = projectInnovationValueChainDAO.save(projectInnovationValueChain);
    Phase nextPhase = this.replicationTarget(valueChain.getPhase());
    if (nextPhase != null) {
      this.saveProjectInnovationValueChainPhase(nextPhase, valueChain);
    }
    return valueChain;
  }

  private void saveProjectInnovationValueChainPhase(Phase next, ProjectInnovationValueChain valueChain) {
    Phase phase = phaseDAO.find(next.getId());
    if (this.sameValueChain(phase, valueChain).isEmpty()) {
      ProjectInnovationValueChain valueChainAdd = new ProjectInnovationValueChain();
      valueChainAdd.setProjectInnovation(valueChain.getProjectInnovation());
      valueChainAdd.setValueChain(valueChain.getValueChain());
      valueChainAdd.setPhase(phase);
      projectInnovationValueChainDAO.save(valueChainAdd);
    }
    if (phase.getNext() != null) {
      this.saveProjectInnovationValueChainPhase(phase.getNext(), valueChain);
    }
  }
}
