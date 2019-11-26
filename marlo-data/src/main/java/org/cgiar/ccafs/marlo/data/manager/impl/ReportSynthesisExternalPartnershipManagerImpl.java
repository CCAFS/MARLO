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


import org.cgiar.ccafs.marlo.data.dao.ReportSynthesisExternalPartnershipDAO;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPartnerPartnershipManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisExternalPartnershipManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisManager;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProgramType;
import org.cgiar.ccafs.marlo.data.model.ProjectFocus;
import org.cgiar.ccafs.marlo.data.model.ProjectPartnerPartnership;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesis;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisExternalPartnership;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisExternalPartnershipDTO;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisExternalPartnershipProject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ReportSynthesisExternalPartnershipManagerImpl implements ReportSynthesisExternalPartnershipManager {


  private ReportSynthesisExternalPartnershipDAO reportSynthesisExternalPartnershipDAO;
  // Managers
  private PhaseManager phaseManager;
  private ProjectPartnerPartnershipManager projectPartnerPartnershipManager;
  private ReportSynthesisManager reportSynthesisManager;

  @Inject
  public ReportSynthesisExternalPartnershipManagerImpl(
    ReportSynthesisExternalPartnershipDAO reportSynthesisExternalPartnershipDAO, PhaseManager phaseManager,
    ProjectPartnerPartnershipManager projectPartnerPartnershipManager, ReportSynthesisManager reportSynthesisManager) {
    this.reportSynthesisExternalPartnershipDAO = reportSynthesisExternalPartnershipDAO;
    this.phaseManager = phaseManager;
    this.projectPartnerPartnershipManager = projectPartnerPartnershipManager;
    this.reportSynthesisManager = reportSynthesisManager;
  }

  @Override
  public void deleteReportSynthesisExternalPartnership(long reportSynthesisExternalPartnershipId) {

    reportSynthesisExternalPartnershipDAO
      .deleteReportSynthesisExternalPartnership(reportSynthesisExternalPartnershipId);
  }

  @Override
  public boolean existReportSynthesisExternalPartnership(long reportSynthesisExternalPartnershipID) {

    return reportSynthesisExternalPartnershipDAO
      .existReportSynthesisExternalPartnership(reportSynthesisExternalPartnershipID);
  }

  @Override
  public List<ReportSynthesisExternalPartnership> findAll() {

    return reportSynthesisExternalPartnershipDAO.findAll();

  }

  @Override
  public List<ReportSynthesisExternalPartnership>
    getFlagshipCExternalPartnership(List<LiaisonInstitution> lInstitutions, long phaseID) {

    List<ReportSynthesisExternalPartnership> externalPartnerships = new ArrayList<>();

    for (LiaisonInstitution liaisonInstitution : lInstitutions) {

      ReportSynthesisExternalPartnership externalPartnership = new ReportSynthesisExternalPartnership();
      ReportSynthesis reportSynthesisFP = reportSynthesisManager.findSynthesis(phaseID, liaisonInstitution.getId());

      if (reportSynthesisFP != null) {
        if (reportSynthesisFP.getReportSynthesisExternalPartnership() != null) {
          externalPartnership = reportSynthesisFP.getReportSynthesisExternalPartnership();
        }
      } else {
        ReportSynthesis synthesis = new ReportSynthesis();
        synthesis.setPhase(phaseManager.getPhaseById(phaseID));
        synthesis.setLiaisonInstitution(liaisonInstitution);
        externalPartnership.setReportSynthesis(synthesis);
      }
      externalPartnerships.add(externalPartnership);
    }

    return externalPartnerships;


  }

  @Override
  public List<ReportSynthesisExternalPartnershipDTO> getPlannedPartnershipList(List<LiaisonInstitution> lInstitutions,
    long phaseID, GlobalUnit loggedCrp, LiaisonInstitution liaisonInstitutionPMU) {

    List<ReportSynthesisExternalPartnershipDTO> partnershipPlannedList = new ArrayList<>();
    Phase phase = phaseManager.getPhaseById(phaseID);

    if (projectPartnerPartnershipManager.findAll() != null) {
      List<ProjectPartnerPartnership> projectPartnerPartnerships =
        new ArrayList<>(projectPartnerPartnershipManager.findAll().stream()
          .filter(ps -> ps.isActive() && ps.getProjectPartner().getPhase() != null
            && ps.getProjectPartner().getPhase().getId() == phaseID
            && ps.getProjectPartner().getPhase().getYear() == phase.getYear())
          .collect(Collectors.toList()));

      Collections.sort(projectPartnerPartnerships, (p1, p2) -> p1.getProjectPartner().getInstitution().getId()
        .compareTo(p2.getProjectPartner().getInstitution().getId()));

      for (ProjectPartnerPartnership projectPartnerPartnership : projectPartnerPartnerships) {
        ReportSynthesisExternalPartnershipDTO dto = new ReportSynthesisExternalPartnershipDTO();

        // Set up list
        projectPartnerPartnership.setPartnershipResearchPhases(new ArrayList<>());
        if (projectPartnerPartnership.getProjectPartnerPartnershipResearchPhases() != null
          || !projectPartnerPartnership.getProjectPartnerPartnershipResearchPhases().isEmpty()) {
          projectPartnerPartnership.getPartnershipResearchPhases()
            .addAll(projectPartnerPartnership.getProjectPartnerPartnershipResearchPhases().stream()
              .filter(p -> p.isActive()).collect(Collectors.toList()));
        }

        projectPartnerPartnership.setPartnershipLocations(new ArrayList<>());
        if (projectPartnerPartnership.getProjectPartnerPartnershipLocations() != null
          || !projectPartnerPartnership.getProjectPartnerPartnershipLocations().isEmpty()) {
          projectPartnerPartnership.getPartnershipLocations().addAll(projectPartnerPartnership
            .getProjectPartnerPartnershipLocations().stream().filter(p -> p.isActive()).collect(Collectors.toList()));
        }

        dto.setProjectPartnerPartnership(projectPartnerPartnership);
        if (projectPartnerPartnership.getProjectPartner().getProject().getProjecInfoPhase(phase)
          .getAdministrative() != null
          && projectPartnerPartnership.getProjectPartner().getProject().getProjecInfoPhase(phase).getAdministrative()) {
          dto.setLiaisonInstitutions(new ArrayList<>());
          dto.getLiaisonInstitutions().add(liaisonInstitutionPMU);
        } else {
          List<ProjectFocus> projectFocuses =
            new ArrayList<>(projectPartnerPartnership.getProjectPartner().getProject().getProjectFocuses().stream()
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

        partnershipPlannedList.add(dto);

      }

      List<ReportSynthesisExternalPartnershipProject> externalPartnershipProjects = new ArrayList<>();


      for (LiaisonInstitution liaisonInstitution : lInstitutions) {
        ReportSynthesis reportSynthesisFP = reportSynthesisManager.findSynthesis(phaseID, liaisonInstitution.getId());
        if (reportSynthesisFP != null) {
          if (reportSynthesisFP.getReportSynthesisExternalPartnership() != null) {
            if (reportSynthesisFP.getReportSynthesisExternalPartnership()
              .getReportSynthesisExternalPartnershipProjects() != null) {
              List<ReportSynthesisExternalPartnershipProject> partnerships = new ArrayList<>(reportSynthesisFP
                .getReportSynthesisExternalPartnership().getReportSynthesisExternalPartnershipProjects().stream()
                .filter(s -> s.isActive()).collect(Collectors.toList()));
              if (partnerships != null || !partnerships.isEmpty()) {
                for (ReportSynthesisExternalPartnershipProject reportSynthesisExternalPartnershipProject : partnerships) {
                  externalPartnershipProjects.add(reportSynthesisExternalPartnershipProject);
                }
              }
            }
          }
        }

      }

      List<ReportSynthesisExternalPartnershipDTO> removeList = new ArrayList<>();
      for (ReportSynthesisExternalPartnershipDTO dto : partnershipPlannedList) {
        ReportSynthesis reportSynthesisFP = null;
        List<LiaisonInstitution> removeLiaison = new ArrayList<>();
        for (LiaisonInstitution liaisonInstitution : dto.getLiaisonInstitutions()) {
          try {
            reportSynthesisFP = reportSynthesisManager.findSynthesis(phaseID, liaisonInstitution.getId());
          } catch (Exception e) {

          }
          if (reportSynthesisFP != null) {
            if (reportSynthesisFP.getReportSynthesisExternalPartnership() != null) {

              ReportSynthesisExternalPartnershipProject externalPartnershipNew =
                new ReportSynthesisExternalPartnershipProject();
              externalPartnershipNew = new ReportSynthesisExternalPartnershipProject();
              externalPartnershipNew.setProjectPartnerPartnership(dto.getProjectPartnerPartnership());
              externalPartnershipNew
                .setReportSynthesisExternalPartnership(reportSynthesisFP.getReportSynthesisExternalPartnership());

              if (externalPartnershipProjects.contains(externalPartnershipNew)) {
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


      for (ReportSynthesisExternalPartnershipDTO i : removeList) {
        partnershipPlannedList.remove(i);
      }

    }
    return partnershipPlannedList;
  }

  @Override
  public ReportSynthesisExternalPartnership
    getReportSynthesisExternalPartnershipById(long reportSynthesisExternalPartnershipID) {

    return reportSynthesisExternalPartnershipDAO.find(reportSynthesisExternalPartnershipID);
  }

  @Override
  public ReportSynthesisExternalPartnership
    saveReportSynthesisExternalPartnership(ReportSynthesisExternalPartnership reportSynthesisExternalPartnership) {

    return reportSynthesisExternalPartnershipDAO.save(reportSynthesisExternalPartnership);
  }


}
