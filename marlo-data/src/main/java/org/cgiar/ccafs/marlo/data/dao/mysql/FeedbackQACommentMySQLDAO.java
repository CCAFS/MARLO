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

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;
import org.hibernate.transform.AliasToEntityMapResultTransformer;

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
    if (!list.isEmpty()) {
      return list;
    }
    return null;

  }

  @Override
  public List<FeedbackQAComment> findAllByPhase(long phaseId) {
    String query = "from " + FeedbackQAComment.class.getName() + " where id_phase = " + phaseId;
    List<FeedbackQAComment> list = super.findAll(query);
    if (!list.isEmpty()) {
      return list;
    }
    return null;

  }

  @Override
  public List<FeedbackQAComment> getFeedbackQACommentsByParentId(long parentID) {
    String query = "from " + FeedbackQAComment.class.getName() + " where parent_id=" + parentID;
    List<FeedbackQAComment> list = super.findAll(query);
    if (!list.isEmpty()) {
      return list;
    }
    return null;
  }

  @Override
  public List<FeedbackQAComment> getFeedbackQACommentsByPhaseAndParentId(long phaseID, long parentID) {
    String query =
      "from " + FeedbackQAComment.class.getName() + " where parent_id=" + parentID + " and id_phase =" + phaseID;
    List<FeedbackQAComment> list = super.findAll(query);
    if (!list.isEmpty()) {
      return list;
    }
    return Collections.emptyList();
  }

  @Override
  public List<Map<String, Object>> getUsageByCommentableFieldAndGlobalUnit(long globalUnitID) {
    /*
     * One aggregate for the whole global unit instead of a query per field: the admin screen needs a usage count
     * for every commentable field it lists, plus the breakdown behind each one. The join on
     * feedback_qa_commentable_fields is what scopes the result to the global unit -- feedback_qa_comments has no
     * global unit of its own, it inherits the one of the field it points at.
     */
    StringBuilder query = new StringBuilder();
    query.append("SELECT c.field_id AS field_id, c.project_id AS project_id, c.id_phase AS phase_id, ");
    query.append("COUNT(*) AS total, MIN(c.link) AS link ");
    query.append("FROM feedback_qa_comments c ");
    query.append("JOIN feedback_qa_commentable_fields f ON f.id = c.field_id ");
    query.append("WHERE f.global_unit_id = :globalUnitID ");
    query.append("GROUP BY c.field_id, c.project_id, c.id_phase ");
    query.append("ORDER BY c.field_id, total DESC ");

    List<Map<String, Object>> result = this.getSessionFactory().getCurrentSession()
      .createNativeQuery(query.toString()).setParameter("globalUnitID", globalUnitID)
      .setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE).list();

    if (result == null) {
      return Collections.emptyList();
    }
    return result;
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