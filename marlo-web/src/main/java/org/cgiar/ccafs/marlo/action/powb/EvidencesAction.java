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
import org.cgiar.ccafs.marlo.data.manager.PowbEvidenceManager;
import org.cgiar.ccafs.marlo.data.manager.PowbEvidencePlannedStudyManager;
import org.cgiar.ccafs.marlo.data.manager.PowbSynthesisManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectFocusManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.SrfSloIndicatorManager;
import org.cgiar.ccafs.marlo.data.manager.SrfSubIdoManager;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.GlobalScopeEnum;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.LiaisonUser;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.PowbEvidence;
import org.cgiar.ccafs.marlo.data.model.PowbEvidencePlannedStudy;
import org.cgiar.ccafs.marlo.data.model.PowbEvidencePlannedStudyDTO;
import org.cgiar.ccafs.marlo.data.model.PowbSynthesis;
import org.cgiar.ccafs.marlo.data.model.ProgramType;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudy;
import org.cgiar.ccafs.marlo.data.model.ProjectFocus;
import org.cgiar.ccafs.marlo.data.model.SrfSloIndicator;
import org.cgiar.ccafs.marlo.data.model.SrfSubIdo;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.AutoSaveReader;
import org.cgiar.ccafs.marlo.validation.powb.EvidencesValidator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class EvidencesAction extends BaseAction {


  private static final long serialVersionUID = 6318765683403322101L;


  // Managers
  private GlobalUnitManager crpManager;

  private PowbSynthesisManager powbSynthesisManager;


  private AuditLogManager auditLogManager;

  private LiaisonInstitutionManager liaisonInstitutionManager;


  private UserManager userManager;

  private CrpProgramManager crpProgramManager;


  private SrfSubIdoManager srfSubIdoManager;


  private SrfSloIndicatorManager srfSloIndicatorManager;


  private PowbEvidencePlannedStudyManager powbEvidencePlannedStudyManager;


  private PowbEvidenceManager powbEvidenceManager;


  private EvidencesValidator validator;

  private ProjectFocusManager projectFocusManager;

  private ProjectManager projectManager;


  private ProjectExpectedStudyManager projectExpectedStudyManager;

  // Variables
  private String transaction;

  private PowbSynthesis powbSynthesis;


  private Long liaisonInstitutionID;


  private Long powbSynthesisID;

  private GlobalUnit loggedCrp;

  private List<LiaisonInstitution> liaisonInstitutions;


  private Map<Long, String> subIdos;

  private Map<Long, String> targets;


  private Map<Integer, String> scopes;

  private LiaisonInstitution liaisonInstitution;

  private List<PowbEvidencePlannedStudyDTO> flagshipPlannedList;


  private List<ProjectExpectedStudy> popUpProjects;


  @Inject
  public EvidencesAction(APConfig config, GlobalUnitManager crpManager, PowbSynthesisManager powbSynthesisManager,
    AuditLogManager auditLogManager, UserManager userManager, CrpProgramManager crpProgramManager,
    SrfSubIdoManager srfSubIdoManager, SrfSloIndicatorManager srfSloIndicatorManager,
    PowbEvidencePlannedStudyManager powbEvidencePlannedStudyManager,
    LiaisonInstitutionManager liaisonInstitutionManager, PowbEvidenceManager powbEvidenceManager,
    EvidencesValidator validator, ProjectFocusManager projectFocusManager, ProjectManager projectManager,
    ProjectExpectedStudyManager projectExpectedStudyManager) {
    super(config);
    this.crpManager = crpManager;
    this.auditLogManager = auditLogManager;
    this.userManager = userManager;
    this.crpProgramManager = crpProgramManager;
    this.powbSynthesisManager = powbSynthesisManager;
    this.srfSubIdoManager = srfSubIdoManager;
    this.srfSloIndicatorManager = srfSloIndicatorManager;
    this.powbEvidencePlannedStudyManager = powbEvidencePlannedStudyManager;
    this.liaisonInstitutionManager = liaisonInstitutionManager;
    this.powbEvidenceManager = powbEvidenceManager;
    this.validator = validator;
    this.projectFocusManager = projectFocusManager;
    this.projectManager = projectManager;
    this.projectExpectedStudyManager = projectExpectedStudyManager;
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


  public void expectedStudiesNewData(PowbEvidence powbEvidenceDB) {

    List<Long> selectedPs = new ArrayList<>();
    List<Long> studiesIds = new ArrayList<>();

    for (ProjectExpectedStudy std : popUpProjects) {
      studiesIds.add(std.getId());
    }

    if (powbSynthesis.getPowbEvidence().getPlannedStudiesValue() != null
      && powbSynthesis.getPowbEvidence().getPlannedStudiesValue().length() > 0) {
      List<Long> stList = new ArrayList<>();
      for (String string : powbSynthesis.getPowbEvidence().getPlannedStudiesValue().trim().split(",")) {
        stList.add(Long.parseLong(string.trim()));
      }


      for (Long studyId : studiesIds) {
        int index = stList.indexOf(studyId);
        if (index < 0) {
          selectedPs.add(studyId);
        }


      }

      for (PowbEvidencePlannedStudy powbStudy : powbEvidenceDB.getPowbEvidencePlannedStudies().stream()
        .filter(rio -> rio.isActive()).collect(Collectors.toList())) {
        if (!selectedPs.contains(powbStudy.getProjectExpectedStudy().getId())) {
          powbEvidencePlannedStudyManager.deletePowbEvidencePlannedStudy(powbStudy.getId());
        }
      }

      for (Long studyId : selectedPs) {
        ProjectExpectedStudy expectedStudy = projectExpectedStudyManager.getProjectExpectedStudyById(studyId);

        PowbEvidencePlannedStudy evidencePlannedStudyNew = new PowbEvidencePlannedStudy();

        evidencePlannedStudyNew = new PowbEvidencePlannedStudy();

        evidencePlannedStudyNew.setProjectExpectedStudy(expectedStudy);
        evidencePlannedStudyNew.setPowbEvidence(powbEvidenceDB);

        List<PowbEvidencePlannedStudy> powbEvidencePlannedStudies = powbEvidenceDB.getPowbEvidencePlannedStudies()
          .stream().filter(rio -> rio.isActive()).collect(Collectors.toList());


        if (!powbEvidencePlannedStudies.contains(evidencePlannedStudyNew)) {
          evidencePlannedStudyNew.setActive(true);
          evidencePlannedStudyNew.setActiveSince(new Date());
          evidencePlannedStudyNew.setCreatedBy(this.getCurrentUser());
          evidencePlannedStudyNew.setModifiedBy(this.getCurrentUser());
          evidencePlannedStudyNew.setModificationJustification("");
          evidencePlannedStudyNew =
            powbEvidencePlannedStudyManager.savePowbEvidencePlannedStudy(evidencePlannedStudyNew);
        }

      }
    } else {

      for (Long studyId : studiesIds) {
        ProjectExpectedStudy expectedStudy = projectExpectedStudyManager.getProjectExpectedStudyById(studyId);

        PowbEvidencePlannedStudy evidencePlannedStudyNew = new PowbEvidencePlannedStudy();

        evidencePlannedStudyNew = new PowbEvidencePlannedStudy();

        evidencePlannedStudyNew.setProjectExpectedStudy(expectedStudy);
        evidencePlannedStudyNew.setPowbEvidence(powbEvidenceDB);

        List<PowbEvidencePlannedStudy> powbEvidencePlannedStudies = powbEvidenceDB.getPowbEvidencePlannedStudies()
          .stream().filter(rio -> rio.isActive()).collect(Collectors.toList());


        if (!powbEvidencePlannedStudies.contains(evidencePlannedStudyNew)) {
          evidencePlannedStudyNew.setActive(true);
          evidencePlannedStudyNew.setActiveSince(new Date());
          evidencePlannedStudyNew.setCreatedBy(this.getCurrentUser());
          evidencePlannedStudyNew.setModifiedBy(this.getCurrentUser());
          evidencePlannedStudyNew.setModificationJustification("");
          evidencePlannedStudyNew =
            powbEvidencePlannedStudyManager.savePowbEvidencePlannedStudy(evidencePlannedStudyNew);
        }
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


  public List<PowbEvidencePlannedStudyDTO> getFlagshipPlannedList() {
    return flagshipPlannedList;
  }


  public void getFpPlannedList(List<LiaisonInstitution> lInstitutions, long phaseID) {
    flagshipPlannedList = new ArrayList<>();

    if (projectExpectedStudyManager.findAll() != null) {
      List<ProjectExpectedStudy> expectedStudies =
        new ArrayList<>(
          projectExpectedStudyManager.findAll().stream()
            .filter(ps -> ps.isActive() && ps.getPhase().getId() == phaseID
              && ps.getProject().getGlobalUnitProjects().stream()
                .filter(
                  gup -> gup.isActive() && gup.isOrigin() && gup.getGlobalUnit().getId().equals(loggedCrp.getId()))
                .collect(Collectors.toList()).size() > 0)
            .collect(Collectors.toList()));

      for (ProjectExpectedStudy projectExpectedStudy : expectedStudies) {
        System.out.println(projectExpectedStudy.getId());
        PowbEvidencePlannedStudyDTO dto = new PowbEvidencePlannedStudyDTO();
        projectExpectedStudy.getProject()
          .setProjectInfo(projectExpectedStudy.getProject().getProjecInfoPhase(this.getActualPhase()));
        dto.setProjectExpectedStudy(projectExpectedStudy);
        if (projectExpectedStudy.getProject().getProjectInfo().getAdministrative() != null
          && projectExpectedStudy.getProject().getProjectInfo().getAdministrative()) {
          dto.setLiaisonInstitutions(new ArrayList<>());
          dto.getLiaisonInstitutions().add(this.liaisonInstitution);
        } else {
          List<ProjectFocus> projectFocuses = new ArrayList<>(projectExpectedStudy.getProject().getProjectFocuses()
            .stream().filter(pf -> pf.isActive() && pf.getPhase().getId() == phaseID).collect(Collectors.toList()));
          List<LiaisonInstitution> liaisonInstitutions = new ArrayList<>();
          for (ProjectFocus projectFocus : projectFocuses) {
            liaisonInstitutions.addAll(projectFocus.getCrpProgram().getLiaisonInstitutions().stream()
              .filter(li -> li.isActive() && li.getCrpProgram() != null
                && li.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue())
              .collect(Collectors.toList()));
          }
          dto.setLiaisonInstitutions(liaisonInstitutions);
        }

        flagshipPlannedList.add(dto);
      }

      List<PowbEvidencePlannedStudy> evidencePlannedStudies = new ArrayList<>();
      for (LiaisonInstitution liaisonInstitution : lInstitutions) {
        PowbSynthesis powbSynthesis = powbSynthesisManager.findSynthesis(phaseID, liaisonInstitution.getId());
        if (powbSynthesis != null) {
          if (powbSynthesis.getPowbEvidence() != null) {
            if (powbSynthesis.getPowbEvidence().getPowbEvidencePlannedStudies() != null) {
              List<PowbEvidencePlannedStudy> studies = new ArrayList<>(powbSynthesis.getPowbEvidence()
                .getPowbEvidencePlannedStudies().stream().filter(s -> s.isActive()).collect(Collectors.toList()));
              if (studies != null || !studies.isEmpty()) {
                for (PowbEvidencePlannedStudy powbEvidencePlannedStudy : studies) {
                  evidencePlannedStudies.add(powbEvidencePlannedStudy);
                }
              }
            }
          }
        }
      }

      List<PowbEvidencePlannedStudyDTO> removeList = new ArrayList<>();
      for (PowbEvidencePlannedStudyDTO dto : flagshipPlannedList) {

        List<LiaisonInstitution> removeLiaison = new ArrayList<>();
        for (LiaisonInstitution liaisonInstitution : dto.getLiaisonInstitutions()) {
          PowbSynthesis powbSynthesis = powbSynthesisManager.findSynthesis(phaseID, liaisonInstitution.getId());
          if (powbSynthesis != null) {
            if (powbSynthesis.getPowbEvidence() != null) {

              PowbEvidencePlannedStudy evidencePlannedStudyNew = new PowbEvidencePlannedStudy();
              evidencePlannedStudyNew = new PowbEvidencePlannedStudy();
              evidencePlannedStudyNew.setProjectExpectedStudy(dto.getProjectExpectedStudy());
              evidencePlannedStudyNew.setPowbEvidence(powbSynthesis.getPowbEvidence());

              if (evidencePlannedStudies.contains(evidencePlannedStudyNew)) {
                removeLiaison.add(liaisonInstitution);
              }
            }
          }
        }

        for (LiaisonInstitution li : removeLiaison) {
          dto.getLiaisonInstitutions().remove(li);
        }

        if (dto.getLiaisonInstitutions().isEmpty()) {
          removeList.add(dto);
        }
      }


      for (PowbEvidencePlannedStudyDTO i : removeList) {
        flagshipPlannedList.remove(i);
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

  public List<ProjectExpectedStudy> getPopUpProjects() {
    return popUpProjects;
  }

  public PowbSynthesis getPowbSynthesis() {
    return powbSynthesis;
  }

  public Long getPowbSynthesisID() {
    return powbSynthesisID;
  }


  public Map<Integer, String> getScopes() {
    return scopes;
  }

  public Map<Long, String> getSubIdos() {
    return subIdos;
  }

  public Map<Long, String> getTargets() {
    return targets;
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

  public void plannedStudiesPreviousData(List<PowbEvidencePlannedStudy> plannedStudies) {

    PowbSynthesis powbSynthesisDB = powbSynthesisManager.getPowbSynthesisById(powbSynthesisID);
    List<PowbEvidencePlannedStudy> plannedStudiesPrev = new ArrayList<>(powbSynthesisDB.getPowbEvidence()
      .getPowbEvidencePlannedStudies().stream().filter(ps -> ps.isActive()).collect(Collectors.toList()));

    for (PowbEvidencePlannedStudy powbEvidencePlannedStudy : plannedStudiesPrev) {
      if (!plannedStudies.contains(powbEvidencePlannedStudy)) {
        powbEvidencePlannedStudyManager.deletePowbEvidencePlannedStudy(powbEvidencePlannedStudy.getId());
      }
    }
  }


  public void popUpProject(long phaseID, LiaisonInstitution liaisonInstitution) {

    popUpProjects = new ArrayList<>();

    if (projectFocusManager.findAll() != null) {

      List<ProjectFocus> projectFocus = new ArrayList<>(projectFocusManager.findAll().stream()
        .filter(pf -> pf.isActive() && pf.getCrpProgram().getId() == liaisonInstitution.getCrpProgram().getId()
          && pf.getPhase() != null && pf.getPhase().getId() == phaseID)
        .collect(Collectors.toList()));

      for (ProjectFocus focus : projectFocus) {
        Project project = projectManager.getProjectById(focus.getProject().getId());
        List<ProjectExpectedStudy> expectedStudies = new ArrayList<>(project.getProjectExpectedStudies().stream()
          .filter(es -> es.isActive() && es.getPhase().getId() == phaseID).collect(Collectors.toList()));
        for (ProjectExpectedStudy projectExpectedStudy : expectedStudies) {
          popUpProjects.add(projectExpectedStudy);
        }
      }
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
          List<LiaisonUser> liaisonUsers = new ArrayList<>(
            user.getLiasonsUsers().stream().filter(lu -> lu.isActive() && lu.getLiaisonInstitution().isActive()
              && lu.getLiaisonInstitution().getCrp().getId() == loggedCrp.getId()).collect(Collectors.toList()));
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

      if (this.isFlagship()) {
        this.popUpProject(phase.getId(), liaisonInstitution);
      }

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

        if (this.isFlagship()) {
          if (powbSynthesis.getPowbEvidence().getPlannedStudiesValue() != null) {
            String[] studyValues = powbSynthesis.getPowbEvidence().getPlannedStudiesValue().split(",");
            powbSynthesis.getPowbEvidence().setExpectedStudies(new ArrayList<>());


            for (int i = 0; i < studyValues.length; i++) {

              ProjectExpectedStudy study =
                projectExpectedStudyManager.getProjectExpectedStudyById(Long.parseLong(studyValues[i]));
              powbSynthesis.getPowbEvidence().getExpectedStudies().add(study);
            }
          }
        }

        this.setDraft(true);
        reader.close();
      } else {
        this.setDraft(false);
        // Check if ToC relation is null -create it
        if (powbSynthesis.getPowbEvidence() == null) {
          PowbEvidence evidence = new PowbEvidence();
          evidence.setActive(true);
          evidence.setActiveSince(new Date());
          evidence.setCreatedBy(this.getCurrentUser());
          evidence.setModifiedBy(this.getCurrentUser());
          evidence.setModificationJustification("");
          // create one to one relation
          powbSynthesis.setPowbEvidence(evidence);
          evidence.setPowbSynthesis(powbSynthesis);
          // save the changes
          powbSynthesis = powbSynthesisManager.savePowbSynthesis(powbSynthesis);
        }

        if (this.isFlagship()) {
          powbSynthesis.getPowbEvidence().setExpectedStudies(new ArrayList<>());
          if (powbSynthesis.getPowbEvidence().getPowbEvidencePlannedStudies() != null
            && !powbSynthesis.getPowbEvidence().getPowbEvidencePlannedStudies().isEmpty()) {
            for (PowbEvidencePlannedStudy plannedStudy : powbSynthesis.getPowbEvidence().getPowbEvidencePlannedStudies()
              .stream().filter(ro -> ro.isActive()).collect(Collectors.toList())) {
              powbSynthesis.getPowbEvidence().getExpectedStudies().add(plannedStudy.getProjectExpectedStudy());
            }
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
      this.getFpPlannedList(liaisonInstitutions, phase.getId());
    }

    liaisonInstitutions.addAll(loggedCrp.getLiaisonInstitutions().stream()
      .filter(c -> c.getCrpProgram() == null && c.isActive() && c.getAcronym().equals("PMU"))
      .collect(Collectors.toList()));

    // Setup Geo Scope List
    scopes = new HashMap<>();
    List<GlobalScopeEnum> listScope = Arrays.asList(GlobalScopeEnum.values());
    for (GlobalScopeEnum globalScopeEnum : listScope) {
      scopes.put(globalScopeEnum.getId(), globalScopeEnum.getType());
    }

    // Setup Sub IDOS list
    subIdos = new HashMap<>();
    for (SrfSubIdo srfSubIdo : srfSubIdoManager.findAll()) {
      subIdos.put(srfSubIdo.getId(), srfSubIdo.getDescription());
    }

    // Setup SLO Indicators List
    targets = new HashMap<>();
    for (SrfSloIndicator srfSloIndicator : srfSloIndicatorManager.findAll()) {
      targets.put(srfSloIndicator.getId(), srfSloIndicator.getTitle());
    }

    // Base Permission
    String params[] = {loggedCrp.getAcronym(), powbSynthesis.getId() + ""};
    this.setBasePermission(this.getText(Permission.POWB_SYNTHESIS_EVIDENCES_BASE_PERMISSION, params));

    if (this.isHttpPost()) {
      if (powbSynthesis.getPowbEvidence().getPlannedStudies() != null) {
        powbSynthesis.getPowbEvidence().getPlannedStudies().clear();
      }
    }
  }

  @Override
  public String save() {
    if (this.hasPermission("canEdit")) {

      PowbEvidence powbEvidenceDB = powbSynthesisManager.getPowbSynthesisById(powbSynthesisID).getPowbEvidence();
      if (this.isFlagship()) {
        if (powbSynthesis.getPowbEvidence().getPlannedStudies() == null) {
          powbSynthesis.getPowbEvidence().setPlannedStudies(new ArrayList<>());
        }
        this.expectedStudiesNewData(powbEvidenceDB);
      }

      if (this.isPMU()) {
        powbEvidenceDB.setNarrative(powbSynthesis.getPowbEvidence().getNarrative());
      }

      powbEvidenceDB = powbEvidenceManager.savePowbEvidence(powbEvidenceDB);

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

  public void setFlagshipPlannedList(List<PowbEvidencePlannedStudyDTO> flagshipPlannedList) {
    this.flagshipPlannedList = flagshipPlannedList;
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

  public void setPopUpProjects(List<ProjectExpectedStudy> popUpProjects) {
    this.popUpProjects = popUpProjects;
  }


  public void setPowbSynthesis(PowbSynthesis powbSynthesis) {
    this.powbSynthesis = powbSynthesis;
  }

  public void setPowbSynthesisID(Long powbSynthesisID) {
    this.powbSynthesisID = powbSynthesisID;
  }

  public void setScopes(Map<Integer, String> scopes) {
    this.scopes = scopes;
  }


  public void setSubIdos(Map<Long, String> subIdos) {
    this.subIdos = subIdos;
  }

  public void setTargets(Map<Long, String> targets) {
    this.targets = targets;
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
