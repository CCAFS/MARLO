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

import org.cgiar.ccafs.marlo.data.model.CrpProgramOutcomeIndicator;

import java.util.List;


public interface CrpProgramOutcomeIndicatorDAO {

  /**
   * This method removes a specific crpProgramOutcomeIndicator value from the database.
   * 
   * @param crpProgramOutcomeIndicatorId is the crpProgramOutcomeIndicator identifier.
   * @return true if the crpProgramOutcomeIndicator was successfully deleted, false otherwise.
   */
  public void deleteCrpProgramOutcomeIndicator(long crpProgramOutcomeIndicatorId);

  /**
   * This method validate if the crpProgramOutcomeIndicator identify with the given id exists in the system.
   * 
   * @param crpProgramOutcomeIndicatorID is a crpProgramOutcomeIndicator identifier.
   * @return true if the crpProgramOutcomeIndicator exists, false otherwise.
   */
  public boolean existCrpProgramOutcomeIndicator(long crpProgramOutcomeIndicatorID);

  /**
   * This method gets a crpProgramOutcomeIndicator object by a given crpProgramOutcomeIndicator identifier.
   * 
   * @param crpProgramOutcomeIndicatorID is the crpProgramOutcomeIndicator identifier.
   * @return a CrpProgramOutcomeIndicator object.
   */
  public CrpProgramOutcomeIndicator find(long id);

  /**
   * This method gets a list of crpProgramOutcomeIndicator that are active
   * 
   * @return a list from CrpProgramOutcomeIndicator null if no exist records
   */
  public List<CrpProgramOutcomeIndicator> findAll();


  /**
   * This method saves the information of the given crpProgramOutcomeIndicator
   * 
   * @param crpProgramOutcomeIndicator - is the crpProgramOutcomeIndicator object with the new information to be
   *        added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the
   *         crpProgramOutcomeIndicator was
   *         updated
   *         or -1 is some error occurred.
   */
  public CrpProgramOutcomeIndicator save(CrpProgramOutcomeIndicator crpProgramOutcomeIndicator);
}
