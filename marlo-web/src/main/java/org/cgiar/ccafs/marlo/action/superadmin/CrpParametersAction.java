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


package org.cgiar.ccafs.marlo.action.superadmin;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.data.manager.CustomParameterManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.ParameterManager;
import org.cgiar.ccafs.marlo.data.model.CustomParameter;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.Parameter;
import org.cgiar.ccafs.marlo.data.model.ParameterCategoryEnum;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CrpParametersAction extends BaseAction {


  /**
   * 
   */
  private static final long serialVersionUID = 2672633110828731495L;

  private final Logger LOG = LoggerFactory.getLogger(CrpParametersAction.class);

  // GlobalUnit Manager
  private GlobalUnitManager globalUnitManager;


  private CustomParameterManager crpParameterManager;

  private ParameterManager parameterManager;
  private List<ParameterCategoryEnum> parametersTypes;
  private List<GlobalUnit> crps;

  @Inject
  public CrpParametersAction(APConfig config, GlobalUnitManager globalUnitManager, CustomParameterManager crpParameterManager,
    ParameterManager parameterManager) {

    super(config);
    this.parameterManager = parameterManager;
    this.globalUnitManager = globalUnitManager;
    this.crpParameterManager = crpParameterManager;
  }

  public List<GlobalUnit> getCrps() {
    return crps;
  }

  public List<ParameterCategoryEnum> getParametersTypes() {
    return parametersTypes;
  }


  @Override
  public void prepare() throws Exception {

    super.prepare();
    crps = globalUnitManager.findAll().stream().filter(c -> c.isMarlo()).collect(Collectors.toList());
    for (GlobalUnit globalUnit : crps) {

      globalUnit.setParameters(
        globalUnit.getCustomParameters().stream().filter(c -> c.isActive()).collect(Collectors.toList()));

      List<Parameter> parameters =
        parameterManager.findAll().stream()
          .filter(c -> c.getGlobalUnitType() != null && c.getGlobalUnitType().getId() != null
            && c.getGlobalUnitType().getId() == globalUnit.getGlobalUnitType().getId()
            && c.getCustomParameters().stream()
              .filter(p -> p.isActive() && p.getCrp().getId().equals(globalUnit.getId())).collect(Collectors.toList())
              .isEmpty())
          .collect(Collectors.toList());

      for (Parameter parameter : parameters) {
        CustomParameter customParameter = new CustomParameter();
        customParameter.setParameter(parameter);
        customParameter.setValue(parameter.getDefaultValue());
        globalUnit.getParameters().add(customParameter);

      }
    }

    parametersTypes = Arrays.asList(ParameterCategoryEnum.values());
    parametersTypes.sort((p1, p2) -> p1.getId().compareTo(p2.getId()) * -1);

    if (this.isHttpPost()) {
      for (GlobalUnit crp : crps) {
        if (crp.getParameters() != null) {
          crp.getParameters().clear();
        }
      }
    }
  }


  @Override
  public String save() {
    if (this.canAccessSuperAdmin()) {

      for (GlobalUnit globalUnit : crps) {

        if (globalUnit.getParameters() == null) {
          globalUnit.setParameters(new ArrayList<CustomParameter>());
        }
        GlobalUnit globalUnitDB = globalUnitManager.getGlobalUnitById(globalUnit.getId());
        // long globalUnitTypeId = globalUnit = globalUnitDB.getGlobalUnitType().getId();
        for (CustomParameter customParameter : globalUnitDB.getCustomParameters().stream().filter(c -> c.isActive())
          .collect(Collectors.toList())) {
          if (!globalUnit.getParameters().contains(customParameter)) {
            LOG.debug("CRP Parameter key: " + customParameter.getParameter().getKey());
            crpParameterManager.deleteCustomParameter(customParameter.getId());
          }
        }

        for (CustomParameter customParameterClient : globalUnit.getParameters()) {

          if (customParameterClient != null) {
            if (customParameterClient.getId() == null || customParameterClient.getId().intValue() == -1) {
              customParameterClient.setId(null);
              customParameterClient.setCrp(globalUnitDB);

              Parameter parameterDB = parameterManager.getParameterById(customParameterClient.getParameter().getId());

              customParameterClient.setParameter(parameterDB);
              customParameterClient = crpParameterManager.saveCustomParameter(customParameterClient);
            } else {
              CustomParameter customParameterDb =
                crpParameterManager.getCustomParameterById(customParameterClient.getId());
              // Parameter parameterDB =
              // // parameterManager.getParameterByKey(parameter.getParameter().getKey(), globalUnitTypeId);
              // parameterManager.getParameterById(customParameterClient.getParameter().getId());
              // customParameterDb.setParameter(parameterDB);
              customParameterDb.setValue(customParameterClient.getValue());
              customParameterDb = crpParameterManager.saveCustomParameter(customParameterDb);
            }

          }
        }


      }
      return SUCCESS;
    } else {
      return NOT_AUTHORIZED;
    }
  }


  public void setCrps(List<GlobalUnit> crps) {
    this.crps = crps;
  }


  public void setParametersTypes(List<ParameterCategoryEnum> parametersTypes) {
    this.parametersTypes = parametersTypes;
  }
}
