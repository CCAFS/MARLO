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

import org.cgiar.ccafs.marlo.data.model.CrpMilestone;
import org.cgiar.ccafs.marlo.data.model.PowbIndAssesmentRisk;
import org.cgiar.ccafs.marlo.data.model.PowbIndFollowingMilestone;
import org.cgiar.ccafs.marlo.data.model.PowbIndMilestoneRisk;
import org.cgiar.ccafs.marlo.data.model.RepIndGenderYouthFocusLevel;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * A2-2437 renders the AICCRA period targets as a matrix, and each cell only submits the
 * fields the design shows: id, composeID, title, code, year, value, srfTargetUnit.id,
 * milestonesStatus.id and extendedYear.
 * <p>
 * OutcomesAction.saveMilestones() applies {@link CrpMilestone#copyFields(CrpMilestone)} to the
 * row loaded from the database, and copyFields copies nulls. Without a guard, every column the
 * matrix does not bind — the POWB narrative, the DAC focus levels, the risk references — would
 * be wiped on the next save. The dev database holds 630 milestones with
 * powb_milestone_verification and ~625 with each focus level, so this is not hypothetical.
 * <p>
 * These tests pin the two halves of the contract: a field that was never submitted is restored,
 * and a field that was submitted empty still clears, so the legacy non-AICCRA form can blank a
 * value on purpose.
 */
public class MilestoneUnboundFieldsTest {

  private RepIndGenderYouthFocusLevel focusLevel(long id) {
    RepIndGenderYouthFocusLevel level = new RepIndGenderYouthFocusLevel();
    level.setId(id);
    return level;
  }

  /**
   * Applies the same sequence saveMilestones() uses: snapshot, copyFields, restore.
   *
   * @param stored the milestone loaded from the database
   * @param incoming the milestone bound from the submitted form
   */
  private void saveLikeAction(CrpMilestone stored, CrpMilestone incoming) {
    CrpMilestone before = new CrpMilestone();
    before.copyFields(stored);
    stored.copyFields(incoming);
    OutcomesAction.restoreUnboundMilestoneFields(stored, incoming, before);
  }

  /** A milestone as the database holds it for AICCRA, with POWB and DAC data filled in. */
  private CrpMilestone storedMilestone() {
    CrpMilestone stored = new CrpMilestone();
    stored.setTitle("of which women beneficiaries");
    stored.setCode("1.1");
    stored.setYear(2026);
    stored.setPowbMilestoneVerification("Partner survey, annex 4");
    stored.setPowbMilestoneOtherRisk("Delayed partner reporting");
    stored.setGenderFocusLevel(this.focusLevel(2));
    stored.setYouthFocusLevel(this.focusLevel(3));
    stored.setCapdevFocusLevel(this.focusLevel(4));
    stored.setClimateFocusLevel(this.focusLevel(5));
    PowbIndFollowingMilestone following = new PowbIndFollowingMilestone();
    following.setId(7L);
    stored.setPowbIndFollowingMilestone(following);
    PowbIndAssesmentRisk assesmentRisk = new PowbIndAssesmentRisk();
    assesmentRisk.setId(8L);
    stored.setPowbIndAssesmentRisk(assesmentRisk);
    PowbIndMilestoneRisk milestoneRisk = new PowbIndMilestoneRisk();
    milestoneRisk.setId(9L);
    stored.setPowbIndMilestoneRisk(milestoneRisk);
    stored.setOrderIndex(3);
    stored.setIsPowb(Boolean.TRUE);
    return stored;
  }

  /** What the AICCRA matrix cell actually posts: the design's fields and nothing else. */
  private CrpMilestone matrixCellSubmission() {
    CrpMilestone incoming = new CrpMilestone();
    incoming.setTitle("of which women beneficiaries");
    incoming.setCode("1.1");
    incoming.setYear(2026);
    return incoming;
  }

  @Test
  public void copyFieldsAloneWipesTheUnboundColumns() {
    CrpMilestone stored = this.storedMilestone();
    // No guard: this is what the save did before A2-2437 added one.
    stored.copyFields(this.matrixCellSubmission());

    assertNull("copyFields copies the null over the stored narrative",
      stored.getPowbMilestoneVerification());
    assertNull("copyFields copies the null over the stored focus level", stored.getGenderFocusLevel());
    assertNull("copyFields copies the null over the stored risk", stored.getPowbIndAssesmentRisk());
  }

  @Test
  public void matrixSaveKeepsTheColumnsItDoesNotSubmit() {
    CrpMilestone stored = this.storedMilestone();
    this.saveLikeAction(stored, this.matrixCellSubmission());

    assertEquals("Partner survey, annex 4", stored.getPowbMilestoneVerification());
    assertEquals("Delayed partner reporting", stored.getPowbMilestoneOtherRisk());
    assertEquals(Long.valueOf(2), stored.getGenderFocusLevel().getId());
    assertEquals(Long.valueOf(3), stored.getYouthFocusLevel().getId());
    assertEquals(Long.valueOf(4), stored.getCapdevFocusLevel().getId());
    assertEquals(Long.valueOf(5), stored.getClimateFocusLevel().getId());
    assertEquals(Long.valueOf(7), stored.getPowbIndFollowingMilestone().getId());
    assertEquals(Long.valueOf(8), stored.getPowbIndAssesmentRisk().getId());
    assertEquals(Long.valueOf(9), stored.getPowbIndMilestoneRisk().getId());
    assertEquals(Integer.valueOf(3), stored.getOrderIndex());
    assertTrue(stored.getIsPowb());
  }

  @Test
  public void matrixSaveStillWritesTheFieldsItDoesSubmit() {
    CrpMilestone stored = this.storedMilestone();
    CrpMilestone incoming = this.matrixCellSubmission();
    incoming.setTitle("of which youth beneficiaries (15-35)");
    incoming.setCode("1.2");
    incoming.setYear(2027);

    this.saveLikeAction(stored, incoming);

    assertEquals("of which youth beneficiaries (15-35)", stored.getTitle());
    assertEquals("1.2", stored.getCode());
    assertEquals(Integer.valueOf(2027), stored.getYear());
  }

  @Test
  public void legacyFormCanStillClearAValueOnPurpose() {
    CrpMilestone stored = this.storedMilestone();
    CrpMilestone incoming = this.matrixCellSubmission();
    // The non-AICCRA macro renders these inputs, so an emptied field binds as "" — not null.
    incoming.setPowbMilestoneVerification("");
    incoming.setPowbMilestoneOtherRisk("");

    this.saveLikeAction(stored, incoming);

    assertEquals("an empty submitted value must clear the column", "",
      stored.getPowbMilestoneVerification());
    assertEquals("", stored.getPowbMilestoneOtherRisk());
    // Everything it did not submit is still preserved.
    assertEquals(Long.valueOf(2), stored.getGenderFocusLevel().getId());
  }

  @Test
  public void legacyFormCanStillReplaceAFocusLevel() {
    CrpMilestone stored = this.storedMilestone();
    CrpMilestone incoming = this.matrixCellSubmission();
    incoming.setGenderFocusLevel(this.focusLevel(11));

    this.saveLikeAction(stored, incoming);

    assertEquals("a submitted focus level must win over the stored one", Long.valueOf(11),
      stored.getGenderFocusLevel().getId());
  }
}
