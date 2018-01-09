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

import org.cgiar.ccafs.marlo.data.model.ProjectOtherContribution;

import java.util.List;


/**
 * @author Christian Garcia
 */

public interface ProjectOtherContributionManager {


  /**
   * This method removes a specific projectOtherContribution value from the database.
   * 
   * @param projectOtherContributionId is the projectOtherContribution identifier.
   * @return true if the projectOtherContribution was successfully deleted, false otherwise.
   */
  public void deleteProjectOtherContribution(long projectOtherContributionId);


  /**
   * This method validate if the projectOtherContribution identify with the given id exists in the system.
   * 
   * @param projectOtherContributionID is a projectOtherContribution identifier.
   * @return true if the projectOtherContribution exists, false otherwise.
   */
  public boolean existProjectOtherContribution(long projectOtherContributionID);


  /**
   * This method gets a list of projectOtherContribution that are active
   * 
   * @return a list from ProjectOtherContribution null if no exist records
   */
  public List<ProjectOtherContribution> findAll();


  /**
   * This method gets a projectOtherContribution object by a given projectOtherContribution identifier.
   * 
   * @param projectOtherContributionID is the projectOtherContribution identifier.
   * @return a ProjectOtherContribution object.
   */
  public ProjectOtherContribution getProjectOtherContributionById(long projectOtherContributionID);

  /**
   * This method saves the information of the given projectOtherContribution
   * 
   * @param projectOtherContribution - is the projectOtherContribution object with the new information to be
   *        added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the projectOtherContribution
   *         was
   *         updated
   *         or -1 is some error occurred.
   */
  public ProjectOtherContribution saveProjectOtherContribution(ProjectOtherContribution projectOtherContribution);


}
