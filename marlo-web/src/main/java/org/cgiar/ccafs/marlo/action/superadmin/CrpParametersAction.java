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
import org.cgiar.ccafs.marlo.data.manager.CrpManager;
import org.cgiar.ccafs.marlo.data.model.Crp;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.List;
import java.util.stream.Collectors;

public class CrpParametersAction extends BaseAction {

  /**
   * 
   */
  private static final long serialVersionUID = 2672633110828731495L;


  private CrpManager crpManager;

  private List<Crp> crps;

  public CrpParametersAction(APConfig config, CrpManager crpManager) {

    super(config);
    this.crpManager = crpManager;
  }


  public List<Crp> getCrps() {
    return crps;
  }


  @Override
  public void prepare() throws Exception {

    super.prepare();
    crps = crpManager.findAll().stream().filter(c -> c.isMarlo()).collect(Collectors.toList());
    for (Crp crp : crps) {
      crp.setParameters(crp.getCrpParameters().stream().filter(c -> c.isActive()).collect(Collectors.toList()));
    }
  }


  public void setCrps(List<Crp> crps) {
    this.crps = crps;
  }
}
