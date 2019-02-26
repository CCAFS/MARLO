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

import org.cgiar.ccafs.marlo.data.model.DeliverableGeographicScope;

import java.util.List;


/**
 * @author CCAFS
 */

public interface DeliverableGeographicScopeManager {


  /**
   * This method removes a specific deliverableGeographicScope value from the database.
   * 
   * @param deliverableGeographicScopeId is the deliverableGeographicScope identifier.
   * @return true if the deliverableGeographicScope was successfully deleted, false otherwise.
   */
  public void deleteDeliverableGeographicScope(long deliverableGeographicScopeId);


  /**
   * This method validate if the deliverableGeographicScope identify with the given id exists in the system.
   * 
   * @param deliverableGeographicScopeID is a deliverableGeographicScope identifier.
   * @return true if the deliverableGeographicScope exists, false otherwise.
   */
  public boolean existDeliverableGeographicScope(long deliverableGeographicScopeID);


  /**
   * This method gets a list of deliverableGeographicScope that are active
   * 
   * @return a list from DeliverableGeographicScope null if no exist records
   */
  public List<DeliverableGeographicScope> findAll();


  /**
   * This method gets a deliverableGeographicScope object by a given deliverableGeographicScope identifier.
   * 
   * @param deliverableGeographicScopeID is the deliverableGeographicScope identifier.
   * @return a DeliverableGeographicScope object.
   */
  public DeliverableGeographicScope getDeliverableGeographicScopeById(long deliverableGeographicScopeID);

  /**
   * This method saves the information of the given deliverableGeographicScope
   * 
   * @param deliverableGeographicScope - is the deliverableGeographicScope object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the deliverableGeographicScope was
   *         updated
   *         or -1 is some error occurred.
   */
  public DeliverableGeographicScope saveDeliverableGeographicScope(DeliverableGeographicScope deliverableGeographicScope);


}
