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

import org.cgiar.ccafs.marlo.data.model.GenderType;

import java.util.List;


public interface GenderTypeDAO {

  /**
   * This method removes a specific genderType value from the database.
   * 
   * @param genderTypeId is the genderType identifier.
   * @return true if the genderType was successfully deleted, false otherwise.
   */
  public void deleteGenderType(long genderTypeId);

  /**
   * This method validate if the genderType identify with the given id exists in the system.
   * 
   * @param genderTypeID is a genderType identifier.
   * @return true if the genderType exists, false otherwise.
   */
  public boolean existGenderType(long genderTypeID);

  /**
   * This method gets a genderType object by a given genderType identifier.
   * 
   * @param genderTypeID is the genderType identifier.
   * @return a GenderType object.
   */
  public GenderType find(long id);

  /**
   * This method gets a list of genderType that are active
   * 
   * @return a list from GenderType null if no exist records
   */
  public List<GenderType> findAll();


  /**
   * This method saves the information of the given genderType
   * 
   * @param genderType - is the genderType object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the genderType was
   *         updated
   *         or -1 is some error occurred.
   */
  public GenderType save(GenderType genderType);
}
