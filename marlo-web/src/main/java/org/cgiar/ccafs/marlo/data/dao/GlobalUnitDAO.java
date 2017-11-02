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

import org.cgiar.ccafs.marlo.data.dao.mysql.GlobalUnitMySQLDAO;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;

import java.util.List;

import com.google.inject.ImplementedBy;

@ImplementedBy(GlobalUnitMySQLDAO.class)
public interface GlobalUnitDAO {

  /**
   * This method removes a specific globalUnit value from the database.
   * 
   * @param globalUnitId is the globalUnit identifier.
   */
  public void deleteGlobalUnit(long globalUnitId);

  /**
   * This method validate if the globalUnit identify with the given id exists in the system.
   * 
   * @param globalUnitID is a globalUnit identifier.
   * @return true if the globalUnit exists, false otherwise.
   */
  public boolean existGlobalUnit(long globalUnitID);

  /**
   * This method gets a globalUnit object by a given globalUnit identifier.
   * 
   * @param globalUnitID is the globalUnit identifier.
   * @return a GlobalUnit object.
   */
  public GlobalUnit find(long id);

  /**
   * This method gets a list of globalUnit that are active
   * 
   * @return a list from GlobalUnit null if no exist records
   */
  public List<GlobalUnit> findAll();


  /**
   * This method saves the information of the given globalUnit
   * 
   * @param globalUnit - is the globalUnit object with the new information to be added/updated.
   * @return GlobalUnit object created or updated
   */
  public GlobalUnit save(GlobalUnit globalUnit);
}
