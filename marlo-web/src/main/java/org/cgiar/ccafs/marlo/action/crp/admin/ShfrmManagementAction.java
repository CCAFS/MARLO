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
import org.cgiar.ccafs.marlo.data.model.ShfrmSubAction;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ShfrmManagementAction extends BaseAction {

  private static Logger logger = LoggerFactory.getLogger(ShfrmManagementAction.class);
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

  /**
   * Fill Priority actions with sub actions list
   */
  public void fillSubActions() {
    if (priorityActions != null && !priorityActions.isEmpty()) {
      try {
        List<ShfrmSubAction> subActions = shfrmSubActionManager.findAll();

        if (subActions != null && !subActions.isEmpty()) {
          priorityActions.forEach(action -> {
            List<ShfrmSubAction> subActionsAdd = subActions.stream()
              .filter(subaction -> action != null && action.getId() != null && subaction != null
                && subaction.getShfrmPriorityAction() != null && subaction.getShfrmPriorityAction().getId() != null
                && subaction.getShfrmPriorityAction().getId().equals(action.getId()))
              .collect(Collectors.toList());

            if (!subActionsAdd.isEmpty()) {
              action.setShfrmSubActions(subActionsAdd);
            }
          });
        }
      } catch (Exception e) {
        logger.info(e + "no sub actions added yet");
      }
    }
  }

  public List<ShfrmPriorityAction> getPriorityActions() {
    return priorityActions;
  }

  @Override
  public void prepare() throws Exception {
    priorityActions = new ArrayList<>();
    priorityActions = shfrmPriorityActionManager.findAll();
    this.fillSubActions();
    if (this.isHttpPost()) {
    }
  }

  @Override
  public String save() {
    if (this.canAcessCrpAdmin()) {
      if (this.priorityActions != null && !this.priorityActions.isEmpty()) {

        List<ShfrmPriorityAction> priorityActionsDB = null;
        List<ShfrmSubAction> subActionsDB = null;
        try {
          priorityActionsDB = shfrmPriorityActionManager.findAll();
          subActionsDB = shfrmSubActionManager.findAll();

          if (priorityActions != null && !priorityActions.isEmpty()) {
            for (ShfrmPriorityAction priorityAction : priorityActions) {
              if (!priorityActionsDB.contains(priorityAction)) {
                shfrmPriorityActionManager.deleteShfrmPriorityAction(priorityAction.getId());
              }
            }
          }

        } catch (Exception e) {
          logger.info(e + "no sub actions added yet");
        }


        for (ShfrmPriorityAction action : this.priorityActions) {
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


          // Save sub-actions
          if (action.getShfrmSubActions() != null && !action.getShfrmSubActions().isEmpty()) {
            for (ShfrmSubAction subAction : action.getShfrmSubActions()) {
              ShfrmSubAction subActionSave = new ShfrmSubAction();

              if (subAction.getId() != null) {
                subActionSave.setId(subAction.getId());
              }
              if (subAction.getName() != null) {
                subActionSave.setName(subAction.getName());
              }
              if (subAction.getDescription() != null) {
                subActionSave.setDescription(subAction.getDescription());
              }
              subActionSave.setShfrmPriorityAction(action);
              try {
                shfrmSubActionManager.saveShfrmSubAction(subActionSave);
              } catch (Exception e) {
                logger.info(e + " error saving sub action");
              }

              try {
                if (subAction != null && subAction.getId() != null && subActionsDB != null && !subActionsDB.isEmpty()
                  && !subActionsDB.contains(subAction)) {
                  shfrmSubActionManager.deleteShfrmSubAction(subAction.getId());
                }
              } catch (Exception e) {
                logger.info(e + " error deleting sub action");
              }
            }
          }
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