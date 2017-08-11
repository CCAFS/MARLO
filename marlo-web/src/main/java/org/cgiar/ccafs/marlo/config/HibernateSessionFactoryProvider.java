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

import org.cgiar.ccafs.marlo.data.AuditLogInterceptor;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.PropertiesManager;

import java.net.URL;

import com.google.inject.Provider;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A Guice Provider that returns the Hibernate SessionFactory. This allows us to inject the sessionFactory into other
 * Guice managed objects.
 * This needs to be configured within the Guice Binder as a Singleton otherwise a new sessionFactory will be
 * created for each request. See code snipet below.
 * binder.bind(SessionFactory.class).toProvider(HibernateSessionFactoryProvider.class).in(Singleton.class);
 * 
 * @author GrantL
 */
public class HibernateSessionFactoryProvider implements Provider<SessionFactory> {

  private final String path = "/hibernate.cfg.xml";

  private final SessionFactory sessionFactory;

  private final Logger LOG = LoggerFactory.getLogger(HibernateSessionFactoryProvider.class);

  public HibernateSessionFactoryProvider() {

    LOG.debug("Building the Hibernate SessionFactory using configuration at path " + path);

    URL url = HibernateSessionFactoryProvider.class.getResource(path);
    Configuration hibernateConfig = new Configuration().configure(url);
    PropertiesManager manager = new PropertiesManager();

    hibernateConfig.setProperty("hibernate.connection.username", manager.getPropertiesAsString(APConfig.MYSQL_USER));
    hibernateConfig.setProperty("hibernate.connection.password",
      manager.getPropertiesAsString(APConfig.MYSQL_PASSWORD));
    String urlMysql = "jdbc:mysql://" + manager.getPropertiesAsString(APConfig.MYSQL_HOST) + ":"
      + manager.getPropertiesAsString(APConfig.MYSQL_PORT) + "/"
      + manager.getPropertiesAsString(APConfig.MYSQL_DATABASE) + "?autoReconnect=true&&useSSL=false";
    hibernateConfig.setProperty("hibernate.connection.url", urlMysql);
    hibernateConfig.setProperty("hibernate.current_session_context_class", "thread");
    /**
     * TODO refactor the MARLO properties system and use Guice to inject properties.
     * There are also libraries such as https://bitbucket.org/strangerintheq/guice-config/wiki/Dependency that we could
     * use as well.
     */
    String showSql = manager.getPropertiesAsString(APConfig.MYSQL_SHOW_SQL);
    if (showSql != null && ("true".equals(showSql) || "false".equals(showSql))) {
      hibernateConfig.setProperty(Environment.SHOW_SQL, manager.getPropertiesAsString(APConfig.MYSQL_SHOW_SQL));
    }
    // hibernateConfig.setProperty("hibernate.c3p0.min_size", "5");

    AuditLogInterceptor auditLogInterceptor = new AuditLogInterceptor();

    hibernateConfig.setInterceptor(auditLogInterceptor);
    this.registerHibernateEventListeners(hibernateConfig);
    sessionFactory = hibernateConfig.buildSessionFactory();

    /**
     * See comments on the @HibernateAuditLogListener as to why we need the interceptor as well.
     */
    auditLogInterceptor.setSessionFactory(sessionFactory);

  }

  @Override
  public SessionFactory get() {
    return this.sessionFactory;
  }

  private void registerHibernateEventListeners(Configuration configuration) {
    String className = "org.cgiar.ccafs.marlo.data.HibernateAuditLogListener";

    configuration.setListener("pre-update", className);
    configuration.setListener("pre-insert", className);
    configuration.setListener("pre-delete", className);

    configuration.setListener("post-collection-recreate", className);
    configuration.setListener("post-collection-remove", className);
    configuration.setListener("post-collection-update", className);


    configuration.setListener("post-update", className);
    configuration.setListener("post-insert", className);
    configuration.setListener("post-delete", className);


    configuration.setListeners("flush", new String[] {className, "org.hibernate.event.def.DefaultFlushEventListener"});
    // configuration.setListener("flush-entity", className);

  }

}
