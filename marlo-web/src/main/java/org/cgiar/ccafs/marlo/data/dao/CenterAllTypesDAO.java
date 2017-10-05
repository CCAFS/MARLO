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

import org.cgiar.ccafs.marlo.data.dao.mysql.CenterAllTypesMySQLDAO;
import org.cgiar.ccafs.marlo.data.model.CenterAllTypes;

import java.util.List;

import com.google.inject.ImplementedBy;

@ImplementedBy(CenterAllTypesMySQLDAO.class)
public interface CenterAllTypesDAO {

  /**
   * This method removes a specific centerAllTypes value from the database.
   * 
   * @param centerAllTypesId is the centerAllTypes identifier.
   * @return true if the centerAllTypes was successfully deleted, false otherwise.
   */
  public boolean deleteCenterAllTypes(long centerAllTypesId);

  /**
   * This method validate if the centerAllTypes identify with the given id exists in the system.
   * 
   * @param centerAllTypesID is a centerAllTypes identifier.
   * @return true if the centerAllTypes exists, false otherwise.
   */
  public boolean existCenterAllTypes(long centerAllTypesID);

  /**
   * This method gets a centerAllTypes object by a given centerAllTypes identifier.
   * 
   * @param centerAllTypesID is the centerAllTypes identifier.
   * @return a CenterAllTypes object.
   */
  public CenterAllTypes find(long id);

  /**
   * This method gets a list of centerAllTypes that are active
   * 
   * @return a list from CenterAllTypes null if no exist records
   */
  public List<CenterAllTypes> findAll();


  /**
   * This method saves the information of the given centerAllTypes
   * 
   * @param centerAllTypes - is the centerAllTypes object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the centerAllTypes was
   *         updated
   *         or -1 is some error occurred.
   */
  public long save(CenterAllTypes centerAllTypes);
}
