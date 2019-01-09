package org.cgiar.ccafs.marlo.data.model;
// Generated Jan 8, 2019 11:49:44 AM by Hibernate Tools 5.3.6.Final

import com.google.gson.annotations.Expose;

public class RepIndPolicyType extends MarloBaseEntity implements java.io.Serializable {


  private static final long serialVersionUID = -127424378127879204L;
  @Expose
  private Long id;
  @Expose
  private String name;

  public RepIndPolicyType() {
  }

  public RepIndPolicyType(String name) {
    this.name = name;
  }

  @Override
  public Long getId() {
    return this.id;
  }

  public String getName() {
    return this.name;
  }

  @Override
  public void setId(Long id) {
    this.id = id;
  }

  public void setName(String name) {
    this.name = name;
  }


}

