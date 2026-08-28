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

import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.security.directory.DirectoryPerson;
import org.cgiar.ccafs.marlo.security.directory.DirectoryService;
import org.cgiar.ccafs.marlo.security.directory.DirectorySource;
import org.cgiar.ccafs.marlo.security.directory.FakeDirectoryService;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.lang.reflect.Field;
import java.util.List;

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

  /** Records how many times {@code saveUser} runs, and can simulate a failed save (id stays 0). */
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
