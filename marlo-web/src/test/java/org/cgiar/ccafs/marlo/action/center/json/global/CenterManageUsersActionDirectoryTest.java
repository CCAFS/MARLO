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

package org.cgiar.ccafs.marlo.action.center.json.global;

import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.security.directory.DirectoryLookupException;
import org.cgiar.ccafs.marlo.security.directory.DirectoryPerson;
import org.cgiar.ccafs.marlo.security.directory.DirectoryService;
import org.cgiar.ccafs.marlo.security.directory.DirectorySource;
import org.cgiar.ccafs.marlo.security.directory.FakeDirectoryService;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.lang.reflect.Field;
import java.util.List;

import org.apache.shiro.authz.AuthorizationException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

/**
 * Drives {@link ManageUsersAction#create()} with a {@link FakeDirectoryService} to prove the migration
 * off direct {@code adauth} usage (DIRABS-T10) preserves the action's observable behavior — design.md
 * §5.2, §6.2, DD-3, DD-3a; requirements.md {@code DIRABS-FN-002} *A caller that must not silently
 * degrade* and {@code DIRABS-FN-006} *center/json/global/ManageUsersAction*.
 * <p>
 * This is the only migrated consumer that reads {@code source}: it must distinguish a genuine
 * {@code NOT_FOUND} (returns {@code null}, which {@code create()} maps to
 * {@code manageUsers.email.doesNotExist}) from an {@code ERROR} (propagates a
 * {@link DirectoryLookupException} rather than reporting the same false message).
 * <p>
 * {@code newUser} and {@code actionName} are private fields with no setters — normally populated by
 * {@code prepare()} from Struts request parameters, which this test does not simulate. Reflection sets
 * them directly; every assertion below then runs against real, unmodified {@code create()} /
 * {@code validateOutlookUser()} / {@code addUser()} production code.
 */
public class CenterManageUsersActionDirectoryTest {

  private static final String EMAIL = "new.user@cgiar.org";

  private FakeDirectoryService directoryService;
  private FakeUserManager userManager;
  private TestableManageUsersAction action;

  /**
   * The parameter is deliberately {@code Object}, not {@code ManageUsersAction}: the symptom is
   * reproducible — {@code maven-surefire-plugin:2.12.4} (Maven 3's default; neither POM declares a
   * version) can crash the entire test fork with a bare {@code NoClassDefFoundError}, zero tests run.
   * <b>The cause is not verified.</b> The mechanism first written down at T06 was falsified by the
   * record: the class it names is demonstrably loadable in the same fork, and the single non-crashing
   * case varied two variables at once, so the outer-vs-nested claim is unlicensed. Observed-unsafe
   * ({@code n=1}): an <em>outer</em> test-class method whose parameter type is a {@code BaseAction}
   * <em>subclass</em> — {@code CrpUsersActionDirectoryTest} (whether a {@code BaseAction} subclass
   * <em>return</em> type has the same effect is unobserved). Observed-safe ({@code n=1} each): a
   * <em>nested</em> class taking {@code BaseAction} itself — see
   * {@code GuestUsersValidatorDirectoryTest:53-64}, which carries the corrected note; and no reflection
   * helper at all — {@code SearchUserActionDirectoryTest}. A nested signature taking a {@code BaseAction}
   * subclass is untested by either file and should be avoided until a case exists. Typing this helper as
   * {@code Object} avoids the observed-unsafe shape entirely.
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
    inject(this.action, "actionName", "center/createUser");
  }

  /**
   * FN-006 *center/json/global/ManageUsersAction*, found branch: {@code firstName}, {@code lastName}
   * and a lowercased {@code username} are set on the {@code newUser} FIELD, and the SAME instance is
   * returned and carried through to the saved user — the side effect on the field is part of the
   * contract, so identity (not mere equality) is asserted. FN-004: the mixed-case login
   * ({@code "JSmith"}) is the falsifying input for `D1` — an already-lowercase fixture would pass
   * whether or not the real call site lowercases.
   */
  @Test
  public void foundEmailMutatesTheNewUserFieldAndReturnsTheSameInstance() throws Exception {
    this.directoryService.setMode(FakeDirectoryService.Mode.FOUND);
    this.directoryService
      .setResponse(DirectoryPerson.found(EMAIL, "JSmith", "Jane", "Smith", DirectorySource.LDAP));

    User originalUser = new User();
    originalUser.setEmail(EMAIL);
    inject(this.action, "newUser", originalUser);

    String result = this.action.create();

    assertEquals(ManageUsersAction.SUCCESS, result);

    Field newUserField = ManageUsersAction.class.getDeclaredField("newUser");
    newUserField.setAccessible(true);
    Object fieldValueAfterCreate = newUserField.get(this.action);

    assertSame("validateOutlookUser must mutate and return the SAME newUser field instance, "
      + "not a new User object", originalUser, fieldValueAfterCreate);
    assertEquals("login must be lowercased at the call site, not by the abstraction", "jsmith",
      originalUser.getUsername());
    assertEquals("Jane", originalUser.getFirstName());
    assertEquals("Smith", originalUser.getLastName());
    assertEquals("addUser() must have populated the users list", 1, this.action.getUsers().size());
  }

