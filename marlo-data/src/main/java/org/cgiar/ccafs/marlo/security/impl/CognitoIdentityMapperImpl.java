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

package org.cgiar.ccafs.marlo.security.impl;

import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.security.CognitoAssertion;
import org.cgiar.ccafs.marlo.security.CognitoIdentityMapper;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * CHG-COGNITO-AUTH-001-T07: the identity-mapping collaborator named in {@link CognitoIdentityMapper}'s
 * javadoc.
 * <p>
 * {@link UserManager#getUserByEmail(String)} is where OQ-9's join actually happens -- it delegates to
 * {@code UserMySQLDAO.getUser(String)}, whose {@code trim()} + lowercase normalization and parameterized
 * lookup are this same task's scope extension. This class does not repeat that normalization; doing so
 * here as well would let the two copies drift.
 * <p>
 * <b>CHG-COGNITO-AUTH-001-T17 (U-3), 2026-09-04:</b> this class no longer writes {@code users.username}.
 * FN-006 was amended after a real corporate login proved its premise false -- see {@link #map} below.
 */
@Named
public class CognitoIdentityMapperImpl implements CognitoIdentityMapper {

  // CHG-COGNITO-AUTH-001-T14 (OPS-001): this class had no logger at all before -- every gate rejection was
  // silent here, discoverable only at the caller (CognitoCallbackAction already logs the same
  // RejectionReason, but design.md 11's "gate rejection (which gate)" requirement is this class's own gate,
  // not only its caller's).
  private static final Logger LOG = LoggerFactory.getLogger(CognitoIdentityMapperImpl.class);

  private final UserManager userManager;

  @Inject
  public CognitoIdentityMapperImpl(UserManager userManager) {
    if (userManager == null) {
      throw new IllegalArgumentException("userManager is required");
    }
    this.userManager = userManager;
  }

  @Override
  public Result map(CognitoAssertion assertion) {
    if (assertion == null) {
      throw new IllegalArgumentException("assertion is required");
    }

    // Gate 1 (design.md 13.1): a users row must already exist for the mapped claim -- FN-002's
    // "MUST NOT auto-provision". No User is ever constructed here; only an existing row is read.
    User user = this.userManager.getUserByEmail(assertion.getEmail());
    if (user == null) {
      LOG.info("Cognito identity mapping rejected: {}", RejectionReason.ACCOUNT_NOT_FOUND);
      return Result.rejected(RejectionReason.ACCOUNT_NOT_FOUND);
    }

    // Gate 2 (SEC-006): a federated identity MUST NOT unlock a local (is_cgiar_user = 0) account. This is
    // the gate whose removal is the authentication bypass Judgment Day found -- see
    // CognitoIdentityMappingTest's mutation proof.
    if (!user.isCgiarUser()) {
      LOG.info("Cognito identity mapping rejected: {}", RejectionReason.NOT_CGIAR_ACCOUNT);
      return Result.rejected(RejectionReason.NOT_CGIAR_ACCOUNT);
    }

    // Gate 3: users.is_active is authoritative regardless of IdP state.
    if (!user.isActive()) {
      LOG.info("Cognito identity mapping rejected: {}", RejectionReason.USER_DISABLED);
      return Result.rejected(RejectionReason.USER_DISABLED);
    }

    // CHG-COGNITO-AUTH-001-T17 (U-3), 2026-09-04: users.username is deliberately NOT touched here any
    // more. FN-006 originally read "set users.username from the CGIAR login claim, lowercased" on the
    // premise that the ID token carries that identifier. A real corporate login proved it does not: the
    // only candidate claim, cognito:username, is Cognito's own federated identifier for this pool
    // (provider name + subject, e.g. "cgiar-azuread_c.gamboa@cgiar.org"), never the AD login
    // ("cgamboa") getCgiarNickname() would have set. Writing it was worse than writing nothing -- it
    // replaced a correct AD login with a value correct for nothing, breaking username-based local login
    // (APCustomRealm:161, getUserByUsername -- the branch taken when a user types a login without "@")
    // and surfacing as a display name in QA comments (FeedbackQACommentsAction:180, :403).
    //
    // OQ-9 already made the normalized corporate email the identity key, and the Cognito path never
    // needed users.username to authenticate. So: no derivation from the email, no stripping the
    // provider prefix, no persisting cognito:username -- a null or blank users.username stays null or
    // blank, nothing is invented. The LDAP path remains the only writer: APCustomRealm.getCgiarNickname()
    // looks Active Directory up BY EMAIL and sets the username from ldapUser.getLogin(), so a local login
    // repairs or populates it. users.email is still never written here -- it is the key gate 1 just
    // resolved BY, not a field an unverified claim gets to overwrite.

    // Gate 4 (crp_users membership) is deliberately not applied here -- design.md 13.1 places it inside
    // finishLogin, once the session for the selected Global Unit exists.
    return Result.accepted(user.getId());
  }
}
