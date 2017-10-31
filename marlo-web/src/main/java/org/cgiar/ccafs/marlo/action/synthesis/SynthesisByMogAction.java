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
import org.cgiar.ccafs.marlo.data.manager.IpLiaisonInstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.IpProgramManager;
import org.cgiar.ccafs.marlo.data.manager.IpProjectContributionOverviewManager;
import org.cgiar.ccafs.marlo.data.manager.MogSynthesyManager;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.model.Crp;
import org.cgiar.ccafs.marlo.data.model.IpElement;
import org.cgiar.ccafs.marlo.data.model.IpLiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.IpLiaisonUser;
import org.cgiar.ccafs.marlo.data.model.IpProgram;
import org.cgiar.ccafs.marlo.data.model.IpProjectContributionOverview;
import org.cgiar.ccafs.marlo.data.model.MogSynthesy;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.AutoSaveReader;
import org.cgiar.ccafs.marlo.validation.sythesis.SynthesisByMogValidator;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class SynthesisByMogAction extends BaseAction {


  private static final long serialVersionUID = 4468569031025107796L;


  private static Logger LOG = LoggerFactory.getLogger(SynthesisByMogAction.class);

  // Manager
  private IpLiaisonInstitutionManager IpLiaisonInstitutionManager;


  private IpProgramManager ipProgramManager;

  private IpElementManager ipElementManager;


  private IpProjectContributionOverviewManager overviewManager;


  private MogSynthesyManager mogSynthesisManager;


  private CrpManager crpManager;

  private AuditLogManager auditLogManager;

  // Model for the front-end
  private List<IpLiaisonInstitution> liaisonInstitutions;

  private IpLiaisonInstitution currentLiaisonInstitution;


  private List<IpElement> mogs;

  private IpProgram program;

  private UserManager userManager;
  private List<MogSynthesy> synthesis;

  private Long liaisonInstitutionID;


  private Crp loggedCrp;

  private SynthesisByMogValidator validator;
  private String transaction;

  @Inject
  public SynthesisByMogAction(APConfig config, IpLiaisonInstitutionManager IpLiaisonInstitutionManager,
    UserManager userManager, IpProgramManager ipProgramManager, IpElementManager ipElementManager,
    IpProjectContributionOverviewManager overviewManager, MogSynthesyManager mogSynthesisManager, CrpManager crpManager,
    AuditLogManager auditLogManager, SynthesisByMogValidator validator) {
    super(config);
    this.overviewManager = overviewManager;
    this.IpLiaisonInstitutionManager = IpLiaisonInstitutionManager;
    this.userManager = userManager;
    this.ipProgramManager = ipProgramManager;
    this.ipElementManager = ipElementManager;
    this.mogSynthesisManager = mogSynthesisManager;
    this.crpManager = crpManager;
    this.auditLogManager = auditLogManager;
    this.validator = validator;
  }

  @Override
  public String cancel() {

    Path path = this.getAutoSaveFilePath();

    if (path.toFile().exists()) {

      boolean fileDeleted = path.toFile().delete();
    }

    this.setDraft(false);
    Collection<String> messages = this.getActionMessages();
    if (!messages.isEmpty()) {
      String validationMessage = messages.iterator().next();
      this.setActionMessages(null);
      this.addActionMessage("draft:" + this.getText("cancel.autoSave"));
    } else {
      this.addActionMessage("draft:" + this.getText("cancel.autoSave"));
    }
    messages = this.getActionMessages();

    return SUCCESS;
  }

  private Path getAutoSaveFilePath() {
    String composedClassName = program.getClass().getSimpleName();
    String actionFile = this.getActionName().replace("/", "_");
    String autoSaveFile =
      program.getId() + "_" + composedClassName + "_" + loggedCrp.getAcronym() + "_" + actionFile + ".json";

    return Paths.get(config.getAutoSaveFolder() + autoSaveFile);
  }

  public IpLiaisonInstitution getCurrentLiaisonInstitution() {
    return currentLiaisonInstitution;
  }

  public int getIndex(long mog, long program) {
    MogSynthesy synthe = new MogSynthesy();


    synthe.setIpElement(ipElementManager.getIpElementById(mog));
    synthe.setIpProgram(ipProgramManager.getIpProgramById(program));
    synthe.setYear(this.getCurrentCycleYear());

    int index = this.program.getSynthesis().indexOf(synthe);
    return index;

  }

  public Long getLiaisonInstitutionID() {
    return liaisonInstitutionID;
  }

  public List<IpLiaisonInstitution> getLiaisonInstitutions() {
    return liaisonInstitutions;
  }

  public Crp getLoggedCrp() {
    return loggedCrp;
  }

  public List<IpElement> getMogs() {
    return mogs;
  }

  public IpProgram getProgram() {
    return program;
  }

  public List<IpProjectContributionOverview> getProjectOutputOverviews(long mogId) {
    List<IpProjectContributionOverview> lst = null;
    switch (program.getIpProgramType().getId().intValue()) {
      case 1:
      case 2:
      case 3:
      case 4:
        lst = overviewManager.getProjectContributionOverviewsSytnhesisGlobal(mogId, this.getCurrentCycleYear(),
          program.getId());
        break;

      default:
        lst = overviewManager.getProjectContributionOverviewsSytnhesis(mogId, this.getCurrentCycleYear(),
          program.getCrpProgram().getId());
        break;
    }
    return lst;

  }

  public List<MogSynthesy> getRegionalSynthesis(int midoutcome) {
    List<MogSynthesy> list = mogSynthesisManager.getMogSynthesisRegions(midoutcome, this.getCurrentCycleYear());
    for (MogSynthesy mogSynthesis : list) {
      mogSynthesis.setIpProgram(ipProgramManager.getIpProgramById(mogSynthesis.getIpProgram().getId()));
    }
    return list;
  }

  public List<MogSynthesy> getSynthesis() {
    return synthesis;
  }

  public String getTransaction() {
    return transaction;
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

    loggedCrp = (Crp) this.getSession().get(APConstants.SESSION_CRP);
    loggedCrp = crpManager.getCrpById(loggedCrp.getId());

    // Get the list of liaison institutions.
    liaisonInstitutions = IpLiaisonInstitutionManager.getLiaisonInstitutionSynthesisByMog();

    Collections.sort(liaisonInstitutions, (li1, li2) -> li1.getId().compareTo(li2.getId()));

    long programID = -1;

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

    // Get currentLiaisonInstitution
    currentLiaisonInstitution = IpLiaisonInstitutionManager.getIpLiaisonInstitutionById(liaisonInstitutionID);


    if (this.getRequest().getParameter(APConstants.TRANSACTION_ID) != null) {

      transaction = StringUtils.trim(this.getRequest().getParameter(APConstants.TRANSACTION_ID));
      IpProgram history = (IpProgram) auditLogManager.getHistory(transaction);

      if (history != null) {
        program = history;
        programID = program.getId();
        program.setModifiedBy(userManager.getUser(program.getModifiedBy().getId()));
        program.setSynthesis(new ArrayList<>(program.getMogSynthesis()));

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

      if (this.isLessonsActive()) {
        this.loadLessonsSynthesis(loggedCrp, program);
      }

    }


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
        synthesis = new ArrayList<>(mogSynthesisManager.getMogSynthesis(programID).stream()
          .filter(sy -> sy.isActive() && sy.getYear() == this.getCurrentCycleYear()).collect(Collectors.toList()));

        program.setSynthesis(new ArrayList<>(synthesis));

        if (this.isLessonsActive()) {
          this.loadLessonsSynthesis(loggedCrp, program);
        }

        this.setDraft(false);
      }
    }

    // Get all MOGs manually
    mogs = ipElementManager.getIPElementListForSynthesis(program);

    for (IpElement mog : mogs) {

      if (this.getIndex(mog.getId(), program.getId()) == -1) {
        MogSynthesy synthe = new MogSynthesy();

        synthe.setIpElement(mog);
        synthe.setIpProgram(program);;
        synthe.setYear(this.getCurrentCycleYear());
        synthe.setId(null);
        program.getSynthesis().add(synthe);

      }

    }

    String params[] = {loggedCrp.getAcronym(), program.getId() + ""};
    this.setBasePermission(this.getText(Permission.SYNTHESIS_BY_MOG_BASE_PERMISSION, params));
  }

  @Override
  public String save() {

    for (MogSynthesy synthe : program.getSynthesis()) {

      mogSynthesisManager.saveMogSynthesy(synthe);

    }

    if (this.isLessonsActive()) {
      this.saveLessonsSynthesis(loggedCrp, program);
    }


    List<String> relationsName = new ArrayList<>();
    relationsName.add(APConstants.IPPROGRAM_MOGSYNTHESIS_RELATION);
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

  public void setLiaisonInstitutionID(Long liaisonInstitutionID) {
    this.liaisonInstitutionID = liaisonInstitutionID;
  }

  public void setLiaisonInstitutions(List<IpLiaisonInstitution> liaisonInstitutions) {
    this.liaisonInstitutions = liaisonInstitutions;
  }

  public void setLoggedCrp(Crp loggedCrp) {
    this.loggedCrp = loggedCrp;
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

  public void setTransaction(String transaction) {
    this.transaction = transaction;
  }

  @Override
  public void validate() {
    if (save) {
      validator.validate(this, program.getSynthesis(), program, true);
    }
  }

}