  /**
   * FN-002 *A caller that must not silently degrade* / FN-006: a genuine {@code NOT_FOUND} must still
   * return {@code null} from {@code validateOutlookUser}, which {@code create()} maps to
   * {@code manageUsers.email.doesNotExist} exactly as it does today.
   */
  @Test
  public void notFoundProducesTheDoesNotExistMessage() throws Exception {
    this.directoryService.setMode(FakeDirectoryService.Mode.NOT_FOUND);

    User newUser = new User();
    newUser.setEmail(EMAIL);
    inject(this.action, "newUser", newUser);

    String result = this.action.create();

    assertEquals(ManageUsersAction.SUCCESS, result);
    assertEquals("manageUsers.email.doesNotExist", this.action.getMessage());
    assertEquals("addUser() must never run on a genuine NOT_FOUND", 0, this.userManager.saveUserCallCount);
  }

  /**
   * FN-002 *A caller that must not silently degrade*: on {@code source == ERROR} the method MUST throw
   * {@link DirectoryLookupException} rather than return {@code null}. A broad {@code Exception}
   * assertion would pass for the wrong reason, so the specific type is required here.
   */
  @Test(expected = DirectoryLookupException.class)
  public void errorBranchThrowsDirectoryLookupException() throws Exception {
    this.directoryService.setMode(FakeDirectoryService.Mode.ERROR);

    User newUser = new User();
    newUser.setEmail(EMAIL);
    inject(this.action, "newUser", newUser);

    this.action.create();
  }

  /**
   * FN-002: distinct from the type assertion above, this confirms the actual defect DD-3 exists to
   * prevent — a backend failure must NOT be reported as {@code manageUsers.email.doesNotExist}, which
   * would tell an administrator that a real CGIAR employee does not exist. {@code create()}'s message
   * assignment must never be reached once the exception propagates out of {@code validateOutlookUser}.
   */
  @Test
  public void errorBranchNeverReportsEmailDoesNotExist() throws Exception {
    this.directoryService.setMode(FakeDirectoryService.Mode.ERROR);

    User newUser = new User();
    newUser.setEmail(EMAIL);
    inject(this.action, "newUser", newUser);

    try {
      this.action.create();
      fail("expected a DirectoryLookupException on source == ERROR");
    } catch (DirectoryLookupException expected) {
      // expected — fall through to the assertion below
    }

    assertNull("an ERROR must never surface as manageUsers.email.doesNotExist", this.action.getMessage());
    assertEquals("addUser() must never run when the lookup itself failed", 0,
      this.userManager.saveUserCallCount);
  }

  /**
   * DD-3a: {@code struts.xml:543-545} maps {@code org.apache.shiro.authz.AuthorizationException} to an
   * HTTP 403, not the 500 today's uncaught {@code adauth} exception produces. If
   * {@link DirectoryLookupException} extended it, the ERROR branch would silently render a 403 instead
   * of a 500 — a real behavior change this assertion pins shut.
   */
  @Test
  public void directoryLookupExceptionDoesNotExtendAuthorizationException() {
    assertFalse("DirectoryLookupException must not extend AuthorizationException (would map to 403, not 500)",
      AuthorizationException.class.isAssignableFrom(DirectoryLookupException.class));
  }

  /**
   * Struts' real {@code getText} needs a live container ({@code ActionContext.getContext()} is null
   * outside a Struts request), which this hand-rolled test does not stand up. Returning the key itself
   * is deterministic and lets every assertion above compare against the i18n key directly.
   */
  private static final class TestableManageUsersAction extends ManageUsersAction {

    private static final long serialVersionUID = 1L;

    TestableManageUsersAction(APConfig config, UserManager userManager, DirectoryService directoryService) {
      super(config, userManager, directoryService);
    }

    @Override
    public String getText(String aTextName) {
      return aTextName;
    }
  }

  /** Records how many times {@code saveUser} runs, mutating and returning the SAME instance it receives. */
  private static final class FakeUserManager implements UserManager {

    private long nextId = 1;

    private int saveUserCallCount;

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
      user.setId(this.nextId++);
      return user;
    }

    @Override
    public List<User> searchUser(String searchValue) {
      throw new UnsupportedOperationException("not used in this test");
    }
  }
}
