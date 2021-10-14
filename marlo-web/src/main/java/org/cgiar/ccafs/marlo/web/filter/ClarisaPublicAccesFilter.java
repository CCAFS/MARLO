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
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.utils.APConfig;

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
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Filter to create an automatic public user session to consume the
 * 
 * @author Hermes Jiménez - CIAT/CCAFS
 */

@Named("ClarisaPublicAccesFilter")
public class ClarisaPublicAccesFilter extends OncePerRequestFilter {

  @Inject
  APConfig config;

  @Inject
  private UserManager userManager;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
    throws ServletException, IOException {

    URL url = new URL(request.getRequestURL().toString());

    String path = url.getPath();

    String swaggerString = StringUtils.substringAfter(path, "/swagger/");

    String[] split = swaggerString.split("/");

    String indexString = split[0];

    if (StringUtils.isNotEmpty(indexString)
      && (indexString.equals("index.html") || indexString.equals("api.html") || indexString.equals("home.html")
        || indexString.equals("generalListReference.html") || indexString.equals("additionalServices.html"))) {

      Subject subject = SecurityUtils.getSubject();

      // Verify if there not are public user session
      if (!subject.isAuthenticated()) {
        String user = this.config.getClarisaUser();
        String password = this.config.getClarisaPassword();

        User userobj = userManager.login(user, password);
        if (userobj != null) {
          UsernamePasswordToken token = new UsernamePasswordToken(user, password);
          subject.login(token);
        }
        Session session = subject.getSession();
        session.setAttribute(APConstants.CLARISA_PUBLIC, true);
      }
    }

    filterChain.doFilter(request, response);
  }

}
