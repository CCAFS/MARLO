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

import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationInfo;

import java.util.List;


/**
 * @author Christian Garcia
 */

public interface ProjectInnovationInfoManager {


  /**
   * This method removes a specific projectInnovationInfo value from the database.
   * 
   * @param projectInnovationInfoId is the projectInnovationInfo identifier.
   * @return true if the projectInnovationInfo was successfully deleted, false otherwise.
   */
  public void deleteProjectInnovationInfo(long projectInnovationInfoId);


  /**
   * This method validate if the projectInnovationInfo identify with the given id exists in the system.
   * 
   * @param projectInnovationInfoID is a projectInnovationInfo identifier.
   * @return true if the projectInnovationInfo exists, false otherwise.
   */
  public boolean existProjectInnovationInfo(long projectInnovationInfoID);


  /**
   * This method gets a list of projectInnovationInfo that are active
   * 
   * @return a list from ProjectInnovationInfo null if no exist records
   */
  public List<ProjectInnovationInfo> findAll();


  /**
   * This method gets a projectInnovationInfo object by a given projectInnovationInfo identifier.
   * 
   * @param projectInnovationInfoID is the projectInnovationInfo identifier.
   * @return a ProjectInnovationInfo object.
   */
  public ProjectInnovationInfo getProjectInnovationInfoById(long projectInnovationInfoID);

  /**
   * This method gets a list of ProjectInnovationInfo that are active by a given phase
   * 
   * @return a list from ProjectInnovationInfo null if no exist records
   */
  public List<ProjectInnovationInfo> getProjectInnovationInfoByPhase(Phase phase);

  /**
   * This method saves the information of the given projectInnovationInfo
   * 
   * @param projectInnovationInfo - is the projectInnovationInfo object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the projectInnovationInfo
   *         was
   *         updated
   *         or -1 is some error occurred.
   */
  public ProjectInnovationInfo saveProjectInnovationInfo(ProjectInnovationInfo projectInnovationInfo);

}
