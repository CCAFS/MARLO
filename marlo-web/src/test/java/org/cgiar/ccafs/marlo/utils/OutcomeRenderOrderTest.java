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

package org.cgiar.ccafs.marlo.utils;

import org.cgiar.ccafs.marlo.data.model.CrpMilestone;
import org.cgiar.ccafs.marlo.data.model.CrpProgramOutcome;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * A2-2469: OutcomeValidator reports every gap against the position of its indicator in the list it
 * was handed — "outcomesForm[3].srfTargetUnit.id" — and the front end resolves that key against the
 * card sitting in that position on screen. The two lists therefore have to be ordered identically.
 * <p>
 * They were not. ValidateSectionStatusImpactPathway filled its list straight from
 * CrpProgram.getCrpProgramOutcomes(), a {@link HashSet}, while OutcomesAction.loadInfo() sorted the
 * rendered one. Neither CrpProgramOutcome nor CrpMilestone overrides hashCode, so the iteration
 * order of those sets follows identity hashes and is not stable between requests: findings landed
 * on whichever card or cell happened to occupy that index, and moved around between two clicks of
 * the same button.
 * <p>
 * These tests pin the two orders that fix it, so a future caller that forgets to sort is caught
 * here rather than by a user watching an error land on a field that was never empty.
 */
public class OutcomeRenderOrderTest {

  private CrpMilestone milestone(long id, Integer year, String composeId) {
    CrpMilestone milestone = new CrpMilestone();
    milestone.setId(id);
    milestone.setYear(year);
    milestone.setComposeID(composeId);
    return milestone;
  }

  private CrpProgramOutcome outcome(long id, String description) {
    CrpProgramOutcome outcome = new CrpProgramOutcome();
    outcome.setId(id);
    outcome.setDescription(description);
    return outcome;
  }

  /** The deprecated ones sink to the bottom whatever their id. */
  @Test
  public void deprecatedIndicatorsComeLast() {
    List<CrpProgramOutcome> outcomes = new ArrayList<>(Arrays.asList(
      this.outcome(7529L, "(DEPRECATED) IPI 3.1: Validated climate information services"),
      this.outcome(8105L, "(2024-2025) IPI 3.1: Validated climate information services"),
      this.outcome(7517L, "(DEPRECATED) IPI 3.2: Climate information"),
      this.outcome(8126L, "(2024-2025) IPI 3.2: Gender-smart")));

    outcomes.sort(OutcomeComparators.renderOrder());

    assertEquals("the two live indicators come first, by id, then the deprecated ones by id",
      Arrays.asList(8105L, 8126L, 7517L, 7529L),
      outcomes.stream().map(CrpProgramOutcome::getId).collect(Collectors.toList()));
  }

  /** "deprecated" is matched whatever its casing, the way loadInfo() matched it. */
  @Test
  public void deprecatedIsMatchedCaseInsensitively() {
    List<CrpProgramOutcome> outcomes = new ArrayList<>(Arrays.asList(
      this.outcome(1L, "(Deprecated) lower case marker"),
      this.outcome(2L, "A live indicator")));

    outcomes.sort(OutcomeComparators.renderOrder());

    assertEquals("the deprecated one sinks even though the marker is not upper case",
      Arrays.asList(2L, 1L),
      outcomes.stream().map(CrpProgramOutcome::getId).collect(Collectors.toList()));
  }

  /** An indicator with no statement is not deprecated, and must not blow up the sort. */
  @Test
  public void anIndicatorWithNoStatementIsNotDeprecated() {
    List<CrpProgramOutcome> outcomes = new ArrayList<>(Arrays.asList(
      this.outcome(9L, "(DEPRECATED) something"),
      this.outcome(5L, null)));

    outcomes.sort(OutcomeComparators.renderOrder());

    assertEquals("a null statement sorts as live", Arrays.asList(5L, 9L),
      outcomes.stream().map(CrpProgramOutcome::getId).collect(Collectors.toList()));
  }

  /**
   * The point of the fix: the same set, however the HashSet happens to iterate it, always reaches
   * the validator in the order the form renders.
   */
  @Test
  public void aHashSetIsOrderedTheSameWayTheFormRendersIt() {
    Set<CrpProgramOutcome> stored = new HashSet<>(Arrays.asList(
      this.outcome(8168L, "(2024-2025) IPI 3.4"), this.outcome(7565L, "(DEPRECATED) IPI 3.5"),
      this.outcome(8105L, "(2024-2025) IPI 3.1"), this.outcome(8252L, "(DEPRECATED) IPI 3.6")));

    List<CrpProgramOutcome> ordered =
      stored.stream().sorted(OutcomeComparators.renderOrder()).collect(Collectors.toList());

    assertEquals("live by id, then deprecated by id", Arrays.asList(8105L, 8168L, 7565L, 8252L),
      ordered.stream().map(CrpProgramOutcome::getId).collect(Collectors.toList()));
  }

  /** Period targets are numbered by year, which is the order the matrix lays its columns out in. */
  @Test
  public void periodTargetsAreOrderedByYear() {
    List<CrpMilestone> milestones = new ArrayList<>(Arrays.asList(
      this.milestone(41940L, 2027, "c"), this.milestone(41813L, 2024, "a"),
      this.milestone(41839L, 2026, "b")));

    milestones.sort(MilestoneComparators.renderOrder());

    assertEquals("ascending by year", Arrays.asList(2024, 2026, 2027),
      milestones.stream().map(CrpMilestone::getYear).collect(Collectors.toList()));
  }

  /** Within one year the composed id breaks the tie, so rows keep a stable order across requests. */
  @Test
  public void periodTargetsOfOneYearAreOrderedByComposedId() {
    List<CrpMilestone> milestones = new ArrayList<>(Arrays.asList(
      this.milestone(3L, 2026, "c"), this.milestone(1L, 2026, "a"), this.milestone(2L, 2026, "b")));

    milestones.sort(MilestoneComparators.renderOrder());

    assertEquals("ascending by composed id when the year ties", Arrays.asList(1L, 2L, 3L),
      milestones.stream().map(CrpMilestone::getId).collect(Collectors.toList()));
  }
}
