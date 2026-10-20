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

package org.cgiar.ccafs.marlo.data.manager.impl;

import org.cgiar.ccafs.marlo.data.dao.ActivityDAO;
import org.cgiar.ccafs.marlo.data.dao.DeliverableActivityDAO;
import org.cgiar.ccafs.marlo.data.dao.PhaseDAO;
import org.cgiar.ccafs.marlo.data.dao.ProjectDAO;
import org.cgiar.ccafs.marlo.data.dao.ProjectPartnerPersonDAO;
import org.cgiar.ccafs.marlo.data.model.Activity;
import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.data.model.DeliverableActivity;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.Project;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * Forward replication of a cluster activity into a later phase (A2-2614). The deliverable links were matched by
 * entity although the ones bound from the request carry no id, so every save deactivated every link and created it
 * again. These tests pin the reconciliation by deliverable: only the links that really changed are touched, repeated
 * links to one deliverable in the saved phase are reduced to the oldest, and links of other phases are left alone.
 * Every link is written through the DAO, one row each: the phase walk already gives each later copy its own links,
 * and DeliverableActivityManager replicated every link once more into those phases, attached to the activity of the
 * phase being saved. The lookup is stubbed here; the composed-id lookup that never matches in the DAO is tracked in
 * docs/specs/bugfix/activities-save-performance, because fixing it changes what a Planning save writes into an
 * open Reporting phase.
 */
public class ActivityManagerImplReplicationTest {

  private static final long PROJECT_ID = 102076L;
  private static final String COMPOSED_ID = "102076-23882";

  private final List<Object> savedActivities = new ArrayList<>();
  /** Links written through DeliverableActivityDAO: one row each. */
  private final List<Long> daoDeleted = new ArrayList<>();
  private final List<Long> daoCreated = new ArrayList<>();
  /** The phase of each created link, in the same order as daoCreated. */
  private final List<Long> daoCreatedPhases = new ArrayList<>();
  private final Map<Long, Phase> phases = new HashMap<>();
  private List<Activity> copiesInNextPhase = new ArrayList<>();

  private ActivityManagerImpl manager;
  private Phase nextPhase;

  /** A stored link in the phase being saved. */
  private DeliverableActivity link(long id, long deliverableId, boolean active) {
    return this.link(id, deliverableId, active, nextPhase);
  }

  private DeliverableActivity link(long id, long deliverableId, boolean active, Phase phase) {
    DeliverableActivity link = new DeliverableActivity();
    link.setId(id);
    link.setDeliverable(this.deliverable(deliverableId));
    link.setActive(active);
    link.setPhase(phase);
    return link;
  }

  private Phase phase(long id) {
    Phase phase = new Phase();
    phase.setId(id);
    return phase;
  }

  private Deliverable deliverable(long id) {
    Deliverable deliverable = new Deliverable();
    deliverable.setId(id);
    return deliverable;
  }

  /** A link as bindDeliverablesForActivity builds it from the request: no id. */
  private DeliverableActivity requestLink(long deliverableId) {
    DeliverableActivity link = new DeliverableActivity();
    link.setDeliverable(this.deliverable(deliverableId));
    return link;
  }

  private Activity sourceActivity(DeliverableActivity... requestLinks) {
    Project project = new Project();
    project.setId(PROJECT_ID);
    Activity activity = new Activity();
    activity.setId(29278L);
    activity.setComposeID(COMPOSED_ID);
    activity.setProject(project);
    activity.setTitle("1.2.2 Strengthening digital climate advisory services");
    activity.setDeliverables(requestLinks == null ? null : new ArrayList<>(Arrays.asList(requestLinks)));
    return activity;
  }

  private Activity existingCopy(DeliverableActivity... storedLinks) {
    Activity copy = new Activity();
    copy.setId(42739L);
    copy.setComposeID(COMPOSED_ID);
    copy.getDeliverableActivities().addAll(Arrays.asList(storedLinks));
    return copy;
  }

