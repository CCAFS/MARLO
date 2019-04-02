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

import org.cgiar.ccafs.marlo.data.model.ClarisaMonitoring;

import java.util.List;


/**
 * @author CCAFS
 */

public interface ClarisaMonitoringManager {


  /**
   * This method removes a specific clarisaMonitoring value from the database.
   * 
   * @param clarisaMonitoringId is the clarisaMonitoring identifier.
   * @return true if the clarisaMonitoring was successfully deleted, false otherwise.
   */
  public void deleteClarisaMonitoring(long clarisaMonitoringId);


  /**
   * This method validate if the clarisaMonitoring identify with the given id exists in the system.
   * 
   * @param clarisaMonitoringID is a clarisaMonitoring identifier.
   * @return true if the clarisaMonitoring exists, false otherwise.
   */
  public boolean existClarisaMonitoring(long clarisaMonitoringID);


  /**
   * This method gets a list of clarisaMonitoring that are active
   * 
   * @return a list from ClarisaMonitoring null if no exist records
   */
  public List<ClarisaMonitoring> findAll();


  /**
   * This method gets a clarisaMonitoring object by a given clarisaMonitoring identifier.
   * 
   * @param clarisaMonitoringID is the clarisaMonitoring identifier.
   * @return a ClarisaMonitoring object.
   */
  public ClarisaMonitoring getClarisaMonitoringById(long clarisaMonitoringID);

  /**
   * This method saves the information of the given clarisaMonitoring
   * 
   * @param clarisaMonitoring - is the clarisaMonitoring object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the clarisaMonitoring was
   *         updated
   *         or -1 is some error occurred.
   */
  public ClarisaMonitoring saveClarisaMonitoring(ClarisaMonitoring clarisaMonitoring);


}
