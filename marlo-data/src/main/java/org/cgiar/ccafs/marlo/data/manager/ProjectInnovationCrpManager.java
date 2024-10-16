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

import org.cgiar.ccafs.marlo.data.model.ProjectInnovationCrp;

import java.util.List;


/**
 * @author CCAFS
 */

public interface ProjectInnovationCrpManager {


  /**
   * This method removes a specific projectInnovationCrp value from the database.
   * 
   * @param projectInnovationCrpId is the projectInnovationCrp identifier.
   * @return true if the projectInnovationCrp was successfully deleted, false otherwise.
   */
  public void deleteProjectInnovationCrp(long projectInnovationCrpId);


  /**
   * This method validate if the projectInnovationCrp identify with the given id exists in the system.
   * 
   * @param projectInnovationCrpID is a projectInnovationCrp identifier.
   * @return true if the projectInnovationCrp exists, false otherwise.
   */
  public boolean existProjectInnovationCrp(long projectInnovationCrpID);


  /**
   * This method gets a list of projectInnovationCrp that are active
   * 
   * @return a list from ProjectInnovationCrp null if no exist records
   */
  public List<ProjectInnovationCrp> findAll();


  /**
   * This method gets a projectInnovationCrp object by a given projectInnovationCrp identifier.
   * 
   * @param projectInnovationCrpID is the projectInnovationCrp identifier.
   * @return a ProjectInnovationCrp object.
   */
  public ProjectInnovationCrp getProjectInnovationCrpById(long projectInnovationCrpID);

  public ProjectInnovationCrp getProjectInnovationCrpById(long innovationid, long globalUnitID, long phaseID);


  /**
   * This method saves the information of the given projectInnovationCrp
   * 
   * @param projectInnovationCrp - is the projectInnovationCrp object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the projectInnovationCrp was
   *         updated
   *         or -1 is some error occurred.
   */
  public ProjectInnovationCrp saveProjectInnovationCrp(ProjectInnovationCrp projectInnovationCrp);


}