  @SuppressWarnings("unchecked")
  private <T> T stub(Class<T> type, java.util.function.BiFunction<String, Object[], Object> answer) {
    return (T) Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[] {type},
      (proxy, method, args) -> answer.apply(method.getName(), args));
  }

  @Before
  public void setUp() {
    nextPhase = new Phase();
    nextPhase.setId(433L);
    phases.put(nextPhase.getId(), nextPhase);

    ActivityDAO activityDAO = this.stub(ActivityDAO.class, (name, args) -> {
      if ("getActivitiesByComposedIDPhaseIDProjectID".equals(name)) {
        assertEquals(COMPOSED_ID, args[0]);
        return copiesInNextPhase;
      }
      if ("save".equals(name)) {
        savedActivities.add(args[0]);
        return args[0];
      }
      return null;
    });
    PhaseDAO phaseDAO = this.stub(PhaseDAO.class, (name, args) -> "find".equals(name) ? phases.get(args[0]) : null);
    ProjectDAO projectDAO = this.stub(ProjectDAO.class, (name, args) -> {
      if ("find".equals(name)) {
        Project project = new Project();
        project.setId((Long) args[0]);
        return project;
      }
      return null;
    });
    DeliverableActivityDAO linkDAO = this.stub(DeliverableActivityDAO.class, (name, args) -> {
      if ("deleteDeliverableActivity".equals(name)) {
        daoDeleted.add((Long) args[0]);
      } else if ("save".equals(name)) {
        DeliverableActivity created = (DeliverableActivity) args[0];
        daoCreated.add(created.getDeliverable().getId());
        daoCreatedPhases.add(created.getPhase().getId());
        return args[0];
      }
      return null;
    });
    manager = new ActivityManagerImpl(activityDAO, phaseDAO, projectDAO, linkDAO,
      this.stub(ProjectPartnerPersonDAO.class, (name, args) -> null));
  }

  @Test
  public void anExistingCopyIsUpdatedInsteadOfDuplicated() {
    Activity copy = this.existingCopy(this.link(1L, 24406L, true));
    copiesInNextPhase = new ArrayList<>(Collections.singletonList(copy));

    manager.saveActvityPhase(nextPhase, PROJECT_ID, this.sourceActivity(this.requestLink(24406L)));

    assertEquals("only the existing copy is saved", 1, savedActivities.size());
    assertSame(copy, savedActivities.get(0));
    assertEquals("1.2.2 Strengthening digital climate advisory services", copy.getTitle());
  }

  @Test
  public void anUnchangedListOfDeliverablesTouchesNoLink() {
    copiesInNextPhase = new ArrayList<>(
      Collections.singletonList(this.existingCopy(this.link(1L, 24406L, true), this.link(2L, 24407L, true))));

    manager.saveActvityPhase(nextPhase, PROJECT_ID,
      this.sourceActivity(this.requestLink(24406L), this.requestLink(24407L)));

    assertTrue("no link is deactivated: " + daoDeleted, daoDeleted.isEmpty());
    assertTrue("no link is created: " + daoCreated, daoCreated.isEmpty());
  }

  @Test
  public void onlyTheRemovedLinkIsDeactivatedAndOnlyTheNewOneCreated() {
    copiesInNextPhase = new ArrayList<>(Collections.singletonList(
      this.existingCopy(this.link(1L, 24406L, true), this.link(2L, 24407L, true), this.link(3L, 24428L, false))));

    manager.saveActvityPhase(nextPhase, PROJECT_ID,
      this.sourceActivity(this.requestLink(24406L), this.requestLink(24428L)));

    assertEquals("the link to the removed deliverable", Collections.singletonList(2L), daoDeleted);
    assertEquals("an inactive link does not count as stored", Collections.singletonList(24428L), daoCreated);
  }

  @Test
  public void aDeliverableListedTwiceGetsOneLink() {
    copiesInNextPhase = new ArrayList<>(Collections.singletonList(this.existingCopy()));

    manager.saveActvityPhase(nextPhase, PROJECT_ID,
      this.sourceActivity(this.requestLink(24406L), this.requestLink(24406L)));

    assertEquals(Collections.singletonList(24406L), daoCreated);
  }

  @Test
  public void repeatedLinksInThisPhaseKeepOnlyTheOldest() {
    copiesInNextPhase = new ArrayList<>(Collections.singletonList(this.existingCopy(this.link(5L, 24406L, true),
      this.link(3L, 24406L, true), this.link(8L, 24407L, true), this.link(7L, 24407L, true))));

    manager.saveActvityPhase(nextPhase, PROJECT_ID,
      this.sourceActivity(this.requestLink(24406L), this.requestLink(24407L)));

    assertEquals("the newer duplicate of each deliverable", Arrays.asList(5L, 8L), daoDeleted);
    assertTrue("no link is created: " + daoCreated, daoCreated.isEmpty());
  }

  @Test
  public void aListedLinkOfAnotherPhaseIsNotTreatedAsADuplicate() {
    copiesInNextPhase = new ArrayList<>(Collections.singletonList(
      this.existingCopy(this.link(1L, 24406L, true, this.phase(431L)), this.link(2L, 24406L, true))));

    manager.saveActvityPhase(nextPhase, PROJECT_ID, this.sourceActivity(this.requestLink(24406L)));

    assertTrue("a past phase is never written: " + daoDeleted, daoDeleted.isEmpty());
    assertTrue(daoCreated.isEmpty());
  }

  @Test
  public void aLinkOnlyInAnotherPhaseStillGetsOneInThisPhase() {
    copiesInNextPhase = new ArrayList<>(
      Collections.singletonList(this.existingCopy(this.link(1L, 24406L, true, this.phase(431L)))));

    manager.saveActvityPhase(nextPhase, PROJECT_ID, this.sourceActivity(this.requestLink(24406L)));

    assertTrue("the other phase's link is kept: " + daoDeleted, daoDeleted.isEmpty());
    assertEquals(Collections.singletonList(24406L), daoCreated);
  }

  @Test
  public void noDeliverablesInTheRequestDeactivatesTheStoredLinks() {
    copiesInNextPhase = new ArrayList<>(
      Collections.singletonList(this.existingCopy(this.link(1L, 24406L, true), this.link(2L, 24407L, false))));

    manager.saveActvityPhase(nextPhase, PROJECT_ID, this.sourceActivity((DeliverableActivity[]) null));

    assertEquals("only the active link", Collections.singletonList(1L), daoDeleted);
    assertTrue(daoCreated.isEmpty());
  }

  @Test
  public void aPhaseWithoutACopyGetsANewOne() {
    copiesInNextPhase = new ArrayList<>();

    manager.saveActvityPhase(nextPhase, PROJECT_ID, this.sourceActivity(this.requestLink(24406L)));

    assertEquals("the new copy, saved once", 1, savedActivities.size());
    Activity created = (Activity) savedActivities.get(0);
    assertEquals(COMPOSED_ID, created.getComposeID());
    assertSame(nextPhase, created.getPhase());
    assertEquals(Collections.singletonList(24406L), daoCreated);
  }

  @Test
  public void theSavedReportingPhaseWritesOnlyItsOwnLinks() {
    Phase reportingPhase = this.phase(431L);
    reportingPhase.setDescription("Reporting");
    phases.put(reportingPhase.getId(), reportingPhase);
    Activity activity = this.sourceActivity(this.requestLink(24406L));
    activity.setPhase(reportingPhase);
    activity.getDeliverableActivities().add(this.link(9L, 24407L, true, reportingPhase));

    manager.saveActivity(activity);

    assertEquals("the new link of the saved phase", Collections.singletonList(24406L), daoCreated);
    assertEquals(Collections.singletonList(431L), daoCreatedPhases);
    assertEquals("the removed link of the saved phase", Collections.singletonList(9L), daoDeleted);
  }

  @Test
  public void aPlanningSaveGivesTheSavedPhaseAndEachLaterCopyOneLink() {
    Phase planningPhase = this.phase(429L);
    planningPhase.setDescription("Planning");
    planningPhase.setNext(nextPhase);
    phases.put(planningPhase.getId(), planningPhase);
    copiesInNextPhase = new ArrayList<>();
    Activity activity = this.sourceActivity(this.requestLink(24406L));
    activity.setPhase(planningPhase);

    manager.saveActivity(activity);

    assertEquals("one link in the saved phase and one in the copy", Arrays.asList(24406L, 24406L), daoCreated);
    assertEquals("each link in its own phase", Arrays.asList(429L, 433L), daoCreatedPhases);
    assertTrue(daoDeleted.isEmpty());
  }

  private Activity sourceIn(String phaseDescription, String progress) {
    Phase sourcePhase = this.phase(429L);
    sourcePhase.setDescription(phaseDescription);
    Activity activity = this.sourceActivity(this.requestLink(24406L));
    activity.setPhase(sourcePhase);
    activity.setDescription("Planned description");
    activity.setActivityStatus(2);
    activity.setActivityProgress(progress);
    return activity;
  }

  private Activity reportedCopy() {
    Activity copy = this.existingCopy(this.link(1L, 24406L, true));
    copy.setDescription("Reported description");
    copy.setActivityStatus(3);
    copy.setActivityProgress("Reported progress");
    return copy;
  }

  @Test
  public void aPlanningSaveKeepsTheProgressReportedInALaterPhase() {
    Activity copy = this.reportedCopy();
    copiesInNextPhase = new ArrayList<>(Collections.singletonList(copy));

    manager.saveActvityPhase(nextPhase, PROJECT_ID, this.sourceIn("Planning", null));

    assertEquals("the progress is only written by a Reporting save", "Reported progress", copy.getActivityProgress());
    assertEquals("the other fields still follow the Planning save", "Planned description", copy.getDescription());
    assertEquals(Integer.valueOf(2), copy.getActivityStatus());
  }

  @Test
  public void aCopyCreatedByAPlanningSaveStartsWithoutProgress() {
    copiesInNextPhase = new ArrayList<>();

    manager.saveActvityPhase(nextPhase, PROJECT_ID, this.sourceIn("Planning", "Left by an older replication"));

    Activity created = (Activity) savedActivities.get(0);
    assertNull(created.getActivityProgress());
  }

  @Test
  public void aReportingSaveCarriesItsProgressForward() {
    Activity copy = this.reportedCopy();
    copiesInNextPhase = new ArrayList<>(Collections.singletonList(copy));

    manager.saveActvityPhase(nextPhase, PROJECT_ID, this.sourceIn("Reporting", "New progress"));

    assertEquals("New progress", copy.getActivityProgress());
  }
}
