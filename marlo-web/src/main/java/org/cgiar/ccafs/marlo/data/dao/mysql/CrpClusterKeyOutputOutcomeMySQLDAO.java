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

import org.cgiar.ccafs.marlo.data.dao.CrpClusterKeyOutputOutcomeDAO;
import org.cgiar.ccafs.marlo.data.model.CrpClusterKeyOutputOutcome;

import java.util.List;

import com.google.inject.Inject;
import org.hibernate.SessionFactory;

public class CrpClusterKeyOutputOutcomeMySQLDAO extends AbstractMarloDAO implements CrpClusterKeyOutputOutcomeDAO {


  @Inject
  public CrpClusterKeyOutputOutcomeMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public boolean deleteCrpClusterKeyOutputOutcome(long crpClusterKeyOutputOutcomeId) {
    CrpClusterKeyOutputOutcome crpClusterKeyOutputOutcome = this.find(crpClusterKeyOutputOutcomeId);
    crpClusterKeyOutputOutcome.setActive(false);
    return this.save(crpClusterKeyOutputOutcome) > 0;
  }

  @Override
  public boolean existCrpClusterKeyOutputOutcome(long crpClusterKeyOutputOutcomeID) {
    CrpClusterKeyOutputOutcome crpClusterKeyOutputOutcome = this.find(crpClusterKeyOutputOutcomeID);
    if (crpClusterKeyOutputOutcome == null) {
      return false;
    }
    return true;

  }

  @Override
  public CrpClusterKeyOutputOutcome find(long id) {
    return super.find(CrpClusterKeyOutputOutcome.class, id);

  }

  @Override
  public List<CrpClusterKeyOutputOutcome> findAll() {
    String query = "from " + CrpClusterKeyOutputOutcome.class.getName() + " where is_active=1";
    List<CrpClusterKeyOutputOutcome> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public long save(CrpClusterKeyOutputOutcome crpClusterKeyOutputOutcome) {
    if (crpClusterKeyOutputOutcome.getId() == null) {
      super.save(crpClusterKeyOutputOutcome);
    } else {
      super.update(crpClusterKeyOutputOutcome);
    }


    return crpClusterKeyOutputOutcome.getId();
  }


}