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
import org.cgiar.ccafs.marlo.security.directory.DirectorySource;
import org.cgiar.ccafs.marlo.security.directory.FakeDirectoryService;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Drives {@link SearchUserAction#execute()} with a {@link FakeDirectoryService} to prove the
 * migration off {@code BaseAction.getOutlookUser} (DIRABS-T09) preserves the action's observable
 * behavior exactly — design.md §6.2, requirements.md {@code DIRABS-FN-006} *SearchUserAction*.
 * <p>
 * Neither {@code userEmail} nor {@code userFound} needs reflection: both carry public
 * setters/getters, so this test drives real, unmodified {@code execute()} production code directly.
 */
public class SearchUserActionDirectoryTest {

  private FakeDirectoryService directoryService;
  private FakeUserManager userManager;
  private SearchUserAction action;

  @Before
  public void setUp() {
    this.directoryService = new FakeDirectoryService();
    this.userManager = new FakeUserManager();
    this.action = new SearchUserAction(new APConfig(), this.userManager, this.directoryService);
  }

  /**
   * FN-006 *SearchUserAction*, found branch: all nine {@code userFound} keys are present with the
   * exact values the current code produces. No order assertion — {@code userFound} is a
   * {@code HashMap} and the order clause was dropped 2026-08-28 (see requirements.md Decision Log).
   * FN-004: {@code login} is mixed-case ({@code "JSmith"}) so a fake or call site that skips
   * lowercasing would fail the {@code username} assertion below.
   */
  @Test
  public void foundCgiarEmailPopulatesAllNineKeysWithLowercasedUsername() throws Exception {
    this.directoryService.setMode(FakeDirectoryService.Mode.FOUND);
    this.directoryService.setResponse(
      DirectoryPerson.found("Jane.Smith@CGIAR.org", "JSmith", "Jane", "Smith", DirectorySource.LDAP));

    this.action.setUserEmail("Jane.Smith@CGIAR.org");
    String result = this.action.execute();

    assertEquals(SearchUserAction.SUCCESS, result);
    Map<String, Object> userFound = this.action.getUserFound();

    assertEquals("all nine keys must be present, no more, no fewer", 9, userFound.size());
    assertEquals(Boolean.TRUE, userFound.get("newUser"));
    assertEquals(-1, userFound.get("id"));
    assertEquals("Jane", userFound.get("name"));
    assertEquals("Smith", userFound.get("lastName"));
    assertEquals("login must be lowercased at the call site, not by the abstraction", "jsmith",
      userFound.get("username"));
    assertEquals("email must be lowercased at the call site, not by the abstraction", "jane.smith@cgiar.org",
      userFound.get("email"));
    assertEquals(Boolean.TRUE, userFound.get("cgiar"));
    assertEquals(Boolean.FALSE, userFound.get("active"));
    assertEquals(Boolean.FALSE, userFound.get("autosave"));

    assertEquals("the suffix guard must pass a single lowercased lookup through to the seam", 1,
      this.directoryService.getInvocationCount());
    assertEquals("jane.smith@cgiar.org", this.directoryService.getLastEmailReceived());
  }

  /**
   * FN-006 *SearchUserAction*: the not-found branch must not change — exactly the 3-key shape
   * ({@code newUser=false}, {@code cgiar=false}, {@code cgiarNoExist=true}).
   */
  @Test
  public void notFoundCgiarEmailProducesTheThreeKeyShape() throws Exception {
    this.directoryService.setMode(FakeDirectoryService.Mode.NOT_FOUND);

    this.action.setUserEmail("unknown@cgiar.org");
    String result = this.action.execute();

    assertEquals(SearchUserAction.SUCCESS, result);
    Map<String, Object> userFound = this.action.getUserFound();

    assertEquals("not-found must produce exactly the 3-key shape", 3, userFound.size());
    assertEquals(Boolean.FALSE, userFound.get("newUser"));
    assertEquals(Boolean.FALSE, userFound.get("cgiar"));
    assertEquals(Boolean.TRUE, userFound.get("cgiarNoExist"));
  }

  /**
   * FN-002 *Backend failure*: {@code SearchUserAction} reads only {@code found}, so {@code ERROR}
   * must behave identically to {@code NOT_FOUND} — same 3-key shape, same values.
   */
  @Test
  public void errorBehavesIdenticallyToNotFound() throws Exception {
    this.directoryService.setMode(FakeDirectoryService.Mode.ERROR);

    this.action.setUserEmail("unknown@cgiar.org");
    String result = this.action.execute();

    assertEquals(SearchUserAction.SUCCESS, result);
    Map<String, Object> userFound = this.action.getUserFound();

    assertEquals("ERROR must reach the same 3-key shape as NOT_FOUND", 3, userFound.size());
    assertEquals(Boolean.FALSE, userFound.get("newUser"));
    assertEquals(Boolean.FALSE, userFound.get("cgiar"));
    assertEquals(Boolean.TRUE, userFound.get("cgiarNoExist"));
  }

  /**
   * FN-006 *SearchUserAction*: the {@code APConstants.OUTLOOK_EMAIL} suffix guard must short-circuit
   * before the lookup for a non-{@code @cgiar.org} email — the 4-key third branch
   * ({@code newUser=true}, {@code id=-1}, lowercased {@code email}, {@code cgiar=false}) is produced
   * with **zero** {@code findByEmail} invocations, proving the guard sits before the seam.
   */
  @Test
  public void nonCgiarEmailNeverReachesTheDirectoryLookup() throws Exception {
    this.action.setUserEmail("Guest.User@example.com");
    String result = this.action.execute();

    assertEquals(SearchUserAction.SUCCESS, result);
    Map<String, Object> userFound = this.action.getUserFound();

    assertEquals("the third branch must produce exactly the 4-key shape", 4, userFound.size());
    assertEquals(Boolean.TRUE, userFound.get("newUser"));
    assertEquals(-1, userFound.get("id"));
    assertEquals("guest.user@example.com", userFound.get("email"));
    assertEquals(Boolean.FALSE, userFound.get("cgiar"));

    assertEquals("the suffix guard must short-circuit before the lookup seam is ever reached", 0,
      this.directoryService.getInvocationCount());
  }

  /** Answers {@code getUserByEmail} with {@code null} so {@code execute()} always reaches the guard branch. */
  private static final class FakeUserManager implements UserManager {

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
      throw new UnsupportedOperationException("not used in this test");
    }

    @Override
    public List<User> searchUser(String searchValue) {
      throw new UnsupportedOperationException("not used in this test");
    }
  }
}
