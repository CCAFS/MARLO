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

import javax.inject.Named;
import javax.inject.Inject;
import org.hibernate.SessionFactory;

@Named
public class ProjectPhaseMySQLDAO extends AbstractMarloDAO<ProjectPhase, Long> implements ProjectPhaseDAO {


  @Inject
  public ProjectPhaseMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteProjectPhase(long projectPhaseId) {
    ProjectPhase projectPhase = this.find(projectPhaseId);

    super.delete(projectPhase);
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
    return super.find(ProjectPhase.class, id);

  }

  @Override
  public List<ProjectPhase> findAll() {
    String query = "from " + ProjectPhase.class.getName() + " ";
    List<ProjectPhase> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public ProjectPhase save(ProjectPhase projectPhase) {
    if (projectPhase.getId() == null) {
      super.saveEntity(projectPhase);
    } else {
      projectPhase = super.update(projectPhase);
    }


    return projectPhase;
  }


}