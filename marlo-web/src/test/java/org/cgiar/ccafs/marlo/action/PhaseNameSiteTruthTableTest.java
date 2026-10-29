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

import org.cgiar.ccafs.marlo.action.annualReport.y2018.OutcomesMilestonesAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.CrpMilestoneManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyInfoManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationInfoManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPolicyInfoManager;
import org.cgiar.ccafs.marlo.data.model.CrpMilestone;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudy;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyInfo;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovation;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationInfo;
import org.cgiar.ccafs.marlo.data.model.ProjectMilestone;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicy;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicyInfo;
import org.cgiar.ccafs.marlo.validation.projects.ProjectOutcomeValidator;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Truth tables of the sites that decided a phase's type by comparing its name with a literal
 * (ENH-PHASE-TYPE-UNIFICATION FN-005, NF-005). Every site answers the same on a plan, a progress, a reporting, the
 * empty and a null phase, except that a throw becomes the value of the new expression (INT-3).
 * <p>
 * Each table has two columns of literal expectations, one for the old expression and one for the new one. The old
 * expression is copied into this file with the line it had at commit 9e2a9ed838, and the old column is asserted
 * against that copy, so a wrong literal cannot hide behind the new code. The new column is asserted against the real
 * code wherever it can be reached: the three year methods of {@link BaseAction}, {@code validateProjectMilestone} of
 * {@link ProjectOutcomeValidator} and {@code getNextPOWBMilestone} of {@link OutcomesMilestonesAction} run with their
 * collaborators stood in for by dynamic proxies that check the arguments they receive. The summary sites sit inside
 * report builders that cannot be run here, so they are held by the {@link Phase} answer plus a read of the shipped
 * source.
 * <p>
 * The plan and the progress phase are each tried in both naming families (POWB / AWPB and UpKeep / Progress) because
 * the old expressions read the name.
 */
public class PhaseNameSiteTruthTableTest {

  /** The result of a site expression on one input. */
  private enum Outcome {
    YES, NO, THROWS
  }

  /** The five inputs of NF-005, with the plan and the progress phase in both naming families. */
  private enum Input {

    PLAN_POWB("POWB/Planning/0", "POWB", "Planning", Boolean.FALSE),
    PLAN_AWPB("AWPB/Planning/0", "AWPB", "Planning", Boolean.FALSE),
    PROGRESS_UPKEEP("UpKeep/Planning/1", "UpKeep", "Planning", Boolean.TRUE),
    PROGRESS_PROGRESS("Progress/Planning/1", "Progress", "Planning", Boolean.TRUE),
    REPORTING_AR("AR/Reporting/0", "AR", "Reporting", Boolean.FALSE),
    EMPTY("empty phase", null, "", null),
    NULL_PHASE("null phase", null, null, null);

    private final String label;

    private final String name;

    private final String description;

    private final Boolean upkeep;

    Input(String label, String name, String description, Boolean upkeep) {
      this.label = label;
      this.name = name;
      this.description = description;
      this.upkeep = upkeep;
    }

    /** A new phase of this input for the given year; the empty phase keeps its own year, as the action builds it. */
    private Phase phase(int year) {
      if (this == NULL_PHASE) {
        return null;
      }
      if (this == EMPTY) {
        return new Phase(null, "", -1);
      }
      Phase phase = new Phase(null, this.description, year);
      phase.setName(this.name);
      phase.setUpkeep(this.upkeep);
      return phase;
    }
  }

  private static final Long PHASE_ID = Long.valueOf(7L);

  private static final Long ENTITY_ID = Long.valueOf(5L);

  private static final int YEAR = 2026;

  private static final String SUMMARIES = "src/main/java/org/cgiar/ccafs/marlo/action/summaries/";

  /**
   * BaseAction.java:3966, 3982, 4230, 4243, 4584 and 4597 at 9e2a9ed838: the six year-method filters compared
   * {@code c.getPhase().getName()} with the previous-name constant of the APConstants classes, whose value was "AR".
   */
  private static boolean oldPreviousNameTest(Phase infoPhase) {
    return infoPhase.getName().equals("AR");
  }

  /**
   * ProjectOutcomeValidator.java:191-194 at 9e2a9ed838 ({@code APConstants.POWB_ACRONYM} being "AWPB"): the
   * achieved-value check runs.
   */
  private static boolean oldAchievedValueCheckRuns(Phase actual) {
    return actual != null && actual.getName() != null
      && (!actual.getName().equals("POWB") && !actual.getName().equals("AWPB"));
  }

