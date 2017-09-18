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
import org.cgiar.ccafs.marlo.data.manager.CenterAllTypesManager;
import org.cgiar.ccafs.marlo.data.manager.ICenterAreaManager;
import org.cgiar.ccafs.marlo.data.manager.ICenterLeaderManager;
import org.cgiar.ccafs.marlo.data.manager.ICenterManager;
import org.cgiar.ccafs.marlo.data.manager.ICenterProgramManager;
import org.cgiar.ccafs.marlo.data.model.Center;
import org.cgiar.ccafs.marlo.data.model.CenterArea;
import org.cgiar.ccafs.marlo.data.model.CenterLeader;
import org.cgiar.ccafs.marlo.data.model.CenterLeaderTypeEnum;
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
 * @author Andrés Valencia - CIAT/CCAFS
 */
public class ResearchManagementAction extends BaseAction {

  private static final long serialVersionUID = -8241378443798479147L;

  private ICenterManager centerService;
  private ICenterAreaManager centerAreaService;
  private ICenterProgramManager centerProgramService;
  private ICenterLeaderManager centerLeaderService;
  private CenterAllTypesManager centerAllTypesService;
  private Center loggedCenter;
  private List<CenterArea> centerAreas;

  @Inject
  public ResearchManagementAction(APConfig config, ICenterManager centerService, ICenterAreaManager areaService,
    ICenterProgramManager programService, ICenterLeaderManager centerLeaderService,
    CenterAllTypesManager centerAllTypesService) {
    super(config);
    this.centerService = centerService;
    this.centerAreaService = areaService;
    this.centerProgramService = programService;
    this.centerLeaderService = centerLeaderService;
    this.centerAllTypesService = centerAllTypesService;
  }

