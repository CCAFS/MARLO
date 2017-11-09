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

import org.cgiar.ccafs.marlo.data.dao.FundingSourceInfoDAO;
import org.cgiar.ccafs.marlo.data.model.FundingSourceInfo;

import java.util.List;

import com.google.inject.Inject;
import org.hibernate.SessionFactory;

public class FundingSourceInfoMySQLDAO extends AbstractMarloDAO<FundingSourceInfo, Long>
  implements FundingSourceInfoDAO {


  @Inject
  public FundingSourceInfoMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteFundingSourceInfo(long fundingSourceInfoId) {
    FundingSourceInfo fundingSourceInfo = this.find(fundingSourceInfoId);
    super.delete(fundingSourceInfo);
  }

  @Override
  public boolean existFundingSourceInfo(long fundingSourceInfoID) {
    FundingSourceInfo fundingSourceInfo = this.find(fundingSourceInfoID);
    if (fundingSourceInfo == null) {
      return false;
    }
    return true;

  }

  @Override
  public FundingSourceInfo find(long id) {
    return super.find(FundingSourceInfo.class, id);

  }

  @Override
  public List<FundingSourceInfo> findAll() {
    String query = "from " + FundingSourceInfo.class.getName();
    List<FundingSourceInfo> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public FundingSourceInfo save(FundingSourceInfo fundingSourceInfo) {
    if (fundingSourceInfo.getId() == null) {
      fundingSourceInfo = super.saveEntity(fundingSourceInfo);
    } else {
      fundingSourceInfo = super.update(fundingSourceInfo);
    }


    return fundingSourceInfo;
  }


}