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
import org.cgiar.ccafs.marlo.data.manager.AuditLogManager;
import org.cgiar.ccafs.marlo.data.manager.CrpManager;
import org.cgiar.ccafs.marlo.data.manager.IpElementManager;
import org.cgiar.ccafs.marlo.data.manager.IpIndicatorManager;
import org.cgiar.ccafs.marlo.data.manager.IpLiaisonInstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.IpProgramManager;
import org.cgiar.ccafs.marlo.data.manager.IpProjectIndicatorManager;
import org.cgiar.ccafs.marlo.data.manager.LiaisonInstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.OutcomeSynthesyManager;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.model.Crp;
import org.cgiar.ccafs.marlo.data.model.IpElement;
import org.cgiar.ccafs.marlo.data.model.IpIndicator;
import org.cgiar.ccafs.marlo.data.model.IpLiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.IpLiaisonUser;
import org.cgiar.ccafs.marlo.data.model.IpProgram;
import org.cgiar.ccafs.marlo.data.model.IpProjectIndicator;
import org.cgiar.ccafs.marlo.data.model.OutcomeSynthesy;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.AutoSaveReader;
import org.cgiar.ccafs.marlo.validation.sythesis.SynthesisByOutcomeValidator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
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
  private IpProjectIndicatorManager ipProjectIndicatorManager;
  private String transaction;
  private AuditLogManager auditLogManager;
  private SynthesisByOutcomeValidator validator;
  // Model for the front-end
  private List<IpLiaisonInstitution> liaisonInstitutions;


  private IpLiaisonInstitution currentLiaisonInstitution;

  private List<IpElement> midOutcomes;
  private IpLiaisonInstitutionManager IpLiaisonInstitutionManager;
  private IpProgram program;

  private long liaisonInstitutionID;
  private UserManager userManager;

  @Inject
  public OutcomeSynthesisAction(APConfig config, LiaisonInstitutionManager liaisonInstitutionManager,
    IpProgramManager ipProgramManager, IpElementManager ipElementManager, CrpManager crpManager,
    IpLiaisonInstitutionManager IpLiaisonInstitutionManager, OutcomeSynthesyManager outcomeSynthesisManager,
    SynthesisByOutcomeValidator validator, UserManager userManager, AuditLogManager auditLogManager,
    IpIndicatorManager ipIndicatorManager) {
    super(config);
    this.liaisonInstitutionManager = liaisonInstitutionManager;
    this.ipProgramManager = ipProgramManager;
    this.ipElementManager = ipElementManager;
    this.outcomeSynthesisManager = outcomeSynthesisManager;
    this.ipIndicatorManager = ipIndicatorManager;
    this.IpLiaisonInstitutionManager = IpLiaisonInstitutionManager;
    this.auditLogManager = auditLogManager;
    this.validator = validator;
    this.userManager = userManager;
    this.crpManager = crpManager;


  }

  public double getAchievedExpected(long indicatorID) {

    double achievedExpected = 0;
    List<OutcomeSynthesy> outcomeSynthesies = outcomeSynthesisManager.findAll().stream()
      .filter(c -> c.getIpProgram().getId().longValue() == program.getId().longValue()
        && c.getIpIndicator().getId().longValue() == indicatorID && c.getYear() < this.getCurrentCycleYear())
      .collect(Collectors.toList());

    for (OutcomeSynthesy outcomeSynthesy : outcomeSynthesies) {
      try {
        achievedExpected = achievedExpected + outcomeSynthesy.getAchieved().doubleValue();
      } catch (Exception e) {
        achievedExpected = achievedExpected + 0;
      }
    }
    return achievedExpected;
  }

  public double getAchievedExpected(long indicatorID, long programID) {

    double achievedExpected = 0;
    List<OutcomeSynthesy> outcomeSynthesies = outcomeSynthesisManager.findAll().stream()
      .filter(c -> c.getIpProgram().getId().longValue() == programID
        && c.getIpIndicator().getId().longValue() == indicatorID && c.getYear() < this.getCurrentCycleYear())
      .collect(Collectors.toList());

    for (OutcomeSynthesy outcomeSynthesy : outcomeSynthesies) {
      try {
        achievedExpected = achievedExpected + outcomeSynthesy.getAchieved().doubleValue();
      } catch (Exception e) {
        achievedExpected = achievedExpected + 0;
      }
    }
    return achievedExpected;
  }

  private Path getAutoSaveFilePath() {
    String composedClassName = program.getClass().getSimpleName();
    String actionFile = this.getActionName().replace("/", "_");
    String autoSaveFile = program.getId() + "_" + composedClassName + "_" + actionFile + ".json";

    return Paths.get(config.getAutoSaveFolder() + autoSaveFile);
  }

  public IpLiaisonInstitution getCurrentLiaisonInstitution() {
    return currentLiaisonInstitution;
  }

  public int getIndex(long indicator, long midoutcome, long program) {
    OutcomeSynthesy synthe = new OutcomeSynthesy();
    synthe.setIpIndicator(ipIndicatorManager.getIpIndicatorById(indicator));
    synthe.setIpElement(ipElementManager.getIpElementById(midoutcome));
    synthe.setIpProgram(ipProgramManager.getIpProgramById(program));
    synthe.setYear(this.getCurrentCycleYear());


    int index = this.program.getSynthesisOutcome().indexOf(synthe);
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

  public List<IpProjectIndicator> getProjectIndicators(int year, long indicator, long midOutcome) {
    return ipProjectIndicatorManager.getProjectIndicators(year, indicator, program.getId(), midOutcome);
  }

  public List<OutcomeSynthesy> getRegionalSynthesis(long indicator, long midoutcome) {
    List<OutcomeSynthesy> list = outcomeSynthesisManager.findAll()
      .stream().filter(c -> c.getYear() == this.getCurrentCycleYear()
        && c.getIpIndicator().getId().longValue() == indicator && c.getIpProgram().isRegionalProgram())
      .collect(Collectors.toList());

    for (OutcomeSynthesy mogSynthesis : list) {
      mogSynthesis.setIpProgram(ipProgramManager.getIpProgramById(mogSynthesis.getIpProgram().getId()));
      mogSynthesis.setAchievedExpected(this.getAchievedExpected(mogSynthesis.getIpIndicator().getId().longValue(),
        mogSynthesis.getIpProgram().getId().longValue()));
    }
    return list;
  }

  public String getTransaction() {
    return transaction;
  }


  @Override
  public void prepare() throws Exception {
    loggedCrp = (Crp) this.getSession().get(APConstants.SESSION_CRP);
    loggedCrp = crpManager.getCrpById(loggedCrp.getId());

    try {
      liaisonInstitutionID =
        Long.parseLong(StringUtils.trim(this.getRequest().getParameter(APConstants.LIAISON_INSTITUTION_REQUEST_ID)));
    } catch (Exception e) {
      User user = userManager.getUser(this.getCurrentUser().getId());
      if (user.getIpLiaisonUsers() != null || !user.getIpLiaisonUsers().isEmpty()) {

        List<IpLiaisonUser> liaisonUsers = new ArrayList<>(user.getIpLiaisonUsers());

        if (!liaisonUsers.isEmpty()) {
          IpLiaisonUser liaisonUser = new IpLiaisonUser();
          liaisonUser = liaisonUsers.get(0);
          liaisonInstitutionID = liaisonUser.getIpLiaisonInstitution().getId();
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
    if (this.getRequest().getParameter(APConstants.TRANSACTION_ID) != null) {

      transaction = StringUtils.trim(this.getRequest().getParameter(APConstants.TRANSACTION_ID));
      IpProgram history = (IpProgram) auditLogManager.getHistory(transaction);

      if (history != null) {
        program = history;
        programID = program.getId();

        program.setSynthesisOutcome(new ArrayList<>(program.getOutcomeSynthesis()));

        currentLiaisonInstitution = IpLiaisonInstitutionManager.findByIpProgram(programID);
      } else {
        this.transaction = null;

        this.setTransaction("-1");
      }

    } else {


      try {
        programID = Long.valueOf(currentLiaisonInstitution.getIpProgram());
      } catch (Exception e) {
        programID = 1;
        liaisonInstitutionID = new Long(2);
        currentLiaisonInstitution = IpLiaisonInstitutionManager.getIpLiaisonInstitutionById(liaisonInstitutionID);

      }
      program = ipProgramManager.getIpProgramById(programID);
    }

    // Get Outcomes 2019 of current IPProgram
    midOutcomes = ipElementManager.getIPElementListForOutcomeSynthesis(program, APConstants.ELEMENT_TYPE_OUTCOME2019);


    if (program != null) {
      Path path = this.getAutoSaveFilePath();

      if (path.toFile().exists() && this.getCurrentUser().isAutoSave()) {

        BufferedReader reader = null;

        reader = new BufferedReader(new FileReader(path.toFile()));

        Gson gson = new GsonBuilder().create();


        JsonObject jReader = gson.fromJson(reader, JsonObject.class);
 	      reader.close();
 	

        AutoSaveReader autoSaveReader = new AutoSaveReader();

        program = (IpProgram) autoSaveReader.readFromJson(jReader);

        programID = program.getId();

        this.setDraft(true);
      
      } else {
        this.program.setSynthesisOutcome(program.getOutcomeSynthesis().stream()
          .filter(c -> c.getYear() == this.getCurrentCycleYear()).collect(Collectors.toList()));

        if (this.isLessonsActive()) {
          this.loadLessonsSynthesis(loggedCrp, program);
        }
        this.setDraft(false);
      }
    }


    for (IpElement midoutcome : midOutcomes) {
      midoutcome.setIndicators(ipIndicatorManager.getIndicatorsByElementID(midoutcome.getId().longValue()));
      for (IpIndicator indicator : midoutcome.getIndicators()) {
        long indicatorId = indicator.getId().longValue();
        if (indicator.getIpIndicator() != null) {
          indicatorId = indicator.getIpIndicator().getId().longValue();
        }
        if (this.getIndex(indicatorId, midoutcome.getId(), program.getId()) == -1) {
          OutcomeSynthesy synthe = new OutcomeSynthesy();

          synthe.setIpIndicator(ipIndicatorManager.getIpIndicatorById(indicatorId));
          synthe.setIpElement(midoutcome);
          synthe.setIpProgram(program);
          synthe.setYear(this.getCurrentCycleYear());
          synthe.setId(null);

          program.getSynthesisOutcome().add(synthe);

        }

      }
    }

    for (OutcomeSynthesy outcomeSynthesy : program.getSynthesisOutcome()) {
      outcomeSynthesy
        .setAchievedExpected(this.getAchievedExpected(outcomeSynthesy.getIpIndicator().getId().longValue()));
    }
    String params[] = {loggedCrp.getAcronym(), program.getId() + ""};
    this.setBasePermission(this.getText(Permission.SYNTHESIS_BY_MOG_BASE_PERMISSION, params));
  }

  @Override
  public String save() {
    for (OutcomeSynthesy synthe : program.getSynthesisOutcome()) {

      outcomeSynthesisManager.saveOutcomeSynthesy(synthe);

    }

    if (this.isLessonsActive()) {
      this.saveLessonsSynthesis(loggedCrp, program);
    }


    List<String> relationsName = new ArrayList<>();
    relationsName.add(APConstants.IPPROGRAM_OUTCOME_SYNTHESIS_RELATION);
    relationsName.add(APConstants.IPPROGRAM_LESSONS_RELATION);


    program = ipProgramManager.getIpProgramById(program.getId());
    program.setActiveSince(new Date());
    program.setModifiedBy(this.getCurrentUser());
    ipProgramManager.save(program, this.getActionName(), relationsName);
    Path path = this.getAutoSaveFilePath();

    if (path.toFile().exists()) {
      path.toFile().delete();
    }


    Collection<String> messages = this.getActionMessages();

    if (!this.getInvalidFields().isEmpty()) {
      this.setActionMessages(null);
      List<String> keys = new ArrayList<String>(this.getInvalidFields().keySet());
      for (String key : keys) {
        this.addActionMessage(key + ": " + this.getInvalidFields().get(key));
      }

    } else {
      this.addActionMessage("message:" + this.getText("saving.saved"));
    }
    return SUCCESS;
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

  public void setTransaction(String transaction) {
    this.transaction = transaction;
  }

  @Override
  public void validate() {
    if (save) {
      validator.validate(this, program.getSynthesisOutcome(), program, true);
    }
  }


}
