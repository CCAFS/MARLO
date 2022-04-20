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


import org.cgiar.ccafs.marlo.data.dao.FeedbackQACommentDAO;
import org.cgiar.ccafs.marlo.data.manager.FeedbackQACommentManager;
import org.cgiar.ccafs.marlo.data.model.FeedbackQAComment;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class FeedbackQACommentManagerImpl implements FeedbackQACommentManager {


  private FeedbackQACommentDAO feedbackQACommentDAO;
  // Managers


  @Inject
  public FeedbackQACommentManagerImpl(FeedbackQACommentDAO feedbackQACommentDAO) {
    this.feedbackQACommentDAO = feedbackQACommentDAO;


  }

  @Override
  public void deleteFeedbackQAComment(long feedbackQACommentId) {

    feedbackQACommentDAO.deleteFeedbackQAComment(feedbackQACommentId);
  }

  @Override
  public boolean existFeedbackQAComment(long feedbackQACommentID) {

    return feedbackQACommentDAO.existFeedbackQAComment(feedbackQACommentID);
  }

  @Override
  public List<FeedbackQAComment> findAll() {

    return feedbackQACommentDAO.findAll();

  }

  @Override
  public FeedbackQAComment getFeedbackQACommentById(long feedbackQACommentID) {

    return feedbackQACommentDAO.find(feedbackQACommentID);
  }

  @Override
  public FeedbackQAComment saveFeedbackQAComment(FeedbackQAComment feedbackQAComment) {

    return feedbackQACommentDAO.save(feedbackQAComment);
  }


}
