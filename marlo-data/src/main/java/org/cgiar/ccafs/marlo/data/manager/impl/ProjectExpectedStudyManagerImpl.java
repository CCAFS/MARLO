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
package org.cgiar.ccafs.marlo.data.manager.impl;


import org.cgiar.ccafs.marlo.data.dao.ProjectExpectedStudyDAO;
import org.cgiar.ccafs.marlo.data.dto.StudyHomeDTO;
import org.cgiar.ccafs.marlo.data.manager.CrpProgramManager;
import org.cgiar.ccafs.marlo.data.manager.LiaisonInstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectFocusManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisManager;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProgramType;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudy;
import org.cgiar.ccafs.marlo.data.model.ProjectFocus;
import org.cgiar.ccafs.marlo.data.model.RepIndOrganizationType;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesis;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgressStudy;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgressStudyDTO;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisStudiesByCrpProgramDTO;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author Christian Garcia
 */
@Named
public class ProjectExpectedStudyManagerImpl implements ProjectExpectedStudyManager {


  private ProjectExpectedStudyDAO projectExpectedStudyDAO;
  private CrpProgramManager crpProgramManager;
  private ProjectFocusManager projectFocusManager;
  private ProjectManager projectManager;
  private ReportSynthesisManager reportSynthesisManager;
  private PhaseManager phaseManager;
  private LiaisonInstitutionManager liaisonInstitutionManager;


  @Inject
  public ProjectExpectedStudyManagerImpl(ProjectExpectedStudyDAO projectExpectedStudyDAO,
    CrpProgramManager crpProgramManager, ProjectFocusManager projectFocusManager, ProjectManager projectManager,
    ReportSynthesisManager reportSynthesisManager, PhaseManager phaseManager,
    LiaisonInstitutionManager liaisonInstitutionManager) {
    this.projectExpectedStudyDAO = projectExpectedStudyDAO;
    this.crpProgramManager = crpProgramManager;
    this.projectFocusManager = projectFocusManager;
    this.projectManager = projectManager;
    this.reportSynthesisManager = reportSynthesisManager;
    this.phaseManager = phaseManager;
    this.liaisonInstitutionManager = liaisonInstitutionManager;
  }

  @Override
  public void deleteProjectExpectedStudy(long projectExpectedStudyId) {
    projectExpectedStudyDAO.deleteProjectExpectedStudy(projectExpectedStudyId);

  }


  @Override
  public boolean existProjectExpectedStudy(long projectExpectedStudyID) {
    return projectExpectedStudyDAO.existProjectExpectedStudy(projectExpectedStudyID);
  }


  @Override
  public List<ProjectExpectedStudy> findAll() {
    return projectExpectedStudyDAO.findAll();
  }

  @Override
  public List<ProjectExpectedStudy> getAllStudiesByPhase(Long phaseId) {
    return this.projectExpectedStudyDAO.getAllStudiesByPhase(phaseId.longValue());
  }

