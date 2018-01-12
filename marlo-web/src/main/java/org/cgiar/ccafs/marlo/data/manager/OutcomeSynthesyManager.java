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

import org.cgiar.ccafs.marlo.data.model.OutcomeSynthesy;

import java.util.List;


/**
 * @author Christian Garcia
 */

public interface OutcomeSynthesyManager {


  /**
   * This method removes a specific outcomeSynthesy value from the database.
   * 
   * @param outcomeSynthesyId is the outcomeSynthesy identifier.
   * @return true if the outcomeSynthesy was successfully deleted, false otherwise.
   */
  public void deleteOutcomeSynthesy(long outcomeSynthesyId);


  /**
   * This method validate if the outcomeSynthesy identify with the given id exists in the system.
   * 
   * @param outcomeSynthesyID is a outcomeSynthesy identifier.
   * @return true if the outcomeSynthesy exists, false otherwise.
   */
  public boolean existOutcomeSynthesy(long outcomeSynthesyID);


  /**
   * This method gets a list of outcomeSynthesy that are active
   * 
   * @return a list from OutcomeSynthesy null if no exist records
   */
  public List<OutcomeSynthesy> findAll();


  /**
   * This method gets a outcomeSynthesy object by a given outcomeSynthesy identifier.
   * 
   * @param outcomeSynthesyID is the outcomeSynthesy identifier.
   * @return a OutcomeSynthesy object.
   */
  public OutcomeSynthesy getOutcomeSynthesyById(long outcomeSynthesyID);

  /**
   * This method saves the information of the given outcomeSynthesy
   * 
   * @param outcomeSynthesy - is the outcomeSynthesy object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the outcomeSynthesy was
   *         updated
   *         or -1 is some error occurred.
   */
  public OutcomeSynthesy saveOutcomeSynthesy(OutcomeSynthesy outcomeSynthesy);


}
