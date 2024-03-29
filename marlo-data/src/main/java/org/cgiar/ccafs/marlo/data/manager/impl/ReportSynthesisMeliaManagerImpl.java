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


import org.cgiar.ccafs.marlo.data.dao.ReportSynthesisMeliaDAO;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisMeliaManager;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.PowbEvidencePlannedStudyDTO;
import org.cgiar.ccafs.marlo.data.model.ProgramType;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudy;
import org.cgiar.ccafs.marlo.data.model.ProjectFocus;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesis;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisMelia;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisMeliaEvaluation;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisMeliaStudy;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ReportSynthesisMeliaManagerImpl implements ReportSynthesisMeliaManager {


  private ReportSynthesisMeliaDAO reportSynthesisMeliaDAO;
  // Managers
  private ProjectExpectedStudyManager projectExpectedStudyManager;
  private ReportSynthesisManager reportSynthesisManager;
  private PhaseManager phaseManager;


  @Inject
  public ReportSynthesisMeliaManagerImpl(ReportSynthesisMeliaDAO reportSynthesisMeliaDAO,
    ProjectExpectedStudyManager projectExpectedStudyManager, ReportSynthesisManager reportSynthesisManager,
    PhaseManager phaseManager) {
    this.reportSynthesisMeliaDAO = reportSynthesisMeliaDAO;
    this.projectExpectedStudyManager = projectExpectedStudyManager;
    this.reportSynthesisManager = reportSynthesisManager;
    this.phaseManager = phaseManager;

  }

  @Override
  public void deleteReportSynthesisMelia(long reportSynthesisMeliaId) {

    reportSynthesisMeliaDAO.deleteReportSynthesisMelia(reportSynthesisMeliaId);
  }

  @Override
  public boolean existReportSynthesisMelia(long reportSynthesisMeliaID) {

    return reportSynthesisMeliaDAO.existReportSynthesisMelia(reportSynthesisMeliaID);
  }

  @Override
  public List<ReportSynthesisMelia> findAll() {

    return reportSynthesisMeliaDAO.findAll();

  }

  @Override
  public List<ReportSynthesisMeliaEvaluation> flagshipSynthesisEvaluation(List<LiaisonInstitution> lInstitutions,
    long phaseID) {

    List<ReportSynthesisMeliaEvaluation> evaluations = new ArrayList<>();

    for (LiaisonInstitution liaisonInstitution : lInstitutions) {
      ReportSynthesis reportSynthesisFP = reportSynthesisManager.findSynthesis(phaseID, liaisonInstitution.getId());
      if (reportSynthesisFP != null) {
        if (reportSynthesisFP.getReportSynthesisMelia() != null) {
          if (reportSynthesisFP.getReportSynthesisMelia().getReportSynthesisMeliaEvaluations() != null) {
            List<ReportSynthesisMeliaEvaluation> meliaEvaluations =
              new ArrayList<>(reportSynthesisFP.getReportSynthesisMelia().getReportSynthesisMeliaEvaluations().stream()
                .filter(s -> s.isActive()).collect(Collectors.toList()));
            if (meliaEvaluations != null || !meliaEvaluations.isEmpty()) {
              for (ReportSynthesisMeliaEvaluation meliaEvaluation : meliaEvaluations) {
                evaluations.add(meliaEvaluation);
              }
            }
          }
        }
      }
    }

    return evaluations;
  }

  @Override
  public List<ReportSynthesisMelia> getFlagshipMelia(List<LiaisonInstitution> lInstitutions, long phaseID) {

    List<ReportSynthesisMelia> synthesisMelia = new ArrayList<>();

    for (LiaisonInstitution liaisonInstitution : lInstitutions) {

      ReportSynthesisMelia melia = new ReportSynthesisMelia();
      ReportSynthesis reportSynthesisFP = reportSynthesisManager.findSynthesis(phaseID, liaisonInstitution.getId());

      if (reportSynthesisFP != null) {
        if (reportSynthesisFP.getReportSynthesisMelia() != null) {
          melia = reportSynthesisFP.getReportSynthesisMelia();
        }
      } else {
        ReportSynthesis synthesis = new ReportSynthesis();
        synthesis.setPhase(phaseManager.getPhaseById(phaseID));
        synthesis.setLiaisonInstitution(liaisonInstitution);
        melia.setReportSynthesis(synthesis);
      }
      synthesisMelia.add(melia);
    }

    return synthesisMelia;


  }


  @Override
  public List<PowbEvidencePlannedStudyDTO> getMeliaPlannedList(List<LiaisonInstitution> lInstitutions, long phaseID,
    GlobalUnit loggedCrp, LiaisonInstitution liaisonInstitutionPMU) {

    List<PowbEvidencePlannedStudyDTO> flagshipPlannedList = new ArrayList<>();
    Phase phase = phaseManager.getPhaseById(phaseID);

    if (projectExpectedStudyManager.findAll() != null) {
      List<ProjectExpectedStudy> expectedStudies = new ArrayList<>(projectExpectedStudyManager.findAll().stream()
        .filter(ps -> ps.isActive() && ps.getProjectExpectedStudyInfo(phase) != null
          && ps.getProjectExpectedStudyInfo(phase).getStudyType() != null
          && ps.getProjectExpectedStudyInfo(phase).getStudyType().getId() != 1
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
            if (projectExpectedStudy.getProject().getProjectInfo() != null
              && projectExpectedStudy.getProject().getProjectInfo().getAdministrative() != null
              && projectExpectedStudy.getProject().getProjectInfo().getAdministrative()) {
              dto.setLiaisonInstitutions(new ArrayList<>());
              dto.getLiaisonInstitutions().add(liaisonInstitutionPMU);
            } else {
              List<ProjectFocus> projectFocuses =
                new ArrayList<>(projectExpectedStudy.getProject().getProjectFocuses().stream()
                  .filter(pf -> pf.isActive() && pf.getPhase().getId().equals(phaseID)).collect(Collectors.toList()));
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
            dto.setLiaisonInstitutions(new ArrayList<>());
            dto.getLiaisonInstitutions().add(liaisonInstitutionPMU);
          }

          if (projectExpectedStudy.getProjectExpectedStudySubIdos() != null
            && !projectExpectedStudy.getProjectExpectedStudySubIdos().isEmpty()) {
            projectExpectedStudy.setSubIdos(new ArrayList<>(projectExpectedStudy.getProjectExpectedStudySubIdos()
              .stream().filter(s -> s.getPhase().getId().equals(phase.getId())).collect(Collectors.toList())));
          }

          flagshipPlannedList.add(dto);
        }
      }
      List<ReportSynthesisMeliaStudy> reportStudies = new ArrayList<>();


      for (LiaisonInstitution liaisonInstitution : lInstitutions) {
        ReportSynthesis reportSynthesisFP = reportSynthesisManager.findSynthesis(phaseID, liaisonInstitution.getId());
        if (reportSynthesisFP != null) {
          if (reportSynthesisFP.getReportSynthesisMelia() != null) {
            if (reportSynthesisFP.getReportSynthesisMelia().getReportSynthesisMeliaStudies() != null) {
              List<ReportSynthesisMeliaStudy> studies = new ArrayList<>(reportSynthesisFP.getReportSynthesisMelia()
                .getReportSynthesisMeliaStudies().stream().filter(s -> s.isActive()).collect(Collectors.toList()));
              if (studies != null || !studies.isEmpty()) {
                for (ReportSynthesisMeliaStudy meliaStudy : studies) {
                  reportStudies.add(meliaStudy);
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
            if (reportSynthesisFP.getReportSynthesisMelia() != null) {

              ReportSynthesisMeliaStudy reportStudyNew = new ReportSynthesisMeliaStudy();
              reportStudyNew.setProjectExpectedStudy(dto.getProjectExpectedStudy());
              reportStudyNew.setReportSynthesisMelia(reportSynthesisFP.getReportSynthesisMelia());

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
  public ReportSynthesisMelia getReportSynthesisMeliaById(long reportSynthesisMeliaID) {

    return reportSynthesisMeliaDAO.find(reportSynthesisMeliaID);
  }

  @Override
  public List<ProjectExpectedStudy> getTable10(List<LiaisonInstitution> lInstitutions, long phaseID,
    GlobalUnit loggedCrp, LiaisonInstitution liaisonInstitutionPMU) {

    List<PowbEvidencePlannedStudyDTO> flagshipPlannedList = new ArrayList<>();
    Phase phase = phaseManager.getPhaseById(phaseID);

    if (projectExpectedStudyManager.findAll() != null) {
      List<ProjectExpectedStudy> expectedStudies = new ArrayList<>(projectExpectedStudyManager.findAll().stream()
        .filter(ps -> ps.isActive() && ps.getProjectExpectedStudyInfo(phase) != null
          && ps.getProjectExpectedStudyInfo(phase).getStudyType() != null
          && ps.getProjectExpectedStudyInfo(phase).getStudyType().getId() != 1
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
            if (projectExpectedStudy.getProject().getProjectInfo() != null
              && projectExpectedStudy.getProject().getProjectInfo().getAdministrative() != null
              && projectExpectedStudy.getProject().getProjectInfo().getAdministrative()) {
              dto.setLiaisonInstitutions(new ArrayList<>());
              dto.getLiaisonInstitutions().add(liaisonInstitutionPMU);
            } else {
              List<ProjectFocus> projectFocuses =
                new ArrayList<>(projectExpectedStudy.getProject().getProjectFocuses().stream()
                  .filter(pf -> pf.isActive() && pf.getPhase().getId().equals(phaseID)).collect(Collectors.toList()));
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
            dto.setLiaisonInstitutions(new ArrayList<>());
            dto.getLiaisonInstitutions().add(liaisonInstitutionPMU);
          }

          if (projectExpectedStudy.getProjectExpectedStudySubIdos() != null
            && !projectExpectedStudy.getProjectExpectedStudySubIdos().isEmpty()) {
            projectExpectedStudy.setSubIdos(new ArrayList<>(projectExpectedStudy.getProjectExpectedStudySubIdos()
              .stream().filter(s -> s.getPhase().getId().equals(phase.getId())).collect(Collectors.toList())));
          }

          flagshipPlannedList.add(dto);
        }
      }
      List<ReportSynthesisMeliaStudy> reportStudies = new ArrayList<>();


      for (LiaisonInstitution liaisonInstitution : lInstitutions) {
        ReportSynthesis reportSynthesisFP = reportSynthesisManager.findSynthesis(phaseID, liaisonInstitution.getId());
        if (reportSynthesisFP != null) {
          if (reportSynthesisFP.getReportSynthesisMelia() != null) {
            if (reportSynthesisFP.getReportSynthesisMelia().getReportSynthesisMeliaStudies() != null) {
              List<ReportSynthesisMeliaStudy> studies = new ArrayList<>(reportSynthesisFP.getReportSynthesisMelia()
                .getReportSynthesisMeliaStudies().stream().filter(s -> s.isActive()).collect(Collectors.toList()));
              if (studies != null || !studies.isEmpty()) {
                for (ReportSynthesisMeliaStudy meliaStudy : studies) {
                  reportStudies.add(meliaStudy);
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
            if (reportSynthesisFP.getReportSynthesisMelia() != null) {

              ReportSynthesisMeliaStudy reportStudyNew = new ReportSynthesisMeliaStudy();
              reportStudyNew.setProjectExpectedStudy(dto.getProjectExpectedStudy());
              reportStudyNew.setReportSynthesisMelia(reportSynthesisFP.getReportSynthesisMelia());

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

    List<ProjectExpectedStudy> studiesWord = new ArrayList<>();

    for (PowbEvidencePlannedStudyDTO dtoAdd : flagshipPlannedList) {
      studiesWord.add(dtoAdd.getProjectExpectedStudy());
    }

    ReportSynthesis reportSynthesisPMU = null;
    if (liaisonInstitutionPMU != null && liaisonInstitutionPMU.getId() != null) {
      reportSynthesisPMU = reportSynthesisManager.findSynthesis(phase.getId(), liaisonInstitutionPMU.getId());
    }

    if (reportSynthesisPMU != null) {

      List<ProjectExpectedStudy> pmuStudies = new ArrayList<>();

      // check if the PMU exclude some key external partnership
      if (reportSynthesisPMU.getReportSynthesisMelia() != null
        && reportSynthesisPMU.getReportSynthesisMelia().getReportSynthesisMeliaStudies() != null
        && !reportSynthesisPMU.getReportSynthesisMelia().getReportSynthesisMeliaStudies().isEmpty()) {
        for (ReportSynthesisMeliaStudy plannedPmu : reportSynthesisPMU.getReportSynthesisMelia()
          .getReportSynthesisMeliaStudies().stream().filter(ro -> ro.isActive()).collect(Collectors.toList())) {
          pmuStudies.add(plannedPmu.getProjectExpectedStudy());
        }
      }

      if (!pmuStudies.isEmpty()) {
        // Remove for the list the key external partnership the the PMU exclude it.
        studiesWord.removeAll(pmuStudies);
      }
    }


    return studiesWord;
  }

  @Override
  public ReportSynthesisMelia saveReportSynthesisMelia(ReportSynthesisMelia reportSynthesisMelia) {

    return reportSynthesisMeliaDAO.save(reportSynthesisMelia);
  }


}
