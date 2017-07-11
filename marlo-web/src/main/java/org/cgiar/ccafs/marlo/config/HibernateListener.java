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
import org.hibernate.cfg.Configuration;

public class HibernateListener implements ServletContextListener {

  private static Class<?> clazz = HibernateListener.class;
  public static final String KEY_NAME = clazz.getName();
  private Configuration config;
  private SessionFactory factory;

  private String path = "/hibernate.cfg.xml";

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

      // System.out.println("Entering Hibernate Listener");
      URL url = HibernateListener.class.getResource(path);
      config = new Configuration().configure(url);
      PropertiesManager manager = new PropertiesManager();

      config.setProperty("hibernate.connection.username", manager.getPropertiesAsString(APConfig.MYSQL_USER));
      config.setProperty("hibernate.connection.password", manager.getPropertiesAsString(APConfig.MYSQL_PASSWORD));
      String urlMysql = "jdbc:mysql://" + manager.getPropertiesAsString(APConfig.MYSQL_HOST) + ":"
        + manager.getPropertiesAsString(APConfig.MYSQL_PORT) + "/"
        + manager.getPropertiesAsString(APConfig.MYSQL_DATABASE) + "?autoReconnect=true&&useSSL=false";
      config.setProperty("hibernate.connection.url", urlMysql);
      config.setProperty("hibernate.current_session_context_class", "thread");
      // config.setProperty("hibernate.c3p0.min_size", "5");
      // System.out.println("url_mysql " + url_mysql);
      // System.out.println(url.toString());

      factory = config.buildSessionFactory();

      // System.out.println("Build factory " + factory);

      // save the Hibernate session factory into serlvet context
      event.getServletContext().setAttribute(KEY_NAME, factory);
      // System.out.println(KEY_NAME + "puso el dato e session");
      // System.out.println(event.getServletContext().getAttribute(KEY_NAME));
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println(e.getMessage());
    }
  }
}