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

package org.cgiar.ccafs.marlo.security.otp;

import org.cgiar.ccafs.marlo.data.manager.CrpUserManager;
import org.cgiar.ccafs.marlo.data.manager.CustomParameterManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.OtpChallengeManager;
import org.cgiar.ccafs.marlo.data.manager.ParameterManager;
import org.cgiar.ccafs.marlo.data.manager.UserManager;

import java.lang.reflect.Proxy;

/**
 * CHG-PWD-SWITCH-001-T05: the {@link OtpEligibility} the tests of the password endpoints hand to the actions that do
 * not exercise the password gate.
 * <p>
 * The code key is <b>missing</b>, so by DD-5 the gate never refuses and never reads anything (the last resort). Every
 * manager is a double that throws {@link AssertionError} on any call: a test built on this fixture proves, by passing,
 * that the gate made no read at all, instead of passing because a permissive double answered. A test of the gate
 * itself builds its own {@link OtpEligibility} with a valid key and doubles that record what they are asked.
 */
public final class UnconfiguredOtpEligibility {

  private UnconfiguredOtpEligibility() {
    throw new AssertionError("utility class, never instantiated");
  }

  /**
   * @return an eligibility whose code key is not configured and whose managers fail the test on any call
   */
  public static OtpEligibility create() {
    return new OtpEligibility(new OtpKeys(""), forbidden(UserManager.class), forbidden(GlobalUnitManager.class),
      forbidden(CrpUserManager.class), forbidden(OtpChallengeManager.class), forbidden(CustomParameterManager.class),
      forbidden(ParameterManager.class));
  }

  private static <T> T forbidden(Class<T> type) {
    return type.cast(Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[] {type}, (proxy, method, args) -> {
      throw new AssertionError("the password gate must make no read while the code key is missing, but "
        + type.getSimpleName() + "." + method.getName() + " was called");
    }));
  }
}
