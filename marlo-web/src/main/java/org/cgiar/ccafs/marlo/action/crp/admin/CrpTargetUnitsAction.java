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

package org.cgiar.ccafs.marlo.action.crp.admin;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.CrpManager;
import org.cgiar.ccafs.marlo.data.manager.CrpTargetUnitManager;
import org.cgiar.ccafs.marlo.data.manager.SrfTargetUnitManager;
import org.cgiar.ccafs.marlo.data.model.Crp;
import org.cgiar.ccafs.marlo.data.model.SrfTargetUnit;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.inject.Inject;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class CrpTargetUnitsAction extends BaseAction {

  private static final long serialVersionUID = -1004871247517845386L;

  // Managers
  private SrfTargetUnitManager targetUnitManager;
  private CrpTargetUnitManager crpTargetUnitManager;
  private CrpManager crpManager;

  // Variables
  private Crp loggedCrp;
  private List<SrfTargetUnit> targetUnitsList;

  @Inject
  public CrpTargetUnitsAction(APConfig config, SrfTargetUnitManager targetUnitManager,
    CrpTargetUnitManager crpTargetUnitManager, CrpManager crpManager) {
    super(config);
    this.targetUnitManager = targetUnitManager;
    this.crpManager = crpManager;
    this.crpTargetUnitManager = crpTargetUnitManager;

  }

  public Crp getLoggedCrp() {
    return loggedCrp;
  }

  public List<SrfTargetUnit> getTargetUnitsList() {
    return targetUnitsList;
  }

  @Override
  public void prepare() throws Exception {

    loggedCrp = (Crp) this.getSession().get(APConstants.SESSION_CRP);
    loggedCrp = crpManager.getCrpById(loggedCrp.getId());
    String params[] = {loggedCrp.getAcronym()};


    targetUnitsList =
      new ArrayList<>(targetUnitManager.findAll().stream().filter(tu -> tu.isActive()).collect(Collectors.toList()));

    loggedCrp.setTargetUnits(new ArrayList<>(
      loggedCrp.getCrpTargetUnits().stream().filter(ctu -> ctu.isActive()).collect(Collectors.toList())));

    this.setBasePermission(this.getText(Permission.CRP_ADMIN_BASE_PERMISSION, params));

    if (this.isHttpPost()) {

      if (loggedCrp.getTargetUnits() != null) {
        loggedCrp.getTargetUnits().clear();
      }

      if (targetUnitsList != null) {
        targetUnitsList.clear();
      }

    }
  }

  public void setLoggedCrp(Crp loggedCrp) {
    this.loggedCrp = loggedCrp;
  }


  public void setTargetUnitsList(List<SrfTargetUnit> targetUnitsList) {
    this.targetUnitsList = targetUnitsList;
  }


}
