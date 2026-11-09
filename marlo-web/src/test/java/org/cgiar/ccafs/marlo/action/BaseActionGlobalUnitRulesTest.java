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
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitProjectManager;
import org.cgiar.ccafs.marlo.data.manager.SectionStatusManager;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.GlobalUnitProject;
import org.cgiar.ccafs.marlo.data.model.GlobalUnitType;
import org.cgiar.ccafs.marlo.data.model.SectionStatus;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * The BaseAction rules that used to branch on isAiccra() now answer from the data alone. Every case runs with the
 * AICCRA Global Unit in the session, which is where the removed branches used to short-circuit the answer.
 */
public class BaseActionGlobalUnitRulesTest {

  private static final long PROJECT_ID = 102076L;

  private static final int YEAR = 2026;

  private BaseAction action;

  private Map<String, Object> session;

  /** Answers of the stubbed managers, set by each test. */
  private GlobalUnitProject originUnitProject;

  private Boolean crpOrPlatform;

  private SectionStatus sectionStatus;

  private static GlobalUnit aiccra() {
    GlobalUnit unit = new GlobalUnit();
    unit.setId(Long.valueOf(45L));
    unit.setAcronym("AICCRA");
    return unit;
  }

  private static GlobalUnitProject originOfType(long typeId) {
    GlobalUnitType type = new GlobalUnitType();
    type.setId(Long.valueOf(typeId));
    GlobalUnit unit = new GlobalUnit();
    unit.setGlobalUnitType(type);
    GlobalUnitProject globalUnitProject = new GlobalUnitProject();
    globalUnitProject.setGlobalUnit(unit);
    return globalUnitProject;
  }

  private static void inject(Object target, String fieldName, Object value) throws ReflectiveOperationException {
    Field field = BaseAction.class.getDeclaredField(fieldName);
    field.setAccessible(true);
    field.set(target, value);
  }

  private static SectionStatus statusWithMissingFields(String missingFields) {
    SectionStatus status = new SectionStatus();
    status.setMissingFields(missingFields);
    return status;
  }

  @Before
  public void setUp() throws ReflectiveOperationException {
    this.action = new BaseAction() {

      private static final long serialVersionUID = 1L;

      @Override
      public String getCurrentCycle() {
        return APConstants.PLANNING;
      }

      @Override
      public int getCurrentCycleYear() {
        return YEAR;
      }

      @Override
      public boolean isUpKeepActive() {
        return false;
      }
    };
    this.session = new HashMap<>();
    this.session.put(APConstants.SESSION_CRP, aiccra());
    this.action.setSession(this.session);

    GlobalUnitProjectManager globalUnitProjectManager = (GlobalUnitProjectManager) Proxy.newProxyInstance(
      GlobalUnitProjectManager.class.getClassLoader(), new Class<?>[] {GlobalUnitProjectManager.class},
      (proxy, method, args) -> {
        if ("findByProjectId".equals(method.getName())) {
          return this.originUnitProject;
        }
        if ("isProjectCreatedByCrpOrPlatform".equals(method.getName())) {
          return this.crpOrPlatform;
        }
        throw new UnsupportedOperationException(method.getName());
      });
    SectionStatusManager sectionStatusManager = (SectionStatusManager) Proxy.newProxyInstance(
      SectionStatusManager.class.getClassLoader(), new Class<?>[] {SectionStatusManager.class},
      (proxy, method, args) -> {
        if ("getSectionStatusByProject".equals(method.getName())) {
          return this.sectionStatus;
        }
        throw new UnsupportedOperationException(method.getName());
      });
    inject(this.action, "globalUnitProjectManager", globalUnitProjectManager);
    inject(this.action, "sectionStatusManager", sectionStatusManager);
  }

  @Test
  public void activityCreationFollowsTheStoredValue() {
    this.session.put(APConstants.PROJECT_ACTIVITY_CREATION_ACTIVE, "true");
    assertTrue(this.action.isProjectActivityCreationActive());

    this.session.put(APConstants.PROJECT_ACTIVITY_CREATION_ACTIVE, "false");
    assertFalse(this.action.isProjectActivityCreationActive());
  }

  @Test
  public void activityCreationWithoutAStoredValueUsesTheCatalog() {
    assertFalse(this.action.isProjectActivityCreationActive());
  }

  @Test
  public void budgetByFlagshipIsCompleteOnlyWhenItsStatusHasNoMissingFields() {
    this.sectionStatus = statusWithMissingFields("");
    assertTrue(this.action.getProjectSectionStatus("budgetByFlagship", PROJECT_ID));

    this.sectionStatus = statusWithMissingFields("budget");
    assertFalse(this.action.getProjectSectionStatus("budgetByFlagship", PROJECT_ID));
  }

  @Test
  public void budgetByFlagshipWithUnwrittenMissingFieldsIsNotComplete() {
    this.sectionStatus = statusWithMissingFields(null);
    assertFalse(this.action.getProjectSectionStatus("budgetByFlagship", PROJECT_ID));
  }

  @Test
  public void budgetByFlagshipWithoutAStatusIsNotComplete() {
    this.sectionStatus = null;
    assertFalse(this.action.getProjectSectionStatus("budgetByFlagship", PROJECT_ID));
  }

  @Test
  public void projectCenterFollowsTheOriginUnitType() {
    this.originUnitProject = originOfType(4L);
    assertTrue(this.action.isProjectCenter(PROJECT_ID));

    this.originUnitProject = originOfType(3L);
    assertFalse(this.action.isProjectCenter(PROJECT_ID));
  }

  @Test
  public void projectCenterWithoutAnActiveOriginIsFalse() {
    this.originUnitProject = null;
    assertFalse(this.action.isProjectCenter(PROJECT_ID));
  }

  @Test
  public void projectCenterWithAnUnidentifiedUnitTypeIsFalse() {
    this.originUnitProject = originOfType(4L);
    this.originUnitProject.getGlobalUnit().getGlobalUnitType().setId(null);
    assertFalse(this.action.isProjectCenter(PROJECT_ID));
  }

  @Test
  public void projectCrpOrPlatformWithoutAnAnswerIsFalse() {
    this.crpOrPlatform = null;
    assertFalse(this.action.isProjectCrpOrPlatform(PROJECT_ID));
  }

  @Test
  public void projectCrpOrPlatformFollowsTheOriginLookup() {
    this.crpOrPlatform = Boolean.TRUE;
    assertTrue(this.action.isProjectCrpOrPlatform(PROJECT_ID));

    this.crpOrPlatform = Boolean.FALSE;
    assertFalse(this.action.isProjectCrpOrPlatform(PROJECT_ID));
  }
}
