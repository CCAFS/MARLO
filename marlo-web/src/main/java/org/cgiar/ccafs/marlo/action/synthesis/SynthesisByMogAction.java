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
import org.cgiar.ccafs.marlo.data.manager.IpElementManager;
import org.cgiar.ccafs.marlo.data.manager.IpLiaisonInstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.IpProgramManager;
import org.cgiar.ccafs.marlo.data.manager.IpProjectContributionOverviewManager;
import org.cgiar.ccafs.marlo.data.manager.MogSynthesyManager;
import org.cgiar.ccafs.marlo.data.model.IpElement;
import org.cgiar.ccafs.marlo.data.model.IpLiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.IpLiaisonUser;
import org.cgiar.ccafs.marlo.data.model.IpProgram;
import org.cgiar.ccafs.marlo.data.model.IpProjectContributionOverview;
import org.cgiar.ccafs.marlo.data.model.LiaisonUser;
import org.cgiar.ccafs.marlo.data.model.MogSynthesy;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;
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
  private IpLiaisonInstitutionManager IpLiaisonInstitutionManager;


  private IpProgramManager ipProgramManager;

  private IpElementManager ipElementManager;


  private IpProjectContributionOverviewManager overviewManager;

  private MogSynthesyManager mogSynthesisManager;


  // Model for the front-end
  private List<IpLiaisonInstitution> liaisonInstitutions;

  private IpLiaisonInstitution currentLiaisonInstitution;


  private List<IpElement> mogs;

  private IpProgram program;
  private List<MogSynthesy> synthesis;
  private Long liaisonInstitutionID;

  @Inject
  public SynthesisByMogAction(APConfig config, IpLiaisonInstitutionManager IpLiaisonInstitutionManager,
    IpProgramManager ipProgramManager, IpElementManager ipElementManager,
    IpProjectContributionOverviewManager overviewManager, MogSynthesyManager mogSynthesisManager) {
    super(config);
    this.overviewManager = overviewManager;
    this.IpLiaisonInstitutionManager = IpLiaisonInstitutionManager;
    this.ipProgramManager = ipProgramManager;
    this.ipElementManager = ipElementManager;
    this.mogSynthesisManager = mogSynthesisManager;
  }

  public IpLiaisonInstitution getCurrentLiaisonInstitution() {
    return currentLiaisonInstitution;
  }

  public int getIndex(long mog, long program) {
    MogSynthesy synthe = new MogSynthesy();


    synthe.setIpElement(ipElementManager.getIpElementById(mog));
    synthe.setIpProgram(ipProgramManager.getIpProgramById(program));
    synthe.setYear(this.getCurrentCycleYear());

    int index = synthesis.indexOf(synthe);
    return index;

  }

  public Long getLiaisonInstitutionID() {
    return liaisonInstitutionID;
  }

  public List<IpLiaisonInstitution> getLiaisonInstitutions() {
    return liaisonInstitutions;
  }

  public List<IpElement> getMogs() {
    return mogs;
  }

  public IpProgram getProgram() {
    return program;
  }

  public List<IpProjectContributionOverview> getProjectOutputOverviews(long mogId) {
    List<IpProjectContributionOverview> lst = null;
    switch (program.getId().intValue()) {
      case 1:
      case 2:
      case 3:
      case 4:
        lst = overviewManager.getProjectContributionOverviewsSytnhesisGlobal(mogId, this.getCurrentCycleYear(),
          program.getId());
        break;

      default:
        lst =
          overviewManager.getProjectContributionOverviewsSytnhesis(mogId, this.getCurrentCycleYear(), program.getId());
        break;
    }
    return lst;

  }

  public List<MogSynthesy> getRegionalSynthesis(int midoutcome) {
    List<MogSynthesy> list = mogSynthesisManager.getMogSynthesisRegions(midoutcome);
    for (MogSynthesy mogSynthesis : list) {
      mogSynthesis.setIpProgram(ipProgramManager.getIpProgramById(mogSynthesis.getIpProgram().getId()));
    }
    return list;
  }

  public List<MogSynthesy> getSynthesis() {
    return synthesis;
  }

  @Override
  public String next() {
    String result = this.save();
    if (result.equals(BaseAction.SUCCESS)) {
      return BaseAction.NEXT;
    } else {
      return result;
    }
  }

  @Override
  public void prepare() throws Exception {
    super.prepare();

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

    // Get all MOGs manually

    mogs = ipElementManager.getIPElementListForSynthesis(program);
    synthesis = new ArrayList<>(mogSynthesisManager.getMogSynthesis(programID).stream()
      .filter(sy -> sy.isActive() && sy.getYear() == this.getCurrentCycleYear()).collect(Collectors.toList()));

    for (IpElement mog : mogs) {

      if (this.getIndex(mog.getId(), program.getId()) == -1) {
        MogSynthesy synthe = new MogSynthesy();

        synthe.setIpElement(mog);
        synthe.setIpProgram(program);;
        synthe.setYear(this.getCurrentCycleYear());
        synthe.setId(null);
        synthesis.add(synthe);

      }

    }
    // this.setProjectLessons(lessonManager.getProjectComponentLessonSynthesis(program.getId(), this.getActionName(),
    // this.getCurrentReportingYear(), this.getCycleName()));
  }

  @Override
  public String save() {

    for (MogSynthesy synthe : synthesis) {

      mogSynthesisManager.saveMogSynthesy(synthe);

    }

    // this.saveProjectLessonsSynthesis(program.getId());
    Collection<String> messages = this.getActionMessages();
    if (!messages.isEmpty()) {
      String validationMessage = messages.iterator().next();
      this.setActionMessages(null);
      this.addActionWarning(this.getText("saving.saved") + validationMessage);
    } else {
      this.addActionMessage("All required fields are filled. You've successfully completed your work. Thank you!");
    }

    return SUCCESS;

  }

  public void setCurrentLiaisonInstitution(IpLiaisonInstitution currentLiaisonInstitution) {
    this.currentLiaisonInstitution = currentLiaisonInstitution;
  }

  public void setLiaisonInstitutionID(Long liaisonInstitutionID) {
    this.liaisonInstitutionID = liaisonInstitutionID;
  }

  public void setLiaisonInstitutions(List<IpLiaisonInstitution> liaisonInstitutions) {
    this.liaisonInstitutions = liaisonInstitutions;
  }

  public void setMogs(List<IpElement> mogs) {
    this.mogs = mogs;
  }

  public void setProgram(IpProgram program) {
    this.program = program;
  }

  public void setSynthesis(List<MogSynthesy> synthesis) {
    this.synthesis = synthesis;
  }


}
