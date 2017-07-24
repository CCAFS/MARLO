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

package org.cgiar.ccafs.marlo.action.center.json.monitoring.project;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.ICenterOutputManager;
import org.cgiar.ccafs.marlo.data.model.CenterOutput;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.HashMap;
import java.util.Map;

import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class OutputInfoAction extends BaseAction {


  private static final long serialVersionUID = 6056401366531194841L;


  private ICenterOutputManager outputService;


  private long outputID;

  private Map<String, Object> outputInfo;

  @Inject
  public OutputInfoAction(APConfig config, ICenterOutputManager outputService) {
    super(config);
    this.outputService = outputService;
  }

  @Override
  public String execute() throws Exception {

    outputInfo = new HashMap<>();
    CenterOutput output = outputService.getResearchOutputById(outputID);

    if (output != null) {
      outputInfo.put("id", output.getId());
      outputInfo.put("outcomeName", output.getResearchOutcome().getDescription());
      outputInfo.put("topicName", output.getResearchOutcome().getResearchTopic().getResearchTopic());
    }

    return SUCCESS;
  }

  public Map<String, Object> getOutputInfo() {
    return outputInfo;
  }

  @Override
  public void prepare() throws Exception {
    Map<String, Object> parameters = this.getParameters();
    outputID = Long.parseLong(StringUtils.trim(((String[]) parameters.get(APConstants.OUTPUT_ID))[0]));
  }


  public void setOutputInfo(Map<String, Object> outputInfo) {
    this.outputInfo = outputInfo;
  }

}
