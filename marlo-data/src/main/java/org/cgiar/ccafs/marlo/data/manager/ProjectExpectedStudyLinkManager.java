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

import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyLink;

import java.util.List;


/**
 * @author CCAFS
 */

public interface ProjectExpectedStudyLinkManager {


  /**
   * This method removes a specific projectExpectedStudyLink value from the database.
   * 
   * @param projectExpectedStudyLinkId is the projectExpectedStudyLink identifier.
   * @return true if the projectExpectedStudyLink was successfully deleted, false otherwise.
   */
  public void deleteProjectExpectedStudyLink(long projectExpectedStudyLinkId);


  /**
   * This method validate if the projectExpectedStudyLink identify with the given id exists in the system.
   * 
   * @param projectExpectedStudyLinkID is a projectExpectedStudyLink identifier.
   * @return true if the projectExpectedStudyLink exists, false otherwise.
   */
  public boolean existProjectExpectedStudyLink(long projectExpectedStudyLinkID);


  /**
   * This method gets a list of projectExpectedStudyLink that are active
   * 
   * @return a list from ProjectExpectedStudyLink null if no exist records
   */
  public List<ProjectExpectedStudyLink> findAll();


  /**
   * This method gets a projectExpectedStudyLink object by a given projectExpectedStudyLink identifier.
   * 
   * @param projectExpectedStudyLinkID is the projectExpectedStudyLink identifier.
   * @return a ProjectExpectedStudyLink object.
   */
  public ProjectExpectedStudyLink getProjectExpectedStudyLinkById(long projectExpectedStudyLinkID);

  /**
   * This method saves the information of the given projectExpectedStudyLink
   * 
   * @param projectExpectedStudyLink - is the projectExpectedStudyLink object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the projectExpectedStudyLink was
   *         updated
   *         or -1 is some error occurred.
   */
  public ProjectExpectedStudyLink saveProjectExpectedStudyLink(ProjectExpectedStudyLink projectExpectedStudyLink);


}
