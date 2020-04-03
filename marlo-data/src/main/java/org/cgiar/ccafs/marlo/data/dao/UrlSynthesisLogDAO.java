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

import org.cgiar.ccafs.marlo.data.model.UrlSynthesisLog;

import java.util.List;


public interface UrlSynthesisLogDAO {

  /**
   * This method removes a specific urlSynthesisLog value from the database.
   * 
   * @param urlSynthesisLogId is the urlSynthesisLog identifier.
   * @return true if the urlSynthesisLog was successfully deleted, false otherwise.
   */
  public void deleteUrlSynthesisLog(long urlSynthesisLogId);

  /**
   * This method validate if the urlSynthesisLog identify with the given id exists in the system.
   * 
   * @param urlSynthesisLogID is a urlSynthesisLog identifier.
   * @return true if the urlSynthesisLog exists, false otherwise.
   */
  public boolean existUrlSynthesisLog(long urlSynthesisLogID);

  /**
   * This method gets a urlSynthesisLog object by a given urlSynthesisLog identifier.
   * 
   * @param urlSynthesisLogID is the urlSynthesisLog identifier.
   * @return a UrlSynthesisLog object.
   */
  public UrlSynthesisLog find(long id);

  /**
   * This method gets a list of urlSynthesisLog that are active
   * 
   * @return a list from UrlSynthesisLog null if no exist records
   */
  public List<UrlSynthesisLog> findAll();


  /**
   * This method saves the information of the given urlSynthesisLog
   * 
   * @param urlSynthesisLog - is the urlSynthesisLog object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the urlSynthesisLog was
   *         updated
   *         or -1 is some error occurred.
   */
  public UrlSynthesisLog save(UrlSynthesisLog urlSynthesisLog);
}
