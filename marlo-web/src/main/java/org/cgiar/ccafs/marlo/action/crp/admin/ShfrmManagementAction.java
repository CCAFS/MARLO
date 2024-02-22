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
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.jfree.util.Log;
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
      if (priorityActions != null) {
        priorityActions.clear();;
      }
    }
  }

  @Override
  public String save() {
    if (this.canAcessCrpAdmin()) {
      List<ShfrmSubAction> subActionstoDelete = new ArrayList<>();
      List<ShfrmPriorityAction> priorityActionsDB = null;
      priorityActionsDB = shfrmPriorityActionManager.findAll();


      try {
        List<ShfrmSubAction> subActionsDB = null;
        subActionsDB = shfrmSubActionManager.findAll();
        if (this.priorityActions != null && !this.priorityActions.isEmpty()) {
          for (ShfrmPriorityAction action : this.priorityActions) {

            // Remove Sub actions
            if (action.getShfrmSubActions() != null && !action.getShfrmSubActions().isEmpty()) {
              // Obtener los IDs de las subacciones en action.getShfrmSubActions()
              Set<Long> subActionIdsInFrontend = action.getShfrmSubActions().stream().filter(Objects::nonNull)
                .map(ShfrmSubAction::getId).collect(Collectors.toSet());

              // Filter subActionsDB to retain only the subactions present in action.getShfrmSubActions()
              subActionsDB = subActionsDB.stream()
                .filter(subActionDB -> subActionDB.getShfrmPriorityAction() != null
                  && subActionDB.getShfrmPriorityAction().getId() != null
                  && subActionDB.getShfrmPriorityAction().getId().equals(action.getId())
                  && !subActionIdsInFrontend.contains(subActionDB.getId()))
                .collect(Collectors.toList());

              // Delete subactions from subActionsDB that are not in action.getShfrmSubActions()
              subActionsDB.forEach(subActionDB -> {
                shfrmSubActionManager.deleteShfrmSubAction(subActionDB.getId());
                action.getShfrmSubActions().removeIf(sa -> sa.getId().equals(subActionDB.getId()));
                subActionstoDelete.add(subActionDB);
              });

            } else {
              subActionsDB.forEach(subActionDB -> {
                shfrmSubActionManager.deleteShfrmSubAction(subActionDB.getId());
                subActionstoDelete.add(subActionDB);
              });
            }

          }
        }

      } catch (Exception e) {
        logger.info(e + " error deleting sub action");
      }


      if (this.priorityActions != null && !this.priorityActions.isEmpty()) {

        if (priorityActionsDB != null) {
          try {
            List<ShfrmSubAction> subActions = new ArrayList<>();

            for (ShfrmPriorityAction actionDB : priorityActionsDB) {
              if (this.priorityActions != null && !this.priorityActions.isEmpty()) {
                Set<Long> actionIdsInFrontend = this.priorityActions.stream().filter(Objects::nonNull)
                  .map(ShfrmPriorityAction::getId).collect(Collectors.toSet());
                if (!actionIdsInFrontend.contains(actionDB.getId())) {

                  // Validate previous sub actions in DB for each priority actions

                  try {
                    subActions = shfrmSubActionManager.findAll().stream()
                      .filter(subActionDB -> subActionDB.getShfrmPriorityAction() != null
                        && subActionDB.getShfrmPriorityAction().getId() != null
                        && subActionDB.getShfrmPriorityAction().getId().equals(actionDB.getId()))
                      .collect(Collectors.toList());;


                    if (subActions != null && !subActions.isEmpty()) {

                      // delete sub actions
                      for (ShfrmSubAction subActionDelete : subActions) {
                        if (subActionDelete != null && subActionDelete.getId() != null) {
                          shfrmSubActionManager.deleteShfrmSubAction(subActionDelete.getId());
                        }
                      }
                    }
                  } catch (Exception e) {
                    logger.error("error deleting sub actions", e);
                  }
                  shfrmPriorityActionManager.deleteShfrmPriorityAction(actionDB.getId());


                }
              }
            }
          } catch (Exception e) {
            logger.info(e + " error deleting actions");
          }
        }

        for (ShfrmPriorityAction action : this.priorityActions) {
          ShfrmPriorityAction actionSave = new ShfrmPriorityAction();
          if (action.getId() != null) {
            actionSave = shfrmPriorityActionManager.getShfrmPriorityActionById(action.getId());
          }

          if (subActionstoDelete != null && !subActionstoDelete.isEmpty()) {

            for (ShfrmSubAction subActiontoDelete : subActionstoDelete) {
              if (actionSave.getShfrmSubActions().contains(subActiontoDelete)) {
                actionSave.getShfrmSubActions().remove(subActiontoDelete);
              }
            }
          }

          if (action.getName() != null) {
            actionSave.setName(action.getName());
          }
          if (action.getDescription() != null) {
            actionSave.setDescription(action.getDescription());
          }
          List<ShfrmSubAction> subActionsTemp = new ArrayList<>();
          if (action.getShfrmSubActions() != null && !action.getShfrmSubActions().isEmpty()) {
            subActionsTemp = action.getShfrmSubActions();
          }

          action = shfrmPriorityActionManager.saveShfrmPriorityAction(actionSave);
          action.setShfrmSubActions(subActionsTemp);

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
              if (action.getId() != null) {
                subActionSave.setShfrmPriorityAction(action);
              }
              try {
                shfrmSubActionManager.saveShfrmSubAction(subActionSave);
              } catch (Exception e) {
                logger.info(e + " error saving sub action");
              }
            }
          }
        }
      } else {
        // Delete all priority actions DB
        try {
          List<ShfrmSubAction> subActions = new ArrayList<>();

          if (priorityActionsDB != null && !priorityActionsDB.isEmpty()) {
            for (ShfrmPriorityAction priorityActionDB : priorityActionsDB) {

              subActions = shfrmSubActionManager.findAll().stream()
                .filter(subActionDB -> subActionDB.getShfrmPriorityAction() != null
                  && subActionDB.getShfrmPriorityAction().getId() != null
                  && subActionDB.getShfrmPriorityAction().getId().equals(priorityActionDB.getId()))
                .collect(Collectors.toList());;


              if (subActions != null && !subActions.isEmpty()) {

                // delete sub actions
                for (ShfrmSubAction subActionDelete : subActions) {
                  if (subActionDelete != null && subActionDelete.getId() != null) {
                    shfrmSubActionManager.deleteShfrmSubAction(subActionDelete.getId());
                  }
                }
              }

              shfrmPriorityActionManager.deleteShfrmPriorityAction(priorityActionDB.getId());
            }
          }
        } catch (Exception e) {
          logger.error("error deleting priority actions: " + e);
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
    try {
      if (save) {
      }
    } catch (Exception e) {
      Log.error("validating error " + e);
    }
  }
}