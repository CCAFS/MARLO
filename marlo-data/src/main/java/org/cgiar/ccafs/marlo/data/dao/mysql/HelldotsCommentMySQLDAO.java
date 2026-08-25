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

import org.cgiar.ccafs.marlo.data.dao.HelldotsCommentDAO;
import org.cgiar.ccafs.marlo.data.model.HelldotsComment;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

@Named
public class HelldotsCommentMySQLDAO extends AbstractMarloDAO<HelldotsComment, Long>
  implements HelldotsCommentDAO {

  @Inject
  public HelldotsCommentMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public HelldotsComment find(long id) {
    return super.find(HelldotsComment.class, id);
  }

  @Override
  public List<HelldotsComment> findAllActive() {
    String hql = "select hc from HelldotsComment hc where hc.active = true order by hc.createdAt desc";
    List<HelldotsComment> list = super.findAll(hql);
    if (list == null) {
      return new ArrayList<>();
    }
    return list;
  }

  @Override
  public HelldotsComment findByCommentId(String commentId) {
    String hql = "select hc from HelldotsComment hc where hc.commentId = :commentId and hc.active = true";
    Query<HelldotsComment> createQuery = this.getSessionFactory().getCurrentSession().createQuery(hql);
    createQuery.setParameter("commentId", commentId);
    return super.findSingleResult(HelldotsComment.class, createQuery);
  }

  @Override
  public List<HelldotsComment> findByPage(String page) {
    String hql =
      "select hc from HelldotsComment hc where hc.page = :page and hc.active = true order by hc.createdAt asc";
    Query<HelldotsComment> createQuery = this.getSessionFactory().getCurrentSession().createQuery(hql);
    createQuery.setParameter("page", page);
    List<HelldotsComment> list = super.findAll(createQuery);
    if (list == null) {
      return new ArrayList<>();
    }
    return list;
  }

  @Override
  public HelldotsComment save(HelldotsComment helldotsComment) {
    if (helldotsComment.getId() == null) {
      super.saveEntity(helldotsComment);
    } else {
      helldotsComment = super.update(helldotsComment);
    }
    // The session this request runs on carries FlushMode.MANUAL (set by MARLOCustomPersistFilter), so
    // Transaction.commit() does not auto-flush. An update on an already-managed entity only registers as
    // dirty in the persistence context; without an explicit flush here, that pending UPDATE is silently
    // discarded when the session is cleared at commit and no SQL is ever sent. Flush explicitly so the write
    // reaches the database regardless of the ambient flush mode. (The insert branch above is unaffected by
    // this because the identity generator forces Hibernate to run the INSERT eagerly inside saveEntity() to
    // obtain the generated key, independent of flush mode; the flush call is a harmless no-op in that case.)
    this.getSessionFactory().getCurrentSession().flush();
    return helldotsComment;
  }
}
