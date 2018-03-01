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

package org.cgiar.ccafs.marlo.action.powb;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.AuditLogManager;
import org.cgiar.ccafs.marlo.data.manager.CrpProgramManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.LiaisonInstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.PowbMonitoringEvaluationLearningExerciseManager;
import org.cgiar.ccafs.marlo.data.manager.PowbMonitoringEvaluationLearningManager;
import org.cgiar.ccafs.marlo.data.manager.PowbSynthesisManager;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.LiaisonUser;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.PowbMonitoringEvaluationLearning;
import org.cgiar.ccafs.marlo.data.model.PowbMonitoringEvaluationLearningExercise;
import org.cgiar.ccafs.marlo.data.model.PowbSynthesis;
import org.cgiar.ccafs.marlo.data.model.ProgramType;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.AutoSaveReader;
import org.cgiar.ccafs.marlo.validation.powb.MonitoringEvaluationLearningValidator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class MonitoringEvaluationLearningAction extends BaseAction {


  private static final long serialVersionUID = 5112563288393590088L;


  // Managers
  private GlobalUnitManager crpManager;
  private PowbSynthesisManager powbSynthesisManager;
  private AuditLogManager auditLogManager;
  private LiaisonInstitutionManager liaisonInstitutionManager;
  private UserManager userManager;
  private CrpProgramManager crpProgramManager;
  private PowbMonitoringEvaluationLearningExerciseManager powbMonitoringEvaluationLearningExerciseManager;
  private PowbMonitoringEvaluationLearningManager powbMonitoringEvaluationLearningManager;
  private MonitoringEvaluationLearningValidator validator;


  // Variables
  private String transaction;
  private PowbSynthesis powbSynthesis;
  private Long liaisonInstitutionID;
  private Long powbSynthesisID;
  private GlobalUnit loggedCrp;
  private List<LiaisonInstitution> liaisonInstitutions;
  private LiaisonInstitution liaisonInstitution;


  private List<PowbMonitoringEvaluationLearningExercise> flagshipExercises;

  @Inject
  public MonitoringEvaluationLearningAction(APConfig config, GlobalUnitManager crpManager,
    PowbSynthesisManager powbSynthesisManager, AuditLogManager auditLogManager,
    LiaisonInstitutionManager liaisonInstitutionManager, UserManager userManager, CrpProgramManager crpProgramManager,
    PowbMonitoringEvaluationLearningExerciseManager powbMonitoringEvaluationLearningExerciseManager,
    PowbMonitoringEvaluationLearningManager powbMonitoringEvaluationLearningManager,
    MonitoringEvaluationLearningValidator validator) {
    super(config);
    this.crpManager = crpManager;
    this.powbSynthesisManager = powbSynthesisManager;
    this.auditLogManager = auditLogManager;
    this.liaisonInstitutionManager = liaisonInstitutionManager;
    this.userManager = userManager;
    this.crpProgramManager = crpProgramManager;
    this.powbMonitoringEvaluationLearningExerciseManager = powbMonitoringEvaluationLearningExerciseManager;
    this.powbMonitoringEvaluationLearningManager = powbMonitoringEvaluationLearningManager;
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

  public void exercisesPreviousData(List<PowbMonitoringEvaluationLearningExercise> exercises) {

    PowbSynthesis powbSynthesisDB = powbSynthesisManager.getPowbSynthesisById(powbSynthesisID);
    List<PowbMonitoringEvaluationLearningExercise> exercisesPrev = new ArrayList<>(
      powbSynthesisDB.getPowbMonitoringEvaluationLearning().getPowbMonitoringEvaluationLearningExercises().stream()
        .filter(ps -> ps.isActive()).collect(Collectors.toList()));

    for (PowbMonitoringEvaluationLearningExercise evaluationLearningExercise : exercisesPrev) {
      if (!exercises.contains(evaluationLearningExercise)) {
        powbMonitoringEvaluationLearningExerciseManager
          .deletePowbMonitoringEvaluationLearningExercise(evaluationLearningExercise.getId());
      }
    }
  }

  public void expectedStudiesNewData(PowbMonitoringEvaluationLearning powbMonitoringEvaluationLearningDB) {

    for (PowbMonitoringEvaluationLearningExercise exercises : powbSynthesis.getPowbMonitoringEvaluationLearning()
      .getExercises()) {
      if (exercises != null) {

        PowbMonitoringEvaluationLearningExercise exercisesNew;

        if (exercises.getId() == null) {
          exercisesNew = new PowbMonitoringEvaluationLearningExercise();
          exercisesNew.setActive(true);
          exercisesNew.setActiveSince(new Date());
          exercisesNew.setCreatedBy(this.getCurrentUser());
          exercisesNew.setModifiedBy(this.getCurrentUser());
          exercisesNew.setModificationJustification("");

        } else {
          exercisesNew = powbMonitoringEvaluationLearningExerciseManager
            .getPowbMonitoringEvaluationLearningExerciseById(exercises.getId());
          exercisesNew.setModifiedBy(this.getCurrentUser());

        }
        exercisesNew.setPowbMonitoringEvaluationLearning(powbMonitoringEvaluationLearningDB);
        exercisesNew.setExercise(exercises.getExercise());
        exercisesNew.setComments(exercises.getComments());
        exercisesNew =
          powbMonitoringEvaluationLearningExerciseManager.savePowbMonitoringEvaluationLearningExercise(exercisesNew);
      }
    }


  }

  public Long firstFlagship() {
    List<LiaisonInstitution> liaisonInstitutions = new ArrayList<>(loggedCrp.getLiaisonInstitutions().stream()
      .filter(c -> c.getCrpProgram() != null && c.isActive()
        && c.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue())
      .collect(Collectors.toList()));
    liaisonInstitutions.sort(Comparator.comparing(LiaisonInstitution::getAcronym));
    long liaisonInstitutionId = liaisonInstitutions.get(0).getId();
    return liaisonInstitutionId;
  }

  private Path getAutoSaveFilePath() {
    String composedClassName = powbSynthesis.getClass().getSimpleName();
    String actionFile = this.getActionName().replace("/", "_");
    String autoSaveFile = powbSynthesis.getId() + "_" + composedClassName + "_" + this.getActualPhase().getDescription()
      + "_" + this.getActualPhase().getYear() + "_" + actionFile + ".json";
    return Paths.get(config.getAutoSaveFolder() + autoSaveFile);
  }

  public List<PowbMonitoringEvaluationLearningExercise> getFlagshipExercises() {
    return flagshipExercises;
  }

  public void getfpExercises(List<LiaisonInstitution> lInstitutions, long phaseID) {
    flagshipExercises = new ArrayList<>();
    for (LiaisonInstitution liaisonInstitution : lInstitutions) {
      PowbSynthesis powbSynthesis = powbSynthesisManager.findSynthesis(phaseID, liaisonInstitution.getId());
      if (powbSynthesis != null) {
        if (powbSynthesis.getPowbMonitoringEvaluationLearning() != null) {
          if (powbSynthesis.getPowbMonitoringEvaluationLearning()
            .getPowbMonitoringEvaluationLearningExercises() != null) {
            List<PowbMonitoringEvaluationLearningExercise> exercises = new ArrayList<>(
              powbSynthesis.getPowbMonitoringEvaluationLearning().getPowbMonitoringEvaluationLearningExercises()
                .stream().filter(s -> s.isActive()).collect(Collectors.toList()));
            if (exercises != null || !exercises.isEmpty()) {
              for (PowbMonitoringEvaluationLearningExercise powbMonitoringEvaluationLearningExercise : exercises) {
                flagshipExercises.add(powbMonitoringEvaluationLearningExercise);
              }
            }
          }
        }
      }
    }
  }

  public LiaisonInstitution getLiaisonInstitution() {
    return liaisonInstitution;
  }

  public Long getLiaisonInstitutionID() {
    return liaisonInstitutionID;
  }

  public List<LiaisonInstitution> getLiaisonInstitutions() {
    return liaisonInstitutions;
  }


  public GlobalUnit getLoggedCrp() {
    return loggedCrp;
  }

  public PowbSynthesis getPowbSynthesis() {
    return powbSynthesis;
  }

  public Long getPowbSynthesisID() {
    return powbSynthesisID;
  }

  public String getTransaction() {
    return transaction;
  }

  public boolean isFlagship() {
    boolean isFP = false;
    if (liaisonInstitution != null) {
      if (liaisonInstitution.getCrpProgram() != null) {
        CrpProgram crpProgram =
          crpProgramManager.getCrpProgramById(liaisonInstitution.getCrpProgram().getId().longValue());
        if (crpProgram.getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue()) {
          isFP = true;
        }
      }
    }
    return isFP;
  }

  @Override
  public boolean isPMU() {
    boolean isFP = false;
    if (liaisonInstitution != null) {
      if (liaisonInstitution.getCrpProgram() == null) {
        isFP = true;
      }
    }
    return isFP;

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
    // Get current CRP
    loggedCrp = (GlobalUnit) this.getSession().get(APConstants.SESSION_CRP);
    loggedCrp = crpManager.getGlobalUnitById(loggedCrp.getId());
    Phase phase = this.getActualPhase();

    // If there is a history version being loaded
    if (this.getRequest().getParameter(APConstants.TRANSACTION_ID) != null) {
      transaction = StringUtils.trim(this.getRequest().getParameter(APConstants.TRANSACTION_ID));
      PowbSynthesis history = (PowbSynthesis) auditLogManager.getHistory(transaction);
      if (history != null) {
        powbSynthesis = history;
        powbSynthesisID = powbSynthesis.getId();
      } else {
        this.transaction = null;
        this.setTransaction("-1");
      }
    } else {
      // Get Liaison institution ID Parameter
      try {
        liaisonInstitutionID =
          Long.parseLong(StringUtils.trim(this.getRequest().getParameter(APConstants.LIAISON_INSTITUTION_REQUEST_ID)));
      } catch (NumberFormatException e) {
        User user = userManager.getUser(this.getCurrentUser().getId());
        if (user.getLiasonsUsers() != null || !user.getLiasonsUsers().isEmpty()) {
          List<LiaisonUser> liaisonUsers = new ArrayList<>(user.getLiasonsUsers().stream()
            .filter(lu -> lu.isActive() && lu.getLiaisonInstitution().getCrp().getId() == loggedCrp.getId())
            .collect(Collectors.toList()));
          if (!liaisonUsers.isEmpty()) {
            boolean isLeader = false;
            for (LiaisonUser liaisonUser : liaisonUsers) {
              LiaisonInstitution institution = liaisonUser.getLiaisonInstitution();
              if (institution.isActive()) {
                if (institution.getCrpProgram() != null) {
                  if (institution.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue()) {
                    liaisonInstitutionID = institution.getId();
                    isLeader = true;
                    break;
                  }
                } else {
                  if (institution.getAcronym().equals("PMU")) {
                    liaisonInstitutionID = institution.getId();
                    isLeader = true;
                    break;
                  }
                }
              }
            }
            if (!isLeader) {
              liaisonInstitutionID = this.firstFlagship();
            }
          } else {
            liaisonInstitutionID = this.firstFlagship();
          }
        } else {
          liaisonInstitutionID = this.firstFlagship();
        }
      }


      try {
        powbSynthesisID =
          Long.parseLong(StringUtils.trim(this.getRequest().getParameter(APConstants.POWB_SYNTHESIS_ID)));
        powbSynthesis = powbSynthesisManager.getPowbSynthesisById(powbSynthesisID);

        if (!powbSynthesis.getPhase().equals(phase)) {
          powbSynthesis = powbSynthesisManager.findSynthesis(phase.getId(), liaisonInstitutionID);
          if (powbSynthesis == null) {
            powbSynthesis = this.createPowbSynthesis(phase.getId(), liaisonInstitutionID);
          }
          powbSynthesisID = powbSynthesis.getId();
        }
      } catch (Exception e) {

        powbSynthesis = powbSynthesisManager.findSynthesis(phase.getId(), liaisonInstitutionID);
        if (powbSynthesis == null) {
          powbSynthesis = this.createPowbSynthesis(phase.getId(), liaisonInstitutionID);
        }
        powbSynthesisID = powbSynthesis.getId();

      }
    }


    if (powbSynthesis != null) {

      PowbSynthesis powbSynthesisDB = powbSynthesisManager.getPowbSynthesisById(powbSynthesisID);
      powbSynthesisID = powbSynthesisDB.getId();
      liaisonInstitutionID = powbSynthesisDB.getLiaisonInstitution().getId();
      liaisonInstitution = liaisonInstitutionManager.getLiaisonInstitutionById(liaisonInstitutionID);

      Path path = this.getAutoSaveFilePath();
      // Verify if there is a Draft file
      if (path.toFile().exists() && this.getCurrentUser().isAutoSave()) {
        BufferedReader reader;
        reader = new BufferedReader(new FileReader(path.toFile()));
        Gson gson = new GsonBuilder().create();
        JsonObject jReader = gson.fromJson(reader, JsonObject.class);
        AutoSaveReader autoSaveReader = new AutoSaveReader();
        powbSynthesis = (PowbSynthesis) autoSaveReader.readFromJson(jReader);
        powbSynthesisID = powbSynthesis.getId();
        this.setDraft(true);
        reader.close();
      } else {
        this.setDraft(false);
        // Check if ToC relation is null -create it
        if (powbSynthesis.getPowbMonitoringEvaluationLearning() == null) {
          PowbMonitoringEvaluationLearning monitoringEvaluationLearning = new PowbMonitoringEvaluationLearning();
          monitoringEvaluationLearning.setActive(true);
          monitoringEvaluationLearning.setActiveSince(new Date());
          monitoringEvaluationLearning.setCreatedBy(this.getCurrentUser());
          monitoringEvaluationLearning.setModifiedBy(this.getCurrentUser());
          monitoringEvaluationLearning.setModificationJustification("");
          // create one to one relation
          powbSynthesis.setPowbMonitoringEvaluationLearning(monitoringEvaluationLearning);
          monitoringEvaluationLearning.setPowbSynthesis(powbSynthesis);
          // save the changes
          powbSynthesis = powbSynthesisManager.savePowbSynthesis(powbSynthesis);
        }

        if (this.isFlagship()) {
          if (powbSynthesis.getPowbMonitoringEvaluationLearning()
            .getPowbMonitoringEvaluationLearningExercises() != null) {
            powbSynthesis.getPowbMonitoringEvaluationLearning()
              .setExercises(new ArrayList<>(
                powbSynthesis.getPowbMonitoringEvaluationLearning().getPowbMonitoringEvaluationLearningExercises()
                  .stream().filter(ps -> ps.isActive()).collect(Collectors.toList())));
          }
        }
      }
    }


    // Get the list of liaison institutions Flagships and PMU.
    liaisonInstitutions = loggedCrp.getLiaisonInstitutions().stream()
      .filter(c -> c.getCrpProgram() != null && c.isActive()
        && c.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue())
      .collect(Collectors.toList());
    liaisonInstitutions.sort(Comparator.comparing(LiaisonInstitution::getAcronym));


    if (this.isPMU()) {
      this.getfpExercises(liaisonInstitutions, phase.getId());
    }

    liaisonInstitutions.addAll(loggedCrp.getLiaisonInstitutions().stream()
      .filter(c -> c.getCrpProgram() == null && c.isActive() && c.getAcronym().equals("PMU"))
      .collect(Collectors.toList()));

    // Base Permission
    String params[] = {loggedCrp.getAcronym(), powbSynthesis.getId() + ""};
    this.setBasePermission(this.getText(Permission.POWB_SYNTHESIS_MEL_BASE_PERMISSION, params));

    if (this.isHttpPost()) {
      if (powbSynthesis.getPowbMonitoringEvaluationLearning().getExercises() != null) {
        powbSynthesis.getPowbMonitoringEvaluationLearning().getExercises().clear();
      }
    }
  }

  @Override
  public String save() {
    if (this.hasPermission("canEdit")) {

      PowbMonitoringEvaluationLearning monitoringEvaluationLearningDB =
        powbSynthesisManager.getPowbSynthesisById(powbSynthesisID).getPowbMonitoringEvaluationLearning();

      if (this.isFlagship()) {
        if (powbSynthesis.getPowbMonitoringEvaluationLearning().getExercises() == null) {
          powbSynthesis.getPowbMonitoringEvaluationLearning().setExercises(new ArrayList<>());
        }


        this.exercisesPreviousData(powbSynthesis.getPowbMonitoringEvaluationLearning().getExercises());
        this.expectedStudiesNewData(monitoringEvaluationLearningDB);
      }

      if (this.isPMU()) {
        monitoringEvaluationLearningDB.setHighlight(powbSynthesis.getPowbMonitoringEvaluationLearning().getHighlight());
      }

      monitoringEvaluationLearningDB =
        powbMonitoringEvaluationLearningManager.savePowbMonitoringEvaluationLearning(monitoringEvaluationLearningDB);

      List<String> relationsName = new ArrayList<>();

      powbSynthesis = powbSynthesisManager.getPowbSynthesisById(powbSynthesisID);
      powbSynthesis.setModifiedBy(this.getCurrentUser());
      powbSynthesis.setActiveSince(new Date());

      powbSynthesisManager.save(powbSynthesis, this.getActionName(), relationsName, this.getActualPhase());


      Path path = this.getAutoSaveFilePath();
      if (path.toFile().exists()) {
        path.toFile().delete();
      }

      Collection<String> messages = this.getActionMessages();
      if (!this.getInvalidFields().isEmpty()) {
        this.setActionMessages(null);
        // this.addActionMessage(Map.toString(this.getInvalidFields().toArray()));
        List<String> keys = new ArrayList<String>(this.getInvalidFields().keySet());
        for (String key : keys) {
          this.addActionMessage(key + ": " + this.getInvalidFields().get(key));
        }

      } else {
        this.addActionMessage("message:" + this.getText("saving.saved"));
      }

      return SUCCESS;
    } else {
      return NOT_AUTHORIZED;
    }
  }

  public void setFlagshipExercises(List<PowbMonitoringEvaluationLearningExercise> flagshipExercises) {
    this.flagshipExercises = flagshipExercises;
  }

  public void setLiaisonInstitution(LiaisonInstitution liaisonInstitution) {
    this.liaisonInstitution = liaisonInstitution;
  }

  public void setLiaisonInstitutionID(Long liaisonInstitutionID) {
    this.liaisonInstitutionID = liaisonInstitutionID;
  }

  public void setLiaisonInstitutions(List<LiaisonInstitution> liaisonInstitutions) {
    this.liaisonInstitutions = liaisonInstitutions;
  }

  public void setLoggedCrp(GlobalUnit loggedCrp) {
    this.loggedCrp = loggedCrp;
  }


  public void setPowbSynthesis(PowbSynthesis powbSynthesis) {
    this.powbSynthesis = powbSynthesis;
  }

  public void setPowbSynthesisID(Long powbSynthesisID) {
    this.powbSynthesisID = powbSynthesisID;
  }

  public void setTransaction(String transaction) {
    this.transaction = transaction;
  }

  @Override
  public void validate() {
    if (save) {
      validator.validate(this, powbSynthesis, true);
    }
  }
}
