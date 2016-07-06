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

package org.cgiar.ccafs.marlo.action.superadmin;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.data.manager.SrfTargetUnitManager;
import org.cgiar.ccafs.marlo.data.model.SrfTargetUnit;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import com.google.inject.Inject;

/**
 * @author Sebastian Amariles - CIAT/CCAFS
 */
public class BoardAction extends BaseAction {

  private static final long serialVersionUID = -793652591843623397L;


  private HashMap<Long, String> idoList;


  private SrfTargetUnitManager srfTargetUnitManager;


  private List<SrfTargetUnit> targetUnitList;


  @Inject
  public BoardAction(APConfig config, SrfTargetUnitManager srfTargetUnitManager) {
    super(config);
    this.srfTargetUnitManager = srfTargetUnitManager;

  }


  public HashMap<Long, String> getIdoList() {
    return idoList;
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


  public void setIdoList(HashMap<Long, String> idoList) {
    this.idoList = idoList;
  }

  public void setTargetUnitList(List<SrfTargetUnit> targetUnitList) {
    this.targetUnitList = targetUnitList;
  }

  @Override
  public void validate() {
    if (save) {

    }
  }

}