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


import org.cgiar.ccafs.marlo.data.dao.FeedbackQAReplyDAO;
import org.cgiar.ccafs.marlo.data.manager.FeedbackQAReplyManager;
import org.cgiar.ccafs.marlo.data.model.FeedbackQAReply;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class FeedbackQAReplyManagerImpl implements FeedbackQAReplyManager {


  private FeedbackQAReplyDAO feedbackQAReplyDAO;
  // Managers


  @Inject
  public FeedbackQAReplyManagerImpl(FeedbackQAReplyDAO feedbackQAReplyDAO) {
    this.feedbackQAReplyDAO = feedbackQAReplyDAO;


  }

  @Override
  public void deleteFeedbackComment(long feedbackCommentId) {

    feedbackQAReplyDAO.deleteFeedbackComment(feedbackCommentId);
  }

  @Override
  public boolean existFeedbackComment(long feedbackCommentID) {

    return feedbackQAReplyDAO.existFeedbackComment(feedbackCommentID);
  }

  @Override
  public List<FeedbackQAReply> findAll() {

    return feedbackQAReplyDAO.findAll();

  }

  @Override
  public FeedbackQAReply getFeedbackCommentById(long feedbackCommentID) {

    return feedbackQAReplyDAO.find(feedbackCommentID);
  }

  @Override
  public FeedbackQAReply saveFeedbackComment(FeedbackQAReply feedbackQAReply) {

    return feedbackQAReplyDAO.save(feedbackQAReply);
  }


}