  /**
   * OutcomesMilestonesAction.java:421-439 at 9e2a9ed838, the whole of {@code getNextPOWBMilestone} with the manager
   * call replaced by the phase id it would receive. The manager takes a primitive {@code long}, hence the unboxing.
   */
  private static String oldNextPowbStop(Phase actual, String stripped) {
    Phase current = actual;
    if (current != null) {
      while (current.getName() != null
        && ((!StringUtils.containsIgnoreCase(current.getName(), "POWB"))
          && (!StringUtils.containsIgnoreCase(current.getName(), "AWPB")))
        && current.getYear() != actual.getYear() + 1) {
        current = current.getNext();
      }
    }
    if (stripped != null && current != null) {
      long phaseId = current.getId();
      return stopOf(Long.valueOf(phaseId));
    }
    return "not called";
  }

  /**
   * FundingSourcesSummaryAction.java:498-499, 697-698 and 1004-1005, ProjectsFundingSourcesSummaryAction.java:611-612
   * at 9e2a9ed838.
   */
  private static boolean oldSummaryProgressTest(Phase selected) {
    return selected.getName().equals("UpKeep") || selected.getName().equals("Progress");
  }

  /** ExpectedDeliverablesSummaryAction.java:352-356 at 9e2a9ed838, the part that reads the phase name. */
  private static boolean oldExpectedDeliverablesProgressTest(Phase selected) {
    return selected != null && ((selected.getName() != null && selected.getName().equals("UpKeep"))
      || (selected.getName() != null && selected.getName().equals("Progress")));
  }

  private static boolean newSummaryProgressTest(Phase selected) {
    return selected.isProgressPhase();
  }

  private static boolean newExpectedDeliverablesProgressTest(Phase selected) {
    return selected != null && selected.isProgressPhase();
  }

  private static Outcome outcomeOf(Supplier<Boolean> expression) {
    try {
      return expression.get().booleanValue() ? Outcome.YES : Outcome.NO;
    } catch (NullPointerException e) {
      return Outcome.THROWS;
    }
  }

  private static String stopOf(Long phaseId) {
    return phaseId.longValue() == 1L ? "start" : "next";
  }

  private static void inject(Object target, String fieldName, Object value) throws ReflectiveOperationException {
    Field field = BaseAction.class.getDeclaredField(fieldName);
    field.setAccessible(true);
    field.set(target, value);
  }

  /** Makes the real getActualPhase() of the action answer the given phase, or null when it is null. */
  private static <A extends BaseAction> A resolveTo(A action, Phase phase) throws ReflectiveOperationException {
    Map<Long, Phase> allPhases = new HashMap<>();
    allPhases.put(PHASE_ID, phase);
    Map<String, Object> session = new HashMap<>();
    session.put(APConstants.ALL_PHASES, allPhases);
    action.setSession(session);
    action.setPhaseID(PHASE_ID);
    if (phase == null) {
      PhaseManager phaseManager = (PhaseManager) Proxy.newProxyInstance(PhaseManager.class.getClassLoader(),
        new Class<?>[] {PhaseManager.class}, (proxy, method, args) -> {
          if ("getPhaseById".equals(method.getName())) {
            assertEquals("the phase id handed to the manager", PHASE_ID, args[0]);
            return null;
          }
          throw new UnsupportedOperationException(method.getName());
        });
      inject(action, "phaseManager", phaseManager);
      assertNull("the stubbed lookup leaves the actual phase null", action.getActualPhase());
    }
    return action;
  }

  private static BaseAction plainAction() {
    return new BaseAction() {

      private static final long serialVersionUID = 1L;
    };
  }

  private static void collect(List<String> mismatches, String what, Outcome expected, Outcome actual) {
    if (expected != actual) {
      mismatches.add(what + " expected:<" + expected + "> but was:<" + actual + ">");
    }
  }

  /** What a table row asserts: the literal for the old expression and the literal for the new one. */
  private static void assertRows(String site, Outcome[] oldLiterals, Outcome[] newLiterals, RowRunner oldSide,
    RowRunner newSide) throws ReflectiveOperationException {
    Input[] inputs = Input.values();
    assertEquals(site + " old literals", inputs.length, oldLiterals.length);
    assertEquals(site + " new literals", inputs.length, newLiterals.length);
    // Every row is run, so that a wrong expression reports all the inputs it gets wrong and not just the first one.
    List<String> mismatches = new ArrayList<>();
    for (int i = 0; i < inputs.length; i++) {
      Input input = inputs[i];
      collect(mismatches, site + " OLD expression on " + input.label, oldLiterals[i], oldSide.run(input));
      collect(mismatches, site + " NEW expression on " + input.label, newLiterals[i], newSide.run(input));
      if (oldLiterals[i] != Outcome.THROWS) {
        collect(mismatches, site + " is unchanged on " + input.label, oldLiterals[i], newLiterals[i]);
      }
    }
    if (!mismatches.isEmpty()) {
      fail(String.join("\n", mismatches));
    }
  }

