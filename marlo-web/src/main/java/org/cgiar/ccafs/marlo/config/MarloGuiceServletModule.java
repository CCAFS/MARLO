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

import org.cgiar.ccafs.marlo.web.filter.MARLOCustomPersistFilter;
import org.cgiar.ccafs.marlo.web.filter.RemoveSessionFromUrlFilter;

import java.util.HashMap;
import java.util.Map;

import com.google.inject.Singleton;
import com.google.inject.servlet.ServletModule;
import org.apache.shiro.web.servlet.ShiroFilter;
import org.apache.struts2.dispatcher.filter.StrutsPrepareAndExecuteFilter;

/**
 * This class configures our Servlets and Filters.
 * By registering them with Guice we can inject guice registered dependencies such as the @org.hibernate.SessionFactory
 * into our servlets and filters.
 * 
 * @author GrantL
 */
public class MarloGuiceServletModule extends ServletModule {

  @Override
  protected void configureServlets() {

    this.binder().bind(ShiroFilter.class).in(Singleton.class);
    this.binder().bind(StrutsPrepareAndExecuteFilter.class).in(Singleton.class);
    this.binder().bind(MARLOCustomPersistFilter.class).in(Singleton.class);
    this.binder().bind(RemoveSessionFromUrlFilter.class).in(Singleton.class);

    this.filter("/*").through(RemoveSessionFromUrlFilter.class);

    this.filter("*.do").through(MARLOCustomPersistFilter.class);
    this.filter("*.json").through(MARLOCustomPersistFilter.class);
    /**
     * TODO
     * Need to find way to redirect the root path to use the login.do page or if user logged in to navigate them to
     * the home page. Enabling the filter on the root path breaks the filter mapping rules for other pages.
     */
    this.filter("/").through(MARLOCustomPersistFilter.class);

    /**
     * TODO
     * Do we want requests for static resources (e.g. images, css etc) to apply the ShiroFilter. Struts provides
     * a staticResources loader which we don't seem to be using.
     */
    this.filter("/*").through(ShiroFilter.class);


    Map<String, String> initParams = new HashMap<>();
    initParams.put("actionPackages", "com.concretepage.action");

    /**
     * TODO
     * Do we want requests for static resources (e.g. images, css etc) to apply the StrutsPrepareAndExcuteFilter. Struts
     * provides a staticResources loader which we don't seem to be using.
     */
    this.filter("/*").through(StrutsPrepareAndExecuteFilter.class, initParams);
  }

}
