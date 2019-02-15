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

import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyGeographicScope;

import java.util.List;


public interface ProjectExpectedStudyGeographicScopeDAO {

  /**
   * This method removes a specific projectExpectedStudyGeographicScope value from the database.
   * 
   * @param projectExpectedStudyGeographicScopeId is the projectExpectedStudyGeographicScope identifier.
   * @return true if the projectExpectedStudyGeographicScope was successfully deleted, false otherwise.
   */
  public void deleteProjectExpectedStudyGeographicScope(long projectExpectedStudyGeographicScopeId);

  /**
   * This method validate if the projectExpectedStudyGeographicScope identify with the given id exists in the system.
   * 
   * @param projectExpectedStudyGeographicScopeID is a projectExpectedStudyGeographicScope identifier.
   * @return true if the projectExpectedStudyGeographicScope exists, false otherwise.
   */
  public boolean existProjectExpectedStudyGeographicScope(long projectExpectedStudyGeographicScopeID);

  /**
   * This method gets a projectExpectedStudyGeographicScope object by a given projectExpectedStudyGeographicScope identifier.
   * 
   * @param projectExpectedStudyGeographicScopeID is the projectExpectedStudyGeographicScope identifier.
   * @return a ProjectExpectedStudyGeographicScope object.
   */
  public ProjectExpectedStudyGeographicScope find(long id);

  /**
   * This method gets a list of projectExpectedStudyGeographicScope that are active
   * 
   * @return a list from ProjectExpectedStudyGeographicScope null if no exist records
   */
  public List<ProjectExpectedStudyGeographicScope> findAll();


  /**
   * This method saves the information of the given projectExpectedStudyGeographicScope
   * 
   * @param projectExpectedStudyGeographicScope - is the projectExpectedStudyGeographicScope object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the projectExpectedStudyGeographicScope was
   *         updated
   *         or -1 is some error occurred.
   */
  public ProjectExpectedStudyGeographicScope save(ProjectExpectedStudyGeographicScope projectExpectedStudyGeographicScope);
}
