/*
 * This file is part of Managing Agricultural Research for Learning&*Outcomes Platform(MARLO).
 ** MARLO is free software:you can redistribute it and/or modify
 ** it under the terms of the GNU General Public License as published by
 ** the Free Software Foundation,either version 3 of the License,or*at your option)any later version.
 ** MARLO is distributed in the hope that it will be useful,
 ** but WITHOUT ANY WARRANTY;without even the implied warranty of*MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.See
 * the
 ** GNU General Public License for more details.
 ** You should have received a copy of the GNU General Public License
 ** along with MARLO.If not,see<http:// www.gnu.org/licenses/>.
 *****************************************************************/

package org.cgiar.ccafs.marlo.action.crp.admin;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.data.manager.ShfrmPriorityActionManager;
import org.cgiar.ccafs.marlo.data.manager.ShfrmSubActionManager;
import org.cgiar.ccafs.marlo.data.model.ShfrmPriorityAction;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;


public class ShfrmManagementAction extends BaseAction {

  private static final long serialVersionUID = -793652591843623397L;
  private List<ShfrmPriorityAction> priorityActions;
  private final ShfrmPriorityActionManager shfrmPriorityActionManager;
  private final ShfrmSubActionManager shfrmSubActionManager;

  @Inject
  public ShfrmManagementAction(APConfig config, ShfrmPriorityActionManager shfrmPriorityActionManager,
    ShfrmSubActionManager shfrmSubActionManager) {
    super(config);
    this.shfrmPriorityActionManager = shfrmPriorityActionManager;
    this.shfrmSubActionManager = shfrmSubActionManager;
  }

  public List<ShfrmPriorityAction> getPriorityActions() {
    return priorityActions;
  }

  @Override
  public void prepare() throws Exception {
    priorityActions = new ArrayList<>();
    priorityActions = shfrmPriorityActionManager.findAll();

    if (this.isHttpPost()) {
    }
  }

  @Override
  public String save() {
    if (this.canAcessCrpAdmin()) {
      if (priorityActions != null && !priorityActions.isEmpty()) {

        for (ShfrmPriorityAction action : priorityActions) {
          ShfrmPriorityAction actionSave = new ShfrmPriorityAction();
          if (action.getId() != null) {
            actionSave = shfrmPriorityActionManager.getShfrmPriorityActionById(action.getId());
          }
          if (action.getName() != null) {
            actionSave.setName(action.getName());
          }
          if (action.getDescription() != null) {
            actionSave.setDescription(action.getDescription());
          }
          shfrmPriorityActionManager.saveShfrmPriorityAction(actionSave);
        }
      }

      if (this.getUrl() == null || this.getUrl().isEmpty()) {
        Collection<String> messages = this.getActionMessages();
        /*
         * if (!this.getInvalidFields().isEmpty()) {
         * this.setActionMessages(null);
         * // this.addActionMessage(Map.toString(this.getInvalidFields().toArray()));
         * List<String> keys = new ArrayList<String>(this.getInvalidFields().keySet());
         * for (String key : keys) {
         * this.addActionMessage(key + ": " + this.getInvalidFields().get(key));
         * }
         * } else {
         * this.addActionMessage("message:" + this.getText("saving.saved"));
         * }
         */
        this.addActionMessage("message:" + this.getText("saving.saved"));

        return SUCCESS;
      } else {
        this.addActionMessage("");
        this.setActionMessages(null);
        return REDIRECT;
      }

    } else {
      return NOT_AUTHORIZED;
    }
  }

  public void setPriorityActions(List<ShfrmPriorityAction> priorityActions) {
    this.priorityActions = priorityActions;
  }


  @Override
  public void validate() {
    if (save) {
      // validator.validate(this, feedbackFields);
    }
  }
}