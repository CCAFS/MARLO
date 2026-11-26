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
package org.cgiar.ccafs.marlo.data.model;

/**
 * The result of one decision on one partner request (accept, reject, edit or an office decision). It is a plain value
 * for the caller and is never serialised: the JSON actions publish {@link #getStatus()} as a string only.
 */
public class PartnerRequestOutcome {

  /**
   * What happened to the request.
   */
  public enum Status {
    /** The decision was applied. */
    DONE,
    /** The request is no longer pending, so nothing was written. */
    ALREADY_HANDLED,
    /** No request has that id, so nothing was written. */
    NOT_FOUND,
    /** The request is not the kind of row this decision applies to, so nothing was written. */
    WRONG_KIND,
    /** An office request whose country is already a location of the institution: closed as not accepted. */
    SKIPPED_EXISTING
  }


  private final long requestId;
  private final Status status;
  private final PartnerRequest request;
  private final Institution institution;

  public PartnerRequestOutcome(long requestId, Status status, PartnerRequest request, Institution institution) {
    this.requestId = requestId;
    this.status = status;
    this.request = request;
    this.institution = institution;
  }

  /**
   * @return the institution created (partner accept) or extended (office accept); null when there is none.
   */
  public Institution getInstitution() {
    return this.institution;
  }

  /**
   * @return the request the decision was applied to; null when the status is NOT_FOUND.
   */
  public PartnerRequest getRequest() {
    return this.request;
  }

  /**
   * @return the id the caller asked for, which is the only handle left when the status is NOT_FOUND.
   */
  public long getRequestId() {
    return this.requestId;
  }

  public Status getStatus() {
    return this.status;
  }

  /**
   * @return true when the requester must be notified: the decision was applied, or the office country was already
   *         there (AC FN-008).
   */
  public boolean isNotifiable() {
    return this.status == Status.DONE || this.status == Status.SKIPPED_EXISTING;
  }

}
