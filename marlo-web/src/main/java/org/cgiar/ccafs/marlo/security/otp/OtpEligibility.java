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

import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.CrpUserManager;
import org.cgiar.ccafs.marlo.data.manager.CustomParameterManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.OtpChallengeManager;
import org.cgiar.ccafs.marlo.data.manager.ParameterManager;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.security.CognitoAuthSpecificity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The single eligibility predicate of the Center email one-time-code sign-in (CHG-OTP-LOGIN-001-T06, FN-004, FN-005,
 * FN-015, SEC-009, FN-001, DD-4).
 * <p>
 * <b>Full predicate</b> ({@link #isEligible(String, long)}), used when a code is requested (design 5.1 step 6) and
 * again after it is consumed (5.2 step 8, FN-015). It is true only when <b>all</b> of these hold:
 * <ol>
 * <li>the feature is configured ({@link OtpKeys#isConfigured()});</li>
 * <li>the email's domain equals an entry of the Global Unit's allow-list, read <b>uncached</b> through
 * {@link OtpChallengeManager#allowListFor} (DD-12), with {@code cgiar.org} ignored (SEC-009);</li>
 * <li>the Global Unit exists;</li>
 * <li>the user exists, by the case-insensitive and trimmed lookup of {@link UserManager#getUserByEmail} (P-12);</li>
 * <li>the user is active;</li>
 * <li>the user is a {@code crp_users} member of the Global Unit, through the same
 * {@link CrpUserManager#existCrpUser(long, long)} that {@code LoginAction.finishLogin} calls (P-2, P-13);</li>
 * <li>it is <b>not</b> the case that the user is a CGIAR user ({@link User#isCgiarUser()}, the
 * {@code users.is_cgiar_user} column) <b>and</b> {@link CognitoAuthSpecificity#isActiveFor} holds for the unit
 * (SEC-009). A CGIAR user on a unit where Cognito is inactive is eligible (OQ-4).</li>
 * </ol>
 * The checks run cheapest and least revealing first and stop at the first refusal, but the answer is a bare boolean:
 * the caller can never tell which fact refused.
 * <p>
 * <b>Disclosure-safe variant</b> ({@link #otpEnabledFor}), used by {@code crpByEmail.do} to say whether a unit offers
 * the code button. It checks the configuration, the email's domain and SEC-009 only. It never reads the user's
 * active state or membership, so the response discloses nothing beyond today's {@code isCgiarUser} flag and unit list
 * (JI-11).
 * <p>
 * <b>Call it only from an action's {@code execute()}.</b> The user, membership and allow-list reads go through
 * transactional managers, and a transactional call made from {@code validate()} commits whatever {@code prepare()}
 * left half-edited. A store failure surfaces as the manager's runtime exception; the caller turns it into its own
 * {@code unavailable} or {@code internal_error} answer. The class is stateless and holds no session.
 * <p>
 * The email is normalised here, and only after {@link OtpDomainPolicy#domainOf(String)} has accepted it, because
 * {@link OtpDomainPolicy#normaliseEmail(String)} answers the empty string for a rejected (non-ASCII) address, which
 * must never be looked up.
 */
public final class OtpEligibility {

  private final OtpKeys keys;
  private final UserManager userManager;
  private final GlobalUnitManager globalUnitManager;
  private final CrpUserManager crpUserManager;
  private final OtpChallengeManager otpChallengeManager;
  private final CustomParameterManager customParameterManager;
  private final ParameterManager parameterManager;

  public OtpEligibility(OtpKeys keys, UserManager userManager, GlobalUnitManager globalUnitManager,
    CrpUserManager crpUserManager, OtpChallengeManager otpChallengeManager,
    CustomParameterManager customParameterManager, ParameterManager parameterManager) {
    this.keys = keys;
    this.userManager = userManager;
    this.globalUnitManager = globalUnitManager;
    this.crpUserManager = crpUserManager;
    this.otpChallengeManager = otpChallengeManager;
    this.customParameterManager = customParameterManager;
    this.parameterManager = parameterManager;
  }

  /**
   * The full predicate (design 5.1 step 6 and 5.2 step 8).
   *
   * @param email the submitted email, raw; possibly {@code null}
   * @param globalUnitId the selected Global Unit
   * @return {@code true} only when every condition in the class description holds
   */
  public boolean isEligible(String email, long globalUnitId) {
    if (!this.keys.isConfigured() || OtpDomainPolicy.domainOf(email) == null) {
      return false;
    }
    Long unitId = Long.valueOf(globalUnitId);
    if (!this.isDomainAllowed(email, this.allowLists(Collections.singletonList(unitId)).get(unitId))) {
      return false;
    }
    GlobalUnit globalUnit = this.globalUnitManager.getGlobalUnitById(globalUnitId);
    if (globalUnit == null) {
      return false;
    }
    User user = this.userManager.getUserByEmail(OtpDomainPolicy.normaliseEmail(email));
    if (user == null || !user.isActive() || user.getId() == null) {
      return false;
    }
    if (!this.crpUserManager.existCrpUser(user.getId().longValue(), globalUnitId)) {
      return false;
    }
    return !this.isRefusedBySec009(user.isCgiarUser(), globalUnit);
  }

  /**
   * The variant for {@code crpByEmail.do}: configured, domain allowed for the unit, and not refused by SEC-009. It
   * reads no account state beyond the {@code isCgiarUser} flag the caller already holds.
   *
   * @param email the submitted email, raw; possibly {@code null}
   * @param cgiarUser the {@code is_cgiar_user} flag of the user the caller found
   * @param units the Global Units to answer for; {@code null} entries are skipped
   * @return one entry per listed unit id, in the order given. The allow-lists of all the units come from one
   *         uncached query, made only when the feature is configured and the email has a domain
   */
  public Map<Long, Boolean> otpEnabledFor(String email, boolean cgiarUser, Collection<GlobalUnit> units) {
    Map<Long, Boolean> result = new LinkedHashMap<Long, Boolean>();
    List<Long> unitIds = new ArrayList<Long>();
    if (units != null) {
      for (GlobalUnit unit : units) {
        if (unit != null && unit.getId() != null) {
          unitIds.add(unit.getId());
          result.put(unit.getId(), Boolean.FALSE);
        }
      }
    }
    if (unitIds.isEmpty() || !this.keys.isConfigured() || OtpDomainPolicy.domainOf(email) == null) {
      return result;
    }
    Map<Long, String> allowLists = this.allowLists(unitIds);
    for (GlobalUnit unit : units) {
      if (unit != null && unit.getId() != null && this.isDomainAllowed(email, allowLists.get(unit.getId()))
        && !this.isRefusedBySec009(cgiarUser, unit)) {
        result.put(unit.getId(), Boolean.TRUE);
      }
    }
    return result;
  }

  private Map<Long, String> allowLists(Collection<Long> unitIds) {
    Map<Long, String> allowLists =
      this.otpChallengeManager.allowListFor(APConstants.OTP_ALLOWED_EMAIL_DOMAINS, unitIds);
    return allowLists == null ? Collections.<Long, String>emptyMap() : allowLists;
  }

  private boolean isDomainAllowed(String email, String rawAllowList) {
    return OtpDomainPolicy.isAllowed(email, OtpDomainPolicy.parseAllowList(rawAllowList));
  }

  /** SEC-009: a CGIAR user is refused on a unit where Cognito is active. */
  private boolean isRefusedBySec009(boolean cgiarUser, GlobalUnit globalUnit) {
    return cgiarUser
      && CognitoAuthSpecificity.isActiveFor(globalUnit, this.customParameterManager, this.parameterManager);
  }
}
