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

/**************
 * @author Diego Perez - CIAT/CCAFS
 **************/

package org.cgiar.ccafs.marlo.data.manager;

import org.cgiar.ccafs.marlo.data.model.ProjectInnovationCenter;

import java.util.List;

public interface ProjectInnovationCenterManager {


  /**
   * This method removes a specific projectInnovationCenter value from the database.
   * 
   * @param projectInnovationCrpId is the projectInnovationCenter identifier.
   * @return true if the projectInnovationCenter was successfully deleted, false otherwise.
   */
  public void deleteProjectInnovationCenter(long projectInnovationCenterId);


  /**
   * This method validate if the projectInnovationCenter identify with the given id exists in the system.
   * 
   * @param projectInnovationCenterID is a projectInnovationCenter identifier.
   * @return true if the projectInnovationCenter exists, false otherwise.
   */
  public boolean existProjectInnovationCenter(long projectInnovationCenterID);


  /**
   * This method gets a list of projectInnovationCenter that are active
   * 
   * @return a list from projectInnovationCenter null if no exist records
   */
  public List<ProjectInnovationCenter> findAll();


  List<ProjectInnovationCenter> findAllByInsitutionAndPhase(long institutionId, long phaseId);

  /**
   * This method gets a projectInnovationCenter object by a given projectInnovationCenter identifier.
   * 
   * @param projectInnovationCenterID is the projectInnovationCenter identifier.
   * @return a projectInnovationCenter object.
   */
  public ProjectInnovationCenter getProjectInnovationCenterById(long projectInnovationCenterID);


  public ProjectInnovationCenter getProjectInnovationCenterById(long innovationid, long globalUnitID, long phaseID);


  /**
   * This method saves the information of the given projectInnovationCenter
   * 
   * @param projectInnovationCenter - is the projectInnovationCrp object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the projectInnovationCrp was
   *         updated
   *         or -1 is some error occurred.
   */
  public ProjectInnovationCenter saveProjectInnovationCenter(ProjectInnovationCenter projectInnovationCenter);
}
