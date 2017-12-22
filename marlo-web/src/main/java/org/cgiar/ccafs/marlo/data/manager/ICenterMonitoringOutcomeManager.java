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

import org.cgiar.ccafs.marlo.data.model.CenterMonitoringOutcome;

import java.util.List;


/**
 * @author Christian Garcia
 */

public interface ICenterMonitoringOutcomeManager {


  /**
   * This method removes a specific monitoringOutcome value from the database.
   * 
   * @param monitoringOutcomeId is the monitoringOutcome identifier.
   * @return true if the monitoringOutcome was successfully deleted, false otherwise.
   */
  public void deleteMonitoringOutcome(long monitoringOutcomeId);


  /**
   * This method validate if the monitoringOutcome identify with the given id exists in the system.
   * 
   * @param monitoringOutcomeID is a monitoringOutcome identifier.
   * @return true if the monitoringOutcome exists, false otherwise.
   */
  public boolean existMonitoringOutcome(long monitoringOutcomeID);


  /**
   * This method gets a list of monitoringOutcome that are active
   * 
   * @return a list from CenterMonitoringOutcome null if no exist records
   */
  public List<CenterMonitoringOutcome> findAll();


  /**
   * This method gets a monitoringOutcome object by a given monitoringOutcome identifier.
   * 
   * @param monitoringOutcomeID is the monitoringOutcome identifier.
   * @return a CenterMonitoringOutcome object.
   */
  public CenterMonitoringOutcome getMonitoringOutcomeById(long monitoringOutcomeID);

  /**
   * This method gets a list of monitoringOutcomes belongs of the user
   * 
   * @param userId - the user id
   * @return List of MonitoringOutcomes or null if the user is invalid or not have roles.
   */
  public List<CenterMonitoringOutcome> getMonitoringOutcomesByUserId(Long userId);

  /**
   * This method saves the information of the given monitoringOutcome
   * 
   * @param monitoringOutcome - is the monitoringOutcome object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the monitoringOutcome was
   *         updated
   *         or -1 is some error occurred.
   */
  public CenterMonitoringOutcome saveMonitoringOutcome(CenterMonitoringOutcome monitoringOutcome);


}
