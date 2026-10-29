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

import org.cgiar.ccafs.marlo.action.superadmin.GlobalUnitCreateAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.CrpLocElementTypeManager;
import org.cgiar.ccafs.marlo.data.manager.CrpUserManager;
import org.cgiar.ccafs.marlo.data.manager.CustomParameterManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitCreationManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitTypeManager;
import org.cgiar.ccafs.marlo.data.manager.InstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.LiaisonInstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.ParameterManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.RoleManager;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.manager.UserRoleManager;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.GlobalUnitType;
import org.cgiar.ccafs.marlo.data.model.Parameter;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.User;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * A global unit created from the admin screen names each phase by its type (FN-007, DD-8): Planning, Progress or
 * Reporting. This holds whatever template is copied; a phase whose type is none of the three keeps the name it was
 * copied with.
 * <p>
 * The manager sees the template only through the phases the action copied into {@code PhaseInput}, so the template
 * cases build those inputs the way {@code GlobalUnitCreateAction.createPhaseInputFromTemplate} does: name,
 * description and upkeep copied from the template phase. The names asserted are literal strings. They are not read
 * from {@link APConstants} or from the mapping the code uses, so the assertions can disagree with the code.
 * <p>
 * The pre-M1 case relies on migration M1 of the spec, which corrects {@code description = 'Planing'} to
 * {@code Planning}. Creation follows the strict definitions (DD-3) and does not tolerate the typo: an unmigrated
 * {@code AWPB/Planing/0} template phase is an unknown type and is copied unchanged.
 */
public class GlobalUnitCreationPhaseNamingTest {

  private static final long TYPE_ID = 1L;

  private static final long NEW_GLOBAL_UNIT_ID = 900L;

  /** Names recorded at the moment each phase reached {@code PhaseManager.savePhase}, in call order. */
  private final List<String> savedNames = new ArrayList<>();

  /** Descriptions recorded at the same moment. */
  private final List<String> savedDescriptions = new ArrayList<>();

  private final AtomicLong phaseIds = new AtomicLong(1000L);

  private GlobalUnitCreationManagerImpl manager;

  /**
   * A double whose methods answer a neutral value (an empty list, zero, false, null) unless the test installs an
   * answer, so a creation step the test does not care about does nothing.
   */
  private interface Answers {

    Object answer(Method method, Object[] args) throws Throwable;
  }

