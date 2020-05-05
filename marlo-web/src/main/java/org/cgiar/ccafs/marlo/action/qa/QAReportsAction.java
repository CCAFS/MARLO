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

package org.cgiar.ccafs.marlo.action.qa;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.data.manager.QATokenAuthManager;
import org.cgiar.ccafs.marlo.data.model.QATokenAuth;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.MD5Convert;

import java.util.Calendar;
import java.util.Date;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luis Benavides - CIAT/CCAFS
 */
public class QAReportsAction extends BaseAction {

  private static final long serialVersionUID = 6081410201110891882L;

  private static final Logger LOG = LoggerFactory.getLogger(QAReportsAction.class);

  private static final int SECONDS_EXPIRATION = 60;

  // Managers and mappers
  private QATokenAuthManager qATokenManager;

  // Front
  private QATokenAuth qATokenAuth;

  @Inject
  public QAReportsAction(APConfig config, QATokenAuthManager qATokenManager) {
    super(config);
    this.qATokenManager = qATokenManager;
  }


  private QATokenAuth createToken(String name, String username, String email, String smoCode, User user) {
    Calendar c = Calendar.getInstance();
    String textBeforeMD5 = null;
    QATokenAuth qATokenAuth = new QATokenAuth();

    Date currentDate = c.getTime();
    c.add(Calendar.SECOND, SECONDS_EXPIRATION);
    Date expirationDate = c.getTime();

    textBeforeMD5 = user.getId().toString() + username.trim() + currentDate;

    qATokenAuth.setCreatedAt(currentDate);
    qATokenAuth.setUpdatedAt(currentDate);
    qATokenAuth.setCrpId(smoCode.trim());
    qATokenAuth.setToken(MD5Convert.stringToMD5(textBeforeMD5));
    qATokenAuth.setExpirationDate(expirationDate);
    qATokenAuth.setUsername(username.trim());
    qATokenAuth.setEmail(email.trim());
    qATokenAuth.setName(name.trim());
    qATokenAuth.setAppUser(user.getId());

    qATokenManager.saveQATokenAuth(qATokenAuth);

    return qATokenAuth;
  }


  public QATokenAuth getqATokenAuth() {
    return qATokenAuth;
  }


  @Override
  public void prepare() {

    qATokenAuth = this.createToken(this.getCurrentUser().getFirstName() + " " + this.getCurrentUser().getLastName(),
      this.getCurrentUser().getUsername(), this.getCurrentUser().getEmail(), this.getCurrentGlobalUnit().getSmoCode(),
      this.getCurrentUser());
  }


  public void setqATokenAuth(QATokenAuth qATokenAuth) {
    this.qATokenAuth = qATokenAuth;
  }


}
