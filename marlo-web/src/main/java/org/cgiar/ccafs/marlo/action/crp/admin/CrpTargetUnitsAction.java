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
import org.cgiar.ccafs.marlo.data.manager.CrpTargetUnitManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.SrfTargetUnitManager;
import org.cgiar.ccafs.marlo.data.model.CrpMilestone;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.CrpProgramOutcome;
import org.cgiar.ccafs.marlo.data.model.CrpTargetUnit;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.SrfTargetUnit;
import org.cgiar.ccafs.marlo.data.model.TargetUnitSelect;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class CrpTargetUnitsAction extends BaseAction {


  private static final long serialVersionUID = -1004871247517845386L;


  // Managers
  private SrfTargetUnitManager targetUnitManager;


  private CrpTargetUnitManager crpTargetUnitManager;

  // GlobalUnit Manager
  private GlobalUnitManager crpManager;
  // Variables
  private GlobalUnit loggedCrp;

  private List<SrfTargetUnit> targetUnitsList;

  @Inject
  public CrpTargetUnitsAction(APConfig config, SrfTargetUnitManager targetUnitManager,
    CrpTargetUnitManager crpTargetUnitManager, GlobalUnitManager crpManager) {
    super(config);
    this.targetUnitManager = targetUnitManager;
    this.crpManager = crpManager;
    this.crpTargetUnitManager = crpTargetUnitManager;

  }

  public boolean canBeDeletedCrptargetUnit(long id, String className) {
    Class<?> clazz;
    try {
      clazz = Class.forName(className);


      if (clazz == SrfTargetUnit.class) {
        SrfTargetUnit targetUnit = targetUnitManager.getSrfTargetUnitById(id);


        CrpTargetUnit crpTargetUnit =
          crpTargetUnitManager.getByTargetUnitIdAndCrpId(loggedCrp.getId(), targetUnit.getId());

        if (crpTargetUnit != null) {

          List<CrpProgram> programs =
            new ArrayList<>(loggedCrp.getCrpPrograms().stream().filter(p -> p.isActive()).collect(Collectors.toList()));

          for (CrpProgram crpProgram : programs) {

            List<CrpProgramOutcome> outcomes = new ArrayList<>(
              crpProgram.getCrpProgramOutcomes().stream().filter(o -> o.isActive()).collect(Collectors.toList()));

            for (CrpProgramOutcome crpProgramOutcome : outcomes) {

              SrfTargetUnit targetUnitOutcome = crpProgramOutcome.getSrfTargetUnit();

              if (targetUnitOutcome.equals(targetUnit)) {
                return false;
              }

              List<CrpMilestone> milestones = new ArrayList<>(
                crpProgramOutcome.getCrpMilestones().stream().filter(m -> m.isActive()).collect(Collectors.toList()));

              for (CrpMilestone crpMilestone : milestones) {
                SrfTargetUnit targetUnitMilestone = crpMilestone.getSrfTargetUnit();

                if (targetUnitMilestone.equals(targetUnit)) {
                  return false;
                }
              }

            }

          }

          // if (targetUnit.getCrpProgramOutcomes().stream().filter(o -> o.isActive()).collect(Collectors.toList())
          // .size() > 0) {
          // return false;
          // }
          //
          // if (targetUnit.getCrpMilestones().stream().filter(u -> u.isActive()).collect(Collectors.toList())
          // .size() > 0) {
          // return false;
          // }
        }


      }

      return true;
    } catch (Exception e) {
      return false;
    }
  }

  public GlobalUnit getLoggedCrp() {
    return loggedCrp;
  }

  public List<SrfTargetUnit> getTargetUnitsList() {
    return targetUnitsList;
  }


  @Override
  public void prepare() throws Exception {

    loggedCrp = (GlobalUnit) this.getSession().get(APConstants.SESSION_CRP);
    loggedCrp = crpManager.getGlobalUnitById(loggedCrp.getId());
    String params[] = {loggedCrp.getAcronym()};

    targetUnitsList =
      new ArrayList<>(targetUnitManager.findAll().stream().filter(tu -> tu.isActive()).collect(Collectors.toList()));

    Collections.sort(targetUnitsList, (tu1, tu2) -> tu1.getName().compareTo(tu2.getName()));

    loggedCrp.setTargetUnits(new ArrayList<>());

    for (SrfTargetUnit targetUnit : targetUnitsList) {

      TargetUnitSelect select = new TargetUnitSelect();
      select.setTargetUnit(targetUnit);

      CrpTargetUnit crpTargetUnit =
        crpTargetUnitManager.getByTargetUnitIdAndCrpId(loggedCrp.getId(), targetUnit.getId());

      if (crpTargetUnit != null) {
        boolean check = crpTargetUnit.isActive();
        select.setCheck(check);

      } else {
        select.setCheck(false);
      }

      loggedCrp.getTargetUnits().add(select);

    }


    this.setBasePermission(this.getText(Permission.CRP_ADMIN_BASE_PERMISSION, params));

    if (this.isHttpPost()) {

      if (targetUnitsList != null) {

        targetUnitsList.clear();
      }

      if (loggedCrp.getTargetUnits() != null) {
        loggedCrp.getTargetUnits().clear();
      }

    }
  }

  @Override
  public String save() {

    if (this.hasPermission("*")) {

      for (TargetUnitSelect targetUnit : loggedCrp.getTargetUnits()) {

        CrpTargetUnit crpTargetUnit =
          crpTargetUnitManager.getByTargetUnitIdAndCrpId(loggedCrp.getId(), targetUnit.getTargetUnit().getId());

        if (crpTargetUnit != null) {

          if (targetUnit.getCheck() != null) {
            crpTargetUnit.setActive(targetUnit.getCheck());
          } else {
            crpTargetUnit.setActive(false);
          }

          crpTargetUnitManager.saveCrpTargetUnit(crpTargetUnit);

        } else {

          if (targetUnit.getCheck() != null && targetUnit.getCheck()) {

            CrpTargetUnit crpTargetUnitNew = new CrpTargetUnit();

            crpTargetUnitNew.setActive(true);
            crpTargetUnitNew.setActiveSince(new Date());
            crpTargetUnitNew.setCreatedBy(this.getCurrentUser());
            crpTargetUnitNew.setModifiedBy(this.getCurrentUser());
            crpTargetUnitNew.setModificationJustification("");
            crpTargetUnitNew.setCrp(loggedCrp);
            crpTargetUnitNew.setSrfTargetUnit(targetUnit.getTargetUnit());

            crpTargetUnitManager.saveCrpTargetUnit(crpTargetUnitNew);

          }

        }

      }

      return SUCCESS;
    } else {
      return NOT_AUTHORIZED;
    }

  }

  public void setLoggedCrp(GlobalUnit loggedCrp) {
    this.loggedCrp = loggedCrp;
  }


  public void setTargetUnitsList(List<SrfTargetUnit> targetUnitsList) {
    this.targetUnitsList = targetUnitsList;
  }


}
