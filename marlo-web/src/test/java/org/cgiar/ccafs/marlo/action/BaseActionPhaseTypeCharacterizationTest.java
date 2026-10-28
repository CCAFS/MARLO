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
import static org.junit.Assert.assertThrows;

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

  /** The answers of the six old type methods and of the cycle for one input. {@code null} upkeep means it throws. */
  private static final class Answers {

    private final boolean powb;

    private final boolean awpbActive;

    private final boolean planningActive;

    private final boolean progressActive;

    private final Boolean upKeepActive;

    private final boolean reportingActive;

    private final String currentCycle;

    private Answers(boolean powb, boolean awpbActive, boolean planningActive, boolean progressActive,
      Boolean upKeepActive, boolean reportingActive, String currentCycle) {
      this.powb = powb;
      this.awpbActive = awpbActive;
      this.planningActive = planningActive;
      this.progressActive = progressActive;
      this.upKeepActive = upKeepActive;
      this.reportingActive = reportingActive;
      this.currentCycle = currentCycle;
    }
  }

  private static final Answers PLAN_NAME_NO_UPKEEP =
    new Answers(true, true, true, false, Boolean.FALSE, false, "Planning");

  private static final Answers UPKEEP_FLAGGED = new Answers(false, false, true, true, Boolean.TRUE, false, "Planning");

  private static final Answers REPORTING = new Answers(false, false, false, false, Boolean.FALSE, true, "Reporting");

  /** The empty phase and every input that reads as "no usable phase" for the type methods. */
  private static final Answers NO_PHASE = new Answers(false, false, false, false, null, false, "Planning");

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
    assertEquals(input + " isPOWB", expected.powb, action.isPOWB());
    assertEquals(input + " isAWPBActive", expected.awpbActive, action.isAWPBActive());
    assertEquals(input + " isPlanningActive", expected.planningActive, action.isPlanningActive());
    assertEquals(input + " isProgressActive", expected.progressActive, action.isProgressActive());
    if (expected.upKeepActive == null) {
      assertThrows(input + " isUpKeepActive", NullPointerException.class, action::isUpKeepActive);
    } else {
      assertEquals(input + " isUpKeepActive", expected.upKeepActive.booleanValue(), action.isUpKeepActive());
    }
    assertEquals(input + " isReportingActive", expected.reportingActive, action.isReportingActive());
    assertEquals(input + " getCurrentCycle", expected.currentCycle, action.getCurrentCycle());
  }

  private static void inject(Object target, String fieldName, Object value) throws ReflectiveOperationException {
    Field field = BaseAction.class.getDeclaredField(fieldName);
    field.setAccessible(true);
    field.set(target, value);
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
      new Answers(true, true, false, false, Boolean.FALSE, false, "Planning"));
  }

  @Test
  public void theEmptyPhaseIsNoTypeAndItsUpkeepQuestionThrows() {
    assertAnswers("empty phase", actionOn(new Phase(null, "", -1)), NO_PHASE);
  }

  @Test
  public void aNullActualPhaseAnswersNoTypeAndItsUpkeepQuestionThrows() throws ReflectiveOperationException {
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
  public void aNullUpkeepIsNotProgressAndItsUpkeepQuestionThrows() {
    assertAnswers("UpKeep/Planning/null", actionOn(phase("UpKeep", "Planning", null)),
      new Answers(false, false, true, false, null, false, "Planning"));
  }

  @Test
  public void aNullDescriptionIsNeitherPlanningNorReportingButKeepsItsNameAndUpkeep() {
    assertAnswers("POWB/null/0", actionOn(phase("POWB", null, Boolean.FALSE)),
      new Answers(true, true, false, false, Boolean.FALSE, false, "Planning"));
    assertAnswers("UpKeep/null/1", actionOn(phase("UpKeep", null, Boolean.TRUE)),
      new Answers(false, false, false, true, Boolean.TRUE, false, "Planning"));
  }

  @Test
  public void aNullNameIsNoPlanNameButKeepsItsDescriptionAndUpkeep() {
    assertAnswers("null/Planning/0", actionOn(phase(null, "Planning", Boolean.FALSE)),
      new Answers(false, false, true, false, Boolean.FALSE, false, "Planning"));
    assertAnswers("null/Reporting/0", actionOn(phase(null, "Reporting", Boolean.FALSE)), REPORTING);
  }

  @Test
  public void thePhaseReportingQuestionComparesTheDescriptionAndThrowsOnANullOne() {
    assertEquals(true, phase("AR", "Reporting", Boolean.FALSE).isReporting().booleanValue());
    assertEquals(false, phase("POWB", "Planning", Boolean.FALSE).isReporting().booleanValue());
    assertEquals(false, new Phase(null, "", -1).isReporting().booleanValue());
    assertThrows("Phase.isReporting() with a null description", NullPointerException.class,
      () -> phase("AR", null, Boolean.FALSE).isReporting());
  }
}
