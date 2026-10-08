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

import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;

import java.util.HashMap;
import java.util.Map;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * getCrpSession(), getCenterSession() and getCenterID() read the Global Unit from the session. A session without one
 * is the normal case on the login page and for anonymous requests: it must leave the value as it was and log nothing.
 * Each of those reads used to throw a NullPointerException, caught and logged as a WARN with a ~225-line stack trace.
 */
public class BaseActionSessionGlobalUnitTest {

  private BaseAction action;

  private ListAppender<ILoggingEvent> appender;

  private Logger logger;

  private Level originalLevel;

  private static GlobalUnit globalUnit() {
    GlobalUnit unit = new GlobalUnit();
    unit.setId(Long.valueOf(45L));
    unit.setAcronym("AICCRA");
    return unit;
  }

  /** A session that holds data, so the methods take their session branch, but no Global Unit. */
  private static Map<String, Object> sessionWithoutGlobalUnit() {
    Map<String, Object> session = new HashMap<>();
    session.put(APConstants.LOGIN_MESSAGE, "any value");
    return session;
  }

  private long warnings() {
    return this.appender.list.stream().filter(event -> event.getLevel() == Level.WARN).count();
  }

  @Before
  public void setUp() {
    this.action = new BaseAction();
    this.logger = (Logger) LoggerFactory.getLogger(BaseAction.class);
    this.originalLevel = this.logger.getLevel();
    this.logger.setLevel(Level.TRACE);
    this.appender = new ListAppender<>();
    this.appender.start();
    this.logger.addAppender(this.appender);
  }

  @After
  public void tearDown() {
    this.logger.detachAppender(this.appender);
    this.logger.setLevel(this.originalLevel);
  }

  @Test
  public void noGlobalUnitInTheSessionLogsNothingAndKeepsTheValues() {
    this.action.setSession(sessionWithoutGlobalUnit());

    assertNull(this.action.getCrpSession());
    assertNull(this.action.getCenterSession());
    assertNull(this.action.getCenterID());
    assertEquals("a session without a Global Unit is not an error", 0, this.warnings());
  }

  /** Same outcome as before the change: a value set earlier is left as it was, not cleared. */
  @Test
  public void noGlobalUnitInTheSessionKeepsAnEarlierCrpSession() {
    this.action.setCrpSession("EARLIER");
    this.action.setSession(sessionWithoutGlobalUnit());

    assertEquals("EARLIER", this.action.getCrpSession());
    assertEquals(0, this.warnings());
  }

  @Test
  public void aGlobalUnitInTheSessionIsRead() {
    Map<String, Object> session = sessionWithoutGlobalUnit();
    session.put(APConstants.SESSION_CRP, globalUnit());
    this.action.setSession(session);

    assertEquals("AICCRA", this.action.getCrpSession());
    assertEquals("AICCRA", this.action.getCenterSession());
    assertEquals(Long.valueOf(45L), this.action.getCenterID());
    assertEquals(0, this.warnings());
  }

  /** A value that is not a Global Unit is a real anomaly: it is still reported, once per read. */
  @Test
  public void somethingElseUnderTheGlobalUnitKeyIsStillReported() {
    Map<String, Object> session = sessionWithoutGlobalUnit();
    session.put(APConstants.SESSION_CRP, "not a Global Unit");
    this.action.setSession(session);

    this.action.getCrpSession();
    this.action.getCenterSession();
    this.action.getCenterID();
    assertEquals(3, this.warnings());
  }
}
