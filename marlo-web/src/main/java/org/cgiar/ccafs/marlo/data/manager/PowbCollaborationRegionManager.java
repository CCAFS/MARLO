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

import org.cgiar.ccafs.marlo.data.model.PowbCollaborationRegion;

import java.util.List;


/**
 * @author Christian Garcia
 */

public interface PowbCollaborationRegionManager {


  /**
   * This method removes a specific powbCollaborationRegion value from the database.
   * 
   * @param powbCollaborationRegionId is the powbCollaborationRegion identifier.
   * @return true if the powbCollaborationRegion was successfully deleted, false otherwise.
   */
  public void deletePowbCollaborationRegion(long powbCollaborationRegionId);


  /**
   * This method validate if the powbCollaborationRegion identify with the given id exists in the system.
   * 
   * @param powbCollaborationRegionID is a powbCollaborationRegion identifier.
   * @return true if the powbCollaborationRegion exists, false otherwise.
   */
  public boolean existPowbCollaborationRegion(long powbCollaborationRegionID);


  /**
   * This method gets a list of powbCollaborationRegion that are active
   * 
   * @return a list from PowbCollaborationRegion null if no exist records
   */
  public List<PowbCollaborationRegion> findAll();


  /**
   * This method gets a powbCollaborationRegion object by a given powbCollaborationRegion identifier.
   * 
   * @param powbCollaborationRegionID is the powbCollaborationRegion identifier.
   * @return a PowbCollaborationRegion object.
   */
  public PowbCollaborationRegion getPowbCollaborationRegionById(long powbCollaborationRegionID);

  /**
   * This method saves the information of the given powbCollaborationRegion
   * 
   * @param powbCollaborationRegion - is the powbCollaborationRegion object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the powbCollaborationRegion was
   *         updated
   *         or -1 is some error occurred.
   */
  public PowbCollaborationRegion savePowbCollaborationRegion(PowbCollaborationRegion powbCollaborationRegion);


}
