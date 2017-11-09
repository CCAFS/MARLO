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

import org.cgiar.ccafs.marlo.data.dao.mysql.CenterNextuserTypeDAO;
import org.cgiar.ccafs.marlo.data.model.CenterNextuserType;

import java.util.List;

import com.google.inject.ImplementedBy;

@ImplementedBy(CenterNextuserTypeDAO.class)
public interface ICenterNextuserTypeDAO {

  /**
   * This method removes a specific nextuserType value from the database.
   * 
   * @param nextuserTypeId is the nextuserType identifier.
   * @return true if the nextuserType was successfully deleted, false otherwise.
   */
  public void deleteNextuserType(long nextuserTypeId);

  /**
   * This method validate if the nextuserType identify with the given id exists in the system.
   * 
   * @param nextuserTypeID is a nextuserType identifier.
   * @return true if the nextuserType exists, false otherwise.
   */
  public boolean existNextuserType(long nextuserTypeID);

  /**
   * This method gets a nextuserType object by a given nextuserType identifier.
   * 
   * @param nextuserTypeID is the nextuserType identifier.
   * @return a CenterNextuserType object.
   */
  public CenterNextuserType find(long id);

  /**
   * This method gets a list of nextuserType that are active
   * 
   * @return a list from CenterNextuserType null if no exist records
   */
  public List<CenterNextuserType> findAll();


  /**
   * This method gets a list of nextuserTypes belongs of the user
   * 
   * @param userId - the user id
   * @return List of NextuserTypes or null if the user is invalid or not have roles.
   */
  public List<CenterNextuserType> getNextuserTypesByUserId(long userId);

  /**
   * This method saves the information of the given nextuserType
   * 
   * @param nextuserType - is the nextuserType object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the nextuserType was
   *         updated
   *         or -1 is some error occurred.
   */
  public CenterNextuserType save(CenterNextuserType nextuserType);
}
