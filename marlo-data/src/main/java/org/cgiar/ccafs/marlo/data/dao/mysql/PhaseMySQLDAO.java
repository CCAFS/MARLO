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

import org.cgiar.ccafs.marlo.data.dao.PhaseDAO;
import org.cgiar.ccafs.marlo.data.model.Phase;

import java.util.List;

import com.google.inject.Inject;

public class PhaseMySQLDAO implements PhaseDAO {

  private StandardDAO dao;

  @Inject
  public PhaseMySQLDAO(StandardDAO dao) {
    this.dao = dao;
  }

  @Override
  public boolean deletePhase(long phaseId) {
    Phase phase = this.find(phaseId);

    return this.dao.delete(phase);
  }

  @Override
  public boolean existPhase(long phaseID) {
    Phase phase = this.find(phaseID);
    if (phase == null) {
      return false;
    }
    return true;

  }

  @Override
  public Phase find(long id) {
    return dao.find(Phase.class, id);

  }

  @Override
  public List<Phase> findAll() {
    String query = "from " + Phase.class.getName() + " ";
    List<Phase> list = dao.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public Phase findCycle(String cylce, int year, long crpId) {
    String query =
      "from " + Phase.class.getName() + " where description='" + cylce + "' and year=" + year + " and crp_id=" + crpId;
    List<Phase> list = dao.findAll(query);
    if (list.size() > 0) {
      return list.get(0);
    }
    return null;
  }

  @Override
  public long save(Phase phase) {
    if (phase.getId() == null) {
      dao.save(phase);
    } else {
      dao.update(phase);
    }


    return phase.getId();
  }


}