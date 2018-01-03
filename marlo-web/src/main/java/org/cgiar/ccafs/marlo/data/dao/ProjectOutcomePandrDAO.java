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

import org.cgiar.ccafs.marlo.data.model.ProjectOutcomePandr;

import java.util.List;


public interface ProjectOutcomePandrDAO {

  /**
   * This method removes a specific projectOutcomePandr value from the database.
   * 
   * @param projectOutcomePandrId is the projectOutcomePandr identifier.
   * @return true if the projectOutcomePandr was successfully deleted, false otherwise.
   */
  public void deleteProjectOutcomePandr(long projectOutcomePandrId);

  /**
   * This method validate if the projectOutcomePandr identify with the given id exists in the system.
   * 
   * @param projectOutcomePandrID is a projectOutcomePandr identifier.
   * @return true if the projectOutcomePandr exists, false otherwise.
   */
  public boolean existProjectOutcomePandr(long projectOutcomePandrID);

  /**
   * This method gets a projectOutcomePandr object by a given projectOutcomePandr identifier.
   * 
   * @param projectOutcomePandrID is the projectOutcomePandr identifier.
   * @return a ProjectOutcomePandr object.
   */
  public ProjectOutcomePandr find(long id);

  /**
   * This method gets a list of projectOutcomePandr that are active
   * 
   * @return a list from ProjectOutcomePandr null if no exist records
   */
  public List<ProjectOutcomePandr> findAll();


  /**
   * This method saves the information of the given projectOutcomePandr
   * 
   * @param projectOutcomePandr - is the projectOutcomePandr object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the projectOutcomePandr was
   *         updated
   *         or -1 is some error occurred.
   */
  public ProjectOutcomePandr save(ProjectOutcomePandr projectOutcomePandr);
}
