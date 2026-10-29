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

package org.cgiar.ccafs.marlo.action;

import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.model.Phase;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Characterization of the phase type methods of {@link BaseAction} as they answer today. Every expected value below was
 * observed by reading the old method bodies and running them, not derived from the intended behavior: the test pins
 * the results that the unified phase type has to reproduce (and the throws it is allowed to replace).
 * <p>
 * Each row resolves the phase through the real {@code getActualPhase()}: the session carries the phases of the unit,
 * the action carries the phase id, and nothing is overridden.
 */
public class BaseActionPhaseTypeCharacterizationTest {

  private static final Long PHASE_ID = Long.valueOf(7L);

  /** The answers of the phase type methods, of the planning cycle and of the current cycle for one input. */
  private static final class Answers {

    private final boolean planningPhase;

    private final boolean planningCycle;

    private final boolean progressPhase;

    private final boolean reportingPhase;

    private final String currentCycle;

    private Answers(boolean planningPhase, boolean planningCycle, boolean progressPhase, boolean reportingPhase,
      String currentCycle) {
      this.planningPhase = planningPhase;
      this.planningCycle = planningCycle;
      this.progressPhase = progressPhase;
      this.reportingPhase = reportingPhase;
      this.currentCycle = currentCycle;
    }
  }

  private static final Answers PLAN_NAME_NO_UPKEEP =
    new Answers(true, true, false, false, "Planning");

  private static final Answers UPKEEP_FLAGGED = new Answers(false, true, true, false, "Planning");

  private static final Answers REPORTING = new Answers(false, false, false, true, "Reporting");

  /** The empty phase and every input that reads as "no usable phase" for the type methods. */
  private static final Answers NO_PHASE = new Answers(false, false, false, false, "Planning");

  private static Phase phase(String name, String description, Boolean upkeep) {
    Phase phase = new Phase(null, description, 2026);
    phase.setName(name);
    phase.setUpkeep(upkeep);
    return phase;
  }

  /** An action whose real getActualPhase() finds the given phase in the session by its id. */
  private static BaseAction actionOn(Phase actualPhase) {
    BaseAction action = new BaseAction() {

      private static final long serialVersionUID = 1L;
    };
    Map<Long, Phase> allPhases = new HashMap<>();
    allPhases.put(PHASE_ID, actualPhase);
    Map<String, Object> session = new HashMap<>();
    session.put(APConstants.ALL_PHASES, allPhases);
    action.setSession(session);
    action.setPhaseID(PHASE_ID);
    return action;
  }

  private static void assertAnswers(String input, BaseAction action, Answers expected) {
    assertEquals(input + " isPlanningPhase", expected.planningPhase, action.isPlanningPhase());
    assertEquals(input + " planning cycle", expected.planningCycle,
      action.isPlanningPhase() || action.isProgressPhase());
    assertEquals(input + " isProgressPhase", expected.progressPhase, action.isProgressPhase());
    assertEquals(input + " isReportingPhase", expected.reportingPhase, action.isReportingPhase());
    assertEquals(input + " getCurrentCycle", expected.currentCycle, action.getCurrentCycle());
  }

  private static void inject(Object target, String fieldName, Object value) throws ReflectiveOperationException {
    Field field = BaseAction.class.getDeclaredField(fieldName);
    field.setAccessible(true);
    field.set(target, value);
  }

  /** An action whose actual phase resolves to null: the session has no such phase and the manager finds none. */
  private static BaseAction actionWithNullActualPhase() throws ReflectiveOperationException {
    BaseAction action = actionOn(null);
    PhaseManager phaseManager = (PhaseManager) Proxy.newProxyInstance(PhaseManager.class.getClassLoader(),
      new Class<?>[] {PhaseManager.class}, (proxy, method, args) -> {
        if ("getPhaseById".equals(method.getName())) {
          assertEquals("the phase id handed to the manager", PHASE_ID, args[0]);
          return null;
        }
        throw new UnsupportedOperationException(method.getName());
      });
    inject(action, "phaseManager", phaseManager);
    return action;
  }

  @Test
  public void powbPlanningWithoutUpkeepIsAPlanningPhaseOnly() {
    assertAnswers("POWB/Planning/0", actionOn(phase("POWB", "Planning", Boolean.FALSE)), PLAN_NAME_NO_UPKEEP);
  }

  @Test
  public void awpbPlanningWithoutUpkeepIsAPlanningPhaseOnly() {
    assertAnswers("AWPB/Planning/0", actionOn(phase("AWPB", "Planning", Boolean.FALSE)), PLAN_NAME_NO_UPKEEP);
  }

  @Test
  public void upKeepPlanningWithUpkeepIsAProgressPhase() {
    assertAnswers("UpKeep/Planning/1", actionOn(phase("UpKeep", "Planning", Boolean.TRUE)), UPKEEP_FLAGGED);
  }

  @Test
  public void progressPlanningWithUpkeepIsAProgressPhase() {
    assertAnswers("Progress/Planning/1", actionOn(phase("Progress", "Planning", Boolean.TRUE)), UPKEEP_FLAGGED);
  }

  @Test
  public void annualReportReportingWithoutUpkeepIsAReportingPhase() {
    assertAnswers("AR/Reporting/0", actionOn(phase("AR", "Reporting", Boolean.FALSE)), REPORTING);
  }

  @Test
  public void aMisspelledPlaningDescriptionIsNotAPlanningPhaseButStillAPlanningCycle() {
    assertAnswers("POWB/Planing/0", actionOn(phase("POWB", "Planing", Boolean.FALSE)),
      new Answers(false, false, false, false, "Planning"));
  }

