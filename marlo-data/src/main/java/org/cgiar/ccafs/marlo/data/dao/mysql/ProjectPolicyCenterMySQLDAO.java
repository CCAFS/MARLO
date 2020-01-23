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

/**************
 * @author Diego Perez - CIAT/CCAFS
 **************/

package org.cgiar.ccafs.marlo.data.dao.mysql;

import org.cgiar.ccafs.marlo.data.dao.ProjectPolicyCenterDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicyCenter;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;


@Named
public class ProjectPolicyCenterMySQLDAO extends AbstractMarloDAO<ProjectPolicyCenter, Long>
  implements ProjectPolicyCenterDAO {

  @Inject
  public ProjectPolicyCenterMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteProjectPolicyCenter(long projectPolicyCenterId) {
    ProjectPolicyCenter projectPolicyCenter = this.find(projectPolicyCenterId);
    this.delete(projectPolicyCenter);

  }

  @Override
  public boolean existProjectPolicyCenter(long projectPolicyCenterID) {
    ProjectPolicyCenter projectPolicyCenter = this.find(projectPolicyCenterID);
    if (projectPolicyCenter == null) {
      return false;
    }
    return true;

  }

  @Override
  public ProjectPolicyCenter find(long id) {
    return super.find(ProjectPolicyCenter.class, id);
  }

  @Override
  public List<ProjectPolicyCenter> findAll() {
    String query = "from " + ProjectPolicyCenter.class.getName();
    List<ProjectPolicyCenter> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;
  }

  @Override
  public ProjectPolicyCenter save(ProjectPolicyCenter projectPolicyCenter) {
    if (projectPolicyCenter.getId() == null) {
      super.saveEntity(projectPolicyCenter);
    } else {
      projectPolicyCenter = super.update(projectPolicyCenter);
    }

    return projectPolicyCenter;
  }

}
