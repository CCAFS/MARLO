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

import org.cgiar.ccafs.marlo.data.IAuditLog;

import java.util.HashSet;
import java.util.Set;

import com.google.gson.annotations.Expose;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class DeliverableType implements java.io.Serializable, IAuditLog {


  private static final long serialVersionUID = 2591192504126616140L;


  @Expose
  private Long id;

  @Expose
  private DeliverableType deliverableType;
  @Expose
  private String name;
  @Expose
  private String description;
  @Expose
  private Integer timeline;
  @Expose
  private Boolean fair;
  @Expose
  private Crp crp;

  @Expose
  private GlobalUnit globalUnit;
  private Set<DeliverableType> deliverableTypes = new HashSet<DeliverableType>(0);
  private Set<Deliverable> deliverables = new HashSet<Deliverable>(0);


  public DeliverableType() {
  }


  public DeliverableType(DeliverableType deliverableType, String name, String description, Integer timeline,
    Set<DeliverableType> deliverableTypes, Set<Deliverable> deliverables) {
    this.deliverableType = deliverableType;
    this.name = name;
    this.description = description;
    this.timeline = timeline;
    this.deliverableTypes = deliverableTypes;
    this.deliverables = deliverables;
  }

  public Crp getCrp() {
    return crp;
  }


  public Set<Deliverable> getDeliverables() {
    return this.deliverables;
  }

  public DeliverableType getDeliverableType() {
    return this.deliverableType;
  }

  public Set<DeliverableType> getDeliverableTypes() {
    return this.deliverableTypes;
  }

  public String getDescription() {
    return this.description;
  }

  public Boolean getFair() {
    return fair;
  }

  public GlobalUnit getGlobalUnit() {
    return globalUnit;
  }

  @Override
  public Long getId() {
    return this.id;
  }

  @Override
  public String getLogDeatil() {
    StringBuilder sb = new StringBuilder();

    sb.append("Id : ").append(this.getId());


    return sb.toString();
  }

  @Override
  public String getModificationJustification() {

    return "";
  }

  @Override
  public User getModifiedBy() {
    User u = new User();
    u.setId(new Long(3));
    return u;
  }

  public String getName() {
    return this.name;
  }

  public Integer getTimeline() {
    return this.timeline;
  }

  @Override
  public boolean isActive() {
    return true;
  }

  public void setCrp(Crp crp) {
    this.crp = crp;
  }

  public void setDeliverables(Set<Deliverable> deliverables) {
    this.deliverables = deliverables;
  }

  public void setDeliverableType(DeliverableType deliverableType) {
    this.deliverableType = deliverableType;
  }

  public void setDeliverableTypes(Set<DeliverableType> deliverableTypes) {
    this.deliverableTypes = deliverableTypes;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setFair(Boolean fair) {
    this.fair = fair;
  }

  public void setGlobalUnit(GlobalUnit globalUnit) {
    this.globalUnit = globalUnit;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setTimeline(Integer timeline) {
    this.timeline = timeline;
  }

}
