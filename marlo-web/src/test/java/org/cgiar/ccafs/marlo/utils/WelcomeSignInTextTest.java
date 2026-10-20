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

package org.cgiar.ccafs.marlo.utils;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.data.manager.CrpUserManager;
import org.cgiar.ccafs.marlo.data.manager.CustomParameterManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.OtpChallengeManager;
import org.cgiar.ccafs.marlo.data.manager.ParameterManager;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.model.CustomParameter;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.GlobalUnitType;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.security.otp.OtpEligibility;
import org.cgiar.ccafs.marlo.security.otp.OtpKeys;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * CHG-PWD-SWITCH-001-T07 (FN-008, FN-009, UI-003, DD-7): which text the welcome email carries and with which
 * arguments. The rule is {@code SignInMethodRule}'s, reached through {@link OtpEligibility#methodsFor}; these tests
 * only prove that the helper asks it for the unit it was given and picks the text from the answer.
 * <p>
 * The managers are hand-rolled doubles (MARLO has no mocking framework). The action is a recorder, so the assertions
 * read the key and the arguments the helper handed to {@code getText} instead of a rendered string.
 */
public class WelcomeSignInTextTest {

  private static final String SECRET_32_BYTES = "AAECAwQFBgcICQoLDA0ODxAREhMUFRYXGBkaGxwdHh8=";
  private static final String COGNITO_KEY = "cognito_auth_active";
  private static final String PASSWORD_KEY = "system_password_login_active";
  private static final long UNIT_ID = 45L;
  private static final long OTHER_UNIT_ID = 7L;
  private static final String UNIT_ACRONYM = "ZEBRA";
  private static final String PASSWORD = "pw-482913";
  private static final String EMAIL = "new.member@example.org";

  private final List<String> specificityReads = new ArrayList<String>();
  private final Map<Long, String> passwordSwitch = new HashMap<Long, String>();
  private final Map<Long, String> cognitoOverride = new HashMap<Long, String>();
  private RuntimeException specificityFailure;
  private String secret;

