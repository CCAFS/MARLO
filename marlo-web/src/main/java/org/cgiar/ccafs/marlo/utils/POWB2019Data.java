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

package org.cgiar.ccafs.marlo.utils;

import org.cgiar.ccafs.marlo.data.manager.PowbSynthesisManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectFocusManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.CrpProgramOutcome;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.PowbEvidencePlannedStudy;
import org.cgiar.ccafs.marlo.data.model.PowbEvidencePlannedStudyDTO;
import org.cgiar.ccafs.marlo.data.model.PowbSynthesis;
import org.cgiar.ccafs.marlo.data.model.PowbTocListDTO;
import org.cgiar.ccafs.marlo.data.model.ProgramType;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudy;
import org.cgiar.ccafs.marlo.data.model.ProjectFocus;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
@Named
public class POWB2019Data<T> {

  private PowbSynthesisManager powbSynthesisManager;
  private ProjectExpectedStudyManager projectExpectedStudyManager;
  private ProjectFocusManager projectFocusManager;
  private ProjectManager projectManager;


  @Inject
  public POWB2019Data(PowbSynthesisManager powbSynthesisManager,
    ProjectExpectedStudyManager projectExpectedStudyManager, ProjectFocusManager projectFocusManager,
    ProjectManager projectManager) {
    this.powbSynthesisManager = powbSynthesisManager;
    this.projectExpectedStudyManager = projectExpectedStudyManager;
    this.projectFocusManager = projectFocusManager;
    this.projectManager = projectManager;
  }

  /**
   * Expected CRP progress Table 1
   * 
   * @param phase
   * @param flagships
   * @return
   */
  public List<PowbTocListDTO> getExpectedProgressFlgashipChanges(Phase phase, List<LiaisonInstitution> flagships) {
    List<PowbTocListDTO> tocList = new ArrayList<>();
    for (LiaisonInstitution liaisonInstitution : flagships) {
      PowbTocListDTO powbTocList = new PowbTocListDTO();
      powbTocList.setLiaisonInstitution(liaisonInstitution);
      powbTocList.setOverall("");
      PowbSynthesis powbSynthesis = powbSynthesisManager.findSynthesis(phase.getId(), liaisonInstitution.getId());
      if (powbSynthesis != null) {
        if (powbSynthesis.getExpectedProgressNarrative() != null) {
          powbTocList.setOverall(powbSynthesis.getExpectedProgressNarrative());
        }
      }
      tocList.add(powbTocList);
    }
    return tocList;
  }

