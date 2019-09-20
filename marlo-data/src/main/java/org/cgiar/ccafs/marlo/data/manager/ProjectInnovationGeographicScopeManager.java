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

import org.cgiar.ccafs.marlo.data.model.ProjectInnovationGeographicScope;

import java.util.List;


/**
 * @author CCAFS
 */

public interface ProjectInnovationGeographicScopeManager {


  /**
   * This method removes a specific projectInnovationGeographicScope value from the database.
   * 
   * @param projectInnovationGeographicScopeId is the projectInnovationGeographicScope identifier.
   * @return true if the projectInnovationGeographicScope was successfully deleted, false otherwise.
   */
  public void deleteProjectInnovationGeographicScope(long projectInnovationGeographicScopeId);


  /**
   * This method validate if the projectInnovationGeographicScope identify with the given id exists in the system.
   * 
   * @param projectInnovationGeographicScopeID is a projectInnovationGeographicScope identifier.
   * @return true if the projectInnovationGeographicScope exists, false otherwise.
   */
  public boolean existProjectInnovationGeographicScope(long projectInnovationGeographicScopeID);


  /**
   * This method gets a list of projectInnovationGeographicScope that are active
   * 
   * @return a list from ProjectInnovationGeographicScope null if no exist records
   */
  public List<ProjectInnovationGeographicScope> findAll();


  /**
   * This method get the information of the given projectInnovationGeographicScope
   * 
   * @param project_innovation_id identifier
   * @param rep_ind_geographic_scope_id identifier
   * @param id_phase id phase
   * @return a Object that contains information about innovation Geographic scope
   */
  public ProjectInnovationGeographicScope getProjectInnovationGeographicScope(long project_innovation_id,
    long rep_ind_geographic_scope_id, long id_phase);

  /**
   * This method gets a projectInnovationGeographicScope object by a given projectInnovationGeographicScope identifier.
   * 
   * @param projectInnovationGeographicScopeID is the projectInnovationGeographicScope identifier.
   * @return a ProjectInnovationGeographicScope object.
   */
  public ProjectInnovationGeographicScope
    getProjectInnovationGeographicScopeById(long projectInnovationGeographicScopeID);

  /**
   * This method saves the information of the given projectInnovationGeographicScope
   * 
   * @param projectInnovationGeographicScope - is the projectInnovationGeographicScope object with the new information
   *        to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the
   *         projectInnovationGeographicScope was
   *         updated
   *         or -1 is some error occurred.
   */
  public ProjectInnovationGeographicScope
    saveProjectInnovationGeographicScope(ProjectInnovationGeographicScope projectInnovationGeographicScope);


}
