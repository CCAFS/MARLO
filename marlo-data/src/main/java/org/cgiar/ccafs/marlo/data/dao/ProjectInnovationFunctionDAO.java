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

import org.cgiar.ccafs.marlo.data.model.ProjectInnovationFunction;

import java.util.List;


public interface ProjectInnovationFunctionDAO {

  /**
   * This method removes a specific projectInnovationFunction value from the database.
   * 
   * @param projectInnovationFunctionId is the projectInnovationFunction identifier.
   * @return true if the projectInnovationFunction was successfully deleted, false otherwise.
   */
  public void deleteProjectInnovationFunction(long projectInnovationFunctionId);

  /**
   * This method validate if the projectInnovationFunction identify with the given id exists in the system.
   * 
   * @param projectInnovationFunctionID is a projectInnovationFunction identifier.
   * @return true if the projectInnovationFunction exists, false otherwise.
   */
  public boolean existProjectInnovationFunction(long projectInnovationFunctionID);

  /**
   * This method gets a projectInnovationFunction object by a given projectInnovationFunction identifier.
   * 
   * @param projectInnovationFunctionID is the projectInnovationFunction identifier.
   * @return a ProjectInnovationFunction object.
   */
  public ProjectInnovationFunction find(long id);

  /**
   * This method gets a list of projectInnovationFunction that are active
   * 
   * @return a list from ProjectInnovationFunction null if no exist records
   */
  public List<ProjectInnovationFunction> findAll();


  /**
   * This method saves the information of the given projectInnovationFunction
   * 
   * @param projectInnovationFunction - is the projectInnovationFunction object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the projectInnovationFunction was
   *         updated
   *         or -1 is some error occurred.
   */
  public ProjectInnovationFunction save(ProjectInnovationFunction projectInnovationFunction);
}
