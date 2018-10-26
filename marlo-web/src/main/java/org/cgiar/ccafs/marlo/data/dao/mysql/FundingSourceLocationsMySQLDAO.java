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

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named
public class FundingSourceLocationsMySQLDAO extends AbstractMarloDAO<FundingSourceLocation, Long>
  implements FundingSourceLocationsDAO {

  private Logger LOG = LoggerFactory.getLogger(FundingSourceLocationsMySQLDAO.class);


  @Inject
  public FundingSourceLocationsMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }


  @Override
  public void deleteAllFundingSourceLocationsForFundingSource(long fundingSourceId) {
    int rowCount = this.getSessionFactory().getCurrentSession()
      .createQuery("UPDATE FundingSourceLocation fsl SET active = FALSE WHERE fsl.fundingSource.id = :fundingSourceId ")
      .setParameter("fundingSourceId", fundingSourceId).executeUpdate();

    LOG.debug(rowCount + " FundingSourceLocation rows were set to inactive");
  }

  @Override
  public void deleteFundingSourceLocations(long fundingSourceLocationsId) {
    FundingSourceLocation fundingSourceLocations = this.find(fundingSourceLocationsId);
    fundingSourceLocations.setActive(false);
    super.update(fundingSourceLocations);
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
  public List<FundingSourceLocation> findAllByFundingSourceId(Long fundingSourceId) {
    String queryString = "SELECT fsl FROM FundingSourceLocation fsl " + "WHERE fsl.active = TRUE "
      + "AND fsl.fundingSource.id = :fundingSourceId";
    List<FundingSourceLocation> fundingSourceLocations = this.getSessionFactory().getCurrentSession()
      .createQuery(queryString).setParameter("fundingSourceId", fundingSourceId).list();

    return fundingSourceLocations;
  }


  @Override
  public FundingSourceLocation save(FundingSourceLocation fundingSourceLocations) {
    if (fundingSourceLocations.getId() == null) {
      super.saveEntity(fundingSourceLocations);
    } else {
      fundingSourceLocations = super.update(fundingSourceLocations);
    }

    return fundingSourceLocations;
  }

}