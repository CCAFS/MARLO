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

package org.cgiar.ccafs.marlo.validation;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
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
 * The Global Unit that names an autosave file comes from the session when it is the requested one, so a validation
 * reads no data through a transactional manager. Any other unit, or a call without a session, is still read.
 */
public class BaseValidatorAutoSaveUnitTest {

  private static final long AICCRA_ID = 45L;

  private static final long OTHER_ID = 1L;

  private final List<Long> readIds = new ArrayList<>();

  private final BaseValidator validator = new BaseValidator();

  private BaseAction action;

  private GlobalUnitManager crpManager;

  private static GlobalUnit unit(long id, String acronym) {
    GlobalUnit unit = new GlobalUnit();
    unit.setId(Long.valueOf(id));
    unit.setAcronym(acronym);
    return unit;
  }

  @Before
  public void setUp() {
    this.action = new BaseAction() {

      private static final long serialVersionUID = 1L;
    };
    this.crpManager = (GlobalUnitManager) Proxy.newProxyInstance(GlobalUnitManager.class.getClassLoader(),
      new Class<?>[] {GlobalUnitManager.class}, (proxy, method, args) -> {
        if ("getGlobalUnitById".equals(method.getName())) {
          long id = ((Long) args[0]).longValue();
          this.readIds.add(Long.valueOf(id));
          return id == OTHER_ID ? unit(OTHER_ID, "CCAFS") : null;
        }
        throw new UnsupportedOperationException(method.getName());
      });
  }

  private void sessionWith(GlobalUnit unit) {
    Map<String, Object> session = new HashMap<>();
    session.put(APConstants.SESSION_CRP, unit);
    this.action.setSession(session);
  }

  @Test
  public void anotherUnitIsReadFromTheDatabase() {
    this.sessionWith(unit(AICCRA_ID, "AICCRA"));

    GlobalUnit result = this.validator.getAutoSaveGlobalUnit(this.action, OTHER_ID, this.crpManager);

    assertEquals("CCAFS", result.getAcronym());
    assertEquals(1, this.readIds.size());
  }

  @Test
  public void theSessionUnitIsUsedWithoutReadingIt() {
    GlobalUnit sessionUnit = unit(AICCRA_ID, "AICCRA");
    this.sessionWith(sessionUnit);

    GlobalUnit result = this.validator.getAutoSaveGlobalUnit(this.action, AICCRA_ID, this.crpManager);

    assertSame(sessionUnit, result);
    assertTrue("the manager is not called", this.readIds.isEmpty());
  }

  @Test
  public void withoutAnActionTheUnitIsRead() {
    GlobalUnit result = this.validator.getAutoSaveGlobalUnit(null, OTHER_ID, this.crpManager);

    assertEquals("CCAFS", result.getAcronym());
    assertEquals(1, this.readIds.size());
  }

  @Test
  public void withoutSessionTheUnitIsReadAndMissingStaysNull() {
    GlobalUnit result = this.validator.getAutoSaveGlobalUnit(this.action, AICCRA_ID, this.crpManager);

    assertNull("a unit that does not exist is still null", result);
    assertEquals(1, this.readIds.size());
  }
}
