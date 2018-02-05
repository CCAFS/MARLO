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

import org.cgiar.ccafs.marlo.data.model.PowbEvidence;

import java.util.List;


/**
 * @author Christian Garcia
 */

public interface PowbEvidenceManager {


  /**
   * This method removes a specific powbEvidence value from the database.
   * 
   * @param powbEvidenceId is the powbEvidence identifier.
   * @return true if the powbEvidence was successfully deleted, false otherwise.
   */
  public void deletePowbEvidence(long powbEvidenceId);


  /**
   * This method validate if the powbEvidence identify with the given id exists in the system.
   * 
   * @param powbEvidenceID is a powbEvidence identifier.
   * @return true if the powbEvidence exists, false otherwise.
   */
  public boolean existPowbEvidence(long powbEvidenceID);


  /**
   * This method gets a list of powbEvidence that are active
   * 
   * @return a list from PowbEvidence null if no exist records
   */
  public List<PowbEvidence> findAll();


  /**
   * This method gets a powbEvidence object by a given powbEvidence identifier.
   * 
   * @param powbEvidenceID is the powbEvidence identifier.
   * @return a PowbEvidence object.
   */
  public PowbEvidence getPowbEvidenceById(long powbEvidenceID);

  /**
   * This method saves the information of the given powbEvidence
   * 
   * @param powbEvidence - is the powbEvidence object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the powbEvidence was
   *         updated
   *         or -1 is some error occurred.
   */
  public PowbEvidence savePowbEvidence(PowbEvidence powbEvidence);


}