  /**
   * Method to get the list of studies selected by flagships
   * 
   * @param flagshipsLiaisonInstitutions
   * @return
   */
  public List<ReportSynthesisFlagshipProgressStudyDTO> getFpPlannedList(
    List<LiaisonInstitution> flagshipsLiaisonInstitutions, Phase phase, LiaisonInstitution pmuInstitution) {
    List<ReportSynthesisFlagshipProgressStudyDTO> flagshipPlannedList = new ArrayList<>();

    if (this.findAll() != null) {

      // Get global unit studies
      List<ProjectExpectedStudy> projectExpectedStudies =
        new ArrayList<>(
          this.findAll().stream()
            .filter(
              ps -> ps.isActive() && ps.getProjectExpectedStudyInfo(phase) != null
                && ps.getProjectExpectedStudyInfo().getStudyType() != null
                && ps.getProjectExpectedStudyInfo().getStudyType().getId() == 1
                && ps.getProjectExpectedStudyInfo().getYear() != null
                && ps.getProjectExpectedStudyInfo().getYear().equals(phase.getYear()) && ps.getProject() != null
                && ps.getProject().getGlobalUnitProjects().stream().filter(
                  gup -> gup.isActive() && gup.isOrigin() && gup.getGlobalUnit().getId().equals(phase.getCrp().getId()))
                  .collect(Collectors.toList()).size() > 0)
            .collect(Collectors.toList()));

      // Fill all project studies of the global unit
      for (ProjectExpectedStudy projectExpectedStudy : projectExpectedStudies) {
        ReportSynthesisFlagshipProgressStudyDTO dto = new ReportSynthesisFlagshipProgressStudyDTO();
        projectExpectedStudy.getProject().setProjectInfo(projectExpectedStudy.getProject().getProjecInfoPhase(phase));
        dto.setProjectExpectedStudy(projectExpectedStudy);
        if (projectExpectedStudy.getProject().getProjectInfo().getAdministrative() != null
          && projectExpectedStudy.getProject().getProjectInfo().getAdministrative()) {
          dto.setLiaisonInstitutions(new ArrayList<>());
          dto.getLiaisonInstitutions().add(pmuInstitution);
        } else {
          List<ProjectFocus> projectFocuses =
            new ArrayList<>(projectExpectedStudy.getProject().getProjectFocuses().stream()
              .filter(pf -> pf.isActive() && pf.getPhase().getId().equals(phase.getId())).collect(Collectors.toList()));
          List<LiaisonInstitution> liaisonInstitutions = new ArrayList<>();
          for (ProjectFocus projectFocus : projectFocuses) {
            liaisonInstitutions.addAll(projectFocus.getCrpProgram().getLiaisonInstitutions().stream()
              .filter(li -> li.isActive() && li.getCrpProgram() != null
                && li.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue()
                && li.getCrp() != null && li.getCrp().equals(phase.getCrp()))
              .collect(Collectors.toList()));
          }
          dto.setLiaisonInstitutions(liaisonInstitutions);
        }

        flagshipPlannedList.add(dto);
      }

      // Get supplementary studies
      List<ProjectExpectedStudy> projectSupplementaryStudies = new ArrayList<>(this.findAll().stream()
        .filter(ps -> ps.isActive() && ps.getProjectExpectedStudyInfo(phase) != null && ps.getProject() == null
          && ps.getProjectExpectedStudyInfo().getStudyType() != null
          && ps.getProjectExpectedStudyInfo().getStudyType().getId() == 1
          && ps.getProjectExpectedStudyInfo().getYear() != null
          && ps.getProjectExpectedStudyInfo().getYear().equals(phase.getYear()))
        .collect(Collectors.toList()));

      // Fill all supplementary studies
      for (ProjectExpectedStudy projectExpectedStudy : projectSupplementaryStudies) {
        ReportSynthesisFlagshipProgressStudyDTO dto = new ReportSynthesisFlagshipProgressStudyDTO();
        dto.setProjectExpectedStudy(projectExpectedStudy);
        dto.setLiaisonInstitutions(new ArrayList<>());
        dto.getLiaisonInstitutions().add(pmuInstitution);
        flagshipPlannedList.add(dto);
      }

      // Get deleted studies
      List<ReportSynthesisFlagshipProgressStudy> flagshipProgressStudies = new ArrayList<>();
      for (LiaisonInstitution liaisonInstitution : flagshipsLiaisonInstitutions) {
        ReportSynthesis reportSynthesis =
          reportSynthesisManager.findSynthesis(phase.getId(), liaisonInstitution.getId());
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
            reportSynthesisManager.findSynthesis(phase.getId(), liaisonInstitution.getId());
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

  @Override
  public ProjectExpectedStudy getProjectExpectedStudyById(long projectExpectedStudyID) {
    return projectExpectedStudyDAO.find(projectExpectedStudyID);
  }


  @Override
  public List<ProjectExpectedStudy> getProjectStudiesList(LiaisonInstitution liaisonInstitution, Phase phase) {
    Phase phaseDB = phaseManager.getPhaseById(phase.getId());
    List<ProjectExpectedStudy> projectExpectedStudies = new ArrayList<>();
    if (crpProgramManager.isFlagship(liaisonInstitution)) {
      // Fill Project Expected Studies of the current flagship
      if (projectFocusManager.findAll() != null) {
        List<ProjectFocus> projectFocus = new ArrayList<>(projectFocusManager.findAll().stream()
          .filter(pf -> pf.isActive() && pf.getCrpProgram().getId().equals(liaisonInstitution.getCrpProgram().getId())
            && pf.getPhase() != null && pf.getPhase().getId().equals(phaseDB.getId()))
          .collect(Collectors.toList()));

        for (ProjectFocus focus : projectFocus) {
          Project project = projectManager.getProjectById(focus.getProject().getId());
          List<ProjectExpectedStudy> plannedProjectExpectedStudies =
            new ArrayList<>(project.getProjectExpectedStudies().stream()
              .filter(ps -> ps.isActive() && ps.getProjectExpectedStudyInfo(phaseDB) != null
                && ps.getProjectExpectedStudyInfo().getStudyType() != null
                && ps.getProjectExpectedStudyInfo().getStudyType().getId() == 1
                && ps.getProjectExpectedStudyInfo().getYear() != null
                && ps.getProjectExpectedStudyInfo().getYear().equals(phaseDB.getYear()))
              .collect(Collectors.toList()));

          for (ProjectExpectedStudy projectExpectedStudy : plannedProjectExpectedStudies) {
            projectExpectedStudy.getProjectExpectedStudyInfo(phaseDB);
            projectExpectedStudy.setSrfTargets(projectExpectedStudy.getSrfTargets(phaseDB));
            projectExpectedStudy.setSubIdos(projectExpectedStudy.getSubIdos(phaseDB));
            projectExpectedStudies.add(projectExpectedStudy);
          }
        }
      }
    } else {
      // Fill Project Expected Studies of the PMU, removing flagship deletions
      List<LiaisonInstitution> liaisonInstitutions = phaseDB.getCrp().getLiaisonInstitutions().stream()
        .filter(c -> c.getCrpProgram() != null && c.isActive()
          && c.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue())
        .collect(Collectors.toList());
      liaisonInstitutions.sort(Comparator.comparing(LiaisonInstitution::getAcronym));

      List<ReportSynthesisFlagshipProgressStudyDTO> flagshipPlannedList =
        this.getFpPlannedList(liaisonInstitutions, phaseDB, liaisonInstitution);

      for (ReportSynthesisFlagshipProgressStudyDTO reportSynthesisFlagshipProgressStudyDTO : flagshipPlannedList) {

        ProjectExpectedStudy projectExpectedStudy = reportSynthesisFlagshipProgressStudyDTO.getProjectExpectedStudy();
        projectExpectedStudy.getProjectExpectedStudyInfo(phaseDB);
        projectExpectedStudy.setSrfTargets(projectExpectedStudy.getSrfTargets(phaseDB));
        projectExpectedStudy.setSubIdos(projectExpectedStudy.getSubIdos(phaseDB));
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
    if (projectExpectedStudies != null && !projectExpectedStudies.isEmpty()) {
      projectExpectedStudies.sort((p1, p2) -> p1.getId().compareTo(p2.getId()));
    }
    return projectExpectedStudies;
  }

  @Override
  public List<ReportSynthesisStudiesByCrpProgramDTO> getProjectStudiesListByFP(
    List<LiaisonInstitution> liaisonInstitutions, List<ProjectExpectedStudy> selectedStudies, Phase phase) {
    Phase phaseDB = phaseManager.getPhaseById(phase.getId());
    List<ReportSynthesisStudiesByCrpProgramDTO> reportSynthesisStudiesByCrpProgramDTOs = new ArrayList<>();

    for (LiaisonInstitution liaison : liaisonInstitutions) {
      List<ProjectExpectedStudy> projectExpectedStudies = new ArrayList<>();
      if (crpProgramManager.isFlagship(liaison)) {
        // Fill Project Expected Studies of the current flagship
        if (projectFocusManager.findAll() != null) {
          List<ProjectFocus> projectFocus =
            new ArrayList<>(projectFocusManager.findAll().stream()
              .filter(pf -> pf.isActive() && pf.getCrpProgram().getId().equals(liaison.getCrpProgram().getId())
                && pf.getPhase() != null && pf.getPhase().getId().equals(phaseDB.getId()))
              .collect(Collectors.toList()));

          for (ProjectFocus focus : projectFocus) {
            Project project = projectManager.getProjectById(focus.getProject().getId());
            List<ProjectExpectedStudy> plannedProjectExpectedStudies = new ArrayList<>(project
              .getProjectExpectedStudies().stream()
              .filter(ps -> ps.isActive() && ps.getProjectExpectedStudyInfo(phaseDB) != null
                && ps.getProjectExpectedStudyInfo().getStudyType() != null
                && ps.getProjectExpectedStudyInfo().getStudyType().getId() == 1
                && ps.getProjectExpectedStudyInfo().getYear() != null
                && ps.getProjectExpectedStudyInfo().getYear().equals(phaseDB.getYear()))
              .collect(Collectors.toList()));

            for (ProjectExpectedStudy projectExpectedStudy : plannedProjectExpectedStudies) {
              if (selectedStudies.contains(projectExpectedStudy)) {
                projectExpectedStudy.getProjectExpectedStudyInfo(phaseDB);
                projectExpectedStudy.setSrfTargets(projectExpectedStudy.getSrfTargets(phaseDB));
                projectExpectedStudy.setSubIdos(projectExpectedStudy.getSubIdos(phaseDB));
                projectExpectedStudies.add(projectExpectedStudy);
              }
            }
          }
        }

        ReportSynthesisStudiesByCrpProgramDTO studiesByCrpProgramDTO = new ReportSynthesisStudiesByCrpProgramDTO();
        studiesByCrpProgramDTO.setProjectStudies(projectExpectedStudies);
        studiesByCrpProgramDTO.setCrpProgram(liaison.getCrpProgram());

        reportSynthesisStudiesByCrpProgramDTOs.add(studiesByCrpProgramDTO);
      }
    }

    return reportSynthesisStudiesByCrpProgramDTOs;
  }

  @Override
  public List<ProjectExpectedStudy> getStudiesByOrganizationType(RepIndOrganizationType repIndOrganizationType,
    Phase phase) {
    return projectExpectedStudyDAO.getStudiesByOrganizationType(repIndOrganizationType, phase);
  }


  @Override
  public List<ProjectExpectedStudy> getStudiesByPhase(Phase phase) {
    return projectExpectedStudyDAO.getStudiesByPhase(phase);
  }

  @Override
  public List<StudyHomeDTO> getStudiesByProjectAndPhaseHome(Long phaseId, Long projectId) {
    return this.projectExpectedStudyDAO.getStudiesByProjectAndPhaseHome(phaseId.longValue(), projectId.longValue());
  }

  @Override
  public List<ProjectExpectedStudy> getUserStudies(long userId, String crp) {

    List<ProjectExpectedStudy> projects = new ArrayList<>();

    List<Map<String, Object>> view = projectExpectedStudyDAO.getUserStudies(userId, crp);

    if (view != null) {
      for (Map<String, Object> map : view) {
        projects.add(this.getProjectExpectedStudyById((Long.parseLong(map.get("project_id").toString()))));
      }
    }


    return projects;
  }

  @Override
  public Boolean isStudyExcluded(Long projectExpectedStudyId, Long phaseId, Long typeStudy) {

    return projectExpectedStudyDAO.isStudyExcluded(projectExpectedStudyId, phaseId, typeStudy);
  }

  @Override
  public ProjectExpectedStudy save(ProjectExpectedStudy projectExpectedStudy, String section,
    List<String> relationsName, Phase phase) {
    return projectExpectedStudyDAO.save(projectExpectedStudy, section, relationsName, phase);
  }

  @Override
  public ProjectExpectedStudy saveProjectExpectedStudy(ProjectExpectedStudy projectExpectedStudy) {
    return projectExpectedStudyDAO.save(projectExpectedStudy);
  }


}
