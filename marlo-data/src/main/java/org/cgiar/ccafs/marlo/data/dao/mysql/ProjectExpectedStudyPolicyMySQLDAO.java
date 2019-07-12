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

import org.cgiar.ccafs.marlo.data.dao.ProjectExpectedStudyPolicyDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyPolicy;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ProjectExpectedStudyPolicyMySQLDAO extends AbstractMarloDAO<ProjectExpectedStudyPolicy, Long>
  implements ProjectExpectedStudyPolicyDAO {


  @Inject
  public ProjectExpectedStudyPolicyMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteProjectExpectedStudyPolicy(long projectExpectedStudyPolicyId) {
    ProjectExpectedStudyPolicy projectExpectedStudyPolicy = this.find(projectExpectedStudyPolicyId);
    this.delete(projectExpectedStudyPolicy);
  }

  @Override
  public boolean existProjectExpectedStudyPolicy(long projectExpectedStudyPolicyID) {
    ProjectExpectedStudyPolicy projectExpectedStudyPolicy = this.find(projectExpectedStudyPolicyID);
    if (projectExpectedStudyPolicy == null) {
      return false;
    }
    return true;

  }

  @Override
  public ProjectExpectedStudyPolicy find(long id) {
    return super.find(ProjectExpectedStudyPolicy.class, id);

  }

  @Override
  public List<ProjectExpectedStudyPolicy> findAll() {
    String query = "from " + ProjectExpectedStudyPolicy.class.getName();
    List<ProjectExpectedStudyPolicy> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public ProjectExpectedStudyPolicy save(ProjectExpectedStudyPolicy projectExpectedStudyPolicy) {
    if (projectExpectedStudyPolicy.getId() == null) {
      super.saveEntity(projectExpectedStudyPolicy);
    } else {
      projectExpectedStudyPolicy = super.update(projectExpectedStudyPolicy);
    }


    return projectExpectedStudyPolicy;
  }


}