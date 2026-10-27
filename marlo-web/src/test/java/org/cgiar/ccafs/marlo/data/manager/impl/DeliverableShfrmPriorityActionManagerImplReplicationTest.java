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

import org.cgiar.ccafs.marlo.data.dao.DeliverableShfrmPriorityActionDAO;
import org.cgiar.ccafs.marlo.data.dao.PhaseDAO;
import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.data.model.DeliverableShfrmPriorityAction;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ShfrmPriorityAction;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

/**
 * Forward replication of a deliverable's SHFRM priority action into the next phase. Looking for the action in that
 * phase threw in the normal case of not finding it: the DAO answers null when the phase has no action, and get(0) on
 * an empty match threw when it has others. The catch left the result null, so a new action was created as it should
 * be, but every replication logged an error, now with its stack trace. These tests pin that each case saves the same
 * row as before and logs no error.
 */
public class DeliverableShfrmPriorityActionManagerImplReplicationTest {

  private static final long DELIVERABLE_ID = 7001L;
  private static final long NEXT_PHASE_ID = 432L;

  private final List<DeliverableShfrmPriorityAction> saved = new ArrayList<>();
  private final ListAppender<ILoggingEvent> logEvents = new ListAppender<>();

  private Logger managerLogger;
  private List<DeliverableShfrmPriorityAction> actionsInNextPhase;
  private Phase nextPhase;
  private DeliverableShfrmPriorityActionManagerImpl manager;

  private DeliverableShfrmPriorityAction action(Long id, long priorityActionId) {
    ShfrmPriorityAction priorityAction = new ShfrmPriorityAction();
    priorityAction.setId(priorityActionId);
    DeliverableShfrmPriorityAction action = new DeliverableShfrmPriorityAction();
    action.setId(id);
    action.setShfrmPriorityAction(priorityAction);
    return action;
  }

  private void assertNoErrorLogged() {
    for (ILoggingEvent event : logEvents.list) {
      assertFalse("Unexpected error logged: " + event.getFormattedMessage(),
        event.getLevel().isGreaterOrEqual(Level.ERROR));
    }
  }

  /** The action saved in the phase being replicated: the deliverable and priority action the user chose. */
  private DeliverableShfrmPriorityAction source() {
    DeliverableShfrmPriorityAction source = this.action(9001L, 12L);
    Deliverable deliverable = new Deliverable();
    deliverable.setId(DELIVERABLE_ID);
    source.setDeliverable(deliverable);
    return source;
  }

  @SuppressWarnings("unchecked")
  private <T> T stub(Class<T> type, BiFunction<String, Object[], Object> answer) {
    return (T) Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[] {type},
      (proxy, method, args) -> answer.apply(method.getName(), args));
  }

  @Before
  public void setUp() {
    nextPhase = new Phase();
    nextPhase.setId(NEXT_PHASE_ID);

    DeliverableShfrmPriorityActionDAO dao = this.stub(DeliverableShfrmPriorityActionDAO.class, (name, args) -> {
      if ("findByDeliverableAndPhase".equals(name)) {
        assertEquals(DELIVERABLE_ID, args[0]);
        assertEquals(NEXT_PHASE_ID, args[1]);
        return actionsInNextPhase;
      }
      if ("save".equals(name)) {
        saved.add((DeliverableShfrmPriorityAction) args[0]);
        return args[0];
      }
      return null;
    });
    PhaseDAO phaseDAO = this.stub(PhaseDAO.class, (name, args) -> "find".equals(name) ? nextPhase : null);
    manager = new DeliverableShfrmPriorityActionManagerImpl(dao, phaseDAO);

    managerLogger = (Logger) LoggerFactory.getLogger(DeliverableShfrmPriorityActionManagerImpl.class);
    logEvents.start();
    managerLogger.addAppender(logEvents);
  }

  @After
  public void tearDown() {
    managerLogger.detachAppender(logEvents);
  }

  @Test
  public void testAPhaseWithoutAnyActionGetsANewOneWithoutLoggingAnError() {
    actionsInNextPhase = null;
    DeliverableShfrmPriorityAction source = this.source();

    manager.saveDeliverableShfrmPriorityActionPhase(nextPhase, DELIVERABLE_ID, source);

    assertEquals(1, saved.size());
    assertNull("a new row, not an update", saved.get(0).getId());
    assertSame(nextPhase, saved.get(0).getPhase());
    assertSame(source.getShfrmPriorityAction(), saved.get(0).getShfrmPriorityAction());
    assertSame(source.getDeliverable(), saved.get(0).getDeliverable());
    this.assertNoErrorLogged();
  }

  @Test
  public void testAPhaseWithOtherActionsOnlyGetsANewOneWithoutLoggingAnError() {
    DeliverableShfrmPriorityAction other = this.action(501L, 99L);
    actionsInNextPhase = new ArrayList<>(Arrays.asList(other));

    manager.saveDeliverableShfrmPriorityActionPhase(nextPhase, DELIVERABLE_ID, this.source());

    assertEquals(1, saved.size());
    assertNotSame(other, saved.get(0));
    assertNull("a new row, not an update", saved.get(0).getId());
    this.assertNoErrorLogged();
  }

  @Test
  public void testAPhaseThatHasTheActionUpdatesThatRow() {
    DeliverableShfrmPriorityAction other = this.action(501L, 99L);
    DeliverableShfrmPriorityAction existing = this.action(502L, 12L);
    actionsInNextPhase = new ArrayList<>(Arrays.asList(other, existing));
    DeliverableShfrmPriorityAction source = this.source();

    manager.saveDeliverableShfrmPriorityActionPhase(nextPhase, DELIVERABLE_ID, source);

    assertEquals(1, saved.size());
    assertSame("the row already in that phase is the one updated", existing, saved.get(0));
    assertSame(nextPhase, existing.getPhase());
    assertSame(source.getDeliverable(), existing.getDeliverable());
    this.assertNoErrorLogged();
  }
}
