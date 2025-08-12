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

import org.cgiar.ccafs.marlo.data.model.ProjectInnovationBundle;

import java.util.List;


public interface ProjectInnovationBundleDAO {

  /**
   * This method removes a specific projectInnovationBundle value from the database.
   * 
   * @param projectInnovationBundleId is the projectInnovationBundle identifier.
   * @return true if the projectInnovationBundle was successfully deleted, false otherwise.
   */
  public void deleteProjectInnovationBundle(long projectInnovationBundleId);

  /**
   * This method validate if the projectInnovationBundle identify with the given id exists in the system.
   * 
   * @param projectInnovationBundleID is a projectInnovationBundle identifier.
   * @return true if the projectInnovationBundle exists, false otherwise.
   */
  public boolean existProjectInnovationBundle(long projectInnovationBundleID);

  /**
   * This method gets a projectInnovationBundle object by a given projectInnovationBundle identifier.
   * 
   * @param projectInnovationBundleID is the projectInnovationBundle identifier.
   * @return a ProjectInnovationBundle object.
   */
  public ProjectInnovationBundle find(long id);

  /**
   * This method gets a list of projectInnovationBundle that are active
   * 
   * @return a list from ProjectInnovationBundle null if no exist records
   */
  public List<ProjectInnovationBundle> findAll();


  /**
   * This method gets a projectInnovationBundle object by a given projectInnovationBundle identifier.
   * 
   * @param projectInnovationBundleID is the projectInnovationBundle identifier.
   * @return a ProjectInnovationBundle object.
   */
  public List<ProjectInnovationBundle> getProjectInnovationBundleByInnovationAndPhase(long innovationID, long phaseID);

  /**
   * This method saves the information of the given projectInnovationBundle
   * 
   * @param projectInnovationBundle - is the projectInnovationBundle object with the new information to be
   *        added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the projectInnovationBundle
   *         was
   *         updated
   *         or -1 is some error occurred.
   */
  public ProjectInnovationBundle save(ProjectInnovationBundle projectInnovationBundle);
}
