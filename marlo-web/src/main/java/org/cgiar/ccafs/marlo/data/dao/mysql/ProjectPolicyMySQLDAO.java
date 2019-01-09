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

import org.cgiar.ccafs.marlo.data.dao.ProjectPolicyDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicy;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ProjectPolicyMySQLDAO extends AbstractMarloDAO<ProjectPolicy, Long> implements ProjectPolicyDAO {


  @Inject
  public ProjectPolicyMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteProjectPolicy(long projectPolicyId) {
    ProjectPolicy projectPolicy = this.find(projectPolicyId);
    projectPolicy.setActive(false);
    this.update(projectPolicy);
  }

  @Override
  public boolean existProjectPolicy(long projectPolicyID) {
    ProjectPolicy projectPolicy = this.find(projectPolicyID);
    if (projectPolicy == null) {
      return false;
    }
    return true;

  }

  @Override
  public ProjectPolicy find(long id) {
    return super.find(ProjectPolicy.class, id);

  }

  @Override
  public List<ProjectPolicy> findAll() {
    String query = "from " + ProjectPolicy.class.getName() + " where is_active=1";
    List<ProjectPolicy> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public ProjectPolicy save(ProjectPolicy projectPolicy) {
    if (projectPolicy.getId() == null) {
      super.saveEntity(projectPolicy);
    } else {
      projectPolicy = super.update(projectPolicy);
    }


    return projectPolicy;
  }


}