  private static <T> T stub(Class<T> type, Answers answers) {
    return type.cast(Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[] {type}, (proxy, method, args) -> {
      Object answer = answers.answer(method, args);
      if (answer != null) {
        return answer;
      }
      Class<?> returnType = method.getReturnType();
      if (List.class.equals(returnType)) {
        return Collections.emptyList();
      }
      if (boolean.class.equals(returnType)) {
        return Boolean.FALSE;
      }
      if (int.class.equals(returnType)) {
        return Integer.valueOf(0);
      }
      if (long.class.equals(returnType)) {
        return Long.valueOf(0L);
      }
      return null;
    }));
  }

  private static GlobalUnitCreationManager.PhaseInput input(String name, String description, boolean upkeep) {
    GlobalUnitCreationManager.PhaseInput input = new GlobalUnitCreationManager.PhaseInput();
    input.setName(name);
    input.setDescription(description);
    input.setYear(Calendar.getInstance().get(Calendar.YEAR));
    input.setUpkeep(Boolean.valueOf(upkeep));
    return input;
  }

  private void create(GlobalUnitCreationManager.PhaseInput... phases) {
    GlobalUnitCreationManager.CreateRequest request = new GlobalUnitCreationManager.CreateRequest();
    request.setName("New unit");
    request.setAcronym("NEWU");
    request.setGlobalUnitTypeId(TYPE_ID);
    request.setMarlo(true);
    request.setLogin(true);
    request.setTemplateGlobalUnitId(45L);
    request.setPhasesInput(new ArrayList<>(Arrays.asList(phases)));
    request.setCurrentPhaseIndex(0);
    this.manager.createGlobalUnit(request);
  }

  @Before
  public void setUp() {
    GlobalUnitType type = new GlobalUnitType();
    type.setId(Long.valueOf(TYPE_ID));

    GlobalUnit template = new GlobalUnit();
    template.setId(Long.valueOf(45L));
    template.setActive(true);
    template.setGlobalUnitType(type);

    Parameter currentPhase = new Parameter();
    currentPhase.setKey(APConstants.CURRENT_PHASE_PARAM);
    currentPhase.setGlobalUnitType(type);

    GlobalUnitManager globalUnitManager = stub(GlobalUnitManager.class, (method, args) -> {
      switch (method.getName()) {
        case "getGlobalUnitById":
          return template;
        case "saveGlobalUnit":
          GlobalUnit saved = (GlobalUnit) args[0];
          saved.setId(Long.valueOf(NEW_GLOBAL_UNIT_ID));
          return saved;
        default:
          return null;
      }
    });
    GlobalUnitTypeManager typeManager = stub(GlobalUnitTypeManager.class, (method, args) -> {
      switch (method.getName()) {
        case "getGlobalUnitTypeById":
          return type;
        case "findAll":
          return Collections.singletonList(type);
        default:
          return null;
      }
    });
    ParameterManager parameterManager = stub(ParameterManager.class, (method, args) -> {
      return "findAll".equals(method.getName()) ? Collections.singletonList(currentPhase) : null;
    });
    PhaseManager phaseManager = stub(PhaseManager.class, (method, args) -> {
      if (!"savePhase".equals(method.getName())) {
        return null;
      }
      Phase phase = (Phase) args[0];
      if (phase.getId() == null) {
        phase.setId(Long.valueOf(this.phaseIds.incrementAndGet()));
        this.savedNames.add(phase.getName());
        this.savedDescriptions.add(phase.getDescription());
      }
      return phase;
    });
    SessionFactory sessionFactory = stub(SessionFactory.class, (method, args) -> {
      if ("getCurrentSession".equals(method.getName())) {
        return stub(Session.class, (m, a) -> null);
      }
      return null;
    });

    this.manager = new GlobalUnitCreationManagerImpl(globalUnitManager, typeManager,
      stub(InstitutionManager.class, (method, args) -> null), phaseManager,
      stub(CrpUserManager.class, (method, args) -> null), stub(UserManager.class, (method, args) -> null),
      stub(RoleManager.class, (method, args) -> null), stub(UserRoleManager.class, (method, args) -> null),
      stub(CrpLocElementTypeManager.class, (method, args) -> null),
      stub(LiaisonInstitutionManager.class,
        (method, args) -> "saveLiaisonInstitution".equals(method.getName()) ? args[0] : null),
      parameterManager,
      stub(CustomParameterManager.class, (method, args) -> null), sessionFactory);
  }

  /** AICCRA (45) as the template, after M1: AWPB/Planning/0, Progress/Planning/1, AR/Reporting/0. */
  @Test
  public void aiccraTemplateIsNamedPlanningProgressReporting() {
    this.create(input("AWPB", "Planning", false), input("Progress", "Planning", true),
      input("AR", "Reporting", false));

    assertEquals(Arrays.asList("Planning", "Progress", "Reporting"), this.savedNames);
    assertEquals(Arrays.asList("Planning", "Planning", "Reporting"), this.savedDescriptions);
  }

  /** AICCRA_III (47) as the template: POWB/Planning/0, UpKeep/Planning/1, AR/Reporting/0. */
  @Test
  public void aiccraThreeTemplateIsNamedPlanningProgressReporting() {
    this.create(input("POWB", "Planning", false), input("UpKeep", "Planning", true),
      input("AR", "Reporting", false));

    assertEquals(Arrays.asList("Planning", "Progress", "Reporting"), this.savedNames);
  }

  /** A phase whose type is none of the three keeps the name it was copied with, trimmed as before. */
  @Test
  public void unknownTypePhaseKeepsItsCopiedName() {
    this.create(input("  Pilot ", "Pilot", false), input("Special", "Other", false));

    assertEquals(Arrays.asList("Pilot", "Special"), this.savedNames);
    assertEquals(Arrays.asList("Pilot", "Other"), this.savedDescriptions);
  }

  /**
   * A template phase left at {@code AWPB/Planing/0} (M1 not run) is an unknown type under the strict definitions,
   * so it is copied unchanged: the typo is not tolerated and no phase is named Planning from it.
   */
  @Test
  public void preM1PlaningTemplatePhaseIsCopiedUnchanged() {
    this.create(input("AWPB", "Planing", false), input("AR", "Reporting", false));

    assertEquals(Arrays.asList("AWPB", "Reporting"), this.savedNames);
    assertEquals(Arrays.asList("Planing", "Reporting"), this.savedDescriptions);
  }

  /** The text path: lines naming Planning, Progress and Reporting give each the type it names; Pilot does not. */
  @Test
  public void textPathDerivesTypeForThePhaseNamesOnly() {
    final List<GlobalUnitCreationManager.PhaseInput> received = new ArrayList<>();
    GlobalUnitCreationManager creation = stub(GlobalUnitCreationManager.class, (method, args) -> {
      if ("createGlobalUnit".equals(method.getName())) {
        received.addAll(((GlobalUnitCreationManager.CreateRequest) args[0]).getPhasesInput());
        throw new IllegalStateException("captured");
      }
      return null;
    });
    GlobalUnitCreateAction action = new GlobalUnitCreateAction(null, null, null, null, creation, null) {

      private static final long serialVersionUID = 1L;

      @Override
      public boolean canAccessSuperAdmin() {
        return true;
      }

      @Override
      public GlobalUnit getCurrentGlobalUnit() {
        return null;
      }

      @Override
      public User getCurrentUser() {
        return null;
      }

      @Override
      public String getText(String key, String[] args) {
        return key;
      }
    };
    String year = String.valueOf(Calendar.getInstance().get(Calendar.YEAR) + 1);
    action.setName("New unit");
    action.setAcronym("NEWU");
    action.setPhasesDefinition("Planning|" + year + "\nProgress|" + year + "\nReporting|" + year + "\nPilot|" + year);

    action.save();

    assertEquals(4, received.size());
    assertPhase(received.get(0), "Planning", "Planning", false);
    assertPhase(received.get(1), "Progress", "Planning", true);
    assertPhase(received.get(2), "Reporting", "Reporting", false);
    assertPhase(received.get(3), "Pilot", "Pilot", false);
  }

  private static void assertPhase(GlobalUnitCreationManager.PhaseInput phase, String name, String description,
    boolean upkeep) {
    assertNotNull(phase);
    assertEquals(name, phase.getName());
    assertEquals(description, phase.getDescription());
    assertEquals(Boolean.valueOf(upkeep), phase.isUpkeep());
  }
}
