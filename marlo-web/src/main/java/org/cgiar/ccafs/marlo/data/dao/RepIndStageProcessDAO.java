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

import org.cgiar.ccafs.marlo.data.model.RepIndStageProcess;

import java.util.List;


public interface RepIndStageProcessDAO {

  /**
   * This method removes a specific repIndStageProcess value from the database.
   * 
   * @param repIndStageProcessId is the repIndStageProcess identifier.
   * @return true if the repIndStageProcess was successfully deleted, false otherwise.
   */
  public void deleteRepIndStageProcess(long repIndStageProcessId);

  /**
   * This method validate if the repIndStageProcess identify with the given id exists in the system.
   * 
   * @param repIndStageProcessID is a repIndStageProcess identifier.
   * @return true if the repIndStageProcess exists, false otherwise.
   */
  public boolean existRepIndStageProcess(long repIndStageProcessID);

  /**
   * This method gets a repIndStageProcess object by a given repIndStageProcess identifier.
   * 
   * @param repIndStageProcessID is the repIndStageProcess identifier.
   * @return a RepIndStageProcess object.
   */
  public RepIndStageProcess find(long id);

  /**
   * This method gets a list of repIndStageProcess that are active
   * 
   * @return a list from RepIndStageProcess null if no exist records
   */
  public List<RepIndStageProcess> findAll();


  /**
   * This method saves the information of the given repIndStageProcess
   * 
   * @param repIndStageProcess - is the repIndStageProcess object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the repIndStageProcess was
   *         updated
   *         or -1 is some error occurred.
   */
  public RepIndStageProcess save(RepIndStageProcess repIndStageProcess);
}
