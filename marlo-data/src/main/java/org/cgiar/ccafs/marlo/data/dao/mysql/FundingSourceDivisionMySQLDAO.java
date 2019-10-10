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

import org.cgiar.ccafs.marlo.data.dao.FundingSourceDivisionDAO;
import org.cgiar.ccafs.marlo.data.model.FundingSourceDivision;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class FundingSourceDivisionMySQLDAO extends AbstractMarloDAO<FundingSourceDivision, Long>
  implements FundingSourceDivisionDAO {


  @Inject
  public FundingSourceDivisionMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }


  @Override
  public void deleteFundingSourceDivision(long fundingSourceDivisionId) {
    FundingSourceDivision fundingSourceDivision = this.find(fundingSourceDivisionId);
    super.delete(fundingSourceDivision);


  }


  @Override
  public boolean existFundingSourceDivision(long fundingSourceDivisionID) {
    FundingSourceDivision fundingSourceDivision = this.find(fundingSourceDivisionID);
    if (fundingSourceDivision == null) {
      return false;
    }
    return true;

  }

  @Override
  public FundingSourceDivision find(long id) {
    return super.find(FundingSourceDivision.class, id);

  }

  @Override
  public List<FundingSourceDivision> findAll() {
    String query = "from " + FundingSourceDivision.class.getName() + " where is_active=1";
    List<FundingSourceDivision> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public FundingSourceDivision save(FundingSourceDivision fundingSourceDivision) {
    if (fundingSourceDivision.getId() == null) {
      super.saveEntity(fundingSourceDivision);
    } else {
      fundingSourceDivision = super.update(fundingSourceDivision);
    }


    return fundingSourceDivision;
  }


}