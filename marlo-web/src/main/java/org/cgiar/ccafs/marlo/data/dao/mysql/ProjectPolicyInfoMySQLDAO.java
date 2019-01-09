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

import org.cgiar.ccafs.marlo.data.dao.ProjectPolicyInfoDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicyInfo;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ProjectPolicyInfoMySQLDAO extends AbstractMarloDAO<ProjectPolicyInfo, Long>
  implements ProjectPolicyInfoDAO {


  @Inject
  public ProjectPolicyInfoMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteProjectPolicyInfo(long projectPolicyInfoId) {
    this.delete(projectPolicyInfoId);
  }

  @Override
  public boolean existProjectPolicyInfo(long projectPolicyInfoID) {
    ProjectPolicyInfo projectPolicyInfo = this.find(projectPolicyInfoID);
    if (projectPolicyInfo == null) {
      return false;
    }
    return true;

  }

  @Override
  public ProjectPolicyInfo find(long id) {
    return super.find(ProjectPolicyInfo.class, id);

  }

  @Override
  public List<ProjectPolicyInfo> findAll() {
    String query = "from " + ProjectPolicyInfo.class.getName();
    List<ProjectPolicyInfo> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public ProjectPolicyInfo save(ProjectPolicyInfo projectPolicyInfo) {
    if (projectPolicyInfo.getId() == null) {
      super.saveEntity(projectPolicyInfo);
    } else {
      projectPolicyInfo = super.update(projectPolicyInfo);
    }


    return projectPolicyInfo;
  }


}