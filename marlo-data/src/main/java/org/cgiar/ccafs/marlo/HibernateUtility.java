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

package org.cgiar.ccafs.marlo;

import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.PropertiesManager;

import java.net.URL;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * Class to initiate the hibernate session connection whit the Database.
 * 
 * @author Hermes Jimenez - CCAFS
 */
public class HibernateUtility {

  public static SessionFactory factory;

  private static String path = "/hibernate.cfg.xml";

  private static Configuration config;

  /**
   * Get the connection info through to properties file.
   */
  private static synchronized void getConection() {
    URL url = HibernateUtility.class.getResource(path);
    config = new Configuration().configure(url);
    PropertiesManager manager = new PropertiesManager();

    config.setProperty("hibernate.connection.username", manager.getPropertiesAsString(APConfig.MYSQL_USER));
    config.setProperty("hibernate.connection.password", manager.getPropertiesAsString(APConfig.MYSQL_PASSWORD));
    String urlMysql = "jdbc:mysql://" + manager.getPropertiesAsString(APConfig.MYSQL_HOST) + ":"
      + manager.getPropertiesAsString(APConfig.MYSQL_PORT) + "/"
      + manager.getPropertiesAsString(APConfig.MYSQL_DATABASE) + "?autoReconnect=true&&useSSL=false";
    config.setProperty("hibernate.connection.url", urlMysql);
    config.setProperty("hibernate.current_session_context_class", "thread");


  }

  /**
   * to disallow creating objects by other classes.
   * 
   * @return a SessionFactory unique instance
   */
  public static synchronized SessionFactory getSessionFactory() {

    if (factory == null) {
      getConection();
      factory = config.buildSessionFactory();

    }
    return factory;
  }

  private HibernateUtility() {
  }
  // maling the Hibernate SessionFactory object as singleton
}