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
import org.cgiar.ccafs.marlo.data.manager.ICenterDeliverableTypeManager;
import org.cgiar.ccafs.marlo.data.model.CenterDeliverableType;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.dispatcher.Parameter;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class DeliverableSubTypeAction extends BaseAction {


  private static final long serialVersionUID = -4481506940773334182L;

  private ICenterDeliverableTypeManager deliverableTypeService;

  private Long deliverableTypeId;

  private List<Map<String, Object>> deliverableSubTypes;

  @Inject
  public DeliverableSubTypeAction(APConfig config, ICenterDeliverableTypeManager deliverableTypeService) {
    super(config);
    this.deliverableTypeService = deliverableTypeService;
  }

  @Override
  public String execute() throws Exception {
    deliverableSubTypes = new ArrayList<>();
    Map<String, Object> deliverableSubType;

    CenterDeliverableType deliverableType = deliverableTypeService.getDeliverableTypeById(deliverableTypeId);

    if (deliverableType != null) {
      for (CenterDeliverableType type : deliverableTypeService.getSubDeliverableType(deliverableType.getId())) {
        if (type.getId() != 62) {
          deliverableSubType = new HashMap<String, Object>();
          deliverableSubType.put("id", type.getId());
          deliverableSubType.put("name", type.getName());
          deliverableSubTypes.add(deliverableSubType);
        }
      }
    }

    return SUCCESS;
  }

  public List<Map<String, Object>> getDeliverableSubTypes() {
    return deliverableSubTypes;
  }

  public Long getDeliverableTypeId() {
    return deliverableTypeId;
  }

  @Override
  public void prepare() throws Exception {
    // Map<String, Object> parameters = this.getParameters();
    Map<String, Parameter> parameters = this.getParameters();
    deliverableTypeId =
      // Long.parseLong(StringUtils.trim(((String[]) parameters.get(APConstants.DELIVERABLE_TYPE_ID))[0]));
      Long.parseLong(StringUtils.trim(parameters.get(APConstants.DELIVERABLE_TYPE_ID).getMultipleValues()[0]));
  }

  public void setDeliverableSubTypes(List<Map<String, Object>> deliverableSubTypes) {
    this.deliverableSubTypes = deliverableSubTypes;
  }

  public void setDeliverableTypeId(Long deliverableTypeId) {
    this.deliverableTypeId = deliverableTypeId;
  }

}
