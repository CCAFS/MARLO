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

import org.cgiar.ccafs.marlo.data.dao.FeedbackQACommentDAO;
import org.cgiar.ccafs.marlo.data.model.FeedbackQAComment;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class FeedbackQACommentMySQLDAO extends AbstractMarloDAO<FeedbackQAComment, Long>
  implements FeedbackQACommentDAO {


  @Inject
  public FeedbackQACommentMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteFeedbackQAComment(long feedbackQACommentId) {
    FeedbackQAComment feedbackQAComment = this.find(feedbackQACommentId);
    this.delete(feedbackQAComment);
  }

  @Override
  public boolean existFeedbackQAComment(long feedbackQACommentID) {
    FeedbackQAComment feedbackQAComment = this.find(feedbackQACommentID);
    if (feedbackQAComment == null) {
      return false;
    }
    return true;

  }

  @Override
  public FeedbackQAComment find(long id) {
    return super.find(FeedbackQAComment.class, id);

  }

  @Override
  public List<FeedbackQAComment> findAll() {
    String query = "from " + FeedbackQAComment.class.getName();
    List<FeedbackQAComment> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public FeedbackQAComment save(FeedbackQAComment feedbackQAComment) {
    if (feedbackQAComment.getId() == null) {
      super.saveEntity(feedbackQAComment);
    } else {
      feedbackQAComment = super.update(feedbackQAComment);
    }


    return feedbackQAComment;
  }


}