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

package org.cgiar.ccafs.marlo.action.crp.admin;

import org.cgiar.ccafs.marlo.data.manager.CrpUserManager;
import org.cgiar.ccafs.marlo.data.manager.CustomParameterManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.ParameterManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.RoleManager;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.manager.UserRoleManager;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.CrpUser;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.Role;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.data.model.UserRole;
import org.cgiar.ccafs.marlo.security.directory.DirectoryPerson;
import org.cgiar.ccafs.marlo.security.directory.DirectoryService;
import org.cgiar.ccafs.marlo.security.directory.DirectorySource;
import org.cgiar.ccafs.marlo.security.directory.FakeDirectoryService;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.SendMailS;
import org.cgiar.ccafs.marlo.validation.superadmin.GuestUsersValidator;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.opensymphony.xwork2.Action;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Drives {@link CrpUsersAction#save()} with a {@link FakeDirectoryService} to prove the migration off
 * {@code BaseAction.getOutlookUser} (DIRABS-T06) preserves the action's observable behavior exactly —
 * design.md §6.2, requirements.md {@code DIRABS-FN-006} *CrpUsersAction*.
 * <p>
 * Every collaborator except {@link DirectoryService} is a hand-rolled double: MARLO has no mocking
 * framework (`DEC-005` `PENDING`). {@link #action} bypasses only the infrastructure this migration does
 * not touch — Shiro authorization ({@code canAcessCrpAdmin}), the Struts action-name lookup
 * ({@code getActionName}), and the mail-merge/PDF-attachment machinery in {@code sendMailNewUser}, which
 * would otherwise require a live Struts dispatcher and Hibernate session. The directory lookup, the
 * field mapping, the password generation and every {@code isCGIARUser}/{@code setCgiarUser} branch run
 * as real production code.
 */
public class CrpUsersActionDirectoryTest {

  private static final String EMAIL = "new.guest@cgiar.org";

  private FakeDirectoryService directoryService;
  private FakeUserManager userManager;
  private TestableCrpUsersAction action;

  /**
   * The parameter is deliberately {@code Object}, not {@code CrpUsersAction}: the old
   * {@code maven-surefire-plugin:2.12.4} JUnit4 test-class scanner resolves every declared method's
   * parameter and return types on this outer test class while probing it for {@code @Test} methods,
   * and doing that for {@code CrpUsersAction} (which pulls in the whole 9,700-line {@code BaseAction})
   * crashes the scanning fork with a bare {@code NoClassDefFoundError: CrpUsersAction} before any test
   * runs. A reference inside the method body, resolved lazily at first execution, does not trigger it.
   */
  private static void setSelectedGlobalUnitAcronym(Object action, String acronym) throws Exception {
    Field field = CrpUsersAction.class.getDeclaredField("selectedGlobalUnitAcronym");
    field.setAccessible(true);
    field.set(action, acronym);
  }

  @Before
  public void setUp() throws Exception {
    this.directoryService = new FakeDirectoryService();
    this.userManager = new FakeUserManager();

    APConfig config = new APConfig();
    SendMailS noOpSendMailS = new SendMailS(config, null, null) {

      @Override
      public void send(String toEmail, String ccEmail, String bbcEmail, String subject, String messageContent,
        byte[] attachment, String attachmentMimeType, String fileName, boolean isHtml) {
        // Mail delivery is outside this test's scope; DIRABS-T06 only touches the directory lookup.
      }
    };

    this.action = new TestableCrpUsersAction(config, new TestGlobalUnitManager(), new NoOpCrpUserManager(),
      this.userManager, new NoOpProjectManager(), new NoOpPhaseManager(), new NoOpRoleManager(),
      new NoOpUserRoleManager(), noOpSendMailS, new GuestUsersValidator(this.directoryService),
      this.directoryService);
    this.action.setInvalidFields(new HashMap<String, String>());
    setSelectedGlobalUnitAcronym(this.action, "TESTCRP");
  }

  /**
   * FN-006 *CrpUsersAction*, found branch: {@code newUser} receives {@code firstName}, {@code lastName},
   * a lowercased {@code username} and {@code setCgiarUser(true)}; {@code isCGIARUser} is {@code true}.
   * FN-004: the login is mixed-case ({@code "JSmith"}) so a fake that lowercases would pass the test
   * whether or not the real call site does — only the exact {@code "jsmith"} value proves it.
   */
  @Test
  public void foundPersonReceivesLowercasedLoginAndIsMarkedCgiar() throws Exception {
    this.directoryService.setMode(FakeDirectoryService.Mode.FOUND);
    this.directoryService
      .setResponse(DirectoryPerson.found(EMAIL, "JSmith", "Jane", "Smith", DirectorySource.LDAP));

    User formUser = new User();
    formUser.setEmail(EMAIL);
    this.action.setUser(formUser);

    String result = this.action.save();

    assertEquals(Action.SUCCESS, result);
    assertTrue(this.action.isCGIARUser());
    User saved = this.userManager.lastSavedUser;
    assertNotNull("save() must reach userManager.saveUser on the found branch", saved);
    assertEquals("login must be lowercased at the call site, not by the abstraction", "jsmith", saved.getUsername());
    assertTrue(saved.isCgiarUser());
    assertEquals("Jane", saved.getFirstName());
    assertEquals("Smith", saved.getLastName());
  }

  /**
   * FN-006 *CrpUsersAction*, non-resolving branch: unchanged when the person is not found — both
   * {@code firstName}/{@code lastName} come from the form, a 6-digit numeric password is generated, and
   * {@code setCgiarUser(false)} is applied.
   */
  @Test
  public void notFoundPersonWithFormNamesIsCreatedAsNonCgiarWithASixDigitPassword() throws Exception {
    this.directoryService.setMode(FakeDirectoryService.Mode.NOT_FOUND);

    User formUser = new User();
    formUser.setEmail(EMAIL);
    formUser.setFirstName("Guest");
    formUser.setLastName("User");
    this.action.setUser(formUser);

    String result = this.action.save();

    assertEquals(Action.SUCCESS, result);
    assertFalse(this.action.isCGIARUser());
    User saved = this.userManager.lastSavedUser;
    assertNotNull("save() must reach userManager.saveUser on the non-resolving branch", saved);
    assertFalse(saved.isCgiarUser());
    assertEquals("Guest", saved.getFirstName());
    assertEquals("User", saved.getLastName());
    assertNotNull(this.action.capturedPassword);
    assertTrue("password must be exactly 6 digits", this.action.capturedPassword.matches("\\d{6}"));
  }

  /**
   * A2-2449: <b>a blank name must not create an account.</b> Both branches of {@code save()} used to test
   * the names for {@code null} alone, so {@code ""} -- which is what the form actually submits when the
   * fields are left empty -- and {@code " "} both passed, and produced a row carrying a blank name, visible
   * in every listing that renders it. The sibling {@code json/global/ManageUsersAction} has always tested
   * for content rather than presence; this brings both branches here in line with it. Strengthened on both,
   * not only the new one, because the weakness was identical and leaving two neighbouring branches
   * validating differently is worse than the bug.
   * <p>
   * {@code ""} is the case that matters: it is the realistic input, and the one that made the guard below
   * necessary rather than merely tidy. See {@link #aBlankNameReportsTheMissingFieldsInsteadOfThrowing()}.
   */
  @Test
  public void blankNamesNeverCreateAnAccount() throws Exception {
    for (String blank : new String[] {"", " ", "\t"}) {
      this.setUp();
      this.directoryService.setMode(FakeDirectoryService.Mode.NOT_FOUND);

      User formUser = new User();
      formUser.setEmail(EMAIL);
      formUser.setFirstName(blank);
      formUser.setLastName(blank);
      this.action.setUser(formUser);

      this.action.save();

      assertNull("a blank name must not reach userManager.saveUser, given [" + blank + "]",
        this.userManager.lastSavedUser);
    }
  }

  /**
   * A2-2449, <b>the regression this change had to fix to be safe.</b> When no account is created,
   * {@code save()} used to fall straight through to {@code notifyRoleAssigned}, which reloads the user by id
   * ({@code userManager.getUser(userAssigned.getId())}) and then dereferences what comes back -- {@code null}
   * for a user that was never persisted. It also went on to write a {@code CrpUser} and a {@code UserRole}
   * pointing at that unsaved {@code User}.
   * <p>
   * That path was nearly unreachable before, because the old {@code != null} test accepted the {@code ""} the
   * form submits and created the account anyway. Requiring real content made it the <b>common</b> path, so
   * an admin submitting the guest form with no names would have got a 500 instead of a blank-name row.
   * {@code GuestUsersValidator} does not prevent it: it tests {@code isEmpty()} rather than trimming, and
   * only calls {@code addMessage}, never {@code addFieldError}, so {@code save()} runs regardless.
   * <p>
   * Remove the {@code newUser.getId() != null} guard and this test ERRORS with a NullPointerException.
   */
  @Test
  public void aBlankNameReportsTheMissingFieldsInsteadOfThrowing() throws Exception {
    this.directoryService.setMode(FakeDirectoryService.Mode.NOT_FOUND);

    User formUser = new User();
    formUser.setEmail(EMAIL);
    formUser.setFirstName("");
    formUser.setLastName("");
    this.action.setUser(formUser);

    String result = this.action.save();

    assertEquals("a blank name is a validation failure, not a save", Action.INPUT, result);
    assertTrue("the first name must be reported as missing",
      this.action.getInvalidFields().containsKey("input-user.firstName"));
    assertTrue("the last name must be reported as missing",
      this.action.getInvalidFields().containsKey("input-user.lastName"));
    assertNull("nothing may be persisted", this.userManager.lastSavedUser);
  }

  /**
   * FN-002 *Backend failure*: {@code CrpUsersAction} reads only {@code found}, so {@code ERROR} must
   * behave exactly like {@code NOT_FOUND} here — same branch, same outcome.
   */
  @Test
  public void errorBehavesIdenticallyToNotFound() throws Exception {
    this.directoryService.setMode(FakeDirectoryService.Mode.ERROR);

    User formUser = new User();
    formUser.setEmail(EMAIL);
    formUser.setFirstName("Guest");
    formUser.setLastName("User");
    this.action.setUser(formUser);

    String result = this.action.save();

    assertEquals(Action.SUCCESS, result);
    assertFalse(this.action.isCGIARUser());
    User saved = this.userManager.lastSavedUser;
    assertNotNull("ERROR must reach the same non-resolving branch as NOT_FOUND", saved);
    assertFalse(saved.isCgiarUser());
    assertEquals("Guest", saved.getFirstName());
    assertEquals("User", saved.getLastName());
    assertNotNull(this.action.capturedPassword);
    assertTrue("ERROR's password must be the same 6-digit shape as NOT_FOUND's",
      this.action.capturedPassword.matches("\\d{6}"));
  }

  /** No-op {@link CrpUserManager}: {@code save()} only needs {@code saveCrpUser} to echo its argument. */
  private static final class NoOpCrpUserManager implements CrpUserManager {

    @Override
    public void deleteCrpUser(long crpUserId) {
      throw new UnsupportedOperationException("not used in this test");
    }

    @Override
    public boolean existActiveCrpUser(long userId, long crpId) {
      throw new UnsupportedOperationException("not used in this test");
    }

    @Override
    public boolean existCrpUser(long crpUserID) {
      throw new UnsupportedOperationException("not used in this test");
    }

    @Override
    public boolean existCrpUser(long userId, long crpId) {
      throw new UnsupportedOperationException("not used in this test");
    }

    @Override
    public List<CrpUser> findAll() {
      throw new UnsupportedOperationException("not used in this test");
    }

    @Override
    public CrpUser getCrpUserById(long crpUserID) {
      throw new UnsupportedOperationException("not used in this test");
    }

    @Override
    public CrpUser getCrpUserByUserIdAndCrpId(long userId, long crpId) {
      throw new UnsupportedOperationException("not used in this test");
    }

    @Override
    public CrpUser saveCrpUser(CrpUser crpUser) {
      return crpUser;
    }
  }

  /** No-op {@link PhaseManager}: unused on the {@code save()} path exercised here. */
  private static final class NoOpPhaseManager implements PhaseManager {

    @Override
    public void deletePhase(long phaseId) {
      throw new UnsupportedOperationException("not used in this test");
    }

    @Override
    public boolean existPhase(long phaseID) {
      throw new UnsupportedOperationException("not used in this test");
    }

    @Override
    public List<Phase> findAll() {
      throw new UnsupportedOperationException("not used in this test");
    }

    @Override
    public Phase findCycle(String cylce, int year, boolean upkeep, long crpId) {
      throw new UnsupportedOperationException("not used in this test");
    }

    @Override
    public Phase findPreviousPhase(long phaseId) {
      throw new UnsupportedOperationException("not used in this test");
    }

    @Override
    public Phase getActivePhase(long globalUnitId) {
      throw new UnsupportedOperationException("not used in this test");
    }

    @Override
    public Phase getPhaseById(long phaseID) {
      throw new UnsupportedOperationException("not used in this test");
    }

    @Override
    public Phase savePhase(Phase phase) {
      throw new UnsupportedOperationException("not used in this test");
    }
  }

  /** No-op {@link ProjectManager}: unused on the {@code save()} path exercised here. */
  private static final class NoOpProjectManager implements ProjectManager {

    @Override
    public void deleteProject(Project project) {
      throw new UnsupportedOperationException("not used in this test");
    }

    @Override
    public boolean existProject(long projectID) {
      throw new UnsupportedOperationException("not used in this test");
    }

    @Override
    public List<Project> findAll() {
      throw new UnsupportedOperationException("not used in this test");
    }

    @Override
    public Integer findAllQuantity() {
      throw new UnsupportedOperationException("not used in this test");
    }

    @Override
    public List<Project> getActiveProjectsByPhase(Phase phase, int year, String[] projectStatuses) {
      throw new UnsupportedOperationException("not used in this test");
    }

    @Override
    public List<Project> getCompletedProjects(long crpId, long idPhase) {
      throw new UnsupportedOperationException("not used in this test");
    }

    @Override
    public List<Project> getNoPhaseProjects(long crpId, Phase phase) {
      throw new UnsupportedOperationException("not used in this test");
    }

    @Override
    public List<CrpProgram> getPrograms(long projectID, int type, long idPhase) {
      throw new UnsupportedOperationException("not used in this test");
    }

    @Override
    public Project getProjectById(long projectID) {
      throw new UnsupportedOperationException("not used in this test");
    }

    @Override
    public List<Project> getProjectWebPageList(Long globalunitId) {
      throw new UnsupportedOperationException("not used in this test");
    }

    @Override
    public List<Project> getUserProjects(long userId, String crp) {
      throw new UnsupportedOperationException("not used in this test");
    }

    @Override
    public Project saveProject(Project project) {
      throw new UnsupportedOperationException("not used in this test");
    }

    @Override
    public Project saveProject(Project project, String section, List<String> relationsName) {
      throw new UnsupportedOperationException("not used in this test");
    }

    @Override
    public Project saveProject(Project project, String sectionName, List<String> relationsName, Phase phase) {
      throw new UnsupportedOperationException("not used in this test");
    }
  }

  /** No-op {@link RoleManager}: unused on the {@code save()} path exercised here. */
  private static final class NoOpRoleManager implements RoleManager {

    @Override
    public void deleteRole(long roleId) {
      throw new UnsupportedOperationException("not used in this test");
    }

    @Override
    public boolean existRole(long roleID) {
      throw new UnsupportedOperationException("not used in this test");
    }

    @Override
    public List<Role> findAll() {
      throw new UnsupportedOperationException("not used in this test");
    }

    @Override
    public List<Role> findAllByGlobalUnit(long globalUnitId) {
      throw new UnsupportedOperationException("not used in this test");
    }

    @Override
    public int cloneRolePermissionsByAcronym(long templateGlobalUnitId, long targetGlobalUnitId) {
      throw new UnsupportedOperationException("not used in this test");
    }

    @Override
    public int ensureSuperAdminRoleAndPermissions(long targetGlobalUnitId, long templateGlobalUnitId) {
      throw new UnsupportedOperationException("not used in this test");
    }

    @Override
    public boolean existsPermissionsByNames(List<String> permissionNames) {
      throw new UnsupportedOperationException("not used in this test");
    }

    @Override
    public Role getRoleById(long roleID) {
      throw new UnsupportedOperationException("not used in this test");
    }

    @Override
    public Role saveRole(Role role) {
      throw new UnsupportedOperationException("not used in this test");
    }
  }

  /** No-op {@link UserRoleManager}: {@code save()} only needs {@code saveUserRole} to echo its argument. */
  private static final class NoOpUserRoleManager implements UserRoleManager {

    @Override
    public void deleteUserRole(long userRoleId) {
      throw new UnsupportedOperationException("not used in this test");
    }

    @Override
    public boolean existUserRole(long userRoleID) {
      throw new UnsupportedOperationException("not used in this test");
    }

    @Override
    public List<UserRole> findAll() {
      throw new UnsupportedOperationException("not used in this test");
    }

    @Override
    public UserRole getUserRoleById(long userRoleID) {
      throw new UnsupportedOperationException("not used in this test");
    }

    @Override
    public List<UserRole> getUserRolesByRoleId(Long roleID) {
      throw new UnsupportedOperationException("not used in this test");
    }

    @Override
    public List<UserRole> getUserRolesByUserId(Long userId) {
      throw new UnsupportedOperationException("not used in this test");
    }

    @Override
    public UserRole saveUserRole(UserRole userRole) {
      return userRole;
    }
  }

  /** Records every {@link User} handed to {@code saveUser}, and reports "no existing user" otherwise. */
  private static final class FakeUserManager implements UserManager {

    private long nextId = 1;

    private User lastSavedUser;

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
      return this.lastSavedUser;
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
      user.setId(this.nextId++);
      this.lastSavedUser = user;
      return user;
    }

    @Override
    public List<User> searchUser(String searchValue) {
      throw new UnsupportedOperationException("not used in this test");
    }
  }

  /** Answers {@code findGlobalUnitByAcronym} with a fixture carrying the "G" guest role {@code save()} needs. */
  private static final class TestGlobalUnitManager implements GlobalUnitManager {

    private final GlobalUnit globalUnit;

    TestGlobalUnitManager() {
      Role guestRole = new Role();
      guestRole.setAcronym("G");
      Set<Role> roles = new HashSet<>();
      roles.add(guestRole);

      this.globalUnit = new GlobalUnit();
      this.globalUnit.setAcronym("TESTCRP");
      this.globalUnit.setRoles(roles);
    }

    @Override
    public List<GlobalUnit> crpUsers(String email) {
      throw new UnsupportedOperationException("not used in this test");
    }

    @Override
    public void deleteGlobalUnit(long globalUnitId) {
      throw new UnsupportedOperationException("not used in this test");
    }

    @Override
    public boolean existGlobalUnit(long globalUnitID) {
      throw new UnsupportedOperationException("not used in this test");
    }

    @Override
    public List<GlobalUnit> findAll() {
      throw new UnsupportedOperationException("not used in this test");
    }

    @Override
    public GlobalUnit findGlobalUnitByAcronym(String acronym) {
      return this.globalUnit;
    }

    @Override
    public GlobalUnit findGlobalUnitBySMOCode(String smoCode) {
      throw new UnsupportedOperationException("not used in this test");
    }

    @Override
    public GlobalUnit getGlobalUnitById(long globalUnitID) {
      throw new UnsupportedOperationException("not used in this test");
    }

    @Override
    public GlobalUnit saveGlobalUnit(GlobalUnit globalUnit) {
      throw new UnsupportedOperationException("not used in this test");
    }
  }

  /**
   * Bypasses only the infrastructure this migration does not touch: Shiro authorization, the Struts
   * action-name lookup, and the mail-merge/PDF machinery in {@code sendMailNewUser} (which this test
   * captures the generated password from instead of exercising). Everything DIRABS-T06 changed —
   * {@code save()}'s directory lookup and field mapping — runs unmodified.
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

  private static final class TestableCrpUsersAction extends CrpUsersAction {

    private static final long serialVersionUID = 1L;

    private String capturedPassword;

    TestableCrpUsersAction(APConfig config, GlobalUnitManager globalUnitManager, CrpUserManager crpUserManager,
      UserManager userManager, ProjectManager projectManager, PhaseManager phaseManager, RoleManager roleManager,
      UserRoleManager userRoleManager, SendMailS sendMailS, GuestUsersValidator validator,
      DirectoryService directoryService) {
      super(config, globalUnitManager, crpUserManager, userManager, projectManager, phaseManager, roleManager,
        userRoleManager, sendMailS, validator, directoryService,
        unconfiguredManager(CustomParameterManager.class), unconfiguredManager(ParameterManager.class));
    }

    @Override
    public boolean canAcessCrpAdmin() {
      return true;
    }

    @Override
    public String getActionName() {
      return "crp/manageCrpUsers";
    }

    @Override
    public void sendMailNewUser(User user, GlobalUnit loggedCrp, String password) throws NoSuchAlgorithmException {
      this.capturedPassword = password;
    }

    /**
     * Struts' real {@code getText} needs a live container ({@code ActionContext.getContext()} is null
     * outside a Struts request), which this hand-rolled test does not stand up. Returning the key
     * itself is deterministic and does not affect any assertion in this suite, none of which inspects
     * resolved message text.
     */
    @Override
    public String getText(String aTextName) {
      return aTextName;
    }

    @Override
    public String getText(String aTextName, String[] args) {
      return aTextName;
    }
  }
}