  private interface RowRunner {

    Outcome run(Input input) throws ReflectiveOperationException;
  }

  // ---------------------------------------------------------------------------------------------------------------
  // BaseAction year methods: the six sites that compared the info's phase name with "AR".
  // ---------------------------------------------------------------------------------------------------------------

  /** An info of the entity under test, as the year methods read it. */
  private static final class InfoSpec {

    private final Phase phase;

    private final long year;

    private InfoSpec(Phase phase, long year) {
      this.phase = phase;
      this.year = year;
    }
  }

  /** One of the three year methods, driven through its real code. */
  private interface YearMethod {

    List<Integer> years(BaseAction action, List<InfoSpec> infos) throws ReflectiveOperationException;
  }

  private static final YearMethod INNOVATIONS_YEARS = (action, specs) -> {
    List<ProjectInnovationInfo> infos = new ArrayList<>();
    for (InfoSpec spec : specs) {
      ProjectInnovation innovation = new ProjectInnovation();
      innovation.setId(ENTITY_ID);
      ProjectInnovationInfo info = new ProjectInnovationInfo();
      info.setProjectInnovation(innovation);
      info.setPhase(spec.phase);
      info.setYear(Long.valueOf(spec.year));
      infos.add(info);
    }
    inject(action, "projectInnovationInfoManager",
      Proxy.newProxyInstance(ProjectInnovationInfoManager.class.getClassLoader(),
        new Class<?>[] {ProjectInnovationInfoManager.class}, (proxy, method, args) -> {
          if ("findAll".equals(method.getName())) {
            return infos;
          }
          throw new UnsupportedOperationException(method.getName());
        }));
    return action.getInnovationsYears(ENTITY_ID);
  };

  private static final YearMethod POLICIES_YEARS = (action, specs) -> {
    List<ProjectPolicyInfo> infos = new ArrayList<>();
    for (InfoSpec spec : specs) {
      ProjectPolicy policy = new ProjectPolicy();
      policy.setId(ENTITY_ID);
      ProjectPolicyInfo info = new ProjectPolicyInfo();
      info.setProjectPolicy(policy);
      info.setPhase(spec.phase);
      info.setYear(Long.valueOf(spec.year));
      infos.add(info);
    }
    inject(action, "projectPolicyInfoManager",
      Proxy.newProxyInstance(ProjectPolicyInfoManager.class.getClassLoader(),
        new Class<?>[] {ProjectPolicyInfoManager.class}, (proxy, method, args) -> {
          if ("findAll".equals(method.getName())) {
            return infos;
          }
          throw new UnsupportedOperationException(method.getName());
        }));
    return action.getPoliciesYears(ENTITY_ID);
  };

  private static final YearMethod EXPECTED_STUDIES_YEARS = (action, specs) -> {
    List<ProjectExpectedStudyInfo> infos = new ArrayList<>();
    for (InfoSpec spec : specs) {
      ProjectExpectedStudy study = new ProjectExpectedStudy();
      study.setId(ENTITY_ID);
      ProjectExpectedStudyInfo info = new ProjectExpectedStudyInfo();
      info.setProjectExpectedStudy(study);
      info.setPhase(spec.phase);
      info.setYear(Integer.valueOf((int) spec.year));
      infos.add(info);
    }
    inject(action, "projectExpectedStudyInfoManager",
      Proxy.newProxyInstance(ProjectExpectedStudyInfoManager.class.getClassLoader(),
        new Class<?>[] {ProjectExpectedStudyInfoManager.class}, (proxy, method, args) -> {
          if ("findAllByExpectedStudy".equals(method.getName())) {
            assertEquals("the study id handed to the manager", ENTITY_ID, args[0]);
            return infos;
          }
          throw new UnsupportedOperationException(method.getName());
        }));
    return action.getExpectedStudiesYears(ENTITY_ID);
  };

