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

import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.dao.ProjectInfoDAO;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectInfo;
import org.cgiar.ccafs.marlo.data.model.ProjectPhase;

import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

import com.google.inject.Inject;

public class ProjectInfoMySQLDAO implements ProjectInfoDAO {

  private StandardDAO dao;

  @Inject
  public ProjectInfoMySQLDAO(StandardDAO dao) {
    this.dao = dao;
  }

  @Override
  public boolean deleteProjectInfo(long projectInfoId) {
    ProjectInfo projectInfo = this.find(projectInfoId);
    return dao.delete(projectInfo);
  }

  @Override
  public boolean existProjectInfo(long projectInfoID) {
    ProjectInfo projectInfo = this.find(projectInfoID);
    if (projectInfo == null) {
      return false;
    }
    return true;

  }

  @Override
  public ProjectInfo find(long id) {
    return dao.find(ProjectInfo.class, id);

  }

  @Override
  public List<ProjectInfo> findAll() {
    String query = "from " + ProjectInfo.class.getName() + "";
    List<ProjectInfo> list = dao.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public long save(ProjectInfo projectInfo) {
    if (projectInfo.getId() == null) {
      dao.save(projectInfo);
    } else {
      dao.update(projectInfo);
    }

    if (projectInfo.getPhase().getDescription().equals(APConstants.PLANNING)) {
      if (projectInfo.getPhase().getNext() != null) {
        this.saveInfoPhase(projectInfo.getPhase().getNext(), projectInfo.getProject().getId(), projectInfo);
      }
    }


    return projectInfo.getId();
  }

  public void saveInfoPhase(Phase next, long projecID, ProjectInfo projectInfo) {
    Phase phase = dao.find(Phase.class, next.getId());
    if (phase.getEditable() != null && phase.getEditable()) {
      List<ProjectInfo> projectInfos = phase.getProjectInfos().stream()
        .filter(c -> c.isActive() && c.getProject().getId().longValue() == projecID).collect(Collectors.toList());
      if (!projectInfos.isEmpty()) {
        for (ProjectInfo projectInfoPhase : projectInfos) {
          projectInfoPhase.updateProjectInfo(projectInfo);
          dao.update(projectInfoPhase);
        }
      } else {
        if (projectInfo.getEndDate() != null) {
          Calendar cal = Calendar.getInstance();
          cal.setTime(projectInfo.getEndDate());
          if (cal.get(Calendar.YEAR) >= phase.getYear()) {
            ProjectInfo projectInfoPhaseAdd = new ProjectInfo();
            projectInfoPhaseAdd.setProject(projectInfo.getProject());
            projectInfoPhaseAdd.setPhase(phase);
            projectInfoPhaseAdd.setProjectEditLeader(false);
            projectInfoPhaseAdd.updateProjectInfo(projectInfo);
            dao.save(projectInfoPhaseAdd);
            ProjectPhase projectPhase = new ProjectPhase();
            projectPhase.setPhase(phase);
            projectPhase.setProject(projectInfo.getProject());
            dao.save(projectPhase);
          }
        }
      }


    }
    if (phase.getNext() != null) {
      this.saveInfoPhase(phase.getNext(), projecID, projectInfo);
    }


  }


}