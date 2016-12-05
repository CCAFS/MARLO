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
import org.cgiar.ccafs.marlo.data.model.ProjectHighlight;

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
    ProjectHighlight projectHighlight = this.find(projectHighligthId);
    projectHighlight.setActive(false);
    return this.save(projectHighlight) > 0;
  }

  @Override
  public boolean existProjectHighligth(long projectHighligthID) {
    ProjectHighlight projectHighlight = this.find(projectHighligthID);
    if (projectHighlight == null) {
      return false;
    }
    return true;

  }

  @Override
  public ProjectHighlight find(long id) {
    return dao.find(ProjectHighlight.class, id);

  }

  @Override
  public List<ProjectHighlight> findAll() {
    String query = "from " + ProjectHighlight.class.getName() + " where is_active=1";
    List<ProjectHighlight> list = dao.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public long save(ProjectHighlight projectHighlight) {
    if (projectHighlight.getId() == null) {
      dao.save(projectHighlight);
    } else {
      dao.update(projectHighlight);
    }


    return projectHighlight.getId();
  }


}