  /**
   * Runs a year method and tells whether the info of {@code marker} year passed the phase filter under test. The
   * actual phase is always a reporting phase of {@link #YEAR}. A throw is told apart from a pass: the methods that
   * let it escape throw a NullPointerException, and {@code getExpectedStudiesYears} swallows it and answers an empty
   * list, which no other path of the three produces because the actual year is always appended.
   */
  private static Outcome yearMethodOutcome(YearMethod method, List<InfoSpec> infos, int marker)
    throws ReflectiveOperationException {
    BaseAction action = resolveTo(plainAction(), Input.REPORTING_AR.phase(YEAR));
    List<Integer> years;
    try {
      years = method.years(action, infos);
    } catch (NullPointerException e) {
      return Outcome.THROWS;
    }
    if (years.isEmpty()) {
      return Outcome.THROWS;
    }
    if (years.equals(Arrays.asList(Integer.valueOf(marker), Integer.valueOf(YEAR)))) {
      return Outcome.YES;
    }
    assertEquals("the info of year " + marker + " did not pass, so only the actual year is left",
      Collections.singletonList(Integer.valueOf(YEAR)), years);
    return Outcome.NO;
  }

  /** The info's phase has the actual year, the info says 2025: it passes the first filter or the year is not added. */
  private static Outcome firstFilterOutcome(YearMethod method, Input input) throws ReflectiveOperationException {
    return yearMethodOutcome(method, Collections.singletonList(new InfoSpec(input.phase(YEAR), 2025L)), 2025);
  }

  /**
   * The first info is a reporting phase of the actual year and carries the actual year, which sends the method to its
   * second filter; the second info's phase is a year older and passes it, adding 2024, or does not.
   */
  private static Outcome secondFilterOutcome(YearMethod method, Input input) throws ReflectiveOperationException {
    InfoSpec actualYearInfo = new InfoSpec(Input.REPORTING_AR.phase(YEAR), (long) YEAR);
    InfoSpec olderInfo = new InfoSpec(input.phase(YEAR - 1), 2024L);
    return yearMethodOutcome(method, Arrays.asList(actualYearInfo, olderInfo), 2024);
  }

  private static final Outcome[] PREVIOUS_NAME_OLD =
    {Outcome.NO, Outcome.NO, Outcome.NO, Outcome.NO, Outcome.YES, Outcome.THROWS, Outcome.THROWS};

  /** Same order as {@link Input}: POWB, AWPB, UpKeep, Progress, AR, empty, null. The empty phase stops throwing. */
  private static final Outcome[] PREVIOUS_NAME_NEW =
    {Outcome.NO, Outcome.NO, Outcome.NO, Outcome.NO, Outcome.YES, Outcome.NO, Outcome.THROWS};

  private static void assertYearSites(String method, YearMethod yearMethod) throws ReflectiveOperationException {
    RowRunner old = input -> outcomeOf(() -> oldPreviousNameTest(input.phase(YEAR)));
    assertRows(method + " first filter", PREVIOUS_NAME_OLD, PREVIOUS_NAME_NEW, old,
      input -> firstFilterOutcome(yearMethod, input));
    assertRows(method + " second filter", PREVIOUS_NAME_OLD, PREVIOUS_NAME_NEW, old,
      input -> secondFilterOutcome(yearMethod, input));
  }

  @Test
  public void getInnovationsYearsPicksTheReportingPhaseByTypeLikeItDidByName() throws ReflectiveOperationException {
    assertYearSites("getInnovationsYears (BaseAction:4230, 4243)", INNOVATIONS_YEARS);
  }

  @Test
  public void getPoliciesYearsPicksTheReportingPhaseByTypeLikeItDidByName() throws ReflectiveOperationException {
    assertYearSites("getPoliciesYears (BaseAction:4584, 4597)", POLICIES_YEARS);
  }

  @Test
  public void getExpectedStudiesYearsPicksTheReportingPhaseByTypeLikeItDidByName()
    throws ReflectiveOperationException {
    assertYearSites("getExpectedStudiesYears (BaseAction:3966, 3982)", EXPECTED_STUDIES_YEARS);
  }

  // ---------------------------------------------------------------------------------------------------------------
  // ProjectOutcomeValidator:191-194, the guarded negation.
  // ---------------------------------------------------------------------------------------------------------------

  private static CrpMilestone crpMilestone(Long id) {
    CrpMilestone crpMilestone = new CrpMilestone();
    crpMilestone.setId(id);
    crpMilestone.setYear(Integer.valueOf(YEAR));
    return crpMilestone;
  }

