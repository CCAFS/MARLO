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

import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.data.model.DeliverableAffiliation;
import org.cgiar.ccafs.marlo.data.model.Phase;

import java.util.List;


/**
 * @author CCAFS
 */

public interface DeliverableAffiliationManager {


  /**
   * This method removes a specific deliverableAffiliation value from the database.
   * 
   * @param deliverableAffiliationId is the deliverableAffiliation identifier.
   * @return true if the deliverableAffiliation was successfully deleted, false otherwise.
   */
  public void deleteDeliverableAffiliation(long deliverableAffiliationId);


  /**
   * This method validate if the deliverableAffiliation identify with the given id exists in the system.
   * 
   * @param deliverableAffiliationID is a deliverableAffiliation identifier.
   * @return true if the deliverableAffiliation exists, false otherwise.
   */
  public boolean existDeliverableAffiliation(long deliverableAffiliationID);


  /**
   * This method gets a list of deliverableAffiliation that are active
   * 
   * @return a list from DeliverableAffiliation null if no exist records
   */
  public List<DeliverableAffiliation> findAll();


  /**
   * This method gets a list of deliverableAffiliation by a given phase, deliverable
   * 
   * @param metadataElement
   * @return a list of DeliverableAffiliation.
   */
  public List<DeliverableAffiliation> findByPhaseAndDeliverable(Phase phase, Deliverable deliverable);

  /**
   * This method gets a deliverableAffiliation object by a given deliverableAffiliation identifier.
   * 
   * @param deliverableAffiliationID is the deliverableAffiliation identifier.
   * @return a DeliverableAffiliation object.
   */
  public DeliverableAffiliation getDeliverableAffiliationById(long deliverableAffiliationID);

  /**
   * Replicates a affiliation, starting from the given phase
   * 
   * @param originalDeliverableAffiliation affiliation to be replicated
   * @param initialPhase initial replication phase
   */
  public void replicate(DeliverableAffiliation originalDeliverableAffiliation, Phase initialPhase);

  /**
   * This method saves the information of the given deliverableAffiliation
   * 
   * @param deliverableAffiliation - is the deliverableAffiliation object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the deliverableAffiliation
   *         was
   *         updated
   *         or -1 is some error occurred.
   */
  public DeliverableAffiliation saveDeliverableAffiliation(DeliverableAffiliation deliverableAffiliation);

}
