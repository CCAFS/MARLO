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


import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.PropertiesManager;

import java.net.URL;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

public class HibernateListener implements ServletContextListener {

  private static Class<?> clazz = HibernateListener.class;
  public static final String KEY_NAME = clazz.getName();
  private Configuration config;
  private SessionFactory factory;

  private String path = "/hibernate.cfg.xml";

  private SessionFactory configSessionFactory(URL url) {
    Configuration config = null;
    config = new Configuration().configure(url);
    PropertiesManager manager = new PropertiesManager();

    config.setProperty("hibernate.connection.username", manager.getPropertiesAsString(APConfig.MYSQL_USER));
    config.setProperty("hibernate.connection.password", manager.getPropertiesAsString(APConfig.MYSQL_PASSWORD));
    String urlMysql = "jdbc:mysql://" + manager.getPropertiesAsString(APConfig.MYSQL_HOST) + ":"
      + manager.getPropertiesAsString(APConfig.MYSQL_PORT) + "/"
      + manager.getPropertiesAsString(APConfig.MYSQL_DATABASE) + "?autoReconnect=true&&useSSL=false";
    config.setProperty("hibernate.connection.url", urlMysql);
    config.setProperty("hibernate.current_session_context_class", "thread");
    config.setProperty("hibernate.hikari.dataSource.url", urlMysql);
    config.setProperty("hibernate.hikari.dataSource.user", manager.getPropertiesAsString(APConfig.MYSQL_USER));
    config.setProperty("hibernate.hikari.dataSource.password", manager.getPropertiesAsString(APConfig.MYSQL_PASSWORD));
    config.setProperty("hibernate.hikari.connectionTimeout", "10000");
    // Minimum number of ideal connections in the pool
    config.setProperty("hibernate.hikari.minimumIdle", "1000");
    // Maximum number of actual connection in the pool
    config.setProperty("hibernate.hikari.maximumPoolSize", "5000");
    // Maximum time that a connection is allowed to sit ideal in the pool
    config.setProperty("hibernate.hikari.idleTimeout", "5000");
    config.setProperty("hibernate.bytecode.use_reflection_optimizer'", "false");
    // config.setProperty("hibernate.c3p0.min_size", "5");
    // System.out.println("url_mysql " + url_mysql);
    // System.out.println(url.toString());

    StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder().applySettings(config.getProperties());
    SessionFactory factory = config.buildSessionFactory(builder.build());
    return factory;

  }

  @Override
  public void contextDestroyed(ServletContextEvent event) {
    //
    if (factory != null) {
      factory.close();
    }

  }


  @Override
  public void contextInitialized(ServletContextEvent event) {

    try {
      URL url = HibernateListener.class.getResource(path);
      factory = this.configSessionFactory(url);
      event.getServletContext().setAttribute(KEY_NAME, factory);

    } catch (Exception e) {
      e.printStackTrace();
      System.out.println(e.getMessage());
    }
  }
}