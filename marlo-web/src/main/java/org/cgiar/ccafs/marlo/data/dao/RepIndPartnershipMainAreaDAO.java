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

import org.cgiar.ccafs.marlo.data.model.RepIndPartnershipMainArea;

import java.util.List;


public interface RepIndPartnershipMainAreaDAO {

  /**
   * This method removes a specific repIndPartnershipMainArea value from the database.
   * 
   * @param repIndPartnershipMainAreaId is the repIndPartnershipMainArea identifier.
   * @return true if the repIndPartnershipMainArea was successfully deleted, false otherwise.
   */
  public void deleteRepIndPartnershipMainArea(long repIndPartnershipMainAreaId);

  /**
   * This method validate if the repIndPartnershipMainArea identify with the given id exists in the system.
   * 
   * @param repIndPartnershipMainAreaID is a repIndPartnershipMainArea identifier.
   * @return true if the repIndPartnershipMainArea exists, false otherwise.
   */
  public boolean existRepIndPartnershipMainArea(long repIndPartnershipMainAreaID);

  /**
   * This method gets a repIndPartnershipMainArea object by a given repIndPartnershipMainArea identifier.
   * 
   * @param repIndPartnershipMainAreaID is the repIndPartnershipMainArea identifier.
   * @return a RepIndPartnershipMainArea object.
   */
  public RepIndPartnershipMainArea find(long id);

  /**
   * This method gets a list of repIndPartnershipMainArea that are active
   * 
   * @return a list from RepIndPartnershipMainArea null if no exist records
   */
  public List<RepIndPartnershipMainArea> findAll();


  /**
   * This method saves the information of the given repIndPartnershipMainArea
   * 
   * @param repIndPartnershipMainArea - is the repIndPartnershipMainArea object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the repIndPartnershipMainArea was
   *         updated
   *         or -1 is some error occurred.
   */
  public RepIndPartnershipMainArea save(RepIndPartnershipMainArea repIndPartnershipMainArea);
}
