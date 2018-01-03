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

import org.cgiar.ccafs.marlo.data.model.DeliverableInfo;

import java.util.List;

public interface DeliverableInfoDAO {

  /**
   * This method removes a specific deliverableInfo value from the database.
   * 
   * @param deliverableInfoId is the deliverableInfo identifier.
   * @return true if the deliverableInfo was successfully deleted, false otherwise.
   */
  public void deleteDeliverableInfo(long deliverableInfoId);

  /**
   * This method validate if the deliverableInfo identify with the given id exists in the system.
   * 
   * @param deliverableInfoID is a deliverableInfo identifier.
   * @return true if the deliverableInfo exists, false otherwise.
   */
  public boolean existDeliverableInfo(long deliverableInfoID);

  /**
   * This method gets a deliverableInfo object by a given deliverableInfo identifier.
   * 
   * @param deliverableInfoID is the deliverableInfo identifier.
   * @return a DeliverableInfo object.
   */
  public DeliverableInfo find(long id);

  /**
   * This method gets a list of deliverableInfo that are active
   * 
   * @return a list from DeliverableInfo null if no exist records
   */
  public List<DeliverableInfo> findAll();


  /**
   * This method saves the information of the given deliverableInfo
   * 
   * @param deliverableInfo - is the deliverableInfo object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the deliverableInfo was
   *         updated
   *         or -1 is some error occurred.
   */
  public DeliverableInfo save(DeliverableInfo deliverableInfo);
}
