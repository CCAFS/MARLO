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

package org.cgiar.ccafs.marlo.action.center.capdev;

import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.AdUserManager;
import org.cgiar.ccafs.marlo.data.model.AdUser;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.opensymphony.xwork2.Action;
import org.apache.struts2.dispatcher.Parameter;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Drives {@link ContactPersonAction#searchADUser()} — the {@code searchContact.do} endpoint
 * ({@code struts-json.xml:1041}) — against a stubbed {@link AdUserManager} to prove that DIRABS-T14's
 * deletion of the dead Active Directory construction left the payload's shape and values unchanged.
 * Note the deliberate limit: these tests never invoke the JSON result and never assert {@code HashMap}
 * key order, so the emitted JSON's <em>field order</em> is NOT proven identical, and no claim of a
 * byte-identical response is made here:
 * requirements.md {@code DIRABS-FN-008} *"the returned `users` list MUST have the same map keys and
 * values as today, sourced from `adUsermanager.searchUsers(queryParameter)`"*.
 * <p>
 * T14 was gated on a compile and a diff. Neither can see the payload, so every assertion here is on
 * the shape and the values of what {@link ContactPersonAction#getUsers()} hands the JSON result — the
 * exact key set (four keys, no more), the value traced to the stub row that produced it, and the
 * {@code idUser} counter's origin.
 * <p>
 * {@code getParameters()} normally reads the live Struts {@code ActionContext}, which this hand-rolled
 * test does not stand up; {@link TestableContactPersonAction} overrides it with a map the test fills.
 * Everything below that override — the loop, the counter, the four {@code put} calls, the null guard —
 * is real, unmodified production code.
 * <p>
 * This file deliberately imports no {@code org.cgiar.ciat} type. DIRABS-T16 re-runs the import gate and
 * requires {@code marlo-web/src/test} to hold exactly one such importer ({@code LdapDirectoryServiceTest},
 * permitted by DD-12).
 */
public class ContactPersonActionTest {

  /**
   * The four keys {@code searchADUser()} puts into every row. tasks.md T15 writes this set with a
   * trailing ellipsis; the source has no fifth key, and a test that tolerated one would not gate the
   * shape.
   */
  private static final Set<String> EXPECTED_KEYS =
    new HashSet<>(Arrays.asList("idUser", "firstName", "lastName", "email"));

  /**
   * Scanned for in {@code ContactPersonAction}'s compiled constant pool. Kept as literals rather than
   * as imports on purpose: importing an {@code adauth} type here is exactly what DIRABS-T16's
   * {@code src/test} gate forbids.
   */
  private static final String AD_PACKAGE_INTERNAL = "org/cgiar/ciat";

  private static final String AD_PACKAGE_DOTTED = "org.cgiar.ciat";

  private static final String COMPILED_CLASS =
    "/org/cgiar/ccafs/marlo/action/center/capdev/ContactPersonAction.class";

  private FakeAdUserManager adUserManager;
  private Map<String, Parameter> requestParameters;
  private TestableContactPersonAction action;

  private static AdUser adUser(String login, String firstName, String lastName, String email) {
    AdUser user = new AdUser();
    user.setLogin(login);
    user.setFirstName(firstName);
    user.setLastName(lastName);
    user.setEmail(email);
    return user;
  }

  /**
   * Asserts a row's key set exactly and every one of its four values. Key lookup, never iteration
   * order: {@code userMap} is a {@code HashMap}, so the order its keys come out in is not a promise the
   * production code makes and must not be asserted.
   */
  private static void assertRow(Map<String, Object> row, int expectedIdUser, String expectedFirstName,
    String expectedLastName, String expectedEmail) {
    assertEquals("the row must carry exactly these four keys - no more, no fewer, none renamed",
      EXPECTED_KEYS, row.keySet());
    assertEquals("idUser must be an Integer, as the JSON payload has always carried it", Integer.class,
      row.get("idUser").getClass());
    assertEquals(Integer.valueOf(expectedIdUser), row.get("idUser"));
    assertEquals(expectedFirstName, row.get("firstName"));
    assertEquals(expectedLastName, row.get("lastName"));
    assertEquals(expectedEmail, row.get("email"));
  }

  @Before
  public void setUp() throws Exception {
    this.adUserManager = new FakeAdUserManager();
    this.requestParameters = new HashMap<>();
    this.action = new TestableContactPersonAction(new APConfig(), this.adUserManager, this.requestParameters);
    this.setQueryParameter("smith");
  }

  private void setQueryParameter(String value) {
    this.requestParameters.put(APConstants.QUERY_PARAMETER,
      new Parameter.Request(APConstants.QUERY_PARAMETER, new String[] {value}));
  }

  /**
   * FN-008: the row shape and every value in it. Two stub rows in, two maps out, each carrying exactly
   * {@code idUser}/{@code firstName}/{@code lastName}/{@code email} and the values of the row that
   * produced it. A version that dropped a {@code put}, renamed a key, or crossed two rows' values fails
   * here.
   * <p>
   * The stub rows also set {@code login} — a field {@code AdUser} has and the payload deliberately does
   * not carry. The exact-key-set assertion is what keeps it out.
   */
  @Test
  public void twoStubRowsProduceTwoMapsWithExactKeysAndTracedValues() throws Exception {
    this.adUserManager.setResult(Arrays.asList(adUser("jsmith", "Jane", "Smith", "j.smith@cgiar.org"),
      adUser("bjones", "Bob", "Jones", "b.jones@cgiar.org")));

    String result = this.action.searchADUser();

    assertEquals(Action.SUCCESS, result);
    List<Map<String, Object>> users = this.action.getUsers();
    assertNotNull(users);
    assertEquals(2, users.size());
    // List order is the loop's order over the manager's result, and IS a promise - unlike HashMap key order.
    assertRow(users.get(0), 1, "Jane", "Smith", "j.smith@cgiar.org");
    assertRow(users.get(1), 2, "Bob", "Jones", "b.jones@cgiar.org");
  }

  /**
   * FN-008, the {@code idUser} counter: {@code idUser++} runs before the {@code put}, so the first row
   * is 1 and not 0. Three rows rather than two, so an off-by-one and a stalled counter are separable.
   */
  @Test
  public void idUserCounterStartsAtOneAndIncrementsPerRow() throws Exception {
    this.adUserManager.setResult(Arrays.asList(adUser("a", "A", "One", "a@cgiar.org"),
      adUser("b", "B", "Two", "b@cgiar.org"), adUser("c", "C", "Three", "c@cgiar.org")));

    this.action.searchADUser();

    List<Map<String, Object>> users = this.action.getUsers();
    assertEquals(3, users.size());
    assertEquals("the counter is pre-incremented, so the first row is 1 - not 0, not the list index",
      Integer.valueOf(1), users.get(0).get("idUser"));
    assertEquals(Integer.valueOf(2), users.get(1).get("idUser"));
    assertEquals(Integer.valueOf(3), users.get(2).get("idUser"));
  }

  /**
   * FN-008: the list the payload serializes is the one built from {@code adUsermanager.searchUsers}, so
   * the query the manager receives is part of the contract. {@code searchADUser()} trims twice
   * ({@code StringUtils.trim} then {@code String.trim}); a padded fixture is the falsifying input for
   * both.
   */
  @Test
  public void trimmedQueryParameterIsWhatReachesTheManager() throws Exception {
    this.setQueryParameter("  smith  ");
    this.adUserManager.setResult(new ArrayList<AdUser>());

    this.action.searchADUser();

    assertEquals(1, this.adUserManager.searchUsersCallCount);
    assertEquals("smith", this.adUserManager.lastSearchParameter);
  }

  /**
   * FN-008, the empty-result path: no rows means an empty list, not a null one and not a stale one.
   * The JSON result serializes {@code users} either way, so the distinction is user-visible.
   */
  @Test
  public void emptyManagerResultProducesAnEmptyUsersList() throws Exception {
    this.adUserManager.setResult(new ArrayList<AdUser>());

    String result = this.action.searchADUser();

    assertEquals(Action.SUCCESS, result);
    assertNotNull("an empty result must still leave a non-null list for the JSON result to serialize",
      this.action.getUsers());
    assertTrue(this.action.getUsers().isEmpty());
  }

  /**
   * FN-008, the null-result branch: {@code users} is assigned a fresh {@code ArrayList} before the
   * {@code ad_users != null} guard, so a null from the manager yields {@code []} rather than {@code null}.
   * Not named in tasks.md T15, but it is a real branch in the method and the difference between an empty
   * JSON array and a null field.
   */
  @Test
  public void nullManagerResultProducesAnEmptyUsersListNotNull() throws Exception {
    this.adUserManager.setResult(null);

    String result = this.action.searchADUser();

    assertEquals(Action.SUCCESS, result);
    assertNotNull("a null manager result must not propagate as a null users list", this.action.getUsers());
    assertTrue(this.action.getUsers().isEmpty());
  }

  /**
   * FN-008 *"no `LDAPService` and no `ADConexion` MUST be constructed"*, checked where it cannot be
   * argued: {@code ContactPersonAction}'s own constant pool. A runtime assertion could not see this —
   * the {@code adauth} classes are still on the test classpath, so a restored {@code new LDAPService()}
   * would execute happily and every other test here would stay green. The compiled class is the only
   * place the absence is visible, and it covers the deleted imports and the deleted constructions alike.
   */
  @Test
  public void compiledActionReferencesNoActiveDirectoryType() throws Exception {
    byte[] bytecode;
    try (InputStream in = ContactPersonActionTest.class.getResourceAsStream(COMPILED_CLASS)) {
      assertNotNull("could not locate " + COMPILED_CLASS + " on the test classpath", in);
      bytecode = in.readAllBytes();
    }
    // ISO-8859-1 is a byte-preserving decode, so every constant-pool byte survives into the haystack.
    String constantPool = new String(bytecode, StandardCharsets.ISO_8859_1);
    assertFalse("ContactPersonAction must reference no Active Directory type (internal form)",
      constantPool.contains(AD_PACKAGE_INTERNAL));
    assertFalse("ContactPersonAction must reference no Active Directory type (dotted form)",
      constantPool.contains(AD_PACKAGE_DOTTED));
  }

  /**
   * Supplies the request parameters {@code searchADUser()} reads. The real {@code getParameters()} calls
   * {@code ActionContext.getContext().getParameters()}, which is null outside a Struts request.
   */
  private static final class TestableContactPersonAction extends ContactPersonAction {

    private static final long serialVersionUID = 1L;

    private final Map<String, Parameter> testParameters;

    TestableContactPersonAction(APConfig config, AdUserManager adUsermanager,
      Map<String, Parameter> testParameters) {
      super(config, adUsermanager);
      this.testParameters = testParameters;
    }

    @Override
    public Map<String, Parameter> getParameters() {
      return this.testParameters;
    }
  }

  /** Returns a configurable {@code searchUsers} result and records the query it was called with. */
  private static final class FakeAdUserManager implements AdUserManager {

    private List<AdUser> result = new ArrayList<>();

    private String lastSearchParameter;

    private int searchUsersCallCount;

    @Override
    public void deleteAdUser(long adUserId) {
      throw new UnsupportedOperationException("not used in this test");
    }

    @Override
    public boolean existAdUser(long adUserID) {
      throw new UnsupportedOperationException("not used in this test");
    }

    @Override
    public List<AdUser> findAll() {
      throw new UnsupportedOperationException("not used in this test");
    }

    @Override
    public AdUser findByUserEmail(String email) {
      throw new UnsupportedOperationException("not used in this test");
    }

    @Override
    public AdUser getAdUserById(long adUserID) {
      throw new UnsupportedOperationException("not used in this test");
    }

    @Override
    public AdUser saveAdUser(AdUser adUser) {
      throw new UnsupportedOperationException("not used in this test");
    }

    @Override
    public List<AdUser> searchUsers(String parameter) {
      this.searchUsersCallCount++;
      this.lastSearchParameter = parameter;
      return this.result;
    }

    private void setResult(List<AdUser> result) {
      this.result = result;
    }
  }
}
