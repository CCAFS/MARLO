package org.cgiar.ccafs.marlo.data.model;
// Generated Jan 5, 2017 7:52:28 AM by Hibernate Tools 4.3.1.Final


/**
 * IpRelationships generated by hbm2java
 */
public class IpRelationship extends MarloBaseEntity implements java.io.Serializable {


  /**
   * 
   */
  private static final long serialVersionUID = 5395742573146117408L;
  private IpElement ipElementsByParentId;
  private IpElement ipElementsByChildId;
  private long relationTypeId;

  public IpRelationship() {
  }

  public IpElement getIpElementsByChildId() {
    return this.ipElementsByChildId;
  }

  public IpElement getIpElementsByParentId() {
    return this.ipElementsByParentId;
  }

  public long getRelationTypeId() {
    return this.relationTypeId;
  }

  public void setIpElementsByChildId(IpElement ipElementsByChildId) {
    this.ipElementsByChildId = ipElementsByChildId;
  }

  public void setIpElementsByParentId(IpElement ipElementsByParentId) {
    this.ipElementsByParentId = ipElementsByParentId;
  }

  public void setRelationTypeId(long relationTypeId) {
    this.relationTypeId = relationTypeId;
  }

  @Override
  public String toString() {
    return "IpRelationship [id=" + this.getId() + ", ipElementsByParentId=" + ipElementsByParentId
      + ", ipElementsByChildId=" + ipElementsByChildId + ", relationTypeId=" + relationTypeId + "]";
  }


}

