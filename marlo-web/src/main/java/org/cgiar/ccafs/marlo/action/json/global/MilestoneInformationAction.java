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
import org.cgiar.ccafs.marlo.data.manager.CrpMilestoneManager;
import org.cgiar.ccafs.marlo.data.model.CrpMilestone;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.HashMap;
import java.util.Map;

import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.dispatcher.Parameter;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class MilestoneInformationAction extends BaseAction {

  private long milestoneID;
  private CrpMilestoneManager crpMilestoneManager;

  private Map<String, Object> crpMilestone;

  @Inject
  public MilestoneInformationAction(APConfig config, CrpMilestoneManager crpMilestoneManager) {
    super(config);
    this.crpMilestoneManager = crpMilestoneManager;
  }


  @Override
  public String execute() throws Exception {
    CrpMilestone crpMilestone = crpMilestoneManager.getCrpMilestoneById(milestoneID);
    this.crpMilestone = new HashMap<String, Object>();
    this.crpMilestone.put("title", crpMilestone.getTitle());
    this.crpMilestone.put("year", crpMilestone.getYear());
    this.crpMilestone.put("value", crpMilestone.getValue());

    if (crpMilestone.getSrfTargetUnit() == null) {
      this.crpMilestone.put("targetUnit", -1);
    } else {
      this.crpMilestone.put("targetUnit", crpMilestone.getSrfTargetUnit().getId());
    }

    if (crpMilestone.getSrfTargetUnit() == null) {
      this.crpMilestone.put("targetUnitName", "Not Applicable");
    } else {
      this.crpMilestone.put("targetUnitName", crpMilestone.getSrfTargetUnit().getName());
    }

    return SUCCESS;
  }

  public Map<String, Object> getCrpMilestone() {
    return crpMilestone;
  }

  @Override
  public void prepare() throws Exception {
    // Map<String, Object> parameters = this.getParameters();
    Map<String, Parameter> parameters = this.getParameters();
    // milestoneID = Long.parseLong(StringUtils.trim(((String[]) parameters.get(APConstants.MILESTONE_REQUEST_ID))[0]));
    milestoneID =
      Long.parseLong(StringUtils.trim(parameters.get(APConstants.MILESTONE_REQUEST_ID).getMultipleValues()[0]));

  }


  public void setCrpMilestone(Map<String, Object> crpMilestone) {
    this.crpMilestone = crpMilestone;
  }


}
