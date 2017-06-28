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

import org.cgiar.ccafs.marlo.data.dao.impl.CenterDeliverableDAO;
import org.cgiar.ccafs.marlo.data.model.CenterDeliverable;

import java.util.List;

import com.google.inject.ImplementedBy;

@ImplementedBy(CenterDeliverableDAO.class)
public interface ICenterDeliverableDAO {

  /**
   * This method removes a specific deliverable value from the database.
   * 
   * @param deliverableId is the deliverable identifier.
   * @return true if the deliverable was successfully deleted, false otherwise.
   */
  public boolean deleteDeliverable(long deliverableId);

  /**
   * This method validate if the deliverable identify with the given id exists in the system.
   * 
   * @param deliverableID is a deliverable identifier.
   * @return true if the deliverable exists, false otherwise.
   */
  public boolean existDeliverable(long deliverableID);

  /**
   * This method gets a deliverable object by a given deliverable identifier.
   * 
   * @param deliverableID is the deliverable identifier.
   * @return a CenterDeliverable object.
   */
  public CenterDeliverable find(long id);

  /**
   * This method gets a list of deliverable that are active
   * 
   * @return a list from CenterDeliverable null if no exist records
   */
  public List<CenterDeliverable> findAll();


  /**
   * This method gets a list of deliverables belongs of the user
   * 
   * @param userId - the user id
   * @return List of Deliverables or null if the user is invalid or not have roles.
   */
  public List<CenterDeliverable> getDeliverablesByUserId(long userId);

  /**
   * This method saves the information of the given deliverable
   * 
   * @param deliverable - is the deliverable object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the deliverable was
   *         updated
   *         or -1 is some error occurred.
   */
  public long save(CenterDeliverable deliverable);

  /**
   * This method saves the information of the given deliverable
   * 
   * @param deliverable - is the deliverable object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the deliverbale was
   *         updated
   *         or -1 is some error occurred.
   */
  public long save(CenterDeliverable deliverable, String actionName, List<String> relationsName);
}
