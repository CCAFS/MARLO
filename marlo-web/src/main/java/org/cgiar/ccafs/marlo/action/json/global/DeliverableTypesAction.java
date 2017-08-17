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
import org.cgiar.ccafs.marlo.data.manager.DeliverableTypeManager;
import org.cgiar.ccafs.marlo.data.model.DeliverableType;
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
 * @author Christian Garcia- CIAT/CCAFS
 */
public class DeliverableTypesAction extends BaseAction {


  /**
   * 
   */
  private static final long serialVersionUID = 2270859686229584756L;


  private Long deliverableTypeID;


  private DeliverableTypeManager deliverableTypeManager;


  private List<Map<String, Object>> subTypes;

  @Inject
  public DeliverableTypesAction(APConfig config, DeliverableTypeManager deliverableTypeManager) {
    super(config);
    this.deliverableTypeManager = deliverableTypeManager;
  }

  @Override
  public String execute() throws Exception {

    subTypes = new ArrayList<>();
    Map<String, Object> keyOutput;

    DeliverableType deliverableType = deliverableTypeManager.getDeliverableTypeById(deliverableTypeID.longValue());
    if (deliverableType != null) {
      if (deliverableType.getDeliverableTypes() != null) {
        for (DeliverableType deliverableSubType : deliverableType.getDeliverableTypes().stream()
          .collect(Collectors.toList())) {
          keyOutput = new HashMap<String, Object>();
          keyOutput.put("id", deliverableSubType.getId());
          keyOutput.put("description", deliverableSubType.getName());
          subTypes.add(keyOutput);
        }
      }
    }

    return SUCCESS;
  }

  public List<Map<String, Object>> getSubTypes() {
    return subTypes;
  }

  @Override
  public void prepare() throws Exception {
    // Map<String, Object> parameters = this.getParameters();
    Map<String, Parameter> parameters = this.getParameters();
    deliverableTypeID =
      // Long.parseLong(StringUtils.trim(((String[]) parameters.get(APConstants.DELIVERABLE_TYPE_ID))[0]));
      Long.parseLong(StringUtils.trim(parameters.get(APConstants.DELIVERABLE_TYPE_ID).getMultipleValues()[0]));
  }


  public void setSubTypes(List<Map<String, Object>> keyOutputs) {
    this.subTypes = keyOutputs;
  }


}
