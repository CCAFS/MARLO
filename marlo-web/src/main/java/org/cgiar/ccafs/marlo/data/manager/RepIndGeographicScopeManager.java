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

import org.cgiar.ccafs.marlo.data.model.ProjectPartnerPartnership;
import org.cgiar.ccafs.marlo.data.model.RepIndGeographicScope;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisPartnershipsByGeographicScopeDTO;

import java.util.List;


/**
 * @author Christian Garcia
 */

public interface RepIndGeographicScopeManager {


  /**
   * This method removes a specific repIndGeographicScope value from the database.
   * 
   * @param repIndGeographicScopeId is the repIndGeographicScope identifier.
   * @return true if the repIndGeographicScope was successfully deleted, false otherwise.
   */
  public void deleteRepIndGeographicScope(long repIndGeographicScopeId);


  /**
   * This method validate if the repIndGeographicScope identify with the given id exists in the system.
   * 
   * @param repIndGeographicScopeID is a repIndGeographicScope identifier.
   * @return true if the repIndGeographicScope exists, false otherwise.
   */
  public boolean existRepIndGeographicScope(long repIndGeographicScopeID);


  /**
   * This method gets a list of repIndGeographicScope that are active
   * 
   * @return a list from RepIndGeographicScope null if no exist records
   */
  public List<RepIndGeographicScope> findAll();


  /**
   * This method gets a list of ReportSynthesisPartnershipsByGeographicScopeDTO that are active:
   * List of Partnerships grouped by Geographic Scope
   * 
   * @return a list from ReportSynthesisPartnershipsByGeographicScopeDTO null if no exist records
   */
  public List<ReportSynthesisPartnershipsByGeographicScopeDTO>
    getPartnershipsByGeographicScopeDTO(List<ProjectPartnerPartnership> projectPartnerPartnerships);

  /**
   * This method gets a repIndGeographicScope object by a given repIndGeographicScope identifier.
   * 
   * @param repIndGeographicScopeID is the repIndGeographicScope identifier.
   * @return a RepIndGeographicScope object.
   */
  public RepIndGeographicScope getRepIndGeographicScopeById(long repIndGeographicScopeID);

  /**
   * This method saves the information of the given repIndGeographicScope
   * 
   * @param repIndGeographicScope - is the repIndGeographicScope object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the repIndGeographicScope
   *         was
   *         updated
   *         or -1 is some error occurred.
   */
  public RepIndGeographicScope saveRepIndGeographicScope(RepIndGeographicScope repIndGeographicScope);
}
