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

import org.cgiar.ccafs.marlo.data.dao.ProjectInnovationComplementarySolutionDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationComplementarySolution;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ProjectInnovationComplementarySolutionMySQLDAO extends
  AbstractMarloDAO<ProjectInnovationComplementarySolution, Long> implements ProjectInnovationComplementarySolutionDAO {


  @Inject
  public ProjectInnovationComplementarySolutionMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteProjectInnovationComplementarySolution(long projectInnovationComplementarySolutionId) {
    ProjectInnovationComplementarySolution projectInnovationComplementarySolution =
      this.find(projectInnovationComplementarySolutionId);
    projectInnovationComplementarySolution.setActive(false);
    this.update(projectInnovationComplementarySolution);
  }

  @Override
  public boolean existProjectInnovationComplementarySolution(long projectInnovationComplementarySolutionID) {
    ProjectInnovationComplementarySolution projectInnovationComplementarySolution =
      this.find(projectInnovationComplementarySolutionID);
    if (projectInnovationComplementarySolution == null) {
      return false;
    }
    return true;

  }

  @Override
  public ProjectInnovationComplementarySolution find(long id) {
    return super.find(ProjectInnovationComplementarySolution.class, id);

  }

  @Override
  public List<ProjectInnovationComplementarySolution> findAll() {
    String query = "from " + ProjectInnovationComplementarySolution.class.getName() + " where is_active=1";
    List<ProjectInnovationComplementarySolution> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<ProjectInnovationComplementarySolution>
    getProjectInnovationComplementarySolutionByInnovationAndPhase(long innovationID, long phaseID) {
    String query = "from " + ProjectInnovationComplementarySolution.class.getName()
      + " where is_active=1 and project_innovation_id = " + innovationID + " and id_phase = " + phaseID;
    List<ProjectInnovationComplementarySolution> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;
  }

  @Override
  public ProjectInnovationComplementarySolution
    save(ProjectInnovationComplementarySolution projectInnovationComplementarySolution) {
    if (projectInnovationComplementarySolution.getId() == null) {
      super.saveEntity(projectInnovationComplementarySolution);
    } else {
      projectInnovationComplementarySolution = super.update(projectInnovationComplementarySolution);
    }


    return projectInnovationComplementarySolution;
  }

}