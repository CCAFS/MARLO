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

import org.cgiar.ccafs.marlo.data.model.PowbIndMilestoneRisk;

import java.util.List;


/**
 * @author CCAFS
 */

public interface PowbIndMilestoneRiskManager {


  /**
   * This method removes a specific powbIndMilestoneRisk value from the database.
   * 
   * @param powbIndMilestoneRiskId is the powbIndMilestoneRisk identifier.
   * @return true if the powbIndMilestoneRisk was successfully deleted, false otherwise.
   */
  public void deletePowbIndMilestoneRisk(long powbIndMilestoneRiskId);


  /**
   * This method validate if the powbIndMilestoneRisk identify with the given id exists in the system.
   * 
   * @param powbIndMilestoneRiskID is a powbIndMilestoneRisk identifier.
   * @return true if the powbIndMilestoneRisk exists, false otherwise.
   */
  public boolean existPowbIndMilestoneRisk(long powbIndMilestoneRiskID);


  /**
   * This method gets a list of powbIndMilestoneRisk that are active
   * 
   * @return a list from PowbIndMilestoneRisk null if no exist records
   */
  public List<PowbIndMilestoneRisk> findAll();


  /**
   * This method gets a powbIndMilestoneRisk object by a given powbIndMilestoneRisk identifier.
   * 
   * @param powbIndMilestoneRiskID is the powbIndMilestoneRisk identifier.
   * @return a PowbIndMilestoneRisk object.
   */
  public PowbIndMilestoneRisk getPowbIndMilestoneRiskById(long powbIndMilestoneRiskID);

  /**
   * This method saves the information of the given powbIndMilestoneRisk
   * 
   * @param powbIndMilestoneRisk - is the powbIndMilestoneRisk object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the powbIndMilestoneRisk was
   *         updated
   *         or -1 is some error occurred.
   */
  public PowbIndMilestoneRisk savePowbIndMilestoneRisk(PowbIndMilestoneRisk powbIndMilestoneRisk);


}
