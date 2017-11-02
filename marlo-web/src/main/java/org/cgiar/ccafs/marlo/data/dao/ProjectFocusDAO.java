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

import org.cgiar.ccafs.marlo.data.dao.mysql.ProjectFocusMySQLDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectFocus;

import java.util.List;

import com.google.inject.ImplementedBy;

@ImplementedBy(ProjectFocusMySQLDAO.class)
public interface ProjectFocusDAO {

  /**
   * This method removes a specific projectFocus value from the database.
   * 
   * @param projectFocusId is the projectFocus identifier.
   * @return true if the projectFocus was successfully deleted, false otherwise.
   */
  public void deleteProjectFocus(long projectFocusId);

  /**
   * This method validate if the projectFocus identify with the given id exists in the system.
   * 
   * @param projectFocusID is a projectFocus identifier.
   * @return true if the projectFocus exists, false otherwise.
   */
  public boolean existProjectFocus(long projectFocusID);

  /**
   * This method gets a projectFocus object by a given projectFocus identifier.
   * 
   * @param projectFocusID is the projectFocus identifier.
   * @return a ProjectFocus object.
   */
  public ProjectFocus find(long id);

  /**
   * This method gets a list of projectFocus that are active
   * 
   * @return a list from ProjectFocus null if no exist records
   */
  public List<ProjectFocus> findAll();


  /**
   * This method saves the information of the given projectFocus
   * 
   * @param projectFocus - is the projectFocus object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the projectFocus was
   *         updated
   *         or -1 is some error occurred.
   */
  public ProjectFocus save(ProjectFocus projectFocus);
}
