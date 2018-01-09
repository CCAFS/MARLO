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

import org.cgiar.ccafs.marlo.data.model.DeliverableMetadataElement;

import java.util.List;


/**
 * @author Christian Garcia
 */

public interface DeliverableMetadataElementManager {


  /**
   * This method removes a specific deliverableMetadataElement value from the database.
   * 
   * @param deliverableMetadataElementId is the deliverableMetadataElement identifier.
   * @return true if the deliverableMetadataElement was successfully deleted, false otherwise.
   */
  public void deleteDeliverableMetadataElement(long deliverableMetadataElementId);


  /**
   * This method validate if the deliverableMetadataElement identify with the given id exists in the system.
   * 
   * @param deliverableMetadataElementID is a deliverableMetadataElement identifier.
   * @return true if the deliverableMetadataElement exists, false otherwise.
   */
  public boolean existDeliverableMetadataElement(long deliverableMetadataElementID);


  /**
   * This method gets a list of deliverableMetadataElement that are active
   * 
   * @return a list from DeliverableMetadataElement null if no exist records
   */
  public List<DeliverableMetadataElement> findAll();


  /**
   * This method gets a deliverableMetadataElement object by a given deliverableMetadataElement identifier.
   * 
   * @param deliverableMetadataElementID is the deliverableMetadataElement identifier.
   * @return a DeliverableMetadataElement object.
   */
  public DeliverableMetadataElement getDeliverableMetadataElementById(long deliverableMetadataElementID);

  /**
   * This method saves the information of the given deliverableMetadataElement
   * 
   * @param deliverableMetadataElement - is the deliverableMetadataElement object with the new information to be
   *        added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the
   *         deliverableMetadataElement was
   *         updated
   *         or -1 is some error occurred.
   */
  public DeliverableMetadataElement
    saveDeliverableMetadataElement(DeliverableMetadataElement deliverableMetadataElement);


}
