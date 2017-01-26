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

import org.cgiar.ccafs.marlo.data.dao.mysql.DeliverableCrpMySQLDAO;
import org.cgiar.ccafs.marlo.data.model.DeliverableCrp;

import java.util.List;

import com.google.inject.ImplementedBy;

@ImplementedBy(DeliverableCrpMySQLDAO.class)
public interface DeliverableCrpDAO {

  /**
   * This method removes a specific deliverableCrp value from the database.
   * 
   * @param deliverableCrpId is the deliverableCrp identifier.
   * @return true if the deliverableCrp was successfully deleted, false otherwise.
   */
  public boolean deleteDeliverableCrp(long deliverableCrpId);

  /**
   * This method validate if the deliverableCrp identify with the given id exists in the system.
   * 
   * @param deliverableCrpID is a deliverableCrp identifier.
   * @return true if the deliverableCrp exists, false otherwise.
   */
  public boolean existDeliverableCrp(long deliverableCrpID);

  /**
   * This method gets a deliverableCrp object by a given deliverableCrp identifier.
   * 
   * @param deliverableCrpID is the deliverableCrp identifier.
   * @return a DeliverableCrp object.
   */
  public DeliverableCrp find(long id);

  /**
   * This method gets a list of deliverableCrp that are active
   * 
   * @return a list from DeliverableCrp null if no exist records
   */
  public List<DeliverableCrp> findAll();


  /**
   * This method saves the information of the given deliverableCrp
   * 
   * @param deliverableCrp - is the deliverableCrp object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the deliverableCrp was
   *         updated
   *         or -1 is some error occurred.
   */
  public long save(DeliverableCrp deliverableCrp);
}
