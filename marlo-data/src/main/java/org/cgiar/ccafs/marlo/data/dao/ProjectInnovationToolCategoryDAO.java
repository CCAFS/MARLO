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

import org.cgiar.ccafs.marlo.data.model.ProjectInnovationToolCategory;

import java.util.List;


public interface ProjectInnovationToolCategoryDAO {

  /**
   * This method removes a specific projectInnovationToolCategory value from the database.
   * 
   * @param projectInnovationToolCategoryId is the projectInnovationToolCategory identifier.
   * @return true if the projectInnovationToolCategory was successfully deleted, false otherwise.
   */
  public void deleteProjectInnovationToolCategory(long projectInnovationToolCategoryId);

  /**
   * This method validate if the projectInnovationToolCategory identify with the given id exists in the system.
   * 
   * @param projectInnovationToolCategoryID is a projectInnovationToolCategory identifier.
   * @return true if the projectInnovationToolCategory exists, false otherwise.
   */
  public boolean existProjectInnovationToolCategory(long projectInnovationToolCategoryID);

  /**
   * This method gets a projectInnovationToolCategory object by a given projectInnovationToolCategory identifier.
   * 
   * @param projectInnovationToolCategoryID is the projectInnovationToolCategory identifier.
   * @return a ProjectInnovationToolCategory object.
   */
  public ProjectInnovationToolCategory find(long id);

  /**
   * This method gets a list of projectInnovationToolCategory that are active
   * 
   * @return a list from ProjectInnovationToolCategory null if no exist records
   */
  public List<ProjectInnovationToolCategory> findAll();

  /**
   * This method gets a list of projectInnovationToolCategory that are active
   * 
   * @param projectInnovationID is a projectInnovation identifier.
   * @param phaseID is a phase identifier.
   * @return a list from ProjectInnovationToolCategory null if no exist records
   */
  public List<ProjectInnovationToolCategory> getProjectInnovationToolCategoryByInnovationAndPhase(long innovationID,
    long phaseID);

  /**
   * This method saves the information of the given projectInnovationToolCategory
   * 
   * @param projectInnovationToolCategory - is the projectInnovationToolCategory object with the new information to be
   *        added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the
   *         projectInnovationToolCategory was
   *         updated
   *         or -1 is some error occurred.
   */
  public ProjectInnovationToolCategory save(ProjectInnovationToolCategory projectInnovationToolCategory);
}
