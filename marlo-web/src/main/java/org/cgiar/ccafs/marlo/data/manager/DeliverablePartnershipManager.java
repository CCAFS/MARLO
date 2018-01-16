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

import org.cgiar.ccafs.marlo.data.model.DeliverablePartnership;
import org.cgiar.ccafs.marlo.data.model.Phase;

import java.util.List;


/**
 * @author Christian Garcia
 */

public interface DeliverablePartnershipManager {


  public DeliverablePartnership copyDeliverablePartnership(DeliverablePartnership deliverablePartnership, Phase phase);


  /**
   * This method removes a specific deliverablePartnership value from the database.
   * 
   * @param deliverablePartnershipId is the deliverablePartnership identifier.
   * @return true if the deliverablePartnership was successfully deleted, false otherwise.
   */
  public void deleteDeliverablePartnership(long deliverablePartnershipId);


  /**
   * This method validate if the deliverablePartnership identify with the given id exists in the system.
   * 
   * @param deliverablePartnershipID is a deliverablePartnership identifier.
   * @return true if the deliverablePartnership exists, false otherwise.
   */
  public boolean existDeliverablePartnership(long deliverablePartnershipID);


  /**
   * This method gets a list of deliverablePartnership that are active
   * 
   * @return a list from DeliverablePartnership null if no exist records
   */
  public List<DeliverablePartnership> findAll();

  public List<DeliverablePartnership> findByDeliverablePhasePartnerAndPartnerperson(long deliverableID, Long phase,
    Long projectPartnerId, Long projectPartnerPersonId, Long partnerDivisionId, String partnerType);

  public List<DeliverablePartnership> findForDeliverableIdAndPartnerTypeOther(long deliverableId);

  public List<DeliverablePartnership> findForDeliverableIdAndProjectPersonIdPartnerTypeOther(long deliverableId,
    long projectPersonId);

  /**
   * This method gets a deliverablePartnership object by a given deliverablePartnership identifier.
   * 
   * @param deliverablePartnershipID is the deliverablePartnership identifier.
   * @return a DeliverablePartnership object.
   */
  public DeliverablePartnership getDeliverablePartnershipById(long deliverablePartnershipID);

  /**
   * This method saves the information of the given deliverablePartnership
   * 
   * @param deliverablePartnership - is the deliverablePartnership object with the new information to be added/updated.
   * @param managingPartnersRequired
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the deliverablePartnership
   *         was
   *         updated
   *         or -1 is some error occurred.
   */
  public DeliverablePartnership saveDeliverablePartnership(DeliverablePartnership deliverablePartnership);


  public DeliverablePartnership updateDeliverablePartnership(DeliverablePartnership partnershipResponsibleDBUpdated,
    DeliverablePartnership partnershipResponsibleDB);

}
