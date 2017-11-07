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

package org.cgiar.ccafs.marlo.config;

import org.cgiar.ccafs.marlo.data.HibernateAuditLogListener;

import org.hibernate.cfg.Configuration;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.integrator.spi.Integrator;
import org.hibernate.metamodel.source.MetadataImplementor;
import org.hibernate.service.spi.SessionFactoryServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyIntegrator implements Integrator {

  private final Logger LOG = LoggerFactory.getLogger(MyIntegrator.class);

  @Override
  public void disintegrate(SessionFactoryImplementor sessionFactory, SessionFactoryServiceRegistry serviceRegistry) {
    return;
  }

  @Override
  public void integrate(Configuration configuration, SessionFactoryImplementor sessionFactory,
    SessionFactoryServiceRegistry serviceRegistry) {

    final EventListenerRegistry eventListenerRegistry = serviceRegistry.getService(EventListenerRegistry.class);

    HibernateAuditLogListener hibernateAuditLogListener = new HibernateAuditLogListener();

    eventListenerRegistry.prependListeners(EventType.POST_UPDATE, hibernateAuditLogListener);
    eventListenerRegistry.prependListeners(EventType.POST_INSERT, hibernateAuditLogListener);
    eventListenerRegistry.prependListeners(EventType.POST_DELETE, hibernateAuditLogListener);

    eventListenerRegistry.prependListeners(EventType.FLUSH, hibernateAuditLogListener);

    LOG.debug("Finished registering Hibernate Event Listeners");

  }

  @Override
  public void integrate(MetadataImplementor metadata, SessionFactoryImplementor sessionFactory,
    SessionFactoryServiceRegistry serviceRegistry) {
    return;

  }


}
