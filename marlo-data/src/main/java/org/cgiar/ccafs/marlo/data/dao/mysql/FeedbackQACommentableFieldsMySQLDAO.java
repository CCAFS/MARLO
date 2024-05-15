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

import org.cgiar.ccafs.marlo.data.dao.FeedbackQACommentableFieldsDAO;
import org.cgiar.ccafs.marlo.data.model.FeedbackQACommentableFields;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class FeedbackQACommentableFieldsMySQLDAO extends AbstractMarloDAO<FeedbackQACommentableFields, Long>
  implements FeedbackQACommentableFieldsDAO {


  @Inject
  public FeedbackQACommentableFieldsMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteInternalQaCommentableFields(long internalQaCommentableFieldsId) {
    FeedbackQACommentableFields feedbackQACommentableFields = this.find(internalQaCommentableFieldsId);
    this.delete(feedbackQACommentableFields);
  }

  @Override
  public boolean existInternalQaCommentableFields(long internalQaCommentableFieldsID) {
    FeedbackQACommentableFields feedbackQACommentableFields = this.find(internalQaCommentableFieldsID);
    if (feedbackQACommentableFields == null) {
      return false;
    }
    return true;

  }

  @Override
  public FeedbackQACommentableFields find(long id) {
    return super.find(FeedbackQACommentableFields.class, id);

  }

  @Override
  public List<FeedbackQACommentableFields> findAll() {
    String query = "from " + FeedbackQACommentableFields.class.getName() + " where is_active=1";
    List<FeedbackQACommentableFields> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<FeedbackQACommentableFields> findBySectionName(String sectionName) {
    String query = "from " + FeedbackQACommentableFields.class.getName() + " where is_active=1 and section_name='"
      + sectionName + "'";
    List<FeedbackQACommentableFields> list = super.findAll(query);
    if (!list.isEmpty()) {
      return list;
    }
    return Collections.emptyList();
  }

  /**
   * Get the answered comment by phase
   * 
   * @author IBD
   * @param phase phase of the project
   * @return deliverable list with the comment count. olny coment with answer
   */
  @Override
  public List<String> getAnsweredCommentByPhaseToStudy(long phase) {
    StringBuilder query = new StringBuilder();
    query.append("SELECT parent_id as parent_id,count(*) as count ");
    query.append("FROM feedback_qa_comments fqc");
    query.append(" WHERE id_phase=" + phase);
    query.append(" and status_id = 1 ");
    query.append(" and field_id in (select id from feedback_qa_commentable_fields fqcf ");
    query.append(" where section_name = 'study') ");
    query.append(" and reply_id is not null ");
    query.append(" group by parent_id ");

    List<Map<String, Object>> rList = super.findCustomQuery(query.toString());
    List<String> comments = new ArrayList<>();

    if (rList != null) {
      for (Map<String, Object> map : rList) {
        String tmp = map.get("parent_id").toString() + "|" + map.get("count").toString();
        comments.add(tmp);
      }
    }

    return comments;
  }


  /**
   * Get the commentstatus by phase
   * 
   * @author IBD
   * @param phase phase of the project
   * @return deliverable list with the comment count
   */
  @Override
  public List<String> getCommentStatusByPhaseToStudy(long phase) {
    StringBuilder query = new StringBuilder();
    query.append("SELECT parent_id as parent_id,count(*) as count ");
    query.append("FROM feedback_qa_comments fqc");
    query.append(" WHERE id_phase=" + phase);
    query.append(" and status_id <> 6 ");
    query.append(" and field_id in (select id from feedback_qa_commentable_fields fqcf ");
    query.append(" where section_name = 'study') ");
    query.append(" group by parent_id ");

    List<Map<String, Object>> rList = super.findCustomQuery(query.toString());
    List<String> comments = new ArrayList<>();

    if (rList != null) {
      for (Map<String, Object> map : rList) {
        String tmp = map.get("parent_id").toString() + "|" + map.get("count").toString();
        comments.add(tmp);
      }
    }

    return comments;
  }


  @Override
  public FeedbackQACommentableFields save(FeedbackQACommentableFields feedbackQACommentableFields) {
    if (feedbackQACommentableFields.getId() == null) {
      super.saveEntity(feedbackQACommentableFields);
    } else {
      feedbackQACommentableFields = super.update(feedbackQACommentableFields);
    }


    return feedbackQACommentableFields;
  }


}