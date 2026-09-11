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

package org.cgiar.ccafs.marlo.action.json.global;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.security.directory.DirectoryPerson;
import org.cgiar.ccafs.marlo.security.directory.DirectoryService;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.dispatcher.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Read-only lookup of the CGIAR directory, so a screen can ask whether the directory holds an address
 * <b>before</b> anything is saved.
 * <p>
 * The Guest Users screen decides whether to ask the administrator for a first and last name, and that
 * decision belongs to the directory: it supplies both when it knows the person, and nothing when it does
 * not. Only the server can reach the directory -- {@code org.cgiar.ciat.auth.LDAPService} opens an LDAP
 * connection from inside MARLO -- so without this action the browser had no source for the answer and
 * guessed from the email domain instead. That guess is the defect this action exists to retire.
 * <p>
 * <b>Read-only by construction.</b> {@code createUser.do} already reaches the same directory and already
 * returns the names it found ({@code ManageUsersAction#addUser}, {@code fName} / {@code lName}), but only
 * as a side effect of creating an account. This action returns the same answer and creates nothing.
 * <p>
 * <b>Three guards, in order.</b> The directory is corporate infrastructure and a lookup is a disclosure --
 * it reveals that an address exists and who it belongs to -- so nothing reaches it until all three pass:
 * <ol>
 * <li>a session user holding the CRP administrator permission, which is already what creating a Guest
 * User requires, so this action discloses nothing to anyone who could not obtain it by creating the
 * account;</li>
 * <li>an address in the corporate domain, since the directory would never hold any other and refusing
 * them keeps the reachable surface to a single domain;</li>
 * <li>a non-blank address, so an empty parameter never becomes an LDAP connection.</li>
 * </ol>
 * A refused request is indistinguishable from an unknown address: both answer {@code found: false} and
 * carry nothing else. There is no branch here that reports <i>why</i> the answer was negative.
 * <p>
 * <b>Failure is not an error.</b> {@code LdapDirectoryService} already collapses an outage into
 * {@code notFound}, so an unreachable directory answers {@code found: false} and the caller asks the
 * administrator for the names. That is the correct outcome: a directory that cannot be consulted must
 * never block a creation, which is exactly what the old browser-side guess got wrong.
 *
 * @author Kenji Tanaka - Alliance Bioversity-CIAT
 */
public class DirectoryByEmailAction extends BaseAction {

  private static final long serialVersionUID = 6218452074419637021L;

  private static final Logger LOG = LoggerFactory.getLogger(DirectoryByEmailAction.class);

  private final DirectoryService directoryService;

  private String userEmail;

  // The whole response. Declared as the result's `root` so the JSON carries these keys and nothing else --
  // without it the plugin would serialize every public getter BaseAction exposes.
  private Map<String, Object> directory;

  @Inject
  public DirectoryByEmailAction(APConfig config, DirectoryService directoryService) {
    super(config);
    this.directoryService = directoryService;
  }

  @Override
  public String execute() throws Exception {
    this.directory = new HashMap<>();
    this.directory.put("found", false);

    if (this.getCurrentUser() == null || !this.canAcessCrpAdmin()) {
      LOG.debug("A directory lookup was refused because the caller is not a CRP administrator");
      return SUCCESS;
    }

    String email = StringUtils.trimToEmpty(this.userEmail).toLowerCase();
    if (email.isEmpty() || !email.endsWith(APConstants.OUTLOOK_EMAIL)) {
      return SUCCESS;
    }

    DirectoryPerson person = this.directoryService.findByEmail(email);
    if (!person.isFound()) {
      return SUCCESS;
    }

    this.directory.put("found", true);
    this.directory.put("firstName", person.getFirstName());
    this.directory.put("lastName", person.getLastName());
    if (person.getLogin() != null) {
      this.directory.put("username", person.getLogin().toLowerCase());
    }

    return SUCCESS;
  }

  public Map<String, Object> getDirectory() {
    return this.directory;
  }

  public String getUserEmail() {
    return this.userEmail;
  }

  @Override
  public void prepare() throws Exception {
    Map<String, Parameter> parameters = this.getParameters();
    Parameter emailParameter = parameters.get(APConstants.USER_EMAIL);
    // An absent parameter is a caller mistake, not a reason to 500: execute() treats a blank address the
    // same way it treats an unknown one. The three sites that read USER_EMAIL today index the value array
    // straight away and throw when it is missing.
    if (emailParameter != null && emailParameter.getMultipleValues() != null
      && emailParameter.getMultipleValues().length > 0) {
      this.userEmail = StringUtils.trim(emailParameter.getMultipleValues()[0]);
    }
  }

  public void setDirectory(Map<String, Object> directory) {
    this.directory = directory;
  }

  public void setUserEmail(String userEmail) {
    this.userEmail = userEmail;
  }

}
