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

import org.cgiar.ccafs.marlo.data.model.PartnerRequest;

import java.util.List;


public interface PartnerRequestDAO {

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
   * This method gets a partnerRequest object by a given partnerRequest identifier.
   * 
   * @param partnerRequestID is the partnerRequest identifier.
   * @return a PartnerRequest object.
   */
  public PartnerRequest find(long id);

  /**
   * This method gets a list of partnerRequest that are active
   * 
   * @return a list from PartnerRequest null if no exist records
   */
  public List<PartnerRequest> findAll();


  /**
   * This method saves the information of the given partnerRequest
   * 
   * @param partnerRequest - is the partnerRequest object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the partnerRequest was
   *         updated
   *         or -1 is some error occurred.
   */
  public PartnerRequest save(PartnerRequest partnerRequest);
}
