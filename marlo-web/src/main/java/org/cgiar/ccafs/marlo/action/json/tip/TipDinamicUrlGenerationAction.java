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

package org.cgiar.ccafs.marlo.action.json.tip;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.data.manager.TipParametersManager;
import org.cgiar.ccafs.marlo.data.model.TipParameters;

import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TipDinamicUrlGenerationAction extends BaseAction {

  private static final long serialVersionUID = 3229622720197560976L;
  private static final Logger LOG = LoggerFactory.getLogger(TipDinamicUrlGenerationAction.class);

  // Front-end
  private String dinamicTipURL;

  @Inject
  private TipParametersManager tipParametersManager;

  @Inject
  public TipDinamicUrlGenerationAction() {

  }

  public String createDinamicURL() {
    String tipURL = null;
    boolean isCGIARUser = true;

    try {
      String userEmail = "", token = "", loginService = "";
      if (this.getCurrentUser() != null && this.getCurrentUser().getEmail() != null) {
        userEmail = this.getCurrentUser().getEmail();
        if (this.getCurrentUser().isCgiarUser() == true) {
          isCGIARUser = true;
        } else {
          isCGIARUser = false;
        }
      }

      List<TipParameters> tipParameters = tipParametersManager.findAll();
      if (isCGIARUser) {
        if (tipParameters != null && !tipParameters.isEmpty() && tipParameters.get(0) != null) {
          if (tipParameters.get(0).getTipTokenService() != null) {
            token = tipParameters.get(0).getTokenValue();
          }
          if (tipParameters.get(0).getTipLoginService() != null) {
            loginService = tipParameters.get(0).getTipLoginService();
          }
        }
        tipURL = loginService + "/" + token + "/staff/" + userEmail;
      } else {
        // Not CGIAR User
        if (tipParameters != null && tipParameters.get(0) != null && tipParameters.get(0).getTipBaseUrl() != null) {
          tipURL = tipParameters.get(0).getTipBaseUrl();
        }
      }
    } catch (NumberFormatException e) {
      LOG.error("Error getting tip URL: " + e);
    }

    return tipURL;
  }

  @Override
  public String execute() throws Exception {

    if (this.createDinamicURL() != null) {
      this.dinamicTipURL = this.createDinamicURL();
      return SUCCESS;
    }
    return ERROR;
  }

  public String getDinamicTipURL() {
    return dinamicTipURL;
  }

  @Override
  public void prepare() throws Exception {
  }

  public void setDinamicTipURL(String dinamicTipURL) {
    this.dinamicTipURL = dinamicTipURL;
  }
}