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

import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudySrfTarget;

import java.util.List;


/**
 * @author CCAFS
 */

public interface ProjectExpectedStudySrfTargetManager {


  /**
   * This method removes a specific projectExpectedStudySrfTarget value from the database.
   * 
   * @param projectExpectedStudySrfTargetId is the projectExpectedStudySrfTarget identifier.
   * @return true if the projectExpectedStudySrfTarget was successfully deleted, false otherwise.
   */
  public void deleteProjectExpectedStudySrfTarget(long projectExpectedStudySrfTargetId);


  /**
   * This method validate if the projectExpectedStudySrfTarget identify with the given id exists in the system.
   * 
   * @param projectExpectedStudySrfTargetID is a projectExpectedStudySrfTarget identifier.
   * @return true if the projectExpectedStudySrfTarget exists, false otherwise.
   */
  public boolean existProjectExpectedStudySrfTarget(long projectExpectedStudySrfTargetID);


  /**
   * This method gets a list of projectExpectedStudySrfTarget that are active
   * 
   * @return a list from ProjectExpectedStudySrfTarget null if no exist records
   */
  public List<ProjectExpectedStudySrfTarget> findAll();


  /**
   * This method gets a projectExpectedStudySrfTarget object by a given projectExpectedStudySrfTarget identifier.
   * 
   * @param projectExpectedStudySrfTargetID is the projectExpectedStudySrfTarget identifier.
   * @return a ProjectExpectedStudySrfTarget object.
   */
  public ProjectExpectedStudySrfTarget getProjectExpectedStudySrfTargetById(long projectExpectedStudySrfTargetID);

  /**
   * This method saves the information of the given projectExpectedStudySrfTarget
   * 
   * @param projectExpectedStudySrfTarget - is the projectExpectedStudySrfTarget object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the projectExpectedStudySrfTarget was
   *         updated
   *         or -1 is some error occurred.
   */
  public ProjectExpectedStudySrfTarget saveProjectExpectedStudySrfTarget(ProjectExpectedStudySrfTarget projectExpectedStudySrfTarget);


}
