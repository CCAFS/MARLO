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
    // The session this request runs on carries FlushMode.MANUAL (set by MARLOCustomPersistFilter), so
    // Transaction.commit() does not auto-flush. An update on an already-managed entity only registers as
    // dirty in the persistence context; without an explicit flush here, that pending UPDATE is silently
    // discarded when the session is cleared at commit and no SQL is ever sent. Flush explicitly so the write
    // reaches the database regardless of the ambient flush mode. (The insert branch above is unaffected by
    // this because the identity generator forces Hibernate to run the INSERT eagerly inside saveEntity() to
    // obtain the generated key, independent of flush mode; the flush call is a harmless no-op in that case.)
    this.getSessionFactory().getCurrentSession().flush();
    return helldotsScreenshot;
  }
}
