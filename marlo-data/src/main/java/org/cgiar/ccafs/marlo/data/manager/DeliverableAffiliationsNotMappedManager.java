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

import org.cgiar.ccafs.marlo.data.model.DeliverableAffiliationsNotMapped;
import org.cgiar.ccafs.marlo.data.model.Phase;

import java.util.List;

/**************
 * @author German C. Martinez - CIAT/CCAFS
 **************/

public interface DeliverableAffiliationsNotMappedManager {

  /**
   * This method removes a specific deliverableAffiliationsNotMapped value from the database.
   * 
   * @param deliverableAffiliationsNotMappedId is the deliverableAffiliationsNotMapped identifier.
   * @return true if the deliverableAffiliationsNotMapped was successfully deleted, false otherwise.
   */
  public void deleteDeliverableAffiliationsNotMapped(long deliverableAffiliationsNotMappedId);


  /**
   * This method validate if the deliverableAffiliationsNotMapped identify with the given id exists in the system.
   * 
   * @param deliverableAffiliationsNotMappedID is a deliverableAffiliationsNotMapped identifier.
   * @return true if the deliverableAffiliationsNotMapped exists, false otherwise.
   */
  public boolean existDeliverableAffiliationsNotMapped(long deliverableAffiliationsNotMappedID);


  /**
   * This method gets a list of deliverableAffiliationsNotMapped that are active
   * 
   * @return a list from DeliverableAffiliationsNotMapped null if no exist records
   */
  public List<DeliverableAffiliationsNotMapped> findAll();

  /**
   * This method gets a list of deliverableAffiliationsNotMapped by externalSourceId
   *
   * @param externalSourceId deliverable Metadata External Sources Id
   * @return a list from DeliverableAffiliationsNotMappeds null if no exist records
   */
  List<DeliverableAffiliationsNotMapped> findBydeliverableMetadataExternalSourcesId(long externalSourceId);

  /**
   * This method gets a deliverableAffiliationsNotMapped object by a given deliverableAffiliationsNotMapped
   * identifier.
   * 
   * @param deliverableAffiliationsNotMappedID is the deliverableAffiliationsNotMapped identifier.
   * @return a DeliverableAffiliationsNotMapped object.
   */
  public DeliverableAffiliationsNotMapped
    getDeliverableAffiliationsNotMappedById(long deliverableAffiliationsNotMappedID);

  /**
   * Replicates a deliverableAffiliationsNotMapped, starting from the given phase
   * 
   * @param originalDeliverableAffiliationsNotMapped DeliverableAffiliationsNotMapped to be replicated
   * @param initialPhase initial replication phase
   */
  public void replicate(DeliverableAffiliationsNotMapped originalDeliverableAffiliationsNotMapped, Phase initialPhase);


  /**
   * This method saves the information of the given deliverableAffiliationsNotMapped
   * 
   * @param deliverableAffiliationsNotMapped - is the deliverableAffiliationsNotMapped object with the new information
   *        to be
   *        added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the
   *         deliverableAffiliationsNotMapped was
   *         updated
   *         or -1 is some error occurred.
   */
  public DeliverableAffiliationsNotMapped
    saveDeliverableAffiliationsNotMapped(DeliverableAffiliationsNotMapped deliverableAffiliationsNotMapped);
}
