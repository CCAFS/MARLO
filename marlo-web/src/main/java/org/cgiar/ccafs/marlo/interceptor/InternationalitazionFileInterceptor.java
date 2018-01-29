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
import org.cgiar.ccafs.marlo.config.MarloLocalizedTextProvider;
import org.cgiar.ccafs.marlo.data.manager.CustomParameterManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.model.CustomParameter;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;

import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.LocalizedTextProvider;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import org.apache.struts2.ServletActionContext;

public class InternationalitazionFileInterceptor extends AbstractInterceptor {

  /**
   * For a specific file to work for a CRP, a parameter must be created
   * Crp_custom_file in the Database, the value of that parameter should not have the .properties
   * 
   * @author Christian David Garcia Oviedo
   */
  private static final long serialVersionUID = -3807232981762261100L;
  private final GlobalUnitManager crpManager;

  private final LocalizedTextProvider localizedTextProvider;

  private CustomParameterManager crpParameterManager;

  @Inject
  public InternationalitazionFileInterceptor(GlobalUnitManager crpManager, CustomParameterManager crpParameterManager,
    LocalizedTextProvider localizedTextProvider) {
    this.crpManager = crpManager;
    this.crpParameterManager = crpParameterManager;
    this.localizedTextProvider = localizedTextProvider;

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

    /**
     * This is yuck to have to cast the interface to a custom implementation but I can't see a nice way to remove custom
     * properties bundles (the reason we are doing this is the scenario where a user navigates between CRPs. If we don't
     * reset the properties bundles then the user will potentially get the properties loaded from another CRP if that
     * property has not been defined by that CRP or Center.
     */
    ((MarloLocalizedTextProvider) this.localizedTextProvider).resetResourceBundles();

    this.localizedTextProvider.addDefaultResourceBundle(APConstants.CUSTOM_FILE);


    ServletActionContext.getContext().setLocale(locale);

    if (session.containsKey(APConstants.SESSION_CRP)) {

      if (session.containsKey(APConstants.CRP_CUSTOM_FILE)) {
        pathFile = pathFile + session.get(APConstants.CRP_CUSTOM_FILE);
        this.localizedTextProvider.addDefaultResourceBundle(pathFile);
      } else if (session.containsKey(APConstants.CENTER_CUSTOM_FILE)) {
        pathFile = pathFile + session.get(APConstants.CENTER_CUSTOM_FILE);
        this.localizedTextProvider.addDefaultResourceBundle(pathFile);
      } else {

        this.localizedTextProvider.addDefaultResourceBundle(APConstants.CUSTOM_FILE);
      }
    }

    GlobalUnit crp = (GlobalUnit) session.get(APConstants.SESSION_CRP);
    if (crp != null) {
      GlobalUnit loggedCrp = crpManager.getGlobalUnitById(crp.getId());

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
        session.remove(APConstants.CURRENT_PHASE);
        session.remove(APConstants.PHASES);
        session.remove(APConstants.PHASES);
        session.remove(APConstants.ALL_PHASES);
      }
    }


    return invocation.invoke();
  }

  public boolean isCrpRefresh(GlobalUnit crp) {
    try {
      // return Integer.parseInt(this.getSession().get(APConstants.CRP_CLOSED).toString()) == 1;
      return Boolean.parseBoolean(crpManager.getGlobalUnitById(crp.getId()).getCustomParameters().stream()
        .filter(c -> c.getParameter().getKey().equals(APConstants.CRP_REFRESH)).collect(Collectors.toList()).get(0)
        .getValue());
    } catch (Exception e) {
      return false;
    }
  }
}