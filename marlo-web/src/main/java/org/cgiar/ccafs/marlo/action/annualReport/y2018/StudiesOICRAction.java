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
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectFocusManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisFlagshipProgressManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisFlagshipProgressStudyManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisManager;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.LiaisonUser;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProgramType;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudy;
import org.cgiar.ccafs.marlo.data.model.ProjectFocus;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesis;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgress;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgressStudy;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgressStudyDTO;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.AutoSaveReader;
import org.cgiar.ccafs.marlo.validation.annualreport.y2018.StudiesOICR2018Validator;

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
public class StudiesOICRAction extends BaseAction {

  private static final long serialVersionUID = 8323800211228698584L;

  // Managers
  private GlobalUnitManager crpManager;
  private LiaisonInstitutionManager liaisonInstitutionManager;
  private ReportSynthesisManager reportSynthesisManager;
  private AuditLogManager auditLogManager;
  private CrpProgramManager crpProgramManager;
  private UserManager userManager;
  private StudiesOICR2018Validator validator;
  private ProjectExpectedStudyManager projectExpectedStudyManager;
  private ProjectFocusManager projectFocusManager;
  private ProjectManager projectManager;
  private ReportSynthesisFlagshipProgressManager reportSynthesisFlagshipProgressManager;
  private ReportSynthesisFlagshipProgressStudyManager reportSynthesisFlagshipProgressStudyManager;

  // Variables
  private String transaction;
  private ReportSynthesis reportSynthesis;
  private Long liaisonInstitutionID;
  private Long synthesisID;
  private LiaisonInstitution liaisonInstitution;
  private GlobalUnit loggedCrp;
  private List<LiaisonInstitution> liaisonInstitutions;
  private List<ProjectExpectedStudy> projectExpectedStudies;
  private Phase actualPhase;


  @Inject
  public StudiesOICRAction(APConfig config, GlobalUnitManager crpManager,
    LiaisonInstitutionManager liaisonInstitutionManager, ReportSynthesisManager reportSynthesisManager,
    AuditLogManager auditLogManager, UserManager userManager, StudiesOICR2018Validator validator,
    CrpProgramManager crpProgramManager, ProjectExpectedStudyManager projectExpectedStudyManager,
    ProjectFocusManager projectFocusManager, ProjectManager projectManager,
    ReportSynthesisFlagshipProgressManager reportSynthesisFlagshipProgressManager,
    ReportSynthesisFlagshipProgressStudyManager reportSynthesisFlagshipProgressStudyManager) {
    super(config);
    this.crpManager = crpManager;
    this.liaisonInstitutionManager = liaisonInstitutionManager;
    this.reportSynthesisManager = reportSynthesisManager;
    this.auditLogManager = auditLogManager;
    this.userManager = userManager;
    this.validator = validator;
    this.crpProgramManager = crpProgramManager;
    this.projectExpectedStudyManager = projectExpectedStudyManager;
    this.projectFocusManager = projectFocusManager;
    this.projectManager = projectManager;
    this.reportSynthesisFlagshipProgressManager = reportSynthesisFlagshipProgressManager;
    this.reportSynthesisFlagshipProgressStudyManager = reportSynthesisFlagshipProgressStudyManager;
  }


