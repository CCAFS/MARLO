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


package org.cgiar.ccafs.marlo.data.dao;

import org.cgiar.ccafs.marlo.data.model.ProjectClusterActivity;

import java.util.List;


public interface ProjectClusterActivityDAO {

  /**
   * This method removes a specific projectClusterActivity value from the database.
   * 
   * @param projectClusterActivityId is the projectClusterActivity identifier.
   * @return true if the projectClusterActivity was successfully deleted, false otherwise.
   */
  public void deleteProjectClusterActivity(long projectClusterActivityId);

  /**
   * This method validate if the projectClusterActivity identify with the given id exists in the system.
   * 
   * @param projectClusterActivityID is a projectClusterActivity identifier.
   * @return true if the projectClusterActivity exists, false otherwise.
   */
  public boolean existProjectClusterActivity(long projectClusterActivityID);

  /**
   * This method gets a projectClusterActivity object by a given projectClusterActivity identifier.
   * 
   * @param projectClusterActivityID is the projectClusterActivity identifier.
   * @return a ProjectClusterActivity object.
   */
  public ProjectClusterActivity find(long id);

  /**
   * This method gets a list of projectClusterActivity that are active
   * 
   * @return a list from ProjectClusterActivity null if no exist records
   */
  public List<ProjectClusterActivity> findAll();


  /**
   * This method saves the information of the given projectClusterActivity
   * 
   * @param projectClusterActivity - is the projectClusterActivity object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the projectClusterActivity
   *         was
   *         updated
   *         or -1 is some error occurred.
   */
  public ProjectClusterActivity save(ProjectClusterActivity projectClusterActivity);
}
