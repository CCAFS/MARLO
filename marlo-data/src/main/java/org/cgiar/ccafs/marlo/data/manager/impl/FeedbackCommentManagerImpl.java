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
package org.cgiar.ccafs.marlo.data.manager.impl;


import org.cgiar.ccafs.marlo.data.dao.FeedbackCommentDAO;
import org.cgiar.ccafs.marlo.data.manager.FeedbackCommentManager;
import org.cgiar.ccafs.marlo.data.model.FeedbackQAReply;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class FeedbackCommentManagerImpl implements FeedbackCommentManager {


  private FeedbackCommentDAO feedbackCommentDAO;
  // Managers


  @Inject
  public FeedbackCommentManagerImpl(FeedbackCommentDAO feedbackCommentDAO) {
    this.feedbackCommentDAO = feedbackCommentDAO;


  }

  @Override
  public void deleteFeedbackComment(long feedbackCommentId) {

    feedbackCommentDAO.deleteFeedbackComment(feedbackCommentId);
  }

  @Override
  public boolean existFeedbackComment(long feedbackCommentID) {

    return feedbackCommentDAO.existFeedbackComment(feedbackCommentID);
  }

  @Override
  public List<FeedbackQAReply> findAll() {

    return feedbackCommentDAO.findAll();

  }

  @Override
  public FeedbackQAReply getFeedbackCommentById(long feedbackCommentID) {

    return feedbackCommentDAO.find(feedbackCommentID);
  }

  @Override
  public FeedbackQAReply saveFeedbackComment(FeedbackQAReply feedbackQAReply) {

    return feedbackCommentDAO.save(feedbackQAReply);
  }


}
