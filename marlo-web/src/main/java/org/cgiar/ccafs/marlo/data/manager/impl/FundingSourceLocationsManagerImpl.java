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


import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.dao.FundingSourceLocationsDAO;
import org.cgiar.ccafs.marlo.data.dao.PhaseDAO;
import org.cgiar.ccafs.marlo.data.manager.FundingSourceLocationsManager;
import org.cgiar.ccafs.marlo.data.model.FundingSourceLocation;
import org.cgiar.ccafs.marlo.data.model.Phase;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author Christian Garcia
 */
@Named
public class FundingSourceLocationsManagerImpl implements FundingSourceLocationsManager {


  private FundingSourceLocationsDAO fundingSourceLocationsDAO;
  private PhaseDAO phaseDAO;

  // Managers


  @Inject
  public FundingSourceLocationsManagerImpl(FundingSourceLocationsDAO fundingSourceLocationsDAO, PhaseDAO phaseDAO) {
    this.fundingSourceLocationsDAO = fundingSourceLocationsDAO;
    this.phaseDAO = phaseDAO;


  }

  private void addFundingSouceLocationPhase(Phase next, long fundingSourceID,
    FundingSourceLocation fundingSourceLocation) {
    Phase phase = phaseDAO.find(next.getId());
    boolean hasLocElement = false;
    if (fundingSourceLocation.getLocElementType() == null) {
      hasLocElement = true;
    }
    List<FundingSourceLocation> locations = new ArrayList<FundingSourceLocation>();

    if (hasLocElement) {
      locations
        .addAll(phase.getFundingSourceLocations().stream()
          .filter(c -> c.isActive() && c.getFundingSource().getId().longValue() == fundingSourceID
            && c.getLocElement() != null
            && fundingSourceLocation.getLocElement().getId().longValue() == c.getLocElement().getId().longValue())
        .collect(Collectors.toList()));
    } else {
      locations.addAll(phase.getFundingSourceLocations().stream()
        .filter(c -> c.isActive() && c.getFundingSource().getId().longValue() == fundingSourceID
          && c.getLocElementType() != null
          && fundingSourceLocation.getLocElementType().getId().longValue() == c.getLocElementType().getId().longValue())
        .collect(Collectors.toList()));
    }


    if (phase.getEditable() != null && phase.getEditable() && locations.isEmpty()) {
      FundingSourceLocation fundingSourceLocationAdd = new FundingSourceLocation();
      fundingSourceLocationAdd.setActive(true);
      fundingSourceLocationAdd.setActiveSince(fundingSourceLocation.getActiveSince());
      fundingSourceLocationAdd.setCreatedBy(fundingSourceLocation.getCreatedBy());
      fundingSourceLocationAdd.setLocElement(fundingSourceLocation.getLocElement());
      fundingSourceLocationAdd.setLocElementType(fundingSourceLocation.getLocElementType());
      fundingSourceLocationAdd.setModificationJustification(fundingSourceLocation.getModificationJustification());
      fundingSourceLocationAdd.setModifiedBy(fundingSourceLocation.getModifiedBy());
      fundingSourceLocationAdd.setPhase(phase);
      fundingSourceLocationAdd.setFundingSource(fundingSourceLocation.getFundingSource());
      fundingSourceLocationsDAO.save(fundingSourceLocationAdd);

    }

    if (phase.getNext() != null) {
      this.addFundingSouceLocationPhase(phase.getNext(), fundingSourceID, fundingSourceLocation);
    }


  }

  public void deleteFundingSourceLocationPhase(Phase next, long fundingSourceID,
    FundingSourceLocation fundingSourceLocation) {
    Phase phase = phaseDAO.find(next.getId());
    boolean hasLocElement = false;
    if (fundingSourceLocation.getLocElementType() == null) {
      hasLocElement = true;
    }


    List<FundingSourceLocation> locations = new ArrayList<FundingSourceLocation>();

    if (hasLocElement) {
      locations
        .addAll(phase.getFundingSourceLocations().stream()
          .filter(c -> c.isActive() && c.getFundingSource().getId().longValue() == fundingSourceID
            && c.getLocElement() != null
            && fundingSourceLocation.getLocElement().getId().longValue() == c.getLocElement().getId().longValue())
        .collect(Collectors.toList()));
    } else {
      locations.addAll(phase.getFundingSourceLocations().stream()
        .filter(c -> c.isActive() && c.getFundingSource().getId().longValue() == fundingSourceID
          && c.getLocElementType() != null
          && fundingSourceLocation.getLocElementType().getId().longValue() == c.getLocElementType().getId().longValue())
        .collect(Collectors.toList()));
    }
    for (FundingSourceLocation location : locations) {
      location.setActive(false);
      this.fundingSourceLocationsDAO.save(location);
    }

    if (phase.getNext() != null) {
      this.deleteFundingSourceLocationPhase(phase.getNext(), fundingSourceID, fundingSourceLocation);

    }


  }

  @Override
  public void deleteFundingSourceLocations(long fundingSourceLocationsId) {

    fundingSourceLocationsDAO.deleteFundingSourceLocations(fundingSourceLocationsId);
    FundingSourceLocation fundingSourceLocations = this.getFundingSourceLocationsById(fundingSourceLocationsId);
    Phase currentPhase = phaseDAO.find(fundingSourceLocations.getPhase().getId());
    if (currentPhase.getDescription().equals(APConstants.PLANNING)) {

      if (fundingSourceLocations.getPhase().getNext() != null) {
        this.deleteFundingSourceLocationPhase(fundingSourceLocations.getPhase().getNext(),
          fundingSourceLocations.getFundingSource().getId(), fundingSourceLocations);
      }
    }
  }

  @Override
  public boolean existFundingSourceLocations(long fundingSourceLocationsID) {

    return fundingSourceLocationsDAO.existFundingSourceLocations(fundingSourceLocationsID);
  }

  @Override
  public List<FundingSourceLocation> findAll() {

    return fundingSourceLocationsDAO.findAll();

  }

  @Override
  public FundingSourceLocation getFundingSourceLocationsById(long fundingSourceLocationsID) {

    return fundingSourceLocationsDAO.find(fundingSourceLocationsID);
  }

  @Override
  public FundingSourceLocation saveFundingSourceLocations(FundingSourceLocation fundingSourceLocations) {

    FundingSourceLocation sourceLocation = fundingSourceLocationsDAO.save(fundingSourceLocations);
    Phase currentPhase = phaseDAO.find(fundingSourceLocations.getPhase().getId());
    if (currentPhase.getDescription().equals(APConstants.PLANNING)) {
      if (fundingSourceLocations.getPhase().getNext() != null) {
        this.addFundingSouceLocationPhase(fundingSourceLocations.getPhase().getNext(),
          fundingSourceLocations.getFundingSource().getId(), fundingSourceLocations);
      }
    }
    return sourceLocation;

  }

}
