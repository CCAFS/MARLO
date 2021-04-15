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

import org.cgiar.ccafs.marlo.data.model.LeverOutcome;

import java.util.List;


public interface LeverOutcomeDAO {

  /**
   * This method removes a specific leverOutcome value from the database.
   * 
   * @param leverOutcomeId is the leverOutcome identifier.
   * @return true if the leverOutcome was successfully deleted, false otherwise.
   */
  public void deleteLeverOutcome(long leverOutcomeId);

  /**
   * This method validate if the leverOutcome identify with the given id exists in the system.
   * 
   * @param leverOutcomeID is a leverOutcome identifier.
   * @return true if the leverOutcome exists, false otherwise.
   */
  public boolean existLeverOutcome(long leverOutcomeID);

  /**
   * This method gets a leverOutcome object by a given leverOutcome identifier.
   * 
   * @param leverOutcomeID is the leverOutcome identifier.
   * @return a LeverOutcome object.
   */
  public LeverOutcome find(long id);

  /**
   * This method gets a list of leverOutcome that are active
   * 
   * @return a list from LeverOutcome null if no exist records
   */
  public List<LeverOutcome> findAll();


  /**
   * This method saves the information of the given leverOutcome
   * 
   * @param leverOutcome - is the leverOutcome object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the leverOutcome was
   *         updated
   *         or -1 is some error occurred.
   */
  public LeverOutcome save(LeverOutcome leverOutcome);
}
