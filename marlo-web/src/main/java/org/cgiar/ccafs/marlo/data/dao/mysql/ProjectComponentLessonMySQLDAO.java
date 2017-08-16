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
import org.hibernate.SessionFactory;

public class ProjectComponentLessonMySQLDAO extends AbstractMarloDAO<ProjectComponentLesson, Long> implements ProjectComponentLessonDAO {


  @Inject
  public ProjectComponentLessonMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
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
    return super.find(ProjectComponentLesson.class, id);

  }

  @Override
  public List<ProjectComponentLesson> findAll() {
    String query = "from " + ProjectComponentLesson.class.getName() + " where is_active=1";
    List<ProjectComponentLesson> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public long save(ProjectComponentLesson projectComponentLesson) {
    if (projectComponentLesson.getId() == null) {
      super.saveEntity(projectComponentLesson);
    } else {
      super.update(projectComponentLesson);
    }


    return projectComponentLesson.getId();
  }


}