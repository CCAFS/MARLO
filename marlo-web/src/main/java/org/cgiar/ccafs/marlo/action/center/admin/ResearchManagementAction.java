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
import org.cgiar.ccafs.marlo.data.manager.ICenterAreaManager;
import org.cgiar.ccafs.marlo.data.manager.ICenterLeaderManager;
import org.cgiar.ccafs.marlo.data.manager.ICenterManager;
import org.cgiar.ccafs.marlo.data.manager.ICenterProgramManager;
import org.cgiar.ccafs.marlo.data.model.Center;
import org.cgiar.ccafs.marlo.data.model.CenterArea;
import org.cgiar.ccafs.marlo.data.model.CenterLeader;
import org.cgiar.ccafs.marlo.data.model.CenterProgram;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.google.inject.Inject;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class ResearchManagementAction extends BaseAction {

  private static final long serialVersionUID = -8241378443798479147L;

  private ICenterManager centerService;
  private ICenterAreaManager centerAreaService;
  private ICenterProgramManager centerProgramService;
  private ICenterLeaderManager centerLeaderService;
  private Center loggedCenter;
  private List<CenterArea> centerAreas;

  @Inject
  public ResearchManagementAction(APConfig config, ICenterManager centerService, ICenterAreaManager areaService,
    ICenterProgramManager programService, ICenterLeaderManager centerLeaderService) {
    super(config);
    this.centerService = centerService;
    this.centerAreaService = areaService;
    this.centerProgramService = programService;
    this.centerLeaderService = centerLeaderService;
  }

  /*
   * Check if center area, programs and/or leaders were deleted
   */
  private void checkDeleted() {
    // Get database center area
    List<CenterArea> centerAreasDB = new ArrayList<>(centerAreaService.findAll().stream()
      .filter(ca -> ca.isActive() && ca.getResearchCenter().equals(loggedCenter)).collect(Collectors.toList()));
    // Check deleted center area
    for (CenterArea centerAreaDB : centerAreasDB) {
      if (!centerAreas.contains(centerAreaDB)) {
        // delete centerLeaders
        List<CenterLeader> areaLeadersDB =
          centerAreaDB.getResearchLeaders().stream().filter(rl -> rl.isActive()).collect(Collectors.toList());
        for (CenterLeader centerLeader : areaLeadersDB) {
          centerLeaderService.deleteResearchLeader(centerLeader.getId());
        }
        // delete research programs
        List<CenterProgram> centerProgramsDB = centerAreaDB.getResearchPrograms().stream()
          .filter(cps -> cps.isActive() && cps.getResearchArea().getId() == centerAreaDB.getId())
          .collect(Collectors.toList());

        for (CenterProgram centerProgramDB : centerProgramsDB) {
          List<CenterLeader> programLeadersDB =
            centerProgramDB.getResearchLeaders().stream().filter(rl -> rl.isActive()).collect(Collectors.toList());
          // delete program leaders
          for (CenterLeader centerLeaderDB : programLeadersDB) {
            centerLeaderService.deleteResearchLeader(centerLeaderDB.getId());
          }
          centerProgramService.deleteProgram(centerProgramDB.getId());
        }
        // delete center area
        centerAreaService.deleteResearchArea(centerAreaDB.getId());
      }
      // check area leaders

    }

  }


  public List<CenterArea> getCenterAreas() {
    return centerAreas;
  }


  public Center getLoggedCenter() {
    return loggedCenter;
  }


  @Override
  public void prepare() throws Exception {
    loggedCenter = (Center) this.getSession().get(APConstants.SESSION_CENTER);
    loggedCenter = centerService.getCrpById(loggedCenter.getId());

    String params[] = {loggedCenter.getAcronym() + ""};
    this.setBasePermission(this.getText(Permission.CENTER_ADMIN_BASE_PERMISSION, params));
    // Get loggedCenter centerAreas
    centerAreas = new ArrayList<>(centerAreaService.findAll().stream()
      .filter(ca -> ca.isActive() && ca.getResearchCenter().equals(loggedCenter)).collect(Collectors.toList()));
    for (CenterArea centerArea : centerAreas) {
      // Set area leaders
      centerArea
        .setLeaders(centerArea.getResearchLeaders().stream().filter(rl -> rl.isActive()).collect(Collectors.toList()));
      // Set center programs

      centerArea.setPrograms(centerArea.getResearchPrograms().stream()
        .filter(cps -> cps.isActive() && cps.getResearchArea().getId() == centerArea.getId())
        .collect(Collectors.toList()));
      // set program leader
      for (CenterProgram centerProgram : centerArea.getPrograms()) {
        centerProgram.setLeaders(
          centerProgram.getResearchLeaders().stream().filter(rl -> rl.isActive()).collect(Collectors.toList()));
      }
    }
    if (this.isHttpPost()) {
      if (centerAreas != null) {
        centerAreas.clear();
      }
    }
  }

  @Override
  public String save() {
    if (this.hasPermission("*")) {
      this.checkDeleted();


      // Check changes
      for (CenterArea centerArea : centerAreas) {
        // Check if is a new one
        if (centerArea.getId() == null || centerArea.getId() == -1) {
          CenterArea newCenterArea = new CenterArea();

          newCenterArea.setActive(true);
          newCenterArea.setCreatedBy(this.getCurrentUser());
          newCenterArea.setModifiedBy(this.getCurrentUser());
          newCenterArea.setActiveSince(new Date());
          newCenterArea.setName(centerArea.getName());
          newCenterArea.setAcronym(centerArea.getAcronym());
          newCenterArea.setResearchCenter(loggedCenter);
          newCenterArea.setModificationJustification("");
          centerAreaService.save(newCenterArea);
          // save area leaders
          List<CenterLeader> centerLeaders =
            newCenterArea.getResearchLeaders().stream().filter(rl -> rl.isActive()).collect(Collectors.toList());
          for (CenterLeader centerLeader : centerLeaders) {
            centerLeaderService.saveResearchLeader(centerLeader);
          }
          // save new programs
          List<CenterProgram> newCenterPrograms = newCenterArea.getResearchPrograms().stream()
            .filter(cps -> cps.isActive() && cps.getResearchArea().getId() == centerArea.getId())
            .collect(Collectors.toList());
          for (CenterProgram newCenterProgram : newCenterPrograms) {
            // Save center program
            centerProgramService.saveProgram(newCenterProgram);
            List<CenterLeader> programLeaders =
              newCenterProgram.getResearchLeaders().stream().filter(rl -> rl.isActive()).collect(Collectors.toList());
            for (CenterLeader centerLeader : programLeaders) {
              centerLeaderService.saveResearchLeader(centerLeader);
            }
          }

        } else {
          // check if there are changes
          CenterArea centerAreaDB = centerAreaService.find(centerArea.getId());
          if (!centerArea.equals(centerAreaDB)) {
            centerAreaService.save(centerArea);
            List<CenterLeader> areaLeaders =
              centerArea.getResearchLeaders().stream().filter(rl -> rl.isActive()).collect(Collectors.toList());
            List<CenterLeader> areaLeadersDB =
              centerAreaDB.getResearchLeaders().stream().filter(rl -> rl.isActive()).collect(Collectors.toList());
            if (!areaLeaders.equals(areaLeadersDB)) {
              for (CenterLeader areaLeader : areaLeaders) {
                if (areaLeader.getId() == null || areaLeader.getId() == -1) {
                  // new area leader
                  centerLeaderService.saveResearchLeader(areaLeader);

                }
              }
            }
            // TODO: Check if there are area leaders, program or program leaders changes.
          }
        }

      }

      return SUCCESS;
    } else {
      return NOT_AUTHORIZED;
    }
  }


  public void setCenterAreas(List<CenterArea> centerAreas) {
    this.centerAreas = centerAreas;
  }


  public void setLoggedCenter(Center loggedCenter) {
    this.loggedCenter = loggedCenter;
  }
}
