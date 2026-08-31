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

package org.cgiar.ccafs.marlo.action.crp.admin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Grouping of the activity title relation rows. Activities replicate forward, so the query returns one row per
 * phase and the grouping has to collapse them into one row per logical activity.
 */
public class CrpActivityActionTest {

  private static final long CURRENT_PHASE = 200L;

  private Map<String, Object> row(long titleId, long clusterId, String composedId, long activityId, boolean active,
    String description, long phaseId, String phaseName, int phaseYear, String clusterTitle) {
    Map<String, Object> row = new HashMap<>();
    row.put("titleId", Long.valueOf(titleId));
    row.put("clusterId", Long.valueOf(clusterId));
    row.put("composedId", composedId);
    row.put("activityId", Long.valueOf(activityId));
    row.put("activityActive", Integer.valueOf(active ? 1 : 0));
    row.put("activityDescription", description);
    row.put("phaseId", Long.valueOf(phaseId));
    row.put("phaseName", phaseName);
    row.put("phaseYear", Integer.valueOf(phaseYear));
    row.put("clusterTitle", clusterTitle);
    return row;
  }

  @Test
  public void testEmptyAndNullInputAreHandled() {
    assertTrue(CrpActivityAction.groupRelations(null, CURRENT_PHASE).isEmpty());
    assertTrue(CrpActivityAction.groupRelations(new ArrayList<Map<String, Object>>(), CURRENT_PHASE).isEmpty());

    List<Map<String, Object>> rows = new ArrayList<>();
    rows.add(null);
    assertTrue(CrpActivityAction.groupRelations(rows, CURRENT_PHASE).isEmpty());
  }

  @Test
  public void testGroupsRepeatedPhasesIntoOneRowWithASpan() {
    // Same activity (composedId 10-4) replicated over three phases
    List<Map<String, Object>> rows = Arrays.asList(
      this.row(1L, 10L, "10-4", 41L, true, "Seed systems", 100L, "AR", 2022, "Cluster Ten"),
      this.row(1L, 10L, "10-4", 42L, true, "Seed systems", CURRENT_PHASE, "AR", 2023, "Cluster Ten"),
      this.row(1L, 10L, "10-4", 43L, true, "Seed systems", 300L, "AR", 2024, "Cluster Ten"));

    Map<Long, List<ActivityTitleRelation>> grouped = CrpActivityAction.groupRelations(rows, CURRENT_PHASE);

    assertEquals(1, grouped.size());
    List<ActivityTitleRelation> relations = grouped.get(1L);
    assertEquals("three phase rows collapse into one activity", 1, relations.size());

    ActivityTitleRelation relation = relations.get(0);
    assertEquals(10L, relation.getClusterId());
    assertEquals("Cluster Ten", relation.getClusterTitle());
    assertEquals("10-4", relation.getComposedId());
    assertEquals(3, relation.getPhaseCount());
    assertEquals("every phase is listed, in chronological order", Arrays.asList("AR 2022", "AR 2023", "AR 2024"),
      relation.getPhaseLabels());
    assertEquals("AR 2024", relation.getPhaseLabels().get(relation.getPhaseCount() - 1));
    assertTrue(relation.isReportedInCurrentPhase());
  }

  @Test
  public void testSinglePhaseCollapsesTheRange() {
    List<Map<String, Object>> rows = Arrays.asList(
      this.row(1L, 22L, "22-7", 71L, true, "Advisory bundles", CURRENT_PHASE, "AR", 2023, "Cluster Two"));

    ActivityTitleRelation relation = CrpActivityAction.groupRelations(rows, CURRENT_PHASE).get(1L).get(0);
    assertEquals(1, relation.getPhaseCount());
    assertEquals(Arrays.asList("AR 2023"), relation.getPhaseLabels());
  }

