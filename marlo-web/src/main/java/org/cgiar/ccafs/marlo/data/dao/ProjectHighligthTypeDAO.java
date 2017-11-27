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

import org.cgiar.ccafs.marlo.data.dao.mysql.ProjectHighligthTypeMySQLDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectHighlightType;

import java.util.List;

import com.google.inject.ImplementedBy;

@ImplementedBy(ProjectHighligthTypeMySQLDAO.class)
public interface ProjectHighligthTypeDAO {

  /**
   * This method removes a specific projectHighligthType value from the database.
   * 
   * @param projectHighligthTypeId is the projectHighligthType identifier.
   * @return true if the projectHighligthType was successfully deleted, false otherwise.
   */
  public void deleteProjectHighligthType(long projectHighligthTypeId);

  /**
   * This method validate if the projectHighligthType identify with the given id exists in the system.
   * 
   * @param projectHighligthTypeID is a projectHighligthType identifier.
   * @return true if the projectHighligthType exists, false otherwise.
   */
  public boolean existProjectHighligthType(long projectHighligthTypeID);

  /**
   * This method gets a projectHighligthType object by a given projectHighligthType identifier.
   * 
   * @param projectHighligthTypeID is the projectHighligthType identifier.
   * @return a ProjectHighlightType object.
   */
  public ProjectHighlightType find(long id);

  /**
   * This method gets a list of projectHighligthType that are active
   * 
   * @return a list from ProjectHighlightType null if no exist records
   */
  public List<ProjectHighlightType> findAll();


  /**
   * This method saves the information of the given projectHighligthType
   * 
   * @param projectHighlightType - is the projectHighligthType object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the projectHighligthType was
   *         updated
   *         or -1 is some error occurred.
   */
  public ProjectHighlightType save(ProjectHighlightType projectHighlightType);
}
