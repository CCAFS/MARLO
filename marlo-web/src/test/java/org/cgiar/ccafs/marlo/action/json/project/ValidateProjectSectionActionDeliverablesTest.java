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

package org.cgiar.ccafs.marlo.action.json.project;

import org.cgiar.ccafs.marlo.data.model.DeliverableInfo;
import org.cgiar.ccafs.marlo.data.model.Phase;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * The deliverables the section answer reports are the ones the menu counts: required to be completed in the phase, or
 * completed for it or a later one. Phase 2026 below, as the Planning phase where the local copy showed the problem.
 */
public class ValidateProjectSectionActionDeliverablesTest {

  private static final int PHASE_YEAR = 2026;

  private static DeliverableInfo info(Integer status, int year, Integer newExpectedYear) {
    Phase phase = new Phase();
    phase.setId(Long.valueOf(429L));
    phase.setYear(PHASE_YEAR);
    DeliverableInfo info = new DeliverableInfo();
    info.setPhase(phase);
    info.setStatus(status);
    info.setYear(year);
    info.setNewExpectedYear(newExpectedYear);
    return info;
  }

  @Test
  public void aCancelledDeliverableIsNotReported() {
    assertFalse(ValidateProjectSectionAction.isValidatedDeliverable(info(5, PHASE_YEAR, -1)));
  }

  @Test
  public void aDeliverableCompletedInAnEarlierYearIsNotReported() {
    assertFalse(ValidateProjectSectionAction.isValidatedDeliverable(info(3, 2023, -1)));
  }

  @Test
  public void aDeliverableCompletedThisYearIsReported() {
    assertTrue(ValidateProjectSectionAction.isValidatedDeliverable(info(3, PHASE_YEAR, -1)));
  }

  @Test
  public void aDeliverableCompletedForALaterYearIsReportedLikeTheMenuDoes() {
    assertTrue(ValidateProjectSectionAction.isValidatedDeliverable(info(3, 2024, 2027)));
  }

  @Test
  public void aDeliverableCompletedForAnEarlierExpectedYearIsNotReported() {
    assertFalse(ValidateProjectSectionAction.isValidatedDeliverable(info(3, 2024, 2025)));
  }

  @Test
  public void aDeliverableExtendedToAnotherYearIsNotReported() {
    assertFalse(ValidateProjectSectionAction.isValidatedDeliverable(info(4, 2024, 2025)));
  }

  @Test
  public void aDeliverableExtendedToThisYearIsReported() {
    assertTrue(ValidateProjectSectionAction.isValidatedDeliverable(info(4, 2024, PHASE_YEAR)));
  }

  @Test
  public void anOngoingOrUnstatedDeliverableIsReported() {
    assertTrue(ValidateProjectSectionAction.isValidatedDeliverable(info(2, 2023, -1)));
    assertTrue(ValidateProjectSectionAction.isValidatedDeliverable(info(null, PHASE_YEAR, -1)));
  }

  @Test
  public void withoutInformationInThePhaseNothingIsReported() {
    assertFalse(ValidateProjectSectionAction.isValidatedDeliverable(null));
  }
}