  /**
   * Method to fill the list of studies selected by flagships
   * 
   * @param flagshipsLiaisonInstitutions
   * @return
   */
  public List<ReportSynthesisFlagshipProgressStudyDTO>
    fillFpPlannedList(List<LiaisonInstitution> flagshipsLiaisonInstitutions) {
    List<ReportSynthesisFlagshipProgressStudyDTO> flagshipPlannedList = new ArrayList<>();

    if (projectExpectedStudyManager.findAll() != null) {

      // Get global unit studies
      List<ProjectExpectedStudy> projectExpectedStudies = new ArrayList<>(projectExpectedStudyManager.findAll().stream()
        .filter(ps -> ps.isActive() && ps.getProjectExpectedStudyInfo(actualPhase) != null
          && ps.getProjectExpectedStudyInfo().getStudyType() != null
          && ps.getProjectExpectedStudyInfo().getStudyType().getId() == 1
          && ps.getProjectExpectedStudyInfo().getYear() != null
          && ps.getProjectExpectedStudyInfo().getYear() == actualPhase.getYear() && ps.getProject() != null
          && ps.getProject().getGlobalUnitProjects().stream()
            .filter(gup -> gup.isActive() && gup.isOrigin() && gup.getGlobalUnit().getId().equals(loggedCrp.getId()))
            .collect(Collectors.toList()).size() > 0)
        .collect(Collectors.toList()));

      // Fill all project studies of the global unit
      for (ProjectExpectedStudy projectExpectedStudy : projectExpectedStudies) {
        ReportSynthesisFlagshipProgressStudyDTO dto = new ReportSynthesisFlagshipProgressStudyDTO();
        projectExpectedStudy.getProject()
          .setProjectInfo(projectExpectedStudy.getProject().getProjecInfoPhase(actualPhase));
        dto.setProjectExpectedStudy(projectExpectedStudy);
        if (projectExpectedStudy.getProject().getProjectInfo().getAdministrative() != null
          && projectExpectedStudy.getProject().getProjectInfo().getAdministrative()) {
          dto.setLiaisonInstitutions(new ArrayList<>());
          dto.getLiaisonInstitutions().add(this.liaisonInstitution);
        } else {
          List<ProjectFocus> projectFocuses = new ArrayList<>(projectExpectedStudy.getProject().getProjectFocuses()
            .stream().filter(pf -> pf.isActive() && pf.getPhase().getId() == actualPhase.getId())
            .collect(Collectors.toList()));
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

      // Get supplementary studies
      List<ProjectExpectedStudy> projectSupplementaryStudies =
        new ArrayList<>(projectExpectedStudyManager.findAll().stream()
          .filter(ps -> ps.isActive() && ps.getProjectExpectedStudyInfo(actualPhase) != null && ps.getProject() == null
            && ps.getProjectExpectedStudyInfo().getStudyType() != null
            && ps.getProjectExpectedStudyInfo().getStudyType().getId() == 1
            && ps.getProjectExpectedStudyInfo().getYear() != null
            && ps.getProjectExpectedStudyInfo().getYear() == actualPhase.getYear())
          .collect(Collectors.toList()));

      // Fill all supplementary studies
      for (ProjectExpectedStudy projectExpectedStudy : projectSupplementaryStudies) {
        ReportSynthesisFlagshipProgressStudyDTO dto = new ReportSynthesisFlagshipProgressStudyDTO();
        dto.setProjectExpectedStudy(projectExpectedStudy);
        dto.setLiaisonInstitutions(new ArrayList<>());
        dto.getLiaisonInstitutions().add(this.liaisonInstitution);
        flagshipPlannedList.add(dto);
      }

      // Get deleted studies
      List<ReportSynthesisFlagshipProgressStudy> flagshipProgressStudies = new ArrayList<>();
      for (LiaisonInstitution liaisonInstitution : flagshipsLiaisonInstitutions) {
        ReportSynthesis reportSynthesis =
          reportSynthesisManager.findSynthesis(actualPhase.getId(), liaisonInstitution.getId());
        if (reportSynthesis != null) {
          if (reportSynthesis.getReportSynthesisFlagshipProgress() != null) {
            if (reportSynthesis.getReportSynthesisFlagshipProgress()
              .getReportSynthesisFlagshipProgressStudies() != null) {
              List<ReportSynthesisFlagshipProgressStudy> studies = new ArrayList<>(
                reportSynthesis.getReportSynthesisFlagshipProgress().getReportSynthesisFlagshipProgressStudies()
                  .stream().filter(s -> s.isActive()).collect(Collectors.toList()));
              if (studies != null || !studies.isEmpty()) {
                for (ReportSynthesisFlagshipProgressStudy reportSynthesisFlagshipProgressStudy : studies) {
                  flagshipProgressStudies.add(reportSynthesisFlagshipProgressStudy);
                }
              }
            }
          }
        }
      }

      // Get list of studies to remove
      List<ReportSynthesisFlagshipProgressStudyDTO> removeList = new ArrayList<>();
      for (ReportSynthesisFlagshipProgressStudyDTO dto : flagshipPlannedList) {

        List<LiaisonInstitution> removeLiaison = new ArrayList<>();
        for (LiaisonInstitution liaisonInstitution : dto.getLiaisonInstitutions()) {
          ReportSynthesis reportSynthesis =
            reportSynthesisManager.findSynthesis(actualPhase.getId(), liaisonInstitution.getId());
          if (reportSynthesis != null) {
            if (reportSynthesis.getReportSynthesisFlagshipProgress() != null) {

              ReportSynthesisFlagshipProgressStudy flagshipProgressStudyNew =
                new ReportSynthesisFlagshipProgressStudy();
              flagshipProgressStudyNew = new ReportSynthesisFlagshipProgressStudy();
              flagshipProgressStudyNew.setProjectExpectedStudy(dto.getProjectExpectedStudy());
              flagshipProgressStudyNew
                .setReportSynthesisFlagshipProgress(reportSynthesis.getReportSynthesisFlagshipProgress());

              if (flagshipProgressStudies.contains(flagshipProgressStudyNew)) {
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

      // Remove studies unselected by flagships
      for (ReportSynthesisFlagshipProgressStudyDTO i : removeList) {
        flagshipPlannedList.remove(i);
      }

    }
    return flagshipPlannedList;
  }

  private void fillProjectStudiesList(LiaisonInstitution liaisonInstitution) {
    projectExpectedStudies = new ArrayList<>();
    if (this.isFlagship()) {
      // Fill Project Expected Studies of the current flagship
      if (projectFocusManager.findAll() != null) {
        List<ProjectFocus> projectFocus = new ArrayList<>(projectFocusManager.findAll().stream()
          .filter(pf -> pf.isActive() && pf.getCrpProgram().getId() == liaisonInstitution.getCrpProgram().getId()
            && pf.getPhase() != null && pf.getPhase().getId() == actualPhase.getId())
          .collect(Collectors.toList()));

        for (ProjectFocus focus : projectFocus) {
          Project project = projectManager.getProjectById(focus.getProject().getId());
          List<ProjectExpectedStudy> plannedProjectExpectedStudies =
            new ArrayList<>(project.getProjectExpectedStudies().stream()
              .filter(ps -> ps.isActive() && ps.getProjectExpectedStudyInfo(actualPhase) != null
                && ps.getProjectExpectedStudyInfo().getStudyType() != null
                && ps.getProjectExpectedStudyInfo().getStudyType().getId() == 1
                && ps.getProjectExpectedStudyInfo().getYear() != null
                && ps.getProjectExpectedStudyInfo().getYear() == actualPhase.getYear())
              .collect(Collectors.toList()));

          for (ProjectExpectedStudy projectExpectedStudy : plannedProjectExpectedStudies) {
            projectExpectedStudy.getProjectExpectedStudyInfo(actualPhase);
            projectExpectedStudy.setSrfTargets(projectExpectedStudy.getSrfTargets(actualPhase));
            projectExpectedStudy.setSubIdos(projectExpectedStudy.getSubIdos(actualPhase));
            projectExpectedStudies.add(projectExpectedStudy);
          }
        }
      }
    } else {
      // Fill Project Expected Studies of the PMU, removing flagship deletions
      liaisonInstitutions = loggedCrp.getLiaisonInstitutions().stream()
        .filter(c -> c.getCrpProgram() != null && c.isActive()
          && c.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue())
        .collect(Collectors.toList());
      liaisonInstitutions.sort(Comparator.comparing(LiaisonInstitution::getAcronym));

      List<ReportSynthesisFlagshipProgressStudyDTO> flagshipPlannedList = this.fillFpPlannedList(liaisonInstitutions);

      for (ReportSynthesisFlagshipProgressStudyDTO reportSynthesisFlagshipProgressStudyDTO : flagshipPlannedList) {

        ProjectExpectedStudy projectExpectedStudy = reportSynthesisFlagshipProgressStudyDTO.getProjectExpectedStudy();
        projectExpectedStudy.getProjectExpectedStudyInfo(actualPhase);
        projectExpectedStudy.setSrfTargets(projectExpectedStudy.getSrfTargets(actualPhase));
        projectExpectedStudy.setSubIdos(projectExpectedStudy.getSubIdos(actualPhase));
        projectExpectedStudy.setSelectedFlahsgips(new ArrayList<>());
        // sort selected flagships
        if (reportSynthesisFlagshipProgressStudyDTO.getLiaisonInstitutions() != null
          && !reportSynthesisFlagshipProgressStudyDTO.getLiaisonInstitutions().isEmpty()) {
          reportSynthesisFlagshipProgressStudyDTO.getLiaisonInstitutions()
            .sort((l1, l2) -> l1.getCrpProgram().getAcronym().compareTo(l2.getCrpProgram().getAcronym()));
        }
        projectExpectedStudy.getSelectedFlahsgips()
          .addAll(reportSynthesisFlagshipProgressStudyDTO.getLiaisonInstitutions());
        projectExpectedStudies.add(projectExpectedStudy);

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
    flagshipProgressProjectStudiesNewData(ReportSynthesisFlagshipProgress reportSynthesisFlagshipProgressDB) {

    List<Long> selectedPs = new ArrayList<>();
    List<Long> selectedStudiesIds = new ArrayList<>();

    for (ProjectExpectedStudy projectExpectedStudy : projectExpectedStudies) {
      selectedStudiesIds.add(projectExpectedStudy.getId());
    }

    // Add studies (active =0)
    if (reportSynthesis.getReportSynthesisFlagshipProgress().getStudiesValue() != null
      && reportSynthesis.getReportSynthesisFlagshipProgress().getStudiesValue().length() > 0) {
      List<Long> stList = new ArrayList<>();
      for (String string : reportSynthesis.getReportSynthesisFlagshipProgress().getStudiesValue().trim().split(",")) {
        stList.add(Long.parseLong(string.trim()));
      }

      for (Long studyId : selectedStudiesIds) {
        int index = stList.indexOf(studyId);
        if (index < 0) {
          selectedPs.add(studyId);
        }
      }

      for (ReportSynthesisFlagshipProgressStudy flagshipProgressStudy : reportSynthesisFlagshipProgressDB
        .getReportSynthesisFlagshipProgressStudies().stream().filter(rio -> rio.isActive())
        .collect(Collectors.toList())) {
        if (!selectedPs.contains(flagshipProgressStudy.getProjectExpectedStudy().getId())) {
          reportSynthesisFlagshipProgressStudyManager
            .deleteReportSynthesisFlagshipProgressStudy(flagshipProgressStudy.getId());
        }
      }

      for (Long studyId : selectedPs) {
        ProjectExpectedStudy projectExpectedStudy = projectExpectedStudyManager.getProjectExpectedStudyById(studyId);

        ReportSynthesisFlagshipProgressStudy flagshipProgressStudyNew = new ReportSynthesisFlagshipProgressStudy();

        flagshipProgressStudyNew.setProjectExpectedStudy(projectExpectedStudy);
        flagshipProgressStudyNew.setReportSynthesisFlagshipProgress(reportSynthesisFlagshipProgressDB);

        List<ReportSynthesisFlagshipProgressStudy> flagshipProgressStudies =
          reportSynthesisFlagshipProgressDB.getReportSynthesisFlagshipProgressStudies().stream()
            .filter(rio -> rio.isActive()).collect(Collectors.toList());


        if (!flagshipProgressStudies.contains(flagshipProgressStudyNew)) {
          flagshipProgressStudyNew = reportSynthesisFlagshipProgressStudyManager
            .saveReportSynthesisFlagshipProgressStudy(flagshipProgressStudyNew);
        }

      }
    } else {

      // Delete Studies (Save with active=1)
      for (Long studyId : selectedStudiesIds) {
        ProjectExpectedStudy projectExpectedStudy = projectExpectedStudyManager.getProjectExpectedStudyById(studyId);

        ReportSynthesisFlagshipProgressStudy flagshipProgressPlannedStudyNew =
          new ReportSynthesisFlagshipProgressStudy();

        flagshipProgressPlannedStudyNew.setProjectExpectedStudy(projectExpectedStudy);
        flagshipProgressPlannedStudyNew.setReportSynthesisFlagshipProgress(reportSynthesisFlagshipProgressDB);

        List<ReportSynthesisFlagshipProgressStudy> reportSynthesisFlagshipProgressStudies =
          reportSynthesisFlagshipProgressDB.getReportSynthesisFlagshipProgressStudies().stream()
            .filter(rio -> rio.isActive()).collect(Collectors.toList());


        if (!reportSynthesisFlagshipProgressStudies.contains(flagshipProgressPlannedStudyNew)) {
          flagshipProgressPlannedStudyNew = reportSynthesisFlagshipProgressStudyManager
            .saveReportSynthesisFlagshipProgressStudy(flagshipProgressPlannedStudyNew);
        }
      }
    }

  }


  private Path getAutoSaveFilePath() {
    String composedClassName = reportSynthesis.getClass().getSimpleName();
    String actionFile = this.getActionName().replace("/", "_");
    String autoSaveFile = reportSynthesis.getId() + "_" + composedClassName + "_" + actualPhase.getName() + "_"
      + actualPhase.getYear() + "_" + actionFile + ".json";
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


  public List<ProjectExpectedStudy> getProjectExpectedStudies() {
    return projectExpectedStudies;
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

    this.actualPhase = this.getActualPhase();

    // Get current CRP
    loggedCrp = (GlobalUnit) this.getSession().get(APConstants.SESSION_CRP);
    loggedCrp = crpManager.getGlobalUnitById(loggedCrp.getId());

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

        if (!reportSynthesis.getPhase().equals(actualPhase)) {
          reportSynthesis = reportSynthesisManager.findSynthesis(actualPhase.getId(), liaisonInstitutionID);
          if (reportSynthesis == null) {
            reportSynthesis = this.createReportSynthesis(actualPhase.getId(), liaisonInstitutionID);
          }
          synthesisID = reportSynthesis.getId();
        }
      } catch (Exception e) {
        reportSynthesis = reportSynthesisManager.findSynthesis(actualPhase.getId(), liaisonInstitutionID);
        if (reportSynthesis == null) {
          reportSynthesis = this.createReportSynthesis(actualPhase.getId(), liaisonInstitutionID);
        }
        synthesisID = reportSynthesis.getId();

      }
    }

    if (reportSynthesis != null) {

      ReportSynthesis reportSynthesisDB = reportSynthesisManager.getReportSynthesisById(synthesisID);
      synthesisID = reportSynthesisDB.getId();
      liaisonInstitutionID = reportSynthesisDB.getLiaisonInstitution().getId();
      liaisonInstitution = liaisonInstitutionManager.getLiaisonInstitutionById(liaisonInstitutionID);

      this.fillProjectStudiesList(liaisonInstitution);

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


        reportSynthesis.getReportSynthesisFlagshipProgress().setProjectStudies(new ArrayList<>());
        if (reportSynthesis.getReportSynthesisFlagshipProgress().getReportSynthesisFlagshipProgressStudies() != null
          && !reportSynthesis.getReportSynthesisFlagshipProgress().getReportSynthesisFlagshipProgressStudies()
            .isEmpty()) {
          for (ReportSynthesisFlagshipProgressStudy flagshipProgressStudy : reportSynthesis
            .getReportSynthesisFlagshipProgress().getReportSynthesisFlagshipProgressStudies().stream()
            .filter(ro -> ro.isActive()).collect(Collectors.toList())) {
            reportSynthesis.getReportSynthesisFlagshipProgress().getProjectStudies()
              .add(flagshipProgressStudy.getProjectExpectedStudy());
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

    if (projectExpectedStudies != null && !projectExpectedStudies.isEmpty()) {
      projectExpectedStudies.sort((p1, p2) -> p1.getId().compareTo(p2.getId()));
    }

    // Base Permission
    String params[] = {loggedCrp.getAcronym(), reportSynthesis.getId() + ""};
    this.setBasePermission(this.getText(Permission.REPORT_SYNTHESIS_FLAGSHIP_PROGRESS_BASE_PERMISSION, params));

    if (this.isHttpPost()) {
      if (reportSynthesis.getReportSynthesisFlagshipProgress().getPlannedStudies() != null) {
        reportSynthesis.getReportSynthesisFlagshipProgress().getPlannedStudies().clear();
      }
    }

  }


  @Override
  public String save() {
    if (this.hasPermission("canEdit")) {

      ReportSynthesisFlagshipProgress reportSynthesisFlagshipProgressDB =
        reportSynthesisManager.getReportSynthesisById(synthesisID).getReportSynthesisFlagshipProgress();

      this.flagshipProgressProjectStudiesNewData(reportSynthesisFlagshipProgressDB);

      if (reportSynthesis.getReportSynthesisFlagshipProgress().getPlannedStudies() == null) {
        reportSynthesis.getReportSynthesisFlagshipProgress().setPlannedStudies(new ArrayList<>());
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

      reportSynthesisManager.save(reportSynthesis, this.getActionName(), relationsName, actualPhase);

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


  public void setProjectExpectedStudies(List<ProjectExpectedStudy> projectExpectedStudies) {
    this.projectExpectedStudies = projectExpectedStudies;
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