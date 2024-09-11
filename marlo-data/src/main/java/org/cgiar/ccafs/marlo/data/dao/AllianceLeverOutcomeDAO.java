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

import org.cgiar.ccafs.marlo.data.model.AllianceLeverOutcome;

import java.util.List;


public interface AllianceLeverOutcomeDAO {

  /**
   * This method removes a specific allianceLeverOutcome value from the database.
   * 
   * @param allianceLeverOutcomeId is the allianceLeverOutcome identifier.
   * @return true if the allianceLeverOutcome was successfully deleted, false otherwise.
   */
  public void deleteAllianceLeverOutcome(long allianceLeverOutcomeId);

  /**
   * This method validate if the allianceLeverOutcome identify with the given id exists in the system.
   * 
   * @param allianceLeverOutcomeID is a allianceLeverOutcome identifier.
   * @return true if the allianceLeverOutcome exists, false otherwise.
   */
  public boolean existAllianceLeverOutcome(long allianceLeverOutcomeID);

  /**
   * This method gets a allianceLeverOutcome object by a given allianceLeverOutcome identifier.
   * 
   * @param allianceLeverOutcomeID is the allianceLeverOutcome identifier.
   * @return a AllianceLeverOutcome object.
   */
  public AllianceLeverOutcome find(long id);

  /**
   * This method gets a list of allianceLeverOutcome that are active
   * 
   * @return a list from AllianceLeverOutcome null if no exist records
   */
  public List<AllianceLeverOutcome> findAll();


  /**
   * This method saves the information of the given allianceLeverOutcome
   * 
   * @param allianceLeverOutcome - is the allianceLeverOutcome object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the allianceLeverOutcome was
   *         updated
   *         or -1 is some error occurred.
   */
  public AllianceLeverOutcome save(AllianceLeverOutcome allianceLeverOutcome);
}
