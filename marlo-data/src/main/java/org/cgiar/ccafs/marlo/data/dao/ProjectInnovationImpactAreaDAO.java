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

import org.cgiar.ccafs.marlo.data.model.ProjectInnovationImpactArea;

import java.util.List;


public interface ProjectInnovationImpactAreaDAO {

  /**
   * This method removes a specific projectInnovationImpactArea value from the database.
   * 
   * @param projectInnovationImpactAreaId is the projectInnovationImpactArea identifier.
   * @return true if the projectInnovationImpactArea was successfully deleted, false otherwise.
   */
  public void deleteProjectInnovationImpactArea(long projectInnovationImpactAreaId);

  /**
   * This method validate if the projectInnovationImpactArea identify with the given id exists in the system.
   * 
   * @param projectInnovationImpactAreaID is a projectInnovationImpactArea identifier.
   * @return true if the projectInnovationImpactArea exists, false otherwise.
   */
  public boolean existProjectInnovationImpactArea(long projectInnovationImpactAreaID);

  /**
   * This method gets a projectInnovationImpactArea object by a given projectInnovationImpactArea identifier.
   * 
   * @param projectInnovationImpactAreaID is the projectInnovationImpactArea identifier.
   * @return a ProjectInnovationImpactArea object.
   */
  public ProjectInnovationImpactArea find(long id);

  /**
   * This method gets a list of projectInnovationImpactArea that are active
   * 
   * @return a list from ProjectInnovationImpactArea null if no exist records
   */
  public List<ProjectInnovationImpactArea> findAll();


  /**
   * This method saves the information of the given projectInnovationImpactArea
   * 
   * @param projectInnovationImpactArea - is the projectInnovationImpactArea object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the projectInnovationImpactArea was
   *         updated
   *         or -1 is some error occurred.
   */
  public ProjectInnovationImpactArea save(ProjectInnovationImpactArea projectInnovationImpactArea);
}
