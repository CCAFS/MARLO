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
import org.cgiar.ccafs.marlo.data.model.DeliverableAltmetricInfo;
import org.cgiar.ccafs.marlo.data.model.Phase;

import java.util.List;

/**************
 * @author German C. Martinez - CIAT/CCAFS
 **************/

public interface DeliverableAltmetricInfoManager {

  /**
   * This method removes a specific deliverableAltmetricInfo value from the database.
   * 
   * @param deliverableAltmetricInfoId is the deliverableAltmetricInfo identifier.
   * @return true if the deliverableAltmetricInfo was successfully deleted, false otherwise.
   */
  public void deleteDeliverableAltmetricInfo(long deliverableAltmetricInfoId);


  /**
   * This method validate if the deliverableAltmetricInfo identify with the given id exists in the system.
   * 
   * @param deliverableAltmetricInfoID is a deliverableAltmetricInfo identifier.
   * @return true if the deliverableAltmetricInfo exists, false otherwise.
   */
  public boolean existDeliverableAltmetricInfo(long deliverableAltmetricInfoID);


  /**
   * This method gets a list of deliverableAltmetricInfo that are active
   * 
   * @return a list from DeliverableAltmetricInfo null if no exist records
   */
  public List<DeliverableAltmetricInfo> findAll();


  /**
   * This method gets a deliverableAltmetricInfo object by a given phase, deliverable and metadata identifier
   * 
   * @param metadataElement
   * @return a DeliverableAltmetricInfo object.
   */
  public DeliverableAltmetricInfo findByPhaseAndDeliverable(Phase phase, Deliverable deliverable);

  /**
   * This method gets a deliverableAltmetricInfo object by a given deliverableAltmetricInfo
   * identifier.
   * 
   * @param deliverableAltmetricInfoID is the deliverableAltmetricInfo identifier.
   * @return a DeliverableAltmetricInfo object.
   */
  public DeliverableAltmetricInfo getDeliverableAltmetricInfoById(long deliverableAltmetricInfoID);

  /**
   * Replicates a deliverableAltmetricInfo, starting from the given phase
   * 
   * @param originalDeliverableMetadataExternalSource DeliverableAltmetricInfo to be replicated
   * @param initialPhase initial replication phase
   */
  public void replicate(DeliverableAltmetricInfo originalDeliverableMetadataExternalSource, Phase initialPhase);

  /**
   * This method saves the information of the given deliverableAltmetricInfo
   * 
   * @param deliverableAltmetricInfo - is the deliverableAltmetricInfo object with the new information
   *        to be
   *        added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the
   *         deliverableAltmetricInfo was
   *         updated
   *         or -1 is some error occurred.
   */
  public DeliverableAltmetricInfo saveDeliverableAltmetricInfo(DeliverableAltmetricInfo deliverableAltmetricInfo);
}
