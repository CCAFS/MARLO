package org.cgiar.ccafs.marlo.data.model;
// Generated Jan 3, 2017 1:26:41 PM by Hibernate Tools 4.3.1.Final

import org.cgiar.ccafs.marlo.data.IAuditLog;

import com.google.gson.annotations.Expose;

/**
 * MogSynthesis generated by hbm2java
 */
public class MogSynthesy extends MarloBaseEntity implements java.io.Serializable, IAuditLog {


  /**
   * 
   */
  private static final long serialVersionUID = -5798198289191463231L;


  @Expose
  private IpElement ipElement;

  @Expose
  private IpProgram ipProgram;

  @Expose
  private int year;


  @Expose
  private String synthesisReport;

  @Expose
  private String synthesisGender;

  public MogSynthesy() {
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }

    MogSynthesy other = (MogSynthesy) obj;
    if (ipElement == null) {
      if (other.ipElement != null) {
        return false;
      }
    } else if (!ipElement.getId().equals(other.ipElement.getId())) {
      return false;
    }
    if (ipProgram == null) {
      if (other.ipProgram != null) {
        return false;
      }
    } else if (!ipProgram.getId().equals(other.ipProgram.getId())) {
      return false;
    }
    if (year != other.year) {
      return false;
    }
    return true;
  }


  public IpElement getIpElement() {
    return ipElement;
  }


  public IpProgram getIpProgram() {
    return ipProgram;
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

  public String getSynthesisGender() {
    return synthesisGender;
  }

  public String getSynthesisReport() {
    return synthesisReport;
  }

  public int getYear() {
    return year;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((ipElement == null) ? 0 : ipElement.hashCode());
    result = prime * result + ((ipProgram == null) ? 0 : ipProgram.hashCode());
    result = prime * result + year;
    return result;
  }

  @Override
  public boolean isActive() {

    return true;
  }

  public void setIpElement(IpElement ipElement) {
    this.ipElement = ipElement;
  }

  public void setIpProgram(IpProgram ipProgram) {
    this.ipProgram = ipProgram;
  }


  @Override
  public void setModifiedBy(User modifiedBy) {

  }


  public void setSynthesisGender(String synthesisGender) {
    this.synthesisGender = synthesisGender;
  }


  public void setSynthesisReport(String synthesisReport) {
    this.synthesisReport = synthesisReport;
  }


  public void setYear(int year) {
    this.year = year;
  }

  @Override
  public String toString() {
    return "MogSynthesy [id=" + this.getId() + ", ipElement=" + ipElement + ", ipProgram=" + ipProgram + ", year="
      + year + ", synthesisReport=" + synthesisReport + ", synthesisGender=" + synthesisGender + "]";
  }


}

