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


import org.cgiar.ccafs.marlo.data.dao.RepIndStageStudyDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyInfoManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndStageStudyManager;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudy;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyInfo;
import org.cgiar.ccafs.marlo.data.model.RepIndStageStudy;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisStudiesByRepIndStageStudyDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class RepIndStageStudyManagerImpl implements RepIndStageStudyManager {


  private RepIndStageStudyDAO repIndStageStudyDAO;
  // Managers
  private ProjectExpectedStudyInfoManager projectExpectedStudyInfoManager;

  @Inject
  public RepIndStageStudyManagerImpl(RepIndStageStudyDAO repIndStageStudyDAO,
    ProjectExpectedStudyInfoManager projectExpectedStudyInfoManager) {
    this.repIndStageStudyDAO = repIndStageStudyDAO;
    this.projectExpectedStudyInfoManager = projectExpectedStudyInfoManager;
  }

  @Override
  public void deleteRepIndStageStudy(long repIndStageStudyId) {

    repIndStageStudyDAO.deleteRepIndStageStudy(repIndStageStudyId);
  }

  @Override
  public boolean existRepIndStageStudy(long repIndStageStudyID) {

    return repIndStageStudyDAO.existRepIndStageStudy(repIndStageStudyID);
  }

  @Override
  public List<RepIndStageStudy> findAll() {
    return repIndStageStudyDAO.findAll();
  }

  @Override
  public RepIndStageStudy getRepIndStageStudyById(long repIndStageStudyID) {
    return repIndStageStudyDAO.find(repIndStageStudyID);
  }

  @Override
  public List<ReportSynthesisStudiesByRepIndStageStudyDTO>
    getStudiesByStageStudy(List<ProjectExpectedStudy> selectedProjectStudies, Phase phase) {
    List<ReportSynthesisStudiesByRepIndStageStudyDTO> reportSynthesisStudiesByRepIndStageStudyDTOs = new ArrayList<>();
    List<RepIndStageStudy> stageStudies =
      this.findAll().stream().sorted((o1, o2) -> o1.getName().compareTo(o2.getName())).collect(Collectors.toList());

    if (stageStudies != null) {
      for (RepIndStageStudy repIndStageStudies : stageStudies) {
        if (repIndStageStudies != null && repIndStageStudies.getId() != null) {
          ReportSynthesisStudiesByRepIndStageStudyDTO reportSynthesisStudiesByRepIndStageStudyDTO =
            new ReportSynthesisStudiesByRepIndStageStudyDTO();
          reportSynthesisStudiesByRepIndStageStudyDTO.setRepIndStageStudy(repIndStageStudies);
          List<ProjectExpectedStudy> projectStudiesByRepIndStageStudy = new ArrayList<>();

          for (ProjectExpectedStudy pp : selectedProjectStudies) {
            if (pp != null && pp.getId() != null && pp.isActive()) {
              ProjectExpectedStudyInfo info = pp.getProjectExpectedStudyInfo(phase);
              if (info != null && info.getId() != null) {
                info = projectExpectedStudyInfoManager.getProjectExpectedStudyInfoById(info.getId());
                RepIndStageStudy stage = info.getRepIndStageStudy();
                if (stage != null && stage.getId() != null && stage.getId().equals(repIndStageStudies.getId())) {
                  projectStudiesByRepIndStageStudy.add(pp);
                }
              }
            }
          }

          reportSynthesisStudiesByRepIndStageStudyDTO.setProjectStudies(projectStudiesByRepIndStageStudy);
          reportSynthesisStudiesByRepIndStageStudyDTOs.add(reportSynthesisStudiesByRepIndStageStudyDTO);
        }
      }
    }

    return reportSynthesisStudiesByRepIndStageStudyDTOs.stream()
      .sorted(
        (o1, o2) -> new Integer(o2.getProjectStudies().size()).compareTo(new Integer(o1.getProjectStudies().size())))
      .collect(Collectors.toList());

  }

  @Override
  public RepIndStageStudy saveRepIndStageStudy(RepIndStageStudy repIndStageStudy) {
    return repIndStageStudyDAO.save(repIndStageStudy);
  }
}
