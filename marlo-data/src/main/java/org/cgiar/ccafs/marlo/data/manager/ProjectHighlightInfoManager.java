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

import org.cgiar.ccafs.marlo.data.model.ProjectHighlightInfo;

import java.util.List;


/**
 * @author Christian Garcia
 */

public interface ProjectHighlightInfoManager {


  /**
   * This method removes a specific projectHighlightInfo value from the database.
   * 
   * @param projectHighlightInfoId is the projectHighlightInfo identifier.
   * @return true if the projectHighlightInfo was successfully deleted, false otherwise.
   */
  public void deleteProjectHighlightInfo(long projectHighlightInfoId);


  /**
   * This method validate if the projectHighlightInfo identify with the given id exists in the system.
   * 
   * @param projectHighlightInfoID is a projectHighlightInfo identifier.
   * @return true if the projectHighlightInfo exists, false otherwise.
   */
  public boolean existProjectHighlightInfo(long projectHighlightInfoID);


  /**
   * This method gets a list of projectHighlightInfo that are active
   * 
   * @return a list from ProjectHighlightInfo null if no exist records
   */
  public List<ProjectHighlightInfo> findAll();


  /**
   * This method gets a projectHighlightInfo object by a given projectHighlightInfo identifier.
   * 
   * @param projectHighlightInfoID is the projectHighlightInfo identifier.
   * @return a ProjectHighlightInfo object.
   */
  public ProjectHighlightInfo getProjectHighlightInfoById(long projectHighlightInfoID);

  /**
   * This method saves the information of the given projectHighlightInfo
   * 
   * @param projectHighlightInfo - is the projectHighlightInfo object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the projectHighlightInfo was
   *         updated
   *         or -1 is some error occurred.
   */
  public ProjectHighlightInfo saveProjectHighlightInfo(ProjectHighlightInfo projectHighlightInfo);


}
