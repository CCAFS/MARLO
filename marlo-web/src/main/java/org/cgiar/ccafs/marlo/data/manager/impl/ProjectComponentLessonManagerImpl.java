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
package org.cgiar.ccafs.marlo.data.manager.impl;


import org.cgiar.ccafs.marlo.data.dao.ProjectComponentLessonDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectComponentLessonManager;
import org.cgiar.ccafs.marlo.data.model.ProjectComponentLesson;

import java.util.List;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class ProjectComponentLessonManagerImpl implements ProjectComponentLessonManager {


  private ProjectComponentLessonDAO projectComponentLessonDAO;
  // Managers


  @Inject
  public ProjectComponentLessonManagerImpl(ProjectComponentLessonDAO projectComponentLessonDAO) {
    this.projectComponentLessonDAO = projectComponentLessonDAO;


  }

  @Override
  public boolean deleteProjectComponentLesson(long projectComponentLessonId) {

    return projectComponentLessonDAO.deleteProjectComponentLesson(projectComponentLessonId);
  }

  @Override
  public boolean existProjectComponentLesson(long projectComponentLessonID) {

    return projectComponentLessonDAO.existProjectComponentLesson(projectComponentLessonID);
  }

  @Override
  public List<ProjectComponentLesson> findAll() {

    return projectComponentLessonDAO.findAll();

  }

  @Override
  public ProjectComponentLesson getProjectComponentLessonById(long projectComponentLessonID) {

    return projectComponentLessonDAO.find(projectComponentLessonID);
  }

  @Override
  public long saveProjectComponentLesson(ProjectComponentLesson projectComponentLesson) {

    return projectComponentLessonDAO.save(projectComponentLesson);
  }


}
