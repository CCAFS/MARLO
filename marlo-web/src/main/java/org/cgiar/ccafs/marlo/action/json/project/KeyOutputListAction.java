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
import org.cgiar.ccafs.marlo.data.manager.CrpClusterOfActivityManager;
import org.cgiar.ccafs.marlo.data.model.CrpClusterKeyOutput;
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

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class KeyOutputListAction extends BaseAction {


  private static final long serialVersionUID = 4663544283175165587L;


  private Long clusterOfActivityId;


  private CrpClusterOfActivityManager crpClusterOfActivityManager;


  private List<Map<String, Object>> keyOutputs;

  @Inject
  public KeyOutputListAction(APConfig config, CrpClusterOfActivityManager crpClusterOfActivityManager) {
    super(config);
    this.crpClusterOfActivityManager = crpClusterOfActivityManager;
  }

  @Override
  public String execute() throws Exception {

    keyOutputs = new ArrayList<>();
    Map<String, Object> keyOutput;

    CrpClusterOfActivity activity = crpClusterOfActivityManager.getCrpClusterOfActivityById(clusterOfActivityId);
    if (activity != null) {
      if (activity.getCrpClusterKeyOutputs() != null) {
        for (CrpClusterKeyOutput clusterKeyOutput : activity.getCrpClusterKeyOutputs().stream()
          .filter(ko -> ko.isActive()).collect(Collectors.toList())) {
          keyOutput = new HashMap<String, Object>();
          keyOutput.put("id", clusterKeyOutput.getId());
          keyOutput.put("description", clusterKeyOutput.getKeyOutput());
          keyOutputs.add(keyOutput);
        }
      }
    }

    return SUCCESS;
  }

  public List<Map<String, Object>> getKeyOutputs() {
    return keyOutputs;
  }

  @Override
  public void prepare() throws Exception {
    // Map<String, Object> parameters = this.getParameters();
    Map<String, Parameter> parameters = this.getParameters();

    clusterOfActivityId =
      // Long.parseLong(StringUtils.trim(((String[]) parameters.get(APConstants.CRP_CLUSTER_ACTIVITY_ID))[0]));
      Long.parseLong(StringUtils.trim(parameters.get(APConstants.CRP_CLUSTER_ACTIVITY_ID).getMultipleValues()[0]));
  }


  public void setKeyOutputs(List<Map<String, Object>> keyOutputs) {
    this.keyOutputs = keyOutputs;
  }


}
