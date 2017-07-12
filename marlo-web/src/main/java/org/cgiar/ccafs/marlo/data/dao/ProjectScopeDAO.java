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

import org.cgiar.ccafs.marlo.data.dao.mysql.ProjectScopeMySQLDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectScope;

import java.util.List;

import com.google.inject.ImplementedBy;

@ImplementedBy(ProjectScopeMySQLDAO.class)
public interface ProjectScopeDAO {

  /**
   * This method removes a specific projectScope value from the database.
   * 
   * @param projectScopeId is the projectScope identifier.
   * @return true if the projectScope was successfully deleted, false otherwise.
   */
  public boolean deleteProjectScope(long projectScopeId);

  /**
   * This method validate if the projectScope identify with the given id exists in the system.
   * 
   * @param projectScopeID is a projectScope identifier.
   * @return true if the projectScope exists, false otherwise.
   */
  public boolean existProjectScope(long projectScopeID);

  /**
   * This method gets a projectScope object by a given projectScope identifier.
   * 
   * @param projectScopeID is the projectScope identifier.
   * @return a ProjectScope object.
   */
  public ProjectScope find(long id);

  /**
   * This method gets a list of projectScope that are active
   * 
   * @return a list from ProjectScope null if no exist records
   */
  public List<ProjectScope> findAll();


  /**
   * This method saves the information of the given projectScope
   * 
   * @param projectScope - is the projectScope object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the projectScope was
   *         updated
   *         or -1 is some error occurred.
   */
  public long save(ProjectScope projectScope);
}
