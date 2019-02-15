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

import org.cgiar.ccafs.marlo.data.model.ProjectInnovationRegion;

import java.util.List;


public interface ProjectInnovationRegionDAO {

  /**
   * This method removes a specific projectInnovationRegion value from the database.
   * 
   * @param projectInnovationRegionId is the projectInnovationRegion identifier.
   * @return true if the projectInnovationRegion was successfully deleted, false otherwise.
   */
  public void deleteProjectInnovationRegion(long projectInnovationRegionId);

  /**
   * This method validate if the projectInnovationRegion identify with the given id exists in the system.
   * 
   * @param projectInnovationRegionID is a projectInnovationRegion identifier.
   * @return true if the projectInnovationRegion exists, false otherwise.
   */
  public boolean existProjectInnovationRegion(long projectInnovationRegionID);

  /**
   * This method gets a projectInnovationRegion object by a given projectInnovationRegion identifier.
   * 
   * @param projectInnovationRegionID is the projectInnovationRegion identifier.
   * @return a ProjectInnovationRegion object.
   */
  public ProjectInnovationRegion find(long id);

  /**
   * This method gets a list of projectInnovationRegion that are active
   * 
   * @return a list from ProjectInnovationRegion null if no exist records
   */
  public List<ProjectInnovationRegion> findAll();


  List<ProjectInnovationRegion> getInnovationRegionbyPhase(long innovationID, long phaseID);

  /**
   * This method saves the information of the given projectInnovationRegion
   * 
   * @param projectInnovationRegion - is the projectInnovationRegion object with the new information to be
   *        added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the projectInnovationRegion
   *         was
   *         updated
   *         or -1 is some error occurred.
   */
  public ProjectInnovationRegion save(ProjectInnovationRegion projectInnovationRegion);
}
