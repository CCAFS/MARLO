/*****************************************************************
 * This file is part of CCAFS Planning and Reporting Platform.
 * CCAFS P&R is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 * CCAFS P&R is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with CCAFS P&R. If not, see <http://www.gnu.org/licenses/>.
 *****************************************************************/
package org.cgiar.ccafs.marlo.config;


import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.struts2.Struts2GuicePluginModule;
import org.apache.shiro.guice.web.ShiroWebModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class APGuiceContextListener extends GuiceServletContextListener {

  public static Logger LOG = LoggerFactory.getLogger(APGuiceContextListener.class);
  private ServletContext servletContext;

  @Override
  public void contextDestroyed(ServletContextEvent servletContextEvent) {
    // TODO Auto-generated method stub

  }

  @Override
  public void contextInitialized(ServletContextEvent servletContextEvent) {


    LOG.info("-- ContextInitialized start -- ");
    servletContext = servletContextEvent.getServletContext();
    super.contextInitialized(servletContextEvent);
  }

  @Override
  protected Injector getInjector() {
    return Guice.createInjector(new APShiroWebModule(this.servletContext), ShiroWebModule.guiceFilterModule(),
      new Struts2GuicePluginModule(), new APModule());
  }
}
