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


import org.cgiar.ccafs.marlo.data.dao.ProjectInnovationInfoDAO;
import org.cgiar.ccafs.marlo.data.dao.RepIndStageInnovationDAO;
import org.cgiar.ccafs.marlo.data.manager.RepIndStageInnovationManager;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovation;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationInfo;
import org.cgiar.ccafs.marlo.data.model.RepIndStageInnovation;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisInnovationsByStageDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author Christian Garcia
 */
@Named
public class RepIndStageInnovationManagerImpl implements RepIndStageInnovationManager {


  private RepIndStageInnovationDAO repIndStageInnovationDAO;
  private ProjectInnovationInfoDAO projectInnovationInfoDAO;
  // Managers


  @Inject
  public RepIndStageInnovationManagerImpl(RepIndStageInnovationDAO repIndStageInnovationDAO,
    ProjectInnovationInfoDAO projectInnovationInfoDAO) {
    this.repIndStageInnovationDAO = repIndStageInnovationDAO;
    this.projectInnovationInfoDAO = projectInnovationInfoDAO;


  }

  @Override
  public void deleteRepIndStageInnovation(long repIndStageInnovationId) {

    repIndStageInnovationDAO.deleteRepIndStageInnovation(repIndStageInnovationId);
  }

  @Override
  public boolean existRepIndStageInnovation(long repIndStageInnovationID) {

    return repIndStageInnovationDAO.existRepIndStageInnovation(repIndStageInnovationID);
  }

  @Override
  public List<RepIndStageInnovation> findAll() {

    return repIndStageInnovationDAO.findAll();

  }

  @Override
  public List<ReportSynthesisInnovationsByStageDTO>
    getInnovationsByStageDTO(List<ProjectInnovation> selectedProjectInnovations, Phase phase) {
    List<ReportSynthesisInnovationsByStageDTO> innovationsByStageDTO = new ArrayList<>();
    List<RepIndStageInnovation> stageInnovations =
      this.findAll().stream().sorted((o1, o2) -> o1.getName().compareTo(o2.getName())).collect(Collectors.toList());
    if (stageInnovations != null) {
      for (RepIndStageInnovation stageInnovation : stageInnovations) {
        ReportSynthesisInnovationsByStageDTO reportSynthesisInnovationsByStageDTO =
          new ReportSynthesisInnovationsByStageDTO();
        reportSynthesisInnovationsByStageDTO.setRepIndStageInnovation(stageInnovation);
        List<ProjectInnovation> projectInnovationByStageInnnovation = selectedProjectInnovations.stream()
          .filter(pp -> pp.isActive() && pp.getProjectInnovationInfo(phase) != null
            && pp.getProjectInnovationInfo().getRepIndStageInnovation() != null
            && pp.getProjectInnovationInfo().getRepIndStageInnovation().equals(stageInnovation))
          .collect(Collectors.toList());
        List<ProjectInnovationInfo> projectInnovationInfos = new ArrayList<ProjectInnovationInfo>();
        if (projectInnovationByStageInnnovation != null && !projectInnovationByStageInnnovation.isEmpty()) {
          for (ProjectInnovation projectInnovation : projectInnovationByStageInnnovation) {
            projectInnovationInfos.add(projectInnovation.getProjectInnovationInfo(phase));
          }
          reportSynthesisInnovationsByStageDTO.setProjectInnovationInfos(projectInnovationInfos);
        } else {
          reportSynthesisInnovationsByStageDTO.setProjectInnovationInfos(projectInnovationInfos);
        }

        innovationsByStageDTO.add(reportSynthesisInnovationsByStageDTO);
      }
    }

    return innovationsByStageDTO.stream().sorted((o1, o2) -> new Integer(o2.getProjectInnovationInfos().size())
      .compareTo(new Integer(o1.getProjectInnovationInfos().size()))).collect(Collectors.toList());
  }

  @Override
  public List<ReportSynthesisInnovationsByStageDTO> getInnovationsByStageDTO(Phase phase) {
    List<ReportSynthesisInnovationsByStageDTO> innovationsByStageDTOs = new ArrayList<>();
    List<RepIndStageInnovation> stageInnovations = this.findAll().stream().collect(Collectors.toList());
    if (stageInnovations != null) {
      for (RepIndStageInnovation stageInnovation : stageInnovations) {
        ReportSynthesisInnovationsByStageDTO reportSynthesisInnovationsByStageDTO =
          new ReportSynthesisInnovationsByStageDTO();
        reportSynthesisInnovationsByStageDTO.setRepIndStageInnovation(stageInnovation);
        reportSynthesisInnovationsByStageDTO
          .setProjectInnovationInfos(projectInnovationInfoDAO.getInnovationsByStage(stageInnovation, phase));
        innovationsByStageDTOs.add(reportSynthesisInnovationsByStageDTO);
      }
    }

    return innovationsByStageDTOs.stream().sorted((o1, o2) -> new Integer(o2.getProjectInnovationInfos().size())
      .compareTo(new Integer(o1.getProjectInnovationInfos().size()))).collect(Collectors.toList());
  }

  @Override
  public RepIndStageInnovation getRepIndStageInnovationById(long repIndStageInnovationID) {

    return repIndStageInnovationDAO.find(repIndStageInnovationID);
  }

  @Override
  public RepIndStageInnovation saveRepIndStageInnovation(RepIndStageInnovation repIndStageInnovation) {

    return repIndStageInnovationDAO.save(repIndStageInnovation);
  }

}
