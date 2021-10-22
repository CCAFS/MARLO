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
import org.cgiar.ccafs.marlo.data.manager.LocElementManager;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.model.ClarisaMonitoring;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.LocElement;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.security.APCustomRealm;
import org.cgiar.ccafs.marlo.security.BaseSecurityContext;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Enumeration;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CountryResponse;
import com.maxmind.geoip2.record.Country;
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
  private LocElementManager locElementManager;

  @Inject
  APConfig config;

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
  private void addMonitoringInfo(String serviceType, String restApiString, HttpServletRequest request,
    HttpServletResponse response) {

    // initial variables
    String serviceName;

    // New clarisa monitoring Object to add in the database
    ClarisaMonitoring monitoring = new ClarisaMonitoring();
    monitoring.setServiceType(serviceType);

    monitoring.setServiceUrl(request.getRequestURL().toString());

    String ip = "";
    // Get the Ip Public
    try {
      URL whatismyip = new URL("http://checkip.amazonaws.com");
      BufferedReader in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));

      ip = in.readLine();

      monitoring.setUserIp(ip);
    } catch (Exception e) {
      // TODO: get message
      monitoring.setUserIp("");
    }


    String[] split = restApiString.split("/");

    String arg1 = split[0];

    // If arg1 contains Crp Acronym the arg2 is the ServiceName, else the arg1 is the service name (Public
    // Service)
    GlobalUnit globalUnit = globalUnitManager.findGlobalUnitByAcronym(arg1);

    if (globalUnit != null) {
      serviceName = split[1];
    } else {
      serviceName = arg1;
    }

    // Get the Http request parameters
    Enumeration<String> enumeration = request.getParameterNames();
    while (enumeration.hasMoreElements()) {
      if (request.getParameter(enumeration.nextElement().toString()) != null) {
        // TODO try to get all the parameters (POST) specially the JSON structure
      }
    }

    // Get the country
    LocElement locElement = this.getCountrybyIp(monitoring.getUserIp());

    // Get the user
    Subject subject = securityContext.getSubject();
    Long currentUserId = (Long) subject.getPrincipal();

    User user = userManager.getUser(currentUserId);

    // Save the information to Clarisa Monitoring Table
    monitoring.setServiceName(serviceName);
    monitoring.setGlobalUnit(globalUnit);
    monitoring.setUser(user);
    monitoring.setLocElement(locElement);
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


    if (StringUtils.isNotEmpty(globalUnitAcronym) && (globalUnitAcronym.equals("index.html")
      || globalUnitAcronym.equals("home.html") || globalUnitAcronym.equals("api.html")
      || globalUnitAcronym.equals("additionalServices.html") || globalUnitAcronym.equals("generalListReference.html")
      || globalUnitAcronym.equals("financialoperation.html"))) {
      if (this.isPublicUser()) {
        Subject subject = SecurityUtils.getSubject();
        subject.logout();
      }
    }

    // Check to see if a swagger request and if so, skip trying to extract the globalUnit from the url.
    if (StringUtils.isNotEmpty(globalUnitAcronym) && !globalUnitAcronym.equals("v2")
      && !globalUnitAcronym.equals("swagger-ui.html") && !globalUnitAcronym.equals("webjars")
      && !globalUnitAcronym.equals("swagger-resources") && !globalUnitAcronym.equals("configuration")
      && !globalUnitAcronym.equals("index.html") && !globalUnitAcronym.equals("home.html")
      && !globalUnitAcronym.equals("api.html") && !globalUnitAcronym.equals("generalListReference.html")
      && !globalUnitAcronym.equals("financialoperation.html") && !globalUnitAcronym.contains(".js")
      && !globalUnitAcronym.contains(".css") && !globalUnitAcronym.contains(".jpg")
      && !globalUnitAcronym.contains(".png")) {
      this.addCrpToSession(globalUnitAcronym);
      this.addMonitoringInfo(serviceType, restApiString, request, response);
    }


    filterChain.doFilter(request, response);
  }

  /**
   * Get the Country trough the Remote IP address
   * 
   * @param ip - The remote ip address
   * @return
   */
  public LocElement getCountrybyIp(String ip) {

    LocElement element = new LocElement();

    try {

      File countryFile = this.getCountryFilePath().toFile();

      DatabaseReader reader = new DatabaseReader.Builder(countryFile).build();

      InetAddress ipAddress = InetAddress.getByName(ip);

      CountryResponse countryResonse = reader.country(ipAddress);

      Country country = countryResonse.getCountry();

      String isoCode = country.getIsoCode();

      element = locElementManager.getLocElementByISOCode(isoCode);

    } catch (IOException e) {
      e.printStackTrace();
      element = null;
    } catch (GeoIp2Exception e) {
      e.printStackTrace();
      element = null;
    }


    return element;

  }

  private Path getCountryFilePath() {
    String countryFilename = APConstants.DATABASE_COUNTRY_FILENAME;
    return Paths.get(config.getClarisaMapDatabase() + countryFilename);
  }

  private boolean isPublicUser() {
    Subject subject = SecurityUtils.getSubject();
    if (subject.isAuthenticated()) {
      Session session = subject.getSession();
      if (session.getAttribute(APConstants.CLARISA_PUBLIC) != null) {
        return true;
      }
    }
    return false;
  }


}