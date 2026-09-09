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

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luis Benavides - CIAT/CCAFS
 */
public class QAReportsAction extends BaseAction {

  private static final long serialVersionUID = 6081410201110891882L;

  private static final Logger LOG = LoggerFactory.getLogger(QAReportsAction.class);

  // Managers and mappers
  private QATokenAuthManager qATokenManager;

  // Front
  private QATokenAuth qATokenAuth;

  @Inject
  public QAReportsAction(APConfig config, QATokenAuthManager qATokenManager) {
    super(config);
    this.qATokenManager = qATokenManager;
  }


  public QATokenAuth getqATokenAuth() {
    return qATokenAuth;
  }


  /**
   * Composes the display name sent to the QA service from the name parts that are actually present.
   * <p>
   * {@code User.getComposedCompleteName()} is deliberately not reused here: it concatenates the two fields
   * without guarding them, which is the defect this method exists to avoid rather than relocate.
   *
   * @param user the signed-in user
   * @return the name, or an empty string when neither part is set -- never the text {@code "null"}
   */
  private String composeName(User user) {
    String firstName = StringUtils.trimToEmpty(user.getFirstName());
    String lastName = StringUtils.trimToEmpty(user.getLastName());
    return StringUtils.trimToEmpty(firstName + " " + lastName);
  }

  /**
   * Builds the QA token for the signed-in user.
   * <p>
   * Every value that can legitimately be absent is normalized to an empty string first. The reason is that
   * {@code QATokenAuthMySQLDAO.generate} composes its call by string concatenation, so a {@code null}
   * reaching it is stringified by Java into the four-character text {@code "null"} <b>before</b> SQL sees
   * it: the token is still generated, but {@code qa_token_auth.username} and the identity forwarded to
   * qa.cgiar.org record the word "null" as though it were the person's login. An absent value must look
   * absent.
   * <p>
   * {@code users.username} is the value this actually protects. It is populated only from Active Directory,
   * and the Cognito migration accepts a null in that column, so accounts created after the AD retirement
   * will reach here with nothing set -- see
   * {@code docs/specs/changes/migrate-ad-authentication-to-cognito/analysis/username-field-audit.md} and
   * Jira A2-2460.
   * <p>
   * {@code email} and {@code id} are passed unguarded on purpose: the email is the key every account is
   * resolved by and the id is the primary key of the authenticated session, so neither can be null here,
   * and guarding them would imply a doubt that does not exist.
   */
  @Override
  public void prepare() {
    User user = this.getCurrentUser();
    qATokenAuth = qATokenManager.generateQATokenAuth(this.composeName(user),
      StringUtils.trimToEmpty(user.getUsername()), user.getEmail(),
      StringUtils.trimToEmpty(this.getCurrentGlobalUnit().getSmoCode()), user.getId().toString());
  }


  public void setqATokenAuth(QATokenAuth qATokenAuth) {
    this.qATokenAuth = qATokenAuth;
  }


}
