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

package org.cgiar.ccafs.marlo.security;

import org.cgiar.ccafs.marlo.data.dao.mysql.UserMySQLDAO;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.security.CognitoIdentityMapper.RejectionReason;
import org.cgiar.ccafs.marlo.security.CognitoIdentityMapper.Result;
import org.cgiar.ccafs.marlo.security.impl.CognitoIdentityMapperImpl;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Covers CHG-COGNITO-AUTH-001-T07: {@link CognitoIdentityMapperImpl} resolving a validated {@link
 * CognitoAssertion} to a {@code users} row and applying gates 1-3 of design.md 13.1, in order.
 * <p>
 * <b>Mutation proof (T07's own <i>Fails when</i> clause), run manually and recorded in {@code
 * execution.md}, not encoded as a standing {@code @Test} that would fail on purpose:</b> deleting the
 * {@code is_cgiar_user} check in {@link CognitoIdentityMapperImpl#map(CognitoAssertion)} flips the
 * account's authentication outcome from refused to wrongly accepted, which turns
 * {@link #nonCgiarAccountIsRefusedOnGateTwoNotMembership()} from passing to failing -- and restoring the
 * gate turns it back to passing. The same discipline {@code CognitoTokenValidatorTest} and
 * {@code APCustomRealmDispatchTest} already established in this spec.
 * <p>
 * Every collaborator is a hand-rolled double: MARLO has no mocking framework ({@code DEC-005} is
 * {@code PENDING}), matching {@code APCustomRealmDispatchTest} and {@code CognitoAssertionTest}.
 * <p>
 * <b>Normalization (T07's rewritten <i>Not evidence when</i> clause).</b> {@link
 * #differentCasingStillResolvesTheSameRow()} passes a claim whose email differs only in casing from the
 * stored row and must still resolve it -- a test that only ever passed an already-clean claim would prove
 * nothing about OQ-9's normalization. It calls the real {@code UserMySQLDAO.normalizeEmail} inside its
 * double rather than a boolean flag the double invents, so it reddens if that production method regresses
 * (see the audit correction on {@code RecordingUserManager} below). The surrounding-whitespace half of the
 * same normalization is exercised directly in
 * {@code org.cgiar.ccafs.marlo.data.dao.mysql.UserMySQLDAOEmailNormalizationTest}: {@link CognitoAssertion}
 * already trims on construction (T04), so a whitespace-carrying claim can never reach this resolver in
 * production -- only {@code UserMySQLDAO.normalizeEmail} sees the un-trimmed value a non-Cognito caller
 * (e.g. the local-login username field) can still supply.
 * <p>
 * <b>CHG-COGNITO-AUTH-001-T17 (U-3), 2026-09-04 -- FN-006 amended, this class's username write removed.</b>
 * A real corporate login proved the {@code cognito:username} claim T07 mapped is Cognito's own federated
 * identifier ({@code cgiar-azuread_c.gamboa@cgiar.org}), never the CGIAR/AD login T07's requirement assumed
 * ({@code cgamboa}) -- see {@code requirements.md} FN-006's amendment and {@code execution.md} 31. The
 * mapper now leaves {@code users.username} exactly as it found it; the LDAP path
 * ({@code APCustomRealm.getCgiarNickname()}) remains the only writer, resolving Active Directory **by
 * email** on a local login. The T07 test that asserted the write happened,
 * {@code validCgiarUserGetsUsernameLoweredAndEmailUnchanged}, is **updated, not deleted**: it survives
 * below as {@link #populatedUsernameIsPreservedByteForByte()}, its assertions inverted from "the claim
 * overwrites the username" to "the prior value is untouched". {@link #nullUsernameStaysNull()} and
 * {@link #blankUsernameStaysBlank()} are new, covering FN-006's other two amended scenarios.
 */
public class CognitoIdentityMappingTest {

  private static final Instant ISSUED_AT = Instant.parse("2026-09-01T10:00:00Z");

  /**
   * Registers users by exact (already-normalized) email and records every {@code saveLastLogin} call.
   * {@code saveUser} throws: {@code AbstractMarloDAO.update(T)} returns before {@code merge()} for an
   * entity the session already contains, which every {@code User} handed to this double is, since it came
   * from a lookup -- so a production regression back to {@code saveUser()} is a no-op write that this
   * double turns into a loud test failure instead of a silent one.
   */
  private static final class RecordingUserManager implements UserManager {

    private final List<User> byExactEmail = new ArrayList<>();
    private final List<User> lastLoginSaves = new ArrayList<>();

    void register(User user) {
      this.byExactEmail.add(user);
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
      // Calls the REAL UserMySQLDAO.normalizeEmail on both sides -- package-visible from this test tree,
      // no SessionFactory needed -- rather than a double-invented flag. This is the audit correction on
      // Issue 4: a double that decides for itself how matching works can pass even if normalizeEmail is
      // deleted; this one cannot.
      String normalizedInput = UserMySQLDAO.normalizeEmail(email);
      if (normalizedInput == null) {
        return null;
      }
      for (User candidate : this.byExactEmail) {
        if (UserMySQLDAO.normalizeEmail(candidate.getEmail()).equals(normalizedInput)) {
          return candidate;
        }
      }
      return null;
    }

    @Override
    public User getUserByUsername(String username) {
      throw new UnsupportedOperationException("this suite only exercises the email lookup branch");
    }

    @Override
    public User getActiveSuperAdminUserByUsernameOccurrence() {
      throw new UnsupportedOperationException("not needed by this suite");
    }

    @Override
    public User login(String email, String password) {
      throw new UnsupportedOperationException("not needed by this suite");
    }

    @Override
    public boolean saveLastLogin(User user) {
      this.lastLoginSaves.add(user);
      return true;
    }

    @Override
    public User saveUser(User user) {
      throw new AssertionError(
        "the username write must go through saveLastLogin(), not saveUser() -- see tasks.md's "
          + "corrected Constitutional check: saveUser() is a no-op on a session-managed entity");
    }

    @Override
    public List<User> searchUser(String searchValue) {
      throw new UnsupportedOperationException("not needed by this suite");
    }
  }

  /** Throws on any write -- used to prove gate 1's refusal never attempts one (FN-002). */
  private static final class ExplodingWriteUserManager implements UserManager {

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
      return null;
    }

    @Override
    public User getUserByUsername(String username) {
      throw new UnsupportedOperationException("not needed by this suite");
    }

    @Override
    public User getActiveSuperAdminUserByUsernameOccurrence() {
      throw new UnsupportedOperationException("not needed by this suite");
    }

    @Override
    public User login(String email, String password) {
      throw new UnsupportedOperationException("not needed by this suite");
    }

    @Override
    public boolean saveLastLogin(User user) {
      throw new AssertionError("gate 1 (no auto-provisioning) must never write a users row");
    }

    @Override
    public User saveUser(User user) {
      throw new AssertionError("gate 1 (no auto-provisioning) must never write a users row");
    }

    @Override
    public List<User> searchUser(String searchValue) {
      throw new UnsupportedOperationException("not needed by this suite");
    }
  }

  private static User cgiarUser(long id, String email, String username, boolean active) {
    User user = new User();
    user.setId(id);
    user.setEmail(email);
    user.setUsername(username);
    user.setCgiarUser(true);
    user.setActive(active);
    return user;
  }

  /**
   * T07 test 1: an unknown claim is refused on gate 1, and no {@code users} row is created -- FN-002's
   * "MUST NOT auto-provision". {@link ExplodingWriteUserManager} throws on either write method, so
   * reaching the assertions below without an {@link AssertionError} is itself the proof no write was
   * attempted.
   */
  @Test
  public void unknownClaimIsRefusedAndCreatesNoRow() {
    CognitoIdentityMapper mapper = new CognitoIdentityMapperImpl(new ExplodingWriteUserManager());
    CognitoAssertion assertion =
      new CognitoAssertion("sub-unknown-1", "nobody@cgiar.org", "nobody", ISSUED_AT);

    Result result = mapper.map(assertion);

    assertFalse("an unknown claim must be refused", result.isAccepted());
    assertEquals(RejectionReason.ACCOUNT_NOT_FOUND, result.getRejectionReason());
  }

  /**
   * T07 test 2: {@code is_cgiar_user = 0} with a matching CGIAR-shaped email is refused on gate 2
   * (SEC-006), not on membership -- {@code crp_users} is gate 4 and this mapper never touches it.
   * <p>
   * <b>This is the mutation-proof test.</b> Comment out the {@code isCgiarUser()} check in
   * {@link CognitoIdentityMapperImpl#map(CognitoAssertion)} and this test's
   * {@code assertFalse(result.isAccepted())} line fails -- the account authenticates, which is exactly
   * the bypass Judgment Day found. Restoring the gate turns it back to passing.
   */
  @Test
  public void nonCgiarAccountIsRefusedOnGateTwoNotMembership() {
    User localAccountWithCgiarEmail = new User();
    localAccountWithCgiarEmail.setId(55L);
    localAccountWithCgiarEmail.setEmail("jane.local@cgiar.org");
    localAccountWithCgiarEmail.setUsername("jane.local");
    localAccountWithCgiarEmail.setCgiarUser(false);
    localAccountWithCgiarEmail.setActive(true);

    RecordingUserManager userManager = new RecordingUserManager();
    userManager.register(localAccountWithCgiarEmail);
    CognitoIdentityMapper mapper = new CognitoIdentityMapperImpl(userManager);

    CognitoAssertion assertion =
      new CognitoAssertion("sub-local-55", "jane.local@cgiar.org", "janelocal", ISSUED_AT);

    Result result = mapper.map(assertion);

    assertFalse("a local account must not be unlocked by a federated identity", result.isAccepted());
    assertEquals("the refusal must be attributed to gate 2, not to crp_users membership",
      RejectionReason.NOT_CGIAR_ACCOUNT, result.getRejectionReason());
  }

  /**
   * T07 test 3: {@code is_active = false} is refused with {@code USER_DISABLED}, treating the local flag
   * as authoritative regardless of IdP state.
   */
  @Test
  public void inactiveAccountIsRefusedWithUserDisabled() {
    User disabledCgiarUser = cgiarUser(77L, "retired.cgiar@cgiar.org", "retiredcgiar", false);
    RecordingUserManager userManager = new RecordingUserManager();
    userManager.register(disabledCgiarUser);
    CognitoIdentityMapper mapper = new CognitoIdentityMapperImpl(userManager);

    CognitoAssertion assertion =
      new CognitoAssertion("sub-retired-77", "retired.cgiar@cgiar.org", "retiredcgiar", ISSUED_AT);

    Result result = mapper.map(assertion);

    assertFalse("an inactive account must be refused", result.isAccepted());
    assertEquals(RejectionReason.USER_DISABLED, result.getRejectionReason());
  }

  /**
   * CHG-COGNITO-AUTH-001-T17, test 1 -- a populated {@code users.username} is preserved **byte-for-byte**
   * across a successful Cognito login. Formerly {@code validCgiarUserGetsUsernameLoweredAndEmailUnchanged},
   * which asserted the opposite: that the claim overwrote the username. That assertion is inverted here,
   * not deleted -- FN-006's amendment records that the write it proved was the U-3 defect, not the
   * contract. The assertion carries a realistic **federated** {@code usernameClaim}
   * ({@code cgiar-azuread_c.gamboa@cgiar.org}, the exact shape U-3's real token produced) specifically so
   * this test cannot pass by accident on a claim that happens to already equal the stored value -- if the
   * write ever returns, this claim would silently clobber {@code cgamboa} with the federated string, and
   * this assertion must catch it.
   * <p>
   * {@code users.email} is also asserted unchanged (FN-006's "MUST NOT overwrite users.email"), and no
   * {@code saveLastLogin} call is made -- this mapper no longer writes anything for a valid, active
   * account.
   */
  @Test
  public void populatedUsernameIsPreservedByteForByte() {
    User validCgiarUser = cgiarUser(99L, "carlos.cgiar@cgiar.org", "cgamboa", true);
    RecordingUserManager userManager = new RecordingUserManager();
    userManager.register(validCgiarUser);
    CognitoIdentityMapper mapper = new CognitoIdentityMapperImpl(userManager);

    CognitoAssertion assertion = new CognitoAssertion("sub-carlos-99", "carlos.cgiar@cgiar.org",
      "cgiar-azuread_c.gamboa@cgiar.org", ISSUED_AT);

    Result result = mapper.map(assertion);

    assertTrue("a valid, active CGIAR account must be accepted", result.isAccepted());
    assertEquals(Long.valueOf(99L), result.getUserId());
    assertEquals("username must be left exactly as it was -- no claim, federated or otherwise, may "
      + "overwrite it", "cgamboa", validCgiarUser.getUsername());
    assertEquals("email must never be overwritten", "carlos.cgiar@cgiar.org", validCgiarUser.getEmail());
    assertTrue("nothing about username is written any more, so saveLastLogin must not be called here",
      userManager.lastLoginSaves.isEmpty());
  }

  /**
   * CHG-COGNITO-AUTH-001-T17, test 2 -- a {@code null} {@code users.username} stays {@code null}. The
   * claim carries a federated value, exactly as in the previous test, to prove nothing is invented from
   * it.
   */
  @Test
  public void nullUsernameStaysNull() {
    User cgiarUserWithNoUsername = cgiarUser(101L, "noname.cgiar@cgiar.org", null, true);
    RecordingUserManager userManager = new RecordingUserManager();
    userManager.register(cgiarUserWithNoUsername);
    CognitoIdentityMapper mapper = new CognitoIdentityMapperImpl(userManager);

    CognitoAssertion assertion = new CognitoAssertion("sub-noname-101", "noname.cgiar@cgiar.org",
      "cgiar-azuread_no.name@cgiar.org", ISSUED_AT);

    Result result = mapper.map(assertion);

    assertTrue("a valid, active CGIAR account must be accepted", result.isAccepted());
    assertEquals("a null username must stay null -- nothing is invented", null,
      cgiarUserWithNoUsername.getUsername());
    assertTrue("no write must occur for a null username either",
      userManager.lastLoginSaves.isEmpty());
  }

  /**
   * CHG-COGNITO-AUTH-001-T17, test 3 -- a blank {@code users.username} stays blank: a distinct case from
   * {@code null} above, and one FN-006's amendment calls out explicitly so a blank does not quietly become
   * a derived value.
   */
  @Test
  public void blankUsernameStaysBlank() {
    User cgiarUserWithBlankUsername = cgiarUser(102L, "blank.cgiar@cgiar.org", "", true);
    RecordingUserManager userManager = new RecordingUserManager();
    userManager.register(cgiarUserWithBlankUsername);
    CognitoIdentityMapper mapper = new CognitoIdentityMapperImpl(userManager);

    CognitoAssertion assertion = new CognitoAssertion("sub-blank-102", "blank.cgiar@cgiar.org",
      "cgiar-azuread_blank@cgiar.org", ISSUED_AT);

    Result result = mapper.map(assertion);

    assertTrue("a valid, active CGIAR account must be accepted", result.isAccepted());
    assertEquals("a blank username must stay blank, distinct from null, and must not be derived either",
      "", cgiarUserWithBlankUsername.getUsername());
    assertTrue("no write must occur for a blank username either",
      userManager.lastLoginSaves.isEmpty());
  }

  /**
   * T07 test 5 (SEC-006's non-disclosure clause): the i18n key a gate-2 refusal renders as is identical to
   * the key a gate-1 refusal (unknown account) renders as -- no oracle telling a caller which
   * authentication mode an email uses. {@code USER_DISABLED} is deliberately excluded: FN-002 names it as
   * the existing, distinct message, unrelated to SEC-006's federated-identity concern.
   * <p>
   * Audit correction on Issue 3: the shared key is {@code login.error.cognitoNotEligible} -- the same key
   * T08's {@code CognitoLoginAction} already uses for its own two analogous refusals on this Cognito path
   * -- not {@code ADLoginMessages.ERROR_LOGON_FAILURE}, which is legacy local-password text and a hard
   * rule 8 violation (a hardcoded literal) besides. Both keys are verified present in
   * {@code global.properties} (lines 1567 and 1597).
   */
  @Test
  public void gateTwoRefusalKeyIsIndistinguishableFromGenericFailure() {
    String unknownAccountKey = RejectionReason.ACCOUNT_NOT_FOUND.toMessageKey();
    String notCgiarAccountKey = RejectionReason.NOT_CGIAR_ACCOUNT.toMessageKey();

    assertEquals("gate 1 and gate 2 must render the identical i18n key, or a caller could distinguish "
      + "'no such user' from 'not a CGIAR account'", unknownAccountKey, notCgiarAccountKey);
    assertEquals("login.error.cognitoNotEligible", unknownAccountKey);
    assertFalse("the shared generic key must differ from the disabled-account key",
      unknownAccountKey.equals(RejectionReason.USER_DISABLED.toMessageKey()));
    assertEquals("login.error.inactive", RejectionReason.USER_DISABLED.toMessageKey());
  }

  /**
   * T07's rewritten <i>Not evidence when</i> clause: a claim whose email differs only in casing from the
   * stored row must still resolve it. {@link RecordingUserManager#getUserByEmail(String)} calls the real
   * {@code UserMySQLDAO.normalizeEmail} rather than a double-invented case-insensitive flag (audit
   * correction on Issue 4) -- so this test reddens if that production method regresses, which a
   * self-fulfilling double could not.
   */
  @Test
  public void differentCasingStillResolvesTheSameRow() {
    User storedUser = cgiarUser(303L, "priya.cgiar@cgiar.org", "priyacgiar", true);
    RecordingUserManager userManager = new RecordingUserManager();
    userManager.register(storedUser);
    CognitoIdentityMapper mapper = new CognitoIdentityMapperImpl(userManager);

    CognitoAssertion assertion =
      new CognitoAssertion("sub-priya-303", "Priya.CGIAR@Cgiar.Org", "priyacgiar", ISSUED_AT);

    Result result = mapper.map(assertion);

    assertTrue("a differently-cased claim must resolve the same row as an already-clean one",
      result.isAccepted());
    assertEquals(Long.valueOf(303L), result.getUserId());
  }

  /** Constructor guard: a {@code null} {@link UserManager} must fail loudly at wiring time, not at login. */
  @Test
  public void constructorRejectsNullUserManager() {
    try {
      new CognitoIdentityMapperImpl(null);
      fail("expected IllegalArgumentException");
    } catch (IllegalArgumentException expected) {
      // expected
    }
  }
}
