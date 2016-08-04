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

import org.cgiar.ccafs.marlo.data.dao.ProjectComponentLessonDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectComponentLesson;

import java.util.List;

import com.google.inject.Inject;

public class ProjectComponentLessonMySQLDAO implements ProjectComponentLessonDAO {

  private StandardDAO dao;

  @Inject
  public ProjectComponentLessonMySQLDAO(StandardDAO dao) {
    this.dao = dao;
  }

  @Override
  public boolean deleteProjectComponentLesson(long projectComponentLessonId) {
    ProjectComponentLesson projectComponentLesson = this.find(projectComponentLessonId);
    projectComponentLesson.setActive(false);
    return this.save(projectComponentLesson) > 0;
  }

  @Override
  public boolean existProjectComponentLesson(long projectComponentLessonID) {
    ProjectComponentLesson projectComponentLesson = this.find(projectComponentLessonID);
    if (projectComponentLesson == null) {
      return false;
    }
    return true;

  }

  @Override
  public ProjectComponentLesson find(long id) {
    return dao.find(ProjectComponentLesson.class, id);

  }

  @Override
  public List<ProjectComponentLesson> findAll() {
    String query = "from " + ProjectComponentLesson.class.getName() + " where is_active=1";
    List<ProjectComponentLesson> list = dao.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public long save(ProjectComponentLesson projectComponentLesson) {
    if (projectComponentLesson.getId() == null) {
      dao.save(projectComponentLesson);
    } else {
      dao.update(projectComponentLesson);
    }


    return projectComponentLesson.getId();
  }


}