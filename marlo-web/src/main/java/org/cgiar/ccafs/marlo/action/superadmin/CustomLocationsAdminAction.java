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
import org.cgiar.ccafs.marlo.data.manager.CrpTargetUnitManager;
import org.cgiar.ccafs.marlo.data.manager.SrfTargetUnitManager;
import org.cgiar.ccafs.marlo.data.model.CrpTargetUnit;
import org.cgiar.ccafs.marlo.data.model.SrfTargetUnit;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.google.inject.Inject;

/**
 * @author Christian Garcia - CIAT/CCAFS
 */
public class CustomLocationsAdminAction extends BaseAction {


  /**
   * 
   */
  private static final long serialVersionUID = -8068503147148935293L;


  private SrfTargetUnitManager srfTargetUnitManager;
  private CrpTargetUnitManager crpTargetUnitManager;


  private List<SrfTargetUnit> targetUnitList;


  @Inject
  public CustomLocationsAdminAction(APConfig config, SrfTargetUnitManager srfTargetUnitManager,
    CrpTargetUnitManager crpTargetUnitManager) {
    super(config);
    this.srfTargetUnitManager = srfTargetUnitManager;
    this.crpTargetUnitManager = crpTargetUnitManager;

  }


  public List<SrfTargetUnit> getTargetUnitList() {
    return targetUnitList;
  }


  @Override
  public void prepare() throws Exception {


    targetUnitList = new ArrayList<>();
    if (srfTargetUnitManager.findAll() != null) {
      List<SrfTargetUnit> targetUnits =
        srfTargetUnitManager.findAll().stream().filter(c -> c.isActive()).collect(Collectors.toList());
      for (SrfTargetUnit srfTargetUnit : targetUnits) {
        targetUnitList.add(srfTargetUnit);
      }
    }
    if (this.isHttpPost()) {
      targetUnitList.clear();
    }

  }


  @Override
  public String save() {
    if (this.canAccessSuperAdmin()) {

      List<SrfTargetUnit> targetsPreview =
        srfTargetUnitManager.findAll().stream().filter(c -> c.isActive()).collect(Collectors.toList());
      // Removing crp flagship program type
      if (targetsPreview != null) {
        for (SrfTargetUnit srfTargetUnit : targetsPreview) {
          if (!targetUnitList.contains(srfTargetUnit)) {

            if (srfTargetUnit.getCrpTargetUnits() != null) {
              List<CrpTargetUnit> crpTargetUnits = new ArrayList<>(
                srfTargetUnit.getCrpTargetUnits().stream().filter(tu -> tu.isActive()).collect(Collectors.toList()));

              if (!crpTargetUnits.isEmpty()) {
                for (CrpTargetUnit crpTargetUnit : crpTargetUnits) {
                  crpTargetUnitManager.deleteCrpTargetUnit(crpTargetUnit.getId());
                }
              }
            }

            srfTargetUnitManager.deleteSrfTargetUnit(srfTargetUnit.getId());

          }
        }
      }

      for (SrfTargetUnit srfTargetUnit : targetUnitList) {
        if (srfTargetUnit.getId() == null) {

          srfTargetUnit.setActive(true);
          srfTargetUnit.setCreatedBy(this.getCurrentUser());
          srfTargetUnit.setModifiedBy(this.getCurrentUser());
          srfTargetUnit.setModificationJustification("");
          srfTargetUnit.setActiveSince(new Date());

          srfTargetUnitManager.saveSrfTargetUnit(srfTargetUnit);
        } else {
          SrfTargetUnit srfTargetUnitDB = srfTargetUnitManager.getSrfTargetUnitById(srfTargetUnit.getId());

          srfTargetUnit.setActive(true);
          srfTargetUnit.setCreatedBy(srfTargetUnitDB.getCreatedBy());
          srfTargetUnit.setModifiedBy(this.getCurrentUser());
          srfTargetUnit.setModificationJustification("");
          srfTargetUnit.setActiveSince(srfTargetUnitDB.getActiveSince());

          srfTargetUnitManager.saveSrfTargetUnit(srfTargetUnit);
        }
      }
      Collection<String> messages = this.getActionMessages();
      if (!messages.isEmpty()) {
        String validationMessage = messages.iterator().next();
        this.setActionMessages(null);
        this.addActionWarning(this.getText("saving.saved") + validationMessage);
      } else {
        this.addActionMessage(this.getText("saving.saved"));
      }
      messages = this.getActionMessages();
      return SUCCESS;
    } else {
      return NOT_AUTHORIZED;
    }
  }


  public void setTargetUnitList(List<SrfTargetUnit> targetUnitList) {
    this.targetUnitList = targetUnitList;
  }

}