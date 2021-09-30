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

/**************
 * @author Diego Perez - CIAT/CCAFS
 **************/

package org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.submissiontools;

import org.cgiar.ccafs.marlo.data.manager.DepthScaleManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectedBenefitsManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectedBenefitsProbabilitiesManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectedBenefitsWeightDescriptionManager;
import org.cgiar.ccafs.marlo.data.model.DepthScales;
import org.cgiar.ccafs.marlo.data.model.ProjectedBenefits;
import org.cgiar.ccafs.marlo.data.model.ProjectedBenefitsProbabilites;
import org.cgiar.ccafs.marlo.data.model.ProjectedBenefitsWeightDescription;
import org.cgiar.ccafs.marlo.rest.dto.DepthDescriptionsDTO;
import org.cgiar.ccafs.marlo.rest.dto.ProjectedBenefitsDTO;
import org.cgiar.ccafs.marlo.rest.dto.ProjectedBenefitsDepthScaleDTO;
import org.cgiar.ccafs.marlo.rest.dto.ProjectedBenefitsProbabilitiesDTO;
import org.cgiar.ccafs.marlo.rest.mappers.DepthScaleMapper;
import org.cgiar.ccafs.marlo.rest.mappers.ProjectedBenefitsMapper;
import org.cgiar.ccafs.marlo.rest.mappers.ProjectedBenefitsProbabilitiesMapper;
import org.cgiar.ccafs.marlo.rest.mappers.ProjectedBenefitsWeightingMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

@Named
public class ProjectedBenefitsItem<T> {

  private ProjectedBenefitsManager projectedBenefitsManager;
  private ProjectedBenefitsMapper projectedBenefitsMapper;
  private DepthScaleManager depthScaleManager;
  private DepthScaleMapper depthScaleMapper;
  private ProjectedBenefitsWeightDescriptionManager projectedBenefitsWeightDescriptionManager;
  private ProjectedBenefitsWeightingMapper projectedBenefitsWeightingMapper;
  private ProjectedBenefitsProbabilitiesManager projectedBenefitsProbabilitiesManager;
  private ProjectedBenefitsProbabilitiesMapper projectedBenefitsProbabilitiesMapper;


  @Inject
  public ProjectedBenefitsItem(ProjectedBenefitsManager projectedBenefitsManager,
    ProjectedBenefitsMapper projectedBenefitsMapper, DepthScaleManager depthScaleManager,
    DepthScaleMapper depthScaleMapper,
    ProjectedBenefitsWeightDescriptionManager projectedBenefitsWeightDescriptionManager,
    ProjectedBenefitsWeightingMapper projectedBenefitsWeightingMapper,
    ProjectedBenefitsProbabilitiesManager projectedBenefitsProbabilitiesManager,
    ProjectedBenefitsProbabilitiesMapper projectedBenefitsProbabilitiesMapper) {
    super();
    this.projectedBenefitsManager = projectedBenefitsManager;
    this.projectedBenefitsMapper = projectedBenefitsMapper;
    this.depthScaleManager = depthScaleManager;
    this.depthScaleMapper = depthScaleMapper;
    this.projectedBenefitsWeightDescriptionManager = projectedBenefitsWeightDescriptionManager;
    this.projectedBenefitsWeightingMapper = projectedBenefitsWeightingMapper;
    this.projectedBenefitsProbabilitiesManager = projectedBenefitsProbabilitiesManager;
    this.projectedBenefitsProbabilitiesMapper = projectedBenefitsProbabilitiesMapper;


  }

  public List<DepthDescriptionsDTO> getDepthDescriptions() {
    List<ProjectedBenefitsWeightDescription> depthDescriptionList = projectedBenefitsWeightDescriptionManager.findAll();
    List<DepthDescriptionsDTO> depthDescriptions = depthDescriptionList.stream()
      .map(c -> this.projectedBenefitsWeightingMapper.projectedBenefitsWeightDescriptionToDepthDescriptionsDTO(c))
      .collect(Collectors.toList());
    return depthDescriptions;
  }

  public List<ProjectedBenefitsDepthScaleDTO> getDepthScales() {
    List<DepthScales> depthScalesList = depthScaleManager.findAll();
    List<ProjectedBenefitsDepthScaleDTO> depthScales = new ArrayList<ProjectedBenefitsDepthScaleDTO>();
    depthScales = depthScalesList.stream()
      .map(c -> this.depthScaleMapper.depthScalesToProjectedBenefitsDepthScaleDTO(c)).collect(Collectors.toList());
    return depthScales;
  }

  /**
   * Get All the projectedBenefits *
   * 
   * @return a List of ProjectedBenefitsDTO .
   */
  public List<ProjectedBenefitsDTO> getProjectedBenefits() {
    List<ProjectedBenefits> projectedBenefitsList = projectedBenefitsManager.findAll();
    List<ProjectedBenefits> newProjectedBenefitsList = new ArrayList<ProjectedBenefits>();
    List<ProjectedBenefitsDTO> projectedBenefitsDTOs = new ArrayList<ProjectedBenefitsDTO>();
    if (projectedBenefitsList != null) {
      for (ProjectedBenefits projectedBenefits : projectedBenefitsList) {
        projectedBenefits
          .setDepthScales(projectedBenefits.getProjectedBenefitsDepthScales().stream().collect(Collectors.toList()));

        projectedBenefits
          .setWeightingList(projectedBenefits.getProjectedBenefitsWeighting().stream().collect(Collectors.toList()));
        newProjectedBenefitsList.add(projectedBenefits);
      }
      projectedBenefitsDTOs = newProjectedBenefitsList.stream()
        .map(c -> this.projectedBenefitsMapper.projectBenefitsToProjectedBenefitsDTO(c)).collect(Collectors.toList());
    }
    return projectedBenefitsDTOs;
  }

  public List<ProjectedBenefitsProbabilitiesDTO> getProjectedBenefitsProbabilities() {
    List<ProjectedBenefitsProbabilites> probabilitiesList = projectedBenefitsProbabilitiesManager.findAll();
    List<ProjectedBenefitsProbabilitiesDTO> probabilities = new ArrayList<ProjectedBenefitsProbabilitiesDTO>();
    probabilities = probabilitiesList.stream().map(c -> this.projectedBenefitsProbabilitiesMapper
      .projectedBenefitsProbabilitiesToProjectedBenefitsProbabilitiesDTO(c)).collect(Collectors.toList());
    return probabilities;
  }

}
