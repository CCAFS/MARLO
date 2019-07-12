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

import org.cgiar.ccafs.marlo.data.model.RepIndStageStudy;

import java.util.List;


public interface RepIndStageStudyDAO {

  /**
   * This method removes a specific repIndStageStudy value from the database.
   * 
   * @param repIndStageStudyId is the repIndStageStudy identifier.
   * @return true if the repIndStageStudy was successfully deleted, false otherwise.
   */
  public void deleteRepIndStageStudy(long repIndStageStudyId);

  /**
   * This method validate if the repIndStageStudy identify with the given id exists in the system.
   * 
   * @param repIndStageStudyID is a repIndStageStudy identifier.
   * @return true if the repIndStageStudy exists, false otherwise.
   */
  public boolean existRepIndStageStudy(long repIndStageStudyID);

  /**
   * This method gets a repIndStageStudy object by a given repIndStageStudy identifier.
   * 
   * @param repIndStageStudyID is the repIndStageStudy identifier.
   * @return a RepIndStageStudy object.
   */
  public RepIndStageStudy find(long id);

  /**
   * This method gets a list of repIndStageStudy that are active
   * 
   * @return a list from RepIndStageStudy null if no exist records
   */
  public List<RepIndStageStudy> findAll();


  /**
   * This method saves the information of the given repIndStageStudy
   * 
   * @param repIndStageStudy - is the repIndStageStudy object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the repIndStageStudy was
   *         updated
   *         or -1 is some error occurred.
   */
  public RepIndStageStudy save(RepIndStageStudy repIndStageStudy);
}
