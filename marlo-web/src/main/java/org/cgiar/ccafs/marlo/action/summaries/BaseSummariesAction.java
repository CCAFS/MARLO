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

package org.cgiar.ccafs.marlo.action.summaries;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.CrpManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.model.Crp;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseSummariesAction extends BaseAction {

  private static final long serialVersionUID = 2837063045483756677L;
  private static Logger LOG = LoggerFactory.getLogger(BaseSummariesAction.class);
  // parameters
  private Crp loggedCrp;
  private int selectedYear;
  private String selectedCycle;
  private Phase selectedPhase;

  // Managers
  private CrpManager crpManager;
  private PhaseManager phaseManager;

  public BaseSummariesAction(APConfig config, CrpManager crpManager, PhaseManager phaseManager) {
    super(config);
    this.crpManager = crpManager;
    this.phaseManager = phaseManager;
  }


  public String getSelectedCycle() {
    return selectedCycle;
  }


  public Crp getLoggedCrp() {
    return loggedCrp;
  }


  public Phase getSelectedPhase() {
    return selectedPhase;
  }


  public int getSelectedYear() {
    return selectedYear;
  }


  public void setGeneralParameters() {
    try {
      this.setLoggedCrp((Crp) this.getSession().get(APConstants.SESSION_CRP));
      this.setLoggedCrp(crpManager.getCrpById(loggedCrp.getId()));
    } catch (Exception e) {
      LOG.error("Failed to get " + APConstants.SESSION_CRP + " parameter. Exception: " + e.getMessage());
    }
    // Get parameters from URL
    // Get year
    try {
      Map<String, Object> parameters = this.getParameters();
      this.setSelectedYear(Integer.parseInt((StringUtils.trim(((String[]) parameters.get(APConstants.YEAR_REQUEST))[0]))));
    } catch (Exception e) {
      LOG.warn("Failed to get " + APConstants.YEAR_REQUEST
        + " parameter. Parameter will be set as CurrentCycleYear. Exception: " + e.getMessage());
      this.setSelectedYear(this.getCurrentCycleYear());
    }
    // Get cycle
    try {
      Map<String, Object> parameters = this.getParameters();
      this.setSelectedCycle((StringUtils.trim(((String[]) parameters.get(APConstants.CYCLE))[0])));
    } catch (Exception e) {
      LOG.warn("Failed to get " + APConstants.CYCLE + " parameter. Parameter will be set as CurrentCycle. Exception: "
        + e.getMessage());
      this.setSelectedCycle(this.getCurrentCycle());
    }
    // Get phase
    this.setSelectedPhase(phaseManager.findCycle(this.getSelectedCycle(), this.getSelectedYear(), loggedCrp.getId().longValue()));
  }


  public void setSelectedCycle(String cycle) {
    this.selectedCycle = cycle;
  }


  public void setLoggedCrp(Crp loggedCrp) {
    this.loggedCrp = loggedCrp;
  }


  public void setSelectedPhase(Phase selectedPhase) {
    this.selectedPhase = selectedPhase;
  }

  public void setSelectedYear(int year) {
    this.selectedYear = year;
  }

}