  @Test
  public void testPhaseLabelHasNoDashSoTheListStaysReadable() {
    List<Map<String, Object>> rows = Arrays.asList(
      this.row(1L, 10L, "10-4", 41L, true, "x", 100L, "POWB", 2023, "Cluster Ten"),
      this.row(1L, 10L, "10-4", 42L, true, "x", CURRENT_PHASE, "AR", 2023, "Cluster Ten"),
      this.row(1L, 10L, "10-4", 43L, true, "x", 300L, "UpKeep", 2023, "Cluster Ten"));

    ActivityTitleRelation relation = CrpActivityAction.groupRelations(rows, CURRENT_PHASE).get(1L).get(0);
    assertEquals("POWB 2023, AR 2023, UpKeep 2023", String.join(", ", relation.getPhaseLabels()));
    assertEquals("three phases of the same year stay distinct", 3, relation.getPhaseCount());
    assertEquals("chronological tail", "UpKeep 2023",
      relation.getPhaseLabels().get(relation.getPhaseCount() - 1));
  }

  @Test
  public void testGapsAreVisibleBecauseEveryPhaseIsListed() {
    // Reported in 2021 and 2023 but not in 2022: the list must not imply continuity
    List<Map<String, Object>> rows = Arrays.asList(
      this.row(1L, 10L, "10-4", 41L, true, "x", 100L, "AR", 2021, "Cluster Ten"),
      this.row(1L, 10L, "10-4", 43L, true, "x", 300L, "AR", 2023, "Cluster Ten"));

    ActivityTitleRelation relation = CrpActivityAction.groupRelations(rows, CURRENT_PHASE).get(1L).get(0);
    assertEquals(Arrays.asList("AR 2021", "AR 2023"), relation.getPhaseLabels());
    assertEquals(2, relation.getPhaseCount());
  }

  @Test
  public void testRepeatedPhaseLabelIsNotDuplicated() {
    List<Map<String, Object>> rows = Arrays.asList(
      this.row(1L, 10L, "10-4", 41L, true, "x", 100L, "AR", 2023, "Cluster Ten"),
      this.row(1L, 10L, "10-4", 42L, true, "x", 100L, "AR", 2023, "Cluster Ten"));

    ActivityTitleRelation relation = CrpActivityAction.groupRelations(rows, CURRENT_PHASE).get(1L).get(0);
    assertEquals(1, relation.getPhaseCount());
  }

  @Test
  public void testEmptyRelationHasNoPhases() {
    assertEquals(0, new ActivityTitleRelation().getPhaseCount());
    assertTrue(new ActivityTitleRelation().getPhaseLabels().isEmpty());
  }

  @Test
  public void testStatusComesFromTheCurrentPhaseOnly() {
    // Active in past phases, removed in the current one
    List<Map<String, Object>> rows = Arrays.asList(
      this.row(1L, 10L, "10-4", 41L, true, "Old text", 100L, "AR", 2022, "Cluster Ten"),
      this.row(1L, 10L, "10-4", 42L, false, "Old text", CURRENT_PHASE, "AR", 2023, "Cluster Ten"));

    ActivityTitleRelation relation = CrpActivityAction.groupRelations(rows, CURRENT_PHASE).get(1L).get(0);
    assertFalse("removed in the current phase", relation.isReportedInCurrentPhase());
  }

  @Test
  public void testActivityWithNoRowInTheCurrentPhaseIsNotReported() {
    List<Map<String, Object>> rows = Arrays.asList(
      this.row(1L, 31L, "31-9", 91L, true, "Historic only", 100L, "AR", 2021, "Cluster Three"));

    ActivityTitleRelation relation = CrpActivityAction.groupRelations(rows, CURRENT_PHASE).get(1L).get(0);
    assertFalse("never reaches the current phase", relation.isReportedInCurrentPhase());
    assertEquals("Historic only", relation.getActivityDescription());
  }

  @Test
  public void testCurrentPhaseDescriptionWins() {
    List<Map<String, Object>> rows = Arrays.asList(
      this.row(1L, 10L, "10-4", 41L, true, "Draft text", 100L, "AR", 2022, "Cluster Ten"),
      this.row(1L, 10L, "10-4", 42L, true, "Reported text", CURRENT_PHASE, "AR", 2023, "Cluster Ten"),
      this.row(1L, 10L, "10-4", 43L, true, "Future copy", 300L, "AR", 2024, "Cluster Ten"));

    ActivityTitleRelation relation = CrpActivityAction.groupRelations(rows, CURRENT_PHASE).get(1L).get(0);
    assertEquals("Reported text", relation.getActivityDescription());
  }

