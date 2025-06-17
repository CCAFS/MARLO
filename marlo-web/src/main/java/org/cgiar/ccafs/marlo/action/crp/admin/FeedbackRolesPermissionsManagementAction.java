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
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.inject.Inject;


public class FeedbackRolesPermissionsManagementAction extends BaseAction {

  private static final long serialVersionUID = -793652591843623397L;

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
      feedbackRolesPermissions = feedbackRolesPermissionManager.getFeedbackRolesPermissionByGlobalUnitID(globalUnitId);

      feedbackPermissionsList = feedbackPermissionManager.findAll();

      roleList = roleManager.findAll().stream()
        .filter(r -> r.getCrp() != null && Objects.equals(r.getCrp().getId(), globalUnitId))
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


  @Override
  public String save() {
    if (this.canAccessSuperAdmin()) {
      if (feedbackRolesPermissions != null && !feedbackRolesPermissions.isEmpty()) {

        for (FeedbackRolesPermission rolePermission : feedbackRolesPermissions) {

          FeedbackRolesPermission rolePermissionSave = new FeedbackRolesPermission();

          if (rolePermission.getId() != null) {
            rolePermissionSave = feedbackRolesPermissionManager.getFeedbackRolesPermissionById(rolePermission.getId());
          }

          if (rolePermission.getClusterType() != null) {
            rolePermissionSave.setClusterType(rolePermission.getClusterType());
          }
          if (rolePermission.getFeedbackPermission() != null) {
            rolePermissionSave.setFeedbackPermission(rolePermission.getFeedbackPermission());
          }
          if (rolePermission.getDescription() != null) {
            rolePermissionSave.setDescription(rolePermission.getDescription());
          }
          if (rolePermission.getRole() != null) {
            rolePermissionSave.setRole(rolePermission.getRole());
          }
          if (rolePermission.getGlobalUnit() != null) {
            rolePermissionSave.setGlobalUnit(rolePermission.getGlobalUnit());
          } else {
            rolePermissionSave.setGlobalUnit(this.getCurrentGlobalUnit());
          }

          feedbackRolesPermissionManager.saveFeedbackRolesPermission(null);

        }
      }

      if (this.getUrl() == null || this.getUrl().isEmpty()) {
        Collection<String> messages = this.getActionMessages();
        if (this.getInvalidFields() != null && !this.getInvalidFields().isEmpty()) {
          this.setActionMessages(null);
          // this.addActionMessage(Map.toString(this.getInvalidFields().toArray()));
          List<String> keys = new ArrayList<String>(this.getInvalidFields().keySet());
          for (String key : keys) {
            this.addActionMessage(key + ": " + this.getInvalidFields().get(key));
          }
        } else {
          // this.addActionMessage("message:" + this.getText("saving.saved"));
        }
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