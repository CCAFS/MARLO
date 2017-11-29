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
import org.cgiar.ccafs.marlo.data.manager.DeliverableManager;
import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.data.model.ProjectStatusEnum;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Christian Garcia- CIAT/CCAFS
 */
public class DeliverableStatusAction extends BaseAction {


  /**
   * 
   */
  private static final long serialVersionUID = 8027160696406597679L;
  /**
   * 
   */


  private Long deliverableID;
  private DeliverableManager deliverableManager;


  private Map<String, String> status;


  private int year;

  @Inject
  public DeliverableStatusAction(APConfig config, DeliverableManager deliverableManager) {
    super(config);
    this.deliverableManager = deliverableManager;
  }

  @Override
  public String execute() throws Exception {

    status = new HashMap<>();
    Deliverable deliverable = deliverableManager.getDeliverableById(deliverableID.longValue());

    List<ProjectStatusEnum> list = Arrays.asList(ProjectStatusEnum.values());
    for (ProjectStatusEnum projectStatusEnum : list) {

      status.put(projectStatusEnum.getStatusId(), projectStatusEnum.getStatus());
    }
    if (this.isPlanningActive()) {

      if (deliverable.getDeliverableInfo(this.getActualPhase()).getStatus() != null
        && deliverable.getDeliverableInfo(this.getActualPhase()).getStatus().intValue() != Integer
          .parseInt(ProjectStatusEnum.Complete.getStatusId())) {
        status.remove(ProjectStatusEnum.Complete.getStatusId());
      }
      if (this.isDeliverableNew(deliverableID)) {
        status.remove(ProjectStatusEnum.Cancelled.getStatusId());
        status.remove(ProjectStatusEnum.Extended.getStatusId());

      } else {
        if (year < this.getActualPhase().getYear()) {

          status.remove(ProjectStatusEnum.Extended.getStatusId());
        }
      }
    }

    return SUCCESS;
  }

  public Map<String, String> getStatus() {
    return status;
  }

  @Override
  public void prepare() throws Exception {
    Map<String, Object> parameters = this.getParameters();
    deliverableID = Long.parseLong(StringUtils.trim(((String[]) parameters.get(APConstants.DELIVERABLE_ID))[0]));
    year = Integer.parseInt(StringUtils.trim(((String[]) parameters.get(APConstants.YEAR_REQUEST))[0]));
  }


  public void setStatus(Map<String, String> keyOutputs) {
    this.status = keyOutputs;
  }


}
