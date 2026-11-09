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

package org.cgiar.ccafs.marlo.action.summaries;

import org.apache.struts2.dispatcher.Parameter;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * The public innovation report reads its innovationID from anyone. A value that is not a number, such as the template
 * expressions scanners send, must be answered as a missing innovation instead of an unhandled exception.
 */
public class ProjectInnovationSummaryActionIdTest {

  private static Parameter request(String value) {
    return new Parameter.Request("innovationID", value);
  }

  @Test
  public void aNumberIsTheId() {
    assertEquals(Long.valueOf(1767L), ProjectInnovationSummaryAction.readInnovationID(request(" 1767 ")));
  }

  @Test
  public void theExpressionsOfAScannerAreNoId() {
    assertNull(ProjectInnovationSummaryAction.readInnovationID(request("${900964452+919539869}")));
    assertNull(ProjectInnovationSummaryAction.readInnovationID(request("/*1*/{{850909520+980972544}}")));
  }

  @Test
  public void aMissingOrBlankValueIsNoId() {
    assertNull(ProjectInnovationSummaryAction.readInnovationID(null));
    assertNull(ProjectInnovationSummaryAction.readInnovationID(new Parameter.Empty("innovationID")));
    assertNull(ProjectInnovationSummaryAction.readInnovationID(request("  ")));
  }

  @Test
  public void aNumberTooLargeForAnIdIsNoId() {
    assertNull(ProjectInnovationSummaryAction.readInnovationID(request("99999999999999999999")));
  }
}
