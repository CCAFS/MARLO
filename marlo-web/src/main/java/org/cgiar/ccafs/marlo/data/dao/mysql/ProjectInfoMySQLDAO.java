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

import org.cgiar.ccafs.marlo.data.dao.ProjectInfoDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectInfo;

import java.util.List;

import com.google.inject.Inject;
import org.hibernate.SessionFactory;

public class ProjectInfoMySQLDAO extends AbstractMarloDAO<ProjectInfo, Long> implements ProjectInfoDAO {


  @Inject
  public ProjectInfoMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteProjectInfo(long projectInfoId) {
    ProjectInfo projectInfo = this.find(projectInfoId);
    super.delete(projectInfo);
  }

  @Override
  public boolean existProjectInfo(long projectInfoID) {
    ProjectInfo projectInfo = this.find(projectInfoID);
    if (projectInfo == null) {
      return false;
    }
    return true;

  }

  @Override
  public ProjectInfo find(long id) {
    return super.find(ProjectInfo.class, id);

  }

  @Override
  public List<ProjectInfo> findAll() {
    String query = "from " + ProjectInfo.class.getName() + "";
    List<ProjectInfo> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public ProjectInfo save(ProjectInfo projectInfo) {
    if (projectInfo.getId() == null) {
      super.saveEntity(projectInfo);
    } else {
      projectInfo = super.update(projectInfo);
    }


    return projectInfo;
  }


}