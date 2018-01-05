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

import org.cgiar.ccafs.marlo.data.model.ProjectPartnerContribution;

import java.util.List;


/**
 * @author Christian Garcia
 */

public interface ProjectPartnerContributionManager {


  /**
   * This method removes a specific projectPartnerContribution value from the database.
   * 
   * @param projectPartnerContributionId is the projectPartnerContribution identifier.
   * @return true if the projectPartnerContribution was successfully deleted, false otherwise.
   */
  public void deleteProjectPartnerContribution(long projectPartnerContributionId);


  /**
   * This method validate if the projectPartnerContribution identify with the given id exists in the system.
   * 
   * @param projectPartnerContributionID is a projectPartnerContribution identifier.
   * @return true if the projectPartnerContribution exists, false otherwise.
   */
  public boolean existProjectPartnerContribution(long projectPartnerContributionID);


  /**
   * This method gets a list of projectPartnerContribution that are active
   * 
   * @return a list from ProjectPartnerContribution null if no exist records
   */
  public List<ProjectPartnerContribution> findAll();


  /**
   * This method gets a projectPartnerContribution object by a given projectPartnerContribution identifier.
   * 
   * @param projectPartnerContributionID is the projectPartnerContribution identifier.
   * @return a ProjectPartnerContribution object.
   */
  public ProjectPartnerContribution getProjectPartnerContributionById(long projectPartnerContributionID);

  /**
   * This method saves the information of the given projectPartnerContribution
   * 
   * @param projectPartnerContribution - is the projectPartnerContribution object with the new information to be
   *        added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the
   *         projectPartnerContribution was
   *         updated
   *         or -1 is some error occurred.
   */
  public ProjectPartnerContribution
    saveProjectPartnerContribution(ProjectPartnerContribution projectPartnerContribution);


}
