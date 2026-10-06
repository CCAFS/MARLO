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

import org.cgiar.ccafs.marlo.logging.LogContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.Result;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import org.apache.shiro.authz.UnauthorizedException;
import org.apache.struts2.config.StrutsXmlConfigurationProvider;
import org.apache.struts2.junit.XWorkJUnit4TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Runs ErrorStatusLogInterceptor inside real Struts invocations (DefaultActionInvocation, the struts-default exception
 * mapping and chain result), loaded from error-status-log-test-struts.xml, to prove what a proxied invocation cannot:
 * the PreResultListener sees the code whether an action or an interceptor returned it or the exception mapping produced
 * it, an exception is never swallowed, a chained action is logged once, and the page is rendered as before.
 */
public class ErrorStatusLogInterceptorStrutsTest extends XWorkJUnit4TestCase {

  /**
   * Stops the chain with 403, as the MARLO permission interceptors do.
   */
  public static class DenyInterceptor extends AbstractInterceptor {

    private static final long serialVersionUID = 1L;

    @Override
    public String intercept(ActionInvocation invocation) {
      return "403";
    }
  }

  /**
   * Fails with an exception that no mapping handles.
   */
  public static class FailingAction {

    public String execute() {
      throw new IllegalStateException("failing action");
    }
  }

  /**
   * Records which result codes were rendered, in place of the FreeMarker pages.
   */
  public static class RecordingResult implements Result {

    private static final long serialVersionUID = 1L;

    @Override
    public void execute(ActionInvocation invocation) {
      RENDERED.add(invocation.getResultCode());
    }
  }

  /**
   * Returns the result code configured as a static parameter.
   */
  public static class ResultAction {

    private String resultCode = "success";

    public String execute() {
      return this.resultCode;
    }

    public void setResultCode(String resultCode) {
      this.resultCode = resultCode;
    }
  }

  /**
   * Fails as Shiro does when a permission check is denied; struts maps it to the 403 result.
   */
  public static class ShiroDeniedAction {

    public String execute() {
      throw new UnauthorizedException("denied");
    }
  }

  private static final List<String> RENDERED = new ArrayList<>();

  private ListAppender<ILoggingEvent> appender;
  private Logger logger;
  private Level previousLevel;

  private String execute(String namespace, String actionName) throws Exception {
    Map<String, Object> extraContext = ActionContext.of(new HashMap<>()).withSession(new HashMap<>()).getContextMap();
    return this.actionProxyFactory.createActionProxy(namespace, actionName, null, extraContext).execute();
  }

  private void assertOneLine(String statusCode) {
    assertEquals("exactly one line must be logged", 1, this.appender.list.size());
    assertEquals(statusCode, this.appender.list.get(0).getMDCPropertyMap().get(LogContext.STATUS_CODE));
  }

  @Before
  public void loadTestConfiguration() {
    this.loadConfigurationProviders(new StrutsXmlConfigurationProvider("struts-default.xml"),
      new StrutsXmlConfigurationProvider("error-status-log-test-struts.xml"));
    RENDERED.clear();
    LogContext.clear();
    this.logger = (Logger) LoggerFactory.getLogger(ErrorStatusLogInterceptor.class);
    this.previousLevel = this.logger.getLevel();
    this.logger.setLevel(Level.INFO);
    this.appender = new ListAppender<>();
    this.appender.start();
    this.logger.addAppender(this.appender);
  }

  @After
  public void restoreLogger() {
    this.logger.detachAppender(this.appender);
    this.logger.setLevel(this.previousLevel);
    LogContext.clear();
  }

  @Test
  public void aChainedActionIsLoggedOnce() throws Exception {
    this.execute("/", "chained");
    this.assertOneLine("404");
    assertEquals("the chained 404 page must be rendered", "[404]", RENDERED.toString());
  }

  @Test
  public void aNotLoggedResultReturnedByTheActionIsLoggedWith401() throws Exception {
    assertEquals("401", this.execute("/", "notLogged"));
    this.assertOneLine("401");
    assertEquals("the 401 page must still be rendered", "[401]", RENDERED.toString());
  }

  @Test
  public void aNotFoundResultReturnedByTheActionIsLoggedWith404() throws Exception {
    assertEquals("404", this.execute("/", "notFound"));
    this.assertOneLine("404");
    assertEquals("the 404 page must still be rendered", "[404]", RENDERED.toString());
  }

  @Test
  public void anInterceptorThatStopsTheChainIsLoggedWith403() throws Exception {
    assertEquals("403", this.execute("/", "denied"));
    this.assertOneLine("403");
    assertEquals("the 403 page must still be rendered", "[403]", RENDERED.toString());
  }

  @Test
  public void anUnhandledExceptionStillReachesTheCaller() throws Exception {
    try {
      this.execute("/", "failing");
      fail("the exception must not be swallowed");
    } catch (IllegalStateException e) {
      assertEquals("failing action", e.getMessage());
    }
    assertTrue("nothing must be logged for a request that never reached a result", this.appender.list.isEmpty());
    assertNull(MDC.get(LogContext.STATUS_CODE));
  }

  @Test
  public void aResultWithoutPageIsLoggedWithoutStatusAndStillFailsAsBefore() throws Exception {
    try {
      this.execute("/noResults", "unmapped");
      fail("a result code with no result must still fail, as it does without the interceptor");
    } catch (Exception e) {
      assertTrue(e.getMessage(), e.getMessage().contains("No result defined"));
    }
    assertEquals("exactly one line must be logged", 1, this.appender.list.size());
    assertTrue(this.appender.list.get(0).getFormattedMessage().contains("has no result for it"));
    assertNull("a status the client never receives must not be logged",
      this.appender.list.get(0).getMDCPropertyMap().get(LogContext.STATUS_CODE));
    assertNull(MDC.get(LogContext.STATUS_CODE));
  }

  @Test
  public void aShiroAuthorizationExceptionMappedTo403IsLoggedWith403() throws Exception {
    assertEquals("403", this.execute("/", "shiroDenied"));
    this.assertOneLine("403");
    assertEquals("the 403 page must still be rendered", "[403]", RENDERED.toString());
  }

  @Test
  public void aSuccessfulRequestIsNotLogged() throws Exception {
    assertEquals("success", this.execute("/", "ok"));
    assertTrue("no line must be logged", this.appender.list.isEmpty());
    assertNull(MDC.get(LogContext.STATUS_CODE));
    assertEquals("[success]", RENDERED.toString());
  }

}
