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

import org.cgiar.ccafs.marlo.data.model.DeliverableClarisaUser;

import java.util.List;


public interface DeliverableClarisaUserDAO {

  /**
   * This method removes a specific deliverableClarisaUser value from the database.
   * 
   * @param deliverableClarisaUserId is the deliverableClarisaUser identifier.
   * @return true if the deliverableClarisaUser was successfully deleted, false otherwise.
   */
  public void deleteDeliverableClarisaUser(long deliverableClarisaUserId);

  /**
   * This method validate if the deliverableClarisaUser identify with the given id exists in the system.
   * 
   * @param deliverableClarisaUserID is a deliverableClarisaUser identifier.
   * @return true if the deliverableClarisaUser exists, false otherwise.
   */
  public boolean existDeliverableClarisaUser(long deliverableClarisaUserID);

  /**
   * This method gets a deliverableClarisaUser object by a given deliverableClarisaUser identifier.
   * 
   * @param deliverableClarisaUserID is the deliverableClarisaUser identifier.
   * @return a DeliverableClarisaUser object.
   */
  public DeliverableClarisaUser find(long id);

  /**
   * This method gets a list of deliverableClarisaUser that are active
   * 
   * @return a list from DeliverableClarisaUser null if no exist records
   */
  public List<DeliverableClarisaUser> findAll();


  /**
   * This method saves the information of the given deliverableClarisaUser
   * 
   * @param deliverableClarisaUser - is the deliverableClarisaUser object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the deliverableClarisaUser was
   *         updated
   *         or -1 is some error occurred.
   */
  public DeliverableClarisaUser save(DeliverableClarisaUser deliverableClarisaUser);
}
