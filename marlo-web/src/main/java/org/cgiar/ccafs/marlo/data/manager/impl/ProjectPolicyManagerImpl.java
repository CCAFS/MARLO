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


import org.cgiar.ccafs.marlo.data.dao.ProjectPolicyDAO;
import org.cgiar.ccafs.marlo.data.manager.CrpProgramManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectFocusManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPolicyManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisManager;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProgramType;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectFocus;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicy;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesis;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgressPolicy;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgressPolicyDTO;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ProjectPolicyManagerImpl implements ProjectPolicyManager {


  private ProjectPolicyDAO projectPolicyDAO;
  private PhaseManager phaseManager;
  private CrpProgramManager crpProgramManager;
  private ProjectFocusManager projectFocusManager;
  private ProjectManager projectManager;
  private ReportSynthesisManager reportSynthesisManager;


  @Inject
  public ProjectPolicyManagerImpl(ProjectPolicyDAO projectPolicyDAO, PhaseManager phaseManager,
    CrpProgramManager crpProgramManager, ProjectFocusManager projectFocusManager, ProjectManager projectManager,
    ReportSynthesisManager reportSynthesisManager) {
    this.projectPolicyDAO = projectPolicyDAO;
    this.phaseManager = phaseManager;
    this.crpProgramManager = crpProgramManager;
    this.projectFocusManager = projectFocusManager;
    this.projectManager = projectManager;
    this.reportSynthesisManager = reportSynthesisManager;
  }

  @Override
  public void deleteProjectPolicy(long projectPolicyId) {

    projectPolicyDAO.deleteProjectPolicy(projectPolicyId);
  }

  @Override
  public boolean existProjectPolicy(long projectPolicyID) {

    return projectPolicyDAO.existProjectPolicy(projectPolicyID);
  }

  @Override
  public List<ProjectPolicy> findAll() {

    return projectPolicyDAO.findAll();

  }

  /**
   * * Method to get the list of policies selected by flagships
   * 
   * @param flagshipsLiaisonInstitutions
   * @param phase
   * @param pmuInstitution
   * @return
   */
  private List<ReportSynthesisFlagshipProgressPolicyDTO> getFpPlannedList(
    List<LiaisonInstitution> flagshipsLiaisonInstitutions, Phase phase, LiaisonInstitution pmuInstitution) {
    List<ReportSynthesisFlagshipProgressPolicyDTO> flagshipPlannedList = new ArrayList<>();

    if (this.findAll() != null) {

      // Get global unit policies
      List<ProjectPolicy> projectPolicies = new ArrayList<>(this.findAll().stream().filter(ps -> ps.isActive()
        && ps.getProjectPolicyInfo(phase) != null && ps.getProjectPolicyInfo().isRequired() && ps.getProject() != null
        && ps.getProject().getGlobalUnitProjects().stream()
          .filter(gup -> gup.isActive() && gup.isOrigin() && gup.getGlobalUnit().getId().equals(phase.getCrp().getId()))
          .collect(Collectors.toList()).size() > 0)
        .collect(Collectors.toList()));

      // Fill all project policies of the global unit
      for (ProjectPolicy projectPolicy : projectPolicies) {
        ReportSynthesisFlagshipProgressPolicyDTO dto = new ReportSynthesisFlagshipProgressPolicyDTO();
        projectPolicy.getProject().setProjectInfo(projectPolicy.getProject().getProjecInfoPhase(phase));
        dto.setProjectPolicy(projectPolicy);
        if (projectPolicy.getProject().getProjectInfo().getAdministrative() != null
          && projectPolicy.getProject().getProjectInfo().getAdministrative()) {
          dto.setLiaisonInstitutions(new ArrayList<>());
          dto.getLiaisonInstitutions().add(pmuInstitution);
        } else {
          List<ProjectFocus> projectFocuses = new ArrayList<>(projectPolicy.getProject().getProjectFocuses().stream()
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

      // Get deleted policies
      List<ReportSynthesisFlagshipProgressPolicy> flagshipProgressPolicies = new ArrayList<>();
      for (LiaisonInstitution liaisonInstitution : flagshipsLiaisonInstitutions) {
        ReportSynthesis reportSynthesis =
          reportSynthesisManager.findSynthesis(phase.getId(), liaisonInstitution.getId());
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
          ReportSynthesis reportSynthesis =
            reportSynthesisManager.findSynthesis(phase.getId(), liaisonInstitution.getId());
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

  @Override
  public List<ProjectPolicy> getProjectPoliciesList(LiaisonInstitution liaisonInstitution, Phase phase) {
    List<ProjectPolicy> projectPolicies = new ArrayList<>();
    Phase phaseDB = phaseManager.getPhaseById(phase.getId());
    if (crpProgramManager.isFlagship(liaisonInstitution)) {
      // Fill Project policies of the current flagship
      if (projectFocusManager.findAll() != null) {
        List<ProjectFocus> projectFocus = new ArrayList<>(projectFocusManager.findAll().stream()
          .filter(pf -> pf.isActive() && pf.getCrpProgram().getId().equals(liaisonInstitution.getCrpProgram().getId())
            && pf.getPhase() != null && pf.getPhase().getId().equals(phaseDB.getId()))
          .collect(Collectors.toList()));

        for (ProjectFocus focus : projectFocus) {
          Project project = projectManager.getProjectById(focus.getProject().getId());
          List<ProjectPolicy> plannedProjectPolicies = new ArrayList<>(project.getProjectPolicies().stream()
            .filter(
              pp -> pp.isActive() && pp.getProjectPolicyInfo(phaseDB) != null && pp.getProjectPolicyInfo().isRequired())
            .collect(Collectors.toList()));

          for (ProjectPolicy projectPolicy : plannedProjectPolicies) {
            projectPolicy.getProjectPolicyInfo(phaseDB);
            projectPolicy.setSubIdos(projectPolicy.getSubIdos(phaseDB));
            projectPolicy.setCrossCuttingMarkers(projectPolicy.getCrossCuttingMarkers(phaseDB));
            projectPolicy.setOwners(projectPolicy.getOwners(phaseDB));
            projectPolicy.setGeographicScopes(projectPolicy.getGeographicScopes(phaseDB));
            projectPolicy.setRegions(projectPolicy.getRegions(phaseDB));
            projectPolicy.setCountries(projectPolicy.getCountries(phaseDB));
            projectPolicy.setEvidences(projectPolicy.getEvidences(phaseDB));
            projectPolicies.add(projectPolicy);
          }
        }
      }
    } else {
      // Fill Project policies of the PMU, removing flagship deletions
      List<LiaisonInstitution> liaisonInstitutions = phaseDB.getCrp().getLiaisonInstitutions().stream()
        .filter(c -> c.getCrpProgram() != null && c.isActive()
          && c.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue())
        .collect(Collectors.toList());
      liaisonInstitutions.sort(Comparator.comparing(LiaisonInstitution::getAcronym));

      List<ReportSynthesisFlagshipProgressPolicyDTO> flagshipPlannedList =
        this.getFpPlannedList(liaisonInstitutions, phaseDB, liaisonInstitution);

      for (ReportSynthesisFlagshipProgressPolicyDTO reportSynthesisFlagshipProgressPolicyDTO : flagshipPlannedList) {

        ProjectPolicy projectPolicy = reportSynthesisFlagshipProgressPolicyDTO.getProjectPolicy();
        projectPolicy.getProjectPolicyInfo(phaseDB);
        projectPolicy.setSelectedFlahsgips(new ArrayList<>());
        // sort selected flagships
        if (reportSynthesisFlagshipProgressPolicyDTO.getLiaisonInstitutions() != null
          && !reportSynthesisFlagshipProgressPolicyDTO.getLiaisonInstitutions().isEmpty()) {
          reportSynthesisFlagshipProgressPolicyDTO.getLiaisonInstitutions()
            .sort((l1, l2) -> l1.getCrpProgram().getAcronym().compareTo(l2.getCrpProgram().getAcronym()));
        }
        projectPolicy.getSelectedFlahsgips().addAll(reportSynthesisFlagshipProgressPolicyDTO.getLiaisonInstitutions());
        projectPolicy.setSubIdos(projectPolicy.getSubIdos(phaseDB));
        projectPolicy.setCrossCuttingMarkers(projectPolicy.getCrossCuttingMarkers(phaseDB));
        projectPolicy.setOwners(projectPolicy.getOwners(phaseDB));
        projectPolicy.setGeographicScopes(projectPolicy.getGeographicScopes(phaseDB));
        projectPolicy.setRegions(projectPolicy.getRegions(phaseDB));
        projectPolicy.setCountries(projectPolicy.getCountries(phaseDB));
        projectPolicy.setEvidences(projectPolicy.getEvidences(phaseDB));
        projectPolicies.add(projectPolicy);

      }
    }
    if (projectPolicies != null && !projectPolicies.isEmpty()) {
      projectPolicies.sort((p1, p2) -> p1.getId().compareTo(p2.getId()));
    }
    return projectPolicies;
  }

  @Override
  public ProjectPolicy getProjectPolicyById(long projectPolicyID) {

    return projectPolicyDAO.find(projectPolicyID);
  }

  @Override
  public List<ProjectPolicy> getProjectPolicyByPhase(Phase phase) {
    return projectPolicyDAO.getProjectPolicyByPhase(phase);
  }

  @Override
  public ProjectPolicy saveProjectPolicy(ProjectPolicy projectPolicy) {

    return projectPolicyDAO.save(projectPolicy);
  }

  @Override
  public ProjectPolicy saveProjectPolicy(ProjectPolicy projectPolicy, String section, List<String> relationsName,
    Phase phase) {
    return projectPolicyDAO.save(projectPolicy, section, relationsName, phase);
  }

}