  /**
   * Runs the real validateProjectMilestone and tells whether the achieved-value check of the site fired. The milestone
   * is valid in every other field and its year differs from the phase year, so that the reporting-only blocks stay
   * out and the only field that can be flagged is the negative achieved value.
   */
  private static Outcome achievedValueCheckOutcome(Input input) throws ReflectiveOperationException {
    Long milestoneId = Long.valueOf(11L);
    CrpMilestoneManager crpMilestoneManager = (CrpMilestoneManager) Proxy.newProxyInstance(
      CrpMilestoneManager.class.getClassLoader(), new Class<?>[] {CrpMilestoneManager.class},
      (proxy, method, args) -> {
        if ("getCrpMilestoneById".equals(method.getName())) {
          assertEquals("the milestone id handed to the manager", milestoneId, args[0]);
          return crpMilestone(milestoneId);
        }
        throw new UnsupportedOperationException(method.getName());
      });
    ProjectOutcomeValidator validator = new ProjectOutcomeValidator(null, null, null, crpMilestoneManager, null);

    // The action reports the cycle year itself: with the real one the empty phase (year -1) and a null phase never
    // reach the site, because the milestone year is compared with the cycle year first.
    BaseAction action = resolveTo(new BaseAction() {

      private static final long serialVersionUID = 1L;

      @Override
      public int getCurrentCycleYear() {
        return YEAR;
      }
    }, input.phase(YEAR));
    action.setInvalidFields(new HashMap<>());

    ProjectMilestone milestone = new ProjectMilestone();
    milestone.setCrpMilestone(crpMilestone(milestoneId));
    milestone.setExpectedValue(Double.valueOf(1D));
    milestone.setAchievedValue(Double.valueOf(-1D));
    milestone.setNarrativeTarget("A narrative target");
    milestone.setYear(YEAR + 4);

    validator.validateProjectMilestone(action, milestone, 0);

    String achievedKey = "input-projectOutcome.milestones[0].achievedValue";
    if (action.getInvalidFields().containsKey(achievedKey)) {
      assertEquals("the achieved value is the only flagged field", Collections.singleton(achievedKey),
        action.getInvalidFields().keySet());
      return Outcome.YES;
    }
    assertEquals("nothing else is flagged", Collections.emptySet(), action.getInvalidFields().keySet());
    return Outcome.NO;
  }

  /** Same order as {@link Input}: POWB, AWPB, UpKeep, Progress, AR, empty, null. */
  private static final Outcome[] ACHIEVED_VALUE_CHECK =
    {Outcome.NO, Outcome.NO, Outcome.YES, Outcome.YES, Outcome.YES, Outcome.NO, Outcome.NO};

  @Test
  public void theAchievedValueCheckOfTheOutcomeValidatorRunsOnProgressAndReportingAndNeverWithoutAPhase()
    throws ReflectiveOperationException {
    assertRows("ProjectOutcomeValidator:191-194", ACHIEVED_VALUE_CHECK, ACHIEVED_VALUE_CHECK,
      input -> outcomeOf(() -> oldAchievedValueCheckRuns(input.phase(YEAR))),
      PhaseNameSiteTruthTableTest::achievedValueCheckOutcome);
  }

  // ---------------------------------------------------------------------------------------------------------------
  // OutcomesMilestonesAction:426-428, the guarded negation inside a loop.
  // ---------------------------------------------------------------------------------------------------------------

  /**
   * Runs the real getNextPOWBMilestone on a chain actual (id 1) then a plan phase (id 2) of the same year, and tells
   * where the loop stopped by the phase id the manager received: the start, the next phase, or no call at all.
   */
  private static String nextPowbStopOutcome(Phase actual) throws ReflectiveOperationException {
    List<Long> phaseIds = new ArrayList<>();
    CrpMilestoneManager crpMilestoneManager = (CrpMilestoneManager) Proxy.newProxyInstance(
      CrpMilestoneManager.class.getClassLoader(), new Class<?>[] {CrpMilestoneManager.class},
      (proxy, method, args) -> {
        if ("getCrpMilestoneByPhase".equals(method.getName())) {
          assertEquals("the milestone composed id handed to the manager", "C-1", args[0]);
          phaseIds.add((Long) args[1]);
          return null;
        }
        throw new UnsupportedOperationException(method.getName());
      });
    OutcomesMilestonesAction action = resolveTo(new OutcomesMilestonesAction(null, null, null, null, null, null, null,
      null, null, crpMilestoneManager, null, null, null, null, null, null, null, null, null), actual);
    try {
      action.getNextPOWBMilestone("C-1");
    } catch (NullPointerException e) {
      return "throws";
    }
    if (phaseIds.isEmpty()) {
      return "not called";
    }
    assertEquals("the manager is called once", 1, phaseIds.size());
    return stopOf(phaseIds.get(0));
  }

