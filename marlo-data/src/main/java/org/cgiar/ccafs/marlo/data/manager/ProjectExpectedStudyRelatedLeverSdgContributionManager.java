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

import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyRelatedLeverSdgContribution;

import java.util.List;


/**
 * @author CCAFS
 */

public interface ProjectExpectedStudyRelatedLeverSdgContributionManager {


  /**
   * This method removes a specific projectExpectedStudyRelatedLeverSDGContribution value from the database.
   * 
   * @param projectExpectedStudyRelatedLeverSDGContributionId is the projectExpectedStudyRelatedLeverSDGContribution identifier.
   * @return true if the projectExpectedStudyRelatedLeverSDGContribution was successfully deleted, false otherwise.
   */
  public void deleteProjectExpectedStudyRelatedLeverSdgContribution(long projectExpectedStudyRelatedLeverSDGContributionId);


  /**
   * This method validate if the projectExpectedStudyRelatedLeverSDGContribution identify with the given id exists in the system.
   * 
   * @param projectExpectedStudyRelatedLeverSDGContributionID is a projectExpectedStudyRelatedLeverSDGContribution identifier.
   * @return true if the projectExpectedStudyRelatedLeverSDGContribution exists, false otherwise.
   */
  public boolean existProjectExpectedStudyRelatedLeverSdgContribution(long projectExpectedStudyRelatedLeverSDGContributionID);


  /**
   * This method gets a list of projectExpectedStudyRelatedLeverSDGContribution that are active
   * 
   * @return a list from ProjectExpectedStudyRelatedLeverSdgContribution null if no exist records
   */
  public List<ProjectExpectedStudyRelatedLeverSdgContribution> findAll();


  /**
   * This method gets a projectExpectedStudyRelatedLeverSDGContribution object by a given projectExpectedStudyRelatedLeverSDGContribution identifier.
   * 
   * @param projectExpectedStudyRelatedLeverSDGContributionID is the projectExpectedStudyRelatedLeverSDGContribution identifier.
   * @return a ProjectExpectedStudyRelatedLeverSdgContribution object.
   */
  public ProjectExpectedStudyRelatedLeverSdgContribution getProjectExpectedStudyRelatedLeverSdgContributionById(long projectExpectedStudyRelatedLeverSDGContributionID);

  /**
   * This method saves the information of the given projectExpectedStudyRelatedLeverSDGContribution
   * 
   * @param projectExpectedStudyRelatedLeverSDGContribution - is the projectExpectedStudyRelatedLeverSDGContribution object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the projectExpectedStudyRelatedLeverSDGContribution was
   *         updated
   *         or -1 is some error occurred.
   */
  public ProjectExpectedStudyRelatedLeverSdgContribution saveProjectExpectedStudyRelatedLeverSdgContribution(ProjectExpectedStudyRelatedLeverSdgContribution projectExpectedStudyRelatedLeverSDGContribution);


}
