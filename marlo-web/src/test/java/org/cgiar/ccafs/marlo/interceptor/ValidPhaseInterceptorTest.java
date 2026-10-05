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
import org.cgiar.ccafs.marlo.data.manager.CrpUserManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.User;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
 * Covers ValidPhaseInterceptor (A2-2606). A phaseID is accepted only when the phase exists, belongs to the Global Unit
 * named in the URL (when the URL names one), and belongs to the session Global Unit or to one the user is an active
 * member of. Everything else is answered 404 before the action runs.
 * The interceptor is driven directly, with in-memory managers and invocation: nothing touches a database.
 */
public class ValidPhaseInterceptorTest {

  private static final long USER_ID = 1082L;

  private static final long AICCRA_ID = 45L;
  private static final long AICCRA_III_ID = 47L;
  private static final long OTHER_UNIT_ID = 17L;

  private static final long AICCRA_PHASE_ID = 429L;
  private static final long AICCRA_III_PHASE_ID = 444L;
  private static final long OTHER_UNIT_PHASE_ID = 100L;
  private static final long PHASE_WITHOUT_GLOBAL_UNIT_ID = 500L;
  private static final long UNKNOWN_PHASE_ID = 999999L;

  private static final String FLAT_ACTION = "autosaveWriter";

  private final Map<Long, Phase> phases = new HashMap<>();
  private final Set<Long> activeMemberships = new HashSet<>();
  private final List<Long> lookedUpPhaseIDs = new ArrayList<>();
  private final List<Long> membershipChecks = new ArrayList<>();
  private boolean invoked;

  private ValidPhaseInterceptor interceptor;

  private static GlobalUnit globalUnit(Long id, String acronym) {
    GlobalUnit globalUnit = new GlobalUnit();
    globalUnit.setId(id);
    globalUnit.setAcronym(acronym);
    return globalUnit;
  }

  private static Phase phase(long id, GlobalUnit globalUnit) {
    Phase phase = new Phase();
    phase.setId(id);
    phase.setCrp(globalUnit);
    return phase;
  }

