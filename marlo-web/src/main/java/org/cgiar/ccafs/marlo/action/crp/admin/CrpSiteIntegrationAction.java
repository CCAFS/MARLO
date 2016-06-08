/*****************************************************************
 * This file is part of CCAFS Planning and Reporting Platform.
 * CCAFS P&R is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 * CCAFS P&R is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with CCAFS P&R. If not, see <http://www.gnu.org/licenses/>.
 *****************************************************************/

package org.cgiar.ccafs.marlo.action.crp.admin;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.CrpManager;
import org.cgiar.ccafs.marlo.data.manager.LocElementManager;
import org.cgiar.ccafs.marlo.data.model.Crp;
import org.cgiar.ccafs.marlo.data.model.CrpsSiteIntegration;
import org.cgiar.ccafs.marlo.data.model.LocElement;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.inject.Inject;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class CrpSiteIntegrationAction extends BaseAction {


  private static final long serialVersionUID = 1323996683605051647L;

  private CrpManager crpManager;
  private LocElementManager locElementManager;

  private Crp loggedCrp;
  private List<LocElement> locElements;

  @Inject
  public CrpSiteIntegrationAction(APConfig config, CrpManager crpManager, LocElementManager locElementManager) {
    super(config);
    this.crpManager = crpManager;
    this.locElementManager = locElementManager;
  }

  public List<LocElement> getLocElements() {
    return locElements;
  }

  public Crp getLoggedCrp() {
    return loggedCrp;
  }

  @Override
  public void prepare() throws Exception {
    loggedCrp = (Crp) this.getSession().get(APConstants.SESSION_CRP);
    loggedCrp = crpManager.getCrpById(loggedCrp.getId());

    if (loggedCrp.getCrpsSitesIntegrations() != null) {
      loggedCrp.setSiteIntegration(new ArrayList<CrpsSiteIntegration>(loggedCrp.getCrpsSitesIntegrations()));
    }

    locElements = locElementManager.findAll().stream().filter(le -> le.getLocElementType().getId() == 2)
      .collect(Collectors.toList());

    String params[] = {loggedCrp.getAcronym()};
    this.setBasePermission(this.getText(Permission.CRP_ADMIN_BASE_PERMISSION, params));

    if (this.isHttpPost()) {
      loggedCrp.getSiteIntegration().clear();
    }

  }

  public void setLocElements(List<LocElement> locElements) {
    this.locElements = locElements;
  }

  public void setLoggedCrp(Crp loggedCrp) {
    this.loggedCrp = loggedCrp;
  }

}