  /*
   * Check if center area, program or leaders has changed
   */
  private void checkChanges() {
    // Check changes
    if (centerAreas != null) {
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
          List<CenterLeader> areaLeaders = centerArea.getLeaders();
          if (areaLeaders != null) {
            for (CenterLeader areaLeader : areaLeaders) {
              CenterLeader newareaLeader = new CenterLeader();
              newareaLeader.setActive(true);
              newareaLeader.setActiveSince(new Date());
              newareaLeader.setCreatedBy(this.getCurrentUser());
              newareaLeader.setModificationJustification("");
              newareaLeader.setModifiedBy(this.getCurrentUser());
              newareaLeader.setResearchArea(newCenterArea);
              newareaLeader.setResearchCenter(loggedCenter);
              newareaLeader.setType(
                centerAllTypesService.getCenterAllTypesById(CenterLeaderTypeEnum.RESEARCH_AREA_LEADER_TYPE.getValue()));
              newareaLeader.setUser(areaLeader.getUser());
              centerLeaderService.saveResearchLeader(newareaLeader);
            }
          }

          // save new programs
          List<CenterProgram> newCenterPrograms = centerArea.getPrograms();
          if (newCenterPrograms != null) {
            for (CenterProgram centerProgram : newCenterPrograms) {
              CenterProgram newCenterProgram = new CenterProgram();
              // Save center program
              newCenterProgram.setAcronym(centerProgram.getAcronym());
              newCenterProgram.setName(centerProgram.getName());
              newCenterProgram.setActive(true);
              newCenterProgram.setActiveSince(new Date());
              newCenterProgram.setCreatedBy(this.getCurrentUser());
              newCenterProgram.setModificationJustification("");
              newCenterProgram.setModifiedBy(this.getCurrentUser());
              newCenterProgram.setProgramType(centerAllTypesService.getCenterAllTypesById(
                Long.parseLong(this.getSession().get(APConstants.CENTER_PROGRAM_TYPE).toString())));
              newCenterProgram.setResearchArea(newCenterArea);
              List<CenterLeader> programLeaders = centerProgram.getLeaders();
              centerProgramService.saveProgram(newCenterProgram);
              if (programLeaders != null) {
                for (CenterLeader programLeader : programLeaders) {
                  CenterLeader newProgramLeader = new CenterLeader();
                  newProgramLeader.setActive(true);
                  newProgramLeader.setActiveSince(new Date());
                  newProgramLeader.setCreatedBy(this.getCurrentUser());
                  newProgramLeader.setModificationJustification("");
                  newProgramLeader.setModifiedBy(this.getCurrentUser());
                  newProgramLeader.setResearchCenter(loggedCenter);
                  newProgramLeader.setResearchProgram(newCenterProgram);
                  newProgramLeader.setType(centerAllTypesService
                    .getCenterAllTypesById(CenterLeaderTypeEnum.RESEARCH_PROGRAM_LEADER_TYPE.getValue()));
                  newProgramLeader.setUser(programLeader.getUser());
                  centerLeaderService.saveResearchLeader(newProgramLeader);
                }
              }
            }
          }
        } else {
          // check center area changes
          boolean hasChanges = false;
          CenterArea centerAreaDB = centerAreaService.find(centerArea.getId());
          if (!centerArea.getAcronym().equals(centerAreaDB.getAcronym())) {
            centerAreaDB.setAcronym(centerArea.getAcronym());
            hasChanges = true;
          }
          if (!centerArea.getName().equals(centerAreaDB.getName())) {
            centerAreaDB.setName(centerArea.getName());
            hasChanges = true;
          }
          if (hasChanges) {
            centerAreaDB.setActive(true);
            centerAreaDB.setModifiedBy(this.getCurrentUser());
            centerAreaService.save(centerAreaDB);
          }

          // check if there are new area leaders
          List<CenterLeader> newAreaLeaders = centerArea.getLeaders();
          if (newAreaLeaders != null) {
            for (CenterLeader areaLeader : newAreaLeaders) {
              // check new area leader
              if (areaLeader.getId() == null || areaLeader.getId() == -1) {
                CenterLeader newareaLeader = new CenterLeader();
                newareaLeader.setActive(true);
                newareaLeader.setActiveSince(new Date());
                newareaLeader.setCreatedBy(this.getCurrentUser());
                newareaLeader.setModificationJustification("");
                newareaLeader.setModifiedBy(this.getCurrentUser());
                newareaLeader.setResearchArea(centerAreaDB);
                newareaLeader.setResearchCenter(loggedCenter);
                newareaLeader.setType(centerAllTypesService
                  .getCenterAllTypesById(CenterLeaderTypeEnum.RESEARCH_AREA_LEADER_TYPE.getValue()));
                newareaLeader.setUser(areaLeader.getUser());
                centerLeaderService.saveResearchLeader(newareaLeader);
              }
            }
          }

          // check new programs
          if (centerArea.getPrograms() != null && centerArea.getPrograms().size() > 0) {
            // save new programs
            for (CenterProgram centerProgram : centerArea.getPrograms()) {
              if (centerProgram.getId() == null || centerProgram.getId() == -1) {
                CenterProgram newCenterProgram = new CenterProgram();
                // Save center program
                newCenterProgram.setAcronym(centerProgram.getAcronym());
                newCenterProgram.setName(centerProgram.getName());
                newCenterProgram.setActive(true);
                newCenterProgram.setActiveSince(new Date());
                newCenterProgram.setCreatedBy(this.getCurrentUser());
                newCenterProgram.setModificationJustification("");
                newCenterProgram.setModifiedBy(this.getCurrentUser());
                newCenterProgram.setProgramType(centerAllTypesService.getCenterAllTypesById(
                  Long.parseLong(this.getSession().get(APConstants.CENTER_PROGRAM_TYPE).toString())));
                newCenterProgram.setResearchArea(centerArea);
                List<CenterLeader> programLeaders = centerProgram.getLeaders();
                centerProgramService.saveProgram(newCenterProgram);
                if (programLeaders != null) {
                  for (CenterLeader programLeader : programLeaders) {
                    CenterLeader newProgramLeader = new CenterLeader();
                    newProgramLeader.setActive(true);
                    newProgramLeader.setActiveSince(new Date());
                    newProgramLeader.setCreatedBy(this.getCurrentUser());
                    newProgramLeader.setModificationJustification("");
                    newProgramLeader.setModifiedBy(this.getCurrentUser());
                    newProgramLeader.setResearchCenter(loggedCenter);
                    newProgramLeader.setResearchProgram(newCenterProgram);
                    newProgramLeader.setType(centerAllTypesService
                      .getCenterAllTypesById(CenterLeaderTypeEnum.RESEARCH_PROGRAM_LEADER_TYPE.getValue()));
                    newProgramLeader.setUser(programLeader.getUser());
                    centerLeaderService.saveResearchLeader(newProgramLeader);
                  }
                }
              } else {
                // check programs changes
                CenterProgram centerProgramDB = centerProgramService.getProgramById(centerProgram.getId());
                boolean hasChangesProgram = false;
                if (!centerProgram.getAcronym().equals(centerProgramDB.getAcronym())) {
                  centerProgramDB.setAcronym(centerProgram.getAcronym());
                  hasChangesProgram = true;
                }
                if (!centerProgram.getName().equals(centerProgramDB.getName())) {
                  centerProgramDB.setName(centerProgram.getName());
                  hasChangesProgram = true;
                }
                if (hasChangesProgram) {
                  centerProgramDB.setActive(true);
                  centerProgramDB.setModifiedBy(this.getCurrentUser());
                  centerProgramService.saveProgram(centerProgramDB);
                }
                // check if there are new program leaders

                List<CenterLeader> newProgramLeaders = centerProgram.getLeaders();
                if (newProgramLeaders != null) {
                  for (CenterLeader programLeader : newProgramLeaders) {
                    // check new area leader
                    if (programLeader.getId() == null || programLeader.getId() == -1) {
                      CenterLeader newProgramLeader = new CenterLeader();
                      newProgramLeader.setActive(true);
                      newProgramLeader.setActiveSince(new Date());
                      newProgramLeader.setCreatedBy(this.getCurrentUser());
                      newProgramLeader.setModificationJustification("");
                      newProgramLeader.setModifiedBy(this.getCurrentUser());
                      newProgramLeader.setResearchArea(centerAreaDB);
                      newProgramLeader.setResearchCenter(loggedCenter);
                      newProgramLeader.setType(centerAllTypesService
                        .getCenterAllTypesById(CenterLeaderTypeEnum.RESEARCH_PROGRAM_LEADER_TYPE.getValue()));
                      newProgramLeader.setUser(programLeader.getUser());
                      centerLeaderService.saveResearchLeader(newProgramLeader);
                    }
                  }
                }
              }
            }
          }
        }
      }
    }

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
      if (centerAreas.isEmpty() || !centerAreas.contains(centerAreaDB)) {
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
    }
    // Check programs and leaders
    for (CenterArea centerArea : centerAreas) {
      if (centerArea.getId() != null && centerArea.getId() != -1) {
        CenterArea centerAreaDB = centerAreaService.find(centerArea.getId());
        // Delete area leaders
        List<CenterLeader> areaLeadersDB =
          centerAreaDB.getResearchLeaders().stream().filter(rl -> rl.isActive()).collect(Collectors.toList());
        for (CenterLeader areaLeaderDB : areaLeadersDB) {
          if (centerArea.getLeaders() == null || !centerArea.getLeaders().contains(areaLeaderDB)) {
            centerLeaderService.deleteResearchLeader(areaLeaderDB.getId());
          }
        }
        // Delete programs
        List<CenterProgram> centerProgramsDB =
          centerAreaDB.getResearchPrograms().stream().filter(rp -> rp.isActive()).collect(Collectors.toList());
        for (CenterProgram centerProgramDB : centerProgramsDB) {
          if (centerArea.getPrograms() == null || !centerArea.getPrograms().contains(centerProgramDB)) {
            List<CenterLeader> programLeadersDB =
              centerProgramDB.getResearchLeaders().stream().filter(rl -> rl.isActive()).collect(Collectors.toList());
            // delete program leaders
            for (CenterLeader centerLeaderDB : programLeadersDB) {
              centerLeaderService.deleteResearchLeader(centerLeaderDB.getId());
            }
            centerProgramService.deleteProgram(centerProgramDB.getId());
          }
        }
        // Delete program leaders
        List<CenterProgram> centerPrograms = centerArea.getPrograms();
        if (centerPrograms != null) {
          for (CenterProgram centerProgram : centerPrograms) {
            if (centerProgram.getId() != null && centerProgram.getId() != -1) {
              CenterProgram centerProgramDB = centerProgramService.getProgramById(centerProgram.getId());
              List<CenterLeader> programLeadersDB =
                centerProgramDB.getResearchLeaders().stream().filter(rl -> rl.isActive()).collect(Collectors.toList());
              for (CenterLeader programLeaderDB : programLeadersDB) {
                if (centerProgram.getLeaders() == null || !centerProgram.getLeaders().contains(programLeaderDB)) {
                  centerLeaderService.deleteResearchLeader(programLeaderDB.getId());
                }
              }
            }
          }
        }
      }
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
      this.checkChanges();


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
