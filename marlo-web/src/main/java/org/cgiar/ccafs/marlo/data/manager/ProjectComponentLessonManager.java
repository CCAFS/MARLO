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
package org.cgiar.ccafs.marlo.data.manager;

import org.cgiar.ccafs.marlo.data.manager.impl.ProjectComponentLessonManagerImpl;
import org.cgiar.ccafs.marlo.data.model.ProjectComponentLesson;

import java.util.List;

import com.google.inject.ImplementedBy;

/**
 * @author Christian Garcia
 */
@ImplementedBy(ProjectComponentLessonManagerImpl.class)
public interface ProjectComponentLessonManager {


  /**
   * This method removes a specific projectComponentLesson value from the database.
   * 
   * @param projectComponentLessonId is the projectComponentLesson identifier.
   * @return true if the projectComponentLesson was successfully deleted, false otherwise.
   */
  public boolean deleteProjectComponentLesson(long projectComponentLessonId);


  /**
   * This method validate if the projectComponentLesson identify with the given id exists in the system.
   * 
   * @param projectComponentLessonID is a projectComponentLesson identifier.
   * @return true if the projectComponentLesson exists, false otherwise.
   */
  public boolean existProjectComponentLesson(long projectComponentLessonID);


  /**
   * This method gets a list of projectComponentLesson that are active
   * 
   * @return a list from ProjectComponentLesson null if no exist records
   */
  public List<ProjectComponentLesson> findAll();


  /**
   * This method gets a projectComponentLesson object by a given projectComponentLesson identifier.
   * 
   * @param projectComponentLessonID is the projectComponentLesson identifier.
   * @return a ProjectComponentLesson object.
   */
  public ProjectComponentLesson getProjectComponentLessonById(long projectComponentLessonID);

  /**
   * This method saves the information of the given projectComponentLesson
   * 
   * @param projectComponentLesson - is the projectComponentLesson object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the projectComponentLesson was
   *         updated
   *         or -1 is some error occurred.
   */
  public long saveProjectComponentLesson(ProjectComponentLesson projectComponentLesson);


}