  @Test
  public void theEmptyPhaseIsNoType() {
    assertAnswers("empty phase", actionOn(new Phase(null, "", -1)), NO_PHASE);
  }

  @Test
  public void aNullActualPhaseAnswersNoType() throws ReflectiveOperationException {
    BaseAction action = actionWithNullActualPhase();
    assertNull("the stubbed lookup leaves the actual phase null", action.getActualPhase());
    assertAnswers("null actual phase", action, NO_PHASE);
  }

  @Test
  public void anActionWithoutSessionResolvesTheEmptyPhaseAndAnswersNoType() {
    BaseAction action = new BaseAction() {

      private static final long serialVersionUID = 1L;
    };
    assertNull("there is no session", action.getSession());
    assertEquals("no phase id", -1, action.getActualPhase().getYear());
    assertNull("no phase name", action.getActualPhase().getName());
    assertAnswers("no session", action, NO_PHASE);
  }

  @Test
  public void anActionWithAnEmptySessionResolvesTheEmptyPhaseAndAnswersNoType() {
    BaseAction action = new BaseAction() {

      private static final long serialVersionUID = 1L;
    };
    action.setSession(new HashMap<>());
    assertEquals("no phase id", -1, action.getActualPhase().getYear());
    assertAnswers("empty session", action, NO_PHASE);
  }

  @Test
  public void aNullUpkeepIsNotProgressAndItsUpkeepQuestionAnswersFalse() {
    assertAnswers("UpKeep/Planning/null", actionOn(phase("UpKeep", "Planning", null)),
      new Answers(false, false, false, false, "Planning"));
  }

  @Test
  public void aNullDescriptionIsNeitherPlanningNorReportingButKeepsItsNameAndUpkeep() {
    assertAnswers("POWB/null/0", actionOn(phase("POWB", null, Boolean.FALSE)),
      new Answers(false, false, false, false, "Planning"));
    assertAnswers("UpKeep/null/1", actionOn(phase("UpKeep", null, Boolean.TRUE)),
      new Answers(false, true, true, false, "Planning"));
  }

  @Test
  public void aNullNameIsNoPlanNameButKeepsItsDescriptionAndUpkeep() {
    assertAnswers("null/Planning/0", actionOn(phase(null, "Planning", Boolean.FALSE)),
      new Answers(true, true, false, false, "Planning"));
    assertAnswers("null/Reporting/0", actionOn(phase(null, "Reporting", Boolean.FALSE)), REPORTING);
  }

  private static void assertTypes(String input, BaseAction action, boolean plan, boolean progress, boolean reporting) {
    assertEquals(input + " isPlanningPhase", plan, action.isPlanningPhase());
    assertEquals(input + " isProgressPhase", progress, action.isProgressPhase());
    assertEquals(input + " isReportingPhase", reporting, action.isReportingPhase());
  }

  @Test
  public void theSameNamedQuestionsAnswerThePhaseTypeOfEachRow() {
    assertTypes("POWB/Planning/0", actionOn(phase("POWB", "Planning", Boolean.FALSE)), true, false, false);
    assertTypes("AWPB/Planning/0", actionOn(phase("AWPB", "Planning", Boolean.FALSE)), true, false, false);
    assertTypes("UpKeep/Planning/1", actionOn(phase("UpKeep", "Planning", Boolean.TRUE)), false, true, false);
    assertTypes("Progress/Planning/1", actionOn(phase("Progress", "Planning", Boolean.TRUE)), false, true, false);
    assertTypes("AR/Reporting/0", actionOn(phase("AR", "Reporting", Boolean.FALSE)), false, false, true);
    assertTypes("POWB/Planing/0", actionOn(phase("POWB", "Planing", Boolean.FALSE)), false, false, false);
  }

  @Test
  public void theSameNamedQuestionsReadTheTypeFieldsAndNeverTheName() {
    assertTypes("null/Planning/0", actionOn(phase(null, "Planning", Boolean.FALSE)), true, false, false);
    assertTypes("POWB/null/0", actionOn(phase("POWB", null, Boolean.FALSE)), false, false, false);
    assertTypes("UpKeep/null/1", actionOn(phase("UpKeep", null, Boolean.TRUE)), false, true, false);
    assertTypes("UpKeep/Planning/null", actionOn(phase("UpKeep", "Planning", null)), false, false, false);
    assertTypes("AR/Reporting/null", actionOn(phase("AR", "Reporting", null)), false, false, true);
  }

  @Test
  public void theSameNamedQuestionsAnswerFalseWithoutAUsablePhase() throws ReflectiveOperationException {
    assertTypes("empty phase", actionOn(new Phase(null, "", -1)), false, false, false);
    assertTypes("null actual phase", actionWithNullActualPhase(), false, false, false);
    assertTypes("no session", new BaseAction() {

      private static final long serialVersionUID = 1L;
    }, false, false, false);
  }

  @Test
  public void thePhaseReportingQuestionComparesTheDescriptionAndAnswersFalseOnANullOne() {
    assertEquals(true, phase("AR", "Reporting", Boolean.FALSE).isReporting().booleanValue());
    assertEquals(false, phase("POWB", "Planning", Boolean.FALSE).isReporting().booleanValue());
    assertEquals(false, new Phase(null, "", -1).isReporting().booleanValue());
    assertEquals("Phase.isReporting() with a null description (INT-3: it threw before)", false,
      phase("AR", null, Boolean.FALSE).isReporting().booleanValue());
  }
}