  /** The chain the loop walks: the input phase (id 1), then a plan phase of the same year (id 2). */
  private static Phase chainOf(Phase start) {
    if (start != null) {
      start.setId(Long.valueOf(1L));
      Phase plan = Input.PLAN_POWB.phase(YEAR);
      plan.setId(Long.valueOf(2L));
      start.setNext(plan);
    }
    return start;
  }

  @Test
  public void theNextPowbLoopStopsAtTheFirstPhaseThatIsNeitherProgressNorReporting()
    throws ReflectiveOperationException {
    // Same order as Input: POWB, AWPB, UpKeep, Progress, AR, empty (no id: the primitive long parameter throws,
    // before and after), null.
    String[] expected = {"start", "start", "next", "next", "next", "throws", "not called"};
    Input[] inputs = Input.values();
    for (int i = 0; i < inputs.length; i++) {
      String label = "OutcomesMilestonesAction:426-428 on " + inputs[i].label;
      Phase oldChain = inputs[i] == Input.EMPTY ? inputs[i].phase(YEAR) : chainOf(inputs[i].phase(YEAR));
      String oldResult;
      try {
        oldResult = oldNextPowbStop(oldChain, "C-1");
      } catch (NullPointerException e) {
        oldResult = "throws";
      }
      assertEquals(label + " OLD expression", expected[i], oldResult);
      Phase newChain = inputs[i] == Input.EMPTY ? inputs[i].phase(YEAR) : chainOf(inputs[i].phase(YEAR));
      assertEquals(label + " NEW expression", expected[i], nextPowbStopOutcome(newChain));
    }
  }

  @Test
  public void theNextPowbLoopStopsAtAnEmptyPhaseThatCarriesAnId() throws ReflectiveOperationException {
    // The empty phase has no id, which hides the loop behind the unboxing of the id. With an id the loop decision
    // shows: the old loop stopped on the missing name, the new one stops on the missing type.
    Phase oldChain = chainOf(Input.EMPTY.phase(YEAR));
    assertEquals("OLD expression on an empty phase with an id", "start", oldNextPowbStop(oldChain, "C-1"));
    assertEquals("NEW expression on an empty phase with an id", "start",
      nextPowbStopOutcome(chainOf(Input.EMPTY.phase(YEAR))));
  }

  // ---------------------------------------------------------------------------------------------------------------
  // Summary sites: FundingSourcesSummaryAction, ProjectsFundingSourcesSummaryAction, ExpectedDeliverablesSummaryAction.
  // ---------------------------------------------------------------------------------------------------------------

  @Test
  public void theFundingSummarySitesAskTheProgressPhaseInsteadOfTwoNames() throws ReflectiveOperationException {
    // Same order as Input: POWB, AWPB, UpKeep, Progress, AR, empty, null. A missing name stops throwing (INT-3); a
    // null phase still throws, on both sides.
    assertRows("FundingSourcesSummaryAction:498, 697, 1004 and ProjectsFundingSourcesSummaryAction:611",
      new Outcome[] {Outcome.NO, Outcome.NO, Outcome.YES, Outcome.YES, Outcome.NO, Outcome.THROWS, Outcome.THROWS},
      new Outcome[] {Outcome.NO, Outcome.NO, Outcome.YES, Outcome.YES, Outcome.NO, Outcome.NO, Outcome.THROWS},
      input -> outcomeOf(() -> oldSummaryProgressTest(input.phase(YEAR))),
      input -> outcomeOf(() -> newSummaryProgressTest(input.phase(YEAR))));
  }

  @Test
  public void theExpectedDeliverablesSummarySiteKeepsItsNullGuardAndAsksTheProgressPhase()
    throws ReflectiveOperationException {
    Outcome[] literals = {Outcome.NO, Outcome.NO, Outcome.YES, Outcome.YES, Outcome.NO, Outcome.NO, Outcome.NO};
    assertRows("ExpectedDeliverablesSummaryAction:352-356", literals, literals,
      input -> outcomeOf(() -> oldExpectedDeliverablesProgressTest(input.phase(YEAR))),
      input -> outcomeOf(() -> newExpectedDeliverablesProgressTest(input.phase(YEAR))));
  }

  // ---------------------------------------------------------------------------------------------------------------
  // Java call sites of the old type methods (T06): the guarded, the combined and the negated expressions. The old
  // expression is a copy of the old BaseAction methods at 9e2a9ed838; the new one runs the real BaseAction methods.
  // ---------------------------------------------------------------------------------------------------------------

  private static final Outcome Y = Outcome.YES;

  private static final Outcome N = Outcome.NO;

  private static final Outcome T = Outcome.THROWS;

