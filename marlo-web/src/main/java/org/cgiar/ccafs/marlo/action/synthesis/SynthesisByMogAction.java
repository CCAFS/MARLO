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
import org.cgiar.ccafs.marlo.data.manager.IpElementManager;
import org.cgiar.ccafs.marlo.data.manager.IpProgramManager;
import org.cgiar.ccafs.marlo.data.manager.IpProjectContributionOverviewManager;
import org.cgiar.ccafs.marlo.data.manager.LiaisonInstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.MogSynthesyManager;
import org.cgiar.ccafs.marlo.data.model.IpElement;
import org.cgiar.ccafs.marlo.data.model.IpProgram;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.MogSynthesy;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class SynthesisByMogAction extends BaseAction {

  private static final long serialVersionUID = 4468569031025107796L;
  private static Logger LOG = LoggerFactory.getLogger(SynthesisByMogAction.class);

  // Manager
  // private MogSynthesisValidator validator;
  private LiaisonInstitutionManager liaisonInstitutionManager;
  private IpProgramManager ipProgramManager;
  private IpElementManager ipElementManager;
  private IpProjectContributionOverviewManager overviewManager;
  // Model for the front-end
  private List<LiaisonInstitution> liaisonInstitutions;
  private LiaisonInstitution currentLiaisonInstitution;
  private List<IpElement> mogs;
  private IpProgram program;
  private List<MogSynthesy> synthesis;
  private MogSynthesyManager mogSynthesisManager;
  private int liaisonInstitutionID;

  public SynthesisByMogAction(APConfig config) {
    super(config);
    // TODO Auto-generated constructor stub
  }

}
