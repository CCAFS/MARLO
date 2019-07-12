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


package org.cgiar.ccafs.marlo.data.dao;

import org.cgiar.ccafs.marlo.data.model.EvidenceTag;

import java.util.List;


public interface EvidenceTagDAO {

  /**
   * This method removes a specific evidenceTag value from the database.
   * 
   * @param evidenceTagId is the evidenceTag identifier.
   * @return true if the evidenceTag was successfully deleted, false otherwise.
   */
  public void deleteEvidenceTag(long evidenceTagId);

  /**
   * This method validate if the evidenceTag identify with the given id exists in the system.
   * 
   * @param evidenceTagID is a evidenceTag identifier.
   * @return true if the evidenceTag exists, false otherwise.
   */
  public boolean existEvidenceTag(long evidenceTagID);

  /**
   * This method gets a evidenceTag object by a given evidenceTag identifier.
   * 
   * @param evidenceTagID is the evidenceTag identifier.
   * @return a EvidenceTag object.
   */
  public EvidenceTag find(long id);

  /**
   * This method gets a list of evidenceTag that are active
   * 
   * @return a list from EvidenceTag null if no exist records
   */
  public List<EvidenceTag> findAll();


  /**
   * This method saves the information of the given evidenceTag
   * 
   * @param evidenceTag - is the evidenceTag object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the evidenceTag was
   *         updated
   *         or -1 is some error occurred.
   */
  public EvidenceTag save(EvidenceTag evidenceTag);
}
