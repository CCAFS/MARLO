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


package org.cgiar.ccafs.marlo.data.dao.mysql;

import org.cgiar.ccafs.marlo.data.dao.FeedbackCommentDAO;
import org.cgiar.ccafs.marlo.data.model.FeedbackComment;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class FeedbackCommentMySQLDAO extends AbstractMarloDAO<FeedbackComment, Long> implements FeedbackCommentDAO {


  @Inject
  public FeedbackCommentMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteFeedbackComment(long feedbackCommentId) {
    FeedbackComment feedbackComment = this.find(feedbackCommentId);
    this.delete(feedbackComment);
  }

  @Override
  public boolean existFeedbackComment(long feedbackCommentID) {
    FeedbackComment feedbackComment = this.find(feedbackCommentID);
    if (feedbackComment == null) {
      return false;
    }
    return true;

  }

  @Override
  public FeedbackComment find(long id) {
    return super.find(FeedbackComment.class, id);

  }

  @Override
  public List<FeedbackComment> findAll() {
    String query = "from " + FeedbackComment.class.getName();
    List<FeedbackComment> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public FeedbackComment save(FeedbackComment feedbackComment) {
    if (feedbackComment.getId() == null) {
      super.saveEntity(feedbackComment);
    } else {
      feedbackComment = super.update(feedbackComment);
    }


    return feedbackComment;
  }


}