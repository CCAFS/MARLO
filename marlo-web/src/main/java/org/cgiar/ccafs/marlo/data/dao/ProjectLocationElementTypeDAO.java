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

import org.cgiar.ccafs.marlo.data.dao.mysql.ProjectLocationElementTypeMySQLDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectLocationElementType;

import java.util.List;

import com.google.inject.ImplementedBy;

@ImplementedBy(ProjectLocationElementTypeMySQLDAO.class)
public interface ProjectLocationElementTypeDAO {

  /**
   * This method removes a specific projectLocationElementType value from the database.
   * 
   * @param projectLocationElementTypeId is the projectLocationElementType identifier.
   * @return true if the projectLocationElementType was successfully deleted, false otherwise.
   */
  public void deleteProjectLocationElementType(long projectLocationElementTypeId);

  /**
   * This method validate if the projectLocationElementType identify with the given id exists in the system.
   * 
   * @param projectLocationElementTypeID is a projectLocationElementType identifier.
   * @return true if the projectLocationElementType exists, false otherwise.
   */
  public boolean existProjectLocationElementType(long projectLocationElementTypeID);

  /**
   * This method gets a projectLocationElementType object by a given projectLocationElementType identifier.
   * 
   * @param projectLocationElementTypeID is the projectLocationElementType identifier.
   * @return a ProjectLocationElementType object.
   */
  public ProjectLocationElementType find(long id);

  /**
   * This method gets a list of projectLocationElementType that are active
   * 
   * @return a list from ProjectLocationElementType null if no exist records
   */
  public List<ProjectLocationElementType> findAll();


  /**
   * This method gets a projectLocationElementType object by a given projectLocationElementType identifier.
   * 
   * @param projectId - is the project id
   * @param elementTypeId - is the loc element type id
   * @return a ProjectLocationElementType object.
   */
  public ProjectLocationElementType getByProjectAndElementType(long projectId, long elementTypeId);

  /**
   * This method saves the information of the given projectLocationElementType
   * 
   * @param projectLocationElementType - is the projectLocationElementType object with the new information to be
   *        added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the
   *         projectLocationElementType was
   *         updated
   *         or -1 is some error occurred.
   */
  public ProjectLocationElementType save(ProjectLocationElementType projectLocationElementType);
}
