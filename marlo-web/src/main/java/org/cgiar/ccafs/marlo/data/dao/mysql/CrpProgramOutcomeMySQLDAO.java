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

import org.cgiar.ccafs.marlo.data.dao.CrpProgramOutcomeDAO;
import org.cgiar.ccafs.marlo.data.model.CrpProgramOutcome;

import java.util.List;

import com.google.inject.Inject;

public class CrpProgramOutcomeMySQLDAO implements CrpProgramOutcomeDAO {

  private StandardDAO dao;

  @Inject
  public CrpProgramOutcomeMySQLDAO(StandardDAO dao) {
    this.dao = dao;
  }

  @Override
  public boolean deleteCrpProgramOutcome(long crpProgramOutcomeId) {
    CrpProgramOutcome crpProgramOutcome = this.find(crpProgramOutcomeId);
    crpProgramOutcome.setActive(false);
    return this.save(crpProgramOutcome) > 0;
  }

  @Override
  public boolean existCrpProgramOutcome(long crpProgramOutcomeID) {
    CrpProgramOutcome crpProgramOutcome = this.find(crpProgramOutcomeID);
    if (crpProgramOutcome == null) {
      return false;
    }
    return true;

  }

  @Override
  public CrpProgramOutcome find(long id) {
    return dao.find(CrpProgramOutcome.class, id);

  }


  @Override
  public List<CrpProgramOutcome> findAll() {
    String query = "from " + CrpProgramOutcome.class.getName() + " where is_active=1";
    List<CrpProgramOutcome> list = dao.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }


  @Override
  public long save(CrpProgramOutcome crpProgramOutcome) {
    if (crpProgramOutcome.getId() == null) {
      dao.save(crpProgramOutcome);
    } else {
      dao.update(crpProgramOutcome);
    }


    return crpProgramOutcome.getId();
  }


}