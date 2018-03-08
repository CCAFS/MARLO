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

package org.cgiar.ccafs.marlo.action.center.json.impactpathway;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.ICenterOutcomeManager;
import org.cgiar.ccafs.marlo.data.model.CenterOutcome;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.dispatcher.Parameter;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class OutcomeInfoAction extends BaseAction {


  private static final long serialVersionUID = 6056401366531194841L;


  private ICenterOutcomeManager outcomeService;

  private long outcomeID;


  private Map<String, Object> outcomeInfo;


  @Inject
  public OutcomeInfoAction(APConfig config, ICenterOutcomeManager outcomeService) {
    super(config);
    this.outcomeService = outcomeService;
  }

  @Override
  public String execute() throws Exception {

    outcomeInfo = new HashMap<>();
    CenterOutcome outcome = outcomeService.getResearchOutcomeById(outcomeID);

    if (outcome != null) {
      outcomeInfo.put("id", outcome.getId());
      outcomeInfo.put("researchTopic", outcome.getResearchTopic().getResearchTopic());
    }

    return SUCCESS;
  }

  public Map<String, Object> getOutcomeInfo() {
    return outcomeInfo;
  }

  public Map<String, Object> getOutputInfo() {
    return outcomeInfo;
  }

  @Override
  public void prepare() throws Exception {
    Map<String, Parameter> parameters = this.getParameters();
    outcomeID = Long.parseLong(StringUtils.trim(parameters.get(APConstants.OUTCOME_ID).getMultipleValues()[0]));
  }

  public void setOutcomeInfo(Map<String, Object> outcomeInfo) {
    this.outcomeInfo = outcomeInfo;
  }


}
