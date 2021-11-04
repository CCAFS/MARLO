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


import org.cgiar.ccafs.marlo.data.dao.RepIndStageProcessDAO;
import org.cgiar.ccafs.marlo.data.manager.RepIndStageProcessManager;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicy;
import org.cgiar.ccafs.marlo.data.model.RepIndStageProcess;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisPoliciesByRepIndStageProcessDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class RepIndStageProcessManagerImpl implements RepIndStageProcessManager {


  private RepIndStageProcessDAO repIndStageProcessDAO;
  // Managers


  @Inject
  public RepIndStageProcessManagerImpl(RepIndStageProcessDAO repIndStageProcessDAO) {
    this.repIndStageProcessDAO = repIndStageProcessDAO;


  }

  @Override
  public void deleteRepIndStageProcess(long repIndStageProcessId) {

    repIndStageProcessDAO.deleteRepIndStageProcess(repIndStageProcessId);
  }

  @Override
  public boolean existRepIndStageProcess(long repIndStageProcessID) {

    return repIndStageProcessDAO.existRepIndStageProcess(repIndStageProcessID);
  }

  @Override
  public List<RepIndStageProcess> findAll() {

    return repIndStageProcessDAO.findAll();

  }

  @Override
  public List<ReportSynthesisPoliciesByRepIndStageProcessDTO>
    getPoliciesByStageProcess(List<ProjectPolicy> selectedProjectPolicies, Phase phase) {
    List<ReportSynthesisPoliciesByRepIndStageProcessDTO> reportSynthesisPoliciesByRepIndStageProcessDTOs =
      new ArrayList<>();
    List<RepIndStageProcess> stageProcess =
      this.findAll().stream().filter(o -> o != null && o.getId() != null && o.getRepIndStageStudy() != null)
        .sorted((o1, o2) -> o1.getName().compareTo(o2.getName())).collect(Collectors.toList());
    if (stageProcess != null) {
      for (RepIndStageProcess repIndStageProcess : stageProcess) {
        ReportSynthesisPoliciesByRepIndStageProcessDTO reportSynthesisPoliciesByRepIndStageProcessDTO =
          new ReportSynthesisPoliciesByRepIndStageProcessDTO();
        reportSynthesisPoliciesByRepIndStageProcessDTO.setRepIndStageProcess(repIndStageProcess);
        List<ProjectPolicy> projectPoliciesByRepIndStageProcess = selectedProjectPolicies.stream()
          .filter(pp -> pp.isActive() && pp.getProjectPolicyInfo(phase) != null
            && pp.getProjectPolicyInfo().getRepIndStageProcess() != null
            && pp.getProjectPolicyInfo().getRepIndStageProcess().equals(repIndStageProcess))
          .collect(Collectors.toList());
        if (projectPoliciesByRepIndStageProcess != null && !projectPoliciesByRepIndStageProcess.isEmpty()) {
          reportSynthesisPoliciesByRepIndStageProcessDTO.setProjectPolicies(projectPoliciesByRepIndStageProcess);
        } else {
          reportSynthesisPoliciesByRepIndStageProcessDTO.setProjectPolicies(new ArrayList<>());
        }

        reportSynthesisPoliciesByRepIndStageProcessDTOs.add(reportSynthesisPoliciesByRepIndStageProcessDTO);
      }
    }

    return reportSynthesisPoliciesByRepIndStageProcessDTOs.stream()
      .sorted(
        (o1, o2) -> new Integer(o2.getProjectPolicies().size()).compareTo(new Integer(o1.getProjectPolicies().size())))
      .collect(Collectors.toList());
  }

  @Override
  public RepIndStageProcess getRepIndStageProcessById(long repIndStageProcessID) {

    return repIndStageProcessDAO.find(repIndStageProcessID);
  }

  @Override
  public RepIndStageProcess saveRepIndStageProcess(RepIndStageProcess repIndStageProcess) {

    return repIndStageProcessDAO.save(repIndStageProcess);
  }


}
