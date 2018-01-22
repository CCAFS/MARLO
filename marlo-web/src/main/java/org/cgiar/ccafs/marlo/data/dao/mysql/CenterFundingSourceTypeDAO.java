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

import javax.inject.Named;
import javax.inject.Inject;
import org.hibernate.SessionFactory;

@Named
public class CenterFundingSourceTypeDAO extends AbstractMarloDAO<CenterFundingSourceType, Long>
  implements ICenterFundingSourceTypeDAO {


  @Inject
  public CenterFundingSourceTypeDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteFundingSourceType(long fundingSourceTypeId) {
    CenterFundingSourceType fundingSourceType = this.find(fundingSourceTypeId);
    fundingSourceType.setActive(false);
    this.save(fundingSourceType);
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
    return super.find(CenterFundingSourceType.class, id);

  }

  @Override
  public List<CenterFundingSourceType> findAll() {
    String query = "from " + CenterFundingSourceType.class.getName();
    List<CenterFundingSourceType> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<CenterFundingSourceType> getFundingSourceTypesByUserId(long userId) {
    String query = "from " + CenterFundingSourceType.class.getName() + " where user_id=" + userId;
    return super.findAll(query);
  }

  @Override
  public CenterFundingSourceType save(CenterFundingSourceType fundingSourceType) {
    if (fundingSourceType.getId() == null) {
      super.saveEntity(fundingSourceType);
    } else {
      fundingSourceType = super.update(fundingSourceType);
    }
    return fundingSourceType;
  }


}