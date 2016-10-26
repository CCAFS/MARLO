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

import org.cgiar.ccafs.marlo.data.manager.impl.ProjectBranchManagerImpl;
import org.cgiar.ccafs.marlo.data.model.ProjectBranch;

import java.util.List;

import com.google.inject.ImplementedBy;

/**
 * @author Christian Garcia
 */
@ImplementedBy(ProjectBranchManagerImpl.class)
public interface ProjectBranchManager {


  /**
   * This method removes a specific projectBranch value from the database.
   * 
   * @param projectBranchId is the projectBranch identifier.
   * @return true if the projectBranch was successfully deleted, false otherwise.
   */
  public boolean deleteProjectBranch(long projectBranchId);


  /**
   * This method validate if the projectBranch identify with the given id exists in the system.
   * 
   * @param projectBranchID is a projectBranch identifier.
   * @return true if the projectBranch exists, false otherwise.
   */
  public boolean existProjectBranch(long projectBranchID);


  /**
   * This method gets a list of projectBranch that are active
   * 
   * @return a list from ProjectBranch null if no exist records
   */
  public List<ProjectBranch> findAll();


  /**
   * This method gets a projectBranch object by a given projectBranch identifier.
   * 
   * @param projectBranchID is the projectBranch identifier.
   * @return a ProjectBranch object.
   */
  public ProjectBranch getProjectBranchById(long projectBranchID);

  /**
   * This method saves the information of the given projectBranch
   * 
   * @param projectBranch - is the projectBranch object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the projectBranch was
   *         updated
   *         or -1 is some error occurred.
   */
  public long saveProjectBranch(ProjectBranch projectBranch);


}
