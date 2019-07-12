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

import org.cgiar.ccafs.marlo.data.model.ProjectPolicyGeographicScope;

import java.util.List;


public interface ProjectPolicyGeographicScopeDAO {

  /**
   * This method removes a specific projectPolicyGeographicScope value from the database.
   * 
   * @param projectPolicyGeographicScopeId is the projectPolicyGeographicScope identifier.
   * @return true if the projectPolicyGeographicScope was successfully deleted, false otherwise.
   */
  public void deleteProjectPolicyGeographicScope(long projectPolicyGeographicScopeId);

  /**
   * This method validate if the projectPolicyGeographicScope identify with the given id exists in the system.
   * 
   * @param projectPolicyGeographicScopeID is a projectPolicyGeographicScope identifier.
   * @return true if the projectPolicyGeographicScope exists, false otherwise.
   */
  public boolean existProjectPolicyGeographicScope(long projectPolicyGeographicScopeID);

  /**
   * This method gets a projectPolicyGeographicScope object by a given projectPolicyGeographicScope identifier.
   * 
   * @param projectPolicyGeographicScopeID is the projectPolicyGeographicScope identifier.
   * @return a ProjectPolicyGeographicScope object.
   */
  public ProjectPolicyGeographicScope find(long id);

  /**
   * This method gets a list of projectPolicyGeographicScope that are active
   * 
   * @return a list from ProjectPolicyGeographicScope null if no exist records
   */
  public List<ProjectPolicyGeographicScope> findAll();


  /**
   * This method saves the information of the given projectPolicyGeographicScope
   * 
   * @param projectPolicyGeographicScope - is the projectPolicyGeographicScope object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the projectPolicyGeographicScope was
   *         updated
   *         or -1 is some error occurred.
   */
  public ProjectPolicyGeographicScope save(ProjectPolicyGeographicScope projectPolicyGeographicScope);
}
