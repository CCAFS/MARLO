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
import org.cgiar.ccafs.marlo.data.manager.ParameterManager;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.model.CustomParameter;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.GlobalUnitType;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.security.directory.DirectoryPerson;
import org.cgiar.ccafs.marlo.security.directory.DirectoryService;
import org.cgiar.ccafs.marlo.security.directory.DirectorySource;
import org.cgiar.ccafs.marlo.security.directory.FakeDirectoryService;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Drives {@link ManageUsersAction#create()} with a {@link FakeDirectoryService} to prove the migration
 * off {@code BaseAction.getOutlookUser} (DIRABS-T07) preserves the action's observable behavior exactly
 * — design.md §6.2, requirements.md {@code DIRABS-FN-006} *json/global/ManageUsersAction*.
 * <p>
 * {@code newUser} and {@code actionName} are private fields with no setters — normally populated by
 * {@code prepare()} from Struts request parameters, which this test does not simulate. Reflection sets
 * them directly; every assertion below then runs against real, unmodified {@code create()}/{@code addUser()}
 * production code.
 */
public class ManageUsersActionDirectoryTest {

  private static final String EMAIL = "new.user@cgiar.org";

  private FakeDirectoryService directoryService;
  private FakeUserManager userManager;
  private TestableManageUsersAction action;

  /**
   * The parameter is deliberately {@code Object}, not {@code ManageUsersAction}: see the matching note
   * on {@code CrpUsersActionDirectoryTest.setSelectedGlobalUnitAcronym} — the old
   * {@code maven-surefire-plugin:2.12.4} scanner resolves declared-method parameter types on this outer
   * test class while probing for {@code @Test} methods, and {@code BaseAction}'s subclasses crash that.
   */
  private static void inject(Object action, String fieldName, Object value) throws Exception {
    Field field = ManageUsersAction.class.getDeclaredField(fieldName);
    field.setAccessible(true);
    field.set(action, value);
  }

  @Before
  public void setUp() throws Exception {
    this.directoryService = new FakeDirectoryService();
    this.userManager = new FakeUserManager();
    this.action = new TestableManageUsersAction(new APConfig(), this.userManager, this.directoryService);
    inject(this.action, "actionName", "global/createUser");
  }

  /**
   * FN-006 *json/global/ManageUsersAction*, found branch: {@code firstName}, {@code lastName}, a
   * lowercased {@code username} and {@code setCgiarUser(true)} are set and {@code addUser()} is called.
   * FN-004: the mixed-case login ({@code "JSmith"}) is the falsifying input for `D1` — an
   * already-lowercase fixture would pass whether or not the real call site lowercases.
   */
  @Test
  public void foundEmailIsAddedWithLowercasedUsername() throws Exception {
    this.directoryService.setMode(FakeDirectoryService.Mode.FOUND);
    this.directoryService
      .setResponse(DirectoryPerson.found(EMAIL, "JSmith", "Jane", "Smith", DirectorySource.LDAP));

    User newUser = new User();
    newUser.setEmail(EMAIL);
    inject(this.action, "newUser", newUser);

    String result = this.action.create();

    assertEquals(ManageUsersAction.SUCCESS, result);
    assertEquals("addUser() saves twice on success: once to obtain an id, once to set active=false", 2,
      this.userManager.saveUserCallCount);
    assertEquals("login must be lowercased at the call site, not by the abstraction", "jsmith",
      newUser.getUsername());
    assertTrue(newUser.isCgiarUser());
    assertEquals("Jane", newUser.getFirstName());
    assertEquals("Smith", newUser.getLastName());
    assertEquals("addUser() must have populated the users list", 1, this.action.getUsers().size());
  }

  /**
   * FN-006 *json/global/ManageUsersAction*: the non-resolving branch's trim-and-length validation on
   * {@code firstName}/{@code lastName} must not change. Whitespace-only names fail
   * {@code trim().length() > 0}, so {@code addUser()} must never run and the
   * {@code manageUsers.email.validation} message (and {@code emailStatus} flag) must be set instead.
   */
  @Test
  public void notFoundWithWhitespaceOnlyNamesNeverCallsAddUser() throws Exception {
    this.directoryService.setMode(FakeDirectoryService.Mode.NOT_FOUND);

    User newUser = new User();
    newUser.setEmail(EMAIL);
    newUser.setFirstName("   ");
    newUser.setLastName("   ");
    inject(this.action, "newUser", newUser);

    String result = this.action.create();

    assertEquals(ManageUsersAction.SUCCESS, result);
    assertEquals("the trim-and-length guard must reject whitespace-only names", 0,
      this.userManager.saveUserCallCount);
    assertEquals(this.action.getText("manageUsers.email.validation"), this.action.getMessage());
    assertEquals(Boolean.TRUE, this.action.getEmailStatus().get("status"));
  }

  /**
   * FN-006: when the non-resolving branch's names are valid but {@code addUser()} itself fails (the
   * database rejects the save), {@code manageUsers.email.notAdded} must be reported — the other of the
   * two i18n messages this migration must not disturb.
   */
  @Test
  public void notFoundWithValidNamesReportsNotAddedWhenSaveFails() throws Exception {
    this.directoryService.setMode(FakeDirectoryService.Mode.NOT_FOUND);
    this.userManager.simulateSaveFailure = true;

    User newUser = new User();
    newUser.setEmail(EMAIL);
    newUser.setFirstName("Guest");
    newUser.setLastName("User");
    inject(this.action, "newUser", newUser);

    String result = this.action.create();

    assertEquals(ManageUsersAction.SUCCESS, result);
    assertEquals(1, this.userManager.saveUserCallCount);
    assertFalse(newUser.isCgiarUser());
    assertEquals(this.action.getText("manageUsers.email.notAdded"), this.action.getMessage());
  }

  /**
   * FN-002 *Backend failure*: {@code ManageUsersAction} reads only {@code found}, so {@code ERROR} must
   * behave exactly like {@code NOT_FOUND} — same branch, same {@code addUser()} outcome.
   */
  @Test
  public void errorBehavesIdenticallyToNotFound() throws Exception {
    this.directoryService.setMode(FakeDirectoryService.Mode.ERROR);

    User newUser = new User();
    newUser.setEmail(EMAIL);
    newUser.setFirstName("Guest");
    newUser.setLastName("User");
    inject(this.action, "newUser", newUser);

    String result = this.action.create();

    assertEquals(ManageUsersAction.SUCCESS, result);
    assertEquals("ERROR must reach the same addUser() success path as NOT_FOUND", 2,
      this.userManager.saveUserCallCount);
    assertFalse(newUser.isCgiarUser());
    assertNull("ERROR must not surface as a validation message", this.action.getMessage());
  }

  /**
   * Struts' real {@code getText} needs a live container ({@code ActionContext.getContext()} is null
   * outside a Struts request), which this hand-rolled test does not stand up. Returning the key itself
   * is deterministic and lets every assertion above compare against {@code getText(key)} directly.
   */
  /**
   * A2-2449: a manager double that answers "nothing configured" to every lookup, so
   * {@code CognitoAuthSpecificity.isActiveFor} resolves the flag to {@code false} and this suite keeps
   * exercising the pre-Cognito behaviour it was written for. A {@link Proxy} rather than a full interface
   * implementation, because only two methods are ever reached and both return objects -- anything else is a
   * call this suite did not intend to make.
   *
   * @param <T> the manager interface
   * @param type the manager interface to stand in for
   * @return a proxy answering {@code null} from every object-returning method
   */
  private static <T> T unconfiguredManager(Class<T> type) {
    return type.cast(Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[] {type},
      (proxy, method, args) -> method.getReturnType() == boolean.class ? Boolean.FALSE : null));
  }

  private static final class TestableManageUsersAction extends ManageUsersAction {

    private static final long serialVersionUID = 1L;

    TestableManageUsersAction(APConfig config, UserManager userManager, DirectoryService directoryService) {
      super(config, userManager, directoryService, unconfiguredManager(CustomParameterManager.class),
        unconfiguredManager(ParameterManager.class));
    }

    /** A2-2449: lets one test supply managers that resolve {@code cognito_auth_active} to active. */
    TestableManageUsersAction(APConfig config, UserManager userManager, DirectoryService directoryService,
      CustomParameterManager customParameterManager, ParameterManager parameterManager) {
      super(config, userManager, directoryService, customParameterManager, parameterManager);
    }

    @Override
    public String getText(String aTextName) {
      return aTextName;
    }
  }

  /** Records how many times {@code saveUser} runs, and can simulate a failed save (id stays 0). */
  /**
   * A2-2449: managers that resolve {@code cognito_auth_active} to active for any Global Unit.
   *
   * @return a {@code CustomParameterManager} answering with an active {@code "true"} override
   */
  private static CustomParameterManager migratedUnitManager() {
    CustomParameter override = new CustomParameter();
    override.setValue("true");
    override.setActive(true);
    return (CustomParameterManager) Proxy.newProxyInstance(
      CustomParameterManager.class.getClassLoader(), new Class<?>[] {CustomParameterManager.class},
      (proxy, method, args) -> "getCustomParameterByParameterKeyAndGlobalUnitId".equals(method.getName())
        ? override : (method.getReturnType() == boolean.class ? Boolean.FALSE : null));
  }

  /**
   * A2-2449: a Global Unit carrying an id, seeded into the session as the administrator's current unit --
   * which is the only unit this global JSON action can resolve.
   *
   * @param action the action to seed
   * @throws Exception if the session cannot be set
   */
  private static void seedMigratedSessionUnit(Object action) throws Exception {
    GlobalUnit unit = new GlobalUnit();
    unit.setId(Long.valueOf(45L));
    unit.setAcronym("TESTCRP");
    // A faithful stand-in: SESSION_CRP always holds a fully loaded entity, and
    // CognitoAuthSpecificity.isActiveFor dereferences getGlobalUnitType().getId() on the catalog-fallback
    // path without guarding it. A fixture missing the type would make this suite fail on a state production
    // cannot produce -- and would hide the branch actually under test.
    GlobalUnitType type = new GlobalUnitType();
    type.setId(Long.valueOf(3L));
    unit.setGlobalUnitType(type);
    Map<String, Object> session = new HashMap<String, Object>();
    session.put(APConstants.SESSION_CRP, unit);
    ((ManageUsersAction) action).setSession(session);
  }

  /**
   * A2-2449, <b>the branch this change adds</b>. The directory cannot confirm the person -- which is what
   * every account will look like once AD is retired -- but the address is corporate and the administrator's
   * Global Unit authenticates through Cognito. The account must be created as CGIAR, or gate 2 of
   * {@code CognitoIdentityMapper} refuses that person permanently and no screen can flip the flag back.
   * <p>
   * The names come from the form and are provisional; {@code CognitoCallbackAction} replaces them from the
   * directory on the first sign-in. No username is set -- the token carries no AD login.
   */
  @Test
  public void aCorporateAddressInAMigratedUnitIsCreatedAsCgiarWithTheFormNames() throws Exception {
    this.directoryService.setMode(FakeDirectoryService.Mode.NOT_FOUND);
    TestableManageUsersAction migrated = new TestableManageUsersAction(new APConfig(), this.userManager,
      this.directoryService, migratedUnitManager(), unconfiguredManager(ParameterManager.class));
    inject(migrated, "actionName", "global/createUser");
    seedMigratedSessionUnit(migrated);
    User newUser = new User();
    newUser.setEmail(EMAIL);
    newUser.setFirstName("Priyanka");
    newUser.setLastName("Chandra");
    inject(migrated, "newUser", newUser);

    migrated.create();

    assertTrue("a corporate address in a migrated unit must be created as CGIAR", newUser.isCgiarUser());
    assertEquals("the form names stand in until the first sign-in", "Priyanka", newUser.getFirstName());
    assertEquals("the form names stand in until the first sign-in", "Chandra", newUser.getLastName());
    assertNull("no AD login exists to set", newUser.getUsername());
  }

  /**
   * A2-2449, <b>the guard that keeps the flag honest</b>. Same corporate address, same missing directory
   * answer -- but the Global Unit has not been migrated, so nothing changes and the account is created
   * exactly as it is today. This reddens if the specificity check is ever dropped.
   */
  @Test
  public void aCorporateAddressInANonMigratedUnitIsStillCreatedAsNonCgiar() throws Exception {
    this.directoryService.setMode(FakeDirectoryService.Mode.NOT_FOUND);
    seedMigratedSessionUnit(this.action);
    User newUser = new User();
    newUser.setEmail(EMAIL);
    newUser.setFirstName("Priyanka");
    newUser.setLastName("Chandra");
    inject(this.action, "newUser", newUser);

    this.action.create();

    assertFalse("with the flag off nothing may change", newUser.isCgiarUser());
  }

  /**
   * A2-2449: <b>an incompletely populated Global Unit must resolve to "flag off", never throw.</b>
   * {@code CognitoAuthSpecificity.isActiveFor} guards a null unit and then dereferences {@code getId()} and
   * {@code getGlobalUnitType().getId()}, both auto-unboxing. Production's {@code SESSION_CRP} holds a fully
   * loaded entity, so this state should not arise -- but a creation screen must not answer a feature flag
   * with a 500, and "should not arise" has been wrong before. Remove either clause of the guard and this
   * test throws instead of failing an assertion.
   */
  @Test
  public void anIncompleteSessionUnitResolvesToFlagOffRatherThanThrowing() throws Exception {
    this.directoryService.setMode(FakeDirectoryService.Mode.NOT_FOUND);
    // NO active override on purpose: with one, isActiveFor answers from that branch and never reaches the
    // catalog lookup where getGlobalUnitType() is dereferenced. A null override is what forces it there,
    // which is the only way this test can prove the guard prevents an exception rather than merely
    // changing an outcome. With the guard removed this test ERRORS with a NullPointerException; the first
    // version of it used an active override and only failed an assertion, proving nothing about throwing.
    TestableManageUsersAction migrated = new TestableManageUsersAction(new APConfig(), this.userManager,
      this.directoryService, unconfiguredManager(CustomParameterManager.class),
      unconfiguredManager(ParameterManager.class));
    inject(migrated, "actionName", "global/createUser");
    // an id but no GlobalUnitType -- the dereference isActiveFor performs without guarding it
    GlobalUnit noType = new GlobalUnit();
    noType.setId(Long.valueOf(45L));
    Map<String, Object> session = new HashMap<String, Object>();
    session.put(APConstants.SESSION_CRP, noType);
    migrated.setSession(session);
    User newUser = new User();
    newUser.setEmail(EMAIL);
    newUser.setFirstName("Priyanka");
    newUser.setLastName("Chandra");
    inject(migrated, "newUser", newUser);

    migrated.create();

    assertFalse("an incomplete unit must not be treated as migrated", newUser.isCgiarUser());
  }

  private static final class FakeUserManager implements UserManager {

    private long nextId = 1;

    private int saveUserCallCount;

    private boolean simulateSaveFailure;

    @Override
    public List<String> getCenterPermission(int userId, String crp) {
      throw new UnsupportedOperationException("not used in this test");
    }

    @Override
    public List<String> getPermission(int userId, String crp) {
      throw new UnsupportedOperationException("not used in this test");
    }

    @Override
    public User getUser(Long userId) {
      throw new UnsupportedOperationException("not used in this test");
    }

    @Override
    public User getUserByEmail(String email) {
      return null;
    }

    @Override
    public User getUserByUsername(String username) {
      throw new UnsupportedOperationException("not used in this test");
    }

    @Override
    public User getActiveSuperAdminUserByUsernameOccurrence() {
      throw new UnsupportedOperationException("not used in this test");
    }

    @Override
    public User login(String email, String password) {
      throw new UnsupportedOperationException("not used in this test");
    }

    @Override
    public boolean saveLastLogin(User user) {
      throw new UnsupportedOperationException("not used in this test");
    }

    @Override
    public User saveUser(User user) {
      this.saveUserCallCount++;
      if (this.simulateSaveFailure) {
        user.setId(0L);
      } else {
        user.setId(this.nextId++);
      }
      return user;
    }

    @Override
    public List<User> searchUser(String searchValue) {
      throw new UnsupportedOperationException("not used in this test");
    }
  }
}
