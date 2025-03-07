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
import org.cgiar.ccafs.marlo.data.manager.ReportConfigurationManager;
import org.cgiar.ccafs.marlo.data.model.ReportConfiguration;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import org.jfree.util.Log;


public class ReportsManagementAction extends BaseAction {

  private static final long serialVersionUID = -793652591843623397L;

  public static long getSerialversionuid() {
    return serialVersionUID;
  }

  private ReportConfigurationManager reportConfigurationManager;
  private List<ReportConfiguration> reportConfigurations;
  private String oicrTemplate;
  private ReportConfiguration reportConfiguration;

  @Inject
  public ReportsManagementAction(APConfig config, ReportConfigurationManager reportConfigurationManager) {
    super(config);
    this.reportConfigurationManager = reportConfigurationManager;
  }

  public String getOicrTemplate() {
    return oicrTemplate;
  }

  public void loadData() {
    if (reportConfigurations != null && !reportConfigurations.isEmpty()) {
      reportConfiguration = reportConfigurations.get(0);
      oicrTemplate = reportConfiguration.getOicrTemplateData();
    }
  }

  @Override
  public void prepare() throws Exception {
    reportConfigurations = new ArrayList<>();
    try {
      reportConfigurations = reportConfigurationManager.findAll();
      this.loadData();
    } catch (Exception e) {
      Log.error("error getting tip parameters " + e);
    }
  }

  @Override
  public String save() {
    if (this.canAccessSuperAdmin()) {
      if (reportConfiguration != null) {

        ReportConfiguration reportConfigurationSave = new ReportConfiguration();
        if (reportConfiguration.getId() != null) {
          reportConfigurationSave.setId(reportConfigurationSave.getId());
        }
        reportConfigurationSave.setOicrTemplateData(oicrTemplate);
        reportConfigurationManager.saveReportConfiguration(reportConfigurationSave);
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

    } else

    {
      return NOT_AUTHORIZED;
    }
  }

  public void setOicrTemplate(String oicrTemplate) {
    this.oicrTemplate = oicrTemplate;
  }
}