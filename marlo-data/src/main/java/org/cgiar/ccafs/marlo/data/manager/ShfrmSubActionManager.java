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

import org.cgiar.ccafs.marlo.data.model.ShfrmSubAction;

import java.util.List;


/**
 * @author CCAFS
 */

public interface ShfrmSubActionManager {


  /**
   * This method removes a specific shfrmSubAction value from the database.
   * 
   * @param shfrmSubActionId is the shfrmSubAction identifier.
   * @return true if the shfrmSubAction was successfully deleted, false otherwise.
   */
  public void deleteShfrmSubAction(long shfrmSubActionId);


  /**
   * This method validate if the shfrmSubAction identify with the given id exists in the system.
   * 
   * @param shfrmSubActionID is a shfrmSubAction identifier.
   * @return true if the shfrmSubAction exists, false otherwise.
   */
  public boolean existShfrmSubAction(long shfrmSubActionID);


  /**
   * This method gets a list of shfrmSubAction that are active
   * 
   * @return a list from ShfrmSubAction null if no exist records
   */
  public List<ShfrmSubAction> findAll();


  /**
   * This method gets a shfrmSubAction object by a given shfrmSubAction identifier.
   * 
   * @param shfrmSubActionID is the shfrmSubAction identifier.
   * @return a ShfrmSubAction object.
   */
  public ShfrmSubAction getShfrmSubActionById(long shfrmSubActionID);

  /**
   * This method saves the information of the given shfrmSubAction
   * 
   * @param shfrmSubAction - is the shfrmSubAction object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the shfrmSubAction was
   *         updated
   *         or -1 is some error occurred.
   */
  public ShfrmSubAction saveShfrmSubAction(ShfrmSubAction shfrmSubAction);


}
