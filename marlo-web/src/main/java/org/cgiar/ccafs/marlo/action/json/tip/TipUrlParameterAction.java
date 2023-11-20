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
import org.cgiar.ccafs.marlo.config.APConstants;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TipUrlParameterAction extends BaseAction {

  private static final long serialVersionUID = 3229622720197560976L;
  private static final Logger LOG = LoggerFactory.getLogger(TipUrlParameterAction.class);

  // Front-end
  private String url;

  @Inject
  public TipUrlParameterAction() {

  }

  @Override
  public String execute() throws Exception {

    if (this.getTipURL() != null) {
      this.url = this.getTipURL();
      return SUCCESS;
    }
    return ERROR;
  }

  public String getTipURL() {
    String tipURL = null;

    if (APConstants.CRP_AICCRA_TIP_URL != null && this.getSession().get(APConstants.CRP_AICCRA_TIP_URL) != null) {
      try {
        tipURL = (String) this.getSession().get(APConstants.CRP_AICCRA_TIP_URL);
      } catch (NumberFormatException e) {
        LOG.error("Error getting tip URL: " + e);
      }
    } else {
      LOG.error("CRP_AICCRA_TIP_URL is null or not found in session.");
    }
    return tipURL;
  }

  @Override
  public String getUrl() {
    return url;
  }

  @Override
  public void prepare() throws Exception {
  }

  @Override
  public void setUrl(String url) {
    this.url = url;
  }
}
