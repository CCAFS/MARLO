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

import org.cgiar.ccafs.marlo.utils.AuditLogContext;
import org.cgiar.ccafs.marlo.utils.AuditLogContextProvider;

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
 * This filter is required as our Audit column pre update and pre insert listeners require the current User id.
 * However if we try to use Shiro's getSubject, sometimes the code will fail as the ShiroFilter has already exited
 * when the transaction is committed in the MARLOCustomPersistFilter.
 * This filter needs to be executed after the ShiroFilter so that the Subject is available.
 * 
 * @author GrantL
 */
@Named("AddUserIdFilter")
public class AddUserIdFilter extends OncePerRequestFilter {

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
    throws ServletException, IOException {

    AuditLogContext auditLogContext = AuditLogContextProvider.getAuditLogContext();

    Subject subject = SecurityUtils.getSubject();

    Long currentUserId = (Long) subject.getPrincipal();

    auditLogContext.setCurrentUserId(currentUserId);

    filterChain.doFilter(request, response);
  }


}
