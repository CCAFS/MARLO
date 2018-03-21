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
import org.cgiar.ccafs.marlo.data.manager.CrpMilestoneManager;
import org.cgiar.ccafs.marlo.data.manager.CrpProgramManager;
import org.cgiar.ccafs.marlo.data.manager.FileDBManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.LiaisonInstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.PowbExpectedCrpProgressManager;
import org.cgiar.ccafs.marlo.data.manager.PowbSynthesisManager;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.model.CrpMilestone;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.CrpProgramOutcome;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.LiaisonUser;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.PowbExpectedCrpProgress;
import org.cgiar.ccafs.marlo.data.model.PowbSynthesis;
import org.cgiar.ccafs.marlo.data.model.ProgramType;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectBudgetsFlagship;
import org.cgiar.ccafs.marlo.data.model.ProjectFocus;
import org.cgiar.ccafs.marlo.data.model.ProjectMilestone;
import org.cgiar.ccafs.marlo.data.model.ProjectStatusEnum;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.AutoSaveReader;
import org.cgiar.ccafs.marlo.validation.powb.ExpectedCRPProgressValidator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Christian Garcia- CIAT/CCAFS
 */
public class ExpectedCRPProgressAction extends BaseAction {


  /**
   * 
   */
  private static final long serialVersionUID = 87856461770557046L;

  // Managers
  private GlobalUnitManager crpManager;

  private LiaisonInstitutionManager liaisonInstitutionManager;


  private PowbSynthesisManager powbSynthesisManager;

  private AuditLogManager auditLogManager;


  private UserManager userManager;

  private CrpProgramManager crpProgramManager;

  private FileDBManager fileDBManager;

  private ExpectedCRPProgressValidator validator;

  // Variables
  private String transaction;

  private PowbSynthesis powbSynthesis;

  private Long liaisonInstitutionID;
  private LiaisonInstitution liaisonInstitution;


  private Long powbSynthesisID;


  private GlobalUnit loggedCrp;

  private List<LiaisonInstitution> liaisonInstitutions;


  private List<CrpProgramOutcome> outcomes;

  private CrpMilestoneManager crpMilestoneManager;

  private PowbExpectedCrpProgressManager powbExpectedCrpProgressManager;

  private List<CrpProgram> flagships;


