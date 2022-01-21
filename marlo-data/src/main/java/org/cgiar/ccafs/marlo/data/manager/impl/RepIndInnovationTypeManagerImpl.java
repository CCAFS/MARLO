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


import org.cgiar.ccafs.marlo.data.dao.RepIndInnovationTypeDAO;
import org.cgiar.ccafs.marlo.data.manager.RepIndInnovationTypeManager;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovation;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationInfo;
import org.cgiar.ccafs.marlo.data.model.RepIndInnovationType;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisInnovationsByTypeDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author Christian Garcia
 */
@Named
public class RepIndInnovationTypeManagerImpl implements RepIndInnovationTypeManager {


  private RepIndInnovationTypeDAO repIndInnovationTypeDAO;
  // Managers


  @Inject
  public RepIndInnovationTypeManagerImpl(RepIndInnovationTypeDAO repIndInnovationTypeDAO) {
    this.repIndInnovationTypeDAO = repIndInnovationTypeDAO;


  }

  @Override
  public void deleteRepIndInnovationType(long repIndInnovationTypeId) {

    repIndInnovationTypeDAO.deleteRepIndInnovationType(repIndInnovationTypeId);
  }

  @Override
  public boolean existRepIndInnovationType(long repIndInnovationTypeID) {

    return repIndInnovationTypeDAO.existRepIndInnovationType(repIndInnovationTypeID);
  }

  @Override
  public List<RepIndInnovationType> findAll() {

    return repIndInnovationTypeDAO.findAll();

  }

  @Override
  public List<ReportSynthesisInnovationsByTypeDTO>
    getInnovationsByTypeDTO(List<ProjectInnovation> selectedProjectInnovations, Phase phase) {
    List<ReportSynthesisInnovationsByTypeDTO> innovationsByTypeDTO = new ArrayList<>();
    List<RepIndInnovationType> innovationTypes =
      this.findAll().stream().sorted((o1, o2) -> o1.getName().compareTo(o2.getName())).collect(Collectors.toList());
    if (innovationTypes != null) {
      for (RepIndInnovationType innovationType : innovationTypes) {
        ReportSynthesisInnovationsByTypeDTO reportSynthesisInnovationsByTypeDTO =
          new ReportSynthesisInnovationsByTypeDTO();
        reportSynthesisInnovationsByTypeDTO.setRepIndInnovationType(innovationType);
        List<ProjectInnovation> projectInnovationByInnnovationType = selectedProjectInnovations.stream()
          .filter(pp -> pp.isActive() && pp.getProjectInnovationInfo(phase) != null
            && pp.getProjectInnovationInfo().getRepIndInnovationType() != null
            && pp.getProjectInnovationInfo().getRepIndInnovationType().equals(innovationType))
          .collect(Collectors.toList());
        List<ProjectInnovationInfo> projectInnovationInfos = new ArrayList<ProjectInnovationInfo>();
        if (projectInnovationByInnnovationType != null && !projectInnovationByInnnovationType.isEmpty()) {
          for (ProjectInnovation projectInnovation : projectInnovationByInnnovationType) {
            projectInnovationInfos.add(projectInnovation.getProjectInnovationInfo(phase));
          }
          reportSynthesisInnovationsByTypeDTO.setProjectInnovationInfos(projectInnovationInfos);
        } else {
          reportSynthesisInnovationsByTypeDTO.setProjectInnovationInfos(projectInnovationInfos);
        }

        innovationsByTypeDTO.add(reportSynthesisInnovationsByTypeDTO);
      }
    }

    return innovationsByTypeDTO.stream().sorted((o1, o2) -> new Integer(o2.getProjectInnovationInfos().size())
      .compareTo(new Integer(o1.getProjectInnovationInfos().size()))).collect(Collectors.toList());
  }

  @Override
  public RepIndInnovationType getRepIndInnovationTypeById(long repIndInnovationTypeID) {

    return repIndInnovationTypeDAO.find(repIndInnovationTypeID);
  }

  @Override
  public List<RepIndInnovationType> oneCGIARFindAll() {

    return repIndInnovationTypeDAO.oneCGIARFindAll();

  }

  @Override
  public RepIndInnovationType saveRepIndInnovationType(RepIndInnovationType repIndInnovationType) {

    return repIndInnovationTypeDAO.save(repIndInnovationType);
  }


}
