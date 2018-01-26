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


import org.cgiar.ccafs.marlo.rest.errors.ErrorConstants;
import org.cgiar.ccafs.marlo.rest.errors.ErrorDTO;

import java.io.IOException;

import javax.inject.Named;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * This Filter catches any exceptions that were thrown before the Spring Dispatcher Servlet gets
 * executed (i.e. in Filters).
 * 
 * @author GrantL
 */
@Named("ExceptionHandlerFilter")
public class ExceptionHandlerFilter extends OncePerRequestFilter {

  private final Logger LOG = LoggerFactory.getLogger(ExceptionHandlerFilter.class);

  public String convertObjectToJson(Object object) throws JsonProcessingException {
    if (object == null) {
      return null;
    }
    ObjectMapper mapper = new ObjectMapper();
    return mapper.writeValueAsString(object);
  }


  @Override
  public void destroy() {
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
    throws ServletException, IOException {

    try {
      filterChain.doFilter(request, response);
    } catch (RuntimeException e) {
      LOG.error(e.getMessage(), e);
      ErrorDTO errorDto = new ErrorDTO(ErrorConstants.ERR_INTERNAL_SERVER, e.getMessage());

      response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
      response.getWriter().write(this.convertObjectToJson(errorDto));
    }
  }
}