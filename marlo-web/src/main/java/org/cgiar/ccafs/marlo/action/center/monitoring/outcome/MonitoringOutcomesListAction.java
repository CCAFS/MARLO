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

package org.cgiar.ccafs.marlo.action.center.monitoring.outcome;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.CrpProgramManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.ICenterAreaManager;
import org.cgiar.ccafs.marlo.data.manager.ICenterOutcomeManager;
import org.cgiar.ccafs.marlo.data.manager.ICenterTopicManager;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.model.CenterArea;
import org.cgiar.ccafs.marlo.data.model.CenterLeader;
import org.cgiar.ccafs.marlo.data.model.CenterLeaderTypeEnum;
import org.cgiar.ccafs.marlo.data.model.CenterOutcome;
import org.cgiar.ccafs.marlo.data.model.CenterTopic;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class MonitoringOutcomesListAction extends BaseAction {


  private static final long serialVersionUID = 131978907868869233L;


  // GlobalUnit Manager
  private GlobalUnitManager centerService;

  private GlobalUnit loggedCenter;


  private List<CenterOutcome> outcomes;


  private CrpProgramManager programService;


  private List<CenterArea> researchAreas;

  private ICenterAreaManager researchAreaService;


  private List<CrpProgram> researchPrograms;
  private List<CenterTopic> researchTopics;
  private ICenterTopicManager researchTopicService;
  private CrpProgram selectedProgram;
  private CenterArea selectedResearchArea;
  private CenterTopic selectedResearchTopic;
  private UserManager userService;
  private long topicID;
  private long crpProgramID;
  private long outcomeID;
  private long areaID;

  @Inject
  public MonitoringOutcomesListAction(APConfig config, GlobalUnitManager centerService,
    CrpProgramManager programService, ICenterAreaManager researchAreaService, UserManager userService,
    ICenterTopicManager researchTopicService, ICenterOutcomeManager outcomeService) {
    super(config);
    this.centerService = centerService;
    this.programService = programService;
    this.researchAreaService = researchAreaService;
    this.userService = userService;
    this.researchTopicService = researchTopicService;
  }

  public List<CenterOutcome> allProgramOutcomes() {

    List<CenterOutcome> ouList = new ArrayList<>();

    List<CenterTopic> researchTopics = new ArrayList<>(
      selectedProgram.getResearchTopics().stream().filter(rt -> rt.isActive()).collect(Collectors.toList()));

    for (CenterTopic researchTopic : researchTopics) {
      List<CenterOutcome> researchOutcomes = new ArrayList<>(
        researchTopic.getResearchOutcomes().stream().filter(ro -> ro.isActive()).collect(Collectors.toList()));

      for (CenterOutcome researchOutcome : researchOutcomes) {
        ouList.add(researchOutcome);
      }
    }

    return ouList;

  }

  public long getAreaID() {
    return areaID;
  }

  public long getCrpProgramID() {
    return crpProgramID;
  }

  public long getOutcomeID() {
    return outcomeID;
  }


  public List<CenterOutcome> getOutcomes() {
    return outcomes;
  }

  public CrpProgramManager getProgramService() {
    return programService;
  }


  public List<CenterArea> getResearchAreas() {
    return researchAreas;
  }


  public List<CenterTopic> getResearchTopics() {
    return researchTopics;
  }


  public CrpProgram getSelectedProgram() {
    return selectedProgram;
  }

  public CenterArea getSelectedResearchArea() {
    return selectedResearchArea;
  }

  public CenterTopic getSelectedResearchTopic() {
    return selectedResearchTopic;
  }

  public long getTopicID() {
    return topicID;
  }

  @Override
  public void prepare() throws Exception {
    areaID = -1;
    crpProgramID = -1;
    topicID = -1;

    loggedCenter = (GlobalUnit) this.getSession().get(APConstants.SESSION_CRP);
    loggedCenter = centerService.getGlobalUnitById(loggedCenter.getId());

    researchAreas =
      new ArrayList<>(loggedCenter.getCenterAreas().stream().filter(ra -> ra.isActive()).collect(Collectors.toList()));

    Collections.sort(researchAreas, (ra1, ra2) -> ra1.getId().compareTo(ra2.getId()));

    if (researchAreas != null) {

      try {
        areaID = Long.parseLong(StringUtils.trim(this.getRequest().getParameter(APConstants.CENTER_AREA_ID)));
      } catch (Exception e) {
        try {
          crpProgramID = Long.parseLong(StringUtils.trim(this.getRequest().getParameter(APConstants.CRP_PROGRAM_ID)));
        } catch (Exception ex) {
          User user = userService.getUser(this.getCurrentUser().getId());

          // Check if the User is an Area Leader
          List<CenterLeader> userAreaLeads =
            new ArrayList<>(user.getResearchLeaders().stream()
              .filter(rl -> rl.isActive()
                && rl.getType().getId() == CenterLeaderTypeEnum.RESEARCH_AREA_LEADER_TYPE.getValue())
              .collect(Collectors.toList()));
          if (!userAreaLeads.isEmpty()) {
            areaID = userAreaLeads.get(0).getResearchArea().getId();
          } else {
            // Check if the User is a Program Leader
            List<CenterLeader> userProgramLeads = new ArrayList<>(user.getResearchLeaders().stream()
              .filter(rl -> rl.isActive()
                && rl.getType().getId() == CenterLeaderTypeEnum.RESEARCH_PROGRAM_LEADER_TYPE.getValue())
              .collect(Collectors.toList()));
            if (!userProgramLeads.isEmpty()) {
              crpProgramID = userProgramLeads.get(0).getResearchProgram().getId();
            } else {
              // Check if the User is a Scientist Leader
              List<CenterLeader> userScientistLeader = new ArrayList<>(user.getResearchLeaders().stream()
                .filter(rl -> rl.isActive()
                  && rl.getType().getId() == CenterLeaderTypeEnum.PROGRAM_SCIENTIST_LEADER_TYPE.getValue())
                .collect(Collectors.toList()));
              if (!userScientistLeader.isEmpty()) {
                crpProgramID = userScientistLeader.get(0).getResearchProgram().getId();
              } else {
                List<CrpProgram> rps = researchAreas.get(0).getResearchPrograms().stream().filter(r -> r.isActive())
                  .collect(Collectors.toList());
                Collections.sort(rps, (rp1, rp2) -> rp1.getId().compareTo(rp2.getId()));
                CrpProgram rp = rps.get(0);
                crpProgramID = rp.getId();
                areaID = rp.getResearchArea().getId();
              }
            }
          }
        }
      }

      if (areaID != -1 && crpProgramID == -1) {
        selectedResearchArea = researchAreaService.find(areaID);
        researchPrograms = new ArrayList<>(
          selectedResearchArea.getResearchPrograms().stream().filter(rp -> rp.isActive()).collect(Collectors.toList()));
        Collections.sort(researchPrograms, (rp1, rp2) -> rp1.getId().compareTo(rp2.getId()));
        if (researchPrograms != null) {
          try {
            crpProgramID = Long.parseLong(StringUtils.trim(this.getRequest().getParameter(APConstants.CRP_PROGRAM_ID)));
          } catch (Exception e) {
            User user = userService.getUser(this.getCurrentUser().getId());

            List<CenterLeader> userLeads = new ArrayList<>(user.getResearchLeaders().stream()
              .filter(rl -> rl.isActive()
                && rl.getType().getId() == CenterLeaderTypeEnum.RESEARCH_PROGRAM_LEADER_TYPE.getValue())
              .collect(Collectors.toList()));

            if (!userLeads.isEmpty()) {
              crpProgramID = userLeads.get(0).getResearchProgram().getId();
            } else {
              if (!researchPrograms.isEmpty()) {
                crpProgramID = researchPrograms.get(0).getId();
              }
            }
          }
        }

        if (crpProgramID != -1) {
          selectedProgram = programService.getCrpProgramById(crpProgramID);
        }
      } else {
        if (crpProgramID != -1) {
          selectedProgram = programService.getCrpProgramById(crpProgramID);
          areaID = selectedProgram.getResearchArea().getId();
          selectedResearchArea = researchAreaService.find(areaID);
        }
      }

      if (selectedProgram.getResearchTopics() != null) {
        researchTopics = new ArrayList<>(selectedProgram.getResearchTopics().stream()
          .filter(rt -> rt.isActive() && rt.getResearchTopic().trim().length() > 0).collect(Collectors.toList()));
        try {
          topicID = Long.parseLong(StringUtils.trim(this.getRequest().getParameter(APConstants.RESEARCH_TOPIC_ID)));
          if (topicID == -1) {
            outcomes = this.allProgramOutcomes();
          } else {
            selectedResearchTopic = researchTopicService.getResearchTopicById(topicID);
          }
        } catch (Exception e) {
          outcomes = this.allProgramOutcomes();
        }
        if (selectedResearchTopic != null) {
          if (selectedResearchTopic.getResearchOutcomes() != null) {
            outcomes = selectedResearchTopic.getResearchOutcomes().stream().filter(ro -> ro.isActive())
              .collect(Collectors.toList());
          }
        }

      }

    }
  }

  public void setAreaID(long areaID) {
    this.areaID = areaID;
  }

  public void setCrpProgramID(long crpProgramID) {
    this.crpProgramID = crpProgramID;
  }


  public void setOutcomeID(long outcomeID) {
    this.outcomeID = outcomeID;
  }


  public void setOutcomes(List<CenterOutcome> outcomes) {
    this.outcomes = outcomes;
  }

  public void setProgramService(CrpProgramManager programService) {
    this.programService = programService;
  }

  public void setResearchAreas(List<CenterArea> researchAreas) {
    this.researchAreas = researchAreas;
  }

  public void setResearchTopics(List<CenterTopic> researchTopics) {
    this.researchTopics = researchTopics;
  }


  public void setSelectedProgram(CrpProgram selectedProgram) {
    this.selectedProgram = selectedProgram;
  }


  public void setSelectedResearchArea(CenterArea selectedResearchArea) {
    this.selectedResearchArea = selectedResearchArea;
  }

  public void setSelectedResearchTopic(CenterTopic selectedResearchTopic) {
    this.selectedResearchTopic = selectedResearchTopic;
  }

  public void setTopicID(long topicID) {
    this.topicID = topicID;
  }

}
