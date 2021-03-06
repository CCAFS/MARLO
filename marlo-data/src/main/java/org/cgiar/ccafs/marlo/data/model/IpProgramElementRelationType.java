package org.cgiar.ccafs.marlo.data.model;
// Generated Jan 3, 2017 1:26:41 PM by Hibernate Tools 4.3.1.Final


import org.cgiar.ccafs.marlo.data.IAuditLog;

import java.util.HashSet;
import java.util.Set;

import com.google.gson.annotations.Expose;

/**
 * IpProgramElementRelationTypes generated by hbm2java
 */
public class IpProgramElementRelationType extends MarloBaseEntity implements java.io.Serializable, IAuditLog {


  /**
   * 
   */
  private static final long serialVersionUID = 6317495249767270568L;

  @Expose
  private String name;
  private Set<IpProgramElement> ipProgramElements = new HashSet<IpProgramElement>(0);

  public IpProgramElementRelationType() {
  }

  public IpProgramElementRelationType(String name) {
    this.name = name;
  }

  public IpProgramElementRelationType(String name, Set<IpProgramElement> ipProgramElementses) {
    this.name = name;
    this.ipProgramElements = ipProgramElementses;
  }

  public Set<IpProgramElement> getIpProgramElements() {
    return this.ipProgramElements;
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

  @Override
  public boolean isActive() {

    return true;
  }

  public void setIpProgramElements(Set<IpProgramElement> ipProgramElementses) {
    this.ipProgramElements = ipProgramElementses;
  }

  @Override
  public void setModifiedBy(User modifiedBy) {

  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return "IpProgramElementRelationType [id=" + this.getId() + ", name=" + name + "]";
  }


}

