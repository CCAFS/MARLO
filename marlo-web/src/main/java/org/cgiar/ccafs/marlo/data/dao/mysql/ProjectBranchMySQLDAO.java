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

import org.cgiar.ccafs.marlo.data.dao.ProjectBranchDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectBranch;

import java.util.List;

import com.google.inject.Inject;

public class ProjectBranchMySQLDAO implements ProjectBranchDAO {

  private StandardDAO dao;

  @Inject
  public ProjectBranchMySQLDAO(StandardDAO dao) {
    this.dao = dao;
  }

  @Override
  public boolean deleteProjectBranch(long projectBranchId) {
    ProjectBranch projectBranch = this.find(projectBranchId);
    projectBranch.setActive(false);
    return this.save(projectBranch) > 0;
  }

  @Override
  public boolean existProjectBranch(long projectBranchID) {
    ProjectBranch projectBranch = this.find(projectBranchID);
    if (projectBranch == null) {
      return false;
    }
    return true;

  }

  @Override
  public ProjectBranch find(long id) {
    return dao.find(ProjectBranch.class, id);

  }

  @Override
  public List<ProjectBranch> findAll() {
    String query = "from " + ProjectBranch.class.getName() + " where is_active=1";
    List<ProjectBranch> list = dao.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public long save(ProjectBranch projectBranch) {
    if (projectBranch.getId() == null) {
      dao.save(projectBranch);
    } else {
      dao.update(projectBranch);
    }


    return projectBranch.getId();
  }


}