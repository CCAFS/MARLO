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
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.model.Crp;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.google.inject.Inject;

public class CrpPhasesAction extends BaseAction {

  /**
   * 
   */
  private static final long serialVersionUID = -8565941816588436403L;

  private List<Phase> phasesAction;

  private PhaseManager phaseManager;
  private Crp loggedCrp;


  @Inject
  public CrpPhasesAction(APConfig config, PhaseManager phaseManager) {
    super(config);
    this.phaseManager = phaseManager;
  }


  public PhaseManager getPhaseManager() {
    return phaseManager;
  }


  public List<Phase> getPhasesAction() {
    return phasesAction;
  }


  @Override
  public void prepare() throws Exception {
    loggedCrp = (Crp) this.getSession().get(APConstants.SESSION_CRP);

    phasesAction = phaseManager.findAll().stream()
      .filter(c -> c.getCrp().getId().longValue() == this.getCrpID().longValue()).collect(Collectors.toList());
    phasesAction.sort((p1, p2) -> p1.getStartDate().compareTo(p2.getStartDate()));
    String params[] = {loggedCrp.getAcronym()};
    this.setBasePermission(this.getText(Permission.CRP_ADMIN_BASE_PERMISSION, params));
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
      }

      this.getSession().remove(APConstants.PHASES);
      this.getSession().remove(APConstants.PHASES_IMPACT);
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

  public void setPhaseManager(PhaseManager phaseManager) {
    this.phaseManager = phaseManager;
  }

  public void setPhasesAction(List<Phase> phases) {
    this.phasesAction = phases;
  }
}
