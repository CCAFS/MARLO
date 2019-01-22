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

import org.cgiar.ccafs.marlo.data.dao.ProjectPolicyOwnerDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicyOwner;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ProjectPolicyOwnerMySQLDAO extends AbstractMarloDAO<ProjectPolicyOwner, Long>
  implements ProjectPolicyOwnerDAO {


  @Inject
  public ProjectPolicyOwnerMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteProjectPolicyOwner(long projectPolicyOwnerId) {
    ProjectPolicyOwner projectPolicyOwner = this.find(projectPolicyOwnerId);
    this.delete(projectPolicyOwner);
  }

  @Override
  public boolean existProjectPolicyOwner(long projectPolicyOwnerID) {
    ProjectPolicyOwner projectPolicyOwner = this.find(projectPolicyOwnerID);
    if (projectPolicyOwner == null) {
      return false;
    }
    return true;

  }

  @Override
  public ProjectPolicyOwner find(long id) {
    return super.find(ProjectPolicyOwner.class, id);

  }

  @Override
  public List<ProjectPolicyOwner> findAll() {
    String query = "from " + ProjectPolicyOwner.class.getName();
    List<ProjectPolicyOwner> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public ProjectPolicyOwner save(ProjectPolicyOwner projectPolicyOwner) {
    if (projectPolicyOwner.getId() == null) {
      super.saveEntity(projectPolicyOwner);
    } else {
      projectPolicyOwner = super.update(projectPolicyOwner);
    }


    return projectPolicyOwner;
  }


}