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

import org.cgiar.ccafs.marlo.data.model.EmailLog;

import java.util.List;


/**
 * @author Christian Garcia
 */

public interface EmailLogManager {


  /**
   * This method removes a specific emailLog value from the database.
   * 
   * @param emailLogId is the emailLog identifier.
   * @return true if the emailLog was successfully deleted, false otherwise.
   */
  public void deleteEmailLog(long emailLogId);


  /**
   * This method validate if the emailLog identify with the given id exists in the system.
   * 
   * @param emailLogID is a emailLog identifier.
   * @return true if the emailLog exists, false otherwise.
   */
  public boolean existEmailLog(long emailLogID);


  /**
   * This method gets a list of emailLog that are active
   * 
   * @return a list from EmailLog null if no exist records
   */
  public List<EmailLog> findAll();


  /**
   * This method gets a emailLog object by a given emailLog identifier.
   * 
   * @param emailLogID is the emailLog identifier.
   * @return a EmailLog object.
   */
  public EmailLog getEmailLogById(long emailLogID);

  /**
   * This method saves the information of the given emailLog
   * 
   * @param emailLog - is the emailLog object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the emailLog was
   *         updated
   *         or -1 is some error occurred.
   */
  public EmailLog saveEmailLog(EmailLog emailLog);


}
