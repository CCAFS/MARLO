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

import org.cgiar.ccafs.marlo.data.model.RepIndDegreeInnovation;

import java.util.List;


public interface RepIndDegreeInnovationDAO {

  /**
   * This method removes a specific repIndDegreeInnovation value from the database.
   * 
   * @param repIndDegreeInnovationId is the repIndDegreeInnovation identifier.
   * @return true if the repIndDegreeInnovation was successfully deleted, false otherwise.
   */
  public void deleteRepIndDegreeInnovation(long repIndDegreeInnovationId);

  /**
   * This method validate if the repIndDegreeInnovation identify with the given id exists in the system.
   * 
   * @param repIndDegreeInnovationID is a repIndDegreeInnovation identifier.
   * @return true if the repIndDegreeInnovation exists, false otherwise.
   */
  public boolean existRepIndDegreeInnovation(long repIndDegreeInnovationID);

  /**
   * This method gets a repIndDegreeInnovation object by a given repIndDegreeInnovation identifier.
   * 
   * @param repIndDegreeInnovationID is the repIndDegreeInnovation identifier.
   * @return a RepIndDegreeInnovation object.
   */
  public RepIndDegreeInnovation find(long id);

  /**
   * This method gets a list of repIndDegreeInnovation that are active
   * 
   * @return a list from RepIndDegreeInnovation null if no exist records
   */
  public List<RepIndDegreeInnovation> findAll();


  /**
   * This method saves the information of the given repIndDegreeInnovation
   * 
   * @param repIndDegreeInnovation - is the repIndDegreeInnovation object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the repIndDegreeInnovation was
   *         updated
   *         or -1 is some error occurred.
   */
  public RepIndDegreeInnovation save(RepIndDegreeInnovation repIndDegreeInnovation);
}
