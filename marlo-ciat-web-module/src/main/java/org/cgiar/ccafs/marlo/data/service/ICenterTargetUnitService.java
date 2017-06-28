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
package org.cgiar.ccafs.marlo.data.service;

import org.cgiar.ccafs.marlo.data.model.CenterTargetUnit;
import org.cgiar.ccafs.marlo.data.service.impl.CenterTargetUnitService;

import java.util.List;

import com.google.inject.ImplementedBy;

/**
 * @author Christian Garcia
 */
@ImplementedBy(CenterTargetUnitService.class)
public interface ICenterTargetUnitService {


  /**
   * This method removes a specific targetUnit value from the database.
   * 
   * @param targetUnitId is the targetUnit identifier.
   * @return true if the targetUnit was successfully deleted, false otherwise.
   */
  public boolean deleteTargetUnit(long targetUnitId);


  /**
   * This method validate if the targetUnit identify with the given id exists in the system.
   * 
   * @param targetUnitID is a targetUnit identifier.
   * @return true if the targetUnit exists, false otherwise.
   */
  public boolean existTargetUnit(long targetUnitID);


  /**
   * This method gets a list of targetUnit that are active
   * 
   * @return a list from CenterTargetUnit null if no exist records
   */
  public List<CenterTargetUnit> findAll();


  /**
   * This method gets a targetUnit object by a given targetUnit identifier.
   * 
   * @param targetUnitID is the targetUnit identifier.
   * @return a CenterTargetUnit object.
   */
  public CenterTargetUnit getTargetUnitById(long targetUnitID);

  /**
   * This method gets a list of targetUnits belongs of the user
   * 
   * @param userId - the user id
   * @return List of TargetUnits or null if the user is invalid or not have roles.
   */
  public List<CenterTargetUnit> getTargetUnitsByUserId(Long userId);

  /**
   * This method saves the information of the given targetUnit
   * 
   * @param targetUnit - is the targetUnit object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the targetUnit was
   *         updated
   *         or -1 is some error occurred.
   */
  public long saveTargetUnit(CenterTargetUnit targetUnit);


}
