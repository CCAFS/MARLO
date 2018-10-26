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

import org.cgiar.ccafs.marlo.data.model.ProjectCenterOutcome;

import java.util.List;


public interface ProjectCenterOutcomeDAO {

  /**
   * This method removes a specific projectCenterOutcome value from the database.
   * 
   * @param projectCenterOutcomeId is the projectCenterOutcome identifier.
   * @return true if the projectCenterOutcome was successfully deleted, false otherwise.
   */
  public void deleteProjectCenterOutcome(long projectCenterOutcomeId);

  /**
   * This method validate if the projectCenterOutcome identify with the given id exists in the system.
   * 
   * @param projectCenterOutcomeID is a projectCenterOutcome identifier.
   * @return true if the projectCenterOutcome exists, false otherwise.
   */
  public boolean existProjectCenterOutcome(long projectCenterOutcomeID);

  /**
   * This method gets a projectCenterOutcome object by a given projectCenterOutcome identifier.
   * 
   * @param projectCenterOutcomeID is the projectCenterOutcome identifier.
   * @return a ProjectCenterOutcome object.
   */
  public ProjectCenterOutcome find(long id);

  /**
   * This method gets a list of projectCenterOutcome that are active
   * 
   * @return a list from ProjectCenterOutcome null if no exist records
   */
  public List<ProjectCenterOutcome> findAll();


  /**
   * This method saves the information of the given projectCenterOutcome
   * 
   * @param projectCenterOutcome - is the projectCenterOutcome object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the projectCenterOutcome was
   *         updated
   *         or -1 is some error occurred.
   */
  public ProjectCenterOutcome save(ProjectCenterOutcome projectCenterOutcome);
}
