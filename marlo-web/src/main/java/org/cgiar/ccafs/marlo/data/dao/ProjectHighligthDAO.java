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

import org.cgiar.ccafs.marlo.data.dao.mysql.ProjectHighligthMySQLDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectHighlight;

import java.util.List;

import com.google.inject.ImplementedBy;

@ImplementedBy(ProjectHighligthMySQLDAO.class)
public interface ProjectHighligthDAO {

  /**
   * This method removes a specific projectHighligth value from the database.
   * 
   * @param projectHighligthId is the projectHighligth identifier.
   * @return true if the projectHighligth was successfully deleted, false otherwise.
   */
  public void deleteProjectHighligth(long projectHighligthId);

  /**
   * This method validate if the projectHighligth identify with the given id exists in the system.
   * 
   * @param projectHighligthID is a projectHighligth identifier.
   * @return true if the projectHighligth exists, false otherwise.
   */
  public boolean existProjectHighligth(long projectHighligthID);

  /**
   * This method gets a projectHighligth object by a given projectHighligth identifier.
   * 
   * @param projectHighligthID is the projectHighligth identifier.
   * @return a ProjectHighlight object.
   */
  public ProjectHighlight find(long id);

  /**
   * This method gets a list of projectHighligth that are active
   * 
   * @return a list from ProjectHighlight null if no exist records
   */
  public List<ProjectHighlight> findAll();


  /**
   * This method saves the information of the given projectHighligth
   * 
   * @param projectHighlight - is the projectHighligth object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the projectHighligth was
   *         updated
   *         or -1 is some error occurred.
   */
  public ProjectHighlight save(ProjectHighlight projectHighlight);

  public ProjectHighlight save(ProjectHighlight projectHighlight, String section, List<String> relationsName);
}
