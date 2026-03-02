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

      if (!this.isHttpPost()) {
        // For GET: Load feedback roles permissions from DB
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
      } else {
        // For POST: Will bind feedback roles permissions manually from request in save()
        feedbackRolesPermissions = new ArrayList<>();
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
  }

  /**
   * Manually bind feedback roles permissions from HTTP request parameters.
   * This is necessary because Struts 6 cannot automatically populate lists when items are deleted.
   */
  private void bindFeedbackRolesPermissionsFromRequest() {
    feedbackRolesPermissions = new ArrayList<>();
    int index = 0;
    boolean hasMore = true;

    while (hasMore) {
      String idParam = this.getRequest().getParameter("feedbackRolesPermissions[" + index + "].id");
      String descriptionParam = this.getRequest().getParameter("feedbackRolesPermissions[" + index + "].description");
      String feedbackPermissionIdParam =
        this.getRequest().getParameter("feedbackRolesPermissions[" + index + "].feedbackPermission.id");
      String roleIdParam = this.getRequest().getParameter("feedbackRolesPermissions[" + index + "].role.id");
      String clusterTypeIdParam =
        this.getRequest().getParameter("feedbackRolesPermissions[" + index + "].clusterType.id");

      boolean hasAnyContent = (idParam != null && !idParam.trim().isEmpty()) ||
        (descriptionParam != null && !descriptionParam.trim().isEmpty()) ||
        (feedbackPermissionIdParam != null && !feedbackPermissionIdParam.trim().isEmpty()) ||
        (roleIdParam != null && !roleIdParam.trim().isEmpty()) ||
        (clusterTypeIdParam != null && !clusterTypeIdParam.trim().isEmpty());

      if (hasAnyContent) {
        try {
          FeedbackRolesPermission permission = new FeedbackRolesPermission();

          // ID (can be null for new elements)
          if (idParam != null && !idParam.trim().isEmpty()) {
            try {
              Long id = Long.parseLong(idParam);
              permission.setId(id);
            } catch (NumberFormatException e) {
              // Silent fail
            }
          }

          // Description
          permission.setDescription(descriptionParam);

          // Feedback Permission ID
          if (feedbackPermissionIdParam != null && !feedbackPermissionIdParam.trim().isEmpty()) {
            try {
              Long feedbackPermissionId = Long.parseLong(feedbackPermissionIdParam);
              FeedbackPermission feedbackPermission =
                feedbackPermissionManager.getFeedbackPermissionById(feedbackPermissionId);
              permission.setFeedbackPermission(feedbackPermission);
            } catch (NumberFormatException e) {
              // Silent fail
            }
          }

          // Role ID
          if (roleIdParam != null && !roleIdParam.trim().isEmpty()) {
            try {
              Long roleId = Long.parseLong(roleIdParam);
              Role role = roleManager.getRoleById(roleId);
              permission.setRole(role);
            } catch (NumberFormatException e) {
              // Silent fail
            }
          }

          // Cluster Type ID
          if (clusterTypeIdParam != null && !clusterTypeIdParam.trim().isEmpty()) {
            try {
              Long clusterTypeId = Long.parseLong(clusterTypeIdParam);
              ClusterType clusterType = clusterTypeManager.getClusterTypeById(clusterTypeId);
              permission.setClusterType(clusterType);
            } catch (NumberFormatException e) {
              // Silent fail
            }
          }

          feedbackRolesPermissions.add(permission);
          index++;
        } catch (Exception e) {
          hasMore = false;
        }
      } else {
        hasMore = false;
      }
    }
  }

  @Override
  public String save() {
    if (!this.hasPermission("*")) {
      return NOT_AUTHORIZED;
    }

    // Manually bind feedback roles permissions from request parameters
    bindFeedbackRolesPermissionsFromRequest();

    // Collect inputIds and existingBeforeSave BEFORE save loop (to avoid deleting newly created items)
    final List<Long> inputIds = (feedbackRolesPermissions != null)
      ? feedbackRolesPermissions.stream().map(FeedbackRolesPermission::getId).filter(Objects::nonNull)
        .collect(Collectors.toList())
      : new ArrayList<>();

    final List<FeedbackRolesPermission> existingBeforeSave =
      feedbackRolesPermissionManager.getFeedbackRolesPermissionByGlobalUnitID(this.getCurrentGlobalUnit().getId());

    if (feedbackRolesPermissions != null && !feedbackRolesPermissions.isEmpty()) {
      List<Long> newIds = new ArrayList<>();

      // FIRST: Save/update all feedback roles permissions from the form
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

    // THEN: Delete feedback roles permissions not present in the form (using pre-save snapshot)
    if (existingBeforeSave != null && !existingBeforeSave.isEmpty()) {
      for (FeedbackRolesPermission permissionToDelete : existingBeforeSave) {
        if (permissionToDelete.getId() != null && !inputIds.contains(permissionToDelete.getId())) {
          try {
            feedbackRolesPermissionManager.deleteFeedbackRolesPermission(permissionToDelete.getId());
          } catch (Exception e) {
            logger.error("Error deleting FeedbackRolesPermission with ID: {}", permissionToDelete.getId(), e);
          }
        }
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
    // Note: This setter is kept for JSP/FTL access but is not used for Struts binding
    // We bind feedback roles permissions manually in save() using bindFeedbackRolesPermissionsFromRequest()
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