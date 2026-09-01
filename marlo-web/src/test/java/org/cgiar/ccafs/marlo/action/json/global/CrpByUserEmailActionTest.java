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

package org.cgiar.ccafs.marlo.action.json.global;

import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.CustomParameterManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.ParameterManager;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.model.CustomParameter;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.GlobalUnitType;
import org.cgiar.ccafs.marlo.data.model.Parameter;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Covers CHG-COGNITO-AUTH-001-T10: {@code crpByEmail.do}'s per-unit {@code cognitoEnabled} flag and the
 * two structural fixes to the existing action (design.md 4).
 * <p>
 * Every collaborator is a hand-rolled double: MARLO has no mocking framework ({@code DEC-005} is
 * {@code PENDING}), matching {@code CognitoLoginActionTest} and {@code LoginActionFinishLoginTest}. Tests
 * drive the real {@link CrpByUserEmailAction#execute()} directly -- it needs no live Struts request. The
 * private {@code userEmail} field, which production only populates from {@code prepare()} reading a live
 * Struts {@code Parameter}, is set through reflection instead, the same workaround
 * {@code LoginActionFinishLoginTest} uses for the same reason.
 * <p>
 * <b>What these tests do not cover</b> (recorded here rather than only in the implementer's report): the
 * Struts interceptor stack that routes a real request to {@link CrpByUserEmailAction#execute()}, and the
 * actual JSON serialization {@code struts-home.xml}'s {@code crpByEmail} action result produces --
 * notably, that result has no {@code excludeNullProperties}, so a {@code null} {@code user} still appears
 * in the wire payload as the JSON key {@code "user":null} rather than being omitted. These tests assert on
 * the action's Java getters, which is the layer available without a servlet container.
 */
public class CrpByUserEmailActionTest {

  private static final String CGIAR_EMAIL = "priya.cgiar@cgiar.org";
  private static final String UNKNOWN_EMAIL = "nobody@cgiar.org";

  private RecordingUserManager userManager;
  private StubGlobalUnitManager crpManager;
  private StubCustomParameterManager customParameterManager;
  private StubParameterManager parameterManager;

  /**
   * Global Unit ids and Global Unit TYPE ids are deliberately disjoint number ranges here. If they overlapped,
   * passing the wrong one to the catalog lookup would resolve anyway and the mistake would never surface.
   */
  private static final long EXPECTED_TYPE_ID = 91L;

  private static GlobalUnit globalUnit(long id, String acronym) {
    GlobalUnitType type = new GlobalUnitType();
    type.setId(Long.valueOf(EXPECTED_TYPE_ID));
    GlobalUnit unit = new GlobalUnit();
    unit.setId(Long.valueOf(id));
    unit.setAcronym(acronym);
    unit.setName(acronym + " full name");
    unit.setGlobalUnitType(type);
    return unit;
  }

  private static User cgiarUser() {
    User user = new User();
    user.setId(Long.valueOf(9001L));
    user.setEmail(CGIAR_EMAIL);
    user.setCgiarUser(true);
    user.setActive(true);
    return user;
  }

  private static Parameter catalogRow(String defaultValue) {
    Parameter parameter = new Parameter();
    parameter.setKey(APConstants.COGNITO_AUTH_ACTIVE);
    parameter.setDefaultValue(defaultValue);
    return parameter;
  }

  private static CustomParameter activeOverride(String value) {
    CustomParameter override = new CustomParameter();
    override.setValue(value);
    override.setActive(true);
    return override;
  }

  @Before
  public void setUp() {
    this.userManager = new RecordingUserManager();
    this.crpManager = new StubGlobalUnitManager();
    this.customParameterManager = new StubCustomParameterManager();
    this.parameterManager = new StubParameterManager();
    // Catalog default is "false" everywhere unless a test overrides it -- matches CognitoLoginActionTest's
    // baseline and keeps a Global Unit disabled unless something explicitly turns it on.
    this.parameterManager.catalogDefault = "false";
  }

  private CrpByUserEmailAction newAction() {
    return new CrpByUserEmailAction(new APConfig(), this.crpManager, this.userManager, this.customParameterManager,
      this.parameterManager);
  }

  /**
   * T10 test 1 -- the C-1 defect. <b>Not evidence when the two units share one flag state</b> (T10's own
   * "Not evidence when" clause): that variant would pass under the broken single-scalar design and prove
   * nothing about per-unit resolution. Here unit A has an ACTIVE override of {@code "true"} and unit B has
   * no override and a catalog default of {@code "false"} -- two genuinely different resolution paths, not
   * just two different literal values.
   */
  @Test
  public void aUserInTwoUnitsWithDifferentFlagStatesGetsDifferentCognitoEnabledPerEntry() {
    GlobalUnit unitA = globalUnit(1L, "AAA");
    GlobalUnit unitB = globalUnit(2L, "BBB");
    this.crpManager.units = Arrays.asList(unitA, unitB);
    this.userManager.register(cgiarUser());
    this.customParameterManager.overridesByUnitId.put(Long.valueOf(1L), activeOverride("true"));
    // Unit B intentionally has no override: it falls through to the catalog default ("false" from setUp()).

    CrpByUserEmailAction action = this.newAction();
    this.setUserEmail(action, CGIAR_EMAIL);

    this.execute(action);

    List<Map<String, Object>> crps = action.getCrps();
    assertEquals(2, crps.size());
    Map<String, Object> aEntry = this.byAcronym(crps, "AAA");
    Map<String, Object> bEntry = this.byAcronym(crps, "BBB");
    assertEquals(Boolean.TRUE, aEntry.get("cognitoEnabled"));
    assertEquals(Boolean.FALSE, bEntry.get("cognitoEnabled"));
    assertNotNull("a user with Global Units must get a populated user map", action.getUser());
    assertEquals(Boolean.TRUE, action.getUser().get("isCgiarUser"));
  }

  /**
   * <b>The task's own "Fails when" mutation, run and recorded verbatim in the implementer's report</b>:
   * hoisting {@code cognitoEnabled} to a single scalar on {@code user} makes this exact test unable to
   * express two different values for unit A and unit B.
   */
  @Test
  public void mixedFlagsProduceTwoDistinctValuesNotOneSharedScalar() {
    GlobalUnit unitA = globalUnit(1L, "AAA");
    GlobalUnit unitB = globalUnit(2L, "BBB");
    this.crpManager.units = Arrays.asList(unitA, unitB);
    this.userManager.register(cgiarUser());
    this.customParameterManager.overridesByUnitId.put(Long.valueOf(1L), activeOverride("true"));

    CrpByUserEmailAction action = this.newAction();
    this.setUserEmail(action, CGIAR_EMAIL);
    this.execute(action);

    List<Map<String, Object>> crps = action.getCrps();
    Object aFlag = this.byAcronym(crps, "AAA").get("cognitoEnabled");
    Object bFlag = this.byAcronym(crps, "BBB").get("cognitoEnabled");
    assertTrue("the two units must genuinely disagree for this test to prove anything",
      !aFlag.equals(bFlag));
  }

  /**
   * <b>MIG-001's rollback, which nothing in this spec proved until now.</b>
   * <p>
   * Every other test here and in {@code CognitoLoginActionTest} uses a catalog default of {@code "false"} and,
   * where an override exists, an active override of {@code "true"}. Two mutations of
   * {@code CognitoAuthSpecificity.isActiveFor} survive that entire suite green:
   * <ul>
   * <li>{@code if (override != null) { return true; }} — ignoring the override's <i>value</i>. An operator
   * performing MIG-001's rollback (set {@code custom_parameters.value} to {@code 'false'}; "the flag is the
   * rollback") would leave Cognito <b>enabled</b>.</li>
   * <li>reading the catalog before the override — inverting the COALESCE precedence PS-16 exists to make
   * canonical, undetectable while every catalog default is {@code "false"}.</li>
   * </ul>
   * This test inverts both fixtures: the catalog says {@code "true"}, unit A's active override says
   * {@code "false"}. Unit A must come back <b>disabled</b> (override wins, and its value is parsed), unit B —
   * no override — must come back <b>enabled</b> (the catalog default is actually read).
   */
  @Test
  public void anActiveOverrideOfFalseBeatsACatalogDefaultOfTrue() {
    GlobalUnit unitA = globalUnit(1L, "AAA");
    GlobalUnit unitB = globalUnit(2L, "BBB");
    this.crpManager.units = Arrays.asList(unitA, unitB);
    this.userManager.register(cgiarUser());
    this.parameterManager.catalogDefault = "true";
    this.customParameterManager.overridesByUnitId.put(Long.valueOf(1L), activeOverride("false"));

    CrpByUserEmailAction action = this.newAction();
    this.setUserEmail(action, CGIAR_EMAIL);

    this.execute(action);

    List<Map<String, Object>> crps = action.getCrps();
    assertEquals(Boolean.FALSE, this.byAcronym(crps, "AAA").get("cognitoEnabled"));
    assertEquals(Boolean.TRUE, this.byAcronym(crps, "BBB").get("cognitoEnabled"));
  }

  /**
   * T10 test 2 -- the structural bug. A user row exists (found by email) but belongs to zero Global Units:
   * the original code built {@code user} <b>inside</b> the {@code for} loop over {@code crps}, so it never
   * ran and {@code user} stayed {@code null}. The fix must return a well-formed response instead.
   */
  @Test
  public void aUserWithZeroGlobalUnitsReturnsAWellFormedResponseNotUserNull() {
    this.crpManager.units = new ArrayList<GlobalUnit>();
    this.userManager.register(cgiarUser());

    CrpByUserEmailAction action = this.newAction();
    this.setUserEmail(action, CGIAR_EMAIL);
    this.execute(action);

    assertNotNull("user must not be null for an account that exists, even with zero Global Units",
      action.getUser());
    assertEquals(Boolean.TRUE, action.getUser().get("isCgiarUser"));
    assertNotNull("crps must be a well-formed (empty) list, not null", action.getCrps());
    assertTrue(action.getCrps().isEmpty());
  }

  /**
   * T10 test 3 -- the security test (FN-001 "Email not found" / R-D3). An unknown email must not disclose
   * whether it would have been CGIAR or local. This asserts <b>absence</b>, not merely a null field: no
   * {@code isCgiarUser} key can be read at all (there is no {@code user} map to read it from), and
   * {@code crps} carries zero entries -- the same shape a known user with zero Global Units produces, so
   * an attacker comparing the two responses gains no oracle from this endpoint's structure.
   */
  @Test
  public void anUnknownEmailDisclosesNeitherIsCgiarUserNorCrps() {
    this.crpManager.units = Arrays.asList(globalUnit(1L, "AAA"));
    // No user registered under UNKNOWN_EMAIL in either lookup path.

    CrpByUserEmailAction action = this.newAction();
    this.setUserEmail(action, UNKNOWN_EMAIL);
    this.execute(action);

    assertNull("no user object may be produced for an unknown email", action.getUser());
    assertNotNull(action.getCrps());
    assertTrue("no Global Unit membership may be disclosed for an unknown email", action.getCrps().isEmpty());
    // The stubbed GlobalUnitManager below records whether it was even asked -- an unknown email must not
    // cause a membership lookup to run at all, since crps[] cannot be trusted to come back empty by luck.
    assertFalse("crpUsers() must not be called once the account itself cannot be found",
      this.crpManager.crpUsersCalled);
  }

  private Map<String, Object> byAcronym(List<Map<String, Object>> crps, String acronym) {
    for (Map<String, Object> entry : crps) {
      if (acronym.equals(entry.get("acronym"))) {
        return entry;
      }
    }
    throw new IllegalStateException("no entry for acronym " + acronym);
  }

  /** Drives {@link CrpByUserEmailAction#execute()}, translating its checked exception into a test failure. */
  private void execute(CrpByUserEmailAction action) {
    try {
      action.execute();
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }

  /**
   * {@code userEmail} has no public setter -- production only ever populates it from {@code prepare()}
   * reading a live Struts {@code Parameter}, which these tests deliberately avoid faking (matching
   * {@code LoginActionFinishLoginTest}'s reflection approach for the same reason: no servlet container).
   */
  private void setUserEmail(CrpByUserEmailAction action, String email) {
    try {
      Field field = CrpByUserEmailAction.class.getDeclaredField("userEmail");
      field.setAccessible(true);
      field.set(action, email);
    } catch (ReflectiveOperationException e) {
      throw new IllegalStateException(e);
    }
  }

  /** Records whether {@code crpUsers(String)} was invoked, and returns the configured unit list. */
  private static final class StubGlobalUnitManager implements GlobalUnitManager {

    private List<GlobalUnit> units = new ArrayList<GlobalUnit>();
    private boolean crpUsersCalled;

    @Override
    public List<GlobalUnit> crpUsers(String email) {
      this.crpUsersCalled = true;
      return new ArrayList<GlobalUnit>(this.units);
    }

    @Override
    public void deleteGlobalUnit(long globalUnitId) {
      throw new UnsupportedOperationException("not needed by this suite");
    }

    @Override
    public boolean existGlobalUnit(long globalUnitID) {
      throw new UnsupportedOperationException("not needed by this suite");
    }

    @Override
    public List<GlobalUnit> findAll() {
      throw new UnsupportedOperationException("not needed by this suite");
    }

    @Override
    public GlobalUnit findGlobalUnitByAcronym(String acronym) {
      throw new UnsupportedOperationException("not needed by this suite");
    }

    @Override
    public GlobalUnit findGlobalUnitBySMOCode(String smoCode) {
      throw new UnsupportedOperationException("not needed by this suite");
    }

    @Override
    public GlobalUnit getGlobalUnitById(long globalUnitID) {
      throw new UnsupportedOperationException("not needed by this suite");
    }

    @Override
    public GlobalUnit saveGlobalUnit(GlobalUnit globalUnit) {
      throw new UnsupportedOperationException("not needed by this suite");
    }
  }

  /** Resolves {@code getUserByEmail}/{@code getUserByUsername} from one registered-user map. */
  private static final class RecordingUserManager implements UserManager {

    private final Map<String, User> byEmail = new HashMap<String, User>();

    void register(User user) {
      this.byEmail.put(user.getEmail(), user);
    }

    @Override
    public User getActiveSuperAdminUserByUsernameOccurrence() {
      throw new UnsupportedOperationException("not needed by this suite");
    }

    @Override
    public List<String> getCenterPermission(int userId, String crp) {
      throw new UnsupportedOperationException("not needed by this suite");
    }

    @Override
    public List<String> getPermission(int userId, String crp) {
      throw new UnsupportedOperationException("not needed by this suite");
    }

    @Override
    public User getUser(Long userId) {
      throw new UnsupportedOperationException("not needed by this suite");
    }

    @Override
    public User getUserByEmail(String email) {
      return this.byEmail.get(email);
    }

    @Override
    public User getUserByUsername(String username) {
      return this.byEmail.get(username);
    }

    @Override
    public User login(String email, String password) {
      throw new UnsupportedOperationException("not needed by this suite");
    }

    @Override
    public boolean saveLastLogin(User user) {
      throw new UnsupportedOperationException("not needed by this suite");
    }

    @Override
    public User saveUser(User user) {
      throw new UnsupportedOperationException("not needed by this suite");
    }

    @Override
    public List<User> searchUser(String searchValue) {
      throw new UnsupportedOperationException("not needed by this suite");
    }
  }

  /** Resolves only the {@code cognito_auth_active} key, keyed by Global Unit id; anything else is a test bug. */
  private static final class StubCustomParameterManager implements CustomParameterManager {

    private final Map<Long, CustomParameter> overridesByUnitId = new HashMap<Long, CustomParameter>();

    @Override
    public void deleteCustomParameter(long customParameterId) {
      throw new UnsupportedOperationException("not needed by this suite");
    }

    @Override
    public boolean existCustomParameter(long customParameterID) {
      throw new UnsupportedOperationException("not needed by this suite");
    }

    @Override
    public List<CustomParameter> findAll() {
      throw new UnsupportedOperationException("not needed by this suite");
    }

    @Override
    public List<CustomParameter> getAllCustomParametersByGlobalUnitId(long globalUnitId) {
      throw new UnsupportedOperationException("not needed by this suite");
    }

    @Override
    public CustomParameter getCustomParameterById(long customParameterID) {
      throw new UnsupportedOperationException("not needed by this suite");
    }

    @Override
    public CustomParameter getCustomParameterByParameterKeyAndGlobalUnitId(String paramaterKey, long globalUnitId) {
      if (!APConstants.COGNITO_AUTH_ACTIVE.equals(paramaterKey)) {
        throw new IllegalStateException("test authoring error: unexpected key " + paramaterKey);
      }
      return this.overridesByUnitId.get(Long.valueOf(globalUnitId));
    }

    @Override
    public CustomParameter saveCustomParameter(CustomParameter customParameter) {
      throw new UnsupportedOperationException("not needed by this suite");
    }
  }

  /** Resolves only the {@code cognito_auth_active} key with one configured catalog default. */
  private static final class StubParameterManager implements ParameterManager {

    private String catalogDefault;

    @Override
    public void deleteParameter(long parameterId) {
      throw new UnsupportedOperationException("not needed by this suite");
    }

    @Override
    public boolean existParameter(long parameterID) {
      throw new UnsupportedOperationException("not needed by this suite");
    }

    @Override
    public List<Parameter> findAll() {
      throw new UnsupportedOperationException("not needed by this suite");
    }

    @Override
    public Parameter getParameterById(long parameterID) {
      throw new UnsupportedOperationException("not needed by this suite");
    }

    @Override
    public Parameter getParameterByKey(String key, long globalUnitId) {
      if (!APConstants.COGNITO_AUTH_ACTIVE.equals(key)) {
        throw new IllegalStateException("test authoring error: unexpected key " + key);
      }
      // The id argument is HONOURED, not ignored. The `parameters` catalog is keyed by Global Unit TYPE,
      // and this interface names the parameter `globalUnitId` while the DAO beneath it names the same
      // argument `globalUnitTypeId` -- so passing a unit id instead of a type id is an easy, silent mistake.
      // A stub that returns the same row for any id makes that mutation invisible; this one reddens it.
      if (globalUnitId != EXPECTED_TYPE_ID) {
        return null;
      }
      return catalogRow(this.catalogDefault);
    }

    @Override
    public Parameter saveParameter(Parameter parameter) {
      throw new UnsupportedOperationException("not needed by this suite");
    }
  }
}
