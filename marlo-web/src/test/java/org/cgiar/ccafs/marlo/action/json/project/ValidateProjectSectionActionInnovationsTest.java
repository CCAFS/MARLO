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

import org.cgiar.ccafs.marlo.data.model.ProjectInnovationInfo;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * The innovations the section answer reports are the ones the menu counts: with information in the phase, of the
 * year of the cycle.
 */
public class ValidateProjectSectionActionInnovationsTest {

  private static final int CYCLE_YEAR = 2025;

  private static ProjectInnovationInfo info(Long year) {
    ProjectInnovationInfo info = new ProjectInnovationInfo();
    info.setYear(year);
    return info;
  }

  @Test
  public void anInnovationOfAnotherYearIsNotReported() {
    assertFalse(ValidateProjectSectionAction.isCountedInnovation(info(Long.valueOf(2024L)), CYCLE_YEAR));
  }

  @Test
  public void anInnovationOfTheCycleYearIsReported() {
    assertTrue(ValidateProjectSectionAction.isCountedInnovation(info(Long.valueOf(CYCLE_YEAR)), CYCLE_YEAR));
  }

  @Test
  public void anInnovationWithoutAYearIsStillReported() {
    assertTrue(ValidateProjectSectionAction.isCountedInnovation(info(null), CYCLE_YEAR));
  }

  @Test
  public void anInnovationWithoutInformationInThePhaseIsNotReported() {
    assertFalse(ValidateProjectSectionAction.isCountedInnovation(null, CYCLE_YEAR));
  }
}
