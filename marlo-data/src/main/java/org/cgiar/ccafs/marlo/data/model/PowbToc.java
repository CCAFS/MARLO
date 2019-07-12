package org.cgiar.ccafs.marlo.data.model;
// Generated Feb 2, 2018 8:27:14 AM by Hibernate Tools 3.4.0.CR1


import org.cgiar.ccafs.marlo.data.IAuditLog;

import com.google.gson.annotations.Expose;

public class PowbToc extends MarloAuditableEntity implements java.io.Serializable, IAuditLog {


  private static final long serialVersionUID = -1756166028966293976L;

  private PowbSynthesis powbSynthesis;

  @Expose
  private FileDB file;

  @Expose
  private String tocOverall;

  public PowbToc() {
  }

  public FileDB getFile() {
    return file;
  }


  @Override
  public String getLogDeatil() {
    StringBuilder sb = new StringBuilder();
    sb.append("Id : ").append(this.getId());
    return sb.toString();
  }

  public PowbSynthesis getPowbSynthesis() {
    return powbSynthesis;
  }

  public String getTocOverall() {
    return tocOverall;
  }

  public void setFile(FileDB file) {
    this.file = file;
  }

  public void setPowbSynthesis(PowbSynthesis powbSynthesis) {
    this.powbSynthesis = powbSynthesis;
  }


  public void setTocOverall(String tocOverall) {
    this.tocOverall = tocOverall;
  }


}