  @Test
  public void testDifferentActivitiesAndClustersStaySeparate() {
    List<Map<String, Object>> rows = Arrays.asList(
      this.row(1L, 10L, "10-4", 41L, true, "A", CURRENT_PHASE, "AR", 2023, "Cluster Ten"),
      this.row(1L, 10L, "10-5", 51L, true, "B", CURRENT_PHASE, "AR", 2023, "Cluster Ten"),
      this.row(1L, 22L, "22-7", 71L, true, "C", CURRENT_PHASE, "AR", 2023, "Cluster Two"),
      this.row(2L, 10L, "10-6", 61L, true, "D", CURRENT_PHASE, "AR", 2023, "Cluster Ten"));

    Map<Long, List<ActivityTitleRelation>> grouped = CrpActivityAction.groupRelations(rows, CURRENT_PHASE);
    assertEquals("two activity titles", 2, grouped.size());
    assertEquals("title 1 keeps three separate activities", 3, grouped.get(1L).size());
    assertEquals("title 2 keeps its own activity", 1, grouped.get(2L).size());
  }

  @Test
  public void testLegacyRowsWithoutComposedIdAreNotMerged() {
    // composedId is nullable: two distinct activities of the same cluster must not collapse into one
    List<Map<String, Object>> rows = Arrays.asList(
      this.row(1L, 10L, null, 41L, true, "First", CURRENT_PHASE, "AR", 2023, "Cluster Ten"),
      this.row(1L, 10L, null, 42L, true, "Second", CURRENT_PHASE, "AR", 2023, "Cluster Ten"));

    List<ActivityTitleRelation> relations = CrpActivityAction.groupRelations(rows, CURRENT_PHASE).get(1L);
    assertEquals("each legacy activity stands alone", 2, relations.size());
    assertEquals("", relations.get(0).getComposedId());
  }

  @Test
  public void testRowsWithMissingKeysAreSkipped() {
    Map<String, Object> noTitle = this.row(1L, 10L, "10-4", 41L, true, "x", CURRENT_PHASE, "AR", 2023, "Ten");
    noTitle.remove("titleId");
    Map<String, Object> noCluster = this.row(1L, 10L, "10-4", 41L, true, "x", CURRENT_PHASE, "AR", 2023, "Ten");
    noCluster.put("clusterId", null);

    assertTrue(CrpActivityAction.groupRelations(Arrays.asList(noTitle), CURRENT_PHASE).isEmpty());
    assertTrue(CrpActivityAction.groupRelations(Arrays.asList(noCluster), CURRENT_PHASE).isEmpty());
  }

  @Test
  public void testStringAndBigIntegerColumnTypesAreAccepted() {
    // JDBC drivers can hand back strings or BigInteger for these columns
    Map<String, Object> row = new HashMap<>();
    row.put("titleId", "1");
    row.put("clusterId", java.math.BigInteger.valueOf(10L));
    row.put("composedId", " 10-4 ");
    row.put("activityId", "41");
    row.put("activityActive", "1");
    row.put("activityDescription", " Seed systems ");
    row.put("phaseId", String.valueOf(CURRENT_PHASE));
    row.put("phaseName", "AR");
    row.put("phaseYear", "2023");
    row.put("clusterTitle", " Cluster Ten ");

    ActivityTitleRelation relation = CrpActivityAction.groupRelations(Arrays.asList(row), CURRENT_PHASE).get(1L).get(0);
    assertEquals(10L, relation.getClusterId());
    assertEquals("10-4", relation.getComposedId());
    assertEquals("Seed systems", relation.getActivityDescription());
    assertEquals("Cluster Ten", relation.getClusterTitle());
    assertTrue(relation.isReportedInCurrentPhase());
  }
}
