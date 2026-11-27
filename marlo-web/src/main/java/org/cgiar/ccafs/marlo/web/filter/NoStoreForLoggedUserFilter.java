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

import java.io.IOException;

import javax.inject.Named;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * A2-2751: forbids the browser from keeping a copy of any dynamic response served to a logged user. Without it, the
 * browser's Back button redisplayed the dashboard from its cache after logout, so the next person at a shared computer
 * could read it. With "no-store" the browser has to ask the server again, which answers with the login page once the
 * session is gone.
 * Only responses to a logged user are marked: the login page and other anonymous responses keep today's caching. It
 * is mapped to the non-static requests only, so CSS, JS and images keep theirs. It must run after the Shiro filter,
 * which binds the Subject read here.
 */
@Named("NoStoreForLoggedUserFilter")
public class NoStoreForLoggedUserFilter extends OncePerRequestFilter {

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
    throws ServletException, IOException {

    // Same test as AddUserIdFilter: a principal also covers a remembered user, whom isAuthenticated() leaves out
    Subject subject = SecurityUtils.getSubject();
    if (subject != null && subject.getPrincipal() != null) {
      // Set before the chain runs, while the response cannot be committed yet
      response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, private");
      response.setHeader("Pragma", "no-cache");
      response.setDateHeader("Expires", 0);
    }

    filterChain.doFilter(request, response);
  }

}
