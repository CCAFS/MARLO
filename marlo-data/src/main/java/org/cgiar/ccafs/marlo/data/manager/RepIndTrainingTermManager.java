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

import org.cgiar.ccafs.marlo.data.model.RepIndTrainingTerm;

import java.util.List;


/**
 * @author CCAFS
 */

public interface RepIndTrainingTermManager {


  /**
   * This method removes a specific repIndTrainingTerm value from the database.
   * 
   * @param repIndTrainingTermId is the repIndTrainingTerm identifier.
   * @return true if the repIndTrainingTerm was successfully deleted, false otherwise.
   */
  public void deleteRepIndTrainingTerm(long repIndTrainingTermId);


  /**
   * This method validate if the repIndTrainingTerm identify with the given id exists in the system.
   * 
   * @param repIndTrainingTermID is a repIndTrainingTerm identifier.
   * @return true if the repIndTrainingTerm exists, false otherwise.
   */
  public boolean existRepIndTrainingTerm(long repIndTrainingTermID);


  /**
   * This method gets a list of repIndTrainingTerm that are active
   * 
   * @return a list from RepIndTrainingTerm null if no exist records
   */
  public List<RepIndTrainingTerm> findAll();


  /**
   * This method gets a repIndTrainingTerm object by a given repIndTrainingTerm identifier.
   * 
   * @param repIndTrainingTermID is the repIndTrainingTerm identifier.
   * @return a RepIndTrainingTerm object.
   */
  public RepIndTrainingTerm getRepIndTrainingTermById(long repIndTrainingTermID);

  /**
   * This method saves the information of the given repIndTrainingTerm
   * 
   * @param repIndTrainingTerm - is the repIndTrainingTerm object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the repIndTrainingTerm was
   *         updated
   *         or -1 is some error occurred.
   */
  public RepIndTrainingTerm saveRepIndTrainingTerm(RepIndTrainingTerm repIndTrainingTerm);


}
