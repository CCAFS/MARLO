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

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * A2-2437 removed the Order field from the Overall Performance Indicators form, so
 * orderIndex no longer binds from the request. CrpProgramOutcome.copyFields() copies
 * nulls, which would silently wipe the stored order_index on the next save.
 * OutcomesAction guards against that; these tests pin both halves of the behaviour.
 */
public class CrpProgramOutcomeOrderIndexTest {

  /**
   * Reproduces the guard OutcomesAction applies around copyFields().
   *
   * @param stored the outcome loaded from the database
   * @param incoming the outcome bound from the request
   */
  private void copyPreservingOrderIndex(CrpProgramOutcome stored, CrpProgramOutcome incoming) {
    Integer storedOrderIndex = stored.getOrderIndex();
    stored.copyFields(incoming);
    if (incoming.getOrderIndex() == null) {
      stored.setOrderIndex(storedOrderIndex);
    }
  }

  private CrpProgramOutcome outcomeWith(Integer orderIndex, String description) {
    CrpProgramOutcome outcome = new CrpProgramOutcome();
    outcome.setOrderIndex(orderIndex);
    outcome.setDescription(description);
    return outcome;
  }

  /**
   * The regression itself: a bare copyFields() from a form that no longer carries the
   * field nulls the stored value. If this ever stops holding, the guard is redundant.
   */
  @Test
  public void copyFieldsAloneWipesTheStoredOrderIndex() {
    CrpProgramOutcome stored = this.outcomeWith(31, "stored statement");
    CrpProgramOutcome incoming = this.outcomeWith(null, "edited statement");

    stored.copyFields(incoming);

    assertNull("copyFields is expected to copy the incoming null", stored.getOrderIndex());
  }

  /**
   * A form that still sends the field keeps winning, so the guard cannot freeze the value.
   */
  @Test
  public void anIncomingOrderIndexStillOverwritesTheStoredOne() {
    CrpProgramOutcome stored = this.outcomeWith(31, "stored statement");
    CrpProgramOutcome incoming = this.outcomeWith(99, "edited statement");

    this.copyPreservingOrderIndex(stored, incoming);

    assertEquals(Integer.valueOf(99), stored.getOrderIndex());
  }

  /**
   * The guard keeps order_index intact while still applying the rest of the edit.
   */
  @Test
  public void theGuardKeepsTheStoredOrderIndexWhenTheFormOmitsIt() {
    CrpProgramOutcome stored = this.outcomeWith(31, "stored statement");
    CrpProgramOutcome incoming = this.outcomeWith(null, "edited statement");

    this.copyPreservingOrderIndex(stored, incoming);

    assertEquals(Integer.valueOf(31), stored.getOrderIndex());
    assertEquals("the rest of the edit must still be applied", "edited statement", stored.getDescription());
  }

  /**
   * An outcome created through the redesigned form has no stored value to fall back to.
   */
  @Test
  public void aBrandNewOutcomeKeepsANullOrderIndex() {
    CrpProgramOutcome stored = this.outcomeWith(null, null);
    CrpProgramOutcome incoming = this.outcomeWith(null, "new statement");

    this.copyPreservingOrderIndex(stored, incoming);

    assertNull(stored.getOrderIndex());
    assertEquals("new statement", stored.getDescription());
  }
}
