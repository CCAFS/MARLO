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

import org.cgiar.ccafs.marlo.data.model.PowbManagementGovernance;

import java.util.List;


/**
 * @author Christian Garcia
 */

public interface PowbManagementGovernanceManager {


  /**
   * This method removes a specific powbManagementGovernance value from the database.
   * 
   * @param powbManagementGovernanceId is the powbManagementGovernance identifier.
   * @return true if the powbManagementGovernance was successfully deleted, false otherwise.
   */
  public void deletePowbManagementGovernance(long powbManagementGovernanceId);


  /**
   * This method validate if the powbManagementGovernance identify with the given id exists in the system.
   * 
   * @param powbManagementGovernanceID is a powbManagementGovernance identifier.
   * @return true if the powbManagementGovernance exists, false otherwise.
   */
  public boolean existPowbManagementGovernance(long powbManagementGovernanceID);


  /**
   * This method gets a list of powbManagementGovernance that are active
   * 
   * @return a list from PowbManagementGovernance null if no exist records
   */
  public List<PowbManagementGovernance> findAll();


  /**
   * This method gets a powbManagementGovernance object by a given powbManagementGovernance identifier.
   * 
   * @param powbManagementGovernanceID is the powbManagementGovernance identifier.
   * @return a PowbManagementGovernance object.
   */
  public PowbManagementGovernance getPowbManagementGovernanceById(long powbManagementGovernanceID);

  /**
   * This method saves the information of the given powbManagementGovernance
   * 
   * @param powbManagementGovernance - is the powbManagementGovernance object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the powbManagementGovernance was
   *         updated
   *         or -1 is some error occurred.
   */
  public PowbManagementGovernance savePowbManagementGovernance(PowbManagementGovernance powbManagementGovernance);


}