  private String intercept(String actionName, Map<String, Object> session, Map<String, Object> parameters)
    throws Exception {
    ActionContext.of(new HashMap<String, Object>()).withActionName(actionName).withSession(session)
      .withParameters(HttpParameters.create(parameters).build()).bind();
    return this.interceptor.intercept(this.invocation());
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

  private Map<String, Object> phaseParameter(String... values) {
    Map<String, Object> parameters = new HashMap<>();
    parameters.put(APConstants.PHASE_ID, values);
    return parameters;
  }

  private void assertNotFound(String result) {
    assertEquals(BaseAction.NOT_FOUND, result);
    assertFalse("the action must not run", this.invoked);
  }

  private void assertReachesTheAction(String result) {
    assertEquals(Action.SUCCESS, result);
    assertTrue("the action must run", this.invoked);
  }

  private Map<String, Object> sessionIn(GlobalUnit globalUnit, boolean withUser) {
    Map<String, Object> session = new HashMap<>();
    session.put(APConstants.SESSION_CRP, globalUnit);
    if (withUser) {
      User user = new User();
      user.setId(USER_ID);
      session.put(APConstants.SESSION_USER, user);
    }
    return session;
  }

  @Before
  public void setUp() {
    GlobalUnit aiccra = globalUnit(AICCRA_ID, "AICCRA");
    GlobalUnit aiccraIII = globalUnit(AICCRA_III_ID, "AICCRA_III");
    GlobalUnit otherUnit = globalUnit(OTHER_UNIT_ID, "OTHER");
    this.phases.put(AICCRA_PHASE_ID, phase(AICCRA_PHASE_ID, aiccra));
    this.phases.put(AICCRA_III_PHASE_ID, phase(AICCRA_III_PHASE_ID, aiccraIII));
    this.phases.put(OTHER_UNIT_PHASE_ID, phase(OTHER_UNIT_PHASE_ID, otherUnit));
    this.phases.put(PHASE_WITHOUT_GLOBAL_UNIT_ID, phase(PHASE_WITHOUT_GLOBAL_UNIT_ID, null));
    // The user is an active member of AICCRA and AICCRA III, never of OTHER.
    this.activeMemberships.add(AICCRA_ID);
    this.activeMemberships.add(AICCRA_III_ID);

    PhaseManager phaseManager = (PhaseManager) Proxy.newProxyInstance(this.getClass().getClassLoader(),
      new Class<?>[] {PhaseManager.class}, (proxy, method, args) -> {
        if ("getPhaseById".equals(method.getName())) {
          Long phaseID = (Long) args[0];
          this.lookedUpPhaseIDs.add(phaseID);
          return this.phases.get(phaseID);
        }
        throw new AssertionError("ValidPhaseInterceptor must only look phases up by id, not call " + method.getName());
      });
    CrpUserManager crpUserManager = (CrpUserManager) Proxy.newProxyInstance(this.getClass().getClassLoader(),
      new Class<?>[] {CrpUserManager.class}, (proxy, method, args) -> {
        if ("existActiveCrpUser".equals(method.getName())) {
          assertEquals("membership is checked for the session user", USER_ID, ((Long) args[0]).longValue());
          Long crpID = (Long) args[1];
          this.membershipChecks.add(crpID);
          return this.activeMemberships.contains(crpID);
        }
        throw new AssertionError("ValidPhaseInterceptor must only check active memberships, not call "
          + method.getName());
      });
    this.interceptor = new ValidPhaseInterceptor(phaseManager, crpUserManager);
  }

  @After
  public void tearDown() {
    ActionContext.clear();
  }

  @Test
  public void aMixedUrlIsAnsweredNotFoundEvenForAMember() throws Exception {
    // An AICCRA URL carrying an AICCRA III phase: never accepted, the action would work on the wrong unit's phase.
    this.assertNotFound(this.intercept("AICCRA/description", this.sessionIn(globalUnit(AICCRA_ID, "AICCRA"), true),
      this.phaseParameter(String.valueOf(AICCRA_III_PHASE_ID))));
  }

  @Test
  public void anOldTabOfAnotherUnitTheUserBelongsToKeepsWorkingOnFlatUrls() throws Exception {
    // The session moved to AICCRA in another tab; this tab's autosave still sends its AICCRA III phase.
    this.assertReachesTheAction(this.intercept(FLAT_ACTION, this.sessionIn(globalUnit(AICCRA_ID, "AICCRA"), true),
      this.phaseParameter(String.valueOf(AICCRA_III_PHASE_ID))));
  }

  @Test
  public void anOldTabOfAnotherUnitTheUserBelongsToKeepsWorkingOnItsOwnUrls() throws Exception {
    // A {crp}/ action without the Global Unit switch, from a tab still on AICCRA III: URL and phase agree.
    this.assertReachesTheAction(this.intercept("AICCRA_III/submit",
      this.sessionIn(globalUnit(AICCRA_ID, "AICCRA"), true), this.phaseParameter(String.valueOf(AICCRA_III_PHASE_ID))));
  }

  @Test
  public void aPhaseOfAUnitTheUserDoesNotBelongToIsAnsweredNotFoundOnFlatUrls() throws Exception {
    this.assertNotFound(this.intercept(FLAT_ACTION, this.sessionIn(globalUnit(AICCRA_ID, "AICCRA"), true),
      this.phaseParameter(String.valueOf(OTHER_UNIT_PHASE_ID))));
  }

  @Test
  public void aPhaseOfAUnitTheUserDoesNotBelongToIsAnsweredNotFoundOnItsOwnUrls() throws Exception {
    // The URL and the phase agree, but the user is not a member of that unit.
    this.assertNotFound(this.intercept("OTHER/submit", this.sessionIn(globalUnit(AICCRA_ID, "AICCRA"), true),
      this.phaseParameter(String.valueOf(OTHER_UNIT_PHASE_ID))));
  }

  @Test
  public void aForeignPhaseWithoutASessionUserIsAnsweredNotFound() throws Exception {
    this.assertNotFound(this.intercept(FLAT_ACTION, this.sessionIn(globalUnit(AICCRA_ID, "AICCRA"), false),
      this.phaseParameter(String.valueOf(AICCRA_III_PHASE_ID))));
  }

  @Test
  public void anUnknownPhaseIsAnsweredNotFound() throws Exception {
    this.assertNotFound(this.intercept(FLAT_ACTION, this.sessionIn(globalUnit(AICCRA_ID, "AICCRA"), true),
      this.phaseParameter(String.valueOf(UNKNOWN_PHASE_ID))));
  }

  @Test
  public void aPhaseWithoutGlobalUnitIsAnsweredNotFound() throws Exception {
    this.assertNotFound(this.intercept(FLAT_ACTION, this.sessionIn(globalUnit(AICCRA_ID, "AICCRA"), true),
      this.phaseParameter(String.valueOf(PHASE_WITHOUT_GLOBAL_UNIT_ID))));
  }

  @Test
  public void anOwnPhaseReachesTheActionWithoutAMembershipCheck() throws Exception {
    this.assertReachesTheAction(this.intercept("AICCRA/description",
      this.sessionIn(globalUnit(AICCRA_ID, "AICCRA"), true), this.phaseParameter(String.valueOf(AICCRA_PHASE_ID))));
    assertTrue("a phase of the session unit needs no membership query", this.membershipChecks.isEmpty());
  }

  @Test
  public void theUrlGlobalUnitIsComparedIgnoringCase() throws Exception {
    this.assertReachesTheAction(this.intercept("aiccra/description",
      this.sessionIn(globalUnit(AICCRA_ID, "AICCRA"), true), this.phaseParameter(String.valueOf(AICCRA_PHASE_ID))));
  }

  @Test
  public void oneRejectedValueAmongSeveralIsEnoughToAnswerNotFound() throws Exception {
    this.assertNotFound(this.intercept("AICCRA/description", this.sessionIn(globalUnit(AICCRA_ID, "AICCRA"), true),
      this.phaseParameter(String.valueOf(AICCRA_PHASE_ID), String.valueOf(AICCRA_III_PHASE_ID))));
  }

  @Test
  public void anEmptyOrNonNumericPhaseIsLeftToTheActionWithoutALookup() throws Exception {
    this.assertReachesTheAction(this.intercept("AICCRA/description",
      this.sessionIn(globalUnit(AICCRA_ID, "AICCRA"), true), this.phaseParameter("", " ", "abc", "0")));
    assertTrue(this.lookedUpPhaseIDs.isEmpty());
  }

  @Test
  public void aRequestWithoutPhaseReachesTheActionWithoutALookup() throws Exception {
    this.assertReachesTheAction(this.intercept("AICCRA/description",
      this.sessionIn(globalUnit(AICCRA_ID, "AICCRA"), true), new HashMap<String, Object>()));
    assertTrue(this.lookedUpPhaseIDs.isEmpty());
  }

  @Test
  public void anAnonymousSessionIsNeverChecked() throws Exception {
    // An unlogged visitor has no Global Unit in the session: public pages must keep working with any phaseID.
    this.assertReachesTheAction(this.intercept("AICCRA/studySummary", new HashMap<String, Object>(),
      this.phaseParameter(String.valueOf(OTHER_UNIT_PHASE_ID))));
    assertTrue(this.lookedUpPhaseIDs.isEmpty());
  }

  @Test
  public void aSessionGlobalUnitWithoutIdIsNeverChecked() throws Exception {
    this.assertReachesTheAction(this.intercept(FLAT_ACTION, this.sessionIn(globalUnit(null, "AICCRA"), true),
      this.phaseParameter(String.valueOf(OTHER_UNIT_PHASE_ID))));
    assertTrue(this.lookedUpPhaseIDs.isEmpty());
  }

}
