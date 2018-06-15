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


import org.cgiar.ccafs.marlo.data.dao.ProjectExpectedStudyDAO;
import org.cgiar.ccafs.marlo.data.dao.RepIndOrganizationTypeDAO;
import org.cgiar.ccafs.marlo.data.manager.RepIndOrganizationTypeManager;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.RepIndOrganizationType;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisStudiesByOrganizationTypeDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author Christian Garcia
 */
@Named
public class RepIndOrganizationTypeManagerImpl implements RepIndOrganizationTypeManager {


  private RepIndOrganizationTypeDAO repIndOrganizationTypeDAO;
  private ProjectExpectedStudyDAO projectExpectedStudyDAO;
  // Managers


  @Inject
  public RepIndOrganizationTypeManagerImpl(RepIndOrganizationTypeDAO repIndOrganizationTypeDAO,
    ProjectExpectedStudyDAO projectExpectedStudyDAO) {
    this.repIndOrganizationTypeDAO = repIndOrganizationTypeDAO;
    this.projectExpectedStudyDAO = projectExpectedStudyDAO;


  }

  @Override
  public void deleteRepIndOrganizationType(long repIndOrganizationTypeId) {

    repIndOrganizationTypeDAO.deleteRepIndOrganizationType(repIndOrganizationTypeId);
  }

  @Override
  public boolean existRepIndOrganizationType(long repIndOrganizationTypeID) {

    return repIndOrganizationTypeDAO.existRepIndOrganizationType(repIndOrganizationTypeID);
  }

  @Override
  public List<RepIndOrganizationType> findAll() {

    return repIndOrganizationTypeDAO.findAll();

  }

  @Override
  public List<ReportSynthesisStudiesByOrganizationTypeDTO> getOrganizationTypesByStudies(Phase phase) {
    List<ReportSynthesisStudiesByOrganizationTypeDTO> studiesByOrganizationTypeDTOs = new ArrayList<>();
    List<RepIndOrganizationType> organizationTypes =
      this.findAll().stream().sorted((o1, o2) -> o1.getName().compareTo(o2.getName())).collect(Collectors.toList());
    if (organizationTypes != null) {
      for (RepIndOrganizationType organizationType : organizationTypes) {
        ReportSynthesisStudiesByOrganizationTypeDTO ReportSynthesisStudiesByOrganizationTypeDTO =
          new ReportSynthesisStudiesByOrganizationTypeDTO();
        ReportSynthesisStudiesByOrganizationTypeDTO.setRepIndOrganizationType(organizationType);
        ReportSynthesisStudiesByOrganizationTypeDTO
          .setProjectExpectedStudies(projectExpectedStudyDAO.getStudiesByOrganizationType(organizationType, phase));
        studiesByOrganizationTypeDTOs.add(ReportSynthesisStudiesByOrganizationTypeDTO);
      }
    }

    return studiesByOrganizationTypeDTOs.stream().sorted((o1, o2) -> new Integer(o2.getProjectExpectedStudies().size())
      .compareTo(new Integer(o1.getProjectExpectedStudies().size()))).collect(Collectors.toList());
  }

  @Override
  public RepIndOrganizationType getRepIndOrganizationTypeById(long repIndOrganizationTypeID) {

    return repIndOrganizationTypeDAO.find(repIndOrganizationTypeID);
  }

  @Override
  public RepIndOrganizationType saveRepIndOrganizationType(RepIndOrganizationType repIndOrganizationType) {

    return repIndOrganizationTypeDAO.save(repIndOrganizationType);
  }


}
