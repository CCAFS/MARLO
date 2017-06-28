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

import org.cgiar.ccafs.marlo.data.dao.impl.CenterProjectStatusDAO;
import org.cgiar.ccafs.marlo.data.model.CenterProjectStatus;

import java.util.List;

import com.google.inject.ImplementedBy;

@ImplementedBy(CenterProjectStatusDAO.class)
public interface ICenterProjectStatusDAO {

  /**
   * This method removes a specific projectStatus value from the database.
   * 
   * @param projectStatusId is the projectStatus identifier.
   * @return true if the projectStatus was successfully deleted, false otherwise.
   */
  public boolean deleteProjectStatus(long projectStatusId);

  /**
   * This method validate if the projectStatus identify with the given id exists in the system.
   * 
   * @param projectStatusID is a projectStatus identifier.
   * @return true if the projectStatus exists, false otherwise.
   */
  public boolean existProjectStatus(long projectStatusID);

  /**
   * This method gets a projectStatus object by a given projectStatus identifier.
   * 
   * @param projectStatusID is the projectStatus identifier.
   * @return a CenterProjectStatus object.
   */
  public CenterProjectStatus find(long id);

  /**
   * This method gets a list of projectStatus that are active
   * 
   * @return a list from CenterProjectStatus null if no exist records
   */
  public List<CenterProjectStatus> findAll();


  /**
   * This method gets a list of projectStatuss belongs of the user
   * 
   * @param userId - the user id
   * @return List of ProjectStatuss or null if the user is invalid or not have roles.
   */
  public List<CenterProjectStatus> getProjectStatussByUserId(long userId);

  /**
   * This method saves the information of the given projectStatus
   * 
   * @param projectStatus - is the projectStatus object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the projectStatus was
   *         updated
   *         or -1 is some error occurred.
   */
  public long save(CenterProjectStatus projectStatus);
}
