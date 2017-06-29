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

import org.cgiar.ccafs.marlo.data.dao.ProjectPhaseDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectPhase;

import java.util.List;

import com.google.inject.Inject;

public class ProjectPhaseMySQLDAO implements ProjectPhaseDAO {

  private StandardDAO dao;

  @Inject
  public ProjectPhaseMySQLDAO(StandardDAO dao) {
    this.dao = dao;
  }

  @Override
  public boolean deleteProjectPhase(long projectPhaseId) {
    ProjectPhase projectPhase = this.find(projectPhaseId);

    return this.dao.delete(projectPhase);
  }

  @Override
  public boolean existProjectPhase(long projectPhaseID) {
    ProjectPhase projectPhase = this.find(projectPhaseID);
    if (projectPhase == null) {
      return false;
    }
    return true;

  }

  @Override
  public ProjectPhase find(long id) {
    return dao.find(ProjectPhase.class, id);

  }

  @Override
  public List<ProjectPhase> findAll() {
    String query = "from " + ProjectPhase.class.getName() + " ";
    List<ProjectPhase> list = dao.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public long save(ProjectPhase projectPhase) {
    if (projectPhase.getId() == null) {
      dao.save(projectPhase);
    } else {
      dao.update(projectPhase);
    }


    return projectPhase.getId();
  }


}