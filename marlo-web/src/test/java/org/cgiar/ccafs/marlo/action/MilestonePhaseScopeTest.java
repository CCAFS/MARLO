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
 * A2-2469: phased data is forward-only, so a period target whose year is behind the phase being
 * rendered is shown read-only and can no longer be answered. The form has always known that —
 * OutcomesAction.canEditMileStone() gates the cell — but OutcomeValidator did not, and reported
 * those cells as missing fields. The section could then never be completed: it asked for values in
 * boxes the user cannot type into.
 * <p>
 * {@link BaseAction#canEditMilestoneYear(Integer)} is now the single definition of that rule, read
 * by both. These tests pin it, because widening it would let a closed year become editable and
 * narrowing it would bring the unfixable gap back.
 */
public class MilestonePhaseScopeTest {

  /** A BaseAction pinned to one phase year, which is all the rule reads. */
  private BaseAction actionInPhase(final int year) {
    Phase phase = new Phase();
    phase.setYear(year);
    return new BaseAction(null) {

      private static final long serialVersionUID = 1L;

      @Override
      public Phase getActualPhase() {
        return phase;
      }
    };
  }

  /** A target still being created carries no year, and stays editable. */
  @Test
  public void aTargetWithNoYearIsEditable() {
    assertTrue("a period target with no year yet is still being created",
      this.actionInPhase(2026).canEditMilestoneYear(null));
  }

  /** -1 is the placeholder the year select posts before anything is chosen. */
  @Test
  public void thePlaceholderYearIsEditable() {
    assertTrue("-1 means the year has not been chosen, not that it is behind",
      this.actionInPhase(2026).canEditMilestoneYear(Integer.valueOf(-1)));
  }

  /** The phase's own year is open: this is the year being reported on. */
  @Test
  public void theCurrentPhaseYearIsEditable() {
    assertTrue("the year the phase reports on is open",
      this.actionInPhase(2026).canEditMilestoneYear(Integer.valueOf(2026)));
  }

  /** Later years are open too — the matrix plans ahead. */
  @Test
  public void aFutureYearIsEditable() {
    assertTrue("a year ahead of the phase is still to be answered",
      this.actionInPhase(2026).canEditMilestoneYear(Integer.valueOf(2027)));
  }

  /** The regression this ticket was about: closed years are not reportable as missing. */
  @Test
  public void aYearBehindThePhaseIsNotEditable() {
    BaseAction action = this.actionInPhase(2026);
    assertFalse("2025 closed when the phase moved to 2026",
      action.canEditMilestoneYear(Integer.valueOf(2025)));
    assertFalse("and so did every year before it",
      action.canEditMilestoneYear(Integer.valueOf(2024)));
  }
}
