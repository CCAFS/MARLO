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
import org.cgiar.ccafs.marlo.data.manager.CrpProgramManager;
import org.cgiar.ccafs.marlo.data.model.CrpClusterOfActivity;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;

/**
 * @author Christian Garcia - CIAT/CCAFS
 */
public class ClusetrActivitiesListAction extends BaseAction {


  private static final long serialVersionUID = 4663544283175165587L;


  private String flagshipsId;


  private CrpProgramManager crpProgramManager;


  private List<Map<String, Object>> clusterOfActivities;

  @Inject
  public ClusetrActivitiesListAction(APConfig config, CrpProgramManager crpProgramManager) {
    super(config);
    this.crpProgramManager = crpProgramManager;
  }

  @Override
  public String execute() throws Exception {

    clusterOfActivities = new ArrayList<>();
    String flagshipIds[] = flagshipsId.split(",");
    for (int i = 0; i < flagshipIds.length; i++) {

      CrpProgram crpProgram = crpProgramManager.getCrpProgramById(Long.parseLong(flagshipIds[i]));
      if (crpProgram != null) {

        for (CrpClusterOfActivity clusterOfActivity : crpProgram.getCrpClusterOfActivities().stream()
          .filter(c -> c.isActive()).collect(Collectors.toList())) {
          Map<String, Object> clusterOfActivityMap = new HashMap<String, Object>();
          clusterOfActivityMap.put("id", clusterOfActivity.getId());
          clusterOfActivityMap.put("description", clusterOfActivity.getComposedName());
          clusterOfActivities.add(clusterOfActivityMap);

        }

      }
    }


    return SUCCESS;

  }


  public List<Map<String, Object>> getClusterOfActivities() {
    return clusterOfActivities;
  }


  public String getFlagshipsId() {
    return flagshipsId;
  }


  @Override
  public void prepare() throws Exception {

  }


  public void setClusterOfActivities(List<Map<String, Object>> clusterOfActivities) {
    this.clusterOfActivities = clusterOfActivities;
  }

  public void setFlagshipsId(String flagshipsId) {
    this.flagshipsId = flagshipsId;
  }


}
