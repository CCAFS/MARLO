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
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndInnovationTypeManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndStageInnovationManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisFlagshipProgressInnovationManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisFlagshipProgressManager;
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
import org.cgiar.ccafs.marlo.data.model.ProjectInnovation;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesis;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgress;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgressInnovation;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgressInnovationDTO;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisInnovationsByStageDTO;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisInnovationsByTypeDTO;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.AutoSaveReader;
import org.cgiar.ccafs.marlo.validation.annualreport.y2018.Innovations2018Validator;

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
public class InnovationsAction extends BaseAction {

  private static final long serialVersionUID = 8323800211228698584L;

  // Managers
  private GlobalUnitManager crpManager;
  private LiaisonInstitutionManager liaisonInstitutionManager;
  private ReportSynthesisManager reportSynthesisManager;
  private AuditLogManager auditLogManager;
  private CrpProgramManager crpProgramManager;
  private UserManager userManager;
  private Innovations2018Validator validator;
  private ProjectInnovationManager projectInnovationManager;
  private ProjectFocusManager projectFocusManager;
  private ProjectManager projectManager;
  private ReportSynthesisFlagshipProgressManager reportSynthesisFlagshipProgressManager;
  private ReportSynthesisFlagshipProgressInnovationManager reportSynthesisFlagshipProgressInnovationManager;
  private RepIndStageInnovationManager repIndStageInnovationManager;
  private RepIndInnovationTypeManager repIndInnovationTypeManager;

  // Variables
  private String transaction;
  private ReportSynthesis reportSynthesis;
  private Long liaisonInstitutionID;
  private Long synthesisID;
  private LiaisonInstitution liaisonInstitution;
  private GlobalUnit loggedCrp;
  private List<LiaisonInstitution> liaisonInstitutions;
  private List<ProjectInnovation> projectInnovations;
  private List<ReportSynthesisInnovationsByStageDTO> innovationsByStageDTO;
  private List<ReportSynthesisInnovationsByTypeDTO> innovationsByTypeDTO;


  @Inject
  public InnovationsAction(APConfig config, GlobalUnitManager crpManager,
    LiaisonInstitutionManager liaisonInstitutionManager, ReportSynthesisManager reportSynthesisManager,
    AuditLogManager auditLogManager, UserManager userManager, Innovations2018Validator validator,
    CrpProgramManager crpProgramManager, ProjectInnovationManager projectInnovationManager,
    ProjectFocusManager projectFocusManager, ProjectManager projectManager,
    ReportSynthesisFlagshipProgressManager reportSynthesisFlagshipProgressManager,
    ReportSynthesisFlagshipProgressInnovationManager reportSynthesisFlagshipProgressInnovationManager,
    RepIndStageInnovationManager repIndStageInnovationManager,
    RepIndInnovationTypeManager repIndInnovationTypeManager) {
    super(config);
    this.crpManager = crpManager;
    this.liaisonInstitutionManager = liaisonInstitutionManager;
    this.reportSynthesisManager = reportSynthesisManager;
    this.auditLogManager = auditLogManager;
    this.userManager = userManager;
    this.validator = validator;
    this.crpProgramManager = crpProgramManager;
    this.projectInnovationManager = projectInnovationManager;
    this.projectFocusManager = projectFocusManager;
    this.projectManager = projectManager;
    this.reportSynthesisFlagshipProgressManager = reportSynthesisFlagshipProgressManager;
    this.reportSynthesisFlagshipProgressInnovationManager = reportSynthesisFlagshipProgressInnovationManager;
    this.repIndStageInnovationManager = repIndStageInnovationManager;
    this.repIndInnovationTypeManager = repIndInnovationTypeManager;
  }


