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

package org.cgiar.ccafs.marlo.security.directory;

/**
 * Signals that a directory lookup could not be completed and the caller chose not to degrade the
 * failure into a not-found result.
 * <p>
 * {@code DirectoryService} implementations never throw this exception — the contract is
 * never-throws, and a failed lookup is represented as a {@link DirectoryPerson} with
 * {@link DirectorySource#ERROR}. It is a <em>consumer</em> that reads {@code source == ERROR} and
 * decides the failure must propagate rather than be silently treated as "not found" that throws it.
 * <p>
 * This type deliberately extends {@link RuntimeException} directly rather than
 * {@code org.apache.shiro.authz.AuthorizationException}: the latter is mapped by
 * {@code struts.xml} to an HTTP 403 response, which would silently change the observable outcome of
 * a backend failure from a 500 to a 403. Extending {@link RuntimeException} directly keeps that
 * mapping — and therefore the response the caller already sees today — unchanged.
 */
public class DirectoryLookupException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  private final String email;

  /**
   * @param email the email that was being looked up when the failure occurred
   * @param cause the underlying failure
   */
  public DirectoryLookupException(String email, Throwable cause) {
    super("Directory lookup failed for the requested email", cause);
    this.email = email;
  }

  /**
   * @return the email that was being looked up when the failure occurred
   */
  public String getEmail() {
    return email;
  }

}
