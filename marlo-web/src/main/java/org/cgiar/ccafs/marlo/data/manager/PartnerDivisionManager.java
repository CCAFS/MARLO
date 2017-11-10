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

import org.cgiar.ccafs.marlo.data.model.PartnerDivision;

import java.util.List;


/**
 * @author Christian Garcia
 */

public interface PartnerDivisionManager {


  /**
   * This method removes a specific partnerDivision value from the database.
   * 
   * @param partnerDivisionId is the partnerDivision identifier.
   * @return true if the partnerDivision was successfully deleted, false otherwise.
   */
  public void deletePartnerDivision(long partnerDivisionId);


  /**
   * This method validate if the partnerDivision identify with the given id exists in the system.
   * 
   * @param partnerDivisionID is a partnerDivision identifier.
   * @return true if the partnerDivision exists, false otherwise.
   */
  public boolean existPartnerDivision(long partnerDivisionID);


  /**
   * This method gets a list of partnerDivision that are active
   * 
   * @return a list from PartnerDivision null if no exist records
   */
  public List<PartnerDivision> findAll();


  /**
   * This method gets a partnerDivision object by a given partnerDivision identifier.
   * 
   * @param partnerDivisionID is the partnerDivision identifier.
   * @return a PartnerDivision object.
   */
  public PartnerDivision getPartnerDivisionById(long partnerDivisionID);

  /**
   * This method saves the information of the given partnerDivision
   * 
   * @param partnerDivision - is the partnerDivision object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the partnerDivision was
   *         updated
   *         or -1 is some error occurred.
   */
  public PartnerDivision savePartnerDivision(PartnerDivision partnerDivision);


}
