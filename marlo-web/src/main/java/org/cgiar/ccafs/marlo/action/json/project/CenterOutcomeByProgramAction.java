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

package org.cgiar.ccafs.marlo.action.json.project;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.CrpProgramManager;
import org.cgiar.ccafs.marlo.data.model.CenterOutcome;
import org.cgiar.ccafs.marlo.data.model.CenterTopic;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.dispatcher.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class CenterOutcomeByProgramAction extends BaseAction {


  private static final long serialVersionUID = 3406776116755988206L;


  private final Logger logger = LoggerFactory.getLogger(CenterOutcomeByProgramAction.class);

  private List<Map<String, Object>> outcomes;
  private String programID;

  private CrpProgramManager crpManager;

  public CenterOutcomeByProgramAction(APConfig config, CrpProgramManager crpManager) {
    super(config);
    this.crpManager = crpManager;
  }

  @Override
  public String execute() throws Exception {
    outcomes = new ArrayList<Map<String, Object>>();
    Map<String, Object> flagShip;

    String flagships[] = programID.split(",");

    for (String string : flagships) {

      CrpProgram crpProgram = crpManager.getCrpProgramById(Long.parseLong(string));

      List<CenterTopic> centerTopics = new ArrayList<>(
        crpProgram.getResearchTopics().stream().filter(rt -> rt.isActive()).collect(Collectors.toList()));

      for (CenterTopic centerTopic : centerTopics) {
        List<CenterOutcome> centerOutcomes = new ArrayList<>(
          centerTopic.getResearchOutcomes().stream().filter(ro -> ro.isActive()).collect(Collectors.toList()));

        for (CenterOutcome centerOutcome : centerOutcomes) {
          try {
            flagShip = new HashMap<String, Object>();
            flagShip.put("id", centerOutcome.getId());
            flagShip.put("description", centerOutcome.getListComposedName());
            this.outcomes.add(flagShip);
          } catch (Exception e) {
            logger.error("unable to add the item to outcome list", e);
          }
        }
      }
    }
    return SUCCESS;

  }

  public List<Map<String, Object>> getOutcomes() {
    return outcomes;
  }

  public String getProgramID() {
    return programID;
  }


  @Override
  public void prepare() throws Exception {
    Map<String, Parameter> parameters = this.getParameters();
    programID = (StringUtils.trim(parameters.get(APConstants.CENTER_PROGRAM_ID).getMultipleValues()[0]));
  }


  public void setOutcomes(List<Map<String, Object>> outcomes) {
    this.outcomes = outcomes;
  }

  public void setProgramID(String programID) {
    this.programID = programID;
  }

}
