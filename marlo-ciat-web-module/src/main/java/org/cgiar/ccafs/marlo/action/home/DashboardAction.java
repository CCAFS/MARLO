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

package org.cgiar.ccafs.marlo.action.home;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConfig;
import org.cgiar.ccafs.marlo.data.model.Center;
import org.cgiar.ccafs.marlo.data.service.ICenterService;
import org.cgiar.ccafs.marlo.utils.APConstants;

import com.google.inject.Inject;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class DashboardAction extends BaseAction {

  private static final long serialVersionUID = -4006598729888638344L;
  private Center loggedCrp;
  private ICenterService crpManager;

  /**
   * @param config
   * @param loggedCrp
   * @param crpManager
   */
  @Inject
  public DashboardAction(APConfig config, ICenterService crpManager) {
    super(config);
    this.crpManager = crpManager;
  }

  public Center getLoggedCrp() {
    return loggedCrp;
  }

  @Override
  public void prepare() throws Exception {
    loggedCrp = (Center) this.getSession().get(APConstants.SESSION_CENTER);
    loggedCrp = crpManager.getCrpById(loggedCrp.getId());
  }

  public void setLoggedCrp(Center loggedCrp) {
    this.loggedCrp = loggedCrp;
  }

}
