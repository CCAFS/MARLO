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

package org.cgiar.ccafs.marlo.action.synthesis;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.data.manager.CrpManager;
import org.cgiar.ccafs.marlo.data.manager.IpLiaisonInstitutionManager;
import org.cgiar.ccafs.marlo.utils.APConfig;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class CrpIndicatorsAction extends BaseAction {

  private static final long serialVersionUID = -1417643537265271428L;

  private CrpManager crpManager;
  private IpLiaisonInstitutionManager liaisonInstitutionManager;
  // private IndicatorReportManager indicatorsReportManager;

  public CrpIndicatorsAction(APConfig config, CrpManager crpManager) {
    super(config);
    crpManager = crpManager;
  }

}
