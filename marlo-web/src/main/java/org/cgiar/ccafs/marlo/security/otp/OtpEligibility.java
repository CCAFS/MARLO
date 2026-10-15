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
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.OtpChallengeManager;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * The single eligibility predicate of the Center email one-time-code sign-in (CHG-OTP-LOGIN-001-T06, FN-001, FN-003,
 * FN-004, FN-005, FN-015, SEC-009, DD-4; amended by T21, DD-13).
 * <p>
 * <b>Full predicate</b> ({@link #isEligible(String, long)}), used when a code is requested (design 5.1 step 6) and
 * again after it is consumed (5.2 step 8, FN-015). It is true only when <b>all</b> of these hold:
 * <ol>
 * <li>the feature is configured ({@link OtpKeys#isConfigured()});</li>
 * <li>the Global Unit's switch {@code system_otp_login_active} is on and the email's domain is not on its
 * {@code system_otp_excluded_email_domains} list, both read <b>uncached</b> through
 * {@link OtpChallengeManager#specificityValuesFor} (DD-12, {@link OtpDomainPolicy#isOffered});</li>
 * <li>the Global Unit exists;</li>
 * <li>the user exists, by the case-insensitive and trimmed lookup of {@link UserManager#getUserByEmail} (P-12);</li>
 * <li>the user is active;</li>
 * <li>the user is an <b>active</b> {@code crp_users} member of the Global Unit, through
 * {@link CrpUserManager#existActiveCrpUser(long, long)} (FN-005: a soft-deleted row, {@code is_active=0}, is not a
 * member). Password login ({@code LoginAction.finishLogin}) still uses the activity-blind {@code existCrpUser}; this
 * is a deliberate departure from that parity (requirements Decision Log, 2026-10-13).</li>
 * </ol>
 * Nothing here reads {@code users.is_cgiar_user} or the Cognito specificity, and no domain is fixed in code
 * (SEC-009, DD-13): a CGIAR account with access to the unit gets a code like any other account unless its domain is
 * excluded. The checks run cheapest and least revealing first and stop at the first refusal, but the answer is a
 * bare boolean: the caller can never tell which fact refused.
 * <p>
 * <b>Disclosure-safe variants.</b> {@link #isOffered(String, long)} (request step 4) and
 * {@link #otpEnabledFor(String, Collection)} ({@code crpByEmail.do}) check the switch and the exclusion only (the
 * second also the configuration). They never read the user's active state or membership, so the response discloses
 * nothing beyond today's {@code isCgiarUser} flag and unit list (JI-11).
 * <p>
 * <b>Call it only from an action's {@code execute()}.</b> The user, membership and specificity reads go through
 * transactional managers, and a transactional call made from {@code validate()} commits whatever {@code prepare()}
 * left half-edited. A store failure surfaces as the manager's runtime exception; the caller turns it into its own
 * {@code unavailable} or {@code internal_error} answer. The class is stateless and holds no session.
 * <p>
 * The email is normalised here, and only after {@link OtpDomainPolicy#domainOf(String)} has accepted it, because
 * {@link OtpDomainPolicy#normaliseEmail(String)} answers the empty string for a rejected (non-ASCII) address, which
 * must never be looked up.
 * <p>
 * It is a container singleton: {@code @Named}, found by the component scan and created once from its managers, so
 * every action shares one instance. Do not construct it in an action: inject it.
 */
@Named
public final class OtpEligibility {

  /** The two specificities of FN-001 and FN-003, read together in one query (DD-12, DD-13). */
  private static final List<String> SPECIFICITY_KEYS = Collections.unmodifiableList(
    Arrays.asList(APConstants.OTP_LOGIN_ACTIVE, APConstants.OTP_EXCLUDED_EMAIL_DOMAINS));

  private final OtpKeys keys;
  private final UserManager userManager;
  private final GlobalUnitManager globalUnitManager;
  private final CrpUserManager crpUserManager;
  private final OtpChallengeManager otpChallengeManager;

  @Inject
  public OtpEligibility(OtpKeys keys, UserManager userManager, GlobalUnitManager globalUnitManager,
    CrpUserManager crpUserManager, OtpChallengeManager otpChallengeManager) {
    this.keys = keys;
    this.userManager = userManager;
    this.globalUnitManager = globalUnitManager;
    this.crpUserManager = crpUserManager;
    this.otpChallengeManager = otpChallengeManager;
  }

  /**
   * Design 5.1 step 4 on its own: the Global Unit's switch is on and the email's domain is not excluded. The secret
   * is not checked here (the request action does it first), and nothing about the account is read.
   *
   * @param email the submitted email, raw; possibly {@code null}
   * @param globalUnitId the selected Global Unit. One that does not exist has its switch off
   * @return {@code true} only when the code is offered to this email on this Global Unit
   */
  public boolean isOffered(String email, long globalUnitId) {
    if (OtpDomainPolicy.domainOf(email) == null) {
      return false;
    }
    Long unitId = Long.valueOf(globalUnitId);
    return this.isOffered(email, this.specificities(Collections.singletonList(unitId)).get(unitId));
  }

  /**
   * The full predicate (design 5.1 step 6 and 5.2 step 8).
   *
   * @param email the submitted email, raw; possibly {@code null}
   * @param globalUnitId the selected Global Unit
   * @return {@code true} only when every condition in the class description holds
   */
  public boolean isEligible(String email, long globalUnitId) {
    if (!this.keys.isConfigured() || !this.isOffered(email, globalUnitId)) {
      return false;
    }
    if (this.globalUnitManager.getGlobalUnitById(globalUnitId) == null) {
      return false;
    }
    User user = this.userManager.getUserByEmail(OtpDomainPolicy.normaliseEmail(email));
    if (user == null || !user.isActive() || user.getId() == null) {
      return false;
    }
    return this.crpUserManager.existActiveCrpUser(user.getId().longValue(), globalUnitId);
  }

  /**
   * The variant for {@code crpByEmail.do}: configured, the unit's switch on and the email's domain not excluded. It
   * reads no account state.
   *
   * @param email the submitted email, raw; possibly {@code null}
   * @param units the Global Units to answer for; {@code null} entries are skipped
   * @return one entry per listed unit id, in the order given. The switch and the exclusion of all the units come
   *         from one uncached query, made only when the feature is configured and the email has a domain
   */
  public Map<Long, Boolean> otpEnabledFor(String email, Collection<GlobalUnit> units) {
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
    Map<Long, Map<String, String>> specificities = this.specificities(unitIds);
    for (Long unitId : unitIds) {
      if (this.isOffered(email, specificities.get(unitId))) {
        result.put(unitId, Boolean.TRUE);
      }
    }
    return result;
  }

  private boolean isOffered(String email, Map<String, String> values) {
    return values != null && OtpDomainPolicy.isOffered(values.get(APConstants.OTP_LOGIN_ACTIVE),
      values.get(APConstants.OTP_EXCLUDED_EMAIL_DOMAINS), email);
  }

  private Map<Long, Map<String, String>> specificities(Collection<Long> unitIds) {
    Map<Long, Map<String, String>> values = this.otpChallengeManager.specificityValuesFor(SPECIFICITY_KEYS, unitIds);
    return values == null ? Collections.<Long, Map<String, String>>emptyMap() : values;
  }
}
