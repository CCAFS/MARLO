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


package org.cgiar.ccafs.marlo.data.dao.mysql;

import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.dao.FundingSourceLocationsDAO;
import org.cgiar.ccafs.marlo.data.model.FundingSourceLocation;
import org.cgiar.ccafs.marlo.data.model.Phase;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.inject.Inject;

public class FundingSourceLocationsMySQLDAO implements FundingSourceLocationsDAO {

  private StandardDAO dao;

  @Inject
  public FundingSourceLocationsMySQLDAO(StandardDAO dao) {
    this.dao = dao;
  }


  public void deleteFundingSourceLocationPhase(Phase next, long fundingSourceID,
    FundingSourceLocation fundingSourceLocation) {
    Phase phase = dao.find(Phase.class, next.getId());
    boolean hasLocElement = false;
    if (fundingSourceLocation.getLocElementType() == null) {
      hasLocElement = true;
    }
    if (phase.getEditable() != null && phase.getEditable()) {

      List<FundingSourceLocation> locations = new ArrayList<FundingSourceLocation>();

      if (hasLocElement) {
        locations.addAll(phase.getFundingSourceLocations().stream()
          .filter(c -> c.isActive() && c.getFundingSource().getId().longValue() == fundingSourceID
            && c.getLocElement() != null
            && fundingSourceLocation.getLocElement().getId().longValue() == c.getLocElement().getId().longValue())
          .collect(Collectors.toList()));
      } else {
        locations.addAll(phase.getFundingSourceLocations().stream().filter(c -> c.isActive()
          && c.getFundingSource().getId().longValue() == fundingSourceID && c.getLocElementType() != null
          && fundingSourceLocation.getLocElementType().getId().longValue() == c.getLocElementType().getId().longValue())
          .collect(Collectors.toList()));
      }
      for (FundingSourceLocation location : locations) {
        location.setActive(false);
        this.dao.update(location);
      }
    }
    if (phase.getNext() != null) {
      this.deleteFundingSourceLocationPhase(phase.getNext(), fundingSourceID, fundingSourceLocation);

    }


  }


  @Override
  public boolean deleteFundingSourceLocations(long fundingSourceLocationsId) {
    FundingSourceLocation fundingSourceLocations = this.find(fundingSourceLocationsId);
    fundingSourceLocations.setActive(false);
    boolean result = dao.update(fundingSourceLocations);
    Phase currentPhase = dao.find(Phase.class, fundingSourceLocations.getPhase().getId());
    if (currentPhase.getDescription().equals(APConstants.PLANNING)) {

      if (fundingSourceLocations.getPhase().getNext() != null) {
        this.deleteFundingSourceLocationPhase(fundingSourceLocations.getPhase().getNext(),
          fundingSourceLocations.getFundingSource().getId(), fundingSourceLocations);
      }
    }
    return result;
  }

  @Override
  public boolean existFundingSourceLocations(long fundingSourceLocationsID) {
    FundingSourceLocation fundingSourceLocations = this.find(fundingSourceLocationsID);
    if (fundingSourceLocations == null) {
      return false;
    }
    return true;

  }

  @Override
  public FundingSourceLocation find(long id) {
    return dao.find(FundingSourceLocation.class, id);

  }

  @Override
  public List<FundingSourceLocation> findAll() {
    String query = "from " + FundingSourceLocation.class.getName() + " where is_active=1";
    List<FundingSourceLocation> list = dao.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public long save(FundingSourceLocation fundingSourceLocations) {
    if (fundingSourceLocations.getId() == null) {
      dao.save(fundingSourceLocations);
    } else {
      dao.update(fundingSourceLocations);
    }


    return fundingSourceLocations.getId();
  }


}