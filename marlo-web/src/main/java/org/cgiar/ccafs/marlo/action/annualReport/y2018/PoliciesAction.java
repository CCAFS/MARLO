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

package org.cgiar.ccafs.marlo.action.annualReport.y2018;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.AuditLogManager;
import org.cgiar.ccafs.marlo.data.manager.CrpProgramManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.LiaisonInstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectFocusManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPolicyManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndOrganizationTypeManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisFlagshipProgressManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisFlagshipProgressPolicyManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisManager;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.LiaisonUser;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProgramType;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectFocus;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicy;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesis;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgress;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgressPolicy;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgressPolicyDTO;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisPoliciesByOrganizationTypeDTO;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.AutoSaveReader;
import org.cgiar.ccafs.marlo.validation.annualreport.y2018.CCDimension2018Validator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Andrés Valencia - CIAT/CCAFS
 */
public class PoliciesAction extends BaseAction {

  private static final long serialVersionUID = -4977149795769459191L;

  // Managers
  private GlobalUnitManager crpManager;
  private LiaisonInstitutionManager liaisonInstitutionManager;
  private ReportSynthesisManager reportSynthesisManager;
  private AuditLogManager auditLogManager;
  private CrpProgramManager crpProgramManager;
  private UserManager userManager;
  private CCDimension2018Validator validator;
  private ProjectPolicyManager projectPolicyManager;
  private ProjectFocusManager projectFocusManager;
  private ProjectManager projectManager;
  private ReportSynthesisFlagshipProgressManager reportSynthesisFlagshipProgressManager;
  private ReportSynthesisFlagshipProgressPolicyManager reportSynthesisFlagshipProgressPolicyManager;
  private RepIndOrganizationTypeManager repIndOrganizationTypeManager;

  // Variables
  private String transaction;
  private ReportSynthesis reportSynthesis;
  private Long liaisonInstitutionID;
  private Long synthesisID;
  private LiaisonInstitution liaisonInstitution;
  private GlobalUnit loggedCrp;
  private List<LiaisonInstitution> liaisonInstitutions;
  private List<ProjectPolicy> projectPolicies;
  private List<ReportSynthesisPoliciesByOrganizationTypeDTO> policiesByOrganizationTypeDTOs;


  @Inject
  public PoliciesAction(APConfig config, GlobalUnitManager crpManager,
    LiaisonInstitutionManager liaisonInstitutionManager, ReportSynthesisManager reportSynthesisManager,
    AuditLogManager auditLogManager, UserManager userManager, CCDimension2018Validator validator,
    CrpProgramManager crpProgramManager, ProjectPolicyManager projectPolicyManager,
    ProjectFocusManager projectFocusManager, ProjectManager projectManager,
    ReportSynthesisFlagshipProgressManager reportSynthesisFlagshipProgressManager,
    ReportSynthesisFlagshipProgressPolicyManager reportSynthesisFlagshipProgressPolicyManager,
    RepIndOrganizationTypeManager repIndOrganizationTypeManager) {
    super(config);
    this.crpManager = crpManager;
    this.liaisonInstitutionManager = liaisonInstitutionManager;
    this.reportSynthesisManager = reportSynthesisManager;
    this.auditLogManager = auditLogManager;
    this.userManager = userManager;
    this.validator = validator;
    this.crpProgramManager = crpProgramManager;
    this.projectPolicyManager = projectPolicyManager;
    this.projectFocusManager = projectFocusManager;
    this.projectManager = projectManager;
    this.reportSynthesisFlagshipProgressManager = reportSynthesisFlagshipProgressManager;
    this.reportSynthesisFlagshipProgressPolicyManager = reportSynthesisFlagshipProgressPolicyManager;
    this.repIndOrganizationTypeManager = repIndOrganizationTypeManager;
  }


