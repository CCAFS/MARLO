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

import org.cgiar.ccafs.marlo.data.dao.HelldotsScreenshotDAO;
import org.cgiar.ccafs.marlo.data.model.HelldotsScreenshot;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class HelldotsScreenshotMySQLDAO extends AbstractMarloDAO<HelldotsScreenshot, Long>
  implements HelldotsScreenshotDAO {

  @Inject
  public HelldotsScreenshotMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public HelldotsScreenshot find(long id) {
    return super.find(HelldotsScreenshot.class, id);
  }

  @Override
  public HelldotsScreenshot save(HelldotsScreenshot helldotsScreenshot) {
    if (helldotsScreenshot.getId() == null) {
      super.saveEntity(helldotsScreenshot);
    } else {
      helldotsScreenshot = super.update(helldotsScreenshot);
    }
    // The session this request runs on carries FlushMode.MANUAL, set by Spring's OpenSessionInViewFilter
    // (web.xml:28-40), not by MARLOCustomPersistFilter (which only begins/commits the raw transaction and
    // never touches flush mode). Every other MARLO manager avoids this because HibernateTransactionManager
    // .doBegin() flips a bound MANUAL session to AUTO for a non-read-only @Transactional; this manager has
    // no @Transactional (see HelldotsScreenshotManagerImpl), so nothing performs that flip and a pending
    // UPDATE would otherwise be discarded, unflushed, when the session clears at commit. Flush explicitly.
    // (The insert branch above is unaffected: the identity generator forces Hibernate to run the INSERT
    // eagerly inside saveEntity() to obtain the generated key, independent of flush mode.)
    // Caution: session.flush() flushes the entire persistence context, not just this entity. Do not call
    // this manager from inside a larger unit of work that has other pending changes on the same session.
    this.getSessionFactory().getCurrentSession().flush();
    return helldotsScreenshot;
  }
}
