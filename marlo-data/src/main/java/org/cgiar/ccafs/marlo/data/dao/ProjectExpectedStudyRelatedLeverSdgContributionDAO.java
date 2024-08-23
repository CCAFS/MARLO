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

import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyRelatedLeverSdgContribution;

import java.util.List;


public interface ProjectExpectedStudyRelatedLeverSdgContributionDAO {

  /**
   * This method removes a specific projectExpectedStudyRelatedLeverSdgContribution value from the database.
   * 
   * @param projectExpectedStudyRelatedLeverSdgContributionId is the projectExpectedStudyRelatedLeverSdgContribution identifier.
   * @return true if the projectExpectedStudyRelatedLeverSdgContribution was successfully deleted, false otherwise.
   */
  public void deleteProjectExpectedStudyRelatedLeverSdgContribution(long projectExpectedStudyRelatedLeverSdgContributionId);

  /**
   * This method validate if the projectExpectedStudyRelatedLeverSdgContribution identify with the given id exists in the system.
   * 
   * @param projectExpectedStudyRelatedLeverSdgContributionID is a projectExpectedStudyRelatedLeverSdgContribution identifier.
   * @return true if the projectExpectedStudyRelatedLeverSdgContribution exists, false otherwise.
   */
  public boolean existProjectExpectedStudyRelatedLeverSdgContribution(long projectExpectedStudyRelatedLeverSdgContributionID);

  /**
   * This method gets a projectExpectedStudyRelatedLeverSdgContribution object by a given projectExpectedStudyRelatedLeverSdgContribution identifier.
   * 
   * @param projectExpectedStudyRelatedLeverSdgContributionID is the projectExpectedStudyRelatedLeverSdgContribution identifier.
   * @return a ProjectExpectedStudyRelatedLeverSdgContribution object.
   */
  public ProjectExpectedStudyRelatedLeverSdgContribution find(long id);

  /**
   * This method gets a list of projectExpectedStudyRelatedLeverSdgContribution that are active
   * 
   * @return a list from ProjectExpectedStudyRelatedLeverSdgContribution null if no exist records
   */
  public List<ProjectExpectedStudyRelatedLeverSdgContribution> findAll();


  /**
   * This method saves the information of the given projectExpectedStudyRelatedLeverSdgContribution
   * 
   * @param projectExpectedStudyRelatedLeverSdgContribution - is the projectExpectedStudyRelatedLeverSdgContribution object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the projectExpectedStudyRelatedLeverSdgContribution was
   *         updated
   *         or -1 is some error occurred.
   */
  public ProjectExpectedStudyRelatedLeverSdgContribution save(ProjectExpectedStudyRelatedLeverSdgContribution projectExpectedStudyRelatedLeverSdgContribution);
}
