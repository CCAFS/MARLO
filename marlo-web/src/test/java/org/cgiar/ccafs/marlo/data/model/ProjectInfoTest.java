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
 * Tests for {@link ProjectInfo#updateProjectInfo(ProjectInfo)}, the copy that replicates a Planning save to every
 * later phase.
 */
public class ProjectInfoTest {

  private ProjectInfo reportingPhaseInfo() {
    ProjectInfo info = new ProjectInfo();
    info.setLessonsLearned("Lessons reported in the Reporting phase");
    info.setDimension("Gender justification reported in the Reporting phase");
    info.setCrossCuttingCapacity(true);
    info.setCrossCuttingClimate(true);
    info.setCrossCuttingGender(true);
    info.setCrossCuttingYouth(true);
    info.setCrossCuttingNa(false);
    return info;
  }

  @Test
  public void replicationKeepsTheHiddenFieldsOfTheLaterPhase() {
    // A Planning save whose hidden fields are empty, replicated onto a Reporting phase that has them filled
    ProjectInfo planning = new ProjectInfo();
    planning.setSummary("Planning summary");
    ProjectInfo reporting = this.reportingPhaseInfo();

    reporting.updateProjectInfo(planning);

    assertEquals("Lessons reported in the Reporting phase", reporting.getLessonsLearned());
    assertEquals("Gender justification reported in the Reporting phase", reporting.getDimension());
    assertEquals(Boolean.TRUE, reporting.getCrossCuttingCapacity());
    assertEquals(Boolean.TRUE, reporting.getCrossCuttingClimate());
    assertEquals(Boolean.TRUE, reporting.getCrossCuttingGender());
    assertEquals(Boolean.TRUE, reporting.getCrossCuttingYouth());
    assertEquals(Boolean.FALSE, reporting.getCrossCuttingNa());
  }

  @Test
  public void replicationStillCopiesTheEditableFields() {
    ProjectInfo planning = new ProjectInfo();
    planning.setSummary("Planning summary");
    planning.setChallengesSolutions("Planning challenges");
    ProjectInfo reporting = this.reportingPhaseInfo();
    reporting.setSummary("Old summary");

    reporting.updateProjectInfo(planning);

    assertEquals("Planning summary", reporting.getSummary());
    assertEquals("Planning challenges", reporting.getChallengesSolutions());
  }

  @Test
  public void aNewPhaseRecordInheritsTheHiddenFields() {
    // saveInfoPhase() creates the record of a phase that has none: updateProjectInfo() and then
    // copyHiddenDescriptionFields(), so the new phase starts with the saved phase's values as it always did
    ProjectInfo planning = this.reportingPhaseInfo();
    ProjectInfo newPhase = new ProjectInfo();

    newPhase.updateProjectInfo(planning);
    newPhase.copyHiddenDescriptionFields(planning);

    assertEquals("Lessons reported in the Reporting phase", newPhase.getLessonsLearned());
    assertEquals("Gender justification reported in the Reporting phase", newPhase.getDimension());
    assertEquals(Boolean.TRUE, newPhase.getCrossCuttingCapacity());
    assertEquals(Boolean.TRUE, newPhase.getCrossCuttingClimate());
    assertEquals(Boolean.TRUE, newPhase.getCrossCuttingGender());
    assertEquals(Boolean.TRUE, newPhase.getCrossCuttingYouth());
    assertEquals(Boolean.FALSE, newPhase.getCrossCuttingNa());
  }

  @Test
  public void copyingTheHiddenFieldsFromNoSourceChangesNothing() {
    ProjectInfo reporting = this.reportingPhaseInfo();

    reporting.copyHiddenDescriptionFields(null);

    assertEquals("Lessons reported in the Reporting phase", reporting.getLessonsLearned());
    assertEquals(Boolean.TRUE, reporting.getCrossCuttingGender());
  }

  @Test
  public void updatingFromNoSourceChangesNothing() {
    ProjectInfo reporting = this.reportingPhaseInfo();
    reporting.setSummary("Reporting summary");

    reporting.updateProjectInfo(null);

    assertEquals("Reporting summary", reporting.getSummary());
    assertEquals("Lessons reported in the Reporting phase", reporting.getLessonsLearned());
  }

  @Test
  public void aNewEmptyPhaseRecordStaysEmptyWithoutTheCopy() {
    // updateProjectInfo() alone no longer carries the hidden fields
    ProjectInfo newPhase = new ProjectInfo();

    newPhase.updateProjectInfo(this.reportingPhaseInfo());

    assertNull(newPhase.getLessonsLearned());
    assertNull(newPhase.getCrossCuttingGender());
  }
}
