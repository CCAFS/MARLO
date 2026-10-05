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
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.Phase;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import org.apache.struts2.dispatcher.HttpParameters;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Covers ValidPhaseInterceptor (A2-2606): a request whose phaseID does not belong to the session Global Unit must be
 * answered 404 before the action runs, and every other request must reach the action untouched.
 * The interceptor is driven directly, with an in-memory PhaseManager and ActionInvocation: nothing touches a database.
 */
public class ValidPhaseInterceptorTest {

  private static final long AICCRA_ID = 45L;
  private static final long AICCRA_III_ID = 47L;

  private static final long AICCRA_PHASE_ID = 429L;
  private static final long AICCRA_III_PHASE_ID = 444L;
  private static final long PHASE_WITHOUT_GLOBAL_UNIT_ID = 500L;
  private static final long UNKNOWN_PHASE_ID = 999999L;

  private final Map<Long, Phase> phases = new HashMap<>();
  private final List<Long> lookedUpPhaseIDs = new ArrayList<>();
  private boolean invoked;

  private ValidPhaseInterceptor interceptor;

  private static GlobalUnit globalUnit(Long id) {
    GlobalUnit globalUnit = new GlobalUnit();
    globalUnit.setId(id);
    return globalUnit;
  }

  private static Phase phase(long id, GlobalUnit globalUnit) {
    Phase phase = new Phase();
    phase.setId(id);
    phase.setCrp(globalUnit);
    return phase;
  }

  private ActionInvocation invocation() {
    return (ActionInvocation) Proxy.newProxyInstance(this.getClass().getClassLoader(),
      new Class<?>[] {ActionInvocation.class}, (proxy, method, args) -> {
        if ("getInvocationContext".equals(method.getName())) {
          return ActionContext.getContext();
        }
        if ("invoke".equals(method.getName())) {
          this.invoked = true;
          return Action.SUCCESS;
        }
        return null;
      });
  }

  private String intercept(Map<String, Object> session, Map<String, Object> parameters) throws Exception {
    ActionContext.of(new HashMap<String, Object>()).withSession(session)
      .withParameters(HttpParameters.create(parameters).build()).bind();
    return this.interceptor.intercept(this.invocation());
  }

  private Map<String, Object> phaseParameter(String... values) {
    Map<String, Object> parameters = new HashMap<>();
    parameters.put(APConstants.PHASE_ID, values);
    return parameters;
  }

  private Map<String, Object> sessionIn(GlobalUnit globalUnit) {
    Map<String, Object> session = new HashMap<>();
    session.put(APConstants.SESSION_CRP, globalUnit);
    return session;
  }

  @Before
  public void setUp() {
    GlobalUnit aiccra = globalUnit(AICCRA_ID);
    GlobalUnit aiccraIII = globalUnit(AICCRA_III_ID);
    this.phases.put(AICCRA_PHASE_ID, phase(AICCRA_PHASE_ID, aiccra));
    this.phases.put(AICCRA_III_PHASE_ID, phase(AICCRA_III_PHASE_ID, aiccraIII));
    this.phases.put(PHASE_WITHOUT_GLOBAL_UNIT_ID, phase(PHASE_WITHOUT_GLOBAL_UNIT_ID, null));

    PhaseManager phaseManager = (PhaseManager) Proxy.newProxyInstance(this.getClass().getClassLoader(),
      new Class<?>[] {PhaseManager.class}, (proxy, method, args) -> {
        if ("getPhaseById".equals(method.getName())) {
          Long phaseID = (Long) args[0];
          this.lookedUpPhaseIDs.add(phaseID);
          return this.phases.get(phaseID);
        }
        throw new AssertionError("ValidPhaseInterceptor must only look phases up by id, not call " + method.getName());
      });
    this.interceptor = new ValidPhaseInterceptor(phaseManager);
  }

  @After
  public void tearDown() {
    ActionContext.clear();
  }

  @Test
  public void aForeignPhaseIsAnsweredNotFoundAndTheActionDoesNotRun() throws Exception {
    String result = this.intercept(this.sessionIn(globalUnit(AICCRA_ID)),
      this.phaseParameter(String.valueOf(AICCRA_III_PHASE_ID)));

    assertEquals(BaseAction.NOT_FOUND, result);
    assertFalse("the action must not run with another Global Unit's phase", this.invoked);
  }

