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

import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyImpactArea;

import java.util.List;


public interface ProjectExpectedStudyImpactAreaDAO {

  /**
   * This method removes a specific projectExpectedStudyImpactArea value from the database.
   * 
   * @param projectExpectedStudyImpactAreaId is the projectExpectedStudyImpactArea identifier.
   * @return true if the projectExpectedStudyImpactArea was successfully deleted, false otherwise.
   */
  public void deleteProjectExpectedStudyImpactArea(long projectExpectedStudyImpactAreaId);

  /**
   * This method validate if the projectExpectedStudyImpactArea identify with the given id exists in the system.
   * 
   * @param projectExpectedStudyImpactAreaID is a projectExpectedStudyImpactArea identifier.
   * @return true if the projectExpectedStudyImpactArea exists, false otherwise.
   */
  public boolean existProjectExpectedStudyImpactArea(long projectExpectedStudyImpactAreaID);

  /**
   * This method gets a projectExpectedStudyImpactArea object by a given projectExpectedStudyImpactArea identifier.
   * 
   * @param projectExpectedStudyImpactAreaID is the projectExpectedStudyImpactArea identifier.
   * @return a ProjectExpectedStudyImpactArea object.
   */
  public ProjectExpectedStudyImpactArea find(long id);

  /**
   * This method gets a list of projectExpectedStudyImpactArea that are active
   * 
   * @return a list from ProjectExpectedStudyImpactArea null if no exist records
   */
  public List<ProjectExpectedStudyImpactArea> findAll();


  /**
   * This method saves the information of the given projectExpectedStudyImpactArea
   * 
   * @param projectExpectedStudyImpactArea - is the projectExpectedStudyImpactArea object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the projectExpectedStudyImpactArea was
   *         updated
   *         or -1 is some error occurred.
   */
  public ProjectExpectedStudyImpactArea save(ProjectExpectedStudyImpactArea projectExpectedStudyImpactArea);
}
