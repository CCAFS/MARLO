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

import org.cgiar.ccafs.marlo.data.dao.ProjectExpectedStudyInnovationDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyInnovation;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ProjectExpectedStudyInnovationMySQLDAO extends AbstractMarloDAO<ProjectExpectedStudyInnovation, Long>
  implements ProjectExpectedStudyInnovationDAO {


  @Inject
  public ProjectExpectedStudyInnovationMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteProjectExpectedStudyInnovation(long projectExpectedStudyInnovationId) {
    ProjectExpectedStudyInnovation projectExpectedStudyInnovation = this.find(projectExpectedStudyInnovationId);
    this.delete(projectExpectedStudyInnovation);
  }

  @Override
  public boolean existProjectExpectedStudyInnovation(long projectExpectedStudyInnovationID) {
    ProjectExpectedStudyInnovation projectExpectedStudyInnovation = this.find(projectExpectedStudyInnovationID);
    if (projectExpectedStudyInnovation == null) {
      return false;
    }
    return true;

  }

  @Override
  public ProjectExpectedStudyInnovation find(long id) {
    return super.find(ProjectExpectedStudyInnovation.class, id);

  }

  @Override
  public List<ProjectExpectedStudyInnovation> findAll() {
    String query = "from " + ProjectExpectedStudyInnovation.class.getName();
    List<ProjectExpectedStudyInnovation> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public ProjectExpectedStudyInnovation save(ProjectExpectedStudyInnovation projectExpectedStudyInnovation) {
    if (projectExpectedStudyInnovation.getId() == null) {
      super.saveEntity(projectExpectedStudyInnovation);
    } else {
      projectExpectedStudyInnovation = super.update(projectExpectedStudyInnovation);
    }


    return projectExpectedStudyInnovation;
  }


}