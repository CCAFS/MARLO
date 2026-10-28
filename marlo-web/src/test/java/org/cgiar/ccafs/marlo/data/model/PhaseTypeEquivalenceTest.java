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

package org.cgiar.ccafs.marlo.data.model;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * The phase type answered by {@link Phase} against the old type methods of {@link BaseAction}.
 * <p>
 * The old answers come from the real {@code BaseAction} methods, resolved through the real {@code getActualPhase()}.
 * The new answers come from {@link Phase} and are also pinned as literals taken from the strict definitions (design
 * DD-3), so an equivalence that holds only because both sides share a bug cannot pass.
 */
public class PhaseTypeEquivalenceTest {

  private static final Long PHASE_ID = Long.valueOf(7L);

  /** The expected new answers for one phase: plan, progress, reporting and, derived, the planning cycle. */
  private static final class Expected {

    private final boolean plan;

    private final boolean progress;

    private final boolean reporting;

    private final boolean cycle;

    private Expected(boolean plan, boolean progress, boolean reporting, boolean cycle) {
      this.plan = plan;
      this.progress = progress;
      this.reporting = reporting;
      this.cycle = cycle;
    }
  }

  private static final Expected PLAN = new Expected(true, false, false, true);

  private static final Expected PROGRESS = new Expected(false, true, false, true);

  private static final Expected REPORTING = new Expected(false, false, true, false);

  private static final Expected NO_TYPE = new Expected(false, false, false, false);

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

  /** The planning cycle is the plan phase or the progress phase: it has no method of its own. */
  private static boolean cycle(Phase phase) {
    return phase.isPlanningPhase() || phase.isProgressPhase();
  }

  private static void assertNewAnswers(String input, Phase phase, Expected expected) {
    assertEquals(input + " isPlanningPhase", expected.plan, phase.isPlanningPhase());
    assertEquals(input + " isProgressPhase", expected.progress, phase.isProgressPhase());
    assertEquals(input + " isReportingPhase", expected.reporting, phase.isReportingPhase());
    assertEquals(input + " planning cycle", expected.cycle, cycle(phase));
  }

  /** Old and new on the same phase (FN-002), then the new answers against the literals. */
  private static void assertEquivalent(String input, Phase phase, Expected expected) {
    BaseAction old = actionOn(phase);
    assertEquals(input + " plan phase vs isPOWB", old.isPOWB(), phase.isPlanningPhase());
    assertEquals(input + " progress phase vs isProgressActive", old.isProgressActive(), phase.isProgressPhase());
    assertEquals(input + " reporting phase vs isReportingActive", old.isReportingActive(), phase.isReportingPhase());
    assertEquals(input + " cycle vs isPlanningActive", old.isPlanningActive(), cycle(phase));
    assertNewAnswers(input, phase, expected);
  }

  @Test
  public void powbPlanningWithoutUpkeepIsThePlanPhase() {
    assertEquivalent("POWB/Planning/0", phase("POWB", "Planning", Boolean.FALSE), PLAN);
  }

  @Test
  public void awpbPlanningWithoutUpkeepIsThePlanPhase() {
    assertEquivalent("AWPB/Planning/0", phase("AWPB", "Planning", Boolean.FALSE), PLAN);
  }

  @Test
  public void upKeepPlanningWithUpkeepIsTheProgressPhaseAndNotThePlanPhase() {
    assertEquivalent("UpKeep/Planning/1", phase("UpKeep", "Planning", Boolean.TRUE), PROGRESS);
  }

  @Test
  public void progressPlanningWithUpkeepIsTheProgressPhaseAndNotThePlanPhase() {
    assertEquivalent("Progress/Planning/1", phase("Progress", "Planning", Boolean.TRUE), PROGRESS);
  }

  @Test
  public void annualReportReportingIsTheReportingPhaseAndNotAPlanningCycle() {
    assertEquivalent("AR/Reporting/0", phase("AR", "Reporting", Boolean.FALSE), REPORTING);
  }

