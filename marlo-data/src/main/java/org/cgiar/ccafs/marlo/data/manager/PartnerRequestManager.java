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

import org.cgiar.ccafs.marlo.data.model.PartnerRequest;
import org.cgiar.ccafs.marlo.data.model.PartnerRequestOutcome;
import org.cgiar.ccafs.marlo.data.model.User;

import java.util.List;


/**
 * @author Christian Garcia
 */

public interface PartnerRequestManager {

  /**
   * Accepts the office requests of one institution: each requested country becomes a non-headquarters location of the
   * institution unless it is already one (that request is closed as not accepted, SKIPPED_EXISTING). Runs in one
   * transaction and locks the requests in ascending id order. An id that is missing, not an office request of the
   * institution, or not pending is reported and the others proceed.
   * 
   * @param requestIds the office request ids.
   * @param institutionId the institution the requests belong to.
   * @return one outcome per distinct id, in ascending id order.
   */
  public List<PartnerRequestOutcome> acceptOfficeRequests(List<Long> requestIds, long institutionId);

  /**
   * Accepts a pending partner request, identified by its editable copy: creates the institution from the copy, its
   * headquarters location, marks the copy accepted and closes both rows, in one transaction. Nothing is written when
   * the request is missing, is not a copy, or is no longer pending.
   * 
   * @param requestId the id of the copy.
   * @param approver the superadmin accepting the request; recorded as the creator of the institution.
   * @return the outcome, carrying the copy and the new institution when DONE.
   */
  public PartnerRequestOutcome acceptPartnerRequest(long requestId, User approver);

  /**
   * Saves a new partner request: the original and its editable copy, in one transaction.
   * 
   * @param original the request as the requester made it (never edited afterwards).
   * @param copy the editable copy the superadmin reviews; it is linked to the saved original.
   * @return the saved copy.
   */
  public PartnerRequest createPartnerRequest(PartnerRequest original, PartnerRequest copy);

  /**
   * Edits the copy of a pending partner request. Text fields that are empty are blanked, the type and country are
   * changed only when they are given and exist, and nothing is written when no field is given. Nothing is written
   * either when the request is missing, is not a copy, or is no longer pending.
   * 
   * @param requestId the id of the copy.
   * @param name the new name.
   * @param acronym the new acronym.
   * @param webPage the new website.
   * @param institutionTypeId the new institution type id, or null.
   * @param countryIso the ISO alpha-2 code of the new country, or null.
   * @param justification why the copy was edited.
   * @return the outcome, carrying the copy when DONE.
   */
  public PartnerRequestOutcome editPartnerRequest(long requestId, String name, String acronym, String webPage,
    Long institutionTypeId, String countryIso, String justification);


  /**
   * This method removes a specific partnerRequest value from the database.
   * 
   * @param partnerRequestId is the partnerRequest identifier.
   * @return true if the partnerRequest was successfully deleted, false otherwise.
   */
  public void deletePartnerRequest(long partnerRequestId);


  /**
   * This method validate if the partnerRequest identify with the given id exists in the system.
   * 
   * @param partnerRequestID is a partnerRequest identifier.
   * @return true if the partnerRequest exists, false otherwise.
   */
  public boolean existPartnerRequest(long partnerRequestID);


  /**
   * This method gets a list of partnerRequest that are active
   * 
   * @return a list from PartnerRequest null if no exist records
   */
  public List<PartnerRequest> findAll();


  /**
   * This method gets a partnerRequest object by a given partnerRequest identifier.
   * 
   * @param partnerRequestID is the partnerRequest identifier.
   * @return a PartnerRequest object.
   */
  public PartnerRequest getPartnerRequestById(long partnerRequestID);

  /**
   * Rejects the office requests of one institution, recording the justification and the rejecting user. Same
   * transaction, lock order and per-id refusals as {@link #acceptOfficeRequests(List, long)}.
   * 
   * @param requestIds the office request ids.
   * @param institutionId the institution the requests belong to.
   * @param justification why they were rejected.
   * @param user the rejecting user.
   * @return one outcome per distinct id, in ascending id order.
   */
  public List<PartnerRequestOutcome> rejectOfficeRequests(List<Long> requestIds, long institutionId,
    String justification, User user);

  /**
   * Rejects a pending partner request, identified by its editable copy: records the justification, the rejecting user
   * and the date on the copy and closes both rows, in one transaction. Nothing is written when the request is
   * missing, is not a copy, or is no longer pending.
   * 
   * @param requestId the id of the copy.
   * @param justification why it was rejected.
   * @param user the rejecting user.
   * @return the outcome, carrying the copy when DONE.
   */
  public PartnerRequestOutcome rejectPartnerRequest(long requestId, String justification, User user);

  /**
   * This method saves the information of the given partnerRequest
   * 
   * @param partnerRequest - is the partnerRequest object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the partnerRequest was
   *         updated
   *         or -1 is some error occurred.
   */
  public PartnerRequest savePartnerRequest(PartnerRequest partnerRequest);


}
