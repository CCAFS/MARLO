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

import org.cgiar.ccafs.marlo.data.dao.FeedbackQAReplyDAO;
import org.cgiar.ccafs.marlo.data.model.FeedbackQAReply;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class FeedbackCommentMySQLDAO extends AbstractMarloDAO<FeedbackQAReply, Long> implements FeedbackQAReplyDAO {


  @Inject
  public FeedbackCommentMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteFeedbackComment(long feedbackCommentId) {
    FeedbackQAReply feedbackQAReply = this.find(feedbackCommentId);
    this.delete(feedbackQAReply);
  }

  @Override
  public boolean existFeedbackComment(long feedbackCommentID) {
    FeedbackQAReply feedbackQAReply = this.find(feedbackCommentID);
    if (feedbackQAReply == null) {
      return false;
    }
    return true;

  }

  @Override
  public FeedbackQAReply find(long id) {
    return super.find(FeedbackQAReply.class, id);

  }

  @Override
  public List<FeedbackQAReply> findAll() {
    String query = "from " + FeedbackQAReply.class.getName();
    List<FeedbackQAReply> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public FeedbackQAReply save(FeedbackQAReply feedbackQAReply) {
    if (feedbackQAReply.getId() == null) {
      super.saveEntity(feedbackQAReply);
    } else {
      feedbackQAReply = super.update(feedbackQAReply);
    }


    return feedbackQAReply;
  }


}