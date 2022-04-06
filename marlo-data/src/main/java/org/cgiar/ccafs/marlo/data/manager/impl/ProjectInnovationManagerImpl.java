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


import org.cgiar.ccafs.marlo.data.dao.ProjectInnovationDAO;
import org.cgiar.ccafs.marlo.data.dto.InnovationHomeDTO;
import org.cgiar.ccafs.marlo.data.manager.CrpProgramManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyInnovationManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectFocusManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationCountryManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationRegionManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisManager;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProgramType;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyInnovation;
import org.cgiar.ccafs.marlo.data.model.ProjectFocus;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovation;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationCountry;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationRegion;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesis;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgressInnovation;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgressInnovationDTO;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.collections4.ListUtils;

/**
 * @author Christian Garcia
 */
@Named
public class ProjectInnovationManagerImpl implements ProjectInnovationManager {


  private ProjectInnovationDAO projectInnovationDAO;
  private PhaseManager phaseManager;
  private CrpProgramManager crpProgramManager;
  private ProjectFocusManager projectFocusManager;
  private ProjectManager projectManager;
  private ReportSynthesisManager reportSynthesisManager;
  private ProjectInnovationCountryManager projectInnovationCountryManager;
  private ProjectInnovationRegionManager projectInnovationRegionManager;
  private ProjectExpectedStudyInnovationManager projectExpectedStudyInnovationManager;

  @Inject
  public ProjectInnovationManagerImpl(ProjectInnovationDAO projectInnovationDAO, PhaseManager phaseManager,
    CrpProgramManager crpProgramManager, ProjectFocusManager projectFocusManager, ProjectManager projectManager,
    ReportSynthesisManager reportSynthesisManager, ProjectInnovationCountryManager projectInnovationCountryManager,
    ProjectInnovationRegionManager projectInnovationRegionManager,
    ProjectExpectedStudyInnovationManager projectExpectedStudyInnovationManager) {
    this.projectInnovationDAO = projectInnovationDAO;
    this.phaseManager = phaseManager;
    this.crpProgramManager = crpProgramManager;
    this.projectFocusManager = projectFocusManager;
    this.projectManager = projectManager;
    this.reportSynthesisManager = reportSynthesisManager;
    this.projectInnovationCountryManager = projectInnovationCountryManager;
    this.projectInnovationRegionManager = projectInnovationRegionManager;
    this.projectExpectedStudyInnovationManager = projectExpectedStudyInnovationManager;
  }

  @Override
  public void deleteProjectInnovation(long projectInnovationId) {

    projectInnovationDAO.deleteProjectInnovation(projectInnovationId);
  }

  @Override
  public boolean existProjectInnovation(long projectInnovationID) {

    return projectInnovationDAO.existProjectInnovation(projectInnovationID);
  }

  private void fillBasicInfo(ProjectInnovation innovation, Phase phase) {
    // Setup Geographic Scope
    if (innovation.getProjectInnovationGeographicScopes() != null) {
      innovation.setGeographicScopes(new ArrayList<>(innovation.getProjectInnovationGeographicScopes().stream()
        .filter(o -> o.isActive() && o.getPhase().getId().equals(phase.getId())).collect(Collectors.toList())));
    }

    // Innovation Countries List
    if (innovation.getProjectInnovationCountries() == null) {
      innovation.setCountries(new ArrayList<>());
    } else {
      List<ProjectInnovationCountry> countries =
        projectInnovationCountryManager.getInnovationCountrybyPhase(innovation.getId(), phase.getId());
      innovation.setCountries(countries);
    }

    if (innovation.getProjectInnovationRegions() == null) {
      innovation.setRegions(new ArrayList<>());
    } else {
      List<ProjectInnovationRegion> geographics =
        projectInnovationRegionManager.getInnovationRegionbyPhase(innovation.getId(), phase.getId());

      // Load Regions
      innovation.setRegions(geographics.stream().filter(sc -> sc.getLocElement().getLocElementType().getId() == 1)
        .collect(Collectors.toList()));
    }

    // Innovation Contributing organizations List
    if (innovation.getProjectInnovationContributingOrganization() != null) {
      innovation.setContributingOrganizations(new ArrayList<>(innovation.getProjectInnovationContributingOrganization()
        .stream().filter(d -> d.getPhase().getId().equals(phase.getId()))
        .sorted((o1, o2) -> o1.getInstitution().getComposedName().compareTo(o2.getInstitution().getComposedName()))
        .collect(Collectors.toList())));
    }

    // studies
    if (innovation.getProjectExpectedStudyInnovations() == null) {
      innovation.setCountries(new ArrayList<>());
    } else {
      List<ProjectExpectedStudyInnovation> expectedStudyInnovations = ListUtils
        .emptyIfNull(projectExpectedStudyInnovationManager.getAllStudyInnovationsByInnovation(innovation.getId()))
        .stream().filter(ei -> ei != null && ei.getId() != null && ei.isActive() && ei.getPhase() != null
          && ei.getPhase().getId() != null && ei.getPhase().equals(phase))
        .collect(Collectors.toList());
      innovation.setStudies(expectedStudyInnovations);
    }

  }

