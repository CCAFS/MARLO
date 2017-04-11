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

import org.cgiar.ccafs.marlo.data.manager.impl.PhaseManagerImpl;
import org.cgiar.ccafs.marlo.data.model.Phase;

import java.util.List;

import com.google.inject.ImplementedBy;

/**
 * @author Christian Garcia
 */
@ImplementedBy(PhaseManagerImpl.class)
public interface PhaseManager {


  /**
   * This method removes a specific phase value from the database.
   * 
   * @param phaseId is the phase identifier.
   * @return true if the phase was successfully deleted, false otherwise.
   */
  public boolean deletePhase(long phaseId);


  /**
   * This method validate if the phase identify with the given id exists in the system.
   * 
   * @param phaseID is a phase identifier.
   * @return true if the phase exists, false otherwise.
   */
  public boolean existPhase(long phaseID);

  /**
   * This method gets a list of phase that are active
   * 
   * @return a list from Phase null if no exist records
   */
  public List<Phase> findAll();

  public Phase findCycle(String cylce, int year);


  /**
   * This method gets a phase object by a given phase identifier.
   * 
   * @param phaseID is the phase identifier.
   * @return a Phase object.
   */
  public Phase getPhaseById(long phaseID);

  /**
   * This method saves the information of the given phase
   * 
   * @param phase - is the phase object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the phase was
   *         updated
   *         or -1 is some error occurred.
   */
  public long savePhase(Phase phase);


}
