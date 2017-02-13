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
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.CrpManager;
import org.cgiar.ccafs.marlo.data.manager.IpElementManager;
import org.cgiar.ccafs.marlo.data.manager.IpIndicatorManager;
import org.cgiar.ccafs.marlo.data.manager.IpLiaisonInstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.IpProgramManager;
import org.cgiar.ccafs.marlo.data.manager.LiaisonInstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.OutcomeSynthesyManager;
import org.cgiar.ccafs.marlo.data.model.Crp;
import org.cgiar.ccafs.marlo.data.model.IpElement;
import org.cgiar.ccafs.marlo.data.model.IpIndicator;
import org.cgiar.ccafs.marlo.data.model.IpLiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.IpLiaisonUser;
import org.cgiar.ccafs.marlo.data.model.IpProgram;
import org.cgiar.ccafs.marlo.data.model.LiaisonUser;
import org.cgiar.ccafs.marlo.data.model.OutcomeSynthesy;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 * @author Christian Garcia- CIAT/CCAFS
 */
public class OutcomeSynthesisAction extends BaseAction {


  private static final long serialVersionUID = -38851756215381752L;
  private Crp loggedCrp;
  private CrpManager crpManager;

  // private OutcomeSynthesisValidator validator;
  private LiaisonInstitutionManager liaisonInstitutionManager;
  private IpProgramManager ipProgramManager;
  private IpElementManager ipElementManager;
  private OutcomeSynthesyManager outcomeSynthesisManager;
  private IpIndicatorManager ipIndicatorManager;

  // Model for the front-end
  private List<IpLiaisonInstitution> liaisonInstitutions;
  private IpLiaisonInstitution currentLiaisonInstitution;
  private List<IpElement> midOutcomes;
  private List<OutcomeSynthesy> synthesis;
  private IpLiaisonInstitutionManager IpLiaisonInstitutionManager;

  private IpProgram program;
  private long liaisonInstitutionID;

  @Inject
  public OutcomeSynthesisAction(APConfig config, LiaisonInstitutionManager liaisonInstitutionManager,
    IpProgramManager ipProgramManager, IpElementManager ipElementManager, CrpManager crpManager,
    IpLiaisonInstitutionManager IpLiaisonInstitutionManager, OutcomeSynthesyManager outcomeSynthesisManager,
    IpIndicatorManager ipIndicatorManager) {
    super(config);
    this.liaisonInstitutionManager = liaisonInstitutionManager;
    this.ipProgramManager = ipProgramManager;
    this.ipElementManager = ipElementManager;
    this.outcomeSynthesisManager = outcomeSynthesisManager;
    this.ipIndicatorManager = ipIndicatorManager;
    this.IpLiaisonInstitutionManager = IpLiaisonInstitutionManager;
    this.crpManager = crpManager;


  }

  public IpLiaisonInstitution getCurrentLiaisonInstitution() {
    return currentLiaisonInstitution;
  }

  public int getIndex(long indicator, long midoutcome, long program) {
    OutcomeSynthesy synthe = new OutcomeSynthesy();
    synthe.setIpIndicator(ipIndicatorManager.getIpIndicatorById(indicator));
    synthe.setIpElement(ipElementManager.getIpElementById(midoutcome));
    synthe.setIpProgram(ipProgramManager.getIpProgramById(program));

    int index = synthesis.indexOf(synthe);
    return index;

  }

  public long getLiaisonInstitutionID() {
    return liaisonInstitutionID;
  }


  public List<IpLiaisonInstitution> getLiaisonInstitutions() {
    return liaisonInstitutions;
  }

  public List<IpElement> getMidOutcomes() {
    return midOutcomes;
  }

  public IpProgram getProgram() {
    return program;
  }

  public List<IpIndicator> getProjectIndicators(int year, long indicator, long midOutcome) {
    return ipIndicatorManager.getProjectIndicators(year, indicator, program.getId(), midOutcome);
  }

  public List<OutcomeSynthesy> getRegionalSynthesis(long indicator, long midoutcome) {
    List<OutcomeSynthesy> list = outcomeSynthesisManager.findAll().stream()
      .filter(c -> c.getIpProgram().getId().longValue() == program.getId().longValue()
        && c.getYear() == this.getCurrentCycleYear() && c.getIpElement().getId().longValue() == midoutcome
        && c.getIpIndicator().getId().longValue() == midoutcome && c.getIpProgram().isRegionalProgram())
      .collect(Collectors.toList());

    for (OutcomeSynthesy mogSynthesis : list) {
      mogSynthesis.setIpProgram(ipProgramManager.getIpProgramById(mogSynthesis.getIpProgram().getId()));
    }
    return list;
  }