  private static <T> T fake(Class<T> type, InvocationHandler handler) {
    return type.cast(Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[] {type}, handler));
  }

  private static GlobalUnit unit(long id, String acronym) {
    GlobalUnit unit = new GlobalUnit();
    unit.setId(Long.valueOf(id));
    unit.setAcronym(acronym);
    GlobalUnitType type = new GlobalUnitType();
    type.setId(3L);
    unit.setGlobalUnitType(type);
    return unit;
  }

  private static User user(boolean cgiar) {
    User user = new User();
    user.setId(900L);
    user.setEmail(EMAIL);
    user.setCgiarUser(cgiar);
    return user;
  }

  private WelcomeSignInText helper() {
    OtpChallengeManager challenge = fake(OtpChallengeManager.class, (proxy, method, args) -> {
      if ("specificityValuesFor".equals(method.getName())) {
        if (this.specificityFailure != null) {
          throw this.specificityFailure;
        }
        Collection<?> ids = (Collection<?>) args[1];
        this.specificityReads.add(ids.toString());
        Map<Long, Map<String, String>> answer = new HashMap<Long, Map<String, String>>();
        for (Object id : ids) {
          Map<String, String> values = new HashMap<String, String>();
          for (Object key : (Collection<?>) args[0]) {
            values.put((String) key, "");
          }
          String stored = this.passwordSwitch.get(id);
          if (stored != null) {
            values.put(PASSWORD_KEY, stored);
          }
          answer.put((Long) id, values);
        }
        return answer;
      }
      throw new UnsupportedOperationException(method.getName());
    });
    CustomParameterManager custom = fake(CustomParameterManager.class, (proxy, method, args) -> {
      if ("getCustomParameterByParameterKeyAndGlobalUnitId".equals(method.getName())) {
        String value = COGNITO_KEY.equals(args[0]) ? this.cognitoOverride.get(args[1]) : null;
        if (value == null) {
          return null;
        }
        CustomParameter row = new CustomParameter();
        row.setValue(value);
        row.setActive(true);
        return row;
      }
      throw new UnsupportedOperationException(method.getName());
    });
    ParameterManager catalog = fake(ParameterManager.class, (proxy, method, args) -> {
      if ("getParameterByKey".equals(method.getName())) {
        return null;
      }
      throw new UnsupportedOperationException(method.getName());
    });
    OtpEligibility eligibility = new OtpEligibility(new OtpKeys(this.configWithSecret()),
      fake(UserManager.class, this::refuse), fake(GlobalUnitManager.class, this::refuse),
      fake(CrpUserManager.class, this::refuse), challenge, custom, catalog);
    return new WelcomeSignInText(eligibility, custom, catalog);
  }

  private APConfig configWithSecret() {
    final String configured = this.secret;
    return new APConfig() {

      @Override
      public String getOtpHmacSecret() {
        return configured;
      }
    };
  }

  private Object refuse(Object proxy, java.lang.reflect.Method method, Object[] args) {
    throw new AssertionError("the helper must not read " + method.getDeclaringClass().getSimpleName() + "."
      + method.getName());
  }

  @Before
  public void setUp() {
    this.secret = SECRET_32_BYTES;
  }

  @Test
  public void passwordOnKeepsTodaysTextWithThePassword() {
    this.passwordSwitch.put(UNIT_ID, "true");
    RecordingAction action = new RecordingAction();

    String text = this.helper().part1(action, user(false), unit(UNIT_ID, UNIT_ACRONYM), PASSWORD, "support");

    assertEquals("email.newUser.part1", action.key);
    assertEquals(Arrays.asList("roles", "https://marlo.test", EMAIL, PASSWORD, "support"),
      Arrays.asList(action.args));
    assertEquals("the helper returns what getText answered", "text:email.newUser.part1", text);
  }

  @Test
  public void aMissingSwitchReadsAsOn() {
    RecordingAction action = new RecordingAction();

    this.helper().part1(action, user(false), unit(UNIT_ID, UNIT_ACRONYM), PASSWORD, "support");

    assertEquals("email.newUser.part1", action.key);
  }

  @Test
  public void passwordOffUsesTheCodeTextWithTheUnitAcronymAndNoPassword() {
    this.passwordSwitch.put(UNIT_ID, "false");
    RecordingAction action = new RecordingAction();

    String text = this.helper().part1(action, user(false), unit(UNIT_ID, UNIT_ACRONYM), PASSWORD, "support");

    assertEquals("email.newUser.part1.codeSignIn", action.key);
    assertEquals(Arrays.asList("roles", "https://marlo.test", EMAIL, UNIT_ACRONYM, "support"),
      Arrays.asList(action.args));
    assertFalse("the password must not travel in the arguments", Arrays.asList(action.args).contains(PASSWORD));
    assertEquals("text:email.newUser.part1.codeSignIn", text);
  }

  @Test
  public void aCgiarAccountWithoutCognitoInTheUnitGetsTheCodeText() {
    this.passwordSwitch.put(UNIT_ID, "false");
    RecordingAction action = new RecordingAction();

    this.helper().part1(action, user(true), unit(UNIT_ID, UNIT_ACRONYM), PASSWORD, "support");

    assertEquals("email.newUser.part1.codeSignIn", action.key);
  }

  @Test
  public void aCognitoAccountKeepsTodaysTextEvenWithThePasswordOff() {
    this.passwordSwitch.put(UNIT_ID, "false");
    this.cognitoOverride.put(UNIT_ID, "true");
    RecordingAction action = new RecordingAction();

    this.helper().part1(action, user(true), unit(UNIT_ID, UNIT_ACRONYM), PASSWORD, "support");

    assertEquals("email.newUser.part1", action.key);
    assertEquals(PASSWORD, action.args[3]);
  }

  @Test
  public void withoutAValidCodeKeyThePasswordIsTheLastResortAndTheTextIsToday() {
    this.passwordSwitch.put(UNIT_ID, "false");
    this.secret = "";
    RecordingAction action = new RecordingAction();

    this.helper().part1(action, user(false), unit(UNIT_ID, UNIT_ACRONYM), PASSWORD, "support");

    assertEquals("email.newUser.part1", action.key);
  }

  @Test
  public void theSwitchIsReadForTheUnitItWasGivenAndForNoOther() {
    this.passwordSwitch.put(UNIT_ID, "true");
    this.passwordSwitch.put(OTHER_UNIT_ID, "false");
    RecordingAction action = new RecordingAction();

    this.helper().part1(action, user(false), unit(OTHER_UNIT_ID, "OTHER"), PASSWORD, "support");

    assertEquals("the unit passed is the one that decides", "email.newUser.part1.codeSignIn", action.key);
    assertEquals(Arrays.asList("[" + OTHER_UNIT_ID + "]"), this.specificityReads);
  }

  @Test
  public void aFailingSettingsReadKeepsTodaysText() {
    this.specificityFailure = new IllegalStateException("store down");
    RecordingAction action = new RecordingAction();

    this.helper().part1(action, user(false), unit(UNIT_ID, UNIT_ACRONYM), PASSWORD, "support");

    assertEquals("email.newUser.part1", action.key);
    assertEquals(PASSWORD, action.args[3]);
  }

  @Test
  public void aUnitWithNeitherAcronymNorNameNeverPrintsNull() {
    this.passwordSwitch.put(UNIT_ID, "false");
    RecordingAction action = new RecordingAction();

    this.helper().part1(action, user(false), unit(UNIT_ID, null), PASSWORD, "support");

    assertEquals("email.newUser.part1.codeSignIn", action.key);
    assertEquals("", action.args[3]);
  }

  @Test
  public void aMissingUnitKeepsTodaysTextWithoutReadingAnything() {
    RecordingAction action = new RecordingAction();

    this.helper().part1(action, user(false), null, PASSWORD, "support");

    assertEquals("email.newUser.part1", action.key);
    assertTrue(this.specificityReads.isEmpty());
  }

  /** Records the key and the arguments of the one {@code getText} call the helper makes with arguments. */
  private static final class RecordingAction extends BaseAction {

    private static final long serialVersionUID = 1L;

    private String key;
    private String[] args;

    RecordingAction() {
      super(new APConfig());
    }

    @Override
    public String getBaseUrl() {
      return "https://marlo.test";
    }

    @Override
    public String getText(String aTextName) {
      return "email.newUser.listRoles".equals(aTextName) ? "roles" : aTextName;
    }

    @Override
    public String getText(String aTextName, String[] args) {
      this.key = aTextName;
      this.args = args;
      return "text:" + aTextName;
    }
  }
}
