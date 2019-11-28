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

package org.cgiar.ccafs.marlo.action.crp.admin;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.CustomParameterManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.model.CustomParameter;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

public class CrpPhasesAction extends BaseAction {

  /**
   * 
   */
  private static final long serialVersionUID = -8565941816588436403L;

  private List<Phase> phasesAction;

  private PhaseManager phaseManager;
  private CustomParameterManager crpParameterManager;
  private GlobalUnit loggedCrp;
  private Long defaultPhaseID;


  @Inject
  public CrpPhasesAction(APConfig config, PhaseManager phaseManager, CustomParameterManager crpParameterManager) {
    super(config);
    this.phaseManager = phaseManager;
    this.crpParameterManager = crpParameterManager;
  }


  public Long getDefaultPhaseID() {
    return defaultPhaseID;
  }


  public PhaseManager getPhaseManager() {
    return phaseManager;
  }


  public List<Phase> getPhasesAction() {
    return phasesAction;
  }

  @Override
  public void prepare() throws Exception {
    loggedCrp = (GlobalUnit) this.getSession().get(APConstants.SESSION_CRP);

    phasesAction = phaseManager.findAll().stream()
      .filter(c -> c.getCrp().getId().longValue() == this.getCrpID().longValue()).collect(Collectors.toList());
    phasesAction.sort((p1, p2) -> p1.getStartDate().compareTo(p2.getStartDate()));
    String params[] = {loggedCrp.getAcronym()};
    this.setBasePermission(this.getText(Permission.CRP_ADMIN_BASE_PERMISSION, params));
    for (CustomParameter customParameter : loggedCrp.getCustomParameters().stream().filter(c -> c.isActive())
      .collect(Collectors.toList())) {
      if (customParameter.getParameter().getKey().equals(APConstants.CURRENT_PHASE_PARAM)) {
        CustomParameter customParameterDb = crpParameterManager.getCustomParameterById(customParameter.getId());
        defaultPhaseID = customParameterDb.getValue() != null ? Long.parseLong(customParameterDb.getValue()) : null;
      }
    }
    if (this.isHttpPost()) {
      phasesAction.clear();
    }
  }

  @Override
  public String save() {
    if (this.hasPermission("*")) {
      for (Phase phase : phasesAction) {
        if (phase.getNext().getId() == null) {
          phase.setNext(null);
        }
        phaseManager.savePhase(phase);
        if (phase.getId().equals(defaultPhaseID)) {
          System.out.println(" phase ID " + defaultPhaseID);
          for (CustomParameter customParameter : loggedCrp.getCustomParameters().stream().filter(c -> c.isActive())
            .collect(Collectors.toList())) {
            if (customParameter.getParameter().getKey().equals(APConstants.CURRENT_PHASE_PARAM)) {
              if (!customParameter.getValue().equals(defaultPhaseID)) {
                CustomParameter customParameterDb = crpParameterManager.getCustomParameterById(customParameter.getId());
                customParameterDb.setValue(defaultPhaseID != null ? String.valueOf(defaultPhaseID) : null);
                customParameterDb = crpParameterManager.saveCustomParameter(customParameterDb);
              }
            }
          }
        }


      }

      this.getSession().remove(APConstants.PHASES);
      this.getSession().remove(APConstants.PHASES_IMPACT);
      this.getSession().remove(APConstants.ALL_PHASES);
      this.getSession().remove(APConstants.CURRENT_PHASE);

      Collection<String> messages = this.getActionMessages();
      if (!messages.isEmpty()) {
        String validationMessage = messages.iterator().next();
        this.setActionMessages(null);
        this.addActionWarning(this.getText("saving.saved") + validationMessage);
      } else {
        this.addActionMessage("message:" + this.getText("saving.saved"));
      }
      messages = this.getActionMessages();

      return SUCCESS;
    } else {
      return NOT_AUTHORIZED;
    }

  }

  public void setDefaultPhaseID(Long defaultPhaseID) {
    this.defaultPhaseID = defaultPhaseID;
  }


  public void setPhaseManager(PhaseManager phaseManager) {
    this.phaseManager = phaseManager;
  }


  public void setPhasesAction(List<Phase> phases) {
    this.phasesAction = phases;
  }


}