  /**
   * Method to fill the list of innovations selected by flagships
   * 
   * @param flagshipsLiaisonInstitutions
   * @param phaseID
   * @return
   */
  public List<ReportSynthesisFlagshipProgressInnovationDTO>
    fillFpPlannedList(List<LiaisonInstitution> flagshipsLiaisonInstitutions, long phaseID) {
    List<ReportSynthesisFlagshipProgressInnovationDTO> flagshipPlannedList = new ArrayList<>();

    if (projectInnovationManager.findAll() != null) {

      // Get global unit Innovations
      List<ProjectInnovation> projectInnovations =
        new ArrayList<>(projectInnovationManager.findAll().stream()
          .filter(ps -> ps.isActive() && ps.getProjectInnovationInfo(this.getActualPhase()) != null
            && ps.getProject() != null
            && ps.getProject().getGlobalUnitProjects().stream()
              .filter(gup -> gup.isActive() && gup.isOrigin() && gup.getGlobalUnit().getId().equals(loggedCrp.getId()))
              .collect(Collectors.toList()).size() > 0)
          .collect(Collectors.toList()));

      // Fill all project Innovations of the global unit
      for (ProjectInnovation projectInnovation : projectInnovations) {
        ReportSynthesisFlagshipProgressInnovationDTO dto = new ReportSynthesisFlagshipProgressInnovationDTO();
        projectInnovation.getProject()
          .setProjectInfo(projectInnovation.getProject().getProjecInfoPhase(this.getActualPhase()));
        dto.setProjectInnovation(projectInnovation);
        if (projectInnovation.getProject().getProjectInfo().getAdministrative() != null
          && projectInnovation.getProject().getProjectInfo().getAdministrative()) {
          dto.setLiaisonInstitutions(new ArrayList<>());
          dto.getLiaisonInstitutions().add(this.liaisonInstitution);
        } else {
          List<ProjectFocus> projectFocuses = new ArrayList<>(projectInnovation.getProject().getProjectFocuses()
            .stream().filter(pf -> pf.isActive() && pf.getPhase().getId() == phaseID).collect(Collectors.toList()));
          List<LiaisonInstitution> liaisonInstitutions = new ArrayList<>();
          for (ProjectFocus projectFocus : projectFocuses) {
            liaisonInstitutions.addAll(projectFocus.getCrpProgram().getLiaisonInstitutions().stream()
              .filter(li -> li.isActive() && li.getCrpProgram() != null
                && li.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue()
                && li.getCrp() != null && li.getCrp().equals(this.getLoggedCrp()))
              .collect(Collectors.toList()));
          }
          dto.setLiaisonInstitutions(liaisonInstitutions);
        }

        flagshipPlannedList.add(dto);
      }

      // Get deleted innovations
      List<ReportSynthesisFlagshipProgressInnovation> flagshipProgressInnovations = new ArrayList<>();
      for (LiaisonInstitution liaisonInstitution : flagshipsLiaisonInstitutions) {
        ReportSynthesis reportSynthesis = reportSynthesisManager.findSynthesis(phaseID, liaisonInstitution.getId());
        if (reportSynthesis != null) {
          if (reportSynthesis.getReportSynthesisFlagshipProgress() != null) {
            if (reportSynthesis.getReportSynthesisFlagshipProgress()
              .getReportSynthesisFlagshipProgressInnovations() != null) {
              List<ReportSynthesisFlagshipProgressInnovation> innovations = new ArrayList<>(
                reportSynthesis.getReportSynthesisFlagshipProgress().getReportSynthesisFlagshipProgressInnovations()
                  .stream().filter(s -> s.isActive()).collect(Collectors.toList()));
              if (innovations != null || !innovations.isEmpty()) {
                for (ReportSynthesisFlagshipProgressInnovation reportSynthesisFlagshipProgressInnovation : innovations) {
                  flagshipProgressInnovations.add(reportSynthesisFlagshipProgressInnovation);
                }
              }
            }
          }
        }
      }

      // Get list of Innovations to remove
      List<ReportSynthesisFlagshipProgressInnovationDTO> removeList = new ArrayList<>();
      for (ReportSynthesisFlagshipProgressInnovationDTO dto : flagshipPlannedList) {

        List<LiaisonInstitution> removeLiaison = new ArrayList<>();
        for (LiaisonInstitution liaisonInstitution : dto.getLiaisonInstitutions()) {
          ReportSynthesis reportSynthesis = reportSynthesisManager.findSynthesis(phaseID, liaisonInstitution.getId());
          if (reportSynthesis != null) {
            if (reportSynthesis.getReportSynthesisFlagshipProgress() != null) {

              ReportSynthesisFlagshipProgressInnovation flagshipProgressInnovationNew =
                new ReportSynthesisFlagshipProgressInnovation();
              flagshipProgressInnovationNew = new ReportSynthesisFlagshipProgressInnovation();
              flagshipProgressInnovationNew.setProjectInnovation(dto.getProjectInnovation());
              flagshipProgressInnovationNew
                .setReportSynthesisFlagshipProgress(reportSynthesis.getReportSynthesisFlagshipProgress());

              if (flagshipProgressInnovations.contains(flagshipProgressInnovationNew)) {
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

      // Remove Innovations unselected by flagships
      for (ReportSynthesisFlagshipProgressInnovationDTO i : removeList) {
        flagshipPlannedList.remove(i);
      }

    }
    return flagshipPlannedList;
  }

  private void fillprojectInnovationsList(Long phaseID, LiaisonInstitution liaisonInstitution) {
    projectInnovations = new ArrayList<>();
    Phase phase = this.getActualPhase();
    if (this.isFlagship()) {
      // Fill Project Innovations of the current flagship
      if (projectFocusManager.findAll() != null) {
        List<ProjectFocus> projectFocus = new ArrayList<>(projectFocusManager.findAll().stream()
          .filter(pf -> pf.isActive() && pf.getCrpProgram().getId() == liaisonInstitution.getCrpProgram().getId()
            && pf.getPhase() != null && pf.getPhase().getId() == phaseID)
          .collect(Collectors.toList()));

        for (ProjectFocus focus : projectFocus) {
          Project project = projectManager.getProjectById(focus.getProject().getId());
          List<ProjectInnovation> plannedprojectInnovations = new ArrayList<>(project.getProjectInnovations().stream()
            .filter(ps -> ps.isActive() && ps.getProjectInnovationInfo(phase) != null).collect(Collectors.toList()));

          for (ProjectInnovation projectInnovation : plannedprojectInnovations) {
            projectInnovation.getProjectInnovationInfo(phase);
            projectInnovations.add(projectInnovation);
          }
        }
      }
    } else {
      // Fill Project Innovations of the PMU, removing flagship deletions
      liaisonInstitutions = loggedCrp.getLiaisonInstitutions().stream()
        .filter(c -> c.getCrpProgram() != null && c.isActive()
          && c.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue())
        .collect(Collectors.toList());
      liaisonInstitutions.sort(Comparator.comparing(LiaisonInstitution::getAcronym));

      List<ReportSynthesisFlagshipProgressInnovationDTO> flagshipPlannedList =
        this.fillFpPlannedList(liaisonInstitutions, phase.getId());

      for (ReportSynthesisFlagshipProgressInnovationDTO reportSynthesisFlagshipProgressInnovationDTO : flagshipPlannedList) {

        ProjectInnovation projectInnovation = reportSynthesisFlagshipProgressInnovationDTO.getProjectInnovation();
        projectInnovation.getProjectInnovationInfo(phase);
        projectInnovation.setSelectedFlahsgips(new ArrayList<>());
        // sort selected flagships
        if (reportSynthesisFlagshipProgressInnovationDTO.getLiaisonInstitutions() != null
          && !reportSynthesisFlagshipProgressInnovationDTO.getLiaisonInstitutions().isEmpty()) {
          reportSynthesisFlagshipProgressInnovationDTO.getLiaisonInstitutions()
            .sort((l1, l2) -> l1.getCrpProgram().getAcronym().compareTo(l2.getCrpProgram().getAcronym()));
        }
        projectInnovation.getSelectedFlahsgips()
          .addAll(reportSynthesisFlagshipProgressInnovationDTO.getLiaisonInstitutions());
        projectInnovations.add(projectInnovation);

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
    flagshipProgressprojectInnovationsNewData(ReportSynthesisFlagshipProgress reportSynthesisFlagshipProgressDB) {

    List<Long> selectedPs = new ArrayList<>();
    List<Long> selectedInnovationsIds = new ArrayList<>();

    for (ProjectInnovation projectInnovation : projectInnovations) {
      selectedInnovationsIds.add(projectInnovation.getId());
    }

    // Add Innovations (active =0)
    if (reportSynthesis.getReportSynthesisFlagshipProgress().getInnovationsValue() != null
      && reportSynthesis.getReportSynthesisFlagshipProgress().getInnovationsValue().length() > 0) {
      List<Long> stList = new ArrayList<>();
      for (String string : reportSynthesis.getReportSynthesisFlagshipProgress().getInnovationsValue().trim()
        .split(",")) {
        stList.add(Long.parseLong(string.trim()));
      }

      for (Long innovationId : selectedInnovationsIds) {
        int index = stList.indexOf(innovationId);
        if (index < 0) {
          selectedPs.add(innovationId);
        }
      }

      for (ReportSynthesisFlagshipProgressInnovation flagshipProgressInnovation : reportSynthesisFlagshipProgressDB
        .getReportSynthesisFlagshipProgressInnovations().stream().filter(rio -> rio.isActive())
        .collect(Collectors.toList())) {
        if (!selectedPs.contains(flagshipProgressInnovation.getProjectInnovation().getId())) {
          reportSynthesisFlagshipProgressInnovationManager
            .deleteReportSynthesisFlagshipProgressInnovation(flagshipProgressInnovation.getId());
        }
      }

      for (Long innovationId : selectedPs) {
        ProjectInnovation projectInnovation = projectInnovationManager.getProjectInnovationById(innovationId);

        ReportSynthesisFlagshipProgressInnovation flagshipProgressInnovationNew =
          new ReportSynthesisFlagshipProgressInnovation();

        flagshipProgressInnovationNew.setProjectInnovation(projectInnovation);
        flagshipProgressInnovationNew.setReportSynthesisFlagshipProgress(reportSynthesisFlagshipProgressDB);

        List<ReportSynthesisFlagshipProgressInnovation> flagshipProgressInnovations =
          reportSynthesisFlagshipProgressDB.getReportSynthesisFlagshipProgressInnovations().stream()
            .filter(rio -> rio.isActive()).collect(Collectors.toList());


        if (!flagshipProgressInnovations.contains(flagshipProgressInnovationNew)) {
          flagshipProgressInnovationNew = reportSynthesisFlagshipProgressInnovationManager
            .saveReportSynthesisFlagshipProgressInnovation(flagshipProgressInnovationNew);
        }

      }
    } else {

      // Delete Innovations (Save with active=1)
      for (Long innovationId : selectedInnovationsIds) {
        ProjectInnovation projectInnovation = projectInnovationManager.getProjectInnovationById(innovationId);

        ReportSynthesisFlagshipProgressInnovation flagshipProgressPlannedInnovationNew =
          new ReportSynthesisFlagshipProgressInnovation();

        flagshipProgressPlannedInnovationNew.setProjectInnovation(projectInnovation);
        flagshipProgressPlannedInnovationNew.setReportSynthesisFlagshipProgress(reportSynthesisFlagshipProgressDB);

        List<ReportSynthesisFlagshipProgressInnovation> reportSynthesisFlagshipProgressInnovations =
          reportSynthesisFlagshipProgressDB.getReportSynthesisFlagshipProgressInnovations().stream()
            .filter(rio -> rio.isActive()).collect(Collectors.toList());


        if (!reportSynthesisFlagshipProgressInnovations.contains(flagshipProgressPlannedInnovationNew)) {
          flagshipProgressPlannedInnovationNew = reportSynthesisFlagshipProgressInnovationManager
            .saveReportSynthesisFlagshipProgressInnovation(flagshipProgressPlannedInnovationNew);
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


  public List<ReportSynthesisInnovationsByStageDTO> getInnovationsByStageDTO() {
    return innovationsByStageDTO;
  }


  public List<ReportSynthesisInnovationsByTypeDTO> getInnovationsByTypeDTO() {
    return innovationsByTypeDTO;
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


  public List<ProjectInnovation> getProjectInnovations() {
    return projectInnovations;
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

      this.fillprojectInnovationsList(phase.getId(), liaisonInstitution);

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


        reportSynthesis.getReportSynthesisFlagshipProgress().setProjectInnovations(new ArrayList<>());
        if (reportSynthesis.getReportSynthesisFlagshipProgress().getReportSynthesisFlagshipProgressInnovations() != null
          && !reportSynthesis.getReportSynthesisFlagshipProgress().getReportSynthesisFlagshipProgressInnovations()
            .isEmpty()) {
          for (ReportSynthesisFlagshipProgressInnovation flagshipProgressInnovation : reportSynthesis
            .getReportSynthesisFlagshipProgress().getReportSynthesisFlagshipProgressInnovations().stream()
            .filter(ro -> ro.isActive()).collect(Collectors.toList())) {
            reportSynthesis.getReportSynthesisFlagshipProgress().getProjectInnovations()
              .add(flagshipProgressInnovation.getProjectInnovation());
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
    List<ProjectInnovation> selectedProjectInnovations = new ArrayList<ProjectInnovation>();
    if (projectInnovations != null && !projectInnovations.isEmpty()) {
      projectInnovations.sort((p1, p2) -> p1.getId().compareTo(p2.getId()));
      selectedProjectInnovations.addAll(projectInnovations);
      // Remove unchecked innovations
      if (reportSynthesis.getReportSynthesisFlagshipProgress().getProjectInnovations() != null
        && !reportSynthesis.getReportSynthesisFlagshipProgress().getProjectInnovations().isEmpty()) {
        for (ProjectInnovation projectInnovation : reportSynthesis.getReportSynthesisFlagshipProgress()
          .getProjectInnovations()) {
          selectedProjectInnovations.remove(projectInnovation);
        }
      }
      // Chart: Innovations by stage
      innovationsByStageDTO = repIndStageInnovationManager.getInnovationsByStageDTO(selectedProjectInnovations, phase);
      // Chart: Innovations by type
      innovationsByTypeDTO = repIndInnovationTypeManager.getInnovationsByTypeDTO(selectedProjectInnovations, phase);
    }

    // Base Permission
    String params[] = {loggedCrp.getAcronym(), reportSynthesis.getId() + ""};
    this.setBasePermission(this.getText(Permission.REPORT_SYNTHESIS_FLAGSHIP_PROGRESS_BASE_PERMISSION, params));

    if (this.isHttpPost()) {
      if (reportSynthesis.getReportSynthesisFlagshipProgress().getPlannedInnovations() != null) {
        reportSynthesis.getReportSynthesisFlagshipProgress().getPlannedInnovations().clear();
      }
    }

  }


  @Override
  public String save() {
    if (this.hasPermission("canEdit")) {

      ReportSynthesisFlagshipProgress reportSynthesisFlagshipProgressDB =
        reportSynthesisManager.getReportSynthesisById(synthesisID).getReportSynthesisFlagshipProgress();

      this.flagshipProgressprojectInnovationsNewData(reportSynthesisFlagshipProgressDB);

      if (reportSynthesis.getReportSynthesisFlagshipProgress().getPlannedInnovations() == null) {
        reportSynthesis.getReportSynthesisFlagshipProgress().setPlannedInnovations(new ArrayList<>());
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

  public void setInnovationsByStageDTO(List<ReportSynthesisInnovationsByStageDTO> innovationsByStageDTO) {
    this.innovationsByStageDTO = innovationsByStageDTO;
  }

  public void setInnovationsByTypeDTO(List<ReportSynthesisInnovationsByTypeDTO> innovationsByTypeDTO) {
    this.innovationsByTypeDTO = innovationsByTypeDTO;
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


  public void setProjectInnovations(List<ProjectInnovation> projectInnovations) {
    this.projectInnovations = projectInnovations;
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
      validator.validate(this, reportSynthesis, true);
    }
  }
}