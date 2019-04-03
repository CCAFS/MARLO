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

package org.cgiar.ccafs.marlo.web.filter;


import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.ClarisaMonitoringManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.model.ClarisaMonitoring;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.security.APCustomRealm;
import org.cgiar.ccafs.marlo.security.BaseSecurityContext;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.Enumeration;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Our REST Requests need to have a CRP or Center in the session in order to pass
 * any authorization checks.
 * 
 * @author GrantL
 */
@Named("AddSessionToRestRequestFilter")
public class AddSessionToRestRequestFilter extends OncePerRequestFilter {

  @Inject
  private BaseSecurityContext securityContext;

  @Inject
  private GlobalUnitManager globalUnitManager;

  @Inject
  private UserManager userManager;

  @Inject
  private ClarisaMonitoringManager clarisaMonitoringManager;

  @Inject
  private APCustomRealm realm;

  private final Logger LOG = LoggerFactory.getLogger(AddSessionToRestRequestFilter.class);

  private void addCrpToSession(String globalUnitAcronym) {
    Subject subject = securityContext.getSubject();

    /**
     * Clear the users cached security info so that we handle an external system that is registered to many CRPs.
     */
    realm.clearCachedAuthorizationInfo(SecurityUtils.getSubject().getPrincipals());

    LOG.debug("check user : " + subject.getPrincipal() + " , has permissions to invoke rest api");

    Session session = SecurityUtils.getSubject().getSession();

    if (session.getAttribute(APConstants.SESSION_CRP) == null) {

      GlobalUnit globalUnit = globalUnitManager.findGlobalUnitByAcronym(globalUnitAcronym);

      session.setAttribute(APConstants.SESSION_CRP, globalUnit);


    }
  }


  /**
   * Add to the database the Service request information
   * 
   * @param serviceType - The type of service (GET, POST.. etc)
   * @param restApiString - The Url shorted by the Api elements
   * @param request - The http Request
   */
  private void addMonitoringInfo(String serviceType, String restApiString, HttpServletRequest request) {

    // initial variables
    String serviceName;
    String serviceArg = "";
    int maxArg;

    // New clarisa monitoring Object to add in the database
    ClarisaMonitoring monitoring = new ClarisaMonitoring();
    monitoring.setServiceType(serviceType);

    String[] split = restApiString.split("/");

    String arg1 = split[0];

    // If arg1 contains Crp Acronym the arg2 is the ServiceName, on the contrary the arg1 is the service name (Public
    // Service)
    GlobalUnit globalUnit = globalUnitManager.findGlobalUnitByAcronym(arg1);

    if (globalUnit != null) {
      serviceName = split[1];
      maxArg = 2;
    } else {
      serviceName = arg1;
      maxArg = 1;
    }

    // Get the url arguments if the request is GET
    if (serviceType.equals("GET")) {
      // If there are more than 2 args, theses args are the serviceArg, for example the id of the element to search.
      if (split.length > maxArg) {
        for (int i = maxArg; i < split.length; i++) {
          serviceArg = serviceArg + "-" + split[i];
        }
      }
    }

    // Get the Http request parameters
    Enumeration<String> enumeration = request.getParameterNames();
    while (enumeration.hasMoreElements()) {
      if (request.getParameter(enumeration.nextElement().toString()) != null) {
        serviceArg = serviceArg + "-" + request.getParameter(enumeration.nextElement().toString());
      }
    }


    // Get the user
    Subject subject = securityContext.getSubject();
    Long currentUserId = (Long) subject.getPrincipal();

    User user = userManager.getUser(currentUserId);

    // Save the information to Clarisa Monitoring Table
    monitoring.setServiceName(serviceName);
    monitoring.setGlobalUnit(globalUnit);
    monitoring.setUser(user);
    monitoring.setServiceArg(serviceArg);
    monitoring.setDate(new Date());

    clarisaMonitoringManager.saveClarisaMonitoring(monitoring);


  }


  @Override
  public void destroy() {
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
    throws ServletException, IOException {

    URL url = new URL(request.getRequestURL().toString());

    // Get the HTTP method with which thisrequest was made, for example, GET, POST, or PUT.
    String serviceType = request.getMethod();

    String path = url.getPath();

    String restApiString = StringUtils.substringAfter(path, "/api/");

    String[] split = restApiString.split("/");

    String globalUnitAcronym = split[0];

    // Check to see if a swagger request and if so, skip trying to extract the globalUnit from the url.
    if (StringUtils.isNotEmpty(globalUnitAcronym) && !globalUnitAcronym.equals("v2")
      && !globalUnitAcronym.equals("swagger-ui.html") && !globalUnitAcronym.equals("webjars")
      && !globalUnitAcronym.equals("swagger-resources") && !globalUnitAcronym.equals("configuration")
      && !globalUnitAcronym.equals("index.html") && !globalUnitAcronym.contains(".js")
      && !globalUnitAcronym.contains(".css") && !globalUnitAcronym.contains(".jpg")
      && !globalUnitAcronym.contains(".png")) {
      this.addCrpToSession(globalUnitAcronym);
      this.addMonitoringInfo(serviceType, restApiString, request);
    }

    filterChain.doFilter(request, response);
  }


}