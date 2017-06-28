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

public class ProjectInfoMySQLDAO implements ProjectInfoDAO {

  private StandardDAO dao;

  @Inject
  public ProjectInfoMySQLDAO(StandardDAO dao) {
    this.dao = dao;
  }

  @Override
  public boolean deleteProjectInfo(long projectInfoId) {
    ProjectInfo projectInfo = this.find(projectInfoId);
    projectInfo.setActive(false);
    return this.save(projectInfo) > 0;
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
    return dao.find(ProjectInfo.class, id);

  }

  @Override
  public List<ProjectInfo> findAll() {
    String query = "from " + ProjectInfo.class.getName() + " where is_active=1";
    List<ProjectInfo> list = dao.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public long save(ProjectInfo projectInfo) {
    if (projectInfo.getId() == null) {
      dao.save(projectInfo);
    } else {
      dao.update(projectInfo);
    }


    return projectInfo.getId();
  }


}