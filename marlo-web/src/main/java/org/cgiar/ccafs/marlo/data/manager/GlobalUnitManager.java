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

import org.cgiar.ccafs.marlo.data.manager.impl.GlobalUnitManagerImpl;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;

import java.util.List;

import com.google.inject.ImplementedBy;

/**
 * @author Christian Garcia
 */
@ImplementedBy(GlobalUnitManagerImpl.class)
public interface GlobalUnitManager {


  /**
   * This method removes a specific globalUnit value from the database.
   * 
   * @param globalUnitId is the globalUnit identifier.
   * @return true if the globalUnit was successfully deleted, false otherwise.
   */
  public boolean deleteGlobalUnit(long globalUnitId);


  /**
   * This method validate if the globalUnit identify with the given id exists in the system.
   * 
   * @param globalUnitID is a globalUnit identifier.
   * @return true if the globalUnit exists, false otherwise.
   */
  public boolean existGlobalUnit(long globalUnitID);


  /**
   * This method gets a list of globalUnit that are active
   * 
   * @return a list from GlobalUnit null if no exist records
   */
  public List<GlobalUnit> findAll();


  /**
   * This method gets a globalUnit object by a given globalUnit identifier.
   * 
   * @param globalUnitID is the globalUnit identifier.
   * @return a GlobalUnit object.
   */
  public GlobalUnit getGlobalUnitById(long globalUnitID);

  /**
   * This method saves the information of the given globalUnit
   * 
   * @param globalUnit - is the globalUnit object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the globalUnit was
   *         updated
   *         or -1 is some error occurred.
   */
  public long saveGlobalUnit(GlobalUnit globalUnit);


}
