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

package org.cgiar.ccafs.marlo.validation.superadmin;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.security.directory.DirectoryPerson;
import org.cgiar.ccafs.marlo.security.directory.DirectoryService;
import org.cgiar.ccafs.marlo.security.directory.DirectorySource;
import org.cgiar.ccafs.marlo.security.directory.FakeDirectoryService;
import org.cgiar.ccafs.marlo.validation.BaseValidator;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * Proves the migration of {@link GuestUsersValidator} off its duplicate {@code getOutlookUser} onto
 * {@link DirectoryService} preserves its observable behavior — design.md &sect;6.2, DD-8, &sect;10.0
 * C-3; requirements.md {@code DIRABS-FN-006} <em>GuestUsersValidator</em>.
 * <p>
 * MARLO has no mocking framework (`DEC-005` `PENDING`), so this test uses a hand-rolled
 * {@link FakeDirectoryService} (T04) for the seam, a plain {@code new BaseAction()} for the Struts
 * action collaborator — {@code getFieldErrors()} and {@code getValidationMessage()} both start empty
 * on a freshly constructed instance, so {@link GuestUsersValidator#validate} never reaches
 * {@code action.getText(...)}, and no live i18n/Struts context is required — and a spy subclass of
 * {@link GuestUsersValidator} that overrides {@code validateGuestUsers} to capture its four arguments
 * instead of running it, since MARLO has no mocking framework to verify a real invocation.
 * <p>
 * <b>The parameter-type note that matters for this file:</b> {@code maven-surefire-plugin:2.12.4}
 * (Maven 3's default; neither POM declares a version) crashes the entire test fork with a bare
 * {@code NoClassDefFoundError} — see {@code CrpUsersActionDirectoryTest} (DIRABS-T06) for the first
 * occurrence and its workaround. The symptom is reproducible; <b>the cause is not verified</b> — the
 * mechanism first written down for T06 (an outer-class method scan) was already falsified by the
 * record, since the class it names is demonstrably loadable in the same fork. What the evidence
 * actually supports, kept deliberately narrow: observed-safe is a <em>nested</em> class declaring a
 * method whose parameter/field type is {@link BaseAction} <em>itself</em> — {@code n=1}, this file's
 * {@link SpyGuestUsersValidator}; observed-unsafe is an <em>outer</em> test-class method whose
 * parameter or return type is a {@link BaseAction} <em>subclass</em> — {@code n=1},
 * {@code CrpUsersActionDirectoryTest}. A nested signature taking a {@link BaseAction} subclass is
 * <b>untested</b> by either file and should be avoided until a case exists.
 */
public class GuestUsersValidatorDirectoryTest {

  private static final String EMAIL = "guest@cgiar.org";

  private FakeDirectoryService directoryService;

  @Before
  public void setUp() {
    this.directoryService = new FakeDirectoryService();
  }

  /**
   * FN-006 *GuestUsersValidator*: {@code isCGIARUser} MUST derive {@code true} from a found person,
   * and {@code validateGuestUsers} MUST still be invoked with the same action, user and acronym, and
   * with the derived {@code isCGIARUser} value — not the caller's original argument.
   */
  @Test
  public void foundPersonDerivesIsCgiarUserTrueAndPassesItToValidateGuestUsers() throws Exception {
    this.directoryService.setMode(FakeDirectoryService.Mode.FOUND);
    this.directoryService.setResponse(DirectoryPerson.found(EMAIL, "jsmith", "Jane", "Smith", DirectorySource.LDAP));

    SpyGuestUsersValidator validator = new SpyGuestUsersValidator(this.directoryService);
    BaseAction action = new BaseAction();
    action.setInvalidFields(new HashMap<String, String>());
    User user = new User();
    user.setEmail(EMAIL);

    validator.validate(action, user, "TESTCRP", false, true);

    assertEquals("validateGuestUsers must be invoked exactly once", 1, validator.invocationCount);
    assertTrue("isCGIARUser must derive true from a found person", validator.capturedIsCgiarUser);
    assertSame("the same action instance must reach validateGuestUsers", action, validator.capturedAction);
    assertSame("the same user instance must reach validateGuestUsers", user, validator.capturedUser);
    assertEquals("the acronym must pass through unchanged", "TESTCRP", validator.capturedAcronym);
  }

  /**
   * FN-006 *GuestUsersValidator*: {@code isCGIARUser} MUST derive {@code false} from a not-found
   * person, and {@code validateGuestUsers} MUST still receive that derived value.
   */
  @Test
  public void notFoundPersonDerivesIsCgiarUserFalse() throws Exception {
    this.directoryService.setMode(FakeDirectoryService.Mode.NOT_FOUND);

    SpyGuestUsersValidator validator = new SpyGuestUsersValidator(this.directoryService);
    BaseAction action = new BaseAction();
    action.setInvalidFields(new HashMap<String, String>());
    User user = new User();
    user.setEmail(EMAIL);

    validator.validate(action, user, "TESTCRP", true, true);

    assertEquals(1, validator.invocationCount);
    assertFalse("isCGIARUser must derive false from a not-found person", validator.capturedIsCgiarUser);
    assertSame(action, validator.capturedAction);
    assertSame(user, validator.capturedUser);
    assertEquals("TESTCRP", validator.capturedAcronym);
  }

  /**
   * FN-002 / FN-006: this consumer reads only {@code found}, so a backend {@code ERROR} MUST behave
   * identically to a genuine {@code NOT_FOUND} — same {@code isCGIARUser == false}, same downstream
   * call.
   */
  @Test
  public void errorBehavesIdenticallyToNotFound() throws Exception {
    this.directoryService.setMode(FakeDirectoryService.Mode.ERROR);

    SpyGuestUsersValidator validator = new SpyGuestUsersValidator(this.directoryService);
    BaseAction action = new BaseAction();
    action.setInvalidFields(new HashMap<String, String>());
    User user = new User();
    user.setEmail(EMAIL);

    validator.validate(action, user, "TESTCRP", true, true);

    assertEquals(1, validator.invocationCount);
    assertFalse("ERROR must be treated the same as NOT_FOUND by this consumer", validator.capturedIsCgiarUser);
  }

  /**
   * FN-006's field-injection clause: {@code config} MUST keep arriving through the inherited
   * {@code @Inject protected APConfig config} field on {@code BaseValidator:52-53}; the new
   * {@code @Inject} constructor MUST NOT shadow or otherwise hide it.
   * <p>
   * <b>What this proves and what it does not.</b> Constructing with {@code new} — the only option
   * available to a MARLO unit test — bypasses Spring entirely, so nothing here exercises Spring's
   * field-injection machinery. What this <em>does</em> prove is narrower and purely structural:
   * {@link GuestUsersValidator} declares no field of its own named {@code config} — so there is
   * nothing on this subclass that could shadow the inherited {@link BaseValidator} slot — and that
   * inherited field still carries the {@code @Inject} annotation and {@code protected} visibility
   * that let Spring populate it.
   * <p>
   * <b>This test does not, and cannot, cover the field-injection clause itself.</b> That requires a
   * Spring-managed instance, which MARLO's test setup cannot produce ({@code D8}).
   * <p>
   * <b>Nothing else covers it either — the clause is genuinely uncovered, and is recorded as such.</b>
   * <i>(Corrected 2026-08-29 by {@code /akili-validate}. This paragraph previously said the clause was
   * "reported as covered by DIRABS-T12's app-start check". {@code tasks.md} repudiated exactly that on
   * 2026-08-28: after T08 this class references {@code config} nowhere, so a green app start certifies
   * a <b>no-op</b> and is not evidence. Five loci were corrected then; this one — the only one in a
   * compiled file — was missed.)</i>
   * <p>
   * What <em>is</em> gated here is the falsifiable structural substitute: no subclass field shadows
   * {@code config}, and {@code BaseValidator}'s inherited field is still {@code @Inject}-annotated and
   * {@code protected}.
   */
  @Test
  public void configFieldIsNotShadowedByTheNewConstructor() throws Exception {
    for (Field declaredField : GuestUsersValidator.class.getDeclaredFields()) {
      assertFalse("GuestUsersValidator must not declare its own field named config: doing so would "
        + "shadow BaseValidator's inherited field", "config".equals(declaredField.getName()));
    }

    Field configField = BaseValidator.class.getDeclaredField("config");

    assertTrue("the inherited config field must stay annotated with @Inject for Spring to populate it",
      configField.isAnnotationPresent(Inject.class));
    assertTrue("the inherited config field must stay protected, as BaseValidator declares it",
      Modifier.isProtected(configField.getModifiers()));
  }

  /**
   * Spy subclass of {@link GuestUsersValidator}: overrides {@code validateGuestUsers} to capture its
   * four arguments instead of running the real field-validation logic, since MARLO has no mocking
   * framework (`DEC-005` `PENDING`) to verify a real invocation directly.
   */
  private static final class SpyGuestUsersValidator extends GuestUsersValidator {

    private BaseAction capturedAction;
    private User capturedUser;
    private String capturedAcronym;
    private Boolean capturedIsCgiarUser;
    private int invocationCount;

    SpyGuestUsersValidator(DirectoryService directoryService) {
      super(directoryService);
    }

    @Override
    public void validateGuestUsers(BaseAction action, User user, String selectedGlobalUnitAcronym,
      boolean isCGIARUser) {
      this.capturedAction = action;
      this.capturedUser = user;
      this.capturedAcronym = selectedGlobalUnitAcronym;
      this.capturedIsCgiarUser = isCGIARUser;
      this.invocationCount++;
    }
  }
}