  @Test
  public void anUnknownPhaseIsAnsweredNotFound() throws Exception {
    String result =
      this.intercept(this.sessionIn(globalUnit(AICCRA_ID)), this.phaseParameter(String.valueOf(UNKNOWN_PHASE_ID)));

    assertEquals(BaseAction.NOT_FOUND, result);
    assertFalse(this.invoked);
  }

  @Test
  public void aPhaseWithoutGlobalUnitIsAnsweredNotFound() throws Exception {
    String result = this.intercept(this.sessionIn(globalUnit(AICCRA_ID)),
      this.phaseParameter(String.valueOf(PHASE_WITHOUT_GLOBAL_UNIT_ID)));

    assertEquals(BaseAction.NOT_FOUND, result);
    assertFalse(this.invoked);
  }

  @Test
  public void anOwnPhaseReachesTheAction() throws Exception {
    String result =
      this.intercept(this.sessionIn(globalUnit(AICCRA_ID)), this.phaseParameter(String.valueOf(AICCRA_PHASE_ID)));

    assertEquals(Action.SUCCESS, result);
    assertTrue(this.invoked);
  }

  @Test
  public void aPhaseIsComparedWithTheGlobalUnitAlreadyInTheSession() throws Exception {
    // After ValidSessionCrpInterceptor switched to AICCRA III, the AICCRA III phase is the session's own.
    String result = this.intercept(this.sessionIn(globalUnit(AICCRA_III_ID)),
      this.phaseParameter(String.valueOf(AICCRA_III_PHASE_ID)));

    assertEquals(Action.SUCCESS, result);
    assertTrue(this.invoked);
  }

  @Test
  public void oneForeignValueAmongSeveralIsEnoughToAnswerNotFound() throws Exception {
    String result = this.intercept(this.sessionIn(globalUnit(AICCRA_ID)),
      this.phaseParameter(String.valueOf(AICCRA_PHASE_ID), String.valueOf(AICCRA_III_PHASE_ID)));

    assertEquals(BaseAction.NOT_FOUND, result);
    assertFalse(this.invoked);
  }

  @Test
  public void repeatedOwnValuesReachTheAction() throws Exception {
    String result = this.intercept(this.sessionIn(globalUnit(AICCRA_ID)),
      this.phaseParameter(String.valueOf(AICCRA_PHASE_ID), String.valueOf(AICCRA_PHASE_ID)));

    assertEquals(Action.SUCCESS, result);
    assertTrue(this.invoked);
  }

  @Test
  public void anEmptyOrNonNumericPhaseIsLeftToTheActionWithoutALookup() throws Exception {
    String result = this.intercept(this.sessionIn(globalUnit(AICCRA_ID)), this.phaseParameter("", " ", "abc", "0"));

    assertEquals(Action.SUCCESS, result);
    assertTrue(this.invoked);
    assertTrue("nothing to look up for values that are not a phase id", this.lookedUpPhaseIDs.isEmpty());
  }

  @Test
  public void aRequestWithoutPhaseReachesTheActionWithoutALookup() throws Exception {
    String result = this.intercept(this.sessionIn(globalUnit(AICCRA_ID)), new HashMap<String, Object>());

    assertEquals(Action.SUCCESS, result);
    assertTrue(this.invoked);
    assertTrue(this.lookedUpPhaseIDs.isEmpty());
  }

  @Test
  public void anAnonymousSessionIsNeverChecked() throws Exception {
    // An unlogged visitor has no Global Unit in the session: public pages must keep working with any phaseID.
    String result =
      this.intercept(new HashMap<String, Object>(), this.phaseParameter(String.valueOf(AICCRA_III_PHASE_ID)));

    assertEquals(Action.SUCCESS, result);
    assertTrue(this.invoked);
    assertTrue(this.lookedUpPhaseIDs.isEmpty());
  }

  @Test
  public void aSessionGlobalUnitWithoutIdIsNeverChecked() throws Exception {
    String result =
      this.intercept(this.sessionIn(globalUnit(null)), this.phaseParameter(String.valueOf(AICCRA_III_PHASE_ID)));

    assertEquals(Action.SUCCESS, result);
    assertTrue(this.invoked);
    assertTrue(this.lookedUpPhaseIDs.isEmpty());
  }

}