  @Override
  public List<ProjectInnovation> findAll() {

    return projectInnovationDAO.findAll();

  }

  private List<ReportSynthesisFlagshipProgressInnovationDTO> getFpPlannedList(
    List<LiaisonInstitution> flagshipsLiaisonInstitutions, Phase phaseDB, LiaisonInstitution pmuInstitution) {

    List<ReportSynthesisFlagshipProgressInnovationDTO> flagshipPlannedList = new ArrayList<>();

    if (this.findAll() != null) {
      // Get global unit Innovations
      List<ProjectInnovation> projectInnovations =
        new ArrayList<>(
          this.findAll().stream()
            .filter(ps -> ps.isActive() && ps.getProjectInnovationInfo(phaseDB) != null
              && ps.getProjectInnovationInfo().getYear() != null
              && ps.getProjectInnovationInfo().getYear().intValue() == phaseDB.getYear() && ps.getProject() != null
              && ps.getProject().getGlobalUnitProjects().stream().filter(
                gup -> gup.isActive() && gup.isOrigin() && gup.getGlobalUnit().getId().equals(phaseDB.getCrp().getId()))
                .collect(Collectors.toList()).size() > 0)
            .collect(Collectors.toList()));

      // Fill all project Innovations of the global unit
      for (ProjectInnovation projectInnovation : projectInnovations) {
        ReportSynthesisFlagshipProgressInnovationDTO dto = new ReportSynthesisFlagshipProgressInnovationDTO();
        projectInnovation.getProject().setProjectInfo(projectInnovation.getProject().getProjecInfoPhase(phaseDB));
        dto.setProjectInnovation(projectInnovation);
        if (projectInnovation.getProject().getProjectInfo().getAdministrative() != null
          && projectInnovation.getProject().getProjectInfo().getAdministrative()) {
          dto.setLiaisonInstitutions(new ArrayList<>());
          dto.getLiaisonInstitutions().add(pmuInstitution);
        } else {
          List<ProjectFocus> projectFocuses = new ArrayList<>(projectInnovation.getProject().getProjectFocuses()
            .stream().filter(pf -> pf.isActive() && pf.getPhase().getId().equals(phaseDB.getId()))
            .collect(Collectors.toList()));
          List<LiaisonInstitution> liaisonInstitutions = new ArrayList<>();
          for (ProjectFocus projectFocus : projectFocuses) {
            liaisonInstitutions.addAll(projectFocus.getCrpProgram().getLiaisonInstitutions().stream()
              .filter(li -> li.isActive() && li.getCrpProgram() != null
                && li.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue()
                && li.getCrp() != null && li.getCrp().equals(phaseDB.getCrp()))
              .collect(Collectors.toList()));
          }
          dto.setLiaisonInstitutions(liaisonInstitutions);
        }

        flagshipPlannedList.add(dto);
      }

      // Get deleted innovations
      List<ReportSynthesisFlagshipProgressInnovation> flagshipProgressInnovations = new ArrayList<>();
      for (LiaisonInstitution liaisonInstitution : flagshipsLiaisonInstitutions) {
        ReportSynthesis reportSynthesis =
          reportSynthesisManager.findSynthesis(phaseDB.getId(), liaisonInstitution.getId());
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
          ReportSynthesis reportSynthesis =
            reportSynthesisManager.findSynthesis(phaseDB.getId(), liaisonInstitution.getId());
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

  @Override
  public List<ProjectInnovation> getInnovationsByPhase(Phase phase) {
    return this.projectInnovationDAO.getInnovationsByPhase(phase);
  }

  @Override
  public List<InnovationHomeDTO> getInnovationsByProjectAndPhaseHome(Long phaseId, Long projectId) {
    return this.projectInnovationDAO.getInnovationsByProjectAndPhaseHome(phaseId.longValue(), projectId.longValue());
  }

  @Override
  public ProjectInnovation getProjectInnovationById(long projectInnovationID) {

    return projectInnovationDAO.find(projectInnovationID);
  }

  @Override
  public List<ProjectInnovation> getProjectInnovationsList(LiaisonInstitution liaisonInstitution, Phase phase) {
    Phase phaseDB = phaseManager.getPhaseById(phase.getId());
    List<ProjectInnovation> projectInnovations = new ArrayList<>();
    if (crpProgramManager.isFlagship(liaisonInstitution)) {
      // Fill Project Innovations of the current flagship
      if (projectFocusManager.findAll() != null) {
        List<ProjectFocus> projectFocus = new ArrayList<>(projectFocusManager.findAll().stream()
          .filter(pf -> pf.isActive() && pf.getCrpProgram().getId().equals(liaisonInstitution.getCrpProgram().getId())
            && pf.getPhase() != null && pf.getPhase().getId().equals(phaseDB.getId()))
          .collect(Collectors.toList()));

        for (ProjectFocus focus : projectFocus) {
          Project project = projectManager.getProjectById(focus.getProject().getId());
          List<ProjectInnovation> plannedprojectInnovations = new ArrayList<>(project.getProjectInnovations().stream()
            .filter(ps -> ps.isActive() && ps.getProjectInnovationInfo(phaseDB) != null
              && ps.getProjectInnovationInfo().getYear() != null
              && ps.getProjectInnovationInfo().getYear().intValue() == phaseDB.getYear())
            .collect(Collectors.toList()));

          for (ProjectInnovation projectInnovation : plannedprojectInnovations) {
            projectInnovation.getProjectInnovationInfo(phaseDB);
            this.fillBasicInfo(projectInnovation, phaseDB);
            projectInnovations.add(projectInnovation);
          }
        }
      }
    } else {
      // Fill Project Innovations of the PMU, removing flagship deletions
      List<LiaisonInstitution> liaisonInstitutions = phaseDB.getCrp().getLiaisonInstitutions().stream()
        .filter(c -> c.getCrpProgram() != null && c.isActive()
          && c.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue())
        .collect(Collectors.toList());
      liaisonInstitutions.sort(Comparator.comparing(LiaisonInstitution::getAcronym));

      List<ReportSynthesisFlagshipProgressInnovationDTO> flagshipPlannedList =
        this.getFpPlannedList(liaisonInstitutions, phaseDB, liaisonInstitution);

      for (ReportSynthesisFlagshipProgressInnovationDTO reportSynthesisFlagshipProgressInnovationDTO : flagshipPlannedList) {

        ProjectInnovation projectInnovation = reportSynthesisFlagshipProgressInnovationDTO.getProjectInnovation();
        projectInnovation.getProjectInnovationInfo(phaseDB);
        this.fillBasicInfo(projectInnovation, phaseDB);

        projectInnovation.setSelectedFlahsgips(new ArrayList<>());
        // sort selected flagships
        if (reportSynthesisFlagshipProgressInnovationDTO.getLiaisonInstitutions() != null
          && !reportSynthesisFlagshipProgressInnovationDTO.getLiaisonInstitutions().isEmpty()) {
          reportSynthesisFlagshipProgressInnovationDTO.getLiaisonInstitutions()
            .sort((l1, l2) -> l1.getCrpProgram().getAcronym().compareTo(l2.getCrpProgram().getAcronym()));
        }
        projectInnovation.getSelectedFlahsgips()
          .addAll(reportSynthesisFlagshipProgressInnovationDTO.getLiaisonInstitutions());
        projectInnovation.setStudies(projectInnovation.getStudies(phaseDB));
        projectInnovations.add(projectInnovation);

      }
    }
    if (projectInnovations != null && !projectInnovations.isEmpty()) {
      projectInnovations.sort((p1, p2) -> p1.getId().compareTo(p2.getId()));
    }
    return projectInnovations;
  }

  @Override
  public List<ProjectInnovation> getProjectInnovationsNoSynthesisList(LiaisonInstitution liaisonInstitution,
    Phase phase) {
    Phase phaseDB = phaseManager.getPhaseById(phase.getId());
    List<ProjectInnovation> projectInnovations = new ArrayList<>();
    // Fill Project Innovations of the current flagship
    if (projectFocusManager.findAll() != null) {
      List<ProjectFocus> projectFocus = new ArrayList<>(projectFocusManager.findAll().stream()
        .filter(pf -> pf.isActive() && pf.getCrpProgram().getId().equals(liaisonInstitution.getCrpProgram().getId())
          && pf.getPhase() != null && pf.getPhase().getId().equals(phaseDB.getId()))
        .collect(Collectors.toList()));

      for (ProjectFocus focus : projectFocus) {
        Project project = projectManager.getProjectById(focus.getProject().getId());
        List<ProjectInnovation> plannedprojectInnovations = new ArrayList<>(project.getProjectInnovations().stream()
          .filter(ps -> ps.isActive() && ps.getProjectInnovationInfo(phaseDB) != null
            && ps.getProjectInnovationInfo().getYear() != null
            && ps.getProjectInnovationInfo().getYear().intValue() != phaseDB.getYear())
          .collect(Collectors.toList()));

        for (ProjectInnovation projectInnovation : plannedprojectInnovations) {
          projectInnovation.getProjectInnovationInfo(phaseDB);
          this.fillBasicInfo(projectInnovation, phaseDB);
          projectInnovations.add(projectInnovation);
        }
      }
    }
    if (projectInnovations != null && !projectInnovations.isEmpty()) {
      projectInnovations.sort((p1, p2) -> p1.getId().compareTo(p2.getId()));
    }
    return projectInnovations;
  }

  @Override
  public Boolean isInnovationExcluded(Long innovationId, Long phaseId) {

    return projectInnovationDAO.isInnovationExcluded(innovationId, phaseId);
  }

  @Override
  public ProjectInnovation saveProjectInnovation(ProjectInnovation projectInnovation) {
    return projectInnovationDAO.save(projectInnovation);
  }

  @Override
  public ProjectInnovation saveProjectInnovation(ProjectInnovation projectInnovation, String section,
    List<String> relationsName, Phase phase) {
    return projectInnovationDAO.save(projectInnovation, section, relationsName, phase);
  }
}