  @Inject
  public ExpectedCRPProgressAction(APConfig config, GlobalUnitManager crpManager,
    LiaisonInstitutionManager liaisonInstitutionManager, FileDBManager fileDBManager, AuditLogManager auditLogManager,
    UserManager userManager, CrpProgramManager crpProgramManager, PowbSynthesisManager powbSynthesisManager,
    ExpectedCRPProgressValidator validator, CrpMilestoneManager crpMilestoneManager,
    PowbExpectedCrpProgressManager powbExpectedCrpProgressManager) {
    super(config);
    this.crpManager = crpManager;
    this.liaisonInstitutionManager = liaisonInstitutionManager;
    this.fileDBManager = fileDBManager;
    this.auditLogManager = auditLogManager;
    this.userManager = userManager;
    this.crpProgramManager = crpProgramManager;
    this.powbSynthesisManager = powbSynthesisManager;
    this.crpMilestoneManager = crpMilestoneManager;
    this.validator = validator;
    this.powbExpectedCrpProgressManager = powbExpectedCrpProgressManager;
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

  public void expectedProgressNewData() {
    if (powbSynthesis.getExpectedCrpProgresses() == null) {
      powbSynthesis.setExpectedCrpProgresses(new ArrayList<>());
    }
    for (PowbExpectedCrpProgress powbExpectedCrpProgress : powbSynthesis.getExpectedCrpProgresses()) {
      PowbExpectedCrpProgress powbExpectedCrpProgressNew = null;
      if (powbExpectedCrpProgress != null) {


        if (powbExpectedCrpProgress.getCrpMilestone() != null
          && powbExpectedCrpProgress.getCrpMilestone().getId() > 0) {
          powbExpectedCrpProgress.setCrpMilestone(
            crpMilestoneManager.getCrpMilestoneById(powbExpectedCrpProgress.getCrpMilestone().getId()));
        }


        if (powbExpectedCrpProgress.getId() == null) {

          powbExpectedCrpProgressNew = new PowbExpectedCrpProgress();
          powbExpectedCrpProgressNew.setActive(true);
          powbExpectedCrpProgressNew.setCreatedBy(this.getCurrentUser());
          powbExpectedCrpProgressNew.setModifiedBy(this.getCurrentUser());
          powbExpectedCrpProgressNew.setModificationJustification("");
          powbExpectedCrpProgressNew.setActiveSince(new Date());
          powbExpectedCrpProgressNew.setPowbSynthesis(powbSynthesis);

        } else {

          powbExpectedCrpProgressNew =
            powbExpectedCrpProgressManager.getPowbExpectedCrpProgressById(powbExpectedCrpProgress.getId());
          powbExpectedCrpProgressNew.setModifiedBy(this.getCurrentUser());


        }
        powbExpectedCrpProgressNew.setExpectedHighlights(powbExpectedCrpProgress.getExpectedHighlights());
        powbExpectedCrpProgressNew.setMeans(powbExpectedCrpProgress.getMeans());
        powbExpectedCrpProgressNew.setAssessment(powbExpectedCrpProgress.getAssessment());
        powbExpectedCrpProgressNew.setCrpMilestone(powbExpectedCrpProgress.getCrpMilestone());

        powbExpectedCrpProgressNew =
          powbExpectedCrpProgressManager.savePowbExpectedCrpProgress(powbExpectedCrpProgressNew);


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

  public List<ProjectMilestone> getContributions(long milestoneID) {
    List<ProjectMilestone> milestones = new ArrayList<>();
    Set<ProjectMilestone> milestonesSet = new HashSet<>();

    CrpMilestone crpMilestone = crpMilestoneManager.getCrpMilestoneById(milestoneID);
    List<ProjectMilestone> projectMilestones =
      crpMilestone.getProjectMilestones().stream().filter(c -> c.isActive() && c.getProjectOutcome().getPhase() != null
        && c.getProjectOutcome().getPhase().equals(this.getActualPhase())).collect(Collectors.toList());

    for (ProjectMilestone projectMilestone : projectMilestones) {
      projectMilestone.getProjectOutcome().getProject().getProjecInfoPhase(this.getActualPhase());
      if (projectMilestone.getProjectOutcome().isActive()) {
        Project project = projectMilestone.getProjectOutcome().getProject();
        if (project.getProjecInfoPhase(this.getActualPhase()) != null) {
          if (project.getProjecInfoPhase(this.getActualPhase()).getStatus().longValue() == Long
            .parseLong(ProjectStatusEnum.Ongoing.getStatusId())
            || project.getProjecInfoPhase(this.getActualPhase()).getStatus().longValue() == Long
              .parseLong(ProjectStatusEnum.Extended.getStatusId())) {
            if (project.getProjecInfoPhase(this.getActualPhase()).getEndDate() != null) {
              Calendar cal = Calendar.getInstance();
              cal.setTime(project.getProjecInfoPhase(this.getActualPhase()).getEndDate());
              if (cal.get(Calendar.YEAR) >= this.getActualPhase().getYear()) {
                milestonesSet.add(projectMilestone);
              }
            }
          }
        }
      }


    }
    milestones.addAll(milestonesSet);
    return milestones;
  }

  public List<CrpProgram> getFlagships() {
    return flagships;
  }


  public int getIndex(Long crpMilestoneID) {
    if (powbSynthesis.getExpectedCrpProgresses() != null) {
      int i = 0;
      for (PowbExpectedCrpProgress powbExpectedCrpProgress : powbSynthesis.getExpectedCrpProgresses()) {
        if (powbExpectedCrpProgress != null && powbExpectedCrpProgress.getCrpMilestone() != null
          && powbExpectedCrpProgress.getCrpMilestone().getId() != null) {
          if (powbExpectedCrpProgress.getCrpMilestone().getId().longValue() == crpMilestoneID.longValue()) {
            return i;
          }

        }
        i++;
      }


    } else

    {
      powbSynthesis.setExpectedCrpProgresses(new ArrayList<>());
    }

    PowbExpectedCrpProgress powbExpectedCrpProgress = new PowbExpectedCrpProgress();
    powbExpectedCrpProgress.setCrpMilestone(crpMilestoneManager.getCrpMilestoneById(crpMilestoneID));
    powbExpectedCrpProgress.setPowbSynthesis(powbSynthesis);
    powbSynthesis.getExpectedCrpProgresses().add(powbExpectedCrpProgress);

    return this.getIndex(crpMilestoneID);

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


  public List<CrpProgramOutcome> getOutcomes() {
    return outcomes;
  }

  // Method to download link file
  public String getPath() {
    return config.getDownloadURL() + "/" + this.getPowbSourceFolder().replace('\\', '/');
  }

  public PowbExpectedCrpProgress getPMUPowbExpectedCrpProgress() {
    if (powbSynthesis.getExpectedCrpProgresses().isEmpty()) {
      PowbExpectedCrpProgress powbExpectedCrpProgress = new PowbExpectedCrpProgress();
      powbExpectedCrpProgress.setPowbSynthesis(powbSynthesis);
      powbSynthesis.getExpectedCrpProgresses().add(powbExpectedCrpProgress);
    }
    return powbSynthesis.getExpectedCrpProgresses().get(0);
  }

  public PowbExpectedCrpProgress getPowbExpectedCrpProgress(Long crpMilestoneID) {
    return powbSynthesis.getExpectedCrpProgresses().get(this.getIndex(crpMilestoneID));
  }

  public PowbExpectedCrpProgress getPowbExpectedCrpProgressProgram(Long crpMilestoneID, Long crpProgramID) {
    List<PowbExpectedCrpProgress> powbExpectedCrpProgresses =
      powbExpectedCrpProgressManager.findByProgram(crpProgramID);
    List<PowbExpectedCrpProgress> powbExpectedCrpProgressMilestone = powbExpectedCrpProgresses.stream()
      .filter(c -> c.getCrpMilestone().getId().longValue() == crpMilestoneID.longValue() && c.isActive())
      .collect(Collectors.toList());
    if (!powbExpectedCrpProgressMilestone.isEmpty()) {
      return powbExpectedCrpProgressMilestone.get(0);
    }
    return new PowbExpectedCrpProgress();
  }

  // Method to get the download folder
  private String getPowbSourceFolder() {
    return APConstants.POWB_FOLDER.concat(File.separator).concat(this.getCrpSession()).concat(File.separator)
      .concat(liaisonInstitution.getAcronym()).concat(File.separator).concat(this.getActionName().replace("/", "_"))
      .concat(File.separator);
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

  public void loadFlagShipBudgetInfo(CrpProgram crpProgram) {
    List<ProjectFocus> projects = crpProgram.getProjectFocuses().stream()
      .filter(c -> c.getProject().isActive() && c.isActive()).collect(Collectors.toList());
    Set<Project> myProjects = new HashSet();
    for (ProjectFocus projectFocus : projects) {
      Project project = projectFocus.getProject();
      if (project.isActive()) {
        project.setProjectInfo(project.getProjecInfoPhase(this.getActualPhase()));
        if (project.getProjectInfo() != null && project.getProjectInfo().getStatus() != null) {
          if (project.getProjectInfo().getStatus().intValue() == Integer
            .parseInt(ProjectStatusEnum.Ongoing.getStatusId())
            || project.getProjectInfo().getStatus().intValue() == Integer
              .parseInt(ProjectStatusEnum.Extended.getStatusId())) {
            myProjects.add(project);
          }
        }


      }
    }
    for (Project project : myProjects) {


      double w1 = project.getCoreBudget(this.getActualPhase().getYear(), this.getActualPhase());
      double w3 = project.getW3Budget(this.getActualPhase().getYear(), this.getActualPhase());
      double bilateral = project.getBilateralBudget(this.getActualPhase().getYear(), this.getActualPhase());
      List<ProjectBudgetsFlagship> budgetsFlagships = project.getProjectBudgetsFlagships().stream()
        .filter(c -> c.isActive() && c.getCrpProgram().getId().longValue() == crpProgram.getId().longValue()
          && c.getPhase().equals(this.getActualPhase()) && c.getYear() == this.getActualPhase().getYear())
        .collect(Collectors.toList());
      double percentageW1 = 0;
      double percentageW3 = 0;
      double percentageB = 0;

      if (!this.getCountProjectFlagships(project.getId())) {
        percentageW1 = 100;
        percentageW3 = 100;
        percentageB = 100;

      }
      for (ProjectBudgetsFlagship projectBudgetsFlagship : budgetsFlagships) {
        switch (projectBudgetsFlagship.getBudgetType().getId().intValue()) {
          case 1:
            percentageW1 = percentageW1 + projectBudgetsFlagship.getAmount();
            break;
          case 2:
            percentageW3 = percentageW3 + projectBudgetsFlagship.getAmount();
            break;
          case 3:
            percentageB = percentageB + projectBudgetsFlagship.getAmount();
            break;
          default:
            break;
        }
      }
      w1 = w1 * (percentageW1) / 100;
      w3 = w3 * (percentageW3) / 100;
      bilateral = bilateral * (percentageB) / 100;
      crpProgram.setW1(crpProgram.getW1() + w1);
      crpProgram.setW3(crpProgram.getW3() + w3 + bilateral);


    }
  }


  public void loadTablePMU() {
    flagships = loggedCrp.getCrpPrograms().stream()
      .filter(c -> c.isActive() && c.getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue())
      .collect(Collectors.toList());
    flagships.sort((p1, p2) -> p1.getAcronym().compareTo(p2.getAcronym()));

    for (CrpProgram crpProgram : flagships) {
      crpProgram.setMilestones(new ArrayList<>());
      crpProgram.setW1(new Double(0));
      crpProgram.setW3(new Double(0));

      crpProgram.setOutcomes(crpProgram.getCrpProgramOutcomes().stream()
        .filter(c -> c.isActive() && c.getPhase().equals(this.getActualPhase())).collect(Collectors.toList()));
      List<CrpProgramOutcome> validOutcomes = new ArrayList<>();
      for (CrpProgramOutcome crpProgramOutcome : crpProgram.getOutcomes()) {

        crpProgramOutcome.setMilestones(crpProgramOutcome.getCrpMilestones().stream()
          .filter(c -> c.isActive() && c.getYear().intValue() == this.getActualPhase().getYear())
          .collect(Collectors.toList()));
        crpProgramOutcome.setSubIdos(
          crpProgramOutcome.getCrpOutcomeSubIdos().stream().filter(c -> c.isActive()).collect(Collectors.toList()));
        crpProgram.getMilestones().addAll(crpProgramOutcome.getMilestones());
        if (!crpProgram.getMilestones().isEmpty()) {
          validOutcomes.add(crpProgramOutcome);
        }
      }
      crpProgram.setOutcomes(validOutcomes);
      this.loadFlagShipBudgetInfo(crpProgram);

    }
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
    liaisonInstitution = liaisonInstitutionManager.getLiaisonInstitutionById(liaisonInstitutionID);

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
        powbSynthesisID =
          Long.parseLong(StringUtils.trim(this.getRequest().getParameter(APConstants.POWB_SYNTHESIS_ID)));
        powbSynthesis = powbSynthesisManager.getPowbSynthesisById(powbSynthesisID);

        if (!powbSynthesis.getPhase().equals(this.getActualPhase())) {
          powbSynthesis = powbSynthesisManager.findSynthesis(this.getActualPhase().getId(), liaisonInstitutionID);
          if (powbSynthesis == null) {
            powbSynthesis = this.createPowbSynthesis(this.getActualPhase().getId(), liaisonInstitutionID);
          }
          powbSynthesisID = powbSynthesis.getId();
        }
      } catch (Exception e) {
        Phase phase = this.getActualPhase();
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

      Path path = this.getAutoSaveFilePath();
      // Verify if there is a Draft file
      if (path.toFile().exists() && this.getCurrentUser().isAutoSave()) {
        BufferedReader reader;
        reader = new BufferedReader(new FileReader(path.toFile()));
        Gson gson = new GsonBuilder().create();
        JsonObject jReader = gson.fromJson(reader, JsonObject.class);
        AutoSaveReader autoSaveReader = new AutoSaveReader();
        powbSynthesis = (PowbSynthesis) autoSaveReader.readFromJson(jReader);
        powbSynthesis.setLiaisonInstitution(liaisonInstitution);
        powbSynthesisID = powbSynthesis.getId();
        this.setDraft(true);
        reader.close();
      } else {
        this.setDraft(false);
        powbSynthesis.setExpectedCrpProgresses(
          powbSynthesis.getPowbExpectedCrpProgresses().stream().filter(c -> c.isActive()).collect(Collectors.toList()));
        powbSynthesis.getExpectedCrpProgresses()
          .sort((p1, p2) -> p1.getCrpMilestone().getId().compareTo(p2.getCrpMilestone().getId()));
      }
    }
    outcomes = new ArrayList<>();
    List<CrpProgramOutcome> outcomesList = new ArrayList<>();
    Set<CrpProgramOutcome> outcomesSet = new HashSet<>();

    for (CrpProgram crpProgram : loggedCrp.getCrpPrograms().stream().filter(c -> c.isActive())
      .collect(Collectors.toList())) {
      outcomesList
        .addAll(crpProgram.getCrpProgramOutcomes().stream()
          .filter(c -> c.isActive() && c.getPhase().equals(this.getActualPhase())
            && liaisonInstitution.getCrpProgram() != null
            && liaisonInstitution.getCrpProgram().getId().equals(c.getCrpProgram().getId()))
          .collect(Collectors.toList()));
    }
    for (CrpProgramOutcome outcome : outcomesList) {
      outcome.setMilestones(outcome.getCrpMilestones().stream()
        .filter(c -> c.isActive() && c.getYear().intValue() == this.getActualPhase().getYear())
        .collect(Collectors.toList()));
      if (!outcome.getMilestones().isEmpty()) {
        outcomesSet.add(outcome);
      }
    }
    outcomes.addAll(outcomesSet);
    outcomes.sort((p1, p2) -> p1.getId().compareTo(p2.getId()));


    // Get the list of liaison institutions Flagships and PMU.
    liaisonInstitutions = loggedCrp.getLiaisonInstitutions().stream()
      .filter(c -> c.getCrpProgram() != null && c.isActive()
        && c.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue())
      .collect(Collectors.toList());
    liaisonInstitutions.addAll(loggedCrp.getLiaisonInstitutions().stream()
      .filter(c -> c.getCrpProgram() == null && c.getAcronym().equals("PMU") & c.isActive())
      .collect(Collectors.toList()));
    liaisonInstitutions.sort(Comparator.comparing(LiaisonInstitution::getAcronym));

    if (this.isPMU()) {
      this.loadTablePMU();
    }

    // Base Permission
    String params[] = {loggedCrp.getAcronym(), powbSynthesis.getId() + ""};
    this.setBasePermission(this.getText(Permission.POWB_SYNTHESIS_EXPECTED_BASE_PERMISSION, params));

    if (this.isHttpPost()) {
      if (powbSynthesis.getExpectedCrpProgresses() != null) {
        powbSynthesis.setExpectedCrpProgresses(null);
      }
    }
  }

  @Override
  public String save() {
    if (this.hasPermission("canEdit")) {


      this.expectedProgressNewData();
      List<String> relationsName = new ArrayList<>();
      powbSynthesis = powbSynthesisManager.getPowbSynthesisById(powbSynthesisID);
      powbSynthesis.setModifiedBy(this.getCurrentUser());
      powbSynthesis.setActiveSince(new Date());
      relationsName.add(APConstants.SYNTHESIS_EXPECTED_RELATION);
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

  public void setFlagships(List<CrpProgram> flagships) {
    this.flagships = flagships;
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

  public void setOutcomes(List<CrpProgramOutcome> outcomes) {
    this.outcomes = outcomes;
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
