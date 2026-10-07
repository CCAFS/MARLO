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
import org.cgiar.ccafs.marlo.data.model.Activity;
import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.data.model.DeliverableActivity;
import org.cgiar.ccafs.marlo.data.model.Phase;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Replication of a deliverable-activity link saved from the deliverable page (A2-2614). Once the composed-id lookup
 * found the activity's copy in each later phase, the link was created on that copy but still looked up on the
 * activity of the saved phase, so every save of the deliverable added it again. The link is now looked up, created
 * and deactivated on the copy; a replica that older saves attached to the saved phase's activity is deactivated too.
 */
public class DeliverableActivityManagerImplReplicationTest {

  private static final String COMPOSED_ID = "102076-23882";
  private static final long DELIVERABLE_ID = 26977L;

  /** Every link the DAO holds, as the stub's table. */
  private final List<DeliverableActivity> links = new ArrayList<>();
  private final Map<Long, Phase> phases = new HashMap<>();
  /** The activity copy of each phase, by phase id. */
  private final Map<Long, Activity> copies = new HashMap<>();
  private long nextId = 100L;

  private DeliverableActivityManagerImpl manager;
  private Phase awpb;
  private Phase progress;
  private Phase report;
  private Activity awpbActivity;

  private Phase phase(long id, String description, Phase next) {
    Phase phase = new Phase();
    phase.setId(id);
    phase.setDescription(description);
    phase.setNext(next);
    phases.put(id, phase);
    return phase;
  }

  private Activity activity(long id, Phase phase) {
    Activity activity = new Activity();
    activity.setId(id);
    activity.setComposeID(COMPOSED_ID);
    activity.setPhase(phase);
    return activity;
  }

  private DeliverableActivity link(Activity activity, Phase phase) {
    Deliverable deliverable = new Deliverable();
    deliverable.setId(DELIVERABLE_ID);
    DeliverableActivity link = new DeliverableActivity();
    link.setDeliverable(deliverable);
    link.setActivity(activity);
    link.setPhase(phase);
    link.setActive(true);
    return link;
  }

  private DeliverableActivity stored(Activity activity, Phase phase) {
    DeliverableActivity link = this.link(activity, phase);
    link.setId(nextId++);
    links.add(link);
    return link;
  }

  private List<DeliverableActivity> activeIn(Phase phase) {
    return links.stream().filter(l -> l.isActive() && l.getPhase().getId().equals(phase.getId()))
      .collect(Collectors.toList());
  }

  @SuppressWarnings("unchecked")
  private <T> T stub(Class<T> type, java.util.function.BiFunction<String, Object[], Object> answer) {
    return (T) Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[] {type},
      (proxy, method, args) -> answer.apply(method.getName(), args));
  }

  @Before
  public void setUp() {
    report = this.phase(431L, "Reporting", null);
    progress = this.phase(430L, "Planning", report);
    awpb = this.phase(429L, "Planning", progress);
    awpbActivity = this.activity(24292L, awpb);
    copies.put(430L, this.activity(24304L, progress));
    copies.put(431L, this.activity(29278L, report));

    DeliverableActivityDAO linkDAO = this.stub(DeliverableActivityDAO.class, (name, args) -> {
      if ("save".equals(name)) {
        DeliverableActivity link = (DeliverableActivity) args[0];
        if (link.getId() == null) {
          link.setId(nextId++);
          link.setActive(true);
          links.add(link);
        }
        return link;
      }
      if ("find".equals(name)) {
        return links.stream().filter(l -> l.getId().equals(args[0])).findFirst().orElse(null);
      }
      if ("deleteDeliverableActivity".equals(name)) {
        links.stream().filter(l -> l.getId().equals(args[0])).forEach(l -> l.setActive(false));
        return null;
      }
      if ("getDeliverableActivitiesByDeliverableIDActivityAndPhase".equals(name)) {
        return links.stream()
          .filter(l -> l.isActive() && l.getDeliverable().getId().equals(args[0])
            && l.getActivity().getId().equals(args[1]) && l.getPhase().getId().equals(args[2]))
          .collect(Collectors.toList());
      }
      return null;
    });
    PhaseDAO phaseDAO = this.stub(PhaseDAO.class, (name, args) -> "find".equals(name) ? phases.get(args[0]) : null);
    ActivityDAO activityDAO = this.stub(ActivityDAO.class, (name, args) -> {
      if ("getActivitiesByComposedID".equals(name)) {
        Activity copy = COMPOSED_ID.equals(args[0]) ? copies.get(args[1]) : null;
        return copy == null ? Collections.emptyList() : Collections.singletonList(copy);
      }
      return null;
    });
    manager = new DeliverableActivityManagerImpl(linkDAO, phaseDAO, activityDAO);
  }

  @Test
  public void eachLaterPhaseGetsTheLinkOnItsOwnCopyOnce() {
    manager.saveDeliverableActivity(this.link(awpbActivity, awpb));
    manager.saveDeliverableActivity(this.activeIn(awpb).get(0));

    assertEquals("saving twice adds nothing the second time", 1, this.activeIn(progress).size());
    assertEquals(Long.valueOf(24304L), this.activeIn(progress).get(0).getActivity().getId());
    assertEquals(1, this.activeIn(report).size());
    assertEquals(Long.valueOf(29278L), this.activeIn(report).get(0).getActivity().getId());
  }

  @Test
  public void deletingDeactivatesTheCopyLinkAndAnOlderReplicaOnTheSourceActivity() {
    DeliverableActivity source = this.stored(awpbActivity, awpb);
    this.stored(copies.get(430L), progress);
    DeliverableActivity olderReplica = this.stored(awpbActivity, progress);
    this.stored(copies.get(431L), report);

    manager.deleteDeliverableActivity(source.getId());

    assertFalse(source.isActive());
    assertFalse(olderReplica.isActive());
    assertEquals("nothing stays active in a later phase", 0,
      this.activeIn(progress).size() + this.activeIn(report).size());
  }

  @Test
  public void aPhaseWithoutACopyKeepsTheSourceActivity() {
    copies.remove(430L);

    manager.saveDeliverableActivity(this.link(awpbActivity, awpb));

    assertEquals(Long.valueOf(24292L), this.activeIn(progress).get(0).getActivity().getId());
    assertEquals(Long.valueOf(29278L), this.activeIn(report).get(0).getActivity().getId());
  }
}