  /** BaseAction.isPlanningActive() at 9e2a9ed838. */
  private static boolean oldPlanningActive(Phase actual) {
    return actual != null && actual.getDescription() != null && actual.getDescription().equals("Planning");
  }

  /** BaseAction.isUpKeepActive() at 9e2a9ed838: throws on a null phase and on a null flag. */
  private static boolean oldUpKeepActive(Phase actual) {
    return actual.getUpkeep();
  }

  /** BaseAction.isProgressActive() at 9e2a9ed838: the throw is caught and answered false. */
  private static boolean oldProgressActive(Phase actual) {
    try {
      return actual.getUpkeep();
    } catch (Exception e) {
      return false;
    }
  }

  /** BaseAction.isReportingActive() at 9e2a9ed838. */
  private static boolean oldReportingActive(Phase actual) {
    return actual != null && actual.getDescription() != null && actual.getDescription().equals("Reporting");
  }

  /** BaseAction.isPOWB() and isAWPBActive() at 9e2a9ed838. */
  private static boolean oldPowbActive(Phase actual) {
    return actual != null && actual.getName() != null
      && (actual.getName().equals("POWB") || actual.getName().equals("AWPB"));
  }

  private static RowRunner oldSide(Function<Phase, Boolean> expression) {
    return input -> outcomeOf(() -> expression.apply(input.phase(YEAR)));
  }

  private static RowRunner newSide(Function<BaseAction, Boolean> expression) {
    return input -> {
      BaseAction action = resolveTo(plainAction(), input.phase(YEAR));
      return outcomeOf(() -> expression.apply(action));
    };
  }

  /** Same order as {@link Input}: POWB, AWPB, UpKeep, Progress, AR, empty, null. */
  @Test
  public void thePlanPhaseCombinationsAskThePlanningPhase() throws ReflectiveOperationException {
    // DeliverableAction:2087, 5594; DeliverableListAction:166, 300; DeliverableStatusAction:76;
    // EditDeliverableInterceptor:350, 370: isPlanningActive() && !isUpKeepActive().
    Outcome[] literals = {Y, Y, N, N, N, N, N};
    assertRows("isPlanningActive() && !isUpKeepActive() (seven sites)", literals, literals,
      oldSide(p -> oldPlanningActive(p) && !oldUpKeepActive(p)), newSide(a -> a.isPlanningPhase()));
    // CanEditPowbSynthesisInterceptor:112: isPlanningActive() && !getActualPhase().getUpkeep().
    assertRows("CanEditPowbSynthesisInterceptor:112", literals, literals,
      oldSide(p -> oldPlanningActive(p) && !p.getUpkeep()), newSide(a -> a.isPlanningPhase()));
  }

  @Test
  public void theProgressCombinationOfTheSectionValidatorAsksTheProgressPhase() throws ReflectiveOperationException {
    // ValidateProjectSectionAction:406: isPlanningActive() && isUpKeepActive().
    Outcome[] literals = {N, N, Y, Y, N, N, N};
    assertRows("ValidateProjectSectionAction:406", literals, literals,
      oldSide(p -> oldPlanningActive(p) && oldUpKeepActive(p)), newSide(a -> a.isProgressPhase()));
  }

  @Test
  public void thePlanningCycleIsThePlanOrTheProgressPhase() throws ReflectiveOperationException {
    // CanEditPowbSynthesisInterceptor:165 and the bare isPlanningActive() sites.
    Outcome[] literals = {Y, Y, Y, Y, N, N, N};
    assertRows("isPlanningActive() (CanEditPowbSynthesisInterceptor:165)", literals, literals,
      oldSide(p -> oldPlanningActive(p)), newSide(a -> a.isPlanningPhase() || a.isProgressPhase()));
  }

  @Test
  public void theNegatedPlanningCycleOfTheSubmissionActionIsTheComplementOfBothTypes()
    throws ReflectiveOperationException {
    // ProjectSubmissionAction:304: !isPlanningActive().
    Outcome[] literals = {N, N, N, N, Y, Y, Y};
    assertRows("ProjectSubmissionAction:304", literals, literals, oldSide(p -> !oldPlanningActive(p)),
      newSide(a -> !(a.isPlanningPhase() || a.isProgressPhase())));
  }

  @Test
  public void theBudgetGateOfTheSectionValidatorKeepsTheCycleAndStopsThrowingWithoutAPhase()
    throws ReflectiveOperationException {
    // ValidateProjectSectionAction:222 with the specificity true: planning || ((reporting || upkeep) && flag).
    assertRows("ValidateProjectSectionAction:222", new Outcome[] {Y, Y, Y, Y, Y, T, T},
      new Outcome[] {Y, Y, Y, Y, Y, N, N},
      oldSide(p -> oldPlanningActive(p) || ((oldReportingActive(p) || oldUpKeepActive(p)))),
      newSide(a -> a.isPlanningPhase() || a.isProgressPhase() || (a.isReportingPhase() || a.isProgressPhase())));
  }

