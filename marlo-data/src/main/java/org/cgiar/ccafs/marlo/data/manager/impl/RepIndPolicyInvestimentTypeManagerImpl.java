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


import org.cgiar.ccafs.marlo.data.dao.RepIndPolicyInvestimentTypeDAO;
import org.cgiar.ccafs.marlo.data.manager.RepIndPolicyInvestimentTypeManager;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicy;
import org.cgiar.ccafs.marlo.data.model.RepIndPolicyInvestimentType;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisPoliciesByRepIndPolicyInvestimentTypeDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author Christian Garcia
 */
@Named
public class RepIndPolicyInvestimentTypeManagerImpl implements RepIndPolicyInvestimentTypeManager {


  private RepIndPolicyInvestimentTypeDAO repIndPolicyInvestimentTypeDAO;
  // Managers


  @Inject
  public RepIndPolicyInvestimentTypeManagerImpl(RepIndPolicyInvestimentTypeDAO repIndPolicyInvestimentTypeDAO) {
    this.repIndPolicyInvestimentTypeDAO = repIndPolicyInvestimentTypeDAO;


  }

  @Override
  public void deleteRepIndPolicyInvestimentType(long repIndPolicyInvestimentTypeId) {

    repIndPolicyInvestimentTypeDAO.deleteRepIndPolicyInvestimentType(repIndPolicyInvestimentTypeId);
  }

  @Override
  public boolean existRepIndPolicyInvestimentType(long repIndPolicyInvestimentTypeID) {

    return repIndPolicyInvestimentTypeDAO.existRepIndPolicyInvestimentType(repIndPolicyInvestimentTypeID);
  }

  @Override
  public List<RepIndPolicyInvestimentType> findAll() {

    return repIndPolicyInvestimentTypeDAO.findAll();

  }

  @Override
  public List<ReportSynthesisPoliciesByRepIndPolicyInvestimentTypeDTO>
    getPoliciesByInvestimentType(List<ProjectPolicy> selectedProjectPolicies, Phase phase) {
    List<ReportSynthesisPoliciesByRepIndPolicyInvestimentTypeDTO> policiesByPolicyInvestimentTypeDTOs =
      new ArrayList<>();
    List<RepIndPolicyInvestimentType> policyInvestimentTypes =
      this.findAll().stream().sorted((o1, o2) -> o1.getName().compareTo(o2.getName())).collect(Collectors.toList());
    if (policyInvestimentTypes != null) {
      for (RepIndPolicyInvestimentType policyInvestimentType : policyInvestimentTypes) {
        ReportSynthesisPoliciesByRepIndPolicyInvestimentTypeDTO reportSynthesisPoliciesByRepIndPolicyInvestimentTypeDTO =
          new ReportSynthesisPoliciesByRepIndPolicyInvestimentTypeDTO();
        reportSynthesisPoliciesByRepIndPolicyInvestimentTypeDTO.setRepIndPolicyInvestimentType(policyInvestimentType);
        List<ProjectPolicy> projectPoliciesByPolicyInvestimentType = selectedProjectPolicies.stream()
          .filter(pp -> pp.isActive() && pp.getProjectPolicyInfo(phase) != null
            && pp.getProjectPolicyInfo().getRepIndPolicyInvestimentType() != null
            && pp.getProjectPolicyInfo().getRepIndPolicyInvestimentType().equals(policyInvestimentType))
          .collect(Collectors.toList());
        if (projectPoliciesByPolicyInvestimentType != null && !projectPoliciesByPolicyInvestimentType.isEmpty()) {
          reportSynthesisPoliciesByRepIndPolicyInvestimentTypeDTO
            .setProjectPolicies(projectPoliciesByPolicyInvestimentType);
        } else {
          reportSynthesisPoliciesByRepIndPolicyInvestimentTypeDTO.setProjectPolicies(new ArrayList<>());
        }

        policiesByPolicyInvestimentTypeDTOs.add(reportSynthesisPoliciesByRepIndPolicyInvestimentTypeDTO);
      }
    }

    return policiesByPolicyInvestimentTypeDTOs.stream()
      .sorted(
        (o1, o2) -> new Integer(o2.getProjectPolicies().size()).compareTo(new Integer(o1.getProjectPolicies().size())))
      .collect(Collectors.toList());
  }

  @Override
  public RepIndPolicyInvestimentType getRepIndPolicyInvestimentTypeById(long repIndPolicyInvestimentTypeID) {

    return repIndPolicyInvestimentTypeDAO.find(repIndPolicyInvestimentTypeID);
  }

  @Override
  public RepIndPolicyInvestimentType saveRepIndPolicyInvestimentType(RepIndPolicyInvestimentType repIndPolicyInvestimentType) {

    return repIndPolicyInvestimentTypeDAO.save(repIndPolicyInvestimentType);
  }


}
