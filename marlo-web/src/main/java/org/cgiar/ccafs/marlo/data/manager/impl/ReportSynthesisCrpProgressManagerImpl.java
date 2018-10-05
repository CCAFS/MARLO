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


import org.cgiar.ccafs.marlo.data.dao.ReportSynthesisCrpProgressDAO;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisCrpProgressManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisManager;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.PowbEvidencePlannedStudyDTO;
import org.cgiar.ccafs.marlo.data.model.ProgramType;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudy;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyFlagship;
import org.cgiar.ccafs.marlo.data.model.ProjectFocus;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesis;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisCrpProgress;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisCrpProgressStudy;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ReportSynthesisCrpProgressManagerImpl implements ReportSynthesisCrpProgressManager {


  private ReportSynthesisCrpProgressDAO reportSynthesisCrpProgressDAO;
  // Managers
  private ProjectExpectedStudyManager projectExpectedStudyManager;
  private PhaseManager phaseManager;
  private ReportSynthesisManager reportSynthesisManager;

  @Inject
  public ReportSynthesisCrpProgressManagerImpl(ReportSynthesisCrpProgressDAO reportSynthesisCrpProgressDAO,
    ProjectExpectedStudyManager projectExpectedStudyManager, PhaseManager phaseManager,
    ReportSynthesisManager reportSynthesisManager) {
    this.reportSynthesisCrpProgressDAO = reportSynthesisCrpProgressDAO;
    this.projectExpectedStudyManager = projectExpectedStudyManager;
    this.phaseManager = phaseManager;
    this.reportSynthesisManager = reportSynthesisManager;

  }

  @Override
  public void deleteReportSynthesisCrpProgress(long reportSynthesisCrpProgressId) {

    reportSynthesisCrpProgressDAO.deleteReportSynthesisCrpProgress(reportSynthesisCrpProgressId);
  }

  @Override
  public boolean existReportSynthesisCrpProgress(long reportSynthesisCrpProgressID) {

    return reportSynthesisCrpProgressDAO.existReportSynthesisCrpProgress(reportSynthesisCrpProgressID);
  }

  @Override
  public List<ReportSynthesisCrpProgress> findAll() {

    return reportSynthesisCrpProgressDAO.findAll();

  }


  @Override
  public List<ReportSynthesisCrpProgress> getFlagshipCrpProgress(List<LiaisonInstitution> lInstitutions, long phaseID) {

    List<ReportSynthesisCrpProgress> synthesisCrpProgres = new ArrayList<>();

    for (LiaisonInstitution liaisonInstitution : lInstitutions) {

      ReportSynthesisCrpProgress crpProgress = new ReportSynthesisCrpProgress();
      ReportSynthesis reportSynthesisFP = reportSynthesisManager.findSynthesis(phaseID, liaisonInstitution.getId());

      if (reportSynthesisFP != null) {
        if (reportSynthesisFP.getReportSynthesisCrpProgress() != null) {
          crpProgress = reportSynthesisFP.getReportSynthesisCrpProgress();
        }
      } else {
        ReportSynthesis synthesis = new ReportSynthesis();
        synthesis.setPhase(phaseManager.getPhaseById(phaseID));
        synthesis.setLiaisonInstitution(liaisonInstitution);
        crpProgress.setReportSynthesis(synthesis);
      }
      synthesisCrpProgres.add(crpProgress);
    }

    return synthesisCrpProgres;


  }

  @Override
  public List<PowbEvidencePlannedStudyDTO> getPlannedList(List<LiaisonInstitution> lInstitutions, long phaseID,
    GlobalUnit loggedCrp, LiaisonInstitution liaisonInstitutionPMU) {

    List<PowbEvidencePlannedStudyDTO> flagshipPlannedList = new ArrayList<>();
    Phase phase = phaseManager.getPhaseById(phaseID);

    if (projectExpectedStudyManager.findAll() != null) {
      List<ProjectExpectedStudy> expectedStudies = new ArrayList<>(projectExpectedStudyManager.findAll().stream()
        .filter(ps -> ps.isActive() && ps.getProjectExpectedStudyInfo(phase) != null
          && ps.getProjectExpectedStudyInfo(phase).getStudyType() != null
          && ps.getProjectExpectedStudyInfo(phase).getStudyType().getId() == 1
          && ps.getProjectExpectedStudyInfo(phase).getPhase().getId() == phaseID
          && ps.getProjectExpectedStudyInfo(phase).getYear() == phase.getYear())
        .collect(Collectors.toList()));

      for (ProjectExpectedStudy projectExpectedStudy : expectedStudies) {
        PowbEvidencePlannedStudyDTO dto = new PowbEvidencePlannedStudyDTO();
        if (projectExpectedStudy.getProjectExpectedStudyInfo(phase) != null) {
          dto.setProjectExpectedStudy(projectExpectedStudy);

          if (projectExpectedStudy.getProject() != null) {
            projectExpectedStudy.getProject()
              .setProjectInfo(projectExpectedStudy.getProject().getProjecInfoPhase(phase));
            dto.setProjectExpectedStudy(projectExpectedStudy);
            if (projectExpectedStudy.getProject().getProjectInfo().getAdministrative() != null
              && projectExpectedStudy.getProject().getProjectInfo().getAdministrative()) {
              dto.setLiaisonInstitutions(new ArrayList<>());
              dto.getLiaisonInstitutions().add(liaisonInstitutionPMU);
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
          } else {
            List<ProjectExpectedStudyFlagship> studiesPrograms =
              new ArrayList<>(projectExpectedStudy.getProjectExpectedStudyFlagships().stream()
                .filter(s -> s.isActive() && s.getPhase().getId() == phase.getId()).collect(Collectors.toList()));
            List<LiaisonInstitution> liaisonInstitutions = new ArrayList<>();
            for (ProjectExpectedStudyFlagship projectExpectedStudyFlagship : studiesPrograms) {
              liaisonInstitutions.addAll(projectExpectedStudyFlagship.getCrpProgram().getLiaisonInstitutions().stream()
                .filter(li -> li.isActive() && li.getCrpProgram() != null
                  && li.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue())
                .collect(Collectors.toList()));
            }
            dto.setLiaisonInstitutions(liaisonInstitutions);
          }

          if (projectExpectedStudy.getProjectExpectedStudySubIdos() != null
            && !projectExpectedStudy.getProjectExpectedStudySubIdos().isEmpty()) {
            projectExpectedStudy.setSubIdos(new ArrayList<>(projectExpectedStudy.getProjectExpectedStudySubIdos()
              .stream().filter(s -> s.getPhase().getId() == phase.getId()).collect(Collectors.toList())));
          }

          flagshipPlannedList.add(dto);
        }
      }
      List<ReportSynthesisCrpProgressStudy> reportStudies = new ArrayList<>();


      for (LiaisonInstitution liaisonInstitution : lInstitutions) {
        ReportSynthesis reportSynthesisFP = reportSynthesisManager.findSynthesis(phaseID, liaisonInstitution.getId());
        if (reportSynthesisFP != null) {
          if (reportSynthesisFP.getReportSynthesisCrpProgress() != null) {
            if (reportSynthesisFP.getReportSynthesisCrpProgress().getReportSynthesisCrpProgressStudies() != null) {
              List<ReportSynthesisCrpProgressStudy> studies =
                new ArrayList<>(reportSynthesisFP.getReportSynthesisCrpProgress().getReportSynthesisCrpProgressStudies()
                  .stream().filter(s -> s.isActive()).collect(Collectors.toList()));
              if (studies != null || !studies.isEmpty()) {
                for (ReportSynthesisCrpProgressStudy crpProgressStudy : studies) {
                  reportStudies.add(crpProgressStudy);
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
          ReportSynthesis reportSynthesisFP = reportSynthesisManager.findSynthesis(phaseID, liaisonInstitution.getId());
          if (reportSynthesisFP != null) {
            if (reportSynthesisFP.getReportSynthesisCrpProgress() != null) {

              ReportSynthesisCrpProgressStudy reportStudyNew = new ReportSynthesisCrpProgressStudy();
              reportStudyNew = new ReportSynthesisCrpProgressStudy();
              reportStudyNew.setProjectExpectedStudy(dto.getProjectExpectedStudy());
              reportStudyNew.setReportSynthesisCrpProgress(reportSynthesisFP.getReportSynthesisCrpProgress());

              if (reportStudies.contains(reportStudyNew)) {
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

    return flagshipPlannedList;
  }

  @Override
  public ReportSynthesisCrpProgress getReportSynthesisCrpProgressById(long reportSynthesisCrpProgressID) {

    return reportSynthesisCrpProgressDAO.find(reportSynthesisCrpProgressID);
  }

  @Override
  public ReportSynthesisCrpProgress
    saveReportSynthesisCrpProgress(ReportSynthesisCrpProgress reportSynthesisCrpProgress) {

    return reportSynthesisCrpProgressDAO.save(reportSynthesisCrpProgress);
  }


}
