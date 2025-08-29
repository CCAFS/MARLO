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

import org.cgiar.ccafs.marlo.data.model.PRMSInnovation;

import java.util.List;


/**
 * @author CCAFS
 */

public interface PRMSInnovationManager {


  /**
   * This method removes a specific pRMSInnovation value from the database.
   * 
   * @param pRMSInnovationId is the pRMSInnovation identifier.
   * @return true if the pRMSInnovation was successfully deleted, false otherwise.
   */
  public void deletePRMSInnovation(long pRMSInnovationId);


  /**
   * This method validate if the pRMSInnovation identify with the given id exists in the system.
   * 
   * @param pRMSInnovationID is a pRMSInnovation identifier.
   * @return true if the pRMSInnovation exists, false otherwise.
   */
  public boolean existPRMSInnovation(long pRMSInnovationID);


  /**
   * This method gets a list of pRMSInnovation that are active
   * 
   * @return a list from PRMSInnovation null if no exist records
   */
  public List<PRMSInnovation> findAll();


  /**
   * This method gets a pRMSInnovation object by a given pRMSInnovation identifier.
   * 
   * @param pRMSInnovationID is the pRMSInnovation identifier.
   * @return a PRMSInnovation object.
   */
  public PRMSInnovation getPRMSInnovationById(long pRMSInnovationID);

  /**
   * This method saves the information of the given pRMSInnovation
   * 
   * @param pRMSInnovation - is the pRMSInnovation object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the pRMSInnovation was
   *         updated
   *         or -1 is some error occurred.
   */
  public PRMSInnovation savePRMSInnovation(PRMSInnovation pRMSInnovation);


}
