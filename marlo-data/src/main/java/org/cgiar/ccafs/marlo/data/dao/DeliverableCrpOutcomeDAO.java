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

import org.cgiar.ccafs.marlo.data.model.DeliverableCrpOutcome;

import java.util.List;


public interface DeliverableCrpOutcomeDAO {

  /**
   * This method removes a specific deliverableCrpOutcome value from the database.
   * 
   * @param deliverableCrpOutcomeId is the deliverableCrpOutcome identifier.
   * @return true if the deliverableCrpOutcome was successfully deleted, false otherwise.
   */
  public void deleteDeliverableCrpOutcome(long deliverableCrpOutcomeId);

  /**
   * This method validate if the deliverableCrpOutcome identify with the given id exists in the system.
   * 
   * @param deliverableCrpOutcomeID is a deliverableCrpOutcome identifier.
   * @return true if the deliverableCrpOutcome exists, false otherwise.
   */
  public boolean existDeliverableCrpOutcome(long deliverableCrpOutcomeID);

  /**
   * This method gets a deliverableCrpOutcome object by a given deliverableCrpOutcome identifier.
   * 
   * @param deliverableCrpOutcomeID is the deliverableCrpOutcome identifier.
   * @return a DeliverableCrpOutcome object.
   */
  public DeliverableCrpOutcome find(long id);

  /**
   * This method gets a list of deliverableCrpOutcome that are active
   * 
   * @return a list from DeliverableCrpOutcome null if no exist records
   */
  public List<DeliverableCrpOutcome> findAll();


  /**
   * This method saves the information of the given deliverableCrpOutcome
   * 
   * @param deliverableCrpOutcome - is the deliverableCrpOutcome object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the deliverableCrpOutcome
   *         was
   *         updated
   *         or -1 is some error occurred.
   */
  public DeliverableCrpOutcome save(DeliverableCrpOutcome deliverableCrpOutcome);
}
