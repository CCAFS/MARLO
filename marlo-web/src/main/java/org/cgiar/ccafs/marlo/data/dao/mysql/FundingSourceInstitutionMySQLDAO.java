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

import org.cgiar.ccafs.marlo.data.dao.FundingSourceInstitutionDAO;
import org.cgiar.ccafs.marlo.data.model.FundingSourceInstitution;

import java.util.List;

import javax.inject.Named;
import javax.inject.Inject;
import org.hibernate.SessionFactory;

@Named
public class FundingSourceInstitutionMySQLDAO extends AbstractMarloDAO<FundingSourceInstitution, Long> implements FundingSourceInstitutionDAO {


  @Inject
  public FundingSourceInstitutionMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteFundingSourceInstitution(long fundingSourceInstitutionId) {
    FundingSourceInstitution fundingSourceInstitution = this.find(fundingSourceInstitutionId);

    super.delete(fundingSourceInstitution);
  }

  @Override
  public boolean existFundingSourceInstitution(long fundingSourceInstitutionID) {
    FundingSourceInstitution fundingSourceInstitution = this.find(fundingSourceInstitutionID);
    if (fundingSourceInstitution == null) {
      return false;
    }
    return true;

  }

  @Override
  public FundingSourceInstitution find(long id) {
    return super.find(FundingSourceInstitution.class, id);

  }

  @Override
  public List<FundingSourceInstitution> findAll() {
    String query = "from " + FundingSourceInstitution.class.getName() + " where is_active=1";
    List<FundingSourceInstitution> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public FundingSourceInstitution save(FundingSourceInstitution fundingSourceInstitution) {
    if (fundingSourceInstitution.getId() == null) {
      super.saveEntity(fundingSourceInstitution);
    } else {
      fundingSourceInstitution = super.update(fundingSourceInstitution);
    }


    return fundingSourceInstitution;
  }


}