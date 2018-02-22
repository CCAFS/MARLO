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
import javax.servlet.http.HttpServletResponseWrapper;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Protects from exposing session ids in URLs for security reasons.
 * Does the following:
 * <ul>
 * <li>invalidates session if session id is exposed in the URL</li>
 * <li>removes session id from URLs.</li>
 * </ul>
 */
@Named("RemoveSessionFromUrlFilter")
public class RemoveSessionFromUrlFilter extends OncePerRequestFilter {

  private final Logger LOG = LoggerFactory.getLogger(RemoveSessionFromUrlFilter.class);

  @Override
  public void destroy() {
  }

  /**
   * Filters requests to remove URL-based session identifiers.
   */
  @Override
  public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
    throws IOException, ServletException {


    HttpServletRequest httpRequest = request;
    HttpServletResponse httpResponse = response;

    if (this.isRequestedSessionIdFromURL(httpRequest)) {
      HttpSession session = httpRequest.getSession(false);

      if (session != null) {
        String sessionId = session.getId();
        session.invalidate(); // clear session if session id in URL
        LOG.debug("Removed Session Id: " + sessionId + " from URL");
      }
    }

    // wrap response to remove URL encoding
    HttpServletResponseWrapper wrappedResponse = new HttpServletResponseWrapper(httpResponse) {

      @Override
      public String encodeRedirectUrl(String url) {
        return url;
      }

      @Override
      public String encodeRedirectURL(String url) {
        return url;
      }

      @Override
      public String encodeUrl(String url) {
        return url;
      }

      @Override
      public String encodeURL(String url) {
        return url;
      }
    };

    chain.doFilter(request, wrappedResponse);
  }

  /**
   * Detects if session ID exist in the URL. It works more reliable
   * than <code>servletRequest.isRequestedSessionIdFromURL()</code>.
   */
  protected boolean isRequestedSessionIdFromURL(HttpServletRequest servletRequest) {
    if (servletRequest.isRequestedSessionIdFromURL()) {
      return true;
    }

    HttpSession session = servletRequest.getSession(false);
    if (session != null) {
      String sessionId = session.getId();
      StringBuffer requestUri = servletRequest.getRequestURL();

      return requestUri.indexOf(sessionId) != -1;
    }

    return false;
  }
}