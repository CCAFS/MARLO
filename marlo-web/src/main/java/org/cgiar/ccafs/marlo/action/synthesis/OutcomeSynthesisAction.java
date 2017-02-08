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
import org.cgiar.ccafs.marlo.data.manager.IpIndicatorManager;
import org.cgiar.ccafs.marlo.data.manager.IpProgramManager;
import org.cgiar.ccafs.marlo.data.manager.LiaisonInstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.OutcomeSynthesyManager;
import org.cgiar.ccafs.marlo.data.model.IpElement;
import org.cgiar.ccafs.marlo.data.model.IpProgram;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.OutcomeSynthesy;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.List;

import com.google.inject.Inject;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class OutcomeSynthesisAction extends BaseAction {


  private static final long serialVersionUID = -38851756215381752L;

  // private OutcomeSynthesisValidator validator;
  private LiaisonInstitutionManager liaisonInstitutionManager;
  private IpProgramManager ipProgramManager;
  private IpElementManager ipElementManager;
  private OutcomeSynthesyManager outcomeSynthesisManager;
  private IpIndicatorManager ipIndicatorManager;

  // Model for the front-end
  private List<LiaisonInstitution> liaisonInstitutions;
  private LiaisonInstitution currentLiaisonInstitution;
  private List<IpElement> midOutcomes;
  private List<OutcomeSynthesy> synthesis;

  private IpProgram program;
  private long liaisonInstitutionID;

  @Inject
  public OutcomeSynthesisAction(APConfig config, LiaisonInstitutionManager liaisonInstitutionManager,
    IpProgramManager ipProgramManager, IpElementManager ipElementManager,
    OutcomeSynthesyManager outcomeSynthesisManager, IpIndicatorManager ipIndicatorManager) {
    super(config);
    this.liaisonInstitutionManager = liaisonInstitutionManager;
    this.ipProgramManager = ipProgramManager;
    this.ipElementManager = ipElementManager;
    this.outcomeSynthesisManager = outcomeSynthesisManager;
    this.ipIndicatorManager = ipIndicatorManager;


  }

  public LiaisonInstitution getCurrentLiaisonInstitution() {
    return currentLiaisonInstitution;
  }

  public long getLiaisonInstitutionID() {
    return liaisonInstitutionID;
  }

  public List<LiaisonInstitution> getLiaisonInstitutions() {
    return liaisonInstitutions;
  }

  public List<IpElement> getMidOutcomes() {
    return midOutcomes;
  }

  public IpProgram getProgram() {
    return program;
  }

  public List<OutcomeSynthesy> getSynthesis() {
    return synthesis;
  }

  public void setCurrentLiaisonInstitution(LiaisonInstitution currentLiaisonInstitution) {
    this.currentLiaisonInstitution = currentLiaisonInstitution;
  }

  public void setLiaisonInstitutionID(long liaisonInstitutionID) {
    this.liaisonInstitutionID = liaisonInstitutionID;
  }

  public void setLiaisonInstitutions(List<LiaisonInstitution> liaisonInstitutions) {
    this.liaisonInstitutions = liaisonInstitutions;
  }

  public void setMidOutcomes(List<IpElement> midOutcomes) {
    this.midOutcomes = midOutcomes;
  }

  public void setProgram(IpProgram program) {
    this.program = program;
  }

  public void setSynthesis(List<OutcomeSynthesy> synthesis) {
    this.synthesis = synthesis;
  }

}
