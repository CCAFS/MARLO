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
import org.cgiar.ccafs.marlo.data.dao.ProjectInnovationActorDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationActorManager;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationActor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ProjectInnovationActorManagerImpl implements ProjectInnovationActorManager {

  private ProjectInnovationActorDAO projectInnovationActorDAO;
  private PhaseDAO phaseDAO;
  // Managers

  @Inject
  public ProjectInnovationActorManagerImpl(ProjectInnovationActorDAO projectInnovationActorDAO, PhaseDAO phaseDAO) {
    this.projectInnovationActorDAO = projectInnovationActorDAO;
    this.phaseDAO = phaseDAO;
  }

  @Override
  public void deleteProjectInnovationActor(long projectInnovationActorsId) {

    ProjectInnovationActor projectInnovationActor = this.getProjectInnovationActorById(projectInnovationActorsId);

    // Conditions to Project Innovation Works In AR phase and Upkeep Phase
    if (projectInnovationActor.getPhase().getDescription().equals(APConstants.PLANNING)
      && projectInnovationActor.getPhase().getNext() != null) {
      this.deleteProjectInnovationActorPhase(projectInnovationActor.getPhase().getNext(),
        projectInnovationActor.getProjectInnovation().getId(), projectInnovationActor);
    }

    if (projectInnovationActor.getPhase().getDescription().equals(APConstants.REPORTING)) {
      if (projectInnovationActor.getPhase().getNext() != null
        && projectInnovationActor.getPhase().getNext().getNext() != null) {
        Phase upkeepPhase = projectInnovationActor.getPhase().getNext().getNext();
        if (upkeepPhase != null) {
          this.deleteProjectInnovationActorPhase(upkeepPhase, projectInnovationActor.getProjectInnovation().getId(),
            projectInnovationActor);
        }
      }
    }
    projectInnovationActorDAO.deleteProjectInnovationActor(projectInnovationActorsId);
  }

  public void deleteProjectInnovationActorPhase(Phase next, long innovationID,
    ProjectInnovationActor projectInnovationActors) {
    Phase phase = phaseDAO.find(next.getId());

    List<ProjectInnovationActor> innovationActors =
      projectInnovationActorDAO.getProjectInnovationActorByInnovationAndPhase(innovationID, phase.getId()).stream()
        .filter(c -> c.getActor() != null && c.getActor().getId() != null && projectInnovationActors.getActor() != null
          && projectInnovationActors.getActor().getId() != null
          && c.getActor().getId().equals(projectInnovationActors.getActor().getId()))
        .collect(Collectors.toList());

    for (ProjectInnovationActor projectInnovationActorsDB : innovationActors) {
      if (projectInnovationActorsDB.getId() != null) {
        projectInnovationActorDAO.deleteProjectInnovationActor(projectInnovationActorsDB.getId());
      }
    }

    if (phase.getNext() != null) {
      this.deleteProjectInnovationActorPhase(phase.getNext(), innovationID, projectInnovationActors);
    }
  }

  @Override
  public boolean existProjectInnovationActor(long projectInnovationActorID) {

    return projectInnovationActorDAO.existProjectInnovationActor(projectInnovationActorID);
  }

  @Override
  public List<ProjectInnovationActor> findAll() {

    return projectInnovationActorDAO.findAll();

  }

  @Override
  public ProjectInnovationActor getProjectInnovationActorById(long projectInnovationActorID) {

    return projectInnovationActorDAO.find(projectInnovationActorID);
  }

  @Override
  public List<ProjectInnovationActor> getProjectInnovationActorByInnovationAndPhase(long innovationID, long phaseID) {
    return projectInnovationActorDAO.getProjectInnovationActorByInnovationAndPhase(innovationID, phaseID);
  }

  @Override
  public ProjectInnovationActor saveProjectInnovationActor(ProjectInnovationActor projectInnovationActors) {

    ProjectInnovationActor innovationActor = projectInnovationActorDAO.save(projectInnovationActors);
    Phase phase = phaseDAO.find(innovationActor.getPhase().getId());

    // Conditions to Project Innovation Works In AR phase and Upkeep Phase
    if (phase.getDescription().equals(APConstants.PLANNING) && phase.getNext() != null) {
      this.saveProjectInnovationActorPhase(innovationActor.getPhase().getNext(),
        innovationActor.getProjectInnovation().getId(), projectInnovationActors);
    }

    if (phase.getDescription().equals(APConstants.REPORTING) && phase.getNext() != null
      && phase.getNext().getNext() != null) {
      Phase upkeepPhase = phase.getNext().getNext();
      if (upkeepPhase != null) {
        this.saveProjectInnovationActorPhase(upkeepPhase, innovationActor.getProjectInnovation().getId(),
          projectInnovationActors);
      }
    }

    return innovationActor;
  }

  private void saveProjectInnovationActorPhase(Phase next, Long innovationID,
    ProjectInnovationActor projectInnovationActor) {
    Phase phase = phaseDAO.find(next.getId());
    List<ProjectInnovationActor> innovationActors = new ArrayList<>();
    try {
      innovationActors =

        projectInnovationActorDAO.getProjectInnovationActorByInnovationAndPhase(innovationID, phase.getId()).stream()
          .filter(c -> c.getActor().getId().equals(projectInnovationActor.getActor().getId()))
          .collect(Collectors.toList());
    } catch (Exception e) {
      System.out.println("Error in ActorByInnovationAndPhase method" + e);
    }
    if (innovationActors.isEmpty()) {
      ProjectInnovationActor projectInnovationActorAdd = new ProjectInnovationActor();
      projectInnovationActorAdd.setWomenYouth(projectInnovationActor.getWomenYouth());
      projectInnovationActorAdd.setWomenNotYouth(projectInnovationActor.getWomenNotYouth());
      projectInnovationActorAdd.setMenYouth(projectInnovationActor.getMenYouth());
      projectInnovationActorAdd.setMenNotYouth(projectInnovationActor.getMenNotYouth());
      projectInnovationActorAdd.setNonbinaryYouth(projectInnovationActor.getNonbinaryYouth());
      projectInnovationActorAdd.setNonbinaryNotYouth(projectInnovationActor.getNonbinaryNotYouth());
      projectInnovationActorAdd.setActor(projectInnovationActor.getActor());
      projectInnovationActorAdd.setProjectInnovation(projectInnovationActor.getProjectInnovation());
      projectInnovationActorAdd.setSexAgeNotApply(projectInnovationActor.getSexAgeNotApply());
      projectInnovationActorAdd.setMenYouthNumber(projectInnovationActor.getMenYouthNumber());
      projectInnovationActorAdd.setMenNonYouthNumber(projectInnovationActor.getMenNonYouthNumber());
      projectInnovationActorAdd.setWomenYouthNumber(projectInnovationActor.getWomenYouthNumber());
      projectInnovationActorAdd.setWomenNonYouthNumber(projectInnovationActor.getWomenNonYouthNumber());
      projectInnovationActorAdd.setPhase(phase);
      projectInnovationActorDAO.save(projectInnovationActorAdd);
    } else {
      for (ProjectInnovationActor projectInnovationActorCopy : innovationActors) {
        try {
          projectInnovationActorCopy.setWomenYouth(projectInnovationActor.getWomenYouth());
          projectInnovationActorCopy.setWomenNotYouth(projectInnovationActor.getWomenNotYouth());
          projectInnovationActorCopy.setMenYouth(projectInnovationActor.getMenYouth());
          projectInnovationActorCopy.setMenNotYouth(projectInnovationActor.getMenNotYouth());
          projectInnovationActorCopy.setNonbinaryYouth(projectInnovationActor.getNonbinaryYouth());
          projectInnovationActorCopy.setNonbinaryNotYouth(projectInnovationActor.getNonbinaryNotYouth());
          projectInnovationActorCopy.setActor(projectInnovationActor.getActor());
          projectInnovationActorCopy.setProjectInnovation(projectInnovationActor.getProjectInnovation());
          projectInnovationActorCopy.setSexAgeNotApply(projectInnovationActor.getSexAgeNotApply());
          projectInnovationActorCopy.setMenYouthNumber(projectInnovationActor.getMenYouthNumber());
          projectInnovationActorCopy.setMenNonYouthNumber(projectInnovationActor.getMenNonYouthNumber());
          projectInnovationActorCopy.setWomenYouthNumber(projectInnovationActor.getWomenYouthNumber());
          projectInnovationActorCopy.setWomenNonYouthNumber(projectInnovationActor.getWomenNonYouthNumber());
          projectInnovationActorCopy.setPhase(phase);
          projectInnovationActorDAO.save(projectInnovationActorCopy);
        } catch (Exception e) {
          System.out.println("Error delete ActorByInnovationAndPhase method" + e);
        }
      }
    }
    if (phase.getNext() != null) {
      this.saveProjectInnovationActorPhase(phase.getNext(), innovationID, projectInnovationActor);
    }
  }

}