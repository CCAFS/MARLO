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

package org.cgiar.ccafs.marlo.rest.controller.controllist.items.powbar;

import org.cgiar.ccafs.marlo.data.manager.RepIndContributionOfCrpManager;
import org.cgiar.ccafs.marlo.data.model.RepIndContributionOfCrp;
import org.cgiar.ccafs.marlo.rest.dto.ContributionOfCrpDTO;
import org.cgiar.ccafs.marlo.rest.mappers.ContributionOfCrpMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */

@Named
public class ContributionOfCrpItem<T> {

  private RepIndContributionOfCrpManager repIndContributionOfCrpManager;
  private ContributionOfCrpMapper contributionOfCrpMapper;

  @Inject
  public ContributionOfCrpItem(RepIndContributionOfCrpManager repIndContributionOfCrpManager,
    ContributionOfCrpMapper contributionOfCrpMapper) {
    super();
    this.repIndContributionOfCrpManager = repIndContributionOfCrpManager;
    this.contributionOfCrpMapper = contributionOfCrpMapper;
  }

  /**
   * Find a Contribution of CRP requesting by id
   * 
   * @param id
   * @return a ContributionOfCrpDTO with the Contribution of CRP data.
   */
  public ResponseEntity<ContributionOfCrpDTO> findContributionOfCrpById(Long id) {
    RepIndContributionOfCrp repIndContributionOfCrp = repIndContributionOfCrpManager.getRepIndContributionOfCrpById(id);
    return Optional.ofNullable(repIndContributionOfCrp)
      .map(contributionOfCrpMapper::repIndContributionOfCrpToContributionOfCrpDTO)
      .map(result -> new ResponseEntity<>(result, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  /**
   * Get All the Contribution of CRP Items *
   * 
   * @return a List of ContributionOfCrpDTO with all RepIndContributionOfCrp Items.
   */
  public List<ContributionOfCrpDTO> getAllContributionOfCrps() {
    if (repIndContributionOfCrpManager.findAll() != null) {
      List<RepIndContributionOfCrp> repIndContributionOfCrps =
        new ArrayList<>(repIndContributionOfCrpManager.findAll());
      List<ContributionOfCrpDTO> contributionOfCrpDTOs =
        repIndContributionOfCrps.stream().map(repIndContributionOfCrpEntity -> contributionOfCrpMapper
          .repIndContributionOfCrpToContributionOfCrpDTO(repIndContributionOfCrpEntity)).collect(Collectors.toList());
      return contributionOfCrpDTOs;
    } else {
      return null;
    }
  }


}
