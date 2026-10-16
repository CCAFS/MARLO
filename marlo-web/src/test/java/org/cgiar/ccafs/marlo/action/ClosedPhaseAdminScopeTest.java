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

import org.cgiar.ccafs.marlo.data.model.Phase;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * A closed phase is read-only for everyone, with one exception the PM asked for: Admin and Super
 * Admin keep editing rights in Overall Performance Indicators, because they are the ones who
 * correct a past cycle.
 * <p>
 * {@link BaseAction#canEditClosedPhaseOutcomes()} is the single definition of that exception, and
 * two callers depend on it agreeing with itself: EditImpactPathwayInterceptor, which decides what
 * the form renders, and OutcomesAction.save(), which decides what is accepted. If they ever
 * disagree, the failure is silent in the worst direction — a form that invites edits and then
 * answers NOT_AUTHORIZED, or a form that looks locked while the endpoint still writes.
 * <p>
 * The scope matters as much as the rule. The interceptor that applies it is shared with cluster
 * activities, program impacts, research topics, outputs and next users; none of those were part of
 * the decision, so widening this predicate quietly grants permissions nobody asked for.
 */
public class ClosedPhaseAdminScopeTest {

  private static final String OUTCOMES = "AICCRA/outcomes";

  /**
   * A BaseAction pinned to the four things the rule reads, so the rule is tested and nothing else.
   *
   * @param phaseEditable the phase's editable flag; null for a phase that never set one
   * @param superAdmin whether the user holds full privileges
   * @param crpAdmin whether the user holds the CRP admin grant
   * @param actionName the action being served
   * @return an action that answers exactly those four questions
   */
  private BaseAction action(final Boolean phaseEditable, final boolean superAdmin, final boolean crpAdmin,
    final String actionName) {
    final Phase phase;
    if (phaseEditable == null) {
      phase = null;
    } else {
      phase = new Phase();
      phase.setEditable(phaseEditable);
    }
    return new BaseAction(null) {

      private static final long serialVersionUID = 1L;

      @Override
      public boolean canAccessSuperAdmin() {
        return superAdmin;
      }

      @Override
      public boolean canEditCrpAdmin() {
        return crpAdmin;
      }

      @Override
      public String getActionName() {
        return actionName;
      }

      @Override
      public Phase getActualPhase() {
        return phase;
      }
    };
  }

  /** The exception is for the section it was decided for, and stops there. */
  @Test
  public void anotherImpactPathwaySectionStaysLocked() {
    assertFalse("cluster activities was not part of the decision and keeps the closed-phase lock",
      this.action(Boolean.FALSE, true, false, "AICCRA/clusterActivities").canEditClosedPhaseOutcomes());
    assertFalse("nor was program impacts",
      this.action(Boolean.FALSE, true, false, "AICCRA/programimpacts").canEditClosedPhaseOutcomes());
  }

  /** The CRP admin is the second half of the pair the PM named. */
  @Test
  public void crpAdminMayEditAClosedPhase() {
    assertTrue("Admin corrects a past cycle too",
      this.action(Boolean.FALSE, false, true, OUTCOMES).canEditClosedPhaseOutcomes());
  }

  /** Every other role keeps the lock, which is the rest of the PM's sentence. */
  @Test
  public void everyOtherRoleKeepsTheLock() {
    assertFalse("a closed phase stays read-only for anyone who is not an admin",
      this.action(Boolean.FALSE, false, false, OUTCOMES).canEditClosedPhaseOutcomes());
  }

  /** A phase with no flag set is not evidence that it is closed. */
  @Test
  public void noPhaseAndNoFlagAreNotTreatedAsClosed() {
    assertFalse("no phase means there is nothing to unlock",
      this.action(null, true, false, OUTCOMES).canEditClosedPhaseOutcomes());
    Phase blank = new Phase();
    BaseAction noFlag = new BaseAction(null) {

      private static final long serialVersionUID = 1L;

      @Override
      public boolean canAccessSuperAdmin() {
        return true;
      }

      @Override
      public String getActionName() {
        return OUTCOMES;
      }

      @Override
      public Phase getActualPhase() {
        return blank;
      }
    };
    assertFalse("a phase whose editable flag was never set is not unlocked either",
      noFlag.canEditClosedPhaseOutcomes());
  }

  /**
   * In an open phase the ordinary permissions already decide, and they are the ones that carry the
   * per-programme scoping. Answering true here would hand admins a second, blunter route.
   */
  @Test
  public void openPhaseIsLeftToTheOrdinaryPermissions() {
    assertFalse("an open phase has no lock to lift",
      this.action(Boolean.TRUE, true, false, OUTCOMES).canEditClosedPhaseOutcomes());
  }

  /** The case the ticket is about. */
  @Test
  public void superAdminMayEditAClosedPhase() {
    assertTrue("Super Admin edits Overall Performance Indicators in a closed phase",
      this.action(Boolean.FALSE, true, false, OUTCOMES).canEditClosedPhaseOutcomes());
  }

  /** A null action name must not blow up the interceptor on its way through. */
  @Test
  public void unknownActionIsNotUnlocked() {
    assertFalse("no action name means no reason to lift the lock",
      this.action(Boolean.FALSE, true, false, null).canEditClosedPhaseOutcomes());
  }
}
