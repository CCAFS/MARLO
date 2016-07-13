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

import org.cgiar.ccafs.marlo.data.dao.ProjectFocusDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectFocus;

import java.util.List;

import com.google.inject.Inject;

public class ProjectFocusMySQLDAO implements ProjectFocusDAO {

  private StandardDAO dao;

  @Inject
  public ProjectFocusMySQLDAO(StandardDAO dao) {
    this.dao = dao;
  }

  @Override
  public boolean deleteProjectFocus(long projectFocusId) {
    ProjectFocus projectFocus = this.find(projectFocusId);
    projectFocus.setActive(false);
    return this.save(projectFocus) > 0;
  }

  @Override
  public boolean existProjectFocus(long projectFocusID) {
    ProjectFocus projectFocus = this.find(projectFocusID);
    if (projectFocus == null) {
      return false;
    }
    return true;

  }

  @Override
  public ProjectFocus find(long id) {
    return dao.find(ProjectFocus.class, id);

  }

  @Override
  public List<ProjectFocus> findAll() {
    String query = "from " + ProjectFocus.class.getName() + " where is_active=1";
    List<ProjectFocus> list = dao.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public long save(ProjectFocus projectFocus) {
    if (projectFocus.getId() == null) {
      dao.save(projectFocus);
    } else {
      dao.update(projectFocus);
    }


    return projectFocus.getId();
  }


}