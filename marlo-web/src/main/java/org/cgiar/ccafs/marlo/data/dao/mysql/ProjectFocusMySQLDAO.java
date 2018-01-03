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

import javax.inject.Named;
import javax.inject.Inject;
import org.hibernate.SessionFactory;

@Named
public class ProjectFocusMySQLDAO extends AbstractMarloDAO<ProjectFocus, Long> implements ProjectFocusDAO {


  @Inject
  public ProjectFocusMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }


  @Override
  public void deleteProjectFocus(long projectFocusId) {
    ProjectFocus projectFocus = this.find(projectFocusId);
    projectFocus.setActive(false);
    super.update(projectFocus);


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
    return super.find(ProjectFocus.class, id);

  }

  @Override
  public List<ProjectFocus> findAll() {
    String query = "from " + ProjectFocus.class.getName() + " where is_active=1";
    List<ProjectFocus> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public ProjectFocus save(ProjectFocus projectFocus) {
    if (projectFocus.getId() == null) {
      super.saveEntity(projectFocus);
    } else {
      projectFocus = super.update(projectFocus);
    }

    return projectFocus;
  }


}