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

package org.cgiar.ccafs.marlo.data.model;
// Generated May 17, 2016 9:53:46 AM by Hibernate Tools 4.3.1.Final


import java.util.HashSet;
import java.util.Set;

import com.google.gson.annotations.Expose;

/**
 * Roles generated by hbm2java
 */
public class Role extends MarloBaseEntity implements java.io.Serializable {


  private static final long serialVersionUID = 8679238437361759448L;

  private GlobalUnit crp;


  @Expose
  private String description;


  @Expose
  private String acronym;

  @Expose
  private Integer order;

  @Expose
  private Set<UserRole> userRoles = new HashSet<UserRole>(0);

  @Expose
  private Set<FeedbackRolesPermission> feedbackRoles = new HashSet<FeedbackRolesPermission>(0);

  public Role() {
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }

    Role other = (Role) obj;
    if (this.getId() == null) {
      if (other.getId() != null) {
        return false;
      }
    } else if (!this.getId().equals(other.getId())) {
      return false;
    }
    return true;
  }


  public String getAcronym() {
    return this.acronym;
  }

  public String getAcronymDimanic() {
    String acronym = this.getAcronym();

    // If is Center Chang
    if (this.getCrp().getGlobalUnitType().getId() == 4) {
      switch (this.getAcronym()) {
        case "FPL":
          acronym = "Area/Program Leaders";
          break;
        case "CRP-Admin":
          acronym = "Admin";
          break;
        case "RPL":
          acronym = "Regional Offices Leaders";
          break;

      }

    }

    return acronym;

  }

  public String getAiccraAcronymDimanic() {
    String acronym = this.getAcronym();


    if (this.getCrp() != null && this.getCrp().getAcronym() != null && this.getCrp().getAcronym().equals("AICCRA")) {
      switch (this.getAcronym()) {
        case "PL":
          acronym = "Cluster Leader";
          break;
        case "PC":
          acronym = "Cluster Coordinator";
          break;
        case "PMU":
          acronym = "Secretariat";
          break;
        case "CP":
          acronym = "Contact Point";
          break;
        case "G":
          acronym = "Guest";
          break;
      }
    }

    return acronym;

  }

  public GlobalUnit getCrp() {
    return crp;
  }

  public String getDescription() {
    return this.description;
  }

  public Set<FeedbackRolesPermission> getFeedbackRoles() {
    return feedbackRoles;
  }

  public Integer getOrder() {
    return order;
  }

  public Set<UserRole> getUserRoles() {
    return this.userRoles;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.getId() == null) ? 0 : this.getId().hashCode());
    return result;
  }

  public void setAcronym(String acronym) {
    this.acronym = acronym;
  }


  public void setCrp(GlobalUnit crp) {
    this.crp = crp;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setFeedbackRoles(Set<FeedbackRolesPermission> feedbackRoles) {
    this.feedbackRoles = feedbackRoles;
  }

  public void setOrder(Integer order) {
    this.order = order;
  }

  public void setUserRoles(Set<UserRole> userRoles) {
    this.userRoles = userRoles;
  }

  @Override
  public String toString() {
    return "Role [id=" + this.getId() + ", crp=" + crp + ", description=" + description + ", acronym=" + acronym
      + ", order=" + order + "]";
  }
}

