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


package org.cgiar.ccafs.marlo.action.json.impactpathway;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.CrpClusterOfActivityManager;
import org.cgiar.ccafs.marlo.data.model.CrpClusterActivityLeader;
import org.cgiar.ccafs.marlo.data.model.CrpClusterOfActivity;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.dispatcher.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Christian Garcia - CIAT/CCAFS
 */
public class CrpClusterLeadersListAction extends BaseAction {


  /**
   * 
   */
  private static final long serialVersionUID = -7826297593244502793L;

  private static final Logger LOG = LoggerFactory.getLogger(CrpClusterLeadersListAction.class);

  private CrpClusterOfActivityManager crpClusterOfActivityManager;
  private List<Map<String, Object>> leaders;
  private String clusterId;

  @Inject
  public CrpClusterLeadersListAction(APConfig config, CrpClusterOfActivityManager crpClusterOfActivityManager) {
    super(config);
    this.crpClusterOfActivityManager = crpClusterOfActivityManager;

  }

  @Override
  public String execute() throws Exception {
    long clusterId = 0;
    try {
      clusterId = Long.parseLong(this.clusterId);
    } catch (NumberFormatException e) {
      LOG.error("There was an exception trying to parse the parent id = {} ", clusterId);
    }

    CrpClusterOfActivity activity = crpClusterOfActivityManager.getCrpClusterOfActivityById(clusterId);
    List<CrpClusterActivityLeader> crpClusterActivityLeaders =
      activity.getCrpClusterActivityLeaders().stream().filter(c1 -> c1.isActive()).collect(Collectors.toList());

    leaders = new ArrayList<Map<String, Object>>();
    for (CrpClusterActivityLeader crpClusterActivityLeader : crpClusterActivityLeaders) {
      Map<String, Object> mapSubIdo = new HashMap<String, Object>();
      mapSubIdo.put("id", crpClusterActivityLeader.getId());

      mapSubIdo.put("description", crpClusterActivityLeader.getUser().getComposedName());


      leaders.add(mapSubIdo);
    }
    return SUCCESS;
  }


  public List<Map<String, Object>> getLeaders() {
    return leaders;
  }


  @Override
  public void prepare() throws Exception {
    // Map<String, Object> parameters = this.getParameters();
    Map<String, Parameter> parameters = this.getParameters();

    // Verify if there is a programID parameter
    if (!parameters.get(APConstants.CRP_CLUSTER_ACTIVITY_ID).isDefined()) {
      clusterId = "";
      return;
    }


    // If there is a parameter take its values
    // clusterId = StringUtils.trim(((String[]) parameters.get(APConstants.CRP_CLUSTER_ACTIVITY_ID))[0]);
    clusterId = StringUtils.trim(parameters.get(APConstants.CRP_CLUSTER_ACTIVITY_ID).getMultipleValues()[0]);
  }

  public void setLeaders(List<Map<String, Object>> subIdos) {
    this.leaders = subIdos;
  }
}
