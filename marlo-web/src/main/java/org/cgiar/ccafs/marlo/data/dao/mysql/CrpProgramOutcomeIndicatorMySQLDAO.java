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

import org.cgiar.ccafs.marlo.data.dao.CrpProgramOutcomeIndicatorDAO;
import org.cgiar.ccafs.marlo.data.model.CrpProgramOutcomeIndicator;

import java.util.List;

import com.google.inject.Inject;
import org.hibernate.SessionFactory;

public class CrpProgramOutcomeIndicatorMySQLDAO extends AbstractMarloDAO<CrpProgramOutcomeIndicator, Long> implements CrpProgramOutcomeIndicatorDAO {


  @Inject
  public CrpProgramOutcomeIndicatorMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteCrpProgramOutcomeIndicator(long crpProgramOutcomeIndicatorId) {
    CrpProgramOutcomeIndicator crpProgramOutcomeIndicator = this.find(crpProgramOutcomeIndicatorId);
    crpProgramOutcomeIndicator.setActive(false);
    this.save(crpProgramOutcomeIndicator);
  }

  @Override
  public boolean existCrpProgramOutcomeIndicator(long crpProgramOutcomeIndicatorID) {
    CrpProgramOutcomeIndicator crpProgramOutcomeIndicator = this.find(crpProgramOutcomeIndicatorID);
    if (crpProgramOutcomeIndicator == null) {
      return false;
    }
    return true;

  }

  @Override
  public CrpProgramOutcomeIndicator find(long id) {
    return super.find(CrpProgramOutcomeIndicator.class, id);

  }

  @Override
  public List<CrpProgramOutcomeIndicator> findAll() {
    String query = "from " + CrpProgramOutcomeIndicator.class.getName() + " where is_active=1";
    List<CrpProgramOutcomeIndicator> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public CrpProgramOutcomeIndicator save(CrpProgramOutcomeIndicator crpProgramOutcomeIndicator) {
    if (crpProgramOutcomeIndicator.getId() == null) {
      super.saveEntity(crpProgramOutcomeIndicator);
    } else {
      crpProgramOutcomeIndicator = super.update(crpProgramOutcomeIndicator);
    }


    return crpProgramOutcomeIndicator;
  }


}