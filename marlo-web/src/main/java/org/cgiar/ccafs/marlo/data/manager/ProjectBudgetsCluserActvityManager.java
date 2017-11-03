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

import org.cgiar.ccafs.marlo.data.manager.impl.ProjectBudgetsCluserActvityManagerImpl;
import org.cgiar.ccafs.marlo.data.model.ProjectBudgetsCluserActvity;

import java.util.List;

import com.google.inject.ImplementedBy;

/**
 * @author Christian Garcia
 */
@ImplementedBy(ProjectBudgetsCluserActvityManagerImpl.class)
public interface ProjectBudgetsCluserActvityManager {


  /**
   * This method removes a specific projectBudgetsCluserActvity value from the database.
   * 
   * @param projectBudgetsCluserActvityId is the projectBudgetsCluserActvity identifier.
   * @return true if the projectBudgetsCluserActvity was successfully deleted, false otherwise.
   */
  public void deleteProjectBudgetsCluserActvity(long projectBudgetsCluserActvityId);


  /**
   * This method validate if the projectBudgetsCluserActvity identify with the given id exists in the system.
   * 
   * @param projectBudgetsCluserActvityID is a projectBudgetsCluserActvity identifier.
   * @return true if the projectBudgetsCluserActvity exists, false otherwise.
   */
  public boolean existProjectBudgetsCluserActvity(long projectBudgetsCluserActvityID);


  /**
   * This method gets a list of projectBudgetsCluserActvity that are active
   * 
   * @return a list from ProjectBudgetsCluserActvity null if no exist records
   */
  public List<ProjectBudgetsCluserActvity> findAll();


  /**
   * This method gets a projectBudgetsCluserActvity object by a given projectBudgetsCluserActvity identifier.
   * 
   * @param projectBudgetsCluserActvityID is the projectBudgetsCluserActvity identifier.
   * @return a ProjectBudgetsCluserActvity object.
   */
  public ProjectBudgetsCluserActvity getProjectBudgetsCluserActvityById(long projectBudgetsCluserActvityID);

  /**
   * This method saves the information of the given projectBudgetsCluserActvity
   * 
   * @param projectBudgetsCluserActvity - is the projectBudgetsCluserActvity object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the projectBudgetsCluserActvity was
   *         updated
   *         or -1 is some error occurred.
   */
  public ProjectBudgetsCluserActvity saveProjectBudgetsCluserActvity(ProjectBudgetsCluserActvity projectBudgetsCluserActvity);


}
