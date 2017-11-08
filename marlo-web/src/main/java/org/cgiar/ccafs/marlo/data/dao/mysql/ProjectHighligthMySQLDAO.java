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
import org.hibernate.SessionFactory;

public class ProjectHighligthMySQLDAO extends AbstractMarloDAO<ProjectHighlight, Long> implements ProjectHighligthDAO {


  @Inject
  public ProjectHighligthMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteProjectHighligth(long projectHighligthId) {
    ProjectHighlight projectHighlight = this.find(projectHighligthId);
    projectHighlight.setActive(false);
    this.save(projectHighlight);
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
    return super.find(ProjectHighlight.class, id);

  }

  @Override
  public List<ProjectHighlight> findAll() {
    String query = "from " + ProjectHighlight.class.getName() + " where is_active=1";
    List<ProjectHighlight> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public ProjectHighlight save(ProjectHighlight projectHighlight) {
    if (projectHighlight.getId() == null) {
      super.saveEntity(projectHighlight);
    } else {
      projectHighlight = super.update(projectHighlight);
    }


    return projectHighlight;
  }

  @Override
  public ProjectHighlight save(ProjectHighlight projectHighlight, String section, List<String> relationsName) {
    if (projectHighlight.getId() == null) {
      super.saveEntity(projectHighlight, section, relationsName);
    } else {
      projectHighlight = super.update(projectHighlight, section, relationsName);
    }
    return projectHighlight;
  }


}