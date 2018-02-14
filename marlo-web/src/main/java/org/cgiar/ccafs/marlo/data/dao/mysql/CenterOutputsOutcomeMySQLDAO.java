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

import org.cgiar.ccafs.marlo.data.dao.CenterOutputsOutcomeDAO;
import org.cgiar.ccafs.marlo.data.model.CenterOutputsOutcome;

import java.util.List;

import javax.inject.Named;
import javax.inject.Inject;
import org.hibernate.SessionFactory;

@Named
public class CenterOutputsOutcomeMySQLDAO extends AbstractMarloDAO<CenterOutputsOutcome, Long> implements CenterOutputsOutcomeDAO {


  @Inject
  public CenterOutputsOutcomeMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteCenterOutputsOutcome(long centerOutputsOutcomeId) {
    CenterOutputsOutcome centerOutputsOutcome = this.find(centerOutputsOutcomeId);
    centerOutputsOutcome.setActive(false);
    this.save(centerOutputsOutcome);
  }

  @Override
  public boolean existCenterOutputsOutcome(long centerOutputsOutcomeID) {
    CenterOutputsOutcome centerOutputsOutcome = this.find(centerOutputsOutcomeID);
    if (centerOutputsOutcome == null) {
      return false;
    }
    return true;

  }

  @Override
  public CenterOutputsOutcome find(long id) {
    return super.find(CenterOutputsOutcome.class, id);

  }

  @Override
  public List<CenterOutputsOutcome> findAll() {
    String query = "from " + CenterOutputsOutcome.class.getName() + " where is_active=1";
    List<CenterOutputsOutcome> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public CenterOutputsOutcome save(CenterOutputsOutcome centerOutputsOutcome) {
    if (centerOutputsOutcome.getId() == null) {
      super.saveEntity(centerOutputsOutcome);
    } else {
      centerOutputsOutcome = super.update(centerOutputsOutcome);
    }


    return centerOutputsOutcome;
  }


}