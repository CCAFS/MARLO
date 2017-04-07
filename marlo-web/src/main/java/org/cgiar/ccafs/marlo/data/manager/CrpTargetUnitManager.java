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

import org.cgiar.ccafs.marlo.data.manager.impl.CrpTargetUnitManagerImpl;
import org.cgiar.ccafs.marlo.data.model.CrpTargetUnit;

import java.util.List;

import com.google.inject.ImplementedBy;

/**
 * @author Christian Garcia
 */
@ImplementedBy(CrpTargetUnitManagerImpl.class)
public interface CrpTargetUnitManager {


  /**
   * This method removes a specific crpTargetUnit value from the database.
   * 
   * @param crpTargetUnitId is the crpTargetUnit identifier.
   * @return true if the crpTargetUnit was successfully deleted, false otherwise.
   */
  public boolean deleteCrpTargetUnit(long crpTargetUnitId);


  /**
   * This method validate if the crpTargetUnit identify with the given id exists in the system.
   * 
   * @param crpTargetUnitID is a crpTargetUnit identifier.
   * @return true if the crpTargetUnit exists, false otherwise.
   */
  public boolean existCrpTargetUnit(long crpTargetUnitID);


  /**
   * This method gets a list of crpTargetUnit that are active
   * 
   * @return a list from CrpTargetUnit null if no exist records
   */
  public List<CrpTargetUnit> findAll();


  /**
   * This method gets a crpTargetUnit object by a given crp and target unit identifier
   * 
   * @param crpId is the crp id.
   * @param targetUnitId is the target unit id.
   * @return a CrpTargetUnit object.
   */
  public CrpTargetUnit getByTargetUnitIdAndCrpId(long crpId, long targetUnitId);

  /**
   * This method gets a crpTargetUnit object by a given crpTargetUnit identifier.
   * 
   * @param crpTargetUnitID is the crpTargetUnit identifier.
   * @return a CrpTargetUnit object.
   */
  public CrpTargetUnit getCrpTargetUnitById(long crpTargetUnitID);

  /**
   * This method saves the information of the given crpTargetUnit
   * 
   * @param crpTargetUnit - is the crpTargetUnit object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the crpTargetUnit was
   *         updated
   *         or -1 is some error occurred.
   */
  public long saveCrpTargetUnit(CrpTargetUnit crpTargetUnit);


}
