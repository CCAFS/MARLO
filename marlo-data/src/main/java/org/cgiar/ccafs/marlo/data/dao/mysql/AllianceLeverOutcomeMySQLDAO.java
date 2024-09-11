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

import org.cgiar.ccafs.marlo.data.dao.AllianceLeverOutcomeDAO;
import org.cgiar.ccafs.marlo.data.model.AllianceLeverOutcome;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class AllianceLeverOutcomeMySQLDAO extends AbstractMarloDAO<AllianceLeverOutcome, Long> implements AllianceLeverOutcomeDAO {


  @Inject
  public AllianceLeverOutcomeMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteAllianceLeverOutcome(long allianceLeverOutcomeId) {
    AllianceLeverOutcome allianceLeverOutcome = this.find(allianceLeverOutcomeId);
    allianceLeverOutcome.setActive(false);
    this.update(allianceLeverOutcome);
  }

  @Override
  public boolean existAllianceLeverOutcome(long allianceLeverOutcomeID) {
    AllianceLeverOutcome allianceLeverOutcome = this.find(allianceLeverOutcomeID);
    if (allianceLeverOutcome == null) {
      return false;
    }
    return true;

  }

  @Override
  public AllianceLeverOutcome find(long id) {
    return super.find(AllianceLeverOutcome.class, id);

  }

  @Override
  public List<AllianceLeverOutcome> findAll() {
    String query = "from " + AllianceLeverOutcome.class.getName() + " where is_active=1";
    List<AllianceLeverOutcome> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public AllianceLeverOutcome save(AllianceLeverOutcome allianceLeverOutcome) {
    if (allianceLeverOutcome.getId() == null) {
      super.saveEntity(allianceLeverOutcome);
    } else {
      allianceLeverOutcome = super.update(allianceLeverOutcome);
    }


    return allianceLeverOutcome;
  }


}