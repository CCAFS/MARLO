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
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

/**
 * @author Sebastian Amariles - CIAT/CCAFS
 */
public class BoardAction extends BaseAction {

  private static final long serialVersionUID = -793652591843623397L;


  private Map<Long, String> idoList;


  private final SrfTargetUnitManager srfTargetUnitManager;
  private final CrpTargetUnitManager crpTargetUnitManager;


  private List<SrfTargetUnit> targetUnitList;


  @Inject
  public BoardAction(APConfig config, SrfTargetUnitManager srfTargetUnitManager,
    CrpTargetUnitManager crpTargetUnitManager) {
    super(config);
    this.srfTargetUnitManager = srfTargetUnitManager;
    this.crpTargetUnitManager = crpTargetUnitManager;

  }


  public Map<Long, String> getIdoList() {
    return idoList;
  }


  public List<SrfTargetUnit> getTargetUnitList() {
    return targetUnitList;
  }


  @Override
  public void prepare() throws Exception {

    if (!this.isHttpPost()) {
      targetUnitList = new ArrayList<>();
      if (srfTargetUnitManager.findAll() != null) {
        List<SrfTargetUnit> targetUnits =
          srfTargetUnitManager.findAll().stream().filter(c -> c.isActive()).toList();
        targetUnitList.addAll(targetUnits);
      }
    } else {
      if (targetUnitList == null) {
        targetUnitList = new ArrayList<>();
      }
    }

  }


  @Override
  public String save() {
    if (this.canAccessSuperAdmin()) {
      this.deleteRemovedTargetUnits();
      this.saveTargetUnits();
      this.addSuccessMessage();
      return SUCCESS;
    } else {
      return NOT_AUTHORIZED;
    }
  }

  private void deleteRemovedTargetUnits() {
    List<SrfTargetUnit> targetsPreview =
      srfTargetUnitManager.findAll().stream().filter(c -> c.isActive()).toList();

    List<Long> incomingIds = targetUnitList.stream()
      .filter(tu -> tu != null && tu.getId() != null && tu.getId() != -1L)
      .map(SrfTargetUnit::getId)
      .toList();

    if (targetsPreview != null) {
      for (SrfTargetUnit srfTargetUnit : targetsPreview) {
        if (!incomingIds.contains(srfTargetUnit.getId())) {
          this.deleteCrpTargetUnitsForTarget(srfTargetUnit);
          srfTargetUnitManager.deleteSrfTargetUnit(srfTargetUnit.getId());
        }
      }
    }
  }

  private void deleteCrpTargetUnitsForTarget(SrfTargetUnit srfTargetUnit) {
    if (srfTargetUnit.getCrpTargetUnits() != null) {
      List<CrpTargetUnit> crpTargetUnits = srfTargetUnit.getCrpTargetUnits().stream()
        .filter(tu -> tu.isActive())
        .toList();

      for (CrpTargetUnit crpTargetUnit : crpTargetUnits) {
        crpTargetUnitManager.deleteCrpTargetUnit(crpTargetUnit.getId());
      }
    }
  }

  private void saveTargetUnits() {
    for (SrfTargetUnit srfTargetUnit : targetUnitList) {
      if (srfTargetUnit != null) {
        if (srfTargetUnit.getId() == null || srfTargetUnit.getId() == -1L) {
          srfTargetUnitManager.saveSrfTargetUnit(srfTargetUnit);
        } else {
          SrfTargetUnit srfTargetUnitDb = srfTargetUnitManager.getSrfTargetUnitById(srfTargetUnit.getId());
          srfTargetUnitDb.setName(srfTargetUnit.getName());
          srfTargetUnitManager.saveSrfTargetUnit(srfTargetUnitDb);
        }
      }
    }
  }

  private void addSuccessMessage() {
    Collection<String> messages = this.getActionMessages();
    if (!messages.isEmpty()) {
      String validationMessage = messages.iterator().next();
      this.setActionMessages(null);
      this.addActionWarning(this.getText("saving.saved") + validationMessage);
    } else {
      this.addActionMessage("message:" + this.getText("saving.saved"));
    }
  }

  public void setIdoList(Map<Long, String> idoList) {
    this.idoList = idoList;
  }

  public void setTargetUnitList(List<SrfTargetUnit> targetUnitList) {
    this.targetUnitList = targetUnitList;
  }

}