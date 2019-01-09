package org.cgiar.ccafs.marlo.data.model;
// Generated Jan 8, 2019 3:50:56 PM by Hibernate Tools 5.3.6.Final


import org.cgiar.ccafs.marlo.data.IAuditLog;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.gson.annotations.Expose;

public class ProjectPolicy extends MarloAuditableEntity implements java.io.Serializable, IAuditLog {

  private static final long serialVersionUID = -4753410776769726974L;

  @Expose
  private Project project;

  private ProjectPolicyInfo projectPolicyInfo;

  private Set<ProjectPolicyInfo> projectPolicyInfos = new HashSet<ProjectPolicyInfo>(0);

  public ProjectPolicy() {
  }

  @Override
  public String getLogDeatil() {
    StringBuilder sb = new StringBuilder();
    sb.append("Id : ").append(this.getId());
    return sb.toString();
  }

  public Project getProject() {
    return project;
  }

  public ProjectPolicyInfo getProjectPolicyInfo() {
    return projectPolicyInfo;
  }

  public ProjectPolicyInfo getProjectPolicyInfo(Phase phase) {
    if (this.getProjectPolicyInfo() != null) {
      return this.getProjectPolicyInfo();
    } else {
      List<ProjectPolicyInfo> infos =
        projectPolicyInfos.stream().filter(c -> c != null && c.getPhase() != null && c.getPhase().getId() != null
          && c.getPhase().getId().longValue() == phase.getId()).collect(Collectors.toList());
      if (!infos.isEmpty()) {
        this.setProjectPolicyInfo(infos.get(0));
        return this.getProjectPolicyInfo();
      } else {
        return null;
      }
    }
  }

  public Set<ProjectPolicyInfo> getProjectPolicyInfos() {
    return projectPolicyInfos;
  }

  public void setProject(Project project) {
    this.project = project;
  }

  public void setProjectPolicyInfo(ProjectPolicyInfo projectPolicyInfo) {
    this.projectPolicyInfo = projectPolicyInfo;
  }

  public void setProjectPolicyInfos(Set<ProjectPolicyInfo> projectPolicyInfos) {
    this.projectPolicyInfos = projectPolicyInfos;
  }

}

