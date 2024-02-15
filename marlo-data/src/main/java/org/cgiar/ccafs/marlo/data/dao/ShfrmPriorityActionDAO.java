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

import org.cgiar.ccafs.marlo.data.model.ShfrmPriorityAction;

import java.util.List;


public interface ShfrmPriorityActionDAO {

  /**
   * This method removes a specific shfrmPriorityAction value from the database.
   * 
   * @param shfrmPriorityActionId is the shfrmPriorityAction identifier.
   * @return true if the shfrmPriorityAction was successfully deleted, false otherwise.
   */
  public void deleteShfrmPriorityAction(long shfrmPriorityActionId);

  /**
   * This method validate if the shfrmPriorityAction identify with the given id exists in the system.
   * 
   * @param shfrmPriorityActionID is a shfrmPriorityAction identifier.
   * @return true if the shfrmPriorityAction exists, false otherwise.
   */
  public boolean existShfrmPriorityAction(long shfrmPriorityActionID);

  /**
   * This method gets a shfrmPriorityAction object by a given shfrmPriorityAction identifier.
   * 
   * @param shfrmPriorityActionID is the shfrmPriorityAction identifier.
   * @return a ShfrmPriorityAction object.
   */
  public ShfrmPriorityAction find(long id);

  /**
   * This method gets a list of shfrmPriorityAction that are active
   * 
   * @return a list from ShfrmPriorityAction null if no exist records
   */
  public List<ShfrmPriorityAction> findAll();


  /**
   * This method saves the information of the given shfrmPriorityAction
   * 
   * @param shfrmPriorityAction - is the shfrmPriorityAction object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the shfrmPriorityAction was
   *         updated
   *         or -1 is some error occurred.
   */
  public ShfrmPriorityAction save(ShfrmPriorityAction shfrmPriorityAction);
}
