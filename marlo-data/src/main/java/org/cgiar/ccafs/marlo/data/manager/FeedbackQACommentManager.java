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

import org.cgiar.ccafs.marlo.data.model.FeedbackQAComment;

import java.util.List;


/**
 * @author CCAFS
 */

public interface FeedbackQACommentManager {


  /**
   * This method removes a specific feedbackQAComment value from the database.
   * 
   * @param feedbackQACommentId is the feedbackQAComment identifier.
   * @return true if the feedbackQAComment was successfully deleted, false otherwise.
   */
  public void deleteFeedbackQAComment(long feedbackQACommentId);


  /**
   * This method validate if the feedbackQAComment identify with the given id exists in the system.
   * 
   * @param feedbackQACommentID is a feedbackQAComment identifier.
   * @return true if the feedbackQAComment exists, false otherwise.
   */
  public boolean existFeedbackQAComment(long feedbackQACommentID);


  /**
   * This method gets a list of feedbackQAComment that are active
   * 
   * @return a list from FeedbackQAComment null if no exist records
   */
  public List<FeedbackQAComment> findAll();


  /**
   * This method gets a feedbackQAComment object by a given feedbackQAComment identifier.
   * 
   * @param feedbackQACommentID is the feedbackQAComment identifier.
   * @return a FeedbackQAComment object.
   */
  public FeedbackQAComment getFeedbackQACommentById(long feedbackQACommentID);

  /**
   * This method saves the information of the given feedbackQAComment
   * 
   * @param feedbackQAComment - is the feedbackQAComment object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the feedbackQAComment was
   *         updated
   *         or -1 is some error occurred.
   */
  public FeedbackQAComment saveFeedbackQAComment(FeedbackQAComment feedbackQAComment);


}
