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

package org.cgiar.ccafs.marlo.action.json.global;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.CrpProgramOutcomeManager;
import org.cgiar.ccafs.marlo.data.model.CrpMilestone;
import org.cgiar.ccafs.marlo.data.model.CrpProgramOutcome;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.dispatcher.Parameter;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class MilestonesbyYearAction extends BaseAction {

  /**
   * 
   */
  private static final long serialVersionUID = 8475012643100907284L;
  private long crpProgamID;
  private int year;
  private CrpProgramOutcomeManager crpProgramManager;

  private List<Map<String, Object>> crpMilestones;

  @Inject
  public MilestonesbyYearAction(APConfig config, CrpProgramOutcomeManager crpProgramManager) {
    super(config);
    this.crpProgramManager = crpProgramManager;
  }


  @Override
  public String execute() throws Exception {
    crpMilestones = new ArrayList<Map<String, Object>>();
    CrpProgramOutcome crpProgramOutcome = crpProgramManager.getCrpProgramOutcomeById(crpProgamID);
    List<CrpMilestone> milestones = crpProgramOutcome.getCrpMilestones().stream()
      .filter(c -> c.isActive() & c.getYear() >= year).collect(Collectors.toList());

    for (CrpMilestone crpMilestoneInfo : milestones) {
      Map<String, Object> crpMilestone = new HashMap<>();
      crpMilestone.put("id", crpMilestoneInfo.getId());
      crpMilestone.put("description", crpMilestoneInfo.getComposedName());
      crpMilestones.add(crpMilestone);
    }
    return SUCCESS;
  }

  public List<Map<String, Object>> getCrpMilestones() {
    return crpMilestones;
  }

  @Override
  public void prepare() throws Exception {
    // Map<String, Object> parameters = this.getParameters();
    // crpProgamID = Long.parseLong(StringUtils.trim(((String[]) parameters.get(APConstants.OUTCOME_REQUEST_ID))[0]));
    // year = Integer.parseInt(StringUtils.trim(((String[]) parameters.get(APConstants.YEAR_REQUEST))[0]));

    Map<String, Parameter> parameters = this.getParameters();
    crpProgamID =
      Long.parseLong(StringUtils.trim(parameters.get(APConstants.OUTCOME_REQUEST_ID).getMultipleValues()[0]));
    year = Integer.parseInt(StringUtils.trim(parameters.get(APConstants.YEAR_REQUEST).getMultipleValues()[0]));

  }


  public void setCrpMilestones(List<Map<String, Object>> crpMilestone) {
    this.crpMilestones = crpMilestone;
  }


}
