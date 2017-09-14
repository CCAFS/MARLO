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
import org.cgiar.ccafs.marlo.action.publications.PublicationAction;
import org.cgiar.ccafs.marlo.data.manager.CrpManager;
import org.cgiar.ccafs.marlo.data.manager.CustomParameterManager;
import org.cgiar.ccafs.marlo.data.manager.ParameterManager;
import org.cgiar.ccafs.marlo.data.model.Crp;
import org.cgiar.ccafs.marlo.data.model.CustomParameter;
import org.cgiar.ccafs.marlo.data.model.Parameter;
import org.cgiar.ccafs.marlo.data.model.ParameterCategoryEnum;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CrpParametersAction extends BaseAction {

  /**
   * 
   */
  private static final long serialVersionUID = 2672633110828731495L;

  private final Logger LOG = LoggerFactory.getLogger(CrpParametersAction.class);
  
  private CrpManager crpManager;
  private CustomParameterManager crpParameterManager;
  private ParameterManager parameterManager;
  private List<ParameterCategoryEnum> parametersTypes;
  private List<Crp> crps;

  @Inject
  public CrpParametersAction(APConfig config, CrpManager crpManager, CustomParameterManager crpParameterManager,
    ParameterManager parameterManager) {

    super(config);
    this.parameterManager = parameterManager;
    this.crpManager = crpManager;
    this.crpParameterManager = crpParameterManager;
  }


  public List<Crp> getCrps() {
    return crps;
  }


  public List<ParameterCategoryEnum> getParametersTypes() {
    return parametersTypes;
  }


  @Override
  public void prepare() throws Exception {

    super.prepare();
    crps = crpManager.findAll().stream().filter(c -> c.isMarlo()).collect(Collectors.toList());
    for (Crp crp : crps) {
      crp.setParameters(crp.getCustomParameters().stream().filter(c -> c.isActive()).collect(Collectors.toList()));


      if (crp.getId().equals(new Long(21))) {
        System.out.println("a");
      }
      List<Parameter> parameters = parameterManager.findAll().stream()
        .filter(c -> c.getCustomParameters().stream()
          .filter(p -> p.isActive() && p.getCrp().getId().equals(crp.getId())).collect(Collectors.toList()).isEmpty())
        .collect(Collectors.toList());

      for (Parameter parameter : parameters) {
        CustomParameter customParameter = new CustomParameter();
        customParameter.setParameter(parameter);
        customParameter.setValue(parameter.getDefaultValue());
        crp.getParameters().add(customParameter);

      }
    }

    parametersTypes = Arrays.asList(ParameterCategoryEnum.values());
    parametersTypes.sort((p1, p2) -> p1.getId().compareTo(p2.getId()) * -1);

    if (this.isHttpPost()) {
      for (Crp crp : crps) {
        if (crp.getParameters() != null) {
          crp.getParameters().clear();
        }
      }
    }
  }


  @Override
  public String save() {
    if (this.canAccessSuperAdmin()) {

      for (Crp crp : crps) {
        if (crp.getParameters() == null) {
          crp.setParameters(new ArrayList<CustomParameter>());
        }
        Crp crpDB = crpManager.getCrpById(crp.getId());
        for (CustomParameter parameter : crpDB.getCustomParameters().stream().filter(c -> c.isActive())
          .collect(Collectors.toList())) {
          if (!crp.getParameters().contains(parameter)) {
            LOG.debug("CRP Parameter key: " + parameter.getParameter().getKey());
            crpParameterManager.deleteCustomParameter(parameter.getId());
          }
        }

        for (CustomParameter parameter : crp.getParameters()) {

          if (parameter != null) {
            if (parameter.getId() == null || parameter.getId().intValue() == -1) {
              parameter.setId(null);
              parameter.setActiveSince(new Date());
              parameter.setActive(true);
              parameter.setCreatedBy(this.getCurrentUser());
              parameter.setCrp(crp);
              parameter.setParameter(parameterManager.getParameterByKey(parameter.getParameter().getKey()));
              parameter.setModificationJustification("");
              parameter.setModifiedBy(this.getCurrentUser());
              crpParameterManager.saveCustomParameter(parameter);
            } else {
              CustomParameter customParameterDB = crpParameterManager.getCustomParameterById(parameter.getId());
              parameter.setActiveSince(customParameterDB.getActiveSince());
              parameter.setActive(true);
              parameter.setCreatedBy(customParameterDB.getCreatedBy());
              parameter.setCrp(crp);
              parameter.setParameter(parameterManager.getParameterByKey(parameter.getParameter().getKey()));
              parameter.setModificationJustification("");
              parameter.setModifiedBy(this.getCurrentUser());
              crpParameterManager.saveCustomParameter(parameter);
            }

          }
        }


      }
      return SUCCESS;
    } else {
      return NOT_AUTHORIZED;
    }
  }


  public void setCrps(List<Crp> crps) {
    this.crps = crps;
  }

  public void setParametersTypes(List<ParameterCategoryEnum> parametersTypes) {
    this.parametersTypes = parametersTypes;
  }
}
