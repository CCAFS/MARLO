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
import org.cgiar.ccafs.marlo.data.manager.ClusterTypeManager;
import org.cgiar.ccafs.marlo.data.manager.FeedbackPermissionManager;
import org.cgiar.ccafs.marlo.data.manager.FeedbackRolesPermissionManager;
import org.cgiar.ccafs.marlo.data.manager.RoleManager;
import org.cgiar.ccafs.marlo.data.model.ClusterType;
import org.cgiar.ccafs.marlo.data.model.FeedbackPermission;
import org.cgiar.ccafs.marlo.data.model.FeedbackRolesPermission;
import org.cgiar.ccafs.marlo.data.model.Role;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class FeedbackRolesPermissionsManagementAction extends BaseAction {

  private static final long serialVersionUID = -793652591843623397L;
  private final Logger logger = LoggerFactory.getLogger(FeedbackRolesPermissionsManagementAction.class);

  private FeedbackRolesPermissionManager feedbackRolesPermissionManager;
  private FeedbackPermissionManager feedbackPermissionManager;
  private RoleManager roleManager;
  private ClusterTypeManager clusterTypeManager;

  private List<FeedbackRolesPermission> feedbackRolesPermissions;
  private List<FeedbackPermission> feedbackPermissionsList;
  private List<Role> roleList;
  private List<ClusterType> clusterTypeList;


  @Inject
  public FeedbackRolesPermissionsManagementAction(APConfig config,
    FeedbackRolesPermissionManager feedbackRolesPermissionManager, RoleManager roleManager,
    FeedbackPermissionManager feedbackPermissionManager, ClusterTypeManager clusterTypeManager) {
    super(config);
    this.feedbackRolesPermissionManager = feedbackRolesPermissionManager;
    this.roleManager = roleManager;
    this.feedbackPermissionManager = feedbackPermissionManager;
    this.clusterTypeManager = clusterTypeManager;
  }

  public List<ClusterType> getClusterTypeList() {
    return clusterTypeList;
  }

  public ClusterTypeManager getClusterTypeManager() {
    return clusterTypeManager;
  }


  public List<FeedbackPermission> getFeedbackPermissionsList() {
    return feedbackPermissionsList;
  }

  public List<FeedbackRolesPermission> getFeedbackRolesPermissions() {
    return feedbackRolesPermissions;
  }


  public List<Role> getRoleList() {
    return roleList;
  }

  public RoleManager getRoleManager() {
    return roleManager;
  }

  @Override
  public void prepare() throws Exception {
    feedbackRolesPermissions = new ArrayList<>();
    feedbackPermissionsList = new ArrayList<>();

    try {

      long globalUnitId = this.getCurrentGlobalUnit().getId();

      List<Long> newIds = (List<Long>) this.getRequest().getSession().getAttribute("recentlyCreatedFRP");

      feedbackRolesPermissions =
        feedbackRolesPermissionManager.getFeedbackRolesPermissionByGlobalUnitID(globalUnitId).stream()
          .sorted(Comparator.comparing(
            frp -> frp.getFeedbackPermission() != null ? frp.getFeedbackPermission().getId() : Long.MAX_VALUE))
          .peek(frp -> {
            if (newIds != null && newIds.contains(frp.getId())) {
              frp.setRecentlyCreated(true);
            }
          }).collect(Collectors.toList());

      if (newIds != null) {
        this.getRequest().getSession().removeAttribute("recentlyCreatedFRP");
      }

      feedbackPermissionsList = feedbackPermissionManager.findAll();

      roleList = roleManager.findAll().stream()
        .filter(r -> r.getCrp() != null && Objects.equals(r.getCrp().getId(), globalUnitId))
        .sorted(Comparator.comparing(Role::getAcronym, Comparator.nullsLast(String::compareToIgnoreCase)))
        .collect(Collectors.toList());


      clusterTypeList = clusterTypeManager.findAll();

    } catch (Exception e) {
      roleList = new ArrayList<>();
    }

    if (this.isHttpPost()) {
      feedbackRolesPermissions.clear();
      feedbackPermissionsList.clear();
      clusterTypeList.clear();
      roleList.clear();
    }
  }

  /**
   * Saves or updates FeedbackRolesPermission entities.
   * - If an ID is present, it updates the existing record.
   * - If no ID is present, it creates a new one.
   * This method does not perform uniqueness validation, allowing overwriting existing entries.
   * It's up to the user interface to prevent unintentional duplicates.
   */
  @Override
  public String save() {
    if (!this.hasPermission("*")) {
      return NOT_AUTHORIZED;
    }

    if (feedbackRolesPermissions != null) {

      List<Long> inputIds = feedbackRolesPermissions.stream().map(FeedbackRolesPermission::getId)
        .filter(Objects::nonNull).collect(Collectors.toList());

      List<FeedbackRolesPermission> allExisting =
        feedbackRolesPermissionManager.getFeedbackRolesPermissionByGlobalUnitID(this.getCurrentGlobalUnit().getId());

      allExisting.stream().filter(existing -> existing.getId() != null && !inputIds.contains(existing.getId()))
        .forEach(permissionToDelete -> {
          try {
            feedbackRolesPermissionManager.deleteFeedbackRolesPermission(permissionToDelete.getId());
          } catch (Exception e) {
            logger.error("Error deleting FeedbackRolesPermission with ID: {}", permissionToDelete.getId(), e);
          }
        });
    }

    if (feedbackRolesPermissions != null && !feedbackRolesPermissions.isEmpty()) {
      List<Long> newIds = new ArrayList<>();

      for (FeedbackRolesPermission inputPermission : feedbackRolesPermissions) {
        try {

          FeedbackRolesPermission permissionToSave = (inputPermission.getId() != null)
            ? feedbackRolesPermissionManager.getFeedbackRolesPermissionById(inputPermission.getId())
            : new FeedbackRolesPermission();

          boolean isNew = inputPermission.getId() == null;

          if (inputPermission.getClusterType() != null && inputPermission.getClusterType().getId() != null) {
            ClusterType clusterTypeSave =
              clusterTypeManager.getClusterTypeById(inputPermission.getClusterType().getId());
            if (clusterTypeSave != null) {
              permissionToSave.setClusterType(clusterTypeSave);
            } else {
              permissionToSave.setClusterType(null);
            }
          }
          permissionToSave.setFeedbackPermission(inputPermission.getFeedbackPermission());
          permissionToSave.setDescription(inputPermission.getDescription());
          permissionToSave.setRole(inputPermission.getRole());
          permissionToSave.setGlobalUnit(
            inputPermission.getGlobalUnit() != null ? inputPermission.getGlobalUnit() : this.getCurrentGlobalUnit());

          permissionToSave = feedbackRolesPermissionManager.saveFeedbackRolesPermission(permissionToSave);

          if (isNew && permissionToSave.getId() != null) {
            newIds.add(permissionToSave.getId());
          }
        } catch (Exception e) {
          logger.error("Error saving FeedbackRolesPermission: {}", e.getMessage(), e);
        }
      }

      if (!newIds.isEmpty()) {
        this.getRequest().getSession().setAttribute("recentlyCreatedFRP", newIds);
      }
    }

    if (this.getUrl() == null || this.getUrl().isEmpty()) {
      if (this.getInvalidFields() != null && !this.getInvalidFields().isEmpty()) {
        this.setActionMessages(null);
        this.getInvalidFields().forEach((key, value) -> this.addActionMessage(key + ": " + value));
      }
      this.addActionMessage("message:" + this.getText("saving.saved"));

      return SUCCESS;
    } else {
      this.addActionMessage("");
      this.setActionMessages(null);
      return REDIRECT;
    }

  }

  public void setClusterTypeList(List<ClusterType> clusterTypeList) {
    this.clusterTypeList = clusterTypeList;
  }

  public void setClusterTypeManager(ClusterTypeManager clusterTypeManager) {
    this.clusterTypeManager = clusterTypeManager;
  }

  public void setFeedbackPermissionsList(List<FeedbackPermission> feedbackPermissionsList) {
    this.feedbackPermissionsList = feedbackPermissionsList;
  }

  public void setFeedbackRolesPermissions(List<FeedbackRolesPermission> feedbackRolesPermissions) {
    this.feedbackRolesPermissions = feedbackRolesPermissions;
  }

  public void setRoleList(List<Role> roleList) {
    this.roleList = roleList;
  }

  public void setRoleManager(RoleManager roleManager) {
    this.roleManager = roleManager;
  }

  @Override
  public void validate() {
    if (save) {
    }
  }
}