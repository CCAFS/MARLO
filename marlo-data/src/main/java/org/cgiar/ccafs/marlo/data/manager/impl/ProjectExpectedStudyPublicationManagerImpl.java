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
import org.cgiar.ccafs.marlo.data.dao.ProjectExpectedStudyPublicationDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyPublicationManager;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyPublication;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ProjectExpectedStudyPublicationManagerImpl implements ProjectExpectedStudyPublicationManager {


  private ProjectExpectedStudyPublicationDAO projectExpectedStudyPublicationDAO;
  private PhaseDAO phaseDAO;
  // Managers


  @Inject
  public ProjectExpectedStudyPublicationManagerImpl(
    ProjectExpectedStudyPublicationDAO projectExpectedStudyPublicationDAO, PhaseDAO phaseDAO) {
    this.projectExpectedStudyPublicationDAO = projectExpectedStudyPublicationDAO;
    this.phaseDAO = phaseDAO;


  }

  @Override
  public void deleteProjectExpectedStudyPublication(long projectExpectedStudyPublicationId) {

    ProjectExpectedStudyPublication projectExpectedStudyPublication =
      this.getProjectExpectedStudyPublicationById(projectExpectedStudyPublicationId);
    Phase currentPhase = projectExpectedStudyPublication.getPhase();

    if (currentPhase.getDescription().equals(APConstants.PLANNING) && currentPhase.getNext() != null) {
      this.deleteProjectExpectedStudyPublicationPhase(currentPhase.getNext(), projectExpectedStudyPublication);
    }

    if (currentPhase.getDescription().equals(APConstants.REPORTING)) {
      if (currentPhase.getNext() != null && currentPhase.getNext().getNext() != null) {
        Phase upkeepPhase = currentPhase.getNext().getNext();
        if (upkeepPhase != null) {
          this.deleteProjectExpectedStudyPublicationPhase(upkeepPhase.getNext(), projectExpectedStudyPublication);
        }
      }
    }

    projectExpectedStudyPublicationDAO.deleteProjectExpectedStudyPublication(projectExpectedStudyPublicationId);
  }


  public void deleteProjectExpectedStudyPublicationPhase(Phase next,
    ProjectExpectedStudyPublication projectExpectedStudyPublication) {
    Phase phase = phaseDAO.find(next.getId());

    List<ProjectExpectedStudyPublication> projectExpectedStudyPublicationList =
      phase.getProjectExpectedStudyPublications().stream()
        .filter(c -> c.isActive()
          && c.getProjectExpectedStudy().getId() == projectExpectedStudyPublication.getProjectExpectedStudy().getId())
        .collect(Collectors.toList());
    for (ProjectExpectedStudyPublication projectExpectedStudyPublicationTmp : projectExpectedStudyPublicationList) {
      projectExpectedStudyPublicationDAO
        .deleteProjectExpectedStudyPublication(projectExpectedStudyPublicationTmp.getId());
    }

    if (phase.getNext() != null) {
      this.deleteProjectExpectedStudyPublicationPhase(phase.getNext(), projectExpectedStudyPublication);
    }
  }

  @Override
  public boolean existProjectExpectedStudyPublication(long projectExpectedStudyPublicationID) {

    return projectExpectedStudyPublicationDAO.existProjectExpectedStudyPublication(projectExpectedStudyPublicationID);
  }

  @Override
  public List<ProjectExpectedStudyPublication> findAll() {

    return projectExpectedStudyPublicationDAO.findAll();

  }

  @Override
  public ProjectExpectedStudyPublication
    getProjectExpectedStudyPublicationById(long projectExpectedStudyPublicationID) {

    return projectExpectedStudyPublicationDAO.find(projectExpectedStudyPublicationID);
  }

  /**
   * Reply the information to the next Phases
   * 
   * @param next - The next Phase
   * @param projectExpectedStudyPublication - The project expected study publication outcome into the
   *        database.
   */
  public void saveInfoPhase(Phase next, ProjectExpectedStudyPublication projectExpectedStudyPublication) {

    Phase phase = phaseDAO.find(next.getId());
    List<ProjectExpectedStudyPublication> projectExpectedStudyPublicationList =
      phase
        .getProjectExpectedStudyPublications().stream().filter(c -> c.getProjectExpectedStudy().getId()
          .longValue() == projectExpectedStudyPublication.getProjectExpectedStudy().getId())
        .collect(Collectors.toList());
    if (projectExpectedStudyPublicationList.isEmpty()) {

      ProjectExpectedStudyPublication projectExpectedStudyPublicationAdd = new ProjectExpectedStudyPublication();

      projectExpectedStudyPublicationAdd
        .setProjectExpectedStudy(projectExpectedStudyPublication.getProjectExpectedStudy());
      projectExpectedStudyPublicationAdd.setPhase(phase);
      projectExpectedStudyPublicationAdd.setName(projectExpectedStudyPublication.getName());
      projectExpectedStudyPublicationAdd.setPosition(projectExpectedStudyPublication.getPosition());
      projectExpectedStudyPublicationAdd.setAffiliation(projectExpectedStudyPublication.getAffiliation());


      projectExpectedStudyPublicationDAO.save(projectExpectedStudyPublicationAdd);
    }

    if (phase.getNext() != null) {
      this.saveInfoPhase(phase.getNext(), projectExpectedStudyPublication);
    }
  }

  @Override
  public ProjectExpectedStudyPublication
    saveProjectExpectedStudyPublication(ProjectExpectedStudyPublication projectExpectedStudyPublication) {

    ProjectExpectedStudyPublication sourceInfo =
      projectExpectedStudyPublicationDAO.save(projectExpectedStudyPublication);
    Phase phase = phaseDAO.find(sourceInfo.getPhase().getId());

    if (phase.getDescription().equals(APConstants.PLANNING)) {
      if (phase.getNext() != null) {
        this.saveInfoPhase(phase.getNext(), projectExpectedStudyPublication);
      }
    }

    if (phase.getDescription().equals(APConstants.REPORTING)) {
      if (phase.getNext() != null && phase.getNext().getNext() != null) {
        Phase upkeepPhase = phase.getNext().getNext();
        if (upkeepPhase != null) {
          this.saveInfoPhase(upkeepPhase, projectExpectedStudyPublication);
        }
      }
    }


    return sourceInfo;
  }


}
