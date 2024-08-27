package org.cgiar.ccafs.marlo.data.model;
// Generated Apr 30, 2018 10:29:46 AM by Hibernate Tools 3.4.0.CR1

import com.google.gson.annotations.Expose;

/**
 * RepIndStageStudy generated by hbm2java
 */
public class RepIndStageStudy extends MarloBaseEntity implements java.io.Serializable {

  private static final long serialVersionUID = -3600242932024372635L;

  @Expose
  private String name;

  @Expose
  private String description;

  @Expose
  private String descriptionAF;

  private String composedName;


  public RepIndStageStudy() {
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (this.getClass() != obj.getClass()) {
      return false;
    }
    RepIndStageStudy other = (RepIndStageStudy) obj;
    if (this.getId() == null) {
      if (other.getId() != null) {
        return false;
      }
    } else if (!this.getId().equals(other.getId())) {
      return false;
    }
    return true;
  }

  public String getComposedName() {
    return composedName;
  }

  public String getDescription() {
    return description;
  }

  public String getDescriptionAF() {
    return descriptionAF;
  }

  public String getName() {
    return name;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.getId() == null) ? 0 : this.getId().hashCode());
    return result;
  }

  public void setComposedName(String composedName) {
    this.composedName = composedName;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setDescriptionAF(String descriptionAF) {
    this.descriptionAF = descriptionAF;
  }

  public void setName(String name) {
    this.name = name;
  }
}

