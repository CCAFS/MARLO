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

import org.cgiar.ccafs.marlo.data.model.SrfTargetUnit;

import java.util.List;


/**
 * @author Christian Garcia
 */

public interface SrfTargetUnitManager {


  /**
   * This method removes a specific srfTargetUnit value from the database.
   * 
   * @param srfTargetUnitId is the srfTargetUnit identifier.
   * @return true if the srfTargetUnit was successfully deleted, false otherwise.
   */
  public void deleteSrfTargetUnit(long srfTargetUnitId);


  /**
   * This method validate if the srfTargetUnit identify with the given id exists in the system.
   * 
   * @param srfTargetUnitID is a srfTargetUnit identifier.
   * @return true if the srfTargetUnit exists, false otherwise.
   */
  public boolean existSrfTargetUnit(long srfTargetUnitID);


  /**
   * This method gets a list of srfTargetUnit that are active
   * 
   * @return a list from SrfTargetUnit null if no exist records
   */
  public List<SrfTargetUnit> findAll();


  /**
   * This method gets a srfTargetUnit object by a given srfTargetUnit identifier.
   * 
   * @param srfTargetUnitID is the srfTargetUnit identifier.
   * @return a SrfTargetUnit object.
   */
  public SrfTargetUnit getSrfTargetUnitById(long srfTargetUnitID);

  /**
   * This method saves the information of the given srfTargetUnit
   * 
   * @param srfTargetUnit - is the srfTargetUnit object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the srfTargetUnit was
   *         updated
   *         or -1 is some error occurred.
   */
  public SrfTargetUnit saveSrfTargetUnit(SrfTargetUnit srfTargetUnit);


}
