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

package org.cgiar.ccafs.marlo.action.center.admin;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.ICenterObjectiveManager;
import org.cgiar.ccafs.marlo.data.model.CenterObjective;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class StrategicObjectivesAction extends BaseAction {


  private static final long serialVersionUID = 4240576135659881112L;


  private ICenterObjectiveManager objectiveService;


  // GlobalUnit Manager
  private GlobalUnitManager centerService;


  private GlobalUnit loggedCenter;


  private List<CenterObjective> objectives;


  @Inject
  public StrategicObjectivesAction(APConfig config, ICenterObjectiveManager objectiveService,
    GlobalUnitManager centerService) {
    super(config);
    this.objectiveService = objectiveService;
    this.centerService = centerService;
  }

  public GlobalUnit getLoggedCenter() {
    return loggedCenter;
  }

  public List<CenterObjective> getObjectives() {
    return objectives;
  }


  @Override
  public void prepare() throws Exception {
    loggedCenter = (GlobalUnit) this.getSession().get(APConstants.SESSION_CRP);
    loggedCenter = centerService.getGlobalUnitById(loggedCenter.getId());

    objectives = new ArrayList<>(objectiveService.findAll().stream()
      .filter(o -> o.isActive() && o.getResearchCenter().equals(loggedCenter)).collect(Collectors.toList()));

    String params[] = {loggedCenter.getAcronym() + ""};
    this.setBasePermission(this.getText(Permission.CENTER_ADMIN_BASE_PERMISSION, params));

    if (this.isHttpPost()) {
      if (objectives != null) {
        objectives.clear();
      }
    }
  }

  @Override
  public String save() {
    if (this.hasPermission("*")) {
      List<CenterObjective> objectivesDB = new ArrayList<>(objectiveService.findAll().stream()
        .filter(o -> o.isActive() && o.getResearchCenter().equals(loggedCenter)).collect(Collectors.toList()));

      for (CenterObjective researchObjective : objectivesDB) {
        if (!objectives.contains(researchObjective)) {
          objectiveService.deleteResearchObjective(researchObjective.getId());
        }
      }

      for (CenterObjective researchObjective : objectives) {
        if (researchObjective.getId() == null || researchObjective.getId() == -1) {
          CenterObjective researchObjectiveNew = new CenterObjective();

          researchObjectiveNew.setActive(true);
          researchObjectiveNew.setCreatedBy(this.getCurrentUser());
          researchObjectiveNew.setModifiedBy(this.getCurrentUser());
          researchObjectiveNew.setActiveSince(new Date());
          researchObjectiveNew.setModificationJustification("");
          researchObjectiveNew.setResearchCenter(loggedCenter);
          researchObjectiveNew.setObjective(researchObjective.getObjective());

          objectiveService.saveResearchObjective(researchObjectiveNew);

        }
      }
      return SUCCESS;
    } else {
      return NOT_AUTHORIZED;
    }
  }

  public void setLoggedCenter(GlobalUnit loggedCenter) {
    this.loggedCenter = loggedCenter;
  }


  public void setObjectives(List<CenterObjective> objectives) {
    this.objectives = objectives;
  }
}
