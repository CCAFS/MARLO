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

import org.cgiar.ccafs.marlo.data.dao.FundingSourceLocationsDAO;
import org.cgiar.ccafs.marlo.data.model.FundingSourceLocation;

import java.util.List;

import com.google.inject.Inject;
import org.hibernate.SessionFactory;

public class FundingSourceLocationsMySQLDAO extends AbstractMarloDAO implements FundingSourceLocationsDAO {


  @Inject
  public FundingSourceLocationsMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public boolean deleteFundingSourceLocations(long fundingSourceLocationsId) {
    FundingSourceLocation fundingSourceLocations = this.find(fundingSourceLocationsId);
    fundingSourceLocations.setActive(false);
    return this.save(fundingSourceLocations) > 0;
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
    return super.find(FundingSourceLocation.class, id);

  }

  @Override
  public List<FundingSourceLocation> findAll() {
    String query = "from " + FundingSourceLocation.class.getName() + " where is_active=1";
    List<FundingSourceLocation> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public long save(FundingSourceLocation fundingSourceLocations) {
    if (fundingSourceLocations.getId() == null) {
      super.save(fundingSourceLocations);
    } else {
      super.update(fundingSourceLocations);
    }


    return fundingSourceLocations.getId();
  }


}