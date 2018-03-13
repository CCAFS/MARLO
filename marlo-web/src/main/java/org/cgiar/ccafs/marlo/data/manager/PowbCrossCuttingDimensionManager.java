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

import org.cgiar.ccafs.marlo.data.model.CrossCuttingDimensionTableDTO;
import org.cgiar.ccafs.marlo.data.model.PowbCrossCuttingDimension;

import java.util.List;


/**
 * @author Christian Garcia
 */

public interface PowbCrossCuttingDimensionManager {


  /**
   * This method removes a specific powbCrossCuttingDimension value from the database.
   * 
   * @param powbCrossCuttingDimensionId is the powbCrossCuttingDimension identifier.
   * @return true if the powbCrossCuttingDimension was successfully deleted, false otherwise.
   */
  public void deletePowbCrossCuttingDimension(long powbCrossCuttingDimensionId);


  /**
   * This method validate if the powbCrossCuttingDimension identify with the given id exists in the system.
   * 
   * @param powbCrossCuttingDimensionID is a powbCrossCuttingDimension identifier.
   * @return true if the powbCrossCuttingDimension exists, false otherwise.
   */
  public boolean existPowbCrossCuttingDimension(long powbCrossCuttingDimensionID);


  /**
   * This method gets a list of powbCrossCuttingDimension that are active
   * 
   * @return a list from PowbCrossCuttingDimension null if no exist records
   */
  public List<PowbCrossCuttingDimension> findAll();


  /**
   * This method gets a powbCrossCuttingDimension object by a given powbCrossCuttingDimension identifier.
   * 
   * @param powbCrossCuttingDimensionID is the powbCrossCuttingDimension identifier.
   * @return a PowbCrossCuttingDimension object.
   */
  public PowbCrossCuttingDimension getPowbCrossCuttingDimensionById(long powbCrossCuttingDimensionID);

  public CrossCuttingDimensionTableDTO loadTableByLiaisonAndPhase(Long liaisonInstitution, Long phaseId);

  /**
   * This method saves the information of the given powbCrossCuttingDimension
   * 
   * @param powbCrossCuttingDimension - is the powbCrossCuttingDimension object with the new information to be
   *        added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the
   *         powbCrossCuttingDimension was
   *         updated
   *         or -1 is some error occurred.
   */
  public PowbCrossCuttingDimension savePowbCrossCuttingDimension(PowbCrossCuttingDimension powbCrossCuttingDimension);


}
