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

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.config.MarloLocalizedTextProvider;
import org.cgiar.ccafs.marlo.data.manager.CustomParameterManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitProjectManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.model.CustomParameter;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.GlobalUnitProject;
import org.cgiar.ccafs.marlo.data.model.Project;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.LocalizedTextProvider;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.dispatcher.Parameter;
import org.apache.struts2.dispatcher.mapper.ActionMapping;

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
  private final GlobalUnitProjectManager globalUnitProjectManager;
  private final CustomParameterManager customParameterManager;
  private final ProjectManager projectManager;
  private final GlobalUnitManager globalUnitManager;
  private Map<String, Parameter> parameters;


  @Inject
  public InternationalitazionFileInterceptor(GlobalUnitManager crpManager,
    CustomParameterManager customParameterManager, LocalizedTextProvider localizedTextProvider,
    ProjectManager projectManager, GlobalUnitProjectManager globalUnitProjectManager,
    GlobalUnitManager globalUnitManager) {
    this.crpManager = crpManager;
    this.customParameterManager = customParameterManager;
    this.localizedTextProvider = localizedTextProvider;
    this.projectManager = projectManager;
    this.globalUnitProjectManager = globalUnitProjectManager;
    this.globalUnitManager = globalUnitManager;
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

    if (!session.isEmpty()) {
      if (session.containsKey(APConstants.SESSION_CRP)) {

        BaseAction baseAction = (BaseAction) invocation.getAction();
        parameters = invocation.getInvocationContext().getParameters();
        GlobalUnit globalUnit = (GlobalUnit) session.get(APConstants.SESSION_CRP);
        try {
          String projectParameter = parameters.get(APConstants.PROJECT_REQUEST_ID).getMultipleValues()[0];
          long projectId = Long.parseLong(projectParameter);
          Project project = projectManager.getProjectById(projectId);
          if (project != null && globalUnit.getGlobalUnitType().getId() == 4) {
            GlobalUnitProject globalUnitProject = globalUnitProjectManager.findByProjectId(project.getId());
            // GlobalUnit globalUnit = globalUnitManager.getGlobalUnitById(globalUnitProject.getGlobalUnit().getId());
            pathFile = pathFile + globalUnitProject.getGlobalUnit().getAcronym().toLowerCase();
            this.localizedTextProvider.addDefaultResourceBundle(pathFile);
          } else {
            if (session.containsKey(APConstants.CRP_CUSTOM_FILE)) {
              pathFile = pathFile + session.get(APConstants.CRP_CUSTOM_FILE);
              this.localizedTextProvider.addDefaultResourceBundle(pathFile);
            } else {
              this.localizedTextProvider.addDefaultResourceBundle(APConstants.CUSTOM_FILE);
            }
          }
        } catch (Exception e) {
          if (session.containsKey(APConstants.CRP_CUSTOM_FILE)) {
            pathFile = pathFile + session.get(APConstants.CRP_CUSTOM_FILE);
            this.localizedTextProvider.addDefaultResourceBundle(pathFile);
          } else {
            this.localizedTextProvider.addDefaultResourceBundle(APConstants.CUSTOM_FILE);
          }
        }
      }
    } else {
      if (invocation.getAction() != null && (invocation.getAction() instanceof BaseAction)) {
        BaseAction action = (BaseAction) invocation.getAction();
        if (action.isPublicRoute()) {
          ActionMapping actionMapping = (ActionMapping) invocation.getInvocationContext().get("struts.actionMapping");
          if (actionMapping != null) {
            String[] names = StringUtils.split(actionMapping.getName(), "/");
            if (names != null && names.length > 0) {
              // in theory the crp is always the first parameter in the url
              String crpAcronym = names[0];
              GlobalUnit globalUnit = globalUnitManager.findGlobalUnitByAcronym(crpAcronym);
              if (globalUnit != null) {
                pathFile = pathFile + StringUtils.lowerCase(crpAcronym);
                this.localizedTextProvider.addDefaultResourceBundle(pathFile);
              }
            }
          }
        }
      }
    }

    GlobalUnit crp = (GlobalUnit) session.get(APConstants.SESSION_CRP);
    if (crp != null && crp.getId() != null) {
      GlobalUnit loggedCrp = crpManager.getGlobalUnitById(crp.getId());

      if (this.isCrpRefresh(loggedCrp)) {

        List<CustomParameter> customParameters =
          customParameterManager.getAllCustomParametersByGlobalUnitId(loggedCrp.getId());

        for (CustomParameter parameter : customParameters) {
          if (parameter.isActive()) {
            if (parameter.getParameter().getKey().equals(APConstants.CRP_REFRESH)) {
              session.put(parameter.getParameter().getKey(), "false");
              parameter.setValue("false");
              customParameterManager.saveCustomParameter(parameter);
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

      // Run a query to get Parameter object with value crp_refresh and check if it true or false.
      CustomParameter crpRefresh =
        customParameterManager.getCustomParameterByParameterKeyAndGlobalUnitId(APConstants.CRP_REFRESH, crp.getId());

      return Boolean.parseBoolean(crpRefresh.getValue());

    } catch (Exception e) {
      return false;
    }
  }
}