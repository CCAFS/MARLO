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

import org.cgiar.ccafs.marlo.data.dao.FundingSourceDAO;
import org.cgiar.ccafs.marlo.data.model.FundingSource;

import java.util.List;

import com.google.inject.Inject;

public class FundingSourceMySQLDAO implements FundingSourceDAO {

  private StandardDAO dao;

  @Inject
  public FundingSourceMySQLDAO(StandardDAO dao) {
    this.dao = dao;
  }

  @Override
  public boolean deleteFundingSource(long fundingSourceId) {
    FundingSource fundingSource = this.find(fundingSourceId);
    fundingSource.setActive(false);
    return this.save(fundingSource) > 0;
  }

  @Override
  public boolean existFundingSource(long fundingSourceID) {
    FundingSource fundingSource = this.find(fundingSourceID);
    if (fundingSource == null) {
      return false;
    }
    return true;

  }

  @Override
  public FundingSource find(long id) {
    return dao.find(FundingSource.class, id);

  }

  @Override
  public List<FundingSource> findAll() {
    String query = "from " + FundingSource.class.getName() + " where is_active=1";
    List<FundingSource> list = dao.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public long save(FundingSource fundingSource) {
    if (fundingSource.getId() == null) {
      dao.save(fundingSource);
    } else {
      dao.update(fundingSource);
    }


    return fundingSource.getId();
  }


}