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


import org.cgiar.ccafs.marlo.data.dao.RepIndGeographicScopeDAO;
import org.cgiar.ccafs.marlo.data.manager.RepIndGeographicScopeManager;
import org.cgiar.ccafs.marlo.data.model.ProjectPartnerPartnership;
import org.cgiar.ccafs.marlo.data.model.RepIndGeographicScope;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisPartnershipsByGeographicScopeDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author Christian Garcia
 */
@Named
public class RepIndGeographicScopeManagerImpl implements RepIndGeographicScopeManager {


  private RepIndGeographicScopeDAO repIndGeographicScopeDAO;
  // Managers


  @Inject
  public RepIndGeographicScopeManagerImpl(RepIndGeographicScopeDAO repIndGeographicScopeDAO) {
    this.repIndGeographicScopeDAO = repIndGeographicScopeDAO;


  }

  @Override
  public void deleteRepIndGeographicScope(long repIndGeographicScopeId) {

    repIndGeographicScopeDAO.deleteRepIndGeographicScope(repIndGeographicScopeId);
  }

  @Override
  public boolean existRepIndGeographicScope(long repIndGeographicScopeID) {

    return repIndGeographicScopeDAO.existRepIndGeographicScope(repIndGeographicScopeID);
  }

  @Override
  public List<RepIndGeographicScope> findAll() {

    return repIndGeographicScopeDAO.findAll();

  }

  @Override
  public List<ReportSynthesisPartnershipsByGeographicScopeDTO>
    getPartnershipsByGeographicScopeDTO(List<ProjectPartnerPartnership> projectPartnerPartnerships) {
    List<ReportSynthesisPartnershipsByGeographicScopeDTO> partnershipsByGeographicScopeDTO = new ArrayList<>();

    List<RepIndGeographicScope> repIndGeographicScopes = this.findAll().stream().collect(Collectors.toList());
    if (repIndGeographicScopes != null && !repIndGeographicScopes.isEmpty()) {
      for (RepIndGeographicScope geographicScope : repIndGeographicScopes) {

        ReportSynthesisPartnershipsByGeographicScopeDTO partnershipsByInstitutionTypeDTO =
          new ReportSynthesisPartnershipsByGeographicScopeDTO();
        partnershipsByInstitutionTypeDTO.setRepIndGeographicScope(geographicScope);
        if (projectPartnerPartnerships != null && !projectPartnerPartnerships.isEmpty()) {
          partnershipsByInstitutionTypeDTO.setProjectPartnerPartnerships(projectPartnerPartnerships.stream()
            .filter(ppp -> ppp.getGeographicScope() != null && ppp.getGeographicScope().equals(geographicScope))
            .collect(Collectors.toList()));
        } else {
          partnershipsByInstitutionTypeDTO.setProjectPartnerPartnerships(new ArrayList<ProjectPartnerPartnership>());
        }
        partnershipsByGeographicScopeDTO.add(partnershipsByInstitutionTypeDTO);

      }
    }
    return partnershipsByGeographicScopeDTO.stream()
      .sorted((d1, d2) -> new Integer(d2.getProjectPartnerPartnerships().size())
        .compareTo(new Integer(d1.getProjectPartnerPartnerships().size())))
      .collect(Collectors.toList());
  }

  @Override
  public RepIndGeographicScope getRepIndGeographicScopeById(long repIndGeographicScopeID) {

    return repIndGeographicScopeDAO.find(repIndGeographicScopeID);
  }

  @Override
  public RepIndGeographicScope saveRepIndGeographicScope(RepIndGeographicScope repIndGeographicScope) {

    return repIndGeographicScopeDAO.save(repIndGeographicScope);
  }

}
