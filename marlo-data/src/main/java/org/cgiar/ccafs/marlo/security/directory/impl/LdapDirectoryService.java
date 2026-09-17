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

package org.cgiar.ccafs.marlo.security.directory.impl;

import org.cgiar.ccafs.marlo.security.directory.DirectoryPerson;
import org.cgiar.ccafs.marlo.security.directory.DirectoryService;
import org.cgiar.ccafs.marlo.security.directory.DirectorySource;
import org.cgiar.ccafs.marlo.utils.APConfig;

import org.cgiar.ciat.auth.LDAPService;
import org.cgiar.ciat.auth.LDAPUser;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The LDAP-backed {@link DirectoryService} implementation. It delegates every lookup to
 * {@code adauth} and reproduces {@code BaseAction.getOutlookUser}'s observable behavior exactly,
 * with one deliberate addition: a backend failure for a well-formed email is logged and reported as
 * {@link DirectorySource#ERROR} instead of being swallowed silently. A backend failure for a
 * malformed email (see {@link #isWellFormed(String)}) is reported as {@link DirectorySource#NOT_FOUND}
 * with no error log, because invalid input is not a backend failure.
 * <p>
 * This is the only class under {@code security/directory/**} permitted to import
 * {@code org.cgiar.ciat} — every other type in that package is provider-agnostic, and this class is
 * the containment boundary that keeps it that way.
 * <p>
 * No connection pooling, caching, or retry is performed: a new {@link LDAPService} is constructed on
 * every call, exactly as the code this class replaces does.
 */
@Named
public class LdapDirectoryService implements DirectoryService {

  private static final Logger LOG = LoggerFactory.getLogger(LdapDirectoryService.class);

  private final APConfig config;

  @Inject
  public LdapDirectoryService(APConfig config) {
    this.config = config;
  }

  @Override
  public DirectoryPerson findByEmail(String email) {
    if ((email == null) || email.trim().isEmpty()) {
      return DirectoryPerson.notFound(email, DirectorySource.NOT_FOUND);
    }

    LDAPService service = this.newLdapService();
    service.setInternalConnection(!this.config.isProduction());

    try {
      LDAPUser user = service.searchUserByEmail(email);
      if (user == null) {
        return DirectoryPerson.notFound(email, DirectorySource.NOT_FOUND);
      }
      return DirectoryPerson.found(user.getEmail(), user.getLogin(), user.getFirstName(), user.getLastName(),
        DirectorySource.LDAP);
    } catch (Exception e) {
      if (isWellFormed(email)) {
        LOG.error("Directory lookup failed for email '{}'", email, e);
        return DirectoryPerson.notFound(email, DirectorySource.ERROR);
      }
      return DirectoryPerson.notFound(email, DirectorySource.NOT_FOUND);
    }
  }

  /**
   * Factory seam (DD-12) so a test can substitute the backend without touching the mapping logic
   * above. Overridden only by {@code LdapDirectoryServiceTest}'s test-local subclass; production
   * always takes this default.
   *
   * @return a new {@link LDAPService}
   */
  protected LDAPService newLdapService() {
    return new LDAPService();
  }

  /**
   * Minimal well-formedness check, consulted **only** from the exception handler in
   * {@link #findByEmail(String)} to decide whether a backend failure is a real outage
   * ({@link DirectorySource#ERROR}) or an admin typo that was never going to resolve
   * ({@link DirectorySource#NOT_FOUND}). Deliberately not RFC 5322: exactly one {@code @}, a
   * non-empty local part, and a domain part containing at least one {@code .}.
   *
   * @param email a non-null, non-blank email, as guaranteed by the fail-fast check above
   * @return {@code true} when the email has the minimal shape of a valid address
   */
  private static boolean isWellFormed(String email) {
    int at = email.indexOf('@');
    if ((at <= 0) || (at != email.lastIndexOf('@'))) {
      return false;
    }
    String domain = email.substring(at + 1);
    return domain.contains(".");
  }

}
