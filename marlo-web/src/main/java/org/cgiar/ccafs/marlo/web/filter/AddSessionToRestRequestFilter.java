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
import org.cgiar.ccafs.marlo.data.manager.CrpManager;
import org.cgiar.ccafs.marlo.data.model.Crp;
import org.cgiar.ccafs.marlo.security.BaseSecurityContext;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Our REST Requests need to have a CRP or Center in the session in order to pass
 * any authorization checks.
 * 
 * @author GrantL
 */
@Named("AddSessionToRestRequestFilter")
public class AddSessionToRestRequestFilter implements Filter {

  @Inject
  private BaseSecurityContext securityContext;

  @Inject
  private CrpManager crpManager;

  private final Logger LOG = LoggerFactory.getLogger(AddSessionToRestRequestFilter.class);

  @Override
  public void destroy() {
  }

  /**
   * Filters requests to remove URL-based session identifiers.
   */
  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
    throws IOException, ServletException {

    Subject subject = securityContext.getSubject();

    LOG.debug("check user : " + subject.getPrincipal() + " , has permissions to invoke rest api");

    Session session = SecurityUtils.getSubject().getSession();

    if (session.getAttribute(APConstants.SESSION_CRP) == null) {
      /**
       * The struts2 rest-plugin isn't able to handle URLS such as /api/{crp}/institutions or even a hard-coded
       * values such as /api/test/institutions. Therefore for our Realm to return an AuthorizationInfo object we need
       * to either have a CRP or a Center in the HTTP session. The BigData platform seemed the best hard-coded choice
       * for this.
       */
      Crp bigData = crpManager.findCrpByAcronym("BigData");

      session.setAttribute(APConstants.SESSION_CRP, bigData);
    }

    chain.doFilter(request, response);

  }

  @Override
  public void init(FilterConfig config) throws ServletException {
    LOG.debug("initializing AddSessionToRestRequestFilter");
  }
}