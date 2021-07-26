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

import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyNexus;

import java.util.List;


/**
 * @author CCAFS
 */

public interface ProjectExpectedStudyNexusManager {


  /**
   * This method removes a specific projectExpectedStudyNexus value from the database.
   * 
   * @param projectExpectedStudyNexusId is the projectExpectedStudyNexus identifier.
   * @return true if the projectExpectedStudyNexus was successfully deleted, false otherwise.
   */
  public void deleteProjectExpectedStudyNexus(long projectExpectedStudyNexusId);


  /**
   * This method validate if the projectExpectedStudyNexus identify with the given id exists in the system.
   * 
   * @param projectExpectedStudyNexusID is a projectExpectedStudyNexus identifier.
   * @return true if the projectExpectedStudyNexus exists, false otherwise.
   */
  public boolean existProjectExpectedStudyNexus(long projectExpectedStudyNexusID);


  /**
   * This method gets a list of projectExpectedStudyNexus that are active
   * 
   * @return a list from ProjectExpectedStudyNexus null if no exist records
   */
  public List<ProjectExpectedStudyNexus> findAll();


  /**
   * This method gets a list of projectExpectedStudyNexus by a given projectExpectedStudy identifier.
   * 
   * @param studyId is the projectExpectedStudy identifier.
   * @return a list of projectExpectedStudyNexus objects.
   */
  public List<ProjectExpectedStudyNexus> getAllStudyNexussByStudy(Long studyId);

  /**
   * This method gets a projectExpectedStudyNexus object by a given projectExpectedStudyNexus identifier.
   * 
   * @param projectExpectedStudyNexusID is the projectExpectedStudyNexus identifier.
   * @return a ProjectExpectedStudyNexus object.
   */
  public ProjectExpectedStudyNexus getProjectExpectedStudyNexusById(long projectExpectedStudyNexusID);

  /**
   * This method saves the information of the given projectExpectedStudyNexus
   * 
   * @param projectExpectedStudyNexus - is the projectExpectedStudyNexus object with the new information to be
   *        added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the
   *         projectExpectedStudyNexus was
   *         updated
   *         or -1 is some error occurred.
   */
  public ProjectExpectedStudyNexus saveProjectExpectedStudyNexus(ProjectExpectedStudyNexus projectExpectedStudyNexus);
}
