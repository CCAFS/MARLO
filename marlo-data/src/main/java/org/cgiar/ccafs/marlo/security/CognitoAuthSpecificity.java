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

import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.CustomParameterManager;
import org.cgiar.ccafs.marlo.data.manager.ParameterManager;
import org.cgiar.ccafs.marlo.data.model.CustomParameter;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.Parameter;

/**
 * CHG-COGNITO-AUTH-001-T10: the single place that resolves {@code cognito_auth_active} for a Global Unit.
 * <p>
 * <b>Why this class exists (PS-16).</b> design.md 9.2 resolves this flag at three points on purpose --
 * {@code crpByEmail.do} (rendering hint), {@code CognitoLoginAction} (authoritative pre-filter) and
 * {@code CognitoCallbackAction} (authoritative gate) -- and requires all three to agree. T08 implemented
 * {@code COALESCE(active custom_parameters value, parameters.default_value)} semantics directly inside
 * {@code CognitoLoginAction}. Left alone, T10 would have had to copy that logic to resolve the same flag
 * per Global Unit for {@code crpByEmail.do}'s response -- a <b>fourth</b> reading of one flag, and every
 * additional reading is one more place a future edit can make disagree with the others. An operator
 * flipping a catalog {@code default_value} to {@code 'true'} as a global enable would then make one call
 * site say enabled while another said disabled, and T11's guard against CGIAR credential relay is one of
 * those call sites.
 * <p>
 * This is a stateless utility, not a Spring-managed manager: both {@link CustomParameterManager} and
 * {@link ParameterManager} are already constructor-injected into every caller (T08's
 * {@code CognitoLoginAction} and T10's {@code CrpByUserEmailAction}), so resolution is a pure function of
 * those two collaborators and the {@link GlobalUnit} in question. Wrapping it in another injected bean
 * would add a Spring wiring seam for no behavioral gain, and would have required changing
 * {@code CognitoLoginAction}'s already-audited constructor signature.
 */
public final class CognitoAuthSpecificity {

  private CognitoAuthSpecificity() {
  }

  /**
   * Resolves {@code cognito_auth_active} for {@code globalUnit} with {@code COALESCE(custom value,
   * parameters.default_value)} semantics -- exactly {@code hasSpecificities()}'s intent, applied where
   * {@code hasSpecificities()} itself cannot run (design.md 9.1: the session is empty until after login).
   * An active {@code custom_parameters} row wins; absent one, the catalog default for the Global Unit's
   * type applies. A Global Unit of a type with no catalog row (types 2 and 5 -- design.md 3, R-D10)
   * resolves to {@code false}, exactly as intended: the flag cannot exist for them.
   *
   * @param globalUnit the Global Unit to resolve the flag for. {@code null} resolves to {@code false}
   * @param customParameterManager the manager to read an active per-unit override from
   * @param parameterManager the manager to read the type-level catalog default from
   * @return {@code true} when the flag resolves active for {@code globalUnit}, {@code false} otherwise
   */
  public static boolean isActiveFor(GlobalUnit globalUnit, CustomParameterManager customParameterManager,
    ParameterManager parameterManager) {
    if (globalUnit == null) {
      return false;
    }
    CustomParameter override = customParameterManager
      .getCustomParameterByParameterKeyAndGlobalUnitId(APConstants.COGNITO_AUTH_ACTIVE, globalUnit.getId());
    if (override != null) {
      return Boolean.parseBoolean(override.getValue());
    }
    Parameter catalog =
      parameterManager.getParameterByKey(APConstants.COGNITO_AUTH_ACTIVE, globalUnit.getGlobalUnitType().getId());
    return catalog != null && Boolean.parseBoolean(catalog.getDefaultValue());
  }
}
