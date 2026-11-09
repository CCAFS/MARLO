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

package org.cgiar.ccafs.marlo.action.impactpathway;

import java.lang.reflect.Constructor;
import java.util.Arrays;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * The year selects of the indicators offer a fixed range. A stored year missing from it is shown empty in edit mode
 * and saved back as -1, so the range must keep every year the program has used.
 */
public class OutcomesActionTargetYearsTest {

  private static OutcomesAction action() throws Exception {
    // The constructor only stores its collaborators, and the target years read none of them
    Constructor<?> constructor = OutcomesAction.class.getConstructors()[0];
    return (OutcomesAction) constructor.newInstance(new Object[constructor.getParameterCount()]);
  }

  @Test
  public void theYearsAre2021To2031InOrder() throws Exception {
    assertEquals(Arrays.asList(2021, 2022, 2023, 2024, 2025, 2026, 2027, 2028, 2029, 2030, 2031),
      action().getTargetYears());
  }
}
