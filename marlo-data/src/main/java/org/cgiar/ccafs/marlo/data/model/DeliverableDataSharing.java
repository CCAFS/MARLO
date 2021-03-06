package org.cgiar.ccafs.marlo.data.model;
// Generated Jan 5, 2017 7:38:48 AM by Hibernate Tools 3.4.0.CR1

import com.google.gson.annotations.Expose;

/**
 * DeliverableDataSharing generated by hbm2java
 */
public class DeliverableDataSharing extends MarloBaseEntity implements java.io.Serializable {

  private static final long serialVersionUID = -1666405005550147437L;

  @Expose
  private Deliverable deliverable;

  @Expose
  private Boolean institutionalRepository;

  @Expose
  private String linkInstitutionalRepository;

  @Expose
  private Boolean ccfasHostGreater;

  @Expose
  private String linkCcfasHostGreater;

  @Expose
  private Boolean ccfasHostSmaller;

  @Expose
  private Phase phase;


  public DeliverableDataSharing() {
  }

  public DeliverableDataSharing(Deliverable deliverable) {
    this.deliverable = deliverable;
  }

  public Boolean getCcfasHostGreater() {
    return ccfasHostGreater;
  }


  public Boolean getCcfasHostSmaller() {
    return ccfasHostSmaller;
  }


  public Deliverable getDeliverable() {
    return deliverable;
  }

  public Boolean getInstitutionalRepository() {
    return institutionalRepository;
  }


  public String getLinkCcfasHostGreater() {
    return linkCcfasHostGreater;
  }

  public String getLinkInstitutionalRepository() {
    return linkInstitutionalRepository;
  }

  public Phase getPhase() {
    return phase;
  }

  public void setCcfasHostGreater(Boolean ccfasHostGreater) {
    this.ccfasHostGreater = ccfasHostGreater;
  }

  public void setCcfasHostSmaller(Boolean ccfasHostSmaller) {
    this.ccfasHostSmaller = ccfasHostSmaller;
  }

  public void setDeliverable(Deliverable deliverable) {
    this.deliverable = deliverable;
  }

  public void setInstitutionalRepository(Boolean institutionalRepository) {
    this.institutionalRepository = institutionalRepository;
  }

  public void setLinkCcfasHostGreater(String linkCcfasHostGreater) {
    this.linkCcfasHostGreater = linkCcfasHostGreater;
  }


  public void setLinkInstitutionalRepository(String linkInstitutionalRepository) {
    this.linkInstitutionalRepository = linkInstitutionalRepository;
  }

  public void setPhase(Phase phase) {
    this.phase = phase;
  }

  @Override
  public String toString() {
    return "DeliverableDataSharing [id=" + this.getId() + ", deliverable=" + deliverable + ", institutionalRepository="
      + institutionalRepository + ", linkInstitutionalRepository=" + linkInstitutionalRepository + ", ccfasHostGreater="
      + ccfasHostGreater + ", linkCcfasHostGreater=" + linkCcfasHostGreater + ", ccfasHostSmaller=" + ccfasHostSmaller
      + "]";
  }

}

