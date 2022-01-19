package org.cgiar.ccafs.marlo.data.model;
// Generated Apr 18, 2018 1:21:50 PM by Hibernate Tools 3.4.0.CR1

import com.google.gson.annotations.Expose;

/**
 * RepIndGeographicScope generated by hbm2java
 */
public class RepIndGeographicScope extends MarloBaseEntity implements java.io.Serializable {

  private static final long serialVersionUID = 4303845202490583052L;
  @Expose
  private String name;
  @Expose
  private String iatiName;
  @Expose
  private String definition;

  private Boolean isOneCGIAR;

  private Boolean isMarlo;


  public RepIndGeographicScope() {
  }


  public String getDefinition() {
    return this.definition;
  }


  public String getIatiName() {
    return this.iatiName;
  }


  public Boolean getIsMarlo() {
    return isMarlo;
  }

  public Boolean getIsOneCGIAR() {
    return isOneCGIAR;
  }

  public String getName() {
    return this.name;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.getId() == null) ? 0 : this.getId().hashCode());
    return result;
  }

  public void setDefinition(String definition) {
    this.definition = definition;
  }

  public void setIatiName(String iatiName) {
    this.iatiName = iatiName;
  }

  public void setIsMarlo(Boolean isMarlo) {
    this.isMarlo = isMarlo;
  }

  public void setIsOneCGIAR(Boolean isOneCGIAR) {
    this.isOneCGIAR = isOneCGIAR;
  }

  public void setName(String name) {
    this.name = name;
  }


}

