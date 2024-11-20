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

import org.cgiar.ccafs.marlo.data.dao.ProjectInnovationReferenceComplementarySolutionDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationReferenceComplementarySolution;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ProjectInnovationReferenceComplementarySolutionMySQLDAO extends AbstractMarloDAO<ProjectInnovationReferenceComplementarySolution, Long> implements ProjectInnovationReferenceComplementarySolutionDAO {


  @Inject
  public ProjectInnovationReferenceComplementarySolutionMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteProjectInnovationReferenceComplementarySolution(long projectInnovationReferenceComplementarySolutionId) {
    ProjectInnovationReferenceComplementarySolution projectInnovationReferenceComplementarySolution = this.find(projectInnovationReferenceComplementarySolutionId);
    projectInnovationReferenceComplementarySolution.setActive(false);
    this.update(projectInnovationReferenceComplementarySolution);
  }

  @Override
  public boolean existProjectInnovationReferenceComplementarySolution(long projectInnovationReferenceComplementarySolutionID) {
    ProjectInnovationReferenceComplementarySolution projectInnovationReferenceComplementarySolution = this.find(projectInnovationReferenceComplementarySolutionID);
    if (projectInnovationReferenceComplementarySolution == null) {
      return false;
    }
    return true;

  }

  @Override
  public ProjectInnovationReferenceComplementarySolution find(long id) {
    return super.find(ProjectInnovationReferenceComplementarySolution.class, id);

  }

  @Override
  public List<ProjectInnovationReferenceComplementarySolution> findAll() {
    String query = "from " + ProjectInnovationReferenceComplementarySolution.class.getName() + " where is_active=1";
    List<ProjectInnovationReferenceComplementarySolution> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public ProjectInnovationReferenceComplementarySolution save(ProjectInnovationReferenceComplementarySolution projectInnovationReferenceComplementarySolution) {
    if (projectInnovationReferenceComplementarySolution.getId() == null) {
      super.saveEntity(projectInnovationReferenceComplementarySolution);
    } else {
      projectInnovationReferenceComplementarySolution = super.update(projectInnovationReferenceComplementarySolution);
    }


    return projectInnovationReferenceComplementarySolution;
  }


}