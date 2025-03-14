package org.cgiar.ccafs.marlo.data.model;
// Generated Apr 30, 2018 10:52:36 AM by Hibernate Tools 3.4.0.CR1

import org.cgiar.ccafs.marlo.data.IAuditLog;

import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.annotations.Expose;

public class ReportConfiguration extends MarloBaseEntity implements java.io.Serializable, IAuditLog {


  private static final long serialVersionUID = -7806050142645120199L;

  @Expose
  private String oicrTemplateData;
  @Expose
  private String innovationTemplateData;

  public ReportConfiguration() {
  }

  public Map<String, Object> convertToMap() {
    ObjectMapper oMapper = new ObjectMapper();
    Map<String, Object> map = oMapper.convertValue(this, Map.class);

    return map;
  }

  public String getInnovationTemplateData() {
    return innovationTemplateData;
  }

  @Override
  public String getLogDeatil() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getModificationJustification() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public User getModifiedBy() {
    // TODO Auto-generated method stub
    return null;
  }

  public String getOicrTemplateData() {
    return oicrTemplateData;
  }

  @Override
  public boolean isActive() {
    // TODO Auto-generated method stub
    return false;
  }

  public void setInnovationTemplateData(String innovationTemplateData) {
    this.innovationTemplateData = innovationTemplateData;
  }

  @Override
  public void setModifiedBy(User modifiedBy) {
    // TODO Auto-generated method stub
  }

  public void setOicrTemplateData(String oicrTemplateData) {
    this.oicrTemplateData = oicrTemplateData;
  }
}