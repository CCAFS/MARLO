/*****************************************************************
 * This file is part of Managing Agricultural Research for Learning &
 * Outcomes Platform (MARLO).
 * MARLO is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 * MARLO is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with MARLO. If not, see <http://www.gnu.org/licenses/>.
 *****************************************************************/

package org.cgiar.ccafs.marlo.data.model;

import org.cgiar.ccafs.marlo.data.IAuditLog;

import java.util.Date;

import com.google.gson.annotations.Expose;

public class TipParameters extends MarloBaseEntity implements java.io.Serializable, IAuditLog {


  private static final long serialVersionUID = -963914989396761020L;

  @Expose
  private String tipTokenService;
  @Expose
  private String tipLoginService;
  @Expose
  private String tipStatusService;
  @Expose
  private String tokenValue;
  @Expose
  private String privateKey;
  @Expose
  private String tipBaseUrl;
  @Expose
  private Date tokenDueDate;

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

  public String getPrivateKey() {
    return privateKey;
  }

  public String getTipBaseUrl() {
    return tipBaseUrl;
  }

  public String getTipLoginService() {
    return tipLoginService;
  }

  public String getTipStatusService() {
    return tipStatusService;
  }

  public String getTipTokenService() {
    return tipTokenService;
  }

  public Date getTokenDueDate() {
    return tokenDueDate;
  }

  public String getTokenValue() {
    return tokenValue;
  }

  @Override
  public boolean isActive() {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public void setModifiedBy(User modifiedBy) {
    // TODO Auto-generated method stub
  }

  public void setPrivateKey(String privateKey) {
    this.privateKey = privateKey;
  }

  public void setTipBaseUrl(String tipBaseUrl) {
    this.tipBaseUrl = tipBaseUrl;
  }

  public void setTipLoginService(String tipLoginService) {
    this.tipLoginService = tipLoginService;
  }


  public void setTipStatusService(String tipStatusService) {
    this.tipStatusService = tipStatusService;
  }

  public void setTipTokenService(String tipTokenService) {
    this.tipTokenService = tipTokenService;
  }

  public void setTokenDueDate(Date tokenDueDate) {
    this.tokenDueDate = tokenDueDate;
  }

  public void setTokenValue(String tokenValue) {
    this.tokenValue = tokenValue;
  }
}