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

import org.cgiar.ccafs.marlo.data.dao.mysql.CenterFundingSyncTypeMySQLDAO;
import org.cgiar.ccafs.marlo.data.model.CenterFundingSyncType;

import java.util.List;

import com.google.inject.ImplementedBy;

@ImplementedBy(CenterFundingSyncTypeMySQLDAO.class)
public interface CenterFundingSyncTypeDAO {

  /**
   * This method removes a specific centerFundingSyncType value from the database.
   * 
   * @param centerFundingSyncTypeId is the centerFundingSyncType identifier.
   * @return true if the centerFundingSyncType was successfully deleted, false otherwise.
   */
  public boolean deleteCenterFundingSyncType(long centerFundingSyncTypeId);

  /**
   * This method validate if the centerFundingSyncType identify with the given id exists in the system.
   * 
   * @param centerFundingSyncTypeID is a centerFundingSyncType identifier.
   * @return true if the centerFundingSyncType exists, false otherwise.
   */
  public boolean existCenterFundingSyncType(long centerFundingSyncTypeID);

  /**
   * This method gets a centerFundingSyncType object by a given centerFundingSyncType identifier.
   * 
   * @param centerFundingSyncTypeID is the centerFundingSyncType identifier.
   * @return a CenterFundingSyncType object.
   */
  public CenterFundingSyncType find(long id);

  /**
   * This method gets a list of centerFundingSyncType that are active
   * 
   * @return a list from CenterFundingSyncType null if no exist records
   */
  public List<CenterFundingSyncType> findAll();


  /**
   * This method saves the information of the given centerFundingSyncType
   * 
   * @param centerFundingSyncType - is the centerFundingSyncType object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the centerFundingSyncType was
   *         updated
   *         or -1 is some error occurred.
   */
  public long save(CenterFundingSyncType centerFundingSyncType);
}
