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

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.List;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import org.junit.After;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Covers CHG-COGNITO-AUTH-001-T08's decision to <b>not</b> register {@code cognitoLogin.do} (and, by the same
 * reasoning, {@code cognitoCallback.do}) with the shared {@code unloggedStack} interceptor stack, even though
 * both {@code design.md} 8 and {@code tasks.md} T08 say to.
 * <p>
 * {@code unloggedStack} (struts.xml) is {@code i18nFile}, {@code validCrp}, {@code validSessionCrp},
 * {@code defaultStack}. {@link ValidCrpActionInterceptor} and {@code ValidSessionCrpInterceptor} both split
 * {@code ActionContext.getContext().getActionName()} on {@code "/"} and return
 * {@link BaseAction#NOT_FOUND} when there is no second segment -- a rule written for
 * {@code {crp}/actionName} URLs like {@code {*}/crpDashboard} and {@code {crp}/userSummary}, the only shapes
 * {@code unloggedStack} is used with anywhere else in this codebase today. {@code cognitoLogin.do} and
 * {@code cognitoCallback.do} are flat action names, exactly like {@code login.do} and {@code logout.do} above
 * them in {@code struts-home.xml} -- the Global Unit is not yet known from the URL at authorize time.
 * <p>
 * This test drives the <b>real</b> {@link ValidCrpActionInterceptor} -- the first member of
 * {@code unloggedStack} -- against exactly the action name Struts assigns to {@code cognitoLogin.do}. It is
 * the same class of proof this spec's own T06 audit demanded twice: a unit that is correct in isolation can
 * still be unreachable through the framework that actually calls it, and only a test that drives the real
 * framework component can tell the difference. Calling {@code intercept()} directly (same-package-style
 * access, matching {@code APCustomRealmDispatchTest}'s precedent for the realm) is the closest this
 * dependency-injection-free, container-free test suite can get to that without a running Tomcat.
 */
public class CognitoUnloggedStackReachabilityTest {

  @After
  public void tearDown() {
    ActionContext.clear();
  }

  /**
   * If this test ever starts failing because {@code invocation.invoke()} was reached, that is <b>good
   * news</b> for reachability but means this test's premise (and the struts-home.xml deviation it justifies)
   * needs re-examination -- not that the assertion should be loosened.
   */
  @Test
  public void unloggedStacksValidCrpMemberRejectsAFlatActionNameLikeCognitoLogin() throws Exception {
    ActionContext.of(new HashMap<String, Object>()).withActionName("cognitoLogin")
      .withSession(new HashMap<String, Object>()).bind();

    ValidCrpActionInterceptor interceptor = new ValidCrpActionInterceptor(new ExplodingGlobalUnitManager());
    ActionInvocation invocation = this.actionInvocationThatExplodesIfInvoked();

    String result = interceptor.intercept(invocation);

    assertEquals("a bare action name (no {crp}/ prefix) has no '/' to split on, so validCrp returns "
      + "NOT_FOUND unconditionally -- before validSessionCrp, defaultStack, or the action itself ever run. "
      + "This is why cognitoLogin.do/cognitoCallback.do cannot register with unloggedStack verbatim.",
      BaseAction.NOT_FOUND, result);
  }

  private ActionInvocation actionInvocationThatExplodesIfInvoked() {
    return (ActionInvocation) Proxy.newProxyInstance(this.getClass().getClassLoader(),
      new Class<?>[] {ActionInvocation.class}, new InvocationHandler() {

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) {
          if ("getInvocationContext".equals(method.getName())) {
            return ActionContext.getContext();
          }
          if ("invoke".equals(method.getName())) {
            throw new AssertionError(
              "cognitoLogin.do must never reach invocation.invoke() through unloggedStack's validCrp member");
          }
          Class<?> returnType = method.getReturnType();
          if (returnType == boolean.class) {
            return Boolean.FALSE;
          }
          if (returnType == int.class) {
            return Integer.valueOf(0);
          }
          if (returnType == long.class) {
            return Long.valueOf(0L);
          }
          return null;
        }
      });
  }

  /** Never reached: {@code validCrp} returns before calling this for a flat (no {@code "/"}) action name. */
  private static final class ExplodingGlobalUnitManager implements GlobalUnitManager {

    @Override
    public List<GlobalUnit> crpUsers(String email) {
      throw new AssertionError("must not be called: validCrp returns before this for a flat action name");
    }

    @Override
    public void deleteGlobalUnit(long globalUnitId) {
      throw new AssertionError("must not be called: validCrp returns before this for a flat action name");
    }

    @Override
    public boolean existGlobalUnit(long globalUnitID) {
      throw new AssertionError("must not be called: validCrp returns before this for a flat action name");
    }

    @Override
    public List<GlobalUnit> findAll() {
      throw new AssertionError("must not be called: validCrp returns before this for a flat action name");
    }

    @Override
    public GlobalUnit findGlobalUnitByAcronym(String acronym) {
      throw new AssertionError("must not be called: validCrp returns before this for a flat action name");
    }

    @Override
    public GlobalUnit findGlobalUnitBySMOCode(String smoCode) {
      throw new AssertionError("must not be called: validCrp returns before this for a flat action name");
    }

    @Override
    public GlobalUnit getGlobalUnitById(long globalUnitID) {
      throw new AssertionError("must not be called: validCrp returns before this for a flat action name");
    }

    @Override
    public GlobalUnit saveGlobalUnit(GlobalUnit globalUnit) {
      throw new AssertionError("must not be called: validCrp returns before this for a flat action name");
    }
  }
}
