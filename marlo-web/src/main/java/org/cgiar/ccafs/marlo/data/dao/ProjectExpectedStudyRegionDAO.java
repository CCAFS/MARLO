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

import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyRegion;

import java.util.List;


public interface ProjectExpectedStudyRegionDAO {

  /**
   * This method removes a specific projectExpectedStudyRegion value from the database.
   * 
   * @param projectExpectedStudyRegionId is the projectExpectedStudyRegion identifier.
   * @return true if the projectExpectedStudyRegion was successfully deleted, false otherwise.
   */
  public void deleteProjectExpectedStudyRegion(long projectExpectedStudyRegionId);

  /**
   * This method validate if the projectExpectedStudyRegion identify with the given id exists in the system.
   * 
   * @param projectExpectedStudyRegionID is a projectExpectedStudyRegion identifier.
   * @return true if the projectExpectedStudyRegion exists, false otherwise.
   */
  public boolean existProjectExpectedStudyRegion(long projectExpectedStudyRegionID);

  /**
   * This method gets a projectExpectedStudyRegion object by a given projectExpectedStudyRegion identifier.
   * 
   * @param projectExpectedStudyRegionID is the projectExpectedStudyRegion identifier.
   * @return a ProjectExpectedStudyRegion object.
   */
  public ProjectExpectedStudyRegion find(long id);

  /**
   * This method gets a list of projectExpectedStudyRegion that are active
   * 
   * @return a list from ProjectExpectedStudyRegion null if no exist records
   */
  public List<ProjectExpectedStudyRegion> findAll();


  public List<ProjectExpectedStudyRegion> getProjectExpectedStudyRegionbyPhase(long expectedID, long phaseID);

  /**
   * This method saves the information of the given projectExpectedStudyRegion
   * 
   * @param projectExpectedStudyRegion - is the projectExpectedStudyRegion object with the new information to be
   *        added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the
   *         projectExpectedStudyRegion was
   *         updated
   *         or -1 is some error occurred.
   */
  public ProjectExpectedStudyRegion save(ProjectExpectedStudyRegion projectExpectedStudyRegion);
}
