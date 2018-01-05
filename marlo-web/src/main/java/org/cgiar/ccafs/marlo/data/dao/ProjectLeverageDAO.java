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

import org.cgiar.ccafs.marlo.data.model.ProjectLeverage;

import java.util.List;


public interface ProjectLeverageDAO {

  /**
   * This method removes a specific projectLeverage value from the database.
   * 
   * @param projectLeverageId is the projectLeverage identifier.
   * @return true if the projectLeverage was successfully deleted, false otherwise.
   */
  public void deleteProjectLeverage(long projectLeverageId);

  /**
   * This method validate if the projectLeverage identify with the given id exists in the system.
   * 
   * @param projectLeverageID is a projectLeverage identifier.
   * @return true if the projectLeverage exists, false otherwise.
   */
  public boolean existProjectLeverage(long projectLeverageID);

  /**
   * This method gets a projectLeverage object by a given projectLeverage identifier.
   * 
   * @param projectLeverageID is the projectLeverage identifier.
   * @return a ProjectLeverage object.
   */
  public ProjectLeverage find(long id);

  /**
   * This method gets a list of projectLeverage that are active
   * 
   * @return a list from ProjectLeverage null if no exist records
   */
  public List<ProjectLeverage> findAll();


  /**
   * This method saves the information of the given projectLeverage
   * 
   * @param projectLeverage - is the projectLeverage object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the projectLeverage was
   *         updated
   *         or -1 is some error occurred.
   */
  public ProjectLeverage save(ProjectLeverage projectLeverage);
}
