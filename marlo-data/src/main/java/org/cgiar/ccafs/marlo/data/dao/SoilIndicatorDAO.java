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

import org.cgiar.ccafs.marlo.data.model.SoilIndicator;

import java.util.List;


public interface SoilIndicatorDAO {

  /**
   * This method removes a specific soilIndicator value from the database.
   * 
   * @param soilIndicatorId is the soilIndicator identifier.
   * @return true if the soilIndicator was successfully deleted, false otherwise.
   */
  public void deleteSoilIndicator(long soilIndicatorId);

  /**
   * This method validate if the soilIndicator identify with the given id exists in the system.
   * 
   * @param soilIndicatorID is a soilIndicator identifier.
   * @return true if the soilIndicator exists, false otherwise.
   */
  public boolean existSoilIndicator(long soilIndicatorID);

  /**
   * This method gets a soilIndicator object by a given soilIndicator identifier.
   * 
   * @param soilIndicatorID is the soilIndicator identifier.
   * @return a SoilIndicator object.
   */
  public SoilIndicator find(long id);

  /**
   * This method gets a list of soilIndicator that are active
   * 
   * @return a list from SoilIndicator null if no exist records
   */
  public List<SoilIndicator> findAll();


  /**
   * This method saves the information of the given soilIndicator
   * 
   * @param soilIndicator - is the soilIndicator object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the soilIndicator was
   *         updated
   *         or -1 is some error occurred.
   */
  public SoilIndicator save(SoilIndicator soilIndicator);
}