  public List<OutcomeSynthesy> getSynthesis() {
    return synthesis;
  }

  @Override
  public void prepare() throws Exception {
    loggedCrp = (Crp) this.getSession().get(APConstants.SESSION_CRP);
    loggedCrp = crpManager.getCrpById(loggedCrp.getId());

    try {
      liaisonInstitutionID =
        Long.parseLong(StringUtils.trim(this.getRequest().getParameter(APConstants.LIAISON_INSTITUTION_REQUEST_ID)));
    } catch (Exception e) {
      if (this.getCurrentUser().getIpLiaisonUsers() != null || !this.getCurrentUser().getIpLiaisonUsers().isEmpty()) {

        List<IpLiaisonUser> liaisonUsers = new ArrayList<>(this.getCurrentUser().getIpLiaisonUsers());

        if (!liaisonUsers.isEmpty()) {
          LiaisonUser liaisonUser = new LiaisonUser();
          liaisonUser = new ArrayList<>(this.getCurrentUser().getLiasonsUsers()).get(0);
          liaisonInstitutionID = liaisonUser.getLiaisonInstitution().getId();
        } else {
          liaisonInstitutionID = new Long(7);
        }


      } else {
        liaisonInstitutionID = new Long(7);
      }
    }

    // Get the list of liaison institutions.
    liaisonInstitutions = IpLiaisonInstitutionManager.getLiaisonInstitutionSynthesisByMog();

    Collections.sort(liaisonInstitutions, (li1, li2) -> li1.getId().compareTo(li2.getId()));

    // Get currentLiaisonInstitution
    currentLiaisonInstitution = IpLiaisonInstitutionManager.getIpLiaisonInstitutionById(liaisonInstitutionID);


    long programID;
    try {
      programID = Long.valueOf(currentLiaisonInstitution.getIpProgram());
    } catch (Exception e) {
      programID = 1;
      liaisonInstitutionID = new Long(2);
      currentLiaisonInstitution = IpLiaisonInstitutionManager.getIpLiaisonInstitutionById(liaisonInstitutionID);

    }
    program = ipProgramManager.getIpProgramById(programID);


    // Get Outcomes 2019 of current IPProgram
    midOutcomes = ipElementManager.getIPElementListForOutcomeSynthesis(program, APConstants.ELEMENT_TYPE_OUTCOME2019);
    synthesis = outcomeSynthesisManager.findAll().stream()
      .filter(c -> c.getIpProgram().getId().longValue() == program.getId().longValue()
        && c.getYear() == this.getCurrentCycleYear())
      .collect(Collectors.toList());

    for (IpElement midoutcome : midOutcomes) {
      midoutcome.setIndicators(ipIndicatorManager.getIndicatorsByElementID(midoutcome.getId().longValue()));
      for (IpIndicator indicator : midoutcome.getIndicators()) {
        long indicatorId = indicator.getId().longValue();
        if (indicator.getIpIndicator() != null) {
          indicatorId = indicator.getIpIndicator().getId();
        }
        if (this.getIndex(indicatorId, midoutcome.getId(), program.getId()) == -1) {
          OutcomeSynthesy synthe = new OutcomeSynthesy();
          synthe.setIpIndicator(ipIndicatorManager.getIpIndicatorById(indicatorId));
          synthe.setIpElement(midoutcome);
          synthe.setIpProgram(program);
          synthe.setYear(this.getCurrentCycleYear());
          synthe.setId(null);
          synthesis.add(synthe);

        }

      }
    }
  }

  public void setCurrentLiaisonInstitution(IpLiaisonInstitution currentLiaisonInstitution) {
    this.currentLiaisonInstitution = currentLiaisonInstitution;
  }

  public void setLiaisonInstitutionID(long liaisonInstitutionID) {
    this.liaisonInstitutionID = liaisonInstitutionID;
  }

  public void setLiaisonInstitutions(List<IpLiaisonInstitution> liaisonInstitutions) {
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
