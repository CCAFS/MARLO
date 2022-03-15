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

import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyFundingSource;

import java.util.List;


public interface ProjectExpectedStudyFundingSourceDAO {

  /**
   * This method removes a specific projectExpectedStudyFundingSource value from the database.
   * 
   * @param projectExpectedStudyFundingSourceId is the projectExpectedStudyFundingSource
   *        identifier.
   * @return true if the projectExpectedStudyFundingSource was successfully deleted, false otherwise.
   */
  public void deleteProjectExpectedStudyFundingSource(long projectExpectedStudyFundingSourceId);

  /**
   * This method validate if the projectExpectedStudyFundingSource identify with the given id exists in the
   * system.
   * 
   * @param projectExpectedStudyFundingSourceID is a projectExpectedStudyFundingSource
   *        identifier.
   * @return true if the projectExpectedStudyFundingSource exists, false otherwise.
   */
  public boolean existProjectExpectedStudyFundingSource(long projectExpectedStudyFundingSourceID);

  /**
   * This method gets a projectExpectedStudyFundingSource object by a given
   * projectExpectedStudyFundingSource identifier.
   * 
   * @param projectExpectedStudyFundingSourceID is the projectExpectedStudyFundingSource
   *        identifier.
   * @return a ProjectExpectedStudyFundingSource object.
   */
  public ProjectExpectedStudyFundingSource find(long id);

  /**
   * This method gets a list of projectExpectedStudyFundingSource that are active
   * 
   * @return a list from ProjectExpectedStudyFundingSource null if no exist records
   */
  public List<ProjectExpectedStudyFundingSource> findAll();


  /**
   * This method gets a list of projectExpectedStudyFundingSource by a given projectExpectedStudy
   * identifier.
   * 
   * @param studyId is the projectExpectedStudy identifier.
   * @return a list of projectExpectedStudyFundingSource objects.
   */
  public List<ProjectExpectedStudyFundingSource> getAllStudyFundingSourcesByStudy(long studyId);

  /**
   * This method gets a projectExpectedStudyFundingSource object by a given
   * projectExpectedStudyFundingSource identifier.
   * 
   * @param expectedID is the projectExpectedStudyFundingSource identifier.
   * @param fundingSourceID is the projectExpectedStudyFundingSource identifier.
   * @param phaseID is the projectExpectedStudyFundingSource identifier.
   * @return a ProjectExpectedStudyFundingSource object.
   */
  public ProjectExpectedStudyFundingSource getProjectExpectedStudyFundingSourceByPhase(Long expectedID,
    Long fundingSourceID, Long phaseID);

  /**
   * This method saves the information of the given projectExpectedStudyFundingSource
   * 
   * @param projectExpectedStudyFundingSource - is the projectExpectedStudyFundingSource
   *        object with the new information to be
   *        added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the
   *         projectExpectedStudyFundingSource
   *         was
   *         updated
   *         or -1 is some error occurred.
   */
  public ProjectExpectedStudyFundingSource save(ProjectExpectedStudyFundingSource projectExpectedStudyFundingSource);
}
