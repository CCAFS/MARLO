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

import org.cgiar.ccafs.marlo.data.dao.ProjectExpectedStudyInstitutionDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyInstitution;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ProjectExpectedStudyInstitutionMySQLDAO extends AbstractMarloDAO<ProjectExpectedStudyInstitution, Long>
  implements ProjectExpectedStudyInstitutionDAO {


  @Inject
  public ProjectExpectedStudyInstitutionMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteProjectExpectedStudyInstitution(long projectExpectedStudyInstitutionId) {
    ProjectExpectedStudyInstitution projectExpectedStudyInstitution = this.find(projectExpectedStudyInstitutionId);
    this.delete(projectExpectedStudyInstitution);
  }

  @Override
  public boolean existProjectExpectedStudyInstitution(long projectExpectedStudyInstitutionID) {
    ProjectExpectedStudyInstitution projectExpectedStudyInstitution = this.find(projectExpectedStudyInstitutionID);
    if (projectExpectedStudyInstitution == null) {
      return false;
    }
    return true;

  }

  @Override
  public ProjectExpectedStudyInstitution find(long id) {
    return super.find(ProjectExpectedStudyInstitution.class, id);

  }

  @Override
  public List<ProjectExpectedStudyInstitution> findAll() {
    String query = "from " + ProjectExpectedStudyInstitution.class.getName();
    List<ProjectExpectedStudyInstitution> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public ProjectExpectedStudyInstitution save(ProjectExpectedStudyInstitution projectExpectedStudyInstitution) {
    if (projectExpectedStudyInstitution.getId() == null) {
      super.saveEntity(projectExpectedStudyInstitution);
    } else {
      projectExpectedStudyInstitution = super.update(projectExpectedStudyInstitution);
    }


    return projectExpectedStudyInstitution;
  }


}