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

import org.cgiar.ccafs.marlo.data.dao.SdgContributionDAO;
import org.cgiar.ccafs.marlo.data.model.SdgContribution;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class SdgContributionMySQLDAO extends AbstractMarloDAO<SdgContribution, Long> implements SdgContributionDAO {


  @Inject
  public SdgContributionMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteSdgContribution(long sdgContributionId) {
    SdgContribution sdgContribution = this.find(sdgContributionId);
    sdgContribution.setActive(false);
    this.update(sdgContribution);
  }

  @Override
  public boolean existSdgContribution(long sdgContributionID) {
    SdgContribution sdgContribution = this.find(sdgContributionID);
    if (sdgContribution == null) {
      return false;
    }
    return true;

  }

  @Override
  public SdgContribution find(long id) {
    return super.find(SdgContribution.class, id);

  }

  @Override
  public List<SdgContribution> findAll() {
    String query = "from " + SdgContribution.class.getName() + " where is_active=1";
    List<SdgContribution> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<SdgContribution> findAllByPhase(long phaseId) {
    String query = "from " + SdgContribution.class.getName() + " where is_active=1 and id_phase=" + phaseId;
    List<SdgContribution> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public SdgContribution save(SdgContribution sdgContribution) {
    if (sdgContribution.getId() == null) {
      super.saveEntity(sdgContribution);
    } else {
      sdgContribution = super.update(sdgContribution);
    }


    return sdgContribution;
  }


}