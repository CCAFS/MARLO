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
import org.cgiar.ccafs.marlo.data.model.DeliverableMetadataExternalSources;
import org.cgiar.ccafs.marlo.data.model.Phase;

import java.util.List;

/**************
 * @author German C. Martinez - CIAT/CCAFS
 **************/

public interface DeliverableMetadataExternalSourcesManager {

  /**
   * This method removes a specific deliverableMetadataExternalSource value from the database.
   * 
   * @param deliverableMetadataExternalSourceId is the deliverableMetadataExternalSource identifier.
   * @return true if the deliverableMetadataExternalSource was successfully deleted, false otherwise.
   */
  public void deleteDeliverableMetadataExternalSources(long deliverableMetadataExternalSourceId);


  /**
   * This method validate if the deliverableMetadataExternalSource identify with the given id exists in the system.
   * 
   * @param deliverableMetadataExternalSourceID is a deliverableMetadataExternalSource identifier.
   * @return true if the deliverableMetadataExternalSource exists, false otherwise.
   */
  public boolean existDeliverableMetadataExternalSources(long deliverableMetadataExternalSourceID);


  /**
   * This method gets a list of deliverableMetadataExternalSource that are active
   * 
   * @return a list from DeliverableMetadataExternalSources null if no exist records
   */
  public List<DeliverableMetadataExternalSources> findAll();


  /**
   * This method gets a deliverableMetadataExternalSource object by a given phase, deliverable and metadata identifier
   * 
   * @param metadataElement
   * @return a DeliverableMetadataExternalSources object.
   */
  public DeliverableMetadataExternalSources findByPhaseAndDeliverable(Phase phase, Deliverable deliverable);

  /**
   * This method gets a deliverableMetadataExternalSource object by a given deliverableMetadataExternalSource
   * identifier.
   * 
   * @param deliverableMetadataExternalSourceID is the deliverableMetadataExternalSource identifier.
   * @return a DeliverableMetadataExternalSources object.
   */
  public DeliverableMetadataExternalSources
    getDeliverableMetadataExternalSourcesById(long deliverableMetadataExternalSourceID);

  /**
   * Replicates a deliverableMetadataExternalSource, starting from the given phase
   * 
   * @param originalDeliverableMetadataExternalSource DeliverableMetadataExternalSources to be replicated
   * @param initialPhase initial replication phase
   */
  public void replicate(DeliverableMetadataExternalSources originalDeliverableMetadataExternalSource,
    Phase initialPhase);

  /**
   * This method saves the information of the given deliverableMetadataExternalSource
   * 
   * @param deliverableMetadataExternalSource - is the deliverableMetadataExternalSource object with the new information
   *        to be
   *        added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the
   *         deliverableMetadataExternalSource was
   *         updated
   *         or -1 is some error occurred.
   */
  public DeliverableMetadataExternalSources
    saveDeliverableMetadataExternalSources(DeliverableMetadataExternalSources deliverableMetadataExternalSource);
}
