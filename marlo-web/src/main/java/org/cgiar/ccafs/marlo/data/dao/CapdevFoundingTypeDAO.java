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

import org.cgiar.ccafs.marlo.data.dao.mysql.CapdevFoundingTypeMySQLDAO;
import org.cgiar.ccafs.marlo.data.model.CapdevFoundingType;

import java.util.List;

import com.google.inject.ImplementedBy;

@ImplementedBy(CapdevFoundingTypeMySQLDAO.class)
public interface CapdevFoundingTypeDAO {

  /**
   * This method removes a specific capdevFoundingType value from the database.
   * 
   * @param capdevFoundingTypeId is the capdevFoundingType identifier.
   */
  public void deleteCapdevFoundingType(long capdevFoundingTypeId);

  /**
   * This method validate if the capdevFoundingType identify with the given id exists in the system.
   * 
   * @param capdevFoundingTypeID is a capdevFoundingType identifier.
   * @return true if the capdevFoundingType exists, false otherwise.
   */
  public boolean existCapdevFoundingType(long capdevFoundingTypeID);

  /**
   * This method gets a capdevFoundingType object by a given capdevFoundingType identifier.
   * 
   * @param capdevFoundingTypeID is the capdevFoundingType identifier.
   * @return a CapdevFoundingType object.
   */
  public CapdevFoundingType find(long id);

  /**
   * This method gets a list of capdevFoundingType that are active
   * 
   * @return a list from CapdevFoundingType null if no exist records
   */
  public List<CapdevFoundingType> findAll();


  /**
   * This method saves the information of the given capdevFoundingType
   * 
   * @param capdevFoundingType - is the capdevFoundingType object with the new information to be added/updated.
   * @return CapdevFoundingType object.
   */
  public CapdevFoundingType save(CapdevFoundingType capdevFoundingType);
}