  @Test
  public void theReportingOrProgressSitesStopThrowingWithoutAPhase() throws ReflectiveOperationException {
    Outcome[] oldLiterals = {N, N, Y, Y, Y, T, T};
    Outcome[] newLiterals = {N, N, Y, Y, Y, N, N};
    assertRows("isReportingActive() || isUpKeepActive()", oldLiterals, newLiterals,
      oldSide(p -> oldReportingActive(p) || oldUpKeepActive(p)),
      newSide(a -> a.isReportingPhase() || a.isProgressPhase()));
    assertRows("isUpKeepActive() || isReportingActive()", oldLiterals, newLiterals,
      oldSide(p -> oldUpKeepActive(p) || oldReportingActive(p)),
      newSide(a -> a.isProgressPhase() || a.isReportingPhase()));
  }

  @Test
  public void theNegationsOfTheNullSafeOldMethodsStayBareNegations() throws ReflectiveOperationException {
    // DD-11 rule 1. !isPOWB() / !isAWPBActive() (DeliverableValidator:494, 806, ProjectExpectedStudiesValidator:755).
    Outcome[] notPowb = {N, N, Y, Y, Y, Y, Y};
    assertRows("!isPOWB()", notPowb, notPowb, oldSide(p -> !oldPowbActive(p)), newSide(a -> !a.isPlanningPhase()));
    // !isReportingActive() (FundingSourceAction:219, 302; FundingSourceListAction:366; ProjectOutcomeValidator:200;
    // ProjectActivitiesValidator:157).
    Outcome[] notReporting = {Y, Y, Y, Y, N, Y, Y};
    assertRows("!isReportingActive()", notReporting, notReporting, oldSide(p -> !oldReportingActive(p)),
      newSide(a -> !a.isReportingPhase()));
    // !isProgressActive() (BaseAction:1273, CanDeleteDeliverableWithSharedTrainees:62).
    Outcome[] notProgress = {Y, Y, N, N, Y, Y, Y};
    assertRows("!isProgressActive()", notProgress, notProgress, oldSide(p -> !oldProgressActive(p)),
      newSide(a -> !a.isProgressPhase()));
  }

  @Test
  public void theNegatedUpKeepSiteStopsThrowingWithoutAPhase() throws ReflectiveOperationException {
    // INT-3: EditDeliverableInterceptor:307, !isUpKeepActive().
    assertRows("!isUpKeepActive()", new Outcome[] {Y, Y, N, N, Y, T, T}, new Outcome[] {Y, Y, N, N, Y, Y, Y},
      oldSide(p -> !oldUpKeepActive(p)), newSide(a -> !a.isProgressPhase()));
  }

  private static String sourceOf(String fileName) throws IOException {
    String source = new String(Files.readAllBytes(Paths.get(SUMMARIES + fileName)), StandardCharsets.UTF_8);
    return source.replaceAll("\\s+", " ");
  }

  private static int occurrences(String text, String part) {
    return StringUtils.countMatches(text, part);
  }

  @Test
  public void theShippedSummarySourcesCarryTheTypeQuestionAndNoPhaseNameLiteral() throws IOException {
    String funding = sourceOf("FundingSourcesSummaryAction.java");
    String projectsFunding = sourceOf("ProjectsFundingSourcesSummaryAction.java");
    String expectedDeliverables = sourceOf("ExpectedDeliverablesSummaryAction.java");

    assertEquals("FundingSourcesSummaryAction sites", 3,
      occurrences(funding, "this.getSelectedPhase().isProgressPhase()"));
    assertEquals("ProjectsFundingSourcesSummaryAction site", 1,
      occurrences(projectsFunding, "this.getSelectedPhase().isProgressPhase()"));
    assertEquals("ExpectedDeliverablesSummaryAction site", 1, occurrences(expectedDeliverables,
      "this.getSelectedPhase() != null && this.getSelectedPhase().isProgressPhase()"));
    for (String source : Arrays.asList(funding, projectsFunding, expectedDeliverables)) {
      assertTrue("no UpKeep name literal is left", !source.contains("equals(\"UpKeep\")"));
      assertTrue("no Progress name literal is left", !source.contains("equals(\"Progress\")"));
    }
  }
}
