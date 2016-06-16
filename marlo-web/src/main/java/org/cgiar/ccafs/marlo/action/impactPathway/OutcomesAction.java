/*****************************************************************
 * This file is part of CCAFS Planning and Reporting Platform.
 * CCAFS P&R is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 * CCAFS P&R is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with CCAFS P&R. If not, see <http://www.gnu.org/licenses/>.
 *****************************************************************/

package org.cgiar.ccafs.marlo.action.impactPathway;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.SrfTargetUnitManager;
import org.cgiar.ccafs.marlo.data.model.Crp;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.CrpProgramOutcome;
import org.cgiar.ccafs.marlo.data.model.SrfTargetUnit;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import com.google.inject.Inject;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 * @author Christian Garcia - CIAT/CCAFS
 */
public class OutcomesAction extends BaseAction {

  private static final long serialVersionUID = -793652591843623397L;
  private Crp loggedCrp;
  private HashMap<Long, String> targetUnitList;
  private SrfTargetUnitManager srfTargetUnitManager;
  private List<CrpProgramOutcome> outcomes;


  @Inject
  public OutcomesAction(APConfig config, SrfTargetUnitManager srfTargetUnitManager) {
    super(config);
    this.srfTargetUnitManager = srfTargetUnitManager;
  }


  public Crp getLoggedCrp() {
    return loggedCrp;
  }


  public List<CrpProgramOutcome> getOutcomes() {
    return outcomes;
  }


  public HashMap<Long, String> getTargetUnitList() {
    return targetUnitList;
  }

  @Override
  public void prepare() throws Exception {
    loggedCrp = (Crp) this.getSession().get(APConstants.SESSION_CRP);
    outcomes = new ArrayList<CrpProgramOutcome>();
    List<CrpProgram> programs =
      loggedCrp.getCrpPrograms().stream().filter(c -> c.isActive()).collect(Collectors.toList());
    for (CrpProgram crpProgram : programs) {
      outcomes.addAll(crpProgram.getCrpProgramOutcomes());
    }
    for (CrpProgramOutcome crpProgramOutcome : outcomes) {
      crpProgramOutcome.setMilestones(
        crpProgramOutcome.getCrpMilestones().stream().filter(c -> c.isActive()).collect(Collectors.toList()));
      crpProgramOutcome.setSubIdos(
        crpProgramOutcome.getCrpOutcomeSubIdos().stream().filter(c -> c.isActive()).collect(Collectors.toList()));
    }
    targetUnitList = new HashMap<>();
    List<SrfTargetUnit> targetUnits =
      srfTargetUnitManager.findAll().stream().filter(c -> c.isActive()).collect(Collectors.toList());
    for (SrfTargetUnit srfTargetUnit : targetUnits) {
      targetUnitList.put(srfTargetUnit.getId(), srfTargetUnit.getAcronym());
    }
  }

  public void setLoggedCrp(Crp loggedCrp) {
    this.loggedCrp = loggedCrp;
  }


  public void setOutcomes(List<CrpProgramOutcome> outcomes) {
    this.outcomes = outcomes;
  }


  public void setTargetUnitList(HashMap<Long, String> targetUnitList) {
    this.targetUnitList = targetUnitList;
  }


}
