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
import org.cgiar.ccafs.marlo.data.dao.ProjectFocusDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectFocusManager;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectFocus;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author Christian Garcia
 */
@Named
public class ProjectFocusManagerImpl implements ProjectFocusManager {


  private ProjectFocusDAO projectFocusDAO;
  private PhaseDAO phaseDAO;

  // Managers


  @Inject
  public ProjectFocusManagerImpl(ProjectFocusDAO projectFocusDAO, PhaseDAO phaseDAO) {
    this.projectFocusDAO = projectFocusDAO;
    this.phaseDAO = phaseDAO;


  }

  public void addProjectFocusPhase(Phase next, long projecID, ProjectFocus projectFocus) {
    Phase phase = phaseDAO.find(next.getId());
    List<ProjectFocus> projectFocuses = phase.getProjectFocuses().stream()
      .filter(c -> c.isActive() && c.getProject().getId().longValue() == projecID
        && projectFocus.getCrpProgram().getId().longValue() == c.getCrpProgram().getId().longValue())
      .collect(Collectors.toList());
    if ( projectFocuses.isEmpty()) {

      ProjectFocus projectFocusAdd = new ProjectFocus();
      projectFocusAdd.setActive(true);
      projectFocusAdd.setActiveSince(projectFocus.getActiveSince());
      projectFocusAdd.setCreatedBy(projectFocus.getCreatedBy());
      projectFocusAdd.setCrpProgram(projectFocus.getCrpProgram());
      projectFocusAdd.setModificationJustification(projectFocus.getModificationJustification());
      projectFocusAdd.setModifiedBy(projectFocus.getModifiedBy());
      projectFocusAdd.setPhase(phase);
      projectFocusAdd.setProject(projectFocus.getProject());
      projectFocusDAO.save(projectFocusAdd);
    } else {
      for (ProjectFocus projectFocusBD : projectFocuses) {
        projectFocusBD.setActive(projectFocus.isActive());
        projectFocusDAO.save(projectFocusBD);

      }
    }
    if (phase.getNext() != null) {
      this.addProjectFocusPhase(phase.getNext(), projecID, projectFocus);
    }


  }

  @Override
  public void deleteProjectFocus(long projectFocusId) {

    projectFocusDAO.deleteProjectFocus(projectFocusId);
    ProjectFocus projectFocus = this.getProjectFocusById(projectFocusId);
    if (projectFocus.getPhase().getNext() != null) {
      this.deletProjectFocusPhase(projectFocus.getPhase().getNext(), projectFocus.getProject().getId(), projectFocus);
    }
  }

  public void deletProjectFocusPhase(Phase next, long projecID, ProjectFocus projectFocus) {
    Phase phase = phaseDAO.find(next.getId());

    List<ProjectFocus> projectFocuses = phase.getProjectFocuses().stream()
      .filter(c -> c.isActive() && c.getProject().getId().longValue() == projecID
        && projectFocus.getCrpProgram().getId().longValue() == c.getCrpProgram().getId().longValue())
      .collect(Collectors.toList());
    for (ProjectFocus projectFocusDB : projectFocuses) {
      this.deleteProjectFocus(projectFocusDB.getId());
    }

    if (phase.getNext() != null) {
      this.deletProjectFocusPhase(phase.getNext(), projecID, projectFocus);
    }


  }

  @Override
  public boolean existProjectFocus(long projectFocusID) {

    return projectFocusDAO.existProjectFocus(projectFocusID);
  }

  @Override
  public List<ProjectFocus> findAll() {

    return projectFocusDAO.findAll();

  }

  @Override
  public ProjectFocus getProjectFocusById(long projectFocusID) {

    return projectFocusDAO.find(projectFocusID);
  }

  @Override
  public ProjectFocus saveProjectFocus(ProjectFocus projectFocus) {

    ProjectFocus focus = projectFocusDAO.save(projectFocus);

    if (projectFocus.getPhase().getNext() != null) {
      this.addProjectFocusPhase(projectFocus.getPhase().getNext(), projectFocus.getProject().getId(), projectFocus);
    }
    return focus;
  }

}
