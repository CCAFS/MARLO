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

import org.cgiar.ccafs.marlo.data.model.ProjectInnovationComplementarySolutionFunction;

import java.util.List;


/**
 * @author CCAFS
 */

public interface ProjectInnovationComplementarySolutionFunctionManager {


  /**
   * This method removes a specific projectInnovationComplementarySolutionFunction value from the database.
   * 
   * @param projectInnovationComplementarySolutionFunctionId is the projectInnovationComplementarySolutionFunction identifier.
   * @return true if the projectInnovationComplementarySolutionFunction was successfully deleted, false otherwise.
   */
  public void deleteProjectInnovationComplementarySolutionFunction(long projectInnovationComplementarySolutionFunctionId);


  /**
   * This method validate if the projectInnovationComplementarySolutionFunction identify with the given id exists in the system.
   * 
   * @param projectInnovationComplementarySolutionFunctionID is a projectInnovationComplementarySolutionFunction identifier.
   * @return true if the projectInnovationComplementarySolutionFunction exists, false otherwise.
   */
  public boolean existProjectInnovationComplementarySolutionFunction(long projectInnovationComplementarySolutionFunctionID);


  /**
   * This method gets a list of projectInnovationComplementarySolutionFunction that are active
   * 
   * @return a list from ProjectInnovationComplementarySolutionFunction null if no exist records
   */
  public List<ProjectInnovationComplementarySolutionFunction> findAll();


  /**
   * This method gets a projectInnovationComplementarySolutionFunction object by a given projectInnovationComplementarySolutionFunction identifier.
   * 
   * @param projectInnovationComplementarySolutionFunctionID is the projectInnovationComplementarySolutionFunction identifier.
   * @return a ProjectInnovationComplementarySolutionFunction object.
   */
  public ProjectInnovationComplementarySolutionFunction getProjectInnovationComplementarySolutionFunctionById(long projectInnovationComplementarySolutionFunctionID);

  /**
   * This method saves the information of the given projectInnovationComplementarySolutionFunction
   * 
   * @param projectInnovationComplementarySolutionFunction - is the projectInnovationComplementarySolutionFunction object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the projectInnovationComplementarySolutionFunction was
   *         updated
   *         or -1 is some error occurred.
   */
  public ProjectInnovationComplementarySolutionFunction saveProjectInnovationComplementarySolutionFunction(ProjectInnovationComplementarySolutionFunction projectInnovationComplementarySolutionFunction);


}
