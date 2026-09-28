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

package org.cgiar.ccafs.marlo.utils;

import java.lang.reflect.Proxy;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionProxy;
import org.junit.After;
import org.junit.Test;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * SendMailS records in email_logs.source_action where each email was sent from. It runs inside a Struts action, a
 * Spring MVC REST request or a background thread, and each of the three has to give a usable answer.
 * The Struts and servlet types are interfaces, so they are stood in for by dynamic proxies that answer only the
 * methods the code reads.
 */
public class SendMailSSourceTest {

  @SuppressWarnings("unchecked")
  private static <T> T stub(Class<T> type, String method, Object value) {
    return (T) Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[] {type},
      (proxy, invoked, args) -> invoked.getName().equals(method) ? value : null);
  }

  private static ActionInvocation invocation(String namespace, String actionName) {
    ActionProxy actionProxy = (ActionProxy) Proxy.newProxyInstance(ActionProxy.class.getClassLoader(),
      new Class<?>[] {ActionProxy.class}, (proxy, invoked, args) -> {
        if ("getNamespace".equals(invoked.getName())) {
          return namespace;
        }
        return "getActionName".equals(invoked.getName()) ? actionName : null;
      });
    return stub(ActionInvocation.class, "getProxy", actionProxy);
  }

  private final SendMailS sendMail = new SendMailS(null, null, null, null);

  @After
  public void tearDown() {
    ActionContext.clear();
    RequestContextHolder.resetRequestAttributes();
  }

  @Test
  public void testBackgroundThreadHasNoSource() {
    assertNull(sendMail.getRequestSource());
  }

  @Test
  public void testRestRequestIsItsUri() {
    RequestContextHolder.setRequestAttributes(
      new ServletRequestAttributes(stub(HttpServletRequest.class, "getRequestURI", "/api/users/notify")));

    assertEquals("/api/users/notify", sendMail.getRequestSource());
  }

  @Test
  public void testRootNamespaceIsNotDoubled() {
    ActionContext.of(new HashMap<>()).withActionInvocation(invocation("/", "manageUsers")).bind();

    assertEquals("/manageUsers", sendMail.getRequestSource());
  }

  @Test
  public void testStrutsActionIsNamespaceAndName() {
    ActionContext.of(new HashMap<>()).withActionInvocation(invocation("/projects", "partners")).bind();

    assertEquals("/projects/partners", sendMail.getRequestSource());
  }
}
