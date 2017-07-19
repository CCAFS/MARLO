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


package org.cgiar.ccafs.marlo.interceptor;

import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.CrpManager;
import org.cgiar.ccafs.marlo.data.manager.CustomParameterManager;
import org.cgiar.ccafs.marlo.data.manager.ICenterManager;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.model.Crp;
import org.cgiar.ccafs.marlo.data.model.CustomParameter;

import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.inject.Inject;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.opensymphony.xwork2.util.LocalizedTextUtil;
import org.apache.struts2.ServletActionContext;

public class InternationalitazionFileInterceptor extends AbstractInterceptor {

  /**
   * For a specific file to work for a CRP, a parameter must be created
   * Crp_custom_file in the Database, the value of that parameter should not have the .properties
   * 
   * @author Christian David Garcia Oviedo
   */
  private static final long serialVersionUID = -3807232981762261100L;

  private UserManager userManager;

  private CrpManager crpManager;

  private ICenterManager centerManager;


  private CustomParameterManager crpParameterManager;

  @Inject
  public InternationalitazionFileInterceptor(UserManager userManager, CrpManager crpManager,
    CustomParameterManager crpParameterManager, ICenterManager centerManager) {
    this.userManager = userManager;
    this.crpManager = crpManager;
    this.crpParameterManager = crpParameterManager;
    this.centerManager = centerManager;

  }

  @Override
  public String intercept(ActionInvocation invocation) throws Exception {


    String language = APConstants.CUSTOM_LAGUAGE;
    String pathFile = APConstants.PATH_CUSTOM_FILES;
    Map<String, Object> session = invocation.getInvocationContext().getSession();
    if (session.containsKey(APConstants.CRP_LANGUAGE)) {
      language = (String) session.get(APConstants.CRP_LANGUAGE);
    }


    Locale locale = new Locale(language);
    LocalizedTextUtil.reset();
    LocalizedTextUtil.addDefaultResourceBundle(APConstants.CUSTOM_FILE);
    ServletActionContext.getContext().setLocale(locale);

    if (session.containsKey(APConstants.SESSION_CRP)) {

      if (session.containsKey(APConstants.CRP_CUSTOM_FILE)) {
        pathFile = pathFile + session.get(APConstants.CRP_CUSTOM_FILE);

        LocalizedTextUtil.addDefaultResourceBundle(pathFile);
      } else {

        LocalizedTextUtil.addDefaultResourceBundle(APConstants.CUSTOM_FILE);
      }
    }

    if (session.containsKey(APConstants.SESSION_CENTER)) {
      if (session.containsKey(APConstants.CENTER_CUSTOM_FILE)) {
        pathFile = pathFile + session.get(APConstants.CENTER_CUSTOM_FILE);

        LocalizedTextUtil.addDefaultResourceBundle(pathFile);
      } else {

        LocalizedTextUtil.addDefaultResourceBundle(APConstants.CUSTOM_FILE);
      }
    }


    Crp crp = (Crp) session.get(APConstants.SESSION_CRP);
    if (crp != null) {
      Crp loggedCrp = crpManager.getCrpById(crp.getId());

      if (this.isCrpRefresh(loggedCrp)) {
        for (CustomParameter parameter : loggedCrp.getCustomParameters()) {
          if (parameter.isActive()) {
            if (parameter.getParameter().getKey().equals(APConstants.CRP_REFRESH)) {
              session.put(parameter.getParameter().getKey(), "false");
              parameter.setValue("false");
              crpParameterManager.saveCustomParameter(parameter);
            } else {
              session.put(parameter.getParameter().getKey(), parameter.getValue());
            }

          }

        }

      }
    }


    return invocation.invoke();
  }

  public boolean isCrpRefresh(Crp crp) {
    try {
      // return Integer.parseInt(this.getSession().get(APConstants.CRP_CLOSED).toString()) == 1;
      return Boolean.parseBoolean(crpManager.getCrpById(crp.getId()).getCustomParameters().stream()
        .filter(c -> c.getParameter().getKey().equals(APConstants.CRP_REFRESH)).collect(Collectors.toList()).get(0)
        .getValue());
    } catch (Exception e) {
      return false;
    }
  }
}
