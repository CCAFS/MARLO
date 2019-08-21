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

import org.cgiar.ccafs.marlo.data.model.DeliverableUserPartnershipPerson;

import java.util.List;


public interface DeliverableUserPartnershipPersonDAO {

  /**
   * This method removes a specific deliverableUserPartnershipPerson value from the database.
   * 
   * @param deliverableUserPartnershipPersonId is the deliverableUserPartnershipPerson identifier.
   * @return true if the deliverableUserPartnershipPerson was successfully deleted, false otherwise.
   */
  public void deleteDeliverableUserPartnershipPerson(long deliverableUserPartnershipPersonId);

  /**
   * This method validate if the deliverableUserPartnershipPerson identify with the given id exists in the system.
   * 
   * @param deliverableUserPartnershipPersonID is a deliverableUserPartnershipPerson identifier.
   * @return true if the deliverableUserPartnershipPerson exists, false otherwise.
   */
  public boolean existDeliverableUserPartnershipPerson(long deliverableUserPartnershipPersonID);

  /**
   * This method gets a deliverableUserPartnershipPerson object by a given deliverableUserPartnershipPerson identifier.
   * 
   * @param deliverableUserPartnershipPersonID is the deliverableUserPartnershipPerson identifier.
   * @return a DeliverableUserPartnershipPerson object.
   */
  public DeliverableUserPartnershipPerson find(long id);

  /**
   * This method gets a list of deliverableUserPartnershipPerson that are active
   * 
   * @return a list from DeliverableUserPartnershipPerson null if no exist records
   */
  public List<DeliverableUserPartnershipPerson> findAll();


  /**
   * This method saves the information of the given deliverableUserPartnershipPerson
   * 
   * @param deliverableUserPartnershipPerson - is the deliverableUserPartnershipPerson object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the deliverableUserPartnershipPerson was
   *         updated
   *         or -1 is some error occurred.
   */
  public DeliverableUserPartnershipPerson save(DeliverableUserPartnershipPerson deliverableUserPartnershipPerson);
}
