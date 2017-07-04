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

import org.cgiar.ccafs.marlo.data.model.CenterCycle;
import org.cgiar.ccafs.marlo.data.service.impl.CenterCycleManager;

import java.util.List;

import com.google.inject.ImplementedBy;

/**
 * @author Christian Garcia
 */
@ImplementedBy(CenterCycleManager.class)
public interface ICenterCycleManager {


  /**
   * This method removes a specific researchCycle value from the database.
   * 
   * @param researchCycleId is the researchCycle identifier.
   * @return true if the researchCycle was successfully deleted, false otherwise.
   */
  public boolean deleteResearchCycle(long researchCycleId);


  /**
   * This method validate if the researchCycle identify with the given id exists in the system.
   * 
   * @param researchCycleID is a researchCycle identifier.
   * @return true if the researchCycle exists, false otherwise.
   */
  public boolean existResearchCycle(long researchCycleID);


  /**
   * This method gets a list of researchCycle that are active
   * 
   * @return a list from CenterCycle null if no exist records
   */
  public List<CenterCycle> findAll();


  /**
   * This method gets a researchCycle object by a given researchCycle identifier.
   * 
   * @param researchCycleID is the researchCycle identifier.
   * @return a CenterCycle object.
   */
  public CenterCycle getResearchCycleById(long researchCycleID);

  /**
   * This method gets a list of researchCycles belongs of the user
   * 
   * @param userId - the user id
   * @return List of ResearchCycles or null if the user is invalid or not have roles.
   */
  public List<CenterCycle> getResearchCyclesByUserId(Long userId);

  /**
   * This method saves the information of the given researchCycle
   * 
   * @param researchCycle - is the researchCycle object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the researchCycle was
   *         updated
   *         or -1 is some error occurred.
   */
  public long saveResearchCycle(CenterCycle researchCycle);


}
