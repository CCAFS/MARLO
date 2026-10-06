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
import org.cgiar.ccafs.marlo.data.model.EmailLogSearch;

import java.util.List;


/**
 * @author Christian Garcia
 */

public interface EmailLogManager {

  /**
   * Counts the logged emails that match the given filters.
   *
   * @param search the filters; a null field does not filter.
   * @return the number of matching rows.
   */
  public long count(EmailLogSearch search);

  /**
   * @return the distinct global unit ids the logged emails were sent for.
   */
  public List<Long> findGlobalUnitIds();

  /**
   * @return the distinct places the logged emails were sent from, in alphabetical order.
   */
  public List<String> findSourceActions();

  /**
   * Lists one page of the logged emails that match the given filters, without the message or the attachment.
   *
   * @param search the filters and the order; a null field does not filter.
   * @param first the position of the first row, from 0.
   * @param max the number of rows.
   * @return detached EmailLog objects holding only the columns of the table.
   */
  public List<EmailLog> findSummaries(EmailLogSearch search, int first, int max);

  /**
   * Lists every logged email that matches the given filters, message and attachment included.
   *
   * @param search the filters; a null field does not filter.
   * @return the matching rows.
   */
  public List<EmailLog> search(EmailLogSearch search);


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
