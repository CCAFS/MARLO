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

import org.cgiar.ccafs.marlo.data.model.FeedbackQAReply;

import java.util.List;


/**
 * @author CCAFS
 */

public interface FeedbackCommentManager {


  /**
   * This method removes a specific feedbackComment value from the database.
   * 
   * @param feedbackCommentId is the feedbackComment identifier.
   * @return true if the feedbackComment was successfully deleted, false otherwise.
   */
  public void deleteFeedbackComment(long feedbackCommentId);


  /**
   * This method validate if the feedbackComment identify with the given id exists in the system.
   * 
   * @param feedbackCommentID is a feedbackComment identifier.
   * @return true if the feedbackComment exists, false otherwise.
   */
  public boolean existFeedbackComment(long feedbackCommentID);


  /**
   * This method gets a list of feedbackComment that are active
   * 
   * @return a list from FeedbackQAReply null if no exist records
   */
  public List<FeedbackQAReply> findAll();


  /**
   * This method gets a feedbackComment object by a given feedbackComment identifier.
   * 
   * @param feedbackCommentID is the feedbackComment identifier.
   * @return a FeedbackQAReply object.
   */
  public FeedbackQAReply getFeedbackCommentById(long feedbackCommentID);

  /**
   * This method saves the information of the given feedbackComment
   * 
   * @param feedbackQAReply - is the feedbackComment object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the feedbackComment was
   *         updated
   *         or -1 is some error occurred.
   */
  public FeedbackQAReply saveFeedbackComment(FeedbackQAReply feedbackQAReply);


}
