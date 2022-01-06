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

import org.cgiar.ccafs.marlo.data.model.DeliverableUserPartnership;
import org.cgiar.ccafs.marlo.data.model.Phase;

import java.util.List;


/**
 * @author CCAFS
 */

public interface DeliverableUserPartnershipManager {


  public DeliverableUserPartnership copyDeliverableUserPartnership(DeliverableUserPartnership deliverablePartnership,
    Phase phase);


  /**
   * This method removes a specific deliverableUserPartnership value from the database.
   * 
   * @param deliverableUserPartnershipId is the deliverableUserPartnership identifier.
   * @return true if the deliverableUserPartnership was successfully deleted, false otherwise.
   */
  public void deleteDeliverableUserPartnership(long deliverableUserPartnershipId);


  /**
   * This method validate if the deliverableUserPartnership identify with the given id exists in the system.
   * 
   * @param deliverableUserPartnershipID is a deliverableUserPartnership identifier.
   * @return true if the deliverableUserPartnership exists, false otherwise.
   */
  public boolean existDeliverableUserPartnership(long deliverableUserPartnershipID);


  /**
   * This method gets a list of deliverableUserPartnership that are active
   * 
   * @return a list from DeliverableUserPartnership null if no exist records
   */
  public List<DeliverableUserPartnership> findAll();

  /**
   * This method gets a deliverableUserPartnership list by a given delivearable identifier.
   * 
   * @param deliverableUserPartnershipID is the deliverableUserPartnership identifier.
   * @return a DeliverableUserPartnership list.
   */
  List<DeliverableUserPartnership> findByDeliverableID(long deliverableID);

  /**
   * This method gets a deliverableUserPartnership list by a given Institution, Project and Phase identifier.
   * 
   * @param institutionId is the institution identifier.
   * @param projectId is the project identifier.
   * @param phaseId is the phase identifier.
   * @return a DeliverableUserPartnership list.
   */
  public List<DeliverableUserPartnership> findPartnershipsByInstitutionProjectAndPhase(Long institutionId,
    Long projectId, Long phaseId);

  /**
   * This method gets a deliverableUserPartnership object by a given deliverableUserPartnership identifier.
   * 
   * @param deliverableUserPartnershipID is the deliverableUserPartnership identifier.
   * @return a DeliverableUserPartnership object.
   */
  public DeliverableUserPartnership getDeliverableUserPartnershipById(long deliverableUserPartnershipID);

  /**
   * This method saves the information of the given deliverableUserPartnership
   * 
   * @param deliverableUserPartnership - is the deliverableUserPartnership object with the new information to be
   *        added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the
   *         deliverableUserPartnership was
   *         updated
   *         or -1 is some error occurred.
   */
  public DeliverableUserPartnership
    saveDeliverableUserPartnership(DeliverableUserPartnership deliverableUserPartnership);


}
