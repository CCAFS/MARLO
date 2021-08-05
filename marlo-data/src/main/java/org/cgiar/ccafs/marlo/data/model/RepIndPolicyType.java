package org.cgiar.ccafs.marlo.data.model;
// Generated Jan 8, 2019 11:49:44 AM by Hibernate Tools 5.3.6.Final

import com.google.gson.annotations.Expose;

public class RepIndPolicyType extends MarloBaseEntity implements java.io.Serializable {


  private static final long serialVersionUID = -127424378127879204L;

  @Expose
  private String name;

  public RepIndPolicyType() {
  }

  public RepIndPolicyType(String name) {
    this.name = name;
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


  public void setName(String name) {
    this.name = name;
  }


}

