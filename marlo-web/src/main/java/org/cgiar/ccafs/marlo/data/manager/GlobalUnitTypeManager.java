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
package org.cgiar.ccafs.marlo.data.manager;

import org.cgiar.ccafs.marlo.data.manager.impl.GlobalUnitTypeManagerImpl;
import org.cgiar.ccafs.marlo.data.model.GlobalUnitType;

import java.util.List;

import com.google.inject.ImplementedBy;

/**
 * @author Christian Garcia
 */
@ImplementedBy(GlobalUnitTypeManagerImpl.class)
public interface GlobalUnitTypeManager {


  /**
   * This method removes a specific globalUnitType value from the database.
   * 
   * @param globalUnitTypeId is the globalUnitType identifier.
   * @return true if the globalUnitType was successfully deleted, false otherwise.
   */
  public boolean deleteGlobalUnitType(long globalUnitTypeId);


  /**
   * This method validate if the globalUnitType identify with the given id exists in the system.
   * 
   * @param globalUnitTypeID is a globalUnitType identifier.
   * @return true if the globalUnitType exists, false otherwise.
   */
  public boolean existGlobalUnitType(long globalUnitTypeID);


  /**
   * This method gets a list of globalUnitType that are active
   * 
   * @return a list from GlobalUnitType null if no exist records
   */
  public List<GlobalUnitType> findAll();


  /**
   * This method gets a globalUnitType object by a given globalUnitType identifier.
   * 
   * @param globalUnitTypeID is the globalUnitType identifier.
   * @return a GlobalUnitType object.
   */
  public GlobalUnitType getGlobalUnitTypeById(long globalUnitTypeID);

  /**
   * This method saves the information of the given globalUnitType
   * 
   * @param globalUnitType - is the globalUnitType object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the globalUnitType was
   *         updated
   *         or -1 is some error occurred.
   */
  public long saveGlobalUnitType(GlobalUnitType globalUnitType);


}
