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
// Generated May 26, 2016 9:42:28 AM by Hibernate Tools 4.3.1.Final


import org.cgiar.ccafs.marlo.data.IAuditLog;

import java.util.HashSet;
import java.util.Set;

import com.google.gson.annotations.Expose;

/**
 * SrfCrossCuttingIssue generated by hbm2java
 */
public class SrfCrossCuttingIssue extends MarloAuditableEntity implements java.io.Serializable, IAuditLog {


  private static final long serialVersionUID = -1176189419675828693L;


  @Expose
  private String name;

  @Expose
  private String smoCode;

  private Set<SrfIdo> srfIdos = new HashSet<SrfIdo>(0);

  public SrfCrossCuttingIssue() {
  }

  @Override
  public String getLogDeatil() {
    StringBuilder sb = new StringBuilder();
    sb.append("Id : ").append(this.getId());
    return sb.toString();
  }

  public String getName() {
    return this.name;
  }

  public String getSmoCode() {
    return smoCode;
  }

  public Set<SrfIdo> getSrfIdos() {
    return this.srfIdos;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setSmoCode(String smoCode) {
    this.smoCode = smoCode;
  }

  public void setSrfIdos(Set<SrfIdo> srfIdos) {
    this.srfIdos = srfIdos;
  }

  @Override
  public String toString() {
    return "SrfCrossCuttingIssue [id=" + this.getId() + ", name=" + name + "]";
  }
}

