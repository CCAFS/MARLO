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
import org.cgiar.ccafs.marlo.data.model.CrpClusterOfActivity;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.dispatcher.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClusterByFPsAction extends BaseAction {

  private static final long serialVersionUID = -690592438898925949L;

  private final Logger logger = LoggerFactory.getLogger(ClusterByFPsAction.class);

  private List<Map<String, Object>> clusters;

  private String flagshipID;
  private CrpProgramManager crpManager;

  @Inject
  public ClusterByFPsAction(APConfig config, CrpProgramManager crpManager) {
    super(config);
    this.crpManager = crpManager;
  }


  @Override
  public String execute() throws Exception {
    clusters = new ArrayList<Map<String, Object>>();
    Map<String, Object> flagShip;

    String flagships[] = flagshipID.split(",");

    for (String string : flagships) {

      CrpProgram crpProgram = crpManager.getCrpProgramById(Long.parseLong(string));
      List<CrpClusterOfActivity> crpPrograms =
        crpProgram.getCrpClusterOfActivities().stream().filter(c -> c.isActive()).collect(Collectors.toList());;
      for (CrpClusterOfActivity ccActivity : crpPrograms) {
        try {
          flagShip = new HashMap<String, Object>();
          flagShip.put("id", ccActivity.getId());
          flagShip.put("description", ccActivity.getComposedName());

          this.clusters.add(flagShip);
        } catch (Exception e) {
          logger.error("unable to add flagship to clusters list", e);
          /**
           * Original code swallows the exception and didn't even log it. Now we at least log it,
           * but we need to revisit to see if we should continue processing or re-throw the exception.
           */
        }
      }

    }
    return SUCCESS;

  }


  public List<Map<String, Object>> getClusters() {
    return clusters;
  }

  @Override
  public void prepare() throws Exception {
    // Map<String, Object> parameters = this.getParameters();
    // flagshipID = (StringUtils.trim(((String[]) parameters.get(APConstants.FLAGSHIP_ID))[0]));

    Map<String, Parameter> parameters = this.getParameters();
    flagshipID = (StringUtils.trim(parameters.get(APConstants.FLAGSHIP_ID).getMultipleValues()[0]));
  }


  public void setClusters(List<Map<String, Object>> flagships) {
    this.clusters = flagships;
  }


}
