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

import org.cgiar.ccafs.marlo.data.model.RepIndStageInnovation;

import java.util.List;


public interface RepIndStageInnovationDAO {

  /**
   * This method removes a specific repIndStageInnovation value from the database.
   * 
   * @param repIndStageInnovationId is the repIndStageInnovation identifier.
   * @return true if the repIndStageInnovation was successfully deleted, false otherwise.
   */
  public void deleteRepIndStageInnovation(long repIndStageInnovationId);

  /**
   * This method validate if the repIndStageInnovation identify with the given id exists in the system.
   * 
   * @param repIndStageInnovationID is a repIndStageInnovation identifier.
   * @return true if the repIndStageInnovation exists, false otherwise.
   */
  public boolean existRepIndStageInnovation(long repIndStageInnovationID);

  /**
   * This method gets a repIndStageInnovation object by a given repIndStageInnovation identifier.
   * 
   * @param repIndStageInnovationID is the repIndStageInnovation identifier.
   * @return a RepIndStageInnovation object.
   */
  public RepIndStageInnovation find(long id);

  /**
   * This method gets a list of repIndStageInnovation that are active
   * 
   * @return a list from RepIndStageInnovation null if no exist records
   */
  public List<RepIndStageInnovation> findAll();


  /**
   * This method saves the information of the given repIndStageInnovation
   * 
   * @param repIndStageInnovation - is the repIndStageInnovation object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the repIndStageInnovation was
   *         updated
   *         or -1 is some error occurred.
   */
  public RepIndStageInnovation save(RepIndStageInnovation repIndStageInnovation);
}