  /**
   * Method to fill the list of policies selected by flagships
   * 
   * @param flagshipsLiaisonInstitutions
   * @param phaseID
   * @return
   */
  public List<ReportSynthesisFlagshipProgressPolicyDTO>
    fillFpPlannedList(List<LiaisonInstitution> flagshipsLiaisonInstitutions, long phaseID) {
    List<ReportSynthesisFlagshipProgressPolicyDTO> flagshipPlannedList = new ArrayList<>();

    if (projectPolicyManager.findAll() != null) {

      // Get global unit policies
      List<ProjectPolicy> projectPolicies = new ArrayList<>(projectPolicyManager.findAll().stream()
        .filter(ps -> ps.isActive() && ps.getProjectPolicyInfo(this.getActualPhase()) != null
          && ps.getProjectPolicyInfo().isRequired() && ps.getProject() != null
          && ps.getProject().getGlobalUnitProjects().stream()
            .filter(gup -> gup.isActive() && gup.isOrigin() && gup.getGlobalUnit().getId().equals(loggedCrp.getId()))
            .collect(Collectors.toList()).size() > 0)
        .collect(Collectors.toList()));

      // Fill all project policies of the global unit
      for (ProjectPolicy projectPolicy : projectPolicies) {
        ReportSynthesisFlagshipProgressPolicyDTO dto = new ReportSynthesisFlagshipProgressPolicyDTO();
        projectPolicy.getProject().setProjectInfo(projectPolicy.getProject().getProjecInfoPhase(this.getActualPhase()));
        dto.setProjectPolicy(projectPolicy);
        if (projectPolicy.getProject().getProjectInfo().getAdministrative() != null
          && projectPolicy.getProject().getProjectInfo().getAdministrative()) {
          dto.setLiaisonInstitutions(new ArrayList<>());
          dto.getLiaisonInstitutions().add(this.liaisonInstitution);
        } else {
          List<ProjectFocus> projectFocuses = new ArrayList<>(projectPolicy.getProject().getProjectFocuses().stream()
            .filter(pf -> pf.isActive() && pf.getPhase().getId() == phaseID).collect(Collectors.toList()));
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

      // Get deleted policies
      List<ReportSynthesisFlagshipProgressPolicy> flagshipProgressPolicies = new ArrayList<>();
      for (LiaisonInstitution liaisonInstitution : flagshipsLiaisonInstitutions) {
        ReportSynthesis reportSynthesis = reportSynthesisManager.findSynthesis(phaseID, liaisonInstitution.getId());
        if (reportSynthesis != null) {
          if (reportSynthesis.getReportSynthesisFlagshipProgress() != null) {
            if (reportSynthesis.getReportSynthesisFlagshipProgress()
              .getReportSynthesisFlagshipProgressPolicies() != null) {
              List<ReportSynthesisFlagshipProgressPolicy> policies = new ArrayList<>(
                reportSynthesis.getReportSynthesisFlagshipProgress().getReportSynthesisFlagshipProgressPolicies()
                  .stream().filter(s -> s.isActive()).collect(Collectors.toList()));
              if (policies != null || !policies.isEmpty()) {
                for (ReportSynthesisFlagshipProgressPolicy reportSynthesisFlagshipProgressPolicy : policies) {
                  flagshipProgressPolicies.add(reportSynthesisFlagshipProgressPolicy);
                }
              }
            }
          }
        }
      }

      // Get list of policies to remove
      List<ReportSynthesisFlagshipProgressPolicyDTO> removeList = new ArrayList<>();
      for (ReportSynthesisFlagshipProgressPolicyDTO dto : flagshipPlannedList) {

        List<LiaisonInstitution> removeLiaison = new ArrayList<>();
        for (LiaisonInstitution liaisonInstitution : dto.getLiaisonInstitutions()) {
          ReportSynthesis reportSynthesis = reportSynthesisManager.findSynthesis(phaseID, liaisonInstitution.getId());
          if (reportSynthesis != null) {
            if (reportSynthesis.getReportSynthesisFlagshipProgress() != null) {

              ReportSynthesisFlagshipProgressPolicy flagshipProgressPolicyNew =
                new ReportSynthesisFlagshipProgressPolicy();
              flagshipProgressPolicyNew = new ReportSynthesisFlagshipProgressPolicy();
              flagshipProgressPolicyNew.setProjectPolicy(dto.getProjectPolicy());
              flagshipProgressPolicyNew
                .setReportSynthesisFlagshipProgress(reportSynthesis.getReportSynthesisFlagshipProgress());

              if (flagshipProgressPolicies.contains(flagshipProgressPolicyNew)) {
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

      // Remove policies unselected by flagships
      for (ReportSynthesisFlagshipProgressPolicyDTO i : removeList) {
        flagshipPlannedList.remove(i);
      }

    }
    return flagshipPlannedList;
  }


  private void fillProjectPoliciesList(Long phaseID, LiaisonInstitution liaisonInstitution) {
    projectPolicies = new ArrayList<>();
    Phase phase = this.getActualPhase();
    if (this.isFlagship()) {
      // Fill Project policies of the current flagship
      if (projectFocusManager.findAll() != null) {
        List<ProjectFocus> projectFocus = new ArrayList<>(projectFocusManager.findAll().stream()
          .filter(pf -> pf.isActive() && pf.getCrpProgram().getId() == liaisonInstitution.getCrpProgram().getId()
            && pf.getPhase() != null && pf.getPhase().getId() == phaseID)
          .collect(Collectors.toList()));

        for (ProjectFocus focus : projectFocus) {
          Project project = projectManager.getProjectById(focus.getProject().getId());
          List<ProjectPolicy> plannedProjectPolicies = new ArrayList<>(project.getProjectPolicies().stream()
            .filter(
              pp -> pp.isActive() && pp.getProjectPolicyInfo(phase) != null && pp.getProjectPolicyInfo().isRequired())
            .collect(Collectors.toList()));

          for (ProjectPolicy projectPolicy : plannedProjectPolicies) {
            projectPolicy.getProjectPolicyInfo(phase);
            projectPolicies.add(projectPolicy);
          }
        }
      }
    } else {
      // Fill Project policies of the PMU, removing flagship deletions
      liaisonInstitutions = loggedCrp.getLiaisonInstitutions().stream()
        .filter(c -> c.getCrpProgram() != null && c.isActive()
          && c.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue())
        .collect(Collectors.toList());
      liaisonInstitutions.sort(Comparator.comparing(LiaisonInstitution::getAcronym));

      List<ReportSynthesisFlagshipProgressPolicyDTO> flagshipPlannedList =
        this.fillFpPlannedList(liaisonInstitutions, phase.getId());

      for (ReportSynthesisFlagshipProgressPolicyDTO reportSynthesisFlagshipProgressPolicyDTO : flagshipPlannedList) {

        ProjectPolicy projectPolicy = reportSynthesisFlagshipProgressPolicyDTO.getProjectPolicy();
        projectPolicy.getProjectPolicyInfo(phase);
        projectPolicy.setSelectedFlahsgips(new ArrayList<>());
        projectPolicy.getSelectedFlahsgips().addAll(reportSynthesisFlagshipProgressPolicyDTO.getLiaisonInstitutions());

        projectPolicies.add(projectPolicy);

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


  private void
    flagshipProgressProjectPoliciesNewData(ReportSynthesisFlagshipProgress reportSynthesisFlagshipProgressDB) {

    List<Long> selectedPs = new ArrayList<>();
    List<Long> selectedPoliciesIds = new ArrayList<>();

    for (ProjectPolicy projectPolicy : projectPolicies) {
      selectedPoliciesIds.add(projectPolicy.getId());
    }

    // Add policies (active =0)
    if (reportSynthesis.getReportSynthesisFlagshipProgress().getPoliciesValue() != null
      && reportSynthesis.getReportSynthesisFlagshipProgress().getPoliciesValue().length() > 0) {
      List<Long> stList = new ArrayList<>();
      for (String string : reportSynthesis.getReportSynthesisFlagshipProgress().getPoliciesValue().trim().split(",")) {
        stList.add(Long.parseLong(string.trim()));
      }

      for (Long studyId : selectedPoliciesIds) {
        int index = stList.indexOf(studyId);
        if (index < 0) {
          selectedPs.add(studyId);
        }
      }

      for (ReportSynthesisFlagshipProgressPolicy flagshipProgressPolicy : reportSynthesisFlagshipProgressDB
        .getReportSynthesisFlagshipProgressPolicies().stream().filter(rio -> rio.isActive())
        .collect(Collectors.toList())) {
        if (!selectedPs.contains(flagshipProgressPolicy.getProjectPolicy().getId())) {
          reportSynthesisFlagshipProgressPolicyManager
            .deleteReportSynthesisFlagshipProgressPolicy(flagshipProgressPolicy.getId());
        }
      }

      for (Long policyId : selectedPs) {
        ProjectPolicy projectPolicy = projectPolicyManager.getProjectPolicyById(policyId);

        ReportSynthesisFlagshipProgressPolicy flagshipProgressPolicyNew = new ReportSynthesisFlagshipProgressPolicy();

        flagshipProgressPolicyNew = new ReportSynthesisFlagshipProgressPolicy();

        flagshipProgressPolicyNew.setProjectPolicy(projectPolicy);
        flagshipProgressPolicyNew.setReportSynthesisFlagshipProgress(reportSynthesisFlagshipProgressDB);

        List<ReportSynthesisFlagshipProgressPolicy> flagshipProgressPolicies =
          reportSynthesisFlagshipProgressDB.getReportSynthesisFlagshipProgressPolicies().stream()
            .filter(rio -> rio.isActive()).collect(Collectors.toList());


        if (!flagshipProgressPolicies.contains(flagshipProgressPolicyNew)) {
          flagshipProgressPolicyNew = reportSynthesisFlagshipProgressPolicyManager
            .saveReportSynthesisFlagshipProgressPolicy(flagshipProgressPolicyNew);
        }

      }
    } else {

      // Delete Policies (Save with active=1)
      for (Long policyId : selectedPoliciesIds) {
        ProjectPolicy projectPolicy = projectPolicyManager.getProjectPolicyById(policyId);

        ReportSynthesisFlagshipProgressPolicy flagshipProgressPlannedPolicyNew =
          new ReportSynthesisFlagshipProgressPolicy();

        flagshipProgressPlannedPolicyNew = new ReportSynthesisFlagshipProgressPolicy();

        flagshipProgressPlannedPolicyNew.setProjectPolicy(projectPolicy);
        flagshipProgressPlannedPolicyNew.setReportSynthesisFlagshipProgress(reportSynthesisFlagshipProgressDB);

        List<ReportSynthesisFlagshipProgressPolicy> reportSynthesisFlagshipProgressPolicies =
          reportSynthesisFlagshipProgressDB.getReportSynthesisFlagshipProgressPolicies().stream()
            .filter(rio -> rio.isActive()).collect(Collectors.toList());


        if (!reportSynthesisFlagshipProgressPolicies.contains(flagshipProgressPlannedPolicyNew)) {
          flagshipProgressPlannedPolicyNew = reportSynthesisFlagshipProgressPolicyManager
            .saveReportSynthesisFlagshipProgressPolicy(flagshipProgressPlannedPolicyNew);
        }
      }
    }

  }


  private Path getAutoSaveFilePath() {
    String composedClassName = reportSynthesis.getClass().getSimpleName();
    String actionFile = this.getActionName().replace("/", "_");
    String autoSaveFile = reportSynthesis.getId() + "_" + composedClassName + "_" + this.getActualPhase().getName()
      + "_" + this.getActualPhase().getYear() + "_" + actionFile + ".json";
    return Paths.get(config.getAutoSaveFolder() + autoSaveFile);
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

  public List<ReportSynthesisPoliciesByOrganizationTypeDTO> getPoliciesByOrganizationTypeDTOs() {
    return policiesByOrganizationTypeDTOs;
  }


  public List<ProjectPolicy> getProjectPolicies() {
    return projectPolicies;
  }

  public ReportSynthesis getReportSynthesis() {
    return reportSynthesis;
  }

  public Long getSynthesisID() {
    return synthesisID;
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
      ReportSynthesis history = (ReportSynthesis) auditLogManager.getHistory(transaction);
      if (history != null) {
        reportSynthesis = history;
        synthesisID = reportSynthesis.getId();
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
            .filter(lu -> lu.isActive() && lu.getLiaisonInstitution().isActive()
              && lu.getLiaisonInstitution().getCrp().getId() == loggedCrp.getId()
              && lu.getLiaisonInstitution().getInstitution() == null)
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
                  if (institution.getAcronym() != null && institution.getAcronym().equals("PMU")) {
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
        synthesisID = Long.parseLong(StringUtils.trim(this.getRequest().getParameter(APConstants.REPORT_SYNTHESIS_ID)));
        reportSynthesis = reportSynthesisManager.getReportSynthesisById(synthesisID);

        if (!reportSynthesis.getPhase().equals(phase)) {
          reportSynthesis = reportSynthesisManager.findSynthesis(phase.getId(), liaisonInstitutionID);
          if (reportSynthesis == null) {
            reportSynthesis = this.createReportSynthesis(phase.getId(), liaisonInstitutionID);
          }
          synthesisID = reportSynthesis.getId();
        }
      } catch (Exception e) {
        reportSynthesis = reportSynthesisManager.findSynthesis(phase.getId(), liaisonInstitutionID);
        if (reportSynthesis == null) {
          reportSynthesis = this.createReportSynthesis(phase.getId(), liaisonInstitutionID);
        }
        synthesisID = reportSynthesis.getId();

      }
    }

    if (reportSynthesis != null) {

      ReportSynthesis reportSynthesisDB = reportSynthesisManager.getReportSynthesisById(synthesisID);
      synthesisID = reportSynthesisDB.getId();
      liaisonInstitutionID = reportSynthesisDB.getLiaisonInstitution().getId();
      liaisonInstitution = liaisonInstitutionManager.getLiaisonInstitutionById(liaisonInstitutionID);

      this.fillProjectPoliciesList(phase.getId(), liaisonInstitution);

      Path path = this.getAutoSaveFilePath();
      // Verify if there is a Draft file
      if (path.toFile().exists() && this.getCurrentUser().isAutoSave()) {
        BufferedReader reader;
        reader = new BufferedReader(new FileReader(path.toFile()));
        Gson gson = new GsonBuilder().create();
        JsonObject jReader = gson.fromJson(reader, JsonObject.class);
        reader.close();
        AutoSaveReader autoSaveReader = new AutoSaveReader();
        reportSynthesis = (ReportSynthesis) autoSaveReader.readFromJson(jReader);
        synthesisID = reportSynthesis.getId();
        this.setDraft(true);
      } else {
        this.setDraft(false);
        // Check if ToC relation is null -create it
        if (reportSynthesis.getReportSynthesisFlagshipProgress() == null) {
          ReportSynthesisFlagshipProgress flagshipProgress = new ReportSynthesisFlagshipProgress();
          // create one to one relation
          reportSynthesis.setReportSynthesisFlagshipProgress(flagshipProgress);
          flagshipProgress.setReportSynthesis(reportSynthesis);
          // save the changes
          reportSynthesis = reportSynthesisManager.saveReportSynthesis(reportSynthesis);
        }


        reportSynthesis.getReportSynthesisFlagshipProgress().setProjectPolicies(new ArrayList<>());
        if (reportSynthesis.getReportSynthesisFlagshipProgress().getReportSynthesisFlagshipProgressPolicies() != null
          && !reportSynthesis.getReportSynthesisFlagshipProgress().getReportSynthesisFlagshipProgressPolicies()
            .isEmpty()) {
          for (ReportSynthesisFlagshipProgressPolicy flagshipProgressPolicy : reportSynthesis
            .getReportSynthesisFlagshipProgress().getReportSynthesisFlagshipProgressPolicies().stream()
            .filter(ro -> ro.isActive()).collect(Collectors.toList())) {
            reportSynthesis.getReportSynthesisFlagshipProgress().getProjectPolicies()
              .add(flagshipProgressPolicy.getProjectPolicy());
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

    // ADD PMU as liasion Institution too
    liaisonInstitutions.addAll(loggedCrp.getLiaisonInstitutions().stream()
      .filter(c -> c.getCrpProgram() == null && c.isActive() && c.getAcronym() != null && c.getAcronym().equals("PMU"))
      .collect(Collectors.toList()));

    /** Graphs and Tables */
    List<ProjectPolicy> selectedProjectPolicies = new ArrayList<ProjectPolicy>();
    policiesByOrganizationTypeDTOs = new ArrayList<ReportSynthesisPoliciesByOrganizationTypeDTO>();
    if (projectPolicies != null && !projectPolicies.isEmpty()) {
      selectedProjectPolicies.addAll(projectPolicies);
      // Remove unchecked policies
      if (reportSynthesis.getReportSynthesisFlagshipProgress().getProjectPolicies() != null
        && !reportSynthesis.getReportSynthesisFlagshipProgress().getProjectPolicies().isEmpty()) {
        for (ProjectPolicy projectPolicy : reportSynthesis.getReportSynthesisFlagshipProgress().getProjectPolicies()) {
          selectedProjectPolicies.remove(projectPolicy);
        }
      }
      // Chart: Policies by organization type
      if (selectedProjectPolicies != null && !selectedProjectPolicies.isEmpty()) {
        policiesByOrganizationTypeDTOs =
          repIndOrganizationTypeManager.getPoliciesByOrganizationTypes(selectedProjectPolicies, phase);
      }
    }

    // Base Permission
    String params[] = {loggedCrp.getAcronym(), reportSynthesis.getId() + ""};
    this.setBasePermission(this.getText(Permission.REPORT_SYNTHESIS_FLAGSHIP_PROGRESS_BASE_PERMISSION, params));

    if (this.isHttpPost()) {
      if (reportSynthesis.getReportSynthesisFlagshipProgress().getPlannedPolicies() != null) {
        reportSynthesis.getReportSynthesisFlagshipProgress().getPlannedPolicies().clear();
      }
    }

  }

  @Override
  public String save() {
    if (this.hasPermission("canEdit")) {

      ReportSynthesisFlagshipProgress reportSynthesisFlagshipProgressDB =
        reportSynthesisManager.getReportSynthesisById(synthesisID).getReportSynthesisFlagshipProgress();

      this.flagshipProgressProjectPoliciesNewData(reportSynthesisFlagshipProgressDB);

      if (reportSynthesis.getReportSynthesisFlagshipProgress().getPlannedPolicies() == null) {
        reportSynthesis.getReportSynthesisFlagshipProgress().setPlannedPolicies(new ArrayList<>());
      }

      reportSynthesisFlagshipProgressDB =
        reportSynthesisFlagshipProgressManager.saveReportSynthesisFlagshipProgress(reportSynthesisFlagshipProgressDB);

      List<String> relationsName = new ArrayList<>();
      reportSynthesis = reportSynthesisManager.getReportSynthesisById(synthesisID);

      /**
       * The following is required because we need to update something on the @ReportSynthesis if we want a row created
       * in the auditlog table.
       */
      this.setModificationJustification(reportSynthesis);

      reportSynthesisManager.save(reportSynthesis, this.getActionName(), relationsName, this.getActualPhase());

      Path path = this.getAutoSaveFilePath();
      if (path.toFile().exists()) {
        path.toFile().delete();
      }

      this.getActionMessages();
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

  public void setPoliciesByOrganizationTypeDTOs(
    List<ReportSynthesisPoliciesByOrganizationTypeDTO> policiesByOrganizationTypeDTOs) {
    this.policiesByOrganizationTypeDTOs = policiesByOrganizationTypeDTOs;
  }

  public void setProjectPolicies(List<ProjectPolicy> projectPolicies) {
    this.projectPolicies = projectPolicies;
  }


  public void setReportSynthesis(ReportSynthesis reportSynthesis) {
    this.reportSynthesis = reportSynthesis;
  }

  public void setSynthesisID(Long synthesisID) {
    this.synthesisID = synthesisID;
  }

  public void setTransaction(String transaction) {
    this.transaction = transaction;
  }

  @Override
  public void validate() {
    if (save) {
      // validator.validate(this, reportSynthesis, true);
    }
  }
}