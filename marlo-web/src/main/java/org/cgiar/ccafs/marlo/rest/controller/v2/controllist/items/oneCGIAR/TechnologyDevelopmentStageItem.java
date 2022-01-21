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

package org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.oneCGIAR;

import org.cgiar.ccafs.marlo.data.manager.OneCGIARTechnologyDevelopmentStageManager;
import org.cgiar.ccafs.marlo.data.model.OneCGIARTechnologyDevelopmentStage;
import org.cgiar.ccafs.marlo.rest.dto.TechnologyDevelopmentStageDTO;
import org.cgiar.ccafs.marlo.rest.mappers.TechnologyDevelopmentStageMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Named
public class TechnologyDevelopmentStageItem<T> {

  private static final Logger LOG = LoggerFactory.getLogger(TechnologyDevelopmentStageItem.class);

  // mappers
  private TechnologyDevelopmentStageMapper technologyDeploymentStageMapper;

  // managers
  private OneCGIARTechnologyDevelopmentStageManager oneCGIARTechnologyDeploymentStageManager;

  @Inject
  public TechnologyDevelopmentStageItem(TechnologyDevelopmentStageMapper technologyDeploymentStageMapper,
    OneCGIARTechnologyDevelopmentStageManager oneCGIARTechnologyDeploymentStageManager) {
    super();
    this.technologyDeploymentStageMapper = technologyDeploymentStageMapper;
    this.oneCGIARTechnologyDeploymentStageManager = oneCGIARTechnologyDeploymentStageManager;
  }

  public ResponseEntity<List<TechnologyDevelopmentStageDTO>> getAll() {
    List<OneCGIARTechnologyDevelopmentStage> oneCGIARTechnologyDeploymentStages =
      this.oneCGIARTechnologyDeploymentStageManager.getAll();

    List<TechnologyDevelopmentStageDTO> technologyDeploymentStageDTOs =
      CollectionUtils.emptyIfNull(oneCGIARTechnologyDeploymentStages).stream()
        .map(this.technologyDeploymentStageMapper::technologyDevelopmentStageToTechnologyDevelopmentStageDTO)
        .collect(Collectors.toList());

    return Optional.ofNullable(technologyDeploymentStageDTOs).map(result -> new ResponseEntity<>(result, HttpStatus.OK))
      .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

}
