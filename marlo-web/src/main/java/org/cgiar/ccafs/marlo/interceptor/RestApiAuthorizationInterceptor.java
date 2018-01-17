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

package org.cgiar.ccafs.marlo.interceptor;

import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.CrpManager;
import org.cgiar.ccafs.marlo.data.model.Crp;
import org.cgiar.ccafs.marlo.security.BaseSecurityContext;
import org.cgiar.ccafs.marlo.security.Permission;

import javax.inject.Inject;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RestApiAuthorizationInterceptor extends AbstractInterceptor {

  private static final long serialVersionUID = 3747497854060930008L;

  private static final Logger LOG = LoggerFactory.getLogger(RestApiAuthorizationInterceptor.class);

  private final BaseSecurityContext securityContext;

  private final CrpManager crpManager;

  @Inject
  public RestApiAuthorizationInterceptor(BaseSecurityContext securityContext, CrpManager crpManager) {
    this.securityContext = securityContext;
    this.crpManager = crpManager;
  }


  @Override
  public String intercept(ActionInvocation invocation) throws Exception {

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


    if (securityContext.hasPermission(Permission.FULL_REST_API_PERMISSION)) {
      return invocation.invoke();
    }

    String message = "User with id: " + subject.getPrincipal() + ", does not have permission to access rest api";

    LOG.error(message);

    throw new AuthorizationException(message);
  }

}
