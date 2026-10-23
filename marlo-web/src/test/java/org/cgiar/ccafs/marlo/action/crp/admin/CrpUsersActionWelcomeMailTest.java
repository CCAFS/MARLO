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

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.CrpUserManager;
import org.cgiar.ccafs.marlo.data.manager.CustomParameterManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.ParameterManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.RoleManager;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.manager.UserRoleManager;
import org.cgiar.ccafs.marlo.data.model.CustomParameter;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.Parameter;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.security.directory.FakeDirectoryService;
import org.cgiar.ccafs.marlo.security.otp.UnconfiguredOtpEligibility;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.SendMailS;
import org.cgiar.ccafs.marlo.utils.WelcomeSignInText;
import org.cgiar.ccafs.marlo.validation.superadmin.GuestUsersValidator;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

/**
 * CHG-PWD-SWITCH-001-T07 (DD-7, P-9): {@code CrpUsersAction.sendMailNewUser} hands the welcome-email helper the Global
 * Unit it was given, which is the {@code globalUnit} of {@code save()}, and not the unit the action resolves again from
 * the selected acronym. The two units differ here, so passing the wrong one is visible.
 * <p>
 * The helper is a recorder: the assertion reads what it received, not a rendered string. Email delivery is switched
 * off by the unit's notification parameter, so no mail machinery runs.
 */
public class CrpUsersActionWelcomeMailTest {

  private static GlobalUnit unit(long id, String acronym) {
    GlobalUnit unit = new GlobalUnit();
    unit.setId(Long.valueOf(id));
    unit.setAcronym(acronym);
    Parameter parameter = new Parameter();
    parameter.setKey(APConstants.CRP_EMAIL_NOTIFICATIONS);
    CustomParameter notifications = new CustomParameter();
    notifications.setParameter(parameter);
    notifications.setValue("false");
    Set<CustomParameter> rows = new HashSet<CustomParameter>();
    rows.add(notifications);
    unit.setCustomParameters(rows);
    return unit;
  }

  private static <T> T forbidden(Class<T> type) {
    return type.cast(Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[] {type}, (proxy, method, args) -> {
      throw new AssertionError("not expected in this test: " + type.getSimpleName() + "." + method.getName());
    }));
  }

  @Test
  public void theHelperReceivesTheUnitTheAccountIsCreatedFor() throws Exception {
    final GlobalUnit created = unit(45L, "CREATED");
    final GlobalUnit selected = unit(7L, "SELECTED");
    GlobalUnitManager globalUnitManager =
      (GlobalUnitManager) Proxy.newProxyInstance(GlobalUnitManager.class.getClassLoader(),
        new Class<?>[] {GlobalUnitManager.class}, (proxy, method, args) -> {
          if ("findGlobalUnitByAcronym".equals(method.getName())) {
            return "SELECTED".equals(args[0]) ? selected : null;
          }
          throw new AssertionError("not expected: " + method.getName());
        });
    final RecordingText helper = new RecordingText();
    APConfig config = new APConfig();
    CrpUsersAction action = new CrpUsersAction(config, globalUnitManager, forbidden(CrpUserManager.class),
      forbidden(UserManager.class), forbidden(ProjectManager.class), forbidden(PhaseManager.class),
      forbidden(RoleManager.class), forbidden(UserRoleManager.class), new SendMailS(config, null, null, null),
      new GuestUsersValidator(new FakeDirectoryService()), new FakeDirectoryService(),
      forbidden(CustomParameterManager.class), forbidden(ParameterManager.class), helper) {

      private static final long serialVersionUID = 1L;

      @Override
      public boolean isAiccra() {
        return false;
      }

      @Override
      public String getText(String aTextName) {
        return aTextName;
      }

      @Override
      public String getText(String aTextName, String[] args) {
        return aTextName;
      }
    };
    Field acronym = CrpUsersAction.class.getDeclaredField("selectedGlobalUnitAcronym");
    acronym.setAccessible(true);
    acronym.set(action, "SELECTED");
    User newUser = new User();
    newUser.setEmail("new.member@example.org");
    newUser.setFirstName("New");

    sendNewUserMail(action, newUser, created);

    assertNotNull("the email must reach the helper", helper.unit);
    assertSame("the helper decides on the unit the account is created for", created, helper.unit);
    assertSame(newUser, helper.user);
    assertEquals("123456", helper.password);
  }

  private static void sendNewUserMail(Object action, User user, GlobalUnit unit) throws NoSuchAlgorithmException {
    ((CrpUsersAction) action).sendMailNewUser(user, unit, "123456");
  }

  /** The helper as a recorder. It needs no collaborator: it never reads a rule. */
  private static final class RecordingText extends WelcomeSignInText {

    private User user;
    private GlobalUnit unit;
    private String password;

    RecordingText() {
      super(UnconfiguredOtpEligibility.create(), forbidden(CustomParameterManager.class),
        forbidden(ParameterManager.class));
    }

    @Override
    public String part1(BaseAction action, User user, GlobalUnit unit, String password, String supportText) {
      this.user = user;
      this.unit = unit;
      this.password = password;
      return "recorded";
    }
  }
}
