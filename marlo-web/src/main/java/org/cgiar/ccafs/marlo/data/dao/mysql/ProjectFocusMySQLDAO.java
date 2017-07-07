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


package org.cgiar.ccafs.marlo.data.dao.mysql;

import org.cgiar.ccafs.marlo.data.dao.ProjectFocusDAO;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectFocus;

import java.util.List;
import java.util.stream.Collectors;

import com.google.inject.Inject;

public class ProjectFocusMySQLDAO implements ProjectFocusDAO {

  private StandardDAO dao;

  @Inject
  public ProjectFocusMySQLDAO(StandardDAO dao) {
    this.dao = dao;
  }

  public void addProjectFocusPhase(Phase next, long projecID, ProjectFocus projectFocus) {
    Phase phase = dao.find(Phase.class, next.getId());
    List<ProjectFocus> projectFocuses = phase.getProjectFocuses().stream()
      .filter(c -> c.isActive() && c.getProject().getId().longValue() == projecID
        && projectFocus.getCrpProgram().getId().longValue() == c.getCrpProgram().getId().longValue())
      .collect(Collectors.toList());
    if (phase.getEditable() != null && phase.getEditable() && projectFocuses.isEmpty()) {

      ProjectFocus projectFocusAdd = new ProjectFocus();
      projectFocusAdd.setActive(true);
      projectFocusAdd.setActiveSince(projectFocus.getActiveSince());
      projectFocusAdd.setCreatedBy(projectFocus.getCreatedBy());
      projectFocusAdd.setCrpProgram(projectFocus.getCrpProgram());
      projectFocusAdd.setModificationJustification(projectFocus.getModificationJustification());
      projectFocusAdd.setModifiedBy(projectFocus.getModifiedBy());
      projectFocusAdd.setPhase(phase);
      projectFocusAdd.setProject(projectFocus.getProject());
      this.save(projectFocusAdd);
    } else {
      if (phase.getNext() != null) {
        this.addProjectFocusPhase(phase.getNext(), projecID, projectFocus);
      }
    }


  }


  @Override
  public boolean deleteProjectFocus(long projectFocusId) {
    ProjectFocus projectFocus = this.find(projectFocusId);
    projectFocus.setActive(false);
    long result = this.save(projectFocus);

    if (projectFocus.getPhase().getNext() != null) {
      this.deletProjectFocusPhase(projectFocus.getPhase().getNext(), projectFocus.getProject().getId(), projectFocus);
    }
    return result > 0;
  }

  public void deletProjectFocusPhase(Phase next, long projecID, ProjectFocus projectFocus) {
    Phase phase = dao.find(Phase.class, next.getId());
    if (phase.getEditable() != null && phase.getEditable()) {
      List<ProjectFocus> projectFocuses = phase.getProjectFocuses().stream()
        .filter(c -> c.isActive() && c.getProject().getId().longValue() == projecID
          && projectFocus.getCrpProgram().getId().longValue() == c.getCrpProgram().getId().longValue())
        .collect(Collectors.toList());
      for (ProjectFocus projectFocusDB : projectFocuses) {
        this.deleteProjectFocus(projectFocusDB.getId());
      }
    } else {
      if (phase.getNext() != null) {
        this.deletProjectFocusPhase(phase.getNext(), projecID, projectFocus);
      }
    }


  }

  @Override
  public boolean existProjectFocus(long projectFocusID) {
    ProjectFocus projectFocus = this.find(projectFocusID);
    if (projectFocus == null) {
      return false;
    }
    return true;

  }

  @Override
  public ProjectFocus find(long id) {
    return dao.find(ProjectFocus.class, id);

  }

  @Override
  public List<ProjectFocus> findAll() {
    String query = "from " + ProjectFocus.class.getName() + " where is_active=1";
    List<ProjectFocus> list = dao.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public long save(ProjectFocus projectFocus) {
    if (projectFocus.getId() == null) {
      dao.save(projectFocus);
    } else {
      dao.update(projectFocus);
    }
    if (projectFocus.getPhase().getNext() != null) {
      this.addProjectFocusPhase(projectFocus.getPhase().getNext(), projectFocus.getProject().getId(), projectFocus);
    }

    return projectFocus.getId();
  }


}