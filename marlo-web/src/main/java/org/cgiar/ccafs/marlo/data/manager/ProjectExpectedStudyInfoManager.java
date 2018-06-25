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
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyInfo;

import java.util.List;


/**
 * @author CCAFS
 */

public interface ProjectExpectedStudyInfoManager {


  /**
   * This method removes a specific projectExpectedStudyInfo value from the database.
   * 
   * @param projectExpectedStudyInfoId is the projectExpectedStudyInfo identifier.
   * @return true if the projectExpectedStudyInfo was successfully deleted, false otherwise.
   */
  public void deleteProjectExpectedStudyInfo(long projectExpectedStudyInfoId);


  /**
   * This method validate if the projectExpectedStudyInfo identify with the given id exists in the system.
   * 
   * @param projectExpectedStudyInfoID is a projectExpectedStudyInfo identifier.
   * @return true if the projectExpectedStudyInfo exists, false otherwise.
   */
  public boolean existProjectExpectedStudyInfo(long projectExpectedStudyInfoID);


  /**
   * This method gets a list of projectExpectedStudyInfo that are active
   * 
   * @return a list from ProjectExpectedStudyInfo null if no exist records
   */
  public List<ProjectExpectedStudyInfo> findAll();


  /**
   * This method gets a projectExpectedStudyInfo object by a given projectExpectedStudyInfo identifier.
   * 
   * @param projectExpectedStudyInfoID is the projectExpectedStudyInfo identifier.
   * @return a ProjectExpectedStudyInfo object.
   */
  public ProjectExpectedStudyInfo getProjectExpectedStudyInfoById(long projectExpectedStudyInfoID);

  /**
   * This method gets a list of projectExpectedStudyInfo that are active given a phase identifier
   * 
   * @return a list from ProjectExpectedStudyInfo null if no exist records
   */
  public List<ProjectExpectedStudyInfo> getProjectExpectedStudyInfoByPhase(Phase phase);

  /**
   * This method saves the information of the given projectExpectedStudyInfo
   * 
   * @param projectExpectedStudyInfo - is the projectExpectedStudyInfo object with the new information to be
   *        added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the projectExpectedStudyInfo
   *         was
   *         updated
   *         or -1 is some error occurred.
   */
  public ProjectExpectedStudyInfo saveProjectExpectedStudyInfo(ProjectExpectedStudyInfo projectExpectedStudyInfo);
}
