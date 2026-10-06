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
import org.cgiar.ccafs.marlo.data.manager.DeliverableActivityManager;
import org.cgiar.ccafs.marlo.data.model.Activity;
import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.data.model.DeliverableActivity;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.Project;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * Forward replication of a cluster activity into a later phase (A2-2614). The deliverable links were matched by
 * entity although the ones bound from the request carry no id, so every save deactivated every link and created it
 * again. These tests pin the reconciliation by deliverable: only the links that really changed are touched. The
 * lookup is stubbed here; the composed-id lookup that never matches in the DAO is tracked in
 * docs/specs/bugfix/activities-save-performance, because fixing it changes what a Planning save writes into an
 * open Reporting phase.
 */
public class ActivityManagerImplReplicationTest {

  private static final long PROJECT_ID = 102076L;
  private static final String COMPOSED_ID = "102076-23882";

  private final List<Object> savedActivities = new ArrayList<>();
  private final List<Long> deletedLinks = new ArrayList<>();
  private final List<Long> savedLinkDeliverables = new ArrayList<>();
  private List<Activity> copiesInNextPhase = new ArrayList<>();

  private ActivityManagerImpl manager;
  private Phase nextPhase;

  private DeliverableActivity link(long id, long deliverableId, boolean active) {
    DeliverableActivity link = new DeliverableActivity();
    link.setId(id);
    link.setDeliverable(this.deliverable(deliverableId));
    link.setActive(active);
    return link;
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
    PhaseDAO phaseDAO = this.stub(PhaseDAO.class, (name, args) -> "find".equals(name) ? nextPhase : null);
    ProjectDAO projectDAO = this.stub(ProjectDAO.class, (name, args) -> {
      if ("find".equals(name)) {
        Project project = new Project();
        project.setId((Long) args[0]);
        return project;
      }
      return null;
    });
    DeliverableActivityManager linkManager = this.stub(DeliverableActivityManager.class, (name, args) -> {
      if ("deleteDeliverableActivity".equals(name)) {
        deletedLinks.add((Long) args[0]);
      } else if ("saveDeliverableActivity".equals(name)) {
        savedLinkDeliverables.add(((DeliverableActivity) args[0]).getDeliverable().getId());
        return args[0];
      }
      return null;
    });
    manager = new ActivityManagerImpl(activityDAO, phaseDAO, projectDAO,
      this.stub(DeliverableActivityDAO.class, (name, args) -> null),
      this.stub(ProjectPartnerPersonDAO.class, (name, args) -> null), linkManager);
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

    assertTrue("no link is deactivated: " + deletedLinks, deletedLinks.isEmpty());
    assertTrue("no link is created: " + savedLinkDeliverables, savedLinkDeliverables.isEmpty());
  }

  @Test
  public void onlyTheRemovedLinkIsDeactivatedAndOnlyTheNewOneCreated() {
    copiesInNextPhase = new ArrayList<>(Collections.singletonList(
      this.existingCopy(this.link(1L, 24406L, true), this.link(2L, 24407L, true), this.link(3L, 24428L, false))));

    manager.saveActvityPhase(nextPhase, PROJECT_ID,
      this.sourceActivity(this.requestLink(24406L), this.requestLink(24428L)));

    assertEquals("the link to the removed deliverable", Collections.singletonList(2L), deletedLinks);
    assertEquals("an inactive link does not count as stored", Collections.singletonList(24428L),
      savedLinkDeliverables);
  }

  @Test
  public void aDeliverableListedTwiceGetsOneLink() {
    copiesInNextPhase = new ArrayList<>(Collections.singletonList(this.existingCopy()));

    manager.saveActvityPhase(nextPhase, PROJECT_ID,
      this.sourceActivity(this.requestLink(24406L), this.requestLink(24406L)));

    assertEquals(Collections.singletonList(24406L), savedLinkDeliverables);
  }

  @Test
  public void noDeliverablesInTheRequestDeactivatesTheStoredLinks() {
    copiesInNextPhase = new ArrayList<>(
      Collections.singletonList(this.existingCopy(this.link(1L, 24406L, true), this.link(2L, 24407L, false))));

    manager.saveActvityPhase(nextPhase, PROJECT_ID, this.sourceActivity((DeliverableActivity[]) null));

    assertEquals("only the active link", Collections.singletonList(1L), deletedLinks);
    assertTrue(savedLinkDeliverables.isEmpty());
  }

  @Test
  public void aPhaseWithoutACopyGetsANewOne() {
    copiesInNextPhase = new ArrayList<>();

    manager.saveActvityPhase(nextPhase, PROJECT_ID, this.sourceActivity(this.requestLink(24406L)));

    assertEquals("the new copy, saved once", 1, savedActivities.size());
    Activity created = (Activity) savedActivities.get(0);
    assertEquals(COMPOSED_ID, created.getComposeID());
    assertSame(nextPhase, created.getPhase());
    assertEquals(Collections.singletonList(24406L), savedLinkDeliverables);
  }
}
