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
import org.cgiar.ccafs.marlo.data.manager.ShfrmSubActionManager;
import org.cgiar.ccafs.marlo.data.model.ShfrmSubAction;
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

public class SubActionsByPriorityAction extends BaseAction {

  private static final long serialVersionUID = 3674950424554096761L;
  private long priorityActionID;
  private ShfrmSubActionManager shfrmSubActionManager;
  private List<Map<String, Object>> subActions;
  private final Logger logger = LoggerFactory.getLogger(SubActionsByPriorityAction.class);


  @Inject
  public SubActionsByPriorityAction(APConfig config, ShfrmSubActionManager shfrmSubActionManager) {
    super(config);
    this.shfrmSubActionManager = shfrmSubActionManager;
  }

  @Override
  public String execute() throws Exception {
    subActions = new ArrayList<>();
    Map<String, Object> subAction;
    List<ShfrmSubAction> shfrmSubActions = new ArrayList<>();
    try {
      shfrmSubActions = shfrmSubActionManager.findAll().stream()
        .filter(s -> s != null && s.isActive() && s.getShfrmPriorityAction() != null
          && s.getShfrmPriorityAction().getId() != null && s.getShfrmPriorityAction().getId().equals(priorityActionID))
        .collect(Collectors.toList());
    } catch (Exception e) {
      logger.error("error getting sub actions " + e);
    }

    if (shfrmSubActions != null && !shfrmSubActions.isEmpty()) {
      for (ShfrmSubAction shfrmSubAction : shfrmSubActions) {
        subAction = new HashMap<String, Object>();
        subAction.put("id", shfrmSubAction.getId());
        subAction.put("composedName", shfrmSubAction.getComposedName());
        subAction.put("name", shfrmSubAction.getName());
        subAction.put("description", shfrmSubAction.getDescription());
        if (shfrmSubAction.getShfrmPriorityAction() != null
          && shfrmSubAction.getShfrmPriorityAction().getId() != null) {
          subAction.put("priorityActionId", shfrmSubAction.getShfrmPriorityAction().getId());
        }
        subActions.add(subAction);
      }
    }
    return SUCCESS;

  }

  public List<Map<String, Object>> getSubActions() {
    return subActions;
  }

  @Override
  public void prepare() throws Exception {
    // Map<String, Object> parameters = this.getParameters();
    // partnerID = Long.parseLong(StringUtils.trim(((String[]) parameters.get(APConstants.PARTNER_ID))[0]));

    Map<String, Parameter> parameters = this.getParameters();
    priorityActionID =
      Long.parseLong(StringUtils.trim(parameters.get(APConstants.PRIORITY_ACTION_ID).getMultipleValues()[0]));
  }

  public void setSubActions(List<Map<String, Object>> subActions) {
    this.subActions = subActions;
  }

}
