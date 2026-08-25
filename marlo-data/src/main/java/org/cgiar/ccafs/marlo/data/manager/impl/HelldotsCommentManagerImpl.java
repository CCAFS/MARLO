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
  public List<HelldotsComment> findByPage(String page) {
    return helldotsCommentDAO.findByPage(page);
  }

  /**
   * Intentionally NOT annotated with {@code @Transactional}. Every request that reaches this manager is mapped
   * under {@code NON_STATIC_RESOURCE_REQUESTS} in {@code WebAppInitializer} (which includes {@code /api/*}), and
   * {@code MARLOCustomPersistFilter} already begins a Hibernate transaction directly on the current session
   * before the filter chain runs, committing it after. Spring's {@code HibernateTransactionManager} has no
   * synchronization registered for a transaction opened that way, so it cannot see it is already active. Adding
   * {@code @Transactional} here makes Spring try to open a second transaction on the same session, which
   * Hibernate rejects with {@code IllegalStateException: Transaction already active}. The filter is the real
   * transaction boundary for this call path; do not re-add this annotation.
   */
  @Override
  public HelldotsComment save(HelldotsComment helldotsComment) {
    return helldotsCommentDAO.save(helldotsComment);
  }
}
