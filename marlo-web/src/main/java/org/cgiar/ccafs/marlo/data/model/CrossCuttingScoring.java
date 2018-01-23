package org.cgiar.ccafs.marlo.data.model;
// Generated Jan 22, 2018 2:38:07 PM by Hibernate Tools 5.2.5.Final

import com.google.gson.annotations.Expose;


public class CrossCuttingScoring implements java.io.Serializable {


  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  @Expose
  private Long id;
  @Expose
  private String description;
  @Expose
  private String completeDescription;
  private Crp crp;

  public CrossCuttingScoring() {
  }


  public CrossCuttingScoring(long id, String description) {
    this.id = id;
    this.description = description;
  }

  public CrossCuttingScoring(long id, String description, String completeDescription, Crp crp) {
    this.id = id;
    this.description = description;
    this.completeDescription = completeDescription;
    this.crp = crp;
  }

  public String getCompleteDescription() {
    return this.completeDescription;
  }

  public Crp getCrp() {
    return this.crp;
  }

  public String getDescription() {
    return this.description;
  }

  public Long getId() {
    return this.id;
  }

  public void setCompleteDescription(String completeDescription) {
    this.completeDescription = completeDescription;
  }

  public void setCrp(Crp crp) {
    this.crp = crp;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setId(Long id) {
    this.id = id;
  }


}

