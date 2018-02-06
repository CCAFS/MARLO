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
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.security.APCustomRealm;
import org.cgiar.ccafs.marlo.security.BaseSecurityContext;

import java.io.IOException;
import java.net.URL;

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

      if (globalUnit == null) {
        throw new IllegalArgumentException("globalUnit with acronymn: " + globalUnitAcronym + ", could not be found");
      }

      session.setAttribute(APConstants.SESSION_CRP, globalUnit);
    }
  }


  @Override
  public void destroy() {
  }


  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
    throws ServletException, IOException {

    URL url = new URL(request.getRequestURL().toString());

    String path = url.getPath();

    String restApiString = StringUtils.substringAfter(path, "/api/");

    String[] split = restApiString.split("/");

    String globalUnitAcronym = split[0];

    // Check to see if a swagger request and if so, skip trying to extract the globalUnit from the url.
    if (StringUtils.isNotEmpty(globalUnitAcronym) && !globalUnitAcronym.equals("v2")
      && !globalUnitAcronym.equals("swagger-ui.html") && !globalUnitAcronym.equals("webjars")
      && !globalUnitAcronym.equals("swagger-resources") && !globalUnitAcronym.equals("configuration")) {
      this.addCrpToSession(globalUnitAcronym);
    }

    filterChain.doFilter(request, response);
  }


}