  @Test
  public void theCycleIsTrueForThePlanAndTheProgressPhaseAndFalseForReporting() {
    assertEquals(true, cycle(phase("POWB", "Planning", Boolean.FALSE)));
    assertEquals(true, cycle(phase("UpKeep", "Planning", Boolean.TRUE)));
    assertEquals(false, cycle(phase("AR", "Reporting", Boolean.FALSE)));
  }

  @Test
  public void theEmptyPhaseIsNoTypeAndThePlanningCycleIsFalse() {
    assertEquivalent("empty phase", new Phase(null, "", -1), NO_TYPE);
  }

  @Test
  public void aPhaseWithEveryFieldNullIsNoTypeAndDoesNotThrow() {
    assertNewAnswers("all fields null", new Phase(), NO_TYPE);
  }

  @Test
  public void aNullUpkeepIsNeitherThePlanNorTheProgressPhaseAndDoesNotThrow() {
    assertNewAnswers("UpKeep/Planning/null", phase("UpKeep", "Planning", null), NO_TYPE);
    assertNewAnswers("POWB/Planning/null", phase("POWB", "Planning", null), NO_TYPE);
  }

  @Test
  public void aNullDescriptionIsNeitherThePlanNorTheReportingPhaseAndDoesNotThrow() {
    assertNewAnswers("POWB/null/0", phase("POWB", null, Boolean.FALSE), NO_TYPE);
    assertNewAnswers("POWB/null/null", phase("POWB", null, null), NO_TYPE);
    assertNewAnswers("null/null/null", phase(null, null, null), NO_TYPE);
  }

  /**
   * DD-3: the progress phase is decided by the upkeep flag alone, so a null description with upkeep true is a progress
   * phase and a planning cycle. Requirements AC for FN-003/FN-004 reads "each MUST return false" for a phase with
   * {@code description = null}; this row is where the two differ and it is pinned at the DD-3 value.
   */
  @Test
  public void aNullDescriptionWithUpkeepIsTheProgressPhaseByDd3() {
    assertNewAnswers("UpKeep/null/1", phase("UpKeep", null, Boolean.TRUE), PROGRESS);
  }

  /**
   * DD-3: the reporting phase is decided by the description alone, so a Reporting description with a null upkeep is
   * the reporting phase. Requirements AC for FN-003/FN-004 reads "each MUST return false" for {@code upkeep = null}.
   */
  @Test
  public void aReportingDescriptionWithANullUpkeepIsTheReportingPhaseByDd3() {
    assertNewAnswers("AR/Reporting/null", phase("AR", "Reporting", null), REPORTING);
  }

  /**
   * DD-3 never reads the name. FN-002 lists no name, yet today's plan phase answer needs the name POWB or AWPB, so a
   * Planning/0 phase with another or a null name is a plan phase by DD-3 and not by {@code isPOWB()}.
   */
  @Test
  public void theNameIsNeverReadSoANullNameKeepsTheTypeOfItsDescriptionAndUpkeep() {
    assertNewAnswers("null/Planning/0", phase(null, "Planning", Boolean.FALSE), PLAN);
    assertNewAnswers("null/Reporting/0", phase(null, "Reporting", Boolean.FALSE), REPORTING);
    assertNewAnswers("null/Planning/1", phase(null, "Planning", Boolean.TRUE), PROGRESS);
    assertNewAnswers("Other/Planning/0", phase("Other", "Planning", Boolean.FALSE), PLAN);
  }

  @Test
  public void aMisspelledPlaningDescriptionIsNoLongerThePlanPhase() {
    assertNewAnswers("POWB/Planing/0", phase("POWB", "Planing", Boolean.FALSE), NO_TYPE);
  }

  @Test
  public void thePhaseReportingQuestionDelegatesAndNoLongerThrowsOnANullDescription() {
    assertEquals(true, phase("AR", "Reporting", Boolean.FALSE).isReporting().booleanValue());
    assertEquals(false, phase("POWB", "Planning", Boolean.FALSE).isReporting().booleanValue());
    assertEquals(false, phase("AR", null, Boolean.FALSE).isReporting().booleanValue());
  }
}
