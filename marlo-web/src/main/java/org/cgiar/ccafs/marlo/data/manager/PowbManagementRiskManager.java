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

import org.cgiar.ccafs.marlo.data.model.PowbManagementRisk;

import java.util.List;


/**
 * @author Christian Garcia
 */

public interface PowbManagementRiskManager {


  /**
   * This method removes a specific powbManagementRisk value from the database.
   * 
   * @param powbManagementRiskId is the powbManagementRisk identifier.
   * @return true if the powbManagementRisk was successfully deleted, false otherwise.
   */
  public void deletePowbManagementRisk(long powbManagementRiskId);


  /**
   * This method validate if the powbManagementRisk identify with the given id exists in the system.
   * 
   * @param powbManagementRiskID is a powbManagementRisk identifier.
   * @return true if the powbManagementRisk exists, false otherwise.
   */
  public boolean existPowbManagementRisk(long powbManagementRiskID);


  /**
   * This method gets a list of powbManagementRisk that are active
   * 
   * @return a list from PowbManagementRisk null if no exist records
   */
  public List<PowbManagementRisk> findAll();


  /**
   * This method gets a powbManagementRisk object by a given powbManagementRisk identifier.
   * 
   * @param powbManagementRiskID is the powbManagementRisk identifier.
   * @return a PowbManagementRisk object.
   */
  public PowbManagementRisk getPowbManagementRiskById(long powbManagementRiskID);

  /**
   * This method saves the information of the given powbManagementRisk
   * 
   * @param powbManagementRisk - is the powbManagementRisk object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the powbManagementRisk was
   *         updated
   *         or -1 is some error occurred.
   */
  public PowbManagementRisk savePowbManagementRisk(PowbManagementRisk powbManagementRisk);


}
