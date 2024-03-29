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

import org.cgiar.ccafs.marlo.data.model.CrpClusterOfActivity;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.Phase;

import java.util.List;


/**
 * @author Christian Garcia
 */

public interface CrpClusterOfActivityManager {


  /**
   * This method removes a specific crpClusterOfActivity value from the database.
   * 
   * @param crpClusterOfActivityId is the crpClusterOfActivity identifier.
   * @return true if the crpClusterOfActivity was successfully deleted, false otherwise.
   */
  public void deleteCrpClusterOfActivity(long crpClusterOfActivityId);


  /**
   * This method validate if the crpClusterOfActivity identify with the given id exists in the system.
   * 
   * @param crpClusterOfActivityID is a crpClusterOfActivity identifier.
   * @return true if the crpClusterOfActivity exists, false otherwise.
   */
  public boolean existCrpClusterOfActivity(long crpClusterOfActivityID);


  /**
   * This method gets a list of crpClusterOfActivity that are active
   * 
   * @return a list from CrpClusterOfActivity null if no exist records
   */
  public List<CrpClusterOfActivity> findAll();

  /**
   * This method gets a list of crpClusterOfActivity that are active for a program and phase
   * 
   * @param crpProgram the program to filter clusters
   * @param phase the phase to filter clusters
   * @return a list from CrpClusterOfActivity null if no exist records
   */
  public List<CrpClusterOfActivity> findClusterProgramPhase(CrpProgram crpProgram, Phase phase);


  /**
   * This method gets a crpClusterOfActivity object by a given crpClusterOfActivity identifier.
   * 
   * @param crpClusterOfActivityID is the crpClusterOfActivity identifier.
   * @return a CrpClusterOfActivity object.
   */
  public CrpClusterOfActivity getCrpClusterOfActivityById(long crpClusterOfActivityID);

  public CrpClusterOfActivity getCrpClusterOfActivityByIdentifierPhase(String crpClusterOfActivityIdentefier,
    Phase phase);

  /**
   * This method saves the information of the given crpClusterOfActivity
   * 
   * @param crpClusterOfActivity - is the crpClusterOfActivity object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the crpClusterOfActivity was
   *         updated
   *         or -1 is some error occurred.
   */
  public CrpClusterOfActivity saveCrpClusterOfActivity(CrpClusterOfActivity crpClusterOfActivity);


}
