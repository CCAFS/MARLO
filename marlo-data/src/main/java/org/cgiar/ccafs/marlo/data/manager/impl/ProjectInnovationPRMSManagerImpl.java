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
import org.cgiar.ccafs.marlo.data.dao.ProjectInnovationPRMSDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationPRMSManager;
import org.cgiar.ccafs.marlo.data.model.PRMSInnovation;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationPRMS;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ProjectInnovationPRMSManagerImpl implements ProjectInnovationPRMSManager {

  private final ProjectInnovationPRMSDAO projectInnovationPRMSDAO;
  private final PhaseDAO phaseDAO;

  @Inject
  public ProjectInnovationPRMSManagerImpl(ProjectInnovationPRMSDAO projectInnovationPRMSDAO, PhaseDAO phaseDAO) {
    this.projectInnovationPRMSDAO = projectInnovationPRMSDAO;
    this.phaseDAO = phaseDAO;
  }

  @Override
  public void deleteProjectInnovationPRMS(long projectInnovationPRMSId) {
    ProjectInnovationPRMS link = this.getProjectInnovationPRMSById(projectInnovationPRMSId);
    if (link == null || link.getPhase() == null) {
      // Nothing to replicate if the record/phase is missing
      projectInnovationPRMSDAO.deleteProjectInnovationPRMS(projectInnovationPRMSId);
      return;
    }

    // Replicate deletions according to phase rules
    if (APConstants.PLANNING.equals(link.getPhase().getDescription()) && link.getPhase().getNext() != null) {
      this.deleteProjectInnovationPRMSPhase(link.getPhase().getNext(), link.getProjectInnovation().getId(), link);
    }

    if (APConstants.REPORTING.equals(link.getPhase().getDescription()) && link.getPhase().getNext() != null
      && link.getPhase().getNext().getNext() != null) {
      Phase upkeepPhase = link.getPhase().getNext().getNext();
      this.deleteProjectInnovationPRMSPhase(upkeepPhase, link.getProjectInnovation().getId(), link);
    }

    projectInnovationPRMSDAO.deleteProjectInnovationPRMS(projectInnovationPRMSId);
  }

  /**
   * Recursively delete PRMS links in subsequent phases that match the same PRMSInnovation.
   *
   * @param next target phase (starting point)
   * @param innovationID owner innovation id
   * @param link original link to compare against (by PRMSInnovation)
   */
  public void deleteProjectInnovationPRMSPhase(Phase next, long innovationID, ProjectInnovationPRMS link) {
    Phase phase = phaseDAO.find(next.getId());

    // Load all PRMS links for this innovation and phase
    List<ProjectInnovationPRMS> prmsLinks =
      Optional.ofNullable(projectInnovationPRMSDAO.findByInnovationIDAndPhaseID(innovationID, phase.getId()))
        .orElse(Collections.emptyList()).stream().filter(c -> {
          // Compare by PRMSInnovation id to identify the same relationship across phases
          PRMSInnovation selA = c != null ? c.getPRMSInnovation() : null;
          PRMSInnovation selB = link != null ? link.getPRMSInnovation() : null;
          return selA != null && selA.getId() != null && selB != null && selB.getId() != null
            && selA.getId().equals(selB.getId());
        }).collect(Collectors.toList());

    // Delete all matches in this phase
    for (ProjectInnovationPRMS dbLink : prmsLinks) {
      if (dbLink != null && dbLink.getId() != null) {
        projectInnovationPRMSDAO.deleteProjectInnovationPRMS(dbLink.getId());
      }
    }

    // Cascade to next phase if available
    if (phase.getNext() != null) {
      this.deleteProjectInnovationPRMSPhase(phase.getNext(), innovationID, link);
    }
  }

  @Override
  public boolean existProjectInnovationPRMS(long projectInnovationPRMSID) {
    return projectInnovationPRMSDAO.existProjectInnovationPRMS(projectInnovationPRMSID);
  }

  @Override
  public List<ProjectInnovationPRMS> findAll() {
    return projectInnovationPRMSDAO.findAll();
  }

  @Override
  public List<ProjectInnovationPRMS> findByInnovationIDAndPhaseID(long projectInnovationID, long phaseID) {
    return projectInnovationPRMSDAO.findByInnovationIDAndPhaseID(projectInnovationID, phaseID);
  }

  @Override
  public ProjectInnovationPRMS getProjectInnovationPRMSById(long projectInnovationPRMSID) {
    return projectInnovationPRMSDAO.find(projectInnovationPRMSID);
  }

  @Override
  public ProjectInnovationPRMS saveProjectInnovationPRMS(ProjectInnovationPRMS link) {
    // Persist original record
    ProjectInnovationPRMS saved = projectInnovationPRMSDAO.save(link);
    Phase phase = phaseDAO.find(saved.getPhase().getId());

    // Replicate according to phase semantics
    if (APConstants.PLANNING.equals(phase.getDescription()) && phase.getNext() != null) {
      this.saveProjectInnovationPRMSPhase(saved.getPhase().getNext(), saved.getProjectInnovation().getId(), saved);
    }

    if (APConstants.REPORTING.equals(phase.getDescription())) {
      if (phase.getNext() != null && phase.getNext().getNext() != null) {
        Phase upkeepPhase = phase.getNext().getNext();
        this.saveProjectInnovationPRMSPhase(upkeepPhase, saved.getProjectInnovation().getId(), saved);
      }
    }

    return saved;
  }

  /**
   * Recursively ensure the PRMS link exists in subsequent phases; create if missing (idempotent).
   *
   * @param next target phase (starting point)
   * @param innovationID owner innovation id
   * @param source original link to replicate (provides PRMSInnovation)
   */
  private void saveProjectInnovationPRMSPhase(Phase next, Long innovationID, ProjectInnovationPRMS source) {
    Phase phase = phaseDAO.find(next.getId());

    // Load current links in this phase and check if one already targets the same PRMSInnovation
    List<ProjectInnovationPRMS> prmsLinks =
      Optional.ofNullable(projectInnovationPRMSDAO.findByInnovationIDAndPhaseID(innovationID, phase.getId()))
        .orElse(Collections.emptyList()).stream().filter(c -> {
          PRMSInnovation selA = c != null ? c.getPRMSInnovation() : null;
          PRMSInnovation selB = source != null ? source.getPRMSInnovation() : null;
          return selA != null && selA.getId() != null && selB != null && selB.getId() != null
            && selA.getId().equals(selB.getId());
        }).collect(Collectors.toList());

    // If missing, create the replicated link for this phase
    if (prmsLinks.isEmpty()) {
      ProjectInnovationPRMS copy = new ProjectInnovationPRMS();
      copy.setProjectInnovation(source.getProjectInnovation());
      copy.setPhase(phase);
      copy.setPRMSInnovation(source.getPRMSInnovation());
      projectInnovationPRMSDAO.save(copy);
    }

    // Cascade to next phase if available
    if (phase.getNext() != null) {
      this.saveProjectInnovationPRMSPhase(phase.getNext(), innovationID, source);
    }
  }
}
