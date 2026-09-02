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

/**
 * CHG-COGNITO-AUTH-001-T07: the identity-mapping collaborator named in {@link CognitoIdentityMapper}'s
 * javadoc.
 * <p>
 * {@link UserManager#getUserByEmail(String)} is where OQ-9's join actually happens -- it delegates to
 * {@code UserMySQLDAO.getUser(String)}, whose {@code trim()} + lowercase normalization and parameterized
 * lookup are this same task's scope extension. This class does not repeat that normalization; doing so
 * here as well would let the two copies drift.
 */
@Named
public class CognitoIdentityMapperImpl implements CognitoIdentityMapper {

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
      return Result.rejected(RejectionReason.ACCOUNT_NOT_FOUND);
    }

    // Gate 2 (SEC-006): a federated identity MUST NOT unlock a local (is_cgiar_user = 0) account. This is
    // the gate whose removal is the authentication bypass Judgment Day found -- see
    // CognitoIdentityMappingTest's mutation proof.
    if (!user.isCgiarUser()) {
      return Result.rejected(RejectionReason.NOT_CGIAR_ACCOUNT);
    }

    // Gate 3: users.is_active is authoritative regardless of IdP state.
    if (!user.isActive()) {
      return Result.rejected(RejectionReason.USER_DISABLED);
    }

    // FN-006: users.username is kept current from the CGIAR login claim, lowercased, with no LDAP call.
    // users.email is never written here -- it is the key gate 1 just resolved BY, not a field an
    // unverified claim (until gate 1 found a row) gets to overwrite.
    //
    // CORRECTED after the T07 audit (tasks.md's amended Constitutional checks): this write goes through
    // userManager.saveLastLogin(...), NOT saveUser(...). saveUser -> UserMySQLDAO.saveUser ->
    // AbstractMarloDAO.update(T) returns before merge() when the session already contains the entity --
    // and `user` here came from a session.get()-backed lookup, so it IS managed, making saveUser a no-op
    // on it. saveLastLogin carries @Transactional, which this OSIV session's FlushMode.MANUAL setup
    // requires for the change to leave memory at all -- identical to T09's constraint on this same write
    // path in LoginAction, and to why ValidateUserAction uses saveLastLogin rather than saveUser.
    String usernameClaim = assertion.getUsernameClaim();
    if (usernameClaim != null) {
      String loweredUsername = usernameClaim.toLowerCase();
      if (!loweredUsername.equals(user.getUsername())) {
        user.setUsername(loweredUsername);
        this.userManager.saveLastLogin(user);
      }
    }

    // Gate 4 (crp_users membership) is deliberately not applied here -- design.md 13.1 places it inside
    // finishLogin, once the session for the selected Global Unit exists.
    return Result.accepted(user.getId());
  }
}
