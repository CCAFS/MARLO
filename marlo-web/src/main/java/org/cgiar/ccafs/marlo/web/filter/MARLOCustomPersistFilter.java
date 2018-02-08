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

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.SessionFactory;
import org.hibernate.StaleObjectStateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * A Filter that implements the 'open session in view' pattern. See link here
 * https://developer.jboss.org/wiki/OpenSessionInView#jive_content_id_Using_an_interceptor
 * Be aware that some consider this an anti-pattern see :
 * https://vladmihalcea.com/2016/05/30/the-open-session-in-view-anti-pattern
 * However if we want to have the transaction boundary at the Service Layer (currently called Manager in MARLO) then
 * this would involve a complete refactor of the application so that the logic is moved out of the Struts2 Actions and
 * into the Service Layer (also note that when compiling FreeMarker templates that all entities and their required
 * relations would need to be fetched in the Struts2 prepare phase).
 * Therefore considering the above this filter could be considered as an intermediate step. It partially solves our
 * current performance issues by creating a single Transaction per request (and creates an AuditLogContext as well)
 * whereas before each DAO call MARLO was creating a new transaction and this caused connection pool problems.
 * 
 * @author GrantL
 */
@Named("MARLOCustomPersistFilter")
public class MARLOCustomPersistFilter extends OncePerRequestFilter {

  @Inject
  private SessionFactory sessionFactory;

  private final Logger LOG = LoggerFactory.getLogger(MARLOCustomPersistFilter.class);

  /**
   * TODO check to see if the sessionFactory gets closed when the app
   * gets redeployed. If not then there will be a memory leak on deployment.
   */
  @Override
  public void destroy() {
    LOG.info("Closing MARLO Hibernate SessionFactory");
    sessionFactory.close();
  }

  @Override
  protected void doFilterInternal(HttpServletRequest httpRequest, HttpServletResponse response, FilterChain chain)
    throws ServletException, IOException {
    String url = httpRequest.getRequestURL().toString();
    String queryString = httpRequest.getQueryString();
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(" [").append(httpRequest.getMethod()).append("] ");

    stringBuilder.append(url);
    if (queryString != null) {
      stringBuilder.append("?").append(queryString);
    }

    String requestUrl = stringBuilder.toString();

    try {
      // Create an AuditLogContext for AuditLogging
      AuditLogContextProvider.push(new AuditLogContext());

      LOG.debug("begin doFilter for MARLOCustomPersistFilter for request: " + requestUrl);
      sessionFactory.getCurrentSession().beginTransaction();

      // Continue filter chain
      chain.doFilter(httpRequest, response);

      if (sessionFactory.getCurrentSession() != null && sessionFactory.getCurrentSession().getTransaction() != null) {
        sessionFactory.getCurrentSession().getTransaction().commit();

      }


    } catch (StaleObjectStateException staleEx) {
      LOG.error("This interceptor does not implement optimistic concurrency control!");
      LOG.error("Your application will not work until you add compensation actions!");
      // Rollback, close everything, possibly compensate for any permanent changes
      // during the conversation, and finally restart business conversation. Maybe
      // give the user of the application a chance to merge some of his work with
      // fresh data... what you do here depends on your applications design.
      throw staleEx;
    } catch (Throwable ex) {

      // Rollback only
      LOG.error("Exception occurred when trying to commit transaction");
      try {
        if (sessionFactory.getCurrentSession().getTransaction().isActive()) {
          LOG.info("Trying to rollback database transaction after exception");
          sessionFactory.getCurrentSession().getTransaction().rollback();
        }
      } catch (Throwable rbEx) {
        LOG.error("Could not rollback transaction after exception!", rbEx);
      }

      // Let others handle it... maybe another interceptor for exceptions?
      throw new ServletException(ex);
    }
    /**
     * We want to decouple or AuditLogInterceptor from our DAOs so we need a mechanism to
     * pass entity values from the DAOs to the Hibernate Interceptors/Hibernate Listeners.
     */
    finally {
      LOG.debug("clean up AuditLogHelper for MARLOCustomPersistFilter request : " + requestUrl);
      // This must get executed in a finally block or otherwise we risk a memory leak.
      AuditLogContextProvider.pop();
    }


  }

}
