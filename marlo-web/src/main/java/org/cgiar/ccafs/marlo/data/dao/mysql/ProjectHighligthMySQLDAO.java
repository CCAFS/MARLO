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

import org.cgiar.ccafs.marlo.data.dao.ProjectHighligthDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectHighligth;

import java.util.List;

import com.google.inject.Inject;

public class ProjectHighligthMySQLDAO implements ProjectHighligthDAO {

  private StandardDAO dao;

  @Inject
  public ProjectHighligthMySQLDAO(StandardDAO dao) {
    this.dao = dao;
  }

  @Override
  public boolean deleteProjectHighligth(long projectHighligthId) {
    ProjectHighligth projectHighligth = this.find(projectHighligthId);
    projectHighligth.setActive(false);
    return this.save(projectHighligth) > 0;
  }

  @Override
  public boolean existProjectHighligth(long projectHighligthID) {
    ProjectHighligth projectHighligth = this.find(projectHighligthID);
    if (projectHighligth == null) {
      return false;
    }
    return true;

  }

  @Override
  public ProjectHighligth find(long id) {
    return dao.find(ProjectHighligth.class, id);

  }

  @Override
  public List<ProjectHighligth> findAll() {
    String query = "from " + ProjectHighligth.class.getName() + " where is_active=1";
    List<ProjectHighligth> list = dao.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public long save(ProjectHighligth projectHighligth) {
    if (projectHighligth.getId() == null) {
      dao.save(projectHighligth);
    } else {
      dao.update(projectHighligth);
    }


    return projectHighligth.getId();
  }


}