  /**
   * @param lInstitutions
   * @param phaseID
   * @param phase
   * @param loggedCrp
   * @param liaisonPMU
   * @return
   */
  public List<PowbEvidencePlannedStudyDTO> getFpPlannedList(List<LiaisonInstitution> lInstitutions, long phaseID,
    Phase phase, GlobalUnit loggedCrp, LiaisonInstitution liaisonPMU) {
    List<PowbEvidencePlannedStudyDTO> flagshipPlannedList = new ArrayList<>();

    if (projectExpectedStudyManager.findAll() != null) {
      List<ProjectExpectedStudy> expectedStudies =
        new ArrayList<>(
          projectExpectedStudyManager.findAll().stream()
            .filter(ps -> ps.isActive() && ps.getProjectExpectedStudyInfo(phase) != null
              && ps.getProjectExpectedStudyInfo(phase).getYear() == 2019 // TODO year Burn
              && ps.getProject() != null
              && ps.getProject().getGlobalUnitProjects().stream()
                .filter(
                  gup -> gup.isActive() && gup.isOrigin() && gup.getGlobalUnit().getId().equals(loggedCrp.getId()))
                .collect(Collectors.toList()).size() > 0)
            .collect(Collectors.toList()));

      for (ProjectExpectedStudy projectExpectedStudy : expectedStudies) {
        PowbEvidencePlannedStudyDTO dto = new PowbEvidencePlannedStudyDTO();
        projectExpectedStudy.getProject().setProjectInfo(projectExpectedStudy.getProject().getProjecInfoPhase(phase));
        dto.setProjectExpectedStudy(projectExpectedStudy);
        if (projectExpectedStudy.getProject().getProjectInfo().getAdministrative() != null
          && projectExpectedStudy.getProject().getProjectInfo().getAdministrative()) {
          dto.setLiaisonInstitutions(new ArrayList<>());
          dto.getLiaisonInstitutions().add(liaisonPMU);
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

    return flagshipPlannedList;
  }


  /**
   * Expected CRP Progress Table 2A - Planned Milestones
   * 
   * @return
   */
  public List<CrpProgram> getTable2A(GlobalUnit loggedCrp, Phase phase) {

    List<CrpProgram> flagships = loggedCrp.getCrpPrograms().stream()
      .filter(c -> c.isActive() && c.getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue())
      .collect(Collectors.toList());
    flagships.sort((p1, p2) -> p1.getAcronym().compareTo(p2.getAcronym()));

    for (CrpProgram crpProgram : flagships) {
      crpProgram.setMilestones(new ArrayList<>());
      crpProgram.setW1(new Double(0));
      crpProgram.setW3(new Double(0));

      crpProgram.setOutcomes(crpProgram.getCrpProgramOutcomes().stream()
        .filter(c -> c.isActive() && c.getPhase().equals(phase)).collect(Collectors.toList()));
      List<CrpProgramOutcome> validOutcomes = new ArrayList<>();
      for (CrpProgramOutcome crpProgramOutcome : crpProgram.getOutcomes()) {

        crpProgramOutcome.setMilestones(crpProgramOutcome.getCrpMilestones().stream()
          .filter(
            c -> c.isActive() && c.getYear().intValue() == phase.getYear() && c.getIsPowb() != null && c.getIsPowb())
          .collect(Collectors.toList()));
        crpProgramOutcome.setSubIdos(
          crpProgramOutcome.getCrpOutcomeSubIdos().stream().filter(c -> c.isActive()).collect(Collectors.toList()));
        crpProgram.getMilestones().addAll(crpProgramOutcome.getMilestones());
        if (!crpProgram.getMilestones().isEmpty()) {


          validOutcomes.add(crpProgramOutcome);
        }
      }
      crpProgram.setOutcomes(validOutcomes);
    }
    return flagships;
  }


  /**
   * @param phase
   * @param loggedCrp
   * @param year
   * @param liaisonPMU
   * @return
   */
  public List<ProjectExpectedStudy> getTable2B(Phase phase, GlobalUnit loggedCrp, int year,
    LiaisonInstitution liaisonPMU) {
    List<ProjectExpectedStudy> popUpProjects = new ArrayList<>();
    List<LiaisonInstitution> liaisonInstitutions = loggedCrp.getLiaisonInstitutions().stream()
      .filter(c -> c.getCrpProgram() != null && c.isActive()
        && c.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue())
      .collect(Collectors.toList());
    liaisonInstitutions.sort(Comparator.comparing(LiaisonInstitution::getAcronym));

    List<PowbEvidencePlannedStudyDTO> flagshipPlannedList =
      this.getFpPlannedList(liaisonInstitutions, phase.getId(), phase, loggedCrp, liaisonPMU);

    for (PowbEvidencePlannedStudyDTO powbEvidencePlannedStudyDTO : flagshipPlannedList) {

      ProjectExpectedStudy expectedStudy = powbEvidencePlannedStudyDTO.getProjectExpectedStudy();
      expectedStudy.getProjectExpectedStudyInfo(phase);

      popUpProjects.add(expectedStudy);

    }

    return popUpProjects;

  }

  /**
   * TOC table 1
   * 
   * @param phase
   * @param flagships
   * @return
   */
  public List<PowbTocListDTO> getTocFlgashipChanges(Phase phase, List<LiaisonInstitution> flagships) {
    List<PowbTocListDTO> tocList = new ArrayList<>();
    for (LiaisonInstitution liaisonInstitution : flagships) {
      PowbTocListDTO powbTocList = new PowbTocListDTO();
      powbTocList.setLiaisonInstitution(liaisonInstitution);
      powbTocList.setOverall("");
      PowbSynthesis powbSynthesis = powbSynthesisManager.findSynthesis(phase.getId(), liaisonInstitution.getId());
      if (powbSynthesis != null) {
        if (powbSynthesis.getPowbToc() != null) {
          if (powbSynthesis.getPowbToc().getTocOverall() != null) {
            powbTocList.setOverall(powbSynthesis.getPowbToc().getTocOverall());
          }
        }
      }
      tocList.add(powbTocList);
    }
    return tocList;
  }


}
