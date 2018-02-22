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

package org.cgiar.ccafs.marlo.action.superadmin;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.data.manager.EmailLogManager;
import org.cgiar.ccafs.marlo.data.model.EmailLog;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

/**
 * @author Sebastian Amariles - CIAT/CCAFS
 */
public class EmailsOnTrackAction extends BaseAction {

  private static final long serialVersionUID = -793652591843623397L;

  // Managers
  private EmailLogManager emailLogManager;
  // Front-end
  private ArrayList<EmailLog> emails;

  @Inject
  public EmailsOnTrackAction(APConfig config, EmailLogManager emailLogManager) {
    super(config);
    this.emailLogManager = emailLogManager;
  }

  public ArrayList<EmailLog> getEmails() {
    return emails;
  }

  @Override
  public void prepare() throws Exception {
    List<EmailLog> emailLogs = emailLogManager.findAll().stream().filter(c -> c.getSucces().booleanValue() == false)
      .collect(Collectors.toList());
    emails = new ArrayList<>();
    emails.addAll(emailLogs);
  }

  @Override
  public String save() {
    if (this.canAccessSuperAdmin()) {
      return SUCCESS;
    } else {
      return NOT_AUTHORIZED;
    }
  }

  public void setEmails(ArrayList<EmailLog> emails) {
    this.emails = emails;
  }

  @Override
  public void validate() {
    if (save) {
    }
  }

}