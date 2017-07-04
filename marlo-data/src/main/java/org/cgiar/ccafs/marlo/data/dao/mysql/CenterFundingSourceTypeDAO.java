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

import org.cgiar.ccafs.marlo.data.dao.ICenterFundingSourceTypeDAO;
import org.cgiar.ccafs.marlo.data.model.CenterFundingSourceType;

import java.util.List;

import com.google.inject.Inject;

public class CenterFundingSourceTypeDAO implements ICenterFundingSourceTypeDAO {

  private StandardDAO dao;

  @Inject
  public CenterFundingSourceTypeDAO(StandardDAO dao) {
    this.dao = dao;
  }

  @Override
  public boolean deleteFundingSourceType(long fundingSourceTypeId) {
    CenterFundingSourceType fundingSourceType = this.find(fundingSourceTypeId);
    fundingSourceType.setActive(false);
    return this.save(fundingSourceType) > 0;
  }

  @Override
  public boolean existFundingSourceType(long fundingSourceTypeID) {
    CenterFundingSourceType fundingSourceType = this.find(fundingSourceTypeID);
    if (fundingSourceType == null) {
      return false;
    }
    return true;

  }

  @Override
  public CenterFundingSourceType find(long id) {
    return dao.find(CenterFundingSourceType.class, id);

  }

  @Override
  public List<CenterFundingSourceType> findAll() {
    String query = "from " + CenterFundingSourceType.class.getName();
    List<CenterFundingSourceType> list = dao.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<CenterFundingSourceType> getFundingSourceTypesByUserId(long userId) {
    String query = "from " + CenterFundingSourceType.class.getName() + " where user_id=" + userId;
    return dao.findAll(query);
  }

  @Override
  public long save(CenterFundingSourceType fundingSourceType) {
    if (fundingSourceType.getId() == null) {
      dao.save(fundingSourceType);
    } else {
      dao.update(fundingSourceType);
    }
    return fundingSourceType.getId();
  }


}