/*
 * This file is part of Managing Agricultural Research for Learning&*Outcomes Platform(MARLO).
 ** MARLO is free software:you can redistribute it and/or modify
 ** it under the terms of the GNU General Public License as published by
 ** the Free Software Foundation,either version 3 of the License,or*at your option)any later version.
 ** MARLO is distributed in the hope that it will be useful,
 ** but WITHOUT ANY WARRANTY;without even the implied warranty of*MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.See
 * the
 ** GNU General Public License for more details.
 ** You should have received a copy of the GNU General Public License
 ** along with MARLO.If not,see<http:// www.gnu.org/licenses/>.
 *****************************************************************/

package org.cgiar.ccafs.marlo.action.superadmin;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.data.manager.TipParametersManager;
import org.cgiar.ccafs.marlo.data.model.TipParameters;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import org.jfree.util.Log;


public class TIPManagementAction extends BaseAction {

  private static final long serialVersionUID = -793652591843623397L;
  private TipParametersManager tipParameterManager;
  private TipParameters tipParameter;

  @Inject
  public TIPManagementAction(APConfig config, TipParametersManager tipParameterManager) {
    super(config);
    this.tipParameterManager = tipParameterManager;
  }

  public TipParameters getTipParameter() {
    return tipParameter;
  }

  @Override
  public void prepare() throws Exception {
    try {
      tipParameter = tipParameterManager.findAll().get(0);
    } catch (Exception e) {
      Log.error("error getting tip parameters " + e);
    }
  }

  @Override
  public String save() {
    if (this.canAccessSuperAdmin()) {
      if (tipParameter != null) {

        TipParameters tipParameterSave = new TipParameters();
        if (tipParameter.getId() != null) {
          tipParameterSave.setId(tipParameter.getId());
        }
        if (tipParameter.getTipBaseUrl() != null) {
          tipParameterSave.setTipBaseUrl(tipParameter.getTipBaseUrl());
        }
        if (tipParameter.getTipLoginService() != null) {
          tipParameterSave.setTipLoginService(tipParameter.getTipLoginService());
        }
        if (tipParameter.getTipStatusService() != null) {
          tipParameterSave.setTipStatusService(tipParameter.getTipStatusService());
        }
        if (tipParameter.getTipTokenService() != null) {
          tipParameterSave.setTipTokenService(tipParameter.getTipTokenService());
        }
        if (tipParameter.getTokenDueDate() != null) {
          tipParameterSave.setTokenDueDate(tipParameter.getTokenDueDate());
        }
        if (tipParameter.getTokenValue() != null) {
          tipParameterSave.setTokenValue(tipParameter.getTokenValue());
        }
        if (tipParameter.getPrivateKey() != null) {
          tipParameterSave.setPrivateKey(tipParameter.getPrivateKey());
        }
        if (tipParameter.getEncryptionKey() != null) {
          tipParameterSave.setEncryptionKey(tipParameter.getEncryptionKey());
        }
        if (tipParameter.getTipEmail() != null) {
          tipParameterSave.setTipEmail(tipParameter.getTipEmail());
        }
        if (tipParameter.getEmailText() != null) {
          tipParameterSave.setEmailText(tipParameter.getEmailText());
        }
        if (tipParameter.getEmailSubject() != null) {
          tipParameterSave.setEmailSubject(tipParameter.getEmailSubject());
        }

        tipParameterManager.saveTipParameters(tipParameterSave);
      }

      if (this.getUrl() == null || this.getUrl().isEmpty()) {
        Collection<String> messages = this.getActionMessages();
        if (this.getInvalidFields() != null && !this.getInvalidFields().isEmpty()) {
          this.setActionMessages(null);
          // this.addActionMessage(Map.toString(this.getInvalidFields().toArray()));
          List<String> keys = new ArrayList<String>(this.getInvalidFields().keySet());
          for (String key : keys) {
            this.addActionMessage(key + ": " + this.getInvalidFields().get(key));
          }
        } else {
          this.addActionMessage("message:" + this.getText("saving.saved"));
        }
        return SUCCESS;
      } else {
        this.addActionMessage("");
        this.setActionMessages(null);
        return REDIRECT;
      }

    } else {
      return NOT_AUTHORIZED;
    }
  }

  public void setTipParameter(TipParameters tipParameter) {
    this.tipParameter = tipParameter;
  }

}