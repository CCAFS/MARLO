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

import org.cgiar.ccafs.marlo.data.model.ProgressTargetCaseGeographicScope;

import java.util.List;


/**
 * @author CCAFS
 */

public interface ProgressTargetCaseGeographicScopeManager {


  /**
   * This method removes a specific progressTargetCaseGeographicScope value from the database.
   * 
   * @param progressTargetCaseGeographicScopeId is the progressTargetCaseGeographicScope identifier.
   * @return true if the progressTargetCaseGeographicScope was successfully deleted, false otherwise.
   */
  public void deleteProgressTargetCaseGeographicScope(long progressTargetCaseGeographicScopeId);


  /**
   * This method validate if the progressTargetCaseGeographicScope identify with the given id exists in the system.
   * 
   * @param progressTargetCaseGeographicScopeID is a progressTargetCaseGeographicScope identifier.
   * @return true if the progressTargetCaseGeographicScope exists, false otherwise.
   */
  public boolean existProgressTargetCaseGeographicScope(long progressTargetCaseGeographicScopeID);


  /**
   * This method gets a list of progressTargetCaseGeographicScope that are active
   * 
   * @return a list from ProgressTargetCaseGeographicScope null if no exist records
   */
  public List<ProgressTargetCaseGeographicScope> findAll();

  public List<ProgressTargetCaseGeographicScope> findGeographicScopeByTargetCase(long targetCaseID);


  /**
   * This method gets a progressTargetCaseGeographicScope object by a given progressTargetCaseGeographicScope
   * identifier.
   * 
   * @param progressTargetCaseGeographicScopeID is the progressTargetCaseGeographicScope identifier.
   * @return a ProgressTargetCaseGeographicScope object.
   */
  public ProgressTargetCaseGeographicScope
    getProgressTargetCaseGeographicScopeById(long progressTargetCaseGeographicScopeID);

  /**
   * This method saves the information of the given progressTargetCaseGeographicScope
   * 
   * @param progressTargetCaseGeographicScope - is the progressTargetCaseGeographicScope object with the new information
   *        to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the
   *         progressTargetCaseGeographicScope was
   *         updated
   *         or -1 is some error occurred.
   */
  public ProgressTargetCaseGeographicScope
    saveProgressTargetCaseGeographicScope(ProgressTargetCaseGeographicScope progressTargetCaseGeographicScope);


}
