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

package org.cgiar.ccafs.marlo.data;

import org.cgiar.ccafs.marlo.data.model.MarloAuditableEntity;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.utils.AuditLogContextProvider;

import java.util.Date;

import org.apache.commons.lang3.ArrayUtils;
import org.hibernate.event.spi.PreInsertEvent;
import org.hibernate.event.spi.PreInsertEventListener;
import org.hibernate.event.spi.PreUpdateEvent;
import org.hibernate.event.spi.PreUpdateEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A Hibernate listener to standardize how we are populating the Audit Columns for MARLO tables.
 * This circumvents the need for developers to populate the fields themselves.
 * 
 * @author GrantL
 */
public class AuditColumnHibernateListener implements PreInsertEventListener, PreUpdateEventListener {

  private static final long serialVersionUID = 1L;

  private static final Logger LOG = LoggerFactory.getLogger(AuditColumnHibernateListener.class);

  private User getUser() {
    /**
     * This SecurityUtils.getSubject() won't work in some cases as the ShiroFilter may have exited and the currentUser
     * details will have been popped from the thread. However our AuditLogContext will still be accessible so we can
     * store the userId in that instead.
     */
    // Subject subject = SecurityUtils.getSubject();
    Long userId = AuditLogContextProvider.getAuditLogContext().getCurrentUserId();

    User user = new User();
    user.setId(userId);
    return user;
  }


  /**
   * Note the implementation would be much simpler if we use JPA annotation @PreInser rather than raw hibernate.
   */
  @Override
  public boolean onPreInsert(PreInsertEvent event) {

    if (!(event.getEntity() instanceof MarloAuditableEntity)) {
      LOG.debug("entity: " + event.getEntity().getClass() + "is not a MarloAuditableEntity");
      return false;
    }

    MarloAuditableEntity auditableEntity = (MarloAuditableEntity) event.getEntity();

    String[] propertyNames = event.getPersister().getEntityMetamodel().getPropertyNames();
    Object[] state = event.getState();

    User user = this.getUser();
    Date now = new Date();

    Boolean active = new Boolean(true);

    this.setValue(state, propertyNames, "createdBy", user, auditableEntity);
    this.setValue(state, propertyNames, "activeSince", now, auditableEntity);
    this.setValue(state, propertyNames, "active", active, auditableEntity);

    // Required to guard against an insert happening together with an update.
    auditableEntity.setCreatedBy(user);
    auditableEntity.setActiveSince(now);
    auditableEntity.setActive(active);

    return false;
  }


  /**
   * Note the implementation would be much simpler if we use JPA annotation @PreUpdate arather than raw hibernate.
   */
  @Override
  public boolean onPreUpdate(PreUpdateEvent event) {

    if (!(event.getEntity() instanceof MarloAuditableEntity)) {
      LOG.debug("entity: " + event.getEntity().getClass() + "is not a MarloAuditableEntity");
      return false;
    }

    User user = this.getUser();

    /**
     * This is to guard against an unauthenticated user making an update
     */
    if (user.getId() == null) {
      return false;
    }

    String[] propertyNames = event.getPersister().getEntityMetamodel().getPropertyNames();
    Object[] state = event.getState();

    MarloAuditableEntity auditableEntity = (MarloAuditableEntity) event.getEntity();
    this.setValue(state, propertyNames, "modifiedBy", user, auditableEntity);


    auditableEntity.setModifiedBy(user);

    return false;
  }


  private void setValue(Object[] currentState, String[] propertyNames, String propertyToSet, Object value,
    Object entity) {
    int index = ArrayUtils.indexOf(propertyNames, propertyToSet);
    if (index >= 0) {
      currentState[index] = value;
    } else {
      LOG.error("Field '" + propertyToSet + "' not found on entity '" + entity.getClass().getName() + "'.");
    }
  }

}
