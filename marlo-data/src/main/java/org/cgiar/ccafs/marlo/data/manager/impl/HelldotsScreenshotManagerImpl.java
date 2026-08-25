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

import org.cgiar.ccafs.marlo.data.dao.HelldotsScreenshotDAO;
import org.cgiar.ccafs.marlo.data.manager.HelldotsScreenshotManager;
import org.cgiar.ccafs.marlo.data.model.HelldotsScreenshot;

import javax.inject.Inject;
import javax.inject.Named;

@Named
public class HelldotsScreenshotManagerImpl implements HelldotsScreenshotManager {

  private HelldotsScreenshotDAO helldotsScreenshotDAO;

  @Inject
  public HelldotsScreenshotManagerImpl(HelldotsScreenshotDAO helldotsScreenshotDAO) {
    this.helldotsScreenshotDAO = helldotsScreenshotDAO;
  }

  @Override
  public HelldotsScreenshot find(long id) {
    return helldotsScreenshotDAO.find(id);
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
  public HelldotsScreenshot save(HelldotsScreenshot helldotsScreenshot) {
    return helldotsScreenshotDAO.save(helldotsScreenshot);
  }
}
