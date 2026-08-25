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

import org.cgiar.ccafs.marlo.data.dao.HelldotsCommentDAO;
import org.cgiar.ccafs.marlo.data.manager.HelldotsCommentManager;
import org.cgiar.ccafs.marlo.data.model.HelldotsComment;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

@Named
public class HelldotsCommentManagerImpl implements HelldotsCommentManager {

  private HelldotsCommentDAO helldotsCommentDAO;

  @Inject
  public HelldotsCommentManagerImpl(HelldotsCommentDAO helldotsCommentDAO) {
    this.helldotsCommentDAO = helldotsCommentDAO;
  }

  @Override
  public HelldotsComment find(long id) {
    return helldotsCommentDAO.find(id);
  }

  @Override
  public List<HelldotsComment> findAllActive() {
    return helldotsCommentDAO.findAllActive();
  }

  @Override
  public HelldotsComment findByCommentId(String commentId) {
    return helldotsCommentDAO.findByCommentId(commentId);
  }

  @Override
  public HelldotsComment findByCommentIdIncludingInactive(String commentId) {
    return helldotsCommentDAO.findByCommentIdIncludingInactive(commentId);
  }

  @Override
  public List<HelldotsComment> findByPage(String page) {
    return helldotsCommentDAO.findByPage(page);
  }

  /**
   * Intentionally NOT annotated with {@code @Transactional}. Spring's {@code OpenSessionInViewFilter}
   * (web.xml:28-40) opens the Hibernate session for {@code /api/*} with {@code FlushMode.MANUAL};
   * {@code MARLOCustomPersistFilter} only begins and commits a raw transaction on that session and never
   * touches flush mode. What flips a MANUAL session to AUTO for every other MARLO manager is
   * {@code HibernateTransactionManager.doBegin()} on a non-read-only {@code @Transactional}. Adding
   * {@code @Transactional} here would make Spring try to open a second transaction on a session that already
   * has one active via the filter, which Hibernate rejects with {@code IllegalStateException}. The DAO flushes
   * explicitly instead; do not re-add this annotation.
   */
  @Override
  public HelldotsComment save(HelldotsComment helldotsComment) {
    return helldotsCommentDAO.save(helldotsComment);
  }
}
