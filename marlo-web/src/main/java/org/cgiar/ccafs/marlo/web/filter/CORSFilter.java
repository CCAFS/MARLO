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

/**************
 * @author Diego Perez - CIAT/CCAFS
 **************/

package org.cgiar.ccafs.marlo.web.filter;

import java.io.IOException;

import javax.inject.Named;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.filter.OncePerRequestFilter;

@Named("CORSFilter")
public class CORSFilter extends OncePerRequestFilter {

  @Override
  protected void doFilterInternal(HttpServletRequest servletRequest, HttpServletResponse servletResponse,
    FilterChain filterChain) throws ServletException, IOException {
    HttpServletRequest request = servletRequest;
    System.out.println("CORSFilter HTTP Request: " + request.getMethod());

    // Authorize (allow) all domains to consume the content
    servletResponse.addHeader("Access-Control-Allow-Origin", "*");
    servletResponse.addHeader("Access-Control-Allow-Methods", "GET, OPTIONS, HEAD, PUT, POST");

    HttpServletResponse resp = servletResponse;

    // For HTTP OPTIONS verb/method reply with ACCEPTED status code -- per CORS handshake
    if (request.getMethod().equals("OPTIONS")) {
      resp.setStatus(HttpServletResponse.SC_ACCEPTED);
      return;
    }

    // pass the request along the filter chain
    filterChain.doFilter(request, servletResponse);

  }

}
