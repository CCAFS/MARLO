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

import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.apache.struts2.dispatcher.filter.StrutsPrepareAndExecuteFilter;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.DelegatingFilterProxy;

/**
 * This is the servlet 3.0 way to load a web context wihtout (or combined with) a web.xml file.
 */
public class WebAppInitializer implements WebApplicationInitializer {

  @Override
  public void onStartup(ServletContext servletContext) throws ServletException {
    AnnotationConfigWebApplicationContext appContext = new AnnotationConfigWebApplicationContext();
    appContext.register(ApplicationContextConfig.class, MarloDatabaseConfiguration.class,
      MarloShiroConfiguration.class);

    ContextLoaderListener contextLoaderListener = new ContextLoaderListener(appContext);
    servletContext.addListener(contextLoaderListener);


    FilterRegistration.Dynamic removeSessionFromUrlFilter =
      servletContext.addFilter("RemoveSessionFromUrlFilter", new DelegatingFilterProxy("RemoveSessionFromUrlFilter"));
    removeSessionFromUrlFilter.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "/*");
    /**
     * Need to use the DelegatingFilterProxy to allow the SessionFactory to be injected.
     */
    FilterRegistration.Dynamic marloOpenSessionInViewFilter =
      servletContext.addFilter("MARLOCustomPersistFilter", new DelegatingFilterProxy("MARLOCustomPersistFilter"));
    /**
     * URL patterns are to exclude creating transactions for fetching static resources.
     */
    marloOpenSessionInViewFilter.setInitParameter("targetFilterLifecycle", "true");
    marloOpenSessionInViewFilter.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "*.do", "*.json",
      "/");

    /**
     * Need to use the DelegatingFilterProxy to allow the shiroFilter to be instantiated properly.
     */
    FilterRegistration.Dynamic shiroFilter =
      servletContext.addFilter("shiroFilter", new DelegatingFilterProxy("shiroFilter"));
    shiroFilter.setInitParameter("targetFilterLifecycle", "true");
    shiroFilter.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "/*");

    FilterRegistration.Dynamic struts2Filter =
      servletContext.addFilter("StrutsDispatcher", new StrutsPrepareAndExecuteFilter());
    struts2Filter.setInitParameter("actionPackages", "com.concretepage.action");
    struts2Filter.setInitParameter("targetFilterLifecycle", "true");
    struts2Filter.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "/*");
  }
}
