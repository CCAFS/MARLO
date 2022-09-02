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

import org.cgiar.ccafs.marlo.data.model.FeedbackStatus;

import java.util.List;


/**
 * @author CCAFS
 */

public interface FeedbackStatusManager {


  /**
   * This method removes a specific feedbackStatus value from the database.
   * 
   * @param feedbackStatusId is the feedbackStatus identifier.
   * @return true if the feedbackStatus was successfully deleted, false otherwise.
   */
  public void deleteFeedbackStatus(long feedbackStatusId);


  /**
   * This method validate if the feedbackStatus identify with the given id exists in the system.
   * 
   * @param feedbackStatusID is a feedbackStatus identifier.
   * @return true if the feedbackStatus exists, false otherwise.
   */
  public boolean existFeedbackStatus(long feedbackStatusID);


  /**
   * This method gets a list of feedbackStatus that are active
   * 
   * @return a list from FeedbackStatus null if no exist records
   */
  public List<FeedbackStatus> findAll();


  /**
   * This method gets a feedbackStatus object by a given feedbackStatus identifier.
   * 
   * @param feedbackStatusID is the feedbackStatus identifier.
   * @return a FeedbackStatus object.
   */
  public FeedbackStatus getFeedbackStatusById(long feedbackStatusID);

  /**
   * This method saves the information of the given feedbackStatus
   * 
   * @param feedbackStatus - is the feedbackStatus object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the feedbackStatus was
   *         updated
   *         or -1 is some error occurred.
   */
  public FeedbackStatus saveFeedbackStatus(FeedbackStatus feedbackStatus);


}
