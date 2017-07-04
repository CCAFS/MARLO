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


package org.cgiar.ccafs.marlo.action.json.global;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.Map;

import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;

public class ChangePhaseAction extends BaseAction {

  /**
   * 
   */
  private static final long serialVersionUID = 20368948176911683L;

  /**
   * 
   */

  private Long phaseId;
  private PhaseManager phaseManager;

  @Inject
  public ChangePhaseAction(APConfig config, PhaseManager phaseManager) {
    super(config);
    this.phaseManager = phaseManager;
  }

  @Override
  public String execute() throws Exception {
    Phase phase = phaseManager.getPhaseById(phaseId);
    this.getSession().put(APConstants.CURRENT_PHASE, phase);
    return SUCCESS;
  }


  @Override
  public void prepare() throws Exception {
    Map<String, Object> parameters = this.getParameters();
    phaseId = Long.parseLong(StringUtils.trim(((String[]) parameters.get(APConstants.PHASE_ID))[0]));
  }


}
